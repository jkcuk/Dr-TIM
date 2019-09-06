package optics.raytrace.research.lenses;


import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.GlensHologram;
import optics.raytrace.surfaces.Point2PointImaging;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;
import optics.raytrace.surfaces.SurfacePropertyLayerStack;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeComboBox;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.Dial;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.lowLevel.LensElementTypeComboBox;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableThickLens;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTriangle;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;


/**
 * Allows playing around with various physical realisations of lenses, e.g. bits of glass, phase holograms, ....
 * Not finished yet, so taken out of the build path for the moment
 * 
 * @author Johannes Courtial
 */
public class PhysicalLensTest extends NonInteractiveTIMEngine
implements ChangeListener
{
	// additional parameters
	
	/**
	 * the physical realisation
	 */
	protected LensElementType lensElementType;

	/**
	 * focal length
	 */
	protected double focalLength;

	/**
	 * azimuthal angle of the view direction
	 */
	protected double cameraAzimuthalAngle;
	
	/**
	 * polar angle of the view direction
	 */
	protected double cameraPolarAngle;
	
	
	// GUI variables
	
	private JSlider cameraPolarAngleSlider;
	private Dial cameraAzimuthalAngleDial;
	private LensElementTypeComboBox lensElementTypeComboBox;
	private ApertureSizeComboBox apertureSizeComboBox;
	private DoublePanel focalLengthPanel, cameraDistancePanel, cameraFocussingDistancePanel;

	
	// internal variables
	protected Vector3D principalPoint = new Vector3D(0, 0, 0);
	protected Vector3D opticalAxisDirection = new Vector3D(0, 0, 1);

	

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PhysicalLensTest()
	{
		super();
		
		test = true;
		interactive = true;
		movie = false;
		if(movie)
		{
			movieNumberOfFrames = 50;
			movieFirstFrame = 0;
			movieLastFrame = movieNumberOfFrames-1;
		}
		
		lensElementType = LensElementType.IDEAL_THIN_LENS;
		focalLength = 10;
		cameraAzimuthalAngle = 0;

		cameraDistance = 10;
		cameraFocussingDistance = 10;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		// other camera parameters are set in createStudio()
	}

	
	public LensElementType getLensElementType() {
		return lensElementType;
	}


	public void setLensElementType(LensElementType lensElementType) {
		this.lensElementType = lensElementType;
	}


	public double getFocalLength() {
		return focalLength;
	}


	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}


	public double getCameraAzimuthalAngle() {
		return cameraAzimuthalAngle;
	}


	public void setCameraAzimuthalAngle(double cameraAzimuthalAngle) {
		this.cameraAzimuthalAngle = cameraAzimuthalAngle;
	}


	public double getCameraPolarAngle() {
		return cameraPolarAngle;
	}


	public void setCameraPolarAngle(double cameraPolarAngle) {
		this.cameraPolarAngle = cameraPolarAngle;
	}



	@Override
	public String getFirstPartOfFilename()
	{
		return
				"PhysicalLensTest"
				+ " lens type="+lensElementType
				+ " f="+focalLength
				+ " angle="+cameraAzimuthalAngle+"°"
				;
	}
	
	/**
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return a disc with the given surface property
	 */
	private SceneObjectPrimitive getDisc(SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		return new EditableScaledParametrisedDisc(
				"Disc",	// description
				principalPoint,	// centre
				opticalAxisDirection,	// normal
				1,	// radius
				surfaceProperty,
				parent, 
				studio
		);
	}
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		// double phi = -0.25+(movie?2.*Math.PI*frame/(movieNumberOfFrames+1):0);
		double
			cameraPhi = MyMath.deg2rad(cameraAzimuthalAngle),
			cosCameraPhi = Math.cos(cameraPhi),
			sinCameraPhi = Math.sin(cameraPhi),
			cameraTheta = MyMath.deg2rad(cameraPolarAngle),
			cosCameraTheta = Math.cos(cameraTheta),
			sinCameraTheta = Math.sin(cameraTheta);
		
		cameraViewDirection = new Vector3D(-cosCameraTheta*sinCameraPhi, -sinCameraTheta, cosCameraTheta*cosCameraPhi);	// new Vector3D(-Math.sin(cameraAzimuthalAngle), -.2, Math.cos(cameraAzimuthalAngle));
		cameraViewCentre = principalPoint;
		cameraHorizontalFOV = 20;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();


		// this is where the lens goes
		SceneObject lens;
		
		double surfaceTransmissionCoefficient = 0.9;
		
		switch(lensElementType)
		{
		case GLASS_PANE:
			// a surface that doesn't change light-ray direction
			lens = getDisc(
					new GlensHologram(
							surfaceTransmissionCoefficient,	// transmission coefficient
							true
						),	// surfaceProperty 
					scene,	// parent
					studio
				);
			break;
		case PHASE_HOLOGRAM_OF_LENS:
			// a phase hologram of a lens, realised with a Point2PointImaging surface
			// The Point2PointImaging surface takes as arguments two points, one in inside space, the other in
			// outside space.
			// Calculate the outwards normal, which should be colinear with the optical-axis direction;
			// the lens is the surface of an EditableTriangle SceneObject, and its outwards normal is calculated
			// as follows:
			lens = getDisc(
					new Point2PointImagingPhaseHologram(
							Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-2*focalLength)),	// inside-space point, which is imaged to the outside-space point
							Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith( 2*focalLength)),	// outside-space point
							surfaceTransmissionCoefficient,	// transmission coefficient
							false,	// reflective
							true	// shadowThrowing
						),	// surfaceProperty 
					scene,	// parent
					studio
				);
			break;
		case SYMMETRIC_GLASS_LENS:
			double refractiveIndex = 1.5;
			// the surface that does the refraction
			Refractive refractive = new Refractive(refractiveIndex, surfaceTransmissionCoefficient, true);
			// the lens surface, which can be slightly fancier to make the lens look more realistic in non-test mode
			SurfaceProperty lensSurface;
			// set the lens surface...
			if(test)
			{
				// ... simply to the refractive surface (in test mode) ...
				lensSurface = refractive;
			}
			else
			{
				// ... or to the combination of the refractive surface and a surface that gives the glass some
				// subtle colour (in non-test mode)
				lensSurface =
						new SurfacePropertyLayerStack(
								refractive,
								new SurfaceOfTintedSolid(
										DoubleColour.complementaryColour(DoubleColour.CYAN),
										false
										)
							);
			}
			lens = new EditableThickLens(
					"Symmetric glass lens",	// description
					1,	// apertureRadius
					2*focalLength*(refractiveIndex - 1),	// radiusOfCurvatureFront
					2*focalLength*(refractiveIndex - 1),	// radiusOfCurvatureBack
					principalPoint,
					opticalAxisDirection,
					lensSurface,	// surfacePropertyFront
					lensSurface,	// surfacePropertyBack
					scene,	// parent
					studio
				);
			break;
		case IDEAL_THIN_LENS:
		default:
			// basically the surface of an ideal thin lens
			GlensHologram lensHologram = new GlensHologram(
					0.9,	// transmission coefficient
					true
				);
			lensHologram.setParametersUsingNodalPoint(new Vector3D(0, 0, 1), new Vector3D(0, 0, 0), -focalLength, focalLength);
			lens = getDisc(
					lensHologram,	// surfaceProperty 
					scene,	// parent
					studio
				);
			break;
		}
		scene.addSceneObject(lens);
		
	}
	
	protected void colourLens(EditableIdealLensCloak cloak, String name)
	{
		// find the lens in the cloak
		EditableTriangle lens = (EditableTriangle)(cloak.getFirstSceneObjectWithDescription(name, true));
		
		// make its surface visible by giving it a cyan tint (and don't add a shadow)
		lens.setSurfaceProperty(
				new SurfacePropertyLayerStack(
						lens.getSurfaceProperty(),
						new ColourFilter(DoubleColour.CYAN, false)
					)
			);
	}
	
	protected void addNodalPointSphere(Vector3D centre, SceneObjectContainer scene, double radius)
	{
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"Nodal point",	// description
				centre,
				radius,
				SurfaceColour.BLACK_SHINY,	// surfaceProperty
				scene,	// parent
				scene.getStudio()	// studio
			));
	}
	
	public static void main(final String[] args)
	{
        Runnable r = new PhysicalLensTest();

        EventQueue.invokeLater(r);
	}


	//
	// GUI stuff
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#createInteractiveControlPanel()
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		lensElementTypeComboBox = new LensElementTypeComboBox(false);
		lensElementTypeComboBox.setSelectedItem(lensElementType);
		lensElementTypeComboBox.addActionListener(this);
		interactiveControlPanel.add(lensElementTypeComboBox, "wrap");
		
		focalLengthPanel = new DoublePanel();
		focalLengthPanel.setNumber(focalLength);
		focalLengthPanel.addChangeListener(this);
		interactiveControlPanel.add(new LabelledComponent("Focal length", focalLengthPanel), "wrap");
		
		JPanel cameraAzimuthalPositionPanel = new JPanel();
		cameraAzimuthalPositionPanel.setLayout(new BorderLayout());
		JLabel northLabel = new JLabel("North");
		northLabel.setHorizontalAlignment(JLabel.CENTER);
		cameraAzimuthalPositionPanel.add(northLabel, BorderLayout.NORTH);
		JLabel southLabel = new JLabel("South");
		southLabel.setHorizontalAlignment(JLabel.CENTER);
		cameraAzimuthalPositionPanel.add(southLabel, BorderLayout.SOUTH);
		cameraAzimuthalPositionPanel.add(new JLabel("West"), BorderLayout.WEST);
		cameraAzimuthalPositionPanel.add(new JLabel("East"), BorderLayout.EAST);
		cameraAzimuthalAngleDial = new Dial(0, 359, 0);
		cameraAzimuthalAngleDial.setValue((int)cameraAzimuthalAngle);
		cameraAzimuthalAngleDial.addChangeListener(this);
		cameraAzimuthalPositionPanel.add(cameraAzimuthalAngleDial);
		
		interactiveControlPanel.add(cameraAzimuthalPositionPanel, "split 2");
		
		cameraPolarAngleSlider = new JSlider(JSlider.VERTICAL, -100, 8900, 0);
		cameraPolarAngleSlider.setValue((int)(cameraPolarAngle*100));
		cameraPolarAngleSlider.addChangeListener(this);
		interactiveControlPanel.add(cameraPolarAngleSlider, "wrap");
		
		cameraDistancePanel = new DoublePanel();
		cameraDistancePanel.setNumber(cameraDistance);
		cameraDistancePanel.addChangeListener(this);
		interactiveControlPanel.add(new LabelledComponent("Camera distance", cameraDistancePanel), "wrap");

		apertureSizeComboBox = new ApertureSizeComboBox();
		apertureSizeComboBox.setSelectedItem(cameraApertureSize);
		apertureSizeComboBox.addActionListener(this);
		interactiveControlPanel.add(new LabelledComponent("Aperture size", apertureSizeComboBox), "wrap");

		cameraFocussingDistancePanel = new DoublePanel();
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraFocussingDistancePanel.addChangeListener(this);
		interactiveControlPanel.add(new LabelledComponent("Focussing distance", cameraFocussingDistancePanel), "wrap");
		
	}
	
	
	// listeners

