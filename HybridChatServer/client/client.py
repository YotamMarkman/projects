import socket
import threading  
from protocol import Protocol

class ChatClient:
    def __init__(self, host, port):
        self.running = False
        self.username = None
        self.host = host
        self.port = port
        self.socket = None
    
    def connect(self):
        try:
            self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.socket.connect((self.host, self.port))
            self.running = True 
            print(f"Connected to {self.host} on port {self.port}")
            return True
        except Exception as e:
            print(f"Connection Failed: {e}")
            return False
        
    def send_login(self, username):
        packet = Protocol.pack_message(Protocol.CMD_LOGIN, username)
        self.socket.sendall(packet)
        self.username = username
        print(f"LOGGED IN as {username}")
        
    def send_chat(self, target_user, message):
        packedMessage = f"{target_user}|{message}"
        packet = Protocol.pack_message(Protocol.CMD_MSG, packedMessage)
        self.socket.sendall(packet)
        print(f"Sent {packedMessage}")
        
    def recv_exact(self, n_bytes):
        data = b''
        while len(data) < n_bytes:
            packet = self.socket.recv(n_bytes - len(data))
            if not packet:
                return None 
            data += packet
        return data

    def listen_messages(self):
        print("Listening for messages...")
        while self.running:
            try:
                header_data = self.recv_exact(19)
                
                if not header_data:
                    print("Server disconnected.")
                    break
                
                header = Protocol.unpack_header(header_data)
                
                if header:
                    msg_length = header['length']
                    if msg_length > 0:
                        payload_data = self.recv_exact(msg_length)
                        message = payload_data.decode('utf-8')
                        
                        print(f"\n[RECEIVED] {message}")
                        print("Enter command: ", end='', flush=True)
                else:
                    print("[ERROR] Header invalid (Magic mismatch)")
                        
            except Exception as e:
                if self.running: 
                    print(f"Listener Error: {e}")
                break
    
    def start(self):
        self.username = input("Enter your username: ")
        if not self.connect():
            return
        self.send_login(self.username)
        t = threading.Thread(target=self.listen_messages, daemon=True)
        t.start()
        print("Type 'Target|Message' to chat. Type 'q' to quit.")
        while self.running:    
            try:
                user_input = input()
                if user_input.lower() in ["exit", "quit", "q"]:
                    self.running = False
                    self.socket.close()
                    break
                if '|' in user_input:
                    target, message = user_input.split('|', 1)
                    self.send_chat(target, message)
                else:
                    print("Format error! Use: Target|Message")   
            except Exception as e:
                print(f"Input Error: {e}")
                break
if __name__ == "__main__":
    client = ChatClient('127.0.0.1', 8080)
    client.start()