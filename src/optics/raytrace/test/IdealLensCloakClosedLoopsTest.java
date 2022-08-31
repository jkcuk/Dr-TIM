package optics.raytrace.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import math.*;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;


/**
 * Testing the single ray trace properties of an ideal lens cloak to investigate closed loop trajectories. 
 *  
 * @author Maik
 */
public class IdealLensCloakClosedLoopsTest extends NonInteractiveTIMEngine implements ActionListener
{	

	//cloak stuff
	EditableLensSimplicialComplex cloak, cloakFrame;
	private double baseFocal; //base lens focal length 
	private double h1P; // Height to lower inner vertex in physical space
	private double h2P; //Height to upper inner vertex in physical space
	private double h; //over all height of cloak
	private double r; //base radius
	private LensType lensType;
	private double lensTrans; 
	private boolean CloakFrame; 
	private double cloakRotationAngle;

	//image stuff
	private boolean baseLensImageCell2;
	private boolean baseLensImageCell3;
	private boolean plane01ImageCell2;
	private boolean hide01Colour;
	
	public enum CellsNumber
	{
		ONE("1"),
		TWO("2"),
		THREE("3");

		private String description;
		private CellsNumber(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
		
	}
	private CellsNumber cellNumber;



	//ray trace stuff
	private boolean showTrajectory;
	private Vector3D trajectoryDefaultDirection; //defines the default ray direction
	private Vector3D rayAim; // defines the direction when a position of where to look is inputed
	private boolean manualRayDirection;//defines which direction should be set for the ray tracing 
	private double rayAngle;// sets the angle of the ray location, in equal to camera sets it to camera position
	private double rayUpAngle;
	private Vector3D rayPos; //ray trace starting position 
	private Vector3D rayDirection; // raytrace direction
	private boolean images; //shows a second ray to see where the images form


	//camera stuff
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private double cameraFOV; //set the focus and thus zoom of camera	
	private double cameraDistance; //set camera distance from (0,0,0) 
	private int maxSteps;

	//movie stuff
	private double startAngleCloak, stopAngleCloak, startAngleRay, stopAngleRay, startAngleCamera, stopAngleCamera;
	private double startUpAngleRay, stopUpAngleRay, startUpAngleCamera, stopUpAngleCamera;
	public enum MovieType
	{
		MOVING_RAY("Ray moves around the cloak"),
		ROTATING_CLOAK("Cloak roatates anticlockwise"),
		CAMERA_MOVING("Camera Moves");

		private String description;
		private MovieType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}

	}

	private MovieType movieType;



