package optics.raytrace.demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;


/**
 * Shows off how to get information about the last click from the RaytracingImageCanvas
 * 
 * @author Johannes Courtial
 */
public class LastClickDemo extends NonInteractiveTIMEngine implements ActionListener
{

	//
	// the rest of the scene
	//
	
	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;
	
	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LastClickDemo()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;

		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's LastClickDemo";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
	

	@Override
	public String getFirstPartOfFilename()
	{
		return "LastClickDemo";	// the name
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// super.writeParameters(printStream);
		
		printStream.println("renderQuality = "+renderQuality);

		printStream.println("studioInitialisation =" + studioInitialisation);		
	}


	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);
		
		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
			);
		

		// the camera
		EditableRelativisticAnyFocusSurfaceCamera camera = getStandardCamera();
		// camera.setApertureRadius(cameraApertureSize.getApertureRadius()/10.*cameraFocussingDistance);
		studio.setCamera(camera);
	}

	
	
	//
	// for interactive version
	//
	
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private JButton getLastClickIntersectionButton;
	private JTextArea lastClickIntersectionTextArea;
	


	
	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Background", studioInitialisationComboBox), "span");
		
		lastClickIntersectionTextArea = new JTextArea(20, 40);
		JScrollPane scrollPane = new JScrollPane(lastClickIntersectionTextArea); 
		lastClickIntersectionTextArea.setEditable(false);
		lastClickIntersectionTextArea.setText("Click on Update button to show info");
		getLastClickIntersectionButton = new JButton("Update");
		getLastClickIntersectionButton.addActionListener(this);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow(scrollPane, getLastClickIntersectionButton), "span");

	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(getLastClickIntersectionButton))
		{
			RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
			
			if(i != null)
				lastClickIntersectionTextArea.setText("Last click in image was with "+i.o.getDescription()+" at position "+i.p);
			else
				lastClickIntersectionTextArea.setText("No click so far...");
		}

	}


	//
	// the main method, so that this can be run as a Java application
	//

	/**
	 * Called when this is run; don't touch!
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new LastClickDemo()).run();
	}
}
