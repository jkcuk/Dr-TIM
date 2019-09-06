package optics.raytrace.test;

import math.MyMath;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor;
import optics.raytrace.voxellations.SetOfCoaxialInfiniteCylinderMantles;
import optics.raytrace.voxellations.Voxellation;

/**
 * @author johannes, ejovboke
 * A cylindrical Luneburg lens, simulated in terms of thin spherical shells, each of constant refractive index
 */
public class SurfaceOfLayeredCylindricalLuneburgLens extends SurfaceOfVoxellatedRefractor
{
	private static final long serialVersionUID = 1256742919830076541L;

	protected SetOfCoaxialInfiniteCylinderMantles cylinders;

	public SurfaceOfLayeredCylindricalLuneburgLens(
			ParametrisedCylinder surface,
			int numberOfShells,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(null, surface, 4*numberOfShells, transmissionCoefficient, shadowThrowing);
		
		Voxellation[] voxellations = new Voxellation[1];
		cylinders = new SetOfCoaxialInfiniteCylinderMantles(
				surface.getStartPoint(),
				surface.getEndPoint(),
				surface.getRadius() + MyMath.TINY,	// radius of sphere #0
				surface.getRadius() / numberOfShells	// radius difference between neighbouring cylinders
			);
		voxellations[0] = cylinders;
		setVoxellations(voxellations);
	}

	/* (non-Javadoc)
	 * radial dependence of refractive index from M. Sarbort and T. Tyc,
	 * "Spherical media and geodesic lenses in geometrical optics",
	 * Journal of Optics�14, 075705 (2012)
	 * and from http://en.wikipedia.org/wiki/Luneburg_lens
	 * @see optics.raytrace.surfaces.SurfaceOfVoxellatedRefractor#getRefractiveIndex(int[])
	 */
	@Override
	public double getRefractiveIndex(int[] voxelIndices)
	throws Exception
	{
		// calculate the radius of the spherical shell represented by the voxel the ray is currently in
		double r = cylinders.getRadius(voxelIndices[0]+0.5);
		double rOverR = r / ((ParametrisedCylinder)surface).getRadius();
		return Math.sqrt(2-rOverR*rOverR); 
	}


}