//	@Override
//	public void dialAdjusted(DialEvent e)
//	{
//		if(e.getSource().equals(cameraAzimuthalAngleDial))
//		{
//			setCameraAzimuthalAngle(cameraAzimuthalAngleDial.getValue());
//			render();
//		}
//	}


	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource().equals(cameraPolarAngleSlider))
		{
			if(!(cameraPolarAngleSlider.getValueIsAdjusting()))
			{
				setCameraPolarAngle(cameraPolarAngleSlider.getValue()/100.);
				render();
			}
		}
		else if(e.getSource().equals(focalLengthPanel))
		{
			if(!(focalLengthPanel.isValueIsAdjusting()))
			{
				setFocalLength(focalLengthPanel.getNumber());
				render();
			}
		}
		else if(e.getSource().equals(cameraDistancePanel))
		{
			if(!(cameraDistancePanel.isValueIsAdjusting()))
			{
				setCameraDistance(cameraDistancePanel.getNumber());
				render();
			}
		}
		else if(e.getSource().equals(cameraFocussingDistancePanel))
		{
			if(!(cameraFocussingDistancePanel.isValueIsAdjusting()))
			{
				setCameraFocussingDistance(cameraFocussingDistancePanel.getNumber());
				render();
			}
		}
		else if(e.getSource().equals(cameraAzimuthalAngleDial))
		{
			setCameraAzimuthalAngle(cameraAzimuthalAngleDial.getValue());
			render();
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(lensElementTypeComboBox))
		{
			setLensElementType((LensElementType)(lensElementTypeComboBox.getSelectedItem()));
			render();
		}
		else if(e.getSource().equals(apertureSizeComboBox))
		{
			setCameraApertureSize((ApertureSizeType)(apertureSizeComboBox.getSelectedItem()));
			cameraFocussingDistancePanel.setEnabled(getCameraApertureSize() != ApertureSizeType.PINHOLE);
			render();
		}
	}
}
