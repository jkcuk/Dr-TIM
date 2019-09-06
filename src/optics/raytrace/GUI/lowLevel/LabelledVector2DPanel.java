package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Vector2D;

/**
 * A label and a panel for editing a 2D vector.
 */
public class LabelledVector2DPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Vector2DPanel vector2DPanel;

	public LabelledVector2DPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		vector2DPanel = new Vector2DPanel();
		add(vector2DPanel);
		// add(Box.createHorizontalGlue());
	}
	
	public void setVector2D(double x, double y)
	{
		vector2DPanel.setVector2D(x, y);
	}
	
	public void setVector2D(Vector2D v)
	{
		vector2DPanel.setVector2D(v);
	}

	public Vector2D getVector2D()
	{
		return vector2DPanel.getVector2D();
	}
	
	public Vector2DPanel getVector2DPanel()
	{
		return vector2DPanel;
	}
}