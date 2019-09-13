package optics.raytrace.surfaces;

import java.util.ArrayList;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector2D;
import math.Vector3D;

/**
 * A teleporting surface that provides a mysterious* link to another scene object.
 * The object with the teleporting surface is called the origin object; the linked
 * scene object is called the destination object.
 * 
 * * The link can be not so mysterious, e.g. holographic [1].
 * [1] O. Bryngdahl, "Geometrical transformations in optics", J. Opt. Soc. Am. 64, 1092-1099 (1974)
 * 
 * A ray hits the surface at coordinates (u, v) in the origin object's
 * surface coordinate system (defined in ParametrisedSurface)
 * from a direction du*<b>u1</b> + dv*<b>v1</b> + dn*<b>n1</b>, where
 * <b>u1</b> and <b>v1</b> are the two surface-coordinate axes (also defined in
 * ParametrisedSurface) and <b>n1</b> is the local surface normal (defined in the
 * SceneObjectPrimitive class) at the intersection point.
 * Then the ray exits the other destination object at its surface coordinates (u, v) and
 * with a direction du*<b>u2</b> + dv*<b>v2</b> + dn*<b>n2</b>,
 * where <b>u2</b>, <b>v2</b>, and <b>n2</b> are the two surface-coordinate axes and surface
 * normal, respectively, at the point on the destination object's surface from where the ray
 * continues.
 * 
 * Note that, if the ray hits the surface from the inside/outside, it leaves the other
 * object in the direction of the outside/inside.
 * 
 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinates(Vector3D p)
 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(Vector3D p)
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class Teleporting extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -4244964788427326376L;
	
	private One2OneParametrisedObject destinationObject;
	
	public enum TeleportationType
	{
		PERFECT("Perfect teleportation"),
		HOLOGRAPHIC("Holographic teleportation");
		
		private String description;
		private TeleportationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private TeleportationType teleportationType;
	
	/**
	 * Creates a new teleporting surface.
	 * @param destinationObject
	 * @param teleportationCoefficient
	 * @param teleportationType
	 */
	public Teleporting(
			One2OneParametrisedObject destinationObject,
			double teleportationCoefficient,
			TeleportationType teleportationType,
			boolean shadowThrowing
		)
	{
		super(teleportationCoefficient, shadowThrowing);
		setDestinationObject(destinationObject);
		setTeleportationType(teleportationType);
	}
	
	/**
	 * Creates a new, perfect, teleporting surface.
	 * @param destinationObject
	 */
	public Teleporting(One2OneParametrisedObject destinationObject)
	{
		this(
				destinationObject,
				PERFECT_TRANSMISSION_COEFFICIENT,
				TeleportationType.PERFECT,
				true	// shadow-throwing
			);
	}

	/**
	 * Clone the original teleporting surface
	 * @param original
	 */
	public Teleporting(Teleporting original)
	{
		this(
				original.getDestinationObject(),
				original.getTransmissionCoefficient(),
				original.getTeleportationType(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Teleporting clone()
	{
		return new Teleporting(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
//		if (i.o==null) return DoubleColour.BLACK;
//		else if (i == RaySceneObjectIntersection.NO_INTERSECTION) return DoubleColour.BLACK;

		// i.o is the object that's been intersected
				
		// calculate the coordinates (u, v) of the intersection point in object 1's
		// local coordinate system (defined in ParametrisedSurface)
		
		// First check that the intersected object is properly parametrised...
		if(!(i.o instanceof ParametrisedObject))
		{
			throw new RayTraceException("Teleporting::getColour: Object with teleporting surface is not of type ParametrisedSurface");
		}
		// ... and then calculate the parameters
		Vector2D uv = ((ParametrisedObject)i.o).getSurfaceCoordinates(i.p);
		// System.out.println("Teleporting::getColour: intersection point "+i.p+" has surface coordinates "+uv);	// TODO
		
		// this is where the commonality between the different types of teleporting surfaces ends;
		// calculate the new starting point, the new ray direction, and the intensity factor according to the different teleportation type
		Vector3D newStartingPoint, newRayDirection;
		double intensityFactor;
		
		Vector3D n1, n2;
		
		switch(teleportationType)
		{
		case HOLOGRAPHIC:
			// Both surfaces are phase holograms with a phase gradient such that light that is normally incident on the
			// first surface gets re-directed such that it intersects the second surface precisely at the corresponding
			// point (i.e. the point with the same local coordinate values).

			// The point where the light ray intersects the first surface is i.p;
			// its local coordinates are given by (uv.x, uv.y).
			// Calculate the corresponding point on the second surface, i.e.			
			// calculate the 3D position that corresponds to the coordinates (u, v)
			// in the other object's local coordinate system.
			Vector3D
				correspondingPoint = getDestinationObject().getPointForSurfaceCoordinates(uv.x, uv.y),
				directionPoint2CorrespondingPoint = Vector3D.difference(correspondingPoint, i.p).getNormalised();
			
			// We also need the surface normals.
			n1 = i.getNormalisedOutwardsSurfaceNormal();
			n2 = getDestinationObject().getNormalisedOutwardsSurfaceNormal(correspondingPoint);

			// Now calculate the light-ray direction behind the first surface.
			Vector3D intermediateRayDirection = PhaseHologram.getOutgoingNormalisedRayDirection(
					r.getD(),	// incidentNormalisedRayDirection
					n1.getProductWith(Vector3D.scalarProduct(directionPoint2CorrespondingPoint, n1) > 0?1.:-1.),	// incidentNormalisedRayDirection1
					directionPoint2CorrespondingPoint,	// outgoingNormalisedRayDirection1,
					n1,	// normalisedOutwardsSurfaceNormal,
					false	// reflecting
				);

			// Now assume that the second surface is the one the light ray re-directed by the first surface would hit,
			// and calculate where it would hit.
			Ray r2 = r.getBranchRay(i.p, intermediateRayDirection, i.t);
			newStartingPoint = ((SceneObject)getDestinationObject()).getClosestRayIntersection(r2).p;
			if(newStartingPoint == null)
			{
				// intermediate ray misses destination object
				// continue tracing the intermediate ray instead
				return scene.getColourAvoidingOrigin(
						r2,
						i.o,	// object to avoid
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
					).multiply(getTransmissionCoefficient());
			}
			else
			{
				if(r2.isRayWithTrajectory())
				{
					((RayWithTrajectory)r2).addIntersectionPoint(newStartingPoint);
				}
			}
			
			// Now calculate the light-ray direction behind the second surface.
			newRayDirection = PhaseHologram.getOutgoingNormalisedRayDirection(
					intermediateRayDirection,	// incidentNormalisedRayDirection
					directionPoint2CorrespondingPoint,	// incidentNormalisedRayDirection1
					n2.getProductWith(Vector3D.scalarProduct(directionPoint2CorrespondingPoint, n2) > 0?1.:-1.),	// outgoingNormalisedRayDirection1,
					n2,	// normalisedOutwardsSurfaceNormal,
					false	// reflecting
				);
			
			break;
		case PERFECT:
		default:
			// default option is perfect teleportation
			// now calculate the direction in the coordinate system defined by
			// the intersected surface and the way it's parametrised

			// calculate the vectors u1, v1, ...
			ArrayList<Vector3D> u1v1 = ((ParametrisedObject)i.o).getSurfaceCoordinateAxes(i.p);
			Vector3D
			u1 = u1v1.get(0),
			v1 = u1v1.get(1),
			// ... their normalised versions, ...
			u1Normalised = u1.getNormalised(),
			v1Normalised = v1.getNormalised();
			n1 = i.getNormalisedOutwardsSurfaceNormal();

			// calculate a 2D vector (du, dv) such that the light-ray direction d is
			// 	d = du*u1Normalised + dv*v1Normalised + component perpendicular to surface
			// Note that this works for non-orthogonal vectors u1Normalised and v1Normalised
			Vector2D dudv = r.getD().calculateDecomposition(u1Normalised, v1Normalised);

			// calculate the 3D position that corresponds to the coordinates (u, v)
			// in the other object's local coordinate system; this is the new ray's
			// new starting point
			newStartingPoint = getDestinationObject().getPointForSurfaceCoordinates(uv.x, uv.y);
			// System.out.println("Teleporting::getColour: ... teleported to position "+newStartingPoint+" with surface coordinates "+getDestinationObject().getSurfaceCoordinates(newStartingPoint));	// TODO

			// now calculate the new direction in the coordinate system defined by
			// the other object's surface and the way it's parametrised
			// In analogy to what we did above, calculate the vectors u2, v2, ...
			ArrayList<Vector3D> u2v2 = getDestinationObject().getSurfaceCoordinateAxes(newStartingPoint);
			Vector3D
			u2 = u2v2.get(0),
			v2 = u2v2.get(1),
			// ... their normalised versions, ...
			u2Normalised = u2.getNormalised(),
			v2Normalised = v2.getNormalised();
			// ... and n2, the (normalised) surface normal
			n2 = getDestinationObject().getNormalisedOutwardsSurfaceNormal(newStartingPoint);

			// is the incident beam coming from the inside or from the outside?
			// More importantly, is the scalar product of normal and ray direction positive or negative?
			double
			dDotN1 = Vector3D.scalarProduct(r.getD(), n1),
			sign = Math.signum(dDotN1);

			// In the basis (u2Normalised, v2Normalised, n2), the components of the new light-ray direction are
			// those of the original light-ray direction in the (u1Normalised, v1Normalised, n1)
			// basis, but scaled inversely to the axes scale factor (as the transverse wavelengths lambda_u and lambda_v
			// scale like the axes, but the transverse k components scale like 1/lambda_u, 1/lambda_v.
			double
			ku2 = dudv.x * u1.getLength() / u2.getLength(),
			kv2 = dudv.y * v1.getLength() / v2.getLength();

			// part of new direction that lies in plane tangential to surface
			Vector3D
			newRayDirectionTangential = Vector3D.sum(
					u2Normalised.getProductWith(ku2),
					v2Normalised.getProductWith(kv2)
			);

			// The longitudinal k component scales such that the length of k is unchanged.
			double
			kn2Squared = r.getD().getModSquared() - newRayDirectionTangential.getModSquared(),
			kn2;
			if(kn2Squared <= 0.0)
				// transformed ray is evanescent.
				// Does TIR happen?
				// And if so, where in between the teleporting surface and the target surface does it happen?
				// return Reflective.getReflectedColour(r, i, scene, l, traceLevel);
				throw new EvanescentException("Teleporting::getColour: transformed light-ray direction describes an evanescent wave");
			else
				kn2 = sign*Math.sqrt(kn2Squared);

			// Calculate the new light-ray direction in the (x, y, z) basis
			newRayDirection = Vector3D.sum(
					newRayDirectionTangential,
					n2.getProductWith(kn2)
			);

			// System.out.println("new ray direction = " + newRayDirection);
		}

		// calculate the intensity scaling factor
		intensityFactor = 1;
		// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
		// Also, one of the article's reviewers wrote this:
		// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
		// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
		// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
		// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
		// account together with the other method of calculation of the ray direction, no brightening will occur.
		// 
		// if brightening does occur, the factor should be something like
		//	(Vector3D.crossProduct(u2, v2).getLength() / Vector3D.crossProduct(u1, v1).getLength())
		//	* Math.abs(kn2 / dDotN1);	// cos(angle of new ray with normal) / cos(angle of old ray with normal)
		// provided the ray directions are normalised, the cos ratio is simply the modulus of the ratio of the ray-direction
		// components normal to the surface

		// launch a new ray from here, leaving the same time the incident ray hit
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(newStartingPoint, newRayDirection, i.t),
				(SceneObjectPrimitive)getDestinationObject(),	// object to avoid
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).multiply(getTransmissionCoefficient()*intensityFactor);
	}

	public void setDestinationObject(One2OneParametrisedObject destinationObject)
	{
		this.destinationObject = destinationObject;
	}

	public One2OneParametrisedObject getDestinationObject()
	{
		return destinationObject;
	}

	public TeleportationType getTeleportationType() {
		return teleportationType;
	}

	public void setTeleportationType(TeleportationType teleportationType) {
		this.teleportationType = teleportationType;
	}
}
