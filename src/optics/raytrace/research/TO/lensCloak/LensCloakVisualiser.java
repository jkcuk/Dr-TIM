package optics.raytrace.research.TO.lensCloak;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.*;
import math.simplicialComplex.Face;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.Cylinder;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.IdealThinLensSimplicialComplex;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.HueBrightnessGradientSurface;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableArray;
import optics.raytrace.GUI.sceneObjects.EditableCameraShape;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;


/**
 * Simulate the visual appearance of a lens cloak, and the view out of it.
 * 
 * @author Johannes Courtial
 */
public class LensCloakVisualiser extends NonInteractiveTIMEngine
implements ChangeListener
{
	/**
	 * position of the top vertex, i.e. the tip of the outer tetrahedron
	 */
	private Vector3D topVertex;
	
	/**
	 * centre of the triangular base of all three nested tetrahedra
	 */
	private Vector3D baseCentre;
	
	/**
	 * position of base vertex 1, used to determine the rotational orientation around the axis through the top vertex and base centre
	 */
	private Vector3D baseVertex1;
	
	/**
	 * height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex,
	 * in physical space
	 */
	private double h1P;

	/**
	 * height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the upper inner vertex,
	 * in physical space
	 */
	private double h2P;

	/**
	 * height, as a fraction of the overall height (i.e. the distance from the base centre to the tip) of the lower inner vertex,
	 * in virtual space
	 */
	private double h1V;
	
	/**
	 * focal length of the base lens
	 */
	private double baseLensF;

	/**
	 * Allows the type of SceneObject that represents the faces for raytracing purposes to be selected
	 * @see optics.raytrace.simplicialComplex.ImagingSimplicialComplex#getSceneObjectRepresentingFace(int, double, boolean, optics.raytrace.core.SceneObject, optics.raytrace.core.Studio)
	 */
	private LensType lensTypeRepresentingFace;
	
	/**
	 * Position in outside space from which this simplicial complex looks best.
	 * If lensTypeRepresentingFace = IDEAL_THIN_LENS, the simplicial complex should work perfectly from <i>any</i> outside-space viewing position.
	 * If lensTypeRepresentingFace = POINT_2_POINT_IMAGING_HOLOGRAM, this is not the case, but from the
	 * optimumOutsideSpaceViewingPosition the simplicial complex should look the same as if lensTypeRepresentingFace = IDEAL_THIN_LENS.
	 */
	private Vector3D optimumOutsideSpaceViewingPosition;
	
	/**
	 * show structure of physical space
	 */
	private boolean showStructureP;
	
	/**
	 * radius of the cylinders and spheres that represent the physical-space structure
	 */
	private double structurePRadius;
	
	/**
	 * show structure of virtual space
	 */
	private boolean showStructureV;
	
	/**
	 * radius of the cylinders and spheres that represent the virtual-space structure
	 */
	private double structureVRadius;
	
	/**
	 * show the centroids of the selected faces
	 */
	private boolean highlightFaces;
	
	/**
	 * show the principal points of the two faces under consideration
	 */
	private boolean showPrincipalPoints;
	
	//
	// light-ray trajectories
	//
	
	/**
	 * if true, show a light-ray trajectory
	 */
	private boolean showTrajectory;
	
	/**
	 * start point of the light-ray trajectory
	 */
	private Vector3D trajectoryStartPoint;
	
	/**
	 * initial direction of the light-ray trajectory
	 */
	private Vector3D trajectoryStartDirection;
	
	/**
	 * radius of the tube that represents the trajectory
	 */
	private double trajectoryRadius;
	
	/**
	 * max. trace level when tracing trajectory
	 */
	private int trajectoryMaxTraceLevel;

	
	//
	// array of spheres
	//
	
	/**
	 * show an array of spheres
	 */
	private boolean showSpheresArray;
	
	/**
	 * radius of the spheres in the array
	 */
	private double radiusOfSpheresInArray;
	
	private double spheresArrayXMin;
	private double spheresArrayXMax;
	private double spheresArrayDX;
	private double spheresArrayYMin;
	private double spheresArrayYMax;
	private double spheresArrayDY;
	private double spheresArrayZMin;
	private double spheresArrayZMax;
	private double spheresArrayDZ;

	
	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	/**
	 * If true, draw a model camera at the inside-camera position, pointing in the direction the camera views
	 */
	private boolean indicateInsideCameraPosition;
	
	/**
	 * width of the model camera
	 */
	private double modelCameraWidth;
	
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
	 * show the virtual-space position of the centre of the patterned sphere:
	 */
	private boolean showVirtualSpacePositionOfPatternedSphereCentre;
	
	/**
	 * show the position of the centre of the patterned sphere in the (physical) spaces of the other simplices
	 */
	private boolean showOtherRealSpacePositionsOfPatternedSphereCentre;
	
	/**
	 * Radius of the patterned sphere
	 */
	private double patternedSphereRadius;
			
	/**
	 * The point about which the view appears rotated
	 */
	private Vector3D pointOfInterest;
	
	
	//
	// cameras
	//
	
	private enum CameraType
	{
		INSIDE("Inside, looking out"),
		OUTSIDE("Outside");
		
		private String description;
		private CameraType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * Determines which camera to use to generate the photo
	 */
	private CameraType activeCamera;
	
	//
	// the outside camera's movie mode
	//
	
	/**
	 * direction of the rotation axis, which passes through the view centre, of the (outside) camera when in movie mode
	 */
	private Vector3D cameraRotationAxisDirection;
	
	//
	// the inside camera
	//
	
	/**
	 * the direction in which the camera looks out
	 */
	private Vector3D insideCameraViewDirection;
	
	/**
	 * the aperture-centre position of the inside camera
	 */
	private Vector3D insideCameraApertureCentre;
	
	/**
	 * focussing distance of the inside camera
	 */
	private double insideCameraFocussingDistance;
	
	/**
	 * the inside camera's horizontal field of view, in degrees
	 */
	private double insideCameraHorizontalFOVDeg;
	
	/**
	 * the inside camera's aperture size
	 */
	private ApertureSizeType insideCameraApertureSize;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensCloakVisualiser()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		activeCamera = CameraType.OUTSIDE;
		
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0.15, 0, 1);
		cameraDistance = 10;
		cameraHorizontalFOVDeg = 20;
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
//		 * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;
		
		movie = false;
		// if movie = true, then the following are relevant:
		numberOfFrames = 10;
		cameraRotationAxisDirection = new Vector3D(0, 1, 0);
		
		// inside-camera parameters
		insideCameraApertureCentre = new Vector3D(-0.1, -0.3, 0);
		insideCameraViewDirection = new Vector3D(0, 0, 1);
		insideCameraFocussingDistance = 10;
		insideCameraHorizontalFOVDeg = 20;
		insideCameraApertureSize = ApertureSizeType.PINHOLE;


		
		// blur quality if test = false; make this better in the non-interactive version, as this is used to produce publication-quality images
		// standardCameraBlurQuality = (interactive?QualityType.GOOD:QualityType.SUPER);	// this is the camera's blur quality if test=false

		// lens-cloak parameters
		
		topVertex = new Vector3D(0, 1, 0);//Vector3D(0, 1, 0);
		baseCentre = new Vector3D(0, -1, 0);//Vector3D(0, -1, 0);
		baseVertex1 = new Vector3D(1.5, -1, 0);//Vector3D(1.5, -1, 0);
		h1P = 0.2;
		h2P = 0.6;
		h1V = 0.4;
		baseLensF = calculateBaseLensF();
		lensTypeRepresentingFace = LensType.IDEAL_THIN_LENS;
		optimumOutsideSpaceViewingPosition = getStandardCameraPosition();
		showStructureP = false;
		structurePRadius = 0.021;
		showStructureV = false;
		structureVRadius = 0.02;
		
		// light-ray trajectory

		// first, switch of NonInteractiveTIM's automatic tracing of rays with trajectory, as this doesn't work
		traceRaysWithTrajectory = false;	// we do this ourselves
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0, -0.8, 0.01);
		trajectoryStartDirection = new Vector3D(1, 0, 0);
		trajectoryRadius = 0.025;
		trajectoryMaxTraceLevel = 100;

		
		// array of spheres
		
		showSpheresArray = false;
		radiusOfSpheresInArray = 0.05;
		spheresArrayXMin = -1;
		spheresArrayXMax = 1;
		spheresArrayDX = 0.2;
		spheresArrayYMin = -1;
		spheresArrayYMax = 1;
		spheresArrayDY = 0.2;
		spheresArrayZMin = 0;
		spheresArrayZMax = 0;
		spheresArrayDZ = 1;

		// other scene objects
		
		studioInitialisation = StudioInitialisationType.LATTICE;	// the backdrop
		indicateInsideCameraPosition = false;
		modelCameraWidth = 0.2;
		showPatternedSphere = true;
		patternedSphereCentre = new Vector3D(0, -0.75, 0);
		showVirtualSpacePositionOfPatternedSphereCentre = false;
		showOtherRealSpacePositionsOfPatternedSphereCentre = false;
		patternedSphereRadius = 0.05;

		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's lens-cloak visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getClassName()
	{
		return "LensCloakVisualiser";	// the name
//				+ " tV "+topVertex
//				+ " bC "+baseCentre
//				+ " bV1 "+baseVertex1
//				+ " h1P "+h1P
//				+ " h2P "+h2P
//				+ " h1V "+h1V
//				+ " lT "+lensTypeRepresentingFace
//				+ ((lensTypeRepresentingFace==LensType.POINT_2_POINT_IMAGING_HOLOGRAM)?" ovp "+optimumOutsideSpaceViewingPosition:"")
//				+ (showStructureP?" pss shown":"")
//				+ (showStructureV?" vss shown":"")
//				+ " camera "+activeCamera
//				+ ((activeCamera==CameraType.INSIDE)
//						?(
//								" ac "+insideCameraApertureCentre
//								+ " vd "+insideCameraViewDirection
//								+ " hFOV "+insideCameraHorizontalFOVDeg+"°"
//								+ " as "+insideCameraApertureSize
//								+ " fd "+insideCameraFocussingDistance
//						)
//						:(
//								" vc "+cameraViewCentre
//								+ " vd "+cameraViewDirection
//								+ " cd "+cameraDistance
//								+ " hFOV "+cameraHorizontalFOVDeg+"°"
//								+ " as "+cameraApertureSize
//								+ " fd "+cameraFocussingDistance
//						)
//				)
//				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);
		printStream.println("activeCamera ="+activeCamera);

		// outside camera movie parameters
		printStream.println("movie ="+movie);
		printStream.println("cameraRotationAxisDirection ="+cameraRotationAxisDirection);
		
		// inside-camera parameters
		printStream.println("insideCameraApertureCentre = "+insideCameraApertureCentre);
		printStream.println("insideCameraViewDirection = "+insideCameraViewDirection);
		printStream.println("insideCameraFocussingDistance = "+insideCameraFocussingDistance);
		printStream.println("insideCameraHorizontalFOVDeg = "+insideCameraHorizontalFOVDeg);
		printStream.println("insideCameraApertureSize = "+insideCameraApertureSize);

		// lens-cloak parameters
		printStream.println("topVertex = "+topVertex);
		printStream.println("baseCentre = "+baseCentre);
		printStream.println("baseVertex1 = "+baseVertex1);
		printStream.println("h1P = "+h1P);
		printStream.println("h2P = "+h2P);
		printStream.println("h1V = "+h1V);
		printStream.println("baseLensF = "+baseLensF);
		printStream.println("lensTypeRepresentingFace = "+lensTypeRepresentingFace);
		printStream.println("optimumOutsideSpaceViewingPosition = "+optimumOutsideSpaceViewingPosition);
		printStream.println("showStructureP = "+showStructureP);
		printStream.println("structurePRadius = "+structurePRadius);
		printStream.println("showStructureV = "+showStructureV);
		printStream.println("structureVRadius = "+structureVRadius);
		
		// light-ray trajectory
		printStream.println("showTrajectory = "+showTrajectory);
		printStream.println("trajectoryStartPoint = "+trajectoryStartPoint);
		printStream.println("trajectoryStartDirection = "+trajectoryStartDirection);
		printStream.println("trajectoryRadius = "+trajectoryRadius);
		printStream.println("trajectoryMaxTraceLevel = "+trajectoryMaxTraceLevel);

		// array of spheres
		printStream.println("showSpheresArray = "+showSpheresArray);
		printStream.println("radiusOfSpheresInArray = "+radiusOfSpheresInArray);
		printStream.println("spheresArrayXMin = "+spheresArrayXMin);
		printStream.println("spheresArrayXMax = "+spheresArrayXMax);
		printStream.println("spheresArrayDX = "+spheresArrayDX);
		printStream.println("spheresArrayYMin = "+spheresArrayYMin);
		printStream.println("spheresArrayYMax = "+spheresArrayYMax);
		printStream.println("spheresArrayDY = "+spheresArrayDY);
		printStream.println("spheresArrayZMin = "+spheresArrayZMin);
		printStream.println("spheresArrayZMax = "+spheresArrayZMax);
		printStream.println("spheresArrayDZ = "+spheresArrayDZ);

		// other scene objects
		
		printStream.println("studioInitialisation = "+studioInitialisation);	// the backdrop
		printStream.println("indicateInsideCameraPosition = "+indicateInsideCameraPosition);
		printStream.println("modelCameraWidth ="+modelCameraWidth);
		printStream.println("showPatternedSphere = "+showPatternedSphere);
		printStream.println("patternedSphereCentre = "+patternedSphereCentre);
		printStream.println("showVirtualSpacePositionOfPatternedSphereCentre = "+showVirtualSpacePositionOfPatternedSphereCentre);
		printStream.println("showOtherRealSpacePositionsOfPatternedSphereCentre = "+showOtherRealSpacePositionsOfPatternedSphereCentre);
		printStream.println("patternedSphereRadius = "+patternedSphereRadius);

	
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	private EditableLensSimplicialComplex lensSimplicialComplex;
	
//	/**
//	 * Map the given position into outside space.
//	 * If the position is inside one of the simplices of the simplicial complex, it is interpreted as inside the space of that simplex and mapped accordingly.
//	 * If the position happens to be outside of the simplicial complex, it is interpreted to be already in outside space.
//	 * @param position
//	 * @return	position, mapped into outside space (if necessary)
//	 */
//	public Vector3D mapToOutsideSpace(Vector3D position)
//	{
//		// ... and extract the simplicial complex
//		IdealThinLensSimplicialComplex lsc = lensSimplicialComplex.getLensSimplicialComplex();
//		
//		// now do the mapping as required
//		return lsc.mapToOutside(
//				lsc.getIndexOfSimplexContainingPosition(position),	// simplexIndex
//				position
//			);
//	}
	
	private Vector3D frame0CameraViewDirection;
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		// System.out.println("LensCloakVisualiser::populateStudio: frame="+frame);
		
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
				
		// the lens cloak
		
		// create a new lens simplicial complex...
		lensSimplicialComplex = new EditableLensSimplicialComplex(
				"Lens simplicial complex",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it first as an ideal-lens cloak, for the purposes of raytracing and calculating the image of
		// the optimum outside viewing position
		lensSimplicialComplex.setLensTypeRepresentingFace(LensType.IDEAL_THIN_LENS);
		lensSimplicialComplex.setShowStructureP(false);
		lensSimplicialComplex.setVertexRadiusP(structurePRadius);
		lensSimplicialComplex.setShowStructureV(false);
		lensSimplicialComplex.setVertexRadiusV(structureVRadius);
		
		// initialise for the first time, which sets the properties of the simplicial complex such that it can do the mapping to outside space...
		double h2V = lensSimplicialComplex.initialiseToOmnidirectionalLens(h1P, h2P, h1V, topVertex, baseCentre, baseVertex1);
		
		scene.addSceneObject(lensSimplicialComplex);

		if(showTrajectory)
		{
			// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							trajectoryStartPoint,	// startPoint
							0,	// startTime
							trajectoryStartDirection,	// startDirection
							trajectoryRadius,	// rayRadius
							SurfaceColourLightSourceIndependent.RED,	// surfaceProperty
							// SurfaceColour.RED_SHINY,	// surfaceProperty
							trajectoryMaxTraceLevel,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);

			// RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());
			studio.traceRaysWithTrajectory();
		}

		// then initialise the cloak again, this time according the parameters
		lensSimplicialComplex.setLensTypeRepresentingFace(lensTypeRepresentingFace);
		lensSimplicialComplex.setShowStructureP(showStructureP);
		lensSimplicialComplex.setShowStructureV(showStructureV);

		// ... required here, ...
		lensSimplicialComplex.setOptimumOutsideSpaceViewingPosition(
				lensSimplicialComplex.getLensSimplicialComplex().mapToOutsideSpace(optimumOutsideSpaceViewingPosition)
			);
		// ... then initialise again to add scene objects optimised for the correct optimum viewing position
		lensSimplicialComplex.initialiseToOmnidirectionalLens(h1P, h2P, h1V, h2V, topVertex, baseCentre, baseVertex1);
		
		// if(h2VDoublePanel != null) h2VDoublePanel.setNumber(h2V);
		if(h2VPanel != null) h2VPanel.setNumber(h2V);
		// System.out.println("LensCloakVisualiser:populateStudio: calculated h2V="+h2V);
		
//		Face face1 = lensSimplicialComplex.getFaceSurfaceProperty()
		// System.out.println(lensSimplicialComplex.getFaceSurfaceProperty());
		
		
		// the arrow indicating the position of the inside camera
//		if(activeCamera != CameraType.INSIDE)
//			scene.addSceneObject(
//					new EditableArrow(
//							"Arrow pointing to inside-camera position",
//							Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(-1)),	// start point
//							insideCameraApertureCentre,	// end point
//							0.05,	// shaft radius
//							0.2,	// tip length
//							MyMath.deg2rad(30),	// tip angle
//							new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),
//							scene, studio
//							),
//					indicateInsideCameraPosition	// visibility
//					);

		scene.addSceneObject(
				new EditableCameraShape(
						"Camera shape at position of inside camera",	// description
						Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(-MyMath.TINY)),	// apertureCentre
						insideCameraViewDirection,	// forwardDirection
						new Vector3D(0, 1, 0),	// topDirection
						modelCameraWidth,	// width
						SurfaceColour.GREY50_MATT,	// surfacePropertyBody
						SurfaceColour.GREY30_MATT,	// surfacePropertyLens
						null,	// surfacePropertyGlass
						scene,	// parent
						studio
				),
				indicateInsideCameraPosition	// visibility
			);

		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);
		
		// the virtual-space image of the patterned sphere
		Vector3D patternedSphereCentreV = lensSimplicialComplex.getLensSimplicialComplex().mapToOutsideSpace(patternedSphereCentre);
		if(showVirtualSpacePositionOfPatternedSphereCentre)
		{
			EditableScaledParametrisedSphere sphereAtPatternedSphereCentreV = new EditableScaledParametrisedSphere(
					"Virtual-space position of centre of patterned sphere", // description
					patternedSphereCentreV, // centre
					patternedSphereRadius,	// radius
					SurfaceColour.GREY50_SHINY,
					scene, studio);
			scene.addSceneObject(sphereAtPatternedSphereCentreV, showVirtualSpacePositionOfPatternedSphereCentre);
			System.out.println("Virtual-space position of centre of patterned sphere = "+patternedSphereCentreV);
		}

		// the position of the centre of the patterned sphere in the physical spaces of the other simplices
		if(showOtherRealSpacePositionsOfPatternedSphereCentre)
		for(int simplex=0; simplex<lensSimplicialComplex.getLensSimplicialComplex().getSimplices().size(); simplex++)
		{
			Vector3D p = lensSimplicialComplex.getLensSimplicialComplex().mapFromOutside(simplex, patternedSphereCentreV);
			scene.addSceneObject(
					new EditableScaledParametrisedSphere(
					"Position of centre of patterned sphere in physical space of simplex #"+simplex, // description
					p, // centre
					patternedSphereRadius*0.99,	// radius
					SurfaceColour.GREY20_SHINY,
					scene, studio
				)
			);
			System.out.println("Position of centre of patterned sphere in physical space of simplex #"+simplex+" = "+p);
		}
			
		

		// the array
		EditableSceneObjectCollection arrayUnitCell = new EditableSceneObjectCollection(
				"array unit cell",	// description
				false,	// combinationModeEditable
				scene,	// parent
				studio
			);
		arrayUnitCell.addSceneObject(
				new EditableScaledParametrisedSphere(
						"sphere in array",	// description
						new Vector3D(0, 0, 0),	// centre
						radiusOfSpheresInArray,	// radius
						new Vector3D(0, 0, 1),	// pole
						new Vector3D(1, 0, 0),	// phi0Direction
						0.5, .5001,	// sThetaMin, sThetaMax
						0, 1, 	// sPhiMin, sPhiMax
						new HueBrightnessGradientSurface(false),	//SurfaceColour.WHITE_SHINY,	// surfaceProperty
						arrayUnitCell,	// parent 
						studio
					)
			);

		scene.addSceneObject(
				new EditableArray(
						"array",	// description
						spheresArrayXMin, spheresArrayXMax, spheresArrayDX,	// xMin, double xMax, double dx,
						spheresArrayYMin, spheresArrayYMax, spheresArrayDY,	// yMin, double yMax, double dy,
						spheresArrayZMin, spheresArrayZMax, spheresArrayDZ,	// zMin, double zMax, double dz,
						arrayUnitCell.getSceneObjectContainer(),
						scene,	// parent
						studio
						),
				showSpheresArray
				);



		// the camera
		
		
		switch(activeCamera)
		{
		case INSIDE:
			studio.setCamera(
					new EditableRelativisticAnyFocusSurfaceCamera(
							"Inside camera",
							insideCameraApertureCentre,	// centre of aperture
							insideCameraViewDirection,	// viewDirection
							new Vector3D(0, 1, 0),	// top direction vector
							insideCameraHorizontalFOVDeg,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
							new Vector3D(0, 0, 0),	// beta
							cameraPixelsX, cameraPixelsY,	// logical number of pixels
							ExposureCompensationType.EC0,	// exposure compensation +0
							cameraMaxTraceLevel,	// maxTraceLevel
							new Plane(
									"focus plane",	// description
									Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(insideCameraFocussingDistance)),	// pointOnPlane
									insideCameraViewDirection,	// normal
									null,	// surfaceProperty
									null,	// parent
									null	// studio
								),	// focus scene
							null,	// cameraFrameScene,
							insideCameraApertureSize,	// aperture size
							renderQuality.getBlurQuality(),	// blur quality
							renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
						)
				);
			break;
		case OUTSIDE:
		default:
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
		}

		IdealThinLensSimplicialComplex extractedComplex = lensSimplicialComplex.getLensSimplicialComplex();
		Face face1 = extractedComplex.getFace(9);
		Face face2 = extractedComplex.getFace(0);
		EditableParametrisedTriangle refOfFace1 = (EditableParametrisedTriangle) extractedComplex.getSceneObjectRepresentingFace(9, 1, false, lensSimplicialComplex.getParent(), studio);
		EditableParametrisedTriangle refOfFace2 = (EditableParametrisedTriangle) extractedComplex.getSceneObjectRepresentingFace(0, 1, false, lensSimplicialComplex.getParent(), studio);

		
		//add frames to the faces controlled by checkbox.
		if(highlightFaces==true) {
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face1.getVertex(0),// startPoint,
					face1.getVertex(1),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face1.getVertex(1),// startPoint,
					face1.getVertex(2),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face1.getVertex(2),// startPoint,
					face1.getVertex(0),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face2.getVertex(0),// startPoint,
					face2.getVertex(1),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face2.getVertex(1),// startPoint,
					face2.getVertex(2),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
			scene.addSceneObject(new Cylinder(
					"one of the edges of face under consideration", //description,
					face2.getVertex(2),// startPoint,
					face2.getVertex(0),// endPoint,
					0.01,// radius,
					new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true),//  surfaceProperty,
					scene, // parent,
					studio
					));
		}

		


		Vector3D principalPoint1 = null;
		Vector3D principalPoint2 = null;

		if(!(lensTypeRepresentingFace==LensType.IDEAL_THIN_LENS)) {

		} else {
			SurfaceProperty surfProp1 = refOfFace1.getSurfaceProperty();
			SurfaceProperty surfProp2 = refOfFace2.getSurfaceProperty();

			if(surfProp1 instanceof IdealThinLensSurface && surfProp2 instanceof IdealThinLensSurface) {
				IdealThinLensSurface idealTLS1 = (IdealThinLensSurface)surfProp1;
				principalPoint1 = idealTLS1.getPrincipalPoint();

				IdealThinLensSurface idealTLS2 = (IdealThinLensSurface)surfProp2;
				principalPoint2 = idealTLS2.getPrincipalPoint();
			}
			if(showPrincipalPoints==true) {

				scene.addSceneObject(new Sphere(
						"the principal point of one of the faces under consideration", // description,
						principalPoint1, // centre,
						0.03, // radius,
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true), // surfaceProperty,
						scene, // parent,
						studio
						));
				scene.addSceneObject(new Sphere(
						"the principal point of one of the faces under consideration", // description,
						principalPoint2, // centre,
						0.03, // radius,
						new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true), // surfaceProperty,
						scene, // parent,
						studio
						));

			}
		}
		
		///////////////////////////

		if(lensTypeRepresentingFace == LensType.IDEAL_THIN_LENS) {

//			double focalLength1 = 0;
//			double focalLength2 = 0;
			//			Vector3D principalPoint1 = null;
			//			Vector3D principalPoint2 = null;

//			//all the trigonometric quantitites that will be needed for the computation of the point about which the view seems rotated i.e. the pointOfInterest
//			double adjacentToBothPhi = 1.5/2;
//			double oppositeToPhi1 = h1P*Vector3D.difference(topVertex,baseCentre).getLength();//Vector3D.difference(topVertex,baseCentre).getLength();
//			double oppositeToPhi2 = 0;//h2P*oppositeToPhi1; //Note h2P is fractionalUpperInnerVertexHeight and oppositeToPhi1 happens to be the total height of the simplicial complex
//			double hypotenuseToPhi1 = Math.sqrt(adjacentToBothPhi*adjacentToBothPhi + oppositeToPhi1*oppositeToPhi1);
//			double hypotenuseToPhi2 = Math.sqrt(adjacentToBothPhi*adjacentToBothPhi + oppositeToPhi2*oppositeToPhi2);
//			double separation =  oppositeToPhi1 - oppositeToPhi2; 

//			//			double sin1 = oppositeToPhi1/hypotenuseToPhi1;
//			//			double sin2 = oppositeToPhi2/hypotenuseToPhi2;
//			double cos1 = adjacentToBothPhi/hypotenuseToPhi1;
//			double cos2 = adjacentToBothPhi/hypotenuseToPhi2;
//			double tan1 = oppositeToPhi1/adjacentToBothPhi;
//			double tan2 = oppositeToPhi2/adjacentToBothPhi;

//			SurfaceProperty surfProp1 = refOfFace1.getSurfaceProperty();
//			SurfaceProperty surfProp2 = refOfFace2.getSurfaceProperty();
//			if(surfProp1 instanceof IdealThinLensSurface && surfProp2 instanceof IdealThinLensSurface) {
//				IdealThinLensSurface idealTLS1 = (IdealThinLensSurface)surfProp1;
//				focalLength1 = idealTLS1.getFocalLength();
//				principalPoint1 = idealTLS1.getPrincipalPoint();
//
//				IdealThinLensSurface idealTLS2 = (IdealThinLensSurface)surfProp2;
//				focalLength2 = idealTLS2.getFocalLength();
//				principalPoint2 = idealTLS2.getPrincipalPoint();
//			}

//			double g1 = focalLength1/cos1;
//			double g2 = focalLength2/cos2;
//			// double overallF = g1*g2/(g1 + g2 - separation);
//
//			double k1 = (separation - g1 - g2)/(tan1*(separation - g2 -g1*tan2/tan1));
			// double k2 = (separation - g1 - g2)/(tan2*(separation - g1 -g2*tan1/tan2));

			///////////////////////////
			// double magnification = Math.sqrt(1 + 1/(k2*k2)) / Math.sqrt(1 + 1/(k1*k1));
			// double zPos = -1.*overallF*(magnification*magnification + 1);
			// double zPosPrime = zPos*overallF/(zPos + overallF);
			// double zPrime = zPosPrime/Math.sqrt(1 + 1/(k1*k1));
			
			// double xComponent = -1.*overallF*(1/(k2 + 1/k2) - 1/(k1 + 1/k1));;
			// double yCoordinate = xComponent/k1 + separation - separation*overallF/g2 + principalPoint2.y;
			// double zCoordinate = 5;	

			// Vector3D somePoint = new Vector3D(xComponent, yCoordinate, zCoordinate);
//			Vector3D commonStart = new Vector3D(0.26483, 1.098, 5);//Vector3D.sum(somePoint, new Vector3D(-1/k1,1,0).getNormalised().getProductWith(zPrime));Vector3D(0.11371, 0.464, 5);//
//
//			ParametrisedCylinder cylinder1 = new ParametrisedCylinder(
//					"whatevs", // description,
//					commonStart, // startPoint, 
//					Vector3D.sum(commonStart, new Vector3D(k1,1,0).getNormalised()), // endPoint, 
//					0.05, 
//					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.BLACK, true), // surfaceProperty, 
//					scene, // parent, 
//					studio);
//			ParametrisedCylinder cylinder2 = new ParametrisedCylinder(
//					"whatevs", // description,
//					commonStart, // startPoint, 
//					Vector3D.sum(commonStart, new Vector3D(1,-k1,0).getNormalised()), // endPoint, 
//					0.05, 
//					new SurfaceColour(DoubleColour.GREEN, DoubleColour.BLACK, true), // surfaceProperty, 
//					scene, // parent, 
//					studio);
			// scene.addSceneObject(cylinder1);
			// scene.addSceneObject(cylinder2);
		}
	}
	
	private Vector3D getRotationPoint() {
		
		IdealThinLensSimplicialComplex extractedComplex = lensSimplicialComplex.getLensSimplicialComplex();

		if(lensTypeRepresentingFace == LensType.IDEAL_THIN_LENS) {

			EditableParametrisedTriangle refOfFace1 = (EditableParametrisedTriangle) extractedComplex.getSceneObjectRepresentingFace(9, 1, false, lensSimplicialComplex.getParent(), studio);
			EditableParametrisedTriangle refOfFace2 = (EditableParametrisedTriangle) extractedComplex.getSceneObjectRepresentingFace(0, 1, false, lensSimplicialComplex.getParent(), studio);

			double focalLength1 = 0;
			double focalLength2 = 0;
			// Vector3D principalPoint1 = null;
			Vector3D principalPoint2 = null;

			//all the trigonometric quantitites that will be needed for the computation of the point about which the view seems rotated i.e. the pointOfInterest
			double adjacentToBothPhi = 1.5/2;
			double oppositeToPhi1 = h1P*Vector3D.difference(topVertex,baseCentre).getLength();//Vector3D.difference(topVertex,baseCentre).getLength();
			double oppositeToPhi2 = 0;//h2P*oppositeToPhi1; //Note h2P is fractionalUpperInnerVertexHeight and oppositeToPhi1 happens to be the total height of the simplicial complex
			double hypotenuseToPhi1 = Math.sqrt(adjacentToBothPhi*adjacentToBothPhi + oppositeToPhi1*oppositeToPhi1);
			double hypotenuseToPhi2 = Math.sqrt(adjacentToBothPhi*adjacentToBothPhi + oppositeToPhi2*oppositeToPhi2);
			double separation =  oppositeToPhi1 - oppositeToPhi2; 

//			double sin1 = oppositeToPhi1/hypotenuseToPhi1;
//			double sin2 = oppositeToPhi2/hypotenuseToPhi2;
			double cos1 = adjacentToBothPhi/hypotenuseToPhi1;
			double cos2 = adjacentToBothPhi/hypotenuseToPhi2;
			double tan1 = oppositeToPhi1/adjacentToBothPhi;
			double tan2 = oppositeToPhi2/adjacentToBothPhi;

			SurfaceProperty surfProp1 = refOfFace1.getSurfaceProperty();
			SurfaceProperty surfProp2 = refOfFace2.getSurfaceProperty();
			if(surfProp1 instanceof IdealThinLensSurface && surfProp2 instanceof IdealThinLensSurface) {
				IdealThinLensSurface idealTLS1 = (IdealThinLensSurface)surfProp1;
				focalLength1 = idealTLS1.getFocalLength();
				// principalPoint1 = idealTLS1.getPrincipalPoint();

				IdealThinLensSurface idealTLS2 = (IdealThinLensSurface)surfProp2;
				focalLength2 = idealTLS2.getFocalLength();
				principalPoint2 = idealTLS2.getPrincipalPoint();
			}

			double g1 = focalLength1/cos1;
			double g2 = focalLength2/cos2;
			double overallF = g1*g2/(g1 + g2 - separation);

			double k1 = (separation - g1 - g2)/(tan1*(separation - g2 -g1*tan2/tan1));
			double k2 = (separation - g1 - g2)/(-g2*tan1);
			
			double magnification = Math.sqrt(1 + 1/(k2*k2)) / Math.sqrt(1 + 1/(k1*k1));
			double zPos = -1.*overallF*(magnification*magnification*magnification + 1)/Math.sqrt(1 + 1/(k2*k2));
			double xComponent = -1.*overallF*(1/(k2 + 1/k2) - 1/(k1 + 1/k1));
			double yComponent = principalPoint2.y + separation*overallF/g1 + xComponent/k2; 
			double zComponent = 0;

			Vector3D oldPointOfInterest = new Vector3D(xComponent, yComponent, zComponent);
			pointOfInterest = Vector3D.sum(oldPointOfInterest, new Vector3D(-1./k2, 1, 0).getNormalised().getProductWith(zPos));
			System.out.println("f1 = "+focalLength1);
			System.out.println("f2 = "+focalLength2);
			System.out.println("D = "+separation);
			System.out.println("h1 = "+oppositeToPhi1);
			System.out.println("h2 = "+oppositeToPhi2);
			System.out.println("overall F = "+overallF);
			System.out.println("k1 = "+k1);
			System.out.println("k2 = "+k2);

			
		}
		return pointOfInterest;
		
	}
	
	/**
	 * from h1P and h1V, calculate baseLensF
	 * @return	baseLensF
	 */
	private double calculateBaseLensF()
	{
		// 1/(h1P h) + 1/(-h1V h) == 1/f
		
		// the overall height of the cloak
		double h = Vector3D.getDistance(topVertex, baseCentre);
		return 1./(1/(h1P*h) - 1/(h1V*h));
	}
	
	/**
	 * from h1P and baseLensF, calculate h1V
	 * @return	h1V
	 */
	private double calculateH1V()
	{
		// 1/(h1P h) + 1/(-h1V h) == 1/f

		// the overall height of the cloak
		double h = Vector3D.getDistance(topVertex, baseCentre);
		return 1./(-h/baseLensF + 1/h1P);
	}
	
	
	
	
	//
	// for interactive version
	//
	
