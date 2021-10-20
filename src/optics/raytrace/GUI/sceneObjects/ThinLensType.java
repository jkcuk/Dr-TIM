package optics.raytrace.GUI.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.SemiTransparent;

public enum ThinLensType {
	IDEAL_THIN_LENS("Ideal thin lens"),
	LENS_HOLOGRAM("Phase hologram of lens"),
	SEMITRANSPARENT_PLANE("Semi-transparent plane");

	private String description;
	private ThinLensType(String description)
	{
		this.description = description;
	}
	@Override
	public String toString() {return description;}
	
	public static SurfaceProperty createThinLensSurface(ThinLensType thinLensType, double focalLength, Vector3D principalPoint, Vector3D opticalAxisDirection, double transmissionCoefficient, boolean shadowThrowing)
	{
		switch(thinLensType)
		{
		case LENS_HOLOGRAM:
			return new PhaseHologramOfLens(
					focalLength, 
					principalPoint,
					transmissionCoefficient,	// throughputCoefficient
					false,	// reflective
					shadowThrowing
				);
		case SEMITRANSPARENT_PLANE:
			return SemiTransparent.BLUE_SHINY_SEMITRANSPARENT;
		case IDEAL_THIN_LENS:
		default:
//			return new GlensSurface(
//					opticalAxisDirection,	// opticalAxisDirectionPos
//					principalPoint,	// principalPoint
//					-focalLength,	// focalLengthNeg
//					focalLength,	// focalLengthPos
//					transmissionCoefficient,	// transmissionCoefficient
//					shadowThrowing	// shadowThrowing
//					);
			return new IdealThinLensSurfaceSimple(
					principalPoint,	// lensCentre
					opticalAxisDirection,
					focalLength,
					transmissionCoefficient,
					shadowThrowing
				);

		}

	}
}