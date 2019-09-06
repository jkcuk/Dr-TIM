package optics.raytrace.test;

import javax.swing.JComboBox;

import math.*;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceOfVolumeWithColouredVoxelBoundaries;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;


/**
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 * 
 * @author  Johannes Courtial
 */
public class SurfaceOfVolumeWithColouredVoxelBoundariesTest
extends NonInteractiveTIMEngine
{	
	@Override
	public void populateStudio()
	throws SceneException
	{
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects

		
		// create a SurfaceOfVoxellatedAbsorber surface object
		
		// scene.addSceneObject(new ParametrisedSphere("for comparison", new Vector3D(1, 0.3, 10), 1, new Refractive(2.0, 0.94), scene, studio));
		
		Vector3D sphereCentre = new Vector3D(0, 0, 0);
		
		Sphere sphere = new Sphere(
				"strange sphere",	// description
				sphereCentre,	// centre
				1,	// radius
				null,	// placeholder --- replace in a minute
				scene, 
				studio
		);
		
		// the set of planes that define the voxels
		Vector3D planesCentre = sphereCentre;
		SetOfEquidistantParallelPlanes[] planeSets = new SetOfEquidistantParallelPlanes[2];
		double planeSeparation = 0.1;
		planeSets[0] = new SetOfEquidistantParallelPlanes(
				planesCentre,	// point on 0th plane
				Vector3D.X,	// normal to surfaces
				planeSeparation
			);
		planeSets[1] = new SetOfEquidistantParallelPlanes(
				planesCentre,	// point on 0th plane
				Vector3D.Y,	// normal to surfaces
				planeSeparation
			);
		
		SurfaceOfVolumeWithColouredVoxelBoundaries s = new SurfaceOfVolumeWithColouredVoxelBoundaries(
				planeSets,	// the sets of parallel planes defining the voxels
				sphere,	// (SceneObject) new Refractive(0,0), the object
				DoubleColour.CYAN,
				700,	// maxSteps
				0.96,	// random transmission coefficient
				true	// shadow-throwing
			);

		// now give the sphere that marvellous surface property
		sphere.setSurfaceProperty(s);

		scene.addSceneObject(sphere);


		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());			
	}

	
	// interactive control panels
	
	// camera
	private LabelledVector3DPanel cameraViewCentrePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel;
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
		
		
		//
		// cameras 
		//
				
		cameraViewCentrePanel = new LabelledVector3DPanel("Camera view centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		interactiveControlPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		interactiveControlPanel.add(cameraViewDirectionPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		interactiveControlPanel.add(cameraDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		interactiveControlPanel.add(cameraFocussingDistancePanel, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraDistance = cameraDistancePanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		(new SurfaceOfVolumeWithColouredVoxelBoundariesTest()).run();
	}
}
