package optics.raytrace.research.JakubsCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

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
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ConicPrism;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;

/**
 * A cloak which utilises prisms to steer light around a cloaked area.
 * @author Maik based on Jakub's design
 */
public class JakubsConicPrismaticCloakExplorer extends NonInteractiveTIMEngine
{

	/**
	 * The centre, which is halfway between the sets of prisms. This is where an object should be cloaked. 
	 */
	private Vector3D centre;

	/**
	 * Normalised vector in the direction of the optical axis.
	 * This should point towards the camera for the cloaking to be seen. If it is perpendicular, the cloak can be seen from the side. 
	 */
	private Vector3D normalisedOpticalAxisDirection;

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
	
	/**
	 * if true, the prisms will appear
	 */
	private boolean showPrisms;
	
	
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
	 * show a background lattice
	 */
	private boolean toggleBackgroundLattice;
	
	/**
	 * some raytracing stuff with a point and click function
	 */
	private boolean traceRay;
	
	private Vector3D rayTraceStartingPosition, rayTracePosition;
	
	
	public JakubsConicPrismaticCloakExplorer()
	{
		super();
		centre = Vector3D.O;
		normalisedOpticalAxisDirection= Vector3D.Z;
		d = 5;
		w = MyMath.deg2rad(60);
		h = 1;	
		n = 1.515;
		surfaceTransmissionCoefficient = 0.9;
		shadowThrowing = false;
		showPrisms = true;


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
		printStream.println("centre = "+centre);
		printStream.println("normalisedOpticalAxisDirection = "+normalisedOpticalAxisDirection);
		printStream.println("d = "+d);
		printStream.println("omega = "+w);
		printStream.println("h = "+h);
		printStream.println("n = "+n);
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
						new Vector3D (0,0,18), //centre
						scene,	// parent
						studio)
						);
			}
			
			//Adding the actual cloak 
			
			//some math to get the correct centres
			double D = calculateD(d, h, w ,n);
			Vector3D p1centre =Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(-D/2));
			Vector3D p2centre =Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(-d/2));
			Vector3D p3centre =Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(d/2));
			Vector3D p4centre =Vector3D.sum(centre, normalisedOpticalAxisDirection.getProductWith(D/2));
			
			ConicPrism	prism1 = new ConicPrism(
					"prism 1", //description,
					p1centre,// centre,
					normalisedOpticalAxisDirection,// normalisedOpticalAxisDirection,
					w,// w,
					h,// h,
					n,// n,
					false,// invertPrism,
					surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
					shadowThrowing,// shadowThrowing,
					scene,// parent,
					studio// studio
					);
			
			ConicPrism prism2 = new ConicPrism(
					"prism 2", //description,
					p2centre,// centre,
					normalisedOpticalAxisDirection,// normalisedOpticalAxisDirection,
					w,// w,
					h,// h,
					n,// n,
					true,// invertPrism,
					surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
					shadowThrowing,// shadowThrowing,
					scene,// parent,
					studio// studio
					);
			
			ConicPrism prism3 = new ConicPrism(
					"prism 3", //description,
					p3centre,// centre,
					normalisedOpticalAxisDirection.getProductWith(-1),// normalisedOpticalAxisDirection,
					w,// w,
					h,// h,
					n,// n,
					true,// invertPrism,
					surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
					shadowThrowing,// shadowThrowing,
					scene,// parent,
					studio// studio
					);
			
			
			ConicPrism prism4 = new ConicPrism(
					"prism 4", //description,
					p4centre,// centre,
					normalisedOpticalAxisDirection.getProductWith(-1),// normalisedOpticalAxisDirection,
					w,// w,
					h,// h,
					n,// n,
					false,// invertPrism,
					surfaceTransmissionCoefficient,// surfaceTransmissionCoefficient,
					shadowThrowing,// shadowThrowing,
					scene,// parent,
					studio// studio
					);
			
			if(showPrisms) {
			scene.addSceneObject(prism1);
			scene.addSceneObject(prism2);
			scene.addSceneObject(prism3);
			scene.addSceneObject(prism4);
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
			
			if(showPrisms) {
			scene.removeSceneObject(prism1);
			scene.removeSceneObject(prism2);
			scene.removeSceneObject(prism3);
			scene.removeSceneObject(prism4);
			}
			
			}
			
			studio.setLights(LightSource.getStandardLightsFromBehind());
			studio.setCamera(getStandardCamera());
			studio.setScene(scene);
		}
		
	//Scene stuff

	private LabelledVector3DPanel centrePanel, normalisedOpticalAxisDirectionPanel;
	private JComboBox<ViewObject> viewObjectComboBox;
	private DoublePanel dPanel, wPanel, hPanel, nPanel, surfaceTransmissionCoefficientPanel, radiusPanel;
	private JCheckBox shadowThrowingCheckBox, toggleBackgroundLatticeCheckBox, showPrismsCheckBox;

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
			
			showPrismsCheckBox = new JCheckBox("Show the prisms");
			showPrismsCheckBox.setSelected(showPrisms);
			cloakPanel.add(showPrismsCheckBox, "span");
			
			centrePanel = new LabelledVector3DPanel("Centre");
			centrePanel.setVector3D(centre);
			cloakPanel.add(centrePanel, "span");
			
			normalisedOpticalAxisDirectionPanel = new LabelledVector3DPanel("Normal direction");
			normalisedOpticalAxisDirectionPanel.setVector3D(normalisedOpticalAxisDirection);
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
			showPrisms = showPrismsCheckBox.isSelected();
			centre = centrePanel.getVector3D();
			normalisedOpticalAxisDirection = normalisedOpticalAxisDirectionPanel.getVector3D();
			viewObject = (ViewObject)(viewObjectComboBox.getSelectedItem());
			d = dPanel.getNumber();
			w = MyMath.deg2rad(wPanel.getNumber());
			h = hPanel.getNumber();
			n = nPanel.getNumber();
			surfaceTransmissionCoefficient = surfaceTransmissionCoefficientPanel.getNumber();
			radius = radiusPanel.getNumber();
			shadowThrowing = shadowThrowingCheckBox.isSelected();
			toggleBackgroundLattice = toggleBackgroundLatticeCheckBox.isSelected();
			
			
			
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
		(new JakubsConicPrismaticCloakExplorer()).run();
	}
}