//	private LabelledVector2DPanel h1Panel;
//	private LabelledVector2DPanel h2Panel;
	private LabelledDoublePanel h1PPanel, h1VPanel, h2PPanel, h2VPanel, baseLensFPanel;
	private JTabbedPane baseLensTabbedPane;
// 	private DoublePanel h2VDoublePanel;
	private LabelledVector3DPanel topVertexPanel;
	private LabelledVector3DPanel baseCentrePanel;
	private LabelledVector3DPanel baseVertex1Panel;
	private LabelledVector3DPanel optimumOutsideSpaceViewingPositionPanel;
	private JButton setOptimumOutsideSpaceViewingPosition2CameraPositionButton;
	private JButton setOptimumOutsideSpaceViewingPosition2PatternedSphereCentreButton;
	private JButton setCameraApertureCentreToPointOfInterestButton;
	private JComboBox<LensType> lensTypeRepresentingFaceComboBox;
	private JCheckBox showStructurePCheckBox, showStructureVCheckBox, highlightFacesCheckBox, showPrincipalPointsCheckBox;
	private DoublePanel structurePRadiusPanel, structureVRadiusPanel;
	
	// light-ray trajectory
	private JCheckBox showTrajectoryCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel;
	private LabelledVector3DPanel trajectoryStartDirectionPanel;
	private JButton setTrajectoryAccordingToInsideCamera;
	private LabelledDoublePanel	trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;


	// array of spheres
	private JCheckBox showSpheresArrayCheckBox;
	private LabelledDoublePanel radiusOfSpheresInArrayPanel;
	private LabelledDoublePanel
		spheresArrayXMinPanel, spheresArrayXMaxPanel, spheresArrayDXPanel,
		spheresArrayYMinPanel, spheresArrayYMaxPanel, spheresArrayDYPanel,
		spheresArrayZMinPanel, spheresArrayZMaxPanel, spheresArrayDZPanel;

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox indicateInsideCameraPositionCheckBox;
	private DoublePanel modelCameraWidthPanel;
	private JCheckBox showPatternedSphereCheckBox, showVirtualSpacePositionOfPatternedSphereCentreCheckBox, showOtherRealSpacePositionsOfPatternedSphereCentreCheckBox;
	private JButton setPatternedSphereCentreToInsideCameraPosition;
	private LabelledVector3DPanel patternedSphereCentrePanel;
	private JTextField patternedSphereCentreInfoTextField;
	private JButton patternedSphereCentreInfoButton;
	private LabelledDoublePanel patternedSphereRadiusPanel;

	
	// cameras
	private JComboBox<CameraType> activeCameraComboBox;

	// main (outside) camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	private JButton setCameraViewCentreToCloakCentroidButton;
	private JButton setCameraViewCentreToPatternedSphereCentreButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;
	private JCheckBox movieCheckBox;
	private LabelledVector3DPanel cameraRotationAxisDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;

	// inside camera
	private LabelledVector3DPanel insideCameraApertureCentrePanel;
	private JTextField insideCameraApertureCentreInfoTextField;
	private JTextField pointOfInterestTextField;
	private JButton updateInsideCameraApertureCentreInfoButton;
	private LabelledVector3DPanel insideCameraViewDirectionPanel;
	private DoublePanel insideCameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> insideCameraApertureSizeComboBox;
	private LabelledDoublePanel insideCameraFocussingDistancePanel;


	
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
		
		//
		// lens-cloak panel
		//
		
		JPanel lensCloakPanel = new JPanel();
		lensCloakPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lens cloak", lensCloakPanel);
		
		topVertexPanel = new LabelledVector3DPanel("Top vertex");
		topVertexPanel.setVector3D(topVertex);
		lensCloakPanel.add(topVertexPanel, "span");
		
		baseCentrePanel = new LabelledVector3DPanel("Base centre");
		baseCentrePanel.setVector3D(baseCentre);
		lensCloakPanel.add(baseCentrePanel, "span");
		
		baseVertex1Panel = new LabelledVector3DPanel("Base vertex 1");
		baseVertex1Panel.setVector3D(baseVertex1);
		lensCloakPanel.add(baseVertex1Panel, "span");

		lensCloakPanel.add(new JLabel("Heights above the base centre of..."), "span");

		h1PPanel = new LabelledDoublePanel("  ... the lower inner vertex in physical space");
		h1PPanel.setNumber(h1P);
		lensCloakPanel.add(h1PPanel, "span");

		h2PPanel = new LabelledDoublePanel("  ... the upper inner vertex in physical space");
		h2PPanel.setNumber(h2P);
		lensCloakPanel.add(h2PPanel, "span");
		
		baseLensTabbedPane = new JTabbedPane();
		lensCloakPanel.add(baseLensTabbedPane, "span");
		
		JPanel baseLensVirtualHeightsPanel = new JPanel();
		baseLensVirtualHeightsPanel.setLayout(new MigLayout("insets 0"));
		baseLensTabbedPane.addTab("Heights in virtual space", baseLensVirtualHeightsPanel);

		h1VPanel = new LabelledDoublePanel("  ... the lower inner vertex in virtual space");
		h1VPanel.setNumber(h1V);
		baseLensVirtualHeightsPanel.add(h1VPanel, "span");

		h2VPanel = new LabelledDoublePanel("  ... the upper inner vertex in virtual space");
		h2VPanel.setNumber(0);
		h2VPanel.setEnabled(false);
		h2VPanel.setToolTipText("This parameter is calculated at render time from the other parameters");
		baseLensVirtualHeightsPanel.add(h2VPanel, "span");
		
		baseLensFPanel = new LabelledDoublePanel("f");
		baseLensFPanel.setNumber(baseLensF);
		baseLensTabbedPane.add("Focal length of the base lens", baseLensFPanel);
		
		baseLensTabbedPane.addChangeListener(this);


