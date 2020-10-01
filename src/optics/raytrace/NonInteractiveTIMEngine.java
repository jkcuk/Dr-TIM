package optics.raytrace;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.core.RaytraceWorker;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.RaytracingImageCanvas;
import optics.raytrace.GUI.lowLevel.RenderPanel;
import optics.raytrace.GUI.lowLevel.StatusIndicator;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;


/**
 * A class that facilitates running Dr TIM as a non-interactive Java application.
 * 
 * In order to work as a JavaApplication, any subclass of this class needs to implement a main method
 * that gets called when the Java application starts. In the simplest case, this is of the form
 *  
 *  	public static void main(final String[] args)
 *  	{
 *  		(new <subclass of NonInteractiveTIMEngine>()).run();
 *  	}
 *  
 * Set all the variables in the constructor.
 * 
 * Implement the method populateStudio() to change scene, lights and/or camera.
 * Start with a simple studio this this code
 * 
 * 		public void populateStudio()
 * 		{
 * 			// start with a simple studio
 * 			super.populateSimpleStudio();
 * 
 * 			// modify the studio here
 * 		}
 * 
 * Override the method getFirstPartOfFilename() to change the filename under which the rendered result will be saved.
 * 
 * @author Johannes Courtial
 */
public abstract class NonInteractiveTIMEngine
implements RenderPanel, StatusIndicator, ActionListener, Runnable
{
	// parameters
		
	/**
	 * Determines the quality of the output
	 */
	protected RenderQualityEnum renderQuality;
	
//	/**
//	 * if true, all frames are of reduced quality (but calculate faster)
//	 */
//	protected boolean test;
	
//	/**
//	 * true if a control panel should be shown, false if not
//	 */
//	protected boolean interactive1;
	
	protected NonInteractiveTIMActionEnum nonInteractiveTIMAction;
	
	protected boolean movie;
	
	/**
	 * for batch processing and creation of movies
	 */
	protected int numberOfFrames;
	
	/**
	 * the first frame to be calculated in this run; together with lastFrame allows part of the movie to be rendered
	 * @see lastFrame
	 */
	protected int firstFrame;

	/**
	 * the last frame to be calculated in this run; together with firstFrame allows part of the movie to be rendered
	 * @see firstFrame
	 */
	protected int lastFrame;
	
	/**
	 * the direction from which the camera views the centre of the scene
	 * @see optics.raytrace.PointCloudMakerEngine.cameraViewCentre
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected Vector3D cameraViewDirection;
	
	/**
	 * the centre of the scene, which is also a point in the focussing plane if the aperture size is not PINHOLE
	 * @see optics.raytrace.PointCloudMakerEngine.cameraApertureSize
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected Vector3D cameraViewCentre;
	
	/**
	 * the distance of the camera from cameraViewCentre
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected double cameraDistance;
	
	/**
	 * focussing distance
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected double cameraFocussingDistance;
	
	/**
	 * the camera's horizontal field of view, in degrees
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected double cameraHorizontalFOVDeg;
	
	protected SpaceTimeTransformationType cameraSpaceTimeTransformationType;
	
	/**
	 * the velocity of the camera in the scene frame, in units of c
	 */
	protected Vector3D cameraBeta;
	
	/**
	 * the camera's maxTraceLevel
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected int cameraMaxTraceLevel;
	
	/**
	 * the number of pixels in the horizontal direction
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected int cameraPixelsX;
	
	/**
	 * the number of pixels in the vertical direction
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected int cameraPixelsY;
	
	/**
	 * the camera's aperture size
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected ApertureSizeType cameraApertureSize;
	
	/**
	 * exposure compensation
	 * @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
	 */
	protected ExposureCompensationType cameraExposureCompensation;
	
	/**
	 * automatically trace rays with trajectory
	 */
	protected boolean traceRaysWithTrajectory;

	
	protected String windowTitle;
	protected int windowWidth;
	protected int windowHeight;
	
	
	
	
	// internal variables
	
	/**
	 * the studio
	 */
	protected Studio studio;
	
	/**
	 * the frame currently being calculated
	 */
	protected int frame;
	
	
	
//	/**
//	 * the standard camera's blur quality (if test=false)
//	 * @see getStandardCamera()
//	 */
//	protected QualityType standardCameraBlurQuality;
//	
//	/**
//	 * the standard camera's anti-aliasing quality (if test=false)
//	 * @see getStandardCamera()
//	 */
//	protected QualityType standardCameraAntiAliasingQuality;
	
	
	/**
	 * Constructor.
	 * Override to set all variables.
	 * @see optics.raytrace.test
	 * @see nonInteractiveTIMAction
	 * @see numberOfFrames
 	 * @see firstFrame
	 * @see lastFrame
	 * @see cameraViewDirection
	 * @see cameraViewCentre
	 * @see cameraDistance
	 * @see cameraFocussingDistance
	 * @see cameraHorizontalFOVDeg
	 * @see camaraBeta
	 * @see cameraMaxTraceLevel
	 * @see cameraPixelsX
	 * @see cameraPixelsY
	 * @see cameraApertureSize
	 * @see cameraExposureCompensation
	 * @see windowTitle
	 * @see windowWidth
	 * @see windowHeight
	 */
	public NonInteractiveTIMEngine()
	{
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		movie = false;
		numberOfFrames = 10;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// camera parameters; these are often set (or altered) in createStudio()
		cameraViewDirection = new Vector3D(-.3, -.2, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraSpaceTimeTransformationType = SpaceTimeTransformationType.LORENTZ_TRANSFORMATION;
		cameraBeta = new Vector3D(0, 0, 0);
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraExposureCompensation = ExposureCompensationType.EC0;
		
		traceRaysWithTrajectory = true;
		
		windowTitle = "Dr TIM";
		windowWidth = 850;
		windowHeight = 650;
		
//		standardCameraBlurQuality = QualityType.GOOD;
//		standardCameraAntiAliasingQuality = QualityType.GOOD;
	}
	
	
	public RenderQualityEnum getRenderQuality() {
		return renderQuality;
	}


	public void setRenderQuality(RenderQualityEnum renderQuality) {
		this.renderQuality = renderQuality;
	}



	public NonInteractiveTIMActionEnum getNonInteractiveTIMAction() {
		return nonInteractiveTIMAction;
	}


	public void setNonInteractiveTIMAction(NonInteractiveTIMActionEnum nonInteractiveTIMAction) {
		this.nonInteractiveTIMAction = nonInteractiveTIMAction;
	}



	public int getNumberOfFrames() {
		return numberOfFrames;
	}


	public void setNumberOfFrames(int numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}


	public int getFirstFrame() {
		return firstFrame;
	}


	public void setFirstFrame(int firstFrame) {
		this.firstFrame = firstFrame;
	}


	public int getLastFrame() {
		return lastFrame;
	}


	public void setLastFrame(int lastFrame) {
		this.lastFrame = lastFrame;
	}


	public Vector3D getCameraViewDirection() {
		return cameraViewDirection;
	}


	public void setCameraViewDirection(Vector3D cameraViewDirection) {
		this.cameraViewDirection = cameraViewDirection;
	}


	public Vector3D getCameraViewCentre() {
		return cameraViewCentre;
	}


	public void setCameraViewCentre(Vector3D cameraViewCentre) {
		this.cameraViewCentre = cameraViewCentre;
	}


	public double getCameraDistance() {
		return cameraDistance;
	}


	public void setCameraDistance(double cameraDistance) {
		this.cameraDistance = cameraDistance;
	}


	public double getCameraFocussingDistance() {
		return cameraFocussingDistance;
	}


	public void setCameraFocussingDistance(double cameraFocussingDistance) {
		this.cameraFocussingDistance = cameraFocussingDistance;
	}


	public double getCameraHorizontalFOV() {
		return cameraHorizontalFOVDeg;
	}


	public void setCameraHorizontalFOV(double cameraHorizontalFOVDeg) {
		this.cameraHorizontalFOVDeg = cameraHorizontalFOVDeg;
	}


	public SpaceTimeTransformationType getCameraSpaceTimeTransformationType() {
		return cameraSpaceTimeTransformationType;
	}


	public void setCameraSpaceTimeTransformationType(SpaceTimeTransformationType cameraSpaceTimeTransformationType) {
		this.cameraSpaceTimeTransformationType = cameraSpaceTimeTransformationType;
	}


	public Vector3D getCameraBeta() {
		return cameraBeta;
	}


	public void setCameraBeta(Vector3D cameraBeta) {
		this.cameraBeta = cameraBeta;
	}


	public int getCameraMaxTraceLevel() {
		return cameraMaxTraceLevel;
	}


	public void setCameraMaxTraceLevel(int cameraMaxTraceLevel) {
		this.cameraMaxTraceLevel = cameraMaxTraceLevel;
	}


	public int getCameraPixelsX() {
		return cameraPixelsX;
	}


	public void setCameraPixelsX(int cameraPixelsX) {
		this.cameraPixelsX = cameraPixelsX;
	}


	public int getCameraPixelsY() {
		return cameraPixelsY;
	}


	public void setCameraPixelsY(int cameraPixelsY) {
		this.cameraPixelsY = cameraPixelsY;
	}


	public ApertureSizeType getCameraApertureSize() {
		return cameraApertureSize;
	}


	public void setCameraApertureSize(ApertureSizeType cameraApertureSize) {
		this.cameraApertureSize = cameraApertureSize;
	}


	public ExposureCompensationType getCameraExposureCompensation() {
		return cameraExposureCompensation;
	}


	public void setCameraExposureCompensation(ExposureCompensationType cameraExposureCompensation) {
		this.cameraExposureCompensation = cameraExposureCompensation;
	}


	public boolean isTraceRaysWithTrajectory() {
		return traceRaysWithTrajectory;
	}


	public void setTraceRaysWithTrajectory(boolean traceRaysWithTrajectory) {
		this.traceRaysWithTrajectory = traceRaysWithTrajectory;
	}


	public int getFrame() {
		return frame;
	}


	public void setFrame(int frame) {
		this.frame = frame;
	}


	public String getWindowTitle() {
		return windowTitle;
	}


	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}


	public int getWindowWidth() {
		return windowWidth;
	}


	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}


	public int getWindowHeight() {
		return windowHeight;
	}


	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	

