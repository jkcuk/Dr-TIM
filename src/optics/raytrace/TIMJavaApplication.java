package optics.raytrace;

import optics.raytrace.GUI.lowLevel.GUIFrame;

/**
 * TODO add drawings of the geometry of cylinders etc.
 * 
 * TODO add a transformation panel at the bottom of the dialog for editing SceneObjects
 */


/**
 * Default The METATOY Raytracer (TIM) Java Application class.
 * 
 * Bundles the TIMInteractiveBits class into a Java application,
 * which can be turned into a runnable JAR file.
 * (To create a runnable JAR file, right-click on the project ("TheInteractiveMETATOY")
 * in the "Package Explorer" tab; select "Export…"; select "Runnable JAR file"; 
 * the rest should be obvious.)
 */
public class TIMJavaApplication
{
	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		TIMInteractiveBits interactiveBits = new TIMInteractiveBits();
		
		// open a window, and take a note of its content pane
		interactiveBits.setContentPane((new GUIFrame()).getContentPane());
		
		// images can be saved
		interactiveBits.setAllowSaving(true);
	
		// okay, set everything up and go!
		interactiveBits.startInteractiveBits();
	}
}
