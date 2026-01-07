import socket
from protocol import Protocol
import threading

class hub:
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
        self.boiler_conn = None
        
        
    
    def handle_client(self, connection, address):
        print(f"Handling connection from {address}")
        
        while True:
            try:
                data = connection.recv(8)
                if not data:
                    print(f"Device {address} disconnected")
                    break
                dev_type, msg_type, payload = Protocol.unpack_command(data)
                if dev_type == 2: 
                    print(f"Boiler Registered: {address}")
                    self.boiler_conn = connection # <--- SAVE THE SOCKET
                    
                # CASE B: It's the Thermostat (Reading)
                elif dev_type == 1:
                    print(f"Thermostat reports: {payload}°C")
                    
                    # The Logic:
                    if payload < 20:
                        print("Too cold! Turning Boiler ON...")
                        self.send_boiler_command(1) # Send ON
                    else:
                        print("Warm enough. Turning Boiler OFF...")
                        self.send_boiler_command(0) # Send OFF
            except Exception as e:
                print(f"Error with {address}: {e}")
                break
                
        if connection == self.boiler_conn:
            self.boiler_conn = None
        connection.close()
        
    def start(self):
        print("Threading ...")
        while True:
            try:
                conn, address = self.sock.accept()
                t = threading.Thread(target=self.handle_client, args=(conn, address))
                t.start()
            except Exception as e:
                print(f"Error with {address}: {e}")
                break
            
    def send_boiler_command(self, cmd_id):
        if self.boiler_conn:
            try:
                # Pack: Boiler (2), Command (1), Payload (0 - unused for simple ON/OFF)
                packet = Protocol.pack_command(2, cmd_id, 0)
                self.boiler_conn.sendall(packet)
                print(f"-> Sent Command {cmd_id} to Boiler")
            except Exception as e:
                print(f"Failed to send to boiler: {e}")
                self.boiler_conn = None  # Reset if broken
        else:
            print("Boiler not connected yet!")
            
if __name__ == "__main__":
    hub = hub('127.0.0.1', 65432)
    hub.start()