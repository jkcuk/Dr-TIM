package optics.raytrace.research.TO.idealLensCloak;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.sceneObjects.Editable4PiLens;
import optics.raytrace.exceptions.SceneException;


/**
 * Calculates the view from inside a 4pi lens.
 * 
 * @author Johannes Courtial
 */
public class FourPiLensInsideView extends NonInteractiveTIMEngine
{
	// additional parameters
		
	/**
	 * should lens L1 be shown?
	 */
	protected boolean showL1;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public FourPiLensInsideView()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;

		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.RUN_AND_SAVE;
		movie = true;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		showL1 = true;
		
		// camera parameters are set in createStudio()
	}

	
	@Override
	public String getClassName()
	{
		return
				"FourPiLensInsideView"
				+ (showL1?"":" (L1 not shown)");
	}
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		double phi = 2.*Math.PI*frame/(numberOfFrames+1);
		cameraViewDirection = new Vector3D(Math.cos(phi), 0, Math.sin(phi));
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 0;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 90;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();

		
		Vector3D opticalAxisDirection = new Vector3D(0, 0, 1);
		double h = 1;
		double hC = 0.2;
		double f = MyMath.HUGE;
		
		scene.addSceneObject(new Editable4PiLens(
				"4pi lens",	// description
				Vector3D.sum(cameraViewCentre, opticalAxisDirection.getWithLength(-hC)),	// principalPoint
				opticalAxisDirection,	// opticalAxisDirection
				f,	// focalLengthOutwards
				new Vector3D(1, 0, 0).getPartPerpendicularTo(opticalAxisDirection),	// transverseDirection1
				new Vector3D(0, 1, 0).getPartPerpendicularTo(opticalAxisDirection),	// transverseDirection2
				h,	// radius
				h,	// length
				showL1,	// showL1
				true,	// showR1
				h/3,	// closerInnerVertexDistance
				2.*h/3,	// fartherInnerVertexDistance
				Editable4PiLens.LensType.IDEAL_THIN_LENS,	// LensType
				0.96,	// individualLensTransmissionCoefficient
				true,	// showFrames
				0.001*h,	// frameRadius
				SurfaceColour.GREY50_SHINY,	// frameSurfaceProperty
				false,	// showPlaceholderSurfaces
				scene,	// parent
				studio
		));
	}
	
	public static void main(final String[] args)
	{
		(new FourPiLensInsideView()).run();
	}
}
