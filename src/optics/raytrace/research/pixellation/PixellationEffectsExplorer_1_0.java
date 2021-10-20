package optics.raytrace.research.pixellation;

import javax.swing.JCheckBox;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableSpaceShiftingPlane;
import optics.raytrace.GUI.sceneObjects.EditableText;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;


/**
 * Effects of pixellation demos for JOSA A manuscript.
 * Note that this is an extended version of the earlier SPIE proceedings article
 * (E. N. Cowie, C. Bourgenot, D. Robertson, and J. Courtial, "Resolution limit of pixellated optical components", Proc. SPIE 9948, 99480N (2016)),
 * which also contained simulations which were calculated in the PixellationEffectsSPIE class.
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 */
public class PixellationEffectsExplorer_1_0 extends NonInteractiveTIMEngine
{   
	// a few units
	public static final double CM = 1e-2;
	public static final double MM = 1e-3;
	public static final double UM = 1e-6;
	public static final double NM = 1e-9;
	
	/**
	 * if true, simulate diffractive blur
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * if true, simulate ray-offset blur
	 */
	private boolean simulateRayOffsetBlur;
	
	/**
	 * pixel side length, in m;
	 * this determines the size of the diffractive blur
	 */
	private double pixelSideLength;
	
	/**
	 * wavelength, in m;
	 * determines the size of the diffractive blur
	 */
	private double wavelength;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
	
	/**
	 * distance of the window in front of the camera, in m
	 */
	private double windowDistance;
	
	private boolean showWindowDistanceLabel;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PixellationEffectsExplorer_1_0()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		simulateDiffractiveBlur = true;
		simulateRayOffsetBlur = true;
		pixelSideLength = 0.25*MM;
		wavelength = 564*NM;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
		windowDistance = 20*CM;
		showWindowDistanceLabel = true;

		renderQuality = RenderQualityEnum.DRAFT;
		
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraDistance = 10;
		cameraViewCentre = cameraViewDirection.getWithLength(cameraDistance);
		cameraHorizontalFOVDeg = 30;
		cameraApertureSize = ApertureSizeType.INFINITESIMAL;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's pixellation-effects explorer";
			windowWidth = 1050;
			windowHeight = 650;
		}
	}

	
	@Override
	public String getFirstPartOfFilename()
	{
		return "PixellationEffectJOSAA"
				+" diffractive blur "+(simulateDiffractiveBlur?"on":"off")
				+" ray-offset blur "+(simulateRayOffsetBlur?"on":"off")
				+" pixel sidelength="+pixelSideLength/MM+"mm"
				+" wavelength="+wavelength/NM+"nm"
				+" windowDistance="+windowDistance/CM+"cm"
				+(showWindowDistanceLabel?" (indicated on window)":"")
				;
	}
	
	private void addZPlaneText(int zInCM, double zShift, SceneObjectContainer scene)
	{
		scene.addSceneObject(EditableText.getCentredEditableText(
				"Text in plane z="+zInCM+" cm",	// description
				"<i>i</i>&thinsp;=&thinsp;"+zInCM+"&thinsp;cm",	// text
				new Vector3D(0, (-0.16*zInCM+1.1)*CM, zInCM*CM-zShift),	// centre
				// new Vector3D(-2*CM, (-0.16*zInCM+0.6)*CM, zInCM*CM-zShift),	// bottomLeftCorner
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				1024,	// fontSize
				"Times",	// fontFamily
				1*CM,	// textHeight
				SurfaceColour.BLACK_SHINY,	// textSurfaceProperty
				scene,
				studio
			));
	}

	@Override
	public void populateStudio()
	throws SceneException
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
		
		// a white background
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));	// the sky


		//
		// add any other scene objects
		//
		
		
		double zShift = -100;
		scene.addSceneObject(new EditableSpaceShiftingPlane(
				"space-shifting plane",	// description
				new Vector3D(0, 0, 100),	// pointOnPlane
				new Vector3D(0, 0, 1),	// normal
				new Vector3D(0, 0, zShift),	// imageSpaceOffset
				scene,	// parent
				studio
			));
		
		
		// something to look at
		
		double cylinderRadius = 0.05*CM;

//		int cylinderLatticeLayersZ = 5;
//		double cylinderLatticeLayerSeparationZ = 4*CM;
//		double cylinderLatticeDepth = (cylinderLatticeLayersZ - 1)*cylinderLatticeLayerSeparationZ;
//		
//		int cylinderLatticeLayersXY = 4;
//		double cylinderLatticeLayerSeparationXY = 0.25*CM;
//		
//		scene.addSceneObject(new EditableCylinderLattice(
//				"cylinder lattice",
//				-cylinderLatticeLayerSeparationXY*0.5*(cylinderLatticeLayersXY-1), cylinderLatticeLayerSeparationXY*0.5*(cylinderLatticeLayersXY-1), cylinderLatticeLayersXY,
//				-cylinderLatticeLayerSeparationXY*0.5*(cylinderLatticeLayersXY-1), cylinderLatticeLayerSeparationXY*0.5*(cylinderLatticeLayersXY-1), cylinderLatticeLayersXY,
//				cylinderLatticeDistance+cylinderRadius+100, cylinderLatticeDistance+cylinderRadius + cylinderLatticeDepth+100, cylinderLatticeLayersZ,
//				cylinderRadius,
//				scene,
//				studio
//		));
		
		addZPlaneText(
				5,	// zInCM
				zShift,
				scene
			);

		addZPlaneText(
				10,	// zInCM
				zShift,
				scene
			);

		addZPlaneText(
				20,	// zInCM
				zShift,
				scene
			);

		addZPlaneText(
				40,	// zInCM
				zShift,
				scene
			);

		addZPlaneText(
				80,	// zInCM
				zShift,
				scene
			);

