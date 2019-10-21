package optics.raytrace.research.curvedSpaceSimulation;

/**
 * A type that allows the type of simulation to be specified.
 * @author johannes
 */
public enum GluingType
{
	SPACE_CANCELLING_WEDGES("Space-cancelling wedges (asymmetric, without containment mirrors)"),
	SPACE_CANCELLING_WEDGES_SYMMETRIC("Symmetric space-cancelling wedges (without containment mirrors)"),
	SPACE_CANCELLING_WEDGES_WITH_CONTAINMENT_MIRRORS("Space-cancelling wedges (asymmetric) with containment mirrors"),
	PERFECT("Perfect"),	// teleporting sides
	MIRROR_APPROXIMATION("Mirror approximation");	
	
	private String description;

	private GluingType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString() {return description;}
}