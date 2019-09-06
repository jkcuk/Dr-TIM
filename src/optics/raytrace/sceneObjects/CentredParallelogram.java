package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Scene object representing a parallelogram described in terms of the position of its centre
 * and two "span vectors" along the sides.
 * 
 * @author Dean et al.
 */
public class CentredParallelogram extends SceneObjectPrimitive implements Serializable
{
	private static final long serialVersionUID = -1370306618080008504L;

	private Vector3D
		centre,	// centre of parallelogram
		spanVector1, spanVector2,	// side Vector3Ds
		surfaceNormal;	// pre-calculate in setSpanVectors

	/**
	 * Creates a parallelogram.
	 * The outside of the parallelogram is defined by the direction v1 x v2.
	 * 
	 * @param description
	 * @param centre centre of the parallelogram
	 * @param spanVector1 side Vector3D 1
	 * @param spanVector2 side Vector3D 2
	 * @param sp any surface properties
	 */
	public CentredParallelogram(
			String description,
			Vector3D centre,
			Vector3D spanVector1, Vector3D spanVector2,
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setCentreAndSpanVectors(centre, spanVector1, spanVector2);
	}
	
	public CentredParallelogram(CentredParallelogram original)
	{
		super(original);
		
		setCentreAndSpanVectors(original.getCentre().clone(), original.getSpanVector1().clone(), original.getSpanVector2().clone());
	}
	
	@Override
	public CentredParallelogram clone()
	{
		return new CentredParallelogram(this);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Vector3D normal = getNormalisedOutwardsSurfaceNormal(null);

		double numerator = Vector3D.scalarProduct(Vector3D.difference(centre, ray.getP()), normal);
		double denominator = Vector3D.scalarProduct(ray.getD(), normal);
		if (denominator==0)
			return RaySceneObjectIntersection.NO_INTERSECTION;

		double distance = numerator / denominator;

		if (distance < 0.0 )
		{
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		// find the intersection point
		Ray rayAtIntersectionPoint = ray.getAdvancedRay(distance);
		Vector3D intersectionPoint = rayAtIntersectionPoint.getP();

		Vector2D coefficients = intersectionPoint.getDifferenceWith(centre).calculateDecomposition(spanVector1, spanVector2);
		double alpha = coefficients.x;
		double beta = coefficients.y;

//		// the distance from the origin of the sheet, i.e. the centre.
//		Vector3D distanceFromSheetOrigin = Vector3D.difference(intersectionPoint, centre);
//
//		//double alpha = Vector3D.scalarProduct(u.normalise(), Vector3D.difference(point, centre));
//		double alpha = Vector3D.scalarProduct(spanVector1, distanceFromSheetOrigin) / spanVector1.getModSquared();

		if (alpha < -0.5 || +0.5 < alpha)
		{
			// alpha * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		//double beta = Vector3D.scalarProduct(v.normalise(), Vector3D.difference(point, centre));
//		double beta = Vector3D.scalarProduct(spanVector2, distanceFromSheetOrigin) / spanVector2.getModSquared();
		if (beta < -0.5 || +0.5 < beta)
		{
			// beta * u are not within the correct boundary
			return RaySceneObjectIntersection.NO_INTERSECTION;						
		}

		//return new RaySceneObjectIntersection (ray.getP().add(Vector3D.scalarTimesVector3D(lambda, ray.getD())),this);
		return new RaySceneObjectIntersection(intersectionPoint, this, rayAtIntersectionPoint.getT());

//		Vector3D
//			v1xd = Vector3D.crossProduct(spanVector1, ray.getD()),
//			v2xd = Vector3D.crossProduct(spanVector2, ray.getD());
//		double 
//			s=(ray.getP().subtract(centre)).scalarProduct(v2xd)/spanVector1.scalarProduct(v2xd),
//			t=(ray.getP().subtract(centre)).scalarProduct(v1xd)/spanVector2.scalarProduct(v1xd);
//
//		if(s<0.0 || s>1.0 || t<0.0 || t>1.0)
//			return RaySceneObjectIntersection.NO_INTERSECTION;	//returns null if there is no intersection
//
//		Vector3D i = centre.add(spanVector1.multiply(s)).add(spanVector2.multiply(t));	// intersection point
//
//		// is the intersection point in the backwards ray direction?
//		if(Vector3D.scalarProduct(Vector3D.difference(i, ray.getP()), ray.getD()) < 0)
//			return RaySceneObjectIntersection.NO_INTERSECTION;
//
//		return new RaySceneObjectIntersection (i,this);
	}

	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray, SceneObject excludeObject)
	{
		return getClosestRayIntersection(ray);
	}

	@Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	{
		return surfaceNormal;
	}

	// TransformableSceneObject method
	@Override
	public CentredParallelogram transform(Transformation t)
	{
		return new CentredParallelogram(
				description,
				t.transformPosition(centre),
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
		return 0 < getNormalisedOutwardsSurfaceNormal(null).getScalarProductWith( p.getDifferenceWith(centre) );
	}

	public void setCentreAndSpanVectors(Vector3D centre, Vector3D spanVector1, Vector3D spanVector2)
	{
		this.centre = centre;
		this.spanVector1 = spanVector1;
		this.spanVector2 = spanVector2;
		
		surfaceNormal = Vector3D.crossProduct(spanVector1, spanVector2).getNormalised();
	}

	public Vector3D getCentre()
	{
		return centre;
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
	public String getType()
	{
		return "Parallelogram";
	}

	@Override
	public String toString()
	{
		return description + " [Parallelogram]";
	}
}

