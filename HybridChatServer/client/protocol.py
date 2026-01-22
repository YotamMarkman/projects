import struct
import time

"""
Python side of the protocol - matches the Java server format
Header: 19 bytes total
Format: [Magic(2) | Command(1) | Length(4) | Sequence(4) | Timestamp(8)]
"""

class Protocol:
    # Command IDs (must match the Java server!)
    CMD_LOGIN = 0x01
    CMD_MSG = 0x02
    CMD_USERS = 0x03
    CMD_CREATE_GROUP = 0x04
    CMD_GROUP_MSG = 0x05
    CMD_MY_GROUPS = 0x06
    CMD_ADD_TO_GROUP = 0x07
    CMD_ERROR = 0x08
    
    MAGIC = 0xCAFE  # Coffee!
    
    # Struct format: > = big endian, H = short(2), B = byte(1), I = int(4), Q = long(8)
    HEADER_FMT = '>HBIIQ'
    HEADER_SIZE = 19

    @staticmethod
    def pack_message(cmd_id, payload_str, seq_id=0):
        """Build a complete message to send to the server"""
        # Convert string to bytes
        if payload_str:
            payload_bytes = payload_str.encode('utf-8')
        else:
            payload_bytes = b''
        
        length = len(payload_bytes)
        timestamp = int(time.time() * 1000)  # Milliseconds since epoch
        
        # Pack the header (19 bytes)
        header = struct.pack(Protocol.HEADER_FMT, 
                             Protocol.MAGIC, 
                             cmd_id, 
                             length, 
                             seq_id, 
                             timestamp)
        
        # Return header + payload
        return header + payload_bytes

    @staticmethod
    def unpack_header(data_19_bytes):
        """Parse the header we got from the server"""
        if len(data_19_bytes) != Protocol.HEADER_SIZE:
            return None
            
        try:
            magic, cmd, length, seq, ts = struct.unpack(Protocol.HEADER_FMT, data_19_bytes)
            
            # Validate magic number
            if magic != Protocol.MAGIC:
                return None
            
            # Return as a dictionary    
            return {
                "magic": magic,
                "command": cmd,
                "length": length,
                "sequence": seq,
                "timestamp": ts
            }
        except Exception as e:
            print(f"Protocol Error: {e}")
            return None