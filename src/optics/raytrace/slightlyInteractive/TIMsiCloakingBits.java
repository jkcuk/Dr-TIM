package optics.raytrace.slightlyInteractive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.Dial;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsCloak;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsCloak.GCLACloakType;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A version of TIMSlightlyInteractiveBits that allows experimentation with
 * a gCLAs cloak
 */
public class TIMsiCloakingBits extends TIMsiBits
implements ActionListener, ChangeListener
{
	// add any parameters, and the panels for editing them, here
	private GCLACloakType cloakType = GCLACloakType.OCTAHEDRAL;
	private JComboBox<GCLACloakType> cloakTypeComboBox;
	
	private boolean showFrames = false, cloakVisible = false;
	private JCheckBox showFramesCheckBox, cloakVisibleCheckBox;
	
	private double cameraDistance = 5;
	private JSlider cameraDistanceSlider;
	
	private double cameraPolarPosition = 0;
	private JSlider cameraPolarPositionSlider;
	
	private int cameraAzimuthalPosition = 0;
	private Dial cameraAzimuthalPositionDial;

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public JPanel createParametersPanel()
	{
		JPanel editFields = new JPanel();
		editFields.setLayout(new MigLayout("insets 0"));
		
		cloakVisibleCheckBox = new JCheckBox("Show cloak");
		cloakVisibleCheckBox.addActionListener(this);
		editFields.add(cloakVisibleCheckBox);

		cloakTypeComboBox = new JComboBox<GCLACloakType>(GCLACloakType.values());
		cloakTypeComboBox.addActionListener(this);
		editFields.add(new JLabel("Cloak type"));
		editFields.add(cloakTypeComboBox, "wrap");

		showFramesCheckBox = new JCheckBox("Show frames");
		showFramesCheckBox.addActionListener(this);
		editFields.add(showFramesCheckBox, "wrap");
		
		JPanel cameraAzimuthalPositionPanel = new JPanel();
		cameraAzimuthalPositionPanel.setLayout(new MigLayout("wrap 3"));
		cameraAzimuthalPositionPanel.add(new JLabel("North"), "span");
		cameraAzimuthalPositionPanel.add(new JLabel("West"));
		cameraAzimuthalPositionDial = new Dial(0, 359, 0);
		cameraAzimuthalPositionDial.addChangeListener(this);
		cameraAzimuthalPositionPanel.add(cameraAzimuthalPositionDial);
		cameraAzimuthalPositionPanel.add(new JLabel("East"), "wrap");
		cameraAzimuthalPositionPanel.add(new JLabel("South"), "span");
		
		editFields.add(cameraAzimuthalPositionPanel);
		
		cameraPolarPositionSlider = new JSlider(SwingConstants.VERTICAL, -100, 8900, 0);
		cameraPolarPositionSlider.addChangeListener(this);
		editFields.add(cameraPolarPositionSlider);
		
		cameraDistanceSlider = new JSlider(SwingConstants.HORIZONTAL, 100, 1000, 500);
		cameraDistanceSlider.addChangeListener(this);
		editFields.add(cameraDistanceSlider);

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
		cloakVisibleCheckBox.setSelected(cloakVisible);
		cloakTypeComboBox.setSelectedItem(cloakType);
		showFramesCheckBox.setSelected(showFrames);
		cameraAzimuthalPositionDial.setValue(cameraAzimuthalPosition);
		cameraPolarPositionSlider.setValue((int)(cameraPolarPosition*100));
		cameraDistanceSlider.setValue((int)(cameraDistance*100));
	}

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public void acceptValuesInParametersPanel()
	{
		setCloakVisible(cloakVisibleCheckBox.isSelected());
		setCloakType((GCLACloakType)(cloakTypeComboBox.getSelectedItem()));
		setShowFrames(showFramesCheckBox.isSelected());
		setCameraAzimuthalPosition(cameraAzimuthalPositionDial.getValue());
		setCameraPolarPosition(cameraPolarPositionSlider.getValue()/100.);
		setCameraDistance(cameraDistanceSlider.getValue()/100.);
		
		// enable or disable components as necessary
		cloakTypeComboBox.setEnabled(cloakVisible);
		showFramesCheckBox.setEnabled(cloakVisible);
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
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));

		// ... and the chequerboard floor
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));
		
		// add any other scene objects
		
		// a sphere to cloak
		scene.addSceneObject(
				new EditableScaledParametrisedSphere(
						"cloaked sphere",	// description
						new Vector3D(0, 0, 0),	// centre
						0.2,	// radius
						new Vector3D(0, 1, 0),	// pole
						new Vector3D(1, 0, 0),	// phi0Direction,
						0, 180,	// sThetaMin, sThetaMax
						-180, 180,	// sPhiMin, sPhiMax
						SurfaceColour.GREEN_SHINY,	// surfaceProperty
						scene,	// parent
						studio
					)
			);

		// and a cloak
		scene.addSceneObject(
				new EditableGCLAsCloak(
						"Cloak",
						new Vector3D(0, 0, 0),	// centre
						new Vector3D(0, 0, -1),	// front direction
						new Vector3D(1, 0, 0),	// right direction
						new Vector3D(0, 1, 0),	// top direction
						1.0,	// side length
						0.07,	// a
						0.7,	// aPrime
						0.96,	// gCLA transmission coefficient
						cloakType,	// cloak type
						showFrames,	// show frames
						0.01,	// frame radius / side length
						SurfaceColour.GREY50_SHINY,	// frame surface property
						scene,	// parent, 
						studio	// studio
					),
				cloakVisible
			);
		
		// a ray-trajectory cone
		// a light ray with trajectory, which bounces about between the two mirrors;
		// to trace the rays with trajectory through the scene, need to add
		// "studio.traceRaysWithTrajectory();" later
