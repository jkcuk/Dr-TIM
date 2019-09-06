package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Scene object representing a parallelogram described in terms of the position of its corner
 * and two "span vectors" along the sides.
 * 
 * @author Johannes
 */
public class Parallelogram extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = 3275858759091448802L;

	private Vector3D
		corner,	// corner of parallelogram
		spanVector1, spanVector2,	// side Vector3Ds
		surfaceNormal;	// pre-calculate in setSpanVectors

	/**
	 * Creates a parallelogram.
	 * The outside of the parallelogram is defined by the direction v1 x v2.
	 * 
	 * @param description
	 * @param corner corner of the parallelogram
	 * @param spanVector1 side Vector3D 1
	 * @param spanVector2 side Vector3D 2
	 * @param sp any surface properties
	 */
	public Parallelogram(
			String description,
			Vector3D corner,
			Vector3D spanVector1, Vector3D spanVector2,
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setCorner(corner);
		setSpanVectors(spanVector1, spanVector2);
	}
	
	public Parallelogram(Parallelogram original)
	{
		super(original);
		
		setCorner(original.getCorner().clone());
		setSpanVectors(original.getSpanVector1().clone(), original.getSpanVector2().clone());
	}
	
	@Override
	public Parallelogram clone()
	{
		return new Parallelogram(this);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Vector3D normal = getNormalisedOutwardsSurfaceNormal(null);

		double numerator = Vector3D.scalarProduct(Vector3D.difference(corner, ray.getP()), normal);
		double denominator = Vector3D.scalarProduct(ray.getD(), normal);
		if (denominator==0)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		double lambda = numerator / denominator;

		if (lambda < 0.0 ) {
			//returns null if there is no intersection
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		// find the intersection point
		Ray rayAtIntersectionPoint = ray.getAdvancedRay(lambda);
		Vector3D intersectionPoint = rayAtIntersectionPoint.getP();

		
		Vector2D coefficients = intersectionPoint.getDifferenceWith(corner).calculateDecomposition(spanVector1, spanVector2);
		double alpha = coefficients.x;
		double beta = coefficients.y;
		
//		// the distance from the corner
//		Vector3D distanceFromCorner = Vector3D.difference(intersectionPoint, corner);
//
//		//double alpha = Vector3D.scalarProduct(u.normalise(), Vector3D.difference(point, centre));
//		double alpha = Vector3D.scalarProduct(spanVector1, distanceFromCorner) / spanVector1.getModSquared();


		if (alpha < 0. || 1. < alpha) {
			// alpha * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		//double beta = Vector3D.scalarProduct(v.normalise(), Vector3D.difference(point, centre));
//		double beta = Vector3D.scalarProduct(spanVector2, distanceFromCorner) / spanVector2.getModSquared();
		if (beta < 0. || 1. < beta) {
			// beta * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		//return new RaySceneObjectIntersection (ray.getP().add(Vector3D.scalarTimesVector3D(lambda, ray.getD())),this);
		return new RaySceneObjectIntersection(intersectionPoint, this, rayAtIntersectionPoint.getT());
	}

	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray, SceneObject excludeObject)
	{
		return getClosestRayIntersection(ray);
	}
	
	public Vector3D getSurfaceNormal()
	{
		return surfaceNormal;
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		return surfaceNormal;
	}

	// TransformableSceneObject method
	@Override
	public Parallelogram transform(Transformation t)
	{
		return new Parallelogram(
				description,
				t.transformPosition(corner),
				t.transformDirection(spanVector1),
				t.transformDirection(spanVector2),
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	} 

	@Override
	public boolean insideObject(Vector3D p)
	{
		return 0 < getNormalisedOutwardsSurfaceNormal(null).getScalarProductWith( p.getDifferenceWith(corner) );
	}

	public void setCorner(Vector3D corner)
	{
		this.corner = corner;
	}

	public Vector3D getCorner()
	{
		return corner;
	}

	public void setSpanVectors(Vector3D spanVector1, Vector3D spanVector2)
	{
		this.spanVector1 = spanVector1;
		this.spanVector2 = spanVector2;
		
		surfaceNormal = Vector3D.crossProduct(spanVector1, spanVector2).getNormalised();
	}

	public Vector3D getSpanVector1()
	{
		return spanVector1;
	}

	public Vector3D getSpanVector2()
	{
		return spanVector2;
	}

	@Override
	public String toString()
	{
		return description + " [Parallelogram]";
	}
	
	@Override
	public String getType()
	{
		return "Parallelogram";
	}
}

