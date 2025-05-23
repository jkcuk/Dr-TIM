package optics.raytrace.surfaces;

import math.Geometry;
import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.research.refractiveTaylorSeries.DirectionChangingSurfaceSequence;
import optics.raytrace.research.refractiveTaylorSeries.SurfaceParameters;
import optics.raytrace.sceneObjects.ParametrisedCentredParallelogram;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.RefractiveCylindricalLensTelescope;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.surfaceOfPixelArray.BoundingBoxSurface;
import optics.raytrace.surfaces.surfaceOfPixelArray.BoundingBoxSurfaceForRefractiveComponent;
import optics.raytrace.surfaces.surfaceOfPixelArray.SurfaceOfPixelArray;
import optics.raytrace.surfaces.surfaceOfPixelArray.SurfaceSeparatingRefractiveVoxels;
import optics.raytrace.surfaces.surfaceOfPixelArray.SurfaceSeparatingVoxels;
import optics.raytrace.voxellations.FanOfPlanes;
import optics.raytrace.voxellations.SetOfSurfaces;
import optics.raytrace.voxellations.SetOfSurfaces.OutwardsNormalOrientation;

/*
 * The surface of a component which rotates the view seen through it by a defined angle using a refractive surface.
 * 
 */
public class SurfaceOfRefractiveViewRotator_Copy extends SurfaceOfPixelArray
{
	private static final long serialVersionUID = -2062342228761066401L;

	/**
	 * Primary flat surface normal, from the surface to the camera position.
	 */
	private Vector3D ocularPlaneNormal;

	/**
	 * Position from which the view of the surface of the view object through the component appear rotated and scaled 
	 */
	private Vector3D eyePosition;

	/**
	 * the scene object whose surface appears distorted when seen from the eye position through the component
	 */
	private SceneObject viewObject;

	/**
	 * 
	 */
	private Vector3D rotationAxisDirection;


	/**
	 * The centre of the ocular plane of the component
	 */
	private Vector3D ocularPlaneCentre;

	/**
	 * Centre of the rotation
	 */
	// private Vector3D pointOnRotationAxis;

	/**
	 * The angle through which the image should be rotated, in degrees.
	 */
	private double rotationAngle;

	/**
	 * in addition to appearing rotated, the surface of the view object also appears scaled
	 */
	private double magnificationFactor;

	/**
	 * The period and direction vectors in the ocular plane for the voxellation (pixels)
	 */
	private Vector3D periodVector1, periodVector2;

	/**
	 * Refractive index of the lens material
	 */
	private double refractiveIndex;

	/**
	 * Thickness of the individual wedges at the centre
	 */
	private double wedgeThickness;

	/**
	 * Derivative controller thickness
	 */
	private double derivativeControlThickness;
	/**
	 * Derivative control rotation amount in degrees
	 */
	private double derivativeControlRotation;

	/**
	 * the transmission coefficient of the material
	 */
	private double surfaceTransmissionCoefficient;

	/**
	 * Max trace level within the component
	 */
	private int maxStepsInArray;
	/**
	 * simulate diffractive blur blur when true
	 */
	private boolean simulateDiffractionBlur;

	/**
	 * Adding the derivative control surfaces to the specs. 
	 * These can at the moment either be no correction, ideal lenses, phase holograms or refractive lenses
	 */
	public enum DerivativeControlType{
		NONE("No correction"),
		IDEAL_THIN_LENS("Ideal thin lens"),
		HOLOGRAM("Hologram"),
		REFRACTIVE("Refractive");

		private String description;
		private DerivativeControlType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	protected DerivativeControlType derivativeControlType;


	/**
	 * 
	 * @param ocularPlaneNormal
	 * @param eyePosition
	 * @param ocularPlaneCentre
	 * @param rotationAngle
	 * @param rotationAxisDirection
	 * @param magnificationFactor
	 * @param periodVector1
	 * @param periodVector2
	 * @param viewObject
	 * @param derivativeControlType
	 * @param refractiveIndex
	 * @param wedgeThickness
	 * @param surfaceTransmissionCoefficient
	 * @param maxStepsInArray
	 * @param boundingBox
	 * @param scene
	 */

