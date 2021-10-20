package optics.raytrace.research.pixellation;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.surfaces.*;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import testImages.TestImage;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;


/**
 * Pixellation explorer 2.1
 * Now includes a lens that enables focussing to remove ray-offset effects.
 */
public class PixellationEffectsExplorer extends NonInteractiveTIMEngine
{   
	// a few units
	public static final double CM = 1e-2;
	// public static final double MM = 1e-3;
	// public static final double UM = 1e-6;
	public static final double NM = 1e-9;
	
	private enum PixellatedComponentType
	{
		BLUR_ONLY("Idealised blur only"),
		CLAs("CLAs");
		
		private String description;
		private PixellatedComponentType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * Determines the simulated component type
	 */
	private PixellatedComponentType pixellatedComponentType;

	private enum CLALensType
	{
		IDEAL("Ideal lenses"),
		HOLOGRAPHIC("Holographic lenses"),
		NONE("None");
		
		private String description;
		private CLALensType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * Determines the simulated component type
	 */
	private CLALensType claLensType;

	/**
	 * for CLAs, show the baffles?
	 */
	private boolean showBaffles;

	/**
	 * if true, simulate diffractive blur
	 * (IDEALISED pixellated component only)
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * if true, simulate ray-offset blur
	 * (IDEALISED pixellated component only)
	 */
	private boolean simulateRayOffsetBlur;
	
	/**
	 * focal length of closer lenslet array
	 * (CLAs pixellated component only)
	 */
	private double closerLensletArrayF;

	/**
	 * focal length of farther lenslet array
	 * (CLAs pixellated component only)
	 */
	private double fartherLensletArrayF;
	
	private boolean showCloserLA, showFartherLA;
	
	/**
	 * if true, simulate an ideal thin lens that removes ray-offset blur of images in the lens's focal plane
	 */
	private boolean showPixelChannelingLens;
	
	/**
	 * focal length of the pixel-channeling lens
	 */
	private double pixelChannelingLensF;
	
	/**
	 * distance of the pixel-channeling lens from the camera
	 */
	private double pixelChannelingLensZ;
	
	/**
	 * pixel side length in the x direction, in m
	 */
	private double pixelSideLengthX;
	
	/**
	 * pixel side length in the y direction, in m
	 */
	private double pixelSideLengthY;
	
	/**
	 * wavelength, in m;
	 * determines the size of the diffractive blur
	 * (IDEALISED pixellated component only)
	 */
	private double wavelength;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
	
	/**
	 * distance of the pixellated plane from the camera, in m
	 */
	private double pixellatedPlaneZ;
	
//	private enum InitType
//	{
//		STANDARD("Standard scenes"),
//		CUSTOM("Test image");
//		
//		private String description;
//		private InitType(String description) {this.description = description;}
//		@Override
//		public String toString() {return description;}
//	}
//	
//	private InitType initType;

	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	private boolean showTestImage;
	
	private TestImage testImage;
	
	private double testImageZ;
	
	private double testImageCentreX, testImageCentreY;
	
	private double testImageHeight;
	
	private double focusObjectZ;
	
	private double apertureRadiusFactor;

	private double cameraApertureCentreX;
	private double cameraApertureCentreY;
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PixellationEffectsExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		renderQuality = RenderQualityEnum.STANDARD;
		
//		// experiment initialisation
//		initType = InitType.CUSTOM;
//		studioInitialisation = StudioInitialisationType.DISTANCE_LABELLED_PLANES_1;	// the backdrop
//		testImage = TestImage.USAF_TEST_CHART_Thorlabs;
//		testImageZ = 83*CM;
//		testImageCentreX = -1.5*CM;
//		testImageCentreY = -1.1*CM;
//		testImageHeight = 8*CM;
//		
//		showPixelChannelingLens = true;
//		pixelChannelingLensZ = 56*CM;	// MyMath.TINY;
//		pixelChannelingLensF = testImageZ - pixelChannelingLensZ;
//		
//		// set all parameters
//		pixellatedComponentType = PixellatedComponentType.CLAs;
//		claLensType = CLALensType.IDEAL;
//		
//		pixelSideLengthX = .54*CM;
//		pixelSideLengthY = .7*CM;
//		pixellatedPlaneZ = 30*CM;
//		simulateDiffractiveBlur = true;
//		simulateRayOffsetBlur = true;
//		wavelength = 633*NM;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml
//		closerLensletArrayF = 4*CM;
//		fartherLensletArrayF = 4*CM;
//		showBaffles = true;

		// paper initialisation
		// initType = InitType.CUSTOM;
		studioInitialisation = StudioInitialisationType.DARKNESS;	// the backdrop
		showTestImage = true;
		testImage = TestImage.USAF_TEST_CHART_Thorlabs;
		testImageZ = 100*CM;
		testImageCentreX = 0*CM;
		testImageCentreY = 0*CM;
		testImageHeight = 8*CM;
		
		showPixelChannelingLens = true;
		pixelChannelingLensZ = 2*CM;	// MyMath.TINY;
		pixelChannelingLensF = testImageZ - pixelChannelingLensZ;
		
		// set all parameters
		pixellatedComponentType = PixellatedComponentType.CLAs;
		
