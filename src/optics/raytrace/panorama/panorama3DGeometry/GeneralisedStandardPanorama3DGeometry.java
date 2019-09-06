package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.core.Ray;
import optics.raytrace.panorama.Side;
import math.Vector3D;


public class GeneralisedStandardPanorama3DGeometry extends StandardPanorama3DGeometry
{
	protected double
		R;	// distance from midpoint of...
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param i	interpupillaryDistance
	 * @param R	distance to...
	 */
	public GeneralisedStandardPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double i, double R)
	{
		super();
		
		setMidpoint(M);
		setDirections(theta0, phi0);
		setInterpupillaryDistance(i);
		setR(R);
	}
	
	/**
	 * Default constructor
	 */
	public GeneralisedStandardPanorama3DGeometry()
	{
		this(
				new Vector3D(0, 0, 0),	// betweenTheEyes
				new Vector3D(0, 1, 0),	// up direction
				new Vector3D(0, 0, -1),	// phi=0 direction, i.e. "backward" direction
				1,	// interpupillary distance
				10
			);
	}
	
	
	// setters & getters

	public double getR() {
		return R;
	}

	public void setR(double R) {
		this.R = R;
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.panorama.AbstractPanorama3DGeometry#getRayForAngles(double, double, optics.raytrace.panorama.Side)
	 */
	@Override
	public Ray getRayForAngles(double phi, double theta, Side side)
	{
		// first calculate the view direction that corresponds to the angles phi and theta
		Vector3D d = angles2Direction(phi, theta);
		
		// the direction from the midpoint to the right-eye camera is then theta0 x d (it's a left-handed coordinate system!),
		// i.e. the camera position is
		Vector3D c = Vector3D.sum(
				M,	// midpoint
				Vector3D.crossProduct(d, theta0).getWithLength(0.5*interpupillaryDistance*side.toFactor())
			);
		
		// the point on the sphere through which the light ray passes is
		Vector3D P = Vector3D.sum(M, d.getProductWith(R));
		
		// the ray from the camera position in the direction d is then
		return new Ray(c, Vector3D.difference(P, c), 0);	// start time = 0
	}
}
