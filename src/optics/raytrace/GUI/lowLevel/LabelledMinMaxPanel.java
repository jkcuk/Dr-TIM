package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a double.
 */
public class LabelledMinMaxPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel minPanel, maxPanel;

	public LabelledMinMaxPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text)); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		minPanel = new DoublePanel();
		add(minPanel);
		
		add(new JLabel("to"));
		
		maxPanel = new DoublePanel();
		add(maxPanel);
		
		// add( Box.createHorizontalGlue() );
	}

	public void setMin(double min)
	{
		minPanel.setNumber(min);
	}

	public void setMax(double max)
	{
		maxPanel.setNumber(max);
	}

	public double getMin()
	{
		return minPanel.getNumber();
	}

	public double getMax()
	{
		return maxPanel.getNumber();
	}
}