//		h1Panel = new LabelledVector2DPanel("  ... the lower inner vertex (physical space, virtual space)");
//		h1Panel.setVector2D(h1P, h1V);
//		lensCloakPanel.add(h1Panel, "span");
//
//		h2Panel = new LabelledVector2DPanel("  ... the upper inner vertex (physical space, virtual space)");
//		h2VDoublePanel = h2Panel.getVector2DPanel().getyPanel();
//		// h2VDoublePanel.setEditable(false);	// the virtual-space component is not editable
//		h2VDoublePanel.setEnabled(false);
//		// h2VDoublePanel.setBackground(Color.GRAY);
//		h2VDoublePanel.setToolTipText("This parameter is calculated at render time from the other parameters");
//		h2Panel.setVector2D(h2P, 0);
//		lensCloakPanel.add(h2Panel, "span");
		
		lensCloakPanel.add(new JLabel("as a fraction of the overall height (i.e. the distance from the base centre to the tip)"), "span");

		lensTypeRepresentingFaceComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeRepresentingFaceComboBox.setSelectedItem(lensTypeRepresentingFace);
		lensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeRepresentingFaceComboBox), "span");
		
		JPanel optimumOutsideSpaceViewingPositionJPanel = new JPanel();
		optimumOutsideSpaceViewingPositionJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Optimise for viewing position"));
		optimumOutsideSpaceViewingPositionJPanel.setLayout(new MigLayout("insets 0"));
		lensCloakPanel.add(optimumOutsideSpaceViewingPositionJPanel, "span");
		
		optimumOutsideSpaceViewingPositionPanel = new LabelledVector3DPanel("Optimum viewing position");
		optimumOutsideSpaceViewingPositionPanel.setVector3D(optimumOutsideSpaceViewingPosition);
		optimumOutsideSpaceViewingPositionPanel.setToolTipText("Not relevant if the cloak consists of ideal thin lenses");
		optimumOutsideSpaceViewingPositionJPanel.add(optimumOutsideSpaceViewingPositionPanel, "span");
		
		setOptimumOutsideSpaceViewingPosition2CameraPositionButton = new JButton("Set to camera position");
		setOptimumOutsideSpaceViewingPosition2CameraPositionButton.addActionListener(this);
		
		setOptimumOutsideSpaceViewingPosition2PatternedSphereCentreButton = new JButton("Set to centre of patterned sphere");
		setOptimumOutsideSpaceViewingPosition2PatternedSphereCentreButton.addActionListener(this);
		
		optimumOutsideSpaceViewingPositionJPanel.add(
				GUIBitsAndBobs.makeRow(
						setOptimumOutsideSpaceViewingPosition2CameraPositionButton,
						setOptimumOutsideSpaceViewingPosition2PatternedSphereCentreButton
				),
				"span"
			);
		
		JPanel lensesPanel = new JPanel(); //////
		lensesPanel.setBorder(GUIBitsAndBobs.getTitledBorder("The two lenses under consideration"));
		lensesPanel.setLayout(new MigLayout("insets 2"));
		lensCloakPanel.add(lensesPanel, "span");
		
		highlightFacesCheckBox = new JCheckBox("Highlight faces");
		highlightFacesCheckBox.setSelected(highlightFaces);
		lensesPanel.add(highlightFacesCheckBox);
		
		showPrincipalPointsCheckBox = new JCheckBox("Show principal points");
		showPrincipalPointsCheckBox.setSelected(showPrincipalPoints);
		lensesPanel.add(showPrincipalPointsCheckBox);
		
		showStructurePCheckBox = new JCheckBox("Show physical-space structure, tube radius");
		showStructurePCheckBox.setSelected(showStructureP);
		structurePRadiusPanel = new DoublePanel();
		structurePRadiusPanel.setNumber(structurePRadius);
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(showStructurePCheckBox, structurePRadiusPanel), "span");

		showStructureVCheckBox = new JCheckBox("Show virtual-space structure, tube radius");
		showStructureVCheckBox.setSelected(showStructureV);
		structureVRadiusPanel = new DoublePanel();
		structureVRadiusPanel.setNumber(structureVRadius);
		lensCloakPanel.add(GUIBitsAndBobs.makeRow(showStructureVCheckBox, structureVRadiusPanel), "span");
		
		//
		// Light-ray trajectories
		//
		
		JPanel trajectoryPanel = new JPanel();
		trajectoryPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Trajectory", trajectoryPanel);

		showTrajectoryCheckBox = new JCheckBox("Show trajectory");
		showTrajectoryCheckBox.setSelected(showTrajectory);
		trajectoryPanel.add(showTrajectoryCheckBox, "wrap");

		trajectoryStartPointPanel = new LabelledVector3DPanel("Start position");
		trajectoryStartPointPanel.setVector3D(trajectoryStartPoint);
		trajectoryPanel.add(trajectoryStartPointPanel, "span");

		trajectoryStartDirectionPanel = new LabelledVector3DPanel("Initial direction");
		trajectoryStartDirectionPanel.setVector3D(trajectoryStartDirection);
		trajectoryPanel.add(trajectoryStartDirectionPanel, "span");

		setTrajectoryAccordingToInsideCamera = new JButton("Set to inside camera position and view direction");
		setTrajectoryAccordingToInsideCamera.addActionListener(this);
		trajectoryPanel.add(setTrajectoryAccordingToInsideCamera, "span");
				
		trajectoryRadiusPanel = new LabelledDoublePanel("Trajectory radius");
		trajectoryRadiusPanel.setNumber(trajectoryRadius);
		trajectoryPanel.add(trajectoryRadiusPanel, "span");
		
		trajectoryMaxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		trajectoryMaxTraceLevelPanel.setNumber(trajectoryMaxTraceLevel);
		trajectoryPanel.add(trajectoryMaxTraceLevelPanel, "span");
		
		
		//
		// Array of spheres
		//
		
		JPanel spheresArrayPanel = new JPanel();
		spheresArrayPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Array of spheres", spheresArrayPanel);

		showSpheresArrayCheckBox = new JCheckBox("Show array of spheres");
		showSpheresArrayCheckBox.setSelected(showSpheresArray);
		spheresArrayPanel.add(showSpheresArrayCheckBox, "wrap");
		
		radiusOfSpheresInArrayPanel = new LabelledDoublePanel("Radius of spheres in array");
		radiusOfSpheresInArrayPanel.setNumber(radiusOfSpheresInArray);
		spheresArrayPanel.add(radiusOfSpheresInArrayPanel, "wrap");
		
		// x axis
		
		spheresArrayXMinPanel = new LabelledDoublePanel("x_min");
		spheresArrayXMinPanel.setNumber(spheresArrayXMin);
		spheresArrayPanel.add(spheresArrayXMinPanel, "split 3");
		
		spheresArrayXMaxPanel = new LabelledDoublePanel(", x_max");
		spheresArrayXMaxPanel.setNumber(spheresArrayXMax);
		spheresArrayPanel.add(spheresArrayXMaxPanel);
		
		spheresArrayDXPanel = new LabelledDoublePanel(", dx");
		spheresArrayDXPanel.setNumber(spheresArrayDX);
		spheresArrayPanel.add(spheresArrayDXPanel, "wrap");
		
		// y axis
		
		spheresArrayYMinPanel = new LabelledDoublePanel("y_min");
		spheresArrayYMinPanel.setNumber(spheresArrayYMin);
		spheresArrayPanel.add(spheresArrayYMinPanel, "split 3");
		
		spheresArrayYMaxPanel = new LabelledDoublePanel(", y_max");
		spheresArrayYMaxPanel.setNumber(spheresArrayYMax);
		spheresArrayPanel.add(spheresArrayYMaxPanel);
		
		spheresArrayDYPanel = new LabelledDoublePanel(", dy");
		spheresArrayDYPanel.setNumber(spheresArrayDY);
		spheresArrayPanel.add(spheresArrayDYPanel, "wrap");

		// z axis
		
		spheresArrayZMinPanel = new LabelledDoublePanel("z_min");
		spheresArrayZMinPanel.setNumber(spheresArrayZMin);
		spheresArrayPanel.add(spheresArrayZMinPanel, "split 3");
		
		spheresArrayZMaxPanel = new LabelledDoublePanel(", z_max");
		spheresArrayZMaxPanel.setNumber(spheresArrayZMax);
		spheresArrayPanel.add(spheresArrayZMaxPanel);
		
		spheresArrayDZPanel = new LabelledDoublePanel(", dz");
		spheresArrayDZPanel.setNumber(spheresArrayDZ);
		spheresArrayPanel.add(spheresArrayDZPanel, "wrap");

		
		//
		// Other scene-objects panel
		//
		
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		indicateInsideCameraPositionCheckBox = new JCheckBox("Indicate inside-camera position by a model camera of width");
		// indicateInsideCameraPositionCheckBox.setToolTipText("The inside-camera position is indicated by the tip of an arrow with direction (-insideCameraViewDirection)");
		indicateInsideCameraPositionCheckBox.setSelected(indicateInsideCameraPosition);
		modelCameraWidthPanel = new DoublePanel();
		modelCameraWidthPanel.setNumber(modelCameraWidth);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow(indicateInsideCameraPositionCheckBox, modelCameraWidthPanel), "span");
		
		JPanel patternedSpherePanel = new JPanel();
		patternedSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Patterned sphere"));
		patternedSpherePanel.setLayout(new MigLayout("insets 0"));
		otherObjectsPanel.add(patternedSpherePanel, "span");
		
		showPatternedSphereCheckBox = new JCheckBox("Show");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		patternedSpherePanel.add(showPatternedSphereCheckBox, "span");
		
		patternedSphereCentrePanel = new LabelledVector3DPanel("Centre");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		setPatternedSphereCentreToInsideCameraPosition = new JButton("Set to inside camera position");
		setPatternedSphereCentreToInsideCameraPosition.addActionListener(this);
		patternedSpherePanel.add(GUIBitsAndBobs.makeRow(patternedSphereCentrePanel, setPatternedSphereCentreToInsideCameraPosition), "span");

		patternedSphereCentreInfoTextField = new JTextField(40);
		patternedSphereCentreInfoTextField.setEditable(false);
		patternedSphereCentreInfoTextField.setText("Click on Update button to show info");
		patternedSphereCentreInfoButton = new JButton("Update");
		patternedSphereCentreInfoButton.addActionListener(this);
		patternedSpherePanel.add(GUIBitsAndBobs.makeRow(patternedSphereCentreInfoTextField, patternedSphereCentreInfoButton), "span");
		
		showVirtualSpacePositionOfPatternedSphereCentreCheckBox = new JCheckBox("Show virtual-space position of centre");
		showVirtualSpacePositionOfPatternedSphereCentreCheckBox.setSelected(showVirtualSpacePositionOfPatternedSphereCentre);
		patternedSpherePanel.add(showVirtualSpacePositionOfPatternedSphereCentreCheckBox, "span");
		
		showOtherRealSpacePositionsOfPatternedSphereCentreCheckBox = new JCheckBox("Show position of centre in (physical) spaces of other simplices");
		showOtherRealSpacePositionsOfPatternedSphereCentreCheckBox.setSelected(showOtherRealSpacePositionsOfPatternedSphereCentre);
		patternedSpherePanel.add(showOtherRealSpacePositionsOfPatternedSphereCentreCheckBox, "span");
		
		
		patternedSphereRadiusPanel = new LabelledDoublePanel("Radius");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		patternedSpherePanel.add(patternedSphereRadiusPanel, "span");

		
		//
		// cameras panel
		//
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cameras", cameraPanel);
		
		activeCameraComboBox = new JComboBox<CameraType>(CameraType.values());
		activeCameraComboBox.setSelectedItem(activeCamera);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Active camera", activeCameraComboBox), "span");

		JTabbedPane camerasTabbedPane = new JTabbedPane();
		cameraPanel.add(camerasTabbedPane, "span");

		
		// main (outside) camera
		
		JPanel outsideCameraPanel = new JPanel();
		outsideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Outside camera", outsideCameraPanel);
		
		JPanel cameraViewCentreJPanel = new JPanel();
		cameraViewCentreJPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Centre of view"));
		cameraViewCentreJPanel.setLayout(new MigLayout("insets 0"));
		outsideCameraPanel.add(cameraViewCentreJPanel, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Position");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraViewCentreJPanel.add(cameraViewCentrePanel, "span");
		
		setCameraViewCentreToCloakCentroidButton = new JButton("Set to cloak centroid");
		setCameraViewCentreToCloakCentroidButton.addActionListener(this);
		
		setCameraViewCentreToPatternedSphereCentreButton = new JButton("Set to centre of patterned sphere");
		setCameraViewCentreToPatternedSphereCentreButton.addActionListener(this);
		
		cameraViewCentreJPanel.add(
				GUIBitsAndBobs.makeRow(
						setCameraViewCentreToCloakCentroidButton,
						setCameraViewCentreToPatternedSphereCentreButton
				),
				"span"
			);
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		outsideCameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		outsideCameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		outsideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		outsideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		outsideCameraPanel.add(cameraFocussingDistancePanel, "span");
		
		// movie panel
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		outsideCameraPanel.add(moviePanel, "span");

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
				
		
		// inside camera
		
		JPanel insideCameraPanel = new JPanel();
		insideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Inside camera", insideCameraPanel);
		
		insideCameraApertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		insideCameraApertureCentrePanel.setVector3D(insideCameraApertureCentre);
		insideCameraPanel.add(insideCameraApertureCentrePanel, "span");
		
		insideCameraApertureCentreInfoTextField = new JTextField(40);
		insideCameraApertureCentreInfoTextField.setEditable(false);
		insideCameraApertureCentreInfoTextField.setText("Click on Update button to show info");
		updateInsideCameraApertureCentreInfoButton = new JButton("Update");
		updateInsideCameraApertureCentreInfoButton.addActionListener(this);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow(insideCameraApertureCentreInfoTextField, updateInsideCameraApertureCentreInfoButton), "span");
		
		insideCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		insideCameraViewDirectionPanel.setVector3D(insideCameraViewDirection);
		insideCameraPanel.add(insideCameraViewDirectionPanel, "span");
		
		insideCameraHorizontalFOVDegPanel = new DoublePanel();
		insideCameraHorizontalFOVDegPanel.setNumber(insideCameraHorizontalFOVDeg);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", insideCameraHorizontalFOVDegPanel, "°"), "span");
		
		insideCameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		insideCameraApertureSizeComboBox.setSelectedItem(insideCameraApertureSize);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", insideCameraApertureSizeComboBox), "span");		
		
		insideCameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		insideCameraFocussingDistancePanel.setNumber(insideCameraFocussingDistance);
		insideCameraPanel.add(insideCameraFocussingDistancePanel, "span");
		
		pointOfInterestTextField = new JTextField(20);
		pointOfInterestTextField.setEditable(false);
