package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Cuboid;

/**
 * An editable cuboid.
 * 
 * @author Johannes
 */
public class EditableCuboid_old extends EditableSceneObjectCollection implements ActionListener
{	
	private static final long serialVersionUID = 5469730626719384200L;

	// GUI panels
	protected LabelledVector3DPanel centrePanel, centre2ToFace1Panel, centre2ToFace2Panel, centre2ToFace3Panel;
	protected JButton convertButton;
	protected SurfacePropertyPanel surfacePropertyPanel;
	
	//
	// constructors
	//
	
	public EditableCuboid_old(
			String description,
			Vector3D centre,
			Vector3D centre2centreOfFace1,
			Vector3D centre2centreOfFace2,
			Vector3D centre2centreOfFace3,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				new Cuboid(description, centre, centre2centreOfFace1, centre2centreOfFace2, centre2centreOfFace3, surfaceProperty, parent, studio),	// sceneObjectContainer,
				false	// combinationModeEditable
			);
	}
	
	public EditableCuboid_old(
			SceneObject parent,
			Studio studio
		)
	{
		this(new Cuboid(parent, studio));
	}
	
	public EditableCuboid_old(Cuboid cuboid)
	{
		super(cuboid, false);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCuboid_old(EditableCuboid_old original)
	{
		this(original.getCuboid());
	}

	@Override
	public EditableCuboid_old clone()
	{
		return new EditableCuboid_old(this);
	}

	
	//
	// setters and getters
	//
	
	public Cuboid getCuboid()
	{
		return (Cuboid)getSceneObjectContainer();
	}

	public void setCuboid(Cuboid cuboid) {
		setSceneObjectContainer(cuboid);
	}
	

	
	
	
	
	//
	// GUI stuff
	//
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cuboid"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "span");

		centre2ToFace1Panel = new LabelledVector3DPanel("Vector from centre to the centre of face 1");
		editPanel.add(centre2ToFace1Panel, "span");

		centre2ToFace2Panel = new LabelledVector3DPanel("Vector from centre to the centre of face 2");
		editPanel.add(centre2ToFace2Panel, "span");

		centre2ToFace3Panel = new LabelledVector3DPanel("Vector from centre to the centre of face 3");
		editPanel.add(centre2ToFace3Panel, "span");
			
		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel, "span");
		surfacePropertyPanel.setIPanel(iPanel);

		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton);
		
		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		
		centrePanel.setVector3D(getCuboid().getCentre());
		centre2ToFace1Panel.setVector3D(getCuboid().getCentre2centreOfFace1());
		centre2ToFace2Panel.setVector3D(getCuboid().getCentre2centreOfFace2());
		centre2ToFace3Panel.setVector3D(getCuboid().getCentre2centreOfFace3());
		surfacePropertyPanel.setSurfaceProperty(getCuboid().getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCuboid_old acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		getCuboid().setCentre(centrePanel.getVector3D());
		getCuboid().setCentre2centreOfFace1(centre2ToFace1Panel.getVector3D());
		getCuboid().setCentre2centreOfFace2(centre2ToFace2Panel.getVector3D());
		getCuboid().setCentre2centreOfFace3(centre2ToFace3Panel.getVector3D());
		getCuboid().setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());

		// add the objects
		getCuboid().addSceneObjects();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-lens-TO-tetrahedron");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				getCuboid().setSurfaceProperty((SurfaceProperty)edited);
			}
	}
}