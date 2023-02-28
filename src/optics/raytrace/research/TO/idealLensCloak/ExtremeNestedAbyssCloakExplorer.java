package optics.raytrace.research.TO.idealLensCloak;

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

import math.*;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
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
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;

/**
 * Two nested abyss cloaks placed such that any object placed in the inner cloak will be fully hidden from any outside viewing position.
 * Unlike the original nested abyss cloak, the inner cloak is placed in cell 2 as oppose to cell 1. 
 * The placement is also very precise but at the same time allows for the inner cloak to have any focal length as long as it is within the cloaking range
 * This is because it no longer depends on the image position of the inside object but rather the image of the base lens of the inner cloak with respect
 * to the outer cloak
 * 
 * @author Maik
 */
public class ExtremeNestedAbyssCloakExplorer extends NonInteractiveTIMEngine
{

	//defining the cloak parameters, I = inner cloak, O = outer cloak
	private EditableLensSimplicialComplex cloakO;
	private Vector3D baseCentreO;
	private boolean showO;
	private boolean showCylindersI, showCylindersO;
	private LensType lensTypeI, lensTypeO;
	private double heightO;
	private double baseRadiusO;
	private double baseRadiusFraction;
	private double baseLensFI,baseLensFO;
	private double lowerInnerVertexI, lowerInnerVertexO;

	public enum ShowInnerCloaks
	{
		ONE("inner cloak in 1 cell"),
		TWO("inner cloak in 2 cells"),
		ALL("inner cloak in all cells"),
		NONE("no inner cloak");

		private String description;

		private ShowInnerCloaks(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private ShowInnerCloaks showInnerCloaks;


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

	/**
	 * setting up an editable tim head
	 */
	private Vector3D timCentre, timFront, timTop, timRight;
	private double timRadius;

	/**
	 * some raytracing stuff with a point and click function
	 */
	private boolean traceRay;
	
	private Vector3D rayTraceStartingPosition, rayTracePosition;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public ExtremeNestedAbyssCloakExplorer()
	{
		super();
		
		
		//cloak stuff
		baseCentreO = new Vector3D(0,-0.5,0);
		showO = true;
		showCylindersI = false;
		showCylindersO = false;
		lensTypeO = LensType.IDEAL_THIN_LENS;
		lensTypeI = LensType.IDEAL_THIN_LENS;
		heightO = 2;
		baseRadiusO = 1;
		baseRadiusFraction = 0.8;
		baseLensFI = 0.15;
		baseLensFO = 0.03;
		lowerInnerVertexI = 0.8;
		lowerInnerVertexO = 0.25;
		showInnerCloaks = ShowInnerCloaks.NONE;
		
		//other scene objects
		showPatternedSphere = false;
		patternedSphereRadius = 0.1;
		timCentre = new Vector3D(0,0,5); 
		timRadius = 0.8;
		timFront =Vector3D.Z.getProductWith(-1); //front direction
		timTop = Vector3D.Y; //top direction
		timRight = Vector3D.X; //right direction


		//ray trace
		traceRay = false;
		rayTraceStartingPosition = Vector3D.Z.getProductWith(-9);
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

		//Tim engine setup
		renderQuality = RenderQualityEnum.DRAFT;//Set the default render quality		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;// set to true for interactive version
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Testing an ideal lens cloak with single ray tracing";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"ExtremeNestedAbyssCloakExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
			throws SceneException
	{
		// System.out.println("LensCloakVisualiser::populateStudio: frame="+frame);

				// the studio
				studio = new Studio();


				//setting the scene
				SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		

				// the standard scene objects
				scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio)); //the floor
				scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	
				
				
				double frameRadius = 0.01; // radius of cloak frame
				
				cloakO = new EditableLensSimplicialComplex(
						"outer Abyss cloak",	// description
						scene,	// parent
						studio
						);
				// ... and initialise it as an ideal-lens cloak
				cloakO.setLensTypeRepresentingFace(lensTypeO);
				cloakO.setShowStructureP(showCylindersO);
				cloakO.setVertexRadiusP(frameRadius);
				cloakO.setShowStructureV(false);
				cloakO.setVertexRadiusV(frameRadius);
				cloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.BLUE, false));

				cloakO.initialiseToOmnidirectionalLens(
						lowerInnerVertexO,	// physicalSpaceFractionalLowerInnerVertexHeight
						0.95,	// physicalSpaceFractionalUpperInnerVertexHeight
						1./(-heightO/baseLensFO + 1/lowerInnerVertexO),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
						new Vector3D(0, 2+heightO/2, 0),	// topVertex
						baseCentreO,	// baseCentre
						new Vector3D(0,0,baseRadiusO)	// baseVertex
						);
				