		pixelSideLengthX = 1*CM;
		pixelSideLengthY = 1*CM;
		pixellatedPlaneZ = 1*CM;
		simulateRayOffsetBlur = true;
		showFartherLA = true;
		fartherLensletArrayF = 0.1*CM;
		showCloserLA = true;
		closerLensletArrayF = -0.05*CM;
		claLensType = CLALensType.IDEAL;
		showBaffles = true;
		simulateDiffractiveBlur = true;
		wavelength = 633*NM;	// lambda; 564nm is the wavelength at which the human eye is most sensitive -- see http://hypertextbook.com/facts/2007/SusanZhao.shtml

		cameraApertureCentreX = 0;
		cameraApertureCentreY = 0;
		cameraViewDirection = new Vector3D(0, 0, 1);
		// cameraDistance = 10;	// do not change!
		cameraViewCentre = Vector3D.sum(
				new Vector3D(cameraApertureCentreX, cameraApertureCentreY, 0),
				cameraViewDirection.getWithLength(cameraDistance)
			);
		cameraHorizontalFOVDeg = 10;
		cameraApertureSize = ApertureSizeType.TINY;
		apertureRadiusFactor = 1.0;
		cameraFocussingDistance = 10000000*CM;	// focussed on images produced by individual telescopes
		focusObjectZ = testImageZ;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's pixellation-effects explorer 2.1";
			windowWidth = 1400;
			windowHeight = 750;
		}
	}

	
	@Override
	public String getFirstPartOfFilename()
	{
		String s = "PixellationEffectExplorer2";
		
//		// the name of the pixellation component, ...
//		s += " "+pixellatedComponentType.toString();
//		
//		// ... the common parameters, ...
//		s += " pixel sidelengthX="+pixelSideLengthX/CM+"cm"
//			+" pixel sidelengthY="+pixelSideLengthY/CM+"cm"
//			+" pixellatedPlaneDistance="+pixellatedPlaneZ/CM+"cm";
//
//		// ... and the component-specific parameters
//		switch(pixellatedComponentType)
//		{
//		case IDEALISED:
//			s += " ray-offset blur "+(simulateRayOffsetBlur?"on":"off");
//			break;
//		case CLAs:
//			s += " closerLensletArrayF "+closerLensletArrayF/CM+"cm"
//				+" fartherLensletArrayF "+fartherLensletArrayF/CM+"cm"
//				+" CLALensType "+claLensType
//				+" showBaffles = "+showBaffles;
//		}
//		s += " diffractive blur "+(simulateDiffractiveBlur?"on":"off")
//				+" wavelength="+wavelength/NM+"nm";
//
//		
//		// the offset-focussing lens
//		if(showPixelChannelingLens)
//		{
//			s += " pixelChannelingLensF="+pixelChannelingLensF/CM+"cm"
//				+" pixelChannelingLensZ="+pixelChannelingLensZ/CM+"cm";
//		}
//		
//		// and finally the background
//		switch(initType)
//		{
//		case CUSTOM:
//			s += " testImage="+testImage+"@(x,y,z)=("+testImageCentreX+","+testImageCentreY+","+testImageZ+")&height="+testImageHeight;
//			break;
//		case STANDARD:
//			s += " "+studioInitialisation.toString();
//		}
		
		return s;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println(renderQuality+" (Render quality)");

		// printStream.println("*** Scene initialisation*** ");
		printStream.println();
		
//		printStream.println(initType+" (Initialisation type)");
//		switch(initType)
//		{
//		case STANDARD:
			printStream.println("Background "+studioInitialisation);	
			printStream.println();
//		case CUSTOM:
			printStream.println("Test image");
			printStream.println();
			printStream.println("  Show "+showTestImage);
			if(showTestImage)
			{
				printStream.println("  Image "+testImage);
				printStream.println("  Distance from camera "+testImageZ/CM+" cm");
				printStream.println("  Transverse offset ("+testImageCentreX/CM+" cm ,"+testImageCentreY/CM+" cm)");
				printStream.println("  Height = "+testImageHeight/CM+" cm");
			}
//			break;
//		}
		
		printStream.println();
		printStream.println("Pixel-channeling lens");
		printStream.println();
		printStream.println("  Show "+showPixelChannelingLens);
		if(showPixelChannelingLens)
		{
			printStream.println("  Focal length "+pixelChannelingLensF/CM+" cm");
			printStream.println("  Distance from camera "+pixelChannelingLensZ/CM+" cm");
		}
		
		printStream.println();
		printStream.println("Pixellated component");
		printStream.println();

		printStream.println("  Pixel size "+pixelSideLengthX/CM+" cm x"+pixelSideLengthY/CM+" cm");
		printStream.println("  Distance from camera "+pixellatedPlaneZ/CM+" cm");

		printStream.println("  Component type "+pixellatedComponentType);
		switch(pixellatedComponentType)
		{
		case BLUR_ONLY:
			printStream.println("  Simulate ray-offset blur "+simulateRayOffsetBlur);
			break;
		case CLAs:
			printStream.println("  Show farther lenslet array "+showFartherLA+" of focal length "+fartherLensletArrayF/CM+" cm");
			printStream.println("  Show closer lenslet array "+showCloserLA+" of focal length "+closerLensletArrayF/CM+" cm");
			printStream.println("  Lens type "+claLensType);
			printStream.println("  Show baffles "+showBaffles);
		}
		printStream.println("  Simulate diffractive blur "+simulateDiffractiveBlur);
		if(simulateDiffractiveBlur)
			printStream.println("  Wavelength "+wavelength/NM+"nm");	// +" (simulateDiffractiveBlur=true only)");
		
		printStream.println();
		printStream.println("Camera");
		printStream.println();

		printStream.println("  Aperture centre ("+cameraApertureCentreX/CM+" cm, "+cameraApertureCentreY/CM+" cm, 0)");
		printStream.println("  Horizontal FOV "+cameraHorizontalFOVDeg+"°");
		printStream.println("  Camera aperture "+cameraApertureSize+"*"+apertureRadiusFactor);
		printStream.println("  Focussing distance "+cameraFocussingDistance/CM+" cm");
		printStream.println("  [Focus on images due to individual telescopes] or [Focus on integral image] of plane a distance "+focusObjectZ/CM+" cm from camera");
		
		printStream.println("  Camera exposure compensation "+cameraExposureCompensation);
	
		// write all parameters defined in NonInteractiveTIMEngine
		// super.writeParameters(printStream);
	}

	
	@Override
	public void populateStudio()
	throws SceneException
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		studio.setScene(scene);
		
//		switch(initType)
//		{
//		case STANDARD:
			// initialise the scene and lights
			StudioInitialisationType.initialiseSceneAndLights(
					studioInitialisation,
					scene,
					studio
					);
//			break;
//		case CUSTOM:
//		default:
//			// a white background...
//			StudioInitialisationType.initialiseSceneAndLights(
//					StudioInitialisationType.HEAVEN,
//					scene,
//					studio
//					);
			
