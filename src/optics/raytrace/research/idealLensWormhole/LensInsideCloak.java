package optics.raytrace.research.idealLensWormhole;


import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;

import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;










public class LensInsideCloak extends NonInteractiveTIMEngine
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
	private Vector3D baseVertex1;
	private boolean Cloak;
	private boolean EquivLens;
	private double CalcF;
	private double z2;
	private double z4;
	private EditableLensSimplicialComplex lensSimplicialComplex;
	private LensType lensTypeRepresentingFace;
	private Vector3D optimumOutsideSpaceViewingPosition;
	
	private double cameraPhi;
	
	//  defining cloak params
	public LensInsideCloak()
	{
		super();
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		renderQuality = RenderQualityEnum.DRAFT;
		
		BaseF = 30;
		LensF = 40; //focal lens of inside lens
		R = 2.*2./3;// base radius
		h = 2.0;	// height of top vertex above base
		h1P = h*1/3;	// heightLowerInnerVertexP
		h1V = 1./(-h/BaseF + 1/h1P);	// heightLowerInnerVertexV
		h2P = h*2/3;	// heightUpperInnerVertexP
		z2 = 0.3; //z coord of principle lens point
//		z4 = (BaseF*z2)/(BaseF - z2);
//		CalcF = (BaseF*BaseF*LensF)/((BaseF-z2)*(BaseF-z2));
		lensTypeRepresentingFace = LensType.IDEAL_THIN_LENS;
		
		
		baseCentre = new Vector3D(0, 0, 0);
		topDirection = new Vector3D(0, 0, -2);
		baseVertex1 = new Vector3D(1.5,-1,0);
		
		// set to true for interactive version
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		
		// set all parameters
		
		renderQuality = RenderQualityEnum.DRAFT;	
		
		EquivLens = false;
		Cloak = false;
		
		cameraPhi = 0;
		
		
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
		
		
		//setting the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);		
		
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1, scene, studio)); //the floor
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky


		double frameRadius = 0.02;
		
