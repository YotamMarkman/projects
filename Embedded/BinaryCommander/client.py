import socket
from protocol import Protocol

class client:
    def __init__(self, host , port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        try:
            self.sock.connect((self.host, self.port))
            print("Connected to robot arm")
        except ConnectionRefusedError:
            print("Error: Could not find the Robot (Server). Is it running?")
            exit()
    
    def send_command(self, cmd_id, value):
        packed = Protocol.pack_command(cmd_id, value)
        self.sock.sendall(packed)
        data = self.sock.recv(1)
        if data == b'\x01':
            print("Command Executed Succesfully")
        else:
            print("Error, No Answer From Robot")
            
    def start(self):
        print("--- Manual Control ---")
        print("Format: <Command ID> <Value>")
        print("Example: 1 90")
        print("Type 'exit' to quit")
        while True:
            user_input = input("Enter command (1 90): ")
            if user_input.lower() == 'exit':
                break
            parts = user_input.split(" ")
            if len(parts) != 2:
                print("Error: Please enter exactly two numbers (e.g., '1 90')")
                continue 
            
            try:
                cmd = int(parts[0])
                val = int(parts[1])

                self.send_command(cmd, val)
                
            except ValueError:
                print("Error: Inputs must be numbers, not text.")
                
        self.sock.close()
        
if __name__ == "__main__":
    print("Initializing Remote Control...")
    remote = client('127.0.0.1', 65432)
    remote.start()
            