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
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.LinearTransformation;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class NestedAbyssCloakExplorer extends NonInteractiveTIMEngine
{
	// I = inner Abyss cloak, O = outer Abyss cloak
	private boolean showI, showO;
	private boolean showCylindersI, showCylindersO;
	private LensType lensTypeI, lensTypeO;
	private Vector3D baseCentreI, baseCentreO;
	private Vector3D eulerAnglesDegI, eulerAnglesDegO;
	private double heightI, heightO;
	private double baseLensFI,baseLensFO;
	// private double virtualSpaceFractionalLowerInnerVertexHeightI, virtualSpaceFractionalLowerInnerVertexHeightO;
	private boolean showImageOfInnerCloak;
	private boolean makeCylinderModelsShadowThrowing;
	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * The patterned sphere is an additional object to study.
	 * It is normally placed inside the cloak.
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
	
	private boolean showImage1OfPatternedSphere, showImage2OfPatternedSphere, makePatternedSpheresShadowThrowing;
	
	
	/**
	 * direction of the rotation axis, which passes through the view centre, of the (outside) camera when in movie mode
	 */
	private Vector3D cameraRotationAxisDirection;
	


	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public NestedAbyssCloakExplorer()
	{
		super();
		
		showI = true;
		showO = true;
		lensTypeI = LensType.NONE; // IDEAL_THIN_LENS;
		lensTypeO = LensType.NONE; // IDEAL_THIN_LENS;
		showCylindersI = true;
		showCylindersO = true;
		baseCentreI = new Vector3D(0, 0, 6.4);
		baseCentreO = new Vector3D(0, 0, 6);
		eulerAnglesDegI = new Vector3D(0, -90, 0);
		eulerAnglesDegO = new Vector3D(0, 90, 90);
		heightI = 1./3.;
		heightO = 2;
		baseLensFI = 0.075;
		baseLensFO = 0.25;	//  1.35;
//		virtualSpaceFractionalLowerInnerVertexHeightI = -5;
//		virtualSpaceFractionalLowerInnerVertexHeightO = -5;
		showImageOfInnerCloak = true;
		makeCylinderModelsShadowThrowing = true;

		
		// other scene objects
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop
		showPatternedSphere = true;
		patternedSphereCentre = new Vector3D(0, .08333333, 6.4);
		patternedSphereRadius = 0.05;

		showImage1OfPatternedSphere = true;
		showImage2OfPatternedSphere = true;
		makePatternedSpheresShadowThrowing = true;

		
		// camera parameters
		cameraViewDirection = new Vector3D(-1, -0.16, 0.2);
		cameraViewCentre = new Vector3D(0, 0, 7);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 45;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;
		
		movie = false;
		// if movie = true, then the following are relevant:
		numberOfFrames = 360;
		firstFrame = 0;
		lastFrame = 90;
		cameraRotationAxisDirection = new Vector3D(1, 0, 0);


		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		windowTitle = "Dr TIM's Nested-Abyss-cloak explorer";
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
				"NestedAbyssCloakExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);
		printStream.println("showI = "+showI);
		printStream.println("showO = "+showO);
		printStream.println("showCylindersI = "+showCylindersI);
		printStream.println("showCylindersO = "+showCylindersO);
		printStream.println("lensTypeI = "+lensTypeI);
		printStream.println("lensTypeO = "+lensTypeO);
		printStream.println("baseCentreI = "+baseCentreI);
		printStream.println("baseCentreO = "+baseCentreO);
		printStream.println("eulerAnglesDegI = "+eulerAnglesDegI);
		printStream.println("eulerAnglesDegO = "+eulerAnglesDegO);
		printStream.println("heightI = "+heightI);
		printStream.println("heightO = "+heightO);
		printStream.println("baseLensFI = "+baseLensFI);
		printStream.println("baseLensFO = "+baseLensFO);
		printStream.println("showImageOfInnerCloak = "+showImageOfInnerCloak);
		printStream.println("makeCylinderModelsShadowThrowing = "+makeCylinderModelsShadowThrowing);
//		printStream.println("virtualSpaceFractionalLowerInnerVertexHeightI = "+virtualSpaceFractionalLowerInnerVertexHeightI);
//		printStream.println("virtualSpaceFractionalLowerInnerVertexHeightO = "+virtualSpaceFractionalLowerInnerVertexHeightO);
		
		//
		// the rest of the scene
		//
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		printStream.println("patternedSphereCentre = "+patternedSphereCentre);
		printStream.println("patternedSphereRadius = "+patternedSphereRadius);
		printStream.println("showPatternedSphere = "+showPatternedSphere);
		printStream.println("showImage1OfPatternedSphere = "+showImage1OfPatternedSphere);
		printStream.println("showImage2OfPatternedSphere = "+showImage2OfPatternedSphere);
		printStream.println("makePatternedSpheresShadowThrowing = "+makePatternedSpheresShadowThrowing);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	
	EditableLensSimplicialComplex abyssCloakI, abyssCloakO;
	private Vector3D frame0CameraViewDirection;
	

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();
		
		// set the camera
		if(movie)
		{
			// calculate view position that corresponds to the current frame
			// initial camera position
			Vector3D initialCameraPosition = Vector3D.sum(cameraViewCentre, frame0CameraViewDirection.getWithLength(-cameraDistance));
			// the camera will move in a circle; calculate its centre
			Vector3D centreOfCircle = Geometry.getPointOnLineClosestToPoint(
					cameraViewCentre,	// pointOnLine
					cameraRotationAxisDirection,	// directionOfLine
					initialCameraPosition	// point
				);
			// construct two unit vectors that span the plane of the circle in which the camera will move
			Vector3D uHat = Vector3D.difference(initialCameraPosition, centreOfCircle).getNormalised();
			Vector3D vHat = Vector3D.crossProduct(cameraRotationAxisDirection, uHat).getNormalised();

			// define the azimuthal angle phi that parametrises the circle
			double phi = 2.*Math.PI*frame/numberOfFrames;
			System.out.println("LensCloakVisualiser::populateStudio: phi="+phi+"(="+MyMath.rad2deg(phi)+"deg)");
			
			// finally, calculate the view direction
			cameraViewDirection = Vector3D.difference(
					cameraViewCentre, 
					Vector3D.sum(centreOfCircle, uHat.getProductWith(Math.cos(phi)*cameraDistance), vHat.getProductWith(Math.sin(phi)*cameraDistance))
				);
		}
		studio.setCamera(getStandardCamera());

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// create the inner cloak; first create a lens-simplicial complex...
		abyssCloakI = new EditableLensSimplicialComplex(
				"Inner Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		double vertexRadiusI = 0.02;
		abyssCloakI.setLensTypeRepresentingFace(lensTypeI);
		abyssCloakI.setShowStructureP(showCylindersI);
		abyssCloakI.setVertexRadiusP(vertexRadiusI);
		abyssCloakI.setShowStructureV(false);
		abyssCloakI.setVertexRadiusV(vertexRadiusI);
		abyssCloakI.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, makeCylinderModelsShadowThrowing));
		
		LinearTransformation tI = new LinearTransformation(
				LinearTransformation.getMatrixForEulerRotation(MyMath.deg2rad(eulerAnglesDegI.x), MyMath.deg2rad(eulerAnglesDegI.y), MyMath.deg2rad(eulerAnglesDegI.z)),
				baseCentreI
			);
		
		// the relationship between the fractional lower inner vertex height in physical space, h1P;
		// the fractional lower  inner vertex height in virtual space, h1V;
		// and the focal length of the base lens, f, is
		// 1/(h1P h) + 1/(-h1V h) == 1/f

		double h1PI = 1./3.;
		abyssCloakI.initialiseToOmnidirectionalLens(
				h1PI,	// physicalSpaceFractionalLowerInnerVertexHeight
				2./3.,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-heightI/baseLensFI + 1/h1PI),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				tI.transformPosition(new Vector3D(0, heightI, 0)),	// topVertex
				tI.transformPosition(new Vector3D(0, 0, 0)),	// baseCentre
				tI.transformPosition(new Vector3D(heightI/Math.sqrt(2.), 0, 0))	// baseVertex1
			);
		
		scene.addSceneObject(abyssCloakI, showI);

		
		// create the outer cloak; first create a lens-simplicial complex...
		abyssCloakO = new EditableLensSimplicialComplex(
				"Outer Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		double vertexRadiusO = 0.02;
		abyssCloakO.setLensTypeRepresentingFace(lensTypeO);
		abyssCloakO.setShowStructureP(showCylindersO);
		abyssCloakO.setVertexRadiusP(vertexRadiusO);
		abyssCloakO.setShowStructureV(false);
		abyssCloakO.setVertexRadiusV(vertexRadiusO);
		abyssCloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, makeCylinderModelsShadowThrowing));
		
		LinearTransformation tO = new LinearTransformation(
				LinearTransformation.getMatrixForEulerRotation(MyMath.deg2rad(eulerAnglesDegO.x), MyMath.deg2rad(eulerAnglesDegO.y), MyMath.deg2rad(eulerAnglesDegO.z)),
				baseCentreO
			);
		
		// the relationship between the fractional lower inner vertex height in physical space, h1P;
		// the fractional lower  inner vertex height in virtual space, h1V;
		// and the focal length of the base lens, f, is
		// 1/(h1P h) + 1/(-h1V h) == 1/f

		double h1PO = 1./3.;
		abyssCloakO.initialiseToOmnidirectionalLens(
				h1PO,	// physicalSpaceFractionalLowerInnerVertexHeight
				2./3.,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-heightO/baseLensFO + 1/h1PO),	// virtualSpaceFractionalLowerInnerVertexHeightO,	// virtualSpaceFractionalLowerInnerVertexHeight
				tO.transformPosition(new Vector3D(0, heightO, 0)),	// topVertex
				tO.transformPosition(new Vector3D(0, 0, 0)),	// baseCentre
				tO.transformPosition(new Vector3D(heightO/Math.sqrt(2.), 0, 0))	// baseVertex1
			);
		
		scene.addSceneObject(abyssCloakO, showO);
		
		
		// the image of the inner cloak due to the outer cloak (assuming it is in cell 0)
		// abyssCloakO.getLensSimplicialComplex().mapToOutside(simplexIndex, position)
		try {
			// make a copy of the simplicial complex representing the inner cloak's physical-space structure...
			// SimplicialComplex innerCloakImage = new SimplicialComplex(abyssCloakI.getLensSimplicialComplex());
			// SimplicialComplex innerCloakImage = new SimplicialComplex();
			
			// ... and change the positions of all the vertices to the image positions due to the outer cloak
			ArrayList<Vector3D> innerCloakImageVertices = new ArrayList<Vector3D>(6);
			for(Vector3D vertex:abyssCloakI.getVertices()) innerCloakImageVertices.add(abyssCloakO.getLensSimplicialComplex().mapToOutside(0, vertex));
			ArrayList<Edge> innerCloakImageEdges = new ArrayList<Edge>(10);
			for(Edge edge:abyssCloakI.getEdges()) innerCloakImageEdges.add(edge.clone());
			// System.out.println("NestedAbyssCloakExplorer::populateStudio(): innerCloakImageVertices="+innerCloakImageVertices+", innerCloakImageEdges = "+innerCloakImageEdges);
			SimplicialComplex innerCloakImage = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(
					innerCloakImageVertices,	// vertices
					innerCloakImageEdges	// edges
				);
			scene.addSceneObject(innerCloakImage.getEditableSceneObjectCollection(
					"Image of inner cloak due to outer cloak",	// description
					true,	// showVertices
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, makeCylinderModelsShadowThrowing),	// vertexSurfaceProperty
					vertexRadiusI,	// vertexRadius
					true,	// showEdges
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, makeCylinderModelsShadowThrowing),	// edgeSurfaceProperty
					vertexRadiusI,	// edgeRadius
					false,
					null,	// faceSurfaceProperty
					scene,
					studio
				), showImageOfInnerCloak);
		} catch (InconsistencyException e) {
			//  something went wrong with the cloning -- panic!
			e.printStackTrace();
		}
		
		
		// the patterned sphere...
		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);
		
		// ... and the positions of its image due to the inner cloak...
		Vector3D p1 = abyssCloakI.getLensSimplicialComplex().mapToOutsideSpace(patternedSphereCentre);
		EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
				"Image of patterned sphere due to inner cloak", // description
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
		scene.addSceneObject(patternedSphere1, showImage1OfPatternedSphere);

		// ... and due to both cloaks
		Vector3D p2 = abyssCloakO.getLensSimplicialComplex().mapToOutside(0, p1);
		EditableScaledParametrisedSphere patternedSphere2 = new EditableScaledParametrisedSphere(
				"Image of patterned sphere due to both cloaks", // description
				p2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, makePatternedSpheresShadowThrowing),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere2, showImage2OfPatternedSphere);
	}

	
	
	//
	// for interactive version
	//

	// the cloaks
	private JCheckBox showICheckBox, showOCheckBox;
	private JComboBox<LensType> lensTypeIComboBox, lensTypeOComboBox;
	private JCheckBox showCylindersICheckBox, showCylindersOCheckBox, makeCylinderModelsShadowThrowingCheckBox;
	private LabelledVector3DPanel baseCentreIPanel, baseCentreOPanel;
	private LabelledVector3DPanel eulerAnglesDegIPanel, eulerAnglesDegOPanel;
	private LabelledDoublePanel heightIPanel, heightOPanel;
	private LabelledDoublePanel baseLensFIPanel, baseLensFOPanel;
