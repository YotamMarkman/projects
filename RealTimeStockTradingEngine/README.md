# Real-Time Stock Trading Engine

A Java-based order matching engine that simulates real-time stock trading. The system accepts buy and sell orders and automatically matches them when prices align, similar to how real stock exchanges operate.

## Overview

This trading engine implements a basic **order book** system where:
- Traders submit **buy orders** (bids) specifying how much they're willing to pay
- Traders submit **sell orders** (asks) specifying their minimum selling price
- The engine automatically matches orders when: `buy price >= sell price`
- Matched trades execute immediately and are removed from the order book

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Trading Server                       │
│                  (Port 8080 - TCP)                      │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
              ┌────────────────┐
              │   Order Book   │ ← Thread-safe matching engine
              └────────┬───────┘
                       │
        ┌──────────────┴──────────────┐
        ▼                             ▼
   ┌─────────┐                  ┌──────────┐
   │Buy Orders│                  │Sell Orders│
   │(Bids)   │                  │(Asks)    │
   └─────────┘                  └──────────┘
```

### Components

1. **Order** (abstract) - Base class for all orders
   - Properties: `symbol`, `quantity`, `price`, `traderName`
   - Abstract method: `getType()`

2. **BuyOrder** - Represents a bid to purchase shares
   - Type: "BUY"
   - Semantics: "I want to buy X shares at price Y or lower"

3. **SellOrder** - Represents an ask to sell shares
   - Type: "SELL"
   - Semantics: "I want to sell X shares at price Y or higher"

4. **OrderBook** - Core matching engine
   - Maintains separate lists for buy and sell orders
   - Thread-safe using `synchronized` methods
   - Automatically matches compatible orders

5. **TradingServer** - TCP server for remote order submission
   - Listens on port 8080
   - Accepts orders via text protocol: `BUY AAPL 100 150.00`
   - Multi-threaded using `ClientHandler`

6. **Main** - Standalone demo with examples

## Quick Start

### Compile All Classes

```powershell
cd RealTimeStockTradingEngine
javac *.java
```

### Run the Standalone Demo

```powershell
java RealTimeStockTradingEngine.Main
```

This will run 5 example scenarios showing different order matching behaviors.

### Run the TCP Server

```powershell
java RealTimeStockTradingEngine.TradingServer
```

The server will start on port 8080 and accept connections.

### Connect as a Client

Use telnet or netcat to connect:

```powershell
# Windows (enable Telnet Client feature if needed)
telnet localhost 8080

# Linux/macOS
nc localhost 8080
```

Then send orders in this format:
```
BUY AAPL 100 150.00
SELL TSLA 50 700.00
BUY GOOGL 200 2800.50
```

Format: `<TYPE> <SYMBOL> <QUANTITY> <PRICE>`

## Order Matching Logic

The engine uses a simple price-time priority algorithm:

1. **Price Match Condition**: `buyOrder.price >= sellOrder.price`
2. **First Come, First Served**: Orders are matched in the order they arrive
3. **Full Fill Only**: Currently only supports complete order execution (no partial fills)

### Example Trade Scenarios

#### Scenario 1: Successful Match
```
BUY AAPL 100 $150.00  (Alice)
SELL AAPL 100 $149.00 (Bob)
→ TRADE EXECUTED at $149.00
```
Alice is willing to pay $150, Bob will sell for $149 → Match!

#### Scenario 2: No Match (Price Gap)
```
BUY TSLA 50 $200.00  (Charlie)
SELL TSLA 50 $210.00 (Diana)
→ NO TRADE (both orders remain in book)
```
Charlie won't pay more than $200, Diana won't accept less than $210 → Gap too wide.

#### Scenario 3: Multiple Buyers
```
BUY GOOGL 200 $2800.00 (Eve)    ← Added first
BUY GOOGL 150 $2750.00 (Frank)  ← Added second
SELL GOOGL 100 $2750.00 (Grace)
→ TRADE with Eve (first in queue, price matches)
```

## API Usage

### Programmatic Order Submission

```java
OrderBook orderBook = new OrderBook();

// Create a buy order
BuyOrder buyOrder = new BuyOrder("AAPL", 100, 150.00, "Alice");
orderBook.addOrder(buyOrder);

