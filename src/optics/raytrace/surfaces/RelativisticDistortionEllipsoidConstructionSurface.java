package optics.raytrace.surfaces;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Ellipsoid;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;


/**
 * A surface property representing the surface of an ellipsoid which, when seen from the inside, makes the scene  looks relativistically distorted
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

	private SceneObjectContainer scene;
	
	// TODO
	/**
	 * direction of the velocity of the camera in the scene frame, or the other way round?
	 */
	private Vector3D betaHat;
	
	private double beta;
	
	private Ellipsoid ellipsoid;
	
	/**
	 * Creates a new surface property for a (partially) transparent surface
	 * 
	 * @param transmissionCoefficient
	 */
	public RelativisticDistortionEllipsoidConstructionSurface(SceneObjectContainer scene, Vector3D betaHat, double beta, Ellipsoid ellipsoid)
	{
		super(
				1.0,	// transmissionCoefficient
				false	// shadowThrowing
			);
		
		setScene(scene);
		setBetaHat(betaHat);
		setBeta(beta);
		setEllipsoid(ellipsoid);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RelativisticDistortionEllipsoidConstructionSurface clone()
	{
		return new RelativisticDistortionEllipsoidConstructionSurface(scene, betaHat, beta, ellipsoid);
	}
	
	
	
	// setters & getters

	public SceneObjectContainer getScene() {
		return scene;
	}

	public void setScene(SceneObjectContainer scene) {
		this.scene = scene;
	}

	public Vector3D getBetaHat() {
		return betaHat;
	}

	public void setBetaHat(Vector3D betaHat) {
		this.betaHat = betaHat;
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

	public void setEllipsoid(Ellipsoid ellipsoid) {
		this.ellipsoid = ellipsoid;
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
		Vector3D dPrimeMinusBeta = Vector3D.difference(ray.getD(), betaHat.getProductWith(beta));
		Vector3D d = Vector3D.sum(
				dPrimeMinusBeta.getPartParallelTo(betaHat),
				dPrimeMinusBeta.getPartPerpendicularTo(betaHat).getProductWith(gamma)
				);

		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: dPrimeMinusBeta = "+dPrimeMinusBeta);
		// System.out.println("RelativisticDistortionEllipsoidConstructionSurface::getColour: d = "+d);

		// launch a new ray from here
			
		return this.scene.getColour(
			new Ray(
					ray.getP(),	// start position
					d,	// direction
					ray.getT()	// start time
					),
			l,
			this.scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}
}
