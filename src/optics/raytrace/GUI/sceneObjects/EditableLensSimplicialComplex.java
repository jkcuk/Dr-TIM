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
import math.NamedVector3D;
import math.Vector3D;
import math.simplicialComplex.NamedEdge;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.LoopImagingTheorem;
import optics.raytrace.simplicialComplex.IdealThinLensSimplicialComplex;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * <p>
 * An editable object representing a lens structure describe by a simplicial complex (in physical space) and
 * a the positions of the vertices in virtual space.
 * </p>
 * <p>
 * The vertices in virtual space cannot be placed arbitrarily, as lenses (specifically ideal thin lenses) cannot perform the most general imaging.
 * (Glenses [1] --- generalised lenses --- can, but lenses cannot.)
 * </p>
 * <p>
 * [1] G. J. Chaplain, G. Macauley, J. Belin, T. Tyc, E. N. Cowie, and J. Courtial, <i>Ray optics of generalized lenses</i>, J. Opt. Soc. Am. A <b>33</b>, 962-969 (2016)
 * </p>
 * @author Johannes
 */
public class EditableLensSimplicialComplex extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 1095044088580155630L;

	/**
	 * The list of real-space vertex positions.
	 * Note that these are all of type NamedVector3D, which means they can come with little descriptions.
	 */
	ArrayList<NamedVector3D> vertices;

	/**
	 * The list of virtual-space positions that correspond to the (physical-space) vertex positions.
	 * Note that these are all of type NamedVector3D, which means they can come with little descriptions.
	 */
	ArrayList<NamedVector3D> verticesV;
	
	/**
	 * The list of edges, which is shared between the real- and virtual-space simplicial complexes.
	 * All edges are named, to make editing easier.
	 */
	ArrayList<NamedEdge> edges;

	/**
	 * Allows the type of SceneObject that represents the faces for raytracing purposes to be selected
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)
	 */
	private LensType lensTypeRepresentingFace;
	
	/**
	 * Position in outside space from which this simplicial complex looks best.
	 * If lensTypeRepresentingFace = IDEAL_THIN_LENS, the simplicial complex should work perfectly from <i>any</i> outside-space viewing position.
	 * If lensTypeRepresentingFace = POINT_2_POINT_IMAGING_HOLOGRAM, this is not the case, but from the
	 * optimumOutsideSpaceViewingPosition the simplicial complex should look the same as if lensTypeRepresentingFace = IDEAL_THIN_LENS.
	 */
	private Vector3D optimumOutsideSpaceViewingPosition;
	
	/**
	 * show structure of physical space
	 */
	private boolean showStructureP;
	
	/**
	 * show structure of virtual space
	 */
	private boolean showStructureV;
	
	/**
	 * surface property of the spheres and cylinders representing the vertices and edges in physical space
	 */
	private SurfaceProperty surfacePropertyP;
	
	/**
	 * surface property of the spheres and cylinders representing the vertices and edges in virtual space
	 */
	private SurfaceProperty surfacePropertyV;
	
	/**
	 * radius of the spheres and cylinders representing the vertices and edges in physical space
	 */
	private double vertexRadiusP;

	/**
	 * radius of the spheres and cylinders representing the vertices and edges in virtual space
	 */
	private double vertexRadiusV;

	/**
	 * show the faces of the simplicial complex representing physical space, i.e. the triangles where the lenses are
	 */
	private boolean showFaces;

	/**
	 * show the faces of the simplicial complex representing virtual space
	 */
	private boolean showFacesV;
	
	/**
	 * surface property of the faces in physical space, i.e. the lenses
	 */
	private SurfaceProperty faceSurfaceProperty;

	/**
	 * surface property of the faces in virtual space
	 */
	private SurfaceProperty faceSurfacePropertyV;
	
	
	
	//
	// internal variables
	//
	
	/**
	 * the lens simplicial complex
	 */
	private IdealThinLensSimplicialComplex lensSimplicialComplex;
		
	// GUI panels
	private LabelledVector3DPanel optimumOutsideSpaceViewingPositionPanel;
	private LabelledDoublePanel vertexRadiusPPanel, vertexRadiusVPanel;
	private JCheckBox showStructurePCheckBox, showStructureVCheckBox, showFacesCheckBox, showFacesVCheckBox;
	private JComboBox<LensType> lensTypeRepresentingFaceComboBox;
	private SurfacePropertyPanel surfacePropertyPPanel, surfacePropertyVPanel, faceSurfacePropertyPanel, faceSurfacePropertyVPanel;
	private JButton convertButton;
	
	
	//
	// constructors
	//
	
	
	/**
	 * Create an EditableLensSimplicialComplex populated with an omnidirectional lens
	 * @param description
	 * @param parent
	 * @param studio
	 */
	public EditableLensSimplicialComplex(
			String description,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				null,	// ArrayList<NamedVector3D> vertices,
				null,	// ArrayList<NamedVector3D> verticesV,
				null,	// ArrayList<NamedEdge> edges,
				null,	// IdealThinLensSimplicialComplex lensSimplicialComplex,
				LensType.IDEAL_THIN_LENS,	// lensTypeRepresentingFace,
				new Vector3D(0, 0, 0),	// optimumOutsideSpaceViewingPosition
				true,	// showStructureP,
				SurfaceColour.BLUE_SHINY,	// surfacePropertyP,
				0.021,	// vertexRadiusP,
				true,	// showStructureV,
				SurfaceColour.RED_SHINY,	// surfacePropertyV,
				0.02,	// vertexRadiusV,
				false,	// showFaces,
				SurfaceColour.LIGHT_BLUE_SHINY,	// faceSurfaceProperty,
				false,	// showFacesV,
				SurfaceColour.LIGHT_RED_SHINY,	// faceSurfacePropertyV,
				parent,
				studio
			);
		
		// super(description, false, parent, studio);

		// create a physical-space simplicial complex
		
		initialiseToOmnidirectionalLens();
	}
	
	
	/**
	 * @param description
	 * @param vertices
	 * @param verticesV
	 * @param edges
	 * @param lensSimplicialComplex
	 * @param lensTypeRepresentingFace
	 * @param optimumOutsideSpaceViewingPosition
	 * @param showStructureP
	 * @param surfacePropertyP
	 * @param vertexRadiusP
	 * @param showStructureV
	 * @param surfacePropertyV
	 * @param vertexRadiusV
	 * @param showFaces
	 * @param faceSurfaceProperty
	 * @param showFacesV
	 * @param faceSurfacePropertyV
	 * @param parent
	 * @param studio
	 */
	public EditableLensSimplicialComplex(
			String description,
			ArrayList<NamedVector3D> vertices,
			ArrayList<NamedVector3D> verticesV,
			ArrayList<NamedEdge> edges,
			IdealThinLensSimplicialComplex lensSimplicialComplex,
			LensType lensTypeRepresentingFace,
			Vector3D optimumOutsideSpaceViewingPosition,
			boolean showStructureP,
			SurfaceProperty surfacePropertyP,
			double vertexRadiusP,
			boolean showStructureV,
			SurfaceProperty surfacePropertyV,
			double vertexRadiusV,
			boolean showFaces,
			SurfaceProperty faceSurfaceProperty,
			boolean showFacesV,
			SurfaceProperty faceSurfacePropertyV,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		this.vertices = vertices;
		this.verticesV = verticesV;
		this.edges = edges;
		this.lensSimplicialComplex = lensSimplicialComplex;
		this.lensTypeRepresentingFace = lensTypeRepresentingFace;
		this.optimumOutsideSpaceViewingPosition = optimumOutsideSpaceViewingPosition;
		this.showStructureP = showStructureP;
		this.showStructureV = showStructureV;
		this.surfacePropertyP = surfacePropertyP;
		this.surfacePropertyV = surfacePropertyV;
		this.vertexRadiusP = vertexRadiusP;
		this.vertexRadiusV = vertexRadiusV;
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
	public EditableLensSimplicialComplex(EditableLensSimplicialComplex original)
	{
		this(
			original.getDescription(),
			original.getVertices(),
			original.getVerticesV(),
			original.getEdges(),
			original.getLensSimplicialComplex(),
			original.getLensTypeRepresentingFace(),
			original.getOptimumOutsideSpaceViewingPosition(),
			original.isShowStructureP(),
			original.getSurfacePropertyP(),
			original.getVertexRadiusP(),
			original.isShowStructureV(),
			original.getSurfacePropertyV(),
			original.getVertexRadiusV(),
			original.isShowFaces(),
			original.getFaceSurfaceProperty(),
			original.isShowFacesV(),
			original.getFaceSurfacePropertyV(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableLensSimplicialComplex clone()
	{
		return new EditableLensSimplicialComplex(this);
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

	public ArrayList<NamedVector3D> getVerticesV() {
		return verticesV;
	}

	public void setVerticesV(ArrayList<NamedVector3D> verticesV) {
		this.verticesV = verticesV;
	}

	public ArrayList<NamedEdge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<NamedEdge> edges) {
		this.edges = edges;
	}

	public IdealThinLensSimplicialComplex getLensSimplicialComplex() {
		return lensSimplicialComplex;
	}

	public void setLensSimplicialComplex(IdealThinLensSimplicialComplex lensSimplicialComplex) {
		this.lensSimplicialComplex = lensSimplicialComplex;
	}

	public LensType getLensTypeRepresentingFace() {
		return lensTypeRepresentingFace;
	}


	public void setLensTypeRepresentingFace(LensType lensTypeRepresentingFace) {
		this.lensTypeRepresentingFace = lensTypeRepresentingFace;
	}


	public Vector3D getOptimumOutsideSpaceViewingPosition() {
		return optimumOutsideSpaceViewingPosition;
	}


	public void setOptimumOutsideSpaceViewingPosition(Vector3D optimumOutsideSpaceViewingPosition) {
		this.optimumOutsideSpaceViewingPosition = optimumOutsideSpaceViewingPosition;
	}


	public boolean isShowStructureP() {
		return showStructureP;
	}

	public void setShowStructureP(boolean showStructureP) {
		this.showStructureP = showStructureP;
	}

	public boolean isShowStructureV() {
		return showStructureV;
	}

	public void setShowStructureV(boolean showStructureV) {
		this.showStructureV = showStructureV;
	}

	public SurfaceProperty getSurfacePropertyP() {
		return surfacePropertyP;
	}

	public void setSurfacePropertyP(SurfaceProperty surfacePropertyP) {
		this.surfacePropertyP = surfacePropertyP;
	}

	public SurfaceProperty getSurfacePropertyV() {
		return surfacePropertyV;
	}

	public void setSurfacePropertyV(SurfaceProperty surfacePropertyV) {
		this.surfacePropertyV = surfacePropertyV;
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
	//
	//
	
	public void initialiseToOmnidirectionalLens()
	{
		// create a physical-space simplicial complex
		
		// create a new array of vertices
		vertices = new ArrayList<NamedVector3D>();
		
		// add a few vertices
		vertices.add(new NamedVector3D("back left bottom vertex", -1.5, 1.1, 10+0));	// 0; back left bottom vertex
		vertices.add(new NamedVector3D("back right bottom vertex",   1,   1, 10+0));	// 1; back right bottom vertex
		vertices.add(new NamedVector3D("front bottom vertex",             0, -.5, 10+0));	// 2; front bottom vertex
		vertices.add(new NamedVector3D("lower inner vertex",              0,   0, 10+0.6));	// 3; lower inner vertex
		vertices.add(new NamedVector3D("upper inner vertex",              0,   0, 10+1.2));	// 4; upper inner vertex
		vertices.add(new NamedVector3D("top vertex",                      0,   0, 10+2));	// 5; top vertex
//		vertices.add(new Vector3D(-1, 0, 10+1));	// 0; back left bottom vertex
//		vertices.add(new Vector3D( 1, 0, 10+1));	// 1; back right bottom vertex
//		vertices.add(new Vector3D( 0, 0, 10-1));	// 2; front bottom vertex
//		vertices.add(new Vector3D( 0, 0.3, 10+0));	// 3; lower inner vertex
//		vertices.add(new Vector3D( 0, 0.6, 10+0));	// 4; upper inner vertex
//		vertices.add(new Vector3D( 0, 1, 10+0));	// 5; top vertex
		
		// create a new array of edges
		edges = new ArrayList<NamedEdge>();

		// add a few edges
		try {
			edges.add(createNamedEdge(0, 1, vertices));	// 0
			edges.add(createNamedEdge(1, 2, vertices));	// 1
			edges.add(createNamedEdge(2, 0, vertices));	// 2
			edges.add(createNamedEdge(3, 0, vertices));	// 3
			edges.add(createNamedEdge(3, 1, vertices));	// 4
			edges.add(createNamedEdge(3, 2, vertices));	// 5
			edges.add(createNamedEdge(4, 0, vertices));	// 6
			edges.add(createNamedEdge(4, 1, vertices));	// 7	
			edges.add(createNamedEdge(4, 2, vertices));	// 8
			edges.add(createNamedEdge(5, 0, vertices));	// 9
			edges.add(createNamedEdge(5, 1, vertices));	// 10
			edges.add(createNamedEdge(5, 2, vertices));	// 11
			edges.add(createNamedEdge(3, 4, vertices));	// 12
			edges.add(createNamedEdge(4, 5, vertices));	// 13

			// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred

			SimplicialComplex simplicialComplex;
			simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
			

			// create a new array of vertices
			verticesV = new ArrayList<NamedVector3D>();

			// add a few vertices
			verticesV.add(new NamedVector3D("back left bottom vertex", -1.5, 1.1, 10+0));	// 0; back left bottom vertex
			verticesV.add(new NamedVector3D("back right bottom vertex",   1,   1, 10+0));	// 1; back right bottom vertex
			verticesV.add(new NamedVector3D("front bottom vertex",             0, -.5, 10+0));	// 2; front bottom vertex
			verticesV.add(new NamedVector3D("lower inner vertex",              0,   0, 10+0.8));	// 3; lower inner vertex
			verticesV.add(new NamedVector3D("upper inner vertex",              0,   0, 10+1.4));	// 4; upper inner vertex
			verticesV.add(new NamedVector3D("top vertex",                      0,   0, 10+2));	// 5; top vertex

			lensSimplicialComplex = new IdealThinLensSimplicialComplex(simplicialComplex, verticesV);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		populateSceneObjectCollection();
	}
	
	
	/**
	 * Initialises this to represent an omnidirectional lens with the given parameters.
	 * The virtual-space height of the upper inner vertex is calculated and returned.
	 * @param physicalSpaceFractionalLowerInnerVertexHeight	physical-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex
	 * @param physicalSpaceFractionalUpperInnerVertexHeight	physical-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the upper inner vertex
	 * @param virtualSpaceFractionalLowerInnerVertexHeight	virtual-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex
	 * @param topVertex
	 * @param baseCentre
	 * @param baseVertex1
	 * @return	the virtual-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the upper inner vertex
	 */
	public double initialiseToOmnidirectionalLens(
			double physicalSpaceFractionalLowerInnerVertexHeight,
			double physicalSpaceFractionalUpperInnerVertexHeight,
			double virtualSpaceFractionalLowerInnerVertexHeight,
			Vector3D topVertex,
			Vector3D baseCentre,
			Vector3D baseVertex1
		)
	{
		// define a vector from the base centre to the tip...
		Vector3D baseCentre2tip = Vector3D.difference(topVertex, baseCentre);

		// ... and calculate its length, which is the height of the cloak
		double height = baseCentre2tip.getLength();
		
		// also define a vector from the base centre to one of the base vertices...
		Vector3D baseCentre2baseVertex1 = Vector3D.difference(baseVertex1, baseCentre).getPartPerpendicularTo(baseCentre2tip);	// basically one of the vectors spanning the base
		
		// ... and its length, which is the base radius
		double baseRadius = baseCentre2baseVertex1.getLength();
		
		// absolute height of the lower inner vertex above the base in physical space
		double h2 = height * physicalSpaceFractionalLowerInnerVertexHeight;
		
		// absolute height of the lower inner vertex above the base in virtual space		
		double h2V = height * virtualSpaceFractionalLowerInnerVertexHeight;
		
		// absolute height of the upper inner vertex above the base in physical space
		double h3 = height * physicalSpaceFractionalUpperInnerVertexHeight;

		
		// calculate the focal lengths, according to the loop-imaging theorem
		double[] f234 = LoopImagingTheorem.getFocalLengthsForFourLensIntersectionGeometry1(
				baseRadius/2,	// x0; distance from base centre to one of the vertices * cos(60 degrees) with cos(60 degrees) = 1/2
				h2,	// y2
				height * physicalSpaceFractionalUpperInnerVertexHeight,	// y3
				height,	// y4
				h2 * h2V / (h2V - h2)	// f1
			);
		
		// calculate the virtual-space height of the upper inner vertex
		// The virtual-space position of the upper inner vertex is the image, due to the upper outer lenses, of the upper inner vertex's physical-space position.
		// The upper outer lenses have focal length f4 (in the list f234):
		double f4 = f234[2];
		// the (physical-space position) is a distance (height - h3)*cos(alpha4) from the lens L4, where alpha4 is the angle between the optical axis of L4
		// and the central axis (through all the principal points) of the cloak, which is perpendicular to L1
		double alpha4 = Math.atan2(height, baseRadius/2);
		// the virtual-space position is a distance (height - h3V)*cos(alpha4) from L4, and so the lens equation becomes
		//   1/f4 = 1/((height - h3)*cos(alpha4)) - 1/((height - h3V)*cos(alpha4)),
		// or (after multiplying through by cos(alpha4))
		//   cos(alpha4)/f4 = 1/(height-h3) - 1/(height-h3V),
		// which becomes
		//   1/(height-h3V) = 1/(height-h3) - cos(alpha4)/f4 = (f4 - cos(alpha4)*(height-h3)) / ((height-h3)*f4),
		// or
		//   height - h3V = (height-h3)*f4 / (f4 - cos(alpha4)*(height-h3)),
		// or
		//   h3V = height - (height-h3)*f4 / (f4 - cos(alpha4)*(height-h3))
		double h3V = height - (height-h3)*f4 / (f4 - Math.cos(alpha4)*(height-h3));
		
		double virtualSpaceFractionalUpperInnerVertexHeight = h3V/height;
		
		initialiseToOmnidirectionalLens(
				physicalSpaceFractionalLowerInnerVertexHeight,
				physicalSpaceFractionalUpperInnerVertexHeight,
				virtualSpaceFractionalLowerInnerVertexHeight,
				virtualSpaceFractionalUpperInnerVertexHeight,
				topVertex,
				baseCentre,
				baseVertex1
			);
		
		return virtualSpaceFractionalUpperInnerVertexHeight;
	}

	
	/**
	 * make this an omnidirectional lens with given parameters
	 * @param physicalSpaceFractionalLowerInnerVertexHeight	physical-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex
	 * @param physicalSpaceFractionalUpperInnerVertexHeight	physical-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the upper inner vertex
	 * @param virtualSpaceFractionalLowerInnerVertexHeight	virtual-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex
	 * @param virtualSpaceFractionalUpperInnerVertexHeight	virtual-space height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the upper inner vertex
	 * @param topVertex
	 * @param baseCentre
	 * @param baseVertex1
	 */
	public void initialiseToOmnidirectionalLens(
			double physicalSpaceFractionalLowerInnerVertexHeight,
			double physicalSpaceFractionalUpperInnerVertexHeight,
			double virtualSpaceFractionalLowerInnerVertexHeight,
			double virtualSpaceFractionalUpperInnerVertexHeight,
			Vector3D topVertex,
			Vector3D baseCentre,
			Vector3D baseVertex1
		)
	{
		// create a physical-space simplicial complex
		
		// create a new array of vertices
		vertices = new ArrayList<NamedVector3D>();
		
		Vector3D baseCentre2tip = Vector3D.difference(topVertex, baseCentre);
		Vector3D baseCentre2baseVertex1 = Vector3D.difference(baseVertex1, baseCentre).getPartPerpendicularTo(baseCentre2tip);	// basically one of the vectors spanning the base
		Vector3D baseCentreSpanVector2 = Vector3D.crossProduct(baseCentre2tip, baseCentre2baseVertex1).getWithLength(baseCentre2baseVertex1.getLength());
		
		// add a few vertices
		for(int i=0; i<3; i++)
		{
			double phi = i*2*Math.PI/3;
			vertices.add(new NamedVector3D("base vertex #"+(i+1), 
					Vector3D.sum(
							baseCentre,
							baseCentre2baseVertex1.getProductWith(Math.cos(phi)),
							baseCentreSpanVector2.getProductWith(Math.sin(phi))
						)
				));	// 0, 1 and 2: base vertices
		}
		vertices.add(new NamedVector3D("lower inner vertex", Vector3D.sum(
				baseCentre,
				baseCentre2tip.getProductWith(physicalSpaceFractionalLowerInnerVertexHeight)
			)));	// 3; lower inner vertex
		vertices.add(new NamedVector3D("upper inner vertex", Vector3D.sum(
				baseCentre,
				baseCentre2tip.getProductWith(physicalSpaceFractionalUpperInnerVertexHeight)
			)));	// 4; upper inner vertex
		vertices.add(new NamedVector3D("top vertex", topVertex));	// 5; top vertex (tip)
		
		// create a new array of edges
		edges = new ArrayList<NamedEdge>();

		// add a few edges
		try {
			edges.add(createNamedEdge(0, 1, vertices));	// 0
			edges.add(createNamedEdge(1, 2, vertices));	// 1
			edges.add(createNamedEdge(2, 0, vertices));	// 2
			edges.add(createNamedEdge(3, 0, vertices));	// 3
			edges.add(createNamedEdge(3, 1, vertices));	// 4
			edges.add(createNamedEdge(3, 2, vertices));	// 5
			edges.add(createNamedEdge(4, 0, vertices));	// 6
			edges.add(createNamedEdge(4, 1, vertices));	// 7	
			edges.add(createNamedEdge(4, 2, vertices));	// 8
			edges.add(createNamedEdge(5, 0, vertices));	// 9
			edges.add(createNamedEdge(5, 1, vertices));	// 10
			edges.add(createNamedEdge(5, 2, vertices));	// 11
			edges.add(createNamedEdge(3, 4, vertices));	// 12
			edges.add(createNamedEdge(4, 5, vertices));	// 13

			// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred

			SimplicialComplex simplicialComplex;
			simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
			

			// create a new array of vertices
			verticesV = new ArrayList<NamedVector3D>();

			// add a few vertices
			// add a few vertices
			for(int i=0; i<3; i++)
			{
				double phi = i*2*Math.PI/3;
				verticesV.add(new NamedVector3D("base vertex #"+(i+1), 
						Vector3D.sum(
								baseCentre,
								baseCentre2baseVertex1.getProductWith(Math.cos(phi)),
								baseCentreSpanVector2.getProductWith(Math.sin(phi))
							)
					));	// 0, 1 and 2: base vertices
			}
			verticesV.add(new NamedVector3D("lower inner vertex", Vector3D.sum(
					baseCentre,
					baseCentre2tip.getProductWith(virtualSpaceFractionalLowerInnerVertexHeight)
				)));	// 3; lower inner vertex
			verticesV.add(new NamedVector3D("upper inner vertex", Vector3D.sum(
					baseCentre,
					baseCentre2tip.getProductWith(virtualSpaceFractionalUpperInnerVertexHeight)
				)));	// 4; upper inner vertex
			verticesV.add(new NamedVector3D("top vertex", topVertex));	// 5; top vertex (tip)

			lensSimplicialComplex = new IdealThinLensSimplicialComplex(simplicialComplex, verticesV);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}

		populateSceneObjectCollection();
	}
	


	
	//
	// add the scene objects that form this TO structure
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// add the scene objects representing the simplicial complex in physical space
		if(lensSimplicialComplex != null)
		{
			lensSimplicialComplex.setLensTypeRepresentingFace(lensTypeRepresentingFace);
			lensSimplicialComplex.setOptimumOutsideSpaceViewingPosition(optimumOutsideSpaceViewingPosition);
			lensSimplicialComplex.populateEditableSceneObjectCollection(
					this,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// lens transmission coefficient	TODO make a variable!
					false,	// shadow-throwing	TODO make variable
					showStructureP,
					surfacePropertyP,
					vertexRadiusP,
					showStructureV,
					surfacePropertyV,
					vertexRadiusV,
					showStructureP,
					surfacePropertyP,
					vertexRadiusP,
					showStructureV,
					surfacePropertyV,
					vertexRadiusV,
					showFaces,
					faceSurfaceProperty,
					showFacesV,
					faceSurfacePropertyV,
					this,	// parent
					getStudio()
					);
		}

	}
	
	
	private NamedEdge createNamedEdge(int vertexIndex0, int vertexIndex1, ArrayList<NamedVector3D> vertices)
	throws InconsistencyException
	{
		return new NamedEdge(vertices.get(vertexIndex0).getName() + " to " + vertices.get(vertexIndex1).getName(), null, vertexIndex0, vertexIndex1);
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane, "wrap");
		
		//
		// the lens panel
		//
		
		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		
		lensTypeRepresentingFaceComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeRepresentingFaceComboBox.setSelectedItem(lensTypeRepresentingFace);
		lensPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeRepresentingFaceComboBox), "wrap");
		
		optimumOutsideSpaceViewingPositionPanel = new LabelledVector3DPanel("Optimum outside-space viewing position");
		optimumOutsideSpaceViewingPositionPanel.setVector3D(optimumOutsideSpaceViewingPosition);
		lensPanel.add(optimumOutsideSpaceViewingPositionPanel, "wrap");

		tabbedPane.addTab("Lenses", lensPanel);

		
		//
		// the physical-space-visualisation panel
		//
		
		JPanel visualisationPPanel = new JPanel();
		visualisationPPanel.setLayout(new MigLayout("insets 0"));
		
		showStructurePCheckBox = new JCheckBox("Show physical-space structure");
		visualisationPPanel.add(showStructurePCheckBox, "wrap");

		vertexRadiusPPanel = new LabelledDoublePanel("Edge radius");
		visualisationPPanel.add(vertexRadiusPPanel, "wrap");

		surfacePropertyPPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationPPanel.add(surfacePropertyPPanel, "wrap");
		surfacePropertyPPanel.setIPanel(iPanel);

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
		
		showStructureVCheckBox = new JCheckBox("Show virtual-space structure");
		visualisationVPanel.add(showStructureVCheckBox, "wrap");

		vertexRadiusVPanel = new LabelledDoublePanel("Edge radius");
		visualisationVPanel.add(vertexRadiusVPanel, "wrap");

		surfacePropertyVPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		visualisationVPanel.add(surfacePropertyVPanel, "wrap");
		surfacePropertyVPanel.setIPanel(iPanel);

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
		
		lensTypeRepresentingFaceComboBox.setSelectedItem(lensTypeRepresentingFace);
		optimumOutsideSpaceViewingPositionPanel.setVector3D(optimumOutsideSpaceViewingPosition);
		
		showStructurePCheckBox.setSelected(showStructureP);
		surfacePropertyPPanel.setSurfaceProperty(surfacePropertyP);
		vertexRadiusPPanel.setNumber(vertexRadiusP);
		showFacesCheckBox.setSelected(showFaces);
		faceSurfacePropertyPanel.setSurfaceProperty(faceSurfaceProperty);
		
		showStructureVCheckBox.setSelected(showStructureV);
		surfacePropertyVPanel.setSurfaceProperty(surfacePropertyV);
		vertexRadiusVPanel.setNumber(vertexRadiusV);
		showFacesVCheckBox.setSelected(showFacesV);
		faceSurfacePropertyVPanel.setSurfaceProperty(faceSurfacePropertyV);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableLensSimplicialComplex acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		lensTypeRepresentingFace = (LensType)(lensTypeRepresentingFaceComboBox.getSelectedItem());
		optimumOutsideSpaceViewingPosition = optimumOutsideSpaceViewingPositionPanel.getVector3D();

		setShowStructureP(showStructurePCheckBox.isSelected());
		setSurfacePropertyP(surfacePropertyPPanel.getSurfaceProperty());
		setVertexRadiusP(vertexRadiusPPanel.getNumber());
		setShowFaces(showFacesCheckBox.isSelected());
		setFaceSurfaceProperty(faceSurfacePropertyPanel.getSurfaceProperty());
		
		setShowStructureV(showStructureVCheckBox.isSelected());
		setSurfacePropertyV(surfacePropertyVPanel.getSurfaceProperty());
		setVertexRadiusV(vertexRadiusVPanel.getNumber());
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