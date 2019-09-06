package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.SceneObjectPrimitivesComboBox;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.ParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;

/**
 * An editable coordinate system, indicated by arrows pointing in the three main directions
 * 
 * @author Johannes
 *
 */
public class EditableObjectCoordinateSystem extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 9148074779370402726L;

	// parameters
	private One2OneParametrisedObject object;	// the scene object whose coordinate system is being visualised
	private double u, v;	// the coordinates (u, v) in the objects's coordinate system where the vectors are being placed
	
	// appearence of the arrows
	private double shaftRadius, tipAngle, tipLength;
	private SurfaceProperty surfacePropertyU, surfacePropertyV, surfacePropertyN;

	// GUI panels
	private JPanel basicParametersPanel, surfacesPanel;
	private ObjectComboBox objectPanel;
	private LabelledVector2DPanel coordinatesPanel;
	private LabelledDoublePanel shaftRadiusPanel, tipAnglePanel, tipLengthPanel;
	private SurfacePropertyPanel surfacePropertyUPanel, surfacePropertyVPanel, surfacePropertyNPanel;
	private JButton convertButton;

	public EditableObjectCoordinateSystem(
			String description,
			One2OneParametrisedObject object,
			Vector2D coordinates,
			double shaftRadius,
			double tipAngle,
			double tipLength,
			SurfaceProperty surfacePropertyU,
			SurfaceProperty surfacePropertyV,
			SurfaceProperty surfacePropertyN,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setObject(object);
		setCoordinates(coordinates);
		setShaftRadius(shaftRadius);
		setTipAngle(tipAngle);
		setTipLength(tipLength);
		setSurfacePropertyU(surfacePropertyU);
		setSurfacePropertyV(surfacePropertyV);
		setSurfacePropertyN(surfacePropertyN);

		addSceneObjects();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableObjectCoordinateSystem(EditableObjectCoordinateSystem original)
	{
		super(original);
		
		// copy the parameters into this instance's variables
		setObject(original.getObject());
		setCoordinates(original.getCoordinates());
		setShaftRadius(original.getShaftRadius());
		setTipAngle(original.getTipAngle());
		setTipLength(original.getTipLength());
		setSurfacePropertyU(original.getSurfacePropertyU());
		setSurfacePropertyV(original.getSurfacePropertyV());
		setSurfacePropertyN(original.getSurfacePropertyN());
	}

	@Override
	public EditableObjectCoordinateSystem clone()
	{
		return new EditableObjectCoordinateSystem(this);
	}

	private void addSceneObjects()
	{
		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();

		if(object == null) return;
		
		// create all the arrows
		Vector3D position = object.getPointForSurfaceCoordinates(u, v);
		
		ArrayList<Vector3D> directions = ((ParametrisedObject)object).getSurfaceCoordinateAxes(position);
		ArrayList<String> parameterNames = ((ParametrisedObject)object).getSurfaceCoordinateNames();
				
		addSceneObject(new EditableArrow(
				parameterNames.get(0) + " vector",
				position,	// start point
				Vector3D.sum(position, directions.get(0)),	// end point
				getShaftRadius(),
				getTipLength(),
				getTipAngle(),
				getSurfacePropertyU(),
				this,
				getStudio()
		));

		addSceneObject(new EditableArrow(
				parameterNames.get(1) + " vector",
				position,	// start point
				Vector3D.sum(position, directions.get(1)),	// end point
				getShaftRadius(),
				getTipLength(),
				getTipAngle(),
				getSurfacePropertyV(),
				this,
				getStudio()
		));

		addSceneObject(new EditableArrow(
				"surface normal unit vector",
				position,	// start point
				Vector3D.sum(position, ((SceneObjectPrimitive)object).getNormalisedOutwardsSurfaceNormal(position)),	// end point
				getShaftRadius(),
				getTipLength(),
				getTipAngle(),
				getSurfacePropertyN(),
				this,
				getStudio()
		));
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Object's surface coordinates"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the basic-parameters panel
		//
		
		// the editSpherePanel is for editing the sphere's basic parameters
		basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		objectPanel = new ObjectComboBox(getStudio().getScene());
		basicParametersPanel.add(objectPanel, "wrap");
		
		coordinatesPanel = new LabelledVector2DPanel("Coordinates (u, v)");
		basicParametersPanel.add(coordinatesPanel, "wrap");
		
		shaftRadiusPanel = new LabelledDoublePanel("arrow shaft radius");
		basicParametersPanel.add(shaftRadiusPanel, "wrap");
		
		tipAnglePanel = new LabelledDoublePanel("arrow tip angle (degrees)");
		basicParametersPanel.add(tipAnglePanel, "wrap");
		
		tipLengthPanel = new LabelledDoublePanel("arrow tip length");
		basicParametersPanel.add(tipLengthPanel, "wrap");

		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		basicParametersPanel.add(convertButton, "south");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		
		//
		// the surfaces panel
		// 
		surfacesPanel = new JPanel();
		surfacesPanel.setLayout(new MigLayout("insets 0"));

		surfacePropertyUPanel = new SurfacePropertyPanel("Surface of arrow in u direction", getStudio().getScene());
		surfacesPanel.add(surfacePropertyUPanel, "wrap");

		surfacePropertyVPanel = new SurfacePropertyPanel("Surface of arrow in v direction", getStudio().getScene());
		surfacesPanel.add(surfacePropertyVPanel, "wrap");
		
		surfacePropertyNPanel = new SurfacePropertyPanel("Surface of arrow in direction of surface normal", getStudio().getScene());
		surfacesPanel.add(surfacePropertyNPanel);
		
		tabbedPane.addTab("Surfaces", surfacesPanel);


		editPanel.add(tabbedPane);

		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// refresh the list of objects in the object panel
		if(getStudio() != null) objectPanel.setScene(getStudio().getScene()); 

		// initialize any fields
		descriptionPanel.setString(getDescription());
		objectPanel.setObject((SceneObjectPrimitive)getObject());
		coordinatesPanel.setVector2D(getCoordinates());
		shaftRadiusPanel.setNumber(getShaftRadius());
		tipAnglePanel.setNumber(MyMath.rad2deg(getTipAngle()));
		tipLengthPanel.setNumber(getTipLength());
		surfacePropertyUPanel.setSurfaceProperty(getSurfacePropertyU());
		surfacePropertyVPanel.setSurfaceProperty(getSurfacePropertyV());
		surfacePropertyNPanel.setSurfaceProperty(getSurfacePropertyN());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableObjectCoordinateSystem acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setObject((One2OneParametrisedObject)(objectPanel.getObject()));
		setCoordinates(coordinatesPanel.getVector2D());
		setShaftRadius(shaftRadiusPanel.getNumber());
		setTipAngle(MyMath.deg2rad(tipAnglePanel.getNumber()));
		setTipLength(tipLengthPanel.getNumber());
		setSurfacePropertyU(surfacePropertyUPanel.getSurfaceProperty());
		setSurfacePropertyV(surfacePropertyVPanel.getSurfaceProperty());
		setSurfacePropertyN(surfacePropertyNPanel.getSurfaceProperty());

		// ... and add the arrows
		addSceneObjects();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-local-coordinate-system");
		container.setValuesInEditPanel();
	}
	
	public One2OneParametrisedObject getObject() {
		return object;
	}

	public void setObject(One2OneParametrisedObject object) {
		this.object = object;
	}

	public Vector2D getCoordinates() {
		return new Vector2D(u, v);
	}

	public void setCoordinates(Vector2D coordinates) {
		this.u = coordinates.x;
		this.v = coordinates.y;
	}

	public double getShaftRadius() {
		return shaftRadius;
	}

	public void setShaftRadius(double shaftRadius) {
		this.shaftRadius = shaftRadius;
	}

	public double getTipAngle() {
		return tipAngle;
	}

	public void setTipAngle(double tipAngle) {
		this.tipAngle = tipAngle;
	}

	public double getTipLength() {
		return tipLength;
	}

	public void setTipLength(double tipLength) {
		this.tipLength = tipLength;
	}

	public SurfaceProperty getSurfacePropertyU() {
		return surfacePropertyU;
	}

	public void setSurfacePropertyU(SurfaceProperty surfacePropertyU) {
		this.surfacePropertyU = surfacePropertyU;
	}

	public SurfaceProperty getSurfacePropertyV() {
		return surfacePropertyV;
	}

	public void setSurfacePropertyV(SurfaceProperty surfacePropertyV) {
		this.surfacePropertyV = surfacePropertyV;
	}

	public SurfaceProperty getSurfacePropertyN() {
		return surfacePropertyN;
	}

	public void setSurfacePropertyN(SurfaceProperty surfacePropertyN) {
		this.surfacePropertyN = surfacePropertyN;
	}

	private class ObjectComboBox extends SceneObjectPrimitivesComboBox
	{
		private static final long serialVersionUID = -8976974723013386694L;

		public ObjectComboBox(SceneObject scene)
		{
			super(scene);
		}
		
		/**
		 * Callback method -- override in subclass
		 * @param sop	the SceneObjectPrimitive
		 * @return	true if the SceneObjectPrimitive is to be included in the list, or false otherwise
		 */
		@Override
		public boolean inclusionCondition(SceneObjectPrimitive sop)
		{
			return (sop instanceof One2OneParametrisedObject);
		}
	}
}