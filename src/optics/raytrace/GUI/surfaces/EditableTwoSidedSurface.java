package optics.raytrace.GUI.surfaces;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.TwoSidedSurface;


/**
 * 
 * 
 * @author Johannes Courtial
 *
 */
public class EditableTwoSidedSurface extends TwoSidedSurface implements IPanelComponent
{
	private static final long serialVersionUID = -820821216848852549L;

	private SurfacePropertyPanel insideSurfacePropertyPanel, outsideSurfacePropertyPanel, beingEdited;
	private JPanel editPanel;
	
	// the scene is there so that the Teleporting surface property can select
	// an object to teleport to
	private SceneObject scene;
	
	public EditableTwoSidedSurface(SurfaceProperty insideSurfaceProperty, SurfaceProperty outsideSurfaceProperty, SceneObject scene)
	{
		super(insideSurfaceProperty, outsideSurfaceProperty);
		setScene(scene);
	}
		
	public EditableTwoSidedSurface(EditableTwoSidedSurface original)
	{
		super(original);
		setScene(original.getScene());
	}
	
	@Override
	public EditableTwoSidedSurface clone()
	{
		return new EditableTwoSidedSurface(this);
	}
		
	@Override
	public void createEditPanel(IPanel iPanel)
	{
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Two-sided surface"));
		
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		insideSurfacePropertyPanel = new SurfacePropertyPanel(getScene());
		insideSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(insideSurfacePropertyPanel));
		insideSurfacePropertyPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inside"));
		editPanel.add(insideSurfacePropertyPanel);
		insideSurfacePropertyPanel.setIPanel(iPanel);

		outsideSurfacePropertyPanel = new SurfacePropertyPanel(getScene());
		outsideSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(outsideSurfacePropertyPanel));
		outsideSurfacePropertyPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outside"));
		editPanel.add(outsideSurfacePropertyPanel);
		outsideSurfacePropertyPanel.setIPanel(iPanel);
		
		editPanel.validate();
	}

	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/**
	 * @param repaintComponent the component to be repainted when this panel's size changes
	 * @return the edit panel
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}
	
	/**
	 * Sets the values in the object's edit panel from the values in the object
	 * @return the object's edit panel
	 */
	@Override
	public void setValuesInEditPanel()
	{
		insideSurfacePropertyPanel.setSurfaceProperty(getInsideSurfaceProperty());
		outsideSurfacePropertyPanel.setSurfaceProperty(getOutsideSurfaceProperty());
		
		editPanel.revalidate();
		// editPanel.repaint();
	}
	
	/**
	 * Uses values from the edit panel and sets them as the new variable value
	 */
	@Override
	public EditableTwoSidedSurface acceptValuesInEditPanel()
	{
		setInsideSurfaceProperty(insideSurfacePropertyPanel.getSurfaceProperty());
		setOutsideSurfaceProperty(outsideSurfacePropertyPanel.getSurfaceProperty());
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				if(beingEdited == insideSurfacePropertyPanel)
				{
					// inside surface property has been edited
					setInsideSurfaceProperty((SurfaceProperty)edited);
					insideSurfacePropertyPanel.setSurfaceProperty(getInsideSurfaceProperty());
				}
				if(beingEdited == outsideSurfacePropertyPanel)
				{
					// outside surface property has been edited
					setOutsideSurfaceProperty((SurfaceProperty)edited);
					outsideSurfacePropertyPanel.setSurfaceProperty(getOutsideSurfaceProperty());
				}
			}
	}

	class SurfacePropertyPanelListener implements ActionListener
	{
		private SurfacePropertyPanel surfacePropertyPanel;
		
		public SurfacePropertyPanelListener(SurfacePropertyPanel surfacePropertyPanel)
		{
			this.surfacePropertyPanel = surfacePropertyPanel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals(SurfacePropertyPanel.TILING_PARAMS_BUTTON_TEXT))
			{
				beingEdited = surfacePropertyPanel;
			}
		}
	}

	public SceneObject getScene() {
		return scene;
	}

	public void setScene(SceneObject scene) {
		this.scene = scene;
	}
}