package org.example;

import annin_protocol.CommandOuterClass;
import annin_protocol.FullLogin;
import fish.FishOuterClass;
import protocol.Hit;
import protocol.Join;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws IOException {


        FileInputStream fis = new FileInputStream("C:\\Users\\Donald Trung\\Desktop\\ddos\\webgame\\wgame\\fish2\\11");
        FishOuterClass.Script script = FishOuterClass.Script.parseFrom(fis);

        // Build a map from path id to PathData for quick lookup
        Map<Integer, FishOuterClass.PathData> pathMap = new HashMap<>();
        for (FishOuterClass.PathData path : script.getPathList()) {
            pathMap.put(path.getId(), path);
        }

        // Store results in a list of maps
        List<Map<String, Object>> fishInfoList = new ArrayList<>();

        for (FishOuterClass.GroupData group : script.getGroupList()) {
            for (FishOuterClass.Fish fish : group.getFishList()) {
                int fishNo = fish.getFish();
                int fishId = fish.getId();

                if(fishId == 1965 || fishId ==2234  || fishId ==2238 || fishId ==71|| fishId ==164||
                        fishId ==304|| fishId ==524|| fishId ==955|| fishId ==955
                        || fishId ==1109|| fishId ==1149|| fishId ==1331
                        || fishId ==1762|| fishId ==1946|| fishId ==2150|| fishId ==2265|| fishId ==2375
                    || fishId ==120|| fishId ==474){
                    System.out.println("fishId: " + fishId +"  -  fishNo: " + fishNo);
                }

            }
        }



    }

}
