package optics.raytrace.GUI.surfaces;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.SurfaceTiling;


/**
 * 
 * 
 * @author Johannes Courtial
 *
 */
public class EditableSurfaceTiling extends SurfaceTiling implements IPanelComponent
{
	private static final long serialVersionUID = -2368916837305947334L;

	private SurfacePropertyPanel surfacePropertyPanel1, surfacePropertyPanel2, beingEdited;
	private LabelledDoublePanel widthULine, widthVLine;
	private JPanel editPanel;
	
	// the scene is there so that the Teleporting surface property can select
	// an object to teleport to
	private SceneObject scene;
	
	public EditableSurfaceTiling(SurfaceProperty surfaceProperty1, SurfaceProperty surfaceProperty2, double widthU, double widthV, SceneObject scene)
	{
		super(surfaceProperty1, surfaceProperty2, widthU, widthV);
		setScene(scene);
	}
	
	public EditableSurfaceTiling(SceneObject scene)
	{
		super();
		setScene(scene);
	}
	
	public EditableSurfaceTiling(EditableSurfaceTiling original)
	{
		super(original);
		setScene(original.getScene());
	}
	
	@Override
	public EditableSurfaceTiling clone()
	{
		return new EditableSurfaceTiling(this);
	}
		
	@Override
	public void createEditPanel(IPanel iPanel)
	{
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Tiling"));
		
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		
		surfacePropertyPanel1 = new SurfacePropertyPanel(getScene());
		surfacePropertyPanel1.addButtonsActionListener(new SurfacePropertyPanelListener(surfacePropertyPanel1));
		surfacePropertyPanel1.setBorder(GUIBitsAndBobs.getTitledBorder("Tile type 1"));
		editPanel.add(surfacePropertyPanel1);
		surfacePropertyPanel1.setIPanel(iPanel);

		surfacePropertyPanel2 = new SurfacePropertyPanel(getScene());
		surfacePropertyPanel1.addButtonsActionListener(new SurfacePropertyPanelListener(surfacePropertyPanel2));
		surfacePropertyPanel2.setBorder(GUIBitsAndBobs.getTitledBorder("Tile type 2"));
		editPanel.add(surfacePropertyPanel2);
		surfacePropertyPanel2.setIPanel(iPanel);
		
		widthULine = new LabelledDoublePanel("Period in u direction");
		editPanel.add(widthULine);

		widthVLine = new LabelledDoublePanel("Period in v direction");
		editPanel.add(widthVLine);
		
		editPanel.validate();
	}
	
	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/**
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
		surfacePropertyPanel1.setSurfaceProperty(getSurfaceProperty1());
		surfacePropertyPanel2.setSurfaceProperty(getSurfaceProperty2());
		
		widthULine.setNumber(getWidthU());
		widthVLine.setNumber(getWidthV());
		
		editPanel.revalidate();
		// editPanel.repaint();
	}
	
	/**
	 * Uses values from the edit panel and sets them as the new variable value
	 */
	@Override
	public EditableSurfaceTiling acceptValuesInEditPanel()
	{
		setSurfaceProperty1(surfacePropertyPanel1.getSurfaceProperty());
		setSurfaceProperty2(surfacePropertyPanel2.getSurfaceProperty());
		setWidthU(widthULine.getNumber());
		setWidthV(widthVLine.getNumber());
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				if(beingEdited == surfacePropertyPanel1)
				{
					// surface property 1 has been edited
					setSurfaceProperty1((SurfaceProperty)edited);
					surfacePropertyPanel1.setSurfaceProperty(getSurfaceProperty1());
				}
				if(beingEdited == surfacePropertyPanel2)
				{
					// surface property 2 has been edited
					setSurfaceProperty2((SurfaceProperty)edited);
					surfacePropertyPanel2.setSurfaceProperty(getSurfaceProperty2());
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