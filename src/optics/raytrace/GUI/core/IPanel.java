package optics.raytrace.GUI.core;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

import javax.swing.*;

import optics.raytrace.GUI.lowLevel.StatusIndicator;

/**
 * This panel tries to be a bit like the iPhone display.
 * Only one component is being edited at any one time.
 * At the top is a breadcrumbs list; in the centre is the component being edited.
 */
public class IPanel extends JPanel
implements StatusIndicator, ActionListener
{
	private static final long serialVersionUID = -5629218822433643131L;

	protected JPanel mainPanel, buttonsPanel;
	protected JButton OKButton;
	protected JLabel statusField;
	
	private int mainPanelSizeX, mainPanelSizeY;
	
	private String status, temporaryStatus;
	
	private Stack<String> titleStack;
	private Stack<IPanelComponent> mainPanelComponentStack;

	/**
	 */
	public IPanel(int mainPanelSizeX, int mainPanelSizeY)
	{
		super();
		setLayout(new BorderLayout());
		
		this.mainPanelSizeX = mainPanelSizeX;
		this.mainPanelSizeY = mainPanelSizeY;

		// initialise the component stack and the title stack
		mainPanelComponentStack = new Stack<IPanelComponent>();
		titleStack = new Stack<String>();
		
		addGUIComponents();
		setStatus("");
		removeTemporaryStatus();
		
		// add action listeners as required
		OKButton.addActionListener(this);

		// validate and repaint
		revalidate();
		repaint();
	}
	
	/**
	 * override to change layout
	 */
	protected void addGUIComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		// JScrollPane mainPanelScrollPane = new JScrollPane(mainPanel);
		Dimension mainPanelSize = new Dimension(mainPanelSizeX, mainPanelSizeY);
		// mainPanel.setSize(mainPanelSize);
		// mainPanel.setMinimumSize(mainPanelSize);
		// mainPanel.setMaximumSize(mainPanelSize);
		mainPanel.setPreferredSize(mainPanelSize);

		// JPanel centrePanel = new JPanel();
		// centrePanel.setSize(mainPanelSize);
		// centrePanel.setMinimumSize(mainPanelSize);
		// centrePanel.setMaximumSize(mainPanelSize);
		// centrePanel.setPreferredSize(mainPanelSize);
		// centrePanel.add(mainPanelScrollPane);
		// add(centrePanel, BorderLayout.CENTER);
		// add(mainPanelScrollPane, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		buttonsPanel = new JPanel();
		OKButton = new JButton("OK");
		buttonsPanel.add(OKButton);
		bottomPanel.add(buttonsPanel, BorderLayout.EAST);
		statusField = new JLabel();
		bottomPanel.add(statusField, BorderLayout.WEST);

		// This will get the operating system specific preferred height and then set
		// the minimum and maximum sizes based on this preferred height. This means
		// that whenever the window is resized, the button panel stays the same size --
		// its most natural size for the operating system -- at all times.
		int bottomPanelPreferredHeight = bottomPanel.getPreferredSize().height;
		
		bottomPanel.setMinimumSize(new Dimension(100, bottomPanelPreferredHeight));
		bottomPanel.setMaximumSize(new Dimension(10000, bottomPanelPreferredHeight));

		// bottomPanel.validate();
		
		add(bottomPanel, BorderLayout.SOUTH);

//		Dimension iPanelSize = new Dimension(RayTraceApplet.IMAGE_CANVAS_SIZE_X, RayTraceApplet.IMAGE_CANVAS_SIZE_Y+100);
//		setMinimumSize(iPanelSize);
//		// setMaximumSize(iPanelSize);
//		// setPreferredSize(iPanelSize);
//		setSize(iPanelSize);
	}

	/**
	 * set the status and refresh
	 * @param status
	 */
	@Override
	public void setStatus(String status)
	{
		this.status = status;
		
		// display the new status only if no (higher-priority) temporary status is displayed
		if(temporaryStatus == null) setStatusText(status);
	}
	
	/*
	 * (non-Javadoc)
	 * @see optics.raytrace.GUI.panels.StatusIndicator#getStatus()
	 */
	@Override
	public String getStatus()
	{
		return status;
	}
	
	/*
	 * sometimes it's handy to display something in the status line that overrides the "real" status,
	 * e.g. the coordinates over which the mouse is hovering
	 */
	@Override
	public void setTemporaryStatus(String temporaryStatus)
	{
		this.temporaryStatus = temporaryStatus;
		setStatusText(temporaryStatus);
	}
	
	@Override
	public void removeTemporaryStatus()
	{
		if(temporaryStatus != null)
		{
			temporaryStatus = null;
			setStatusText(status);
		}
	}
	
	@Override
	public boolean isTemporaryStatus()
	{
		return temporaryStatus != null;
	}

	private void setStatusText(String statusText)
	{
//		final int MAX_STATUS_LENGTH = 95;
//		
//		if(statusText.length() > MAX_STATUS_LENGTH)
//		{
//			// statusText = "..." + statusText.substring(statusText.length()-96);
//			statusText = statusText.substring(0, MAX_STATUS_LENGTH-5) + "...";
//		}
		statusField.setText(" "+statusText);
	}
	
	/**
	 * Places a new central panel over the current one.
	 * In a sense, the new centre panel is "in front of" the old one:
	 * the old central panel is stored, and becomes visible again when the
	 * new centre panel is removed using the removeFrontCentrePanel() method.
	 * @see removeFront()
	 * @param component
	 * @param title
	 */
	public void addFrontComponent(IPanelComponent component, String title)
	{
		// create the component's edit panel, if necessary
		if(component.getEditPanel() == null) component.createEditPanel(this);
		
		// store the title and the new component on the corresponding stacks
		titleStack.push(title);
		mainPanelComponentStack.push(component);
		
		setStatus("");
		
		// update the main panel to show the new component
		updateMainPanel();

		// update the breadcrumb panel to reflect the new stack
		updateRest();
	}
	
	/**
	 * Replaces the current central panel with a new one.
	 * @param component
	 * @param title
	 */
	public void replaceFrontComponent(IPanelComponent component, String title)
	{
		// create the component's edit panel
		if(component.getEditPanel() == null) component.createEditPanel(this);

		// replace the title and component on the corresponding stacks
		titleStack.pop();	// remove the old front title...
		titleStack.push(title);	// ... and replace it with the new one
		mainPanelComponentStack.pop().discardEditPanel();	// remove the old component, discard its edit panel, ...
		mainPanelComponentStack.push(component);	// ... and replace it with the new one
		
		// setStatus(title);
		
		// update the main panel to show the new component
		updateMainPanel();

		// update the breadcrumb panel to reflect the new stack
		updateRest();
	}

	public void removeFrontComponent()
	{
		IPanelComponent edited;
		
		titleStack.pop();
		
		// accept new values
		edited = mainPanelComponentStack.pop().acceptValuesInEditPanel();
		edited.discardEditPanel();
		// setStatus(titleStack.peek());
		
		// tell the new front component that it's back at the front
		mainPanelComponentStack.peek().backToFront(edited);
		
		// update the main panel to show the new component
		updateMainPanel();

		// update the breadcrumb panel to reflect the new stack
		updateRest();
	}
	
