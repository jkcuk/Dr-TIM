package optics.raytrace.surfaces;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import optics.raytrace.voxellations.Voxellation;

public class SurfaceOfRefractiveCLAs extends SurfaceOfVoxellatedLensArray
{
	private static final long serialVersionUID = 6135865580634960305L;

	// variables
	
	
	/**
	 * The focal length of the individual lenslets
	 */
	double focalLength;
	double refractiveIndex;
	
	Vector3D uPeriod;
	Vector3D vPeriod;
	Vector3D centreOfLens00;
	
	public SurfaceOfRefractiveCLAs(Vector3D uPeriod, Vector3D vPeriod, Vector3D centreOfLens00, double focalLength, double refractiveIndex, SceneObject surface, int maxSteps, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(
				createVoxellations(uPeriod, vPeriod, centreOfLens00),	// voxellations
				surface, maxSteps, transmissionCoefficient, shadowThrowing
			);
		
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.uPeriod = uPeriod;
		this.vPeriod = vPeriod;
		this.centreOfLens00 = centreOfLens00;
	}
	
	public SurfaceOfRefractiveCLAs(SurfaceOfRefractiveCLAs o)
	{
		this(
				o.getuPeriod(),
				o.getvPeriod(),
				o.getCentreOfLens00(),
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
		return new SurfaceOfRefractiveCLAs(this);
	}
	
	
	// getters & setters

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

	public Vector3D getuPeriod() {
		return uPeriod;
	}

	public void setuPeriod(Vector3D uPeriod) {
		this.uPeriod = uPeriod;
	}

	public Vector3D getvPeriod() {
		return vPeriod;
	}

	public void setvPeriod(Vector3D vPeriod) {
		this.vPeriod = vPeriod;
	}

	public Vector3D getCentreOfLens00() {
		return centreOfLens00;
	}

	public void setCentreOfLens00(Vector3D centreOfLens00) {
		this.centreOfLens00 = centreOfLens00;
	}

	
	// the vaguely fascinating bits
	
	@Override
	public RefractiveBoxLens getRefractiveLens(int[] voxelIndices) {
		Vector3D centre = Vector3D.sum(centreOfLens00, uPeriod.getProductWith(voxelIndices[0]), vPeriod.getProductWith(voxelIndices[1]));
		
		return new RefractiveBoxLens(
						"Lens #"+ voxelIndices[0]+", "+voxelIndices[1],	// description
						vPeriod.getLength(),	// windowHeight
						uPeriod.getLength(),	// windowWidth
						focalLength,
						refractiveIndex,
						centre,	// optical axis passes through here
						centre,	// windowCentre, i.e. clear-aperture centre
						Vector3D.crossProduct(uPeriod, vPeriod),	// frontDirection
						surface,	// parent
						surface.getStudio()	// studio
			);
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
