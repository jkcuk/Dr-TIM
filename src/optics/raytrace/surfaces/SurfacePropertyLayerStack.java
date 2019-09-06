package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A stack of layers with different surface properties.
 * 
 * This allows things like a refracting surface property sitting underneath one that makes everything blue, 
 * or a combination of GCLAs and Pixellation that simulates pixellated GCLAs.
 * (Note that in the former example the order of the elements doesn't matter, but in the latter it does.)
 */
public class SurfacePropertyLayerStack extends SurfacePropertyContainer
{
	private static final long serialVersionUID = -5683233590772297299L;

	/**
	 * Create an empty stack of surface-property layers.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyLayerStack#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyLayerStack()
	{
		super();
	}
	
	/**
	 * Create a series of (initially) two surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyLayerStack#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyLayerStack(SurfaceProperty sp1, SurfaceProperty sp2)
	{
		super(sp1, sp2);
	}

	public SurfacePropertyLayerStack(SurfaceProperty sp1, SurfaceProperty sp2, SurfaceProperty sp3)
	{
		super(sp1, sp2, sp3);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurfacePropertyLayerStack(SurfacePropertyLayerStack original)
	{
		super(original);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SurfacePropertyLayerStack clone()
	{
		return new SurfacePropertyLayerStack(this);
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)   //Ray r is the incoming light ray
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// is the ray intersecting the surface inwards or outwards?
		Orientation o = i.getRayOrientation(r);
		InsideOfSurfacePropertyLayerStack insideOfSurfacePropertyLayerStack;
		int indexOfCurrentLayer;
		
		if(scene instanceof InsideOfSurfacePropertyLayerStack)
		{
			insideOfSurfacePropertyLayerStack = (InsideOfSurfacePropertyLayerStack)scene;
			indexOfCurrentLayer =
					insideOfSurfacePropertyLayerStack.getIndexOfCurrentLayer() +
					((o==Orientation.INWARDS)?1:-1);
			insideOfSurfacePropertyLayerStack.setIndexOfCurrentLayer(indexOfCurrentLayer);

			// System.out.println("Inside stack.  Current layer: "+indexOfCurrentLayer);

			if((indexOfCurrentLayer < 0) || (indexOfCurrentLayer >= surfaceProperties.size()))
			{
				// index out of bounds -- the ray is leaving the stack
				// System.out.println("Leaving stack.");

				scene = insideOfSurfacePropertyLayerStack.getParent();
				
				return scene.getColourAvoidingOrigin(
						r,
						i.o,
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
					);
			}
			else
			{
				// System.out.println("Ray = "+r);
				SurfaceProperty surfaceProperty = surfaceProperties.get(indexOfCurrentLayer);
		        return surfaceProperty.getColour(
		        		r,
		        		i,
		        		insideOfSurfacePropertyLayerStack,
		        		l,
		        		traceLevel-1,
		        		raytraceExceptionHandler
		        	);
			}
		}
		else
		{
			// the SurfacePropertyLayerStack is first hit
			
			// if it is hit inwards, start with the first layer, layer 0;
			// otherwise, start with the last layer
			indexOfCurrentLayer = (o==Orientation.INWARDS)?0:surfaceProperties.size()-1;
			
			insideOfSurfacePropertyLayerStack = new InsideOfSurfacePropertyLayerStack(
					this,	// surfaceProperty
					indexOfCurrentLayer,	// number of first surface property
					i,
					scene,	// parent
					scene.getStudio()
				);
			SurfaceProperty surfaceProperty = surfaceProperties.get(indexOfCurrentLayer);
	        return surfaceProperty.getColour(r, i, insideOfSurfacePropertyLayerStack, l, traceLevel-1, raytraceExceptionHandler);
		}
	}	
}
