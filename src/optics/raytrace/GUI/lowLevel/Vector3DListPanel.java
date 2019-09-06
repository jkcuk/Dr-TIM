package optics.raytrace.GUI.lowLevel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.ButtonsPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.sceneObjects.EditableArray;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableComplexThinLens;
import optics.raytrace.GUI.sceneObjects.EditableCylinderFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableEatonLens;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsPinchTransformationWindow;
import optics.raytrace.GUI.sceneObjects.EditableFramedGCLAMappingGoggles;
import optics.raytrace.GUI.sceneObjects.EditableFramedWindow;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsCloak;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsTardisWindow;
import optics.raytrace.GUI.sceneObjects.EditableGlens;
import optics.raytrace.GUI.sceneObjects.EditableLens;
import optics.raytrace.GUI.sceneObjects.EditableLuneburgLens;
import optics.raytrace.GUI.sceneObjects.EditableMaxwellFisheyeLens;
import optics.raytrace.GUI.sceneObjects.EditableObjectCoordinateSystem;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedParaboloid;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditablePolarToCartesianConverter;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryCone;
import optics.raytrace.GUI.sceneObjects.EditableGGRINLens;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryHyperboloid;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableSphericalCap;
import optics.raytrace.GUI.sceneObjects.EditableTelescope;
import optics.raytrace.GUI.sceneObjects.EditableThinLens;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.*;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;

/**
 * A panel for controlling lists of 3D vectors.
 *
 * @author Johannes Courtial
 *
 */
public class Vector3DListPanel extends JPanel
implements ListSelectionListener
{	
	// the list of 3D vectors being edited
	protected ArrayList<Vector3D> vectors;

	// the IPanel in which all the editing happens
	protected IPanel iPanel;

	// GUI components and related stuff
	protected JPanel listPanel;
	private JList<Vector3D> vectorList;
	private DefaultListModel<Vector3D> vectorListModel;	// contains the data in vectorList
	private ListCellRenderer<Vector3D> vectorRenderer;
	private Vector3DListControlPanel vectorListControlPanel;

	// constructor
	public Vector3DListPanel(String borderTitle, ArrayList<Vector3D> vectors, IPanel iPanel)
	{
		super();
		
		this.vectors = vectors;

		this.iPanel = iPanel;
		
		setLayout(new MigLayout("insets 0"));
		// add a border to the list panel
		setBorder(GUIBitsAndBobs.getTitledBorder(borderTitle));

		vectorListModel = new DefaultListModel();
		vectorList = new JList<Vector3D>(vectorListModel);
		vectorRenderer = new Vector3DRenderer();
		vectorList.setCellRenderer(vectorRenderer);
		vectorList.addListSelectionListener(this);
		// sceneObjectsList.setCellRenderer(new SceneObjectListCellRenderer());
		// sceneObjectsList.setPrototypeCellValue("Index 1234567890");
		vectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		vectorList.setLayoutOrientation(JList.VERTICAL);
		vectorList.setVisibleRowCount(10); // display the maximum number of items possible in the available space
		
		JScrollPane sceneObjectsListScroller = new JScrollPane(vectorList);
		// sceneObjectsListScroller.setPreferredSize(new Dimension(250, 150));
		add(sceneObjectsListScroller, "span");
		// add(sceneObjectsList, "span");

		// add a bit of (non-stretchable) space
		// sceneObjectsPanel.add(Box.createRigidArea(new Dimension(10,5)));

		//
		// the Edit and Remove buttons
		//

		vectorListControlPanel = new Vector3DListControlPanel();
		add(vectorListControlPanel, "wrap");
		vectorList.addMouseListener(vectorListControlPanel);

		validate();
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

	private void initializeVectorListModel()
	{
		// initialize the list
		vectorListModel.clear();
		for(int i=0; i<vectors.size(); i++)
		{
			vectorListModel.addElement(vectors.get(i));
			// sceneObjectsListModel.addElement(o.getDescription() + ((o instanceof IPanelComponent)?"":" [not editable]"));	// toString()
		}
	}

	class Vector3DRenderer extends JPanel implements ListCellRenderer
    {
		public Vector3DRenderer()
		{
			setOpaque(true);
		}

/*
* This method finds the image and text corresponding
* to the selected value and returns the label, set up
* to display the text and image.
*/
public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
//Get the selected index. (The index param isn't
//always valid, so just use the value.)
int selectedIndex = ((Integer)value).intValue();

if (isSelected) {
setBackground(list.getSelectionBackground());
setForeground(list.getSelectionForeground());
} else {
setBackground(list.getBackground());
setForeground(list.getForeground());
}

//Set the icon and text.  If icon was null, say so.
ImageIcon icon = images[selectedIndex];
String pet = petStrings[selectedIndex];
setIcon(icon);
if (icon != null) {
setText(pet);
setFont(list.getFont());
} else {
setUhOhText(pet + " (no image available)",
     list.getFont());
}

return this;
}
. . .
}

	
	/**
	 * An internal class that deals with the buttons for altering the component list.
	 * It also deals with double-clicking an element of the list of SceneObjects.
	 * 
	 * @author Johannes Courtial
	 */
	class Vector3DListControlPanel extends ButtonsPanel implements ActionListener, MouseListener, ItemListener
	{
		private JButton duplicateButton, removeButton;

		private boolean isNew;

		public Vector3DListControlPanel()
		{
			super();
			
			duplicateButton = addButton("+");
			duplicateButton.addActionListener(this);
			
			removeButton = addButton("-");
			removeButton.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			IPanelComponent iPanelComponent = null;
			
			if(e.getActionCommand().equals("-"))
			{
				// find the index of the selected object
				int selectedIndex = vectorList.getSelectedIndex();

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
			else if(e.getActionCommand().equals("New"))
			{
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
				
				visibleCheckBox.setSelected(Vector3DListPanel.this.sceneObjectContainer.isSceneObjectVisible(selectedIndex));
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
}
