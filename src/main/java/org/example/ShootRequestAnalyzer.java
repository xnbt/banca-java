package org.example;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ShootRequestAnalyzer {
    public static void main(String[] args) {
        // Example usage with exact values from devtool
        double x = -398.1807929090785;
        double y = -181.5899986040532;
        int bet = 100;
        int fishId = 1711;
        int bulletId = 141;
        int type = 1; // Lock type

        byte[] encoded = ShootRequestEncoder.encodeShootRequest(x, y, bet, fishId, bulletId, type);

        // Print raw bytes
        printRawBytes(encoded);

        // Print detailed analysis
        analyzeBytes(encoded);

        // Compare with expected bytes from devtool
        byte[] expectedBytes = {
                9, 0, 0, 0,      // Header (0-3)
                0, 0, 0, 89,     // Bet and markers (4-7)
                64, 18, 10, 13,  // Point structure (8-11)
                36, 23, (byte)199, (byte)195,  // X coordinate (12-15)
                21, 10, (byte)151, 53,         // Y coordinate (16-19)
                (byte)195, 24, 1, 32,          // Type and markers (20-23)
                (byte)175, 13, 48, (byte)141,  // FishId and BulletId (24-27)
                1                              // End marker (28)
        };

        compareWithDevtool(encoded, expectedBytes);
    }

    public static void printRawBytes(byte[] bytes) {
        System.out.println("Raw bytes output:");
        for (int i = 0; i < bytes.length; i++) {
            System.out.printf("%2d: %3d (0x%02X)\n", i, bytes[i] & 0xFF, bytes[i] & 0xFF);
        }
        System.out.println();
    }

    public static void analyzeBytes(byte[] bytes) {
        System.out.println("Detailed message structure analysis:");
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        try {
            // Header (bytes 0-3)
            System.out.println("\nHeader Section:");
            System.out.printf("Message Type: %d\n", buffer.get() & 0xFF);
            System.out.printf("Reserved bytes: %d %d %d\n",
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF);

            // Bet and markers (bytes 4-7)
            System.out.println("\nBet Section:");
            System.out.printf("Bet value bytes: %d %d %d %d\n",
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF);

            // Point structure (bytes 8-11)
            System.out.println("\nPoint Structure Markers:");
            System.out.printf("Point markers: %d %d %d %d\n",
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF);

            // X coordinate (bytes 12-15)
            System.out.println("\nX Coordinate Bytes:");
            byte[] xBytes = new byte[4];
            buffer.get(xBytes);
            System.out.printf("X value: %02X %02X %02X %02X\n",
                    xBytes[0] & 0xFF,
                    xBytes[1] & 0xFF,
                    xBytes[2] & 0xFF,
                    xBytes[3] & 0xFF);

            // Y coordinate (bytes 16-19)
            System.out.println("\nY Coordinate Bytes:");
            byte[] yBytes = new byte[4];
            buffer.get(yBytes);
            System.out.printf("Y value: %02X %02X %02X %02X\n",
                    yBytes[0] & 0xFF,
                    yBytes[1] & 0xFF,
                    yBytes[2] & 0xFF,
                    yBytes[3] & 0xFF);

            // Type and markers (bytes 20-23)
            System.out.println("\nType Section:");
            System.out.printf("Type marker: %d\n", buffer.get() & 0xFF);
            System.out.printf("Type value: %d\n", buffer.get() & 0xFF);
            System.out.printf("Type additional: %d %d\n",
                    buffer.get() & 0xFF,
                    buffer.get() & 0xFF);

            // FishId and BulletId (bytes 24-27)
            System.out.println("\nFishId and BulletId Section:");
            System.out.printf("FishId marker: %d\n", buffer.get() & 0xFF);
            System.out.printf("FishId value: %d\n", buffer.get() & 0xFF);
            System.out.printf("BulletId marker: %d\n", buffer.get() & 0xFF);
            System.out.printf("BulletId value: %d\n", buffer.get() & 0xFF);

            // End marker (byte 28)
            System.out.println("\nEnd Section:");
            System.out.printf("End marker: %d\n", buffer.get() & 0xFF);

        } catch (Exception e) {
            System.err.println("Error during analysis at position: " + buffer.position());
            e.printStackTrace();
        }
    }

    private static void compareWithDevtool(byte[] encoded, byte[] expected) {
        System.out.println("\nComparing with devtool bytes:");
        if (encoded.length != expected.length) {
            System.out.printf("Length mismatch! Encoded: %d, Expected: %d\n",
                    encoded.length, expected.length);
            return;
        }

        boolean match = true;
        for (int i = 0; i < encoded.length; i++) {
            if (encoded[i] != expected[i]) {
                System.out.printf("Mismatch at byte %d: Encoded=%d (0x%02X), Expected=%d (0x%02X)\n",
                        i,
                        encoded[i] & 0xFF,
                        encoded[i] & 0xFF,
                        expected[i] & 0xFF,
                        expected[i] & 0xFF);
                match = false;
            }
        }

        if (match) {
            System.out.println("All bytes match exactly with devtool output!");
        }
    }

    // Utility method to print byte array as hex string
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b & 0xFF));
        }
        return sb.toString().trim();
    }

    // Utility method to convert 4 bytes to float (IEEE 754)
    private static float bytesToFloat(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getFloat();
    }

    // Test method to verify encoding
    public static void testEncoding() {
        System.out.println("Running encoding test...");

        // Test case 1: Original values from devtool
        double x1 = -398.180792909785;
        double y1 = -181.58999860405932;
        byte[] encoded1 = ShootRequestEncoder.encodeShootRequest(x1, y1, 100, 1711, 141, 1);
        System.out.println("\nTest Case 1 - Original values:");
        printRawBytes(encoded1);

        // Test case 2: Different values
        double x2 = -200.0;
        double y2 = -100.0;
        byte[] encoded2 = ShootRequestEncoder.encodeShootRequest(x2, y2, 200, 1000, 100, 2);
        System.out.println("\nTest Case 2 - Different values:");
        printRawBytes(encoded2);
    }
}
