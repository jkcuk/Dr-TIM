package optics.raytrace.research.adaptiveFresnelLens;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc.DiscParametrisationType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.PhaseHologramOfLogarithmicCylindricalLensSpiral;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Simulate light-ray trajectories through a combination of two cylindrical lenses.
 * The "principal line" of one lens is given by x_1 = y_1 = 0.
 * That of the other lens is given by x_2, y_2.
 * The focal lengths of the lenses are f_1 and f_2, respectively.
 * 
 * @author Johannes Courtial
 */
public class CylindricalLensTelescopeVisualiser extends NonInteractiveTIMEngine
{
	/**
	 * (x_2, y_2), i.e. the x and y coordinates of the focal line of lens 2
	 */
	private Vector2D p2;
	
	/**
	 * focal length of lens 1
	 */
	private double f1;
	
	/**
	 * focal length of lens 2
	 */
	private double f2;


	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public CylindricalLensTelescopeVisualiser()
	{
		super();
		
		// set to true for interactive version
		interactive = true;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		p2 = new Vector2D(0, 0.1);
		f1 = 1;
		f2 = -1;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop

		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 15;
		
		if(interactive)
		{
			windowTitle = "Dr TIM's cylindrical-lens-telescope visualiser";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "CylindricalLensTelescopeVisualiser"	// the name
				+ " p2="+p2
				+ " f1="+f1
				+ " f2="+f2
				;
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
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		
		
		SceneObjectPrimitiveIntersection
		
		
		// add the lens holograms
		Vector3D lensNormal = new Vector3D(0, 0, 1);
		
		double lensRadius = 1;

		Vector3D spiralLens1Centre = centre;
		EditableScaledParametrisedDisc spiralLens1 = new EditableScaledParametrisedDisc(
				"spiral-lens 1",	// description
				spiralLens1Centre,
				lensNormal,	// normal
				1,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				null,	// surface property
				scene,	// parent
				studio
		);
		PhaseHologramOfLogarithmicCylindricalLensSpiral hologram1 = new PhaseHologramOfLogarithmicCylindricalLensSpiral(
				f,	// focalLength
				1,	// a
				b,
				spiralLens1,	// sceneObject
				0.96,	// throughputCoefficient
				false,	// reflective
				false	// shadowThrowing
			);
		spiralLens1.setSurfaceProperty(hologram1);
		scene.addSceneObject(spiralLens1, showLens1);

		Vector3D spiralLens2Centre = Vector3D.sum(centre, lensNormal.getWithLength(distanceOfLens2BehindLens1));
		EditableScaledParametrisedDisc spiralLens2 = new EditableScaledParametrisedDisc(
				"spiral-lens 2",	// description
				spiralLens2Centre,
				lensNormal,	// normal
				lensRadius,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				null,	// surface property
				scene,	// parent
				studio
		);
		PhaseHologramOfLogarithmicCylindricalLensSpiral hologram2 = new PhaseHologramOfLogarithmicCylindricalLensSpiral(
				-f,	// focalLength
				1*Math.exp(b*MyMath.deg2rad(rotationAngleDeg)),	// a
				b,
				spiralLens2,	// sceneObject
				0.96,	// throughputCoefficient
				false,	// reflective
				false	// shadowThrowing
			);
		spiralLens2.setSurfaceProperty(hologram2);
		scene.addSceneObject(spiralLens2, showLens2);


		// the comparison lens
		
		Vector3D comparisonLensCentre = Vector3D.sum(centre, lensNormal.getWithLength(-MyMath.TINY));
		EditableScaledParametrisedDisc comparisonLens = new EditableScaledParametrisedDisc(
				"comparison lens",	// description
				comparisonLensCentre,
				lensNormal,	// normal
				lensRadius,	// radius
				new Vector3D(1, 0, 0),	// phi0Direction
				DiscParametrisationType.CARTESIAN,	// parametrisationType
				-lensRadius, lensRadius,	// suMin, suMax
				-lensRadius, lensRadius,	// svMin, svMax
				new PhaseHologramOfLens(
						comparisonLensF,	// focal length
						centre,	// principalPoint
						0.96,	// throughputCoefficient
						false,	// reflective
						false	// shadowThrowing
					),	// surface property
				scene,	// parent
				studio
		);
		scene.addSceneObject(comparisonLens, showComparisonLens);

		// the camera
		studio.setCamera(getStandardCamera());
	}

	
	
	//
	// for interactive version
	//
	
	private LabelledVector2DPanel p2Panel;
	private LabelledDoublePanel f1Panel, f2Panel;
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledDoublePanel cameraDistancePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;



	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
				
		p2Panel = new LabelledVector2DPanel("x and y coordinates of principal line of cylindrical lens 2");
		p2Panel.setVector2D(p2);
		interactiveControlPanel.add(p2Panel, "span");
		
		f1Panel = new LabelledDoublePanel("f_1");
		f1Panel.setNumber(f1);
		interactiveControlPanel.add(f1Panel, "span");
		
		f2Panel = new LabelledDoublePanel("f_2");
		f2Panel.setNumber(f2);
		interactiveControlPanel.add(f2Panel, "span");

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(cameraPanel, "span");

		// camera stuff
		
//		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
//		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
//		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel);
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		p2 = p2Panel.getVector2D();
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		
		// cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
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
		(new CylindricalLensTelescopeVisualiser()).run();
	}
}
