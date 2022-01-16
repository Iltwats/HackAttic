package readingQR;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainQR {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("------Making Network Request for JSON------");
        String result = new NetworkRequest().makeGetRequest();
        JSONObject object = new JSONObject(result);

        System.out.println("------Parsing JSON------");
        String image_url = object.get("image_url").toString();
        System.out.println(image_url);

        System.out.println("------Downloading Image------");
        URL url = new URL(image_url);
        Image image = ImageIO.read(url);

        System.out.println("------Converting Image to BufferedImage------");
        BufferedImage bufferedImage = new ImageUtils().toBufferedImage(image);

        System.out.println("------Extracting content from QR ------");
        String qrText = new QRCode().readQRCode(bufferedImage);
        System.out.println(qrText);

        System.out.println("------Making post request with qr code content------");
        new NetworkRequest().makePostRequest(qrText);
    }


}