//	public QualityType getStandardCameraBlurQuality() {
//		return standardCameraBlurQuality;
//	}
//
//
//	public void setStandardCameraBlurQuality(QualityType standardCameraBlurQuality) {
//		this.standardCameraBlurQuality = standardCameraBlurQuality;
//	}
//
//
//	public QualityType getStandardCameraAntiAliasingQuality() {
//		return standardCameraAntiAliasingQuality;
//	}
//
//
//	public void setStandardCameraAntiAliasingQuality(QualityType standardCameraAntiAliasingQuality) {
//		this.standardCameraAntiAliasingQuality = standardCameraAntiAliasingQuality;
//	}


	/**
	 * Override to represent parameters in filename
	 * @return	Any parts of the filename to be added between the class name, frame number, etc.
	 */
	public String getFirstPartOfFilename()
	{
		try {
			return  Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()).getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Filename under which main saves the rendered image.
	 * Override to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public String getFilename()
	{
		return 
				getFirstPartOfFilename()
				+" "+renderQuality.getBriefDescription()
				+(movie?" "+(new DecimalFormat("0000")).format(frame):"")	// if this is a movie, then this adds the number of the frame, converted into a string
				+".bmp";
	}
	
	/**
	 * Override to save all parameters; suggested format: 
	 * @Override
	 * public void writeParameters(PrintStream printStream)
	 * {
	 * 	// write any parameters not defined in NonInteractiveTIMEngine
	 * 	printStream.println("parameterName = "+parameterName);
	 * 
	 * 	// write all parameters defined in NonInteractiveTIMEngine
	 * 	super.writeParameters(printStream);
	 * }
	 * @param printStream
	 */
	public void writeParameters(PrintStream printStream)
	{
		// protected RenderQualityEnum renderQuality;
		printStream.println("renderQuality = "+renderQuality);

		// protected boolean traceRaysWithTrajectory;
		printStream.println("traceRaysWithTrajectory = "+traceRaysWithTrajectory);
		
		writeCameraParameters(printStream);
		
		writeMovieParameters(printStream);
	}
	
	public abstract void populateStudio()
	throws SceneException;
	
	/**
	 * Set the studio to consist of a very basic scene, standard lights, and the camera set according to the camera parameters.
	 * @return a studio, i.e. scene, lights and camera
	 * @throws SceneException	if the scene cannot be set up, for some reason s
	 */
	public void populateSimpleStudio()
	throws SceneException
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects


		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
	}

	
	public Vector3D getStandardCameraPosition()
	{
		return Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance));

	}
	
	/**
	 * @return	a camera set according to a number of parameters
	 * @see optics.raytrace.test
	 * @see optics.raytrace.PointCloudMakerEngine.cameraViewDirection
	 * @see optics.raytrace.PointCloudMakerEngine.cameraViewCentre
	 * @see optics.raytrace.PointCloudMakerEngine.cameraDistance
	 * @see optics.raytrace.PointCloudMakerEngine.cameraHorizontalFOVDeg
	 * @see optics.raytrace.PointCloudMakerEngine.cameraMaxTraceLevel
	 * @see optics.raytrace.PointCloudMakerEngine.cameraPixelsX
	 * @see optics.raytrace.PointCloudMakerEngine.cameraPixelsY
	 * @see optics.raytrace.PointCloudMakerEngine.cameraApertureSize
	 * @see optics.raytrace.PointCloudMakerEngine.cameraFocussingDistance
	 */
	public EditableRelativisticAnyFocusSurfaceCamera getStandardCamera()
	{
		Vector3D cameraPosition = getStandardCameraPosition();
		
		// System.out.println("NonInteractiveTIMEngine::getStandardCamera: Camera position = "+cameraPosition);
		
//		QualityType blurQuality, aaQuality;
//		switch(renderQuality)
//		{
//		case STANDARD:
//			aaQuality = standardCameraAntiAliasingQuality;
//			blurQuality = cameraApertureSize==ApertureSizeType.PINHOLE?QualityType.RUBBISH:standardCameraBlurQuality;
//			break;
//		case GREAT:
//			aaQuality = standardCameraAntiAliasingQuality;
//			blurQuality = cameraApertureSize==ApertureSizeType.PINHOLE?QualityType.RUBBISH:standardCameraBlurQuality;
//			break;
//		case DRAFT:
//		default:
//			blurQuality = QualityType.RUBBISH;
//			aaQuality = QualityType.NORMAL;
//		}
		
		Vector3D topDirection = new Vector3D(0, 1, 0);
		if(cameraViewDirection.getPartPerpendicularTo(topDirection).getLength() == 0) topDirection = new Vector3D(1, 0, 0);
		return new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				cameraPosition,	// centre of aperture
				cameraViewDirection,	// viewDirection
				topDirection,	// top direction vector
				cameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				cameraSpaceTimeTransformationType,	// spaceTimeTransformationType
				cameraBeta,	// beta
				cameraPixelsX, cameraPixelsY,	// logical number of pixels
				cameraExposureCompensation,	// ExposureCompensationType.EC0,	// exposure compensation +0
				cameraMaxTraceLevel,	// maxTraceLevel
				new Plane(
						"focus plane",	// description
						Vector3D.sum(cameraPosition, cameraViewDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
						cameraViewDirection,	// normal
						null,	// surfaceProperty
						null,	// parent
						null	// studio
					),	// focus scene
				null,	// cameraFrameScene,
				cameraApertureSize,	// aperture size
				renderQuality.getBlurQuality(),	// blur quality
				renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			);

	}
	
	
	
	// GUI variables
	
	/**
	 * the window's content pane, where the frames are being displayed
	 */
	protected Container container;
	
	/**
	 * the area in which the image is being displayed
	 */
