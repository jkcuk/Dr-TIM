package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.CoordinateSystems;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;


/**
 * Simulates a generalised ray-rotation sheet described in 
 * A. C. Hamilton, B. Sundar and J. Courtial, "Local light-ray rotation around arbitrary axes", J. Opt. 12, 095101 (2010)
 * 
 * (If <basis> = GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, the scene object has to be parametrised.)
 * 
 * @author Johannes
 *
 */
public class RayRotatingAboutArbitraryAxisDirection extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -8362326164701511685L;

	/**
	 * The angle, in radians, by which the light-ray direction is rotated upon outwards transmission;
	 * upon inwards transmission, the light-ray direction is rotated by an angle of the same magnitude but opposite direction
	 */
	private double outwardsRotationAngle;
	
	/**
	 * A unit vector in the direction of the rotation axis
	 */
	private Vector3D rotationAxisUnitVector;
	
	/**
	 * The basis in which the rotation axis unit vector is specified
	 */
	private GlobalOrLocalCoordinateSystemType basis;
	
	/**
	 * @param outwardsRotationAngle	in radians
	 * @param rotationAxisUnitVector
	 * @param basis
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public RayRotatingAboutArbitraryAxisDirection(
			double outwardsRotationAngle,
			Vector3D rotationAxisUnitVector,
			GlobalOrLocalCoordinateSystemType basis,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		this.outwardsRotationAngle = outwardsRotationAngle;
		this.rotationAxisUnitVector = rotationAxisUnitVector;
		this.basis = basis;
	}
	
	/**
	 * Default: 90° rotation around surface normal
	 */
	public RayRotatingAboutArbitraryAxisDirection()
	{
		this(MyMath.deg2rad(90), new Vector3D(0, 0, 1), GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, DEFAULT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RayRotatingAboutArbitraryAxisDirection clone()
	{
		return new RayRotatingAboutArbitraryAxisDirection(getOutwardsRotationAngle(), getRotationAxisUnitVector(), getBasis(), getTransmissionCoefficient(), isShadowThrowing());
	}

	//
	// getters & setters
	//
	
	public double getOutwardsRotationAngle() {
		return outwardsRotationAngle;
	}

	public void setOutwardsRotationAngle(double outwardsRotationAngle) {
		this.outwardsRotationAngle = outwardsRotationAngle;
	}

	public Vector3D getRotationAxisUnitVector() {
		return rotationAxisUnitVector;
	}

	public void setRotationAxisUnitVector(Vector3D rotationAxisUnitVector) {
		this.rotationAxisUnitVector = rotationAxisUnitVector;
	}

	public GlobalOrLocalCoordinateSystemType getBasis() {
		return basis;
	}

	public void setBasis(GlobalOrLocalCoordinateSystemType basis) {
		this.basis = basis;
	}
	
	
	//
	// implement SurfaceProperty method
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, int, optics.raytrace.core.RaytraceExceptionHandler)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i,
			SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// first calculate the rotation angle
		Vector3D rayDirection = ray.getD();
		Vector3D outwardsNormal = i.getNormalisedOutwardsSurfaceNormal();
		
		double rotationAngle;
		switch(Orientation.getOrientation(rayDirection, outwardsNormal))
		{
		case INWARDS:
			rotationAngle = -getOutwardsRotationAngle();
			break;
		case OUTWARDS:
		default:
			rotationAngle = getOutwardsRotationAngle();
		}
		
		// calculate a unit vector in the direction of the rotation axis in global coordinates
		Vector3D rotationAxisNormalsed;
		switch(getBasis())
		{
		case LOCAL_OBJECT_BASIS:
			rotationAxisNormalsed = getRotationAxisUnitVector().toBasis(CoordinateSystems.getSurfaceBasis((ParametrisedObject)(i.o), i.p)).getNormalised();
			break;
		case GLOBAL_BASIS:
		default:
			rotationAxisNormalsed = getRotationAxisUnitVector().getNormalised();
		}
		
		Vector3D newRayDirection = Geometry.rotate(
				rayDirection,	// direction vector
				rotationAxisNormalsed,
				rotationAngle
			);
		
		// TODO is this the right thing to do?
		// if the orientation has changed, reverse the light-ray direction
		if(Orientation.getOrientation(rayDirection, outwardsNormal) != Orientation.getOrientation(newRayDirection, outwardsNormal))
		{
			newRayDirection = newRayDirection.getReverse();
		}
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t, ray.isReportToConsole()),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}
}
