package optics.raytrace.lights;

import java.io.Serializable;

import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.*;

/**
 * Ambient lighting that illuminates all objects in a scene equally.
 * Can be used in conjunction with a point light source.
 * @see PhongLightSource
 * @see LightSource
 * 
 * @author Dean et al.
 *
 */
public class AmbientLight extends LightSource implements Serializable
{
	private static final long serialVersionUID = 557186619706725663L;

	private DoubleColour c;

	public AmbientLight(String description) {
		super(description);
		c=new DoubleColour(0.3, 0.3, 0.3);
	}

	public AmbientLight(String description, DoubleColour c) {
		super(description);
		this.c=c;
	}

	@Override
	public DoubleColour getColour(SurfaceColour surfaceColour, SceneObject scene, RaySceneObjectIntersection i, Ray r, int traceLevel)
	{
		return DoubleColour.multiply(surfaceColour.getDiffuseColour(), c);
	}

	public DoubleColour getC() {
		return c;
	}

	public void setC(DoubleColour c) {
		this.c = c;
	}


	@Override
	public String toString() {
		return "AmbientLightSource(" + c.getR() + ", " + c.getG() + ", " + c.getB() + ")";
	}
}
