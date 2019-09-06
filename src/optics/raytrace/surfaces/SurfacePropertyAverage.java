package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A collection of surface properties that combine into one surface property.
 */
public class SurfacePropertyAverage extends SurfacePropertyContainer
{
	private static final long serialVersionUID = 4497425576516750550L;

	/**
	 * Create an empty collection of surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyAverage#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyAverage()
	{
		super();
	}
	
	/**
	 * Create a collection of (initially) two surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyAverage#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyAverage(SurfaceProperty sp1, SurfaceProperty sp2)
	{
		super(sp1, sp2);
	}
	
	/**
	 * Create a collection of surface properties representing a coloured, (partially) transmissive,
	 * and (partially) reflective surface
	 * 
	 * @param diffuseColour
	 * @param specularColour
	 * @param transmissionCoefficient
	 * @param reflectionCoefficient
	 */
	public SurfacePropertyAverage(
			DoubleColour diffuseColour,
			DoubleColour specularColour,
			double transmissionCoefficient,
			double reflectionCoefficient,
			boolean shadowThrowing)
	{
		super(
				new SurfaceColour(diffuseColour, specularColour, shadowThrowing),
				new Transparent(transmissionCoefficient, shadowThrowing),
				new Reflective(reflectionCoefficient, shadowThrowing)
			);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurfacePropertyAverage(SurfacePropertyAverage original)
	{
		super(original);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfacePropertyAverage clone()
	{
		return new SurfacePropertyAverage(this);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)   //Ray r is the incoming light ray
	throws RayTraceException
	{
		DoubleColour sumColour = new DoubleColour(0,0,0);
		
		int n = surfaceProperties.size();	// number of surface properties
		DoubleColour colour;
		for(int j=0; j<n; j++) {
			//c = surfaceProperties.get(j).getColour(r, i, scene, l, traceLevel);
            SurfaceProperty surfaceProperty = surfaceProperties.get(j);
            colour = surfaceProperty.getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
			sumColour = sumColour.add(colour);
		}
        return sumColour;
		//return sumColour.capSaturatedComponents();
// 		return new DoubleColour(sumColour.r/n, sumColour.g/n, sumColour.b/n);
	}
}
