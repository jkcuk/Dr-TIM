package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.utility.SingleSlitDiffraction;

/**
 * @author johannes
 * This can be a stand-alone surface property that simulates a pixellated, but otherwise transparent, surface (hence it is a subclass of SurfaceProperty),
 * or it can be used to simulate pixellation of another surface property, e.g. GCLAs, in a <i>SurfacePropertyLayerStack</i>.
 * TODO implement more realistic distributions of the diffractive angular deviation that represent slit diffraction etc. -- see http://www.av8n.com/physics/arbitrary-probability.htm
 */
public class Pixellation extends SurfaceProperty
{
	private static final long serialVersionUID = -8564617628976453462L;

	// additional parameters
	
	/**
	 * side length of each "pixel", assuming rectangular pixels whose sides are aligned with the surface-coordinate axes;
	 * used to calculate approximate magnitude of diffractive blur and position offset;
	 * when combined with GCLAs, this can be calculated from f1 and sigma
	 * @see optics.raytrace.core.ParametrisedObject.getSurfaceCoordinateAxes(Vector3D)
	 */
	private double pixelSideLengthU;

	/**
	 * side length of each "pixel", assuming rectangular pixels whose sides are aligned with the surface-coordinate axes;
	 * used to calculate approximate magnitude of diffractive blur and position offset;
	 * when combined with GCLAs, this can be calculated from f1 and sigma
	 * @see optics.raytrace.core.ParametrisedObject.getSurfaceCoordinateAxes(Vector3D)
	 */
	private double pixelSideLengthV;

	/**
	 * wavelength of light;
	 * used to calculate approximate magnitude of diffractive blur
	 */
	private double lambda;	// wavelength of light, for diffraction purposes
	
	
	/**
	 * if true, add a random angle that represents diffractive blur to the direction of the outgoing light ray
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * if true, add a random position offset that represents the ray-position offset during transmission
	 */
	private boolean simulateRayOffset;
	
	
	/**
	 * @param pixelSideLengthU
	 * @param pixelSideLengthV
	 * @param lambda
	 * @param simulateDiffractiveBlur
	 * @param simulateRayOffset
	 */
	public Pixellation(
			double pixelSideLengthU,
			double pixelSideLengthV,
			double lambda,
			boolean simulateDiffractiveBlur,
			boolean simulateRayOffset
		) 
	{
		super();
		this.pixelSideLengthU = pixelSideLengthU;
		this.pixelSideLengthV = pixelSideLengthV;
		this.lambda = lambda;
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
		this.simulateRayOffset = simulateRayOffset;
	}
	
	/**
	 * @param pixelSideLength
	 * @param lambda
	 * @param simulateDiffractiveBlur
	 * @param simulateRayOffset
	 */
	public Pixellation(
			double pixelSideLength,
			double lambda,
			boolean simulateDiffractiveBlur,
			boolean simulateRayOffset
		)
	{
		this(pixelSideLength, pixelSideLength, lambda, simulateDiffractiveBlur, simulateRayOffset);
	}


	/**
	 * @param original
	 * Create a copy of <i>original</i>.
	 */
	public Pixellation(Pixellation original)
	{
		this(
				original.getPixelSideLengthU(),
				original.getPixelSideLengthV(),
				original.getLambda(),
				original.isSimulateDiffractiveBlur(),
				original.isSimulateRayOffset()
			);
	}

	/**
	 * @return	a clone of this Pixellation
	 */
	@Override
	public Pixellation clone()
	{
		return new Pixellation(this);
	}

	
	//
	// getters & setters
	//
	
	public double getPixelSideLengthU() {
		return pixelSideLengthU;
	}

	public void setPixelSideLengthU(double pixelSideLengthU) {
		this.pixelSideLengthU = pixelSideLengthU;
	}

	public double getPixelSideLengthV() {
		return pixelSideLengthV;
	}

