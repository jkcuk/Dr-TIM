package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

public class ParametrisedCube extends ParametrisedCuboid
{
	private static final long serialVersionUID = -1976823467358869408L;

	private double sidelength;

	public ParametrisedCube(String description, double sidelength, Vector3D centre, SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, sidelength, sidelength, sidelength, centre, surfaceProperty, parent, studio);
		
		this.sidelength = sidelength;
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ParametrisedCube(ParametrisedCube original)
	{
		super(original);
		
		sidelength = original.sidelength;
		centre = original.centre.clone();
		surfaceProperty = original.surfaceProperty.clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public ParametrisedCube clone()
	{
		return new ParametrisedCube(this);
	}

	public double getSidelength() {
		return sidelength;
	}

	public void setSidelength(double sidelength) {
		this.sidelength = sidelength;
		setWidth(sidelength);
		setHeight(sidelength);
		setDepth(sidelength);
	}

	 @Override
	public String toString() {
		 return "<Cube>\n" + 
		 "\t<centre Vector3D="+getCentre()+">\n" + 
		 "\t<sidelength double="+sidelength+">\n" + 
		 "</Cube>";
	 }
	 
		@Override
		public String getType()
		{
			return "Cube";
		}
}