//	private LabelledDoublePanel virtualSpaceFractionalLowerInnerVertexHeightIPanel, virtualSpaceFractionalLowerInnerVertexHeightOPanel;
	private JCheckBox showImageOfInnerCloakCheckBox;
	
	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox showPatternedSphereCheckBox, showImage1OfPatternedSphereCheckBox, showImage2OfPatternedSphereCheckBox, makePatternedSpheresShadowThrowingCheckBox;

	private LabelledVector3DPanel patternedSphereCentrePanel;
	private JTextArea patternedSphereCentreInfoTextArea;
	private JButton patternedSphereCentreInfoButton;
	private LabelledDoublePanel patternedSphereRadiusPanel;

	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private JCheckBox movieCheckBox;
	private LabelledVector3DPanel cameraRotationAxisDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;


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

		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		
		// inner Abyss cloak

		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");

		showICheckBox = new JCheckBox("Show");
		showICheckBox.setSelected(showI);
		innerLensCloakPanel.add(showICheckBox, "span");
		
		lensTypeIComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeIComboBox.setSelectedItem(lensTypeI);
		innerLensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeIComboBox), "span");

		showCylindersICheckBox = new JCheckBox("Show cylinder model");
		showCylindersICheckBox.setSelected(showCylindersI);
		innerLensCloakPanel.add(showCylindersICheckBox, "span");
		
		baseCentreIPanel = new LabelledVector3DPanel("Base centre");
		baseCentreIPanel.setVector3D(baseCentreI);
		innerLensCloakPanel.add(baseCentreIPanel, "span");
		
		heightIPanel = new LabelledDoublePanel("Height");
		heightIPanel.setNumber(heightI);
		innerLensCloakPanel.add(heightIPanel, "span");
		
		baseLensFIPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFIPanel.setNumber(baseLensFI);
		innerLensCloakPanel.add(baseLensFIPanel, "span");
		
