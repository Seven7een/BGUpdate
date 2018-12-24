package BGUpdate;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

class ImagePull{

    static JSONArray sendGET(String board) throws Exception{
        String returnString = "";

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://a.4cdn.org" + board + "threads.json";
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
        return (new JSONArray(returnString));
    }

    static JSONObject sendGETURL(String inURL, String board) throws Exception{
        String returnString = "";

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://a.4cdn.org" + board + "thread/" + inURL + ".json";
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
        return (new JSONObject(returnString));
    }

    static void saveIMG(String inURL, String board, String ext) throws Exception{

        HttpURLConnection.setFollowRedirects(true); // defaults to true

        String url = "https://i.4cdn.org" + board + inURL + "." + ext;
        URL request_url = new URL(url);
        HttpURLConnection http_conn = (HttpURLConnection)request_url.openConnection();
        http_conn.setConnectTimeout(100000);
        http_conn.setReadTimeout(100000);
        http_conn.setInstanceFollowRedirects(true);
        String response = http_conn.getResponseMessage();

        if(response.equals("OK")) {
            try {
                InputStream in = http_conn.getInputStream();
                Files.copy(in, Paths.get("./images/newimage." + ext), StandardCopyOption.REPLACE_EXISTING);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
