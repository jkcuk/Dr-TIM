package optics.raytrace.surfaces;

import math.Geometry;
import math.MathException;
import math.MyMath;
import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.Parallelepiped2;


/**
 * A rectangular array of lenslets, which all have the same focal length.
 * 
 * The array is surrounded by a bounding box comprising a parallelepiped.  This SceneObject is that bounding box.
 * It is the responsibility of the user to ensure that this bounding box surrounds the relevant part of the array.
 * 
 * The principal points of the lenses can be different from the clear-aperture centres of the lenses.
 * Unlike the simple CLAs here the voxellation is made using a fan of planes which should all intersect at one point.
 * It is the users responsibility to set the variables appropriately such that this can occur.
 * 
 * Tracing through the lenses is handled by the bounding box's SurfaceProperty.
 * 
 * @author Maik based on RefractiveLensletArray
 */
public class GaborSupererRefractiveCLAs extends Parallelepiped2 
{



	private static final long serialVersionUID = -7368586705464154295L;
	
	
	private SurfaceOfGaborSupererRefractiveCLAs surface;
	
	private boolean separatedArrays = false;
	
	/**
	 * 
	 * @param description
	 * @param boundingBoxCentre
	 * @param boundingBoxSpanVector1
	 * @param boundingBoxSpanVector2
	 * @param boundingBoxSpanVector3
	 * @param normalisedOpticalAxisDirection
	 * @param commonPlaneInterceptionPoint
	 * @param lens00ClearApertureCentre
	 * @param clearApertureArrayBasisVector1
	 * @param clearApertureArrayBasisVector2
	 * @param focalLengthArray1
	 * @param lens00PrincipalPointArray1
	 * @param principalPointArrayBasisVector1Array1
	 * @param principalPointArrayBasisVector2Array1
	 * @param centreThicknessArray1
	 * @param focalLengthArray2
	 * @param lens00PrincipalPointArray2
	 * @param principalPointArrayBasisVector1Array2
	 * @param principalPointArrayBasisVector2Array2
	 * @param centreThicknessArray2
	 * @param refractiveIndex
	 * @param surfaceTransmissionCoefficient
	 * @param shadowThrowing
	 * @param maxSteps
	 * @param parent
	 * @param studio
	 */
	public GaborSupererRefractiveCLAs(
			String description,
			Vector3D boundingBoxCentre, 
			Vector3D boundingBoxSpanVector1,
			Vector3D boundingBoxSpanVector2,
			Vector3D boundingBoxSpanVector3,
			Vector3D normalisedOpticalAxisDirection,
			Vector3D commonPlaneInterceptionPoint,
			Vector3D lens00ClearApertureCentre,
			Vector3D clearApertureArrayBasisVector1,
			Vector3D clearApertureArrayBasisVector2,
			double focalLengthArray1,
			Vector3D lens00PrincipalPointArray1,
			Vector3D principalPointArrayBasisVector1Array1,
			Vector3D principalPointArrayBasisVector2Array1,
			double centreThicknessArray1,
			double focalLengthArray2,
			Vector3D lens00PrincipalPointArray2,
			Vector3D principalPointArrayBasisVector1Array2,
			Vector3D principalPointArrayBasisVector2Array2,
			double centreThicknessArray2,
			double refractiveIndex,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			boolean separatedArrays,
			int maxSteps,
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
			surface = new SurfaceOfGaborSupererRefractiveCLAs(
					correctedNormal(normalisedOpticalAxisDirection, lens00PrincipalPointArray1, lens00PrincipalPointArray2, focalLengthArray1),
					lens00ClearApertureCentre,
					clearApertureArrayBasisVector1,
					clearApertureArrayBasisVector2,
					commonPlaneInterceptionPoint,
					focalLengthArray1,
					lens00PrincipalPointArray1,
					principalPointArrayBasisVector1Array1,
					principalPointArrayBasisVector2Array1,
					centreThicknessArray1,
					focalLengthArray2,
					lens00PrincipalPointArray2,
					principalPointArrayBasisVector1Array2,
					principalPointArrayBasisVector2Array2,
					centreThicknessArray2,
					this,
					refractiveIndex,
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
	public GaborSupererRefractiveCLAs(
			String description,
			Vector3D boundingBoxCentre, 
			Vector3D boundingBoxSpanVector1,
			Vector3D boundingBoxSpanVector2,
			Vector3D boundingBoxSpanVector3,
			Vector3D normalisedOpticalAxisDirection,
			double focalLengthArray1,
			Vector3D lens00ClearApertureCentreArray1,
			Vector3D clearApertureArrayBasisVector1Array1,
			Vector3D clearApertureArrayBasisVector2Array1,
			Vector3D lens00PrincipalPointArray1,
			Vector3D principalPointArrayBasisVector1Array1,
			Vector3D principalPointArrayBasisVector2Array1,
			double centreThicknessArray1,
			double focalLengthArray2,
			Vector3D lens00ClearApertureCentreArray2,
			Vector3D clearApertureArrayBasisVector1Array2,
			Vector3D clearApertureArrayBasisVector2Array2,
			Vector3D lens00PrincipalPointArray2,
			Vector3D principalPointArrayBasisVector1Array2,
			Vector3D principalPointArrayBasisVector2Array2,
			double centreThicknessArray2,
			double refractiveIndex,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			boolean separatedArrays,
			int maxSteps,
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
		surface = new SurfaceOfGaborSupererRefractiveCLAs(
				correctedNormal(normalisedOpticalAxisDirection, lens00PrincipalPointArray1, lens00PrincipalPointArray2, focalLengthArray1),
				lens00ClearApertureCentreArray1,
				clearApertureArrayBasisVector1Array1,
				clearApertureArrayBasisVector2Array1,
				pointWhereAllPlanesMeet(clearApertureArrayBasisVector1Array1, clearApertureArrayBasisVector2Array1, lens00ClearApertureCentreArray1, clearApertureArrayBasisVector1Array2, clearApertureArrayBasisVector2Array2, lens00ClearApertureCentreArray2),	// voxellations
				focalLengthArray1,
				lens00PrincipalPointArray1,
				principalPointArrayBasisVector1Array1,
				principalPointArrayBasisVector2Array1,
				centreThicknessArray1,
				focalLengthArray2,
				lens00PrincipalPointArray2,
				principalPointArrayBasisVector1Array2,
				principalPointArrayBasisVector2Array2,
				centreThicknessArray2,
				this,
				refractiveIndex,
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
	public GaborSupererRefractiveCLAs(GaborSupererRefractiveCLAs original)
	{
		this(
				original.getDescription(),
				original.getBoundingBoxCentre(), 
				original.getBoundingBoxSpanVector1(),
				original.getBoundingBoxSpanVector2(),
				original.getBoundingBoxSpanVector3(),
				original.getNormalisedOpticalAxisDirection(),
				original.getCommonPlaneInterceptionPoint(),
				original.getLens00ClearApertureCentre(),
				original.getClearApertureArrayBasisVector1(),
				original.getClearApertureArrayBasisVector2(),
				original.getFocalLengthArray1(),
				original.getLens00PrincipalPointArray1(),
				original.getPrincipalPointArrayBasisVector1Array1(),
				original.getPrincipalPointArrayBasisVector2Array1(),
				original.getCentreThicknessArray1(),
				original.getFocalLengthArray2(),
				original.getLens00PrincipalPointArray2(),
				original.getPrincipalPointArrayBasisVector1Array2(),
				original.getPrincipalPointArrayBasisVector2Array2(),
				original.getCentreThicknessArray2(),
				original.getRefractiveIndex(),
				original.getSurfaceTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.isSeparatedArrays(),
				original.getMaxSteps(),
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
	
	public Vector3D getCommonPlaneInterceptionPoint() {
		return surface.getCommonPlaneInterceptionPoint();
	}
	
	public void setCommonPlaneInterceptionPoint(Vector3D commonPlaneInterceptionPoint) {
		surface.setCommonPlaneInterceptionPoint(commonPlaneInterceptionPoint);
	}
	
	public double getFocalLengthArray1() {
		return surface.getFocalLengthArray1();
	}
	
	public double getCentreThicknessArray1() {
		return surface.getCentreThicknessArray1();
	}

	public void setCentreThicknessArray1(double CentreThicknessArray1) {
		surface.setCentreThicknessArray1(CentreThicknessArray1);
	}

	public void setFocalLengthArray1(double focalLengthArray1) {
		surface.setFocalLengthArray1(focalLengthArray1);
	}
	
	public Vector3D getLens00ClearApertureCentre() {
		return surface.getLens00ClearApertureCentre();
	}

	public void setLens00ClearApertureCentre(Vector3D lens00ClearApertureCentre) {
		surface.setLens00ClearApertureCentre(lens00ClearApertureCentre);
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
	
	public Vector3D getLens00PrincipalPointArray1() {
		return surface.getLens00PrincipalPointArray1();
	}

	public void setLens00PrincipalPointArray1(Vector3D lens00PrincipalPointArray1) {
		surface.setLens00PrincipalPointArray1(lens00PrincipalPointArray1);
	}
	
	public Vector3D getPrincipalPointArrayBasisVector1Array1() {
		return surface.getPrincipalPointArrayBasisVector1Array1();
	}

	public void setPrincipalPointArrayBasisVector1Array1(Vector3D principalPointArrayBasisVector1Array1) {
		surface.setPrincipalPointArrayBasisVector1Array1(principalPointArrayBasisVector1Array1);
	}
	
	public Vector3D getPrincipalPointArrayBasisVector2Array1() {
		return surface.getPrincipalPointArrayBasisVector2Array1();
	}

	public void setPrincipalPointArrayBasisVector2Array1(Vector3D principalPointArrayBasisVector2Array1) {
		surface.setPrincipalPointArrayBasisVector2Array1(principalPointArrayBasisVector2Array1);
	}
		
	public double getFocalLengthArray2() {
		return surface.getFocalLengthArray2();
	}

	public void setFocalLengthArray2(double focalLengthArray2) {
		surface.setFocalLengthArray2(focalLengthArray2);
	}
	
	public double getCentreThicknessArray2() {
		return surface.getCentreThicknessArray2();
	}
	
	public Vector3D getLens00PrincipalPointArray2() {
		return surface.getLens00PrincipalPointArray2();
	}

	public void setLens00PrincipalPointArray2(Vector3D lens00PrincipalPointArray2) {
		surface.setLens00PrincipalPointArray2(lens00PrincipalPointArray2);
	}
		
	public Vector3D getPrincipalPointArrayBasisVector1Array2() {
		return surface.getPrincipalPointArrayBasisVector1Array1();
	}

	public void setPrincipalPointArrayBasisVector1Array2(Vector3D principalPointArrayBasisVector1Array2) {
		surface.setPrincipalPointArrayBasisVector1Array2(principalPointArrayBasisVector1Array2);
	}
	
	public Vector3D getPrincipalPointArrayBasisVector2Array2() {
		return surface.getPrincipalPointArrayBasisVector2Array2();
	}

	public void setPrincipalPointArrayBasisVector2Array2(Vector3D principalPointArrayBasisVector2Array2) {
		surface.setPrincipalPointArrayBasisVector2Array2(principalPointArrayBasisVector2Array2);
	}

	public double getRefractiveIndex() {
		return surface.getRefractiveIndex();
	}

	public void setRefractiveIndex(double refractiveIndex) {
		surface.setRefractiveIndex(refractiveIndex);
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
	 * Find the point, if it exists(as it should for the superer lens), where all planes meet.
	 * If no such point is found the meeting point is given as (0,0,0) and an error gets printed.
	 * 
	 * @param uPeriodArray1
	 * @param vPeriodArray1
	 * @param centreOfLens00Array1
	 * @param uPeriodArray2
	 * @param vPeriodArray2
	 * @param centreOfLens00Array2
	 * @return
	 */
	public static Vector3D pointWhereAllPlanesMeet(Vector3D uPeriodArray1, Vector3D vPeriodArray1, Vector3D centreOfLens00Array1, Vector3D uPeriodArray2, Vector3D vPeriodArray2, Vector3D centreOfLens00Array2)
	{		
		
		Vector3D pointWhereAllPlanesMeet;
		Vector3D intersectionFan1, intersectionFan2;
		//points of interest on the first array
		Vector3D pointOnPlane00Array1Fan1 = Vector3D.sum(centreOfLens00Array1, uPeriodArray1.getProductWith(0.5));
		Vector3D pointOnPlane00Array1Fan2 = Vector3D.sum(centreOfLens00Array1, vPeriodArray1.getProductWith(0.5));
		Vector3D pointOnplane10Array1 = Vector3D.sum(pointOnPlane00Array1Fan1, uPeriodArray1);
		Vector3D pointOnplane01Array1 = Vector3D.sum(pointOnPlane00Array1Fan2, vPeriodArray1);
		//points of interest on the second array	
		Vector3D pointOnPlane00Array2Fan1 = Vector3D.sum(centreOfLens00Array2, uPeriodArray2.getProductWith(0.5));
		Vector3D pointOnPlane00Array2Fan2 = Vector3D.sum(centreOfLens00Array2, vPeriodArray2.getProductWith(0.5));
		Vector3D pointOnplane10Array2 = Vector3D.sum(pointOnPlane00Array2Fan1, uPeriodArray2);
		Vector3D pointOnplane01Array2 = Vector3D.sum(pointOnPlane00Array2Fan2, vPeriodArray2);
		
		//time to do some geometry math to find the intersection points
		
			intersectionFan1 = Geometry.pointClosestToBothLines(
				pointOnPlane00Array1Fan1, // pointOnLine1,	// p_1
				Vector3D.difference(pointOnPlane00Array2Fan1, pointOnPlane00Array1Fan1),// directionOfLine1,	// v_1
				pointOnplane10Array1,// pointOnLine2,
				Vector3D.difference(pointOnplane10Array2, pointOnplane10Array1),// directionOfLine2
				false
				);
		
			intersectionFan2 = Geometry.pointClosestToBothLines(
					pointOnPlane00Array1Fan2, // pointOnLine1,	// p_1
					Vector3D.difference(pointOnPlane00Array2Fan2, pointOnPlane00Array1Fan2),// directionOfLine1,	// v_1
					pointOnplane01Array1,// pointOnLine2,
					Vector3D.difference(pointOnplane01Array2, pointOnplane01Array1),// directionOfLine2
					false
					);
		

				
		if(intersectionFan1.getDifferenceWith(intersectionFan2).getLength()> MyMath.EPSILON) {
			System.err.println("The Point of intercestion for Plane Fan 1 "+intersectionFan1+" is not the same as for Fan 2 "+intersectionFan2);
		}
		pointWhereAllPlanesMeet = intersectionFan1;	
		System.out.println(pointWhereAllPlanesMeet);
		return pointWhereAllPlanesMeet;
	}
	
	/**
	 * 
	 * @param normalisedOpticalAxisDirection
	 * @param lens00PrincipalPointArray1
	 * @param lens00PrincipalPointArray2
	 * @param focalLengthArray1
	 * @return correctedNormal, the normal which is pointing towards the second array. 
	 */
	public static Vector3D correctedNormal(Vector3D normalisedOpticalAxisDirection, Vector3D lens00PrincipalPointArray1, Vector3D lens00PrincipalPointArray2, double focalLengthArray1)
	{
		Vector3D correctedNormal;

		if(lens00PrincipalPointArray1.getSumWith(normalisedOpticalAxisDirection.getProductWith(MyMath.EPSILON)).getDifferenceWith(lens00PrincipalPointArray2).getLength()
				<
				lens00PrincipalPointArray1.getSumWith(normalisedOpticalAxisDirection.getProductWith(-MyMath.EPSILON)).getDifferenceWith(lens00PrincipalPointArray2).getLength())
		{
			correctedNormal = normalisedOpticalAxisDirection;
		}else {
			correctedNormal = normalisedOpticalAxisDirection.getProductWith(-1);
		}

		return correctedNormal;
	}
	
	/**
	 * 
	 * @param principalPointArrayBasisVector1Array1
	 * @param principalPointArrayBasisVector2Array1
	 * @param lens00ClearApertureCentre
	 * @param lens00PrincipalPointArray2
	 * @param commonPlaneInterceptionPoint
	 * @return lensClearApertureCentreArray2, the centre of the clear aperture of the second array
	 */
	public static Vector3D lensClearApertureCentreArray2Calculator(Vector3D principalPointArrayBasisVector1Array1, Vector3D principalPointArrayBasisVector2Array1, 
			Vector3D lens00ClearApertureCentre, Vector3D lens00PrincipalPointArray2 ,Vector3D commonPlaneInterceptionPoint) {
		//calc to find corresponding aperture centre vectors for second array.
		Vector3D lensClearApertureCentreArray2 = null;
		Vector3D normal = Vector3D.crossProduct(principalPointArrayBasisVector1Array1, principalPointArrayBasisVector1Array1).getNormalised();
		Vector3D clearAC1ToCommonPoint = commonPlaneInterceptionPoint.getDifferenceWith(lens00ClearApertureCentre);
		try {
			lensClearApertureCentreArray2 = Geometry.linePlaneIntersection(lens00ClearApertureCentre, clearAC1ToCommonPoint, lens00PrincipalPointArray2, normal);
		} catch (MathException e) {
			System.err.println("There are no or too many interceptions");
			e.printStackTrace();
		}
		return lensClearApertureCentreArray2;
	}

	
	@Override
	public String getType()
	{
		return "Gabor Superer Refractive CLAs";
	}

}
