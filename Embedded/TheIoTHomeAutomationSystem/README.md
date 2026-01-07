# The IoT Home Automation System

A Python TCP-based smart home system with a central hub that controls heating based on temperature readings.

## What It Does

- **Hub**: Central server that manages connected IoT devices
- **Thermostat**: Sends temperature readings every 2 seconds
- **Boiler**: Receives ON/OFF commands based on temperature
- Logic: If temp < 20°C → Boiler ON, else Boiler OFF

## Protocol Format

8-byte packet: `[Magic:2bytes][DeviceType:1byte][MessageType:1byte][Payload:4bytes]`
- Device Type 1: Thermostat
- Device Type 2: Boiler
- Magic number: 0xBEEF

## Run

```bash
# Terminal 1 - Start hub
python hub.py

# Terminal 2 - Start boiler
python boiler.py

# Terminal 3 - Start thermostat
python thermostat.py

# Watch as thermostat sends temps and hub controls boiler
```
