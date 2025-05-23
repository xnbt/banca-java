package org.example;

import java.net.URI;

public class GameClient {
    public static void main(String[] args) throws Exception {
        String token = "ee2352e97feb5fc981348fa3abf6615520d4ff39"; // token lấy từ login
        String url = "wss://fish.bd33fgabh.com/v15/ws/" + token + "?r=0";

        PersistentWebSocketClient socket = new PersistentWebSocketClient(new URI(url));
        socket.safeConnect();

        while (true) {
            Thread.sleep(10000);
        }
    }
}
