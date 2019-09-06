package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
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
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.sceneObjects.ConeTop;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;

/**
 * A space-cancelling wedge, that is, a wedge of space that looks like it isn't there.
 * The basic geometry of the wedge is given by its apex edge, the apex angle (the angle of space that is "cancelled"),
 * and the bisector direction (the direction from the apex edge to the middle of the "cancelled" space).
 * 
 * @author Johannes Courtial
 */
public class EditableMirroredSpaceCancellingWedge extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 5982446479094292208L;


	//
	// parameters
	//
	
	/**
	 * Apex angle of the null-space wedge, &nu;, in radians
	 */
	private double apexAngle;
	
	/**
	 * Centre of the apex edge, i.e. the edge through the apex of the isosceles triangle.
	 */
	private Vector3D apexEdgeCentre;
	
	/**
	 * Direction of the apex edge.
	 * This is a line of length <i>wedgeHeight</i>, centred on the apex-edge centre.
	 */
	private Vector3D apexEdgeDirection;
	
	/**
	 * Direction of the null-space-wedge bisector, i.e. the line through the centre of the wedge that is perpendicular to the edge direction.
	 * Together with the edge direction, this determines the orientation of the wedge.
	 */
	private Vector3D bisectorDirection;
	
	/**
	 * Leg length of the wedge.
	 * Each of the wedges is an extruded isoceles triangle of leg length <i>legLength</i>.
	 */
	private double legLength;
	
	/**
	 * Wedge height.
	 * Each of the wedges is an isoceles triangle, extruded to height <i>height</i>.
	 */
	private double apexEdgeLength;

	/**
	 * If true, show the refracting surfaces, otherwise don't
	 */
	private boolean showSurfaces;
	
	/**
	 * Transmission coefficient of each refracting surface
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * If true, show edges of refracting surfaces, otherwise don't
	 */
	private boolean showEdges;
	
	/**
	 * radius of the edges (if shown)
	 */
	private double edgeRadius;
	
	/**
	 * surface property of the edges (if shown)
	 */

	private SurfaceProperty edgeSurfaceProperty;
	
	/**
	 * Null-space-wedge type, which describes the way this null-space wedge is realised
	 */
	private GluingType gluingType;
	
	// for debugging
	private boolean showBisector = true;
	private boolean colourMirrorSurfaces =  true;
	private boolean colourNegativeRefractingSurfaces = true;


	//
	// constructors
	//
	
	public EditableMirroredSpaceCancellingWedge(
			String description,
			double apexAngle,
			Vector3D apexEdgeCentre,
			Vector3D apexEdgeDirection,
			Vector3D bisectorDirection,
			double legLength,
			double apexEdgeLength,
			boolean showSurfaces,
			double surfaceTransmissionCoefficient,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			GluingType gluingType,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		this.apexAngle = apexAngle;
		this.apexEdgeCentre = apexEdgeCentre;
		this.apexEdgeDirection = apexEdgeDirection;
		this.bisectorDirection = bisectorDirection;
		this.legLength = legLength;
		this.apexEdgeLength = apexEdgeLength;
		this.showSurfaces = showSurfaces;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.showEdges = showEdges;
		this.edgeRadius = edgeRadius;
		this.edgeSurfaceProperty = edgeSurfaceProperty;
		this.gluingType = gluingType;
		
		// System.out.println("EditableMirroredSpaceCancellingWedge::EditableMirroredSpaceCancellingWedge: apexAngle="+apexAngle+", apexEdgeCentre="+apexEdgeCentre+", apexEdgeDirection="+apexEdgeDirection+", bisectorDirection="+bisectorDirection);

		populateSceneObjectCollection();
	}

	/**
	 * Create a default null-space wedge
	 * @param parent
	 * @param studio
	 */
	public EditableMirroredSpaceCancellingWedge(SceneObject parent, Studio studio)
	{
		this(
				"Null-space wedge",	// description
				MyMath.deg2rad(90),	// apexAngle
				new Vector3D(0, 0, 10),	// apexEdgeCentre
				new Vector3D(0, 1, 0),	// apexEdgeDirection
				new Vector3D(1, 0, 0),	// bisectorDirection
				1,	// legLength
				1,	// apexEdgeLength
				true,	// showSurfaces
				0.96,	// surfaceTransmissionCoefficient
				false,	// showEdges
				0.01,	// edgeRadius
				SurfaceColour.RED_SHINY,	// edgeSurfaceProperty
				GluingType.NEGATIVE_SPACE_WEDGES,	// gluingType
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableMirroredSpaceCancellingWedge(EditableMirroredSpaceCancellingWedge original)
	{
		this(
			original.getDescription(),
			original.getApexAngle(),
			original.getApexEdgeCentre(),
			original.getApexEdgeDirection(),
			original.getBisectorDirection(),
			original.getLegLength(),
			original.getApexEdgeLength(),
			original.isShowSurfaces(),
			original.getSurfaceTransmissionCoefficient(),
			original.isShowEdges(),
			original.getEdgeRadius(),
			original.getEdgeSurfaceProperty().clone(),
			original.getGluingType(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableMirroredSpaceCancellingWedge clone()
	{
		return new EditableMirroredSpaceCancellingWedge(this);
	}

	
	//
	// setters and getters
	//
	

	public double getApexAngle() {
		return apexAngle;
	}

	public void setApexAngle(double apexAngle) {
		this.apexAngle = apexAngle;
	}

	public Vector3D getApexEdgeCentre() {
		return apexEdgeCentre;
	}

	public void setApexEdgeCentre(Vector3D apexEdgeCentre) {
		this.apexEdgeCentre = apexEdgeCentre;
	}

	public Vector3D getApexEdgeDirection() {
		return apexEdgeDirection;
	}

	public void setApexEdgeDirection(Vector3D apexEdgeDirection) {
		this.apexEdgeDirection = apexEdgeDirection;
	}

	public Vector3D getBisectorDirection() {
		return bisectorDirection;
	}

	public void setBisectorDirection(Vector3D bisectorDirection) {
		this.bisectorDirection = bisectorDirection;
	}

	public double getLegLength() {
		return legLength;
	}

	public void setLegLength(double legLength) {
		this.legLength = legLength;
	}

	public double getApexEdgeLength() {
		return apexEdgeLength;
	}

	public void setApexEdgeLength(double apexEdgeLength) {
		this.apexEdgeLength = apexEdgeLength;
	}

	public boolean isShowSurfaces() {
		return showSurfaces;
	}

	public void setShowSurfaces(boolean showSurfaces) {
		this.showSurfaces = showSurfaces;
	}

	public double getSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}

	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
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

	public GluingType getGluingType() {
		return gluingType;
	}

	public void setGluingType(GluingType gluingType) {
		this.gluingType = gluingType;
	}


	
	//
	// internal variables
	//
	
	// containers for the leg surfaces the corresponding mirrors, and the edges
	private EditableSceneObjectCollection surfaces, edges;



	// define coordinate system of star
	/**
	 * unit vector in the direction of the apex edge
	 */
	private Vector3D a;
	
	/**
	 * unit vector in the direction of the wedge bisector
	 */
	private Vector3D b;
	
	/**
	 * unit vector perpendicular to both a and b
	 */
	private Vector3D c;
	
	public void calculateCoordinateAxisDirections()
	{
		// define coordinate system of the wedge
		
		// unit vector in the direction of the apex edge
		a = apexEdgeDirection.getNormalised();
		
		// unit vector in the direction of the wedge bisector
		b = bisectorDirection.getPartPerpendicularTo(a).getNormalised();
		
		// unit vector perpendicular to both a and b
		c = Vector3D.crossProduct(b, a);
	}
	
	
	private ConeTop getConeForEdge(
			String description,
			Vector3D edgeVertexOnApexEdge,	// edgeVertexOnApexEdge,
			Vector3D otherEdgeVertex	// otherEdgeVertex
		)
	{
		// the apex etc. of each mirrored cone is calculated from points which lie on straight lines through principal points of the leg surfaces,
		// extended by a small length, delta
		// see J's lab book 16/5/19
		double delta = 0.01*apexEdgeLength;	// TODO this should be small, but positive

		Vector3D coneApex = Vector3D.sum(edgeVertexOnApexEdge, Vector3D.difference(edgeVertexOnApexEdge, otherEdgeVertex).getWithLength(delta));
		Vector3D apexEdgeCentreToConeAxis = Vector3D.sum(
				Vector3D.difference(apexEdgeCentre, otherEdgeVertex).getNormalised(),
				b
			).getWithLength(delta);
		Vector3D pointOnConeAxis = Vector3D.sum(
				apexEdgeCentre,
				apexEdgeCentreToConeAxis
			);
		Vector3D coneAxisDirection = Vector3D.difference(pointOnConeAxis, coneApex).getNormalised();
		
		return new ConeTop(
				description,	// description
				coneApex,	// apex
				coneAxisDirection,	// axis
				Math.acos(Vector3D.scalarProduct(
						coneAxisDirection,
						Vector3D.difference(otherEdgeVertex, coneApex).getNormalised()
						)),	// theta (cone angle)
				1,	// -1,	// height -- infinite
				colourMirrorSurfaces?
				SurfaceColour.getRandom():	// TODO
				// new ColourFilter(),	// TODO for debugging
				// SurfaceColourLightSourceIndependent.getRandom(),	// TODO for debugging
				Reflective.PERFECT_MIRROR,	// surfaceProperty
				surfaces,	// parent
				getStudio()	// studio
			);
	}
	
	private EditableParametrisedTriangle createTriangleWithCorrectNormalDirection(String description, Vector3D vertex1, Vector3D vertex2, Vector3D vertex3)
	{
		EditableParametrisedTriangle t = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
					description,
					vertex1,
					vertex2,
					vertex3,
					null,	// surfaceProperty -- set later
					null,	// parent -- set later
					getStudio()
				);
		
		if(Vector3D.scalarProduct(
				t.getNormalisedOutwardsSurfaceNormal(vertex1),
				b
			) < 0) return t;
		else return EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				description,
				vertex3,
				vertex2,
				vertex1,
				null,	// surfaceProperty -- set later
				null,	// parent -- set later
				getStudio()
			);
	}

	/**
	 * 
	 */
	private void populateCollections()
	{
		// first, calculate the vertices of the leg surfaces, which are...
		// ... the ends of the apex edge...
		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength));
		Vector3D apexEdgeTop    = Vector3D.sum(apexEdgeCentre, a.getProductWith(+0.5*apexEdgeLength));
		
		// ... and the third vertex of both leg surfaces
		Vector3D legSurface1Vertex3 = Vector3D.sum(
				apexEdgeCentre,
				b.getProductWith(legLength*Math.cos( 0.5*apexAngle)),
				c.getProductWith(legLength*Math.sin( 0.5*apexAngle))
			);
		Vector3D legSurface2Vertex3 = Vector3D.sum(
				apexEdgeCentre,
				b.getProductWith(legLength*Math.cos(-0.5*apexAngle)),
				c.getProductWith(legLength*Math.sin(-0.5*apexAngle))
			);
		
		// add spheres at the vertices of the leg surfaces
		edges.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Apex edge bottom",	// description
						apexEdgeBottom,	// centre
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,	// parent
						getStudio()	// studio
					)
			);
		edges.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Apex edge top",	// description
						apexEdgeTop,	// centre
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,	// parent
						getStudio()	// studio
					)
			);
		edges.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Leg surface 1, vertex 3",	// description
						legSurface1Vertex3,	// centre
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,	// parent
						getStudio()	// studio
					)
			);
		edges.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Leg surface 2, vertex 3",	// description
						legSurface2Vertex3,	// centre
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,	// parent
						getStudio()	// studio
					)
			);
		
		// the edges
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Apex edge",	// description
						apexEdgeBottom,	// start point
						apexEdgeTop,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Leg surface 1, apex edge top to vertex 3",	// description
						apexEdgeTop,	// start point
						legSurface1Vertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Leg surface 1, apex edge bottom to vertex 3",	// description
						apexEdgeBottom,	// start point
						legSurface1Vertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Leg surface 2, apex edge top to vertex 3",	// description
						apexEdgeTop,	// start point
						legSurface2Vertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,
						getStudio()
					)
			);
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Leg surface 2, apex edge bottom to vertex 3",	// description
						apexEdgeBottom,	// start point
						legSurface2Vertex3,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,	// surfaceProperty
						edges,
						getStudio()
					)
			);

		// add the leg surfaces
		EditableParametrisedTriangle legSurface1 = createTriangleWithCorrectNormalDirection(
				"Leg surface 1",	// description
				apexEdgeBottom,	// vertex1
				apexEdgeTop,	// vertex2
				legSurface1Vertex3	// vertex3
			);
		EditableParametrisedTriangle legSurface2 = createTriangleWithCorrectNormalDirection(
				"Leg surface 2",	// description
				apexEdgeBottom,	// vertex1
				apexEdgeTop,	// vertex2
				legSurface2Vertex3	// vertex3
			);
		
