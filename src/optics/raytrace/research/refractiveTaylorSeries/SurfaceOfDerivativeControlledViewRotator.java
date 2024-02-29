package optics.raytrace.research.refractiveTaylorSeries;

import java.util.stream.DoubleStream;

import math.Geometry;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfSimplePrism;
import optics.raytrace.surfaces.surfaceOfPixelArray.SurfaceOfPixelArray;
import optics.raytrace.voxellations.FanOfPlanes;
import optics.raytrace.voxellations.SetOfSurfaces;

/*
 *A surface property which uses a series of holograms in each pixel to create an integral rotated image. By using multiple holograms, the derivatives of the rotator can be controlled. 
 */
public class SurfaceOfDerivativeControlledViewRotator extends SurfaceOfPixelArray
{
	private static final long serialVersionUID = -8990682698875445695L;

	/**
	 * The centre of the ocular plane of the component
	 */
	private Vector3D ocularPlaneCentre;
	
	/**
	 * Normal of the planes which form the ocular and objective surface of the component which should be the same.
	 */
	private Vector3D componentNormal;
	
	/**
	 * Position from which the view of the surface of the view object through the component appear rotated and scaled 
	 */
	private Vector3D eyePosition;
	
	/**
	 * the scene object whose surface appears distorted when seen from the eye position through the component
	 */
	private SceneObject viewObject;
	
	/**
	 * The rotation axis
	 */
	private Vector3D rotationAxisDirection;
	
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
	 * If true, we are adding the last hologram as a wedge, that way it should be possible to have an apparent rotation. If false, nothing is added
	 */
	private boolean addWedgeHologram;
	
	/**
	 * The total number of derivative control surface holograms
	 */
	private int noOfSurfaces;
	
	/**
	 * The polynomial order of the holograms
	 */
	private int polynomialOrder;

	/**
	 * The separation between holograms, {dz[0], dz[1], dz[2], ...} where dz[i] is the z separation between hologram  #i-1 and hologram #i
	 */
	private double[] dz;
	
	/**
	 * The coeficents of the holograms
	 */
	private double[][][] a;
	
	/**
	 * The period and direction vectors in the ocular plane for the voxellation (pixels)
	 */
	private Vector3D periodVector1, periodVector2;
	
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

	
	
	public SurfaceOfDerivativeControlledViewRotator(
			Vector3D componentNormal,
			Vector3D eyePosition,
			Vector3D ocularPlaneCentre,
			double rotationAngle,
			Vector3D rotationAxisDirection,
			double magnificationFactor,
			boolean addWedgeHologram,
			Vector3D periodVector1,
			Vector3D periodVector2,
			SceneObject viewObject,
			int noOfSurfaces, 
			int polynomialOrder, 
			double[] dz, 
			double[][][] a,
			double surfaceTransmissionCoefficient,
			boolean simulateDiffractionBlur,
			int maxStepsInArray,
			SceneObject boundingBox,
			SceneObject scene
		)
	{	
	super(
			createVoxellations(periodVector1, periodVector2, ocularPlaneCentre, eyePosition),
			boundingBox,//getBoundingBox(height, width, dz, getInwardNormal(componentNormal, ocularPlaneCentre, eyePosition), ocularPlaneCentre, periodVector1, periodVector2, addWedgeHologram),
			scene,
			maxStepsInArray
		);
	this.componentNormal = getInwardNormal(componentNormal, ocularPlaneCentre, eyePosition);
	this.eyePosition=eyePosition;
	this.ocularPlaneCentre=ocularPlaneCentre;
	this.rotationAxisDirection =rotationAxisDirection;
	this.magnificationFactor = magnificationFactor;
	this.rotationAngle=rotationAngle;
	this.addWedgeHologram = addWedgeHologram;
	this.periodVector1=periodVector1;
	this.periodVector2=periodVector2;
	this.noOfSurfaces = noOfSurfaces;
	this.polynomialOrder = polynomialOrder;
	this.dz = dz;
	this.a = a;
	this.viewObject = viewObject;
	this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	this.simulateDiffractionBlur = simulateDiffractionBlur;
	}
	
