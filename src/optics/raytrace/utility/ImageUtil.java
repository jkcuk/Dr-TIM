package optics.raytrace.utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * A utility class concerned with images
 * @author johannes
 */
public class ImageUtil
{

	/**
	 * Convert the string <i>s</i> into a BufferedImage;
	 * based on code from https://stackoverflow.com/questions/17282495/java-parsing-truetype-font-to-extract-each-characters-as-image-its-code
	 * @param s	the string
	 * @param fontSize	the larger, the higher resolution
	 * @param color	text colour
	 * @return	the string, converted into a BufferedImage
	 */
	public static BufferedImage stringToBufferedImage(String s, int fontSize, Color color)
	{
		//First, we have to calculate the string's width and height

		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = img.getGraphics();

		//Set the font to be used when drawing the string
		Font f = new Font("Tahoma", Font.PLAIN, fontSize);
		g.setFont(f);

		//Get the string visual bounds
		FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
		Rectangle2D rect = f.getStringBounds(s, frc);
		//Release resources
		g.dispose();

		//Then, we have to draw the string on the final image

		//Create a new image where to print the character
		img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_INT_RGB/*TYPE_4BYTE_ABGR*/);
		g = img.getGraphics();
		g.setColor(color); //Otherwise the text would be white
		g.setFont(f);

		//Calculate x and y for that string
		FontMetrics fm = g.getFontMetrics();
		int x = 0;
		int y = fm.getAscent(); //getAscent() = baseline
		g.drawString(s, x, y);

		//Release resources
		g.dispose();

		//Return the image
		return img;
	}
	
	/**
	 * Convert the html string <i>html</i> into a BufferedImage;
	 * cobbled together from various sources, including https://code.google.com/archive/p/java-html2image/
	 * @param html
	 * @param fontFamily
	 * @param fontSize
	 * @return
	 */
	public static BufferedImage htmlStringToBufferedImage(String html, String fontFamily, int fontSize)
	{
		JEditorPane editorPane = new JEditorPane("text/html",
				"<html><head><style>p {font-family: "+fontFamily+"; font-size:"+fontSize+";}</style></head>" +
                "<body><p>"+html+"</p></body></html>");
				
		// from HTMLImageGenerator.java's getBufferedImage() method
		Dimension prefSize = editorPane.getPreferredSize();
		BufferedImage img = new BufferedImage(prefSize.width, editorPane.getPreferredSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = img.getGraphics();
		editorPane.setSize(prefSize);
		editorPane.paint(graphics);
		return img;
	}
	
	public static BufferedImage htmlStringToBufferedImage(String html, int fontSize)
	{
		return htmlStringToBufferedImage(html, "Arial", fontSize);
	}


	/**
	 * Return a BufferedImage of a JPanel.
	 * See https://stackoverflow.com/questions/1349220/convert-jpanel-to-image
	 * @param panel
	 * @return	a BufferedImage of the JPanel
	 */
	public static BufferedImage createImage(JPanel panel)
	{
	    int w = panel.getWidth();
	    int h = panel.getHeight();
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    panel.paint(g);
	    g.dispose();
	    return bi;
	}
}
