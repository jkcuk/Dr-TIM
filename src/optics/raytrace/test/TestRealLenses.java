package optics.raytrace.test;

import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;
import optics.raytrace.surfaces.RefractiveLensletArray;
import optics.raytrace.surfaces.RefractiveLensletArray2;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableRectangularLensletArray;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Testing the spherical lens quality.
 *  
 * @author Maik Locher
 */
public class TestRealLenses extends NonInteractiveTIMEngine implements ActionListener
{

	BasicConvexConvergingLensRound RealLensConvex;
	BasicConvexConvergingLensBox RealBoxLensConvex;
	BasicPlanoDivergingLensBox RealPlanoBoxConcave;
	BasicConcaveDivergingLensRound RealLensConcave;
	BasicConcaveDivergingLensBox RealBoxLensConcave;
	BasicPlanoConvergingLensRound PlanoConvexLens;
	BasicPlanoConvergingLensBox PlanoConvexBoxLens;
	BasicPlanoDivergingLensRound PlanoConcaveLens;
	RefractiveLensletArray RefractiveLensletArray;
	EditableRectangularLensletArray LensletArray;
	RefractiveLensletArray2 RefractiveLensletArray2;
	RefractiveBoxLens lens;
	BasicMeniscusLens meniscusLens;
	private double focalLength, radius, lensTrans, refractiveIndex, thickness;
	private double width, height;
	
	private Vector3D centre, nhat, phi0;
	
	private boolean showEquiv;
	
	//camera stuff
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private double cameraFOV; //set the focus and thus zoom of camera	
	private double cameraDistance; //set camera distance from (0,0,0)
	
