package optics.raytrace.research.skewLensImaging;

import math.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * @author johannes
 * A class that facilitates dealing with combinations of two skew lenses
 */
public class TwoLensCombo {
	/**
	 * principal point of first lens
	 */
	protected Vector3D p1;

	/**
	 * principal point of 2nd lens
	 */
	protected Vector3D p2;

	/**
	 * normalised normal to the first lens (pointing in the direction of object space)
	 */
	protected Vector3D n1;

	/**
	 * normalised normal to the 2nd lens (pointing in the direction of object space)
	 */
	protected Vector3D n2;

	/**
	 * focal length of first lens
	 */
	protected double f1;
	
	/**
	 * focal length of 2nd lens
	 */
	protected double f2;
	
	/**
	 * @author johannes
	 * The optical-axis direction is defined to be positive when it points from lens-1-side space to lens-2-side space.
	 * In the "normal" case, when the physical parts of the lenses contain the principal points, this is the direction from p1 to p2.
	 * However, it can also happen that the positive optical-axis direction is in the direction from p2 to p1.
	 * An example for when this is the case is when none of the physical parts of both lenses contains the principal points,
	 * and the principal-point positions are on the other side of the line where the lenses intersect.
	 */
	public enum OpticalAxisSense { P1_TO_P2, P2_TO_P1 }
	
	/**
	 * The optical-axis direction is positive in the direction from p1 to p2 if the value is P1_TO_P2;
	 * it is positive in the direction from p2 to p1 if the value is P2_TO_P1.
	 */
	protected OpticalAxisSense opticalAxisSense;

	/**
	 * Default constructor; directly set all variables
	 * @param p1	principal point of lens 1
	 * @param p2	principal point of lens 2
	 * @param n1	normal to lens 1 (pointing in the direction of object space)
	 * @param n2	normal to lens 2 (pointing in the direction of object space)
	 * @param f1	focal length of lens 1
	 * @param f2	focal length of lens 2
	 * @param opticalAxisSense	optical-axis direction is positive in the direction from p1 to p2 if the value is P1_TO_P2, it is positive in the direction from p2 to p1 if the value is P2_TO_P1.
	 */
	public TwoLensCombo(Vector3D p1, Vector3D p2, Vector3D n1, Vector3D n2, double f1, double f2, OpticalAxisSense opticalAxisSense)
	{
		super();
		
		setP1(p1);
		setP2(p2);
		setN1(n1);
		setN2(n2);
		setF1(f1);
		setF2(f2);
		setOpticalAxisSense(opticalAxisSense);
	}
	
	/**
	 * @author johannes
	 * Allows selection of the space on the side of lens 1 or lens 2.
	 */
	public enum TwoLensComboSpace { LENS1SIDE, LENS2SIDE }
	
	/**
	 * @author johannes
	 * Allows selection of the meaning of the argument ff in the relevant TwoLensCombo constructor
	 */
	public enum TwoLensComboFFMeaning { F, F1, F2, F1F2RATIO }
	
	// I had originally written this constructor differently; in case I am changing back, here are
	// a few of the relevant bits I took out
//	 * @param normalToTransverseDirection	normal to transverse direction either in lens-1-side space or in lens-2-side space, depending on value of <normalToTransverseDirectionSpace>
//	 * @param normalToTransverseDirectionSpace	determines the space <normalToTransverseDirection> refers to
//	 * @param ff	either f (focal length of combination), f1 (focal length of lens 1), f2 (focal length of lens 2), or f1/f2, depending on value of <ffMeaning>
//	 * @param ffMeaning	determines the meaning of the parameter <ff>
//	TwoLensComboSpace normalToTransverseDirectionSpace,
//	double ff,
//	TwoLensComboFFMeaning ffMeaning
//	switch(ffMeaning)
//	{
//	case F1:
//		setF1(ff);
//		// TODO set and calculate f2
//		break;
//	case F2:
//		setF2(ff);
//		// TODO set and calculate f1
//		break;
//	case F1F2RATIO:
//		// TODO calculate and set f1 and f2
//		break;
//	case F:
//	default:
//		// f is the distance from the principal point of the combo to the focal point of the combo.  This is the case
//		// both in lens-1-sided space and lens-2-sided space.
//		// Calculate first the principal points, then the focal points.
//		
//	}

