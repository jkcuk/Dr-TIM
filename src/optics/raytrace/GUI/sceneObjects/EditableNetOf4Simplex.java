package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.Geometry;
import math.MyMath;
import math.NamedVector3D;
import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.IndexArray;
import math.simplicialComplex.NamedEdge;
import math.simplicialComplex.Simplex;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;

/**
 * <p>
 * An editable object representing the 3D net of a (4D) 4-simplex (also known, confusingly, as 5-cell).
 * This is a simplicial complex in which a simplex is attached to each face of a central simplex.
 * </p>
 * <p>
 * At the moment, the 4-simplex is regular, i.e. all 5 simplices are regular tetrahedra, all of the same size (but with different orientations).
 * </p>
 * @author Johannes, Dimitris
 */
public class EditableNetOf4Simplex extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -8866404289547641316L;

	/**
	 * The list of vertex positions.
	 * Note that these are all of type NamedVector3D, which means they can come with little descriptions.
	 */
	private ArrayList<NamedVector3D> vertices;

	/**
	 * The list of edges, which are all named, to make editing easier.
	 */
	private ArrayList<NamedEdge> edges;

	/**
	 * show the null-space wedges
	 */
	protected boolean showNullSpaceWedges;

	/**
	 * Leg length of each null-space wedge.
	 * Each of the wedges is an extruded isoceles triangle of leg length <i>legLength</i>.
	 */
	// protected double nullSpaceWedgeLegLength;
	
	/**
	 * Factor by which the minimum leg length of each null-space wedge is multiplied.
	 */
	protected double nullSpaceWedgeLegLengthFactor;

	/**
	 * Transmission coefficient of each null-space-wedge surface
	 */
	protected double nullSpaceWedgeSurfaceTransmissionCoefficient;
		
	/**
	 * Null-space-wedge type, which describes the way this null-space wedge is realised
	 */
	protected GluingType gluingType;
	
	/**
	 * Number of negative-space wedges, <i>N</i>, in the null-space wedge
	 */
	protected int numberOfNegativeSpaceWedges;

	/**
	 * show edges of the simplicial complex that forms the net of the 4-simplex
	 */
	protected boolean showNetEdges;
	
	/**
	 * show faces of the simplicial complex that forms the net of the 4-simplex
	 */
	protected boolean showNetFaces;
	
	/**
	 * show edges of the null-space wedges, i.e. the vertices and edges of the sheets forming the null-space wedges
	 */
	protected boolean showNullSpaceWedgeEdges;

	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty netEdgeSurfaceProperty;
	
	/**
	 * surface property of the faces of the net
	 */
	protected SurfaceProperty netFaceSurfaceProperty;
	
	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty;

	/**
	 * radius of the spheres and cylinders representing the vertices and edges
	 */
	protected double edgeRadius;
	
