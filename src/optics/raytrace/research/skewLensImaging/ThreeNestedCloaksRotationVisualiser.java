package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * Altered version of the TwoNestedCloaksRotationVisualiser, which hopefully works!
 * The idea is to look *sideways* through a combination of three lenses that rotates the image, such that the image is rotated around the view direction
 * 
 * @author Johannes Courtial, Katie Bear, Jakub Belin
 */
public class ThreeNestedCloaksRotationVisualiser extends NonInteractiveTIMEngine
{
	// parameters
	private boolean showInner, showMiddle, showOuter, showLensesInner, showLensesMiddle, showLensesOuter, showFramesInner, showFramesMiddle, showFramesOuter;
	private Vector3D p1;
	private double hInner = 0.7;
	private double h12hInner = 0.5;
	private double h22hInner = 0.75;
	private double r2hInner = 2;
	private double d;
	private double psi1Deg;
	private double psi2Deg;
	private double deltaPhiDeg;
	
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
	// the rest of the scene
	//
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	/**
	 * If true, draw an arrow with its tip at the position of the inside-camera position, pointing in the direction the camera views
	 */
	private boolean indicateInsideCameraPosition;
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
	public ThreeNestedCloaksRotationVisualiser()
	{
		super();
		showInner = true;
		showMiddle = true;
		showOuter = true;
		showLensesInner = false;
		showLensesMiddle = false;
		showLensesOuter = false;
		showFramesInner = true;
		showFramesMiddle = true;
		showFramesOuter = true;
		p1 = new Vector3D(0, 0, 0);
		h12hInner = 0.3;
		hInner = 0.7;
		r2hInner = 2;
		h22hInner = 0.452;
		d = 1;
		psi1Deg = 5;
		psi2Deg = -5;
		deltaPhiDeg = -30;

		
		// light-ray trajectories
		// first, switch of NonInteractiveTIM's automatic tracing of rays with trajectory, as this doesn't work
		traceRaysWithTrajectory = false;
		// we do this ourselves
		showTrajectory = false;
		trajectoryStartPoint = new Vector3D(0.9, 0.04, 0);
		trajectoryStartDirection = new Vector3D(0, 0, 1);
		trajectoryRadius = 0.025;
		trajectoryMaxTraceLevel = 1000;

		// other scene objects
		studioInitialisation = StudioInitialisationType.HEAVEN;
		// the backdrop
		indicateInsideCameraPosition = false;
		showPatternedSphere = false;
		patternedSphereCentre = new Vector3D(0, -0.75, 0);
		patternedSphereRadius = 0.05;

		activeCamera = CameraType.OUTSIDE;
		// (outside) camera parameters; the camera is set by getStandardCamera()
		// @see optics.raytrace.NonInteractiveTIMEngine.getStandardCamera()
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraViewDirection = new Vector3D(0, 1, 0);
		cameraDistance = 170;
		cameraHorizontalFOVDeg = 8;
		// * @see optics.raytrace.NonInteractiveTIMEngine.cameraMaxTraceLevel
		// * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsX
		// * @see optics.raytrace.NonInteractiveTIMEngine.cameraPixelsY
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = cameraDistance;
		// inside-camera parameters
		insideCameraApertureCentre = new Vector3D(0.9, 0.04, 0);
		insideCameraViewDirection = new Vector3D(0, 0, 1);
		insideCameraFocussingDistance = 10;
		insideCameraHorizontalFOVDeg = 40;
		insideCameraApertureSize = ApertureSizeType.PINHOLE;

		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		// nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		firstFrame = 0;

		// camera parameters are set in createStudio()
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's Nested-cloak-rotation visualiser";
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
				"NestedCloakRotation"
				;
	}
	/**
	 * Save all parameters
	 * @param printStream
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// the cloaks
		printStream.println("showInner = "+showInner);
		printStream.println("showMiddle = "+showMiddle);
		printStream.println("showOuter = "+showOuter);
		printStream.println("showLensesInner = "+showLensesInner);
		printStream.println("showLensesMiddle = "+showLensesMiddle);
		printStream.println("showLensesOuter = "+showLensesOuter);
		printStream.println("showFramesInner = "+showFramesInner);
		printStream.println("showFramesMiddle = "+showFramesMiddle);
		printStream.println("showFramesOuter = "+showFramesOuter);
		printStream.println("p1 = "+p1);
		printStream.println("hInner = "+hInner);
		printStream.println("h12hInner = "+h12hInner);
		printStream.println("h22hInner = "+h22hInner);
		printStream.println("r2hInner = "+r2hInner);
		printStream.println("d = "+d);
		printStream.println("psi1Deg = "+psi1Deg);
		printStream.println("psi2Deg = "+psi2Deg);
		printStream.println("deltaPhiDeg = "+deltaPhiDeg);
		
		// light-ray trajectories
		printStream.println("traceRaysWithTrajectory = "+traceRaysWithTrajectory);
		printStream.println("showTrajectory = "+showTrajectory);
		printStream.println("trajectoryStartPoint = "+trajectoryStartPoint);
		printStream.println("trajectoryStartDirection = "+trajectoryStartDirection);
		printStream.println("trajectoryRadius = "+trajectoryRadius);
		printStream.println("trajectoryMaxTraceLevel = "+trajectoryMaxTraceLevel);

		// other scene objects
		printStream.println("studioInitialisation = "+studioInitialisation);
		printStream.println("indicateInsideCameraPosition = "+indicateInsideCameraPosition);
		printStream.println("showPatternedSphere = "+showPatternedSphere);
		printStream.println("patternedSphereCentre = "+patternedSphereCentre);
		printStream.println("patternedSphereRadius = "+patternedSphereRadius);

		// write any parameters not defined in NonInteractiveTIMEngine
		// printStream.println("parameterName = "+parameterName);
		printStream.println("camera = "+activeCamera);
		if(activeCamera==CameraType.INSIDE)
		{
			printStream.println("ac = "+insideCameraApertureCentre);
			printStream.println(" vd "+insideCameraViewDirection);
			printStream.println(" hFOV "+insideCameraHorizontalFOVDeg+"°");
			printStream.println(" as "+insideCameraApertureSize);
			printStream.println(" fd "+insideCameraFocussingDistance);
		}
		else
		{
			printStream.println(" vc "+cameraViewCentre);
			printStream.println(" vd "+cameraViewDirection);
			printStream.println(" cd "+cameraDistance);
			printStream.println(" hFOV "+cameraHorizontalFOVDeg+"°");
			printStream.println(" as "+cameraApertureSize);
			printStream.println(" fd "+cameraFocussingDistance);
		}
		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	/**
	 * the inner cloak; this is of type EditableLensSimplicialComplex as it supplies methods such as getIndexOfSimplexContainingPosition
	 */
	private EditableLensSimplicialComplex innerCloak;
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
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
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
				);

		// add more scene objects to scene

		// introduce a sensible coordinate system
		// origin is at p1
		// optical axis q
		Vector3D qHat = Vector3D.Z;
		// r axis is rotation axis
		Vector3D rHat = Vector3D.Y;
		// s axis is perpendicular to both
		Vector3D sHat = Vector3D.crossProduct(qHat, rHat);

		Vector3D p2 = Vector3D.sum(p1, qHat.getProductWith(d));
		
		double deltaphi = MyMath.deg2rad(deltaPhiDeg); // the rotation angle
		double psi1 = MyMath.deg2rad(psi1Deg) ;
		double psi2 =  MyMath.deg2rad(psi2Deg);
		double f1 = (d/2)*(Math.cos(psi1)+(1/Math.tan(deltaphi/2)*Math.sin(psi1)));
		double g1 = f1/Math.cos(psi1);
		double f2 = (Math.sin(psi2)/2)*(
				d/Math.tan(psi1)-2*f1/Math.sin(psi1)
				)+d*Math.cos(psi2)/2;
		double g2 = f2/Math.cos(psi2);
		double f = g1*g2/(g1+g2-d);
		double f3 = -f*Math.cos(deltaphi/2);

		NumberFormat nf = NumberFormat.getInstance();
		fOuterTextField.setText(nf.format(f1));
		fMiddleTextField.setText(nf.format(f2));
		fInnerTextField.setText(nf.format(f3));
		
		double pzPrime = d - d*f / g1;
		Vector3D p3 = Vector3D.sum(
				p1, // in Jakub's coordinate system, p1 is the origin
				sHat.getProductWith(f*Math.sin(deltaphi)),
				qHat.getProductWith(pzPrime + 2*f*MyMath.square(Math.sin(0.5*deltaphi)))
				);

		// the inner cloak
		double fDInner = f3;
		double h1Inner = hInner*h12hInner;
		double rInner = hInner*r2hInner;
		double h2Inner = hInner*h22hInner;
		double h1InnerV = 1/(1/h1Inner - 1/fDInner);
		// virtual-space height of lower inner vertex
		// EditableIdealLensCloak innerCloak_01 = new EditableIdealLensCloak(
		// "inner cloak",
		// description
		// new Vector3D(0, 0, 0),
		// baseCentre
		// new Vector3D(1, 0, 0),
		// frontDirection
		// new Vector3D(0, 0, 1),
		// rightDirection
		// new Vector3D(0, 1, 0),
		// topDirection
		// rInner,
		// baseRadius
		// hInner,
		// height
		// h1Inner,
		// heightLowerInnerVertexP
		// h2Inner,
		// heightUpperInnerVertexP
		// h1InnerV,
		// heightLowerInnerVertexE
		// 0.96,
		// gCLAsTransmissionCoefficient
		// showFramesInner,
		// showFrames
		// 0.01,
		// frameRadius
		// SurfaceColour.RED_SHINY,
		// frameSurfaceProperty
		// // boolean showPlaceholderSurfaces,
		// (showLensesInner?LensElementType.IDEAL_THIN_LENS:LensElementType.GLASS_PANE),
		// lensElementType
		// scene,
		// parent
		// studio
		// );
		// create a new lens simplicial complex...
		innerCloak = new EditableLensSimplicialComplex(
				"inner cloak",
				// description
				scene,
				// parent
				studio
				);
		// ... and initialise it for tracing of rays with trajectories
		innerCloak.setLensTypeRepresentingFace(optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS);
		innerCloak.setShowStructureP(false);
		innerCloak.setShowStructureV(false);
		// initialise
		// calculate the normal to the base lens
		Vector3D nHatInner = Vector3D.sum(
				qHat.getProductWith(Math.cos(0.5*deltaphi)),
				sHat.getProductWith(Math.sin(0.5*deltaphi))
				);
		innerCloak.initialiseToOmnidirectionalLens(
				h1Inner / hInner,
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Inner / hInner,
				// physicalSpaceFractionalUpperInnerVertexHeight
				h1InnerV / hInner,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p3, nHatInner.getProductWith(hInner)),
				// topVertex
				p3,
				// baseCentre
				Vector3D.sum(p3, Vector3D.crossProduct(rHat, nHatInner).getWithLength(-rInner))
				// baseVertex1
				);
		scene.addSceneObject(innerCloak, showInner);

		// TODO
		// insideCameraApertureCentre = Vector3D.sum(p3, nHatInner.getProductWith(0.5*h1Inner));
		// insideCameraViewDirection = rHat;
		
		// the middle cloak
		double fDMiddle = f2;
		double h1Middle = 3.5;
		double hMiddle = 1.3*h1Middle;
		double rMiddle = 4;
		double h2Middle = 1.15*h1Middle;
		double h1MiddleV = 1/(1/h1Middle - 1/fDMiddle);


		EditableLensSimplicialComplex middleCloak = new EditableLensSimplicialComplex(
				"middle cloak",
				// description
				scene,
				// parent
				studio
				);
		// ... and initialise it for tracing of rays with trajectories
		middleCloak.setLensTypeRepresentingFace(optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS);
		middleCloak.setShowStructureP(false);
		middleCloak.setShowStructureV(false);
		// initialise
		// calculate the normal to the base lens
		Vector3D nHatMiddle = Vector3D.sum(
				qHat.getProductWith(Math.cos(psi2)),
				sHat.getProductWith(Math.sin(psi2))
				);
		middleCloak.initialiseToOmnidirectionalLens(
				h1Middle / hMiddle, 
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Middle / hMiddle,
				// physicalSpaceFractionalUpperInnerVertexHeight
				h1MiddleV / hMiddle,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p2, nHatMiddle.getProductWith(hMiddle)),
				// topVertex
				p2,
				// baseCentre
				Vector3D.sum(p2, Vector3D.crossProduct(rHat, nHatMiddle).getWithLength(-rMiddle))
				// baseVertex1
				);
		// virtual-space height of lower inner vertex
		// EditableIdealLensCloak innerCloak_01 = new EditableIdealLensCloak(
		// "middle cloak",
		// description
		// new Vector3D(0, 0, 0),
		// baseCentre
		// new Vector3D(1, 0, 0),
		// frontDirection
		// new Vector3D(0, 0, 1),
		// rightDirection
		// new Vector3D(0, 1, 0),
		// topDirection
		// rMiddle,
		// baseRadius
		// hMiddle,
		// height
		// h1Middle,
		// heightLowerInnerVertexP
		// h2Middle,
		// heightUpperInnerVertexP
		// h1MiddleV,
		// heightLowerMiddleVertexE
		// 0.96,
		// gCLAsTransmissionCoefficient
		// showFramesMiddle,
		// showFrames
		// 0.01,
		// frameRadius
		// SurfaceColour.RED_SHINY,
		// frameSurfaceProperty
		// // boolean showPlaceholderSurfaces,
		// (showLensesMiddle?LensElementType.IDEAL_THIN_LENS:LensElementType.GLASS_PANE),
		// lensElementType
		// scene,
		// parent
		// studio
		// );
		// create a new lens simplicial complex...
		scene.addSceneObject(middleCloak, showMiddle);


		// the outer cloak
		double fDOuter = f1;
		double h1Outer = 10;
		// has to be big enough so that inner cloak fits into cell 0
		double rOuter = 8;
		// has to be big enough so that inner cloak fits into cell 0
		double hOuter = 2*h1Outer;
		double h2Outer = 1.5*h1Outer;
		EditableLensSimplicialComplex outerCloak = new EditableLensSimplicialComplex(
				"outer cloak",
				// description
				scene,
				// parent
				studio
				);
		// ... and initialise it for tracing of rays with trajectories
		outerCloak.setLensTypeRepresentingFace(optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS);
		outerCloak.setShowStructureP(false);
		outerCloak.setShowStructureV(false);
		// initialise
		// calculate the normal to the base lens
		Vector3D nHatOuter = Vector3D.sum(
				qHat.getProductWith(Math.cos(psi1)),
				sHat.getProductWith(Math.sin(psi1))
				);
		outerCloak.initialiseToOmnidirectionalLens(
				h1Outer / hOuter,
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Outer / hOuter,
				// physicalSpaceFractionalUpperInnerVertexHeight
				1/(1/h1Outer - 1/fDOuter) / hOuter,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p1, nHatOuter.getProductWith(hOuter)),
				// topVertex
				p1,
				// baseCentre
				Vector3D.sum(p1, Vector3D.crossProduct(rHat, nHatOuter).getWithLength(-rOuter))
				// baseVertex1
				);
		// EditableIdealLensCloak outerCloak_01 = new EditableIdealLensCloak(
		// "outer cloak",
		// description
		// new Vector3D(0.12, -0.508, 0),
		// baseCentre
		// new Vector3D(Math.cos(0.5*beta), -Math.sin(0.5*beta), 0),
		// frontDirection
		// new Vector3D(0, 0, 1),
		// rightDirection
		// new Vector3D(Math.sin(0.5*beta), Math.cos(0.5*beta), 0),
		// topDirection
		// rOuter,
		// baseRadius
		// 2*h1Outer,
		// height
		// h1Outer,
		// heightLowerInnerVertexP
		// 1.5*h1Outer,
		// heightUpperInnerVertexP
		// 1/(1/h1Outer - 1/fDOuter),
		// heightLowerInnerVertexE
		// 0.96,
		// gCLAsTransmissionCoefficient
		// showFramesOuter,
		// showFrames
		// 0.01,
		// frameRadius
		// SurfaceColour.GREEN_SHINY,
		// frameSurfaceProperty
		// // boolean showPlaceholderSurfaces,
		// (showLensesOuter?LensElementType.IDEAL_THIN_LENS:LensElementType.GLASS_PANE),
		// lensElementType
		// scene,
		// parent
		// studio
		// );
		scene.addSceneObject(outerCloak, showOuter);


		// trace rays with trajectories
		if(showTrajectory)
		{
			// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",
							// description
							trajectoryStartPoint,
							// startPoint
							0,
							// startTime
							trajectoryStartDirection,
							// startDirection
							trajectoryRadius,
							// rayRadius
							SurfaceColourLightSourceIndependent.RED,
							// surfaceProperty
							// SurfaceColour.RED_SHINY,
							// surfaceProperty
							trajectoryMaxTraceLevel,
							// maxTraceLevel
							true,
							// reportToConsole
							scene,
							// parent
							studio
							)
					);

			// RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());
			studio.traceRaysWithTrajectory();
		}

		// initialise the cloaks again, this time according to the parameters
		innerCloak.setLensTypeRepresentingFace(
				showLensesInner?
						optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS:
							optics.raytrace.simplicialComplex.LensType.NONE
				);
		innerCloak.setShowStructureP(showFramesInner);
		innerCloak.setShowStructureV(false);
		// initialise
		innerCloak.initialiseToOmnidirectionalLens(
				h1Inner / hInner,
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Inner / hInner,
				// physicalSpaceFractionalUpperInnerVertexHeight
				h1InnerV / hInner,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p3, nHatInner.getProductWith(hInner)),
				// topVertex
				p3,
				// baseCentre
				Vector3D.sum(p3, Vector3D.crossProduct(rHat, nHatInner).getWithLength(-rInner))
				// baseVertex1
				);

		middleCloak.setLensTypeRepresentingFace(
				showLensesMiddle?
						optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS:
							optics.raytrace.simplicialComplex.LensType.NONE
				);
		middleCloak.setShowStructureP(showFramesMiddle);
		middleCloak.setShowStructureV(false);
		middleCloak.setSurfacePropertyP(SurfaceColour.CYAN_SHINY);
		// initialise
		middleCloak.initialiseToOmnidirectionalLens(
				h1Middle / hMiddle, 
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Middle / hMiddle,
				// physicalSpaceFractionalUpperInnerVertexHeight
				h1MiddleV / hMiddle,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p2, nHatMiddle.getProductWith(hMiddle)),
				// topVertex
				p2,
				// baseCentre
				Vector3D.sum(p2, Vector3D.crossProduct(rHat, nHatMiddle).getWithLength(-rMiddle))
				// baseVertex1
				);

		outerCloak.setLensTypeRepresentingFace(
				showLensesOuter?
						optics.raytrace.simplicialComplex.LensType.IDEAL_THIN_LENS:
							optics.raytrace.simplicialComplex.LensType.NONE
				);
		outerCloak.setShowStructureP(showFramesOuter);
		outerCloak.setShowStructureV(false);
		outerCloak.setSurfacePropertyP(SurfaceColour.GREEN_SHINY);
		// initialise
		outerCloak.initialiseToOmnidirectionalLens(
				h1Outer / hOuter,
				// physicalSpaceFractionalLowerInnerVertexHeight
				h2Outer / hOuter,
				// physicalSpaceFractionalUpperInnerVertexHeight
				1/(1/h1Outer - 1/fDOuter) / hOuter,
				// virtualSpaceFractionalLowerInnerVertexHeight
				Vector3D.sum(p1, nHatOuter.getProductWith(hOuter)),
				// topVertex
				p1,
				// baseCentre
				Vector3D.sum(p1, Vector3D.crossProduct(rHat, nHatOuter).getWithLength(-rOuter))
				// baseVertex1
				);

		// the arrow indicating the position of the inside camera
		if(activeCamera != CameraType.INSIDE)
			scene.addSceneObject(
					new EditableArrow(
							"Arrow pointing to inside-camera position",
							Vector3D.sum(insideCameraApertureCentre, insideCameraViewDirection.getWithLength(-1)),
							// start point
							insideCameraApertureCentre,
							// end point
							0.05,
							// shaft radius
							0.2,
							// tip length
							MyMath.deg2rad(30),
							// tip angle
							new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),
							scene, studio
							),
					indicateInsideCameraPosition
					// visibility
					);
		EditableScaledParametrisedSphere patternedSphere = new EditableScaledParametrisedSphere(
				"Patterned sphere", // description
				patternedSphereCentre, // centre
				patternedSphereRadius,
				// radius
				new Vector3D(1, 1, 1),
				// pole
				new Vector3D(1, 0, 0),
				// phi0Direction
				0, Math.PI,
				// sThetaMin, sThetaMax
				-Math.PI, Math.PI,
				// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),
						// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),
						// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
						),
				scene, studio);
		scene.addSceneObject(patternedSphere, showPatternedSphere);

		switch(activeCamera)
		{
		case INSIDE:
			System.out.println("ThreeNestedCloaksRotationVisualiser::populateStudio: "
					+ "insideCameraApertureCentre = "+insideCameraApertureCentre+"\n"
					+ "insideCameraViewDirection = "+insideCameraViewDirection+"\n"
					+ "insideCameraHorizontalFOVDeg = "+insideCameraHorizontalFOVDeg+"\n"
					+ "cameraPixelsX = "+cameraPixelsX+"\n"
					+ "cameraPixelsY = "+cameraPixelsY+"\n"
					+ "cameraMaxTraceLevel = "+cameraMaxTraceLevel+"\n"
					+ "insideCameraApertureSize = "+insideCameraApertureSize+"\n"
					);
			Vector3D topDirection = new Vector3D(0, 1, 0);
			if(insideCameraViewDirection.getPartPerpendicularTo(topDirection).getLength() == 0) topDirection = new Vector3D(1, 0, 0);

			studio.setCamera(
					new EditableRelativisticAnyFocusSurfaceCamera(
							"Inside camera",
							insideCameraApertureCentre,	// centre of aperture
							insideCameraViewDirection,	// viewDirection
							topDirection,	// top direction vector
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
			studio.setCamera(getStandardCamera());
		}

	}
	
	
	//
	// GUI stuff
	//

	private JCheckBox showInnerCheckbox;
	private JCheckBox showMiddleCheckbox;
	private JCheckBox showOuterCheckbox;
	private JCheckBox showLensesInnerCheckbox;
	private JCheckBox showLensesMiddleCheckbox;
	private JCheckBox showLensesOuterCheckbox;
	private JCheckBox showFramesInnerCheckbox;
	private JCheckBox showFramesMiddleCheckbox;
	private JCheckBox showFramesOuterCheckbox;
	private LabelledVector3DPanel p1Panel;
	private LabelledDoublePanel hInnerPanel;
	private LabelledDoublePanel h12hInnerPanel;
	private LabelledDoublePanel h22hInnerPanel;
	private LabelledDoublePanel r2hInnerPanel;
	private LabelledDoublePanel dPanel;
	private LabelledDoublePanel psi1DegPanel;
	private LabelledDoublePanel psi2DegPanel;
	private LabelledDoublePanel deltaPhiDegPanel;
	private JTextField fOuterTextField, fMiddleTextField, fInnerTextField;

	// light-ray trajectory
	private JCheckBox showTrajectoryCheckBox;
	private LabelledVector3DPanel trajectoryStartPointPanel;
	private LabelledVector3DPanel trajectoryStartDirectionPanel;
	private JButton setTrajectoryAccordingToInsideCamera;
	private LabelledDoublePanel trajectoryRadiusPanel;
	private LabelledIntPanel trajectoryMaxTraceLevelPanel;

	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JCheckBox indicateInsideCameraPositionCheckBox;
	private JCheckBox showPatternedSphereCheckBox;
	private LabelledVector3DPanel patternedSphereCentrePanel;
	private LabelledDoublePanel patternedSphereRadiusPanel;

	// cameras
	private JComboBox<CameraType> activeCameraComboBox;

	// main (outside) camera
	private LabelledVector3DPanel cameraViewCentrePanel;

	// private JButton setCameraViewCentreToCloakCentroidButton;
	private JButton setCameraViewCentreToInsideCameraPositionButton;
	private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;

	// inside camera
	private LabelledVector3DPanel insideCameraApertureCentrePanel;
	private JTextField insideCameraApertureCentreInfoTextField;
	// private JTextField pointOfInterestTextField;
	private JButton updateInsideCameraApertureCentreInfoButton;
	private LabelledVector3DPanel insideCameraViewDirectionPanel;
	private JButton setInsideCameraApertureCentreToInnerCloakCell1Button, setInsideCameraViewDirectionToRotationDirectionButton;
	private DoublePanel insideCameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> insideCameraApertureSizeComboBox;
	private LabelledDoublePanel insideCameraFocussingDistancePanel;

	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		deltaPhiDegPanel = new LabelledDoublePanel("Rotation angle (degrees)");
		deltaPhiDegPanel.setNumber(deltaPhiDeg);
		cloaksPanel.add(deltaPhiDegPanel, "span");

		dPanel = new LabelledDoublePanel("d");
		dPanel.setNumber(d);
		cloaksPanel.add(dPanel, "span");
		


		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");

		showInnerCheckbox = new JCheckBox("Show");
		showInnerCheckbox.setSelected(showInner);
		innerLensCloakPanel.add(showInnerCheckbox, "span");

		showLensesInnerCheckbox = new JCheckBox("Show lenses");
		showLensesInnerCheckbox.setSelected(showLensesInner);
		innerLensCloakPanel.add(showLensesInnerCheckbox, "span");

		showFramesInnerCheckbox = new JCheckBox("Show frames");
		showFramesInnerCheckbox.setSelected(showFramesInner);
		innerLensCloakPanel.add(showFramesInnerCheckbox, "span");
		
		hInnerPanel = new LabelledDoublePanel("Height, h");
		hInnerPanel.setNumber(hInner);
		innerLensCloakPanel.add(hInnerPanel, "span");
		
		h12hInnerPanel = new LabelledDoublePanel("h1/h");
		h12hInnerPanel.setNumber(h12hInner);
		innerLensCloakPanel.add(h12hInnerPanel, "span");
		
		h22hInnerPanel = new LabelledDoublePanel("h2/h");
		h22hInnerPanel.setNumber(h22hInner);
		innerLensCloakPanel.add(h22hInnerPanel, "span");

		r2hInnerPanel = new LabelledDoublePanel("r/h");
		r2hInnerPanel.setNumber(r2hInner);
		innerLensCloakPanel.add(r2hInnerPanel, "span");

		fInnerTextField = new JTextField(10);
		fInnerTextField.setEditable(false);
		innerLensCloakPanel.add(GUIBitsAndBobs.makeRow("f of base length ", fInnerTextField));


		JPanel middleLensCloakPanel = new JPanel();
		middleLensCloakPanel.setLayout(new MigLayout("insets 0"));
		middleLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Middle cloak"));
		cloaksPanel.add(middleLensCloakPanel, "wrap");

		showMiddleCheckbox = new JCheckBox("Show");
		showMiddleCheckbox.setSelected(showMiddle);
		middleLensCloakPanel.add(showMiddleCheckbox, "span");

		showLensesMiddleCheckbox = new JCheckBox("Show lenses");
		showLensesMiddleCheckbox.setSelected(showLensesMiddle);
		middleLensCloakPanel.add(showLensesMiddleCheckbox, "span");

		showFramesMiddleCheckbox = new JCheckBox("Show frames");
		showFramesMiddleCheckbox.setSelected(showFramesMiddle);
		middleLensCloakPanel.add(showFramesMiddleCheckbox, "span");
		
		psi2DegPanel = new LabelledDoublePanel("Angle of normal with z axis (degrees)");
		psi2DegPanel.setNumber(psi2Deg);
		middleLensCloakPanel.add(psi2DegPanel, "span");
		
		fMiddleTextField = new JTextField(10);
		fMiddleTextField.setEditable(false);
		middleLensCloakPanel.add(GUIBitsAndBobs.makeRow("f of base length ", fMiddleTextField));

		JPanel outerLensCloakPanel = new JPanel();
		outerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		outerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer cloak"));
		cloaksPanel.add(outerLensCloakPanel, "wrap");

		showOuterCheckbox = new JCheckBox("Show");
		showOuterCheckbox.setSelected(showOuter);
		outerLensCloakPanel.add(showOuterCheckbox, "span");

		showLensesOuterCheckbox = new JCheckBox("Show lenses");
		showLensesOuterCheckbox.setSelected(showLensesOuter);
		outerLensCloakPanel.add(showLensesOuterCheckbox, "span");

		showFramesOuterCheckbox = new JCheckBox("Show frames");
		showFramesOuterCheckbox.setSelected(showFramesOuter);
		outerLensCloakPanel.add(showFramesOuterCheckbox, "span");
		
		p1Panel = new LabelledVector3DPanel("Base centre");
		p1Panel.setVector3D(p1);
		outerLensCloakPanel.add(p1Panel, "span");
		
		psi1DegPanel = new LabelledDoublePanel("Angle of normal with z axis (degrees)");
		psi1DegPanel.setNumber(psi1Deg);
		outerLensCloakPanel.add(psi1DegPanel, "span");
		
		fOuterTextField = new JTextField(10);
		fOuterTextField.setEditable(false);
		outerLensCloakPanel.add(GUIBitsAndBobs.makeRow("f of base length ", fOuterTextField));


		
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
		// Other scene-objects panel
		//
		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Other scene objects", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		indicateInsideCameraPositionCheckBox = new JCheckBox("Show arrow (from inside-camera view direction) indicating inside-camera position");
		// indicateInsideCameraPositionCheckBox.setToolTipText("The inside-camera position is indicated by the tip of an arrow with direction (-insideCameraViewDirection)");
		indicateInsideCameraPositionCheckBox.setSelected(indicateInsideCameraPosition);
		otherObjectsPanel.add(indicateInsideCameraPositionCheckBox, "span");
		JPanel patternedSpherePanel = new JPanel();
		patternedSpherePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Patterned sphere"));
		patternedSpherePanel.setLayout(new MigLayout("insets 0"));
		otherObjectsPanel.add(patternedSpherePanel, "span");
		showPatternedSphereCheckBox = new JCheckBox("Show");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		patternedSpherePanel.add(showPatternedSphereCheckBox, "span");
		patternedSphereCentrePanel = new LabelledVector3DPanel("Centre");
		patternedSphereCentrePanel.setVector3D(patternedSphereCentre);
		patternedSpherePanel.add(patternedSphereCentrePanel, "span");
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
		setCameraViewCentreToInsideCameraPositionButton = new JButton("Set to position of inside camera");
		setCameraViewCentreToInsideCameraPositionButton.addActionListener(this);
		cameraViewCentreJPanel.add(setCameraViewCentreToInsideCameraPositionButton, "span");
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
		// inside camera
		JPanel insideCameraPanel = new JPanel();
		insideCameraPanel.setLayout(new MigLayout("insets 0"));
		camerasTabbedPane.addTab("Inside camera", insideCameraPanel);
		insideCameraApertureCentrePanel = new LabelledVector3DPanel("Aperture centre");
		insideCameraApertureCentrePanel.setVector3D(insideCameraApertureCentre);
		insideCameraPanel.add(insideCameraApertureCentrePanel);
		
		setInsideCameraApertureCentreToInnerCloakCell1Button = new JButton("Set to centre of inner cloak's cell 1");
		setInsideCameraApertureCentreToInnerCloakCell1Button.addActionListener(this);
		insideCameraPanel.add(setInsideCameraApertureCentreToInnerCloakCell1Button, "span");
		
		insideCameraApertureCentreInfoTextField = new JTextField(40);
		insideCameraApertureCentreInfoTextField.setEditable(false);
		insideCameraApertureCentreInfoTextField.setText("Click on Update button to show info");
		updateInsideCameraApertureCentreInfoButton = new JButton("Update");
		updateInsideCameraApertureCentreInfoButton.addActionListener(this);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow(insideCameraApertureCentreInfoTextField, updateInsideCameraApertureCentreInfoButton), "span");
		insideCameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		insideCameraViewDirectionPanel.setVector3D(insideCameraViewDirection);
		insideCameraPanel.add(insideCameraViewDirectionPanel);

		setInsideCameraViewDirectionToRotationDirectionButton = new JButton("Set to rotation axis");
		setInsideCameraViewDirectionToRotationDirectionButton.addActionListener(this);
		insideCameraPanel.add(setInsideCameraViewDirectionToRotationDirectionButton, "span");
		
		insideCameraHorizontalFOVDegPanel = new DoublePanel();
		insideCameraHorizontalFOVDegPanel.setNumber(insideCameraHorizontalFOVDeg);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", insideCameraHorizontalFOVDegPanel, "°"), "span");
		insideCameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		insideCameraApertureSizeComboBox.setSelectedItem(insideCameraApertureSize);
		insideCameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", insideCameraApertureSizeComboBox), "span");
		insideCameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		insideCameraFocussingDistancePanel.setNumber(insideCameraFocussingDistance);
		insideCameraPanel.add(insideCameraFocussingDistancePanel, "span");
		// pointOfInterestTextField = new JTextField(20);
		// pointOfInterestTextField.setEditable(false);
		// pointOfInterestTextField.setText(getRotationPoint().toString());
		// insideCameraPanel.add(pointOfInterestTextField, "span");

		// setCameraApertureCentreToPointOfInterestButton = new JButton("Set to point about which image rotates");
		// setCameraApertureCentreToPointOfInterestButton.addActionListener(this);
		// insideCameraPanel.add(setCameraApertureCentreToPointOfInterestButton);

	}
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		// inner cloak
		showInner = showInnerCheckbox.isSelected();
		showLensesInner = showLensesInnerCheckbox.isSelected();
		showFramesInner = showFramesInnerCheckbox.isSelected();

		// middle cloak
		showMiddle = showMiddleCheckbox.isSelected();
		showLensesMiddle = showLensesMiddleCheckbox.isSelected();
		showFramesMiddle = showFramesMiddleCheckbox.isSelected();

		// outer cloak
		showOuter = showOuterCheckbox.isSelected();
		showLensesOuter = showLensesOuterCheckbox.isSelected();
		showFramesOuter = showFramesOuterCheckbox.isSelected();
		
		p1 = p1Panel.getVector3D();
		hInner = hInnerPanel.getNumber();
		h12hInner = h12hInnerPanel.getNumber();
		h22hInner = h22hInnerPanel.getNumber();
		r2hInner = r2hInnerPanel.getNumber();
		d = dPanel.getNumber();
		psi1Deg = psi1DegPanel.getNumber();
		psi2Deg = psi2DegPanel.getNumber();
		deltaPhiDeg = deltaPhiDegPanel.getNumber();


		// trajectory
		showTrajectory = showTrajectoryCheckBox.isSelected();
		trajectoryStartPoint = trajectoryStartPointPanel.getVector3D();
		trajectoryStartDirection = trajectoryStartDirectionPanel.getVector3D();
		trajectoryRadius = trajectoryRadiusPanel.getNumber();
		trajectoryMaxTraceLevel = trajectoryMaxTraceLevelPanel.getNumber();

		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		indicateInsideCameraPosition = indicateInsideCameraPositionCheckBox.isSelected();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		patternedSphereCentre = patternedSphereCentrePanel.getVector3D();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();

		// cameras
		activeCamera = (CameraType)(activeCameraComboBox.getSelectedItem());

		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		insideCameraApertureCentre = insideCameraApertureCentrePanel.getVector3D();
		insideCameraViewDirection = insideCameraViewDirectionPanel.getVector3D();
		insideCameraHorizontalFOVDeg = insideCameraHorizontalFOVDegPanel.getNumber();
		insideCameraApertureSize = (ApertureSizeType)(insideCameraApertureSizeComboBox.getSelectedItem());
		insideCameraFocussingDistance = insideCameraFocussingDistancePanel.getNumber();
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(updateInsideCameraApertureCentreInfoButton))
		{
			acceptValuesInInteractiveControlPanel();
			try {
				populateStudio();
			} catch (SceneException e1) {
				e1.printStackTrace();
			}
			insideCameraApertureCentreInfoTextField.setText(
					"Inside camera is centred in simplex #"+
							innerCloak.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(insideCameraApertureCentre)
					);
		}
		else if(e.getSource().equals(setCameraViewCentreToInsideCameraPositionButton))
		{
			cameraViewCentrePanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
		}
		else if(e.getSource().equals(setInsideCameraApertureCentreToInnerCloakCell1Button))
		{
			// introduce a sensible coordinate system
			// origin is at p1
			// optical axis q
			Vector3D qHat = Vector3D.Z;
			// r axis is rotation axis
			Vector3D rHat = Vector3D.Y;
			// s axis is perpendicular to both
			Vector3D sHat = Vector3D.crossProduct(qHat, rHat);
			
			double deltaphi = MyMath.deg2rad(deltaPhiDeg); // the rotation angle
			double psi1 = MyMath.deg2rad(psi1Deg) ;
			double psi2 =  MyMath.deg2rad(psi2Deg);
			double f1 = (d/2)*(Math.cos(psi1)+(1/Math.tan(deltaphi/2)*Math.sin(psi1)));
			double g1 = f1/Math.cos(psi1);
			double f2 = (Math.sin(psi2)/2)*(
					d/Math.tan(psi1)-2*f1/Math.sin(psi1)
					)+d*Math.cos(psi2)/2;
			double g2 = f2/Math.cos(psi2);
			double f = g1*g2/(g1+g2-d);
			double f3 = -f*Math.cos(deltaphi/2);

			NumberFormat nf = NumberFormat.getInstance();
			fOuterTextField.setText(nf.format(f1));
			fMiddleTextField.setText(nf.format(f2));
			fInnerTextField.setText(nf.format(f3));
			
			double pzPrime = d - d*f / g1;
			Vector3D p3 = Vector3D.sum(
					p1, // in Jakub's coordinate system, p1 is the origin
					sHat.getProductWith(f*Math.sin(deltaphi)),
					qHat.getProductWith(pzPrime + 2*f*MyMath.square(Math.sin(0.5*deltaphi)))
					);

			// the inner cloak
			double h1Inner = hInner*h12hInner;

			// calculate the normal to the base lens
			Vector3D nHatInner = Vector3D.sum(
					qHat.getProductWith(Math.cos(0.5*deltaphi)),
					sHat.getProductWith(Math.sin(0.5*deltaphi))
					);

			// TODO
			// insideCameraApertureCentre = Vector3D.sum(p3, nHatInner.getProductWith(0.5*h1Inner));
			// insideCameraViewDirection = rHat;
			insideCameraApertureCentrePanel.setVector3D(Vector3D.sum(p3, nHatInner.getProductWith(0.5*h1Inner)));
		}
		else if(e.getSource().equals(setInsideCameraViewDirectionToRotationDirectionButton))
		{
			// r axis is rotation axis
			insideCameraViewDirectionPanel.setVector3D(Vector3D.Y);
		}
		else if(e.getSource().equals(setTrajectoryAccordingToInsideCamera))
		{
			trajectoryStartPointPanel.setVector3D(insideCameraApertureCentrePanel.getVector3D());
			trajectoryStartDirectionPanel.setVector3D(insideCameraViewDirectionPanel.getVector3D());
		}
		else super.actionPerformed(e);
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
		(new ThreeNestedCloaksRotationVisualiser()).run();
	}
}