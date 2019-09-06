package optics.raytrace.panorama.panorama3DViewer;

import optics.raytrace.panorama.panorama3DGeometry.AbstractPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.GeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.PositionInvertingGeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.PositionInvertingStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.StandardPanorama3DGeometry;

public enum Panorama3DGeometryViewingType
{
	STANDARD_GEOMETRY("Standard geometry", new StandardPanorama3DGeometry()),
	STANDARD_GEOMETRY_POSITION_INVERTING("Position-inverting standard geometry", new PositionInvertingStandardPanorama3DGeometry()),
	// SIN_THETA_IPD_FACTOR_GEOMETRY("IPD multiplied by sin(theta)", new SinThetaIPDFactorPanorama3DGeometry()),
	FINITE_RADIUS_GEOMETRY("That finite-radius geometry", new PositionInvertingGeneralisedStandardPanorama3DGeometry());

	private String description;
	private AbstractPanorama3DGeometry panorama3DGeometry;
	private Panorama3DGeometryViewingType(String description, AbstractPanorama3DGeometry panorama3DGeometry)
	{
		this.description = description;
		this.panorama3DGeometry = panorama3DGeometry;
	}
	
	public AbstractPanorama3DGeometry toPanorama3DGeometry() {return panorama3DGeometry;}
	@Override
	public String toString() {return description;}

	public static Panorama3DGeometryViewingType Panorama3DGeometry2Panorama3DGeometryType(AbstractPanorama3DGeometry g)
	{
		// if(g instanceof SinThetaIPDFactorPanorama3DGeometry) return SIN_THETA_IPD_FACTOR_GEOMETRY;
		if(g instanceof GeneralisedStandardPanorama3DGeometry) return FINITE_RADIUS_GEOMETRY;
		if(g instanceof PositionInvertingStandardPanorama3DGeometry) return STANDARD_GEOMETRY_POSITION_INVERTING;
		if(g instanceof StandardPanorama3DGeometry) return STANDARD_GEOMETRY;
		// g doesn't seem to be of one of the above types; return null
		return null;
	}
}