			if(showTestImage)
			{
			// ... and an object in a given z plane
			double testImageWidth = testImageHeight * testImage.getAspectRatio();
			scene.addSceneObject(testImage.getEditableScaledParametrisedCentredParallelogram(
					testImage.toString(), 	// description
					new Vector3D(testImageCentreX, testImageCentreY, testImageZ),	// centre
					new Vector3D(testImageWidth, 0, 0),	// spanVector1
					new Vector3D(0, -testImageHeight, 0),	// spanVector2
					scene,	// parent
					studio));
			}
//		}

		// studio.setLights(LightSource.getStandardLightsFromBehind());
		// cameraViewCentre = new Vector3D(cameraApertureCentreX, cameraApertureCentreY, cameraDistance);
		cameraViewCentre = Vector3D.sum(
				new Vector3D(cameraApertureCentreX, cameraApertureCentreY, 0),	// camera aperture centre
				cameraViewDirection.getWithLength(cameraDistance)
			);
		EditableRelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		camera.setApertureRadius(cameraApertureSize.getApertureRadius()*apertureRadiusFactor);
		studio.setCamera(camera);
		
		//
		// add any other scene objects
		//
		
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
				
		Vector3D planeNormal = new Vector3D(0, 0, 1);
		
		// first, construct the baffles, which can then be added later (if required)
		SceneObjectContainer baffledVolume = new SceneObjectContainer("baffled volume", scene, studio);
		// the set of planes that define the voxels
		Vector3D pointOnPlanes = new Vector3D(0.5*pixelSideLengthX, 0.5*pixelSideLengthY, 0);
		SetOfEquidistantParallelPlanes[] planeSets = new SetOfEquidistantParallelPlanes[2];
		planeSets[0] = new SetOfEquidistantParallelPlanes(
				pointOnPlanes,	// point on 0th plane
				Vector3D.X,	// normal to surfaces
				pixelSideLengthX
			);
		planeSets[1] = new SetOfEquidistantParallelPlanes(
				pointOnPlanes,	// point on 0th plane
				Vector3D.Y,	// normal to surfaces
				pixelSideLengthY
			);
		
		SurfaceOfVolumeWithColouredVoxelBoundaries surfaceOfBaffledVolume = new SurfaceOfVolumeWithColouredVoxelBoundaries(
				planeSets,	// the sets of parallel planes defining the voxels
				baffledVolume,	// (SceneObject) new Refractive(0,0), the object
				DoubleColour.BLACK,
				700,	// maxSteps
				1,	// transmission coefficient
				false	// shadow-throwing
			);

		EditableParametrisedPlane fartherBaffledVolumeBoundary = new EditableParametrisedPlane(
				"farther boundary of baffled volume",	// description
				new Vector3D(0, 0, pixellatedPlaneZ+fartherLensletArrayF-MyMath.TINY),	// pointOnPlane
				planeNormal,	// normal
				surfaceOfBaffledVolume,	// surfaceProperty
				baffledVolume,
				studio
				);
		baffledVolume.addSceneObject(fartherBaffledVolumeBoundary);
		EditableParametrisedPlane closerBaffledVolumeBoundary = new EditableParametrisedPlane(
				"closer boundary of baffled volume",	// description
				new Vector3D(0, 0, pixellatedPlaneZ-closerLensletArrayF+MyMath.TINY),	// pointOnPlane
				planeNormal.getReverse(),	// normal; reverse planeNormal to make it point outwards
				surfaceOfBaffledVolume,	// surfaceProperty
				baffledVolume,
				studio
				);
		baffledVolume.addSceneObject(closerBaffledVolumeBoundary);

