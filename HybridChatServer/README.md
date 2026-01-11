# HybridChatServer

A cross-language chat application featuring a multithreaded Java server and Python clients, enabling real-time direct messaging between users through a custom binary protocol.

## Features

- **Multithreaded Server**: Java-based server handling multiple concurrent client connections
- **Cross-Language Support**: Python clients communicate with Java server seamlessly
- **Direct Messaging**: Private user-to-user message routing
- **Binary Protocol**: Custom 19-byte header with magic validation, timestamps, and sequence numbers
- **Thread-Safe Management**: Concurrent client tracking using `ConcurrentHashMap`

## Architecture

### Server (Java)
- `ServerMain.java` - Main server accepting connections on port 8080
- `ClientHandler.java` - Thread-per-client worker handling messages
- `ClientManager.java` - Thread-safe client registry and routing
- `Protocol.java` - Binary protocol serialization/deserialization

### Client (Python)
- `client.py` - Interactive chat client with threaded message listener
- `protocol.py` - Python implementation of binary protocol

## Usage

```bash
# Start the server
java src.ServerMain

# Connect clients (in separate terminals)
python client/client.py
# Enter username, then send messages: Target|Message
```

## Protocol

19-byte header: `[Magic: 0xCAFE][Command][Length][Sequence][Timestamp]` followed by UTF-8 payload.

**Commands**: `0x01` LOGIN, `0x02` MSG
