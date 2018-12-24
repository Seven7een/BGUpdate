package BGUpdate;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageGUI {

    private JFrame mainFrame;
    private ImagePanel pictureFrame;
    private String currentIMGPath;
    private String currentIMGName;
    private String currentIMGext;

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
        contentPane.add(imageInfo, BorderLayout.SOUTH);

        //listeners

        addBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.addItem(JOptionPane.showInputDialog("Enter board with / (e.g. /wg/) : "));
            }
        });

        setBackground.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    String cmd= "./changeBG.sh \"" + currentIMGext + "\"" ;
                    System.out.println(cmd);
                    ProcessBuilder bd = new ProcessBuilder(cmd.split(" "));

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

                } catch (Exception e2){
                    e2.printStackTrace();
                }
            }
        });

        saveImage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                fc.setCurrentDirectory(new File("./"));
                //fc.setSelectedFile(new File("./newimage." + currentIMGext));
                int retrival = fc.showSaveDialog(mainFrame);

                if (retrival == JFileChooser.APPROVE_OPTION) {
                    try {

                        Path in = Paths.get("./images/newimage." + currentIMGext);
                        Path out = Paths.get(fc.getSelectedFile().getAbsolutePath() + "." + currentIMGext);

                        Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        getImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Path newP = Paths.get("./images");
                try {
                    Files.createDirectory(newP);
                } catch (FileAlreadyExistsException fae) {
                } catch (IOException ioe) {
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

                    allThreads = ImagePull.sendGET(board.getSelectedItem().toString());

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

                    allImages = ImagePull.sendGETURL(threadNumsList.get(0), board.getSelectedItem().toString());

                    matcher = pattern.matcher(allImages);
                    matcherExt = patternExt.matcher(allImages);

                    while(matcher.find() && matcherExt.find()){
                        imageNumbersList.add(allImages.substring(matcher.start(), matcher.end()));
                        imageExtensionsList.add(allImages.substring(matcherExt.start(), matcherExt.end()));
                    }

                    int index = (int) Math.ceil(Math.random() * imageNumbersList.size()) - 1;

                    ImagePull.saveIMG(imageNumbersList.get(index), board.getSelectedItem().toString(), imageExtensionsList.get(index));

                    currentIMGName += imageNumbersList.get(index);
                    currentIMGName += imageExtensionsList.get(index);

                    ext = imageExtensionsList.get(index);

                    currentIMGext = ext;

                    URL url = new URL("https://i.4cdn.org" + board.getSelectedItem().toString() + imageNumbersList.get(index) + "." + imageExtensionsList.get(index));
                    addImage(ImageIO.read(url), dimensions, format);

                } catch (IIOException iio) {
                    //iio.printStackTrace();
                    JOptionPane.showMessageDialog(pictureFrame, "This image seems to no longer exist.");
                } catch (Exception any){
                    any.printStackTrace();
                }

            }
        });

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

    }

    public void addImage(BufferedImage image, JLabel dimensions, JLabel extention){

        dimensions.setText(image.getWidth() + " x " + image.getHeight());
        extention.setText(currentIMGext);
        pictureFrame.addImage(image);
        pictureFrame.repaint();
        pictureFrame.revalidate();
    }

}
