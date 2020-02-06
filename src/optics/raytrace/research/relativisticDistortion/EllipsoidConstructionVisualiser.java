package optics.raytrace.research.relativisticDistortion;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.Matrix3D;
import math.MyMath;
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
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera.TransformType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A very basic example of NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class EllipsoidConstructionVisualiser extends NonInteractiveTIMEngine
{
	
	private Vector3D betaHat, cameraViewDirection0;
	private double beta0, cameraDistance0, ellipsoidPrincipalRadiusInBetaDirection;
	private boolean simulateAsEllipsoidConstruction, showBetaHatArrow;
	
	TransformType transformType;

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
		
		transformType = TransformType.LORENTZ_TRANSFORM;
		betaHat = new Vector3D(0, 0, 1);
		beta0 = 0.99;
		showBetaHatArrow = false;
		
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
	public String getFirstPartOfFilename()
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

		printStream.println("transformType = "+transformType);
		printStream.println("betaHat = "+ betaHat);
		printStream.println("beta0 = "+beta0);
		printStream.println("showBetaHatVector = "+showBetaHatArrow);

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
		double beta = beta0;
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
			cameraDistance = cameraDistance0 - 2*cameraDistance0*frame/(numberOfFrames-1);
			
			// vary the cameraDistance from cameraDistance0 to 0
			// cameraDistance = cameraDistance0*(numberOfFrames-1-frame)/(numberOfFrames-1);
			
			// System.out.println("camera distance = "+cameraDistance);

			break;
		case MOVIE_VARY_BETA:
			movie = true;
			
			// vary the value of beta
			beta = beta0*frame/(numberOfFrames-1);
			
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
		Vector3D betaHatArrowStartPoint = betaHat.getProductWith((1+beta)*ellipsoidPrincipalRadiusInBetaDirection);	// set the start point such that the foot of the arrow is in front of the ellipsoid
		scene.addSceneObject(
				new EditableArrow(
						"betaHat",	// description
						betaHatArrowStartPoint,	// startPoint
						Vector3D.sum(betaHatArrowStartPoint, betaHat.getWithLength(beta*ellipsoidPrincipalRadiusInBetaDirection)),	// endPoint
						beta*ellipsoidPrincipalRadiusInBetaDirection*0.05,	// shaftRadius
						beta*ellipsoidPrincipalRadiusInBetaDirection*0.25,	// tipLength
						MyMath.deg2rad(20),	// tipAngle
						SurfaceColour.WHITE_SHINY,	// surfaceProperty
						scene,	// parent 
						studio
					), showBetaHatArrow);

		if(simulateAsEllipsoidConstruction)
		{
			// scene.addSceneObject(new Sphere("Sphere", new Vector3D(0, 0, 10), 1, SurfaceColour.CYAN_SHINY, scene, studio));

			RelativisticDistortionEllipsoidConstructionSurface s =
					new RelativisticDistortionEllipsoidConstructionSurface(new Vector3D(0, 0, 0), betaHat, beta, transformType, ellipsoidPrincipalRadiusInBetaDirection);
			
			scene.addSceneObject(s.createAndSetEllipsoid("Ellipsoid", scene, studio));

			cameraBeta = new Vector3D(0, 0, 0);
		}
		else
		{
			// studio.setScene(scene);
			cameraBeta = betaHat.getProductWith(beta);
		}
		studio.setScene(scene);
		
		RelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		camera.setTransformType(transformType);
		
		studio.setCamera(camera);

		
		// add anything to the scene by uncommenting the following line...
		// SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		// ... and then adding scene objects to scene
	}

	
	// interactive stuff
	
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JComboBox<TransformType> transformTypeComboBox;
	private JComboBox<MovieType> movieTypeComboBox;
	private LabelledVector3DPanel betaVectorPanel, initialCameraViewDirectionPanel;
	private LabelledDoublePanel initialCameraDistancePanel, cameraHorizontalFOVDegPanel, ellipsoidPrincipalRadiusInBetaDirectionPanel;
	private LabelledIntPanel numberOfFramesPanel;
	private JCheckBox simulateAsEllipsoidConstructionCheckBox, showBetaHatArrowCheckBox;

	
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
		
		transformTypeComboBox = new JComboBox<TransformType>(TransformType.values());
		transformTypeComboBox.setSelectedItem(transformType);
		interactiveControlPanel.add(transformTypeComboBox, "span");

		betaVectorPanel = new LabelledVector3DPanel("Beta");
		betaVectorPanel.setVector3D(betaHat.getProductWith(beta0));
		interactiveControlPanel.add(betaVectorPanel, "span");
		
		showBetaHatArrowCheckBox = new JCheckBox("Show arrow indicating direction of beta");
		showBetaHatArrowCheckBox.setSelected(showBetaHatArrow);
		interactiveControlPanel.add(showBetaHatArrowCheckBox, "span");

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
		
		transformType = (TransformType)(transformTypeComboBox.getSelectedItem());
		simulateAsEllipsoidConstruction = simulateAsEllipsoidConstructionCheckBox.isSelected();
		ellipsoidPrincipalRadiusInBetaDirection = ellipsoidPrincipalRadiusInBetaDirectionPanel.getNumber();
		
		Vector3D betaVector = betaVectorPanel.getVector3D();
		beta0 = betaVector.getLength();
		betaHat = ((beta0!=0.0)?betaVector.getNormalised():Vector3D.Z);
		showBetaHatArrow = showBetaHatArrowCheckBox.isSelected();
		
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
