package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.simplicialComplex.HomogeneousPlanarImagingSurfaceSimplicialComplex;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable shifty cloak, realised using homogeneous GCLAs (i.e. homogeneous glenses ).
 * 
 * This implementation is as a subclass of EditableHomogeneousPlanarImagingSurfaceSimplicialComplex. 
 * 
 * The outside of the cloak is a regular tetrahedron.
 * 
 * Inside the outside tetrahedron, there is a smaller inner tetrahedron which is, in physical space, centred in the outer tetrahedron.
 * The inner tetrahedron is dual to the outer tetrahedron (see https://en.wikipedia.org/wiki/Tetrahedron); 
 * this means that, when seen from the centroid of both tetrahedra, each vertex of the inner tetrahedron is in the direction of the centre of one of the faces of the outer tetrahedron.
 * In virtual space, the inner tetrahedron -- and anything placed inside it -- appears shifted.
 * 
 * The vertices of the regular outer tetrahedron are given by four of the 8 vertices of a cube.
 * The vertices of the regular inner tetrahedron are given by the other four vertices of a similar, but smaller, cube.
 * 
 * @author Johannes
 */
/**
 * @author johannes
 *
 */
public class EditableGCLAsTetrahedralShiftyCloak extends EditableHomogeneousPlanarImagingSurfaceSimplicialComplex implements ActionListener
{
	private static final long serialVersionUID = 2347562642193958376L;

	// parameters

	/**
	 * the centre of the tetrahedral cloak
	 */
	private Vector3D centre;
	
	/**
	 * vector along one of the edges of the cube defining the orientation
	 */
	private Vector3D u;

	/**
	 * vector along the second of the edges of the cube defining the orientation
	 */
	private Vector3D v;

	/**
	 * vector along the third of the edges of the cube defining the orientation
	 */
	private Vector3D w;

	/**
	 * side length of the cube that shares four vertices with the outer tetrahedron
	 */
	private double sideLength;
	
	/**
	 * side length of the cube that shares four vertices with the inner tetrahedron (which appears shifted by delta)
	 */
	private double sideLengthI;
	
	/**
	 * the vector describing the apparent shift of the inner cube when seen from the outside
	 */
	private Vector3D delta;
	
	private double frameRadius;
	private boolean asymmetricConfiguration, showGCLAs, showFrames;
	private SurfaceProperty frameSurfaceProperty;
	
	
	// constructors
	
	public EditableGCLAsTetrahedralShiftyCloak(
			String description,
			Vector3D centre,
			Vector3D u,
			Vector3D v,
			Vector3D w,
			boolean asymmetricConfiguration,
			double sideLength,
			double sideLengthI,
			Vector3D delta,
			boolean showGCLAs,
			double gCLAsTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,			
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		// super(description, parent, studio);
		super(
				description,
				null,	// homogeneousPlanarImagingElementSimplicialComplex
				showGCLAs,	// showImagingElements
				showFrames,	// showVertices
				frameSurfaceProperty,	// vertexSurfaceProperty
				frameRadius,	// vertexRadius
				false,	// showVerticesV
				SurfaceColour.RED_SHINY,	// vertexSurfacePropertyV
				0.99*frameRadius,	// vertexRadiusV
				true,	// showEdges
				frameSurfaceProperty,	// edgeSurfaceProperty
				frameRadius,	// edgeRadius
				false,	// showEdgesV
				SurfaceColour.RED_SHINY,	// edgeSurfacePropertyV
				0.99*frameRadius,	// edgeRadiusV,
				false,	// showFaces
				SurfaceColour.LIGHT_BLUE_SHINY,	// faceSurfaceProperty
				false,	// showFacesV,
				SurfaceColour.LIGHT_RED_SHINY,	// faceSurfacePropertyV
				parent,
				studio
			);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setU(u);
		setV(v);
		setW(w);
		setAsymmetricConfiguration(asymmetricConfiguration);
		setSideLength(sideLength);
		setSideLengthI(sideLengthI);
		setDelta(delta);
		setShowGCLAs(showGCLAs);
		setImagingElementTransmissionCoefficient(gCLAsTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);

		populateSceneObjectCollection();
	}


	public EditableGCLAsTetrahedralShiftyCloak(SceneObject parent, Studio studio)
	{
		this(
				"Glens shifty cloak",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// u
				new Vector3D(0, 1, 0),	// v
				new Vector3D(0, 0, 1),	// w
				true,	// asymmetricConfiguration
				1,	// sideLength
				0.3,	// sideLengthInnerCube
				new Vector3D(0, 0, 0),	// delta
				true,	// showGCLAs
				0.96,	// gCLAsTransmissionCoefficient
				true,	// showFrames
				0.01,	// frameRadius
				SurfaceColour.GREY50_SHINY,	// frameSurfaceProperty
				parent,
				studio
			);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableGCLAsTetrahedralShiftyCloak(EditableGCLAsTetrahedralShiftyCloak original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getU().clone(),
			original.getV().clone(),
			original.getW().clone(),
			original.isAsymmetricConfiguration(),
			original.getSideLength(),
			original.getSideLengthI(),
			original.getDelta().clone(),
			original.isShowGCLAs(),
			original.getImagingElementTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),			
			original.getParent(),
			original.getStudio()
		);
	}
	

	@Override
	public EditableGCLAsTetrahedralShiftyCloak clone()
	{
		return new EditableGCLAsTetrahedralShiftyCloak(this);
	}

	
	
	
	// setters and getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getU() {
		return u;
	}

	public void setU(Vector3D u) {
		this.u = u;
	}

	public Vector3D getV() {
		return v;
	}

	public void setV(Vector3D v) {
		this.v = v;
	}

	public Vector3D getW() {
		return w;
	}

	public void setW(Vector3D w) {
		this.w = w;
	}

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public double getSideLengthI() {
		return sideLengthI;
	}

	public void setSideLengthI(double sideLengthI) {
		this.sideLengthI = sideLengthI;
	}

	public Vector3D getDelta() {
		return delta;
	}

	public void setDelta(Vector3D delta) {
		this.delta = delta;
	}

	public boolean isAsymmetricConfiguration() {
		return asymmetricConfiguration;
	}


	public void setAsymmetricConfiguration(boolean asymmetricConfiguration) {
		this.asymmetricConfiguration = asymmetricConfiguration;
	}


	public boolean isShowGCLAs() {
		return showGCLAs;
	}

	public void setShowGCLAs(boolean showGCLAs) {
		this.showGCLAs = showGCLAs;
	}

	public double getFrameRadius() {
		return frameRadius;
	}

	public void setFrameRadius(double frameRadius) {
		this.frameRadius = frameRadius;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}

	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}





	
	// internal variables
	
	// containers for the GCLAs and frames
	EditableSceneObjectCollection gCLAs, frames;
	
	@Override
	public void populateSceneObjectCollection()
	{
		// create the physical-space simplicial complex
		
		// create a new array of vertices
		ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
		
		// outside tetrahedron
		vertices.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLength), v.getWithLength( 0.5*sideLength), w.getWithLength( 0.5*sideLength)));	// 0
		vertices.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLength), v.getWithLength(-0.5*sideLength), w.getWithLength(-0.5*sideLength)));	// 1
		vertices.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLength), v.getWithLength( 0.5*sideLength), w.getWithLength(-0.5*sideLength)));	// 2
		vertices.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLength), v.getWithLength(-0.5*sideLength), w.getWithLength( 0.5*sideLength)));	// 3
		
		// inner tetrahedron
		if(asymmetricConfiguration)
		{
			ArrayList<Vector3D> verticesI  = new ArrayList<Vector3D>();
			verticesI.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLengthI), v.getWithLength( 0.5*sideLengthI), w.getWithLength( 0.5*sideLengthI)));	// 4
			verticesI.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLengthI), v.getWithLength(-0.5*sideLengthI), w.getWithLength(-0.5*sideLengthI)));	// 5
			verticesI.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLengthI), v.getWithLength( 0.5*sideLengthI), w.getWithLength(-0.5*sideLengthI)));	// 6
			verticesI.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLengthI), v.getWithLength(-0.5*sideLengthI), w.getWithLength( 0.5*sideLengthI)));	// 7
			
			// now rotate the inner tetrahedron slightly by pushing the jth vertex of the inner tetrahedron slightly away
			// from the ((j+1) % 4)th vertex of the outer tetrahedron
			// (if this is not done, pairs of edges connecting vertices of the inner and outer tetrahedra intersect, confusing the SimplicialComplex class,
			// which then arrives at the wrong number of simplices...)
			for(int j=0; j<4; j++)
				vertices.add(
						Vector3D.sum(
								vertices.get((j+1) % 4),
								Vector3D.difference(verticesI.get(j), vertices.get((j+1) % 4)).getProductWith(1.01)
//								verticesI.get(j),
//								Vector3D.difference(vertices.get((j+1) % 4), verticesI.get(j)).getWithLength(-0.01)
							)
					);
		}
		else
		{
			vertices.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLengthI), v.getWithLength(-0.5*sideLengthI), w.getWithLength(-0.5*sideLengthI)));	// 4
			vertices.add(Vector3D.sum(getCentre(), u.getWithLength(-0.5*sideLengthI), v.getWithLength( 0.5*sideLengthI), w.getWithLength( 0.5*sideLengthI)));	// 5
			vertices.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLengthI), v.getWithLength(-0.5*sideLengthI), w.getWithLength( 0.5*sideLengthI)));	// 6
			vertices.add(Vector3D.sum(getCentre(), u.getWithLength( 0.5*sideLengthI), v.getWithLength( 0.5*sideLengthI), w.getWithLength(-0.5*sideLengthI)));	// 7
		}

		// create a new array of edges
		ArrayList<Edge> edges = new ArrayList<Edge>();

		// add the edges
		try {
			// outer tetrahedron
			edges.add(new Edge(0, 1));	// 0
			edges.add(new Edge(1, 2));	// 1
			edges.add(new Edge(2, 0));	// 2
			edges.add(new Edge(0, 3));	// 3
			edges.add(new Edge(1, 3));	// 4
			edges.add(new Edge(2, 3));	// 5
			
			// inner tetrahedron
			edges.add(new Edge(4, 5));	// 6
			edges.add(new Edge(5, 6));	// 7	
			edges.add(new Edge(6, 4));	// 8
			edges.add(new Edge(4, 7));	// 9
			edges.add(new Edge(5, 7));	// 10
			edges.add(new Edge(6, 7));	// 11

			// tetrahedra filling the volume inside the outer tetrahedron and at the same time outside the inner tetrahedron
			if(asymmetricConfiguration)
			{
				// connect vertex i of outer tetrahedron to all vertices of inner tetrahedron other than vertex (i+1) mod 4
				for(int i=0; i<4; i++)
				{
					for(int j=0; j<4; j++)
					{
						if(i != ((j+1) % 4))
						{
							edges.add(new Edge(i, 4+j));
						}
					}
				}
			}
			else
			{
				// tetrahedron connected to vertex 0 of the outer tetrahedron
				edges.add(new Edge(0, 5));	// 12
				edges.add(new Edge(0, 6));	// 13
				edges.add(new Edge(0, 7));	// 14

				// tetrahedron connected to vertex 1 of the outer tetrahedron
				edges.add(new Edge(1, 4));	// 15
				edges.add(new Edge(1, 6));	// 16
				edges.add(new Edge(1, 7));	// 17

				// tetrahedron connected to vertex 2 of the outer tetrahedron
				edges.add(new Edge(2, 4));	// 18
				edges.add(new Edge(2, 5));	// 19	
				edges.add(new Edge(2, 7));	// 20

				// tetrahedron connected to vertex 3 of the outer tetrahedron
				edges.add(new Edge(3, 4));	// 21
				edges.add(new Edge(3, 5));	// 22
				edges.add(new Edge(3, 6));	// 23
			}

			// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred

			SimplicialComplex simplicialComplex;
			simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
			

			// create a new array of vertices
			ArrayList<Vector3D> verticesV = new ArrayList<Vector3D>();

			// the first four vertices in physical space are those of the outer tetrahedron, and they are identical in virtual space
			for(int v=0; v<4; v++) verticesV.add(vertices.get(v));
			
			// the second four vertices in physical space are those of the inner tetrahedron, and they are shifted by delta in virtual space
			for(int v=4; v<8; v++) verticesV.add(Vector3D.sum(vertices.get(v), delta));

			homogeneousPlanarImagingElementSimplicialComplex = new HomogeneousPlanarImagingSurfaceSimplicialComplex(simplicialComplex, verticesV);
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
		
//		for(Face f:homogeneousPlanarImagingElementSimplicialComplex.getFaces())
//		{
//			System.out.println(
//					"EditableGCLAsShiftyCloak::populateSceneObjectCollection: Type of face #"+homogeneousPlanarImagingElementSimplicialComplex.getFaces().indexOf(f)+
//					": "+f.getClass().toString()+
//					", noOfFacesToOutside="+f.getNoOfFacesToOutside());
//			for(int v=0; v<3; v++) System.out.println("vertex #"+v+"="+f.getVertexIndices()[v]);
//		}

		setShowImagingElements(showGCLAs);
		setShowVertices(showFrames);
		setShowEdges(showFrames);
		
		super.populateSceneObjectCollection();
	}

	
	
	
	
	// GUI stuff
	
	
	
	// GUI panels
	private LabelledVector3DPanel centreLine, plusUDirectionLine, plusVDirectionLine, plusWDirectionLine, deltaLine;
	private LabelledDoublePanel sideLengthLine, sideLengthInnerCubeLine, gCLAsTransmissionCoefficientLine, frameRadiusLine;
	private JButton convertButton;
	private JCheckBox asymmetricConfigurationCheckBox, showGCLAsCheckBox, showFramesCheckBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cubic glens cloak"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the shifty cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		centreLine = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centreLine, "wrap");

		plusUDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(plusUDirectionLine, "wrap");

		plusVDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(plusVDirectionLine, "wrap");

		plusWDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(plusWDirectionLine, "wrap");

		basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");
		
		asymmetricConfigurationCheckBox = new JCheckBox("Asymmetric configuration");
		basicParametersPanel.add(asymmetricConfigurationCheckBox, "wrap");

		sideLengthLine = new LabelledDoublePanel("Side length");
		basicParametersPanel.add(sideLengthLine, "wrap");

		sideLengthInnerCubeLine = new LabelledDoublePanel("Side length of inner cube");
		basicParametersPanel.add(sideLengthInnerCubeLine, "wrap");

		deltaLine = new LabelledVector3DPanel("Apparent shift of inner cube in virtual space");
		basicParametersPanel.add(deltaLine, "wrap");

		showGCLAsCheckBox = new JCheckBox("Show GCLAs");
		basicParametersPanel.add(showGCLAsCheckBox, "wrap");

		gCLAsTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each surface");
		basicParametersPanel.add(gCLAsTransmissionCoefficientLine, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));

		showFramesCheckBox = new JCheckBox("Show frames");
		framePanel.add(showFramesCheckBox, "wrap");
		
		frameRadiusLine = new LabelledDoublePanel("frame cylinder radius");
		framePanel.add(frameRadiusLine, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel, "wrap");
		frameSurfacePropertyPanel.setIPanel(iPanel);

		// omitInnermostSurfacesCheckBox = new JCheckBox("Simplified cloak (omit innermost interfaces)");
		// editPanel.add(omitInnermostSurfacesCheckBox);
		
		tabbedPane.addTab("Frames", framePanel);
		
		editPanel.add(tabbedPane, "wrap");

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
		
		centreLine.setVector3D(getCentre());
		plusUDirectionLine.setVector3D(getU());
		plusVDirectionLine.setVector3D(getV());
		plusWDirectionLine.setVector3D(getW());
		asymmetricConfigurationCheckBox.setSelected(asymmetricConfiguration);
		sideLengthLine.setNumber(getSideLength());
		sideLengthInnerCubeLine.setNumber(getSideLengthI());
		deltaLine.setVector3D(delta);
		showGCLAsCheckBox.setSelected(isShowGCLAs());
		gCLAsTransmissionCoefficientLine.setNumber(getImagingElementTransmissionCoefficient());	// getGCLAsTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableGCLAsTetrahedralShiftyCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setU(plusUDirectionLine.getVector3D());
		setV(plusVDirectionLine.getVector3D());
		setW(plusWDirectionLine.getVector3D());
		setAsymmetricConfiguration(asymmetricConfigurationCheckBox.isSelected());
		setSideLength(sideLengthLine.getNumber());
		setSideLengthI(sideLengthInnerCubeLine.getNumber());
		setDelta(deltaLine.getVector3D());
		setShowGCLAs(showGCLAsCheckBox.isSelected());
		setImagingElementTransmissionCoefficient(gCLAsTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-glens cloak");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				setFrameSurfaceProperty((SurfaceProperty)edited);
				frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
			}
	}
}