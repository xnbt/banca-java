package org.example;

public class TestShootRequest {
    public static void main(String[] args) {
        try {
            double x = -398.180792909785;
            double y = -181.58999860405932;

            byte[] encoded = ShootRequestEncoder.encodeShootRequest(x, y, 100, 1711, 141, 1);

            // Print each byte to compare with devtool output
            for (int i = 0; i < encoded.length; i++) {
                System.out.printf("%d: %d\n", i, encoded[i] & 0xFF);
            }

            ShootRequestAnalyzer.analyzeBytes(encoded);

        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
