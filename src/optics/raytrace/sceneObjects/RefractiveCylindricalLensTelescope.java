package optics.raytrace.sceneObjects;

import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;
//import optics.raytrace.surfaces.SurfaceColour;

/**
 * A simple refractive cylindrical lens telescope. At the moment it only really works and is tested for positive focal lengths...
 * @author Maik
 */
public class RefractiveCylindricalLensTelescope extends SceneObjectContainer
{
	private static final long serialVersionUID = -3495489316492484275L;

	/**
	 * The principal point of a front cylindrical lens. This is the front of the curved surface.
	 */
	private Vector3D principalPoint;

	/**
	 * Normalised vector in the direction of the optical axis..
	 */
	private Vector3D normalisedOpticalAxisDirection;
	
	/**
	 * Cylinder axis direction
	 */
	private Vector3D normalisedCylinderAxisDirection;
	
	/**
	 * Height, along cylinder Axis direction of the telescope. This can be infinitely tall. 
	 */
	private double height;
	
	/**
	 * width of the telescope. The maximum width is 2R where R is the radius of the smaller cylindrical lens in the telescope.  
	 */
	private double width;

	/**
	 * Focal length of the front surface lens in the refractive medium
	 */
	private double frontFocalLength;
	
	/**
	 * Focal length of the front surface lens in the refractive medium
	 */
	private double backFocalLength;

	/**
	 * Refractive index of the lens material and telescope interior
	 */
	private double refractiveIndex;
	
	/**
	 * Transmission coefficient of each surface
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the lens surfaces throw shadows
	 */
	private boolean shadowThrowing;

/**
 * Constructor for the cylindrical lens telescope
 *
 * @param description
 * @param height
 * @param width
 * @param principalPoint
 * @param normalisedOpticalAxisDirection
 * @param normalisedCylinderAxisDirection
 * @param frontFocalLength
 * @param backFocalLength
 * @param refractiveIndex
 * @param centreThickness
 * @param surfaceTransmissionCoefficient
 * @param shadowThrowing
 * @param parent
 * @param studio
 */
	public RefractiveCylindricalLensTelescope(
			String description,
			double height,
			double width,
			Vector3D principalPoint,
			Vector3D normalisedOpticalAxisDirection,
			Vector3D normalisedCylinderAxisDirection,
			double frontFocalLength,
			double backFocalLength,
			double refractiveIndex,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio)
	{

		super(description, parent, studio);
		setHeight(height);
		setWidth(width);
		setPrincipalPoint(principalPoint);
		setNormalisedOpticalAxisDirection(normalisedOpticalAxisDirection);
		setNormalisedCylinderAxisDirection(normalisedCylinderAxisDirection);
		setFrontFocalLength(frontFocalLength);
		setBackFocalLength(backFocalLength);
		setRefractiveIndex(refractiveIndex);
		setSurfaceTransmissionCoefficient(surfaceTransmissionCoefficient);
		setShadowThrowing(shadowThrowing);
		
		addElements();
	}

	public RefractiveCylindricalLensTelescope(RefractiveCylindricalLensTelescope original)
	{
		this(
				original.getDescription(),
				original.getHeight(),
				original.getWidth(),
				original.getPrincipalPoint(),
				original.getNormalisedOpticalAxisDirection(),
				original.getNormalisedCylinderAxisDirection(),
				original.getFrontFocalLength(),
				original.getBackFocalLength(),
				original.getRefractiveIndex(),
				original.getSurfaceTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
				);
	}

