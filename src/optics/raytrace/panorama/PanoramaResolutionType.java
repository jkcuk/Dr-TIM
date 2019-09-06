package optics.raytrace.panorama;

public enum PanoramaResolutionType
{
	R360("1 pixel/degree", 360, 180),
	R720("2 pixels/degree", 2*360, 2*180),
	R1440("4 pixels/degree", 4*360, 4*180),
	R2880("8 pixels/degree", 8*360, 8*180),
	R5760("16 pixels/degree", 16*360, 16*180);
	
	private String name;
	private int hPixels, vPixels;
	private PanoramaResolutionType(String name, int hPixels, int vPixels)
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