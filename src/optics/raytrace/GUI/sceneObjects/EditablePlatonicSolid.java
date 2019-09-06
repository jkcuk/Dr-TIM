package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComboBoxPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;

/**
 * A Platonic solid
 * 
 * @author Johannes
 */
public class EditablePlatonicSolid extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 7122477603061760725L;


	//
	// parameters
	//
	
	/**
	 * The Platonic solid on which this lens is based.
	 */
	private PlatonicSolidType platonicSolid;
	
	/**
	 * Centre of the platonic solid.
	 */
	private Vector3D centre;
	
	/**
	 * The Platonic solids are in whatever orientation Mathematica, where the data are from, puts them.
	 * Interpret these coordinates as coordinates in an (u, v, w) basis
	 */
	private BasisVectors3D basisVectors;
	
	/**
	 * Radius
	 */
	private double radius;
	
	private boolean showVertices, showEdges, showFaces;
	private double vertexRadius, edgeRadius;
	private SurfaceProperty vertexSurfaceProperty, edgeSurfaceProperty, faceSurfaceProperty;
	


	//
	// internal variables
	//
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection vertices, edges, faces;


	// GUI panels
	private JComboBox<PlatonicSolidType> platonicSolidComboBox;
	private LabelledDoublePanel radiusPanel, vertexRadiusPanel, edgeRadiusPanel;
	private LabelledVector3DPanel centrePanel;
	private JButton convertButton;
	private JCheckBox showVerticesCheckBox, showEdgesCheckBox, showFacesCheckBox;
	private SurfacePropertyPanel
		// vertexSurfacePropertyPanel, edgeSurfacePropertyPanel, 
		faceSurfacePropertyPanel;
	
	/**
	 * @param description
	 * @param platonicSolid
	 * @param centre
	 * @param basisVectors
	 * @param radius
	 * @param showVertices
	 * @param showEdges
	 * @param showFaces
	 * @param vertexRadius
	 * @param edgeRadius
	 * @param vertexSurfaceProperty
	 * @param edgeSurfaceProperty
	 * @param faceSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditablePlatonicSolid(
			String description,
			PlatonicSolidType platonicSolid,
			Vector3D centre,
			BasisVectors3D basisVectors,
			double radius,
			boolean showVertices,
			boolean showEdges,
			boolean showFaces,
			double vertexRadius,
			double edgeRadius,
			SurfaceProperty vertexSurfaceProperty,
			SurfaceProperty edgeSurfaceProperty,
			SurfaceProperty faceSurfaceProperty,
			SceneObject parent, 
			Studio studio
			)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setPlatonicSolid(platonicSolid);
		setCentre(centre);
		setBasisVectors(basisVectors);
		setRadius(radius);
		setShowVertices(showVertices);
		setShowEdges(showEdges);
		setShowFaces(showFaces);
		setVertexRadius(vertexRadius);
		setEdgeRadius(edgeRadius);
		setVertexSurfaceProperty(vertexSurfaceProperty);
		setEdgeSurfaceProperty(edgeSurfaceProperty);
		setFaceSurfaceProperty(faceSurfaceProperty);

		populateSceneObjectCollection();
	}

	public EditablePlatonicSolid(SceneObject parent, Studio studio)
	{

		this(
				"Platonic solid",	// description
				PlatonicSolidType.TETRAHEDRON,	// platonicSolid
				new Vector3D(0, 0, 10),	// centre
				BasisVectors3D.getOrthonormalXYZBasisVectors(),	// basisVectors
				1,	// radius
				true,	// showVertices
				true,	// showEdges
				true,	// showFaces
				0.01,	// vertexRadius
				0.01,	// edgeRadius
				SurfaceColour.RED_SHINY,	// vertexSurfaceProperty
				SurfaceColour.RED_SHINY,	// edgeSurfaceProperty
				new Transparent(0.75, true),	// faceSurfaceProperty
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePlatonicSolid(EditablePlatonicSolid original)
	{
		this(
			original.getDescription(),
			original.getPlatonicSolid(),
			original.getCentre().clone(),
			original.getBasisVectors().clone(),
			original.getRadius(),
			original.isShowVertices(),
			original.isShowEdges(),
			original.isShowFaces(),
			original.getVertexRadius(),
			original.getEdgeRadius(),
			original.getVertexSurfaceProperty().clone(),
			original.getEdgeSurfaceProperty().clone(),
			original.getFaceSurfaceProperty().clone(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePlatonicSolid clone()
	{
		return new EditablePlatonicSolid(this);
	}

	
	//
	// setters and getters
	//
	
	public PlatonicSolidType getPlatonicSolid() {
		return platonicSolid;
	}

	public void setPlatonicSolid(PlatonicSolidType platonicSolid) {
		this.platonicSolid = platonicSolid;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public BasisVectors3D getBasisVectors() {
		return basisVectors;
	}

	public void setBasisVectors(BasisVectors3D basisVectors) {
		this.basisVectors = basisVectors;
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public boolean isShowVertices() {
		return showVertices;
	}

	public void setShowVertices(boolean showVertices) {
		this.showVertices = showVertices;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public boolean isShowFaces() {
		return showFaces;
	}

	public void setShowFaces(boolean showFaces) {
		this.showFaces = showFaces;
	}

	public double getVertexRadius() {
		return vertexRadius;
	}

	public void setVertexRadius(double vertexRadius) {
		this.vertexRadius = vertexRadius;
	}

	public double getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public SurfaceProperty getVertexSurfaceProperty() {
		return vertexSurfaceProperty;
	}

	public void setVertexSurfaceProperty(SurfaceProperty vertexSurfaceProperty) {
		this.vertexSurfaceProperty = vertexSurfaceProperty;
	}

	public SurfaceProperty getEdgeSurfaceProperty() {
		return edgeSurfaceProperty;
	}

	public void setEdgeSurfaceProperty(SurfaceProperty edgeSurfaceProperty) {
		this.edgeSurfaceProperty = edgeSurfaceProperty;
	}

	public SurfaceProperty getFaceSurfaceProperty() {
		return faceSurfaceProperty;
	}

	public void setFaceSurfaceProperty(SurfaceProperty faceSurfaceProperty) {
		this.faceSurfaceProperty = faceSurfaceProperty;
	}

	
	//
	// convenience methods
	//
	
	public Vector3D getVertex(int i)
	{
		return Vector3D.sum(
				centre,
				platonicSolid.getVertex(i).fromBasis(basisVectors.getArrayListOfBasisVectors3D()).getWithLength(radius)
			);
	}
	
	public int[] getEdge(int i)
	{
		return platonicSolid.getEdges()[i];
	}
	
	public int[] getFace(int i)
	{
		return platonicSolid.getFaces()[i];
	}
	
	public int getNumberOfVertices()
	{
		return platonicSolid.getNumberOfVertices();
	}
	
	public int getNumberOfEdges()
	{
		return platonicSolid.getNumberOfEdges();
	}
	
	public int getNumberOfFaces()
	{
		return platonicSolid.getNumberOfFaces();
	}
	
	public int getNumberOfVerticesPerFace()
	{
		return platonicSolid.getNumberOfVerticesPerFace();
	}
	
	public Vector3D getOutwardFaceNormal(int faceIndex)
	{
		return platonicSolid.getOutwardFaceNormal(faceIndex);
	}
	
	private void addVertices()
	{
		for(int i=0; i<platonicSolid.getNumberOfVertices(); i++)
		{
			vertices.addSceneObject(new EditableScaledParametrisedSphere(
				"Vertex #"+(i+1),
				getVertex(i),	// centre
				vertexRadius,	// radius
				vertexSurfaceProperty,
				vertices,
				getStudio()
			));
		}
	}
	
	private void addEdges()
	{
		for(int i=0; i<platonicSolid.getNumberOfEdges(); i++)
		{
			edges.addSceneObject(new EditableParametrisedCylinder(
				"Edge #"+(i+1),
				getVertex(platonicSolid.getEdges()[i][0]),	// start point
				getVertex(platonicSolid.getEdges()[i][1]),	// end point
				edgeRadius,	// radius
				edgeSurfaceProperty,
				vertices,
				getStudio()
			));
		}
	}
	
	private void addFaces()
	{
		for(int i=0; i<platonicSolid.getNumberOfFaces(); i++)
		{
			int[] faceIndices = platonicSolid.getFaces()[i];
			Vector3D[] faceVertices = new Vector3D[platonicSolid.getNumberOfVerticesPerFace()];
			
			for(int j=0; j<platonicSolid.getNumberOfVerticesPerFace(); j++)
			{
				faceVertices[j] = getVertex(faceIndices[j]);
			}
			
			faces.addSceneObject(
						new EditableParametrisedConvexPolygon(
								"Face "+(i+1),	// description,
						platonicSolid.getOutwardFaceNormal(i),	// normalToPlane,
						faceVertices,	// vertices[],
						faceSurfaceProperty,	// surfaceProperty
						faces,	// parent
						getStudio()
					));
		}
	}
		

	/**
	 * (re)populate the scene-object collection with scene objects that represent the current parameters
	 */
	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the lenses...
		vertices = new EditableSceneObjectCollection("Vertices", true, this, getStudio());
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());
		faces = new EditableSceneObjectCollection("Faces", true, this, getStudio());
		
		// populate these collections
		addVertices();
		addEdges();
		addFaces();
		
		// add these collections to this collection
		addSceneObject(vertices, showVertices);
		addSceneObject(edges, showEdges);
		addSceneObject(faces, showFaces);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Platonic solid"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
