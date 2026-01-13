# Paper Trading Platform - Microservices Architecture

A production-grade Indian Paper Trading Platform built with **Spring Boot** and **Microservices Architecture**.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND                                 │
│                    (HTML/CSS/JavaScript)                         │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                    API GATEWAY (:8080)                           │
│              (Spring Cloud Gateway)                              │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│               TRADING SERVICE (:9876)                            │
│     (Users, Orders, Stocks, Leaderboard)                        │
└──────────────────────────┬──────────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
┌───────▼───────┐  ┌───────▼───────┐  ┌───────▼───────┐
│    EUREKA     │  │     KAFKA     │  │     REDIS     │
│   (:8761)     │  │   (:29092)    │  │   (:6379)     │
└───────────────┘  └───────────────┘  └───────────────┘
                           │
                   ┌───────▼───────┐
                   │   POSTGRES    │
                   │   (:5432)     │
                   └───────────────┘
```

## 🚀 Tech Stack

| Component | Technology |
|-----------|------------|
| **Backend** | Java 17, Spring Boot 3.2 |
| **API Gateway** | Spring Cloud Gateway |
| **Service Discovery** | Netflix Eureka |
| **Database** | PostgreSQL |
| **Cache** | Redis |
| **Message Queue** | Apache Kafka |
| **Circuit Breaker** | Resilience4j |
| **Monitoring** | Prometheus + Grafana |
| **Containerization** | Docker, Docker Compose |

## 📦 Quick Start

### Prerequisites
- Docker Desktop installed
- Java 17 (for local development)

### Run with Docker (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f trading-app
```

### Access Points
| Service | URL |
|---------|-----|
| **Trading App** | http://localhost:9876 |
| **API Gateway** | http://localhost:8080 |
| **Eureka Dashboard** | http://localhost:8761 |
| **Prometheus** | http://localhost:9090 |
| **Grafana** | http://localhost:3000 (admin/admin123) |

### Run Locally (Development)

```bash
# Start only infrastructure
docker-compose up -d postgres redis kafka zookeeper

# Run the app
./mvnw spring-boot:run
```

## 📁 Project Structure

```
Paper-Trading-Platform/
├── docker-compose.yml       # All services orchestration
├── Dockerfile               # Main app container
├── eureka-server/           # Service discovery
├── api-gateway/             # API gateway with rate limiting
├── monitoring/              # Prometheus config
└── src/                     # Main trading application
    ├── config/              # Kafka, Redis, WebSocket configs
    ├── controller/          # REST APIs
    ├── service/             # Business logic
    ├── model/               # JPA entities
    └── event/               # Kafka event DTOs
```

## 🔥 Features

- ✅ **Paper Trading** - Buy/Sell 50+ Indian stocks
- ✅ **₹1 Crore** starting balance
- ✅ **Leaderboard** - Weekly, Monthly, All-time rankings
- ✅ **Portfolio Tracking** - Real-time P&L
- ✅ **Service Discovery** - Eureka
- ✅ **Rate Limiting** - API Gateway
- ✅ **Event Streaming** - Kafka
- ✅ **Caching** - Redis
- ✅ **Monitoring** - Prometheus + Grafana

## 📊 Monitoring

Access Grafana at http://localhost:3000 with:
- Username: `admin`
- Password: `admin123`

## 🛠️ Development

```bash
# Build all services
./mvnw clean package

# Run tests
./mvnw test
```

## 📝 License

MIT License
