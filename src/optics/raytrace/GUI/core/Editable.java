package optics.raytrace.GUI.core;

import javax.swing.JComponent;

/**
 * An object that can be edited, i.e. that can provide a JComponent that allows editing of all relevant parameters
 */
public interface Editable {
	
	/**
	 * @return	a description of the class
	 */
	public abstract String getDescription();
		
	/**
	 * @return the edit panel
	 */
	public JComponent getEditPanel();
	
	/**
	 * Sets the values in the object's edit panel from the values in the object
	 * @return the object's edit panel
	 */
	public void setValuesInEditPanel();
	
	/**
	 * @return the altered IPanelComponent
	 */
	public void acceptValuesInEditPanel();

}
