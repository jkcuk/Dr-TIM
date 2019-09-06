package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing an int.
 */
public class LabelledIntPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private IntPanel intPanel;
	private JLabel label;

	public LabelledIntPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		label = new JLabel(text);
		add(label); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		intPanel = new IntPanel();
		add(intPanel);
		// add(Box.createHorizontalGlue() );
	}

	public void setNumber(int number)
	{
		intPanel.setNumber(number);
	}

	public int getNumber()
	{
		return intPanel.getNumber();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{		
		label.setEnabled(enabled);
		intPanel.setEnabled(enabled);
	}
}