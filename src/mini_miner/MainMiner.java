package mini_miner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MainMiner {
    public static void main(String[] args) throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        String response = new NetworkRequest().makeGetRequest();
        //System.out.println(response);
        JSONObject jsonObject = new JSONObject(response);
        String difficulty = jsonObject.get("difficulty").toString();
        //System.out.println(difficulty);
        JSONObject secondJson = (JSONObject) jsonObject.get("block");
        //Object obj = secondJson.get("data").toString();
        //JSONArray jsonArray = new JSONArray(obj.toString());

        System.out.println(secondJson);
        JSONObject sortedJson = getSortedJson(secondJson);
        System.out.println(sortedJson);
        //getNonce(jsonArray,difficulty);
    }

    private static JSONObject getSortedJson(JSONObject secondJson) {
        JSONObject sortedJson = new JSONObject();
        JSONArray jsonArray = (JSONArray) secondJson.get("data");
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray innerArray = (JSONArray) jsonArray.get(i);
            String key = innerArray.get(0).toString();
            int value = (int) innerArray.get(1);
            map.put(key,value);
        }
        map = sortByKey(map);
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            sortedJson.put(key,value);
        }
        return sortedJson;
    }

    public static int getNonce(JSONArray jsonArray,String difficulty) throws NoSuchAlgorithmException {
        int nonce = -1;
        int diff = Integer.parseInt(difficulty);
        String bits = "0";
        String expected = bits.repeat((int) Math.ceil(diff/4));
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray innerArray = (JSONArray) jsonArray.get(i);
            String key = innerArray.get(0).toString();
            int value = (int) innerArray.get(1);
            map.put(key,value);
        }
        map = sortByKey(map);
        StringBuilder originalString = new StringBuilder();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            originalString.append(key).append(value);
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
         while (nonce<10) {
             nonce++;
             System.out.println(originalString.toString());
             byte[] hash = digest.digest(originalString.toString().getBytes(StandardCharsets.UTF_8));
             String encoded = Base64.getEncoder().encodeToString(hash);
             if(encoded.startsWith(expected)){
                 System.out.println("nonce: "+nonce);
                 break;
             }
             System.out.println(encoded);
        }
         return nonce;
    }

    // Function to sort map by Key
    public static HashMap<String, Integer> sortByKey(Map<String, Integer> map)
    {
        // Copy all data from hashMap into TreeMap
        TreeMap<String, Integer> sorted = new TreeMap<>(map);
        return new HashMap<>(sorted);
    }


}
