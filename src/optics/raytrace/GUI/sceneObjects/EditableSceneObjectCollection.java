package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.SceneObjectListPanel;
import optics.raytrace.GUI.lowLevel.SceneObjectTablePanel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.Transformation;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.solidGeometry.*;
import optics.raytrace.utility.CopyModeType;

/**
 * A collection of scene objects.
 * 
 * The list of object types has now moved into the SceneObjectListPanel class.
 * 
 * Note that this class does not simply extend the SceneObjectContainer class so that it can also change it
 * from a simple collection of scene objects into an intersection, ... of scene objects and vice versa.
 *
 * @author Johannes Courtial
 *
 */
public class EditableSceneObjectCollection
implements SceneObject, IPanelComponent, ActionListener, Cloneable, Serializable
{
	private static final long serialVersionUID = 5956058557396407505L;
	
	private boolean TABLE = false;	// TODO	set to true once the various bugs have been fixed

	private static final String STUDIO_INITIALISE_TO = "Initialise scene to...";
	
	public enum CombinationMode {
		GROUP("Group"),
		UNION("Union"),
		INTERSECTION("Intersection");
		
		private final String description;
		
		CombinationMode(String description) {
	        this.description = description;
	    }
		
		public String toString()
		{
			return description;
		}
	}
	
	/**
	 * the SceneObject one level up in the hierarchy
	 */
	private SceneObject parent;
	
	/**
	 * the scene-object container this EditableSceneObjectContainer is editing
	 */
	protected SceneObjectContainer sceneObjectContainer;

	protected boolean combinationModeEditable;
	
	protected JPanel editPanel, combinationModePanel;
	protected LabelledStringPanel descriptionPanel;
	protected SceneObjectTablePanel sceneObjectTablePanel;
	protected SceneObjectListPanel sceneObjectListPanel;
	private JComboBox<Object> initialiseSceneComboBox;	// combinationModeComboBox;
	private JComboBox<CombinationMode> combinationModeComboBox;
	protected IPanel iPanel;

	public EditableSceneObjectCollection(String description, CombinationMode combinationMode, boolean combinationModeEditable, SceneObject parent, Studio studio)
	{
		setCombinationModeEditable(combinationModeEditable);
		sceneObjectContainer = createNewSceneObjectContainer(description, combinationMode, parent, studio);
	}

	public EditableSceneObjectCollection(String description, boolean combinationModeEditable, SceneObject parent, Studio studio)
	{
		setCombinationModeEditable(combinationModeEditable);
		sceneObjectContainer = 	createNewSceneObjectContainer(description, CombinationMode.GROUP, parent, studio);
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public EditableSceneObjectCollection(EditableSceneObjectCollection original)
	{
		setCombinationModeEditable(original.isCombinationModeEditable());
		sceneObjectContainer = original.getSceneObjectContainer().clone();
	}
	
	public EditableSceneObjectCollection(SceneObjectContainer sceneObjectContainer, boolean combinationModeEditable)
	{
		this.sceneObjectContainer = sceneObjectContainer;
		setCombinationModeEditable(combinationModeEditable);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.SceneObjectContainer#clone()
	 */
	@Override
	public EditableSceneObjectCollection clone()
	{
		return new EditableSceneObjectCollection(this);
	}


	public SceneObjectContainer getSceneObjectContainer() {
		return sceneObjectContainer;
	}

	public void setSceneObjectContainer(SceneObjectContainer sceneObjectContainer) {
		this.sceneObjectContainer = sceneObjectContainer;
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;

		// create the edit panel for this SceneObjectContainer, which can contain one of two
		// elements: either the listPanel, which selects or manages individual objects in the
		// container, or the editPanel of one of the individual components
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		// add a border to the list panel
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Collection of scene objects"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

//		String[] combinationModeComponentStrings = { COMBINATION_MODE_GROUP, COMBINATION_MODE_UNION, COMBINATION_MODE_INTERSECTION };
//		combinationModeComboBox = new JComboBox<String>(combinationModeComponentStrings);
		combinationModeComboBox = new JComboBox<CombinationMode>(CombinationMode.values());
		combinationModePanel = GUIBitsAndBobs.makeRow("Combine elements as", combinationModeComboBox);
		if(isCombinationModeEditable())
		{
			editPanel.add(combinationModePanel, "wrap");
		}

		if(TABLE)
		{
			sceneObjectTablePanel = new SceneObjectTablePanel(sceneObjectContainer, this, iPanel);
			editPanel.add(sceneObjectTablePanel, "wrap");
		}
		else
		{
			sceneObjectListPanel = new SceneObjectListPanel(sceneObjectContainer, this, iPanel);
			editPanel.add(sceneObjectListPanel, "wrap");
		}

		// add a bit of (non-stretchable) space
		// listPanel.add(Box.createRigidArea(new Dimension(10,5)));

		//
		// the Initialise selector and button
		//

		initialiseSceneComboBox = new JComboBox<Object>(StudioInitialisationType.limitedValuesForInteractiveTIM);
		initialiseSceneComboBox.insertItemAt(STUDIO_INITIALISE_TO, 0);
		initialiseSceneComboBox.setSelectedItem(STUDIO_INITIALISE_TO);
		initialiseSceneComboBox.setActionCommand("Initialise");
		initialiseSceneComboBox.addActionListener(this);
		initialiseSceneComboBox.setVisible(false);	// by default, make invisible
		editPanel.add(initialiseSceneComboBox);

		// validate the entire edit panel
		editPanel.validate();
	}
	
	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}
	
//	public void setCombinationModePanelVisible(boolean visibility)
//	{
//		combinationModePanel.setVisible(visibility);
//	}
	
	public void setInitialisationComboBoxVisible(boolean visibility)
	{
		initialiseSceneComboBox.setVisible(visibility);
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
		descriptionPanel.setString(sceneObjectContainer.getDescription());
		
		if(sceneObjectContainer instanceof SceneObjectUnion)
		{
			combinationModeComboBox.setSelectedItem(CombinationMode.UNION);
		}
		else if(sceneObjectContainer instanceof SceneObjectIntersection)
		{
			combinationModeComboBox.setSelectedItem(CombinationMode.INTERSECTION);
		}
		else
		{
			combinationModeComboBox.setSelectedItem(CombinationMode.GROUP);
		}

		if(TABLE) sceneObjectTablePanel.setSceneObjectContainer(sceneObjectContainer);
		else sceneObjectListPanel.setSceneObjectContainer(sceneObjectContainer);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSceneObjectCollection acceptValuesInEditPanel()
	{
		sceneObjectContainer.setDescription(descriptionPanel.getString());

		switch((CombinationMode)(combinationModeComboBox.getSelectedItem()))
		{
		case INTERSECTION:
			// System.out.println("EditableSceneObjectCollection::acceptValuesInEditPanel: combinationMode = INTERSECTION");
			sceneObjectContainer = new SceneObjectIntersection(sceneObjectContainer, CopyModeType.SHARE_DATA);
			break;
		case UNION:
			// System.out.println("EditableSceneObjectCollection::acceptValuesInEditPanel: combinationMode = UNION");
			sceneObjectContainer = new SceneObjectUnion(sceneObjectContainer, CopyModeType.SHARE_DATA);
			break;
		case GROUP:
		default:
			// System.out.println("EditableSceneObjectCollection::acceptValuesInEditPanel: combinationMode = GROUP");
			sceneObjectContainer = new SceneObjectContainer(sceneObjectContainer, CopyModeType.SHARE_DATA);
			break;
		}

		return this;
	}
	
	/**
	 * @param description
	 * @param combinationMode
	 * @param parent
	 * @param studio
	 * @return	a new SceneObjectContainer with its type determined by combinationMode
	 */
	private SceneObjectContainer createNewSceneObjectContainer(String description, CombinationMode combinationMode, SceneObject parent, Studio studio)
	{
		switch(combinationMode)
		{
		case UNION:
			return new SceneObjectUnion(description, parent, studio);
		case INTERSECTION:
			return new SceneObjectIntersection(description, parent, studio);
		case GROUP:
		default:
			return new SceneObjectContainer(description, parent, studio);
		}
	}
	
	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(TABLE)
		{
			System.out.println("EditableSceneObjectCollection:backToFront: sceneObjectTablePanel="+sceneObjectTablePanel);
			sceneObjectTablePanel.sceneObjectEditingCompleted((SceneObject)edited);
		}
		else sceneObjectListPanel.sceneObjectEditingCompleted((SceneObject)edited);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// IPanelComponent iPanelComponent = null;
		
		if((e.getSource() == initialiseSceneComboBox) && (initialiseSceneComboBox.getSelectedItem() instanceof StudioInitialisationType))
		// if(e.getActionCommand().equals("Initialise") && initialiseSceneComboBox.getSelectedIndex() != 0)
		{
			StudioInitialisationType studioInitialisationType = (StudioInitialisationType)initialiseSceneComboBox.getSelectedItem();
			initialiseSceneComboBox.setSelectedItem(STUDIO_INITIALISE_TO);
			
			// remove any scene objects currently in this collection
			sceneObjectContainer.clear();
			
			System.out.println("Initialising scene to "+studioInitialisationType.toString());
			
			StudioInitialisationType.initialiseSceneAndLights(studioInitialisationType, sceneObjectContainer, getStudio());
			
			// show the new contents
			if(TABLE) sceneObjectTablePanel.setSceneObjectContainer(sceneObjectContainer);
			else sceneObjectListPanel.setSceneObjectContainer(sceneObjectContainer);
		}
	}


	//
	// make all the methods of a SceneObjectContainer available
	//
	
	public boolean containsSceneObject(SceneObject o)
	{
		return sceneObjectContainer.containsSceneObject( o );
	}
	
	public boolean isSceneObjectVisible(int i)
	{
		return sceneObjectContainer.isSceneObjectVisible(i);
	}
	
	public void setSceneObjectVisible(int i, boolean isVisible)
	{
		sceneObjectContainer.setSceneObjectVisible(i, isVisible);
	}
	
	public void setSceneObjectVisible(SceneObject o, boolean isVisible)
	{
		sceneObjectContainer.setSceneObjectVisible(o, isVisible);
	}

	public boolean isCombinationModeEditable() {
		return combinationModeEditable;
	}

	public void setCombinationModeEditable(boolean combinationModeEditable) {
		this.combinationModeEditable = combinationModeEditable;
	}

	public void addSceneObject(SceneObject o)
	{
		sceneObjectContainer.addSceneObject(o);
	}
	
	public void addSceneObject(SceneObject o, boolean isVisible)
	{
		sceneObjectContainer.addSceneObject(o, isVisible);
	}

	public void addSceneObject(int index, SceneObject o, boolean isVisible)
	{
		sceneObjectContainer.addSceneObject(index, o, isVisible);
	}
	
	public void addSceneObject(int index, SceneObject o)
	{
		sceneObjectContainer.addSceneObject(index, o);
	}

	public void setSceneObject(int index, SceneObject o, boolean isVisible)
	{
		sceneObjectContainer.setSceneObject(index, o, isVisible);
	}
	
	public void setSceneObject(int index, SceneObject o)
	{
		sceneObjectContainer.setSceneObject(index, o);
	}

	public void removeSceneObject(SceneObject o)
	{
		sceneObjectContainer.removeSceneObject(o);
	}

	public ArrayList<SceneObject> getSceneObjects()
	{
		return sceneObjectContainer.getSceneObjects();
	}
	
	public ArrayList<Boolean> getVisibilities()
	{
		return sceneObjectContainer.getVisibilities();
	}

	public void clear()
	{
		sceneObjectContainer.clear();
	}

	public SceneObject getSceneObject(int index)
	{
		return sceneObjectContainer.getSceneObject(index);
	}
	
	public int getNumberOfSceneObjects()
	{
		return sceneObjectContainer.getNumberOfSceneObjects();
	}
	
	public SceneObject getFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		return sceneObjectContainer.getFirstSceneObjectWithDescription(description, searchIteratively);
	}

	public ArrayList<SceneObject> getPathToFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		return sceneObjectContainer.getPathToFirstSceneObjectWithDescription(description, searchIteratively);
	}

	public SceneObject removeFirstSceneObjectWithDescription(String description, boolean searchIteratively)
	{
		return sceneObjectContainer.removeFirstSceneObjectWithDescription(description, searchIteratively);
	}

	public int removeAllSceneObjectsWithDescription(String description, boolean searchIteratively)
	{
		return sceneObjectContainer.removeAllSceneObjectsWithDescription(description, searchIteratively);
	}

	//
	// also make all the methods of a SceneObject available
	//

	@Override
	public RaySceneObjectIntersection getClosestRayIntersection(Ray ray)
	{
		return sceneObjectContainer.getClosestRayIntersection(ray);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray)
	{
		return sceneObjectContainer.getClosestRayIntersectionWithShadowThrowingSceneObject(ray);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		return sceneObjectContainer.getClosestRayIntersectionAvoidingOrigin(ray, originObject);
	}

	@Override
	public RaySceneObjectIntersection getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(Ray ray, SceneObjectPrimitive originObject)
	{
		return sceneObjectContainer.getClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject);
	}

	@Override
	public EditableSceneObjectCollection transform(Transformation t)
	{
		return new EditableSceneObjectCollection(sceneObjectContainer.transform(t), isCombinationModeEditable());
	}

	@Override
	public boolean insideObject(Vector3D p)
	{
		return sceneObjectContainer.insideObject(p);
	}

	@Override
	public DoubleColour getColour(Ray ray, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		return sceneObjectContainer.getColour(ray, l, scene, traceLevel, raytraceExceptionHandler);
	}

	@Override
	public DoubleColour getColourAvoidingOrigin(Ray ray,
			SceneObjectPrimitive originObject, LightSource l, SceneObject scene, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		return sceneObjectContainer.getColourAvoidingOrigin(ray, originObject, l, scene, traceLevel, raytraceExceptionHandler);
	}

	@Override
	public String getDescription()
	{
		return sceneObjectContainer.getDescription();
	}

	@Override
	public void setDescription(String description)
	{
		sceneObjectContainer.setDescription(description);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersection(Ray ray,
			RaySceneObjectIntersection i)
	{
		return sceneObjectContainer.getNextClosestRayIntersection(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObject(Ray ray,
			RaySceneObjectIntersection i)
	{
		return sceneObjectContainer.getNextClosestRayIntersectionWithShadowThrowingSceneObject(ray, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionAvoidingOrigin(
			Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i)
	{
		return sceneObjectContainer.getNextClosestRayIntersectionAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public RaySceneObjectIntersection getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(
			Ray ray, SceneObjectPrimitive originObject, RaySceneObjectIntersection i)
	{
		return sceneObjectContainer.getNextClosestRayIntersectionWithShadowThrowingSceneObjectAvoidingOrigin(ray, originObject, i);
	}

	@Override
	public ArrayList<SceneObjectPrimitive> getSceneObjectPrimitives()
	{
		return sceneObjectContainer.getSceneObjectPrimitives();
	}

	@Override
	public Studio getStudio() {
		return sceneObjectContainer.getStudio();
	}
	
	@Override
	public void setStudio(Studio studio)
	{
		sceneObjectContainer.setStudio(studio);
	}
	
	@Override
	public SceneObject getParent() {
		return parent;
	}

	@Override
	public void setParent(SceneObject parent) {
		this.parent = parent;
	}

	@Override
	public String toString()
	{
          return "<EditableSceneObjectCollection \""+
          	sceneObjectContainer.getDescription() + "\", " +
          	getNumberOfSceneObjects() + " objects>";
          // return description;
	}
	
	@Override
	public String getType()
	{
		return "Collection";
	}
}
