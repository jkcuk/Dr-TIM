package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Complex;

/**
 * This panel allows editing of a complex number.
 */
public class ComplexPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel rPanel, iPanel;

	public ComplexPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
				
		rPanel = new DoublePanel();
		add(rPanel);

		add(new JLabel("+"));
		
		iPanel = new DoublePanel();
		add(iPanel);

		add(new JLabel("i"));
	}

	public void setNumber(Complex c)
	{
		rPanel.setNumber(c.r);
		iPanel.setNumber(c.i);
	}

	public Complex getNumber()
	{
		return new Complex(
				rPanel.getNumber(),
				iPanel.getNumber()
		);
	}
}


