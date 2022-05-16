package optics.raytrace.core;

import java.util.Vector;

import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.sceneObjects.RayTrajectory;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;

import math.*;

/**
 * This class represents a ray that records any intersection points, intersection times, etc. during its history.
 * The ray trajectory can then be plotted.
 * 
 * @author Johannes
 *
 */
public class RayWithTrajectory extends Ray
{
	/**
	 * A list of points where the ray has intersected objects while being traced through the
	 * optical system.  Note that there may be further intersection points in the branch rays.
	 */
	private Vector<Vector3D> intersectionPoints;
	
	/**
	 * list of intersection times
	 */
	private Vector<Double> intersectionTimes;
	
	/**
	 * A list of branches, e.g. the ray reflected from a surface.  
	 */
	private Vector<RayWithTrajectory> branchRays;
	
	/**
	 * A light ray is defined by an initial position and a direction in which it propagates.
	 * @param p The position of the light ray.
	 * @param d The direction in which the ray propagates.
	 * @param t	The time the light ray passes the position p
	 * @param reportToConsole if true, the ray tracer reports on this ray's progress on the console
	 */
	public RayWithTrajectory(Vector3D p, Vector3D d, double t, boolean reportToConsole)
	{
		super(p, d, t, reportToConsole);
		
		// initialise intersectionPoints vector
		intersectionPoints = new Vector<Vector3D>();
		
		intersectionTimes = new Vector<Double>();
		
		// initialise branchRays vector
		branchRays = new Vector<RayWithTrajectory>();
		
		setP(p);	// this adds the start point to the list of intersection points
		setT(t);

		firstReport();
	}
	
	public RayWithTrajectory(Vector3D p, Vector3D k, Vector3D d, double t, boolean reportToConsole)
	{
		this(p, d, t, reportToConsole);
		
		setK(k);
	}

	
	/**
	 * Constructor that simply sets its private variables to the values supplied
	 * @param p
	 * @param d
	 * @param t
	 * @param intersectionPoints
	 * @param intersectionTimes
	 * @param branchRays
	 * @param reportToConsole
	 */
	public RayWithTrajectory(Vector3D p, Vector3D d, double t, Vector<Vector3D> intersectionPoints, Vector<Double> intersectionTimes, Vector<RayWithTrajectory> branchRays, boolean reportToConsole)
	{
		super(p, d, t, reportToConsole);
		
		this.intersectionPoints = intersectionPoints;
		this.intersectionTimes = intersectionTimes;
		this.branchRays = branchRays;
		firstReport();
	}
	
	@Override
	public boolean isRayWithTrajectory()
	{
		return true;
	}

	/**
	 * Create the first report on this ray's progress
	 */
	protected void firstReport()
	{
		if(isReportToConsole())
			System.out.println("New ray created, possibly a branch ray.  Position "+getP()+", direction "+getD()+", time "+getT());
	}

	/**
	 * Return a branch of this ray with initial position p and direction d.
	 * @param p
	 * @param d
	 * @return
	 */
	@Override
	public RayWithTrajectory getBranchRay(Vector3D p, Vector3D d, double t, boolean reportToConsole)
	{
		RayWithTrajectory branchRay = new RayWithTrajectory(p, d, t, reportToConsole);
		
		addBranchRay(branchRay);
		
		return branchRay;
	}

	@Override
	public RayWithTrajectory getBranchRay(Vector3D p, Vector3D k, Vector3D d, double t, boolean reportToConsole)
	{
		RayWithTrajectory branchRay = new RayWithTrajectory(p, k, d, t, reportToConsole);
		
		addBranchRay(branchRay);
		
		return branchRay;
	}

	/* 
	 * Override so that advanced ray is another RayWithTrajectory
	 */
	@Override
	public Ray getAdvancedRay(double a)
	{
		// TODO time calculation is wrong in medium, and k might need to be re-calculated
		return new RayWithTrajectory(
				getP().getSumWith(getD().getProductWith(a)),
				getD(),
				getT() - a/SpaceTimeTransformation.c,
				isReportToConsole()
			);
	}

	/**
	 * Set the current position of this light ray.
	 * @param p The new light ray position.
	 */
	@Override
	public void setP(Vector3D p)
	{
		super.setP(p);
		addIntersectionPoint(p);
	}

