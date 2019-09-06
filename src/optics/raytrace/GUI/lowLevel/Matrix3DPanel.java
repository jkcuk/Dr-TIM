package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;

/**
 * This panel allows editing of a 3x3 matrix.
 */
public class Matrix3DPanel extends JPanel
{
	private static final long serialVersionUID = 1386374318638861254L;

	private Vector3DPanel row1Panel, row2Panel, row3Panel;

	public Matrix3DPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		
		row1Panel = new Vector3DPanel();
		row2Panel = new Vector3DPanel();
		row3Panel = new Vector3DPanel();
		
		add(new JLabel("("), "west");
		add(new JLabel(")"), "east");
		add(row1Panel, "wrap");
		add(row2Panel, "wrap");
		add(row3Panel, "wrap");
	}

	public void setMatrix3D(Vector3D row1, Vector3D row2, Vector3D row3)
	{
		row1Panel.setVector3D(row1);
		row2Panel.setVector3D(row2);
		row3Panel.setVector3D(row3);
	}
	
	public void setMatrix3D(double[][] m)
	{
		row1Panel.setVector3D(m[0][0], m[0][1], m[0][2]);
		row2Panel.setVector3D(m[1][0], m[1][1], m[1][2]);
		row3Panel.setVector3D(m[2][0], m[2][1], m[2][2]);
	}

	public double[][] getMatrix3D()
	{
		double[][] m = new double[3][3];
		m[0][0] = row1Panel.getXComponent();
		m[0][1] = row1Panel.getYComponent();
		m[0][2] = row1Panel.getZComponent();
		m[1][0] = row2Panel.getXComponent();
		m[1][1] = row2Panel.getYComponent();
		m[1][2] = row2Panel.getZComponent();
		m[2][0] = row3Panel.getXComponent();
		m[2][1] = row3Panel.getYComponent();
		m[2][2] = row3Panel.getZComponent();
		return m;
	}
}


