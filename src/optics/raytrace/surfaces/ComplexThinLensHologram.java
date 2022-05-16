package optics.raytrace.surfaces;

import optics.DoubleColour;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import math.Vector3D;

/**
 * @author Magnus More, Johannes Courtial
 * 
 * A surface property representing a hologram of a thin lens with certain complex focal lengths.
 * 
 * A ray that hits the hologram at a distance r from the position of the lens centre 
 * gets rotated by an angle angleFactor*r (in radians) around the radial direction.
 * This is based on the idea that rotations can be represented as vectors (direction
 * is the axis direction, length is rotation angle), and that the combined effect of
 * rotations is described by the sum of the vectors representing the individual
 * rotations, provided the individual rotation vectors are all approximately parallel
 * or antiparallel.
 * 
 * This class really only works if the surface is planar, and if the lens centre
 * lies on the surface.  This is not being checked.
 */

public class ComplexThinLensHologram extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -7750352676910306946L;

	protected Vector3D lensCentre;
	protected double angleFactor;
	
	//Constructor
	public ComplexThinLensHologram(Vector3D lensCentre, double angleFactor, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		setLensCentre(lensCentre);
		setAngleFactor(angleFactor);
	}

	public ComplexThinLensHologram(ComplexThinLensHologram original)
	{
		super(original.getTransmissionCoefficient(), original.isShadowThrowing());
		setLensCentre(original.getLensCentre());
		setAngleFactor(original.getAngleFactor());
	}
	
	@Override
	public ComplexThinLensHologram clone()
	{
		return new ComplexThinLensHologram(this);
	}

	public Vector3D getLensCentre()
	{
		return lensCentre;
	}

	public void setLensCentre(Vector3D lensCentre)
	{
		this.lensCentre = lensCentre;
	}

	public double getAngleFactor()
	{
		return angleFactor;
	}

	public void setAngleFactor(double angleFactor)
	{
		this.angleFactor = angleFactor;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceProperty#getColour(optics.raytrace.core.Ray, optics.raytrace.core.RaySceneObjectIntersection, optics.raytrace.sceneObjects.SceneObject, optics.raytrace.lights.LightSource, int)
	 */
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection intersection, SceneObject scene, LightSource lights, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		// Check traceLevel is greater than 0.
		if(traceLevel <= 0) return DoubleColour.BLACK;
	
		// first construct vectors in the normal, radial and azimuthal direction
		Vector3D nNormalised = intersection.getNormalisedOutwardsSurfaceNormal();
		
		Vector3D rVector = Vector3D.difference(intersection.p, getLensCentre());	// un-normalised
		// Vector3D rNormalised = rVector.getNormalised();
		
		// if the surface is planar, and the lens centre lies on the surface, then
		// this vector should be normalised
		Vector3D phiNormalised = Vector3D.crossProduct(rVector, nNormalised).getNormalised();
		
		// System.out.println("n="+nNormalised+", r="+rVector+", phi="+phiNormalised);
		
		// calculate the components of the incident light-ray direction in the radial direction,
		// in the azimuthal direction, and in the direction normal to the surface
		
		// the component in the normal direction
		double dN = Vector3D.scalarProduct(ray.getD(), nNormalised);
		
		// the component in the r direction
		// double dR = Vector3D.scalarProduct(ray.getD(), rNormalised);
		
		// the component in the phi direction
		double dPhi = Vector3D.scalarProduct(ray.getD(), phiNormalised);
		
		// now construct the outgoing light-ray direction
		
		// first calculate the rotation angle
		double
			rotationAngle;		// in radians
		
		// this depends on whether or not the ray is going inwards or outwards;
		// is the ray direction pointing in the direction as the (outwards-pointing) surface normal?
		if(Vector3D.scalarProduct(ray.getD(), nNormalised) > 0.)
		{
			// the ray is travelling outwards; rotate by the positive angle

			rotationAngle = rVector.getLength()*angleFactor;
		}
		else
		{
			// the ray is travelling inwards; rotate by the positive angle

			rotationAngle = -rVector.getLength()*angleFactor;
		}

		// pre-calculate sine and cosine of the angle 
		double
			cos = Math.cos(rotationAngle),
			sin = Math.sin(rotationAngle);
		
		// System.out.println("cos="+cos+", sin="+sin);

		// calculate the new light-ray direction		
		Vector3D newRayDirection = Vector3D.sum(
				// nNormalised.getProductWith(dN),
				nNormalised.getProductWith(dN*cos+dPhi*sin),
				// phiNormalised.getProductWith(dPhi),
				phiNormalised.getProductWith(dPhi*cos-dN*sin),
				// rNormalised.getProductWith(dR)
				ray.getD().getPartParallelTo(rVector)	// the part in the r direction
			);
		
		// if the new ray direction points in the "wrong" direction, turn it round
		if(Vector3D.scalarProduct(newRayDirection, nNormalised)/Vector3D.scalarProduct(ray.getD(),nNormalised) < 0)
		{
			// the ray has the "wrong" direction; turn it round
			newRayDirection = newRayDirection.getReverse();
		}
		
		// System.out.println("direction in = "+ray.getD()+", out = "+newRayDirection);

		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(intersection.p, newRayDirection, intersection.t, ray.isReportToConsole()),
			intersection.o,
			lights,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());	// *cosRatio --- see above
	}
}



