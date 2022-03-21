
package optics.raytrace.demo;
import java.awt.EventQueue;
import java.io.FileWriter;
import java.io.IOException;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.sceneObjects.Arrow;
import optics.raytrace.sceneObjects.FresnelLensSurface;



/**
 * Test/demo of a LensSurface.
 * 
 * @author Johannes Courtial, Jakub Belin
 */
public class DiagnosticDemo extends NonInteractiveTIMEngine
{
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public DiagnosticDemo()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		numberOfFrames = 20;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;

		// camera parameters are set in createStudio()
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"DiagnosticDemo"
				;
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	
	throws SceneException
	{

		//-0.25+(movie?2.*Math.PI*frame/(movieNumberOfFrames+1):0)
		double phi = 23.*Math.PI/30;//+(movie?1.02*2.*Math.PI*frame/(movieNumberOfFrames):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), 0, Math.cos(phi));
		cameraViewCentre = new Vector3D(0,0,0);//(0,0,10.5+(movie?-10.*frame/(movieNumberOfFrames):0));
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 5;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();

/*		// create a scene-object-primitive intersection, ...
		SceneObjectPrimitiveIntersection sceneObjectPrimitiveIntersection = new SceneObjectPrimitiveIntersection(
				"Scene-object-primitive intersection",	// description
				scene,	// parent
				studio
			);
		scene.addSceneObject(sceneObjectPrimitiveIntersection);
		
		SceneObjectIntersection sceneObjectIntersection = new SceneObjectIntersection(
				"sceneobject intersection", // description,
				scene, // parent,
				studio // studio
				);
		scene.addSceneObject(sceneObjectIntersection);*/
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


/////////////////////////////////////////////////////////////////////////
////////Facilities for testing FresnelLenses and FresnelLensSurfaces/////
/////////////////////////////////////////////////////////////////////////
		
////////The relevant points for Fresnel lenses & surfaces thereof////////
/*		Vector3D vertex0 = new Vector3D(0,1,0);
		Vector3D vertex1 = new Vector3D(1,-0.5,0);
		Vector3D vertex2 = new Vector3D(-1,-0.5,0);
		Vector3D frontConjugatePoint = new Vector3D(0,0,-2);//(0,0,-(3-(movie?2.*Math.PI*frame/(movieNumberOfFrames):0)));
		Vector3D backConjugatePoint = new Vector3D(0,0,5);//(0,0,(3-(movie?2.*Math.PI*frame/(movieNumberOfFrames):0)));
		Vector3D forwardsCentralPlaneNormal = new Vector3D(0,0,-1);
		double backConjugateDistance = 1;
		
		Vector3D lensCentre = new Vector3D(0,0,0);
		double thickness = 0.1;
		double minimumSurfaceSeparation = 0.01;
		
		double radiusOfDisc = 0.5+(movie?0.5*frame/(movieNumberOfFrames):0); 
		
		Vector3D centre = new Vector3D(0,0,0);
		Vector3D normal = new Vector3D(0,0,1);
		double phi2 = 0;//+(movie?2.*Math.PI*frame/(movieNumberOfFrames):0);*/
/////////////////////////////////////////////////////////////////////////
		
////////spheres indicating the relevant points above/////////////////////
/*		scene.addSceneObject(new Sphere(
				"vertex0",// description,
				vertex0,// center
				0.05,// radius,
				SurfaceColour.RED_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));
		scene.addSceneObject(new Sphere(
				"vertex1",// description,
				vertex1,// center
				0.05,// radius,
				SurfaceColour.GREEN_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));
		scene.addSceneObject(new Sphere(
				"vertex2",// description,
				vertex2,// center
				0.05,// radius,
				SurfaceColour.BLUE_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));
		scene.addSceneObject(new Sphere(
				"frontConjugatePoint",// description,
				frontConjugatePoint,// center
				0.01,// radius,
				SurfaceColour.CYAN_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));
		scene.addSceneObject(new Sphere(
				"backConjugatePoint",// description,
				backConjugatePoint,// center
				0.01,// radius,
				SurfaceColour.WHITE_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));*/
/////////////////////////////////////////////////////////////////////////
		
		
/*		
////////FresnelLens Lines: 332 & 335/////////////////////////////////////
		// first create a vector in the optical-axis direction but not yet normalised and facing forwards
		Vector3D opticalAxisDirection = Vector3D.difference(backConjugatePoint, frontConjugatePoint);
		opticalAxisDirection = opticalAxisDirection.getWithLength(Math.signum(Vector3D.scalarProduct(opticalAxisDirection, forwardsCentralPlaneNormal)));			
////////Fresnel Lens lines: 346(9)-379///////////////////////////////////
		// create a unit vector along the optical-axis direction and facing forwards
		Vector3D opticalAxisDirectionForwards = opticalAxisDirection; //alternate version of lines 346-349	 which originally sets this by calling a method defined by lines 332-335

////////computing the focal lengths of the Fresnel lens sections/////////
////////This functionality is commented out in FresnelLensShaped/////////
////////and will cease to exist//////////////////////////////////////////
		double[] fMinAndMaxFront = FresnelLensTriangular.calculateFMinAndMax(
				vertex0,// vertex0,
				vertex1,// vertex1,
				vertex2,// vertex2,
				frontConjugatePoint,// conjugatePoint,
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),// pointOnLens,
				forwardsCentralPlaneNormal,// normalToLensPlane,
				1.4// refractiveIndex
				);
		double[] fMinAndMaxBack = FresnelLensTriangular.calculateFMinAndMax(
				vertex0,// vertex0,
				vertex1,// vertex1,
				vertex2,// vertex2,
				backConjugatePoint,// conjugatePoint,
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(-0.5*minimumSurfaceSeparation)),// pointOnLens,
				forwardsCentralPlaneNormal.getReverse(),// normalToLensPlane,
				1.4// refractiveIndex
				);
////////computing the focal lengths: DONE////////////////////////////////

		// add the front surface
		FresnelLensSurface testSurfaceFront = new FresnelLensSurface(
				"testSurfaceFront",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
//				6,// numberOfLensSections,
				fMinAndMaxFront[0], //lensSurfaceFMin
				fMinAndMaxFront[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
		);
		// add the back surface
		FresnelLensSurface testSurfaceBack = new FresnelLensSurface(
				"testSurfaceBack",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(-0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal.getReverse(),	// outwardsPrincipalishPlaneNormal
				backConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards.getReverse(),	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
//				6,// numberOfLensSections,
				fMinAndMaxBack[0], //lensSurfaceFMin
				fMinAndMaxBack[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
		);
		FresnelLensTriangular testLens =  new FresnelLensTriangular(
				"testLens",// description,
				lensCentre,// lensCentre,
				forwardsCentralPlaneNormal, // forwardsCentralPlaneNormal,
				frontConjugatePoint, // frontConjugatePoint,
				backConjugatePoint,// backConjugatePoint,
				vertex0,
				vertex1,
				vertex2,
				1.5,// refractiveIndex,
				0.1,// thickness,
				0.01,// minimumSurfaceSeparation,
				true, // makeStepSurfacesBlack,
				0.96,// transmissionCoefficient,
				scene,// parent,
				studio// studio
			);
		FresnelLensTriangular testLens =  new FresnelLensTriangular(
				scene,// parent,
				studio// studio
			).transform(new Translation(new Vector3D(0,0,-10)));
		scene.addSceneObject(testLens);
		scene.addSceneObject(testSurfaceFront);
		scene.addSceneObject(testSurfaceBack);
		System.out.println("frontLensSurfaceMin = "+testSurfaceFront.getLensSurfaceFMin());
		System.out.println("frontLensSurfaceMax = "+testSurfaceFront.getLensSurfaceFMax());
		System.out.println("backLensSurfaceMin = "+testSurfaceBack.getLensSurfaceFMin());
		System.out.println("backLensSurfaceMax = "+testSurfaceBack.getLensSurfaceFMax());

		System.out.println("focal point of lens surface "+testSurface.getFocalPoint());
		System.out.println("minimum focal length of lens surface "+testSurface.getLensSurfaceFMin());
		System.out.println("maximum focal length of lens surface "+testSurface.getLensSurfaceFMax());
*/		
/////////////////////////////////////////////////////////////////////////
////////Fresnel Lens & Surface thereof testing DONE//////////////////////
/////////////////////////////////////////////////////////////////////////

		
		

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!!!!!!!!!!!!!!!!!!!ORGANISE THIS NEXT BIT!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
/*
/////////////////////////////////////////////////////////////////////////
////////Testing sampling of random points in a given area////////////////
/////////////////////////////////////////////////////////////////////////
 		
////////Testing sampling within the area given by 3 vertices/////////////
//------OBSOLETE: AREA NOW SPECIFIED BY PARAMETRISED TRIANGLE----------//
//------OR PARAMETRISED DISC-------------------------------------------//

		scene.addSceneObject(new Arrow(
				"U oordinate axis",// description,
				vertex0,// startPoint,
				vertex1,// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLUE_SHINY,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"U oordinate axis discplaced by V",// description,
				Vector3D.sum(vertex0, Vector3D.difference(vertex2,vertex0)),// startPoint,
				Vector3D.sum(vertex1, Vector3D.difference(vertex2,vertex0)),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLUE_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"V oordinate axis",// description,
				vertex0,// startPoint,
				vertex2,// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.RED_SHINY,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"V oordinate axis discplaced by U",// description,
				Vector3D.sum(vertex0, Vector3D.difference(vertex1,vertex0)),// startPoint,
				Vector3D.sum(vertex2, Vector3D.difference(vertex1,vertex0)),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.RED_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"U oordinate axis",// description,
				vertex1,// startPoint,
				vertex2,// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.GREEN_SHINY,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));		
  		Vector3D vectorU = Vector3D.difference(vertex1,vertex0);
		Vector3D vectorV = Vector3D.difference(vertex2,vertex0);

		System.out.println("U basis vector = "+vectorU+" V basis vector = "+vectorV);
		for(int counter = 0; counter < 500; counter++) {
			double coordinateU = Math.random();
			double coordinateV = Math.random();
			if (coordinateU + coordinateV > 1) {
				coordinateU = 1 - coordinateU;
				coordinateV = 1 - coordinateV;
			}
			scene.addSceneObject(new Sphere(
					"random point",// description,
					Vector3D.sum(vertex0, Vector3D.sum(vectorU.getProductWith(coordinateU), vectorV.getProductWith(coordinateV))), //centre,
					0.01,// radius,
					SurfaceColour.BLACK_SHINY,// surfaceProperty,
					scene,// parent,
					studio //studio
					));
			
			System.out.println("u = "+coordinateU+", v = "+coordinateV+" u + v = "+(coordinateU + coordinateV));
//																	  //
//		}*/															  //
/////////////////////////////////////////////////////////////////////////
		
////////Testing uniform sampling in a disc and a parametrised triangle///
/*		////////In a disk/////////////////////
		////////METHOD 1: sqrt(Radius)////////
		for(int counter = 0; counter < 10000; counter++) {
			double randomRadius = radiusOfDisc*Math.sqrt(Math.random());
			double randomPhi = Math.random()*2*Math.PI;
			scene.addSceneObject(new Sphere(
					"random point",// description,
					Vector3D.sum(origin,new Vector3D(randomRadius*Math.cos(randomPhi),randomRadius*Math.sin(randomPhi),0)), //centre,
					0.01,// radius,
					SurfaceColour.BLACK_MATT,// surfaceProperty,
					scene,// parent,
					studio //studio
					));
		}
		////////METHOD 2: INFINITELY THIN TRIANGLE////////
		for(int counter = 0; counter < 10000; counter++) {
			double randomRadius = Math.random() + Math.random();
			if(randomRadius > 1) {
				randomRadius = 2 - randomRadius;
			}
			double randomPhi = Math.random()*2*Math.PI;
			scene.addSceneObject(new Sphere(
					"random point",// description,
					new Vector3D(radiusOfDisc*randomRadius*Math.cos(randomPhi),radiusOfDisc*randomRadius*Math.sin(randomPhi),0), //centre,
					0.01,// radius,
					SurfaceColour.BLACK_MATT,// surfaceProperty,
					scene,// parent,
					studio //studio
					));
		}


		ParametrisedDisc testDisc = new ParametrisedDisc(
				"disc for testing its randomPoint method",// description,
				centre, // centre,
				new Vector3D(0,Math.sin(phi2),Math.cos(phi2)),
//				new Vector3D(0,1,0), // normal,
				radiusOfDisc, // radius,
				SurfaceColour.CYAN_MATT,// surface,
				scene,// parent,
				studio// studio
				);
*//*	
		for(int counter = 0; counter < 1000; counter++) {
			scene.addSceneObject(new Sphere(
					"random point",// description,
					testDisc.randomPointOnShape(), //centre,
					0.01,// radius,
					SurfaceColour.BLACK_MATT,// surfaceProperty,
					scene,// parent,
					studio //studio
					));
		}
		////////In a triangle/////////////////
		Vector3D upperVertex = new Vector3D(0,0,0);
		ParametrisedTriangle testTriangle = new ParametrisedTriangle(
				"parametrised triangle for testing the returnRandomPoint() method",// description,
				upperVertex,// vertex1,
				new Vector3D(Math.random()-0.5,Math.random()-0.5,Math.random()-0.5),// vertex1ToVertex2,
				new Vector3D(Math.random()-0.5,Math.random()-0.5,Math.random()-0.5),// vertex1ToVertex3,
				true,// semiInfinite,
				SurfaceColour.CYAN_MATT,// surfaceProperty,
				scene,// parent, 
				studio// studio
			);
		scene.addSceneObject(testTriangle);
		scene.addSceneObject(new Arrow(
				"u-axis",// description,
				upperVertex,// startPoint,
				Vector3D.sum(upperVertex, testTriangle.getuUnitVector()),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLACK_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Arrow(
				"v-axis",// description,
				upperVertex,// startPoint,
				Vector3D.sum(upperVertex, testTriangle.getvUnitVector()),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.WHITE_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				));
		scene.addSceneObject(new Sphere(
				"origin",// description,
				upperVertex, //centre,
				0.05,// radius,
				SurfaceColour.WHITE_SHINY,// surfaceProperty,
				scene,// parent,
				studio //studio
				));
		for(int counter = 0; counter < 1000; counter++) {
			scene.addSceneObject(new Sphere(
					"random point",// description,
					testTriangle.randomPointOnShape(), //centre,
					0.01,// radius,
					SurfaceColour.BLACK_MATT,// surfaceProperty,
					scene,// parent,
					studio //studio
					));
		}
		System.out.println("testTriangle: uUnitVector is "+(testTriangle.getuUnitVector()).getLength()+"long");
		System.out.println("testTriangle: vUnitVector is "+(testTriangle.getvUnitVector()).getLength()+"long");
		
		//////////////////////////////////////////////////////////////////////////////////////////////
		////////Working out the normals to the sides of a ParametrisedTriangle////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		Vector3D upperVertex = new Vector3D(0,radiusOfDisc,0);
		ParametrisedTriangle testTriangle = new ParametrisedTriangle(
				"parametrised triangle for testing the getBoundary() method",// description,
				upperVertex,// vertex1,
				new Vector3D(0.5*Math.sqrt(3), -1.5, 0).getProductWith(radiusOfDisc),// vertex1ToVertex2,
				new Vector3D(-0.5*Math.sqrt(3),-1.5,0).getProductWith(radiusOfDisc),// vertex1ToVertex3,
				false,// semiInfinite,
				SurfaceColour.CYAN_MATT,// surfaceProperty,
				scene,// parent, 
				studio// studio
			);
*//*		Arrow normalToTriangle = new Arrow(
				"getNormal() of the testTriangle",// description,
				testTriangle.getVertex1(),// startPoint,
				Vector3D.sum(testTriangle.getVertex1(), testTriangle.getSurfaceNormal()),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLACK_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				);
		Arrow normalTo1To2 = new Arrow(
				"getNormal() of the testTriangle",// description,
				Vector3D.sum(testTriangle.getVertex1(),(testTriangle.getVertex1ToVertex2()).getProductWith(0.5)),// startPoint,
				Vector3D.sum(testTriangle.getVertex1(),(testTriangle.getVertex1ToVertex2()).getProductWith(0.5), Vector3D.crossProduct(testTriangle.getVertex1ToVertex2(), testTriangle.getSurfaceNormal()) ),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.RED_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				);
		Arrow normalTo1To3 = new Arrow(
				"getNormal() of the testTriangle",// description,
				Vector3D.sum(testTriangle.getVertex1(),(testTriangle.getVertex1ToVertex3()).getProductWith(0.5)),// startPoint,
				Vector3D.sum(testTriangle.getVertex1(),(testTriangle.getVertex1ToVertex3()).getProductWith(0.5), Vector3D.crossProduct(testTriangle.getSurfaceNormal(), testTriangle.getVertex1ToVertex3()) ),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.GREEN_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				);
		Vector3D vertex2To3 = Vector3D.difference(testTriangle.getVertex1ToVertex3(), testTriangle.getVertex1ToVertex2());
		Arrow normalTo2To3 = new Arrow(
				"getNormal() of the testTriangle",// description,
				Vector3D.sum(testTriangle.getVertex1(),testTriangle.getVertex1ToVertex2(),vertex2To3.getProductWith(0.5)),// startPoint,
				Vector3D.sum(testTriangle.getVertex1(),testTriangle.getVertex1ToVertex2(),vertex2To3.getProductWith(0.5), Vector3D.crossProduct(vertex2To3,testTriangle.getSurfaceNormal()) ),// endPoint,
				0.01,// shaftRadius,
				0.05,// tipLength,
				Math.PI/10,// tipAngle,
				SurfaceColour.BLUE_MATT,// surfaceProperty,
				scene, // parent, 
				studio// studio
				);
		sceneObjectIntersection.addSceneObject(normalToTriangle);
		scene.addSceneObject(normalTo1To2);
		scene.addSceneObject(normalTo1To3);
		scene.addSceneObject(normalTo2To3);
		scene.addSceneObject(testTriangle);
		//////////////////////////////////////////////////////////////////////////////////////////////
		////////Normals to the sides of a ParametrisedTriangle: DONE//////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////
		
//////////////////////////////////////////////////////////////////////////////////////////////
////////Testing the intersection of lens surfaces with the////////////////////////////////////
////////getBoundary() of Parametrised Disc and Triangle///////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////


		SceneObject lensSurface1 = new LensSurface(
				"one of the lens surfaces for testing getBoundary() of a disc", // description,
				new Vector3D(0,0,1.5), // focalPoint,
				1, // focalLength,
				1.5, // refractiveIndex,
				new Vector3D(0,0,1), // opticalAxisDirectionOutwards,
				0.96, // transmissionCoefficient,
				false, // shadowThrowing,
				scene, // parent,
				studio // studio
		);
		SceneObject lensSurface2 = new LensSurface(
				"one of the lens surfaces for testing getBoundary() of a disc", // description,
				new Vector3D(0,0,-1.5), // focalPoint,
				1, // focalLength,
				1.5, // refractiveIndex,
				new Vector3D(0,0,-1), // opticalAxisDirectionOutwards,
				0.96, // transmissionCoefficient,
				false, // shadowThrowing,
				scene, // parent,
				studio // studio
		);

		//////////////////////////////////////////////////////////////////////////////////////
		//////////////THIS PART IS NOW USED FOR PLAYING AROUND WITH THE BLACK/////////////////
		//////////////AREAS THAT SHOW UP IN A SIMPLE LENS/////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////

		sceneObjectIntersection.addSceneObject(lensSurface1);
		sceneObjectIntersection.addSceneObject(lensSurface2);	
		
		phi = 11*Math.PI/30;
		cameraViewDirection = new Vector3D(-Math.sin(phi), 0, Math.cos(phi));
		Vector3D rayStartPosition = getStandardCameraPosition();

		phi = //11*Math.PI/30;//
		0+(movie?0.01*2.*Math.PI*frame/(movieNumberOfFrames):0);
		Vector3D rayDirection = new Vector3D(-Math.sin(phi), 0, Math.cos(phi));

		EditableRayTrajectory ray = new EditableRayTrajectory(
				"Test ray",	// description,
				rayStartPosition,	// startPoint,
				0,	// startTime,
				rayDirection,	// startDirection,
				0.01,	// rayRadius,
				SurfaceColour.YELLOW_SHINY,	// surfaceProperty,
				255,	// maxTraceLevel,
				true,	// reportToConsole,
				scene,	// parent, 
				studio
		);
		scene.addSceneObject(ray);
		studio.traceRaysWithTrajectory();
		
		scene.removeSceneObject(sceneObjectIntersection);
		
		phi = 0;
		cameraViewDirection = new Vector3D(-Math.sin(phi), 0, Math.cos(phi));
		//////////////////////////////////////////////////////////////////////////////////////
		//////////////PLAYING WITH BLACK AREAS IN SIMPLE LENS: DONE///////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////

*//*		SceneObject boundaryOfDisc = testDisc.getBoundary();//(1, 1.5, 0.96);
		SceneObject boundaryOfTriangle = testTriangle.getBoundary();
//		sceneObjectIntersection.addSceneObject(boundaryOfTriangle);
		sceneObjectIntersection.addSceneObject(boundaryOfDisc);
		sceneObjectIntersection.addSceneObject(testPlane);
//		sceneObjectIntersection.addSceneObject(testDisc);
//////////////////////////////////////////////////////////////////////////////////////////////
////////Testing the intersections DONE////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////


		*/
/////////////////////////////////////////////////////////////////////////
////////Testing of FresnelLensSurface taking/////////////////////////////
////////ShapeWithRandomPointandBoundary argument/////////////////////////
/////////////////////////////////////////////////////////////////////////
/*		scene.addSceneObject(new FresnelLensSurface(
				"front surface",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				testDisc,
//				focalLengthMinFront,
//				focalLengthMaxFront,
//				numberOfLensSections,
				true,
				0.96,
				scene,
				studio));
		FresnelLensSurface testSurfaceFront = new FresnelLensSurface(
				"testSurfaceFront",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				testTriangle, 
//				6,// numberOfLensSections,
//				fMinAndMaxFront[0], //lensSurfaceFMin
//				fMinAndMaxFront[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
		);
		// add the back surface
		FresnelLensSurface testSurfaceBack = new FresnelLensSurface(
				"testSurfaceBack",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(-0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal.getReverse(),	// outwardsPrincipalishPlaneNormal
				backConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards.getReverse(),	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				testTriangle,
//				6,// numberOfLensSections,
//				fMinAndMaxBack[0], //lensSurfaceFMin
//				fMinAndMaxBack[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
		);*/
/*FresnelLens testFresnelLens = new FresnelLens(
				"testSurfaceBack",	// description
				lensCentre, // lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,
				backConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				thickness,// thickness
				minimumSurfaceSeparation, //minimumSurfaceSeparation
				testTriangle,
//				6,// numberOfLensSections,
//				fMinAndMaxBack[0], //lensSurfaceFMin
//				fMinAndMaxBack[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
		);
			
*/		
		
//		SceneObject boundaryOfDisc = testDisc.getBoundary();//(1, 1.5, 0.96);
//		SceneObject boundaryOfTriangle = testTriangle.getBoundary();
//		sceneObjectIntersection.addSceneObject(lensSurface1);
//		sceneObjectIntersection.addSceneObject(lensSurface2);	
//		sceneObjectIntersection.addSceneObject(boundaryOfTriangle);
//		sceneObjectIntersection.addSceneObject(boundaryOfDisc);
//		sceneObjectIntersection.addSceneObject(testPlane);
//		sceneObjectIntersection.addSceneObject(testDisc);
//		scene.addSceneObject(testFresnelLens);
/*		scene.addSceneObject(new FresnelLensShaped(
				"asdfghjk", // description,
				lensCentre, // lensCentre,
				forwardsCentralPlaneNormal,// forwardsCentralPlaneNormal,
				frontConjugatePoint, // frontConjugatePoint,
				backConjugatePoint, // backConjugatePoint,
				testTriangle, //apertureShape,
				1.5, // refractiveIndex,
				0.1, // thickness,
				0.01, // minimumSurfaceSeparation,
				true, // makeStepSurfacesBlack,
				0.96, // transmissionCoefficient,
				scene, // parent,
				studio // studio
			));
*/		
//		scene.addSceneObject(testSurfaceFront);
//		scene.addSceneObject(testSurfaceBack);
//		scene.addSceneObject(testTriangle);
//		scene.addSceneObject(testFresnelLens);
//		sceneObjectIntersection.addSceneObject(boundaryOfTriangle);
//		System.out.println("Front surface: min "+testSurfaceFront.getLensSurfaceFMin()+"\tmax: "+testSurfaceFront.getLensSurfaceFMax());
//		System.out.println("Back surface: min "+testSurfaceBack.getLensSurfaceFMin()+"\tmax: "+testSurfaceBack.getLensSurfaceFMax());
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking the backwards compatibility of FresnelLensSurface with /////////////////
		/////////////////lensSurfaceMin/Max or number of lens sections specified manually/////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
/*	
		//specified using a ShapeWithRandomPointAndBoundary
		FresnelLensSurface backwardsCompatibilityTestSurface0 = new FresnelLensSurface(
				"testSurface set with ShapeWithRandomPointAndBoundary",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
				testTriangle, 
//				6,// numberOfLensSections,
//				fMinAndMaxFront[0], //lensSurfaceFMin
//				fMinAndMaxFront[1], //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
				);
		System.out.println("getLensSurfaceFMin0 = "+backwardsCompatibilityTestSurface0.getLensSurfaceFMin());
		System.out.println("getLensSurfaceFMax0 = "+backwardsCompatibilityTestSurface0.getLensSurfaceFMax());
		
		//specified using lensSurfaceFMin & lensSurfaceFMax focal lengths
		FresnelLensSurface backwardsCompatibilityTestSurface1 = new FresnelLensSurface(
				"testSurfaceset with lensSurfaceFMin & lensSurfaceFMax",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
//				testTriangle, 
//				6,// numberOfLensSections,
				backwardsCompatibilityTestSurface0.getLensSurfaceFMin(), //lensSurfaceFMin
				backwardsCompatibilityTestSurface0.getLensSurfaceFMax(), //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
				).transform(new Translation(new Vector3D(1,0,0)));
		System.out.println("getLensSurfaceFMin1 = "+backwardsCompatibilityTestSurface1.getLensSurfaceFMin());
		System.out.println("getLensSurfaceFMax1 = "+backwardsCompatibilityTestSurface1.getLensSurfaceFMax());
		
		//specified using numberOfLensSections focal lengths
		FresnelLensSurface backwardsCompatibilityTestSurface2 = new FresnelLensSurface(
				"testSurface set with numberOfLensSections",	// description
				Vector3D.sum(lensCentre, opticalAxisDirectionForwards.getWithLength(0.5*minimumSurfaceSeparation)),	// lensCentre
				forwardsCentralPlaneNormal,	// outwardsPrincipalishPlaneNormal
				frontConjugatePoint,	// focalPoint
				1.5, //refractiveIndex,
				opticalAxisDirectionForwards,	// opticalAxisDirection
				0.5*(thickness - minimumSurfaceSeparation),	// thickness
//				testTriangle, 
				2,// numberOfLensSections,
//				1.8795386041941828,//backwardsCompatibilityTestSurface0.getLensSurfaceFMin(), //lensSurfaceFMin
//				1.9999890852305091,//backwardsCompatibilityTestSurface0.getLensSurfaceFMax(), //lensSurfaceFMax
				false, //makeStepSurfacesBlack,
				0.96, //transmissionCoefficient,
				scene, //parent
				studio //studio
				).transform(new Translation(new Vector3D(2,0,0)));
		System.out.println("getLensSurfaceFMin2 = "+backwardsCompatibilityTestSurface2.getLensSurfaceFMin());
		System.out.println("getLensSurfaceFMax2 = "+backwardsCompatibilityTestSurface2.getLensSurfaceFMax());
		
		scene.addSceneObject(backwardsCompatibilityTestSurface0);
		scene.addSceneObject(backwardsCompatibilityTestSurface1);
		scene.addSceneObject(backwardsCompatibilityTestSurface2);
*/			
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking backwards compatibility of FresnelLensSurfaces: DONE////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking the backwards compatibility of FresnelLens with ////////////////////////
		/////////////////lensSurfaceMin/Max or number of lens sections specified manually/////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
/*	//specified using a ShapeWithRandomPointAndBoundary
		FresnelLens backwardsCompatibilityTestLens0 =  new FresnelLens(
				"test lens set with ShapeWithRandomPointAndBoundary",// description,
				lensCentre, // lensCentre,
				forwardsCentralPlaneNormal,// forwardsCentralPlaneNormal,
				frontConjugatePoint,// frontConjugatePoint,
				backConjugatePoint,// backConjugatePoint,
				1.5,// refractiveIndex,
				thickness,// thickness,
				minimumSurfaceSeparation, // minimumSurfaceSeparation,
				testDisc,// apertureShape,
				true, // makeStepSurfacesBlack,
				0.96, // transmissionCoefficient,
				scene, // parent,
				studio // studio
			);
		System.out.println("lens0 front fmin = "+backwardsCompatibilityTestLens0.getFocalLengthMinFront());
		System.out.println("lens0 front fmax = "+backwardsCompatibilityTestLens0.getFocalLengthMaxFront());
		System.out.println("lens0 back fmin = "+backwardsCompatibilityTestLens0.getFocalLengthMinBack());
		System.out.println("lens0 back fmax = "+backwardsCompatibilityTestLens0.getFocalLengthMaxBack());
		
		//specified using the min and max focal lengths on each side
		FresnelLens backwardsCompatibilityTestLens1 =  new FresnelLens(
				"test lens set with ShapeWithRandomPointAndBoundary",// description,
				lensCentre, // lensCentre,
				forwardsCentralPlaneNormal,// forwardsCentralPlaneNormal,
				frontConjugatePoint,// frontConjugatePoint,
				backConjugatePoint,// backConjugatePoint,
				1.5,// refractiveIndex,
				thickness,// thickness,
				minimumSurfaceSeparation, // minimumSurfaceSeparation,
				backwardsCompatibilityTestLens0.getFocalLengthMinFront(),
				backwardsCompatibilityTestLens0.getFocalLengthMaxFront(),
				backwardsCompatibilityTestLens0.getFocalLengthMinBack(),
				backwardsCompatibilityTestLens0.getFocalLengthMaxBack(),
				true, // makeStepSurfacesBlack,
				0.96, // transmissionCoefficient,
				scene, // parent,
				studio // studio
			).transform(new Translation(new Vector3D(1,0,0)));
		System.out.println("lens1 front fmin = "+backwardsCompatibilityTestLens1.getFocalLengthMinFront());
		System.out.println("lens1 front fmax = "+backwardsCompatibilityTestLens1.getFocalLengthMaxFront());
		System.out.println("lens1 back fmin = "+backwardsCompatibilityTestLens1.getFocalLengthMinBack());
		System.out.println("lens1 back fmax = "+backwardsCompatibilityTestLens1.getFocalLengthMaxBack());
		
		//specified using the min and max focal lengths on each side
		FresnelLens backwardsCompatibilityTestLens2 =  new FresnelLens(
				"test lens set with ShapeWithRandomPointAndBoundary",// description,
				lensCentre, // lensCentre,
				forwardsCentralPlaneNormal,// forwardsCentralPlaneNormal,
				frontConjugatePoint,// frontConjugatePoint,
				backConjugatePoint,// backConjugatePoint,
				1.5,// refractiveIndex,
				thickness,// thickness,
				minimumSurfaceSeparation, // minimumSurfaceSeparation,
				2, //numberOfLensSections
				true, // makeStepSurfacesBlack,
				0.96, // transmissionCoefficient,
				scene, // parent,
				studio // studio
			).transform(new Translation(new Vector3D(2,0,0)));
		System.out.println("lens2 front fmin = "+backwardsCompatibilityTestLens2.getFocalLengthMinFront());
		System.out.println("lens2 front fmax = "+backwardsCompatibilityTestLens2.getFocalLengthMaxFront());
		System.out.println("lens2 back fmin = "+backwardsCompatibilityTestLens2.getFocalLengthMinBack());
		System.out.println("lens2 back fmax = "+backwardsCompatibilityTestLens2.getFocalLengthMaxBack());
				
		scene.addSceneObject(backwardsCompatibilityTestLens0);
		scene.addSceneObject(backwardsCompatibilityTestLens1);
		scene.addSceneObject(backwardsCompatibilityTestLens2);
		
*/			
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking the backwards compatibility of FresnelLens: DONE////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////

		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking that transformations on FresnelLensShaped work alright//////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////		
/*		FresnelLensShaped transformationTest = new FresnelLensShaped(
				"making sure that transformations work ok", // description,
				lensCentre, // lensCentre,
				forwardsCentralPlaneNormal, // forwardsCentralPlaneNormal,
				frontConjugatePoint, // frontConjugatePoint,
				backConjugatePoint, // backConjugatePoint,
				1.5, // refractiveIndex,
				thickness, // thickness,
				minimumSurfaceSeparation, // minimumSurfaceSeparation,
				testDisc, // apertureShape,
				true, // makeStepSurfacesBlack,
				0.96, // transmissionCoefficient,
				scene, // parent,
				studio // studio
			);

		scene.addSceneObject(transformationTest);
*/		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Checking that transformations on FresnelLensShaped work alright: DONE////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////	
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Debugging the IdealThinLensSimplicialComplex/////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////	
		/*
		//from the EditableLensSimplicialComplex constructor (lines 160-220)
		// create a physical-space simplicial complex
		
				Vector3D optimumOutsideViewingPosition = new Vector3D(1,0,0);
				// create a new array of vertices
				ArrayList<Vector3D> vertices = new ArrayList<Vector3D>();
				// add a few vertices
				vertices.add(new Vector3D(-1.5, 1.1, 10+0));	// 0; back left bottom vertex
				vertices.add(new Vector3D( 1, 1, 10+0));	// 1; back right bottom vertex
				vertices.add(new Vector3D( 0, -.5, 10+0));	// 2; front bottom vertex
				vertices.add(new Vector3D( 0, 0, 10+0.6));	// 3; lower inner vertex
				vertices.add(new Vector3D( 0, 0, 10+1.2));	// 4; upper inner vertex
				vertices.add(new Vector3D( 0, 0, 10+2));	// 5; top vertex
				// create a new array of edges
				ArrayList<Edge> edges = new ArrayList<Edge>();
				// add a few vertices
				try {
					edges.add(new Edge(0, 1));	// 0
					edges.add(new Edge(1, 2));	// 1
					edges.add(new Edge(2, 0));	// 2
					edges.add(new Edge(3, 0));	// 3
					edges.add(new Edge(3, 1));	// 4
					edges.add(new Edge(3, 2));	// 5
					edges.add(new Edge(4, 0));	// 6
					edges.add(new Edge(4, 1));	// 7	
					edges.add(new Edge(4, 2));	// 8
					edges.add(new Edge(5, 0));	// 9
					edges.add(new Edge(5, 1));	// 10
					edges.add(new Edge(5, 2));	// 11
					edges.add(new Edge(3, 4));	// 12
					edges.add(new Edge(4, 5));	// 13
					// create a simplicial complex with the physical-space vertices and standard edges, and with the faces and simplices inferred
					SimplicialComplex simplicialComplex;
					simplicialComplex = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(vertices, edges);
					// create a new array of vertices
					ArrayList<Vector3D> verticesV = new ArrayList<Vector3D>();
					// add a few vertices
					verticesV.add(new Vector3D(-1.5, 1.1, 10+0));	// 0; back left bottom vertex
					verticesV.add(new Vector3D( 1, 1, 10+0));	// 1; back right bottom vertex
					verticesV.add(new Vector3D( 0, -.5, 10+0));	// 2; front bottom vertex
					verticesV.add(new Vector3D( 0, 0, 10+0.8));	// 3; lower inner vertex
					verticesV.add(new Vector3D( 0, 0, 10+1.4));	// 4; upper inner vertex
					verticesV.add(new Vector3D( 0, 0, 10+2));	// 5; top vertex

					IdealThinLensSimplicialComplex lensSimplicialComplex = new IdealThinLensSimplicialComplex(simplicialComplex, verticesV);
					
					lensSimplicialComplex.setLensTypeRepresentingFace(LensType.IDEAL_THIN_LENS);
					lensSimplicialComplex.setOptimumOutsideViewingPosition(optimumOutsideViewingPosition);
					
				} catch (InconsistencyException e) {
					e.printStackTrace();
				}
		EditableSceneObjectCollection testComplex = new EditableLensSimplicialComplex("Lens Simplicial Complex for debugging", scene,studio).transform(new Translation(new Vector3D(0,0,0+(movie?-10.*frame/(movieNumberOfFrames):0))));
				scene.addSceneObject(testComplex);*/
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////Debugging the IdealThinLensSimplicialComplex: DONE///////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////	
			
		//////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////Visualising the Fresnel lens we're going to get machined in Durham///////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
/**/	
				double xMin = -1;
				double xMax = +1;
				double deltaX = 0.05;
				double yMin = -1;
				double yMax = +1;
				double deltaY = 0.05;
				ParametrisedDisc apertureDisc = new ParametrisedDisc(
						"disc for computing the focal lengths of the FresnelLensSurfaces",// description,
						new Vector3D(0,0,0), //centre,
						new Vector3D(0,0,1), //normal,
						1, //radius,
						SurfaceProperty.NO_SURFACE_PROPERTY,// sp,
						null, //parent, 
						null //studio
						);
				FresnelLensSurface lensToBeMachined = new FresnelLensSurface(
						"the Fresnel lens that will be machined", //description,
						new Vector3D(0,0,0), //pointInPrincipalishPlane,
						new Vector3D(0,0,1), //outwardsPrincipalishPlaneNormal,
						new Vector3D(0,0,4), //focalPoint,   !!!!!THIS IS ALSO TOTALLY ARBITRARY!!!!!!
						1.5, //refractiveIndex ???NEED MORE PRECISE VALUE???
						new Vector3D(0,0,1),  //opticalAxisDirection,
						0.1, //approximateThickness, !!!!!THIS IS A TOTALLY RANDOM VALUE THAT I PUT IN!!!!!
						apertureDisc,
						false, //makeStepSurfacesBlack,
						0.92, //transmissionCoefficient,
						null, //parent,
						null //studio
				);
				scene.addSceneObject(lensToBeMachined);
				FileWriter pointCloud;
				try {
					pointCloud = new FileWriter("FresnelLensPointCloud-DEMO.txt");

					for(double x = xMin; x < xMax; x += deltaX) {
						for(double y = yMin; y < yMax; y += deltaY)
						{
							Ray pointMaker = new Ray(new Vector3D(x,y,1), //position
									new Vector3D(0,0,-1), //direction
									0//time
									);
							RaySceneObjectIntersection intersectionPoint = lensToBeMachined.getClosestRayIntersection(pointMaker);
							String intersectionPointString = intersectionPoint.toString();//(lensToBeMachined.getClosestRayIntersection(pointMaker)).toString();   
							String positionString = intersectionPointString.substring(intersectionPointString.indexOf("<position>") + 10, intersectionPointString.indexOf("</position>"));
							String coordinates;
							if (intersectionPoint==RaySceneObjectIntersection.NO_INTERSECTION) {
								coordinates = x+"\t"+y+"\t"+"0";
							} else {
								coordinates = (positionString.substring(positionString.indexOf("(") + 1, positionString.indexOf(")"))).replaceAll(", ", "\t");
							}
							System.out.println(coordinates);
							pointCloud.write(coordinates+"\n");
						}

					}
					pointCloud.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scene.addSceneObject(lensToBeMachined);
				//////////////A REASON FOR INTRODUCING SOME ROUNDING////////////
				///set xMin = yMin = 0 and deltaX = deltaY = 0.1
				/*for(double x = xMin; x < xMax; x += deltaX) {
					for(double y = yMin; y < yMax; y += deltaY)
						{
						System.out.println(x+"\t"+y);
						}}*/
				////////////////////////////////////////////////////////////////
	}




	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
        Runnable r = new DiagnosticDemo();

        EventQueue.invokeLater(r);
	}
}
