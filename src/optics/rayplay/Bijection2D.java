package optics.rayplay;

import math.Vector2D;

/**
 * A 2D bijection, i.e. a one-to-one map between two spaces, "inside" and "outside"
 * @author johannes
 */
public interface Bijection2D {

	public Vector2D mapInwards(Vector2D q);
	public Vector2D mapOutwards(Vector2D q);
}
