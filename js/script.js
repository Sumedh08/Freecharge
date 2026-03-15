const API_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:9876'
    : 'https://freecharge-1flm.onrender.com';
let jwt = localStorage.getItem('jwt');
let currentUser = null;
let currentStock = null;
let currentOrderType = 'BUY';
let portfolioChart = null;

// Investor Profile (Phase B - built by AI onboarding)
let userProfile = {
    goals: [],
    monthlyInvestment: 0,
    experience: 'beginner',
    timeCommitment: 'low'
};

// Routing
const views = ['dashboard', 'stocks', 'portfolio', 'mutual-funds'];

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
    document.getElementById('mf-search').oninput = handleMfSearch;

    // Trade Modal
    document.querySelector('.close-modal').onclick = () => document.getElementById('trade-modal').classList.add('hidden');
    document.getElementById('tab-buy').onclick = () => setOrderType('BUY');
    document.getElementById('tab-sell').onclick = () => setOrderType('SELL');
    document.getElementById('trade-quantity').oninput = updateTradeTotal;
    document.getElementById('confirm-trade-btn').onclick = executeTrade;

    // AI Mentor Chat
    document.getElementById('axis-fab').onclick = () => {
        document.getElementById('axis-chat-widget').classList.remove('hidden');
        document.getElementById('axis-fab').classList.add('hidden');
    };
    
    document.getElementById('close-chat-btn').onclick = () => {
        document.getElementById('axis-chat-widget').classList.add('hidden');
        document.getElementById('axis-fab').classList.remove('hidden');
    };

    document.getElementById('send-chat-btn').onclick = sendChatMessage;
    document.getElementById('chat-input').onkeypress = (e) => {
        if (e.key === 'Enter') sendChatMessage();
    };

    // Spotlight Overlay dismiss with click-through support
    document.getElementById('spotlight-overlay').onclick = (e) => {
        const highlightedElement = document.querySelector('.agent-highlight');
        if (highlightedElement) {
            const rect = highlightedElement.getBoundingClientRect();
            // If the user clicked inside the physical bounds of the glowing element, trigger its click natively!
            if (e.clientX >= rect.left && e.clientX <= rect.right &&
                e.clientY >= rect.top && e.clientY <= rect.bottom) {
                highlightedElement.click();
            }
        }
        clearAgentHighlights();
    };
}

// Navigation
function navigateTo(viewName) {
    views.forEach(v => {
        const viewEl = document.getElementById(`view-${v}`);
        const navEl = document.getElementById(`nav-${v}`);
        if (viewEl) viewEl.classList.add('hidden');
        if (navEl) navEl.classList.remove('active');
    });

    const targetView = document.getElementById(`view-${viewName}`);
    const targetNav = document.getElementById(`nav-${viewName}`);
    if (targetView) targetView.classList.remove('hidden');
    if (targetNav) targetNav.classList.add('active');

    const titles = {
        'dashboard': 'Dashboard',
        'stocks': 'Stocks',
        'portfolio': 'Portfolio',
        'mutual-funds': 'Mutual Funds'
    };
    document.getElementById('page-title').textContent = titles[viewName] || 'FinMate';

    // Load Data
    if (viewName === 'stocks') fetchStocks();
    if (viewName === 'portfolio') fetchPortfolio();
    if (viewName === 'dashboard') loadDashboardData();
    if (viewName === 'mutual-funds') fetchMutualFunds();
    
    // Remove tutorial overlay if user clicks naturally to navigate
    if (typeof clearAgentHighlights === 'function') {
        clearAgentHighlights();
    }
}

