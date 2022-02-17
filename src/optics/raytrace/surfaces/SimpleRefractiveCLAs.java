package optics.raytrace.surfaces;

import math.Geometry;
import math.MyMath;
import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.Parallelepiped2;
import optics.raytrace.sceneObjects.RefractiveFrontHalfLensSurfaces;
import optics.raytrace.sceneObjects.RefractiveLensSurfaces;


/**
 * A rectangular array of lenslets, which all have the same focal length.
 * 
 * The array is surrounded by a bounding box comprising a parallelepiped.  This SceneObject is that bounding box.
 * It is the responsibility of the user to ensure that this bounding box surrounds the relevant part of the array.
 * 
 * The principal points of the lenses can be different from the clear-aperture centres of the lenses.
 * This allows things like Gabor superlenses to be realised.
 * 
 * Tracing through the lenses is handled by the bounding box's SurfaceProperty.
 * 
 * @author Maik and Johannes
 */
public class SimpleRefractiveCLAs extends Parallelepiped2 
{

	private static final long serialVersionUID = -5613189023782562206L;

	private SurfaceOfSimpleRefractiveCLAs surface;
	
	private boolean separatedArrays = false;
	
	/**
	 * @param description
	 * @param boundingBoxCentre
	 * @param boundingBoxSpanVector1
	 * @param boundingBoxSpanVector2
	 * @param boundingBoxSpanVector3
	 * @param normalisedOpticalAxisDirection
	 * @param clearApertureArrayBasisVector1
	 * @param clearApertureArrayBasisVector2
	 * @param principalPointArrayBasisVector1
	 * @param principalPointArrayBasisVector2
	 * @param lens00ClearApertureCentre
	 * @param lens00PrincipalPoint
	 * @param focalLength
	 * @param refractiveIndex
	 * @param lensletCentreThickness
	 * @param maxSteps
	 * @param surfaceTransmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public SimpleRefractiveCLAs(
			String description,
			Vector3D boundingBoxCentre, 
			Vector3D boundingBoxSpanVector1,
			Vector3D boundingBoxSpanVector2,
			Vector3D boundingBoxSpanVector3,
			Vector3D normalisedOpticalAxisDirection,
			Vector3D clearApertureArrayBasisVector1, 
			Vector3D clearApertureArrayBasisVector2,
			Vector3D principalPointArray1BasisVector1,
			Vector3D principalPointArray1BasisVector2,
			Vector3D principalPointArray2BasisVector1,
			Vector3D principalPointArray2BasisVector2,
			Vector3D lens00ClearApertureCentreArray1,
			Vector3D lens00ClearApertureCentreArray2,		
			Vector3D lens00PrincipalPointArray1,
			Vector3D lens00PrincipalPointArray2,
			double focalLengthArray1,
			double focalLengthArray2,			
			double refractiveIndex,
			double lensletCentreThicknessArray1,
			double lensletCentreThicknessArray2,
			int maxSteps, 
			double surfaceTransmissionCoefficient, 
			boolean shadowThrowing,
			boolean separatedArrays,
			SceneObject parent, 
			Studio studio
		)
	{
		// first create the bounding box...
		super(
				"Bounding box",	// description
				boundingBoxCentre,	// centre
				boundingBoxSpanVector1,	// u
				boundingBoxSpanVector2,	// v
				boundingBoxSpanVector3,	// w
				null,	// surfaceProperty -- for now; will be set in a second
				parent,
				studio
			);
		// ... then set its surface property to be a suitable refractive lenslet array
		surface = new SurfaceOfSimpleRefractiveCLAs(
				GaborSupererRefractiveCLAs.correctedNormal(normalisedOpticalAxisDirection, lens00PrincipalPointArray1, lens00PrincipalPointArray2, focalLengthArray1),
				focalLengthArray1,
				focalLengthArray2,
				clearApertureArrayBasisVector1,
				clearApertureArrayBasisVector2,
				principalPointArray1BasisVector1,
				principalPointArray1BasisVector2,
				principalPointArray2BasisVector1,
				principalPointArray2BasisVector2,
				lens00ClearApertureCentreArray1,
				lens00ClearApertureCentreArray2,
				lens00PrincipalPointArray1,
				lens00PrincipalPointArray2,
				this,	// bounding box
				refractiveIndex,
				lensletCentreThicknessArray1,
				lensletCentreThicknessArray2,// centreThickness,
				surfaceTransmissionCoefficient,
				shadowThrowing,
				maxSteps
			);
		if(separatedArrays) {
			surface.setSeparatedArrays(true);
		}else{
			surface.setSeparatedArrays(false);
		}
		setSurfaceProperty(surface);
		
	}

		public SimpleRefractiveCLAs(
				String description,
				Vector3D boundingBoxCentre, 
				Vector3D boundingBoxSpanVector1,
				Vector3D boundingBoxSpanVector2,
				Vector3D normalisedOpticalAxisDirection,
				Vector3D clearApertureArrayBasisVector1, 
				Vector3D clearApertureArrayBasisVector2,
				Vector3D principalPointArray1BasisVector1,
				Vector3D principalPointArray1BasisVector2,
				Vector3D principalPointArray2BasisVector1,
				Vector3D principalPointArray2BasisVector2,
				Vector3D lens00ClearApertureCentreArray1, 
				Vector3D lens00ClearApertureCentreArray2,
				Vector3D lens00PrincipalPointArray1,
				Vector3D lens00PrincipalPointArray2,
				double focalLengthArray1,
				double focalLengthArray2,			
				double refractiveIndex,
				double lensletCentreThicknessArray1,
				double lensletCentreThicknessArray2,
				int maxSteps, 
				double surfaceTransmissionCoefficient, 
				boolean shadowThrowing,
				boolean separatedArrays,
				SceneObject parent, 
				Studio studio
			)
		{
			// first create the bounding box...
			super(
					"Bounding box",	// description
					boundingBoxCentre,	// centre
					boundingBoxSpanVector1,	// u
					boundingBoxSpanVector2,	// v
					getBoundingBoxThicknessSpanVector(lens00PrincipalPointArray1, principalPointArray1BasisVector1, principalPointArray1BasisVector2, lensletCentreThicknessArray1, focalLengthArray1, lens00ClearApertureCentreArray1,//Array 1 params
							 lens00PrincipalPointArray2, principalPointArray2BasisVector1, principalPointArray2BasisVector2, lensletCentreThicknessArray2, focalLengthArray2, lens00ClearApertureCentreArray2,//Array 2 params
							 clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, boundingBoxSpanVector1, boundingBoxSpanVector2, refractiveIndex, separatedArrays),//Common Params,	// w
					null,	// surfaceProperty -- for now; will be set in a second
					parent,
					studio

				);
//			System.out.println(getBoundingBoxThicknessSpanVector(lens00PrincipalPointArray1, principalPointArray1BasisVector1, principalPointArray1BasisVector2, lensletCentreThicknessArray1, focalLengthArray1, lens00ClearApertureCentreArray1,//Array 1 params
//					 lens00PrincipalPointArray2, principalPointArray2BasisVector1, principalPointArray2BasisVector2, lensletCentreThicknessArray2, focalLengthArray2, lens00ClearApertureCentreArray2,//Array 2 params
//					 clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, boundingBoxSpanVector1, boundingBoxSpanVector2, refractiveIndex, separatedArrays));
			
		// ... then set its surface property to be a suitable refractive lenslet array
		surface = new SurfaceOfSimpleRefractiveCLAs(
				GaborSupererRefractiveCLAs.correctedNormal(normalisedOpticalAxisDirection, lens00PrincipalPointArray1, lens00PrincipalPointArray2, focalLengthArray1),
				focalLengthArray1,
				focalLengthArray2,
				clearApertureArrayBasisVector1,
				clearApertureArrayBasisVector2,
				principalPointArray1BasisVector1,
				principalPointArray1BasisVector2,
				principalPointArray2BasisVector1,
				principalPointArray2BasisVector2,
				lens00ClearApertureCentreArray1,
				lens00ClearApertureCentreArray2,
				lens00PrincipalPointArray1,
				lens00PrincipalPointArray2,
				this,	// bounding box
				refractiveIndex,
				lensletCentreThicknessArray1,
				lensletCentreThicknessArray2,// centreThickness,
				surfaceTransmissionCoefficient,
				shadowThrowing,
				maxSteps
				
			);
		if(separatedArrays) {
			surface.setSeparatedArrays(true);
		}else{
			surface.setSeparatedArrays(false);
		}
		setSurfaceProperty(surface);
	}
	
	/**
	 * @param original
	 */
	public SimpleRefractiveCLAs(SimpleRefractiveCLAs original)
	{
		this(
				original.getDescription(),
				original.getBoundingBoxCentre(), 
				original.getBoundingBoxSpanVector1(),
				original.getBoundingBoxSpanVector2(),
				original.getBoundingBoxSpanVector3(),
				original.getNormalisedOpticalAxisDirection(),
				original.getClearApertureArrayBasisVector1(), 
				original.getClearApertureArrayBasisVector2(),
				original.getPrincipalPointArray1BasisVector1(),
				original.getPrincipalPointArray1BasisVector2(),
				original.getPrincipalPointArray2BasisVector1(),
				original.getPrincipalPointArray2BasisVector2(),
				original.getLens00ClearApertureCentreArray1(), 
				original.getLens00ClearApertureCentreArray2(),
				original.getLens00PrincipalPointArray1(),
				original.getLens00PrincipalPointArray2(),
				original.getFocalLengthArray1(), 
				original.getFocalLengthArray2(), 
				original.getRefractiveIndex(),
				original.getLensletCentreThicknessArray1(),
				original.getLensletCentreThicknessArray2(),
				original.getMaxSteps(), 
				original.getSurfaceTransmissionCoefficient(), 
				original.isShadowThrowing(),
				original.isSeparatedArrays(),
				original.getParent(), 
				original.getStudio()
			);	
	}
	