		switch(pixellatedComponentType)
		{
		case BLUR_ONLY:
			scene.addSceneObject(new EditableParametrisedPlane(
					"pixellated plane",	// description
					new Vector3D(0, 0, pixellatedPlaneZ),	// pointOnPlane
					Vector3D.X, Vector3D.Y,	// planeNormal,	// normal
					new Pixellation(
							pixelSideLengthX,
							pixelSideLengthY,	// pixelSideLength
							wavelength,		// lambda
							simulateDiffractiveBlur,
							simulateRayOffsetBlur
							) ,
					//				new GCLAsWithApertures(
					//						new Vector3D(0, 0, 1),	// aHat
					//						new Vector3D(1, 0, 0),	// uHat
					//						new Vector3D(0, 1, 0),	// vHat
					//						1,	// etaU
					//						1,	// etaV
					//						0,	// deltaU
					//						0,	// deltaV
					//						GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,	// basis
					//						0.9,	// constantTransmissionCoefficient
					//						GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
					//						false,	// shadowThrowing
					//						pixelSideLength,	// pixelSideLength
					//						wavelength,	// lambda
					//						simulateDiffractiveBlur,	// simulateDiffractiveBlur
					//						simulateRayOffsetBlur	// simulateRayOffset
					//						),
					scene,
					studio
					));
			break;
		case CLAs:
			SurfaceProperty fartherLASurfaceProperty, closerLASurfaceProperty;
			
			switch(claLensType)
			{
			case NONE:
				fartherLASurfaceProperty = Transparent.PERFECT;
				closerLASurfaceProperty = Transparent.PERFECT;
				break;
			case HOLOGRAPHIC:
				Vector3D fartherLensletArrayCentre = new Vector3D(0, 0, pixellatedPlaneZ+fartherLensletArrayF);
				fartherLASurfaceProperty = new PhaseHologramOfRectangularLensletArray(
						fartherLensletArrayCentre,	// centre
						Vector3D.X,	// uHat
						Vector3D.Y,	// vHat
						fartherLensletArrayF,	// focalLength
						pixelSideLengthX,	// uPeriod
						pixelSideLengthY,	// vPeriod
						0,	// uOffset
						0,	// vOffset
						((Math.abs(fartherLensletArrayF) > Math.abs(closerLensletArrayF))?simulateDiffractiveBlur:false),	// simulate diffractive blur here only if this is the array with the larger |f|
						wavelength,
						1,	// throughputCoefficient
						false,	// reflective
						false	// shadowThrowing
					);
				Vector3D closerLensletArrayCentre = new Vector3D(0, 0, pixellatedPlaneZ-closerLensletArrayF);
				closerLASurfaceProperty = new PhaseHologramOfRectangularLensletArray(
						closerLensletArrayCentre,	// centre
						Vector3D.X,	// uHat
						Vector3D.Y,	// vHat
						closerLensletArrayF,	// focalLength
						pixelSideLengthX,	// uPeriod
						pixelSideLengthY,	// vPeriod
						0,	// uOffset
						0,	// vOffset
						((Math.abs(closerLensletArrayF) > Math.abs(fartherLensletArrayF))?simulateDiffractiveBlur:false),	// simulate diffractive blur here only if this is the array with the larger |f|
						wavelength,
						1,	// throughputCoefficient
						false,	// reflective
						false	// shadowThrowing
					);
				break;
			case IDEAL:
			default:
				fartherLASurfaceProperty = new RectangularIdealThinLensletArray(
						fartherLensletArrayF,	// focalLength
						pixelSideLengthX,	// xPeriod
						pixelSideLengthY,	// yPeriod
						0,	// xOffset
						0,	// yOffset
						((Math.abs(fartherLensletArrayF) > Math.abs(closerLensletArrayF))?simulateDiffractiveBlur:false),	// simulate diffractive blur here only if this is the array with the larger |f|
						wavelength,
						1,	// transmissionCoefficient
						false	// shadowThrowing
					);
				closerLASurfaceProperty = new RectangularIdealThinLensletArray(
						closerLensletArrayF,	// focalLength
						pixelSideLengthX,	// xPeriod
						pixelSideLengthY,	// yPeriod
						0,	// xOffset
						0,	// yOffset
						((Math.abs(closerLensletArrayF) > Math.abs(fartherLensletArrayF))?simulateDiffractiveBlur:false),	// simulate diffractive blur here only if this is the array with the larger |f|
						wavelength,
						1,	// transmissionCoefficient
						false	// shadowThrowing
					);
			}
			System.out.println("showFartherLA = "+showFartherLA+", showCloserLA = "+showCloserLA);
			
			scene.addSceneObject(new EditableParametrisedPlane(
					"farther lenslet array",	// description
					new Vector3D(0, 0, pixellatedPlaneZ+fartherLensletArrayF),	// pointOnPlane
					Vector3D.X, Vector3D.Y,	// planeNormal,	// normal
					fartherLASurfaceProperty,
					scene,
					studio
					),
				showFartherLA);

			scene.addSceneObject(new EditableParametrisedPlane(
					"closer lenslet array",	// description
					new Vector3D(0, 0, pixellatedPlaneZ-closerLensletArrayF),	// pointOnPlane
					Vector3D.X, Vector3D.Y,	// planeNormal,	// normal
					closerLASurfaceProperty,
					scene,
					studio
					),
				showCloserLA);
			
			scene.addSceneObject(baffledVolume, showBaffles);
		}

		
//		scene.addSceneObject(
//				new EditableThinLens(
//						"pixel-channeling lens",	// description
//						new Vector3D(0, 0, windowDistance+windowPixelChannelingLensDistance),	// centre
//						new Vector3D(0, 0, 1),	// normal
//						0.6*windowWidth,	// radius
//						pixelChannelingLensF,	// focalLength
//						0.9,	// transmissionCoefficient
//						true,	// shadowThrowing
//						scene,	// parent
//						studio
//					),
//				showPixelChannelingLens
//			);
		
