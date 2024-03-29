package optics.raytrace.research.curvedSpaceSimulation;

/**
 * A type that allows the type of simulation to be specified.
 * @author johannes
 */
public enum GluingType
{
	NEGATIVE_SPACE_WEDGES("Negative-space wedges (asymmetric, without containment mirrors)"),
	NEGATIVE_SPACE_WEDGES_SYMMETRIC("Symmetric negative-space wedges (without containment mirrors)"),
	NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS("Negative-space wedges (asymmetric) with containment mirrors"),
	PERFECT("Perfect (teleporting sides)"),	// teleporting sides
	LENSES_COMPLETELY_SYMMETRIC("Three-lens combo, symmetric (f1=f2=f3)"),
	LENSES_QUITE_SYMMETRIC("Three-lens combo, quite symmetric (f1=f3)"),
	MIRROR_APPROXIMATION("Mirror approximation");	
	
	private String description;

	private GluingType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString() {return description;}
}