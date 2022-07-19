package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * TIM's head but with spectacles.
 * The default head radius is about 20 such that the eyes radius is about that of the human eye.
 * There are two^ types of spectacles to chose from, with free choice in frame colour restricted only by SurfaceColour.
 * 
 * ^two for the moment but may increase if there is fun in making spectacle types.
 * 
 * 
 * @author Maik
 */

public class TimHeadWithSpecsAndUnits extends SceneObjectContainer implements Serializable
{
	private static final long serialVersionUID = -2020879195818245854L;
	
	// units TODO add them maybe? or not necessary?
	public static double NM = 1e-9;
	public static double UM = 1e-6;
	public static double MM = 1e-3;
	public static double CM = 1e-2;
	public static double M = 1;

	//Tim head settings
	private TimHead timHead;
	private Vector3D centre;
	private Vector3D frontDirection;
	private Vector3D topDirection;
	private Vector3D rightDirection;
	private double radius;
	
	//surface properties of the specs
	SurfaceProperty leftSpecsSurface;
	SurfaceProperty rightSpecsSurface;

	//frame colour of the specs
	private SurfaceColour frameColour;

	public enum FrameType
	{
		ROUND("Simple round specs"),
		PILOT("Pilot style specs"),
		NOTHING("No specs");

		private String description;
		private FrameType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private FrameType frameType;
	
	/**
	 * Creates Tim but with the added option of giving him specs with a desired surface property
	 * 
	 * @param description
	 * @param centre
	 * @param radius
	 * @param frontDirection
	 * @param topDirection
	 * @param rightDirection
	 * @param frameType
	 * @param frameColour
	 * @param leftSpecsSurface 
	 * @param rightSpecsSurface
	 * @param parent
	 * @param studio
	 */
	public TimHeadWithSpecsAndUnits(
			String description, 
			Vector3D centre, 
			double radius, 
			Vector3D frontDirection, 
			Vector3D topDirection, 
			Vector3D rightDirection, 
			FrameType frameType,
			SurfaceColour frameColour,
			SurfaceProperty leftSpecsSurface,
			SurfaceProperty rightSpecsSurface,
			SceneObject parent, 
			Studio studio)
	{
		super(description, parent, studio);
		this.centre = centre;
		this.radius = radius;
		this.frontDirection = frontDirection;
		this.topDirection = topDirection;
		this.rightDirection = rightDirection;
		this.frameType = frameType;
		this.frameColour = frameColour;
		this.leftSpecsSurface =leftSpecsSurface;
		this.rightSpecsSurface = rightSpecsSurface;


		addElements();
	}
	
	
	/**
	 * A constructor in case the eye radius wishes to be defined instead. This scales the whole Tim head down to the desired eye size.
	 * 
	 * @param description
	 * @param eyeRadius
	 * @param centre
	 * @param frontDirection
	 * @param topDirection
	 * @param rightDirection
	 * @param frameType
	 * @param frameColour
	 * @param leftSpecsSurface
	 * @param rightSpecsSurface
	 * @param parent
	 * @param studio
	 */
	public TimHeadWithSpecsAndUnits(
			String description, 
			double eyeRadius,
			Vector3D centre, 	
			Vector3D frontDirection, 
			Vector3D topDirection, 
			Vector3D rightDirection, 
			FrameType frameType,
			SurfaceColour frameColour,
			SurfaceProperty leftSpecsSurface,
			SurfaceProperty rightSpecsSurface,
			SceneObject parent, 
			Studio studio)
	{
		this(
				description,
				centre, 
				eyeRadius/0.15,	// radius
				frontDirection,	// frontDirection
				topDirection, 	// topDirection
				rightDirection,	// rightDirection
				frameType,
				frameColour,
				leftSpecsSurface,
				rightSpecsSurface,
				parent,
				studio
				);
	}


