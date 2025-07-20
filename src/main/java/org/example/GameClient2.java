package org.example;

import fish.FishOuterClass;
import org.json.JSONObject;
import protocol.ShootReqOuterClass;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class GameClient2 {
    public static void main(String[] args) throws Exception {
        func3();
    }

   public static int bulletId = 1;


    public static Set<Integer> fishDeadList = new HashSet<>();

    public static List<Map<String, Object>> fishInfoList = new ArrayList<>();


    public static void func3() throws IOException, InterruptedException {
        System.currentTimeMillis();
        //    String csvFile = "fish_shoot_summary_" + System.currentTimeMillis() + "_zzz.csv";
        //  boolean fileExists = new File(csvFile).exists();
//        PrintWriter csvWriter = new PrintWriter(new FileWriter(csvFile, true));
//        if (!fileExists) {
//            csvWriter.println("fishId,fishNo,balance_before,balance_after,difference,elapsed_seconds");
//            csvWriter.flush();
//        }

        String token = "ab000ac8becb4d768973bc133a560448bbe24052"; // token lấy từ login
        String serverUrl = "wss://fish.bd33fgabh.com/v15/ws/" + token + "?r=0";

        PersistentWebSocketClient socket = null;
        try {
            socket = new PersistentWebSocketClient(new URI(serverUrl));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.safeConnect();

        try {
            Thread.sleep(17000L); // Wait for connection to stabilize
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int bet = 100;
        ShootReqOuterClass.ShootType type = ShootReqOuterClass.ShootType.NORMAL;



        FileInputStream fis = new FileInputStream("C:\\Users\\Donald Trung\\Desktop\\ddos\\webgame\\wgame\\fish2\\11");
        FishOuterClass.Script script = FishOuterClass.Script.parseFrom(fis);

        // Build a map from path id to PathData for quick lookup
        Map<Integer, FishOuterClass.PathData> pathMap = new HashMap<>();
        for (FishOuterClass.PathData path : script.getPathList()) {
            pathMap.put(path.getId(), path);
        }

        // Store results in a list of maps

        for (FishOuterClass.GroupData group : script.getGroupList()) {
            for (FishOuterClass.Fish fish : group.getFishList()) {
                int fishNo = fish.getFish();
                int fishId = fish.getId();
                if (fishNo <=3  && fishId <= 3500) {
                    int pathId = fish.getPath();
                    float x = Float.NaN, y = Float.NaN;

                    // Get the first point of the path, if available
                    FishOuterClass.PathData pathData = pathMap.get(pathId);
                    if (pathData != null && pathData.getPointCount() > 0) {
                        FishOuterClass.Point point = pathData.getPoint(0);
                        x = point.getX();
                        y = point.getY();
                    }

                    // Put info into a map
                    Map<String, Object> info = new HashMap<>();
                    info.put("fishId", fishId);
                    info.put("x", x);
                    info.put("y", y);
                    info.put("fishNo", fishNo);
                    fishInfoList.add(info);

               //     fishInfoList.sort(Comparator.comparing(info1 -> (Integer) info1.get("fishId")));

                }
            }
        }

        // banCaScaner(socket);

        Random rd = new Random();
        int min = 3;
        int max = 12;
        // Iterate and print

        // for (int i = 1; i <= 20; i++) {
        long startTime = System.currentTimeMillis();
        for (Map<String, Object> info : fishInfoList) {
            double balanceBefore = 0;
            double balanceAfter = 0;
            try {
                balanceBefore = getAccountBalance();
            } catch (Exception e) {
                System.err.println("[!] Failed to get balance before shoot: " + e.getMessage());
            }

            Integer fishId = (Integer) info.get("fishId");
            Integer fishNo = (Integer) info.get("fishNo");
            if (fishDeadList.contains(fishId)) {
            //    System.out.println("dead fish decrease balance, fishId = " + fishId + "--- fishNo = " + fishNo);

//                Scanner scanner = new Scanner(System.in);
//                int fishId2 = 0;
//                do {
//                    bulletId++;
//                    fishId2 = scanner.nextInt();
//                    System.out.println("dead fishId2 = " + fishId2);
//                    socket.sendShootRequest(100, -130.51436863039382f, 141.4878493100918f,
//                            bulletId, fishId2, ShootReqOuterClass.ShootType.NORMAL);
//                    Thread.sleep(30l);
//                    socket.sendHitRequest(bulletId, fishId2, 1);
//                } while (fishId2 != -1);

                continue;
            }

            int z = rd.nextInt(max - min + 1) + min;
            Float x = (Float) (info.get("x"));
            if (x < 0) {
                x = x + z;
            } else {
                x = x - z;
            }
            Float y = (Float) info.get("y");
            if (y < 0) {
                y = y + z;
            } else {
                y = y - z;
            }


//            socket.sendShootRequest(bet, x, y, bulletId, 0, type);
//            Thread.sleep(20l);
//            socket.sendHitRequest(bulletId, fishId, 1);
//            bulletId++;
//
//            try {
//                Thread.sleep(300l); // Wait a bit for balance to update
//                balanceAfter = getAccountBalance();
//            } catch (Exception e) {
//                System.err.println("[!] Failed to get balance after shoot: " + e.getMessage());
//            }

            double diff = balanceAfter - balanceBefore;
          //  if (diff < 0) {
                System.out.println(" decrease balance, fishId = " + fishId + "--- fishNo = " + fishNo);

           //     Scanner scanner = new Scanner(System.in);
            //    int fishId3 = 0;
                int numOfShot = 0;
            //    do {
                    numOfShot++;
                    bulletId++;
                //    fishId3 = scanner.nextInt();

                    System.out.println("fishId2 = " + fishId + " numOfShot = " + numOfShot);
                    socket.sendShootRequest(100, -130.51436863039382f, 141.4878493100918f,
                            bulletId, fishId, ShootReqOuterClass.ShootType.NORMAL);
                    Thread.sleep(30l);
                    socket.sendHitRequest(bulletId, fishId, 1);
                  //  balanceBefore = balanceAfter;

                //    Thread.sleep(3000l);
                  //  balanceAfter  = getAccountBalance();
                 //   diff = balanceAfter - balanceBefore;
            Thread.sleep(300l);
             //       System.out.println("diff after = " + diff + " - numOfShot " + numOfShot);
             //   } while ( numOfShot < 5);

          //  }
            //   long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            //    csvWriter.printf("%d,%d,%.2f,%.2f,%.2f,%d\n", fishId,fishNo, balanceBefore, balanceAfter, diff, elapsed);
            //  csvWriter.flush();

            //System.out.println("fishId: " + fishId + "---time in: " + elapsed);
            Thread.sleep(150l);
        }
        //  csvWriter.close();
        System.exit(0);
    }


    public static void banCaScaner(PersistentWebSocketClient socket) throws InterruptedException {
        int bulletId = 1;
        while (true) {
            System.out.println("input fishId: ");
            Scanner scanner = new Scanner(System.in);
            int fishId = scanner.nextInt();
            System.out.println("fishId = " + fishId);
            socket.sendShootRequest(100, -130.51436863039382f, 141.4878493100918f,
                    bulletId, 0, ShootReqOuterClass.ShootType.NORMAL);
            Thread.sleep(30l);
            socket.sendHitRequest(bulletId, fishId, 1);
            bulletId++;
        }
    }


    public static double getAccountBalance()  {
//        try {
//            String url = "https://api.hycxsq.com/api/front/user/money";
//            URL obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            // Set request method
//            con.setRequestMethod("GET");
//
//            // Set headers
//            con.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
//            con.setRequestProperty("accept-language", "en-US,en;q=0.9");
//            con.setRequestProperty("access-control-max-age", "60000");
//            con.setRequestProperty("origin", "https://gggg.hycxsq.com");
//            con.setRequestProperty("priority", "u=1, i");
//            con.setRequestProperty("referer", "https://gggg.hycxsq.com/");
//            con.setRequestProperty("sec-ch-ua", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\"");
//            con.setRequestProperty("sec-ch-ua-mobile", "?0");
//            con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
//            con.setRequestProperty("sec-fetch-dest", "empty");
//            con.setRequestProperty("sec-fetch-mode", "cors");
//            con.setRequestProperty("sec-fetch-site", "same-site");
//            con.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36");
//            con.setRequestProperty("x-key", "VA8X5MTc1MjUwOTI3Njc3MQ==");
//            con.setRequestProperty("x-lang", "vi");
//            con.setRequestProperty("x-session-token", "tqVvZyYMg8y7ZK2dM7Nd/AzXSeYtHHiVNGQHwYJGc8I=");
//            con.setRequestProperty("x-versions", "v2025.7.5.16");
//            con.setRequestProperty("xc-tk", "9ebd9a0268a1f5af8e885fb986fa0620");
//
//            // Read response
//            int responseCode = con.getResponseCode();
//            if (responseCode != 200) {
//                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
//            }
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            // Parse JSON and extract balance
//            JSONObject json = new JSONObject(response.toString());
//            double balance = json.getDouble("t");
//            return balance;
//        }catch (Exception e){
//            System.out.println("get balnce excep");
//        }

        return 0;
    }

}
