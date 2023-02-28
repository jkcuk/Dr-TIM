package optics.raytrace.research.JakubsCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.MyMath;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeComboBox;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ConicPrism;
import optics.raytrace.sceneObjects.CylinderMantle;
import optics.raytrace.sceneObjects.JakubsPrism;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.PhaseHologramOfSimplePrism;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;

/**
 * A cloak which utilises prisms to steer light around a cloaked area.
 * @author Maik based on Jakub's design
 */
public class JakubsPrismaticCloakExplorer extends NonInteractiveTIMEngine
{

	/**
	 * The centre, which is halfway between the sets of prisms. This is where an object should be cloaked. 
	 */
	private Vector3D centre;

	/**
	 * Normalised vector in the direction of the symmetry axis.
	 * This should point towards the sky for the cloaking to be seen. If it is perpendicular, the cloak can be seen from the side. 
	 */
	private Vector3D symmetryAxis;

	/**
	 * d, the separation between the inner prisms
	 */
	private double d;

	/**
	 * w, the interior angle of the prisms
	 */
	private double w;

	/**
	 * h, the cross sectional height of the prisms
	 */
	private double h;	
	
	/**
	 * the refractive index of the material
	 */
	private double n;
	
	/**
	 * Transmission coefficient of each prism
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the system should  throw shadows
	 */
	private boolean shadowThrowing;
	
	public enum CloakingStrat
	{
		CONNIC("Connic prism"),
		JAKUB("Jakubs prism"),
		PHASE_HOLOGRAM("Phase hologram"),
		NONE("No cloak");

