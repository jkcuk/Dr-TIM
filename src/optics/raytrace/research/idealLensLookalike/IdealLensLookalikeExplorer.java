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
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.IdealLensLookalike;
import optics.raytrace.sceneObjects.IdealLensLookalike.InitOrder;
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
	private boolean showIdealLensLookalike;
	
	private boolean showSides;
	
	private boolean showIdealLens;
	
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
	


	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public IdealLensLookalikeExplorer()
	{
		super();
		
		showIdealLensLookalike = true;
		showSides = true;
		showIdealLens = false;
		p1 = new Vector3D(-1, 0, -5);
		p2 = new Vector3D(0, 0, 5);
		n = 1.5;
		material =
				// Material.COLOURED;
				Material.REFRACTIVE;
		d1 = 1;
		d2 = 1;
		iMax = 200;	// 0;
		jMax = 200;	// 0;
		initOrder = InitOrder.ITHENJ;
		
		// other scene objects
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop

		
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
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);
		printStream.println("showIdealLensLookalike = "+showIdealLensLookalike);
		printStream.println("showIdealLens = "+showIdealLens);
		printStream.println("p1 = "+p1);
		printStream.println("p2 = "+p2);
		printStream.println("n = "+n);
		printStream.println("material = "+material);
		printStream.println("d1 = "+d1);
		printStream.println("d2 = "+d2);
		printStream.println("iMax = "+iMax);
		printStream.println("jMax = "+jMax);
		printStream.println("initOrder = "+initOrder);
		
		//
		// the rest of the scene
		//
		
		printStream.println("studioInitialisation = "+studioInitialisation);

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
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		// ... and then adding scene objects to scene
		
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

		// start somewhere in the middle
		int i0 = (int)(0.5*iMax);
		int j0 = (int)(0.5*jMax);

		IdealLensLookalike ill = new IdealLensLookalike(
				"Ideal-lens lookalike",	// description
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
		scene.addSceneObject(ill, showIdealLensLookalike);
		
		EditableScaledParametrisedDisc il;
		try {
			il = new EditableScaledParametrisedDisc(
					"Ideal lens",	// description
					new Vector3D(0, 0, 0),	// centre
					new Vector3D(0, 0, 1),	// normal
					1,	// radius
					new IdealThinLensSurfaceSimple(
							Geometry.linePlaneIntersection(
									p1,	// pointOnLine
									Vector3D.difference(p2, p1),	// directionOfLine
									Vector3D.O,	// pointOnPlane
									Vector3D.Z	// normalToPlane
								),	// lensCentre
							Vector3D.Z,	// opticalAxisDirection
							1./(-1./p1.z + 1./p2.z),	// focalLength; 1/f = -1/o + 1/i
							SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
							true	// shadowThrowing
					),	// surfaceProperty
					scene,	// parent
					studio
			);
			scene.addSceneObject(il, showIdealLens);
		} catch (MathException e) {
			// couldn't create ideal lens
			e.printStackTrace();
		}


	}

	
	
	//
	// for interactive version
	//

	// ideal-lens-lookalike panel
	private JCheckBox showIdealLensLookalikeCheckbox, showSidesCheckbox, showIdealLensCheckbox;
	private JComboBox<Material> materialComboBox;
	private LabelledDoublePanel nPanel, d1Panel, d2Panel;
	private LabelledVector3DPanel p1Panel, p2Panel;
	private LabelledIntPanel iMaxPanel, jMaxPanel;
	private JComboBox<InitOrder> initOrderComboBox;

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	// private JButton setCameraViewCentreToCloakCentroidButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private JButton cameraToP1Button;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;


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

		// ideal-lens-lookalike panel
		
		JPanel idealLensLookalikePanel = new JPanel();
		idealLensLookalikePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Ideal-lens lookalike", idealLensLookalikePanel);
		
		p1Panel = new LabelledVector3DPanel("P1");
		p1Panel.setVector3D(p1);
		idealLensLookalikePanel.add(p1Panel, "span");

		p2Panel = new LabelledVector3DPanel("P2");
		p2Panel.setVector3D(p2);
		idealLensLookalikePanel.add(p2Panel, "span");

		showIdealLensCheckbox = new JCheckBox("Show ideal lens");
		showIdealLensCheckbox.setSelected(showIdealLens);
		idealLensLookalikePanel.add(showIdealLensCheckbox, "span");

		showIdealLensLookalikeCheckbox = new JCheckBox("Show ideal-lens lookalike");
		showIdealLensLookalikeCheckbox.setSelected(showIdealLensLookalike);
		idealLensLookalikePanel.add(showIdealLensLookalikeCheckbox, "span");

		materialComboBox = new JComboBox<Material>(Material.values());
		materialComboBox.setSelectedItem(material);
		idealLensLookalikePanel.add(GUIBitsAndBobs.makeRow("Material", materialComboBox), "span");
		
		showSidesCheckbox = new JCheckBox("Show sides of ideal-lens lookalike");
		showSidesCheckbox.setSelected(showSides);
		idealLensLookalikePanel.add(showSidesCheckbox, "span");

		nPanel = new LabelledDoublePanel("Refractive index");
		nPanel.setNumber(n);
		idealLensLookalikePanel.add(nPanel, "span");
		
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
		
		initOrderComboBox = new JComboBox<InitOrder>(InitOrder.values());
		initOrderComboBox.setSelectedItem(initOrder);
		idealLensLookalikePanel.add(GUIBitsAndBobs.makeRow("Initialisation order", initOrderComboBox), "span");
		
		
		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		
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

	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		// showVertices = showVerticesCheckbox.isSelected();
		p1 = p1Panel.getVector3D();
		p2 = p2Panel.getVector3D();
		showIdealLens = showIdealLensCheckbox.isSelected();
		showIdealLensLookalike = showIdealLensLookalikeCheckbox.isSelected();
		material = (Material)(materialComboBox.getSelectedItem());
		showSides = showSidesCheckbox.isSelected();
		n = nPanel.getNumber();
		d1 = d1Panel.getNumber();
		d2 = d2Panel.getNumber();
		iMax = iMaxPanel.getNumber();
		jMax = jMaxPanel.getNumber();
		initOrder = (InitOrder)(initOrderComboBox.getSelectedItem());
		
		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());

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
