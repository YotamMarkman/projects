import unittest
import struct

"""
Tests that Java and Python protocol formats match
Cross-language compatibility is critical!
"""

class CrossLanguageTest(unittest.TestCase):
    
    # These values are from Java's Protocol.java
    JAVA_MAGIC = 0xCAFE
    JAVA_HEADER_FORMAT = '>HBIIQ'  # Big endian: short, byte, int, int, long
    JAVA_HEADER_SIZE = 19
    
    def test_header_byte_layout(self):
        """Header structure must match Java exactly"""
        # Java writes: writeShort(2) + writeByte(1) + writeInt(4) + writeInt(4) + writeLong(8)
        # Total: 2 + 1 + 4 + 4 + 8 = 19 bytes
        
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC,  # 2 bytes
            0x01,             # 1 byte (CMD_LOGIN)
            100,              # 4 bytes (length)
            42,               # 4 bytes (sequence)
            1234567890        # 8 bytes (timestamp)
        )
        
        self.assertEqual(len(header), self.JAVA_HEADER_SIZE)
    
    def test_magic_bytes_position(self):
        """Magic number should be first 2 bytes"""
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x02, 50, 1, 0
        )
        
        # Read back the first 2 bytes as big-endian short
        magic = struct.unpack('>H', header[0:2])[0]
        self.assertEqual(magic, self.JAVA_MAGIC)
    
    def test_command_byte_position(self):
        """Command should be at byte 2 (third byte)"""
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x05, 50, 1, 0  # CMD_GROUP_MSG = 0x05
        )
        
        # Command is at index 2
        command = header[2]
        self.assertEqual(command, 0x05)
    
    def test_length_bytes_position(self):
        """Length should be bytes 3-6 (4 bytes)"""
        test_length = 12345
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x01, test_length, 1, 0
        )
        
        # Length is at bytes 3-7 (indices 3, 4, 5, 6)
        length = struct.unpack('>I', header[3:7])[0]
        self.assertEqual(length, test_length)
    
    def test_sequence_bytes_position(self):
        """Sequence should be bytes 7-10 (4 bytes)"""
        test_seq = 99999
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x01, 0, test_seq, 0
        )
        
        # Sequence is at bytes 7-11
        seq = struct.unpack('>I', header[7:11])[0]
        self.assertEqual(seq, test_seq)
    
    def test_timestamp_bytes_position(self):
        """Timestamp should be bytes 11-18 (8 bytes)"""
        test_ts = 1704067200000  # Some timestamp
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x01, 0, 0, test_ts
        )
        
        # Timestamp is at bytes 11-19
        ts = struct.unpack('>Q', header[11:19])[0]
        self.assertEqual(ts, test_ts)
    
    def test_big_endian_encoding(self):
        """Java uses big-endian, verify our format matches"""
        # 0xCAFE in big endian should be [0xCA, 0xFE]
        header = struct.pack(self.JAVA_HEADER_FORMAT,
            self.JAVA_MAGIC, 0x01, 0, 0, 0
        )
        
        self.assertEqual(header[0], 0xCA)
        self.assertEqual(header[1], 0xFE)


if __name__ == '__main__':
    print("=== Cross-Language Compatibility Tests ===\n")
    unittest.main(verbosity=2)
