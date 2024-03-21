package optics.raytrace.research.relativisticDistortion;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import math.*;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.EditableSurfaceTiling;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.DistortedLookalikeSphere;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.ScaledParametrisedSphere;
import optics.raytrace.sceneObjects.TimHead;
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
    
    private enum ObjectsType
    {
    	SPHERES("spheres"),
    	CUBES("cubes"),
    	TIMS("Tims");
    	private String description;
    	private ObjectsType(String description) {this.description = description;}
    	@Override
    	public String toString() {return description;}
    }
    private ObjectsType objectsType;
    
    private enum ArrangementType
    {
    	CENTRED_ON_XY_LINE("centred on line (x,y)"),
    	CENTRED_ON_YZ_LINE("centred on line (z,y)");
    	private String description;
    	private ArrangementType(String description) {this.description = description;}
    	@Override
    	public String toString() {return description;}
    }
    private ArrangementType arrangementType;
    
    private int objectsNumber;
    private double objectsX, objectsY, objectsZ, objectsSeparation;

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
	    
	    objectsType = ObjectsType.CUBES;
	    arrangementType = ArrangementType.CENTRED_ON_XY_LINE;
	    objectsNumber = 5;
	    objectsX = 2;
	    objectsY = 1.5;
	    objectsZ = 10;
	    objectsSeparation = 2.0;

	    
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
		printStream.println("spaceTimeTransformationType = "+spaceTimeTransformationType);
		printStream.println("beta = "+beta);
		printStream.println("shutterOpeningTime = "+shutterOpeningTime);
		printStream.println("simulateAsEllipsoidConstruction = "+simulateAsEllipsoidConstruction);
		printStream.println("lookalikeSphereRadius = "+lookalikeSphereRadius);
		printStream.println("lookalikeSphereTransmissionCoefficient = "+lookalikeSphereTransmissionCoefficient);
		printStream.println("objectsType = "+objectsType);
		printStream.println("arrangementType = "+arrangementType);
		printStream.println("objectsNumber = "+objectsNumber);
		printStream.println("objectsX = "+objectsX);
		printStream.println("objectsY = "+objectsY);
		printStream.println("objectsZ = "+objectsZ);
		printStream.println("objectsSeparation = "+objectsSeparation);

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
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-2.5, scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects
		// scene.addSceneObject(new Sphere("The sphere", new Vector3D(0,0,0), 1, SurfaceColour.CYAN_SHINY, scene, studio));
		for(int i=0; i<objectsNumber; i++)
		{
			switch(arrangementType)
			{
			case CENTRED_ON_XY_LINE:
				scene.addSceneObject(createObject(new Vector3D(objectsX, objectsY, objectsSeparation*(i-0.5*(objectsNumber-1))), scene, studio));
				break;
			case CENTRED_ON_YZ_LINE:
			default:
				scene.addSceneObject(createObject(new Vector3D(objectsSeparation*(i-0.5*(objectsNumber-1)), objectsY, objectsZ), scene, studio));
			}
		}
		
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
		double transformedShutterOpeningTime = stt.getTransformedTime(new Vector3D(0, 0, 0), shutterOpeningTime);
		pinholePositionPanel.setVector3D(transformedPinholePosition);
		transformedShutterOpeningTimePanel.setNumber(transformedShutterOpeningTime);

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
				
		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);		
	}

	@Override
	public Vector3D getStandardCameraPosition()
	{
		return cameraPosition;
	}

	public SceneObject createObject(Vector3D centre, SceneObject parent, Studio studio)
	{
		switch(objectsType)
		{
		case TIMS:
			return new TimHead("Tim, centred at "+centre, centre, parent, studio);
		case SPHERES:
			return createSphere(centre, parent, studio);
		case CUBES:
		default:
			return createCube(centre, parent, studio);
		}
	}
	
	public SceneObject createSphere(Vector3D centre, SceneObject parent, Studio studio)
	{
		return new ScaledParametrisedSphere(
				"Sphere centred at "+centre,	// description,
				centre,
				1,	// radius,
				Vector3D.Y,	// pole,
				Vector3D.Z,	// phi0Direction,
				0,	// sThetaMin, 
				1,	// sThetaMax,
				0,	// sPhiMin, 
				1,	// sPhiMax,
				new EditableSurfaceTiling(SurfaceColour.BLACK_SHINY, SurfaceColour.WHITE_SHINY, 0.125, 0.0625, studio.getScene()),	// surfaceProperty,
				parent, 
				studio
		);
	}
	
	public SceneObject createCube(Vector3D centre, SceneObject parent, Studio studio)
	{
		SceneObjectIntersectionSimple s = new SceneObjectIntersectionSimple("Cube centred at "+centre, parent, studio);
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
	private LabelledDoublePanel shutterOpeningTimePanel, lookalikeSphereRadiusPanel, lookalikeSphereTransmissionCoefficientPanel, 
		cameraHorizontalFOVDegPanel, transformedShutterOpeningTimePanel;
	private JCheckBox simulateAsEllipsoidConstructionCheckBox;
	private JComboBox<ObjectsType> objectsTypeComboBox;
	private JComboBox<ArrangementType> arrangementTypeComboBox;
	private IntPanel objectsNumberPanel;
	private DoublePanel objectsSeparationPanel, objectsXorZPanel, objectsYPanel;
	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		interactiveControlPanel.add(new JLabel("<html>The pinhole camera moves with velocity <b>&beta;</b> <i>c</i> in the scene frame, where</html>"),  "wrap");
		betaPanel = new LabelledVector3DPanel("<html><b>&beta;</b> = </html>");
		betaPanel.setVector3D(beta);
		interactiveControlPanel.add(betaPanel, "wrap");
		interactiveControlPanel.add(new JLabel("<html>and <i>c</i> is the speed of light, here taken to be <i>c</i> = 1 (in dimensionless units).</html>"),  "wrap");


		interactiveControlPanel.add(new JLabel("<html>At <i>t</i> = 0, the pinhole is at the origin.</html>"),  "wrap");
		
		JPanel scenePanel = new JPanel();
		scenePanel.setLayout(new MigLayout("insets 0"));
		scenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Scene"));
		interactiveControlPanel.add(scenePanel, "wrap");

		scenePanel.add(new JLabel("<html>In addition to the floor in the plane <i>y</i> = -1, the scene comprises a line of</html>"),  "wrap");
		
		objectsNumberPanel = new IntPanel();
		objectsNumberPanel.setNumber(objectsNumber);

		objectsTypeComboBox = new JComboBox<ObjectsType>(ObjectsType.values());
		objectsTypeComboBox.setSelectedItem(objectsType);
		objectsSeparationPanel = new DoublePanel();
		objectsSeparationPanel.setNumber(objectsSeparation);
		scenePanel.add(GUIBitsAndBobs.makeRow(objectsNumberPanel, objectsTypeComboBox, ", their centres separated by a distance", objectsSeparationPanel, ","), "wrap");

		arrangementTypeComboBox = new JComboBox<ArrangementType>(ArrangementType.values());
		arrangementTypeComboBox.setSelectedItem(arrangementType);
		arrangementTypeComboBox.addActionListener(this);
		objectsXorZPanel = new DoublePanel();
		objectsXorZPanel.setNumber(getObjectsXorZ());
		objectsYPanel = new DoublePanel();
		objectsYPanel.setNumber(objectsY);
		scenePanel.add(GUIBitsAndBobs.makeRow(arrangementTypeComboBox, " = (", objectsXorZPanel, ",", objectsYPanel, ")"), "wrap");
		
		// private DoublePanel objectsSeparationPanel;

		shutterOpeningTimePanel = new LabelledDoublePanel("Shutter-opening time in camera frame");
		shutterOpeningTimePanel.setNumber(shutterOpeningTime);
		interactiveControlPanel.add(shutterOpeningTimePanel, "wrap");
		
		JPanel sceneFramePanel = new JPanel();
		sceneFramePanel.setLayout(new MigLayout("insets 0"));
		sceneFramePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Scene-frame coordinates of shutter-opening event"));
		interactiveControlPanel.add(sceneFramePanel, "wrap");

		transformedShutterOpeningTimePanel = new LabelledDoublePanel("Shutter-opening time");
		transformedShutterOpeningTimePanel.getDoublePanel().setText("---");
		transformedShutterOpeningTimePanel.setEnabled(false);
		sceneFramePanel.add(transformedShutterOpeningTimePanel, "wrap");

		pinholePositionPanel = new LabelledVector3DPanel("Pinhole position at shutter-opening time");
		pinholePositionPanel.setVector3D(new Vector3D(0, 0, 0));
		pinholePositionPanel.setEnabled(false);
		sceneFramePanel.add(pinholePositionPanel, "wrap");
		
		JPanel lookalikeSpherePanel = new JPanel();
		lookalikeSpherePanel.setLayout(new MigLayout("insets 0"));
		lookalikeSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Distorted-lookalike-sphere construction"));
		interactiveControlPanel.add(lookalikeSpherePanel, "wrap");

		simulateAsEllipsoidConstructionCheckBox = new JCheckBox("Simulate using distorted-lookalike-sphere construction");
		simulateAsEllipsoidConstructionCheckBox.setSelected(simulateAsEllipsoidConstruction);
		lookalikeSpherePanel.add(simulateAsEllipsoidConstructionCheckBox, "span");
		
		lookalikeSphereRadiusPanel = new LabelledDoublePanel(
				"Radius of lookalike sphere"
				// "Principal radius of ellipsoid in beta direction"
			);
		lookalikeSphereRadiusPanel.setNumber(lookalikeSphereRadius);
		lookalikeSpherePanel.add(lookalikeSphereRadiusPanel, "span");
		
		lookalikeSphereTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of lookalike sphere");
		lookalikeSphereTransmissionCoefficientPanel.setNumber(lookalikeSphereTransmissionCoefficient);
		lookalikeSpherePanel.add(lookalikeSphereTransmissionCoefficientPanel, "span");
		
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
		
		objectsNumber = objectsNumberPanel.getNumber();
		objectsType = (ObjectsType)(objectsTypeComboBox.getSelectedItem());
		arrangementType = (ArrangementType)(arrangementTypeComboBox.getSelectedItem());
		objectsSeparation = objectsSeparationPanel.getNumber();
		objectsY = objectsYPanel.getNumber();
		setObjectsXorZ(objectsXorZPanel.getNumber());
	}

	public double getObjectsXorZ()
	{
		switch(arrangementType)
		{
		case CENTRED_ON_XY_LINE:
			return objectsX;
		case CENTRED_ON_YZ_LINE:
		default:
			return objectsZ;
		}
	}
	
	public void setObjectsXorZ(double objectsXorZ)
	{
		switch(arrangementType)
		{
		case CENTRED_ON_XY_LINE:
			objectsX = objectsXorZ;
			break;
		case CENTRED_ON_YZ_LINE:
		default:
			objectsZ = objectsXorZ;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(arrangementTypeComboBox))
		{
			setObjectsXorZ(objectsXorZPanel.getNumber());
			arrangementType = (ArrangementType)(arrangementTypeComboBox.getSelectedItem());
			objectsXorZPanel.setNumber(getObjectsXorZ());
		}
		else super.actionPerformed(e);
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
