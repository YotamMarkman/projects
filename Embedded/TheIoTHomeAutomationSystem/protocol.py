import struct

class Protocol:
    @staticmethod
    def pack_command(device_type, command_id, value):
        return struct.pack(
            ">HBBI", 
            0xBEEF,      # Magic header
            device_type,
            command_id,  # 1 byte
            value        # 4 bytes
        )
    
    @staticmethod
    def unpack_command(data: bytes):
        if len(data) != 8:
            raise ValueError("Packet length must be exactly 8 bytes")

        magic, device_type, msg_type, payload = struct.unpack(">HBBI", data)

        if magic != 0xBEEF:
            raise ValueError("Invalid Magic Number - Packet Corrupted")

        return device_type, msg_type, payload