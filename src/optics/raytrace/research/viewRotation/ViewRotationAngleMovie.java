package optics.raytrace.research.viewRotation;

import java.awt.Container;
import java.text.DecimalFormat;

import math.Vector3D;

import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.core.*;


public class ViewRotationAngleMovie {
	private static String Imagename;
	
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
		
		PhotoCanvas photoCanvas = null;

		DecimalFormat formatter = new DecimalFormat("000");

		//Loop
		int iteration = 0;
		
		// for(double gamma = 120; gamma < 150; gamma += .5)	// the transition between pseudoscopic/orthoscopic views
		for(double gamma = -180; gamma < 180; gamma += 5)	// the normal movie
		{
			Imagename = "gammaSolution1" + formatter.format(iteration) + " (" + gamma + "¡)" +".bmp";
			
			//final String FILENAME2 = TempFilename;
			System.out.println(Imagename);

			// define scene, lights and camera
			Studio studio = ViewRotation.createStudio(
					ViewRotation.gamma2alphaSolution1(gamma),	// alpha, in degrees
					ViewRotation.gamma2betaSolution1(gamma),	// beta, in degrees
					0,	// cameraX
					0,	// cameraY
					0,	// cameraZ
					new Vector3D(0, 0, 10),	// centre of view
					1	// zoom factor
				);

			// do the ray tracing
			studio.takePhoto();

			// save the image
			studio.savePhoto(Imagename, "bmp");
			
			// display the image on the screen
			if(photoCanvas == null)
			{
				photoCanvas = new PhotoCanvas(studio.getPhoto());
				container.add(photoCanvas);
				container.validate();
			}
			else
			{
				// doesn't work, for some reason...
				photoCanvas.setImage(studio.getPhoto());
			}
			
			iteration++;
		}
	}
}