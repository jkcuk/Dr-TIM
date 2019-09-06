package optics.raytrace.GUI.lowLevel;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * A panel containing buttons.
 * 
 * @author Johannes Courtial
 */
public class ButtonsPanel extends JPanel
{
	private static final long serialVersionUID = 3408001171103949846L;

	private Vector<JButton> buttons;
	
	public ButtonsPanel()
	{
		super();

		setLayout(new MigLayout("insets 0"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		buttons = new Vector<JButton>(2);
	}
	
	public JButton addButton(String buttonText)
	{
		// make the new button, ...
		JButton newButton = new JButton(buttonText);
		
		// ... and add it to the Vector of buttons...
		buttons.add(newButton);
		
		// ... and the panel
		add(newButton);
		
		// return the new button, in case anybody wants to do something with it
		return newButton;
	}
	
	public JButton getButton(int i)
	{
		return buttons.get(i);
	}
}
