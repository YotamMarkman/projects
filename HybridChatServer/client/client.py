import socket
import threading  
from protocol import Protocol

"""
Chat client that connects to the Java server
Runs a background thread to listen for incoming messages
"""

class ChatClient:
    def __init__(self, host, port):
        self.running = False
        self.username = None
        self.host = host
        self.port = port
        self.socket = None
    
    def connect(self):
        """Open TCP connection to the server"""
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
        """Send our username to register with the server"""
        packet = Protocol.pack_message(Protocol.CMD_LOGIN, username)
        self.socket.sendall(packet)
        self.username = username
        print(f"LOGGED IN as {username}")
        
    def send_chat(self, target_user, message):
        """Send a private message to another user"""
        packedMessage = f"{target_user}|{message}"
        packet = Protocol.pack_message(Protocol.CMD_MSG, packedMessage)
        self.socket.sendall(packet)
        print(f"Sent {packedMessage}")
        
    def send_user_list_request(self):
        """Ask the server who's online"""
        print(f"[DEBUG] Sending CMD_USERS = 0x{Protocol.CMD_USERS:02X}")
        packedMessage = Protocol.pack_message(Protocol.CMD_USERS,"")
        self.socket.sendall(packedMessage)
        print(f"[DEBUG] Packet sent: {packedMessage.hex()}")
        
    def recv_exact(self, n_bytes):
        """Keep reading until we get exactly n_bytes (handles partial reads)"""
        data = b''
        while len(data) < n_bytes:
            packet = self.socket.recv(n_bytes - len(data))
            if not packet:
                return None  # Connection closed
            data += packet
        return data
    
    def send_create_group(self, name, users):
        """Tell the server to create a new group"""
        users_string = ",".join(users)
        payload = f"{name}|{users_string}"
        print(f"[DEBUG] Sending CMD_CREATE_GROUP = 0x{Protocol.CMD_CREATE_GROUP:02X}")
        packet = Protocol.pack_message(Protocol.CMD_CREATE_GROUP, payload)
        print(f"[DEBUG] Packet sent: {packet.hex()}")
        self.socket.sendall(packet)
        print(f"Request sent to create group: {name}")

    def send_group_message(self, group_name, message):
        """Send a message to everyone in a group"""
        payload = f"{group_name}|{message}"
        packet = Protocol.pack_message(Protocol.CMD_GROUP_MSG, payload)
        self.socket.sendall(packet)
        print(f"Request sent to group: {group_name}")
        
    def request_my_groups(self):
        """Ask what groups I'm in"""
        print(f"[DEBUG] Sending CMD_MY_GROUPS = 0x{Protocol.CMD_MY_GROUPS:02X}")
        packet = Protocol.pack_message(Protocol.CMD_MY_GROUPS,"")
        print(f"[DEBUG] Packet sent: {packet.hex()}")
        self.socket.sendall(packet)
        print(f"Request to see all groups")

    def listen_messages(self):
        """Background thread that listens for messages from the server"""
        print("Listening for messages...")
        while self.running:
            try:
                # Read the 19-byte header first
                header_data = self.recv_exact(19)
                
                if not header_data:
                    print("Server disconnected.")
                    break
                
                # Parse the header
                header = Protocol.unpack_header(header_data)
                
                if header:
                    msg_length = header['length']
                    cmd = header['command'] 
                    payload_content = ""
                    
                    # Read the payload if there is one
                    if msg_length > 0:
                        payload_data = self.recv_exact(msg_length)
                        payload_content = payload_data.decode('utf-8')
                    
                    # Handle different message types
                    if cmd == 0x02:  # Private message
                        print(f"\n[RECEIVED] {payload_content}")
                        
                    elif cmd == 0x03:  # User list response
                        print(f"\n[ONLINE USERS] {payload_content}")
                        
                    elif cmd == 0x04:  # Group created
                        print(f"\n[SYSTEM] {payload_content}") 

                    elif cmd == 0x05:  # Group message
                        if "|" in payload_content:
                            g_name, g_msg = payload_content.split("|", 1)
                            print(f"\n[GROUP - {g_name}] {g_msg}")
                        else:
                            print(f"\n[GROUP] {payload_content}")

                    elif cmd == 0x06:  # My groups response
                        print(f"\n[MY GROUPS] {payload_content}") 
                    
                    elif cmd == 0x08:  # Error message
                        print(f"\n[ERROR] {payload_content}")

                    # Re-prompt the user
                    print("Enter command: ", end='', flush=True)

                else:
                    print("[ERROR] Header invalid (Magic mismatch)")
                        
            except Exception as e:
                if self.running: 
                    print(f"Listener Error: {e}")
                break
    
    def start(self):
        """Main entry point - login and start the command loop"""
        # Step 1: Get username and login
        self.username = input("Enter your username: ")
        if not self.connect():
            return
        self.send_login(self.username)
        
        # Step 2: Start background listener thread
        t = threading.Thread(target=self.listen_messages, daemon=True)
        t.start()

        # Step 3: Show help menu
        print("-" * 50)
        print("COMMANDS:")
        print("  Target|Message       -> Private Message (e.g., Alice|Hi)")
        print("  #Group|Message       -> Group Message   (e.g., #StudyGroup|Hi)")
        print("  create               -> Create a new group")
        print("  groups               -> See groups you are in")
        print("  users                -> See connected users")
        print("  q                    -> Quit")
        print("-" * 50)

        # Step 4: Command loop
        while self.running:    
            try:
                user_input = input()
                command = user_input.lower()

                # Quit command
                if command in ["exit", "quit", "q"]:
                    self.running = False
                    self.socket.close()
                    break
                
                # List all online users
                elif command == "users":
                    self.send_user_list_request()
                
                # List my groups
                elif command == "groups":
                    self.request_my_groups()

                # Create a new group
                elif command == "create":
                    g_name = input("Enter Group Name: ")
                    g_users_str = input("Enter Members (comma separated): ")
                    g_users_list = [u.strip() for u in g_users_str.split(',')]
                    self.send_create_group(g_name, g_users_list)
                
                # Send a message (private or group)
                elif '|' in user_input:
                    target, message = user_input.split('|', 1)
                    
                    # Group messages start with #
                    if target.startswith("#"):
                        real_group_name = target[1:]  # Remove the #
                        self.send_group_message(real_group_name, message)
                    else:
                        # Private message
                        self.send_chat(target, message)
                else:
                    print("Format error! Use: Target|Message")
                    
            except Exception as e:
                print(f"Input Error: {e}")
                break

if __name__ == "__main__":
    client = ChatClient('127.0.0.1', 8080)
    client.start()