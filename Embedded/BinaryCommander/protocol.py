import struct

class Protocol:
    @staticmethod
    def pack_command(command_id, value):
        # > = Big Endian
        # H = Unsigned Short (2 bytes) -> Magic Number
        # B = Unsigned Char (1 byte)   -> Command ID
        # I = Unsigned Int (4 bytes)   -> Value (Large Number)
        return struct.pack(
            ">HBI", 
            0xCAFE,      # Magic header
            command_id,  # 1 byte
            value        # 4 bytes
        )
    
    @staticmethod
    def unpack_command(data: bytes):
        if len(data) != 7:
            raise ValueError("Packet length must be exactly 7 bytes")

        magic, command_id, value = struct.unpack(">HBI", data)

        if magic != 0xCAFE:
            raise ValueError("Invalid Magic Number - Packet Corrupted")

        return command_id, value