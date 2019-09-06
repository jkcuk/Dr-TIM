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
import math.*;

/**
 * A framed rectangular window -- a combination of a rectangle and an editable cylinder frame.
 * @author Johannes Courtial
 */
public class EditableFramedRectangle extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -9019288481116476294L;

	// parameters
	private Vector3D
		corner,	// assume this is the bottom left corner
		widthVector, heightVector;
	private double frameRadius;
	private SurfaceProperty paneSurfaceProperty, frameSurfaceProperty;
	private boolean showFrames = true;
	private EditableScaledParametrisedCentredParallelogram pane;

	/**
	 * @param description
	 * @param corner
	 * @param widthVector
	 * @param heightVector
	 * @param frameRadius
	 * @param windowSurfaceProperty
	 * @param frameSurfaceProperty
	 * @param showFrames
	 * @param parent
	 * @param studio
	 */
	public EditableFramedRectangle(
			String description,
			Vector3D corner,
			Vector3D widthVector,
			Vector3D heightVector,
			double frameRadius,
			SurfaceProperty windowSurfaceProperty,
			SurfaceProperty frameSurfaceProperty,
			boolean showFrames,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.corner = corner;
		this.widthVector = widthVector;
		this.heightVector = heightVector;
		this.frameRadius = frameRadius;
		this.paneSurfaceProperty = windowSurfaceProperty;
		this.frameSurfaceProperty = frameSurfaceProperty;
		this.showFrames = showFrames;

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFramedRectangle(EditableFramedRectangle original)
	{
		super(original);
		
		// copy the original's parameters
		this.corner = original.corner;
		this.widthVector = original.widthVector;
		this.heightVector = original.heightVector;
		this.frameRadius = original.frameRadius;
		this.paneSurfaceProperty = original.paneSurfaceProperty;
		this.frameSurfaceProperty = original.frameSurfaceProperty;
	}

	@Override
	public EditableFramedRectangle clone()
	{
		return new EditableFramedRectangle(this);
	}

	public Vector3D getCorner() {
		return corner;
	}

	public void setCorner(Vector3D corner) {
		this.corner = corner;
	}

	public Vector3D getWidthVector() {
		return widthVector;
	}

	public void setWidthVector(Vector3D widthVector) {
		this.widthVector = widthVector;
	}

	public Vector3D getHeightVector() {
		return heightVector;
	}

	public void setHeightVector(Vector3D heightVector) {
		this.heightVector = heightVector;
	}

	public double getRadius() {
		return frameRadius;
	}

	public void setRadius(double radius) {
		this.frameRadius = radius;
	}
	
	public SurfaceProperty getPaneSurfaceProperty() {
		return paneSurfaceProperty;
	}

	public void setPaneSurfaceProperty(SurfaceProperty paneSurfaceProperty) {
		this.paneSurfaceProperty = paneSurfaceProperty;
		if(pane != null) pane.setSurfaceProperty(paneSurfaceProperty);
	}

	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}

	public EditableScaledParametrisedCentredParallelogram getPane() {
		return pane;
	}

	private void populateSceneObjectCollection()
	{
		// create the window
		
		if(getPaneSurfaceProperty() != null)
		{
		pane = new EditableScaledParametrisedCentredParallelogram(
				"pane",	// description
				Vector3D.sum(
						getCorner(),
						getWidthVector().getProductWith(0.5),
						getHeightVector().getProductWith(0.5)
					),	// centre
				getWidthVector(),	// spanVector1
				getHeightVector(),	// spanVector2
				0,	// suMin
				1,	// suMax
				0,	// svMin
				1,	// svMax
				getPaneSurfaceProperty(),	// surfaceProperty
				this,	// parent
				getStudio()	// studio
			);
		addSceneObject(pane);
		}
		
		// create all the cylinders for the frame
		if(showFrames)
		{
			Vector3D
			bottomLeft = getCorner(),
			bottomRight = Vector3D.sum(bottomLeft, getWidthVector()),
			topLeft = Vector3D.sum(bottomLeft, getHeightVector()),
			topRight = Vector3D.sum(bottomRight, getHeightVector());

			addSceneObject(new EditableParametrisedCylinder(
					"bottom cylinder",
					bottomLeft,	// bottom left corner is start point
					bottomRight,	// end point
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"top cylinder",
					topLeft,	// start point
					topRight,	// end point
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"left cylinder",
					bottomLeft,	// start point
					topLeft,	// end point
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"right cylinder",
					bottomRight,	// start point
					topRight,	// end point
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			// add the spheres in the corners

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in bottom left corner",
					bottomLeft,	// centre
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in top left corner",
					topLeft,	// centre
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in top right corner",
					topRight,	// centre
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in bottom right corner",
					bottomRight,	// centre
					getRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));
		}
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
	public EditableFramedRectangle acceptValuesInEditPanel()
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
