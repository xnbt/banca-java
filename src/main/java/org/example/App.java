package org.example;

import annin_protocol.CommandOuterClass;
import protocol.LC;

import java.io.FileInputStream;
import java.io.IOException;

public class App {
    public static void main(String[] args) {
        byte[] binaryData = new byte[0];
        try {
            // Đọc file chứa protobuf binary
            FileInputStream fis = new FileInputStream("src/main/resources/bin/command.bin");
            CommandOuterClass.Command cmd = CommandOuterClass.Command.parseFrom(fis);

            // In ra thông tin
            System.out.println("Type: " + cmd.getType());
            binaryData = cmd.getData().toByteArray();

            System.out.println("Payload (hex): " + bytesToHex(cmd.getData().toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            FileInputStream fis = new FileInputStream("src/main/resources/bin/lc-shoot-notify.bin");

            LC.ShootNotify lc = LC.ShootNotify.parseFrom(fis);

            System.out.println("Seat: " + lc.getSeat());
            System.out.println("Coin: " + lc.getCoin());
            System.out.println("Remain: " + lc.getRemain());

            LC.ShootReq data = lc.getData();
            System.out.println("Bullet ID: " + data.getBulletId());
            System.out.println("Fish ID: " + data.getFishId());
            System.out.println("Bet: " + data.getBet());
            System.out.println("Type: " + data.getType());

            LC.Point pt = data.getPoint();
            System.out.println("Point: (" + pt.getX() + ", " + pt.getY() + ")");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