async function showApp() {
    document.getElementById('auth-section').classList.add('hidden');
    document.getElementById('dashboard-section').classList.remove('hidden');
    await fetchUserProfile();
    
    // Ensure AI Mentor FAB is visible if the widget isn't already open
    if (document.getElementById('axis-chat-widget').classList.contains('hidden')) {
        document.getElementById('axis-fab').classList.remove('hidden');
    }

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
            
            // Open the AI Mentor on login as requested
            document.getElementById('axis-chat-widget').classList.remove('hidden');
            document.getElementById('axis-chat-widget').classList.add('full-screen');
            document.getElementById('axis-chat-widget').classList.add('onboarding');
            
            setTimeout(() => {
                appendMessage("ai", `Welcome back! I am Axis, your AI Wealth Mentor. Are we continuing your investment journey today, or would you prefer me to guide you straight to the Stocks or Mutual Funds dashboard?`);
            }, 1000);

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
            
            // Full screen onboarding for AI
            document.getElementById('axis-chat-widget').classList.remove('hidden');
            document.getElementById('axis-chat-widget').classList.add('full-screen');
            document.getElementById('axis-chat-widget').classList.add('onboarding');
            
            appendMessage("ai", `Hi ${body.fullName}! I'm Axis. I'm here to help you understand investing and find options that match your goals. Should I help you explore the app?`);
            
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
        const userNameEl = document.getElementById('user-name');
        const walletBalanceEl = document.getElementById('wallet-balance');
        if (userNameEl) userNameEl.textContent = currentUser.fullName;
        if (walletBalanceEl) walletBalanceEl.textContent = formatCurrency(currentUser.balance);
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
    if (!container) return;
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

// Mutual Funds Fetching and Rendering
let allMutualFunds = [];
let currentRenderedFunds = [];
let currentCagrPeriod = '3Y';

function toggleCagrPeriod(e) {
    if(e) e.stopPropagation();
    if (currentCagrPeriod === '1Y') currentCagrPeriod = '3Y';
    else if (currentCagrPeriod === '3Y') currentCagrPeriod = '5Y';
    else currentCagrPeriod = '1Y';
    
    renderMutualFunds(currentRenderedFunds, 'mf-list');
}

async function fetchMutualFunds() {
    try {
        const res = await fetch(`${API_URL}/mutual-funds`);
        const funds = await res.json();
        allMutualFunds = funds;
        renderMutualFunds(funds, 'mf-list');
    } catch(e) { console.error(e); }
}

function filterMutualFunds(category, btnElement) {
    // Update active tab styling
    const tabs = document.getElementById('mf-filter-tabs').querySelectorAll('button');
    tabs.forEach(tab => tab.classList.remove('active'));
    if (btnElement) btnElement.classList.add('active');

    if (category === 'All') {
        renderMutualFunds(allMutualFunds, 'mf-list');
    } else {
        const filtered = allMutualFunds.filter(fund => fund.category === category);
        renderMutualFunds(filtered, 'mf-list');
    }
}

async function handleMfSearch(e) {
    const query = e.target.value;
    if (query.length > 2) {
        try {
            const res = await fetch(`${API_URL}/mutual-funds/search?keyword=${query}`);
            const funds = await res.json();
            renderMutualFunds(funds, 'mf-list');
        } catch(e) { console.error(e); }
    } else if (query.length === 0) fetchMutualFunds();
}

function renderMutualFunds(funds, containerId) {
    currentRenderedFunds = funds;
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = '';
    funds.forEach(fund => {
        const div = document.createElement('div');
        div.className = 'stock-item';
        
        // Dynamic CAGR logic
        let cagrValue = fund.cagr3Y;
        if(currentCagrPeriod === '1Y') cagrValue = fund.cagr1Y;
        if(currentCagrPeriod === '5Y') cagrValue = fund.cagr5Y;

        // Risk badge color
        let riskBadge = '';
        if (fund.riskLevel) {
            const riskColors = { 'Low': '#00d09c', 'Moderate': '#f5a623', 'High': '#ff4757' };
            const riskEmoji = { 'Low': '🟢', 'Moderate': '🟡', 'High': '🔴' };
            riskBadge = `<span style="display:inline-block; padding:2px 8px; border-radius:10px; font-size:10px; font-weight:600; color:white; background:${riskColors[fund.riskLevel] || '#888'}; margin-left:6px;">${riskEmoji[fund.riskLevel] || ''} ${fund.riskLevel} Risk</span>`;
        }

        div.innerHTML = `
            <div>
                <h4>${fund.schemeName}</h4>
                <small style="color: var(--text-secondary)">${fund.category} ${riskBadge}</small>
            </div>
            <div style="text-align: right; cursor: pointer;" onclick="toggleCagrPeriod(event)" title="Click to toggle 1Y/3Y/5Y Return">
                <h4>${formatCurrency(fund.currentNav)} </h4>
                <small class="${cagrValue >= 0 ? 'text-up' : 'text-down'}">
                    ${currentCagrPeriod} Return: <span style="font-weight: 500">${cagrValue.toFixed(2)}%</span>
                </small>
            </div>
        `;
        div.onclick = () => openMutualFundModal(fund);
        container.appendChild(div);
    });
}

function openMutualFundModal(fund) {
    const existing = document.getElementById('fund-detail-modal');
    if (existing) existing.remove();

    const modal = document.createElement('div');
    modal.id = 'fund-detail-modal';
    modal.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.7);display:flex;justify-content:center;align-items:center;z-index:500;backdrop-filter:blur(5px);';
    modal.innerHTML = `
        <div style="background:var(--bg-card);border-radius:16px;padding:2.5rem;width:450px;max-width:90vw;box-shadow:0 30px 80px rgba(0,0,0,0.6);border:1px solid var(--border);">
            <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:1.5rem;">
                <div>
                    <h2 style="margin:0; font-size: 1.5rem; line-height: 1.2;">${fund.schemeName}</h2>
                    <small style="color:var(--text-secondary)">${fund.category}</small>
                </div>
                <button onclick="document.getElementById('fund-detail-modal').remove()" style="background:none;border:none;color:var(--text-secondary);font-size:28px;cursor:pointer;padding:0;line-height:1;">&times;</button>
            </div>
            
            <div style="background:var(--bg-main); padding: 1.2rem; border-radius: 12px; margin-bottom: 2rem;">
                <div style="display:flex; justify-content:space-between; margin-bottom: 0.8rem;">
                    <span style="color:var(--text-secondary);">Current NAV</span>
                    <span style="color:white; font-weight:600;">${formatCurrency(fund.currentNav)}</span>
                </div>
                <div style="display:flex; justify-content:space-between; margin-bottom: 0.8rem;">
                    <span style="color:var(--text-secondary);">3Y CAGR</span>
                    <span style="color:var(--success); font-weight:600;">${fund.cagr3Y.toFixed(2)}%</span>
                </div>
                <div style="display:flex; justify-content:space-between;">
                    <span style="color:var(--text-secondary);">Risk Level</span>
                    <span style="color:white; font-weight:600;">${fund.riskLevel}</span>
                </div>
            </div>

            <div style="display:grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1.2rem;">
                <button onclick="alert('Mutual Fund Buying is coming in V2!')" class="primary-btn" style="background:var(--success); border:none; padding:14px;">Buy</button>
                <button onclick="alert('Mutual Fund Selling is coming in V2!')" class="primary-btn" style="background:var(--error); border:none; padding:14px;">Sell</button>
            </div>
            <button id="modal-sip-calc-btn" class="primary-btn" style="width:100%; background:var(--primary); border:none; padding:14px; font-size: 0.95rem;">
                📈 SIP Calculator (uses ${fund.cagr3Y.toFixed(1)}% rate)
            </button>
        </div>
    `;
    document.body.appendChild(modal);

    document.getElementById('modal-sip-calc-btn').onclick = () => {
        document.getElementById('fund-detail-modal').remove();
        showSipCalculator(userProfile.monthlyInvestment || 5000, fund.cagr3Y, 0, 10);
    };

    modal.onclick = (e) => { if (e.target === modal) modal.remove(); };
}

