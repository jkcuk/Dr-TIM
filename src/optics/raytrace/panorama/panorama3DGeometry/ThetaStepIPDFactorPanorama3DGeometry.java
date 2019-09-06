package optics.raytrace.panorama.panorama3DGeometry;

import math.MyMath;
import math.Vector3D;


/**
 * @author johannes
 * Describes a modification of the standard way a 3D panoramic image is being calculated.
 * That standard way is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * 
 * The modification is that the camera distance is multiplied by a function that is approx. zero in an angular range
 * around the poles, which means that, when viewing
 * the poles, the camera distance is zero and the view becomes 2-dimensional. 
 */
public class ThetaStepIPDFactorPanorama3DGeometry extends IPDFactorPanorama3DGeometry
{
	protected double
		thetaP,	// angle at which poles end
		k;	// determines width of transitional region
	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpupillary distance
	 * @param sinePower	power of sin(theta) factor
	 */
	public ThetaStepIPDFactorPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance, double thetaP, double k)
	{
		super(M, theta0, phi0, interpupillaryDistance);

		setThetaP(thetaP);
		setK(k);
	}
	
	/**
	 * Default constructor
	 */
	public ThetaStepIPDFactorPanorama3DGeometry()
	{
		super();
		
		setThetaP(MyMath.deg2rad(40));
		setK(10);
	}
	
	
	// setters & getters

	public double getThetaP() {
		return thetaP;
	}

	public void setThetaP(double thetaP) {
		this.thetaP = thetaP;
	}

	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
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
		return 0.25*(1+Math.tanh(k*(theta-thetaP)))*(1-Math.tanh(k*(theta-Math.PI+thetaP)));
	}
}
