package help_me_unpack;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

public class MainUnpack {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String response = new NetworkRequest().makeGetRequest();

        JSONObject jsonObject = new JSONObject(response);
        String bytes = jsonObject.getString("bytes");
        System.out.println(bytes);
        //byte[] dec = "qMmPi27odLMGPAAAt6ZIQowHDC1OYnJAQHJiTi0MB4w=";
        byte[] decodedBytes = Base64.getDecoder().decode((String) jsonObject.get("bytes"));
        System.out.println(new String(decodedBytes));
        String decodedString = new String(decodedBytes);
        System.out.println(decodedString);

    }

//    byte[] bytes = "Hello, World!".getBytes("UTF-8");
//    String encoded = Base64.getEncoder().encodeToString(bytes);
//        System.out.println(encoded);
//    byte[] decodedBytes = Base64.getDecoder().decode(encoded);
//    String decodedString = new String(decodedBytes);
//        System.out.println(decodedString);
}
