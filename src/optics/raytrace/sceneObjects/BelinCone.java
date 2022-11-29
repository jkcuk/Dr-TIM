package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * @author johannes, Jakub
 * 
 * A cone-like surface that forms part of a generalised Fresnel lens.
 * The main parts of the Fresnel lens's surface are formed by a <i>LensSurface</i>, which refracts the light rays to do the required imaging.
 * This cone-like surface forms the steps in the Fresnel lens.
 * Note that the Belin cone that corresponds to a lens surface with a negative focal length is "inside out", which means we can use the same
 * "recipe" to construct a Fresnel-lens surface irrespective of the sign of the focal length
 * 
 * @see optics.raytrace.sceneObjects.LensSurface
 */
public class BelinCone extends SceneObjectPrimitive
implements Serializable
{
	private static final long serialVersionUID = -7824752470402496706L;

	/**
	 * point in contour plane
	 */
	protected Vector3D pointInContourPlane;

	/**
	 * normal to the contour plane, which defines the direction of the <i>t</i> axis;
	 * from this, the internal variable <i>tHat</i> is calculated, which is normalised and points "outwards"
	 */
	protected Vector3D contourNormal;

	/**
	 * the lens surface
	 */
	protected LensSurface lensSurface;	
	
	
	// private variables
	
	// lens-surface variables, re-named
	private double f;	// focal length...
	private double f2;	// ... and its square
	private double n;	// refractive index...
	private double n2;	// ... and its square
	private double nM1;	// (n-1)
	private double nM12;	// (n-1)^2
	private double n2M1;	// (n^2 - 1)
	
	// All calculations are done in the Cartesian (r, s, t) coordinate system, defined as follows.
	// The origin is at <i>apex</i>.
	// <i>tHat</i> is the unit vector in the <i>t</i> direction, which is normal to the contour plane and pointing outwards.
	// <i>rHat</i> and <i>sHat</i> are the unit vectors describing the other two directions.
	private Vector3D apex;
	private Vector3D rHat;
	private Vector3D sHat;
	private Vector3D tHat;
	
	// the t component of the contour plane, E (and its square)
	private double tE;
	private double tE2;
	
	// the lens surface is described in the Cartesian (u, v, w) coordinate system;
	// w coordinate of the apex
	private double wA;
	private double wA2;
	
	// wHat.tHat, i.e. the w component of the vector tHat
	private double wTHat;
	private double wTHat2;
	// vHat.tHat, i.e. the v component of the vector tHat
	private double vTHat;	// sin alpha
	private double vTHat2;
	
	
	//
	// constructors
	// 
	
	/**
	 * @param description
	 * @param pointInContourPlane
	 * @param contourNormal
	 * @param lensSurface
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public BelinCone(
			String description,
			Vector3D pointInContourPlane,
			Vector3D contourNormal,
			LensSurface lensSurface,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
	)
	{
		super(
				description,
				surfaceProperty,
				parent, studio
			);
		
		setPointInContourPlane(pointInContourPlane);
		setContourNormal(contourNormal);
		setLensSurface(lensSurface);
		
		precalculateInternalVariables();
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public BelinCone(BelinCone original)
	{
		this(
				original.getDescription(),
				original.getPointInContourPlane(),
				original.getContourNormal(),
				original.getLensSurface(),
				original.getSurfaceProperty(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public BelinCone clone()
	{
		return new BelinCone(this);
	}
	
	@Override
	public BelinCone transform(Transformation t)
	{
		return new BelinCone(
				description,
				t.transformPosition(getPointInContourPlane()),
				t.transformDirection(getContourNormal()),
				getLensSurface().transform(t),
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}



	//
	// getters and setters
	//

	public Vector3D getPointInContourPlane() {
		return pointInContourPlane;
	}
//	private Vector3D apex;
//	private Vector3D rHat;
//	private Vector3D sHat;
//	private Vector3D tHat;
	public Vector3D getRHat() {
		return rHat;
	}
	
	public Vector3D getSHat() {
		return sHat;
	}
	
	public Vector3D getTHat() {
		return tHat;
	}
	
	public Vector3D getApex() {
		return apex;
	}
	/**
	 * make sure to call precalculateRSTCoordinateSystem() after setting
	 * @param pointInContourPlane
	 */
	public void setPointInContourPlane(Vector3D pointInContourPlane) {
		this.pointInContourPlane = pointInContourPlane;
	}

	public Vector3D getContourNormal() {
		return contourNormal;
	}

	/**
	 * Set this.contourNormal to <i>contourNormal</i>
	 * @param contourNormal
	 */
	public void setContourNormal(Vector3D contourNormal) {
		this.contourNormal = contourNormal;
	}

	public LensSurface getLensSurface() {
		return lensSurface;
	}

	/**
	 * make sure to call precalculateRSTCoordinateSystem() after setting
	 * @param lensSurface
	 */
	public void setLensSurface(LensSurface lensSurface) {
		this.lensSurface = lensSurface;
	}


	/**
	 * call this after all the other variables have been set
	 */
	public void precalculateInternalVariables()
	{
		f = lensSurface.getFocalLength();	// focal length...
		f2 = f*f;	// ... and its square
		n = lensSurface.getRefractiveIndex();	// refractive index...
		n2 = n*n;	// ... and its square
		nM1 = n-1;	// (n-1)
		nM12 = nM1*nM1;	// (n-1)^2
		n2M1 = n2-1;	// (n^2 - 1)

		Vector3D wHat = lensSurface.getOpticalAxisDirectionOutwards();

		// tHat points in the direction of the contour normal, outwards;
		// to see if <i>contourNormal</i> points inwards or outwards, calculate its scalar product with lensSurface.wHat, which also points outwards;
		// contourNormal should already be normalised, so it just needs to be calculated with the sign of wHat.contourNormal
		tHat = contourNormal.getWithLength(Math.signum(Vector3D.scalarProduct(wHat, contourNormal)));
		
//		System.out.println(
//				"BelinCone::precalculateRSTCoordinateSystem: Vector3D.crossProduct(lensSurface.getOpticalAxisDirectionOutwards(), tHat)="+
//				Vector3D.crossProduct(lensSurface.getOpticalAxisDirectionOutwards(), tHat)
//			);
		Vector3D rAxis = Vector3D.crossProduct(wHat, tHat);
		// it can be the case that <i>tHat</i> is parallel to the lens surface's optical-axis direction, in which case <i>rAxis</i> = 0
		if(rAxis.getModSquared() == 0.0)
			// rAxis = 0; take any normal to tHat for the r direction
			rHat = Vector3D.getANormal(tHat);
		else
			// normalise rAxis and take this as the unit vector in the r direction
			rHat = rAxis.getNormalised();
		sHat = Vector3D.crossProduct(tHat, rHat);	// should already be normalised
		
		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: rHat="+rHat+", sHat="+sHat+", tHat="+tHat);
		
		Vector3D uHat = rHat;
		Vector3D vHat = Vector3D.crossProduct(wHat, uHat);
		
		// calculate the principal point, where the lens surface's optical axis intersects the contour plane, ...
		Vector3D principalPoint = Geometry.uniqueLinePlaneIntersection(
				lensSurface.getFocalPoint(),	// pointOnLine
				wHat,	// directionOfLine
				pointInContourPlane,	// pointOnPlane
				tHat	// normalToPlane
			);
		// alternative: take the principal point to be the lens surface's principal point
		// Vector3D principalPoint = lensSurface.calculatePrincipalPoint();
		// ...  and the apex position
		apex = Vector3D.sum(
				principalPoint,
				Vector3D.difference(lensSurface.getFocalPoint(), principalPoint).getProductWith(2)
			);
		
		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: apex="+apex);
		
		// w coordinate of the apex
		wA = Vector3D.scalarProduct(Vector3D.difference(apex, lensSurface.getFocalPoint()), wHat);
		wA2 = wA*wA;
		
		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: wA="+wA);
		
		// wHat.tHat, i.e. the w component of the vector tHat
		wTHat = Vector3D.scalarProduct(tHat, wHat);	// cos alpha
		wTHat2 = wTHat*wTHat;
		// vHat.tHat, i.e. the v component of the vector tHat
		vTHat = Vector3D.scalarProduct(tHat, vHat);	// -Math.sqrt(1-wTHat2);	// sin alpha
		vTHat2 = vTHat*vTHat;
		
		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: wTHat="+wTHat+", vTHat="+vTHat);
		
		// the t component of the contour plane, E
		tE = Vector3D.scalarProduct(Vector3D.difference(pointInContourPlane, apex), tHat);
		tE2 = tE*tE;

		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: tE="+tE);
		
		// System.out.println("BelinCone::precalculateRSTCoordinateSystem: rHat="+rHat+", sHat="+sHat+", tHat="+tHat+", apex="+apex);
	}

	//
	// SceneObjectPrimitive methods
	//
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// points on the ray are parametrised in the form P + t*D
				
		// first, write the light-ray direction in the (r, s, t) basis...
		double rD = Vector3D.scalarProduct(ray.getD(), rHat);
		double sD = Vector3D.scalarProduct(ray.getD(), sHat);
		double tD = Vector3D.scalarProduct(ray.getD(), tHat);
		double rD2 = rD*rD, sD2 = sD*sD, tD2 = tD*tD;
		
		// System.out.println("BelinCone::getClosestRayIntersection: ray.getD().x="+ray.getD().x);
		// if(Double.isNaN(ray.getD().x)) new RayTraceException("Ray with NaN direction?!").printStackTrace();
		
		// System.out.println("BelinCone::getClosestRayIntersection: ray.getD()="+ray.getD());
		// System.out.println("BelinCone::getClosestRayIntersection: rD="+rD+", sD="+sD+", tD="+tD);

		// ... and the ray's start position
		Vector3D f2p = Vector3D.difference(ray.getP(), apex);
		double rP = Vector3D.scalarProduct(f2p, rHat);
		double sP = Vector3D.scalarProduct(f2p, sHat);
		double tP = Vector3D.scalarProduct(f2p, tHat);
		double rP2 = rP*rP, sP2 = sP*sP, tP2 = tP*tP;
		
		// System.out.println("BelinCone::getClosestRayIntersection: ray.getP()="+ray.getP());
		// System.out.println("BelinCone::getClosestRayIntersection: rP="+rP+", sP="+sP+", tP="+tP);
		
		// the solutions for t are of the form (-b +/- sqrt(b^2 - 4 a c))/(2 a)
		// see FresnelLens.nb		
		double a = f2*nM12*tD2 - rD2*tE2 - 2*sD*tD*tE*vTHat*(n2M1*wA + n2*tE*wTHat) + 2*f*n*nM1*tD*(-(sD*tE*vTHat) + tD*(wA + tE*wTHat)) + 
				   sD2*tE2*(n2M1*vTHat2 - wTHat2) + tD2*(n2M1*wA2 + tE*(-(tE*vTHat2) + 2*n2M1*wA*wTHat + n2M1*tE*wTHat2));
		double b = 2*(-(rD*rP*tE2) + f2*nM12*tD*tP - sD*sP*tE2*vTHat2 + n2*sD*sP*tE2*vTHat2 - tD*tE2*tP*vTHat2 + sP*tD*tE*vTHat*wA - 
			     n2*sP*tD*tE*vTHat*wA + sD*tE*tP*vTHat*wA - n2*sD*tE*tP*vTHat*wA - tD*tP*wA2 + n2*tD*tP*wA2 - 
			     tE*(n2*tE*(sP*tD + sD*tP)*vTHat - 2*n2M1*tD*tP*wA)*wTHat - f*n*nM1*(sP*tD*tE*vTHat + sD*tE*tP*vTHat - 2*tD*tP*(wA + tE*wTHat)) - 
			     tE2*(sD*sP - n2M1*tD*tP)*wTHat2);
		double c = -(rP2*tE2) + f2*nM12*tP2 - sP2*tE2*vTHat2 + n2*sP2*tE2*vTHat2 - tE2*tP2*vTHat2 + 2*sP*tE*tP*vTHat*wA - 2*n2*sP*tE*tP*vTHat*wA - 
				   tP2*wA2 + n2*tP2*wA2 - 2*tE*tP*(tP*wA + n2*(sP*tE*vTHat - tP*wA))*wTHat + 2*f*n*nM1*tP*(-(sP*tE*vTHat) + tP*wA + tE*tP*wTHat) - 
				   tE2*(sP2 + tP2 - n2*tP2)*wTHat2;
		
		// System.out.println("BelinCone::getClosestRayIntersection: a="+a+", b="+b+", c="+c);
		
		// calculate the discriminant to check if there is a solution
		double discriminant = b*b-4*a*c;
		
		if(discriminant < 0)
		{
			// there is no solution, and so no intersection with this surface
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}
		
		// calculate the two solutions and check which one, if any, is the relevant one
		// NOTE THAT THESE ts ARE NOT THE t COORDINATES OF THE INTERSECTION POINT IN THE (t, r, s) COORDINATE SYSTEM!
		double sqrtDiscriminant = Math.sqrt(discriminant);
		double t1 = (-b - sqrtDiscriminant)/(2*a);
		double t2 = (-b + sqrtDiscriminant)/(2*a);
		
		// first sort the solutions by size
		double[] t = new double[2];
		t[0] = Math.min(t1, t2);
		t[1] = Math.max(t1, t2);
		
		// To find the value of t that corresponds to the solution of interest, go through them all, starting with the smallest, 
		// and find the first for which t>0 and the intersection is with the correct "branch" of the surface.
		// So, go through all the values of t, starting with the smallest ...
		for(int i=0; i<2; i++)
		{
			if(t[i] > 0)
			{
					// found an intersection!
					Ray rayAtIntersectionPoint = ray.getAdvancedRay(t[i]);
					//return this intersection only if it is on the lens side of the apex ( as defined by getApproximateConeAxis() )
					if(Vector3D.scalarProduct(Vector3D.difference(rayAtIntersectionPoint.getP(), apex), getApproximateConeAxis()) > 0) {
					return new RaySceneObjectIntersection(
							rayAtIntersectionPoint.getP(),
							this,
							rayAtIntersectionPoint.getT()
						);
					}
			}
		}
		
		// the two solutions for t do not correspond to an intersection of the ray and the cone as they either correspond to 
		// intersections on the straight line of the ray but behind the ray's start position
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}

	/**
	 * The cone surface is given by the function coneFunction == 0
	 * @param r
	 * @param s
	 * @param t
	 * @param f
	 * @param n
	 * @return
	 */
