package optics.raytrace.GUI.lowLevel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.surfaces.MetricInterface;

/**
 * This panel allows editing of a symmetric 3x3 matrix.
 */
public class SymmetricMatrix3DPanel extends JPanel implements KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7641515402714033819L;
	private DoublePanel
		m11Panel, m12Panel, m13Panel,
		m21Panel, m22Panel, m23Panel,
		m31Panel, m32Panel, m33Panel;
	
	public SymmetricMatrix3DPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		
		m11Panel = new DoublePanel();
		m12Panel = new DoublePanel();
		m13Panel = new DoublePanel();
		m21Panel = new DoublePanel();
		m22Panel = new DoublePanel();
		m23Panel = new DoublePanel();
		m31Panel = new DoublePanel();
		m32Panel = new DoublePanel();
		m33Panel = new DoublePanel();
		
		// disable those panels that are just the same as others
		m21Panel.setEnabled(false);
		m31Panel.setEnabled(false);
		m32Panel.setEnabled(false);
		
		// add KeyListeners to the panels that get mirrored
		m12Panel.addKeyListener(this);
		m13Panel.addKeyListener(this);
		m23Panel.addKeyListener(this);

		// add the brackets
		add(new JLabel("("), "west");
		add(new JLabel(")"), "east");
		
		// add the top row
		add(m11Panel);
		add(new JLabel(","));
		add(m12Panel);
		add(new JLabel(","));
		add(m13Panel, "wrap");
		
		add(m21Panel);
		add(new JLabel(","));
		add(m22Panel);
		add(new JLabel(","));
		add(m23Panel, "wrap");

		add(m31Panel);
		add(new JLabel(","));
		add(m32Panel);
		add(new JLabel(","));
		add(m33Panel);
	}

	public void setMatrix3D(double[] m)
	{
		m11Panel.setNumber(m[MetricInterface._11]);
		m12Panel.setNumber(m[MetricInterface._12]);
		m13Panel.setNumber(m[MetricInterface._13]);
		m21Panel.setNumber(m[MetricInterface._12]);
		m22Panel.setNumber(m[MetricInterface._22]);
		m23Panel.setNumber(m[MetricInterface._23]);
		m31Panel.setNumber(m[MetricInterface._13]);
		m32Panel.setNumber(m[MetricInterface._23]);
		m33Panel.setNumber(m[MetricInterface._33]);
	}

	public double[] getMatrix3D()
	{
		double[] m = new double[6];
		m[MetricInterface._11] = m11Panel.getNumber();
		m[MetricInterface._12] = m12Panel.getNumber();
		m[MetricInterface._13] = m13Panel.getNumber();
		m[MetricInterface._22] = m22Panel.getNumber();
		m[MetricInterface._23] = m23Panel.getNumber();
		m[MetricInterface._33] = m33Panel.getNumber();
		return m;
	}
	
	private void mirrorSymmetricPanels()
	{
		try
		{
			m21Panel.setNumber(m12Panel.getNumber());
			m31Panel.setNumber(m13Panel.getNumber());
			m32Panel.setNumber(m23Panel.getNumber());
		}
		catch(NumberFormatException e)
		{
			// don't do anything, and hope that the user fixes things
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		mirrorSymmetricPanels();
	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		mirrorSymmetricPanels();
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		mirrorSymmetricPanels();
	}
}


