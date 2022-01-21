package password_hashing;

import mini_miner.MainMiner;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainHashing {
    public static void main(String[] args) throws ExecutionException, InterruptedException, NoSuchAlgorithmException {
        String response = new NetworkRequest().makeGetRequest();
        JSONObject jsonObject = new JSONObject(response);
        String password = jsonObject.getString("password");
        String salt = jsonObject.getString("salt");
        JSONObject jsonObject1 = (JSONObject) jsonObject.get("pbkdf2");
        JSONObject jsonObject2 = (JSONObject) jsonObject.get("scrypt");
        System.out.println(jsonObject.toString());
        Integer pbkdf2Rounds = jsonObject1.getInt("rounds");
        String pbkdf2Hash = jsonObject1.getString("hash");
        Integer scryptN = jsonObject2.getInt("N");
        Integer scryptR = jsonObject2.getInt("r");
        Integer scryptP = jsonObject2.getInt("p");
        Integer scryptBuflen = jsonObject2.getInt("buflen");
        String scryptControl = jsonObject2.getString("_control");
        String decodedSalt = bytesToHex(Base64.getDecoder().decode(salt));
        String sha256 = computeSHA256(password, decodedSalt, pbkdf2Rounds);
        String hmac_sha256 = computeHMAC_SHA256(password, decodedSalt, pbkdf2Rounds);
        
        System.out.println(sha256);
        JSONObject outputJSON = new JSONObject();
        outputJSON.put("sha256", sha256);
    }

    private static String computeHMAC_SHA256(String password, String decodedSalt, Integer pbkdf2Rounds) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
    }

    private static String computeSHA256(String password, String decodedSalt, Integer pbkdf2Rounds) throws NoSuchAlgorithmException {
        String sha256 = password+decodedSalt;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(sha256.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
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
}