	public void setPixelSideLengthV(double pixelSideLengthV) {
		this.pixelSideLengthV = pixelSideLengthV;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public boolean isSimulateRayOffset() {
		return simulateRayOffset;
	}

	public void setSimulateRayOffset(boolean simulateRayOffset) {
		this.simulateRayOffset = simulateRayOffset;
	}

	

	//
	// SurfaceProperty methods
	//
	
	@Override
	public DoubleColour getColour(
			Ray ray,
			RaySceneObjectIntersection intersection,
			SceneObject scene,
			LightSource lights,
			int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler
	)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// the intersected object has to be a ParametrisedObject, which has associated surface-coordinate axes;
		// this is necessary as we assume that the pixel sides are aligned with the surface-coordinate axes
		if(!(intersection.o instanceof ParametrisedObject))
		{
			// the intersected object is *not* a ParametrisedObject, so the pixel orientation is not defined
			throw new RayTraceException("Cannot simulate pixellation imperfections as intersected SceneObject is not a ParametrisedObject.");
		}
		
		// everything is okay: the intersected object is a ParametrisedObject
	
		// find the surface-coordinate axes
		ArrayList<Vector3D> surfaceCoordinateAxes = ((ParametrisedObject)(intersection.o)).getSurfaceCoordinateAxes(intersection.p);
		Vector3D surfaceCoordinate1Axis = surfaceCoordinateAxes.get(0).getNormalised();
		Vector3D surfaceCoordinate2Axis = surfaceCoordinateAxes.get(1).getNormalised();
	
		// find the non-pixellated ray parameters...
		Vector3D rayStartPosition = intersection.p;
		Vector3D rayDirection = ray.getD().getNormalised();
		double rayStartTime = intersection.t;
		
		// ... and pixellate them
		
		if(simulateDiffractiveBlur)
		{
			// simulate diffractive blur
			
			// The first diffraction minimum corresponds to a direction in which light rays that
			// have passed through the slit a transverse distance w/2 apart receive phase shifts
			// that differ by pi.  Such phase shifts are precisely achieved with a phase hologram
			// that corresponds to a transverse phase gradient of pi / (w / 2) = 2 pi / w.
			// 	So the direction of the first diffraction minimum can be calculated by simulating
			// transmission through a phase hologram with this phase gradient.
			// In the PhaseHologram class, phase gradients are given in units of (2 pi/lambda),
			// and so a phase gradient of 2 pi / w becomes (2 pi / w) / (2 pi / lambda) = lambda / w.
			// Here, a uniformly distributed random phase-gradient in the range +/- lambda/w is added
			// in each transverse dimension.
			// (2.*(Math.random()-0.5) gives a uniformly distributes random number in the range -1 to 1.)
			Vector3D tangentialDirectionComponentChange = Vector3D.sum(
					surfaceCoordinate1Axis.getProductWith(SingleSlitDiffraction.getRandomSinTheta(lambda, pixelSideLengthU)),
					surfaceCoordinate2Axis.getProductWith(SingleSlitDiffraction.getRandomSinTheta(lambda, pixelSideLengthV))
//					surfaceCoordinate1Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5)),
//					surfaceCoordinate2Axis.getProductWith(lambda/pixelSideLength*2.*(Math.random()-0.5))
				);
			try
			{
				rayDirection = PhaseHologram.getOutgoingNormalisedRayDirection(
						rayDirection.getNormalised(),	// incidentNormalisedRayDirection
						tangentialDirectionComponentChange,	// tangentialDirectionComponentChange
						intersection.o.getNormalisedOutwardsSurfaceNormal(rayStartPosition),	// normalisedOutwardsSurfaceNormal
						false	// isReflective
					);
			}
			catch(EvanescentException e)
			{
				// this is normal -- return the reflected ray
				// (Don't multiply by the transmission coefficient, as this is TIR!)
				return Reflective.getReflectedColour(ray, intersection, scene, lights, traceLevel-1, raytraceExceptionHandler);
			}
		}

		if(simulateRayOffset)
		{
			// simulate a random ray offset, assuming square pixels whose sides are aligned with the surface-coordinate axes;
			// this offset can be between -pixelSideLength and +pixelSideLength in the direction of these surface-coordinate axes
			// @see optics.raytrace.core.ParametrisedObject.getSurfaceCoordinateAxes(Vector3D)

			rayStartPosition = Vector3D.sum(
					rayStartPosition,
					surfaceCoordinate1Axis.getProductWith(pixelSideLengthU*2.*(Math.random()-0.5)),
					surfaceCoordinate2Axis.getProductWith(pixelSideLengthV*2.*(Math.random()-0.5))
				);
		}


		// launch a new ray from here
			
		return scene.getColourAvoidingOrigin(
						ray.getBranchRay(
								rayStartPosition,
								rayDirection,
								rayStartTime
								),
						intersection.o,
						lights,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
				);
	}



	@Override
	public boolean isShadowThrowing() {
		return false;
	}
}