//		JTabbedPane tabbedPane = new JTabbedPane();
		

		//
		// the basic-parameters panel
		// 
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		platonicSolidComboBox = new JComboBox<PlatonicSolidType>(PlatonicSolidType.values());
		basicParametersPanel.add(
				new LabelledComboBoxPanel(
						"Type",
						platonicSolidComboBox
					),
					"wrap"
				);

		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");

		// basisVectors
		// TODO

		radiusPanel = new LabelledDoublePanel("Radius");
		basicParametersPanel.add(radiusPanel, "wrap");

		showVerticesCheckBox = new JCheckBox("Show vertices");
		basicParametersPanel.add(showVerticesCheckBox, "wrap");

		showEdgesCheckBox = new JCheckBox("Show edges");
		basicParametersPanel.add(showEdgesCheckBox, "wrap");

		showFacesCheckBox = new JCheckBox("Show faces");
		basicParametersPanel.add(showFacesCheckBox, "wrap");
		
//		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		
		//
		// the vertex panel
		// 
		
//		JPanel vertexPanel = new JPanel();
//		vertexPanel.setLayout(new MigLayout("insets 0"));

		vertexRadiusPanel = new LabelledDoublePanel("Vertex radius");
		basicParametersPanel.add(vertexRadiusPanel, "wrap");

