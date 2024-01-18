package optics.raytrace.GUI.surfaces;

import javax.swing.JPanel;

public abstract class EditableSurfaceProperty_old
{

	protected JPanel additionalParametersPanel;
	
	public abstract String getDescription();
		
	/**
	 * override this
	 */
	private void initialiseAdditionalParametersPanel()
	{
		additionalParametersPanel = new JPanel();
	}
	
	/**
	 * Return the additional-parameters panel.  If this hasn't been initialised yet, do that first.
	 * @return the additional-parameters panel
	 */
	public JPanel getAdditionalParametersPanel()
	{
		if(additionalParametersPanel == null) initialiseAdditionalParametersPanel();
		
		return additionalParametersPanel;
	}
	
	// public abstract void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio);

	public EditableSurfaceProperty_old() {
		// TODO Auto-generated constructor stub
	}

}
