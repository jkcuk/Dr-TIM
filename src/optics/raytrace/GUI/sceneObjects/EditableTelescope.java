package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.*;
import optics.raytrace.surfaces.Teleporting.TeleportationType;

/**
 * An editable window that acts like a telescope.
 * It uses the Teleporting surface property to re-scale the cross-section of the incoming beam.
 * 
 * @author Johannes
 */
public class EditableTelescope extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 3677860133292654840L;

	// parameters
	private Vector3D
		centre,
		ocularNormal;
	private double
		magnification,
		ocularRadius;	// objective radius = ocular radius * magnification

	// GUI panels
	private LabelledVector3DPanel centreLine, ocularNormalLine;
	private LabelledDoublePanel magnificationLine, ocularRadiusLine;
	private JButton convertButton;

	public EditableTelescope(
			String description,
			Vector3D centre,
			Vector3D ocularNormal,
			double magnification,
			double ocularRadius,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setOcularNormal(ocularNormal);
		setMagnification(magnification);
		setOcularRadius(ocularRadius);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableTelescope(EditableTelescope original)
	{
		super(original);
		
		// copy the original's parameters
		setCentre(original.getCentre());
		setOcularNormal(original.getOcularNormal());
		setMagnification(original.getMagnification());
		setOcularRadius(original.getOcularRadius());
	}

	@Override
	public EditableTelescope clone()
	{
		return new EditableTelescope(this);
	}

	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getOcularNormal() {
		return ocularNormal;
	}

	public void setOcularNormal(Vector3D ocularNormal) {
		this.ocularNormal = ocularNormal;
	}
	
	public double getMagnification()
	{
		return magnification;
	}
	
	public void setMagnification(double magnification)
	{
		this.magnification = magnification;
	}

	public double getOcularRadius() {
		return ocularRadius;
	}

	public void setOcularRadius(double ocularRadius) {
		this.ocularRadius = ocularRadius;
	}

	private void populateSceneObjectCollection()
	{
		// create the objects
		EditableScaledParametrisedDisc
			ocular = new EditableScaledParametrisedDisc(
					"ocular",
					Vector3D.sum(getCentre(), getOcularNormal().getWithLength(MyMath.TINY)),	// centre
					getOcularNormal(),	// normal
					getOcularRadius(),	// radius
					Vector3D.getANormal(getOcularNormal()),	// direction corresponding to phi = 0 (*)
					0, 1,	// scaled r range
					0, 1,	// scaled phi range
					null,	// surface property; will be set later
					getParent(),	// parent
					getStudio()	// studio
				),
				objective = new EditableScaledParametrisedDisc(
						"objective",
						Vector3D.sum(getCentre(), getOcularNormal().getWithLength(-1*MyMath.TINY)),	// centre
						getOcularNormal(),	// normal
						getOcularRadius() * getMagnification(),	// radius
						Vector3D.getANormal(getOcularNormal()),	// direction corresponding to phi = 0 (*)
						0, 1,	// scaled r range
						0, 1,	// scaled phi range
						null,	// surface property; will be set later
						getParent(),	// parent
						getStudio()	// studio
					);
				
		ocular.setSurfaceProperty(new EditableTwoSidedSurface(
				SurfaceColour.BLACK_MATT,	// inside
				new Teleporting(objective, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, TeleportationType.PERFECT, true),	// outside
				getStudio().getScene()
			));
		
		objective.setSurfaceProperty(new EditableTwoSidedSurface(
				new Teleporting(ocular, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, TeleportationType.PERFECT, true),	// inside
				SurfaceColour.BLACK_MATT,	// outside
				getStudio().getScene()
			));
		
		addSceneObject(ocular);
		addSceneObject(objective);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Polar-to-Cartesian converter"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		centreLine = new LabelledVector3DPanel("Centre");
		editPanel.add(centreLine, "wrap");
		
		ocularNormalLine = new LabelledVector3DPanel("Surface normal (outwards) for ocular side");
		editPanel.add(ocularNormalLine, "wrap");
				
		magnificationLine = new LabelledDoublePanel("Magnification");
		editPanel.add(magnificationLine, "wrap");

		ocularRadiusLine = new LabelledDoublePanel("Radius of ocular aperture");
		editPanel.add(ocularRadiusLine, "wrap");
		
		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		
		centreLine.setVector3D(getCentre());
		ocularNormalLine.setVector3D(getOcularNormal());
		magnificationLine.setNumber(getMagnification());
		ocularRadiusLine.setNumber(getOcularRadius());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableTelescope acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setOcularNormal(ocularNormalLine.getVector3D());
		setMagnification(magnificationLine.getNumber());
		setOcularRadius(ocularRadiusLine.getNumber());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-telescope");
		container.setValuesInEditPanel();
	}
}