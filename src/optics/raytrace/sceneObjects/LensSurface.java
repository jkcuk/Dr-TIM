package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.RefractiveSimple;


/**
 * @author johannes
 * 
 * A surface of a material with refractive index <i>n</i> that refracts light rays that travel inside the lens in the <i>w</i> direction
 * such that, outside of the lens, they travel through the origin of the (u, v, w) coordinate system.
 * (We can then construct a lens from two such surfaces, or a Fresnel lens from a number of them.)
 */
public class LensSurface extends SceneObjectPrimitive
implements Serializable
{
	private static final long serialVersionUID = 3800391412551149446L;

	/**
	 * the outside focal point of the lens surface
	 */
	private Vector3D focalPoint;
	
	/**
	 * the focal length of the lens surface; the centre of the lens surface is located at (u,v,w) = (0, 0, focalLength)
	 */
	private double focalLength;
	
	/**
	 * refractive index of the lens (the refractive index outside is assumed to be 1);
	 * throughout, it is assumed that n>1
	 */
	private double refractiveIndex;
	
	/**
	 * a unit vector in the direction of the <i>w</i> axis, i.e. the direction of the optical axis, pointing outwards
	 */
	private Vector3D wHat;
	
	
	// private variables
	
	// together with <i>wHat</i>, <i>uHat</i> and <i>vHat</i> are the unit vectors describing the transverse directions of the orthonormal (u, v, w)
	// coordinate system, in which the focal point is at the origin, <i>wHat</i> is the optical-axis direction, pointing outwards, and
	// <i>uHat</i> and </i>vHat</i> are two directions orthogonal to the optical-axis direction
	private Vector3D uHat;
	private Vector3D vHat;

	
	
	//
	// constructors
	// 
	
	public LensSurface(
			String description,
			Vector3D focalPoint,
			double focalLength,
			double refractiveIndex,
			Vector3D opticalAxisDirectionOutwards,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
	)
	{
		super(
				description,
				new RefractiveSimple(refractiveIndex, transmissionCoefficient, shadowThrowing),
				parent, studio
			);
		
		setFocalPoint(focalPoint);
		setFocalLength(focalLength);
		setRefractiveIndex(refractiveIndex);
		setOpticalAxisDirectionOutwards(opticalAxisDirectionOutwards);
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public LensSurface(LensSurface original)
	{
		this(
				original.getDescription(),
				original.getFocalPoint(),
				original.getFocalLength(),
				original.getRefractiveIndex(),
				original.getOpticalAxisDirectionOutwards(),
				((RefractiveSimple)(original.getSurfaceProperty())).getTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public LensSurface clone()
	{
		return new LensSurface(this);
	}
	
	@Override
	public LensSurface transform(Transformation t)
	{
		return new LensSurface(
				description,
				t.transformPosition(getFocalPoint()),
				getFocalLength(),
				getRefractiveIndex(),
				t.transformDirection(getOpticalAxisDirectionOutwards()),
				((RefractiveSimple)(getSurfaceProperty())).getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(),
				getStudio()
		);
	}



	//
	// getters and setters
	//

	public Vector3D getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(Vector3D principalPoint) {
		this.focalPoint = principalPoint;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public Vector3D getOpticalAxisDirectionOutwards() {
		return wHat;
	}
	
	public Vector3D getUHat() {
		return uHat;
	}
	
	public Vector3D getVHat() {
		return vHat;
	}

	/**
	 * sets <i>wHat</i> to <i>opticalAxisDirectionOutwards</i>, normalised;
	 * also pre-calculates <i>uHat</i> and <i>vHat</i>
	 * @param opticalAxisDirectionOutwards
	 */
	public void setOpticalAxisDirectionOutwards(Vector3D opticalAxisDirectionOutwards) {
		wHat = opticalAxisDirectionOutwards.getNormalised();
		
		// create the axes of an orthonormal coordinate system...
		uHat = Vector3D.getANormal(opticalAxisDirectionOutwards);
		vHat = Vector3D.crossProduct(wHat, uHat);
		
		// System.out.println("LensSurface::getClosestRayIntersection: uHat="+uHat+", vHat="+vHat+", wHat="+wHat);
	}

	
	/**
	 * @return	the lens surface's principal point, where the optical axis intersects the lens surface
	 */
	public Vector3D calculatePrincipalPoint()
	{
		return Vector3D.sum(
				focalPoint,
				wHat.getWithLength(-focalLength)
			);
	}


	@Override
	public String toString() {
		return "LensSurface [focalPoint=" + focalPoint + ", focalLength=" + focalLength + ", refractiveIndex="
				+ refractiveIndex + ", wHat=" + wHat + "]";
	}


	/**
	 * first transform the ray parameters into the lens surface's (u,v,w) Cartesian coordinate system
	 * @param u
	 * @param v
	 * @return	the w value of the surface
	 */
	public double calculateW(double u, double v)
	{
		// special case
		double nP1 = refractiveIndex + 1;
		double nM1 = refractiveIndex - 1;
		return -focalLength/nP1*(refractiveIndex + Math.sqrt( 1 + nP1/nM1*(u*u+v*v)/(focalLength*focalLength)));
	}
	
	/**
	 * @param u
	 * @param v
	 * @param w
	 * @return	true if the point (u, v, w) is on the lens surface; false otherwise
	 */
	private boolean isOnLensSurface(double u, double v, double w)
	{
		// calculate the <i>w</i> value of the lens surface for the point's <i>u</i> and <i>v</i> coordinates, and if this value is
		// the same (well, numerically) as the point's <i>w</i> coordinate then the point lies on the lens surface
		return (Math.abs(calculateW(u, v) - w) < MyMath.TINY);
	}
	
	/**
	 * @param point
	 * @return	the focal length of the lens surface through <i>point</i> (with the given focal point, optical-axis direction, and refractive index)
	 * @throws InconsistencyException	if there is no such lens surface
	 */
	public double calculateFocalLengthOfSurfaceThroughPoint(Vector3D point)
	throws InconsistencyException
	{
		// transform <i>point</i> onto the (u, v, w) coordinate system
		Vector3D f2p = Vector3D.difference(point, focalPoint);
		double u = Vector3D.scalarProduct(f2p, uHat);
		double v = Vector3D.scalarProduct(f2p, vHat);
		double w = Vector3D.scalarProduct(f2p, wHat);

		double f1 = -Math.abs(w)*(refractiveIndex - Math.sqrt(1+(u*u+v*v)/(w*w)))/(refractiveIndex-1);
		if(f1 > 0) throw new InconsistencyException("There is no lens surface for focal point "+focalPoint+", optical-axis direction "+wHat+", and refractive index "+refractiveIndex+" that passes through "+point);
		return Math.signum(w)*f1;
	}
	
	/**
	 * @param point
	 * @param focalPoint
	 * @param wHat	must be normalised!
	 * @param refractiveIndex
	 * @return
	 * @throws InconsistencyException 
	 */
	public static double calculateFocalLengthOfSurfaceThroughPoint(Vector3D point, Vector3D focalPoint, Vector3D wHat, double refractiveIndex)
	throws InconsistencyException
	{
		// calculate component of vector from <focalPoint> to <point> in w direction ...
		Vector3D f2p = Vector3D.difference(point,  focalPoint);
		double w  = Vector3D.scalarProduct(f2p, wHat);
		double u2v2 = f2p.getPartPerpendicularTo(wHat).getModSquared();
		
		double f1 = -Math.abs(w)*(refractiveIndex - Math.sqrt(1+u2v2/(w*w)))/(refractiveIndex-1);
		if(f1 > 0) throw new InconsistencyException("There is no lens surface for focal point "+focalPoint+", optical-axis direction "+wHat+", and refractive index "+refractiveIndex+" that passes through "+point);
		return Math.signum(w)*f1;
	}

	//
	// SceneObjectPrimitive methods
	//
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// points on the ray are parametrised in the form P + delta*d
		
		// write the light-ray direction in the (u, v, w) basis...
		double dU = Vector3D.scalarProduct(ray.getD(), uHat);
		double dV = Vector3D.scalarProduct(ray.getD(), vHat);
		double dW = Vector3D.scalarProduct(ray.getD(), wHat);
		
		// System.out.println("LensSurface::getClosestRayIntersection: ray.getD().x="+ray.getD().x);
		// if(Double.isNaN(ray.getD().x)) new RayTraceException("Ray with NaN direction?!").printStackTrace();
		
		// System.out.println("LensSurface::getClosestRayIntersection: ray.getD()="+ray.getD());
		// System.out.println("LensSurface::getClosestRayIntersection: uD="+uD+", vD="+vD+", wD="+wD);

		// ... and the ray's start position
		Vector3D f2p = Vector3D.difference(ray.getP(), focalPoint);
		double pU = Vector3D.scalarProduct(f2p, uHat);
		double pV = Vector3D.scalarProduct(f2p, vHat);
		double pW = Vector3D.scalarProduct(f2p, wHat);
		
		// the solutions for delta are of the form (-b +/- sqrt(b^2 - 4 a c))/(2 a)
		// see FresnelLens.nb
		double nP = refractiveIndex + 1;
		double nM = refractiveIndex - 1;
		
		double a = -dU*dU - dV*dV - dW*dW*(1-refractiveIndex*refractiveIndex); 
		double b = -2*(dU*pU + dV*pV - dW*nM*(focalLength*refractiveIndex + nP*pW));
		double c = -pU*pU - pV*pV + nM*(focalLength + pW)*(focalLength*nM + nP*pW);
		
		// System.out.println("LensSurface::getClosestRayIntersection: a="+a+", b="+b+", c="+c);
		
		// calculate the discriminant to check if there is a solution
		double discriminant = b*b-4*a*c;
		
		if(discriminant < 0)
		{
			// there is no solution, and so no intersection with this surface
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}
		
		// calculate the two solutions and check which one, if any, is the relevant one
		double sqrtDiscriminant = Math.sqrt(discriminant);
		double delta1 = (-b - sqrtDiscriminant)/(2*a);
		double delta2 = (-b + sqrtDiscriminant)/(2*a);
		
		// first sort the solutions by size
		double[] delta = new double[2];
		delta[0] = Math.min(delta1, delta2);
		delta[1] = Math.max(delta1, delta2);
		
		// To find the value of t that corresponds to the solution of interest, go through them all, starting with the smallest, 
		// and find the first for which delta>0 and the intersection is with the correct "branch" of the surface.
		// So, go through all the values of delta, starting with the smallest ...
		for(int i=0; i<2; i++)
		{
			if(delta[i] > 0)
			{
				// does the intersection's w value agree with the w value that corresponds to the intersection's u and v values?
				if(isOnLensSurface(pU + delta[i]*dU, pV + delta[i]*dV, pW + delta[i]*dW))
				{
					// yes, so this is the right intersection!
					Ray rayAtIntersectionPoint = ray.getAdvancedRay(delta[i]);

					return new RaySceneObjectIntersection(
							rayAtIntersectionPoint.getP(),
							this,
							rayAtIntersectionPoint.getT()
						);
				}
			}
		}
		
		// the two solutions for t do not correspond to an intersection of the ray and the lens surface as they either correspond to 
		// intersections on the straight line of the ray but behind the ray's start position or the "branch" of the lens surface they intersect with
		// is the wrong one
		return RaySceneObjectIntersection.NO_INTERSECTION;
	}


	@Override
	public boolean insideObject(Vector3D p) {
		// the surface is given by the equation (optical path length via point on surface - f) == 0,
		// so calculate the LHS of that equation and check if it is greater than 0 or less than 0

		// convert the position <p> into the (u,v,w) coordinate system
		Vector3D f2p = Vector3D.difference(p, focalPoint);	// as the focal point is the origin of the (u, v, w) coordinate system, take the vector from there
		double u = Vector3D.scalarProduct(f2p, uHat);
		double v = Vector3D.scalarProduct(f2p, vHat);
		double w = Vector3D.scalarProduct(f2p, wHat);

		return (w < calculateW(u, v));
	}


	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p) {
		// the lens surface is given by the equation (optical path length via point on surface - d) == 0;
		// if the surface passes through a point <p>, then the normal to the surface equals the gradient of the LHS of the above equation, which is,
		// according to Mathematica,
		//   {(u Sign[f])/Sqrt[u^2 + v^2 + (f + w)^2],
		//    (v Sign[f])/Sqrt[u^2 + v^2 + (f + w)^2],
		//     -n + ((f + w) Sign[f])/Sqrt[u^2 + v^2 + (f + w)^2]};
		// the vector (u, v, f+w) that comes up there is the same as v=(p-principalPoint)+directionInside*focalLength,
		// so the whole expression can be written in the form
		// v/||v|| * sign(f) + <opticalAxisDirectionOutwards>*n.
		return Vector3D.sum(
				Vector3D.difference(p, focalPoint).getWithLength(Math.signum(focalLength)),
				wHat.getProductWith(refractiveIndex)
			).getNormalised();
	}
	
	@Override
	public String getType()
	{
		return "Lens surface";
	}
}
