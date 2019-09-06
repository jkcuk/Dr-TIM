package optics.raytrace.imagingElements;

import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.GeneralisedLens;
import math.Vector3D;

/**
 * Now superceded by GlensSurface class.
 * 
 * A collection of useful methods related to imaging with inhomogeneous planar surfaces, as described in Ref. [1].
 * Such surfaces can be approximately realised in the form of planar gCLAs [2,3].
 * 
 * The subclass GeneralisedLens that implements a surface with these imaging properties.
 * 
 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
 * [2] A. C. Hamilton and J. Courtial, "Generalized refraction using lenslet arrays", J. Opt. A: Pure Appl. Opt. <b>11</b>, 065502 (2009)
 * [3] S. Oxburgh, C. D. White, G. Antoniou, and J. Courtial, "Law of refraction for generalised confocal lenslet arrays", Opt. Commun. <b>313</b>, 119-122 (2014)
 * 
 * @author johannes
 */
public class InhomogeneousPlanarImagingSurface
{
	// parameters that describe the surface
	private Vector3D pointOnPlane, uHat, vHat, nHat;
	private double
		eta,	// the ratio of the focal lengths, g = eta f
		f,	// object-sided focal length
		uCOverF, vCOverF;	// coordinates in the plane of the lens centre (where the optical axis intersects), divided by f

	//
	// constructors
	//
	
	/**
	 * Create an inhomogeneous planar imaging surface.
	 * This is essentially a slightly generalised thin lens, in which the object- and image-sided
	 * focal lengths are different by a factor eta.
	 * The parameters are described in Ref. [1].
	 * 
	 * [1] J. Courtial, "Geometric limits to geometric optical imaging with infinite, planar, non-absorbing sheets", Opt. Commun. <b>282</b>, 2480-2483 (2009)
	 * 
	 * @param pointOnPlane	a point on the surface
	 * @param u	one of the span vectors of the plane
	 * @param v	the second span vector of the plane
	 * @param n	the outwards-facing surface normal
	 * @param eta	eta, as defined for gCLAs
	 * @param f	the object-sided focal length [1]
	 * @param uCOverF	the u coordinate of the lens centre, divided by f
	 * @param vCOverF	the v coordinate of the lens centre, divided by f
	 */
	public InhomogeneousPlanarImagingSurface(
			Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n,
			double eta, double f, double uCOverF, double vCOverF
		)
	{
		this.pointOnPlane = pointOnPlane;
		setUVN(u, v, n);
		this.eta = eta;
		this.f = f;
		this.uCOverF = uCOverF;
		this.vCOverF = vCOverF;
	}
	
	public enum ParameterCalculationMethod
	{
		SET_ETA("Set eta, calculate the other parameters accordingly"),
		SET_F("Set f, calculate the other parameters accordingly");
		