/*	private double coneFunction(double r, double s, double t) //Vestigial, was used by the previous version of insideObject
	{
		// see FresnelLens.nb
		return -f + ((s*tE*vTHat - t*(wA + tE*wTHat))*(n - Math.sqrt(1 + (tE2*(r*r + MyMath.square(t*vTHat + s*wTHat)))/
		           MyMath.square(s*tE*vTHat - t*(wA + tE*wTHat)))))/(nM1*t);
	}
	*/
	/**
	 * a function for the intersection of the lens plane with the lens surface in the form of 2ndOrderPlynomial(r,s) = -1 or rather
	 * 1 + 2ndOrderPlynomial(r,s) = 0. For a point in the contour plane INSIDE the contour the left hand side evaluates to < 0. c.f. FresnelLens.nb
	 * @param r
	 * @param s
	 * @return the LHS of 1 + 2ndOrderPlynomial(r,s) = 0. Where LHS = 0 defines the contour, LHS < 0 means the point is inside the contour, LHS >0 means that the point is outside the contour
	 */
	private double contourFunction(double r, double s) 
	{
		return 1 + ((1 + n)*r*r)/(nM1*MyMath.square(f)) + ((1 + n)*MyMath.square(tE*vTHat + s*wTHat))/(nM1*MyMath.square(f)) - (MyMath.square(1 + n)*MyMath.square((f*n)/(1 + n) - s*vTHat + wA + tE*wTHat))/MyMath.square(f);
	}
	
	/**
	 * The intersection of the lens plane with the lens surface is a conic section which in the r=0 plane consists of two points (s0 + S, s0 - S).
	 * @return  This function returns s0. c.f. FresnelLens.nb
	 */
	public double getSCoorinateBetweenVerticesOfHyperbola()
	{
		return (vTHat*(f*n*nM1 + n2M1*wA + n2*tE*wTHat))/(n2M1*vTHat2 - wTHat2);
	}
	
	/**
	 * A function for determining whether the contour (of the lens surface in the lens/countour plane is a hyperbola or an ellipse.  see FresnelLens.nb
	 * @return true if intersection is a hyperbola and false if it is an ellipse
	 */
	public boolean isIntersectionAHyperbola() {
		return ((1 + n)*(-(n2M1*vTHat2) + wTHat2))/(2.*nM1*MyMath.square(f)) < 0;
	}
	/**
	 * If isIntersectionAHyperbola()==false then this points from the cone apex to (r=0, s=s0, t=tE).
	 * If isIntersectionAHyperbola()==true then it returns rHat x (r=0, s=s0, t=tE) (x = cross product)
	 * The plane to which this axis is perpendicular separates the two cones from each other at the apex.
	 * If for a point p Vector3D.scalarProduct(Vector3D.difference(p, apex), approximateConeAxis) > then that point
	 * is said to be on the lens side of the apex. 
	 * @return This returns only an approximate cone axis based on the getSCoorinateBetweenVerticesOfHyperbola().
	 * However the plane to which this axis is perpendicular separates the two cones from each other at the apex.
	 */
	public Vector3D getApproximateConeAxis() {
		//the apex lies at the origin of the (r,s,t) coordinate system so its r-coordinate is zero. The vector pointing to approximately
		//half way between the vertices of the conic section in the r=0 plane is then
		Vector3D apexToHalfWayPoint = Vector3D.sum(tHat.getProductWith(tE), sHat.getProductWith(getSCoorinateBetweenVerticesOfHyperbola()));
		Vector3D approximateConeAxis = null;
		//if the contour is a hyperbola then this apxToHalfWayPoint is perpendicular to the approximate axis
		if(isIntersectionAHyperbola()==true) {
			approximateConeAxis = Vector3D.crossProduct(apexToHalfWayPoint, rHat).getNormalised();//Vector3D.sum(tAxis.getProductWith(cos2), sAxis.getProductWith(sin2)).getNormalised();					
		//if the contour is an ellipse then apexToHalfWayPoint IS the approximate axis
		} else {	
			approximateConeAxis = apexToHalfWayPoint.getNormalised();
		}
		return approximateConeAxis;
	}
	
	@Override
	public boolean insideObject(Vector3D p) {
/*		//the next 18 lines are the old version that, with the hack fix, works to a good approximation. 
		// the surface is given by the equation (optical path length via point on surface - f) == 0,
		// so calculate the LHS of that equation and check if it is greater than 0 or less than 0

		// convert the position p into the (r,s,t) coordinate system
		Vector3D f2p = Vector3D.difference(p, apex);
		double r = Vector3D.scalarProduct(f2p, rHat);
		double s = Vector3D.scalarProduct(f2p, sHat);
		double t = Vector3D.scalarProduct(f2p, tHat);
		//the next 8 lines are a hack fix (doesn't work entirely)
		Vector3D wHat = lensSurface.getOpticalAxisDirectionOutwards().getNormalised();
		double sin = Vector3D.crossProduct(tHat, wHat).getLength();
		double cos = Math.abs(Vector3D.scalarProduct(tHat, wHat));
		double wOfPoint = Math.abs(Vector3D.scalarProduct(wHat, f2p));
		double uOrVofPoint = Math.abs(Vector3D.scalarProduct(Vector3D.crossProduct(rHat, wHat), p));
//		if (wOfPoint <= sin*uOrVofPoint/cos) {
//			isPointInside = !isPointInside;
//		}
		return (coneFunction(r, s, t) > 0.0 && cos*wOfPoint > sin*uOrVofPoint) || (coneFunction(r, s, t) < 0.0 && cos*wOfPoint < sin*uOrVofPoint);
*/		
		//take the continuation of the line from the apex to the point in question and find its intersection with the lens/contour plane
		Vector3D apexToPoint = Vector3D.difference(p, apex);
		Vector3D stereographicProjectionToContourPlane = Geometry.uniqueLinePlaneIntersection(
					apex, //pointOnLine,
					apexToPoint, //directionOfLine,
					pointInContourPlane, //pointOnPlane,
					contourNormal //normalToPlane
				);
		//the (r,s) coordinates of this point are
		double rEprime = Vector3D.scalarProduct(Vector3D.difference(stereographicProjectionToContourPlane,apex), rHat);
		double sEprime = Vector3D.scalarProduct(Vector3D.difference(stereographicProjectionToContourPlane,apex), sHat);
		//whether this should be considered inside or outside the cone depends on which side of the lens we are on (w.r.t. lens normal = tHat)
		//whichSideOfLens > 0 means we are on the side of the lens towards which the lens normal (parallel to tHat) and the optical axis point
		double whichSideOfLens = Vector3D.scalarProduct(tHat, Vector3D.difference(apex,pointInContourPlane));
		//The cone emanates from its apex in two directions, we only care about the one that points towards/engulfs the lens centre
		double whichHalfCone = Vector3D.scalarProduct(apexToPoint, getApproximateConeAxis());
		
		//If the point in question and the apex are in front of the lens (side to which lens normal points), then it is considered inside the Belin cone if it is inside the cone on the lens side of the apex.
		return (whichSideOfLens > 0 && (whichHalfCone > 0  && (contourFunction(rEprime, sEprime) < 0 ))) 
				//if the point and apex are behind the lens then the point in question is to be considered inside the Belin cone if it is outside the cone on the lens side of the apex or it is not on the lens side of the apex.
				||  (whichSideOfLens < 0 && (contourFunction(rEprime, sEprime) > 0 || whichHalfCone < 0)); 
	}


	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p) {
		// convert the position p into the (r,s,t) coordinate system
		Vector3D f2p = Vector3D.difference(p, apex);
		double r = Vector3D.scalarProduct(f2p, rHat);
		double s = Vector3D.scalarProduct(f2p, sHat);
		double t = Vector3D.scalarProduct(f2p, tHat);
		double r2 = r*r;

		// see FresnelLens.nb
		double j = (tE2*(r2 + MyMath.square(t*vTHat + s*wTHat)))/MyMath.square(s*tE*vTHat - t*(wA + tE*wTHat));
		double sJ1 = Math.sqrt(1 + j);
				
		// calculate the r, s and t components
		double rGrad = -((r*tE2)/(nM1*sJ1*t*(s*tE*vTHat - t*(wA + tE*wTHat))));
		double sGrad = ((n - sJ1)*tE*vTHat)/(nM1*t) - ((s*tE*vTHat - t*(wA + tE*wTHat))*
			      ((2*tE2*wTHat*(t*vTHat + s*wTHat))/MyMath.square(s*tE*vTHat - t*(wA + tE*wTHat)) - 
			    	        (2*vTHat*(r2 + MyMath.square(t*vTHat + s*wTHat))*MyMath.toPower3(tE))/MyMath.toPower3(s*tE*vTHat - t*(wA + tE*wTHat))))/
			    	    (2.*nM1*sJ1*t);
		double tGrad = ((n - sJ1)*(-wA - tE*wTHat))/(nM1*t) - ((n - sJ1)*(s*tE*vTHat - t*(wA + tE*wTHat)))/(nM1*MyMath.square(t)) - 
				   ((s*tE*vTHat - t*(wA + tE*wTHat))*((2*tE2*vTHat*(t*vTHat + s*wTHat))/MyMath.square(s*tE*vTHat - t*(wA + tE*wTHat)) - 
					        (2*tE2*(-wA - tE*wTHat)*(r2 + MyMath.square(t*vTHat + s*wTHat)))/MyMath.toPower3(s*tE*vTHat - t*(wA + tE*wTHat))))/(2.*nM1*sJ1*t);
		
		Vector3D normal = Vector3D.sum(
					rHat.getProductWith(rGrad),
					sHat.getProductWith(sGrad),
					tHat.getProductWith(tGrad)
			).getNormalised();
		
		// make sure the normal points outwards
		return normal.getProductWith(Math.signum(Vector3D.scalarProduct(normal, tHat)));
	}


	@Override
	public String getType()
	{
		return "Belin cone";
	}

	
	@Override
	public String toString() {
		return "BelinCone [pointInContourPlane=" + pointInContourPlane + ", contourNormal=" + contourNormal
				+ ", lensSurface=" + lensSurface + "]";
	}
}
