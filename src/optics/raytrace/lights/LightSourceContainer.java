package optics.raytrace.lights;
import java.util.ArrayList;

import java.io.Serializable;

import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.surfaces.SurfaceColour;

public class LightSourceContainer extends LightSource implements Serializable
{
	private static final long serialVersionUID = -2343440150284611946L;

	protected ArrayList<LightSource>lightSources;
    
	public LightSourceContainer(String description){
		super(description);
		lightSources=new ArrayList<LightSource>();
	}
    
	public void add(LightSource s) {
		lightSources.add(s);
	}
    
	public void remove(LightSource s) {
		lightSources.remove(s);
	}
    
	public boolean contains(LightSource s) {
		return lightSources.contains(s);
	}

	public LightSource get(int index) {
		return lightSources.get(index);
	}

	public int size() {
		return lightSources.size();
	}
    
	@Override
	public DoubleColour getColour(SurfaceColour surfaceColour, 
			SceneObject scene, 
			RaySceneObjectIntersection i, 
			Ray r,	  // incoming light ray
			int traceLevel)
	{
		DoubleColour
		c,	// current colour
		sumColour = new DoubleColour(0,0,0);
        
		int n = lightSources.size();	// number of light sources
        
		for(int j=0; j<n; j++) {
			c = lightSources.get(j).getColour(surfaceColour, scene, i, r, traceLevel);
			sumColour = sumColour.add(c);		
		}
        
		return sumColour;
	}

	@Override
	public String toString()
	{
		String string = "LightSourceContainer:\n";
		for (int i=0; i<lightSources.size(); i++)
			string += "\tlight: '" + lightSources.get(i) + "'\n";
		return string;
	}
}
