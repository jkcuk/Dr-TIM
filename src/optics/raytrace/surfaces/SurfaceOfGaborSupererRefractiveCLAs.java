package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.RefractiveFrontHalfLensSurfaces;
import optics.raytrace.sceneObjects.RefractiveLensSurfaces;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.FanOfPlanes;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOfGaborSupererRefractiveCLAs extends SurfaceOfVoxellatedLensArray
{
	

	private static final long serialVersionUID = 1565344580956656840L;

	// variables
	
	/**
	 * Normalised vector in the direction of the optical axis of each of the lenslets.
	 */
	private Vector3D normalisedOpticalAxisDirection;



	/**
	 * Refractive index of the lens material
	 */
	private double refractiveIndex;
	
	/**
	 * transmission coefficient of each of the lens surfaces
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the lens surfaces throw shadows
	 */
	private boolean shadowThrowing;
	
	/**
	 * Focal length of the individual lenslets in the first array
	 */
	private double focalLengthArray1;
	
	/**
	 * Focal length of the individual lenslets in the second array
	 */
	private double focalLengthArray2;

	/**
	 * The thickness of each lens, i.e. the separation between the intersection between the optical axis and each of the two lens surfaces in the first array
	 */
	private double centreThicknessArray1;	
	
	/**
	 * The thickness of each lens, i.e. the separation between the intersection between the optical axis and each of the two lens surfaces in the second array
	 */
	private double centreThicknessArray2;
	
	/**
	 * True if the arrays should consist of two lenslet arrays separated by air, rather than two "lumps" on a refractive material.
	 */
	private boolean separatedArrays = false;
		

	Vector3D lens00PrincipalPointArray1;
	Vector3D principalPointArrayBasisVector1Array1;
	Vector3D principalPointArrayBasisVector2Array1;
	Vector3D lens00PrincipalPointArray2;
	Vector3D principalPointArrayBasisVector1Array2;
	Vector3D principalPointArrayBasisVector2Array2;
	
	Vector3D lens00ClearApertureCentre;
	Vector3D clearApertureArrayBasisVector1;
	Vector3D clearApertureArrayBasisVector2;
	Vector3D commonPlaneInterceptionPoint;
	
	
/**
 * Create a refractive Gabor superer lens surface by defining a point where all planes meet and the clear aperture size at any point.
 * 
 * @param normalisedOpticalAxisDirection
 * @param lens00ClearApertureCentre
 * @param clearApertureArrayBasisVector1
 * @param clearApertureArrayBasisVector2
 * @parma commonPlaneInterceptionPoint this is for the planes which will make up the voxells
 * @param focalLengthArray1
 * @param lens00PrincipalPointArray1
 * @param principalPointArrayBasisVector1Array1
 * @param principalPointArrayBasisVector2Array1
 * @param centreThicknessArray1
 * @param focalLengthArray2
 * @param lens00ClearApertureCentreArray2
 * @param clearApertureArrayBasisVector1Array2
 * @param clearApertureArrayBasisVector2Array2
 * @param lens00PrincipalPointArray2
 * @param principalPointArrayBasisVector1Array2
 * @param principalPointArrayBasisVector2Array2
 * @param centreThicknessArray2
 * @param boundingBox
 * @param refractiveIndex
 * @param surfaceTransmissionCoefficient
 * @param shadowThrowing
 * @param maxSteps
 */

	public SurfaceOfGaborSupererRefractiveCLAs(
			Vector3D normalisedOpticalAxisDirection,
			Vector3D lens00ClearApertureCentre,
			Vector3D clearApertureArrayBasisVector1,
			Vector3D clearApertureArrayBasisVector2,
			Vector3D commonPlaneInterceptionPoint,
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
			SceneObject boundingBox,
			double refractiveIndex,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			int maxSteps
		)
	{	
	super(
			createVoxellations(clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, lens00ClearApertureCentre, commonPlaneInterceptionPoint),	// voxellations
			boundingBox, maxSteps, 1., shadowThrowing
		);
	
	this.clearApertureArrayBasisVector1 = clearApertureArrayBasisVector1;
	this.clearApertureArrayBasisVector2 = clearApertureArrayBasisVector2;
	this.lens00ClearApertureCentre = lens00ClearApertureCentre;
	this.commonPlaneInterceptionPoint = commonPlaneInterceptionPoint;
	
	this.centreThicknessArray1 = centreThicknessArray1;
	this.focalLengthArray1 = focalLengthArray1;
	this.principalPointArrayBasisVector1Array1 = principalPointArrayBasisVector1Array1;
	this.principalPointArrayBasisVector2Array1 = principalPointArrayBasisVector2Array1;
	this.lens00PrincipalPointArray1 = lens00PrincipalPointArray1;
	
	this.centreThicknessArray2 = centreThicknessArray2;
	this.focalLengthArray2 = focalLengthArray2;
	this.principalPointArrayBasisVector1Array2 = principalPointArrayBasisVector1Array2;
	this.principalPointArrayBasisVector2Array2 = principalPointArrayBasisVector2Array2;
	this.lens00PrincipalPointArray2 = lens00PrincipalPointArray2;
	
	this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;	
	this.refractiveIndex = refractiveIndex;
	this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}


	
	
	
	public SurfaceOfGaborSupererRefractiveCLAs(SurfaceOfGaborSupererRefractiveCLAs o)
	{
		this(
				o.getNormalisedOpticalAxisDirection(),
				o.getLens00ClearApertureCentre(),
				o.getClearApertureArrayBasisVector1(),
				o.getClearApertureArrayBasisVector2(),
				o.getCommonPlaneInterceptionPoint(),
				o.getFocalLengthArray1(),				
				o.getLens00PrincipalPointArray1(),
				o.getPrincipalPointArrayBasisVector1Array1(),
				o.getPrincipalPointArrayBasisVector2Array1(),
				o.getCentreThicknessArray1(),
				o.getFocalLengthArray2(),
				o.getLens00PrincipalPointArray2(),
				o.getPrincipalPointArrayBasisVector1Array2(),
				o.getPrincipalPointArrayBasisVector2Array2(),
				o.getCentreThicknessArray2(),
				o.getSurface(),
				o.getRefractiveIndex(),
				o.getSurfaceTransmissionCoefficient(),
				o.isShadowThrowing(),
				o.getMaxSteps()
			);
	}

	@Override
	public SurfaceOfVoxellatedLensArray clone() {
		return new SurfaceOfGaborSupererRefractiveCLAs(this);
	}
	
	
	public Vector3D getNormalisedOpticalAxisDirection() {
		return normalisedOpticalAxisDirection;
	}


	public void setNormalisedOpticalAxisDirection(Vector3D normalisedOpticalAxisDirection) {
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;
	}


	public double getRefractiveIndex() {
		return refractiveIndex;
	}


	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}


	public double getSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}


	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}


	public boolean isShadowThrowing() {
		return shadowThrowing;
	}


	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}


	public double getFocalLengthArray1() {
		return focalLengthArray1;
	}


	public void setFocalLengthArray1(double focalLengthArray1) {
		this.focalLengthArray1 = focalLengthArray1;
	}


	public double getFocalLengthArray2() {
		return focalLengthArray2;
	}


	public void setFocalLengthArray2(double focalLengthArray2) {
		this.focalLengthArray2 = focalLengthArray2;
	}


	public double getCentreThicknessArray1() {
		return centreThicknessArray1;
	}


	public void setCentreThicknessArray1(double centreThicknessArray1) {
		this.centreThicknessArray1 = centreThicknessArray1;
	}


	public double getCentreThicknessArray2() {
		return centreThicknessArray2;
	}


	public void setCentreThicknessArray2(double centreThicknessArray2) {
		this.centreThicknessArray2 = centreThicknessArray2;
	}


	public Vector3D getLens00PrincipalPointArray1() {
		return lens00PrincipalPointArray1;
	}


	public void setLens00PrincipalPointArray1(Vector3D lens00PrincipalPointArray1) {
		this.lens00PrincipalPointArray1 = lens00PrincipalPointArray1;
	}


	public Vector3D getPrincipalPointArrayBasisVector1Array1() {
		return principalPointArrayBasisVector1Array1;
	}


	public void setPrincipalPointArrayBasisVector1Array1(Vector3D principalPointArrayBasisVector1Array1) {
		this.principalPointArrayBasisVector1Array1 = principalPointArrayBasisVector1Array1;
	}


	public Vector3D getPrincipalPointArrayBasisVector2Array1() {
		return principalPointArrayBasisVector2Array1;
	}


	public void setPrincipalPointArrayBasisVector2Array1(Vector3D principalPointArrayBasisVector2Array1) {
		this.principalPointArrayBasisVector2Array1 = principalPointArrayBasisVector2Array1;
	}


	public Vector3D getLens00PrincipalPointArray2() {
		return lens00PrincipalPointArray2;
	}


	public void setLens00PrincipalPointArray2(Vector3D lens00PrincipalPointArray2) {
		this.lens00PrincipalPointArray2 = lens00PrincipalPointArray2;
	}


	public Vector3D getPrincipalPointArrayBasisVector1Array2() {
		return principalPointArrayBasisVector1Array2;
	}


	public void setPrincipalPointArrayBasisVector1Array2(Vector3D principalPointArrayBasisVector1Array2) {
		this.principalPointArrayBasisVector1Array2 = principalPointArrayBasisVector1Array2;
	}


	public Vector3D getPrincipalPointArrayBasisVector2Array2() {
		return principalPointArrayBasisVector2Array2;
	}


	public void setPrincipalPointArrayBasisVector2Array2(Vector3D principalPointArrayBasisVector2Array2) {
		this.principalPointArrayBasisVector2Array2 = principalPointArrayBasisVector2Array2;
	}

	public Vector3D getLens00ClearApertureCentre() {
		return lens00ClearApertureCentre;
	}


	public void setLens00ClearApertureCentre(Vector3D lens00ClearApertureCentre) {
		this.lens00ClearApertureCentre = lens00ClearApertureCentre;
	}


	public Vector3D getClearApertureArrayBasisVector1() {
		return clearApertureArrayBasisVector1;
	}


	public void setClearApertureArrayBasisVector1(Vector3D clearApertureArrayBasisVector1) {
		this.clearApertureArrayBasisVector1 = clearApertureArrayBasisVector1;
	}


	public Vector3D getClearApertureArrayBasisVector2() {
		return clearApertureArrayBasisVector2;
	}


	public void setClearApertureArrayBasisVector2(Vector3D clearApertureArrayBasisVector2) {
		this.clearApertureArrayBasisVector2 = clearApertureArrayBasisVector2;
	}


	public Vector3D getCommonPlaneInterceptionPoint() {
		return commonPlaneInterceptionPoint;
	}


	public void setCommonPlaneInterceptionPoint(Vector3D commonPlaneInterceptionPoint) {
		this.commonPlaneInterceptionPoint = commonPlaneInterceptionPoint;
	}
	
	public boolean isSeparatedArrays() {
		return separatedArrays;
	}

	public void setSeparatedArrays(boolean separatedArrays) {
		this.separatedArrays = separatedArrays;
	}	


	// the vaguely fascinating bits
	@Override
	public SceneObject getRefractiveLens(int[] voxelIndices) {
		// Vector3D centreclearAperture = Vector3D.sum(lens00ClearApertureCentre, clearApertureArrayBasisVector1.getProductWith(voxelIndices[0]), clearApertureArrayBasisVector2.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPointLensArray1 = Vector3D.sum(lens00PrincipalPointArray1, principalPointArrayBasisVector1Array1.getProductWith(voxelIndices[0]), principalPointArrayBasisVector2Array1.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPointLensArray2 = Vector3D.sum(lens00PrincipalPointArray2, principalPointArrayBasisVector1Array2.getProductWith(voxelIndices[0]), principalPointArrayBasisVector2Array2.getProductWith(voxelIndices[1]));
		
		SceneObjectContainer gaborSupererRefractiveLensArrayContainer = new SceneObjectContainer(
				"Scene object container containing the two lenslet arrays", // description, 
				surface, // parent, 
				surface.getStudio()// studio
				);
	
		if(separatedArrays) {
			RefractiveLensSurfaces gaborSupererRefractiveLensArray1 = new RefractiveLensSurfaces(
					"Lens #"+ voxelIndices[0]+", "+voxelIndices[1]+ " in Array 1",	// description
					centrePrincipalPointLensArray1,	// centre
					normalisedOpticalAxisDirection,
					focalLengthArray1,
					refractiveIndex,
					centreThicknessArray1,
					surfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient
					shadowThrowing,	// shadowThrowing
					surface,	// parent
					surface.getStudio()	// studio
					);

			RefractiveLensSurfaces gaborSupererRefractiveLensArray2 = new RefractiveLensSurfaces(
					"Lens #"+ voxelIndices[0]+", "+voxelIndices[1]+ " in Array 2",	// description
					centrePrincipalPointLensArray2,	// centre
					normalisedOpticalAxisDirection,
					focalLengthArray2,
					refractiveIndex,
					centreThicknessArray2,
					surfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient
					shadowThrowing,	// shadowThrowing
					surface,	// parent
					surface.getStudio()	// studio
					);

			gaborSupererRefractiveLensArrayContainer.addSceneObject(gaborSupererRefractiveLensArray1);
			gaborSupererRefractiveLensArrayContainer.addSceneObject(gaborSupererRefractiveLensArray2);
			
		}else {
			
				RefractiveFrontHalfLensSurfaces RefractiveFrontHalfLensSurfaceArray1 = new RefractiveFrontHalfLensSurfaces(
						"Array 1, Lens #"+ voxelIndices[0]+", "+voxelIndices[1], // description,
						centrePrincipalPointLensArray1,// centre,
						normalisedOpticalAxisDirection.getWithLength(Math.signum(focalLengthArray1)), //normalisedOpticalAxisDirection,
						focalLengthArray1, // focalLength,
						refractiveIndex, // refractiveIndex,
						Math.abs(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray1, refractiveIndex)),// centreThickness equal to r to make maximum size lens,
						surfaceTransmissionCoefficient, // surfaceTransmissionCoefficient,
						shadowThrowing, // shadowThrowing,
						surface, // parent,
						surface.getStudio()	// studio
						);
						
				RefractiveFrontHalfLensSurfaces RefractiveFrontHalfLensSurfaceArray2 = new RefractiveFrontHalfLensSurfaces(
						"Array 2, Lens #"+ voxelIndices[0]+", "+voxelIndices[1], // description,
						centrePrincipalPointLensArray2, //centre
						normalisedOpticalAxisDirection.getWithLength(-Math.signum(focalLengthArray2)), //normalisedOpticalAxisDirection,
						focalLengthArray2, // focalLength,
						refractiveIndex, // refractiveIndex,
						Math.abs(RefractiveFrontHalfLensSurfaces.calculateRFromLensmakersEquation(focalLengthArray2, refractiveIndex)),// centreThickness equal to r to make maximum size lens,
						surfaceTransmissionCoefficient, // surfaceTransmissionCoefficient,
						shadowThrowing, // shadowThrowing,
						surface, // parent,
						surface.getStudio()	// studio
						);
						
				gaborSupererRefractiveLensArrayContainer.addSceneObject(RefractiveFrontHalfLensSurfaceArray1);
				gaborSupererRefractiveLensArrayContainer.addSceneObject(RefractiveFrontHalfLensSurfaceArray2);
			}
			//System.out.println(separatedArrays);
		 	
		 return gaborSupererRefractiveLensArrayContainer;
	}
	
	public static Voxellation[] createVoxellations(Vector3D uPeriod, Vector3D vPeriod, Vector3D centreOfLens00, Vector3D commonInterceptionPoint)
	{
		Voxellation voxellations[] = new Voxellation[2];
		
		Vector3D pointOnPlane0 = Vector3D.sum(centreOfLens00, uPeriod.getProductWith(+0.5), vPeriod.getProductWith(+0.5));
		Vector3D pointOnplane1 = Vector3D.sum(pointOnPlane0, uPeriod, vPeriod);

		if(commonInterceptionPoint.getLength() >= Double.POSITIVE_INFINITY) {		
		//System.out.println("SurfaceOfGeneralRefractiveLensletArrays::createVoxellations: pointOnPlane0="+pointOnPlane0);
		
		voxellations[0] = new SetOfEquidistantParallelPlanes(
				pointOnPlane0,	// pointOnPlane0
				uPeriod.getPartPerpendicularTo(vPeriod).getNormalised(),	// normal
				uPeriod.getPartPerpendicularTo(vPeriod).getLength()	// separation
			);

		voxellations[1] = new SetOfEquidistantParallelPlanes(
				pointOnPlane0,	// pointOnPlane0
				vPeriod.getPartPerpendicularTo(uPeriod).getNormalised(),	// normal
				vPeriod.getPartPerpendicularTo(uPeriod).getLength()	// separation
			);
		}else {
			
			voxellations[0] = new FanOfPlanes(
					commonInterceptionPoint,// c1, 
					commonInterceptionPoint.getSumWith(vPeriod),// c2, 
					pointOnPlane0,// p0, 
					pointOnplane1 //p1
				);

			voxellations[1] = new FanOfPlanes(
					commonInterceptionPoint,// c1, 
					commonInterceptionPoint.getSumWith(uPeriod),// c2, 
					pointOnPlane0,// p0, 
					pointOnplane1 //p1
				);
		}
		
		return voxellations;
	}
}
