package optics.raytrace.research.autostereogramResonator;

import java.awt.Container;
import java.text.DecimalFormat;

import math.*;
import optics.raytrace.TIMInteractiveBits;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.studioInitialisation.AutostereogramResonatorInitialisation;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.QualityType;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;


/**
 * Autostereogram resonator
 * 
 * Note that in Project>Properties (or alternatively Run>Run Configurations...) the working
 * directory (which can be found in the arguments tab) has to be set to the directory
 * containing all the images, normally
 * ${workspace_loc:TIM/demo/}, so that the saved image is in the right place.
 * 
 * Adapted from RayTraceJavaApplication.java
 */
public class AutostereogramResonator
{
	/**
	 * Filename under which main saves the rendered image.
	 */
	private static String filename = "temp.bmp";
	
	private static final boolean SAVE_FILE = true;

	/**
	 * Define scene, lights, and/or camera.
	 * @return the studio, i.e. scene, lights and camera
	 */
	public static Studio createStudio(int setupNumber)
	{		
		Studio studio = new Studio();
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the camera parameters
		
		// String antiAliasingQuality = QualityComboBox.NORMAL;
		ApertureSizeType apertureSize = ApertureSizeType.PINHOLE; // ApertureSizeComboBox.MEDIUM,
		QualityType
			antiAliasingQuality = QualityType.GOOD,
			blurQuality = QualityType.RUBBISH; // QualityComboBox.GREAT;
		double
			focusDistance = 10,
			cameraX = 0,
			cameraY = 0,
			cameraZ = 0;

		DecimalFormat formatter = new DecimalFormat("000");

		if(setupNumber == 0)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// view with non-zero aperture, focussed at z=10
			filename = "focusZ20.bmp";
			apertureSize = ApertureSizeType.MEDIUM;
			blurQuality = QualityType.GREAT;
			focusDistance = 20;
		}
		else if(setupNumber == 1)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// view with non-zero aperture, focussed at z=10
			filename = "focusZ10.bmp";
			apertureSize = ApertureSizeType.MEDIUM;
			blurQuality = QualityType.GREAT;
			focusDistance = 10;
		}
		else if(setupNumber == 2)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// view with non-zero aperture, focussed at z=Infinity
			filename = "focusZInfinity.bmp";
			apertureSize = ApertureSizeType.MEDIUM;
			blurQuality = QualityType.GREAT;
			focusDistance = MyMath.HUGE;
		}
		else if(setupNumber == 3)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted forward by 0.5
			filename = "dz0.5.bmp";
			cameraZ = 0.5;
		}
		else if(setupNumber == 4)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted backwards by 0.5
			filename = "dz-0.5.bmp";
			cameraZ = -0.5;
		}
		else if(setupNumber == 5)
		{
			// the view from the camera position, shifted sideways by 0.1
			filename = "dx0.1.bmp";
			cameraX = 0.1;
		}
		else if(setupNumber == 6)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways by -0.1
			filename = "dx-0.1.bmp";
			cameraX = -0.1;
		}
		else if(setupNumber == 7)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted up by 0.1
			filename = "dy0.1.bmp";
			cameraY = 0.1;
		}
		else if(setupNumber == 8)
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted down by 0.1
			filename = "dy-0.1.bmp";
			cameraY = -0.1;
		}
		else if((20 <= setupNumber) && (setupNumber <= 52))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(true).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways to be a frame of the x-shift movie
			// setupNumber = 20 -> cameraX = -0.3
			// setupNumber = 26 -> cameraX = 0
			// setupNumber = 46 -> cameraX = 1; note that this is the position of the other eye
			// setupNumber = 52 -> cameraX = 1.3
			cameraX = (setupNumber - 26) * 0.05;
			filename = "dx"+cameraX+".bmp";
		}
		else if((100 <= setupNumber) && (setupNumber <= 120))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways to be a frame of the y-shift movie
			// setupNumber = 100 -> cameraY = -0.2
			// setupNumber = 110 -> cameraY = 0
			// setupNumber = 120 -> cameraY = 0.2
			cameraY= (setupNumber - 110) * 0.02;
			filename = "dy"+cameraY+".bmp";
		}
		else if((200 <= setupNumber) && (setupNumber <= 220))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways to be a frame of the z-shift movie
			// setupNumber = 200 -> cameraZ = -0.2
			// setupNumber = 210 -> cameraZ = 0
			// setupNumber = 220 -> cameraZ = 0.2
			cameraZ = (setupNumber - 210) * 0.02;
			filename = "dz"+cameraZ+".bmp";
		}
		else if((300 <= setupNumber) && (setupNumber <= 320))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways to be a frame of the z-shift movie
			// setupNumber = 300 -> cameraZ = -0.5
			// setupNumber = 310 -> cameraZ = 0
			// setupNumber = 320 -> cameraZ = 0.5
			cameraZ = (setupNumber - 310) * 0.05;
			filename = "dz"+cameraZ+".bmp";
		}
		else if((400 <= setupNumber) && (setupNumber <= 420))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			// the view from the camera position, shifted sideways to be a frame of the z-shift movie
			// setupNumber = 400 -> cameraZ = -10
			// setupNumber = 410 -> cameraZ = 0
			// setupNumber = 420 -> cameraZ = 10
			cameraZ = (setupNumber - 410);
			filename = "dz"+cameraZ+".bmp";
		}
		else if((500 <= setupNumber) && (setupNumber <= 540))
		{
			// populate scene and define the lights
			new AutostereogramResonatorInitialisation(false).initialiseSceneAndLights(scene, studio);

			apertureSize = ApertureSizeType.MEDIUM;
			blurQuality = QualityType.GREAT;
			// shifting focus
			// setupNumber = 500 -> cameraZ = -20
			// setupNumber = 510 -> cameraZ = -10
			// setupNumber = 519 -> cameraZ = -1
			// setupNumber = 520 -> cameraZ = 0; this is sort of undefined --- don't run this
			// setupNumber = 540 -> cameraZ = 20
			focusDistance = setupNumber - 520;
			filename = "df"+formatter.format(setupNumber)+".bmp";
		}
		else
		{
			// the standard view
			filename = "standardView.bmp";
		}
		
		studio.setCamera(new EditableRelativisticAnyFocusSurfaceCamera(
				"camera",
				new Vector3D(cameraX, cameraY, cameraZ),	// centre of aperture
				new Vector3D(0, 0, 10),	// lookAtPoint: the point in the centre of the field of view
				new Vector3D(4, 0, 0),	// horizontal span vector
				new Vector3D(0, 0, 0),	// beta
				TIMInteractiveBits.IMAGE_CANVAS_SIZE_X, TIMInteractiveBits.IMAGE_CANVAS_SIZE_Y,	// logical number of pixels
				ExposureCompensationType.EC0,
				1000,	// maxTraceLevel
				Plane.zPlane("focus plane", focusDistance, Transparent.PERFECT, null, studio),
				(SceneObject)null,	// camera-frame scene is empty
				apertureSize,	// aperture size
				blurQuality,	// blur quality
				antiAliasingQuality	// anti-aliasing quality
			));
				
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
		
		PhotoCanvas photoCanvas = null;

		Studio studio;
		
		for(int setupNumber = 535; setupNumber <= 540; setupNumber += 1)
		{
			// define scene, lights and camera
			studio = createStudio(setupNumber);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			if(SAVE_FILE) studio.savePhoto(filename, "bmp");

			// display the image on the screen
			if(photoCanvas == null)
			{
				// if the photo canvas hasn't been initialised, do this now
				photoCanvas = new PhotoCanvas(studio.getPhoto());
				container.add(photoCanvas);
				container.validate();
			}
			else
			{
				photoCanvas.setImage(studio.getPhoto());
			}
		}
	}
}
