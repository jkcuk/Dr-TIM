package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;


/**
 * @author johannes
 * 
 * A surface of a Fresnel lens with refractive index <i>n</i> that refracts light rays that travel inside the lens in the <i>w</i> direction
 * such that, outside of the lens, they travel through the origin of the (u, v, w) coordinate system.
 * (We can then construct a Fresnel lens from two such surfaces.)
 */
public class FresnelLensSurface_01 extends EditableSceneObjectCollection
{
	private static final long serialVersionUID = 4693005033608366287L;
	
	/**
	 * the outside focal point of the Fresnel-lens surface
	 */
	private Vector3D focalPoint;
	
	/**
	 * the focal length of the Fresnel-lens surface, i.e. the distance of the plane from the focal point
	 */
	private double focalLength;
	
	/**
	 * refractive index of the lens;
	 * the refractive index outside is assumed to be 1, otherwise this should be interpreted as the inside-to-outside refractive-index ratio;
	 * throughout, it is assumed that n>1
	 */
	private double refractiveIndex;
	
	/**
	 * a unit vector in the direction of the optical axis, pointing outwards
	 */
	private Vector3D opticalAxisDirectionOutwards;
	
	/**
	 * (approximate?) thickness of the Fresnel-lens surface
	 */
	private double thickness;
	
	/**
	 * number of lens sections that form this Fresnel lens
	 */
	private int numberOfLensSections;
	
	/**
	 * if true, make all step surfaces black, otherwise make them refracting
	 */
	private boolean makeStepSurfacesBlack;
	
	/**
	 * transmission coefficient
	 */
	private double transmissionCoefficient;
	
	
	// private variables
	
	
	
	//
	// constructors
	// 
	
	/**
	 * @param description
	 * @param focalPoint
	 * @param focalLength
	 * @param refractiveIndex
	 * @param opticalAxisDirectionOutwards
	 * @param thickness
	 * @param numberOfLensSections
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLensSurface_01(
			String description,
			Vector3D focalPoint,
			double focalLength,
			double refractiveIndex,
			Vector3D opticalAxisDirectionOutwards,
			double thickness,
			int numberOfLensSections,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		setFocalPoint(focalPoint);
		setFocalLength(focalLength);
		setRefractiveIndex(refractiveIndex);
		setOpticalAxisDirectionOutwards(opticalAxisDirectionOutwards);
		setThickness(thickness);
		setNumberOfLensSections(numberOfLensSections);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public FresnelLensSurface_01(FresnelLensSurface_01 original)
	{
		this(
				original.getDescription(),
				original.getFocalPoint(),
				original.getFocalLength(),
				original.getRefractiveIndex(),
				original.getOpticalAxisDirectionOutwards(),
				original.getThickness(),
				original.getNumberOfLensSections(),
				original.isMakeStepSurfacesBlack(),
				original.getTransmissionCoefficient(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public FresnelLensSurface_01 clone()
	{
		return new FresnelLensSurface_01(this);
	}
	
	@Override
	public FresnelLensSurface_01 transform(Transformation t)
	{
		return new FresnelLensSurface_01(
				getDescription(),
				t.transformPosition(getFocalPoint()),
				getFocalLength(),
				getRefractiveIndex(),
				t.transformDirection(getOpticalAxisDirectionOutwards()),
				getThickness(),
				getNumberOfLensSections(),
				isMakeStepSurfacesBlack(),
				getTransmissionCoefficient(),
				getParent(),
				getStudio()
		);
	}



	//
	// getters and setters
	//

	public Vector3D getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(Vector3D principalPoint) {
		this.focalPoint = principalPoint;
	}

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

	public Vector3D getOpticalAxisDirectionOutwards() {
		return opticalAxisDirectionOutwards;
	}

	/**
	 * @param opticalAxisDirectionOutwards
	 */
	public void setOpticalAxisDirectionOutwards(Vector3D opticalAxisDirectionOutwards) {
		this.opticalAxisDirectionOutwards = opticalAxisDirectionOutwards;
	}

	
	public double getThickness() {
		return thickness;
	}


	public void setThickness(double thickness) {
		this.thickness = thickness;
	}


