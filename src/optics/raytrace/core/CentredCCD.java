package optics.raytrace.core;

import math.Vector3D;

/**
 * A CCD described by its centre position rather than corner position.
 */
public class CentredCCD extends CCD
{
	private static final long serialVersionUID = 6944362495042319013L;

	private Vector3D centrePosition;	// position of centre of detector
	
	/**
	 * Construct a detector array from its position, the orientation of the 
	 * detector is then given by the horizontal and Vector3D spans.  
	 * A corresponding image pixellation is also stored in terms of 
	 * detectorPixelsHorizontal and detectorPixelsVertical respectively and
	 * gives the resolution at which the image is rendered.
	 * @param centrePosition
	 * @param horizontalSpanVector3D This orientates the horizontal within any rendered image.
	 * @param verticalSpanVector3D This orientates the vertical within any rendered image.
	 * @param detectorPixelsHorizontal The number of horizontal pixels in the scene.
	 * @param detectorPixelsVertical The number of vertical pixels in the scene.
	 */
	public CentredCCD(
			Vector3D centrePosition,
			Vector3D horizontalSpanVector, Vector3D verticalSpanVector,
			int detectorPixelsHorizontal, int detectorPixelsVertical)
	{
		super(	calculateCornerPosition(centrePosition, horizontalSpanVector, verticalSpanVector),
				horizontalSpanVector, verticalSpanVector,
				detectorPixelsHorizontal, detectorPixelsVertical
			);
		
		setCentrePosition(centrePosition);
	}
	
	/**
	 * Create a clone of the original.
	 * Doesn't clone the image.
	 * @param original
	 */
	public CentredCCD(CentredCCD original)
	{
		super(original);
		
		setCentrePosition(original.getCentrePosition().clone());
	}
	
//	public CentredCCD()
//	{
//		super();
//		
//		// setCentrePosition(new Vector3D(0,0,0));
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public CentredCCD clone()
	{
		return new CentredCCD(this);
	}

	/////////////////////////
	// GET AND SET METHODS //
	/////////////////////////
	
	/**
	 * @param centrePosition
	 * @param horizontalSpanVector
	 * @param verticalSpanVector
	 * @param detectorPixelsHorizontal
	 * @param detectorPixelsVertical
	 */
	public void setParameters(
			Vector3D centrePosition,
			Vector3D horizontalSpanVector,
			Vector3D verticalSpanVector,
			int detectorPixelsHorizontal,
			int detectorPixelsVertical
		)
	{
		setCentrePosition(centrePosition);
		setSpanVectors(horizontalSpanVector, verticalSpanVector);
		setDetectorPixelsHorizontal(detectorPixelsHorizontal);
		setDetectorPixelsVertical(detectorPixelsVertical);
		
		// set corner position
		validate();
	}
		
	/**
	 * Get the position of the detector centre.  
	 * @return	the position of the centre of the detector
	 */
	public Vector3D getCentrePosition()
	{
		return centrePosition;
	}

	/**
	 * Set the position of the detector centre.
	 * validate() must be called after all the parameters are set!
	 * @return	nothing
	 */
	public void setCentrePosition(Vector3D centrePosition)
	{
		this.centrePosition = centrePosition;
	}
	
	public static Vector3D calculateCornerPosition(
			Vector3D centrePosition,
			Vector3D horizontalSpanVector,
			Vector3D verticalSpanVector
		)
	{
		return Vector3D.sum(
					centrePosition,
					horizontalSpanVector.getProductWith(-0.5),
					verticalSpanVector.getProductWith(-0.5)
				);
	}
		
	/**
	 * set the corner position from the centre position and the span vectors.
	 */
	public void validate()
	{		
		// calculate the corner position
		setCorner(
				calculateCornerPosition(
						getCentrePosition(),
						getHorizontalSpanVector(),
						getVerticalSpanVector()
					)
			);
	}
}
