package optics.raytrace.research.GaborSupererLens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.LensType;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.RefractiveLensSurfaces;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GaborSupererRefractiveCLAs;
import optics.raytrace.surfaces.SimpleRefractiveCLAs;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.TextAreaOutputStream;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedUSAFTestChart;
import optics.raytrace.GUI.sceneObjects.EditableLensletArrayForGaborSupererLens;
import optics.raytrace.GUI.sceneObjects.EditableRectangularLens;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate the visual appearance of pairs of lenslet arrays, and the view through them.
 * Lenslet array 0 is closer to the object and located in the plane z=0, lenslet array 1 is closer to the eye.
 * In the same plane as each lenslet array, specifically on the outside of each lenslet array, there can be an additional "big lens".
 * 
 * The maths is described in GaborSupererLensSpecs.pdf.
 * There, we have an unprimed and a primed lenslet array, whose parameters are respectively described by unprimed parameters (e.g. f)
 * and primed parameters (f').
 * The unprimed array is array 0, the primed array is array 1, so
 *   f[0] = f
 *   f[1] = f'
 * etc.
 *  
 * @author Johannes Courtial
 */
public class GaborSupererLensExplorer extends NonInteractiveTIMEngine implements ActionListener
{
	// units
	public static double NM = 1e-9;
	public static double UM = 1e-6;
	public static double MM = 1e-3;
	public static double CM = 1e-2;
	public static double M = 1;
	
	// which lenslet array is which?
	public static int OBJECTIVE = 0;	// index of lenslet array that is closer to object
	public static int OCULAR = 1;	// index of lenslet array that is closer to eye
	
	/**
	 * width of the specs lens
	 */
	private double width;
	
	/**
	 * height of the specs lens
	 */
	private double height;
	
	/**
	 * focal length of LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private double[] f = new double[2];

	
	/**
	 * (x,y) coordinates of centre of the aperture with indices (0, 0) of of LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private Vector2D[] ac00 = new Vector2D[2];
	
	/**
	 * (x,y) coordinates of principal point with indices (0, 0) of of LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private Vector2D[] pp00 = new Vector2D[2];

	/**
	 * period of clear-aperture array of of LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private double[] ppPeriod = new double[2];

	/**
	 * period of clear-aperture array of LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private double[] acPeriod = new double[2];
			
	/**
	 * rotation angle around the z axis of LA1 relative to LA0
	 */
	private double phiDeg;
	
	/**
	 * minimum z separation of LA centres
	 */
	private double minimumZSeparation;

	/**
	 * show LA0 (unprimed, closer to object, located in plane z=0) and LA1 (primed, closer to camera)
	 */
	private boolean[] showLensletArray = new boolean[2];
	
	/**
	 * show big lens in plane of LA0 (i.e. on the object side) and of LA1 (i.e. on the eye of the camera)
	 */
	private boolean[] showBigLens = new boolean[2];
	
	/**
	 * focal length of big lens in plane of LA0 (i.e. on the object side) and of LA1 (i.e. on the eye of the camera)
	 */
	private double[] bigLensF = new double[2];
	
	/**
	 * All lens types, selection for hologram or ideal, and check box for refractive lens
	 */
	private LensType lensType;
	
	private boolean shadowThrowing;
	
	/**
	 * show a field lens in the common focal plane (if the telescopes are "focussed" on infinity)
	 * or in the plane of the image of the plane on which the telescopes are focussed;
	 * this works only if both f[OBJECTIVE] and f[OCULAR] are positive, period[OBJECTIVE] = period[OCULAR], and if phiDeg = 0
	 */
	private boolean showFieldLensArray;
	
	/**
	 * if true, focus on an object plane at z=objectDistance
	 */
	private boolean focusOnObjectZ;
	
	/**
	 * if <focusOnObjectZ>, the camera focusses on an object plane at z=objectDistance
	 */
	private double objectDistance;

	
	/**
	 * if true, diffractive blur will be simulated
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength for which diffractive blur is simulated
	 */
	private double lambda;
	
	//
	//Adding params for the refractive lenses
	//
	private boolean refractiveLens; 
	/**
	 * refractive index of the refractive lenses
	 */
	private double refractiveIndex;
	
	/**
	 * show the two arrays separated by air
	 */
	private boolean separatedArrays;
	/**
	 * thickness of the bounding box
	 */
	private double boundingBoxThickness;
	/**
	 * Centre thicknesses for the lenses in array 1 and 2.
	 */
	private double centreThicknessArray1, centreThicknessArray2;
	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	// private StudioInitialisationWithZAdjustableForegroundType studioInitialisation;
	
	public enum ForegroundType
	{
		LATTICE("Lattice"),
		TIM("Dr TIM"),
		CHART("USAF chart");
		
		private String description;
		private ForegroundType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}

	private ForegroundType foreground;

	/**
	 * z coordinate of foreground object
	 */
	private double foregroundZ;
	
	private double foregroundWidth;
	
	private double foregroundPhiDeg;
	
	private boolean showSphereAtImageOfCamera;
	
	//initialisation
	public enum GaborSupererLensExplorerInitialisationType
	{
		SPECS_DEMAG("Spectacles, de-magnifying"),
		SPECS_MAG("Spectacles, magnifying"),
		WINDOW_DEMAG("Window, de-magnifying"),
		WINDOW_MAG("Window, magnifying");
		
		private String description;
		private GaborSupererLensExplorerInitialisationType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}

	GaborSupererLensExplorerInitialisationType gaborSupererLensExplorerInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public GaborSupererLensExplorer()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		gaborSupererLensExplorerInitialisation = GaborSupererLensExplorerInitialisationType.SPECS_DEMAG;
		
		initParameters();
		
		// specs lens size
		// width = 5*CM;	// now done in initParameters()
		// height = 3*CM;	// now done in initParameters()
		
		// lenslet-array parameters
		// f[OBJECTIVE] = -1*MM;	// now done in initParameters()
		// f[OCULAR] =  5*MM;	// now done in initParameters()

		// centred of clear-aperture arrays
		ac00[OBJECTIVE] = new Vector2D(0, 0);
		ac00[OCULAR] = new Vector2D(0, 0);
		
		// principal point with indices (0, 0) of the LAs
		pp00[OBJECTIVE] = new Vector2D(0, 0);
		pp00[OCULAR] = new Vector2D(0, 0);

		// period of clear-aperture array of the two LAs
		ppPeriod[OBJECTIVE] = 1*MM;
		ppPeriod[OCULAR] = 1*MM;

		// period of clear-aperture array of the two LAs
		acPeriod[OBJECTIVE] = 1*MM;
		acPeriod[OCULAR] = 1*MM;
				
		// rotation angle around the z axis of the two LAs
		phiDeg = 0;
		
		showLensletArray[OBJECTIVE] = true;
		showLensletArray[OCULAR] = true;
		
		// additional lenses
		showBigLens[OBJECTIVE] = false;
		showBigLens[OCULAR] = false;
		bigLensF[OBJECTIVE] = 2*M;
		bigLensF[OCULAR] = 1*CM;
		
		minimumZSeparation = 10*UM;
		lensType = LensType.IDEAL_THIN_LENS;
		shadowThrowing = true;
		showFieldLensArray = false;
		focusOnObjectZ = false;
		objectDistance = 2*M;
		simulateDiffractiveBlur = true;
		lambda = 632.8*NM;
		
		//refractive settings
		refractiveLens = false;
		refractiveIndex = 1.5;
		separatedArrays = true;
		boundingBoxThickness= 0.05;
		centreThicknessArray1 = 0.0003; 
		centreThicknessArray2 = 0.0003;
		
		// studioInitialisation = StudioInitialisationWithZAdjustableForegroundType.LATTICE;	// the backdrop
		foreground = ForegroundType.LATTICE;
		foregroundZ = objectDistance;
		foregroundWidth = 1;
		foregroundPhiDeg = 0;
		showSphereAtImageOfCamera = false;

		// camera

		cameraViewCentre = new Vector3D(0, 0, 0);
		// cameraDistance = 1*CM;	// now done in initParameters()
		cameraViewDirection = new Vector3D(0, 0, 1);
		// cameraHorizontalFOVDeg = 150;	// now done in initParameters()
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 10*M;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Gabor-superer-lens explorer";
			windowWidth = 1500;
			windowHeight = 950;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "GaborSupererLensExplorer";	// the name
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine

		// printStream.println("@=("+@[0]+","+@[1]+")");
		printStream.println("width="+width);
		printStream.println("height="+height);
		printStream.println("f=("+f[0]+","+f[1]+")");
		printStream.println("ac00="+ac00[0]+","+ac00[1]+")");
		printStream.println("pp00=("+pp00[0]+","+pp00[1]+")");
		printStream.println("ppPeriod=("+ppPeriod[0]+","+ppPeriod[1]+")");
		printStream.println("acPeriod=("+acPeriod[0]+","+acPeriod[1]+")");
		printStream.println("phiDeg="+phiDeg);
		printStream.println("showLensletArray=("+showLensletArray[0]+","+showLensletArray[1]+")");
		printStream.println("showBigLens=("+showBigLens[0]+","+showBigLens[1]+")");
		printStream.println("bigLensF=("+bigLensF[0]+","+bigLensF[1]+")");
		printStream.println("minimumZSeparation="+minimumZSeparation);
		printStream.println("lensType="+lensType);
		printStream.println("shadowThrowing="+shadowThrowing);
		printStream.println("showFieldLens="+showFieldLensArray);
		printStream.println("focusOnObjectZ="+focusOnObjectZ);
		printStream.println("objectDistance="+objectDistance);
		printStream.println("simulateDiffractiveBlur="+simulateDiffractiveBlur);
		printStream.println("lambda="+lambda);
		printStream.println("foreground="+foreground);
		printStream.println("foregroundZ="+foregroundZ);
		printStream.println("foregroundWidth="+foregroundWidth);
		printStream.println("foregroundPhiDeg="+foregroundPhiDeg);
		printStream.println("showSphereAtImageOfCamera="+showSphereAtImageOfCamera);

		printStream.println();

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	
	// private variables; use calculateAllParameters() to calculate
	
	private double zSeparation, zLA[] = new double[2];
	private Vector3D
		normal,	// normal common to the plane of lenslet arrays 1 and 2 and the common focal plane, F
		centreOfRectangularOutline[] = new Vector3D[2],	// centre of the rectangular outline of lenslet array 1 and 2
		ac00xyz[] = new Vector3D[2],	// centre of the array of clear apertures (i.e. coordinate of the centre of the clear aperture of lenslet (0,0)) of lenslet array 1 and 2
		pp00xyz[] = new Vector3D[2],	// centre of the array of principal points (i.e. coordinate of the principal point of lenslet (0,0)) of lenslet array 1 and 2
		pp10xyz[] = new Vector3D[2],	// coordinate of the principal point of lenslet (1,0) of lenslet array 1 and 2
		pBigLens[] = new Vector3D[2],	// principal points of big lenses
		cameraPosition;
	
	private void calculateAllParameters()
	{
		// the z separation	
		if(focusOnObjectZ)
		{
			zSeparation = (f[OCULAR]*ppPeriod[OBJECTIVE]/ppPeriod[OCULAR] + f[OBJECTIVE]) / (1-f[OBJECTIVE]/objectDistance);
					// (fP p q + f pP q)/(pP (q - f));
					// (f[OBJECTIVE] + f[OCULAR]*ppPeriod[OBJECTIVE]/ppPeriod[OCULAR])/(1 - f[OBJECTIVE]/objectDistance);
		}
		else
		{
			zSeparation = f[OBJECTIVE] + f[OCULAR];
		}
		
				
		// ... but when it gets too small, then Dr TIM doesn't recognise that there are two separate lenslet arrays
		if(Math.abs(zSeparation) < minimumZSeparation)
		{
			// in that case, set the z separation to some number (with the same sign as f1+f2) that is large enough
			// so that Dr TIM can tell the two lenslet arrays apart but small enough that the telescope array formed
			// by the two lenslet arrays still works well
			zSeparation = minimumZSeparation*MyMath.signumNever0(zSeparation);
		}
		if(zSeparation < 0)
		{
			// if the two lenslet arrays switch order, the imaging will change; put a warning in the console
			System.err.println("The separation between the two lenslet arrays is negative ("+zSeparation+"), which means they switch order");			
		}

		if(separatedArrays != true) {
			zSeparation = zSeparation*refractiveIndex;
		}
		
		// normalised normal common to the plane of lenslet arrays 1 and 2 and the common focal plane, F;
		// must point from lenslet array 1 to lenslet array 0!
		normal = Vector3D.Z;

		for(int i=0; i<=1; i++)
		{
			double phi = MyMath.deg2rad(-Math.pow(-1, i)*0.5*phiDeg);
			double c = Math.cos(phi);
			double s = Math.sin(phi);
			zLA[i] = (i==0)?0:-zSeparation;
			
			// centre of the rectangular outlines of lenslet array 1 and 2
			centreOfRectangularOutline[i] = new Vector3D(0, 0, zLA[i]);
			// System.out.println("CLASpecsExplorer::calculateAllParameters: centreOfRectangularOutline["+i+"] = " + centreOfRectangularOutline[i]);

			// centre of the array of clear apertures (i.e. coordinate of the centre of the clear aperture of lenslet (0,0)) of lenslet arrays 1 and 2
			ac00xyz[i] = new Vector3D(ac00[i].x, ac00[i].y, zLA[i]);
			// System.out.println("CLASpecsExplorer::calculateAllParameters: ac00xyz["+i+"] = " + ac00xyz[i]);

			// centre of the array of principal points (i.e. coordinate of the principal point of lenslet (0,0)) of lenslet arrays 1 and 2
			pp00xyz[i] = new Vector3D(pp00[i].x, pp00[i].y, zLA[i]);
			// System.out.println("CLASpecsExplorer::calculateAllParameters: pp00xyz["+i+"] = " + pp00xyz[i]);

			// coordinate of the principal point of lenslet (1,0) of lenslet arrays 1 and 2
			pp10xyz[i] = new Vector3D(pp00[i].x + ppPeriod[i]*c, pp00[i].y + ppPeriod[i]*s, zLA[i]);
			// System.out.println("CLASpecsExplorer::calculateAllParameters: pp10xyz["+i+"] = " + pp10xyz[i]);
			
			// principal point of big lens
			pBigLens[i] = new Vector3D(0, 0, zLA[i] + minimumZSeparation*Math.pow(-1, i));
		}
		
		cameraPosition = Vector3D.sum(centreOfRectangularOutline[OCULAR], cameraViewDirection.getWithLength(-cameraDistance));
		// System.out.println("CLASpecsExplorer::calculateAllParameters: centreOfRectangularOutline[OCULAR]="+centreOfRectangularOutline[OCULAR]+", cameraDistance="+cameraDistance+", cameraPosition="+cameraPosition);
	}

		
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
//		StudioInitialisationWithZAdjustableForegroundType.initialiseSceneAndLights(
//				studioInitialisation,
//				foregroundZ,
//				scene,
//				studio
//			);

		StudioInitialisationType.initialiseSceneAndLights(
				StudioInitialisationType.MINIMALIST,
				scene,
				studio
			);
		
		addForeground(scene);

		calculateAllParameters();
		
		if(refractiveLens) {
			Vector3D boundingBoxCentre=null;
			if(separatedArrays) {
				boundingBoxCentre = Vector3D.sum(centreOfRectangularOutline[0], centreOfRectangularOutline[1]).getProductWith(0.5);
			}else {
				boundingBoxCentre = Vector3D.sum(centreOfRectangularOutline[0], centreOfRectangularOutline[1]).getProductWith(0.5*refractiveIndex);
			}
			double phi0 = MyMath.deg2rad(-Math.pow(-1, 0)*0.5*phiDeg);
			double c0 = Math.cos(phi0);
			double s0 = Math.sin(phi0);
			
			double phi1 = MyMath.deg2rad(-Math.pow(-1, 1)*0.5*phiDeg);
			double c1 = Math.cos(phi1);
			double s1 = Math.sin(phi1);
			Vector3D commonPlaneInterceptionPoint;
			
			
			scene.addSceneObject(new GaborSupererRefractiveCLAs(		
//					"lenslet array made of refractive lenses",// description,
//					boundingBoxCentre,	// boundingBoxCentre,
//					new Vector3D(width, 0, 0),	// boundingBoxSpanVector1,
//					new Vector3D(0, height, 0),	// boundingBoxSpanVector2,
//					new Vector3D(0, 0, boundingBoxThickness), // boundingBoxSpanVector3,
//					new Vector3D(0,0,1),// normalisedOpticalAxisDirection,
//					f[0],// focalLengthArray1,
//					ac00xyz[0], // lens00ClearApertureCentreArray1,
//					new Vector3D(acPeriod[0], 0, 0),	//  clearApertureArrayBasisVector1Array1,
//					new Vector3D(0, acPeriod[0], 0),	// clearApertureArrayBasisVector2Array1,
//					pp00xyz[0],//  lens00PrincipalPointArray1,
//					new Vector3D( ppPeriod[0]*c0, ppPeriod[0]*s0, 0),	// principalPointArrayBasisVector1Array1,
//					new Vector3D(-ppPeriod[0]*s0, ppPeriod[0]*c0, 0),	// principalPointArrayBasisVector2Array1,
//					centreThicknessArray1,// centreThicknessArray1,
//					f[1],// focalLengthArray2,
//					ac00xyz[1], // lens00ClearApertureCentreArray2,
//					new Vector3D(acPeriod[1], 0, 0),	// clearApertureArrayBasisVector1Array2,
//					new Vector3D(0, acPeriod[1], 0),	// clearApertureArrayBasisVector2Array2,
//					pp00xyz[1],//  lens00PrincipalPointArray2,
//					new Vector3D( ppPeriod[1]*c1, ppPeriod[1]*s1, 0),	// principalPointArrayBasisVector1Array2,
//					new Vector3D(-ppPeriod[1]*s1, ppPeriod[1]*c1, 0),	// principalPointArrayBasisVector2Array2,
//					centreThicknessArray2,// centreThicknessArray2,
//					refractiveIndex, // refractiveIndex,
//					0.96, //surfaceTransmissionCoefficient,
//					shadowThrowing, // shadowThrowing,
//					separatedArrays, //separatedArrays,
//					cameraMaxTraceLevel, //maxSteps
//					scene, //parent,
//					studio //studio
			
			"lenslet array made of refractive lenses",// description,
			boundingBoxCentre,	// boundingBoxCentre,
			new Vector3D(width, 0, 0),	// boundingBoxSpanVector1,
			new Vector3D(0, height, 0),	// boundingBoxSpanVector2,
			new Vector3D(0, 0, boundingBoxThickness), // boundingBoxSpanVector3,
			new Vector3D(0,0,1),// normalisedOpticalAxisDirection,
			new Vector3D(0,0,Double.POSITIVE_INFINITY), //commonPlaneInterceptionPoint,
			ac00xyz[0], // lens00ClearApertureCentreArray1,
			new Vector3D(acPeriod[0], 0, 0),	//  clearApertureArrayBasisVector1Array1,
			new Vector3D(0, acPeriod[0], 0),	// clearApertureArrayBasisVector2Array1,
			f[0],// focalLengthArray1,
			pp00xyz[0],//  lens00PrincipalPointArray1,
			new Vector3D( ppPeriod[0]*c0, ppPeriod[0]*s0, 0),	// principalPointArrayBasisVector1Array1,
			new Vector3D(-ppPeriod[0]*s0, ppPeriod[0]*c0, 0),	// principalPointArrayBasisVector2Array1,
			centreThicknessArray1,// centreThicknessArray1,
			f[1],// focalLengthArray2,
			pp00xyz[1],//  lens00PrincipalPointArray2,
			new Vector3D( ppPeriod[1]*c1, ppPeriod[1]*s1, 0),	// principalPointArrayBasisVector1Array2,
			new Vector3D(-ppPeriod[1]*s1, ppPeriod[1]*c1, 0),	// principalPointArrayBasisVector2Array2,
			centreThicknessArray2,// centreThicknessArray2,
			refractiveIndex, // refractiveIndex,
			0.96, //surfaceTransmissionCoefficient,
			shadowThrowing, // shadowThrowing,
			separatedArrays, //separatedArrays,
			50, //maxSteps
			scene, //parent,
			studio //studio		
					)
					);
		}else {

			for(int i=0; i<=1; i++)
			{
				double phi = MyMath.deg2rad(-Math.pow(-1, i)*0.5*phiDeg);
				double c = Math.cos(phi);
				double s = Math.sin(phi);

				scene.addSceneObject(new EditableLensletArrayForGaborSupererLens(
						"LA"+i,	// description,
						centreOfRectangularOutline[i],	// centreRectangle,
						new Vector3D(width, 0, 0),	// uSpanVector,
						new Vector3D(0, height, 0),	// vSpanVector, 
						f[i],	// focalLength,
						new Vector3D(acPeriod[i], 0, 0),	// aperturesLatticeVector1,
						new Vector3D(0, acPeriod[i], 0),	// aperturesLatticeVector2,
						new Vector3D( ppPeriod[i]*c, ppPeriod[i]*s, 0),	// principalPointsLatticeVector1,
						new Vector3D(-ppPeriod[i]*s, ppPeriod[i]*c, 0),	//  principalPointsLatticeVector2,
						ac00xyz[i],	// aperture00Centre,
						pp00xyz[i],	// principalPoint00,
						lensType,
						simulateDiffractiveBlur,
						lambda,
						0.96,	// throughputCoefficient,
						true,	// shadowThrowing,
						scene,	// parent,
						studio
						),
						showLensletArray[i]
						);
			}
		}
		
		// field-lens array
		
		// TODO add the field-lens array
//		// this works only if both f1 and f2 are positive, period1 = period2, and if theta1 = phi1 = theta2 = phi2 = 0
//		if((f1 > 0) && (f2 > 0) && (period1 == period2) && (theta1 == 0) && (phi1 == 0) && (theta2 == 0) && (phi2 == 0))
//		{
//			// 1/fField == 1/f1 + 1/f2
//			// field lens has to be a distance i behind LA1, where 1/objectDistance + 1/i = 1/f1, so 1/i = 1/f1 - 1/objectDistance
//			scene.addSceneObject(new EditableRectangularLensletArray(
//					"field-lens array",	// description
//					new Vector3D(0, 0, (focusOnObjectZ?-1/(1/f1 - 1/objectDistance):-f1)),	// centre
//					right1,	// spanVector1
//					up,	// spanVector2
//					1/(1/(f1*zSeparationFactor) + 1/(f2*zSeparationFactor)),	// focalLength
//					period1,	// xPeriod
//					period1,	// yPeriod
//					0,	// xOffset
//					0,	// yOffset
//					lensType,
//					simulateDiffractiveBlur,
//					lambdaNM*1e-9,
//					0.96,	// throughputCoefficient
//					false,	// reflective
//					shadowThrowing,	// shadowThrowing
//					scene,	// parent
//					studio
//				), 
//				showFieldLensArray
//			);
//		}
		for(int i=0; i<=1; i++){
			if(refractiveLens) {
//			double centreThickness;
//			if(bigLensF[i]<=0) {
//				centreThickness = SimpleRefractiveCLAs.calculateMinAndMaxThickness(pBigLens[i], pBigLens[i],  new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), 
//						new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), -1, bigLensF[i], refractiveIndex, new int[] {0,0})[0];
//			}
//				else {
//					centreThickness = SimpleRefractiveCLAs.calculateMinAndMaxThickness(pBigLens[i], pBigLens[i],  new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), 
//							new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), -1, bigLensF[i], refractiveIndex, new int[] {0,0})[1];	
//			}
//			double boundingBoxThicknessSingleLens = 0.5*SimpleRefractiveCLAs.calculateMinAndMaxThickness(pBigLens[i], pBigLens[i],  new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), 
//					new Vector3D(width, 0, 0),  new Vector3D(0, height, 0), -1, bigLensF[i], refractiveIndex, new int[] {0,0})[1];
//		
//			//create a single refractive lens by using a 1x1 lenslet array. 
//			scene.addSceneObject(new SimpleRefractiveCLAs(
//					"Big lens in front of LA"+i,// description,
//					pBigLens[i],// centre,
//					new Vector3D(width, 0, 0),	// spanVector1,
//					new Vector3D(0, height, 0),	// spanVector2,
//					new Vector3D(0, 0, boundingBoxThicknessSingleLens),	// spanVector2,
//					new Vector3D(0,0,1), //normalisedOpticalAxisDirection,
//					new Vector3D(width, 0, 0), // clearApertureArrayBasisVector1, 
//					new Vector3D(0, height, 0),// clearApertureArrayBasisVector2,
//					new Vector3D(width, 0, 0), // principalPointArray1BasisVector1,
//					new Vector3D(0, height, 0), // principalPointArray1BasisVector2,
//					new Vector3D(width, 0, 0), // principalPointArray2BasisVector1,
//					new Vector3D(0, height, 0), // principalPointArray2BasisVector2,
//					pBigLens[i], // lens00ClearApertureCentreArray1, 
//					pBigLens[i], //lens00ClearApertureCentreArray2,
//					pBigLens[i], // lens00PrincipalPointArray1,
//					pBigLens[i], // lens00PrincipalPointArray2,
//					bigLensF[i], //focalLengthArray1,
//					bigLensF[i], //focalLengthArray2,			
//					refractiveIndex, // refractiveIndex,
//					centreThickness, // lensletCentreThicknessArray1,
//					centreThickness, // lensletCentreThicknessArray2,
//					cameraMaxTraceLevel, // maxSteps, 
//					0.96, //surfaceTransmissionCoefficient,
//					shadowThrowing, // shadowThrowing,
//					true, //separatedArrays,
//					scene, //parent,
//					studio //studio
//					),
//					showBigLens[i]	
//			);

		}else {
			// the big lenses
			scene.addSceneObject(new EditableRectangularLens(
					"Big lens in front of LA"+i,	// description,
					pBigLens[i],	// centre,
					new Vector3D(width, 0, 0),	// spanVector1,
					new Vector3D(0, height, 0),	// spanVector2, 
					bigLensF[i],	// focalLength,
					lensType,
					0.96,	// throughputCoefficient,
					shadowThrowing,
					scene,	// parent
					studio
					),
					showBigLens[i]
					);
		}
		}
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"Sphere at image of camera position",	// description,
						calculateImage10(getCameraPosition()),	// centre,
						0.01,	// radius,
						SurfaceColour.WHITE_SHINY,	// surfaceProperty,
						scene, studio),
				showSphereAtImageOfCamera
				);

		// the camera
		
		Vector3D topDirection = new Vector3D(0, 1, 0);
		if(cameraViewDirection.getPartPerpendicularTo(topDirection).getLength() == 0) topDirection = new Vector3D(1, 0, 0);
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Camera",
				cameraPosition,	// centre of aperture
				cameraViewDirection,	// viewDirection
				topDirection,	// top direction vector
				cameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				cameraSpaceTimeTransformationType,	// spaceTimeTransformationType
				cameraBeta,	// beta
				cameraPixelsX, cameraPixelsY,	// logical number of pixels
				cameraExposureCompensation,	// ExposureCompensationType.EC0,	// exposure compensation +0
				cameraMaxTraceLevel,	// maxTraceLevel
				new Plane(
						"focus plane",	// description
						Vector3D.sum(cameraPosition, cameraViewDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
						cameraViewDirection,	// normal
						null,	// surfaceProperty
						null,	// parent
						null	// studio
					),	// focus scene
				null,	// cameraFrameScene,
				cameraApertureSize,	// aperture size
				renderQuality.getBlurQuality(),	// blur quality
				renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			);
		studio.setCamera(camera);
	}
	
	public void addForeground(SceneObjectContainer scene)
	{
		double foregroundPhi = MyMath.deg2rad(foregroundPhiDeg);
		double c = Math.cos(foregroundPhi);
		double s = Math.sin(foregroundPhi);
		
		// rotated basis vectors
		Vector3D h = new Vector3D( c, s, 0);
		Vector3D v = new Vector3D(-s, c, 0);
		
		switch(foreground)
		{
		case LATTICE:
			double cylinderRadius = 0.02;
			
			// a cylinder lattice...
			scene.addSceneObject(new EditableCylinderLattice(
					"cylinder lattice",
					-0.5*foregroundWidth, 0.5*foregroundWidth, 4, h,
					-0.5*foregroundWidth, 0.5*foregroundWidth, 4, v,
					foregroundZ, foregroundZ+30, 4, Vector3D.Z,	// this puts the "transverse" cylinders into the planes z=10, 20, 30, 40
					cylinderRadius,
					scene,
					studio
			));		

			break;
		case TIM:
			scene.addSceneObject(new EditableTimHead(
					"Tim's head",
					new Vector3D(0, 0, foregroundZ),
					foregroundWidth,	// radius
					new Vector3D(0, 0, -1),	// front direction
					v,	// top direction
					h,	// right direction
					scene,
					studio
				));
			break;
		case CHART:
		default:
			scene.addSceneObject(new EditableFramedUSAFTestChart(
					"USAF chart",	// description,
					new Vector3D(0, 0, foregroundZ),	// centre,
					v,	// up,
					new Vector3D(0, 0, -1),	// front,
					foregroundWidth,	// width,
					// double frameRadius,
					SurfaceColour.GREY20_SHINY,	// frameSurfaceProperty,
					true,	// showFrame,
					scene,	// parent, 
					studio
			));
		}
	}
	
	public double calculateImageZ(double objectZ, double lensZ, double lensF)
	{
		// object distance
		double o = lensZ - objectZ;
		// 1/f = 1/o + 1/i, so 1/i = 1/f - 1/o = (o-f)/(o*f)
		double i = o*lensF/(o-lensF);
		// z coordinate of image
		return lensZ + i;
	}

	/**
	 * Calculate the position of the image of an object if light passes first through lenslet array 1, then through lenslet array 0
	 * @param object
	 * @return	the image position that corresponds to the given object position
	 */
	public Vector3D calculateImage10(Vector3D object)
	{
		calculateAllParameters();
		
		// calculate a point on the intermediate image plane -- see lab book p.99
		double zI = calculateImageZ(object.z, zLA[OCULAR], f[OCULAR]);
//		double o1 = zLA[OCULAR] - object.z;
//		// 1/f1 = 1/o1 + 1/i1, so 1/i1 = 1/f1 - 1/o1 = (o1-f1)/(o1*f1)
//		double i1 = o1*f[OCULAR]/(o1-f[OCULAR]);
//		// z coordinate of intermediate image
//		double zI = zLA[OCULAR] + i1;
		
		Vector3D pointOnI = new Vector3D(0, 0, zI);
		
		try {
			// the points I00 and I10 are given by the intersection with the plane F of the line through <object> and P00 and P10, respectively
			Vector3D i00 = Geometry.linePlaneIntersection(
					object,	// pointOnLine,
					Vector3D.difference(pp00xyz[OCULAR], object),	// directionOfLine,
					pointOnI,	// pointOnPlane,
					normal	// normalToPlane
					);
			Vector3D i10 = Geometry.linePlaneIntersection(
					object,	// pointOnLine,
					Vector3D.difference(pp10xyz[OCULAR], object),	// directionOfLine,
					pointOnI,	// pointOnPlane,
					normal	// normalToPlane
					);
			
			// the image lies on the intersection between the lines through i00 and P00 of LA2 and through i10 and P10 of LA2

			try {
				// or use pointClosestToBothLines?
				return Geometry.lineLineIntersection(
						i00,	// pointOnLine1,
						Vector3D.difference(pp00xyz[OBJECTIVE], i00),	// directionOfLine1,
						i10,	// pointOnLine2,
						Vector3D.difference(pp10xyz[OBJECTIVE], i10)	// directionOfLine2
						);
			} catch (RayTraceException e) {
				// no intersection, which must be because the image is at infinity
				return new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
		} catch (MathException e) {
			e.printStackTrace();
			return Vector3D.NaV;
		}
	}
	
	public void makeSuperlens()
	{
		// simply align the clear-aperture arrays with the principal-point arrays
		
		for(int i=0; i<=1; i++)
		{
			// make the offset of the clear-aperture arrays the same as those of the principal-point arrays...
			ac00MMPanel[i].setVector2D(pp00[i].x/MM, pp00[i].y/MM);

			// ... and make the periods of the clear-aperture arrays the same as those of the principal-point arrays
			acPeriodMMPanel[i].setNumber(ppPeriod[i]/MM);
		}
	}

	public Vector3D getCameraPosition()
	{
		return getStandardCameraPosition();
	}
	
	public void makeSupererLensOptimisedForCameraPosition()
	{
		calculateAllParameters();
		// Vector3D cameraPosition = getCameraPosition();
		
		// first calculate the z coordinate of either the camera (if there is no ocular big lens) or of
		// the image of the camera due to the ocular big lens...
		double zC;
		if(showBigLens[OCULAR]) zC = calculateImageZ(cameraPosition.z, pBigLens[OCULAR].z, bigLensF[OCULAR]);
		else zC = cameraPosition.z;
		System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: pBigLens[OCULAR].z="+pBigLens[OCULAR].z);
		System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: zC="+zC);
		
//		// find a point on the plane between the two lenslet arrays that 
//		double absf[] = new double[2];
//		absf[0] = Math.abs(f[0]);
//		absf[1] = Math.abs(f[1]);
//		Vector3D pointOnI = new Vector3D(0, 0, zLA[OCULAR] + absf[OCULAR] / (absf[OCULAR] + absf[OBJECTIVE])*zSeparation);
//		System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: absf[0]="+absf[0]+", absf[1]="+absf[1]+", zI="+(zLA[OCULAR] + absf[OCULAR] / (absf[OCULAR] + absf[OBJECTIVE])*zSeparation));
		
		// ... and then calculate the z coordinate of the elemental images of *that* due to the ocular LA
		double zI = calculateImageZ(zC, zLA[OCULAR], f[OCULAR]);
		
//		// calculate a point on the intermediate image plane -- see lab book p.99
//		double o1 = zLA[OCULAR] - cameraPosition.z;
//		// 1/f1 = 1/o1 + 1/i1, so 1/i1 = 1/f1 - 1/o1 = (o1-f1)/(o1*f1)
//		double i1 = o1*f[OCULAR]/(o1-f[OCULAR]);
//		// z coordinate of intermediate image
//		double zI = zLA[OCULAR] + i1;
//		// System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: zLA[OCULAR]="+zLA[OCULAR]+", o1="+o1+", i1="+i1+", zI="+zI);
		
		Vector3D pointOnI = new Vector3D(0, 0, zI);
		System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: pointOnI="+pointOnI);


		try {
			// if necessary, calculate image of camera position due to ocular big lens
			Vector3D cI;
			if(showBigLens[OCULAR])
			{
				cI = Geometry.linePlaneIntersection(
						cameraPosition, 
						Vector3D.difference(pBigLens[OCULAR], cameraPosition),
						new Vector3D(0, 0, zC),
						normal
					);
				System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: cI="+cI);
			}
			else
				cI = cameraPosition;
			
			// first construct I00, the intersection of the line through the camera position and P00 with the intermediate image plane
			Vector3D i00 = Geometry.linePlaneIntersection(
					cI,	// pointOnLine,
					Vector3D.difference(pp00xyz[OCULAR], cI),	// directionOfLine,
					pointOnI,	// pointOnPlane,
					normal	// normalToPlane
					);
			System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: pp00xyz[OCULAR]="+pp00xyz[OCULAR]);
			System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: i00="+i00);

			Vector3D i10 = Geometry.linePlaneIntersection(
					cI,	// pointOnLine,
					Vector3D.difference(pp10xyz[OCULAR], cI),	// directionOfLine,
					pointOnI,	// pointOnPlane,
					normal	// normalToPlane
					);
			System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: pp10xyz[OCULAR]="+pp10xyz[OCULAR]);
			System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: i10="+i10);

			// next, construct A00 of LA1 and LA2, which is the intersection between the plane of LA1 or LA2
			// and the line through I00 with
			// EITHER the direction from the camera position to its image
			// Vector3D cameraImage = calculateImage(cameraPosition);
			// Vector3D cameraPosition2Image = Vector3D.difference(cameraImage, cameraPosition).getNormalised();
			// Vector3D directionThroughA00I00 = Vector3D.difference(cameraImage, cameraPosition).getNormalised();
			// OR (better, I think) the same direction as the normal to the lenslet arrays
			Vector3D directionThroughA00I00 = normal;
			
			// calculate the object distance
			// double o = Vector3D.scalarProduct(Vector3D.difference(centreOfRectangularOutline[OCULAR], cameraPosition), normal);
			// and the image distance
			// double i = Vector3D.scalarProduct(Vector3D.difference(cameraImage, centreOfRectangularOutlineOfLA2), normal);
			
			for(int i=0; i<=1; i++)
			{
				ac00xyz[i] = Geometry.linePlaneIntersection(
						i00,	// pointOnLine,
						directionThroughA00I00,	// directionOfLine,
						centreOfRectangularOutline[i],	// pointOnPlane,
						normal	// normalToPlane
						);

				ac00MMPanel[i].setVector2D(ac00xyz[i].x/MM, ac00xyz[i].y/MM);

//				Vector3D ac10xyz = Geometry.linePlaneIntersection(
//						i10,	// pointOnLine,
//						directionThroughA00I00,	// directionOfLine,
//						centreOfRectangularOutline[i],	// pointOnPlane,
//						normal	// normalToPlane
//						);
				System.out.println("CLASpecsExplorer::makeSupererLensOptimisedForCameraPosition: a="+Vector3D.getDistance(i10, i00)/MM+"mm");
				acPeriodMMPanel[i].setNumber(Vector3D.getDistance(i10, i00)/MM); // (ppPeriod[OCULAR]*(o+f[OCULAR])/o)/MM);
			}
		} catch (MathException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * initialise TW width, height, focal lengths; camera distance & horizontal FOV
	 */
	public void initParameters()
	{
		switch(gaborSupererLensExplorerInitialisation)
		{
		case SPECS_DEMAG:
		case SPECS_MAG:
			width = 5*CM;
			height = 3*CM;
			cameraDistance = 1*CM;
			cameraHorizontalFOVDeg = 150;
			break;
		case WINDOW_DEMAG:
		case WINDOW_MAG:
			width = 1*M;
			height = 1*M;
			cameraDistance = 1*M;
			cameraHorizontalFOVDeg = 70;
		}
		
		switch(gaborSupererLensExplorerInitialisation)
		{
		case SPECS_DEMAG:
		case WINDOW_DEMAG:
			f[OBJECTIVE] = -1*MM;
			f[OCULAR] =  5*MM;
			break;
		case SPECS_MAG:
		case WINDOW_MAG:
			f[OBJECTIVE] =  5*MM;
			f[OCULAR] = -1*MM;
		}
		
		if(widthCMPanel != null) widthCMPanel.setNumber(width/CM);
		if(heightCMPanel != null) heightCMPanel.setNumber(height/CM);
		for(int i=0; i<=1; i++)
		{
			if(fMMPanel != null) fMMPanel[i].setNumber(f[i]/MM);
		}
		if(cameraDistanceCMPanel != null) cameraDistanceCMPanel.setNumber(cameraDistance/CM);
		if(cameraHorizontalFOVDegPanel != null) cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
	}

	
	//
	// for interactive version
	//
	
//	public enum LensletArraysInitialisationType
//	{
//		INIT("Initialise lenslet arrays to..."),
//		GABOR("Gabor superlens"),
//		MOIRE("Moir\u00E9 magnifier"),
//		CLAS("Confocal lenslet arrays");
//		
//		private String description;
//		private LensletArraysInitialisationType(String description) {this.description = description;}	
//		@Override
//		public String toString() {return description;}
//	}

	private DoublePanel minimumZSeparationMMPanel, fMMPanel[], ppPeriodMMPanel[], bigLensFMPanel[], acPeriodMMPanel[], foregroundZMPanel, foregroundWidthMPanel;
	private LabelledVector2DPanel ac00MMPanel[], pp00MMPanel[];
	private DoublePanel widthCMPanel, heightCMPanel, phiDegPanel, foregroundPhiDegPanel, lambdaNMPanel, objectDistanceMPanel; // , etaPanel, separationPanel
	private JCheckBox showLensletArrayCheckBox[], showBigLensCheckBox[], shadowThrowingCheckBox, simulateDiffractiveBlurCheckBox, showFieldLensArrayCheckBox, focusOnObjectCheckBox, showSphereAtImageOfCameraCheckBox,
	refractiveLensCheckBox, separatedArraysCheckBox;
	// private JComboBox<LensletArraysInitialisationType> lensletArraysInitialisationComboBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JComboBox<ForegroundType> foregroundComboBox;
	// private JComboBox<StudioInitialisationWithZAdjustableForegroundType> studioInitialisationComboBox;
	private JButton setObjectZ2LastClickZButton;	// etaSOInitialisationButton;	// gaborInitialisationButton, moireInitialisationButton, clasInitialisationButton;
	private JTextArea infoTextArea;
	private JButton updateInfoButton, makeSupererLensButton, makeSuperlensButton;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraDistanceCMPanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistanceMPanel;
	
	private JComboBox<GaborSupererLensExplorerInitialisationType> GaborSupererLensExplorerInitialisationComboBox;
	private JButton initButton;

	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		// the main tabbed pane, with "Scene" and "Camera" tabs
		JTabbedPane sceneCameraTabbedPane = new JTabbedPane();
		interactiveControlPanel.add(sceneCameraTabbedPane, "span");
		
		GaborSupererLensExplorerInitialisationComboBox = new JComboBox<GaborSupererLensExplorerInitialisationType>(GaborSupererLensExplorerInitialisationType.values());
		GaborSupererLensExplorerInitialisationComboBox.setSelectedItem(gaborSupererLensExplorerInitialisation);
		initButton = new JButton("Go");
		initButton.addActionListener(this);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Initialisation", GaborSupererLensExplorerInitialisationComboBox, initButton), "span");
		
		//
		// the Lenslet arrays panel
		//

		JPanel lensletArraysPanel = new JPanel();
		lensletArraysPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Lenslet arrays", lensletArraysPanel);

		//
		// the Lenslet-arrays-initialisation panel
		//

		// scenePanel.add(new JLabel("Lenslet-array initialisation"));
		
//		JPanel initPanel = new JPanel();
//		initPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Initialisation of lenslet arrays"));
//		initPanel.setLayout(new MigLayout("insets 0"));
//		scenePanel.add(initPanel, "span");
//
//		lensletArraysInitialisationComboBox = new JComboBox<LensletArraysInitialisationType>(LensletArraysInitialisationType.values());
//		lensletArraysInitialisationComboBox.setSelectedItem(LensletArraysInitialisationType.INIT);
//		lensletArraysInitialisationComboBox.addActionListener(this);
////		lensletArraysInitialisationButton = new JButton("Go");
////		lensletArraysInitialisationButton.addActionListener(this);
//		initPanel.add(
//				GUIBitsAndBobs.makeRow("Either", 
//						lensletArraysInitialisationComboBox
//						)
//				, "span");

		widthCMPanel = new DoublePanel();
		widthCMPanel.setNumber(width/CM);
		heightCMPanel = new DoublePanel();
		heightCMPanel.setNumber(height/CM);
		lensletArraysPanel.add(GUIBitsAndBobs.makeRow("Lens size", widthCMPanel, "cm by", heightCMPanel, "cm"), "span");
		
		//
		// the LA panels
		//
		
		fMMPanel = new DoublePanel[2];
		ac00MMPanel = new LabelledVector2DPanel[2];
		pp00MMPanel = new LabelledVector2DPanel[2];
		ppPeriodMMPanel = new DoublePanel[2];
		acPeriodMMPanel = new DoublePanel[2];
		showLensletArrayCheckBox = new JCheckBox[2];
		bigLensFMPanel = new DoublePanel[2];
		showBigLensCheckBox = new JCheckBox[2];
		
		for(int i=0; i<=1; i++)
		{
			JPanel laPanel = new JPanel();
			laPanel.setBorder(GUIBitsAndBobs.getTitledBorder(((i==0)?"Objective":"Ocular")+" lenslet array"));
			laPanel.setLayout(new MigLayout("insets 0"));

			fMMPanel[i] = new DoublePanel();
			fMMPanel[i].setNumber(f[i]/MM);
			fMMPanel[i].setToolTipText("Focal length of the lenslets");
			// la1Panel.add(f1Panel, "span");

			showLensletArrayCheckBox[i] = new JCheckBox("Show");
			showLensletArrayCheckBox[i].setSelected(showLensletArray[i]);
			laPanel.add(GUIBitsAndBobs.makeRow("f", fMMPanel[i], "mm, ", showLensletArrayCheckBox[i]), "span");

			ac00MMPanel[i] = new LabelledVector2DPanel("Aperture centre of lens #(0,0)", "mm");
			ac00MMPanel[i].setVector2D(ac00[i]);
			ac00MMPanel[i].setToolTipText("(x,y) coordinates of clear-aperture centre of lens #(0,0)");
			laPanel.add(ac00MMPanel[i], "span");

			pp00MMPanel[i] = new LabelledVector2DPanel("Principal point of lens #(0,0)", "mm");
			pp00MMPanel[i].setVector2D(pp00[i]);
			pp00MMPanel[i].setToolTipText("(x,y) coordinates of principal point of lens #(0,0)");
			laPanel.add(pp00MMPanel[i], "span");

			acPeriodMMPanel[i] = new DoublePanel();
			acPeriodMMPanel[i].setNumber(acPeriod[i]/MM);
			acPeriodMMPanel[i].setToolTipText("Period of clear-aperture, i.e. width and height of the lenses' clear apertures");
			laPanel.add(GUIBitsAndBobs.makeRow("Period of clear-aperture-centre array", acPeriodMMPanel[i], "mm"), "span");

			ppPeriodMMPanel[i] = new DoublePanel();
			ppPeriodMMPanel[i].setNumber(ppPeriod[i]/MM);
			ppPeriodMMPanel[i].setToolTipText("Period of principal-point array, i.e. distance between the principal points of neighbouring lenses");
			laPanel.add(GUIBitsAndBobs.makeRow("Period of principal-point array", ppPeriodMMPanel[i], "mm"), "span");
			
			showBigLensCheckBox[i] = new JCheckBox("Show");
			showBigLensCheckBox[i].setSelected(showBigLens[i]);
			bigLensFMPanel[i] = new DoublePanel();
			bigLensFMPanel[i].setNumber(bigLensF[i]/M);
			laPanel.add(GUIBitsAndBobs.makeRow("Big lens in same plane, f", bigLensFMPanel[i], "m, ", showBigLensCheckBox[i]), "span");

			lensletArraysPanel.add(laPanel, "span");
		}
		

		//
		// common LA parameters panel
		//
		
		JPanel commonLAParametersPanel = new JPanel();
		commonLAParametersPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Common LA parameters"));
		commonLAParametersPanel.setLayout(new MigLayout("insets 0"));
		lensletArraysPanel.add(commonLAParametersPanel, "span");

		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		shadowThrowingCheckBox = new JCheckBox("Shadow throwing");
		shadowThrowingCheckBox.setSelected(shadowThrowing);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(lensTypeComboBox, shadowThrowingCheckBox), "span");
		
		refractiveLensCheckBox = new JCheckBox("Refractive lenses");
		refractiveLensCheckBox.setSelected(refractiveLens);
		separatedArraysCheckBox = new JCheckBox("Two refractive arrays separated by air");
		separatedArraysCheckBox.setSelected(separatedArrays);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(refractiveLensCheckBox, separatedArraysCheckBox), "span");
		
		showFieldLensArrayCheckBox = new JCheckBox("Add array of field lenses in common focal plane");
		showFieldLensArrayCheckBox.setSelected(showFieldLensArray);
		commonLAParametersPanel.add(showFieldLensArrayCheckBox, "span");
		
		phiDegPanel = new DoublePanel();
		phiDegPanel.setNumber(phiDeg);
		phiDegPanel.setToolTipText("Angle by which the arrays are rotated around the array normal");
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow("phi", phiDegPanel, "\u00B0"), "span");

		
		makeSuperlensButton = new JButton("Make superlens");
		makeSuperlensButton.setToolTipText("Set (x,y) of lensets (0, 0) of bboth arrays to (0, 0); set periods of clear-aperture arrays to those of principal-point arrays");
		makeSuperlensButton.addActionListener(this);
		commonLAParametersPanel.add(makeSuperlensButton, "span");
		
		makeSupererLensButton = new JButton("Make super-er lens optimised for camera position");
		makeSupererLensButton.setToolTipText("Set (x,y) of lenslets (0, 0) of both arrays and periods of principal-point arrays and of clear-aperture array 2 from period of clear-aperture array of LA1 and camera position and its image");
		makeSupererLensButton.addActionListener(this);
		commonLAParametersPanel.add(makeSupererLensButton, "span");
	
		
		
		//
		
		focusOnObjectCheckBox = new JCheckBox("Focus on object at z=");
		focusOnObjectCheckBox.setSelected(focusOnObjectZ);
		objectDistanceMPanel = new DoublePanel();
		objectDistanceMPanel.setNumber(objectDistance);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(focusOnObjectCheckBox, objectDistanceMPanel, new JLabel("m")), "span");
		
		setObjectZ2LastClickZButton = new JButton("Set object distance to that of last click");
		setObjectZ2LastClickZButton.addActionListener(this);
		commonLAParametersPanel.add(setObjectZ2LastClickZButton, "span");
		
		// objectDistancePanel.setNumber(getLastClickIntersection().p.z);


		simulateDiffractiveBlurCheckBox = new JCheckBox("");
		simulateDiffractiveBlurCheckBox.setSelected(simulateDiffractiveBlur);
		lambdaNMPanel = new DoublePanel();
		lambdaNMPanel.setNumber(lambda/NM);
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow(simulateDiffractiveBlurCheckBox, "Simulate diffractive blur for wavelength", lambdaNMPanel, "nm"), "span");

		minimumZSeparationMMPanel = new DoublePanel();
		minimumZSeparationMMPanel.setNumber(minimumZSeparation/MM);
		minimumZSeparationMMPanel.setToolTipText("The two lenslet arrays are separated in the z direction by a distance usually close to f1 + f2, unless this is less than the minimum z separation");
		commonLAParametersPanel.add(GUIBitsAndBobs.makeRow("Minimum z separation between arrays", minimumZSeparationMMPanel, "mm"), "span");

		
		//
		// rest-of-the-scene panel
		//
		
		JPanel restOfScenePanel = new JPanel();
		// restOfScenePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rest of scene"));
		restOfScenePanel.setLayout(new MigLayout("insets 0"));
		// scenePanel.add(restOfScenePanel, "span");
		sceneCameraTabbedPane.addTab("Rest of scene", restOfScenePanel);

		
