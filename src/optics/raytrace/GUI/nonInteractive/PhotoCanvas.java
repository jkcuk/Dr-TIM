package optics.raytrace.GUI.nonInteractive;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


/**
 * A panel that displays the rendered image and which can save the image.
 * 
 * Some attempt has been made at trying to indicate the progress, but this has never been implemented properly
 * and has been commented out in this version.
 */
public class PhotoCanvas extends JPanel
{
	private static final long serialVersionUID = -2500585257946143105L;

	static final Color SKY_BLUE = new Color(136, 187, 238);

    BufferedImage image;
    Image scaledImage=null;
    Dimension oldSize=null;
    boolean fillFrame;
//    double progress;

    /**
     * Create a new panel onto which the rendered image
     * image will be displayed.
     * 
     * @param fillFrame	true if the image is to fill the entire frame, otherwise standard size
     */
    public PhotoCanvas(boolean fillFrame) {
    	super();
        this.fillFrame = fillFrame;
 //       setProgress(0.0);
    }
    
    public PhotoCanvas(BufferedImage image)
    {
    	this(false);
    	setImage(image);
    }

    /**
     * Allows the external rendering thread to update the displayed image.
     */
    public void setImage(BufferedImage image)
    {
        this.image = image;
        needsRepainting();
        repaint();
    }
  
    /**
     * Return the image that was last rendered, or null.
     */
    public BufferedImage getImage() {
        return image;
    }
    
    /**
     * call to ensure next time repaint() is called this component is actually repainted
     */
    public void needsRepainting()
    {
    	oldSize = new Dimension(-1, -1);
    }

//    /**
//     * Keep a record of the progress made by the rendering thread.
//     * @param progress The decimal fraction of the image's height that has been rendered so far
//     */
//    public void setProgress(double progress) {
//        this.progress = progress;
//        repaint();
//    }
  
    @Override
	public void paint(Graphics g)
    {
        super.paint(g);
        Dimension newSize = this.getSize();
    
        int W = (fillFrame) ? (int) newSize.getWidth(): 800;
        int H = (fillFrame) ? (int) newSize.getHeight(): 600;
            
        if (image==null)
        {
//            g.setColor(Color.BLACK);
//            g.fillRect(0, 1 + (int) (progress * H), W, H);
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, W, (int) (progress * H));
            return;
        }
      
        if (!newSize.equals(oldSize)) {
            int w = image.getWidth();
            int h = image.getHeight();
            Dimension actualSize;
            if (w/h > W/H) {
                // wide image
                actualSize = new Dimension(W, W*h/w);
            } else {
                // tall image
                actualSize = new Dimension(H*w/h, H);
            }
            oldSize = newSize;
            scaledImage = image.getScaledInstance(actualSize.width, actualSize.height, Image.SCALE_SMOOTH);
        }
      
        g.drawImage(scaledImage, 2, 2, null);
    }
    
    @Override
	public Dimension getPreferredSize() {
        return fillFrame ? super.getPreferredSize() : new Dimension(800, 600);
    }
    
    
    /**
     * Save the image to a given file and with a particular format.
     * @param filename The file to save to.
     * @param format The format in which to save the image (eg "bmp").
     */
    public void saveImage(String filename, String format) { // format: "bmp"
	try {
		File outputfile = new File(filename);
		ImageIO.write(image, format, outputfile);
	} catch (IOException e) {
		System.err.println("Error saving image");
	}
    }

    /**
     * Return the file type of a filename, or "bmp" as default
     */
    public static String suffix(File file) {
        String[] nameBits = file.getName().split("\\.");
        //System.out.println("# bits = " + nameBits.length);
        return (nameBits.length>1) ? nameBits[nameBits.length-1] : "bmp";
    }

    /**
     * Save a bitmap image to a given file.
     */
    public void saveImage(File outputfile) { // format: "bmp"
        try {
                ImageIO.write(image, suffix(outputfile), outputfile);
        } catch (IOException e) {
                System.err.println("Error saving image");
        }
    }

}