package optics.raytrace.panorama.panorama3DViewer;

import optics.raytrace.panorama.panorama3DGeometry.AbstractPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.GeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.ParabolaThetaIPDFactorPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.PositionInvertingGeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.SinNThetaIPDFactorPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.StandardPanorama3DGeometry;

public enum Panorama3DGeometryRenderingType
{
	STANDARD_GEOMETRY("Standard geometry", new StandardPanorama3DGeometry()),
	// STANDARD_GEOMETRY_POSITION_INVERTING("Position-inverting standard geometry", new PositionInvertingStandardPanorama3DGeometry()),
	SIN_THETA_IPD_FACTOR_GEOMETRY("IPD multiplied by sin(theta)", new SinNThetaIPDFactorPanorama3DGeometry()),
	POWER2_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^2)", new ParabolaThetaIPDFactorPanorama3DGeometry()),
	POWER4_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^4)", new ParabolaThetaIPDFactorPanorama3DGeometry(2)),
	POWER6_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^6)", new ParabolaThetaIPDFactorPanorama3DGeometry(3)),
	POWER8_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^8)", new ParabolaThetaIPDFactorPanorama3DGeometry(4)),
	POWER10_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^10)", new ParabolaThetaIPDFactorPanorama3DGeometry(5)),
	POWER16_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^16)", new ParabolaThetaIPDFactorPanorama3DGeometry(8)),
	POWER20_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^20)", new ParabolaThetaIPDFactorPanorama3DGeometry(10)),
	POWER32_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^32)", new ParabolaThetaIPDFactorPanorama3DGeometry(16)),
	POWER64_IPD_FACTOR_GEOMETRY("IPD multiplied by (1-(theta/90°-1)^64)", new ParabolaThetaIPDFactorPanorama3DGeometry(32)),
	FINITE_RADIUS_GEOMETRY("That finite-radius geometry", new PositionInvertingGeneralisedStandardPanorama3DGeometry());

	private String description;
	private AbstractPanorama3DGeometry panorama3DGeometry;
	private Panorama3DGeometryRenderingType(String description, AbstractPanorama3DGeometry panorama3DGeometry)
	{
		this.description = description;
		this.panorama3DGeometry = panorama3DGeometry;
	}
	
	public AbstractPanorama3DGeometry toPanorama3DGeometry() {return panorama3DGeometry;}
	@Override
	public String toString() {return description;}

	public static Panorama3DGeometryRenderingType Panorama3DGeometry2Panorama3DGeometryType(AbstractPanorama3DGeometry g)
	{
		if(g instanceof SinNThetaIPDFactorPanorama3DGeometry) return SIN_THETA_IPD_FACTOR_GEOMETRY;
		if(g instanceof GeneralisedStandardPanorama3DGeometry) return FINITE_RADIUS_GEOMETRY;
		// if(g instanceof PositionInvertingStandardPanorama3DGeometry) return STANDARD_GEOMETRY_POSITION_INVERTING;
		if(g instanceof StandardPanorama3DGeometry) return STANDARD_GEOMETRY;
		// g doesn't seem to be of one of the above types; return null
		return null;
	}
}