	public IdealLensCloakClosedLoopsTest()
	{
		super();


		//cloak stuff
		CloakFrame = false; //shows the frame of the cloak
		baseFocal = 0.1;
		lensType = LensType.IDEAL_THIN_LENS; //set to ideal thin lens by default
		lensTrans = 1; //sets transmission coef of lens to 1 (default)
		h = 3;//over all height of cloak
		h1P = 1/3.; // Height to lower inner vertex in physical space
		h2P = 2/3.; //Height to upper inner vertex in physical space
		r = h/2; //base radius
		cloakRotationAngle = 0;

		//image stuff
		baseLensImageCell2 = false;
		baseLensImageCell3 = false;
		plane01ImageCell2 = false;
		hide01Colour = false;
		cellNumber = CellsNumber.ONE;
		
		//ray trace stuff
		traceRaysWithTrajectory = false; 
		manualRayDirection = false;
		showTrajectory = false;
		trajectoryDefaultDirection = new Vector3D(0,0,1);//sets the manual direction of the ray
		rayAim = new Vector3D(0, 0, 0);//sets the position the ray should go towards
		rayAngle = 0; //setting the ray angle to the camera angle. 
		rayUpAngle = 0;

		// camera params
		cameraAngle = 0;
		cameraUpAngle = 0;
		cameraFOV = 30;
		cameraDistance = 10;
		maxSteps = 50;

		//movie stuff
		movie = false;
		numberOfFrames = 10;
		movieType = MovieType.ROTATING_CLOAK;
		startAngleCloak = 0;
		stopAngleCloak = 0;

		startAngleRay = 0;
		stopAngleRay = 0;
		startUpAngleRay = 0;
		stopUpAngleRay = 0;

		startAngleCamera = 0;
		stopAngleCamera = 0;
		startUpAngleCamera = 0;
		stopUpAngleCamera = 0;

		//Tim engine setup
		renderQuality = RenderQualityEnum.DRAFT;//Set the default render quality		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;// set to true for interactive version
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Testing an ideal lens cloak with single ray tracing";
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
		scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio)); //the floor
		//scene.addSceneObject(SceneObjectClass.getChequerboardFloor(scene, studio)); //the floor
		//scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));	// the sky	


		Vector3D baseVertex = new Vector3D(r*Math.cos(Math.toRadians(cloakRotationAngle-90)), 2-h/2, r*Math.sin(Math.toRadians(cloakRotationAngle-90)));
		
		if(movie) {

			switch(movieType)
			{
			case ROTATING_CLOAK:
				double partialRotationAngle = startAngleCloak+(stopAngleCloak-startAngleCloak)*frame/numberOfFrames;
				baseVertex = new Vector3D(r*Math.cos(Math.toRadians(partialRotationAngle)), 2-h/2, r*Math.sin(Math.toRadians(partialRotationAngle)));
				break;
			case MOVING_RAY:
				rayAngle = startAngleRay+(stopAngleRay-startAngleRay)*frame/numberOfFrames;
				rayUpAngle = startUpAngleRay+(stopUpAngleRay-startUpAngleRay)*frame/numberOfFrames;
				break;
			case CAMERA_MOVING:
				cameraAngle = startAngleCamera+(stopAngleCamera-startAngleCamera)*frame/numberOfFrames;
				cameraUpAngle = startUpAngleCamera+(stopUpAngleCamera-startUpAngleCamera)*frame/numberOfFrames;
				break;

			}
		}

		//adding the lens cloak

		double frameRadius = 0.005; // radius of cloak frame


		cloak = new EditableLensSimplicialComplex(
				"outer Abyss cloak",	// description
				scene,	// parent
				studio
				);
		// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
		cloak.setLensTypeRepresentingFace(lensType);
		cloak.setShowStructureP(CloakFrame);
		cloak.setVertexRadiusP(frameRadius);
		cloak.setShowStructureV(false);
		cloak.setVertexRadiusV(frameRadius);
		cloak.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));

		cloak.initialiseToOmnidirectionalLens(
				h1P,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2P,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-h/baseFocal + 1/h1P),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, 2+h/2, 0),	// topVertex
				new Vector3D(0, 2-h/2, 0),	// baseCentre
				baseVertex	// baseVertex
				);
		scene.addSceneObject(cloak);




		//adding tims head to view through lens.
		EditableTimHead timHead = new EditableTimHead(
				"Tim's head",	// description
				new Vector3D(0, 2, 11),//.getSumWith(new Vector3D(0.4,0.5,0)),
				0.2,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,	// parent
				studio
				);
		scene.addSceneObject(timHead);

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
				maxSteps,	// maxTraceLevel
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
		//System.out.println("centre = "+centre);
		studio.setCamera(camera);

		studio.setScene(scene);

		studio.setLights(LightSource.getStandardLightsFromBehind());

		/*
		 * image stuff
		 */
		boolean anyImages;
		if(baseLensImageCell2 ||baseLensImageCell3 || plane01ImageCell2) {
			anyImages = true;
		}else anyImages = false; 
		
		if(anyImages) {
			ArrayList<Vector3D> cloakVertices = new ArrayList<Vector3D>(9);
			for(Vector3D vertex:cloak.getVertices()) cloakVertices.add(vertex.clone());
			ArrayList<Edge> cloakEdges = new ArrayList<Edge>(10);
			for(Edge edge:cloak.getEdges()) cloakEdges.add(edge.clone());
			try {
				SimplicialComplex innerCloakImage = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(
						cloakVertices,	// vertices
						cloakEdges	// edges

						);

				//get the lenses that do the imaging
				
				int upperI = 0;
				switch(cellNumber) {
				case ONE:
					upperI = 1;
				break;
				case TWO:
					upperI = 2;
				break;
				case THREE:
					upperI = 3;
				break;
					
				}

				for(int i = 0; i < upperI; i++) {//3

					SurfaceColour colour = SurfaceColour.getRandom();
					SurfaceColour outsideColour = SurfaceColour.getRandom();
					//add another colour in to make some things clear to see
					SurfaceColour anotherOutsideColour = SurfaceColour.getRandom();
					if(upperI != 1) anotherOutsideColour = outsideColour;

					//the some positions in the base lens to see where it is imaged to.
					ArrayList<Vector3D> insidePositionsToImage = new ArrayList<Vector3D>(7);
					//the plane going to infinity, selecting to use points which should span the whole outside plane.
					ArrayList<Vector3D> outsidePositionsToImage = new ArrayList<Vector3D>(8);
					insidePositionsToImage.clear();
					outsidePositionsToImage.clear();
					//inside
					insidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(i));
					insidePositionsToImage.add(new Vector3D(0, 2-h/2, 0));	
					//outside
					outsidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(i));
					Vector3D baseCentre = new Vector3D(0, 2-h/2, 0);

					int k = 0;
					if (i <2) k = i +1;
					//inside
					insidePositionsToImage.add(Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.5))));
					Vector3D midPositions = Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.5)));	
					insidePositionsToImage.add(Vector3D.sum(midPositions,Vector3D.difference(baseCentre,midPositions).getProductWith(0.5)));	
					insidePositionsToImage.add(Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.25))));
					insidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(k));
					
					//outside
					outsidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(k));
					Vector3D toNearInfinity = Vector3D.sum(baseCentre, Vector3D.difference(innerCloakImage.getFace(0).getVertex(k), baseCentre).getWithLength(MyMath.HUGE));
					Vector3D toNegtaiveNearInfinity = Vector3D.difference(baseCentre, Vector3D.difference(innerCloakImage.getFace(0).getVertex(k), baseCentre).getWithLength(MyMath.HUGE));
					outsidePositionsToImage.add(midPositions);
					outsidePositionsToImage.add(toNearInfinity);
					outsidePositionsToImage.add(toNegtaiveNearInfinity);
					
					
					k = 2;
					if(i>0) k = i-1;
					//inside
					insidePositionsToImage.add(Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.5))));
					Vector3D midPositionsNegative = Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.5)));	
					insidePositionsToImage.add(Vector3D.sum(midPositionsNegative,Vector3D.difference(baseCentre,midPositionsNegative).getProductWith(0.5)));	
					insidePositionsToImage.add(Vector3D.sum(innerCloakImage.getFace(0).getVertex(i),(Vector3D.difference(innerCloakImage.getFace(0).getVertex(k),innerCloakImage.getFace(0).getVertex(i)).getProductWith(0.25))));
					insidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(k));
					
					//outside
					outsidePositionsToImage.add(innerCloakImage.getFace(0).getVertex(k));
					Vector3D toOtherNearInfinity = Vector3D.sum(baseCentre, Vector3D.difference(innerCloakImage.getFace(0).getVertex(k), baseCentre).getWithLength(MyMath.HUGE));
					Vector3D toOtherNegtaiveNearInfinity = Vector3D.difference(baseCentre, Vector3D.difference(innerCloakImage.getFace(0).getVertex(k), baseCentre).getWithLength(MyMath.HUGE));
					outsidePositionsToImage.add(midPositionsNegative);
					outsidePositionsToImage.add(toOtherNearInfinity);
					outsidePositionsToImage.add(toOtherNegtaiveNearInfinity);
					
					ArrayList<Vector3D> insideIimages = new ArrayList<Vector3D>(insidePositionsToImage.size());
					ArrayList<Vector3D> outsideIimages = new ArrayList<Vector3D>(outsidePositionsToImage.size());
					
					if(plane01ImageCell2) {
						
						//finding the position of the images inside cell 2.
						int j = 4;
						if(i == 0) j = 9;
						if(i == 2) j = 1;


						String lensFace = "Face #"+j;
						//get the cloak focal length on face 4
						EditableParametrisedTriangle cLens = (EditableParametrisedTriangle)(cloak.getSceneObjectContainer().getFirstSceneObjectWithDescription(lensFace, true));
						//SurfaceProperty s = cLens.getSurfaceProperty();
						IdealThinLensSurface cLensFocal = (IdealThinLensSurface)(cLens.getSurfaceProperty());
						insideIimages.clear();
						for (int l = 0; l<outsidePositionsToImage.size(); l++) {
							//addSphere(outsidePositionsToImage.get(l), frameRadius, scene);
							Vector3D outsideImagePosition = cLensFocal.getImagePosition(outsidePositionsToImage.get(l), ImagingDirection.POS2NEG);
							//addSphere(imagePosition, frameRadius, scene);
							outsideIimages.add(outsideImagePosition);
						}
						//and time to add some triangles
						if(!hide01Colour) {
						//part of plane on opposite side of where the lens is
						addTriangle(outsidePositionsToImage.get(0), outsidePositionsToImage.get(4), outsidePositionsToImage.get(8), outsideColour, scene);
						addTriangle(outsidePositionsToImage.get(0), outsidePositionsToImage.get(2), outsidePositionsToImage.get(8), outsideColour, scene);
						addTriangle(outsidePositionsToImage.get(0), outsidePositionsToImage.get(4), outsidePositionsToImage.get(6), outsideColour, scene);
						
						//part of plane on same side as lens
						addTriangle(outsidePositionsToImage.get(1), outsidePositionsToImage.get(5), outsidePositionsToImage.get(7), anotherOutsideColour, scene);
						addTriangle(outsidePositionsToImage.get(1), outsidePositionsToImage.get(3), outsidePositionsToImage.get(7), anotherOutsideColour, scene);
						}
						
						//and now the images of all these...
						addTriangle(outsideIimages.get(0), outsideIimages.get(4), outsideIimages.get(8), outsideColour, scene);
						addTriangle(outsideIimages.get(0), outsideIimages.get(2), outsideIimages.get(8), outsideColour, scene);
						addTriangle(outsideIimages.get(0), outsideIimages.get(4), outsideIimages.get(6), outsideColour, scene);
						addTriangle(outsideIimages.get(1), outsideIimages.get(5), outsideIimages.get(7), anotherOutsideColour, scene);
						addTriangle(outsideIimages.get(1), outsideIimages.get(3), outsideIimages.get(7), anotherOutsideColour, scene);
						
						//adding the black cylinders to indicated where the 'infinity' line is
						addCylinder(outsideIimages.get(3), outsideIimages.get(7), 0.5*frameRadius, scene);
					}
					

					if(baseLensImageCell2) {
						int j = 4;
						if(i == 0) j = 9;
						if(i == 2) j = 1;


						String lensFace = "Face #"+j;
						//get the cloak focal length on face 4
						EditableParametrisedTriangle cLens = (EditableParametrisedTriangle)(cloak.getSceneObjectContainer().getFirstSceneObjectWithDescription(lensFace, true));
						//SurfaceProperty s = cLens.getSurfaceProperty();
						IdealThinLensSurface cLensFocal = (IdealThinLensSurface)(cLens.getSurfaceProperty());
						insideIimages.clear();
						for (int l = 0; l<insidePositionsToImage.size(); l++) {
							//addSphere(positionsToImage.get(l), frameRadius, scene);
							Vector3D imagePosition = cLensFocal.getImagePosition(insidePositionsToImage.get(l), ImagingDirection.POS2NEG);
							//addSphere(imagePosition, frameRadius, scene);
							insideIimages.add(imagePosition);
						}

						if(!hide01Colour) {
						addTriangle(insidePositionsToImage.get(0), insidePositionsToImage.get(1), insidePositionsToImage.get(2), colour, scene); 
						addTriangle(insidePositionsToImage.get(0), insidePositionsToImage.get(1), insidePositionsToImage.get(6), colour, scene); 	
						}
						addTriangle(insideIimages.get(0), insideIimages.get(1), insideIimages.get(2), colour, scene); 
						addTriangle(insideIimages.get(0), insideIimages.get(1), insideIimages.get(6), colour, scene);
						
						//Test to see where other parts of base lens are imaged to. This does not follow what we see for infinity below.
//						Vector3D middleTest = Vector3D.sum(innerCloakImage.getFace(0).getVertex(1),innerCloakImage.getFace(0).getVertex(2)).getProductWith(0.5);
//						addSphere(innerCloakImage.getFace(0).getVertex(1), 0.5*frameRadius, scene);
//						addSphere(middleTest, 0.5*frameRadius, scene);
//						addSphere(innerCloakImage.getFace(0).getVertex(2), 0.5*frameRadius, scene);
//						addSphere(cLensFocal.getImagePosition(innerCloakImage.getFace(0).getVertex(1), ImagingDirection.POS2NEG), 0.5*frameRadius, scene);
//						addSphere(cLensFocal.getImagePosition(middleTest, ImagingDirection.POS2NEG), 0.5*frameRadius, scene);
//						addSphere(cLensFocal.getImagePosition(innerCloakImage.getFace(0).getVertex(2), ImagingDirection.POS2NEG), 0.5*frameRadius, scene);

					}
					
					if(baseLensImageCell3) {
						int j = 6;
						if(i == 0) j = 11;
						if(i == 2) j = 3;


						String lensFace = "Face #"+j;
						//get the cloak focal length on face 4
						EditableParametrisedTriangle cLens = (EditableParametrisedTriangle)(cloak.getSceneObjectContainer().getFirstSceneObjectWithDescription(lensFace, true));
						//SurfaceProperty s = cLens.getSurfaceProperty();
						IdealThinLensSurface cLensFocal = (IdealThinLensSurface)(cLens.getSurfaceProperty());
						insideIimages.clear();
						for (int l = 0; l<insidePositionsToImage.size(); l++) {
							//addSphere(positionsToImage.get(l), frameRadius, scene);
							Vector3D imagePosition = cLensFocal.getImagePosition(insidePositionsToImage.get(l), ImagingDirection.POS2NEG);
							//addSphere(imagePosition, frameRadius, scene);
							insideIimages.add(imagePosition);
						}			

						if(!hide01Colour) {
						addTriangle(insidePositionsToImage.get(1), insidePositionsToImage.get(5), insidePositionsToImage.get(9), colour, scene);  	
						}
						
						addTriangle(insideIimages.get(1), insideIimages.get(5), insideIimages.get(9), colour, scene); 
						insideIimages.clear();

					}
					
				}
			}


			catch (InconsistencyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//studio.setScene(scene);
		}


		/*
		 * setting the ray tracing
		 */
		if(showTrajectory)
		{

			rayPos = new Vector3D (cameraDistance*Math.cos(Math.toRadians(rayUpAngle+90))*Math.sin(Math.toRadians(rayAngle)), 2+cameraDistance*Math.sin(Math.toRadians(rayUpAngle+90)),-cameraDistance*Math.cos(Math.toRadians(rayAngle))*Math.cos(Math.toRadians(rayUpAngle+90))  );//sets the 'automatic' position of the ray ;				
			if(manualRayDirection)
			{System.out.println(rayPos);
			rayDirection = Vector3D.difference(trajectoryDefaultDirection,rayPos);
			}
			else
			{RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
			rayAim = i.p;
			rayDirection = Vector3D.difference(rayAim, rayPos);	

			}	
			Vector3D imageRayDirection;
			imageRayDirection = Vector3D.difference(Vector3D.sum(rayDirection, rayPos).getSumWith(new Vector3D(0.1,0.1,0.1)),rayPos);



			// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							rayPos,	// startPoint
							0,	// startTime
							rayDirection,	// startDirection
							0.005,	// rayRadius
							new SurfaceColourLightSourceIndependent(DoubleColour.RED, false),	// surfaceProperty
							100,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);
			if (images) {
				// do the tracing of rays with trajectory
				scene.addSceneObject(
						new EditableRayTrajectory(
								"light-ray trajectory",	// description
								rayPos,	// startPoint
								0,	// startTime
								imageRayDirection,	// startDirection
								0.005,	// rayRadius
								new SurfaceColourLightSourceIndependent(DoubleColour.GREEN, false),	// surfaceProperty
								100,	// maxTraceLevel
								true,	// reportToConsole
								scene,	// parent
								studio
								)
						);

			}

			studio.setScene(scene);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();
		}	
		
		if(showTrajectory||anyImages) {	
			//remove all the objects and replace with transparent ones
			scene.removeSceneObject(cloak);	
			scene.removeSceneObject(timHead);
			// create the outer cloak; first create a lens-simplicial complex...
			cloakFrame = new EditableLensSimplicialComplex(
					"outer Abyss cloak",	// description
					scene,	// parent
					studio
					);
			// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
			cloakFrame.setLensTypeRepresentingFace(LensType.NONE);
			cloakFrame.setShowStructureP(true);
			cloakFrame.setVertexRadiusP(frameRadius);
			cloakFrame.setShowStructureV(false);
			cloakFrame.setVertexRadiusV(frameRadius);
			cloakFrame.setSurfacePropertyP(new SurfaceColour(DoubleColour.DARK_BLUE, DoubleColour.GREY10, false));

			cloakFrame.initialiseToOmnidirectionalLens(
					h1P,	// physicalSpaceFractionalLowerInnerVertexHeight
					h2P,	// physicalSpaceFractionalUpperInnerVertexHeight
					1./(-h/baseFocal + 1/h1P),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
					new Vector3D(0, 2+h/2, 0),	// topVertex
					new Vector3D(0, 2-h/2, 0),	// baseCentre
					baseVertex	// baseVertex1
					);
			scene.addSceneObject(cloakFrame);
			}
	}

	public void addTriangle(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3, SurfaceColour colour, SceneObjectContainer scene) {

		Vector3D v1Tov2 = Vector3D.difference(vertex2, vertex1);
		Vector3D v1Tov3 = Vector3D.difference(vertex3, vertex1);

		ParametrisedTriangle triangle = new ParametrisedTriangle(
				"triangle",// description,
				vertex1,// vertex1,
				v1Tov2,// vertex1ToVertex2,
				v1Tov3,// vertex1ToVertex3,
				false,// semiInfinite,
				new SemiTransparent(colour, 0.2),// surfaceProperty,
				scene,// parent,
				studio
				);
		scene.addSceneObject(triangle);
	}

	public void addSphere(Vector3D position, double frameRadius, SceneObjectContainer scene) {
		Sphere sphere =  new Sphere(
				"Vertex", // description
				position, // centre
				9*frameRadius,	// radius
				 new SurfaceColour(DoubleColour.BLACK, DoubleColour.BLACK, false),
				scene, studio);
		scene.addSceneObject(sphere);
	}
	
	public void addCylinder( Vector3D startPoint, Vector3D endPoint, double frameRadius, SceneObjectContainer scene) {
		Cylinder cylinder = new Cylinder(
				"line/tube",// description,
				startPoint,// startPoint,
				endPoint,// endPoint,
				frameRadius,// radius,
				new SurfaceColour(DoubleColour.BLACK, DoubleColour.BLACK, false),// surfaceProperty,
				scene,// parent,
				studio// studio
			);
		scene.addSceneObject(cylinder);	
	}


	//general and camera
	private LabelledDoublePanel cameraAnglePanel, lensTransPanel, cameraZoomPanel, cameraUpAnglePanel,cameraDistancePanel, cloakRotationAnglePanel;
	private LabelledIntPanel maxStepsPanel;
	private LabelledDoublePanel baseLensFPanel, rayAnglePanel, rayUpAnglePanel;
	private LabelledDoublePanel startAnglePanelCloak, stopAnglePanelCloak, 
	startAnglePanelRay, startUpAnglePanelRay, stopAnglePanelRay, stopUpAnglePanelRay,
	startAnglePanelCamera, startUpAnglePanelCamera, stopAnglePanelCamera, stopUpAnglePanelCamera;
	private  JCheckBox   CloakFrameCheck, showTrajectoryPanel, imagesPanel,
	manualRayDirectionCheckBox, movieCheckBox;	
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	private JComboBox<LensType>  lensTypeComboBox;
	private LabelledVector3DPanel trajectoryDefaultDirectionPanel;
	private IntPanel numberOfFramesPanel, firstFramePanel, lastFramePanel;
	JTabbedPane movieTabbedPane;
	private JCheckBox baseLensImageCell2CheckBox, baseLensImageCell3CheckBox, plane01ImageCell2CheckBox, hide01ColourCheckBox;
	private JComboBox<CellsNumber>  cellNumberComboBox;



	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");


		//general panel

		JPanel generalpanel = new JPanel();
		generalpanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("General", generalpanel);


		// cloak
		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 1"));
		cloaksPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak"));
		generalpanel.add(cloaksPanel, "wrap");


		CloakFrameCheck = new JCheckBox("Toggles the frames of the outer cloak");
		cloaksPanel.add(CloakFrameCheck, "span");

		baseLensFPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFPanel.setNumber(baseFocal);
		cloaksPanel.add(baseLensFPanel, "span");

		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		cloaksPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeComboBox), "span");

		lensTransPanel = new LabelledDoublePanel("Lens transmission coefficient, set between 0-1, default = 1");
		lensTransPanel.setNumber(lensTrans);
		cloaksPanel.add(lensTransPanel, "span");

		cloakRotationAnglePanel = new LabelledDoublePanel("anti clockwise cloak rotation angle");
		cloakRotationAnglePanel.setNumber(cloakRotationAngle);
		cloaksPanel.add(cloakRotationAnglePanel, "span");



		//raytrace stuff

		JPanel rayPanel = new JPanel();
		rayPanel.setLayout(new MigLayout("insets 0"));
		rayPanel.setBorder(GUIBitsAndBobs.getTitledBorder("ray trace"));
		generalpanel.add(rayPanel, "wrap");

		showTrajectoryPanel = new JCheckBox("Show the trajectory a of  light ray");
		showTrajectoryPanel.setSelected(showTrajectory);
		rayPanel.add(showTrajectoryPanel, "span");

		imagesPanel = new JCheckBox("Second ray to show images");
		imagesPanel.setSelected(images);
		rayPanel.add(imagesPanel, "span");

		//semi automatic parts
		rayAnglePanel = new LabelledDoublePanel("xz angle");
		rayAnglePanel.setNumber(rayAngle);
		rayPanel.add(rayAnglePanel, "span");


		rayUpAnglePanel = new LabelledDoublePanel("xy angle");
		rayUpAnglePanel.setNumber(rayUpAngle);
		rayPanel.add(rayUpAnglePanel, "span");

		rayLastClickTextArea = new JTextArea(2, 40);
		JScrollPane scrollPane = new JScrollPane(rayLastClickTextArea); 
		rayLastClickTextArea.setEditable(false);
		rayLastClickTextArea.setText("Click on the aim ray button set the aim of the light ray");
		rayLastClickInfo = new JButton("Aim ray");
		rayLastClickInfo.addActionListener(this);
		rayPanel.add(GUIBitsAndBobs.makeRow(scrollPane, rayLastClickInfo), "span");


		manualRayDirectionCheckBox = new JCheckBox("use manual ray direction");
		manualRayDirectionCheckBox.setSelected(manualRayDirection);
		rayPanel.add(manualRayDirectionCheckBox, "span");

		trajectoryDefaultDirectionPanel = new LabelledVector3DPanel("Point to be viewed");
		trajectoryDefaultDirectionPanel.setVector3D(trajectoryDefaultDirection);
		rayPanel.add(trajectoryDefaultDirectionPanel, "span");


		//images
		
		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new MigLayout("insets 0"));
		imagePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Images"));
		generalpanel.add(imagePanel, "wrap");

		baseLensImageCell2CheckBox = new JCheckBox("Base lens images in cell 2");
		baseLensImageCell2CheckBox.setSelected(baseLensImageCell2);
		imagePanel.add(baseLensImageCell2CheckBox);
		
		hide01ColourCheckBox = new JCheckBox("No base colours");
		hide01ColourCheckBox.setSelected(hide01Colour);
		imagePanel.add(hide01ColourCheckBox, "span");

		baseLensImageCell3CheckBox = new JCheckBox("Base lens images in cell 3");
		baseLensImageCell3CheckBox.setSelected(baseLensImageCell3);
		imagePanel.add(baseLensImageCell3CheckBox);
		
		cellNumberComboBox = new JComboBox<CellsNumber>(CellsNumber.values());
		cellNumberComboBox.setSelectedItem(cellNumber);
		imagePanel.add(GUIBitsAndBobs.makeRow("Image", cellNumberComboBox, "cell"), "span");
		
		plane01ImageCell2CheckBox = new JCheckBox("Outer plane image in cell 2");
		plane01ImageCell2CheckBox.setSelected(plane01ImageCell2);
		imagePanel.add(plane01ImageCell2CheckBox, "span");
		
		


		//movie stuff
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		//moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie Time"));
		tabbedPane.add(cameraPanel, "Camera");

		cameraAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking at origin");
		cameraAnglePanel.setNumber(cameraAngle);
		cameraPanel.add(cameraAnglePanel, "span");

		cameraUpAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking down at the origin");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		cameraPanel.add(cameraUpAnglePanel, "span");

		cameraZoomPanel = new LabelledDoublePanel("FOV of camera. Default = 80, decrease to 'zoom'");
		cameraZoomPanel.setNumber(cameraFOV);
		cameraPanel.add(cameraZoomPanel, "span");

		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");

		maxStepsPanel = new LabelledIntPanel("Max number of steps before returning black");
		maxStepsPanel.setNumber(maxSteps);
		cameraPanel.add(maxStepsPanel, "span");



		//movie stuff
		JPanel moviePanel = new JPanel();
		moviePanel.setLayout(new MigLayout("insets 0"));
		//moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie Time"));
		tabbedPane.add(moviePanel, "Movie Time");

		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");

		movieTabbedPane = new JTabbedPane();
		moviePanel.add(movieTabbedPane, "span");


		JPanel movingCloakPanle = new JPanel();
		//movingCloakPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Cloak"));
		movingCloakPanle.setLayout(new MigLayout("insets 0"));		

		startAnglePanelCloak = new LabelledDoublePanel("starting angle");
		startAnglePanelCloak.setNumber(startAngleCloak);
		movingCloakPanle.add(startAnglePanelCloak, ""); 

		stopAnglePanelCloak = new LabelledDoublePanel("stopping angle");
		stopAnglePanelCloak.setNumber(stopAngleCloak);
		movingCloakPanle.add(stopAnglePanelCloak, "span");

		movieTabbedPane.add(movingCloakPanle, "Cloak");


		JPanel movingRayPanle = new JPanel();
		//movingRayPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Ray"));
		movingRayPanle.setLayout(new MigLayout("insets 0"));

		startAnglePanelRay = new LabelledDoublePanel("starting angle");
		startAnglePanelRay.setNumber(startAngleRay);
		movingRayPanle.add(startAnglePanelRay, ""); 

		stopAnglePanelRay = new LabelledDoublePanel("stopping angle");
		stopAnglePanelRay.setNumber(stopAngleRay);
		movingRayPanle.add(stopAnglePanelRay, "span");

		startUpAnglePanelRay = new LabelledDoublePanel("starting up angle");
		startUpAnglePanelRay.setNumber(startUpAngleRay);
		movingRayPanle.add(startUpAnglePanelRay, ""); 

		stopUpAnglePanelRay = new LabelledDoublePanel("stopping up angle");
		stopUpAnglePanelRay.setNumber(stopUpAngleRay);
		movingRayPanle.add(stopUpAnglePanelRay, "span");

		movieTabbedPane.add(movingRayPanle,"Ray");



		JPanel movingCameraPanle = new JPanel();
		//movingCameraPanle.setBorder(GUIBitsAndBobs.getTitledBorder("Camera"));
		movingCameraPanle.setLayout(new MigLayout("insets 0"));

		startAnglePanelCamera = new LabelledDoublePanel("starting angle");
		startAnglePanelCamera.setNumber(startAngleCamera);
		movingCameraPanle.add(startAnglePanelCamera, ""); 

		stopAnglePanelCamera = new LabelledDoublePanel("stopping angle");
		stopAnglePanelCamera.setNumber(stopAngleCamera);
		movingCameraPanle.add(stopAnglePanelCamera, "span");

		startUpAnglePanelCamera = new LabelledDoublePanel("starting up angle");
		startUpAnglePanelCamera.setNumber(startUpAngleCamera);
		movingCameraPanle.add(startUpAnglePanelCamera, ""); 

		stopUpAnglePanelCamera = new LabelledDoublePanel("stopping up angle");
		stopUpAnglePanelCamera.setNumber(stopUpAngleCamera);
		movingCameraPanle.add(stopUpAnglePanelCamera, "span");

		movieTabbedPane.add(movingCameraPanle,"Camera");


		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);

		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);

		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");
	}

	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		switch(movieTabbedPane.getSelectedIndex())
		{
		case 0:
			movieType = MovieType.ROTATING_CLOAK;
			break;
		case 1:
			movieType = MovieType.MOVING_RAY;
			break;
		case 2:
			movieType = MovieType.CAMERA_MOVING;
			break;
		}




		//cloak
		lensTrans = lensTransPanel.getNumber();
		CloakFrame = CloakFrameCheck.isSelected();
		baseFocal = baseLensFPanel.getNumber();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		cloakRotationAngle = cloakRotationAnglePanel.getNumber();



		// raytrace
		showTrajectory = showTrajectoryPanel.isSelected();
		rayAngle = rayAnglePanel.getNumber(); //angle
		rayUpAngle = rayUpAnglePanel.getNumber();
		manualRayDirection = manualRayDirectionCheckBox.isSelected(); //checkbox to set to manual direction
		trajectoryDefaultDirection = trajectoryDefaultDirectionPanel.getVector3D();//manual direction of beam
		images = imagesPanel.isSelected();

		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraFOV = cameraZoomPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber();
		maxSteps = maxStepsPanel.getNumber();

		//images
		baseLensImageCell2 = baseLensImageCell2CheckBox.isSelected();
		baseLensImageCell3 = baseLensImageCell3CheckBox.isSelected();
		plane01ImageCell2 = plane01ImageCell2CheckBox.isSelected();
		cellNumber = (CellsNumber)(cellNumberComboBox.getSelectedItem());
		hide01Colour = hide01ColourCheckBox.isSelected();

		//movie stuff
		movie = movieCheckBox.isSelected();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		startAngleCloak = startAnglePanelCloak.getNumber();
		stopAngleCloak = stopAnglePanelCloak.getNumber();
		startAngleRay = startAnglePanelRay.getNumber();
		stopAngleRay = stopAnglePanelRay.getNumber();
		startUpAngleRay = startUpAnglePanelRay.getNumber();
		stopUpAngleRay = stopUpAnglePanelRay.getNumber();
		startAngleCamera = startAnglePanelCamera.getNumber();
		stopAngleCamera = stopAnglePanelCamera.getNumber();
		startUpAngleCamera = startUpAnglePanelCamera.getNumber();
		stopUpAngleCamera = stopUpAnglePanelCamera.getNumber();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);

		if(e.getSource().equals(rayLastClickInfo))
		{
			RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();

			rayLastClickTextArea.setText("Ray aiming at "+i.p)
			;

		}

	}

	public static void main(final String[] args)
	{
		(new IdealLensCloakClosedLoopsTest()).run();
	}
}