	/**
	 * A constructor which makes tim's actual head sized such that his eye radius is close to the humane value.
	 * 
	 * @param description
	 * @param centre
	 * @param frameType
	 * @param frameColour
	 * @param leftSpecsSurface
	 * @param rightSpecsSurface
	 * @param parent
	 * @param studio
	 */
	public TimHeadWithSpecsAndUnits(
			String description, 
			Vector3D centre, 	
			FrameType frameType,
			SurfaceColour frameColour,
			SurfaceProperty leftSpecsSurface,
			SurfaceProperty rightSpecsSurface,
			SceneObject parent, 
			Studio studio)
	{
		this(
				description,
				centre, 
				1.25*CM/0.15,	// radius
				new Vector3D(0, 0, -1),	// frontDirection
				new Vector3D(0, 1, 0), 	// topDirection
				new Vector3D(1, 0, 0),	// rightDirection
				frameType,
				frameColour,
				leftSpecsSurface,
				rightSpecsSurface,
				parent,
				studio
				);
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public TimHeadWithSpecsAndUnits(TimHeadWithSpecsAndUnits original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getRadius(),
				original.getFrontDirection(),
				original.getTopDirection(),
				original.getRightDirection(),
				original.getFrameType(),
				original.getFrameColour(),
				original.getLeftSpecsSurface(),
				original.getRightSpecsSurface(),
				original.getParent(),
				original.getStudio()
				);
	}

	//getters and setters
	
