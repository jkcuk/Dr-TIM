package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;

/**
 * An editable CLA structure that works from certain directions.
 * 
 * @author Johannes, Stephen
 */
public class EditableCLACloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 6601170820854914480L;

	// parameters
	private Vector3D
		centre,
		normalToSurfaces1,
		normalToSurfaces2;
	private double
		a,
		b,
		height;

	// GUI panels
	private LabelledVector3DPanel centreLine, normalToSurfaces1Line,normalToSurfaces2Line;
	private LabelledDoublePanel aLine, bLine;
	private JButton convertButton;

	public EditableCLACloak(
			String description,
			Vector3D centre,
			Vector3D normalToSurfaces1,
			Vector3D normalToSurfaces2,
			double a,
			double b,
			double height,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setNormalToSurfaces1(normalToSurfaces1);
		setNormalToSurfaces2(normalToSurfaces2);
		setA(a);
		setB(b);
		setHeight(height);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCLACloak(EditableCLACloak original)
	{
		super(original);
		
		// copy the original's parameters
		setCentre(original.getCentre().clone());
		setNormalToSurfaces1(original.getNormalToSurfaces1().clone());
		setNormalToSurfaces2(original.getNormalToSurfaces2().clone());
		setA(original.getA());
		setB(original.getB());
		setHeight(original.getHeight());
	}

	@Override
	public EditableCLACloak clone()
	{
		return new EditableCLACloak(this);
	}

	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}


	public Vector3D getNormalToSurfaces1() {
		return normalToSurfaces1;
	}

	/**
	 * normalises the argument, and uses it as the normal to one type of windows
	 * @param normalToSurfaces1
	 */
	public void setNormalToSurfaces1(Vector3D normalToSurfaces1) {
		this.normalToSurfaces1 = normalToSurfaces1.getNormalised();
	}

	public Vector3D getNormalToSurfaces2() {
		return normalToSurfaces2;
	}

	public void setNormalToSurfaces2(Vector3D normalToSurfaces2) {
		this.normalToSurfaces2 = normalToSurfaces2;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}
	
	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	private void addSurfacesForCorner(Vector3D cornerPoint, Vector3D u, Vector3D v)
	{
		// TODO add those three surfaces here that intersect that corner, using addSceneObject
		// make them all EditableFramedWindows
	}

	private void populateSceneObjectCollection()
	{
		// add the surfaces corresponding to all the corners
		for(int i=-1; i<=1; i+=2)
		{
			for(int j=-1; j<=1; j+=2)
			{
				Vector3D cornerPoint = Vector3D.sum(
						getCentre(),
						getNormalToSurfaces1().getProductWith(getA()*i),
						getNormalToSurfaces2().getProductWith(getA()*j)
					);
				Vector3D u, v;
				if(i == j)
				{
					u = getNormalToSurfaces1().getProductWith(-i);
					v = getNormalToSurfaces2().getProductWith(-j);
				}
				else
				{
					u = getNormalToSurfaces2().getProductWith(-j);
					v = getNormalToSurfaces1().getProductWith(-i);
				}
				addSurfacesForCorner(cornerPoint, u, v);
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
		editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("CLA cloak"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel);

		centreLine = new LabelledVector3DPanel("Centre");
		editPanel.add(centreLine);

		normalToSurfaces1Line = new LabelledVector3DPanel("Normal to surfaces 1");
		editPanel.add(normalToSurfaces1Line);

		normalToSurfaces2Line = new LabelledVector3DPanel("Normal to surfaces 2");
		editPanel.add(normalToSurfaces2Line);

		aLine = new LabelledDoublePanel("a");
		editPanel.add(aLine);

		bLine = new LabelledDoublePanel("b");
		editPanel.add(bLine);
		
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
		normalToSurfaces1Line.setVector3D(getNormalToSurfaces1());
		normalToSurfaces2Line.setVector3D(getNormalToSurfaces2());
		aLine.setNumber(getA());
		bLine.setNumber(getB());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCLACloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setNormalToSurfaces1(normalToSurfaces1Line.getVector3D());
		setNormalToSurfaces2(normalToSurfaces2Line.getVector3D());
		setA(aLine.getNumber());
		setB(bLine.getNumber());

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