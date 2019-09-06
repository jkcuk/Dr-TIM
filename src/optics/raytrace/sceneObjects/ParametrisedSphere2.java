package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.core.*;

import java.util.ArrayList;

/**
 * Like ParametrisedSphere, but the parameters are in the opposite order, i.e. (phi, theta)
 * @author Johannes Courtial
 */
public class ParametrisedSphere2 extends ParametrisedSphere
{
	private static final long serialVersionUID = -1694225146846676906L;

	/**
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param pole	direction from the centre to the north pole
	 * @param phi0Direction	direction from the centre to the intersection between zero-degree meridian and equator
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedSphere2(String description,
			Vector3D c,
			double r,
			Vector3D pole,
			Vector3D phi0Direction,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, c, r, pole, phi0Direction, surfaceProperty, parent, studio);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public ParametrisedSphere2(ParametrisedSphere original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public ParametrisedSphere2 clone()
	{
		return new ParametrisedSphere2(this);
	}

	/**
	 * Returns the azimuthal angle phi and the polar angle theta that describe the point p on the sphere
	 * 
	 * @param p	the point on the sphere
	 * @return	(phi, theta)
	 * @see ParametrisedObject#getParametersForSurfacePoint
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		return super.getSurfaceCoordinates(p).getTranspose();
	}
	
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("phi");
		parameterNames.add("theta");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		return super.getPointForSurfaceCoordinates(v, u);
	}

	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
	{
		ArrayList<Vector3D>
			ds = new ArrayList<Vector3D>(2),
			ds2 = super.getSurfaceCoordinateAxes(p);

		// construct an array list in which the two elements are swapped
		ds.add(0, ds2.get(1));
		ds.add(1, ds2.get(0));

		return ds;
	}
	
	@Override
	public ParametrisedSphere2 transform(Transformation t)
	{
		return new ParametrisedSphere2(super.transform(t));
	}

	@Override
	public String toString() {
		return description + " [ParametrisedSphere2]";
	}
	
	@Override
	public String getType()
	{
		return "Sphere";
	}
}
