package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.panorama.Side;
import math.MyMath;
import math.Vector3D;


/**
 * @author johannes
 * Describes the standard way a 3D panoramic image is being calculated, which is described in
 * P. Bourke, "Synthetic stereoscopic panoramic images", in 
 * <i>Lecture Notes in Computer Science (LNCS)</i>, <b>4270</b>, 147-155 (2006).
 * 
 * However, the viewing is happening in an "improved" way, such that the eye positions are the same as the corresponding
 * camera positions.
 */
public class PositionInvertingStandardPanorama3DGeometry extends StandardPanorama3DGeometry
{	
	/**
	 * Constructor; the direction with theta=90° and phi=180° is the centre of the panoramic image
	 * @param M	midpoint between the cameras
	 * @param theta0	the theta=0 ("up") direction
	 * @param phi0	vector in the phi=0 plane
	 * @param interpupillaryDistance	virtual interpuillary distance
	 */
	public PositionInvertingStandardPanorama3DGeometry(Vector3D M, Vector3D theta0, Vector3D phi0, double interpupillaryDistance)
	{
		super(M, theta0, phi0, interpupillaryDistance);
	}
	
	/**
	 * Default constructor
	 */
	public PositionInvertingStandardPanorama3DGeometry()
	{
		super();
	}
	

	/**
	 * calculate the position the camera would have when looking at a given position P
	 * @param P
	 * @param side	LEFT or RIGHT
	 * @return
	 */
	@Override
	public Vector3D calculateCameraPosition(Vector3D P, Side side, Vector3D centreOfView)
	{
		// create a coordinate system of the plane in which the cameras lie such that the
		// point M is the origin and the orthographic projection of P into this plane lies on the x axis;
		// this is achieved by pointing the x axis in the direction of the part of (P-M) that is
		// perpendicular to theta0
		Vector3D
			xHat = Vector3D.difference(P, M).getPartPerpendicularTo(theta0).getNormalised(),
			// this is achieved by pointing the x axis in the direction (up x (P-M)) x up
//			xHat = Vector3D.crossProduct(
//					Vector3D.crossProduct(theta0, Vector3D.difference(P, M)),	// up x (P - M)
//					theta0
//					).getNormalised(),
			yHat = Vector3D.crossProduct(theta0, xHat);
		
		// System.out.println("xHat = " + xHat + ", yHat = " + yHat);
		
		// now we can calculate the x component of P
		double Px = Vector3D.scalarProduct(Vector3D.difference(P, M), xHat);
		
		// System.out.println("Px = " + Px);
		
		// calculate Daniel's Lambda_p factor
		double
			f = MyMath.square(2*Px/interpupillaryDistance),
			LambdaP = Math.sqrt(f /* 4*MyMath.square(Px/interpupillaryDistance) */ - 1);
		
		// System.out.println("LambdaP = " + LambdaP);
		
		// calculate the position of the camera
		return Vector3D.sum(
				M,	// origin of new coordinate system
				Vector3D.sum(
						xHat.getProductWith(Px/f),	// divide here by f to save ourselves getProductWith below
						yHat.getProductWith(-side.toFactor()*Px*LambdaP/f)	// divide here by f to save ourselves getProductWith below
					)	// .getProductWith(1/f /* (0.25*interpupillaryDistance*interpupillaryDistance)/Px/Px */)
			);
	}
}