	@Override
	public TimHeadWithSpecsAndUnits clone()
	{
		return new TimHeadWithSpecsAndUnits(this);
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector3D getFrontDirection() {
		return frontDirection;
	}

	public void setFrontDirection(Vector3D frontDirection) {
		this.frontDirection = frontDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	public void setTopDirection(Vector3D topDirection) {
		this.topDirection = topDirection;
	}

	public Vector3D getRightDirection() {
		return rightDirection;
	}

	public void setRightDirection(Vector3D rightDirection) {
		this.rightDirection = rightDirection;
	}

	public SurfaceColour getFrameColour() {
		return frameColour;
	}

	public void setFrameColour(SurfaceColour frameColour) {
		this.frameColour = frameColour;
	}

	public FrameType getFrameType() {
		return frameType;
	}

	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	public SurfaceProperty getLeftSpecsSurface() {
		return leftSpecsSurface;
	}

	public void setLeftSpecsSurface(SurfaceProperty leftSpecsSurface) {
		this.leftSpecsSurface = leftSpecsSurface;
	}

	public SurfaceProperty getRightSpecsSurface() {
		return rightSpecsSurface;
	}

	public void setRightSpecsSurface(SurfaceProperty rightSpecsSurface) {
		this.rightSpecsSurface = rightSpecsSurface;
	}

	public TimHead getTimHead() {
		return timHead;
	}

	public void setTimHead(TimHead timHead) {
		this.timHead = timHead;
	}

	public void addElements()


	{
		clear();

		// first create a coordinate system from the given directions
		ArrayList<Vector3D> basis = new ArrayList<Vector3D>(3);
		basis.add(rightDirection.getPartPerpendicularTo(topDirection, frontDirection).getNormalised());
		basis.add(topDirection.getNormalised());
		basis.add(frontDirection.getPartPerpendicularTo(topDirection).getWithLength(-1));
		//now add tims head
		timHead = new TimHead(
				description, 
				centre, 
				radius, 
				frontDirection, 
				topDirection, 
				rightDirection, 
				this, 
				getStudio()
				);		

		addSceneObject(timHead);

		
		//time to add some stylish specs
		//set the important params and scale factors for the specs based on the time head created above...

		Vector3D leftEyeCentre = timHead.getLeftEye().getCentre();
		Vector3D rightEyeCentre  = timHead.getRightEye().getCentre();

		//head radius used to scale specs
		double headRadius = timHead.getRadius();

		//when the eye radius is that of a normal eye i.e about 1.25cm the specs should be 1.5cm from the eye.
		//hence the specs scale with head radius as follows: r_head*0.18 = Specs_distance 
		double specsEyeDistance = headRadius*0.18;

		//in vector format along the front direction:
		Vector3D specsEyeVector = timHead.getFrontDirection().getWithLength(specsEyeDistance);

		//applying this to every eye now gives a centre position for the specs and the plane within the specs
		Vector3D leftSpecCentre = Vector3D.sum(leftEyeCentre, specsEyeVector);
		Vector3D rightSpecCentre = Vector3D.sum(rightEyeCentre, specsEyeVector);

		// specs dimensions human about 37 mm tall and 52 mm wide. 

		//make specs 5mm thick when realistic eye size this leads to the following ratio between head radius and spec thickness
		double frameThickness = headRadius*0.06;
		Vector3D frameThicknessVector = timHead.getFrontDirection().getWithLength(frameThickness);

		//frame radius should be about 3.3 cm when humanoid head size
		double frameradius = 0.165*headRadius;

		
		//create scene object collections to which each component part can be added.
		SceneObjectPrimitiveIntersection leftFrames = new SceneObjectPrimitiveIntersection("frames", null, null);
		SceneObjectPrimitiveIntersection rightFrames = new SceneObjectPrimitiveIntersection("frames", null, null);
		SceneObjectPrimitiveIntersection leftSpecs = new SceneObjectPrimitiveIntersection("specs", null, null);
		SceneObjectPrimitiveIntersection rightSpecs = new SceneObjectPrimitiveIntersection("specs", null, null);
		//Add a collection for a conneting piece between the two frames... for now only used in the round type
		SceneObjectPrimitiveIntersection framesConnection = new SceneObjectPrimitiveIntersection("frames", null, null);
		//create two more intersections as these will be needed for more complicated specs for now
		SceneObjectPrimitiveIntersection leftFramesTop = new SceneObjectPrimitiveIntersection("frames", null, null);
		SceneObjectPrimitiveIntersection rightFramesTop = new SceneObjectPrimitiveIntersection("frames", null, null);


		//creating the planes to define the specs and cut the frames...
		Plane objectivePlane = new Plane(
				"plane to cut the specs in objective direction",
				Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(0.5)),
				timHead.getFrontDirection().getNormalised(),//.getProductWith(-1),
				frameColour,//SurfaceColour.YELLOW_SHINY,
				this,
				getStudio()
				);
		Plane ocularPlane = new Plane(
				"plane to cut the specs in objective direction",
				Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(-0.5)),
				timHead.getFrontDirection().getNormalised().getProductWith(-1),
				frameColour,
				this,
				getStudio()
				);
		
		//...and planes that represent the actual spectacle surfaces
		Plane leftSpecsPlane = new Plane(
				"left eye specs plane",
				leftSpecCentre,
				timHead.getFrontDirection().getNormalised(),
				leftSpecsSurface,
				this,
				getStudio()
				);
		
		Plane rightSpecsPlane = new Plane(
				"right eye specs plane",
				rightSpecCentre,
				timHead.getFrontDirection().getNormalised(),
				rightSpecsSurface,
				this,
				getStudio()
				);

		
		//defining the cylinders and planes to be used for creating the frame, depending on the type selected some all or none of these will be used.
		CylinderMantle frameCylinder, frameNegativeCylinder, leftFrameCylinder, leftNegativeFrameCylinder, rightFrameCylinder, rightNegativeFrameCylinder,
						leftFrameTopCylinder, leftNegativeFrameTopCylinder, rightFrameTopCylinder, rightNegativeFrameTopCylinder;
		Plane connectorFrameHorizontalCutOff, connectorFrameRightCutOff, connectorFrameLeftCutOff, leftHorizontalCutoffPlane, rightHorizontalCutoffPlane;
		

		//selecting and creating the frames
		switch(frameType) {
		case ROUND:	
			//adding the middle connector frame bit
			frameCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					timHead.getNose().getCentre(),
					timHead.getNose().getCentre().getSumWith(frameThicknessVector.getWithLength(headRadius)),
					timHead.getNose().getRadius()*1.45, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			frameNegativeCylinder = new CylinderMantle(
					"the cylinder that will be cut out from the middle",
					timHead.getNose().getCentre(),
					timHead.getNose().getCentre().getSumWith(frameThicknessVector.getWithLength(headRadius)),
					timHead.getNose().getRadius()*1.363, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			connectorFrameHorizontalCutOff = new Plane(
					"horizontal connector cut off plane",
					rightSpecCentre,
					timHead.getTopDirection().getWithLength(1),
					frameColour,
					this,
					getStudio()
					);

			connectorFrameRightCutOff = new Plane(
					"horizontal connector cut off plane",
					rightSpecCentre.getSumWith(timHead.getRightDirection().getWithLength(frameradius)),
					timHead.getRightDirection().getWithLength(1),
					frameColour,
					this,
					getStudio()
					);

			connectorFrameLeftCutOff = new Plane(
					"horizontal connector cut off plane",
					leftSpecCentre.getSumWith(timHead.getRightDirection().getWithLength(-frameradius)),
					timHead.getRightDirection().getWithLength(-1),
					frameColour,
					this,
					getStudio()
					);
			
			
			//creating the cylinders that will form the left specs frame
			leftFrameCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(-0.6)),
					frameradius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			leftNegativeFrameCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftSpecCentre, frameThicknessVector.getProductWith(-0.6)),
					frameradius*0.94, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);	

