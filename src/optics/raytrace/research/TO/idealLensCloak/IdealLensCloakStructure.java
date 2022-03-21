package optics.raytrace.research.TO.idealLensCloak;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfacePropertyLayerStack;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTriangle;
import optics.raytrace.exceptions.SceneException;


/**
 * Calculates different views of an ideal-lens cloak.
 * 
 * @author Johannes Courtial
 */
public class IdealLensCloakStructure extends NonInteractiveTIMEngine
{
	// additional parameters
	
	/**
	 * should lenses La, the outer pyramid lenses, be shown?
	 */
	protected boolean showLa;

	/**
	 * should lenses Lb, the upper inner pyramid lenses, be shown?
	 */
	protected boolean showLb;

	/**
	 * should lenses Lc, the lower inner pyramid lenses, be shown?
	 */
	protected boolean showLc;

	/**
	 * should lens Ld, the base lens, be shown?
	 */
	protected boolean showLd;

	/**
	 * should lenses Le, the lower vertical lenses, be shown?
	 */
	protected boolean showLe;

	/**
	 * should lenses Lf, the upper vertical lenses, be shown?
	 */
	protected boolean showLf;

	/**
	 * show nodal point N0, which is the nodal point of lens Ld
	 */
	protected boolean showN0;

	/**
	 * show nodal point N1, which is the nodal point of lenses Lc and Le
	 */
	protected boolean showN1;

	/**
	 * show nodal point N2, which is the nodal point of lenses Lb
	 */
	protected boolean showN2;

	/**
	 * show nodal point N3, which is the nodal point of lenses La and Lf
	 */
	protected boolean showN3;
	
	/**
	 * show the cylinder-frame model
	 */
	protected boolean showFrames;
	
	/**
	 * show a sphere inside the bottom cell
	 */
	protected boolean showSphere;
	
