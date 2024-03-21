package optics.raytrace.research.relativisticDistortion;

import java.io.PrintStream;

import javax.swing.JCheckBox;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.DistortedLookalikeSphere;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * A simulation similar to Fig. 14 in https://www.andrewyork.net/Math/TerrellRotation_York.html
 * 
 * @author Johannes Courtial
 */
public class TerrellRotationExplorer extends NonInteractiveTIMEngine
{
	private static final long serialVersionUID = 7475313972298704169L;

	private SpaceTimeTransformationType spaceTimeTransformationType;
	private Vector3D beta;
	private double shutterOpeningTime;
    private boolean simulateAsEllipsoidConstruction;
    private double lookalikeSphereRadius, lookalikeSphereTransmissionCoefficient;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public TerrellRotationExplorer()
	{
		super();
		
		spaceTimeTransformationType = SpaceTimeTransformationType.LORENTZ_TRANSFORMATION;
	    beta = new Vector3D(0, 0, 0.99);
	    shutterOpeningTime = 0;
	    simulateAsEllipsoidConstruction = false;
	    lookalikeSphereRadius = 1;
	    lookalikeSphereTransmissionCoefficient = 0.8;
	    
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 10);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 50;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;


		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		movie = false;
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		windowTitle = "Dr TIM's Terrell-rotation explorer";
		windowWidth = 1350;
		windowHeight = 650;
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"TerrellRotationExplorer"
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

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	private Vector3D cameraPosition;
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects
		// scene.addSceneObject(new Sphere("The sphere", new Vector3D(0,0,0), 1, SurfaceColour.CYAN_SHINY, scene, studio));
		scene.addSceneObject(createCuboid(new Vector3D(0, 1.5, 10), scene, studio));
		scene.addSceneObject(createCuboid(new Vector3D(-2, 1.5, 10), scene, studio));
		scene.addSceneObject(createCuboid(new Vector3D(2, 1.5, 10), scene, studio));
		
		// calculate transformed camera position
		SpaceTimeTransformation stt;
		switch(spaceTimeTransformationType)
		{
		case GALILEAN_TRANSFORMATION:
			stt = new GalileanTransformation(beta.getReverse());
			break;
		case LORENTZ_TRANSFORMATION:
		default:
			stt = new LorentzTransformation(beta.getReverse());
		}
		Vector3D transformedPinholePosition = stt.getTransformedPosition(new Vector3D(0, 0, 0), shutterOpeningTime);
		pinholePositionPanel.setVector3D(transformedPinholePosition);

		if(simulateAsEllipsoidConstruction)
		{
			scene.addSceneObject(
					new DistortedLookalikeSphere(
							"Distorted lookalike sphere",	// description
							transformedPinholePosition,	// camera position
							beta,
							cameraSpaceTimeTransformationType,
							lookalikeSphereRadius,
							lookalikeSphereTransmissionCoefficient,
							scene,
							studio
						),
					true	// visibility
				);
			
			// take the picture from the transformed camera position...
			cameraPosition = transformedPinholePosition;
			// ... and with the camera at rest
			cameraBeta = new Vector3D(0, 0, 0);
		}
		else
		{
			// take the picture from the normal camera position...
			cameraPosition = new Vector3D(0, 0, 0);
			// ... and with the camera moving
			cameraBeta = beta;
		}

		// camera
		
		RelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		camera.setShutterModel(new AperturePlaneShutterModel(shutterOpeningTime));
		camera.setSpaceTimeTransformation(spaceTimeTransformationType, cameraBeta);
		
//		double antiAliasingFactor = renderQuality.getAntiAliasingQuality().getAntiAliasingFactor();
//
//		int detectorPixelsHorizontal = (int)(cameraPixelsX*antiAliasingFactor);
//		int detectorPixelsVertical = (int)(cameraPixelsY*antiAliasingFactor);
//		
//		double apertureRadius = cameraApertureSize.getApertureRadius();
//		int raysPerPixel =
//				// the blur quality only matters if the aperture size is not "Pinhole"
//				(cameraApertureSize == ApertureSizeType.PINHOLE)?1:renderQuality.getBlurQuality().getRaysPerPixel();
//				// CORRECTION: this is no longer true, as the blur quality also matters when simulating artefacts that lead to
//				// blurring, such as pixellation effects of GCLAs
//				// blurQuality.getRaysPerPixel();		
//
//		RelativisticAnyFocusSurfaceCamera camera = new RelativisticAnyFocusSurfaceCamera(
//	    		"Camera",	// description,
//	            new Vector3D(0, 0, 0),	// apertureCentre,
//	            new Vector3D(0, 0, 10),	// centreOfView,
//	            EditableRelativisticAnyFocusSurfaceCamera.calculateHorizontalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg),	// horizontalSpanVector
//	            EditableRelativisticAnyFocusSurfaceCamera.calculateVerticalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg, cameraPixelsX, cameraPixelsY),	// verticalSpanVector
//				spaceTimeTransformationType,
//	            beta,
//	            detectorPixelsHorizontal, detectorPixelsVertical, 
//	            ExposureCompensationType.EC0,	// exposureCompensation,
//	            cameraMaxTraceLevel,	// maxTraceLevel,
//	            new Plane(
//	            		"Focus plane",	// description
//	            		new Vector3D(0, 0, cameraFocussingDistance),	// pointOnPlane 
//	            		new Vector3D(0, 0, 1),	// normal
//	            		null,	// surfaceProperty
//	            		scene, 
//	            		studio
//	            	),	// focusScene,
//	            new SceneObjectContainer(
//	            		"Camera-frame scene",	// description
//	            		scene,	// parent, 
//	            		studio
//	            	),	// cameraFrameScene,
//	            new AperturePlaneShutterModel(shutterOpeningTime),	// shutterModel,
//	            // double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
//	            apertureRadius,
//				false,	// diffractiveAperture,
//				550e-9,	// lambda,
//	            raysPerPixel
//	    	);
		
		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);		
	}

	@Override
	public Vector3D getStandardCameraPosition()
	{
		return cameraPosition;
	}

	
	public SceneObject createCuboid(Vector3D centre, SceneObject parent, Studio studio)
	{
		SceneObjectIntersectionSimple s = new SceneObjectIntersectionSimple("cuboid", parent, studio);
		s.addSceneObject(
				new Plane(
						"plane 1",	// description,
						Vector3D.sum(centre, new Vector3D(0.5, 0, 0)),	// pointOnPlane
						new Vector3D(1, 0, 0),	// normal
						SurfaceColour.RED_SHINY,
						s,
						studio
					)
				);
		s.addSceneObject(
				new Plane(
						"plane 2",	// description,
						Vector3D.sum(centre, new Vector3D(-0.5, 0, 0)),	// pointOnPlane
						new Vector3D(-1, 0, 0),	// normal
						SurfaceColour.RED_SHINY,
						s,
						studio
					)
				);
		s.addSceneObject(
				new Plane(
						"plane 3",	// description,
						Vector3D.sum(centre, new Vector3D(0, 0.5, 0)),	// pointOnPlane
						new Vector3D(0, 1, 0),	// normal
						SurfaceColour.GREEN_SHINY,
						s,
						studio
					)
				);
		s.addSceneObject(
				new Plane(
						"plane 4",	// description,
						Vector3D.sum(centre, new Vector3D(0, -0.5, 0)),	// pointOnPlane
						new Vector3D(0, -1, 0),	// normal
						SurfaceColour.GREEN_SHINY,
						s,
						studio
					)
				);
		s.addSceneObject(
				new Plane(
						"plane 5",	// description,
						Vector3D.sum(centre, new Vector3D(0, 0, 0.5)),	// pointOnPlane
						new Vector3D(0, 0, 1),	// normal
						SurfaceColour.BLUE_SHINY,
						s,
						studio
					)
				);
		s.addSceneObject(
				new Plane(
						"plane 6",	// description,
						Vector3D.sum(centre, new Vector3D(0, 0, -0.5)),	// pointOnPlane
						new Vector3D(0, 0, -1),	// normal
						SurfaceColour.BLUE_SHINY,
						s,
						studio
					)
				);

		return s;
	}
	
	
	// GUI stuff
	
	private LabelledVector3DPanel betaPanel, pinholePositionPanel;
	private LabelledDoublePanel shutterOpeningTimePanel, lookalikeSphereRadiusPanel, lookalikeSphereTransmissionCoefficientPanel, cameraHorizontalFOVDegPanel;
	private JCheckBox simulateAsEllipsoidConstructionCheckBox;
	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		betaPanel = new LabelledVector3DPanel("Beta");
		betaPanel.setVector3D(beta);
		interactiveControlPanel.add(betaPanel, "wrap");
		
		shutterOpeningTimePanel = new LabelledDoublePanel("Shutter-opening time");
		shutterOpeningTimePanel.setNumber(shutterOpeningTime);
		interactiveControlPanel.add(shutterOpeningTimePanel, "wrap");
		
		simulateAsEllipsoidConstructionCheckBox = new JCheckBox("Simulate using distorted-lookalike-sphere construction");
		simulateAsEllipsoidConstructionCheckBox.setSelected(simulateAsEllipsoidConstruction);
		interactiveControlPanel.add(simulateAsEllipsoidConstructionCheckBox, "span");
		
		pinholePositionPanel = new LabelledVector3DPanel("Pinhole position at shutter-opening time");
		pinholePositionPanel.setVector3D(new Vector3D(0, 0, 0));
		pinholePositionPanel.setEnabled(false);
		interactiveControlPanel.add(pinholePositionPanel, "wrap");

		lookalikeSphereRadiusPanel = new LabelledDoublePanel(
				"Radius of lookalike sphere"
				// "Principal radius of ellipsoid in beta direction"
			);
		lookalikeSphereRadiusPanel.setNumber(lookalikeSphereRadius);
		interactiveControlPanel.add(lookalikeSphereRadiusPanel, "span");
		
		lookalikeSphereTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of lookalike sphere");
		lookalikeSphereTransmissionCoefficientPanel.setNumber(lookalikeSphereTransmissionCoefficient);
		interactiveControlPanel.add(lookalikeSphereTransmissionCoefficientPanel, "span");
		
		cameraHorizontalFOVDegPanel = new LabelledDoublePanel("Camera horizontal FOV (°)");
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		interactiveControlPanel.add(cameraHorizontalFOVDegPanel, "wrap");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		beta = betaPanel.getVector3D();
		shutterOpeningTime = shutterOpeningTimePanel.getNumber();
		simulateAsEllipsoidConstruction = simulateAsEllipsoidConstructionCheckBox.isSelected();
		lookalikeSphereRadius = lookalikeSphereRadiusPanel.getNumber();
		lookalikeSphereTransmissionCoefficient = lookalikeSphereTransmissionCoefficientPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
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
		(new TerrellRotationExplorer()).run();
	}
}
