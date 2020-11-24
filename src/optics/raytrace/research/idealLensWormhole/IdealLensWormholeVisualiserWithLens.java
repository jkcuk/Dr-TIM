package optics.raytrace.research.idealLensWormhole;


import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.cameras.ApertureCamera;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
import optics.DoubleColour;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.sceneObjects.ParametrisedTriangle;





public class IdealLensWormholeVisualiserWithLens extends NonInteractiveTIMEngine
{
	/*
	 * Variables for the inside lenses
	 */
	
	//Lens names
	ParametrisedTriangle lens1,lens2,lens3,lens4;
	
	//focal length of lens 1 and 2 (3 and 4 are not needed as they are the same as 1 and 2)
	private double f1;
	private double f2;
	
	//principal points of lenses
	private Vector3D pP1;
	private Vector3D pP2;
	private Vector3D pP3;
	private Vector3D pP4;
	
	private double d;//Separation between the 2nd and 3rd lens
	private Vector3D nhat;//optical axis direction vector 
	private Vector3D phi0;// phi0Direction
	//private double r;// radius of lens
	private Vector3D vert1; // unit vector to vertex 1
	private Vector3D vert2; // unit vector to vertex 2
	private Vector3D vert3; // unit vector to vertex 3
	
	/*
	 *Variables for nested cloak 
	 */
	EditableLensSimplicialComplex cloakI, cloakO, cloakI2, cloakO2;
	// Inside cloak
	private double baseFocalI; //base lens focal length 
	private double h1PI; // Height to lower inner vertex in physical space
	private double h2PI; // Height to upper inner vertex in physical space
	private double hI; //over all height of cloak
	private double rI; //base radius
	private Vector3D baseCentreI; //Position of the base lens centre
	private Vector3D topDirectionI; //Vector indicating the top direction of cloak
	
	// Outside cloak
	private double baseFocalO; //base lens focal length 
	private double h1PO; // Height to lower inner vertex in physical space
	private double h2PO; //Height to upper inner vertex in physical space
	private double hO; //over all height of cloak
	private double rO; //base radius
	private Vector3D baseCentreO; //Position of the base lens centre
	private Vector3D topDirectionO; //Vector indicating the top direction of cloak
	
	
	//inner cloak but aligned
	private Vector3D topVertex;
	private Vector3D h;
	private Vector3D baseVertex;
	
	
	
	/*
	 * Interactive additions
	 */
	
	private boolean lensesInsideNestedCloak; //check box to show the setup
	private boolean alignedCloaks; //check box to show the setup with the inside cloak aligned with the outside cloak
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private boolean frameI; // shows the frame of inner cloak
	private boolean frameO; //shows the frame of the outer cloak
	private double lensTrans; // set the transmission coef of the lenses
	private double cloakITrans; // set the transmission coef of the inner cloak
	private double cloakOTrans; // set the transmission coef of the outer cloak
	private double cameraZoom; //set the focus and thus zoom of camera
	private boolean showImageOfInnerCloak; //shows the image of the inner cloak
	private boolean showPatternedSphere; //shows sphere at principal point of lenses
	private boolean showImage1OfPatternedSphere; //shows the image of the sphere due to the inner cloak
	private boolean showImage2OfPatternedSphere; //show the image of the sphere due to both cloaks
	private double patternedSphereRadius = 0.02; // radius of spheres
	private LensType lensTypeI, lensTypeO; // define the lens type of the cloaks, usually either ideal thin or none
	private boolean showTrajectory;
	private Vector3D trajectoryDefaultDirection; //defines the default ray direction
	private Vector3D rayAim; // defines the direction when a position of where to look is inputed
	private Vector3D trajectoryStartPos; // defines the raytrace position to camera position when angles are the same. 
	private Vector3D trajectoryDefaultPos; //defines the manual ray trace position
	private boolean manualRayStart; //defines which starting point should be set for the ray tracing 
	private boolean manualRayDirection;//defines which direction should be set for the ray tracing 
	private double rayAngle;// sets the angle of the ray location, in equal to camera sets it to camera position
	private Vector3D rayPos; //ray trace starting position 
	private Vector3D rayDirection; // raytrace direction

		
	
