package optics.raytrace.research.idealLensWormhole;

// import java.awt.Container;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;

import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
// import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.SceneException;


// import java.io.PrintStream;



// import optics.raytrace.GUI.sceneObjects.EditableCameraShape;






public class LensInsideCloak_old extends NonInteractiveTIMEngine
{
		
	private double BaseF;
	private double LensF;
	private double h1P;
	private double h2P;
	private double h1V;
	private double h;
	private double R;
	private Vector3D baseCentre;
	private Vector3D topDirection;
	private boolean Cloak;
	private boolean EquivLens;
	private double CalcF;
	private double z2;
	private double z4;
	private double zO;
	// private StudioInitialisationType studioInitialisation;


	
	// private Vector3D optimumOutsideSpaceViewingPosition;

	
	//  defining cloak params
	public LensInsideCloak_old()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		BaseF = 2;
		LensF = 3; //focal lens of inside lens
		R = 2.*2./3;// base radius
		h = 2.0;	// height of top vertex above base
		h1P = h*0.5;	// heightLowerInnerVertexP
		h1V = 1./(-h/BaseF + 1/h1P);	// heightLowerInnerVertexV
		h2P = h*0.75;	// heightUpperInnerVertexP
		z2 = 1;
		zO = 13;
		z4 = (BaseF*z2)/(BaseF - z2);
		CalcF = (BaseF*BaseF*LensF)/((BaseF-z2)*(BaseF-z2));
		
		
		baseCentre = new Vector3D(0, -1, 10);
		topDirection = new Vector3D(0, 1, 0);
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;	
		
		Cloak = true;
		
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's lens inside a cloak";
			windowWidth = 1500;
			windowHeight = 650;
		}
	}
		
	
	
	public void populateStudio()
	throws SceneException
	{
		// System.out.println("LensCloakVisualiser::populateStudio: frame="+frame);
		
		// the studio
		studio = new Studio();
		
		// Studio studio = new Studio();
		
		//setting the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1, scene, studio)); //the floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky


		double frameRadius = 0.02;
		
		
		if(Cloak) 
		{
		// the Cloak
		EditableIdealLensCloak idealLensCloak = new EditableIdealLensCloak(
				"Ideal-lens cloak",
				baseCentre,	// base centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				topDirection,	// top direction
				R,	// base radius
				h,	// height
				h1P,	// heightLowerInnerVertexP
				h2P,	// heightUpperInnerVertexP
				h1V,	// heightLowerInnerVertexE
				0.9,	// interface transmission coefficient
				true,	// show frames
				frameRadius,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				LensElementType.IDEAL_THIN_LENS,
				scene,
				studio	
			);
		scene.addSceneObject(idealLensCloak);
		
		scene.addSceneObject(new ParametrisedDisc(
				"thin lens",	//description,
				new Vector3D(0, 0, z2),	// centre,
				new Vector3D(0, 0, 1),	// normal,
				0.5,	// radius,
				new Vector3D(1, 0, 0),	// phi0Direction,
				new IdealThinLensSurfaceSurfaceCoordinates(
						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
						LensF,	// focalLength,
						0.9,	// transmissionCoefficient
						false	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
			));
		}
			
		
		if (EquivLens)
		{
			Cloak = false;
			
			
			
			scene.addSceneObject(new ParametrisedDisc(
					"equivalent lens",	//description,
					new Vector3D(0, 0, z4),	// centre,
					new Vector3D(0, 0, 1),	// normal,
					0.8,	// radius,
					new Vector3D(1, 0, 0),	// phi0Direction,
					new IdealThinLensSurfaceSurfaceCoordinates(
							new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
							CalcF,	// focalLength,
							0.9,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
				));
		}
			
			int
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 1;
			

		// a camera with a non-zero aperture size (so it simulates blur)
			ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(0, 0, 0),	// centre of aperture
				new Vector3D(0, 0, 4),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, 1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				zO,	// focusing distance
				0.,	// aperture radius
				1	// rays per pixel; the more, the less noisy the photo is
		);
		// System.out.println(camera.equals(null));
		
//	EditableCameraShape camera = new EditableCameraShape(	
//			"Camera",	// description
//			new Vector3D(0, 0, 10),	// apertureCentre
//			new Vector3D(0, 0, -1),	// forwardDirection
//			new Vector3D(0, 1, 0),	// topDirection
//			1,	// width
//			SurfaceColour.GREY50_MATT,	// surfacePropertyBody
//			SurfaceColour.GREY30_MATT,	// surfacePropertyLens
//			null,	// surfacePropertyGlass
//			scene, 
//			studio
//		);
		
		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);
	}


	
	
	private LabelledDoublePanel BaseFPanel, LensFPanel;
	private JCheckBox ShowEquivLensCheck;	
	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		JPanel Panel = new JPanel();
		Panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Panel", Panel);

		
		BaseFPanel = new LabelledDoublePanel("Focal length of Base Lens");
		BaseFPanel.setNumber(BaseF);
		Panel.add(BaseFPanel, "span");
		
		LensFPanel = new LabelledDoublePanel("Focal length of interior Lens");
		LensFPanel.setNumber(LensF);
		Panel.add(LensFPanel, "span");
				
		ShowEquivLensCheck = new JCheckBox("Shows the equivalent single ideal Lens");
		Panel.add(ShowEquivLensCheck, "span");
	}
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
			
		BaseF = BaseFPanel.getNumber();
		LensF = LensFPanel.getNumber();
		EquivLens = ShowEquivLensCheck.isSelected();
	}
		
	

	public static void main(final String[] args)
	{
		(new LensInsideCloak_old()).run();
	}
}







