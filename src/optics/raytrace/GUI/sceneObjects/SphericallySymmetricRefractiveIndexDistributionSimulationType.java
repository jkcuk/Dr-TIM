package optics.raytrace.GUI.sceneObjects;

public enum SphericallySymmetricRefractiveIndexDistributionSimulationType
{
	SPHERICAL_SHELLS_CONSTANT_N("Spherical shells with n=const."),
	SMOOTH_N("Smooth n(r)");
	
	private String description;

	private SphericallySymmetricRefractiveIndexDistributionSimulationType(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
}