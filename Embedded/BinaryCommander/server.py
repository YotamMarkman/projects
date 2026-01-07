import socket
from protocol import Protocol

class server:
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
        
    def start(self):
        print(f"Server listening on {self.host}:{self.port}")
        client_connection, client_address = self.sock.accept()
        print(f"Connected to {client_address}")
        
        while True:
            data = client_connection.recv(7)
            
            if not data: 
                print("Client disconnected")
                break
            
            try:
                cmd_id, value = Protocol.unpack_command(data)
                
                if cmd_id == 1:
                    print(f"Moving arm to {value}")
                elif cmd_id == 2:
                    print(f"Setting LED to {value}")
                
                client_connection.sendall(b'\x01')
                
            except ValueError as e:
                print(f"Error: {e}")
                break
                
        client_connection.close()
        
if __name__ == "__main__":
    print("Initializing Robot Server...")
    robot = server('127.0.0.1', 65432)
    robot.start()