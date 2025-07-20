package org.example;

import annin_protocol.CommandOuterClass;
import fish.FishOuterClass;
import fish.TrungOuterClass;
import protocol.LC;

import javax.swing.plaf.RootPaneUI;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        byte[] binaryData = new byte[0];
        try {
            // Đọc file chứa protobuf binary
            FileInputStream fis = new FileInputStream("C:\\Users\\Donald Trung\\Desktop\\ddos\\webgame\\wgame\\fish2\\11");
            FishOuterClass.Script script = FishOuterClass.Script.parseFrom(fis);

            // Build a map from path id to PathData for quick lookup
            Map<Integer, FishOuterClass.PathData> pathMap = new HashMap<>();
            for (FishOuterClass.PathData path : script.getPathList()) {
                pathMap.put(path.getId(), path);
            }

            for (FishOuterClass.GroupData group : script.getGroupList()) {
                for (FishOuterClass.Fish fish : group.getFishList()) {
                    int fishNo = fish.getFish();
                    if (fishNo == 1 || fishNo == 2) {
                        int fishId = fish.getId();
                        int pathId = fish.getPath();
                        double x = Double.NaN, y = Double.NaN;

                        // Get the first point of the path, if available
                        FishOuterClass.PathData pathData = pathMap.get(pathId);
                        if (pathData != null && pathData.getPointCount() > 0) {
                            FishOuterClass.Point point = pathData.getPoint(0);
                            x = point.getX();
                            y = point.getY();
                        }

                        System.out.printf("fishId: %d, x: %f, y: %f, fishNo: %d%n", fishId, x, y, fishNo);
                    }
                }
            }

            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
