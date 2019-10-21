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
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;

/**
 * A wedge, more precisely an extruded isosceles triangle (i.e. an isosceles triangle with thickness) that looks like it isn't there.
 * The wedge edge, of direction <i>edgeDirection</i>, is centred on <i>centre</i>.
 *  <i>N</i> negative-space wedges, each of angle &nu;/(2<i>N</i>), that cancel out another <i>N</i> positive-space wedges
 * of angle &nu;/(2<i>N</i>), such that an entire wedge of angle &nu; is cancelled out.
 * Each negative-space wedge consists of 2 negative-refraction sheets (actually 180° ray-rotation sheets).
 * 
 * @author Johannes Courtial, Dimitris Georgantzis
 */
public class EditableSpaceCancellingWedge extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 487436997322260356L;

	//
	// parameters
	//
	
	/**
	 * Apex angle of the space-cancelling wedge, &nu;, in radians
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
	 * Direction of the space-cancelling-wedge bisector, i.e. the line through the centre of the wedge that is perpendicular to the edge direction.
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
	private boolean showRefractingSurfaces;
	
	/**
	 * Angle of the containment mirrors with the sides of the wedge, in radians.
	 * Sensible values range from pi/2 to pi-apexAngle/2.
	 */
	private double containmentMirrorsAngleWithSides;
		
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
	 * Space-cancelling-wedge type, which describes the way this space-cancelling wedge is realised
	 */
	private GluingType gluingType;
	
	/**
	 * Number of negative-space wedges, <i>N</i>, in the space-cancelling wedge
	 */
	private int numberOfNegativeSpaceWedges;
	


	//
	// constructors
	//
	
	/**
	 * @param description
	 * @param apexAngle
	 * @param apexEdgeCentre
	 * @param apexEdgeDirection
	 * @param bisectorDirection
	 * @param legLength
	 * @param apexEdgeLength
	 * @param showRefractingSurfaces
	 * @param containmentMirrorsAngleWithSides
	 * @param showContainmentMirrors
	 * @param surfaceTransmissionCoefficient
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param gluingType
	 * @param numberOfNegativeSpaceWedges
	 * @param parent
	 * @param studio
	 */
	public EditableSpaceCancellingWedge(
			String description,
			double apexAngle,
			Vector3D apexEdgeCentre,
			Vector3D apexEdgeDirection,
			Vector3D bisectorDirection,
			double legLength,
			double apexEdgeLength,
			boolean showRefractingSurfaces,
			double containmentMirrorsAngleWithSides,
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
		this.showRefractingSurfaces = showRefractingSurfaces;
		this.containmentMirrorsAngleWithSides = containmentMirrorsAngleWithSides;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.showEdges = showEdges;
		this.edgeRadius = edgeRadius;
		this.edgeSurfaceProperty = edgeSurfaceProperty;
		this.gluingType = gluingType;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;

		populateSceneObjectCollection();
	}

	/**
	 * Create a default space-cancelling wedge
	 * @param parent
	 * @param studio
	 */
	public EditableSpaceCancellingWedge(SceneObject parent, Studio studio)
	{
		this(
				"Space-cancelling wedge",	// description
				MyMath.deg2rad(90),	// apexAngle
				new Vector3D(0, 0, 10),	// apexEdgeCentre
				new Vector3D(0, 1, 0),	// apexEdgeDirection
				new Vector3D(1, 0, 0),	// bisectorDirection
				1,	// legLength
				1,	// apexEdgeLength
				true,	// showRefractingSurfaces
				MyMath.deg2rad(91),	// containmentMirrorsAngleWithSides
				0.96,	// surfaceTransmissionCoefficient
				false,	// showEdges
				0.01,	// edgeRadius
				SurfaceColour.RED_SHINY,	// edgeSurfaceProperty
				GluingType.SPACE_CANCELLING_WEDGES,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableSpaceCancellingWedge(EditableSpaceCancellingWedge original)
	{
		this(
			original.getDescription(),
			original.getApexAngle(),
			original.getApexEdgeCentre(),
			original.getApexEdgeDirection(),
			original.getBisectorDirection(),
			original.getLegLength(),
			original.getApexEdgeLength(),
			original.isShowRefractingSurfaces(),
			original.getContainmentMirrorsAngleWithSides(),
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
	public EditableSpaceCancellingWedge clone()
	{
		return new EditableSpaceCancellingWedge(this);
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

	public boolean isShowRefractingSurfaces() {
		return showRefractingSurfaces;
	}

	public void setShowRefractingSurfaces(boolean showRefractingSurfaces) {
		this.showRefractingSurfaces = showRefractingSurfaces;
	}

	public double getContainmentMirrorsAngleWithSides() {
		return containmentMirrorsAngleWithSides;
	}

	public void setContainmentMirrorsAngleWithSides(double containmentMirrorsAngleWithSides) {
		this.containmentMirrorsAngleWithSides = containmentMirrorsAngleWithSides;
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
	
	// containers for the refracting surfaces and edges
	private EditableSceneObjectCollection surfaces, edges;
	



	// define coordinate system of star
	private Vector3D a;	// unit vector in the direction of the apex edge
	private Vector3D b;	// unit vector in the direction of the wedge bisector
	private Vector3D c;	// unit vector perpendicular to both a and b
	
	public void calculateCoordinateAxisDirections()
	{
		// define coordinate system of the wedge
		a = apexEdgeDirection.getNormalised();
		b = bisectorDirection.getPartPerpendicularTo(a).getNormalised();
		c = Vector3D.crossProduct(b, a);
	}
	
	/**
	 * Add one of the surfaces that form the "leg surfaces" of the isosceles negative-space wedges
	 * @param description
	 * @param angleWithBisector
	 */
	private void addNegativeSpaceWedgeLegSurface(
			String description,
			double angleWithBisector,
			boolean longLeg
		)
	{
		double h = legLength*Math.sin(containmentMirrorsAngleWithSides)/Math.sin(Math.PI-(apexAngle / (2*numberOfNegativeSpaceWedges))-containmentMirrorsAngleWithSides);

		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength));
		Vector3D apexEdgeTop = Vector3D.sum(apexEdgeCentre, a.getProductWith(+0.5*apexEdgeLength));
		// span vector in the a direction
		Vector3D aSpanVector = a.getProductWith(apexEdgeLength);
		// span vector in the (b,c) plane
		Vector3D bcSpanVector = Vector3D.sum(
				b.getProductWith(Math.cos(angleWithBisector)),
				c.getProductWith(Math.sin(angleWithBisector))
			).getProductWith(longLeg?h:legLength);

		surfaces.addSceneObject(new EditableScaledParametrisedParallelogram(
				description,
				apexEdgeBottom,	// corner 
				aSpanVector,	// spanVector1
				bcSpanVector,	// spanVector2
				new RayRotating(
						Math.PI,	// rotation angle
						surfaceTransmissionCoefficient,	// transmissionCoefficient
						false	// shadowThrowing
				),	// surfaceProperty
				surfaces,	// parent
				getStudio()
			));

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of "+description,
						apexEdgeTop,	// start point
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Outer edge of "+description,
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Bottom edge of "+description,
						apexEdgeBottom,	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
	}
	
	private void addNegativeSpaceWedgeBaseSurface(
			String description,
			double angleWithBisector1,
			double angleWithBisector2
		)
	{
		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength));
		// span vector in the a direction
		Vector3D aSpanVector = a.getProductWith(apexEdgeLength);
		Vector3D bottomCorner1 = Vector3D.sum(
				apexEdgeBottom,
				b.getProductWith(legLength*Math.cos(angleWithBisector1)),
				c.getProductWith(legLength*Math.sin(angleWithBisector1))
			);
		Vector3D bottomCorner2 = Vector3D.sum(
				apexEdgeBottom,
				b.getProductWith(legLength*Math.cos(angleWithBisector2)),
				c.getProductWith(legLength*Math.sin(angleWithBisector2))
			);

		surfaces.addSceneObject(new EditableScaledParametrisedParallelogram(
				description,
				bottomCorner1,	// corner 
				aSpanVector,	// spanVector1
				Vector3D.difference(bottomCorner2, bottomCorner1),	// spanVector2
				SurfaceColour.BLACK_MATT,	// surfaceProperty
				surfaces,	// parent
				getStudio()
			));
		
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of "+description,
						Vector3D.sum(bottomCorner1, aSpanVector),	// start point
						Vector3D.sum(bottomCorner2, aSpanVector),	// end point
						edgeRadius,	// radius
						SurfaceColour.BLACK_MATT,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Bottom edge of "+description,
						bottomCorner1,	// start point
						bottomCorner2,	// end point
						edgeRadius,	// radius
						SurfaceColour.BLACK_MATT,
						edges,
						getStudio()
					)
			);

	}

	private void addTeleportingSurfaces()
	{
		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength));
		Vector3D apexEdgeTop = Vector3D.sum(apexEdgeCentre, a.getProductWith(+0.5*apexEdgeLength));
		// span vector in the a direction
		Vector3D aSpanVector = a.getProductWith(apexEdgeLength);
		Vector3D bcSpanVector = Vector3D.sum(
				b.getProductWith(Math.cos(-apexAngle/2)),
				c.getProductWith(Math.sin(-apexAngle/2))
			).getProductWith(legLength);
		
		EditableScaledParametrisedParallelogram teleportingSurface1 = new EditableScaledParametrisedParallelogram(
				"First teleporting surface",
				apexEdgeBottom,	// corner 
				aSpanVector,	// spanVector1
				bcSpanVector,	// spanVector2
				null,	// surfaceProperty
				surfaces,	// parent
				getStudio()
			);
		
		// add edges
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of first teleporting surface",
						apexEdgeTop,	// start point
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Outer edge of first teleporting surface",
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Bottom edge of first teleporting surface",
						apexEdgeBottom,	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		bcSpanVector = Vector3D.sum(
				b.getProductWith(Math.cos(+apexAngle/2)),
				c.getProductWith(Math.sin(+apexAngle/2))
			).getProductWith(legLength);
		
		EditableScaledParametrisedParallelogram teleportingSurface2 = new EditableScaledParametrisedParallelogram(
				"Second teleporting surface",
				apexEdgeBottom,	// corner 
				aSpanVector,	// spanVector1
				bcSpanVector,	// spanVector2
				null,	// surfaceProperty
				surfaces,	// parent
				getStudio()
			);
		
		// add edges
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of second teleporting surface",
						apexEdgeTop,	// start point
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Outer edge of second teleporting surface",
						Vector3D.sum(apexEdgeTop, bcSpanVector),	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Bottom edge of second teleporting surface",
						apexEdgeBottom,	// start point
						Vector3D.sum(apexEdgeBottom, bcSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
		
		teleportingSurface1.setSurfaceProperty(
				new Teleporting(
						teleportingSurface2,	// destinationObject
						surfaceTransmissionCoefficient,	// teleportationCoefficient
						TeleportationType.PERFECT,	// teleportationType
						false	// shadowThrowing
					)
			);

		teleportingSurface2.setSurfaceProperty(
				new Teleporting(
						teleportingSurface1,	// destinationObject
						surfaceTransmissionCoefficient,	// teleportationCoefficient
						TeleportationType.PERFECT,	// teleportationType
						false	// shadowThrowing
					)
			);

		surfaces.addSceneObject(teleportingSurface1);
		surfaces.addSceneObject(teleportingSurface2);		
	}
	
	private void addMirrors(
			int i,
			double angleWithBisector,
			double delta
		)
	{
		Vector3D aSpanVector = a.getProductWith(apexEdgeLength);
		Vector3D apexEdgeBottom = Vector3D.sum(apexEdgeCentre, aSpanVector.getProductWith(-0.5));
		Vector3D endOfShortLegBottom1 = Vector3D.sum(
				apexEdgeBottom,
				b.getProductWith(legLength*Math.cos(angleWithBisector-delta)),
				c.getProductWith(legLength*Math.sin(angleWithBisector-delta))
			);
		Vector3D endOfShortLegBottom2 = Vector3D.sum(
				apexEdgeBottom,
				b.getProductWith(legLength*Math.cos(angleWithBisector+delta)),
				c.getProductWith(legLength*Math.sin(angleWithBisector+delta))
			);
		// length of the long leg
		double h = legLength*Math.sin(containmentMirrorsAngleWithSides)/Math.sin(Math.PI-delta-containmentMirrorsAngleWithSides);
		Vector3D endOfLongLegBottom = Vector3D.sum(
				apexEdgeBottom,
				b.getProductWith(h*Math.cos(angleWithBisector)),
				c.getProductWith(h*Math.sin(angleWithBisector))
			);
		
		surfaces.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Mirror at the end of n=-1 wedge #"+i,
				endOfShortLegBottom1,	// corner 
				aSpanVector,	// spanVector1
				Vector3D.difference(endOfLongLegBottom, endOfShortLegBottom1),	// spanVector2
				Reflective.PERFECT_MIRROR,	// surfaceProperty
				surfaces,	// parent
				getStudio()
			));
		surfaces.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Mirror at the end of n=+1 wedge #"+i,
				endOfLongLegBottom,	// corner 
				aSpanVector,	// spanVector1
				Vector3D.difference(endOfShortLegBottom2, endOfLongLegBottom),	// spanVector2
				Reflective.PERFECT_MIRROR,	// surfaceProperty
				surfaces,	// parent
				getStudio()
			));
		
		// add edges
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of mirror at the end of n=-1 wedge #"+i,
						endOfShortLegBottom1,	// start point
						endOfLongLegBottom,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of mirror at the end of n=+1 wedge #"+i,
						endOfLongLegBottom,	// start point
						endOfShortLegBottom2,	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of mirror at the end of n=-1 wedge #"+i,
						Vector3D.sum(endOfShortLegBottom1, aSpanVector),	// start point
						Vector3D.sum(endOfLongLegBottom, aSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of mirror at the end of n=+1 wedge #"+i,
						Vector3D.sum(endOfLongLegBottom, aSpanVector),	// start point
						Vector3D.sum(endOfShortLegBottom2, aSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
	}


	private void populateCollections()
	{
		switch(gluingType)
		{
		case SPACE_CANCELLING_WEDGES:
		case SPACE_CANCELLING_WEDGES_SYMMETRIC:
		case SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS:
			// calculate the angle of each wedge
			// each wedge needs to rotate by <wedgeAngle> / <numberOfNegativeSpaceWedges>, which it does by
			// having a wedge angle that is half of that (in radians)
			double delta = apexAngle / (2*numberOfNegativeSpaceWedges);
			
			// System.out.println("wedge angle = "+MyMath.rad2deg(wedgeAngle)+"°");
			
			// add all the negative-refraction sheets
			for(int i=0; i<2*numberOfNegativeSpaceWedges; i++)
			{	
				// add sheet #i

				// first calculate the angle in the (a, b) plane and with respect to the b axis
				double angleWithBisector =
						(gluingType==GluingType.SPACE_CANCELLING_WEDGES_SYMMETRIC)
						?(-(apexAngle-delta)/2 + i*delta)
						:(-apexAngle/2 + i*delta);

				// in all cases, add the negative-refraction surface
				addNegativeSpaceWedgeLegSurface(
						"Surface #"+i+" (angle with bisector="+MyMath.rad2deg(angleWithBisector)+"°)",	// description
						angleWithBisector,	// azimuthalAngle
						(gluingType==GluingType.SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS) && (i%2 == 1)
					);
			
				if(i%2 == 1)
				{
					if(gluingType==GluingType.SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS)
					{
						// add mirrors
						addMirrors(
								i,
								angleWithBisector,
								delta
							);
					}
					else
					{
						// add a black back to the negative-space wedges
						addNegativeSpaceWedgeBaseSurface(
								"Base surface of negative-space wedge #"+(i-1),
								angleWithBisector - delta,
								angleWithBisector
								);
					}
				}
			}
			break;
		case PERFECT:
		default:
			addTeleportingSurfaces(); 
		}
		
		// add apex edge
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Apex edge",
						Vector3D.sum(apexEdgeCentre, a.getProductWith(-0.5*apexEdgeLength)),	// start point
						Vector3D.sum(apexEdgeCentre, a.getProductWith( 0.5*apexEdgeLength)),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
	}

	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the refracting surfaces...
		surfaces = new EditableSceneObjectCollection("Refracting surfaces", true, this, getStudio());
		
		// ... and the edges
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// populate these collections
		calculateCoordinateAxisDirections();
		populateCollections();
		
		// add the windows and the edges to this collection
		addSceneObject(surfaces, showRefractingSurfaces);
		addSceneObject(edges, showEdges);
	}

	
	// GUI stuff
	
	// GUI panels
	private LabelledDoublePanel wedgeAngleDegPanel;
	private JComboBox<GluingType> gluingTypeComboBox;	
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledVector3DPanel commonEdgeDirectionPanel;
	private LabelledVector3DPanel bisectorDirectionPanel;
	private LabelledDoublePanel legLengthPanel;
	private LabelledDoublePanel heightPanel;
	private JCheckBox showSheetsCheckBox;
	private LabelledDoublePanel containmentMirrorsAngleWithSidesDegPanel;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Space-cancelling wedge"));
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

		showSheetsCheckBox = new JCheckBox("Show refracting surfaces");
		basicParametersPanel.add(showSheetsCheckBox, "wrap");
		
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
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges");
		wedgeTypePanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		containmentMirrorsAngleWithSidesDegPanel = new LabelledDoublePanel("Angle of containment mirrors with sides (in degrees)");
		wedgeTypePanel.add(containmentMirrorsAngleWithSidesDegPanel, "wrap");

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
		showSheetsCheckBox.setSelected(showRefractingSurfaces);
		sheetTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
		gluingTypeComboBox.setSelectedItem(getGluingType());
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		enableOrDisableAdditionalWedgeTypeControlPanels();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSpaceCancellingWedge acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setApexAngle(MyMath.deg2rad(wedgeAngleDegPanel.getNumber()));
		setApexEdgeCentre(centrePanel.getVector3D());
		setApexEdgeDirection(commonEdgeDirectionPanel.getVector3D());
		setBisectorDirection(bisectorDirectionPanel.getVector3D());
		setLegLength(legLengthPanel.getNumber());
		setApexEdgeLength(heightPanel.getNumber());
		setShowRefractingSurfaces(showSheetsCheckBox.isSelected());
		setSurfaceTransmissionCoefficient(sheetTransmissionCoefficientPanel.getNumber());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
		setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));
		setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());

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
		case SPACE_CANCELLING_WEDGES:
		case SPACE_CANCELLING_WEDGES_SYMMETRIC:
		case SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS:
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
			iPanel.replaceFrontComponent(container, "Edit ex-space-cancelling wedge");
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