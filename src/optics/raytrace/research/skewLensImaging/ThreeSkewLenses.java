package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectDifference;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.LensHologram;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraTop;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeComboBox;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;


/**
 * Simulation of a combination of three skew lenses.
 * One of the aims is to use this to understand how the view out of the ideal-lens cloak / 4 pi lens can rotate the view!?
 * 
 * @author Johannes Courtial
 */
public class ThreeSkewLenses extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * principal point of the first lens
	 */
	protected Vector3D p1;
	
	/**
	 * principal point of the second lens
	 */
	protected Vector3D p2;

	/**
	 * principal point of the 3rd lens
	 */
	protected Vector3D p3;

	/**
	 * angle (in degrees) between the x axis and the normal to the 1st lens
	 */
	protected double alpha1;

	/**
	 * angle (in degrees) between the x axis and the normal to the 2nd lens
	 */
	protected double alpha2;

	/**
	 * angle (in degrees) between the z axis and the normal to the 3rd lens
	 */
	protected double alpha3;

	/**
	 * focal length of first lens
	 */
	protected double f1;
	
	/**
	 * focal length of 2nd lens
	 */
	protected double f2;
	
	/**
	 * focal length of 3rd lens
	 */
	protected double f3;

	/**
	 * If true, shows an orthographic projection from above
	 */
	protected boolean topView;
	
	/**
	 * If true, show a number of light-ray trajectories
	 */
	protected boolean showTrajectories;
		
	/**
	 * Allows selection of the simulated lens type
	 */
	protected LensType lensType;
	
	
	// internal variables
	
	/**
	 * normalised normal to the first lens (pointing in the direction of object space)
	 */
	private Vector3D n1;

	/**
	 * normalised normal to the 2nd lens (pointing in the direction of object space)
	 */
	private Vector3D n2;
	private Vector3D n3;

	// GUI panels
	
	private LabelledDoublePanel alpha1Panel, alpha2Panel, alpha3Panel, f1Panel, f2Panel, f3Panel, cameraFocussingDistancePanel;
	private JCheckBox topViewCheckBox, showTrajectoriesCheckBox;
	private ApertureSizeComboBox cameraApertureSizeComboBox;
	private JComboBox<LensType> lensTypeComboBox;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public ThreeSkewLenses()
	{
		// set all standard parameters, including those for the camera
		super();
		
		// set all parameters
		alpha1 = -40;	// degrees
		alpha2 = -20.;	// degrees
		alpha3 = 30;
		calculateN123FromAlpha123();
		p1 = new Vector3D(-0.5, -0.5, 0);
		p2 = new Vector3D(   0, -0.5, 0);
		p3 = new Vector3D( 0.5, -0.5, 0);
		f1 = 1;
		f2 = -.3;
		f3 = 1;
		topView = false;
		showTrajectories = true;
		lensType = LensType.IDEAL_THIN_LENS;
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraFocussingDistance = 15;
		cameraApertureSize = ApertureSizeType.PINHOLE;
	}
	
	private void calculateN123FromAlpha123()
	{
		n1 = new Vector3D(Math.sin(MyMath.deg2rad(alpha1)), 0, Math.cos(MyMath.deg2rad(alpha1)));
		n2 = new Vector3D(Math.sin(MyMath.deg2rad(alpha2)), 0, Math.cos(MyMath.deg2rad(alpha2)));
		n3 = new Vector3D(0, Math.sin(MyMath.deg2rad(alpha3)), Math.cos(MyMath.deg2rad(alpha3)));
	}

	@Override
	public String getFirstPartOfFilename()
	{
		return "ThreeSkewLenses "	// the name
				+ " alpha1 "+alpha1+"°"
				+ " alpha2 "+alpha2+"°"
				+ " alpha3 "+alpha3+"°"
				+ " f1 "+f1
				+ " f2 "+f2
				+ " f3 "+f3
				+ " lens type "+lensType
				+ ((!topView && cameraApertureSize != ApertureSizeType.PINHOLE)?" focussing distance "+cameraFocussingDistance:"")
				+ (!topView?" aperture size "+cameraApertureSize:"")
				+ ((topView && showTrajectories)?" (with trajectories)":"")
				+ (topView?" (top view)":"")
				;
	}
	
	private void addTrajectory(
			String name,
			Vector3D startPoint,
			double initialAngle,	// in radians
			DoubleColour colour,
			double radius,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		Vector3D direction = new Vector3D(Math.sin(initialAngle), 0, Math.cos(initialAngle));
		
		SurfaceColourLightSourceIndependent c = new SurfaceColourLightSourceIndependent(colour, true);
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name,
				startPoint,	// start point
				0,	// start time
				direction,	// initial direction
				radius,	// radius
				c,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);
	}
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		super.populateSimpleStudio();

		studio = new Studio();

		double frameWidth = 0.05;
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1-frameWidth, scene, studio));
				
		// add any other scene objects
		
		double lensRadius = 2.;
		
		// then calculate the remaining parameters, which depends on what exactly we want to do;
		// in all cases, we want the object-sided focal plane of the lens to coincide with the image-sided focal plane of the
		// combination of lenses 1 and 2
		
			// we want the phase hologram of lens 1 to work like an ideal thin lens for rays through the eye position;
			// the eye position should therefore be imaged to <eyePositionImage1>
			Vector3D eyePositionImage1 = IdealThinLensSurface.getImagePosition(
					f1,	// focalLength
					p1,	// principalPoint
					n1,	// opticalAxisDirectionPos
					getStandardCameraPosition(),	// objectPosition
					ImagingDirection.NEG2POS	// direction
				);
			// the first lens
			addFramedCircularLens(
					"lens 1",	// description
					p1,	// principalPoint
					n1,	// normal
					f1,	// focalLength
					getStandardCameraPosition(),	// insideSpacePoint
					eyePositionImage1,	// outsideSpacePoint
					lensType,
					lensRadius,	// radius
					frameWidth,
					SurfaceColour.RED_SHINY,	// frameSurfaceProperty
					scene, studio
				);

			// we want the phase hologram of lens 2 to work like an ideal thin lens for rays through the <eyePositionImage1>;
			// the eye position should therefore be imaged to <eyePositionImage2>
			Vector3D eyePositionImage2 = IdealThinLensSurface.getImagePosition(
					f2,	// focalLength
					p2,	// principalPoint
					n2,	// opticalAxisDirectionPos
					eyePositionImage1,	// objectPosition
					ImagingDirection.NEG2POS	// direction
				);
			addFramedCircularLens(
					"lens 2",	// description
					p2,	// principalPoint
					n2,	// normal
					f2,	// focalLength
					eyePositionImage1,	// insideSpacePoint
					eyePositionImage2,	// outsideSpacePoint
					lensType,
					lensRadius,	// radius
					frameWidth,
					SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty
					scene, studio
				);

				// we want the phase hologram of lens 3 to work like an ideal thin lens for rays through the <eyePositionImage2>;
				// the eye position should therefore be imaged to <eyePositionImage3>
				Vector3D eyePositionImage3 = IdealThinLensSurface.getImagePosition(
						f3,	// focalLength
						p3,	// principalPoint
						n3,	// opticalAxisDirectionPos
						eyePositionImage2,	// objectPosition
						ImagingDirection.NEG2POS	// direction
					);
				addFramedCircularLens(
						"lens 3",	// description
						p3,	// principalPoint
						n3,	// normal
						f3,	// focalLength
						eyePositionImage2,	// insideSpacePoint
						eyePositionImage3,	// outsideSpacePoint
						lensType,
						lensRadius,	// radius
						frameWidth,
						SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty
						scene, studio
						);
		
		if(topView && showTrajectories)
		{
			for(double x = -0.5; x<0.5; x+=0.25)
			{
			// a few light-ray trajectories
			addTrajectory(
					"trajectory",	// name
					new Vector3D(x, 0, -2),	// startPoint
					0,	// initialAngle, in radians
					DoubleColour.RED,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);

			addTrajectory(
					"trajectory",	// name
					new Vector3D(x, 0, -2),	// startPoint
					0.1,	// initialAngle, in radians
					DoubleColour.ORANGE,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);

			addTrajectory(
					"trajectory",	// name
					new Vector3D(x, 0, -2),	// startPoint
					-0.1,	// initialAngle, in radians
					DoubleColour.VIOLET,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);
			}
		}
		
		// something to look at
		
		// the cylinder lattice
		EditableCylinderLattice cylinderLattice = new EditableCylinderLattice(
				"cylinder lattice",	// description
				-2.5, 2.5, 4,	// xMin, xMax, nX
				-1, 4, 4,	// yMin, yMax, nY
				5, 10, 4,	// zMin, zMax, nZ
				0.02,	// radius
				scene,	// parent
				studio
		);
		scene.addSceneObject(cylinderLattice);

		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		if(topView)
		{
			studio.setCamera(new EditableOrthographicCameraTop(
					"Ceiling view",
					p2.x,	// xCentre
					p2.z,	// zCentre
					4,	// zLength
					cameraPixelsX, cameraPixelsY,	// logical number of pixels
					100,	// maxTraceLevel
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			));
//			camera = new EditableOrthographicCamera(
//					"Camera",	// name
//					new Vector3D(0, -1, 0),	// viewDirection
//					new Vector3D(0, 1000, 0),	// CCDCentre
//					new Vector3D(0, 0, width),	// horizontalSpanVector3D
//					new Vector3D(width*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
//					pixelsX,	// imagePixelsHorizontal
//					pixelsY,	// imagePixelsVertical
//					100,	// maxTraceLevel
//					(test?QualityType.NORMAL:QualityType.GOOD)	// anti-aliasing quality
//					);
		}
		else
		{
			studio.setCamera(getStandardCamera());
		}
	}
	
	/**
	 * Adds a framed circular lens to <scene>.
	 * The surface property can be set in different ways, depending on <lensType>.
	 * If <lensType> is IDEAL_THIN_LENS or LENS_HOLOGRAM, it uses <normal>, <principalPoint> and <focalLength> to calculate the surface parameters.
	 * If <lensType> is LENS_HOLOGRAM_EYE, it uses <insideSpacePoint> and <outsideSpacePoint> to optimise the lens hologram (which is chosen
	 * such that it stigmatically images <insideSpacePoint> into <outsideSpacePoint>).
	 * @param description
	 * @param principalPoint
	 * @param normal
	 * @param focalLength
	 * @param insideSpacePoint
	 * @param outsideSpacePoint
	 * @param lensType
	 * @param radius
	 * @param frameWidth
	 * @param frameSurfaceProperty
	 * @param scene
	 * @param studio
	 */
	public static void addFramedCircularLens(
			String description,
			Vector3D principalPoint,
			Vector3D normal,
			double focalLength,
			Vector3D insideSpacePoint,
			Vector3D outsideSpacePoint,
			LensType lensType,
			double radius,
			double frameWidth,
			SurfaceProperty frameSurfaceProperty,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		SurfaceProperty surface;
		switch(lensType)
		{
		case LENS_HOLOGRAM:
			surface = new LensHologram(
					normal,	// opticalAxisDirectionOutwards
					principalPoint,
					focalLength,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case LENS_HOLOGRAM_EYE:
			surface = new Point2PointImagingPhaseHologram(
					insideSpacePoint,
					outsideSpacePoint,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					true	// shadowThrowing
				);
			break;
		case IDEAL_THIN_LENS:
		default:
			surface = new IdealThinLensSurfaceSimple(
					principalPoint,	// lensCentre
					normal,	// opticalAxisDirection
					focalLength,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
				);
		}
		EditableScaledParametrisedDisc lens = new EditableScaledParametrisedDisc(
				description,
				principalPoint,	// centre
				normal,	// normal
				radius,	// radius
				surface,	// surfaceProperty
				scene, studio
			);
		scene.addSceneObject(lens);
		
		EditableParametrisedCylinder frameOutside = new EditableParametrisedCylinder(
				description + " (frame outside)",	// description
				Vector3D.sum(principalPoint, normal.getWithLength( 0.5*frameWidth)),	// startPoint
				Vector3D.sum(principalPoint, normal.getWithLength(-0.5*frameWidth)),	// endPoint
				radius + frameWidth,	// radius
				frameSurfaceProperty,
				scene, studio
		);
		EditableParametrisedCylinder frameInside = new EditableParametrisedCylinder(
				description + " (frame inside)",	// description
				Vector3D.sum(principalPoint, normal.getWithLength( frameWidth)),	// startPoint
				Vector3D.sum(principalPoint, normal.getWithLength(-frameWidth)),	// endPoint
				radius,	// radius
				frameSurfaceProperty,
				scene, studio
		);
		SceneObjectDifference frame = new SceneObjectDifference(
				description + " (frame)",	// description
				frameOutside, frameInside,
				scene, studio
			);
		scene.addSceneObject(frame);
	}
	
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
//		testCheckBox = new JCheckBox("Test");
//		testCheckBox.setSelected(test);
//		testCheckBox.addActionListener(this);
//		interactiveControlPanel.add(testCheckBox);

		alpha1Panel = new LabelledDoublePanel("alpha_1 (degrees)");
		alpha1Panel.setNumber(alpha1);
		interactiveControlPanel.add(alpha1Panel, "span");

		alpha2Panel = new LabelledDoublePanel("alpha_2 (degrees)");
		alpha2Panel.setNumber(alpha2);
		interactiveControlPanel.add(alpha2Panel, "span");

		alpha3Panel = new LabelledDoublePanel("alpha_3 (degrees)");
		alpha3Panel.setNumber(alpha3);
		interactiveControlPanel.add(alpha3Panel, "span");

		f1Panel = new LabelledDoublePanel("f_1");
		f1Panel.setNumber(f1);
		interactiveControlPanel.add(f1Panel, "span");

		f2Panel = new LabelledDoublePanel("f_2");
		f2Panel.setNumber(f2);
		interactiveControlPanel.add(f2Panel, "span");
		
		f3Panel = new LabelledDoublePanel("f_3");
		f3Panel.setNumber(f3);
		interactiveControlPanel.add(f3Panel, "span");
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		interactiveControlPanel.add(cameraFocussingDistancePanel, "span");
		
		cameraApertureSizeComboBox = new ApertureSizeComboBox();
		cameraApertureSizeComboBox.setApertureSize(cameraApertureSize);
		interactiveControlPanel.add(new LabelledComponent("Camera aperture", cameraApertureSizeComboBox), "span");
				
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		interactiveControlPanel.add(new LabelledComponent("Lens type", lensTypeComboBox), "span");

		topViewCheckBox = new JCheckBox("Top view");
		topViewCheckBox.setSelected(topView);
		interactiveControlPanel.add(topViewCheckBox, "span");

		showTrajectoriesCheckBox = new JCheckBox("Show trajectories (top view only)");
		showTrajectoriesCheckBox.setSelected(showTrajectories);
		interactiveControlPanel.add(showTrajectoriesCheckBox, "span");
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		alpha1 = alpha1Panel.getNumber();
		alpha2 = alpha2Panel.getNumber();
		alpha3 = alpha3Panel.getNumber();
		calculateN123FromAlpha123();

		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		f3 = f3Panel.getNumber();
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraApertureSize = cameraApertureSizeComboBox.getApertureSize();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		topView = topViewCheckBox.isSelected();
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
		(new ThreeSkewLenses()).run();
	}
}
