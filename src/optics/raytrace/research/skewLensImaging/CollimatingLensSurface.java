package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

import math.*;
import optics.raytrace.sceneObjects.CylinderMantle;
import optics.raytrace.sceneObjects.LensSurface;
import optics.raytrace.sceneObjects.RayTrajectoryCone;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraSide;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;


/**
 * Simulation of a combination of a perfectly collimating lens surface.
 * 
 * @author Johannes Courtial, Jakub Belin
 */
public class CollimatingLensSurface extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * If true, shows a view from the side
	 */
	protected boolean sideView;
	
	/**
	 * If true, show lens 1
	 */
	protected boolean showLens1;

	/**
	 * If true, show lens 2
	 */
	protected boolean showLens2;
	
	/**
	 * show sphere at the focus of lens 2
	 */
	protected boolean showSphere;

	/**
	 * If true, show a number of light-ray trajectories
	 */
	protected boolean showTrajectories;
		
	/**
	 * focal length of first lens
	 */
	protected double f1;
	
	/**
	 * focal length of 2nd lens
	 */
	protected double f2;
	
	/**
	 * thickness of the lens at the centre
	 */
	protected double lensThickness;
	
	/**
	 * refractive index of the lens glass
	 */
	protected double refractiveIndex;
	
	/**
	 * the maximum aperture radius of the two lenses
	 */
	protected double maxApertureRadius;
	
	
	// internal variables
	
	// GUI panels
	
	private LabelledDoublePanel f1Panel, f2Panel, lensThicknessPanel, refractiveIndexPanel, maxApertureRadiusPanel;
	private JCheckBox sideViewCheckBox, showLens1CheckBox, showLens2CheckBox, showSphereCheckBox, showTrajectoriesCheckBox;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public CollimatingLensSurface()
	{
		// set all standard parameters, including those for the camera
		super();
		
		// set all parameters
		f1 = 1;
		f2 = 1;
		lensThickness = 0.5;
		refractiveIndex = 1.5;
		maxApertureRadius = 0.2;
		sideView = true;
		showSphere = true;
		showTrajectories = true;
		showLens1 = true;
		showLens2 = true;
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		windowTitle = "Dr TIM -- CollimatingLensSurface";
		windowWidth = 1100;
		windowHeight = 650;

		// camera parameters are set in populateStudio()
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "CollimatingLensSurface "	// the name
				+ " f1 "+f1
				+ " f2 "+f2
				+ " lensThickness "+lensThickness
				+ " refractiveIndex "+refractiveIndex
				+ " maxApertureRadius "+maxApertureRadius
				+ " ("
				+ (showLens1?" L1":"")
				+ (showLens2?" L2":"")
				+ (showSphere?" sphere":"")
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

		// add the lens surfaces, which will be surfaces in a glass cylinder
		
		// create a scene-object-primitive intersection, ...
		SceneObjectPrimitiveIntersection lens = new SceneObjectPrimitiveIntersection(
				"Scene-object-primitive intersection",	// description
				scene,	// parent
				studio
			);
		scene.addSceneObject(lens);
		
		// ... and add the cylinder, ...
		lens.addPositiveSceneObjectPrimitive(new CylinderMantle(
			"cylinder providing maximum lens aperture",	// description
			new Vector3D(0, 0, -10),	// startPoint
			new Vector3D(0, 0, 10),	// endPoint
			maxApertureRadius,	// radius
			new RefractiveSimple(refractiveIndex, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, false),	// surfaceProperty
			lens,	// parent
			studio
		));

		LensSurface lensSurface1 = new LensSurface(
				"Lens surface 1",	// description
				new Vector3D(0, 0, -0.5*lensThickness-f1),	// focal point
				f1,	// focalDistance
				refractiveIndex,	// refractiveIndex
				new Vector3D(0, 0, -1),	// opticalAxisDirectionOutwards
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				false,	// shadowThrowing
				lens,	// parent
				studio
		);
		// lensSurface1.setSurfaceProperty(Reflective.PERFECT_MIRROR);
		// lensSurface1.setSurfaceProperty(SurfaceColour.CYAN_MATT);
		if(showLens1) lens.addPositiveSceneObjectPrimitive(lensSurface1);

		LensSurface lensSurface2 = new LensSurface(
				"Lens surface 2",	// description
				new Vector3D(0, 0,  0.5*lensThickness+f2),	// principal point
				f2,	// focalDistance
				refractiveIndex,	// refractiveIndex
				new Vector3D(0, 0, 1),	// opticalAxisDirectionOutwards
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
				false,	// shadowThrowing
				lens,	// parent
				studio
		);
		// lensSurface.setSurfaceProperty(SurfaceColour.GREEN_SHINY);
		if(showLens2) lens.addPositiveSceneObjectPrimitive(lensSurface2);
		
		if(sideView && showTrajectories) scene.addSceneObject(new RayTrajectoryCone(
				"ray cone",	// description
				new Vector3D(0, 0, -0.5*lensThickness-f1),	// cone apex
				(f1<0?2*f1:0),	// start a distance 10 in front of the cone apex
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

		if(showSphere) scene.addSceneObject(new Sphere(
				"tiny sphere, imaged to camera", 	// description
				new Vector3D(0, 0,  0.5*lensThickness+f2),	// centre
				0.01,	// radius
				SurfaceColour.YELLOW_MATT,	// surfaceProperty
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
			cameraDistance = 0.5*lensThickness+f1;	// camera is located at (0, 0, 0)
			cameraFocussingDistance = 0.5*lensThickness+f1;
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

		f1Panel = new LabelledDoublePanel("f_1");
		f1Panel.setNumber(f1);
		interactiveControlPanel.add(f1Panel, "span");

		f2Panel = new LabelledDoublePanel("f_2");
		f2Panel.setNumber(f2);
		interactiveControlPanel.add(f2Panel, "span");
		
		lensThicknessPanel = new LabelledDoublePanel("lens thickness");
		lensThicknessPanel.setNumber(lensThickness);
		interactiveControlPanel.add(lensThicknessPanel, "span");
		
		refractiveIndexPanel = new LabelledDoublePanel("refractive index");
		refractiveIndexPanel.setNumber(refractiveIndex);
		interactiveControlPanel.add(refractiveIndexPanel, "span");
		
		maxApertureRadiusPanel = new LabelledDoublePanel("max aperture radius");
		maxApertureRadiusPanel.setNumber(maxApertureRadius);
		interactiveControlPanel.add(maxApertureRadiusPanel, "span");

		sideViewCheckBox = new JCheckBox("Side view");
		sideViewCheckBox.setSelected(sideView);
		interactiveControlPanel.add(sideViewCheckBox, "span");

		showLens1CheckBox = new JCheckBox("Show lens 1");
		showLens1CheckBox.setSelected(showLens1);
		interactiveControlPanel.add(showLens1CheckBox, "span");

		showLens2CheckBox = new JCheckBox("Show lens 2");
		showLens2CheckBox.setSelected(showLens2);
		interactiveControlPanel.add(showLens2CheckBox, "span");

		showSphereCheckBox = new JCheckBox("Show sphere at focus of lens 2");
		showSphereCheckBox.setSelected(showSphere);
		interactiveControlPanel.add(showSphereCheckBox, "span");

		showTrajectoriesCheckBox = new JCheckBox("Show trajectories (side view only)");
		showTrajectoriesCheckBox.setSelected(showTrajectories);
		interactiveControlPanel.add(showTrajectoriesCheckBox, "span");
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		lensThickness = lensThicknessPanel.getNumber();
		refractiveIndex = refractiveIndexPanel.getNumber();
		maxApertureRadius = maxApertureRadiusPanel.getNumber();

		sideView = sideViewCheckBox.isSelected();
		showLens1 = showLens1CheckBox.isSelected();
		showLens2 = showLens2CheckBox.isSelected();
		showSphere = showSphereCheckBox.isSelected();
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
		(new CollimatingLensSurface()).run();
	}
}
