package optics.raytrace.GUI.sceneObjects;

import java.io.Serializable;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledMinMaxPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.ScaledParametrisedCentredParallelogram;


/**
 * A centred parallelogram.
 */
public class EditableScaledParametrisedCentredParallelogram extends ScaledParametrisedCentredParallelogram
implements Serializable, IPanelComponent
{
	private static final long serialVersionUID = -1816766799238337560L;

	/**
	 * @param description
	 * @param centre	centre of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param suMin	lower end of range of scaled u
	 * @param suMax	upper end of range of scaled u
	 * @param svMin	lower end of range of scaled v
	 * @param svMax	upper end of range of scaled v
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, centre, spanVector1, spanVector2, suMin, suMax, svMin, svMax, surfaceProperty, parent, studio);
		
		createEditPanel(null);
	}

	/**
	 * @param description
	 * @param centre	centre of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableScaledParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2, 
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio
		)
	{
		this(description, centre, spanVector1, spanVector2, 0, 1, 0, 1, surfaceProperty, parent, studio);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableScaledParametrisedCentredParallelogram(EditableScaledParametrisedCentredParallelogram original)
	{
		super(original);
		
		createEditPanel(null);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableScaledParametrisedCentredParallelogram clone()
	{
		return new EditableScaledParametrisedCentredParallelogram(this);
	}
	
	
	/**
	 * 
	 * Editable-interface stuff
	 * 
	 */
	
	/**
	 * variables
	 */
	protected JPanel editPanel, basicParametersPanel, parametrisationPanel;
	protected LabelledStringPanel descriptionPanel;
	protected LabelledVector3DPanel centrePanel, widthVectorPanel, heightVectorPanel;
	protected SurfacePropertyPanel surfacePropertyPanel;
	protected LabelledMinMaxPanel suMinMaxPanel, svMinMaxPanel;
	protected JTabbedPane tabbedPane;
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rectangle"));
		
		tabbedPane = new JTabbedPane();

		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the rectangle's basic parameters
		basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
				
		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");

		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
		basicParametersPanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
		basicParametersPanel.add(heightVectorPanel, "wrap");

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		basicParametersPanel.add(surfacePropertyPanel);
		surfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		
		//
		// the parametrisation panel
		// 
		parametrisationPanel = new JPanel();
		parametrisationPanel.setLayout(new MigLayout("insets 0"));
		
		suMinMaxPanel = new LabelledMinMaxPanel("scaled u range");
		parametrisationPanel.add(suMinMaxPanel, "wrap");
		
		svMinMaxPanel = new LabelledMinMaxPanel("scaled v range");
		parametrisationPanel.add(svMinMaxPanel, "wrap");
				
		tabbedPane.addTab("Parametrisation", parametrisationPanel);

		// make sure they are in plane of rectangle!
		// define everything in terms of the Cartesian coordinate system spanned by the
		// normalised width and height vectors
		
		editPanel.add(tabbedPane);

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
		centrePanel.setVector3D(getCentre());
		widthVectorPanel.setVector3D(getSpanVector1());
		heightVectorPanel.setVector3D(getSpanVector2());
		suMinMaxPanel.setMin(getSUMin());
		suMinMaxPanel.setMax(getSUMax());
		svMinMaxPanel.setMin(getSVMin());
		svMinMaxPanel.setMax(getSVMax());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedCentredParallelogram acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setCentreAndSpanVectors(
				centrePanel.getVector3D(),
				widthVectorPanel.getVector3D(),
				heightVectorPanel.getVector3D()
			);

		setUScaling(suMinMaxPanel.getMin(), suMinMaxPanel.getMax());
		setVScaling(svMinMaxPanel.getMin(), svMinMaxPanel.getMax());
		
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


