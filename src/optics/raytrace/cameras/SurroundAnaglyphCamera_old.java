package optics.raytrace.cameras;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Joerg and Maria have some 3D goggles that sense the orientation of the goggles, and so they can display
 * different images when the user rotates their head.  They would like to produce video frames for this.
 * 
 * Joerg's idea is to have a pinhole camera for each eye, and so there are two images, as usual;
 * each column represents the view along a vertical line in the centre of the field of view of the eye, for
 * one particular orientation of the head.
 * 
 * As the head rotates, the positions of the pinholes move on a circle around the between-the-eyes position.
 * The direction of the centre of an eye's field of view is given by the vector from the between-the-eyes position
 * to the eye, rotated by 90°.
 * 
 * To calculate the image that corresponds to the left/right eye, set eyeSeparation to a negative/positive value.
 * 
 * I should write a quick Java program that reads in the two images and then displays them as anaglyphs.
 * Or this could just be done in Photoshop, of course.
 * 
 * @author Johannes Courtial
 */
public class SurroundAnaglyphCamera_old extends CameraClass implements Serializable
{
	private static final long serialVersionUID = 6716415179754731785L;

	/* 
	 * These should only be accessed by set and get methods,
	 * or terrible things happen.
	 */
	protected Vector3D betweenTheEyes;	// the centre between the two eyes
	protected Vector3D right0deg;	// direction from the centre between the two eyes to the right eye, for 0� goggles orientation
	protected Vector3D up;	// upwards direction, i.e. rotation axis
	protected double eyeSeparation;
	protected double verticalAngleOfView;	// in degrees
	protected OutputType outputType;
//	protected boolean colour;
	protected ProjectionType projectionType;
	
	public enum ProjectionType
	{
		CYLINDRICAL("Cylindrical"),
		SPHERICAL("Spherical"),
		SPHERICAL_COSINE("Spherical, radius of camera circle varies with cos of polar angle"),
		TEST("Test -- experimental!");
		
