package optics.raytrace.surfaces;

import math.Vector3D;

import optics.raytrace.GUI.sceneObjects.EditableCuboid;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Parallelepiped2;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;

/**
 * A rectangular array of lenslets, which all have the same focal length (to be confocal).
 * This will feature the ability to have a lens window or clear aperture, separate from the centre of the lens.
 * To determine the central position of a specific lens index, periods in the u, v direction (which can be chosen) are used.
 * Two different types are used here, one for the Principal points, one for the lens windows.
 * 
 * @author Maik
 */
public class RefractiveLensletArray extends Parallelepiped2 
{

	private static final long serialVersionUID = -5613189023782562206L;
//	/**
//	 * box params
//	 */
//	private double width;//The width of the box
//	private double height;// the height of the box
//	private double thickness; //the thickness of the box (may be calculated in future to be minimum
//	
//	private Vector3D lensletArrayCentre;
//	private Vector3D centre2centreOfFace1;
//	private Vector3D centre2centreOfFace2;
//	private Vector3D centre2centreOfFace3; //vectors defining the box
//	
//
//	
//	
//	
//	/**
//	 * Setting lenslet params
//	 */
//	SurfaceProperty surfaceLensletArray;
//	
//	//centre of the lens window, this will refer to the initial, central window
//	private Vector3D lensWindowCentre;
//
//	//centre of the lens Principal point, this will refer to the initial Principal point
//	private Vector3D principalPointCentre;
//
//	/**
//	 * setting the direction and length of lenslet windows
//	 */
//	private Vector3D uPeriodWindow;
//	private Vector3D vPeriodWindow;
//	
//	/**
//	 * setting the direction and length of lenslet principal points
//	 */
//	private Vector3D uPeriodPrincipalPoint;
//	private Vector3D vPeriodPrincipalPoint;
//	
//
//
//	//focal length of each lenslet, all the same as they need to be confocal.
//	private double focalLength;
//	
//	//the refractive index of each lenslet
//	private double refractiveIndex;
//	
//	private int maxSteps; 
//	private double transmissionCoefficient; 
//	private boolean shadowThrowing;
	


	/**
	 * Constructor for the lenslet array
	 * 
	 * @param lensWindowCentre
	 * @param PrincipalPointCentre
	 * @param uHat
	 * @param vHat
	 * @param focalLength
	 * @param uPeriodWindows
	 * @param vPeriodWindows
	 * @param uPeriodPrincipalPoints
	 * @param vPeriodPrincipalPoints
//	 * @param simulateDiffractiveBlur
//	 * @param lambda
	 */
	public RefractiveLensletArray(
			//set all the array params
			String description,
			double width,
			double height,
			double thickness,
			Vector3D lensletArrayCentre, 
			//set all the lenslet params
			Vector3D uPeriodWindow, 
			Vector3D vPeriodWindow,
			Vector3D uPeriodPrincipalPoint,
			Vector3D vPeriodPrincipalPoint,
			Vector3D lensWindowCentre, 
			Vector3D principalPointCentre,
			double focalLength, 
			double refractiveIndex, 
			int maxSteps, 
			double transmissionCoefficient, 
			boolean shadowThrowing,
			SceneObject parent, 
			Studio studio
		)
	{
		super(
				"Box containing the lenslet array",// description, 
				lensletArrayCentre, //centre, 
				uPeriodWindow.getWithLength(width), // u, 
				vPeriodWindow.getWithLength(height), // v, 
				Vector3D.crossProduct(uPeriodWindow, vPeriodWindow).getWithLength(thickness), // w, 
				new SurfaceOfGeneralRefractiveCLAs(uPeriodWindow, vPeriodWindow, uPeriodPrincipalPoint, vPeriodPrincipalPoint, lensWindowCentre, principalPointCentre,
						focalLength, refractiveIndex, parent,  maxSteps,  transmissionCoefficient,  shadowThrowing),
				parent,	// parent, 
				studio	// the studio
				);

//		if(uPeriodWindow==uPeriodPrincipalPoint && vPeriodWindow==vPeriodPrincipalPoint && lensWindowCentre==principalPointCentre)
//		{
//			SurfaceProperty surfaceLensletArray = new SurfaceOfRefractiveCLAs(uPeriodWindow, vPeriodWindow, lensWindowCentre,
//			focalLength, refractiveIndex, parent, maxSteps, transmissionCoefficient, shadowThrowing); //parent correct??
//			this.surfaceLensletArray = surfaceLensletArray;
//		}else {
//			SurfaceProperty surfaceLensletArray = new SurfaceOfGeneralRefractiveCLAs(uPeriodWindow, vPeriodWindow, uPeriodPrincipalPoint, vPeriodPrincipalPoint, lensWindowCentre, principalPointCentre,
//					focalLength, refractiveIndex, parent,  maxSteps,  transmissionCoefficient,  shadowThrowing);//parent correct??
//			this.surfaceLensletArray = surfaceLensletArray;
//		}
		
	}
	

