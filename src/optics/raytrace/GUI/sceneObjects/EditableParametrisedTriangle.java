package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
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
import optics.raytrace.sceneObjects.ParametrisedTriangle;

public class EditableParametrisedTriangle extends ParametrisedTriangle
implements IPanelComponent
{
	private static final long serialVersionUID = -3275345246864197864L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel vertex1Panel, vertex1ToVertex2Panel, vertex1ToVertex3Panel, uUnitVectorPanel, vUnitVectorPanel;
	private JCheckBox semiInfiniteCheckBox;
	private SurfacePropertyPanel surfacePropertyPanel;

	/**
	 * Creates a parametrised triangle.
	 * 
	 * @param description
	 * @param vertex1
	 * @param v1	span Vector3D 1
	 * @param v2	span Vector3D 2
	 * @param semiInfinite
	 * @param uUnitVector
	 * @param vUnitVector
	 * @param surfaceProperty	the surface properties
	 * @param parent
	 * @param studio
	 */
	public EditableParametrisedTriangle(
			String description,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			boolean semiInfinite,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, vertex1, vertex1ToVertex2, vertex1ToVertex3, semiInfinite, uUnitVector, vUnitVector, surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedTriangle(ParametrisedTriangle original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableParametrisedTriangle clone()
	{
		return new EditableParametrisedTriangle(this);
	}
	
	/**
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param outsidePosition
	 * @param uUnitVector
	 * @param vUnitVector
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return	an EditableParametrisedTriangle with the given vertices and parametrisation given by  uUnitVector and vUnitVector, and with outwardsSurfaceNormal such that insidePosition lies on the outside side
	 */
	public static EditableParametrisedTriangle makeEditableParametrisedTriangleFromVertices(
			String description,
			Vector3D vertex1,
			Vector3D vertex2,
			Vector3D vertex3,
			Vector3D outsidePosition,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		EditableParametrisedTriangle t = new EditableParametrisedTriangle(
				description,
				vertex1,
				Vector3D.difference(vertex2, vertex1),	// vertex1ToVertex2,
				Vector3D.difference(vertex3, vertex1),	// vertex1ToVertex3,
				false,	// not semiInfinite,
				uUnitVector,
				vUnitVector,
				surfaceProperty,
				parent,
				studio
			);
		
		// is outsidePosition actually on  the outside?
		if(t.insideObject(outsidePosition))
		{
			// no, it isn't
			// the outwards-facing normal is the wrong way round;
			// switch vertices 2 and 3 to reverse it
			t = new EditableParametrisedTriangle(
					description,
					vertex1,
					Vector3D.difference(vertex3, vertex1),	// vertex1ToVertex3,
					Vector3D.difference(vertex2, vertex1),	// vertex1ToVertex2,
					false,	// not semiInfinite,
					uUnitVector,
					vUnitVector,
					surfaceProperty,
					parent,
					studio
				);
		}
		
		return t;
	}

	/**
	 * make an EditableParametrisedTriangle from three vertex positions
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param uUnitVector
	 * @param vUnitVector
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return
	 */
	public static EditableParametrisedTriangle makeEditableParametrisedTriangleFromVertices(
			String description,
			Vector3D vertex1,
			Vector3D vertex2,
			Vector3D vertex3,
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		return new EditableParametrisedTriangle(
				description,
				vertex1,
				Vector3D.difference(vertex2, vertex1),	// vertex1ToVertex2,
				Vector3D.difference(vertex3, vertex1),	// vertex1ToVertex3,
				false,	// not semiInfinite,
				uUnitVector,
				vUnitVector,
				surfaceProperty,
				parent,
				studio
			);
	}
	
	/**
	 * make an EditableParametrisedTriangle from three vertex positions
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return
	 */
	public static EditableParametrisedTriangle makeEditableParametrisedTriangleFromVertices(
			String description,
			Vector3D vertex1,
			Vector3D vertex2,
			Vector3D vertex3,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		return new EditableParametrisedTriangle(
				description,
				vertex1,
				Vector3D.difference(vertex2, vertex1),	// vertex1ToVertex2,
				Vector3D.difference(vertex3, vertex1),	// vertex1ToVertex3,
				false,	// not semiInfinite,
				Vector3D.difference(vertex2, vertex1).getNormalised(),	// uUnitVector
				Vector3D.difference(vertex3, vertex1).getNormalised(),	// vUnitVector
				surfaceProperty,
				parent,
				studio
			);
	}
	
	/**
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param invertNormal	if true, the order of the vertices is reversed so that the normal, (vertex2 - vertex1) x (vertex3 - vertex1), is inverted
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return	an editable parametrised triangle scene-object that corresponds to the three vertex positions
	 */
	public static EditableParametrisedTriangle makeEditableParametrisedTriangleFromVertices(
			String description,
			Vector3D vertex1,
			Vector3D vertex2,
			Vector3D vertex3,
			boolean invertNormal,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		return (
			invertNormal
			? makeEditableParametrisedTriangleFromVertices(description, vertex3, vertex2, vertex1, surfaceProperty, parent, studio)
			: makeEditableParametrisedTriangleFromVertices(description, vertex1, vertex2, vertex3, surfaceProperty, parent, studio)
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
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Triangle"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		vertex1Panel = new LabelledVector3DPanel("Vertex 1");
		editPanel.add(vertex1Panel, "wrap");
		
		vertex1ToVertex2Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 2");
		editPanel.add(vertex1ToVertex2Panel, "wrap");

		vertex1ToVertex3Panel = new LabelledVector3DPanel("Vector from vertex 1 to vertex 3");
		editPanel.add(vertex1ToVertex3Panel, "wrap");
		
		semiInfiniteCheckBox = new JCheckBox("Semi-infinite");
		editPanel.add(semiInfiniteCheckBox, "wrap");
		
		uUnitVectorPanel = new LabelledVector3DPanel("Unit vector in u direction");
		editPanel.add(uUnitVectorPanel, "wrap");

		vUnitVectorPanel = new LabelledVector3DPanel("Unit vector in v direction");
		editPanel.add(vUnitVectorPanel, "wrap");

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
		vertex1Panel.setVector3D(getVertex1());
		vertex1ToVertex2Panel.setVector3D(getVertex1ToVertex2());
		vertex1ToVertex3Panel.setVector3D(getVertex1ToVertex3());
		semiInfiniteCheckBox.setSelected(isSemiInfinite());
		uUnitVectorPanel.setVector3D(getuUnitVector());
		vUnitVectorPanel.setVector3D(getvUnitVector());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedTriangle acceptValuesInEditPanel()
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
