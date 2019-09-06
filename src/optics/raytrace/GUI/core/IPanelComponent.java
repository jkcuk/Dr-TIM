package optics.raytrace.GUI.core;

import javax.swing.*;

/**
 * 
 * 
 * @author Johannes Courtial
 *
 */
public interface IPanelComponent
{
	/**
	 * Create a panel in which the component's parameters can be edited.
	 * @param iPanel
	 */
	public void createEditPanel(IPanel iPanel);
	
	/**
	 * Discard the edit panel.
	 */
	public void discardEditPanel();
	
	/**
	 * @return the edit panel
	 */
	public JPanel getEditPanel();
	
	/**
	 * Sets the values in the object's edit panel from the values in the object
	 * @return the object's edit panel
	 */
	public void setValuesInEditPanel();
	
	/**
	 * @return the altered IPanelComponent
	 */
	public IPanelComponent acceptValuesInEditPanel();
	
	/**
	 * called when editing of the IPanelComponent in front of this one is complete
	 * @param edited the IPanelComponent that has just been edited
	 */
	public void backToFront(IPanelComponent edited);
}