//	protected PhotoCanvas photoCanvas;
	protected RaytracingImageCanvas raytracingImageCanvas;
	
	/**
	 * the control panel (interactive only)
	 */
	protected JPanel interactiveControlPanel;
	
	/**
	 * a text field at the bottom that displays status information
	 * (additional status information will be displayed on the console)
	 */
	protected JLabel statusField;
	
	private String status, temporaryStatus;
	
	/**
	 * a combo box to select the renderQuality (interactive only)
	 */
	protected JComboBox<RenderQualityEnum> renderQualityComboBox;

	/**
	 * a button for rendering (or to stop rendering, while rendering) (interactive only)
	 */
	protected JButton renderButton;
	
	// possible texts displayed by the renderButton
	protected final String RENDER_BUTTON_RENDER_TEXT = "Render";
	protected final String RENDER_BUTTON_STOP_TEXT = "Stop";

	/**
	 * a button for saving (interactive only)
	 */
	protected JButton saveButton;

	
	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	protected void createInteractiveControlPanel()
	{
		interactiveControlPanel = new JPanel();
		interactiveControlPanel.setLayout(new MigLayout("insets 0"));
		
		renderQualityComboBox = new JComboBox<RenderQualityEnum>(RenderQualityEnum.values());
		renderQualityComboBox.setSelectedItem(renderQuality);
		renderQualityComboBox.addActionListener(this);
		// interactiveControlPanel.add(renderQualityComboBox);	// , "split 2");

		renderButton = new JButton(RENDER_BUTTON_RENDER_TEXT);
		renderButton.addActionListener(this);
		// interactiveControlPanel.add(renderButton);
		
		saveButton = new JButton("Save...");
		saveButton.addActionListener(this);
		
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow(renderQualityComboBox, renderButton, saveButton), "wrap");		
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	protected void acceptValuesInInteractiveControlPanel()
	{}
	
	/**
	 * Opens a window and initiates the calculation of a series of frames (if movie=true)
	 * or a single frame (if movie=false).
	 * @see movie
	 * @see createStudio
	 * @author	Johannes Courtial
	 */
	@Override
	public void run()
	{
		// open a window
		container = (new PhotoFrame(windowTitle, windowWidth, windowHeight, true)).getContentPane();
		container.setLayout(new BorderLayout());
		
		// create the status field 
		// (the PhotoCanvas, in which the image is being displayed, will be "lazily" added in setRenderedImage)
		statusField = new JLabel();
		container.add(statusField, BorderLayout.SOUTH);

		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			createInteractiveControlPanel();
			container.add(new JScrollPane(interactiveControlPanel), BorderLayout.EAST);
		}

