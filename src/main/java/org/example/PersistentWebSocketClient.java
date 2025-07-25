package org.example;

import annin_protocol.CommandOuterClass;
import annin_protocol.FullLogin;
import annin_protocol.Misssion115;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import fish.TimeLine;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import protocol.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class PersistentWebSocketClient extends WebSocketClient {

    private final Queue<List<Map<String, Object>>> pendingBatches = new LinkedList<>();
    private boolean isProcessingBatch = false;

    private final URI uri;
    private int retryCount = 0;
    private boolean manualClose = false;
    private boolean pingStarted = false;
    private boolean loginSent = false;
    private Timer pingTimer;

    // Protocol Commands - User to Server
    private static final int U2S_NONE = 0;
    private static final int U2S_JOIN_REQ = 21;
    private static final int U2S_SHOOT_REQ = 22;
    private static final int U2S_HIT_REQ = 23;
    private static final int U2S_LEAVE_REQ = 24;
    private static final int U2S_JACKPOT_REQ = 25;
    private static final int U2S_SHOOT_CANCEL = 26;
    private static final int U2S_JACKOPT_HISTORY = 27;

    // Protocol Commands - Server to User
    private static final int S2U_NONE = 0;
    private static final int S2U_JOIN_ACK = 21;
    private static final int S2U_JOIN_NOTIFY = 22;
    private static final int S2U_SHOOT_NOTIFY = 23;
    private static final int S2U_SHOOT_CANCEL = 24;
    private static final int S2U_HIT_ACK = 25;
    private static final int S2U_TIME_LINE = 40;
    private static final int S2U_LEAVE_ACK = 26;
    private static final int S2U_LEAVE_NOTIFY = 27;
    private static final int S2U_FEATURE_UPDATE = 28;
    private static final int S2U_VAMPIRE_UPDATE = 29;
    private static final int S2U_BROADCAST = 30;
    private static final int S2U_JACKPOT_ACK = 31;
    private static final int S2U_JACKOPT_HISTORY = 32;
    private static final int S2U_FIRE_NOTIFY = 33;

    // Room Types
    private static final int ROOM_TYPE_MULTI = 0;
    private static final int ROOM_TYPE_SINGLE = 1;

    // Bullet Types
    private static final int BULLET_TYPE_NORMAL = 0;
    private static final int BULLET_TYPE_ENERGY = 1;
    private static final int BULLET_TYPE_FIRE = 2;

    public PersistentWebSocketClient(URI uri) {
        super(uri);
        this.uri = uri;

        this.addHeader("Upgrade", "websocket");
        this.addHeader("Connection", "Upgrade");
        this.addHeader("Sec-WebSocket-Version", "13");
        this.addHeader("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
        this.addHeader("Origin", "https://wbgame.bd33fgabh.com");
        this.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36");

    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        retryCount = 0;
        pingStarted = false;
        loginSent = false;
        System.out.println("[✔] Connected: " + getURI());
        sendLogin();
    }

    @Override
    public void onMessage(ByteBuffer message) {
        CommandOuterClass.Command cmd = null;
        try {
            cmd = CommandOuterClass.Command.parseFrom(message.array());
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        try {
//            if (cmd.getType() == 28){
//                List<FeatureOuterClass.Feature.FeatureItem> featureList = FeatureOuterClass.Feature.parseFrom(cmd.getData()).getListList();
//                int bulletId = 1;
//                for(FeatureOuterClass.Feature.FeatureItem feature : featureList){
//                    float x = 123.7804462553622f; // You can change this to random or calculated positions
//                    float y = -67.7804462553622f;
//
//                    System.out.println("Sending shoot/hit for fishId: " + feature.getNo());
//
//                    sendShootRequest(100, x, y, bulletId, feature.getNo(), ShootReqOuterClass.ShootType.NORMAL);
//                    Thread.sleep(50);
//                    sendHitRequest(bulletId, feature.getNo(), 1);
//                    bulletId++;
//                    Thread.sleep(200l); // Delay between requests
//                }
//
//                System.out.println("parse feature done!");
//            }
        } catch (Exception e) {
            System.out.println("parse feature failure " + e.getMessage());
        }


//        try {
//            if (cmd.getType() == S2U_SHOOT_NOTIFY) { // S2U_HIT_ACK
//                ShootAckOuterClass.ShootAck shootAck = ShootAckOuterClass.ShootAck.parseFrom(cmd.getData());
//                int fishIdHit = shootAck.getData().getFishId();
//                float remain = shootAck.getRemain();
//                float coin = shootAck.getCoin();
//                int bullet = shootAck.getDataOrBuilder().getBulletId();
//                System.out.println("received SHOOT_NOTIFY:::" + fishIdHit + "::: remain " + remain + "::: coin " + coin + "::: bullet " + bullet);
//
//                // Check if fishIdHit exists in fishInfoList
//                boolean found = GameClient2.fishInfoList.stream()
//                        .anyMatch(info -> fishIdHit == (Integer) info.get("fishId")
//                                && !GameClient2.fishDeadList.contains(fishIdHit));
//
//                if (found) {
//                    System.out.println("====> fishIdHit " + fishIdHit + " found in fishInfoList.");
//                    Random random = new Random();
//                    float x = -300 + random.nextFloat() * 100; // range: -300 to -200
//                    float y = -300 + random.nextFloat() * 100; // range: -300 to -200
//
//                    sendShootRequest(100, x, y, GameClient2.bulletId, fishIdHit, ShootReqOuterClass.ShootType.NORMAL);
//                    Thread.sleep(50L);
//                    sendHitRequest(GameClient2.bulletId, fishIdHit, 1);
//
//                    GameClient2.bulletId++;
//                    GameClient2.fishDeadList.add(fishIdHit);
//                }
//            } else {
//            }
//
//        } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
//            invalidProtocolBufferException.printStackTrace();
//        }

        try {
            if (cmd.getType() == S2U_HIT_ACK) { // S2U_HIT_ACK
                HitAckOuterClass.HitAck hitAck = HitAckOuterClass.HitAck.parseFrom(cmd.getData());

                int bulletId = hitAck.getBulletId();
                double remain = hitAck.getRemain();
                double bonus = hitAck.getBonus();

                int sizeDead = hitAck.getDeadList().size();

                System.out.println("received sizeDead " + sizeDead  + ": : bulletId " + bulletId + "::: remain " + remain + "::: bonus " + bonus );

                hitAck.getDeadList().stream().forEach(dead -> {
                    System.out.println("Dead bulletId " + bulletId  + "====> id: " + dead.getId() + "| coin: " + dead.getCoin());
                });

                List<Integer> deadFishIds = new ArrayList<>();
                for (HitAckOuterClass.HitAck.Fish fish : hitAck.getDeadList()) {
                    deadFishIds.add(fish.getId());

                    //    System.out.println("Dead fish:::: " + fish.getId());
                    GameClient2.fishDeadList.add(fish.getId());
                }

            }
        } catch (
                Exception e) {
            System.out.println("S2U_HIT_ACK failure " + e.getMessage());
        }

        try {
            if (cmd.getType() == S2U_TIME_LINE) { // S2U_HIT_ACK
                //     System.out.println("Receive TimeLine dead fish");
                TimeLine.Timeline timeline = TimeLine.Timeline.parseFrom(cmd.getData());

                if (timeline != null && timeline.getDeadCount() > 0) {
                    for (int deadFishId : timeline.getDeadList()) {
                        if (!GameClient2.fishDeadList.contains(deadFishId)) {
                            GameClient2.fishDeadList.add(deadFishId);
                            //   System.out.println("S2U_TIME_LINE Added dead fish ID: " + deadFishId);
                        }
                    }
                }
            }
        } catch (
                Exception e) {
            System.out.println("S2U_TIME_LINE failure " + e.getMessage());
        }

        try {
            if (cmd.getType() == 23) {

                ShootAckOuterClass.ShootAck shootAck = ShootAckOuterClass.ShootAck.parseFrom(message.array());
                //     System.out.printf("[←] Received SHOOT_NOTIFY (type=23): seat=%d, bulletId=%d, bet=%.2f, remain=%.2f, fishId=%d%n",
                //         shootAck.getSeat(), shootAck.getRemain());


                // Wait a bit before sending next request
                Thread.sleep(100);
            }
        } catch (
                Exception e) {
            System.out.println("ShootAck failure " + e.getMessage());
        }

        //   System.out.println("[→] Received binary message: " + message.remaining() + " bytes");
        try {
            CommandOuterClass.Command response = CommandOuterClass.Command.parseFrom(message.array());
            //   System.out.println("↪ type: " + response.getType() + ", data length: " + response.getData().size());

            // Start ping only after JOIN_ACK (e.g., type = 2)
            if (response.getType() == 0 && !pingStarted) {
                System.out.println("[✔] Received JOIN_ACK. Starting ping...");
                startPing();
                sendVipExpRequest();
                sendMailInfo();
                sendPromotionInfo();
                sendFavoriteInfo();
                sendU2SVIPInfo();
                sendU2SCardInfo();
                sendFREEZE();
                sendJoinRequest();
                sendU2S_JACKOPT_HISTORY();
                sendU2SU2S_REWARD_INFO();
                sendU2SLoginLog();
                sendU2S_FREESPIN_ALL();
                sendU2S_FREESPIN_AUTO_SEND();
                sendU2SU2S_REWARD_INFO();

                sendLoginRequest();
                sendU2SU2S_REWARD_INFO();
                sendU2S_VIP_EXP_REQ();
                sendU2S_FREESPIN_HISTROY();
                sendU2S_VIP_EXP_REQ();

                sendU2S_VIP_EXP_REQ();
                sendU2S_syn_timeline();

                sendU2S_TIMELINE_ADD();
                pingStarted = true;

            }


        } catch (
                Exception e) {
            System.err.println("[!] Failed to parse Command: " + e.getMessage());
        }

    }

    private void processNextBatch() {
        synchronized (pendingBatches) {
            if (isProcessingBatch || pendingBatches.isEmpty()) return;
            isProcessingBatch = true;
            List<Map<String, Object>> batch = pendingBatches.poll();
            new Thread(() -> {
                try {
                    int bulletId = 1; // or get from context
                    for (Map<String, Object> info : batch) {
                        Integer fishId = (Integer) info.get("fishId");
                        if (GameClient2.fishDeadList.contains(fishId)) continue;
                        float x = (Float) info.get("x");
                        float y = (Float) info.get("y");
                        int bet = 100; // or get from context
                        ShootReqOuterClass.ShootType type = ShootReqOuterClass.ShootType.NORMAL;
                        sendShootRequest(bet, x, y, bulletId, 0, type);
                        Thread.sleep(50L);
                        sendHitRequest(bulletId, fishId, 1);
                        bulletId++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    synchronized (pendingBatches) {
                        isProcessingBatch = false;
                        if (!pendingBatches.isEmpty()) {
                            processNextBatch();
                        }
                    }
                }
            }).start();
        }
    }

    private List<Map<String, Object>> getFishBatch(int deadFishId) {
        List<Map<String, Object>> fishInfoList = GameClient2.fishInfoList;
        List<Map<String, Object>> batch = new ArrayList<>();
        int startIndex = -1;
        for (int i = 0; i < fishInfoList.size(); i++) {
            Integer fishId = (Integer) fishInfoList.get(i).get("fishId");
            if (fishId >= deadFishId) {
                startIndex = i;
                break;
            }
        }
        if (startIndex != -1) {
            for (int i = startIndex; i < Math.min(startIndex + 300, fishInfoList.size()); i++) {
                batch.add(fishInfoList.get(i));
            }
        }
        return batch;
    }

    private void sendLoginRequest() {
        try {
            // Tạo LoginRequest message
            LoginRequestOuterClass.LoginRequest loginRequest = LoginRequestOuterClass.LoginRequest.newBuilder()
                    .setToken("baeb950508127ab7b712f6acaf00f281cfc8d0fc")
                    .setGame(468) // gameId của game bắn cá
                    .build();

            // Tạo Command message với type = U2S_LOGIN_REQ (0)
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(0) // U2S_LOGIN_REQ = 0
                    .setData(loginRequest.toByteString())
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);


            System.out.println("Sent login request");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendJoinRequest2() {
        try {
            JSONObject joinData = new JSONObject();
            joinData.put("theme", 1);
            joinData.put("room", ROOM_TYPE_MULTI);

            ByteBuffer buffer = createMessage(U2S_JOIN_REQ, joinData);
            this.send(buffer);

            System.out.println("Sent join request");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHit(int bulletId, int fishId, int hits) {
        try {
            JSONObject hitData = new JSONObject();
            hitData.put("bulletId", bulletId);
            hitData.put("fishId", fishId);
            hitData.put("hits", hits);

            ByteBuffer buffer = createMessage(U2S_HIT_REQ, hitData);
            this.send(buffer);

            //  System.out.println("Sent hit request");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ByteBuffer createMessage(int type, JSONObject data) {
        try {
            byte[] jsonBytes = data.toString().getBytes("UTF-8");

            ByteBuffer buffer = ByteBuffer.allocate(8 + jsonBytes.length);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            buffer.putInt(type);
            buffer.putInt(jsonBytes.length);
            buffer.put(jsonBytes);

            buffer.flip();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendShootRequest(int bet, float x, float y, int bulletId, int fishId, ShootReqOuterClass.ShootType type) {
        try {
            ShootReqOuterClass.Point point = ShootReqOuterClass.Point.newBuilder()
                    .setX(x)
                    .setY(y)
                    .build();

            ShootReqOuterClass.ShootReq shootReq = ShootReqOuterClass.ShootReq.newBuilder()
                    .setBet(bet)
                    .setPoint(point)
                    .setBulletId(bulletId)
                    .setFishId(fishId)
                    .setType(ShootReqOuterClass.BulletType.Normal)
                    .build();

            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(22) // ✅ Replace with actual U2S_SHOOT_REQ code
                    .setData(shootReq.toByteString())
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();
            this.send(buffer);
            //  System.out.printf("[↑] Sent SHOOT_REQ (type=22): bulletId=%d, fishId=%d, x=%.2f, y=%.2f, type=%s%n", bulletId, fishId, x, y, type);
        } catch (Exception e) {
            System.err.println("[!] Failed to send SHOOT_REQ: " + e.getMessage());
        }
    }


    public void sendU2S_FREESPIN_HISTROY() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_FREESPIN_HISTROY) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_FREESPIN_HISTROY (140) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_FREESPIN_AUTO_SEND 140: " + e.getMessage());
        }
    }

    public void sendU2S_syn_timeline() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_SYN_TIMELINE) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent sendU2S_syn_timeline (140) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_syn_timeline 142: " + e.getMessage());
        }
    }

    public void sendU2S_FREESPIN_AUTO_SEND() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_FREESPIN_AUTO_SEND) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();
            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            this.send(cmd.toByteArray());
            System.out.println("[↑] Sent sendU2S_FREESPIN_AUTO_SEND (144) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_FREESPIN_AUTO_SEND 144: " + e.getMessage());
        }
    }

    public void sendU2S_FREESPIN_ALL() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_FREESPIN_ALL) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_FREESPIN_ALL (143) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_FREESPIN_ALL 143: " + e.getMessage());
        }
    }

    public void sendU2S_TIMELINE_ADD() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_TIMELINE_ADD) // U2S_TIMELINE_ADD
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_REWARD_INFO (45) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_REWARD_INFO: " + e.getMessage());
        }
    }

    public void sendU2S_JACKPOT_REQ() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_JACKPOT_REQ) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_REWARD_INFO (45) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_REWARD_INFO: " + e.getMessage());
        }
    }

    public void sendU2SU2S_REWARD_INFO() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_REWARD_INFO) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_REWARD_INFO (45) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_REWARD_INFO: " + e.getMessage());
        }
    }

    public void sendFREEZE() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.FREEZE) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent sendFREEZE (115) with lang = vi-VN");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendFREEZE: " + e.getMessage());
        }
    }

    public void sendU2SLoginLog() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_NONE) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent sendU2SLoginLog (115) with lang = vi-VN");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2SLoginLog: " + e.getMessage());
        }
    }

    public void sendU2SCardInfo() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_CARD_INFO) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_MAIL_INFO (115) with lang = vi-VN");
        } catch (Exception e) {
            System.err.println("[!] Failed to send MAIL_INFO: " + e.getMessage());
        }
    }

    public void sendU2SVIPInfo() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_VIP_INFO) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_MAIL_INFO (115) with lang = vi-VN");
        } catch (Exception e) {
            System.err.println("[!] Failed to send MAIL_INFO: " + e.getMessage());
        }
    }


    public void sendU2S_JACKOPT_HISTORY() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_JACKOPT_HISTORY) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent sendU2S_JACKOPT_HISTORY (27)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_JACKOPT_HISTORY 27: " + e.getMessage());
        }
    }

    public void sendU2S_VIP_EXP_REQ() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_VIP_EXP_REQ) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent sendU2S_VIP_EXP_REQ (91)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_VIP_EXP_REQ: " + e.getMessage());
        }
    }

    public void sendMailInfo() {
        try {
            Misssion115.MissionRequest req = Misssion115.MissionRequest.newBuilder()
                    .setLang("vi-VN")  // or gs() result from JS
                    .build();

            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.MAIL_INFO) // U2S_MAIL_INFO
                    .setData(req.toByteString())
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            StringBuilder sb = new StringBuilder();
            for (byte b : cmdBytes) {
                sb.append(String.format("%02x", b));
            }
            System.out.println("[↑] Sent U2S_MAIL_INFO (115) with lang = vi-VN" + sb.toString());
        } catch (Exception e) {
            System.err.println("[!] Failed to send MAIL_INFO: " + e.getMessage());
        }
    }

    public void sendJoinRequest() {
        try {
            Join.JoinReq req = Join.JoinReq.newBuilder()
                    .setTheme(1)
                    .setRoom(Join.RoomType.Multi) // or Multi based on logic
                    .build();

            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_JOIN_REQ) // U2S_JOIN_REQ
                    .setData(req.toByteString())
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_JOIN_REQ (type 21)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send JOIN_REQ: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[→] Received text message: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[✖] Closed. Code: " + code + ", Reason: " + reason);
        stopPing();
        if (!manualClose) reconnectWithDelay();
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("[!] WebSocket error: " + ex.getMessage());
    }

    public void safeConnect() {
        manualClose = false;
        System.out.println("[…] Connecting to: " + uri);
        this.connect();
    }

    public void safeClose() {
        manualClose = true;
        this.close();
    }

    private void startPing() {
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    sendPing1();
                    //  System.out.println("Send ping ok");
                }
            }
        }, 2000, 2000);
    }

    private void startHit() {
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isOpen()) {
                    sendPing1();
                    //  System.out.println("Send ping ok");
                }

                Random random = new Random();
                float x = -300 + random.nextFloat() * 100; // range: -300 to -200
                float y = -300 + random.nextFloat() * 100; // range: -300 to -200

                sendShootRequest(100, x, y, GameClient2.bulletId, random.nextInt(2000), ShootReqOuterClass.ShootType.NORMAL);
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                sendHitRequest(GameClient2.bulletId, random.nextInt(2000), 1);

                GameClient2.bulletId++;
            }
        }, 30000, 30000);
    }

    public void sendHitRequest(int bulletId, int fishId, int hits) {
        try {
            Hit.HitReq.Builder reqBuilder = Hit.HitReq.newBuilder()
                    .setBulletId(bulletId)
                    .setHits(hits);

            // Add all fish IDs
            reqBuilder.addFishId(fishId);

            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(23) // U2S_HIT_REQ
                    .setData(reqBuilder.build().toByteString())
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            // System.out.printf("[↑] Sent HIT_REQ (type=23) bulletId=%d, fishId=%d, hits=%d%n", bulletId, fishId, hits);
        } catch (Exception e) {
            System.err.println("[!] Failed to send HIT_REQ: " + e.getMessage());
        }
    }

    public void sendPromotionInfo() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_PROMOTION_INFO) // U2S_PROMOTION_INFO
                    .setData(ByteString.EMPTY)
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_PROMOTION_INFO (type 95)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send PROMOTION_INFO: " + e.getMessage());
        }
    }

    public void sendFavoriteInfo() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_FAVORITE_INFO) // U2S_PROMOTION_INFO
                    .setData(ByteString.EMPTY)
                    .build();


            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_PROMOTION_INFO (type 95)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send PROMOTION_INFO: " + e.getMessage());
        }
    }

    private void sendVipExpRequest() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(62) // ← đúng giá trị từ xi
                    .setData(ByteString.EMPTY)
                    .build();

            byte[] cmdBytes = cmd.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(cmdBytes.length);
            buffer.put(cmdBytes);
            buffer.flip();

            this.send(buffer);
            System.out.println("[↑] Sent U2S_VIP_EXP_REQ (type 62)");
        } catch (Exception e) {
            System.err.println("[!] Failed to send VIP_EXP_REQ: " + e.getMessage());
        }
    }

    private void stopPing() {
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    private void sendPing1() {
        try {
            CommandOuterClass.Command ping = CommandOuterClass.Command.newBuilder()
                    .setType(50) // HEART_CHECK_REQ
                    .setData(ByteString.EMPTY)
                    .build();
            this.send(ping.toByteArray());
            //   System.out.println("[↑] Sent HEART_CHECK_REQ (type 50)");
        } catch (Exception e) {
            System.err.println("[!] Ping failed: " + e.getMessage());
        }
    }

    private void sendLogin() {
        try {
            FullLogin.LoginData.Browser browser = FullLogin.LoginData.Browser.newBuilder()
                    .setType("chrome")
                    .setVersion("136.0.0.0")
                    .setLanguage("")
                    .setWidth(1366)
                    .setHeight(768)
                    .setRatio(1.0f)
                    .build();

            FullLogin.LoginData login = FullLogin.LoginData.newBuilder()
                    .setOs("Windows")
                    .setLanguage("vi-VN")
                    .setBrowser(browser)
                    .setVersion("")
                    .setModel("")
                    .build();

            CommandOuterClass.Command loginCmd = CommandOuterClass.Command.newBuilder()
                    .setType(0) // U2S_LOGIN_REQ
                    .setData(login.toByteString())
                    .build();

            this.send(loginCmd.toByteArray());
            System.out.println("[↑] Sent U2S_LOGIN_REQ (type 0)");

        } catch (Exception e) {
            System.err.println("[!] Failed to send login: " + e.getMessage());
        }

    }

    private void reconnectWithDelay() {
        int delay = Math.min(5000 * (++retryCount), 30000);
        System.out.println("[↻] Reconnecting in " + delay + " ms...");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                safeConnect();
            }
        }, delay);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
