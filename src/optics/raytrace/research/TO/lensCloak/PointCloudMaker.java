package optics.raytrace.research.TO.lensCloak;

import java.io.FileWriter;
import java.io.IOException;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.sceneObjects.Arrow;
import optics.raytrace.sceneObjects.FresnelLensShaped;
import optics.raytrace.sceneObjects.FresnelLensSurface;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.ParametrisedPlane;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;


/**
 * For creating the point clouds that Cyril in Durham needs to manufacture our Fresnel-lens surfaces
 * The required format is a tab-separated text file with x, y and z values
 */
public class PointCloudMaker
{
	// all lengths are measured in milimeters here; define a few unit factors
	static final double CM = 1e1;
	static final double MM = 1e0;
	static final double UM = 1e-3;
	static final double NM = 1e-6;

	private static double xMax = +1*CM;
	private static double xMin = -1*CM;
	private static double deltaX =125*UM;
	private static double yMax = +1*CM;
	private static double yMin = -1*CM;
	private static double deltaY = 125*UM;




	public static void main(final String[] args)
	{
		/**
		 * The PoitCloudMaker is set-up such that the lens is centred at the origin of the studios coordinate system. The full variation of the focal
		 * points can be achieved by varying the focal length to either side of the lens, the displacement of its centre of the lens from the optical
		 * axis and the angle that the normal to the lens makes with the optical axis. It is done like this so that the coordinates of the surface of
		 * the lens are in the coordinate system of the studio.
		 */
		/*
		 * Use this variable to select which side of the lens is to be mapped. Options are "Front" and "Back"
		 */
		String side = "Front";
		/*
		 * Use this variable to toggle between tilted and untilted configurations. Options are "Tilted" and "Untilted"
		 */
		String configuration = "Tilted"; //"Untilted";

		double lensRadius = CM;
		double refractiveIndex = 1.5;

		double frontFocalDistance = 5*CM;
		double backFocalDistance = 2*CM;
		double lensDisplacement = -0.5*CM;
		double alpha = Math.PI/6; //the angle that the lens normal makes with the optical axis direction
		
		
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
		SceneObjectIntersectionSimple lensSurface = new SceneObjectIntersectionSimple(
				"one of the two sufaces comprising the FresnelLensShaped", // description,
				null, // parent,
				null // studio
				);
		lensSurface.addSceneObject(lensToBeMachined);
		ParametrisedPlane lensSelector = null; //this will be defined inside the switch statement as orientation depends on which surface is being mapped
		FileWriter pointCloud;
		/*
		 * If imaging front surface: normal of lensSelector is lensNormal.getReverse() and the coordinates are converted to a RIGHT HANDED
		 * system according to (x, y, z) --> (-x, y, z).
		 * If imaging the back surface: normal of lensSelector is lensNormal and the coordinates are converted to a RIGHT HANDED system 
		 * according to (x, y, z) --> (x, y, -z)
		 */
		switch(side) {
		case("Front") :
			lensSelector = new ParametrisedPlane(
					"selects only one of the Fresnel lens surfaces", // description,
					lensCentre, // pointOnPlane,
					/**
					 * Note that lensNormal.getreverse() selects the front lens surface (the normal points towards the outside of the
					 * ParametrisedPlane so everything inside intersects with the FresnelLensSurface)
					 */
					lensNormal.getReverse(), // surfaceNormal,
					SurfaceColour.RED_MATT,// SurfaceProperty.NO_SURFACE_PROPERTY, // sp,
					null, // parent,
					null // studio
					);
		//here the lens surface to be turned into a height map is selected
		lensSurface.addSceneObject(lensSelector);

		try {
			pointCloud = new FileWriter("FresnelLensPointCloud.txt");
			// units are in mm
			// go through an array of x and y positions...
			String coordinates = null;
			/*
			 * When the lens is rotated half of it is in the z < 0 region and half of it is in the z>0 region. By adding the offset to the
			 * z-coordinate the entire lens is brought to the z > 0 positive region. i.e. in the height map the lens surface is the top of
			 * a mountain and the z=0 plane is the lowest that the machining heads needs to go. Presumably this helps with the fabrication.
			 * Note that this is needed only when theoptical axis is aligned with the z-axis and the lens is tilted. If the lens lies in the 
			 * xy-plane and the optical axis is in some arbitrary direction this should be zero and hence is defined in terms of angles (obtained
			 * via the inner product) between the lens normal and the z-direction. 
			 */
			double offsetInZ = Math.sqrt(1 - Vector3D.scalarProduct(lensNormal, new Vector3D(0,0,1))*Vector3D.scalarProduct(lensNormal, new Vector3D(0,0,1)))*lensRadius;
			for(double x = xMin; x <= xMax; x += deltaX) {//for some values of deltaX and/or deltaY we end up with (xMax-xMin)/DeltaX + (yMax-yMin)/deltaY + 1 many datapoints. 
				for(double y = yMin; y <= yMax; y += deltaY)
				{	//the origin of the rays is defined with respect to the centre of the lens
					Ray pointMaker = new Ray(new Vector3D(x,y,1*CM),//Vector3D.sum(centreOfOriginPlane, basis1.getProductWith(x), basis2.getProductWith(y)),// //position
							new Vector3D(0,0,-1), //direction
							0,	//time
							false
							);
					RaySceneObjectIntersection intersection = lensSurface.getClosestRayIntersection(pointMaker);//lensToBeMachined.getClosestRayIntersection(pointMaker);
					if (intersection==RaySceneObjectIntersection.NO_INTERSECTION)
					{
						/* 
						 * If no intersection then return (x,y,0)
						 */
						//the FRONT surface version <--> lensNormal.getReverse()
						coordinates = MyMath.doubleToString(-1.*x,9)+"\t"+MyMath.doubleToString(y,9)+"\t"+"0.000000000";

					}
					else
					{
						Vector3D intersectionPoint = intersection.p;
						/* 
						 * Note that the displacement of the lens centre is removed from the coordinates so that the height maps are centred at x=y=0.
						 * This makes it easier for the machining program in Durham to handle.
						 */
						//the Front surface version <--> lensNormal.getReverse()
						coordinates = 
								MyMath.doubleToString(-1.*(intersectionPoint.x), 9) + "\t" +
										MyMath.doubleToString(intersectionPoint.y, 9) + "\t" +
										MyMath.doubleToString(intersectionPoint.z + offsetInZ, 9);
					}
					pointCloud.write(coordinates+"\r\n");
				}
				//This is unnecessary. Just to indicate how it's progressing
				System.out.println("all y are done for x = "+MyMath.doubleToString(x));
				System.out.println("all y are done for x = "+coordinates.substring(0,coordinates.indexOf("\t"))+"\n");
			}
			pointCloud.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		break;
		case("Back") :
			lensSelector = new ParametrisedPlane(
					"selects only one of the Fresnel lens surfaces", // description,
					lensCentre, // pointOnPlane,
					/**
					 * Note that lensNormal.getreverse() selects the front lens surface (the normal points towards the outside of the
					 * ParametrisedPlane so everything inside intersects with the FresnelLensSurface)
					 */
					lensNormal,// surfaceNormal,
					SurfaceColour.RED_MATT,// SurfaceProperty.NO_SURFACE_PROPERTY, // sp,
					null, // parent,
					null // studio
					);
		//here the lens surface to be turned into a height map is selected
		lensSurface.addSceneObject(lensSelector);
		try {
			pointCloud = new FileWriter("FresnelLensPointCloud.txt");
			// units are in mm
			// go through an array of x and y positions...
			String coordinates = null;
			/*
			 * When the lens is rotated half of it is in the z < 0 region and half of it is in the z>0 region. By adding the offset to the
			 * z-coordinate the entire lens is brought to the z > 0 positive region. i.e. in the height map the lens surface is the top of
			 * a mountain and the z=0 plane is the lowest that the machining heads needs to go. Presumably this helps with the fabrication.
			 * Note that this is needed only when theoptical axis is aligned with the z-axis and the lens is tilted. If the lens lies in the 
			 * xy-plane and the optical axis is in some arbitrary direction this should be zero and hence is defined in terms of angles (obtained
			 * via the inner product) between the lens normal and the z-direction. 
			 */
			double offsetInZ = Math.sqrt(1 - Vector3D.scalarProduct(lensNormal, new Vector3D(0,0,1))*Vector3D.scalarProduct(lensNormal, new Vector3D(0,0,1)))*lensRadius;
			for(double x = xMin; x <= xMax; x += deltaX) {//for some values of deltaX and/or deltaY we end up with (xMax-xMin)/DeltaX + (yMax-yMin)/deltaY + 1 many datapoints. 
				for(double y = yMin; y <= yMax; y += deltaY)
				{	//the origin of the rays is defined with respect to the centre of the lens
					Ray pointMaker = new Ray(new Vector3D(x,y,1*CM),//Vector3D.sum(centreOfOriginPlane, basis1.getProductWith(x), basis2.getProductWith(y)),// //position
							new Vector3D(0,0,-1), //direction
							0,	//time
							false
							);
					RaySceneObjectIntersection intersection = lensSurface.getClosestRayIntersection(pointMaker);//lensToBeMachined.getClosestRayIntersection(pointMaker);
					if (intersection==RaySceneObjectIntersection.NO_INTERSECTION)
					{
						/* 
						 * If no intersection then return (x,y,0)
						 */
						//the BACK surface version <--> lensNormal
						coordinates = MyMath.doubleToString(x,9)+"\t"+MyMath.doubleToString(y,9)+"\t"+"0";

					}
					else
					{
						Vector3D intersectionPoint = intersection.p;
						/* 
						 * Note that the displacement of the lens centre is removed from the coordinates so that the height maps are centred at x=y=0.
						 * This makes it easier for the machining program in Durham to handle.
						 */
						//the BACK surface version <--> lensNormal
						coordinates = 
								MyMath.doubleToString(intersectionPoint.x, 9) + "\t" +
										MyMath.doubleToString(intersectionPoint.y, 9) + "\t" +
										MyMath.doubleToString(-1.*intersectionPoint.z + offsetInZ, 9);

					}

					pointCloud.write(coordinates+"\r\n");
				}
				//This is unnecessary. Just to indicate how it's progressing
				System.out.println("all y are done for x = "+MyMath.doubleToString(x));
				System.out.println("all y are done for x = "+coordinates.substring(0,coordinates.indexOf("\t"))+"\n");
			}
			pointCloud.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		break;
		default :
			break;
		}
		//This file is written with info for the Python script I'm using to visualise the surface.
		FileWriter pointDensity;
		try {
			pointDensity = new FileWriter("RangeAndDelta-xy.txt");
			pointDensity.write(Math.abs(xMax-xMin)+","+deltaX+"\n");
			pointDensity.write(Math.abs(yMax-yMin)+","+deltaY);
			pointDensity.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Your file is done my master");
	}
}
