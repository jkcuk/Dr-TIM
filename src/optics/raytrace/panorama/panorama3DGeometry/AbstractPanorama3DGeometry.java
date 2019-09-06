package optics.raytrace.panorama.panorama3DGeometry;

import optics.raytrace.core.Ray;
import optics.raytrace.panorama.PhiTheta;
import optics.raytrace.panorama.Side;
import math.Vector3D;


/**
 * @author johannes
 * Describes the geometry used to calculate a 3D panoramic image, and view it again.
 */
public abstract class AbstractPanorama3DGeometry
{
	/**
	 * Calculate a ray suitable for ray tracing that corresponds to given angles.
	 * This method is used when calculating the images corresponding to the left and right eyes.
	 * The horizontal coordinate in these panoramic images is the angle phi (0 to 2π),
	 * the vertical coordinate is the angle theta (0 to π).
	 * @param phi	azimuthal angle
	 * @param theta	polar angle
	 * @param side	LEFT or RIGHT
	 * @return	a ray that starts at the position of the (left or right) camera with a direction that corresponds to these angles
	 */
	public abstract Ray getRayForAngles(double phi, double theta, Side side);
	
	/**
	 * Calculate the azimuthal and polar angles of the direction in which the camera sees a given position.
	 * This method can be used to calculate the angles in the left and right panoramic image in which the (virtual)
	 * screen pixel at the given position is seen, so that the corresponding colour in the corresponding panoramic
	 * image can be looked up and displayed.
	 * Often the positioning of the left- and right-eye positions depends on the forward direction, which is
	 * given by a point in the centre of view, e.g. the centre of the screen.
	 * @param position
	 * @param side	LEFT or RIGHT
	 * @param centreOfView	a point in the centre of view
	 * @return	(phi, theta), which take values in the range 0 to 2π (phi) and 0 to π (theta)
	 */
	public abstract PhiTheta getAnglesForPosition(Vector3D position, Side side, Vector3D centreOfView);

//	/**
//	 * Calculate the ray that points from the left/right camera in the direction of position.
//	 * WHEN IS THIS USED?  DO WE ACTUALLY NEED THIS?
//	 * Override if there is a simpler/faster/nicer way to calculate this.
//	 * @param position
//	 * @param side
//	 * @return	a ray
//	 */
//	public Ray getRayForPosition(Vector3D position, Side side)
//	{
//		// calculate phi and theta
//		PhiTheta phiTheta = getAnglesForPosition(position, side);
//		
//		// and return the corresponding ray
//		return getRayForAngles(phiTheta.phi, phiTheta.theta, side);
//	}
}
