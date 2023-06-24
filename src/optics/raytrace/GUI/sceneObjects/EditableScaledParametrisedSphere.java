package optics.raytrace.GUI.sceneObjects;

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
import optics.raytrace.sceneObjects.ScaledParametrisedSphere;


/**
 * A sphere.
 * 
 * @author Johannes, Dean et al.
 *
 */
public class EditableScaledParametrisedSphere extends ScaledParametrisedSphere implements IPanelComponent
{
	private static final long serialVersionUID = 7127930825442857785L;

	protected JPanel editPanel, basicParametersPanel, parametrisationPanel;
	protected LabelledStringPanel descriptionPanel;
	protected LabelledVector3DPanel centrePanel;
	protected LabelledDoublePanel radiusPanel;
	protected SurfacePropertyPanel surfacePropertyPanel;
	
	// panels in the parametrisation panel
	protected LabelledVector3DPanel northPolePanel, phi0DirectionPanel;
	protected LabelledMinMaxPanel sThetaMinMaxPanel, sPhiMinMaxPanel;


	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param pole
	 * @param phi0Direction
	 * @param sThetaMin
	 * @param sThetaMax
	 * @param sPhiMin
	 * @param sPhiMax
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedSphere(
			String description,
			Vector3D centre,
			double radius,
			Vector3D pole,
			Vector3D phi0Direction,
			double sThetaMin, double sThetaMax,
			double sPhiMin, double sPhiMax,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, centre, radius, pole, phi0Direction, sThetaMin, sThetaMax, sPhiMin, sPhiMax, surfaceProperty, parent, studio);
	}
	
	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedSphere(
			String description,
			Vector3D centre,
			double radius,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		this(	description,
				centre,
				radius,
				new Vector3D(0, 0, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				surfaceProperty, parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableScaledParametrisedSphere(EditableScaledParametrisedSphere original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableScaledParametrisedSphere clone()
	{
		return new EditableScaledParametrisedSphere(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		// the editPanel shows either the editSpherePanel or the editDetailsPanel
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Sphere"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the sphere's basic parameters
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
		
		northPolePanel = new LabelledVector3DPanel("Zenith direction");
		parametrisationPanel.add(northPolePanel, "wrap");
				
		phi0DirectionPanel = new LabelledVector3DPanel("Direction of azimuth axis");
		parametrisationPanel.add(phi0DirectionPanel, "wrap");
		
		sThetaMinMaxPanel = new LabelledMinMaxPanel("scaled theta range");
		parametrisationPanel.add(sThetaMinMaxPanel, "wrap");
		
		sPhiMinMaxPanel = new LabelledMinMaxPanel("scaled phi range");
		parametrisationPanel.add(sPhiMinMaxPanel, "wrap");
		
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
		radiusPanel.setNumber(getRadius());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		northPolePanel.setVector3D(getPole());
		phi0DirectionPanel.setVector3D(getPhi0Direction());
		sThetaMinMaxPanel.setMin(getSThetaMin());
		sThetaMinMaxPanel.setMax(getSThetaMax());
		sPhiMinMaxPanel.setMin(getSPhiMin());
		sPhiMinMaxPanel.setMax(getSPhiMax());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedSphere acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		setDirections(
				northPolePanel.getVector3D(),
				phi0DirectionPanel.getVector3D()
			);
		
		setThetaScaling(sThetaMinMaxPanel.getMin(), sThetaMinMaxPanel.getMax());
		setPhiScaling(sPhiMinMaxPanel.getMin(), sPhiMinMaxPanel.getMax());

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
	public EditableScaledParametrisedSphere transform(Transformation t)
	{
		return new EditableScaledParametrisedSphere(
				getDescription(),
				t.transformPosition(getCentre()),
				getRadius(),
				t.transformDirection(getPole()),
				t.transformDirection(getPhi0Direction()),
				getSThetaMin(), getSThetaMax(),
				getSPhiMin(), getSPhiMax(),
				getSurfaceProperty(),
				getParent(), 
				getStudio()
			);
	}

}
