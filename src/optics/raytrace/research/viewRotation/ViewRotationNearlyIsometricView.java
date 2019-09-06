package optics.raytrace.research.viewRotation;

import java.awt.Container;

import math.Vector3D;

import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.GUI.nonInteractive.PhotoFrame;
import optics.raytrace.core.*;


public class ViewRotationNearlyIsometricView {
	private static String Imagename;
	
	public static void main(final String[] args)
	{
		// open a window, and take a note of its content pane
		Container container = (new PhotoFrame()).getContentPane();
				
		Imagename = "isometricViewSolution1.bmp";

		// define scene, lights and camera
		Studio studio = ViewRotation.createStudio(
				ViewRotation.gamma2alphaSolution1(60),	// alpha, in degrees
				ViewRotation.gamma2betaSolution1(60),	// beta, in degrees
				-10,	// cameraX
				10,	// cameraY
				-60,	// cameraZ
				new Vector3D(0, 0, 20),	// centre of view
				0.5	// zoom factor
		);

		// do the ray tracing
		studio.takePhoto();

		// save the image
		studio.savePhoto(Imagename, "bmp");

		// display the image on the screen
		container.add(new PhotoCanvas(studio.getPhoto()));
		container.validate();
	}
}

