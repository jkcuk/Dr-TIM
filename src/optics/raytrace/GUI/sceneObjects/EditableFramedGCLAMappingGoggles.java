package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Editable gCLA goggles that map a square so that its centre is avoided, with the windows optionally framed.
 * 
 * @author Johannes, Lena Mertens
 */
public class EditableFramedGCLAMappingGoggles extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -5448340304922665506L;

	// parameters
	private Vector3D
		eyePosition,	// position of the centre of the pupil for which these goggles are designed
		baseCentre,	// position of the centre of the base of the square pyramid; centre of distortion of view
		right,	// one of the vectors that span the base of the square pyramid
		up;	// the other vector that spans the base of the square pyramid
	private double
		baseCentre2ApexDistance,
		baseSideLength,
		viewCompressionFactor,
		gCLAsTransmissionCoefficient,
		frameRadius;
	private boolean showFrames;	// , omitInnermostSurfaces;
	private SurfaceProperty frameSurfaceProperty;

	// GUI panels
	private LabelledVector3DPanel eyePositionLine, baseCentreLine, rightLine, upLine;
	private LabelledDoublePanel baseCentre2ApexDistanceLine, baseSideLengthLine, viewCompressionFactorLine, gCLAsTransmissionCoefficientLine, frameRadiusLine;
	private JButton convertButton;
	private JCheckBox showFramesCheckBox;	//, omitInnermostSurfacesCheckBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	
	/**
	 * Minimal constructor, assumes default parameters
	 * @param parent
	 * @param studio
	 */
	public EditableFramedGCLAMappingGoggles(SceneObject parent, Studio studio)
	{
		this(
				"View-mapping goggles",
				new Vector3D(0, 0, 0),	// centre of pupil
				new Vector3D(0, 0, 5),	// base centre
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// up direction
				1.0,	// baseCentre2ApexDistance
				1.0,	// baseSideLength
				0.5,	// viewCompressionFactor
				0.96,	// gCLA transmission coefficient
				true,	// show frames
				0.01,	// frame radius
				SurfaceColour.GREY50_SHINY,	// frame surface property
				parent,
				studio
			);
	}

	/**
	 * Full constructor
	 * @param description
	 * @param eyePosition
	 * @param baseCentre
	 * @param right
	 * @param up
	 * @param baseCentre2ApexDistance
	 * @param baseSideLength
	 * @param viewCompressionFactor
	 * @param gCLAsTransmissionCoefficient
	 * @param showFrames
	 * @param frameRadius
	 * @param frameSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableFramedGCLAMappingGoggles(
			String description,
			Vector3D eyePosition,	// position of the centre of the pupil for which these goggles are designed
			Vector3D baseCentre,	// position of the centre of the base of the square pyramid; centre of distortion of view
			Vector3D right,	// one of the vectors that span the base of the square pyramid
			Vector3D up,	// the other vector that spans the base of the square pyramid
			double baseCentre2ApexDistance,
			double baseSideLength,
			double viewCompressionFactor,
			double gCLAsTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setEyePosition(eyePosition);
		setBaseCentre(baseCentre);
		setRightUp(right, up);
		setBaseCentre2ApexDistance(baseCentre2ApexDistance);
		setBaseSideLength(baseSideLength);
		setViewCompressionFactor(viewCompressionFactor);
		setgCLAsTransmissionCoefficient(gCLAsTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFramedGCLAMappingGoggles(EditableFramedGCLAMappingGoggles original)
	{
		this(
			original.getDescription(),
			original.getEyePosition().clone(),
			original.getBaseCentre().clone(),
			original.getRight().clone(),
			original.getUp().clone(),
			original.getBaseCentre2ApexDistance(),
			original.getBaseSideLength(),
			original.getViewCompressionFactor(),
			original.getgCLAsTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableFramedGCLAMappingGoggles clone()
	{
		return new EditableFramedGCLAMappingGoggles(this);
	}

	
	//
	// setters and getters
	//
	
	public Vector3D getEyePosition() {
		return eyePosition;
	}

	public void setEyePosition(Vector3D eyePosition) {
		this.eyePosition = eyePosition;
	}

	public Vector3D getBaseCentre() {
		return baseCentre;
	}

	public void setBaseCentre(Vector3D baseCentre) {
		this.baseCentre = baseCentre;
	}

	public Vector3D getRight() {
		return right;
	}

	public Vector3D getUp() {
		return up;
	}

	public void setRightUp(Vector3D right, Vector3D up)
	{
		this.right = right.getNormalised();
		this.up = up.getPartPerpendicularTo(this.right).getNormalised();
	}

	public double getBaseCentre2ApexDistance() {
		return baseCentre2ApexDistance;
	}

	public void setBaseCentre2ApexDistance(double baseCentre2ApexDistance) {
		this.baseCentre2ApexDistance = baseCentre2ApexDistance;
	}

	public double getBaseSideLength() {
		return baseSideLength;
	}

	public void setBaseSideLength(double baseSideLength) {
		this.baseSideLength = baseSideLength;
	}

	public double getViewCompressionFactor() {
		return viewCompressionFactor;
	}

	public void setViewCompressionFactor(double viewCompressionFactor) {
		this.viewCompressionFactor = viewCompressionFactor;
	}

	public double getgCLAsTransmissionCoefficient() {
		return gCLAsTransmissionCoefficient;
	}

	public void setgCLAsTransmissionCoefficient(double gCLAsTransmissionCoefficient) {
		this.gCLAsTransmissionCoefficient = gCLAsTransmissionCoefficient;
	}

	public double getFrameRadius() {
		return frameRadius;
	}

	public void setFrameRadius(double frameRadius) {
		this.frameRadius = frameRadius;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}


	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}


	// the various points
	protected Vector3D
		p1,	// apex of square pyramid
		p2,	// top left corner of base of square pyramid
		p3, // top right corner of base of square pyramid
		p4,	// bottom right corner of base of square pyramid
		p5,	// bottom left corner of base of square pyramid
		p6,	// inner corner of top distorted triangle
		p7,	// inner corner of right distorted triangle
		p8,	// inner corner of bottom distorted triangle
		p9,	// inner corner of left distorted triangle
		p10,	// centre of base of square pyramid
		p11;	// centre of pupil

	// containers for the windows and frames
	EditableSceneObjectCollection windows, frames;


	private void addWindows(Vector3D triangleInnerCorner, Vector3D triangleOuterCorner1, Vector3D triangleOuterCorner2)
	{
		// the top outer window images the centre of the base, point 10,
		// to the bottom corner of the top triangle in the base plane, point 6
		Vector3D
			u = Vector3D.difference(triangleOuterCorner2, triangleOuterCorner1),	// the vector from point 2 to point 3
			v = Vector3D.sum(p1, triangleOuterCorner1.getProductWith(-0.5), triangleOuterCorner2.getProductWith(-0.5));	// from half-way between points 2 and 3 to point 1
		HomogeneousPlanarImagingSurface s1 = new HomogeneousPlanarImagingSurface(
				p1,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v, 
				p10, triangleInnerCorner	// object and image positions
			);
		// System.out.println("s1 = " + s1);
		
		// add the corresponding window
		windows.addSceneObject(new EditableTriangle(
				"upper triangular face of square prism",
				p1,	// corner 1
				Vector3D.difference(triangleOuterCorner1, p1),	// corner 1 to corner 3
				Vector3D.difference(triangleOuterCorner2, p1),	// corner 1 to corner 2
				false,	// not semi-infinite
				s1.getuHat(),	// unit vector in u direction
				s1.getvHat(),	// unit vector in v direction
				s1.toGCLAs(),	// surface property
				windows,
				getStudio()
			));
		
		// surface s1 images the eye position to an intermediate position, i
		Vector3D i = s1.getImagePosition(p11);
		
		// surface s2 images this intermediate position back to the eye position
		v = Vector3D.sum(triangleInnerCorner, triangleOuterCorner1.getProductWith(-0.5), triangleOuterCorner2.getProductWith(-0.5));	// from half-way between points 2 and 3 to point 1
		HomogeneousPlanarImagingSurface s2 = new HomogeneousPlanarImagingSurface(
				triangleOuterCorner1,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v,
				i, p11	// object and image positions
			);
		// System.out.println("s2 = " + s2);

		// add the corresponding window
		windows.addSceneObject(new EditableTriangle(
				"upper triangle in base plane of square prism",
				triangleInnerCorner,	// corner 1
				Vector3D.difference(triangleOuterCorner1, triangleInnerCorner),	// corner 1 to corner 3
				Vector3D.difference(triangleOuterCorner2, triangleInnerCorner),	// corner 1 to corner 2
				false,	// not semi-infinite
				s2.getuHat(),	// unit vector in u direction
				s2.getvHat(),	// unit vector in v direction
				s2.toGCLAs(),	// surface property
				windows,
				getStudio()
			));
	}

	private void addWindows()
	{
		addWindows(p6, p2, p3);	// top triangle
		addWindows(p7, p3, p4);	// right triangle
		addWindows(p8, p4, p5);	// bottom triangle
		addWindows(p9, p5, p2);	// left triangle
		
		// (new Exception("What's going on?")).StackTrace();
	}

	
	private void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableScaledParametrisedSphere(
				description,
				centrePosition,	// centre
				frameRadius,	// radius
				frameSurfaceProperty,
				collection,
				getStudio()
		));
	}
	
	private void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableParametrisedCylinder(
				description,
				startPosition,	// start point
				endPosition,	// end point
				frameRadius,	// radius
				frameSurfaceProperty,
				collection,
				getStudio()
		));
	}
	
	private void addFrames()
	{
		// the spheres at the corners
		addFrameSphere("apex of square pyramid", p1, frames);
		addFrameSphere("top left corner of base of square pyramid", p2, frames);
		addFrameSphere("top right corner of base of square pyramid", p3, frames);
		addFrameSphere("bottom right corner of base of square pyramid", p4, frames);
		addFrameSphere("bottom left corner of base of square pyramid", p5, frames);
		addFrameSphere("inner corner of top distorted triangle", p6, frames);
		addFrameSphere("inner corner of right distorted triangle", p7, frames);
		addFrameSphere("inner corner of bottom distorted triangle", p8, frames);
		addFrameSphere("inner corner of left distorted triangle", p9, frames);
		// addFrameSphere("centre of base of square pyramid", p10, frames);
		
		// the cylidners between corners
		addFrameCylinder("apex to top left corner", p1, p2, frames);
		addFrameCylinder("apex to top right corner", p1, p3, frames);
		addFrameCylinder("apex to bottom right corner", p1, p4, frames);
		addFrameCylinder("apex to bottom left corner", p1, p5, frames);
		addFrameCylinder("top of square base", p2, p3, frames);
		addFrameCylinder("right of square base", p3, p4, frames);
		addFrameCylinder("bottom of square base", p4, p5, frames);
		addFrameCylinder("left of square base", p5, p2, frames);
		addFrameCylinder("inner left side of top triangle", p2, p6, frames);
		addFrameCylinder("inner right side of top triangle", p6, p3, frames);
		addFrameCylinder("inner top side of right triangle", p3, p7, frames);
		addFrameCylinder("inner bottom side of right triangle", p7, p4, frames);
		addFrameCylinder("inner right side of bottom triangle", p4, p8, frames);
		addFrameCylinder("inner left side of bottom triangle", p8, p5, frames);
		addFrameCylinder("inner bottom side of left triangle", p5, p9, frames);
		addFrameCylinder("inner top side of left triangle", p9, p2, frames);
	}

	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// prepare scene-object collection objects for the windows...
		windows = new EditableSceneObjectCollection("Windows", true, this, getStudio());
		
		// ... and the frames
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		// pre-calculate the vertices
		double w2 = 0.5*baseSideLength;	// half the base side length
		p1 = Vector3D.sum(baseCentre, Vector3D.difference(baseCentre, eyePosition).getWithLength(baseCentre2ApexDistance));	// apex of square pyramid
		p2 = Vector3D.sum(baseCentre, right.getWithLength(-w2), up.getWithLength(w2));	// top left corner of base of square pyramid
		p3 = Vector3D.sum(baseCentre, right.getWithLength(w2), up.getWithLength(w2)); // top right corner of base of square pyramid
		p4 = Vector3D.sum(baseCentre, right.getWithLength(w2), up.getWithLength(-w2));	// bottom right corner of base of square pyramid
		p5 = Vector3D.sum(baseCentre, right.getWithLength(-w2), up.getWithLength(-w2));	// bottom left corner of base of square pyramid
		p6 = Vector3D.sum(baseCentre, up.getWithLength((1-viewCompressionFactor)*w2));	// inner corner of top distorted triangle
		p7 = Vector3D.sum(baseCentre, right.getWithLength((1-viewCompressionFactor)*w2));	// inner corner of right distorted triangle
		p8 = Vector3D.sum(baseCentre, up.getWithLength(-(1-viewCompressionFactor)*w2));	// inner corner of bottom distorted triangle
		p9 = Vector3D.sum(baseCentre, right.getWithLength(-(1-viewCompressionFactor)*w2));	// inner corner of left distorted triangle
		p10 = baseCentre;	// centre of base of square pyramid
		p11 = eyePosition;	// centre of pupil

		// calculate the windows and the frames
		addWindows();
		addFrames();
		
		// add the windows and the frames to this collection
		addSceneObject(windows);
		addSceneObject(frames, showFrames);
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

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cubic gCLA cloak"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		eyePositionLine = new LabelledVector3DPanel("Eye position");
		basicParametersPanel.add(eyePositionLine, "wrap");

		baseCentreLine = new LabelledVector3DPanel("Centre of base of distorting pyramid");
		basicParametersPanel.add(baseCentreLine, "wrap");

		rightLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(rightLine, "wrap");

		upLine = new LabelledVector3DPanel("Upwards direction");
		basicParametersPanel.add(upLine, "wrap");

		baseCentre2ApexDistanceLine = new LabelledDoublePanel("Distance from Centre of base of distorting pyramid to apex");
		basicParametersPanel.add(baseCentre2ApexDistanceLine, "wrap");

		baseSideLengthLine = new LabelledDoublePanel("Side length of base of distorting pyramid");
		basicParametersPanel.add(baseSideLengthLine, "wrap");

		viewCompressionFactorLine = new LabelledDoublePanel("View compression factor");
		basicParametersPanel.add(viewCompressionFactorLine, "wrap");

		gCLAsTransmissionCoefficientLine = new LabelledDoublePanel("transmission coefficient of each surface");
		basicParametersPanel.add(gCLAsTransmissionCoefficientLine);

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));

		showFramesCheckBox = new JCheckBox("Show frames");
		framePanel.add(showFramesCheckBox, "wrap");
		
		frameRadiusLine = new LabelledDoublePanel("frame cylinder radius");
		framePanel.add(frameRadiusLine, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel);
		frameSurfacePropertyPanel.setIPanel(iPanel);

		// omitInnermostSurfacesCheckBox = new JCheckBox("Simplified cloak (omit innermost interfaces)");
		// editPanel.add(omitInnermostSurfacesCheckBox);
		
		tabbedPane.addTab("Frames", framePanel);
		
		editPanel.add(tabbedPane);

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
		
		eyePositionLine.setVector3D(getEyePosition());
		baseCentreLine.setVector3D(getBaseCentre());
		rightLine.setVector3D(getRight());
		upLine.setVector3D(getUp());
		baseCentre2ApexDistanceLine.setNumber(getBaseCentre2ApexDistance());
		baseSideLengthLine.setNumber(getBaseSideLength());
		viewCompressionFactorLine.setNumber(getViewCompressionFactor());
		gCLAsTransmissionCoefficientLine.setNumber(getgCLAsTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableFramedGCLAMappingGoggles acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setEyePosition(eyePositionLine.getVector3D());
		setBaseCentre(baseCentreLine.getVector3D());
		setRightUp(rightLine.getVector3D(), upLine.getVector3D());
		setBaseCentre2ApexDistance(baseCentre2ApexDistanceLine.getNumber());
		setBaseSideLength(baseSideLengthLine.getNumber());
		setViewCompressionFactor(viewCompressionFactorLine.getNumber());
		setgCLAsTransmissionCoefficient(gCLAsTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-CLA cloak");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				setFrameSurfaceProperty((SurfaceProperty)edited);
				frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
			}
	}
}