import struct
import time

class Protocol:
    CMD_LOGIN = 0x01
    CMD_MSG = 0x02
    MAGIC = 0xCAFE
    
    # Format: > (Big Endian), H (2), B (1), I (4), I (4), Q (8) = 19 Bytes
    HEADER_FMT = '>HBIIQ'
    HEADER_SIZE = 19

    @staticmethod
    def pack_message(cmd_id, payload_str, seq_id=0):
        if payload_str:
            payload_bytes = payload_str.encode('utf-8')
        else:
            payload_bytes = b''
        length = len(payload_bytes)
        timestamp = int(time.time() * 1000)
        
        header = struct.pack(Protocol.HEADER_FMT, 
                             Protocol.MAGIC, 
                             cmd_id, 
                             length, 
                             seq_id, 
                             timestamp)
        return header + payload_bytes

    @staticmethod
    def unpack_header(data_19_bytes):
        if len(data_19_bytes) != Protocol.HEADER_SIZE:
            return None
            
        try:
            magic, cmd, length, seq, ts = struct.unpack(Protocol.HEADER_FMT, data_19_bytes)
            
            if magic != Protocol.MAGIC:
                return None
                
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