				scene.addSceneObject(cloakO, showO);
				
				
				//now make a copy of this cloak but always initialise it as an ideal thin lens but do not add it to the scene...
				//in case it is not an ideal thin lens, we need to set it as such...
				EditableLensSimplicialComplex idealCloakO = new EditableLensSimplicialComplex(
						"outer Abyss cloak clone",	// description
						scene,	// parent
						studio
						);
				// ... and initialise it as an (always) ideal-lens cloak
				idealCloakO.setLensTypeRepresentingFace(LensType.IDEAL_THIN_LENS);
				idealCloakO.setShowStructureP(showCylindersO);
				idealCloakO.setVertexRadiusP(frameRadius);
				idealCloakO.setShowStructureV(false);
				idealCloakO.setVertexRadiusV(frameRadius);
				idealCloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.BLUE, false));

				idealCloakO.initialiseToOmnidirectionalLens(
						lowerInnerVertexO,	// physicalSpaceFractionalLowerInnerVertexHeight
						0.95,	// physicalSpaceFractionalUpperInnerVertexHeight
						1./(-heightO/baseLensFO + 1/lowerInnerVertexO),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
						new Vector3D(0, 2+heightO/2, 0),	// topVertex
						baseCentreO,	// baseCentre
						new Vector3D(0,0,baseRadiusO)	// baseVertex
						);

				int innerCloaks = 0;
				switch(showInnerCloaks) {
				case ONE:
					innerCloaks = 1;
					break;
				case TWO:
					innerCloaks = 2;
					break;
				case ALL:
					innerCloaks = 3;
					break;
				case NONE:
					innerCloaks = 0;
					break;
				}
				
				
				ArrayList<Vector3D> cloakVertices = new ArrayList<Vector3D>(9);
				cloakVertices.clear();
				for(Vector3D vertex:idealCloakO.getVertices()) cloakVertices.add(vertex.clone());
				ArrayList<Edge> cloakEdges = new ArrayList<Edge>(10);
				cloakEdges.clear();
				for(Edge edge:idealCloakO.getEdges()) cloakEdges.add(edge.clone());
				try {
					SimplicialComplex cloakImage = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(
							cloakVertices,	// vertices
							cloakEdges	// edges

							);
				
				for(int i=1; i<=innerCloaks;i++) {
				
					//find the image of this point...
					
					int p = 4-i; 
					Vector3D corner = cloakImage.getVertex(i-1);
					
					String lensFace = "Face #"+(p+(p-1)*p);
					EditableParametrisedTriangle cLens = (EditableParametrisedTriangle)(idealCloakO.getSceneObjectContainer().getFirstSceneObjectWithDescription(lensFace, true));
					IdealThinLensSurface cLensFocal = (IdealThinLensSurface)(cLens.getSurfaceProperty());
					Vector3D cPoint = cLensFocal.getImagePosition(corner, ImagingDirection.POS2NEG);
					//... when seen thorough the opposite face of index (p+(p-1)*p) where p is 4-i.
					
					//this will form the top vertex of the base lens of every corresponding inner cloak.
					Vector3D vertex1outer = cloakImage.getFace(p+(p-1)*p).getVertices()[0];
					Vector3D vertex2outer = cloakImage.getFace(p+(p-1)*p).getVertices()[1];
					Vector3D vertex3outer = cloakImage.getFace(p+(p-1)*p).getVertices()[2];
					
					
					//Now take the three vertices and use it to extract the base centre of the inner cloak 
					
					//first find the vertices:
					Vector3D vertex1Inner = Vector3D.sum(cPoint, Vector3D.difference(vertex1outer, cPoint).getProductWith(baseRadiusFraction));
					Vector3D vertex2Inner = Vector3D.sum(cPoint ,Vector3D.difference(vertex2outer, cPoint).getProductWith(baseRadiusFraction));
				
					//From these the centre is simply
					Vector3D baseCentreI = Vector3D.sum(cPoint, vertex1Inner, vertex2Inner).getProductWith(1/3.);
					
					//Next find the height, which will be calculated by assuming the maximum height possible. 
					//first get a surface normal and make sure it points towards the base lens of the outer cloak
					Vector3D normalI = Vector3D.crossProduct(Vector3D.difference(vertex1outer, cPoint), Vector3D.difference(vertex2outer, cPoint)).getNormalised();
					Vector3D normalIBaseCentreFacing = normalI.getProductWith(-Math.signum(Vector3D.scalarProduct(normalI, Vector3D.difference(baseCentreI, baseCentreO))));
					
					//Now this can be used to find the height of the inner cloak and the position where this intersects the outer cloak
					Vector3D innerTopVertex = null;
					try {
						innerTopVertex = Geometry.linePlaneIntersection(baseCentreI, normalI, vertex3outer, 
								Vector3D.crossProduct(Vector3D.difference(vertex2outer, vertex1outer), Vector3D.difference(vertex3outer, vertex1outer)).getNormalised());
					} catch (MathException e) {
						System.err.println("there is no intersection!");
						e.printStackTrace();
					}
					
					double heightI = Vector3D.getDistance(innerTopVertex, baseCentreI);
					
					EditableLensSimplicialComplex cloakI = new EditableLensSimplicialComplex(
							"inner Abyss cloak",	// description
							scene,	// parent
							studio
							);
					// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
					cloakI.setLensTypeRepresentingFace(lensTypeI);
					cloakI.setShowStructureP(showCylindersI);
					cloakI.setVertexRadiusP(frameRadius);
					cloakI.setShowStructureV(false);
					cloakI.setVertexRadiusV(frameRadius);
					cloakI.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.RED, false));

					cloakI.initialiseToOmnidirectionalLens(
							lowerInnerVertexI,	// physicalSpaceFractionalLowerInnerVertexHeight
							0.95,	// physicalSpaceFractionalUpperInnerVertexHeight
							1./(-heightI/baseLensFI + 1/lowerInnerVertexI),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
							innerTopVertex,	// topVertex
							baseCentreI,	// baseCentre
							cPoint	// baseVertex
							);
					
					scene.addSceneObject(cloakI); 
					
					patternedSphereCentre = Vector3D.sum(normalIBaseCentreFacing.getProductWith(heightI/2), baseCentreI);
