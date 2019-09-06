package optics.raytrace.panorama;

/**
 * @author johannes
 * An azimuthal angle, phi, and a polar angle, theta.
 * Together, they describe a direction
 */
public class PhiTheta
{
	protected double phi, theta;
	
	public PhiTheta(double phi, double theta)
	{
		setPhi(phi);
		setTheta(theta);
	}

	public double getPhi() {
		return phi;
	}

	public void setPhi(double phi) {
		this.phi = phi;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}
}
