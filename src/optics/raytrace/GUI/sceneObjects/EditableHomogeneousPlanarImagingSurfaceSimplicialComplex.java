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
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.simplicialComplex.HomogeneousPlanarImagingSurfaceSimplicialComplex;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable object representing a structure of homogeneous planar imaging elements described by a simplicial complex (in physical space) and
 * a the positions of the vertices in virtual space.
 * Note that such homogeneous planar imaging elements can be realised, in pixellated form, with GCLAs.
 * 
 * This should really be combined with EditableLensSimplicialComplex.
 * 
 * @see optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex
 * 
 * @author Johannes
 */
public class EditableHomogeneousPlanarImagingSurfaceSimplicialComplex extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -785494205212435613L;

	/**
	 * the simplicial complex
	 */
	protected HomogeneousPlanarImagingSurfaceSimplicialComplex homogeneousPlanarImagingElementSimplicialComplex;
	
	/**
	 * show the imaging elements of the simplicial complex representing physical space
	 */
	protected boolean showImagingElements;

	/**
	 * show spheres representing the vertices in physical space
	 */
	protected boolean showVertices;
	
	/**
	 * show spheres representing the vertices in virtual space
	 */
	protected boolean showVerticesV;
	
	/**
	 * surface property of the spheres representing the vertices in physical space
	 */
	protected SurfaceProperty vertexSurfaceProperty;
	
	/**
	 * surface property of the spheres representing the vertices in virtual space
	 */
	protected SurfaceProperty vertexSurfacePropertyV;
	
	/**
	 * radius of the spheres representing the vertices in physical space
	 */
	protected double vertexRadius;

	/**
	 * radius of the spheres representing the vertices in virtual space
	 */
	protected double vertexRadiusV;

	/**
	 * show the edges of the simplicial complex representing physical space, i.e. the lens edges
	 */
	protected boolean showEdges;
	
	/**
	 * show the edges of the simplicial complex representing virtual space
	 */
	protected boolean showEdgesV;
	
	/**
	 * surface property of the cylinders representing the edges in physical space
	 */
	protected SurfaceProperty edgeSurfaceProperty;

	/**
	 * surface property of the cylinders representing the edges in virtual space
	 */
	protected SurfaceProperty edgeSurfacePropertyV;
	
	/**
	 * radius of the cylinders representing the edges in physical space
	 */
	protected double edgeRadius;

	/**
	 * radius of the cylinders representing the edges in virtual space
	 */
	protected double edgeRadiusV;

	/**
	 * show the faces of the simplicial complex representing physical space, i.e. the triangles where the imaging elements are
	 */
	protected boolean showFaces;

	/**
	 * show the faces of the simplicial complex representing virtual space
	 */
	protected boolean showFacesV;
	
	/**
	 * surface property of the faces in physical space, i.e. the lenses
	 */
	protected SurfaceProperty faceSurfaceProperty;

	/**
	 * surface property of the faces in virtual space
	 */
	protected SurfaceProperty faceSurfacePropertyV;
	
	
	
	//
	// internal variables
	//
	
	// GUI panels
	protected LabelledDoublePanel vertexRadiusPanel, vertexRadiusVPanel, edgeRadiusPanel, edgeRadiusVPanel;
	protected JCheckBox showImagingElementsCheckBox, showVerticesCheckBox, showVerticesVCheckBox, showEdgesCheckBox, showEdgesVCheckBox, showFacesCheckBox, showFacesVCheckBox;
	protected SurfacePropertyPanel vertexSurfacePropertyPanel, vertexSurfacePropertyVPanel, edgeSurfacePropertyPanel, edgeSurfacePropertyVPanel, faceSurfacePropertyPanel, faceSurfacePropertyVPanel;
	protected JButton convertButton;
	
	
	//
	// constructors
	//
	
	
	public EditableHomogeneousPlanarImagingSurfaceSimplicialComplex(
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
		
//		// a tetrahedron with two inner vertices
//		vertices.add(new Vector3D(-1, 0, 10+1));	// 0; back left bottom vertex
//		vertices.add(new Vector3D( 1, 0, 10+1));	// 1; back right bottom vertex
//		vertices.add(new Vector3D( 0, 0, 10-1));	// 2; front bottom vertex
//		vertices.add(new Vector3D( 0, 0.3, 10+0));	// 3; lower inner vertex
//		vertices.add(new Vector3D( 0, 0.6, 10+0));	// 4; upper inner vertex
//		vertices.add(new Vector3D( 0, 1, 10+0));	// 5; top vertex

		// a tetrahedron with a single inner vertex
		vertices.add(new Vector3D(-1, 0, 10+1));	// 0; back left bottom vertex
		vertices.add(new Vector3D( 1, 0, 10+1));	// 1; back right bottom vertex
		vertices.add(new Vector3D( 0, 0, 10-1));	// 2; front bottom vertex
		vertices.add(new Vector3D( 0, 1, 10+0));	// 3; top vertex
		vertices.add(new Vector3D( 0, 0.5, 10+0));	// 4; inner vertex

		// create a new array of edges
		ArrayList<Edge> edges = new ArrayList<Edge>();

		// add a few vertices
		try {
//			// the tetrahedron with two inner vertices
//			edges.add(new Edge(0, 1));	// 0
//			edges.add(new Edge(1, 2));	// 1
//			edges.add(new Edge(2, 0));	// 2
//			edges.add(new Edge(3, 0));	// 3
//			edges.add(new Edge(3, 1));	// 4
//			edges.add(new Edge(3, 2));	// 5
//			edges.add(new Edge(4, 0));	// 6
//			edges.add(new Edge(4, 1));	// 7	
//			edges.add(new Edge(4, 2));	// 8
//			edges.add(new Edge(5, 0));	// 9
//			edges.add(new Edge(5, 1));	// 10
//			edges.add(new Edge(5, 2));	// 11
//			edges.add(new Edge(3, 4));	// 12
//			edges.add(new Edge(4, 5));	// 13

//			// the tetrahedron with a single inner vertex
			edges.add(new Edge(0, 1));	// 0 bottom edge #1
			edges.add(new Edge(1, 2));	// 1 bottom edge #2
			edges.add(new Edge(2, 0));	// 2 bottom edge #3
			edges.add(new Edge(0, 3));	// 3 top edge #1
			edges.add(new Edge(1, 3));	// 4 top edge #2
			edges.add(new Edge(2, 3));	// 5 top edge #3
			edges.add(new Edge(0, 4));	// 6 inner edge #1
			edges.add(new Edge(1, 4));	// 7 inner edge #1	
			edges.add(new Edge(2, 4));	// 8 inner edge #1
			edges.add(new Edge(3, 4));	// 9 inner edge #1

			// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred

			SimplicialComplex simplicialComplex;
			simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
			

			// create a new array of vertices
			ArrayList<Vector3D> verticesV = new ArrayList<Vector3D>();

			// add a few vertices

//			// a tetrahedron with two inner vertices
//			verticesV.add(new Vector3D(-1, 0, 10+1));	// 0
//			verticesV.add(new Vector3D( 1, 0, 10+1));	// 1
//			verticesV.add(new Vector3D( 0, 0, 10-1));	// 2
//			verticesV.add(new Vector3D( 0, 0.1, 10+0));	// 3; lower inner vertex
//			verticesV.add(new Vector3D( 0, 0.9, 10+0));	// 4; upper inner vertex
//			verticesV.add(new Vector3D( 0, 1, 10+0));	// 5; top vertex

			// a tetrahedron with a single inner vertex
			verticesV.add(new Vector3D(-1, 0, 10+1));	// 0; back left bottom vertex
			verticesV.add(new Vector3D( 1, 0, 10+1));	// 1; back right bottom vertex
			verticesV.add(new Vector3D( 0, 0, 10-1));	// 2; front bottom vertex
			verticesV.add(new Vector3D( 0, 1, 10+0));	// 3; top vertex
			verticesV.add(new Vector3D( 2, 0.75, 13));	// 4; inner vertex


			homogeneousPlanarImagingElementSimplicialComplex = new HomogeneousPlanarImagingSurfaceSimplicialComplex(simplicialComplex, verticesV);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		this.showImagingElements = true;
		this.showVertices = true;
		this.showVerticesV = true;
		this.vertexSurfaceProperty = SurfaceColour.DARK_BLUE_SHINY;
		this.vertexSurfacePropertyV = SurfaceColour.DARK_RED_SHINY;
		this.vertexRadius = 0.021;
		this.vertexRadiusV = 0.02;
		this.showEdges = true;
		this.showEdgesV = true;
		this.edgeSurfaceProperty = SurfaceColour.BLUE_SHINY;
		this.edgeSurfacePropertyV = SurfaceColour.RED_SHINY;
		this.edgeRadius = 0.021;
		this.edgeRadiusV = 0.02;
		this.showFaces = false;
		this.showFacesV = false;
		this.faceSurfaceProperty = SurfaceColour.LIGHT_BLUE_SHINY;
		this.faceSurfacePropertyV = SurfaceColour.LIGHT_RED_SHINY;
		
		populateSceneObjectCollection();
	}
	
	
	/**
	 * @param description
	 * @param simplicialComplex
	 * @param showImagingElements
	 * @param showVertices
	 * @param vertexSurfaceProperty
	 * @param vertexRadiusP
	 * @param showVerticesV
	 * @param vertexSurfacePropertyV
	 * @param vertexRadiusV
	 * @param showEdgesP
	 * @param edgeSurfaceProperty
	 * @param edgeRadiusP
	 * @param showEdgesV
	 * @param edgeSurfacePropertyV
	 * @param edgeRadiusV
	 * @param showFacesP
	 * @param faceSurfaceProperty
	 * @param showFacesV
	 * @param faceSurfacePropertyV
	 * @param parent
	 * @param studio
	 */
	public EditableHomogeneousPlanarImagingSurfaceSimplicialComplex(
			String description,
			HomogeneousPlanarImagingSurfaceSimplicialComplex homogeneousPlanarImagingElementSimplicialComplex,
			boolean showImagingElements,
			boolean showVertices,
			SurfaceProperty vertexSurfaceProperty,
			double vertexRadius,
			boolean showVerticesV,
			SurfaceProperty vertexSurfacePropertyV,
			double vertexRadiusV,
			boolean showEdges,
			SurfaceProperty edgeSurfaceProperty,
			double edgeRadius,
			boolean showEdgesV,
			SurfaceProperty edgeSurfacePropertyV,
			double edgeRadiusV,
			boolean showFaces,
			SurfaceProperty faceSurfaceProperty,
			boolean showFacesV,
			SurfaceProperty faceSurfacePropertyV,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		this.homogeneousPlanarImagingElementSimplicialComplex = homogeneousPlanarImagingElementSimplicialComplex;
		this.showImagingElements = showImagingElements;
		this.showVertices = showVertices;
		this.showVerticesV = showVerticesV;
		this.vertexSurfaceProperty = vertexSurfaceProperty;
		this.vertexSurfacePropertyV = vertexSurfacePropertyV;
		this.vertexRadius = vertexRadius;
		this.vertexRadiusV = vertexRadiusV;
		this.showEdges = showEdges;
		this.showEdgesV = showEdgesV;
		this.edgeSurfaceProperty = edgeSurfaceProperty;
		this.edgeSurfacePropertyV = edgeSurfacePropertyV;
		this.edgeRadius = edgeRadius;
		this.edgeRadiusV = edgeRadiusV;
		this.showFaces = showFaces;
		this.showFacesV = showFacesV;
		this.faceSurfaceProperty = faceSurfaceProperty;
		this.faceSurfacePropertyV = faceSurfacePropertyV;
		
		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableHomogeneousPlanarImagingSurfaceSimplicialComplex(EditableHomogeneousPlanarImagingSurfaceSimplicialComplex original)
	{
		this(
			original.getDescription(),
			original.getHomogeneousPlanarImagingElementSimplicialComplex(),
			original.isShowImagingElements(),
			original.isShowVertices(),
			original.getVertexSurfaceProperty(),
			original.getVertexRadius(),
			original.isShowVerticesV(),
			original.getVertexSurfacePropertyV(),
			original.getVertexRadiusV(),
			original.isShowEdges(),
			original.getEdgeSurfaceProperty(),
			original.getEdgeRadius(),
			original.isShowEdgesV(),
			original.getEdgeSurfacePropertyV(),
			original.getEdgeRadiusV(),
			original.isShowFaces(),
			original.getFaceSurfaceProperty(),
			original.isShowFacesV(),
			original.getFaceSurfacePropertyV(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableHomogeneousPlanarImagingSurfaceSimplicialComplex clone()
	{
		return new EditableHomogeneousPlanarImagingSurfaceSimplicialComplex(this);
	}

	
	//
	// setters and getters
	//

	public HomogeneousPlanarImagingSurfaceSimplicialComplex getHomogeneousPlanarImagingElementSimplicialComplex() {
		return homogeneousPlanarImagingElementSimplicialComplex;
	}

	public void setHomogeneousPlanarImagingElementSimplicialComplex(HomogeneousPlanarImagingSurfaceSimplicialComplex homogeneousPlanarImagingElementSimplicialComplex) {
		this.homogeneousPlanarImagingElementSimplicialComplex = homogeneousPlanarImagingElementSimplicialComplex;
	}

	public boolean isShowImagingElements() {
		return showImagingElements;
	}


	public void setShowImagingElements(boolean showImagingElements) {
		this.showImagingElements = showImagingElements;
	}


	public boolean isShowVertices() {
		return showVertices;
	}

	public void setShowVertices(boolean showVertices) {
		this.showVertices = showVertices;
	}

	public boolean isShowVerticesV() {
		return showVerticesV;
	}

	public void setShowVerticesV(boolean showVerticesV) {
		this.showVerticesV = showVerticesV;
	}

	public SurfaceProperty getVertexSurfaceProperty() {
		return vertexSurfaceProperty;
	}

	public void setVertexSurfaceProperty(SurfaceProperty vertexSurfaceProperty) {
		this.vertexSurfaceProperty = vertexSurfaceProperty;
	}

	public SurfaceProperty getVertexSurfacePropertyV() {
		return vertexSurfacePropertyV;
	}

	public void setVertexSurfacePropertyV(SurfaceProperty vertexSurfacePropertyV) {
		this.vertexSurfacePropertyV = vertexSurfacePropertyV;
	}

	public double getVertexRadius() {
		return vertexRadius;
	}

	public void setVertexRadius(double vertexRadius) {
		this.vertexRadius = vertexRadius;
	}

	public double getVertexRadiusV() {
		return vertexRadiusV;
	}

	public void setVertexRadiusV(double vertexRadiusV) {
		this.vertexRadiusV = vertexRadiusV;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public boolean isShowEdgesV() {
		return showEdgesV;
	}

	public void setShowEdgesV(boolean showEdgesV) {
		this.showEdgesV = showEdgesV;
	}

	public SurfaceProperty getEdgeSurfaceProperty() {
		return edgeSurfaceProperty;
	}

	public void setEdgeSurfaceProperty(SurfaceProperty edgeSurfaceProperty) {
		this.edgeSurfaceProperty = edgeSurfaceProperty;
	}

	public SurfaceProperty getEdgeSurfacePropertyV() {
		return edgeSurfacePropertyV;
	}

	public void setEdgeSurfacePropertyV(SurfaceProperty edgeSurfacePropertyV) {
		this.edgeSurfacePropertyV = edgeSurfacePropertyV;
	}

	public double getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public double getEdgeRadiusV() {
		return edgeRadiusV;
	}

	public void setEdgeRadiusV(double edgeRadiusV) {
		this.edgeRadiusV = edgeRadiusV;
	}

	public boolean isShowFaces() {
		return showFaces;
	}

	public void setShowFaces(boolean showFaces) {
		this.showFaces = showFaces;
	}

	public boolean isShowFacesV() {
		return showFacesV;
	}

	public void setShowFacesV(boolean showFacesV) {
		this.showFacesV = showFacesV;
	}

	public SurfaceProperty getFaceSurfaceProperty() {
		return faceSurfaceProperty;
	}

	public void setFaceSurfaceProperty(SurfaceProperty faceSurfaceProperty) {
		this.faceSurfaceProperty = faceSurfaceProperty;
	}

	public SurfaceProperty getFaceSurfacePropertyV() {
		return faceSurfacePropertyV;
	}

	public void setFaceSurfacePropertyV(SurfaceProperty faceSurfacePropertyV) {
		this.faceSurfacePropertyV = faceSurfacePropertyV;
	}

	
	
	//
	// add the scene objects that form this TO structure
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	protected void populateSceneObjectCollection()
	{
		// System.out.println("EditableHomogeneousPlanarImagingElementSimplicialComplex::populateSceneObjectCollection: Hi!");
		
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// add the scene objects representing the simplicial complex in physical space
		if(homogeneousPlanarImagingElementSimplicialComplex != null)
		homogeneousPlanarImagingElementSimplicialComplex.populateEditableSceneObjectCollection(
				this,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// imagingSurfaceTransmissionCoefficient
				false,	// imagingSurfaceShadowThrowing
				showVertices,
				vertexSurfaceProperty,
				vertexRadius,
				showVerticesV,
				vertexSurfacePropertyV,
				vertexRadiusV,
				showEdges,
				edgeSurfaceProperty,
				edgeRadius,
				showEdgesV,
				edgeSurfacePropertyV,
				edgeRadiusV,
				showFaces,
				faceSurfaceProperty,
				showFacesV,
				faceSurfacePropertyV,
				this,	// parent
				getStudio()
			);
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Homogeneous planar imaging element simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane, "wrap");
		
		//
		// the imaging-elements panel
		//
		
		JPanel imagingElementsPanel = new JPanel();
		imagingElementsPanel.setLayout(new MigLayout("insets 0"));
		
		showImagingElementsCheckBox = new JCheckBox("Show imaging elements");
		imagingElementsPanel.add(showImagingElementsCheckBox, "wrap");

		tabbedPane.addTab("Imaging elements", imagingElementsPanel);

		
		//
		// the physical-space-visualisation panel
		//
		
		JPanel visualisationPPanel = new JPanel();
		visualisationPPanel.setLayout(new MigLayout("insets 0"));
		
		showVerticesCheckBox = new JCheckBox("Show vertices");
		visualisationPPanel.add(showVerticesCheckBox, "wrap");

		vertexRadiusPanel = new LabelledDoublePanel("Radius of spheres representing vertices");
		visualisationPPanel.add(vertexRadiusPanel, "wrap");

		vertexSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(vertexSurfacePropertyPanel, "wrap");
		vertexSurfacePropertyPanel.setIPanel(iPanel);

		showEdgesCheckBox = new JCheckBox("Show edges");
		visualisationPPanel.add(showEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Radius of cylinders representing edges");
		visualisationPPanel.add(edgeRadiusPanel, "wrap");

		edgeSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(edgeSurfacePropertyPanel, "wrap");
		edgeSurfacePropertyPanel.setIPanel(iPanel);

		showFacesCheckBox = new JCheckBox("Show faces");
		visualisationPPanel.add(showFacesCheckBox, "wrap");

		faceSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(faceSurfacePropertyPanel, "wrap");
		faceSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Physical space", visualisationPPanel);

		//
		// the virtual-space-visualisation panel
		//
		
		JPanel visualisationVPanel = new JPanel();
		visualisationVPanel.setLayout(new MigLayout("insets 0"));
		
		showVerticesVCheckBox = new JCheckBox("Show vertices");
		visualisationVPanel.add(showVerticesVCheckBox, "wrap");

		vertexRadiusVPanel = new LabelledDoublePanel("Radius of spheres representing vertices");
		visualisationVPanel.add(vertexRadiusVPanel, "wrap");

		vertexSurfacePropertyVPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationVPanel.add(vertexSurfacePropertyVPanel, "wrap");
		vertexSurfacePropertyVPanel.setIPanel(iPanel);

		showEdgesVCheckBox = new JCheckBox("Show edges");
		visualisationVPanel.add(showEdgesVCheckBox, "wrap");

		edgeRadiusVPanel = new LabelledDoublePanel("Radius of cylinders representing edges");
		visualisationVPanel.add(edgeRadiusVPanel, "wrap");

		edgeSurfacePropertyVPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationVPanel.add(edgeSurfacePropertyVPanel, "wrap");
		edgeSurfacePropertyVPanel.setIPanel(iPanel);

		showFacesVCheckBox = new JCheckBox("Show faces");
		visualisationVPanel.add(showFacesVCheckBox, "wrap");

		faceSurfacePropertyVPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationVPanel.add(faceSurfacePropertyVPanel, "wrap");
		faceSurfacePropertyVPanel.setIPanel(iPanel);

		tabbedPane.addTab("Virtual space", visualisationVPanel);

		
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
		
		showImagingElementsCheckBox.setSelected(showImagingElements);
		
		showVerticesCheckBox.setSelected(showVertices);
		vertexSurfacePropertyPanel.setSurfaceProperty(vertexSurfaceProperty);
		vertexRadiusPanel.setNumber(vertexRadius);
		showEdgesCheckBox.setSelected(showEdges);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
		edgeRadiusPanel.setNumber(edgeRadius);
		showFacesCheckBox.setSelected(showFaces);
		faceSurfacePropertyPanel.setSurfaceProperty(faceSurfaceProperty);
		
		showVerticesVCheckBox.setSelected(showVerticesV);
		vertexSurfacePropertyVPanel.setSurfaceProperty(vertexSurfacePropertyV);
		vertexRadiusVPanel.setNumber(vertexRadiusV);
		showEdgesVCheckBox.setSelected(showEdgesV);
		edgeSurfacePropertyVPanel.setSurfaceProperty(edgeSurfacePropertyV);
		edgeRadiusVPanel.setNumber(edgeRadiusV);
		showFacesVCheckBox.setSelected(showFacesV);
		faceSurfacePropertyVPanel.setSurfaceProperty(faceSurfacePropertyV);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableHomogeneousPlanarImagingSurfaceSimplicialComplex acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setShowImagingElements(showImagingElementsCheckBox.isSelected());

		setShowVertices(showVerticesCheckBox.isSelected());
		setVertexSurfaceProperty(vertexSurfacePropertyPanel.getSurfaceProperty());
		setVertexRadius(vertexRadiusPanel.getNumber());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setShowFaces(showFacesCheckBox.isSelected());
		setFaceSurfaceProperty(faceSurfacePropertyPanel.getSurfaceProperty());
		
		setShowVerticesV(showVerticesVCheckBox.isSelected());
		setVertexSurfacePropertyV(vertexSurfacePropertyVPanel.getSurfaceProperty());
		setVertexRadiusV(vertexRadiusVPanel.getNumber());
		setShowEdgesV(showEdgesVCheckBox.isSelected());
		setEdgeSurfacePropertyV(edgeSurfacePropertyVPanel.getSurfaceProperty());
		setEdgeRadiusV(edgeRadiusVPanel.getNumber());
		setShowFacesV(showFacesVCheckBox.isSelected());
		setFaceSurfacePropertyV(faceSurfacePropertyVPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-homogeneous-planar-imaging-elements-simplicial-complex");
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