//		// create a new lens simplicial complex...
//		lensSimplicialComplex = new EditableLensSimplicialComplex(
//				"Lens simplicial complex",	// description
//				scene,	// parent
//				studio
//			);
//		// ... and initialise it first as an ideal-lens cloak, for the purposes of raytracing and calculating the image of
//		// the optimum outside viewing position
//		lensSimplicialComplex.setLensTypeRepresentingFace(lensTypeRepresentingFace);;
//		lensSimplicialComplex.setShowStructureP(true);
//		lensSimplicialComplex.setVertexRadiusP(frameRadius);
//		lensSimplicialComplex.setShowStructureV(false);
//		lensSimplicialComplex.setVertexRadiusV(frameRadius);
//		
//		// initialise for the first time, which sets the properties of the simplicial complex such that it can do the mapping to outside space...
//		double h2V = lensSimplicialComplex.initialiseToOmnidirectionalLens(h1P, h2P, h1V, topDirection, baseCentre, baseVertex1);
//		
//		
//		scene.addSceneObject(lensSimplicialComplex);
//		
//		// ... then initialise again to add scene objects optimised for the correct optimum viewing position
//		lensSimplicialComplex.initialiseToOmnidirectionalLens(h1P, h2P, h1V, h2V, topDirection, baseCentre, baseVertex1);



		
		if(Cloak) 
		{
		// the Cloak
		EditableIdealLensCloak idealLensCloak = new EditableIdealLensCloak(
				"Ideal-lens cloak",
				baseCentre,	// base centre
				new Vector3D(0, -1, 0),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				topDirection,	// top direction
				R,	// base radius
				h,	// height
				h1P,	// heightLowerInnerVertexP
				h2P,	// heightUpperInnerVertexP
				BaseF*h1P/(BaseF-h1P),	// heightLowerInnerVertexE
				0.9,	// interface transmission coefficient
				true,	// show frames
				frameRadius,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				LensElementType.IDEAL_THIN_LENS,
				scene,
				studio	
			);
		
				
//baseLensFocalLength*heightLowerInnerVertexP / (baseLensFocalLength - heightLowerInnerVertexP),	// heightLowerInnerVertexE
		scene.addSceneObject(idealLensCloak);
		
		scene.addSceneObject(new ParametrisedDisc(
				"thin lens",	//description,
				new Vector3D(0, 0, z2),	// centre,
				new Vector3D(0, 0, 1),	// normal,
				0.4,	// radius,
				new Vector3D(1, 0, 0),	// phi0Direction,
				SurfaceColour.RED_SHINY,
//				new IdealThinLensSurfaceSurfaceCoordinates(
//						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
//						LensF,	// focalLength,
//						1,	// transmissionCoefficient
//						false	// shadow-throwing
//					),	// surface property
				scene,	// parent, 
				studio	// the studio
			));
		}
			
		
		if (EquivLens)
		{
			z4 = (BaseF*z2)/(BaseF - z2);
			CalcF = (BaseF*BaseF*LensF)/((BaseF-z2)*(BaseF-z2));
						
			
			
			scene.addSceneObject(new ParametrisedDisc(
					"equivalent lens",	//description,
					new Vector3D(0, 0, z4),	// centre,
					new Vector3D(0, 0, 1),	// normal,
					0.4,	// radius,
					new Vector3D(1, 0, 0),	// phi0Direction,
					SurfaceColour.CYAN_MATT,
//					new IdealThinLensSurfaceSurfaceCoordinates(
//							new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
//							CalcF,	// focalLength,
//							1,	// transmissionCoefficient
//							false	// shadow-throwing
//						),	// surface property
					scene,	// parent, 
					studio	// the studio
				));
		}
		
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",	// description
				new Vector3D(0, 0, 20),	// centre, was (0, 0, 13)
				1,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,	// parent
				studio
			));
			
			int
			quality = 1,	// 1 = normal, 2 = good, 4 = great
			pixelsX = 640*quality,
			pixelsY = 480*quality,
			antiAliasingFactor = 1;
			

		// a camera with a non-zero aperture size (so it simulates blur)
			
		double cameraDistance = 40;
		
			ApertureCamera camera = new ApertureCamera(
				"Camera",
				new Vector3D(-cameraDistance * Math.sin(cameraPhi), 0, -cameraDistance * Math.cos(cameraPhi)),	// centre of aperture
				new Vector3D(Math.sin(cameraPhi), 0, Math.cos(cameraPhi)),	// view direction (magnitude is distance to detector centre)
				new Vector3D(-(double)pixelsX/pixelsY, 0, 0),	// horizontal basis Vector3D
				new Vector3D(0, -1, 0),	// vertical basis Vector3D
				pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
				ExposureCompensationType.EC0,
				100,	// maxTraceLevel
				20,	// focusing distance
				0,	// aperture radius
				3	// rays per pixel; the more, the less noisy the photo is
		);
	
		studio.setScene(scene);
		studio.setLights(LightSource.getStandardLightsFromBehind());
		studio.setCamera(camera);
	}


	
	
	private LabelledDoublePanel baseFPanel, lensFPanel, cameraPhiPanel;
	private JCheckBox showEquivLensCheck,showCloak;	
	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Panel", panel);

		
		baseFPanel = new LabelledDoublePanel("Focal length of Base Lens");
		baseFPanel.setNumber(BaseF);
		panel.add(baseFPanel, "span");
		
		lensFPanel = new LabelledDoublePanel("Focal length of interior Lens");
		lensFPanel.setNumber(LensF);
		panel.add(lensFPanel, "span");
				
		showEquivLensCheck = new JCheckBox("Shows the equivalent single ideal Lens");
		panel.add(showEquivLensCheck, "span");
		
		showCloak = new JCheckBox("Shows the Cloak With a lens inside");
		panel.add(showCloak, "span");
		
		cameraPhiPanel = new LabelledDoublePanel("Camera phi");
		cameraPhiPanel.setNumber(cameraPhi);
		panel.add(cameraPhiPanel, "span");
	}
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
			
		BaseF = baseFPanel.getNumber();
		LensF = lensFPanel.getNumber();
		EquivLens = showEquivLensCheck.isSelected();
		Cloak = showCloak.isSelected();
		cameraPhi = MyMath.deg2rad(cameraPhiPanel.getNumber());
	}
		
	

	public static void main(final String[] args)
	{
		(new LensInsideCloak()).run();
	}
}







