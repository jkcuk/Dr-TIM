package optics.raytrace.sceneObjects;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;


/**
 * @author johannes
 * 
 * A Fresnel lens with refractive index <i>refractiveIndex</i> that images a pair of conjugate points into each other, stigmatically.
 * One of these positions is in "front" space (i.e. in the space of light rays travelling in front of the lens), the other in "back" space.
 * The lens is centred around a central plane, given by the <i>lensCentre</i> and the <i>forwardsCentralPlaneNormal</i>, and has a given
 * (approximate) thickness and minimum separation between the two sides.
 * It comprises two <i>FresnelLensSurface</i> objects.
 * @see optics.raytrace.sceneObjects.FresnelLensSurface
 */
public class FresnelLens extends EditableSceneObjectCollection
{
	private static final long serialVersionUID = -2838205129301399866L;

	/**
	 * the Fresnel lens is formed by <i>numberOfLensSections</i> on either side of <i>lensCentre</i>;
	 * additionally, all lens sections end in the lens's central plane, which is defined by the point <i>lensCentre</i> and the <i>centralPlaneNormal</i>
	 */
	private Vector3D lensCentre;
	
	/**
	 * the lens's central plane is defined by the <i>lensCentre</i> and this <i>centralPlaneNormal</i>;
	 * note that the Fresnel lens has a front and a back, defined by the direction of this vector
	 */
	private Vector3D forwardsCentralPlaneNormal;
	
	/**
	 * the front conjugate point, i.e. the point in the space of light rays travelling in front of the lens that is imaged into the back conjugate point
	 */
	private Vector3D frontConjugatePoint;

	/**
	 * the back conjugate point, i.e. the point in the space of light rays travelling behind the lens that is imaged into the front conjugate point
	 */
	private Vector3D backConjugatePoint;

	/**
	 * refractive index of the lens;
	 * the refractive index outside is assumed to be 1, otherwise this should be interpreted as the inside-to-outside refractive-index ratio;
	 * throughout, it is assumed that n>1
	 */
	private double refractiveIndex;
		
	/**
	 * (approximate?) thickness of the Fresnel lens
	 */
	private double thickness;
	
	/**
	 * the minimum separation between the two surfaces of the Fresnel lens
	 */
	private double minimumSurfaceSeparation;
	
