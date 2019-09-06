package optics.raytrace.cameras;

import java.io.*;

import math.*;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.*;

/**
 * A normal camera with a circular aperture, focussed on a plane in front of the camera.
 */
public class ApertureCamera extends AnyFocusSurfaceCamera implements Serializable
{
	private static final long serialVersionUID = -907620586337797187L;

	protected double focusDistance;

	/**
	 * Constructor that takes the camera position (i.e. centre of aperture) and view direction
	 * @param name
	 * @param apertureCentre
	 * @param viewDirection
	 * @param horizontalSpanVector3D
	 * @param verticalSpanVector3D
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param focusDistance
	 * @param apertureRadius
	 * @param raysPerPixel
	 */
	public ApertureCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D viewDirection,
			Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
			int detectorPixelsHorizontal, int detectorPixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			double focusDistance,
			double apertureRadius,
			int raysPerPixel) {
		super(	name,
				apertureCentre,	// pinhole position is aperture Centre
				apertureCentre.getSumWith(
						viewDirection.getNormalised().getProductWith(focusDistance)
				),	// centre of view
				horizontalSpanVector3D, verticalSpanVector3D,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				new Plane(
						"focussing plane",
						apertureCentre.getSumWith(
								viewDirection.getNormalised().getProductWith(focusDistance)
						),	// point on plane
						viewDirection,	// normal to plane
						(SurfaceProperty)null,
						null,	// parent
						Studio.NULL_STUDIO
				),
				apertureRadius,
				raysPerPixel);
		this.focusDistance = focusDistance;
	}

	/**
	 * Constructor that takes as elements the position of the camera and the point in the centre of view that's in focus
	 * @param name
	 * @param apertureCentre
	 * @param centreOfViewInFocus
	 * @param horizontalSpanVector3D
	 * @param verticalSpanVector3D
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 * @param exposureCompensation
	 * @param maxTraceLevel
	 * @param apertureRadius
	 * @param raysPerPixel
	 */
	public ApertureCamera(
			String name,
			Vector3D apertureCentre,
			Vector3D centreOfViewInFocus,
			Vector3D horizontalSpanVector3D, Vector3D verticalSpanVector3D,
			int detectorPixelsHorizontal, int detectorPixelsVertical, 
			ExposureCompensationType exposureCompensation,
			int maxTraceLevel,
			double apertureRadius,
			int raysPerPixel) {
		super(	name,
				apertureCentre,	// pinhole position is aperture Centre
				centreOfViewInFocus,	// centre of view
				horizontalSpanVector3D, verticalSpanVector3D,
				detectorPixelsHorizontal, detectorPixelsVertical,
				exposureCompensation,
				maxTraceLevel,
				new Plane(
						"focussing plane",
						centreOfViewInFocus,
						centreOfViewInFocus.getDifferenceWith(apertureCentre).getNormalised(),	// normal to plane
						(SurfaceProperty)null,
						null,	// parent
						Studio.NULL_STUDIO
				),
				apertureRadius,
				raysPerPixel);
		this.focusDistance = centreOfViewInFocus.getDifferenceWith(apertureCentre).getLength();
	}

    /**
     * Create a clone of the original
     * @param original
     */
    public ApertureCamera(ApertureCamera original)
    {
    	super(original);
    	focusDistance = original.getFocusDistance();
    }
    
    /* (non-Javadoc)
     * @see optics.raytrace.cameras.PinholeCamera#clone()
     */
    @Override
	public ApertureCamera clone()
    {
    	return new ApertureCamera(this);
    }

	public double getFocusDistance() {
		return focusDistance;
	}

	public void setFocusDistance(double focusDistance)
	{
		this.focusDistance = focusDistance;
		
		Vector3D viewDirection = Vector3D.difference(getCentreOfView(), getPinholePosition());
		setFocusScene(new Plane(
				"focussing plane",
				getApertureCentre().getSumWith(
						viewDirection.getWithLength(focusDistance)
				),	// point on plane
				viewDirection,	// normal to plane
				(SurfaceProperty)null,
				null,	// parent
				Studio.NULL_STUDIO
		));
	}



	/**
	 * Summarize the ApertureCamera in a string of text.
	 * @return The string representation of the text.
	 */
	@Override
	public String toString () {
		return description+" [ApertureCamera]";
	} 
}
