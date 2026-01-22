import unittest
import struct
import sys
import os

# Add the client folder to path so we can import protocol
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'client'))

from protocol import Protocol

"""
Tests for the Python Protocol class
Making sure it matches the Java implementation
"""

class ProtocolTest(unittest.TestCase):
    
    def test_magic_constant(self):
        """Magic number should be 0xCAFE"""
        self.assertEqual(Protocol.MAGIC, 0xCAFE)
    
    def test_header_size(self):
        """Header should be exactly 19 bytes"""
        self.assertEqual(Protocol.HEADER_SIZE, 19)
    
    def test_command_constants(self):
        """Command IDs should match Java server"""
        self.assertEqual(Protocol.CMD_LOGIN, 0x01)
        self.assertEqual(Protocol.CMD_MSG, 0x02)
        self.assertEqual(Protocol.CMD_USERS, 0x03)
        self.assertEqual(Protocol.CMD_CREATE_GROUP, 0x04)
        self.assertEqual(Protocol.CMD_GROUP_MSG, 0x05)
        self.assertEqual(Protocol.CMD_MY_GROUPS, 0x06)
    
    def test_pack_message_length(self):
        """Packed message should be header + payload length"""
        payload = "Hello"
        packet = Protocol.pack_message(Protocol.CMD_MSG, payload)
        
        # 19 bytes header + 5 bytes payload
        self.assertEqual(len(packet), 19 + 5)
    
    def test_pack_empty_payload(self):
        """Empty payload should just be 19 byte header"""
        packet = Protocol.pack_message(Protocol.CMD_USERS, "")
        self.assertEqual(len(packet), 19)
    
    def test_unpack_header_valid(self):
        """Should correctly unpack a valid header"""
        # Create a known header
        packet = Protocol.pack_message(Protocol.CMD_LOGIN, "testuser")
        
        # Unpack just the header part
        header = Protocol.unpack_header(packet[:19])
        
        self.assertIsNotNone(header)
        self.assertEqual(header['magic'], 0xCAFE)
        self.assertEqual(header['command'], Protocol.CMD_LOGIN)
        self.assertEqual(header['length'], 8)  # "testuser" = 8 chars
    
    def test_unpack_header_invalid_magic(self):
        """Should reject header with wrong magic"""
        # Create a header with bad magic
        bad_header = struct.pack('>HBIIQ', 0xDEAD, 0x01, 5, 0, 0)
        
        result = Protocol.unpack_header(bad_header)
        self.assertIsNone(result)
    
    def test_unpack_header_wrong_size(self):
        """Should reject header that's not 19 bytes"""
        too_short = b'\x00' * 10
        too_long = b'\x00' * 30
        
        self.assertIsNone(Protocol.unpack_header(too_short))
        self.assertIsNone(Protocol.unpack_header(too_long))
    
    def test_utf8_encoding(self):
        """Should handle special characters"""
        payload = "Hello 你好 שלום"
        packet = Protocol.pack_message(Protocol.CMD_MSG, payload)
        
        header = Protocol.unpack_header(packet[:19])
        payload_bytes = packet[19:]
        decoded = payload_bytes.decode('utf-8')
        
        self.assertEqual(decoded, payload)
    
    def test_private_message_format(self):
        """Private message should be target|message"""
        packet = Protocol.pack_message(Protocol.CMD_MSG, "alice|Hello there!")
        
        header = Protocol.unpack_header(packet[:19])
        payload = packet[19:].decode('utf-8')
        
        target, message = payload.split('|', 1)
        self.assertEqual(target, "alice")
        self.assertEqual(message, "Hello there!")
    
    def test_group_message_format(self):
        """Group message should be groupname|message"""
        packet = Protocol.pack_message(Protocol.CMD_GROUP_MSG, "devteam|Meeting at 3pm")
        
        header = Protocol.unpack_header(packet[:19])
        self.assertEqual(header['command'], Protocol.CMD_GROUP_MSG)


if __name__ == '__main__':
    print("=== Python Protocol Tests ===\n")
    unittest.main(verbosity=2)
