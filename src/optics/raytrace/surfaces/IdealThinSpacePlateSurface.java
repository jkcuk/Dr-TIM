package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.ParametrisedPlane;


/**
 * A surface property representing the a space plate
 * This is able to add an extra distance to a propagating light ray upon intersection with the plate
 * The main use of this is to make optical systems more compact
 * Here we define it to be idealised i.e non dispersive and work for all wavelengths
 * Additionally, it is thin which may not always be the case with a 'real' space plate.
 * 
 * @author Maik
 */
public class IdealThinSpacePlateSurface extends SurfacePropertyPrimitive
{

	private static final long serialVersionUID = 8313406615498345372L;
	
	// centre of the space plate
	protected Vector3D centre;
	//the object which a ray will intersect to form the surface extra distance profile
	protected ParametrisedObject teleportingIntersectingObject;	
	// the extra distance the space plate should add in the basic planar case


	/**
	 * Creates an instance of the surface property that adds an extra bit of propagation space to a light ray...
	 * ...In the most general case this is not limited to the shape of the surface.
	 * @param teleportingIntersectingObject
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public IdealThinSpacePlateSurface(
			ParametrisedObject teleportingIntersectingObject,
			double transmissionCoefficient,
			boolean shadowThrowing
			) 
	{
		super(transmissionCoefficient, shadowThrowing);
		this.teleportingIntersectingObject = teleportingIntersectingObject;
	}


	/**
	 * Creates an instance of the surface property that adds an extra bit of propagation space to a light ray...
	 * ...Specifically in the case where both surfaces are planar
	 * @param centre
	 * @param distanceVectorToBeAdded This will have both the direction and magnitude of the extra distance, i.e 1 unit along the z direction will be (0,0,1).
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public IdealThinSpacePlateSurface(
			Vector3D centre,
			Vector3D distanceVectorToBeAdded,
			double transmissionCoefficient,
			boolean shadowThrowing
			) 
	{
		super(transmissionCoefficient, shadowThrowing);
		this.teleportingIntersectingObject = getAsPlanarSurface(centre, distanceVectorToBeAdded);

	}


	/**
	 * Make a clone of the original IdealThinLensSurface surface property.
	 * @param original
	 */
	public IdealThinSpacePlateSurface(IdealThinSpacePlateSurface original)
	{
		this(
				original.getTeleportingIntersectingObject(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
				);		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinSpacePlateSurface clone()
	{
		return new IdealThinSpacePlateSurface(this);
	}

	// setters and getters

	public Vector3D getCentre() {
		return centre;
	}
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}
	public ParametrisedObject getTeleportingIntersectingObject() {
		return teleportingIntersectingObject;
	}
	public void setTeleportingIntersectingObject(ParametrisedObject teleportingIntersectingObject) {
		this.teleportingIntersectingObject = teleportingIntersectingObject;
	}


	public ParametrisedObject getAsPlanarSurface(Vector3D centre, Vector3D distanceVectorToBeAdded) {
		teleportingIntersectingObject = 
				new ParametrisedPlane(
						"plane of intersection after propegation",// description,
						Vector3D.sum(centre, distanceVectorToBeAdded),// pointOnPlane,
						distanceVectorToBeAdded.getNormalised(),// normal, 
						null,// surfaceProperty,
						null,// parent,
						null// studio
						);
		return teleportingIntersectingObject;
	}

	//
	// implement SurfaceProperty method
	//

	/*
	 * Define what should happen when the sides of the space plates are hit. 
	 * Default is return Black simulating baffles.
	 * Can be overwritten.
	 */
	public DoubleColour getSpacePlateedgeConditions() {
		return DoubleColour.BLACK;	
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
			throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;		

		//place a plane a distance away from the intersection and find where the same ray intersects this plane.
		//		teleportingIntersectingObject.setSurfaceProperty(new Teleporting((One2OneParametrisedObject)i.o)
		//				);
		RaySceneObjectIntersection i2;
		i2 = ((SceneObjectPrimitive)teleportingIntersectingObject).getClosestRayIntersection(ray.getBranchRay(i.p, ray.getD(), i.t, ray.isReportToConsole()));

		//if this is no intersection then either it is travelling the 'wrong' way or something has gone wrong...
		if(i2 == RaySceneObjectIntersection.NO_INTERSECTION) {

			//... if it is goign the other way, check to add the extra distance in this direction....
			i2 = ((SceneObjectPrimitive)teleportingIntersectingObject).getClosestRayIntersection(ray.getBranchRay(i.p, ray.getD().getProductWith(-1), i.t, ray.isReportToConsole()));

			//... if it still has no intersection something has now gone wrong
			if(i2 == RaySceneObjectIntersection.NO_INTERSECTION) {
				throw new RayTraceException("IdealThinSpacePlateSurface::getColour: Intersecting surface was not intersected in either direction");
				//				System.err.println("");
				//				return DoubleColour.YELLOW;
			}
		}

		Vector3D outwardPointingNormal = i.o.getNormalisedOutwardsSurfaceNormal(i.p);
		Vector3D inwardPointingNormal = outwardPointingNormal.getProductWith(-1);
		//System.out.println(inwardPointingNormal);

		RaySceneObjectIntersection i3;
		i3 = ((SceneObjectPrimitive)i.o).getClosestRayIntersection(new Ray(i2.p, inwardPointingNormal, i2.t, ray.isReportToConsole()));

		if(i3 == RaySceneObjectIntersection.NO_INTERSECTION) {

			//check if the ray is travelling the other way i.e back to the camera. If it does add the extra distance again
			i3 = ((SceneObjectPrimitive)i.o).getClosestRayIntersection(new Ray(i2.p, outwardPointingNormal, i2.t, ray.isReportToConsole()));

			//If it still is not intersection it has hit the sides of the space plate
			if(i3 == RaySceneObjectIntersection.NO_INTERSECTION) {
				//by default return black
				return getSpacePlateedgeConditions();
			}

		}

		return scene.getColourAvoidingOrigin(
				ray.getBranchRay(i3.p, ray.getD(), i2.t, ray.isReportToConsole()),
				i.o,
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
				).multiply(
						getTransmissionCoefficient()
						);
	}
	/**
	 * Teleporting surfaces that did not work... yet...
	 */
	//		
	//		One2OneParametrisedObject destinationObject = (One2OneParametrisedObject)i.o;
	//		
	//		//System.out.println(i2.o == teleportingIntersectingObject);
	//		
	//		// First check that the intersected object is properly parametrised...
	//		if(!(i2.o instanceof ParametrisedObject))
	//		{
	//			throw new RayTraceException("Teleporting::getColour: Object with teleporting surface is not of type ParametrisedSurface");
	//		}
	//		// ... and then calculate the parameters
	//		Vector2D uv = ((ParametrisedObject)i2.o).getSurfaceCoordinates(i2.p);
	//
	//		//System.out.println("Teleporting::getColour: intersection point "+i2.p+" has surface coordinates "+uv);
	//		
	//		// this is where the commonality between the different types of teleporting surfaces ends;
	//		// calculate the new starting point, the new ray direction, and the intensity factor according to the different teleportation type
	//		Vector3D newStartingPoint, newRayDirection;
	//		
	//		Vector3D n1, n2;
	//				
	//		// calculate the vectors u1, v1, ...
	//					ArrayList<Vector3D> u1v1 = ((ParametrisedObject)i2.o).getSurfaceCoordinateAxes(i2.p);
	//					Vector3D
	//					u1 = u1v1.get(0),
	//					v1 = u1v1.get(1),
	//					// ... their normalised versions, ...
	//					u1Normalised = u1.getNormalised(),
	//					v1Normalised = v1.getNormalised();
	//					n1 = i2.getNormalisedOutwardsSurfaceNormal();
	//					//System.out.println(u1Normalised+" "+v1Normalised);
	//					// calculate a 2D vector (du, dv) such that the light-ray direction d is
	//					// 	d = du*u1Normalised + dv*v1Normalised + component perpendicular to surface
	//					// Note that this works for non-orthogonal vectors u1Normalised and v1Normalised
	//					Vector2D dudv = ray.getD().calculateDecomposition(u1Normalised, v1Normalised);
	//
	//					System.out.println(dudv);
	//					// calculate the 3D position that corresponds to the coordinates (u, v)
	//					// in the other object's local coordinate system; this is the new ray's
	//					// new starting point
	//					newStartingPoint = destinationObject.getPointForSurfaceCoordinates(uv.x, uv.y);
	//					// System.out.println("Teleporting::getColour: ... teleported to position "+newStartingPoint+" with surface coordinates "+getDestinationObject().getSurfaceCoordinates(newStartingPoint));
	//
	//					// now calculate the new direction in the coordinate system defined by
	//					// the other object's surface and the way it's parametrised
	//					// In analogy to what we did above, calculate the vectors u2, v2, ...
	//					ArrayList<Vector3D> u2v2 = destinationObject.getSurfaceCoordinateAxes(newStartingPoint);
	//					Vector3D
	//					u2 = u2v2.get(0),
	//					v2 = u2v2.get(1),
	//					// ... their normalised versions, ...
	//					u2Normalised = u2.getNormalised(),
	//					v2Normalised = v2.getNormalised();
	//					// ... and n2, the (normalised) surface normal
	//					n2 = destinationObject.getNormalisedOutwardsSurfaceNormal(newStartingPoint);
	//
	//					//System.out.println(u1v1+" "+u2v2);
	//					// is the incident beam coming from the inside or from the outside?
	//					// More importantly, is the scalar product of normal and ray direction positive or negative?
	//					double
	//					dDotN1 = Vector3D.scalarProduct(ray.getD(), n1),
	//					sign = Math.signum(dDotN1);
	//					
	//				//	System.out.println(n1+" "+n2);
	//
	//					// In the basis (u2Normalised, v2Normalised, n2), the components of the new light-ray direction are
	//					// those of the original light-ray direction in the (u1Normalised, v1Normalised, n1)
	//					// basis, but scaled inversely to the axes scale factor (as the transverse wavelengths lambda_u and lambda_v
	//					// scale like the axes, but the transverse k components scale like 1/lambda_u, 1/lambda_v.
	//					double
	//					ku2 = dudv.x * u1.getLength() / u2.getLength(),
	//					kv2 = dudv.y * v1.getLength() / v2.getLength();
	//
	//					// part of new direction that lies in plane tangential to surface
	//					Vector3D
	//					newRayDirectionTangential = Vector3D.sum(
	//							u2Normalised.getProductWith(ku2),
	//							v2Normalised.getProductWith(kv2)
	//					);
	//
	//					// The longitudinal k component scales such that the length of k is unchanged.
	//					double
	//					kn2Squared = ray.getD().getModSquared() - newRayDirectionTangential.getModSquared(),
	//					kn2;
	//					if(kn2Squared <= 0.0)
	//						// transformed ray is evanescent.
	//						// Does TIR happen?
	//						// And if so, where in between the teleporting surface and the target surface does it happen?
	//						// return Reflective.getReflectedColour(r, i, scene, l, traceLevel);
	//						throw new EvanescentException("Teleporting::getColour: transformed light-ray direction describes an evanescent wave");
	//					else
	//						kn2 = sign*Math.sqrt(kn2Squared);
	//
	//					// Calculate the new light-ray direction in the (x, y, z) basis
	//					newRayDirection = Vector3D.sum(
	//							newRayDirectionTangential,
	//							n2.getProductWith(kn2)
	//					);
	//
	//
	//			//	System.out.println("new ray direction = " + newRayDirection);
	//			
	//
	//			// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
	//			// Also, one of the article's reviewers wrote this:
	//			// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
	//			// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
	//			// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
	//			// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
	//			// account together with the other method of calculation of the ray direction, no brightening will occur.
	//			// 
	//			// if brightening does occur, the factor should be something like
	//			//	(Vector3D.crossProduct(u2, v2).getLength() / Vector3D.crossProduct(u1, v1).getLength())
	//			//	* Math.abs(kn2 / dDotN1);	// cos(angle of new ray with normal) / cos(angle of old ray with normal)
	//			// provided the ray directions are normalised, the cos ratio is simply the modulus of the ratio of the ray-direction
	//			// components normal to the surface
	//		
	//		
	//	System.out.println("Intersection "+i.p+"sent to "+newStartingPoint+" with direction "+newRayDirection + " Compared to original "+ ray.getD());
	//	
	//		//System.out.println(i2);	
	//		// launch a new ray from here
	//		return 
	//				scene.getColourAvoidingOrigin(
	//						ray.getBranchRay(newStartingPoint, newRayDirection, i2.t, ray.isReportToConsole()),
	//						(SceneObjectPrimitive)i.o,	// object to avoid
	//						l,
	//						scene,
	//						traceLevel-1,
	//						raytraceExceptionHandler
	//					).multiply(getTransmissionCoefficient());
	//			}

}

