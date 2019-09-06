package optics.raytrace.panorama.panorama3DGeometry;

import math.Vector3D;


/**
 * @author johannes
 * Describes a modification of the standard way a 3D panoramic image is being calculated.
 * That standard way is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * The modification is that the camera distance is multiplied by a constant factor.
 * If that factor is zero, the camera distance is zero and the view becomes 2-dimensional. 
 */
public class ConstantIPDFactorPanorama3DGeometry extends IPDFactorPanorama3DGeometry
{
	protected double
		IPDFactor;
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpupillary distance
	 * @param IPDFactor	the IPD factor
	 */
	public ConstantIPDFactorPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance, double IPDFactor)
	{
		super(M, theta0, phi0, interpupillaryDistance);
		
		setIPDFactor(IPDFactor);
	}
	
	public ConstantIPDFactorPanorama3DGeometry(double IPDFactor)
	{
		super();
		
		setIPDFactor(IPDFactor);
	}

	
	/**
	 * Default constructor
	 */
	public ConstantIPDFactorPanorama3DGeometry()
	{
		super();
		
		setIPDFactor(1);
	}
	
	
	// setters & getters

	public double getIPDFactor() {
		return IPDFactor;
	}

	public void setIPDFactor(double IPDFactor) {
		this.IPDFactor = IPDFactor;
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
		return IPDFactor;
	}
}
