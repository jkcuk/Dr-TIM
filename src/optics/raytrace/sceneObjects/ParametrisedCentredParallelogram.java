package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import optics.raytrace.core.*;



/**
 * A parallelogram is represented by a point and two basis Vector3Ds that span the plane.
 * Here it is parametrised such that the u and v parameters range between 0 and 1 on the parallelogram.
 */
public class ParametrisedCentredParallelogram 
extends CentredParallelogram 
implements One2OneParametrisedObject	// , AnisotropicSurface
{
	private static final long serialVersionUID = -3660978678634194158L;
	
	private Vector3D corner;	// pre-calculate in setCentreAndSpanVectors
	private ArrayList<Vector3D> spanVectors;	// pre-calculate in setCentreAndSpanVectors

	/**
	 * @param description
	 * @param centre	centre of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2, 
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, centre, spanVector1, spanVector2, surfaceProperty, parent, studio);
	}
	
	public ParametrisedCentredParallelogram(ParametrisedCentredParallelogram original)
	{
		super(	original.description,
				original.getCentre().clone(),
				original.getSpanVector1().clone(),
				original.getSpanVector2().clone(),
				original.getSurfaceProperty().clone(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectPrimitive#clone()
	 */
	@Override
	public ParametrisedCentredParallelogram clone()
	{
		return new ParametrisedCentredParallelogram(this);
	}
	
	@Override
	public void setCentreAndSpanVectors(Vector3D centre, Vector3D spanVector1, Vector3D spanVector2)
	{
		super.setCentreAndSpanVectors(centre, spanVector1, spanVector2);
		
		// pre-calculate the corner position
		corner = Vector3D.sum(
				centre,
				spanVector1.getProductWith(-0.5),
				spanVector2.getProductWith(-0.5)
			);

		// pre-calculate the list of span vectors
		spanVectors = new ArrayList<Vector3D>(2);
		spanVectors.add(0, spanVector1);
		spanVectors.add(1, spanVector2);
	}
	
	/**
	 * @return	the position of the corner
	 */
	public Vector3D getCorner()
	{
		return corner;
	}

	/**
	 * Returns the directions dP/du and dP/dv on the parallelogram
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		return spanVectors;
	}

	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		return p.getDifferenceWith(getCorner()).calculateDecomposition(getSpanVector1(), getSpanVector2());
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
				getCorner(),
				getSpanVector1().getProductWith(u),
				getSpanVector2().getProductWith(v)
			);
	}

	@Override
	public ParametrisedCentredParallelogram transform(Transformation t)
	{
		return new ParametrisedCentredParallelogram(
				description,
				t.transformPosition(getCentre()), 
				t.transformDirection(getSpanVector1()), 
				t.transformDirection(getSpanVector2()),
				getSurfaceProperty(),
				getParent(),
				getStudio());
	}


	@Override
	public String toString()
	{
		return description + " [ParametrisedParallelogram]";
	}
	
	@Override
	public String getType()
	{
		return "Parallelogram";
	}
}
