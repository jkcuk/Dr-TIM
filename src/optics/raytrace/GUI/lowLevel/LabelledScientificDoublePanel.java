package optics.raytrace.GUI.lowLevel;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a double in scientific notation.
 */
public class LabelledScientificDoublePanel extends JPanel
{
	private static final long serialVersionUID = -6194660253196464841L;

	private JLabel label;
	private ScientificDoublePanel doublePanel;

	public LabelledScientificDoublePanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		label = new JLabel(text);
		add(label); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		doublePanel = new ScientificDoublePanel();
		add(doublePanel);
		// add( Box.createHorizontalGlue() );
	}

	public void setNumber(double number)
	{
		doublePanel.setNumber(number);
	}

	public double getNumber()
	{
		return doublePanel.getNumber();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		label.setEnabled(enabled);
		doublePanel.setEnabled(enabled);
	}
	
	public void addDocumentListener(DocumentListener d)
	{
	    doublePanel.getDocument().addDocumentListener(d);
	}
}