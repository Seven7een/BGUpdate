package BGUpdate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    Image image;
    JLabel frame;

    public ImagePanel(){

        super();
        frame = new JLabel("No image");
        this.add(frame);

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (image != null) {
            addImage(this.image);
        }
        /*
        if(image != null) {
            image = image.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_FAST);
            g.drawImage(image, this.getX(), this.getY(), this);
        }
        */
    }

    public void addImage(Image image){

        this.image = image;
        int originalHeight = this.image.getHeight(null);
        int originalWidth = this.image.getWidth(null);
        double ratio = (double) originalWidth / originalHeight;

        if (originalWidth > originalHeight){
            image = image.getScaledInstance(this.getWidth(), (int) Math.round(this.getWidth() / ratio), Image.SCALE_FAST);
        } else {
            image = image.getScaledInstance((int) Math.round(this.getHeight() * ratio), this.getHeight(), Image.SCALE_FAST);
        }

        //image = image.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_FAST);

        frame.setText("");
        frame.setIcon(new ImageIcon(image));

        this.revalidate();
        this.repaint();

    }

}
