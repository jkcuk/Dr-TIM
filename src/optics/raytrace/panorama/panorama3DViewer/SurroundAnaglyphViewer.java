package optics.raytrace.panorama.panorama3DViewer;

import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import optics.raytrace.GUI.nonInteractive.PhotoFrame;


/**
 * Prompts the user to select two BMP files, interprets one as a 4π surround panoramic view from the
 * left eye, the other from the right eye, and combines them into a 4π surround anaglyph.
 *
 * For some reason, on my computer this runs as an applet but not as a Java Application!?
 * 
 * @author Johannes Courtial
 */
public class SurroundAnaglyphViewer extends JApplet
{
	private static final long serialVersionUID = -5451911963915177243L;

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		run(container);		
	}
	
	
	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init()
	{
		super.init();
		
		run(getContentPane());
	}
	
	/**
	 * Do the business...
	 * @param container
	 */
	public static void run(final Container container)
	{
		Runnable r = new SurroundAnaglyphViewerRunnable(container);
		
		SwingUtilities.invokeLater(r);
	}
}
