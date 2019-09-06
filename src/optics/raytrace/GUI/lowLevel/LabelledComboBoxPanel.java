package optics.raytrace.GUI.lowLevel;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

/**
 * A label and a combo box
 */
public class LabelledComboBoxPanel extends JPanel
{
	private static final long serialVersionUID = 3878473321456966523L;

	private JLabel label;
	private JComboBox<?> comboBox;

	public LabelledComboBoxPanel(String text, JComboBox<?> comboBox)
	{
		super();
		setLayout(new MigLayout("insets 0"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 

		label = new JLabel(text + " ");
		add(label); 
		// add(Box.createRigidArea(new Dimension(0,0)));
		this.comboBox = comboBox;
		add(comboBox);
		// add( Box.createHorizontalGlue() );
	}

	public JLabel getLabel() {
		return label;
	}

	public void setLabel(JLabel label) {
		this.label = label;
	}

	public JComboBox<?> getComboBox() {
		return comboBox;
	}

	public void setComboBox(JComboBox<?> comboBox) {
		this.comboBox = comboBox;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		label.setEnabled(enabled);
		comboBox.setEnabled(enabled);
	}
}