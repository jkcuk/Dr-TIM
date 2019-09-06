package optics.raytrace.core;

/**
 * @author johannes
 * a SurfaceProperty whose shadow can be switched on or off
 */
public interface SurfacePropertyWithControllableShadow {
	/**
	 * @param shadowThrowing	true if the scene object is supposed to throw a shadow, false if it isn't
	 */
	public void setShadowThrowing(boolean shadowThrowing);
	
	/**
	 * also an abstract method in SurfaceProperty
	 * @return true if the scene object throws a shadow, false if it doesn't
	 */
	public boolean isShadowThrowing();

}
