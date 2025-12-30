# Distributed Key-Value Store

A lightweight, multi-threaded, in-memory key-value store built from scratch in Java. This project demonstrates core backend engineering concepts including socket networking, concurrent data structures, and thread pool management without relying on external frameworks like Spring or Netty.

## 🚀 Features

- **In-Memory Storage**: Fast read/write operations using a thread-safe implementation.
- **Multi-Client Support**: Handles multiple concurrent connections using a custom Thread Pool (ExecutorService).
- **Custom Protocol**: Text-based communication protocol similar to Redis.
- **Thread Safety**: Utilizes ConcurrentHashMap and atomic operations to prevent race conditions during simultaneous read/write bursts.
- **Zero Dependencies**: Built entirely with the standard Java SDK (java.net, java.io, java.util.concurrent).

## 🛠️ Architecture

The system is composed of four main components:

- **Main.java**: Bootstraps the application and initializes the shared memory.
- **Server.java**: The "Receptionist." Listens on port 6379, accepts incoming TCP connections, and delegates them to the thread pool.
- **ClientHandler.java**: The "Worker." Runs on a separate thread for each client, parsing raw text commands and sending responses.
- **DataStore.java**: The "Vault." Encapsulates the ConcurrentHashMap and exposes atomic get, set, and delete operations.

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher.🏃‍♂️ How to Run

1. Compile the Code
Navigate to the source directory in your terminal and compile the Java files:
```bash
javac Main.java server/*.java storage/*.java
```

2. Start the Server
Run the main application. The server will start listening on port 6379.
```bash
java Main
```
**Output:**
```plaintext
Initializing J-Redis...
Server started on port 6379
```

3. Connect (Client)
You can connect using telnet (Windows/Mac) or netcat (Linux/Mac). Open a new terminal window:

**Using Netcat (Mac/Linux):**
```bash
nc localhost 6379
```

**Using Telnet (Windows):**
```bash
telnet localhost 6379
```

## 🎮 Usage Commands

Once connected, you can issue the following commands:

| Command | Usage | Description | Example |
|---------|-------|-------------|---------|
| SET | `SET <key> <value>` | Saves a value to the store. | `SET name John` |
| GET | `GET <key>` | Retrieves a value. Returns NULL if missing. | `GET name` |
| DEL | `DEL <key>` | Removes a key. Returns DELETED or NOT FOUND. | `DEL name` |

**Example Session:**
```plaintext
> SET current_project JRedis
OK
> GET current_project
JRedis
> DEL current_project
DELETED
> GET current_project
NULL
```

## 🔮 Future Improvements

- **Persistence**: Implement Snapshotting (save HashMap to JSON on disk) or AOF (Append Only File) so data survives server restarts.
- **TTL (Time-To-Live)**: Add an expiration feature (e.g., `EXPIRE key 30`) to automatically delete keys after a set time.
- **Load Testing**: creating a simple script to bombard the server with 10,000 requests to benchmark the ExecutorService.
