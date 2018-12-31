package BGUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

class ImageGUI {

    private JFrame mainFrame;
    private ImagePanel pictureFrame;
    private String currentIMGName;
    private String currentIMGext;
    GalleryPanel[] historyPanels;

    ImageGUI(){

        init();

    }

    private void init(){

        this.mainFrame = new JFrame("4Chan Image Grabber");
        mainFrame.setSize(1024, 768);
        Container contentPane = mainFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        Container options = new Container();
        options.setLayout(new FlowLayout());

        JComboBox<String> board = new JComboBox<>();
        JButton getImage = new JButton("Get new image");
        JButton saveImage = new JButton("Save");

        board.addItem("/wg/");
        board.addItem("/hr/");
        board.addItem("/w/");

        JButton addBoard = new JButton("Add board");

        options.add(board);
        options.add(addBoard);
        options.add(getImage);
        options.add(saveImage);

        pictureFrame = new ImagePanel();

        JButton setBackground = new JButton("Set background (Ubuntu)");

        options.add(setBackground);

        Container imageInfo = new Container();
        imageInfo.setLayout(new FlowLayout());

        JLabel dimensions = new JLabel();
        JLabel format = new JLabel();

        imageInfo.add(dimensions);
        imageInfo.add(format);

        contentPane.add(options, BorderLayout.NORTH);
        contentPane.add(pictureFrame, BorderLayout.CENTER);

        historyPanels = new GalleryPanel[3];

        Container bottomPanel = new Container();
        bottomPanel.setLayout(new BorderLayout());

        Container gallery = new Container();
        gallery.setLayout(new GridLayout(1, 3));

        for(int i = 0; i < 3; i++){
            historyPanels[i] = new GalleryPanel();
            //historyPanels[i].adjustSize((int) (mainFrame.getWidth() / 3.5), (int)(mainFrame.getHeight() / 3.5));
            gallery.add(historyPanels[i]);
        }

        bottomPanel.add(gallery, BorderLayout.SOUTH);
        bottomPanel.add(imageInfo, BorderLayout.NORTH);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);


        //listeners

        addBoard.addActionListener(e -> board.addItem(JOptionPane.showInputDialog("Enter board with / (e.g. /wg/) : ")));

        setBackground.addActionListener(e -> {

            try {

                String cmd= "./changeBG.sh" + currentIMGext;
                System.out.println(cmd);
                ProcessBuilder bd = new ProcessBuilder("./changeBG.sh", currentIMGext);

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

            } catch (IOException ioex){
                JOptionPane.showMessageDialog(pictureFrame, "Please that changeBG.sh is in the same directory as the jar and is executable.");
            }
            catch (Exception e2){
                e2.printStackTrace();
            }
        });

        saveImage.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();

            fc.setCurrentDirectory(new File("./"));
            int retrieval = fc.showSaveDialog(mainFrame);

            if (retrieval == JFileChooser.APPROVE_OPTION) {
                try {

                    Path in = Paths.get("./images/newimage." + currentIMGext);
                    Path out = Paths.get(fc.getSelectedFile().getAbsolutePath() + "." + currentIMGext);

                    Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        getImage.addActionListener(e -> {

            Path newP = Paths.get("./images");
            try {
                Files.createDirectory(newP);
            } catch (Exception ignored){
            }

            JSONArray allThreads;
            JSONObject allImages;
            ArrayList<String> threadNumsList = new ArrayList<>();
            ArrayList<String> imageNumbersList = new ArrayList<>();
            ArrayList<String> imageExtensionsList = new ArrayList<>();
            String ext;



            try {

                //get and add all thread numbers on a board to array
                allThreads = ImagePull.sendGET(Objects.requireNonNull(board.getSelectedItem()).toString());
                for (int i = 0; i < allThreads.length(); i++)
                {
                    JSONObject allThreadsJSONObject = allThreads.getJSONObject(i);
                    JSONArray threadArr = allThreadsJSONObject.getJSONArray("threads");
                    for(int j = 0; j < threadArr.length(); j++){
                        JSONObject currentThread = threadArr.getJSONObject(j);
                        threadNumsList.add(currentThread.get("no").toString());
                    }
                }

                //shuffle all threads to pick random one
                Collections.shuffle(threadNumsList);

                //get all image numbers from a given thread

                allImages = ImagePull.sendGETURL(threadNumsList.get(0), board.getSelectedItem().toString());
                JSONArray currentPosts = allImages.getJSONArray("posts");

                for(int i = 0; i < currentPosts.length(); i++){

                    //if a given reply doesn't contain an image, move on
                    try {
                        if (currentPosts.getJSONObject(i).get("tim") != null) {
                            imageNumbersList.add(currentPosts.getJSONObject(i).get("tim").toString());
                            imageExtensionsList.add(currentPosts.getJSONObject(i).getString("ext"));
                        }
                    } catch (JSONException ignored){
                    }
                }

                if(!imageNumbersList.isEmpty()){

                    //pick image at random
                    int index = (int) Math.ceil(Math.random() * imageNumbersList.size()) - 1;

                    //get the selected image on to disk
                    ImagePull.saveIMG(imageNumbersList.get(index), board.getSelectedItem().toString(), imageExtensionsList.get(index).substring(1));

                    currentIMGName = imageNumbersList.get(index);
                    ext = imageExtensionsList.get(index).substring(1);
                    currentIMGext = ext;

                    //display selected image
                    String urlString = "https://i.4cdn.org" + board.getSelectedItem().toString() + currentIMGName + "." + currentIMGext;
                    URL url = new URL(urlString);
                    pictureFrame.setThreadURL("http://boards.4channel.org" + board.getSelectedItem().toString() + "thread/" + threadNumsList.get(0));
                    addImage(ImageIO.read(url), dimensions, format);
                    saveToGallery(ImageIO.read(url), urlString);


                } else {
                    JOptionPane.showMessageDialog(pictureFrame, "Unlucky, you hit a thread with no images!");
                }



            } catch (IIOException iio) {
                JOptionPane.showMessageDialog(pictureFrame, "This image seems to no longer exist.");
            } catch (Exception any){
                any.printStackTrace();
            }

        });

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

    }

    private void addImage(BufferedImage image, JLabel dimensions, JLabel extension){

        dimensions.setText(image.getWidth() + " x " + image.getHeight());
        extension.setText(currentIMGext);
        pictureFrame.addImage(image);
        pictureFrame.repaint();
        pictureFrame.revalidate();
    }

    public void addGalleryImage(BufferedImage image, GalleryPanel panel){

        panel.addImage(image);
        panel.revalidate();
        panel.repaint();

    }

    private void saveToGallery(BufferedImage image, String url){

        int successful = 0;

        for(int i = 0; i < 3; i++){
            if(historyPanels[i].getThreadURL() == null){
                addGalleryImage(image, historyPanels[i]);
                historyPanels[i].setThreadURL(url);
                successful = 1;
                break;
            }
        }

        if(successful == 0){
            historyPanels[0].addImage(historyPanels[1].getImage());
            historyPanels[0].setThreadURL(historyPanels[1].getThreadURL());

            historyPanels[1].addImage(historyPanels[2].getImage());
            historyPanels[1].setThreadURL(historyPanels[2].getThreadURL());

            historyPanels[2].addImage(image);
            historyPanels[2].setThreadURL(url);


        }

    }

}
