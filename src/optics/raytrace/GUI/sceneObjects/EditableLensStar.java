package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A "star of lenses", i.e. <numberOfLenses> ideal thin lenses, each of focal length <focalLength>
 * and centred at position <centre> (i.e. this is where the nodal point is),
 * which intersect along a line with direction <intersectionDirection>
 * 
 * @author Johannes
 */
/**
 * @author johannes
 *
 */
public class EditableLensStar extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 8246818092635747459L;

	//
	// parameters
	//
	
	/**
	 * The number of lenses that form this star.
	 * This number cannot be negative.
	 */
	private int numberOfLenses;
	
	/**
	 * Show all the lenses in the lens star?  If true, yes.
	 * If no, show a subset of <numberOfShownLenses> lenses, starting with index <shownLensesStartIndex>.
	 */
	private boolean showAllLenses;
	
	/**
	 * If <showAllLenses> is false, show this number of lenses, starting with the one with
	 * index <shownLensesStartIndex>.
	 * If the value is negative, show all lenses.
	 */
	private int numberOfShownLenses;
	
	/**
	 * If <showAllLenses> is false, show <numberOfShownLenses> lenses, starting with the one with
	 * this index.
	 */
	private int shownLensesStartIndex;
	
	/**
	 * Focal length of each of the lenses.
	 */
	private double focalLength;

	/**
	 * Distance of the lens's principal point from the line through the centre along which the lenses intersect.
	 */
	private double principalPointDistance;

	/**
	 * Centre of the lens star.
	 * The lenses intersect along a line through this position.
	 * The nodal point (= principal point) of each lens coincides with this position.
	 */
	private Vector3D centre;
	
	/**
	 * Direction of the line through the centre of the lens star along which the lenses intersect.
	 */
	private Vector3D intersectionDirection;
	
	/**
	 * A point on one of the lenses.
	 * This determines the absolute rotation angle of the star around the intersection line.
	 */
	private Vector3D pointOnLens1;
	
	/**
	 * Radius of the star.
	 * Each of the lenses is rectangular, with one side of the rectangle, of length <starLength>, 
	 * coinciding with the intersection line and the opposite side lying on the surface of a cylinder
	 * of radius <starRadius> whose axis coincides with the intersection line.
	 */
	private double starRadius;
	
	/**
	 * Length of the star.
	 * Each of the lenses is rectangular, with one side of the rectangle, of length <starLength>, 
	 * coinciding with the intersection line and the opposite side lying on the surface of a cylinder
	 * of radius <starRadius> whose axis coincides with the intersection line.
	 */
	private double starLength;
	
	private ThinLensType thinLensType;
	
	/**
	 * if thinLensType = phaseHologram and designPosition != null, optimise the phase hologram for light rays that pass, before passing through lens #0,
	 * through the designPosition
	 */
	private Vector3D designPosition;

	/**
	 * Transmission coefficient of each lens.
	 */
	private double lensTransmissionCoefficient;
	
	/**
	 * True if lenses show a shadow, false if they don't
	 */
	private boolean lensesShadowThrowing;
	
	private boolean showEdges;
	private double edgeRadius;
	private SurfaceProperty edgeSurfaceProperty;

	//
	// internal variables
	//
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection lenses, edges;
	

	double getRadiusOfRegularPolygonTrajectory()
	{
		return 2*focalLength*Math.sin(Math.PI/numberOfLenses);
	}
	
	static public double getFocalLengthForRadiusOfRegularPolygonTrajectory(int numberOfLenses, double radiusOfRegularPolygonTrajectory)
	{
		return 0.5*radiusOfRegularPolygonTrajectory/Math.sin(Math.PI/numberOfLenses);
	}
	
	/**
	 * @param description
	 * @param numberOfLenses
	 * @param showAllLenses
	 * @param numberOfShownLenses
	 * @param shownLensesStartIndex
	 * @param focalLength
	 * @param principalPointDistance
	 * @param centre
	 * @param intersectionDirection
	 * @param pointOnLens1
	 * @param starRadius
	 * @param starLength
	 * @param lensTransmissionCoefficient
	 * @param lensesShadowThrowing
	 * @param thinLensType
	 * @param designPosition
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableLensStar(
			String description,
			int numberOfLenses,
			boolean showAllLenses,
			int numberOfShownLenses,
			int shownLensesStartIndex,
			double focalLength,
			double principalPointDistance,
			Vector3D centre,
			Vector3D intersectionDirection,
			Vector3D pointOnLens1,
			double starRadius,
			double starLength,
			double lensTransmissionCoefficient,
			boolean lensesShadowThrowing,
			ThinLensType thinLensType,
			Vector3D designPosition,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			SceneObject parent, 
			Studio studio
			)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setNumberOfLenses(numberOfLenses);
		setShowAllLenses(showAllLenses);
		setNumberOfShownLenses(numberOfShownLenses);
		setShownLensesStartIndex(shownLensesStartIndex);
		setFocalLength(focalLength);
		setPrincipalPointDistance(principalPointDistance);
		setCentre(centre);
		setIntersectionDirection(intersectionDirection);
		setPointOnLens1(pointOnLens1);
		setStarRadius(starRadius);
		setStarLength(starLength);
		setLensTransmissionCoefficient(lensTransmissionCoefficient);
		setLensesShadowThrowing(lensesShadowThrowing);
		setThinLensType(thinLensType);
		setShowEdges(showEdges);
		setEdgeRadius(edgeRadius);
		setEdgeSurfaceProperty(edgeSurfaceProperty);
		setDesignPosition(designPosition);

		populateSceneObjectCollection();
	}

	/**
	 * Constructor that allows all parameters to be specified.
	 * @param description
	 * @param numberOfLenses
	 * @param showAllLenses
	 * @param numberOfShownLenses
	 * @param shownLensesStartIndex
	 * @param focalLength
	 * @param principalPointDistance
	 * @param centre
	 * @param intersectionDirection
	 * @param pointOnLens1
	 * @param starRadius
	 * @param starLength
	 * @param lensTransmissionCoefficient
	 * @param showEdges
	 * @param edgeRadius
	 * @param edgeSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableLensStar(
			String description,
			int numberOfLenses,
			boolean showAllLenses,
			int numberOfShownLenses,
			int shownLensesStartIndex,
			double focalLength,
			double principalPointDistance,
			Vector3D centre,
			Vector3D intersectionDirection,
			Vector3D pointOnLens1,
			double starRadius,
			double starLength,
			double lensTransmissionCoefficient,
			boolean lensesShadowThrowing,
			ThinLensType thinLensType,
			boolean showEdges,
			double edgeRadius,
			SurfaceProperty edgeSurfaceProperty,
			SceneObject parent, 
			Studio studio
			)
	{
		this(
				description,
				numberOfLenses,
				showAllLenses,
				numberOfShownLenses,
				shownLensesStartIndex,
				focalLength,
				principalPointDistance,
				centre,
				intersectionDirection,
				pointOnLens1,
				starRadius,
				starLength,
				lensTransmissionCoefficient,
				lensesShadowThrowing,
				thinLensType,
				null,	// designPosition
				showEdges,
				edgeRadius,
				edgeSurfaceProperty,
				parent,
				studio
			);
		
//		// constructor of superclass
//		super(description, true, parent, studio);
//		
//		setNumberOfLenses(numberOfLenses);
//		setShowAllLenses(showAllLenses);
//		setNumberOfShownLenses(numberOfShownLenses);
//		setShownLensesStartIndex(shownLensesStartIndex);
//		setFocalLength(focalLength);
//		setPrincipalPointDistance(principalPointDistance);
//		setCentre(centre);
//		setIntersectionDirection(intersectionDirection);
//		setPointOnLens1(pointOnLens1);
//		setStarRadius(starRadius);
//		setStarLength(starLength);
//		setLensTransmissionCoefficient(lensTransmissionCoefficient);
//		setLensesShadowThrowing(lensesShadowThrowing);
//		setThinLensType(thinLensType);
//		setShowEdges(showEdges);
//		setEdgeRadius(edgeRadius);
//		setEdgeSurfaceProperty(edgeSurfaceProperty);
//		setDesignPosition(null);
//
//		populateSceneObjectCollection();
	}

	public EditableLensStar(SceneObject parent, Studio studio)
	{

		this(
				"Lens star",	// description
				5,	// numberOfLenses
				true,	// showAllLenses
				-1,	// numberOfShownLenses
				0,	// shownLensesStartIndex
				1.0,	// focalLength
				0,	// principalPointDistance
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 1, 0),	// intersectionDirection
				new Vector3D(1, 0, 10),	// pointOnLens1
				2,	// starRadius,
				1,	// starLength,
				0.96,	// lensTransmissionCoefficient
				false,	// lensesShadowThrowing
				ThinLensType.IDEAL_THIN_LENS,	// thinLensType
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
	public EditableLensStar(EditableLensStar original)
	{
		this(
			original.getDescription(),
			original.getNumberOfLenses(),
			original.isShowAllLenses(),
			original.getNumberOfShownLenses(),
			original.getShownLensesStartIndex(),
			original.getFocalLength(),
			original.getPrincipalPointDistance(),
			original.getCentre(),
			original.getIntersectionDirection().clone(),
			original.getPointOnLens1().clone(),
			original.getStarRadius(),
			original.getStarLength(),
			original.getLensTransmissionCoefficient(),
			original.isLensesShadowThrowing(),
			original.getThinLensType(),
			original.isShowEdges(),
			original.getEdgeRadius(),
			original.getEdgeSurfaceProperty().clone(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableLensStar clone()
	{
		return new EditableLensStar(this);
	}

	
	//
	// setters and getters
	//
	
	public int getNumberOfLenses() {
		return numberOfLenses;
	}

	public void setNumberOfLenses(int numberOfLenses) {
		this.numberOfLenses = numberOfLenses;
	}

	public boolean isShowAllLenses() {
		return showAllLenses;
	}

	public void setShowAllLenses(boolean showAllLenses) {
		this.showAllLenses = showAllLenses;
	}

	public int getShownLensesStartIndex() {
		return shownLensesStartIndex;
	}

	public void setShownLensesStartIndex(int shownLensesStartIndex) {
		this.shownLensesStartIndex = shownLensesStartIndex;
	}

	public int getNumberOfShownLenses() {
		return numberOfShownLenses;
	}

	public void setNumberOfShownLenses(int numberOfShownLenses) {
		this.numberOfShownLenses = numberOfShownLenses;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	public double getPrincipalPointDistance() {
		return principalPointDistance;
	}

	public void setPrincipalPointDistance(double principalPointDistance) {
		this.principalPointDistance = principalPointDistance;
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

	public Vector3D getPointOnLens1() {
		return pointOnLens1;
	}

	public void setPointOnLens1(Vector3D pointOnLens1) {
		this.pointOnLens1 = pointOnLens1;
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

	public double getLensTransmissionCoefficient() {
		return lensTransmissionCoefficient;
	}

	public void setLensTransmissionCoefficient(double lensTransmissionCoefficient) {
		this.lensTransmissionCoefficient = lensTransmissionCoefficient;
	}
	
	public boolean isLensesShadowThrowing() {
		return lensesShadowThrowing;
	}

	public void setLensesShadowThrowing(boolean lensesShadowThrowing) {
		this.lensesShadowThrowing = lensesShadowThrowing;
	}

	public ThinLensType getThinLensType() {
		return thinLensType;
	}

	public void setThinLensType(ThinLensType thinLensType) {
		this.thinLensType = thinLensType;
	}

	public Vector3D getDesignPosition() {
		return designPosition;
	}

	public void setDesignPosition(Vector3D designPosition) {
		this.designPosition = designPosition;
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
	 * normalised radial vector in the plane of lens 1
	 */
	private Vector3D u;
	
	/**
	 * normalised radial vector perpendicular to the plane of lens 1
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
		
		// calculate u by taking a vector from the centre to <pointOnLens1>, take the part of this vector
		// that is perpendicular to w, and normalise it
		u = Vector3D.difference(pointOnLens1, centre).getPartPerpendicularTo(w).getNormalised();
		
		// v = w x u; u, v, and w then form a right-handed Cartesian coordinate system
		v = Vector3D.crossProduct(w, u);
	}

	
	private void populateLensesAndEdges()
	{
		Vector3D
			axialSpanVector = w.getProductWith(starLength),
			topCorner = Vector3D.sum(centre, w.getProductWith(-starLength/2));
		
		Vector3D objectPosition, imagePosition;
		
		objectPosition = designPosition;
		imagePosition = null;

		for(int i=0; i<numberOfLenses; i++)
		{
			double phi = i*2.*Math.PI/numberOfLenses;
			double cos = Math.cos(phi);
			double sin = Math.sin(phi);
			
			Vector3D
				radialUnitVector = Vector3D.sum(u.getProductWith(cos), v.getProductWith(sin));
			
			int
				shownLensesStartIndexInRange = Math.floorMod(shownLensesStartIndex, numberOfLenses),
				shownLensesEndIndexInRange = Math.floorMod(
						shownLensesStartIndex + Math.min(numberOfShownLenses, numberOfLenses),
						numberOfLenses
					);
			boolean
				show = (showAllLenses?true:
					(shownLensesStartIndexInRange < shownLensesEndIndexInRange)?
					((i >= shownLensesStartIndexInRange) && (i < shownLensesEndIndexInRange)):
					((i >= shownLensesStartIndexInRange) || (i < shownLensesEndIndexInRange))
				);
			
//			System.out.println("EditableLensStar.populateLensesAndEdges: i="+i
//					+", phi="+phi
//					+", shownLensesStartIndexInRange="+shownLensesStartIndexInRange
//					+", shownLensesEndIndexInRange="+shownLensesEndIndexInRange
//					+", show="+show
//				);
			
			SurfaceProperty surface;
			
			if((thinLensType == ThinLensType.LENS_HOLOGRAM) && (designPosition != null))
			{
				// create a glens surface with the same imaging properties as the lens...
				GlensSurface g =
						new GlensSurface(
								Vector3D.sum(u.getProductWith(-sin), v.getProductWith(cos)),	// opticalAxisDirectionPos
								Vector3D.sum(
										centre,
										radialUnitVector.getProductWith(principalPointDistance)
										),	// principalPoint
								-focalLength,	// focalLengthNeg
								focalLength,	// focalLengthPos
								lensTransmissionCoefficient,	// transmissionCoefficient
								lensesShadowThrowing	// shadowThrowing
								);
				
				// ... and use it to calculate the image of the latest object position
				imagePosition = g.getFiniteImagePosition(objectPosition, ImagingDirection.NEG2POS);
				
				System.out.println("EditableLensStar::populateLensesAndEdges: objectPosition = "+objectPosition+", imagePosition = "+imagePosition);

				surface = new Point2PointImagingPhaseHologram(
						objectPosition,	// insideSpacePoint
						imagePosition,	// outsideSpacePoint
						lensTransmissionCoefficient,	// throughputCoefficient
						false,	// reflective
						lensesShadowThrowing	// shadowThrowing
					);
				
				// the image position of this lens is the next lens's object position
				objectPosition = imagePosition;
			}
			else
			{
				surface = ThinLensType.createThinLensSurface(
						thinLensType,	// thinLensType
						focalLength,	// focalLength
						Vector3D.sum(
								centre,
								radialUnitVector.getProductWith(principalPointDistance)
						),	// principalPoint
						Vector3D.sum(u.getProductWith(-sin), v.getProductWith(cos)),	// opticalAxisDirectionPos
						lensTransmissionCoefficient,	// transmissionCoefficient
						lensesShadowThrowing	// shadowThrowing
					);
			}

			lenses.addSceneObject(
					new EditableScaledParametrisedParallelogram(
							"Lens "+(i+1),	// description
							topCorner,	// corner 
							axialSpanVector,	// spanVector1
							radialUnitVector.getProductWith(starRadius),	// spanVector2
							surface,
							lenses,	// parent
							getStudio()
						),
					show	// visible
				);
						
			edges.addSceneObject(
					new EditableParametrisedCylinder(
							"Top edge of lens #"+(i+1),
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
							"Outer edge of lens #"+(i+1),
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
							"Bottom edge of lens #"+(i+1),
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
						"Axial edge (common to all lenses)",
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
		
		// prepare scene-object collection objects for the lenses...
		lenses = new EditableSceneObjectCollection("Lenses", true, this, getStudio());
		
		// ... and the edges
		edges = new EditableSceneObjectCollection("Edges", true, this, getStudio());

		// populate these collections
		calculateDirections();
		populateLensesAndEdges();
		
		// add the windows and the edges to this collection
		addSceneObject(lenses);
		addSceneObject(edges, showEdges);
	}
	
	
	
	
	// GUI panels
	private LabelledIntPanel numberOfLensesPanel, numberOfShownLensesPanel, shownLensesStartIndexPanel;
	private LabelledDoublePanel focalLengthPanel, principalPointDistancePanel, starRadiusPanel, starLengthPanel, lensTransmissionCoefficientPanel, edgeRadiusPanel;
	private LabelledVector3DPanel centrePanel, intersectionDirectionPanel, pointOnLens1Panel;
	private JComboBox<ThinLensType> thinLensTypeComboBox;
	private JCheckBox showEdgesCheckBox, showAllLensesCheckBox, lensesShadowThrowingCheckBox;
	private SurfacePropertyPanel edgeSurfacePropertyPanel;
	private JButton convertButton;
	
//	private LabelledDoublePanel frameRadiusLine;
//	private JCheckBox showFramesCheckBox;
//	private SurfacePropertyPanel frameSurfacePropertyPanel;
	
	


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Star of lenses"));
		editPanel.setLayout(new MigLayout("insets 0"));
		

		//
		// the basic-parameters panel
		// 
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		numberOfLensesPanel = new LabelledIntPanel("Number of lenses");
		basicParametersPanel.add(numberOfLensesPanel, "wrap");
		
		showAllLensesCheckBox = new JCheckBox("Show all lenses.");
		showAllLensesCheckBox.addActionListener(this);
		basicParametersPanel.add(showAllLensesCheckBox, "split 3");
		
		numberOfShownLensesPanel = new LabelledIntPanel("If not, show only");
		basicParametersPanel.add(numberOfShownLensesPanel);

		shownLensesStartIndexPanel = new LabelledIntPanel("lenses, starting with index");
		basicParametersPanel.add(shownLensesStartIndexPanel, "wrap");

		focalLengthPanel = new LabelledDoublePanel("Focal length of each lens");
		basicParametersPanel.add(focalLengthPanel, "wrap");
		
		principalPointDistancePanel = new LabelledDoublePanel("Distance from intersection line to principal points");
		basicParametersPanel.add(principalPointDistancePanel, "wrap");

		centrePanel = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centrePanel, "wrap");

		intersectionDirectionPanel = new LabelledVector3DPanel("Direction of intersection line");
		basicParametersPanel.add(intersectionDirectionPanel, "wrap");

		pointOnLens1Panel = new LabelledVector3DPanel("Point on first lens");
		basicParametersPanel.add(pointOnLens1Panel, "wrap");

		starRadiusPanel = new LabelledDoublePanel("Star radius");
		basicParametersPanel.add(starRadiusPanel, "wrap");

		starLengthPanel = new LabelledDoublePanel("Star length");
		basicParametersPanel.add(starLengthPanel, "wrap");

		lensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each lens");
		basicParametersPanel.add(lensTransmissionCoefficientPanel, "wrap");
		
		lensesShadowThrowingCheckBox = new JCheckBox("Lenses shadow throwing?");
		basicParametersPanel.add(lensesShadowThrowingCheckBox);
		
		thinLensTypeComboBox = new JComboBox<ThinLensType>(ThinLensType.values());
		thinLensTypeComboBox.setSelectedItem(thinLensType);
		basicParametersPanel.add(GUIBitsAndBobs.makeRow("Lens type", thinLensTypeComboBox), "span");

		
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
		numberOfLensesPanel.setNumber(numberOfLenses);
		showAllLensesCheckBox.setSelected(showAllLenses);
		numberOfShownLensesPanel.setEnabled(!showAllLenses);
		shownLensesStartIndexPanel.setEnabled(!showAllLenses);
		numberOfShownLensesPanel.setNumber(numberOfShownLenses);
		shownLensesStartIndexPanel.setNumber(shownLensesStartIndex);
		focalLengthPanel.setNumber(focalLength);
		principalPointDistancePanel.setNumber(principalPointDistance);
		centrePanel.setVector3D(centre);
		intersectionDirectionPanel.setVector3D(intersectionDirection);
		pointOnLens1Panel.setVector3D(pointOnLens1);
		starRadiusPanel.setNumber(starRadius);
		starLengthPanel.setNumber(starLength);
		lensTransmissionCoefficientPanel.setNumber(lensTransmissionCoefficient);
		lensesShadowThrowingCheckBox.setSelected(lensesShadowThrowing);
		showEdgesCheckBox.setSelected(showEdges);
		edgeRadiusPanel.setNumber(edgeRadius);
		edgeSurfacePropertyPanel.setSurfaceProperty(edgeSurfaceProperty);
		thinLensTypeComboBox.setSelectedItem(thinLensType);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableLensStar acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setNumberOfLenses(numberOfLensesPanel.getNumber());
		setShowAllLenses(showAllLensesCheckBox.isSelected());
		setNumberOfShownLenses(numberOfShownLensesPanel.getNumber());
		setShownLensesStartIndex(shownLensesStartIndexPanel.getNumber());
		setFocalLength(focalLengthPanel.getNumber());
		setPrincipalPointDistance(principalPointDistancePanel.getNumber());
		setCentre(centrePanel.getVector3D());
		setIntersectionDirection(intersectionDirectionPanel.getVector3D());
		setPointOnLens1(pointOnLens1Panel.getVector3D());
		setStarRadius(starRadiusPanel.getNumber());
		setStarLength(starLengthPanel.getNumber());
		setLensTransmissionCoefficient(lensTransmissionCoefficientPanel.getNumber());
		setLensesShadowThrowing(lensesShadowThrowingCheckBox.isSelected());
		setShowEdges(showEdgesCheckBox.isSelected());
		setEdgeRadius(edgeRadiusPanel.getNumber());
		setEdgeSurfaceProperty(edgeSurfacePropertyPanel.getSurfaceProperty());
		thinLensType = (ThinLensType)(thinLensTypeComboBox.getSelectedItem());
		
		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == showAllLensesCheckBox)
		{
			numberOfShownLensesPanel.setEnabled(!(showAllLensesCheckBox.isSelected()));
			shownLensesStartIndexPanel.setEnabled(!(showAllLensesCheckBox.isSelected()));
		}
		else if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-lens-star");
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