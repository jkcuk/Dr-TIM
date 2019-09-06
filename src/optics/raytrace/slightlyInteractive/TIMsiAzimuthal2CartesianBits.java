package optics.raytrace.slightlyInteractive;

import javax.swing.*;

import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledQualityComboBox;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditablePolarToCartesianConverter;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryCone;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;

/**
 * A version of TIMSlightlyInteractiveBits that allows experimentation with an azimuthal-to-Cartesian converter
 */
public class TIMsiAzimuthal2CartesianBits extends TIMsiBits
{
	// add any parameters, and the panels for editing them, here
	private double conePosition = 0;
	private LabelledDoublePanel conePositionPanel;
	
	private QualityType antiAliasingQuality = QualityType.NORMAL;
	private LabelledQualityComboBox antiAliasingQualityPanel;

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public JPanel createParametersPanel()
	{
		JPanel editFields = new JPanel();
		editFields.setLayout(new BoxLayout(editFields, BoxLayout.Y_AXIS));
		
		conePositionPanel = new LabelledDoublePanel("cone position");
		editFields.add(conePositionPanel);
		
		antiAliasingQualityPanel = new LabelledQualityComboBox("Anti-aliasing quality");
		editFields.add(antiAliasingQualityPanel);

		editFields.validate();
		
		return editFields;
	}
	
	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public void setValuesInParametersPanel()
	{
		// initialize any fields
		conePositionPanel.setNumber(conePosition);
		antiAliasingQualityPanel.setQuality(antiAliasingQuality);
	}

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public void acceptValuesInParametersPanel()
	{
		setConePosition(conePositionPanel.getNumber());
		setAntiAliasingQuality(antiAliasingQualityPanel.getQuality());
	}
	
	public double getConePosition() {
		return conePosition;
	}

	public void setConePosition(double conePosition) {
		this.conePosition = conePosition;
	}

	public QualityType getAntiAliasingQuality() {
		return antiAliasingQuality;
	}

	public void setAntiAliasingQuality(QualityType antiAliasingQuality) {
		this.antiAliasingQuality = antiAliasingQuality;
	}

	/**
	 * Alter or override this method to change the scene.
	 * Called both when run as an applet (then this is the initial scene to be rendered)
	 * and when run as a Java application (then this is the scene, which then gets
	 * rendered).
	 */
	@Override
	public void populateStudio()
	{
		//
		// the lights
		//
		
		// studio.setLights(LightSource.getBrightLightsFromBehind());
		studio.setLights(LightSource.getStandardLightsFromBehind());

		//
		// the scene
		//
		
		EditableSceneObjectCollection scene = new EditableSceneObjectCollection("the scene", false, null, studio);
		
		// populate the scene with the standard scene objects, the sky sphere...
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"sky",
				new Vector3D(0,0,0),	// centre
				MyMath.HUGE,	// huge radius
				new SurfaceColourLightSourceIndependent(DoubleColour.LIGHT_BLUE, true),
				scene, 
				studio
		));

		// ... and the chequerboard floor
		scene.addSceneObject(new EditableParametrisedPlane(
				"chequerboard floor", 
				new Vector3D(0, -1, 0),	// point on plane
				new Vector3D(0, 0, 1),	// Vector3D 1 that spans plane
				new Vector3D(1, 0, 0),	// Vector3D 2 that spans plane
				// true,	// shadow-throwing
				SurfacePropertyPanel.TILED, // new SurfaceTiling(SurfaceColour.GREY80_SHINY, SurfaceColour.WHITE_SHINY, 1, 1)
				scene,
				studio
		));
		
		// add any other scene objects
		
		// an azimuthal-to-Cartesian converter
		scene.addSceneObject(
				new EditablePolarToCartesianConverter(
						"polar-Cartesian converter",	// description
						new Vector3D(0.5, 0, 10),	// centre
						new Vector3D(1, 0, 0),	// polarFaceNormal
						new Vector3D(0, 1, 0),	// phi0Direction
						new Vector3D(0, 1, 0),	// xDirection
						2,	// sideLength
						1,	// separation
						scene,	// parent 
						studio
				)
			);
		
		// a lens
		scene.addSceneObject(new ParametrisedDisc(
				"thin lens",	//description,
				new Vector3D(-1, 0, 10),	// centre,
				new Vector3D(1, 0, 0),	// normal,
				1,	// radius,
				new Vector3D(0, 1, 0),	// phi0Direction,
				new IdealThinLensSurfaceSurfaceCoordinates(
						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
						1,	// focalLength,
						0.9,	// transmissionCoefficient
						true	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
		));

		
		// a ray-trajectory cone
		// a light ray with trajectory, which bounces about between the two mirrors;
		// to trace the rays with trajectory through the scene, need to add
		// "studio.traceRaysWithTrajectory();" later
		scene.addSceneObject(
				new EditableRayTrajectoryCone(
						"fuzzy ray trajectory cone",
						new Vector3D(-2, conePosition, 10),	// start point
						0,	// start time
						new Vector3D(1, 0, 0),	// initial direction
						MyMath.deg2rad(10),	// cone angle
						10,	// number of rays
						0.02,	// radius of individual rays
						SurfaceColour.RED_SHINY,
						// new SurfaceOfGlowingCloudPositionDependent(DoubleColour.RED, 0.2),
						100,	// max trace level
						scene,
						studio
				)
		);
		
		studio.setScene(scene);

		//
		// finally, the camera
		//
				
		EditableSceneObjectCollection focusScene = new EditableSceneObjectCollection("focus scene", false, studio.getScene(), studio);
		focusScene.addSceneObject(new EditableParametrisedPlane(
				"focussing plane",
				new Vector3D(0, 0, 15),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				SurfaceColour.BLACK_SHINY,
				studio.getScene(),
				studio
		));
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"camera",
				new Vector3D(-4, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 10),	// lookAtPoint: the point in the centre of the field of view
				new Vector3D(6, 0, 0),	// horizontal span vector
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio),
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				antiAliasingQuality	// anti-aliasing quality
		);
		
		studio.setCamera(camera);
	}

	// the amount by which the iPanel is bigger than the image canvas;
	@Override
	protected int getAdditionalWidth()
	{
		return 20;
	}
	
	@Override
	protected int getAdditionalHeight()
	{
		return 140;
	}
}
