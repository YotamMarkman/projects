import socket
from protocol import Protocol

def main():
    host = '127.0.0.1'
    port = 65432
    
    # 1. Ask user for input
    filename = input("Enter filename to upload (e.g., photo.jpg): ")
    
    # 2. Create the phone (Socket)
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    try:
        # 3. Dial the number (Connect) <-- CRITICAL STEP
        print(f"Connecting to {host}...")
        client.connect((host, port))
        print("Connected!")
        
        # 4. Speak (Send the file)
        # We pass the CONNECTED socket to our helper function
        Protocol.send_file(client, filename)
        
    except ConnectionRefusedError:
        print("Error: The server is not running or rejected the connection.")
    except Exception as e:
        print(f"An error occurred: {e}")
    finally:
        # 5. Hang up
        client.close()

if __name__ == "__main__":
    main()