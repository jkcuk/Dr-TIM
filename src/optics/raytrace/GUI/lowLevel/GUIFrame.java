package optics.raytrace.GUI.lowLevel;

import java.awt.*;
import javax.swing.*;

/**
 * Create a frame to display a photo.
 */
public class GUIFrame extends JFrame
{
	private static final long serialVersionUID = 7858990854089347474L;

	/**
     * Create interactive TIM's window
     */
    public GUIFrame() {
        super("Dr TIM");
        setContentPane(new JPanel());
    
        // give the frame a reasonable default size
        Dimension size = new Dimension(750, 660);
        setPreferredSize(size);
//        setMinimumSize(size);
//        setMaximumSize(size);
        
//        
//        // start the frame maximized
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // show the frame
        pack(); 
        setVisible(true);
    }
}