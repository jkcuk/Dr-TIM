package optics.raytrace.GUI.lowLevel;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a double.
 * For use with listeners that detects when the number has changed, use DoublePanel.
 */
public class LabelledDoublePanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private DoublePanel doublePanel;

	public LabelledDoublePanel(String text, String pattern)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		label = new JLabel(text);
		add(label); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		doublePanel = new DoublePanel(pattern);
		add(doublePanel);
		// add( Box.createHorizontalGlue() );
	}
	
	public LabelledDoublePanel(String text)
	{
		this(text, DoublePanel.STANDARD_PATTERN);
	}

	public void setNumber(double number)
	{
		doublePanel.setNumber(number);
	}

	public double getNumber()
	{
		return doublePanel.getNumber();
	}
	
	public void setText(String text)
	{
		doublePanel.setText(text);
	}
	
	public String getText()
	{
		return doublePanel.getText();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		label.setEnabled(enabled);
		doublePanel.setEnabled(enabled);
	}
	
	public void setEditable(boolean editable)
	{
		doublePanel.setEditable(editable);
	}
	
	@Override
	public void setBackground(Color bg)
	{
		if(doublePanel != null) doublePanel.setBackground(bg);
	}
	
	public void addDocumentListener(DocumentListener d)
	{
	    doublePanel.getDocument().addDocumentListener(d);
	}
	
	public void addActionListener(ActionListener l)
	{
		doublePanel.addActionListener(l);
	}
	
	@Override
	public void addFocusListener(FocusListener l)
	{
		doublePanel.addFocusListener(l);
	}
	
	public DoublePanel getDoublePanel()
	{
		return doublePanel;
	}
}