//		This stuff needs to move into render()
//		System.out.println("NonInteractiveTIMEngine::run: movie="+movie);
//
//		if(movie || (nonInteractiveTIMAction == NonInteractiveTIMActionEnum.BATCH_RUN))
//		{
//			wasCancelled = false;
//			for(frame=firstFrame; (frame<=lastFrame) && !wasCancelled; frame++)
//			{
//				System.out.println("NonInteractiveTIMEngine::run: frame="+frame);
//				render();
//			}
//		}
//		else
//		{
//			frame = firstFrame;
//			render();
//		}

		frame = firstFrame;
		render();
	}

	
	//
	// RenderPanel methods
	// @see optics.raytrace.GUI.lowLevel.RenderPanel
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.lowLevel.RenderPanel#setRenderedImage(java.awt.image.BufferedImage)
	 */
	@Override
	public void setRenderedImage(BufferedImage image)
	{
		if(raytracingImageCanvas == null)
		{
			// open a new PhotoCanvas
			// new PhotoCanvas(image);
			raytracingImageCanvas = new RaytracingImageCanvas(
					image,	// image
					cameraPixelsX,	// imageCanvasSizeX
					cameraPixelsY,	// imageCanvasSizeY
					this,	// statusIndicator
					false,	// showEditSceneObjectMenuItem,
					false,	// showAddLocalCoordinateAxesMenuItem
					studio,
					null
				);
			container.add(
					// new JScrollPane(	// TODO doesn't work, for some reason
							raytracingImageCanvas
					// 	)
					, BorderLayout.CENTER);
			container.validate();
		}
		else
		{
			raytracingImageCanvas.setStudio(studio);
			raytracingImageCanvas.setImage(studio.getPhoto());
		}
	}

	private RaytraceWorker raytraceWorker;

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.lowLevel.RenderPanel#render()
	 */
	@Override
	public void render()
	{
		// set the status appropriately
		switch(nonInteractiveTIMAction)
		{
		case INTERACTIVE:
			System.out.println("Rendering"+(movie?" movie frame #"+frame:"")+"; hit Stop button to stop");
			break;
		case RUN:
		case RUN_AND_SAVE:
			System.out.println("Rendering"+(movie?" movie frame #"+frame:"")+"...");
			break;
		case BATCH_RUN:
			System.out.println("Rendering frame #"+frame);
//			break;
//		case MOVIE:
//			setStatus("Rendering movie frame #"+frame);
		}

		// set the cursor to the "wait" cursor
		container.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			// disable buttons while rendering
			saveButton.setEnabled(false);

			// change the "Render" button to a "Stop" button
			renderButton.setText(RENDER_BUTTON_STOP_TEXT);
		}
		
		// define scene, lights and camera
		try {
			populateStudio();
		} catch (SceneException e) {
			e.printStackTrace();
		}

		//Instances of javax.swing.SwingWorker are not reusuable, so
		//we create new instances as needed.
		raytraceWorker = new RaytraceWorker(studio, this, this, traceRaysWithTrajectory);
		raytraceWorker.execute();
	}
	
	public void writeCameraParameters(PrintStream printStream)
	{
		printStream.println();
		printStream.println("Camera parameters");
		printStream.println();

//		protected Vector3D cameraViewDirection;
		printStream.println("cameraViewDirection = "+cameraViewDirection);

//		protected Vector3D cameraViewCentre;
		printStream.println("cameraViewCentre = "+cameraViewCentre);

//		protected double cameraDistance;
		printStream.println("cameraDistance = "+cameraDistance);

//		protected double cameraHorizontalFOVDeg;
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);

		printStream.println("cameraSpaceTimeTransformationType = "+cameraSpaceTimeTransformationType);
		printStream.println("cameraBeta = "+cameraBeta);