		private String description;
		private ParameterCalculationMethod(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * Constructor which calculates eta, f, and the position of the intersection of the optical axis
	 * from the object and image position.
	 * @param pointOnPlane
	 * @param u
	 * @param v
	 * @param n
	 * @param objectPosition
	 * @param imagePosition
	 * @param parameterCalculation	SET_ETA or SET_F, depending on which parameter is set as opposed (the others are calculated)
	 * @param parameterValue	the value the parameter is set to
	 */
	public InhomogeneousPlanarImagingSurface(
			Vector3D pointOnPlane, Vector3D u, Vector3D v, Vector3D n,
			Vector3D objectPosition, Vector3D imagePosition,
			ParameterCalculationMethod parameterCalculation,
			double parameterValue
		)
	{
		// copy the parameters that can simply be copied
		this.pointOnPlane = pointOnPlane;
		setUVN(u, v, n);
		
		// now calculate everything else, following the Mathematica notebook "gCLAsMapping.nb"
		
		// calculate the parameters of the object and image position in the uvn coordinates (pointOnPlane is origin)
		Vector3D o = Vector3D.difference(objectPosition, pointOnPlane).toBasis(uHat, vHat, nHat);
		Vector3D i = Vector3D.difference(imagePosition, pointOnPlane).toBasis(uHat, vHat, nHat);
		
		switch(parameterCalculation)
		{
		case SET_ETA:
			eta = parameterValue;
			
			// the focal length
			f = i.z*o.z/(o.z*eta - i.z);
			
			// the coordinates of the lens centre
			uCOverF = (i.x*o.z*eta - i.z*o.x)/i.z*o.z;
			vCOverF = (i.y*o.z*eta - i.z*o.y)/i.z*o.z;
			
			break;
		case SET_F:
		default:
			f = parameterValue;
			
			// the other parameters
			uCOverF = (i.x*o.z/f + i.x - o.x)/o.z;
			vCOverF = (i.y*o.z/f + i.y - o.y)/o.z;
			eta     = (i.z*o.z/f + i.z      )/o.z;
		}
	}
	
	
	//
	// getters & setters
	//
	
	public Vector3D getPointOnPlane() {
		return pointOnPlane;
	}

	public void setPointOnPlane(Vector3D pointOnPlane) {
		this.pointOnPlane = pointOnPlane;
	}
	
	/**
	 * @param u	1st vector in the plane
	 * @param v	2nd vector in the plane
	 * @param n	normal vector
	 */
	public void setUVN(Vector3D u, Vector3D v, Vector3D n)
	{
		// normalise the surface normal
		nHat = n.getNormalised();
		
		// take the part of u that is perpendicular to nHat, and normalise it
		uHat = u.getPartPerpendicularTo(nHat).getNormalised();
		
		// take the part of v that is in the direction of nHat x uHat, and therefore perpendicular to uHat and nHat,
		// and normalise it
		vHat = v.getPartParallelTo(Vector3D.crossProduct(nHat, uHat)).getNormalised();
	}

	public Vector3D getuHat() {
		return uHat;
	}

	public void setuHat(Vector3D uHat) {
		this.uHat = uHat;
	}

	public Vector3D getvHat() {
		return vHat;
	}

	public void setvHat(Vector3D vHat) {
		this.vHat = vHat;
	}

	public Vector3D getnHat() {
		return nHat;
	}

	public void setnHat(Vector3D nHat) {
		this.nHat = nHat;
	}

	public double getEta() {
		return eta;
	}

	public void setEta(double eta) {
		this.eta = eta;
	}

	public double getF() {
		return f;
	}


	public void setF(double f) {
		this.f = f;
	}

	public double getuCOverF() {
		return uCOverF;
	}


	public void setuCOverF(double uCOverF) {
		this.uCOverF = uCOverF;
	}


	public double getvCOverF() {
		return vCOverF;
	}


	public void setvCOverF(double vCOverF) {
		this.vCOverF = vCOverF;
	}




	
	//
	// non-static methods
	//
	
	public Vector3D getLensCentre()
	{
		return Vector3D.sum(
				pointOnPlane,
				uHat.getProductWith(uCOverF*f),
				vHat.getProductWith(vCOverF*f)
			);
	}
	
	public Vector3D getLensCentreOverF()
	{
		return Vector3D.sum(
				pointOnPlane.getProductWith(1/f),
				uHat.getProductWith(uCOverF),
				vHat.getProductWith(vCOverF)
			);		
	}
	
	/**
	 * For a given object position, calculate the corresponding image position.
	 * This should also work in the limit where f goes to infinity.
	 * @param objectPosition
	 * @return	the image position
	 */
	public Vector3D getImagePosition(Vector3D objectPosition)
	{
		// calculate the object position in the uvn coordinates (pointOnPlane is origin)
		Vector3D o = Vector3D.difference(objectPosition, pointOnPlane).toBasis(uHat, vHat, nHat);

		// common denominator
		double d = 1 + o.z/f;
		
		// the Mathematica solution in the uvn (here called xyz) coordinate system is
		// xP -> (f x + x0 z)/(f + z), yP -> (f y + y0 z)/(f + z), zP -> (eta f z)/(f + z)
		return Vector3D.sum(
				pointOnPlane,
				uHat.getProductWith((o.x + uCOverF*o.z)/d),
				vHat.getProductWith((o.y + vCOverF*o.z)/d),
				nHat.getProductWith(           eta*o.z /d)
			);
	}
	
	
	// 
	/**
	 * Return a surface property --- a generalised lens --- that refracts like this
	 * @return	the GeneralisedLens surface property
	 */
	public GeneralisedLens toGeneralisedLens(double transmissionCoefficient, boolean shadowThrowing)
	{
		return new GeneralisedLens(this, transmissionCoefficient, shadowThrowing);
	}
	
	public GeneralisedLens toGeneralisedLens()
	{
		return toGeneralisedLens(
			SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
			true	// shadowThrowing
		);
	}

	
		
	@Override
	public String toString()
	{
		return "[Inhomogeneous planar imaging surface" +
			", point on plane = " + pointOnPlane +
			", uHat = " + uHat +
			", vHat = " + vHat +
			", nHat = " + nHat +
			", eta = " + eta +
			", f = " + f +
			", uCOverF = " + uCOverF +
			", vCOverF = " + vCOverF +
			"]";
	}	
}