	/**
	 * Constructs a two-lens combo from a list of properties of the lens combo.
	 * @param pointOnLens1	a point on lens 1 (not necessary the principal point, p1)
	 * @param pointOnLens2	a point on lens 2 (not necessary the principal point, p2)
	 * @param normalToLens1	normal to lens 1
	 * @param normalToLens2	normal to lens 2
	 * @param pointOnOpticalAxis	a point on the optical axis of the two-lens combo (which passes through p1 and p2)
	 * @param positiveOpticalAxisDirection	vector in positive direction of optical axis (pointing from lens-1-side to lens-2-side)
	 * @param normalToTransverseDirection1
	 * @param normalToTransverseDirection2
	 */
	public TwoLensCombo(
			Vector3D pointOnLens1,
			Vector3D pointOnLens2,
			Vector3D normalToLens1,
			Vector3D normalToLens2,
			Vector3D pointOnOpticalAxis,
			Vector3D positiveOpticalAxisDirection,
			Vector3D normalToTransverseDirection1,
			Vector3D normalToTransverseDirection2
		)
	{
		super();
		
		// calculate the principal point of lens 1 as the intersection of the plane of lens 1 with the optical axis
		setP1(Geometry.uniqueLinePlaneIntersection(
				pointOnOpticalAxis,	// pointOnLine
				positiveOpticalAxisDirection,	// directionOfLine
				pointOnLens1,	// pointOnPlane
				normalToLens1	// normalToPlane
			));

		// calculate the principal point of lens 2 as the intersection of the plane of lens 2 with the optical axis
		setP2(Geometry.uniqueLinePlaneIntersection(
				pointOnOpticalAxis,	// pointOnLine
				positiveOpticalAxisDirection,	// directionOfLine
				pointOnLens2,	// pointOnPlane
				normalToLens2	// normalToPlane
			));
		
		// if p2-p1 points in <positiveOpticalAxisDirection>, set <opticalAxisSense> to P1_TO_P2, otherwise to P2_TO_P1
		setOpticalAxisSense((Vector3D.scalarProduct(
				Vector3D.difference(p2, p1),
				positiveOpticalAxisDirection
			) > 0)?OpticalAxisSense.P1_TO_P2:OpticalAxisSense.P2_TO_P1);

		// make sure the normal to lens 1 points in the positive optical-axis half-space
		setN1(normalToLens1.getProductWith(Vector3D.scalarProduct(normalToLens1, positiveOpticalAxisDirection)));

		// make sure the normal to lens 2 points in the positive optical-axis half-space
		setN2(normalToLens2.getProductWith(Vector3D.scalarProduct(normalToLens2, positiveOpticalAxisDirection)));
		
		// The principal planes pass through the line where the two lenses intersect, but as the principal planes
		// are transverse planes (and therefore are parallel to all other transverse planes), the lens-intersection line
		// must be parallel to the direction of all transverse planes.
		// This means the normals to the transverse planes must be perpendicular to the lens-intersection line.
		Vector3D lensIntersectionLineDirection = getDirectionOfLensIntersectionLine();
		
		// calculate the part of <normalToTransverseDirection1> that is perpendicular to <lensIntersectionLineDirection>
		Vector3D t1 = normalToTransverseDirection1.getPartPerpendicularTo(lensIntersectionLineDirection);
		
		// same with <normalToTransverseDirection2>
		Vector3D t2 = normalToTransverseDirection2.getPartPerpendicularTo(lensIntersectionLineDirection);
		
		// Next, we need to calculate the focal lengths of the two lenses.
		// These can be calculated from the position of any point I_F on the line L where the "inner" focal planes intersect.
		// We define a plane P such that it contains the optical axis and such that it is perpendicular to the part of the
		// direction of L that is perpendicular to the optical-axis direction (this plane is then "as perpendicular as possible"
		// to L while at the same time containing the optical axis).
		
		// first we calculate the normal to the plane P
		Vector3D nP = lensIntersectionLineDirection.getPartPerpendicularTo(getOpticalAxisDirection());
		
		// the direction from p1 to I_F is then given by
		Vector3D d1 = Vector3D.crossProduct(t1, nP);
		
		// similarly
		Vector3D d2 = Vector3D.crossProduct(t2, nP);
		
		// the point I_F is then at the intersection point between the line through p1 with direction d1 and that
		// through p2 with direction d2
		try {
			Vector3D iF = Geometry.lineLineIntersection(
					p1,	// pointOnLine1
					d1,	// directionOfLine1
					p2,	// pointOnLine2
					d2	// directionOfLine2
				);

			// The distance from iF to the plane of lens 1 is |f1|, the distance from iF to the plane of lens 2 is |f2|;
			// the signs of f1 and f2 are determined by 
			setF1(Geometry.planePointDistance(
					p1,	// pointOnPlane
					n1,	// planeNormal
					iF	// point
				));
			setF2(Geometry.planePointDistance(
					p2,	// pointOnPlane
					n2,	// planeNormal
					iF	// point
				));
		} catch (RayTraceException e) {
			// something is terribly wrong if we end up here
			e.printStackTrace();
		}		
	}

	
	//
	// setters & getters
	//
		
	public Vector3D getP1() {
		return p1;
	}

	public void setP1(Vector3D p1) {
		this.p1 = p1;
	}

	public Vector3D getP2() {
		return p2;
	}

	public void setP2(Vector3D p2) {
		this.p2 = p2;
	}

	public Vector3D getN1() {
		return n1;
	}

	public void setN1(Vector3D n1) {
		this.n1 = n1.getNormalised();
	}

	public Vector3D getN2() {
		return n2;
	}

	public void setN2(Vector3D n2) {
		this.n2 = n2.getNormalised();
	}

	public double getF1() {
		return f1;
	}

	public void setF1(double f1) {
		this.f1 = f1;
	}

	public double getF2() {
		return f2;
	}

