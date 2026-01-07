# The Echo Server

A simple Python TCP echo server that returns whatever message it receives.

## What It Does

- Server accepts TCP connections on port 65432
- Echoes back any data sent by the client
- Client provides interactive prompt to send messages

## Run

```bash
# Terminal 1 - Start server
python server.py

# Terminal 2 - Start client
python client.py

# Type messages and see them echoed back
# Type 'quit' to exit
```
