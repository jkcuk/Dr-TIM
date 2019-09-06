package optics.raytrace.utility;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;


public class MyImageIO
{
	//Create a file chooser
	protected static final JFileChooser fc = new JFileChooser();
	
	public static BufferedImage selectAndLoadImage(String dialogTitle)
	{
		fc.setDialogTitle(dialogTitle);
		
		// select the image
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
        	File f = fc.getSelectedFile();
        	// System.out.println("Left image: " + f.getName());
    		try
    		{
    		    return ImageIO.read(f);
    		}
    		catch (IOException e)
    		{
            	// IO trouble
                System.err.println("Cannot open "+f.getName()+" (" + e.getMessage() + ").  Stopping.");
                
                // ... and stop
                // System.exit(-1);
    		}
        }
        else
        {
        	// user cancelled; say something...
            System.out.println("Open command cancelled by user.");
            
            // ... and stop
            // System.exit(-1);
        }
        
        return null;
	}
	
	public static void saveImageAsBMP(BufferedImage image, File file)
	{
		try
		{
			ImageIO.write(image, "BMP", file);
		}
		catch (IOException e)
		{
			System.err.println("saveImage::Error saving image: " + e.getMessage());
		}
	}
	
	public static void selectDestinationAndSaveImage(String dialogTitle, BufferedImage image)
	{
		fc.setDialogTitle(dialogTitle);
		
		// select the image
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
        	File f = fc.getSelectedFile();

        	saveImageAsBMP(image, f);
        }
	}

	public static JFileChooser getFileChooser()
	{
		return fc;
	}
}
