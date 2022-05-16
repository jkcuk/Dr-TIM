package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.exceptions.RayTraceException;
import math.MyMath;
import math.Vector3D;

/**
 * This surface property works out how much of any light ray's trajectory passes through the interior
 * of the scene object with this surface property; the more of the light ray passes through the interior
 * of the scene object, the more of the glow colour gets <b>added</b> to the ray.
 * 
 * What on earth does the "cloud" bit in the name mean?
 * When working out how much of the light ray's trajectory passes through the interior, this
 * class ignores other scene objects.
 * This means that a backwards-traced ray that ends on another scene object shortly after entering a
 * scene object with this surface property still gets the full colour corresponding to the whole depth
 * of the object added to it.
 * It also means that several <b>intersecting</b> objects with this surface property each add the full
 * colour corresponding to their whole depth.  This makes it suitable to model things like glowing light
 * rays.
 * 
 * @author Johannes Courtial
 */
public class SurfaceOfGlowingCloud extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = -5472781537304192406L;

	private DoubleColour glowColour;
	private double oneOverEDistance;
	private boolean shadowThrowing = true;
	
	/**
	 * Creates a new tinted-solid surface.
	 */
	public SurfaceOfGlowingCloud(DoubleColour glowColour, double oneOverEDistance, boolean shadowThrowing)
	{
		setGlowColour(glowColour);
		setOneOverEDistance(oneOverEDistance);
		setShadowThrowing(shadowThrowing);
	}
	
	/**
	 * Clone the original tinted-solid surface
	 * @param original
	 */
	public SurfaceOfGlowingCloud(SurfaceOfGlowingCloud original)
	{
		setGlowColour(original.getGlowColour());
		setOneOverEDistance(original.getOneOverEDistance());
		setShadowThrowing(original.isShadowThrowing());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceOfGlowingCloud clone()
	{
		return new SurfaceOfGlowingCloud(this);
	}
	
	
	// setters and getters

	public DoubleColour getGlowColour() {
		return glowColour;
	}

	public void setGlowColour(DoubleColour glowColour) {
		this.glowColour = glowColour;
	}
	
	public double getOneOverEDistance() {
		return oneOverEDistance;
	}

	public void setOneOverEDistance(double oneOverEDistance) {
		this.oneOverEDistance = oneOverEDistance;
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

		// 1 for distance=0, goes to zero as distance goes to infinity
		double f = Math.exp(-distance / getOneOverEDistance());
		
		// keep tracing the ray from the intersection point
		DoubleColour c = scene.getColourAvoidingOrigin(
				ray.getBranchRay(
						i.p,
						ray.getD(),
						i.t,
						ray.isReportToConsole()
				),
				i.o,
				lights,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			),
			glowColour = getGlowColour();

		return new DoubleColour(
				c.getR() + glowColour.getR()*(1-f),
				c.getG() + glowColour.getG()*(1-f),
				c.getB() + glowColour.getB()*(1-f)
			);
	}
	
	@Override
	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	@Override
	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
}
