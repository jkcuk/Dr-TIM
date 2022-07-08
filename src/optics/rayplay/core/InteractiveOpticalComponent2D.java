package optics.rayplay.core;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import math.Vector2D;


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
	
	public RayPlay2DPanel getRayPlay2DPanel();
	
	public void initialiseRays();

	public ArrayList<LightRay2D> getRays();

	public ArrayList<OpticalComponent2D> getOpticalComponents();

	public ArrayList<GraphicElement2D> getGraphicElements(boolean isSelected, boolean isMouseNear);
	
	public void move(Vector2D delta);
	
	// drawing
	
	public void drawRays(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);

	public void drawGraphicElements(RayPlay2DPanel p, Graphics2D g, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);

	public void drawGraphicElementsInFront(RayPlay2DPanel p, Graphics2D g, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);
	
	public void drawGraphicElementsBehind(RayPlay2DPanel p, Graphics2D g, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ);
	
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ);

	
	// mouse stuff
	
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e);

	// drawing into an SVG file
	
	public void writeSVGCode(RayPlay2DPanel rpp);
	

	public void writeParameters(PrintStream printStream);
	
	// CSV reading & writing
	
	public InteractiveOpticalComponent2D readFromCSV(String filename);

	public void writeToCSV(PrintWriter writer);
	
}
