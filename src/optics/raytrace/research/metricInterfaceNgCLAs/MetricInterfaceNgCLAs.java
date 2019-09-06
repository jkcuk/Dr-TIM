package optics.raytrace.research.metricInterfaceNgCLAs;

import java.awt.*;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.MetricInterface.RefractionType;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.cameras.*;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;


/**
 * Example for running The METATOY Raytracer (TIM) as a non-interactive Java application.
 * 
 * The main method renders the image defined by createStudio(), saves it to a file
 * (whose name is given by the constant FILENAME), and displays it in a new window.
 *
 * Change the method createStudio() to change scene, lights and/or camera.
 * 
 * Change the method getFilename() to save the image under a different name.
 * 
 * Change the main method if you want the Java application to do something different altogether.
 * 
 * @author Johannes Courtial
 * @author George Antoniou
 */
public class MetricInterfaceNgCLAs
{
	/**
	 * Filename under which main saves the rendered image.
	 * Alter to save the rendered image under a different name.
	 * @return	the filename under which main saves the rendered image.
	 */
	public static String getFilename()
	{
		return "RayTraceTemp.bmp";
	}
	
	// the parameters of the gCLAs
	public static final double
		delta_x = 0,
		delta_y = 0,
		eta = 1;
	
	/**
	 * @return	a metric-interface surface that corresponds to gCLAs with the given parameters
	 */
	private static SurfaceProperty getMetricInterfaceSurface()
	{
		// the metric tensors on the inside and outside, respectively
		double[] g, h;
		
		// define the elements of the inside metric tensor
		double
			g11 = 1,
			g12 = 0,
			g13 = 0,
			g22 = 1,
			g23 = 0,
			g33 = 1;
		
		// calculate the inside metric tensor from the gCLA parameters
		g = MetricInterface.getMetricTensor(g11, g12, g13, g22, g23, g33);
		
		// calculate the elements of the outside metric tensor from the gCLA parameters
		double
			h11 = g11,
			h12 = g12,
			h13 = (g13 + g12*delta_y + g11*delta_x)/eta,
			h22 = g22,
			h23 = (g23 + g12*delta_x + g22*delta_y)/eta,
			h33 = (g33 + 2*delta_y*g23 + delta_y*delta_y*g22 + 2*delta_x*g13 + delta_x*delta_x*g11 + 2*delta_x*delta_y*g12) / (eta*eta);
		
		// calculate the outside metric tensor from the gCLA parameters
		h = MetricInterface.getMetricTensor(h11, h12, h13, h22, h23, h33);
		
		return new MetricInterface(
				g,
				h,
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,	// basis
				RefractionType.POSITIVE_REFRACTION,	// refractionType
				false,	// allowImaginaryOpticalPathLengths
				0.95,	// transmissionCoefficient
				true	// shadow-throwing
			);
	}
	
	/**
	 * @return	return a SurfaceProperty object that describes a gCLA surface with the given parameters
	 */
	private static SurfaceProperty getGeneralisedConfocalLensletArraysSurface()
	{
	    return new GCLAsWithApertures(
						new Vector3D(0,0,1),	//normalised a pointing in the z direction 
						new Vector3D(1,0,0),	//normalised u pointing in x direction-tangential to the surface
						new Vector3D(0,1,0),
						eta,	// eta_u
						eta,	// eta_v
						delta_x,	// delta_u
						delta_y,	// delta_v
						GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
						0.95, // transmissioncoefficient
						true	// shadow-throwing
					);
	}
	
	/**
	 * Define scene, lights, and/or camera.
	 * @param surface	0 = gCLAs, 1 = metric interface
	 * @return a studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int surface)
	{
		Studio studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio));	// the checkerboard floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky

		// add any other scene objects

		scene.addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"metarefracting window",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(3, 0, 0),	// span vector 1
				new Vector3D(0, 2, 0),	// span vector 2
				(surface==0)?getGeneralisedConfocalLensletArraysSurface():getMetricInterfaceSurface(),
				scene,
				studio
		));

		// define the camera
		//
		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		int
		pixelsX = 640,
		pixelsY = 480,
		antiAliasingFactor = 1;
		// If antiAliasingFactor is set to N, the image is calculated at resolution
		// N*pixelsX x N*pixelsY.

		// a camera with a non-zero aperture size (so it simulates blur)
		ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 1),	// view direction (magnitude is distance to detector centre)
				new Vector3D(4*(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -4, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				10,	// focussing distance
				0.0,	// aperture radius
				1	// rays per pixel; the more, the less noise the photo is
		);

		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);

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

		// define scene, lights and camera
		Studio studio = createStudio(0);

		// do the ray tracing
		studio.takePhoto();

		// save the image
		// studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));

		// define scene, lights and camera
		studio = createStudio(1);

		// do the ray tracing
		studio.takePhoto();

		// save the image
		// studio.savePhoto(getFilename(), "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));

		container.validate();
	}
}
