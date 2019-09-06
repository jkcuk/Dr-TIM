package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

/**
 * An editable gCLA structure that works cloaks from all directions.
 * 
 * @author Johannes, Stephen
 */
public class EditablePolygonalCloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 2905911187722422898L;

	// parameters
	private Vector3D
		centre,
		centreToCentreOfFrontFace,
		centreToCentreOfRightFace,
		centreToCentreOfTopFace;
	private double
		a,
		aPrime;
	private boolean showFrames;

	// GUI panels
	private LabelledVector3DPanel centreLine, centreToCentreOfFrontFaceLine, centreToCentreOfRightFaceLine, centreToCentreOfTopFaceLine;
	private LabelledDoublePanel aLine, aPrimeLine;
	private JButton convertButton;
	private JCheckBox showFramesCheckBox;

	public EditablePolygonalCloak(
			String description,
			Vector3D centre,
			Vector3D centreToCentreOfFrontFace,
			Vector3D centreToCentreOfRightFace,
			Vector3D centreToCentreOfTopFace,
			double a,
			double aPrime,
			boolean showFrames,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setVectorsFromCentreToCentresOfFaces(
				centreToCentreOfFrontFace,
				centreToCentreOfRightFace,
				centreToCentreOfTopFace
			);
		setA(a);
		setAPrime(aPrime);
		setShowFrames(showFrames);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePolygonalCloak(EditablePolygonalCloak original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getCentreToCentreOfFrontFace().clone(),
			original.getCentreToCentreOfRightFace().clone(),
			original.getCentreToCentreOfTopFace().clone(),
			original.getA(),
			original.getAPrime(),
			original.isShowFrames(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePolygonalCloak clone()
	{
		return new EditablePolygonalCloak(this);
	}

	
	// setters and getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getCentreToCentreOfFrontFace() {
		return centreToCentreOfFrontFace;
	}

	public Vector3D getCentreToCentreOfRightFace() {
		return centreToCentreOfRightFace;
	}

	public Vector3D getCentreToCentreOfTopFace() {
		return centreToCentreOfTopFace;
	}

	/**
	 * Set the direction to the front face to centreToCentreOfFrontFace,
	 * then set the direction to the right face to the part of centreToCentreOfRightFace that is perpendicular to the direction to the front face,
	 * then set the direction to the top face to the part of centreToCentreOfTopFace that is perpendicular to both the direction to the front face and to the right face.
	 * @param centreToCentreOfFrontFace
	 * @param centreToCentreOfRightFace
	 * @param centreToCentreOfTopFace
	 */
	public void setVectorsFromCentreToCentresOfFaces(
			Vector3D centreToCentreOfFrontFace,
			Vector3D centreToCentreOfRightFace,
			Vector3D centreToCentreOfTopFace
		)
	{
		this.centreToCentreOfFrontFace = centreToCentreOfFrontFace;
		this.centreToCentreOfRightFace = centreToCentreOfRightFace.getPartPerpendicularTo(centreToCentreOfFrontFace);
		this.centreToCentreOfTopFace = centreToCentreOfTopFace.getPartParallelTo(
				Vector3D.crossProduct(this.centreToCentreOfFrontFace, this.centreToCentreOfRightFace)
			);
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getAPrime() {
		return aPrime;
	}

	public void setAPrime(double aPrime) {
		this.aPrime = aPrime;
	}
	
	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}
	

	/**
	 * We have divided up all the surfaces that make up the cloak into those behind the different exterior faces.
	 * This method adds the surfaces behind one particular exterior face.
	 * @param faceName
	 * @param cloakCentre2FaceCentre
	 * @param right
	 * @param up
	 */
	private void addInterfacesForFace(String faceName, Vector3D cloakCentre2FaceCentre, Vector3D right, Vector3D up)
	{
		// parameters for outside CLAs
		double eta1 = aPrime/a;
		
		// parameters for inside gCLAs (deltaY2 = 0)
		double eta2 = (a*(4*aPrime*aPrime-1))/(aPrime*(4*a*a-1));
		double deltax2 = (2*(a*a-2*a*aPrime+aPrime*aPrime))/(aPrime*(4*a*a-1));
		
		// the centre of the exterior face
		Vector3D faceCentre = Vector3D.sum(getCentre(), cloakCentre2FaceCentre);
				
		SurfacePropertyPrimitive frameSurfaceProperty = new SurfaceColour(DoubleColour.GREY50, DoubleColour.WHITE, false);
		
		// the exterior CLAs
		GCLAsWithApertures sheet1 = new GCLAsWithApertures(
				Vector3D.Z,	// aHat
				Vector3D.X,	// uHat
				Vector3D.Y,	// vHat
				eta1, eta1, 0, 0,
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				0.96,
				false
			);
		EditableFramedRectangle window1 = new EditableFramedRectangle(
				faceName+" exterior face",
				Vector3D.sum(faceCentre, right.getProductWith(-1), up.getProductWith(-1)),	// corner
				right.getProductWith(2),	// width vector
				up.getProductWith(2),	// height vector
				0.01,
				sheet1,
				frameSurfaceProperty,
				isShowFrames(),
				this,
				getStudio()
		);
		addSceneObject(window1);

		// interior gCLAs
		GCLAsWithApertures sheet2 = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y,	eta2, eta2, deltax2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, 0.96, false);
		
		addSceneObject(new EditableFramedRectangle(
				"interior left interface behind "+faceName+" exterior face",
				Vector3D.sum(faceCentre, cloakCentre2FaceCentre.getProductWith(-aPrime/0.5), up), //corner
				Vector3D.sum(right.getProductWith(-1), cloakCentre2FaceCentre.getProductWith(aPrime/0.5)), // width vector
				up.getProductWith(-2),	// height vector 
				0.01,
				sheet2,
				frameSurfaceProperty,
				isShowFrames(),
				this,
				getStudio()
		));

		addSceneObject(new EditableFramedRectangle(
				"interior right interface behind "+faceName+" exterior face",
				Vector3D.sum(faceCentre, cloakCentre2FaceCentre.getProductWith(-aPrime/0.5), up.getProductWith(-1)), //corner
				Vector3D.sum(right, cloakCentre2FaceCentre.getProductWith(aPrime/0.5)), // width vector
				up.getProductWith(2),	// height vector 
				0.01,
				sheet2,
				frameSurfaceProperty,
				isShowFrames(),
				this,
				getStudio()
		));
	
	}
	
//	cornerPoint,	// corner
//	Vector3D.sum(v.getWithLength(b), u.getWithLength(a)),	// width vector

	private void populateSceneObjectCollection()
	{
		addInterfacesForFace(
				"Front",	// faceName,
				centreToCentreOfFrontFace,	// cloakCentre2FaceCentre,
				centreToCentreOfRightFace,	// right,
				centreToCentreOfTopFace	// up
			);

		addInterfacesForFace(
				"Back",	// faceName,
				centreToCentreOfFrontFace.getProductWith(-1),	// cloakCentre2FaceCentre,
				centreToCentreOfRightFace.getProductWith(-1),	// right,
				centreToCentreOfTopFace	// up
			);

		addInterfacesForFace(
				"Right",	// faceName,
				centreToCentreOfRightFace,	// cloakCentre2FaceCentre,
				centreToCentreOfFrontFace.getProductWith(-1),	// right,
				centreToCentreOfTopFace	// up
			);

		addInterfacesForFace(
				"Left",	// faceName,
				centreToCentreOfRightFace.getProductWith(-1),	// cloakCentre2FaceCentre,
				centreToCentreOfFrontFace,	// right,
				centreToCentreOfTopFace	// up
			);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("gCLA cloak"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel);

		centreLine = new LabelledVector3DPanel("Centre");
		editPanel.add(centreLine);

		centreToCentreOfFrontFaceLine = new LabelledVector3DPanel("Cloak centre to centre of front face");
		editPanel.add(centreToCentreOfFrontFaceLine);

		centreToCentreOfRightFaceLine = new LabelledVector3DPanel("Cloak centre to centre of right face");
		editPanel.add(centreToCentreOfRightFaceLine);

		centreToCentreOfTopFaceLine = new LabelledVector3DPanel("Cloak centre to centre of top face");
		editPanel.add(centreToCentreOfTopFaceLine);

		aLine = new LabelledDoublePanel("a");
		editPanel.add(aLine);

		aPrimeLine = new LabelledDoublePanel("aPrime");
		editPanel.add(aPrimeLine);
		
		showFramesCheckBox = new JCheckBox("Show frames");
		editPanel.add(showFramesCheckBox);
		
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
		
		centreLine.setVector3D(getCentre());
		centreToCentreOfFrontFaceLine.setVector3D(getCentreToCentreOfFrontFace());
		centreToCentreOfRightFaceLine.setVector3D(getCentreToCentreOfRightFace());
		centreToCentreOfTopFaceLine.setVector3D(getCentreToCentreOfTopFace());
		aLine.setNumber(getA());
		aPrimeLine.setNumber(getAPrime());
		showFramesCheckBox.setSelected(isShowFrames());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePolygonalCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setVectorsFromCentreToCentresOfFaces(
				centreToCentreOfFrontFaceLine.getVector3D(),
				centreToCentreOfRightFaceLine.getVector3D(),
				centreToCentreOfTopFaceLine.getVector3D()
			);
		setA(aLine.getNumber());
		setAPrime(aPrimeLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-CLA cloak");
		container.setValuesInEditPanel();
	}
}