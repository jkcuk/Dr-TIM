package optics.raytrace.GUI.lowLevel;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.core.RaytraceWorker;
import optics.raytrace.slightlyInteractive.TIMsiBits;

/**
 * This panel displays the rendered scene and a button.
 * 
 * This is a cut-down version of GUIPanel
 */
public class GUIPanelSimple extends JPanel implements IPanelComponent, RenderPanel
{
	private static final long serialVersionUID = 4251017371205281456L;

	private boolean busy = false, allowSaving = false;

	private RaytraceWorker raytraceWorker;

	private RaytracingImageCanvas raytracingImageCanvas;
	private TheButtonsPanel buttonsPanel;
	private IPanel iPanel;
	
	private TIMsiBits interactiveBits;
	

	public GUIPanelSimple(TIMsiBits interactiveBits, int imageCanvasSizeX, int imageCanvasSizeY, boolean allowSaving, IPanel iPanel)
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.interactiveBits = interactiveBits;
		this.allowSaving = allowSaving;
		this.iPanel = iPanel;
		
		raytracingImageCanvas = new RaytracingImageCanvas(imageCanvasSizeX, imageCanvasSizeY, iPanel, interactiveBits.getStudio(), iPanel);
		raytracingImageCanvas.setImage(interactiveBits.getStudio().getCamera().getPhoto());
		add(raytracingImageCanvas);

		// add any fields that can be edited
		add(interactiveBits.createParametersPanel());
		interactiveBits.setValuesInParametersPanel();
		
		// add a panel with the Render button
		buttonsPanel = new TheButtonsPanel();
		add(buttonsPanel);

		validate();
		
		busy = false;
	}
	

	@Override
	public void setRenderedImage(BufferedImage image)
	{
		raytracingImageCanvas.setImage(interactiveBits.getStudio().getPhoto());
	}

	public boolean isBusy() {
		return busy;
	}


	public void setBusy(boolean busy) {
		this.busy = busy;
	}


	public boolean isAllowSaving() {
		return allowSaving;
	}

	public void setAllowSaving(boolean allowSaving) {
		this.allowSaving = allowSaving;
	}

	@Override
	public void render()
	{
		if(isBusy())
		{
			// cancel whatever the raytraceWorker is doing at the moment
			if(raytraceWorker != null) raytraceWorker.cancel(true);
		}

		setBusy(true);

		iPanel.setStatus("Creating scene, lights, camera...");

		// read in the values in the parameter fields...
		interactiveBits.acceptValuesInParametersPanel();

		// ... and create a studio from them
		interactiveBits.populateStudio();

		iPanel.setStatus("Rendering...");

		// any image that's currently in memory will be overwritten, so saving is
		// possible again only once rendering has finished
		if(allowSaving) buttonsPanel.getSaveImageButton().setEnabled(false);

		// disable edit buttons; will be enabled again in the renderingDone() method
		buttonsPanel.setEditButtonsEnabled(false);

		buttonsPanel.getRenderStopButton().setText("Stop");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		//Instances of javax.swing.SwingWorker are not reusuable, so
		//we create new instances as needed.
		raytraceWorker = new RaytraceWorker(interactiveBits.getStudio(), this, iPanel);
		raytraceWorker.execute();
	}

	@Override
	public void renderingDone(boolean wasCancelled)
	{
		setEditButtonsEnabled(true);
		setCursor(null);	// turn off the wait cursor
		// stopButton.setEnabled(false);
		buttonsPanel.getRenderStopButton().setText("Render");
		if(allowSaving) buttonsPanel.getSaveImageButton().setEnabled(!wasCancelled);
		setBusy(false);
	}

	/**
	 * An internal class that deals with any buttons
	 * 
	 * @author Johannes Courtial
	 */
	class TheButtonsPanel extends ButtonsPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;

		private JButton renderStopButton, saveImageButton;
		private JFileChooser fileChooser;

		public TheButtonsPanel()
		{
			super();

			renderStopButton = addButton("Render");
			renderStopButton.addActionListener(this);

			if(allowSaving)
			{
				saveImageButton = addButton("Save image");
				saveImageButton.addActionListener(this);
				saveImageButton.setEnabled(false);	// there isn't an image yet to save

				//Create a file chooser
				fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Save photo in .bmp format");
			}
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
			else if(e.getActionCommand().equals("Save image"))
			{
				int returnVal = fileChooser.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					// the user has pressed the "OK" button
					interactiveBits.getStudio().getCamera().savePhoto(fileChooser.getSelectedFile().getPath(), "bmp");
				}
			}
		}

		public void setEditButtonsEnabled(boolean enabled)
		{
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
		buttonsPanel.setEditButtonsEnabled(enabled);
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
	public GUIPanelSimple acceptValuesInEditPanel() {
		return this;
	}

	@Override
	public void setValuesInEditPanel() {
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		iPanel.setStatus("Ready to render.");
	}

	@Override
	public GUIPanelSimple clone()
	{
		return this;
	}
}


