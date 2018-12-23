import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.net.HttpURLConnection;
import java.nio.file.*;

public class ImagePull{

    static String sendGET() throws Exception{
        String returnString = "";

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://a.4cdn.org/wg/threads.json";
        URL request_url = new URL(url);
        HttpURLConnection http_conn = (HttpURLConnection)request_url.openConnection();
        http_conn.setConnectTimeout(100000);
        http_conn.setReadTimeout(100000);
        http_conn.setInstanceFollowRedirects(true);
        String response = http_conn.getResponseMessage();

        if(response.equals("OK")) {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
                String currentLine;

                while (true) {
                    currentLine = br.readLine();
                    if (currentLine == null) {
                        break;
                    }
                    returnString += currentLine;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Big yikes");
            }
        }
        return returnString;
    }

    static String sendGETURL(String inURL) throws Exception{
        String returnString = "";

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://a.4cdn.org/wg/thread/" + inURL + ".json";
        URL request_url = new URL(url);
        HttpURLConnection http_conn = (HttpURLConnection)request_url.openConnection();
        http_conn.setConnectTimeout(100000);
        http_conn.setReadTimeout(100000);
        http_conn.setInstanceFollowRedirects(true);
        String response = http_conn.getResponseMessage();

        if(response.equals("OK")) {
            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
                String currentLine;

                while (true) {
                    currentLine = br.readLine();
                    if (currentLine == null) {
                        break;
                    }
                    returnString += currentLine;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Lesser yikes");
            }
        }
        return returnString;
    }

    static String saveIMG(String inURL, String ext) throws Exception{
        String returnString = "";

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://i.4cdn.org/wg/" + inURL + "." + ext;
        URL request_url = new URL(url);
        HttpURLConnection http_conn = (HttpURLConnection)request_url.openConnection();
        http_conn.setConnectTimeout(100000);
        http_conn.setReadTimeout(100000);
        http_conn.setInstanceFollowRedirects(true);
        String response = http_conn.getResponseMessage();

        if(response.equals("OK")) {
            try {
                InputStream in = http_conn.getInputStream();
                Files.copy(in, Paths.get("C:\\Memefolder\\newimage." + ext), StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return returnString;
    }



}
