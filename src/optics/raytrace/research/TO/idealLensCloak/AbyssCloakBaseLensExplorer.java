package optics.raytrace.research.TO.idealLensCloak;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import math.simplicialComplex.Simplex;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * A single abyss cloak used to demonstrate the significance of the base lens, 
 * that is, an object within the cloak, irrespective of image position, can only be seen when in a line of sight with the base lens.
 * 
 * @author Maik
 */
public class AbyssCloakBaseLensExplorer extends NonInteractiveTIMEngine
{
	
	//defining the cloak parameters
	private Vector3D baseCentre;
	private boolean show;
	private boolean showCylinders;
	private LensType lensType;
	private double height;
	private double baseRadius;
	private double baseLensF;
	
	/**
	 * The patterned sphere is a test object placed in the cloak and imaged to the outside
	 */
	private boolean showPatternedSphere;
	
	/**
	 * Position of the centre of the patterned sphere.
	 */
	private Vector3D patternedSphereCentre;

	/**
	 * Radius of the patterned sphere
	 */
	private double patternedSphereRadius;
	
	private boolean showImageOfPatternedSphere, makePatternedSpheresShadowThrowing;
	/*
	 * the scene
	 */
	private StudioInitialisationType studioInitialisation;
	
	/*
	 * camera stuff
	 */
	private double 	cameraUpAngle, cameraAngle;
	private double cameraFOV;
	//for the movie
	private double startAngleCamera, stopAngleCamera, startUpAngleCamera, stopUpAngleCamera;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public AbyssCloakBaseLensExplorer()
	{
		super();
		baseCentre = new Vector3D(0,0,0);
		show=true;
		showCylinders = true;
		lensType = LensType.NONE;
		height = 2;
		baseRadius = 1;
		baseLensF = 0.5;
		
		/**
		 * The patterned sphere is a test object placed in the cloak and imaged to the outside
		 */
		showPatternedSphere = true;
		
		/**
		 * Position of the centre of the patterned sphere.
		 */
		patternedSphereCentre = new Vector3D(0,height*0.1,0);

		/**
		 * Radius of the patterned sphere
		 */
		patternedSphereRadius = 0.05;
		
		showImageOfPatternedSphere = true;
		makePatternedSpheresShadowThrowing = false;
		//the scene
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop
		//camera
		movie = false;
		cameraDistance = 10;
		numberOfFrames = 10;
		cameraFOV = 20;
		cameraUpAngle = 0;
		cameraAngle = 0;
		//for the movie
		startAngleCamera = 0;
		stopAngleCamera = 0;
		startUpAngleCamera = 0;
		stopUpAngleCamera = 0;
		
		
		//general settings 
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		windowTitle = "Dr TIM's Abyss-cloak base lens explorer";
		windowWidth = 1400;
		windowHeight = 650;
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"AbyssCloakBaseLensExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Cloak Params");
		printStream.println("base focal= "+baseLensF);
		printStream.println("base centre= "+baseCentre);
		printStream.println("base radius= "+baseRadius);
		printStream.println("cloak height= "+height);
		printStream.println("lens type= "+lensType);
		if(show)printStream.println("Cloak is shown");
		if(showCylinders)printStream.println("Cloak frame is shown");
		printStream.println("Pattern sphere radii="+patternedSphereRadius);
		printStream.println("Images");
		if(showImageOfPatternedSphere)printStream.println("Pattern sphere image is shown");
		printStream.println("PatternSphereImagePos="+patternedSphereCentre);
		printStream.println("Camera");
		printStream.println("cameraAngle="+cameraAngle);
		printStream.println("cameraUpAngle="+cameraUpAngle);
		printStream.println("cameraFOV="+cameraFOV);
		printStream.println("cameraDistance="+cameraDistance);

		if(movie) {
			printStream.println("Camera");
			printStream.println("numberOfFrames="+numberOfFrames);
			printStream.println("startAngle="+startAngleCamera);
			printStream.println("stopAngle="+stopAngleCamera);
			printStream.println("startUpAngle="+startUpAngleCamera);
			printStream.println("stopUpAngle="+stopUpAngleCamera);
		}
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	//create the cloak
	EditableLensSimplicialComplex abyssCloak;
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		//create the simplicial complex...
		abyssCloak = new EditableLensSimplicialComplex(
				"Abyss cloak",	// description
				scene,	// parent
				studio
			); 
		
