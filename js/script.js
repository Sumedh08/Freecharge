const API_URL = 'http://localhost:9876';
let jwt = localStorage.getItem('jwt');
let currentUser = null;
let currentStock = null;
let currentOrderType = 'BUY';
let portfolioChart = null;

// Routing
const views = ['dashboard', 'market', 'portfolio', 'leaderboard'];

// Init
function init() {
    setupEventListeners();
    document.getElementById('current-date').textContent = new Date().toLocaleDateString('en-IN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

    if (jwt) {
        showApp();
    }
}

function setupEventListeners() {
    // Nav Links
    views.forEach(view => {
        const btn = document.getElementById(`nav-${view}`);
        if (btn) {
            btn.addEventListener('click', () => navigateTo(view));
        }
    });

    // Auth Toggles
    document.getElementById('show-login').onclick = () => toggleAuth('login');
    document.getElementById('show-signup').onclick = () => toggleAuth('signup');

    // Forms
    document.getElementById('login-form').onsubmit = handleLogin;
    document.getElementById('signup-form').onsubmit = handleSignup;
    document.getElementById('logout-btn').onclick = logout;

    // Search
    document.getElementById('stock-search').oninput = handleSearch;

    // Trade Modal
    document.querySelector('.close-modal').onclick = () => document.getElementById('trade-modal').classList.add('hidden');
    document.getElementById('tab-buy').onclick = () => setOrderType('BUY');
    document.getElementById('tab-sell').onclick = () => setOrderType('SELL');
    document.getElementById('trade-quantity').oninput = updateTradeTotal;
    document.getElementById('confirm-trade-btn').onclick = executeTrade;
}

// Navigation
function navigateTo(viewName) {
    views.forEach(v => {
        document.getElementById(`view-${v}`).classList.add('hidden');
        document.getElementById(`nav-${v}`).classList.remove('active');
    });

    document.getElementById(`view-${viewName}`).classList.remove('hidden');
    document.getElementById(`nav-${viewName}`).classList.add('active');

    const titles = {
        'dashboard': 'Dashboard',
        'market': 'Market',
        'portfolio': 'Portfolio',
        'leaderboard': 'Leaderboard'
    };
    document.getElementById('page-title').textContent = titles[viewName];

    // Load Data
    if (viewName === 'market') fetchStocks();
    if (viewName === 'portfolio') fetchPortfolio();
    if (viewName === 'leaderboard') loadLeaderboard('weekly');
    if (viewName === 'dashboard') loadDashboardData();
}

async function showApp() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('dashboard-section').classList.remove('hidden');
    await fetchUserProfile();
    navigateTo('dashboard');
}

// Auth Handlers
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_URL}/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok) {
            jwt = data.jwt;
            localStorage.setItem('jwt', jwt);
            showApp();
        } else alert(data.message || 'Login failed');
    } catch (err) { console.error(err); alert('Connection error'); }
}

