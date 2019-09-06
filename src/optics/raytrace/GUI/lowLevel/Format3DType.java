package optics.raytrace.GUI.lowLevel;

public enum Format3DType
{
	// from http://en.wikipedia.org/wiki/HDMI#Version_1.4:
	// HDMI 1.4a requires that 3D displays support the frame packing 3D format at either 720p50 and 1080p24
	// or 720p60 and 1080p24, side-by-side horizontal at either 1080i50 or 1080i60, and top-and-bottom at
	// either 720p50 and 1080p24 or 720p60 and 1080p24.
	TB720("", OrientationType.VERTICAL, 1280, 720/2, 0.5, 0),	// top-and-bottom frame packing, frame compatible
	TB1080("", OrientationType.VERTICAL, 1920, 1080/2, 0.5, 0),	// top-and-bottom frame packing, frame compatible
	SS1080("", OrientationType.HORIZONTAL, 1920/2, 1080, 2.0, 0),	// side-by-side frame packing, frame compatible
	FHD3D("; Full HD 3D/Blu-ray 3D", OrientationType.VERTICAL, 1920, 1080, 1.0, 45);	// top/bottom frame packing, with a 45-pixel gap in between
	
	private String name;
	private OrientationType framePackingOrientation;
	private int hPixels, vPixels, gap;	// number of pixels in horizontal and vertical direction for left and right frames, gap between frames
	private double pixelAspectRatio;	// aspect ratio (horizontal width / vertical width) of one frame pixel
	private Format3DType(String name, OrientationType orientation, int hPixels, int vPixels, double pixelAspectRatio, int gap)
	{
		this.name = name;
		this.framePackingOrientation = orientation;
		this.hPixels = hPixels;
		this.vPixels = vPixels;
		this.pixelAspectRatio = pixelAspectRatio;
		this.gap = gap;
	}
	public OrientationType getOrientation() {return framePackingOrientation;}
	public int getHPixels() {return hPixels;}
	public int getVPixels() {return vPixels;}
	public double getPixelAspectRatio() {return pixelAspectRatio;}
	public int getGap() {return gap;}
	public int getPackedImagePixels(OrientationType orientation)
	{
		int n = (orientation == OrientationType.HORIZONTAL)?hPixels:vPixels;
		return (framePackingOrientation == orientation)?(2*n+gap):n;
	}
	@Override
	public String toString() {return hPixels+"x"+vPixels+" (2 "+framePackingOrientation+"ly packed frames"+name+")";}
}