//		protected int cameraMaxTraceLevel;
		printStream.println("cameraMaxTraceLevel = "+cameraMaxTraceLevel);

//		protected int cameraPixelsX;
		printStream.println("cameraPixelsX = "+cameraPixelsX);

//		protected int cameraPixelsY;
		printStream.println("cameraPixelsY = "+cameraPixelsY);

//		protected ApertureSizeType cameraApertureSize;
		printStream.println("cameraApertureSize = "+cameraApertureSize);		

		switch(cameraApertureSize)
		{
		case PINHOLE:
		case INFINITESIMAL:
			// it doesn't matter where the camera is focussed, so no need to list the focussing distance
			break;
		default:
			// in all other cases, the focussing distance does matter, so list it
//			protected double cameraFocussingDistance;
			printStream.println("cameraFocussingDistance = "+cameraFocussingDistance);
		}
		
		printStream.println("cameraExposureCompensation = "+cameraExposureCompensation);
}
	
	public void writeMovieParameters(PrintStream printStream)
	{
		printStream.println();
		printStream.println("Movie parameters");
		printStream.println();

//		protected int numberOfFrames;
		printStream.println("numberOfFrames = "+numberOfFrames);
		
//		protected int firstFrame;
		printStream.println("firstFrame = "+firstFrame);
		
//		protected int lastFrame;
		printStream.println("lastFrame = "+lastFrame);
	}
	
	/**
	 * Create a new .txt file and save the parameters into it
	 * @param filename
	 */
	public void saveParameters(String filename)
	{
		System.out.println("saving parameters as .txt file...");

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(filename+"_parameters.txt");
			PrintStream printStream = new PrintStream(fileOutputStream);
			
			writeParameters(printStream);

			printStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("...done.");
	}

	@Override
	public void renderingDone(boolean wasCancelled)
	{
		if(!wasCancelled)
		{
			// save the image if appropriate
			if(
					(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.BATCH_RUN) ||
					movie || // (nonInteractiveTIMAction == NonInteractiveTIMActionEnum.MOVIE) ||
					(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.RUN_AND_SAVE)
				)
			{
				studio.savePhoto(getFilename(), "bmp");
				saveParameters(getFilename());
			}
			
			// is this part of a batch run or movie?
			// 
			// The render method executes a RaytraceWorker, which renders a single frame and calls this
			// class's renderingDone method when the frame is complete.  The renderingDone method then
			// calls the render method again if more frames need to be calculated.

			if(
					(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.BATCH_RUN) ||
					movie // (nonInteractiveTIMAction == NonInteractiveTIMActionEnum.MOVIE)
				)
			{
				// yes, do more frames need to be calculated?
				if(frame < lastFrame)
				{
					// calculate the next movie frame
					frame++;
					// System.out.println("Calculating frame "+frame);
					render();
					
					// exit the method, i.e. keep the "clock" cursor
					return;
				}
			}
		}

		// are we done with rendering?
		if(
				wasCancelled ||
				(!movie && (nonInteractiveTIMAction != NonInteractiveTIMActionEnum.BATCH_RUN)) ||
				(frame >= lastFrame)
			)
		{
			// yes, we are done
			
			// turn off the wait cursor
			container.setCursor(null);

			if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
			{
				// change the "Render"-button text from "Stop" back to "Render"
				renderButton.setText(RENDER_BUTTON_RENDER_TEXT);

				// enable all buttons again
				saveButton.setEnabled(true);
			}
		}
	}


	@Override
	public void repaint() {
		container.repaint();
	}

	
	//
	// StatusIndicator methods
	//

