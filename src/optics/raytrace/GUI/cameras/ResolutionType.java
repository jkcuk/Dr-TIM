package optics.raytrace.GUI.cameras;

public enum ResolutionType
{
	// see http://en.wikipedia.org/wiki/Display_resolution
	VGA("VGA", 640, 480),
	PAL("PAL", 768, 576),
	XGA("XGA", 1024, 768),
	HD720("HD720", 1280, 720),
	HD1080("HD1080", 1920, 1080);
	
	private String name;
	private int hPixels, vPixels;
	private ResolutionType(String name, int hPixels, int vPixels)
	{
		this.name = name;
		this.hPixels = hPixels;
		this.vPixels = vPixels;
	}
	public int getHPixels() {return hPixels;}
	public int getVPixels() {return vPixels;}
	@Override
	public String toString() {return name + " ("+hPixels+"x"+vPixels+")";}
}