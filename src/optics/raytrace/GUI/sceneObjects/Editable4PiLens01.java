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
import optics.raytrace.GUI.sceneObjects.boxCloaks.NamedVector3D;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.GlensHologram;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable 4Pi lens which completely surrounds "inside space", which is the tetrahedral cell
 * immediately next to an ideal-lens-cloak's base lens.
 * 
 * In fact, this class is based on the EditableIdealLensCloak, but with the following differences:
 * 	* The focal length is that from inside space to outside space, which is minus that of the base lens.
 * 	* Including the base lens is optional.
 * 	* What used to be called "base centre" is now called the "Principal point"
 * 	* The directions have been re-named.  The optical-axis direction is the 
 * 	* 
 * 
 * @author Johannes
 */
public class Editable4PiLens01 extends EditableIdealLensCloak
{
	private static final long serialVersionUID = 7807779069772958981L;


	// parameters
	
	/**
	 * the focal length when imaging from inside space to outside space
	 */
	protected double focalLengthInside2Outside;
	
	/**
	 * Should the base lens be included?
	 * With it, this is a 4 Pi lens identical to an ideal-lens cloak whose base-lens focal length is
	 * minus the 4 Pi lens's (inside-to-outside) focal length;
	 * without it, it is equivalent to a lens whose focal length is the 4 Pi lens's (inside-to-outside) focal length.
	 */
	protected boolean includeBaseLens;
		
	/**
	 * The direction of the optical axis.
	 * This is the same as the "top" direction of the ideal-lens cloak
	 */
	protected Vector3D opticalAxisDirection;
	
	/**
	 * The first of the two transverse directions.
	 * This is equivalent to the "right" direction of the ideal-lens cloak
	 */
	protected Vector3D transverseDirection1;
	
	/**
	 * The second of the two transverse directions.
	 * This is equivalent to the "front" direction of the ideal-lens cloak
	 */
	protected Vector3D transverseDirection2;

	// GUI panels
	protected LabelledDoublePanel focalLengthLine;
	protected JCheckBox includeBaseLensCheckBox;
	protected LabelledVector3DPanel opticalAxisDirectionLine, transversDirection1Line, transverseDirection2Line;

	
	//
	// constructors
	//
	
	

	
	//
	// setters and getters
	//
	
	public double getFocalLengthInside2Outside() {
		return focalLengthInside2Outside;
	}

	public void setFocalLengthInside2Outside(double focalLengthInside2Outside) {
		this.focalLengthInside2Outside = focalLengthInside2Outside;
	}
	
	public Vector3D getPrincipalPoint() {
		return baseCentre;
	}
	
	public void setPrincipalPoint(Vector3D principalPoint)
	{
		baseCentre = principalPoint;
	}

	public boolean isIncludeBaseLens() {
		return includeBaseLens;
	}

	public void setIncludeBaseLens(boolean includeBaseLens) {
		this.includeBaseLens = includeBaseLens;
	}

	public Vector3D getOpticalAxisDirection() {
		return opticalAxisDirection;
	}

	public void setOpticalAxisDirection(Vector3D opticalAxisDirection) {
		this.opticalAxisDirection = opticalAxisDirection;
	}

	public Vector3D getTransverseDirection1() {
		return transverseDirection1;
	}

	public void setTransverseDirection1(Vector3D transverseDirection1) {
		this.transverseDirection1 = transverseDirection1;
	}

	public Vector3D getTransverseDirection2() {
		return transverseDirection2;
	}

	public void setTransverseDirection2(Vector3D transverseDirection2) {
		this.transverseDirection2 = transverseDirection2;
	}

	public double getHeightLowerInnerVertexP() {
		return heightLowerInnerVertexP;
	}

	public void setHeightLowerInnerVertexP(double heightLowerInnerVertexP) {
		this.heightLowerInnerVertexP = heightLowerInnerVertexP;
	}

	public double getHeightUpperInnerVertexP() {
		return heightUpperInnerVertexP;
	}

	public void setHeightUpperInnerVertexP(double heightUpperInnerVertexP) {
		this.heightUpperInnerVertexP = heightUpperInnerVertexP;
	}

