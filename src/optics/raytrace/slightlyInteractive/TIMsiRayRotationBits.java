package optics.raytrace.slightlyInteractive;

import java.awt.FlowLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.Complex;
import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.RefractiveComplex;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;

/**
 * A version of TIMSlightlyInteractiveBits that allows experimentation with ray rotation
 */
public class TIMsiRayRotationBits extends TIMsiBits implements ChangeListener
{
	private Complex n = new Complex(Math.cos(MyMath.deg2rad(45)), Math.sin(MyMath.deg2rad(45)));
	
	// add any parameters, and the panels for editing them, here
	private JTabbedPane refractionParametersPane;

	private DoublePanel rayRotationAnglePanel;
	
	private DoublePanel absRefractiveIndexPanel;

	// real part of the refractive index
	private DoublePanel reRefractiveIndexPanel;

	// imaginary part of the refractive index
	private DoublePanel imRefractiveIndexPanel;

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public JPanel createParametersPanel()
	{
		JPanel editFields = new JPanel();
		editFields.setLayout(new BoxLayout(editFields, BoxLayout.Y_AXIS));
		
		// editFields.add(new JLabel("ray-optical refractive index ratio:"));
		
		refractionParametersPane = new JTabbedPane();
		editFields.add(refractionParametersPane);
		
		// create a new panel to hold one form of the parameters, namely |n| and the ray-rotation angle
		JPanel nAndRRAnglePanel = new JPanel();
		nAndRRAnglePanel.setLayout(new FlowLayout());
		
		nAndRRAnglePanel.add(new JLabel("Snell's refractive index:"));
		
		absRefractiveIndexPanel = new DoublePanel();
		nAndRRAnglePanel.add(absRefractiveIndexPanel);
		
		nAndRRAnglePanel.add(new JLabel(", ray-rotation angle:"));
		
		rayRotationAnglePanel = new DoublePanel();
		nAndRRAnglePanel.add(rayRotationAnglePanel);

		nAndRRAnglePanel.add(new JLabel("�"));

		refractionParametersPane.addTab("Snell's refr. index and ray-rotation angle", nAndRRAnglePanel);
				
		// create a new panel to hold one form of the parameters, namely re(n) and im(n)
		JPanel reAndImPanel = new JPanel();
		reAndImPanel.setLayout(new FlowLayout());

		reAndImPanel.add(new JLabel("n = ")); 

		reRefractiveIndexPanel = new DoublePanel();
		reAndImPanel.add(reRefractiveIndexPanel);

		reAndImPanel.add(new JLabel(" + i * ")); 

		imRefractiveIndexPanel = new DoublePanel();
		reAndImPanel.add(imRefractiveIndexPanel);

		refractionParametersPane.addTab("real and imaginary part of ray-optical refr. index", reAndImPanel);

		// set values here so that all values are set
		// n = Complex.fromPolar(1, MyMath.deg2rad(20));
		setValuesInParametersPanel();
		
		editFields.validate();
		
		// so that the values in the pane can be updated so they correspond to those of the previously selected pane
		refractionParametersPane.addChangeListener(this);
		
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
		absRefractiveIndexPanel.setNumber(getN().getMod());
		rayRotationAnglePanel.setNumber(MyMath.rad2deg(getN().getArg()));
		reRefractiveIndexPanel.setNumber(getN().getRe());
		imRefractiveIndexPanel.setNumber(getN().getIm());
	}

	/**
	 * Override this method to edit other things
	 * @return
	 */
	@Override
	public void acceptValuesInParametersPanel()
	{
		acceptValuesInParametersPanel(refractionParametersPane.getSelectedIndex());
	}

	public void acceptValuesInParametersPanel(int selectedIndex)
	{
		if(selectedIndex == 0)
		{
			// abs(n) and ray-rotation angle
			setNFromAbsNAndRRAngle(absRefractiveIndexPanel.getNumber(), rayRotationAnglePanel.getNumber());
		}
		else
		{
			setNFromReAndIm(reRefractiveIndexPanel.getNumber(), imRefractiveIndexPanel.getNumber());
		}
		
		setValuesInParametersPanel();
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		// we have just changed the values in the pane corresponding to the other selected index, so...
		acceptValuesInParametersPanel(1-refractionParametersPane.getSelectedIndex());
	}
	
	public Complex getN()
	{
		return n;
	}

	public void setNFromAbsNAndRRAngle(double absRefractiveIndex, double rayRotationAngle)
	{
		n = Complex.fromPolar(
				absRefractiveIndexPanel.getNumber(),
				MyMath.deg2rad(rayRotationAnglePanel.getNumber())	// ray-rotation angle, converted into radians
			);
	}
	
	public void setNFromReAndIm(double reRefractiveIndex, double imRefractiveIndex)
	{
		n = new Complex(reRefractiveIndex, imRefractiveIndex);
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
		
		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1, 1, 4,
				10, 25, 4,
				0.02,
				scene,
				studio
		));

		// ... behind a ray-rotating window...
		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"ray-rotating window",
				new Vector3D(0, 0, 9),	// centre
				new Vector3D(-3, 0, 0),	// width vector
				new Vector3D(0, 2, 0),	// height vector
				new RefractiveComplex(getN(), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
				scene,
				studio
		));
		
		// ... with a frame
		scene.addSceneObject(new EditableCylinderFrame(
				"window frame",
				new Vector3D(-1.5, -1, 9),
				new Vector3D(3, 0, 0),
				new Vector3D(0, 2, 0),
				0.025,
				SurfaceColour.GREY50_SHINY,
				scene,
				studio
		));
		
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
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 10),	// lookAtPoint: the point in the centre of the field of view
				new Vector3D(4, 0, 0),	// horizontal span vector
				new Vector3D(0, 0, 0),	// beta
				imageCanvasSizeX, imageCanvasSizeY,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				focusScene,
				new EditableSceneObjectCollection("camera-frame scene", false, studio.getScene(), studio),
				ApertureSizeType.PINHOLE,	// aperture size
				QualityType.RUBBISH,	// blur quality
				QualityType.NORMAL	// anti-aliasing quality
		);
		
		studio.setCamera(camera);
	}
	
	@Override
	protected int getAdditionalHeight()
	{
		return 150;
	}
}