	public SurfaceOfRefractiveViewRotator_Copy(
			Vector3D ocularPlaneNormal,
			Vector3D eyePosition,
			Vector3D ocularPlaneCentre,
			double rotationAngle,
			Vector3D rotationAxisDirection,
			double magnificationFactor,
			Vector3D periodVector1,
			Vector3D periodVector2,
			SceneObject viewObject,
			DerivativeControlType derivativeControlType,
			double derivativeControlThickness,
			double derivativeControlRotation,
			double refractiveIndex,
			double wedgeThickness,
			double surfaceTransmissionCoefficient,
			boolean simulateDiffractionBlur,
			int maxStepsInArray,
			SceneObject boundingBox,
			SceneObject scene
			)
	{	
		super(
				createVoxellations(periodVector1, periodVector2, ocularPlaneCentre, ocularPlaneNormal, eyePosition),
				boundingBox,
				scene,
				maxStepsInArray
				);
		this.ocularPlaneNormal = ocularPlaneNormal;
		this.eyePosition=eyePosition;
		this.ocularPlaneCentre=ocularPlaneCentre;
		this.rotationAxisDirection =rotationAxisDirection;
		this.magnificationFactor = magnificationFactor;
		this.rotationAngle=rotationAngle;
		this.periodVector1=periodVector1;
		this.periodVector2=periodVector2;
		this.viewObject = viewObject;
		this.derivativeControlType = derivativeControlType;
		this.derivativeControlThickness = derivativeControlThickness;
		this.derivativeControlRotation =derivativeControlRotation;
		this.refractiveIndex = refractiveIndex;
		this.wedgeThickness = wedgeThickness;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.simulateDiffractionBlur = simulateDiffractionBlur;
	}


	/**
	 * As simpler constructor without derivative control
	 * @param ocularPlaneNormal
	 * @param eyePosition
	 * @param ocularPlaneCentre
	 * @param rotationAngle
	 * @param rotationAxisDirection
	 * @param magnificationFactor
	 * @param periodVector1
	 * @param periodVector2
	 * @param viewObject
	 * @param refractiveIndex
	 * @param wedgeThickness
	 * @param surfaceTransmissionCoefficient
	 * @param simulateDiffractionBlur
	 * @param maxStepsInArray
	 * @param boundingBox
	 * @param scene
	 */
	public SurfaceOfRefractiveViewRotator_Copy(
			Vector3D ocularPlaneNormal,
			Vector3D eyePosition,
			Vector3D ocularPlaneCentre,
			double rotationAngle,
			Vector3D rotationAxisDirection,
			double magnificationFactor,
			Vector3D periodVector1,
			Vector3D periodVector2,
			SceneObject viewObject,
			double refractiveIndex,
			double wedgeThickness,
			double surfaceTransmissionCoefficient,
			boolean simulateDiffractionBlur,
			int maxStepsInArray,
			SceneObject boundingBox,
			SceneObject scene
			) {

		this(	ocularPlaneNormal,
				eyePosition,
				ocularPlaneCentre,
				rotationAngle,
				rotationAxisDirection,
				magnificationFactor,
				periodVector1,
				periodVector2,
				viewObject,
				DerivativeControlType.NONE,
				0,
				0,
				refractiveIndex,
				wedgeThickness,
				surfaceTransmissionCoefficient,
				simulateDiffractionBlur,
				maxStepsInArray,
				boundingBox,
				scene);
	}

	public SurfaceOfRefractiveViewRotator_Copy(SurfaceOfRefractiveViewRotator_Copy o)
	{
		this(				
				o.getOcularPlaneNormal(),
				o.getEyePosition(),
				o.getOcularPlaneCentre(),
				o.getRotationAngle(),
				o.getRotationAxisDirection(),
				o.getMagnificationFactor(),
				o.getPeriodVector1(),
				o.getPeriodVector2(),
				o.getViewObject(),
				o.getDerivativeControlType(),
				o.getDerivativeControlThickness(),
				o.getDerivativeControlRotation(),
				o.getRefractiveIndex(),
				o.getWedgeThickness(),
				o.getSurfaceTransmissionCoefficient(),
				o.isSimulateDiffractionBlur(),
				o.getMaxStepsInArray(),
				o.getBoundingBox(),
				o.getScene()
				);
	}

	@Override
	public SurfaceOfRefractiveViewRotator_Copy clone() {
		return new SurfaceOfRefractiveViewRotator_Copy(this);
	}

	/**
	 * Setters and getters
	 */
	public Vector3D getOcularPlaneNormal() {
		return ocularPlaneNormal;
	}

	public void setOcularPlaneNormal(Vector3D ocularPlaneNormal) {
		this.ocularPlaneNormal = ocularPlaneNormal;
	}

	public Vector3D getEyePosition() {
		return eyePosition;
	}

	public void setEyePosition(Vector3D eyePosition) {
		this.eyePosition = eyePosition;
	}

	public SceneObject getViewObject() {
		return viewObject;
	}

	public void setViewObject(SceneObject viewObject) {
		this.viewObject = viewObject;
	}

	public DerivativeControlType getDerivativeControlType() {
		return derivativeControlType;
	}

	public void setDerivativeControlType(DerivativeControlType derivativeControlType) {
		this.derivativeControlType = derivativeControlType;
	}

	public double getDerivativeControlThickness() {
		return derivativeControlThickness;
	}