	public SurfaceOfDerivativeControlledViewRotator(SurfaceOfDerivativeControlledViewRotator o)
	{
		this(
				o.getComponentNormal(),
				o.getEyePosition(),
				o.getOcularPlaneCentre(),
				o.getRotationAngle(),
				o.getRotationAxisDirection(),
				o.getMagnificationFactor(),
				o.isAddWedgeHologram(),
				o.getPeriodVector1(),
				o.getPeriodVector2(),
				o.getViewObject(),
				o.getNoOfSurfaces(), 
				o.getPolynomialOrder(), 
				o.getDz(), 
				o.getA(),
				o.getSurfaceTransmissionCoefficient(),
				o.isSimulateDiffractionBlur(),
				o.getMaxStepsInArray(),
				o.getBoundingBox(),
				o.getScene()
			);
	}

	@Override
	public SurfaceOfDerivativeControlledViewRotator clone() {
		return new SurfaceOfDerivativeControlledViewRotator(this);
	}
	
	/**
	 * Setters and getters
	 */
	
	boolean shadowThrowing = false;

	public Vector3D getOcularPlaneCentre() {
		return ocularPlaneCentre;
	}

	public void setOcularPlaneCentre(Vector3D ocularPlaneCentre) {
		this.ocularPlaneCentre = ocularPlaneCentre;
	}

	public Vector3D getComponentNormal() {
		return componentNormal;
	}