	/**
	 * value of the ratio h1E / h1, a measure of how distorted EM-space is relative to physical space;
	 * set to some value very close to 1 (e.g. 0.999) to model lenses with infinite focal length
	 */
	protected double h1EOverH1;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public IdealLensCloakStructure()
	{
		super();
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// for movie version
		numberOfFrames = 50;
		firstFrame = 0;
		lastFrame = numberOfFrames-1;
		
		// start off with everything being set to false
		showLa = false;
		showLb = false;
		showLc = false;
		showLd = false;
		showLe = false;
		showLf = false;
		
		showN0 = false;
		showN1 = false;
		showN2 = false;
		showN3 = false;
		
		showFrames = false;
		
		showSphere = true;
		
		h1EOverH1 = 2;
		
		// un-comment the following to show all nodal points
		// showN0 = true; showN1 = true; showN2 = true; showN3 = true; showFrames = true;
		
		// un-comment the following to show lenses La and their nodal point
		// showLa = true; showN3 = true; showFrames = true;
		
		// un-comment the following to show lenses Lb and their nodal point
		// showLb = true; showN2 = true; showFrames = true;

		// un-comment the following to show lenses Lc and their nodal point
		// showLc = true; showN1 = true; showFrames = true;

		// un-comment the following to show lens Ld and its nodal point
		// showLd = true; showN0 = true; showFrames = true;
		
		// un-comment the following to show lenses Le and their nodal point
		// showLe = true; showN1 = true; showFrames = true;

		// un-comment the following to show lenses Lf and their nodal point
		// showLf = true; showN3 = true; showFrames = true;

		// camera parameters are set in createStudio()
	}

	
	@Override
	public String getClassName()
	{
		return
				"IdealLensCloakStructure"
				+ " h1E="+h1EOverH1+"*h1"
				+ (showLa?" La":"")
				+ (showLb?" Lb":"")
				+ (showLc?" Lc":"")
				+ (showLd?" Ld":"")
				+ (showLe?" Le":"")
				+ (showLf?" Lf":"")
				+ (showN0?" N0":"")
				+ (showN1?" N1":"")
				+ (showN2?" N2":"")
				+ (showN3?" N3":"")
				+ (showSphere?" sphere":"")
				+ (showFrames?" frames":"")
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
		cameraApertureSize = ApertureSizeType.SMALL;

		super.populateSimpleStudio();
		
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();

		
		double h = 2;
		double h1 = h*0.333;
		double h2 = h*0.666;
		double r = h*2./3;
		
		double frameRadius = 0.0175;
		double nodalPointRadius = 2*frameRadius;
		
		Vector3D
			baseCentre = new Vector3D(0, -1+Math.max(frameRadius, nodalPointRadius), 0),
			front = new Vector3D(0, 0, -1),
			right = new Vector3D(1, 0, 0),
			up = new Vector3D(0, 1, 0);
		
		EditableIdealLensCloak cloak = new EditableIdealLensCloak(
				"Ideal-lens cloak",	// description
				baseCentre,	// baseCentre
				front,	// frontDirection
				right,	// rightDirection
				up,	// topDirection
				r,	// baseRadius
				h,	// height
				h1,	// heightLowerInnerVertexP
				h2,	// heightUpperInnerVertexP
				h1EOverH1*h1,	// heightLowerInnerVertexE
				0.9,	// gCLAsTransmissionCoefficient
				showFrames,	// showFrames
				frameRadius,	// frameRadius
				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
				// false,	// showPlaceholderSurfaces
				LensElementType.IDEAL_THIN_LENS,	// lens-element type
				scene,	// parent
				studio
			);
		scene.addSceneObject(cloak);
		
		// pick out specific surfaces
		if(showLa)
		{
			// lenses "a" are the outer lenses
			colourLens(cloak, "Front outer lens");
			colourLens(cloak, "Left outer lens");
			colourLens(cloak, "Right outer lens");
		}
		if(showLb)
		{
			// lenses "b" are the upper inner pyramid surfaces
			colourLens(cloak, "Front upper inner pyramid lens");
			colourLens(cloak, "Left upper inner pyramid lens");
			colourLens(cloak, "Right upper inner pyramid lens");
		}
		if(showLc)
		{
			// lenses "c" are the lower inner pyramid surfaces
			colourLens(cloak, "Front lower inner pyramid lens");
			colourLens(cloak, "Left lower inner pyramid lens");
			colourLens(cloak, "Right lower inner pyramid lens");
		}
		if(showLd)
		{
			// lens "d" is the base lens
			colourLens(cloak, "Base outer lens");
		}
		if(showLe)
		{
			// lenses "e" are the lower vertical lenses
			colourLens(cloak, "Back lower vertical lens");
			colourLens(cloak, "Left lower vertical lens");
			colourLens(cloak, "Right lower vertical lens");
		}
		if(showLf)
		{
			// lenses "f" are the upper vertical lenses
			colourLens(cloak, "Back upper vertical lens");
			colourLens(cloak, "Left upper vertical lens");
			colourLens(cloak, "Right upper vertical lens");
		}
		
		if(showN0)
		{
			addNodalPointSphere(
					baseCentre,	// centre,
					scene, 
					nodalPointRadius	// radius
				);
		}
		if(showN1)
		{
			addNodalPointSphere(
					Vector3D.sum(baseCentre, up.getWithLength(h1)),	// centre,
					scene, 
					nodalPointRadius	// radius
				);
		}
		if(showN2)
		{
			addNodalPointSphere(
					Vector3D.sum(baseCentre, up.getWithLength(h2)),	// centre,
					scene, 
					nodalPointRadius	// radius
				);
		}
		if(showN3)
		{
			addNodalPointSphere(
					Vector3D.sum(baseCentre, up.getWithLength(h)),	// centre,
					scene, 
					nodalPointRadius	// radius
				);
		}
		
		if(showSphere)
		{
			scene.addSceneObject(new EditableScaledParametrisedSphere(
					"Sphere inside the cloak",	// description
					Vector3D.sum(baseCentre, up.getWithLength(0.5*h1)),	// centre
					0.2*h1,
					new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, false),	// surfaceProperty
					scene,	// parent
					studio	// studio
				));
		}
	}
	
	protected void colourLens(EditableIdealLensCloak cloak, String name)
	{
		// find the lens in the cloak
		EditableTriangle lens = (EditableTriangle)(cloak.getFirstSceneObjectWithDescription(name, true));
		
		// make its surface visible by giving it a cyan tint (and don't add a shadow)
		lens.setSurfaceProperty(
				new SurfacePropertyLayerStack(
						lens.getSurfaceProperty(),
						new ColourFilter(DoubleColour.CYAN, false)
					)
			);
	}
	
	protected void addNodalPointSphere(Vector3D centre, SceneObjectContainer scene, double radius)
	{
		scene.addSceneObject(new EditableScaledParametrisedSphere(
				"Nodal point",	// description
				centre,
				radius,
				SurfaceColour.BLACK_SHINY,	// surfaceProperty
				scene,	// parent
				scene.getStudio()	// studio
			));
	}
	
	public static void main(final String[] args)
	{
		(new IdealLensCloakStructure()).run();
	}
}
