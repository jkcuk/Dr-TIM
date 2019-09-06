package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
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
import optics.raytrace.sceneObjects.ScaledParametrisedDisc;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectDifference;
import math.*;

/**
 * A framed circular window
 * @author Johannes Courtial
 */
public class EditableFramedDisc extends EditableSceneObjectCollection implements ActionListener
{

	/**
	 * stores all parameters associated with the disc, and the disc itself
	 */
	private ScaledParametrisedDisc disc;

	// parameters stored in disc
//	private Vector3D centre;
//	private Vector3D normal;
//	private double radius;
//	private Vector3D direction1;
//	private Vector3D direction2;
//	private DiscParametrisationType discParametrisationType;
//	private double suMin; private double suMax;
//	private double svMin; private double svMax;
//	private SurfaceProperty discSurfaceProperty;
	private double frameRadius;
	private SurfaceProperty frameSurfaceProperty;
	private boolean showFrame;

	
	public EditableFramedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D direction1,
			Vector3D direction2,
			DiscParametrisationType discParametrisationType,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty discSurfaceProperty,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			boolean showFrame,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, false, parent, studio);
		
		// create a disc according to the parameters
		disc = new ScaledParametrisedDisc(
				"Disc",	// description
				centre,
				normal,
				radius,
				direction1,
				direction2,
				discParametrisationType,
				suMin, suMax,
				svMin, svMax,
				discSurfaceProperty,
				this,	// parent
				studio
		);
		
		this.frameRadius = frameRadius;
		this.frameSurfaceProperty = frameSurfaceProperty;
		this.showFrame = showFrame;

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFramedDisc(EditableFramedDisc original)
	{
		super(original);
		
		// copy the original's parameters
		this.disc = original.disc.clone();
		this.frameRadius = original.frameRadius;
		this.frameSurfaceProperty = original.frameSurfaceProperty;
		this.showFrame = original.showFrame;
	}

	@Override
	public EditableFramedDisc clone()
	{
		return new EditableFramedDisc(this);
	}
	
	
	// setters & getters

	public ScaledParametrisedDisc getDisc() {
		return disc;
	}

	public void setDisc(ScaledParametrisedDisc disc) {
		this.disc = disc;
	}

	public Vector3D getCentre() {
		return disc.getCentre();
	}

	public void setCentre(Vector3D centre) {
		disc.setCentre(centre);
	}

	public Vector3D getNormal() {
		return disc.getNormal();
	}

	public void setNormal(Vector3D normal) {
		disc.setNormal(normal);
	}

	public double getRadius() {
		return disc.getRadius();
	}

	public void setRadius(double radius) {
		disc.setRadius(radius);
	}

	public Vector3D getDirection1() {
		return disc.getXDirection();
	}

	public Vector3D getDirection2() {
		return disc.getYDirection();
	}

	public void setDirections(Vector3D direction1, Vector3D direction2) {
		disc.setDirections(direction1, direction2);
	}

	public DiscParametrisationType getDiscParametrisationType() {
		return disc.getDiscParametrisationType();
	}

	public void setDiscParametrisationType(DiscParametrisationType discParametrisationType) {
		disc.setDiscParametrisationType(discParametrisationType);
	}

	public double getSuMin() {
		return disc.getSUMin();
	}

	public double getSuMax() {
		return disc.getSUMax();
	}

	public void setUScaling(double suMin, double suMax) {
		disc.setUScaling(suMin, suMax);
	}

	public double getSvMin() {
		return disc.getSVMin();
	}

	public double getSvMax() {
		return disc.getSVMax();
	}

	public void setVScaling(double svMin, double svMax) {
		disc.setVScaling(svMin, svMax);
	}

	public SurfaceProperty getDiscSurfaceProperty() {
		return disc.getSurfaceProperty();
	}

	public void setDiscSurfaceProperty(SurfaceProperty discSurfaceProperty) {
		disc.setSurfaceProperty(discSurfaceProperty);
	}

	public double getFrameRadius() {
		return frameRadius;
	}

	public void setFrameRadius(double frameRadius) {
		this.frameRadius = frameRadius;
	}

	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}

	public boolean isShowFrame() {
		return showFrame;
	}

	public void setShowFrame(boolean showFrame) {
		this.showFrame = showFrame;
	}

	


	// the meat
	
	private void populateSceneObjectCollection()
	{
		clear();
		
		// add the disc
		addSceneObject(disc);
		
		// create the frame
		EditableParametrisedCylinder frameOutside = new EditableParametrisedCylinder(
				"Outside",	// description
				Vector3D.sum(disc.getCentre(), disc.getNormal().getWithLength( 0.5*frameRadius)),	// startPoint
				Vector3D.sum(disc.getCentre(), disc.getNormal().getWithLength(-0.5*frameRadius)),	// endPoint
				disc.getRadius() + frameRadius,	// radius
				frameSurfaceProperty,
				this,
				getStudio()
		);
		EditableParametrisedCylinder frameInside = new EditableParametrisedCylinder(
				"Inside",	// description
				Vector3D.sum(disc.getCentre(), disc.getNormal().getWithLength( frameRadius)),	// startPoint
				Vector3D.sum(disc.getCentre(), disc.getNormal().getWithLength(-frameRadius)),	// endPoint
				disc.getRadius(),	// radius
				frameSurfaceProperty,
				this,
				getStudio()
		);
		SceneObjectDifference frame = new SceneObjectDifference(
				"Frame",	// description
				frameOutside, frameInside,
				this,
				getStudio()
			);

		addSceneObject(frame, showFrame);
	}
	
	
	
	// GUI panels
