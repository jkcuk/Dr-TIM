package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.RefractiveSimple;


/**
 * @author johannes, Jakub
 * 
 * A surface of a material with refractive index <i>n</i> that refracts light rays from a position (u,v,w) = (0, 0, -f), the focal point, such that they travel
 * in the positive <i>w</i> direction.
 * (We then want to construct a lens from two such surfaces.)
 */
public class LensSurface_old extends SceneObjectPrimitive
implements Serializable
{
	private static final long serialVersionUID = 3800391412551149446L;

	/**
	 * the centre of the lens surface
	 */
	protected Vector3D principalPoint;
	
	/**
	 * the focal length of the lens surface; the focal point is located a distance <i>f</i> in front of the surface
	 */
	protected double focalLength;
	
	/**
	 * refractive index of the lens (the refractive index outside is assumed to be 1)
	 */
	protected double refractiveIndex;
	
	/**
	 * the lens surface redirects all light rays from the focal point such that they have this direction inside the lens
	 */
	protected Vector3D directionInside;
	
	
	//
	// constructors
	// 
	
	public LensSurface_old(
			String description,
			Vector3D principalPoint,
			double focalLength,
			double refractiveIndex,
			Vector3D directionInside,
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
		
		setPrincipalPoint(principalPoint);
		setFocalLength(focalLength);
		setRefractiveIndex(refractiveIndex);
		setDirectionInside(directionInside);
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public LensSurface_old(LensSurface_old original)
	{
		this(
				original.getDescription(),
				original.getPrincipalPoint(),
				original.getFocalLength(),
				original.getRefractiveIndex(),
				original.getDirectionInside(),
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
	public LensSurface_old clone()
	{
		return new LensSurface_old(this);
	}
	
	@Override
	public LensSurface_old transform(Transformation t)
	{
		return new LensSurface_old(
				description,
				t.transformPosition(getPrincipalPoint()),
				getFocalLength(),
				getRefractiveIndex(),
				t.transformDirection(getDirectionInside()),
				((RefractiveSimple)(getSurfaceProperty())).getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(),
				getStudio()
		);
	}



	//
	// getters and setters
	//

	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
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

	public Vector3D getDirectionInside() {
		return directionInside;
	}

	/**
	 * sets this.directionInside to directionInside, normalised
	 * @param directionInside
	 */
	public void setDirectionInside(Vector3D directionInside) {
		this.directionInside = directionInside.getNormalised();
	}

	
	


	@Override
	public String toString() {
		return "LensSurface [principalPoint=" + principalPoint + ", focalLength=" + focalLength + ", refractiveIndex="
				+ refractiveIndex + ", directionInside=" + directionInside + "]";
	}


	/**
	 * first transform the ray parameters into the lens surface's (u,v,w) Cartesian coordinate system
	 * (in which the focus is at the origin and <i>directionInside</i> is the positive <i>w</i> direction)
	 * @param u
	 * @param v
	 * @return	the w value of the surface
	 */
	public double calculateW(double u, double v)
	{
		// special case
		if(focalLength == 0) return 0;
		
		double a = refractiveIndex*refractiveIndex - 1;
		double b = 2*focalLength*(refractiveIndex-1);
		double c = -u*u-v*v;
		
		// calculate the discriminant to check if there is a solution
		double discriminant = b*b-4*a*c;

		// there is always a value for w for both surface "branches", so the discriminant should always be positive,
		// and so we should always be able to take the square root of the discriminant
		return (-b + Math.signum(focalLength)*Math.sqrt(discriminant))/(2*a);
	}
	
	/**
	 * @param u
	 * @param v
	 * @param w
	 * @return	true if the point (u, v, w) is on the lens surface; false otherwise
	 */
	private boolean isOnLensSurface(double u, double v, double w)
	{
		return (Math.abs(calculateW(u, v) - w) < MyMath.TINY);
	}

	//
	// SceneObjectPrimitive methods
	//
	
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// points on the ray are parametrised in the form P + delta*d
		
		// first transform the ray parameters into the lens surface's coordinate system, in which the principal point is at the origin
		// and <i>directionInside</i> is the positive z direction
		// create the axes of an orthonormal coordinate system...
		Vector3D wHat = directionInside;
		Vector3D uHat = Vector3D.getANormal(directionInside);
		Vector3D vHat = Vector3D.crossProduct(wHat, uHat);
		
		// System.out.println("LensSurface::getClosestRayIntersection: uHat="+uHat+", vHat="+vHat+", wHat="+wHat);

		// ... and write the light-ray direction in this basis...
		double dU = Vector3D.scalarProduct(ray.getD(), uHat);
		double dV = Vector3D.scalarProduct(ray.getD(), vHat);
		double dW = Vector3D.scalarProduct(ray.getD(), wHat);
		
		// System.out.println("LensSurface::getClosestRayIntersection: ray.getD().x="+ray.getD().x);
		// if(Double.isNaN(ray.getD().x)) new RayTraceException("Ray with NaN direction?!").printStackTrace();
		
		// System.out.println("LensSurface::getClosestRayIntersection: ray.getD()="+ray.getD());
		// System.out.println("LensSurface::getClosestRayIntersection: uD="+uD+", vD="+vD+", wD="+wD);

		// ... and the ray's start position
		Vector3D f2p = Vector3D.difference(ray.getP(), principalPoint);
		double pU = Vector3D.scalarProduct(f2p, uHat);
		double pV = Vector3D.scalarProduct(f2p, vHat);
		double pW = Vector3D.scalarProduct(f2p, wHat);
		
		// the solutions for delta are of the form (-b +/- sqrt(b^2 - 4 a c))/(2 a)
		// see FresnelLens.nb
		double nP = refractiveIndex + 1;
		double nM = refractiveIndex - 1;
		double a = dU*dU + dV*dV + dW*dW*(1-refractiveIndex*refractiveIndex);
		double b = 2*(dU*pU + dV*pV - dW*nM*(focalLength + pW*nP));
		// double b = -2*refractiveIndex*dW*(focalLength + refractiveIndex*pW) + 2*(dU*pU + dV*pV + dW*(focalLength + pW));
		double c = pU*pU + pV*pV - pW*nM*(2*focalLength + pW*nP);
		// double c = -MyMath.square(focalLength + refractiveIndex*pW) + (pU*pU + pV*pV + MyMath.square(focalLength + pW));
		
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

		// first transform the ray parameters into the lens surface's coordinate system, in which the focus is at the origin
		// and <i>directionInside</i> is the positive z direction
		// create the axes of an orthonormal coordinate system...
		Vector3D wHat = directionInside;
		Vector3D uHat = Vector3D.getANormal(directionInside);
		Vector3D vHat = Vector3D.crossProduct(wHat, uHat);
		
		// ... and convert the position <p> into the (u,v,w) coordinate system
		Vector3D f2p = Vector3D.difference(p, principalPoint);
		double u = Vector3D.scalarProduct(f2p, uHat);
		double v = Vector3D.scalarProduct(f2p, vHat);
		double w = Vector3D.scalarProduct(f2p, wHat);

		return (calculateW(u, v) < w);
		// return (-refractiveIndex*w + Math.sqrt(u*u + v*v + MyMath.square(focalLength + w))*Math.signum(focalLength) - focalLength < 0.0);
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
		// v/||v|| * sign(f) - <directionInside>*n.
		return Vector3D.difference(
				Vector3D.sum(
						Vector3D.difference(p, principalPoint),
						directionInside.getProductWith(focalLength)
					).getWithLength(Math.signum(focalLength)),
				directionInside.getProductWith(refractiveIndex)
			).getNormalised();
	}
	
	@Override
	public String getType()
	{
		return "Lens surface (old)";
	}
}