// Create a sell order
SellOrder sellOrder = new SellOrder("AAPL", 100, 149.00, "Bob");
orderBook.addOrder(sellOrder);

// Matching happens automatically
// If buy.price >= sell.price, trade executes
```

### TCP Protocol

Connect to port 8080 and send text commands:

```
BUY <SYMBOL> <QUANTITY> <PRICE>
SELL <SYMBOL> <QUANTITY> <PRICE>
```

Example session:
```
$ nc localhost 8080
BUY AAPL 100 150.50
Order Received: BUY AAPL
SELL AAPL 100 150.00
Order Received: SELL AAPL
```

Server console will show:
```
TRADE EXECUTED: 100 shares.
```

## Thread Safety

- **OrderBook** uses `synchronized` methods to prevent race conditions
- **CopyOnWriteArrayList** for buy/sell order lists (thread-safe iteration)
- **ClientHandler** runs each connection in a separate thread

## Known Limitations & Bugs

The code includes comments noting several issues:

1. **BUG 4**: Modifying lists during iteration (though mitigated by `return` statement)
2. **No Partial Fills**: Orders must match completely (all-or-nothing)
3. **No Order Types**: Only limit orders supported (no market orders, stop-loss, etc.)
4. **No Order Cancellation**: Once submitted, orders cannot be cancelled
5. **No Persistence**: Order book resets on server restart
6. **Simple Matching**: Uses nested loops (O(n²) - not scalable for high-frequency trading)

## Testing

### Manual Testing with Main
```powershell
java RealTimeStockTradingEngine.Main
```

Watch console output to see 5 example scenarios execute.

### Network Testing
```powershell
# Terminal 1: Start server
java RealTimeStockTradingEngine.TradingServer

# Terminal 2: Connect and send orders
nc localhost 8080
BUY AAPL 100 150.00
SELL AAPL 100 149.00
```

### Check Server Status
```powershell
# Windows
netstat -ano | Select-String ":8080"

# Linux/Mac
netstat -ln | grep 8080
```

## Production Considerations

For a real trading system, you would need:

- ✅ **Partial order fills** (match 50 of 100 shares, leave 50 pending)
- ✅ **Order types**: Market, Limit, Stop-Loss, Stop-Limit, FOK, IOC
- ✅ **Order book depth** display (show all pending orders)
- ✅ **Order cancellation** and modification
- ✅ **Price-time priority** with proper queue management
- ✅ **Persistence** (database/message queue)
- ✅ **Matching engine optimization** (sorted order books, efficient data structures)
- ✅ **Authentication** and authorization
- ✅ **Audit logging** for regulatory compliance
- ✅ **Market data feeds** (real-time price updates)
- ✅ **Risk management** (position limits, circuit breakers)

## Future Enhancements

1. **Order Book Visualization**: Display pending orders sorted by price
2. **Partial Fills**: Split orders if quantities don't match
3. **Market Orders**: Match immediately at best available price
4. **Order Cancellation**: Allow traders to cancel pending orders
5. **Historical Trades**: Store executed trade history
6. **Multiple Symbols**: Track order books for different stocks
7. **REST API**: HTTP interface instead of raw TCP
8. **Web Dashboard**: Real-time order book visualization

## Example Output

```
=== Real-Time Stock Trading Engine Demo ===

--- Example 1: Simple Match ---
Alice wants to BUY 100 shares of AAPL at $150.00
Bob wants to SELL 100 shares of AAPL at $149.00
→ Trade should execute (Buy $150 >= Sell $149)
TRADE EXECUTED: 100 shares.

--- Example 2: No Match (Price Gap) ---
Charlie wants to BUY 50 shares of TSLA at $200.00
Diana wants to SELL 50 shares of TSLA at $210.00
→ No trade (Buy $200 < Sell $210)

--- Example 3: Multiple Orders ---
Eve wants to BUY 200 shares of GOOGL at $2800.00
Frank wants to BUY 150 shares of GOOGL at $2750.00
Grace wants to SELL 100 shares of GOOGL at $2750.00
→ Trade with Eve (first buyer at $2800 >= $2750)
TRADE EXECUTED: 200 shares.

=== Demo Complete ===
```

## Dependencies

- Java 8 or higher
- No external dependencies (uses only Java standard library)

## License

For educational purposes.
