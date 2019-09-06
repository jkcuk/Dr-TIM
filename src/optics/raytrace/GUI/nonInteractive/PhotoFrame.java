package optics.raytrace.GUI.nonInteractive;

import java.awt.*;
import javax.swing.*;

/**
 * Create a window to display a photo.
 */
public class PhotoFrame extends JFrame
{
	private static final long serialVersionUID = -3705361101245165199L;

	/**
     * Create the ray tracer's frame
     * @param title
     * @param width
     * @param height
     * @param visible
     */
    public PhotoFrame(String title, int width, int height, boolean visible) {
        super(title);
        setContentPane(new JPanel());
    
        // give the frame a reasonable default size
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        // setMinimumSize(size);
        // setMaximumSize(size);
        setSize(size);
    
        // show the frame
        pack();
        setVisible(visible);
    }
    
    public PhotoFrame() {
    	this(
    			"Dr TIM", 	// title
    			850,	// width
    			650,	// height
    			true	// visible
    		);
    }

}