package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.RefractiveBoxLens;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOfGeneralRefractiveCLAs extends SurfaceOfVoxellatedLensArray
{
	
	private static final long serialVersionUID = 2324669416292713452L;
	// variables
	
	
	/**
	 * The focal length of the individual lenslets
	 */
	double focalLength;
	double refractiveIndex;
	
	Vector3D uPeriodPrincipalPoint;
	Vector3D vPeriodPrincipalPoint;
	
	Vector3D uPeriodWindow;
	Vector3D vPeriodWindow;
	
	Vector3D centreOfLensWindow00;
	Vector3D centreOfLensPrincipalPoint00;
	
	public SurfaceOfGeneralRefractiveCLAs(Vector3D uPeriodWindow, Vector3D vPeriodWindow, Vector3D uPeriodPrincipalPoint, Vector3D vPeriodPrincipalPoint, Vector3D centreOfLensWindow00, Vector3D centreOfLensPrincipalPoint00,
			double focalLength, double refractiveIndex, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
				createVoxellations(uPeriodWindow, vPeriodWindow, centreOfLensWindow00),	// voxellations
				surface, maxSteps, transmissionCoefficient, shadowThrowing
			);
		
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.uPeriodWindow = uPeriodWindow;
		this.vPeriodWindow = vPeriodWindow;
		this.uPeriodPrincipalPoint = uPeriodPrincipalPoint;
		this.vPeriodPrincipalPoint = vPeriodPrincipalPoint;
		this.centreOfLensWindow00 = centreOfLensWindow00;
		this.centreOfLensPrincipalPoint00 = centreOfLensPrincipalPoint00;
		
	}
	
	public SurfaceOfGeneralRefractiveCLAs(Vector3D uPeriod, Vector3D vPeriod, Vector3D centreOfLens00, double focalLength, double refractiveIndex, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
				createVoxellations(uPeriod, vPeriod, centreOfLens00),	// voxellations
				surface, maxSteps, transmissionCoefficient, shadowThrowing
			);
		
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.uPeriodWindow = uPeriod;
		this.vPeriodWindow = vPeriod;
		this.uPeriodPrincipalPoint = uPeriod;
		this.vPeriodPrincipalPoint = vPeriod;
		this.centreOfLensWindow00 = centreOfLens00;
		this.centreOfLensPrincipalPoint00 = centreOfLens00;
		
	}
	
	
	
	public SurfaceOfGeneralRefractiveCLAs(SurfaceOfGeneralRefractiveCLAs o)
	{
		this(
				o.getuPeriodWindow(),
				o.getvPeriodWindow(),
				o.getuPeriodPrincipalPoint(),
				o.getvPeriodPrincipalPoint(),
				o.getCentreOfLensWindow00(),
				o.getCentreOfLensPrincipalPoint00(),
				o.getFocalLength(),
				o.getRefractiveIndex(),
				o.getSurface(),
				o.getMaxSteps(),
				o.getTransmissionCoefficient(),
				o.isShadowThrowing()
			);
	}

	@Override
	public SurfaceOfVoxellatedLensArray clone() {
		return new SurfaceOfGeneralRefractiveCLAs(this);
	}
	/**
	 * getters and setters
	 */
	
	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public Vector3D getuPeriodPrincipalPoint() {
		return uPeriodPrincipalPoint;
	}

	public void setuPeriodPrincipalPoint(Vector3D uPeriodPrincipalPoint) {
		this.uPeriodPrincipalPoint = uPeriodPrincipalPoint;
	}

	public Vector3D getvPeriodPrincipalPoint() {
		return vPeriodPrincipalPoint;
	}

	public void setvPeriodPrincipalPoint(Vector3D vPeriodPrincipalPoint) {
		this.vPeriodPrincipalPoint = vPeriodPrincipalPoint;
	}

	public Vector3D getuPeriodWindow() {
		return uPeriodWindow;
	}

	public void setuPeriodWindow(Vector3D uPeriodWindow) {
		this.uPeriodWindow = uPeriodWindow;
	}

	public Vector3D getvPeriodWindow() {
		return vPeriodWindow;
	}

	public void setvPeriodWindow(Vector3D vPeriodWindow) {
		this.vPeriodWindow = vPeriodWindow;
	}

	public Vector3D getCentreOfLensWindow00() {
		return centreOfLensWindow00;
	}

	public void setCentreOfLensWindow00(Vector3D centreOfLensWindow00) {
		this.centreOfLensWindow00 = centreOfLensWindow00;
	}

	public Vector3D getCentreOfLensPrincipalPoint00() {
		return centreOfLensPrincipalPoint00;
	}

	public void setCentreOfLensPrincipalPoint00(Vector3D centreOfLensPrincipalPoint00) {
		this.centreOfLensPrincipalPoint00 = centreOfLensPrincipalPoint00;
	}
	
	// the vaguely fascinating bits
	@Override
	public RefractiveBoxLens getRefractiveLens(int[] voxelIndices) {
		Vector3D centreWindow = Vector3D.sum(centreOfLensWindow00, uPeriodWindow.getProductWith(voxelIndices[0]), vPeriodWindow.getProductWith(voxelIndices[1]));
		Vector3D centrePrincipalPoint = Vector3D.sum(centreOfLensPrincipalPoint00, uPeriodPrincipalPoint.getProductWith(voxelIndices[0]), vPeriodPrincipalPoint.getProductWith(voxelIndices[1]));
		
		RefractiveBoxLens l = new RefractiveBoxLens(
						"Lens #"+ voxelIndices[0]+", "+voxelIndices[1],	// description
						vPeriodWindow.getLength(), //windowHeight
						uPeriodWindow.getLength(), //windowWidth
						focalLength, //focalLength
						refractiveIndex, //refractiveIndex
						centrePrincipalPoint, //centre
						centreWindow, //windowCentre
						Vector3D.crossProduct(uPeriodWindow, vPeriodWindow), //frontDirection
						surface,	// parent
						surface.getStudio()	// studio
			);
		l.setAddSideSurfaces(false);
		return l;
	}
	
	public static Voxellation[] createVoxellations(Vector3D uPeriod, Vector3D vPeriod, Vector3D centreOfLens00)
	{
		Voxellation voxellations[] = new Voxellation[2];
		Vector3D pointOnPlane0 = Vector3D.sum(centreOfLens00, uPeriod.getProductWith(-0.5), vPeriod.getProductWith(-0.5));
		
		voxellations[0] = new SetOfEquidistantParallelPlanes(
				pointOnPlane0,	// pointOnPlane0
				uPeriod,	// normal
				uPeriod.getLength()	// separation
			);

		voxellations[1] = new SetOfEquidistantParallelPlanes(
				pointOnPlane0,	// pointOnPlane0
				vPeriod,	// normal
				vPeriod.getLength()	// separation
			);
		
		return voxellations;
	}

}
