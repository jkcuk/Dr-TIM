package optics.raytrace.surfaces;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;


/**
 * 
 * Gives a SceneObject an internal refractive index, light passing through this object will obey the
 * laws of refraction.
 * 
 * The scene object does not need to be parametrised.
 * 
 * @author Dean, Johannes
 *
 */
public class RefractiveSimple extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 3318918776617994512L;

	double insideOutsideRefractiveIndexRatio;
	
	public RefractiveSimple(double insideOutsideRefractiveIndexRatio, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		this.insideOutsideRefractiveIndexRatio=insideOutsideRefractiveIndexRatio;
	}
	
	/**
	 * Default with a negative refracting window.
	 */
	public RefractiveSimple()
	{
		this(1, DEFAULT_TRANSMISSION_COEFFICIENT, true);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public RefractiveSimple clone()
	{
		return new RefractiveSimple(insideOutsideRefractiveIndexRatio, getTransmissionCoefficient(), isShadowThrowing());
	}

	public double getInsideOutsideRefractiveIndexRatio() {
		return insideOutsideRefractiveIndexRatio;
	}

	public void setInsideOutsideRefractiveIndexRatio(double insideOutsideRefractiveIndexRatio) {
		this.insideOutsideRefractiveIndexRatio = insideOutsideRefractiveIndexRatio;
	}

	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i,
			SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		Vector3D rayDirection = ray.getD();
		Vector3D outwardsNormal = i.getNormalisedOutwardsSurfaceNormal();
		
		double refractiveIndexRatio;
		
		switch(Orientation.getOrientation(rayDirection, outwardsNormal))
		{
		case INWARDS:
			// TODO check this!
			refractiveIndexRatio = 1./insideOutsideRefractiveIndexRatio;
			break;
		case OUTWARDS:
		default:
			// TODO check this!
			refractiveIndexRatio = insideOutsideRefractiveIndexRatio;
		}
		
		Vector3D newRayDirection = getRefractedLightRayDirection(
				rayDirection,	// incidentLightRayDirection
				outwardsNormal,	// surfaceNormal
				refractiveIndexRatio
			);
		
		// System.out.println("RefractiveSimple::getColour: rayDirection="+rayDirection+", outwardsNormal="+outwardsNormal+", newRayDirection ="+newRayDirection);
		
//		Vector3D V1, V2,
//		N = i.o.getNormalisedOutwardsSurfaceNormal(i.p), //normalised normal Vector3D at point of intersection
//		rNormalised = ray.getD().getNormalised(); //incoming ray direction normalised
//		
//		double nRatio,
//		cosThetaIncoming = Vector3D.scalarProduct(N, Vector3D.scalarTimesVector3D(-1, rNormalised));
//
//		if(cosThetaIncoming>0)
//		{
//			nRatio = 1/insideOutsideRefractiveIndexRatio;
//			double cosThetaRefracted = Math.sqrt(1-MyMath.square(nRatio)*(1-MyMath.square(cosThetaIncoming)));
//			V2=Vector3D.scalarTimesVector3D(
//					nRatio*cosThetaIncoming-cosThetaRefracted
//					,N);
//		}
//
//		else
//		{
//			nRatio = insideOutsideRefractiveIndexRatio;
//			double cosThetaRefracted = Math.sqrt(1-MyMath.square(nRatio)*(1-MyMath.square(cosThetaIncoming)));
//			V2=Vector3D.scalarTimesVector3D(
//					nRatio*cosThetaIncoming+cosThetaRefracted
//					,N);
//		}
//
//		V1 = Vector3D.scalarTimesVector3D(nRatio, rNormalised);	
//		Vector3D newRayDirection = Vector3D.sum(V1, V2);
		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		);
	}
	
	
	/**
	 * @param normalisedDirectionIn
	 * @param normalisedDirectionOut
	 * @param nIn
	 * @param nOut
	 * @return	a normalised vector parallel to the surface normal
	 */
	public static Vector3D getNormal(Vector3D normalisedDirectionIn, Vector3D normalisedDirectionOut, double nIn, double nOut)
	{
		// Snell's law in vector form (see, e.g. https://en.wikipedia.org/wiki/Snell%27s_law#Vector_form):
		//   v2Hat = (n1/n2) v1Hat + (...) nHat,
		// where v1Hat and v2Hat are the normalised light-ray directions on the two sides of the interface, so
		//   nHat \propto n2 v2Hat - n1 v1Hat
		return Vector3D.difference(
				normalisedDirectionOut.getProductWith(nOut),
				normalisedDirectionIn.getProductWith(nIn)
			).getNormalised();
	}
	
	/**
	 * Calculate refraction according to Snell's law.
	 * 
	 * @param incidentLightRayDirection	the (not necessarily normalised) incident light-ray direction
	 * @param surfaceNormal	the (not necessarily normalised) surface normal
	 * @param refractiveIndexRatio	the ratio of the refractive indices, n_{in front} / n_{behind)
	 * @return	the outgoing light-ray direction
	 * @throws EvanescentException 
	 */
	public static Vector3D getRefractedLightRayDirection(
			Vector3D incidentLightRayDirection,
			Vector3D surfaceNormal,
			double refractiveIndexRatio
		)
		throws EvanescentException
	{
//		System.out.println("RefractiveSimple::getRefractedLightRayDirection: incidentLightRayDirection="+incidentLightRayDirection+
//				", surfaceNormal="+surfaceNormal+
//				", refractiveIndexRatio"+refractiveIndexRatio
//			);
		
		// the normalised light-ray direction
		Vector3D d = incidentLightRayDirection.getNormalised();
		
		// the component of the incident light-ray direction perpendicular to the surface, ...
		Vector3D dNormal = d.getPartParallelTo(surfaceNormal);
		
		// ... and the component that's in the plane of the surface
		Vector3D dSurface = Vector3D.difference(d, dNormal);
		
		// calculate the direction of the refracted ray
		
		// the component in the plane of the surface
		Vector3D dPrimeSurface = dSurface.getProductWith(refractiveIndexRatio);
		
		// which value of sin^2(alphaPrime) does this correspond to?
		double sinSquareAlphaPrime = dPrimeSurface.getModSquared();
		
		if(sinSquareAlphaPrime > 1)
		{
			// TIR
			throw new EvanescentException("RefractionGeometry::refract: refracted ray is evanescent, TIR occurs");
			
			// --- return the reflected ray direction, i.e. simply invert the direction perpendicular to the surface
			// return Vector3D.difference(dSurface, dNormal);
		}
		
		// the component perpendicular to the surface
		Vector3D dPrimeNormal = dNormal.getWithLength(Math.sqrt(1 - sinSquareAlphaPrime));
		
		// return the refracted light-ray direction
		return Vector3D.sum(dPrimeSurface, dPrimeNormal);
	}
}
