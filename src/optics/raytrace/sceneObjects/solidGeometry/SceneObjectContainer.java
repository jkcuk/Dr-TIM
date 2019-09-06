package optics.raytrace.sceneObjects.solidGeometry;

import java.util.ArrayList;
import java.io.Serializable;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.utility.CopyModeType;

/**
 * A collection of scene objects that allows searching for the object in front of a given ray.
 */
/**
 * @author Johannes Courtial
 *
 */
public class SceneObjectContainer extends SceneObjectClass implements Serializable
{
	private static final long serialVersionUID = -4954876099717988277L;
	
	/**
	 * The objects in the container
	 */
	protected ArrayList<SceneObject> sceneObjects;
	
	/**
	 * Determines which objects are visible at render time
	 */
	protected ArrayList<Boolean> visibilities;
	
	/**
	 * Determines which objects are part of the scene when ray trajectories are being traced
	 */
	protected ArrayList<Boolean> visibilitiesWhenTrajectoryTracing;

	/**
	 * Create an empty collection of scene objects.
	 * Additional scene objects can be added later using the add(SceneObject o) method.
	 * 
	 * @param description
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#getSumWith(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectContainer(String description, SceneObject parent, Studio studio)
	{				//constructor creates new array object, any objects in the array need to be added later
		super(description, parent, studio);
		sceneObjects=new ArrayList<SceneObject>();
		visibilities = new ArrayList<Boolean>();
		visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>();
	}


	/**
	 * Create a collection of scene objects that contains (initially) one scene object.
	 * Additional scene objects can be added later using the add(SceneObject o) method.
	 * 
	 * @param description
	 * @param o	the scene object
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#addSceneObject(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectContainer(String description, SceneObject parent, SceneObject o, Studio studio)
	{				//constructor creates new array object, any objects in the array need to be added later
		super(description, parent, studio);
		sceneObjects=new ArrayList<SceneObject>(1);
		visibilities = new ArrayList<Boolean>(1);
		visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>(1);
		addSceneObject(o);
	}

	/**
	 * Create a collection of scene objects that contains (initially) two scene objects.
	 * Additional scene objects can be added later using the add(SceneObject o) method.
	 * 
	 * @param description
	 * @param o1	the first scene object
	 * @param o2	the second scene object
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#addSceneObject(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectContainer(String description, SceneObject parent, SceneObject o1, SceneObject o2, Studio studio)
	{				//constructor creates new array object, any objects in the array need to be added later
		super(description, parent, studio);
		sceneObjects=new ArrayList<SceneObject>(2);
		visibilities = new ArrayList<Boolean>(2);
		visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>(2);
		addSceneObject(o1);
		addSceneObject(o2);
	}

	/**
	 * Create a collection of scene objects that contains (initially) three scene objects.
	 * Additional scene objects can be added later using the add(SceneObject o) method.
	 * 
	 * @param description
	 * @param o1	the first scene object
	 * @param o2	the second scene object
	 * @param o3	the third scene object
	 * 
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer#addSceneObject(optics.raytrace.core.SceneObject)
	 */
	public SceneObjectContainer(String description, SceneObject parent, SceneObject o1, SceneObject o2, SceneObject o3, Studio studio)
	{				//constructor creates new array object, any objects in the array need to be added later
		super(description, parent, studio);
		sceneObjects=new ArrayList<SceneObject>(3);
		visibilities = new ArrayList<Boolean>(3);
		visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>(3);
		addSceneObject(o1);
		addSceneObject(o2);
		addSceneObject(o3);
	}

