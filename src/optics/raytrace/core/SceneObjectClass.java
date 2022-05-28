package optics.raytrace.core;

import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;

import math.MyMath;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;

import java.io.*;

/**
 * An abstract implementation of the SceneObject interface
 * 
 * @author Johannes Courtial
 */
public abstract class SceneObjectClass implements SceneObject, Serializable, Cloneable
{
	private static final long serialVersionUID = 4105818186638759362L;
	
	// the studio as a whole
	private Studio studio;
	
	/**
	 * the SceneObject one level up in the hierarchy
	 */
	private SceneObject parent;

	/**
	 * A more neutral object: a white "sky sphere"
	 */
	public static EditableScaledParametrisedSphere getHeavenSphere(SceneObject parent, Studio studio)
	{
		return new EditableScaledParametrisedSphere(
			"Heaven",
			new Vector3D(0,0,0),	// centre
			MyMath.HUGE,	// huge radius
			new SurfaceColourLightSourceIndependent(DoubleColour.WHITE, true),
			parent,
			studio
		);
	}

	/**
	 * A common object found in a lot of scenes is a blue sky
	 */
	public static EditableScaledParametrisedSphere getSkySphere(double brightnessFactor, SceneObject parent, Studio studio)
	{
		return new EditableScaledParametrisedSphere(
			"sky",
			new Vector3D(0,0,0),	// centre
			MyMath.HUGE,	// huge radius
			new SurfaceColourLightSourceIndependent(DoubleColour.LIGHT_BLUE.multiply(brightnessFactor), true),
			parent,
			studio
		);
	}
	
	public static EditableScaledParametrisedSphere getSkySphere(SceneObject parent, Studio studio)
	{
		return getSkySphere(1, parent, studio);
	}


	/**
	 * A common object often used is a chequerboard floor.
	 */
	public static EditableParametrisedPlane getChequerboardFloor(double yCoordinate, SceneObject parent, Studio studio)
	{
		return new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, yCoordinate, 0),	// point on plane
				new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
				new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				new EditableSurfaceTiling(SurfaceColour.GREY50_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, studio.getScene()),
				parent,
				studio
		);
	}
	
	public static EditableParametrisedPlane getChequerboardFloor(SceneObject parent, Studio studio)
	{
		return getChequerboardFloor(-1, parent, studio);
	}

	public static EditableParametrisedPlane getLighterChequerboardFloor(double yCoordinate, SceneObject parent, Studio studio)
	{
		return new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, yCoordinate, 0),	// point on plane
				new Vector3D(1, 0, 0),	// Vector3D 1 that spans plane
				new Vector3D(0, 0, 1),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				new EditableSurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, studio.getScene()),
				parent,
				studio
		);
	}

	public static EditableParametrisedPlane getLighterChequerboardFloor(SceneObject parent, Studio studio)
	{
		return getLighterChequerboardFloor(-1-MyMath.TINY, parent, studio);
	}

	/**
	 * Store a brief description of the particular object.  
	 * This simplifies the design of what the object actually represents.
	 */
	public String description="";

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * The constructor with which an object may be realized.  This is included 
	 * in order to legally implement the Serializable interface and hence to
	 * allow loading and saving of scene objects.
	 */
	public SceneObjectClass(String description, SceneObject parent, Studio studio)
	{
		setDescription(description);
		setParent(parent);
		setStudio(studio);
	}
	
	public SceneObjectClass(SceneObjectClass original)
	{
		setDescription(original.getDescription());
		setParent(original.getParent());
		setStudio(original.getStudio());
	}
	
	/**
	 * At what point on the object did the ray intersect?  If the intersection didn't take place then 
	 * RaySceneObjectIntersection.NO_INTERSECTION should be returned.
	 * @param ray 
	 * @return The closest intersection between the ray and this SceneObject or any SceneObject contained in it.
	 */
//	 @Override
//	 public abstract RaySceneObjectIntersection getClosestRayIntersection(Ray ray);
	
	/**
	 * Calculates the closest intersection between the ray and this SceneObject or any SceneObjects contained
	 * within it.
	 * 
	 * The SceneObject originObject is the SceneObject on which the ray originated, so it should intersect  
	 * with the ray at the ray's starting point.
	 * We are not interested in this point, but we are interested in other points where ray and originObject might intersect.
	 * This is why originObject is being passed around.
	 * 
	 * @param ray		the ray
	 * @param originObject	the SceneObject on which ray originated
	 * @return			the closest intersection between the ray and this SceneObject
	 */
