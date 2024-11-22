package optics.raytrace.surfaces;

import math.Geometry;
import math.MathException;
import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.diffraction.SingleSlitDiffraction;
import optics.raytrace.voxellations.FanOfPlanes;
import optics.raytrace.voxellations.Voxellation;

/*
 * The surface of a component which rotates the view seen through it by a defined angle using a refractive surface.
 * 
 */
public class SurfaceOfRefractiveViewRotatorOldVoxellation extends SurfaceOfRefractiveComponentArray
{
	private static final long serialVersionUID = 1616722922232355738L;

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
	 * To simulate diffraction
	 */
	private boolean simulateDiffractiveBlur;
	private double lambda; 

	/**
	 * transmission coefficient of each of the lens surfaces
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the lens surfaces throw shadows
	 */
	private boolean shadowThrowing;
	

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
 * @param refractiveIndex
 * @param wedgeThickness
 * @param boundingBox
 * @param surfaceTransmissionCoefficient
 * @param shadowThrowing
 * @param maxSteps
 */
	
	public SurfaceOfRefractiveViewRotatorOldVoxellation(
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
			SceneObject boundingBox,
			boolean simulateDiffractiveBlur,
			double lambda,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			int maxSteps
		)
	{	
	super(
			createVoxellations(periodVector1, periodVector2, ocularPlaneCentre, ocularPlaneNormal, eyePosition, refractiveIndex),
			boundingBox, maxSteps, 1., shadowThrowing//Voxellation
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
	this.refractiveIndex = refractiveIndex;
	this.wedgeThickness = wedgeThickness;
	this.lambda = lambda;
	this.simulateDiffractiveBlur =simulateDiffractiveBlur;
	this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	this.shadowThrowing =shadowThrowing;
	}


	
	
	
	public SurfaceOfRefractiveViewRotatorOldVoxellation(SurfaceOfRefractiveViewRotatorOldVoxellation o)
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
				o.getRefractiveIndex(),
				o.getWedgeThickness(),
				o.getSurface(),
				o.isSimulateDiffractiveBlur(),
				o.getLambda(),
				o.getSurfaceTransmissionCoefficient(),
				o.isShadowThrowing(),
				o.getMaxSteps()
			);
	}

	@Override
	public SurfaceOfRefractiveComponentArray clone() {
		return new SurfaceOfRefractiveViewRotatorOldVoxellation(this);
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
	
	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
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
	
	@Override
public SceneObject getRefractiveComponent(int[] voxelIndices) {
		//create a scene object collection to which the refractive surfaces can be added
		SceneObjectPrimitiveIntersection c = new SceneObjectPrimitiveIntersection("Pixel "+voxelIndices[0]+","+voxelIndices[1], null, null);
		
		// calculate the direction of a ray from the eye that has passed through the ocular surface through the centre of the pixel
		Vector3D ocularPixelSurfaceCentre = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(voxelIndices[0]), periodVector2.getProductWith(voxelIndices[1]));
		Vector3D dOcular = Vector3D.difference(ocularPixelSurfaceCentre, eyePosition).getNormalised();
		
		// calculate the outwards-facing (i.e. towards the eye) surface normal of the ocular plane
		Vector3D ocularPlaneNormalOutwards = ocularPlaneNormal.getWithLength(-Math.signum(Vector3D.scalarProduct(dOcular, ocularPlaneNormal)));
		
		// refract this direction into the pixel
		Vector3D dInside;
		try {
			dInside = RefractiveSimple.getRefractedLightRayDirection(dOcular, ocularPlaneNormalOutwards, 1/refractiveIndex).getNormalised();
		} catch (EvanescentException e) {
			(new RayTraceException("Evanescence when there should be none!?")).printStackTrace();
			return c;
		}
		
		//dInside = dOcular.getProductWith(refractiveIndex).getDifferenceWith(ocularPlaneNormalOutwards);
		//System.out.println("dInside "+dInside);
		
		
		// to get the right thickness of the pixel wedge at the centre, calculate the position where the ray would...
		Vector3D objectivePixelSurfaceCentre;
		try {
			objectivePixelSurfaceCentre = Geometry.linePlaneIntersection(
					ocularPixelSurfaceCentre,	// point on line
					dInside,	// direction of line
					Vector3D.sum(ocularPixelSurfaceCentre, ocularPlaneNormalOutwards.getProductWith(-wedgeThickness)),	// point on plane
					ocularPlaneNormal
				);
		} catch (MathException e) {
			e.printStackTrace();
			return c;
		}
		
		// also calculate the position where we want that ray from the objective pixel surface centre to go
		Vector3D dRotated = Geometry.rotate(dOcular, rotationAxisDirection, MyMath.deg2rad(rotationAngle));
		Vector3D dRotatedAndScaled = Vector3D.sum(
				dRotated.getPartParallelTo(rotationAxisDirection),
				dRotated.getPartPerpendicularTo(rotationAxisDirection).getProductWith(1/magnificationFactor)
				);
		RaySceneObjectIntersection intersection = viewObject.getClosestRayIntersection(new Ray(eyePosition, dRotatedAndScaled, 0, false));
		if(intersection != RaySceneObjectIntersection.NO_INTERSECTION)
		{   //System.out.println(intersection);
			// there is an intersection -- good!
		
			// calculate the ray direction on the other side...
			Vector3D dObjective = Vector3D.difference(intersection.p, objectivePixelSurfaceCentre).getNormalised();			
			
			// calculate the normal of the refractive surface that turns dInside into dObjective
			Vector3D nObjective = Vector3D.difference(dInside.getProductWith(refractiveIndex), dObjective);
			
			// ... and make sure it faces outwards
			nObjective = nObjective.getWithLength(Math.signum(Vector3D.scalarProduct(dObjective, nObjective)));
			//System.out.println(nObjective);
			//create the refractive surface property
			RefractiveSimple surfaceN = new RefractiveSimple(refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);
			
			c.addPositiveSceneObjectPrimitive(new Plane(
					"ocular surface",
					ocularPixelSurfaceCentre,// pointOnPlane,
					ocularPlaneNormalOutwards,// normal, 
					surfaceN,//
					getBoundingBox(),// parent,
					getBoundingBox().getStudio()// studio
					));
			
			c.addPositiveSceneObjectPrimitive(new Plane(
					"objective surface",
					objectivePixelSurfaceCentre,// pointOnPlane,
					nObjective,// normal, 
					surfaceN,//
					getBoundingBox(),// parent,
					getBoundingBox().getStudio()// studio
					));			
		}
		else
		{
			// there is no intersection
			(new RayTraceException("No intersection between the rotated ray and the view object!?")).printStackTrace();
		}
		
		return c;
	}
	
	//Add a diffractive blur for a given pixel span vector
	@Override
	public DoubleColour getColourUponLeavingVolume(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int stepsLeft, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException {
		
		if(simulateDiffractiveBlur)
		{	//
			Vector3D lightRayDirectionChange = SingleSlitDiffraction.getTangentialDirectionComponentChange(
				lambda,
				periodVector1.getLength(),	// pixelSideLengthU
				periodVector2.getLength(),	// pixelSideLengthV
				periodVector1.getNormalised(),	// uHat
				periodVector2.getNormalised()	// vHat
				);
			r.setD(Vector3D.sum(r.getD(), lightRayDirectionChange));
		}
		return super.getColourUponLeavingVolume(r, i, scene, l, stepsLeft, traceLevel, raytraceExceptionHandler);
	}
	
	

	public static Voxellation[] createVoxellations(Vector3D periodVector1, Vector3D periodVector2, Vector3D ocularPlaneCentre, Vector3D ocularPlaneNormal, Vector3D eyePosition, double refractiveIndex)
	{
		Voxellation voxellations[] = new Voxellation[2];
		
		Vector3D pointOnPlane0 = Vector3D.sum(ocularPlaneCentre, periodVector1.getProductWith(0.5), periodVector2.getProductWith(0.5));
		Vector3D pointOnplane1 = Vector3D.sum(pointOnPlane0, periodVector1, periodVector2);
		//point where all planes meet to give the optimum voxel shape. 
		Vector3D dCentreToEye = Vector3D.difference(eyePosition, ocularPlaneCentre);
		Vector3D commonIntersectionPoint = Vector3D.sum(ocularPlaneCentre, dCentreToEye.getProductWith(refractiveIndex));
			
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
	
}