	/**
	 * @param original
	 * @param copyMode one of SHARE_DATA or CLONE_DATA
	 */
	public SceneObjectContainer(SceneObjectContainer original, CopyModeType copyMode)
	{
		super(original.getDescription(), original.getParent(), original.getStudio());
		
		// either share or clone the data, depending on copyMode
		switch(copyMode)
		{
		case SHARE_DATA:
			// use the same vectors
			this.sceneObjects = original.sceneObjects;
			this.visibilities = original.visibilities;
			this.visibilitiesWhenTrajectoryTracing = original.visibilitiesWhenTrajectoryTracing;
			break;
		case CLONE_DATA:
		default:
			// use copies of everything
			sceneObjects=new ArrayList<SceneObject>();
			visibilities = new ArrayList<Boolean>();
			visibilitiesWhenTrajectoryTracing = new ArrayList<Boolean>();

			// copy clones of all the scene objects
			for(int i=0; i<original.getNumberOfSceneObjects(); i++)
			{
				addSceneObject(original.getSceneObject(i).clone(), original.isSceneObjectVisible(i));
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public SceneObjectContainer clone()
	{
		return new SceneObjectContainer(this, CopyModeType.CLONE_DATA);
	}

	/**
	 * Is a scene object part of the scene?
	 */
	public boolean containsSceneObject(SceneObject o) {
		return sceneObjects.contains( o );
	}
	
	public int getIndexOfSceneObject(SceneObject o)
	{
		return sceneObjects.indexOf(o);
	}
	
	/**
	 * @param description
	 * @param searchIteratively	if true, also search SceneObjectContainers and EditableSceneObjectCollections
	 * @return the first scene object whose description matches that given
	 */
	public SceneObject getFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		// look through all scene objects in this container
		for(int i=0; i<sceneObjects.size(); i++)
		{
			SceneObject o = sceneObjects.get(i);
			
			// is the description of the current scene object the same as the description we are searching for?
			if(o.getDescription().equals(description))
			{
				// yes, the descriptions are the same!
				// return the SceneObject
				return o;
			}
			
			if(searchIteratively)
			{
				// also search inside other scene-object containers
				
				// is the current scene object a scene-object container?
				if(o instanceof SceneObjectContainer)
				{
					// yes, the current scene object is a scene-object container
					
					// ask it to search for a scene object with the given description
					SceneObject o1 = ((SceneObjectContainer)o).getFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(o1 != null)
					{
						// there is an object with that description in here;						
						// return it
						return o1;
					}
				}
			
				// is the current scene object an editable scene-object collection?
				if(o instanceof EditableSceneObjectCollection)
				{
					// yes, the current scene object is an editable scene-object collection
					
					// ask it to search for a scene object with the given description
					SceneObject o1 = ((EditableSceneObjectCollection)o).getFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(o1 != null)
					{
						// there is an object with that description in here;
						// return it
						return o1;
					}
				}
			}
		}
		
		// no object with the given description has been found
		// return null
		return null;
	}
	

	
	/**
	 * @param description
	 * @param searchIteratively
	 * @return	the first scene object in this container with that description if it is in there, null otherwise; if the object is found in a container contained in this container, then the ArrayList contains that container first, then the scene object
	 */
	public ArrayList<SceneObject> getPathToFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		// look through all scene objects in this container
		for(int i=0; i<sceneObjects.size(); i++)
		{
			SceneObject o = sceneObjects.get(i);
			
			// is the description of the current scene object the same as the description we are searching for?
			if(o.getDescription().equals(description))
			{
				// yes, the descriptions are the same!
				
				// construct an ArrayList that contains this SceneObject
				ArrayList<SceneObject> al = new ArrayList<SceneObject>();
				al.add(o);
				
				// return the ArrayList
				return al;
			}
			
			if(searchIteratively)
			{
				// also search inside other scene-object containers
				
				// is the current scene object a scene-object container?
				if(o instanceof SceneObjectContainer)
				{
					// yes, the current scene object is a scene-object container
					
					// ask it to search for a scene object with the given description
					ArrayList<SceneObject> al = ((SceneObjectContainer)o).getPathToFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(al != null)
					{
						// there is an object with that description in here;
						
						// add the SceneObjectContainer in which it was found to the start of the ArrayList
						al.add(0, o);
						
						// return it
						return al;
					}
				}
			
				// is the current scene object an editable scene-object collection?
				if(o instanceof EditableSceneObjectCollection)
				{
					// yes, the current scene object is an editable scene-object collection
					
					// ask it to search for a scene object with the given description
					ArrayList<SceneObject> al = ((EditableSceneObjectCollection)o).getPathToFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(al != null)
					{
						// there is an object with that description in here;
						
						// add the SceneObjectContainer in which it was found to the start of the ArrayList
						al.add(0, o);
						
						// return it
						return al;
					}
				}
			}
		}
		
		// no object with the given description has been found
		// return null
		return null;
	}
	
	/**
	 * @param description
	 * @param searchIteratively	if true, also search SceneObjectContainers and EditableSceneObjectCollections
	 * @return the removed scene object, if one is found whose description matches that given; null otherwise
	 */
	public SceneObject removeFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		// look through all scene objects in this container
		for(int i=0; i<sceneObjects.size(); i++)
		{
			SceneObject o = sceneObjects.get(i);
			
			// is the description of the current scene object the same as the description we are searching for?
			if(o.getDescription().equals(description))
			{
				// yes, the descriptions are the same!
				
				// remove the SceneObject...
				removeSceneObject(o);
				
				// .. and return it
				return o;
			}
			
			if(searchIteratively)
			{
				// also search inside other scene-object containers
				
				// is the current scene object a scene-object container?
				if(o instanceof SceneObjectContainer)
				{
					// yes, the current scene object is a scene-object container
					
					// ask it to search for a scene object with the given description
					SceneObject o1 = ((SceneObjectContainer)o).removeFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(o1 != null)
					{
						// there is an object with that description in here;						
						// return it
						return o1;
					}
				}
			
				// is the current scene object an editable scene-object collection?
				if(o instanceof EditableSceneObjectCollection)
				{
					// yes, the current scene object is an editable scene-object collection
					
					// ask it to search for a scene object with the given description
					SceneObject o1 = ((EditableSceneObjectCollection)o).removeFirstSceneObjectWithDescription(description, searchIteratively);
					
					// has a scene object with the given description been found?
					if(o1 != null)
					{
						// there is an object with that description in here;
						// return it
						return o1;
					}
				}
			}
		}
		
		// no object with the given description has been found
		// return null
		return null;
	}

