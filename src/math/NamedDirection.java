package math;

/**
 * A 3D vector with a name for its forwards and backwards direction (e.g. "right" and "left").
 * @author johannes
 */
public class NamedDirection extends Vector3D
{
	private static final long serialVersionUID = 4118508510893346797L;

	/**
	 * name of the forwards direction, e.g. "Right"
	 */
	protected String forwardsName;

	/**
	 * name of the backwards direction, e.g. "Left"
	 */
	protected String backwardsName;

	
	// constructors
	
	public NamedDirection(String forwardsName, String backwardsName, Vector3D vector)
	{
		super(vector);

		this.forwardsName = forwardsName;
		this.backwardsName = backwardsName;
	}
	
	public NamedDirection(String forwardsName, String backwardsName, double x, double y, double z)
	{
		super(x, y, z);
		
		this.forwardsName = forwardsName;
		this.backwardsName = backwardsName;
	}

	
	
	// getters & setters
	
	public String getForwardsName() {
		return forwardsName;
	}

	public void setForwardsName(String forwardsName) {
		this.forwardsName = forwardsName;
	}

	public String getName() {
		return forwardsName;
	}

	public void setName(String forwardsName) {
		this.forwardsName = forwardsName;
	}

	public String getBackwardsName() {
		return backwardsName;
	}

	public void setBackwardsName(String backwardsName) {
		this.backwardsName = backwardsName;
	}

	
	// stuff

	@Override
	public NamedDirection getReverse()
	{
		return new NamedDirection(backwardsName, forwardsName, super.getReverse());
	}
}