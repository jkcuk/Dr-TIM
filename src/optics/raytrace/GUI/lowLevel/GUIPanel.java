package optics.raytrace.GUI.lowLevel;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import optics.raytrace.GUI.cameras.*;
import optics.raytrace.GUI.core.ComponentWithButtonsPanel;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.core.RaytraceWorker;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableSphericalCap;
import optics.raytrace.core.CameraClass;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;

/**
 * This panel displays the rendered scene from different view points, as well as
 * all of the graphical side of the user interface.
 */
public class GUIPanel extends JPanel implements IPanelComponent, ComponentWithButtonsPanel, RenderPanel
{
	private static final long serialVersionUID = 3188650363358627125L;

	private boolean allowSaving = false;

	private Studio studio;	// holds all the information about scene, lights, current camera (which is called "eye" here)
	private ArrayList<CameraClass> cameras;	// holds all the different cameras
	
	private RaytraceWorker raytraceWorker;
	private int indexOfViewBeingRendered;

	/**
	 * Sometimes the eye is shown in the rendered image.
	 * This object is the eye pupil; it allows the pupil size to be set to the actual pupil size.
	 */
	private EditableSphericalCap eyePupil;

	private JTabbedPane imagesPane;
	private ArrayList<RaytracingImageCanvas> raytracingImageCanvases;
	private MainButtonsPanel mainButtonsPanel;
	private IPanel iPanel;


	public GUIPanel(Studio studio, ArrayList<CameraClass> cameras, int imageCanvasSizeX, int imageCanvasSizeY, boolean allowSaving, IPanel iPanel)
	{
		super();
		setLayout(new BorderLayout());

		this.studio = studio;
		this.cameras = cameras;
		this.allowSaving = allowSaving;
		this.iPanel = iPanel;

		imagesPane = new JTabbedPane();
		add(imagesPane, BorderLayout.CENTER);
		raytracingImageCanvases = new ArrayList<RaytracingImageCanvas>();
		
		for(int i=0; i<cameras.size(); i++)
		{
			// the area in which the rendered image will be shown
			RaytracingImageCanvas bufferedImageCanvas = new RaytracingImageCanvas(imageCanvasSizeX, imageCanvasSizeY, iPanel, studio, iPanel);
			bufferedImageCanvas.setImage(cameras.get(i).getPhoto());

			raytracingImageCanvases.add(bufferedImageCanvas);
			imagesPane.addTab(cameras.get(i).getDescription(), bufferedImageCanvas);
		}

		// make a RenderPanel
		// first make a panel with the buttons that goes into the renderPanel
		mainButtonsPanel = new MainButtonsPanel();
		// buttons now get added elsewhere, namely in the iPanel
		// add(mainButtonsPanel, BorderLayout.SOUTH);

		validate();
	}
	
	/**
	 * @return the camera for the currently selected tab
	 */
	public CameraClass getSelectedCamera()
	{
		return cameras.get(imagesPane.getSelectedIndex());
	}
	
	public void setSelectedCamera(CameraClass camera)
	{
		cameras.set(imagesPane.getSelectedIndex(), camera);
	}

	/**
	 * @return the BufferedImageCanvas in the currently selected tab
	 */
	public BufferedImageCanvas getSelectedBufferedImageCanvas()
	{
		return raytracingImageCanvases.get(imagesPane.getSelectedIndex());
	}

	/**
	 * set the image in the currently selected tab and refresh
	 * @param image
	 */
	public void setSelectedImage(BufferedImage image)
	{
		getSelectedBufferedImageCanvas().setImage(image);
	}

	/**
	 * set the image in the currently selected tab to be the photo from the studio
	 */
	public void setSelectedImage()
	{
		studio.setCamera(getSelectedCamera());
		setSelectedImage(studio.getPhoto());
	}
	
	@Override
	public void setRenderedImage(BufferedImage image)
	{
		raytracingImageCanvases.get(indexOfViewBeingRendered).setImage(studio.getPhoto());
	}

	public boolean isAllowSaving() {
		return allowSaving;
	}

	public void setAllowSaving(boolean allowSaving) {
		this.allowSaving = allowSaving;
	}

