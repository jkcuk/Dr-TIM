package optics.raytrace.GUI.surfaces;

import java.awt.FlowLayout;

import javax.swing.*;

import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;

/**
 * A label and a panel for editing a few of the parameters of a tiling.
 */
public class LabelledTeleportingTargetsComboBox extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private TeleportingTargetsComboBox list;
	
	public LabelledTeleportingTargetsComboBox(SceneObject scene)
	{
		super();
		setLayout(new FlowLayout());
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 

		// ... and add the new ones
		add(new JLabel("Destination "));
		list = new TeleportingTargetsComboBox(scene);
		add(list);
	}
	
	public void setDestinationObject(One2OneParametrisedObject destinationObject)
	{
		list.setObject((SceneObjectPrimitive)destinationObject);
	}

	public One2OneParametrisedObject getDestinationObject()
	{
		return (One2OneParametrisedObject)(list.getObject());
	}
	
	public SceneObject getScene()
	{
		return list.getScene();
	}

	public void setScene(SceneObject scene)
	{
		list.setScene(scene);
	}
	
	/**
	 * When the list of scene objects needs updating, call this
	 */
	public void refreshSceneObjectPrimitivesList()
	{
		// System.out.println("TeleportingParametersLine.refreshSceneObjectPrimitivesList()");
		setScene(getScene());
	}
}