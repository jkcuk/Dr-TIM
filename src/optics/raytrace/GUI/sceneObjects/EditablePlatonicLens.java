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
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.GlensSurface;

/**
 * A 3D generalisation of the "star of lenses".
 * 
 * @author Johannes
 */
public class EditablePlatonicLens extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 1819715062857821657L;

	//
	// parameters
	//
	
	/**
	 * The Platonic solid on which this lens is based.
	 */
	private EditablePlatonicSolid platonicSolid;
	
	/**
	 * Focal length of each of the lenses.
	 */
	private double focalLength;
		
	/**
	 * Transmission coefficient of each lens.
	 */
	private double lensTransmissionCoefficient;

	/**
	 * Show the Platonic solid?
	 * To show the lens edges, this has to be true, the Platonic solid's edges must be shown, and <showInnerEdges>
	 * must be true.
	 */
	private boolean showPlatonicSolid;
	
	/**
	 * Show the lens edges inside the Platonic solid?
	 */
	private boolean showInnerEdges;
	

	//
	// internal variables
	//
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection lenses;	//, frames;
	

	// GUI panels
	private LabelledDoublePanel focalLengthPanel, lensTransmissionCoefficientPanel;
	private JButton convertButton, editPlatonicSolidButton;
	private JCheckBox showPlatonicSolidCheckBox, showInnerEdgesCheckBox;

	
	/**
	 * @param description
	 * @param platonicSolid
	 * @param focalLength
	 * @param lensTransmissionCoefficient
	 * @param showPlatonicSolid
	 * @param showInnerEdges
	 * @param parent
	 * @param studio
	 */
	public EditablePlatonicLens(
			String description,
			EditablePlatonicSolid platonicSolid,
			double focalLength,
			double lensTransmissionCoefficient,
			boolean showPlatonicSolid,
			boolean showInnerEdges,
			SceneObject parent, 
			Studio studio
			)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setPlatonicSolid(platonicSolid);
		setFocalLength(focalLength);
		setLensTransmissionCoefficient(lensTransmissionCoefficient);
		setShowPlatonicSolid(showPlatonicSolid);
		setShowInnerEdges(showInnerEdges);

		populateSceneObjectCollection();
	}
	
	public EditablePlatonicLens(SceneObject parent, Studio studio)
	{
		this(
				"Platonic lens",	// description
				new EditablePlatonicSolid(parent, studio),	// platonicSolid
				1.0,	// focalLength
				0.96,	// lensTransmissionCoefficient
				true,	// showPlatonicSolid
				true,	// showInnerEdges
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePlatonicLens(EditablePlatonicLens original)
	{
		this(
			original.getDescription(),
			original.getPlatonicSolid().clone(),
			original.getFocalLength(),
			original.getLensTransmissionCoefficient(),
			original.isShowPlatonicSolid(),
			original.isShowInnerEdges(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePlatonicLens clone()
	{
		return new EditablePlatonicLens(this);
	}

	
	//
	// setters and getters
	//
	
	public EditablePlatonicSolid getPlatonicSolid() {
		return platonicSolid;
	}

	public void setPlatonicSolid(EditablePlatonicSolid platonicSolid) {
		this.platonicSolid = platonicSolid;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}
	
	public double getLensTransmissionCoefficient() {
		return lensTransmissionCoefficient;
	}

	public void setLensTransmissionCoefficient(double lensTransmissionCoefficient) {
		this.lensTransmissionCoefficient = lensTransmissionCoefficient;
	}

	public boolean isShowPlatonicSolid() {
		return showPlatonicSolid;
	}

	public void setShowPlatonicSolid(boolean showPlatonicSolid) {
		this.showPlatonicSolid = showPlatonicSolid;
	}
	
	public boolean isShowInnerEdges() {
		return showInnerEdges;
	}

	public void setShowInnerEdges(boolean showInnerEdges) {
		this.showInnerEdges = showInnerEdges;
	}

	
	private void populateLenses()
	{
		// we need one lens per edge
		for(int i=0; i<platonicSolid.getNumberOfEdges(); i++)
		{
			// collect the vertices of the triangle that is the aperture of the lens
			Vector3D[] triangleVertices = new Vector3D[3];
			triangleVertices[0] = platonicSolid.getCentre();
			triangleVertices[1] = platonicSolid.getVertex(platonicSolid.getEdge(i)[0]);
			triangleVertices[2] = platonicSolid.getVertex(platonicSolid.getEdge(i)[1]);
			
			Vector3D normal = Vector3D.crossProduct(
					Vector3D.difference(triangleVertices[1], triangleVertices[0]),
					Vector3D.difference(triangleVertices[2], triangleVertices[0])
				);
			
			lenses.addSceneObject(
					new EditableParametrisedConvexPolygon(
							"Lens "+(i+1),	// description,
					normal,	// normalToPlane,
					triangleVertices,	// vertices[],
					new GlensSurface(
							normal,	// opticalAxisDirectionPos
							platonicSolid.getCentre(),	// principalPoint
							-focalLength,	// focalLengthNeg
							focalLength,	// focalLengthPos
							lensTransmissionCoefficient,	// transmissionCoefficient
							true	// shadowThrowing
							),	// surfaceProperty
					lenses,	// parent
					getStudio()
				));
		}
	}
	
	private EditableSceneObjectCollection innerEdges;

	private void populateInnerEdges()
	{
		for(int i=0; i<platonicSolid.getNumberOfVertices(); i++)
		{
			innerEdges.addSceneObject(new EditableParametrisedCylinder(
				"Edge from centre to vertex #"+(i+1),
				platonicSolid.getCentre(),	// start point
				platonicSolid.getVertex(i),	// end point
				platonicSolid.getEdgeRadius(),	// radius
				platonicSolid.getEdgeSurfaceProperty(),
				innerEdges,
				getStudio()
			));
		}
	}


	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the lenses and inner edges...
		lenses = new EditableSceneObjectCollection("Lenses", true, this, getStudio());
		innerEdges = new EditableSceneObjectCollection("Inner edges", true, this, getStudio());
		
		// populate these collections
		populateLenses();
		populateInnerEdges();
		
		// add the windows and the frames to this collection
		addSceneObject(lenses);
		addSceneObject(innerEdges, showInnerEdges);
		
		addSceneObject(platonicSolid, showPlatonicSolid);
	}

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
		
		editPlatonicSolidButton = new JButton("Edit Platonic solid");
		editPlatonicSolidButton.addActionListener(this);
		basicParametersPanel.add(editPlatonicSolidButton, "wrap");

		showPlatonicSolidCheckBox = new JCheckBox("Show Platonic solid");
		basicParametersPanel.add(showPlatonicSolidCheckBox, "wrap");

		showInnerEdgesCheckBox = new JCheckBox("Show inner edges (from vertices to centre)");
		basicParametersPanel.add(showInnerEdgesCheckBox, "wrap");

		focalLengthPanel = new LabelledDoublePanel("Focal length of each lens");
		basicParametersPanel.add(focalLengthPanel, "wrap");

		lensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each lens");
		basicParametersPanel.add(lensTransmissionCoefficientPanel, "wrap");

		editPanel.add(basicParametersPanel, "wrap");


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
		focalLengthPanel.setNumber(focalLength);
		lensTransmissionCoefficientPanel.setNumber(lensTransmissionCoefficient);
		showPlatonicSolidCheckBox.setSelected(showPlatonicSolid);
		showInnerEdgesCheckBox.setSelected(showInnerEdges);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePlatonicLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setFocalLength(focalLengthPanel.getNumber());
		setLensTransmissionCoefficient(lensTransmissionCoefficientPanel.getNumber());
		setShowPlatonicSolid(showPlatonicSolidCheckBox.isSelected());
		setShowInnerEdges(showInnerEdgesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(convertButton))
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-Platonic-lens");
			container.setValuesInEditPanel();
		}
		else if(e.getSource().equals(editPlatonicSolidButton))
		{
			iPanel.addFrontComponent(platonicSolid, "Platonic solid");
			platonicSolid.setValuesInEditPanel();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof EditablePlatonicSolid)
			{
				// frame surface property has been edited
				setPlatonicSolid((EditablePlatonicSolid)edited);
			}
	}
}