	public void setComponentNormal(Vector3D componentNormal) {
		this.componentNormal = componentNormal;
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

	public Vector3D getRotationAxisDirection() {
		return rotationAxisDirection;
	}

	public void setRotationAxisDirection(Vector3D rotationAxisDirection) {
		this.rotationAxisDirection = rotationAxisDirection;
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

	public boolean isAddWedgeHologram() {
		return addWedgeHologram;
	}

	public void setAddWedgeHologram(boolean addWedgeHologram) {
		this.addWedgeHologram = addWedgeHologram;
	}

	public int getNoOfSurfaces() {
		return noOfSurfaces;
	}

	public void setNoOfSurfaces(int noOfSurfaces) {
		this.noOfSurfaces = noOfSurfaces;
	}

	public int getPolynomialOrder() {
		return polynomialOrder;
	}

	public void setPolynomialOrder(int polynomialOrder) {
		this.polynomialOrder = polynomialOrder;
	}

	public double[] getDz() {
		return dz;
	}

	public void setDz(double[] dz) {
		this.dz = dz;
	}

	public double[][][] getA() {
		return a;
	}

	public void setA(double[][][] a) {
		this.a = a;
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
		this.maxStepsInArray = maxStepsInArray;
	}

	public boolean isSimulateDiffractionBlur() {
		return simulateDiffractionBlur;
	}

	public void setSimulateDiffractionBlur(boolean simulateDiffractionBlur) {
		this.simulateDiffractionBlur = simulateDiffractionBlur;
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}

	@Override
	public SceneObject getSceneObjectsInPixel(int[] voxelIndices)
	{
		
		
		//create a scene object collection to which the holographic surfaces can be added
		SceneObjectContainer pixelScene = new SceneObjectContainer("Pixel "+voxelIndices[0]+","+voxelIndices[1], null, null);
		
		

		// calculate the direction of a ray from the eye that has passed through the ocular surface through the centre of the pixel
		Vector3D ocularPixelSurfaceCentre = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(voxelIndices[0]), periodVector2.getProductWith(voxelIndices[1]));//TODO importante  we need to fix and/or understand this (-0.5+) better...
		Vector3D dOcular = Vector3D.difference(ocularPixelSurfaceCentre, eyePosition).getNormalised();
		
//		//Using these we can define the coordinate system in our pixel as  
		Vector3D zHat = dOcular;
		Vector3D xHat = Vector3D.X.getPartPerpendicularTo(dOcular).getNormalised();
		Vector3D yHat = Vector3D.Y.getPartPerpendicularTo(dOcular).getNormalised();
		//This will break down in some extreme cases which we should never reach!
		
		// calculate the position where we want the hologram to redirect the light ray to. Including a magnification factor. 
		Vector3D dRotated = Geometry.rotate(dOcular, rotationAxisDirection, MyMath.deg2rad(rotationAngle));
		Vector3D dRotatedMag = Vector3D.sum(dRotated.getPartParallelTo(rotationAxisDirection), dRotated.getPartPerpendicularTo(rotationAxisDirection).getProductWith(1/magnificationFactor));
		RaySceneObjectIntersection intersection = viewObject.getClosestRayIntersection(new Ray(eyePosition, dRotatedMag, 0, false));
		
		if(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
		{   //System.out.println(intersection);
			// there is an intersection -- good!

			//
			//First we add the derivative control surfaces.
			//
			boolean[] editableDz = new boolean[noOfSurfaces]; //If there is a more elegant way to do this 
			boolean[][][] editableA = new boolean[noOfSurfaces][polynomialOrder][polynomialOrder];
			
			SurfaceParameters derivativeControlSurfaces = new SurfaceParameters(noOfSurfaces, polynomialOrder, dz, a, 
					xHat, yHat, zHat, ocularPixelSurfaceCentre,
					editableDz, editableA);
			
			DirectionChangingSurfaceSequence dcss = derivativeControlSurfaces.createCorrespondingDirectionChangingSurfaceSequence(surfaceTransmissionCoefficient);
			// add the surfaces from the DirectionChangingSurfaceSequence dcss to the scene (these represent the derivative control surfaces)
			for(SceneObjectPrimitive s:dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces())
			{
				pixelScene.addSceneObject(s);
			}
			
			//
			//Creating the hologram surface which represents the wedge
			//
			
			//The wedge phase hologram centre is set to be a distance of MyMath.TINY after the last derivative control hologram.
			Vector3D wedgeCentre = Vector3D.sum(zHat.getWithLength(DoubleStream.of(dz).sum()+MyMath.TINY), ocularPixelSurfaceCentre);
			
			// calculate the ray direction on the other side...
			Vector3D dObjective = Vector3D.difference(intersection.p, wedgeCentre).getNormalised();
			
			//Now creating a simple hologram which has the set properties. It should redirect a ray by applying a phase to it along the u direction. 
			//This direction will simply be the parts perpendicular to the surface normal for the normalised outwards direction.
			Vector3D uHat = dObjective.getPartPerpendicularTo(zHat).getNormalised();
			double uHatMag = dObjective.getPartPerpendicularTo(zHat).getLength();
			
			PhaseHologramOfSimplePrism wedgeSurface = new PhaseHologramOfSimplePrism(
					uHat,// uDirection,
					uHatMag,// magnitude,
					surfaceTransmissionCoefficient,// throughputCoefficient,
					false,// reflective,
					shadowThrowing// shadowThrowing
				);
			
			//Lastly, adding the wedge hologram to the scene after the last of the derivative control surfacecs
			pixelScene.addSceneObject(new Plane(
					"wedge hologram",
					wedgeCentre,// pointOnPlane,
					zHat,// normal, 
					wedgeSurface,//
//					SurfaceColour.YELLOW_MATT,
					getBoundingBox(),// parent,
					getBoundingBox().getStudio()// studio
					),addWedgeHologram);	
			//System.out.println(wedgeCentre);
		}
		else
		{
			// there is no intersection
			(new RayTraceException("No intersection between the rotated ray and the view object!?")).printStackTrace();
		}
		
		return pixelScene;
	}
	
	public static Vector3D getInwardNormal(Vector3D componentNormal, Vector3D ocularPlaneCentre, Vector3D eyePosition) {
		
		return componentNormal.getProductWith(Math.signum(Vector3D.scalarProduct(componentNormal, Vector3D.difference(ocularPlaneCentre, eyePosition))));
	}
	

//Creating the voxellation
	public static SetOfSurfaces[] createVoxellations(Vector3D periodVector1, Vector3D periodVector2, Vector3D ocularPlaneCentre, Vector3D eyePosition)
	{
		SetOfSurfaces voxellations[] = new SetOfSurfaces[2];
		
		Vector3D pointOnPlane0 = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(0.5), periodVector2.getProductWith(0.5));
		Vector3D pointOnPlane1 = Vector3D.sum(pointOnPlane0, periodVector1, periodVector2);
		//point where all planes meet to give the optimum voxels shape is the eye position
			
		voxellations[0] = new FanOfPlanes(
				eyePosition,// c1, 
				eyePosition.getSumWith(periodVector2),// c2,
				pointOnPlane0,// p0, 
				pointOnPlane1 //p1
				);

		voxellations[1] = new FanOfPlanes(
				eyePosition,// c1, 
				eyePosition.getSumWith(periodVector1),// c2, 
				pointOnPlane0,// p0,
				pointOnPlane1 //p1
				);
		
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
