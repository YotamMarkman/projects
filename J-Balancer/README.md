# J-Balancer

A Java-based HTTP load balancer that distributes incoming requests across multiple backend servers using round-robin load balancing.

## Overview

J-Balancer is a simple but complete load balancer implementation that:
- Accepts HTTP requests on port 8080
- Forwards requests to backend servers using round-robin distribution
- Supports multiple backend servers running on different ports
- Handles full-duplex communication between clients and backends
- Provides thread-safe concurrent request handling

## Architecture

```
Client (Browser) → Load Balancer (Port 8080) → Backend Servers (Ports 8081, 8082)
                          ↓
                   Round-Robin Selection
```

### Components

1. **LoadBalancerServer** - Main server that listens on port 8080 and accepts client connections
2. **ClientHandler** - Handles individual client requests and forwards them to selected backend servers
3. **LoadBalancerMap** - Implements round-robin backend selection algorithm
4. **Mock_backend** - Simple HTTP backend servers for testing
5. **Main** - Entry point that starts the mock backend servers

## Quick Start

### 1. Compile the Project
```bash
cd J-Balancer
javac *.java
```

### 2. Start Backend Servers
```bash
java Main
```
This will start two mock backend servers:
- Backend-1 on port 8081
- Backend-2 on port 8082

### 3. Start Load Balancer
In a separate terminal:
```bash
java LoadBalancerServer
```
This starts the load balancer listening on port 8080.

### 4. Test the Load Balancer
Open your browser or use curl to test:
```bash
curl http://localhost:8080
```

You should see responses alternating between "Backend-1 response" and "Backend-2 response".

## How It Works

1. **Client Connection**: When a client connects to port 8080, the LoadBalancerServer accepts the connection
2. **Backend Selection**: The LoadBalancerMap uses round-robin to select the next available backend port
3. **Request Forwarding**: A ClientHandler is created to forward the request:
   - Opens connection to selected backend server
   - Creates two threads for full-duplex communication:
     - Thread 1: Forwards data from client to backend
     - Thread 2: Forwards data from backend to client
4. **Response**: The backend's HTTP response is forwarded back to the client
5. **Cleanup**: Connections are properly closed when transfer completes

## Configuration

### Backend Servers
To modify backend server ports, update the `backendPorts` array in `LoadBalancerServer.java`:
```java
String[] backendPorts = {"8081", "8082", "8083"}; // Add more as needed
```

And start corresponding backend servers in `Main.java`:
```java
Mock_backend backend3 = new Mock_backend(8083, "Backend-3");
backend3.start();
```

### Load Balancer Port
To change the load balancer port, modify `LOAD_BALANCER_PORT` in `LoadBalancerServer.java`:
```java
public static final int LOAD_BALANCER_PORT = 9000; // Change as needed
```

## Testing

### Basic Functionality Test
```bash
# Terminal 1: Start backends
java Main

# Terminal 2: Start load balancer  
java LoadBalancerServer

# Terminal 3: Test with curl
curl http://localhost:8080
curl http://localhost:8080
curl http://localhost:8080
```

### Check Port Status
To verify ports are listening:
```bash
# Windows PowerShell
netstat -ano | Select-String ":8080|:8081|:8082"

# Linux/Mac
netstat -ln | grep "8080\|8081\|8082"
```

## Recent Fixes Applied

The following issues were identified and fixed:

1. **Backend Port Mismatch**: Fixed LoadBalancerServer to use correct backend ports (8081, 8082) instead of (9001, 9002)
2. **Load Balancer Loop**: Fixed ClientHandler to connect to backend servers instead of the load balancer itself
3. **Round-Robin Implementation**: Added proper usage of LoadBalancerMap.getNextBackend()
4. **Stream Handling**: Replaced single read/write with full-duplex stream forwarding using two threads
5. **Request Completeness**: Improved to handle complete HTTP requests/responses instead of partial data

## Troubleshooting

### Port 8080 Not Accessible
1. Check if LoadBalancerServer started successfully (should print "Load Balancer started on port 8080")
2. Verify no other service is using port 8080: `netstat -ano | Select-String ":8080"`
3. Check Windows Firewall settings if accessing remotely

### Backend Connection Errors
1. Ensure Main.java is running and backends started successfully
2. Verify backend ports match LoadBalancerServer configuration
3. Check for "Printing on port: XXXX" messages in load balancer console

### No Response from Backends
1. Test backends directly: `curl http://localhost:8081` and `curl http://localhost:8082`
2. Check backend console logs for incoming requests
3. Verify ClientHandler threads are properly forwarding data

## Future Enhancements

- Health checking for backend servers
- Weighted load balancing
- Support for HTTPS/SSL termination
- Configuration file support
- Metrics and monitoring
- Graceful shutdown handling
- Connection pooling

## Dependencies

- Java 8 or higher
- No external dependencies required (uses only Java standard library)

## License

This project is for educational purposes.