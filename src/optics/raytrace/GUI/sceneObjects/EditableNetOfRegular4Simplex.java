package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.Geometry;
import math.NamedVector3D;
import math.Vector3D;
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
 * An editable object representing the 3D net of a regular (4D) 4-simplex (also known, confusingly, as 5-cell).
 * This is a simplicial complex in which a simplex is attached to each face of a central simplex.
 * </p>
 * <p>
 * The 4-simplex is regular, i.e. all 5 simplices are regular tetrahedra, all of the same size (but with different orientations).
 * </p>
 * 
 * Note that a regular 4-simplex is a special case of a symmetric 4-simplex, and so this class is now deprecated.
 * @author Johannes, Dimitris
 * @deprecated	Use the {@link optics.raytrace.GUI.sceneObjects.EditableNetOfSymmetric4Simplex} class instead
 */
@Deprecated
public class EditableNetOfRegular4Simplex
extends EditableNetOf4Simplex
{
	private static final long serialVersionUID = 6828263561917097559L;

	/**
	 * centroid of the central tetrahedron
	 */
	private Vector3D centroid;
	
	/**
	 * edge length, i.e. the length of all edges in the net
	 */
	private double edgeLength;
	
	/**
	 * unit vector pointing in the direction of one of the vertices of the central tetrahedron
	 */
	private Vector3D directionToVertex0;
	
	/**
	 * unit vector pointing in the direction from vertex 1 to vertex 2
	 */
	private Vector3D directionVertex1ToVertex2;
	
	
	//
	// constructors
	//
	
	public EditableNetOfRegular4Simplex(
			String description,
			Vector3D centroid,
			double edgeLength,
			Vector3D directionToVertex0,
			Vector3D directionVertex1ToVertex2,
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
				false,	//  showNetFaces
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
		setDirections(directionToVertex0, directionVertex1ToVertex2);
		
		try {
			populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
	}


	
	
	public EditableNetOfRegular4Simplex(SceneObject parent, Studio studio)
	{
		this(
				"Net of regular 4-simplex",
				new Vector3D(0, 0, 10),	// centroid
				1,	// edgeLength,
				new Vector3D(0, 0, 1),	// directionToVertex0,
				new Vector3D(1, 0, 0),	// directionVertex1ToVertex2
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

	public EditableNetOfRegular4Simplex(EditableNetOfRegular4Simplex original)
	{
		this(
				original.getDescription(),
				original.getCentroid(),
				original.getEdgeLength(),
				original.getDirectionToVertex0(),
				original.getDirectionVertex1ToVertex2(),
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
	
	public EditableNetOfRegular4Simplex clone()
	{
		return new EditableNetOfRegular4Simplex(this);
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

	public Vector3D getDirectionToVertex0() {
		return directionToVertex0;
	}

	public void setDirections(Vector3D directionToVertex0, Vector3D directionVertex1ToVertex2)
	{
		this.directionToVertex0 = directionToVertex0.getNormalised();
		this.directionVertex1ToVertex2 = directionVertex1ToVertex2.getPartPerpendicularTo(directionToVertex0).getNormalised();
	}

	public Vector3D getDirectionVertex1ToVertex2() {
		return directionVertex1ToVertex2;
	}

	public void setDirectionVertex1ToVertex2(Vector3D directionVertex1ToVertex2) {
		this.directionVertex1ToVertex2 = directionVertex1ToVertex2;
	}

	
	//
	//
	//
	
	@Override
	public double calculateDihedralAngleOfOuterTetrahedra()
	{
		return Math.atan(2.*Math.sqrt(2.));
	}

	
	public void populateSceneObjectCollection()
	throws InconsistencyException
	{
		// create a physical-space simplicial complex
		
		// create a unit vector perpendicular to the other two directions
		Vector3D direction3 = Vector3D.crossProduct(directionToVertex0, directionVertex1ToVertex2);
		
		// create a new array of vertices
		ArrayList<NamedVector3D> vertices = new ArrayList<NamedVector3D>();
		
		// add the vertices of the inner tetrahedron
		vertices.add(new NamedVector3D("back vertex",
				Vector3D.sum(
						centroid,
						directionToVertex0.getProductWith(edgeLength*Math.sqrt(6.)/4.)
					)));	// 0; v1 in Dimitris's document
		vertices.add(new NamedVector3D("front bottom left vertex",
				Vector3D.sum(
						centroid,
						direction3.getProductWith(-edgeLength/(2.*Math.sqrt(3.))),
						directionVertex1ToVertex2.getProductWith(-edgeLength/2.),
						directionToVertex0.getProductWith(-edgeLength/(2.*Math.sqrt(6.)))
					)));	// 1; v2 in Dimitris's document
		vertices.add(new NamedVector3D("front top left vertex",
				Vector3D.sum(
						centroid,
						direction3.getProductWith(-edgeLength/(2.*Math.sqrt(3.))),
						directionVertex1ToVertex2.getProductWith(edgeLength/2.),
						directionToVertex0.getProductWith(-edgeLength/(2.*Math.sqrt(6.)))
				)));	// 2; v3 in Dimitris's document
		vertices.add(new NamedVector3D("front right vertex",
				Vector3D.sum(
						centroid,
						direction3.getProductWith(edgeLength/Math.sqrt(3.)),
						directionToVertex0.getProductWith(-edgeLength/(2.*Math.sqrt(6.)))
					)));	// 3; v4 in Dimitris's document
		
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
		SimplicialComplex innerTetrahedron = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);

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

			// ... reflect the position of this vertex at the plane of the current face, ...
			NamedVector3D newVertex = new NamedVector3D(
					"net vertex above face #"+f,
					Geometry.reflectPointOnPlane(
							otherVertex,	// point
							face.getVertex(0),	// pointOnPlane
							face.getNormal()	// normalToPlane
							)
					);

			// ... add that new position as a new vertex to the net, ...
			vertices.add(newVertex);

			// ... and create edges from the vertices of <i>face</i> to the new vertex
			for(int faceVertexIndex : face.getVertexIndices())
			{
				edges.add(createNamedEdge(faceVertexIndex, vertices.size()-1, vertices));
			}
		}

		setVertices(vertices);
		setEdges(edges);
		super.populateSceneObjectCollection();
	}


	
	//
	// GUI stuff
	//

	// GUI panels
	private LabelledVector3DPanel centroidPanel, directionToVertex0Panel, directionVertex1ToVertex2Panel;
	private LabelledDoublePanel edgeLengthPanel, edgeRadiusPanel, nullSpaceWedgeLegLengthFactorPanel, nullSpaceWedgeSurfaceTransmissionCoefficientPanel;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Net of regular 4-simplex"));
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
		
		edgeLengthPanel = new LabelledDoublePanel("Edge length");
		mainParametersPanel.add(edgeLengthPanel, "wrap");
		

		directionToVertex0Panel = new LabelledVector3DPanel("Direction to vertex #0");
		mainParametersPanel.add(directionToVertex0Panel, "wrap");

		directionVertex1ToVertex2Panel = new LabelledVector3DPanel("Direction vertex #1 to vertex #2");
		mainParametersPanel.add(directionVertex1ToVertex2Panel, "wrap");
		
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
		directionToVertex0Panel.setVector3D(directionToVertex0);
		directionVertex1ToVertex2Panel.setVector3D(directionVertex1ToVertex2);
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
		directionToVertex0 = directionToVertex0Panel.getVector3D();
		directionVertex1ToVertex2 = directionVertex1ToVertex2Panel.getVector3D();
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
