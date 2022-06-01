package optics.raytrace.surfaces.surfaceOfPixelArray;

import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceOfRefractiveViewRotator;
import optics.raytrace.voxellations.Voxellation;
import optics.raytrace.voxellations.SetOfSurfaces.OutwardsNormalOrientation;

public class SurfaceSeparatingRefractiveVoxels extends SurfaceSeparatingVoxels
{
	private static final long serialVersionUID = 5865435873080842192L;

	public SurfaceSeparatingRefractiveVoxels(
			SurfaceOfPixelArray surfaceOfPixelArray,
			int[] voxellationIndicesOnInside,
			int voxellationNumber,
			OutwardsNormalOrientation outwardsNormalOrientation,
			int traceLevel
			)
	{
		super(surfaceOfPixelArray, voxellationIndicesOnInside, voxellationNumber, outwardsNormalOrientation, traceLevel);
	}

	@Override
	public SurfaceProperty clone() {
		return new SurfaceSeparatingRefractiveVoxels(
				surfaceOfPixelArray,
				voxellationIndicesOnInside,
				voxellationNumber,
				outwardsNormalOrientation,
				getTraceLevel()
				);
	}


	//
	// SurfaceProperty methods
	//

	@Override
	public DoubleColour getColour(Ray r, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevelInside,
			RaytraceExceptionHandler raytraceExceptionHandler)
					throws RayTraceException
	{		
		if(traceLevelInside <= 0) return DoubleColour.BLACK;

		if(Orientation.getRayOrientation(r, i) == Orientation.INWARDS)
		{
			// this only happens if the ray passes through the surface again (when it shouldn't)
			return surfaceOfPixelArray.getColourStartingInPixel(
					voxellationIndicesOnInside,
					r.getBranchRay(i.p, r.getD(), i.t, r.isReportToConsole()),	// .getAdvancedRay(MyMath.TINY),
					i,
					scene,
					l,
					getTraceLevel(),
					traceLevelInside-1,
					raytraceExceptionHandler
					);
		}


		// calculate voxel indices of new voxel on the other side of this surface
		// first, copy the indices of the voxel on the inside, ...
		int voxellationIndicesOnOutside[] = new int[voxellationIndicesOnInside.length];
		for(int v=0; v<voxellationIndicesOnInside.length; v++) voxellationIndicesOnOutside[v] = voxellationIndicesOnInside[v];
		// ... then alter the relevant one
		voxellationIndicesOnOutside[voxellationNumber] += outwardsNormalOrientation.getSign();

		if(r.isReportToConsole())
		{
			System.out.println(
					"Ray passing through surface "+i.o.description
					+", from voxel "+Voxellation.toString(voxellationIndicesOnInside)
					+" to voxel "+Voxellation.toString(voxellationIndicesOnOutside)
					+", traceLevel="+getTraceLevel()
					+", traceLevelInside="+traceLevelInside
					+" (SurfaceSeparatingVoxels::getColour)"
					);
		}

		//getting the objects in the corresponding voxells
		SceneObject c1 = surfaceOfPixelArray.getSceneObjectsInPixel(voxellationIndicesOnInside);
		SceneObject c2 = surfaceOfPixelArray.getSceneObjectsInPixel(voxellationIndicesOnOutside);
		// light ray direction in the next cell
		Vector3D d2;
		double t;
		//set the ray direction depending on the scenario.
		boolean inside1 = c1.insideObject(i.p);
		boolean inside2 = c2.insideObject(i.p);
		if((inside1 && inside2) || (!inside1 && !inside2)) {
			//the ray is within one object and upon crossing the voxel surface in the neighbouring object(case 1) or
			//the ray is travelling from one voxel volume to another without intersecting any object.
			d2 = r.getD();
			t = 1;	// transmission coeffient = 1 (as we aren't passing through anything)
		}
		else {
			// the light ray leaves or enters a scene object through a voxel boundary, making the voxel boundary part of the object and having to refract the ray.

			double refractiveIndexRatio;
			if(inside1) refractiveIndexRatio = ((SurfaceOfRefractiveViewRotator)surfaceOfPixelArray).getRefractiveIndex();
			//the ray is within the first object but not the second which means it exits the object through a voxel surface.
			else refractiveIndexRatio = 1/((SurfaceOfRefractiveViewRotator)surfaceOfPixelArray).getRefractiveIndex();
			//the ray is not within the first object but within the second -> it enters through a voxel boundary.


			d2 = RefractiveSimple.getRefractedLightRayDirection(
					r.getD(),
					i.getNormalisedOutwardsSurfaceNormal(),
					refractiveIndexRatio
					).getNormalised();
			t = ((SurfaceOfRefractiveViewRotator)surfaceOfPixelArray).getSurfaceTransmissionCoefficient();
		}

		return surfaceOfPixelArray.getColourStartingInPixel(
				(Orientation.getRayOrientation(r, i) == Orientation.OUTWARDS)?voxellationIndicesOnOutside:voxellationIndicesOnInside,
				r.getBranchRay(i.p, d2, i.t, r.isReportToConsole()),	// .getAdvancedRay(MyMath.TINY),
				i,
				scene,
				l,
				getTraceLevel(),
				traceLevelInside-1,
				raytraceExceptionHandler
			).multiply(t);

	}

}
