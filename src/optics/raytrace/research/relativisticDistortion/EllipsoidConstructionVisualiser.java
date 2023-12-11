package optics.raytrace.research.relativisticDistortion;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Matrix3D;
import math.MyMath;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableText;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.DistortedLookalikeSphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class EllipsoidConstructionVisualiser extends NonInteractiveTIMEngine
{
	
	private Vector3D beta0, cameraViewDirection0, xyzCoordinateSystemOrigin;
	private double cameraDistance0, ellipsoidPrincipalRadiusInBetaDirection, xyzCoordinateSystemSize;
	private boolean simulateAsEllipsoidConstruction, showBetaHatArrow, showXYZCoordinateSystem, showXYZCoordinateSystemLabels;
	
	public enum MovieType
	{
		STILL_IMAGE("Still image"),
		MOVIE_Y_ROTATION("Rotation of camera view direction around vertical"),
		MOVIE_CAMERA_DISTANCE("Vary camera distance from +(given value) to -(given value)"),
		MOVIE_VARY_BETA("Vary beta from (0,0,0) to the given vector");
		
		private String description;
		private MovieType(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	}
	MovieType movieType;

	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public EllipsoidConstructionVisualiser()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		numberOfFrames = 20;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		movieType = MovieType.STILL_IMAGE;
		
		
		cameraViewDirection0 = new Vector3D(0, 0, 1);
		cameraDistance0 = 0;
		cameraHorizontalFOVDeg = 40;
		
		cameraSpaceTimeTransformationType = SpaceTimeTransformationType.LORENTZ_TRANSFORMATION;
		beta0 = new Vector3D(0, 0, 0.99);
		showBetaHatArrow = false;
		
		showXYZCoordinateSystem = false;
		showXYZCoordinateSystemLabels = false;
		xyzCoordinateSystemOrigin = new Vector3D(0, 0, 0);
		xyzCoordinateSystemSize = 1;
		
		studioInitialisation = StudioInitialisationType.SURROUND_LATTICE;	// the backdrop

		simulateAsEllipsoidConstruction = true;
		ellipsoidPrincipalRadiusInBetaDirection = 0.4;
		
		// camera parameters are set in createStudio()
		
		windowTitle = "Dr TIM's ellipsoid-construction visualiser";
		windowWidth = 1500;
		windowHeight = 650;

	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"EllipsoidConstructionVisualiser"
				;
	}
	
	/*
	 * Write all parameters to a .txt file
	 * @see optics.raytrace.NonInteractiveTIMEngine#writeParameters(java.io.PrintStream)
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		printStream.println("cameraSpaceTimeTransformationType = "+cameraSpaceTimeTransformationType);
		printStream.println("beta0 = "+beta0);
		printStream.println("showBetaHatArrow = "+showBetaHatArrow);
		
		printStream.println("showXYZCoordinateSystem = "+showXYZCoordinateSystem);
		printStream.println("xyzCoordinateSystemOrigin = "+xyzCoordinateSystemOrigin);
		printStream.println("xyzCoordinateSystemSize = "+xyzCoordinateSystemSize);

		printStream.println("simulateAsEllipsoidConstruction = "+simulateAsEllipsoidConstruction);
		printStream.println("ellipsoidPrincipalRadiusInBetaDirection = "+ellipsoidPrincipalRadiusInBetaDirection);

		printStream.println("cameraViewDirection0 = "+cameraViewDirection0);
		printStream.println("cameraDistance0 = "+cameraDistance0);
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);
		
		printStream.println("movieType = "+movieType);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// standard values, which will be varied by the various movies
		Vector3D beta = beta0;
		cameraViewDirection = cameraViewDirection0;
		cameraDistance = cameraDistance0;

		switch(movieType)
		{
		case MOVIE_Y_ROTATION:
			movie = true;
			
			// vary the cameraViewDirection
			double phi = 2.*Math.PI*frame/numberOfFrames;
			//  cameraViewDirection = new Vector3D(-Math.sin(phi), -.2, Math.cos(phi)).getNormalised();
			cameraViewDirection = Matrix3D.rotateVector(
					cameraViewDirection0,	// vector to be rotated
					-phi,	// rotationAngle
					Vector3D.Y	// rotationAxis
				);

			break;
		case MOVIE_CAMERA_DISTANCE:
			movie = true;

			// vary the cameraDistance from +cameraDistance0 to -cameraDistance0
			cameraDistance = cameraDistance0 - 2*cameraDistance0*frame/(numberOfFrames-1.);
			
			// vary the cameraDistance from cameraDistance0 to 0
			// cameraDistance = cameraDistance0*(numberOfFrames-1-frame)/(numberOfFrames-1);
			
			// System.out.println("camera distance = "+cameraDistance);

			break;
		case MOVIE_VARY_BETA:
			movie = true;
			
			// vary the value of beta
			beta = beta0.getProductWith(frame/(numberOfFrames-1.));
			
			break;
		case STILL_IMAGE:
		default:
			movie = false;
		}

		// in any case, have the origin at the centre of the view
		cameraViewCentre = new Vector3D(0, 0, 0);

//		if(outsideCameraPosition)
//		{
//			// either
//
//			cameraDistance = 10;	// camera is located at (0, 0, 0)
//			cameraViewCentre = new Vector3D(0, 0, 0);	// this places the camera at the origin
//			cameraHorizontalFOVDeg = 25;
//		}
//		else
//		{
//			// or
//			cameraDistance = 1;
//			cameraViewCentre = cameraViewDirection;	// this places the camera at the origin
//			cameraHorizontalFOVDeg = 40;
//		}	
		
		// Vector3D cameraPosition = Vector3D.difference(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance));

		cameraFocussingDistance = 10;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// add any other scene objects
		// Vector3D betaHatArrowStartPoint = Vector3D.difference(new Vector3D(0, 1.2*ellipsoidPrincipalRadiusInBetaDirection, 0), betaHat.getProductWith(ellipsoidPrincipalRadiusInBetaDirection));	// set the start point such that the arrow is centred above the camera position
		Vector3D betaHatArrowStartPoint = 
				beta.getWithLength((1+beta.getLength())*ellipsoidPrincipalRadiusInBetaDirection);	// set the start point such that the foot of the arrow is in front of the ellipsoid
		scene.addSceneObject(
				new EditableArrow(
						"betaHat",	// description
						betaHatArrowStartPoint,	// startPoint
						Vector3D.sum(betaHatArrowStartPoint, beta.getProductWith(ellipsoidPrincipalRadiusInBetaDirection)),	// endPoint
						beta.getLength()*ellipsoidPrincipalRadiusInBetaDirection*0.05,	// shaftRadius
						beta.getLength()*ellipsoidPrincipalRadiusInBetaDirection*0.25,	// tipLength
						MyMath.deg2rad(20),	// tipAngle
						SurfaceColour.WHITE_SHINY,	// surfaceProperty
						scene,	// parent 
						studio
					), showBetaHatArrow);
		
		if(showXYZCoordinateSystem)
		{
			SurfaceProperty arrowsSurface = SurfaceColour.WHITE_MATT;
			SurfaceProperty labelsSurface = SurfaceColour.WHITE_MATT;
			
			scene.addSceneObject(new EditableArrow(
					"xHat",	// description
					xyzCoordinateSystemOrigin,
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.X.getWithLength(xyzCoordinateSystemSize)),	// endPoint
					arrowsSurface,	// surfaceProperty
					scene,	// parent
					studio
					));
			scene.addSceneObject(new EditableText(
					"xHat label",	// description
					"<i>x</i>",	// text
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.X.getWithLength(xyzCoordinateSystemSize), new Vector3D(0, -0.2*xyzCoordinateSystemSize, 0)),	// bottom left corner
					Vector3D.X,	// rightDirection
					Vector3D.Y,	// upDirection
					512,	// fontSize
					"Arial",	// fontFamily
					0.5*xyzCoordinateSystemSize,	// height
					labelsSurface,	// textSurfaceProperty
					scene, studio
				), showXYZCoordinateSystemLabels);

			scene.addSceneObject(new EditableArrow(
					"yHat",	// description
					xyzCoordinateSystemOrigin,
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.Y.getWithLength(xyzCoordinateSystemSize)),	// endPoint
					arrowsSurface,	// surfaceProperty
					scene,	// parent
					studio
					));
			scene.addSceneObject(new EditableText(
					"yHat label",	// description
					"<i>y</i>",	// text
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.Y.getWithLength(xyzCoordinateSystemSize), new Vector3D(-0.2*xyzCoordinateSystemSize, 0, 0)),	// bottom left corner
					Vector3D.X,	// rightDirection
					Vector3D.Y,	// upDirection
					512,	// fontSize
					"Arial",	// fontFamily
					0.5*xyzCoordinateSystemSize,	// height
					labelsSurface,	// textSurfaceProperty
					scene, studio
				), showXYZCoordinateSystemLabels);

			scene.addSceneObject(new EditableArrow(
					"zHat",	// description
					xyzCoordinateSystemOrigin,
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.Z.getWithLength(xyzCoordinateSystemSize)),	// endPoint
					arrowsSurface,	// surfaceProperty
					scene,	// parent
					studio
					));
			scene.addSceneObject(new EditableText(
					"zHat label",	// description
					"<i>z</i>",	// text
					Vector3D.sum(xyzCoordinateSystemOrigin, Vector3D.Z.getWithLength(xyzCoordinateSystemSize), new Vector3D(-0.2*xyzCoordinateSystemSize, -0.2*xyzCoordinateSystemSize, 0.2*xyzCoordinateSystemSize)),	// bottom left corner
					Vector3D.X,	// rightDirection
					Vector3D.Y,	// upDirection
					512,	// fontSize
					"Arial",	// fontFamily
					0.5*xyzCoordinateSystemSize,	// height
					labelsSurface,	// textSurfaceProperty
					scene, studio
				), showXYZCoordinateSystemLabels);
		}

		if(simulateAsEllipsoidConstruction)
		{
			// scene.addSceneObject(new Sphere("Sphere", new Vector3D(0, 0, 10), 1, SurfaceColour.CYAN_SHINY, scene, studio));

			scene.addSceneObject(
					new DistortedLookalikeSphere(
							"Distorted lookalike sphere",	// description
							new Vector3D(0, 0, 0),
							beta,
							cameraSpaceTimeTransformationType,
							ellipsoidPrincipalRadiusInBetaDirection,
							1,
							scene,
							studio
							));
						
//			RelativisticDistortionEllipsoidConstructionSurface s =
//					new RelativisticDistortionEllipsoidConstructionSurface(
//							new Vector3D(0, 0, 0),
//							beta,
//							cameraSpaceTimeTransformationType,
//							ellipsoidPrincipalRadiusInBetaDirection
//						);
//			
//			scene.addSceneObject(s.createAndSetEllipsoid("Ellipsoid", scene, studio));

			cameraBeta = new Vector3D(0, 0, 0);
		}
		else
		{
			// studio.setScene(scene);
			cameraBeta = beta;
		}
		studio.setScene(scene);
		
		RelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		// camera.setSpaceTimeTransformation(spaceTimeTransformationType, cameraBeta);
		// setTransformType(transformType);
		
		studio.setCamera(camera);

		
		// add anything to the scene by uncommenting the following line...
		// SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
	}

	
	// interactive stuff
	
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JComboBox<SpaceTimeTransformationType> cameraSpaceTimeTransformationTypeComboBox;
	private JComboBox<MovieType> movieTypeComboBox;
	private LabelledVector3DPanel betaPanel, initialCameraViewDirectionPanel, xyzCoordinateSystemOriginPanel;
	private LabelledDoublePanel initialCameraDistancePanel, cameraHorizontalFOVDegPanel, ellipsoidPrincipalRadiusInBetaDirectionPanel, xyzCoordinateSystemSizePanel;
	private LabelledIntPanel numberOfFramesPanel;
	private JCheckBox simulateAsEllipsoidConstructionCheckBox, showBetaHatArrowCheckBox, showXYZCoordinateSystemCheckBox, showXYZCoordinateSystemLabelsCheckBox;

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForInteractiveTIM);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		cameraSpaceTimeTransformationTypeComboBox = new JComboBox<SpaceTimeTransformationType>(SpaceTimeTransformationType.values());
		cameraSpaceTimeTransformationTypeComboBox.setSelectedItem(cameraSpaceTimeTransformationType);
		interactiveControlPanel.add(cameraSpaceTimeTransformationTypeComboBox, "span");

		betaPanel = new LabelledVector3DPanel("Beta");
		betaPanel.setVector3D(beta0);
		interactiveControlPanel.add(betaPanel, "span");
		
		showBetaHatArrowCheckBox = new JCheckBox("Show arrow indicating direction of beta");
		showBetaHatArrowCheckBox.setSelected(showBetaHatArrow);
		interactiveControlPanel.add(showBetaHatArrowCheckBox, "span");
		
		JPanel xyzCoordinateSystemPanel = new JPanel();
		xyzCoordinateSystemPanel.setBorder(GUIBitsAndBobs.getTitledBorder("(x,y,z) coordinate system"));
		xyzCoordinateSystemPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(xyzCoordinateSystemPanel, "span");
		
		showXYZCoordinateSystemCheckBox = new JCheckBox("Show");
		showXYZCoordinateSystemCheckBox.setSelected(showXYZCoordinateSystem);
		xyzCoordinateSystemPanel.add(showXYZCoordinateSystemCheckBox, "span");
		
		showXYZCoordinateSystemLabelsCheckBox = new JCheckBox("Show labels");
		showXYZCoordinateSystemLabelsCheckBox.setSelected(showXYZCoordinateSystemLabels);
		xyzCoordinateSystemPanel.add(showXYZCoordinateSystemLabelsCheckBox, "span");
		
		xyzCoordinateSystemOriginPanel = new LabelledVector3DPanel("Origin");
		xyzCoordinateSystemOriginPanel.setVector3D(xyzCoordinateSystemOrigin);
		xyzCoordinateSystemPanel.add(xyzCoordinateSystemOriginPanel, "span");

		xyzCoordinateSystemSizePanel = new LabelledDoublePanel("Size");
		xyzCoordinateSystemSizePanel.setNumber(xyzCoordinateSystemSize);
		xyzCoordinateSystemPanel.add(xyzCoordinateSystemSizePanel, "span");

		simulateAsEllipsoidConstructionCheckBox = new JCheckBox("Simulate as ellipsoid construction?");
		simulateAsEllipsoidConstructionCheckBox.setSelected(simulateAsEllipsoidConstruction);
		interactiveControlPanel.add(simulateAsEllipsoidConstructionCheckBox, "span");
		
		ellipsoidPrincipalRadiusInBetaDirectionPanel = new LabelledDoublePanel("Principal radius of ellipsoid in beta direction");
		ellipsoidPrincipalRadiusInBetaDirectionPanel.setNumber(ellipsoidPrincipalRadiusInBetaDirection);
		interactiveControlPanel.add(ellipsoidPrincipalRadiusInBetaDirectionPanel, "span");
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(cameraPanel, "span");

		initialCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		initialCameraViewDirectionPanel.setVector3D(cameraViewDirection0);
		cameraPanel.add(initialCameraViewDirectionPanel, "span");
		
		initialCameraDistancePanel = new LabelledDoublePanel("Distance from camera position for which ellipsoid is designed");
		initialCameraDistancePanel.setNumber(cameraDistance0);
		cameraPanel.add(initialCameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new LabelledDoublePanel("Horizontal FOV (in degrees)");
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(cameraHorizontalFOVDegPanel, "span");
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(moviePanel, "span");

		movieTypeComboBox = new JComboBox<MovieType>(MovieType.values());
		movieTypeComboBox.setSelectedItem(movieType);
		moviePanel.add(GUIBitsAndBobs.makeRow("Movie type", movieTypeComboBox), "span");
		
		numberOfFramesPanel = new LabelledIntPanel("No of frames");
		numberOfFramesPanel.setNumber(numberOfFrames);
		moviePanel.add(numberOfFramesPanel,  "span");
		
//		movieCheckBox = new JCheckBox("movie");
//		movieCheckBox.setSelected(movie);
//		interactiveControlPanel.add(movieCheckBox, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
				
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		cameraSpaceTimeTransformationType = (SpaceTimeTransformationType)(cameraSpaceTimeTransformationTypeComboBox.getSelectedItem());
		simulateAsEllipsoidConstruction = simulateAsEllipsoidConstructionCheckBox.isSelected();
		ellipsoidPrincipalRadiusInBetaDirection = ellipsoidPrincipalRadiusInBetaDirectionPanel.getNumber();
		
		beta0 = betaPanel.getVector3D();
		showBetaHatArrow = showBetaHatArrowCheckBox.isSelected();
		
		showXYZCoordinateSystem = showXYZCoordinateSystemCheckBox.isSelected();
		showXYZCoordinateSystemLabels = showXYZCoordinateSystemLabelsCheckBox.isSelected();
		xyzCoordinateSystemOrigin = xyzCoordinateSystemOriginPanel.getVector3D();
		xyzCoordinateSystemSize = xyzCoordinateSystemSizePanel.getNumber();
		
		cameraViewDirection0 = initialCameraViewDirectionPanel.getVector3D();
		cameraDistance0 = initialCameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		
		movieType = (MovieType)(movieTypeComboBox.getSelectedItem());
		numberOfFrames = numberOfFramesPanel.getNumber();
		lastFrame = numberOfFrames-1;

		// movie = movieCheckBox.isSelected();
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new EllipsoidConstructionVisualiser()).run();
	}
}
