package optics.raytrace.panorama.panorama3DGeometry;

import math.Vector3D;


/**
 * @author johannes
 * Describes a modification of the standard way a 3D panoramic image is being calculated.
 * That standard way is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * The modification is that the camera distance is multiplied by sin(theta)^exponent, which means that, when viewing
 * the poles, the camera distance is zero and the view becomes 2-dimensional. 
 * 
 * We define here the angle theta as the angle with the <i>up</i> direction.
 */
public class SinNThetaIPDFactorPanorama3DGeometry extends IPDFactorPanorama3DGeometry
{
	protected double
		sinePower;	// power of sine factor
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpupillary distance
	 * @param sinePower	power of sin(theta) factor
	 */
	public SinNThetaIPDFactorPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance, double sinePower)
	{
		super(M, theta0, phi0, interpupillaryDistance);
		
		setSinePower(sinePower);
	}
	
	public SinNThetaIPDFactorPanorama3DGeometry(double sinePower)
	{
		super();
		
		setSinePower(sinePower);
	}

	
	/**
	 * Default constructor
	 */
	public SinNThetaIPDFactorPanorama3DGeometry()
	{
		super();
		
		setSinePower(1);
	}
	
	
	// setters & getters

	public double getSinePower() {
		return sinePower;
	}

	public void setSinePower(double sinePower) {
		this.sinePower = sinePower;
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
		return Math.pow(Math.sin(theta), sinePower);
	}
}
