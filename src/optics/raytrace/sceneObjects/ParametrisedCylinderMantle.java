package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.Vector3D;
import math.Vector2D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

public class ParametrisedCylinderMantle extends CylinderMantle
implements One2OneParametrisedObject	// , AnisotropicSurface
{
	private static final long serialVersionUID = 285085297884676633L;

	private Vector3D
		zeroDeg,
		ninetyDeg;

	/**
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param radius
	 * @param zeroDeg
	 * @param surfaceProperty
	 */
	public ParametrisedCylinderMantle(
			String description,
			Vector3D startPoint,
			Vector3D endPoint,
			double radius,
			Vector3D zeroDeg,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, startPoint, endPoint, radius, surfaceProperty, parent, studio);
		
		setZeroDeg(zeroDeg);
	}

	/**
	 * @param description
	 * @param startPoint
	 * @param endPoint
	 * @param radius
	 * @param surfaceProperty
	 * @param studio
	 */
	public ParametrisedCylinderMantle(String description, Vector3D startPoint, Vector3D endPoint, double radius, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, startPoint, endPoint, radius, surfaceProperty, parent, studio);
		
		// create a vector zeroDeg that is perpendicular to getAxis()
		setZeroDeg(Vector3D.getANormal(getAxis()));
	}
	
	public ParametrisedCylinderMantle(ParametrisedCylinderMantle original)
	{
		super(original);
		
		setZeroDeg(original.getZeroDeg().clone());
	}
		
	@Override
	public ParametrisedCylinderMantle clone()
	{
		return new ParametrisedCylinderMantle(this);
	}

	public Vector3D getZeroDeg() {
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
			q = p.getDifferenceWith(getStartPoint()),	// p - A
			p2 = q.getDifferenceWith(q.getProjectionOnto(getAxis()));	// q, projected into a plane perpendicular to h
		
		return(new Vector2D(
				q.getScalarProductWith(getAxis()),	// the first parameter is the coordinate along the cylinder axis
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
		// A + hNormalised*p[[1]] + zeroDeg*r*cos(phi) + ninetyDeg*r*sin(phi)
		return Vector3D.sum(
				getStartPoint(),	// A
				getAxis().getProductWith(u),
				zeroDeg.getProductWith(getRadius()*Math.cos(v)),	// zeroDeg*r*cos(phi)
				ninetyDeg.getProductWith(getRadius()*Math.sin(v))	// ninetyDeg*r*sin(phi)
			);
	}

	/**
	 * Returns the directions dP/dz and dP/dPhi on the cylinder mantle
	 * 
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		Vector2D zPhi = getSurfaceCoordinates(p);
		double
		phi = zPhi.y;

		ArrayList<Vector3D> ds = new ArrayList<Vector3D>(2);

		/* In Cartesian coordinates with basis Vector3Ds zeroDeg, ninetyDeg and hNormalised, 
		 * a point on the surface with parameters (z, phi) is described by the Vector3D
		 * 		P = (r*cos(phi), r*sin(phi), z).
		 */

		/* The first direction is given by the Vector3D
		 * 		dP/dz = (0, 0, 1)
		 */
		ds.add(0, getAxis());

		/* The second direction is given by the Vector3D
		 * 		dP/dPhi = (-r*sin(phi), r*cos(phi), 0)
		 */
		ds.add(1,
			Vector3D.sum(
				zeroDeg.getProductWith(-getRadius()*Math.sin(phi)),
				ninetyDeg.getProductWith(getRadius()*Math.cos(phi))
			)
		);

		return ds;
	}
	
	@Override
	public String getType()
	{
		return "Cylinder mantle";
	}
}
