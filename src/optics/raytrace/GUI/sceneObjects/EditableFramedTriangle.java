package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
import optics.raytrace.sceneObjects.ParametrisedTriangle;
import math.*;

/**
 * A framed triangle -- a combination of a triangle and an editable cylinder frame.
 * @author Johannes Courtial
 */
public class EditableFramedTriangle extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -2950348795561853041L;

	// parameters
	private ParametrisedTriangle triangle;
	private double frameRadius;
	private SurfaceProperty frameSurfaceProperty;
	private boolean showFrame = true;

	// GUI panels
	private LabelledVector3DPanel vertex1Panel, vertex1ToVertex2Panel, vertex1ToVertex3Panel, uUnitVectorPanel, vUnitVectorPanel;
	private JCheckBox semiInfiniteCheckBox;
	private LabelledDoublePanel frameRadiusPanel;
	private JButton convertButton;
	private SurfacePropertyPanel
		windowSurfacePropertyPanel,
		frameSurfacePropertyPanel,
		beingEdited;

	public EditableFramedTriangle(
			String description,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			boolean semiInfinite,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			double frameRadius,
			SurfaceProperty windowSurfaceProperty,
			SurfaceProperty frameSurfaceProperty,
			boolean showFrame,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		triangle = new ParametrisedTriangle(description, vertex1, vertex1ToVertex2, vertex1ToVertex3, semiInfinite, uUnitVector, vUnitVector, windowSurfaceProperty, parent, studio);

		this.frameRadius = frameRadius;
		this.frameSurfaceProperty = frameSurfaceProperty;
		this.showFrame = showFrame;

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableFramedTriangle(EditableFramedTriangle original)
	{
		super(original);
		
		// copy the original's parameters
		this.triangle = original.getTriangle();
		this.frameRadius = original.getFrameRadius();
		this.frameSurfaceProperty = original.getFrameSurfaceProperty();
		this.showFrame = original.isShowFrame();
	}

	@Override
	public EditableFramedTriangle clone()
	{
		return new EditableFramedTriangle(this);
	}

	public ParametrisedTriangle getTriangle() {
		return triangle;
	}

	public void setTriangle(ParametrisedTriangle triangle) {
		this.triangle = triangle;
	}

	public double getFrameRadius() {
		return frameRadius;
	}

	public void setFrameRadius(double frameRadius) {
		this.frameRadius = frameRadius;
	}
	
	public SurfaceProperty getWindowSurfaceProperty() {
		return triangle.getSurfaceProperty();
	}

	public void setWindowSurfaceProperty(SurfaceProperty windowSurfaceProperty) {
		triangle.setSurfaceProperty(windowSurfaceProperty);
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
		// create the window
		
		EditableParametrisedTriangle window = new EditableParametrisedTriangle(triangle);
		addSceneObject(window);
		
		// create all the cylinders for the frame
		if(showFrame)
		{
			Vector3D
				vertex1 = triangle.getVertex1(),
				vertex2 = Vector3D.sum(vertex1, triangle.isSemiInfinite()?triangle.getVertex1ToVertex2().getWithLength(MyMath.HUGE):triangle.getVertex1ToVertex2()),
				vertex3 = Vector3D.sum(vertex1, triangle.isSemiInfinite()?triangle.getVertex1ToVertex3().getWithLength(MyMath.HUGE):triangle.getVertex1ToVertex3());

			addSceneObject(new EditableParametrisedCylinder(
					"frame cylinder from vertex 1 to vertex 2",
					vertex1,	// bottom left vertex is start point
					vertex2,	// end point
					getFrameRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"frame cylinder from vertex 2 to vertex 3",
					vertex2,	// bottom left vertex is start point
					vertex3,	// end point
					getFrameRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			addSceneObject(new EditableParametrisedCylinder(
					"frame cylinder from vertex 3 to vertex 1",
					vertex3,	// bottom left vertex is start point
					vertex1,	// end point
					getFrameRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			// add the spheres in the vertices

			addSceneObject(new EditableScaledParametrisedSphere(
					"sphere at vertex 1",
					vertex1,	// centre
					getFrameRadius(),	// radius
					getFrameSurfaceProperty(),
					this,
					getStudio()
			));

			if(!triangle.isSemiInfinite())
			{
				addSceneObject(new EditableScaledParametrisedSphere(
						"sphere at vertex 2",
						vertex2,	// centre
						getFrameRadius(),	// radius
						getFrameSurfaceProperty(),
						this,
						getStudio()
				));

				addSceneObject(new EditableScaledParametrisedSphere(
						"sphere at vertex 3",
						vertex3,	// centre
						getFrameRadius(),	// radius
						getFrameSurfaceProperty(),
						this,
						getStudio()
				));
			}
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
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Framed window"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new BoxLayout(basicParametersPanel, BoxLayout.Y_AXIS));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel);

		vertex1Panel = new LabelledVector3DPanel("Vertex 1");
		editPanel.add(vertex1Panel);
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		vertex1ToVertex2Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 2");
		editPanel.add(vertex1ToVertex2Panel);

		vertex1ToVertex3Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 3");
		editPanel.add(vertex1ToVertex3Panel);
		
		semiInfiniteCheckBox = new JCheckBox("Semi-infinite");
		editPanel.add(semiInfiniteCheckBox);
		
		uUnitVectorPanel = new LabelledVector3DPanel("Unit vector in u direction");
		editPanel.add(uUnitVectorPanel);

		vUnitVectorPanel = new LabelledVector3DPanel("Unit vector in v direction");
		editPanel.add(vUnitVectorPanel);

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		windowSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		windowSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(windowSurfacePropertyPanel));
		basicParametersPanel.add(windowSurfacePropertyPanel);
		windowSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		// the frame radius
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new BoxLayout(framePanel, BoxLayout.Y_AXIS));
		// framePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Frame"));

		frameRadiusPanel = new LabelledDoublePanel("Frame radius");
		framePanel.add(frameRadiusPanel);
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel);
		frameSurfacePropertyPanel.setIPanel(iPanel);
		
		tabbedPane.addTab("Frame", framePanel);

		
		editPanel.add(tabbedPane);

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
		vertex1Panel.setVector3D(triangle.getVertex1());
		vertex1ToVertex2Panel.setVector3D(triangle.getVertex1ToVertex2());
		vertex1ToVertex3Panel.setVector3D(triangle.getVertex1ToVertex3());
		semiInfiniteCheckBox.setSelected(triangle.isSemiInfinite());
		uUnitVectorPanel.setVector3D(triangle.getuUnitVector());
		vUnitVectorPanel.setVector3D(triangle.getvUnitVector());
		frameRadiusPanel.setNumber(getFrameRadius());
		windowSurfacePropertyPanel.setSurfaceProperty(triangle.getSurfaceProperty());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableFramedTriangle acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();
		
		triangle.setVertex1(vertex1Panel.getVector3D());
		triangle.setDirectionVectors(
				vertex1ToVertex2Panel.getVector3D(),
				vertex1ToVertex3Panel.getVector3D(),
				uUnitVectorPanel.getVector3D(),
				vUnitVectorPanel.getVector3D()
			);
		triangle.setSemiInfinite(semiInfiniteCheckBox.isSelected());

		setWindowSurfaceProperty(windowSurfacePropertyPanel.getSurfaceProperty());
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
					setWindowSurfaceProperty((SurfaceProperty)edited);
					windowSurfacePropertyPanel.setSurfaceProperty(getWindowSurfaceProperty());
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
