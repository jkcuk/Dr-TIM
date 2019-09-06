package optics.raytrace.GUI.lowLevel;

public enum OrientationType
{
	HORIZONTAL("Horizontal"),
	VERTICAL("Vertical");
	
	private String name;
	private OrientationType(String name)
	{
		this.name = name;
	}
	@Override
	public String toString() {return name;}
}