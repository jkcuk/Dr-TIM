package optics.raytrace.research.billiard;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import math.MyMath;
import math.Vector3D;

/**
 * Surface property for visualising the view on the planar part of a spherical wedge billiard [1].
 * 
 * This consists of a spherical wedge of wedge angle phi.
 * The two planar parts form, after flattening, a disc of radius r, centred on the origin, lying in the y=0 plane.
 * We visualise here the view on these flattened planar parts, with an added third dimension, here y.
 * The effect of the spherical part is taken care of by a cylindrical wall around the disc.
 * 
 * References:
 * [1] T. Tyc and D. Cidlinsky, "Spherical wedge billiard: From chaos to fractals and Talbot carpets", PRE 106, 054202 (2022)
 * 
 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinates(Vector3D p)
 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(Vector3D p)
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class SphericalWedgeBilliardSphericalPart extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -8988519468378296043L;
	
	// private CylinderMantle cylinder;
	
	/**
	 * the wedge angle of the spherical wedge, in degrees
	 */
	private double phiDeg;
	
	private double radius;
	
	// for coordinate system see J's lab book entry 31/5/23 p. 124
	private Vector3D eHat, fHat, uHat;

	
	public SphericalWedgeBilliardSphericalPart(
			// CylinderMantle cylinder,
			double phiDeg,
			Vector3D upDirection,
			Vector3D edgeDirection,
			double radius,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		// setCylinder(cylinder);
		setPhiDeg(phiDeg);
		setRadius(radius);
		setDirections(edgeDirection, upDirection);
	}
	

	/**
	 * Clone the original teleporting surface
	 * @param original
	 */
	public SphericalWedgeBilliardSphericalPart(SphericalWedgeBilliardSphericalPart original)
	{
		this(
				original.getPhiDeg(),
				original.getuHat(),
				original.geteHat(),
				original.getRadius(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SphericalWedgeBilliardSphericalPart clone()
	{
		return new SphericalWedgeBilliardSphericalPart(this);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
//		if (i.o==null) return DoubleColour.BLACK;
//		else if (i == RaySceneObjectIntersection.NO_INTERSECTION) return DoubleColour.BLACK;

		// i.o is the object that's been intersected
				
		// calculate the coordinates (u, v) of the intersection point in object 1's
		// local coordinate system (defined in ParametrisedSurface)
		
//		// First check that the intersected object is a parametrised cylinder mantle, as required...
//		if(!(i.o instanceof ParametrisedCylinderMantle))
//		{
//			throw new RayTraceException("SphericalWedgeBilliardSphericalPart::getColour: Object with this surface is not of type ParametrisedCylinderMantle");
//		}
//		
//		// it is a parametrised cylinder mantle -- good!
//		ParametrisedCylinderMantle cylinder = (ParametrisedCylinderMantle)i.o;
//		
//		// calculate the surface coordinates of the intersection point...
//		Vector2D uv = cylinder.getSurfaceCoordinates(i.p);

		//
		// calculate the parameters of the ray -- see J's lab book entry 31/5/23 p. 125
		//
		
		// create a local Cartesian coordinate system at the intersection point
		Vector3D rPrimeHat = i.p.getPartPerpendicularTo(uHat).getNormalised();	// the disc must be centred on the origin!
		Vector3D aPrimeHat = Vector3D.crossProduct(rPrimeHat, uHat);	// xyz coordinates are left-handed
		
		if(r.isReportToConsole())
			System.out.println("SphericalWedgeBilliardSphericalPart::getColour: rPrimeHat="+rPrimeHat+", aPrimeHat'="+aPrimeHat);

		double omegaPrime = Math.atan2(
				Vector3D.scalarProduct(r.getD(), aPrimeHat),
				Vector3D.scalarProduct(r.getD(), rPrimeHat)
			);
		
		double thetaPrime = Math.atan2(
				Vector3D.scalarProduct(i.p, fHat),
				Vector3D.scalarProduct(i.p, eHat)
			);
		
		double inclination = Math.asin(Vector3D.scalarProduct(r.getD(), uHat)/r.getD().getLength());

		if(r.isReportToConsole())
			System.out.println("SphericalWedgeBilliardSphericalPart::getColour: intersection point="+i.p+", omega'="+omegaPrime+", theta'="+thetaPrime+", inclination="+inclination);

		// ... and extract from this the angle theta of the second intersection point 
//		double theta2 = uv.y;
//		System.out.println("SphericalWedgeBilliardSphericalPart::getColour: intersection point "+i.p+" has theta2="+theta2);
		
//		// first, we need to calculate the angle omega (see Fig. 1(c) in Ref. [1])
//		// We calculate this using the outwards-facing normal to the cylinder surface at the intersection point
//		Vector3D n = cylinder.getNormalisedOutwardsSurfaceNormal(i.p);
//		// omega is then the angle between the ray-direction vector and this normal
//		// see https://stackoverflow.com/questions/5188561/signed-angle-between-two-3d-vectors-with-same-origin-within-the-same-plane
//		double omega = Math.atan2(
//				Vector3D.scalarProduct(
//						Vector3D.crossProduct(n, r.getD()),
//						normalisedUpwardsCylinderAxis
//					),	// (Va x Vb) . Vn, 
//				Vector3D.scalarProduct(n, r.getD())	// Va . Vb
//			);

		double omega = omegaPrime;
		double theta = Math.IEEEremainder(thetaPrime + 2*omega - Math.PI, 2*Math.PI);
		if(r.isReportToConsole())
			System.out.println("SphericalWedgeBilliardSphericalPart::getColour: previous intersection point parameters: omega="
					+omega
					+", theta="+theta
				);

//		// the first intersection point of the ray with the cylinder is 
//		double theta = theta2 - (Math.PI - 2.*omega);
		
		// calculate the new values of theta and omega after one more round trip (Eqns (1) and (2) in [1])
		double alpha = theta - 2.*omega;	// this keeps coming up
		double sAlpha = Math.sin(alpha);
		double cAlpha = Math.cos(alpha);
		double ssAlpha = Math.signum(sAlpha);
		double sPhi = Math.sin(MyMath.deg2rad(phiDeg));
		double cPhi = Math.cos(MyMath.deg2rad(phiDeg));
		double sOmega = Math.sin(omega);
		double cOmega = Math.cos(omega);
		double newTheta = 0.5*Math.PI*ssAlpha - Math.atan(sOmega/cOmega*sPhi/Math.abs(sAlpha) - cPhi*cAlpha/sAlpha);
		double newOmega = -Math.asin(sOmega*cPhi + ssAlpha*cOmega*cAlpha*sPhi);
		
		if(r.isReportToConsole())
			System.out.println("SphericalWedgeBilliardSphericalPart::getColour: new parameters: newOmega="
					+newOmega
					+", newTheta="+newTheta
				);

		// calculate the new starting point, the new ray direction
		Vector3D newStartingPoint = Vector3D.sum(
				eHat.getProductWith(radius*Math.cos(newTheta)),
				fHat.getProductWith(radius*Math.sin(newTheta)),
				uHat.getProductWith(Vector3D.scalarProduct(i.p, uHat))	// same height as previous ray
			);
		Vector3D rBarHat = newStartingPoint.getPartPerpendicularTo(uHat).getNormalised().getReverse();	// actually centre - new start point
		Vector3D aHat = Vector3D.crossProduct(uHat, rBarHat);	// left-handed coordinate system!
		// Vector3D aHat = Vector3D.crossProduct(rBarHat, uHat);	// this formula should be correct for a right-handed coordinate system, which we don't have here TODO
		Vector3D newRayDirection = Vector3D.sum(
					rBarHat.getProductWith(Math.cos(newOmega)),
					aHat.getProductWith(Math.sin(newOmega)),
					uHat.getProductWith(Math.tan(inclination))
			);
		if(r.isReportToConsole())
			System.out.println("SphericalWedgeBilliardSphericalPart::getColour: newStartingPoint="+newStartingPoint+", newRayDirection="+newRayDirection);

		// launch a new ray from here, leaving the same time the incident ray hit
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(newStartingPoint, newRayDirection, i.t, r.isReportToConsole()),
				i.o, 
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).multiply(getTransmissionCoefficient());
	}


	public double getPhiDeg() {
		return phiDeg;
	}


	public void setPhiDeg(double phiDeg) {
		this.phiDeg = phiDeg;
	}


	public double getRadius() {
		return radius;
	}


	public void setRadius(double radius) {
		this.radius = radius;
	}


	public void setDirections(Vector3D edgeDirection, Vector3D upDirection)
	{
		eHat = edgeDirection.getNormalised();
		uHat = upDirection.getPartPerpendicularTo(eHat).getNormalised();
		fHat = Vector3D.crossProduct(eHat, uHat);	// left-handed coordinate system!
	}


	public Vector3D geteHat() {
		return eHat;
	}


	public Vector3D getfHat() {
		return fHat;
	}


	public Vector3D getuHat() {
		return uHat;
	}

}
