package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.panorama.PhiTheta;
import optics.raytrace.panorama.Side;
import math.MyMath;
import math.Vector3D;


public class PositionInvertingGeneralisedStandardPanorama3DGeometry extends GeneralisedStandardPanorama3DGeometry
{
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param i	interpupillaryDistance
	 * @param R	distance to...
	 */
	public PositionInvertingGeneralisedStandardPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double i, double R)
	{
		super(M, theta0, phi0, i, R);
	}
	
	/**
	 * Default constructor
	 */
	public PositionInvertingGeneralisedStandardPanorama3DGeometry()
	{
		super();
	}

	
	@Override
	public PhiTheta getAnglesForPosition(Vector3D P, Side side, Vector3D forwardDirection)
	{
		// Daniel's formulas
		// eqn (5.8)
		// alpha = ej / (P^2 - ej^2) (-ej +/- sqrt(P^2 - R^2 + (P R / ej)^2))
		
		double e = 0.5*interpupillaryDistance;
		Vector3D MP = Vector3D.difference(P, M);
		double p = MP.getLength();
		double
			px = Vector3D.scalarProduct(MP, theta90phi0),
			py = Vector3D.scalarProduct(MP, theta0),
			pz = Vector3D.scalarProduct(MP, theta90phi90);
		double p2 = p*p;
		double r2 = R*R;
		double e2 = e*e;
		double sqrt = Math.sqrt(p2 - r2 + p2*r2/e2);
		double f = e / (p2 - e2);
		double alpha1 = f * (-e + sqrt);
		double alpha2 = f * (-e - sqrt);
		
		// System.out.println("alpha1 = "+alpha1+", alpha2 = "+alpha2);
		
		// need to check which one of these solutions is the correct one!
		// We think that the two values of alpha describe the two points where the ray intersects the sphere of radius R;
		// take the greater value of alpha, i.e. the intersection that lies in the forward direction
		double alpha = Math.max(alpha1, alpha2);
				
		// eqn (5.12)
		// cos theta_j = alpha P_y / R
		double cosTheta = alpha*py / R;
		double theta = Math.acos(cosTheta);
		double sinTheta = Math.sin(theta);
		
		// eqn (5.14) and (5.15)
		double d = e2*MyMath.square(1-alpha) + r2*sinTheta*sinTheta;
		double sinPhi = (side.toFactor()*alpha*px*e*(1-alpha) + alpha*R*pz*sinTheta)/d;
		double cosPhi = (-side.toFactor()*alpha*pz*e*(1-alpha) + alpha*R*px*sinTheta)/d;
		double phi = Math.atan2(sinPhi, cosPhi);
		
		return new PhiTheta(
				(phi<0)?(phi+2*Math.PI):phi,
				theta
			);
	}
}
