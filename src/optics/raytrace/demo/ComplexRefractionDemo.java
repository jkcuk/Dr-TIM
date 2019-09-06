package optics.raytrace.demo;

import java.awt.Container;

import math.*;
import optics.raytrace.surfaces.*;
import optics.raytrace.surfaces.metarefraction.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Complex refraction demo
 * 
 * Calculates the figures for
 * [1] George Constable, Alasdair C. Hamilton and Johannes Courtial,
 *     "Complex representation of light-ray-direction changes",
 *     in preparation (2010)
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class ComplexRefractionDemo
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static final String FILENAME = "ComplexRefractionDemoTemp.bmp";

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(double parameter)
	{		
		Studio studio = new Studio();
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor

		//
		// add any other scene objects
		//
		
		// first, something to look at
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-1, 1, 4,
				-1, 1, 4,
				10, 25, 4,	// standard: 10, 25, 4; 10, zzz, 10 for complex power
				0.02,
				scene,
				studio
		));
		
		// then add an interesting window

//		// complex multiplication (Snell's law, local light-ray rotation)
//		Complex insideOutsideRefractiveIndexRatio = new Complex(2., 0.);
//		ComplexMetarefraction complexMetarefraction =
//			new ComplexMetarefractionMultiplication(Complex.division(1., insideOutsideRefractiveIndexRatio));

//		// ComplexRefractionDemoComparison.bmp
//		double kX = 0.0, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		

//		// ComplexRefractionDemoAdditionKX=0.1.bmp
//		double kX = 0.1, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		
		
//		// ComplexRefractionDemoAdditionKX=0.2.bmp
//		double kX = 0.2, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		

//		// ComplexRefractionDemoAdditionKX=0.3.bmp
//		double kX = 0.3, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		

//		// ComplexRefractionDemoAdditionKX=0.4.bmp
//		double kX = 0.4, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		

//		// ComplexRefractionDemoAdditionKX=0.5.bmp
//		double kX = 0.5, kY = 0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionAddition(new Complex(kX, kY));		

//		// ComplexRefractionDemoPower0.5.bmp
//		double inwardsExponent = 0.5;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower0.8.bmp
//		double inwardsExponent = 0.8;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower1.0.bmp
//		double inwardsExponent = 1.0;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower1.2.bmp
//		double inwardsExponent = 1.2;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower1.5.bmp
//		double inwardsExponent = 1.5;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower1.8.bmp
//		// double inwardsExponent = 1.8;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower2.0.bmp
//		double inwardsExponent = 2;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoPower2.2.bmp
//		double inwardsExponent = 2.2;
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionPower(inwardsExponent);		

		// ComplexRefractionDemoPower3.0.bmp
		double inwardsExponent = .5;
		ComplexMetarefraction complexMetarefraction =
		new ComplexMetarefractionPower(inwardsExponent);		

//		// ComplexRefractionDemoExp.bmp
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionExp();		

//		// ComplexRefractionDemoSin.bmp
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionSin();		

//		// ComplexRefractionDemoCos.bmp
//		ComplexMetarefraction complexMetarefraction =
//		new ComplexMetarefractionCos();		

		scene.addSceneObject(
				new EditableScaledParametrisedCentredParallelogram(
						"complex-metarefracting window",
						new Vector3D(0, 0, 10),	// centre
						new Vector3D(3, 0, 0),	// width vector
						new Vector3D(0, 2, 0),	// height vector
						new Metarefractive(
								complexMetarefraction,
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
								true	// shadow-throwing
						),
						scene,
						studio
				)
		);

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 800,
		pixelsY = 600,
		antiAliasingFactor = 1;

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 3),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, 1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		return studio;
	}
	

	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();

		// if the start and end values are chosen differently, the scene is rendered repeatedly
		for(double parameter=0.0; parameter<=0.0; parameter += 0.1)
		{
			// define scene, lights and camera
			Studio studio = createStudio(parameter);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			// either save under FILENAME...
			studio.savePhoto(FILENAME, "bmp");
			// ... or save under custom name with parameter
			// studio.savePhoto("ComplexRefractionDemoPower" + parameter +".bmp", "bmp");

			// display the image on the screen
			container.add(new PhotoCanvas(studio.getPhoto()));
			container.validate();
		}
	}
}
