package org.example;

import annin_protocol.CommandOuterClass;
import annin_protocol.FullLogin;
import protocol.Hit;
import protocol.Join;

public class Test {
    public static void main(String[] args) {


        Hit.HitReq hit = Hit.HitReq.newBuilder().addFishId(1405).setBulletId(1).setHits(1).build();
        CommandOuterClass.Command cmd = CommandOuterClass.Command.newBuilder()
                .setType(23) // ✅ Replace with actual U2S_SHOOT_REQ code
                .setData(hit.toByteString())
                .build();
        for (byte b : cmd.toByteArray()) {
            System.out.printf("%02x", b);
        }



    }

}
