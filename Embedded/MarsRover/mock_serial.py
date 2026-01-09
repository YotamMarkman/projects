import time

class mock_serial:
    def __init__(self, port, baudrate):
        self.port = port
        self.baudrate = baudrate
        print(f"Connected on port: {self.port}")
        
    def read(self, size):
        if size > 0:
            return b'\x00' * size
        else:
            return b''
    
    def write(self, data):
        hex_string = data.hex()
        print(f"[HARDWARE] Received command: {hex_string}")
        
    def close(self):
        print(f"Port {self.port} is closed")