	public Vector3D getBoundingBoxCentre() {
		return super.getCentre();
	}

	public void setBoundingBoxCentre(Vector3D boundingBoxCentre) {
		super.setCentre(boundingBoxCentre);
	}

	public Vector3D getBoundingBoxSpanVector1() {
		return super.getU();
	}

	public void setBoundingBoxSpanVector1(Vector3D boundingBoxSpanVector1) {
		super.setU(boundingBoxSpanVector1);
	}

	public Vector3D getBoundingBoxSpanVector2() {
		return super.getV();
	}

	public void setBoundingBoxSpanVector2(Vector3D boundingBoxSpanVector2) {
		super.setV(boundingBoxSpanVector2);
	}

	public Vector3D getBoundingBoxSpanVector3() {
		return super.getW();
	}

	public void setBoundingBoxSpanVector3(Vector3D boundingBoxSpanVector3) {
		super.setW(boundingBoxSpanVector3);
	}

	
	
	public Vector3D getNormalisedOpticalAxisDirection() {
		return surface.getNormalisedOpticalAxisDirection();
	}

	public void setNormalisedOpticalAxisDirection(Vector3D opticalAxisDirection) {
		surface.setNormalisedOpticalAxisDirection(opticalAxisDirection);
	}

	public Vector3D getClearApertureArrayBasisVector1() {
		return surface.getClearApertureArrayBasisVector1();
	}