			//creating the cylinders that will form the right specs frame
			rightFrameCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(rightSpecCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightSpecCentre, frameThicknessVector.getProductWith(-0.6)),
					frameradius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			rightNegativeFrameCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(rightSpecCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightSpecCentre, frameThicknessVector.getProductWith(-0.6)),
					frameradius*0.94, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			//adding all the objects (positively and negatively) to their corresponding collection

			//left frames
			leftFrames.addPositiveSceneObjectPrimitive(ocularPlane);
			leftFrames.addPositiveSceneObjectPrimitive(objectivePlane);
			leftFrames.addPositiveSceneObjectPrimitive(leftFrameCylinder);
			leftFrames.addNegativeSceneObjectPrimitive(leftNegativeFrameCylinder);

			//right frame
			rightFrames.addPositiveSceneObjectPrimitive(ocularPlane);
			rightFrames.addPositiveSceneObjectPrimitive(objectivePlane);
			rightFrames.addPositiveSceneObjectPrimitive(rightFrameCylinder);
			rightFrames.addNegativeSceneObjectPrimitive(rightNegativeFrameCylinder);

			//frame connector
			framesConnection.addNegativeSceneObjectPrimitive(connectorFrameRightCutOff);
			framesConnection.addNegativeSceneObjectPrimitive(connectorFrameLeftCutOff);
			framesConnection.addNegativeSceneObjectPrimitive(connectorFrameHorizontalCutOff);
			framesConnection.addPositiveSceneObjectPrimitive(ocularPlane);
			framesConnection.addPositiveSceneObjectPrimitive(objectivePlane);
			framesConnection.addPositiveSceneObjectPrimitive(frameCylinder);
			framesConnection.addNegativeSceneObjectPrimitive(frameNegativeCylinder);

			//adding the specs part
			leftSpecs.addPositiveSceneObjectPrimitive(leftSpecsPlane);
			leftSpecs.addPositiveSceneObjectPrimitive(leftNegativeFrameCylinder);
			rightSpecs.addPositiveSceneObjectPrimitive(rightSpecsPlane);
			rightSpecs.addPositiveSceneObjectPrimitive(rightNegativeFrameCylinder);
			

			break;
		case PILOT:			
			//the radius multiplier to keep the frames at about 2mm for a normal head size must be slightly changed to allow for playing with radii and position
			//calculate some scale factors to be used for the cylinder radii for...
			//...the top cylinders
			double topCentreScaler = 1.4; //scales where the centre of this cylinder will lie (trial and error)
			double topFrameRadius= topCentreScaler*headRadius;
			double scaleFactorTop = 1-((0.0099*headRadius)/(headRadius*topCentreScaler)); //resulting in a scale factor multiplying the inner cylinder radius to create the appropriately sized frames
			//... the bottom cylinders
			double centreScaler = 0.23; //scales where the centre of this cylinder will lie (trial and error)
			double bottomFrameRadius= centreScaler*headRadius;
			double scaleFactorBottom = 1-((0.0099*headRadius)/(headRadius*centreScaler)); //resulting in a scale factor multiplying the inner cylinder radius to create the appropriately sized frames
			
			
			//creating the top object consisting of a large cylinder
			
