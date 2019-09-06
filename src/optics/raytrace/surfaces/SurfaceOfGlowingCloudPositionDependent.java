package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.RayTraceException;
import math.MyMath;
import math.Vector3D;

/**
 * A SurfaceOfGlowingCloud in which the glow colour can be altered depending on the position of the intersection point.
 * 
 * @author Johannes Courtial
 */
public class SurfaceOfGlowingCloudPositionDependent extends SurfaceOfGlowingCloud
{	
	private static final long serialVersionUID = 1803941512373068015L;

	/**
	 * Creates a new tinted-solid surface.
	 */
	public SurfaceOfGlowingCloudPositionDependent(DoubleColour glowColour, double oneOverEDistance, boolean shadowThrowing)
	{
		super(glowColour, oneOverEDistance, shadowThrowing);
	}
	
	/**
	 * Clone the original tinted-solid surface
	 * @param original
	 */
	public SurfaceOfGlowingCloudPositionDependent(SurfaceOfGlowingCloudPositionDependent original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceOfGlowingCloudPositionDependent clone()
	{
		return new SurfaceOfGlowingCloudPositionDependent(this);
	}
	

	/**
	 * The function that determines by what factor the glow colour gets multiplied as a function of position.
	 * Override this function for a different position dependency.
	 * @param position
	 * @return a position-dependent factor by which the colour gets multiplied
	 */
	private double getPositionFactor(Vector3D position)
	{
		// based on physics: falls off with 1/r^2
		// return MyMath.square(10.)/position.getModSquared();	// 1/(r/10)^2, where r is the distance from the camera
		
		// based on giving good contrast: falls of exponentially
		// return Math.exp(-2.*(position.z-10));
		
		// Gaussian centred at centre
		Vector3D centre = new Vector3D(-1, 0, 10);
		return Math.exp(-Vector3D.difference(position, centre).getModSquared() / 10);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		double distance;
		
		// Are we travelling into the surface?  Compare to the (outwards-pointing) surface normal!
		if(Vector3D.scalarProduct(ray.getD(), i.getNormalisedOutwardsSurfaceNormal()) > 0)
		{
			// the ray's direction points outwards
			
			distance = 0;
		}
		else
		{
			// the ray's direction points inwards

			// find the next intersection of this ray
			RaySceneObjectIntersection i2 = i.o.getNextClosestRayIntersection(ray, i);
			
			if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// there is no other intersection with anything
				distance = MyMath.HUGE;
			}
			else
			{
				distance = Vector3D.difference(i2.p, i.p).getLength();
			}
		}

		// 0 for distance=0, goes to 1 as distance goes to infinity
		double f = 1-Math.exp(-distance / getOneOverEDistance());
		
		// keep tracing the ray from the intersection point
		DoubleColour c = scene.getColourAvoidingOrigin(
				ray.getBranchRay(
						i.p,
						ray.getD(),
						i.t
				),
				i.o,
				lights,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			),
			glowColour = getGlowColour().multiply(getPositionFactor(i.p));

		return new DoubleColour(
				c.getR() + glowColour.getR()*f,
				c.getG() + glowColour.getG()*f,
				c.getB() + glowColour.getB()*f
			);
	}
}
