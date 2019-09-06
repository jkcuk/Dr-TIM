package optics.raytrace.GUI.lowLevel;

import java.awt.Component;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and component
 */
public class LabelledComponent extends JPanel
{
	private static final long serialVersionUID = -3072782402233309618L;

	public LabelledComponent(String text, Component component)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text + " "));
		add(component);
	}
}