	public void setDerivativeControlThickness(double derivativeControlThickness) {
		this.derivativeControlThickness = derivativeControlThickness;
	}

	public double getDerivativeControlRotation() {
		return derivativeControlRotation;
	}


	public void setDerivativeControlRotation(double derivativeControlRotation) {
		this.derivativeControlRotation = derivativeControlRotation;
	}


	public Vector3D getRotationAxisDirection() {
		return rotationAxisDirection;
	}

	public void setRotationAxisDirection(Vector3D rotationAxisDirection) {
		this.rotationAxisDirection = rotationAxisDirection.getNormalised();
	}

	public Vector3D getOcularPlaneCentre() {
		return ocularPlaneCentre;
	}

	public void setOcularPlaneCentre(Vector3D ocularPlaneCentre) {
		this.ocularPlaneCentre = ocularPlaneCentre;
	}

	public double getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(double rotationAngle) {
		this.rotationAngle = rotationAngle;
	}

	public double getMagnificationFactor() {
		return magnificationFactor;
	}

	public void setMagnificationFactor(double magnificationFactor) {
		this.magnificationFactor = magnificationFactor;
	}

	public Vector3D getPeriodVector1() {
		return periodVector1;
	}

	public void setPeriodVector1(Vector3D periodVector1) {
		this.periodVector1 = periodVector1;
	}

	public Vector3D getPeriodVector2() {
		return periodVector2;
	}

	public void setPeriodVector2(Vector3D periodVector2) {
		this.periodVector2 = periodVector2;
	}

	@Override
	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public double getWedgeThickness() {
		return wedgeThickness;
	}

	public void setWedgeThickness(double wedgeThickness) {
		this.wedgeThickness = wedgeThickness;
	}


