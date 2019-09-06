package optics.raytrace.GUI.sceneObjects.transformations;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Vector3D;

import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.sceneObjects.transformations.LinearTransformation;
import optics.raytrace.sceneObjects.transformations.RotationAroundXAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundYAxis;
import optics.raytrace.sceneObjects.transformations.RotationAroundZAxis;
import optics.raytrace.sceneObjects.transformations.Translation;

/**
 * Control linear transformations of scene objects.
 * @author Johannes Courtial
 */
public class EditableLinearTransformation extends LinearTransformation
implements IPanelComponent, ActionListener
{
	private static final String
	TRANSFORMATION_TRANSLATION = "Translation",
	TRANSFORMATION_ROTATION = "Rotation",
	TRANSFORMATION_SCALING = "Scaling";
	
	protected JPanel editPanel;
	private JComboBox<String> transformationComboBox;
	private IPanel iPanel;
	private JButton transformButton;
	
	private JPanel optionalParameterPanel;
	private LabelledVector3DPanel translationPanel, scalingPanel;
	private RotationPanel rotationPanel;

	public EditableLinearTransformation()
	{
		super();
	}
	
	public EditableLinearTransformation(LinearTransformation transformation)
	{
		super(transformation);
	}
	
	@Override
	public EditableLinearTransformation clone()
	{
		return new EditableLinearTransformation(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;

		// create the edit panel for this SceneObjectContainer, which can contain one of two
		// elements: either the listPanel, which selects or manages individual objects in the
		// container, or the editPanel of one of the individual components
		editPanel = new JPanel();

		// add a border to the edit panel
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Transformation"));
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		editPanel.setLayout(gridbag);
	        
		// c.fill = GridBagConstraints.BOTH;
		
		String[] transformationStrings = { TRANSFORMATION_TRANSLATION, TRANSFORMATION_ROTATION, TRANSFORMATION_SCALING };
		
		transformationComboBox = new JComboBox<String>(transformationStrings);
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 0;
		gridbag.setConstraints(transformationComboBox, c);
		editPanel.add(GUIBitsAndBobs.makeRow("type", transformationComboBox));
		
		transformButton = new JButton("transform");
		transformButton.addActionListener(this);
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(transformButton, c);
		editPanel.add(transformButton);

		optionalParameterPanel = new JPanel();
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(optionalParameterPanel, c);
		editPanel.add(optionalParameterPanel);

		//
		// initialise the optional-parameter panels
		//

		translationPanel = new LabelledVector3DPanel("offset vector");
		translationPanel.setVector3D(new Vector3D(0,0,0));

		scalingPanel = new LabelledVector3DPanel("scaling factors in x, y, z");
		scalingPanel.setVector3D(new Vector3D(1, 1, 1));
		
		rotationPanel = new RotationPanel();
		rotationPanel.setAxisString(RotationPanel.AXIS_X);
		rotationPanel.setAngle(0);
		
		// validate the entire edit panel
		editPanel.validate();
	}
	
	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#getEditPanel()
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		transformationComboBox.setSelectedItem(TRANSFORMATION_TRANSLATION);
		setOptionalParameterPanelComponent(translationPanel);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableLinearTransformation acceptValuesInEditPanel()
	{
		String transformationString = (String)(transformationComboBox.getSelectedItem());
		
		if(transformationString.equals(TRANSFORMATION_TRANSLATION))
		{
			return new EditableLinearTransformation(new Translation(translationPanel.getVector3D()));
		}
		else if(transformationString.equals(TRANSFORMATION_ROTATION))
		{
			String axisString = rotationPanel.getAxisString();
			
			if(axisString.equals(RotationPanel.AXIS_X))
				return new EditableLinearTransformation(new RotationAroundXAxis(rotationPanel.getAngle()));
			else if(axisString.equals(RotationPanel.AXIS_Y))
				return new EditableLinearTransformation(new RotationAroundYAxis(rotationPanel.getAngle()));
			else if(axisString.equals(RotationPanel.AXIS_Z))
				return new EditableLinearTransformation(new RotationAroundZAxis(rotationPanel.getAngle()));
		}
		else if(transformationString.equals(TRANSFORMATION_SCALING))
		{
			return new EditableLinearTransformation(new Translation(translationPanel.getVector3D()));
		}

		return null;
	}
	
	@Override
	public void backToFront(IPanelComponent edited)
	{
	}

	private void setOptionalParameterPanelComponent(Component newComponent)
	{
		// remove any component currently in the optional-parameter panel
		while(optionalParameterPanel.getComponentCount() > 0) optionalParameterPanel.remove(0);

		// now add the new component
		if(newComponent != null) optionalParameterPanel.add(newComponent);

		if(iPanel != null)
		{
			// validate the enclosing panel
			iPanel.mainPanelChanged();
		}
	}


	/**
	 * A little inner class
	 */
	class RotationPanel extends JPanel
	{
		private static final long serialVersionUID = -5525054600920762087L;

		public static final String
		AXIS_X = "x axis",
		AXIS_Y = "y axis",
		AXIS_Z = "z axis";
		
		private JComboBox<String> axisComboBox;
		private LabelledDoublePanel rotationAnglePanel;

		public RotationPanel()
		{
			super();
			setLayout(new FlowLayout());

			String[] axisStrings = { AXIS_X, AXIS_Y, AXIS_Z };
			axisComboBox = new JComboBox<String>(axisStrings);
			axisComboBox.setSelectedItem(AXIS_X);
			add(GUIBitsAndBobs.makeRow("axis", axisComboBox));
			
			rotationAnglePanel = new LabelledDoublePanel("angle (degrees)");
			rotationAnglePanel.setNumber(0);
			add(rotationAnglePanel);
			
			validate();
		}

		public void setAxisString(String axisString)
		{
			axisComboBox.setSelectedItem(axisString);
		}

		public String getAxisString()
		{
			return (String)(axisComboBox.getSelectedItem());
		}
		
		public void setAngle(double angle)
		{
			rotationAnglePanel.setNumber(angle);
		}
		
		public double getAngle()
		{
			return rotationAnglePanel.getNumber();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == transformButton)
		{
			// TODO do the actual transformation!
		}
		else if(e.getSource() == transformationComboBox)
		{
			String transformationString = (String)(transformationComboBox.getSelectedItem());

			if(transformationString.equals(TRANSFORMATION_TRANSLATION))
			{
				setOptionalParameterPanelComponent(translationPanel);
			}
			else if(transformationString.equals(TRANSFORMATION_ROTATION))
			{
				setOptionalParameterPanelComponent(rotationPanel);
			}
			else if(transformationString.equals(TRANSFORMATION_SCALING))
			{
				setOptionalParameterPanelComponent(scalingPanel);
			}
		}
	}
}
