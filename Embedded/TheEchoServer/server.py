import socket


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
        client_connection, client_address = self.sock.accept()
        print(f"New Connection from: {client_address}")
        while True:
            data = client_connection.recv(1024)
            if data == b'':
                print("Client Disconnected")
                break
            client_connection.sendall(data)
        client_connection.close()
            
            
if __name__ == "__main__":
    srv = server('127.0.0.1', 65432)
    srv.start()