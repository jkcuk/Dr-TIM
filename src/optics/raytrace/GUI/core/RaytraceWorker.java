package optics.raytrace.GUI.core;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.SwingWorker;

import optics.raytrace.GUI.lowLevel.RenderPanel;
import optics.raytrace.GUI.lowLevel.StatusIndicator;
import optics.raytrace.core.RayWithTrajectory;
import optics.raytrace.core.Studio;


/**
 * 
 * @author Dean Lambert, Alasdair Hamilton, Johannes Courtial
 *
 */
public class RaytraceWorker extends SwingWorker<BufferedImage, BufferedImage> implements StatusIndicator
{
	private Studio studio;	// contains information about scene, lights and camera
	private RenderPanel renderPanel;	// where to put the image at the end
	private final StatusIndicator statusIndicator;	// where to write feedback
	private long startTime;
	private boolean traceRaysWithTrajectory;
	
	/**
	 * Create a new thread to render the image.  This requires a studio, 
	 * which contains the scene to be rendered, lights and camera as well as some user
	 * interface elements that should be kept up to date.
	 * 
	 * @param studio
	 * @param RayTracerPanel
	 * @param statusIndicator
	 * @param goButton
	 */
	public RaytraceWorker(Studio studio, RenderPanel renderPanel, StatusIndicator statusIndicator, boolean traceRaysWithTrajectory)
	{
		super();
		
		this.studio = studio;
		this.renderPanel = renderPanel;
		this.statusIndicator = statusIndicator;
		this.traceRaysWithTrajectory = traceRaysWithTrajectory;

		this.startTime = System.currentTimeMillis();
		// addPropertyChangeListener(rayTracerPanel);	// now added in RayTracerPanel
	}
	
	/**
	 * Create a new thread to render the image.  This requires a studio, 
	 * which contains the scene to be rendered, lights and camera as well as some user
	 * interface elements that should be kept up to date.
	 * 
	 * @param studio
	 * @param RayTracerPanel
	 * @param statusIndicator
	 * @param goButton
	 */
	public RaytraceWorker(Studio studio, RenderPanel renderPanel, StatusIndicator statusIndicator)
	{
		this(
				studio, renderPanel, statusIndicator,
				true	// traceRaysWithTrajectory
			);
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected BufferedImage doInBackground() throws Exception
	{
		if(traceRaysWithTrajectory)
		{
			statusIndicator.setStatus("Calculating trajectories of display rays...");
			RayWithTrajectory.traceRaysWithTrajectory(studio.getScene());
		}
		
		statusIndicator.setStatus("Rendering...");
		return studio.takePhoto(this);
	}

	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 */
	@Override
	protected void done()
	{
		/**
		 * When the render finishes, its thread calls this
		 * which updates the user interface's image.
		 */

		// display the image
		if(isCancelled()) statusIndicator.setStatus("Rendering stopped.");
		else
		{
			long
				millis = System.currentTimeMillis() - startTime,
				seconds = millis/1000;
			statusIndicator.setStatus("Done.  Rendering took "+ seconds + "." + (millis-seconds*1000+50)/100 + " s.");
			try {
				renderPanel.setRenderedImage( get() );
				renderPanel.repaint();
			} catch (Exception ignore) {
				System.out.println("-------------------------------------");
				System.out.println("- Exception in RenderWorker::done() -");
				System.out.println("-------------------------------------");
				ignore.printStackTrace();
				System.out.println("-------------------------------------");
			}
		}

		renderPanel.renderingDone(isCancelled());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#process(java.util.List)
	 */
	@Override
	protected void process(List<BufferedImage> images)
	{
		BufferedImage latestImage = images.get(images.size() - 1);
		renderPanel.setRenderedImage(latestImage);
	}

	/**
	 * display the status; whatever goes on in this method has to be thread safe!
	 * @param status a description of the current status, e.g. "Rendering..."
	 */
	@Override
	public void setStatus(String status)
	{
		statusIndicator.setStatus(status);
	}

	/*
	 * (non-Javadoc)
	 * @see optics.raytrace.GUI.panels.StatusIndicator#getStatus()
	 */
	@Override
	public String getStatus()
	{
		return statusIndicator.getStatus();
	}
	
	public void showIntermediateImage(BufferedImage image)
	{
		publish(image);
		// renderPanel.setRenderedImage(image);
	}
	
	/*
	 * sometimes it's handy to display something in the status line that overrides the "real" status,
	 * e.g. the coordinates over which the mouse is hovering
	 */
	@Override
	public void setTemporaryStatus(String temporaryStatus)
	{
		statusIndicator.setTemporaryStatus(temporaryStatus);
	}
	
	@Override
	public void removeTemporaryStatus()
	{
		statusIndicator.removeTemporaryStatus();
	}
	
	@Override
	public boolean isTemporaryStatus()
	{
		return statusIndicator.isTemporaryStatus();
	}

}