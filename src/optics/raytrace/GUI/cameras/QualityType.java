package optics.raytrace.GUI.cameras;

public enum QualityType
{
	// see http://en.wikipedia.org/wiki/Display_resolution
	RUBBISH("Rubbish", 1/4., 1),	// 2^(-2), 10^0
	BAD("Bad", 1/2., 3),	// 2^(-1), 10^0.5
	NORMAL("Normal", 1, 10),	// 2^0, 10^1
	GOOD("Good", 2, 32),	// 2^1, 10^1.5
	GREAT("Great", 4, 100),	// 2^2, 10^2
	SUPER("Super", 8, 316);	// 2^3, 10^2.5
	
	private String description;
	private double antiAliasingFactor;
	private int raysPerPixel;
	private QualityType(String description, double antiAliasingFactor, int raysPerPixel)
	{
		this.description = description;
		this.antiAliasingFactor = antiAliasingFactor;
		this.raysPerPixel = raysPerPixel;
	}
	public double getAntiAliasingFactor() {return antiAliasingFactor;}
	public int getRaysPerPixel() {return raysPerPixel;}
	@Override
	public String toString() {return description;}
}