package password_hashing;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainHashing {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String response = new NetworkRequest().makeGetRequest();
        JSONObject jsonObject = new JSONObject(response);
        System.out.println(jsonObject.toString());
    }
}
