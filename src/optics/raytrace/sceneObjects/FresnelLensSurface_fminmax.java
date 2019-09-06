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
public class FresnelLensSurface_fminmax extends EditableSceneObjectCollection
{
	private static final long serialVersionUID = 4693005033608366287L;

	/**
	 * all lens sections end in the lens's principal(ish) plane, which is defined by this <i>pointInPrincipalishPlane</i> and the <i>outwardsPrincipalishPlaneNormal</i>
	 */
	private Vector3D pointInPrincipalishPlane;
	
	/**
	 * all lens sections end in the lens's principal(ish) plane, which is defined by the <i>pointInPrincipalishPlane</i> and this <i>outwardsPrincipalishPlaneNormal</i>
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
	private double approximateThickness;
	
	/**
	 * focal length of the lens surface with the smallest focal length
	 */
	private double lensSurfaceFMin;
	
	/**
	 * focal length of the lens surface with the greatest focal length
	 */
	private double lensSurfaceFMax;
	
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
	 * @param pointInPrincipalishPlane
	 * @param outwardsPrincipalishPlaneNormal
	 * @param focalPoint
	 * @param refractiveIndex
	 * @param opticalAxisDirection
	 * @param approximateThickness
	 * @param lensSurfaceFMin
	 * @param lensSurfaceFMax
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLensSurface_fminmax(
			String description,
			Vector3D pointInPrincipalishPlane,
			Vector3D outwardsPrincipalishPlaneNormal,
			Vector3D focalPoint,
			double refractiveIndex,
			Vector3D opticalAxisDirection,
			double approximateThickness,
			double lensSurfaceFMin,
			double lensSurfaceFMax,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		setPointInPrincipalishPlane(pointInPrincipalishPlane);
		setOutwardsPrincipalishPlaneNormal(outwardsPrincipalishPlaneNormal);
		setFocalPoint(focalPoint);
		setRefractiveIndex(refractiveIndex);
		setOpticalAxisDirection(opticalAxisDirection);
		setApproximateThickness(approximateThickness);
		setLensSurfaceFMin(lensSurfaceFMin);
		setLensSurfaceFMax(lensSurfaceFMax);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}
	
	
	public FresnelLensSurface_fminmax(
			String description,
			Vector3D lensCentre,
			Vector3D outwardsPrincipalishPlaneNormal,
			Vector3D focalPoint,
			double refractiveIndex,
			Vector3D opticalAxisDirection,
			double approximateThickness,
			int numberOfLensSections,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		setPointInPrincipalishPlane(lensCentre);
		setOutwardsPrincipalishPlaneNormal(outwardsPrincipalishPlaneNormal);
		setFocalPoint(focalPoint);
		setRefractiveIndex(refractiveIndex);
		setOpticalAxisDirection(opticalAxisDirection);
		setApproximateThickness(approximateThickness);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		// now calculate lensSurfaceFMin and Max
		
		// create a unit vector along the optical-axis direction and facing outwards
		Vector3D opticalAxisDirectionOutwards = opticalAxisDirection.getWithLength(Math.signum(Vector3D.scalarProduct(opticalAxisDirection, outwardsPrincipalishPlaneNormal)));
		
		double focalLength = Vector3D.scalarProduct(Vector3D.difference(focalPoint, calculatePrincipalPoint()), opticalAxisDirectionOutwards);
		try {
			setLensSurfaceFMax(LensSurface.calculateFocalLengthOfSurfaceThroughPoint(lensCentre, focalPoint, opticalAxisDirectionOutwards, refractiveIndex)
					+ MyMath.TINY*Math.signum(focalLength)
					+ numberOfLensSections*approximateThickness);
		} catch (InconsistencyException e) {
			System.err.println(e.getMessage()+" The Fresnel-lens surface's scene-object container does not contain any surfaces.");
			return;
		}
		setLensSurfaceFMin(getLensSurfaceFMax() - 2*numberOfLensSections*approximateThickness);
		
		populateSceneObjectCollection();
	}

	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public FresnelLensSurface_fminmax(FresnelLensSurface_fminmax original)
	{
		this(
				original.getDescription(),
				original.getPointInPrincipalishPlane(),
				original.getOutwardsPrincipalishPlaneNormal(),
				original.getFocalPoint(),
				original.getRefractiveIndex(),
				original.getOpticalAxisDirection(),
				original.getApproximateThickness(),
				original.getLensSurfaceFMin(),
				original.getLensSurfaceFMax(),
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
	public FresnelLensSurface_fminmax clone()
	{
		return new FresnelLensSurface_fminmax(this);
	}
	
	@Override
	public FresnelLensSurface_fminmax transform(Transformation t)
	{
		return new FresnelLensSurface_fminmax(
				getDescription(),
				t.transformPosition(getPointInPrincipalishPlane()),
				t.transformDirection(getOutwardsPrincipalishPlaneNormal()),
				t.transformPosition(getFocalPoint()),
				getRefractiveIndex(),
				t.transformDirection(getOpticalAxisDirection()),
				getApproximateThickness(),
				getLensSurfaceFMin(),
				getLensSurfaceFMax(),
				isMakeStepSurfacesBlack(),
				getTransmissionCoefficient(),
				getParent(),
				getStudio()
		);
	}



	//
	// getters and setters
	//

	public Vector3D getPointInPrincipalishPlane() {
		return pointInPrincipalishPlane;
	}


	public void setPointInPrincipalishPlane(Vector3D pointInPrincipalishPlane) {
		this.pointInPrincipalishPlane = pointInPrincipalishPlane;
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

	
	public double getApproximateThickness() {
		return approximateThickness;
	}


	public void setApproximateThickness(double thickness) {
		this.approximateThickness = Math.abs(thickness);
	}


	public double getLensSurfaceFMin() {
		return lensSurfaceFMin;
	}


	public void setLensSurfaceFMin(double lensSurfaceFMin) {
		this.lensSurfaceFMin = lensSurfaceFMin;
	}


	public double getLensSurfaceFMax() {
		return lensSurfaceFMax;
	}


	public void setLensSurfaceFMax(double lensSurfaceFMax) {
		this.lensSurfaceFMax = lensSurfaceFMax;
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
		return Geometry.uniqueLinePlaneIntersection(focalPoint, opticalAxisDirection, pointInPrincipalishPlane, outwardsPrincipalishPlaneNormal);
	}


	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// create a unit vector along the optical-axis direction and facing outwards
		Vector3D opticalAxisDirectionOutwards = opticalAxisDirection.getWithLength(Math.signum(Vector3D.scalarProduct(opticalAxisDirection, outwardsPrincipalishPlaneNormal)));
		
		// the lens's focal length, i.e. the distance from the focal point to the principal point
		double focalLength = Vector3D.scalarProduct(Vector3D.difference(focalPoint, calculatePrincipalPoint()), opticalAxisDirectionOutwards);
//		double f;
//		try {
//			f = LensSurface.calculateFocalLengthOfSurfaceThroughPoint(lensCentre, focalPoint, opticalAxisDirectionOutwards, refractiveIndex)
//					+ MyMath.TINY*Math.signum(focalLength)
//					+ numberOfLensSections*approximateThickness;
//		} catch (InconsistencyException e) {
//			System.err.println(e.getMessage()+" The Fresnel-lens surface's scene-object container does not contain any surfaces.");
//			return;
//		}
		
		// System.out.println("FresnelLensSurface::populateSceneObjectCollection: f="+f+", focalLength = "+focalLength);
		
		// Belin cone #i is defined by lens surface #(i-1), so we need an initial lens surface, just for the Belin cone
		LensSurface lensSurface = new LensSurface(
				"lens surface #-1",
				focalPoint,
				lensSurfaceFMax+approximateThickness,
				refractiveIndex,
				opticalAxisDirectionOutwards,
				transmissionCoefficient,	// transmission coefficient
				false,	// shadow-throwing
				null,
				getStudio()
			);
		
		// add all the lens sections
		int lensSectionNo = 1;
		for(
				double f = lensSurfaceFMax; 
				(f >= lensSurfaceFMin);
				f -= approximateThickness
			)
		{
			if(f*focalLength > 0)	// is the lens surface on the same side of the focal point as the back plane?)
			{
			SceneObjectPrimitiveIntersection lensSection = new SceneObjectPrimitiveIntersection(
					"Lens section #"+lensSectionNo,	// description
					this,	// parent
					getStudio()
					);

			BelinCone belinCone = new BelinCone(
					"Inner Belin cone for lens surface #"+lensSectionNo,	// description
					calculatePrincipalPoint(),	// pointInContourPlane
					outwardsPrincipalishPlaneNormal,	// contourNormal
					lensSurface,	// lensSurface
					(makeStepSurfacesBlack?SurfaceColour.BLACK_MATT:new RefractiveSimple(refractiveIndex, transmissionCoefficient, false)),	// surfaceProperty
					lensSection,	// parent
					getStudio()
					);

			lensSurface = new LensSurface(
					"lens surface #"+lensSectionNo,
					focalPoint,
					f,
					refractiveIndex,
					opticalAxisDirectionOutwards,
					transmissionCoefficient,	// transmission coefficient
					false,	// shadow-throwing
					lensSection,
					getStudio()
					);

			Plane backPlane = new Plane(
					"Back plane",	// description
					calculatePrincipalPoint(),	// point in plane
					outwardsPrincipalishPlaneNormal.getReverse(),	// normal
					SurfaceColour.YELLOW_SHINY,	// surface property; previously Transparent.PERFECT, but now we are adding this as an invisible object, so the surface property doesn't matter
					lensSection,
					getStudio()
					);

			lensSection.addPositiveSceneObjectPrimitive(lensSurface);
			lensSection.addNegativeSceneObjectPrimitive(belinCone);
			lensSection.addInvisiblePositiveSceneObjectPrimitive(backPlane);

			addSceneObject(lensSection);
			
			lensSectionNo++;
			}
		}
	}


	@Override
	public String toString() {
		return "FresnelLensSurface [pointInPrincipalishPlane=" + pointInPrincipalishPlane
				+ ", outwardsPrincipalishPlaneNormal=" + outwardsPrincipalishPlaneNormal + ", focalPoint=" + focalPoint
				+ ", refractiveIndex=" + refractiveIndex + ", opticalAxisDirection=" + opticalAxisDirection
				+ ", approximateThickness=" + approximateThickness + ", lensSurfaceFMin=" + lensSurfaceFMin
				+ ", lensSurfaceFMax=" + lensSurfaceFMax + ", makeStepSurfacesBlack=" + makeStepSurfacesBlack
				+ ", transmissionCoefficient=" + transmissionCoefficient + "]";
	}

	@Override
	public String getType()
	{
		return "Fresnel-lens surface";
	}

}
