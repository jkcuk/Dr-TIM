
package optics.raytrace.research.TO.lensCloak;
import java.awt.EventQueue;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.ParametrisedPlane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.sceneObjects.Arrow;
import optics.raytrace.sceneObjects.FresnelLensShaped;



/**
 * This class has code from @see PointCloudMaker with the heightmapping functionality ripped out. Instead this class extends the
 * @see NonInteractiveTIMEngine so that one can set the same parameters as in the PointCloudMaker and visualise the lens. This can be used for
 * tweaking the lens that is to be machined and visualise it 'instantly' without the intermediate steps of writing the height map to a *.txt
 * file and then visualising that with an external (to TIM) script. The units used in this class differ from PointCloudMaker by an order of
 * magnitude.
 * 
 * @author Gergely
 */
public class LensVisualiser extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensVisualiser()
	{
		super();

		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 20;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;

		// camera parameters are set in createStudio()
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFirstPartOfFilename()
	{
		return
				"LensToBeMachined"
				;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()

			throws SceneException
	{
		double phi = 0;//+(movie?1.02*2.*Math.PI*frame/(movieNumberOfFrames):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), -100, Math.cos(phi));
		cameraViewCentre = new Vector3D(0,0,0);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 5;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;

		super.populateSimpleStudio();

		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();

		/////////////////////////////////////////////////////////////////////////
		////////A coordinate system//////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////
		Vector3D origin = new Vector3D(0,1,0);
		scene.addSceneObject(new Arrow(
				"x-axis",// description,
				origin,// startPoint,
				Vector3D.sum(origin, new Vector3D(0.5,0,0)),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.RED_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"y-axis",// description,
				origin,// startPoint,
				Vector3D.sum(origin, new Vector3D(0,0.5,0)),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.GREEN_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"z-axis",// description,
				origin,// startPoint,
				Vector3D.sum(origin, new Vector3D(0,0,0.5)),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLUE_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));/**/
		/////////////////////////////////////////////////////////////////////////
		////////Coordinate system done///////////////////////////////////////////
		/////////////////////////////////////////////////////////////////////////



		//////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////Visualising the Fresnel lens we're going to get machined in Durham///////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		double CM = 1e0;
		double MM = 1e-1;

		/*
		 * Use this variable to select which side of the lens is to be mapped. Options are "Front" and "Back"
		 */
		String side = "Back";
		/*
		 * Use this variable to toggle between tilted and untilted configurations. Options are "Tilted" and "Untilted"
		 */
		String configuration = "Untilted"; //"Untilted";
		/////////////////////////////////////////////
		////Parameters to be tweaked/////////////////
		/////////////////////////////////////////////
		double lensRadius = CM;
		double refractiveIndex = 1.5;

		double frontFocalDistance = 5*CM;
		double backFocalDistance = 2*CM;
		double lensDisplacement = 0.5*CM;
		double alpha = Math.PI/6; //the angle that the lens normal makes with the optical axis direction
		/////////////////////////////////////////////
		////Nothing else to tweak////////////////////
		/////////////////////////////////////////////


		Vector3D towardsFrontFocalPoint = null;	
		Vector3D towardsLensCentre = null;
		Vector3D lensCentre = new Vector3D(0,0,0);
		Vector3D lensNormal = null;
		Vector3D focalPointFront = null;
		Vector3D focalPointBack = null;

		switch(configuration) {
		case("Untilted") : 
			towardsFrontFocalPoint = new Vector3D(Math.sin(alpha), 0, Math.cos(alpha));
		towardsLensCentre = new Vector3D(Math.cos(alpha), 0, -1.*Math.sin(alpha));			
		lensNormal = new Vector3D(0,0,1);
		break;
		case("Tilted") : 
		default:
			towardsFrontFocalPoint = new Vector3D(0,0,1);	
			towardsLensCentre = new Vector3D(1,0,0);				
			lensNormal = new Vector3D(-1.*Math.sin(alpha), 0, Math.cos(alpha));
			break;
		}
		focalPointFront = Vector3D.difference(towardsFrontFocalPoint.getProductWith(frontFocalDistance), towardsLensCentre.getProductWith(lensDisplacement));
		focalPointBack = Vector3D.difference(towardsFrontFocalPoint.getReverse().getProductWith(backFocalDistance), towardsLensCentre.getProductWith(lensDisplacement));

		ParametrisedDisc apertureDisc = new ParametrisedDisc(
				"disc for computing the focal lengths of the FresnelLensSurfaces",// description,
				lensCentre, //centre,
				lensNormal, //normal,
				lensRadius, //radius,
				SurfaceProperty.NO_SURFACE_PROPERTY,// sp,
				null, //parent, 
				null //studio
				);
		FresnelLensShaped lensToBeMachined = new FresnelLensShaped(
				"full fresnel lens to be machined, two sides are related by rotating the lens around by pi", // description,
				lensCentre,// lensCentre,
				lensNormal, // forwardsCentralPlaneNormal,
				focalPointFront, // frontConjugatePoint,
				focalPointBack, // backConjugatePoint,
				refractiveIndex, // refractiveIndex,
				2*MM, // thickness,
				0.2*MM, // minimumSurfaceSeparation,
				apertureDisc, // apertureShape,
				true, // makeStepSurfacesBlack,
				0.92, // transmissionCoefficient,
				null, // parent,
				null // studio
				);
		SceneObjectIntersection lensSurface = new SceneObjectIntersection(
				"one of the two sufaces comprising the FresnelLensShaped", // description,
				null, // parent,
				null // studio
				);
		lensSurface.addSceneObject(lensToBeMachined);

		ParametrisedPlane lensSelector = null; //this will be defined inside the switch statement as orientation depends on which surface is being mapped
		Vector3D lensSelectorNormal = null;
		/*
		 * this witch statement is differnet from PointCloudMaker as the FileWriter bit is ripped out. The resulting code has here been refactored
		 * with the witch statement affecting only the normal of the lensSelector. The lensSelector itself is created only afterwards.
		 */
		switch(side) {
		case("Front") :
			lensSelectorNormal = lensNormal.getReverse();

		break;
		case("Back") :
			lensSelectorNormal = lensNormal;
		break;
		default :
			break;
		}
		lensSelector = new ParametrisedPlane(
				"selects only one of the Fresnel lens surfaces", // description,
				lensCentre, // pointOnPlane,
				lensSelectorNormal, // surfaceNormal,
				SurfaceColour.RED_MATT,// SurfaceProperty.NO_SURFACE_PROPERTY, // sp,
				null, // parent,
				null // studio
				);
		//here the lens surface to be turned into a height map is selected
		lensSurface.addSceneObject(lensSelector);

		/*
		 * What follows here is entire new (as compared to PointCloudMaker). The lens is cut in half to better see what's going on and the
		 * optical axis is also visualised.
		 */
		ParametrisedPlane lensBisector = new ParametrisedPlane(
				"selects only one of the Fresnel lens surfaces", // description,
				lensCentre, // pointOnPlane,
				new Vector3D(0,1,0), // surfaceNormal,
				SurfaceColour.RED_MATT,// SurfaceProperty.NO_SURFACE_PROPERTY, // sp,
				null, // parent,
				null // studio
				);
		lensSurface.addSceneObject(lensBisector);
		scene.addSceneObject(lensSurface);
		scene.addSceneObject(new Arrow(
				"optical axis",// description,
				focalPointBack,// startPoint,
				focalPointFront,// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.YELLOW_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));

	}

	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		Runnable r = new LensVisualiser();

		EventQueue.invokeLater(r);
	}
}
