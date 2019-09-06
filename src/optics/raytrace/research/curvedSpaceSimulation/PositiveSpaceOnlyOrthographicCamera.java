package optics.raytrace.research.curvedSpaceSimulation;

import java.io.Serializable;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.core.CameraWithRayForImagePixel;
import optics.raytrace.GUI.sceneObjects.EditableNegativeSpaceWedgeStar;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.RayTraceException;

/**
 * Represents a special, downward-looking, orthographic camera which only "sees" the bits of "positive space" in a suitably positioned negative-refraction-wedge star.
 * 
 * @see optics.raytrace.GUI.sceneObjects.EditableNegativeSpaceWedgeStar
 * @author Johannes Courtial
 */
public class PositiveSpaceOnlyOrthographicCamera extends CameraClass implements CameraWithRayForImagePixel, Serializable
{
	private static final long serialVersionUID = 7282158399251447757L;

	/**
	 * the direction in which the rays of this orthographic camera are launched backwards
	 */
	private EditableNegativeSpaceWedgeStar negativeSpaceWedgeStar;
	
	/**
	 * The PositiveSpaceOnlyOrthographicCamera takes a photo in which the wedges of leftover space are stretched to cover all space.
	 * This type indicates the methods by which this can happen.
	 * @author johannes
	 */
	public enum LeftoverSpaceStretchingMethodType
	{
		STRETCH_ANGLE("stretching wedge angle"),
		STRETCH_WIDTH("stretching wedge width");
		
		private String description;
		private LeftoverSpaceStretchingMethodType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * method to be used for stretching the leftover-space wedges to cover all space
	 */
	private LeftoverSpaceStretchingMethodType leftoverSpaceStretchingMethod;
	
