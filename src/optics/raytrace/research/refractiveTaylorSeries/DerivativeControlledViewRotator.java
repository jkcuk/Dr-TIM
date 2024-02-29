package optics.raytrace.research.refractiveTaylorSeries;

import java.util.stream.DoubleStream;

import math.MyMath;
import math.Vector3D;

import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.Parallelepiped2;



/**
 * A view rotator which consists of a series of holograms. The ocular holograms are used to control the derivative,
 * while the last objective hologram is designed to mimic the refractiveViewRotator.
 * 
 */
public class DerivativeControlledViewRotator extends Parallelepiped2 
{
	private static final long serialVersionUID = 5154115585874023966L;
	
	private SurfaceOfDerivativeControlledViewRotator surface;


	public DerivativeControlledViewRotator(
			String description,
			Vector3D height,
			Vector3D width,
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
			SceneObject scene,
			SceneObject parent, 
			Studio studio
		)
	
	{
		// first create the bounding box...
		super(		"Bounding box of view rotator",	// description
				getBoundingBoxVectors( height,  width, 
						dz, componentNormal, ocularPlaneCentre, periodVector1, periodVector2, 
						addWedgeHologram, parent, studio )[0],//Centre
				getBoundingBoxVectors( height,  width, 
						dz, componentNormal, ocularPlaneCentre, periodVector1, periodVector2, 
						addWedgeHologram, parent, studio )[1],//depth
				height,//height
				width,//width
				null,	// surfaceProperty -- for now;
				parent,//parent,
				studio//studio

				);
		// ... then set its surface property to be a suitable hologram array
		surface = new SurfaceOfDerivativeControlledViewRotator(
				componentNormal,// componentNormal,
				eyePosition,// eyePosition,
				ocularPlaneCentre,// ocularPlaneCentre,
				rotationAngle,// rotationAngle,
				rotationAxisDirection,// rotationAxisDirection,
				magnificationFactor,// magnificationFactor,
				addWedgeHologram,// addWedgeHologram,
				periodVector1,// periodVector1,
				periodVector2,// periodVector2,
				viewObject,// viewObject,
				noOfSurfaces,// noOfSurfaces, 
				polynomialOrder,// polynomialOrder, 
				dz,// dz, 
				a,//a,
				surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
				simulateDiffractionBlur,// simulateDiffractionBlur,
				maxStepsInArray,// maxStepsInArray,
				this,// boundingBox,
				scene// scene
			);
		setSurfaceProperty(surface);
		
	}
	
	/**
	 * @param original
	 */
	public DerivativeControlledViewRotator(DerivativeControlledViewRotator o)
	{
		this(
				o.getDescription(),
				o.getHeight(),
				o.getWidth(),
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
				o.getScene(),
				o.getParent(), 
				o.getStudio()
				
			);
	}
	
	//getters and setters
	public Vector3D getHeight() {
		return super.getV();
	}

	public void setHeight(Vector3D height) {
		super.setV(height);
	}

	public Vector3D getWidth() {
		return super.getW();
	}

	public void setWidth(Vector3D width) {
		super.setW(width);
	}
	public Vector3D getOcularPlaneCentre() {
		return surface.getOcularPlaneCentre();
	}
	
	public void setOcularPlaneCentre(Vector3D ocularPlaneCentre) {
		surface.setOcularPlaneCentre(ocularPlaneCentre);
	}
	

	public Vector3D getComponentNormal() {
		return surface.getComponentNormal();
	}

	public void setComponentNormal(Vector3D componentNormal) {
		surface.setComponentNormal(componentNormal);
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

	public boolean isAddWedgeHologram() {
		return surface.isAddWedgeHologram();
	}
	
	public void setAddWedgeHologram(boolean addWedgeHologram) {
		surface.setAddWedgeHologram(addWedgeHologram);
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
	
	public double getSurfaceTransmissionCoefficient() {
		return surface.getSurfaceTransmissionCoefficient();
	}
	
	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		surface.setSurfaceTransmissionCoefficient(surfaceTransmissionCoefficient);
	}
	
	public SceneObject getViewObject() {
		return surface.getViewObject();
	}
	
	public int getNoOfSurfaces() {
		return surface.getNoOfSurfaces();
	}
	
	public void setNoOfSurfaces(int noOfSurfaces) {
		surface.setNoOfSurfaces(noOfSurfaces);
	}
	
	public int getPolynomialOrder() {
		return surface.getPolynomialOrder();
	}
	
	public void setPolynomialOrder(int polynomialOrder) {
		surface.setPolynomialOrder(polynomialOrder);
	}
	
	public double[] getDz() {
		return surface.getDz();
	}
	
	public void setDz(double[] dz) {
		surface.setDz(dz);
	}
	
	public double[][][] getA(){
		return surface.getA();
	}
	
	public void setA(double[][][] a) {
		surface.setA(a);
	}
	
	public void setViewObject(SceneObject viewObject) {
		surface.setViewObject(viewObject);
	}

	public boolean isShadowThrowing() {
		return surface.isShadowThrowing();
	}
	
	public int getMaxStepsInArray() {
		return surface.getMaxStepsInArray();
	}
	public void setMaxStepsInArray(int MaxStepsInArray) {
		surface.setMaxStepsInArray(MaxStepsInArray);
	}
	
	public boolean isSimulateDiffractionBlur() {
		return surface.isSimulateDiffractionBlur();
	}
	
	public void setSimulateDiffractionBlur(boolean simulateDiffractionBlur) {
		surface.setSimulateDiffractionBlur(simulateDiffractionBlur);
	}	

	public SceneObject getScene()
	{
		return surface.getScene();
	}
	
//Create an automatic bounding box which encompasses all the components. 
	public static Vector3D[] getBoundingBoxVectors(Vector3D height, Vector3D width, 
			double[] dz, Vector3D componentNormal, Vector3D ocularPlaneCentre, Vector3D periodVector1, Vector3D periodVector2, 
			boolean addWedgeHologram, SceneObject parent, Studio studio ) {

		//Calculate the "length" of a pixel by using the sum of the separation between the holograms.
		double sumOfdz = DoubleStream.of(dz).sum();

		//Check if there is a "shifting" hologram added in, which is required for the rotation to full occur. If it does, add a math.TINY thickness to the previous result.
		double centralThickness = (addWedgeHologram) ? sumOfdz+MyMath.TINY : sumOfdz;
		
		Vector3D centre = Vector3D.sum(ocularPlaneCentre, componentNormal.getWithLength(centralThickness/2));	// centre
		Vector3D depth = componentNormal.getWithLength(centralThickness+Math.sqrt(periodVector1.getModSquared()+periodVector2.getModSquared()+1));	// adding the diagonal pixel period
		//
		
		
		return new Vector3D[] {centre, depth, height, width};
	}
	
	@Override
	public String getType()
	{
		return "Derivatice Control View Rotator";
	}

}
