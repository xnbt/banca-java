package com.game.protocol;

import com.game.protocol.message.*;

public class Example {
    public static void main(String[] args) {

        Point point = new Point();
        point.setX(-68.91743119266062);
        point.setY(-83.18042813455654);
        ShootReq req = new ShootReq();
        req.setBet(100);
        req.setPoint(point);
        req.setType(BulletType.Normal);  // This now works without conflict
        req.setFishId(0);
        req.setBulletId(1);

        byte[] binary = req.toBinary(null);

        System.out.println("1");
    }
}
