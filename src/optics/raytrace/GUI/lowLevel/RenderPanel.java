package optics.raytrace.GUI.lowLevel;

import java.awt.image.BufferedImage;

/**
 * This panel displays the rendered scene from different view points, as well as
 * all of the graphical side of the user interface.
 * @author Johannes
 */
public interface RenderPanel
{
	/**
	 * called to initiate rendering
	 */
	public void render();

	/**
	 * called to display an (intermediate or final) image
	 * @param image
	 */
	public void setRenderedImage(BufferedImage image);

	/**
	 * called when rendering is complete (or cancelled)
	 * @param wasCancelled
	 */
	public void renderingDone(boolean wasCancelled);
	
	public void repaint();
}