//		vertexSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
//		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
//		vertexPanel.add(vertexSurfacePropertyPanel, "wrap");
//		vertexSurfacePropertyPanel.setIPanel(iPanel);

//		tabbedPane.addTab("Vertex parameters", vertexPanel);
		
		
		//
		// the edge panel
		// 
		
//		JPanel edgePanel = new JPanel();
//		edgePanel.setLayout(new MigLayout("insets 0"));
		
		edgeRadiusPanel = new LabelledDoublePanel("Edge radius");
		basicParametersPanel.add(edgeRadiusPanel, "wrap");

//		edgeSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
//		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
//		basicParametersPanel.add(edgeSurfacePropertyPanel, "wrap");
//		edgeSurfacePropertyPanel.setIPanel(iPanel);

//		tabbedPane.addTab("Edge parameters", edgePanel);
		
		
		//
		// the face panel
		// 
		
//		JPanel facePanel = new JPanel();
//		facePanel.setLayout(new MigLayout("insets 0"));
		
		faceSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		basicParametersPanel.add(faceSurfacePropertyPanel, "wrap");
		faceSurfacePropertyPanel.setIPanel(iPanel);

//		tabbedPane.addTab("Face parameters", facePanel);

		
		editPanel.add(basicParametersPanel, "wrap");

		// tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
//		JPanel framePanel = new JPanel();
//		framePanel.setLayout(new MigLayout("insets 0"));
//
//		showFramesCheckBox = new JCheckBox("Show frames");
//		framePanel.add(showFramesCheckBox, "wrap");
//		
//		frameRadiusPanel = new LabelledDoublePanel("frame cylinder radius");
//		framePanel.add(frameRadiusPanel, "wrap");
//		
//
//		tabbedPane.addTab("Frames", framePanel);
//		
//		editPanel.add(tabbedPane, "wrap");

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
		platonicSolidComboBox.setSelectedItem(platonicSolid);
		centrePanel.setVector3D(centre);
		// basisVectors
		radiusPanel.setNumber(radius);
		showVerticesCheckBox.setSelected(showVertices);
		showEdgesCheckBox.setSelected(showEdges);
		showFacesCheckBox.setSelected(showFaces);
		vertexRadiusPanel.setNumber(vertexRadius);
		edgeRadiusPanel.setNumber(edgeRadius);
//		vertexSurfacePropertyPanel.setSurfaceProperty(vertexSurfaceProperty);		
//		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);		
		faceSurfacePropertyPanel.setSurfaceProperty(faceSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePlatonicSolid acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setPlatonicSolid((PlatonicSolidType)(platonicSolidComboBox.getSelectedItem()));
		setCentre(centrePanel.getVector3D());		
		// setBasisVectors(basisVectors);
		setRadius(radiusPanel.getNumber());
		setShowVertices(showVerticesCheckBox.isSelected());
		setShowEdges(showEdgesCheckBox.isSelected());
		setShowFaces(showFacesCheckBox.isSelected());
		setVertexRadius(vertexRadiusPanel.getNumber());
		setEdgeRadius(edgeRadiusPanel.getNumber());
//		setVertexSurfaceProperty(vertexSurfacePropertyPanel.getSurfaceProperty());
//		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
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
		iPanel.replaceFrontComponent(container, "Edit ex-Platonic-body");
		container.setValuesInEditPanel();
	}
	
	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(edited instanceof SurfaceProperty)
		{
			// face surface property has been edited
			// setFaceSurfaceProperty((SurfaceProperty)edited);
			// faceSurfacePropertyPanel.setSurfaceProperty(getFaceSurfaceProperty());
			faceSurfacePropertyPanel.setSurfaceProperty((SurfaceProperty)edited);
		}
	}
}