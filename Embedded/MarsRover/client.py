import socket
from protocol import Protocol

def main():
    host = '127.0.0.1'
    port = 65432
    
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        client.connect((host, port))
        print(f"Connected to Mars Rover Bridge at {host}:{port}")
    except Exception as e:
        print(f"Could not connect: {e}")
        return

    while True:
        try:
            print("\n--- NEW COMMAND ---")
            
            user_input = input("Enter Command ID (or 'q' to quit): ")
            if user_input.lower() == 'q':
                break
                
            cmd_id = int(user_input)
            value = int(input("Enter Value (Speed/Angle): "))
            
            packet = Protocol.pack_command(cmd_id, value)
            client.sendall(packet)
            
            print(f"Sent: Command={cmd_id}, Value={value}")
            
        except ValueError:
            print("Please enter valid integers!")
        except Exception as e:
            print(f"Connection Error: {e}")
            break
    
    client.close()
    
if __name__ == "__main__":
    main()