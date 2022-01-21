package password_hashing;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.json.JSONObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Charsets.UTF_8;

public class MainHashing {
    public static void main(String[] args) throws ExecutionException, InterruptedException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException {
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
        byte[] decoded = BaseEncoding.base64().decode(salt);
        String sha256 = computeSHA256(password);
        String hmac_sha256 = computeHMAC_SHA256(password, decoded);
        String pbkdf2 = computePBKDF2(password, decoded, pbkdf2Rounds, pbkdf2Hash);
        System.out.println(hmac_sha256);
        System.out.println(sha256);
        JSONObject outputJSON = new JSONObject();
        outputJSON.put("sha256", sha256);
        outputJSON.put("hmac", hmac_sha256);
        outputJSON.put("pbkdf2", pbkdf2);
        new NetworkRequest().makePostRequest(outputJSON);
    }

    private static String computePBKDF2(String password, byte[] decoded, Integer pbkdf2Rounds, String pbkdf2Hash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), pbkdf2Hash.getBytes(StandardCharsets.UTF_8), pbkdf2Rounds, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] hash = skf.generateSecret(spec).getEncoded();
        return bytesToHex(hash);
    }

    private static String computeHMAC_SHA256(String password, byte[] decodedSalt) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        return Hashing.hmacSha256(decodedSalt)
                .newHasher()
                .putString(password, UTF_8)
                .hash()
                .toString();
    }

    private static String computeSHA256(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
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