//	public IPanelComponent getNextToFrontComponent()
//	{
//		System.out.println("stack size = "+titleStack.size());
//		System.out.flush();
//		System.out.println("title = "+titleStack.get(titleStack.size()));
//		return mainPanelComponentStack.get(mainPanelComponentStack.size());
//	}
	
	public void mainPanelChanged()
	{
		revalidate();
		repaint();
//		mainPanel.revalidate();
//		mainPanel.repaint();
	}
	
	/**
	 * make the main panel reflect the centre stack
	 */
	private void updateMainPanel()
	{
		// remove any component(s) currently in the centrePanel, ...
		mainPanel.removeAll();
		// ... add the new component, ...
		JPanel editPanel = mainPanelComponentStack.peek().getEditPanel();
		// editPanel.validate();
		// editPanel.setMinimumSize(editPanel.getPreferredSize());
		// editPanel.setMaximumSize(editPanel.getPreferredSize());
		
		// add the editPanel in a JScrollPane
		mainPanel.add(new JScrollPane(editPanel), BorderLayout.CENTER);
		// mainPanel.revalidate();
		
		// ... and add a titled border
		// mainPanel.setBorder(GUIBitsAndBobs.getTitledBorder(titleStack.peek()));
	
		mainPanelChanged();
	}
	
	private void updateRest()
	{
		// check if we are at the top level
		if(titleStack.size() > 1)
		{
			// not top level
			
			// update "Back" button
			// OKButton.setText("<< " + titleStack.get(titleStack.size()-2));
			
			// update title label
			String s = titleStack.get(1);
			for(int i=2; i<titleStack.size(); i++) s += "  ==>  " + titleStack.get(i);
			setStatus(s);

			// show the relevant stuff
			OKButton.setVisible(true);
		}
		else
		{
			setStatus("Dr TIM by Johannes Courtial et al., University of Glasgow");
			// hide the relevant stuff
			// titleLabel.setVisible(false);
			OKButton.setVisible(false);
		}
		
		buttonsPanel.removeAll();
		buttonsPanel.add(OKButton);
		if(mainPanelComponentStack.peek() instanceof ComponentWithButtonsPanel)
		{
			buttonsPanel.add(((ComponentWithButtonsPanel)mainPanelComponentStack.peek()).getButtonsPanel());
			
			if(getRootPane() != null)
			{
				JButton defaultButton = ((ComponentWithButtonsPanel)mainPanelComponentStack.peek()).getDefaultButton();
				if(defaultButton != null) getRootPane().setDefaultButton(defaultButton);
				else getRootPane().setDefaultButton(OKButton);
			}
		}
		else
		{
			if(getRootPane() != null) getRootPane().setDefaultButton(OKButton);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		removeFrontComponent();
	}
}


