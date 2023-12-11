package optics.raytrace.sceneObjects;

import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.DistortedLookalikeSphereSurfaceProperty;


/**
 * An ellipsoid which, when seen from the inside, makes the rest of the scene looks relativistically distorted
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
public class DistortedLookalikeSphere extends Ellipsoid
{
	private static final long serialVersionUID = 7497435922426956741L;

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
	private double betaAbs;
	
	private double ellipsoidPrincipalRadiusInBetaDirection;
	
	private SpaceTimeTransformationType transformType;
	
	private double transmissionCoefficient;
	

	
	// constructors etc.
	
	/**
	 * Creates a new surface property for a (partially) transparent surface
	 * 
	 * @param transmissionCoefficient
	 */
	public DistortedLookalikeSphere(
			String description,
			Vector3D cameraPosition, 
			Vector3D beta, 
			SpaceTimeTransformationType transformType, 
			double ellipsoidPrincipalRadiusInBetaDirection,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				description,
				Vector3D.O,	// centre,
				Vector3D.X,	// a,
				Vector3D.Y,	// b,
				Vector3D.Z,	// c,
				null,	// surfaceProperty,
				parent,
				studio
			);
		setCameraPosition(cameraPosition);
		setBetaHat(beta);
		setBetaAbs(beta.getLength());
		setTransformType(transformType);
		setEllipsoidPrincipalRadiusInBetaDirection(ellipsoidPrincipalRadiusInBetaDirection);
		setTransmissionCoefficient(transmissionCoefficient);
		
		calculateEllipsoidParameters();
		setSurfaceProperty(new DistortedLookalikeSphereSurfaceProperty(this));
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DistortedLookalikeSphere clone()
	{
		return new DistortedLookalikeSphere(
				description,
				cameraPosition,
				getBeta(),
				transformType,
				ellipsoidPrincipalRadiusInBetaDirection,
				transmissionCoefficient,
				getParent(),
				getStudio()
			);
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

	public void setBetaHat(Vector3D beta) {
		this.betaHat = beta.getNormalised();
	}

	public double getBetaAbs() {
		return betaAbs;
	}
	
	public void setBeta(Vector3D beta)
	{
		this.betaHat = beta.getNormalised();
		this.betaAbs = beta.getLength();
	}

	/**
	 * Set betaAbs, i.e. the speed as a fraction of c, the speed of light.
	 * This method also pre-calculates the transverse stretch factor.
	 * @param betaAbs
	 */
	public void setBetaAbs(double betaAbs) {
		this.betaAbs = betaAbs;
	}
	
	public Vector3D getBeta()
	{
		return betaHat.getProductWith(betaAbs);
	}

	public SpaceTimeTransformationType getTransformType() {
		return transformType;
	}

	public void setTransformType(SpaceTimeTransformationType transformType) {
		this.transformType = transformType;
	}

	public double getEllipsoidPrincipalRadiusInBetaDirection() {
		return ellipsoidPrincipalRadiusInBetaDirection;
	}

	public void setEllipsoidPrincipalRadiusInBetaDirection(double ellipsoidPrincipalRadiusInBetaDirection) {
		this.ellipsoidPrincipalRadiusInBetaDirection = ellipsoidPrincipalRadiusInBetaDirection;
	}

	public double getTransverseStretchFactor() {
		return transverseStretchFactor;
	}

	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
	}



		
	// internal variables
		


	/**
	 * the ellipsoid this surface property is associated with
	 */
	private double transverseStretchFactor;

	
	// the interesting bits

	public double calculateTransverseEllipsoidStretchFactor()
	{
		// if(transformType == null) return 1.0;

		switch(transformType)
		{
		case GALILEAN_TRANSFORMATION:
			return 1.0;
		case LORENTZ_TRANSFORMATION:
		default:
			return Math.sqrt(1.-betaAbs*betaAbs);	// 1./gamma, where gamma = 1./Math.sqrt(1.-beta*beta);
		}
	}
	
	/**
	 * Create the ellipsoid that corresponds to the parameters of this surface property, make this the ellipsoid's surface property
	 * @param description
	 * @param parent
	 * @param studio
	 * @return	the ellipsoid
	 */
	public void calculateEllipsoidParameters()
	{
		// set the tranverse stretch factor (which is also used later, in the getColour method)
		transverseStretchFactor = calculateTransverseEllipsoidStretchFactor();

		// create two directions perpendicular to betaHat
		Vector3D alpha1Hat, alpha2Hat;
		
		alpha1Hat = Vector3D.getANormal(betaHat);
		alpha2Hat = Vector3D.crossProduct(betaHat, alpha1Hat);
		
		// set the internal ellipsoid variables
		setCentre(Vector3D.sum(cameraPosition, betaHat.getProductWith(ellipsoidPrincipalRadiusInBetaDirection*betaAbs)));
		setABC(
				betaHat.getProductWith(ellipsoidPrincipalRadiusInBetaDirection),	// a
				alpha1Hat.getProductWith(transverseStretchFactor*ellipsoidPrincipalRadiusInBetaDirection),	// b
				alpha2Hat.getProductWith(transverseStretchFactor*ellipsoidPrincipalRadiusInBetaDirection)	// c
			);
	}
}