//		scene.addSceneObject(
//				new EditableRayTrajectoryCone(
//						"fuzzy ray trajectory cone",
//						new Vector3D(-2, conePosition, 10),	// start point
//						0,	// start time
//						new Vector3D(1, 0, 0),	// initial direction
//						MyMath.deg2rad(10),	// cone angle
//						10,	// number of rays
//						0.02,	// radius of individual rays
//						SurfaceColour.RED_SHINY,
//						// new SurfaceOfGlowingCloudPositionDependent(DoubleColour.RED, 0.2),
//						100,	// max trace level
//						scene,
//						studio
//				)
//		);
		
		studio.setScene(scene);

		//
		// finally, the camera
		//
		
		EditableSceneObjectCollection focusScene = new EditableSceneObjectCollection("focus scene", false, studio.getScene(), studio);
		focusScene.addSceneObject(new EditableParametrisedPlane(
				"focussing plane",
				new Vector3D(0, 0, 0),	// point on plane
				new Vector3D(0, 0, 1),	// normal to plane
				SurfaceColour.BLACK_SHINY,
				studio.getScene(),
				studio
		));
		double
			cameraPhi = MyMath.deg2rad(cameraAzimuthalPosition),
			cosCameraPhi = Math.cos(cameraPhi),
			sinCameraPhi = Math.sin(cameraPhi),
			cameraTheta = MyMath.deg2rad(cameraPolarPosition),
			cosCameraTheta = Math.cos(cameraTheta),
			sinCameraTheta = Math.sin(cameraTheta);
		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"Eye view",
				new Vector3D(cameraDistance*cosCameraTheta*sinCameraPhi, cameraDistance*sinCameraTheta, -cameraDistance*cosCameraTheta*cosCameraPhi),	// centre of aperture
				new Vector3D(-cosCameraTheta*sinCameraPhi, -sinCameraTheta, cosCameraTheta*cosCameraPhi),	// viewDirection
				new Vector3D(0, 1, 0),	// top direction vector
				20,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,
				1000,	// maxTraceLevel
				focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio),
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);
//
//		EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
//				"camera",
//				new Vector3D(0, 0, 0),	// centre of aperture
//				new Vector3D(0, 0, 5),	// lookAtPoint: the point in the centre of the field of view
//				new Vector3D(1, 0, 0),	// horizontal span vector
//				new Vector3D(0, 0, 0),	// beta
//				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
//				100,	// maxTraceLevel
//				focusScene,
//				new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio),
//				ApertureSizeType.PINHOLE,	// aperture size
//				QualityType.RUBBISH,	// blur quality
//				QualityType.NORMAL	// anti-aliasing quality
//		);
		
		studio.setCamera(camera);
	}

	public GCLACloakType getCloakType() {
		return cloakType;
	}

	public void setCloakType(GCLACloakType cloakType) {
		this.cloakType = cloakType;
	}

	public boolean getCloakVisible() {
		return cloakVisible;
	}

	public void setCloakVisible(boolean cloakVisible) {
		this.cloakVisible = cloakVisible;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}

	public int getCameraAzimuthalPosition() {
		return cameraAzimuthalPosition;
	}

	public void setCameraAzimuthalPosition(int cameraAzimuthalPosition) {
		this.cameraAzimuthalPosition = cameraAzimuthalPosition;
	}

	public double getCameraPolarPosition() {
		return cameraPolarPosition;
	}

	public void setCameraPolarPosition(double cameraPolarPosition) {
		this.cameraPolarPosition = cameraPolarPosition;
	}

	public double getCameraDistance() {
		return cameraDistance;
	}

	public void setCameraDistance(double cameraDistance) {
		this.cameraDistance = cameraDistance;
	}

	// the amount by which the iPanel is bigger than the image canvas;
	@Override
	protected int getAdditionalWidth()
	{
		return 200;
	}
	
	@Override
	protected int getAdditionalHeight()
	{
		return 400;
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if(arg0.getSource() == cameraDistanceSlider)
		{
			if(!(cameraDistanceSlider.getValueIsAdjusting()))
			{
				render();
			}
		}
		else if(arg0.getSource() == cameraPolarPositionSlider)
		{
			if(!(cameraPolarPositionSlider.getValueIsAdjusting()))
			{
				render();
			}
		}
		else if(arg0.getSource() == cameraAzimuthalPositionDial)
		{
			render();
		}
		else render();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		render();
	}
}