async function fetchPortfolio() {
    const res = await fetch(`${API_URL}/api/assets`, {
        headers: { 'Authorization': `Bearer ${jwt}` }
    });
    const assets = await res.json();
    const container = document.getElementById('portfolio-list');
    if (!container) return;
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
        if (!tbody) return;
        tbody.innerHTML = '';

        data.forEach((entry, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="rank-${index + 1}">#${index + 1}</td>
                <td><strong>${entry.fullName}</strong></td>
                <td class="${entry.return >= 0 ? 'text-up' : 'text-down'}">${entry.return.toFixed(2)}%</td>
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
    const res = await fetch(`${API_URL}/stocks`);
    const stocks = await res.json();
    const sorted = stocks.sort((a, b) => Math.abs(b.priceChangePercent) - Math.abs(a.priceChangePercent)).slice(0, 5);

    const topMovers = document.getElementById('top-movers-list');
    if (topMovers) {
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
    }

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
            const today = new Date().toLocaleDateString();
            labels = [today];
            dataPoints = [currentUser ? currentUser.balance : 50000];
        }

        const ctx = document.getElementById('portfolioChart');
        if (ctx) {
            if (portfolioChart) portfolioChart.destroy();
            portfolioChart = new Chart(ctx.getContext('2d'), {
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
        }
    } catch (e) { console.error("Chart load failed", e); }
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
                stockId: currentStock.symbol,
                quantity: parseInt(quantity),
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

// AI Chat Logic
let chatHistory = [];

function appendMessage(sender, text) {
    const chatMessages = document.getElementById('chat-messages');
    if (!chatMessages) return;
    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${sender}`;
    msgDiv.textContent = text;
    chatMessages.appendChild(msgDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
    
    chatHistory.push({ role: sender === 'ai' ? "assistant" : "user", content: text });
}

async function sendChatMessage() {
    const inputEl = document.getElementById('chat-input');
    const text = inputEl.value.trim();
    if (!text) return;

    clearAgentHighlights();
    appendMessage('user', text);
    inputEl.value = '';
    
    const chatMessages = document.getElementById('chat-messages');
    const typing = document.createElement('div');
    typing.className = `message ai`;
    typing.id = 'typing-indicator';
    typing.textContent = '...';
    chatMessages.appendChild(typing);
    chatMessages.scrollTop = chatMessages.scrollHeight;

    document.getElementById('axis-chat-widget').classList.remove('full-screen');
    document.getElementById('axis-chat-widget').classList.remove('onboarding');
    
    try {
        const activeNavEl = document.querySelector('.sidebar .nav-links button.active');
        const currentView = activeNavEl ? activeNavEl.innerText.replace(/[^a-zA-Z\s]/g, '').trim() : 'Unknown';
        
        const profileContext = (userProfile && userProfile.goals && userProfile.goals.length > 0) 
            ? `Investor Profile: Goals=${userProfile.goals.join(',')}, Monthly=₹${userProfile.monthlyInvestment}, Experience=${userProfile.experience}, TimeCommitment=${userProfile.timeCommitment}`
            : 'No investor profile yet.';
        
        const res = await fetch(`${API_URL}/api/chat`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwt}`
            },
            body: JSON.stringify({
                messages: chatHistory,
                context: (currentUser ? `Name: ${currentUser.fullName}, Balance: ${currentUser.balance}` : '') + `, CURRENTLY VIEWING TAB: ${currentView}. ${profileContext}`
            })
        });

        const typingEl = document.getElementById('typing-indicator');
        if(typingEl) typingEl.remove();

        if (res.ok) {
            const data = await res.json();
            let reply = data.reply;
            
            // UI Highlights
            if (reply.includes('[POINT_MUTUAL_FUNDS]')) performAgenticHighlight('nav-mutual-funds');
            if (reply.includes('[POINT_DASHBOARD]')) performAgenticHighlight('nav-dashboard');
            if (reply.includes('[POINT_STOCKS]')) performAgenticHighlight('nav-stocks');
            if (reply.includes('[POINT_PORTFOLIO]')) performAgenticHighlight('nav-portfolio');
            
            // SIP Calculator Trigger
            const sipMatch = reply.match(/\[SHOW_SIP_CALC:(\{.*?\})\]/);
            if (sipMatch) {
                try {
                    const params = JSON.parse(sipMatch[1]);
                    showSipCalculator(params.monthly, params.rate, params.lumpSum, params.years);
                } catch(e) { showSipCalculator(); }
            } else if (reply.includes('[SHOW_SIP_CALC]')) {
                showSipCalculator();
            }
            
            // Profile Update
            const profileMatch = reply.match(/\[PROFILE:\s*\{([^\]]+)\}\s*\]/);
            if (profileMatch) {
                try {
                    const profileData = JSON.parse('{' + profileMatch[1] + '}');
                    if (profileData.goals) userProfile.goals = profileData.goals;
                    if (profileData.monthly) userProfile.monthlyInvestment = profileData.monthly;
                    if (profileData.experience) userProfile.experience = profileData.experience;
                    if (profileData.time) userProfile.timeCommitment = profileData.time;
                } catch(e) {}
            }
            
            if (reply.includes('[SHOW_RECOMMENDED]')) {
                navigateTo('mutual-funds');
                setTimeout(() => showRecommendedFunds(), 500);
            }

            // Cleanup tags for display
            reply = reply.replace(/\[PROFILE:.*?\]/gs, '').trim();
            reply = reply.replace(/\[POINT_.*?\]/g, '').trim();
            reply = reply.replace(/\[SHOW_.*?\]/g, '').trim();
            
            if (reply) appendMessage('ai', reply);
        } else {
            appendMessage('ai', "I'm having trouble connecting to my servers right now.");
        }
    } catch (e) {
        console.error(e);
        const typingEl = document.getElementById('typing-indicator');
        if(typingEl) typingEl.remove();
        appendMessage('ai', "I'm having trouble connecting to my servers right now.");
    }
}