//		virtualSpaceFractionalLowerInnerVertexHeightIPanel = new LabelledDoublePanel("fractional virtual-space height above base of lower inner vertex");
//		virtualSpaceFractionalLowerInnerVertexHeightIPanel.setNumber(virtualSpaceFractionalLowerInnerVertexHeightI);
//		innerLensCloakPanel.add(virtualSpaceFractionalLowerInnerVertexHeightIPanel, "span");
		
		eulerAnglesDegIPanel = new LabelledVector3DPanel("Euler angles (alpha, beta, gamma)/degrees");
		eulerAnglesDegIPanel.setVector3D(eulerAnglesDegI);
		innerLensCloakPanel.add(eulerAnglesDegIPanel, "span");

		// outer Abyss cloak

		JPanel outerLensCloakPanel = new JPanel();
		outerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		outerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer cloak"));
		cloaksPanel.add(outerLensCloakPanel, "wrap");

		showOCheckBox = new JCheckBox("Show");
		showOCheckBox.setSelected(showO);
		outerLensCloakPanel.add(showOCheckBox, "span");
		
		lensTypeOComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeOComboBox.setSelectedItem(lensTypeO);
		outerLensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeOComboBox), "span");

		showCylindersOCheckBox = new JCheckBox("Show cylinder model");
		showCylindersOCheckBox.setSelected(showCylindersO);
		outerLensCloakPanel.add(showCylindersOCheckBox, "span");
		
		baseCentreOPanel = new LabelledVector3DPanel("Base centre");
		baseCentreOPanel.setVector3D(baseCentreO);
		outerLensCloakPanel.add(baseCentreOPanel, "span");
		
		heightOPanel = new LabelledDoublePanel("Height");
		heightOPanel.setNumber(heightO);
		outerLensCloakPanel.add(heightOPanel, "span");
		
		baseLensFOPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFOPanel.setNumber(baseLensFO);
		outerLensCloakPanel.add(baseLensFOPanel, "span");