		// ... and initialise it as an ideal-lens cloak
		double vertexRadius = 0.01;
		abyssCloak.setLensTypeRepresentingFace(lensType);
		abyssCloak.setShowStructureP(showCylinders);
		abyssCloak.setVertexRadiusP(vertexRadius);
		abyssCloak.setShowStructureV(false);
		abyssCloak.setVertexRadiusV(vertexRadius);
		abyssCloak.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false));
		
		double h1P = 1./3.;
		abyssCloak.initialiseToOmnidirectionalLens(
				h1P,	// physicalSpaceFractionalLowerInnerVertexHeight
				2./3.,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-height/baseLensF + 1/h1P),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, height, 0),	// topVertex
				baseCentre,	// baseCentre
				new Vector3D(baseRadius, 0, 0)	// baseVertex1
			);
		
		scene.addSceneObject(abyssCloak, show);
		
		// add the patterned sphere image outside the cloak...
				EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
						"image of Patterned sphere", // description
						patternedSphereCentre, // centre
						patternedSphereRadius,	// radius
						new Vector3D(1, 1, 1),	// pole
						new Vector3D(1, 0, 0),	// phi0Direction
						0, Math.PI,	// sThetaMin, sThetaMax
						-Math.PI, Math.PI,	// sPhiMin, sPhiMax
						new SurfaceTiling(
//								new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
//								new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
								new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
								new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
								2*Math.PI/6,
								2*Math.PI/6
							),
						scene, studio);
				scene.addSceneObject(patternedSphere, showImageOfPatternedSphere);
				
				// ... and the real positions of its image due to the inner cloak...
				Vector3D p1 = new Vector3D(0,0,0);
				//turn the cloak into a number simplicies and find the correct simplex to image into.
				boolean realImages = false;
				//check if the imaging is from inside out or the other way around. 
				double checkIfInside = 0;
				for(int i=0; i<abyssCloak.getLensSimplicialComplex().getSimplices().size(); i++) {
					Simplex simplex = abyssCloak.getLensSimplicialComplex().getSimplices().get(i);
					if(simplex.pointIsInsideSimplex(patternedSphereCentre)) checkIfInside = checkIfInside+1;
				}
				boolean insideOut = false;
				if(checkIfInside>0) insideOut =true;
				//System.out.println(insideOut);
				for(int i=0; i<abyssCloak.getLensSimplicialComplex().getSimplices().size(); i++) {
					Simplex simplex = abyssCloak.getLensSimplicialComplex().getSimplices().get(i);
					if(insideOut) {
						p1 = abyssCloak.getLensSimplicialComplex().mapToOutside(i, patternedSphereCentre);//.mapFromOutside();
						realImages = true;
					}else {
						p1 = abyssCloak.getLensSimplicialComplex().mapFromOutside(i, patternedSphereCentre);//.mapFromOutside();
						if(simplex.pointIsInsideSimplex(p1) && simplex.pointIsInsideSimplex(p1)) {
							realImages = true;
							//System.out.println("real image in simplex "+i);
							break;
						}else {
							realImages = false;
						}
					}
				}
				if(realImages) {
				EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
						"Image of patterned sphere due to cloak", // description
						p1, // centre
						patternedSphereRadius,	// radius
						new Vector3D(1, 1, 1),	// pole
						new Vector3D(1, 0, 0),	// phi0Direction
						0, Math.PI,	// sThetaMin, sThetaMax
						-Math.PI, Math.PI,	// sPhiMin, sPhiMax
						new SurfaceTiling(
								new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
								new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
								2*Math.PI/6,
								2*Math.PI/6
							),
						scene, studio);
				scene.addSceneObject(patternedSphere1, showPatternedSphere);
				//System.out.println("image/postion of patter sphere is at "+p1);
				}else {System.out.println("No real image/object position for the given pattern sphere");}
				
				
				//if we want a movie
				if(movie) {
					//System.out.println("movie making in progress...");
					cameraAngle = startAngleCamera+(stopAngleCamera-startAngleCamera)*frame/numberOfFrames;
					cameraUpAngle = startUpAngleCamera+(stopUpAngleCamera-startUpAngleCamera)*frame/numberOfFrames;
					//System.out.println(frame+","+numberOfFrames);
				}
				/**
				 * setting up a camera
				 */

				int
				quality = 2,	// 1 = normal, 2 = good, 4 = great
				pixelsX = 640*quality,
				pixelsY = 480*quality;

				Vector3D cameraDirection = new Vector3D(-Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), -Math.sin(Math.toRadians(cameraUpAngle)), Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle))).getNormalised();
				Vector3D cameraApertureCentre	= Vector3D.sum(new Vector3D(0, 1, 0), cameraDirection.getProductWith(-cameraDistance));//new Vector3D(cameraDistance*Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), 1+cameraDistance*Math.sin(Math.toRadians(cameraUpAngle)), -cameraDistance*Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));

				EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
						"camera",
						cameraApertureCentre,	// centre of aperture
						cameraDirection,	// viewDirection
						new Vector3D(0, 1, 0),	// top direction vector
						cameraFOV,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
						new Vector3D(0, 0, 0),	// beta
						pixelsX, pixelsY,	// logical number of pixels
						ExposureCompensationType.EC0,	// exposure compensation +0
						50,	// maxTraceLevel
						new Plane(
								"focus plane",	// description
								Vector3D.sum(cameraApertureCentre, cameraDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
								cameraDirection,	// normal
								null,	// surfaceProperty
								null,	// parent
								null	// studio
								),	// focus scene
						null,	// cameraFrameScene,
						ApertureSizeType.PINHOLE,	// aperture size
						renderQuality.getBlurQuality(),	// blur quality
						renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
						);
				//System.out.println("centre = "+centre);
				studio.setCamera(camera);

				studio.setScene(scene);

				studio.setLights(LightSource.getStandardLightsFromBehind());
				
	}

	
	// the cloaks
	private JCheckBox showCheckBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JCheckBox showCylindersCheckBox;
	//private LabelledVector3DPanel baseCentrePanel;
	private LabelledDoublePanel heightPanel;
	private LabelledDoublePanel baseLensFPanel, baseRadiusPanel;

	//the scene and objects
	private JCheckBox showPatternedSphereCheckBox, showImageOfPatternedSphereCheckBox;
	private LabelledVector3DPanel patternedSphereCentrePanel;
	private LabelledDoublePanel patternedSphereRadiusPanel;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	
	//camera and movie stuff
	private JCheckBox movieCheckBox;
	private DoublePanel startAngleCameraPanel,stopAngleCameraPanel,startUpAngleCameraPanel, stopUpAngleCameraPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;
	private LabelledDoublePanel cameraDistancePanel,cameraFOVPanel,cameraAnglePanel, cameraUpAnglePanel;
	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");


		//general panel

		JPanel generalpanel = new JPanel();
		generalpanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("General", generalpanel);


		// cloak
		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 1"));
		cloaksPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak"));
		generalpanel.add(cloaksPanel, "wrap");		
	
		showCheckBox = new JCheckBox("Show cloak");
		showCheckBox.setSelected(show);
		cloaksPanel.add(showCheckBox);
		
		showCylindersCheckBox = new JCheckBox("Show cylinder frame");
		showCylindersCheckBox.setSelected(showCylinders);
		cloaksPanel.add(showCylindersCheckBox, "span");
		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		cloaksPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeComboBox), "span");
		
		baseLensFPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFPanel.setNumber(baseLensF);
		cloaksPanel.add(baseLensFPanel, "span");
		
		heightPanel = new LabelledDoublePanel("Height");
		heightPanel.setNumber(height);
		cloaksPanel.add(heightPanel, "span");

		baseRadiusPanel = new LabelledDoublePanel("Base radius");
		baseRadiusPanel.setNumber(baseRadius);
		cloaksPanel.add(baseRadiusPanel, "span");


		//raytrace stuff

		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		scenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Scene"));
		generalpanel.add(scenePanel, "wrap");
		
		//the scene and objects

		showPatternedSphereCheckBox = new JCheckBox("Show the pattern-sphere...");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		scenePanel.add(showPatternedSphereCheckBox, "span");		

		patternedSphereCentrePanel = new LabelledVector3DPanel("... located at");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		scenePanel.add(patternedSphereCentrePanel, "span");
		
		patternedSphereRadiusPanel = new LabelledDoublePanel("Sphere radius");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		scenePanel.add(patternedSphereRadiusPanel, "span");
		
		showImageOfPatternedSphereCheckBox = new JCheckBox("Show image of pattern-sphere");
		showImageOfPatternedSphereCheckBox.setSelected(showImageOfPatternedSphere);
		scenePanel.add(showImageOfPatternedSphereCheckBox, "span");
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.values());
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		scenePanel.add(GUIBitsAndBobs.makeRow("Initialisation type", studioInitialisationComboBox), "span");


		//camera stuff
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		//moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie Time"));
		tabbedPane.add(cameraPanel, "Camera & Movies");
		
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraFOVPanel = new LabelledDoublePanel("Camera FOV");
		cameraFOVPanel.setNumber(cameraFOV);
		cameraPanel.add(cameraFOVPanel, "span");
		
		cameraAnglePanel = new LabelledDoublePanel("XZ angle(horizontal angle)");
		cameraAnglePanel.setNumber(cameraAngle);
		cameraPanel.add(cameraAnglePanel);
		
		cameraUpAnglePanel = new LabelledDoublePanel("and XY angle(vertical angle)");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		cameraPanel.add(cameraUpAnglePanel,"span");
		
		
		movieCheckBox = new JCheckBox("Select to make a movie");
		movieCheckBox.setSelected(movie);
		cameraPanel.add(movieCheckBox, "span");
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);

		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);

		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		cameraPanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");		
		
		
		startAngleCameraPanel = new DoublePanel();
		startAngleCameraPanel.setNumber(startAngleCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("XZ angle starting at", startAngleCameraPanel, "°, "));
		
		stopAngleCameraPanel = new DoublePanel();
		stopAngleCameraPanel.setNumber(stopAngleCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("and stoping at",stopAngleCameraPanel, "°"),"span");
		
		startUpAngleCameraPanel = new DoublePanel();
		startUpAngleCameraPanel.setNumber(startUpAngleCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("XY angle starting at ", startUpAngleCameraPanel, "°, "));
		
		stopUpAngleCameraPanel = new DoublePanel();
		stopUpAngleCameraPanel.setNumber(stopUpAngleCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("and stoping at",stopUpAngleCameraPanel, "°"),"span");	
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{		super.acceptValuesInInteractiveControlPanel();
	
	// the cloaks
	show = showCheckBox.isSelected();
	lensType = (LensType)(lensTypeComboBox.getSelectedItem());
	showCylinders = showCylindersCheckBox.isSelected();
	height = heightPanel.getNumber();
	baseLensF = baseLensFPanel.getNumber();
	baseRadius = baseRadiusPanel.getNumber();

	//the scene and objects
	showPatternedSphere = showPatternedSphereCheckBox.isSelected();
	showImageOfPatternedSphere = showImageOfPatternedSphereCheckBox.isSelected();
	patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
	patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
	studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());	
	
	//camera and movie stuff
	movie = movieCheckBox.isSelected();
	startAngleCamera = startAngleCameraPanel.getNumber();
	stopAngleCamera = stopAngleCameraPanel.getNumber();
	startUpAngleCamera = startUpAngleCameraPanel.getNumber();
	stopUpAngleCamera = stopUpAngleCameraPanel.getNumber();
	numberOfFrames = numberOfFramesPanel.getNumber();
	firstFrame = firstFramePanel.getNumber();
	lastFrame = lastFramePanel.getNumber();
	cameraDistance = cameraDistancePanel.getNumber();
	cameraFOV = cameraFOVPanel.getNumber();
	cameraAngle = cameraAnglePanel.getNumber();
	cameraUpAngle = cameraUpAnglePanel.getNumber();	
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
		(new AbyssCloakBaseLensExplorer()).run();
	}
}