	public double getHeightLowerInnerVertexE() {
		return heightLowerInnerVertexE;
	}

	public void setHeightLowerInnerVertexE(double heightLowerInnerVertexE) {
		this.heightLowerInnerVertexE = heightLowerInnerVertexE;
	}


	
	
	//
	// the important bit:  add the scene objects that form this box cloak
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	@Override
	public void populateSceneObjectCollection()
	{
		// first set the directions of the superclass
		setDirections(transverseDirection1, transverseDirection2, opticalAxisDirection);
		
		// then call the superclass's populateSceneObjectCollection method
		super.populateSceneObjectCollection();
	}
	
	
	/**
	 * add the base lens;
	 * returns the focal length of the base lens
	 */
	@Override
	protected double addBaseLens()
	{
		addLens(
				"LBar",	// description
				baseLeftVertex,	// vertex 1
				baseRightVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				baseCentre,	// nodalPoint
				-focalLengthInside2Outside,	// focal length
				outerLenses,	// collection
				includeBaseLens
			);
		
		return -focalLengthInside2Outside;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("4Pi lens"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the 4Pi-lens panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		baseCentreLine = new LabelledVector3DPanel("Principal point");
		basicParametersPanel.add(baseCentreLine, "wrap");

		frontDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(frontDirectionLine, "wrap");

		rightDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(rightDirectionLine, "wrap");

		topDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(topDirectionLine, "wrap");

		// basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		baseRadiusLine = new LabelledDoublePanel("Base radius");
		basicParametersPanel.add(baseRadiusLine, "wrap");

		heightLine = new LabelledDoublePanel("Height");
		basicParametersPanel.add(heightLine, "wrap");

		heightLowerInnerVertexPLine = new LabelledDoublePanel("Height of lower inner vertex above base in physical space");
		basicParametersPanel.add(heightLowerInnerVertexPLine, "wrap");

		heightUpperInnerVertexPLine = new LabelledDoublePanel("Height of upper inner vertex above base in physical space");
		basicParametersPanel.add(heightUpperInnerVertexPLine, "wrap");

		heightLowerInnerVertexELine = new LabelledDoublePanel("Height of lower inner vertex above base in EM space");
		basicParametersPanel.add(heightLowerInnerVertexELine, "wrap");

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
		
		frameRadiusLine = new LabelledDoublePanel("frame cylinder radius");
		framePanel.add(frameRadiusLine, "wrap");
		
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
		
		baseCentreLine.setVector3D(getBaseCentre());
		frontDirectionLine.setVector3D(getFrontDirection());
		rightDirectionLine.setVector3D(getRightDirection());
		topDirectionLine.setVector3D(getTopDirection());
		baseRadiusLine.setNumber(getBaseRadius());
		heightLine.setNumber(getHeight());
		heightLowerInnerVertexPLine.setNumber(getHeightLowerInnerVertexP());
		heightUpperInnerVertexPLine.setNumber(getHeightUpperInnerVertexP());
		heightLowerInnerVertexELine.setNumber(getHeightLowerInnerVertexE());
		interfaceTransmissionCoefficientLine.setNumber(getInterfaceTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		showPlaceholderSurfacesCheckBox.setSelected(isShowPlaceholderSurfaces());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public Editable4PiLens01 acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setBaseCentre(baseCentreLine.getVector3D());
		setDirections(
				frontDirectionLine.getVector3D(),
				rightDirectionLine.getVector3D(),
				topDirectionLine.getVector3D()
			);
		setHeight(heightLine.getNumber());
		setBaseRadius(baseRadiusLine.getNumber());
		setHeightLowerInnerVertexP(heightLowerInnerVertexPLine.getNumber());
		setHeightUpperInnerVertexP(heightUpperInnerVertexPLine.getNumber());
		setHeightLowerInnerVertexE(heightLowerInnerVertexELine.getNumber());
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setShowPlaceholderSurfaces(showPlaceholderSurfacesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());
		// setOmitInnermostSurfaces(omitInnermostSurfacesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-lens-TO-tetrahedron");
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