package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A negative-space-wedge star 
 * 
 * @author Johannes Courtial, Dimitris Georgantzis
 */
public class EditableNegativeSpaceWedgeStar extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -914999800974846214L;
	
	//
	// parameters
	//
	
	/**
	 * Deficit angle of star, in degrees
	 */
	private double deficitAngleDeg;
	
	/**
	 * Number of negative-space wedges in star
	 */
	private int numberOfNegativeSpaceWedges;
	
	/**
	 * Show all the ray-rotation sheets in the star?  If true, yes.
	 * If no, show only <numberOfNegativeSpaceWedges> wedges, starting with index <shownRayRotationSheetsStartIndex>.
	 */
	private boolean showAllNegativeSpaceWedges;
	
	/**
	 * If <showAllNegativeSpaceWedges> is false, show this number of negative-space wedges, starting with the one with
	 * index <shownNegativeSpaceWedgesStartIndex>.
	 * If the value is negative, show all negative-space wedges.
	 */
	private int numberOfShownNegativeSpaceWedges;
	
	/**
	 * If <showAllNegativeSpaceWedges> is false, show <numberOfShownNegativeSpaceWedges> negative-space wedges, starting with the one with
	 * this index.
	 */
	private int shownNegativeSpaceWedgesStartIndex;
	
	/**
	 * Centre of the negative-space-wedge star.
	 * The wedges intersect along a line through this position.
	 */
	private Vector3D centre;
	
	/**
	 * Direction of the line through the centre of the negative-space-wedge star along which the wedges intersect.
	 */
	private Vector3D axisDirection;
	
	/**
	 * Direction to the centre of negative-space wedge #0.
	 * This determines the absolute rotation angle of the star around the star's axis.
	 */
	private Vector3D directionOfCentreOfWedge0;
	
	/**
	 * Radius of the star.
	 * Each of the wedges is rectangular, with one side of the rectangle, of length <starLength>, 
	 * coinciding with the intersection line and the opposite side lying on the surface of a cylinder
	 * of radius <starRadius> whose axis coincides with the intersection line.
	 */
	private double starRadius;
	
	/**
	 * Length of the star.
	 * Each of the ray-rotation sheets is rectangular, with one side of the rectangle, of length <starLength>, 
	 * coinciding with the intersection line and the opposite side lying on the surface of a cylinder
	 * of radius <starRadius> whose axis coincides with the intersection line.
	 */
	private double starLength;

	/**
	 * Transmission coefficient of each ray-rotation sheets.
	 */
	private double sheetTransmissionCoefficient;

	
	/**
	 * If true, show edges of negative-space wedges in star, otherwise don't
	 */
	private boolean showEdges;
	
	/**
	 * radius of the edges (if shown)
	 */
	private double edgeRadius;
	
	/**
	 * surface property of the edges (if shown)
	 */
	private SurfaceProperty edgeSurfaceProperty;


	