//		studioInitialisationComboBox = new JComboBox<StudioInitialisationWithZAdjustableForegroundType>(StudioInitialisationWithZAdjustableForegroundType.values());
//		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
//		restOfScenePanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");
		
		JPanel foregroundPanel = new JPanel();
		foregroundPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Foreground"));
		foregroundPanel.setLayout(new MigLayout("insets 0"));
		restOfScenePanel.add(foregroundPanel, "span");

		foregroundComboBox = new JComboBox<ForegroundType>(ForegroundType.values());
		foregroundComboBox.setSelectedItem(foreground);
		foregroundPanel.add(GUIBitsAndBobs.makeRow("Object", foregroundComboBox), "span");
		
		foregroundWidthMPanel = new DoublePanel();
		foregroundWidthMPanel.setNumber(foregroundWidth/M);
		foregroundPanel.add(GUIBitsAndBobs.makeRow("width", foregroundWidthMPanel, "m"), "span");
		
		foregroundZMPanel = new DoublePanel();
		foregroundZMPanel.setNumber(foregroundZ/M);
		foregroundPanel.add(GUIBitsAndBobs.makeRow("z", foregroundZMPanel, "m"), "span");
		
		foregroundPhiDegPanel = new DoublePanel();
		foregroundPhiDegPanel.setNumber(foregroundPhiDeg);
		foregroundPanel.add(GUIBitsAndBobs.makeRow("\u03D5=", foregroundPhiDegPanel, "\u00B0"), "span");
		
		// restOfScenePanel.add(GUIBitsAndBobs.makeRow("Foreground: ", foregroundComboBox, ", width=", foregroundWidthMPanel, ", z=", foregroundZMPanel, "m, \u03D5=", foregroundPhiDegPanel, "\u00B0"), "span");
		
		
		showSphereAtImageOfCameraCheckBox = new JCheckBox("Show white sphere at image of camera position");
		showSphereAtImageOfCameraCheckBox.setSelected(showSphereAtImageOfCamera);
		restOfScenePanel.add(showSphereAtImageOfCameraCheckBox, "wrap");

		infoTextArea = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(infoTextArea); 
		infoTextArea.setEditable(false);
		infoTextArea.setText("Click on Update button to show info");
		updateInfoButton = new JButton("Update");
		updateInfoButton.addActionListener(this);
		restOfScenePanel.add(GUIBitsAndBobs.makeRow(scrollPane, updateInfoButton), "span");


		//
		// the Camera panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistanceCMPanel = new DoublePanel();
		cameraDistanceCMPanel.setNumber(cameraDistance/CM);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera distance", cameraDistanceCMPanel, "cm"), "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistanceMPanel = new DoublePanel();
		cameraFocussingDistanceMPanel.setNumber(cameraFocussingDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance", cameraFocussingDistanceMPanel, "m"));
		
		
		//
		// the console panel
		//
		
		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new MigLayout("insets 0"));
		sceneCameraTabbedPane.addTab("Console", consolePanel);

		// see https://stackoverflow.com/questions/342990/create-java-console-inside-a-gui-panel
		JTextArea ta = new JTextArea(40, 40);
		consolePanel.add(new JScrollPane( ta ), "span");
        TextAreaOutputStream taos = new TextAreaOutputStream( ta, 1000 );
        PrintStream ps = new PrintStream( taos );
        System.setOut( ps );
        System.setErr( ps );
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		gaborSupererLensExplorerInitialisation = (GaborSupererLensExplorerInitialisationType)(GaborSupererLensExplorerInitialisationComboBox.getSelectedItem());
		
		width = widthCMPanel.getNumber()*CM;
		height = heightCMPanel.getNumber()*CM;
		for(int i=0; i<=1; i++)
		{
			f[i] = fMMPanel[i].getNumber()*MM;
			ac00[i] = new Vector2D(ac00MMPanel[i].getVector2D().x*MM, ac00MMPanel[i].getVector2D().y*MM);
			pp00[i] = new Vector2D(pp00MMPanel[i].getVector2D().x*MM, pp00MMPanel[i].getVector2D().y*MM);
			ppPeriod[i] = ppPeriodMMPanel[i].getNumber()*MM;
			acPeriod[i] = acPeriodMMPanel[i].getNumber()*MM;
			showLensletArray[i] = showLensletArrayCheckBox[i].isSelected();
			bigLensF[i] = bigLensFMPanel[i].getNumber()*M;
			showBigLens[i] = showBigLensCheckBox[i].isSelected();
		}
		
		minimumZSeparation = minimumZSeparationMMPanel.getNumber()*MM;
		phiDeg = phiDegPanel.getNumber();
		
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		shadowThrowing = shadowThrowingCheckBox.isSelected();
		refractiveLens = refractiveLensCheckBox.isSelected();
		separatedArrays = separatedArraysCheckBox.isSelected();
		showFieldLensArray = showFieldLensArrayCheckBox.isSelected();
		focusOnObjectZ = focusOnObjectCheckBox.isSelected();
		objectDistance = objectDistanceMPanel.getNumber()*M;
		simulateDiffractiveBlur = simulateDiffractiveBlurCheckBox.isSelected();
		lambda = lambdaNMPanel.getNumber()*NM;
		// studioInitialisation = (StudioInitialisationWithZAdjustableForegroundType)(studioInitialisationComboBox.getSelectedItem());
		foreground = (ForegroundType)(foregroundComboBox.getSelectedItem());
		foregroundZ = foregroundZMPanel.getNumber()*M;
		foregroundWidth = foregroundWidthMPanel.getNumber()*M;
		foregroundPhiDeg = foregroundPhiDegPanel.getNumber();
		showSphereAtImageOfCamera = showSphereAtImageOfCameraCheckBox.isSelected();
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistanceCMPanel.getNumber()*CM;
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistanceMPanel.getNumber()*M;
	}
	
