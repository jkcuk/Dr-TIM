package optics.raytrace.GUI.surfaces;

import optics.raytrace.GUI.lowLevel.SceneObjectPrimitivesComboBox;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;

public class TeleportingTargetsComboBox extends SceneObjectPrimitivesComboBox
{
	private static final long serialVersionUID = -427992970549932444L;

	public TeleportingTargetsComboBox(SceneObject scene)
	{
		super(scene);
	}
	
	/**
	 * Callback method -- override in subclass
	 * @param sop	the SceneObjectPrimitive
	 * @return	true if the SceneObjectPrimitive is to be included in the list, or false otherwise
	 */
	@Override
	public boolean inclusionCondition(SceneObjectPrimitive sop)
	{
		return (sop instanceof One2OneParametrisedObject);
	}
}