	public void setClearApertureArrayBasisVector1(Vector3D clearApertureArrayBasisVector1) {
		surface.setClearApertureArrayBasisVector1(clearApertureArrayBasisVector1);
	}

	public Vector3D getClearApertureArrayBasisVector2() {
		return surface.getClearApertureArrayBasisVector2();
	}

	public void setClearApertureArrayBasisVector2(Vector3D clearApertureArrayBasisVector2) {
		surface.setClearApertureArrayBasisVector2(clearApertureArrayBasisVector2);
	}

	public Vector3D getPrincipalPointArray1BasisVector1() {
		return surface.getPrincipalPointArray1BasisVector1();
	}

	public void setPrincipalPointArray1BasisVector1(Vector3D principalPointArray1BasisVector1) {
		surface.setPrincipalPointArray1BasisVector1(principalPointArray1BasisVector1);
	}

	public Vector3D getPrincipalPointArray1BasisVector2() {
		return surface.getPrincipalPointArray1BasisVector2();
	}

	public void setPrincipalPointArray1BasisVector2(Vector3D principalPointArray1BasisVector2) {
		surface.setPrincipalPointArray1BasisVector2(principalPointArray1BasisVector2);
	}
	
	public Vector3D getPrincipalPointArray2BasisVector1() {
		return surface.getPrincipalPointArray2BasisVector1();
	}

	public void setPrincipalPointArray2BasisVector1(Vector3D principalPointArray2BasisVector1) {
		surface.setPrincipalPointArray2BasisVector1(principalPointArray2BasisVector1);
	}

	public Vector3D getPrincipalPointArray2BasisVector2() {
		return surface.getPrincipalPointArray2BasisVector2();
	}

