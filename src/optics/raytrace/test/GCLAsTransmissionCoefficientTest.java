package optics.raytrace.test;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.surfaces.GeneralisedConfocalLensletArrays;
import optics.raytrace.surfaces.transmissionCoefficient.GCLAsTransmissionCoefficientCalculator;
import optics.raytrace.surfaces.transmissionCoefficient.OptimisedGCLAsTransmissionCoefficientCalculator;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

/**
 * For testing of individual bits
 */
public class GCLAsTransmissionCoefficientTest
{
	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Johannes Courtial
	 */
	public static void main(final String[] args)
	{
		Vector3D aHat = new Vector3D(0, 0, 1);
		Vector3D uHat = new Vector3D(1, 0, 0);
		Vector3D vHat = new Vector3D(0, 1, 0);
		double etaU = -2;
		double etaV = -1.;
		double deltaU = 0;
		double deltaV = 0;
		
		// parameters related to the transmission-coefficient calculation
		double sigma1u = 1;
		double sigma1v = 1;
		double sigma2u = 1;
		double sigma2v = 1;
		double alpha1u = 0;
		double alpha1v = 0;
		double alpha2u = 0;
		double alpha2v = 0;
				
		GeneralisedConfocalLensletArrays gCLAs = new GeneralisedConfocalLensletArrays(
				aHat,	// a
				uHat,	// u
				vHat,	// v
				etaU,
				etaV,
				deltaU,
				deltaV,
				GlobalOrLocalCoordinateSystemType.GLOBAL_BASIS,	// basis
				null,	// transmissionCoefficientCalculator
				null,	// pixellation
				true	// shadowThrowing
			);

//		GCLAsTransmissionCoefficientCalculator transmissionCoefficientCalculator = new GCLAsTransmissionCoefficientCalculator(
//				gCLAs,	// generalisedConfocalLensletArrays
//				sigma1u,
//				sigma1v,
//				sigma2u,
//				sigma2v,
//				alpha1u, 
//				alpha1v,
//				alpha2u, 
//				alpha2v
//			);

		System.out.println("(a,u) projection transmission coefficient="+GCLAsTransmissionCoefficientCalculator.calculateProjectionTransmissionCoefficient(
				0,	// dT
				1,	// dA
				etaU, deltaU, alpha1u, alpha2u, sigma1u, sigma2u, Orientation.OUTWARDS
			));

		System.out.println("(a,v) projection transmission coefficient="+GCLAsTransmissionCoefficientCalculator.calculateProjectionTransmissionCoefficient(
				0,	// dT
				1,	// dA
				etaV, deltaV, alpha1v, alpha2v, sigma1v, sigma2v, Orientation.OUTWARDS
			));

		OptimisedGCLAsTransmissionCoefficientCalculator optimisedTransmissionCoefficientCalculator = new OptimisedGCLAsTransmissionCoefficientCalculator(
				gCLAs,	// generalisedConfocalLensletArrays
				sigma1u,
				sigma1v,
				sigma2u,
				sigma2v,
				1,	// parallelityParameter
				new Vector3D(-1, 0, -1)	// pointAtFOVCentre
			);

		System.out.println("optimised transmission coefficient="+optimisedTransmissionCoefficientCalculator.calculateTransmissionCoefficient(
				new Vector3D(1, 0, 1),	// incidentRayDirection
				new RaySceneObjectIntersection(
						new Vector3D(0, 0, 0),	// p
						Plane.zPlane("z plane",	// description
								0,	// z0
								null,	// surfaceProperty
								null,	// parent
								null	// studio
							),
						0	// t
					),	// intersection
				Orientation.OUTWARDS	// orientation
			));

	}
}
