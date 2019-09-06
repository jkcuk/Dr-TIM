package optics.raytrace.GUI.cameras;

public enum RenderQualityEnum implements RenderQuality
{
	DRAFT("Draft quality (but fast)", "Draft", QualityType.RUBBISH, QualityType.NORMAL),
	STANDARD("Standard quality", "Standard", QualityType.NORMAL, QualityType.GOOD),
	GREAT("Great quality (but slow!)", "Great", QualityType.SUPER, QualityType.GREAT);
	
	private String description, briefDescription;
	private QualityType blurQuality, antiAliasingQuality;
	private RenderQualityEnum(String description, String briefDescription, QualityType blurQuality, QualityType antiAliasingQuality)
	{
		this.description = description;
		this.briefDescription = briefDescription;
		this.blurQuality = blurQuality;
		this.antiAliasingQuality = antiAliasingQuality;
	}
	
	@Override
	public String toString() {return description;}
	public String getDescription() {return description;}
	public String getBriefDescription() {return briefDescription;}
	
	// RenderQuality methods
	
	@Override
	public QualityType getBlurQuality() {return blurQuality;}
	
	@Override
	public QualityType getAntiAliasingQuality() {return antiAliasingQuality;}
}