	public void setF2(double f2) {
		this.f2 = f2;
	}
	
	public OpticalAxisSense getOpticalAxisSense() {
		return opticalAxisSense;
	}


	public void setOpticalAxisSense(OpticalAxisSense opticalAxisSense) {
		this.opticalAxisSense = opticalAxisSense;
	}

	
	//
	// useful methods
	//
	

	/**
	 * @return	the focal length of the 2-lens combination
	 */
	public double getFocalLength()
	{
		Vector3D opticalAxisDirection = getOpticalAxisDirection();
		// effective focal length of lens 1 along optical-axis direction
		double F1 = f1/Vector3D.scalarProduct(n1, opticalAxisDirection);
		// effective focal length of lens 2 along optical-axis direction
		double F2 = f2/Vector3D.scalarProduct(n2, opticalAxisDirection);
		// separation between the principal points of the two lenses
		double D = Vector3D.getDistance(p1, p2);
		
		// Jakub's formula for the combined focal length
		return (F1*F2)/(F1+F2-D);
	}
	
	/**
	 * @return	the normalised direction of the optical axis (which passes through p1 and p2), with direction from p1 to p2 or p2 to p1, depending on the value of <opticalAxisSense>
	 */
	public Vector3D getOpticalAxisDirection()
	{
		return Vector3D.difference(p2, p1).getWithLength((opticalAxisSense==OpticalAxisSense.P1_TO_P2)?1:-1);
	}
	
	/**
	 * @return	a normalised normal to the transverse planes on the side of lens 1
	 */
	public Vector3D getNormalToTransversePlanes1()
	{
		// first find a point I_F on the line where the image-sided focal plane of lens 1
		// and the object-sided focal plane of lens 2 intersect
		Vector3D iF = Geometry.pointOnPlanePlaneIntersection(
				Vector3D.sum(p1, n1.getWithLength(f1)),	// point on the image-sided focal plane of lens 1
				n1,
				Vector3D.sum(p2, n2.getWithLength(-f2)),	// point on the object-sided focal plane of lens 2
				n2,
				p1	// find the point closest to p1
			);
		return Vector3D.crossProduct(
				Vector3D.difference(iF, p1),
				getDirectionOfLensIntersectionLine()
			).getNormalised();
	}
	
	/**
	 * @return	a normalised normal to the transverse planes on the side of lens 2
	 */
	public Vector3D getNormalToTransversePlanes2()
	{
		// first find a point I_F on the line where the image-sided focal plane of lens 1
		// and the object-sided focal plane of lens 2 intersect
		Vector3D iF = Geometry.pointOnPlanePlaneIntersection(
				Vector3D.sum(p1, n1.getWithLength(f1)),	// point on the image-sided focal plane of lens 1
				n1,
				Vector3D.sum(p2, n2.getWithLength(-f2)),	// point on the object-sided focal plane of lens 2
				n2,
				p1	// find the point closest to p1
			);
		return Vector3D.crossProduct(
				Vector3D.difference(iF, p2),
				getDirectionOfLensIntersectionLine()
			).getNormalised();
	}
	
	/**
	 * @return	the point on both principal planes that is closest to the point p
	 */
	public Vector3D getPointOnPrincipalPlanes(Vector3D p)
	{
		// return the point closest to p1
		return Geometry.pointOnPlanePlaneIntersection(p1, n1, p2, n2, p);
	}

	/**
	 * @return	the point on both principal planes that is closest to p1, the principal point of the first lens
	 */
	public Vector3D getPointOnPrincipalPlanes()
	{
		return getPointOnPrincipalPlanes(p1);
	}

	/**
	 * @return	a unit vector in the direction of the line where the two lens planes intersect
	 */
	public Vector3D getDirectionOfLensIntersectionLine()
	{
		return Vector3D.crossProduct(n1, n2).getNormalised();
	}
	
	/**
	 * @return	the point on focal plane 1 (i.e. the focal plane on the side of lens 1) that is closest to p
	 */
	public Vector3D getPointOnFocalPlane1(Vector3D p)
	{
		// return the point closest to p1
		return Geometry.pointOnPlanePlaneIntersection(p1, n1, Vector3D.sum(p2, n2.getProductWith(-f2)), n2, p);
	}

	/**
	 * @return	the point on focal plane 1 (i.e. the focal plane on the side of lens 1) that is closest to p1
	 */
	public Vector3D getPointOnFocalPlane1()
	{
		return getPointOnFocalPlane1(p1);
	}

	/**
	 * @return	the point on focal plane 2 (i.e. the focal plane on the side of lens 2) that is closest to p
	 */
	public Vector3D getPointOnFocalPlane2(Vector3D p)
	{
		return Geometry.pointOnPlanePlaneIntersection(Vector3D.sum(p1, n1.getProductWith(f1)), n1, p2, n2, p);
	}
	
	/**
	 * @return	the point on focal plane 2 (i.e. the focal plane on the side of lens 2) that is closest to p2
	 */
	public Vector3D getPointOnFocalPlane2()
	{
		// return the point closest to p2
		return getPointOnFocalPlane2(p2);
	}
}