//					patternedSphereRadius = heightI/2.5;
					
					EditableScaledParametrisedSphere patternedSphereI = new EditableScaledParametrisedSphere(
							"Sphere in inner cloak", // description
							patternedSphereCentre, // centre
							patternedSphereRadius,	// radius
							new Vector3D(1, 1, 1),	// pole
							new Vector3D(1, 0, 0),	// phi0Direction
							0, Math.PI,	// sThetaMin, sThetaMax
							-Math.PI, Math.PI,	// sPhiMin, sPhiMax
							new SurfaceTiling(
									new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
									new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
									2*Math.PI/6,
									2*Math.PI/6
								),
							scene, studio);
					scene.addSceneObject(patternedSphereI, showPatternedSphere);

				}
				
				}catch (InconsistencyException e) {
					// TODO this should always return a value unless no image can be found or the outer cloak is not initalised
					//using an ideal thin lens surface
					System.err.println("if this is seen then the code broke :(");
					e.printStackTrace();
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
				
				studio.setScene(scene);
				// trace the rays with trajectory through the scene
				studio.traceRaysWithTrajectory();
				scene.removeAllSceneObjectsWithDescription("inner Abyss cloak", true);
				scene.removeAllSceneObjectsWithDescription("outer Abyss cloak", showO);
				
				}
				
				//Lastly, add tims head because it is not a complete ray tracer without it.
				
				scene.addSceneObject(new TimHead("Tim head", 
						timCentre, 
						timRadius, 
						timFront,//Vector3D.Z.getProductWith(-1), //front direction
						timTop,//Vector3D.Y, //top direction
						timRight,//Vector3D.X, //right direction
						scene, 
						studio)
						);
				
				
				studio.setScene(scene);
				studio.setCamera(getStandardCamera());
				studio.setLights(LightSource.getStandardLightsFromBehind());
				
	}




	//
	// for interactive version
	//
	//cloak stuff
	private LabelledVector3DPanel baseCentreOPanel;
	private JCheckBox showCylindersICheckBox, showCylindersOCheckBox, showOCkeckBox;
	private JComboBox<LensType>  lensTypeOComboBox, lensTypeIComboBox;
	private LabelledDoublePanel lowerInnerVertexOPanel, lowerInnerVertexIPanel, baseLensFIPanel, baseLensFOPanel, baseRadiusFractionPanel,
	baseRadiusOPanel, heightOPanel; 
	private JComboBox<ShowInnerCloaks>  showInnerCloaksComboBox;

	//camera stuff
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
	private DoublePanel cameraFocussingDistancePanel, cameraHorizontalFOVDegPanel, cameraDistancePanel;
	private ApertureSizeComboBox cameraApertureSizeComboBox;

	//ray trace stuff
	private LabelledVector3DPanel rayTraceStartingPositionPanel, rayAimPanel;
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	private JCheckBox traceRayCheckBox;
	private JTabbedPane rayAimOptionPanel;
	
	//others
	private JCheckBox showPatternedSphereCheckBox;
	private LabelledDoublePanel patternedSphereRadiusPanel, timRadiusPanel;
	private LabelledVector3DPanel timCentrePanel, timFrontPanel, timTopPanel, timRightPanel;
		
		protected void createInteractiveControlPanel()
		{
			super.createInteractiveControlPanel();
			
			//the panels
			JTabbedPane tabbedPane = new JTabbedPane();
			interactiveControlPanel.add(tabbedPane, "span");
			
			JPanel cloakPanel = new JPanel();
			cloakPanel.setLayout(new MigLayout("insets 0"));
			tabbedPane.addTab("Cloak", cloakPanel);
			

			
			//cloak stuff outer
			JPanel cloakOuterPanel = new JPanel();
			cloakOuterPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer Cloak"));
			cloakOuterPanel.setLayout(new MigLayout("insets 0"));
			cloakPanel.add(cloakOuterPanel, "wrap");
			
			showOCkeckBox= new JCheckBox("Show Outer cloak");
			showOCkeckBox.setSelected(showO);
			cloakOuterPanel.add(showOCkeckBox, "span");
			
			lensTypeOComboBox = new JComboBox<LensType>(LensType.values());
			lensTypeOComboBox.setSelectedItem(lensTypeO);
			cloakOuterPanel.add(lensTypeOComboBox,"span");
			
			baseCentreOPanel = new LabelledVector3DPanel("base centre");
			baseCentreOPanel.setVector3D(baseCentreO);
			cloakOuterPanel.add(baseCentreOPanel, "span");
			
			baseLensFOPanel = new LabelledDoublePanel("base focal length");
			baseLensFOPanel.setNumber(baseLensFO);
			cloakOuterPanel.add(baseLensFOPanel, "span");
			
			baseRadiusOPanel = new LabelledDoublePanel("base radius");
			baseRadiusOPanel.setNumber(baseRadiusO);
			cloakOuterPanel.add(baseRadiusOPanel, "span");
			
			heightOPanel = new LabelledDoublePanel("cloak height");
			heightOPanel.setNumber(heightO);
			cloakOuterPanel.add(heightOPanel, "span");
			
			lowerInnerVertexOPanel= new LabelledDoublePanel("lower vertex (0-0.95)");
			lowerInnerVertexOPanel.setNumber(lowerInnerVertexO);
			cloakOuterPanel.add(lowerInnerVertexOPanel, "span");
			
			showCylindersOCheckBox= new JCheckBox("Show frame");
			showCylindersOCheckBox.setSelected(showCylindersO);
			cloakOuterPanel.add(showCylindersOCheckBox, "span");
			
			//cloak stuff inner
			JPanel cloakInnerPanel = new JPanel();
			cloakInnerPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner Cloak"));
			cloakInnerPanel.setLayout(new MigLayout("insets 0"));
			cloakPanel.add(cloakInnerPanel, "span");
			
			showInnerCloaksComboBox = new JComboBox<ShowInnerCloaks>(ShowInnerCloaks.values());
			showInnerCloaksComboBox.setSelectedItem(showInnerCloaks);
			cloakInnerPanel.add(showInnerCloaksComboBox,"span");
			
			lensTypeIComboBox= new JComboBox<LensType>(LensType.values());
			lensTypeIComboBox.setSelectedItem(lensTypeI);
			cloakInnerPanel.add(lensTypeIComboBox,"span");
			
			baseLensFIPanel = new LabelledDoublePanel("base focal length");
			baseLensFIPanel.setNumber(baseLensFI);
			cloakInnerPanel.add(baseLensFIPanel, "span");
			
			lowerInnerVertexIPanel = new LabelledDoublePanel("lower vertex");
			lowerInnerVertexIPanel.setNumber(lowerInnerVertexI);
			cloakInnerPanel.add(lowerInnerVertexIPanel, "span");
			
			baseRadiusFractionPanel = new LabelledDoublePanel("base radius fraction (0-1)");
			baseRadiusFractionPanel.setNumber(baseRadiusFraction);
			cloakInnerPanel.add(baseRadiusFractionPanel, "span");
			
			showCylindersICheckBox= new JCheckBox("Show frame");
			showCylindersICheckBox.setSelected(showCylindersI);
			cloakInnerPanel.add(showCylindersICheckBox, "span");
			
			
			JPanel cameraAndScenePanel = new JPanel();
			cameraAndScenePanel.setLayout(new MigLayout("insets 0"));
			tabbedPane.addTab("Camera & Objects", cameraAndScenePanel);	
			
			//camera Stuff
			JPanel cameraPanel = new JPanel();
			cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("camera"));
			cameraPanel.setLayout(new MigLayout("insets 0"));
			cameraAndScenePanel.add(cameraPanel, "span");
			
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
			cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "\u00B0"), "span");
			
			cameraDistancePanel = new DoublePanel();
			cameraDistancePanel.setNumber(cameraDistance);
			cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance", cameraDistancePanel));
			
			cameraFocussingDistancePanel = new DoublePanel();
			cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
			cameraPanel.add(GUIBitsAndBobs.makeRow("Camera focusing distance", cameraFocussingDistancePanel), "span");

			//scene stuff
			JPanel scenePanel = new JPanel();
			scenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("rest of scene"));
			scenePanel.setLayout(new MigLayout("insets 0"));
			cameraAndScenePanel.add(scenePanel, "span");
			
			showPatternedSphereCheckBox = new JCheckBox("Show sphere(s) with");
			showPatternedSphereCheckBox.setSelected(showPatternedSphere);
			scenePanel.add(showPatternedSphereCheckBox);
			
			patternedSphereRadiusPanel = new LabelledDoublePanel("sphere radius");
			patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
			scenePanel.add(patternedSphereRadiusPanel, "span");
			
			timCentrePanel = new LabelledVector3DPanel("tim centre");
			timCentrePanel.setVector3D(timCentre);
			scenePanel.add(timCentrePanel, "span");
			
			timFrontPanel = new LabelledVector3DPanel("tim front direction");
			timFrontPanel.setVector3D(timFront);
			scenePanel.add(timFrontPanel, "span");
			
			timTopPanel = new LabelledVector3DPanel("tim top direction");
			timTopPanel.setVector3D(timTop);
			scenePanel.add(timTopPanel, "span");
			
			timRightPanel = new LabelledVector3DPanel("tim right direction");
			timRightPanel.setVector3D(timRight);
			scenePanel.add(timRightPanel, "span");
			
			timRadiusPanel = new LabelledDoublePanel("tim radius");
			timRadiusPanel.setNumber(timRadius);
			scenePanel.add(timRadiusPanel, "span");
			
			
			//Raytrace stuff
			JPanel rayTracePanel = new JPanel();
			rayTracePanel.setLayout(new MigLayout("insets 0"));
			tabbedPane.addTab("Ray Trace", rayTracePanel);
			
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
			
			//cloaky stuff
			baseCentreO = baseCentreOPanel.getVector3D();
			showCylindersI = showCylindersICheckBox.isSelected();
			showCylindersO = showCylindersOCheckBox.isSelected();
			showO = showOCkeckBox.isSelected();
			lensTypeO = (LensType)(lensTypeOComboBox.getSelectedItem());
			lensTypeI = (LensType)(lensTypeIComboBox.getSelectedItem());
			lowerInnerVertexO = lowerInnerVertexOPanel.getNumber();
			lowerInnerVertexI = lowerInnerVertexIPanel.getNumber();
			baseLensFI = baseLensFIPanel.getNumber();
			baseLensFO = baseLensFOPanel.getNumber();
			baseRadiusFraction = baseRadiusFractionPanel.getNumber();
			baseRadiusO = baseRadiusOPanel.getNumber();
			heightO = heightOPanel.getNumber();
			showInnerCloaks = (ShowInnerCloaks)(showInnerCloaksComboBox.getSelectedItem());
			
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
			
			//other scene stuff
			showPatternedSphere = showPatternedSphereCheckBox.isSelected();
			patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
			timRadius =timRadiusPanel.getNumber();
			timCentre = timCentrePanel.getVector3D();
			timFront = timFrontPanel.getVector3D();
			timTop = timTopPanel.getVector3D();
			timRight = timRightPanel.getVector3D();

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
			(new ExtremeNestedAbyssCloakExplorer()).run();
		}
	}