	@Override
	public void setT(double t)
	{
		super.setT(t);
		addIntersectionTime(t);
	}

	/**
	 * Call when the ray has encountered another intersection point while tracing it
	 * through the optical system.  The intersection point will simply be stored in a
	 * list of intersection points for later use.
	 * @see optics.raytrace.core.SceneObjectClass#getColour
	 * @see optics.raytrace.core.SceneObjectClass#getColourAvoidingOrigin
	 * 
	 * @param intersectionPoint
	 */
	public void addIntersectionPoint(Vector3D intersectionPoint)
	{
		intersectionPoints.add(intersectionPoint);
		if(isReportToConsole())
		{
			System.out.println("Intersection point added to ray: "+intersectionPoint+". Ray position "+getP()+", direction "+getD()+", time "+getT());
		}
	}
	
	public void removeLastIntersectionPoint()
	{
		if(isReportToConsole())
		{
			System.out.println("Intersection poin removed: "+intersectionPoints.get(intersectionPoints.size()-1)+". Ray position "+getP()+", direction "+getD()+", time "+getT());
		}
		intersectionPoints.remove(intersectionPoints.size()-1);
	}
	
	public void addIntersectionTime(double t)
	{
		intersectionTimes.add(t);
	}
	
	/**
	 * Return the list of points the ray has encountered while it has been traced through
	 * the optical system.
	 * @return
	 */
	public Vector<Vector3D> getIntersectionPoints()
	{
		return intersectionPoints;
	}
	
	public Vector<Double> getIntersectionTimes()
	{
		return intersectionTimes;
	}
	
	/**
	 * Call when the ray has been continued in the form of a branch.
	 * This happens, for example, when part of the ray gets transmitted or reflected
	 * at some surface.
	 * @see optics.raytrace.core.SceneObjectClass
	 * @param branchRay
	 */
	public void addBranchRay(RayWithTrajectory branchRay)
	{
		branchRays.add(branchRay);
	}
	
	/**
	 * Return the list of branches of this ray.
	 * @return
	 */
	public Vector<RayWithTrajectory> getBranchRays()
	{
		return branchRays;
	}
	
	
	/**
	 * Calculate the trajectories of all rays with trajectory contained in scenePart.
	 * @param scenePart
	 * @param scene	the scene the rays are being traced through
	 */
	private static void traceRaysWithTrajectory(SceneObject scenePart, SceneObject scene)
	{
		// System.out.println("RayWithTrajectory::traceRaysWithTrajectory(" + scenePart.getDescription());
		
		if(scenePart instanceof RayTrajectory)
		{
			// System.out.println("Calculating trajectory of ray "+scenePart);
			((RayTrajectory)scenePart).calculateTrajectory(scene);
		}
		else if(scenePart instanceof SceneObjectContainer)
		{
			SceneObjectContainer c=(SceneObjectContainer)scenePart;
			for(int i=0; i < c.getNumberOfSceneObjects(); i++)
			{
				traceRaysWithTrajectory(c.getSceneObject(i), scene);
			}
		}
		else if(scenePart instanceof EditableSceneObjectCollection)
		{
			SceneObjectContainer c=((EditableSceneObjectCollection)scenePart).getSceneObjectContainer();
			for(int i=0; i < c.getNumberOfSceneObjects(); i++)
			{
				traceRaysWithTrajectory(c.getSceneObject(i), scene);
			}
		}
	}
	
	/**
	 * Calculate the trajectory of all rays with trajectory in the scene
	 * @param scene	the scene
	 */
	public static void traceRaysWithTrajectory(SceneObject scene)
	{
		// check recursively if anything is instanceof RayWithTrajectory
		traceRaysWithTrajectory(scene, scene);
	}



	/**
	 * Return a string representation of the light ray using the syntax ray(p, d)
	 * @return The string representation.
	 */ 
	@Override
	public String toString()
	{
		return "<RayWithTrajectory>\n"+
		"<p>"+getP()+"</p>\n"+
		"<d>"+getD()+"</d>\n"+
		((getK()!=null)?("<k>"+getK()+"</k>\n"):"") +
		"<t>"+getT()+"</t>\n"+
		"</RayWithTrajectory>";
	}
}

