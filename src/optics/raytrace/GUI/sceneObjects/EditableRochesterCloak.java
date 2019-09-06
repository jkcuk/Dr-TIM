package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.boxCloaks.CloakMaker;
import optics.raytrace.GUI.sceneObjects.boxCloaks.BoxCloakType;
import optics.raytrace.GUI.sceneObjects.boxCloaks.RochesterCloakMaker;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable extended Rochester cloak, with the windows optionally framed.
 * The innerVolumeSizeFactorEM and innerVolumeSizeFactorP parameters determine the scale of the structure inside the cloak;
 * any object inside the cloak appears demagnified by a factor innerVolumeSizeFactorEM/innerVolumeSizeFactorP.
 * 
 * In order for the lenses to occur in the correct order, innerVolumeSizeFactorEM < -innerVolumeSizeFactorP,
 * i.e. objects inside the cloak appear magnified and flipped.
 * 
 * @author Johannes
 */
public class EditableRochesterCloak extends EditableBoxCloak
{
	private static final long serialVersionUID = -1947561260168113107L;


	//
	// constructors
	//
	
	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableRochesterCloak(SceneObject parent, Studio studio)
	{
		this(
				"Rochester cloak",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.0,	// side length
				-1.6,	// inner volume size in EM space
				0.8,	// inner volume size in physical space
				0.96,	// interface transmission coefficient
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
	 * @param parent
	 * @param studio
	 */
	public EditableRochesterCloak(
			String description,
			Vector3D centre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double sideLength,
			double innerVolumeSizeFactorEM,
			double innerVolumeSizeFactorP,
			double gCLAsTransmissionCoefficient,
			boolean showFrames,
			double frameRadiusOverSideLength,
			SurfaceProperty frameSurfaceProperty,
			boolean showPlaceholderSurfaces,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, frontDirection, rightDirection, topDirection, sideLength,
				innerVolumeSizeFactorEM, innerVolumeSizeFactorP, gCLAsTransmissionCoefficient, 
				null, showFrames,
				frameRadiusOverSideLength, frameSurfaceProperty, showPlaceholderSurfaces, parent, studio);
	}

	// should not be called
	@Override
	public BoxCloakType getCloakType() {
		return null;
	}

	// should not be called
	@Override
	public void setCloakType(BoxCloakType cloakType) {}


	
	@Override
	protected CloakMaker getBoxCloakMaker()
	{
		return new RochesterCloakMaker(this);
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Extended Rochester cloak"));
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
		basicParametersPanel.add(interfaceTransmissionCoefficientLine);

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
		// cloakTypeComboBox.setSelectedItem(getCloakType());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusOverSideLengthLine.setNumber(getFrameRadiusOverSideLength());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRochesterCloak acceptValuesInEditPanel()
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
		// setCloakType((BoxCloakType)(cloakTypeComboBox.getSelectedItem()));
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadiusOverSideLength(frameRadiusOverSideLengthLine.getNumber());
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
		iPanel.replaceFrontComponent(container, "Edit ex-Extended-Rochester-cloak");
		container.setValuesInEditPanel();
	}
}