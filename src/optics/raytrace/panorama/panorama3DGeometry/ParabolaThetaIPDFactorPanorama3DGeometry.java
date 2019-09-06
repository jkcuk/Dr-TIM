package optics.raytrace.panorama.panorama3DGeometry;

import math.Vector3D;


/**
 * @author johannes
 * Describes a modification of the standard way a 3D panoramic image is being calculated.
 * That standard way is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * The modification is that the camera distance is multiplied by (1-(theta/90°-1)^power), which means that, when viewing
 * the poles, the camera distance is zero and the view becomes 2-dimensional. 
 */
public class ParabolaThetaIPDFactorPanorama3DGeometry extends IPDFactorPanorama3DGeometry
{
	protected double
		power;	// power
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpupillary distance
	 * @param power	power
	 */
	public ParabolaThetaIPDFactorPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance, double power)
	{
		super(M, theta0, phi0, interpupillaryDistance);
		
		setPower(power);
	}
	
	public ParabolaThetaIPDFactorPanorama3DGeometry(double power)
	{
		super();
		
		setPower(power);
	}

	
	/**
	 * Default constructor
	 */
	public ParabolaThetaIPDFactorPanorama3DGeometry()
	{
		super();
		
		setPower(1);
	}
	
	
	// setters & getters

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}


	/**
	 * Override to customise
	 * @param phi
	 * @param theta
	 * @return
	 */
	@Override
	public double getIPDFactor(double phi, double theta)
	{
		return 1. - Math.pow(theta*2/Math.PI-1, 2*power);
	}
}
