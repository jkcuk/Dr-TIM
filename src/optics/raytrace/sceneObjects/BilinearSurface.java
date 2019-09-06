package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Scene object representing a bilinear surface defined by four corners, p00, p01, p10, p11.
 * The sides of the surface, i.e. p00-p01, p01-p11, p11-p10, p10-p00 are straight lines.
 * 
 * @author Johannes
 */
public class BilinearSurface extends SceneObjectPrimitive implements Serializable
{

	/**
	 * corner p00 of the surface
	 */
	private Vector3D p00;
	
	/**
	 * corner p01 of the surface
	 */
	private Vector3D p01;

	/**
	 * corner p10 of the surface
	 */
	private Vector3D p10;

	/**
	 * corner p11 of the surface
	 */
	private Vector3D p11;
	
	/**
	 * there are two solutions to the quadratic equations that describe this surface;
	 * pick either 1 or 2
	 */
	private int solution;
	
	
	// constructors
	
	public BilinearSurface(
			String description,
			Vector3D p00,
			Vector3D p01,
			Vector3D p10,
			Vector3D p11,
			int solution,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setP00(p00);
		setP01(p01);
		setP10(p10);
		setP11(p11);
		setSolution(solution);
	}
	
	public BilinearSurface(BilinearSurface original)
	{
		super(original);
		
		setP00(original.getP00());
		setP01(original.getP01());
		setP10(original.getP10());
		setP11(original.getP11());
		setSolution(original.getSolution());
	}
	
	@Override
	public BilinearSurface clone()
	{
		return new BilinearSurface(this);
	}
	
	
	// getters & setters
	
	public Vector3D getP00() {
		return p00;
	}

	public void setP00(Vector3D p00) {
		this.p00 = p00;
	}

	public Vector3D getP01() {
		return p01;
	}

	public void setP01(Vector3D p01) {
		this.p01 = p01;
	}

	public Vector3D getP10() {
		return p10;
	}

	public void setP10(Vector3D p10) {
		this.p10 = p10;
	}

	public Vector3D getP11() {
		return p11;
	}

	public void setP11(Vector3D p11) {
		this.p11 = p11;
	}
	
	public int getSolution() {
		return solution;
	}

	public void setSolution(int solution) {
		this.solution = solution;
	}

	
	// geometry
	
	/**
	 * @param u
	 * @param v
	 * @return	the position P that corresponds to the parameters u and v
	 */
	Vector3D calculateP(double u, double v)
	{
		// TODO does this correspond to solution 1 or 2?
		return Vector3D.sum(
				p00.getProductWith((1-u)*(1-v)),
				p01.getProductWith((1-u)*  v  ),
				p10.getProductWith(  u  *(1-v)),
				p11.getProductWith(  u  *  v  )
			);
	}
	
	/**
	 * @param p	point on the surface
	 * @return	{u1, v1, u2, v2}, i.e. a 2D vector that contains the parameters u and v that correspond to the point p; null if no solution
	 */
	double[] calculateUAndV(Vector3D p0)
	{
		double fx = p00.x;
		double fy = p00.y;
		double gx = p01.x;
		double gy = p01.y;
		double hx = p10.x;
		double hy = p10.y;
		double ix = p11.x;
		double iy = p11.y;
		double p0x = p0.x;
		double p0y = p0.y;

		double discriminant = 4*(gy*hx - gx*hy + fy*(gx - ix) + hy*ix - hx*iy + fx*(-gy + iy))*
			    (-(gy*p0x) + fy*(-gx + p0x) + fx*(gy - p0y) + gx*p0y) + 
			    Math.pow(gy*hx - gx*hy + fy*(2*gx - ix - p0x) + gy*p0x + hy*p0x - iy*p0x - gx*p0y - 
			      hx*p0y + ix*p0y + fx*(-2*gy + iy + p0y),2);
		
		if(discriminant < 0)
		{
			// all solutions are complex, i.e. there are no real solutions
			return null;
		}
		
		double s = Math.sqrt(discriminant);
		
		// there are two solutions
		double uPart1 = -2*fy*gx + 2*fx*gy - gy*hx + gx*hy + fy*ix - fx*iy + fy*p0x - gy*p0x - hy*p0x + 
				   iy*p0x - fx*p0y + gx*p0y + hx*p0y - ix*p0y;
		
		double vPart1 = 2*fy*hx - gy*hx - 2*fx*hy + gx*hy - fy*ix + fx*iy - fy*p0x + gy*p0x + hy*p0x - 
				   iy*p0x + fx*p0y - gx*p0y - hx*p0y + ix*p0y;
		
		double uDenominator = -2*(fy-hy)*(gx-ix) + 2*(fx-hx)*(gy-iy);
		
		double vDenominator =  2*(fy-gy)*(hx-ix) - 2*(fx-gx)*(hy-iy);
		
		double[] u1v1u2v2 = new double[4];
		
		u1v1u2v2[0] = (uPart1 + s) / uDenominator;	// u1
		u1v1u2v2[1] = (vPart1 + s) / vDenominator;	// v1
		u1v1u2v2[2] = (uPart1 - s) / uDenominator;	// u2
		u1v1u2v2[3] = (vPart1 - s) / vDenominator;	// v2
		
		return u1v1u2v2;
	}

	
	// SceneObject methods
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		double fx = p00.x;
		double fy = p00.y;
		double fz = p00.z;
		double gx = p01.x;
		double gy = p01.y;
		double gz = p01.z;
		double hx = p10.x;
		double hy = p10.y;
		double hz = p10.z;
		double ix = p11.x;
		double iy = p11.y;
		double iz = p11.z;
		double ax = ray.getP().x;
		double ay = ray.getP().y;
		double az = ray.getP().z;
		double dx = ray.getD().x;
		double dy = ray.getD().y;
		double dz = ray.getD().z;
		
