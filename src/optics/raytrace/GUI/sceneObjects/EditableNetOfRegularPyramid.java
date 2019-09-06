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
import math.MyMath;
import math.NamedVector3D;
import math.Vector3D;
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

/**
 * <p>
 * An editable object representing the 2D net of a (3D) regular pyramid.
 * A regular pyramid has a base that is a regular polygon.
 * </p>
 * @author Johannes
 */
public class EditableNetOfRegularPyramid extends EditableSceneObjectCollection implements ActionListener
{
	/**
	 * position of the apex
	 */
	private Vector3D apex;
	
	/**
	 * centre of the base
	 */
	private Vector3D baseCentre;
	
	/**
	 * radius of the base, i.e. the distance from the centre to the vertices
	 */
	private double baseRadius;
	
	/**
	 * number of vertices of the base
	 */
	private int numberOfBaseVertices;
	
	/**
	 * unit vector pointing in the direction of one of the vertices of the base;
	 */
	private Vector3D directionToBaseVertex0;

	/**
	 * show the null-space wedges
	 */
	protected boolean showNullSpaceWedges;

	/**
	 * Leg length of each null-space wedge.
	 * Each of the wedges is an extruded isoceles triangle of leg length <i>legLength</i>.
	 */
	protected double nullSpaceWedgeLegLength;
	
	/**
	 * Transmission coefficient of each null-space-wedge surface
	 */
	protected double nullSpaceWedgeSurfaceTransmissionCoefficient;
		
	/**
	 * Null-space-wedge type, which describes the way this null-space wedge is realised
	 */
	protected NullSpaceWedgeType nullSpaceWedgeType;
	
	/**
	 * Number of negative-space wedges, <i>N</i>, in the null-space wedge
	 */
	protected int numberOfNegativeSpaceWedges;

	/**
	 * show edges of the null-space wedges, i.e. the vertices and edges of the sheets forming the null-space wedges
	 */
	protected boolean showNullSpaceWedgeEdges;

	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty;

