package optics.raytrace.research.idealLensLookalike;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.AdamsIdealLensLookalike;
import optics.raytrace.sceneObjects.IdealLensLookalike;
import optics.raytrace.sceneObjects.ScaledParametrisedCentredParallelogram;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.IdealLensLookalike.InitOrder;
import optics.raytrace.sceneObjects.LensSurface;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;


/**
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class IdealLensLookalikeExplorer extends NonInteractiveTIMEngine
{
	private boolean showJsIdealLensLookalike;
	private boolean showAsIdealLensLookalike;
	private double lookalikeLensSize;
	
	private boolean showSides;
	
	private boolean setFocalLength;
	private double idealLensFocalLength;
	private double idealLensSize;
	
	private boolean setFocalPointsManually;
	private Vector3D frontFocalPoint, backFocalPoint;
	private double frontFocalLength, backFocalLength;
	
	
	
	/**
	 * the first of the two conjugate positions
	 */
	private Vector3D p1;
	
	/**
	 * the second of the two conjugate positions
	 */
	private Vector3D p2;

	private int iMax, jMax;
	
	/**
	 * The integration step size to be used
	 */
	private double integrationStepSize;
	
	/**
	 * the distance from point (i0, j0) on the ideal lens to the corresponding point on surface 1
	 */
	private double d1;
	
	/**
	 * the distance from point (i0, j0) on the ideal lens to the corresponding point on surface 2
	 */
	private double d2;

	/**
	 * refractive index of the material
	 */
	private double n;
	
	public enum OpticalElement{
		IDEALLENS("ideal thin lens"),
		LOOKALIKE("look a like lens"),
		LENSSURFACE("lens surface"),
		EMPTY("No element");
		
		private String description;
		private OpticalElement(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	private OpticalElement opticalElement;
	
	
	public enum Material {
		REFRACTIVE("Refractive"), COLOURED("Coloured"), TINTED("Tinted");
		
		private String description;
		private Material(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	public Material material;

	public InitOrder initOrder;

	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	private boolean addObjectAtp2;
	


	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public IdealLensLookalikeExplorer()
	{
		super();
		
		opticalElement = OpticalElement.EMPTY;
		
		showAsIdealLensLookalike = false;
		showJsIdealLensLookalike = false;
		setFocalLength = false;
		setFocalPointsManually = false;
		
		frontFocalPoint = new Vector3D(0, 0, -5);
		backFocalPoint = new Vector3D(0, 0, 5); 
		frontFocalLength = 1;
		backFocalLength = 0.5;
		integrationStepSize = 0.01;
		
		showSides = true;
		lookalikeLensSize = 2;
		p1 = new Vector3D(0, 0, -5);
		p2 = new Vector3D(0, 0, 5);
		n = 1.5;
		material =
				// Material.COLOURED;
				Material.REFRACTIVE;
		d1 = 1;
		d2 = 1;
		iMax = 50;	// 0;
		jMax = 50;	// 0;
		initOrder = InitOrder.ITHENJ;
		
		idealLensFocalLength = 1;
		idealLensSize = 2;
		
		// other scene objects
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop
		addObjectAtp2 = false;

		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// camera parameters are set in createStudio()
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = 
				// new Vector3D(3, -4, 5);
				Vector3D.difference(cameraViewCentre, p1);
		cameraDistance = cameraViewDirection.getLength();	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 30;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;
		
		windowTitle = "Dr TIM's ideal-lens-lookalike explorer";
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
				"IdealLensLookalikeExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this: TODO
		// printStream.println("parameterName = "+parameterName);
		printStream.println("p1 = "+p1);
		printStream.println("p2 = "+p2);
		printStream.println("n = "+n);
		printStream.println("opticalElement = "+opticalElement.description);
		switch(opticalElement) {
		case IDEALLENS:
			printStream.println("setFocalLength = "+setFocalLength);
			printStream.println("idealLensSize = "+idealLensSize);	
			break;
		case LOOKALIKE:
			printStream.println("showIdealLensLookalike = "+showJsIdealLensLookalike);
			printStream.println("showIdealLensLookalike = "+showAsIdealLensLookalike);
			printStream.println("material = "+material);
			printStream.println("d1 = "+d1);
			printStream.println("d2 = "+d2);
			printStream.println("lookalikeLensSize = "+lookalikeLensSize);
			printStream.println("iMax = "+iMax);
			printStream.println("jMax = "+jMax);
			printStream.println("initOrder = "+initOrder);
			printStream.println("showSides = "+showSides);
			printStream.println("integrationStepSize = "+integrationStepSize);		
			break;
		case LENSSURFACE:
			printStream.println("setFocalPointsManually = "+setFocalPointsManually);			
			printStream.println("ocularFocalPoint = "+frontFocalPoint);
			printStream.println("ocularFocalLength = "+frontFocalLength);	
			printStream.println("objectiveFocalPoint = "+backFocalPoint);
			printStream.println("objectiveFocalLength = "+backFocalLength);	
			break;
		case EMPTY:
			break;		
		}
		
		//
		// the rest of the scene
		//
		
		printStream.println("studioInitialisation = "+studioInitialisation);
		printStream.println("addObjectAtp2 = "+addObjectAtp2);

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
		// the studio
		studio = new Studio();
		
		studio.setCamera(getStandardCamera());

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		if(addObjectAtp2) {
			studioInitialisation = StudioInitialisationType.MINIMALIST;
		}
		
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// ... and then adding scene objects to scene
		if(addObjectAtp2) {
			scene.addSceneObject(new Sphere(
					"p2 object",
					p2,//centre
					0.01,//radius should be small
					SurfaceColour.GREEN_MATT,
					scene,
					studio
					));
		}

		
		Vector3D[][] pointsOnIdealLens = new Vector3D[iMax][jMax];
		for(int i=0; i<iMax; i++)
			for(int j=0; j<jMax; j++)
				pointsOnIdealLens[i][j] = new Vector3D(
						-1 + 2*((double)i)/(iMax-1),
						-1 + 2*((double)j)/(jMax-1),
						0
					);

		SurfaceProperty surfaceProperty1, surfaceProperty2, surfacePropertySides;
		switch(material)
		{
		case REFRACTIVE:
			surfaceProperty1 = surfaceProperty2 = surfacePropertySides = new RefractiveSimple(
					1/n,	// insideOutsideRefractiveIndexRatio
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case TINTED:
			surfaceProperty1 = surfaceProperty2 = surfacePropertySides = new SurfaceOfTintedSolid(
					0,	// redAbsorptionCoefficient
					1.,	// greenAbsorptionCoefficient
					1.,	// blueAbsorptionCoefficient
					true	// shadowThrowing
				);
			break;
		case COLOURED:
		default:
			surfaceProperty1 = SurfaceColour.CYAN_SHINY;
			surfaceProperty2 = SurfaceColour.GREEN_SHINY;
			surfacePropertySides = SurfaceColour.YELLOW_SHINY;
		}

		/*
		 * Adding the scene Objects...
		 */
			
		switch(opticalElement){
		case IDEALLENS:
			ScaledParametrisedCentredParallelogram il;
			if(!setFocalLength)idealLensFocalLength = 1./(-1./p1.z + 1./p2.z);							
			try {
				il = new ScaledParametrisedCentredParallelogram(
						"Ideal lens",	// description
						new Vector3D(0, 0, 0),	// centre
						new Vector3D(idealLensSize, 0, 0),	// spanVector1
						new Vector3D(0, idealLensSize, 0),	// spanVector2
						// size
						new IdealThinLensSurfaceSimple(
								Geometry.linePlaneIntersection(
										p1,	// pointOnLine
										Vector3D.difference(p2, p1),	// directionOfLine
										Vector3D.O,	// pointOnPlane
										Vector3D.Z	// normalToPlane
									),	// lensCentre
								Vector3D.Z,	// opticalAxisDirection
								idealLensFocalLength,	// focalLength; 1/f = -1/o + 1/i
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								true	// shadowThrowing
						),	// surfaceProperty
						scene,	// parent
						studio
				);
				scene.addSceneObject(il);
			} catch (MathException e) {
				// couldn't create ideal lens
				e.printStackTrace();
			}	
		break;
		case LOOKALIKE:
			// start somewhere in the middle
			int i0 = (int)(0.5*iMax);
			int j0 = (int)(0.5*jMax);

			IdealLensLookalike illJ = new IdealLensLookalike(
					"Johannes's Ideal-lens lookalike",	// description
					p1,
					p2,
					d1,
					d2,
					i0,
					j0,
					surfaceProperty1,
					surfaceProperty2,
					showSides?surfacePropertySides:null,
					n,
					pointsOnIdealLens,
					initOrder,
					scene,	// parent
					studio
				);
			scene.addSceneObject(illJ, showJsIdealLensLookalike);
			
			AdamsIdealLensLookalike illA = new  AdamsIdealLensLookalike(
					"Adam's ideal lens lookalike",// description,
					p1,// p,
					p2,// q,
					d1,// dp,
					d2,// dq,
					Vector3D.O,// pI,
					Vector3D.Z,// idealLensNormal,
					lookalikeLensSize,// height,
					lookalikeLensSize,// width,
					iMax,// iSteps,
					jMax,// jSteps,
					integrationStepSize,// integrationStepSize
					n,// n,
					surfaceProperty1,// surfaceProperty1,
					surfaceProperty2,// surfaceProperty2,
					showSides?surfacePropertySides:null,// surfacePropertySides,
					scene,// parent,
					studio// studio
					);
			
			scene.addSceneObject(illA, showAsIdealLensLookalike);
		break;
		case LENSSURFACE:
			LensSurface frontSurface, backSurface;
			
			if(!setFocalPointsManually) {frontFocalPoint = p1; backFocalPoint = p2;};
			
			frontSurface = new LensSurface(
					"front lens surface", //description,
					frontFocalPoint,// focalPoint,
					frontFocalLength,// focalLength,
					n,// refractiveIndex,
					Vector3D.difference(p1, p2).getNormalised(),//
					//Vector3D.Z.getProductWith(-1),// opticalAxisDirectionOutwards,
					0.95,// transmissionCoefficient,
					false,// shadowThrowing,
					scene,// parent,
					studio// studio
			);
			
			backSurface = new LensSurface(
					"back lens surface", //description,
					backFocalPoint,// focalPoint,
					backFocalLength,// focalLength,
					n,// refractiveIndex,
					Vector3D.difference(p2, p1).getNormalised(),//
					//Vector3D.Z,// opticalAxisDirectionOutwards,
					0.95,// transmissionCoefficient,
					false,// shadowThrowing,
					scene,// parent,
					studio// studio
			);
			scene.addSceneObject(frontSurface);
			scene.addSceneObject(backSurface);
			
		break;
		
		case EMPTY:
		default:
		break;	
		}
	}

	
	
	//
	// for interactive version
	//

	//ideal lens panel
	private JCheckBox setFocalLengthCheckbox;
	private LabelledDoublePanel idealLensFocalLengthPanel, idealLensSizePanel;
	

	// ideal-lens-lookalike panel
	private JCheckBox showJsIdealLensLookalikeCheckbox, showAsIdealLensLookalikeCheckbox, showSidesCheckbox;
	private JComboBox<Material> materialComboBox;
	private LabelledDoublePanel nPanel, d1Panel, d2Panel, lookalikeLensSizePanel, integrationStepSizePanel;
	private LabelledVector3DPanel p1Panel, p2Panel;
	private LabelledIntPanel iMaxPanel, jMaxPanel;
	private JComboBox<InitOrder> initOrderComboBox;
	
	//lens surface panel
	private JCheckBox setFocalPointsManuallyCheckBox;
	private LabelledVector3DPanel frontFocalPointPanel, backFocalPointPanel;
	private LabelledDoublePanel frontFocalLengthPanel, backFocalLengthPanel; 
	

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox addObjectAtp2CheckBox;

	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private JButton cameraToP1Button;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	
	JTabbedPane lensTabbedPane;


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
		
		
		
		// ideal lens panel
		
		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lenses", lensPanel);
		
		p1Panel = new LabelledVector3DPanel("P1");
		p1Panel.setVector3D(p1);
		lensPanel.add(p1Panel, "span");

		p2Panel = new LabelledVector3DPanel("P2");
		p2Panel.setVector3D(p2);
		lensPanel.add(p2Panel, "span");
		
		nPanel = new LabelledDoublePanel("Refractive index");
		nPanel.setNumber(n);
		lensPanel.add(nPanel, "span");
		
		
		lensTabbedPane = new JTabbedPane();
		lensPanel.add(lensTabbedPane,"span");
		
		
		JPanel idealLensPanel = new JPanel();
		idealLensPanel.setLayout(new MigLayout("insets 0"));
				
		idealLensFocalLengthPanel = new LabelledDoublePanel("focal length");
		idealLensFocalLengthPanel.setNumber(idealLensFocalLength);
		idealLensPanel.add(idealLensFocalLengthPanel);
		
		setFocalLengthCheckbox = new JCheckBox("Set the focal length manually");
		setFocalLengthCheckbox.setSelected(setFocalLength);
		idealLensPanel.add(setFocalLengthCheckbox, "span");
		
		idealLensSizePanel = new LabelledDoublePanel("Height & Width");
		idealLensSizePanel.setNumber(idealLensSize);
		idealLensPanel.add(idealLensSizePanel, "span");

		lensTabbedPane.addTab("Ideal-lens", idealLensPanel);
		
		// ideal-lens-lookalike panel
		
		JPanel idealLensLookalikePanel = new JPanel();
		idealLensLookalikePanel.setLayout(new MigLayout("insets 0"));

		showAsIdealLensLookalikeCheckbox = new JCheckBox("Show Adam's ideal-lens lookalike");
		showAsIdealLensLookalikeCheckbox.setSelected(showAsIdealLensLookalike);
		idealLensLookalikePanel.add(showAsIdealLensLookalikeCheckbox, "span");
		
		showJsIdealLensLookalikeCheckbox = new JCheckBox("Show Johannes's ideal-lens lookalike");
		showJsIdealLensLookalikeCheckbox.setSelected(showJsIdealLensLookalike);
		idealLensLookalikePanel.add(showJsIdealLensLookalikeCheckbox, "span");

		materialComboBox = new JComboBox<Material>(Material.values());
		materialComboBox.setSelectedItem(material);
		idealLensLookalikePanel.add(GUIBitsAndBobs.makeRow("Material", materialComboBox), "span");
		
		showSidesCheckbox = new JCheckBox("Show sides of ideal-lens lookalike");
		showSidesCheckbox.setSelected(showSides);
		idealLensLookalikePanel.add(showSidesCheckbox, "span");
		
		lookalikeLensSizePanel = new LabelledDoublePanel("Height & Width");
		lookalikeLensSizePanel.setNumber(lookalikeLensSize);
		idealLensLookalikePanel.add(lookalikeLensSizePanel, "span");
		
		d1Panel = new LabelledDoublePanel("d1");
		d1Panel.setNumber(d1);
		idealLensLookalikePanel.add(d1Panel, "span");

		d2Panel = new LabelledDoublePanel("d2");
		d2Panel.setNumber(d2);
		idealLensLookalikePanel.add(d2Panel, "span");

		iMaxPanel = new LabelledIntPanel("# vertices in i direction");
		iMaxPanel.setNumber(iMax);
		idealLensLookalikePanel.add(iMaxPanel, "span");

		jMaxPanel = new LabelledIntPanel("# vertices in j direction");
		jMaxPanel.setNumber(jMax);
		idealLensLookalikePanel.add(jMaxPanel, "span");
		
		integrationStepSizePanel = new LabelledDoublePanel("Integration step size");
		integrationStepSizePanel.setNumber(integrationStepSize);
		idealLensLookalikePanel.add(integrationStepSizePanel, "span");
		
		initOrderComboBox = new JComboBox<InitOrder>(InitOrder.values());
		initOrderComboBox.setSelectedItem(initOrder);
		idealLensLookalikePanel.add(GUIBitsAndBobs.makeRow("Initialisation order", initOrderComboBox), "span");
		
		lensTabbedPane.addTab("Ideal-lens lookalike", idealLensLookalikePanel);
		
		//lens surface panel
		JPanel lensSurfacePanel = new JPanel();
		lensSurfacePanel.setLayout(new MigLayout("insets 0"));

		
		setFocalPointsManuallyCheckBox = new JCheckBox("set focal points manually");
		setFocalPointsManuallyCheckBox.setSelected(setFocalPointsManually);
		lensSurfacePanel.add(setFocalPointsManuallyCheckBox, "span");
		
		frontFocalPointPanel = new LabelledVector3DPanel("ocular focal point");
		frontFocalPointPanel.setVector3D(frontFocalPoint);
		lensSurfacePanel.add(frontFocalPointPanel, "span");
		
		backFocalPointPanel = new LabelledVector3DPanel("objective focal point");
		backFocalPointPanel.setVector3D(backFocalPoint);
		lensSurfacePanel.add(backFocalPointPanel, "span");
		
		frontFocalLengthPanel = new LabelledDoublePanel("ocular focal length");
		frontFocalLengthPanel.setNumber(frontFocalLength);
		lensSurfacePanel.add(frontFocalLengthPanel, "span");
		
		backFocalLengthPanel = new LabelledDoublePanel("objective focal length");
		backFocalLengthPanel.setNumber(backFocalLength);
		lensSurfacePanel.add(backFocalLengthPanel, "span");
		
		lensTabbedPane.addTab("Lens Surface", lensSurfacePanel);
		
		//
		// Other scene-objects panel
		//

		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera and Backdrop", cameraPanel);
				
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
		
		cameraToP1Button = new JButton("Place camera at P1");
		cameraToP1Button.addActionListener(this);
		cameraPanel.add(cameraToP1Button, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel, "span");
		
		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		addObjectAtp2CheckBox = new JCheckBox("Sphere at p2");
		addObjectAtp2CheckBox.setSelected(addObjectAtp2);
		cameraPanel.add(addObjectAtp2CheckBox, "span");

	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		switch(lensTabbedPane.getSelectedIndex())
		{
		case 0:
			opticalElement = OpticalElement.IDEALLENS;
			break;
		case 1:
			opticalElement = OpticalElement.LOOKALIKE;
			break;
		case 2:
			opticalElement = OpticalElement.LENSSURFACE;
			break;
		}
		// lookalike lenses
		// showVertices = showVerticesCheckbox.isSelected();
		p1 = p1Panel.getVector3D();
		p2 = p2Panel.getVector3D();
		showJsIdealLensLookalike = showJsIdealLensLookalikeCheckbox.isSelected();
		showAsIdealLensLookalike = showAsIdealLensLookalikeCheckbox.isSelected();
		lookalikeLensSize = lookalikeLensSizePanel.getNumber();
		material = (Material)(materialComboBox.getSelectedItem());
		showSides = showSidesCheckbox.isSelected();
		n = nPanel.getNumber();
		d1 = d1Panel.getNumber();
		d2 = d2Panel.getNumber();
		iMax = iMaxPanel.getNumber();
		jMax = jMaxPanel.getNumber();
		integrationStepSize = integrationStepSizePanel.getNumber();
		initOrder = (InitOrder)(initOrderComboBox.getSelectedItem());
		
		//lens surface
		setFocalPointsManually = setFocalPointsManuallyCheckBox.isSelected();
		frontFocalPoint = frontFocalPointPanel.getVector3D();
		backFocalPoint = backFocalPointPanel.getVector3D();
		frontFocalLength = frontFocalLengthPanel.getNumber();
		backFocalLength = backFocalLengthPanel.getNumber(); 
		
		//ideal lens
		idealLensSize = idealLensSizePanel.getNumber();
		idealLensFocalLength = idealLensFocalLengthPanel.getNumber();
		setFocalLength = setFocalLengthCheckbox.isSelected();
		
		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		addObjectAtp2 = addObjectAtp2CheckBox.isSelected();

		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(cameraToP1Button))
		{
			Vector3D p1ToViewCentre = Vector3D.difference(cameraViewCentrePanel.getVector3D(), p1Panel.getVector3D());
			cameraViewDirectionPanel.setVector3D(p1ToViewCentre);
			cameraDistancePanel.setNumber(p1ToViewCentre.getLength());
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
		(new IdealLensLookalikeExplorer()).run();
	}
}
