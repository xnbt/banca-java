package v2;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class GameWebSocketClient {
    private WebSocketClient webSocketClient;
    private static final String CHARSET = "UTF-8";

    public void connectToGame(String gameServer, String token, String gameId, String posthost, String subAgentCode) {
        try {
            // Construct the base WebSocket URL
            String wsUrl = String.format("wss://%s/ws", gameServer);

            // Add query parameters
            Map<String, String> params = new HashMap<>();
            params.put("token", token);
            params.put("game", gameId);
            params.put("posthost", posthost);
            params.put("sac", subAgentCode);
            params.put("r", "0"); // retry count, starting with 0

            // Build query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append('&');
                }
                queryString.append(URLEncoder.encode(entry.getKey(), CHARSET))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), CHARSET));
            }

            // Complete WebSocket URL
            String fullUrl = wsUrl + "?" + queryString.toString();

            // Create WebSocket client
            webSocketClient = new WebSocketClient(new URI(fullUrl)) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("WebSocket Connection Opened");
                    startPingTimer(); // Start sending ping every 5 seconds
                }

                @Override
                public void onMessage(String message) {
                    // Handle text messages if any
                    System.out.println("Received text message: " + message);
                }

                @Override
                public void onMessage(ByteBuffer bytes) {
                    // Handle binary messages
                    handleBinaryMessage(bytes);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket Connection Closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("WebSocket Error: " + ex.getMessage());
                }
            };

            // Set connection timeout
            webSocketClient.setConnectionLostTimeout(60);

            // Connect
            webSocketClient.connect();

        } catch (Exception e) {
            System.err.println("Error creating WebSocket connection: " + e.getMessage());
        }
    }

    private void startPingTimer() {
        new Thread(() -> {
            try {
                while (webSocketClient.isOpen()) {
                    sendPing();
                    Thread.sleep(5000); // Sleep for 5 seconds
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void sendPing() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            // Send ping message according to game protocol
            // You'll need to implement the actual ping message format
            ByteBuffer pingMessage = createPingMessage();
            webSocketClient.send(pingMessage);
        }
    }

    private ByteBuffer createPingMessage() {
        // Implement the game's ping message format
        // This is a placeholder - you'll need to implement the actual format
        return ByteBuffer.allocate(4).putInt(0); // Ping message format
    }

    private void handleBinaryMessage(ByteBuffer bytes) {
        try {
            // Convert ByteBuffer to byte array
            byte[] messageBytes = new byte[bytes.remaining()];
            bytes.get(messageBytes);

            // Here you would implement the game's binary protocol handling
            // Based on the game code, messages are protobuf encoded
            // You'll need to implement the specific message handling based on the game protocol

            // Example structure based on the game code:
            // 1. First bytes might indicate message type
            // 2. Rest of the data would be the protobuf encoded message
            int messageType = messageBytes[0]; // Example - actual format may differ

            switch (messageType) {
                case 0: // LOGIN_ACK
                    handleLoginAck(messageBytes);
                    break;
                case 1: // GAME_STATE
                    handleGameState(messageBytes);
                    break;
                // Add other message types as needed
            }

        } catch (Exception e) {
            System.err.println("Error handling binary message: " + e.getMessage());
        }
    }

    private void handleLoginAck(byte[] messageBytes) {
        // Implement login acknowledgment handling
        // This would correspond to the S2U_LOGIN_ACK message in the game code
    }

    private void handleGameState(byte[] messageBytes) {
        // Implement game state update handling
    }

    public void disconnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }

    // Example usage
    public static void main(String[] args) {
        GameWebSocketClient client = new GameWebSocketClient();
        client.connectToGame(
                "fish.bd33fgabh.com",  // Replace with actual game server
                "c59b2f946a06ce01a9eb96e96a77ed4d335dd668",               // Replace with actual token
                "289",                 // Replace with actual game ID
                "wbgame.bd33fgabh.com",               // Replace with actual post host
                "0"           // Replace with actual sub agent code
        );
    }
}