	private Vector3D lensWindowCentre, principalPointCentre, uPeriodWindow,	vPeriodWindow, uPeriodPrincipalPoint, vPeriodPrincipalPoint;
	
	
	
	
	
	
	public TestRealLenses()
	{
		super();
		
		//lens params
		
		focalLength = -100;
		radius = 0.5;
		height = 0.5;
		width = 0.5;
		thickness = 0.5;
		lensTrans = 1;
		refractiveIndex = 1.5;

		
		centre = new Vector3D(0,2,0); //centre of lens
		
		nhat = new Vector3D(0,0,1); //optical axis direction vector 
		phi0 = new Vector3D(1,0,0); // phi0Direction
		
		lensWindowCentre= new Vector3D(0,2,0);
		principalPointCentre= new Vector3D(0,2,0);
		uPeriodWindow= new Vector3D(0.1,0,0);
		vPeriodWindow= new Vector3D(0,0.1,0);
		uPeriodPrincipalPoint= uPeriodWindow;//new Vector3D(0,0,0);
		vPeriodPrincipalPoint= vPeriodWindow;//new Vector3D(0,0,0);
		
		
		//boolean param to switch between ideal and real lens
		showEquiv = false;
		
		
		
		// camera params
		cameraAngle = 0;
		cameraUpAngle = 0;
		cameraFOV = 20;
		cameraDistance = 10;
		
		
		
		
		
	
		
		
		
		
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
		
		RefractiveLensletArray = new RefractiveLensletArray(
				//set all the array params
				"Refractive lenslet array to test",
				width,
				height,
				thickness,
				centre,// lensletArrayCentre

				uPeriodWindow, // uPeriod
				vPeriodWindow, // vPeriod
				uPeriodPrincipalPoint,
				vPeriodPrincipalPoint,
				lensWindowCentre, // lensWindowCentre
				principalPointCentre,
				focalLength,
				refractiveIndex,
				50, 
				lensTrans, //transmissionCoefficient, 
				false,// shadowThrowing
				scene, 
				studio
				);
		scene.addSceneObject(RefractiveLensletArray);

		
		
//		RefractiveLensletArray2 = new RefractiveLensletArray2(
//				//set all the array params
//				"Refractive lenslet array to test",
//				height,
//				width,
//				thickness,
//				centre,// lensletArrayCentre
//				//set all the lenslet params
//				focalLength,
//				refractiveIndex,
//				lensWindowCentre, // lensWindowCentre
//				principalPointCentre, // principalPointCentre
//				uPeriodWindow, // uPeriodWindow
//				vPeriodWindow, // vPeriodWindow
//				uPeriodPrincipalPoint, // uPeriodPrincipalPoint
//				vPeriodPrincipalPoint, // vPeriodPrincipalPoint
//				500, //max trace level
//				lensTrans, //transmission coef. 
//				false,// shadowThrowing
//				scene, 
//				studio
//				);
//		scene.addSceneObject(RefractiveLensletArray2);
				
	

//		if (focalLength > 0) {
//			RealLensConvex = new BasicConvexConvergingLensRound(
//					"convex lens",	//description
//					radius,
//					focalLength,
//					refractiveIndex,
//					centre,
//					new Vector3D (0,0,1),
//					scene,	// parent, 
//					studio	// the studio
//				);
//			scene.addSceneObject(RealLensConvex);
			
//			RealBoxLensConvex = new BasicConvexConvergingLensBox(
//					"convex square lens",	//description
//					height,
//					width,
//					focalLength,
//					refractiveIndex,
//					centre,
//					new Vector3D (0,0,1),
//					scene,	// parent, 
//					studio	// the studio
//					);
//			scene.addSceneObject(RealBoxLensConvex);
					
					
//			PlanoConvexLens = new BasicPlanoConvergingLensRound(
//					"plano Convex lens",
//					radius,
//					focalLength,
//					refractiveIndex,
//					centre,
//					new Vector3D (0,0,-1),
//					scene,	// parent, 
//					studio	// the studio
//					);
//			scene.addSceneObject(PlanoConvexLens);
			
//			PlanoConvexBoxLens = new BasicPlanoConvergingLensBox(
//					"plano square lens",	//description
//					height,
//					width,
//					focalLength,
//					refractiveIndex,
//					centre,
//					new Vector3D (0,0,-1),
//					scene,	// parent, 
//					studio	// the studio
//					);
//			scene.addSceneObject(PlanoConvexBoxLens);
			
			
			//note for the meniscus lens the radii that can be set are restricted and need to be programmed in to output when a curvature is not allowed. 
//			meniscusLens = new BasicMeniscusLens(
//					"converging meniscus lens",	//description
//					height,
//					width,
//					focalLength,
//					refractiveIndex,
//					centre,
//					10,
//					new Vector3D (0,0,1),
//					scene,	// parent, 
//					studio	// the studio
//					);
//					
//			scene.addSceneObject(meniscusLens);
			
//			RealBoxLensConvex = new BasicConvexConvergingLensBox(
//					"convex square lens",	//description
//					height*0.8,
//					width*0.8,
//					focalLength,
//					refractiveIndex,
//					centre,
//					centre.getSumWith(new Vector3D(0.1,0.2,0)),
//					new Vector3D (0,0,1),
//					scene,	// parent, 
//					studio	// the studio
//					);
//			scene.addSceneObject(RealBoxLensConvex);
//			
			
			
					
//		}
//			else {
//				RealLensConcave = new BasicConcaveDivergingLensRound(
//						"concave lens",
//						radius,
//						focalLength,
//						refractiveIndex,
//						thickness,
//						centre,
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//				scene.addSceneObject(RealLensConcave);
				
//				RealBoxLensConcave = new BasicConcaveDivergingLensBox(
//						"Box concave lens",
//						height,
//						width,
//						focalLength,
//						refractiveIndex,
//						thickness,
//						9,
//						centre,
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//				scene.addSceneObject(RealBoxLensConcave);
				
//				RealPlanoBoxConcave = new BasicPlanoDivergingLensBox("Box concave plano lens",
//						height,
//						width,
//						focalLength,
//						refractiveIndex,
//						thickness,
//						centre,
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//				scene.addSceneObject(RealPlanoBoxConcave);
				
//				PlanoConcaveLens = new BasicPlanoDivergingLensRound("Round concave plano lens",
//						radius,
//						focalLength,
//						refractiveIndex,
//						thickness,
//						centre,
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//				
//				scene.addSceneObject(PlanoConcaveLens);
				
//				meniscusLens = new BasicMeniscusLens(
//						"diverging meniscus lens",	//description
//						height,
//						width,
//						focalLength,
//						refractiveIndex,
//						thickness,
//						10,
//						centre,
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//						
//				scene.addSceneObject(meniscusLens);
				
//				RealBoxLensConcave = new BasicConcaveDivergingLensBox(
//						"Box concave lens",
//						height*0.8,
//						width*0.8,
//						focalLength,
//						refractiveIndex,
//						centre,
//						centre.getSumWith(new Vector3D(0.3,0.4,0)),
//						new Vector3D (0,0,1),
//						scene,	// parent, 
//						studio	// the studio
//						);
//				scene.addSceneObject(RealBoxLensConcave);
//		}
//		lens = new RefractiveBoxLens(
//				"Box concave lens",
//				height,//*0.7,
//				width,//*0.7,
//				focalLength,
//				refractiveIndex,
//				thickness,
//				-8,
//				centre,
////				centre.getSumWith(new Vector3D(0.3,0.4,0)),
//				new Vector3D (0,0,1),
//				scene,	// parent, 
//				studio	// the studio
//				);
//		scene.addSceneObject(lens);



	
	if(showEquiv) //if true, removes all previous components and replaces them with equivalent lenses
	{
			scene.removeSceneObject(RefractiveLensletArray);
				
			scene.addSceneObject(new EditableRectangularLensletArray(
					"Equivilant ideal lens array",	// description
					centre,	// centre
					(uPeriodWindow.getNormalised()).getWithLength(width),	// spanVector1
					(vPeriodWindow.getNormalised()).getWithLength(height),	// spanVector2
					focalLength,	// focalLength
					uPeriodWindow.getLength(),	// xPeriod
					vPeriodWindow.getLength(),	// yPeriod
					0,	// xOffset
					0,	// yOffset
					LensType.IDEAL_THIN_LENS,	// lensType
					false,
					600*1e-9,
					0.96,	// throughputCoefficient
					false,	// reflective
					false,	// shadowThrowing
					scene,	// parent
					studio
				), 
				true
			);
	}
//	{
//		if(focalLength > 0) {
////			scene.removeSceneObject(RealLensConvex);
////			scene.removeSceneObject(PlanoConvexLens);
////			scene.removeSceneObject(RealBoxLensConvex);
////			scene.removeSceneObject(PlanoConvexBoxLens);
////			scene.removeSceneObject(meniscusLens);
//			
//		}
//		else {
////			scene.removeSceneObject(RealLensConcave);
////			scene.removeSceneObject(RealBoxLensConcave);
////			scene.removeSceneObject(RealPlanoBoxConcave);
////			scene.removeSceneObject(PlanoConcaveLens);
////			scene.removeSceneObject(meniscusLens);
//		}
//		scene.removeSceneObject(lens);
//		
//		
//		scene.addSceneObject(new ParametrisedDisc(
//				"equivilant ideal thin lens ",	//description,
//				centre,	// centre,
//				nhat,	// normal,
//				radius, // radius,
//				phi0,	// phi0Direction,
//				new IdealThinLensSurfaceSurfaceCoordinates(
//						new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
//						focalLength,	// focalLength,
//						lensTrans,	// transmissionCoefficient
//						false	// shadow-throwing
//					),	// surface property
//				scene,	// parent, 
//				studio	// the studio
//			));
//	}
	
	
	
	
	
	//adding tims head to view through lens.
	scene.addSceneObject(new EditableTimHead(
			"Tim's head",	// description
			new Vector3D(0, 2, 7),//.getSumWith(new Vector3D(0.4,0.5,0)),
			0.2,	// radius
			new Vector3D(0, 0, -1),	// front direction
			new Vector3D(0, 1, 0),	// top direction
			new Vector3D(1, 0, 0),	// right direction
			scene,	// parent
			studio
		));
	
	
	
	/**
	 * setting up a camera
	 */
	
	int
	quality = 2,	// 1 = normal, 2 = good, 4 = great
	pixelsX = 640*quality,
	pixelsY = 480*quality;



	Vector3D cameraDirection = new Vector3D(-Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), -Math.sin(Math.toRadians(cameraUpAngle)), Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));
	Vector3D cameraApertureCentre	= new Vector3D(cameraDistance*Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), 2+cameraDistance*Math.sin(Math.toRadians(cameraUpAngle)), -cameraDistance*Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));
	
	EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
			"camera",
			cameraApertureCentre,	// centre of aperture
			cameraDirection,	// viewDirection
			new Vector3D(0, 1, 0),	// top direction vector
			cameraFOV,	// horiontalViewAngle in degrees; 2*MyMath.rad2deg(Math.atan(2./10.)) gives same view angle as in previous version
			new Vector3D(0, 0, 0),	// beta
			pixelsX, pixelsY,	// logical number of pixels
			ExposureCompensationType.EC0,	// exposure compensation +0
			50,	// maxTraceLevel
			new Plane(
					"focus plane",	// description
					Vector3D.sum(cameraApertureCentre, cameraDirection.getWithLength(cameraFocussingDistance)),	// pointOnPlane
					cameraDirection,	// normal
					null,	// surfaceProperty
					null,	// parent
					null	// studio
				),	// focus scene
			null,	// cameraFrameScene,
			ApertureSizeType.PINHOLE,	// aperture size
			renderQuality.getBlurQuality(),	// blur quality
			renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			);
	studio.setCamera(camera);
	
	studio.setScene(scene);	
	studio.setLights(LightSource.getStandardLightsFromBehind());
	}
	
	private LabelledDoublePanel focalLengthPanel, radiusPanel,cameraAnglePanel, lensTransPanel, cameraZoomPanel, refractiveIndexPanel,
	cameraUpAnglePanel,cameraDistancePanel;
	private JCheckBox  showEquivCheck;	
	private LabelledVector3DPanel centrePanel;
	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		JPanel lenspanel = new JPanel();
		lenspanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lenses", lenspanel);
		
		focalLengthPanel = new LabelledDoublePanel("Focal Length of lens(es)");
		focalLengthPanel.setNumber(focalLength);
		lenspanel.add(focalLengthPanel, "span");
		
		radiusPanel = new LabelledDoublePanel("radius of lens(es)");
		radiusPanel.setNumber(radius);
		lenspanel.add(radiusPanel, "span");
		
		lensTransPanel = new LabelledDoublePanel("Transmission of lens(es)");
		lensTransPanel.setNumber(lensTrans);
		lenspanel.add(lensTransPanel, "span");
		
		refractiveIndexPanel = new LabelledDoublePanel("refractive index of real lens(glass ~1.5)");
		refractiveIndexPanel.setNumber(refractiveIndex);
		lenspanel.add(refractiveIndexPanel, "span");
		
		centrePanel = new LabelledVector3DPanel("Position of lens(es)");
		centrePanel.setVector3D(centre);
		lenspanel.add(centrePanel, "span");
		
		showEquivCheck = new JCheckBox("Show equal ideal lens");
		showEquivCheck.setSelected(showEquiv);
		lenspanel.add(showEquivCheck, "span");
		
		
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("camera", panel);
		
		cameraAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking at origin");
		cameraAnglePanel.setNumber(cameraAngle);
		panel.add(cameraAnglePanel, "span");
		
		cameraUpAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking down at the origin");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		panel.add(cameraUpAnglePanel, "span");
		
		cameraZoomPanel = new LabelledDoublePanel("FOV of camera. Default = 80, decrease to 'zoom'");
		cameraZoomPanel.setNumber(cameraFOV);
		panel.add(cameraZoomPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		panel.add(cameraDistancePanel, "span");
		
	}
	protected void acceptValuesInInteractiveControlPanel()
	{
		
		//lenses
		focalLength = focalLengthPanel.getNumber();
		radius = radiusPanel.getNumber();
		lensTrans = lensTransPanel.getNumber();
		refractiveIndex = refractiveIndexPanel.getNumber();
		centre = centrePanel.getVector3D();
		showEquiv = showEquivCheck.isSelected();
		
		
		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraFOV = cameraZoomPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber();
	}
	
	public static void main(final String[] args)
	{
		(new TestRealLenses()).run();
	}
}

