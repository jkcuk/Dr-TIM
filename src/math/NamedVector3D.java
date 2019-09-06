package math;

/**
 * A 3D vector with a name.
 * @author johannes
 */
public class NamedVector3D extends Vector3D
{
	private static final long serialVersionUID = -7241170848165296142L;

	protected String name;
	
	public NamedVector3D(String name, Vector3D vector)
	{
		super(vector);
		
		this.name = name;
	}
	
	public NamedVector3D(String name, double x, double y, double z)
	{
		super(x, y, z);
		
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}