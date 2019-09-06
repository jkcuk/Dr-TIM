package optics.raytrace.sceneObjects;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.sceneObjects.FresnelLens;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A Fresnel lens cut down to a triangular shape specified by an object implementing the ShapeWIthRandomPointAndBoundary interface (at the time of
 * writing these are so far the @see ParametrisedDisc and @see ParametrisedTriangle). 
 * @see optics.raytrace.sceneObjects.FresnelLens
 * @author johannes, gergely
 */
public class FresnelLensShaped extends EditableSceneObjectCollection
{

	
	private static final long serialVersionUID = -1380543921021923093L;

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
	 * this is used to set the focal lengths of the lens sections in order to fill the aperture and also to cut it down to the shape
	 * of the correct aperture
	 */
	private ShapeWithRandomPointAndBoundary apertureShape;

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
	 * 
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
	public FresnelLensShaped(
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
		super(description, CombinationMode.INTERSECTION, true, parent, studio);
		
		setLensCentre(lensCentre);
		setForwardsCentralPlaneNormal(forwardsCentralPlaneNormal);
		setFrontConjugatePoint(frontConjugatePoint);
		setBackConjugatePoint(backConjugatePoint);
		setRefractiveIndex(refractiveIndex);
		setThickness(thickness);
		setMinimumSurfaceSeparation(minimumSurfaceSeparation);
		setApertureShape(apertureShape);
		setMakeStepSurfacesBlack(makeStepSurfacesBlack);
		setTransmissionCoefficient(transmissionCoefficient);
		
		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 *//**/
	public FresnelLensShaped(SceneObject parent, Studio studio)
	{
		this(
				"triangular Fresnel lens",
				new Vector3D(0, 0, 10),	// lensCentre
				new Vector3D(0, 0, -1),	// forwardsCentralPlaneNormal
				new Vector3D(0, 0, 9),	// frontConjugatePoint
				new Vector3D(0, 0, 11),	// backConjugatePoint
				1.5,	// refractiveIndex
				0.1,	// thickness
				0.01,	// minimumSurfaceSeparation
				new ParametrisedTriangle(
					"parametrised triangle specifying the aperture of the default FresnelLensShaped",// description,
					new Vector3D(0,1,10),// vertex1,
					new Vector3D(0.5*Math.sqrt(3), -1.5, 0),// vertex1ToVertex2,
					new Vector3D(-0.5*Math.sqrt(3),-1.5,0),// vertex1ToVertex3,
					false,// semiInfinite,
					SurfaceColour.CYAN_MATT,// surfaceProperty, can be anything as this shape is never rendered
					parent,// parent, 
					studio// studio
					),
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
	public FresnelLensShaped(FresnelLensShaped original)
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
				original.getApertureShape(),
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
	public FresnelLensShaped clone()
	{
		return new FresnelLensShaped(this);
	}
	
	@Override
	public FresnelLensShaped transform(Transformation t)
	{
		return new FresnelLensShaped(
				getDescription(),
				t.transformPosition(getLensCentre()),
				t.transformDirection(getForwardsCentralPlaneNormal()),
				t.transformPosition(getFrontConjugatePoint()),
				t.transformPosition(getBackConjugatePoint()),
				getRefractiveIndex(),
				getThickness(),
				getMinimumSurfaceSeparation(),
				getApertureShape().transform(t),
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
	
	
	public ShapeWithRandomPointAndBoundary getApertureShape() {
		return apertureShape;
	}
	
	
	public void setApertureShape(ShapeWithRandomPointAndBoundary apertureShape) {
		this.apertureShape = apertureShape;
		
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
	 * this method is static and takes the necessary amount of arguments (i.e. doesn't use internal variables) so that other classes (mainly
	 * thinking of FresnelLens and FresnelLensSurface) can use it as well. Note that the direction of the outwardsPrincipalishPlaneNormal
	 * is not obtained from the apertureShape as it may be the case that the  aperture shape and the lens are not co-planar.
	 * @param apertureShape
	 * @param outwardsPrincipalishPlaneNormal
	 * @return the minimum and maximum focal lengths of lens sections needed for a Fresnel lens to completely fill the area of the aperture
	 * defined by apertureShape
	 */
	
	public static double[] calculateLensSurfaceFMinAndMax(ShapeWithRandomPointAndBoundary apertureShape, Vector3D outwardsPrincipalishPlaneNormal, Vector3D focalPoint, double refractiveIndex) { 
		double FMinAndMax[] = new double[2];
		try {
			double initialGuess = LensSurface.calculateFocalLengthOfSurfaceThroughPoint(apertureShape.getRandomPointOnShape(), focalPoint, outwardsPrincipalishPlaneNormal, refractiveIndex);
			FMinAndMax[0] = initialGuess;
			FMinAndMax[1] = initialGuess;
			//System.out.println("optics.raytrace.sceneObject.FresnelLensTriangular.ln400: initial fMin = "+fMinAndMax[0]);
			//System.out.println("optics.raytrace.sceneObject.FresnelLensTriangular.ln400: initial fMax = "+fMinAndMax[1]);
		} catch (InconsistencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//pepper the aperture with points and ask for the focal length of lens surface going through that point. Update the minimum and maximum values.
		for(int counter = 0; counter < 10000; counter++) {
			try {
				double focalLength = LensSurface.calculateFocalLengthOfSurfaceThroughPoint(apertureShape.getRandomPointOnShape(), focalPoint, outwardsPrincipalishPlaneNormal, refractiveIndex);

				if (focalLength < FMinAndMax[0]) {
					FMinAndMax[0] = focalLength;
					//System.out.println("optics.raytrace.sceneObject.FresnelLensTriangular.ln423: current fMin = "+focalLength+" set by random point "+randomPointOnLens+" which is "+Math.sqrt((Vector3D.difference(randomPointOnLens, new Vector3D(0,0,0))).getModSquared())+" away from the lens centre");
				} else if (focalLength > FMinAndMax[1])
				{
					FMinAndMax[1] = focalLength;
					//System.out.println("optics.raytrace.sceneObject.FresnelLensTriangular.ln423: current fMax = "+focalLength+" set by random point "+randomPointOnLens+" which is "+Math.sqrt((Vector3D.difference(randomPointOnLens, new Vector3D(0,0,0))).getModSquared())+" away from the lens centre");
				} else {}
			} catch (InconsistencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return FMinAndMax;
	}

	/**
	 * call this once all variables have been set; adds the required scene objects that make up the Fresnel lens
	 */
	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		//add in the Fresnel lens
		SceneObject theLens = new FresnelLens(
				"Fresnel lens to be shaped",
				lensCentre,	// lensCentre
				forwardsCentralPlaneNormal,	// forwardsCentralPlaneNormal
				frontConjugatePoint,	// frontConjugatePoint
				backConjugatePoint,	// backConjugatePoint
				refractiveIndex,	// refractiveIndex
				thickness,	// thickness
				minimumSurfaceSeparation,	// minimumSurfaceSeparation
				apertureShape,
				makeStepSurfacesBlack,	// makeStepSurfacesBlack
				transmissionCoefficient,	// transmissionCoefficient
				getParent(),
				getStudio()
				);
		//add in the aperture
		//the extra minimumSurfaceSeparation serves as some padding to the bounding `box'
		SceneObject boundaryOfLens = apertureShape.getBoundary(thickness + minimumSurfaceSeparation); 
		
		addSceneObject(boundaryOfLens);
		addSceneObject(theLens);			
	}


	@Override
	public String toString() {
		return "FresnelLens [lensCentre=" + lensCentre + ", forwardsCentralPlaneNormal=" + forwardsCentralPlaneNormal
				+ ", frontConjugatePoint=" + frontConjugatePoint + ", backConjugatePoint=" + backConjugatePoint
				+ ", refractiveIndex=" + refractiveIndex + ", thickness=" + thickness + ", minimumSurfaceSeparation="
				+ minimumSurfaceSeparation + ", apertureShape=" + apertureShape
				+ ", makeStepSurfacesBlack=" + makeStepSurfacesBlack + ", transmissionCoefficient="
				+ transmissionCoefficient + "]";
	}
	
	@Override
	public String getType()
	{
		return "Fresnel lens";
	}
}