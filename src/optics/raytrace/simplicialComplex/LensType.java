package optics.raytrace.simplicialComplex;

/**
 * One of IDEAL_THIN_LENS or POINT_2_POINT_IMAGING_HOLOGRAM.
 * @author johannes
 */
public enum LensType
{
	/**
	 * Nothing at all
	 */
	NONE("None"),
	/**
	 * A semi-transparent, coloured plane (useful to visualise the position of the lens)
	 */
	SEMITRANSPARENT_PLANE("Semi-transparent plane"),
	/**
	 * Ideal thin lens, a planar surface with an IdealThinLensSurface surface
	 * @see optics.raytrace.surfaces.IdealThinLensSurface
	 */
	IDEAL_THIN_LENS("Ideal thin lens"),
	/**
	 * Point-to-point-imaging hologram, a planar surface with a Point2PointImagingPhaseHologram surface.
	 * The two points being imaged into each other are given separately.
	 * @see optics.raytrace.surfaces.Point2PointImagingPhaseHologram
	 */
	POINT_2_POINT_IMAGING_HOLOGRAM("Point-to-point imaging hologram"),
	/**
	 * Fresnel lens cut down to triangular appertures corresponding to the shapes of the faces.
	 * Two lens surfaces with one flat face each. Actual structures of material with a refractive index
	 */
	FRESNEL_LENS("Fresnel lens");

	private String description;

	private LensType(String description)
	{
		this.description = description;
	}

	@Override
	public String toString() {return description;}
	
	/**
	 * @return	the list of values, minus NONE
	 */
	public LensType[] valuesWithoutNONE()
	{
		LensType[] values = values();
		LensType[] valuesWithoutNONE = new LensType[values.length-1];
		for(int i=0, j=0; i<values.length; i++)
		{
			if(values[i] != NONE) valuesWithoutNONE[j++] = values[i];
		}
		return valuesWithoutNONE;
	}
}