//	 @Override
//	 public abstract RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObject originObject);
	
	/**
	 * Finds the (n+1)th-closest intersection between the ray and this.
	 * 
	 * This is useful if the nth-closest intersection is not what we were looking for.
	 * 
	 * @param ray	ray
	 * @param i	the nth-closest intersection
	 * @return	the (n+1)th-closest intersection
	 */
	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersection(Ray ray, RaySceneObjectIntersection i)
	{
		// return getClosestRayIntersection(new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole()).advance(MyMath.TINY));
		return getClosestRayIntersectionAvoidingOrigin(new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole()), i.o);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray, RaySceneObjectIntersection i)
	{
		// return getClosestRayIntersectionWithShadowThrowingSceneObject(new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole()).advance(MyMath.TINY));
		return getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole()), i.o);
	}

	/**
	 * Finds the (n+1)th-closest intersection between the ray and this.
	 * 
	 * This is useful if the nth-closest intersection is not what we were looking for.
	 * 
	 * @param ray	ray
	 * @param excludeObject	do not intersect with this object (usually the one on which the ray originated)
	 * @param i	the nth-closest intersection
	 * @return	the (n+1)th-closest intersection
	 */
	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive excludeObject, RaySceneObjectIntersection i)
	{
//		if(ray.hasTrajectory())
//			return getClosestRayIntersectionAvoidingOrigin(new RayWithTrajectory(i.p.add(ray.getD().multiply(MyMath.TINY)), ray.getD()), originObject);

		return getClosestRayIntersectionAvoidingOrigin(
				new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole())//.advance(MyMath.TINY)
				, i.o	// excludeObject
			);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i)
	{
		return getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(
				new Ray(i.p, ray.getD(), i.t, ray.isReportToConsole())//.advance(MyMath.TINY)
				, i.o	// originObject
			);
	}

	/**
	 * Returns the SceneObject, transformed according to a geometrical transformation.
	 * 
	 * @param t	the geometrical transformation
	 * @return the transformed SceneObject
	 */
	// @Override
	// public abstract SceneObject transform(Transformation t);

	/**
	* Is the position p inside the SceneObject?
	* 
	* @param p	a position
	* @return true if p is inside the Scene, false otherwise
	*/
	// @Override
	// public abstract boolean insideObject(Vector3D p);

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getColour(optics.raytrace.core.Ray, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel < 0) return DoubleColour.BLACK;
		
		RaySceneObjectIntersection intersection = getClosestRayIntersection(ray);

//		if(ray.isReportToConsole())
//		{
//			System.out.println("Intersection found: "+intersection.toOneLiner()+" (SceneObjectClass::getColour)");
//		}
		
		return getColourAtIntersection(
				intersection,
				ray,
				l,
				scene,
				traceLevel,
				raytraceExceptionHandler
			);
	}

//	@Override
//	public void traceRayWithTrajectory(RayWithTrajectory ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
//	throws RayTraceException
//	{
//		if(traceLevel >= 0)
//		{
//		RaySceneObjectIntersection intersection = getClosestRayIntersection(ray);
//
//		getColourAtIntersection(
//				intersection,
//				ray,
//				l,
//				scene,
//				traceLevel,
//				raytraceExceptionHandler
//			);
//		}
//	}

	@Override
	public DoubleColour getColourAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel < 0) return DoubleColour.BLACK;
		
		RaySceneObjectIntersection intersection = getClosestRayIntersectionAvoidingOrigin(ray, originObject);

		return getColourAtIntersection(
				intersection,
				ray,
				l,
				scene,
				traceLevel,
				raytraceExceptionHandler
			);
	}
	
	/**
	 * For a specific intersection point, calculate the corresponding colour
	 * @param intersection
	 * @param ray
	 * @param l
	 * @param scene
	 * @param traceLevel
	 * @return the colour
	 * @throws RayTraceException
	 */
	protected DoubleColour getColourAtIntersection(RaySceneObjectIntersection intersection, Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// if there is no intersection...
		if (intersection == RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// ... let the RaytraceExceptionHandler handle it
			return raytraceExceptionHandler.getColourOfRayFromNowhere(ray, intersection.o, l, scene, traceLevel);
		}

		// just in case: do something if, for some reason, the object is null
		if (intersection.o==null)
		{
			throw new RayTraceException("Unexpected null scene object.");
		}

		if(ray.isRayWithTrajectory()) ((RayWithTrajectory)ray).addIntersectionPoint(intersection.p);
		
		return intersection.o.getColourAtIntersection(ray, intersection, scene, l, traceLevel-1, raytraceExceptionHandler);
	}


	@Override
	public String toString()
	{
          return "<SceneObject>\n"+
          "<description>" + description + "</description>\n"+
          "</SceneObject>";
          // return description;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract SceneObject clone();
	
	@Override
	public Studio getStudio()
	{
		return studio;
	}

	@Override
	public void setStudio(Studio studio)
	{
		this.studio = studio;
	}

	@Override
	public SceneObject getParent() {
		return parent;
	}

	@Override
	public void setParent(SceneObject parent) {
		this.parent = parent;
	}
	
	/**
	 * Given a straight line with direction straightLineDirection, this method calculates a vector that, for a given surface
	 * with outwards surface normal <outwardsNormal>, either points inwards (if orientation takes the value Orientation.INWARDS)
	 * or outwards (if orientation takes the value Orientation.OUTWARDS). 
	 * @param orientation
	 * @param straightLineDirection
	 * @param outwardsNormal
	 * @return
	 */
	public static Vector3D getStraightLineContinuation(Orientation orientation, Vector3D straightLineDirection, Vector3D outwardsNormal)
	{
		if(orientation == Orientation.getOrientation(straightLineDirection, outwardsNormal))
		{
			return straightLineDirection;
		}
		else
		{
			return straightLineDirection.getReverse();
		}
	}

	/**
	 * 
	 * @param straightLineDirection
	 * @param outwardsNormal
	 * @return
	 */
	public static Vector3D getInwardsPointingStraightLineContinuation(Vector3D straightLineDirection, Vector3D outwardsNormal)
	{
		return getStraightLineContinuation(Orientation.INWARDS, straightLineDirection, outwardsNormal);
	}

	public static Vector3D getOutwardsPointingStraightLineContinuation(Vector3D straightLineDirection, Vector3D outwardsNormal)
	{
		return getStraightLineContinuation(Orientation.OUTWARDS, straightLineDirection, outwardsNormal);
	}

}
