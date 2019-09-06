package optics.raytrace.panorama.panorama3DViewer;

public enum SurroundAnaglyphViewerSceneType
{
	STANDARD_SCENE_ZF1("Standard scene, zFloor=-1"),
	STANDARD_SCENE_ZF2("Standard scene, zFloor=-2"),
	STANDARD_SCENE_ZF4("Standard scene, zFloor=-4"),
	STANDARD_SCENE_ZF8("Standard scene, zFloor=-8"),
	STANDARD_SCENE_ZF16("Standard scene, zFloor=-16"),
	STANDARD_SCENE_ZF32("Standard scene, zFloor=-32"),
	STANDARD_SCENE_ZF64("Standard scene, zFloor=-64"),
	INSIDE_SPHERE_R1("Inside sphere, r=1"),
	INSIDE_SPHERE_R2("Inside sphere, r=2"),
	INSIDE_SPHERE_R4("Inside sphere, r=4"),
	INSIDE_SPHERE_R8("Inside sphere, r=8"),
	INSIDE_SPHERE_R16("Inside sphere, r=16"),
	INSIDE_SPHERE_R32("Inside sphere, r=32"),
	INSIDE_SPHERE_R64("Inside sphere, r=64");

	private String description;
	
	private SurroundAnaglyphViewerSceneType(String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString() {return description;}
}
