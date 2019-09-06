package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable object representing a simplicial complex.
 * 
 * 
 * @author Johannes
 */
public class EditableSimplicialComplex extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 6922155394272864485L;

	/**
	 * the simplicial complex
	 */
	protected SimplicialComplex simplicialComplex;
	
	/**
	 * show spheres representing the vertices
	 */
	protected boolean showVertices;
	
	/**
	 * surface property of the spheres representing the vertices
	 */
	protected SurfaceProperty vertexSurfaceProperty;
	
	/**
	 * radius of the spheres representing the vertices
	 */
	protected double vertexRadius;

	/**
	 * show the edges of the simplicial complex
	 */
	protected boolean showEdges;
		
	/**
	 * surface property of the cylinders representing the edges
	 */
	protected SurfaceProperty edgeSurfaceProperty;
	
	/**
	 * radius of the cylinders representing the edges
	 */
	protected double edgeRadius;

	/**
	 * show the faces of the simplicial complex
	 */
	protected boolean showFaces;
	
	/**
	 * surface property of the faces
	 */
	protected SurfaceProperty faceSurfaceProperty;
	
	
	
	//
	// internal variables
	//
	
	// GUI panels
	protected LabelledDoublePanel vertexRadiusPanel, edgeRadiusPanel;
	protected JCheckBox showVerticesCheckBox, showEdgesCheckBox, showFacesCheckBox;
	protected SurfacePropertyPanel vertexSurfacePropertyPanel, edgeSurfacePropertyPanel, faceSurfacePropertyPanel;
	protected JButton convertButton;
	
	
	//
	// constructors
	//
	
	
	public EditableSimplicialComplex(
			String description,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);

		// create a physical-space simplicial complex
		
		// create a new array of vertices
		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
		
		// add a few vertices
		vertices.add(new Vector3D(-1, 0, 10+1));	// 0
		vertices.add(new Vector3D( 1, 0, 10+1));	// 1
		vertices.add(new Vector3D( 0, 0, 10-1));	// 2
		vertices.add(new Vector3D( 0, 0.3, 10+0));	// 3
		vertices.add(new Vector3D( 0, 0.6, 10+0));	// 4
		vertices.add(new Vector3D( 0, 1, 10+0));	// 5
		
		// create a new array of edges
		ArrayList<Edge> edges = new ArrayList<Edge>();

		// add a few vertices
		try {
			edges.add(new Edge(0, 1));	// 0
			edges.add(new Edge(1, 2));	// 1
			edges.add(new Edge(2, 0));	// 2
			edges.add(new Edge(3, 0));	// 3
			edges.add(new Edge(3, 1));	// 4
			edges.add(new Edge(3, 2));	// 5
			edges.add(new Edge(4, 0));	// 6
			edges.add(new Edge(4, 1));	// 7	
			edges.add(new Edge(4, 2));	// 8
			edges.add(new Edge(5, 0));	// 9
			edges.add(new Edge(5, 1));	// 10
			edges.add(new Edge(5, 2));	// 11
			edges.add(new Edge(3, 4));	// 12
			edges.add(new Edge(4, 5));	// 13
		} catch (InconsistencyException e1) {
			e1.printStackTrace();
		}


		// create a simplicial complex with the given vertices and edges, and with the faces and simplices inferred

		try {
			this.simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		this.showVertices = true;
		this.vertexSurfaceProperty = SurfaceColour.DARK_BLUE_SHINY;
		this.vertexRadius = 0.03;
		this.showEdges = true;
		this.edgeSurfaceProperty = SurfaceColour.BLUE_SHINY;
		this.edgeRadius = 0.02;
		this.showFaces = false;
		this.faceSurfaceProperty = SurfaceColour.LIGHT_BLUE_SHINY;
		
		populateSceneObjectCollection();
	}
	
	
	public EditableSimplicialComplex(
			String description,
			SimplicialComplex simplicialComplex,
			boolean showVertices,
			SurfaceProperty vertexSurfaceProperty,
			double vertexRadius,
			boolean showEdges,
			SurfaceProperty edgeSurfaceProperty,
			double edgeRadius,
			boolean showFaces,
			SurfaceProperty faceSurfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		this.simplicialComplex = simplicialComplex;
		this.showVertices = showVertices;
		this.vertexSurfaceProperty = vertexSurfaceProperty;
		this.vertexRadius = vertexRadius;
		this.showEdges = showEdges;
		this.edgeSurfaceProperty = edgeSurfaceProperty;
		this.edgeRadius = edgeRadius;
		this.showFaces = showFaces;
		this.faceSurfaceProperty = faceSurfaceProperty;
		
		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSimplicialComplex(EditableSimplicialComplex original)
	{
		this(
			original.getDescription(),
			original.getSimplicialComplex(),
			original.isShowVertices(),
			original.getVertexSurfaceProperty(),
			original.getVertexRadius(),
			original.isShowEdges(),
			original.getEdgeSurfaceProperty(),
			original.getEdgeRadius(),
			original.isShowFaces(),
			original.getFaceSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableSimplicialComplex clone()
	{
		return new EditableSimplicialComplex(this);
	}

	
	//
	// setters and getters
	//

	public SimplicialComplex getSimplicialComplex() {
		return simplicialComplex;
	}

	public void setSimplicialComplex(SimplicialComplex simplicialComplex) {
		this.simplicialComplex = simplicialComplex;
	}

	public boolean isShowVertices() {
		return showVertices;
	}

	public void setShowVertices(boolean showVertices) {
		this.showVertices = showVertices;
	}

	public SurfaceProperty getVertexSurfaceProperty() {
		return vertexSurfaceProperty;
	}

	public void setVertexSurfaceProperty(SurfaceProperty vertexSurfaceProperty) {
		this.vertexSurfaceProperty = vertexSurfaceProperty;
	}

	public double getVertexRadius() {
		return vertexRadius;
	}

	public void setVertexRadius(double vertexRadius) {
		this.vertexRadius = vertexRadius;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public SurfaceProperty getEdgeSurfaceProperty() {
		return edgeSurfaceProperty;
	}

	public void setEdgeSurfaceProperty(SurfaceProperty edgeSurfaceProperty) {
		this.edgeSurfaceProperty = edgeSurfaceProperty;
	}

	public double getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public boolean isShowFaces() {
		return showFaces;
	}

	public void setShowFaces(boolean showFaces) {
		this.showFaces = showFaces;
	}

	public SurfaceProperty getFaceSurfaceProperty() {
		return faceSurfaceProperty;
	}

	public void setFaceSurfaceProperty(SurfaceProperty faceSurfaceProperty) {
		this.faceSurfaceProperty = faceSurfaceProperty;
	}

	
	//
	// add the scene objects that form this TO structure
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	protected void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// add the scene objects representing the simplicial complex in physical space
		if(simplicialComplex != null)
		addSceneObject(simplicialComplex.getEditableSceneObjectCollection(
				"Simplicial complex in physical space",	// description
				showVertices,
				vertexSurfaceProperty,
				vertexRadius,
				showEdges,
				edgeSurfaceProperty,
				edgeRadius,
				showFaces,
				faceSurfaceProperty,
				this,	// parent
				getStudio()
			));
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane);
		
		//
		// the visualisation panel
		//
		
		JPanel visualisationPanel = new JPanel();
		visualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showVerticesCheckBox = new JCheckBox("Show vertices");
		visualisationPanel.add(showVerticesCheckBox, "wrap");

		vertexRadiusPanel = new LabelledDoublePanel("Radius of spheres representing vertices");
		visualisationPanel.add(vertexRadiusPanel, "wrap");

		vertexSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPanel.add(vertexSurfacePropertyPanel, "wrap");
		vertexSurfacePropertyPanel.setIPanel(iPanel);

		showEdgesCheckBox = new JCheckBox("Show edges");
		visualisationPanel.add(showEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Radius of cylinders representing edges");
		visualisationPanel.add(edgeRadiusPanel, "wrap");

		edgeSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPanel.add(edgeSurfacePropertyPanel, "wrap");
		edgeSurfacePropertyPanel.setIPanel(iPanel);

		showFacesCheckBox = new JCheckBox("Show faces");
		visualisationPanel.add(showFacesCheckBox, "wrap");

		faceSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPanel.add(faceSurfacePropertyPanel, "wrap");
		faceSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Visualisation", visualisationPanel);
		
		
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
		
		showVerticesCheckBox.setSelected(showVertices);
		vertexSurfacePropertyPanel.setSurfaceProperty(vertexSurfaceProperty);
		vertexRadiusPanel.setNumber(vertexRadius);
		showEdgesCheckBox.setSelected(showEdges);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
		edgeRadiusPanel.setNumber(edgeRadius);
		showFacesCheckBox.setSelected(showFaces);
		faceSurfacePropertyPanel.setSurfaceProperty(faceSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSimplicialComplex acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setShowVertices(showVerticesCheckBox.isSelected());
		setVertexSurfaceProperty(vertexSurfacePropertyPanel.getSurfaceProperty());
		setVertexRadius(vertexRadiusPanel.getNumber());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setShowFaces(showFacesCheckBox.isSelected());
		setFaceSurfaceProperty(faceSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-simplicial-complex");
		container.setValuesInEditPanel();
	}

//	@Override
//	public void backToFront(IPanelComponent edited)
//	{
//			if(edited instanceof SurfaceProperty)
//			{
//				// frame surface property has been edited
//				setFrameSurfaceProperty((SurfaceProperty)edited);
//				frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
//			}
//	}
}