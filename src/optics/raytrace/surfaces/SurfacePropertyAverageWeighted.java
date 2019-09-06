package optics.raytrace.surfaces;

import java.util.ArrayList;

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
 * Compared to SurfacePropertyContainer, this class allows different surface properties to be weighted.
 * 
 * @see optics.raytrace.surfaces.SurfacePropertyContainer
 */
public class SurfacePropertyAverageWeighted extends SurfacePropertyAverage
{
	private static final long serialVersionUID = 6930844958537668166L;

	private ArrayList<Double> weightings;	// the surface properties' weighting factors

	/**
	 * Create an empty collection of surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyAverageWeighted#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyAverageWeighted()
	{
		super();
		weightings = new ArrayList<Double>();
	}
	
	/**
	 * Create a collection of (initially) two surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyAverageWeighted#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyAverageWeighted(SurfaceProperty sp1, SurfaceProperty sp2)
	{
		super(sp1, sp2);
	}
	
	public SurfacePropertyAverageWeighted(SurfaceProperty sp1, double weight1, SurfaceProperty sp2, double weight2)
	{
		this();
		add(sp1, weight1);
		add(sp2, weight2);
	}
	
	/**
	 * Create a collection of surface properties representing a coloured, (partially) transmissive,
	 * and (partially) reflective surface
	 * 
	 * @param colour
	 * @param colourWeighting
	 * @param transmissionCoefficient
	 * @param reflectionCoefficient
	 */
	public SurfacePropertyAverageWeighted(
			SurfaceColour colour,
			double colourWeighting,
			double transmissionCoefficient,
			double reflectionCoefficient)
	{
		this();
		add(colour, colourWeighting);
		add(Transparent.PERFECT, transmissionCoefficient);
		add(Reflective.PERFECT_MIRROR, reflectionCoefficient);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurfacePropertyAverageWeighted(SurfacePropertyAverageWeighted original)
	{
		this();

		// copy clones of all the surface properties in original into this
		for(int i=0; i<original.size(); i++)
		{
			add(original.get(i).clone(), original.getWeighting(i));
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfacePropertyAverageWeighted clone()
	{
		return new SurfacePropertyAverageWeighted(this);
	}

	/**
	 * Add a surface property to container
	 * 
	 * @param sp	surface property to be added
	 * @param weighting	the weighting of the surface property to be added
	 */
	public void add(SurfaceProperty sp, double weighting)
	{		
		surfaceProperties.add(sp);
		weightings.add(weighting);
	}
	
	/**
	 * Add a surface property to container.
	 * The weighting is automatically set to 1.
	 * 
	 * @param sp	surface property to be added
	 */
	@Override
	public void add(SurfaceProperty sp)
	{
		add(sp, 1.0);
	}

	/**
	 * Get the weighting with a given index
	 */
	public double getWeighting(int index)
	{
		return weightings.get(index);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)   //Ray r is the incoming light ray
	throws RayTraceException
	{
		DoubleColour
			c,	// current colour
			sumColour = new DoubleColour(0,0,0);
		
		int n = surfaceProperties.size();	// number of surface properties
		
		for(int j=0; j<n; j++) {
			c = surfaceProperties.get(j).getColour(r, i, scene, l, traceLevel, raytraceExceptionHandler);
			sumColour = sumColour.add(c.multiply(weightings.get(j)));
		}

		return sumColour;
//		return sumColour.capSaturatedComponents();
// 		return new DoubleColour(sumColour.r/n, sumColour.g/n, sumColour.b/n);
	}
}