// Agentic Spotlights
function performAgenticHighlight(targetId) {
    clearAgentHighlights();
    const el = document.getElementById(targetId);
    if (!el) return;
    
    document.getElementById('spotlight-overlay').classList.remove('hidden');
    el.classList.add('agent-highlight');
    
    const widget = document.getElementById('axis-chat-widget');
    widget.classList.add('floating-pointer-mode');
    
    const rect = el.getBoundingClientRect();
    setTimeout(() => {
        widget.style.bottom = 'auto';
        widget.style.right = 'auto';
        let newLeft = rect.right + 20;
        if (newLeft + 300 > window.innerWidth) {
            newLeft = rect.left;
            widget.style.top = (rect.bottom + 20) + 'px';
        } else {
            widget.style.top = Math.max(20, rect.top - 20) + 'px';
        }
        widget.style.left = newLeft + 'px';
    }, 50);
}

function clearAgentHighlights() {
    const spotlightOverlay = document.getElementById('spotlight-overlay');
    if (spotlightOverlay) spotlightOverlay.classList.add('hidden');
    document.querySelectorAll('.agent-highlight').forEach(el => el.classList.remove('agent-highlight'));
    
    const widget = document.getElementById('axis-chat-widget');
    if (widget) {
        widget.classList.remove('floating-pointer-mode');
        widget.style.left = '';
        widget.style.top = '';
        widget.style.bottom = '20px';
        widget.style.right = '20px';
        widget.style.transform = '';
    }
}