async function handleSignup(e) {
    e.preventDefault();
    const body = {
        fullName: document.getElementById('signup-name').value,
        email: document.getElementById('signup-email').value,
        mobile: document.getElementById('signup-mobile').value,
        password: document.getElementById('signup-password').value
    };

    try {
        const res = await fetch(`${API_URL}/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });
        const data = await res.json();
        if (res.ok) {
            jwt = data.jwt;
            localStorage.setItem('jwt', jwt);
            showApp();
        } else alert(data.message || 'Signup failed');
    } catch (err) { console.error(err); }
}

function toggleAuth(mode) {
    if (mode === 'login') {
        document.getElementById('login-form').classList.remove('hidden');
        document.getElementById('signup-form').classList.add('hidden');
        document.getElementById('show-login').classList.add('active');
        document.getElementById('show-signup').classList.remove('active');
    } else {
        document.getElementById('signup-form').classList.remove('hidden');
        document.getElementById('login-form').classList.add('hidden');
        document.getElementById('show-signup').classList.add('active');
        document.getElementById('show-login').classList.remove('active');
    }
}

function logout() {
    localStorage.removeItem('jwt');
    location.reload();
}

// Data Fetching
async function fetchUserProfile() {
    const res = await fetch(`${API_URL}/api/users/profile`, {
        headers: { 'Authorization': `Bearer ${jwt}` }
    });
    if (res.ok) {
        currentUser = await res.json();
        document.getElementById('user-name').textContent = currentUser.fullName;
        document.getElementById('wallet-balance').textContent = formatCurrency(currentUser.balance);
    } else logout();
}

async function fetchStocks() {
    const res = await fetch(`${API_URL}/stocks`);
    const stocks = await res.json();
    renderStocks(stocks, 'stock-list');
}

async function handleSearch(e) {
    const query = e.target.value;
    if (query.length > 2) {
        const res = await fetch(`${API_URL}/stocks/search?keyword=${query}`);
        const stocks = await res.json();
        renderStocks(stocks, 'stock-list');
    } else if (query.length === 0) fetchStocks();
}

function renderStocks(stocks, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = '';
    stocks.forEach(stock => {
        const div = document.createElement('div');
        div.className = 'stock-item';
        div.innerHTML = `
            <div>
                <h4>${stock.symbol}</h4>
                <small style="color: var(--text-secondary)">${stock.name}</small>
            </div>
            <div style="text-align: right">
                <h4>${formatCurrency(stock.currentPrice)}</h4>
                <small class="${stock.priceChangePercent >= 0 ? 'text-up' : 'text-down'}">
                    ${stock.priceChangePercent}%
                </small>
            </div>
        `;
        div.onclick = () => openTradeModal(stock);
        container.appendChild(div);
    });
}

async function fetchPortfolio() {
    const res = await fetch(`${API_URL}/api/assets`, {
        headers: { 'Authorization': `Bearer ${jwt}` }
    });
    const assets = await res.json();
    const container = document.getElementById('portfolio-list');
    container.innerHTML = '';

    if (!Array.isArray(assets) || assets.length === 0) {
        container.innerHTML = '<div style="grid-column: 1/-1; text-align: center; color: var(--text-secondary)">No assets found. Go to Market to trade.</div>';
        return;
    }

    assets.forEach(asset => {
        const div = document.createElement('div');
        div.className = 'portfolio-item';
        const currentVal = asset.quantity * asset.stock.currentPrice;
        const invested = asset.quantity * asset.buyPrice;
        const pnl = currentVal - invested;
        const pnlPercent = (pnl / invested) * 100;

        div.innerHTML = `
            <div>
                <h4>${asset.stock.symbol}</h4>
                <small>${asset.quantity} Qty</small>
            </div>
            <div style="text-align: right">
                <h4>${formatCurrency(currentVal)}</h4>
                <small class="${pnl >= 0 ? 'text-up' : 'text-down'}">
                    ${pnl >= 0 ? '+' : ''}${formatCurrency(pnl)} (${pnlPercent.toFixed(2)}%)
                </small>
            </div>
        `;
        div.onclick = () => openTradeModal(asset.stock);
        container.appendChild(div);
    });
}

// Leaderboard
async function loadLeaderboard(type) {
    try {
        const res = await fetch(`${API_URL}/api/leaderboard/${type}`);
        const data = await res.json();
        const tbody = document.getElementById('leaderboard-body');
        tbody.innerHTML = '';

        data.forEach((entry, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="rank-${index + 1}">#${index + 1}</td>
                <td>
                    <strong>${entry.fullName}</strong>
                </td>
                <td class="${entry.return >= 0 ? 'text-up' : 'text-down'}">
                    ${entry.return.toFixed(2)}%
                </td>
                <td>${formatCurrency(entry.currentValue)}</td>
            `;
            tbody.appendChild(tr);
        });
    } catch (e) {
        console.error("Leaderboard fetch failed", e);
    }
}

