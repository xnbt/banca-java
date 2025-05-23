package org.example;
import annin_protocol.CommandOuterClass;
import annin_protocol.FullLogin;
import annin_protocol.Misssion115;
import com.google.protobuf.ByteString;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import protocol.Hit;
import protocol.Join;
import protocol.LC;
import protocol.ShootReqOuterClass;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public class PersistentWebSocketClient extends WebSocketClient {

    private final URI uri;
    private int retryCount = 0;
    private boolean manualClose = false;
    private boolean pingStarted = false;
    private boolean loginSent = false;
    private Timer pingTimer;

    public PersistentWebSocketClient(URI uri) {
        super(uri);
        this.uri = uri;
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
        System.out.println("[→] Received binary message: " + message.remaining() + " bytes");
        try {
            CommandOuterClass.Command response = CommandOuterClass.Command.parseFrom(message.array());
            System.out.println("↪ type: " + response.getType() + ", data length: " + response.getData().size());

            // Start ping only after JOIN_ACK (e.g., type = 2)
            if (response.getType() == 0 && !pingStarted) {
                System.out.println("[✔] Received JOIN_ACK. Starting ping...");
                startPing();
                sendVipExpRequest();
                sendMailInfo();
                sendPromotionInfo();
                sendFavoriteInfo();
                sendU2SCardInfo();
                sendU2SLoginLog();
                sendU2SVIPInfo();
                sendU2SU2S_REWARD_INFO();
                sendU2S_VIP_EXP_REQ();
                sendU2S_JACKOPT_HISTORY();
                sendU2S_VIP_EXP_REQ();
                sendU2S_VIP_EXP_REQ();
                sendU2S_JACKPOT_REQ();
                sendU2S_FREESPIN_ALL();
                sendU2S_FREESPIN_AUTO_SEND();
                sendU2S_FREESPIN_HISTROY();
                pingStarted = true;

            }

            sendHitRequest(1024,1675,1);

            sendShootRequest(100,100,100,1,1675, ShootReqOuterClass.ShootType.NORMAL);

        } catch (Exception e) {
            System.err.println("[!] Failed to parse Command: " + e.getMessage());
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
                    .setType(type)
                    .build();

            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(22) // ✅ Replace with actual U2S_SHOOT_REQ code
                    .setData(shootReq.toByteString())
                    .build();

            this.send(cmd.toByteArray());
            System.out.printf("[↑] Sent SHOOT_REQ (type=22): bulletId=%d, fishId=%d, x=%.2f, y=%.2f, type=%s%n",
                    bulletId, fishId, x, y, type);
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

            this.send(cmd.toByteArray());
            System.out.println("[↑] Sent U2S_FREESPIN_HISTROY (140) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send sendU2S_FREESPIN_AUTO_SEND 140: " + e.getMessage());
        }
    }

    public void sendU2S_FREESPIN_AUTO_SEND() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_FREESPIN_AUTO_SEND) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

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

            this.send(cmd.toByteArray());
            System.out.println("[↑] Sent U2S_FREESPIN_ALL (143) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_FREESPIN_ALL 143: " + e.getMessage());
        }
    }

    public void sendU2S_JACKPOT_REQ() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_JACKPOT_REQ) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
            System.out.println("[↑] Sent U2S_REWARD_INFO (45) ");
        } catch (Exception e) {
            System.err.println("[!] Failed to send U2S_REWARD_INFO: " + e.getMessage());
        }
    }

    public void sendU2SLoginLog() {
        try {
            CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                    .setType(REQUEST.U2S_NONE) // U2S_MAIL_INFO
                    .setData(ByteString.EMPTY)
                    .build();

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
            System.out.println("[↑] Sent U2S_MAIL_INFO (115) with lang = vi-VN");
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

            this.send(cmd.toByteArray());
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
                    sendU2S_VIP_EXP_REQ();
                    System.out.println("Send ping ok");
                }
            }
        }, 5000, 5000);
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

            this.send(cmd.toByteArray());
            System.out.printf("[↑] Sent HIT_REQ (type=23) bulletId=%d, fishId=%d, hits=%d%n", bulletId, fishId, hits);
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

            this.send(cmd.toByteArray());
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

            this.send(cmd.toByteArray());
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
            this.send(cmd.toByteArray());
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
            System.out.println("[↑] Sent HEART_CHECK_REQ (type 50)");
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
                    .setRatio(1.0)
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
