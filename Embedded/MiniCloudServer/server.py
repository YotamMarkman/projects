import socket
import os
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
        storage_folder = "server_storage"
        if not os.path.exists(storage_folder):
            print(f"Creating storage folder: {storage_folder}")
            os.makedirs(storage_folder) 
        os.chdir(storage_folder)
        print(f"Server listening on {self.host}:{self.port}")
        while True:
            try:
                conn, conn_address = self.sock.accept()
                print(f"Connected to {conn_address}")
                try:
                    Protocol.recv_file(conn)
                except Exception as e:
                    print(f"Transfer failed: {e}")
                conn.close()
                print("Connection closed.\n")
            except KeyboardInterrupt:
                print("Server stopping...")
                break
            except Exception as e:
                print(f"Critical Server Error: {e}")
                break

if __name__ == "__main__":
    s = server('127.0.0.1', 65432)
    s.start()