	/**
	 * radius of the spheres and cylinders representing the vertices and edges
	 */
	protected double edgeRadius;
	
	
		
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
	 * @param nullSpaceWedgeLegLength
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
	public EditableNetOfRegularPyramid(
			String description,
			ArrayList<NamedVector3D> vertices,
			ArrayList<NamedEdge> edges,
			boolean showNullSpaceWedges,
			double nullSpaceWedgeLegLength,
			double nullSpaceWedgeSurfaceTransmissionCoefficient,
			NullSpaceWedgeType nullSpaceWedgeType,
			int numberOfNegativeSpaceWedges,
			boolean showNetEdges,
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
		this.nullSpaceWedgeLegLength = nullSpaceWedgeLegLength;
		this.nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficient;
		this.nullSpaceWedgeType = nullSpaceWedgeType;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		this.showNetEdges = showNetEdges;
		this.showNullSpaceWedgeEdges = showNullSpaceWedgeEdges;
		this.netEdgeSurfaceProperty = netEdgeSurfaceProperty;
		this.netFaceSurfaceProperty = netFaceSurfaceProperty;
		this.nullSpaceWedgeEdgeSurfaceProperty = nullSpaceWedgeEdgeSurfaceProperty;
		this.edgeRadius = structureTubeRadius;
		
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

	public EditableNetOfRegularPyramid(String description, CombinationMode combinationMode,
			boolean combinationModeEditable, SceneObject parent, Studio studio, Vector3D apex, Vector3D baseCentre,
			double baseRadius, int numberOfBaseVertices, Vector3D directionToBaseVertex0, boolean showNullSpaceWedges,
			double nullSpaceWedgeLegLength, double nullSpaceWedgeSurfaceTransmissionCoefficient,
			NullSpaceWedgeType nullSpaceWedgeType, int numberOfNegativeSpaceWedges, boolean showNullSpaceWedgeEdges,
			SurfaceProperty nullSpaceWedgeEdgeSurfaceProperty, double edgeRadius) {
		super(description, combinationMode, combinationModeEditable, parent, studio);
		this.apex = apex;
		this.baseCentre = baseCentre;
		this.baseRadius = baseRadius;
		this.numberOfBaseVertices = numberOfBaseVertices;
		this.directionToBaseVertex0 = directionToBaseVertex0;
		this.showNullSpaceWedges = showNullSpaceWedges;
		this.nullSpaceWedgeLegLength = nullSpaceWedgeLegLength;
		this.nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficient;
		this.nullSpaceWedgeType = nullSpaceWedgeType;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		this.showNullSpaceWedgeEdges = showNullSpaceWedgeEdges;
		this.nullSpaceWedgeEdgeSurfaceProperty = nullSpaceWedgeEdgeSurfaceProperty;
		this.edgeRadius = edgeRadius;
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableNetOfRegularPyramid(EditableNetOfRegularPyramid original)
	{
		this(
			original.getDescription(),
			original.getVertices(),
			original.getEdges(),
			original.isShowNullSpaceWedges(),
			original.getNullSpaceWedgeLegLength(),
			original.getNullSpaceWedgeSurfaceTransmissionCoefficient(),
			original.getNullSpaceWedgeType(),
			original.getNumberOfNegativeSpaceWedges(),
			original.isShowNetEdges(),
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
	public EditableNetOfRegularPyramid clone()
	{
		return new EditableNetOfRegularPyramid(this);
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


	public double getNullSpaceWedgeLegLength() {
		return nullSpaceWedgeLegLength;
	}


	public void setNullSpaceWedgeLegLength(double nullSpaceWedgeLegLength) {
		this.nullSpaceWedgeLegLength = nullSpaceWedgeLegLength;
	}


	public double getNullSpaceWedgeSurfaceTransmissionCoefficient() {
		return nullSpaceWedgeSurfaceTransmissionCoefficient;
	}


	public void setNullSpaceWedgeSurfaceTransmissionCoefficient(double nullSpaceWedgeSurfaceTransmissionCoefficient) {
		this.nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficient;
	}


	public NullSpaceWedgeType getNullSpaceWedgeType() {
		return nullSpaceWedgeType;
	}


	public void setNullSpaceWedgeType(NullSpaceWedgeType nullSpaceWedgeType) {
		this.nullSpaceWedgeType = nullSpaceWedgeType;
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
							true,	// showVertices
							netEdgeSurfaceProperty,	// vertexSurfaceProperty
							edgeRadius+MyMath.TINY,	// vertexRadius; add MyMath.TINY so that these cylinders are larger than the edges of the null-space-wedge frames
							true,	// showEdges
							netEdgeSurfaceProperty,	// edgeSurfaceProperty
							edgeRadius,	// edgeRadius
							true,	// showFaces
							netFaceSurfaceProperty,	// faceSurfaceProperty
							this,	// parent
							getStudio()
							),
					showNetEdges
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
			
			if(centralTetrahedron != null)
			{
				// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: number of faces to the outside from simplex #"+s+"="+simplex.inferNoOfFacesToOutside());
			}

			// centroid of the inner tetrahedron
			Vector3D c = centralTetrahedron.calculateCentroid();

			// for each edge of the central tetrahedron, add a corresponding EditableNullSpaceWedge
			for(int e = 0; e<simplicialComplex.getEdges().size(); e++)
			{
				// do 4 faces intersect at the current edge?
				if(simplicialComplex.getEdge(e).getIntersectingFaceIndices().size() == 4)
				{
					// yes, so this is one of the edges of the inner tetrahedron
					// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: no of faces intersecting at face="+simplicialComplex.getEdge(e).getIntersectingFaceIndices().size());
					int[] vs = simplicialComplex.getEdge(e).getVertexIndices();
					// System.out.println("EditableNetOf4Simplex:populateSceneObjectCollection: vs = ("+vs[0]+", "+vs[1]+")");
					// the two vertices at the ends of the edge
					Vector3D v1 = simplicialComplex.getVertex(vs[0]);
					Vector3D v2 = simplicialComplex.getVertex(vs[1]);
					// the midpoint of the edge
					Vector3D m = Vector3D.sum(v1, v2).getProductWith(0.5);

					// create the null-space wedge and add it to this scene-object collection
					addSceneObject(
							new EditableNullSpaceWedge(
									"Null-space wedge for edge #"+e,	// description
									2.*Math.PI - 3.*Math.atan(2.*Math.sqrt(2)),	// wedgeAngle
									m,	// centre,
									Vector3D.difference(v2, v1),	// commonEdgeDirection
									Vector3D.difference(m, c),	// bisectorDirection
									nullSpaceWedgeLegLength,	// legLength
									Vector3D.difference(v2, v1).getLength(),	// height
									showNullSpaceWedges,	// showSheets
									nullSpaceWedgeSurfaceTransmissionCoefficient,	// sheetTransmissionCoefficient
									showNullSpaceWedgeEdges,	// showEdges
									edgeRadius-MyMath.TINY,	// edgeRadius
									nullSpaceWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty
									nullSpaceWedgeType,	// nullSpaceWedgeType
									numberOfNegativeSpaceWedges,	// numberOfNegativeSpaceWedges,
									this,	// parent, 
									getStudio()
									)
							);
				}
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
	private LabelledDoublePanel edgeRadiusPanel, nullSpaceWedgeLegLengthPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<NullSpaceWedgeType> nullSpaceWedgeTypeComboBox;
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
		
		nullSpaceWedgeLegLengthPanel = new LabelledDoublePanel("Leg length");
		mainParametersPanel.add(nullSpaceWedgeLegLengthPanel, "wrap");
		
		nullSpaceWedgeTypeComboBox = new JComboBox<NullSpaceWedgeType>(NullSpaceWedgeType.values());
		nullSpaceWedgeTypeComboBox.addActionListener(this);
		mainParametersPanel.add(nullSpaceWedgeTypeComboBox, "wrap");
		
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
		nullSpaceWedgeLegLengthPanel.setNumber(nullSpaceWedgeLegLength);
		nullSpaceWedgeSurfaceTransmissionCoefficientPanel.setNumber(nullSpaceWedgeSurfaceTransmissionCoefficient);
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		nullSpaceWedgeTypeComboBox.setSelectedItem(nullSpaceWedgeType);
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
	public EditableNetOfRegularPyramid acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		showNullSpaceWedges = showNullSpaceWedgesCheckBox.isSelected();
		nullSpaceWedgeLegLength = nullSpaceWedgeLegLengthPanel.getNumber();
		nullSpaceWedgeSurfaceTransmissionCoefficient = nullSpaceWedgeSurfaceTransmissionCoefficientPanel.getNumber();
		numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedgesPanel.getNumber();
		nullSpaceWedgeType = (NullSpaceWedgeType)nullSpaceWedgeTypeComboBox.getSelectedItem();
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
		switch(nullSpaceWedgeType)
		{
		case NEGATIVE_SPACE_WEDGES:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case TELEPORTING_SIDES:
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
		else if(e.getSource() == nullSpaceWedgeTypeComboBox)
		{
			setNullSpaceWedgeType((NullSpaceWedgeType)(nullSpaceWedgeTypeComboBox.getSelectedItem()));
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