	public void setPrincipalPointArray2BasisVector2(Vector3D principalPointArray2BasisVector2) {
		surface.setPrincipalPointArray2BasisVector2(principalPointArray2BasisVector2);
	}

	public Vector3D getLens00ClearApertureCentreArray1() {
		return surface.getLens00ClearApertureCentreArray1();
	}

	public void setLens00ClearApertureCentreArray1(Vector3D lens00ClearApertureCentreArray1) {
		surface.setLens00ClearApertureCentreArray1(lens00ClearApertureCentreArray1);
	}
	
	public Vector3D getLens00ClearApertureCentreArray2() {
		return surface.getLens00ClearApertureCentreArray2();
	}

	public void setLens00ClearApertureCentreArray2(Vector3D lens00ClearApertureCentreArray2) {
		surface.setLens00ClearApertureCentreArray2(lens00ClearApertureCentreArray2);
	}

	public Vector3D getLens00PrincipalPointArray1() {
		return surface.getLens00PrincipalPointArray1();
	}

	public void setLens00PrincipalPointArray1(Vector3D lens00PrincipalPointArray1) {
		surface.setLens00PrincipalPointArray1(lens00PrincipalPointArray1);
	}
	
	public Vector3D getLens00PrincipalPointArray2() {
		return surface.getLens00PrincipalPointArray2();
	}

	public void setLens00PrincipalPointArray2(Vector3D lens00PrincipalPointArray2) {
		surface.setLens00PrincipalPointArray2(lens00PrincipalPointArray2);
	}

	public double getFocalLengthArray1() {
		return surface.getFocalLengthArray1();
	}

	public void setFocalLengthArray1(double focalLengthArray1) {
		surface.setFocalLengthArray1(focalLengthArray1);
	}
	
	public double getFocalLengthArray2() {
		return surface.getFocalLengthArray2();
	}

	public void setFocalLengthArray2(double focalLengthArray2) {
		surface.setFocalLengthArray2(focalLengthArray2);
	}

	public double getRefractiveIndex() {
		return surface.getRefractiveIndex();
	}

	public void setRefractiveIndex(double refractiveIndex) {
		surface.setRefractiveIndex(refractiveIndex);
	}

	public double getLensletCentreThicknessArray1() {
		return surface.getCentreThicknessArray1();
	}

	public void setLensletCentreThicknessArray1(double lensletCentreThicknessArray1) {
		surface.setCentreThicknessArray1(lensletCentreThicknessArray1);
	}
	
	public double getLensletCentreThicknessArray2() {
		return surface.getCentreThicknessArray2();
	}

	public void setLensletCentreThicknessArray2(double lensletCentreThicknessArray2) {
		surface.setCentreThicknessArray2(lensletCentreThicknessArray2);
	}

	public int getMaxSteps() {
		return surface.getMaxSteps();
	}

	public void setMaxSteps(int maxSteps) {
		surface.setMaxSteps(maxSteps);
	}

