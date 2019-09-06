package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import testImages.TestImage;
import math.*;

/**
 * A framed USAF test chart
 * @author Johannes Courtial
 */
public class EditableFramedUSAFTestChart extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -6910768254558854715L;

	// parameters
	private Vector3D
		centre,
		up,
		front;
	private double width;	// , frameRadius;
	private SurfaceProperty frameSurfaceProperty;
	private boolean showFrame = true;

	// GUI panels
	private LabelledVector3DPanel centrePanel, upPanel, frontPanel;
	private LabelledDoublePanel widthPanel; // , frameRadiusPanel;
	private JCheckBox showFrameCheckBox;
//	private JButton convertButton;
	private SurfacePropertyPanel
		frameSurfacePropertyPanel;

	public EditableFramedUSAFTestChart(
			String description,
			Vector3D centre,
			Vector3D up,
			Vector3D front,
			double width,
			// double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			boolean showFrame,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.centre = centre;
		this.up = up;
		this.front = front;
		this.width = width;
		// this.frameRadius = frameRadius;
		this.frameSurfaceProperty = frameSurfaceProperty;
		this.showFrame = showFrame;

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFramedUSAFTestChart(EditableFramedUSAFTestChart original)
	{
		super(original);
		
		// copy the original's parameters
		this.centre = original.centre;
		this.up = original.up;
		this.front = original.front;
		this.width = original.width;
		// this.frameRadius = original.frameRadius;
		this.frameSurfaceProperty = original.frameSurfaceProperty;
		this.showFrame = original.showFrame;
	}

	@Override
	public EditableFramedUSAFTestChart clone()
	{
		return new EditableFramedUSAFTestChart(this);
	}


	//
	// setters & getters
	//
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getUp() {
		return up;
	}

	public void setUp(Vector3D up) {
		this.up = up;
	}

	public Vector3D getFront() {
		return front;
	}

	public void setFront(Vector3D front) {
		this.front = front;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
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

	private void populateSceneObjectCollection()
	{
		double height = width / TestImage.USAF_TEST_CHART.getAspectRatio();
		Vector3D spanVectorX = Vector3D.crossProduct(up.getPartPerpendicularTo(front), front).getWithLength(width);
		Vector3D spanVectorY = up.getPartPerpendicularTo(front).getWithLength(height);
		
		// create the window		
		addSceneObject(TestImage.USAF_TEST_CHART.getEditableScaledParametrisedCentredParallelogram(
				"USAF Test Target", 	// description
				centre,	// centre
				spanVectorX.getReverse(),	// spanVector1
				spanVectorY.getReverse(),	// spanVector2
				this,	// parent
				getStudio()	// studio
			));
		
		// create all the cylinders for the frame
		if(showFrame)
		{
			Vector3D
			bottomLeft = Vector3D.sum(centre, spanVectorX.getProductWith(-0.5), spanVectorY.getProductWith(-0.5)),
			bottomRight = Vector3D.sum(bottomLeft, spanVectorX),
			topLeft = Vector3D.sum(bottomLeft, spanVectorY),
			topRight = Vector3D.sum(bottomRight, spanVectorY);
			
			double frameRadius = 0.01*width;

			addSceneObject(new EditableParametrisedCylinder(
					"bottom cylinder",
					bottomLeft,	// bottom left corner is start point
					bottomRight,	// end point
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"top cylinder",
					topLeft,	// start point
					topRight,	// end point
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"left cylinder",
					bottomLeft,	// start point
					topLeft,	// end point
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"right cylinder",
					bottomRight,	// start point
					topRight,	// end point
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			// add the spheres in the corners

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in bottom left corner",
					bottomLeft,	// centre
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in top left corner",
					topLeft,	// centre
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in top right corner",
					topRight,	// centre
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere in bottom right corner",
					bottomRight,	// centre
					frameRadius,	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));
		}
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

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("USAF test chart"));
		
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

		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");
		
		frontPanel = new LabelledVector3DPanel("Front direction");
		basicParametersPanel.add(frontPanel, "wrap");

		upPanel = new LabelledVector3DPanel("Top direction");
		basicParametersPanel.add(upPanel, "wrap");
		
		widthPanel = new LabelledDoublePanel("Width");
		basicParametersPanel.add(widthPanel, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		// the frame radius
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));
		// framePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Frame"));

//		frameRadiusPanel = new LabelledDoublePanel("Frame radius");
//		framePanel.add(frameRadiusPanel, "wrap");
		
		showFrameCheckBox = new JCheckBox("Show frame");
		framePanel.add(showFrameCheckBox, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel);
		frameSurfacePropertyPanel.setIPanel(iPanel);
		
		tabbedPane.addTab("Frame", framePanel);

		
		editPanel.add(tabbedPane);

//		// the convert button
//
//		convertButton = new JButton("Convert to collection of scene objects");
//		convertButton.addActionListener(this);
//		editPanel.add(convertButton, "south");

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
		centrePanel.setVector3D(centre);
		frontPanel.setVector3D(front);
		upPanel.setVector3D(up);
		widthPanel.setNumber(width);
		// frameRadiusPanel.setNumber(getRadius());
		showFrameCheckBox.setSelected(showFrame);
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableFramedUSAFTestChart acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();
		
		setCentre(centrePanel.getVector3D());
		setFront(frontPanel.getVector3D());
		setUp(upPanel.getVector3D());
		setWidth(widthPanel.getNumber());
		// setRadius(frameRadiusPanel.getNumber());
		setShowFrame(showFrameCheckBox.isSelected());
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
}
