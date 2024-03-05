package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.DistortedLookalikeSphere;


/**
 * A surface property representing the surface of an ellipsoid which, when seen from the inside, makes the rest of the scene looks relativistically distorted
 * 
 * The idea is that, for a pinhole camera that is moving with velocity v = beta c in a stationary scene, a photo taken at the time when the camera frame and the scene
 * frame coincide at the camera position, PC, only the light-ray direction changes.
 * 
 * A light ray with direction dHat in the scene frame becomes, in the camera frame, dPrime (not normalised!) = dHat_perp / gamma + dHat_parallel + beta,
 * where gamma = 1/sqrt(1-beta^2), and dHat_perp and dHat_parallel is the part of dHat perpendicular and parallel to beta, respectively.
 * Geometrically, this is the same as dHat being the direction to a point P on a unit sphere, centred at the camera position, and dPrime being the point that results if
 * the unit sphere, and with it the point P, are stretched by a factor 1/gamma in the directions perpendicular to beta (the sphere then becomes an ellipsoid), and the
 * ellipsoid -- and with it the point on it that corresponds to P --, are displaced by beta.
 * 
 * @author Johannes Courtial
 */
public class DistortedLookalikeSphereSurfaceProperty extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -2186997435791552310L;
	
	private DistortedLookalikeSphere ellipsoid;
	
	// constructors etc.
	
	/**
	 * Creates a new surface property for a (partially) transparent surface
	 * 
	 * @param transmissionCoefficient
	 */
	public DistortedLookalikeSphereSurfaceProperty(DistortedLookalikeSphere ellipsoid)
	{
		super(
				ellipsoid.getTransmissionCoefficient(),	// transmissionCoefficient, when looking from inside the ellipsoid
				false	// shadowThrowing
			);
		
		setEllipsoid(ellipsoid);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DistortedLookalikeSphereSurfaceProperty clone()
	{
		return new DistortedLookalikeSphereSurfaceProperty(ellipsoid);
	}
	
	
	
	// setters & getters

	public DistortedLookalikeSphere getEllipsoid() {
		return ellipsoid;
	}

	public void setEllipsoid(DistortedLookalikeSphere ellipsoid) {
		this.ellipsoid = ellipsoid;
	}


	
	// internal variables
	
	/**
	 * the ellipsoid this surface property is associated with
	 */
	// private double transverseStretchFactor;

	
	// the interesting bits

	public double calculateTransverseEllipsoidStretchFactor()
	{
		// if(transformType == null) return 1.0;

		switch(ellipsoid.getTransformType())
		{
		case GALILEAN_TRANSFORMATION:
			return 1.0;
		case LORENTZ_TRANSFORMATION:
		default:
			return Math.sqrt(1.-ellipsoid.getBetaAbs()*ellipsoid.getBetaAbs());	// 1./gamma, where gamma = 1./Math.sqrt(1.-beta*beta);
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int, optics.raytrace.core.RaytraceExceptionHandler)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
				
		// imagine the ellipsoid surface to be coloured;
		// calculate the colour of the given surface point
		
		// System.out.println("DistortedLookalikeSphereSurfaceProperty::getColour: ray = "+ray+", traceLevel = "+traceLevel);

		// ray.getD() is the (backwards) light-ray direction in the camera frame; construct the corresponding light-ray direction in the scene frame
		Vector3D dPrimeMinusBetaOverGamma = 
				// Vector3D.difference(i.p, ellipsoid.getCameraPosition());
				Vector3D.difference(Vector3D.difference(i.p, ellipsoid.getCameraPosition()), ellipsoid.getBetaHat().getProductWith(ellipsoid.getLookalikeSphereRadius()*ellipsoid.getBetaAbs()));
		Vector3D d = Vector3D.sum(
				dPrimeMinusBetaOverGamma.getPartParallelTo(ellipsoid.getBetaHat()),
				dPrimeMinusBetaOverGamma.getPartPerpendicularTo(ellipsoid.getBetaHat()).getProductWith(1./ellipsoid.getTransverseStretchFactor())
				);

		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: dPrimeMinusBeta = "+dPrimeMinusBeta);
		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: d = "+d);

		// calculate the start position of the ray used to determine the colour of the point on the surface
		RaySceneObjectIntersection i1 = ellipsoid.getClosestRayIntersection(new Ray(
				ellipsoid.getCameraPosition(),	// start position
				d,	// direction
				ray.getT(),
				ray.isReportToConsole()
				));
		
		Ray ray1 = new Ray(
				(i1 != RaySceneObjectIntersection.NO_INTERSECTION)?i1.p:ellipsoid.getCameraPosition(),	// start position
				d,	// direction
				ray.getT(),	// start time
				ray.isReportToConsole()
				);
		
		// the colour of the surface point is therefore
		DoubleColour s = scene.getColourAvoidingOrigin(
				ray1,
				ellipsoid,	// origin object
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			);
		
		return s.multiply(getTransmissionCoefficient());
	}
}
