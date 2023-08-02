package optics.raytrace.GUI.sceneObjects;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledMinMaxPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.ScaledParametrisedDisc;

public class EditableScaledParametrisedDisc extends ScaledParametrisedDisc implements IPanelComponent
{
	private static final long serialVersionUID = -907181850778551453L;
	
	/**
	 * @param description
	 * @param centre
	 * @param normal
	 * @param radius
	 * @param xDirection
	 * @param yDirection
	 * @param discParametrisationType
	 * @param suMin
	 * @param suMax
	 * @param svMin
	 * @param svMax
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			Vector3D yDirection,
			DiscParametrisationType discParametrisationType,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, xDirection, yDirection, discParametrisationType, suMin, suMax, svMin, svMax, surfaceProperty, parent, studio);
	}

	/**
	 * @param description
	 * @param centre
	 * @param normal
	 * @param radius
	 * @param xDirection
	 * @param discParametrisationType
	 * @param suMin
	 * @param suMax
	 * @param svMin
	 * @param svMax
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			DiscParametrisationType discParametrisationType,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, xDirection, discParametrisationType, suMin, suMax, svMin, svMax, surfaceProperty, parent, studio);
	}

	/**
	 * A disc, parametrised by polar coordinates
	 * @param description
	 * @param centre
	 * @param normal
	 * @param radius
	 * @param xDirection
	 * @param suMin
	 * @param suMax
	 * @param svMin
	 * @param svMax
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, xDirection, suMin, suMax, svMin, svMax, surfaceProperty, parent, studio);
	}

	/**
	 * A disc, parametrised by polar coordinates.
	 * This constructor that picks random directions
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param surfaceProperty	surface properties
	 */
	public EditableScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableScaledParametrisedDisc(EditableScaledParametrisedDisc original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedDisc#clone()
	 */
	@Override
	public EditableScaledParametrisedDisc clone()
	{
		return new EditableScaledParametrisedDisc(this);
	}
	
	
	
	private JPanel editPanel, basicParametersPanel, parametrisationPanel;
	
	// panels in the basic-parameters panel
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel, normalPanel;
	private LabelledDoublePanel radiusPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	
	// panels in the parametrisation panel
	private JComboBox<DiscParametrisationType> discParametrisationTypeComboBox;
	private LabelledVector3DPanel xDirectionPanel;
	private LabelledMinMaxPanel suMinMaxPanel, svMinMaxPanel;


	

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Disc"));

		JTabbedPane tabbedPane = new JTabbedPane();

		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		normalPanel = new LabelledVector3DPanel("Normal to disc (pointing \"outwards\")");
		basicParametersPanel.add(normalPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		radiusPanel = new LabelledDoublePanel("Radius");
		basicParametersPanel.add(radiusPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		basicParametersPanel.add(surfacePropertyPanel);
		surfacePropertyPanel.setIPanel(iPanel);
		
		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		
		//
		// the parametrisation panel
		// 
		parametrisationPanel = new JPanel();
		parametrisationPanel.setLayout(new MigLayout("insets 0"));
		
		discParametrisationTypeComboBox = new JComboBox<DiscParametrisationType>(DiscParametrisationType.values());
		parametrisationPanel.add(GUIBitsAndBobs.makeRow("Parametrisation", discParametrisationTypeComboBox), "wrap");

		xDirectionPanel = new LabelledVector3DPanel("direction corresponding to phi=0 degree");
		parametrisationPanel.add(xDirectionPanel, "wrap");
		
		suMinMaxPanel = new LabelledMinMaxPanel("scaled r range");
		parametrisationPanel.add(suMinMaxPanel, "wrap");
		
		svMinMaxPanel = new LabelledMinMaxPanel("scaled phi range");
		parametrisationPanel.add(svMinMaxPanel, "wrap");
				
		tabbedPane.addTab("Parametrisation", parametrisationPanel);

		editPanel.add(tabbedPane);

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
		// initialize any fields
		descriptionPanel.setString(getDescription());
		centrePanel.setVector3D(getCentre());
		normalPanel.setVector3D(getNormal());
		radiusPanel.setNumber(getRadius());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		
		discParametrisationTypeComboBox.setSelectedItem(discParametrisationType);
		xDirectionPanel.setVector3D(getXDirection());
		suMinMaxPanel.setMin(getSUMin());
		suMinMaxPanel.setMax(getSUMax());
		svMinMaxPanel.setMin(getSVMin());
		svMinMaxPanel.setMax(getSVMax());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedDisc acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setNormal(normalPanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		discParametrisationType = (DiscParametrisationType)(discParametrisationTypeComboBox.getSelectedItem());
		setDirections(xDirectionPanel.getVector3D());
		setUScaling(suMinMaxPanel.getMin(), suMinMaxPanel.getMax());
		setVScaling(svMinMaxPanel.getMin(), svMinMaxPanel.getMax());
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(edited instanceof SurfaceProperty)
		{
			// the surface property has been edited
			setSurfaceProperty((SurfaceProperty)edited);
			surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		}
	}
	
	@Override
	public EditableScaledParametrisedDisc transform(Transformation t)
	{
		return new EditableScaledParametrisedDisc(
				getDescription(),
				t.transformPosition(getCentre()),
				t.transformDirection(getNormal()),
				getRadius(),
				t.transformDirection(getXDirection()),
				t.transformDirection(getYDirection()),
				getDiscParametrisationType(),
				getSUMin(), getSUMax(),
				getSVMin(), getSVMax(),
				getSurfaceProperty(),
				getParent(), 
				getStudio()
			);
	}
}