	public int getNumberOfLensSections() {
		return numberOfLensSections;
	}


	public void setNumberOfLensSections(int numberOfLensSections) {
		this.numberOfLensSections = numberOfLensSections;
	}


	public boolean isMakeStepSurfacesBlack() {
		return makeStepSurfacesBlack;
	}


	public void setMakeStepSurfacesBlack(boolean makeStepSurfacesBlack) {
		this.makeStepSurfacesBlack = makeStepSurfacesBlack;
	}


	public double getTransmissionCoefficient() {
		return transmissionCoefficient;
	}


	public void setTransmissionCoefficient(double transmissionCoefficient) {
		this.transmissionCoefficient = transmissionCoefficient;
	}


	/**
	 * @return	the lens surface's principal point, where the optical axis intersects the lens surface
	 */
	public Vector3D calculatePrincipalPoint()
	{
		return Vector3D.sum(
				focalPoint,
				opticalAxisDirectionOutwards.getWithLength(-focalLength)
			);
	}


	public void populateSceneObjectCollection()
	{
		System.out.println("FresnelLensSurface::populateSceneObjectCollection: Hi!");

		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		LensSurface lensSurface = null;
		for(int i=1; i<=numberOfLensSections; i++)
		{
			double f = focalLength - i*thickness;
			if(f*focalLength > 0)
			{
				System.out.println("FresnelLensSurface::populateSceneObjectCollection: f = "+f);
				
				SceneObjectPrimitiveIntersection lensSegment = new SceneObjectPrimitiveIntersection(
						"Lens segment #"+i,	// description
						this,	// parent
						getStudio()
					);
				
				if(lensSurface != null) lensSegment.addNegativeSceneObjectPrimitive(
						new BelinCone(
								"Inner Belin cone for lens surface #"+i,	// description
								calculatePrincipalPoint(),	// pointInContourPlane
								opticalAxisDirectionOutwards,	// contourNormal
								lensSurface,	// lensSurface
								(makeStepSurfacesBlack?SurfaceColour.BLACK_MATT:new RefractiveSimple(refractiveIndex, transmissionCoefficient, false)),	// surfaceProperty
								lensSegment,	// parent
								getStudio()
							)
						);

				lensSurface = new LensSurface(
						i+"th lens surface",
						focalPoint,
						f,
						refractiveIndex,
						opticalAxisDirectionOutwards,
						transmissionCoefficient,	// transmission coefficient
						false,	// shadow-throwing
						lensSegment,
						getStudio()
					);
				lensSegment.addPositiveSceneObjectPrimitive(lensSurface);
				
//				BelinCone belinCone = new BelinCone(
//						"outer Belin cone for lens surface #"+i,	// description
//						calculatePrincipalPoint(),	// pointInContourPlane
//						opticalAxisDirectionOutwards,	// contourNormal
//						lensSurface,	// lensSurface
//						(makeStepSurfacesBlack?SurfaceColour.BLACK_MATT:new RefractiveSimple(refractiveIndex, transmissionCoefficient, false)),	// surfaceProperty
//						lensSegment,	// parent
//						getStudio()
//					);		
//				lensSegment.addPositiveSceneObjectPrimitive(belinCone);

				Plane backPlane = new Plane(
						"Back plane",	// description
						calculatePrincipalPoint(),	// point in plane
						opticalAxisDirectionOutwards.getReverse(),	// normal
						Transparent.PERFECT,	// surface property
						lensSegment,
						getStudio()
					);
				lensSegment.addPositiveSceneObjectPrimitive(backPlane);
				
				addSceneObject(lensSegment);
			}
		}
	}



	@Override
	public String toString() {
		return "FresnelLensSurface [focalPoint=" + focalPoint + ", focalLength=" + focalLength + ", refractiveIndex="
				+ refractiveIndex + ", opticalAxisDirectionOutwards=" + opticalAxisDirectionOutwards + ", thickness="
				+ thickness + ", numberOfLensSections=" + numberOfLensSections + ", makeStepSurfacesBlack="
				+ makeStepSurfacesBlack + "]";
	}

	@Override
	public String getType()
	{
		return "Fresnel-lens surface";
	}
}
