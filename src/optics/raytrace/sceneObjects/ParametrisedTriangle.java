package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * Scene object representing a triangle described in terms of the positions of its vertexs.
 * 
 * @author Johannes
 */
public class ParametrisedTriangle extends SceneObjectPrimitive implements One2OneParametrisedObject, ShapeWithRandomPointAndBoundary, Serializable
{
	private static final long serialVersionUID = 4939952701102521547L;

	private Vector3D
		vertex1,
		vertex1ToVertex2,	// vector from vertex 1 to vertex 2
		vertex1ToVertex3,	// vector from vertex 1 to vertex 3
		uUnitVector,	// step of length 1 in direction of first surface coordinate
		vUnitVector,	// step of length 1 in direction of second surface coordinate
		surfaceNormal;	// pre-calculate in setVertexs
	
	private boolean semiInfinite;

	/**
	 * Creates a triangle.
	 * 
	 * @param description
	 * @param vertex 1
	 * @param vertex1ToVertex2	vector from vertex 1 to vertex 2
	 * @param vertex1ToVertex3	vector from vertex 1 to vertex 3
	 * @param uUnitVector	step of length 1 in direction of first surface coordinate
	 * @param vUnitVector	step of length 1 in direction of second surface coordinate
	 * @param semiInfinite	if true, this represents the semi-infinite triangle with vertex1 and sides given by span vectors
	 * @param sp any surface properties
	 */
	public ParametrisedTriangle(
			String description,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			boolean semiInfinite,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, surfaceProperty, parent, studio);

