package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

import math.*;
import optics.raytrace.sceneObjects.FresnelLensSurface;
import optics.raytrace.sceneObjects.RayTrajectoryCone;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraSide;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;


/**
 * Simulation of a combination of perfectly collimating lens surfaces and surfaces that make up the steps of Fresnel-lens surfaces.
 * 
 * @author Johannes Courtial
 */
public class CollimatingFresnelLensSurfaceTest extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * If true, shows a view from the side
	 */
	protected boolean sideView;
	
	/**
	 * if true, show a very simple Fresnel lens comprising two lens sections
	 */
	protected boolean show2PartFresnelLens;
	
	/**
	 * if true, show a Fresnel lens of arbitrary thickness and comprising arbitrarily many lens sections
	 */
	protected boolean showFresnelLens;
	
	// Fresnel-lens parameters
	protected double focalLength;
	protected double thickness;
	protected int numberOfLensSections;
	protected Vector3D opticalAxisDirection;
	protected boolean makeStepSurfacesBlack;

	
	/**
	 * If true, show lens surface 1
	 */
//	protected boolean showLensSurface1;
	
	/**
	 * If true, give the lens surfaces colours
	 */
//	protected boolean colourLensSurfaces;

	/**
	 * If true, show lens surface 2
	 */
//	protected boolean showLensSurface2;
	
	/**
	 * If true, show "step" surface 1
	 */
//	protected boolean showBelinCone1;
	
	/**
	 * If true, show "step" surface 2
	 */
//	protected boolean showBelinCone2;
	
	/**
	 * If true, show the contour plane where lens surface 1 and the step surface intersect
	 */
//	protected boolean showContourPlane;
	
	/**
	 * If true, show a number of light-ray trajectories
	 */
	protected boolean showTrajectories;
		
	/**
	 * focal length of first lens surface (focal point is the origin)
	 */
//	protected double f1;
	
	/**
	 * focal length of 2nd lens surface (focal point is the origin)
	 */
//	protected double f2;
	
	/**
	 * z coordinate of the contour plane
	 */
//	protected double contourZ;
	
	/**
	 * refractive index of the lens glass
	 */
	protected double refractiveIndex;
	
	/**
	 * the maximum aperture radius of the two lenses
	 */
