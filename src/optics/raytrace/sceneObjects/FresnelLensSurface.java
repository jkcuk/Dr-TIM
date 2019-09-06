package optics.raytrace.sceneObjects;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
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
public class FresnelLensSurface extends EditableSceneObjectCollection
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
	 * This constructor specifies @param lensSurfaceFMin and @param lensSurfaceFMax manually.
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
	public FresnelLensSurface(
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
	
	/** Constructor that sets the max and min focal lengths needed for constructing its surfaces using a ShapeWithRandomPointAnBoundary as
	 * an argument. Note that FresnelLensSurface does not store the ShapeWithRandomPointAnBoundary as an internal variable, instead the max
	 * and min focal lengths are treated as more fundamental. There are no setters and getters for the ShapeWithRandomPointAnBoundary argument
	 * it is merely an option for calculating focal lengths it is stored as an internal variable in @see FresnelLensShaped where it is needed 
	 * for the additional purpose of cutting the lens down to shape.
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
	public FresnelLensSurface(
			String description,
			Vector3D pointInPrincipalishPlane,
			Vector3D outwardsPrincipalishPlaneNormal,
			Vector3D focalPoint,
			double refractiveIndex,
			Vector3D opticalAxisDirection,
			double approximateThickness,
			ShapeWithRandomPointAndBoundary apertureShape,
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
		setLensSurfaceFMinAndMax(apertureShape);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}

	
	/**
	 * This constructor is used for backwards compatibility so that it can be called with @param numberOfLensSections
	 */
	//
    public FresnelLensSurface(
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
		
		// now calculate lensSurfaceFMin and Max from numberOfLensSections
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
	public FresnelLensSurface(FresnelLensSurface original)
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
	public FresnelLensSurface clone()
	{
		return new FresnelLensSurface(this);
	}
	
	@Override
	public FresnelLensSurface transform(Transformation t)
	{
		return new FresnelLensSurface(
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
		/*return new FresnelLensSurface(
				getDescription(),
				t.transformPosition(getPointInPrincipalishPlane()),
				t.transformDirection(getOutwardsPrincipalishPlaneNormal()),
				t.transformPosition(getFocalPoint()),
				getRefractiveIndex(),
				t.transformDirection(getOpticalAxisDirection()),
				getApproximateThickness(),
				getApertureShape().transform(t),	
				isMakeStepSurfacesBlack(),
				getTransmissionCoefficient(),
				getParent(),
				getStudio()*/
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
	
	public double getLensSurfaceFMax() {
		return lensSurfaceFMax;
	}
	
	//for setting the focal lengths with an apertureShape
	public void setLensSurfaceFMinAndMax(ShapeWithRandomPointAndBoundary apertureShape) {
		this.lensSurfaceFMin = FresnelLensShaped.calculateLensSurfaceFMinAndMax(apertureShape, outwardsPrincipalishPlaneNormal, focalPoint, refractiveIndex)[0];
		this.lensSurfaceFMax = FresnelLensShaped.calculateLensSurfaceFMinAndMax(apertureShape, outwardsPrincipalishPlaneNormal, focalPoint, refractiveIndex)[1];
	}
	
	//for setting the focal lengths manually
	public void setLensSurfaceFMax(double lensSurfaceFMax) {
		this.lensSurfaceFMax = lensSurfaceFMax;
	}
	
	public void setLensSurfaceFMin(double lensSurfaceFMin) {
		this.lensSurfaceFMin = lensSurfaceFMin;
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
//		try
//		{
//			return Geometry.linePlaneIntersection(focalPoint, opticalAxisDirection, pointInPrincipalishPlane, outwardsPrincipalishPlaneNormal);
//		}
//		catch(MathException e)
//		{
//			// the line lies in the plane, so there are infinitely many intersection points;
//			// return one that isn't the focal point
//			return Vector3D.sum(focalPoint, opticalAxisDirection);
//		}
	}


	/**
	 * populate the scene-object collection with the various surfaces.
	 * Note that this can fail, in which case the scene-object collection is empty (which can easily be checked, using getNumberOfSceneObjects()).
	 */
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
				(f > lensSurfaceFMin - approximateThickness);
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
