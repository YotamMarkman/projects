import struct

class Protocol:
    
    @staticmethod
    def pack_command(cmd_id, value):
        """
        Takes a command ID (int) and a value (int).
        Returns a bytes object (packed binary data).
        """
        return struct.pack(
            ">HHI", 
            0xCAFE, # Magic header
            cmd_id,  
            value
        )

    @staticmethod
    def unpack_command(data):
        """
        Takes raw bytes (data).
        Returns a tuple: (cmd_id, value)
        """
        if len(data) != 8:
            raise ValueError("Packet length must be exactly 8 bytes")

        magic, cmd_id, value = struct.unpack(">HHI", data)

        if magic != 0xCAFE:
            raise ValueError("Invalid Magic Number - Packet Corrupted")

        return cmd_id, value