	public IdealLensWormholeVisualiserWithLens()
	{
		super();
		/*
		 * Defining Lens params
		 */
		
		f1 = 0.01; //focal length lens 1,4
		f2 = 0.01; // focal length lens 2,3
//		d = (2*(f1+f2)*(f2/f1)); //separation along z axis of lenses 2 and 3
//		pP1 = new Vector3D(0,0,-0.48); //Principal point of lens 1
//		pP2 = new Vector3D(0,0,f1+f2-0.48); //Principal point of lens 2
//		pP3 = new Vector3D(0,0,f1+f2+d-0.48); //Principal point of lens 3
//		pP4 = new Vector3D(0,0,2*f1+2*f2+d-0.48); //Principal point of lens 4
		nhat = new Vector3D(0,0,1); //optical axis direction vector 
		phi0 = new Vector3D(1,0,0); // phi0Direction

		
		/*
		 * Cloak Params
		 */
		
		//Outside cloak
		baseFocalO = 0.45; //base lens focal length 
		hO = 5;//over all height of cloak
		h1PO = 0.6; // Height to lower inner vertex in physical space
		h2PO = 0.8; //Height to upper inner vertex in physical space
		rO = hO*2./3; //base radius
		baseCentreO = new Vector3D(0, 2, 0); //Position of the base lens centre
		topDirectionO = new Vector3D(0, 2, -1); //Vector indicating the top direction of cloak
		
		
		//Inside cloak
		baseFocalI = 0.075; //base lens focal length 		
		hI = (hO*h1PO*(5/9.)); //over all height of cloak
		h1PI = 0.6; // Height to lower inner vertex in physical space
		h2PI = 0.8; // Height to upper inner vertex in physical space
		rI = hI; //base radius
		baseCentreI = new Vector3D(0, 2, -hI); //Position of the base lens centre
		topDirectionI = new Vector3D(0, 2, 1); //Vector indicating the top direction of cloak	
		
		
		
		
		/*
		 * Setting the interactive params
		 */
		
		lensesInsideNestedCloak = false; // Set the default if function
		alignedCloaks = false; // Set the default if function
		frameI = true; // shows the frame of inner cloak
		frameO = true; //shows the frame of the outer cloak
		cameraAngle = 0; //setting the initial viewing angle to be from directly behind the cloak
		cameraUpAngle = 0; //setting the initial viewing angle to be from directly behind the cloak
		rayAngle = 0; //setting the ray angle to the camera angle. 
		lensTrans = 1; //sets transmission coef of lens to 1 (default)
		cloakITrans = 1; //sets transmission coef of inner cloak to 1 (default)
		cloakOTrans = 1; //sets transmission coef of outer cloak to 1 (default)
		cameraZoom = 1; //set the default focus/zoom to 1.
		showImageOfInnerCloak = true;
		showPatternedSphere = false;
		showImage1OfPatternedSphere = false;
		showImage1OfPatternedSphere = false; 
		patternedSphereRadius = 0.02; //radius all spheres will have 
		lensTypeI = LensType.NONE; //set to none by default
		lensTypeO = LensType.NONE; //set to none by default
		showTrajectory = false;
		trajectoryDefaultPos = new Vector3D(0, 0, -15); //sets the manual starting position of the ray
		trajectoryDefaultDirection = new Vector3D(0,0,1);//sets the manual direction of the ray
 		rayAim = new Vector3D(0, 0, 0);//sets the position the ray should face
		manualRayStart = false;
		manualRayDirection = false;
		
		renderQuality = RenderQualityEnum.DRAFT;//Set the default render quality		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;// set to true for interactive version
		traceRaysWithTrajectory = false;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's 4 lens setup";
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
		
			
		//shows the inner cloak in opposite direction to outer cloak
		if (lensesInsideNestedCloak) {
			
			d = (2*(f1+f2)*(f2/f1)); //separation along z axis of lenses 2 and 3
			pP1 = new Vector3D(0,2,-hI*1.2); //Principal point of lens 1
			pP2 = new Vector3D(0,2,f1+f2-hI*1.2); //Principal point of lens 2
			pP3 = new Vector3D(0,2,f1+f2+d-hI*1.2); //Principal point of lens 3
			pP4 = new Vector3D(0,2,2*f1+2*f2+d-hI*1.2); //Principal point of lens 4
			//r = (1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.);
			vert1 = new Vector3D(1,0,0);
			vert2 = new Vector3D(-0.5,-(Math.sqrt(3.)/2),0);
			vert3 = new Vector3D(-0.5,(Math.sqrt(3.)/2),0);
			//System.out.println(r);
			/*
			 * Adding the nested cloak
			 */
			double frameRadius = 0.005; // radius of cloak frame
	
			// create the inner cloak; first create a lens-simplicial complex...
			cloakI = new EditableLensSimplicialComplex(
					"Inner Abyss cloak",	// description
					scene,	// parent
					studio
				);
			// ... and initialise it as an ideal-lens cloak
			cloakI.setLensTypeRepresentingFace(lensTypeI);
			cloakI.setShowStructureP(frameI);
			cloakI.setVertexRadiusP(frameRadius);
			cloakI.setShowStructureV(false);
			cloakI.setVertexRadiusV(frameRadius);
			cloakI.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));
			
			cloakI.initialiseToOmnidirectionalLens(
					h1PI,	// physicalSpaceFractionalLowerInnerVertexHeight
					h2PI,	// physicalSpaceFractionalUpperInnerVertexHeight
					1./(-hI/baseFocalI + 1/h1PI),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
					new Vector3D(0, 2, -0.2),	// topVertex
					new Vector3D(0, 2, -hI),	// baseCentre
					new Vector3D(hI/Math.sqrt(2.), 2, 0)	// baseVertex1
				);
			scene.addSceneObject(cloakI);
			
