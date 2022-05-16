package optics.raytrace.surfaces;

import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;

/**
 * One of the two pixellated sides of a volume with baffles that separate the pixels.
 * Both pixellated sides are covered in rectangular arrays of pixels; to change this arrangement, override the calculateIndicesForPosition method.
 * The only way for a light ray that has entered the volume to exit it again is by it exiting through a pixel of one of the pixellated surfaces
 * that has the same indices as the pixel through which it entered.
 * 
 * @author johannes
 */
public class SurfaceOfBaffledPixellatedVolume
extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -6530856384587839141L;

	/**
	 * The parametrised scene object this surface property is associated with
	 */
	private ParametrisedObject sceneObject;
	
	/**
	 * The other pixellated surface of the same volume
	 */
	private SurfaceOfBaffledPixellatedVolume otherPixellatedSurface;
	
	/**
	 * Any remaining surfaces of the volume
	 */
	// private EditableSceneObjectCollection otherSurfaces;
	
	/**
	 * Period in u direction, i.e. the first surface coordinate
	 */
	private double uPeriod;

	/**
	 * Period in v direction, i.e. the second surface coordinate
	 */
	private double vPeriod;

	/**
	 * Offset in u direction; the pixel with indices (0,0) is located at (u,v) = (uOffset, vOffset)
	 */
	private double uOffset;

	/**
	 * Offset in v direction; the pixel with indices (0,0) is located at (u,v) = (uOffset, vOffset)
	 */
	private double vOffset;

	/**
	 * Only simulate baffles if showBaffles is true
	 */
	private boolean showBaffles;
	
	
	// constructors
	
	/**
	 * @param sceneObject
	 * @param otherPixellatedSurface
	 * @param uPeriod
	 * @param vPeriod
	 * @param uOffset
	 * @param vOffset
	 * @param showBaffles
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public SurfaceOfBaffledPixellatedVolume(
			ParametrisedObject sceneObject,
			SurfaceOfBaffledPixellatedVolume otherPixellatedSurface,
			// EditableSceneObjectCollection otherSurfaces,
			double uPeriod,
			double vPeriod,
			double uOffset,
			double vOffset,
			boolean showBaffles,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.sceneObject = sceneObject;
		this.otherPixellatedSurface = otherPixellatedSurface;
		// this.otherSurfaces = otherSurfaces;
		this.uPeriod = uPeriod;
		this.vPeriod = vPeriod;
		this.uOffset = uOffset;
		this.vOffset = vOffset;
		this.showBaffles = showBaffles;
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurfaceOfBaffledPixellatedVolume(SurfaceOfBaffledPixellatedVolume original)
	{
		this(
				original.getSceneObject(),
				original.getOtherPixellatedSurface(),
				// original.getOtherSurfaces(),
				original.getuPeriod(),
				original.getvPeriod(),
				original.getuOffset(),
				original.getvOffset(),
				original.isShowBaffles(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfacePropertyPrimitive#clone()
	 */
	public SurfaceOfBaffledPixellatedVolume clone()
	{
		return new SurfaceOfBaffledPixellatedVolume(this);
	}



	// getters & setters
	
	public ParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(ParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	public SurfaceOfBaffledPixellatedVolume getOtherPixellatedSurface() {
		return otherPixellatedSurface;
	}

	public void setOtherPixellatedSurface(SurfaceOfBaffledPixellatedVolume otherPixellatedSurface) {
		this.otherPixellatedSurface = otherPixellatedSurface;
	}

//	public EditableSceneObjectCollection getOtherSurfaces() {
//		return otherSurfaces;
//	}
//
//	public void setOtherSurfaces(EditableSceneObjectCollection otherSurfaces) {
//		this.otherSurfaces = otherSurfaces;
//	}

	public double getuPeriod() {
		return uPeriod;
	}

	public void setuPeriod(double uPeriod) {
		this.uPeriod = uPeriod;
	}

	public double getvPeriod() {
		return vPeriod;
	}

	public void setvPeriod(double vPeriod) {
		this.vPeriod = vPeriod;
	}

	public double getuOffset() {
		return uOffset;
	}

	public void setuOffset(double uOffset) {
		this.uOffset = uOffset;
	}

	public double getvOffset() {
		return vOffset;
	}

	public void setvOffset(double vOffset) {
		this.vOffset = vOffset;
	}

	public boolean isShowBaffles() {
		return showBaffles;
	}

	public void setShowBaffles(boolean showBaffles) {
		this.showBaffles = showBaffles;
	}


	/**
	 * Calculate the pixel indices associated with a given position on the surface
	 * @param position
	 * @return	the pixel indices associated with the position
	 */
	public int[] calculateIndicesForPosition(Vector3D position)
	{
		// first, find the local surface coordinates, u and v, of the position
		Vector2D uv = sceneObject.getSurfaceCoordinates(position);
		
		// next, calculate the corresponding pixel indices...
		int[] indices = new int[2];	// create a place to store the pixel indices
		indices[0] = findPixelIndex(uv.x, uPeriod, uOffset);	// calculate the index corresponding to the u direction
		indices[1] = findPixelIndex(uv.y, vPeriod, vOffset);	// calculate the index corresponding to the v direction
		
		// ... and return them
		return indices;
	}

	/**
	 * Calculate the index corresponding to coordinate value c and the given period and offset.
	 * This method can be applied to both the u and v coordinates.
	 * @param c
	 * @param period
	 * @param offset
	 * @return
	 */
	private int findPixelIndex(double c, double period, double offset)
	{
		return (int)(Math.floor((c-offset)/period+0.5));
	}
	
	
	/**
	 * Override to alter behaviour, e.g. refract ray
	 * @param rayBeforeInteractingWithSurface
	 * @param intersection
	 * @return
	 */
	public Ray calculateRayAfterInteractingWithSurface(Ray rayBeforeInteractingWithSurface, RaySceneObjectIntersection intersection)
	{
		return rayBeforeInteractingWithSurface.getBranchRay(
				intersection.p,
				rayBeforeInteractingWithSurface.getD(),
				intersection.t,
				rayBeforeInteractingWithSurface.isReportToConsole()
				);
	}

	
	// SurfaceProperty method
	
//	enum IntersectingObjectType
//	{
//		THIS_PIXELLATED_SURFACE,
//		OTHER_PIXELLATED_SURFACE,
//		// OTHER_SURFACE,
//		NONE;
//	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int, optics.raytrace.core.RaytraceExceptionHandler)
	 */
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lightSource, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;

		// Refract ray
		Ray newRay = calculateRayAfterInteractingWithSurface(ray, intersection);
		
		// Is the refracted ray travelling inside the pixellated volume?
		if(Orientation.getRayOrientation(newRay, intersection) == Orientation.OUTWARDS)
		{
			// No, it is travelling outwards, so continue raytracing as usual
			// launch a new ray from here
			return scene.getColourAvoidingOrigin(
					ray.getBranchRay(
							newRay.getP(),
							newRay.getD(),
							newRay.getT(),
							ray.isReportToConsole()
							),
					intersection.o,
					lightSource,
					scene,
					traceLevel-1,
					raytraceExceptionHandler
					).multiply(getTransmissionCoefficient());
		}
		
		// The refracted ray is travelling inside the pixellated volume
		
// 		IntersectingObjectType intersectingObject = IntersectingObjectType.NONE;
		SurfaceOfBaffledPixellatedVolume intersectingSurfaceOfBaffledPixellatedVolume = null;
		RaySceneObjectIntersection closestIntersection = RaySceneObjectIntersection.NO_INTERSECTION;
		double closestIntersectionDistance = Double.POSITIVE_INFINITY;
		
//		// find any intersection points of the refracted ray with itself, or with the other pixellated surface, or with any of the other surfaces
//		RaySceneObjectIntersection nextIntersection = (new SceneObjectContainer(null, null, (SceneObject)sceneObject, (SceneObject)(otherPixellatedSurface.getSceneObject()), otherSurfaces, null)).getClosestRayIntersectionAvoidingOrigin(newRay, intersection.o);
		
		// find any intersection points of the refracted ray with itself, ...
		RaySceneObjectIntersection intersection1 = ((SceneObject)sceneObject).getClosestRayIntersectionAvoidingOrigin(newRay, intersection.o);
		if(intersection1 != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// intersectingObject = IntersectingObjectType.THIS_PIXELLATED_SURFACE;
			intersectingSurfaceOfBaffledPixellatedVolume = this;
			closestIntersection = intersection1;
			closestIntersectionDistance = Vector3D.getDistance(intersection.p, intersection1.p);
		}
		
		// ... or with the other pixellated surface, ...
		intersection1 = ((SceneObject)(otherPixellatedSurface.getSceneObject())).getClosestRayIntersection(newRay);
		if(intersection1 != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			double intersectionDistance = Vector3D.getDistance(intersection.p, intersection1.p);
			if((intersectingSurfaceOfBaffledPixellatedVolume == null) || (intersectionDistance < closestIntersectionDistance))
			{
				intersectingSurfaceOfBaffledPixellatedVolume = otherPixellatedSurface;
				// intersectingObject = IntersectingObjectType.OTHER_PIXELLATED_SURFACE;
				closestIntersection = intersection1;
				closestIntersectionDistance = intersectionDistance;				
			}
		}
		
//		// ... or with any of the other surfaces
//		if(otherSurfaces != null)
//		{
//			intersection1 = otherSurfaces.getClosestRayIntersection(newRay);
//			if(intersection1 != RaySceneObjectIntersection.NO_INTERSECTION)
//			{
//				double intersectionDistance = Vector3D.getDistance(intersection.p, intersection1.p);
//				if((intersectingObject == IntersectingObjectType.NONE) || (intersectionDistance < closestIntersectionDistance))
//				{
//					intersectingObject = IntersectingObjectType.OTHER_SURFACE;
//					closestIntersection = intersection1;
//					closestIntersectionDistance = intersectionDistance;				
//				}
//			}
//		}
		
		// is there an intersection with one of the pixellated surfaces?
		if(intersectingSurfaceOfBaffledPixellatedVolume != null)
		{
			// yes, there is an intersection with one of the pixellated surfaces

			// calculate the indices for the initial intersection point
			int[] pixelIndices1 = calculateIndicesForPosition(intersection.p);

			// calculate the indices for the intersection point
			int[] pixelIndices2 = intersectingSurfaceOfBaffledPixellatedVolume.calculateIndicesForPosition(closestIntersection.p);
			
			// are they the same?
			if((pixelIndices1[0] == pixelIndices2[0]) && (pixelIndices1[1] == pixelIndices2[1]))
			{
				// yes, they are the same
				return getColour(newRay, closestIntersection, scene, lightSource, traceLevel-1, raytraceExceptionHandler).multiply(getTransmissionCoefficient());
			}
			else
			{
				// no, they are not the same
				return DoubleColour.BLACK;
			}
		}
		else
		{
			// no, there is no intersection with one of the pixellated surfaces
			return DoubleColour.BLACK;
		}		
	}
}
