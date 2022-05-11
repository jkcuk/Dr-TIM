package optics.rayplay.core;

import java.awt.Graphics2D;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * An interactive optical component.
 * This can be either a simple optical component, such as a lens or a ray source, or a collection of optical components, such as an omnidirectional lens
 * (which is a collection of lenses).
 * 
 * @author johannes
 */
public interface InteractiveOpticalComponent2D
{
	public String getName();
	
	public void initialiseRays();

	public ArrayList<Ray2D> getRays();

	public ArrayList<OpticalComponent2D> getOpticalComponents();

	public ArrayList<GraphicElement2D> getGraphicElements();
	
	// drawing
	
	public void drawRays(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);

	public void drawGraphicElements(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);

	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);
	
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ);


	// drawing into an SVG file
	
	public void writeSVGCode(RayPlay2DPanel rpp);
	

	public void writeParameters(PrintStream printStream);
	
	// CSV reading & writing
	
	public InteractiveOpticalComponent2D readFromCSV(String filename);

	public void writeToCSV(PrintWriter writer);
	
}