		Vector3D pixelChannelingLensPrincipalPoint = new Vector3D(0, 0, pixelChannelingLensZ);

		scene.addSceneObject(
				new EditableParametrisedPlane(
					"pixel-channeling lens",	// description
					pixelChannelingLensPrincipalPoint,	// pointOnPlane
					planeNormal,	// normal
					new IdealThinLensSurfaceSimple(
							pixelChannelingLensPrincipalPoint,	// lensCentre
							planeNormal,	// opticalAxisDirection
							pixelChannelingLensF,	// focalLength
							1,	// transmissionCoefficient
							false	// shadowThrowing
						),	// surfaceProperty
					scene,	// parent
					studio
				),
			showPixelChannelingLens
		);

	}
	
	
	private double calculateImageZ(double objectZ, double lensZ, double f)
	{
		// lens equation: 1/(objectZ - lensZ) - 1/(imageZ - lensZ) = 1/f
		// so 1/(imageZ - lensZ) = 1/(objectZ - lensZ) - 1/f = (f - (objectZ - lensZ)) / (f (objectZ - lensZ))
		// so imageZ - lensZ = (f (objectZ - lensZ)) / (f - (objectZ - lensZ))
		// and finally imageZ = lensZ + (f (objectZ - lensZ)) / (f - (objectZ - lensZ))
		return lensZ + (f*(objectZ - lensZ)) / (f - (objectZ - lensZ));
	}
	
	private double calculateIndividualTelescopeImageZ()
	{
		double z = focusObjectZ;
		
		// if the pixel-channeling lens is present...
		if(showPixelChannelingLens)
		{
			// ... it images this to
			z = calculateImageZ(z, pixelChannelingLensZ, pixelChannelingLensF);
		}
		
		// if the CLAs are present...
		switch(pixellatedComponentType)
		{
		case CLAs:
			// ... the two lenses successively re-image as follows
			z = calculateImageZ(z, pixellatedPlaneZ+fartherLensletArrayF, fartherLensletArrayF);
			z = calculateImageZ(z, pixellatedPlaneZ-closerLensletArrayF, closerLensletArrayF);
			break;
		case BLUR_ONLY:
		default:
		}
		
		if(Double.isNaN(z) || Double.isInfinite(z))
		{
			// System.out.println("NAN! z="+z);
			z=MyMath.HUGE;
		}
		// System.out.println("z="+z);
		return z;
	}
	
	private double calculateIntegralImageZ()
	{
		double z = focusObjectZ;
		
		// if the offset-focussing lens is present...
		if(showPixelChannelingLens)
		{
			// ... it images this to
			z = calculateImageZ(z, pixelChannelingLensZ, pixelChannelingLensF);
		}
		
		// if the CLAs are present...
		switch(pixellatedComponentType)
		{
		case CLAs:
			// ... the CLAs re-image as follows
			double objectDistance = z - (pixellatedPlaneZ+fartherLensletArrayF);
			double imageDistance = objectDistance * closerLensletArrayF / fartherLensletArrayF;
			z = (pixellatedPlaneZ-closerLensletArrayF) - imageDistance;
			break;
		case BLUR_ONLY:
		default:
		}
		
		if(Double.isNaN(z) || Double.isInfinite(z))
		{
			// System.out.println("NAN! z="+z);
			z=MyMath.HUGE;
		}
		// System.out.println("z="+z);
		return z;	
	}
	
	
	//
	// for interactive version
	//
	
	private DoublePanel
		testImageCentreXCMPanel,
		testImageCentreYCMPanel,
		pixelSideLengthXCMPanel,
		pixelSideLengthYCMPanel,
		closerLensletArrayFCMPanel,
		fartherLensletArrayFCMPanel,
		wavelengthNMPanel,
		pixellatedPlaneZCMPanel,
		pixelChannelingLensFPanel,
		pixelChannelingLensZCMPanel,
		testImageZCMPanel,
		testImageHeightCMPanel,
		focusObjectZCMPanel;
	// private Vector2DPanel testImageCentreXYCMPanel;
	private JCheckBox showTestImageCheckBox, simulateDiffractiveBlurCheckBox, simulateRayOffsetBlurCheckBox, showPixelChannelingLensCheckBox, showBafflesCheckBox, showFartherLACheckBox, showCloserLACheckBox;	// , idealCLAsCheckBox;
	private JComboBox<CLALensType> claLensTypeComboBox;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JComboBox<TestImage> testImageComboBox;
	private JTabbedPane pixellatedComponentTabbedPane;	// , sceneTabbedPane;
	
	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel, cameraApertureCentreXPanel, cameraApertureCentreYPanel;
	private LabelledDoublePanel apertureRadiusFactorPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private JComboBox<ExposureCompensationType> cameraExposureCompensationComboBox;
	private DoublePanel cameraFocussingDistanceCMPanel;
	private JButton focusCameraOnIndividualTelescopeImagesButton, focusCameraOnIntegralImageButton;


	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
