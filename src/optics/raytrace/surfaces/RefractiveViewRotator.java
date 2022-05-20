package optics.raytrace.surfaces;

import math.Vector3D;

import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.Parallelepiped2;



/**
 * A view rotator which consists of refractive wedges to piecewise translates an image to mimic a rotation.
 * The rotator is surrounded by a bounding box (Parallelepiped) which is the scene object and handles the raytracing through it's surface property. 
 * The bounding box size is (TODO for now) determined by the user and as such, it needs to be made sure that it fits around the device.
 */
public class RefractiveViewRotator extends Parallelepiped2 
{
	private static final long serialVersionUID = 3339913072437353313L;
	
	
	private SurfaceOfRefractiveViewRotator surface;

/**
 * 
 * @param description
 * @param boundingBoxCentre
 * @param boundingBoxSpanVector1
 * @param boundingBoxSpanVector2
 * @param boundingBoxSpanVector3
 * @param ocularSurfaceNormal vector to define all front surface normals, should be orientated from the device to the viewing position
 * @param eyePosition
 * @param ocularPlaneCentre
 * @param pointOnRotationAxis
 * @param rotationAngle
 * @param periodVector1
 * @param periodVector2
 * @param refractiveIndex
 * @param wedgeThickness
 * @param maxSteps
 * @param surfaceTransmissionCoefficient
 * @param shadowThrowing
 * @param parent
 * @param studio
 */
	public RefractiveViewRotator(
			String description,
			Vector3D boundingBoxCentre, 
			Vector3D boundingBoxSpanVector1,
			Vector3D boundingBoxSpanVector2,
			Vector3D boundingBoxSpanVector3,
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
			boolean simulateDiffractiveBlur,
			double lambda,
			int maxSteps, 
			double surfaceTransmissionCoefficient, 
			boolean shadowThrowing,
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
		surface = new SurfaceOfRefractiveViewRotator(
				ocularPlaneNormal,
				eyePosition,
				ocularPlaneCentre,
				rotationAngle,
				rotationAxisDirection,
				magnificationFactor,
				periodVector1,
				periodVector2,
				viewObject,
				refractiveIndex,
				wedgeThickness,
				this,	// bounding box
				simulateDiffractiveBlur,
				lambda,
				surfaceTransmissionCoefficient,
				shadowThrowing,
				maxSteps
			);
		setSurfaceProperty(surface);
		
	}
	
	/**
	 * @param original
	 */
	public RefractiveViewRotator(RefractiveViewRotator original)
	{
		this(
				original.getDescription(),
				original.getBoundingBoxCentre(), 
				original.getBoundingBoxSpanVector1(),
				original.getBoundingBoxSpanVector2(),
				original.getBoundingBoxSpanVector3(),
				original.getOcularPlaneNormal(),
				original.getEyePosition(),
				original.getOcularPlaneCentre(),
				original.getRotationAngle(),
				original.getRotationAxisDirection(),
				original.getMagnificationFactor(),
				original.getPeriodVector1(),
				original.getPeriodVector2(),
				original.getViewObject(),
				original.getRefractiveIndex(),
				original.getWedgeThickness(),
				original.isSimulateDiffractiveBlur(),
				original.getLambda(),
				original.getMaxSteps(), 
				original.getSurfaceTransmissionCoefficient(), 
				original.isShadowThrowing(),
				original.getParent(), 
				original.getStudio()

			);	
	}
	
	//getters and setters

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
	
	public Vector3D getOcularPlaneCentre() {
		return surface.getOcularPlaneCentre();
	}
	
	public void setOcularPlaneCentre(Vector3D ocularPlaneCentre) {
		surface.setOcularPlaneCentre(ocularPlaneCentre);
	}
	

	public Vector3D getOcularPlaneNormal() {
		return surface.getOcularPlaneNormal();
	}

	public void setOcularPlaneNormal(Vector3D ocularPlaneNormal) {
		surface.setOcularPlaneNormal(ocularPlaneNormal);
	}

	public Vector3D getEyePosition() {
		return surface.getEyePosition();
	}

	public void setEyePosition(Vector3D eyePosition) {
		surface.setEyePosition(eyePosition);
	}


	public double getRotationAngle() {
		return surface.getRotationAngle();
	}

	public void setRotationAngle(double rotationAngle) {
		surface.setRotationAngle(rotationAngle);
	}

	public Vector3D getRotationAxisDirection() {
		return surface.getRotationAxisDirection();
	}

	public void setRotationAxisDirection(Vector3D rotationAxisDirection) {
		surface.setRotationAxisDirection(rotationAxisDirection);
	}
	
	public double getMagnificationFactor() {
		return surface.getMagnificationFactor();
	}

	public void setMagnificationFactor(double magnificationFactor) {
		surface.setMagnificationFactor(magnificationFactor);
	}

	public Vector3D getPeriodVector1() {
		return surface.getPeriodVector1();
	}

	public void setPeriodVector1(Vector3D periodVector1) {
		surface.setPeriodVector1(periodVector1);
	}
	
	public Vector3D getPeriodVector2() {
		return surface.getPeriodVector2();
	}

	public void setPeriodVector2(Vector3D periodVector2) {
		surface.setPeriodVector2(periodVector2);
	}
	
	public double getWedgeThickness() {
		return surface.getWedgeThickness();
	}

	public void setWedgeThickness(double wedgeThickness) {
		surface.setWedgeThickness(wedgeThickness);
	}
	
	
	public SceneObject getViewObject() {
		return surface.getViewObject();
	}

	public void setViewObject(SceneObject viewObject) {
		surface.setViewObject(viewObject);
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

	public boolean isSimulateDiffractiveBlur() {
		return surface.isSimulateDiffractiveBlur();
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		surface.setSimulateDiffractiveBlur(simulateDiffractiveBlur);
	}
	
	
	public double getLambda() {
		return surface.getLambda();
	}

	public void setLambda(double lambda) {
		surface.setLambda(lambda);
	}
	
	public boolean isShadowThrowing() {
		return surface.isShadowThrowing();
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		surface.setShadowThrowing(shadowThrowing);
	}
	
	
	@Override
	public String getType()
	{
		return "Refractive View Rotator";
	}

}