			// create the outer cloak; first create a lens-simplicial complex...
			cloakO = new EditableLensSimplicialComplex(
					"outer Abyss cloak",	// description
					scene,	// parent
					studio
				);
			// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
			cloakO.setLensTypeRepresentingFace(lensTypeO);
			cloakO.setShowStructureP(frameO);
			cloakO.setVertexRadiusP(frameRadius);
			cloakO.setShowStructureV(false);
			cloakO.setVertexRadiusV(frameRadius);
			cloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false));
			
			cloakO.initialiseToOmnidirectionalLens(
					h1PO,	// physicalSpaceFractionalLowerInnerVertexHeight
					h2PO,	// physicalSpaceFractionalUpperInnerVertexHeight
					1./(-hO/baseFocalO + 1/h1PO),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
					new Vector3D(0, 2, -hO),	// topVertex
					new Vector3D(0, 2, 0),	// baseCentre
					new Vector3D(hO/Math.sqrt(2.), 2, 0)	// baseVertex1
				);
			scene.addSceneObject(cloakO);
			
			try {
				// make a copy of the simplicial complex representing the inner cloak's physical-space structure...
				// SimplicialComplex innerCloakImage = new SimplicialComplex(abyssCloakI.getLensSimplicialComplex());
				// SimplicialComplex innerCloakImage = new SimplicialComplex();
				
				// ... and change the positions of all the vertices to the image positions due to the outer cloak
				ArrayList<Vector3D> innerCloakImageVertices = new ArrayList<Vector3D>(6);
				for(Vector3D vertex:cloakI.getVertices()) innerCloakImageVertices.add(cloakO.getLensSimplicialComplex().mapToOutside(0, vertex));
				ArrayList<Edge> innerCloakImageEdges = new ArrayList<Edge>(10);
				for(Edge edge:cloakI.getEdges()) innerCloakImageEdges.add(edge.clone());
				// System.out.println("NestedAbyssCloakExplorer::populateStudio(): innerCloakImageVertices="+innerCloakImageVertices+", innerCloakImageEdges = "+innerCloakImageEdges);
				SimplicialComplex innerCloakImage = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(
						innerCloakImageVertices,	// vertices
						innerCloakImageEdges	// edges
					);
				scene.addSceneObject(innerCloakImage.getEditableSceneObjectCollection(
						"Image of inner cloak due to outer cloak",	// description
						true,	// showVertices
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// vertexSurfaceProperty
						frameRadius,	// vertexRadius
						true,	// showEdges
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// edgeSurfaceProperty
						frameRadius,	// edgeRadius
						false,
						null,	// faceSurfaceProperty
						scene,
						studio
					), showImageOfInnerCloak);
			} catch (InconsistencyException e) {
				//  something went wrong with the cloning -- panic!
				e.printStackTrace();
			}
			
		
			
			/*
			 * Adding the lenses
			 */
			
			scene.addSceneObject(new ParametrisedTriangle(
					"lens 1",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.), vert1), pP1), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.),vert2), pP1),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.),vert1), pP1)), // vertex 2
					Vector3D.difference(Vector3D.sum(
							Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.),vert3), pP1),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.),vert1), pP1)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							pP1,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							f1,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject(new ParametrisedTriangle(
					"lens 2",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP2)-0.25*hI)/Math.sqrt(2.), vert1), pP2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP2)-0.25*hI)/Math.sqrt(2.),vert2), pP2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP2)-0.25*hI)/Math.sqrt(2.),vert1), pP2)), // vertex 2
					Vector3D.difference(Vector3D.sum(
							Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP2)-0.25*hI)/Math.sqrt(2.),vert3), pP2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP2)-0.25*hI)/Math.sqrt(2.),vert1), pP2)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							pP2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							f2,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject(new ParametrisedTriangle(
					"lens 3",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP3)-0.25*hI)/Math.sqrt(2.), vert1), pP3), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP3)-0.25*hI)/Math.sqrt(2.),vert2), pP3),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP3)-0.25*hI)/Math.sqrt(2.),vert1), pP3)), // vertex 2
					Vector3D.difference(Vector3D.sum(
							Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP3)-0.25*hI)/Math.sqrt(2.),vert3), pP3),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP3)-0.25*hI)/Math.sqrt(2.),vert1), pP3)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							pP3,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							f2,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject(new ParametrisedTriangle(
					"lens 4",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP4)-0.25*hI)/Math.sqrt(2.), vert1), pP4), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP4)-0.25*hI)/Math.sqrt(2.),vert2), pP4),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP4)-0.25*hI)/Math.sqrt(2.),vert1), pP4)), // vertex 2
					Vector3D.difference(Vector3D.sum(
							Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP4)-0.25*hI)/Math.sqrt(2.),vert3), pP4),
							Vector3D.sum(Vector3D.scalarTimesVector3D((1.25*Vector3D.getDistance(baseCentreO, pP4)-0.25*hI)/Math.sqrt(2.),vert1), pP4)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							pP4,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							f1,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
					
			

			
			/*
			 * Adding spheres at centre of lenses
			 */
						
	
			
			// the patterned sphere for lens 1
			EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
					"lens 1 principal point", // description
					pP1, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(patternedSphere1, showPatternedSphere);
			
			// ... and the positions of its image due to the inner cloak...
			Vector3D pP1p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP1);
			EditableScaledParametrisedSphere pP1patternedSphere1 = new EditableScaledParametrisedSphere(
					"Image of lens 1 due to inner cloak", // description
					pP1p1, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP1patternedSphere1, showImage1OfPatternedSphere);
			
			// ... and due to both cloaks
			Vector3D pP1p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP1p1);
			EditableScaledParametrisedSphere pP1patternedSphere2 = new EditableScaledParametrisedSphere(
					"Image of lens 1 due to both cloaks", // description
					pP1p2, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP1patternedSphere2, showImage2OfPatternedSphere);
			
			
			
			
			
			//principal point lens 2
			EditableScaledParametrisedSphere patternedSphere2 = new EditableScaledParametrisedSphere(
					"lens 2 principal point", // description
					pP2, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(patternedSphere2, showPatternedSphere);
			
			// ... and the positions of its image due to the inner cloak...
			Vector3D pP2p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP2);
			EditableScaledParametrisedSphere pP2patternedSphere1 = new EditableScaledParametrisedSphere(
					"Image of lens 2 due to inner cloak", // description
					pP2p1, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP2patternedSphere1, showImage1OfPatternedSphere);
			
			// ... and due to both cloaks
			Vector3D pP2p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP2p1);
			EditableScaledParametrisedSphere pP2patternedSphere2 = new EditableScaledParametrisedSphere(
					"Image of lens 2 due to both cloaks", // description
					pP2p2, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP2patternedSphere2, showImage2OfPatternedSphere);
			
			
			
			
			
			//principal point lens 3
			EditableScaledParametrisedSphere patternedSphere3 = new EditableScaledParametrisedSphere(
					"lens 3 principal point", // description
					pP3, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(patternedSphere3, showPatternedSphere);
			
			// ... and the positions of its image due to the inner cloak...
			Vector3D pP3p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP3);
			EditableScaledParametrisedSphere pP3patternedSphere1 = new EditableScaledParametrisedSphere(
					"Image of lens 3 due to inner cloak", // description
					pP3p1, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP3patternedSphere1, showImage1OfPatternedSphere);
			
			// ... and due to both cloaks
			Vector3D pP3p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP3p1);
			EditableScaledParametrisedSphere pP3patternedSphere2 = new EditableScaledParametrisedSphere(
					"Image of lens 3 due to both cloaks", // description
					pP3p2, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP3patternedSphere2, showImage2OfPatternedSphere);
			
			
			
			
			
			//principal point lens 4
			EditableScaledParametrisedSphere patternedSphere4 = new EditableScaledParametrisedSphere(
					"lens 4 principal point", // description
					pP4, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(patternedSphere4, showPatternedSphere);
			
			// ... and the positions of its image due to the inner cloak...
			Vector3D pP4p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP4);
			EditableScaledParametrisedSphere pP4patternedSphere1 = new EditableScaledParametrisedSphere(
					"Image of lens 4 due to inner cloak", // description
					pP4p1, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP4patternedSphere1, showImage1OfPatternedSphere);
			
			// ... and due to both cloaks
			Vector3D pP4p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP4p1);
			EditableScaledParametrisedSphere pP4patternedSphere2 = new EditableScaledParametrisedSphere(
					"Image of lens 4 due to both cloaks", // description
					pP4p2, // centre
					patternedSphereRadius,	// radius
					new Vector3D(1, 1, 1),	// pole
					new Vector3D(1, 0, 0),	// phi0Direction
					0, Math.PI,	// sThetaMin, sThetaMax
					-Math.PI, Math.PI,	// sPhiMin, sPhiMax
					new SurfaceTiling(
							new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
							new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
							2*Math.PI/6,
							2*Math.PI/6
						),
					scene, studio);
			scene.addSceneObject(pP4patternedSphere2, showImage2OfPatternedSphere);			
		}
		
					
					

	
	if (alignedCloaks) {
		
		baseVertex = new Vector3D(0, 2, -1.5*baseFocalO);
		topVertex = new Vector3D(0,2,-1.5*hI);
		h = Vector3D.sum(Vector3D.scalarTimesVector3D(0.6,Vector3D.difference(topVertex,baseVertex)),baseVertex);
		d = (2*(f1+f2)*(f2/f1)); //separation along z axis of lenses 2 and 3
		pP1 = new Vector3D(0,2,-1.5*baseFocalO-0.1); //Principal point of lens 1
		pP2 = new Vector3D(0,2,-(f1+f2)-1.5*baseFocalO-0.1); //Principal point of lens 2
		pP3 = new Vector3D(0,2,-(f1+f2+d)-1.5*baseFocalO-0.1); //Principal point of lens 3
		pP4 = new Vector3D(0,2,-(2*f1+2*f2+d)-1.5*baseFocalO-0.1); //Principal point of lens 4
		//r = (1.25*Vector3D.getDistance(baseCentreO, pP1)-0.25*hI)/Math.sqrt(2.);
		vert1 = new Vector3D(1,0,0);
		vert2 = new Vector3D(-0.5,-(Math.sqrt(3.)/2),0);
		vert3 = new Vector3D(-0.5,(Math.sqrt(3.)/2),0);
		//System.out.println(r);
		/*
		 * Adding the nested cloak
		 */
		double frameRadius = 0.005; // radius of cloak frame

		// create the inner cloak; first create a lens-simplicial complex...
		cloakI = new EditableLensSimplicialComplex(
				"Inner Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		cloakI.setLensTypeRepresentingFace(lensTypeI);
		cloakI.setShowStructureP(frameI);
		cloakI.setVertexRadiusP(frameRadius);
		cloakI.setShowStructureV(false);
		cloakI.setVertexRadiusV(frameRadius);
		cloakI.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));
		
		cloakI.initialiseToOmnidirectionalLens(
				h1PI,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2PI,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-hI/baseFocalI + 1/h1PI),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				topVertex,	// topVertex
				baseVertex,	// baseCentre
				new Vector3D(0.8*hI/Math.sqrt(2.), 2, 0)	// baseVertex1
			);
		scene.addSceneObject(cloakI);
		
		// create the outer cloak; first create a lens-simplicial complex...
		cloakO = new EditableLensSimplicialComplex(
				"outer Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
		cloakO.setLensTypeRepresentingFace(lensTypeO);
		cloakO.setShowStructureP(frameO);
		cloakO.setVertexRadiusP(frameRadius);
		cloakO.setShowStructureV(false);
		cloakO.setVertexRadiusV(frameRadius);
		cloakO.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false));
		
		cloakO.initialiseToOmnidirectionalLens(
				h1PO,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2PO,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-hO/baseFocalO + 1/h1PO),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, 2, -hO),	// topVertex
				new Vector3D(0, 2, 0),	// baseCentre
				new Vector3D(hO/Math.sqrt(2.), 2, 0)	// baseVertex1
			);
		scene.addSceneObject(cloakO);
		
		try {
			// make a copy of the simplicial complex representing the inner cloak's physical-space structure...
			// SimplicialComplex innerCloakImage = new SimplicialComplex(abyssCloakI.getLensSimplicialComplex());
			// SimplicialComplex innerCloakImage = new SimplicialComplex();
			
			// ... and change the positions of all the vertices to the image positions due to the outer cloak
			ArrayList<Vector3D> innerCloakImageVertices = new ArrayList<Vector3D>(6);
			for(Vector3D vertex:cloakI.getVertices()) innerCloakImageVertices.add(cloakO.getLensSimplicialComplex().mapToOutside(0, vertex));
			ArrayList<Edge> innerCloakImageEdges = new ArrayList<Edge>(10);
			for(Edge edge:cloakI.getEdges()) innerCloakImageEdges.add(edge.clone());
			// System.out.println("NestedAbyssCloakExplorer::populateStudio(): innerCloakImageVertices="+innerCloakImageVertices+", innerCloakImageEdges = "+innerCloakImageEdges);
			SimplicialComplex innerCloakImage = SimplicialComplex.getSimplicialComplexFromVerticesAndEdges(
					innerCloakImageVertices,	// vertices
					innerCloakImageEdges	// edges
				);
			scene.addSceneObject(innerCloakImage.getEditableSceneObjectCollection(
					"Image of inner cloak due to outer cloak",	// description
					true,	// showVertices
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// vertexSurfaceProperty
					frameRadius,	// vertexRadius
					true,	// showEdges
					new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// edgeSurfaceProperty
					frameRadius,	// edgeRadius
					false,
					null,	// faceSurfaceProperty
					scene,
					studio
				), showImageOfInnerCloak);
		} catch (InconsistencyException e) {
			//  something went wrong with the cloning -- panic!
			e.printStackTrace();
		}
		
	
		
		/*
		 * Adding the lenses
		 */
		
			lens1 = new ParametrisedTriangle(
				"lens 1",	//description,
				Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP1, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP1), // vertex 1
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP1, h)/Vector3D.getDistance(baseVertex, h) ),vert2), pP1),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP1, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP1)), // vertex 2
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP1, h)/Vector3D.getDistance(baseVertex, h) ),vert3), pP1),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP1, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP1)), // vertex 3
				false,					
				new IdealThinLensSurfaceSimple(
						pP1,
						new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
						f1,	// focalLength,
						lensTrans,	// transmissionCoefficient
						false	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
				);
		scene.addSceneObject(lens1);
		
			lens2 = new ParametrisedTriangle(
				"lens 2",	//description,
				Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP2), // vertex 1
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), pP2),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP2)), // vertex 2
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), pP2),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP2)), // vertex 3
				false,					
				new IdealThinLensSurfaceSimple(
						pP2,
						new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
						f2,	// focalLength,
						lensTrans,	// transmissionCoefficient
						false	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
				);
		scene.addSceneObject(lens2);
		
		lens3 = new ParametrisedTriangle(
				"lens 3",	//description,
				Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP3, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP3), // vertex 1
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP3, h)/Vector3D.getDistance(baseVertex, h) ),vert2), pP3),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP3, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP3)), // vertex 2
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP3, h)/Vector3D.getDistance(baseVertex, h) ),vert3), pP3),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP3, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP3)), // vertex 3
				false,					
				new IdealThinLensSurfaceSimple(
						pP3,
						new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
						f2,	// focalLength,
						lensTrans,	// transmissionCoefficient
						false	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
				);
		scene.addSceneObject(lens3);
		
		lens4 = new ParametrisedTriangle(
				"lens 4",	//description,
				Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP4, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP4), // vertex 1
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP4, h)/Vector3D.getDistance(baseVertex, h) ),vert2), pP4),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP4, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP4)), // vertex 2
				Vector3D.difference(
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP4, h)/Vector3D.getDistance(baseVertex, h) ),vert3), pP4),
						Vector3D.sum(Vector3D.scalarTimesVector3D(((0.8*hI/Math.sqrt(2.))*Vector3D.getDistance(pP4, h)/Vector3D.getDistance(baseVertex, h) ),vert1), pP4)), // vertex 3
				false,					
				new IdealThinLensSurfaceSimple(
						pP4,
						new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
						f1,	// focalLength,
						lensTrans,	// transmissionCoefficient
						false	// shadow-throwing
					),	// surface property
				scene,	// parent, 
				studio	// the studio
				);
		scene.addSceneObject(lens4);
				
		

		
		/*
		 * Adding spheres at centre of lenses
		 */
					

		
		// the patterned sphere for lens 1
		EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
				"lens 1 principal point", // description
				pP1, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere1, showPatternedSphere);
		
		// ... and the positions of its image due to the inner cloak...
		Vector3D pP1p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP1);
		EditableScaledParametrisedSphere pP1patternedSphere1 = new EditableScaledParametrisedSphere(
				"Image of lens 1 due to inner cloak", // description
				pP1p1, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP1patternedSphere1, showImage1OfPatternedSphere);
		
		// ... and due to both cloaks
		Vector3D pP1p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP1p1);
		EditableScaledParametrisedSphere pP1patternedSphere2 = new EditableScaledParametrisedSphere(
				"Image of lens 1 due to both cloaks", // description
				pP1p2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREY90, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP1patternedSphere2, showImage2OfPatternedSphere);
		
		
		
		
		
		//principal point lens 2
		EditableScaledParametrisedSphere patternedSphere2 = new EditableScaledParametrisedSphere(
				"lens 2 principal point", // description
				pP2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere2, showPatternedSphere);
		
		// ... and the positions of its image due to the inner cloak...
		Vector3D pP2p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP2);
		EditableScaledParametrisedSphere pP2patternedSphere1 = new EditableScaledParametrisedSphere(
				"Image of lens 2 due to inner cloak", // description
				pP2p1, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP2patternedSphere1, showImage1OfPatternedSphere);
		
		// ... and due to both cloaks
		Vector3D pP2p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP2p1);
		EditableScaledParametrisedSphere pP2patternedSphere2 = new EditableScaledParametrisedSphere(
				"Image of lens 2 due to both cloaks", // description
				pP2p2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP2patternedSphere2, showImage2OfPatternedSphere);
		
		
		
		
		
		//principal point lens 3
		EditableScaledParametrisedSphere patternedSphere3 = new EditableScaledParametrisedSphere(
				"lens 3 principal point", // description
				pP3, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere3, showPatternedSphere);
		
		// ... and the positions of its image due to the inner cloak...
		Vector3D pP3p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP3);
		EditableScaledParametrisedSphere pP3patternedSphere1 = new EditableScaledParametrisedSphere(
				"Image of lens 3 due to inner cloak", // description
				pP3p1, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP3patternedSphere1, showImage1OfPatternedSphere);
		
		// ... and due to both cloaks
		Vector3D pP3p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP3p1);
		EditableScaledParametrisedSphere pP3patternedSphere2 = new EditableScaledParametrisedSphere(
				"Image of lens 3 due to both cloaks", // description
				pP3p2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP3patternedSphere2, showImage2OfPatternedSphere);
		
		
		
		
		
		//principal point lens 4
		EditableScaledParametrisedSphere patternedSphere4 = new EditableScaledParametrisedSphere(
				"lens 4 principal point", // description
				pP4, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.BLACK, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(patternedSphere4, showPatternedSphere);
		
		// ... and the positions of its image due to the inner cloak...
		Vector3D pP4p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP4);
		EditableScaledParametrisedSphere pP4patternedSphere1 = new EditableScaledParametrisedSphere(
				"Image of lens 4 due to inner cloak", // description
				pP4p1, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP4patternedSphere1, showImage1OfPatternedSphere);
		
		// ... and due to both cloaks
		Vector3D pP4p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, pP4p1);
		EditableScaledParametrisedSphere pP4patternedSphere2 = new EditableScaledParametrisedSphere(
				"Image of lens 4 due to both cloaks", // description
				pP4p2, // centre
				patternedSphereRadius,	// radius
				new Vector3D(1, 1, 1),	// pole
				new Vector3D(1, 0, 0),	// phi0Direction
				0, Math.PI,	// sThetaMin, sThetaMax
				-Math.PI, Math.PI,	// sPhiMin, sPhiMax
				new SurfaceTiling(
						new SurfaceColour(DoubleColour.PURPLE, DoubleColour.WHITE, false),	// like SurfaceColour.BLACK_SHINY, but doesn't throw a shadow
						new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false),	// like SurfaceColour.GREY90_SHINY, but not shadow-throwing
						2*Math.PI/6,
						2*Math.PI/6
					),
				scene, studio);
		scene.addSceneObject(pP4patternedSphere2, showImage2OfPatternedSphere);			
	}
	
	
	
	scene.addSceneObject(new EditableTimHead(
			"Tim's head",	// description
			new Vector3D(0, 2, 10),	// centre, was (0, 0, 13)
			0.5,	// radius
			new Vector3D(0, 0, -1),	// front direction
			new Vector3D(0, 1, 0),	// top direction
			new Vector3D(1, 0, 0),	// right direction
			scene,	// parent
			studio
		));
	

	
	/*
	 * Setting the camera
	 */	
	int
		quality = 2,	// 1 = normal, 2 = good, 4 = great
		pixelsX = 640*quality,
		pixelsY = 480*quality,
		antiAliasingFactor = 1;
		
		ApertureCamera camera = new ApertureCamera(
			"Camera",
			new Vector3D(20*Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), 2+20*Math.sin(Math.toRadians(cameraUpAngle)), -20*Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle))),	// centre of aperture
			new Vector3D(-Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), -Math.sin(Math.toRadians(cameraUpAngle)), Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle))),	// view direction (magnitude is distance to detector centre)
			new Vector3D(-(double)pixelsX/pixelsY*Math.cos(Math.toRadians(cameraAngle)), 0, -(double)pixelsX/pixelsY*Math.sin(Math.toRadians(cameraAngle))),	// horizontal basis Vector3D
			new Vector3D(0, -1, 0),	// vertical basis Vector3D
			pixelsX*antiAliasingFactor, pixelsY*antiAliasingFactor,	// logical number of pixels
			ExposureCompensationType.EC0,
			1000,	// maxTraceLevel
			cameraZoom,	// focusing distance
			0,	// aperture radius
			1	// rays per pixel; the more, the less noisy the photo is
	);
		//EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				
				
				
				
				
			//	)
				
				
				


	
	if(showTrajectory)
	{
		if(manualRayStart)
		{ rayPos = trajectoryDefaultPos;					
		}
		else
		{ rayPos = new Vector3D(20*Math.sin(Math.toRadians(rayAngle)), 2, -20*Math.cos(Math.toRadians(rayAngle)));//sets the 'automatic' position of the ray ;	
		}
		if(manualRayDirection)
		{ rayDirection = trajectoryDefaultDirection;
		}
		else
		{rayDirection = Vector3D.difference(rayAim, rayPos);	
		}	
		
	
		
		double frameRadius = 0.005;
				// do the tracing of rays with trajectory
		scene.addSceneObject(
				new EditableRayTrajectory(
						"light-ray trajectory",	// description
						rayPos,	// startPoint
						0,	// startTime
						rayDirection,	// startDirection
						0.02,	// rayRadius
						SurfaceColourLightSourceIndependent.GREEN,	// surfaceProperty
						1000,	// maxTraceLevel
						true,	// reportToConsole
						scene,	// parent
						studio
						)
				);

		studio.setScene(scene);

		// trace the rays with trajectory through the scene
		studio.traceRaysWithTrajectory();
		
		scene.removeSceneObject(cloakI);
		scene.removeSceneObject(cloakO);
		scene.removeSceneObject(lens1);
		scene.removeSceneObject(lens2);
		scene.removeSceneObject(lens3);
		scene.removeSceneObject(lens4);
		
		
		// create the inner cloak; first create a lens-simplicial complex...
		cloakI2 = new EditableLensSimplicialComplex(
				"Inner Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak
		cloakI2.setLensTypeRepresentingFace(LensType.SEMITRANSPARENT_PLANE);
		cloakI2.setShowStructureP(true);
		cloakI2.setVertexRadiusP(frameRadius);
		cloakI2.setShowStructureV(false);
		cloakI2.setVertexRadiusV(frameRadius);
		cloakI2.setSurfacePropertyP(new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, false));
		
		cloakI2.initialiseToOmnidirectionalLens(
				h1PI,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2PI,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-hI/baseFocalI + 1/h1PI),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				topVertex,	// topVertex
				baseVertex,	// baseCentre
				new Vector3D(0.9*hI/Math.sqrt(2.), 2, 0)	// baseVertex1
			);
		scene.addSceneObject(cloakI2);
		
		// create the outer cloak; first create a lens-simplicial complex...
		cloakO2 = new EditableLensSimplicialComplex(
				"outer Abyss cloak",	// description
				scene,	// parent
				studio
			);
		// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
		cloakO2.setLensTypeRepresentingFace(LensType.SEMITRANSPARENT_PLANE);
		cloakO2.setShowStructureP(true);
		cloakO2.setVertexRadiusP(frameRadius);
		cloakO2.setShowStructureV(false);
		cloakO2.setVertexRadiusV(frameRadius);
		cloakO2.setSurfacePropertyP(new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, false));
		
		cloakO2.initialiseToOmnidirectionalLens(
				h1PO,	// physicalSpaceFractionalLowerInnerVertexHeight
				h2PO,	// physicalSpaceFractionalUpperInnerVertexHeight
				1./(-hO/baseFocalO + 1/h1PO),	// virtualSpaceFractionalLowerInnerVertexHeightI,	// virtualSpaceFractionalLowerInnerVertexHeight
				new Vector3D(0, 2, -hO),	// topVertex
				new Vector3D(0, 2, 0),	// baseCentre
				new Vector3D(hO/Math.sqrt(2.), 2, 0)	// baseVertex1
			);
		scene.addSceneObject(cloakO2);
		
		
	}
	
			
				
				
		
		studio.setScene(scene);
		studio.setCamera(camera);		
		studio.setLights(LightSource.getStandardLightsFromBehind());
}
	

	


	private LabelledDoublePanel f1Panel, f2Panel,cameraAnglePanel, lensTransPanel, cloakITransPanel, cloakOTransPanel, cameraZoomPanel, baseLensFIPanel, baseLensFOPanel, patternedSphereRadiusPanel,rayAnglePanel,cameraUpAnglePanel;
	private JCheckBox lensesInsideNestedCloakCheck, alignedCloaksCheck, frameICheck, frameOCheck, showImageOfInnerCloakCheckBox, showPatternedSphereCheckBox, showImage1OfPatternedSphereCheckBox, showImage2OfPatternedSphereCheckBox,showTrajectoryPanel,
	manualRayStartCheckBox,manualRayDirectionCheckBox;	
	private JTextArea patternedSphereCentreInfoTextArea;
	private JButton patternedSphereCentreInfoButton;
	private JComboBox<LensType> lensTypeIComboBox, lensTypeOComboBox;
	private LabelledVector3DPanel trajectoryDefaultPosPanel, trajectoryDefaultDirectionPanel, rayAimPanel;
	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		
		//first panel 
		JPanel setScene = new JPanel();
		setScene.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Set the Scene", setScene);
		
		lensesInsideNestedCloakCheck = new JCheckBox("Shows the 4 Lenses inside an omnidirectional abyss cloak");
		setScene.add(lensesInsideNestedCloakCheck, "span");
		
		alignedCloaksCheck = new JCheckBox("Show the inner cloak aligned with the outer cloak");
		setScene.add(alignedCloaksCheck, "span");	
		
		
		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		
		// inner Abyss cloak
		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");
		
