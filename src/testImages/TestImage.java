package testImages;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedParallelogram;
import optics.raytrace.GUI.surfaces.EditablePictureSurfaceDiffuse;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.PictureSurface;

/**
 * Provides a few test images.
 * @author johannes
 *
 */
public enum TestImage
{	
	EPSRC_LOGO("EPSRC logo", "EPSRCLogo.jpg"),
	GU_LOGO("Glasgow University logo", "UniofGlasgowLogo_colour.jpg"),
	TW_PROJECT_LOGOS("TW-project logos", "TWProjectLogos.jpg"),
	USAF_TEST_CHART("US Air Force test chart, 7cm x 7cm", "USAF-1951_white_7cm_x_7cm.png"),	// image from https://en.wikipedia.org/wiki/1951_USAF_resolution_test_chart
	USAF_TEST_CHART_BLACK("US Air Force test chart (black), 7cm x 7cm", "USAF-1951_7cm_x_7cm.png"),	// image from https://en.wikipedia.org/wiki/1951_USAF_resolution_test_chart
	USAF_TEST_CHART_Thorlabs("Thorlabs US Air Force test chart, 8cm x 8cm", "USAF-1951-Thorlabs_8cmx8cm_groups_-2_to_3_white.png"),
	USAF_TEST_CHART_BLACK_Thorlabs("Thorlabs US Air Force test chart (black), 8cm x 8cm", "USAF-1951-Thorlabs_8cmx8cm_groups_-2_to_3.png");
	
	/**
	 * the image's name
	 */
	private String name;
	
	/**
	 * the image's filename
	 */
	private String filename;
	
	private double aspectRatio;
	
	/**
	 * constructor
	 * @param name
	 * @param filename
	 */
	private TestImage(String name, String filename)
	{
		this.name = name;
		this.filename = filename;
		try {
			aspectRatio = calculateAspectRatio();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {return name;}
	
	/**
	 * @return	the image's filename
	 */
	public String getFilename() {return filename;}
	
	/**
	 * @return	the image's URL
	 */
	public URL getURL() {return this.getClass().getResource(filename);}
	
	/**
	 * @return	the image file
	 */
	public File getFile() {return new File(getURL().getFile());}
	
	public BufferedImage getBufferedImage() throws IOException
	{
		// for some reason, .bmp files don't load here, but .png files do
		// java.net.URL imgURL = getClass().getResource(getClass().getSimpleName() + ".png");
		return ImageIO.read(getURL());
	}
	
	public double getAspectRatio()
	{
		return aspectRatio;
	}
	
	private double calculateAspectRatio() throws IOException
	{
		BufferedImage image = getBufferedImage();
		// System.out.println("width="+image.getWidth()+", height="+image.getHeight());
		return (double)(image.getWidth()) / (double)(image.getHeight());
	}
	
	public PictureSurface getPictureSurface(double xMin, double xMax, double yMin, double yMax, boolean shadowThrowing)
	{
		return new PictureSurface(getURL(), xMin, xMax, yMin, yMax, shadowThrowing);
	}

	public EditablePictureSurfaceDiffuse getEditablePictureSurfaceDiffuse(boolean tiled, double xMin, double xMax, double yMin, double yMax)
	{
		return new EditablePictureSurfaceDiffuse(getURL(), tiled, xMin, xMax, yMin, yMax);
	}

	public EditableScaledParametrisedParallelogram getEditableScaledParametrisedParallelogram(
			String description, 
			Vector3D topLeftCorner, 
			Vector3D spanVector1, Vector3D spanVector2,
			SceneObject parent, Studio studio)
	{
		return new EditableScaledParametrisedParallelogram(
				description, 
				topLeftCorner,	// corner
				spanVector1, spanVector2, 
				0, 1,	// suMin, suMax
				0, 1, 	// svMin, svMax
				getEditablePictureSurfaceDiffuse(false, 0, 1, 0, 1),	// surfaceProperty
				parent,
				studio
			);
	}

	/**
	 * @param description
	 * @param centre
	 * @param spanVector1
	 * @param spanVector2
	 * @param parent
	 * @param studio
	 * @return	a SceneObject, suitable for inclusion into the scene, that is a rectangle with the test image on it
	 */
	public EditableScaledParametrisedCentredParallelogram getEditableScaledParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2,
			SceneObject parent, Studio studio)
	{
		return new EditableScaledParametrisedCentredParallelogram(
				description, 
				centre,	// centre
				spanVector1, spanVector2, 
				getEditablePictureSurfaceDiffuse(false, 0, 1, 0, 1),	// surfaceProperty
				parent,
				studio
			);
	}

	
//	java.net.URL imgURL1 = (new AutostereogramResonator()).getClass().getResource("TimHead.jpg");
//	SurfaceProperty pictureSurface1 = new EditablePictureSurfaceDiffuse(imgURL1, true, 0, width, 0, width);
}