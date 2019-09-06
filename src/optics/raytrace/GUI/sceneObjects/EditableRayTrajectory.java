package optics.raytrace.GUI.sceneObjects;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.*;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.RayTrajectory;

/**
 * A ray trajectory.
 * 
 * Aidan Strathearn and myself had tried to do a calculation whereby we wanted to
 * describe a light-ray trajectory in terms of the start position and a complex
 * position the ray passes through, and extend this class accordingly.
 * However, it turned out that we made a mistake in the calculation of the light-ray
 * direction, which made the extensions of this class wrong, and so we commented them out.
 * We will try to fix our calculation, and then we can fix the light-ray-direction
 * method and uncomment the other code related to this.
 * 
 * @author Johannes
 *
 */
public class EditableRayTrajectory extends RayTrajectory implements IPanelComponent
{
	private static final long serialVersionUID = -764387131851979799L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel startPointPanel, startDirectionPanel;
//	private LabelledVector3DPanel complexPositionRePanel, complexPositionImPanel;
	private LabelledDoublePanel rayRadiusPanel;
	private SurfacePropertyPanel surfacePropertyPanel;
	private LabelledIntPanel maxTraceLevelPanel;
	private JCheckBox reportToConsoleCheckBox;
	
	/**
	 * @author johannes
	 * a quick enum that allows the user to choose how to specify the ray direction
	 */
//	public enum RayDirectionSpecificationType
//	{
//		EXPLICIT_DIRECTION("Explicit ray direction"),
//		COMPLEX_POSITION("Complex position on ray");
//		
//		private String description;
//		private RayDirectionSpecificationType(String description) {this.description = description;}	
//		@Override
//		public String toString() {return description;}
//	}
	
//	private RayDirectionSpecificationType rayDirectionSpecification;
//	// real and imaginary parts of the complex position the ray passes through
//	private Vector3D complexPositionRe, complexPositionIm;

	
	/**
	 * @param description
	 * @param startPoint
	 * @param startTime
	 * @param startDirection
	 * @param rayRadius
	 * @param surfaceProperty
	 * @param maxTraceLevel
	 * @param reportToConsole
	 * @param parent
	 * @param studio
	 * Constructor that specifies the ray in terms of its direction
	 */
	public EditableRayTrajectory(
			String description,
			Vector3D startPoint,
			double startTime,
			Vector3D startDirection,
			double rayRadius,
			SurfaceProperty surfaceProperty,
			int maxTraceLevel,
			boolean reportToConsole,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, startPoint, startTime, startDirection, rayRadius, surfaceProperty, maxTraceLevel, reportToConsole, parent, studio);
		
//		// calculate a complex position this ray passes through
//		complexPositionRe = Vector3D.sum(startPoint, startDirection);
//		complexPositionIm = new Vector3D(0, 0, 0);	// actually, it's a real position
//		
//		// store the fact that the direction is specified in terms of the direction vector
//		rayDirectionSpecification = RayDirectionSpecificationType.EXPLICIT_DIRECTION;
	}

	/**
	 * @param description
	 * @param startPoint
	 * @param startTime
	 * @param complexPositionRe
	 * @param complexPositionIm
	 * @param rayRadius
	 * @param surfaceProperty
	 * @param maxTraceLevel
	 * @param reportToConsole
	 * @param parent
	 * @param studio
	 * constructor that defines the ray direction in terms of the complex position
	 * complexPositionReal + i*complexPositionImaginary that the ray passes through
	 */
