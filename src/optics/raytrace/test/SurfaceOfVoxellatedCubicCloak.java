package optics.raytrace.test;

import optics.raytrace.sceneObjects.ParametrisedCube;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.SurfaceOfVoxellatedMetric;
import optics.raytrace.voxellations.SetOfConcentricCubes;
import optics.raytrace.voxellations.SextantsOfCube;
import optics.raytrace.voxellations.Voxellation;

/**
 * Surface around a voxellated volume in which the voxels simulate a cloak by using diagonal
 * planes of cuboids as metric interfaces. 
 * 
 * This is similar to what SurfacesOfSpecificVoxellatedAbsorbers/Refractors are to their 
 * superclasses, and so can be thought of as a SurfaceOfSpecificVoxellatedMetric. Tries to
 * follow the Luneburg lens examples closely.
 * 
 * This implementation is extremely naive, and conservative.
 * 
 * @author E Orife
 */

public class SurfaceOfVoxellatedCubicCloak extends SurfaceOfVoxellatedMetric
{
	private static final long serialVersionUID = 7855970733784308890L;
	private ParametrisedCube cube;
	private double a, b;
	protected SetOfConcentricCubes cubes;
	protected SextantsOfCube sextants;



	public SurfaceOfVoxellatedCubicCloak(
			ParametrisedCube surface,
			int numberOfCuboidShells,
			double b,	// half-sidelength of cloaked cube at the centre
			double transmissionCoefficient,
			boolean shadowThrowing
			)
	{
		super(null, surface, 20*numberOfCuboidShells, transmissionCoefficient, shadowThrowing);

		// take note of the cube and set the parameter a
		setCube(surface);
		setB(b);
		
		// create the voxellations
		cubes = new SetOfConcentricCubes(
				surface.getCentre(), // common centre of all cubes
				b,	// radius of cube #0
				(a-b) / numberOfCuboidShells	// radius difference between neighbouring cubes
				);
		
		sextants = new SextantsOfCube(surface.getCentre());
		
		// set the voxellations
		setVoxellations(cubes,sextants);
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the metric tensor for the voxel with these voxelIndices
	 */
	@Override
	public double[] getMetricTensor(int[] voxelIndices)
	{
//		 //calculate the radius of the cuboid shell represented by the voxel the ray is currently in
//		double r = cubes.index2Radius(voxelIndices[0]+0.5);
//		double rOverR = r / (((ParametrisedCuboid)surface).getWidth()/2);
//		return Math.sqrt(2-rOverR*rOverR); 
//                       OR		
//		return MetricInterface.getMetricTensorForRefractiveIndex(Math.sqrt(2-rOverR*rOverR)); 
		double
			r = cubes.index2Radius(voxelIndices[0]+0.5),
			rPrime = b+(a-b)*(r/a),
			rRatio=r/rPrime, 
			rRatioSq = (rRatio * rRatio),
			ab_RatioSq = (a/b)*(a/b);
		//What sextant are we in?
		if(SextantsOfCube.getSextant(voxelIndices[1]).getAxis() == SextantsOfCube.AxisType.X)
		// if(voxelIndices[1]==0 || voxelIndices[1]==1)
		{
			// it's a sextant off to the positive or negative x direction
			return getMetricTensor(rRatioSq, ab_RatioSq, 'x');
		}
		else if(SextantsOfCube.getSextant(voxelIndices[1]).getAxis() == SextantsOfCube.AxisType.Y)
		// else if(voxelIndices[1]==2 || voxelIndices[1]==3)
		{
			// it's a sextant off to the positive or negative y direction
			return getMetricTensor(rRatioSq, ab_RatioSq, 'y');
		}
		else
		{
			// it must be a sextant off to the positive or negative z direction
			return getMetricTensor(rRatioSq, ab_RatioSq, 'z');
		}
		
//		boolean xOrYTrue=(voxelIndices[1]==0||voxelIndices[1]==1)||
//				         (voxelIndices[1]==2||voxelIndices[1]==3),
//		        isZ=(voxelIndices[1]==4||voxelIndices[1]==5); 
//		if(isZ==false) 
//			return (xOrYTrue)? getMetricTensor(rRatioSq,ab_RatioSq,'x'):
//				               getMetricTensor(rRatioSq,ab_RatioSq,'y');
//		else return getMetricTensor(rRatioSq,ab_RatioSq,'z');
	}
	
	public double[] getMetricTensor(double rRatioSquared, double a_Over_b_squared,char direction)
	{
		// if(rRatioSquared < 0.1) System.out.println("metric diagonal elements: "+a_Over_b_squared+", "+rRatioSquared);
		
		if((direction=='x')||(direction=='X')) 
			return MetricInterface.getDiagonalMetricTensor(a_Over_b_squared,rRatioSquared,rRatioSquared);
		else if((direction=='y')||(direction=='Y'))  
			return MetricInterface.getDiagonalMetricTensor(rRatioSquared,a_Over_b_squared,rRatioSquared);
		else
			return MetricInterface.getDiagonalMetricTensor(rRatioSquared,rRatioSquared,a_Over_b_squared);
	}
	
	public ParametrisedCube getCube() {
		return cube;
	}


	public void setCube(ParametrisedCube cube)
	{
		this.cube = cube;
		
		setA(cube.getSidelength()/2);
	}


	public double getA() {
		return a;
	}


	public void setA(double a) {
		this.a = a;
	}


	public double getB() {
		return b;
	}


	public void setB(double b) {
		this.b = b;
	}


	public double rRatio(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	public double ab_RatioSq(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	public void setVoxellations(SetOfConcentricCubes cubesSet, SextantsOfCube sext)
	{
		setVoxellations(new Voxellation[]{cubesSet,sext});
	}
	
	
}