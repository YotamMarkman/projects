package src;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

public class Protocol {
    public static final int CMD_LOGIN = 0x01;
    public static final int CMD_MSG = 0x02;
    public static final int CMD_USER_LIST = 0x03;
    public static final short MAGIC = (short) 0xCAFE;
    
    public static class Header{
        public short magic;
        public byte command;
        public int length;
        public int sequence;
        public long timestamp;
        public Header(short magic, byte command, int length, int sequence, long timestamp){
            this.magic = magic;
            this.command = command;
            this.length = length;
            this.sequence = sequence;
            this.timestamp = timestamp;
        }
    }
    public static boolean isValidMagic(short magic){
        return magic == MAGIC;
    }

    public static Header readHeaderFromStream(DataInputStream in){
        try {
            short magic = in.readShort();       // Reads 2 bytes
            byte command = in.readByte();       // Reads 1 byte
            int length = in.readInt();          // Reads 4 bytes
            int sequence = in.readInt();        // Reads 4 bytes
            long timestamp = in.readLong();     // Reads 8 bytes
            return new Header(magic, command, length, sequence, timestamp);
        } catch (Exception e) {
            // If we fail to read, the connection is likely broken
            return null;
        }
    }

    public static String readPayloadFromStream(DataInputStream in, int length){
        try {
            byte[] payloadBytes = new byte[length];
            in.readFully(payloadBytes);
            return new String(payloadBytes, "UTF-8");
        } catch (Exception e) {
            // If we fail to read, the connection is likely broken
            return null;
        }
    }

    public static void sendMessage(DataOutputStream out, byte cmdId, int seqId, String payload) {
        try {
            byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            long timestamp = System.currentTimeMillis();
            out.writeShort(MAGIC);     
            out.writeByte(cmdId);       
            out.writeInt(length);       
            out.writeInt(seqId);        
            out.writeLong(timestamp);   
            if (length > 0) {
                out.write(bytes);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }