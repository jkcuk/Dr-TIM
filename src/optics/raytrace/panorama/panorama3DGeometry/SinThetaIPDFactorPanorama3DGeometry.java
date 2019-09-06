package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.core.Ray;
import optics.raytrace.panorama.Side;
import math.Vector3D;


/**
 * @author johannes
 * Describes a modification of the standard way a 3D panoramic image is being calculated.
 * That standard way is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * The modification is that the camera distance is multiplied by sin(theta), which means that, when viewing
 * the poles, the camera distance is zero and the view becomes 2-dimensional. 
 */
public class SinThetaIPDFactorPanorama3DGeometry extends StandardPanorama3DGeometry
{
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpupillary distance
	 * @param sinePower	power of sin(theta) factor
	 */
	public SinThetaIPDFactorPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance)
	{
		super(M, theta0, phi0, interpupillaryDistance);
	}
	
	/**
	 * Default constructor
	 */
	public SinThetaIPDFactorPanorama3DGeometry()
	{
		super();
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
		
		// the direction from the midpoint to the right eye is then theta0 x d (it's a left-handed coordinate system!),
		// i.e. the camera position is
		Vector3D c = Vector3D.sum(
				M,	// midpoint
				Vector3D.crossProduct(d, theta0).getProductWith(0.5*interpupillaryDistance*side.toFactor())
			);
		
		// the ray from the camera position in the direction d is then
		return new Ray(c, d, 0);	// start time = 0
	}
}