// Dashboard
async function loadDashboardData() {
    // Top Movers Mock
    const res = await fetch(`${API_URL}/stocks`);
    const stocks = await res.json();
    const sorted = stocks.sort((a, b) => Math.abs(b.priceChangePercent) - Math.abs(a.priceChangePercent)).slice(0, 5);

    const topMovers = document.getElementById('top-movers-list');
    topMovers.innerHTML = '';
    sorted.forEach(stock => {
        const div = document.createElement('div');
        div.style = "display: flex; justify-content: space-between; padding: 0.5rem 0; border-bottom: 1px solid var(--border)";
        div.innerHTML = `
            <span>${stock.symbol}</span>
            <span class="${stock.priceChangePercent >= 0 ? 'text-up' : 'text-down'}">${stock.priceChangePercent}%</span>
        `;
        topMovers.appendChild(div);
    });

    // Real Portfolio Chart
    try {
        const historyRes = await fetch(`${API_URL}/api/users/portfolio/history`, {
            headers: { 'Authorization': `Bearer ${jwt}` }
        });
        const historyData = await historyRes.json();

        let labels = [];
        let dataPoints = [];

        if (Array.isArray(historyData) && historyData.length > 0) {
            labels = historyData.map(d => new Date(d.timestamp).toLocaleDateString());
            dataPoints = historyData.map(d => d.totalValue);
        } else {
            // New User: Show flat line for "Now"
            const today = new Date().toLocaleDateString();
            labels = [today];
            dataPoints = [currentUser ? currentUser.balance : 10000000];
        }

        const ctx = document.getElementById('portfolioChart').getContext('2d');
        if (portfolioChart) portfolioChart.destroy();

        portfolioChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Portfolio Value',
                    data: dataPoints,
                    borderColor: '#3d7bf5',
                    backgroundColor: 'rgba(61, 123, 245, 0.1)',
                    fill: true,
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: false } },
                scales: {
                    y: {
                        grid: { color: '#2d3748' },
                        ticks: { callback: function (value) { return '₹' + value.toLocaleString(); } }
                    },
                    x: { grid: { display: false } }
                }
            }
        });
    } catch (e) {
        console.error("Chart load failed", e);
    }
}

// Trade Logic
function openTradeModal(stock) {
    currentStock = stock;
    document.getElementById('trade-modal').classList.remove('hidden');
    document.getElementById('modal-stock-name').textContent = stock.name;
    document.getElementById('modal-stock-price').textContent = formatCurrency(stock.currentPrice);
    document.getElementById('trade-quantity').value = '';
    document.getElementById('trade-total').textContent = '₹0.00';
}

function setOrderType(type) {
    currentOrderType = type;
    document.getElementById('tab-buy').classList.toggle('active', type === 'BUY');
    document.getElementById('tab-sell').classList.toggle('active', type === 'SELL');
    document.getElementById('confirm-trade-btn').textContent = `Confirm ${type}`;
    document.getElementById('confirm-trade-btn').style.background = type === 'BUY' ? 'var(--primary)' : 'var(--danger)';
}

function updateTradeTotal(e) {
    const qty = e.target.value;
    const total = qty * currentStock.currentPrice;
    document.getElementById('trade-total').textContent = formatCurrency(total);
}

async function executeTrade() {
    const quantity = document.getElementById('trade-quantity').value;
    if (!quantity || quantity <= 0) return alert('Invalid quantity');

    try {
        const res = await fetch(`${API_URL}/api/orders/pay`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({
                stockId: currentStock.symbol,  // Use symbol instead of id
                quantity: parseInt(quantity),  // Parse as integer
                orderType: currentOrderType
            })
        });

        if (res.ok) {
            alert('Trade Executed Successfully 🚀');
            document.getElementById('trade-modal').classList.add('hidden');
            fetchUserProfile();
            fetchPortfolio();
        } else {
            const err = await res.json();
            alert('Trade Failed: ' + (err.message || 'Unknown error'));
        }
    } catch (e) {
        console.error(e);
        alert('Trade failed');
    }
}

// Helpers
function formatCurrency(value) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(value);
}

// Run
init();
