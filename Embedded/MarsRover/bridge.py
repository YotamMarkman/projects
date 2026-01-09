import socket
from protocol import Protocol
from mock_serial import mock_serial

class BridgeServer:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # SOL_SOCKET = Socket Level
        # SO_REUSEADDR = Allow reuse of local addresses
        # 1 = True
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((self.host, self.port))
        self.sock.listen()
        self.uart = mock_serial('/dev/ttyUSB0', 9600)

    def start(self):
        print("Bridge System Active.")
        
        while True:
            conn, conn_add = self.sock.accept()
            print(f"Connected to socket with address {conn_add}")
            while True:
                try:
                    data = conn.recv(8)
                    if not data:
                        break
                    cmd_id, value = Protocol.unpack_command(data)
                    print(f"Relaying Command {cmd_id}...")
                    self.uart.write(data)
                except Exception as e:
                    # Handle disconnects or protocol errors
                    print(f"Error: {e}")
                    break
            
            conn.close()
            print("Controller disconnected. Waiting for new connection...")

if __name__ == "__main__":
    server = BridgeServer('127.0.0.1', 65432)
    server.start()