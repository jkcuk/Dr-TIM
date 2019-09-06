package optics.raytrace.cameras.shutterModels;

/**
 * @author johannes
 *
 * Shutter model in which the shutter surface becomes transparent for one instant.
 */
public abstract class InstantShutterModel extends ShutterModel
{
	private static final long serialVersionUID = -6125842353976472416L;

	/**
	 * the time when the shutter surface becomes transparent
	 */
	protected double shutterOpeningTime;
	
	
	
	//
	// Constructors
	//
	
	/**
	 * Create an instance of the InstantShutterModel that represents a shutter opening at a specific time
	 * @param shutterOpeningTime	the shutter-opening time
	 */
	public InstantShutterModel(double shutterOpeningTime)
	{
		super();
		
		setShutterOpeningTime(shutterOpeningTime);
	}
	
	/**
	 * Create an instance of the InstantShutterModel that represents a shutter opening at time 0
	 */
	public InstantShutterModel()
	{
		this(0);
	}


	//
	// setters & getters
	//
	
	/**
	 * Get the shutter-opening time
	 * @return	the time when the shutter opens, i.e. when the surface becomes transparent
	 */
	public double getShutterOpeningTime() {
		return shutterOpeningTime;
	}


	/**
	 * Set the shutter-opening time
	 * @param shutterOpeningTime	the time when the shutter opens, i.e. when the surface becomes transparent
	 */
	public void setShutterOpeningTime(double shutterOpeningTime) {
		this.shutterOpeningTime = shutterOpeningTime;
	}
}
