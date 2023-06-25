package optics.raytrace.GUI.sceneObjects;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.MyMath;
import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
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
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;

/**
 * The sky, day, night, Earthrise...
 * Night sky uses RandomizedSkymap.t5_08192x04096.tif from https://svs.gsfc.nasa.gov/4451
 * Earthrise -- see https://en.wikipedia.org/wiki/Earthrise
 * 
 * @author Johannes Courtial
 */
public class EditableSky extends EditableScaledParametrisedSphere implements IPanelComponent
{	
	private static final long serialVersionUID = -3007529524278297166L;

	public enum SkyType
	{
		DAY("Day sky"),
		NIGHT("Night sky"),
		EARTHRISE("Earthrise");
		
		private String description;
		
		SkyType(String description) {this.description = description;}
		
		public String toString() {return description;}
	}
	
	protected SkyType skyType;
		
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
	public EditableSky(
			String description,
			SkyType skyType,
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
				0, Math.PI, 	// sThetaMin, sThetaMax,
				0, 2*Math.PI, 	// sPhiMin, sPhiMax,
				null,	// surfaceProperty -- will be set in a minute
				parent, 
				studio
			);
		
		this.skyType = skyType;
		
		setup();
	}

	public void setup()
	{
		switch(skyType)
		{
		case EARTHRISE:
			double d = MyMath.deg2rad(2);	// the angular size of Earth as seen from the moon is around 2 degree3s
			PictureSurface ps = new PictureSurface(
					this.getClass().getResource("NASA-Apollo8-Dec24-Earthrise90.png"),	// java.net.URL imageURL
					1.48+d,	// double xMin
					1.48,	// double xMax
					3.2,	// double yMin
					3.2+d,	// double yMax
					false	//boolean shadowThrowing				
				);
			ps.setSurfacePropertyOutsidePicture(SurfaceColourLightSourceIndependent.BLACK);
			setSurfaceProperty(ps);
			break;
		case NIGHT:
			setSurfaceProperty(new PictureSurface(
					this.getClass().getResource("SkyMap.png"),	// java.net.URL imageURL
					0,	// double xMin
					Math.PI,	// double xMax
					0,	// double yMin
					2.*Math.PI,	// double yMax
					false	//boolean shadowThrowing					
				));
			break;
		case DAY:
		default:
			setSurfaceProperty(new SurfaceColourLightSourceIndependent(DoubleColour.LIGHT_BLUE, false));
		}
	}
	
	public EditableSky(SkyType skyType, SceneObject parent, Studio studio)
	{
		this(
				"Sky",	// description
				skyType,
				Vector3D.O,	// centre,
				MyMath.HUGE,	// radius,
				Vector3D.Y,	// pole,
				Vector3D.Z,	// phi0Direction,
				parent, studio
			);
	}
	
	public EditableSky(SceneObject parent, Studio studio)
	{
		this(SkyType.DAY, parent, studio);
	}


	/**
	 * Create a clone of original
	 * @param original
	 * @throws SceneException 
	 */
	public EditableSky(EditableSky original)
	throws SceneException
	{
		super(original);
	}

	@Override
	public EditableSky clone()
	{
		try {
			return new EditableSky(this);
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}
	
	// getters & setters
	
	public SkyType getSkyType() {
		return skyType;
	}

	public void setSkyType(SkyType skyType) {
		this.skyType = skyType;
	}


	
	// GUI
	
	private JComboBox<SkyType> skyTypeComboBox;
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		// the editPanel shows either the editSpherePanel or the editDetailsPanel
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Sky"));
				
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		skyTypeComboBox = new JComboBox<SkyType>(SkyType.values());
		editPanel.add(GUIBitsAndBobs.makeRow("Type", skyTypeComboBox), "wrap");
		
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
		skyTypeComboBox.setSelectedItem(skyType);
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
		setSkyType((SkyType)skyTypeComboBox.getSelectedItem());
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
				
		setDirections(
				northPolePanel.getVector3D(),
				phi0DirectionPanel.getVector3D()
			);
		
		setup();

		return this;
	}


}
