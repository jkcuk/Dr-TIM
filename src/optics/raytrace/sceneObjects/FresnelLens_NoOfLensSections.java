package optics.raytrace.sceneObjects;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;


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
public class FresnelLens_NoOfLensSections extends EditableSceneObjectCollection
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
	public FresnelLens_NoOfLensSections(
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
		setNumberOfLensSections(numberOfLensSections);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public FresnelLens_NoOfLensSections(SceneObject parent, Studio studio)
	{
		this(
				"Fresnel lens",
				new Vector3D(0, 0, 10),	// lensCentre
				new Vector3D(0, 0, -1),	// forwardsCentralPlaneNormal
				new Vector3D(0, 0, 9),	// frontConjugatePoint
				new Vector3D(0, 0, 11),	// backConjugatePoint
				1.5,	// refractiveIndex
				0.1,	// thickness
				0.01,	// minimumSurfaceSeparation
				10,	// numberOfLensSections
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
	public FresnelLens_NoOfLensSections(FresnelLens_NoOfLensSections original)
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
	public FresnelLens_NoOfLensSections clone()
	{
		return new FresnelLens_NoOfLensSections(this);
	}
	
	@Override
	public FresnelLens_NoOfLensSections transform(Transformation t)
	{
		return new FresnelLens_NoOfLensSections(
				getDescription(),
				t.transformPosition(getLensCentre()),
				t.transformDirection(getForwardsCentralPlaneNormal()),
				t.transformPosition(getFrontConjugatePoint()),
				t.transformPosition(getBackConjugatePoint()),
				getRefractiveIndex(),
				getThickness(),
				getMinimumSurfaceSeparation(),
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


	public Vector3D getForwardsCentralPlaneNormal() {
		return forwardsCentralPlaneNormal;
	}


	public void setForwardsCentralPlaneNormal(Vector3D forwardsCentralPlaneNormal) {
		this.forwardsCentralPlaneNormal = forwardsCentralPlaneNormal;
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
	 * @return	the lens surface's principal(ish) point, where the optical axis intersects the central plane
	 */
	public Vector3D calculatePrincipalPoint()
	{
		return Geometry.uniqueLinePlaneIntersection(frontConjugatePoint, Vector3D.difference(backConjugatePoint, frontConjugatePoint), lensCentre, forwardsCentralPlaneNormal);
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
				numberOfLensSections,
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
				numberOfLensSections,
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
				+ minimumSurfaceSeparation + ", numberOfLensSections=" + numberOfLensSections
				+ ", makeStepSurfacesBlack=" + makeStepSurfacesBlack + ", transmissionCoefficient="
				+ transmissionCoefficient + "]";
	}
	
	@Override
	public String getType()
	{
		return "Fresnel lens";
	}
}