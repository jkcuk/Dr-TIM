package optics.raytrace.sceneObjects;

import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
//import optics.raytrace.surfaces.SurfaceColour;

/**
 * The optical surfaces of a refractive lens.
 * @author Johannes Courtial, based on Maik Locher's RefractiveBoxLens
 */
public class RefractiveLensSurfaces extends SceneObjectPrimitiveIntersection
{
	private static final long serialVersionUID = -150424376193111445L;

	/**
	 * The point half-way between the intersection between the optical axis and each of the two lens surfaces.
	 * This is approximately the principal point
	 */
	private Vector3D centre;

	/**
	 * Normalised vector in the direction of the optical axis.
	 * It points in the direction from the intersection between the optical axis and lens surface 1 
	 * to the intersection between the optical axis and lens surface 2.
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
	 * The thickness of the lens, i.e. the separation between the intersection between the optical axis and each of the two lens surfaces
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
	 * Creates the optical surfaces of a symmetric refractive lens.
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
	public RefractiveLensSurfaces(
			String description,
			Vector3D centre,
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
		this.centre = centre;
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection;
		this.focalLength = focalLength;
		this.refractiveIndex = refractiveIndex;
		this.centreThickness = centreThickness;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.shadowThrowing = shadowThrowing;

		addElements();
	}

	public RefractiveLensSurfaces(RefractiveLensSurfaces original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
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
	public RefractiveLensSurfaces clone()
	{
		return new RefractiveLensSurfaces(this);
	}




	/**
	 * setters and getters
	 */

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
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


	// interesting method
	
	private void addElements()
	{
		double r = calculateRFromLensmakersEquation(focalLength, refractiveIndex, centreThickness);
		Vector3D sphere1Centre = Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(+r-0.5*centreThickness));
		Vector3D sphere2Centre = Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(-r+0.5*centreThickness));
		RefractiveSimple surfaceN = new RefractiveSimple(refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);
		RefractiveSimple surface1OverN = new RefractiveSimple(1./refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);	// 1/n as the inside of the sphere is now outside of the lens

//		System.out.println("RefractiveLensSurfaces::addElements: r = "+r+", sphere1Centre="+sphere1Centre+", sphere2Centre="+sphere2Centre);

		if (r>0)
		{	
			//condition where no appropriate lens can be created 
			if(Math.abs(r)<0.5*Math.abs(centreThickness)) {
				System.err.println("The lens you are trying to create cannot be made: r ("+Math.abs(r)+")< 0.5*centreThickness ("+0.5*centreThickness+")");
			}
			
			// Biconvex lens
			
			// surface 1
			Sphere sphere1 = new Sphere(
					"Surface 1",	// description
					sphere1Centre,	// centre	
					Math.abs(r),	// radius
					surfaceN,
					this,
					getStudio()
				);

			// surface 2
			Sphere sphere2 = new Sphere(
					"Surface 2",	// description
					sphere2Centre,	// centre	
					Math.abs(r),	// radius
					surfaceN,
					this,
					getStudio()
				);
			//System.out.println("SphereCentre1"+sphere1Centre+"sphereCentre2"+sphere2Centre+"r"+r+"centre"+centre);
			// the lens is the intersection of the two spheres
			addPositiveSceneObjectPrimitive(sphere1);
			addPositiveSceneObjectPrimitive(sphere2);
		}
		else
		{	//condition where lens created may not fill the whole aperture correctly. i.e out most points of lens may be a flat surface
			if(Math.abs(r)<0.5*Math.abs(centreThickness)) {
				System.err.println("The lens you are trying to create may not fill the aperture correctly: r ("+Math.abs(r)+")< 0.5*centreThickness ("+0.5*centreThickness+")");
			}
			
			// Biconcave lens
			
			// surface 1
			Sphere sphere1 = new Sphere(
					"Surface 1",	// description
					sphere1Centre,	// centre	
					Math.abs(r),	// radius
					surface1OverN,	
					this,
					getStudio()
				);

			// surface 2
			Sphere sphere2 = new Sphere(
					"Surface 2",	// description
					sphere2Centre,	// centre	
					Math.abs(r),	// radius
					surface1OverN,
					this,
					getStudio()
				);


			//cylinder to form the positive part of the lens
			CylinderMantle lensCylinder = new CylinderMantle(
					"the cylinder that will consist of the positive space part of the lens",
					sphere1Centre,
					sphere2Centre,
					Math.abs(r),
					surfaceN,//n
					this,
					getStudio()
					);
			//System.out.println("SphereCentre1"+sphere1Centre+"sphereCentre2"+sphere2Centre+"r"+r+"centre"+centre);

			// the lens is a cylinder from which the spheres have been removed
			addPositiveSceneObjectPrimitive(lensCylinder);
			addNegativeSceneObjectPrimitive(sphere1);
			addNegativeSceneObjectPrimitive(sphere2);	

		}

	}

	
	/**
	 * @param r1	radius of curvature of surface 1 (>0 = convex)
	 * @param r2	radius of curvature of surface 2 (>0 = concave)
	 * @param n	refractive index
	 * @param d	lens thickness at optical axis
	 * @return	focal length
	 */
	public static double calculateFFromLensmakersEquation(double r1, double r2, double n, double d)
	{
		// https://en.wikipedia.org/wiki/Lens#Lensmaker's_equation
		return 1./((n-1.)*(1./r1 - 1./r2 + d*(n-1)/(n*r1*r2)));
	}
	
	/**
	 * Calculate the radius of curvature of a symmetric refractive lens
	 * Note that there are two solutions!
	 * @param f	focal length
	 * @param n	refractive index
	 * @param d	lens thickness at optical axis
	 * @return	radius of curvature (>0 = convex) of both surfaces
	 */
	public static double calculateRFromLensmakersEquation(double f, double n, double d)
	{
		// calculated in Mathematica
		return f*(n-1) + Math.signum(f)*Math.sqrt(f*(n-1)*(n-1)*n*(f*n-d))/n;
		
		// the other solution is f*(n-1) - Math.sqrt(f*(n-1)*(n-1)*n*(f*n-d))/n
	}


	@Override
	public String getType()
	{
		return "Refractive lens surfaces";
	}
}
