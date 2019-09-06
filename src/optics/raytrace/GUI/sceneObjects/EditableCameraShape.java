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
import optics.raytrace.cameras.PinholeCamera;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;

/**
 * An editable Camera.
 * 
 * @author Johannes
 */
public class EditableCameraShape extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 8921667509476778230L;

	// parameters
	/**
	 * the position of the aperture centre
	 */
	private Vector3D apertureCentre;
	
	/**
	 * the forward direction
	 */
	private Vector3D forwardDirection;

	/**
	 * the top direction
	 */
	private Vector3D topDirection;

	/**
	 * the radius of the trunk
	 */
	private double width;
	
	/**
	 * surface property of the camera body and viewfinder
	 */
	private SurfaceProperty surfacePropertyBody;

	/**
	 * surface property of the camera lens
	 */
	private SurfaceProperty surfacePropertyLens;
	
	/**
	 * if null it is possible to look into the viewfinder and out of the aperture (and vice versa);
	 * if not null,  the surface property of the viewfinder and aperture will be set to the given surface property
	 */
	private SurfaceProperty surfacePropertyGlass;

	
	//
	// constructors
	//
	
	public EditableCameraShape(
			String description,
			Vector3D apertureCentre,
			Vector3D forwardDirection,
			Vector3D topDirection,
			double width,
			SurfaceProperty surfacePropertyBody,
			SurfaceProperty surfacePropertyLens,
			SurfaceProperty surfacePropertyGlass,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureCentre = apertureCentre;
		this.forwardDirection = forwardDirection;
		this.topDirection = topDirection;
		this.width = width;
		this.surfacePropertyBody = surfacePropertyBody;
		this.surfacePropertyLens = surfacePropertyLens;
		this.surfacePropertyGlass = surfacePropertyGlass;

		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableCameraShape(SceneObject parent, Studio studio)
	{
		this(
				"Camera",	// description
				new Vector3D(0, 0, 10),	// apertureCentre
				new Vector3D(0, 0, -1),	// forwardDirection
				new Vector3D(0, 1, 0),	// topDirection
				1,	// width
				SurfaceColour.GREY50_MATT,	// surfacePropertyBody
				SurfaceColour.GREY30_MATT,	// surfacePropertyLens
				null,	// surfacePropertyGlass
				parent, 
				studio
			);
	}
	
	public EditableCameraShape(
			PinholeCamera camera,
			double width,
			SurfaceProperty surfacePropertyBody,
			SurfaceProperty surfacePropertyLens,
			SurfaceProperty surfacePropertyGlass,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Camera",	// description
				Vector3D.sum(
						camera.getPinholePosition(),
						camera.getViewDirection().getWithLength(-MyMath.TINY)
					),	// apertureCentre
				camera.getViewDirection(),	// forwardDirection
				camera.getVerticalSpanVector(),	// topDirection
				width,	// width
				surfacePropertyBody,
				surfacePropertyLens,
				surfacePropertyGlass,
				parent, 
				studio
			);
	}
	

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCameraShape(EditableCameraShape original)
	{
		this(
			original.getDescription(),
			original.getApertureCentre(),
			original.getForwardDirection(),
			original.getTopDirection(),
			original.getWidth(),
			original.getSurfacePropertyBody(),
			original.getSurfacePropertyLens(),
			original.getSurfacePropertyGlass(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableCameraShape clone()
	{
		return new EditableCameraShape(this);
	}

	
	
	//
	// setters and getters
	//
	
	public Vector3D getApertureCentre() {
		return apertureCentre;
	}

	public void setApertureCentre(Vector3D apertureCentre) {
		this.apertureCentre = apertureCentre;
	}

	public Vector3D getForwardDirection() {
		return forwardDirection;
	}

	public void setForwardDirection(Vector3D forwardDirection) {
		this.forwardDirection = forwardDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	public void setTopDirection(Vector3D topDirection) {
		this.topDirection = topDirection;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public SurfaceProperty getSurfacePropertyBody() {
		return surfacePropertyBody;
	}

	public void setSurfacePropertyBody(SurfaceProperty surfacePropertyBody) {
		this.surfacePropertyBody = surfacePropertyBody;
	}

	public SurfaceProperty getSurfacePropertyLens() {
		return surfacePropertyLens;
	}

	public void setSurfacePropertyLens(SurfaceProperty surfacePropertyLens) {
		this.surfacePropertyLens = surfacePropertyLens;
	}

	public SurfaceProperty getSurfacePropertyGlass() {
		return surfacePropertyGlass;
	}

	public void setSurfacePropertyGlass(SurfaceProperty surfacePropertyGlass) {
		this.surfacePropertyGlass = surfacePropertyGlass;
	}

	
	
	//
	// the important bit:  add the scene objects that form this Christmas tree
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// calculate the directions
		Vector3D front = forwardDirection.getNormalised();
		Vector3D up = topDirection.getPartPerpendicularTo(front).getNormalised();
		Vector3D right = Vector3D.crossProduct(front, up);	// should be normalised already

		double bodyHeight = 0.6*width;
		double bodyDepth = 0.3*width;
		double lensLength = 0.25*width;
		double lensRadius = 0.35*bodyHeight;
		double viewFinderWidth = 0.2*width;
		double viewFinderHeight = 0.15*width;
		
		addSceneObject(
				new EditableCuboid(
						"Body",	// description
						Vector3D.sum(apertureCentre, front.getProductWith(-lensLength-0.5*bodyDepth)),	// centre
						front.getProductWith(0.5*bodyDepth),	// centre2centreOfFace1
						right.getProductWith(0.5*width),	// centre2centreOfFace2
						up.getProductWith(0.5*bodyHeight),	// centre2centreOfFace3
						surfacePropertyBody,	// surfaceProperty
						this,	// parent
						getStudio()
			));

		addSceneObject(
				new EditableCuboid(
						"Viewfinder",	// description
						Vector3D.sum(apertureCentre, front.getProductWith(-lensLength-0.5*bodyDepth), up.getProductWith(0.5*(bodyHeight+viewFinderHeight))),	// centre
						front.getProductWith(0.5*bodyDepth),	// centre2centreOfFace1
						right.getProductWith(0.5*viewFinderWidth),	// centre2centreOfFace2
						up.getProductWith(0.5*viewFinderHeight),	// centre2centreOfFace3
						surfacePropertyBody,	// surfaceProperty
						this,	// parent
						getStudio()
			));

		addSceneObject(
				new EditableParametrisedCylinder(
						"Lens cylinder",	// description
						apertureCentre,	// startPoint
						Vector3D.sum(apertureCentre, front.getProductWith(-lensLength)),	// endPoint
						lensRadius,	// radius
						false,	// showEndCaps
						surfacePropertyLens,	// surfaceProperty
						this,	// parent
						getStudio()	// studio
				));

		addSceneObject(
				new EditableScaledParametrisedDisc(
						"Shutter",	// description
						apertureCentre,	// centre
						front,	// normal
						lensRadius,	// radius
						right,	// xDirection
						up,	// yDirection
						DiscParametrisationType.CARTESIAN,	// discParametrisationType
						0,	// suMin
						1,	// suMax
						0,	// svMin
						1,	// svMax
						SurfaceColourLightSourceIndependent.BLACK,	// surfaceProperty
						this,	// parent
						getStudio()	// studio
				));

		// construct a teleporting viewfinder - aperture opening
		double viewFinderOpeningRadius = 0.4*Math.min(viewFinderWidth, viewFinderHeight);
		double apertureOpeningRadius = 0.6*lensRadius;

		EditableScaledParametrisedDisc viewFinderOpening =
				new EditableScaledParametrisedDisc(
						"Viewfinder opening",	// description
						Vector3D.sum(apertureCentre, front.getProductWith(-lensLength-bodyDepth-MyMath.TINY), up.getProductWith(0.5*(bodyHeight+viewFinderHeight))),	// centre
						front,	// normal
						viewFinderOpeningRadius,	// radius
						right,	// xDirection
						up,	// yDirection
						DiscParametrisationType.CARTESIAN,	// discParametrisationType
						0,	// suMin
						1,	// suMax
						0,	// svMin
						1,	// svMax
						null,	// surfaceProperty
						this,	// parent
						getStudio()	// studio
				);

		EditableScaledParametrisedDisc apertureOpening =
				new EditableScaledParametrisedDisc(
						"Aperture opening",	// description
						Vector3D.sum(apertureCentre, front.getProductWith(MyMath.TINY)),	// centre
						front,	// normal
						apertureOpeningRadius,	// radius
						right,	// xDirection
						up,	// yDirection
						DiscParametrisationType.CARTESIAN,	// discParametrisationType
						0,	// suMin
						1,	// suMax
						0,	// svMin
						1,	// svMax
						null,	// surfaceProperty
						this,	// parent
						getStudio()	// studio
				);

		if(surfacePropertyGlass != null)
		{
			viewFinderOpening.setSurfaceProperty(surfacePropertyGlass);
			apertureOpening.setSurfaceProperty(surfacePropertyGlass);
		}
		else
		{
			Teleporting viewFinderOpeningSurface = new Teleporting(
					apertureOpening,	// destinationObject
					1,	// teleportationCoefficient
					TeleportationType.PERFECT,	// teleportationType
					true	// shadowThrowing
					);

			Teleporting apertureOpeningSurface = new Teleporting(
					viewFinderOpening,	// destinationObject
					1,	// teleportationCoefficient
					TeleportationType.PERFECT,	// teleportationType
					true	// shadowThrowing
					);

			viewFinderOpening.setSurfaceProperty(viewFinderOpeningSurface);
			apertureOpening.setSurfaceProperty(apertureOpeningSurface);
		}
		
		addSceneObject(viewFinderOpening);
		addSceneObject(apertureOpening);
	}
	
		
	
	
	//
	// GUI stuff
	//

	// GUI panels
	private LabelledVector3DPanel apertureCentrePanel, forwardDirectionPanel, topDirectionPanel;
	private LabelledDoublePanel widthPanel;
	private JButton convertButton;
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		apertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		editPanel.add(apertureCentrePanel, "wrap");

		forwardDirectionPanel = new LabelledVector3DPanel("Forward direction");
		editPanel.add(forwardDirectionPanel, "wrap");
		
		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		editPanel.add(topDirectionPanel, "wrap");

		widthPanel = new LabelledDoublePanel("Body width");
		editPanel.add(widthPanel, "wrap");
		

		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton);
		
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
		
		apertureCentrePanel.setVector3D(getApertureCentre());
		forwardDirectionPanel.setVector3D(getForwardDirection());
		topDirectionPanel.setVector3D(getTopDirection());
		widthPanel.setNumber(getWidth());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCameraShape acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setApertureCentre(apertureCentrePanel.getVector3D());
		setForwardDirection(forwardDirectionPanel.getVector3D());
		setTopDirection(topDirectionPanel.getVector3D());
		setWidth(widthPanel.getNumber());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-lens");
		container.setValuesInEditPanel();
	}
}