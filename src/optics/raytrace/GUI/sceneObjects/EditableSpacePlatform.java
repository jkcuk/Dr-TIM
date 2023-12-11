package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;
import optics.raytrace.cameras.PinholeCamera;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable Camera.
 * 
 * @author Johannes
 */
public class EditableSpacePlatform extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -3922897970799563350L;

	// parameters
	/**
	 * the position of the camera's aperture centre
	 */
	private Vector3D cameraApertureCentre;
	
	/**
	 * the forward direction
	 */
	private Vector3D cameraViewDirection;

	/**
	 * the top direction
	 */
	private Vector3D topDirection;


	
	//
	// constructors
	//
	
	/**
	 * @param description
	 * @param cameraApertureCentre
	 * @param cameraViewDirection
	 * @param topDirection
	 * @param parent
	 * @param studio
	 */
	public EditableSpacePlatform(
			String description,
			Vector3D cameraApertureCentre,
			Vector3D cameraViewDirection,
			Vector3D topDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, false, parent, studio);
		
		// copy the parameters into this instance's variables
		this.cameraApertureCentre = cameraApertureCentre;
		this.cameraViewDirection = cameraViewDirection;
		this.topDirection = topDirection;

		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableSpacePlatform(SceneObject parent, Studio studio)
	{
		this(
				"Space platform",	// description
				new Vector3D(0, 0, 0),	// cameraApertureCentre
				new Vector3D(0, 0, 1),	// cameraViewDirection
				new Vector3D(0, 1, 0),	// topDirection
				parent, 
				studio
			);
	}
	
	public EditableSpacePlatform(
			PinholeCamera camera,
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
				parent, 
				studio
			);
	}
	

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSpacePlatform(EditableSpacePlatform original)
	{
		this(
			original.getDescription(),
			original.getCameraApertureCentre(),
			original.getCameraViewDirection(),
			original.getTopDirection(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableSpacePlatform clone()
	{
		return new EditableSpacePlatform(this);
	}

	
	
	//
	// setters and getters
	//
	
	public Vector3D getCameraApertureCentre() {
		return cameraApertureCentre;
	}

	public void setCameraApertureCentre(Vector3D cameraApertureCentre) {
		this.cameraApertureCentre = cameraApertureCentre;
	}

	public Vector3D getCameraViewDirection() {
		return cameraViewDirection;
	}

	public void setCameraViewDirection(Vector3D cameraViewDirection) {
		this.cameraViewDirection = cameraViewDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	public void setTopDirection(Vector3D topDirection) {
		this.topDirection = topDirection;
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
		
		Vector3D zHat = cameraViewDirection.getNormalised();
		Vector3D yHat = topDirection.getPartPerpendicularTo(zHat).getNormalised();
		Vector3D xHat = Vector3D.crossProduct(zHat, yHat).getNormalised();

		// the floor
		double floorY = -4;
		addSceneObject(new EditableScaledParametrisedParallelogram(
				"Floor",	// description, 
				Vector3D.sum(
						cameraApertureCentre, 
						xHat.getProductWith(-5),
						yHat.getProductWith(floorY),
						zHat.getProductWith(-2)
					),	// new Vector3D(-5, floorY, -2),	// corner, 
				xHat.getProductWith(10),	// spanVector1,
				zHat.getProductWith(10),	// spanVector2, 
				0,	// suMin,
				10,	// suMax,
				0,	// svMin,
				10,	// svMax,
				new EditableSurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, getStudio().getScene()),	// surfaceProperty,
				this,	// parent,
				getStudio()
				));
		// Tim's head
		addSceneObject(new EditableTimHead(
				"Tim's head",	// description,
				Vector3D.sum(
						cameraApertureCentre, 
						yHat.getProductWith(-2),
						zHat.getProductWith(5)
					),	// new Vector3D(0, -2, 5),	// centre,
				1,	// radius,
				zHat.getProductWith(-1),	// Vector3D(0, 0, -1),	// frontDirection,
				yHat,	// new Vector3D(0, 1, 0),	// topDirection,
				xHat,	// new Vector3D(1, 0, 0),	// rightDirection,
				this,	// parent, 
				getStudio()
				));
		// Tim's head's pedestal
		addSceneObject(new EditableCuboid(
				"Tim's head's pedestal",	// description,
				Vector3D.sum(
						cameraApertureCentre, 
						yHat.getProductWith(-3.5),
						zHat.getProductWith(5)
					),	// new Vector3D(0, -3.5, 5),	// centre,
				xHat,	// new Vector3D(1, 0, 0),	// centre2centreOfFace1,
				yHat.getProductWith(0.5),	// Vector3D(0, 0.5, 0),	// centre2centreOfFace2,
				zHat,	// new Vector3D(0, 0, 1),	// centre2centreOfFace3,
				SurfaceColour.GREY20_MATT,	// surfaceProperty,
				this,	// parent,
				getStudio()
				));
		
		// camera
		EditableCameraShape cameraShape = new EditableCameraShape(
				"Camera",	// description
				Vector3D.sum(
						cameraApertureCentre, 
						zHat.getProductWith(-0.01)
					),	// new Vector3D(0, 0, -0.01),	// apertureCentre
				zHat,	// new Vector3D(0, 0, 1),	// forwardDirection
				yHat,	// new Vector3D(0, 1, 0),	// topDirection
				1,	// width
				SurfaceColour.LIGHT_RED_MATT,	// surfacePropertyBody
				SurfaceColour.LIGHT_RED_MATT,	// surfacePropertyLens
				null,	// surfacePropertyGlass
				this,	// parent,
				getStudio()
			);
		addSceneObject(cameraShape);
		
		// tripod
		double tripodFootprintRadius = 1;
		EditableSceneObjectCollection tripod = new EditableSceneObjectCollection("Tripod", false, this, getStudio());
		addSceneObject(tripod);
		Vector3D tripodAttachmentPoint = cameraShape.getTripodAttachmentPoint();
		Vector3D tripodBaseCentre = Geometry.getPointOnPlaneClosestToPoint(
				Vector3D.sum(
						cameraApertureCentre, 
						yHat.getProductWith(floorY)
					),	// pointOnPlane
				yHat,	// planeNormal
				tripodAttachmentPoint	// point
			);
		for(int i=1; i<=3; i++)
		{
			tripod.addSceneObject(
					new EditableParametrisedCylinder(
							"Tripod leg "+i,	// description
							tripodAttachmentPoint,	// startPoint
							Vector3D.sum(
									tripodBaseCentre,
									xHat.getProductWith(tripodFootprintRadius*Math.cos(2*Math.PI*i/3.)),
									zHat.getProductWith(tripodFootprintRadius*Math.sin(2*Math.PI*i/3.))
								),	// endPoint
							0.05,	// radius,
							SurfaceColour.GREY90_SHINY,	// surfaceProperty
							tripod,	// parent
							getStudio()
					)
				);
		}

	}
	
	
	
	//
	// GUI stuff
	//

	// GUI panels
	private LabelledVector3DPanel cameraApertureCentrePanel, cameraViewDirectionPanel, topDirectionPanel;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Space platform"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		cameraApertureCentrePanel = new LabelledVector3DPanel("Camera aperture centre");
		editPanel.add(cameraApertureCentrePanel, "wrap");

		cameraViewDirectionPanel = new LabelledVector3DPanel("Camera view direction");
		editPanel.add(cameraViewDirectionPanel, "wrap");
		
		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		editPanel.add(topDirectionPanel, "wrap");
		

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
		
		cameraApertureCentrePanel.setVector3D(getCameraApertureCentre());
		cameraViewDirectionPanel.setVector3D(getCameraViewDirection());
		topDirectionPanel.setVector3D(getTopDirection());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSpacePlatform acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCameraApertureCentre(cameraApertureCentrePanel.getVector3D());
		setCameraViewDirection(cameraViewDirectionPanel.getVector3D());
		setTopDirection(topDirectionPanel.getVector3D());

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