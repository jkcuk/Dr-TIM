package optics.raytrace.GUI.lowLevel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.core.SceneObjectType;
import optics.raytrace.GUI.lowLevel.ButtonsPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.*;

/**
 * A panel for controlling lists of scene objects.
 *
 * @author Johannes Courtial
 *
 */
public class SceneObjectListPanel extends JPanel
implements ListSelectionListener
{	
	private static final long serialVersionUID = 1460488888808510830L;
	
	/**
	 * the scene-object container this SceneObjectListPanel is editing
	 */
	protected SceneObjectContainer sceneObjectContainer;
	
	// this should be part of an EditableSceneObjectCollection's (or similar) editPanel;
	// that EditableSceneObjectCollection is that enclosingSceneObject
	protected SceneObject enclosingSceneObject;
	
	// the IPanel in which all the editing happens
	protected IPanel iPanel;

	// GUI components and related stuff
	protected JPanel listPanel;
	private JList<String> sceneObjectsList;
	private DefaultListModel<String> sceneObjectsListModel;	// contains the data in sceneObjectsList
	private JComboBox<Object> newObjectTypeComboBox;
	private static String NEW_OBJECT_STRING = "Create new...";
	private SceneObjectControlPanel sceneObjectControlPanel;

	// constructor
	public SceneObjectListPanel(String borderTitle, SceneObjectContainer sceneObjectContainer, SceneObject enclosingSceneObject, IPanel iPanel)
	{
		super();

		this.sceneObjectContainer = sceneObjectContainer;
		this.enclosingSceneObject = enclosingSceneObject;
		this.iPanel = iPanel;
		
		setLayout(new MigLayout("insets 0"));
		// add a border to the list panel
		setBorder(GUIBitsAndBobs.getTitledBorder(borderTitle));

		sceneObjectsListModel = new DefaultListModel<String>();
		sceneObjectsList = new JList<String>(sceneObjectsListModel);
		sceneObjectsList.addListSelectionListener(this);
		// sceneObjectsList.setCellRenderer(new SceneObjectListCellRenderer());
		// sceneObjectsList.setPrototypeCellValue("Index 1234567890");
		sceneObjectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceneObjectsList.setLayoutOrientation(JList.VERTICAL);
		sceneObjectsList.setVisibleRowCount(10); // display the maximum number of items possible in the available space
		
		JScrollPane sceneObjectsListScroller = new JScrollPane(sceneObjectsList);
		// sceneObjectsListScroller.setPreferredSize(new Dimension(250, 150));
		add(sceneObjectsListScroller, "span");
		// add(sceneObjectsList, "span");

		// add a bit of (non-stretchable) space
		// sceneObjectsPanel.add(Box.createRigidArea(new Dimension(10,5)));

		//
		// the Edit and Remove buttons
		//

		sceneObjectControlPanel = new SceneObjectControlPanel();
		add(sceneObjectControlPanel, "wrap");
		sceneObjectsList.addMouseListener(sceneObjectControlPanel);

		//
		// the New selector and button
		//

		newObjectTypeComboBox = new JComboBox<Object>(SceneObjectType.values());
		newObjectTypeComboBox.addItem(NEW_OBJECT_STRING);
		newObjectTypeComboBox.setSelectedItem(NEW_OBJECT_STRING);
		newObjectTypeComboBox.setActionCommand("New");
		newObjectTypeComboBox.addActionListener(sceneObjectControlPanel);
		add(newObjectTypeComboBox);

		validate();
	}
	
	public SceneObjectListPanel(SceneObjectContainer sceneObjectContainer, SceneObject enclosingSceneObject, IPanel iPanel)
	{
		this("Scene objects", sceneObjectContainer, enclosingSceneObject, iPanel);
	}


	// getters and setters
	
	public SceneObjectContainer getSceneObjectContainer() {
		return sceneObjectContainer;
	}

	public void setSceneObjectContainer(SceneObjectContainer sceneObjectContainer) {
		this.sceneObjectContainer = sceneObjectContainer;
		
		initializeSceneObjectsListModel();
		
		// update the state of the buttons
		sceneObjectControlPanel.enableOrDisableItemButtons();
	}

	/**
	 * Call from within enclosing IPanelComponent's backToFront method
	 * @param edited	the SceneObject that has just been edited
	 */
	public void sceneObjectEditingCompleted(SceneObject edited)
	{
		sceneObjectControlPanel.setEditedSceneObject(edited);
		
		// update the list of the scene objects
		initializeSceneObjectsListModel();
		sceneObjectsList.ensureIndexIsVisible(sceneObjectsList.getSelectedIndex());
	}

	//This method is required by ListSelectionListener.
	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting() == false)
		{
			sceneObjectControlPanel.enableOrDisableItemButtons();
		}
	}

	private void initializeSceneObjectsListModel()
	{
		// initialize the list
		sceneObjectsListModel.clear();
		for(int i=0; i<sceneObjectContainer.getNumberOfSceneObjects(); i++)
		{
			SceneObject o = sceneObjectContainer.getSceneObject(i);
			sceneObjectsListModel.addElement(o.getDescription() + ((o instanceof IPanelComponent)?"":" [not editable]"));	// toString()
		}
	}

	/**
	 * An internal class that deals with the buttons for altering the component list.
	 * It also deals with double-clicking an element of the list of SceneObjects.
	 * 
	 * @author Johannes Courtial
	 */
	class SceneObjectControlPanel extends ButtonsPanel implements ActionListener, MouseListener, ItemListener
	{
		private static final long serialVersionUID = 2180222784613224166L;

		private JCheckBox visibleCheckBox;
		private JButton editButton, duplicateButton, removeButton;

		private boolean isNew;

		public SceneObjectControlPanel()
		{
			super();
			
			visibleCheckBox = new JCheckBox("Visible");
			visibleCheckBox.addItemListener(this);
			add(visibleCheckBox);

			editButton = addButton("Edit");
			editButton.addActionListener(this);

			duplicateButton = addButton("Duplicate");
			duplicateButton.addActionListener(this);
			
			removeButton = addButton("Remove");
			removeButton.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			IPanelComponent iPanelComponent = null;
			
			if(e.getActionCommand().equals("Remove"))
			{
				// find the index of the selected object
				int selectedIndex = sceneObjectsList.getSelectedIndex();

				// remove the selected object
				sceneObjectContainer.removeSceneObject(sceneObjectContainer.getSceneObject(selectedIndex));

				int size = sceneObjectContainer.getNumberOfSceneObjects();

				if (size == 0)
				{
					initializeSceneObjectsListModel();
					sceneObjectsList.setSelectedIndex(-1);

					enableOrDisableItemButtons();
				}
				else
				{ //Select an index.
					if (selectedIndex == sceneObjectContainer.getNumberOfSceneObjects()) {
						//removed item in last position
						selectedIndex--;
					}

					// update the list of the scene objects
					initializeSceneObjectsListModel();
					sceneObjectsList.setSelectedIndex(selectedIndex);
					sceneObjectsList.ensureIndexIsVisible(selectedIndex);
				}
			}
			else if(e.getActionCommand().equals("Edit"))
			{
				editSelectedSceneObject(sceneObjectsList.getSelectedIndex());
			}
			else if(e.getActionCommand().equals("New") && (newObjectTypeComboBox.getSelectedItem() instanceof SceneObjectType))
			{
				SceneObjectType sceneObjectType = (SceneObjectType)(newObjectTypeComboBox.getSelectedItem());
				
				newObjectTypeComboBox.setSelectedItem(NEW_OBJECT_STRING);	// show the "Create new..." text again

				iPanelComponent = (IPanelComponent)(SceneObjectType.getDefaultSceneObject(sceneObjectType, enclosingSceneObject, getStudio()));
				
				isNew = true;

				iPanel.addFrontComponent(iPanelComponent, "Edit new component");
				iPanelComponent.setValuesInEditPanel();
				
				// replace the container's list panel with the component's edit panel
				// setEditPanelComponent(iPanelComponent.getEditPanel(iPanel));
			}
			else if(e.getActionCommand().equals("Duplicate"))
			{
				// copy scene object
				SceneObject copiedSceneObject = sceneObjectContainer.getSceneObject(sceneObjectsList.getSelectedIndex()).clone();
				
				// add "copy of" to the description
				copiedSceneObject.setDescription("copy of " + copiedSceneObject.getDescription());
				
				iPanelComponent = (IPanelComponent)copiedSceneObject;
				
				isNew = true;
				
				iPanel.addFrontComponent(iPanelComponent, "Edit duplicated component");
				iPanelComponent.setValuesInEditPanel();

				// replace the container's list panel with the component's edit panel
				// setEditPanelComponent(iPanelComponent.getEditPanel(iPanel));
			}
		}
		
		/**
		 * If no item is selected in the list of scene objects, disable the Edit and Remove buttons,
		 * otherwise enable them.
		 */
		private void enableOrDisableItemButtons()
		{
			int selectedIndex = sceneObjectsList.getSelectedIndex();
			if (selectedIndex == -1)
			{
				// No selection, disable relevant buttons
				editButton.setEnabled(false);
				duplicateButton.setEnabled(false);
				removeButton.setEnabled(false);
				visibleCheckBox.setEnabled(false);
			}
			else
			{
				editButton.setEnabled(sceneObjectContainer.getSceneObject(selectedIndex) instanceof IPanelComponent);
				duplicateButton.setEnabled(sceneObjectContainer.getSceneObject(selectedIndex) instanceof IPanelComponent);
				removeButton.setEnabled(true);
				visibleCheckBox.setEnabled(true);
				
				visibleCheckBox.setSelected(SceneObjectListPanel.this.sceneObjectContainer.isSceneObjectVisible(selectedIndex));
			}
		}

		private void editSelectedSceneObject(int selectedIndex)
		{
			// edit the selected component
	
			// find the selected component
			IPanelComponent iPanelComponent = (IPanelComponent)(sceneObjectContainer.getSceneObject(selectedIndex));
			
			isNew = false;
			
			iPanel.addFrontComponent(iPanelComponent, "Edit scene object");			
			iPanelComponent.setValuesInEditPanel();
		}
		
		private void setEditedSceneObject(SceneObject editedSceneObject)
		{
			// set the studio in all objects
			editedSceneObject.setStudio(getStudio());
			
			int index = sceneObjectsList.getSelectedIndex(); //get selected index
			
			if(isNew)
			{
				// the object is new and needs to be added to the list of scene objects
				
				// calculate the index where the new object should be added
				if (index == -1) { //no selection, so insert at end
					index = sceneObjectContainer.getNumberOfSceneObjects();
				} else {           //add after the selected item
					index++;
				}

				sceneObjectContainer.addSceneObject(index, editedSceneObject);
			}
			else
			{
				sceneObjectContainer.setSceneObject(index, editedSceneObject);
			}
			
			sceneObjectsList.setSelectedIndex(index);
		}
		
		public void setSceneObjectVisible(boolean isVisible)
		{
			visibleCheckBox.setSelected(isVisible);
		}

		public boolean isSceneObjectVisible()
		{
			return visibleCheckBox.isSelected();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int index = sceneObjectsList.locationToIndex(e.getPoint());
				if(sceneObjectContainer.getSceneObject(index) instanceof IPanelComponent)
				{
					// the scene object is editable
					editSelectedSceneObject(index);
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void itemStateChanged(ItemEvent e)
		{
			sceneObjectContainer.getVisibilities().set(
					sceneObjectsList.getSelectedIndex(),
					visibleCheckBox.isSelected()
			);
		}
	}
	
	public Studio getStudio()
	{
		return enclosingSceneObject.getStudio();
	}
}
