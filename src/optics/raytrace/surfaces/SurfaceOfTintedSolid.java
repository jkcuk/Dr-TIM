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
 * The surface of a solid that filters the colour of light rays passing through it.
 * The filter properties are defined in terms of separate absorption coefficients for the
 * red, green and blue colour components.
 * For example, the red component of a light ray travelling a distance <i>d</i> inside the
 * solid will be multiplied by a factor exp(-<i>alpha</i><sub>R</sub> <i>d</i>), where
 * <i>alpha</i><sub>R</sub> is the absorption coefficient for the red component.
 *
 * @author Johannes Courtial
 */
public class SurfaceOfTintedSolid extends SurfaceProperty implements SurfacePropertyWithControllableShadow
{
	private static final long serialVersionUID = -2417363425831462532L;

	// the tint colour is defined by the RGB absorption coefficients
	private double
		redAbsorptionCoefficient,
		greenAbsorptionCoefficient,
		blueAbsorptionCoefficient;
	private boolean shadowThrowing = true;
	
	/**
	 * Creates a new tinted-solid surface.
	 */
	public SurfaceOfTintedSolid(
			double redAbsorptionCoefficient,
			double greenAbsorptionCoefficient,
			double blueAbsorptionCoefficient,
			boolean shadowThrowing
		)
	{
		setAbsorptionCoefficients(redAbsorptionCoefficient, greenAbsorptionCoefficient, blueAbsorptionCoefficient);
		setShadowThrowing(shadowThrowing);
	}
	
	public SurfaceOfTintedSolid(DoubleColour colour, boolean shadowThrowing)
	{
		this(colour.getR(), colour.getB(), colour.getG(), shadowThrowing);
	}
	
	/**
	 * Clone the original tinted-solid surface
	 * @param original
	 */
	public SurfaceOfTintedSolid(SurfaceOfTintedSolid original)
	{
		setAbsorptionCoefficients(
				original.getRedAbsorptionCoefficient(),
				original.getGreenAbsorptionCoefficient(),
				original.getBlueAbsorptionCoefficient()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfaceOfTintedSolid clone()
	{
		return new SurfaceOfTintedSolid(this);
	}
	
	
	// setters and getters
	
	public void setAbsorptionCoefficients(
			double redAbsorptionCoefficient,
			double greenAbsorptionCoefficient,
			double blueAbsorptionCoefficient
		)
	{
		this.redAbsorptionCoefficient = redAbsorptionCoefficient;
		this.greenAbsorptionCoefficient = greenAbsorptionCoefficient;
		this.blueAbsorptionCoefficient = blueAbsorptionCoefficient;
	}

	public double getRedAbsorptionCoefficient() {
		return redAbsorptionCoefficient;
	}

	public double getGreenAbsorptionCoefficient() {
		return greenAbsorptionCoefficient;
	}

	public double getBlueAbsorptionCoefficient() {
		return blueAbsorptionCoefficient;
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
			
			// the distance the ray travelled inside the object is the distance between the intersection point
			// and the ray's start point
			distance = Vector3D.difference(i.p, ray.getP()).getLength();
		}
		else
		{
			// the ray's direction points inwards

			// find the next intersection of this ray
			RaySceneObjectIntersection i2 = scene.getNextClosestRayIntersection(ray, i);
			
			if(i2 == RaySceneObjectIntersection.NO_INTERSECTION)
			{
				// there is no other intersection with anything
				distance = MyMath.HUGE;
			}
			else
			{
				if(i2.o == i.o)
					distance = 0;	// the ray intersects again with this scene-object primitive; let the next intersection deal with this
				else
					distance = Vector3D.difference(i2.p, i.p).getLength();
			}
		}

		
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
			);

		// multiply each of the colour components by exp(- alpha * distance), where alpha is the
		// absorption coefficient for this colour component
		return new DoubleColour(
				c.getR() * Math.exp(-getRedAbsorptionCoefficient()*distance),
				c.getG() * Math.exp(-getGreenAbsorptionCoefficient()*distance),
				c.getB() * Math.exp(-getBlueAbsorptionCoefficient()*distance)
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
