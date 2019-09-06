package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ParametrisedPlane;

public class EditableParametrisedPlane extends ParametrisedPlane
implements IPanelComponent
{
	private static final long serialVersionUID = 6231924669248911139L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel pointOnPlanePanel, normalPanel;
	private SurfacePropertyPanel surfacePropertyPanel;

	/**
	 * Creates a parametrised plane.
	 * The surface normal is given by spanVector3D1 x spanVector3D2, which points in the direction of the outside
	 * of the surface.
	 * 
	 * @param description
	 * @param pointOnPlane
	 * @param v1	span Vector3D 1
	 * @param v2	span Vector3D 2
	 * @param sp	the surface properties
	 */
	public EditableParametrisedPlane(
			String description,
			Vector3D pointOnPlane,
			Vector3D v1,
			Vector3D v2,
			SurfaceProperty sp,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, pointOnPlane, v1, v2, sp, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedPlane(EditableParametrisedPlane original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableParametrisedPlane clone()
	{
		return new EditableParametrisedPlane(this);
	}

	/**
	 * Creates a parametrised plane.
	 * Two span vectors are derived from the surface normal.
	 * 
	 * @param description
	 * @param pointOnPlane
	 * @param surfaceNormal	surface normal
	 * @param sp	the surface properties
	 */
	public EditableParametrisedPlane(
			String description,
			Vector3D pointOnPlane,
			Vector3D normal,
			SurfaceProperty sp,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, pointOnPlane, normal, sp, parent, studio);
		
		setSpanVectors();
	}
	
	private void setSpanVectors()
	{
		// create a vector v2 that is perpendicular to surfaceNormal
		Vector3D v;
		if(getNormal().x == 0.) v = new Vector3D(1,0,0);
		else v = new Vector3D(0, 1, 0);
		Vector3D v2 = v.getDifferenceWith(v.getProjectionOnto(getNormal())).getNormalised();
		setV1V2(Vector3D.crossProduct(v2, getNormal()),	// so that surfaceNormal = v1 x v2
				v2
			);
	}

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Plane"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		pointOnPlanePanel = new LabelledVector3DPanel("Point on the plane");
		editPanel.add(pointOnPlanePanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		normalPanel = new LabelledVector3DPanel("Normal to plane (pointing \"outwards\")");
		editPanel.add(normalPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel);
		surfacePropertyPanel.setIPanel(iPanel);

		editPanel.validate();
	}

	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#getEditPanel()
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		pointOnPlanePanel.setVector3D(getPointOnPlane());
		normalPanel.setVector3D(getNormal());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedPlane acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setPointOnPlane(pointOnPlanePanel.getVector3D());
		setNormal(normalPanel.getVector3D());
		setSpanVectors();	// construct new span vectors
		
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(edited instanceof SurfaceProperty)
		{
			// the surface property has been edited
			setSurfaceProperty((SurfaceProperty)edited);
			surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		}
	}
}