	/**
	 * the minimum and maximum focal lengths defining the lens sections for each side of the lens
	 */
	private double focalLengthMinFront, focalLengthMaxFront, focalLengthMinBack, focalLengthMaxBack;
	
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
	 * Constructor with min and max focal lengths specified manually on both sides.
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param focalLengthMinFront
	 * @param focalLengthMaxFront
	 * @param focalLengthMinBack
	 * @param focalLengthMaxBack
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			double focalLengthMinFront,
			double focalLengthMaxFront,
			double focalLengthMinBack,
			double focalLengthMaxBack,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, true, parent, studio);
		
		setLensCentre(lensCentre);
		setForwardsCentralPlaneNormal(forwardsCentralPlaneNormal);
		setFrontConjugatePoint(frontConjugatePoint);
		setBackConjugatePoint(backConjugatePoint);
		setRefractiveIndex(refractiveIndex);
		setThickness(thickness);
		setMinimumSurfaceSeparation(minimumSurfaceSeparation);
		setFocalLengthMinFront(focalLengthMinFront);
		setFocalLengthMaxFront(focalLengthMaxFront);
		setFocalLengthMinBack(focalLengthMinBack);
		setFocalLengthMaxBack(focalLengthMaxBack);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}

	/**
	 * Constructor that uses a ShapeWithRandomPointAndBoundary to set the min and max focal lengths on both sides.
	 * Note that the apertureShape is not stored as an internal variable it is only used to compute the focal lengths.
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param apertureShape
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			ShapeWithRandomPointAndBoundary apertureShape,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, true, parent, studio);
		
		setLensCentre(lensCentre);
		setForwardsCentralPlaneNormal(forwardsCentralPlaneNormal);
		setFrontConjugatePoint(frontConjugatePoint);
		setBackConjugatePoint(backConjugatePoint);
		setRefractiveIndex(refractiveIndex);
		setThickness(thickness);
		setMinimumSurfaceSeparation(minimumSurfaceSeparation);
		setFocalLengthsFromApertureShape(apertureShape);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}
	
	/**
	 * Backwards compatible constructor with numberOfLensSections
	 * @param description
	 * @param lensCentre
	 * @param forwardsCentralPlaneNormal
	 * @param frontConjugatePoint
	 * @param backConjugatePoint
	 * @param refractiveIndex
	 * @param thickness
	 * @param minimumSurfaceSeparation
	 * @param numberOfLensSections
	 * @param makeStepSurfacesBlack
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public FresnelLens(
			String description,
			Vector3D lensCentre,
			Vector3D forwardsCentralPlaneNormal,
			Vector3D frontConjugatePoint,
			Vector3D backConjugatePoint,
			double refractiveIndex,
			double thickness,
			double minimumSurfaceSeparation,
			int numberOfLensSections,
			boolean makeStepSurfacesBlack,
			double transmissionCoefficient,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, true, parent, studio);
		
		setLensCentre(lensCentre);
		setForwardsCentralPlaneNormal(forwardsCentralPlaneNormal);
		setFrontConjugatePoint(frontConjugatePoint);
		setBackConjugatePoint(backConjugatePoint);
		setRefractiveIndex(refractiveIndex);
		setThickness(thickness);
		setMinimumSurfaceSeparation(minimumSurfaceSeparation);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		//THIS BIT SHOULD DISAPPEAR SOON AS BACKWARDS COMPATIBILITY WITH NUMBEROFLENSSECTIONS IS ELLIMINATED
		//convert numberOfLensSections to a focalLengthMin & Max on either side
		//FRONT SURFACE
		double focalLengthFront = Vector3D.scalarProduct(Vector3D.difference(frontConjugatePoint, calculatePrincipalPoint()), calculateOpticalAxisDirectionForwards());
		try {
			setFocalLengthMaxFront(LensSurface.calculateFocalLengthOfSurfaceThroughPoint(lensCentre, frontConjugatePoint, calculateOpticalAxisDirectionForwards(), refractiveIndex)
					+ MyMath.TINY*Math.signum(focalLengthFront)
					+ numberOfLensSections*0.5*(thickness - minimumSurfaceSeparation));
		} catch (InconsistencyException e) {
			System.err.println(e.getMessage()+" The Fresnel-lens surface's scene-object container does not contain any surfaces.");
			return;
		}
		setFocalLengthMinFront(getFocalLengthMaxFront() - 2*numberOfLensSections*0.5*(thickness - minimumSurfaceSeparation));
				
		//BACK SURFACE
		double focalLengthBack = Vector3D.scalarProduct(Vector3D.difference(backConjugatePoint, calculatePrincipalPoint()), calculateOpticalAxisDirectionForwards().getReverse());
		try {
			setFocalLengthMaxBack(LensSurface.calculateFocalLengthOfSurfaceThroughPoint(lensCentre, backConjugatePoint, calculateOpticalAxisDirectionForwards().getReverse(), refractiveIndex)
					+ MyMath.TINY*Math.signum(focalLengthBack)
					+ numberOfLensSections*0.5*(thickness - minimumSurfaceSeparation));
		} catch (InconsistencyException e) {
			System.err.println(e.getMessage()+" The Fresnel-lens surface's scene-object container does not contain any surfaces.");
			return;
		}
		setFocalLengthMinBack(getFocalLengthMaxBack() - 2*numberOfLensSections*0.5*(thickness - minimumSurfaceSeparation));

		populateSceneObjectCollection();
	}
	

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public FresnelLens(SceneObject parent, Studio studio)
	{
		this(
				"Fresnel lens",
				new Vector3D(0, 0, 0),	// lensCentre
				new Vector3D(0, 0, -1),	// forwardsCentralPlaneNormal
				new Vector3D(0, 0, -1),	// frontConjugatePoint
				new Vector3D(0, 0, 1),	// backConjugatePoint
				1.5,	// refractiveIndex
				0.1,	// thickness
				0.01,	// minimumSurfaceSeparation
				0.5, // fMinFront
				0.955, //fMaxFront = distance from lensCentre to frontConjugatePoint)- 0.5*(thickness-minimumSurfaceSeparation)
				0.5, //fMinBack
				0.955, //fMinBack
				true,	// makeStepSurfacesBlack
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				parent,
				studio
			);
	}


	/**
	 * Create a clone of the original
	 * @param original
	 */
	public FresnelLens(FresnelLens original)
	{
		this(
				original.getDescription(),
				original.getLensCentre(),
				original.getForwardsCentralPlaneNormal(),
				original.getFrontConjugatePoint(),
				original.getBackConjugatePoint(),
				original.getRefractiveIndex(),
				original.getThickness(),
				original.getMinimumSurfaceSeparation(),
				original.getFocalLengthMinFront(),
				original.getFocalLengthMaxFront(),
				original.getFocalLengthMinBack(),
				original.getFocalLengthMaxBack(),
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
	public FresnelLens clone()
	{
		return new FresnelLens(this);
	}
	
	@Override
	public FresnelLens transform(Transformation t)
	{
		return new FresnelLens(
				getDescription(),
				t.transformPosition(getLensCentre()),
				t.transformDirection(getForwardsCentralPlaneNormal()),
				t.transformPosition(getFrontConjugatePoint()),
				t.transformPosition(getBackConjugatePoint()),
				getRefractiveIndex(),
				getThickness(),
				getMinimumSurfaceSeparation(),
				getFocalLengthMinFront(),
				getFocalLengthMaxFront(),
				getFocalLengthMinBack(),
				getFocalLengthMaxBack(),
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


	public Vector3D getForwardsCentralPlaneNormal() {
		return forwardsCentralPlaneNormal;
	}


	public void setForwardsCentralPlaneNormal(Vector3D forwardsCentralPlaneNormal) {
		this.forwardsCentralPlaneNormal = forwardsCentralPlaneNormal;
	}
	
	
	public void setFocalLengthsFromApertureShape(ShapeWithRandomPointAndBoundary apertureShape) {
		
		double[] fMinAndMaxFront = FresnelLensShaped.calculateLensSurfaceFMinAndMax(apertureShape, calculateOpticalAxisDirectionForwards(), frontConjugatePoint, refractiveIndex);
		double[] fMinAndMaxBack = FresnelLensShaped.calculateLensSurfaceFMinAndMax(apertureShape, calculateOpticalAxisDirectionForwards().getReverse(), backConjugatePoint, refractiveIndex);

		this.focalLengthMinFront = fMinAndMaxFront[0];
		this.focalLengthMaxFront = fMinAndMaxFront[1];
		this.focalLengthMinBack = fMinAndMaxBack[0];
		this.focalLengthMaxBack = fMinAndMaxBack[1];
	}

	public Vector3D getFrontConjugatePoint() {
		return frontConjugatePoint;
	}


	public void setFrontConjugatePoint(Vector3D frontConjugatePoint) {
		this.frontConjugatePoint = frontConjugatePoint;
	}


	public Vector3D getBackConjugatePoint() {
		return backConjugatePoint;
	}


	public void setBackConjugatePoint(Vector3D backConjugatePoint) {
		this.backConjugatePoint = backConjugatePoint;
	}


	public double getRefractiveIndex() {
		return refractiveIndex;
	}


	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}


	public double getThickness() {
		return thickness;
	}


	public void setThickness(double thickness) {
		this.thickness = Math.abs(thickness);
	}


	public double getMinimumSurfaceSeparation() {
		return minimumSurfaceSeparation;
	}


	public void setMinimumSurfaceSeparation(double minimumSurfaceSeparation) {
		this.minimumSurfaceSeparation = Math.abs(minimumSurfaceSeparation);
	}


	public double getFocalLengthMinFront() {
		return focalLengthMinFront;
	}
	
	public void setFocalLengthMinFront(double fMinFront) {
		this.focalLengthMinFront = fMinFront;
	}
	
	public double getFocalLengthMaxFront() {
		return focalLengthMaxFront;
	}
	
	public void setFocalLengthMaxFront(double fMaxFront) {
		this.focalLengthMaxFront = fMaxFront;
	}
	
	public double getFocalLengthMinBack() {
		return focalLengthMinBack;
	}
	
	public void setFocalLengthMinBack(double fMinBack) {
		this.focalLengthMinBack = fMinBack;
	}
	
	public double getFocalLengthMaxBack() {
		return focalLengthMaxBack;
	}
	
	public void setFocalLengthMaxBack(double fMaxBack) {
		this.focalLengthMaxBack = fMaxBack;
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
	 * @return	the lens surface's principal(ish) point, where the optical axis intersects the central plane
	 */
	public Vector3D calculatePrincipalPoint()
	{
		return Geometry.uniqueLinePlaneIntersection(frontConjugatePoint, Vector3D.difference(backConjugatePoint, frontConjugatePoint), lensCentre, forwardsCentralPlaneNormal);
//		try
//		{
//			return Geometry.linePlaneIntersection(frontConjugatePoint, Vector3D.difference(backConjugatePoint, frontConjugatePoint), lensCentre, forwardsCentralPlaneNormal);
//		}
//		catch(MathException e)
//		{
//			// the line lies in the plane, so there are infinitely many intersection points;
//			// return one that isn't the front focal point
//			return backConjugatePoint;
//		}
	}

	/**
	 * @return	a unit vector along the optical-axis direction and facing forwards
	 */
	public Vector3D calculateOpticalAxisDirectionForwards()
	{
		// first create a vector in the optical-axis direction but not yet normalised and facing forwards
		Vector3D opticalAxisDirection = Vector3D.difference(backConjugatePoint, frontConjugatePoint);
		
		// make this vector face forwards, give it length 1, and return it
		return opticalAxisDirection.getWithLength(Math.signum(Vector3D.scalarProduct(opticalAxisDirection, forwardsCentralPlaneNormal)));
	}

	/**
	 * call this once all variables have been set; adds the required scene objects that make up the Fresnel lens
	 */
	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// create a unit vector along the optical-axis direction and facing forwards
		Vector3D opticalAxisDirectionForwards = calculateOpticalAxisDirectionForwards();
		
		// add the front surface
		addSceneObject(new FresnelLensSurface(
				"front surface",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				focalLengthMinFront,
				focalLengthMaxFront,
				makeStepSurfacesBlack,
				transmissionCoefficient,
				this,
				getStudio()
		));

		// add the back surface
		addSceneObject(new FresnelLensSurface(
				"back surface",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(-0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal.getReverse(),	// outwardsPrincipalishPlaneNormal
				backConjugatePoint,	// focalPoint
				refractiveIndex,
				opticalAxisDirectionForwards.getReverse(),	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				focalLengthMinBack,
				focalLengthMaxBack,
				makeStepSurfacesBlack,
				transmissionCoefficient,
				this,
				getStudio()
		));
	}


	@Override
	public String toString() {
		return "FresnelLens [lensCentre=" + lensCentre + ", forwardsCentralPlaneNormal=" + forwardsCentralPlaneNormal
				+ ", frontConjugatePoint=" + frontConjugatePoint + ", backConjugatePoint=" + backConjugatePoint
				+ ", refractiveIndex=" + refractiveIndex + ", thickness=" + thickness + ", minimumSurfaceSeparation="
				+ minimumSurfaceSeparation + "focalLengthMinFront= " + focalLengthMinFront + "focalLengthMaxFront= "
				+ focalLengthMaxFront + "focalLengthMinBack= " + focalLengthMinBack + "focalLengthMaxBack= "
				+ focalLengthMaxBack + ", makeStepSurfacesBlack=" + makeStepSurfacesBlack + ", transmissionCoefficient="
				+ transmissionCoefficient + "]";
	}

	@Override
	public String getType()
	{
		return "Fresnel lens";
	}
}