//	/**
//	 * for debugging; if this variable takes a positive value, show only null-space wedge #showOnlyNullSpaceWedgeNo
//	 */
	protected int showOnlyNullSpaceWedgeNo;	// TODO for debugging
	
	
	
	//
	// internal variables
	//
	
	/**
	 * the corresponding simplicial complex
	 */
	private SimplicialComplex simplicialComplex;
	
	//
	// constructors
	//
	
		
	
	/**
	 * Set the parameters.
	 * If <i>vertices</i> and <i>edges</i> are given, the scene objects representing this net of a 4-simplex are added.
	 * If <i>vertices</i> and <i>edges</i> are not given, <i>vertices</i> and <i>edges</i> must be set later, followed by a call of the
	 * <i>populateSceneObjectCollection()</i> method to add the scene objects.
	 * @see optics.raytrace.GUI.sceneObjects.EditableNetOf4Simplex.populateSceneObjectCollection()
	 * 
	 * @param description
	 * @param vertices	all vertices in the net
	 * @param edges	the edges in the net
	 * @param showNullSpaceWedges
	 * @param nullSpaceWedgeLegLengthFactor
	 * @param nullSpaceWedgeHeight
	 * @param refractingSurfaceTransmissionCoefficient
	 * @param nullSpaceWedgeType
	 * @param numberOfNegativeSpaceWedges
	 * @param showNetEdges
	 * @param showNullSpaceWedgeEdges
	 * @param netEdgeSurfaceProperty
	 * @param netFaceSurfaceProperty
	 * @param nullSpaceWedgeEdgeSurfaceProperty
	 * @param structureTubeRadius
	 * @param parent
	 * @param studio
	 */
	public EditableNetOf4Simplex(
			String description,
			ArrayList<NamedVector3D> vertices,
			ArrayList<NamedEdge> edges,
			boolean showNullSpaceWedges,
			double nullSpaceWedgeLegLengthFactor,
			double nullSpaceWedgeSurfaceTransmissionCoefficient,
			GluingType gluingType,
			int numberOfNegativeSpaceWedges,
			boolean showNetEdges,
			boolean showNetFaces,
			boolean showNullSpaceWedgeEdges,
			SurfaceProperty netEdgeSurfaceProperty,
			SurfaceProperty netFaceSurfaceProperty,
			SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty,
			double structureTubeRadius,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		this.vertices = vertices;
		this.edges = edges;
		this.showNullSpaceWedges = showNullSpaceWedges;
		this.nullSpaceWedgeLegLengthFactor = nullSpaceWedgeLegLengthFactor;
		this.nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficient;
		this.gluingType = gluingType;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		this.showNetEdges = showNetEdges;
		this.showNetFaces = showNetFaces;
		this.showNullSpaceWedgeEdges = showNullSpaceWedgeEdges;
		this.netEdgeSurfaceProperty = netEdgeSurfaceProperty;
		this.netFaceSurfaceProperty = netFaceSurfaceProperty;
		this.nullSpaceWedgeEdgeSurfaceProperty = nullSpaceWedgeEdgeSurfaceProperty;
		this.edgeRadius = structureTubeRadius;
		
//		showOnlyNullSpaceWedgeNo = -1;	//  by default, show all null-space wedges
		
		if((vertices != null) && (edges != null))
		{
			// the vertices and edges are given; add the SceneObjects representing the net
			try {
				populateSceneObjectCollection();
			} catch (InconsistencyException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableNetOf4Simplex(EditableNetOf4Simplex original)
	{
		this(
			original.getDescription(),
			original.getVertices(),
			original.getEdges(),
			original.isShowNullSpaceWedges(),
			original.getNullSpaceWedgeLegLengthFactor(),
			original.getNullSpaceWedgeSurfaceTransmissionCoefficient(),
			original.getGluingType(),
			original.getNumberOfNegativeSpaceWedges(),
			original.isShowNetEdges(),
			original.isShowNetFaces(),
			original.isShowNullSpaceWedgeEdges(),
			original.getNetEdgeSurfaceProperty(),
			original.getNetFaceSurfaceProperty(),
			original.getNullSpaceWedgeEdgeSurfaceProperty(),
			original.getEdgeRadius(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableNetOf4Simplex clone()
	{
		return new EditableNetOf4Simplex(this);
	}

	
	//
	// setters and getters
	//
	
	public ArrayList<NamedVector3D> getVertices() {
		return vertices;
	}


	public void setVertices(ArrayList<NamedVector3D> vertices) {
		this.vertices = vertices;
	}


	public ArrayList<NamedEdge> getEdges() {
		return edges;
	}


	public void setEdges(ArrayList<NamedEdge> edges) {
		this.edges = edges;
	}


	public boolean isShowNullSpaceWedges() {
		return showNullSpaceWedges;
	}


	public void setShowNullSpaceWedges(boolean showNullSpaceWedges) {
		this.showNullSpaceWedges = showNullSpaceWedges;
	}


	public double getNullSpaceWedgeLegLengthFactor() {
		return nullSpaceWedgeLegLengthFactor;
	}


	public void setNullSpaceWedgeLegLengthFactor(double nullSpaceWedgeLegLengthFactor) {
		this.nullSpaceWedgeLegLengthFactor = nullSpaceWedgeLegLengthFactor;
	}


	public double getNullSpaceWedgeSurfaceTransmissionCoefficient() {
		return nullSpaceWedgeSurfaceTransmissionCoefficient;
	}


	public void setNullSpaceWedgeSurfaceTransmissionCoefficient(double nullSpaceWedgeSurfaceTransmissionCoefficient) {
		this.nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficient;
	}


	public GluingType getGluingType() {
		return gluingType;
	}


	public void setGluingType(GluingType gluingType) {
		this.gluingType = gluingType;
	}


	public int getNumberOfNegativeSpaceWedges() {
		return numberOfNegativeSpaceWedges;
	}


	public void setNumberOfNegativeSpaceWedges(int numberOfNegativeSpaceWedges) {
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
	}


	public boolean isShowNetEdges() {
		return showNetEdges;
	}


	public void setShowNetEdges(boolean showNetEdges) {
		this.showNetEdges = showNetEdges;
	}


	public boolean isShowNetFaces() {
		return showNetFaces;
	}

	public void setShowNetFaces(boolean showNetFaces) {
		this.showNetFaces = showNetFaces;
	}

	public boolean isShowNullSpaceWedgeEdges() {
		return showNullSpaceWedgeEdges;
	}


	public void setShowNullSpaceWedgeEdges(boolean showNullSpaceWedgeEdges) {
		this.showNullSpaceWedgeEdges = showNullSpaceWedgeEdges;
	}


	public SurfaceProperty getNetEdgeSurfaceProperty() {
		return netEdgeSurfaceProperty;
	}


	public void setNetEdgeSurfaceProperty(SurfaceProperty netEdgeSurfaceProperty) {
		this.netEdgeSurfaceProperty = netEdgeSurfaceProperty;
	}


	public SurfaceProperty getNetFaceSurfaceProperty() {
		return netFaceSurfaceProperty;
	}


	public void setNetFaceSurfaceProperty(SurfaceProperty netFaceSurfaceProperty) {
		this.netFaceSurfaceProperty = netFaceSurfaceProperty;
	}


	public SurfaceProperty getNullSpaceWedgeEdgeSurfaceProperty() {
		return nullSpaceWedgeEdgeSurfaceProperty;
	}


	public void setNullSpaceWedgeEdgeSurfaceProperty(SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty) {
		this.nullSpaceWedgeEdgeSurfaceProperty = nullSpaceWedgeEdgeSurfaceProperty;
	}


	public double getEdgeRadius() {
		return edgeRadius;
	}


	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public int getShowOnlyNullSpaceWedgeNo() {
		return showOnlyNullSpaceWedgeNo;
	}

	public void setShowOnlyNullSpaceWedgeNo(int showOnlyNullSpaceWedgeNo) {
		this.showOnlyNullSpaceWedgeNo = showOnlyNullSpaceWedgeNo;
	}

	


	//
	// useful methods
	//
	
	/**
	 * Dihedral angle (i.e. the angle between faces meeting at an edge) of the outer tetrahedra at the inner edges, i.e. the edges shared with the inner tetrahedron.
	 * This is required to calculate the deficit angle of the null-space wedges.
	 * 
	 * @return	the dihedral angle of the outer tetrahedra; for the moment, this is the dihedral angle of a regular tetrahedron
	 */
	public double calculateDihedralAngleOfOuterTetrahedra()
	{
		return Math.atan(2.*Math.sqrt(2.));
	}
	
	
	//
	//
	//

	//
	// add the scene objects that form this scene object
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this scene object
	 * @throws InconsistencyException 
	 */
	public void populateSceneObjectCollection()
	throws InconsistencyException
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		if((vertices != null) && (edges != null))
		{
			// create a simplicial complex with the vertices and standard edges, and with the faces and simplices inferred
			simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);

			// add an outline of the edges of this simplicial complex to this scene-object collection
			addSceneObject(
					simplicialComplex.getEditableSceneObjectCollection(
							"Structure of net of 4-simplex",	// description
							showNetEdges,	// showVertices only if edges are shown
							netEdgeSurfaceProperty,	// vertexSurfaceProperty
							edgeRadius+MyMath.TINY,	// vertexRadius; add MyMath.TINY so that these cylinders are larger than the edges of the null-space-wedge frames
							showNetEdges,	// showEdges
							netEdgeSurfaceProperty,	// edgeSurfaceProperty
							edgeRadius,	// edgeRadius
							showNetFaces,	// showFaces
							netFaceSurfaceProperty,	// faceSurfaceProperty
							this,	// parent
							getStudio()
							)
					);
			
			// identify the central tetrahedron
			Simplex centralTetrahedron = null;
			for(int s=0; s<simplicialComplex.getSimplices().size(); s++)
			{
				Simplex simplex = simplicialComplex.getSimplex(s);
				// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: number of faces to the outside from simplex #"+s+"="+simplex.inferNoOfFacesToOutside());
				if(simplex.inferNoOfFacesToOutside()==2)
				{
					// the number of faces to the outside from this simplex is 2, so this is the central tetrahedron
					centralTetrahedron = simplex;
				}
			}
			
//			if(centralTetrahedron != null)
//			{
//				System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: number of faces to the outside from simplex #"+s+"="+simplex.inferNoOfFacesToOutside());
//			}

			// centroid of the inner tetrahedron
			Vector3D c = centralTetrahedron.calculateCentroid();

			for(int ei=0; ei<centralTetrahedron.getEdges().size(); ei++)
			{
				if((showOnlyNullSpaceWedgeNo >= 0) && (ei != showOnlyNullSpaceWedgeNo)) continue;	// TODO for debugging
				
				Edge e = centralTetrahedron.getEdges().get(ei);
//			for(Edge e:centralTetrahedron.getEdges())
//			{
				int[] vs = e.getVertexIndices();
				//			// for each edge of the central tetrahedron, add a corresponding EditableNullSpaceWedge
				//			for(int e = 0; e<simplicialComplex.getEdges().size(); e++)
				//			{
				//				// do 4 faces intersect at the current edge?
				//				if(simplicialComplex.getEdge(e).getIntersectingFaceIndices().size() == 4)
				//				{
				//					// yes, so this is one of the edges of the inner tetrahedron
				//					// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: no of faces intersecting at face="+simplicialComplex.getEdge(e).getIntersectingFaceIndices().size());
				//					int[] vs = simplicialComplex.getEdge(e).getVertexIndices();
				// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: vs = ("+vs[0]+", "+vs[1]+")");
				// the two vertices at the ends of the edge
				Vector3D v1 = simplicialComplex.getVertex(vs[0]);
				Vector3D v2 = simplicialComplex.getVertex(vs[1]);
				// the midpoint of the edge
				Vector3D m = Vector3D.sum(v1, v2).getProductWith(0.5);
				
				// System.out.println("EditableNetOf4Simplex::populateSceneObjectCollection: v1="+v1+", v2="+v2+", m="+m+", c="+c);

				// find the third vertex of the outside faces that share the current edge;
				// calculate the distance from the current edge to those vertices (which should be the same);
				// this distance is then the leg length (before multiplication by <i>nullSpaceWedgeLengthFactor</i>)

				Vector3D otherVertex = null;
				// go through all the outside faces, ...
				for(Integer f:simplicialComplex.getOutsideFaceIndices())
				{
					// ... get the list of vertex indices, ...
					int[] faceVertexIndices = simplicialComplex.getFace(f).getVertexIndices();

					// ... check that the vertices of the current edge, <i>vs</i>, is in <i>faceVertexIndices</i>
					// (i.e. that the current edge is one of the edges of the outside face), ...
					if(IndexArray.areInArray(vs, faceVertexIndices))
					{
						// ... and if they are, find the other vertex ...
						otherVertex = simplicialComplex.getVertex(IndexArray.getFirstOtherIndex(vs, faceVertexIndices));

						// ... and leave the for loop
						break;	// note that "break" only breaks loops, not "if"s
					}
				}

				double nullSpaceWedgeLegLength = Geometry.linePointDistance(
						v1,	// pointOnLine
						Vector3D.difference(v2, v1),	// directionOfLine
						otherVertex	// point
						);


				// create the null-space wedge and add it to this scene-object collection
				addSceneObject(
						new EditableMirroredSpaceCancellingWedge(
								"Null-space wedge for edge #"+ei,	// description
								2.*Math.PI - Math.atan(2.*Math.sqrt(2)) - 2*calculateDihedralAngleOfOuterTetrahedra(),	// apexAngle;
								// this can be seen when looking along the edge; arctan(2*sqrt(2)) is the dihedral angle of the (regular) inner tetrahedron
								m,	// apexEdgeCentre,
								Vector3D.difference(v2, v1),	// apexEdgeDirection
								Vector3D.difference(m, c),	// bisectorDirection
								nullSpaceWedgeLegLength * nullSpaceWedgeLegLengthFactor,	// legLength
								Vector3D.difference(v2, v1).getLength(),	// apexEdgeLength
								// SCWedgeLegFaceShape.RECTANGULAR,
								showNullSpaceWedges,	// showSheets
								// MyMath.deg2rad(91),	// containmentMirrorsAngleWithSides
								nullSpaceWedgeSurfaceTransmissionCoefficient,	// sheetTransmissionCoefficient
								showNullSpaceWedgeEdges,	// showEdges
								edgeRadius-MyMath.TINY,	// edgeRadius
								nullSpaceWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty
								gluingType,	// gluingTypoe
								numberOfNegativeSpaceWedges,	// numberOfNegativeSpaceWedges,
								this,	// parent, 
								getStudio()
								)
						);
			}
		}

	}
	
	
	protected NamedEdge createNamedEdge(int vertexIndex0, int vertexIndex1, ArrayList<NamedVector3D> vertices)
	throws InconsistencyException
	{
		return new NamedEdge(vertices.get(vertexIndex0).getName() + " to " + vertices.get(vertexIndex1).getName(), null, vertexIndex0, vertexIndex1);
	}
	
	
	//
	// GUI stuff
	//
	
	// GUI panels
	private LabelledDoublePanel edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<GluingType> gluingTypeComboBox;
	private JCheckBox showNullSpaceWedgesCheckBox, showNetEdgesCheckBox, showNullSpaceWedgesEdgesCheckBox;
	private SurfacePropertyPanel netStructureSurfacePropertyPanel, netFaceSurfacePropertyPanel, nullSpaceWedgeEdgesSurfacePropertyPanel;
	private JButton convertButton;

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane, "wrap");
		
		

		//
		// the main parameters panel
		//
		
		JPanel mainParametersPanel = new JPanel();
		mainParametersPanel.setLayout(new MigLayout("insets 0"));
		
		showNullSpaceWedgesCheckBox = new JCheckBox("Show null-space wedges");
		mainParametersPanel.add(showNullSpaceWedgesCheckBox, "wrap");
		
		nullSpaceWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg length factor");
		mainParametersPanel.add(nullSpaceWedgeLegLengthFactorPanel, "wrap");
		
		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		gluingTypeComboBox.addActionListener(this);
		mainParametersPanel.add(gluingTypeComboBox, "wrap");
		
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of null-space-wedge surfaces");
		mainParametersPanel.add(nullSpaceWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per null-space wedge");
		mainParametersPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		tabbedPane.addTab("Main parameters", mainParametersPanel);

		//
		// the structure-visualisation panel
		//
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show structure (edges and faces) of net of 4-simplex");
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showNullSpaceWedgesEdgesCheckBox = new JCheckBox("Show edges of null-space wedges");
		structureVisualisationPanel.add(showNullSpaceWedgesEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Tube radius");
		structureVisualisationPanel.add(edgeRadiusPanel, "wrap");

		netStructureSurfacePropertyPanel = new SurfacePropertyPanel("Surface of net edges", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(netStructureSurfacePropertyPanel, "wrap");
		netStructureSurfacePropertyPanel.setIPanel(iPanel);

		netFaceSurfacePropertyPanel = new SurfacePropertyPanel("Surface of net faces", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(netFaceSurfacePropertyPanel, "wrap");
		netFaceSurfacePropertyPanel.setIPanel(iPanel);
		
		nullSpaceWedgeEdgesSurfacePropertyPanel = new SurfacePropertyPanel("Surface of null-space-wedge edges", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(nullSpaceWedgeEdgesSurfacePropertyPanel, "wrap");
		nullSpaceWedgeEdgesSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Structure-visualisation details", structureVisualisationPanel);

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
		showNullSpaceWedgesCheckBox.setSelected(showNullSpaceWedges);
		nullSpaceWedgeLegLengthFactorPanel.setNumber(nullSpaceWedgeLegLengthFactor);
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setNumber(nullSpaceWedgeSurfaceTransmissionCoefficient);
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		gluingTypeComboBox.setSelectedItem(gluingType);
		edgeRadiusPanel.setNumber(edgeRadius);
		showNetEdgesCheckBox.setSelected(showNetEdges);
		showNullSpaceWedgesEdgesCheckBox.setSelected(showNullSpaceWedgeEdges);
		netStructureSurfacePropertyPanel.setSurfaceProperty(netEdgeSurfaceProperty);
		netFaceSurfacePropertyPanel.setSurfaceProperty(netFaceSurfaceProperty);
		nullSpaceWedgeEdgesSurfacePropertyPanel.setSurfaceProperty(nullSpaceWedgeEdgeSurfaceProperty);
		enableOrDisableAdditionalWedgeTypeControlPanels();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableNetOf4Simplex acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		showNullSpaceWedges = showNullSpaceWedgesCheckBox.isSelected();
		nullSpaceWedgeLegLengthFactor = nullSpaceWedgeLegLengthFactorPanel.getNumber();
		nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficientPanel.getNumber();
		numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedgesPanel.getNumber();
		gluingType = (GluingType)gluingTypeComboBox.getSelectedItem();
		edgeRadius = edgeRadiusPanel.getNumber();
		showNetEdges = showNetEdgesCheckBox.isSelected();
		showNullSpaceWedgeEdges = showNullSpaceWedgesEdgesCheckBox.isSelected();
		netEdgeSurfaceProperty = netStructureSurfacePropertyPanel.getSurfaceProperty();
		netFaceSurfaceProperty = netFaceSurfacePropertyPanel.getSurfaceProperty();
		nullSpaceWedgeEdgeSurfaceProperty = nullSpaceWedgeEdgesSurfacePropertyPanel.getSurfaceProperty();

		// add the objects
		try {
			populateSceneObjectCollection();
		}
		catch (InconsistencyException e)
		{
			e.printStackTrace();
		}
		
		return this;
	}

	/**
	 * depending on the value of <i>nullSpaceWedgeType</i>, enable or disable the control panels for additional parameters
	 */
	private void enableOrDisableAdditionalWedgeTypeControlPanels()
	{
		// show or hide additional parameters as appropriate
		switch(gluingType)
		{
		case NEGATIVE_SPACE_WEDGES:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case PERFECT:
		default:
			numberOfNegativeSpaceWedgesPanel.setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-net-of-4-simplex");
			container.setValuesInEditPanel();
		}
		else if(e.getSource() == gluingTypeComboBox)
		{
			setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));
			enableOrDisableAdditionalWedgeTypeControlPanels();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited == netStructureSurfacePropertyPanel)
			{
				// net-structure surface property has been edited
				netEdgeSurfaceProperty = (SurfaceProperty)edited;
				netStructureSurfacePropertyPanel.setSurfaceProperty(netEdgeSurfaceProperty);
			}
			else if(edited == netFaceSurfacePropertyPanel)
			{
				// netFaceSurfacePropertyPanel has been edited
				netFaceSurfaceProperty = (SurfaceProperty)edited;
				netFaceSurfacePropertyPanel.setSurfaceProperty(netFaceSurfaceProperty);
			}
			else if(edited == nullSpaceWedgeEdgesSurfacePropertyPanel)
			{
				// null-space-wedges-structure surface property has been edited
				nullSpaceWedgeEdgeSurfaceProperty = (SurfaceProperty)edited;
				nullSpaceWedgeEdgesSurfacePropertyPanel.setSurfaceProperty(nullSpaceWedgeEdgeSurfaceProperty);
			}
	}
}