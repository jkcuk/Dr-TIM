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
 * An editable object representing a simplicial complex in (physical) space and in virtual space.
 * 
 * 
 * @author Johannes
 */
public class EditableTOSimplicialComplex extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 6374550291578895604L;

	/**
	 * the simplicial complex representing physical space
	 */
	protected SimplicialComplex simplicialComplexP;
	
	/**
	 * the simplicial complex representing virtual space
	 */
	protected SimplicialComplex simplicialComplexV;
	
	/**
	 * show spheres representing the vertices in physical space
	 */
	protected boolean showVerticesP;
	
	/**
	 * show spheres representing the vertices in virtual space
	 */
	protected boolean showVerticesV;
	
	/**
	 * surface property of the spheres representing the vertices in physical space
	 */
	protected SurfaceProperty vertexSurfacePropertyP;
	
	/**
	 * surface property of the spheres representing the vertices in virtual space
	 */
	protected SurfaceProperty vertexSurfacePropertyV;
	
	/**
	 * radius of the spheres representing the vertices in physical space
	 */
	protected double vertexRadiusP;

	/**
	 * radius of the spheres representing the vertices in virtual space
	 */
	protected double vertexRadiusV;

	/**
	 * show the edges of the simplicial complex representing physical space
	 */
	protected boolean showEdgesP;
	
	/**
	 * show the edges of the simplicial complex representing virtual space
	 */
	protected boolean showEdgesV;
	
	/**
	 * surface property of the cylinders representing the edges in physical space
	 */
	protected SurfaceProperty edgeSurfacePropertyP;

	/**
	 * surface property of the cylinders representing the edges in virtual space
	 */
	protected SurfaceProperty edgeSurfacePropertyV;
	
	/**
	 * radius of the cylinders representing the edges in physical space
	 */
	protected double edgeRadiusP;

	/**
	 * radius of the cylinders representing the edges in virtual space
	 */
	protected double edgeRadiusV;

	/**
	 * show the faces of the simplicial complex representing physical space
	 */
	protected boolean showFacesP;
	
	/**
	 * show the faces of the simplicial complex representing virtual space
	 */
	protected boolean showFacesV;
	
	/**
	 * surface property of the faces in physical space
	 */
	protected SurfaceProperty faceSurfacePropertyP;

	/**
	 * surface property of the faces in virtual space
	 */
	protected SurfaceProperty faceSurfacePropertyV;
	
	
	
	//
	// internal variables
	//
	
	// GUI panels
	protected LabelledDoublePanel vertexRadiusPPanel, vertexRadiusVPanel, edgeRadiusPPanel, edgeRadiusVPanel;
	protected JCheckBox showVerticesPCheckBox, showVerticesVCheckBox, showEdgesPCheckBox, showEdgesVCheckBox, showFacesPCheckBox, showFacesVCheckBox;
	protected SurfacePropertyPanel vertexSurfacePropertyPPanel, vertexSurfacePropertyVPanel, edgeSurfacePropertyPPanel, edgeSurfacePropertyVPanel, faceSurfacePropertyPPanel, faceSurfacePropertyVPanel;
	protected JButton convertButton;
	
	
	//
	// constructors
	//
	
	
	public EditableTOSimplicialComplex(
			String description,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);

		// create a physical-space simplicial complex
		
		// create a new array of vertices
		ArrayList<Vector3D> verticesP = new ArrayList<Vector3D>();
		
		// add a few vertices
		verticesP.add(new Vector3D(-1, 0, 10+1));	// 0
		verticesP.add(new Vector3D( 1, 0, 10+1));	// 1
		verticesP.add(new Vector3D( 0, 0, 10-1));	// 2
		verticesP.add(new Vector3D( 0, 0.3, 10+0));	// 3
		verticesP.add(new Vector3D( 0, 0.6, 10+0));	// 4
		verticesP.add(new Vector3D( 0, 1, 10+0));	// 5
		
		// create a new array of edges
		ArrayList<Edge> edgesP = new ArrayList<Edge>();

		// add a few vertices
		try {
			edgesP.add(new Edge(0, 1));	// 0
			edgesP.add(new Edge(1, 2));	// 1
			edgesP.add(new Edge(2, 0));	// 2
			edgesP.add(new Edge(3, 0));	// 3
			edgesP.add(new Edge(3, 1));	// 4
			edgesP.add(new Edge(3, 2));	// 5
			edgesP.add(new Edge(4, 0));	// 6
			edgesP.add(new Edge(4, 1));	// 7	
			edgesP.add(new Edge(4, 2));	// 8
			edgesP.add(new Edge(5, 0));	// 9
			edgesP.add(new Edge(5, 1));	// 10
			edgesP.add(new Edge(5, 2));	// 11
			edgesP.add(new Edge(3, 4));	// 12
			edgesP.add(new Edge(4, 5));	// 13
		} catch (InconsistencyException e1) {
			e1.printStackTrace();
		}


		// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred

		try {
			this.simplicialComplexP = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(verticesP, edgesP);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		// create a virtual-space simplicial complex

		// create a new array of vertices
		ArrayList<Vector3D> verticesV = new ArrayList<Vector3D>();

		// add a few vertices
		verticesV.add(new Vector3D(-1, 0, 10+1));	// 0
		verticesV.add(new Vector3D( 1, 0, 10+1));	// 1
		verticesV.add(new Vector3D( 0, 0, 10-1));	// 2
		verticesV.add(new Vector3D( 0, 0.1, 10+0));	// 3; lower inner vertex
		verticesV.add(new Vector3D( 0, 0.9, 10+0));	// 4; upper inner vertex
		verticesV.add(new Vector3D( 0, 1, 10+0));	// 5; top vertex
		
		// create a new array of edges
		ArrayList<Edge> edgesV = new ArrayList<Edge>();
		
		// create a copy of the array of edges in physical space
		try {
			for(Edge edge : edgesP) edgesV.add(new Edge(null, edge.getVertexIndices()));
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		// create a simplicial complex with the virtual-space vertices and standard edges, and with the faces and simplices inferred

		try {
			this.simplicialComplexV = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(verticesV, edgesV);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		this.showVerticesP = true;
		this.showVerticesV = true;
		this.vertexSurfacePropertyP = SurfaceColour.DARK_BLUE_SHINY;
		this.vertexSurfacePropertyV = SurfaceColour.DARK_RED_SHINY;
		this.vertexRadiusP = 0.031;
		this.vertexRadiusV = 0.03;
		this.showEdgesP = true;
		this.showEdgesV = true;
		this.edgeSurfacePropertyP = SurfaceColour.BLUE_SHINY;
		this.edgeSurfacePropertyV = SurfaceColour.RED_SHINY;
		this.edgeRadiusP = 0.021;
		this.edgeRadiusV = 0.02;
		this.showFacesP = false;
		this.showFacesV = false;
		this.faceSurfacePropertyP = SurfaceColour.LIGHT_BLUE_SHINY;
		this.faceSurfacePropertyV = SurfaceColour.LIGHT_RED_SHINY;
		
		populateSceneObjectCollection();


	}
	
	
	/**
	 * @param description
	 * @param simplicialComplexP
	 * @param simplicialComplexV
	 * @param showVerticesP
	 * @param vertexSurfacePropertyP
	 * @param vertexRadiusP
	 * @param showVerticesV
	 * @param vertexSurfacePropertyV
	 * @param vertexRadiusV
	 * @param showEdgesP
	 * @param edgeSurfacePropertyP
	 * @param edgeRadiusP
	 * @param showEdgesV
	 * @param edgeSurfacePropertyV
	 * @param edgeRadiusV
	 * @param showFacesP
	 * @param faceSurfacePropertyP
	 * @param showFacesV
	 * @param faceSurfacePropertyV
	 * @param parent
	 * @param studio
	 */
	public EditableTOSimplicialComplex(
			String description,
			SimplicialComplex simplicialComplexP,
			SimplicialComplex simplicialComplexV,
			boolean showVerticesP,
			SurfaceProperty vertexSurfacePropertyP,
			double vertexRadiusP,
			boolean showVerticesV,
			SurfaceProperty vertexSurfacePropertyV,
			double vertexRadiusV,
			boolean showEdgesP,
			SurfaceProperty edgeSurfacePropertyP,
			double edgeRadiusP,
			boolean showEdgesV,
			SurfaceProperty edgeSurfacePropertyV,
			double edgeRadiusV,
			boolean showFacesP,
			SurfaceProperty faceSurfacePropertyP,
			boolean showFacesV,
			SurfaceProperty faceSurfacePropertyV,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		this.simplicialComplexP = simplicialComplexP;
		this.simplicialComplexV = simplicialComplexV;
		this.showVerticesP = showVerticesP;
		this.showVerticesV = showVerticesV;
		this.vertexSurfacePropertyP = vertexSurfacePropertyP;
		this.vertexSurfacePropertyV = vertexSurfacePropertyV;
		this.vertexRadiusP = vertexRadiusP;
		this.vertexRadiusV = vertexRadiusV;
		this.showEdgesP = showEdgesP;
		this.showEdgesV = showEdgesV;
		this.edgeSurfacePropertyP = edgeSurfacePropertyP;
		this.edgeSurfacePropertyV = edgeSurfacePropertyV;
		this.edgeRadiusP = edgeRadiusP;
		this.edgeRadiusV = edgeRadiusV;
		this.showFacesP = showFacesP;
		this.showFacesV = showFacesV;
		this.faceSurfacePropertyP = faceSurfacePropertyP;
		this.faceSurfacePropertyV = faceSurfacePropertyV;
		
		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableTOSimplicialComplex(EditableTOSimplicialComplex original)
	{
		this(
			original.getDescription(),
			original.getSimplicialComplexP(),
			original.getSimplicialComplexV(),
			original.isShowVerticesP(),
			original.getVertexSurfacePropertyP(),
			original.getVertexRadiusP(),
			original.isShowVerticesV(),
			original.getVertexSurfacePropertyV(),
			original.getVertexRadiusV(),
			original.isShowEdgesP(),
			original.getEdgeSurfacePropertyP(),
			original.getEdgeRadiusP(),
			original.isShowEdgesV(),
			original.getEdgeSurfacePropertyV(),
			original.getEdgeRadiusV(),
			original.isShowFacesP(),
			original.getFaceSurfacePropertyP(),
			original.isShowFacesV(),
			original.getFaceSurfacePropertyV(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableTOSimplicialComplex clone()
	{
		return new EditableTOSimplicialComplex(this);
	}

	
	//
	// setters and getters
	//

	public SimplicialComplex getSimplicialComplexP() {
		return simplicialComplexP;
	}

	public void setSimplicialComplexP(SimplicialComplex simplicialComplexP) {
		this.simplicialComplexP = simplicialComplexP;
	}

	public SimplicialComplex getSimplicialComplexV() {
		return simplicialComplexV;
	}

	public void setSimplicialComplexV(SimplicialComplex simplicialComplexV) {
		this.simplicialComplexV = simplicialComplexV;
	}

	public boolean isShowVerticesP() {
		return showVerticesP;
	}

	public void setShowVerticesP(boolean showVerticesP) {
		this.showVerticesP = showVerticesP;
	}

	public boolean isShowVerticesV() {
		return showVerticesV;
	}

	public void setShowVerticesV(boolean showVerticesV) {
		this.showVerticesV = showVerticesV;
	}

	public SurfaceProperty getVertexSurfacePropertyP() {
		return vertexSurfacePropertyP;
	}

	public void setVertexSurfacePropertyP(SurfaceProperty vertexSurfacePropertyP) {
		this.vertexSurfacePropertyP = vertexSurfacePropertyP;
	}

	public SurfaceProperty getVertexSurfacePropertyV() {
		return vertexSurfacePropertyV;
	}

	public void setVertexSurfacePropertyV(SurfaceProperty vertexSurfacePropertyV) {
		this.vertexSurfacePropertyV = vertexSurfacePropertyV;
	}

	public double getVertexRadiusP() {
		return vertexRadiusP;
	}

	public void setVertexRadiusP(double vertexRadiusP) {
		this.vertexRadiusP = vertexRadiusP;
	}

	public double getVertexRadiusV() {
		return vertexRadiusV;
	}

	public void setVertexRadiusV(double vertexRadiusV) {
		this.vertexRadiusV = vertexRadiusV;
	}

	public boolean isShowEdgesP() {
		return showEdgesP;
	}

	public void setShowEdgesP(boolean showEdgesP) {
		this.showEdgesP = showEdgesP;
	}

	public boolean isShowEdgesV() {
		return showEdgesV;
	}

	public void setShowEdgesV(boolean showEdgesV) {
		this.showEdgesV = showEdgesV;
	}

	public SurfaceProperty getEdgeSurfacePropertyP() {
		return edgeSurfacePropertyP;
	}

	public void setEdgeSurfacePropertyP(SurfaceProperty edgeSurfacePropertyP) {
		this.edgeSurfacePropertyP = edgeSurfacePropertyP;
	}

	public SurfaceProperty getEdgeSurfacePropertyV() {
		return edgeSurfacePropertyV;
	}

	public void setEdgeSurfacePropertyV(SurfaceProperty edgeSurfacePropertyV) {
		this.edgeSurfacePropertyV = edgeSurfacePropertyV;
	}

	public double getEdgeRadiusP() {
		return edgeRadiusP;
	}

	public void setEdgeRadiusP(double edgeRadiusP) {
		this.edgeRadiusP = edgeRadiusP;
	}

	public double getEdgeRadiusV() {
		return edgeRadiusV;
	}

	public void setEdgeRadiusV(double edgeRadiusV) {
		this.edgeRadiusV = edgeRadiusV;
	}

	public boolean isShowFacesP() {
		return showFacesP;
	}

	public void setShowFacesP(boolean showFacesP) {
		this.showFacesP = showFacesP;
	}

	public boolean isShowFacesV() {
		return showFacesV;
	}

	public void setShowFacesV(boolean showFacesV) {
		this.showFacesV = showFacesV;
	}

	public SurfaceProperty getFaceSurfacePropertyP() {
		return faceSurfacePropertyP;
	}

	public void setFaceSurfacePropertyP(SurfaceProperty faceSurfacePropertyP) {
		this.faceSurfacePropertyP = faceSurfacePropertyP;
	}

	public SurfaceProperty getFaceSurfacePropertyV() {
		return faceSurfacePropertyV;
	}

	public void setFaceSurfacePropertyV(SurfaceProperty faceSurfacePropertyV) {
		this.faceSurfacePropertyV = faceSurfacePropertyV;
	}

	
	//
	// the clever bit
	//
	
//	/**
//	 * infer the imaging properties of all faces
//	 * @throws InconsistencyException 
//	 */
//	protected void setFacesImagingProperties()
//	throws InconsistencyException
//	{
//		// go through all faces in this simplicial complex, starting with the outside ones and working "inwards", i.e. towards a higher number
//		// of other faces on the way to the outside
//		
//		// first, work out what the highest number of faces is to the outside
//		int noOfFacesToOutsideMax = -1;
//		// go through all the faces...
//		for(Face face : simplicialComplexP.getFaces())
//		{
//			// ... and if the current face's <i>noOfFacesToOutside</i> is greater than <i>noOfFacesToOutsideMax</i>,
//			// set <i>noOfFacesToOutsideMax</i> to the current face's <i>noOfFacesToOutside</i>
//			noOfFacesToOutsideMax = Math.max(face.getNoOfFacesToOutside(), noOfFacesToOutsideMax);
//		}
//		
//		// now go through all possible values of <i>noOfFacesToOutside</i>, starting with 0, ...
//		for(int noOfFacesToOutside = 0; noOfFacesToOutside < noOfFacesToOutsideMax; noOfFacesToOutside++)
//		{
//			// ... go through all faces...
//			for(Face face : simplicialComplexP.getFaces())
//			{
//				// ... whose <i>noOfFacesToOutside</i> equals <i>noOfFacesToOutside</i>, ...
//				if(face.getNoOfFacesToOutside() == noOfFacesToOutside)
//				{
//					// ... and create a list of the faces connecting this one to the outside, initially starting from the inside...
//					ArrayList<Face> facesFromOutside = face.getFacesBetweenThisAndOutside();
//					// ... but then reversing the array, so that it is starting on the outside
//					Collections.reverse(facesFromOutside);
//					
//					// all these faces in combination must then image between the virtual-space and physical-space positions
//					// of the "other" vertex of the innermost simplex, so identify this vertex
//					
//					// first find the vertex of the next outermost face that is not a vertex of this face
//					
//					// create an array of all vertex indices of this face...
//					int[] vertexIndicesThis = face.getVertexIndices();
//					// ... and the next outermost face
//					int[] vertexIndicesNextOutermost = face.getOutermostNeighbouringFace().getVertexIndices();
//					
//					// then go through all the vertex indices in the next outermost face...
//					int v;
//					for(
//							v=0;
//							(v<3) && // keep going while we haven't gone through all indices of the next outermost face...
//							IndexArray.isInArray(vertexIndicesNextOutermost[v], vertexIndicesThis);	// ... and the current one is a vertex index of this face
//							v++
//						);
//					// have we found the "other" vertex index?
//					if(v > 2)
//					{
//						// no, we haven't!
//						throw new InconsistencyException("Cannot find a vertex index of face 2 that is not a vertex index of face 1.  face 1: "+face+"; face 2: "+face.getOutermostNeighbouringFace());
//					}
//					// the other vertex index is
//					int vertexIndex = vertexIndicesNextOutermost[v];
//					
//					// now check the two simplices on either side of this face and pick the one that doesn't contain vertex #vertexIndex
//					int s;
//					for(s=0; (s<2) && face.getSimplex(s).isVertex(vertexIndex); s++);
//					// have we found the "other" simplex index?
//					if(s > 1)
//					{
//						// no, we haven't!
//						throw new InconsistencyException("Cannot find a simplex on either side of face that does not contain vertex #"+vertexIndex+". face: "+face);
//					}
//					Simplex simplex = face.getSimplex(s);
//					
//					// index of the vertex whose virtual-space position the faces have to image to its physical-space position
//					int indexOfVertex = simplex.getFirstVertexIndexNotIn(face.getVertexIndices());
//
//					HERE
//					
//					// go through this list of faces
//
//				}
//			}
//		}
//	}
	
	
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
		if(simplicialComplexP != null)
		addSceneObject(simplicialComplexP.getEditableSceneObjectCollection(
				"Simplicial complex in physical space",	// description
				showVerticesP,
				vertexSurfacePropertyP,
				vertexRadiusP,
				showEdgesP,
				edgeSurfacePropertyP,
				edgeRadiusP,
				showFacesP,
				faceSurfacePropertyP,
				this,	// parent
				getStudio()
			));

		// add the scene objects representing the simplicial complex in virtual space
		if(simplicialComplexV != null)
		addSceneObject(simplicialComplexV.getEditableSceneObjectCollection(
				"Simplicial complex in virtual space",	// description
				showVerticesV,
				vertexSurfacePropertyV,
				vertexRadiusV,
				showEdgesV,
				edgeSurfacePropertyV,
				edgeRadiusV,
				showFacesV,
				faceSurfacePropertyV,
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("TO simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane);
		
		//
		// the physical-space-visualisation panel
		//
		
		JPanel visualisationPPanel = new JPanel();
		visualisationPPanel.setLayout(new MigLayout("insets 0"));
		
		showVerticesPCheckBox = new JCheckBox("Show vertices");
		visualisationPPanel.add(showVerticesPCheckBox, "wrap");

		vertexRadiusPPanel = new LabelledDoublePanel("Radius of spheres representing vertices");
		visualisationPPanel.add(vertexRadiusPPanel, "wrap");

		vertexSurfacePropertyPPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(vertexSurfacePropertyPPanel, "wrap");
		vertexSurfacePropertyPPanel.setIPanel(iPanel);

		showEdgesPCheckBox = new JCheckBox("Show edges");
		visualisationPPanel.add(showEdgesPCheckBox, "wrap");

		edgeRadiusPPanel = new LabelledDoublePanel("Radius of cylinders representing edges");
		visualisationPPanel.add(edgeRadiusPPanel, "wrap");

		edgeSurfacePropertyPPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(edgeSurfacePropertyPPanel, "wrap");
		edgeSurfacePropertyPPanel.setIPanel(iPanel);

		showFacesPCheckBox = new JCheckBox("Show faces");
		visualisationPPanel.add(showFacesPCheckBox, "wrap");

		faceSurfacePropertyPPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(faceSurfacePropertyPPanel, "wrap");
		faceSurfacePropertyPPanel.setIPanel(iPanel);

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
		
		showVerticesPCheckBox.setSelected(showVerticesP);
		vertexSurfacePropertyPPanel.setSurfaceProperty(vertexSurfacePropertyP);
		vertexRadiusPPanel.setNumber(vertexRadiusP);
		showEdgesPCheckBox.setSelected(showEdgesP);
		edgeSurfacePropertyPPanel.setSurfaceProperty(edgeSurfacePropertyP);
		edgeRadiusPPanel.setNumber(edgeRadiusP);
		showFacesPCheckBox.setSelected(showFacesP);
		faceSurfacePropertyPPanel.setSurfaceProperty(faceSurfacePropertyP);
		
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
	public EditableTOSimplicialComplex acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setShowVerticesP(showVerticesPCheckBox.isSelected());
		setVertexSurfacePropertyP(vertexSurfacePropertyPPanel.getSurfaceProperty());
		setVertexRadiusP(vertexRadiusPPanel.getNumber());
		setShowEdgesP(showEdgesPCheckBox.isSelected());
		setEdgeSurfacePropertyP(edgeSurfacePropertyPPanel.getSurfaceProperty());
		setEdgeRadiusP(edgeRadiusPPanel.getNumber());
		setShowFacesP(showFacesPCheckBox.isSelected());
		setFaceSurfacePropertyP(faceSurfacePropertyPPanel.getSurfaceProperty());
		
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
		iPanel.replaceFrontComponent(container, "Edit ex-TO-simplicial-complex");
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