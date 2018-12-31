package BGUpdate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GalleryPanel extends JPanel {

    private Image image;
    private JLabel frame;
    private String threadURL;
    private int width;
    private int height;

    GalleryPanel(){

        super();
        frame = new JLabel("No image");
        this.add(frame);
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setOpaque(true);
        this.setBackground(Color.darkGray);

        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (threadURL != null){
                    try {
                        Desktop.getDesktop().browse(java.net.URI.create(threadURL));
                    }
                    catch (java.io.IOException ioe) {
                        JOptionPane.showMessageDialog(frame, "That didn't work!");
                    }
                }
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        //this.setSize(new Dimension(width, height));

        super.paintComponent(g);

        if (image != null) {
            addImage(this.image);
        }

    }

    void addImage(Image image){

        //System.out.println(this.getHeight() + "P");
        //System.out.println(frame.getHeight() + "F");

        this.image = image;
        int originalHeight = this.image.getHeight(null);
        int originalWidth = this.image.getWidth(null);
        double ratio = (double) originalWidth / originalHeight;

        /*
        if (originalWidth > originalHeight){
            image = image.getScaledInstance(this.getWidth(), (int) Math.round(this.getWidth() / ratio), Image.SCALE_FAST);
        } else {
            image = image.getScaledInstance((int) Math.round(this.getHeight() * ratio), this.getHeight(), Image.SCALE_FAST);
        }
        */

        image = image.getScaledInstance(200, 100, Image.SCALE_FAST);

        frame.setText("");
        frame.setIcon(new ImageIcon(image));

        this.revalidate();
        this.repaint();

    }

    public void adjustSize(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void setThreadURL(String threadURL) {
        this.threadURL = threadURL;
    }

    String getThreadURL(){
        return threadURL;
    }

    Image getImage(){
        return image;
    }
}
