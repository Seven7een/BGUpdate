import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        Path newP = Paths.get("C:\\Memefolder\\");
        try {
            Files.createDirectory(newP);
        } catch (FileAlreadyExistsException e) {
        } catch (IOException e) {
        }

        String allThreads;
        String allImages;
        ArrayList<String> threadNumsList = new ArrayList<>();
        ArrayList<String> imageNumbersList = new ArrayList<>();
        ArrayList<String> imageExtensionsList = new ArrayList<>();
        Matcher matcher;
        Matcher matcherExt;
        Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        Pattern patternExt = Pattern.compile("jpg|png|bmp");
        String ext = "";

        try {

            allThreads = ImagePull.sendGET();

            //System.out.println(allThreads);

            allThreads = (allThreads.replaceAll("[a-zA-Z{}:,]", ""));
            allThreads = (allThreads.replaceAll("[\"]", " "));
            allThreads = (allThreads.replaceAll("[]_\\[]", ""));
            allThreads = (allThreads.replaceAll("[\\s]+", " "));

            String[] threadNums = allThreads.split(" ");
            for(int i = 0; i < threadNums.length; i++){
                if(threadNums[i].length() == 7){threadNumsList.add(threadNums[i]);}
            };
            Collections.shuffle(threadNumsList);

            //System.out.println(threadNumsList.get(0));

            allImages = ImagePull.sendGETURL(threadNumsList.get(0));
            /*
            System.out.println(allImages);
            allImages = allImages.replaceAll("[^0-9]", " ");
            allImages = allImages.replaceAll("\\s+", " ");

            String[] imageNumbers = allImages.split(" ");
            for(int i = 0; i < imageNumbers.length; i++){
                if(imageNumbers[i].length() == 13){imageNumbersList.add(imageNumbers[i]);}
            };
            Collections.shuffle(imageNumbersList);
            */

            matcher = pattern.matcher(allImages);
            matcherExt = patternExt.matcher(allImages);

            while(matcher.find() && matcherExt.find()){
                imageNumbersList.add(allImages.substring(matcher.start(), matcher.end()));
                imageExtensionsList.add(allImages.substring(matcherExt.start(), matcherExt.end()));
            }

            int index = (int) Math.ceil(Math.random() * imageNumbersList.size()) - 1;

            ImagePull.saveIMG(imageNumbersList.get(index), imageExtensionsList.get(index));

            System.out.println(imageNumbersList.get(index));
            System.out.println(imageExtensionsList.get(index));

            ext = imageExtensionsList.get(index);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            CMDRunner.runCommand("reg add \"HKEY_CURRENT_USER\\Control Panel\\Desktop\" /v Wallpaper /t REG_SZ /d  C:\\Memefolder\\newimage." + ext + " /f && RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters 1, True");
        } catch (Exception e){
            e.printStackTrace();
        }


    }

}
