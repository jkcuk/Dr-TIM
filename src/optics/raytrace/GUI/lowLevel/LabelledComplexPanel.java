package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Complex;

/**
 * A label and a panel for editing a complex number.
 */
public class LabelledComplexPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private ComplexPanel complexPanel;

	public LabelledComplexPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text + " "));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		complexPanel = new ComplexPanel();
		add(complexPanel);
	}

	public void setNumber(Complex c)
	{
		complexPanel.setNumber(c);
	}

	public Complex getNumber()
	{
		return complexPanel.getNumber();
	}
}