		setVertex1(vertex1);
		setDirectionVectors(vertex1ToVertex2, vertex1ToVertex3, uUnitVector, vUnitVector);
		setSemiInfinite(semiInfinite);
	}
	
	public ParametrisedTriangle(
			String description,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			boolean semiInfinite,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		this(
				description,
				vertex1,
				vertex1ToVertex2,
				vertex1ToVertex3,
				semiInfinite,
				vertex1ToVertex2,	// uUnitVector
				vertex1ToVertex3,	// vUnitVector
				surfaceProperty,
				parent,
				studio
		);
	}

	
	public ParametrisedTriangle(ParametrisedTriangle original)
	{
		this(
				original.getDescription(),
				original.getVertex1().clone(),
				original.getVertex1ToVertex2().clone(),
				original.getVertex1ToVertex3().clone(),
				original.isSemiInfinite(),
				original.getuUnitVector().clone(),
				original.getvUnitVector().clone(),
				original.getSurfaceProperty(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	@Override
	public ParametrisedTriangle clone()
	{
		return new ParametrisedTriangle(this);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		Ray rayAtIntersectionPoint = Plane.getRayAtClosestRayIntersection(ray, vertex1, surfaceNormal);
		if(rayAtIntersectionPoint == null) return RaySceneObjectIntersection.NO_INTERSECTION;
		
		Vector3D intersectionPoint = rayAtIntersectionPoint.getP();
		
		// vector from the vertex to the intersection point
		Vector3D v = Vector3D.difference(intersectionPoint, vertex1);

		// is the intersection point on the right side of span vector 1?
		if(Vector3D.scalarProduct(v, vertex1ToVertex3.getPartPerpendicularTo(vertex1ToVertex2)) < 0)
		{
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		// is the intersection point on the right side of span vector 2?
		if(Vector3D.scalarProduct(v, vertex1ToVertex2.getPartPerpendicularTo(vertex1ToVertex3)) < 0)
		{
			return RaySceneObjectIntersection.NO_INTERSECTION;
		}

		if(!semiInfinite)
		{
			// the triangle is finite --- check the third side also
			
			// the vertex point is now vertex1 + spanVector1, and the vector from that vertex to the intersection point is
			v = Vector3D.difference(intersectionPoint, Vector3D.sum(vertex1, vertex1ToVertex2));
			
			// calculate the "3rd span vector", i.e. the vector from vertex 2 to vertex 3
			Vector3D vertex2ToVertex3 = Vector3D.difference(vertex1ToVertex3, vertex1ToVertex2);
			
			// is the intersection point on the right side of span vector 3?
			if(Vector3D.scalarProduct(v, vertex1ToVertex2.getPartPerpendicularTo(vertex2ToVertex3)) > 0)
			{
				return RaySceneObjectIntersection.NO_INTERSECTION;
			}
		}

		// okay, the intersection point is actually in the triangle --- return it
		return new RaySceneObjectIntersection(intersectionPoint, this, rayAtIntersectionPoint.getT());
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
	public ParametrisedTriangle transform(Transformation t)
	{
		return new ParametrisedTriangle(
				description,
				t.transformPosition(vertex1),
				t.transformDirection(vertex1ToVertex2),
				t.transformDirection(vertex1ToVertex3),
				isSemiInfinite(),
				t.transformDirection(uUnitVector),
				t.transformDirection(vUnitVector),
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	} 

	@Override
	public boolean insideObject(Vector3D p)
	{
		return 0 < getNormalisedOutwardsSurfaceNormal(null).getScalarProductWith( p.getDifferenceWith(vertex1) );
	}

	// setters & getters
	
	public Vector3D getVertex1() {
		return vertex1;
	}

	public void setVertex1(Vector3D vertex1) {
		this.vertex1 = vertex1;
	}

	public boolean isSemiInfinite() {
		return semiInfinite;
	}

	public void setSemiInfinite(boolean semiInfinite) {
		this.semiInfinite = semiInfinite;
	}

	public Vector3D getVertex1ToVertex2() {
		return vertex1ToVertex2;
	}

	public Vector3D getVertex1ToVertex3() {
		return vertex1ToVertex3;
	}

	public Vector3D getSurfaceNormal() {
		return surfaceNormal;
	}

	/**
	 * set all the direction vectors and pre-calculate the surface normal
	 * @param vertex1ToVertex2
	 * @param vertex1ToVertex3
	 * @param uUnitVector
	 * @param vUnitVector
	 */
	public void setDirectionVectors(Vector3D vertex1ToVertex2, Vector3D vertex1ToVertex3, Vector3D uUnitVector, Vector3D vUnitVector)
	{
		this.vertex1ToVertex2 = vertex1ToVertex2;
		this.vertex1ToVertex3 = vertex1ToVertex3;
		
		surfaceNormal = Vector3D.crossProduct(vertex1ToVertex2, vertex1ToVertex3).getNormalised();
		
		this.uUnitVector = (uUnitVector.getPartPerpendicularTo(surfaceNormal)).getNormalised();
		this.vUnitVector = (vUnitVector.getPartPerpendicularTo(surfaceNormal)).getNormalised();
	}

	public Vector3D getuUnitVector() {
		return uUnitVector;
	}

	public Vector3D getvUnitVector() {
		return vUnitVector;
	}

	@Override
	public String toString()
	{
		return description + " [Triangle]";
	}

	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("u");
		parameterNames.add("v");
		
		return parameterNames;
	}

	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		return p.getDifferenceWith(vertex1).calculateDecomposition(uUnitVector, vUnitVector);
	}

	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
	{
		ArrayList<Vector3D> v1v2 = new ArrayList<Vector3D>(2);
		v1v2.add(0, uUnitVector);
		v1v2.add(1, vUnitVector);
		return v1v2;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
//		System.out.println(
//				"ParametrisedTriangle::getPointForSurfaceCoordinates: vertex1="+vertex1+
//				", uUnitVector="+uUnitVector+
//				", vUnitVector="+vUnitVector+
//				", u="+u+
//				", v="+v
//			);

		return Vector3D.sum(
				vertex1,
				uUnitVector.getProductWith(u),
				vUnitVector.getProductWith(v)
			);
	}

	/**
	 * @return a random point on the surface of the triangle. This will be used by @see FresnelLensShaped, @see FresnelLens and
	 * @see FresnelLensSurface to sample randomly within the area of its aperture and compute the maximum and minimum focal lengths
	 * required for the lens sections to completely fill the aperture.
	 */
	@Override
	public Vector3D getRandomPointOnShape() {
		double coordinateU = Math.random();
		double coordinateV = Math.random();
		if (coordinateU + coordinateV > 1) {
			coordinateU = 1 - coordinateU;
			coordinateV = 1 - coordinateV;
		}
		return Vector3D.sum(vertex1, vertex1ToVertex2.getProductWith(coordinateU),vertex1ToVertex3.getProductWith(coordinateV));
	}

	/**
	 * @return three planes tangent to the sides of the triangle and perpendicular to it. This will be used by @see FresnelLensShaped to cut
	 * down the @see FresnelLens to the aperture specified by the ParametrisedTriangle. 
	 */
	@Override
	public SceneObject getBoundary(double boundaryLength) {
		//first compute the outward facing normals of the buding planes
		Vector3D normalTo1To2 = Vector3D.crossProduct(getVertex1ToVertex2(), getSurfaceNormal());
		Vector3D normalTo1To3 = Vector3D.crossProduct(getSurfaceNormal(), getVertex1ToVertex3());
		////an ancillary vector used in computing the third normal
		Vector3D vertex2To3 = Vector3D.difference(getVertex1ToVertex3(), getVertex1ToVertex2());
		Vector3D normalTo2To3 = Vector3D.crossProduct(vertex2To3, getSurfaceNormal());

		SceneObjectIntersectionSimple boundary = new SceneObjectIntersectionSimple(
				"sceneobject intersection", // description,
				getParent(), // parent,
				getStudio()
				);
		boundary.addSceneObject(new ParametrisedPlane(
				"Boundary connecting vertex 1 and vertex 2",// description,
				vertex1,// pointOnPlane,
				normalTo1To2,// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Boundary connecting vertex 1 and vertex 3",// description,
				vertex1,// pointOnPlane,
				normalTo1To3,// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Boundary connecting vertex 2 and vertex 3",// description,
				Vector3D.sum(getVertex1(), getVertex1ToVertex2()),// pointOnPlane,
				normalTo2To3,// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Front (wrt lens normal) boundary of the lens",// description,
				Vector3D.sum(getVertex1(),surfaceNormal.getProductWith(0.5*boundaryLength)),// pointOnPlane,
				getSurfaceNormal(),// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		boundary.addSceneObject(new ParametrisedPlane(
				"Back (wrt lens normal) boundary of the lens",// description,
				Vector3D.sum(getVertex1(),surfaceNormal.getReverse().getProductWith(0.5*boundaryLength)),// pointOnPlane,
				getSurfaceNormal().getReverse(),// normal, 
				SurfaceColour.BLACK_MATT, //new Refractive(1.4,0.96,true),// surfaceProperty,
				boundary,// parent,
				getStudio()// studio
				));
		
		return boundary;
	}
	
	@Override
	public String getType()
	{
		return "Triangle";
	}
}

