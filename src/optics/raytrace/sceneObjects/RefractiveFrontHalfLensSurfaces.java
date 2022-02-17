package optics.raytrace.sceneObjects;

import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
//import optics.raytrace.surfaces.SurfaceColour;

/**
 * The optical surfaces of the front half of a refractive lens. Essentially a plano lens.
 * @author Maik, based on Johannes Courtial's RefractiveLensSurfaces
 */
public class RefractiveFrontHalfLensSurfaces extends SceneObjectPrimitiveIntersection
{
	private static final long serialVersionUID = 8659007644627971198L;

	/**
	 * The principal point of a plano lens. This is the front of the curved surface.
	 */
	private Vector3D principalPoint;

	/**
	 * Normalised vector in the direction of the optical axis..
	 */
	private Vector3D normalisedOpticalAxisDirection;

	/**
	 * Focal length of the lens
	 */
	private double focalLength;

	/**
	 * Refractive index of the lens material
	 */
	private double refractiveIndex;

	/**
	 * The thickness of the lens, i.e. the separation between the intersection between the optical axis and the lens surface
	 */
	private double centreThickness;	
	
	/**
	 * Transmission coefficient of each surface
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the lens surfaces throw shadows
	 */
	private boolean shadowThrowing;

	/**
	 * True if the lens is to be used as a single plano lens rather than as a "lump" on an array
	 */
	private boolean useAsPlanoLens = false;

	/**
	 * Creates the optical surfaces of a plano refractive lens.
	 * 
	 * @param description
	 * @param centre
	 * @param normalisedOpticalAxisDirection
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centreThickness
	 * @param surfaceTransmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public RefractiveFrontHalfLensSurfaces(
			String description,
			Vector3D principalPoint,
			Vector3D normalisedOpticalAxisDirection,
			double focalLength,
			double refractiveIndex,
			double centreThickness,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio)
	{
		super(description, parent, studio);
		this.principalPoint = principalPoint;
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.centreThickness = centreThickness;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.shadowThrowing = shadowThrowing;

		addElements();
	}

	public RefractiveFrontHalfLensSurfaces(RefractiveFrontHalfLensSurfaces original)
	{
		this(
				original.getDescription(),
				original.getPrincipalPoint(),
				original.getNormalisedOpticalAxisDirection(),
				original.getFocalLength(),
				original.getRefractiveIndex(),
				original.getCentreThickness(),
				original.getSurfaceTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
				);
	}

	@Override
	public RefractiveFrontHalfLensSurfaces clone()
	{
		return new RefractiveFrontHalfLensSurfaces(this);
	}




	/**
	 * setters and getters
	 */

	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public Vector3D getNormalisedOpticalAxisDirection() {
		return normalisedOpticalAxisDirection;
	}

	/**
	 * set optical-axis direction, ensuring it is normalised
	 * @param opticalAxisDirection
	 */
	public void setNormalisedOpticalAxisDirection(Vector3D opticalAxisDirection) {
		this.normalisedOpticalAxisDirection = opticalAxisDirection.getNormalised();
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

	public double getCentreThickness() {
		return centreThickness;
	}

	public void setCentreThickness(double centreThickness) {
		this.centreThickness = centreThickness;
	}

	public double getSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}

	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}

	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}
		
	public Boolean getUseAsPlanoLens() {
		return useAsPlanoLens;
	}
	
	public void setUseAsPlanoLens(Boolean useAsPlanoLens) {
		this.useAsPlanoLens = useAsPlanoLens;
	}

	
	public void addElements()
	{
		double r = calculateRFromLensmakersEquation(focalLength, refractiveIndex);
		Vector3D sphere1Centre = Vector3D.sum(principalPoint, normalisedOpticalAxisDirection.getProductWith(Math.abs(r)));
		Vector3D planeCentre = Vector3D.sum(sphere1Centre, normalisedOpticalAxisDirection.getProductWith(Math.abs(centreThickness)-Math.abs(r)));
		RefractiveSimple surfaceN = new RefractiveSimple(refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);
		RefractiveSimple surface1OverN = new RefractiveSimple(1./refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);	// 1/n as the inside of the sphere is now outside of the lens

		//System.out.println("RefractiveLensSurfaces::addElements: r = "+r+", sphere1Centre="+sphere1Centre+ " "+Math.signum(focalLength));
		//System.out.println("SphereCentre1"+sphere1Centre+"sphereCentre2"+sphere1Centre+"r"+r+"centre"+planeCentre);
		if (r>0)
		{	
			//condition where no appropriate lens can be created 
			if(Math.abs(r)<Math.abs(centreThickness)) {
				System.err.println("The lens you are trying to create cannot be made correctly: r ("+Math.abs(r)+")< centreThickness ("+centreThickness+")");
			}
			
			// Normally refractive plano lens
			
			// surface 1
			Sphere sphere1 = new Sphere(
					"Surface 1",	// description
					sphere1Centre,	// centre	
					Math.abs(r),	// radius
					surfaceN,
					this,
					getStudio()
				);
			
			
			Plane plane = new Plane(
					"Plane to cut off", //description,
					planeCentre, //pointOnPlane,
					normalisedOpticalAxisDirection.getProductWith(1), //normal, 
					surfaceN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);
			


			
			
			addPositiveSceneObjectPrimitive(sphere1);
			
			if(useAsPlanoLens) {
				addPositiveSceneObjectPrimitive(plane);
			}else {
				addInvisiblePositiveSceneObjectPrimitive(plane);
			}

		}
		else
		{	//condition where no appropriate lens can be created 
			if(Math.abs(r)<Math.abs(centreThickness)) {
				System.err.println("The lens you are trying to create cannot be made correctly: r ("+Math.abs(r)+")< centreThickness ("+centreThickness+")");
			}
			
			// Inversely refractive plano lens
			
			// surface 1
			Sphere sphere1 = new Sphere(
					"Surface 1",	// description
					sphere1Centre,	// centre	
					Math.abs(r),	// radius
					surface1OverN,	
					this,
					getStudio()
				);

			Plane plane = new Plane(
					"Plane to cut off", //description,
					planeCentre, //pointOnPlane,
					normalisedOpticalAxisDirection.getProductWith(1), //normal, 
					surface1OverN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);
		
			
			
			addPositiveSceneObjectPrimitive(sphere1);
			
			if(useAsPlanoLens) {
				addPositiveSceneObjectPrimitive(plane);
			}else {
				addInvisiblePositiveSceneObjectPrimitive(plane);
			}

		}
	}

	
	/**
	 * Calculates the focalLength for an essentially plano lens, i.e r2 is infinity
	 * @param r radius of curvature
	 * @param n refractive index
	 * @return
	 */
	public static double calculateFFromLensmakersEquation(double r, double n)
	{

		return r/(n-1);
	}
	
	/**
	 * Calculate the radius of curvature of a plano refractive lens
	 * @param f	focal length
	 * @param n	refractive index
	 * @return	radius of curvature (>0 = convex) 
	 */
	public static double calculateRFromLensmakersEquation(double f, double n)
	{
		return f*(n-1);	
	}


	@Override
	public String getType()
	{
		return "Front half of Refractive lens surfaces";
	}
}
