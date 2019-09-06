package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a 3x3 matrix.
 */
public class LabelledMatrix3DPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Matrix3DPanel matrix3DPanel;

	public LabelledMatrix3DPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		matrix3DPanel = new Matrix3DPanel();
		add(matrix3DPanel);
		// add(Box.createHorizontalGlue());
	}

	public void setMatrix3D(double[][] m)
	{
		matrix3DPanel.setMatrix3D(m);
	}

	public double[][] getMatrix3D()
	{
		return matrix3DPanel.getMatrix3D();
	}
}