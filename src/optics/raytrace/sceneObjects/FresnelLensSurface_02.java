package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * @author johannes
 * 
 * A surface of a Fresnel lens with refractive index <i>n</i> that refracts light rays that travel inside the lens in the <i>w</i> direction
 * such that, outside of the lens, they travel through the origin of the (u, v, w) coordinate system.
 * All lens surfaces end in the principal(ish) plane inside the Fresnel-lens surface.
 * (We can then construct a Fresnel lens from two such surfaces.)
 */
public class FresnelLensSurface_02 extends EditableSceneObjectCollection
{
	private static final long serialVersionUID = 4693005033608366287L;

	/**
	 * the Fresnel lens is formed by <i>numberOfLensSections</i> on either side of <i>lensCentre</i>;
	 * additionally, all lens sections end in the lens's principal(ish) plane, which is defined by the point <i>lensCentre</i> and the <i>outwardsPrincipalishPlaneNormal</i>
	 */
	private Vector3D lensCentre;
	
	/**
	 * all lens sections end in the lens's principal(ish) plane, which is defined by the <i>lensCentre</i> and this <i>outwardsPrincipalishPlaneNormal</i>
	 */
	private Vector3D outwardsPrincipalishPlaneNormal;
	
	/**
	 * the outside focal point of the Fresnel-lens surface
	 */
	private Vector3D focalPoint;
	
	/**
	 * refractive index of the lens;
	 * the refractive index outside is assumed to be 1, otherwise this should be interpreted as the inside-to-outside refractive-index ratio;
	 * throughout, it is assumed that n>1
	 */
	private double refractiveIndex;
	
	/**
	 * a unit vector in the direction of the optical axis;
	 * note that this vector does not need to point outwards, as the outwards side is defined by the vector <i>outwardsPrincipalishPlaneNormal</i>
	 */
	private Vector3D opticalAxisDirection;
	
	/**
	 * (approximate?) thickness of the Fresnel-lens surface
	 */
	private double thickness;
	
	/**
	 * number of lens sections on either side of <i>lensCentre</i> that form this Fresnel lens
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
	 * @param lensCentre
	 * @param outwardsPrincipalishPlaneNormal
	 * @param focalPoint
	 * @param refractiveIndex
	 * @param opticalAxisDirection
	 * @param thickness
	 * @param numberOfLensSections
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLensSurface_02(
			String description,
			Vector3D lensCentre,
			Vector3D outwardsPrincipalishPlaneNormal,
			Vector3D focalPoint,
			double refractiveIndex,
			Vector3D opticalAxisDirection,
			double thickness,
			int numberOfLensSections,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		setLensCentre(lensCentre);
		setOutwardsPrincipalishPlaneNormal(outwardsPrincipalishPlaneNormal);
		setFocalPoint(focalPoint);
		setRefractiveIndex(refractiveIndex);
		setOpticalAxisDirection(opticalAxisDirection);
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
	public FresnelLensSurface_02(FresnelLensSurface_02 original)
	{
		this(
				original.getDescription(),
				original.getLensCentre(),
				original.getOutwardsPrincipalishPlaneNormal(),
				original.getFocalPoint(),
				original.getRefractiveIndex(),
				original.getOpticalAxisDirection(),
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
	public FresnelLensSurface_02 clone()
	{
		return new FresnelLensSurface_02(this);
	}
	
	@Override
	public FresnelLensSurface_02 transform(Transformation t)
	{
		return new FresnelLensSurface_02(
				getDescription(),
				t.transformPosition(getLensCentre()),
				t.transformDirection(getOutwardsPrincipalishPlaneNormal()),
				t.transformPosition(getFocalPoint()),
				getRefractiveIndex(),
				t.transformDirection(getOpticalAxisDirection()),
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

	public Vector3D getLensCentre() {
		return lensCentre;
	}


	public void setLensCentre(Vector3D lensCentre) {
		this.lensCentre = lensCentre;
	}


	public Vector3D getOutwardsPrincipalishPlaneNormal() {
		return outwardsPrincipalishPlaneNormal;
	}


	public void setOutwardsPrincipalishPlaneNormal(Vector3D outwardsPrincipalishPlaneNormal) {
		this.outwardsPrincipalishPlaneNormal = outwardsPrincipalishPlaneNormal;
	}


	public Vector3D getFocalPoint() {
		return focalPoint;
	}

	public void setFocalPoint(Vector3D principalPoint) {
		this.focalPoint = principalPoint;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

	public Vector3D getOpticalAxisDirection() {
		return opticalAxisDirection;
	}

	/**
	 * @param opticalAxisDirection
	 */
	public void setOpticalAxisDirection(Vector3D opticalAxisDirection) {
		this.opticalAxisDirection = opticalAxisDirection;
	}

	
	public double getThickness() {
		return thickness;
	}


