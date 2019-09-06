package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.SurfaceColour;



/**
 * A parallelogram is represented by a point and two basis Vector3Ds that span the plane.
 * Here it is parametrised such that the u and v parameters range between 0 and 1 on the parallelogram.
 */
public class ParametrisedParallelogram 
extends Parallelogram 
implements One2OneParametrisedObject, ShapeWithRandomPointAndBoundary
{
	private static final long serialVersionUID = 3176868049921645430L;

	private ArrayList<Vector3D> spanVectors;	// pre-calculate in setCentreAndSpanVectors

	/**
	 * @param description
	 * @param corner	corner of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ParametrisedParallelogram(
			String description, 
			Vector3D corner, 
			Vector3D spanVector1, Vector3D spanVector2, 
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, corner, spanVector1, spanVector2, surfaceProperty, parent, studio);
	}
	
	public ParametrisedParallelogram(ParametrisedParallelogram original)
	{
		this(	original.description,
				original.getCorner().clone(),
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
	public ParametrisedParallelogram clone()
	{
		return new ParametrisedParallelogram(this);
	}
	
	@Override
	public void setSpanVectors(Vector3D spanVector1, Vector3D spanVector2)
	{
		super.setSpanVectors(spanVector1, spanVector2);
		
		// pre-calculate the list of span vectors
		spanVectors = new ArrayList<Vector3D>(2);
		spanVectors.add(0, spanVector1);
		spanVectors.add(1, spanVector2);
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
		if(p == null) return new Vector2D(-1, -1);
		// System.out.println("p = " + p + ", corner = " + getCorner() + ", span vector 1 = " + getSpanVector1());
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
	public ParametrisedParallelogram transform(Transformation t)
	{
		return new ParametrisedParallelogram(
				description,
				t.transformPosition(getCorner()), 
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


	// ShapeWithRandomPointAndBoundary methods
	
	/* (non-Javadoc)
	 * @see math.geometry.ShapeWithRandomPointAndBoundary1#getRandomPointOnShape()
	 */
	@Override
	public Vector3D getRandomPointOnShape()
	{
		return Vector3D.sum(
				getCorner(),
				getSpanVector1().getProductWith(Math.random()),
				getSpanVector2().getProductWith(Math.random())
			);
	}

	/* (non-Javadoc)
	 * @see math.geometry.ShapeWithRandomPointAndBoundary1#getBoundary(double)
	 */
	@Override
	public SceneObject getBoundary(double boundaryLength)
	{
		// normals to the boundary planes
		
		// normal to sides spanned by span vector 1
		Vector3D normal1 = Vector3D.crossProduct(getSurfaceNormal(), getSpanVector1());
		// make this normal point outwards at the corner
		normal1 = normal1.getWithLength(-Math.signum(Vector3D.scalarProduct(normal1, getSpanVector2())));
		
		// normal to sides spanned by span vector 2
		Vector3D normal2 = Vector3D.crossProduct(getSurfaceNormal(), getSpanVector2());
		// make this normal point outwards at the corner
		normal2 = normal2.getWithLength(-Math.signum(Vector3D.scalarProduct(normal2, getSpanVector1())));

		// the boundary is then the intersection of the "insides" of 6 planes
		SceneObjectIntersection boundary = new SceneObjectIntersection(
				"Boundary", // description,
				getParent(), // parent,
				getStudio()
				);
		// the planes through the corner
		boundary.addSceneObject(new ParametrisedPlane(
				"Plane 1 through corner",// description,
				getCorner(),	// pointOnPlane,
				normal1,	// outwards normal, 
				SurfaceColour.BLACK_MATT, // surfaceProperty,
				boundary,	// parent,
				getStudio()	// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Plane 2 through corner",// description,
				getCorner(),	// pointOnPlane,
				normal2,	// outwards normal, 
				SurfaceColour.BLACK_MATT, // surfaceProperty,
				boundary,	// parent,
				getStudio()	// studio
				));
		
		// the planes through the opposite corner
		Vector3D oppositeCorner = Vector3D.sum(getCorner(), getSpanVector1(), getSpanVector2());
		boundary.addSceneObject(new ParametrisedPlane(
				"Plane 1 through opposite corner",	// description,
				oppositeCorner,	// pointOnPlane,
				normal1.getReverse(),	// normal, 
				SurfaceColour.BLACK_MATT, // surfaceProperty,
				boundary,	// parent,
				getStudio()	// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Plane 2 through opposite corner",	// description,
				oppositeCorner,	// pointOnPlane,
				normal2.getReverse(),	// normal, 
				SurfaceColour.BLACK_MATT, // surfaceProperty,
				boundary,	// parent,
				getStudio()	// studio
				));

		// planes in front of and behind the plane
		boundary.addSceneObject(new ParametrisedPlane(
				"Front plane",// description,
				Vector3D.sum(getCorner(), getSurfaceNormal().getProductWith(0.5*boundaryLength)),// pointOnPlane,
				getSurfaceNormal(),	// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Back plane",// description,
				Vector3D.sum(getCorner(), getSurfaceNormal().getProductWith(-0.5*boundaryLength)),// pointOnPlane,
				getSurfaceNormal().getReverse(),// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		
		return boundary;
	}
}
