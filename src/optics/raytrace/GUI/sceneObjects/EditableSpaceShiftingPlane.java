package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Geometry;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Plane;

/**
 * A plane that offsets each light ray passing through it such that every point is imaged and there is a constant shift between object and image space.
 * @author johannes
 */
public class EditableSpaceShiftingPlane extends Plane
implements IPanelComponent
{
	private static final long serialVersionUID = 8242137661436320188L;

	/**
	 * the offset of image space relative to object space
	 */
	private Vector3D imageSpaceOffset;

	// GUI variables
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel pointOnPlanePanel, normalPanel, imageSpaceOffsetPanel;

	/**
	 * Creates a space-shifting plane.
	 * 
	 * @param description
	 * @param pointOnPlane
	 * @param normal
	 * @param imageSpaceOffset
	 * @param parent
	 * @param studio
	 */
	public EditableSpaceShiftingPlane(
			String description,
			Vector3D pointOnPlane,
			Vector3D normal,
			Vector3D imageSpaceOffset,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				description,
				pointOnPlane,
				normal, 
				null,	// surfaceProperty
				parent,
				studio
			);
		
		this.imageSpaceOffset = imageSpaceOffset;
	}
	
	public EditableSpaceShiftingPlane(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Space-shifting plane",	// description
				new Vector3D(0, 0, 10),	// pointOnPlane
				new Vector3D(0, 0, 1),	// normal
				new Vector3D(0, 0, 0),	// imageSpaceOffset
				parent,
				studio
			);
	}

	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSpaceShiftingPlane(EditableSpaceShiftingPlane original)
	{
		this(
				original.getDescription(),
				original.getPointOnPlane(),
				original.getNormal(),
				original.getImageSpaceOffset(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableSpaceShiftingPlane clone()
	{
		return new EditableSpaceShiftingPlane(this);
	}
	
	
	//
	// setters & getters
	//
	
	public Vector3D getImageSpaceOffset() {
		return imageSpaceOffset;
	}

	public void setImageSpaceOffset(Vector3D imageSpaceOffset) {
		this.imageSpaceOffset = imageSpaceOffset;
	}
	

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Plane"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		pointOnPlanePanel = new LabelledVector3DPanel("Point on the plane");
		editPanel.add(pointOnPlanePanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		normalPanel = new LabelledVector3DPanel("Normal to plane (pointing \"outwards\")");
		editPanel.add(normalPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		imageSpaceOffsetPanel = new LabelledVector3DPanel("Offset between object and image space");
		editPanel.add(imageSpaceOffsetPanel, "wrap");

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
		pointOnPlanePanel.setVector3D(getPointOnPlane());
		normalPanel.setVector3D(getNormal());
		imageSpaceOffsetPanel.setVector3D(getImageSpaceOffset());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSpaceShiftingPlane acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setPointOnPlane(pointOnPlanePanel.getVector3D());
		setNormal(normalPanel.getVector3D());
		setImageSpaceOffset(imageSpaceOffsetPanel.getVector3D());
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}
	
	
	//
	// the interesting bits
	//
	
	/**
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	public boolean isShadowThrowing() {
		return false;
	}
	
	/**
	 * Calculates the colour a specific incoming light ray would "see" if it hits the primitive scene object
	 * at a specific intersection point.
	 * 
	 * @param r	incoming light ray
	 * @param i	intersection between incoming light ray and primitive scene object
	 * @param scene	scene object(s) making up the scene to be rendered
	 * @param l	light source(s) illuminating the scene
	 * @param traceLevel	recursion limit
	 * @return	colour under which intersection is seen
	 */
	@Override
	public DoubleColour getColourAtIntersection(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// the incident ray intersects the plane at i.p; the outgoing ray leaves the plane with the same direction as the incident ray (r.getD()) from
		// a position such that it passes through the position i.p + imageSpaceOffset

		// original version:
		
//		// the ray is a backwards-traced ray, and so it is moving from image space to object space, which is why <imageSpaceOffset> must be subtracted from i.p
//		// to get the pass-through position
//		Vector3D passThroughPosition = Vector3D.difference(i.p, imageSpaceOffset);
//		// System.out.println("EditableSpaceShiftingPlane::getColourAtIntersection: passThroughPosition="+passThroughPosition);
//		
//		Vector3D newStartPosition = Geometry.uniqueLinePlaneIntersection(
//				passThroughPosition,	// pointOnLine
//				r.getD(),	// directionOfLine
//				getPointOnPlane(),	// point on plane
//				getNormal()	// normalToPlane
//			);
//		// System.out.println("EditableSpaceShiftingPlane::getColourAtIntersection: newStartPosition="+newStartPosition);
		
		// new version:
		Vector3D newStartPosition = Vector3D.difference(
				Geometry.uniqueLinePlaneIntersection(
						i.p,	// pointOnLine
						r.getD(),	// directionOfLine
						Vector3D.sum(getPointOnPlane(), imageSpaceOffset),	// pointOnPlane
						getNormal()	// normalToPlane
						),
				imageSpaceOffset
				);

		return scene.getColourAvoidingOrigin(
						r.getBranchRay(
								newStartPosition,
								r.getD(),
								i.t,	// not sure what to do with this -- just leave the time unchanged
								r.isReportToConsole()
								),
						i.o,
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
			);
	}

}