//		
//		cloakITransPanel = new LabelledDoublePanel("Inner cloak transmission coefficient, set between 0-1, default = 1");
//		cloakITransPanel.setNumber(cloakITrans);
//		innerLensCloakPanel.add(cloakITransPanel, "span");		
		
		frameICheck = new JCheckBox("Toggles the frames of the inner cloak");
		innerLensCloakPanel.add(frameICheck, "span");
		
		baseLensFIPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFIPanel.setNumber(baseFocalI);
		innerLensCloakPanel.add(baseLensFIPanel, "span");
		
		lensTypeIComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeIComboBox.setSelectedItem(lensTypeI);
		innerLensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeIComboBox), "span");
		
		// outer Abyss cloak
		JPanel outerLensCloakPanel = new JPanel();
		outerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		outerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Outer cloak"));
		cloaksPanel.add(outerLensCloakPanel, "wrap");

//		cloakOTransPanel = new LabelledDoublePanel("Outer cloak transmission coefficient, set between 0-1, default = 1");
//		cloakOTransPanel.setNumber(cloakOTrans);
//		outerLensCloakPanel.add(cloakOTransPanel, "span");
		
		frameOCheck = new JCheckBox("Toggles the frames of the outer cloak");
		outerLensCloakPanel.add(frameOCheck, "span");
		
		baseLensFOPanel = new LabelledDoublePanel("Focal length of base lens");
		baseLensFOPanel.setNumber(baseFocalO);
		outerLensCloakPanel.add(baseLensFOPanel, "span");
		
		
		showImageOfInnerCloakCheckBox = new JCheckBox("Show (purple) cylinder model of image of inner cloak due to outer cloak");
		showImageOfInnerCloakCheckBox.setSelected(showImageOfInnerCloak);
		cloaksPanel.add(showImageOfInnerCloakCheckBox, "span");
		
		lensTypeOComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeOComboBox.setSelectedItem(lensTypeO);
		outerLensCloakPanel.add(GUIBitsAndBobs.makeRow("Lens type", lensTypeOComboBox), "span");
		
		
		//lens panel
		JPanel lensPanel = new JPanel();
		lensPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Lenses", lensPanel);
		
		f1Panel = new LabelledDoublePanel("Focal length of Lenses 1&4");
		f1Panel.setNumber(f1);
		lensPanel.add(f1Panel, "span");
		
		f2Panel = new LabelledDoublePanel("Focal length of Lenses 2&3");
		f2Panel.setNumber(f2);
		lensPanel.add(f2Panel, "span");
		
		lensTransPanel = new LabelledDoublePanel("Lens transmission coefficient, set between 0-1, default = 1");
		lensTransPanel.setNumber(lensTrans);
		lensPanel.add(lensTransPanel, "span");
		
		patternedSphereRadiusPanel = new LabelledDoublePanel("Radius of spheres at principal points");
		patternedSphereRadiusPanel.setNumber(patternedSphereRadius);
		lensPanel.add(patternedSphereRadiusPanel, "span");

		showPatternedSphereCheckBox = new JCheckBox("Show all principal points");
		showPatternedSphereCheckBox.setSelected(showPatternedSphere);
		lensPanel.add(showPatternedSphereCheckBox, "span");
		
		showImage1OfPatternedSphereCheckBox = new JCheckBox("Show (red) images due to inner cloak");
		showImage1OfPatternedSphereCheckBox.setSelected(showImage1OfPatternedSphere);
		lensPanel.add(showImage1OfPatternedSphereCheckBox, "span");
		
		showImage2OfPatternedSphereCheckBox = new JCheckBox("Show (purple) images due to both cloaks");
		showImage2OfPatternedSphereCheckBox.setSelected(showImage2OfPatternedSphere);
		lensPanel.add(showImage2OfPatternedSphereCheckBox, "span");
		
		patternedSphereCentreInfoTextArea = new JTextArea(20, 40);
		JScrollPane scrollPane = new JScrollPane(patternedSphereCentreInfoTextArea); 
		patternedSphereCentreInfoTextArea.setEditable(false);
		patternedSphereCentreInfoTextArea.setText("Click on Update button to show info");
		patternedSphereCentreInfoButton = new JButton("Update");
		patternedSphereCentreInfoButton.addActionListener(this);
		lensPanel.add(GUIBitsAndBobs.makeRow(scrollPane, patternedSphereCentreInfoButton), "span");

		
		
		//camera panel
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("camera", panel);
		
		cameraAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking at origin");
		cameraAnglePanel.setNumber(cameraAngle);
		panel.add(cameraAnglePanel, "span");
		
		cameraUpAnglePanel = new LabelledDoublePanel("Angle of which the camera is looking down at the origin");
		cameraUpAnglePanel.setNumber(cameraUpAngle);
		panel.add(cameraUpAnglePanel, "span");
		
		cameraZoomPanel = new LabelledDoublePanel("Zoom/focus of camera. Default = 1 (-iv looks backwards)");
		cameraZoomPanel.setNumber(cameraZoom);
		panel.add(cameraZoomPanel, "span");
		
		
		//raytrace
		JPanel rayTracePanel = new JPanel();
		rayTracePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Raytrace", rayTracePanel);
	
		showTrajectoryPanel = new JCheckBox("Show the trajectory a of  light ray");
		showTrajectoryPanel.setSelected(showTrajectory);
		rayTracePanel.add(showTrajectoryPanel, "span");
		
		//semi automatic parts
		rayAnglePanel = new LabelledDoublePanel("Angle of which the ray is looking at origin (set to camera angle to get place ray at camera position)");
		rayAnglePanel.setNumber(rayAngle);
		rayTracePanel.add(rayAnglePanel, "span");
		
		rayAimPanel = new LabelledVector3DPanel("Set the physical position the ray should point to");
		rayAimPanel.setVector3D(rayAim);
		rayTracePanel.add(rayAimPanel, "span");
		 
		
		//manual parts
		manualRayStartCheckBox = new JCheckBox("use manual ray position");
		manualRayStartCheckBox.setSelected(manualRayStart);
		rayTracePanel.add(manualRayStartCheckBox, "span");
		
		trajectoryDefaultPosPanel = new LabelledVector3DPanel("Set the position of the ray manually when manual is selected");
		trajectoryDefaultPosPanel.setVector3D(trajectoryDefaultPos);
		rayTracePanel.add(trajectoryDefaultPosPanel, "span");
		
		
		manualRayDirectionCheckBox = new JCheckBox("use manual ray direction");
		manualRayDirectionCheckBox.setSelected(manualRayDirection);
		rayTracePanel.add(manualRayDirectionCheckBox, "span");
		
		trajectoryDefaultDirectionPanel = new LabelledVector3DPanel("Set the direction of the ray manually when manual is selected");
		trajectoryDefaultDirectionPanel.setVector3D(trajectoryDefaultDirection);
		rayTracePanel.add(trajectoryDefaultDirectionPanel, "span");
		
		

	}
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		//cloaks
		//cloakITrans = cloakITransPanel.getNumber();
		//cloakOTrans = cloakOTransPanel.getNumber();
		lensesInsideNestedCloak = lensesInsideNestedCloakCheck.isSelected();
		frameI = frameICheck.isSelected();
		frameO = frameOCheck.isSelected();		
		baseFocalI = baseLensFIPanel.getNumber();
		baseFocalO = baseLensFOPanel.getNumber();
		showImageOfInnerCloak = showImageOfInnerCloakCheckBox.isSelected();
		lensTypeO = (LensType)(lensTypeOComboBox.getSelectedItem());
		lensTypeI = (LensType)(lensTypeIComboBox.getSelectedItem());
		
