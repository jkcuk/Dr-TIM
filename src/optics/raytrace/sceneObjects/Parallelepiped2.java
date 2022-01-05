package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;

/**
 * A parallelepiped made differently.
 * (What I should really do is define it as the intersection of the "insides" of 6 planes...)
 * 
 * @author Johannes Courtial
 */
public class Parallelepiped2
extends SceneObjectPrimitive 
implements Serializable	// , AnisotropicSurface, ParametrisedObject
{
	private static final long serialVersionUID = 6863190545674092657L;

	static final int
		PLUS_U_DIRECTION = 0,
		MINUS_U_DIRECTION = 1,
		PLUS_V_DIRECTION = 2,
		MINUS_V_DIRECTION = 3,
		PLUS_W_DIRECTION = 4,
		MINUS_W_DIRECTION = 5;

	private Vector3D 
		centre,	// the centre
		u, v, w,	// the three vectors spanning the parallelepiped
		normalisedU, normalisedV, normalisedW;

	/**
	 * Create a new object of size |u|, |v| and |w| along u, v and w centred around centre.
	 */
	public Parallelepiped2(String description, Vector3D centre, Vector3D u, Vector3D v, Vector3D w, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio) {
		super(description, surfaceProperty, parent, studio);
		this.centre = centre;
		setU(u);
		setV(v);
		setW(w);
	}

	public Parallelepiped2(String description, SceneObject parent, Studio studio)
	{
		this(	description,
				new Vector3D(-1,1,15), 	// centre
				Vector3D.X.getProductWith(0.25),	// u
				Vector3D.Y.getProductWith(0.25),	// v
				Vector3D.Z.getProductWith(20),	// ww
				new SurfaceColour(DoubleColour.YELLOW, DoubleColour.BLACK, true),
				parent,
				studio
			);
	}
	
	@Override
	public Parallelepiped2 clone()
	{
		return new Parallelepiped2(description, centre.clone(), u.clone(), v.clone(), w.clone(),
				getSurfaceProperty().clone(),
				getParent(),
				getStudio()
			);
	}

	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getU() {
		return u;
	}

	public void setU(Vector3D u) {
		this.u = u;
		normalisedU = u.getNormalised();
	}

	public Vector3D getV() {
		return v;
	}

	public void setV(Vector3D v) {
		this.v = v;
		normalisedV = v.getNormalised();
	}

	public Vector3D getW() {
		return w;
	}

	public void setW(Vector3D w) {
		this.w = w;
		normalisedW = w.getNormalised();
	}

//	/**
//	 * Calculates two direction Vector3Ds corresponding to point p.
//	 * Note that these direction Vector3Ds are in addition to the surface normal.
//	 * 
//	 * @param p The point at which the directions should be calculated.
//	 * @return The two directions.
//	 */
//	@Override
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p) {
//		ArrayList<Vector3D> directions = new ArrayList<Vector3D>();
//		switch(order(p)) {
//		case PLUS_U_DIRECTION:
//			directions.add(v);
//			directions.add(w);
//			return directions;
//		case MINUS_U_DIRECTION:
//			directions.add(w);
//			directions.add(v);
//			return directions;
//		case PLUS_V_DIRECTION:
//			directions.add(w);
//			directions.add(u);
//			return directions;
//		case MINUS_V_DIRECTION:
//			directions.add(u);
//			directions.add(w);
//			return directions;
//		case PLUS_W_DIRECTION:
//			directions.add(u);
//			directions.add(v);
//			return directions;
//		case MINUS_W_DIRECTION:
//			directions.add(v);
//			directions.add(u);
//			return directions;
//		}
//		directions.add(u);
//		directions.add(v);
//		return directions;
//	}
//
//	/**
//	 * Parametrises the surface of the SceneObject so that patterns etc. can be applied.
//	 * @param p the point on the surface
//	 * @return two parameters, in the form of a Vector2D, that describe the point p
//	 */
//	@Override
//	public Vector2D getCoordinatesForSurfacePoint(Vector3D p) {
//		ArrayList<Vector3D> directions = getVectorsForSurfacePoint(p);
//
//		Vector3D directionA = directions.get(0);
//		Vector3D directionB = directions.get(1);
//
//		double weightA = p.getFractionalProjectionLength(directionA);
//		double weightB = p.getFractionalProjectionLength(directionB);
//
//		return new Vector2D(weightA, weightB);
//	}
//	
//	/**
//	 * @return the names of the parameters, e.g. ("theta", "phi")
//	 */
//	@Override
//	public ArrayList<String> getCoordinateNames()
//	{
//		ArrayList<String> parameterNames = new ArrayList<String>(2);
//		parameterNames.add("u");
//		parameterNames.add("v");
//		
//		return parameterNames;
//	}

//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.ParametrisedSurface#getSurfacePointForParameters(math.Vector2D)
//	 */
//	@Override
//	public Vector3D getSurfacePointForParameters(Vector2D p)
//	{
//		// it's not quite clear which one of the surfaces the parameters refer to...
//		throw(new RuntimeException("ParametrisedParallelepiped::getSurfacePointForParameters: this method does not make sense for this object and should not be called."));
//
//		// return null;
//	}

	@Override
	public Parallelepiped2 transform(Transformation t) {
		return new Parallelepiped2 (
				description, 
				t.transformPosition(centre), 
				t.transformDirection(u), 
				t.transformDirection(v),
				t.transformDirection(w), 
				getSurfaceProperty(),
				getParent(),
				getStudio());
	}

	/**
	 * Get the normal Vector3D of the face closest to the given point.
	 */
	 @Override
	public Vector3D getNormalisedOutwardsSurfaceNormal(Vector3D p)
	 {
		 switch(order(p)) {
		 case PLUS_U_DIRECTION:
			 return normalisedU;
		 case MINUS_U_DIRECTION:
			 return normalisedU.getReverse();
		 case PLUS_V_DIRECTION:
			 return normalisedV;
		 case MINUS_V_DIRECTION:
			 return normalisedV.getReverse();
		 case PLUS_W_DIRECTION:
			 return normalisedW;
		 case MINUS_W_DIRECTION:
			 return normalisedW.getReverse();
		 }
		 throw(new RuntimeException("ParametrisedParallelepiped::getNormalisedSurfaceNormal: point p not on surface"));
	 }

	 /**
	  * Given a point, return the surface normal direction.
	  *@return One of PLUS_U_DIRECTION, MINUS_U_DIRECTION for U, V or W
	  */
	 public int order(Vector3D point) {
		 Vector3D difference = Vector3D.difference(point, centre);
		 double uDistance = difference.getFractionalProjectionLength(u);
		 double vDistance = difference.getFractionalProjectionLength(v);
		 double wDistance = difference.getFractionalProjectionLength(w);        

		 double absUDistance = Math.abs(uDistance);
		 double absVDistance = Math.abs(vDistance);
		 double absWDistance = Math.abs(wDistance);
		 if (absUDistance >= absVDistance && absUDistance >= absWDistance)
			 return (uDistance > 0) ? PLUS_U_DIRECTION : MINUS_U_DIRECTION;
		 if (absVDistance >= absUDistance && absVDistance >= absWDistance)
			 return (vDistance > 0) ? PLUS_V_DIRECTION : MINUS_V_DIRECTION;
		 if (absWDistance >= absUDistance && absWDistance >= absVDistance)
			 return (wDistance > 0) ? PLUS_W_DIRECTION : MINUS_W_DIRECTION;

		 throw(new RuntimeException("ParametrisedParallelepiped::order: point p not on surface"));
	 }

	 @Override
	public boolean insideObject(Vector3D p) {
		 return 0 < getNormalisedOutwardsSurfaceNormal(null).getScalarProductWith( p.getDifferenceWith(centre) );
	 }

	 @Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray) {

		 RaySceneObjectIntersection[] intersections = new RaySceneObjectIntersection[] {
				 planeIntersection(ray, centre.getSumWith(u.getProductWith(-0.5)), v, w),
				 planeIntersection(ray, centre.getSumWith(u.getProductWith(0.5)), v, w),
				 planeIntersection(ray, centre.getSumWith(v.getProductWith(-0.5)), u, w),
				 planeIntersection(ray, centre.getSumWith(v.getProductWith(0.5)), u, w),
				 planeIntersection(ray, centre.getSumWith(w.getProductWith(-0.5)), u, v),
				 planeIntersection(ray, centre.getSumWith(w.getProductWith(0.5)), u, v)};

		 // find the closest intersecting distance
		 double distance = Double.MAX_VALUE;
		 RaySceneObjectIntersection closestIntersection = RaySceneObjectIntersection.NO_INTERSECTION;

		 for (int i=0; i<intersections.length; i++) {

			 if (intersections[i] == RaySceneObjectIntersection.NO_INTERSECTION)
				 continue;

			 Vector3D intersectionPoint = intersections[i].p;
			 Vector3D point = ray.getP();

			 //double newDistance = intersectionPoint.subtract(point);
			 double newDistance = Vector3D.difference(intersectionPoint, point).getLength();
			 if (newDistance < distance) {
				 distance = newDistance;
				 closestIntersection = intersections[i];
			 }
		 }

		 return closestIntersection;
	 }

	 private RaySceneObjectIntersection planeIntersection(Ray ray, Vector3D planeCentre, Vector3D planeU, Vector3D planeV) {

		 Vector3D normal = Vector3D.crossProduct(planeU, planeV);

		 double numerator = Vector3D.scalarProduct(Vector3D.difference(planeCentre, ray.getP()), normal);
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

		 // the distance from the origin of the sheet, i.e. the centre.
		 Vector3D distanceFromSheetOrigin = Vector3D.difference(intersectionPoint, planeCentre);

		 //double alpha = Vector3D.scalarProduct(planeU, distanceFromSheetOrigin) / planeU.modSquared();
		 double alpha = distanceFromSheetOrigin.getFractionalProjectionLength(planeU);
		 if (alpha < -0.5 || +0.5 < alpha) {
			 // alpha * u are not within the correct boundary
			 return RaySceneObjectIntersection.NO_INTERSECTION;
		 }

		 //double beta = Vector3D.scalarProduct(planeV, distanceFromSheetOrigin) / planeV.modSquared();
		 double beta = distanceFromSheetOrigin.getFractionalProjectionLength(planeV);
		 if (beta < -0.5 || +0.5 < beta) {
			 // beta * u are not within the correct boundary
			 return RaySceneObjectIntersection.NO_INTERSECTION;						
		 }

		 return new RaySceneObjectIntersection(intersectionPoint, this, rayAtIntersectionPoint.getT());
	 }
	 
		@Override
		public String getType()
		{
			return "Parallelepiped";
		}
}