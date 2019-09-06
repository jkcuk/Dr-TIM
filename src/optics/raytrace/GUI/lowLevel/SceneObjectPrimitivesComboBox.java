package optics.raytrace.GUI.lowLevel;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;

/**
 * A ComboBox that allows selection of any SceneObjectPrimitive contained in a given scene.
 * Criteria for inclusion in the ComboBox can be given in the inclusionCondition callback method.
 * 
 * @author Johannes Courtial
 */
public class SceneObjectPrimitivesComboBox extends JComboBox<String>
{
	private static final long serialVersionUID = 4667658198824118220L;

	private SceneObject scene;
	private ArrayList<SceneObjectPrimitive> sceneObjectPrimitives;
	private Vector<String> sceneObjectDescriptions;

	public SceneObjectPrimitivesComboBox(SceneObject scene)
	{
		super();
		
		sceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>();
		sceneObjectDescriptions = new Vector<String>();

		setScene(scene);
	}
	
	public void refreshSceneObjectPrimitivesList()
	{
		setScene(getScene());
	}
	
	public void setObject(SceneObjectPrimitive object)
	{
		int index = sceneObjectPrimitives.indexOf(object);
		setSelectedIndex(index);
	}

	public SceneObjectPrimitive getObject()
	{
		if(getSelectedIndex() == -1)
			return null;	// nothing to select or nothing selected
		
		return sceneObjectPrimitives.get(getSelectedIndex());
	}

	public SceneObject getScene()
	{
		return scene;
	}
	
	/**
	 * Callback method -- override in subclass
	 * @param sop	the SceneObjectPrimitive
	 * @return	true if the SceneObjectPrimitive is to be included in the list, or false otherwise
	 */
	public boolean inclusionCondition(SceneObjectPrimitive sop)
	{
		return true;	// by default, include all SceneObjectPrimitives
	}

	public void setScene(SceneObject scene)
	{
		this.scene = scene;

		// System.out.println("SceneObjectPrimitivesListComboBox.setScene("+scene+")");

		// first remove any old items...
		removeAllItems();
		
		// ... and add the new ones
		if(scene != null)
		{
			ArrayList<SceneObjectPrimitive> allSceneObjectPrimitives = scene.getSceneObjectPrimitives();
			sceneObjectPrimitives.clear();
			sceneObjectDescriptions = new Vector<String>();
		
			for(int i=0; i<allSceneObjectPrimitives.size(); i++)
			{
				SceneObjectPrimitive sop = allSceneObjectPrimitives.get(i);
				if(inclusionCondition(sop))	// sop instanceof One2OneParametrisedSurface
				{
					String description=sop.getDescription();
					
					for(SceneObject o=sop.getParent(); o!=null; o=o.getParent())
						description = o.getDescription() + " > " + description;

					sceneObjectDescriptions.add(description);
					sceneObjectPrimitives.add(sop);
				}
			}
		
			for(String s : sceneObjectDescriptions) addItem(s);
			
			setEnabled(true);
		}

		if(getItemCount() == 0)
		{
			setEnabled(false);
			
			sceneObjectPrimitives.clear();
			sceneObjectDescriptions.clear();
			
			addItem(" -- no scene objects to choose from -- ");
		}
	}
}