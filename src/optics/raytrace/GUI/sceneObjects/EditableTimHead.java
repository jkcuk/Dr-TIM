package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.TimHead;

/**
 * Tim's head
 * @author Johannes Courtial
 */
public class EditableTimHead extends TimHead implements IPanelComponent, ActionListener
{
	private static final long serialVersionUID = 2498905045875025645L;

	// GUI panels
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledVector3DPanel frontDirectionPanel;
	private LabelledVector3DPanel topDirectionPanel;
	private LabelledVector3DPanel rightDirectionPanel;
	private JButton convertButton;
	private IPanel iPanel;

	/**
	 * Default constructor
	 * @param description
	 * @param centre
	 * @param parent
	 * @param studio
	 */
	public EditableTimHead(
			String description,
			Vector3D centre,
			double radius,
			Vector3D frontDirection,
			Vector3D topDirection,
			Vector3D rightDirection,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, centre, radius, frontDirection, topDirection, rightDirection, parent, studio);
	}
	
	public EditableTimHead(
			SceneObject parent, 
			Studio studio
	)
	{
		this(
				"Tim's head",	// description
				new Vector3D(0, 0, 10),	// centre
				1,	// radius
				new Vector3D(0, 0, -1),	// frontDirection
				new Vector3D(0, 1, 0),	// topDirection
				new Vector3D(1, 0, 0),	// rightDirection
				parent, 
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableTimHead(EditableTimHead original)
	{
		super(original);
	}

	@Override
	public EditableTimHead clone()
	{
		return new EditableTimHead(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Tim's head"));
        editPanel.setLayout(new MigLayout("insets 0"));
        
        // c.fill = GridBagConstraints.BOTH;
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "span");

		// the centre
		
		centrePanel = new LabelledVector3DPanel("Centre position");
		editPanel.add(centrePanel, "span");
		
		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "span");
		
		frontDirectionPanel = new LabelledVector3DPanel("Front direction");
		editPanel.add(frontDirectionPanel, "span");

		topDirectionPanel = new LabelledVector3DPanel("Top direction");
		editPanel.add(topDirectionPanel, "span");
		
		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
		editPanel.add(rightDirectionPanel, "span");

		// the convert button
		
		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

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
		centrePanel.setVector3D(getCentre());
		radiusPanel.setNumber(getRadius());
		frontDirectionPanel.setVector3D(getFrontDirection());
		topDirectionPanel.setVector3D(getTopDirection());
		rightDirectionPanel.setVector3D(getRightDirection());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableTimHead acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setFrontDirection(frontDirectionPanel.getVector3D());
		setTopDirection(topDirectionPanel.getVector3D());
		setRightDirection(rightDirectionPanel.getVector3D());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the necessary elements
		addElements();
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this, true);
		iPanel.replaceFrontComponent(container, "Edit");
		container.setValuesInEditPanel();
	}
}
