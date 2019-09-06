package optics.raytrace.research.adaptiveIntegralLens;

/**
 * A type that allows the type of adaptive astigmatic lenses to be determined.
 * @author johannes
 */
public enum AdaptiveToricLensType
{
	OFFSET_CONTROLLED("Offset-controlled", "offset"),	// "Crossed linear-power complementary lenticular arrays", "CLCLA"),
	STRAIN_CONTROLLED("Strain-controlled", "strain");	// "Stretchy complementary lenslet arrays", "SCLA");
	
	private String description, acronym;

	private AdaptiveToricLensType(String description, String acronym)
	{
		this.description = description;
		this.acronym = acronym;
	}
	
	public String getAcronym() {return acronym;}
	
	@Override
	public String toString() {return description;}
}