	public double getSurfaceTransmissionCoefficient() {
		return surface.getSurfaceTransmissionCoefficient();
	}

	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		surface.setSurfaceTransmissionCoefficient(surfaceTransmissionCoefficient);
	}

	public boolean isShadowThrowing() {
		return surface.isShadowThrowing();
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		surface.setShadowThrowing(shadowThrowing);
	}
	
	public boolean isSeparatedArrays() {
		return separatedArrays;
	}

	public void setSeparatedArrays(boolean separatedArrays) {
		this.separatedArrays = separatedArrays;
	}
	
	
	/**
	 * TODO generalise... current limitations are:
	 * Same direction for both corresponding basis vectors i.e clearApertureArrayBasisVector1 is the same direction as boundingBoxSpanVector1.
	 * Same centre position for box as voxells. Not sure when they shouldn't/wouldn't be.
	 * It returns the thickness as if all the voxells are filled fully i.e bounding box span vector is assumes to just fill last voxell. 
	 */
	
	
	/**
	 * calculates all the possible combinations of voxells and stores them in an array such that the maximum needed thickness can then be calculated.
	 * 
	 * @param lens00PrincipalPointArray1
	 * @param principalPointArray1BasisVector1
	 * @param principalPointArray1BasisVector2
	 * @param lens00PrincipalPointArray2
	 * @param principalPointArray2BasisVector1
	 * @param principalPointArray2BasisVector2
	 * @param lens00ClearApertureCentre
	 * @param clearApertureArrayBasisVector1
	 * @param clearApertureArrayBasisVector2
	 * @param boundingBoxSpanVector1
	 * @param boundingBoxSpanVector2
	 * @return
	 */
	public static Vector3D getBoundingBoxThicknessSpanVector(
			Vector3D lens00PrincipalPointArray1, Vector3D principalPointArray1BasisVector1, Vector3D principalPointArray1BasisVector2, double centreThicknessArray1,double focalLengthArray1, Vector3D lens00ClearApertureCentreArray1, //Array 1 params
			Vector3D lens00PrincipalPointArray2, Vector3D principalPointArray2BasisVector1, Vector3D principalPointArray2BasisVector2, double centreThicknessArray2,double focalLengthArray2, Vector3D lens00ClearApertureCentreArray2, //Array 2 params
			Vector3D clearApertureArrayBasisVector1, Vector3D clearApertureArrayBasisVector2, Vector3D boundingBoxSpanVector1, Vector3D boundingBoxSpanVector2, double refractiveIndex, boolean separatedArrays)//Common Params
		{ 
		Vector3D boundingBoxThicknessSpanVector;
		double totalThickness;
		if(separatedArrays) {
			double maxThicknessArray1 = -10000;
			double maxThicknessArray2 = -10000;
			double voxellDimension1 = clearApertureArrayBasisVector1.getLength();
			double voxellDimension2 = clearApertureArrayBasisVector2.getLength();
			double boxDimension1 = boundingBoxSpanVector1.getLength();
			double boxDimension2 = boundingBoxSpanVector2.getLength();
			int numberOfVoxells = (2*(int)Math.floor((boxDimension1+0.5*voxellDimension1)) +1)*(2*(int)Math.floor((boxDimension2+0.5*voxellDimension2))+ 1);
			int[][] arrayOfAllVoxells = new int [numberOfVoxells][2]; //-1 and 1 as start on 0th entry
			double[] thicknessesArray1 = new double [numberOfVoxells]; 
			double[] thicknessesArray2 = new double [numberOfVoxells];
			for(int p=0;p<=numberOfVoxells-1;p++) {
				for(int i=-(int) Math.floor((boxDimension1+0.5*voxellDimension1) / voxellDimension1);i<=(int) Math.floor((boxDimension1+0.5*voxellDimension1) / voxellDimension1);i++) {
					for(int j=-(int) Math.floor((boxDimension2+0.5*voxellDimension2) / voxellDimension2);j<=(int) Math.floor((boxDimension2+0.5*voxellDimension2) / voxellDimension2);j++) {
						arrayOfAllVoxells[p][0] = i;
						arrayOfAllVoxells[p][1] = j;
						thicknessesArray1[p] = calculateMinAndMaxThickness(lens00PrincipalPointArray1, lens00ClearApertureCentreArray1, 
								principalPointArray1BasisVector1, principalPointArray1BasisVector2, clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, centreThicknessArray1,focalLengthArray1, refractiveIndex, arrayOfAllVoxells[p])[1];
						
						thicknessesArray2[p] = calculateMinAndMaxThickness(lens00PrincipalPointArray2, lens00ClearApertureCentreArray2, 
								principalPointArray2BasisVector1, principalPointArray2BasisVector2, clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, centreThicknessArray2,focalLengthArray2, refractiveIndex, arrayOfAllVoxells[p])[1];;
								//System.out.println(thicknessesArray2[p]);
					}
				}
			System.out.println("arrayOfAllVoxells is" + arrayOfAllVoxells);
			}
			for( int k=0;k<=numberOfVoxells-1;k++) {
				if (thicknessesArray1[k]>maxThicknessArray1) {
					maxThicknessArray1 = thicknessesArray1[k];
				}
				if(thicknessesArray2[k]>maxThicknessArray2) {
					maxThicknessArray2 = thicknessesArray2[k];
				}
				//System.out.println(thicknessesArray1[k]+" "+thicknessesArray2[k]);
			}
			
			totalThickness=0.5*maxThicknessArray2+0.5*maxThicknessArray1+Vector3D.getDistance(lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2);
		}else {
			
			if(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray1, refractiveIndex)<0 && RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray2, refractiveIndex)<0) {
				totalThickness =  Math.max(Math.abs(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray2, refractiveIndex)),Math.abs(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray1, refractiveIndex))) 
						+ Vector3D.getDistance(lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2);
			}else if(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray1, refractiveIndex)<0) {
				totalThickness = RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray1, refractiveIndex) + Vector3D.getDistance(lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2);
			}else if(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray2, refractiveIndex)<0) {
				totalThickness = RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray2, refractiveIndex) + Vector3D.getDistance(lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2);
			}else {
				totalThickness = Vector3D.getDistance(lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2);
			}
		}

		
		boundingBoxThicknessSpanVector = (Vector3D.crossProduct(boundingBoxSpanVector1, boundingBoxSpanVector2).getNormalised()).getProductWith(totalThickness);
		System.out.println(totalThickness);
		return boundingBoxThicknessSpanVector;
	}
	
	/**
	 * @param lens00PrincipalPointArray1	Centre of the 0th principal point
	 * @param lens00ClearApertureCentre	Centre of the 0th clear aperture
	 * @param principalPointArray1BasisVector1	First basis vector for the principal point in the first array
	 * @param principalPointArray1BasisVector2	Second basis vector for the principal point in the first array
	 * @param clearApertureArrayBasisVector1	First basis vector for the clear aperture
	 * @param clearApertureArrayBasisVector2	Second basis vector for the clear aperture
	 * @param centreThicknessArray1 Thickness if the lenslets in the first array
	 * @param focalLengthArray1 Focal length of the lenslets in the first array
	 * @param refractiveIndex Refractive index of the lenslets
	 * @param voxelIndices	Voxel indices to calculate which voxel and hence which lens the light ray enters
	 * @return	minAndMaxThicknessArray1 An array containing the minimum thickness needed to fill the clear aperture array position [0], and the maximum thickness of the individual lenslets array position [2].
	 */
	public static double[] calculateMinAndMaxThickness(Vector3D lens00PrincipalPoint, Vector3D lens00ClearApertureCentre, Vector3D principalPointArrayBasisVector1, Vector3D principalPointArrayBasisVector2, 
			Vector3D clearApertureArrayBasisVector1, Vector3D clearApertureArrayBasisVector2, double centreThickness,double focalLength, double refractiveIndex, int[] voxelIndices)
	{	
		
		double[] minAndMaxThickness = new double [2]; //creating an array as output so both max and min can be extracted from single function. 
		double lensMaximumThickness, minimumCentreThickness; //the minimum centre thickness, can be negative for concave lenses, and maximum thickness of the lens. 
		//First find the Maximum radius needed to fill the whole clear aperture.
		Vector3D centreClearAperture = Vector3D.sum(lens00ClearApertureCentre, clearApertureArrayBasisVector1.getProductWith(voxelIndices[0]), clearApertureArrayBasisVector2.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPoint = Vector3D.sum(lens00PrincipalPoint, principalPointArrayBasisVector1.getProductWith(voxelIndices[0]), principalPointArrayBasisVector2.getProductWith(voxelIndices[1]));
		Vector3D toCorner1,toCorner2,toCorner3,toCorner4;  //the vector from the ClearAperture centre to the 4 corner positions of the ClearAperture
		toCorner1 = clearApertureArrayBasisVector1.getSumWith(clearApertureArrayBasisVector2); //sum of basis vectors
		toCorner2 = clearApertureArrayBasisVector1.getDifferenceWith(clearApertureArrayBasisVector2); //first basis vector minus second one
		toCorner3 = (clearApertureArrayBasisVector1.getProductWith(-1)).getDifferenceWith(clearApertureArrayBasisVector2); //sum of negative basis vectors
		toCorner4 = clearApertureArrayBasisVector2.getDifferenceWith(clearApertureArrayBasisVector1); //second basis vector minus first one
		double distanceToCorner1,distanceToCorner2,distanceToCorner3,distanceToCorner4; //distance from the Principal Point Centre to the corners of the Clear Aperture
		distanceToCorner1 = centreClearAperture.getDifferenceWith(centrePrincipalPoint).getSumWith(toCorner1).getLength(); //the distance or radius to the first corner from the Principal Point Centre
		distanceToCorner2 = centreClearAperture.getDifferenceWith(centrePrincipalPoint).getSumWith(toCorner2).getLength(); //the distance or radius to the second corner from the Principal Point Centre
		distanceToCorner3 = centreClearAperture.getDifferenceWith(centrePrincipalPoint).getSumWith(toCorner3).getLength(); //the distance or radius to the third corner from the Principal Point Centre
		distanceToCorner4 = centreClearAperture.getDifferenceWith(centrePrincipalPoint).getSumWith(toCorner4).getLength(); //the distance or radius to the fourth corner from the Principal Point Centre
		double maximumRadiusToFillClearAperture = Math.max(Math.max(distanceToCorner1,distanceToCorner2), Math.max(distanceToCorner3,distanceToCorner4)) + MyMath.TINY; //The required radius of the lens to just fill its clear aperture plus MyMath.TINY to make the corner non zero in thickness.


		/**Finding the closest point to the principal point centre along the clear aperture:
		 * For reference the regions are below showing the corners too. 
		 * 			:					:
		 * region 1	:		region 2	:	region 3
		 * 			:					:
		 *  ......c1:___________________:c2..........
		 *  		|					|
		 * region 8	| 		region 9	| 	region 4
		 *  		|(clearAperture)	|
		 * .......c4|___________________|c3..........
		 *  		:					:
		 * region 7 :		region 6	:	region 5
		 * 			:					:
		 */
		Vector3D c1,c2,c3,c4; //corners of the clear aperture (no need for 3rd corner
		c1 = toCorner1.getSumWith(centreClearAperture);
		c2 = toCorner2.getSumWith(centreClearAperture);
		c3 = toCorner3.getSumWith(centreClearAperture);
		c4 = toCorner4.getSumWith(centreClearAperture);
		Vector3D c1Toc2, c1Toc4; //direction vectors between corners
		c1Toc2 = c2.getDifferenceWith(c1); //first direction vector.
		c1Toc4 = c4.getDifferenceWith(c1); //second direction vector.
		Vector3D closestPointAlongClearAperture; //closest point along the clear aperture centre from the principal point centre
		double minDistanceToClearAperture; //minimum distance to the closest point between the lens principal point and clear aperture

		//start with region 9 and define anything inside or on the lines
		if (	Vector3D.scalarProduct(c1Toc2,centrePrincipalPoint)>= Math.min(Vector3D.scalarProduct(c1Toc2,c1),Vector3D.scalarProduct(c1Toc2,c2)) &&
				Vector3D.scalarProduct(c1Toc2,centrePrincipalPoint)<= Math.max(Vector3D.scalarProduct(c1Toc2,c1),Vector3D.scalarProduct(c1Toc2,c2))
				&&
				Vector3D.scalarProduct(c1Toc4,centrePrincipalPoint)>= Math.min(Vector3D.scalarProduct(c1Toc4,c1),Vector3D.scalarProduct(c1Toc4,c4)) &&
				Vector3D.scalarProduct(c1Toc4,centrePrincipalPoint)<= Math.max(Vector3D.scalarProduct(c1Toc4,c1),Vector3D.scalarProduct(c1Toc4,c4))
				) {
			closestPointAlongClearAperture = centrePrincipalPoint; //closest point when within the window is itself

		}else if(Vector3D.scalarProduct(c1Toc2,centrePrincipalPoint)>= Math.min(Vector3D.scalarProduct(c1Toc2,c1),Vector3D.scalarProduct(c1Toc2,c2)) &&
				Vector3D.scalarProduct(c1Toc2,centrePrincipalPoint)<= Math.max(Vector3D.scalarProduct(c1Toc2,c1),Vector3D.scalarProduct(c1Toc2,c2))){ //In the regions 2 or 6


			if (centrePrincipalPoint.getDifferenceWith(Geometry.getPointOnLineClosestToPoint(c1,c1Toc2,centrePrincipalPoint)).getLength()<centrePrincipalPoint.getDifferenceWith(Geometry.getPointOnLineClosestToPoint(c4,c1Toc2,centrePrincipalPoint)).getLength()) {
				closestPointAlongClearAperture = Geometry.getPointOnLineClosestToPoint(c1,c1Toc2,centrePrincipalPoint); // closest point is along line c1 to c2 
			}
			else {
				closestPointAlongClearAperture = Geometry.getPointOnLineClosestToPoint(c1,c1Toc2,centrePrincipalPoint); // closest point is along line c4 to c3
			}

		}			
		else if(Vector3D.scalarProduct(c1Toc4,centrePrincipalPoint)>= Math.min(Vector3D.scalarProduct(c1Toc4,c1),Vector3D.scalarProduct(c1Toc4,c4)) &&
				Vector3D.scalarProduct(c1Toc4,centrePrincipalPoint)<= Math.max(Vector3D.scalarProduct(c1Toc4,c1),Vector3D.scalarProduct(c1Toc4,c4))) { //in the regions 4 or 8

			if (centrePrincipalPoint.getDifferenceWith(Geometry.getPointOnLineClosestToPoint(c1,c1Toc4,centrePrincipalPoint)).getLength()<centrePrincipalPoint.getDifferenceWith(Geometry.getPointOnLineClosestToPoint(c2,c1Toc4,centrePrincipalPoint)).getLength()) {
				closestPointAlongClearAperture = Geometry.getPointOnLineClosestToPoint(c1,c1Toc4,centrePrincipalPoint); // closest point is along line c1 to c4 
			}
			else {
				closestPointAlongClearAperture = Geometry.getPointOnLineClosestToPoint(c2,c1Toc4,centrePrincipalPoint); // closest point is along line c2 to c3
			}
		}
		else {// in the regions 1,3,5 or 7
			Vector3D cornerArray[] = {c1,c2,c3,c4}; 
			minDistanceToClearAperture = 100000; //some very large distance so first check will be true. 
			closestPointAlongClearAperture = new Vector3D(0,0,0); //give it some random vector else it will not run. Gets over written later on. 
			for(int i=1;i<=4;i++){  
				if(cornerArray[i].getDifferenceWith(centrePrincipalPoint).getLength()<=minDistanceToClearAperture) {
					closestPointAlongClearAperture = cornerArray[i];
					minDistanceToClearAperture = closestPointAlongClearAperture.getDifferenceWith(centrePrincipalPoint).getLength();
				}
			}
		}
		minDistanceToClearAperture = closestPointAlongClearAperture.getDifferenceWith(centrePrincipalPoint).getLength();

		/**
		 * With all these defined, the minimum centre thickness and the lens thickness can be found
		 */


		/**
		 * TODO, add some limiting functions such that functions do not break down i.e r<0.5 centreThickness 
		 */
		//define the sphere radius which make up the lenses and the thickness to get the maximum lens thickness
		minimumCentreThickness = centreThickness;
		double r = Math.abs(RefractiveLensSurfaces.calculateRFromLensmakersEquation(focalLength, refractiveIndex, minimumCentreThickness));
		lensMaximumThickness = 2*(Math.sqrt(r*r - minDistanceToClearAperture*minDistanceToClearAperture)-Math.sqrt((r-0.5*centreThickness)*(r-0.5*centreThickness)));

		//if centreThickness is 0 calculate minimum thickness automatically.
		if (centreThickness == -1) {		
			if (focalLength>0) {
				//loop to find best central thickness to make as thin as possible
				for(int i=1;i<=100;i++){
					r = Math.abs(RefractiveLensSurfaces.calculateRFromLensmakersEquation(focalLength, refractiveIndex, minimumCentreThickness));
					minimumCentreThickness = 2*(r-Math.sqrt(r*r - maximumRadiusToFillClearAperture*maximumRadiusToFillClearAperture));
					lensMaximumThickness = 2*(Math.sqrt(r*r - minDistanceToClearAperture*minDistanceToClearAperture)-Math.sqrt((r-0.5*minimumCentreThickness)*(r-0.5*minimumCentreThickness)));	
				}


			}else{ 
				//loop to find best central thickness to make as thin as possible
				for(int i=1;i<=100;i++){
					r = Math.abs(RefractiveLensSurfaces.calculateRFromLensmakersEquation(focalLength, refractiveIndex, minimumCentreThickness));
					minimumCentreThickness = -2*(r-Math.sqrt(r*r - minDistanceToClearAperture*minDistanceToClearAperture));	
					lensMaximumThickness = 2*(Math.sqrt(r*r - minDistanceToClearAperture*minDistanceToClearAperture)-Math.sqrt((r-0.5*minimumCentreThickness)*(r-0.5*minimumCentreThickness)));
				}
			}
		} 
		minAndMaxThickness[0] =  minimumCentreThickness;
		minAndMaxThickness[1] = lensMaximumThickness;
//System.out.println(lensMaximumThickness+" "+minimumCentreThickness);
		return minAndMaxThickness;
	}
	
	

	
	
	@Override
	public String getType()
	{
		return "Refractive Lenslet Array";
	}

}