//	public EditableRayTrajectory(
//			String description,
//			Vector3D startPoint,
//			double startTime,
//			Vector3D complexPositionRe,
//			Vector3D complexPositionIm,
//			double rayRadius,
//			SurfaceProperty surfaceProperty,
//			int maxTraceLevel,
//			SceneObject parent, 
//			Studio studio
//	)
//	{
//		super(description, startPoint, startTime, calculateRayDirectionFromComplexPosition(startPoint, complexPositionRe, complexPositionIm), rayRadius, surfaceProperty, maxTraceLevel, parent, studio);
//				
//		// store the complex position this ray passes through
//		this.complexPositionRe = complexPositionRe;
//		this.complexPositionIm = complexPositionIm;
//		
//		// store the fact that the direction is specified in terms of the direction vector
//		rayDirectionSpecification = RayDirectionSpecificationType.COMPLEX_POSITION;
//	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableRayTrajectory(EditableRayTrajectory original)
	{
		super(original);
		
//		// copy the other parameters
//		setComplexPositionRe(original.getComplexPositionRe());
//		setComplexPositionIm(original.getComplexPositionIm());
//		setRayDirectionSpecification(original.getRayDirectionSpecification());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedCylinder#clone()
	 */
	@Override
	public EditableRayTrajectory clone()
	{
		return new EditableRayTrajectory(this);
	}
	
//	public RayDirectionSpecificationType getRayDirectionSpecification() {
//		return rayDirectionSpecification;
//	}
//
//	public void setRayDirectionSpecification(
//			RayDirectionSpecificationType rayDirectionSpecification) {
//		this.rayDirectionSpecification = rayDirectionSpecification;
//	}
//
//	public Vector3D getComplexPositionRe() {
//		return complexPositionRe;
//	}
//
//	public void setComplexPositionRe(Vector3D complexPositionRe) {
//		this.complexPositionRe = complexPositionRe;
//	}
//
//	public Vector3D getComplexPositionIm() {
//		return complexPositionIm;
//	}
//
//	public void setComplexPositionIm(Vector3D complexPositionIm) {
//		this.complexPositionIm = complexPositionIm;
//	}

	/**
	 * @param startPoint
	 * @param complexPositionRe
	 * @param complexPositionIm
	 * @return the normalised direction of a light ray that passes through startPoint and through the complex position complexPositionRe + i*complexPositionIm
	 */
	// we made a mistake in the calculation of the light-ray direction, and so this
	// method is simply wrong
//	public static Vector3D calculateRayDirectionFromComplexPosition(Vector3D startPoint, Vector3D complexPositionRe, Vector3D complexPositionIm)
//	{
//		Vector3D v = Vector3D.difference(startPoint, complexPositionRe);
//		
//		// t = +/- modT
//		double modT = Math.sqrt(1-Vector3D.crossProduct(v, complexPositionIm).getModSquared()) / v.getLength();
//
//		// ray direction
//		// TODO for the moment, pick the positive sign for t
//		Vector3D d=Vector3D.sum(
//				v.getProductWith(-modT),
//				Vector3D.crossProduct(v, complexPositionIm)
//			);
//
//		System.out.println("Ray direction corresponding to complex position is "+d);
//		
//		return d;
//	}
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
	
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ray trajectory"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		startPointPanel = new LabelledVector3DPanel("Start point");
		editPanel.add(startPointPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

//		switch(rayDirectionSpecification)
//		{
//		case EXPLICIT_DIRECTION:
			startDirectionPanel = new LabelledVector3DPanel("Initial ray direction");
			editPanel.add(startDirectionPanel, "wrap");
//			break;
//			
//		case COMPLEX_POSITION:
//		default:
//			complexPositionRePanel = new LabelledVector3DPanel("Complex position");
//			editPanel.add(complexPositionRePanel);
//			complexPositionImPanel = new LabelledVector3DPanel("+ i");
//			editPanel.add(complexPositionImPanel, "wrap");
//			break;
//		}
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		rayRadiusPanel = new LabelledDoublePanel("Ray radius");
		editPanel.add(rayRadiusPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		surfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(surfacePropertyPanel, "wrap");
		surfacePropertyPanel.setIPanel(iPanel);
		
		maxTraceLevelPanel = new LabelledIntPanel("Max. trace level");
		editPanel.add(maxTraceLevelPanel, "wrap");
		
		reportToConsoleCheckBox = new JCheckBox("Report ray progress to console");
		editPanel.add(reportToConsoleCheckBox);

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
		startPointPanel.setVector3D(getStartPoint());
//		switch(rayDirectionSpecification)
//		{
//		case EXPLICIT_DIRECTION:
			startDirectionPanel.setVector3D(getStartDirection());
//			break;
//			
//		case COMPLEX_POSITION:
//		default:
//			complexPositionRePanel.setVector3D(getComplexPositionRe());
//			complexPositionImPanel.setVector3D(getComplexPositionIm());
//			break;
//		}
		rayRadiusPanel.setNumber(getRayRadius());
		surfacePropertyPanel.setSurfaceProperty(getSurfaceProperty());
		maxTraceLevelPanel.setNumber(getMaxTraceLevel());
		reportToConsoleCheckBox.setSelected(isReportToConsole());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRayTrajectory acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		// start afresh
		sceneObjects.clear();
				
		setStartPoint(startPointPanel.getVector3D());
//		switch(rayDirectionSpecification)
//		{
//		case EXPLICIT_DIRECTION:
			setStartDirection(startDirectionPanel.getVector3D());
//			break;
//			
//		case COMPLEX_POSITION:
//		default:
//			setComplexPositionRe(complexPositionRePanel.getVector3D());
//			setComplexPositionIm(complexPositionImPanel.getVector3D());
//			setStartDirection(calculateRayDirectionFromComplexPosition(getStartPoint(), complexPositionRe, complexPositionIm));
//			break;
//		}

		setRayRadius(rayRadiusPanel.getNumber());
		setSurfaceProperty(surfacePropertyPanel.getSurfaceProperty());
		setMaxTraceLevel(maxTraceLevelPanel.getNumber());
		setReportToConsole(reportToConsoleCheckBox.isSelected());

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
