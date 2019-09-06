package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a panel for editing a symmetric 3x3 matrix.
 */
public class LabelledSymmetricMatrix3DPanel extends JPanel
{
	private static final long serialVersionUID = -5708240199727199217L;

	private transient SymmetricMatrix3DPanel matrix3DPanel;

	public LabelledSymmetricMatrix3DPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		add(new JLabel(text));	// add a space to make it prettier!
		// add(Box.createRigidArea(new Dimension(0,0)));
		matrix3DPanel = new SymmetricMatrix3DPanel();
		add(matrix3DPanel);
		// add(Box.createHorizontalGlue());
	}

	public void setMatrix3D(double[] m)
	{
		matrix3DPanel.setMatrix3D(m);
	}

	public double[] getMatrix3D()
	{
		return matrix3DPanel.getMatrix3D();
	}
}