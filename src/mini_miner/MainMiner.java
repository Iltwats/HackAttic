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
        // Get response from API
        String response = new NetworkRequest().makeGetRequest();
        // Get JSON from response
        JSONObject jsonObject = new JSONObject(response);
        // Get difficulty from JSON
        String difficulty = jsonObject.get("difficulty").toString();
        // Get toBeHashed JSON with block key
        JSONObject secondJson = (JSONObject) jsonObject.get("block");
        // Get nonce from JSON and difficulty
        int nonce = getNonce(secondJson,difficulty);
        // Post nonce to API
        new NetworkRequest().makePostRequest(nonce);

    }

    // Get Nonce from JSON
    public static int getNonce(JSONObject json, String difficulty) throws NoSuchAlgorithmException {
        int nonce = -1;
        int diff = Integer.parseInt(difficulty);
        String bits = "0";
        // Convert difficulty to bits
        String expected = bits.repeat((int) Math.ceil(diff / 4));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        while (true) {
            nonce++;
            // update nonce in JSON
            json.put("nonce", nonce);
            // serialize JSON to string
            String toBeHashed = json.toString();
            // hash string
            byte[] hash = digest.digest(toBeHashed.getBytes(StandardCharsets.UTF_8));
            String encoded = bytesToHex(hash);
            // check if hash starts with expected
            if (encoded.startsWith(expected)) {
                System.out.println(encoded);
                break;
            }
        }
        return nonce;
    }

    // Convert byte array to hex
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }




    // ---Below Functions has no role in the problem, its just for reference---
    // Sort JSON by Key
    private static JSONObject getSortedJson(JSONObject secondJson) {
        JSONObject sortedJson = new JSONObject();
        JSONArray jsonArray = (JSONArray) secondJson.get("data");
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray innerArray = (JSONArray) jsonArray.get(i);
            String key = innerArray.get(0).toString();
            int value = (int) innerArray.get(1);
            map.put(key, value);
        }
        map = sortByKey(map);
        List<List<Object>> lists = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            List<Object> list = new ArrayList<>();
            list.add(key);
            list.add(value);
            lists.add(list);
        }
        sortedJson.put("data", lists);
        sortedJson.put("nonce", secondJson.get("nonce"));
        return sortedJson;
    }
    // Sort map by Key
    public static HashMap<String, Integer> sortByKey(Map<String, Integer> map) {
        // Copy all data from hashMap into TreeMap
        TreeMap<String, Integer> sorted = new TreeMap<>(map);
        return new HashMap<>(sorted);
    }


}