// Recommended Funds
function showRecommendedFunds() {
    let recommended = [];
    const goals = (userProfile && userProfile.goals) ? userProfile.goals.map(g => g.toLowerCase()) : [];
    
    if (goals.some(g => g.includes('emergency'))) {
        recommended = recommended.concat(allMutualFunds.filter(f => f.riskLevel === 'Low'));
    }
    if (goals.some(g => g.includes('wealth') || g.includes('growth'))) {
        recommended = recommended.concat(allMutualFunds.filter(f => f.category === 'Mid Cap' || f.category === 'Small Cap'));
    }
    if (goals.some(g => g.includes('retirement'))) {
        recommended = recommended.concat(allMutualFunds.filter(f => f.category === 'Large Cap' || f.category === 'Gold'));
    }
    
    const seen = new Set();
    recommended = (recommended.length > 0 ? recommended : allMutualFunds).filter(f => {
        if (seen.has(f.schemeCode)) return false;
        seen.add(f.schemeCode);
        return true;
    });
    
    renderMutualFunds(recommended, 'mf-list');
    const tabs = document.getElementById('mf-filter-tabs').querySelectorAll('button');
    tabs.forEach(tab => tab.classList.remove('active'));
    const recBtn = document.getElementById('mf-recommended-btn');
    if (recBtn) recBtn.classList.add('active');
}