	public PositiveSpaceOnlyOrthographicCamera(
			String name,
			EditableNegativeSpaceWedgeStar negativeRefractionWedgeStar,
			LeftoverSpaceStretchingMethodType leftoverSpaceStretchingMethod,
			double width,
			double height,
			int detectorPixelsHorizontal, int detectorPixelsVertical,
			int maxTraceLevel)
	{
		super(	name,
				Vector3D.sum(
						negativeRefractionWedgeStar.getCentre(),
						negativeRefractionWedgeStar.getAxisDirection().getWithLength(-negativeRefractionWedgeStar.getStarLength())
					),	// centre of detector
				negativeRefractionWedgeStar.calculateBDirection().getWithLength(width),	// horizontal span vector
				negativeRefractionWedgeStar.calculateCDirection().getWithLength(height),	// vertical span vector
				detectorPixelsHorizontal, detectorPixelsVertical,
				maxTraceLevel);
		
		this.leftoverSpaceStretchingMethod = leftoverSpaceStretchingMethod;
		this.negativeSpaceWedgeStar = negativeRefractionWedgeStar;
	}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public PositiveSpaceOnlyOrthographicCamera(PositiveSpaceOnlyOrthographicCamera original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.cameras.Camera#clone()
	 */
	@Override
	public PositiveSpaceOnlyOrthographicCamera clone()
	{
		return new PositiveSpaceOnlyOrthographicCamera(this);
	}
	

	//
	// setters and getters
	//
	
	
	
	//
	// implement abstract CameraClass methods
	//

	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getRayForPixel(int, int)
	 */
	@Override
	public Ray getRayForPixel(double i, double j)
	{
		// undistorted position of pixel (i, j)
		Vector3D undistortedPixelPosition = ccd.getPixelCentrePosition(i,j); // ccd.getPositionOnPixel(i, j)
		
		// find the projection of the negative-space wedge's centre into the plane of the CCD
		Vector3D starCentreProjection = Geometry.uniqueLinePlaneIntersection(
				negativeSpaceWedgeStar.getCentre(),	// pointOnLine
				negativeSpaceWedgeStar.getAxisDirection(),	// directionOfLine
				ccd.getCorner(),	// pointOnPlane
				ccd.getSurfaceNormal()	// normalToPlane
			);
		
		Vector3D relativePixelPosition =
				Vector3D.difference(
						undistortedPixelPosition,
						starCentreProjection
					);
		
		// calculate the coordinates of the pixel in an (x,y) coordinate system in the detector plane in which the centre is at the origin
		Vector3D xHat = getHorizontalSpanVector().getNormalised();
		Vector3D yHat = getVerticalSpanVector().getPartPerpendicularTo(xHat).getNormalised();
		double x = Vector3D.scalarProduct(relativePixelPosition, xHat);
		double y = Vector3D.scalarProduct(relativePixelPosition, yHat);
		
		// these Cartesian coordinates correspond to polar coordinates
		double phi = Math.atan2(y, x);
		double r = Math.sqrt(x*x+y*y);
		
		// angle between the centre planes of neighbouring negative-space wedges
		double wedgeCentre2wedgeCentrePhi = 2*Math.PI/negativeSpaceWedgeStar.getNumberOfNegativeSpaceWedges();
		double leftoverSpaceWedgePhi = (2*Math.PI - MyMath.deg2rad(negativeSpaceWedgeStar.getDeficitAngleDeg()))/negativeSpaceWedgeStar.getNumberOfNegativeSpaceWedges();

//		System.out.println("PositiveSpaceOnlyOrthographicCamera.getRayForPixel: wedgeCentre2wedgeCentrePhi = "+MyMath.rad2deg(wedgeCentre2wedgeCentrePhi)+"°, "+
//				"leftoverSpaceWedgePhi = "+MyMath.rad2deg(leftoverSpaceWedgePhi)+"°"
//				);
		
		// find the azimuthal angle of the centre plane of the relevant wedge of leftover space
		double leftoverSpaceWedgeCentrePlanePhi = Math.floor((phi+1*wedgeCentre2wedgeCentrePhi)/wedgeCentre2wedgeCentrePhi)*wedgeCentre2wedgeCentrePhi-0.5*wedgeCentre2wedgeCentrePhi;
		
//		System.out.println("PositiveSpaceOnlyOrthographicCamera.getRayForPixel: phi="+phi+"°, "+
//					"leftoverSpaceWedgeCentrePlanePhi = "+MyMath.rad2deg(leftoverSpaceWedgeCentrePlanePhi)+"°, "+
//		"rest = "+MyMath.rad2deg(phi-leftoverSpaceWedgeCentrePlanePhi)+"°"
//		);

		// calculate the new start position of the ray that corresponds to pixel (i, j), according to one of a number of different methods
		Vector3D newStartPosition;
		switch(leftoverSpaceStretchingMethod)
		{
		case STRETCH_ANGLE:
			// stretch the angles; corresponds to projecting onto a smooth cone
			double rest = phi-leftoverSpaceWedgeCentrePlanePhi;	
			double newPhi = leftoverSpaceWedgeCentrePlanePhi+rest/wedgeCentre2wedgeCentrePhi*leftoverSpaceWedgePhi;
			double newX = r*Math.cos(newPhi);
			double newY = r*Math.sin(newPhi);

			newStartPosition = Vector3D.sum(
					starCentreProjection,
					xHat.getWithLength(newX),
					yHat.getWithLength(newY)
					);
			break;
		case STRETCH_WIDTH:
		default:
			// stretch the coordinate perpendicular to the centre plane of the leftover space wedge
			// define the radial direction in the centre plane of the relevant leftover-space wedge...
			Vector3D leftoverSpaceWedgeCentrePlaneRadialDirection = Vector3D.sum(
					xHat.getWithLength(Math.cos(leftoverSpaceWedgeCentrePlanePhi)),
					yHat.getWithLength(Math.sin(leftoverSpaceWedgeCentrePlanePhi))
					);
			// ... and the (azimuthal) normal to it, i.e. the direction perpendicular to the centre plane of the leftover wedge
//			Vector3D leftoverSpaceWedgeCentrePlaneAzimuthalDirection = Vector3D.sum(
//					xHat.getWithLength( Math.sin(leftoverSpaceWedgeCentrePlanePhi)),
//					yHat.getWithLength(-Math.cos(leftoverSpaceWedgeCentrePlanePhi))
//				);

			newStartPosition = Vector3D.sum(
					starCentreProjection,
					relativePixelPosition.getPartParallelTo(leftoverSpaceWedgeCentrePlaneRadialDirection),
					relativePixelPosition.getPartPerpendicularTo(leftoverSpaceWedgeCentrePlaneRadialDirection).getProductWith(
							Math.tan(0.5*leftoverSpaceWedgePhi)/Math.tan(0.5*wedgeCentre2wedgeCentrePhi)
						)
				);
		}
		
		// find ray direction by subtracting pixel position from pinhole position
		return new Ray(newStartPosition, negativeSpaceWedgeStar.getAxisDirection(), 0);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.Camera#getCentralRayForPixel(int, int)
	 */
	@Override
	public Ray getCentralRayForPixel(double i, double j)
	{
		return getRayForPixel(i, j);
	}
	

	/* (non-Javadoc)
	 * @see optics.raytrace.CCD#calculateColour(int, int)
	 */
	@Override
	public DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException
	{
		Ray r=getRayForPixel(i,j);

		return scene.getColour(r, lights, scene, getMaxTraceLevel(),
				getRaytraceExceptionHandler());
	}

	@Override
	public Ray getRayForImagePixel(int imagePixelsHorizontal, int imagePixelsVertical, double i, double j)
	{
		return getRayForPixel(
				(i * ((double)getDetectorPixelsHorizontal()/(double)imagePixelsHorizontal)),
				(j * ((double)getDetectorPixelsVertical()/(double)imagePixelsVertical))
			);
	}
}