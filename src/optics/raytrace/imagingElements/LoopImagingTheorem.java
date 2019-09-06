package optics.raytrace.imagingElements;

/**
 * A few methods related to the loop-imaging theorem.
 * @author johannes
 */
public class LoopImagingTheorem
{

	/**
	 * Calculate the focal lengths of lenses L<sub>2</sub> to L<sub>4</sub> of a four-lens intersection satisfying the loop-imaging theorem for the following geometry ("Geometry 1"):
	 * <ul>
	 * <li>All four lenses intersect in a single line.  We choose this to be the <i>z</i> axis of a Cartesian coordinate system.</li>
	 * <li>All principal points, P<sub>1</sub> to P<sub>4</sub>, lie on a straight line that is perpendicular to the lens-intersection line and the plane of lens L<sub>1</sub>.  This line is the <i>y</i> axis.</li>
	 * <li>The lenses intersect the <i>y</i> axis in the order L<sub>1</sub>, L<sub>2</sub>, L<sub>3</sub>, L<sub>4</sub>.</li>
	 * </ul>
	 * @param x0	the distance between principal point of lens 1, P<sub>1</sub>, and the lens-intersection line
	 * @param y2	the <i>y</i> coordinate of the principal point of lens 2, P<sub>2</sub>
	 * @param y3	the <i>y</i> coordinate of the principal point of lens 3, P<sub>3</sub>
	 * @param y4	the <i>y</i> coordinate of the principal point of lens 4, P<sub>4</sub>
	 * @param f1	the focal length f<sub>1</sub> of lens 1
	 * @return	an array of doubles containing the focal lengths {<i>f</i><sub>2</sub>, <i>f</i><sub>3</sub>, <i>f</i><sub>4</sub>}
	 */
	public static double[] getFocalLengthsForFourLensIntersectionGeometry1(double x0, double y2, double y3, double y4, double f1)
	{
		double
			y1 = 0,
			y43 = y4 - y3,
			y42 = y4 - y2,	// 4,2
			y41 = y4 - y1,
			y32 = y3 - y2,
			y31 = y3 - y1,
			y21 = y2 - y1,			
			s4 = Math.sqrt(x0*x0 + y4 *y4),
			s3 = Math.sqrt(x0*x0 + y3 *y3),
			s2 = Math.sqrt(x0*x0 + y2*y2),
			s1 = Math.sqrt(x0*x0 + y1*y1);

			
		double[] f234 = new double[3];
		
		// f_2, which is f_10 in the reference
		f234[0] = -f1*s1*y42*y32/(s2*y41*y31) + x0*y32*y21/(s2*y31);
		
		// f_3, which is f_9 in the reference
		f234[1] = f1*s1*y43*y32/(s3*y41*y21);
		
		// f_4, which is f_8 in the reference
		f234[2] = -f1*s1*y43*y42/(s4*y31*y21) + x0*y43*y41/(s4*y31);
		
		return f234;
	}
	
}