//	/**
//	 * initialise the lenslet-array parameters to a Gabor superlens
//	 */
//	private void gaborInitialisation()
//	{
//		// lenslet-array parameters
//		fPanel[OBJECTIVE].setNumber(0.1);
//		fPanel[OCULAR].setNumber(-0.1);
//		minimumZSeprarationPanel.setNumber(0.00001);
//		period1Panel.setNumber(0.0099);
//		period2Panel.setNumber(0.01);
//		theta1DegPanel.setNumber(0);
//		theta2DegPanel.setNumber(0);
//		phi1DegPanel.setNumber(0);
//		phi2DegPanel.setNumber(0);
//		showLensletArray1CheckBox.setSelected(true);
//		showLensletArray2CheckBox.setSelected(true);
//		showFieldLensArrayCheckBox.setSelected(false);
//	}
//
//	/**
//	 * initialise the lenslet-array parameters to a Moire magnifier
//	 */
//	private void moireInitialisation()
//	{
//		// lenslet-array parameters
//		f1Panel.setNumber(-0.1);
//		f2Panel.setNumber(0.1);
//		minimumZSeprarationPanel.setNumber(0.00001);
//		period1Panel.setNumber(0.01);
//		period2Panel.setNumber(0.01);
//		theta1DegPanel.setNumber(0);
//		theta2DegPanel.setNumber(0);
//		phi1DegPanel.setNumber(0);
//		phi2DegPanel.setNumber(0.1);
//		showLensletArray1CheckBox.setSelected(true);
//		showLensletArray2CheckBox.setSelected(true);
//		showFieldLensArrayCheckBox.setSelected(false);
//	}
//
//	/**
//	 * initialise the lenslet-array parameters to a Moire magnifier
//	 */
//	private void clasInitialisation()
//	{
//		// lenslet-array parameters
//		f1Panel.setNumber(-0.05);
//		f2Panel.setNumber(0.1);
//		minimumZSeprarationPanel.setNumber(0.00001);
//		period1Panel.setNumber(0.01);
//		period2Panel.setNumber(0.01);
//		theta1DegPanel.setNumber(0);
//		theta2DegPanel.setNumber(0);
//		phi1DegPanel.setNumber(0);
//		phi2DegPanel.setNumber(0);
//		showLensletArray1CheckBox.setSelected(true);
//		showLensletArray2CheckBox.setSelected(true);
//		showFieldLensArrayCheckBox.setSelected(false);
//	}

	public void updateInfo()
	{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(8);
		
		calculateAllParameters();
		
		infoTextArea.setText(
				"camera position="+cameraPosition+"\n"+
				"image of camera position="+calculateImage10(cameraPosition)
				// "f1="+nf.format(f1)+"\n"+
			);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(setObjectZ2LastClickZButton))
		{
			objectDistanceMPanel.setNumber(getLastClickIntersection().p.z);
		}
		else if(e.getSource().equals(makeSupererLensButton))
		{
			acceptValuesInInteractiveControlPanel();
			makeSupererLensOptimisedForCameraPosition();			
		}
		else if(e.getSource().equals(makeSuperlensButton))
		{
			acceptValuesInInteractiveControlPanel();
			makeSuperlens();						
		}
		else if(e.getSource().equals(updateInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			updateInfo();
		}
		else if(e.getSource().equals(initButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			initParameters();
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
		(new GaborSupererLensExplorer()).run();
	}
}
