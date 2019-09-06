package optics.raytrace.GUI.lowLevel;

import java.awt.*;

import javax.swing.*;

import java.awt.image.*;

/**
 * This panel displays the rendered scene.
 */
/**
 * @author Johannes Courtial
 *
 */
public class BufferedImageCanvas extends JPanel
{
	private static final long serialVersionUID = 2196757023811347433L;

	private BufferedImage image;
    private Image scaledImage=null;
    protected Dimension imageSize, imageScreenSize = new Dimension(1, 1);
    protected boolean needsRepainting;
    protected int imageOffsetX, imageOffsetY;


    /**
     * Create a new panel onto which the rendered image will be displayed.
     */
    public BufferedImageCanvas(int imageCanvasSizeX, int imageCanvasSizeY)
    {
    	super();
    	setSize(imageCanvasSizeX, imageCanvasSizeY);
    	needsRepainting = false;
    }
    
    /**
     * Create a new panel and display in it the image.
     * @param image
     */
    public BufferedImageCanvas(BufferedImage image, int imageCanvasSizeX, int imageCanvasSizeY)
    {
    	this(imageCanvasSizeX, imageCanvasSizeY);
    	setImage(image);
    }
  
    /**
     * Return the image.
     */
    public BufferedImage getImage()
    {
        return image;
    }
    
    public void setImage(BufferedImage image)
    {
    	this.image = image;
    	// setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    	
    	// repaint
    	needsRepainting = true;
    	repaint();
    }
    
    public int getImageOffsetX() {
		return imageOffsetX;
	}

    public Dimension getImageScreenSize() {
		return imageScreenSize;
	}

	public int getImageOffsetY() {
		return imageOffsetY;
	}

	public void setImageOffset(int imageOffsetX, int imageOffsetY)
	{
		this.imageOffsetX = imageOffsetX;
		this.imageOffsetY = imageOffsetY;
	}

	@Override
	public void setSize(int imageCanvasSizeX, int imageCanvasSizeY)
    {
    	setPreferredSize(new Dimension(imageCanvasSizeX, imageCanvasSizeY));
    	
    	// repaint
    	needsRepainting = true;
    	repaint();
    }

	@Override
	public void paint(Graphics g)
    {
        super.paint(g);
        
        if(image != null)
        {
        	Dimension newSize = this.getSize();
    
        	int W = (int)newSize.getWidth();
        	int H = (int)newSize.getHeight();
        	
        	if (needsRepainting || !newSize.equals(imageSize))
        	{
        		int w = image.getWidth();
        		int h = image.getHeight();
        		if (w/h > W/H) {
        			// wide image
        			imageScreenSize = new Dimension(W, (int)((double)W*(double)h/w));
        		} else {
        			// tall image
        			imageScreenSize = new Dimension((int)((double)H*(double)w/h), H);
        		}
        		imageSize = newSize;
        		scaledImage = image.getScaledInstance(imageScreenSize.width, imageScreenSize.height, Image.SCALE_SMOOTH);

        		needsRepainting = false;
        	}

        	int offsetX = (W - imageScreenSize.width)/2;
	        int offsetY = (H - imageScreenSize.height)/2;
	        
	        setImageOffset(offsetX, offsetY);

        	g.drawImage(scaledImage, offsetX, offsetY, null);
        }
    }
}