//		addSceneObject(
//				new EditableArrow(
//						"Leg surface 1 outwards normal",	// description
//						legSurface1Vertex3,	// startPoint
//						Vector3D.sum(legSurface1Vertex3, legSurface1.getNormalisedOutwardsSurfaceNormal(legSurface1Vertex3)),	// endPoint,
//						0.01,	// shaftRadius
//						0.04,	// tipLength
//						MyMath.deg2rad(30),	// tipAngle
//						SurfaceColour.GREEN_MATT,	// surfaceProperty,
//						this,	// parent 
//						getStudio()	// studio
//					)
//			);
//
//		addSceneObject(
//				new EditableArrow(
//						"Leg surface 2 outwards normal",	// description
//						legSurface2Vertex3,	// startPoint
//						Vector3D.sum(legSurface2Vertex3, legSurface2.getNormalisedOutwardsSurfaceNormal(legSurface2Vertex3)),	// endPoint,
//						0.01,	// shaftRadius
//						0.04,	// tipLength
//						MyMath.deg2rad(30),	// tipAngle
//						SurfaceColour.BLUE_SHINY,	// surfaceProperty,
//						this,	// parent 
//						getStudio()	// studio
//					)
//			);


		switch(gluingType)
		{
		case NEGATIVE_SPACE_WEDGES:
		case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
		case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
			SceneObjectPrimitiveIntersection legSurfaceAndMirrors1 = new SceneObjectPrimitiveIntersection(
					"Leg surface 1 & corresponding mirrors",	// description,
					this,	// parent
					getStudio()	// studio
				);
			SceneObjectPrimitiveIntersection legSurfaceAndMirrors2 = new SceneObjectPrimitiveIntersection(
					"Leg surface 2 & corresponding mirrors",	// description,
					this,	// parent
					getStudio()	// studio
				);

			// leg surface 1 is the one that is a +n/-n interface
			legSurface1.setSurfaceProperty(
					colourNegativeRefractingSurfaces?new ColourFilter():	// TODO for debugging
					new RayRotating(
						Math.PI,	// rotation angle
						surfaceTransmissionCoefficient,	// transmissionCoefficient
						false	// shadowThrowing
					)
				);
			legSurfaceAndMirrors1.addNegativeSceneObjectPrimitive(legSurface1);
			
			// leg surface 2 does not refract; it is used only to cut the mirrors to size
			legSurface2.setSurfaceProperty(new ColourFilter());	// this shoudn't show up
			legSurfaceAndMirrors2.addCutSceneObjectPrimitive(legSurface2);	// .addNegativeSceneObjectPrimitive(legSurface2);
			
			// the central n=+1/n=-1 surface
			EditableParametrisedPlane bisectorSurface = new EditableParametrisedPlane(
					"n=+1/n=-1 surface in bisector plane",	// description
					apexEdgeCentre,	// pointOnPlane
					c,	// normal
					colourNegativeRefractingSurfaces?new ColourFilter():	// TODO for debugging
					// SurfaceColour.getRandom(),
					new RayRotating(
							Math.PI,	// rotation angle
							surfaceTransmissionCoefficient,	// transmissionCoefficient
							false	// shadowThrowing
					),	// surface property
					surfaces,	// parent; I am planning to add this object to both leg-surface-and-mirrors collections... not sure if this will cause trouble?
					getStudio()	// studio
				);

			legSurfaceAndMirrors1.addNegativeSceneObjectPrimitive(bisectorSurface);
			legSurfaceAndMirrors2.addPositiveSceneObjectPrimitive(bisectorSurface);

			// the mirrored cones
						
			legSurfaceAndMirrors1.addPositiveSceneObjectPrimitive(
			// addSceneObject(	// TODO
					getConeForEdge(
							"Conical mirror for top side edge of leg surface 1",	// description
							apexEdgeTop,	// edgeVertexOnApexEdge,
							legSurface1Vertex3	// otherEdgeVertex	// otherEdgeVertex
						));

			legSurfaceAndMirrors1.addPositiveSceneObjectPrimitive(
			// addSceneObject(	// TODO
					getConeForEdge(
							"Conical mirror for bottom side edge of leg surface 1",	// description
							apexEdgeBottom,	// edgeVertexOnApexEdge,
							legSurface1Vertex3	// otherEdgeVertex	// otherEdgeVertex
						));

			legSurfaceAndMirrors2.addPositiveSceneObjectPrimitive(
			// addSceneObject(	// TODO
					getConeForEdge(
							"Conical mirror for top side edge of leg surface 2",	// description
							apexEdgeTop,	// edgeVertexOnApexEdge,
							legSurface2Vertex3	// otherEdgeVertex	// otherEdgeVertex
						));

			legSurfaceAndMirrors2.addPositiveSceneObjectPrimitive(
			// addSceneObject(	// TODO
					getConeForEdge(
							"Conical mirror for bottom side edge of leg surface 2",	// description
							apexEdgeBottom,	// edgeVertexOnApexEdge,
							legSurface2Vertex3	// otherEdgeVertex	// otherEdgeVertex
						));

			// ... and add them to the surfaces
			// surfaces.addSceneObject(legSurfaceAndMirrors1);	// TODO
			surfaces.addSceneObject(legSurfaceAndMirrors2);
			
			break;
		case PERFECT:
		default:
			// make the leg surfaces teleport to each other...
			legSurface1.setSurfaceProperty(
					new Teleporting(
							legSurface2,	// destinationObject
							surfaceTransmissionCoefficient,	// teleportationCoefficient
							TeleportationType.PERFECT,	// teleportationType
							false	// shadowThrowing
						)
				);

			legSurface2.setSurfaceProperty(
					new Teleporting(
							legSurface1,	// destinationObject
							surfaceTransmissionCoefficient,	// teleportationCoefficient
							TeleportationType.PERFECT,	// teleportationType
							false	// shadowThrowing
						)
				);

			// ... and add them to the surfaces
			surfaces.addSceneObject(legSurface1);
			surfaces.addSceneObject(legSurface2);
		}
	}

	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection object for the refracting surfaces and the mirrors...
		surfaces = new EditableSceneObjectCollection("Surfaces", true, this, getStudio());
		
		// ... and the edges
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// populate these collections
		calculateCoordinateAxisDirections();
		populateCollections();
		
		// add the surfaces and the edges to this collection
		addSceneObject(surfaces, showSurfaces);
		addSceneObject(edges, showEdges);
		
		//  for debugging
		// System.out.println("EditableMirroredSpaceCancellingWedge::populateSceneObjectCollection: b="+b);
		
		// TODO for debugging
		if(showBisector)
		addSceneObject(
				new EditableArrow(
						"Bisector",	// description
						apexEdgeCentre,	// startPoint
						Vector3D.sum(apexEdgeCentre, b),	// endPoint,
						0.01,	// shaftRadius
						0.04,	// tipLength
						MyMath.deg2rad(30),	// tipAngle
						SurfaceColour.YELLOW_MATT,	// surfaceProperty,
						this,	// parent 
						getStudio()	// studio
					)
			);
	}

	
	// GUI stuff
	
	// GUI panels
	private LabelledDoublePanel wedgeAngleDegPanel;
	private JComboBox<GluingType> gluingTypeComboBox;	
	private LabelledVector3DPanel centrePanel;
	private LabelledVector3DPanel commonEdgeDirectionPanel;
	private LabelledVector3DPanel bisectorDirectionPanel;
	private LabelledDoublePanel legLengthPanel;
	private LabelledDoublePanel heightPanel;
	private JCheckBox showSurfacesCheckBox;
	private LabelledDoublePanel sheetTransmissionCoefficientPanel;
	private JCheckBox showEdgesCheckBox;
	private LabelledDoublePanel edgeRadiusPanel;
	private SurfacePropertyPanel edgeSurfacePropertyPanel;
	
	private JButton convertButton;
	
	
		

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Null-space wedge"));
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
		
		wedgeAngleDegPanel = new LabelledDoublePanel("Wedge angle (vertex angle, in degrees");
		basicParametersPanel.add(wedgeAngleDegPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre of apex edge");
		basicParametersPanel.add(centrePanel, "wrap");

		commonEdgeDirectionPanel = new LabelledVector3DPanel("Direction of apex edge");
		basicParametersPanel.add(commonEdgeDirectionPanel, "wrap");

		heightPanel = new LabelledDoublePanel("Wedge height (length of apex edge)");
		basicParametersPanel.add(heightPanel, "wrap");
	
		bisectorDirectionPanel = new LabelledVector3DPanel("Direction of the wedge bisector (i.e. direction from apex to centre of base)");
		basicParametersPanel.add(bisectorDirectionPanel, "wrap");

		legLengthPanel = new LabelledDoublePanel("Leg length of the wedge");
		basicParametersPanel.add(legLengthPanel, "wrap");

		showSurfacesCheckBox = new JCheckBox("Show surfaces (refracting surfaces & mirrors)");
		basicParametersPanel.add(showSurfacesCheckBox, "wrap");
		
		sheetTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each refracting surface");
		basicParametersPanel.add(sheetTransmissionCoefficientPanel, "wrap");
		
		editPanel.add(basicParametersPanel, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);
		
		
		//
		// the wedge-type panel
		// 
		
		JPanel wedgeTypePanel = new JPanel();
		wedgeTypePanel.setLayout(new MigLayout("insets 0"));

		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		gluingTypeComboBox.addActionListener(this);
		wedgeTypePanel.add(GUIBitsAndBobs.makeRow("Type", gluingTypeComboBox), "wrap");
		
		tabbedPane.addTab("Wedge type", wedgeTypePanel);
		
		

		//
		// the edges panel
		// 
		
		JPanel edgesPanel = new JPanel();
		edgesPanel.setLayout(new MigLayout("insets 0"));

		showEdgesCheckBox = new JCheckBox("Show edges of refracting surfaces");
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
		wedgeAngleDegPanel.setNumber(MyMath.rad2deg(apexAngle));
		centrePanel.setVector3D(apexEdgeCentre);
		commonEdgeDirectionPanel.setVector3D(apexEdgeDirection);
		bisectorDirectionPanel.setVector3D(bisectorDirection);
		legLengthPanel.setNumber(legLength);
		heightPanel.setNumber(apexEdgeLength);
		showSurfacesCheckBox.setSelected(showSurfaces);
		sheetTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
		gluingTypeComboBox.setSelectedItem(getGluingType());
		enableOrDisableAdditionalWedgeTypeControlPanels();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableMirroredSpaceCancellingWedge acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setApexAngle(MyMath.deg2rad(wedgeAngleDegPanel.getNumber()));
		setApexEdgeCentre(centrePanel.getVector3D());
		setApexEdgeDirection(commonEdgeDirectionPanel.getVector3D());
		setBisectorDirection(bisectorDirectionPanel.getVector3D());
		setLegLength(legLengthPanel.getNumber());
		setApexEdgeLength(heightPanel.getNumber());
		setShowSurfaces(showSurfacesCheckBox.isSelected());
		setSurfaceTransmissionCoefficient(sheetTransmissionCoefficientPanel.getNumber());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
		setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));

		// add the objects
		populateSceneObjectCollection();
		
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
		case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
		case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
			break;
		case PERFECT:
		default:
		}
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
		else if(e.getSource() == gluingTypeComboBox)
		{
			setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));
			
			enableOrDisableAdditionalWedgeTypeControlPanels();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				// setEdgeSurfaceProperty((SurfaceProperty)edited);
				// edgeSurfacePropertyPanel.setSurfaceProperty(getEdgeSurfaceProperty());
				edgeSurfacePropertyPanel.setSurfaceProperty((SurfaceProperty)edited);
			}
	}
}