package optics.raytrace.surfaces;


import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Plane;


/**
 * A surface property representing the a space plate
 * This is able to add an extra distance to a propagating light ray upon intersection with the plate
 * The main use of this is to make optical systems more compact
 * Here we define it to be idealised i.e non dispersive and work for all wavelengths
 * Additionally, it is thin which may not always be the case with a 'real' space plate.
 * 
 * @author Maik
 */
public class IdealThinSpacePlateSurface extends SurfacePropertyPrimitive
{

	private static final long serialVersionUID = 4583299398816200666L;
	
	// centre of the space plate
	protected Vector3D centre;
	// the surface normal to construcct the space plate
	protected Vector3D surfaceNormal;
	// the extra distance the space plate should add
	protected double extraDistance;	
	
	/**
	 * Creates an instance of the surface property that adds an extra bit of propagation space to a light ray.
	  * 
	  * @param centre
	  * @param surfaceNormal
	  * @param extraDistance
	  * @param transmissionCoefficient
	  * @param shadowThrowing
	  */
	public IdealThinSpacePlateSurface(
			Vector3D centre,
			Vector3D surfaceNormal,
			double extraDistance,
			double transmissionCoefficient,
			boolean shadowThrowing
		) 
	{
		super(transmissionCoefficient, shadowThrowing);
		this.centre = centre;
		this.surfaceNormal = surfaceNormal;
		this.extraDistance = extraDistance;
		
	}
	/**
	 * Make a clone of the original IdealThinLensSurface surface property.
	 * @param original
	 */
	public IdealThinSpacePlateSurface(IdealThinSpacePlateSurface original)
	{
		this(
				original.getCentre(),
				original.getSurfaceNormal(),
				original.getExtraDistance(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IdealThinSpacePlateSurface clone()
	{
		return new IdealThinSpacePlateSurface(this);
	}
	
	// setters and getters

	public Vector3D getCentre() {
		return centre;
	}
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}
	public Vector3D getSurfaceNormal() {
		return surfaceNormal;
	}
	public void setSurfaceNormal(Vector3D surfaceNormal) {
		this.surfaceNormal = surfaceNormal.getNormalised();
	}
	public double getExtraDistance() {
		return extraDistance;
	}
	public void setExtraDistance(double extraDistance) {
		this.extraDistance = extraDistance;
	}
	
	//
	// implement SurfaceProperty method
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.SurfaceProperty#getColour(optics.raytrace.Ray, optics.raytrace.RaySceneObjectIntersection, optics.raytrace.SceneObject, optics.raytrace.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		//make sure normal is always in ray direction
		if(Vector3D.scalarProduct(surfaceNormal, ray.getD()) < 0) {
			surfaceNormal = surfaceNormal.getProductWith(-1);
		}
		
	
		//place a plane a distance away from the intersection and find where the same ray intersects this plane.
		//TODO check that it does not exit out the sides i.e if i2.p is no longer on first surface then return black or something else

Plane intersectionPlane = new Plane(
		"plane of intersection after propegation",// description,
		Vector3D.sum(centre, surfaceNormal.getProductWith(extraDistance)),// pointOnPlane,
		surfaceNormal,// normal, 
		null,// surfaceProperty,
		null,// parent,
		null// studio
		);

		RaySceneObjectIntersection i2 = intersectionPlane.getClosestRayIntersection(ray);
		
		Vector3D rOut = Vector3D.difference(i2.p, surfaceNormal.getProductWith(extraDistance));
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(rOut, ray.getD(), i2.t, ray.isReportToConsole()),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			getTransmissionCoefficient()
		);
	}
	
	
	
//	@Override
//	public String toString() {
//		return "IdealThinLensSurface [principalPoint=" + principalPoint + ", opticalAxisDirectionPos="
//				+ opticalAxisDirectionPos + ", focalLength=" + focalLength + "]";
//	}
//
//	/* (non-Javadoc)
//	 * @see optics.raytrace.imagingElements.ImagingElement#toSurfaceProperty(double, boolean)
//	 */
//	@Override
//	public SurfaceProperty toSurfaceProperty(
//			double imagingSurfaceTransmissionCoefficient,
//			boolean imagingSurfaceShadowThrowing
//		)
//	{
//		setTransmissionCoefficient(imagingSurfaceTransmissionCoefficient);
//		setShadowThrowing(imagingSurfaceShadowThrowing);
//		
//		return this;
//	}

}