//	private JPanel editPanel;
//	private StringLine descriptionPanel;
	private LabelledVector3DPanel cornerPanel, widthVectorPanel, heightVectorPanel;
	private LabelledDoublePanel frameRadiusPanel;
	private JButton convertButton;
	private SurfacePropertyPanel
		windowSurfacePropertyPanel,
		frameSurfacePropertyPanel,
		beingEdited;



	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Framed window"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		cornerPanel = new LabelledVector3DPanel("Corner");
		basicParametersPanel.add(cornerPanel, "wrap");
		
		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
		basicParametersPanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
		basicParametersPanel.add(heightVectorPanel, "wrap");

		windowSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		windowSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(windowSurfacePropertyPanel));
		basicParametersPanel.add(windowSurfacePropertyPanel);
		windowSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		// the frame radius
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));
		// framePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Frame"));

		frameRadiusPanel = new LabelledDoublePanel("Frame radius");
		framePanel.add(frameRadiusPanel, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel);
		frameSurfacePropertyPanel.setIPanel(iPanel);
		
		tabbedPane.addTab("Frame", framePanel);

		
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
		cornerPanel.setVector3D(getCorner());
		widthVectorPanel.setVector3D(getWidthVector());
		heightVectorPanel.setVector3D(getHeightVector());
		frameRadiusPanel.setNumber(getRadius());
		windowSurfacePropertyPanel.setSurfaceProperty(paneSurfaceProperty);
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableFramedDisc acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();
		
		setCorner(cornerPanel.getVector3D());
		setWidthVector(widthVectorPanel.getVector3D());
		setHeightVector(heightVectorPanel.getVector3D());
		setRadius(frameRadiusPanel.getNumber());
		setPaneSurfaceProperty(windowSurfacePropertyPanel.getSurfaceProperty());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the cylinders
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-framed window");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				if(beingEdited == windowSurfacePropertyPanel)
				{
					// inside surface property has been edited
					setPaneSurfaceProperty((SurfaceProperty)edited);
					windowSurfacePropertyPanel.setSurfaceProperty(getPaneSurfaceProperty());
				}
				if(beingEdited == frameSurfacePropertyPanel)
				{
					// outside surface property has been edited
					setFrameSurfaceProperty((SurfaceProperty)edited);
					frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
				}
			}
	}

	class SurfacePropertyPanelListener implements ActionListener
	{
		private SurfacePropertyPanel surfacePropertyPanel;
		
		public SurfacePropertyPanelListener(SurfacePropertyPanel surfacePropertyPanel)
		{
			this.surfacePropertyPanel = surfacePropertyPanel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals(SurfacePropertyPanel.TILING_PARAMS_BUTTON_TEXT))
			{
				beingEdited = surfacePropertyPanel;
			}
		}
	}
}
