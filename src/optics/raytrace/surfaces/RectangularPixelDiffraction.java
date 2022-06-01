package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.utility.SingleSlitDiffraction;

/**
 * @author johannes
 * This surface property simulates the light-ray direction change due to diffraction by a rectangular pixel.
 */
public class RectangularPixelDiffraction extends Diffusion
{
	private static final long serialVersionUID = 6023321133278951887L;

	private double lambda;
	private double pixelSideLengthU;
	private double pixelSideLengthV;
	private Vector3D uHat;
	private Vector3D vHat;

	public RectangularPixelDiffraction(
			double lambda,
			double pixelSideLengthU,
			double pixelSideLengthV,
			Vector3D uHat,
			Vector3D vHat
		)
	{
		super();
		
		this.lambda = lambda;
		this.pixelSideLengthU = pixelSideLengthU;
		this.pixelSideLengthV = pixelSideLengthV;
		this.uHat = uHat;
		this.vHat = vHat;
	}
	
	public RectangularPixelDiffraction(RectangularPixelDiffraction original)
	{
		this(
				original.getLambda(),
				original.getPixelSideLengthU(),
				original.getPixelSideLengthV(),
				original.getuHat(),
				original.getvHat()
			);
	}
	
	@Override
	public RectangularPixelDiffraction clone()
	{
		return new RectangularPixelDiffraction(this);
	}
	
	
	//
	// setters & getters
	//
	
	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

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

	public Vector3D getuHat() {
		return uHat;
	}

	public void setuHat(Vector3D uHat) {
		this.uHat = uHat;
	}

	public Vector3D getvHat() {
		return vHat;
	}

	public void setvHat(Vector3D vHat) {
		this.vHat = vHat;
	}

	
	//
	// Diffusion methods
	//


	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.Diffusion#calculateDirectionChange(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D calculateDirectionChange(
			Vector3D incidentNormalisedRayDirection,
			Vector3D normalisedOutwardsSurfaceNormal
		)
	{
		return SingleSlitDiffraction.getTangentialDirectionComponentChange(
				lambda,
				pixelSideLengthU,	// pixelSideLengthU
				pixelSideLengthV,	// pixelSideLengthV
				uHat,	// uHat
				vHat	// vHat
			);
	}
	

	
}
