package optics.rayplay.core;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

/**
 * A graphic element in a RayPlay2DPanel.
 * @author johannes
 */
public interface GraphicElement2D
{
	public String getName();
	
//	/**
//	 * Draw the component
//	 * @param p
//	 * @param g
//	 */
//	public void draw(RayPlay2DPanel p, Graphics2D g);
	
	/**
	 * Draw the component.
	 * It might look different if the mouse is near it.
	 * By default, just draws, ignoring the mouse.  Override to change this.
	 * @param p
	 * @param g
	 * @param mouseNear
	 * @param mouseI
	 * @param mouseJ
	 */
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ);

	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ);

	/**
	 * Draw this last, on top
	 * @param p
	 * @param g
	 * @param mouseI
	 * @param mouseJ
	 */
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ);
	
	public void writeSVGCode(RayPlay2DPanel rpp);

	
	// interactivity
	
	public boolean isInteractive();
	
	public boolean isMouseNear(CoordinateConverterXY2IJ cc, int i, int j);
		
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ);
	
	public void mouseClicked(CoordinateConverterXY2IJ c, boolean mouseNear, MouseEvent e);

	/**
	 * @param c
	 * @param mouseNear
	 * @param e
	 * @return	true if the event has been handled by this graphics element and doesn't need any more handling
	 */
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e);

}
