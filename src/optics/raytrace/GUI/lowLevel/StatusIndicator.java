package optics.raytrace.GUI.lowLevel;

public interface StatusIndicator
{
	/**
	 * display the status
	 * @param status a description of the current status, e.g. "Rendering..."
	 */
	public void setStatus(String status);
	
	public String getStatus();
	
	/*
	 * sometimes it's handy to display something in the status line that overrides the "real" status,
	 * e.g. the coordinates over which the mouse is hovering
	 */
	public void setTemporaryStatus(String temporaryStatus);
	
	public void removeTemporaryStatus();
	
	public boolean isTemporaryStatus();
}