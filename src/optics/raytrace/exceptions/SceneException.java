package optics.raytrace.exceptions;

/**
 * An exception thrown when the scene is impossible to set up, e.g. if there is something wrong with the geometry
 * @author johannes
 */
public class SceneException extends RayTraceException
{
	private static final long serialVersionUID = -4330821298074149865L;

	public SceneException(String message) {
		super(message);
	}
	
}
