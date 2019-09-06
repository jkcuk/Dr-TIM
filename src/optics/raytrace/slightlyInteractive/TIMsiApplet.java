package optics.raytrace.slightlyInteractive;

import javax.swing.*;

/**
 * A slightly interactive (si) TIM
 * 
 * Bundles the TIMsiBits class into a Java applet, which can be incorporated into web pages.
 * 
 * To change the functionality, override the TIMsiBits class and replace the call to the TIMsiBits constructor
 * in the second line of the init() method with a call to the constructor of the overridden class
 */
public class TIMsiApplet extends JApplet
{
	private static final long serialVersionUID = -1886340844335662640L;

	/**
	 * Gets called when starting up the applet.
	 * Start the interactive user interface.
	 * 
	 * @author	Johannes Courtial
	 */
	@Override
	public void init()
	{
		super.init();
	
		//
		// create an instance of the (overridden, if applicable) TIMsiBits class, ...
		//
		TIMsiBits interactiveBits = new TIMsiBits();	// if overridden, create an instance of the new TIMsiBits class
		
		//
		// ... pass the content pane, ...
		//
		interactiveBits.setContentPane(getContentPane());
		
		//
		// ... set the values of the various parameters, ...
		//
		
		// read the ImageCanvasSizeX and ImageCanvasSizeY parameters to the applet
	    String imageCanvasSizeXString = this.getParameter("ImageCanvasSizeX");
	    String imageCanvasSizeYString = this.getParameter("ImageCanvasSizeY");

	    // pass the value of these parameters to interactiveBits, provided everything makes sense
	    if ((imageCanvasSizeXString != null) && (imageCanvasSizeYString != null))
	    {
	    	try
	    	{
	    		int imageCanvasSizeXTry = Integer.parseInt(imageCanvasSizeXString);
	    		int imageCanvasSizeYTry = Integer.parseInt(imageCanvasSizeYString);
	    		
	    		// if no exception has been thrown so far, everything is fine
	    		interactiveBits.setImageCanvasSizeX(imageCanvasSizeXTry);
	    		interactiveBits.setImageCanvasSizeY(imageCanvasSizeYTry);
	    		
	    		// System.out.println("Image canvas size set to "+imageCanvasSizeXTry+" x "+imageCanvasSizeYTry+".");
	    	}
	    	catch (Exception e)
	    	{
	    		System.out.println("An error occured when reading the canvas size parameters. Using standard values.");
	    	}
	    }

		// read the AllowSaving parameter to the applet
	    String allowSavingString = this.getParameter("AllowSaving");
	    // set the value of allowSaving according to the AllowSaving parameter, if it exists
	    if (allowSavingString != null) interactiveBits.setAllowSaving(allowSavingString.equalsIgnoreCase("True"));
	    else interactiveBits.setAllowSaving(false); // default: saving not allowed
	
	    //
		// ... and go!
	    //
		interactiveBits.startInteractiveBits();
	}
}
