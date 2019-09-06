package optics.raytrace.studioInitialisation;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableSpaceCancellingWedge;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisation;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * Initialisation of the scene and lights to an optical simulation of a curved space with the geometry/topology of a 4-sided pyramid.
 * @author johannes
 */
public class CurvedSpaceInitialisation extends StudioInitialisation
{
	@Override
	public String getDescription() {
		return "Curved space";
	}
	
	@Override
	public void initialiseSceneAndLights(SceneObjectContainer scene, Studio studio)
	{
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		scene.clear();
		
		// add the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(scene, studio));
		
		// add null-space wedges
		double nullSpaceWedgeAngle = MyMath.deg2rad(150);
		GluingType gluingType = GluingType.PERFECT;
		int noOfNegativeSpaceWedgesPerNullSpaceWedge = 10;
		double legLength = 10;
		double height = 2;
		boolean showSheets = true;
		double sheetTransmissionCoefficient = 0.999;
		boolean showEdges = true;
		double edgeRadius = 0.01;
		SurfaceProperty edgeSurfaceProperty = new SurfaceColour(new DoubleColour(1, 1, 1), DoubleColour.WHITE, false);
				
//		scene.addSceneObject(new EditableNullSpaceWedge(
//					"Right null-space wedge",	// description
//					nullSpaceWedgeAngle,	// wedgeAngle
//					new Vector3D(1, 0, 10),	// centre
//					new Vector3D(0, 1, 0),	// commonEdgeDirection
//					new Vector3D(1, 0, 0),	// bisectorDirection
//					legLength,	// legLength
//					height,	// height
//					showSheets,	// showSheets
//					sheetTransmissionCoefficient,	// sheetTransmissionCoefficient
//					showEdges,	// showEdges
//					edgeRadius,	// edgeRadius
//					edgeSurfaceProperty,	// edgeSurfaceProperty
//					nullSpaceWedgeType,	// nullSpaceWedgeType
//					noOfNegativeSpaceWedgesPerNullSpaceWedge,	// numberOfNegativeSpaceWedges
//					scene,
//					studio
//				));
//
//		scene.addSceneObject(new EditableNullSpaceWedge(
//				"Left null-space wedge",	// description
//				nullSpaceWedgeAngle,	// wedgeAngle
//				new Vector3D(-1, 0, 10),	// centre
//				new Vector3D(0, 1, 0),	// commonEdgeDirection
//				new Vector3D(-1, 0, 0),	// bisectorDirection
//				legLength,	// legLength
//				height,	// height
//				showSheets,	// showSheets
//				sheetTransmissionCoefficient,	// sheetTransmissionCoefficient
//				showEdges,	// showEdges
//				edgeRadius,	// edgeRadius
//				edgeSurfaceProperty,	// edgeSurfaceProperty
//				nullSpaceWedgeType,	// nullSpaceWedgeType
//				noOfNegativeSpaceWedgesPerNullSpaceWedge,	// numberOfNegativeSpaceWedges
//				scene,
//				studio
//			));
//
//		scene.addSceneObject(new EditableNullSpaceWedge(
//				"Top null-space wedge",	// description
//				nullSpaceWedgeAngle,	// wedgeAngle
//				new Vector3D(0, 0, 11),	// centre
//				new Vector3D(0, 1, 0),	// commonEdgeDirection
//				new Vector3D(0, 0, 1),	// bisectorDirection
//				legLength,	// legLength
//				height,	// height
//				showSheets,	// showSheets
//				sheetTransmissionCoefficient,	// sheetTransmissionCoefficient
//				showEdges,	// showEdges
//				edgeRadius,	// edgeRadius
//				edgeSurfaceProperty,	// edgeSurfaceProperty
//				nullSpaceWedgeType,	// nullSpaceWedgeType
//				noOfNegativeSpaceWedgesPerNullSpaceWedge,	// numberOfNegativeSpaceWedges
//				scene,
//				studio
//			));
//
//		scene.addSceneObject(new EditableNullSpaceWedge(
//				"Bottom null-space wedge",	// description
//				nullSpaceWedgeAngle,	// wedgeAngle
//				new Vector3D(0, 0, 9),	// centre
//				new Vector3D(0, 1, 0),	// commonEdgeDirection
//				new Vector3D(0, 0, -1),	// bisectorDirection
//				legLength,	// legLength
//				height,	// height
//				showSheets,	// showSheets
//				sheetTransmissionCoefficient,	// sheetTransmissionCoefficient
//				showEdges,	// showEdges
//				edgeRadius,	// edgeRadius
//				edgeSurfaceProperty,	// edgeSurfaceProperty
//				nullSpaceWedgeType,	// nullSpaceWedgeType
//				noOfNegativeSpaceWedgesPerNullSpaceWedge,	// numberOfNegativeSpaceWedges
//				scene,
//				studio
//			));

		nullSpaceWedgeAngle = MyMath.deg2rad(180);
		double baseRadius = 3;

		
		for(int i = 0; i<3; i++)
		{
			scene.addSceneObject(new EditableSpaceCancellingWedge(
					"Null-space wedge #"+i,	// description
					nullSpaceWedgeAngle,	// apexAngle
					new Vector3D(baseRadius*Math.cos(2*Math.PI*i/3.), 0, baseRadius*Math.sin(2*Math.PI*i/3.)),	// apexEdgeCentre
					new Vector3D(0, 1, 0),	// apexEdgeDirection
					new Vector3D(Math.cos(2*Math.PI*i/3.), 0, Math.sin(2*Math.PI*i/3.)),	// bisectorDirection
					legLength,	// legLength
					height,	// apexEdgeLength
					showSheets,	// showRefractingSurfaces
					MyMath.deg2rad(91),	// containmentMirrorsAngleWithSides
					sheetTransmissionCoefficient,	// surfaceTransmissionCoefficient
					showEdges,	// showEdges
					edgeRadius,	// edgeRadius
					edgeSurfaceProperty,	// edgeSurfaceProperty
					gluingType,	// gluingType
					noOfNegativeSpaceWedgesPerNullSpaceWedge,	// numberOfNegativeSpaceWedges
					scene,
					studio
					));
		}

		// a few scene objects inside the curved space
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"Sphere",
				new Vector3D(0.1, 0, 10),	// centre
				0.1,	// radius
				SurfaceColour.WHITE_SHINY,
				scene,
				studio
		));

		// add a few trajectories
		double rayRadius = 0.01;
		int trajectoriesMaxTraceLevel = 1000;
		boolean trajectoriesVisible = false;
		
		scene.addSceneObject(new EditableRayTrajectory(
				"Green ray trajectory",	// description
				new Vector3D(-0.5, 0, 10),	// startPoint
				0,	// startTime
				new Vector3D(0, 0, 1),	// startDirection
				rayRadius,	// rayRadius
				new SurfaceColour(new DoubleColour(0, 2, 0), DoubleColour.WHITE, false),	// surfaceProperty
				trajectoriesMaxTraceLevel,	// maxTraceLevel
				false,	// reportToConsole
				scene,	// parent
				studio
				),
				trajectoriesVisible
			);

		scene.addSceneObject(new EditableRayTrajectory(
				"Red ray trajectory",	// description
				new Vector3D(-0.5, 0, 10),	// startPoint
				0,	// startTime
				new Vector3D(1, 0, 1),	// startDirection
				rayRadius,	// rayRadius
				new SurfaceColour(new DoubleColour(2, 0, 0), DoubleColour.WHITE, false),	// surfaceProperty
				trajectoriesMaxTraceLevel,	// maxTraceLevel
				false,	// reportToConsole
				scene,	// parent
				studio
				),
				trajectoriesVisible
			);

		scene.addSceneObject(new EditableRayTrajectory(
				"Blue ray trajectory",	// description
				new Vector3D(-0.5, 0, 10),	// startPoint
				0,	// startTime
				new Vector3D(0.41421, 0, 1),	// startDirection
				rayRadius,	// rayRadius
				new SurfaceColour(new DoubleColour(0, 0, 2), DoubleColour.WHITE, false),	// surfaceProperty
				trajectoriesMaxTraceLevel,	// maxTraceLevel
				false,	// reportToConsole
				scene,	// parent
				studio
				),
				trajectoriesVisible
			);
	}
}
