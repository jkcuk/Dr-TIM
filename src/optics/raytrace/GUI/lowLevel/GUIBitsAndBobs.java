package optics.raytrace.GUI.lowLevel;


import java.awt.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

/**
 * A few bits and bobs related to the GUI
 */
public class GUIBitsAndBobs
{
	public static JPanel makeRow(Component c1, Component c2)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		// panel.setLayout(new FlowLayout());

		panel.add(c1); 
		panel.add(c2);

		return panel;
	}

	public static JPanel makeRow(Component c1, Component c2, Component c3)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		// panel.setLayout(new FlowLayout());

		panel.add(c1); 
		panel.add(c2);
		panel.add(c3);

		return panel;
	}

	/**
	 * Creates a combined text label and interactive component.
	 */
	public static JPanel makeRow(String text, Component field)
	{
		return makeRow(new JLabel(text), field);
	}

	/**
	 * Creates a combined interactive component and text label.
	 */
	public static JPanel makeRow(Component component, String text)
	{
		return makeRow(component, new JLabel(text));
	}

	/**
	 * Creates a combined interactive component, text label, and another component.
	 */
	public static JPanel makeRow(Component component1, String text, Component component2)
	{
		return makeRow(component1, new JLabel(text), component2);
	}

	/**
	 * Creates a component with a text label before and after.
	 */
	public static JPanel makeRow(String textBefore, Component component, String textAfter)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		panel.add(new JLabel(textBefore)); 
		panel.add(component);
		panel.add(new JLabel(textAfter));

		return panel;
	}
	
	/**
	 * Text, component1, text, component2.
	 */
	public static JPanel makeRow(String textBefore, Component component1, String textInBetween, Component component2)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		panel.add(new JLabel(textBefore)); 
		panel.add(component1);
		panel.add(new JLabel(textInBetween));
		panel.add(component2);

		return panel;
	}


	/**
	 * Surrounds two components text labels.
	 */
	public static JPanel makeRow(String textBefore, Component component1, String textInBetween, Component component2, String textAfter)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		panel.add(new JLabel(textBefore)); 
		panel.add(component1);
		panel.add(new JLabel(textInBetween));
		panel.add(component2);
		panel.add(new JLabel(textAfter));

		return panel;
	}

	public static JPanel makeRow(String text1, Component component1, String text2, Component component2, String text3, Component component3)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		panel.add(new JLabel(text1)); 
		panel.add(component1);
		panel.add(new JLabel(text2));
		panel.add(component2);
		panel.add(new JLabel(text3));
		panel.add(component3);

		return panel;
	}

	public static JPanel makeRow(Component component1, String text1, Component component2, String text2)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		panel.add(component1);
		panel.add(new JLabel(text1)); 
		panel.add(component2);
		panel.add(new JLabel(text2));

		return panel;
	}

	/**
	 * Creates a combined text label and two components.
	 */
	public static JPanel makeRow(String text, Component field1, Component field2)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		// panel.setLayout(new FlowLayout());

		panel.add(new JLabel(text)); 
		panel.add(field1);
		panel.add(field2);

		return panel;
	}

	/**
	 * Creates a combined text label and three components.
	 */
	public static JPanel makeRow(String text, Component field1, Component field2, Component field3)
	{
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));

		// panel.setLayout(new FlowLayout());

		panel.add(new JLabel(text)); 
		panel.add(field1);
		panel.add(field2);
		panel.add(field3);

		return panel;
	}

	/**
	 * @param title
	 * @return	a titled border which can be added to a component
	 */
	public static TitledBorder getTitledBorder(String title)
	{
		TitledBorder border;
		
		border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				title
			);
		border.setTitleJustification(TitledBorder.LEFT);
		
		return border;
	}
}


