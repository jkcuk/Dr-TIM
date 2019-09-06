package optics.raytrace.GUI.sceneObjects;

/**
 * A type that allows the type of EditableNullSpaceWedge to be specified.
 * @author johannes
 */
public enum NullSpaceWedgeType
{
	NEGATIVE_SPACE_WEDGES("Negative-space wedges (asymmetric, without containment mirrors)"),
	NEGATIVE_SPACE_WEDGES_SYMMETRIC("Symmetric negative-space wedges (without containment mirrors)"),
	NEGATIVE_SPACE_WEDGES_WITH_CONTAINMENT_MIRRORS("Negative-space wedges (asymmetric) with containment mirrors"),
	PERFECT("Perfect");	// teleporting sides
	
	private String description;

	private NullSpaceWedgeType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString() {return description;}
}