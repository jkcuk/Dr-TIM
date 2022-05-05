package optics.rayplay.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * An interactive optical component.
 * This can be either a simple optical component, such as a lens, or a collection of optical components, such as an omnidirectional lens
 * (which is a collection of lenses).
 * 
 * @author johannes
 */
public interface InteractiveOpticalComponent2D
{
	public String getName();

	public ArrayList<OpticalComponent2D> getOpticalComponents();

	public ArrayList<GraphicElement2D> getGraphicElements();
	
	public void writeParameters(PrintStream printStream);
	
	// CSV reading & writing
	
	public InteractiveOpticalComponent2D readFromCSV(String filename);

	public void writeToCSV(PrintWriter writer);
}
