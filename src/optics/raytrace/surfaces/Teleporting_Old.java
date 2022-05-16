package optics.raytrace.surfaces;

import java.util.ArrayList;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector2D;
import math.Vector3D;

/**
 * A teleporting surface that provides a mysterious link to another scene object.
 * This one scales the direction cosines in proportion to the basis vectors, which is <i>not</i>
 * what happens in geometric optical mappings (what happens there is simulated in the Teleporting class).
 * 
 * A ray hits the surface at coordinates (u, v) in the object's
 * local coordinate system (defined in ParametrisedSurface)
 * from a direction du*<b>u1</b> + dv*<b>v1</b> + dn*<b>n1</b>, where
 * <b>u1</b> and <b>v1</b> are the two directions locally spanning the surface of
 * the object at the intersection point (defined in AnisotropicBiaxialSurface) and
 * <b>n1</b> is the local surface normal (defined in the SceneObjectPrimitive class).
 * Then the ray exits the other object at coordinates (u, v) in that object's local
 * coordinate system and with a direction du*<b>u2</b> + dv*<b>v2</b> + dn*<b>n2</b>,
 * where <b>u2</b> and <b>v2</b> are the two directions locally spanning the surface of
 * the other object at the intersection point (defined in AnisotropicBiaxialSurface) and
 * <b>n2</b> is the local surface normal (defined in the SceneObjectPrimitive class).
 * 
 * Note that, if the ray hits the surface from the inside/outside, it leaves the other
 * object in the direction of the outside/inside.
 * 
 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinates(Vector3D p)
 * @see optics.raytrace.core.AnisotropicSurface#getVectorsForSurfacePoint(Vector3D p)
 * @see optics.raytrace.core.SceneObjectPrimitive#getNormalisedOutwardsSurfaceNormal(Vector3D p)
 * @author Johannes Courtial
 */
public class Teleporting_Old extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -4244964788427326376L;
	
	private SceneObjectPrimitive otherObject;
	
	/**
	 * Creates a new teleporting surface.
	 * @param otherObject
	 * @param teleportationCoefficient
	 */
	public Teleporting_Old(SceneObjectPrimitive otherObject, double teleportationCoefficient, boolean shadowThrowing)
	{
		super(teleportationCoefficient, shadowThrowing);
		setOtherObject(otherObject);
	}
	
	/**
	 * Creates a new, perfect, teleporting surface.
	 * @param otherObject
	 */
	public Teleporting_Old(SceneObjectPrimitive otherObject)
	{
		this(otherObject, PERFECT_TRANSMISSION_COEFFICIENT, true);
	}

	/**
	 * Clone the original teleporting surface
	 * @param original
	 */
	public Teleporting_Old(Teleporting_Old original)
	{
		this(
				original.getOtherObject(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Teleporting_Old clone()
	{
		return new Teleporting_Old(this);
	}

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
//		if (i.o==null) return DoubleColour.BLACK;
//		else if (i == RaySceneObjectIntersection.NO_INTERSECTION) return DoubleColour.BLACK;

		// i.o is the object that's been intersected
				
		// calculate the coordinates (u, v) of the intersection point in object 1's
		// local coordinate system (defined in ParametrisedSurface)
		
		// First check that the intersected object is properly parametrised...
		if(!(i.o instanceof ParametrisedObject))
		{
			throw new RayTraceException("Object with teleporting surface is not of type ParametrisedSurface");
		}
		// ... and then calculate the parameters
		Vector2D uv = ((ParametrisedObject)i.o).getSurfaceCoordinates(i.p);
		
		// now calculate the direction in the coordinate system defined by
		// the intersected surface and the way it's parametrised
		
		// First check that the intersected object is properly parametrised...
		if(!(i.o instanceof ParametrisedObject))
		{
			throw new RayTraceException("Object with teleporting surface is not of type ParametrisedObject");
		}
		// ... and then calculate the vectors u1, v1, ...
		ArrayList<Vector3D> u1v1 = ((ParametrisedObject)i.o).getSurfaceCoordinateAxes(i.p);
		Vector3D u1 = u1v1.get(0);
		Vector3D v1 = u1v1.get(1);
		// ... and n1
		Vector3D n1 = i.getNormalisedOutwardsSurfaceNormal();
		
		// calculate the light-ray direction in the basis defined by (u1, v1, n1)
		Vector3D duvn = r.getD().toBasis(u1, v1, n1);
		
		// calculate the 3D position that corresponds to the coordinates (u, v)
		// in the other object's local coordinate system; this is the new ray's
		// new starting point
		Vector3D newStartingPoint = ((One2OneParametrisedObject)getOtherObject()).getPointForSurfaceCoordinates(uv.x, uv.y);
		
		// now calculate the new direction in the coordinate system defined by
		// the other object's surface and the way it's parametrised
		ArrayList<Vector3D> u2v2 = ((ParametrisedObject)getOtherObject()).getSurfaceCoordinateAxes(newStartingPoint);
		Vector3D u2 = u2v2.get(0);
		Vector3D v2 = u2v2.get(1);
		// ... and n2
		Vector3D n2 = getOtherObject().getNormalisedOutwardsSurfaceNormal(newStartingPoint);

		// in the basis (u2, v2, n2), the components of the new light-ray direction are
		// the same as those of the original light-ray direction in the (u1, v1, n1)
		// basis; calculate the new light-ray direction in the (x, y, z) basis
		Vector3D newRayDirection = duvn.fromBasis(u2, v2, n2);

//		System.out.println("(x,y,z)="+ray.getD()+", (du,dv,dn)="+duvn+", (x',y',z')="+newRayDirection);

		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
				r.getBranchRay(newStartingPoint, newRayDirection, i.t, r.isReportToConsole()),
				i.o,
				l,
				scene,
				traceLevel-1,
				raytraceExceptionHandler
			).multiply(getTransmissionCoefficient());
	}

	public void setOtherObject(SceneObjectPrimitive otherObject)
	{
		// First check that otherObject is properly parametrised...
		if(!(otherObject instanceof ParametrisedObject))
		{
			throw new RuntimeException("Object connected to teleporting surface is not of type ParametrisedSurface");
		}
		
		// everything is okay; set the other object
		this.otherObject = otherObject;
	}

	public SceneObjectPrimitive getOtherObject()
	{
		return otherObject;
	}
}