	public void setThickness(double thickness) {
		this.thickness = Math.abs(thickness);
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
	 * @return	the lens surface's principal point, where the optical axis intersects the principal(ish) plane
	 */
	public Vector3D calculatePrincipalPoint()
	{
		return Geometry.uniqueLinePlaneIntersection(focalPoint, opticalAxisDirection, lensCentre, outwardsPrincipalishPlaneNormal);
	}


	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// create a unit vector along the optical-axis direction and facing outwards
		Vector3D opticalAxisDirectionOutwards = opticalAxisDirection.getWithLength(Math.signum(Vector3D.scalarProduct(opticalAxisDirection, outwardsPrincipalishPlaneNormal)));
		
		// the lens's focal length, i.e. the distance from the focal point to the principal point
		double focalLength = Vector3D.scalarProduct(Vector3D.difference(focalPoint, calculatePrincipalPoint()), opticalAxisDirectionOutwards);
		double f;
		try {
			f = LensSurface.calculateFocalLengthOfSurfaceThroughPoint(lensCentre, focalPoint, opticalAxisDirectionOutwards, refractiveIndex)
					+ MyMath.TINY*Math.signum(focalLength)
					+ numberOfLensSections*thickness;
		} catch (InconsistencyException e) {
			System.err.println(e.getMessage()+" The Fresnel-lens surface's scene-object container does not contain any surfaces.");
			return;
		}
		// System.out.println("FresnelLensSurface_young::populateSceneObjectCollection: f="+f+", focalLength = "+focalLength);
		
		// Belin cone #i is defined by lens surface #(i-1), so we need an initial lens surface, just for the Belin cone
		LensSurface lensSurface = new LensSurface(
				"lens surface #-1",
				focalPoint,
				f+thickness,
				refractiveIndex,
				opticalAxisDirectionOutwards,
				transmissionCoefficient,	// transmission coefficient
				false,	// shadow-throwing
				null,
				getStudio()
			);
		// add all the lens sections
		for(int i=0; i<2*numberOfLensSections; i++)
		{
			// double f = focalLength - (i+0.5)*thickness*Math.signum(focalLength);
			// is the lens surface on the same side of the focal point as the back plane?
			if(f*focalLength > 0)
			{
				// yes, the lens surface is on the same side of the focal point as the back plane, so it forms part of this Fresnel-lens surface
				// System.out.println("FresnelLensSurface_young::populateSceneObjectCollection: focal length of lens section #"+i+" = "+f);
				
				SceneObjectPrimitiveIntersection lensSegment = new SceneObjectPrimitiveIntersection(
						"Lens segment #"+i,	// description
						this,	// parent
						getStudio()
					);
				
				BelinCone belinCone = new BelinCone(
						"Inner Belin cone for lens surface #"+i,	// description
						calculatePrincipalPoint(),	// pointInContourPlane
						outwardsPrincipalishPlaneNormal,	// contourNormal
						lensSurface,	// lensSurface
						(makeStepSurfacesBlack?SurfaceColour.BLACK_MATT:new RefractiveSimple(refractiveIndex, transmissionCoefficient, false)),	// surfaceProperty
						lensSegment,	// parent
						getStudio()
					);

				lensSurface = new LensSurface(
						"lens surface #"+i,
						focalPoint,
						f,
						refractiveIndex,
						opticalAxisDirectionOutwards,
						transmissionCoefficient,	// transmission coefficient
						false,	// shadow-throwing
						lensSegment,
						getStudio()
					);
				
				Plane backPlane = new Plane(
						"Back plane",	// description
						calculatePrincipalPoint(),	// point in plane
						outwardsPrincipalishPlaneNormal.getReverse(),	// normal
						SurfaceColour.YELLOW_SHINY,	// surface property; previously Transparent.PERFECT, but now we are adding this as an invisible object, so the surface property doesn't matter
						lensSegment,
						getStudio()
					);
				
				lensSegment.addPositiveSceneObjectPrimitive(lensSurface);
				lensSegment.addNegativeSceneObjectPrimitive(belinCone);
				lensSegment.addInvisiblePositiveSceneObjectPrimitive(backPlane);
				
				addSceneObject(lensSegment);
			}
			f -= thickness;
		}
	}


	@Override
	public String toString() {
		return "FresnelLensSurface_young [lensCentre=" + lensCentre + ", outwardsPrincipalishPlaneNormal="
				+ outwardsPrincipalishPlaneNormal + ", focalPoint=" + focalPoint + ", refractiveIndex=" + refractiveIndex
				+ ", opticalAxisDirection=" + opticalAxisDirection + ", thickness=" + thickness
				+ ", numberOfLensSections=" + numberOfLensSections + ", makeStepSurfacesBlack=" + makeStepSurfacesBlack
				+ ", transmissionCoefficient=" + transmissionCoefficient + "]";
	}
	
	@Override
	public String getType()
	{
		return "Fresnel-lens surface";
	}
}
