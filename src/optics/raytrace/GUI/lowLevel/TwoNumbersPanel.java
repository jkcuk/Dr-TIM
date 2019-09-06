package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;


/**
 * A panel for editing two numbers.
 */
public class TwoNumbersPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel number1Panel, number2Panel;

	public TwoNumbersPanel(String label1, String label2)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(label1));
		number1Panel = new DoublePanel();
		add(number1Panel);

		add(new JLabel(label2));
		number2Panel = new DoublePanel();
		add(number2Panel);
	}

	public void setNumber1(double number)
	{
		number1Panel.setNumber(number);
	}

	public double getNumber1()
	{
		return number1Panel.getNumber();
	}

	public void setNumber2(double number)
	{
		number2Panel.setNumber(number);
	}

	public double getNumber2()
	{
		return number2Panel.getNumber();
	}
}