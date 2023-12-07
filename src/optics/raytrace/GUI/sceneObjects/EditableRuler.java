package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.surfaces.SurfaceColour;
import math.*;

/**
 * A ruler consisting of cylinders along with numbers. A full step is described by one floor tilling.
 * @author Maik
 */
public class EditableRuler extends EditableSceneObjectCollection implements ActionListener
{	

	private static final long serialVersionUID = 5351654768776246088L;
	
	// parameters
	private boolean onlyPositive;
	private double maxLength, cylinderRadius;
	private double nDivisions;//The divisions. if this is 1 there are unit steps. 
	private Vector3D centre, positiveDirection, divisionSpanVector;  
	private boolean shadowThrowing;

	// GUI panels
//	private JPanel editPanel;
//	private StringLine descriptionPanel;
	private LabelledDoublePanel maxLengthPanel, cylinderRadiusPanel, nDivisionsPanel; 
	private LabelledVector3DPanel positiveDirectionPanel, centrePanel, divisionSpanVectorPanel;
	private JButton convertButton;
	private JCheckBox shadowThrowingCheckBox, onlyPositiveCheckBox;

	
	/**
	 * standard constructor. 
	 */
	public EditableRuler(
			String description,
			Vector3D centre,
			boolean onlyPositive,
			Vector3D positiveDirection,
			Vector3D divisionSpanVector,
			double maxLength, 
			double cylinderRadius,
			double nDivisions,
			boolean shadowThrowing,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.positiveDirection = positiveDirection;
		this.divisionSpanVector = divisionSpanVector;
		this.onlyPositive = onlyPositive;
		this.maxLength = maxLength;
		this.cylinderRadius = cylinderRadius;
		this.nDivisions = nDivisions;
		this.shadowThrowing = shadowThrowing;
		this.centre = centre;

		addCylinders();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableRuler(EditableRuler original)
	{
		super(original);
		
		// copy the original's parameters
		this.positiveDirection = original.positiveDirection;
		this.onlyPositive = original.onlyPositive;
		this.cylinderRadius = original.cylinderRadius;
		this.divisionSpanVector = original.divisionSpanVector;
		this.maxLength = original.maxLength;
		this.nDivisions = original.nDivisions;
		this.shadowThrowing = original.shadowThrowing;
		this.centre = original.centre;
	}

	@Override
	public EditableRuler clone()
	{
		return new EditableRuler(this);
	}

	public boolean isOnlyPositive() {
		return onlyPositive;
	}

	public void setOnlyPositive(boolean onlyPositive) {
		this.onlyPositive = onlyPositive;
	}

	public double getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(double maxLength) {
		this.maxLength = maxLength;
	}

	public double getCylinderRadius() {
		return cylinderRadius;
	}

	public void setCylinderRadius(double cylinderRadius) {
		this.cylinderRadius = cylinderRadius;
	}

	public double getnDivisions() {
		return nDivisions;
	}

	public void setnDivisions(double nDivisions) {
		this.nDivisions = nDivisions;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getPositiveDirection() {
		return positiveDirection;
	}

	public void setPositiveDirection(Vector3D positiveDirection) {
		this.positiveDirection = positiveDirection;
	}

	public Vector3D getDivisionSpanVector() {
		return divisionSpanVector;
	}

	public void setDivisionSpanVector(Vector3D divisionSpanVector) {
		this.divisionSpanVector = divisionSpanVector;
	}

	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}

	
	
	private void addCylinders()
	{
		if(nDivisions == 0) {
			System.err.println("The number of divisions cannot be zero!");
		}else {
		//setting all of the surface properties
		SurfaceColour colour = new SurfaceColour("black matt", DoubleColour.BLACK, DoubleColour.BLACK, shadowThrowing);
		//adding the main ruler cylinder to the scene. 
		Vector3D startPoint, endPoint;
		if(onlyPositive) {
			startPoint = centre;
		}else {
			startPoint = Vector3D.sum(centre, positiveDirection.getWithLength(-maxLength));
		}
		endPoint  = Vector3D.sum(centre, positiveDirection.getWithLength(maxLength));
		
		addSceneObject(new EditableParametrisedCylinder(
				"ruler line",
				startPoint,	// start point
				endPoint,	// end point
				cylinderRadius,	// radius
				colour,
				this,
				getStudio()
				));
		
		// create all the division cylinders and numbering.
		EditableSceneObjectCollection rulerDivisions = new EditableSceneObjectCollection("ruler", true, this, getStudio());
		int totalDivisionNumbers = (int) Math.floor(maxLength*nDivisions);
		Vector3D posDivisionCentre, negDivisionCentre;
		String decimalPlaces = "%."+(int)Math.ceil((double)nDivisions/10)+"f";
		//System.out.println(decimalPlaces);
		

		
		for(int i=0; i<=totalDivisionNumbers; i++)
		{	
		
			double positionScalar = (double)i/nDivisions;
			posDivisionCentre = Vector3D.sum(centre,positiveDirection.getWithLength((double)i/nDivisions));
			
			Vector3D smallerDivisionSpanVector; 
			if(i%nDivisions == 0) {
				smallerDivisionSpanVector = divisionSpanVector.getProductWith(1/2.);
			}else {
				smallerDivisionSpanVector = divisionSpanVector.getProductWith(1/3.);
			}
			
//			System.out.println("divisionSpanVector "+divisionSpanVector);
//			System.out.println("smallerDivisionSpanVector "+smallerDivisionSpanVector);
			
			double smallerCylinderRadius = cylinderRadius/(((i%nDivisions)==0)?1.4:1.8);
			double textHeight = smallerCylinderRadius*20/nDivisions;
			Vector3D textverticalDisplacement = ((i%2)==0)?Vector3D.sum(smallerDivisionSpanVector.getWithLength(-textHeight/1.5), smallerDivisionSpanVector.getProductWith(-1.05)):smallerDivisionSpanVector.getProductWith(1.05);
			
		ParametrisedCylinder division = new ParametrisedCylinder(
				"division #"+i,
					Vector3D.sum(posDivisionCentre,smallerDivisionSpanVector.getProductWith(-1)),	// start point The if statement is used to make all the interger lines a bit longer..
					Vector3D.sum(posDivisionCentre,smallerDivisionSpanVector),	// end point
					smallerCylinderRadius,	// radius
					colour,
					rulerDivisions,
					getStudio()
					);
				
		
		EditableText label = new EditableText(	
				"label #"+i,// description, 
				String.format(decimalPlaces, positionScalar),// text,
				Vector3D.sum(posDivisionCentre, textverticalDisplacement, positiveDirection.getWithLength(-textHeight/2.5)),// bottomLeftCorner,
				positiveDirection.getNormalised(),// rightDirection,
				divisionSpanVector.getNormalised(),// upDirection,
				25,//(int) (3000*divisionLengthScalar),// fontSize,
				"Arial",// fontFamily,
				textHeight,// height,
				colour, //textSurfaceProperty,
				rulerDivisions,
				getStudio()
				);
			
			if(!onlyPositive && i !=0) {
				negDivisionCentre = Vector3D.sum(centre,positiveDirection.getWithLength(-(double)i/nDivisions));
				ParametrisedCylinder negativeDivision = new ParametrisedCylinder(
						"division #"+(-i),
						Vector3D.sum(negDivisionCentre,smallerDivisionSpanVector.getProductWith(-1)),	// start point The if statement is used to make all the interger lines a bit longer..
						Vector3D.sum(negDivisionCentre,smallerDivisionSpanVector),	// start point
						smallerCylinderRadius,	// radius
						colour,
						rulerDivisions,
						getStudio()
						);
				
				EditableText negativeLabel = new EditableText(			
						"label #"+(-i),// description, 
						String.format(decimalPlaces, positionScalar),// text,
						Vector3D.sum(negDivisionCentre, textverticalDisplacement, positiveDirection.getWithLength(-textHeight/2.5)),// bottomLeftCorner,
						positiveDirection.getNormalised(),// rightDirection,
						divisionSpanVector.getNormalised(),// upDirection,
						25,//(int) (3000*divisionLengthScalar),// fontSize,
						"Arial",// fontFamily,
						textHeight,// height,
						colour, //textSurfaceProperty,
						rulerDivisions,
						getStudio()
						);
				rulerDivisions.addSceneObject(negativeDivision);
				rulerDivisions.addSceneObject(negativeLabel);
			}
			
			
			rulerDivisions.addSceneObject(division);
			rulerDivisions.addSceneObject(label);
			
			addSceneObject(rulerDivisions);
		}
		}			
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cylinder lattice"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		maxLengthPanel = new LabelledDoublePanel("Max length");
		editPanel.add(maxLengthPanel, "wrap");
		
		
		cylinderRadiusPanel= new LabelledDoublePanel("Cylinder radius");
		editPanel.add(cylinderRadiusPanel, "wrap"); 
		
		nDivisionsPanel = new LabelledDoublePanel("number of divisions");
		editPanel.add(nDivisionsPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("origin");
		editPanel.add(centrePanel, "wrap"); 	
		
		positiveDirectionPanel = new LabelledVector3DPanel("positive direction");
		editPanel.add(positiveDirectionPanel, "wrap"); //"wrap"
		
		divisionSpanVectorPanel = new LabelledVector3DPanel("division up span vector");
		editPanel.add(divisionSpanVectorPanel, "wrap"); //"wrap" 

		// the shadow throwing option
		shadowThrowingCheckBox = new JCheckBox("Shadow throwing");
		editPanel.add(shadowThrowingCheckBox, "wrap");
		
		// the convert button
		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

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
		maxLengthPanel.setNumber(getMaxLength());
		nDivisionsPanel.setNumber(getnDivisions());
		cylinderRadiusPanel.setNumber(getCylinderRadius());
		positiveDirectionPanel.setVector3D(getPositiveDirection());
		divisionSpanVectorPanel.setVector3D(getDivisionSpanVector());
		onlyPositiveCheckBox.setSelected(isOnlyPositive());
		centrePanel.setVector3D(getCentre());
		shadowThrowingCheckBox.setSelected(isShadowThrowing());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRuler acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();

		setMaxLength(maxLengthPanel.getNumber());
		setnDivisions(nDivisionsPanel.getNumber());
		setPositiveDirection(positiveDirectionPanel.getVector3D());
		setCylinderRadius(cylinderRadiusPanel.getNumber());
		setDivisionSpanVector(divisionSpanVectorPanel.getVector3D());
		setOnlyPositive(onlyPositiveCheckBox.isSelected());
		setCentre(centrePanel.getVector3D());
		setShadowThrowing(shadowThrowingCheckBox.isSelected());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the cylinders
		addCylinders();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-ruler");
		container.setValuesInEditPanel();
	}
}
