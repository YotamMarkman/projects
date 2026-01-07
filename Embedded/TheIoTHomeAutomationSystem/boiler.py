import socket
from protocol import Protocol

class boiler:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.device_type = 2
        try:
            self.sock.connect((self.host, self.port))
            print("Boiler connected to Hub")
        except Exception as e:
            print(f"Could not connect: {e}")
            exit()
    
    def send_handshake(self):
        print(f"Identified as {self.port}")
        packet = Protocol.pack_command(self.device_type,0, 0)
        self.sock.sendall(packet)
        print("Handshake sent")
        
    def run(self):
        self.send_handshake()
        while True:
            data = self.sock.recv(8)
            if data == b'':
                print("No Data")
                break
            dev_type, cmd, payload = Protocol.unpack_command(data)
            if cmd == 1:
                print("* BOILER ACTIVATED (HEATING) *")
            elif cmd == 0:
                print("Boiler in Standby")
            
if __name__ == "__main__":
    b = boiler('127.0.0.1', 65432)
    b.run()
            