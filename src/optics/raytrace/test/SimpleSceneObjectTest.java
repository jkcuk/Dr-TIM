package optics.raytrace.test;

import java.io.PrintStream;

import math.*;
import math.ODE.IntegrationType;
import math.SpaceTimeTransformation.SpaceTimeTransformationType;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.cameras.AnyFocusSurfaceCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Parallelepiped;
import optics.raytrace.sceneObjects.Parallelepiped2;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.TimHead;
import optics.raytrace.sceneObjects.WrappedSceneObject;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceOfLissajousLens;



public class SimpleSceneObjectTest extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public SimpleSceneObjectTest()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
//		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.MOVIE;
		cameraViewDirection = new Vector3D(0,-1,0);
		cameraHorizontalFOVDeg = 20;
		cameraTopDirection = new Vector3D(0,0,1); 
		cameraDistance = 10;
		traceRaysWithTrajectory = false;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"SimpleSceneObjectTest"
				;
	}
	
	/*
	 * Write all parameters to a .txt file
	 * @see optics.raytrace.NonInteractiveTIMEngine#writeParameters(java.io.PrintStream)
	 */
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		// the studio
		studio = new Studio();


		//setting the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		

		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio)); //the floor
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(getStandardCamera());
		// ... and then adding scene objects to scene
		
		scene.addSceneObject(new Sphere("point source esk", Vector3D.O, 0.05, SurfaceColour.BLACK_MATT, scene, studio));
		
		
		
//		AnyFocusSurfaceCamera defualtCamera = new AnyFocusSurfaceCamera(
//				"Camera",
//				Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance)),	// centre of aperture
//				cameraViewDirection,	// viewDirection
//				calculateHorizontalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg),// horizontalSpanVector, 
//				calculateVerticalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg, cameraPixelsX, cameraPixelsY) ,//verticalSpanVector,
//				cameraPixelsX, cameraPixelsY,	// logical number of pixels
//				cameraExposureCompensation,	// ExposureCompensationType.EC0,	// exposure compensation +0
//				cameraMaxTraceLevel,	// maxTraceLevel
//				new Plane(
//						"focus plane",	// description
//						Vector3D.sum(Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance)), cameraViewDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
//						cameraViewDirection,	// normal
//						null,	// surfaceProperty
//						null,	// parent
//						null	// studio
//					),	// focus scene
//	            // double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
//	            0.09/1000,// apertureRadius in mm,
//	            true,
//				550e-9,// lambda,
//	            renderQuality.getBlurQuality().getRaysPerPixel()// raysPerPixel
//	    	);
		
		RelativisticAnyFocusSurfaceCamera defualtCamera = new RelativisticAnyFocusSurfaceCamera(
				"Camera",
				Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance)),	// centre of aperture
				cameraViewDirection,	// viewDirection
				calculateHorizontalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg),// horizontalSpanVector, 
				calculateVerticalSpanVector(cameraViewDirection, cameraTopDirection, cameraHorizontalFOVDeg, cameraPixelsX, cameraPixelsY) ,//verticalSpanVector,
				SpaceTimeTransformationType.LORENTZ_TRANSFORMATION,
				Vector3D.O,	// beta,
				cameraPixelsX, cameraPixelsY,	// logical number of pixels
				cameraExposureCompensation,	// ExposureCompensationType.EC0,	// exposure compensation +0
				cameraMaxTraceLevel,	// maxTraceLevel
				new Plane(
						"focus plane",	// description
						Vector3D.sum(Vector3D.sum(cameraViewCentre, cameraViewDirection.getWithLength(-cameraDistance)), cameraViewDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
						cameraViewDirection,	// normal
						null,	// surfaceProperty
						null,	// parent
						null	// studio
					),	// focus scene
				(SceneObject)null,	// cameraFrameScene,
				new FocusSurfaceShutterModel(0),	// shutterModel,
	            // double detectorDistance,	// in the detector-plane shutter model, the detector is this distance behind the entrance pupil
	            0.09/1000,// apertureRadius in mm,
				true,
				550e-9,// lambda,
	            renderQuality.getBlurQuality().getRaysPerPixel()// raysPerPixel
	    	);
		
		studio.setCamera(defualtCamera);

		studio.setScene(scene);
		
	}
	
	private static Vector3D calculateVerticalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1,
			int imagePixelsHorizontal1, int imagePixelsVertical1
		)
	{
		return topDirection1.getPartPerpendicularTo(viewDirection1).getWithLength(
				-2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.) * 
				imagePixelsVertical1 / imagePixelsHorizontal1
			);
	}

	private static Vector3D calculateHorizontalSpanVector(
			Vector3D viewDirection1,
			Vector3D topDirection1,
			double horizontalViewAngle1
		)
	{
		return Vector3D.crossProduct(topDirection1, viewDirection1).getWithLength(2*Math.tan(MyMath.deg2rad(horizontalViewAngle1)/2.));
	}

	
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new SimpleSceneObjectTest()).run();
	}
}