//		virtualSpaceFractionalLowerInnerVertexHeightOPanel = new LabelledDoublePanel("fractional virtual-space height above base of lower inner vertex");
//		virtualSpaceFractionalLowerInnerVertexHeightOPanel.setNumber(virtualSpaceFractionalLowerInnerVertexHeightO);
//		outerLensCloakPanel.add(virtualSpaceFractionalLowerInnerVertexHeightOPanel, "span");
		
		eulerAnglesDegOPanel = new LabelledVector3DPanel("Euler angles (alpha, beta, gamma)/degrees");
		eulerAnglesDegOPanel.setVector3D(eulerAnglesDegO);
		outerLensCloakPanel.add(eulerAnglesDegOPanel, "span");
		
		
		showImageOfInnerCloakCheckBox = new JCheckBox("Show (purple) cylinder model of image of inner cloak due to outer cloak");
		showImageOfInnerCloakCheckBox.setSelected(showImageOfInnerCloak);
		cloaksPanel.add(showImageOfInnerCloakCheckBox, "span");
		
		makeCylinderModelsShadowThrowingCheckBox = new JCheckBox("Make cylinder models shadow throwing");
		makeCylinderModelsShadowThrowingCheckBox.setSelected(makeCylinderModelsShadowThrowing);
		cloaksPanel.add(makeCylinderModelsShadowThrowingCheckBox, "span");


		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel patternedSpherePanel = new JPanel();
		patternedSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Patterned sphere"));
		patternedSpherePanel.setLayout(new MigLayout("insets 0"));
		otherObjectsPanel.add(patternedSpherePanel, "span");
		
		patternedSphereCentrePanel = new LabelledVector3DPanel("Centre");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		patternedSpherePanel.add(patternedSphereCentrePanel, "span");

		patternedSphereRadiusPanel = new LabelledDoublePanel("Radius");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		patternedSpherePanel.add(patternedSphereRadiusPanel, "span");

		showPatternedSphereCheckBox = new JCheckBox("Show");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		patternedSpherePanel.add(showPatternedSphereCheckBox, "span");
		
		showImage1OfPatternedSphereCheckBox = new JCheckBox("Show (red) image due to inner cloak");
		showImage1OfPatternedSphereCheckBox.setSelected(showImage1OfPatternedSphere);
		patternedSpherePanel.add(showImage1OfPatternedSphereCheckBox, "span");
		
		showImage2OfPatternedSphereCheckBox = new JCheckBox("Show (purple) image due to both cloaks");
		showImage2OfPatternedSphereCheckBox.setSelected(showImage2OfPatternedSphere);
		patternedSpherePanel.add(showImage2OfPatternedSphereCheckBox, "span");
		
		makePatternedSpheresShadowThrowingCheckBox = new JCheckBox("Make patterned sphere (and its images) shadow-throwing");
		makePatternedSpheresShadowThrowingCheckBox.setSelected(makePatternedSpheresShadowThrowing);
		patternedSpherePanel.add(makePatternedSpheresShadowThrowingCheckBox, "span");
		
		patternedSphereCentreInfoTextArea = new JTextArea(5, 40);
		JScrollPane scrollPane = new JScrollPane(patternedSphereCentreInfoTextArea); 
		patternedSphereCentreInfoTextArea.setEditable(false);
		patternedSphereCentreInfoTextArea.setText("Click on Update button to show info");
		patternedSphereCentreInfoButton = new JButton("Update");
		patternedSphereCentreInfoButton.addActionListener(this);
		patternedSpherePanel.add(GUIBitsAndBobs.makeRow(scrollPane, patternedSphereCentreInfoButton), "span");
		
		
		//
		// camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cameras", cameraPanel);
				
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
				
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");

		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		cameraPanel.add(moviePanel, "span");

		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		cameraRotationAxisDirectionPanel = new LabelledVector3DPanel("Direction of rotation axis");
		cameraRotationAxisDirectionPanel.setVector3D(cameraRotationAxisDirection);
		moviePanel.add(cameraRotationAxisDirectionPanel, "span");
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");

	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		showI = showICheckBox.isSelected();
		lensTypeI = (LensType)(lensTypeIComboBox.getSelectedItem());
		showCylindersI = showCylindersICheckBox.isSelected();
		baseCentreI = baseCentreIPanel.getVector3D();
		heightI = heightIPanel.getNumber();
		baseLensFI = baseLensFIPanel.getNumber();