	/**
	 * @param description
	 * @param searchIteratively	if true, also search SceneObjectContainers and EditableSceneObjectCollections
	 * @return the number of removed scene objects
	 */
	public int removeAllSceneObjectsWithDescription(String description, boolean searchIteratively)
	{
		int removed = 0;
		
		// look through all scene objects in this container
		for(int i=0; i<sceneObjects.size(); i++)
		{
			SceneObject o = sceneObjects.get(i);
			
			// is the description of the current scene object the same as the description we are searching for?
			if(o.getDescription().equals(description))
			{
				// yes, the descriptions are the same!
				
				// remove the SceneObject
				removeSceneObject(o);
				
				// increase the counter by 1
				removed = removed + 1;
			}
			
			if(searchIteratively)
			{
				// also search inside other scene-object containers
				
				// is the current scene object a scene-object container?
				if(o instanceof SceneObjectContainer)
				{
					// yes, the current scene object is a scene-object container
					
					// ask it to remove all scene objects with the given description
					removed = removed + ((SceneObjectContainer)o).removeAllSceneObjectsWithDescription(description, searchIteratively);
				}
			
				// is the current scene object an editable scene-object collection?
				if(o instanceof EditableSceneObjectCollection)
				{
					// yes, the current scene object is an editable scene-object collection
					
					// ask it to remove all scene objects with the given description
					removed = removed + ((EditableSceneObjectCollection)o).removeAllSceneObjectsWithDescription(description, searchIteratively);
				}
			}
		}
		
		// return the number of removed scene objects
		return removed;
	}

	/**
	 * Is scene object number i visible?
	 * @param i
	 * @return
	 */
	public boolean isSceneObjectVisible(int i)
	{
		return visibilities.get(i);
	}
	
	/**
	 * set visibility of scene object number i
	 * @param i
	 * @param isVisible
	 */
	public void setSceneObjectVisible(int i, boolean isVisible)
	{
		visibilities.set(i, isVisible);
	}
	
	public void setSceneObjectVisible(SceneObject o, boolean isVisible)
	{
		int i = getIndexOfSceneObject(o);

		if(i > -1) setSceneObjectVisible(i, isVisible);
	}

	/**
	 * Is scene object number i visible?
	 * @param i
	 * @return
	 */
	public boolean isSceneObjectVisibleWhenTrajectoryTracing(int i)
	{
		return visibilitiesWhenTrajectoryTracing.get(i);
	}
	
	/**
	 * set visibility of scene object number i
	 * @param i
	 * @param isVisible
	 */
	public void setSceneObjectVisibleWhenTrajectoryTracing(int i, boolean isVisibleWhenTrajectoryTracing)
	{
		visibilitiesWhenTrajectoryTracing.set(i, isVisibleWhenTrajectoryTracing);
	}
	
