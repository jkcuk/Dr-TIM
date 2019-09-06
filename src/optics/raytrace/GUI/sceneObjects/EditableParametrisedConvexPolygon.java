package optics.raytrace.GUI.sceneObjects;

import java.io.Serializable;

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
import optics.raytrace.sceneObjects.ParametrisedConvexPolygon;
import optics.raytrace.surfaces.SurfaceColour;
import math.*;

/**
 * An editable convex polygon.
 * Note that the number of vertices can't currently be edited interactively.
 * 
 * @author Johannes Courtial
 */
public class EditableParametrisedConvexPolygon extends ParametrisedConvexPolygon
implements Serializable, IPanelComponent
{
	private static final long serialVersionUID = 8811347346944609723L;

	// GUI panels
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private LabelledVector3DPanel normalPanel, verticesPanel[], uUnitVectorPanel, vUnitVectorPanel;
	
	/**
	 * Constructor that sets everything explicitly.
	 * @param description
	 * @param pointInPolygonPlane
	 * @param normalToPlane
	 * @param vertices
	 * @param uUnitVector
	 * @param vUnitVector
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableParametrisedConvexPolygon(
			String description,
			Vector3D normalToPlane,
			Vector3D vertices[],
			Vector3D uUnitVector,
			Vector3D vUnitVector,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, normalToPlane, vertices, uUnitVector, vUnitVector, surfaceProperty, parent, studio);
	}

	/**
	 * Constructor that doesn't explicitly set the parametrisation of the plane, but instead finds one itself.
	 * @param description
	 * @param pointInPolygonPlane
	 * @param normalToPlane
	 * @param vertices
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableParametrisedConvexPolygon(
			String description,
			Vector3D normalToPlane,
			Vector3D vertices[],
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		// constructor of superclass
		super(description, normalToPlane, vertices, surfaceProperty, parent, studio);
	}
	
	public EditableParametrisedConvexPolygon(SceneObject parent, Studio studio)
	{
		this(
				"Convex polygon",	// description
				new Vector3D(0, 0, 1),	// normal to plane
				null,	// vertices
				SurfaceColour.DARK_RED_SHINY,	// surface property
				parent, studio
		);
		
		Vector3D vertices[] = new Vector3D[4];
		vertices[0] = new Vector3D(-1,-1, 10);
		vertices[1] = new Vector3D(-1, 1, 10);
		vertices[2] = new Vector3D( 1, 1, 10);
		vertices[3] = new Vector3D( 1,-1, 10);
		setVertices(vertices);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableParametrisedConvexPolygon(EditableParametrisedConvexPolygon original)
	{
		super(original);
	}

	@Override
	public EditableParametrisedConvexPolygon clone()
	{
		return new EditableParametrisedConvexPolygon(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Convex polygon"));

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
		
		normalPanel = new LabelledVector3DPanel("Normal");
		basicParametersPanel.add(normalPanel, "wrap");
		
		// add the vertex panels
		verticesPanel = new LabelledVector3DPanel[getVertices().length];
		for(int i=0; i<getVertices().length; i++)
		{
			verticesPanel[i] = new LabelledVector3DPanel("Vertex "+(i+1));
			basicParametersPanel.add(verticesPanel[i], "wrap");
		}

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
		
//		if(getVertices().length != 4)
//		{
//			new Exception("EditableParametrisedConvexPolygon::setValuesInEditPanel: Currently restricted to polygons with four vertices!").printStackTrace();
//		}
		normalPanel.setVector3D(getNormal());
		for(int i=0; i<getVertices().length; i++)
		{
			verticesPanel[i].setVector3D(getVertices()[i]);
		}
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		uUnitVectorPanel.setVector3D(getV1());
		vUnitVectorPanel.setVector3D(getV2());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableParametrisedConvexPolygon acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setNormal(normalPanel.getVector3D());
		
		// by definition, the first vertex lies on the polygon

		Vector3D vertices[] = new Vector3D[getVertices().length];
		for(int i=0; i<vertices.length; i++)
		{
			vertices[i] = verticesPanel[i].getVector3D();
		}		
		setVertices(vertices);

		setV1V2(
				uUnitVectorPanel.getVector3D(),
				vUnitVectorPanel.getVector3D()
			);

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
