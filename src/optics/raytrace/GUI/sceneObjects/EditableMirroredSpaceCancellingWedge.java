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
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
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
	
	/**
	 * Divide into several null-space wedges
	 */
	private int numberOfNegativeSpaceWedges;
	
	// for debugging
	private boolean showBisector = false;
//	private boolean colourMirrorSurfaces =  true;
//	private boolean colourNegativeRefractingSurfaces = true;
	private SurfaceProperty getMirrorSurfaceProperty()
	{
			// return new ColourFilter();	// either colour the mirror surfaces...
			// return Transparent.PERFECT;
			return new Reflective(surfaceTransmissionCoefficient, false);	// ... or make it a mirror
	}
	private SurfaceProperty getNegativeRefractingSurfaceProperty()
	{
			// return new ColourFilter();	// either colour the mirror surfaces...
			return new RayRotating(
					Math.PI,	// rotation angle
					surfaceTransmissionCoefficient,	// transmissionCoefficient
					false	// shadowThrowing
					);	// ... or make it 180° ray rotator, which is the same as an interface where  the refractive index changes sign
	}
	

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
			int numberOfNegativeSpaceWedges,
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
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		
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
				1,	// numberOfNegativeSpaceWedges
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
			original.getNumberOfNegativeSpaceWedges(),
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

	public int getNumberOfNegativeSpaceWedges() {
		return numberOfNegativeSpaceWedges;
	}
	
	public void setNumberOfNegativeSpaceWedges(int numberOfNegativeSpaceWedges) {
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
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
	
	
//	/**
//	 * @param description
//	 * @param vertex1
//	 * @param vertex2
//	 * @param vertex3
//	 * @return	a triangle that represents one of the "leg surfaces", oriented such that the bisector direction, b, points inwards
//	 */
//	private EditableParametrisedTriangle createTriangleWithCorrectNormalDirection1(String description, Vector3D vertex1, Vector3D vertex2, Vector3D vertex3)
//	{
//		EditableParametrisedTriangle t = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
//					description,
//					vertex1,
//					vertex2,
//					vertex3,
//					null,	// surfaceProperty -- set later
//					null,	// parent -- set later
//					getStudio()
//				);
//		
//		// does b point inwards?
//		if(Vector3D.scalarProduct(
//				t.getNormalisedOutwardsSurfaceNormal(vertex1),
//				b
//				) > 0)
//		{
//			// no, b points outwards; create a triangle with the reverse order of the vertices
//			t = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
//					description,
//					vertex3,
//					vertex2,
//					vertex1,
//					null,	// surfaceProperty -- set later
//					null,	// parent -- set later
//					getStudio()
//					);
//			// now b should point inwards
////
////			// does b point inwards?
////			if(Vector3D.scalarProduct(
////					t.getNormalisedOutwardsSurfaceNormal(vertex1),
////					b
////					) > 0)
////			{
////				// b still points outwards; panic!
////				new RayTraceException("EditableMirroredSpaceCancellingWedge::createTriangleWithCorrectNormalDirection: Can't create triangle with correct normal direction").printStackTrace();
////				System.exit(-1);
////			}
//		}
//
//		return t;
//	}
	
	/**
	 * @param description
	 * @param pointOnPlane
	 * @param normal
	 * @return	a plane that represents one of the mirror planes, oriented such that the apex-edge centre lies on the "inside" side of the plane
	 */
	private EditableParametrisedPlane createMirrorPlaneWithCorrectNormalDirection(String description, Vector3D pointOnPlane, Vector3D normal)
	{
		EditableParametrisedPlane p = new EditableParametrisedPlane(
			description,
			pointOnPlane,
			normal,
			getMirrorSurfaceProperty(),	// surfaceProperty
			null,	// parent -- set later
			getStudio()	// studio
		);
		
		// does the apex-edge centre lie inside the plane?
		if(p.insideObject(apexEdgeCentre))
		{
			// yes, the apex-edge centre lies inside the plane
			return p;
		}
		else
		{
			// no, the apex-edge centre lies outside the plane;  return a plane with the opposite normal direction
			return new EditableParametrisedPlane(
					description,
					pointOnPlane,
					normal.getReverse(),
					getMirrorSurfaceProperty(),	// surfaceProperty
					null,	// parent -- set later
					getStudio()	// studio
				);
		}
	}


	/**
	 * 
	 */
	/**
	 * 
	 */
	private void populateCollections()
	{
		// reminder of coordinate  system:
		// a = unit vector in the direction of the apex edge
		// b = unit vector in the direction of the wedge bisector
		// c = unit vector perpendicular to both a and b, pointing into the same half space as leg surface 1

		// first, calculate the vertices of the leg surfaces, which are...
		// ... the ends of the apex edge...
		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength));
		Vector3D apexEdgeTop    = Vector3D.sum(apexEdgeCentre, a.getProductWith(+0.5*apexEdgeLength));

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
		// and add the apex edge
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

		for(int w=0; w<numberOfNegativeSpaceWedges; w++)
		{
			// calculate a coordinate  system for this particular wedge:
			// a = unit vector in the direction of the apex edge (as before)
			// bw = unit vector in the direction of the wedge bisector
			// cw = unit vector perpendicular to both a and b, pointing into the same half space as leg surface 1

			double apexAngleW = apexAngle/numberOfNegativeSpaceWedges;
			Vector3D bw = Vector3D.sum(
					b.getProductWith(Math.cos(-0.5*apexAngle+(w+0.5)*apexAngleW)),
					c.getProductWith(Math.sin(-0.5*apexAngle+(w+0.5)*apexAngleW))
					);
			Vector3D cw = Vector3D.crossProduct(bw, a);
			
			String nameW = (numberOfNegativeSpaceWedges>0)?"Sub-wedge "+w+", ":"";

			// ... and the third vertex of both leg surfaces
			Vector3D legSurface1Vertex3 = Vector3D.sum(
					apexEdgeCentre,
					bw.getProductWith(legLength*Math.cos( 0.5*apexAngleW)),
					cw.getProductWith(legLength*Math.sin( 0.5*apexAngleW))
					);
			Vector3D legSurface2Vertex3 = Vector3D.sum(
					apexEdgeCentre,
					bw.getProductWith(legLength*Math.cos(-0.5*apexAngleW)),
					cw.getProductWith(legLength*Math.sin(-0.5*apexAngleW))
					);

			edges.addSceneObject(
					new EditableScaledParametrisedSphere(
							nameW+"Leg surface 1, vertex 3",	// description
							legSurface1Vertex3,	// centre
							edgeRadius,	// radius
							edgeSurfaceProperty,	// surfaceProperty
							edges,	// parent
							getStudio()	// studio
							)
					);
			edges.addSceneObject(
					new EditableScaledParametrisedSphere(
							nameW+"Leg surface 2, vertex 3",	// description
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
							nameW+"Leg surface 1, apex edge top to vertex 3",	// description
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
							nameW+"Leg surface 1, apex edge bottom to vertex 3",	// description
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
							nameW+"Leg surface 2, apex edge top to vertex 3",	// description
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
							nameW+"Leg surface 2, apex edge bottom to vertex 3",	// description
							apexEdgeBottom,	// start point
							legSurface2Vertex3,	// end point
							edgeRadius,	// radius
							edgeSurfaceProperty,	// surfaceProperty
							edges,
							getStudio()
							)
					);

			EditableParametrisedTriangle legSurface1, legSurface2;

			switch(gluingType)
			{
			case NEGATIVE_SPACE_WEDGES:
				// add the leg surfaces
				surfaces.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						nameW+"Leg surface 1 (180° ray  rotating)",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						legSurface1Vertex3,	// vertex3
						//					Vector3D.sum(apexEdgeCentre, b.getReverse()),	// outsidePosition
						//					a,	// uUnitVector
						//					Vector3D.difference(legSurface1Vertex3, apexEdgeBottom),	// vUnitVector
						getNegativeRefractingSurfaceProperty(),	// surfaceProperty
						surfaces,	// parent
						getStudio()	// studio
						));

				Vector3D bisectorVertex3 = Vector3D.sum(
						apexEdgeCentre,
						bw.getProductWith(legLength)
						);

				surfaces.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						nameW+"Bisector surface",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						bisectorVertex3,	// vertex3
						// Vector3D.sum(apexEdgeCentre, b.getReverse()),	// outsidePosition
						// a,	// uUnitVector
						// Vector3D.difference(legSurface2Vertex3, apexEdgeBottom),	// vUnitVector
						getNegativeRefractingSurfaceProperty(),	// surfaceProperty
						surfaces,	// parent
						getStudio()	// studio
						));

				break;
			case NEGATIVE_SPACE_WEDGES_SYMMETRIC:
			case NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS:
				// suitable for wedges with deficit angles <180°
				// see Johannes's lab book 10/9/19

				SceneObjectPrimitiveIntersection legSurface1AndMirrors = new SceneObjectPrimitiveIntersection(
						nameW+"Leg surface 1 & associated mirrors",	// description,
						this,	// parent
						getStudio()	// studio
						);
				SceneObjectPrimitiveIntersection legSurface2AndMirrors = new SceneObjectPrimitiveIntersection(
						nameW+"Leg surface 2 & associated mirrors",	// description,
						this,	// parent
						getStudio()	// studio
						);

				// add the leg surfaces
				legSurface1 = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						"Leg surface 1 (180° ray  rotating)",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						legSurface1Vertex3,	// vertex3
						Vector3D.sum(apexEdgeCentre, bw.getReverse()),	// outsidePosition
						a,	// uUnitVector
						Vector3D.difference(legSurface1Vertex3, apexEdgeBottom),	// vUnitVector
						getNegativeRefractingSurfaceProperty(),	// surfaceProperty
						legSurface1AndMirrors,	// parent
						getStudio()	// studio
						);
				legSurface1AndMirrors.addPositiveSceneObjectPrimitive(legSurface1);	// I  don't  understand this; it should really be added as a positive scene-object  primitive,  but that doesn't work

				legSurface2 = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						"Leg surface 2",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						legSurface2Vertex3,	// vertex3
						Vector3D.sum(apexEdgeCentre, bw.getReverse()),	// outsidePosition
						a,	// uUnitVector
						Vector3D.difference(legSurface2Vertex3, apexEdgeBottom),	// vUnitVector
						SurfaceColour.YELLOW_SHINY,	// surfaceProperty; this surface is added as an invisible scene-object primitive, so the surface property shouldn't matter
						legSurface1AndMirrors,	// parent
						getStudio()	// studio
						);			
				// leg surface 2 does not refract; it is used only to cut the mirrors to size
				legSurface2AndMirrors.addInvisiblePositiveSceneObjectPrimitive(legSurface2);	// I  don't understand this;it should really be added as an invisible positive scene-object  primitive,  but that doesn't work

				// the central n=+1/n=-1 surface
				EditableParametrisedPlane bisectorSurface = new EditableParametrisedPlane(
						"n=+1/n=-1 surface in bisector plane",	// description
						apexEdgeCentre,	// pointOnPlane
						cw.getReverse(),	// normal
						getNegativeRefractingSurfaceProperty(),	// surface property
						surfaces,	// parent; I am planning to add this object to both leg-surface-and-mirrors collections... not sure if this will cause trouble?
						getStudio()	// studio
						);

				legSurface1AndMirrors.addPositiveSceneObjectPrimitive(bisectorSurface);

				EditableParametrisedPlane bisectorSurfaceTwin = new EditableParametrisedPlane(bisectorSurface);
				bisectorSurfaceTwin.setNormal(cw);
				legSurface2AndMirrors.addInvisiblePositiveSceneObjectPrimitive(bisectorSurfaceTwin);

				// the mirror planes

				legSurface1AndMirrors.addPositiveSceneObjectPrimitive(
						createMirrorPlaneWithCorrectNormalDirection(
								"Leg surface 1, mirror 1",	// description
								legSurface1Vertex3,	// pointOnPlane
								a.getPartPerpendicularTo(Vector3D.difference(legSurface1Vertex3, apexEdgeBottom))	// normal
								)
						);

				legSurface1AndMirrors.addPositiveSceneObjectPrimitive(
						createMirrorPlaneWithCorrectNormalDirection(
								"Leg surface 1, mirror 2",	// description
								legSurface1Vertex3,	// pointOnPlane
								a.getPartPerpendicularTo(Vector3D.difference(legSurface1Vertex3, apexEdgeTop))	// normal
								)
						);

				legSurface2AndMirrors.addPositiveSceneObjectPrimitive(
						createMirrorPlaneWithCorrectNormalDirection(
								"Leg surface 2, mirror 1",	// description
								legSurface2Vertex3,	// pointOnPlane
								a.getPartPerpendicularTo(Vector3D.difference(legSurface2Vertex3, apexEdgeBottom))	// normal
								)
						);

				legSurface2AndMirrors.addPositiveSceneObjectPrimitive(
						createMirrorPlaneWithCorrectNormalDirection(
								"Leg surface 2, mirror 2",	// description
								legSurface2Vertex3,	// pointOnPlane
								a.getPartPerpendicularTo(Vector3D.difference(legSurface2Vertex3, apexEdgeTop))	// normal
								)
						);

				// ... and add them to the surfaces
				surfaces.addSceneObject(legSurface1AndMirrors);
				surfaces.addSceneObject(legSurface2AndMirrors);

				break;
			case PERFECT:
			default:
				legSurface1 = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						nameW+"Leg surface 1 (teleporting to Leg surface 2)",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						legSurface1Vertex3,	// vertex3
						null,	// surfaceProperty -- set in a minute
						surfaces,	// parent
						getStudio()	// studio
						);
				legSurface2 = EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
						nameW+"Leg surface 2 (teleporting to Leg surface 1)",	// description
						apexEdgeBottom,	// vertex1
						apexEdgeTop,	// vertex2
						legSurface2Vertex3,	// vertex3
						null,	// surfaceProperty -- set in a minute
						surfaces,	// parent
						getStudio()	// studio
						);			

				// make the leg surfaces teleport to each other...
				legSurface1.setSurfaceProperty(
						// SurfaceColour.BLUE_MATT
						new Teleporting(
								legSurface2,	// destinationObject
								surfaceTransmissionCoefficient,	// teleportationCoefficient
								TeleportationType.PERFECT,	// teleportationType
								false	// shadowThrowing
								)
						);

				legSurface2.setSurfaceProperty(
						// SurfaceColour.GREEN_MATT
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