//		scene.addSceneObject(new EditableScaledParametrisedSphere(
//				String description,
//				Vector3D centre,
//				double radius,
//				Vector3D pole,
//				Vector3D phi0Direction,
//				double sThetaMin, double sThetaMax,
//				double sPhiMin, double sPhiMax,
//				SurfaceProperty surfaceProperty,
//				SceneObject parent, 
//				Studio studio
//				));
		
		double windowWidth = 3.75*CM*windowDistance/(10*CM);

		scene.addSceneObject(new EditableFramedRectangle(
				"pixellated window",
				new Vector3D(-0.5*windowWidth, -0.5*windowWidth, windowDistance),	// corner
				new Vector3D(windowWidth, 0, 0),	// width vector
				new Vector3D(0, windowWidth, 0),	// height vector
				cylinderRadius,	// frame radius
				new GCLAsWithApertures(
						new Vector3D(0, 0, 1),	// aHat
						new Vector3D(1, 0, 0),	// uHat
						new Vector3D(0, 1, 0),	// vHat
						1,	// etaU
						1,	// etaV
						0,	// deltaU
						0,	// deltaV
						GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,	// basis
						0.9,	// constantTransmissionCoefficient
						GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
						false,	// shadowThrowing
						pixelSideLength,	// pixelSideLength
						wavelength,	// lambda
						simulateDiffractiveBlur,	// simulateDiffractiveBlur
						simulateRayOffsetBlur	// simulateRayOffset
						),
				SurfaceColour.RED_SHINY,	// frame surface
				true,	// show frame
				scene,
				studio
				));
		
		if(showWindowDistanceLabel)
		{
			int windowDistanceCM = (int)(windowDistance/CM);
			String componentDistanceLabelText = "<i>c</i>&thinsp;=&thinsp;"+windowDistanceCM+"&thinsp;cm";
			double textHeight = 1*CM*windowDistance/(20*CM);
			double textWidth = EditableText.calculateWidth(
					componentDistanceLabelText,	// text,
					"Times",	// fontFamily
					1024,	// fontSize
					textHeight	// textHeight
					);
			if(textWidth + 2*MM > windowWidth) textHeight *= (windowWidth-2*MM)/textWidth;
			scene.addSceneObject(new EditableText(
					"Component distance label z="+windowDistanceCM+" cm",	// description
					componentDistanceLabelText,	// text
					new Vector3D(-0.46*windowWidth, -0.49*windowWidth, windowDistance-MyMath.TINY),	// bottom left corner
					// new Vector3D(-2*CM, (-0.16*zInCM+0.6)*CM, zInCM*CM-zShift),	// bottomLeftCorner
					new Vector3D(1, 0, 0),	// rightDirection
					new Vector3D(0, 1, 0),	// upDirection
					1024,	// fontSize
					"Times",	// fontFamily
					textHeight,	// textHeight
					SurfaceColour.RED_SHINY,	// textColour
					scene,
					studio
					));
		}

	}
	
	
	//
	// for interactive version
	//
	
	private DoublePanel pixelSideLengthMMPanel, wavelengthNMPanel, windowDistanceCMPanel;
	private JCheckBox simulateDiffractiveBlurCheckBox, simulateRayOffsetBlurCheckBox;

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		simulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		interactiveControlPanel.add(simulateDiffractiveBlurCheckBox, "span");

		simulateRayOffsetBlurCheckBox = new JCheckBox("Simulate ray-offset blur");
		simulateRayOffsetBlurCheckBox.setSelected(simulateRayOffsetBlur);
		interactiveControlPanel.add(simulateRayOffsetBlurCheckBox, "span");

		pixelSideLengthMMPanel = new DoublePanel();
		pixelSideLengthMMPanel.setNumber(pixelSideLength/MM);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Pixel side length", pixelSideLengthMMPanel, "mm"), "span");

		wavelengthNMPanel = new DoublePanel();
		wavelengthNMPanel.setNumber(wavelength/NM);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Wavelength", wavelengthNMPanel, "nm"), "span");

		windowDistanceCMPanel = new DoublePanel();
		windowDistanceCMPanel.setNumber(windowDistance/CM);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Window distance", windowDistanceCMPanel, "cm"), "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		pixelSideLength = pixelSideLengthMMPanel.getNumber()*MM;
		wavelength = wavelengthNMPanel.getNumber()*NM;
		windowDistance = windowDistanceCMPanel.getNumber()*CM;
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		simulateRayOffsetBlur = simulateRayOffsetBlurCheckBox.isSelected();
	}


	//
	// the main method, so that this can be run as a Java application
	//
	
	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new PixellationEffectsExplorer_1_0()).run();
	}
}

