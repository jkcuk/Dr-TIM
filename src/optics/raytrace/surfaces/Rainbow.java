package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;


/**
 * A rainbow.
 * Adds rainbow colours to the transmitted light.  The added colour depends on the angle with the light source.
 * @author johannes
 */
public class Rainbow extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -3334075466239806644L;
	
	public static final Rainbow BRIGHT_RAINBOW = 
			new Rainbow(
					1,	// saturation
					.35,	// lightness
					new Vector3D(100,300,-500)	// lightSourcePosition
				);

	/**
	 * the "saturation" of the HSL colour value that gets added to the transmitted colour
	 */
	private double saturation;

	/**
	 * the "lightness" of the HSL colour value that gets added to the transmitted colour
	 */
	private double lightness;
	
	/**
	 * light-source position
	 */
	private Vector3D lightSourcePosition;


	/**
	 * A rainbow
	 * @param saturation
	 * @param lightness
	 * @param lightSourcePosition
	 */
	public Rainbow(double saturation, double lightness, Vector3D lightSourcePosition)
	{
		super(1, false);
		setSaturation(saturation);
		setLightness(lightness);
		setLightSourcePosition(lightSourcePosition);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Rainbow clone()
	{
		return new Rainbow(getSaturation(), getLightness(), getLightSourcePosition());
	}


	//
	// setters & getters
	//

	public double getSaturation() {
		return saturation;
	}

	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}

	public double getLightness() {
		return lightness;
	}

	public void setLightness(double lightness) {
		this.lightness = lightness;
	}

	public Vector3D getLightSourcePosition() {
		return lightSourcePosition;
	}

	public void setLightSourcePosition(Vector3D lightSourcePosition) {
		this.lightSourcePosition = lightSourcePosition;
	}

	
	
	//
	// the method that does the work
	//

	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;

		// calculate the angle between the direction to the observer and that to the light source
		double angle = MyMath.rad2deg(Math.acos(Vector3D.scalarProduct(
				Vector3D.difference(i.p, lightSourcePosition).getNormalised(),
				ray.getD()
			)));
		
		// calculate the rainbow colour
		double rainbowColourLightness = lightness*Math.exp(-Math.pow(angle-42.5, 6)/2.);
		DoubleColour rainbowColour = DoubleColour.getColourFromHSL((41-angle)/2.5, saturation, rainbowColourLightness);
		// Note that the "41" and "2.5" in the formula "DoubleColour.getColourFromHSL((41-angle)/2.5, saturation, rainbowColourLightness);"
		// were found by playing around and comparing the result to images of a rainbow.
		// See http://www.comfsm.fm/~dleeling/cis/hsl_rainbow.html for more details.
		
		// add together...
		return DoubleColour.sum(
				// ... the colour of the straight-through ray...				
				scene.getColourAvoidingOrigin(
						ray.getBranchRay(
								i.p,
								ray.getD(),
								i.t
								),
						i.o,
						l,
						scene,
						traceLevel-1,
						raytraceExceptionHandler
						).multiply(1-rainbowColourLightness),
				// ... and the rainbow colour
				rainbowColour
			);
	}

	@Override
	public String toString() {
		return "Rainbow [saturation=" + saturation + ", lightness=" + lightness + ", lightSourcePosition="
				+ lightSourcePosition + "]";
	}
}