	public void setSceneObjectVisibleWhenTrajectoryTracing(SceneObject o, boolean isVisibleWhenTrajectoryTracing)
	{
		int i = getIndexOfSceneObject(o);

		if(i > -1) setSceneObjectVisibleWhenTrajectoryTracing(i, isVisibleWhenTrajectoryTracing);
	}


	/**
	 * Add a scene object with specified visibility to container
	 * 
	 * @param o	the scene object to be added
	 * @param isVisible
	 */
	public void addSceneObject(SceneObject o, boolean isVisible, boolean isVisibleWhenTrajectoryTracing)
	{
		if(o != null)
		{
			o.setParent(this);
			sceneObjects.add(o);
			visibilities.add(isVisible);	// make the new object visible by default
			visibilitiesWhenTrajectoryTracing.add(isVisibleWhenTrajectoryTracing);
		}
	}
	
	/**
	 * Add a scene object with specified visibility to container
	 * 
	 * @param o	the scene object to be added
	 * @param isVisible
	 */
	public void addSceneObject(SceneObject o, boolean isVisible)
	{
		addSceneObject(o, isVisible, isVisible);
	}


	/**
	 * Add a scene object to container
	 * 
	 * @param o	the scene object to be added
	 */
	public void addSceneObject(SceneObject o)
	{
		addSceneObject(o, true, true);	// make the new object visible by default
	}
	
	/**
	 * Add a scene object to container, at the position specified by index
	 * 
	 * @param o	the scene object to be added
	 */
	public void addSceneObject(int index, SceneObject o, boolean isVisible, boolean isVisibleWhenTrajectoryTracing)
	{
		o.setParent(this);
		sceneObjects.add(index, o);
		visibilities.add(index, isVisible);
		visibilitiesWhenTrajectoryTracing.add(index, isVisibleWhenTrajectoryTracing);
	}
	
	/**
	 * Add a scene object to container, at the position specified by index
	 * 
	 * @param o	the scene object to be added
	 */
	public void addSceneObject(int index, SceneObject o, boolean isVisible)
	{
		addSceneObject(index, o, isVisible, isVisible);
	}

	
	public void addSceneObject(int index, SceneObject o)
	{
		addSceneObject(index, o, true);
	}

	
	/**
	 * Replace the scene object at index with a new one
	 * @param index
	 * @param o the new scene object
	 */
	public void setSceneObject(int index, SceneObject o, boolean isVisible, boolean isVisibleWhenTrajectoryTracing)
	{
		o.setParent(this);
		sceneObjects.set(index, o);
		visibilities.set(index, isVisible);
		visibilitiesWhenTrajectoryTracing.add(index, isVisibleWhenTrajectoryTracing);
	}
	
	/**
	 * Replace the scene object at index with a new one
	 * @param index
	 * @param o the new scene object
	 */
	public void setSceneObject(int index, SceneObject o, boolean isVisible)
	{
		setSceneObject(index, o, isVisible, isVisible);
	}
	
	public void setSceneObject(int index, SceneObject o)
	{
		setSceneObject(index, o, true);
	}

	/**
	 * Remove an object from the scene
	 *
	 * @param o     the object to be removed 
	 */
	public void removeSceneObject(SceneObject o)
	{
		int index = sceneObjects.indexOf(o);
		sceneObjects.remove(index);
		visibilities.remove(index);
		visibilitiesWhenTrajectoryTracing.remove(index);
	}

	public ArrayList<SceneObject> getSceneObjects()
	{
		return sceneObjects;
	}
	
	public ArrayList<Boolean> getVisibilities()
	{
		return visibilities;
	}
	
	public ArrayList<Boolean> getVisibilitiesWhenTrajectoryTracing()
	{
		return visibilitiesWhenTrajectoryTracing;
	}

	public void clear() {
		sceneObjects.clear();
		visibilities.clear();
		visibilitiesWhenTrajectoryTracing.clear();
	}

	/**
	 * Search the container to find the scene object that first intersects the ray.
	 * 
	 * @param ray	the ray
	 * @return	the intersection
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		return getClosestRayIntersectionAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#getClosestRayIntersectionAvoidingOrigin(optics.raytrace.Ray, optics.raytrace.SceneObject)
	 */
	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		double shortestdistance=Double.POSITIVE_INFINITY,currentdistance;
		RaySceneObjectIntersection closest=RaySceneObjectIntersection.NO_INTERSECTION, current;   //this is the current closest intersection