//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.lowLevel.StatusIndicator#setStatus(java.lang.String)
//	 * display <status> in <statusField>
//	 */
//	@Override
//	public void setStatus(String status) {
//		if(statusField != null)
//		{
//			statusField.setText(status);
//		}
//		else
//		{
//			System.out.println(status);
//		}
//	}

	/**
	 * set the status and refresh
	 * @param status
	 */
	@Override
	public void setStatus(String status)
	{
		this.status = status;
		
		// display the new status only if no (higher-priority) temporary status is displayed
		if(temporaryStatus == null) statusField.setText(status);
	}
	
	/*
	 * (non-Javadoc)
	 * @see optics.raytrace.GUI.panels.StatusIndicator#getStatus()
	 */
	@Override
	public String getStatus()
	{
		return status;
	}
	
	/*
	 * sometimes it's handy to display something in the status line that overrides the "real" status,
	 * e.g. the coordinates over which the mouse is hovering
	 */
	@Override
	public void setTemporaryStatus(String temporaryStatus)
	{
		this.temporaryStatus = temporaryStatus;
		statusField.setText(temporaryStatus);
	}
	
	@Override
	public void removeTemporaryStatus()
	{
		if(temporaryStatus != null)
		{
			temporaryStatus = null;
			statusField.setText(status);
		}
	}
	
	@Override
	public boolean isTemporaryStatus()
	{
		return temporaryStatus != null;
	}
	

	//Create a file chooser
	private JFileChooser fileChooser;
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(renderQualityComboBox))
		{
			setRenderQuality((RenderQualityEnum)renderQualityComboBox.getSelectedItem());
			// render();
		}
		else if(e.getSource().equals(renderButton))
		{
			if(renderButton.getText().equals(RENDER_BUTTON_RENDER_TEXT))
			{
				acceptValuesInInteractiveControlPanel();
				if(movie)
				{
					// this is a movie; get a filename stub
					//  TODO
				}
				frame = firstFrame;
				render();
			}
			else
			{
				// the render button is currently a stop button
				raytraceWorker.cancel(true);
			}
		}
		else if(e.getSource().equals(saveButton))
		{
			if(fileChooser == null) { fileChooser = new JFileChooser(); }
			fileChooser.setSelectedFile(new File(getFilename()));
			int returnVal = fileChooser.showSaveDialog(container);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // System.out.println("NonInteractiveTIMEngine:actionPerformed:: filename ="+file.getName());
    			studio.savePhoto(file.getAbsolutePath(), "bmp");
    			setStatus("Image saved as \""+file.getAbsolutePath()+"\".");
    			
    			saveParameters(file.getAbsolutePath());
            }
			else
			{
				setStatus("Saving cancelled.");
			}
		}
	}
}
