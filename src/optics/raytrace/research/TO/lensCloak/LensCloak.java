package optics.raytrace.research.TO.lensCloak;

import math.*;
import optics.raytrace.sceneObjects.TriangularThickLens;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;


/**
 * Calculates different views of a real-lens cloak.
 * 
 * @author Johannes Courtial
 */
public class LensCloak extends NonInteractiveTIMEngine
{
	protected double h;
	
	protected double h1;
	
	protected double h2;
	
	protected double r;
	
	protected double fA;
	
	protected double fB;
	
	protected double fC;
	
	protected double fD;
	
	protected double fE;
	
	protected double fF;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public LensCloak()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		h = 1.5;
		h1 = h/3.;
		h2 = h*2./3.;
		r = h/3.;
		fD = 300*h;

		// camera parameters are set in createStudio()
	}

	
	@Override
	public String getClassName()
	{
		return
				"LensCloak"
				+ " h="+h
				+ " h1="+h1
				+ " h2="+h2
				+ " r="+r
				+ " fD="+fD
				;
	}
	
	@Override
	public void populateStudio()
	throws SceneException
	{
		double phi = -0.25+(movie?2.*Math.PI*frame/(numberOfFrames+1):0);
		cameraViewDirection = new Vector3D(-Math.sin(phi), -.2, Math.cos(phi));
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 20;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = 
				ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		
		// define the directions
		Vector3D up = new Vector3D(0, 1, 0);
		Vector3D front = new Vector3D(0, 0, -1);
		Vector3D right = new Vector3D(1, 0, 0);
		
		// calculate the positions of different vertices
		Vector3D p0 = new Vector3D(0, -h/2., 0);
		Vector3D p1 = Vector3D.sum(p0, up.getWithLength(h1));
		Vector3D p2 = Vector3D.sum(p0, up.getWithLength(h2));
		Vector3D p3 = Vector3D.sum(p0, up.getWithLength(h));
		Vector3D[] v = new Vector3D[3];
		for(int i=0; i<3; i++)
		{
			double angle = i*2.*Math.PI/3. - Math.PI/6.;
			v[i] = Vector3D.sum(p0, right.getWithLength(Math.cos(angle)), front.getWithLength(Math.sin(angle)));
			// System.out.println("LensCloak::populateStudio: v["+i+"]="+v[i]+" (angle="+angle+")");
		}
		
		// calculate the focal lengths of the different lens classes
		double fA = (h-h2)*(fD*(h1-h)+h1*h)*r / (h1*h2*Math.sqrt(4.*h*h + r*r));
		double fB = fD*(h1-h2)*(h2-h)*r / (h1*h*Math.sqrt(4.*h2*h2 + r*r));
		double fC = -(h1-h2)*(fD*(h1-h)+h1*h)*r / (h2*h*Math.sqrt(4.*h1*h1 + r*r));
		double fE = (h2-h1)*(fD*(h1-h)+h1*h)*r*Math.sqrt(4.*h1*h1 + h*h) / (2.*Math.sqrt(3)*h1*h2*h*Math.sqrt(4.*h1*h1 + r*r));
		double fF = (h2-h)*(fD*(h1-h)+h1*h)*r*Math.sqrt(4.*h*h + r*r) / (2.*Math.sqrt(3)*h1*h2*h*Math.sqrt(4.*h*h + r*r));

		System.out.println("LensCloak::populateStudio: fA="+fA+", fB="+fB+", fC="+fC+", fD="+fD+", fE="+fE+", fF="+fF);
		
		// SceneObjectUnion cloak = new SceneObjectUnion(
		SceneObjectContainer cloak = new SceneObjectContainer(
				"Lens cloak",	// description
				scene,	// parent
				studio
			);
		
		cloak.addSceneObject(getTriangularLens(
				"Lens type A front",	// description
				fA,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				v[0],	// corner2
				v[1],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type A right",	// description
				fA,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				v[1],	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type A left",	// description
				fA,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				v[2],	// corner2
				v[0],	// corner3
				cloak,	// parent
				studio
			));
		
		cloak.addSceneObject(getTriangularLens(
				"Lens type B front",	// description
				fB,	// focalLength
				p2,	// principalPoint
				p2,	// corner1
				v[0],	// corner2
				v[1],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type B right",	// description
				fB,	// focalLength
				p2,	// principalPoint
				p2,	// corner1
				v[1],	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type B left",	// description
				fB,	// focalLength
				p2,	// principalPoint
				p2,	// corner1
				v[2],	// corner2
				v[0],	// corner3
				cloak,	// parent
				studio
			));

		cloak.addSceneObject(getTriangularLens(
				"Lens type C front",	// description
				fC,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				v[0],	// corner2
				v[1],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type C right",	// description
				fC,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				v[1],	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type C left",	// description
				fC,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				v[2],	// corner2
				v[0],	// corner3
				cloak,	// parent
				studio
			));

		cloak.addSceneObject(getTriangularLens(
				"Lens type D",	// description
				fD,	// focalLength
				p0,	// principalPoint
				v[0],	// corner1
				v[1],	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));

		cloak.addSceneObject(getTriangularLens(
				"Lens type E left",	// description
				fE,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				p2,	// corner2
				v[0],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type E right",	// description
				fE,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				p2,	// corner2
				v[1],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type E back",	// description
				fE,	// focalLength
				p1,	// principalPoint
				p1,	// corner1
				p2,	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));

		cloak.addSceneObject(getTriangularLens(
				"Lens type F left",	// description
				fF,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				p2,	// corner2
				v[0],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type F right",	// description
				fF,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				p2,	// corner2
				v[1],	// corner3
				cloak,	// parent
				studio
			));
		cloak.addSceneObject(getTriangularLens(
				"Lens type F back",	// description
				fF,	// focalLength
				p3,	// principalPoint
				p3,	// corner1
				p2,	// corner2
				v[2],	// corner3
				cloak,	// parent
				studio
			));

		scene.addSceneObject(cloak);
		
	}
	
	/**
	 * @param description
	 * @param focalLength
	 * @param principalPoint
	 * @param corner1
	 * @param corner2
	 * @param corner3
	 * @param parent
	 * @param studio
	 * @return	currently a PMMA lens, but the plan is to add other options such as ideal thin lens and lens hologram
	 * @throws SceneException
	 */
	protected SceneObject getTriangularLens(
			String description,
			double focalLength, Vector3D principalPoint, Vector3D corner1, Vector3D corner2, Vector3D corner3,
			SceneObject parent, 
			Studio studio
	) throws SceneException
	{
		double refractiveIndex = 1.49;	// PMMA -- see https://en.wikipedia.org/wiki/List_of_refractive_indices
		double thicknessAtThinnestPoint =
				// 0.01*h;
				MyMath.TINY;
		
		return new TriangularThickLens(
				description,
				focalLength,
				principalPoint,
				corner1,
				corner2,
				corner3,
				refractiveIndex,
				thicknessAtThinnestPoint,
				parent,
				studio
			);
	}
	
	public static void main(final String[] args)
	{
		(new LensCloak()).run();
	}
}
