package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import optics.raytrace.GUI.sceneObjects.boxCloaks.CloakMaker;
import optics.raytrace.GUI.sceneObjects.boxCloaks.BoxCloakType;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable cloak, with the windows optionally framed.
 * The parameters a' (0<a'<1) and a (0<a<a') determine the scale of the structure inside the cloak;
 * any object inside the cloak appears demagnified by a factor a/a'.
 * For the Octahedral (piecewise affine) cloak, for example, a' and a is the corner-to-corner diameter of the central
 * octahedron in physical space and electromagnetic space, respectively, as a fraction of the diameter of the cloak
 * 
 * @author Johannes
 */
public class EditableBoxCloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 6630300427020860250L;

	// parameters
	protected Vector3D
		centre,
		frontDirection,	// direction from centre to front face; length irrelevant (size of cloak given by sideLength)
		rightDirection,	// direction from centre to right face; length irrelevant (size of cloak given by sideLength)
		topDirection;	// direction from centre to top face; length irrelevant (size of cloak given by sideLength)
	protected double
		sideLength,
		innerVolumeSizeFactorEM,	// corner-to-corner diameter of the central octahedron in electromagnetic space, as a fraction of the side length
		innerVolumeSizeFactorP,	// corner-to-corner diameter of the central octahedron in physical space, as a fraction of the side length
		interfaceTransmissionCoefficient,	// transmission coefficient of each interface
		frameRadiusOverSideLength;
	protected boolean showFrames, showPlaceholderSurfaces;	// , omitInnermostSurfaces;
	protected SurfaceProperty frameSurfaceProperty;
	protected BoxCloakType cloakType;
	
	// containers for the windows and frames
	protected EditableSceneObjectCollection windows, frames;
	
	// GUI panels
	protected LabelledVector3DPanel centreLine, frontDirectionLine, rightDirectionLine, topDirectionLine;
	protected LabelledDoublePanel sideLengthLine, innerVolumeSizeFactorEMLine, innerVolumeSizeFactorPLine, interfaceTransmissionCoefficientLine, frameRadiusOverSideLengthLine;
	protected JButton convertButton;
	protected JCheckBox showFramesCheckBox, showPlaceholderSurfacesCheckBox;	//, omitInnermostSurfacesCheckBox;
	protected JComboBox<BoxCloakType> cloakTypeComboBox;
	protected SurfacePropertyPanel frameSurfacePropertyPanel;
	
	
	//
	// constructors
	//
	
	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableBoxCloak(SceneObject parent, Studio studio)
	{
		this(
				"Box cloak",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.0,	// side length
				0.4,	// inner volume size in EM space
				0.8,	// inner volume size in physical space
				0.96,	// interface transmission coefficient
				BoxCloakType.CUBIC,	// cloak type
				false,	// show frames
				0.01,	// frame radius / side length
				SurfaceColour.RED_SHINY,	// frame surface property
				false,	// show placeholder surfaces
				parent,
				studio
			);
	}

	/**
	 * Standard constructor
	 * @param description
	 * @param centre
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 * @param sideLength
	 * @param innerVolumeSizeFactorEM
	 * @param innerVolumeSizeFactorP
	 * @param gCLAsTransmissionCoefficient
	 * @param cloakType
	 * @param showFrames
	 * @param frameRadiusOverSideLength
	 * @param frameSurfaceProperty
	 * @param showPlaceholderSurfaces
	 * @param parent
	 * @param studio
	 */
	public EditableBoxCloak(
			String description,
			Vector3D centre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double sideLength,
			double innerVolumeSizeFactorEM,
			double innerVolumeSizeFactorP,
			double gCLAsTransmissionCoefficient,
			BoxCloakType cloakType,
			// boolean omitInnermostSurfaces,
			boolean showFrames,
			double frameRadiusOverSideLength,
			SurfaceProperty frameSurfaceProperty,
			boolean showPlaceholderSurfaces,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setDirections(
				frontDirection,
				rightDirection,
				topDirection
			);
		setSideLength(sideLength);
		setInnerVolumeSizeFactorEM(innerVolumeSizeFactorEM);
		setInnerVolumeSizeFactorP(innerVolumeSizeFactorP);
		setInterfaceTransmissionCoefficient(gCLAsTransmissionCoefficient);
		setCloakType(cloakType);
		// setOmitInnermostSurfaces(omitInnermostSurfaces);
		setShowFrames(showFrames);
		setFrameRadiusOverSideLength(frameRadiusOverSideLength);
		setFrameSurfaceProperty(frameSurfaceProperty);
		setShowPlaceholderSurfaces(showPlaceholderSurfaces);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableBoxCloak(EditableBoxCloak original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getFrontDirection().clone(),
			original.getRightDirection().clone(),
			original.getTopDirection().clone(),
			original.getSideLength(),
			original.getInnerVolumeSizeFactorEM(),
			original.getInnerVolumeSizeFactorP(),
			original.getInterfaceTransmissionCoefficient(),
			original.getCloakType(),
			// original.isOmitInnermostSurfaces(),
			original.isShowFrames(),
			original.getFrameRadiusOverSideLength(),
			original.getFrameSurfaceProperty(),
			original.isShowPlaceholderSurfaces(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableBoxCloak clone()
	{
		return new EditableBoxCloak(this);
	}

	
	//
	// setters and getters
	//
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public NamedVector3D getFrontDirection() {
		return new NamedVector3D("front", frontDirection);
	}
	
	public NamedVector3D getBackDirection() {
		return new NamedVector3D("back", frontDirection.getReverse());
	}

	public NamedVector3D getRightDirection() {
		return new NamedVector3D("right", rightDirection);
	}
	
	public NamedVector3D getLeftDirection() {
		return new NamedVector3D("left", rightDirection.getReverse());
	}

	public NamedVector3D getTopDirection() {
		return new NamedVector3D("top", topDirection);
	}
	
	public NamedVector3D getBottomDirection() {
		return new NamedVector3D("bottom", topDirection.getReverse());
	}

	/**
	 * Set the direction to the front face to centreToCentreOfFrontFace,
	 * then set the direction to the right face to the part of centreToCentreOfRightFace that is perpendicular to the direction to the front face,
	 * then set the direction to the top face to the part of centreToCentreOfTopFace that is perpendicular to both the direction to the front face and to the right face.
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 */
	public void setDirections(
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection
		)
	{
		this.frontDirection = frontDirection.getNormalised();
		this.rightDirection = rightDirection.getPartPerpendicularTo(frontDirection).getNormalised();
		this.topDirection = topDirection.getPartParallelTo(
				Vector3D.crossProduct(this.frontDirection, this.rightDirection)
			).getNormalised();
	}

	public double getInnerVolumeSizeFactorEM() {
		return innerVolumeSizeFactorEM;
	}

	public void setInnerVolumeSizeFactorEM(double innerVolumeSizeFactorEM) {
		this.innerVolumeSizeFactorEM = innerVolumeSizeFactorEM;
	}

	public double getInnerVolumeSizeFactorP() {
		return innerVolumeSizeFactorP;
	}

	public void setInnerVolumeSizeFactorP(double innerVolumeSizeFactorP) {
		this.innerVolumeSizeFactorP = innerVolumeSizeFactorP;
	}
	
	public double getInterfaceTransmissionCoefficient() {
		return interfaceTransmissionCoefficient;
	}

	public void setInterfaceTransmissionCoefficient(double interfaceTransmissionCoefficient) {
		this.interfaceTransmissionCoefficient = interfaceTransmissionCoefficient;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
		
		setSceneObjectVisible(frames, showFrames);
	}
	
	public boolean isShowPlaceholderSurfaces() {
		return showPlaceholderSurfaces;
	}

	public void setShowPlaceholderSurfaces(boolean showPlaceholderSurfaces) {
		this.showPlaceholderSurfaces = showPlaceholderSurfaces;
	}

	public double getFrameRadiusOverSideLength()
	{
		return frameRadiusOverSideLength;
	}
	
	public void setFrameRadiusOverSideLength(double frameRadiusOverSideLength)
	{
		this.frameRadiusOverSideLength = frameRadiusOverSideLength;
	}
	
	public SurfaceProperty getFrameSurfaceProperty()
	{
		return frameSurfaceProperty;
	}
	
	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty)
	{
		this.frameSurfaceProperty = frameSurfaceProperty;
	}
	
	public BoxCloakType getCloakType() {
		return cloakType;
	}

	public void setCloakType(BoxCloakType cloakType) {
		this.cloakType = cloakType;
	}
	
	public EditableSceneObjectCollection getWindows() {
		return windows;
	}

	public EditableSceneObjectCollection getFrames() {
		return frames;
	}

	
	
	//
	// the important bit:  add the scene objects that form this box cloak
	//
	
	protected CloakMaker getBoxCloakMaker()
	{
		return cloakType.getCloakMaker(this);
	}
	
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	protected void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// prepare scene-object collection objects for the windows...
		windows = new EditableSceneObjectCollection("Windows", true, this, getStudio());
		
		// ... and the frames
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		// let the BoxCloakMaker add all required scene objects
		getBoxCloakMaker().addSceneObjects();

		// add the windows and the frames to this collection
		addSceneObject(windows);
		addSceneObject(frames, showFrames);
	}
	
	
	//
	// GUI stuff
	//
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Box cloak"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		cloakTypeComboBox = new JComboBox<BoxCloakType>(BoxCloakType.values());
		basicParametersPanel.add(GUIBitsAndBobs.makeRow("Cloak type", cloakTypeComboBox), "wrap");
		
		centreLine = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centreLine, "wrap");

		frontDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(frontDirectionLine, "wrap");

		rightDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(rightDirectionLine, "wrap");

		topDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(topDirectionLine, "wrap");

		basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		sideLengthLine = new LabelledDoublePanel("Side length");
		basicParametersPanel.add(sideLengthLine, "wrap");

		innerVolumeSizeFactorEMLine = new LabelledDoublePanel("Size of inner volume in EM space (as a fraction of side length)");
		basicParametersPanel.add(innerVolumeSizeFactorEMLine, "wrap");

		innerVolumeSizeFactorPLine = new LabelledDoublePanel("Size of inner volume in physical space (as a fraction of side length)");
		basicParametersPanel.add(innerVolumeSizeFactorPLine, "wrap");

		interfaceTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each interface");
		basicParametersPanel.add(interfaceTransmissionCoefficientLine, "wrap");

		showPlaceholderSurfacesCheckBox = new JCheckBox("Show placeholder surfaces");
		basicParametersPanel.add(showPlaceholderSurfacesCheckBox, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));

		showFramesCheckBox = new JCheckBox("Show frames");
		framePanel.add(showFramesCheckBox, "wrap");
		
		frameRadiusOverSideLengthLine = new LabelledDoublePanel("frame cylinder radius / side length");
		framePanel.add(frameRadiusOverSideLengthLine, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel, "wrap");
		frameSurfacePropertyPanel.setIPanel(iPanel);

		// omitInnermostSurfacesCheckBox = new JCheckBox("Simplified cloak (omit innermost interfaces)");
		// editPanel.add(omitInnermostSurfacesCheckBox);
		
		tabbedPane.addTab("Frames", framePanel);
		
		editPanel.add(tabbedPane, "wrap");

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
		
		centreLine.setVector3D(getCentre());
		frontDirectionLine.setVector3D(getFrontDirection());
		rightDirectionLine.setVector3D(getRightDirection());
		topDirectionLine.setVector3D(getTopDirection());
		sideLengthLine.setNumber(getSideLength());
		innerVolumeSizeFactorEMLine.setNumber(getInnerVolumeSizeFactorEM());
		innerVolumeSizeFactorPLine.setNumber(getInnerVolumeSizeFactorP());
		interfaceTransmissionCoefficientLine.setNumber(getInterfaceTransmissionCoefficient());
		cloakTypeComboBox.setSelectedItem(getCloakType());
		showFramesCheckBox.setSelected(isShowFrames());
		showPlaceholderSurfacesCheckBox.setSelected(isShowPlaceholderSurfaces());
		frameRadiusOverSideLengthLine.setNumber(getFrameRadiusOverSideLength());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableBoxCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setDirections(
				frontDirectionLine.getVector3D(),
				rightDirectionLine.getVector3D(),
				topDirectionLine.getVector3D()
			);
		setSideLength(sideLengthLine.getNumber());
		setInnerVolumeSizeFactorEM(innerVolumeSizeFactorEMLine.getNumber());
		setInnerVolumeSizeFactorP(innerVolumeSizeFactorPLine.getNumber());
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficientLine.getNumber());
		setCloakType((BoxCloakType)(cloakTypeComboBox.getSelectedItem()));
		setShowFrames(showFramesCheckBox.isSelected());
		setShowPlaceholderSurfaces(showPlaceholderSurfacesCheckBox.isSelected());
		setFrameRadiusOverSideLength(frameRadiusOverSideLengthLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());
		// setOmitInnermostSurfaces(omitInnermostSurfacesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-box-cloak");
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