		private String description;
		private ProjectionType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	public enum OutputType
	{
		LEFT_EYE("Left-eye image"),
		RIGHT_EYE("Right-eye image"),
		ANAGLYPH_REDBLUE("Red/blue anaglyph"),
		ANAGLYPH_COLOUR("Colour anaglyph");
		
		private String description;
		private OutputType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	// pre-calculated variables
	protected Vector3D forward0deg;	// gets pre-calculated
	protected double tanHalfVerticalAngleOfView;
	
	/**
	 * Create a new surround anaglyph camera
	 * 
	 * @param betweenTheEyes	position of the centre between the eyes
	 * @param right0deg	
	 * @param up	Vector3D running along the width of the detector array
	 * @param eyeSeparation
	 * @param verticalAngleOfView	vertical angle of view, in degrees
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 * @param maxTraceLevel
	 */
	public SurroundAnaglyphCamera_old(
			String description,
			Vector3D betweenTheEyes,
			Vector3D right0deg, 
			Vector3D up,
			double eyeSeparation,
			OutputType outputType,
            // boolean colour,
			double verticalAngleOfView,	// in degrees
			ProjectionType projectionType,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel
		)
	{
		super(	description,
				betweenTheEyes,	// CCD centre --- not used
				right0deg,	// horizontalSpanVector --- not used
				up,	// verticalSpanVector --- not used
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		
		setBetweenTheEyes(betweenTheEyes);
		setDirections(right0deg, up);
		setEyeSeparation(eyeSeparation);
		setOutputType(outputType);
		// setColour(colour);
		setVerticalAngleOfView(verticalAngleOfView);
		setProjectionType(projectionType);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurroundAnaglyphCamera_old(SurroundAnaglyphCamera_old original)
	{
		super(original);
		setBetweenTheEyes(original.getBetweenTheEyes());
		setDirections(original.getRight0deg(), original.getUp());
		setEyeSeparation(original.getEyeSeparation());
		setOutputType(original.getOutputType());
		// setColour(original.isColour());
		setVerticalAngleOfView(original.getVerticalAngleOfView());
		setProjectionType(original.getProjectionType());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public SurroundAnaglyphCamera_old clone()
	{
		return new SurroundAnaglyphCamera_old(this);
	}
	
	
	////////////////////////////////////////////////////////////

	public Vector3D getBetweenTheEyes() {
		return betweenTheEyes;
	}

	public void setBetweenTheEyes(Vector3D betweenTheEyes) {
		this.betweenTheEyes = betweenTheEyes;
	}

	public Vector3D getRight0deg() {
		return right0deg;
	}

	public Vector3D getUp() {
		return up;
	}

	public void setDirections(Vector3D right0deg, Vector3D up)
	{
		this.right0deg = right0deg;
		this.up = up;
		
		// pre-calculate the forward direction
		this.forward0deg = Vector3D.crossProduct(up, right0deg);
	}

	public double getEyeSeparation() {
		return eyeSeparation;
	}

	public void setEyeSeparation(double eyeSeparation) {
		this.eyeSeparation = eyeSeparation;
	}

	public double getVerticalAngleOfView() {
		return verticalAngleOfView;
	}

	/**
	 * set the vertical angle of view, and pre-calculate the tan of half the vertical angle of view
	 * @param verticalAngleOfView
	 */
	public void setVerticalAngleOfView(double verticalAngleOfView) {
		this.verticalAngleOfView = verticalAngleOfView;
		tanHalfVerticalAngleOfView = Math.tan(0.5*MyMath.deg2rad(verticalAngleOfView));
	}

//	public boolean isColour() {
//		return colour;
//	}
//
//	public void setColour(boolean colour) {
//		this.colour = colour;
//	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	public ProjectionType getProjectionType() {
		return projectionType;
	}

	public void setProjectionType(ProjectionType projectionType) {
		this.projectionType = projectionType;
	}

	/**
	 * @param i
	 * @param j
	 * @param side	-1 for left eye, +1 for right eye
	 * @return
	 */
	public Ray getRayForPixel(double i, double j, int side)
	{
		// find the angle \phi that corresponds to horizontal pixel position i
		// (i = 0 corresponds to phi = 0, i=getDetectorPixelsHorizontal() corresponds to  phi = 2 pi)
		
		double
			phi = 2*Math.PI*i / getCCD().getDetectorPixelsHorizontal() + side*1.4*Math.PI/180,
			cosPhi = Math.cos(phi),
			sinPhi = Math.sin(phi),
			theta = MyMath.deg2rad(0.5*verticalAngleOfView - verticalAngleOfView * j / getCCD().getDetectorPixelsVertical()),
			cosTheta = Math.cos(theta),
			sinTheta = Math.sin(theta);
		
		// find ray direction by subtracting the pinhole position from the pixel position
		// (as the detector is in front of the pinhole)
		switch(projectionType)
		{
		case CYLINDRICAL:
			return new Ray(
					Vector3D.sum(
							betweenTheEyes,
							right0deg.getWithLength(0.5*side*getEyeSeparation()*cosPhi),
							forward0deg.getWithLength(0.5*side*getEyeSeparation()*sinPhi)
					), // start point
					Vector3D.sum(
							right0deg.getWithLength(-sinPhi),
							forward0deg.getWithLength(cosPhi),
							up.getWithLength(tanHalfVerticalAngleOfView-2*tanHalfVerticalAngleOfView*j/getCCD().getDetectorPixelsVertical())
					),	// direction
					0	// start time of ray --- not important here (?)
			);
		case SPHERICAL_COSINE:
			return new Ray(
					Vector3D.sum(
							betweenTheEyes,
							right0deg.getWithLength(0.5*side*getEyeSeparation()*cosTheta*cosPhi),
							forward0deg.getWithLength(0.5*side*getEyeSeparation()*cosTheta*sinPhi)
					), // start point
					Vector3D.sum(
							right0deg.getWithLength(-cosTheta*sinPhi),	// right0deg.getWithLength(-cosTheta*sinPhi)
							forward0deg.getWithLength(cosTheta*cosPhi),	// forward0deg.getWithLength(cosTheta*cosPhi)
							up.getWithLength(sinTheta)	// up.getWithLength(sinTheta)
					),	// direction
					0	// start time of ray --- not important here (?)
			);
		case TEST:
			double scaleFactor = cosTheta;	// replace with ratio of actual to apparent distance
			return new Ray(
					Vector3D.sum(
							betweenTheEyes,
							right0deg.getWithLength(0.5*side*getEyeSeparation()*scaleFactor*cosPhi),
							forward0deg.getWithLength(0.5*side*getEyeSeparation()*scaleFactor*sinPhi)
					), // start point
					Vector3D.sum(
							right0deg.getWithLength(-cosTheta*sinPhi),	// right0deg.getWithLength(-cosTheta*sinPhi)
							forward0deg.getWithLength(cosTheta*cosPhi),	// forward0deg.getWithLength(cosTheta*cosPhi)
							up.getWithLength(sinTheta)	// up.getWithLength(sinTheta)
					),	// direction
					0	// start time of ray --- not important here (?)					
			);
		case SPHERICAL:
		default:
			return new Ray(
					Vector3D.sum(
							betweenTheEyes,
							right0deg.getWithLength(0.5*side*getEyeSeparation()*cosPhi),
							forward0deg.getWithLength(0.5*side*getEyeSeparation()*sinPhi)
					), // start point
					Vector3D.sum(
							right0deg.getWithLength(-cosTheta*sinPhi),	// right0deg.getWithLength(-cosTheta*sinPhi)
							forward0deg.getWithLength(cosTheta*cosPhi),	// forward0deg.getWithLength(cosTheta*cosPhi)
							up.getWithLength(sinTheta)	// up.getWithLength(sinTheta)
					),	// direction
					0	// start time of ray --- not important here (?)
			);
		}
	}
	
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		return getRayForPixel(i, j, +1);	// return ray for right eye
	}
	
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		return getRayForPixel(i, j, +1);	// return ray for right eye
	}
	
	/**
	 * In case the rendered image is shown at a different size (imagePixelsHorizontal x imagePixelsVertical),
	 * return a light ray corresponding to image pixel (i,j).
	 * 
	 * In editable cameras, this method implements a method asked for by the EditableCamera interface.
	 * @see optics.raytrace.GUI.core.CameraWithRayForImagePixel#getRayForImagePixel(int, int, int, int)
	 * 
	 * @param imagePixelsHorizontal
	 * @param imagePixelsVertical
	 * @param i
	 * @param j
	 * @return a light ray that corresponds to image pixel (i,j)
	 */
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, int i, int j)
	{
		return getRayForPixel(
				(int)((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal*i),
				(int)((double)getDetectorPixelsVertical()/(double)imagePixelsVertical*j)
			);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
//		// calculate direction vector and "normal" eye position
//		// define a ray with the eye's position as starting point and the direction vector as ray direction
//		Ray ray;
//		RaySceneObjectIntersection intersection = scene.getClosestRayIntersection(ray);
//		// the intersection point is then
//		intersection.p;
		
		DoubleColour
			rightColour = scene.getColour(getRayForPixel(i,j,-1), lights, scene, getMaxTraceLevel(), getRaytraceExceptionHandler()),
			leftColour = scene.getColour(getRayForPixel(i,j,+1), lights, scene, getMaxTraceLevel(), getRaytraceExceptionHandler());
		
//		// also store together the picture seen by the left and right eye
//		leftCamera.getCCD().setPixelColour(i, j, leftColour.getRGB());
//		rightCamera.getCCD().setPixelColour(i, j, rightColour.getRGB());
		
		switch(outputType)
		{
		case LEFT_EYE:
			return leftColour;
		case RIGHT_EYE:
			return rightColour;
		case ANAGLYPH_COLOUR:
			// from http://en.wikipedia.org/wiki/Anaglyph_image:
			// "In recent simple practice, the left eye image is filtered to remove blue & green.
			// The right eye image is filtered to remove red."
			return new DoubleColour(
					leftColour.getR(),
					rightColour.getG(),
					rightColour.getB()
				);
		case ANAGLYPH_REDBLUE:
		default:
			return new DoubleColour(
					leftColour.getLuminance(),
					0,
					rightColour.getLuminance()	// * DoubleColour.LUMINANCE_R_FACTOR / DoubleColour.LUMINANCE_B_FACTOR
				);
		}
	}


	@Override
	public String toString()
	{
		return getDescription() + " [SurroundAnaglyphCamera]";
	}
}