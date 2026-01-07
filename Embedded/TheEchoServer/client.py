import socket

class client:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        
    def start(self):
        print(f"Connecting to {self.host}:{self.port}...")
        self.sock.connect((self.host, self.port)) 
        print("Connected! Type 'quit' to exit.")
        while True:
            msg = input("You: " )
            if msg == "quit" or msg == "exit":
                break
            encoded = msg.encode()
            self.sock.sendall(encoded)
            data = self.sock.recv(1024)
            print(f"Message is {data.decode()}")
            
        self.sock.close()
        
if __name__ == "__main__":
    c = client('127.0.0.1', 65432)
    c.start()