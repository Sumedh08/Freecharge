# How to Run The Indian Paper Trading App

## Quick Start

### Option 1: Using Maven Wrapper (Recommended)
```bash
# Navigate to project directory
cd c:\Users\Sumed\Downloads\Trading_Platform_Backend

# Run the application
.\mvnw.cmd spring-boot:run
```

### Option 2: Using Java directly (if Maven has issues)
```bash
# First compile
.\mvnw.cmd clean package -DskipTests

# Then run the JAR
java -jar target\Trading-0.0.1-SNAPSHOT.jar
```

## Access the Application
Once the server starts (you'll see "Started TradingApplication"), open your browser:
- **URL**: http://localhost:9876
- **Port**: 9876

## First Time Setup
1. Click "Signup"
2. Enter your details
3. You'll automatically receive ₹1 Crore (₹10,000,000)
4. Start trading Indian stocks!

## Features
- ✅ Real-time stock prices (mocked data)
- ✅ Buy/Sell stocks
- ✅ Portfolio tracking
- ✅ Search stocks
- ✅ Paper trading with virtual money

## Troubleshooting
If you see "JAVA_HOME not defined":
1. Install JDK 17+
2. Set JAVA_HOME environment variable
3. Restart terminal and try again