//		// scene tabbed pane
//		sceneTabbedPane = new JTabbedPane();
//		interactiveControlPanel.add(sceneTabbedPane, "span");
//		
//		// standard scene
//		JPanel standardScenesPanel = new JPanel();
//		standardScenesPanel.setLayout(new MigLayout("insets 0"));
//		sceneTabbedPane.addTab("Standard scenes", standardScenesPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		// studioInitialisationComboBox.addItem(StudioInitialisationType.CUSTOM);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Scene", studioInitialisationComboBox), "span");
		// interactiveControlPanel.add(new JLabel("(choose Heaven for distance-labelled planes)"), "span");

		// test image
		JPanel testImagePanel = new JPanel();
		testImagePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Test image"));
		testImagePanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(testImagePanel, "span");
		// sceneTabbedPane.addTab("Test image", customScenePanel);
		
		showTestImageCheckBox = new JCheckBox("Show");
		showTestImageCheckBox.setSelected(showTestImage);
		testImagePanel.add(showTestImageCheckBox, "wrap");
		
		testImageComboBox = new JComboBox<TestImage>(TestImage.values());
		testImageComboBox.setSelectedItem(testImage);
		testImagePanel.add(GUIBitsAndBobs.makeRow("Test image", testImageComboBox), "span");

		testImageZCMPanel = new DoublePanel();
		testImageZCMPanel.setNumber(testImageZ/CM);
		testImagePanel.add(GUIBitsAndBobs.makeRow("Distance from camera", testImageZCMPanel, " cm"), "span");
		
		testImageCentreXCMPanel = new DoublePanel();
		testImageCentreXCMPanel.setNumber(testImageCentreX/CM);
		testImageCentreYCMPanel = new DoublePanel();
		testImageCentreYCMPanel.setNumber(testImageCentreY/CM);
		testImagePanel.add(GUIBitsAndBobs.makeRow("Transverse offset (", testImageCentreXCMPanel, " cm,", testImageCentreYCMPanel, " cm)"), "span");

//		testImageCentreXYCMPanel = new Vector2DPanel();
//		testImageCentreXYCMPanel.setVector2D(testImageCentreX/CM, testImageCentreY/CM);
//		customScenePanel.add(GUIBitsAndBobs.makeRow("Transverse offset", testImageCentreXYCMPanel, " cm"), "span");

		testImageHeightCMPanel = new DoublePanel();
		testImageHeightCMPanel.setNumber(testImageHeight/CM);
		testImagePanel.add(GUIBitsAndBobs.makeRow("Height", testImageHeightCMPanel, " cm"), "span");
		
//		if(initType == InitType.STANDARD) sceneTabbedPane.setSelectedIndex(0);
//		else sceneTabbedPane.setSelectedIndex(1);
		

		//
		// offset-focussing lens
		//

		JPanel pixelChannelingLensPanel = new JPanel();
		pixelChannelingLensPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Pixel-channeling lens"));
		pixelChannelingLensPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(pixelChannelingLensPanel, "span");

		showPixelChannelingLensCheckBox = new JCheckBox("Show");
		showPixelChannelingLensCheckBox.setSelected(showPixelChannelingLens);
		pixelChannelingLensPanel.add(showPixelChannelingLensCheckBox, "span");

		pixelChannelingLensFPanel = new DoublePanel();
		pixelChannelingLensFPanel.setNumber(pixelChannelingLensF/CM);
		pixelChannelingLensPanel.add(GUIBitsAndBobs.makeRow("Focal length", pixelChannelingLensFPanel, " cm"), "span");
		
		pixelChannelingLensZCMPanel = new DoublePanel();
		pixelChannelingLensZCMPanel.setNumber(pixelChannelingLensZ/CM);
		pixelChannelingLensPanel.add(GUIBitsAndBobs.makeRow("Distance from camera", pixelChannelingLensZCMPanel, " cm"), "span");


		//
		// the pixellated component
		//
		
		JPanel pixellatedComponentPanel = new JPanel();
		pixellatedComponentPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Pixellated component"));
		pixellatedComponentPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(pixellatedComponentPanel, "span");

		// a few panels that are common to all types of pixellated component
		pixelSideLengthXCMPanel = new DoublePanel();
		pixelSideLengthXCMPanel.setNumber(pixelSideLengthX/CM);
		pixelSideLengthYCMPanel = new DoublePanel();
		pixelSideLengthYCMPanel.setNumber(pixelSideLengthY/CM);
		pixellatedComponentPanel.add(GUIBitsAndBobs.makeRow("Pixel size ", pixelSideLengthXCMPanel, " cm x ", pixelSideLengthYCMPanel, " cm"), "span");

		pixellatedPlaneZCMPanel = new DoublePanel();
		pixellatedPlaneZCMPanel.setNumber(pixellatedPlaneZ/CM);
		pixellatedComponentPanel.add(GUIBitsAndBobs.makeRow("Distance from camera", pixellatedPlaneZCMPanel, " cm"), "span");
		

		// pixellated-component tabbed pane
		pixellatedComponentTabbedPane = new JTabbedPane();
		pixellatedComponentPanel.add(pixellatedComponentTabbedPane, "span");
		
		//
		// idealised component
		//
		
		JPanel idealisedPixellatedComponentPanel = new JPanel();
		idealisedPixellatedComponentPanel.setLayout(new MigLayout("insets 0"));
		pixellatedComponentTabbedPane.addTab(PixellatedComponentType.BLUR_ONLY.toString(), idealisedPixellatedComponentPanel);

