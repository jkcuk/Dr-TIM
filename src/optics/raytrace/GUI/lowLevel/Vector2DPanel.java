package optics.raytrace.GUI.lowLevel;

import java.awt.Color;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Vector2D;

/**
 * This panel allows editing of a 2D vector.
 */
public class Vector2DPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel xPanel, yPanel;

	public Vector2DPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		
		add(new JLabel("("));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));
		
		xPanel = new DoublePanel();
		add(xPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		add(new JLabel(","));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		yPanel = new DoublePanel();
		add(yPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		add(new JLabel(")"));
	}

	public void setVector2D(Vector2D v)
	{
		xPanel.setNumber(v.x);
		yPanel.setNumber(v.y);
	}
	
	public void setVector2D(double x, double y)
	{
		xPanel.setNumber(x);
		yPanel.setNumber(y);
	}

	public Vector2D getVector2D()
	{
		return new Vector2D(
				xPanel.getNumber(),
				yPanel.getNumber()
		);
	}

	public DoublePanel getxPanel() {
		return xPanel;
	}

	public DoublePanel getyPanel() {
		return yPanel;
	}
	
	@Override
	public void setBackground(Color bg)
	{
		if(xPanel != null) xPanel.setBackground(bg);
		if(yPanel != null) yPanel.setBackground(bg);
	}
}


