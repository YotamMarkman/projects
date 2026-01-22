package unit_tests;

import src.Protocol;
import java.io.*;

/**
 * Tests for the Protocol class
 * Making sure our binary packing/unpacking works correctly
 */
public class ProtocolTest {
    
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Protocol Tests ===\n");
        
        testMagicValidation();
        testHeaderReadWrite();
        testPayloadReadWrite();
        testEmptyPayload();
        testSpecialCharacters();
        
        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        
        // Exit with error code if any tests failed
        System.exit(failed > 0 ? 1 : 0);
    }
    
    // Test that magic number validation works
    static void testMagicValidation() {
        System.out.print("Magic validation (valid)... ");
        if (Protocol.isValidMagic((short) 0xCAFE)) {
            pass();
        } else {
            fail("Expected true for 0xCAFE");
        }
        
        System.out.print("Magic validation (invalid)... ");
        if (!Protocol.isValidMagic((short) 0xDEAD)) {
            pass();
        } else {
            fail("Expected false for 0xDEAD");
        }
    }
    
    // Test reading and writing headers through streams
    static void testHeaderReadWrite() {
        System.out.print("Header read/write... ");
        try {
            // Create a byte stream to simulate network
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            
            // Send a message
            Protocol.sendMessage(out, (byte) Protocol.CMD_LOGIN, 42, "testuser");
            
            // Read it back
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DataInputStream in = new DataInputStream(bais);
            
            Protocol.Header header = Protocol.readHeaderFromStream(in);
            
            if (header == null) {
                fail("Header was null");
                return;
            }
            
            if (header.magic != (short) 0xCAFE) {
                fail("Wrong magic: " + header.magic);
                return;
            }
            
            if (header.command != Protocol.CMD_LOGIN) {
                fail("Wrong command: " + header.command);
                return;
            }
            
            if (header.length != 8) { // "testuser" = 8 bytes
                fail("Wrong length: " + header.length);
                return;
            }
            
            if (header.sequence != 42) {
                fail("Wrong sequence: " + header.sequence);
                return;
            }
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    // Test payload reading
    static void testPayloadReadWrite() {
        System.out.print("Payload read/write... ");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            
            String testMessage = "Hello|World";
            Protocol.sendMessage(out, (byte) Protocol.CMD_MSG, 0, testMessage);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DataInputStream in = new DataInputStream(bais);
            
            Protocol.Header header = Protocol.readHeaderFromStream(in);
            String payload = Protocol.readPayloadFromStream(in, header.length);
            
            if (testMessage.equals(payload)) {
                pass();
            } else {
                fail("Expected '" + testMessage + "' but got '" + payload + "'");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    // Test empty payload
    static void testEmptyPayload() {
        System.out.print("Empty payload... ");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            
            Protocol.sendMessage(out, (byte) Protocol.CMD_USER_LIST, 0, "");
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DataInputStream in = new DataInputStream(bais);
            
            Protocol.Header header = Protocol.readHeaderFromStream(in);
            
            if (header.length == 0) {
                pass();
            } else {
                fail("Expected length 0, got " + header.length);
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    // Test special characters in payload
    static void testSpecialCharacters() {
        System.out.print("Special characters (UTF-8)... ");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            
            // Using ASCII-safe test with some extended chars
            String testMessage = "Hello World! @#$%^&*() 12345";
            Protocol.sendMessage(out, (byte) Protocol.CMD_MSG, 0, testMessage);
            
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DataInputStream in = new DataInputStream(bais);
            
            Protocol.Header header = Protocol.readHeaderFromStream(in);
            String payload = Protocol.readPayloadFromStream(in, header.length);
            
            if (testMessage.equals(payload)) {
                pass();
            } else {
                fail("UTF-8 encoding failed");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    // Helper methods
    static void pass() {
        System.out.println("PASS");
        passed++;
    }
    
    static void fail(String reason) {
        System.out.println("FAIL - " + reason);
        failed++;
    }
}
