package optics.raytrace.GUI.sceneObjects;

import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ParametrisedTriangle;
import math.*;

/**
 * A triangle
 * @author Johannes Courtial
 */
public class EditableTriangle extends ParametrisedTriangle
implements Serializable, IPanelComponent
{
	private static final long serialVersionUID = -8088924153313962298L;

	// GUI panels
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private LabelledVector3DPanel vertex1Panel, vertex1ToVertex2Panel, vertex1ToVertex3Panel, uUnitVectorPanel, vUnitVectorPanel;
	// private LabelledMinMaxPanel suMinMaxPanel, svMinMaxPanel;
	private JCheckBox semiInfiniteCheckBox;
	
	public EditableTriangle(
			String description,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			boolean semiInfinite,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty windowSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, vertex1, vertex1ToVertex2, vertex1ToVertex3, semiInfinite, uUnitVector, vUnitVector, windowSurfaceProperty, parent, studio);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableTriangle(EditableTriangle original)
	{
		super(original);
	}

	@Override
	public EditableTriangle clone()
	{
		return new EditableTriangle(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Triangle"));

		JTabbedPane tabbedPane = new JTabbedPane();

		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
				
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		vertex1Panel = new LabelledVector3DPanel("Vertex 1");
		basicParametersPanel.add(vertex1Panel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		vertex1ToVertex2Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 2");
		basicParametersPanel.add(vertex1ToVertex2Panel, "wrap");

		vertex1ToVertex3Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 3");
		basicParametersPanel.add(vertex1ToVertex3Panel, "wrap");
		
		semiInfiniteCheckBox = new JCheckBox("Semi-infinite");
		basicParametersPanel.add(semiInfiniteCheckBox);
		
		tabbedPane.addTab("Basic parameters", basicParametersPanel);


		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		surfacePropertyPanel = new SurfacePropertyPanel("Surface", false, getStudio().getScene());
		basicParametersPanel.add(surfacePropertyPanel);
		surfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Surface", surfacePropertyPanel);

		
		//
		// the parametrisation panel
		// 
		JPanel parametrisationPanel = new JPanel();
		parametrisationPanel.setLayout(new MigLayout("insets 0"));
		
		uUnitVectorPanel = new LabelledVector3DPanel("Unit vector in u direction");
		parametrisationPanel.add(uUnitVectorPanel, "wrap");

		vUnitVectorPanel = new LabelledVector3DPanel("Unit vector in v direction");
		parametrisationPanel.add(vUnitVectorPanel, "wrap");
		
//		suMinMaxPanel = new LabelledMinMaxPanel("scaled u range");
//		parametrisationPanel.add(suMinMaxPanel);
//		
//		svMinMaxPanel = new LabelledMinMaxPanel("scaled v range");
//		parametrisationPanel.add(svMinMaxPanel);
				
		tabbedPane.addTab("Parametrisation", parametrisationPanel);

		// make sure they are in plane of rectangle!
		// define everything in terms of the Cartesian coordinate system spanned by the
		// normalised width and height vectors
		
		editPanel.add(tabbedPane);

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
		vertex1Panel.setVector3D(getVertex1());
		vertex1ToVertex2Panel.setVector3D(getVertex1ToVertex2());
		vertex1ToVertex3Panel.setVector3D(getVertex1ToVertex3());
		semiInfiniteCheckBox.setSelected(isSemiInfinite());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		uUnitVectorPanel.setVector3D(getuUnitVector());
		vUnitVectorPanel.setVector3D(getvUnitVector());
//		suMinMaxPanel.setMin(getSUMin());
//		suMinMaxPanel.setMax(getSUMax());
//		svMinMaxPanel.setMin(getSVMin());
//		svMinMaxPanel.setMax(getSVMax());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableTriangle acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setVertex1(vertex1Panel.getVector3D());
		setDirectionVectors(
				vertex1ToVertex2Panel.getVector3D(),
				vertex1ToVertex3Panel.getVector3D(),
				uUnitVectorPanel.getVector3D(),
				vUnitVectorPanel.getVector3D()
			);
		setSemiInfinite(semiInfiniteCheckBox.isSelected());

		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());

		return this;
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
