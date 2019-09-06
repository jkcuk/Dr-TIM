package optics.raytrace.GUI.lowLevel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
 * A panel for controlling a table of scene objects.
 *
 * @author Johannes Courtial
 *
 */
public class SceneObjectTablePanel extends JPanel
implements ListSelectionListener
{	
	private static final long serialVersionUID = -6020770515072375152L;

	private enum ColumnType
	{
		RENDERING("Rendering", "Scene object is part of scene when rendering", Boolean.TRUE),
		TRAJECTORIES("Ray-trajectory tracing", "Scene object is part of scene when tracing ray trajectories", Boolean.TRUE),
		TYPE("Type", "Scene-object type", "Scene object"),
		DESCRIPTION("Description", "Description of the scene object", "Really very long object name, with description, and a bit more information for good measure");
		
		/**
		 * The column title (displayed at the top of the column)
		 */
		private String header;
		
		/**
		 * An example of the data for the column that is the basis for estimating the column's length
		 */
		private Object longExample;
		
		private String toolTip;
		
		private ColumnType(String header, String toolTip, Object longExample)
		{
			this.header = header;
			this.longExample = longExample;
			this.toolTip = toolTip;
		}
		
		public String getHeader() {return header;}
		
		public Class<? extends Object> getColumnClass() {return longExample.getClass();}
		
		public Object getLongExample() {return longExample;}
		
		@SuppressWarnings("unused")
		public String getToolTip() {return toolTip;}
	}
	

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
	private JTable sceneObjectsTable;
	private SceneObjectsTableModel sceneObjectsTableModel;	// contains the data in sceneObjectsTable
	private JComboBox<Object> newObjectTypeComboBox;
	private static String NEW_OBJECT_STRING = "Create new...";
	private SceneObjectControlPanel sceneObjectControlPanel;

	// constructor
	public SceneObjectTablePanel(String borderTitle, SceneObjectContainer sceneObjectContainer, SceneObject enclosingSceneObject, IPanel iPanel)
	{
		super();

		this.sceneObjectContainer = sceneObjectContainer;
		this.enclosingSceneObject = enclosingSceneObject;
		this.iPanel = iPanel;
		
		// setLayout(new MigLayout("insets 0"));
		setLayout(new BorderLayout());
		// add a border to the list panel
		setBorder(GUIBitsAndBobs.getTitledBorder(borderTitle));

		sceneObjectsTableModel = new SceneObjectsTableModel();
		sceneObjectsTable = new JTable(sceneObjectsTableModel);
		sceneObjectsTable.setPreferredScrollableViewportSize(new Dimension(700, 400));
		sceneObjectsTable.setFillsViewportHeight(true);
		if(sceneObjectContainer.getNumberOfSceneObjects() > 0) sceneObjectsTable.setRowSelectionInterval(sceneObjectContainer.getNumberOfSceneObjects()-1, sceneObjectContainer.getNumberOfSceneObjects()-1);
		initColumnSizes(sceneObjectsTable);
		sceneObjectsTableModel.fireTableDataChanged();
 
        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(sceneObjectsTable);
 
//        //Set up column sizes.
//        initColumnSizes(sceneObjectsTable);
// 
//        //Fiddle with the Sport column's cell editors/renderers.
//        setUpSportColumn(table, table.getColumnModel().getColumn(2));
 
        //Add the scroll pane to this panel.
        add(scrollPane, BorderLayout.CENTER);

		
//		sceneObjectsListModel = new DefaultListModel<String>();
//		sceneObjectsList = new JList<String>(sceneObjectsListModel);
//		sceneObjectsList.addListSelectionListener(this);
//		// sceneObjectsList.setCellRenderer(new SceneObjectListCellRenderer());
//		// sceneObjectsList.setPrototypeCellValue("Index 1234567890");
//		sceneObjectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		sceneObjectsList.setLayoutOrientation(JList.VERTICAL);
//		sceneObjectsList.setVisibleRowCount(10); // display the maximum number of items possible in the available space
//		
//		JScrollPane sceneObjectsListScroller = new JScrollPane(sceneObjectsList);
//		// sceneObjectsListScroller.setPreferredSize(new Dimension(250, 150));
//		add(sceneObjectsListScroller, "span");
//		// add(sceneObjectsList, "span");

		// add a bit of (non-stretchable) space
		// sceneObjectsPanel.add(Box.createRigidArea(new Dimension(10,5)));

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new MigLayout("insets 0"));

		//
		// the Edit and Remove buttons
		//

		sceneObjectControlPanel = new SceneObjectControlPanel();
		southPanel.add(sceneObjectControlPanel, "wrap");
		sceneObjectsTable.addMouseListener(sceneObjectControlPanel);

		//
		// the New selector and button
		//

		newObjectTypeComboBox = new JComboBox<Object>(SceneObjectType.values());
		newObjectTypeComboBox.addItem(NEW_OBJECT_STRING);
		newObjectTypeComboBox.setSelectedItem(NEW_OBJECT_STRING);
		newObjectTypeComboBox.setActionCommand("New");
		newObjectTypeComboBox.addActionListener(sceneObjectControlPanel);
		southPanel.add(newObjectTypeComboBox);
		
		add(southPanel, BorderLayout.SOUTH);

		validate();
	}
	
	public SceneObjectTablePanel(SceneObjectContainer sceneObjectContainer, SceneObject enclosingSceneObject, IPanel iPanel)
	{
		this("Scene objects", sceneObjectContainer, enclosingSceneObject, iPanel);
	}


	// getters and setters
	
	public SceneObjectContainer getSceneObjectContainer() {
		return sceneObjectContainer;
	}

	public void setSceneObjectContainer(SceneObjectContainer sceneObjectContainer)
	{
		this.sceneObjectContainer = sceneObjectContainer;
		
		sceneObjectsTableModel.fireTableDataChanged();
		// initializeSceneObjectsListModel();
		
		// update the state of the buttons
		sceneObjectControlPanel.enableOrDisableItemButtons();
	}
	
	
    private void initColumnSizes(JTable table)
    {
    	SceneObjectsTableModel model = (SceneObjectsTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
 
        for (int i = 0; i < model.getColumnCount(); i++)
        {
        	// setup column widths
            column = table.getColumnModel().getColumn(i);
 
            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
 
            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, model.columns[i].getLongExample(),
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;
 
            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
            
//            // set up a column renderer with an appropriate tooltip
//            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
//            renderer.setToolTipText(model.columns[i].getToolTip());
//            column.setCellRenderer(renderer);
        }
    }
    

	/**
	 * Call from within enclosing IPanelComponent's backToFront method
	 * @param edited	the SceneObject that has just been edited
	 */
	public void sceneObjectEditingCompleted(SceneObject edited)
	{
		sceneObjectControlPanel.setEditedSceneObject(edited);
		
		// update the list of the scene objects
		// initializeSceneObjectsListModel();
		// sceneObjectsTable.ensureIndexIsVisible(sceneObjectsTable.getSelectedRow());	// TODO
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
	

    class SceneObjectsTableModel extends AbstractTableModel
    {
		private static final long serialVersionUID = -6087386106489869090L;
		
		public ColumnType[] columns = {
				ColumnType.RENDERING,
				// ColumnType.TRAJECTORIES,	// TODO uncomment this when the functionality to trace rays only through part of the scene has been properly implemented
				ColumnType.TYPE,
				ColumnType.DESCRIPTION
			};
		
//		private String[] columnNames =
//			{
//				"Visible",
//				"Type",
//				"Description"
//			};
//		
//        public final Object[] longValues = {Boolean.TRUE, "Scene object", "Really very long object name, with description, and a bit more information for good measure, and some more"};
 
		@Override
        public int getColumnCount() {
			return columns.length;
        }
 
		@Override
        public int getRowCount() {
            return sceneObjectContainer.getNumberOfSceneObjects();
        }
 
		@Override
        public String getColumnName(int col) {
            return columns[col].getHeader();
        }
 
		@Override
        public Object getValueAt(int row, int col)
        {
        	switch(columns[col])
        	{
        	case RENDERING:	// visibility
        		return sceneObjectContainer.isSceneObjectVisible(row);
        	case TRAJECTORIES:
        		return sceneObjectContainer.isSceneObjectVisibleWhenTrajectoryTracing(row);
        	case TYPE:	// type
        		return sceneObjectContainer.getSceneObject(row).getType();
        	case DESCRIPTION:	// description
        	default:
        		SceneObject o = sceneObjectContainer.getSceneObject(row);
        		return o.getDescription() + ((o instanceof IPanelComponent)?"":" [not editable]");
        	}
        }
 
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
		@Override
        public Class<? extends Object> getColumnClass(int col)
        {
			return columns[col].getColumnClass();
        }
 
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
		@Override
        public boolean isCellEditable(int row, int col)
        {
    		switch(columns[col])
        	{
        	case RENDERING:	// visibility
        		return true;
        	case TRAJECTORIES:
        		return true;
        	case TYPE:	// type
        		return false;
        	case DESCRIPTION:	// description
        	default:
        		return true;
        	}
        }
 
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
		@Override
        public void setValueAt(Object value, int row, int col)
		{
    		switch(columns[col])
        	{
        	case RENDERING:	// visibility
        		sceneObjectContainer.setSceneObjectVisible(row, (boolean)value);
        		break;
        	case TRAJECTORIES:
        		sceneObjectContainer.setSceneObjectVisibleWhenTrajectoryTracing(row, (boolean)value);
        		break;
        	case TYPE:	// type
        		System.err.println("SceneObjectTablePanel:setValueAt: can't set scene-object type");
        		break;
        	case DESCRIPTION:	// description
        	default:
        		sceneObjectContainer.getSceneObject(row).setDescription((String)value);
        	}
            
            fireTableCellUpdated(row, col);
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

		private JButton editButton, duplicateButton, removeButton;

		private boolean isNew;

		public SceneObjectControlPanel()
		{
			super();
			
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
				int selectedIndex = sceneObjectsTable.getSelectedRow();

				// remove the selected object
				sceneObjectContainer.removeSceneObject(sceneObjectContainer.getSceneObject(selectedIndex));

				int size = sceneObjectContainer.getNumberOfSceneObjects();

				if (size == 0)
				{
					// TODO
//					initializeSceneObjectsListModel();
//					sceneObjectsList.setSelectedIndex(-1);

					enableOrDisableItemButtons();
				}
				else
				{ //Select an index.
					if (selectedIndex == sceneObjectContainer.getNumberOfSceneObjects()) {
						//removed item in last position
						selectedIndex--;
					}

					// update the list of the scene objects
					// TODO
//					initializeSceneObjectsListModel();
//					sceneObjectsList.setSelectedIndex(selectedIndex);
//					sceneObjectsList.ensureIndexIsVisible(selectedIndex);
				}
			}
			else if(e.getActionCommand().equals("Edit"))
			{
				editSelectedSceneObject(sceneObjectsTable.getSelectedRow());
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
				SceneObject copiedSceneObject = sceneObjectContainer.getSceneObject(sceneObjectsTable.getSelectedRow()).clone();
				
				// add "copy of" to the description
				copiedSceneObject.setDescription("copy of " + copiedSceneObject.getDescription());
				
				iPanelComponent = (IPanelComponent)copiedSceneObject;
				
				isNew = true;
				
				iPanel.addFrontComponent(iPanelComponent, "Edit duplicated component");
				iPanelComponent.setValuesInEditPanel();

				// replace the container's list panel with the component's edit panel
				// setEditPanelComponent(iPanelComponent.getEditPanel(iPanel));
			}
			
			sceneObjectsTableModel.fireTableDataChanged();
			enableOrDisableItemButtons();
		}
		
		/**
		 * If no item is selected in the list of scene objects, disable the Edit and Remove buttons,
		 * otherwise enable them.
		 */
		private void enableOrDisableItemButtons()
		{
			int selectedIndex = sceneObjectsTable.getSelectedRow();
			if (selectedIndex == -1)
			{
				// No selection, disable relevant buttons
				editButton.setEnabled(false);
				duplicateButton.setEnabled(false);
				removeButton.setEnabled(false);
			}
			else
			{
				editButton.setEnabled(sceneObjectContainer.getSceneObject(selectedIndex) instanceof IPanelComponent);
				duplicateButton.setEnabled(sceneObjectContainer.getSceneObject(selectedIndex) instanceof IPanelComponent);
				removeButton.setEnabled(true);
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
			
			int index = sceneObjectsTable.getSelectedRow(); //get selected index
			
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
			sceneObjectsTable.changeSelection(index, 0, true, true);
		}
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
			int row = sceneObjectsTable.rowAtPoint(e.getPoint());
			// sceneObjectsTable.setRowSelectionInterval(row, row);

			switch(e.getClickCount())
			{
			case 2:	// double click
				if(sceneObjectContainer.getSceneObject(row) instanceof IPanelComponent)
				{
					// the scene object is editable
					editSelectedSceneObject(row);
				}
				break;
			case 1:	// single click
			default:
			}
			
			enableOrDisableItemButtons();
			// sceneObjectsTableModel.fireTableDataChanged();
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
			// TODO
//			sceneObjectContainer.getVisibilities().set(
//					sceneObjectsTable.getSelectedRow(),
//					visibleCheckBox.isSelected()
//			);
		}
	}
	
	public Studio getStudio()
	{
		return enclosingSceneObject.getStudio();
	}
}
