# MiniCloudServer

A lightweight TCP-based file upload server that enables clients to transfer files to a centralized storage location.

## Features
- **File Upload**: Send files from client to server over TCP sockets
- **Binary Protocol**: Custom packet format with magic header validation
- **Persistent Storage**: Files saved in `server_storage/` directory

## Components
- `server.py` - Listens on port 65432 and receives file uploads
- `client.py` - Connects to server and uploads specified files
- `protocol.py` - Handles binary packet packing/unpacking and file transfer logic

## Usage
```bash
# Start the server
python server.py

# Upload a file (in another terminal)
python client.py
# Enter filename when prompted
```

## Protocol
8-byte header: `[Magic: 0xDEAD][Command][Filename Length][File Size]` followed by filename and file content.
