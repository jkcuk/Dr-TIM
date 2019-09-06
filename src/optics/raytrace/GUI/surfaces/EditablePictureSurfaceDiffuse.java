package optics.raytrace.GUI.surfaces;

import java.io.File;
import java.net.URL;

import optics.DoubleColour;
import optics.raytrace.surfaces.PictureSurfaceDiffuse;

public class EditablePictureSurfaceDiffuse extends PictureSurfaceDiffuse
{
	private static final long serialVersionUID = -3085045547790264167L;

	private File pictureFile;
	
	public EditablePictureSurfaceDiffuse(File pictureFile, boolean tiled, double xMin, double xMax, double yMin, double yMax)
	{
		super(pictureFile, tiled, xMin, xMax, yMin, yMax, DoubleColour.BLACK, true);
		this.pictureFile = pictureFile;
	}

	public EditablePictureSurfaceDiffuse(URL pictureURL, boolean tiled, double xMin, double xMax, double yMin, double yMax)
	{
		super(pictureURL, tiled, xMin, xMax, yMin, yMax, DoubleColour.BLACK);
		
		// set the picture file
		pictureFile = new File(pictureURL.getFile());
	}

	public String getFilename()
	{
		if(pictureFile != null)
		{
			return pictureFile.getName();
		}
		else
		{
			return "-- not selected --";
		}
	}

	public File getPictureFile()
	{
		return pictureFile;
	}

	public void setPictureFile(File pictureFile)
	{
		this.pictureFile = pictureFile;
	}	

	@Override
	public void setPicture(File file)
	{
		setPictureFile(file);
		super.setPicture(file);
	}
}
