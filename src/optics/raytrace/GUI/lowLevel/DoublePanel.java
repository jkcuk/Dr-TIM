package optics.raytrace.GUI.lowLevel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * This panel allows editing of a double.
 * 
 * To detect changes in the numerical value, 
 */
public class DoublePanel extends JTextField implements KeyListener, ActionListener, FocusListener
{
	private static final long serialVersionUID = -3865825002430469079L;
	
	public static final String STANDARD_PATTERN = "%G";	// use upper case E as NumberFormat only understands E, not e
	public static final String SCIENTIFIC_PATTERN = "%E";	// use upper case E as NumberFormat only understands E, not e
	
	// private DecimalFormat format;
	private boolean valueIsAdjusting, parsable;
	private String numberFormat;

	/**
	 * Call with pattern = DoublePanel.SCIENTIFIC_PATTERN for scientific notation
	 * @param pattern
	 */
	public DoublePanel(String numberFormat)
	{
		super();
		setColumns(10);
		
		// format = new DecimalFormat(pattern);
		this.numberFormat = numberFormat;
	
		addKeyListener(this);
		valueIsAdjusting = false;
//		setPreferredSize(new Dimension(60,25));
	}
	
	public DoublePanel()
	{
		this(STANDARD_PATTERN);
	}
	
	

	public String getNumberFormat() {
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat) {
		this.numberFormat = numberFormat;
	}

	public void setNumber(double number)
	{
//		setText(format.format(number));
//		setText(String.format(numberFormat, number));
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		setText(nf.format(number));
		parsable = true;
		fireChangeEvent();
	}

	public double getNumber()
	{
		double number;
		
	    try
	    {
			// number = format.parse(getText()).doubleValue();
	    	NumberFormat nf = NumberFormat.getInstance();
	    	number = nf.parse(getText()).doubleValue();
	    	// System.out.println("string="+getText()+", value="+number);
	    	// number = Double.parseDouble(getText());
			parsable = true;
			setBackground(new JTextField().getBackground());
		}
	    catch (ParseException e)
	    // catch (NumberFormatException e)
	    {
	    	number = 0;
	    	parsable = false;
			setBackground(Color.RED);
		}
	    
	    return number;
	}
	
	public boolean isValueIsAdjusting() {
		return valueIsAdjusting;
	}

	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		this.valueIsAdjusting = valueIsAdjusting;
	}

	public boolean isParsable() {
		return parsable;
	}

	public void setParsable(boolean parsable) {
		this.parsable = parsable;
	}

	public void addChangeListener(ChangeListener changeListener) {
		// for this to work, we must add *this* as an ActionListener and a FocusListener
		addActionListener(this);
		addFocusListener(this);
		listenerList.add( ChangeListener.class, changeListener );
	}
	
	public void removeChangeListener(ChangeListener changeListener) {
		listenerList.remove( ChangeListener.class, changeListener );
	}
	
	private double oldNumber;
	
	void considerFireChangeEvent()
	{
		double number = getNumber();
		if(parsable && (number != oldNumber))
		{
			fireChangeEvent();
			oldNumber = number;
		}
	}
	
	void fireChangeEvent() {
		Object[] listeners = listenerList.getListenerList();
		for ( int i = 0; i < listeners.length; i += 2 )
			if ( listeners[i] == ChangeListener.class )
				((ChangeListener)listeners[i + 1]).stateChanged( new ChangeEvent(this) );
	}

	
	@Override
	public void keyPressed(KeyEvent e) { }

	@Override
	public void keyReleased(KeyEvent e)
	{
		getNumber();
		valueIsAdjusting = true;
	}

	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void focusGained(FocusEvent e) { }

	@Override
	public void focusLost(FocusEvent e) {
		if(valueIsAdjusting)
		{
			valueIsAdjusting = false;
			considerFireChangeEvent();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(valueIsAdjusting)
		{
			valueIsAdjusting = false;
			considerFireChangeEvent();
		}
	}

}


