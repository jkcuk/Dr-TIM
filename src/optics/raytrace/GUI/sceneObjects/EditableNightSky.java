package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.surfaces.PictureSurface;

/**
 * The night sky.
 * Uses RandomizedSkymap.t5_08192x04096.tif from https://svs.gsfc.nasa.gov/4451
 * 
 * @author Johannes Courtial
 */
public class EditableNightSky extends EditableScaledParametrisedSphere implements IPanelComponent
{	
	private static final long serialVersionUID = -3007529524278297166L;

	/**
	 * the name of the image that will define the silhouette
	 */
	protected String getImageFilename()
	{
		return "SkyMap.png";
	}
	
	//
	// constructors
	//
	
	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param bottomLeftCorner
	 * @param rightDirection
	 * @param upDirection
	 * @param width
	 * @param parent
	 * @param studio
	 * @throws SceneException 
	 */
	public EditableNightSky(
			String description,
			Vector3D centre,
			double radius,
			Vector3D pole,
			Vector3D phi0Direction,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(
				description,
				centre,
				radius,
				pole,
				phi0Direction,
				0, 1, 	// sThetaMin, sThetaMax,
				0, 1, 	// sPhiMin, sPhiMax,
				null,	// surfaceProperty -- will be set in a minute
				parent, 
				studio
			);
		
		setSurfaceProperty(new PictureSurface(
				this.getClass().getResource(getImageFilename()),	// java.net.URL imageURL
				0,	// double xMin
				1,	// double xMax
				0,	// double yMin
				1,	// double yMax
				false	//boolean shadowThrowing					
			));
	}
	
	
	public EditableNightSky(SceneObject parent, Studio studio)
	{
		this(
				"Night sky",	// description
				Vector3D.O,	// centre,
				1000,	// radius,
				Vector3D.Y,	// pole,
				Vector3D.Z,	// phi0Direction,
				parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 * @throws SceneException 
	 */
	public EditableNightSky(EditableNightSky original)
	throws SceneException
	{
		super(original);
	}

	@Override
	public EditableNightSky clone()
	{
		try {
			return new EditableNightSky(this);
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}
	
	// GUI
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		// the editPanel shows either the editSpherePanel or the editDetailsPanel
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Night sky"));
				
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");
		
		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
				
		northPolePanel = new LabelledVector3DPanel("Zenith direction");
		editPanel.add(northPolePanel, "wrap");
				
		phi0DirectionPanel = new LabelledVector3DPanel("Direction of azimuth axis");
		editPanel.add(phi0DirectionPanel, "wrap");
		
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
		centrePanel.setVector3D(getCentre());
		radiusPanel.setNumber(getRadius());
		northPolePanel.setVector3D(getPole());
		phi0DirectionPanel.setVector3D(getPhi0Direction());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedSphere acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
				
		setDirections(
				northPolePanel.getVector3D(),
				phi0DirectionPanel.getVector3D()
			);

		return this;
	}


}