//		pointOfInterestTextField.setText(getRotationPoint().toString());
		insideCameraPanel.add(pointOfInterestTextField, "span");

		setCameraApertureCentreToPointOfInterestButton = new JButton("Set to point about which image rotates");
		setCameraApertureCentreToPointOfInterestButton.addActionListener(this);
		insideCameraPanel.add(setCameraApertureCentreToPointOfInterestButton);
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		topVertex = topVertexPanel.getVector3D();
		baseCentre = baseCentrePanel.getVector3D();
		baseVertex1 = baseVertex1Panel.getVector3D();
		h1P = h1PPanel.getNumber();	// h1Panel.getVector2D().x;
		h2P = h2PPanel.getNumber();	// h2Panel.getVector2D().x;
		switch(baseLensTabbedPane.getSelectedIndex())
		{
		case 0:
			// selected tab is virtual-space-heights tab
			h1V = h1VPanel.getNumber();	// h1Panel.getVector2D().y;
			baseLensF = calculateBaseLensF();
			break;
		case 1:
		default:
			// current tab is focal-length tab
			baseLensF = baseLensFPanel.getNumber();
			h1V = calculateH1V();
		}
		lensTypeRepresentingFace = (LensType)(lensTypeRepresentingFaceComboBox.getSelectedItem());
		optimumOutsideSpaceViewingPosition = optimumOutsideSpaceViewingPositionPanel.getVector3D();
		showStructureP = showStructurePCheckBox.isSelected();
		structurePRadius = structurePRadiusPanel.getNumber();
		showStructureV = showStructureVCheckBox.isSelected();
		structureVRadius = structureVRadiusPanel.getNumber();
		highlightFaces = highlightFacesCheckBox.isSelected();
		showPrincipalPoints = showPrincipalPointsCheckBox.isSelected();
		
		// trajectory
		
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();

		// array of spheres
		
		showSpheresArray = showSpheresArrayCheckBox.isSelected();
		radiusOfSpheresInArray = radiusOfSpheresInArrayPanel.getNumber();
		spheresArrayXMin = spheresArrayXMinPanel.getNumber();
		spheresArrayXMax = spheresArrayXMaxPanel.getNumber();
		spheresArrayDX = spheresArrayDXPanel.getNumber();
		spheresArrayYMin = spheresArrayYMinPanel.getNumber();
		spheresArrayYMax = spheresArrayYMaxPanel.getNumber();
		spheresArrayDY = spheresArrayDYPanel.getNumber();
		spheresArrayZMin = spheresArrayZMinPanel.getNumber();
		spheresArrayZMax = spheresArrayZMaxPanel.getNumber();
		spheresArrayDZ = spheresArrayDZPanel.getNumber();

		
		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		indicateInsideCameraPosition = indicateInsideCameraPositionCheckBox.isSelected();
		modelCameraWidth = modelCameraWidthPanel.getNumber();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
		showVirtualSpacePositionOfPatternedSphereCentre = showVirtualSpacePositionOfPatternedSphereCentreCheckBox.isSelected();
		showOtherRealSpacePositionsOfPatternedSphereCentre = showOtherRealSpacePositionsOfPatternedSphereCentreCheckBox.isSelected();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
		
		// cameras
		activeCamera = (CameraType)(activeCameraComboBox.getSelectedItem());

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
		// System.out.println("LensCloakVisualiser::acceptValuesInInteractiveControlPanel: firstFrame =" + firstFrame);
		
		insideCameraApertureCentre = insideCameraApertureCentrePanel.getVector3D();
		insideCameraViewDirection = insideCameraViewDirectionPanel.getVector3D();
		insideCameraHorizontalFOVDeg = insideCameraHorizontalFOVDegPanel.getNumber();
		insideCameraApertureSize = (ApertureSizeType)(insideCameraApertureSizeComboBox.getSelectedItem());
		insideCameraFocussingDistance = insideCameraFocussingDistancePanel.getNumber();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(setOptimumOutsideSpaceViewingPosition2CameraPositionButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			switch(activeCamera)
			{
			case INSIDE:
				optimumOutsideSpaceViewingPositionPanel.setVector3D(insideCameraApertureCentre);
				break;
			case OUTSIDE:
			default:
				optimumOutsideSpaceViewingPositionPanel.setVector3D(getStandardCameraPosition());
			}
		}
		else if(e.getSource().equals(setOptimumOutsideSpaceViewingPosition2PatternedSphereCentreButton))
		{
			optimumOutsideSpaceViewingPositionPanel.setVector3D(patternedSphereCentre);
		}
		else if(e.getSource().equals(setCameraViewCentreToCloakCentroidButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			// make sure lensSimplicialComplex is defined
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}

			cameraViewCentrePanel.setVector3D(lensSimplicialComplex.getLensSimplicialComplex().calculateCentroidOfSurface());
		}	
		else if(e.getSource().equals(setCameraViewCentreToPatternedSphereCentreButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			// make sure lensSimplicialComplex is defined
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}

			// map the position of the patterned-sphere centre to outside space, so that the patterned sphere, when seen through the cloak, appears centred
			cameraViewCentrePanel.setVector3D(lensSimplicialComplex.getLensSimplicialComplex().mapToOutsideSpace(patternedSphereCentre));
		}

		else if(e.getSource().equals(updateInsideCameraApertureCentreInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}
			insideCameraApertureCentreInfoTextField.setText(
					"Inside camera is centred in simplex #"+
							lensSimplicialComplex.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(insideCameraApertureCentre)
						);
		}
		else if(e.getSource().equals(patternedSphereCentreInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}
			patternedSphereCentreInfoTextField.setText(
					"in simplex #"+
					lensSimplicialComplex.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(patternedSphereCentre)+
					"; virtual-space position = "+
					lensSimplicialComplex.getLensSimplicialComplex().mapToOutsideSpace(patternedSphereCentre)
				);

		}
		else if(e.getSource().equals(setCameraApertureCentreToPointOfInterestButton))
		{
			acceptValuesInInteractiveControlPanel();
			
			// make sure lensSimplicialComplex is defined
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}
			insideCameraApertureCentrePanel.setVector3D(getRotationPoint());
		}
		else if(e.getSource().equals(setTrajectoryAccordingToInsideCamera))
		{
			trajectoryStartPointPanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
			trajectoryStartDirectionPanel.setVector3D(insideCameraViewDirectionPanel.getVector3D());
		}
		else if(e.getSource().equals(setPatternedSphereCentreToInsideCameraPosition))
		{
			patternedSphereCentrePanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
		}
		else super.actionPerformed(e);
	}



	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource().equals(baseLensTabbedPane))
		{
			// System.out.println("LensCloakVisualiser::stateChanged: baseLensTabbedPane.getSelectedIndex()="+baseLensTabbedPane.getSelectedIndex());
			// something has changed in the base-lens tabbed pane, most likely a different tab has been selected

			acceptValuesInInteractiveControlPanel();
			switch(baseLensTabbedPane.getSelectedIndex())
			{
			case 0:
				// new tab is virtual-space-heights tab
				baseLensF = baseLensFPanel.getNumber();
				h1VPanel.setNumber(calculateH1V());
				// set h2V
				try {
					populateStudio();
				} catch (SceneException e1) {
					e1.printStackTrace();
				}
				break;
			case 1:
			default:
				// new tab is focal-length tab
				h1V = h1VPanel.getNumber();	// h1Panel.getVector2D().y;
				baseLensFPanel.setNumber(calculateBaseLensF());
			}
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
		(new LensCloakVisualiser()).run();
	}
}
