package optics.raytrace.GUI.core;

import javax.swing.JButton;

import optics.raytrace.GUI.lowLevel.ButtonsPanel;

/**
 * 
 * 
 * @author Johannes Courtial
 *
 */
public interface ComponentWithButtonsPanel
{
	/**
	 * Create a panel in which the component's parameters can be edited.
	 * @param iPanel
	 */
	public ButtonsPanel getButtonsPanel();
	
	/**
	 * @return the JButton that should be the default button
	 */
	public JButton getDefaultButton();
}