//		virtualSpaceFractionalLowerInnerVertexHeightI = virtualSpaceFractionalLowerInnerVertexHeightIPanel.getNumber();
		eulerAnglesDegI = eulerAnglesDegIPanel.getVector3D();

		showO = showOCheckBox.isSelected();
		lensTypeO = (LensType)(lensTypeOComboBox.getSelectedItem());
		showCylindersO = showCylindersOCheckBox.isSelected();
		baseCentreO = baseCentreOPanel.getVector3D();
		heightO = heightOPanel.getNumber();
		baseLensFO = baseLensFOPanel.getNumber();
//		virtualSpaceFractionalLowerInnerVertexHeightO = virtualSpaceFractionalLowerInnerVertexHeightOPanel.getNumber();
		eulerAnglesDegO = eulerAnglesDegOPanel.getVector3D();
		
		showImageOfInnerCloak = showImageOfInnerCloakCheckBox.isSelected();
		makeCylinderModelsShadowThrowing = makeCylinderModelsShadowThrowingCheckBox.isSelected();
		
		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		showImage1OfPatternedSphere = showImage1OfPatternedSphereCheckBox.isSelected();
		showImage2OfPatternedSphere = showImage2OfPatternedSphereCheckBox.isSelected();
		makePatternedSpheresShadowThrowing = makePatternedSpheresShadowThrowingCheckBox.isSelected();

		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		
		movie = movieCheckBox.isSelected();
		cameraRotationAxisDirection = cameraRotationAxisDirectionPanel.getVector3D();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();

		frame0CameraViewDirection = cameraViewDirection;
	}
	
	
	
	public boolean isVisible(Vector3D positionOfImageDueToBothCloaks)
	{
		// first get the positions of the vertices of the inner cloak...
		ArrayList<NamedVector3D> abyssCloakIVertices = abyssCloakI.getVertices();
		// ... and calculate the positions of their images due to the outer cloak
		ArrayList<Vector3D> abyssCloakIVertexImages = new ArrayList<Vector3D>(4);
		for(Vector3D vertex:abyssCloakIVertices)
			abyssCloakIVertexImages.add(abyssCloakO.getLensSimplicialComplex().mapToOutside(0, vertex));
		
		for(Vector3D vertex:abyssCloakIVertexImages)
		{
			if(abyssCloakO.getClosestRayIntersection(
					new Ray(positionOfImageDueToBothCloaks, Vector3D.difference(vertex, positionOfImageDueToBothCloaks), 0)
				) != RaySceneObjectIntersection.NO_INTERSECTION) return true;

			if(abyssCloakO.getClosestRayIntersection(
					new Ray(positionOfImageDueToBothCloaks, Vector3D.difference(positionOfImageDueToBothCloaks, vertex), 0)
				) != RaySceneObjectIntersection.NO_INTERSECTION) return true;
		}

		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(patternedSphereCentreInfoButton))
		{
			// get the patterned-sphere centre position from the corresponding panel
			patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
			
			// position of the image of the centre of the sphere due to the inner cloak
			Vector3D p1 = abyssCloakI.getLensSimplicialComplex().mapToOutsideSpace(patternedSphereCentre);

			// position of the image of p1 due to the outer cloak
			Vector3D p2 = abyssCloakO.getLensSimplicialComplex().mapToOutside(0, p1);
			
			// is this image due to both cloaks visible?

			patternedSphereCentreInfoTextArea.setText(
					"Sphere centre in simplex #"+abyssCloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(patternedSphereCentrePanel.getVector3D())+"\n"+
					"Position of image of sphere centre due to inner cloak: "+p1+"\n"+
					"Position of image of sphere centre due to both cloaks: "+p2+"\n"+
					"This image is "+(isVisible(p2)?"visible":"not visible")
				);
//			abyssCloakI.getLensSimplicialComplex().mapToOutside(simplexIndex, position);
//			abyssCloakI.getLensSimplicialComplex().mapToOutsideSpace(position);
//			abyssCloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(position);
		}
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
		(new NestedAbyssCloakExplorer()).run();
	}
}
