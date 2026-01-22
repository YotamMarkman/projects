package src;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Protocol defines our custom binary message format
 * Header: 19 bytes [Magic(2) | Command(1) | Length(4) | Sequence(4) | Timestamp(8)]
 * Followed by: UTF-8 payload (variable length)
 */
public class Protocol {
    // Command types
    public static final int CMD_LOGIN = 0x01;           // Client sends username
    public static final int CMD_MSG = 0x02;             // Private message
    public static final int CMD_USER_LIST = 0x03;       // Request online users
    public static final int CMD_CREATE_GROUP = 0x04;    // Create a new group
    public static final int CMD_GROUP_MSG = 0x05;       // Send message to group
    public static final int CMD_MY_GROUPS = 0x06;       // Get my groups
    public static final int CMD_ADD_TO_GROUP = 0x07;    // Add user to group
    public static final int CMD_ERROR = 0x08;           // Error message
    
    // Magic number to validate packets (0xCAFE = coffee, get it?)
    public static final short MAGIC = (short) 0xCAFE;
    
    // Header structure - holds metadata about the message
    public static class Header{
        public short magic;      // Should always be 0xCAFE
        public byte command;     // What type of message is this?
        public int length;       // How many bytes in the payload?
        public int sequence;     // Message number (for tracking)
        public long timestamp;   // When was this sent?
        
        public Header(short magic, byte command, int length, int sequence, long timestamp){
            this.magic = magic;
            this.command = command;
            this.length = length;
            this.sequence = sequence;
            this.timestamp = timestamp;
        }
    }
    
    // Check if the magic number is correct (packet validation)
    public static boolean isValidMagic(short magic){
        return magic == MAGIC;
    }

    // Read the 19-byte header from the socket
    public static Header readHeaderFromStream(DataInputStream in){
        try {
            short magic = in.readShort();       // 2 bytes
            byte command = in.readByte();       // 1 byte
            int length = in.readInt();          // 4 bytes
            int sequence = in.readInt();        // 4 bytes
            long timestamp = in.readLong();     // 8 bytes
            return new Header(magic, command, length, sequence, timestamp);
        } catch (Exception e) {
            // Connection probably died
            return null;
        }
    }

    // Read the actual message content (the part after the header)
    public static String readPayloadFromStream(DataInputStream in, int length){
        try {
            byte[] payloadBytes = new byte[length];
            in.readFully(payloadBytes);  // Read exactly 'length' bytes
            return new String(payloadBytes, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    // Send a complete message (header + payload)
    public static void sendMessage(DataOutputStream out, byte cmdId, int seqId, String payload) {
        try {
            byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            long timestamp = System.currentTimeMillis();
            
            // Write header (19 bytes)
            out.writeShort(MAGIC);     
            out.writeByte(cmdId);       
            out.writeInt(length);       
            out.writeInt(seqId);        
            out.writeLong(timestamp);
            
            // Write payload (if there is one)
            if (length > 0) {
                out.write(bytes);
            }
            out.flush();  // Make sure it actually sends
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}