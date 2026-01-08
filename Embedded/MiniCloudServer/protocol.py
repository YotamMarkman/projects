import struct
import os

class Protocol:
    @staticmethod
    def pack_command(cmd, filename_len, filesize):
        # > = Big Endian
        # H = Unsigned Short (2 bytes) -> Magic Number
        # B = Unsigned Char (1 byte)   -> Command ID
        # I = Unsigned Int (4 bytes)   -> Value (Large Number)
        return struct.pack(
            ">HBBI", 
            0xDEAD,      # Magic header
            cmd,  # 1 byte
            filename_len,
            filesize # 4 bytes
        )
    
    @staticmethod
    def unpack_command(data: bytes):
        if len(data) != 8:
            raise ValueError("Packet length must be exactly 8 bytes")

        magic, cmd, filename_len, filesize = struct.unpack(">HBBI", data)

        if magic != 0xDEAD:
            raise ValueError("Invalid Magic Number - Packet Corrupted")

        return cmd, filename_len, filesize
    
    @staticmethod
    def send_file(sock, filename):
        try:
            filesize = os.path.getsize(filename) # Get size in bytes
            filename_bytes = filename.encode()   # Convert string name to bytes
            filename_len = len(filename_bytes)
        except FileNotFoundError:
            print("File not found!")
            return

        # Using the Method
        header = Protocol.pack_command(1, filename_len, filesize)

        sock.sendall(header)          # Send 8 bytes
        sock.sendall(filename_bytes)  # Send the name (e.g. "cat.jpg")

        # 4. Send Content (The Chunk Loop)
        print(f"Sending {filename} ({filesize} bytes)...")
        
        with open(filename, 'rb') as f: # 'rb' = Read Binary
            while True:
                # Read a small chunk (4KB)
                chunk = f.read(4096)
                if not chunk:
                    break # End of File (EOF)
                sock.sendall(chunk)
                
        print("Transfer complete.")
                
    
    @staticmethod
    def recv_file(sock):
        data = sock.recv(8)
        if not data:
            return
        cmd, filename_len, filesize = Protocol.unpack_command(data)
        filename_bytes = sock.recv(filename_len)
        filename = filename_bytes.decode()
        print(f"Receiving {filename} ({filesize} bytes)...")
        try:
            with open("server_" + filename, 'wb') as f: 
                remaining = filesize
                while remaining > 0:
                    read_size = 4096
                    if remaining < read_size:
                        read_size = remaining
                    data = sock.recv(read_size)
                    if not data:
                        break 
                    f.write(data)
                    remaining -= len(data)
            print("Download Complete.")
        except Exception as e:
            print(f"Error writing file: {e}")