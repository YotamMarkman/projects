# Binary Commander

A Python TCP client-server system using a custom binary protocol to send robot control commands.

## What It Does

- Server simulates a robot arm that receives binary commands
- Client sends commands (arm movement, LED control) in a packed binary format
- Uses custom protocol with magic number (0xCAFE) for packet validation
- Server acknowledges each command with ACK byte

## Protocol Format

7-byte packet: `[Magic:2bytes][CommandID:1byte][Value:4bytes]`
- Command 1: Move arm to angle
- Command 2: Set LED value

## Run

```bash
# Terminal 1 - Start server
python server.py

# Terminal 2 - Start client
python client.py

# Enter commands like: 1 90
```