		double discriminant = Math.pow(ax*dz*fy - ax*dy*fz - 2*dz*fy*gx + 2*dy*fz*gx - ax*dz*gy + 2*dz*fx*gy - 
			     2*dx*fz*gy + ax*dy*gz - 2*dy*fx*gz + 2*dx*fy*gz - dz*gy*hx + dy*gz*hx - 
			     ax*dz*hy + dz*gx*hy - dx*gz*hy + ax*dy*hz - dy*gx*hz + dx*gy*hz + 
			     ay*dz*(-fx + gx + hx - ix) + dz*fy*ix - dy*fz*ix + az*dy*(fx - gx - hx + ix) + 
			     az*dx*(-fy + gy + hy - iy) + ax*dz*iy - dz*fx*iy + dx*fz*iy - ax*dy*iz + 
			     dy*fx*iz - dx*fy*iz + ay*dx*(fz - gz - hz + iz),2) + 
			   4*(ax*dz*fy - ax*dy*fz + az*dy*(fx - gx) - dz*fy*gx + dy*fz*gx + ay*dz*(-fx + gx) - 
			      ax*dz*gy + dz*fx*gy - dx*fz*gy + az*dx*(-fy + gy) + ay*dx*(fz - gz) + ax*dy*gz - 
			      dy*fx*gz + dx*fy*gz)*(dz*(-(fx*gy) + gy*hx - gx*hy + fy*(gx - ix) + hy*ix + 
			         fx*iy - hx*iy) + dy*(-(fz*gx) + fx*gz - gz*hx + gx*hz + fz*ix - hz*ix - 
			         fx*iz + hx*iz) + dx*(fz*gy - fy*gz + gz*hy - gy*hz - fz*iy + hz*iy + fy*iz - 
			         hy*iz));
		
