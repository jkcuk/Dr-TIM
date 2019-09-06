package optics.raytrace.GUI.sceneObjects;

public enum SCWedgeLegFaceShape
{
	RECTANGULAR("Rectangular"),
	TRIANGULAR("Triangular");
	
	private String description;

	private SCWedgeLegFaceShape(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
}
