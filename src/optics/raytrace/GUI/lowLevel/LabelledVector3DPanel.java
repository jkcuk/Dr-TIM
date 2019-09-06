package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;

/**
 * A label and a panel for editing a 3D vector.
 */
public class LabelledVector3DPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private Vector3DPanel vector3DPanel;
	private JLabel label;

	public LabelledVector3DPanel(String text)
	{
		super();
		setLayout(new MigLayout("insets 0"));

		label = new JLabel(text);
		add(label);	// add a space to make it prettier!
		vector3DPanel = new Vector3DPanel();
		add(vector3DPanel);
	}

	public void setVector3D(Vector3D v)
	{
		vector3DPanel.setVector3D(v);
	}

	public Vector3D getVector3D()
	{
		return vector3DPanel.getVector3D();
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		label.setEnabled(enabled);
		vector3DPanel.setEnabled(enabled);
	}
}