//		//does not work yet
		alignedCloaks = alignedCloaksCheck.isSelected();

		
		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraZoom = cameraZoomPanel.getNumber();
		
		//raytrace
		

		
		showTrajectory = showTrajectoryPanel.isSelected();
		
		rayAngle = rayAnglePanel.getNumber(); //angle
		rayAim = rayAimPanel.getVector3D(); //where to aim gets calculated
		
		
		manualRayStart = manualRayStartCheckBox.isSelected(); //checkbox to set to manual position
		trajectoryDefaultPos = trajectoryDefaultPosPanel.getVector3D();//manual position of beam

		manualRayDirection = manualRayDirectionCheckBox.isSelected(); //checkbox to set to manual direction
		trajectoryDefaultDirection = trajectoryDefaultDirectionPanel.getVector3D();//manual direction of beam
		
	
		
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		lensTrans = lensTransPanel.getNumber();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		showImage1OfPatternedSphere = showImage1OfPatternedSphereCheckBox.isSelected();
		showImage2OfPatternedSphere = showImage2OfPatternedSphereCheckBox.isSelected();
		
	}
	
	public boolean isVisible(Vector3D positionOfImageDueToBothCloaks)
	{
		// first get the positions of the vertices of the inner cloak...
		ArrayList<NamedVector3D> abyssCloakIVertices = cloakI.getVertices();
		// ... and calculate the positions of their images due to the outer cloak
		ArrayList<Vector3D> abyssCloakIVertexImages = new ArrayList<Vector3D>(4);
		for(Vector3D vertex:abyssCloakIVertices)
			abyssCloakIVertexImages.add(cloakO.getLensSimplicialComplex().mapToOutside(0, vertex));
		
		for(Vector3D vertex:abyssCloakIVertexImages)
		{
			if(cloakO.getClosestRayIntersection(
					new Ray(positionOfImageDueToBothCloaks, Vector3D.difference(vertex, positionOfImageDueToBothCloaks), 0)
				) != RaySceneObjectIntersection.NO_INTERSECTION) return true;

			if(cloakO.getClosestRayIntersection(
					new Ray(positionOfImageDueToBothCloaks, Vector3D.difference(positionOfImageDueToBothCloaks, vertex), 0)
				) != RaySceneObjectIntersection.NO_INTERSECTION) return true;
		}

		return false;
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(patternedSphereCentreInfoButton))
		{

			pP1 = new Vector3D(0,2,-3*baseFocalO); //Principal point of lens 1
			pP2 = new Vector3D(0,2,-(f1+f2)-3*baseFocalO); //Principal point of lens 2
			pP3 = new Vector3D(0,2,-(f1+f2+d)-3*baseFocalO); //Principal point of lens 3
			pP4 = new Vector3D(0,2,-(2*f1+2*f2+d)-3*baseFocalO); //Principal point of lens 4
			
			// position of the image of the centre of the sphere due to the inner cloak
			Vector3D l1p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP1);

			// position of the image of p1 due to the outer cloak
			Vector3D l1p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, l1p1);
			
			// is this image due to both cloaks visible?

		
			
			// position of the image of the centre of the sphere due to the inner cloak
			Vector3D l2p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP2);

			// position of the image of p1 due to the outer cloak
			Vector3D l2p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, l2p1);
			
			// is this image due to both cloaks visible?

		
		
			
			
			// position of the image of the centre of the sphere due to the inner cloak
			Vector3D l3p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP3);

			// position of the image of p1 due to the outer cloak
			Vector3D l3p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, l3p1);
			
			// is this image due to both cloaks visible?


			
			// position of the image of the centre of the sphere due to the inner cloak
			Vector3D l4p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(pP4);

			// position of the image of p1 due to the outer cloak
			Vector3D l4p2 = cloakO.getLensSimplicialComplex().mapToOutside(0, l4p1);
			
			// is this image due to both cloaks visible?

			patternedSphereCentreInfoTextArea.setText(
					"Sphere 1 centre in simplex #"+cloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(pP1)+"\n"+
					"Position of image of sphere centre due to inner cloak: "+l1p1+"\n"+
					"Position of image of sphere centre due to both cloaks: "+l1p2+"\n"+
					"This image is "+(isVisible(l1p2)?"visible":"not visible")+
					"Sphere 2 centre in simplex #"+cloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(pP2)+"\n"+
					"Position of image of sphere centre due to inner cloak: "+l2p1+"\n"+
					"Position of image of sphere centre due to both cloaks: "+l2p2+"\n"+
					"This image is "+(isVisible(l2p2)?"visible":"not visible")+
					"Sphere 3 centre in simplex #"+cloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(pP3)+"\n"+
					"Position of image of sphere centre due to inner cloak: "+l3p1+"\n"+
					"Position of image of sphere centre due to both cloaks: "+l3p2+"\n"+
					"This image is "+(isVisible(l3p2)?"visible":"not visible")+
					"Sphere 4 centre in simplex #"+cloakI.getLensSimplicialComplex().getIndexOfSimplexContainingPosition(pP4)+"\n"+
					"Position of image of sphere centre due to inner cloak: "+l4p1+"\n"+
					"Position of image of sphere centre due to both cloaks: "+l4p2+"\n"+
					"This image is "+(isVisible(l4p2)?"visible":"not visible")
						);
			

		}
	}


	public static void main(final String[] args)
	{
		(new IdealLensWormholeVisualiserWithLens()).run();
	}
}