	@Override
	public RefractiveCylindricalLensTelescope clone()
	{
		return new RefractiveCylindricalLensTelescope(this);
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
	
	public Vector3D getNormalisedCylinderAxisDirection() {
		return normalisedCylinderAxisDirection;
	}

	public void setNormalisedCylinderAxisDirection(Vector3D cylinderAxisDirection) {
		this.normalisedCylinderAxisDirection = cylinderAxisDirection.getNormalised();
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getFrontFocalLength() {
		return frontFocalLength;
	}

	public void setFrontFocalLength(double frontFocalLength) {
		this.frontFocalLength = frontFocalLength;
	}

	public double getBackFocalLength() {
		return backFocalLength;
	}

	public void setBackFocalLength(double backFocalLength) {
		this.backFocalLength = backFocalLength;
	}

	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
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
	
	

	public void addElements()
	{
		Vector3D normalisedSideDirection = Vector3D.crossProduct(normalisedCylinderAxisDirection,normalisedOpticalAxisDirection).getNormalised();
		double rfront = calculateRFromLensmakersEquation(frontFocalLength, refractiveIndex);
		double rback = calculateRFromLensmakersEquation(backFocalLength, refractiveIndex);
		Vector3D frontCylinderCentre = Vector3D.sum(principalPoint, normalisedOpticalAxisDirection.getProductWith(rfront));
		Vector3D backCylinderPrincipalPoint = Vector3D.sum(principalPoint, normalisedOpticalAxisDirection.getProductWith((frontFocalLength+backFocalLength)));
		Vector3D backCylinderCentre = Vector3D.sum(backCylinderPrincipalPoint, normalisedOpticalAxisDirection.getProductWith(-rback));//TODO double check these but should be correct ish..

		
		double rAbsMin = Math.min(Math.abs(rback), Math.abs(rfront));
		//condition where no appropriate lens can be created 
		if(2*Math.abs(rAbsMin)<Math.abs(width)) {
			System.err.println("One of the lenses you are trying to create cannot be made correctly: 2r ("+2*Math.abs(rAbsMin)+")< width ("+width+"). Setting width = 2*r ("+2*rAbsMin+")");
			width = 2*rAbsMin;
		}
		
		
		RefractiveSimple surfaceN = new RefractiveSimple(refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);
//		RefractiveSimple surface1OverN = new RefractiveSimple(1./refractiveIndex, surfaceTransmissionCoefficient, shadowThrowing);	// 1/n as the inside of the sphere is now outside of the lens
		
		SurfaceProperty frontSurfaceProperty = surfaceN;
		SurfaceProperty backSurfaceProperty = surfaceN;
//		if (rfront<0) frontSurfaceProperty = surface1OverN;
//		if (rback<0) frontSurfaceProperty = surface1OverN;
		
		//Now to build the telescope lenses
		
		// create a scene-object intersection, for the front
		SceneObjectIntersection frontLens = new SceneObjectIntersection(
				"Front lens",	// description
				this,	// parent
				getStudio()
			);
		
			// surface 1, the front cylinder
			CylinderMantle frontCylinder = new CylinderMantle(
					"front surface",// description,
					Vector3D.sum(frontCylinderCentre, normalisedCylinderAxisDirection.getProductWith(-height/2)),// startPoint,
					Vector3D.sum(frontCylinderCentre, normalisedCylinderAxisDirection.getProductWith(height/2)),//  endPoint,
					Math.abs(rfront),//  radius,
					false,//  infinite,
					frontSurfaceProperty,//  surfaceProperty,
					this,//  parent,
					getStudio()//  studio
				);
			
			// surface 1 invisible back plane
			
			Plane plane1 = new Plane(
			"Plane to cut off", //description,
			frontCylinderCentre, //pointOnPlane,
			normalisedOpticalAxisDirection.getProductWith(1), //normal, //TODO check normals
			null,//SurfaceColour.BLUE_MATT, //surfaceProperty,
			this, //parent,
			getStudio() //studio
					);
	
			// create a scene-object intersection, for the back
			SceneObjectIntersection backLens = new SceneObjectIntersection(
					"Back lens",	// description
					this,	// parent
					getStudio()
				);
			
			// surface 2, the back cylinder
			CylinderMantle backCylinder = new CylinderMantle(
					"back surface",// description,
					Vector3D.sum(backCylinderCentre, normalisedCylinderAxisDirection.getProductWith(-height/2)),// startPoint,
					Vector3D.sum(backCylinderCentre, normalisedCylinderAxisDirection.getProductWith(height/2)),//  endPoint,
					Math.abs(rback),//  radius,
					false,//  infinite,
					backSurfaceProperty,//  surfaceProperty,
					this,//  parent,
					getStudio()//  studio
				);
			
			// surface 2 invisible back plane
			Plane plane2 = new Plane(
			"Plane to cut off", //description,
			backCylinderCentre, //pointOnPlane,
			normalisedOpticalAxisDirection.getProductWith(-1), //normal, //TODO check normals
			null,//SurfaceColour.BLUE_MATT, //surfaceProperty,
			this, //parent,
			getStudio() //studio
					);

			//Adding the front...
			frontLens.addPositiveSceneObject(frontCylinder);
			frontLens.addInvisiblePositiveSceneObject(plane1);
			
			//... and back surfaces
			backLens.addPositiveSceneObject(backCylinder);
			backLens.addInvisiblePositiveSceneObject(plane2);	
			
			
			// create a scene-object intersection, for the back
			SceneObjectIntersection sides = new SceneObjectIntersection(
					"Side",	// description
					this,	// parent
					getStudio()
				);
			
			//And lastly to cut it into the right dimensions
			Plane sidePlane1 = new Plane(
					"Side 1 of telescope", //description,
					Vector3D.sum(principalPoint, normalisedSideDirection.getProductWith(width/2)), //pointOnPlane,
					normalisedSideDirection.getProductWith(1), //normal,
					surfaceN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);
			
			Plane sidePlane2 = new Plane(
					"Side 2 of telescope", //description,
					Vector3D.sum(principalPoint, normalisedSideDirection.getProductWith(-width/2)), //pointOnPlane,
					normalisedSideDirection.getProductWith(-1), //normal, 
					surfaceN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);

			Plane topPlane1 = new Plane(
					"Top 1 of telescope", //description,
					Vector3D.sum(principalPoint, normalisedCylinderAxisDirection.getProductWith(height/2)), //pointOnPlane,
					normalisedCylinderAxisDirection.getProductWith(1), //normal, 
					surfaceN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);
			
			Plane topPlane2 = new Plane(
					"Top 2 of telescope", //description,
					Vector3D.sum(principalPoint, normalisedCylinderAxisDirection.getProductWith(-height/2)), //pointOnPlane,
					normalisedCylinderAxisDirection.getProductWith(-1), //normal, 
					surfaceN,//SurfaceColour.BLUE_MATT, //surfaceProperty,
					this, //parent,
					getStudio() //studio
				);

			frontLens.addPositiveSceneObject(sidePlane1);
			frontLens.addPositiveSceneObject(sidePlane2);
			frontLens.addPositiveSceneObject(topPlane1);
			frontLens.addPositiveSceneObject(topPlane2);
			
			backLens.addPositiveSceneObject(sidePlane1);
			backLens.addPositiveSceneObject(sidePlane2);
			backLens.addPositiveSceneObject(topPlane1);
			backLens.addPositiveSceneObject(topPlane2);
			
			
			//We also need to create the sides so that it is fully working...
			sides.addPositiveSceneObject(sidePlane1);
			sides.addPositiveSceneObject(sidePlane2);
			sides.addPositiveSceneObject(topPlane1);
			sides.addPositiveSceneObject(topPlane2);
			sides.addInvisibleNegativeSceneObject(plane1);
			sides.addInvisibleNegativeSceneObject(plane2);
			
			
			addSceneObject(frontLens);
			addSceneObject(backLens);
			addSceneObject(sides);
		
	}

	

	
	/**
	 * Calculate the radius of curvature of a refractive cylindrical lens (when the outgoing rays are in a material of index n) TODO fix this as well....
	 * @param f	focal length
	 * @param n	refractive index
	 * @return	radius of curvature (>0 = convex) 
	 */
	public static double calculateRFromLensmakersEquation(double f, double n)
	{
		return f*(n-1)/n;	
	}


	@Override
	public String getType()
	{
		return "Cylindrical lens telescope";
	}
}
