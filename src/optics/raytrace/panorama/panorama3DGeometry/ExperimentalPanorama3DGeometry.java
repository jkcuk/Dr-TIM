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
 * The modification is experimental
 */
public class ExperimentalPanorama3DGeometry extends StandardPanorama3DGeometry
{
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
		
		Vector3D perp = Vector3D.crossProduct(theta0, d);
		
		// the direction from the midpoint to the right eye is then theta0 x d (it's a left-handed coordinate system!),
		// i.e. the camera position is
		Vector3D c = Vector3D.sum(
				M,	// midpoint
				Vector3D.crossProduct(perp, d).getWithLength(0.5*interpupillaryDistance*side.toFactor())
			);
		
		// the ray from the camera position in the direction d is then
		return new Ray(c, d, 0);	// start time = 0
	}
}