//		simulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur");
//		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
//		idealisedPixellatedComponentPanel.add(simulateDiffractiveBlurCheckBox, "span");
//
		simulateRayOffsetBlurCheckBox = new JCheckBox("Simulate ray-offset blur");
		simulateRayOffsetBlurCheckBox.setSelected(simulateRayOffsetBlur);
		idealisedPixellatedComponentPanel.add(simulateRayOffsetBlurCheckBox, "span");

//		wavelengthNMPanel = new DoublePanel();
//		wavelengthNMPanel.setNumber(wavelength/NM);
//		idealisedPixellatedComponentPanel.add(GUIBitsAndBobs.makeRow("Wavelength", wavelengthNMPanel, "nm"), "span");

		
		//
		// CLAs
		//
		
		JPanel CLAsPanel = new JPanel();
		CLAsPanel.setLayout(new MigLayout("insets 0"));
		pixellatedComponentTabbedPane.addTab("CLAs", CLAsPanel);

		showFartherLACheckBox = new JCheckBox();
		showFartherLACheckBox.setSelected(showFartherLA);
		fartherLensletArrayFCMPanel = new DoublePanel();
		fartherLensletArrayFCMPanel.setNumber(fartherLensletArrayF/CM);
		CLAsPanel.add(GUIBitsAndBobs.makeRow(showFartherLACheckBox, "Show farther lenslet array of focal length ", fartherLensletArrayFCMPanel, " cm"), "span");

		showCloserLACheckBox = new JCheckBox();
		showCloserLACheckBox.setSelected(showCloserLA);
		closerLensletArrayFCMPanel = new DoublePanel();
		closerLensletArrayFCMPanel.setNumber(closerLensletArrayF/CM);
		CLAsPanel.add(GUIBitsAndBobs.makeRow(showCloserLACheckBox, "Show closer lenslet array of focal length ", closerLensletArrayFCMPanel, " cm"), "span");

		claLensTypeComboBox = new JComboBox<CLALensType>(CLALensType.values());
		claLensTypeComboBox.setSelectedItem(claLensType);
		CLAsPanel.add(GUIBitsAndBobs.makeRow("Lens type", claLensTypeComboBox), "span");

