package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;

/**
 * This panel allows editing of a 3D vector.
 */
public class Vector3DPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private DoublePanel xPanel, yPanel, zPanel;

	public Vector3DPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
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

		add(new JLabel(","));
		
		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		zPanel = new DoublePanel();
		add(zPanel);

		// add a bit of (non-stretchable) space between the image and the status/button line
		// add(Box.createRigidArea(new Dimension(0, 0)));

		add(new JLabel(")"));
		
		// add a (stretchable) space between the status indicator and the edit button
		// statusAndEditButton.add(Box.createHorizontalGlue());
	}

	public void setVector3D(Vector3D v)
	{
		xPanel.setNumber(v.x);
		yPanel.setNumber(v.y);
		zPanel.setNumber(v.z);
	}

	public void setVector3D(double x, double y, double z)
	{
		xPanel.setNumber(x);
		yPanel.setNumber(y);
		zPanel.setNumber(z);
	}

	public Vector3D getVector3D()
	{
		return new Vector3D(
				xPanel.getNumber(),
				yPanel.getNumber(),
				zPanel.getNumber()
		);
	}
	
	public double getXComponent()
	{
		return xPanel.getNumber();
	}

	public double getYComponent()
	{
		return yPanel.getNumber();
	}

	public double getZComponent()
	{
		return zPanel.getNumber();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		xPanel.setEnabled(enabled);
		yPanel.setEnabled(enabled);
		zPanel.setEnabled(enabled);
	}
}