//	protected double maxApertureRadius;
	
	
	// internal variables
	
	// GUI panels
	
	private LabelledDoublePanel focalLengthPanel, thicknessPanel, refractiveIndexPanel;	// , f1Panel, f2Panel, contourZPanel, maxApertureRadiusPanel;
	private LabelledIntPanel numberOfLensSectionsPanel;
	private JCheckBox sideViewCheckBox, showFresnelLensCheckBox, showTrajectoriesCheckBox, makeStepSurfacesBlackCheckBox; // showContourPlaneCheckBox, show2PartFresnelLensCheckBox, showLens1CheckBox, showLens2CheckBox, showBelinCone1CheckBox, showBelinCone2CheckBox, colourLensSurfacesCheckBox;
	private LabelledVector3DPanel opticalAxisDirectionPanel;
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public CollimatingFresnelLensSurfaceTest()
	{
		// set all standard parameters, including those for the camera
		super();
		
		// set all parameters
		showFresnelLens = true;
		focalLength = 1;
		thickness = 0.1;
		numberOfLensSections = 3;
		opticalAxisDirection = new Vector3D(0, 0, -1);
		makeStepSurfacesBlack = false;
//		f1 = 1;
//		f2 = 1.2;
//		contourZ = 1.3;
		refractiveIndex = 1.5;
//		maxApertureRadius = 2;
		sideView = false;
//		show2PartFresnelLens = false;
//		showBelinCone1 = false;
//		showBelinCone2 = false;
//		showContourPlane = false;
		showTrajectories = true;
//		showLensSurface1 = false;
//		showLensSurface2 = false;
//		colourLensSurfaces = true;
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		windowTitle = "Dr TIM -- CollimatingFresnelLensSurface";
		windowWidth = 1100;
		windowHeight = 650;

		// camera parameters are set in populateStudio()
	}
	

	@Override
	public String getClassName()
	{
		return "CollimatingFresnelLensSurface "	// the name
				+ (showFresnelLens?" Fresnel lens":"")
				+ " f "+focalLength
				+ " thickness "+thickness
				+ " #sections "+numberOfLensSections
				+ " optical-axis direction "+opticalAxisDirection
//				+ " f1 "+f1
//				+ " f2 "+f2
//				+ " contourZ "+contourZ
				+ " refractiveIndex "+refractiveIndex
//				+ " maxApertureRadius "+maxApertureRadius
//				+ " ("
//				+ (show2PartFresnelLens?" 2-part Fresnel lens"+(colourLensSurfaces?" (coloured)":""):"")
//				+ (showLensSurface1?" L1"+(colourLensSurfaces?" (coloured)":""):"")
//				+ (showLensSurface2?" L2"+(colourLensSurfaces?" (coloured)":""):"")
//				+ (showBelinCone1?" BC1":"")
//				+ (showBelinCone2?" BC2":"")
//				+ (showContourPlane?" contour plane":"")
				+ ((sideView && showTrajectories)?" trajectories":"")
				+ " shown)"
				+ (sideView?" (side view)":"")
				;
	}
		
	@Override
	public void populateStudio()
	throws SceneException
	{
		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		
		Vector3D focalPoint = new Vector3D(0, 0, 0);
		Vector3D opticalAxisDirectionOutwards = opticalAxisDirection.getNormalised();
		
		// add the Fresnel lens
		
		FresnelLensSurface fresnelLensSurface = new FresnelLensSurface(
				"Fresnel lens",	// description
				Vector3D.sum(focalPoint, opticalAxisDirectionOutwards.getWithLength(-focalLength)),	// lens centre
				new Vector3D(0, 0, -1),	// outwardsInnerLensPlaneNormal,
				focalPoint,
				refractiveIndex,
				opticalAxisDirectionOutwards,
				thickness,
				numberOfLensSections,
				makeStepSurfacesBlack,
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				scene,
				studio
			);
		if(showFresnelLens) scene.addSceneObject(fresnelLensSurface);

		// add the lens surfaces, which will be glass surfaces in an otherwise invisible cylinder
		
//		// create a scene-object-primitive intersection, ...
//		SceneObjectPrimitiveIntersection lens1 = new SceneObjectPrimitiveIntersection(
//				"Lens 1",	// description
//				scene,	// parent
//				studio
//			);
//		
//		LensSurface lensSurface1 = new LensSurface(
//				"Lens surface 1",	// description
//				focalPoint,	// focal point
//				f1,	// focalDistance
//				refractiveIndex,	// refractiveIndex
//				opticalAxisDirectionOutwards,	// opticalAxisDirectionOutwards
//				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
//				false,	// shadowThrowing
//				lens1,	// parent
//				studio
//		);
//		// lensSurface1.setSurfaceProperty(Reflective.PERFECT_MIRROR);
//		if(colourLensSurfaces) lensSurface1.setSurfaceProperty(SurfaceColour.WHITE_SHINY);
//		
//		CylinderMantle apertureCylinder1 = new CylinderMantle(
//				"cylinder providing maximum lens aperture",	// description
//				new Vector3D(0, 0, -10),	// startPoint
//				new Vector3D(0, 0, 10),	// endPoint
//				maxApertureRadius,	// radius
//				new Transparent(),	// surfaceProperty
//				// new RefractiveSimple(refractiveIndex, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false),	// surfaceProperty
//				lens1,	// parent
//				studio
//			);
//		
//		// ... and add the cylinder, ...
//		lens1.addPositiveSceneObjectPrimitive(apertureCylinder1);
//		lens1.addPositiveSceneObjectPrimitive(lensSurface1);
//		if(showLensSurface1) scene.addSceneObject(lens1);
//		
//		Vector3D pointInContourPlane = new Vector3D(0, 0, contourZ);
//		Vector3D contourNormal = new Vector3D(0, 0, -1);
//		
//		BelinCone belinCone1 = new BelinCone(
//				"Belin cone for lens surface 1",	// description
//				pointInContourPlane,	// pointInContourPlane
//				contourNormal,	// contourNormal
//				lensSurface1,	// lensSurface
//				SurfaceColour.BLACK_MATT,	// SemiTransparent.BLUE_SHINY_SEMITRANSPARENT,	// surfaceProperty
//				// new RefractiveSimple(refractiveIndex, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false),	// surfaceProperty
//				scene,	// parent
//				studio
//			);		
//		if(showBelinCone1) scene.addSceneObject(belinCone1);
//		
//		Plane contourPlane = new Plane(
//				"contour plane",	// description
//				pointInContourPlane,	// point in plane
//				contourNormal.getReverse(),	// normal
//				Transparent.PERFECT,	// surface property
//				scene, studio
//			);
//		if(showContourPlane) scene.addSceneObject(contourPlane);
//
//		// create a scene-object-primitive intersection, ...
//		SceneObjectPrimitiveIntersection lens2 = new SceneObjectPrimitiveIntersection(
//				"Lens 2",	// description
//				scene,	// parent
//				studio
//			);
//		if(showLensSurface2) scene.addSceneObject(lens2);
//		
//		// ... and add the cylinder, ...
//		lens2.addPositiveSceneObjectPrimitive(new CylinderMantle(
//			"cylinder providing maximum lens aperture",	// description
//			new Vector3D(0, 0, -10),	// startPoint
//			new Vector3D(0, 0, 10),	// endPoint
//			maxApertureRadius,	// radius
//			new Transparent(),	// surfaceProperty
//			// new RefractiveSimple(refractiveIndex, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false),	// surfaceProperty
//			lens2,	// parent
//			studio
//		));
//
//		
//		LensSurface lensSurface2 = new LensSurface(
//				"Lens surface 2",	// description
//				focalPoint,	// principal point
//				f2,	// focalDistance
//				refractiveIndex,	// refractiveIndex
//				opticalAxisDirectionOutwards,	// opticalAxisDirectionOutwards
//				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
//				false,	// shadowThrowing
//				lens2,	// parent
//				studio
//		);
//		if(colourLensSurfaces) lensSurface2.setSurfaceProperty(SurfaceColour.WHITE_SHINY);
//		// lensSurface.setSurfaceProperty(SurfaceColour.GREEN_SHINY);
//		lens2.addPositiveSceneObjectPrimitive(lensSurface2);
//		
//		BelinCone belinCone2 = new BelinCone(
//				"Belin cone for lens surface 2",	// description
//				pointInContourPlane,	// pointInContourPlane
//				contourNormal,	// contourNormal
//				lensSurface2,	// lensSurface
//				SurfaceColour.BLACK_MATT,	// SemiTransparent.BLUE_SHINY_SEMITRANSPARENT,	// surfaceProperty
//				// new RefractiveSimple(refractiveIndex, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false),	// surfaceProperty
//				scene,	// parent
//				studio
//			);		
//		if(showBelinCone2) scene.addSceneObject(belinCone2);
//		
//		// construct the Fresnel lens
//		
//		// create a scene-object-primitive intersection, ...
//		SceneObjectPrimitiveIntersection fresnelLensSection1 = new SceneObjectPrimitiveIntersection(
//				"Fresnel lens, section 1",	// description
//				scene,	// parent
//				studio
//			);
//		fresnelLensSection1.addPositiveSceneObjectPrimitive(lensSurface2);
//		fresnelLensSection1.addPositiveSceneObjectPrimitive(belinCone2);
//		fresnelLensSection1.addPositiveSceneObjectPrimitive(contourPlane);
//		if(show2PartFresnelLens) scene.addSceneObject(fresnelLensSection1);
//
//		SceneObjectPrimitiveIntersection fresnelLensSection2 = new SceneObjectPrimitiveIntersection(
//				"Fresnel lens, section 2",	// description
//				scene,	// parent
//				studio
//			);
//		fresnelLensSection2.addPositiveSceneObjectPrimitive(lensSurface1);
//		fresnelLensSection2.addPositiveSceneObjectPrimitive(belinCone1);
//		fresnelLensSection2.addNegativeSceneObjectPrimitive(belinCone2);
//		fresnelLensSection2.addPositiveSceneObjectPrimitive(contourPlane);
//		if(show2PartFresnelLens) scene.addSceneObject(fresnelLensSection2);
		

		
		
		if(sideView && showTrajectories) scene.addSceneObject(new RayTrajectoryCone(
				"ray cone",	// description
				focalPoint,	// cone apex
				-2,	// (f1<0?2*f1:0),	// start a distance 10 in front of the cone apex
				0,	// startTime
				new Vector3D(0, 0, 1),	// axisDirection
				MyMath.deg2rad(10),	// coneAngle
				10,	// numberOfRays
				0.01,	// rayRadius
				SurfaceColourLightSourceIndependent.RED,	// surfaceProperty
				100,	// maxTraceLevel
				scene,	// parent
				studio
			));

		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;

		if(sideView)
		{
			studio.setCamera(new EditableOrthographicCameraSide(
					"Side view",
					0,	// yCentre
					0,	// zCentre
					4,	// zLength
					cameraPixelsX, cameraPixelsY,	// logical number of pixels
					cameraMaxTraceLevel,	// maxTraceLevel
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			));
		}
		else
		{
			cameraViewDirection = new Vector3D(0, 0, 1);
			cameraViewCentre = new Vector3D(0, 0, 0);
			cameraDistance = 10;
			cameraFocussingDistance = 10;
			cameraHorizontalFOVDeg = 20;
			cameraApertureSize = 
					ApertureSizeType.PINHOLE;
					// ApertureSizeType.SMALL;

			studio.setCamera(getStandardCamera());
		}
	}
	

	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
