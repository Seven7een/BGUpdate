package BGUpdate;

import java.io.*;

public class CMDRunner {

    public static void runCommand(String command){

        ProcessBuilder bd = new ProcessBuilder("cmd.exe", "/C", command);
        bd.redirectErrorStream(true);


        try {

            Process ps = bd.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String currentLine;

            while (true) {
                currentLine = br.readLine();
                if (currentLine == null) {
                    break;
                }
                System.out.println(currentLine);
            }

        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
