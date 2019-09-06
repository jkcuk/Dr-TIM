package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.NamedVector3D;
import math.Vector3D;
import math.simplicialComplex.Edge;
import math.simplicialComplex.Face;
import math.simplicialComplex.NamedEdge;
import math.simplicialComplex.SimplicialComplex;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * <p>
 * An editable object representing the 3D net of a symmetric (4D) 4-simplex (also known, confusingly, as 5-cell).
 * This is a simplicial complex in which a simplex is attached to each face of a central simplex.
 * </p>
 * <p>
 * The 4-simplex being "symmetric" means that the net consists of an inner, regular, tetrahedron and identical, not necessarily regular, tetrahedra
 * attached to the faces of the inner tetrahedron.
 * If the height of the outer tetrahedra is half that of the inner tetrahedra, the net can be arranged to fill a cube.
 * </p>
 * @author Johannes, Jakub
 */
public class EditableNetOfSymmetric4Simplex
extends EditableNetOf4Simplex
{
	private static final long serialVersionUID = 7879109210795106392L;

	/**
	 * centroid of the central tetrahedron
	 */
	private Vector3D centroid;
	
	/**
	 * edge length, i.e. the length of the edges of the central tetrahedron in the net
	 */
	private double edgeLength;
	
	/**
	 * the height of the outer tetrahedra is (height of the central tetrahedron)*</i>outerTetrahedronHeightFactor</i>
	 */
	private double outerTetrahedronHeightFactor;
	
	/**
	 * unit vector pointing in the direction of the first normal of the enclosing cube
	 */
	private Vector3D normal2enclosingCube1;

	/**
	 * unit vector pointing in the direction of the second normal of the enclosing cube
	 */
	private Vector3D normal2enclosingCube2;

	/**
	 * unit vector pointing in the direction of the third normal of the enclosing cube
	 */
	private Vector3D normal2enclosingCube3;

	
	
	//
	// constructors
	//
	
	public EditableNetOfSymmetric4Simplex(
			String description,
			Vector3D centroid,
			double edgeLength,
			double outerTetrahedronHeightFactor,
			Vector3D normal2enclosingCube1,
			Vector3D normal2enclosingCube2,
			Vector3D normal2enclosingCube3,
			boolean showNullSpaceWedges,
			double nullSpaceWedgeLegLengthFactor,
			double nullSpaceWedgeSurfaceTransmissionCoefficient,
			GluingType gluingType,
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
		super(
				description,
				null,	// vertices; set later
				null,	// edges; set later
				showNullSpaceWedges,
				nullSpaceWedgeLegLengthFactor,
				nullSpaceWedgeSurfaceTransmissionCoefficient,
				gluingType,
				numberOfNegativeSpaceWedges,
				showNetEdges,
				showNullSpaceWedgeEdges,
				netEdgeSurfaceProperty,
				netFaceSurfaceProperty,
				nullSpaceWedgeEdgeSurfaceProperty,
				structureTubeRadius,
				parent,
				studio
			);

		setCentroid(centroid);
		setEdgeLength(edgeLength);
		setOuterTetrahedronHeightFactor(outerTetrahedronHeightFactor);
		setDirections(normal2enclosingCube1, normal2enclosingCube2, normal2enclosingCube3);
		
		try {
			populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
	}


	
	
	public EditableNetOfSymmetric4Simplex(SceneObject parent, Studio studio)
	{
		this(
				"Net of symmetric 4-simplex",
				new Vector3D(0, 0, 10),	// centroid
				1,	// edgeLength,
				1,	// outerTetrahedronHeightFactor
				new Vector3D(1, 0, 0),	// normal2enclosingCube1
				new Vector3D(0, 1, 0),	// normal2enclosingCube2
				new Vector3D(0, 0, 1),	// normal2enclosingCube3
				true,	// showNullSpaceWedges
				1,	// nullSpaceWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				GluingType.PERFECT,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				false,	// showNetStructure
				false,	// showNullSpaceWedgesStructure
				SurfaceColour.BLUE_SHINY,	// netStructureSurfaceProperty
				ColourFilter.CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// nullSpaceWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				parent,
				studio
			);
	}

	public EditableNetOfSymmetric4Simplex(EditableNetOfSymmetric4Simplex original)
	{
		this(
				original.getDescription(),
				original.getCentroid(),
				original.getEdgeLength(),
				original.getOuterTetrahedronHeightFactor(),
				original.getNormal2enclosingCube1(),
				original.getNormal2enclosingCube2(),
				original.getNormal2enclosingCube3(),
				original.isShowNullSpaceWedges(),
				original.getNullSpaceWedgeLegLengthFactor(),
				original.getNullSpaceWedgeSurfaceTransmissionCoefficient(),
				original.getGluingType(),
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
	
	public EditableNetOfSymmetric4Simplex clone()
	{
		return new EditableNetOfSymmetric4Simplex(this);
	}



	
	//
	// setters & getters
	//
	
	public Vector3D getCentroid() {
		return centroid;
	}

	public void setCentroid(Vector3D centroid) {
		this.centroid = centroid;
	}

	public double getEdgeLength() {
		return edgeLength;
	}

	public void setEdgeLength(double centralTetrahedronSideLength) {
		this.edgeLength = centralTetrahedronSideLength;
	}

	public double getOuterTetrahedronHeightFactor() {
		return outerTetrahedronHeightFactor;
	}

	public void setOuterTetrahedronHeightFactor(double outerTetrahedronHeightFactor) {
		this.outerTetrahedronHeightFactor = outerTetrahedronHeightFactor;
	}

	public Vector3D getNormal2enclosingCube1() {
		return normal2enclosingCube1;
	}

	public Vector3D getNormal2enclosingCube2() {
		return normal2enclosingCube2;
	}

	public Vector3D getNormal2enclosingCube3() {
		return normal2enclosingCube3;
	}

	public void setDirections(Vector3D normal2enclosingCube1, Vector3D normal2enclosingCube2, Vector3D normal2enclosingCube3)
	{
		this.normal2enclosingCube1 = normal2enclosingCube1;
		this.normal2enclosingCube2 = normal2enclosingCube2;
		this.normal2enclosingCube3 = normal2enclosingCube3;
	}

	
	
	//
	//
	//
	
	@Override
	public double calculateDihedralAngleOfOuterTetrahedra()
	{
		// The height of the outer tetrahedra is
		//	(height of outer tetrahedra) = outerTetrahedronHeightFactor * (height of inner tetrahedron).
		// The inner tetrahedron is a regular tetrahedron, and its height is (see https://en.wikipedia.org/wiki/Tetrahedron)
		//	(height of inner tetrahedron) = sqrt(2/3)*(edge length of inner tetrahedron),
		// and so
		// 	(height of outer tetrahedra) = outerTetrahedronHeightFactor * sqrt(2/3)*(edge length of inner tetrahedron).
		// The dihedral angle (at the inner edges) can then be calculated from the right-angled triangle formed by the (outer) tip of the tetrahedron, T,
		// the centroid C of its base (i.e. the face it shares with the inner tetrahedron), and the midpoint M of one of the edges of the base.
		// The relevant dihedral angle, alpha, is then the angle BMT, the angle at B is a right angle.
		// Then
		//	tan alpha = BT / MB
		// (where BT is the length from B to T and MB is the length from M to B).
		// BT is simply the height of the outer tetrahedra, which is given above.
		// MB can be calculated from the fact that the base of the tetrahedron is an equilateral triangle, so the angles at the vertices are all 60 degrees,
		// and the fact that B lies in the middle of this equilateral triangle, and so the line from a vertex to B is at an angle 30 degrees to the edges.
		// If we now pick one of the vertices of the edge with midpoint M and call it V, then the right-angled triangle VMB has a right angle at M and
		// a 30 degree angle at V, and the distance VM is half the edge length of the inner tetrahedron.  Therefore
		//  tan 30 degrees = MB / VM = MB / ((edge length of inner tetrahedron)/2).
		// Solving for MB gives
		//	MB = (edge length of inner tetrahedron)/2 * tan 30 degrees = (edge length of inner tetrahedron)/(2 sqrt(3)).
		// Substituting into the equation for tan alpha gives
		//	tan alpha = BT / MB = (height of outer tetrahedra) / MB
		//		= outerTetrahedronHeightFactor * sqrt(2/3)*(edge length of inner tetrahedron) / ((edge length of inner tetrahedron)/(2 sqrt(3)))
		//		= outerTetrahedronHeightFactor * 2*sqrt(2).
		return Math.atan(outerTetrahedronHeightFactor*2.*Math.sqrt(2.));
	}
	
	public void populateSceneObjectCollection()
	throws InconsistencyException
	{
		// create a physical-space simplicial complex
		
		// create a new array of vertices
		ArrayList<NamedVector3D> vertices = new ArrayList<NamedVector3D>();
		
		// we imagine the inner tetrahedron as being embedded in a cube;
		// each edge of the inner tetrahedron is a diagonal of one of the cube's faces, and therefore (by Pythagoras)
		// 	(tetrahedron edge length)^2 = 2*(cube side length)^2,
		// or
		// 	(cube side length) = (tetrahedron edge length) / sqrt(2)
		double cubeHalfSideLength = 0.5 * edgeLength / Math.sqrt(2.);
		
		// add the vertices of the inner tetrahedron
		vertices.add(new NamedVector3D("vertex 0",
				Vector3D.sum(
						centroid,
						normal2enclosingCube1.getWithLength(cubeHalfSideLength),
						normal2enclosingCube2.getWithLength(cubeHalfSideLength),
						normal2enclosingCube3.getWithLength(cubeHalfSideLength)
					)));
		vertices.add(new NamedVector3D("vertex 1",
				Vector3D.sum(
						centroid,
						normal2enclosingCube1.getWithLength(cubeHalfSideLength),
						normal2enclosingCube2.getWithLength(-cubeHalfSideLength),
						normal2enclosingCube3.getWithLength(-cubeHalfSideLength)
					)));
		vertices.add(new NamedVector3D("vertex 2",
				Vector3D.sum(
						centroid,
						normal2enclosingCube1.getWithLength(-cubeHalfSideLength),
						normal2enclosingCube2.getWithLength(cubeHalfSideLength),
						normal2enclosingCube3.getWithLength(-cubeHalfSideLength)
					)));
		vertices.add(new NamedVector3D("vertex 3",
				Vector3D.sum(
						centroid,
						normal2enclosingCube1.getWithLength(-cubeHalfSideLength),
						normal2enclosingCube2.getWithLength(-cubeHalfSideLength),
						normal2enclosingCube3.getWithLength(cubeHalfSideLength)
					)));
		
		// create a new array of edges
		ArrayList<NamedEdge> edges = new ArrayList<NamedEdge>();

		// add the edges of the inner tetrahedron
		edges.add(createNamedEdge(0, 1, vertices));	// 0
		edges.add(createNamedEdge(0, 2, vertices));	// 1
		edges.add(createNamedEdge(0, 3, vertices));	// 2
		edges.add(createNamedEdge(1, 2, vertices));	// 3
		edges.add(createNamedEdge(2, 3, vertices));	// 4
		edges.add(createNamedEdge(3, 1, vertices));	// 5

		// create a simplicial complex that corresponds to the inner tetrahedron
		SimplicialComplex innerTetrahedron = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges((ArrayList<Vector3D>)(vertices.clone()), (ArrayList<Edge>)(edges.clone()));
		
		// in the net of the 4-simplex, there is an additional vertex for each of the faces of the inner tetrahedron
		for(int f=0; f<4; f++)
		{
			Face face = innerTetrahedron.getFace(f);
			// System.out.println("EditableNetOf4Simplex:initialiseToNetOfRegular4Simplex: vertex indices of face #"+f);
			// for(int j=0; j<face.getVertexIndices().length; j++) System.out.println("  "+face.getVertexIndices()[j]);

			// find the vertex of the inner tetrahedron that is not part of the current face, ...
			int indexOfOtherVertex = innerTetrahedron.getSimplex(0).getFirstVertexIndexNotIn(face.getVertexIndices());
			// System.out.println("EditableNetOf4Simplex:initialiseToNetOfRegular4Simplex: index of other vertex ="+indexOfOtherVertex);
			Vector3D otherVertex = innerTetrahedron.getVertex(indexOfOtherVertex);
			
			Vector3D faceCentroid = face.calculateCentroid();

			// The distance of the faces of the inner tetrahedron from the centroid can be calculated as the modulus of the scalar product between nHat, 
			// a normalised normal to one face, and the vector from the centroid to a point P on the face, in the simplest case one of the vertices.
			// For the moment, set the side length of the cube to 1 (i.e. do the calculation in units of the cube side length), and place the centroid at the origin, O.
			// Pick the face of the inner tetrahedron opposite the corner of the cube with coordinates (1, 1, 1)/2.
			// The vector (1, 1, 1)/2 is then a normal to the face.  Normalised, it is nHat = (1, 1, 1)/sqrt(3).
			// As the point on the face, we pick one of the vertices: P=(1, -1, -1)/2.
			// Then
			//	d = |(P-O).nHat| = |(1/2)*(1, -1, -1).(1, 1, 1)/sqrt(3)| = 1/(2 sqrt(3)) |1-1-1| = 1/(2 sqrt(3)).
			// Adding the units, we get
			//	d = (cube side length)/(2 sqrt(3)).
			//
			// From above, we also have
			//	(cube side length) = (tetrahedron edge length) / sqrt(2),
			// and so
			//	d = (tetrahedron edge length)/(2 sqrt(6)).
			
			// reflect the position of the centroid at the plane of the current face, ...
			NamedVector3D newVertex = new NamedVector3D(
					"net vertex above face #"+f,
					Vector3D.sum(
							faceCentroid,
							Vector3D.difference(faceCentroid, otherVertex).getProductWith(outerTetrahedronHeightFactor)
						)
				);

			// ... add that new position as a new vertex to the net, ...
			vertices.add(newVertex);
			// System.out.println("outer vertex for face #"+f+" = "+newVertex);

			// ... and create edges from the vertices of <i>face</i> to the new vertex
			for(int faceVertexIndex : face.getVertexIndices())
			{
				edges.add(createNamedEdge(faceVertexIndex, vertices.size()-1, vertices));
			}
		}
		
//		// debugging
//		for(int i=0; i<vertices.size(); i++)
//		{
//			System.out.println("vertex #"+i+" = "+vertices.get(i));
//		}

		setVertices(vertices);
		setEdges(edges);
		super.populateSceneObjectCollection();
	}


	
	//
	// GUI stuff
	//

	// GUI panels
	private LabelledVector3DPanel centroidPanel, normal2enclosingCube1Panel, normal2enclosingCube2Panel, normal2enclosingCube3Panel;
	private LabelledDoublePanel edgeLengthPanel, outerTetrahedronHeightFactorPanel, edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Net of symmetric 4-simplex"));
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
		
		centroidPanel = new LabelledVector3DPanel("Centroid");
		mainParametersPanel.add(centroidPanel, "wrap");
		
		edgeLengthPanel = new LabelledDoublePanel("Edge length of central tetrahedron");
		mainParametersPanel.add(edgeLengthPanel, "wrap");
		
		outerTetrahedronHeightFactorPanel = new LabelledDoublePanel("(Length of outer edges of outer tetrahedra)/(edge length of central tetrahedron)");
		mainParametersPanel.add(outerTetrahedronHeightFactorPanel, "wrap");
		
		normal2enclosingCube1Panel = new LabelledVector3DPanel("Normal to face 1 of enclosing cube");
		mainParametersPanel.add(normal2enclosingCube1Panel, "wrap");

		normal2enclosingCube2Panel = new LabelledVector3DPanel("Normal to face 2 of enclosing cube");
		mainParametersPanel.add(normal2enclosingCube2Panel, "wrap");

		normal2enclosingCube3Panel = new LabelledVector3DPanel("Normal to face 3 of enclosing cube");
		mainParametersPanel.add(normal2enclosingCube3Panel, "wrap");

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
		centroidPanel.setVector3D(centroid);
		edgeLengthPanel.setNumber(edgeLength);
		outerTetrahedronHeightFactorPanel.setNumber(outerTetrahedronHeightFactor);
		normal2enclosingCube1Panel.setVector3D(normal2enclosingCube1);
		normal2enclosingCube2Panel.setVector3D(normal2enclosingCube2);
		normal2enclosingCube3Panel.setVector3D(normal2enclosingCube3);
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
		centroid = centroidPanel.getVector3D();
		edgeLength = edgeLengthPanel.getNumber();
		outerTetrahedronHeightFactor = outerTetrahedronHeightFactorPanel.getNumber();
		normal2enclosingCube1 = normal2enclosingCube1Panel.getVector3D();
		normal2enclosingCube2 = normal2enclosingCube2Panel.getVector3D();
		normal2enclosingCube3 = normal2enclosingCube3Panel.getVector3D();
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
	 * depending on the value of <i>gluingType</i>, enable or disable the control panels for additional parameters
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