	@Override
	public double getSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}

	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}

	public int getMaxStepsInArray() {
		return maxStepsInArray;
	}
	public void setMaxStepsInArray(int maxStepsInArray) {
		this.setMaxStepsInArray(maxStepsInArray);
	}

	public boolean isSimulateDiffractionBlur() {
		return simulateDiffractionBlur;
	}

	public void setSimulateDiffractionBlur(boolean simulateDiffractionBlur) {
		this.simulateDiffractionBlur = simulateDiffractionBlur;
	}



	boolean shadowThrowing = false;

	@Override
	public SceneObject getSceneObjectsInPixel(int[] voxelIndices)
	{
		//create a scene object collection to which the surfaces can be added
		SceneObjectContainer pixelScene = new SceneObjectContainer("Pixel "+voxelIndices[0]+","+voxelIndices[1], null, null);

		// calculate the direction of a ray from the eye that has passed through the ocular surface through the centre of the pixel
		Vector3D ocularPixelSurfaceCentre = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(voxelIndices[0]), periodVector2.getProductWith(voxelIndices[1]));
		Vector3D dOcular = Vector3D.difference(ocularPixelSurfaceCentre, eyePosition).getNormalised();

		// define the outwards-facing (i.e. towards the eye) surface normal of the ocular plane to be the same ass the eye pixel direction
		Vector3D ocularPlaneNormalOutwards = dOcular.getWithLength(-1);

		//define the centre of the wedge surface, as this may change when adding derivative control...
		Vector3D ocularWedgeCentre =Vector3D.sum(ocularPixelSurfaceCentre, ocularPlaneNormalOutwards.getProductWith(-derivativeControlThickness)); 

		// to get the right thickness of the pixel wedge at the centre, calculate the position where the ray would...
		Vector3D objectiveWedgeSurfaceCentre = Vector3D.sum(ocularWedgeCentre, dOcular.getProductWith(wedgeThickness));

		// also calculate the position where we want that ray from the objective pixel surface centre to go
		Vector3D dRotated = Geometry.rotate(dOcular, rotationAxisDirection, MyMath.deg2rad(rotationAngle));
		Vector3D dRotatedMag = Vector3D.sum(dRotated.getPartParallelTo(rotationAxisDirection), dRotated.getPartPerpendicularTo(rotationAxisDirection).getProductWith(1/magnificationFactor));
		RaySceneObjectIntersection intersection = viewObject.getClosestRayIntersection(new Ray(eyePosition, dRotatedMag, 0, false));
		if(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
		{   //System.out.println(intersection);
			// there is an intersection -- good!

			// calculate the ray direction on the other side...
			Vector3D dObjective = Vector3D.difference(intersection.p, objectiveWedgeSurfaceCentre).getNormalised();

			// calculate the normal of the refractive surface that turns dOcular into dObjective
			Vector3D nObjective = Vector3D.difference(dOcular.getProductWith(refractiveIndex), dObjective);

			// ... and make sure it faces outwards
			nObjective = nObjective.getWithLength(Math.signum(Vector3D.scalarProduct(dObjective, nObjective)));
			//System.out.println(nObjective);
			//create the refractive surface property

			//Extend the derivative control here, with both phase holograms and refractive surfaces... we place these in front of the wedge...
			double compThick = (derivativeControlThickness - 2*MyMath.TINY)/2;
			Vector3D pp1 = ocularPixelSurfaceCentre; //Vector3D.sum(ocularPixelSurfaceCentre,dOcular.getProductWith(-Math.abs(derivativeControlThickness)));
			Vector3D pp2 = Vector3D.sum(pp1,dOcular.getProductWith(compThick));
			Vector3D pp3 = Vector3D.sum(pp2,dOcular.getProductWith(MyMath.TINY));
			Vector3D pp4 = Vector3D.sum(pp3,dOcular.getProductWith(compThick));
			Vector3D[] principalPoints = {pp1,pp2,pp3,pp4};
			Vector3D gradientAxis = periodVector1.getPartPerpendicularTo(dOcular).getNormalised();
			Vector3D rotatedGradientAxis = Geometry.rotate(gradientAxis, dOcular, -0.5*MyMath.deg2rad(derivativeControlRotation)).getNormalised();

			
			for(int i = 0; i<=3; i++) {
				IdealThinCylindricalLensSurfaceSimple itlSurface = new IdealThinCylindricalLensSurfaceSimple(
						principalPoints[i],// lensCentre,
						dOcular,// opticalAxisDirection,
						(i<=1)? gradientAxis:rotatedGradientAxis,
								compThick/2,// focalLength,
								surfaceTransmissionCoefficient,// transmissionCoefficient,
								shadowThrowing//shadowThrowing
						);
				//System.out.println("compThick"+compThick);

				//System.out.println("gradient axis"+((i<=1)? gradientAxis: Geometry.rotate(gradientAxis, ocularPlaneNormalOutwards, MyMath.deg2rad(rotationAngle/2)).getNormalised()));
				
				pixelScene.addSceneObject(new Plane("itl "+(i+1), principalPoints[i], ocularPlaneNormalOutwards, itlSurface //new Transparent()//
						, getBoundingBox(), getBoundingBox().getStudio()));
			//if(Vector3D.scalarProduct(rotatedGradientAxis, dOcular)>0.00000001)System.out.println(Vector3D.scalarProduct(rotatedGradientAxis, dOcular));
			}

		}
		else
		{
			// there is no intersection
			(new RayTraceException("No intersection between the rotated ray and the view object!?")).printStackTrace();
		}

		return pixelScene;
	}





	@Override
	public BoundingBoxSurface getBoundingBoxSurface(
			int voxellationIndicesOnInside[],
			SceneObject scene,
			int traceLevel
			)
	{
		return new BoundingBoxSurfaceForRefractiveComponent(scene, voxellationIndicesOnInside, this, traceLevel);
	}


	public static SetOfSurfaces[] createVoxellations(Vector3D periodVector1, Vector3D periodVector2, Vector3D ocularPlaneCentre, Vector3D ocularPlaneNormal, Vector3D eyePosition)
	{
		SetOfSurfaces voxellations[] = new SetOfSurfaces[2];

		Vector3D pointOnPlane0 = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(0.5), periodVector2.getProductWith(0.5));
		Vector3D pointOnplane1 = Vector3D.sum(pointOnPlane0, periodVector1, periodVector2);
		//point where all planes meet to give the optimum voxel shape. 
		Vector3D commonIntersectionPoint = eyePosition;

		voxellations[0] = new FanOfPlanes(
				commonIntersectionPoint,// c1, 
				commonIntersectionPoint.getSumWith(periodVector2),// c2, 
				pointOnPlane0,// p0, 
				pointOnplane1 //p1
				);

		voxellations[1] = new FanOfPlanes(
				commonIntersectionPoint,// c1, 
				commonIntersectionPoint.getSumWith(periodVector1),// c2, 
				pointOnPlane0,// p0,
				pointOnplane1 //p1
				);


		//System.out.println(commonIntersectionPoint);
		return voxellations;
	}


	@Override
	public boolean isShadowThrowing() {
		return false;
	}

	@Override
	public boolean isSimulateDiffraction()
	{
		return simulateDiffractionBlur;
	}

	@Override
	public double getLambda()
	{
		return 550e-9;	// green
	}

	@Override
	public double getPixelSideLengthU()
	{
		return periodVector1.getLength();
	}

	@Override
	public double getPixelSideLengthV()
	{
		return periodVector2.getLength();
	}

	@Override
	public Vector3D getuHat()
	{
		return periodVector1.getNormalised();
	}

	@Override
	public Vector3D getvHat()
	{
		return periodVector2.getNormalised();
	}

}
