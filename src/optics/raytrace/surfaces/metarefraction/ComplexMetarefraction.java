package optics.raytrace.surfaces.metarefraction;

import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import math.Complex;
import math.Vector3D;

/**
 * 
 * @author George Constable, Alasdair Hamilton, Johannes Courtial
 *
 */


public abstract class ComplexMetarefraction extends Metarefraction
{
	/**
	 * The methods any non-abstract subclass of ComplexMetarefraction needs to implement.
	 * The incident and refracted light-ray directions are represented as complex numbers.
	 * For a given incident light-ray direction, return the refracted light-ray direction. 
	 * 
	 * @param c	complex number representing complexIncidentRayDirection
	 * @return complex number corresponding to the refracted ray direction in the surface basis. 
	 */
	public abstract Complex complexRefractOutwards(Complex incidentRayDirection);
	public abstract Complex complexRefractInwards(Complex incidentRayDirection);
	
//	/* (non-Javadoc)
//	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#clone()
//	 */
//	public abstract ComplexMetarefraction clone();
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#refractOutwards(math.Vector3D)
	 */
	@Override
	public Vector3D refractOutwards(Vector3D incidentRayDirection) throws RayTraceException
	{
		Vector3D incidentRayDirectionNormalised = incidentRayDirection.getNormalised();
		
		// Transform incident ray to projection into the Argand plane.
		// x direction on surface coincides with the real axis. y direction on surface
		// coincides with imaginary axis.
		Complex complexIncidentRayDirection = new Complex(incidentRayDirectionNormalised.x, incidentRayDirectionNormalised.y);
		
		/**
		 * implement abstract method complexRefract. Acts on complexIncidentRayDirection to 
		 * return complexRefractedRayDirection.
		 * Actual transform is dependent on instance of extended class.
		 */
		Complex complexRefractedRayDirection = complexRefractOutwards(complexIncidentRayDirection);

		// any nonsense results?
		if(Double.isNaN(complexRefractedRayDirection.r) || Double.isNaN(complexRefractedRayDirection.i))
			throw new RayTraceException("NaN");

		/**
		 * Transform complex number back into the Vector3D it represents in the basis of the surface
		 *
		 * Start by calculating z coordinate from complex term. Assuming normalisation,
		 * z = sqrt(1-[Re(c')^2 + Im(c')^2])
		 *
		 */
	
		// Introduce variable modSquared for error testing/z Vector3D calculation.
		double modSquared = Complex.modulusSquared(complexRefractedRayDirection);
								
		// check for evanescent ray
		if( modSquared > 1 ) throw new EvanescentException("ComplexMetarefraction::refractOutwards: refracted ray is evanescent");

		// Otherwise return normalised Vector3D. Also maintain direction of z component.
		return(new Vector3D(
				complexRefractedRayDirection.r,
				complexRefractedRayDirection.i,
				Math.sqrt(1-modSquared) // "+" sign as the ray is travelling outwards
			));
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.metarefraction.Metarefraction#refractInwards(math.Vector3D)
	 */
	@Override
	public Vector3D refractInwards(Vector3D incidentRayDirection) throws RayTraceException
	{
		Vector3D incidentRayDirectionNormalised = incidentRayDirection.getNormalised();
		
		// Transform incident ray to projection onto the Argand plane.
		// x direction on surface coincides with the real axis. y direction on surface
		//coincides with imaginary axis.
		Complex complexIncidentRayDirection = new Complex(incidentRayDirectionNormalised.x, incidentRayDirectionNormalised.y);
		
		/**
		 * implement abstracted method complxRefract. Acts on complexIncidentRay to 
		 * return complexRefractedRay.
		 * Actual transform is dependent on instance of extended class.
		 */
		Complex complexRefractedRayDirection = complexRefractInwards(complexIncidentRayDirection);
		
		// any nonsense results?
		if(Double.isNaN(complexRefractedRayDirection.r) || Double.isNaN(complexRefractedRayDirection.i))
			throw new RayTraceException("NaN");

		/**
		 * Transform Vector3D back into a real Vector3D in the basis of the surface
		 *
		 *Start by calculating Z - value from complex term. Assuming normalisation,
		 *z = sqrt(1-[Re(c')^2 + Im(c')^2])
		 *
		 */
		
		// Introduce variable modSquaredComplex for error testing/z Vector3D calculation.
		double modSquared = Complex.modulusSquared(complexRefractedRayDirection);
								
		// check for evanescent ray
		if( modSquared > 1 ) throw new EvanescentException("ComplexMetarefraction::refractInwards: refracted ray is evanescent");

		// Otherwise return normalised Vector3D. Also Maintain Direction of Z component.
		return(new Vector3D(
				complexRefractedRayDirection.r,
				complexRefractedRayDirection.i,
				-Math.sqrt(1-modSquared) // "-" sign because the ray is travelling inwards
			));
	}
} 