//		idealCLAsCheckBox = new JCheckBox("Ideal lenses? (Otherwise: holographic)");
//		idealCLAsCheckBox.setSelected(simulateRayOffsetBlur);
//		CLAsPanel.add(idealCLAsCheckBox, "span");
		
		showBafflesCheckBox = new JCheckBox("Show baffles");
		showBafflesCheckBox.setSelected(showBaffles);
		CLAsPanel.add(showBafflesCheckBox, "span");

		
		// set pixellated component type
		switch(pixellatedComponentType)
		{
		case BLUR_ONLY:
			pixellatedComponentTabbedPane.setSelectedIndex(0);
			break;
//		case IDEAL_CLAs:
		case CLAs:
		default:
			pixellatedComponentTabbedPane.setSelectedIndex(1);
			// idealCLAsCheckBox.setSelected(true);
//			break;
//		case HOLO_CLAs:
//		default:
//			pixellatedComponentTabbedPane.setSelectedIndex(1);
//			idealCLAsCheckBox.setSelected(false);
		}

		simulateDiffractiveBlurCheckBox = new JCheckBox("Simulate diffractive blur,");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		// pixellatedComponentPanel.add(simulateDiffractiveBlurCheckBox, "span");

		wavelengthNMPanel = new DoublePanel();
		wavelengthNMPanel.setNumber(wavelength/NM);
		pixellatedComponentPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "wavelength", wavelengthNMPanel, "nm"), "span");

		
		// camera stuff
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(cameraPanel, "span");

		//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraApertureCentreXPanel = new DoublePanel();
		cameraApertureCentreXPanel.setNumber(cameraApertureCentreX);
		cameraApertureCentreYPanel = new DoublePanel();
		cameraApertureCentreYPanel.setNumber(cameraApertureCentreY);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Aperture centre (", cameraApertureCentreXPanel, " cm,", cameraApertureCentreYPanel, " cm, 0)"), "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		apertureRadiusFactorPanel = new LabelledDoublePanel("*");
		apertureRadiusFactorPanel.setNumber(apertureRadiusFactor);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox, apertureRadiusFactorPanel), "span");		
		
		cameraFocussingDistanceCMPanel = new DoublePanel(DoublePanel.SCIENTIFIC_PATTERN);
		cameraFocussingDistanceCMPanel.setNumber(cameraFocussingDistance/CM);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance", cameraFocussingDistanceCMPanel, " cm"), "span");
		
		focusCameraOnIndividualTelescopeImagesButton = new JButton("Focus on images due to individual telescopes");
		focusCameraOnIndividualTelescopeImagesButton.addActionListener(this);
		focusCameraOnIntegralImageButton = new JButton("Focus on integral image");
		focusCameraOnIntegralImageButton.addActionListener(this);
		focusObjectZCMPanel = new DoublePanel();
		focusObjectZCMPanel.setNumber(focusObjectZ/CM);
		cameraPanel.add(focusCameraOnIndividualTelescopeImagesButton, "span");
		cameraPanel.add(GUIBitsAndBobs.makeRow("or", focusCameraOnIntegralImageButton), "span");
		cameraPanel.add(GUIBitsAndBobs.makeRow("of plane a distance", focusObjectZCMPanel, " cm from camera"), "span");
		
		cameraExposureCompensationComboBox = new JComboBox<ExposureCompensationType>(ExposureCompensationType.values());
		cameraExposureCompensationComboBox.setSelectedItem(cameraExposureCompensation);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera exposure compensation", cameraExposureCompensationComboBox), "span");		
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showTestImage = showTestImageCheckBox.isSelected();
		testImage = (TestImage)(testImageComboBox.getSelectedItem());
		testImageZ = testImageZCMPanel.getNumber()*CM;
		testImageCentreX = testImageCentreXCMPanel.getNumber()*CM;
		testImageCentreY = testImageCentreYCMPanel.getNumber()*CM;
		testImageHeight = testImageHeightCMPanel.getNumber()*CM;

		showPixelChannelingLens = showPixelChannelingLensCheckBox.isSelected();
		pixelChannelingLensF = pixelChannelingLensFPanel.getNumber()*CM;
		pixelChannelingLensZ = pixelChannelingLensZCMPanel.getNumber()*CM;

		pixelSideLengthX = pixelSideLengthXCMPanel.getNumber()*CM;
		pixelSideLengthY = pixelSideLengthYCMPanel.getNumber()*CM;
		pixellatedPlaneZ = pixellatedPlaneZCMPanel.getNumber()*CM;
		simulateRayOffsetBlur = simulateRayOffsetBlurCheckBox.isSelected();
//		if(sceneTabbedPane.getSelectedIndex() == 0) initType = InitType.STANDARD;
//		else initType = InitType.CUSTOM;
		closerLensletArrayF = closerLensletArrayFCMPanel.getNumber()*CM;
		fartherLensletArrayF = fartherLensletArrayFCMPanel.getNumber()*CM;
		showFartherLA = showFartherLACheckBox.isSelected();
		showCloserLA = showCloserLACheckBox.isSelected();
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		wavelength = wavelengthNMPanel.getNumber()*NM;
		
		// set pixellated component type
		switch(pixellatedComponentTabbedPane.getSelectedIndex())
		{
		case 0:
			pixellatedComponentType = PixellatedComponentType.BLUR_ONLY;
			break;
		case 1:
			pixellatedComponentType = PixellatedComponentType.CLAs;
		}

		claLensType = (CLALensType)(claLensTypeComboBox.getSelectedItem());
//		else
//		{
//			if(idealCLAsCheckBox.isSelected())
//			{
//				pixellatedComponentType = PixellatedComponentType.IDEAL_CLAs;
//			}
//			else
//			{
//				pixellatedComponentType = PixellatedComponentType.HOLO_CLAs;
//			}
//		}
		showBaffles = showBafflesCheckBox.isSelected();
		
		cameraApertureCentreX = cameraApertureCentreXPanel.getNumber()*CM;
		cameraApertureCentreY = cameraApertureCentreYPanel.getNumber()*CM;
		

		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		apertureRadiusFactor = apertureRadiusFactorPanel.getNumber();
		cameraFocussingDistance = cameraFocussingDistanceCMPanel.getNumber()*CM;
		focusObjectZ = focusObjectZCMPanel.getNumber()*CM;
		cameraExposureCompensation = (ExposureCompensationType)(cameraExposureCompensationComboBox.getSelectedItem());
		
		// quick check(s)
		if(pixellatedPlaneZ-closerLensletArrayF > pixellatedPlaneZ+fartherLensletArrayF)
		{
			closerLensletArrayFCMPanel.setBackground(Color.RED);
			fartherLensletArrayFCMPanel.setBackground(Color.RED);
		}
		else
		{
			closerLensletArrayFCMPanel.setBackground(new JTextField().getBackground());
			fartherLensletArrayFCMPanel.setBackground(new JTextField().getBackground());
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
	
		if(e.getSource().equals(focusCameraOnIndividualTelescopeImagesButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			cameraFocussingDistance = calculateIndividualTelescopeImageZ();
			cameraFocussingDistanceCMPanel.setNumber(cameraFocussingDistance/CM);
		}
		else if(e.getSource().equals(focusCameraOnIntegralImageButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			cameraFocussingDistance = calculateIntegralImageZ();
			cameraFocussingDistanceCMPanel.setNumber(cameraFocussingDistance/CM);
		}
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
		(new PixellationEffectsExplorer()).run();
	}
}

