package optics.raytrace.research.relativisticDistortion;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Ellipsoid;


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
public class RelativisticDistortionEllipsoidConstructionSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 8616516489729700613L;

	
	/**
	 * the position of the (pinhole!) camera's pinhole
	 */
	private Vector3D cameraPosition;
	
	/**
	 * direction of the velocity of the camera in the scene frame, or the other way round?
	 */
	private Vector3D betaHat;
	
	/**
	 * speed in units of c, i.e. v/c
	 */
	private double beta;
	
	
	// internal variable
	
	/**
	 * the ellipsoid this surface property is associated with
	 */
	private Ellipsoid ellipsoid;
	
	
	
	// constructors etc.
	
	/**
	 * Creates a new surface property for a (partially) transparent surface
	 * 
	 * @param transmissionCoefficient
	 */
	public RelativisticDistortionEllipsoidConstructionSurface(Vector3D cameraPosition, Vector3D betaHat, double beta)
	{
		super(
				1.0,	// transmissionCoefficient
				false	// shadowThrowing
			);
		
		setCameraPosition(cameraPosition);
		setBetaHat(betaHat);
		setBeta(beta);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RelativisticDistortionEllipsoidConstructionSurface clone()
	{
		return new RelativisticDistortionEllipsoidConstructionSurface(cameraPosition, betaHat, beta);
	}
	
	
	
	// setters & getters

	public Vector3D getCameraPosition() {
		return cameraPosition;
	}

	public void setCameraPosition(Vector3D cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public Vector3D getBetaHat() {
		return betaHat;
	}

	public void setBetaHat(Vector3D betaHat) {
		this.betaHat = betaHat.getNormalised();
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public Ellipsoid getEllipsoid() {
		return ellipsoid;
	}

	
	
	// the interesting bits
	
	/**
	 * Create the ellipsoid that corresponds to the parameters of this surface property, make this the ellipsoid's surface property
	 * @param description
	 * @param maxDimension	the size of the ellipsoid in the directions perpendicular to betaHat
	 * @param parent
	 * @param studio
	 * @return	the ellipsoid
	 */
	public Ellipsoid createAndSetEllipsoid(
			String description,
			double maxDimension,
			SceneObject parent,
			Studio studio
		)
	{
		// create two directions perpendicular to betaHat
		Vector3D alpha1Hat, alpha2Hat;
		
		alpha1Hat = Vector3D.getANormal(betaHat);
		alpha2Hat = Vector3D.crossProduct(betaHat, alpha1Hat);
		
		double gamma = 1./Math.sqrt(1.-beta*beta);

		// set the internal ellipsoid variable
		ellipsoid = new Ellipsoid(
				description,
				Vector3D.sum(cameraPosition, betaHat.getProductWith(beta)),	// centre
				betaHat.getProductWith(maxDimension),	// a
				alpha1Hat.getProductWith(maxDimension/gamma),	// b
				alpha2Hat.getProductWith(maxDimension/gamma),	// c
				this,	// surfaceProperty
				parent,	// parent
				studio
				);
		
		return ellipsoid;
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		double gamma = 1./Math.sqrt(1-beta*beta);

		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: gamma = "+gamma);

		// ray.getD() is the (backwards) light-ray direction in the camera frame; construct the corresponding light-ray direction in the scene frame
		Vector3D dPrimeMinusBetaOverGamma = Vector3D.difference(Vector3D.difference(i.p, cameraPosition), betaHat.getProductWith(beta));
		Vector3D d = Vector3D.sum(
				dPrimeMinusBetaOverGamma.getPartParallelTo(betaHat),
				dPrimeMinusBetaOverGamma.getPartPerpendicularTo(betaHat).getProductWith(gamma)
				);

		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: dPrimeMinusBeta = "+dPrimeMinusBeta);
		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: d = "+d);

		// calculate the start position of the ray used to determine the colour of the point on the surface
		RaySceneObjectIntersection i1 = ellipsoid.getClosestRayIntersection(new Ray(
				cameraPosition,	// start position
				d,	// direction
				ray.getT()
				));
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			new Ray(
					i1.p,	// start position
					d,	// direction
					ray.getT()	// start time
					),
			ellipsoid,	// origin object
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}
}
