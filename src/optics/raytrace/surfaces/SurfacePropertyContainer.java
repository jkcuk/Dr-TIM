package optics.raytrace.surfaces;

import java.util.ArrayList;

import optics.raytrace.core.SurfaceProperty;

/**
 * A collection of surface properties that somehow combine into one surface property.
 * This abstract class deals with adding SurfaceProperties to the container.
 * 
 * Extend this abstract class to define how the surface properties combine.
 */
public abstract class SurfacePropertyContainer extends SurfaceProperty
{
	private static final long serialVersionUID = 4643754793935619103L;
	
	/**
	 * The surface properties in the container
	 */
	protected ArrayList<SurfaceProperty> surfaceProperties;

	/**
	 * Create an empty collection of surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyContainer#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyContainer()
	{
		surfaceProperties=new ArrayList<SurfaceProperty>();
	}
	
	/**
	 * Create a collection of (initially) two surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyContainer#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyContainer(SurfaceProperty sp1, SurfaceProperty sp2)
	{
		this();
		add(sp1);
		add(sp2);
	}

	/**
	 * Create a collection of (initially) three surface properties.
	 * Additional surface properties can be added later using the add(SurfaceProperty sp) method.
	 * 
	 * @see optics.raytrace.surfaces.SurfacePropertyContainer#add(optics.raytrace.core.SurfaceProperty)
	 */
	public SurfacePropertyContainer(SurfaceProperty sp1, SurfaceProperty sp2, SurfaceProperty sp3)
	{
		this();
		add(sp1);
		add(sp2);
		add(sp3);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public SurfacePropertyContainer(SurfacePropertyContainer original)
	{
		this();
		
		// copy clones of all the surface properties in original into this
		for(int i=0; i<original.size(); i++)
		{
			add(original.get(i).clone());
		}
	}

		
	/**
	 * Add a surface property to container
	 * 
	 * @param surfaceProperty	surface property to be added
	 */
	public void add(SurfaceProperty surfaceProperty)
	{		
                if (surfaceProperty == null) {
                    System.out.println("Error: null surface property being added");
                    return;
                
                }
		surfaceProperties.add(surfaceProperty);
	}

	/**
	 * @return the surfaceProperties
	 */
	public ArrayList<SurfaceProperty> getSurfaceProperties() {
		return surfaceProperties;
	}

	/**
	 * @param surfaceProperties the surfaceProperties to set
	 */
	public void setSurfaceProperties(ArrayList<SurfaceProperty> surfaceProperties) {
		this.surfaceProperties = surfaceProperties;
	}
	
	/**
	 * Get the surface property with a given index
	 */
	public SurfaceProperty get(int index)
	{
		return surfaceProperties.get(index);
	}

	/**
	 * The number of surface properties in this container.
	 */
	public int size() {
		return surfaceProperties.size();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.SurfaceProperty#isShadowThrowing()
	 * By default, this is shadow-throwing if at least one of the surface properties in the container is
	 */
	@Override
	public boolean isShadowThrowing() {
		for(int i = 0; i<surfaceProperties.size(); i++)
		{
			if(surfaceProperties.get(i).isShadowThrowing())
			{
				// say "yes, this throws a shadow" if any of the surface properties inside throws a shadow
				return true;
			}
		}

		// none of the surface properties inside this throws a shadow
		return false;
	}
}
