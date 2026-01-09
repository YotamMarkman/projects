# MarsRover

A TCP-to-UART bridge system that enables remote control of a Mars rover by relaying commands from a network client to hardware via serial communication.

## Features

- **Remote Control**: Send commands over TCP to control rover hardware
- **Protocol Bridge**: Converts network packets to UART serial data
- **Mock Hardware**: Simulates serial interface for testing without physical hardware
- **Binary Protocol**: Custom 8-byte packet format with validation

## Components

- `client.py` - Remote controller interface for sending commands
- `bridge.py` - Bridge server that relays TCP commands to UART
- `protocol.py` - Binary packet packing/unpacking with magic header
- `mock_serial.py` - Simulated serial port for development testing

## Usage

```bash
# Start the bridge server
python bridge.py

# Send commands (in another terminal)
python client.py
# Enter command ID and value when prompted
```

## Protocol

8-byte packet: `[Magic: 0xCAFE][Command ID][Value]` - Commands relayed from TCP to UART for hardware execution.
