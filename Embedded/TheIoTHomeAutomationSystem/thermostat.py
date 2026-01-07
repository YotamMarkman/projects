import socket
import random
from protocol import Protocol
import time

class thermostat:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.device_type = 1
        try:
            self.sock.connect((self.host, self.port))
            print("Thermostat connected to Hub")
        except Exception as e:
            print(f"Could not connect: {e}")
            exit()
    
    def run(self):
        print("Starting temperature simulation...")
        while True:
            temperature = random.randrange(15,30)
            message = Protocol.pack_command(self.device_type,1,temperature)
            try:
                self.sock.sendall(message)
                print(f"Sent temperature: {temperature}°C")
                time.sleep(2)
            except Exception as e:
                print(f"Connection lost: {e}")
                break
        
        self.sock.close()

if __name__ == "__main__":
    t = thermostat('127.0.0.1', 65432)
    t.run()
                