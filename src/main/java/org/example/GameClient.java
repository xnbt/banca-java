package org.example;

import annin_protocol.CommandOuterClass;
import protocol.Hit;
import protocol.ShootReq2OuterClass;
import protocol.ShootReqOuterClass;

import java.net.URI;
import java.nio.ByteBuffer;

public class GameClient {
    public static void main(String[] args) throws Exception {
        // Thiết lập logging
        String token = "3d1f2b95b4706baf95af08beeaee553fbee47da3"; // token lấy từ login
        String serverUrl = "wss://fish.bd33fgabh.com/v15/ws/" + token + "?r=0";


        PersistentWebSocketClient socket = new PersistentWebSocketClient(new URI(serverUrl));
        socket.safeConnect();

        Thread.sleep(10000l);

        try {
                for(int i =1675 ; i < 1726 ;i++) {

                    ShootReqOuterClass.Point point = ShootReqOuterClass.Point.newBuilder()
                            .setX(158.7780979827089f)
                            .setY(-41.72910662824205f)
                            .build();

                    ShootReqOuterClass.ShootReq shootReq = ShootReqOuterClass.ShootReq.newBuilder()
                            .setBet(100)
                            .setPoint(point)
                            .setBulletId(i)
                            .setFishId(i)
                            .setType(ShootReqOuterClass.BulletType.Normal)
                            .build();


                    CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                            .setType(22) // ✅ Replace with actual U2S_SHOOT_REQ code
                            .setData(shootReq.toByteString())
                            .build();
                    socket.send(cmd.toByteArray());


                    Hit.HitReq hit = Hit.HitReq.newBuilder()
                            .addFishId(i)
                            .setBulletId(i).setHits(1).build();
                    CommandOuterClass.Command cmdHit = CommandOuterClass.Command.newBuilder()
                            .setType(23) // ✅ Replace with actual U2S_SHOOT_REQ code
                            .setData(hit.toByteString())
                            .build();

                    socket.send(cmdHit.toByteArray());
                }

            for(int i =407 ; i < 477 ;i++) {

                    ShootReqOuterClass.Point point = ShootReqOuterClass.Point.newBuilder()
                            .setX(158.7780979827089f)
                            .setY(-41.72910662824205f)
                            .build();

                    ShootReqOuterClass.ShootReq shootReq = ShootReqOuterClass.ShootReq.newBuilder()
                            .setBet(100)
                            .setPoint(point)
                            .setBulletId(i)
                            .setFishId(i)
                            .setType(ShootReqOuterClass.BulletType.Normal)
                            .build();


                    CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                            .setType(22) // ✅ Replace with actual U2S_SHOOT_REQ code
                            .setData(shootReq.toByteString())
                            .build();
                    socket.send(cmd.toByteArray());


                    Hit.HitReq hit = Hit.HitReq.newBuilder()
                            .addFishId(i)
                            .setBulletId(i).setHits(1).build();
                    CommandOuterClass.Command cmdHit = CommandOuterClass.Command.newBuilder()
                            .setType(23) // ✅ Replace with actual U2S_SHOOT_REQ code
                            .setData(hit.toByteString())
                            .build();

                    socket.send(cmdHit.toByteArray());
                 //   k++;
                }
        } catch (Exception e) {
            System.err.println("Error in main: " + e.getMessage());
            e.printStackTrace();
        }

        while (true) {
            Thread.sleep(10000);
        }
    }
}