	/**
	 * @param original
	 */
	public RefractiveLensletArray(RefractiveLensletArray original)
	{
		this(
				original.getDescription(),
				original.getU().getLength(),
				original.getV().getLength(),
				original.getW().getLength(),
				original.getCentre(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getuPeriodWindow(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getvPeriodWindow(), 
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getuPeriodPrincipalPoint(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getvPeriodPrincipalPoint(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getCentreOfLensWindow00(), 
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getCentreOfLensPrincipalPoint00(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getFocalLength(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getRefractiveIndex(), 
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getMaxSteps(),
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).getTransmissionCoefficient(), 
				((SurfaceOfGeneralRefractiveCLAs)(original.getSurfaceProperty())).isShadowThrowing(),
				original.getParent(), 
				original.getStudio()
			);	
	}
	
//	@Override
//	public RefractiveLensletArray clone()
//	{
//		return new RefractiveLensletArray(this);
//	}
//
//	
//	public double getWidth() {
//		return width;
//	}
//
//
//	public void setWidth(double width) {
//		this.width = width;
//	}
//
//
//	public double getHeight() {
//		return height;
//	}
//
//
//	public void setHeight(double height) {
//		this.height = height;
//	}
//
//
//	public double getThickness() {
//		return thickness;
//	}
//
//
//	public void setThickness(double thickness) {
//		this.thickness = thickness;
//	}
//
//
//	public Vector3D getLensletArrayCentre() {
//		return lensletArrayCentre;
//	}
//
//
//	public void setLensletArrayCentre(Vector3D lensletArrayCentre) {
//		this.lensletArrayCentre = lensletArrayCentre;
//	}
//
//
//	public Vector3D getCentre2centreOfFace1() {
//		return centre2centreOfFace1;
//	}
//
//
//	public void setCentre2centreOfFace1(Vector3D centre2centreOfFace1) {
//		this.centre2centreOfFace1 = centre2centreOfFace1;
//	}
//
//
//	public Vector3D getCentre2centreOfFace2() {
//		return centre2centreOfFace2;
//	}
//
//
//	public void setCentre2centreOfFace2(Vector3D centre2centreOfFace2) {
//		this.centre2centreOfFace2 = centre2centreOfFace2;
//	}
//
//
//	public Vector3D getCentre2centreOfFace3() {
//		return centre2centreOfFace3;
//	}
//
//
//	public void setCentre2centreOfFace3(Vector3D centre2centreOfFace3) {
//		this.centre2centreOfFace3 = centre2centreOfFace3;
//	}
//
//
//	public SurfaceProperty getSurfaceLensletArray() {
//		return surfaceLensletArray;
//	}
//
//
//	public void setSurfaceLensletArray(SurfaceProperty surfaceLensletArray) {
//		this.surfaceLensletArray = surfaceLensletArray;
//	}
//
//
//	public Vector3D getLensWindowCentre() {
//		return lensWindowCentre;
//	}
//
//
//	public void setLensWindowCentre(Vector3D lensWindowCentre) {
//		this.lensWindowCentre = lensWindowCentre;
//	}
//
//
//	public Vector3D getPrincipalPointCentre() {
//		return principalPointCentre;
//	}
//
//
//	public void setPrincipalPointCentre(Vector3D principalPointCentre) {
//		this.principalPointCentre = principalPointCentre;
//	}
//
//
//	public Vector3D getuPeriodWindow() {
//		return uPeriodWindow;
//	}
//
//
//	public void setuPeriodWindow(Vector3D uPeriodWindow) {
//		this.uPeriodWindow = uPeriodWindow;
//	}
//
//
//	public Vector3D getvPeriodWindow() {
//		return vPeriodWindow;
//	}
//
//
//	public void setvPeriodWindow(Vector3D vPeriodWindow) {
//		this.vPeriodWindow = vPeriodWindow;
//	}
//
//
//	public Vector3D getuPeriodPrincipalPoint() {
//		return uPeriodPrincipalPoint;
//	}
//
//
//	public void setuPeriodPrincipalPoint(Vector3D uPeriodPrincipalPoint) {
//		this.uPeriodPrincipalPoint = uPeriodPrincipalPoint;
//	}
//
//
//	public Vector3D getvPeriodPrincipalPoint() {
//		return vPeriodPrincipalPoint;
//	}
//
//
//	public void setvPeriodPrincipalPoint(Vector3D vPeriodPrincipalPoint) {
//		this.vPeriodPrincipalPoint = vPeriodPrincipalPoint;
//	}
//
//
//	public double getFocalLength() {
//		return focalLength;
//	}
//
//
//	public void setFocalLength(double focalLength) {
//		this.focalLength = focalLength;
//	}
//
//
//	public double getRefractiveIndex() {
//		return refractiveIndex;
//	}
//
//
//	public void setRefractiveIndex(double refractiveIndex) {
//		this.refractiveIndex = refractiveIndex;
//	}
//
//
//	public int getMaxSteps() {
//		return maxSteps;
//	}
//
//
//	public void setMaxSteps(int maxSteps) {
//		this.maxSteps = maxSteps;
//	}
//
//
//	public double getTransmissionCoefficient() {
//		return transmissionCoefficient;
//	}
//
//
//	public void setTransmissionCoefficient(double transmissionCoefficient) {
//		this.transmissionCoefficient = transmissionCoefficient;
//	}
//
//
//	public boolean isShadowThrowing() {
//		return shadowThrowing;
//	}
//
//
//	public void setShadowThrowing(boolean shadowThrowing) {
//		this.shadowThrowing = shadowThrowing;
//	}







	@Override
	public String getType()
	{
		return "Array of refractive lenses";
	}

}