			//Move the centre down a bit(trial and error)
			Vector3D leftFrameTopCylinderCentre = Vector3D.sum(leftSpecCentre,timHead.getTopDirection().getWithLength(-(topFrameRadius-frameradius)));
			leftFrameTopCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(leftFrameTopCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftFrameTopCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					topFrameRadius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			leftNegativeFrameTopCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(leftFrameTopCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftFrameTopCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					topFrameRadius*scaleFactorTop, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);	
			
			//creating the bottom part made of a smaller cylinder
			//Move the centre up a bit(trial and error)
			Vector3D leftFrameCylinderCentre = Vector3D.sum(leftSpecCentre,timHead.getTopDirection().getWithLength((bottomFrameRadius-frameradius)));
			leftFrameCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(leftFrameCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftFrameCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					bottomFrameRadius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			leftNegativeFrameCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(leftFrameCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(leftFrameCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					bottomFrameRadius*scaleFactorBottom, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);	
			
			//do the same on the right...
			
			//Move the centre down a bit and adjust radii(trial and error...)
			Vector3D rightFrameTopCylinderCentre = Vector3D.sum(rightSpecCentre,timHead.getTopDirection().getWithLength(-(topFrameRadius-frameradius)));
			rightFrameTopCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(rightFrameTopCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightFrameTopCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					topFrameRadius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			rightNegativeFrameTopCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(rightFrameTopCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightFrameTopCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					topFrameRadius*scaleFactorTop, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);	
			
			//creating the bottom part

			//Move the centre up a bit and adjust radii(trial and error...)
			Vector3D rightFrameCylinderCentre = Vector3D.sum(rightSpecCentre,timHead.getTopDirection().getWithLength((bottomFrameRadius-frameradius)));
			rightFrameCylinder = new CylinderMantle(
					"the cylinder that will be the outer frame",
					Vector3D.sum(rightFrameCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightFrameCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					bottomFrameRadius, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);

			rightNegativeFrameCylinder = new CylinderMantle(
					"the cylinder that will be cut out",
					Vector3D.sum(rightFrameCylinderCentre, frameThicknessVector.getProductWith(0.6)),
					Vector3D.sum(rightFrameCylinderCentre, frameThicknessVector.getProductWith(-0.6)),
					bottomFrameRadius*scaleFactorBottom, //the diagonal radius of the box 
					frameColour,//n
					this,
					getStudio()
					);
			
			
			//lastly, add the planes to cut the top and bottom to fit them together at the intersections between the two differently sized cylinders.
			Vector3D leftCircularIntersection = twoCircleIntersection(leftFrameCylinderCentre, leftFrameTopCylinderCentre, bottomFrameRadius*scaleFactorBottom,topFrameRadius*scaleFactorTop, timHead.getFrontDirection());
			leftHorizontalCutoffPlane = new Plane(
					"Plane to cut the specs on the left",
					leftCircularIntersection,
					timHead.getTopDirection(),
					frameColour,
					this,
					getStudio()
					);
			
			Vector3D rightCircularIntersection = twoCircleIntersection(rightFrameCylinderCentre, rightFrameTopCylinderCentre, bottomFrameRadius*scaleFactorBottom,topFrameRadius*scaleFactorTop, timHead.getFrontDirection());
			rightHorizontalCutoffPlane = new Plane(					
					"Plane to cut the specs on the right",
					rightCircularIntersection,
					timHead.getTopDirection(),
					frameColour,
					this,
					getStudio()
					);
			
			
			//finally create the intersections that will now make the frames
			//left frames
			leftFrames.addPositiveSceneObjectPrimitive(ocularPlane);
			leftFrames.addPositiveSceneObjectPrimitive(objectivePlane);
			leftFrames.addPositiveSceneObjectPrimitive(leftFrameCylinder);
			leftFrames.addNegativeSceneObjectPrimitive(leftNegativeFrameCylinder);
			leftFrames.addPositiveSceneObjectPrimitive(leftHorizontalCutoffPlane);
			
			leftFramesTop.addPositiveSceneObjectPrimitive(ocularPlane);
			leftFramesTop.addPositiveSceneObjectPrimitive(objectivePlane);
			leftFramesTop.addPositiveSceneObjectPrimitive(leftFrameTopCylinder);
			leftFramesTop.addNegativeSceneObjectPrimitive(leftNegativeFrameTopCylinder);
			leftFramesTop.addNegativeSceneObjectPrimitive(leftHorizontalCutoffPlane);
			
			//right frames
			rightFrames.addPositiveSceneObjectPrimitive(ocularPlane);
			rightFrames.addPositiveSceneObjectPrimitive(objectivePlane);
			rightFrames.addPositiveSceneObjectPrimitive(rightFrameCylinder);
			rightFrames.addNegativeSceneObjectPrimitive(rightNegativeFrameCylinder);
			rightFrames.addPositiveSceneObjectPrimitive(rightHorizontalCutoffPlane);
			
			rightFramesTop.addPositiveSceneObjectPrimitive(ocularPlane);
			rightFramesTop.addPositiveSceneObjectPrimitive(objectivePlane);
			rightFramesTop.addPositiveSceneObjectPrimitive(rightFrameTopCylinder);
			rightFramesTop.addNegativeSceneObjectPrimitive(rightNegativeFrameTopCylinder);
			rightFramesTop.addNegativeSceneObjectPrimitive(rightHorizontalCutoffPlane);
			
			leftSpecs.addPositiveSceneObjectPrimitive(leftNegativeFrameCylinder);
			leftSpecs.addPositiveSceneObjectPrimitive(leftNegativeFrameTopCylinder);
			leftSpecs.addPositiveSceneObjectPrimitive(leftSpecsPlane);
			
			rightSpecs.addPositiveSceneObjectPrimitive(rightNegativeFrameCylinder);
			rightSpecs.addPositiveSceneObjectPrimitive(rightNegativeFrameTopCylinder);
			rightSpecs.addPositiveSceneObjectPrimitive(rightSpecsPlane);

			break;
		case NOTHING:
		default:
			break;
		}
		//add the scene objects
		addSceneObject(framesConnection);
		addSceneObject(leftFramesTop);
		addSceneObject(rightFramesTop);
		addSceneObject(rightFrames);
		addSceneObject(leftFrames);
		addSceneObject(leftSpecs);
		addSceneObject(rightSpecs);

	}
	/**
	 * To find the additive intersection of two circles, representing the 2D intersection of two cylinders with differing radii and position but same normal.
	 * 
	 * @param centre1
	 * @param centre2
	 * @param radius1 where radius2 > radius1
	 * @param radius2 where radius2 > radius1
	 * @param commonNormal
	 * @return
	 */
	public Vector3D twoCircleIntersection(Vector3D centre1, Vector3D centre2, double radius1, double radius2, Vector3D commonNormal) {
		Vector3D additionIntersection = new Vector3D(0,0,0);
		//test that there is an intersection
		if(Vector3D.getDistance(centre1, centre2)<=Math.abs(radius2-radius1)||Vector3D.getDistance(centre1, centre2)>=Math.abs(radius2+radius1)) {
			System.err.println("circles do not intersect");
			return null;
		}

		//from https://stackoverflow.com/questions/35748840/circle-circle-intersection-in-3d
		//		cdiff = (c1-c2)
		//				cdifflen = cdiff.Length 
		//
		//				if cdifflen > r1 + r2 then no intersection
		//				if cdifflen = r1 + r2 then intersection exists in one point
		//				p = (c1 * r2  + c2 * r1) / (r1 + r2)
		//
		//				if cdifflen < r2 - r1 then no intersection
		//				if cdifflen = r2 - r1 then intersection exists in one point
		//				p = (c1 - c2) * r2 /(r2 - r1)
		//
		//				otherwise there are two intersection points
		//				cdiffnorm = cdiff.Normalized //unit vector
		//				cdiffperp = cdiffnorm * n1.Normalized
		//				q = cdifflen^2 + r2^2 - r1^2
		//				dx = 1/2 * q / cdifflen
		//				dy = 1/2 * Sqrt(4 * cdifflen^2 * r2^2 - q^2) / cdifflen 
		//				p1,2 = c2 + cdiffnorm * dx +/- cdiffperp * dy

		//Equivalent as above
		Vector3D centreToCentre = centre1.getDifferenceWith(centre2);
		Vector3D centreToCentreNormal= centreToCentre.getNormalised();
		Vector3D centreToCentrePerpendicular = Vector3D.crossProduct(centreToCentreNormal, commonNormal);
		double q = centreToCentre.getModSquared() + (radius2*radius2) - (radius1*radius1);
		double dx = 0.5*q/centreToCentre.getLength();
		double dy = 0.5*Math.sqrt(4*centreToCentre.getModSquared()*(radius2*radius2) - (q*q))/centreToCentre.getLength();

		additionIntersection = Vector3D.sum(centre2,centreToCentreNormal.getProductWith(dx), centreToCentrePerpendicular.getProductWith(dy));

		return additionIntersection;
	}

	//TODO add transform to specs maybe? Don't know what this does exactly...
	@Override
	public TimHeadWithSpecsAndUnits transform(Transformation t)
	{
		TimHeadWithSpecsAndUnits h = new TimHeadWithSpecsAndUnits(
				getDescription(),
				t.transformPosition(getCentre()),
				getFrameType(),
				getFrameColour(),
				getLeftSpecsSurface(),
				getRightSpecsSurface(),
				getParent(),
				getStudio()
				);

		// get rid of all the scene objects in h
		h.clear();

		h.setTimHead(getTimHead().transform(t));
		return h;
	}

	@Override
	public String getType()
	{
		return "Head with specs";
	}
}