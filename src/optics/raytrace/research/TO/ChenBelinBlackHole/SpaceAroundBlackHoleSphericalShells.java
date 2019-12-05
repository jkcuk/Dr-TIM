package optics.raytrace.research.TO.ChenBelinBlackHole;

import math.MyMath;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor;
import optics.raytrace.voxellations.SetOfConcentricSpheres;
import optics.raytrace.voxellations.Voxellation;

/**
 * @author johannes
 * The space around a simulated black hole, simulated in terms of thin spherical shells, each of constant refractive index
 */
public class SpaceAroundBlackHoleSphericalShells extends SurfaceOfVoxellatedRefractor
{
	private static final long serialVersionUID = -8867662603617645336L;
	
	/**
	 * the radius of the black hole's horizon
	 */
	private double horizonRadius;
	
	/**
	 * j = 1 corresponds to Kenyon's refractive-index profile;
	 * j = ??? corresponds to Jakub's refractive-index profile, in which the photon-sphere radius is (3/2) R
	 */
	private double jParameter;

	
	protected SetOfConcentricSpheres spheres;

	public SpaceAroundBlackHoleSphericalShells(
			double horizonRadius,
			double jParameter,
			Sphere surface,
			int numberOfSphericalShells,
			int maxSimulationSteps,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(null, null, maxSimulationSteps, transmissionCoefficient, shadowThrowing);
		
		SceneObjectPrimitiveIntersection surfaces = new SceneObjectPrimitiveIntersection(
				"Surfaces of black hole",	// description
				surface.getParent(),	// parent
				surface.getStudio()	// studio
			);
		// SceneObjectContainer surfaces = new SceneObjectContainer("Surfaces of black hole", surface.getParent(), surface.getStudio());
		surfaces.addPositiveSceneObjectPrimitive(surface);
		surfaces.addNegativeSceneObjectPrimitive(new Sphere(
				"Horizon",	// description
				surface.getCentre(),	// centre
				horizonRadius-MyMath.TINY,	// radius
				SurfaceColour.BLACK_MATT,	// surfaceProperty
				surfaces,	// parent
				surface.getStudio()	// studio
			));
		this.setSurface(surfaces);
		
		this.horizonRadius = horizonRadius;
		this.jParameter = jParameter;
		
		Voxellation[] voxellations = new Voxellation[1];
		double deltaR = (surface.getRadius() - horizonRadius) / (numberOfSphericalShells-1);
		spheres = new SetOfConcentricSpheres(
				surface.getCentre(),	// common centre of all spheres
				horizonRadius,	// deltaR,	// surface.getRadius() + MyMath.TINY,	// radius of sphere #0
				deltaR	// radius difference between neighbouring spheres
			);
		voxellations[0] = spheres;
		setVoxellations(voxellations);
	}

	public double getHorizonRadius() {
		return horizonRadius;
	}

	public void setHorizonRadius(double horizonRadius) {
		this.horizonRadius = horizonRadius;
	}

	public double getjParameter() {
		return jParameter;
	}

	public void setjParameter(double jParameter) {
		this.jParameter = jParameter;
	}

	/* (non-Javadoc)
	 * radial dependence of refractive index
	 * 	n(r) = (r + h)^3 / (r^2 (r - h)),
	 * where h = horizon radius.
	 * (This is the radial dependence of refractive index from Sicen Tao's e-mail dated 21/4/19, 
	 * 	n(r) = (4 r + L)^3 / (16 r^2 (4 r - L)),
	 * with the substitution L  = 4 h.)
	 * @see optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor#getRefractiveIndex(int[])
	 */
	@Override
	public double getRefractiveIndex1(int[] voxelIndices)
	{
		// calculate the radius of the spherical shell represented by the voxel the ray is currently in
		double r = spheres.getRadius1(voxelIndices[0]-0.5);
		// if the radius calculations fails, then the refractive-index calculation also fails
		if(r <= 0.0) return Double.NaN;
		
		double rPlusJH = r + jParameter*horizonRadius;
		double rMinusH = r - horizonRadius;
		if(rMinusH == 0.)
				return Double.NaN;
				// throw new RayTraceException("SpaceAroundBlackHole::getRefractiveIndex: division by zero in calculation of refractive index!");
		// System.out.println("voxel index="+voxelIndices[0]+", radius ="+r+", n="+rPlusH*rPlusH*rPlusH/(r*r*rMinusH));
		return rPlusJH*rPlusJH*rPlusJH/(r*r*rMinusH); 
	}


}
