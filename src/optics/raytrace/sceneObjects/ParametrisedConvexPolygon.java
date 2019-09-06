package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;


/**
 * Scene object representing a convex polygon described in terms of the positions of its vertices.
 * 
 * @author Johannes
 */
public class ParametrisedConvexPolygon extends ParametrisedPlane implements One2OneParametrisedObject, Serializable
{
	private static final long serialVersionUID = 7351013492594287653L;

	private Vector3D vertices[];	// the vertices, in the form of an array of position vectors
	
	// The edge planes are used to determine whether or not a point on the polygon plane actually lies on the polygon.
	// Each edge plane is perpendicular to the polygon plane.
	// The ith edge plane is defined as the plane that includes vertices i and i+1 and whose normal lies in
	private Vector3D edgePlaneNormals[];	// pre-calculate in calculateEdgePlaneNormals method
	
	/**
	 * Constructor that sets everything explicitly.
	 * The first vertex lies on the plane.  Together with the normal to the plane it defines the polygon plane.
	 * @param description
	 * @param normalToPlane
	 * @param vertices
	 * @param uUnitVector
	 * @param vUnitVector
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ParametrisedConvexPolygon(
			String description,
			Vector3D normalToPlane,
			Vector3D vertices[],
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		super(description, new Vector3D(0, 0, 0), normalToPlane, uUnitVector, vUnitVector, surfaceProperty, parent, studio);
		
		setVertices(vertices);
	}
	
	/**
	 * Constructor that doesn't explicitly set the parametrisation of the plane, but instead finds one itself.
	 * @param description
	 * @param pointInPolygonPlane
	 * @param normalToPlane
	 * @param vertices
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ParametrisedConvexPolygon(
			String description,
			Vector3D normalToPlane,
			Vector3D vertices[],
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		super(description, new Vector3D(0, 0, 0), normalToPlane, surfaceProperty, parent, studio);
		
		setVertices(vertices);
	}
		
	public ParametrisedConvexPolygon(ParametrisedConvexPolygon original)
	{
		// clone the underlying ParametrisedPlane
		super(original);
		
		// clone the array of vertices
		Vector3D clonedVertices[] = new Vector3D[original.getVertices().length];
		
		for(int i=0; i<original.getVertices().length; i++)
		{
			clonedVertices[i] = original.getVertices()[i].clone();
		}

		setVertices(clonedVertices);
	}
	
	@Override
	public ParametrisedConvexPolygon clone()
	{
		return new ParametrisedConvexPolygon(this);
	}
	
	
	//
	// setters & getters
	//
	
	public Vector3D[] getVertices() {
		return vertices;
	}

	/**
	 * Set the coordinates of the vertices.
	 * This is normally followed by a call to calculateEdgePlanes!
	 * @param vertices
	 */
	public void setVertices(Vector3D[] vertices)
	{
		if(vertices != null) this.vertices = vertices;
		else this.vertices = new Vector3D[0];	// if vertices is null, at least make it a 0-length array
		
		// the first vertex is, by definition, a point on the plane
		if(this.vertices.length > 0) setPointOnPlane(this.vertices[0]);
		
		calculateEdgePlaneNormals();
	}
	
	/**
	 * based on the direction of the normal to the plane and the vertex coordinates, calculate the edge planes
	 */
	private void calculateEdgePlaneNormals()
	{
		// first calculate a point that lies on the polygon;
		// is the (orthographic projection into the polygon plane of the) centre of mass of the vertices such a point?
		// I think so; calculate the centre of mass
		Vector3D centreOfMass = new Vector3D(0, 0, 0);
		// add all the vertices together...
		for(int i=0; i<vertices.length; i++)
		{
			centreOfMass = Vector3D.sum(centreOfMass, vertices[i]);
		}
		// ... and divide this sum by the number of vertices
		centreOfMass = centreOfMass.getProductWith(1.0/vertices.length);
		
		// initialise the edge-plane normals
		edgePlaneNormals = new Vector3D[vertices.length];

		// pre-calculate the edge-plane normals
		for(int i=0; i<vertices.length; i++)
		{
			// the ith edge plane passes through vertices #i and #i2, where i2=i+1 if i<vertices.length-1 or 0 otherwise
			int i2 = ((i==vertices.length-1)?0:i+1);

			// the normal to the edge plane is given by normalToPlane x (vertex[i2]-vertex[i])
			Vector3D edgePlaneNormal = Vector3D.crossProduct(
					getNormal(),
					Vector3D.difference(vertices[i2], vertices[i])
				).getNormalised();
			
			// is this normal pointing "outwards", as it should?
			if(Vector3D.scalarProduct(edgePlaneNormal, Vector3D.difference(vertices[i], centreOfMass)) > 0)
			{
				// the edge-plane normal is already pointing "outwards"
				edgePlaneNormals[i] = edgePlaneNormal;
			}
			else
			{
				// the edge-plane normal is pointing "inwards", so turn it round
				edgePlaneNormals[i] = edgePlaneNormal.getReverse();
			}
		}
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObject#getClosestRayIntersection(optics.raytrace.core.Ray)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		// find the point where the ray intersects the polygon plane
		RaySceneObjectIntersection i = super.getClosestRayIntersection(ray);
		
		if(i != RaySceneObjectIntersection.NO_INTERSECTION)
		{
			// there is an intersection, but does the intersection point lie "inside" each edge plane?
			// check each edge plane in turn
			int j=0;
			for(
					j=0;
					j<edgePlaneNormals.length;
					j++
				)
			{
				// does i.p lie "inside" edge plane #j?
				if(i.p.getDifferenceWith(vertices[j]).getScalarProductWith(edgePlaneNormals[j]) > 0)
				{
					// i.p lies outside of edge plane #j
					return RaySceneObjectIntersection.NO_INTERSECTION;
				}
			}
		}
		
		return i;
	}

	// TransformableSceneObject method
	@Override
	public ParametrisedConvexPolygon transform(Transformation t)
	{
		// calculate the array of transformed vertices
		Vector3D transformedVertices[] = new Vector3D[vertices.length];
		
		for(int i=0; i<vertices.length; i++)
		{
			transformedVertices[i] = t.transformPosition(vertices[i]);
		}
		
		return new ParametrisedConvexPolygon(
				description,
				t.transformDirection(getNormal()),	// normal to plane
				transformedVertices,	// vertices
				t.transformDirection(getV1()),
				t.transformDirection(getV2()),
				getSurfaceProperty(),
				getParent(),
				getStudio()
			);
	} 

	@Override
	public String toString()
	{
		return description + " [ConvexPolygon]";
	}
	
	@Override
	public String getType()
	{
		return "Convex polygon";
	}
}

