package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;

public class ParametrisedPlane extends Plane
implements One2OneParametrisedObject	// , AnisotropicSurface
{
	private static final long serialVersionUID = 4592493619956497926L;

	private Vector3D v1, v2; // the Vector3Ds that span the plane
	private ArrayList<Vector3D> v1v2;	// v1 and v2, in an array list

	/**
	 * Constructor that sets the normal direction explicitly.
	 * @param description
	 * @param pointOnPlane
	 * @param normal
	 * @param v1
	 * @param v2
	 * @param sp
	 * @param parent
	 * @param studio
	 */
	public ParametrisedPlane(String description, Vector3D pointOnPlane, Vector3D normal, Vector3D v1, Vector3D v2, SurfaceProperty sp, SceneObject parent, Studio studio)
	{
		super(description, pointOnPlane, normal, sp, parent, studio);
		
		setV1V2(v1, v2);
	}
	
	/**
	 * Creates a parametrised plane.
	 * The surface normal is given by spanVector3D1 x spanVector3D2, which points in the direction of the outside
	 * of the surface.
	 * 
	 * @param description
	 * @param pointOnPlane
	 * @param v1	span Vector3D 1
	 * @param v2	span Vector3D 2
	 * @param sp	the surface properties
	 */
	public ParametrisedPlane(String description, Vector3D pointOnPlane, Vector3D v1, Vector3D v2, SurfaceProperty sp, SceneObject parent, Studio studio)
	{
		super(description, pointOnPlane, Vector3D.crossProduct(v1, v2), sp, parent, studio);
		//this.description = description;
		//this.pointOnPlane = pointOnPlane;
		setV1V2(v1, v2);
		//this.sp = sp;
	}

	/**
	 * Creates a parametrised plane.
	 * Two span vectors are derived from the surface normal.
	 * 
	 * @param description
	 * @param pointOnPlane
	 * @param surfaceNormal	surface normal
	 * @param sp	the surface properties
	 */
	public ParametrisedPlane(String description, Vector3D pointOnPlane, Vector3D surfaceNormal, SurfaceProperty sp, SceneObject parent, Studio studio)
	{
		super(description, pointOnPlane, surfaceNormal, sp, parent, studio);
		
		// create a vector v2 that is perpendicular to surfaceNormal
		Vector3D v;
		if(surfaceNormal.x == 0.) v = new Vector3D(1,0,0);
		else v = new Vector3D(0, 1, 0);
		Vector3D v2 = v.getDifferenceWith(v.getProjectionOnto(surfaceNormal)).getNormalised();
		Vector3D v1 = Vector3D.crossProduct(v2, surfaceNormal);	// so that surfaceNormal = v1 x v2
		setV1V2(v1, v2);
	}

	public ParametrisedPlane(String description, SceneObject parent, Studio studio)
	{
		super(description, new Vector3D(0,1,0), new Vector3D(0,1,0), new SurfaceColour(DoubleColour.LIGHT_BLUE, DoubleColour.BLACK, true), parent, studio);
		setV1V2(new Vector3D(1,0,0), new Vector3D(0,0,1));
	}
	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ParametrisedPlane(ParametrisedPlane original)
	{
		super(original);
		setV1V2(original.getV1().clone(), original.getV2().clone());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Plane#clone()
	 */
	@Override
	public ParametrisedPlane clone()
	{
		return new ParametrisedPlane(this);
	}

	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		return p.getDifferenceWith(getPointOnPlane()).calculateDecomposition(v1, v2);
	}
	
	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("u");
		parameterNames.add("v");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		return Vector3D.sum(
				getPointOnPlane(),
				getV1().getProductWith(u),
				getV2().getProductWith(v)
			);
	}

	/**
	 * Returns the directions dP/du and dP/dv on the surface of the sphere
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		return v1v2;
	}

	public void setV1V2(Vector3D v1, Vector3D v2)
	{
		this.v1 = v1;
		this.v2 = v2;
		
		v1v2 = new ArrayList<Vector3D>(2);
		v1v2.add(0, v1);
		v1v2.add(1, v2);
	}

	public Vector3D getV1() {
		return v1;
	}

	public Vector3D getV2() {
		return v2;
	}

	@Override
	public ParametrisedPlane transform(Transformation t) {
		return new ParametrisedPlane(description, t.transformPosition(getPointOnPlane()), t.transformDirection(v1), t.transformDirection(v2), getSurfaceProperty(), getParent(), getStudio());
	}
	
	@Override
	public String getType()
	{
		return "Parametrised plane";
	}
}
