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
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A triangular prism, i.e. "a polyhedron made of a triangular base, a translated copy, and 3 faces joining corresponding sides"
 * [https://en.wikipedia.org/wiki/Triangular_prism].
 * The corners of the triangular base are given
 * 
 * @author Johannes Courtial
 */
public class EditableTriangularPrism extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 2619006062041000977L;

	//
	// parameters
	//
	
	/**
	 * Vertex 1 of the triangular base.
	 */
	private Vector3D baseVertex1;
	
	/**
	 * Vertex 2 of the triangular base.
	 */
	private Vector3D baseVertex2;

	/**
	 * Vertex 3 of the triangular base.
	 */
	private Vector3D baseVertex3;

	/**
	 * Edge vector, i.e. a vector from the triangular base to the translated copy.
	 */
	private Vector3D edgeVector;
	

	/**
	 * If true, show the faces
	 */
	private boolean showFaces;
	
	private boolean showTopAndBaseFaces;
	
	/**
	 * surface property of the faces (if shown)
	 */
	private SurfaceProperty faceSurfaceProperty;
	
	/**
	 * If true, draw cylinders along the edges (and spheres at the vertices)
	 */
	private boolean showEdges;
	
	/**
	 * radius of the cylinders along the edges (and spheres at the vertices)
	 */
	private double edgeRadius;
	
	/**
	 * surface property of the edges (if shown)
	 */
	private SurfaceProperty edgeSurfaceProperty;
	
	


	//
	// constructors
	//
	

	/**
	 * @param description
	 * @param baseVertex1
	 * @param baseVertex2
	 * @param baseVertex3
	 * @param edgeVector
	 * @param showFaces
	 * @param showTopAndBaseFaces
	 * @param faceSurfaceProperty
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableTriangularPrism(
			String description,
			Vector3D baseVertex1,
			Vector3D baseVertex2,
			Vector3D baseVertex3,
			Vector3D edgeVector,
			boolean showFaces,
			boolean showTopAndBaseFaces,
			SurfaceProperty faceSurfaceProperty,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, true, parent, studio);
		
		this.baseVertex1 = baseVertex1;
		this.baseVertex2 = baseVertex2;
		this.baseVertex3 = baseVertex3;
		this.edgeVector = edgeVector;
		this.showFaces = showFaces;
		this.showTopAndBaseFaces = showTopAndBaseFaces;
		this.faceSurfaceProperty = faceSurfaceProperty;
		this.showEdges = showEdges;
		this.edgeRadius = edgeRadius;
		this.edgeSurfaceProperty = edgeSurfaceProperty;
		
		populateSceneObjectCollection();
	}

	/**
	 * Create a default null-space wedge
	 * @param parent
	 * @param studio
	 */
	public EditableTriangularPrism(SceneObject parent, Studio studio)
	{
		this(
				"Triangular prism",	// description
				new Vector3D(0, 0, 10),	// baseVertex1
				new Vector3D(0, 0, 11),	// baseVertex2
				new Vector3D(-1, 0, 11),	// baseVertex3
				new Vector3D(0, 1, 0),	// edgeVector
				true,	// showFaces
				true,	// showTopAndBaseFaces
				SurfaceColour.RED_SHINY,	// faceSurfaceProperty
				true,	// showEdges
				0.01,	// edgeRadius
				SurfaceColour.GREY20_SHINY,	// edgeSurfaceProperty
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableTriangularPrism(EditableTriangularPrism original)
	{
		this(
			original.getDescription(),
			original.getBaseVertex1(),
			original.getBaseVertex2(),
			original.getBaseVertex3(),
			original.getEdgeVector(),
			original.isShowFaces(),
			original.isShowTopAndBaseFaces(),
			original.getFaceSurfaceProperty(),
			original.isShowEdges(),
			original.getEdgeRadius(),
			original.getEdgeSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableTriangularPrism clone()
	{
		return new EditableTriangularPrism(this);
	}

	
	//
	// setters and getters
	//
	
	public Vector3D getBaseVertex1() {
		return baseVertex1;
	}

	public void setBaseVertex1(Vector3D baseVertex1) {
		this.baseVertex1 = baseVertex1;
	}

	public Vector3D getBaseVertex2() {
		return baseVertex2;
	}

	public void setBaseVertex2(Vector3D baseVertex2) {
		this.baseVertex2 = baseVertex2;
	}

	public Vector3D getBaseVertex3() {
		return baseVertex3;
	}

	public void setBaseVertex3(Vector3D baseVertex3) {
		this.baseVertex3 = baseVertex3;
	}

	public Vector3D getEdgeVector() {
		return edgeVector;
	}

	public void setEdgeVector(Vector3D edgeVector) {
		this.edgeVector = edgeVector;
	}

	public boolean isShowFaces() {
		return showFaces;
	}

	public void setShowFaces(boolean showFaces) {
		this.showFaces = showFaces;
	}

	public boolean isShowTopAndBaseFaces() {
		return showTopAndBaseFaces;
	}

	public void setShowTopAndBaseFaces(boolean showTopAndBaseFaces) {
		this.showTopAndBaseFaces = showTopAndBaseFaces;
	}

	public SurfaceProperty getFaceSurfaceProperty() {
		return faceSurfaceProperty;
	}

	public void setFaceSurfaceProperty(SurfaceProperty faceSurfaceProperty) {
		this.faceSurfaceProperty = faceSurfaceProperty;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public double getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public SurfaceProperty getEdgeSurfaceProperty() {
		return edgeSurfaceProperty;
	}

	public void setEdgeSurfaceProperty(SurfaceProperty edgeSurfaceProperty) {
		this.edgeSurfaceProperty = edgeSurfaceProperty;
	}





	
	//
	// internal variables
	//
	
	// containers for the faces and edges
	private EditableSceneObjectCollection faces, edges, vertices;
	

	private void populateCollections()
	{
		Vector3D
			topVertex1 = Vector3D.sum(baseVertex1, edgeVector),
			topVertex2 = Vector3D.sum(baseVertex2, edgeVector),
			topVertex3 = Vector3D.sum(baseVertex3, edgeVector);
		
		// the face with vertices 1 and 2 (and 1' and 2', i.e. vertices 1 and 2 of the translated copy of the base)
		faces.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Face 1",
				baseVertex1,	// corner 
				Vector3D.difference(baseVertex2, baseVertex1),	// spanVector1
				edgeVector,	// spanVector2
				faceSurfaceProperty,	// surfaceProperty
				faces,	// parent
				getStudio()
			));

		// the face with vertices 2 and 3 (and 2' and 3', i.e. vertices 2 and 3 of the translated copy of the base)
		faces.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Face 2",
				baseVertex2,	// corner 
				Vector3D.difference(baseVertex3, baseVertex2),	// spanVector1
				edgeVector,	// spanVector2
				faceSurfaceProperty,	// surfaceProperty
				faces,	// parent
				getStudio()
			));

		// the face with vertices 3 and 1 (and 3' and 1', i.e. vertices 3 and 1 of the translated copy of the base)
		faces.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Face 3",
				baseVertex3,	// corner 
				Vector3D.difference(baseVertex1, baseVertex3),	// spanVector1
				edgeVector,	// spanVector2
				faceSurfaceProperty,	// surfaceProperty
				faces,	// parent
				getStudio()
			));

		// the base
		faces.addSceneObject(new EditableTriangle(
				"Base triangle",	// description
				baseVertex1,	// vertex1
				Vector3D.difference(baseVertex2, baseVertex1),	// vertex1ToVertex2
				Vector3D.difference(baseVertex3, baseVertex1),	// vertex1ToVertex3
				false,	// semiInfinite
				Vector3D.difference(baseVertex2, baseVertex1),	// uUnitVector
				Vector3D.difference(baseVertex3, baseVertex1),	// vUnitVector
				faceSurfaceProperty,	// windowSurfaceProperty
				faces,	// parent
				getStudio()
			), showTopAndBaseFaces);

		// the top
		faces.addSceneObject(new EditableTriangle(
				"Top triangle",	// description
				topVertex1,	// vertex1
				Vector3D.difference(topVertex2, topVertex1),	// vertex1ToVertex2
				Vector3D.difference(topVertex3, topVertex1),	// vertex1ToVertex3
				false,	// semiInfinite
				Vector3D.difference(topVertex2, topVertex1),	// uUnitVector
				Vector3D.difference(topVertex3, topVertex1),	// vUnitVector
				faceSurfaceProperty,	// windowSurfaceProperty
				faces,	// parent
				getStudio()
			), showTopAndBaseFaces);
		
		// the edges
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Base edge 1",
						baseVertex1,	// start point
						baseVertex2,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Base edge 2",
						baseVertex2,	// start point
						baseVertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Base edge 3",
						baseVertex3,	// start point
						baseVertex1,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge 1",
						topVertex1,	// start point
						topVertex2,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge 2",
						topVertex2,	// start point
						topVertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge 3",
						topVertex3,	// start point
						topVertex1,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Side edge 1",
						baseVertex1,	// start point
						topVertex1,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Side edge 2",
						baseVertex2,	// start point
						topVertex2,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Side edge 3",
						baseVertex3,	// start point
						topVertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		
		
		// the vertices
		
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Base vertex 1",	// description
				baseVertex1,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Base vertex 2",	// description
				baseVertex2,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Base vertex 3",	// description
				baseVertex3,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Top vertex 1",	// description
				topVertex1,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Top vertex 2",	// description
				topVertex2,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
		vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Top vertex 3",	// description
				topVertex3,	// centre
				edgeRadius,	// radius
				edgeSurfaceProperty,	// surfaceProperty
				vertices, 	// parent
				getStudio()
			));
	}
	

	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the faces, ...
		faces = new EditableSceneObjectCollection("Faces", true, this, getStudio());
		
		// ... the edges, ...
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// ... and the vertices
		vertices = new EditableSceneObjectCollection("Vertices", true, this, getStudio());

		// populate these collections
		populateCollections();
		
		// add the windows and the edges to this collection
		addSceneObject(faces, showFaces);
		addSceneObject(edges, showEdges);
		addSceneObject(vertices, showEdges);
	}

	
	// GUI stuff
	
	// GUI panels
	private LabelledVector3DPanel baseVertex1Panel, baseVertex2Panel, baseVertex3Panel, edgeVectorPanel;
	private JCheckBox showFacesCheckBox, showTopAndBaseFacesCheckBox, showEdgesCheckBox;
	private LabelledDoublePanel edgeRadiusPanel;
	private SurfacePropertyPanel faceSurfacePropertyPanel, edgeSurfacePropertyPanel;
	
	private JButton convertButton;
	
	
		

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Truangular prism"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane, "wrap");
		
		

		//
		// the basic-parameters panel
		// 
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		baseVertex1Panel = new LabelledVector3DPanel("Base vertex 1");
		basicParametersPanel.add(baseVertex1Panel, "wrap");

		baseVertex2Panel = new LabelledVector3DPanel("Base vertex 2");
		basicParametersPanel.add(baseVertex2Panel, "wrap");

		baseVertex3Panel = new LabelledVector3DPanel("Base vertex 3");
		basicParametersPanel.add(baseVertex3Panel, "wrap");

		edgeVectorPanel = new LabelledVector3DPanel("Edge vector (base to top)");
		basicParametersPanel.add(edgeVectorPanel, "wrap");

		showFacesCheckBox = new JCheckBox("Show faces");
		basicParametersPanel.add(showFacesCheckBox, "wrap");
		
		showTopAndBaseFacesCheckBox = new JCheckBox("Show top and base faces");
		basicParametersPanel.add(showTopAndBaseFacesCheckBox, "wrap");

		faceSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// faceSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(faceSurfacePropertyPanel));
		basicParametersPanel.add(faceSurfacePropertyPanel, "wrap");
		faceSurfacePropertyPanel.setIPanel(iPanel);

		editPanel.add(basicParametersPanel, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);
		
		

		//
		// the edges panel
		// 
		
		JPanel edgesPanel = new JPanel();
		edgesPanel.setLayout(new MigLayout("insets 0"));

		showEdgesCheckBox = new JCheckBox("Show edges and vertices");
		edgesPanel.add(showEdgesCheckBox, "wrap");
		
		edgeRadiusPanel = new LabelledDoublePanel("Edge radius");
		edgesPanel.add(edgeRadiusPanel, "wrap");
		
		edgeSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		edgesPanel.add(edgeSurfacePropertyPanel, "wrap");
		edgeSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Edges", edgesPanel);


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
		baseVertex1Panel.setVector3D(baseVertex1);
		baseVertex2Panel.setVector3D(baseVertex2);
		baseVertex3Panel.setVector3D(baseVertex3);
		edgeVectorPanel.setVector3D(edgeVector);
		showFacesCheckBox.setSelected(showFaces);
		showTopAndBaseFacesCheckBox.setSelected(showTopAndBaseFaces);
		faceSurfacePropertyPanel.setSurfaceProperty(faceSurfaceProperty);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableTriangularPrism acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setBaseVertex1(baseVertex1Panel.getVector3D());
		setBaseVertex2(baseVertex2Panel.getVector3D());
		setBaseVertex3(baseVertex3Panel.getVector3D());
		setEdgeVector(edgeVectorPanel.getVector3D());
		setShowFaces(showFacesCheckBox.isSelected());
		setShowTopAndBaseFaces(showTopAndBaseFacesCheckBox.isSelected());
		setFaceSurfaceProperty(faceSurfacePropertyPanel.getSurfaceProperty());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-null-space wedge");
			container.setValuesInEditPanel();
		}
	}

//	@Override
//	public void backToFront(IPanelComponent edited)
//	{
//			if(edited instanceof SurfaceProperty)
//			{
//				// frame surface property has been edited
//				// setEdgeSurfaceProperty((SurfaceProperty)edited);
//				// edgeSurfacePropertyPanel.setSurfaceProperty(getEdgeSurfaceProperty());
//				edgeSurfacePropertyPanel.setSurfaceProperty((SurfaceProperty)edited);
//			}
//	}
}