		if(discriminant < 0)
		{
			// all the solutions are complex, i.e. there is no intersection
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		// there are two solutions;
		// calculate the u and v values for both solutions, which are
		// u_{1,2} = (uPart1 +/- sqrt(discriminant)) / uDenominator
		// v_{1,2} = (vPart1 +/- sqrt(discriminant)) / vDenominator
		
		double uPart1 = az*dy*fx - ay*dz*fx - az*dx*fy + ax*dz*fy + ay*dx*fz - ax*dy*fz - az*dy*gx + 
				   ay*dz*gx - 2*dz*fy*gx + 2*dy*fz*gx + az*dx*gy - ax*dz*gy + 2*dz*fx*gy - 
				   2*dx*fz*gy - ay*dx*gz + ax*dy*gz - 2*dy*fx*gz + 2*dx*fy*gz - az*dy*hx + ay*dz*hx - 
				   dz*gy*hx + dy*gz*hx + az*dx*hy - ax*dz*hy + dz*gx*hy - dx*gz*hy - ay*dx*hz + 
				   ax*dy*hz - dy*gx*hz + dx*gy*hz + az*dy*ix - ay*dz*ix + dz*fy*ix - dy*fz*ix - 
				   az*dx*iy + ax*dz*iy - dz*fx*iy + dx*fz*iy + ay*dx*iz - ax*dy*iz + dy*fx*iz - 
				   dx*fy*iz;
		
		double vPart1 = -(az*dy*fx) + ay*dz*fx + az*dx*fy - ax*dz*fy - ay*dx*fz + ax*dy*fz + az*dy*gx - 
				   ay*dz*gx - az*dx*gy + ax*dz*gy + ay*dx*gz - ax*dy*gz + az*dy*hx - ay*dz*hx + 
				   2*dz*fy*hx - 2*dy*fz*hx - dz*gy*hx + dy*gz*hx - az*dx*hy + ax*dz*hy - 2*dz*fx*hy + 
				   2*dx*fz*hy + dz*gx*hy - dx*gz*hy + ay*dx*hz - ax*dy*hz + 2*dy*fx*hz - 2*dx*fy*hz - 
				   dy*gx*hz + dx*gy*hz - az*dy*ix + ay*dz*ix - dz*fy*ix + dy*fz*ix + az*dx*iy - 
				   ax*dz*iy + dz*fx*iy - dx*fz*iy - ay*dx*iz + ax*dy*iz - dy*fx*iz + dx*fy*iz;
		
		double uDenominator = 2*(-(dz*(fy - hy)*(gx - ix)) + dy*(fz - hz)*(gx - ix) + dz*(fx - hx)*(gy - iy) - 
			     dx*(fz - hz)*(gy - iy) - dy*(fx - hx)*(gz - iz) + dx*(fy - hy)*(gz - iz));
		
		double vDenominator = -2*(-(dz*(fy - gy)*(hx - ix)) + dy*(fz - gz)*(hx - ix) + dz*(fx - gx)*(hy - iy) - 
			     dx*(fz - gz)*(hy - iy) - dy*(fx - gx)*(hz - iz) + dx*(fy - gy)*(hz - iz));
		
		double s = Math.sqrt(discriminant);
		
		double u1 = (uPart1 + s)/uDenominator;
		double u2 = (uPart1 - s)/uDenominator;
		double v1 = (vPart1 + s)/vDenominator;
		double v2 = (vPart1 - s)/vDenominator;
		
		// do these values lie in the range 0 <= u1, u2, v1, v2 <= 1, which defines the patch
		if((u1 >= 0) || (v1 >= 0) || (u1 <= 1) || (v1 <= 1))
		{
			// solution 1 lies on the patch
		}
		
		// find the intersection point
		Ray rayAtIntersectionPoint = ray.getAdvancedRay(lambda);
		Vector3D intersectionPoint = rayAtIntersectionPoint.getP();

		
		Vector2D coefficients = intersectionPoint.getDifferenceWith(corner).calculateDecomposition(spanVector1, spanVector2);
		double alpha = coefficients.x;
		double beta = coefficients.y;
		
//		// the distance from the corner
//		Vector3D distanceFromCorner = Vector3D.difference(intersectionPoint, corner);
//
//		//double alpha = Vector3D.scalarProduct(u.normalise(), Vector3D.difference(point, centre));
//		double alpha = Vector3D.scalarProduct(spanVector1, distanceFromCorner) / spanVector1.getModSquared();


		if (alpha < 0. || 1. < alpha) {
			// alpha * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		//double beta = Vector3D.scalarProduct(v.normalise(), Vector3D.difference(point, centre));
//		double beta = Vector3D.scalarProduct(spanVector2, distanceFromCorner) / spanVector2.getModSquared();
		if (beta < 0. || 1. < beta) {
			// beta * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		//return new RaySceneObjectIntersection (ray.getP().add(Vector3D.scalarTimesVector3D(lambda, ray.getD())),this);
		return new RaySceneObjectIntersection(intersectionPoint, this, rayAtIntersectionPoint.getT());
	}

	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray, SceneObject excludeObject)
	{
		return getClosestRayIntersection(ray);
	}
	
	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		double fx = p00.x;
		double fy = p00.y;
		double fz = p00.z;
		double gx = p01.x;
		double gy = p01.y;
		double gz = p01.z;
		double hx = p10.x;
		double hy = p10.y;
		double hz = p10.z;
		double ix = p11.x;
		double iy = p11.y;
		double iz = p11.z;

		// first calculate u and v that correspond to the intersection point
		double[] u1v1u2v2 = calculateUAndV(p);
		
		// do these values lie in the range 0 <= u1, u2, v1, v2 <= 1, which defines the patch
		if((u1 >= 0) || (v1 >= 0) || (u1 <= 1) || (v1 <= 1))

		// partial p / partial u
		Vector3D dpdu = new Vector3D(
				-(fx*(1 - v)) + hx*(1 - v) - gx*v + ix*v,
				-(fy*(1 - v)) + hy*(1 - v) - gy*v + iy*v,
				-(fz*(1 - v)) + hz*(1 - v) - gz*v + iz*v
			);
		
		// the vector (partial p / partial v)
		Vector3D dpdv = new Vector3D(
				-(fx*(1 - u)) + gx*(1 - u) - hx*u + ix*u,
				-(fy*(1 - u)) + gy*(1 - u) - hy*u + iy*u,
				-(fz*(1 - u)) + gz*(1 - u) - hz*u + iz*u
			);
		
		return Vector3D.crossProduct(dpdu, dpdv).getNormalised();
	}

	// TransformableSceneObject method
	@Override
	public BilinearSurface transform(Transformation t)
	{
		return new BilinearSurface(
				description,
				t.transformPosition(p00),
				t.transformPosition(p01),
				t.transformPosition(p10),
				t.transformPosition(p11),
				getSolution(),
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	} 

	@Override
	public boolean insideObject(Vector3D p)
	{
		// TODO
		return 0 < getNormalisedOutwardsSurfaceNormal(null).getScalarProductWith( p.getDifferenceWith(corner) );
	}

	@Override
	public String toString()
	{
		return description + " [BilinearSurface]";
	}
	
	@Override
	public String getType()
	{
		return "BilinearSurface";
	}
}

