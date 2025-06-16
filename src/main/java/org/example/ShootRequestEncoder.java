package org.example;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ShootRequestEncoder {
    private static final int MESSAGE_SIZE = 29;

    public static byte[] encodeShootRequest(double x, double y, int bet, int fishId, int bulletId, int type) {
        ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Header (9, 0, 0, 0)
        buffer.put((byte)9);  // Message type identifier
        buffer.put((byte)0);  // 3 reserved bytes
        buffer.put((byte)0);
        buffer.put((byte)0);

        // Bet value (4 bytes)
        buffer.putInt(bet);

        // Point structure marker
        buffer.put((byte)18);  // Point type marker
        buffer.put((byte)10);  // Length of point data

        // X coordinate
        encodeCoordinate(buffer, x);

        // Y coordinate
        encodeCoordinate(buffer, y);

        // Type
        buffer.put((byte)32);  // Type marker
        buffer.put((byte)type);

        // FishId (2 bytes)
        buffer.put((byte)175); // FishId marker
        buffer.put((byte)(fishId & 0xFF));        // Low byte
        buffer.put((byte)((fishId >> 8) & 0xFF)); // High byte

        // BulletId
        buffer.put((byte)48);  // BulletId marker
        buffer.put((byte)bulletId);

        // End marker
        buffer.put((byte)1);

        return buffer.array();
    }

    private static void encodeCoordinate(ByteBuffer buffer, double value) {
        // Convert double to the game's custom float format
        float floatValue = (float)value;
        int bits = Float.floatToRawIntBits(floatValue);

        // Write the float bits in the correct byte order
        buffer.put((byte)(bits & 0xFF));
        buffer.put((byte)((bits >> 8) & 0xFF));
        buffer.put((byte)((bits >> 16) & 0xFF));
        buffer.put((byte)((bits >> 24) & 0xFF));

        // Sign byte for the coordinate
        buffer.put((byte)(value < 0 ? 0xFF : 0x00));
    }

    // Helper method to encode a message with proper structure
    private static void encodeMessageField(ByteBuffer buffer, byte marker, byte[] data) {
        buffer.put(marker);
        if (data != null && data.length > 0) {
            buffer.put((byte)data.length); // Length of data
            buffer.put(data);              // Actual data
        }
    }
}
