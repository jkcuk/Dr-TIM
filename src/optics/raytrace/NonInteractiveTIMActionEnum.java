package optics.raytrace;

/**
 * Enables choice of what NonInteractiveTIM should do upon execution
 * @author johannes
 */
public enum NonInteractiveTIMActionEnum
{
	INTERACTIVE("Interactive"),
	RUN("Run (without saving)"),
	RUN_AND_SAVE("Run and save"),
	BATCH_RUN("Batch run and save");	// each image is individually named
	// MOVIE("Movie");	// each image is numbered
	
	private String description;
	private NonInteractiveTIMActionEnum(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString() {return description;}
}