// SIP Calculator
function showSipCalculator(monthly, rate, lumpSum, years) {
    const existing = document.getElementById('sip-calc-modal');
    if (existing) existing.remove();
    
    const defMonthly = monthly !== undefined ? monthly : (userProfile.monthlyInvestment || 5000);
    const defRate = rate !== undefined ? rate : 12;
    const defLumpSum = lumpSum !== undefined ? lumpSum : 0;
    const defYears = years !== undefined ? years : 10;

    const modal = document.createElement('div');
    modal.id = 'sip-calc-modal';
    modal.style.cssText = 'position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.8);display:flex;justify-content:center;align-items:center;z-index:1000;backdrop-filter:blur(8px);';
    modal.innerHTML = `
        <div style="background:var(--bg-card); border-radius:16px; width:800px; max-width:95vw; display:flex; flex-direction:column; box-shadow:0 30px 100px rgba(0,0,0,0.7); border:1px solid var(--border); overflow:hidden;">
            <div style="padding:1.5rem 2rem; border-bottom:1px solid var(--border); display:flex; justify-content:space-between; align-items:center;">
                <h2 style="margin:0; font-size:1.4rem;">💡 Wealth Growth Calculator (SIP / Lump Sum)</h2>
                <button onclick="document.getElementById('sip-calc-modal').remove()" style="background:none; border:none; color:var(--text-secondary); font-size:1.8rem; cursor:pointer;">&times;</button>
            </div>
            <div style="display:flex; flex-direction:row; height:500px;">
                <!-- Inputs Section -->
                <div style="flex:1; padding:2rem; border-right:1px solid var(--border); display:flex; flex-direction:column; gap:1.2rem;">
                    <div>
                        <label style="display:block; margin-bottom:0.5rem; color:var(--text-secondary); font-size:0.9rem;">Monthly Investment commitment (₹)</label>
                        <input type="number" id="sip-monthly" value="${defMonthly}" style="width:100%; background:var(--bg-main); border:1px solid var(--border); padding:12px; border-radius:8px; color:white; font-size:1rem;">
                    </div>
                    <div>
                        <label style="display:block; margin-bottom:0.5rem; color:var(--text-secondary); font-size:0.9rem;">Amount Lumpsum ready to invest (₹)</label>
                        <input type="number" id="sip-lumpsum" value="${defLumpSum}" style="width:100%; background:var(--bg-main); border:1px solid var(--border); padding:12px; border-radius:8px; color:white; font-size:1rem;">
                    </div>
                    <div>
                        <label style="display:block; margin-bottom:0.5rem; color:var(--text-secondary); font-size:0.9rem;">Return Rate (CAGR %)</label>
                        <input type="number" step="0.1" id="sip-rate" value="${defRate}" style="width:100%; background:var(--bg-main); border:1px solid var(--border); padding:12px; border-radius:8px; color:white; font-size:1rem;">
                    </div>
                    <div>
                        <label style="display:block; margin-bottom:0.5rem; color:var(--text-secondary); font-size:0.9rem;">Year to Invest (Time Horizon)</label>
                        <input type="number" id="sip-years" value="${defYears}" style="width:100%; background:var(--bg-main); border:1px solid var(--border); padding:12px; border-radius:8px; color:white; font-size:1rem;">
                    </div>
                </div>
                <!-- Chart/Summary Section -->
                <div style="flex:1.2; padding:2rem; background:var(--bg-main); display:flex; flex-direction:column;">
                    <div style="flex:1; margin-bottom:1.5rem;">
                        <canvas id="sipTrendChart"></canvas>
                    </div>
                    <div id="sip-summary" style="background:var(--bg-card); padding:1rem; border-radius:12px; display:grid; grid-template-columns:1fr 1fr; gap:0.8rem;">
                        <!-- Results injected here -->
                    </div>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
    
    // Auto Update on input
    ['sip-monthly','sip-lumpsum','sip-rate','sip-years'].forEach(id => {
        document.getElementById(id).oninput = updateSipChart;
    });
    
    updateSipChart();
}

let sipChartInstance = null;
function updateSipChart() {
    const P = parseFloat(document.getElementById('sip-monthly').value) || 0;
    const L = parseFloat(document.getElementById('sip-lumpsum').value) || 0;
    const rate = parseFloat(document.getElementById('sip-rate').value) || 0;
    const years = parseFloat(document.getElementById('sip-years').value) || 1;
    
    const monthlyRate = rate / 100 / 12;
    const totalMonths = years * 12;
    
    let labels = [];
    let sipValues = [];
    let lumpSumValues = [];
    
    for (let i = 0; i <= years; i++) {
        labels.push(`Yr ${i}`);
        const months = i * 12;
        // SIP Component
        let sipFV = 0;
        if (monthlyRate > 0) {
            sipFV = P * ((Math.pow(1 + monthlyRate, months) - 1) / monthlyRate) * (1 + monthlyRate);
        } else {
            sipFV = P * months;
        }
        // Lump Sum Component
        const lumpFV = L * Math.pow(1 + (rate / 100), i);
        
        sipValues.push(sipFV + lumpFV);
    }
    
    const finalVal = sipValues[sipValues.length - 1];
    const totalInvested = (P * totalMonths) + L;
    const profit = finalVal - totalInvested;
    
    document.getElementById('sip-summary').innerHTML = `
        <div><small style="color:var(--text-secondary)">Invested</small><div style="font-weight:600">${formatCurrency(totalInvested)}</div></div>
        <div><small style="color:var(--text-secondary)">Est. Wealth</small><div style="font-weight:600; color:var(--success)">${formatCurrency(finalVal)}</div></div>
        <div style="grid-column: 1/-1; border-top:1px solid var(--border); padding-top:0.5rem; margin-top:0.5rem;"><small style="color:var(--text-secondary)">Net Profit</small><div style="font-weight:600; font-size:1.1rem; color:var(--primary)">${formatCurrency(profit)}</div></div>
    `;
    
    const ctx = document.getElementById('sipTrendChart').getContext('2d');
    if (sipChartInstance) sipChartInstance.destroy();
    sipChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: 'Projected Wealth',
                data: sipValues,
                borderColor: '#00d09c',
                backgroundColor: 'rgba(0, 208, 156, 0.1)',
                fill: true,
                tension: 0.3,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                y: { display: false },
                x: { grid: { display: false }, ticks: { color: '#888' } }
            }
        }
    });
}

window.onload = init;