//		testCheckBox = new JCheckBox("Test");
//		testCheckBox.setSelected(test);
//		testCheckBox.addActionListener(this);
//		interactiveControlPanel.add(testCheckBox);

		showFresnelLensCheckBox = new JCheckBox("Show Fresnel lens");
		showFresnelLensCheckBox.setSelected(showFresnelLens);
		interactiveControlPanel.add(showFresnelLensCheckBox, "span");

		focalLengthPanel = new LabelledDoublePanel("f");
		focalLengthPanel.setNumber(focalLength);
		interactiveControlPanel.add(focalLengthPanel, "span");
		
		thicknessPanel = new LabelledDoublePanel("thickness");
		thicknessPanel.setNumber(thickness);
		interactiveControlPanel.add(thicknessPanel, "span");
		
		numberOfLensSectionsPanel = new LabelledIntPanel("number of lens sections");
		numberOfLensSectionsPanel.setNumber(numberOfLensSections);
		interactiveControlPanel.add(numberOfLensSectionsPanel, "span");
		
		opticalAxisDirectionPanel = new LabelledVector3DPanel("optical-axis direction");
		opticalAxisDirectionPanel.setVector3D(opticalAxisDirection);
		interactiveControlPanel.add(opticalAxisDirectionPanel, "span");
		
		makeStepSurfacesBlackCheckBox = new JCheckBox("Make step surfaces black");
		makeStepSurfacesBlackCheckBox.setSelected(makeStepSurfacesBlack);
		interactiveControlPanel.add(makeStepSurfacesBlackCheckBox, "span");