		private String description;
		private CloakingStrat(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private CloakingStrat cloakingStrat;
	
	
	//
	// The scene and object parameters 
	//
	
	
	/**
	 * add a ball or tim to the centre of the scene
	 */
	public enum ViewObject{
		TIM("Tim"),
		BALL("ball"),
		EMPTY("No element");
		
		private String description;
		private ViewObject(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	private ViewObject viewObject;
	
	/**
	 * radius of tims head or the sphere
	 */
	private double radius; 
	
	/**
	 * magnitude of the of phase holograms
	 */
	private double magnitudeInner, magnitudeOuter;
	
	/**
	 * Should the phase hologram be cropped?
	 */
	private boolean cropHologram;
	
	/**
	 * show a background lattice
	 */
	private boolean toggleBackgroundLattice;
	
	/**
	 * some raytracing stuff with a point and click function
	 */
	private boolean traceRay;
	
	private Vector3D rayTraceStartingPosition, rayTracePosition;
	
	
	public JakubsPrismaticCloakExplorer()
	{
		super();
		centre = new Vector3D(0,0,0);
		symmetryAxis= Vector3D.Y;
		d = 5;
		w = MyMath.deg2rad(60);
		h = 1;	
		n = 1.515;
		surfaceTransmissionCoefficient = 0.9;
		shadowThrowing = false;
		cloakingStrat = CloakingStrat.JAKUB;
		magnitudeInner = -0.5;
		magnitudeOuter = 0.5;
		cropHologram = false;


		//object stuff
		viewObject = ViewObject.BALL;
		radius = 0.7; 
		toggleBackgroundLattice = false;
		
		//ray trace
		traceRay = false;
		rayTraceStartingPosition = Vector3D.Z.getProductWith(9);
		rayTracePosition = Vector3D.O;
		traceRaysWithTrajectory = false;

		
		//camera stuff
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraFocussingDistance = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraHorizontalFOVDeg = 40;
		cameraDistance = 10;
		

		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Jakubs Prismatic Cloak";
			windowWidth = 1400;
			windowHeight = 650;
		}

	}
	
	@Override
	public String getClassName()
	{
		return "JakubsPrismaticCloakExplorer"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		printStream.println("Prism type = "+cloakingStrat);
		printStream.println("centre = "+centre);
		printStream.println("symmetry Axis = "+symmetryAxis);
		printStream.println("d = "+d);
		printStream.println("omega = "+w);
		printStream.println("h = "+h);
		printStream.println("n = "+n);
		if(cloakingStrat == CloakingStrat.PHASE_HOLOGRAM) {
		printStream.println("inner hologram magnitude = "+magnitudeInner);
		printStream.println("outer hologram magnitude = "+magnitudeOuter);
		printStream.println("cropHologram = " + cropHologram);
		}
		printStream.println("surfaceTransmissionCoefficient = "+surfaceTransmissionCoefficient);
		printStream.println("shadowThrowing = "+shadowThrowing);
		
		//raytracing stuff
		printStream.println("trace Ray = "+traceRay);
		printStream.println("ray trace from "+	rayTraceStartingPosition+ ", to "+rayTracePosition);
		
		
		//  objects
		printStream.println("show lattice " + toggleBackgroundLattice);
		printStream.println("View Object " + viewObject);
		printStream.println("radius " + radius);
	
		//camera stuff should all be in the Tim engine
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}


	public void populateStudio()
			throws SceneException{
			
			
		//first add the default scene and a view object as well as a background lattice if needed.
			// the studio
			studio = new Studio();
				
				
			//setting the scene
			SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		
					
			// the standard scene objects
			scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-2, scene, studio)); //the floor
			scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
			
			switch(viewObject) {
			case TIM:
				scene.addSceneObject(new TimHead("Tim head", 
						centre, 
						radius, 
						Vector3D.Z.getProductWith(-1), //front direction
						Vector3D.Y, //top direction
						Vector3D.X, //right direction
						scene, 
						studio)
						);
				break;
				
			case BALL:
				scene.addSceneObject(new EditableScaledParametrisedSphere(
						"Patterned sphere", // description
						centre, // centre
						radius,	// radius
						new Vector3D(0, 1, 0),	// pole
						new Vector3D(1, 0, 0),	// phi0Direction
						0, Math.PI,	// sThetaMin, sThetaMax
						-Math.PI, Math.PI,	// sPhiMin, sPhiMax
						new SurfaceTiling(
								new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE,false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
								new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
								2*Math.PI/6,
								2*Math.PI/6
							),
						scene, 
						studio)
						);

				break;
				
			case EMPTY:
				break;
			}
			
			if(toggleBackgroundLattice) {
				
				scene.addSceneObject(new EditableCylinderLattice(
						"cylinder lattice",	// description
						-20, 20, 41, Vector3D.X,	// xMin, xMax, nX, xVector
						-2, 20, 23, Vector3D.Y,	// yMin, yMax, nY, yVector
						-2, 2, 2, Vector3D.Z,	// zMin, zMax, nZ, zVector
						0.08,	// radius
						new Vector3D (0,0,40), //centre
						scene,	// parent
						studio)
						);
			}
			
			//Adding the actual cloak(s)
			double D = calculateD(d, h, w ,n);
			switch(cloakingStrat){
			case CONNIC:
				//some math to get the correct centres
				Vector3D p1centre =Vector3D.sum(centre, symmetryAxis.getProductWith(-D/2));
				Vector3D p2centre =Vector3D.sum(centre, symmetryAxis.getProductWith(-d/2));
				Vector3D p3centre =Vector3D.sum(centre, symmetryAxis.getProductWith(d/2));
				Vector3D p4centre =Vector3D.sum(centre, symmetryAxis.getProductWith(D/2));
				
				ConicPrism	connicPrism1 = new ConicPrism(
						"prism2", //description,
						p1centre,// centre,
						symmetryAxis,// normalisedOpticalAxisDirection,
						w,// w,
						h,// h,
						n,// n,
						false,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				ConicPrism connicPrism2 = new ConicPrism(
						"prism1", //description,
						p2centre,// centre,
						symmetryAxis,// normalisedOpticalAxisDirection,
						w,// w,
						h,// h,
						n,// n,
						true,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				ConicPrism connicPrism3 = new ConicPrism(
						"prism1", //description,
						p3centre,// centre,
						symmetryAxis.getProductWith(-1),// normalisedOpticalAxisDirection,
						w,// w,
						h,// h,
						n,// n,
						true,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				
				ConicPrism connicPrism4 = new ConicPrism(
						"prism2", //description,
						p4centre,// centre,
						symmetryAxis.getProductWith(-1),// normalisedOpticalAxisDirection,
						w,// w,
						h,// h,
						n,// n,
						false,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				scene.addSceneObject(connicPrism1);
				scene.addSceneObject(connicPrism2);
				scene.addSceneObject(connicPrism3);
				scene.addSceneObject(connicPrism4);
				break;
			case JAKUB:
				//some math to get the correct centres
				Vector3D innerCentre =Vector3D.sum(centre, symmetryAxis.getProductWith(-h));
				
				JakubsPrism jakubPrism1 = new JakubsPrism(
						"prism1",// description,
						innerCentre,// centre,
						symmetryAxis,// symmetryAxis,
						w,// w,
						h,// h,
						n,// n,
						d,
						true,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				JakubsPrism jakubPrism2 = new JakubsPrism(
						"prism2",// description,
						centre,// centre,
						symmetryAxis,// symmetryAxis,
						w,// w,
						h,// h,
						n,// n,
						D,
						false,// invertPrism,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						shadowThrowing,// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				scene.addSceneObject(jakubPrism1);
				scene.addSceneObject(jakubPrism2);
				break;
			case PHASE_HOLOGRAM:
				//add a hologram prism
				CylinderMantle holographicPrism1 = new CylinderMantle(
						"prism1",// description,
						Vector3D.sum(symmetryAxis.getProductWith(-10), centre),// startPoint,
						Vector3D.sum(symmetryAxis.getProductWith(10), centre),// endPoint,
						D,// radius,
				new PhaseHologramOfSimplePrism(
						symmetryAxis,// uDirection,
						magnitudeOuter,// magnitude,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						false,
						shadowThrowing),// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				CylinderMantle holographicPrism2 = new CylinderMantle(
						"prism2",// description,
						Vector3D.sum(symmetryAxis.getProductWith(-10), centre),// startPoint,
						Vector3D.sum(symmetryAxis.getProductWith(10), centre),// endPoint,
						d,// radius,
				new PhaseHologramOfSimplePrism(
						symmetryAxis,/// uDirection,
						magnitudeInner,// magnitude,
						surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
						false,
						shadowThrowing),// shadowThrowing,
						scene,// parent,
						studio// studio
						);
				
				if(cropHologram) {
					ArrayList<SceneObjectPrimitive> prism1	= new ArrayList<SceneObjectPrimitive>();
					ArrayList<SceneObjectPrimitive> planes1	= new ArrayList<SceneObjectPrimitive>();
					ArrayList<SceneObjectPrimitive> prism2	= new ArrayList<SceneObjectPrimitive>();
					ArrayList<SceneObjectPrimitive> planes2	= new ArrayList<SceneObjectPrimitive>();
					ArrayList<SceneObjectPrimitive> empty	= new ArrayList<SceneObjectPrimitive>();
					
					//add the outer hologram first
					prism1.add(holographicPrism1);
					//now add the components which will cut the hologram.
					planes1.add(new Plane("crop plane 1", Vector3D.sum(centre, symmetryAxis.getProductWith(0.5*h)), symmetryAxis.getProductWith(1),null, scene, studio));
					planes1.add(new Plane("crop plane 2", Vector3D.sum(centre, symmetryAxis.getProductWith(-0.5*h)), symmetryAxis.getProductWith(-1),null, scene, studio));
					//System.out.println("pos"+symmetryAxis.getProductWith(0.5*h));
					SceneObjectPrimitiveIntersection holographicPrism1cropped = new SceneObjectPrimitiveIntersection(
							"prism1",// description,
							prism1,
							empty,// negativeSceneObjectPrimitives,
							planes1,// invisiblePositiveSceneObjectPrimitives,
							empty,// invisibleNegativeSceneObjectPrimitives,
							empty,// clippedSceneObjectPrimitives,
							scene,// parent,
							studio// studio
						);
					
					scene.addSceneObject(holographicPrism1cropped);
					
//					//now clear and repeat for the inner hologram...
//					prism.clear();
//					planes.clear();
					
					prism2.add(holographicPrism2);
					//now add the components which will cut the hologram, taking into account the light ray direction change of the outer hologram
					Vector3D i = Vector3D.getANormal(symmetryAxis);
					double shift = magnitudeOuter*(D-d)/( Vector3D.scalarProduct(Vector3D.sum(i, symmetryAxis.getProductWith(magnitudeOuter)).getNormalised(), i));
					planes2.add(new Plane("crop plane 1", Vector3D.sum(centre, symmetryAxis.getProductWith(shift+0.5*h)), symmetryAxis.getProductWith(1),null, scene, studio));
					planes2.add(new Plane("crop plane 2", Vector3D.sum(centre, symmetryAxis.getProductWith(shift-0.5*h)), symmetryAxis.getProductWith(-1),null, scene, studio));
					//System.out.println("shifted pos"+symmetryAxis.getProductWith(shift+0.5*h));
					SceneObjectPrimitiveIntersection holographicPrism2cropped = new SceneObjectPrimitiveIntersection(
							"prism2",// description,
							prism2,
							empty,// negativeSceneObjectPrimitives,
							planes2,// invisiblePositiveSceneObjectPrimitives,
							empty,// invisibleNegativeSceneObjectPrimitives,
							empty,// clippedSceneObjectPrimitives,
							scene,// parent,
							studio// studio
						);
					
					scene.addSceneObject(holographicPrism2cropped);
					
					
				}else {
				scene.addSceneObject(holographicPrism1);
				scene.addSceneObject(holographicPrism2);
				}
				
				break;
			case NONE:
				break;
			}
			
			//adding some ray tracing functionality.
			if (traceRay) {
			scene.addSceneObject(new EditableRayTrajectory(
					"test ray",
					rayTraceStartingPosition,	// start point
					0,	// start time
					Vector3D.difference(rayTracePosition, rayTraceStartingPosition).getNormalised(),	// initial direction
					0.02,	// radius
					SurfaceColour.RED_MATT,
					100,	// max trace level
					false,	// reportToConsole
					scene,
					studio
					)
					);
			
			System.out.println(rayTracePosition);
			
			studio.setScene(scene);
			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();
			
//			scene.removeAllSceneObjectsWithDescription("inner prism", false);
//			scene.removeAllSceneObjectsWithDescription("outer prism", false);
			scene.removeAllSceneObjectsWithDescription("prism1", true);
			scene.removeAllSceneObjectsWithDescription("prism2", true);
			
			
			}
			
			studio.setLights(LightSource.getStandardLightsFromBehind());
			studio.setCamera(getStandardCamera());
			studio.setScene(scene);
		}
		
	//Scene stuff

	private LabelledVector3DPanel centrePanel, normalisedOpticalAxisDirectionPanel;
	private JComboBox<ViewObject> viewObjectComboBox;
	private DoublePanel dPanel, wPanel, hPanel, nPanel, surfaceTransmissionCoefficientPanel, radiusPanel, magnitudeInnerPanel, magnitudeOuterPanel;
	private JCheckBox shadowThrowingCheckBox, toggleBackgroundLatticeCheckBox, cropHologramCheckBox;
	private JComboBox<CloakingStrat> cloakingStratComboBox;

	//camera stuff
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
	private DoublePanel cameraFocussingDistancePanel, cameraHorizontalFOVDegPanel, cameraDistancePanel;
	private ApertureSizeComboBox cameraApertureSizeComboBox;

	//ray trace stuff
	private LabelledVector3DPanel rayTraceStartingPositionPanel, rayAimPanel;
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	private JCheckBox traceRayCheckBox;

	JTabbedPane rayAimOptionPanel;
		
		protected void createInteractiveControlPanel()
		{
			super.createInteractiveControlPanel();
			
			//the panels
			JTabbedPane tabbedPane = new JTabbedPane();
			interactiveControlPanel.add(tabbedPane, "span");
			
			JPanel scenePanel = new JPanel();
			scenePanel.setLayout(new MigLayout("insets 0"));
			tabbedPane.addTab("Scene", scenePanel);
			
			JPanel cameraPanel = new JPanel();
			cameraPanel.setLayout(new MigLayout("insets 0"));
			tabbedPane.addTab("Camera", cameraPanel);
			
			//Scene Stuff
			
			JPanel cloakPanel = new JPanel();
			cloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak core"));
			cloakPanel.setLayout(new MigLayout("insets 0"));
			scenePanel.add(cloakPanel, "grow, wrap");
			
			JPanel objectsPanel = new JPanel();
			objectsPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Objects"));
			objectsPanel.setLayout(new MigLayout("insets 0"));
			scenePanel.add(objectsPanel, "grow, wrap");
			
			cloakingStratComboBox = new JComboBox<CloakingStrat>(CloakingStrat.values());
			cloakingStratComboBox.setSelectedItem(cloakingStrat);
			cloakPanel.add(cloakingStratComboBox,"span");
			
			cropHologramCheckBox = new JCheckBox("crop holograms");
			cropHologramCheckBox.setSelected(cropHologram);
			cloakPanel.add(cropHologramCheckBox, "span");
			
			magnitudeInnerPanel =new DoublePanel();
			magnitudeInnerPanel.setNumber(magnitudeInner);
			cloakPanel.add(GUIBitsAndBobs.makeRow("Hologram inner", magnitudeInnerPanel," and ")); 
			
			magnitudeOuterPanel =new DoublePanel();
			magnitudeOuterPanel.setNumber(magnitudeOuter);
			cloakPanel.add(GUIBitsAndBobs.makeRow("outer", magnitudeOuterPanel, "magnitude"),"span"); 

			
			centrePanel = new LabelledVector3DPanel("Centre");
			centrePanel.setVector3D(centre);
			cloakPanel.add(centrePanel, "span");
			
			normalisedOpticalAxisDirectionPanel = new LabelledVector3DPanel("Symmetry axis");
			normalisedOpticalAxisDirectionPanel.setVector3D(symmetryAxis);
			cloakPanel.add(normalisedOpticalAxisDirectionPanel, "span");

			dPanel = new DoublePanel();
			dPanel.setNumber(d);
			cloakPanel.add(GUIBitsAndBobs.makeRow("d, inner prism separation", dPanel),"span");
			
			nPanel = new DoublePanel();
			nPanel.setNumber(n);
			cloakPanel.add(GUIBitsAndBobs.makeRow("refractive index", nPanel),"span");
			
			wPanel = new DoublePanel();
			wPanel.setNumber(MyMath.rad2deg(w));
			cloakPanel.add(GUIBitsAndBobs.makeRow("\u03C9, prism angle", wPanel,"�"),"span");
			
			hPanel = new DoublePanel();
			hPanel.setNumber(h);
			cloakPanel.add(GUIBitsAndBobs.makeRow("h, prism height", hPanel), "span");
			
			surfaceTransmissionCoefficientPanel = new DoublePanel();
			surfaceTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
			cloakPanel.add(GUIBitsAndBobs.makeRow("Transmission coefficient", surfaceTransmissionCoefficientPanel));
			
			shadowThrowingCheckBox = new JCheckBox("shadow throwing");
			shadowThrowingCheckBox.setSelected(shadowThrowing);
			cloakPanel.add(shadowThrowingCheckBox, "span");
			
			viewObjectComboBox = new JComboBox<ViewObject>(ViewObject.values());
			viewObjectComboBox.setSelectedItem(viewObject);
			objectsPanel.add(GUIBitsAndBobs.makeRow("view object", viewObjectComboBox));
			
			radiusPanel = new DoublePanel();
			radiusPanel.setNumber(radius);
			objectsPanel.add(GUIBitsAndBobs.makeRow("view object radius", radiusPanel),"span");
			
			toggleBackgroundLatticeCheckBox = new JCheckBox("Add a background lattice");
			toggleBackgroundLatticeCheckBox.setSelected(toggleBackgroundLattice);
			objectsPanel.add(toggleBackgroundLatticeCheckBox, "span");	
			
			
			//camera Stuff
			cameraViewDirectionPanel = new LabelledVector3DPanel("Camera view direction");
			cameraViewDirectionPanel.setVector3D(cameraViewDirection);
			cameraPanel.add(cameraViewDirectionPanel, "span");
			
			cameraViewCentrePanel = new LabelledVector3DPanel("Camera view centre");
			cameraViewCentrePanel.setVector3D(cameraViewCentre);
			cameraPanel.add(cameraViewCentrePanel, "span");
			
			cameraApertureSizeComboBox = new ApertureSizeComboBox();
			cameraApertureSizeComboBox.setApertureSize(cameraApertureSize);
			cameraPanel.add(new LabelledComponent("Camera aperture", cameraApertureSizeComboBox));
			
			cameraHorizontalFOVDegPanel = new DoublePanel();
			cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
			cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "�"), "span");
			
			cameraDistancePanel = new DoublePanel();
			cameraDistancePanel.setNumber(cameraDistance);
			cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance", cameraDistancePanel));
			
			cameraFocussingDistancePanel = new DoublePanel();
			cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
			cameraPanel.add(GUIBitsAndBobs.makeRow("Camera focusing distance", cameraFocussingDistancePanel), "span");

			
			
			//Raytrace stuff
			JPanel rayTracePanel = new JPanel();
			rayTracePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray tracing"));
			rayTracePanel.setLayout(new MigLayout("insets 0"));
			scenePanel.add(rayTracePanel, "grow, wrap");
			
			traceRayCheckBox = new JCheckBox("Trace scene with a ray");
			traceRayCheckBox.setSelected(traceRay);
			rayTracePanel.add(traceRayCheckBox, "span");
			
			
			rayTraceStartingPositionPanel = new LabelledVector3DPanel("Ray starting point");
			rayTraceStartingPositionPanel.setVector3D(rayTraceStartingPosition);
			rayTracePanel.add(rayTraceStartingPositionPanel, "span");
			

			rayAimOptionPanel = new JTabbedPane();
			rayTracePanel.add(rayAimOptionPanel, "span");
			
			rayLastClickTextArea = new JTextArea(2, 40);
			JScrollPane scrollPane = new JScrollPane(rayLastClickTextArea); 
			rayLastClickTextArea.setEditable(false);
			rayLastClickTextArea.setText("Click on the aim ray button set the aim of the light ray");
			rayLastClickInfo = new JButton("Aim ray");
			rayLastClickInfo.addActionListener(this);
			rayAimOptionPanel.add(GUIBitsAndBobs.makeRow(scrollPane, rayLastClickInfo), "Click-aim");
			
			rayAimPanel = new  LabelledVector3DPanel("Aim ray at");
			rayAimPanel.setVector3D(rayTracePosition);
			rayAimOptionPanel.add(rayAimPanel, "Manual-aim");
		}
		
		
		
		//setting the parameters based on the control panel input
		
		protected void acceptValuesInInteractiveControlPanel()
		{
			super.acceptValuesInInteractiveControlPanel();
			
			//Scene stuff
			cloakingStrat = (CloakingStrat)(cloakingStratComboBox.getSelectedItem());
			centre = centrePanel.getVector3D();
			symmetryAxis = normalisedOpticalAxisDirectionPanel.getVector3D();
			viewObject = (ViewObject)(viewObjectComboBox.getSelectedItem());
			d = dPanel.getNumber();
			w = MyMath.deg2rad(wPanel.getNumber());
			h = hPanel.getNumber();
			n = nPanel.getNumber();
			surfaceTransmissionCoefficient = surfaceTransmissionCoefficientPanel.getNumber();
			radius = radiusPanel.getNumber();
			shadowThrowing = shadowThrowingCheckBox.isSelected();
			toggleBackgroundLattice = toggleBackgroundLatticeCheckBox.isSelected();
			magnitudeInner = magnitudeInnerPanel.getNumber();
			magnitudeOuter = magnitudeOuterPanel.getNumber();
			cropHologram = cropHologramCheckBox.isSelected();
			
			
			//camera stuff
			cameraViewDirection = cameraViewDirectionPanel.getVector3D();
			cameraViewCentre = cameraViewCentrePanel.getVector3D();
			cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
			cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
			cameraDistance = cameraDistancePanel.getNumber();
			cameraApertureSize = cameraApertureSizeComboBox.getApertureSize();
			
			
			//ray trace
			traceRay = traceRayCheckBox.isSelected();
			
			rayTraceStartingPosition = rayTraceStartingPositionPanel.getVector3D();
			
			switch(rayAimOptionPanel.getSelectedIndex())
			{
			case 0:
				if(raytracingImageCanvas.getLastClickIntersection() == null) {
					break;
				}else {
				rayTracePosition = raytracingImageCanvas.getLastClickIntersection().p;
				}
				break;
			case 1:
				rayTracePosition =  rayAimPanel.getVector3D();
				break;
			}
			
			

		}
			
		


	/**
	 * @param w
	 * @param n
	 * @return alpha
	 */
	public static double calculateAlpha(double w, double n)
	{
		return Math.asin(n* Math.sin(w/2));
	}
	
	/**
	 * @param w
	 * @param alpha
	 * @return beta
	 */
	public static double calculateBeta(double w, double alpha)
	{
		return w - alpha;
	}
	
	/**
	 * @param w
	 * @param alpha
	 * @return delta
	 */
	public static double calculateDelta(double w, double alpha)
	{
		return 2*alpha - w;
	}
	
	/**
	 * @param d
	 * @param h
	 * @param beta
	 * @param delta
	 * @return D
	 */
	public static double calculateD(double d, double h, double w, double n)
	{
		return d + 2*h*( Math.tan(w - (Math.asin(n* Math.sin(w/2))))+(1/Math.tan(2*(Math.asin(n* Math.sin(w/2))) - w)));
	}
	

    @Override
    public void actionPerformed(ActionEvent e)
    {
            super.actionPerformed(e);

            if(e.getSource().equals(rayLastClickInfo))
    		{
            	RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
  
            	rayLastClickTextArea.setText("Ray aiming at "+i.p)
    				;

    		}

    }

	public static void main(final String[] args)
	{
		(new JakubsPrismaticCloakExplorer()).run();
	}
}
