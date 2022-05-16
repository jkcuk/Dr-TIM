package optics.raytrace.surfaces;


import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.RefractiveFrontHalfLensSurfaces;
import optics.raytrace.sceneObjects.RefractiveLensSurfaces;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOfSimpleRefractiveCLAs extends SurfaceOfRefractiveComponentArray
{
	
	private static final long serialVersionUID = -411026189354317280L;

	// variables
	
	/**
	 * Normalised vector in the direction of the optical axis of each of the lenslets.
	 * It points in the direction from the intersection between the optical axis and lens surface 1 
	 * to the intersection between the optical axis and lens surface 2.
	 */
	private Vector3D normalisedOpticalAxisDirection;

	/**
	 * Focal length of the individual lenslets for the first(closer) and second(further array)
	 */
	private double focalLengthArray1, focalLengthArray2;

	/**
	 * Refractive index of the lens material
	 */
	private double refractiveIndex;

	/**
	 * The thickness of each lens, i.e. the separation between the intersection between the optical axis and each of the two lens surfaces
	 */
	private double centreThicknessArray1, centreThicknessArray2;	
	
	/**
	 * transmission coefficient of each of the lens surfaces
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the lens surfaces throw shadows
	 */
	private boolean shadowThrowing;
	
	/**
	 * True if the arrays should consist of two lenslet arrays separated by air, rather than two "lumps" on a refractive material.
	 */
	private boolean separatedArrays = false;

	
	Vector3D principalPointArray1BasisVector1,principalPointArray2BasisVector1;
	Vector3D principalPointArray1BasisVector2,principalPointArray2BasisVector2;
	

	Vector3D clearApertureArrayBasisVector1;
	Vector3D clearApertureArrayBasisVector2;
	
	Vector3D lens00ClearApertureCentreArray1, lens00ClearApertureCentreArray2;
	Vector3D lens00PrincipalPointArray1,lens00PrincipalPointArray2;
	
	/**
	 * Generate very general refractive CLAs
	 * 
	 * @param normalisedOpticalAxisDirection
	 * @param focalLength
	 * @param clearApertureArrayBasisVector1
	 * @param clearApertureArrayBasisVector2
	 * @param principalPointArray1BasisVector1
	 * @param principalPointArray1BasisVector2
	 * @param principalPointArray2BasisVector1
	 * @param principalPointArray2BasisVector2
	 * @param lens00ClearApertureCentre
	 * @param lens00PrincipalPointArray1
	 * @param lens00PrincipalPointArray2
	 * @param surface
	 * @param refractiveIndex
	 * @param centreThickness
	 * @param surfaceTransmissionCoefficient
	 * @param shadowThrowing
	 * @param maxSteps
	 */
	public SurfaceOfSimpleRefractiveCLAs(
			Vector3D normalisedOpticalAxisDirection,
			double focalLengthArray1,
			double focalLengthArray2,
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
			SceneObject boundingBox,
			double refractiveIndex,
			double centreThicknessArray1,
			double centreThicknessArray2,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			int maxSteps
		)
	{
		super(
				createVoxellations(clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, lens00ClearApertureCentreArray1),	// voxellations
				boundingBox, maxSteps, 1., shadowThrowing
			);
		
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;
		this.focalLengthArray1 = focalLengthArray1;
		this.focalLengthArray2 = focalLengthArray2;
		this.clearApertureArrayBasisVector1 = clearApertureArrayBasisVector1;
		this.clearApertureArrayBasisVector2 = clearApertureArrayBasisVector2;
		this.principalPointArray1BasisVector1 = principalPointArray1BasisVector1;
		this.principalPointArray1BasisVector2 = principalPointArray1BasisVector2;
		this.principalPointArray2BasisVector1 = principalPointArray2BasisVector1;
		this.principalPointArray2BasisVector2 = principalPointArray2BasisVector2;
		this.lens00ClearApertureCentreArray1 = lens00ClearApertureCentreArray1;
		this.lens00ClearApertureCentreArray2 = lens00ClearApertureCentreArray2;
		this.lens00PrincipalPointArray1 = lens00PrincipalPointArray1;
		this.lens00PrincipalPointArray2 = lens00PrincipalPointArray2;
		
		this.refractiveIndex = refractiveIndex;
		this.centreThicknessArray1 = centreThicknessArray1;
		this.centreThicknessArray2 = centreThicknessArray2;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}
	
	

	
	public SurfaceOfSimpleRefractiveCLAs(SurfaceOfSimpleRefractiveCLAs o)
	{
		this(
				o.getNormalisedOpticalAxisDirection(),
				o.getFocalLengthArray1(),
				o.getFocalLengthArray2(),
				o.getClearApertureArrayBasisVector1(),
				o.getClearApertureArrayBasisVector2(),
				o.getPrincipalPointArray1BasisVector1(),
				o.getPrincipalPointArray1BasisVector2(),
				o.getPrincipalPointArray2BasisVector1(),
				o.getPrincipalPointArray2BasisVector2(),
				o.getLens00ClearApertureCentreArray1(),
				o.getLens00ClearApertureCentreArray2(),
				o.getLens00PrincipalPointArray1(),
				o.getLens00PrincipalPointArray2(),
				o.getSurface(),
				o.getRefractiveIndex(),
				o.getCentreThicknessArray1(),
				o.getCentreThicknessArray2(),				
				o.getSurfaceTransmissionCoefficient(),
				o.isShadowThrowing(),
				o.getMaxSteps()
			);
	}

	@Override
	public SurfaceOfRefractiveComponentArray clone() {
		return new SurfaceOfSimpleRefractiveCLAs(this);
	}
	
	
	public Vector3D getNormalisedOpticalAxisDirection() {
		return normalisedOpticalAxisDirection;
	}


	public void setNormalisedOpticalAxisDirection(Vector3D normalisedOpticalAxisDirection) {
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;
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


	public double getRefractiveIndex() {
		return refractiveIndex;
	}


	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
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


	public Vector3D getPrincipalPointArray1BasisVector1() {
		return principalPointArray1BasisVector1;
	}


	public void setPrincipalPointArray1BasisVector1(Vector3D principalPointArray1BasisVector1) {
		this.principalPointArray1BasisVector1 = principalPointArray1BasisVector1;
	}


	public Vector3D getPrincipalPointArray2BasisVector1() {
		return principalPointArray2BasisVector1;
	}


	public void setPrincipalPointArray2BasisVector1(Vector3D principalPointArray2BasisVector1) {
		this.principalPointArray2BasisVector1 = principalPointArray2BasisVector1;
	}


	public Vector3D getPrincipalPointArray1BasisVector2() {
		return principalPointArray1BasisVector2;
	}


	public void setPrincipalPointArray1BasisVector2(Vector3D principalPointArray1BasisVector2) {
		this.principalPointArray1BasisVector2 = principalPointArray1BasisVector2;
	}


	public Vector3D getPrincipalPointArray2BasisVector2() {
		return principalPointArray2BasisVector2;
	}


	public void setPrincipalPointArray2BasisVector2(Vector3D principalPointArray2BasisVector2) {
		this.principalPointArray2BasisVector2 = principalPointArray2BasisVector2;
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


	public Vector3D getLens00ClearApertureCentreArray1() {
		return lens00ClearApertureCentreArray1;
	}


	public void setLens00ClearApertureCentreArray1(Vector3D lens00ClearApertureCentreArray1) {
		this.lens00ClearApertureCentreArray1 = lens00ClearApertureCentreArray1;
	}
	
	public Vector3D getLens00ClearApertureCentreArray2() {
		return lens00ClearApertureCentreArray2;
	}


	public void setLens00ClearApertureCentreArray2(Vector3D lens00ClearApertureCentreArray2) {
		this.lens00ClearApertureCentreArray2 = lens00ClearApertureCentreArray2;
	}

	public Vector3D getLens00PrincipalPointArray1() {
		return lens00PrincipalPointArray1;
	}


	public void setLens00PrincipalPointArray1(Vector3D lens00PrincipalPointArray1) {
		this.lens00PrincipalPointArray1 = lens00PrincipalPointArray1;
	}


	public Vector3D getLens00PrincipalPointArray2() {
		return lens00PrincipalPointArray2;
	}


	public void setLens00PrincipalPointArray2(Vector3D lens00PrincipalPointArray2) {
		this.lens00PrincipalPointArray2 = lens00PrincipalPointArray2;
	}

	public boolean isSeparatedArrays() {
		return separatedArrays;
	}

	public void setSeparatedArrays(boolean separatedArrays) {
		this.separatedArrays = separatedArrays;
	}




	// the vaguely fascinating bits
	@Override
	public SceneObject getRefractiveComponent(int[] voxelIndices) {
		// Vector3D centreclearAperture = Vector3D.sum(lens00ClearApertureCentre, clearApertureArrayBasisVector1.getProductWith(voxelIndices[0]), clearApertureArrayBasisVector2.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPointLensArray1 = Vector3D.sum(lens00PrincipalPointArray1, principalPointArray1BasisVector1.getProductWith(voxelIndices[0]), principalPointArray1BasisVector2.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPointLensArray2 = Vector3D.sum(lens00PrincipalPointArray2, principalPointArray2BasisVector1.getProductWith(voxelIndices[0]), principalPointArray2BasisVector2.getProductWith(voxelIndices[1]));
		
		SceneObjectContainer refractiveLensletArrayContainer = new SceneObjectContainer(
				"Scene object container containing the two lenslet arrays", // description, 
				surface, // parent, 
				surface.getStudio()// studio
				);
		
		if(separatedArrays) {
		RefractiveLensSurfaces RefractiveLensSurfacesArray1 = new RefractiveLensSurfaces(
				"Array 1, Lens #"+ voxelIndices[0]+", "+voxelIndices[1],	// description
				centrePrincipalPointLensArray1,	// centre
				normalisedOpticalAxisDirection,
				focalLengthArray1,
				refractiveIndex,
				SimpleRefractiveCLAs.calculateMinAndMaxThickness(lens00PrincipalPointArray1, lens00ClearApertureCentreArray1, principalPointArray1BasisVector1, principalPointArray1BasisVector2, clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, 
						centreThicknessArray1,focalLengthArray1, refractiveIndex, voxelIndices)[0],  //central thickness
				surfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient
				shadowThrowing,	// shadowThrowing
				surface,	// parent
				surface.getStudio()	// studio
			);
		
		RefractiveLensSurfaces RefractiveLensSurfacesArray2 = new RefractiveLensSurfaces(
				"Array 2, Lens #"+ voxelIndices[0]+", "+voxelIndices[1],	// description
				centrePrincipalPointLensArray2,	// centre
				normalisedOpticalAxisDirection,
				focalLengthArray2,
				refractiveIndex,
				SimpleRefractiveCLAs.calculateMinAndMaxThickness(lens00PrincipalPointArray2, lens00ClearApertureCentreArray2, principalPointArray2BasisVector1, principalPointArray2BasisVector2, clearApertureArrayBasisVector1, clearApertureArrayBasisVector2, 
						centreThicknessArray2,focalLengthArray2, refractiveIndex, voxelIndices)[0], //central thickness
				surfaceTransmissionCoefficient,	// surfaceTransmissionCoefficient
				shadowThrowing,	// shadowThrowing
				surface,	// parent
				surface.getStudio()	// studio
			);
		
		//System.out.println(centrePrincipalPointLensArray1+" "+centrePrincipalPointLensArray2);

		refractiveLensletArrayContainer.addSceneObject(RefractiveLensSurfacesArray1);
		refractiveLensletArrayContainer.addSceneObject(RefractiveLensSurfacesArray2);
		
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
			

			refractiveLensletArrayContainer.addSceneObject(RefractiveFrontHalfLensSurfaceArray2);
			refractiveLensletArrayContainer.addSceneObject(RefractiveFrontHalfLensSurfaceArray1);
					
		}

		return refractiveLensletArrayContainer;

	}
	
	public static Voxellation[] createVoxellations(Vector3D uPeriod, Vector3D vPeriod, Vector3D centreOfLens00)
	{
		Voxellation voxellations[] = new Voxellation[2];
		Vector3D pointOnPlane0 = Vector3D.sum(centreOfLens00, uPeriod.getProductWith(+0.5), vPeriod.getProductWith(+0.5));
		
		
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
		
		return voxellations;
	}
	
	
	

	
	
	
	

}
