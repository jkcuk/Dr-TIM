package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable Christmas tree.
 * 
 * @author Johannes
 */
public class EditableChristmasTree extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 2374783035862139347L;

	
	// parameters
	/**
	 * the position of the centre of the base of the trunk
	 */
	private Vector3D baseCentre;
	/**
	 * the position of the tip
	 */
	private Vector3D tip;
	/**
	 * inclination angle of branch layers, in degrees
	 */
	private double branchLayerInclinationAngle;
	/**
	 * the number of branch layers
	 */
	private int noOfBranchLayers;
	/**
	 * the radius of the trunk
	 */
	private double trunkRadius;
	/**
	 * the normalised direction to the front
	 */
	private Vector3D front;
	/**
	 * show baubles?
	 */
	private boolean showBaubles;
	/**
	 * show fairy lights?
	 */
	private boolean showFairyLights;
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection tree, baubles, fairyLights;
	
	// GUI panels
	private LabelledVector3DPanel baseCentrePanel, tipPanel, frontPanel;
	private LabelledDoublePanel branchLayerInclinationAnglePanel, trunkRadiusPanel;
	private LabelledIntPanel noOfBranchLayersPanel;
	private JButton convertButton;
	private JCheckBox showBaublesCheckBox, showFairyLightsCheckBox;
	
	
	//
	// constructors
	//
	
	public EditableChristmasTree(
			String description,
			Vector3D baseCentre,
			Vector3D tip,
			double branchLayerInclinationAngle,
			int noOfBranchLayers,
			double trunkRadius,
			Vector3D front,
			boolean showBaubles,
			boolean showFairyLights,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setBaseCentre(baseCentre);
		setTip(tip);
		setBranchLayerInclinationAngle(branchLayerInclinationAngle);
		setNoOfBranchLayers(noOfBranchLayers);
		setTrunkRadius(trunkRadius);
		setFront(front);
		setShowBaubles(showBaubles);
		setShowFairyLights(showFairyLights);

		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableChristmasTree(SceneObject parent, Studio studio)
	{
		this(
				"Christmas tree",	// description
				new Vector3D(0, -1, 10),	// base of trunk
				new Vector3D(0, 1, 10),	// tip
				35,	// branchLayerInclinationAngle
				5,	// noOfBranchLayers
				0.2,	// trunkRadius
				new Vector3D(0, 0, -1),	// front
				true,	// showBaubles
				true,	// showFairyLights
				parent, 
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableChristmasTree(EditableChristmasTree original)
	{
		this(
			original.getDescription(),
			original.getBaseCentre(),
			original.getTip(),
			original.getBranchLayerInclinationAngle(),
			original.getNoOfBranchLayers(),
			original.getTrunkRadius(),
			original.getFront(),
			original.isShowBaubles(),
			original.isShowFairyLights(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableChristmasTree clone()
	{
		return new EditableChristmasTree(this);
	}

	
	
	//
	// setters and getters
	//
	
	
	public Vector3D getBaseCentre() {
		return baseCentre;
	}

	public void setBaseCentre(Vector3D baseCentre) {
		this.baseCentre = baseCentre;
	}

	public Vector3D getTip() {
		return tip;
	}

	public void setTip(Vector3D tip) {
		this.tip = tip;
	}

	public double getBranchLayerInclinationAngle() {
		return branchLayerInclinationAngle;
	}

	public void setBranchLayerInclinationAngle(double branchLayerInclinationAngle) {
		this.branchLayerInclinationAngle = branchLayerInclinationAngle;
	}

	public int getNoOfBranchLayers() {
		return noOfBranchLayers;
	}

	public void setNoOfBranchLayers(int noOfBranchLayers) {
		this.noOfBranchLayers = noOfBranchLayers;
	}

	public double getTrunkRadius() {
		return trunkRadius;
	}

	public void setTrunkRadius(double trunkRadius) {
		this.trunkRadius = trunkRadius;
	}

	public boolean isShowBaubles() {
		return showBaubles;
	}

	public void setShowBaubles(boolean showBaubles) {
		this.showBaubles = showBaubles;
	}

	public boolean isShowFairyLights() {
		return showFairyLights;
	}

	public void setShowFairyLights(boolean showFairyLights) {
		this.showFairyLights = showFairyLights;
	}

	public NamedVector3D getFront() {
		return new NamedVector3D("front direction", front);
	}
	
	/**
	 * sets the direction to the front;
	 * the positions of the base centre and tip have to be set first!
	 * @param front
	 */
	public void setFront(Vector3D front) {
		this.front = front.getPartPerpendicularTo(Vector3D.difference(tip, baseCentre)).getNormalised();
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
		
		tree = new EditableSceneObjectCollection("Tree", true, this, getStudio());
		baubles = new EditableSceneObjectCollection("Baubles", true, this, getStudio());
		fairyLights = new EditableSceneObjectCollection("Fairy lights", true, this, getStudio());
		addSceneObject(tree);
		addSceneObject(baubles, showBaubles);
		addSceneObject(fairyLights, showFairyLights);
				
		// calculate the directions
		Vector3D up = Vector3D.difference(tip, baseCentre).getNormalised();
		// Vector3D right = Vector3D.crossProduct(up, front).getNormalised();

		// first calculate the height of the tree
		double height = Vector3D.getDistance(baseCentre, tip);

		// first add the trunk, which is a brown cone
		tree.addSceneObject(new EditableParametrisedCone(
					"trunk",	// description
					tip,	// apex
					up.getReverse(),	// axis
					false,	// open
					Math.atan2(trunkRadius, height),	// theta
					height,	// height
					SurfaceColour.BROWN_MATT,	// surfaceProperty
					tree,	// parent
					getStudio()
			));
		
		// add the branch layers, one by one
		for(int l=0; l<noOfBranchLayers; l++)
		{
			// calculate the height above the trunk base of the apex of the cone that represents the branch layer 
			double branchLayerTopHeight = height - 0.5*height*l/noOfBranchLayers;
			
			// calculate the height above the trunk base of the bottom of the cone
			double branchLayerBottomHeight = height - 0.8*height*(l+1)/noOfBranchLayers;
			
			tree.addSceneObject(new EditableParametrisedCone(
					"branch layer #"+l,	// description
					Vector3D.sum(baseCentre, up.getProductWith(branchLayerTopHeight)),	// apex
					up.getReverse(),	// axis
					false,	// open
					MyMath.deg2rad(90-branchLayerInclinationAngle),	// theta
					branchLayerTopHeight - branchLayerBottomHeight,	// height
					SurfaceColour.GREEN_MATT,	// surfaceProperty
					tree,	// parent
					getStudio()
			));
		}
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Christmas tree"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		baseCentrePanel = new LabelledVector3DPanel("Base centre position");
		editPanel.add(baseCentrePanel, "wrap");

		tipPanel = new LabelledVector3DPanel("Tip position");
		editPanel.add(tipPanel, "wrap");
		
		branchLayerInclinationAnglePanel = new LabelledDoublePanel("Inclination angle of branch layers (in degrees)");
		editPanel.add(branchLayerInclinationAnglePanel, "wrap");
		
		noOfBranchLayersPanel = new LabelledIntPanel("No of branch layers");
		editPanel.add(noOfBranchLayersPanel, "wrap");

		trunkRadiusPanel = new LabelledDoublePanel("Trunk radius (at the bottom)");
		editPanel.add(trunkRadiusPanel, "wrap");
		
		frontPanel = new LabelledVector3DPanel("Transverse direction 1");
		editPanel.add(frontPanel, "wrap");

		showBaublesCheckBox = new JCheckBox("Show baubles");
		editPanel.add(showBaublesCheckBox, "wrap");

		showFairyLightsCheckBox = new JCheckBox("Show fairy lights");
		editPanel.add(showFairyLightsCheckBox, "wrap");


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
		
		baseCentrePanel.setVector3D(getBaseCentre());
		tipPanel.setVector3D(getTip());
		branchLayerInclinationAnglePanel.setNumber(getBranchLayerInclinationAngle());
		noOfBranchLayersPanel.setNumber(getNoOfBranchLayers());
		trunkRadiusPanel.setNumber(getTrunkRadius());
		frontPanel.setVector3D(getFront());
		showBaublesCheckBox.setSelected(isShowBaubles());
		showFairyLightsCheckBox.setSelected(isShowFairyLights());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableChristmasTree acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setBaseCentre(baseCentrePanel.getVector3D());
		setTip(tipPanel.getVector3D());
		setBranchLayerInclinationAngle(branchLayerInclinationAnglePanel.getNumber());
		setNoOfBranchLayers(noOfBranchLayersPanel.getNumber());
		setTrunkRadius(trunkRadiusPanel.getNumber());
		setFront(frontPanel.getVector3D());
		setShowBaubles(showBaublesCheckBox.isSelected());
		setShowFairyLights(showFairyLightsCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-Christmas-tree");
		container.setValuesInEditPanel();
	}
}