	public void setEyePupil(EditableSphericalCap eyePupil) {
		this.eyePupil = eyePupil;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.lowLevel.RenderPanel#render()
	 */
	@Override
	public void render()
	{
		iPanel.setStatus("Rendering...");

		// any image that's currently in memory will be overwritten, so saving is
		// possible again only once rendering has finished
		if(allowSaving) mainButtonsPanel.getSaveImageButton().setEnabled(false);

		// disable edit buttons; will be enabled again in the renderingDone() method
		mainButtonsPanel.setEditButtonsEnabled(false);

		mainButtonsPanel.getRenderStopButton().setText("Stop");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		indexOfViewBeingRendered = imagesPane.getSelectedIndex();
		studio.setCamera(getSelectedCamera());

		//Instances of javax.swing.SwingWorker are not reusuable, so
		//we create new instances as needed.
		raytraceWorker = new RaytraceWorker(studio, this, iPanel);
		raytraceWorker.execute();
	}

	@Override
	public void renderingDone(boolean wasCancelled)
	{
		setEditButtonsEnabled(true);
		setCursor(null);	// turn off the wait cursor
		// stopButton.setEnabled(false);
		mainButtonsPanel.getRenderStopButton().setText("Render");
		if(allowSaving) mainButtonsPanel.getSaveImageButton().setEnabled(!wasCancelled);
		indexOfViewBeingRendered = -1;
	}

	/**
	 * An internal class that deals with the buttons for editing the scene,
	 * for editing the camera, and for rendering
	 * 
	 * @author Johannes Courtial
	 */
	class MainButtonsPanel extends ButtonsPanel implements ActionListener
	{
		private static final long serialVersionUID = -6581307895200977457L;

		private IPanelComponent iPanelComponent;
		private JButton editSceneButton, editCameraButton, renderStopButton, saveImageButton;
		private JFileChooser fileChooser;

		public MainButtonsPanel()
		{
			super();

			editSceneButton = addButton("Edit scene");
			editSceneButton.addActionListener(this);

			editCameraButton = addButton("Edit view");
			editCameraButton.addActionListener(this);

			if(allowSaving)
			{
				saveImageButton = addButton("Save image");
				saveImageButton.addActionListener(this);
				saveImageButton.setEnabled(false);	// there isn't an image yet to save

				//Create a file chooser
				fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save photo in .bmp format");
			}
			
			renderStopButton = addButton("Render");
			renderStopButton.addActionListener(this);
			// this.getRootPane().setDefaultButton(renderStopButton);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("Render"))
			{
				render();
			}
			else if(e.getActionCommand().equals("Stop"))
			{
				raytraceWorker.cancel(true);
			}
			else if(e.getActionCommand().equals("Edit scene"))
			{
				SceneObject sceneObject = studio.getScene();
				if(sceneObject instanceof IPanelComponent)
				{
					iPanelComponent = (IPanelComponent)sceneObject;

					iPanel.addFrontComponent(iPanelComponent, "Edit scene");

					if(iPanelComponent instanceof EditableSceneObjectCollection)
					{
						// when editing the scene, don't allow the objects to be intersected etc.
						// ((EditableSceneObjectCollection)iPanelComponent).setCombinationModePanelVisible(false);
						
						// also, do allow the scene to be initialised
						((EditableSceneObjectCollection)iPanelComponent).setInitialisationComboBoxVisible(true);
					}
					iPanelComponent.setValuesInEditPanel();
				}
				else
				{
					System.out.println("SceneObject " + sceneObject + " not editable!");
				}
			}
			else if(e.getActionCommand().equals("Edit view"))
			{
				iPanelComponent = (IPanelComponent)getSelectedCamera();

				if(iPanelComponent instanceof NeedsSceneSetBeforeEditing)
				{
					// the camera needs to know the scene in case the user asks it to focus on it
					((NeedsSceneSetBeforeEditing)iPanelComponent).setScene((EditableSceneObjectCollection)(studio.getScene()));
				}

				iPanel.addFrontComponent(iPanelComponent, "Edit view");

				iPanelComponent.setValuesInEditPanel();
			}
			else if(e.getActionCommand().equals("Save image"))
			{
				int returnVal = fileChooser.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// the user has pressed the "OK" button
					getSelectedCamera().savePhoto(fileChooser.getSelectedFile().getPath(), "bmp");
				}
			}
		}

		public void setEditButtonsEnabled(boolean enabled)
		{
			editSceneButton.setEnabled(enabled);
			editCameraButton.setEnabled(enabled);
		}

		public JButton getRenderStopButton()
		{
			return renderStopButton;
		}

		public JButton getSaveImageButton()
		{
			return saveImageButton;
		}
	}

	public void setEditButtonsEnabled(boolean enabled)
	{
		mainButtonsPanel.setEditButtonsEnabled(enabled);
	}
	
	@Override
	public void createEditPanel(IPanel iPanel)
	{
		// nothing needs doing here; the panel is itself, so it already exists
	}

	@Override
	public void discardEditPanel()
	{
		// nothing needs doing here; the panel is itself
	}


	@Override
	public JPanel getEditPanel()
	{
		return this;
	}

	@Override
	public GUIPanel acceptValuesInEditPanel() {
		return this;
	}

	@Override
	public void setValuesInEditPanel() {
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		// update the pupil size, if necessary
		if(edited instanceof EditableRelativisticAnyFocusSurfaceCamera)
		{
			if(eyePupil != null)
			{
				double pupilRadius = ((EditableRelativisticAnyFocusSurfaceCamera)edited).getApertureRadius();
				eyePupil.setApertureRadius(pupilRadius);
				eyePupil.refreshSceneObjects();
			}
		}

		iPanel.setStatus("Ready to render.");
	}

	@Override
	public GUIPanel clone()
	{
		return this;
	}

	@Override
	public ButtonsPanel getButtonsPanel() {
		return mainButtonsPanel;
	}
	
	@Override
	public JButton getDefaultButton()
	{
		return mainButtonsPanel.getRenderStopButton();
	}
}


