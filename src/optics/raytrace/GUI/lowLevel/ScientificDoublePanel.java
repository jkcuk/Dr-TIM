package optics.raytrace.GUI.lowLevel;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.*;

/**
 * This panel allows editing of a double in scientific notation.
 */
public class ScientificDoublePanel extends JTextField implements KeyListener
{
	private static final long serialVersionUID = 2920902293575509646L;

	// see http://stackoverflow.com/questions/2944822/format-double-value-in-scientific-notation
	private DecimalFormat format = new DecimalFormat("0.#####E0");

	public ScientificDoublePanel()
	{
		super();
		setColumns(8);
	
		addKeyListener(this);
//		setPreferredSize(new Dimension(60,25));
	}

	public void setNumber(double number)
	{
		setText(format.format(number));
	}

	public double getNumber()
	{
		double number;
		
	    try
	    {
			number = format.parse(getText()).doubleValue();
			setBackground(new JTextField().getBackground());
		}
	    catch (ParseException e)
	    {
	    	number = 0;
			setBackground(Color.RED);
		}
	    
	    return number;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		getNumber();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		getNumber();
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		getNumber();
	}

}