//	double deficitAngleDeg;
//	int numberOfNegativeSpaceWedges;
//	boolean showAllNegativeSpaceWedges;
//	int numberOfShownNegativeSpaceWedges;
//	int shownNegativeSpaceWedgesStartIndex;
//	Vector3D centre;
//	Vector3D axisDirection;
//	Vector3D directionOfCentreOfWedge0;
//	double starRadius;
//	double starLength;
//	double sheetTransmissionCoefficient;
//	boolean showEdges;
//	double edgeRadius;
//	SurfaceProperty edgeSurfaceProperty;


	//
	// internal variables
	//
	
	// containers for the sheets and frames
	private EditableSceneObjectCollection negativeSpaceWedges, edges;
	

	// GUI panels
	private LabelledDoublePanel deficitAngleDegPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JCheckBox showAllNegativeSpaceWedgesCheckBox;
	private LabelledIntPanel numberOfShownNegativeSpaceWedgesPanel;
	private LabelledIntPanel shownNegativeSpaceWedgesStartIndexPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledVector3DPanel axisDirectionPanel;
	private LabelledVector3DPanel directionOfCentreOfWedge0Panel;
	private LabelledDoublePanel starRadiusPanel;
	private LabelledDoublePanel starLengthPanel;
	private LabelledDoublePanel sheetTransmissionCoefficientPanel;
	private JCheckBox showEdgesCheckBox;
	private LabelledDoublePanel edgeRadiusPanel;
	private SurfacePropertyPanel edgeSurfacePropertyPanel;
	
	private JButton convertButton;
	
	
		
	/**
	 * @param description
	 * @param deficitAngleDeg
	 * @param numberOfNegativeSpaceWedges
	 * @param showAllNegativeSpaceWedges
	 * @param numberOfShownNegativeSpaceWedges
	 * @param shownNegativeSpaceWedgesStartIndex
	 * @param centre
	 * @param axisDirection
	 * @param directionOfCentreOfWedge0
	 * @param starRadius
	 * @param starLength
	 * @param sheetTransmissionCoefficient
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableNegativeSpaceWedgeStar(
			String description,
			double deficitAngleDeg,
			int numberOfNegativeSpaceWedges,
			boolean showAllNegativeSpaceWedges,
			int numberOfShownNegativeSpaceWedges,
			int shownNegativeSpaceWedgesStartIndex,
			Vector3D centre,
			Vector3D axisDirection,
			Vector3D directionOfCentreOfWedge0,
			double starRadius,
			double starLength,
			double sheetTransmissionCoefficient,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, true, parent, studio);
		
		this.deficitAngleDeg = deficitAngleDeg;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		this.showAllNegativeSpaceWedges = showAllNegativeSpaceWedges;
		this.numberOfShownNegativeSpaceWedges = numberOfShownNegativeSpaceWedges;
		this.shownNegativeSpaceWedgesStartIndex = shownNegativeSpaceWedgesStartIndex;
		this.centre = centre;
		this.axisDirection = axisDirection;
		this.directionOfCentreOfWedge0 = directionOfCentreOfWedge0;
		this.starRadius = starRadius;
		this.starLength = starLength;
		this.sheetTransmissionCoefficient = sheetTransmissionCoefficient;
		this.showEdges = showEdges;
		this.edgeRadius = edgeRadius;
		this.edgeSurfaceProperty = edgeSurfaceProperty;

		populateSceneObjectCollection();
	}

	/**
	 * Create a default negative-space-wedge star
	 * @param parent
	 * @param studio
	 */
	public EditableNegativeSpaceWedgeStar(SceneObject parent, Studio studio)
	{
		this(
				"Nagative-refraction-wedge star",	// description
				360.-20.,	// deficitAngleDeg
				100,	// numberOfNegativeSpaceWedges
				true,	// showAllNegativeSpaceWedges
				-1,	// numberOfShownNegativeSpaceWedges
				0,	// shownNegativeSpaceWedgesStartIndex
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 1, 0),	// axisDirection
				new Vector3D(1, 0, 0),	// directionOfCentreOfNegativeSpaceWedge0
				2,	// starRadius
				1,	// starLength
				0.96,	// sheetTransmissionCoefficient
				false,	// showEdges
				0.01,	// edgeRadius
				SurfaceColour.RED_SHINY,	// edgeSurfaceProperty
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableNegativeSpaceWedgeStar(EditableNegativeSpaceWedgeStar original)
	{
		this(
			original.getDescription(),
			original.getDeficitAngleDeg(),
			original.getNumberOfNegativeSpaceWedges(),
			original.isShowAllNegativeSpaceWedges(),
			original.getNumberOfShownNegativeSpaceWedges(),
			original.getShownNegativeSpaceWedgesStartIndex(),
			original.getCentre(),
			original.getAxisDirection(),
			original.getDirectionOfCentreOfWedge0(),
			original.getStarRadius(),
			original.getStarLength(),
			original.getSheetTransmissionCoefficient(),
			original.isShowEdges(),
			original.getEdgeRadius(),
			original.getEdgeSurfaceProperty().clone(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableNegativeSpaceWedgeStar clone()
	{
		return new EditableNegativeSpaceWedgeStar(this);
	}

	
	//
	// setters and getters
	//
	
	public double getDeficitAngleDeg() {
		return deficitAngleDeg;
	}

	public void setDeficitAngleDeg(double deficitAngleDeg) {
		this.deficitAngleDeg = deficitAngleDeg;
	}

	public int getNumberOfNegativeSpaceWedges() {
		return numberOfNegativeSpaceWedges;
	}

	public void setNumberOfNegativeSpaceWedges(int numberOfNegativeSpaceWedges) {
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
	}

	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
	}

	public boolean isShowAllNegativeSpaceWedges() {
		return showAllNegativeSpaceWedges;
	}

	public void setShowAllNegativeSpaceWedges(boolean showAllNegativeSpaceWedges) {
		this.showAllNegativeSpaceWedges = showAllNegativeSpaceWedges;
	}

	public int getNumberOfShownNegativeSpaceWedges() {
		return numberOfShownNegativeSpaceWedges;
	}

	public void setNumberOfShownNegativeSpaceWedges(int numberOfShownNegativeSpaceWedges) {
		this.numberOfShownNegativeSpaceWedges = numberOfShownNegativeSpaceWedges;
	}

	public int getShownNegativeSpaceWedgesStartIndex() {
		return shownNegativeSpaceWedgesStartIndex;
	}

	public void setShownNegativeSpaceWedgesStartIndex(int shownNegativeSpaceWedgesStartIndex) {
		this.shownNegativeSpaceWedgesStartIndex = shownNegativeSpaceWedgesStartIndex;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getAxisDirection() {
		return axisDirection;
	}

	public void setAxisDirection(Vector3D axisDirection) {
		this.axisDirection = axisDirection;
	}

	public Vector3D getDirectionOfCentreOfWedge0() {
		return directionOfCentreOfWedge0;
	}

	public void setDirectionOfCentreOfWedge0(Vector3D directionOfCentreOfWedge0) {
		this.directionOfCentreOfWedge0 = directionOfCentreOfWedge0;
	}

	public double getStarRadius() {
		return starRadius;
	}

	public void setStarRadius(double starRadius) {
		this.starRadius = starRadius;
	}

	public double getStarLength() {
		return starLength;
	}

	public void setStarLength(double starLength) {
		this.starLength = starLength;
	}

	public double getSheetTransmissionCoefficient() {
		return sheetTransmissionCoefficient;
	}

	public void setSheetTransmissionCoefficient(double sheetTransmissionCoefficient) {
		this.sheetTransmissionCoefficient = sheetTransmissionCoefficient;
	}

	public double getEdgeRadius() {
		return edgeRadius;
	}

	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}

	public SurfaceProperty getEdgeSurfaceProperty() {
		return edgeSurfaceProperty;
	}

	public void setEdgeSurfaceProperty(SurfaceProperty edgeSurfaceProperty) {
		this.edgeSurfaceProperty = edgeSurfaceProperty;
	}

	
	


	// define coordinate system of star
	private Vector3D a;	// unit vector in the direction of the star axis
	private Vector3D b;	// unit vector in the direction of the centre of wedge #0
	private Vector3D c;	// third unit vector
	
	public Vector3D calculateADirection()
	{
		return axisDirection.getNormalised();
	}
	
	public Vector3D calculateBDirection()
	{
		return directionOfCentreOfWedge0.getPartPerpendicularTo(axisDirection).getNormalised();
	}
	
	public Vector3D calculateCDirection()
	{
		return Vector3D.crossProduct(calculateADirection(), calculateBDirection());
	}

	public void calculateCoordinateAxisDirections()
	{
		// define coordinate system of star
		a = calculateADirection();	// axisDirection.getNormalised();	// unit vector in the direction of the star axis
		b = calculateBDirection();	// directionOfCentreOfWedge0.getPartPerpendicularTo(a).getNormalised();
		c = calculateCDirection();	// Vector3D.crossProduct(a, b);
	}
	
	private void addNegativeRefractionSheet(String description, double azimuthalAngle, EditableSceneObjectCollection wedge)
	{
		Vector3D axisBottom = Vector3D.sum(centre, a.getProductWith(-0.5*starLength));
		Vector3D axisTop = Vector3D.sum(centre, a.getProductWith(+0.5*starLength));
		Vector3D axialSpanVector = a.getProductWith(starLength);
		Vector3D radialSpanVector = Vector3D.sum(
				b.getProductWith(Math.cos(azimuthalAngle)),
				c.getProductWith(Math.sin(azimuthalAngle))
			).getProductWith(starRadius);

		negativeSpaceWedges.addSceneObject(new EditableScaledParametrisedParallelogram(
				description,
				axisBottom,	// corner 
				axialSpanVector,	// spanVector1
				radialSpanVector,	// spanVector2
				new RayRotating(
						Math.PI,	// rotation angle
						sheetTransmissionCoefficient,	// transmissionCoefficient
						false	// shadowThrowing
				),	// surfaceProperty
				wedge,	// parent
				getStudio()
			));

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Top edge of "+description,
						axisTop,	// start point
						Vector3D.sum(axisTop, radialSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Outer edge of "+description,
						Vector3D.sum(axisTop, radialSpanVector),	// start point
						Vector3D.sum(axisBottom, radialSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);

		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Bottom edge of "+description,
						axisBottom,	// start point
						Vector3D.sum(axisBottom, radialSpanVector),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
	}

	private void populateCollections()
	{
		// calculate the angle of each wedge
		// each wedge needs to rotate by <deficitAngle> / <numberOfNegativeSpaceWedges>, which it does by
		// having a wedge angle that is half of that (in radians)
		double wedgeAngle = MyMath.deg2rad(deficitAngleDeg) / (2*numberOfNegativeSpaceWedges);
		
		System.out.println("wedge angle = "+MyMath.rad2deg(wedgeAngle)+"°");
		
		// add all the wedges
		for(int i=0; i<numberOfNegativeSpaceWedges; i++)
		{	
			// add wedge #i
			EditableSceneObjectCollection negativeSpaceWedge = new EditableSceneObjectCollection("Negative-space wedge #"+i, true, negativeSpaceWedges, getStudio());
			negativeSpaceWedges.addSceneObject(negativeSpaceWedge);
			
			// first calculate the angle in the (b, c) plane and with respect to the b axis of the line through the centre of the wedge (in radians)
			double wedgeCentreAngle = i*2.0*Math.PI/numberOfNegativeSpaceWedges;
			
			// the two sheets that make up the wedge are then located at an angle +/- wedgeAngle/2 on either side of the centre line
			
			addNegativeRefractionSheet(
					"First negative-refraction sheet of wedge #"+i,	// description
					wedgeCentreAngle-0.5*wedgeAngle,	// azimuthalAngle
					negativeSpaceWedge	// wedge
				);
			
			addNegativeRefractionSheet(
					"Second negative-refraction sheet of wedge #"+i,	// description
					wedgeCentreAngle+0.5*wedgeAngle,	// azimuthalAngle
					negativeSpaceWedge	// wedge
				);
		}
			
		// add central edge
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Axial edge (common to all wedges)",
						Vector3D.sum(centre, a.getProductWith(-0.5*starLength)),	// start point
						Vector3D.sum(centre, a.getProductWith( 0.5*starLength)),	// end point
						edgeRadius,	// radius
						edgeSurfaceProperty,
						edges,
						getStudio()
					)
			);
	}

	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the negative-space wedges...
		negativeSpaceWedges = new EditableSceneObjectCollection("Negative-space wedges", true, this, getStudio());
		
		// ... and the edges
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// populate these collections
		calculateCoordinateAxisDirections();
		populateCollections();
		
		// add the windows and the edges to this collection
		addSceneObject(negativeSpaceWedges);
		addSceneObject(edges, showEdges);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Star of ray-rotation sheets"));
		editPanel.setLayout(new MigLayout("insets 0"));
		

		//
		// the basic-parameters panel
		// 
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		deficitAngleDegPanel = new LabelledDoublePanel("Deficit angle of the star (in degrees)");
		basicParametersPanel.add(deficitAngleDegPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges");
		basicParametersPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		showAllNegativeSpaceWedgesCheckBox = new JCheckBox("Show all negative-space wedges");
		showAllNegativeSpaceWedgesCheckBox.addActionListener(this);
		basicParametersPanel.add(showAllNegativeSpaceWedgesCheckBox, "split 3");
		
		numberOfShownNegativeSpaceWedgesPanel = new LabelledIntPanel("If not, show only");
		basicParametersPanel.add(numberOfShownNegativeSpaceWedgesPanel);

		shownNegativeSpaceWedgesStartIndexPanel = new LabelledIntPanel("negative-space wedges, starting with #");
		basicParametersPanel.add(shownNegativeSpaceWedgesStartIndexPanel, "wrap");

		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");

		axisDirectionPanel = new LabelledVector3DPanel("Axis direction");
		basicParametersPanel.add(axisDirectionPanel, "wrap");

		directionOfCentreOfWedge0Panel = new LabelledVector3DPanel("Direction to centre of negative-space wedge #0");
		basicParametersPanel.add(directionOfCentreOfWedge0Panel, "wrap");

		starRadiusPanel = new LabelledDoublePanel("Star radius");
		basicParametersPanel.add(starRadiusPanel, "wrap");

		starLengthPanel = new LabelledDoublePanel("Star length");
		basicParametersPanel.add(starLengthPanel, "wrap");

		sheetTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each negative-refraction sheet (2 per wedge)");
		basicParametersPanel.add(sheetTransmissionCoefficientPanel, "wrap");
		
		editPanel.add(basicParametersPanel, "wrap");

		// tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
//		JPanel edgePanel = new JPanel();
//		edgePanel.setLayout(new MigLayout("insets 0"));

		showEdgesCheckBox = new JCheckBox("Show edges");
		basicParametersPanel.add(showEdgesCheckBox, "wrap");
		
		edgeRadiusPanel = new LabelledDoublePanel("edge radius");
		basicParametersPanel.add(edgeRadiusPanel, "wrap");
		
		edgeSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		basicParametersPanel.add(edgeSurfacePropertyPanel, "wrap");
		edgeSurfacePropertyPanel.setIPanel(iPanel);

//		tabbedPane.addTab("Frames", framePanel);
//		
//		editPanel.add(tabbedPane, "wrap");

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
		deficitAngleDegPanel.setNumber(deficitAngleDeg);
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		showAllNegativeSpaceWedgesCheckBox.setSelected(showAllNegativeSpaceWedges);
		numberOfShownNegativeSpaceWedgesPanel.setNumber(numberOfShownNegativeSpaceWedges);
		shownNegativeSpaceWedgesStartIndexPanel.setNumber(shownNegativeSpaceWedgesStartIndex);
		centrePanel.setVector3D(centre);
		axisDirectionPanel.setVector3D(axisDirection);
		directionOfCentreOfWedge0Panel.setVector3D(directionOfCentreOfWedge0);
		starRadiusPanel.setNumber(starRadius);
		starLengthPanel.setNumber(starLength);
		sheetTransmissionCoefficientPanel.setNumber(sheetTransmissionCoefficient);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableNegativeSpaceWedgeStar acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setDeficitAngleDeg(deficitAngleDegPanel.getNumber());
		setNumberOfNegativeSpaceWedges(numberOfNegativeSpaceWedgesPanel.getNumber());
		setShowAllNegativeSpaceWedges(showAllNegativeSpaceWedgesCheckBox.isSelected());
		setNumberOfShownNegativeSpaceWedges(numberOfShownNegativeSpaceWedgesPanel.getNumber());
		setShownNegativeSpaceWedgesStartIndex(shownNegativeSpaceWedgesStartIndexPanel.getNumber());
		setCentre(centrePanel.getVector3D());
		setAxisDirection(axisDirectionPanel.getVector3D());
		setDirectionOfCentreOfWedge0(directionOfCentreOfWedge0Panel.getVector3D());
		setStarRadius(starRadiusPanel.getNumber());
		setStarLength(starLengthPanel.getNumber());
		setSheetTransmissionCoefficient(sheetTransmissionCoefficientPanel.getNumber());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == showAllNegativeSpaceWedgesCheckBox)
		{
			numberOfShownNegativeSpaceWedgesPanel.setEnabled(!(showAllNegativeSpaceWedgesCheckBox.isSelected()));
			shownNegativeSpaceWedgesStartIndexPanel.setEnabled(!(showAllNegativeSpaceWedgesCheckBox.isSelected()));
		}
		else if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-negative-space-wedge star");
			container.setValuesInEditPanel();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				// setEdgeSurfaceProperty((SurfaceProperty)edited);
				// edgeSurfacePropertyPanel.setSurfaceProperty(getEdgeSurfaceProperty());
				edgeSurfacePropertyPanel.setSurfaceProperty((SurfaceProperty)edited);
			}
	}
}