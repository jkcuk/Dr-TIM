package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Vector3D;
import math.Vector2D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

public class ParametrisedConeTop extends ConeTop
implements One2OneParametrisedObject	// , AnisotropicSurface
{
	private static final long serialVersionUID = -5312251919530965957L;

	private Vector3D
		zeroDeg,
		ninetyDeg;

	/**
	 * @param description
	 * @param apex
	 * @param axis
	 * @param theta
	 * @param height
	 * @param zeroDeg
	 * @param surfaceProperty
	 */
	public ParametrisedConeTop(String description, Vector3D apex, Vector3D axis, double theta, double height, Vector3D zeroDeg, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, apex, axis, theta, height, surfaceProperty, parent, studio);
		
		setZeroDeg(zeroDeg);
	}

	public ParametrisedConeTop(String description, Vector3D apex, Vector3D axis, double theta, double height, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, apex, axis, theta, height, surfaceProperty, parent, studio);
		
		// create a vector zeroDeg that is perpendicular to getAxis()
		setZeroDeg(Vector3D.getANormal(getAxis()));
	}
	
	public ParametrisedConeTop(ParametrisedConeTop original)
	{
		super(original);
		
		setZeroDeg(original.getZeroDeg().clone());
	}
		
	@Override
	public ParametrisedConeTop clone()
	{
		return new ParametrisedConeTop(this);
	}

	public Vector3D getZeroDeg()
	{
		return zeroDeg;
	}

	public void setZeroDeg(Vector3D zeroDeg)
	{
		// make sure this.zeroDeg is actually perpendicular to direction
		this.zeroDeg = zeroDeg.getDifferenceWith(zeroDeg.getProjectionOnto(getAxis())).getNormalised();
		
		// set ninetyDeg to be perpendicular to both the axis and zeroDeg
		ninetyDeg = Vector3D.crossProduct(getZeroDeg(), getAxis());		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.ParametrisedSurface#getParametersForSurfacePoint(math.Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		Vector3D
			q = p.getDifferenceWith(getApex()),	// p - apex
			p2 = q.getDifferenceWith(q.getProjectionOnto(getAxis()));	// q, projected into a plane perpendicular to the axis
		
		return(new Vector2D(
				q.getScalarProductWith(getAxis()),	// the first parameter is the coordinate along the cone axis
				Math.atan2(p2.getScalarProductWith(ninetyDeg), p2.getScalarProductWith(zeroDeg))	// phi
			));
	}
	
	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("z");
		parameterNames.add("phi");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		double radius = u * tanTheta;
		
		// A + hNormalised*p[[1]] + zeroDeg*r*cos(phi) + ninetyDeg*r*sin(phi)
		return Vector3D.sum(
				getApex(),	// A
				getAxis().getProductWith(u),
				zeroDeg.getProductWith(radius*Math.cos(v)),	// zeroDeg*r*cos(phi)
				ninetyDeg.getProductWith(radius*Math.sin(v))	// ninetyDeg*r*sin(phi)
			);
	}

	/**
	 * Returns the directions dP/dz and dP/dPhi on the surface of the cylinder mantle
	 * 
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		Vector2D zPhi = getSurfaceCoordinates(p);
		double
			z = zPhi.x,
			phi = zPhi.y,
			radius = z*tanTheta;

		ArrayList<Vector3D> ds = new ArrayList<Vector3D>(2);

		/* In Cartesian coordinates with basis Vector3Ds zeroDeg, ninetyDeg and hNormalised, 
		 * a point on the surface with parameters (z, phi) is described by the Vector3D
		 * 		P = (r*cos(phi), r*sin(phi), z).
		 */

		/* The first direction is given by the Vector3D
		 * 		dP/dz = (0, 0, 1)
		 */
		ds.add(0, getAxis().getProjectionOnto(Vector3D.difference(p, getApex())));

		/* The second direction is given by the Vector3D
		 * 		dP/dPhi = (-r*sin(phi), r*cos(phi), 0)
		 */
		ds.add(1,
			Vector3D.sum(
				zeroDeg.getProductWith(-radius*Math.sin(phi)),
				ninetyDeg.getProductWith(radius*Math.cos(phi))
			)
		);

		return ds;
	}
	
	@Override
	public String getType()
	{
		return "Cone top";
	}
}
