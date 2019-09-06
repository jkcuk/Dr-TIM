package optics.raytrace.test;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.Cylinder;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.SurfaceOfVoxellatedMetric;
import optics.raytrace.voxellations.SetOfCoaxialInfiniteCylinderMantles;
import optics.raytrace.voxellations.Voxellation;


/**
 * Surface around a voxellated volume in which the voxels simulate a cloak by using layers 
 * (and sectors?) of radially (but not longitudinally) concentric  (i.e. coaxial) cylinders 
 * as metric interfaces. 
 * 
 * This is similar to what SurfacesOfSpecificVoxellatedAbsorbers/Refractors are to their 
 * superclasses, and so can be thought of as a SurfaceOfSpecificVoxellatedMetric. Tries to
 * follow the Luneburg lens examples closely.
 * 
 * This implementation is extremely naive, and conservative.
 * 
 * @author E Orife
 */

public class SurfaceOfVoxellatedCylindricalCloak extends SurfaceOfVoxellatedMetric
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7814668562589958817L;
	protected SetOfCoaxialInfiniteCylinderMantles cylinders;
	protected SectorsOfCylinder sectors;
	private double a,b;
	
	/**
	 * Creates a new surface property that marks a surface as the boundary surface defining a voxellated volume.
	 * @param surface
	 * @param numberOfCylindricalShells
	 * @param transmissionCoefficient transmission coefficient on entering and exiting 
	 *        volume.
	 */
	public SurfaceOfVoxellatedCylindricalCloak(
			 ParametrisedCylinder surface,
			int numberOfCylindricalShells,
			int numbSectors,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		this(surface, 4*numberOfCylindricalShells, numbSectors, 
			 surface.getRadius(), transmissionCoefficient, shadowThrowing);

//		// Instead of the code below 
//		super(null, surface, 4*numberOfCylindricalShells, transmissionCoefficient);
//
//		setA(surface.getRadius());setB(innermostCylinder-MyMath.TINY);
//		cylinders = new SetOfCoaxialCylinderMantles(
//				surface.getCentre(), // common centre of all cylinders
//				surface.getRadius()-MyMath.TINY,	// radius of cylinder #0
//				surface.getRadius() / numberOfCylindricalShells	// radius difference between neighbouring cylinders
//				);
//		setVoxellations(cylinders);
	}


	public SurfaceOfVoxellatedCylindricalCloak(
			ParametrisedCylinder surface,
			int numberOfCylindricalShells,
			int numbSectors,
			double innermostCylinder, // radius of cloaked cylinder at the centre
			double transmissionCoefficient,
			boolean shadowThrowing
			)
	{
		super(null, surface, 4*numberOfCylindricalShells, transmissionCoefficient, shadowThrowing);

		setA(surface.getRadius());setB(innermostCylinder-MyMath.TINY);
		cylinders = new SetOfCoaxialInfiniteCylinderMantles(
				surface.getStartPoint(),surface.getEndPoint(),
				b-MyMath.TINY,	// radius of cylinder #0
				(a-b) / numberOfCylindricalShells	// radius difference between neighbouring cylinders
				);
		sectors = new SectorsOfCylinder(surface, 
				                         numbSectors);
		
		// set the voxellations
		setVoxellations(new Voxellation[]{cylinders,sectors});
	}
	
	
	// override these methods to customise the behaviour of the ray inside the surface
	
	/**
	 * @param voxelIndices
	 * @return	the metric tensor for the voxel with these voxelIndices
	 * Uses metric calculated by Stephen Oxburgh.
	 * @throws Exception 
	 */
	@Override
	public double[] getMetricTensor(int[] voxelIndices) throws Exception
	{
//		** Code based on spherical cloak implementation **
//		
//		//calculate the radius of the cylindrical shell represented by the voxel the ray is currently in 
////	double r=Double.NaN, rRatioSq=Double.NaN, a, ab_RatioSq=Double.NaN;
////	r = cylinders.getRadius(voxelIndices[0]+0.5);
////	rRatioSq = r / (((Cylinder)surface).getRadius());
////	a = ((Cylinder) surface).getRadius();
////	ab_RatioSq = a/cylinders.getRadius(voxelIndices[0]);
////	rRatioSq*=rRatioSq;ab_RatioSq*=ab_RatioSq;
//	
//		//Stephen's idea : use an arithmetic sequence for spherical cloak. Does this work 
//		// for cylindrical cloaks?
//		// See Philbin & Leonhardt, pg.s ??? & ?
//		double r=cylinders.getRadius(voxelIndices[0]+0.5),
//			   R= 500,//double R= something:i? R=dr/dr'.
//		       //r'=r0+R*dr or r'=r0+i*dr 
//			   r_=cylinders.getSeparation()*R,
//			   // e=alpha^2, where alpha is the gradient, however leave as is for debugging
//			   e=(r_/r)*(r_/r)/R;// det e=R*(r_/r)*(r_/r)*1/R*1/R
//		//double[] MetricInterface.getDiagonalMetricTensor(double g11, double g22, double g33);
//
//		// double e_ij=1,g_ij=e_ij/ Math.sqrt(ex*ey*ez); double g(i,j)=e(i,j)/sqrt(det e);
//		// double e(i,j) = diag(ex,ey,ez); 
//		// diag(nx^2,ny^2,nz^2)= diag(ey*ez,ex*ez,ex*ey); // since n^2 = g
//		// return g(i,j)=diag(R*(r'/r)^2, 1/R, 1/R)/det e;
//		return scalarMult(MetricInterface.getDiagonalMetricTensor(R*(r_/r)*(r_/r), 1/R, 1/R), 
//				          1/e);
		
		double 
			   r=((SetOfCoaxialInfiniteCylinderMantles) surface).getRadius(voxelIndices[0]),rSq=r*r,
			   theta=60,/*voxelIndices[1]*/ //polar angle
			   x=r*Math.cos(theta),y=r*Math.cos(theta),
			   bxy=(b*x*y),
			   xx_1st_term=bxy*bxy/rSq*rSq*rSq,
			   xx_2nd_term=(1-(b/a)+(b*y*y/r*r*r))*(1-(b/a)+(b*y*y/r*r*r)),
			   yy_1stTerm=xx_1st_term,
			   yy_2stTerm=(1-(b/a)+(b*x*x/r*r*r))*(1-(b/a)+(b*x*x/r*r*r)),
		       g[];// =MetricInterface.getMetricTensorForRefractiveIndex(2);
		       g=MetricInterface.getMetricTensor(xx_1st_term+xx_2nd_term,/*g11*/ 
	                                             (bxy/(a*rSq*rSq))*(2*b*r-a*(b+2*r))/*g12*/ 
	                                              ,0,/* g13 */ 
	                                              yy_1stTerm+yy_2stTerm,/*g22*/ 
	                                              0,/*g23*/ 1 /*g33*/); 
		       return g;
	}
	
	public double[] scalarMult(double[] array, double a)
	{
		double copy[]=array.clone();
//		System.out.println(Arrays.toString(array)+", and for comparison:"
//                         +Arrays.toString(copy));
		for(int i=copy.length-1;i>-1;i--){// or i==0
			copy[i]*= a;
		} 
			
//		System.out.println(Arrays.toString(array)+", and for comparison:"
//                         +Arrays.toString(copy));
		return copy;
	
	}
	
	public double[] getMetricTensor(double rRatio, double a_Over_b,char x_OR_y)
	{
		if((x_OR_y=='x')||(x_OR_y=='X')) 
			return MetricInterface.getDiagonalMetricTensor(rRatio,a_Over_b,rRatio);
		else if((x_OR_y=='y')||(x_OR_y=='Y'))  
			return MetricInterface.getDiagonalMetricTensor(a_Over_b,rRatio,rRatio);
		else if((x_OR_y=='z')||(x_OR_y=='Z'))  
			return MetricInterface.getDiagonalMetricTensor(rRatio,rRatio,a_Over_b);
		else throw new Error("Error!");
	}
	
	public double rRatio(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	public double ab_RatioSq(int[] voxelIndices)
	{
		return Double.NaN;
	}
	
	public void setVoxellations(SetOfCoaxialInfiniteCylinderMantles cylindersSet, SectorsOfCylinder 
			sect)
	{
		setVoxellations(new Voxellation[]{cylindersSet,sect});
	}

	/**
	 * @author e orife
	 * 
	 * The sectors of a cylinder.
	 * 
	 * Modelled on the SextantsOfCube example.
	 * 
	 * The sectors combine with a cylinder to make the "voxels".
	 * 
	 * Defined by the centre of the cylinder.
	 * 
	 * This class is not used outside the cylindrical cloak (for now anyway).
	 */ 
	class SectorsOfCylinder extends Voxellation
	{	

		private static final long serialVersionUID = 726449539802288547L;
		/**
		 * Centre
		 */
		protected Vector3D centre,
		/**
		 * Normals to the centre of cylinder
		 */
		                   normals[],diff;
		protected int numSectors;
		protected double r;
		private Cylinder cylinder;// A copy of the cylinder's dimensions for our reference
		/** Want to ask whether or not use local (within cylinder) or global coordinate 
		 *  system to calculate the vectors. Note that care must be taken to ensure the 
		 *  point is within cylinder.
		 */
		protected boolean localNotGlobal;	
		
		// constructor
		
		/**
		 * @param cylinder
		 * @param numbSectors 
		 */
		public SectorsOfCylinder(ParametrisedCylinder cylinder, int numbSectors)
		{
			super();
			
			r=cylinder.getRadius();
			setCentre(Vector3D.mean(cylinder.getEndPoint(), cylinder.getStartPoint()));
			setNumSectors(numbSectors);
			this.cylinder=new Cylinder(null,
					                   cylinder.getStartPoint(),
					                   cylinder.getEndPoint(),r,
					                   null,null,null);
			diff=Vector3D.difference(cylinder.getEndPoint(),cylinder.getStartPoint());
			//diff is b-a, NOT a-b; *remember*, these are vectors!
		}

		// setters & getters
		
		public Vector3D getCentre() {
			return centre;
		}

		public void setCentre(Vector3D centre) {
			this.centre = centre;
		}

		/**
		 * @return the no. of sectors
		 */
		public int getNumSectors() {
			return numSectors;
		}

		/**
		 * @param vectorsSectors the no. of sectors to set
		 */
		public void setNumSectors(int vectorsSectors) {
			this.numSectors = vectorsSectors;
		}

		@Override
		public int getVoxelIndex(Vector3D position) {
			// TODO Auto-generated method stub. Remove when done
			// Pick an imaginary line from axis to surface; any line
			// Record this line. Does it have any special criteria?
			return (int) Math.floor(2*Math.PI/getAngleWith(position));
		}
		
		public double getAngle(int voxelIndex) {
			
			return (voxelIndex+0.5)*2*Math.PI/numSectors;
		}

		@Override
		public SceneObject getSurfaceOfVoxel(int i)
				throws IndexOutOfBoundsException {
			// TODO Auto-generated method stub
			return null;
		}
		
		
		
		/** We need somewhere to start measuring angles from. This method devises a means to
		 * pick such a point, on the surface of the cylinder, at the end of a radial line.
		 * This line segment is represented by a vector object.
		 * 
		 * If cylinder parallel to z-axis: We take a step in the y-direction, although can 
		 * likewise for x (as both are normal to the vector pointing toward the centre), but
		 * just chose y for choosing sake. 
		 * 
		 * Otherwise, @return the resulting normal vector between the cylinder axis vector
		 * and the z-axis as the zero angle line.
		 */
		public Vector3D getZeroAngleVector() {

			return getNormals()[0];
		}
		
		/** Method to measure angle the zero-angle normal makes with given vector.
		 * 
		 * There are two ways to do this: in a local or global coordinate system.
		 * 
		 * In a local coordinate frame, the origin is determined by the location of 
		 * startPoint. This simply means that the cylindrical axis is parallel to the z -
		 * direction in this system.
		 * 
		 * Can then take the atan2 function of the x- and y- projections of the difference 
		 * between any general position in the cylinder and the centre.
		 * 
		 * In a global coordinate reference, the origin is independent of startPoint, and in
		 * general the cylinder's axis is tilted toward the vertical (z-axis). The normals 
		 * are obtained by taking the cross product of the z unit-vector and the cylinder's
		 * axis vector - call this z' - (first normal) and then taking vector product of this
		 * result with z' (second normal), i.e. (z x z') x z'. The final step of orthogonal
		 * projections is the same.
		 * 
		 * @param :positionVector the position vector relative to start point. 
		 * 
		 */
		//For some position inside the cylinder, get the angle between the vector and the 
		//zero angle line. This uses the orthogonal projections of x and y.
		public double getAngleWith(Vector3D positionVector) {
		
			localNotGlobal=false;//By default set to false, for now.
			
//						//Unnecessary code? Comment out. 
//						// In a naive OO-approach, would write:
//						Plane plane=new Plane(
//								"Polar plane of cylinder",
//								centre,
//								normal, 
//								null, // set all to null - plane is a pure geometric object without any
//								null, // SurfaceProperties.
//								null);
////	   Then take the angle between the position vector and the plane.
////	   However the plane isn't explicitly used for anything here and this approach isn't 
////	   implemented here, so it is superfluous. 

			//Project position vector onto the plane of the normal
			@SuppressWarnings("unused")
			Vector3D pointOnSurface=/*Vector3D.sum(centre,
                                                   Vector3D.scalarTimesVector3D(r, 
                                                                                Vector3D.Y));
			                        *//*Vector3D.sum(centre,
					                             Vector3D.scalarTimesVector3D(r, 
					                            		                      Vector3D.Y));
			                       */Vector3D.scalarTimesVector3D(r,getZeroAngleVector()
					                                                .getNormalised()),
					  startPoint=cylinder.getStartPoint();
//			//Check: are these two vectors normal?
//			System.out.println("Are vectors orthogonal?:"+Vector3D.scalarProduct(
//					           startPoint,positionVector)+","+checkOrtho(startPoint, 
//							   positionVector)); 

			Vector3D relPos=Vector3D.difference(startPoint, //pointOnSurface
					                            positionVector.getNormalised());

			// Warning: actually X and Y (perhaps) ought to be generalised into normals.
			return Math.atan2(Vector3D.scalarProduct(relPos, getNormals()[0]), 
						      Vector3D.scalarProduct(relPos, getNormals()[1]));
		}
		
		//Returns: (z x z') and (z x z') x z'
		public Vector3D[] getNormals() {

			Vector3D normal1;
			// Mistake trap: what if  they are (anti-) parallel? Remember we said return y!
			boolean antiPara=Vector3D.scalarProduct(diff,Vector3D.Z)>=-(1-MyMath.TINY),
					para=Vector3D.scalarProduct(diff,Vector3D.Z)<=1-MyMath.TINY;
			if(para||antiPara) normal1= Vector3D.Y;
			//Otherwise...
			else normal1= Vector3D.crossProduct(diff,Vector3D.Z).getNormalised();
			return new Vector3D[]{normal1,Vector3D.crossProduct(diff, normal1)};
		}
		
		
	}
	
	//back to the cylindrical cloak!
	public void setA(double a){this.a=a;}
	public void setB(double b){this.b=b;}
	
	//Check: are these two vectors normal?
	public static boolean checkOrtho(Vector3D vec1, Vector3D vec2) {
		return Vector3D.scalarProduct(vec1, vec2)==0 ||
				!(Vector3D.scalarProduct(vec1, vec2)>/* Or "<=" instead of 
					   "!>" */ MyMath.TINY);
	}
	
}