		// look through all the visible objects
		for(int i=0; i<sceneObjects.size(); i++)
		{
			if(isSceneObjectVisible(i))
			{
				//if(this == originObject)		this is commented out for now, avoids specles when using solid geometry
				// avoid calculating the intersection where the ray originated, calculate the intersection with a slightly advanced ray
				// current = sceneObjects.get(i).getClosestRayIntersection(ray.getAdvancedRay(MyMath.TINY));

				current = sceneObjects.get(i).getClosestRayIntersectionAvoidingOrigin(ray, originObject);
				//else
				//this line of code is definitely causing the noise.
				//current = sceneObjects.get(i).getClosestRayIntersectionAvoidingOrigin(ray, originObject);
	
				if (current!=RaySceneObjectIntersection.NO_INTERSECTION){
					currentdistance=(current.p).getDifferenceWith(ray.getP()).getLength();
	
					if ((currentdistance > 0) && (currentdistance<shortestdistance)){
						shortestdistance=currentdistance;
						closest=current;
					}
				}
			}
		}

		return closest;
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		return getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, (SceneObjectPrimitive)null);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		double shortestdistance=Double.POSITIVE_INFINITY,currentdistance;
		RaySceneObjectIntersection closest=RaySceneObjectIntersection.NO_INTERSECTION, current;   //this is the current closest intersection

		// System.out.println("SceneObjectContainer("+description+")::getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin("+ray+", "+originObject+")");
		
		// look through all the visible objects
		for(int i=0; i<sceneObjects.size(); i++)
		{
			if(isSceneObjectVisible(i))
			{
				// System.out.print("[");
				
				//if(this == originObject)		this is commented out for now, avoids specles when using solid geometry
				// avoid calculating the intersection where the ray originated, calculate the intersection with a slightly advanced ray
				current = sceneObjects.get(i).getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject);
				//else
				//this line of code is definitely causing the noise.
				//current = sceneObjects.get(i).getClosestRayIntersectionAvoidingOrigin(ray, originObject);

				// System.out.println("]");

				if (current!=RaySceneObjectIntersection.NO_INTERSECTION)
				{
					currentdistance=(current.p).getDifferenceWith(ray.getP()).getLength();
	
					if ((currentdistance > 0) && (currentdistance<shortestdistance))
					{
						shortestdistance=currentdistance;
						closest=current;
					}
				}
			}
		}
		return closest;
	}

	/**
	 * Get an object in the scene
	 */
	public SceneObject getSceneObject(int index)
	{
		return sceneObjects.get(index);
	}
	
	/**
	 * The number of objects in the scene.
	 */
	public int getNumberOfSceneObjects()
	{
		return sceneObjects.size();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#transform(optics.raytrace.Transformation)
	 */
	@Override
	public SceneObjectContainer transform(Transformation t)
	{
		SceneObjectContainer soc = new SceneObjectContainer(description, getParent(), getStudio());

		for(int i=0; i<sceneObjects.size();i++)
		{
			soc.addSceneObject((sceneObjects.get(i)).transform(t), visibilities.get(i), visibilitiesWhenTrajectoryTracing.get(i));
		}

		return soc;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.SceneObject#insideObject(optics.raytrace.Vector3D)
	 */
	@Override
	public boolean insideObject(Vector3D p)
	{
		for(int i=0; i<sceneObjects.size(); i++)
			if(sceneObjects.get(i).insideObject(p))	return true;

		return false;
	}
	

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		ArrayList<SceneObjectPrimitive> SOPs = new ArrayList<SceneObjectPrimitive>();
		
		for(int i=0; i<getNumberOfSceneObjects(); i++)
		{
			SOPs.addAll(getSceneObject(i).getSceneObjectPrimitives());
		}

		return SOPs;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectClass#setStudio(optics.raytrace.core.Studio)
	 */
	@Override
	public void setStudio(Studio studio)
	{
		super.setStudio(studio);
		
		// if sceneObject is initialised...
		if(sceneObjects != null)
		{
			// ... set the studio for all the included scene objects
			for(int i=0; i<getNumberOfSceneObjects(); i++)
			{
				getSceneObject(i).setStudio(studio);
			}
		}
	}
	
	@Override
	public String getType()
	{
		return "Container";
	}
}