//		f1Panel = new LabelledDoublePanel("f_1");
//		f1Panel.setNumber(f1);
//		interactiveControlPanel.add(f1Panel, "span");
//
//		f2Panel = new LabelledDoublePanel("f_2");
//		f2Panel.setNumber(f2);
//		interactiveControlPanel.add(f2Panel, "span");
		
		refractiveIndexPanel = new LabelledDoublePanel("refractive index");
		refractiveIndexPanel.setNumber(refractiveIndex);
		interactiveControlPanel.add(refractiveIndexPanel, "span");
		
//		maxApertureRadiusPanel = new LabelledDoublePanel("max aperture radius");
//		maxApertureRadiusPanel.setNumber(maxApertureRadius);
//		interactiveControlPanel.add(maxApertureRadiusPanel, "span");

		sideViewCheckBox = new JCheckBox("Side view");
		sideViewCheckBox.setSelected(sideView);
		interactiveControlPanel.add(sideViewCheckBox, "span");

//		show2PartFresnelLensCheckBox = new JCheckBox("Show 2-part Fresnel lens");
//		show2PartFresnelLensCheckBox.setSelected(show2PartFresnelLens);
//		interactiveControlPanel.add(show2PartFresnelLensCheckBox, "span");
//
//		showLens1CheckBox = new JCheckBox("Show lens 1");
//		showLens1CheckBox.setSelected(showLensSurface1);
//		interactiveControlPanel.add(showLens1CheckBox, "span");
//
//		colourLensSurfacesCheckBox = new JCheckBox("Colour lenses");
//		colourLensSurfacesCheckBox.setSelected(colourLensSurfaces);
//		interactiveControlPanel.add(colourLensSurfacesCheckBox, "span");
//		
//		showLens2CheckBox = new JCheckBox("Show lens 2");
//		showLens2CheckBox.setSelected(showLensSurface2);
//		interactiveControlPanel.add(showLens2CheckBox, "span");
//
//		showBelinCone1CheckBox = new JCheckBox("Show Belin cone 1");
//		showBelinCone1CheckBox.setSelected(showBelinCone1);
//		interactiveControlPanel.add(showBelinCone1CheckBox, "span");
//
//		showBelinCone2CheckBox = new JCheckBox("Show Belin cone 2");
//		showBelinCone2CheckBox.setSelected(showBelinCone2);
//		interactiveControlPanel.add(showBelinCone2CheckBox, "span");
//
//		showContourPlaneCheckBox = new JCheckBox("Show contour plane");
//		showContourPlaneCheckBox.setSelected(showContourPlane);
//		interactiveControlPanel.add(showContourPlaneCheckBox, "span");
//		
//		contourZPanel = new LabelledDoublePanel("z_contour");
//		contourZPanel.setNumber(contourZ);
//		interactiveControlPanel.add(contourZPanel, "span");
		
		showTrajectoriesCheckBox = new JCheckBox("Show trajectories (side view only)");
		showTrajectoriesCheckBox.setSelected(showTrajectories);
		interactiveControlPanel.add(showTrajectoriesCheckBox, "span");
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		focalLength = focalLengthPanel.getNumber();
		thickness = thicknessPanel.getNumber();
		numberOfLensSections = numberOfLensSectionsPanel.getNumber();
		opticalAxisDirection = opticalAxisDirectionPanel.getVector3D();
		makeStepSurfacesBlack = makeStepSurfacesBlackCheckBox.isSelected();
		
//		f1 = f1Panel.getNumber();
//		f2 = f2Panel.getNumber();
//		contourZ = contourZPanel.getNumber();
		refractiveIndex = refractiveIndexPanel.getNumber();
//		maxApertureRadius = maxApertureRadiusPanel.getNumber();

		sideView = sideViewCheckBox.isSelected();
		showFresnelLens = showFresnelLensCheckBox.isSelected();
//		show2PartFresnelLens = show2PartFresnelLensCheckBox.isSelected();
//		showLensSurface1 = showLens1CheckBox.isSelected();
//		colourLensSurfaces = colourLensSurfacesCheckBox.isSelected();
//		showLensSurface2 = showLens2CheckBox.isSelected();
//		showBelinCone1 = showBelinCone1CheckBox.isSelected();
//		showBelinCone2 = showBelinCone2CheckBox.isSelected();
//		showContourPlane = showContourPlaneCheckBox.isSelected();
		showTrajectories = showTrajectoriesCheckBox.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
				
//		if(e.getSource().equals(testCheckBox))
//		{
//			setTest(testCheckBox.isSelected());
//			// render();
//		}
	}


	public static void main(final String[] args)
	{
		(new CollimatingFresnelLensSurfaceTest()).run();
	}
}
