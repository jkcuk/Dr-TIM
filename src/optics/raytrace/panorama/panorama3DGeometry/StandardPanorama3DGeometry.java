package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.core.Ray;
import optics.raytrace.panorama.PhiTheta;
import optics.raytrace.panorama.Side;
import math.Vector3D;


/**
 * @author johannes
 * Describes the standard way a 3D panoramic image is being calculated, which is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * 
 * We define here the angle theta as the angle with the <i>up</i> direction.
 */
public class StandardPanorama3DGeometry extends AbstractPanorama3DGeometry
{
	protected Vector3D
		M,	// midpoint of circle on which the virtual cameras move
		theta0,	// normalised vector in the up direction, the theta=0 direction
		theta90phi0,	// unit vector pointing to "equator" (theta=90°) in direction in which phi=0 ("backwards")
		theta90phi90;	// unit vector in the equator (theta=90°) plane pointing in the phi=90° direction
	protected double
		interpupillaryDistance;	// virtual interpupillary distance
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpuillary distance
	 */
	public StandardPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance)
	{
		super();
		
		setMidpoint(M);
		setDirections(theta0, phi0);
		setInterpupillaryDistance(interpupillaryDistance);
	}
	
	/**
	 * Default constructor
	 */
	public StandardPanorama3DGeometry()
	{
		this(
				new Vector3D(0, 0, 0),	// betweenTheEyes
				new Vector3D(0, 1, 0),	// up direction
				new Vector3D(0, 0, -1),	// phi=0 direction, i.e. "backward" direction
				1	// virtual interpupillary distance
			);
	}
	
	
	// setters & getters

	public Vector3D getMidpoint() {
		return M;
	}

	public void setMidpoint(Vector3D M) {
		this.M = M;
	}

	public Vector3D getTheta0() {
		return theta0;
	}

	public Vector3D getTheta90phi0() {
		return theta90phi0;
	}

	public Vector3D getTheta90phi90() {
		return theta90phi90;
	}

	/**
	 * Sets the normalised theta0 ("up"), theta90phi0 (vector in phi=0 direction in equator plane) and theta90phi90 (vector in phi=90 direction in equator plane).
	 * @param theta0	a (not necessarily normalised) vector in the theta=0 ("up") direction
	 * @param phi0	a vector in the phi=0 plane
	 */
	public void setDirections(Vector3D theta0, Vector3D phi0)
	{
		this.theta0 = theta0.getNormalised();
		theta90phi0 = phi0.getPartPerpendicularTo(this.theta0).getNormalised();		
		theta90phi90 = Vector3D.crossProduct(theta0, theta90phi0);
	}

	public double getInterpupillaryDistance() {
		return interpupillaryDistance;
	}

	public void setInterpupillaryDistance(double interpupillaryDistance) {
		this.interpupillaryDistance = interpupillaryDistance;
	}
	
	
	//
	// methods that convert between angles and direction
	//
	
	/**
	 * @param d	normalised direction
	 * @return	the polar angle theta associated with the normalised direction vector d
	 */
	public double direction2Theta(Vector3D d)
	{
		return Math.acos(Vector3D.scalarProduct(d, theta0));
	}
	
	/**
	 * @param d	normalised direction
	 * @return	the azimuthal angle phi associated with the normalised direction vector d
	 */
	public double direction2Phi(Vector3D d)
	{
		double phi = Math.atan2(
				Vector3D.scalarProduct(d, theta90phi90),
				Vector3D.scalarProduct(d, theta90phi0)
			);
		
		return (phi<0)?(phi+2*Math.PI):phi;
	}

	public Vector3D angles2Direction(double phi, double theta)
	{
		double sinTheta = Math.sin(theta);
		return Vector3D.sum(
				theta0.getProductWith(Math.cos(theta)),
				theta90phi0.getProductWith(sinTheta*Math.cos(phi)),
				theta90phi90.getProductWith(sinTheta*Math.sin(phi))
			);
	}
	

	//
	// implementation of abstract AbstractPanorama3DGeometry methods
	//

	
	/* (non-Javadoc)
	 * @see optics.raytrace.panorama.AbstractPanorama3DGeometry#getRayForAngles(double, double, optics.raytrace.panorama.Side)
	 */
	@Override
	public Ray getRayForAngles(double phi, double theta, Side side)
	{
		// first calculate the view direction that corresponds to the angles phi and theta
		Vector3D d = angles2Direction(phi, theta);
		
		Vector3D dCrossTheta0 = Vector3D.crossProduct(d, theta0);
		if(dCrossTheta0.getModSquared() == 0) dCrossTheta0 = theta90phi0;
		
		// the direction from the midpoint to the right eye is then theta0 x d (it's a left-handed coordinate system!),
		// i.e. the camera position is
		Vector3D c = Vector3D.sum(
				M,	// midpoint
				dCrossTheta0.getWithLength(0.5*interpupillaryDistance*side.toFactor())
			);
		
		// System.out.println("StandardPanorama3DGeometry::getRayForAngles: 0.5*interpupillaryDistance*side.toFactor()="+0.5*interpupillaryDistance*side.toFactor());
		
		// the ray from the camera position in the direction d is then
		return new Ray(c, d, 0, false);	// start time = 0
	}


	/**
	 * calculate the position the camera would have when looking at a given position P.
	 * When viewing with VR headsets, the "camera" (i.e. eye) positions are given by the head orientation,
	 * here given by a point in the centre of view.
	 * @param P
	 * @param side	LEFT or RIGHT
	 * @param forwardDirection
	 * @return
	 */
	public Vector3D calculateCameraPosition(Vector3D P, Side side, Vector3D centreOfView)
	{
		Vector3D forwardDirection = Vector3D.difference(centreOfView, M);
		
		return Vector3D.sum(M, Vector3D.crossProduct(forwardDirection, theta0).getWithLength(side.toFactor()*0.5*interpupillaryDistance));
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.panorama.AbstractPanorama3DGeometry#getAngles(math.Vector3D, optics.raytrace.panorama.Side)
	 */
	@Override
	public PhiTheta getAnglesForPosition(Vector3D P, Side side, Vector3D centreOfView)
	{
		Vector3D direction = Vector3D.difference(P, calculateCameraPosition(P, side, centreOfView)).getNormalised();
		
		return new PhiTheta(
				direction2Phi(direction),
				direction2Theta(direction)
			);
	}

	
//	/**
//	 * Calculate the ray that points from the left/right camera in the direction of position.
//	 * @param position
//	 * @param side
//	 * @return	a ray
//	 */
//	public Ray getRayForPosition(Vector3D P, Side side)
//	{
//		// don't use the calculateCameraPosition method in ImprovedPanorama3DGeometry, which is a subclass of this one
//		Vector3D cameraPosition = this.calculateCameraPosition(P, side);
//		
//		// calculate the ray from the eye position to the position
//		return new Ray(
//				cameraPosition,	// start point
//				Vector3D.difference(P, cameraPosition),	// direction, not normalised
//				0	// start time
//			);
//	}
	
}
