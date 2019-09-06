package optics.raytrace.slightlyInteractive;

import javax.swing.*;

/**
 * A simple interactive TIM that demonstrates a gCLAs cloak
 */
public class TIMsiCloakingApplet extends JApplet
{
	private static final long serialVersionUID = 1916213533889706936L;

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
		// create an instance of the TIMInteractiveBits class, ...
		//
		TIMsiBits interactiveBits = new TIMsiCloakingBits();
		
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
