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
import optics.raytrace.surfaces.RayRotatingAboutArbitraryAxisDirection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

/**
 * A ray-rotation-sheet star, i.e. <numberOfRayRotationSheets> ray-rotation sheets, each rotating <focalLength>
 * and centred at position <centre> (i.e. this is where the nodal point is),
 * which intersect along a line with direction <intersectionDirection>
 * 
 * @author johannes
 */
public class EditableRayRotationSheetStar extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 4682546071566780L;

	//
	// parameters
	//
	
	/**
	 * The number of ray-rotation sheets that form this star.
	 * This number cannot be negative.
	 */
	private int numberOfRayRotationSheets;
	
	/**
	 * Show all the ray-rotation sheets in the star?  If true, yes.
	 * If no, show only <numberOfShownRayRotationSheets> sheets, starting with index <shownRayRotationSheetsStartIndex>.
	 */
	private boolean showAllRayRotationSheets;
	
	/**
	 * If <showAllRayRotationSheets> is false, show this number of ray-rotation sheets, starting with the one with
	 * index <shownRayRotationSheetsStartIndex>.
	 * If the value is negative, show all ray-rotation sheets.
	 */
	private int numberOfShownRayRotationSheets;
	
	/**
	 * If <showAllRayRotationSheets> is false, show <numberOfShownRayRotationSheets> ray-rotation sheets, starting with the one with
	 * this index.
	 */
	private int shownRayRotationSheetsStartIndex;
	
	/**
	 * Ray-rotation angle of each sheet, in radians
	 */
	private double rayRotationAngle;

	/**
	 * Direction of the axis around which each ray-rotation sheet rotates the light-ray direction.
	 */
	private Vector3D rotationAxisDirection;

	/**
	 * Centre of the ray-rotation-sheet star.
	 * The sheets intersect along a line through this position.
	 */
	private Vector3D centre;
	
	/**
	 * Direction of the line through the centre of the ray-rotation-sheet star along which the sheets intersect.
	 */
	private Vector3D intersectionDirection;
	
	/**
	 * A point on one of the ray-rotation sheets.
	 * This determines the absolute rotation angle of the star around the intersection line.
	 */
	private Vector3D pointOnRayRotationSheet1;
	
	/**
	 * Radius of the star.
	 * Each of the ray-rotation sheets is rectangular, with one side of the rectangle, of length <starLength>, 
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
	private double rayRotationSheetTransmissionCoefficient;
	
	private boolean showEdges;
	private double edgeRadius;
	private SurfaceProperty edgeSurfaceProperty;

	//
	// internal variables
	//
	
	// containers for the sheets and frames
	private EditableSceneObjectCollection rayRotationSheets, edges;
	

	// GUI panels
	private LabelledIntPanel numberOfRayRotationSheetsPanel, numberOfShownRayRotationSheetsPanel, shownRayRotationSheetsStartIndexPanel;
	private LabelledDoublePanel rotationAnglePanel, starRadiusPanel, starLengthPanel, rayRotationSheetTransmissionCoefficientPanel, edgeRadiusPanel;
	private LabelledVector3DPanel rotationAxisDirectionPanel, centrePanel, intersectionDirectionPanel, pointOnRayRotationSheet1Panel;
	private JCheckBox showEdgesCheckBox, showAllRayRotationSheetsCheckBox;
	private SurfacePropertyPanel edgeSurfacePropertyPanel;
	private JButton convertButton;
	
//	private LabelledDoublePanel frameRadiusLine;
//	private JCheckBox showFramesCheckBox;
//	private SurfacePropertyPanel frameSurfacePropertyPanel;
	
	
	double getRadiusOfRegularPolygonTrajectory()
	{
		return 2*rayRotationAngle*Math.sin(Math.PI/numberOfRayRotationSheets);
	}
	
	static public double getFocalLengthForRadiusOfRegularPolygonTrajectory(int numberOfRayRotationSheets, double radiusOfRegularPolygonTrajectory)
	{
		return 0.5*radiusOfRegularPolygonTrajectory/Math.sin(Math.PI/numberOfRayRotationSheets);
	}
	
	/**
	 * Constructor that allows all parameters to be specified.
	 * @param description
	 * @param numberOfRayRotationSheets
	 * @param showAllRayRotationSheets
	 * @param numberOfShownRayRotationSheets
	 * @param shownRayRotationSheetsStartIndex
	 * @param rotationAngle
	 * @param rotationAxisDirection
	 * @param centre
	 * @param intersectionDirection
	 * @param pointOnRayRotationSheet1
	 * @param starRadius
	 * @param starLength
	 * @param rayRotationSheetTransmissionCoefficient
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableRayRotationSheetStar(
			String description,
			int numberOfRayRotationSheets,
			boolean showAllRayRotationSheets,
			int numberOfShownRayRotationSheets,
			int shownRayRotationSheetsStartIndex,
			double rotationAngle,
			Vector3D rotationAxisDirection,
			Vector3D centre,
			Vector3D intersectionDirection,
			Vector3D pointOnRayRotationSheet1,
			double starRadius,
			double starLength,
			double rayRotationSheetTransmissionCoefficient,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			SceneObject parent, 
			Studio studio
		)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setNumberOfRayRotationSheets(numberOfRayRotationSheets);
		setShowAllRayRotationSheets(showAllRayRotationSheets);
		setNumberOfShownRayRotationSheets(numberOfShownRayRotationSheets);
		setShownRayRotationSheetsStartIndex(shownRayRotationSheetsStartIndex);
		setRotationAngle(rotationAngle);
		setRotationAxisDirection(rotationAxisDirection);
		setCentre(centre);
		setIntersectionDirection(intersectionDirection);
		setPointOnRayRotationSheet1(pointOnRayRotationSheet1);
		setStarRadius(starRadius);
		setStarLength(starLength);
		setRayRotationSheetTransmissionCoefficient(rayRotationSheetTransmissionCoefficient);
		setShowEdges(showEdges);
		setEdgeRadius(edgeRadius);
		setEdgeSurfaceProperty(edgeSurfaceProperty);

		populateSceneObjectCollection();
	}

	public EditableRayRotationSheetStar(SceneObject parent, Studio studio)
	{

		this(
				"RayRotationSheet star",	// description
				5,	// numberOfRayRotationSheets
				true,	// showAllRayRotationSheets
				-1,	// numberOfShownRayRotationSheets
				0,	// shownRayRotationSheetsStartIndex
				1.0,	// rotationAngle
				new Vector3D(0, 1, 0),	// rotationAxisDirection
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				new Vector3D(1, 0, 10),	// pointOnRayRotationSheet1
				2,	// starRadius,
				1,	// starLength,
				0.96,	// rayRotationSheetTransmissionCoefficient
				false,	// showFrames,
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
	public EditableRayRotationSheetStar(EditableRayRotationSheetStar original)
	{
		this(
			original.getDescription(),
			original.getNumberOfRayRotationSheets(),
			original.isShowAllRayRotationSheets(),
			original.getNumberOfShownRayRotationSheets(),
			original.getShownRayRotationSheetsStartIndex(),
			original.getRotationAngle(),
			original.getRotationAxisDirection(),
			original.getCentre(),
			original.getIntersectionDirection().clone(),
			original.getPointOnRayRotationSheet1().clone(),
			original.getStarRadius(),
			original.getStarLength(),
			original.getRayRotationSheetTransmissionCoefficient(),
			original.isShowEdges(),
			original.getEdgeRadius(),
			original.getEdgeSurfaceProperty().clone(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableRayRotationSheetStar clone()
	{
		return new EditableRayRotationSheetStar(this);
	}

	
	//
	// setters and getters
	//
	
	public int getNumberOfRayRotationSheets() {
		return numberOfRayRotationSheets;
	}

	public void setNumberOfRayRotationSheets(int numberOfRayRotationSheets) {
		this.numberOfRayRotationSheets = numberOfRayRotationSheets;
	}

	public boolean isShowAllRayRotationSheets() {
		return showAllRayRotationSheets;
	}

	public void setShowAllRayRotationSheets(boolean showAllRayRotationSheets) {
		this.showAllRayRotationSheets = showAllRayRotationSheets;
	}

	public int getShownRayRotationSheetsStartIndex() {
		return shownRayRotationSheetsStartIndex;
	}

	public void setShownRayRotationSheetsStartIndex(int shownRayRotationSheetsStartIndex) {
		this.shownRayRotationSheetsStartIndex = shownRayRotationSheetsStartIndex;
	}

	public int getNumberOfShownRayRotationSheets() {
		return numberOfShownRayRotationSheets;
	}

	public void setNumberOfShownRayRotationSheets(int numberOfShownRayRotationSheets) {
		this.numberOfShownRayRotationSheets = numberOfShownRayRotationSheets;
	}

	public double getRotationAngle() {
		return rayRotationAngle;
	}

	/**
	 * @param rayRotationAngle	in radians
	 */
	public void setRotationAngle(double rayRotationAngle) {
		this.rayRotationAngle = rayRotationAngle;
	}

	public Vector3D getRotationAxisDirection() {
		return rotationAxisDirection;
	}

	public void setRotationAxisDirection(Vector3D rotationAxisDirection) {
		this.rotationAxisDirection = rotationAxisDirection;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getIntersectionDirection() {
		return intersectionDirection;
	}

	public void setIntersectionDirection(Vector3D intersectionDirection) {
		this.intersectionDirection = intersectionDirection;
	}

	public Vector3D getPointOnRayRotationSheet1() {
		return pointOnRayRotationSheet1;
	}

	public void setPointOnRayRotationSheet1(Vector3D pointOnRayRotationSheet1) {
		this.pointOnRayRotationSheet1 = pointOnRayRotationSheet1;
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

	public double getRayRotationSheetTransmissionCoefficient() {
		return rayRotationSheetTransmissionCoefficient;
	}

	public void setRayRotationSheetTransmissionCoefficient(double rayRotationSheetTransmissionCoefficient) {
		this.rayRotationSheetTransmissionCoefficient = rayRotationSheetTransmissionCoefficient;
	}
	
	public boolean isShowEdges() {
		return showEdges;
	}

	public void setShowEdges(boolean showEdges) {
		this.showEdges = showEdges;
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



	
	//
	// pre-calculated directions
	//

	/**
	 * normalised radial vector in the plane of ray-rotation sheet 1
	 */
	private Vector3D u;
	
	/**
	 * normalised radial vector perpendicular to the plane of ray-rotation sheet 1
	 */
	private Vector3D v;
	
	/**
	 * normalised vector in the direction of the intersection line
	 */
	private Vector3D w;

	private void calculateDirections()
	{
		// first set w to the direction of the intersection line
		w = intersectionDirection.getNormalised();
		
		// calculate u by taking a vector from the centre to <pointOnRayRotationSheet1>, take the part of this vector
		// that is perpendicular to w, and normalise it
		u = Vector3D.difference(pointOnRayRotationSheet1, centre).getPartPerpendicularTo(w).getNormalised();
		
		// v = w x u; u, v, and w then form a right-handed Cartesian coordinate system
		v = Vector3D.crossProduct(w, u);
	}

	
	private void populateRayRotationSheetsAndEdges()
	{
		Vector3D
			axialSpanVector = w.getProductWith(starLength),
			topCorner = Vector3D.sum(centre, w.getProductWith(-starLength/2));

		for(int i=0; i<numberOfRayRotationSheets; i++)
		{
			double phi = i*2.*Math.PI/numberOfRayRotationSheets;
			double cos = Math.cos(phi);
			double sin = Math.sin(phi);
			
			Vector3D
				radialUnitVector = Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin));
			
			int
				shownRayRotationSheetsStartIndexInRange = Math.floorMod(shownRayRotationSheetsStartIndex, numberOfRayRotationSheets),
				shownRayRotationSheetsEndIndexInRange = Math.floorMod(
						shownRayRotationSheetsStartIndex + Math.min(numberOfShownRayRotationSheets, numberOfRayRotationSheets),
						numberOfRayRotationSheets
					);
			boolean
				show = (showAllRayRotationSheets?true:
					(shownRayRotationSheetsStartIndexInRange < shownRayRotationSheetsEndIndexInRange)?
					((i >= shownRayRotationSheetsStartIndexInRange) && (i < shownRayRotationSheetsEndIndexInRange)):
					((i >= shownRayRotationSheetsStartIndexInRange) || (i < shownRayRotationSheetsEndIndexInRange))
				);
						
			rayRotationSheets.addSceneObject(
					new EditableScaledParametrisedParallelogram(
							"ray-rotation sheet "+(i+1),	// description
							topCorner,	// corner 
							axialSpanVector,	// spanVector1
							radialUnitVector.getProductWith(starRadius),	// spanVector2
							new RayRotatingAboutArbitraryAxisDirection(
									rayRotationAngle,	// outwardsRotationAngle
									rotationAxisDirection.getNormalised(),	// rotationAxisUnitVector
									GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
									rayRotationSheetTransmissionCoefficient,	// transmissionCoefficient
									true	// shadowThrowing
								),	// surfaceProperty
							rayRotationSheets,	// parent
							getStudio()
						),
					show	// visible
				);
			
			edges.addSceneObject(
					new EditableParametrisedCylinder(
							"Top edge of ray-rotation sheet #"+(i+1),
							topCorner,	// start point
							Vector3D.sum(
									topCorner,
									radialUnitVector.getWithLength(starRadius)
								),	// end point
							edgeRadius,	// radius
							edgeSurfaceProperty,
							edges,
							getStudio()
						),
					show	// visible
				);

			edges.addSceneObject(
					new EditableParametrisedCylinder(
							"Outer edge of ray-rotation sheet #"+(i+1),
							Vector3D.sum(
									topCorner,
									radialUnitVector.getWithLength(starRadius)
								),	// start point
							Vector3D.sum(
									topCorner,
									radialUnitVector.getWithLength(starRadius),
									axialSpanVector
								),	// end point
							edgeRadius,	// radius
							edgeSurfaceProperty,
							edges,
							getStudio()
						),
					show	// visible
				);

			edges.addSceneObject(
					new EditableParametrisedCylinder(
							"Bottom edge of ray-rotation sheet #"+(i+1),
							Vector3D.sum(
									topCorner,
									radialUnitVector.getWithLength(starRadius),
									axialSpanVector
								),	// start point
							Vector3D.sum(
									topCorner,
									axialSpanVector
								),	// end point
							edgeRadius,	// radius
							edgeSurfaceProperty,
							edges,
							getStudio()
						),
					show	// visible
				);

		}
		
		// add central edge
		edges.addSceneObject(
				new EditableParametrisedCylinder(
						"Axial edge (common to all ray-rotation sheets)",
						topCorner,	// start point
						Vector3D.sum(
								topCorner,
								axialSpanVector
							),	// end point
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
		
		// prepare scene-object collection objects for the ray-rotation sheets...
		rayRotationSheets = new EditableSceneObjectCollection("Ray-rotation sheets", true, this, getStudio());
		
		// ... and the edges
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// populate these collections
		calculateDirections();
		populateRayRotationSheetsAndEdges();
		
		// add the windows and the edges to this collection
		addSceneObject(rayRotationSheets);
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
		
		numberOfRayRotationSheetsPanel = new LabelledIntPanel("Number of ray-rotation sheets");
		basicParametersPanel.add(numberOfRayRotationSheetsPanel, "wrap");
		
		showAllRayRotationSheetsCheckBox = new JCheckBox("Show all ray-rotation sheets.");
		showAllRayRotationSheetsCheckBox.addActionListener(this);
		basicParametersPanel.add(showAllRayRotationSheetsCheckBox, "split 3");
		
		numberOfShownRayRotationSheetsPanel = new LabelledIntPanel("If not, show only");
		basicParametersPanel.add(numberOfShownRayRotationSheetsPanel);

		shownRayRotationSheetsStartIndexPanel = new LabelledIntPanel("ray-rotation sheets, starting with index");
		basicParametersPanel.add(shownRayRotationSheetsStartIndexPanel, "wrap");

		rotationAnglePanel = new LabelledDoublePanel("Rotation angle of each ray-rotation sheet (in degrees)");
		basicParametersPanel.add(rotationAnglePanel, "wrap");
		
		rotationAxisDirectionPanel = new LabelledVector3DPanel("Direction of axis around which each sheet rotates ray direction");
		basicParametersPanel.add(rotationAxisDirectionPanel, "wrap");
		
		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");

		intersectionDirectionPanel = new LabelledVector3DPanel("Direction of intersection line");
		basicParametersPanel.add(intersectionDirectionPanel, "wrap");

		pointOnRayRotationSheet1Panel = new LabelledVector3DPanel("Point on first ray-rotation sheet");
		basicParametersPanel.add(pointOnRayRotationSheet1Panel, "wrap");

		starRadiusPanel = new LabelledDoublePanel("Star radius");
		basicParametersPanel.add(starRadiusPanel, "wrap");

		starLengthPanel = new LabelledDoublePanel("Star length");
		basicParametersPanel.add(starLengthPanel, "wrap");

		rayRotationSheetTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each ray-rotation sheet");
		basicParametersPanel.add(rayRotationSheetTransmissionCoefficientPanel, "wrap");
		
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
		numberOfRayRotationSheetsPanel.setNumber(numberOfRayRotationSheets);
		showAllRayRotationSheetsCheckBox.setSelected(showAllRayRotationSheets);
		numberOfShownRayRotationSheetsPanel.setEnabled(!showAllRayRotationSheets);
		shownRayRotationSheetsStartIndexPanel.setEnabled(!showAllRayRotationSheets);
		numberOfShownRayRotationSheetsPanel.setNumber(numberOfShownRayRotationSheets);
		shownRayRotationSheetsStartIndexPanel.setNumber(shownRayRotationSheetsStartIndex);
		rotationAnglePanel.setNumber(MyMath.rad2deg(rayRotationAngle));
		rotationAxisDirectionPanel.setVector3D(rotationAxisDirection);
		centrePanel.setVector3D(centre);
		intersectionDirectionPanel.setVector3D(intersectionDirection);
		pointOnRayRotationSheet1Panel.setVector3D(pointOnRayRotationSheet1);
		starRadiusPanel.setNumber(starRadius);
		starLengthPanel.setNumber(starLength);
		rayRotationSheetTransmissionCoefficientPanel.setNumber(rayRotationSheetTransmissionCoefficient);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableRayRotationSheetStar acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setNumberOfRayRotationSheets(numberOfRayRotationSheetsPanel.getNumber());
		setShowAllRayRotationSheets(showAllRayRotationSheetsCheckBox.isSelected());
		setNumberOfShownRayRotationSheets(numberOfShownRayRotationSheetsPanel.getNumber());
		setShownRayRotationSheetsStartIndex(shownRayRotationSheetsStartIndexPanel.getNumber());
		setRotationAngle(MyMath.deg2rad(rotationAnglePanel.getNumber()));
		setRotationAxisDirection(rotationAxisDirectionPanel.getVector3D());
		setCentre(centrePanel.getVector3D());
		setIntersectionDirection(intersectionDirectionPanel.getVector3D());
		setPointOnRayRotationSheet1(pointOnRayRotationSheet1Panel.getVector3D());
		setStarRadius(starRadiusPanel.getNumber());
		setStarLength(starLengthPanel.getNumber());
		setRayRotationSheetTransmissionCoefficient(rayRotationSheetTransmissionCoefficientPanel.getNumber());
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
		if(e.getSource() == showAllRayRotationSheetsCheckBox)
		{
			numberOfShownRayRotationSheetsPanel.setEnabled(!(showAllRayRotationSheetsCheckBox.isSelected()));
			shownRayRotationSheetsStartIndexPanel.setEnabled(!(showAllRayRotationSheetsCheckBox.isSelected()));
		}
		else if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-ray-rotation sheet-star");
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