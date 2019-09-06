package optics.raytrace.GUI.cameras;

import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;

/**
 * If an edible camera implements this interface then this means that its setScene method needs to be called before
 * editing.
 * One (the only?) reason for this is the "Focus on scene" button.
 * @author johannes
 *
 */
public interface NeedsSceneSetBeforeEditing
{
	public void setScene(EditableSceneObjectCollection scene);
}
