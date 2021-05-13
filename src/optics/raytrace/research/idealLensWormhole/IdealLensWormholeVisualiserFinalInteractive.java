package optics.raytrace.research.idealLensWormhole;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.simplicialComplex.LensType;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.IdealThinLensSurfaceSurfaceCoordinates;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableRelativisticAnyFocusSurfaceCamera;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableFramedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.cameras.PinholeCamera.ExposureCompensationType;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import math.simplicialComplex.Edge;
import math.simplicialComplex.SimplicialComplex;
import optics.DoubleColour;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.surfaces.SurfaceTiling;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.sceneObjects.ParametrisedDisc;
import optics.raytrace.sceneObjects.ParametrisedTriangle;
import optics.raytrace.sceneObjects.Plane;





public class IdealLensWormholeVisualiserFinalInteractive extends NonInteractiveTIMEngine implements ActionListener
{
	/*
	 * Variables for the inside lenses
	 */
	
	//Lens names
	EditableFramedTriangle lens1,lens2,lens3,lens4,outlineLens1,outlineLens2,outlineLens3,outlineLens4;
	
	//focal length of lens 1 and 2 (3 and 4 are not needed as they are the same as 1 and 2)
	private double f1;
	private double f2;
	
	// set the transmission coef of the lenses
	private double lensTrans; 
	
	//principal points of lenses
	private Vector3D pP1;
	private Vector3D pP2;
	private Vector3D pP3;
	private Vector3D pP4;
	private double d;//Separation between the 2nd and 3rd lens
	
	
	private Vector3D nhat;//optical axis direction vector 
	private Vector3D phi0;// phi0Direction
	private Vector3D vert1; // unit vector to vertex 1
	private Vector3D vert2; // unit vector to vertex 2
	private Vector3D vert3; // unit vector to vertex 3
	private Vector3D vert1Neg,vert2Neg,vert3Neg; //unit vector to negative vertex 1,2,3
	
	private double rI1,rI2,rI3,rI4; //radius of inner lenses
	private boolean combineBaseLensWithLens4 = true;
	
	//variable used to calculate lens radius
	private Vector3D h;
	
	//lens centre spheres
	private boolean showPatternedSphere; //shows sphere at principal point of lenses
	private boolean showImage1OfPatternedSphere; //shows the image of the sphere due to the inner cloak
	private boolean showImage2OfPatternedSphere; //show the image of the sphere due to both cloaks
	private double patternedSphereRadius = 0.02; // radius of spheres
	
	private boolean insideLensFrames;//if true shows the frame of the inside lenses
	private boolean outsideLensFrames;//if true shows the frames of the projected lenses outside
	
	
	/*
	 *Variables for nested cloak 
	 */
	
	EditableLensSimplicialComplex cloakI, cloakO, cloakI2, cloakO2, cloakIVirt;
	SimplicialComplex innerCloakFromVirtual;
	// Inside cloak
	private double baseFocalI; //base lens focal length 
	private double h1PI; // Height to lower inner vertex in physical space
	private double h2PI; // Height to upper inner vertex in physical space
	private double hI; //over all height of cloak
	private double rI; //base radius
	private Vector3D topVertex;
	private Vector3D baseVertex;
	
	// Outside cloak
	private double baseFocalO; //base lens focal length 
	private double h1PO; // Height to lower inner vertex in physical space
	private double h2PO; //Height to upper inner vertex in physical space
	private double hO; //over all height of cloak
	private double rO; //base radius


	//cloaks interactive additions
	private boolean alignedCloaks; //check box to show the setup with the inside cloak aligned with the outside cloak
	private boolean frameI; // shows the frame of inner cloak
	private boolean frameO; //shows the frame of the outer cloak
	private boolean showImageOfInnerCloak; //shows the image of the inner cloak
	private LensType lensTypeI, lensTypeO; // define the lens type of the cloaks, usually either ideal thin or none
	
	
	//setting variables for calculations on lens position and focal lengths
	private double z41;
	private double z42;
	private double z43;
	private double z44;
	private double fL1;
	private double fL2;
	private double fL3;
	private double fL4;
	private double z21;
	private double z22;
	private double z23;
	private double z24;
	private double zI;
	
	
	/*
	 * camera
	 */
	
	private double cameraAngle; // the angle at which the camera will face towards (0,0,0), + to go left - to go right 
	private double cameraUpAngle; //define an angle with which the camera will view the origin
	private double cameraFOV; //set the focus and thus zoom of camera	
	private Vector3D cameraRotationAxisDirection;
	private double cameraDistance;
	
	
	/*
	 * ray tracing
	 */
	
	private boolean showTrajectory;
	private Vector3D trajectoryDefaultDirection; //defines the default ray direction
	private Vector3D rayAim; // defines the direction when a position of where to look is inputed
	private Vector3D trajectoryDefaultPos; //defines the manual ray trace position
	private boolean manualRayStart; //defines which starting point should be set for the ray tracing 
	private boolean manualRayDirection;//defines which direction should be set for the ray tracing 
	private double rayAngle;// sets the angle of the ray location, in equal to camera sets it to camera position
	private Vector3D rayPos; //ray trace starting position 
	private Vector3D rayDirection; // raytrace direction
	
	
	/*
	 * Additional interactive variables
	 */
	
	private boolean fourLenses;
	private boolean antiAlignedCloaks;
	private boolean showEquiv;
	private boolean lens;
	private boolean cloak; 
		
	
	public IdealLensWormholeVisualiserFinalInteractive()
	{
		super();
		

		/*
		 * Setting the interactive params
		 */

		//both cloaks and lenses
		cloak = false;
		lens = false;
		frameI = false; // shows the frame of inner cloak
		frameO = false; //shows the frame of the outer cloak
		lensTypeI = LensType.IDEAL_THIN_LENS; //set to none by default
		lensTypeO = LensType.NONE; //set to none by default
		lensTrans = 1; //sets transmission coef of lens to 1 (default)
		nhat = new Vector3D(0,0,1); //optical axis direction vector 
		phi0 = new Vector3D(1,0,0); // phi0Direction
		showImageOfInnerCloak = false;
		showPatternedSphere = false; // at centre of lens in physical space
		showImage1OfPatternedSphere = false;// at centre of lens in virtual space
		showImage2OfPatternedSphere = false; // at centre of lens in virtual space from both cloaks space
		patternedSphereRadius = 0.02; //radius all spheres will have 
		insideLensFrames = false;
		outsideLensFrames = false;
		combineBaseLensWithLens4 = true;
		
		
		
		//Vertices for triangular lenses
		vert1 = new Vector3D(1,0,0);//base vertex 1 for triangular lens
		vert2 = new Vector3D(-0.5,-(Math.sqrt(3.)/2),0);//base vertex 2 for triangular lens
		vert3 = new Vector3D(-0.5,(Math.sqrt(3.)/2),0);//base vertex 3 for triangular lens
		
		vert1Neg = new Vector3D(-1,0,0);//base vertex 1 for triangular lens
		vert2Neg = new Vector3D(0.5,-(Math.sqrt(3.)/2),0);//base vertex 2 for triangular lens
		vert3Neg = new Vector3D(0.5,(Math.sqrt(3.)/2),0);//base vertex 2 for triangular lens
		
		// Four simple lenses to should cutting out of space
		fourLenses = false;	
		
		//Cloak set up 1
		antiAlignedCloaks = false;
		
		//Cloak set up 2
		alignedCloaks = false; // Set the default if function
		
		//Equivalent lenses
		showEquiv = false; 
		
		
		//Camera params
		cameraAngle = 0; //setting the initial viewing angle to be from directly behind the cloak
		cameraUpAngle = 0; //setting the initial viewing angle to be from directly behind the cloak
		cameraDistance = 8;
		cameraFOV = 20; //set the default fov to 20.
		movie = false;//movie mode
		// if movie = true, then the following are relevant:
		numberOfFrames = 10;
		cameraRotationAxisDirection = new Vector3D(0, 0, 1);		
				
		//ray trace
		traceRaysWithTrajectory = false; 
		manualRayStart = false;
		manualRayDirection = false;
		showTrajectory = false;
		trajectoryDefaultPos = new Vector3D(0, 0, -15); //sets the manual starting position of the ray
		trajectoryDefaultDirection = new Vector3D(0,0,1);//sets the manual direction of the ray
 		rayAim = new Vector3D(0, 0, 0);//sets the position the ray should go towards
		rayAngle = 0; //setting the ray angle to the camera angle. 
		
		//Tim engine setup
		renderQuality = RenderQualityEnum.DRAFT;//Set the default render quality		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;// set to true for interactive version
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
		

			
	if(lens)
	{
		d = (2*(f1+f2)*(f2/f1));
		pP1 = new Vector3D(0,2, -1); //Principal point of virtual image of lens 1
		pP2 = new Vector3D(0,2,(f1+f2)-1); //Principal point of virtual image of lens 1
		pP3 = new Vector3D(0,2,(f1+f2+d)-1); //Principal point of virtual image of lens 1
		pP4 = new Vector3D(0,2,(2*f1+2*f2+d)-1); //Principal point of virtual image of lens 1
		
		scene.addSceneObject(new ParametrisedDisc(
		"lens 1",	//description,
		pP1,	// centre,
		nhat,	// normal,
		0.45,	// radius,
		phi0,	// phi0Direction,
		new IdealThinLensSurfaceSurfaceCoordinates(
				new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
				f1,	// focalLength,
				lensTrans,	// transmissionCoefficient
				false	// shadow-throwing
			),	// surface property
		scene,	// parent, 
		studio	// the studio
	));
	
	scene.addSceneObject(new ParametrisedDisc(
		"lens 2",	//description,
		pP2,	// centre,
		nhat,	// normal,
		0.45,	// radius,
		phi0,	// phi0Direction,
		new IdealThinLensSurfaceSurfaceCoordinates(
				new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
				f2,	// focalLength,
				lensTrans,	// transmissionCoefficient
				false	// shadow-throwing
			),	// surface property
		scene,	// parent, 
		studio	// the studio
	));
	
	scene.addSceneObject(new ParametrisedDisc(
		"lens 3",	//description,
		pP3,	// centre,
		nhat,	// normal,
		0.45,	// radius,
		phi0,	// phi0Direction,
		new IdealThinLensSurfaceSurfaceCoordinates(
				new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
				f2,	// focalLength,
				1,	// transmissionCoefficient
				false	// shadow-throwing
			),	// surface property
		scene,	// parent, 
		studio	// the studio
	));
	
	scene.addSceneObject(new ParametrisedDisc(
		"lens 4",	//description,
		pP4,	// centre,
		nhat,	// normal,
		0.45,	// radius,
		phi0,	// phi0Direction,
		new IdealThinLensSurfaceSurfaceCoordinates(
				new Vector2D(0, 0),	// opticalAxisIntersectionCoordinates,
				f1,	// focalLength,
				lensTrans,	// transmissionCoefficient
				false	// shadow-throwing
			),	// surface property
		scene,	// parent, 
		studio	// the studio
	));
		
	}
	
	if(cloak)
	{	
		
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
				new Vector3D(rI, 2, 0)	// baseVertex1
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
				new Vector3D(rO, 2, 0)	// baseVertex1
			);
		scene.addSceneObject(cloakO);
		
		try {
			// make a copy of the simplicial complex representing the inner cloak's physical-space structure...
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
			System.out.println("pos1="+innerCloakImageVertices);
			
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
			System.out.println(innerCloakImage);
		} catch (InconsistencyException e) {
	
			e.printStackTrace();
		}
	
		if(alignedCloaks);
		
		{	
    		d = (2*(f1+f2)*(f2/f1)); //separation along z axis of virtual lenses 2 and 3
    		pP1 = new Vector3D(0,2, 0); //Principal point of virtual image of lens 1
    		pP2 = new Vector3D(0,2,(f1+f2)); //Principal point of virtual image of lens 1
    		pP3 = new Vector3D(0,2,(f1+f2+d)); //Principal point of virtual image of lens 1
    		pP4 = new Vector3D(0,2,(2*f1+2*f2+d)); //Principal point of virtual image of lens 1
    		
    		
			//calcs to find centre of lenses in real space
			z41=(-baseFocalO*(Vector3D.getDistance(pP1,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP1,new Vector3D(0,2,0))));
			z42=(-baseFocalO*(Vector3D.getDistance(pP2,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP2,new Vector3D(0,2,0))));
			z43=(-baseFocalO*(Vector3D.getDistance(pP3,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP3,new Vector3D(0,2,0))));
			z44=(-baseFocalO*(Vector3D.getDistance(pP4,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP4,new Vector3D(0,2,0))));
	
	
			double z51,z52,z53,z54;
			z51=(-baseFocalO*(z41))/(-baseFocalO-(z41));
			z52=(-baseFocalO*(z42))/(-baseFocalO-(z42));
			z53=(-baseFocalO*(z43))/(-baseFocalO-(z43));
			z54=(-baseFocalO*(z44))/(-baseFocalO-(z44));
			
			
			
			z21=(-baseFocalI*z41+z41*(zI) -((zI)*(zI)))/(-baseFocalI+z41-(zI));
			z22=(-baseFocalI*z42+z42*(zI) -((zI)*(zI)))/(-baseFocalI+z42-(zI));
			z23=(-baseFocalI*z43+z43*(zI) -((zI)*(zI)))/(-baseFocalI+z43-(zI));
			z24=(-baseFocalI*z44+z44*(zI) -((zI)*(zI)))/(-baseFocalI+z44-(zI));
			
			Vector3D l1p2,l2p2,l3p2,l4p2;
			l1p2 = new Vector3D(0,2,z21);
			l2p2 = new Vector3D(0,2,z22);
			l3p2 = new Vector3D(0,2,z23);
			l4p2 = new Vector3D(0,2,z24);
			
			//calcs to find focal length in real space
			double f41,f42,f43,f44;
			f41 = (f1*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP1,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP1,new Vector3D(0,2,0))));
			f42 = (f2*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP2,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP2,new Vector3D(0,2,0))));
			f43 = (f2*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP3,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP3,new Vector3D(0,2,0))));
			f44 = (f1*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP4,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP4,new Vector3D(0,2,0))));
			
			fL1= (f41*((baseFocalI)*(baseFocalI)))/((-baseFocalI+z41-zI)*(-baseFocalI+z41-zI));
			fL2= (f42*((baseFocalI)*(baseFocalI)))/((-baseFocalI+z42-zI)*(-baseFocalI+z42-zI));
			fL3= (f43*((baseFocalI)*(baseFocalI)))/((-baseFocalI+z43-zI)*(-baseFocalI+z43-zI));
			fL4= (f44*((baseFocalI)*(baseFocalI)))/((-baseFocalI+z44-zI)*(-baseFocalI+z44-zI));
	
			System.out.println("z51="+z51+" z52="+z52+" z53="+z53+" z54="+z54);
			System.out.println("z41="+z41+" z42="+z42+" z43="+z43+" z44="+z44);
			System.out.println("f1="+fL1+" f2="+fL2+" f3="+fL3+" f4="+fL4);
			System.out.println("z21="+z21+" z22="+z22+" z23="+z23+" z24="+z24);
			
			rI1 = rI*1;
			rI2 = rI*1;
			rI3 = rI*1;
			rI4 = rI*1;
			
	
			
			/*
			 * Adding the lenses
			 */
				lens1 = new EditableFramedTriangle(
					"lens 1",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l1p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2)), // vertex 3
					false,
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l1p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l1p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL1,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.BLUE_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens1);
			
			
				lens2 = new EditableFramedTriangle(
					"lens 2",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l2p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l2p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2)), // vertex 3
					false,
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l2p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l2p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l2p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL2,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.GREEN_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens2);
			

			
			lens3 = new EditableFramedTriangle(
					"lens 3",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l3p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l3p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2)), // vertex 3
					false,	
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l3p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l3p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l3p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL3,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.YELLOW_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens3);
			
			
			
			if(combineBaseLensWithLens4)
			{
				// TODO
				// try to get the face corresponding to the base lens
				EditableParametrisedTriangle baseLensI = (EditableParametrisedTriangle)(cloakI.getSceneObjectContainer().getFirstSceneObjectWithDescription("Face #0", true));
				System.out.println("Base lens? description = "+baseLensI.getDescription());
//				baseLensI.setSurfaceProperty(SurfaceColour.YELLOW_SHINY);
				SurfaceProperty s = baseLensI.getSurfaceProperty();
				System.out.println(" = "+s.getClass());
				IdealThinLensSurface baseLens = (IdealThinLensSurface)(baseLensI.getSurfaceProperty());
				double fB = baseLens.getFocalLength();
				baseLens.setParameters(baseLens.getPrincipalPoint(), baseLens.getOpticalAxisDirectionPos(), 1/(1/fB+1/fL4));
			}
			else
			{
			lens4 = new EditableFramedTriangle(
					"lens 4",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l4p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l4p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2)), // vertex 3
					false,	
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l4p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l4p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l4p2,
							new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
							fL4,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens4);
			}
			
			//adding outlines to the virtual lenses outside lenses
			Vector3D outVert1l1, outVert2l1, outVert3l1,outVert1l2, outVert2l2, outVert3l2,outVert1l3, outVert2l3, outVert3l3,outVert1l4, outVert2l4, outVert3l4;
			
			
			outVert1l1 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1p2))
					);
			outVert1l2 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2p2))
					);
			outVert1l3 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3p2))
					);
			outVert1l4 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4p2))
					);
			
			
			outVert2l1 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1p2)
							)
					);
			outVert2l2 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l2p2)
							)
					);
			outVert2l3 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l3p2)
							)
					);
			outVert2l4 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l4p2)
							)
					);
			
			
			outVert3l1 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI1*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l1p2)
							)
					);
			outVert3l2 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI2*Vector3D.getDistance(l2p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l2p2)
							)
					);
			outVert3l3 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI3*Vector3D.getDistance(l3p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l3p2)
							)
					);
			outVert3l4 = cloakO.getLensSimplicialComplex().mapToOutside(
					0, cloakI.getLensSimplicialComplex().mapToOutsideSpace(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI4*Vector3D.getDistance(l4p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l4p2)
							)
					);
			System.out.println("pos1="+Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1p2)+" pos2="+outVert3l1);
			
			
			outlineLens1 = new EditableFramedTriangle(
					"lens 1",	//description,
					outVert1l1, // vertex 1
					Vector3D.difference(outVert2l1,outVert1l1), // vertex1 to 2
					Vector3D.difference(outVert3l1,outVert1l1), // vertex1 to 3
					false,
					Vector3D.difference(outVert2l1,outVert1l1), // vertex 2
					Vector3D.difference(outVert3l1,outVert1l1), // vertex 3
					frameRadius,
					Transparent.PERFECT,	// surface property
					SurfaceColour.BLUE_SHINY, // colour of frame
					outsideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(outlineLens1);
			
			
			outlineLens2 = new EditableFramedTriangle(
					"lens 2",	//description,
					outVert1l2, // vertex 1
					Vector3D.difference(outVert2l2,outVert1l2), // vertex 2
					Vector3D.difference(outVert3l2,outVert1l2), // vertex 3
					false,
					Vector3D.difference(outVert2l2,outVert1l2), // vertex 2
					Vector3D.difference(outVert3l2,outVert1l2), // vertex 3
					frameRadius,
					Transparent.PERFECT,	// surface property
					SurfaceColour.GREEN_SHINY, // colour of frame
					outsideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(outlineLens2);
			
			outlineLens3 = new EditableFramedTriangle(
					"lens 3",	//description,
					outVert1l3, // vertex 1
					Vector3D.difference(outVert2l3,outVert1l3), // vertex 2
					Vector3D.difference(outVert3l3,outVert1l3), // vertex 3
					false,
					Vector3D.difference(outVert2l3,outVert1l3), // vertex 2
					Vector3D.difference(outVert3l3,outVert1l3), // vertex 3
					frameRadius,
					Transparent.PERFECT,	// surface property
					SurfaceColour.YELLOW_SHINY, // colour of frame
					outsideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(outlineLens3);
			
			outlineLens4 = new EditableFramedTriangle(
					"lens 4",	//description,
					outVert1l4, // vertex 1
					Vector3D.difference(outVert2l4,outVert1l4), // vertex 2
					Vector3D.difference(outVert3l4,outVert1l4), // vertex 3
					false,
					Vector3D.difference(outVert2l4,outVert1l4), // vertex 2
					Vector3D.difference(outVert3l4,outVert1l4), // vertex 3
					frameRadius,
					Transparent.PERFECT,	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					outsideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(outlineLens4);
			
			if(showEquiv)//removes the scene objects and replaces with eqivalent lenses 
			{
				//removes
				scene.removeSceneObject(cloakI);
				scene.removeSceneObject(cloakO);
				scene.removeSceneObject(lens1);
				scene.removeSceneObject(lens2);
				scene.removeSceneObject(lens3);
				if (combineBaseLensWithLens4) {}
				else {
				scene.removeSceneObject(lens4);
				}
				scene.removeSceneObject(outlineLens1);
				scene.removeSceneObject(outlineLens2);
				scene.removeSceneObject(outlineLens3);
				if (combineBaseLensWithLens4) {}
				else {
					scene.removeSceneObject(outlineLens4);;
				}
				
				
				//calc to find the equivalent positions anf focal lenghts.
				z41=(-baseFocalI*(-Vector3D.getDistance(l1p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l1p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(-baseFocalI-(-Vector3D.getDistance(l1p2,new Vector3D(0,2,0)))+(zI));
				z42=(-baseFocalI*(-Vector3D.getDistance(l2p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l2p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(-baseFocalI-(-Vector3D.getDistance(l2p2,new Vector3D(0,2,0)))+(zI));
				z43=(-baseFocalI*(-Vector3D.getDistance(l3p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l3p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(-baseFocalI-(-Vector3D.getDistance(l3p2,new Vector3D(0,2,0)))+(zI));
				z44=(-baseFocalI*(-Vector3D.getDistance(l4p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l4p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(-baseFocalI-(-Vector3D.getDistance(l4p2,new Vector3D(0,2,0)))+(zI));
				
				Vector3D equil1p,equil2p,equil3p,equil4p;
				
				equil1p = new Vector3D(0,2,(-baseFocalO*(z41))/(-baseFocalO-(z41)));
				equil2p = new Vector3D(0,2,(-baseFocalO*(z42))/(-baseFocalO-(z42)));
				equil3p = new Vector3D(0,2,(-baseFocalO*(z43))/(-baseFocalO-(z43)));
				equil4p = new Vector3D(0,2,(-baseFocalO*(z44))/(-baseFocalO-(z44)));
				
				//calc to find the equivalent focal lengths
				double equif1,equif2,equif3,equif4;
				
				f41 = (fL1*(-baseFocalI+z41-zI)*(-baseFocalI+z41-zI))/(baseFocalI*baseFocalI);
				f42 = (fL2*(-baseFocalI+z42-zI)*(-baseFocalI+z42-zI))/(baseFocalI*baseFocalI);
				f43 = (fL3*(-baseFocalI+z43-zI)*(-baseFocalI+z43-zI))/(baseFocalI*baseFocalI);
				f44 = (fL4*(-baseFocalI+z44-zI)*(-baseFocalI+z44-zI))/(baseFocalI*baseFocalI);
								
				
				equif1 = (f41*baseFocalO*baseFocalO)/((-baseFocalO-z41)*(-baseFocalO-z41));
				equif2 = (f42*baseFocalO*baseFocalO)/((-baseFocalO-z42)*(-baseFocalO-z42));
				equif3 = (f43*baseFocalO*baseFocalO)/((-baseFocalO-z43)*(-baseFocalO-z43));
				equif4 = (f44*baseFocalO*baseFocalO)/((-baseFocalO-z44)*(-baseFocalO-z44));
				
				System.out.println("pos1="+equil1p+" pos2="+equil2p+" pos3="+equil3p+" pos4="+equil4p);
				System.out.println("focal1="+equif1+" focal2="+equif2+" focal3="+equif3+" focal4="+equif4);
				
				
				scene.addSceneObject( new EditableFramedTriangle(
						"lens 1",	//description,
						outVert1l1, // vertex 1
						Vector3D.difference(outVert2l1,outVert1l1), // vertex1 to 2
						Vector3D.difference(outVert3l1,outVert1l1), // vertex1 to 3
						false,
						Vector3D.difference(outVert2l1,outVert1l1), // vertex 2
						Vector3D.difference(outVert3l1,outVert1l1), // vertex 3
						frameRadius,
						new IdealThinLensSurfaceSimple(
								equil1p,
								new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
								equif1,	// focalLength,
								lensTrans,	// transmissionCoefficient
								false	// shadow-throwing
							),	// surface property
						SurfaceColour.BLUE_SHINY, // colour of frame
						outsideLensFrames, //showframe
						scene,	// parent, 
						studio	// the studio
						)
				);
				
				
				scene.addSceneObject( new EditableFramedTriangle(
						"lens 2",	//description,
						outVert1l2, // vertex 1
						Vector3D.difference(outVert2l2,outVert1l2), // vertex 2
						Vector3D.difference(outVert3l2,outVert1l2), // vertex 3
						false,
						Vector3D.difference(outVert2l2,outVert1l2), // vertex 2
						Vector3D.difference(outVert3l2,outVert1l2), // vertex 3
						frameRadius,
						new IdealThinLensSurfaceSimple(
								equil2p,
								new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
								equif2,	// focalLength,
								lensTrans,	// transmissionCoefficient
								false	// shadow-throwing
							),	// surface property
						SurfaceColour.GREEN_SHINY, // colour of frame
						outsideLensFrames, //showframe
						scene,	// parent, 
						studio	// the studio
						)
				);
				
				scene.addSceneObject( new EditableFramedTriangle(
						"lens 3",	//description,
						outVert1l3, // vertex 1
						Vector3D.difference(outVert2l3,outVert1l3), // vertex 2
						Vector3D.difference(outVert3l3,outVert1l3), // vertex 3
						false,
						Vector3D.difference(outVert2l3,outVert1l3), // vertex 2
						Vector3D.difference(outVert3l3,outVert1l3), // vertex 3
						frameRadius,
						new IdealThinLensSurfaceSimple(
								equil3p,
								new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
								equif3,	// focalLength,
								lensTrans,	// transmissionCoefficient
								false	// shadow-throwing
							),	// surface property
						SurfaceColour.YELLOW_SHINY, // colour of frame
						outsideLensFrames, //showframe
						scene,	// parent, 
						studio	// the studio
						)
				);
				
				scene.addSceneObject( new EditableFramedTriangle(
						"lens 4",	//description,
						outVert1l4, // vertex 1
						Vector3D.difference(outVert2l4,outVert1l4), // vertex 2
						Vector3D.difference(outVert3l4,outVert1l4), // vertex 3
						false,
						Vector3D.difference(outVert2l4,outVert1l4), // vertex 2
						Vector3D.difference(outVert3l4,outVert1l4), // vertex 3
						frameRadius,
						new IdealThinLensSurfaceSimple(
								equil4p,
								new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
								equif4,	// focalLength,
								lensTrans,	// transmissionCoefficient
								false	// shadow-throwing
							),	// surface property
						SurfaceColour.RED_SHINY, // colour of frame
						outsideLensFrames, //showframe
						scene,	// parent, 
						studio	// the studio
						)
				);
				
			}
			/*
			 * Adding spheres at centre of lenses and showing heir position in virtual space. 
			 */
			

		
			
			// the patterned sphere for lens 1
			EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
					"lens 1 principal point", // description
					l1p2, // centre
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
			Vector3D pP1p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l1p2);
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
					l2p2, // centre
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
			Vector3D pP2p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l2p2);
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
					l3p2, // centre
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
			Vector3D pP3p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l3p2);
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
					l4p2, // centre
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
			Vector3D pP4p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l4p2);
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
		
		if(antiAlignedCloaks)
		{
			
			scene.removeSceneObject(lens1);
			scene.removeSceneObject(lens2);
			scene.removeSceneObject(lens3);
			scene.removeSceneObject(lens4);	
				
			
    		d = (2*(f1+f2)*(f2/f1)); //separation along z axis of virtual lenses 2 and 3
    		pP1 = new Vector3D(0,2, 0.1); //Principal point of virtual image of lens 1
    		pP2 = new Vector3D(0,2,(f1+f2)+0.1); //Principal point of virtual image of lens 1
    		pP3 = new Vector3D(0,2,(f1+f2+d)+0.1); //Principal point of virtual image of lens 1
    		pP4 = new Vector3D(0,2,(2*f1+2*f2+d)+0.1); //Principal point of virtual image of lens 1
    		

    		
    		//calculating positions in the real space from virtual space.
			z41=(-baseFocalO*(Vector3D.getDistance(pP1,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP1,new Vector3D(0,2,0))));
			z42=(-baseFocalO*(Vector3D.getDistance(pP2,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP2,new Vector3D(0,2,0))));
			z43=(-baseFocalO*(Vector3D.getDistance(pP3,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP3,new Vector3D(0,2,0))));
			z44=(-baseFocalO*(Vector3D.getDistance(pP4,new Vector3D(0,2,0))))/(-baseFocalO+(Vector3D.getDistance(pP4,new Vector3D(0,2,0))));

			
			double z51,z52,z53,z54;
			z51=(-baseFocalO*(z41))/(-baseFocalO-(z41));
			z52=(-baseFocalO*(z42))/(-baseFocalO-(z42));
			z53=(-baseFocalO*(z43))/(-baseFocalO-(z43));
			z54=(-baseFocalO*(z44))/(-baseFocalO-(z44));
			
			
			
			z21=(baseFocalI*z41+z41*(zI) -((zI)*(zI)))/(baseFocalI+z41-(zI));
			z22=(baseFocalI*z42+z42*(zI) -((zI)*(zI)))/(baseFocalI+z42-(zI));
			z23=(baseFocalI*z43+z43*(zI) -((zI)*(zI)))/(baseFocalI+z43-(zI));
			z24=(baseFocalI*z44+z44*(zI) -((zI)*(zI)))/(baseFocalI+z44-(zI));
			
			Vector3D l1_p2,l2_p2,l3_p2,l4_p2;
			l1_p2 = new Vector3D(0,2,z21);
			l2_p2 = new Vector3D(0,2,z22);
			l3_p2 = new Vector3D(0,2,z23);
			l4_p2 = new Vector3D(0,2,z24);
			
			//calculating focal lengths in real space from virtual
			double f41,f42,f43,f44;
			f41 = (f1*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP1,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP1,new Vector3D(0,2,0))));
			f42 = (f2*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP2,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP2,new Vector3D(0,2,0))));
			f43 = (f2*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP3,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP3,new Vector3D(0,2,0))));
			f44 = (f1*baseFocalO*baseFocalO)/((-baseFocalO+Vector3D.getDistance(pP4,new Vector3D(0,2,0)))*(-baseFocalO+Vector3D.getDistance(pP4,new Vector3D(0,2,0))));
			
			fL1= (f41*((baseFocalI)*(baseFocalI)))/((baseFocalI+z41-zI)*(baseFocalI+z41-zI));
			fL2= (f42*((baseFocalI)*(baseFocalI)))/((baseFocalI+z42-zI)*(baseFocalI+z42-zI));
			fL3= (f43*((baseFocalI)*(baseFocalI)))/((baseFocalI+z43-zI)*(baseFocalI+z43-zI));
			fL4= (f44*((baseFocalI)*(baseFocalI)))/((baseFocalI+z44-zI)*(baseFocalI+z44-zI));

			System.out.println("z51="+z51+" z52="+z52+" z53="+z53+" z54="+z54);
			System.out.println("z41="+z41+" z42="+z42+" z43="+z43+" z44="+z44);
			System.out.println("f1="+fL1+" f2="+fL2+" f3="+fL3+" f4="+fL4);
			System.out.println("z21="+z21+" z22="+z22+" z23="+z23+" z24="+z24);
			

			/*
			 * inserting lenses into the cloaks
			 */
			
			lens1 = new EditableFramedTriangle(
					"lens 1",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1_p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l1_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1_p2)), // vertex 3
					false,
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l1_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l1_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l1_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l1_p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l1_p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL1,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens1);
			
			
				lens2 = new EditableFramedTriangle(
					"lens 2",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2_p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l2_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l2_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2_p2)), // vertex 3
					false,
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l2_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l2_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l2_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l2_p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l2_p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL2,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens2);
			
			lens3 = new EditableFramedTriangle(
					"lens 3",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3_p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l3_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l3_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3_p2)), // vertex 3
					false,	
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l3_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l3_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l3_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l3_p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l3_p2,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							fL3,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens3);
			
			
			
			if(combineBaseLensWithLens4)
			{
				// TODO
				// try to get the face corresponding to the base lens
				EditableParametrisedTriangle baseLensI = (EditableParametrisedTriangle)(cloakI.getSceneObjectContainer().getFirstSceneObjectWithDescription("Face #0", true));
				System.out.println("Base lens? description = "+baseLensI.getDescription());
				IdealThinLensSurface baseLens = (IdealThinLensSurface)(baseLensI.getSurfaceProperty());
				double fB = baseLens.getFocalLength();
				baseLens.setParameters(baseLens.getPrincipalPoint(), baseLens.getOpticalAxisDirectionPos(), 1/(1/fB+1/fL4));
			}
			else
			{

			lens4 = new EditableFramedTriangle(
					"lens 4",	//description,
					Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4_p2), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l4_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l4_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4_p2)), // vertex 3
					false,	
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert2), l4_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4_p2)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert3), l4_p2),
							Vector3D.sum(Vector3D.scalarTimesVector3D((rI*Vector3D.getDistance(l4_p2, h)/Vector3D.getDistance(baseVertex, h) ),vert1), l4_p2)), // vertex 3
					frameRadius,
					new IdealThinLensSurfaceSimple(
							l4_p2,
							new Vector3D(0, 0, 1),	// opticalAxisIntersectionCoordinates,
							fL4,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					SurfaceColour.RED_SHINY, // colour of frame
					insideLensFrames, //showframe
					scene,	// parent, 
					studio	// the studio
					);
			scene.addSceneObject(lens4);
			}
		
		
		if(showEquiv) //if true, removes all previous components and replaces them with equivalent lenses
		{
			scene.removeSceneObject(cloakI);
			scene.removeSceneObject(cloakO);
			scene.removeSceneObject(lens1);
			scene.removeSceneObject(lens2);
			scene.removeSceneObject(lens3);
			if (combineBaseLensWithLens4) {}
			else {
			scene.removeSceneObject(lens4);
			}
			
			//position calcs
			z41=(baseFocalI*(-Vector3D.getDistance(l1_p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l1_p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(baseFocalI-(-Vector3D.getDistance(l1_p2,new Vector3D(0,2,0)))+(zI));
			z42=(baseFocalI*(-Vector3D.getDistance(l2_p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l2_p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(baseFocalI-(-Vector3D.getDistance(l2_p2,new Vector3D(0,2,0)))+(zI));
			z43=(baseFocalI*(-Vector3D.getDistance(l3_p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l3_p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(baseFocalI-(-Vector3D.getDistance(l3_p2,new Vector3D(0,2,0)))+(zI));
			z44=(baseFocalI*(-Vector3D.getDistance(l4_p2,new Vector3D(0,2,0)))-(-Vector3D.getDistance(l4_p2,new Vector3D(0,2,0))*zI)+(zI)*(zI))/(baseFocalI-(-Vector3D.getDistance(l4_p2,new Vector3D(0,2,0)))+(zI));
			
			Vector3D equil1p,equil2p,equil3p,equil4p;
			
			equil1p = new Vector3D(0,2,(-baseFocalO*(z41))/(-baseFocalO-(z41)));
			equil2p = new Vector3D(0,2,(-baseFocalO*(z42))/(-baseFocalO-(z42)));
			equil3p = new Vector3D(0,2,(-baseFocalO*(z43))/(-baseFocalO-(z43)));
			equil4p = new Vector3D(0,2,(-baseFocalO*(z44))/(-baseFocalO-(z44)));
			
			//focal length clacs
			double equif1,equif2,equif3,equif4;
			
			f41 = (fL1*(baseFocalI+z41-zI)*(baseFocalI+z41-zI))/(baseFocalI*baseFocalI);
			f42 = (fL2*(baseFocalI+z42-zI)*(baseFocalI+z42-zI))/(baseFocalI*baseFocalI);
			f43 = (fL3*(baseFocalI+z43-zI)*(baseFocalI+z43-zI))/(baseFocalI*baseFocalI);
			f44 = (fL4*(baseFocalI+z44-zI)*(baseFocalI+z44-zI))/(baseFocalI*baseFocalI);
							
			
			equif1 = (f41*baseFocalO*baseFocalO)/((-baseFocalO-z41)*(-baseFocalO-z41));
			equif2 = (f42*baseFocalO*baseFocalO)/((-baseFocalO-z42)*(-baseFocalO-z42));
			equif3 = (f43*baseFocalO*baseFocalO)/((-baseFocalO-z43)*(-baseFocalO-z43));
			equif4 = (f44*baseFocalO*baseFocalO)/((-baseFocalO-z44)*(-baseFocalO-z44));
			
			System.out.println("pos1="+equil1p+" pos2="+equil2p+" pos3="+equil3p+" pos4="+equil4p);
			System.out.println("focal1="+equif1+" focal2="+equif2+" focal3="+equif3+" focal4="+equif4);
			
			
			//add equivalent triangular lenses
			
			scene.addSceneObject( new ParametrisedTriangle(
					"Equivalent lens 1", //description
					Vector3D.sum(Vector3D.scalarTimesVector3D(-0.695,vert1), 
							equil1p), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.695,vert2), equil1p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.695,vert1), equil1p)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.695,vert3), equil1p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.695,vert1), equil1p)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							equil1p,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							equif1,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject( new ParametrisedTriangle(
					"Equivalent lens 2", //description
					Vector3D.sum(Vector3D.scalarTimesVector3D(-0.771,vert1), 
							equil2p), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.771,vert2), equil2p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.771,vert1), equil2p)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.771,vert3), equil2p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.771,vert1), equil2p)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							equil2p,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							equif2,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject( new ParametrisedTriangle(
					"Equivalent lens 3", //description
					Vector3D.sum(Vector3D.scalarTimesVector3D(-0.924,vert1), 
							equil3p), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.924,vert2), equil3p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.924,vert1), equil3p)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.924,vert3), equil3p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-0.924,vert1), equil3p)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							equil3p,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							equif3,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
			scene.addSceneObject( new ParametrisedTriangle(
					"Equivalent lens 4", //description
					Vector3D.sum(Vector3D.scalarTimesVector3D(-1,vert1), 
							equil4p), // vertex 1
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-1,vert2), equil4p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-1,vert1), equil4p)), // vertex 2
					Vector3D.difference(
							Vector3D.sum(Vector3D.scalarTimesVector3D(-1,vert3), equil4p),
							Vector3D.sum(Vector3D.scalarTimesVector3D(-1,vert1), equil4p)), // vertex 3
					false,					
					new IdealThinLensSurfaceSimple(
							equil4p,
							new Vector3D(0, 0,1),	// opticalAxisIntersectionCoordinates,
							equif4,	// focalLength,
							lensTrans,	// transmissionCoefficient
							false	// shadow-throwing
						),	// surface property
					scene,	// parent, 
					studio	// the studio
					));
			
		}
		/*
		 * Adding spheres at centre of lenses and showing heir position in virtual space. 
		 */
					
	
		
		// the patterned sphere for lens 1
		EditableScaledParametrisedSphere patternedSphere1 = new EditableScaledParametrisedSphere(
				"lens 1 principal point", // description
				l1_p2, // centre
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
		Vector3D pP1p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l1_p2);
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
				l2_p2, // centre
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
		Vector3D pP2p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l2_p2);
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
				l3_p2, // centre
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
		Vector3D pP3p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l3_p2);
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
				l4_p2, // centre
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
		Vector3D pP4p1 = cloakI.getLensSimplicialComplex().mapToOutsideSpace(l4_p2);
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
		
		
		
	
		
	}
	
	


	
	/*
	 * Setting the camera
	 */	
	
	int
	quality = 2,	// 1 = normal, 2 = good, 4 = great
	pixelsX = 640*quality,
	pixelsY = 480*quality;



	Vector3D cameraDirection = new Vector3D(-Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), -Math.sin(Math.toRadians(cameraUpAngle)), Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));
	Vector3D cameraApertureCentre	= new Vector3D(cameraDistance*Math.sin(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)), 2+cameraDistance*Math.sin(Math.toRadians(cameraUpAngle)), -1.5-cameraDistance*Math.cos(Math.toRadians(cameraAngle))*Math.cos(Math.toRadians(cameraUpAngle)));	
			
	

		
		if(movie)
		{
			cameraViewCentre = new Vector3D(0,2,0);
			// define the azimuthal angle phi that parametrises the circle
			double phi = 0.1+360*frame/numberOfFrames;
			//double FOV = 10+360*frame/numberOfFrames;
			Vector3D cameraApertureCentreMovie;
			System.out.println("LensCloakVisualiser::populateStudio: phi="+phi);
			
			// finally, calculate the view direction
			cameraViewDirection = new Vector3D(-Math.sin(Math.toRadians(phi)), 0, Math.cos(Math.toRadians(phi)));
			cameraViewCentre = new Vector3D(0,2,0);
			// define the azimuthal angle phi that parametrises the circle
			cameraApertureCentreMovie = new Vector3D(cameraDistance*Math.sin(Math.toRadians(phi))*Math.cos(Math.toRadians(cameraUpAngle)), 2+cameraDistance*Math.sin(Math.toRadians(cameraUpAngle)), -1.5-cameraDistance*Math.cos(Math.toRadians(phi))*Math.cos(Math.toRadians(cameraUpAngle)));	
			
			
			EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
				"camera",
				cameraApertureCentreMovie,	// centre of aperture
				cameraViewDirection,	// viewDirection
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
		}
		else
		{	EditableRelativisticAnyFocusSurfaceCamera camera = new EditableRelativisticAnyFocusSurfaceCamera(
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
			
		}
		
		

		/*
		 * setting the ray tracing
		 */
		if(showTrajectory)
		{
			if(manualRayStart)
			{ rayPos = trajectoryDefaultPos;
			
			}
			else
			{ rayPos = new Vector3D(cameraDistance*Math.sin(Math.toRadians(rayAngle)), 2, -cameraDistance*Math.cos(Math.toRadians(rayAngle)));//sets the 'automatic' position of the ray ;	
			}
			if(manualRayDirection)
			{ rayDirection = trajectoryDefaultDirection;
			}
			else
			{RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
			rayAim = i.p;
			rayDirection = Vector3D.difference(rayAim, rayPos);	
					
			}	
			
		
			
			double frameRadius = 0.005;//radius of ray
			
					// do the tracing of rays with trajectory
			scene.addSceneObject(
					new EditableRayTrajectory(
							"light-ray trajectory",	// description
							rayPos,	// startPoint
							0,	// startTime
							rayDirection,	// startDirection
							0.005,	// rayRadius
							SurfaceColourLightSourceIndependent.GREEN,	// surfaceProperty
							100,	// maxTraceLevel
							true,	// reportToConsole
							scene,	// parent
							studio
							)
					);

			studio.setScene(scene);

			// trace the rays with trajectory through the scene
			studio.traceRaysWithTrajectory();
			
			//remove all the objects and replace with semi transparent ones
			scene.removeSceneObject(cloakI);
			scene.removeSceneObject(cloakO);
			scene.removeSceneObject(lens1);
			scene.removeSceneObject(lens2);
			scene.removeSceneObject(lens3);
			if (combineBaseLensWithLens4) {}
				else {
				scene.removeSceneObject(lens4);
				}
			
			
			// create the inner cloak; first create a lens-simplicial complex...
			cloakI2 = new EditableLensSimplicialComplex(
					"Inner Abyss cloak",	// description
					scene,	// parent
					studio
				);
			// ... and initialise it as an ideal-lens cloak
			cloakI2.setLensTypeRepresentingFace(LensType.NONE);
			cloakI2.setShowStructureP(false);
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
					new Vector3D(rI, 2, 0)	// baseVertex1
				);
			scene.addSceneObject(cloakI2);
			
			// create the outer cloak; first create a lens-simplicial complex...
			cloakO2 = new EditableLensSimplicialComplex(
					"outer Abyss cloak",	// description
					scene,	// parent
					studio
				);
			// ... and initialise it as an ideal-lens cloak  IDEAL_THIN_LENS
			cloakO2.setLensTypeRepresentingFace(LensType.NONE);
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
					new Vector3D(rO, 2, 0)	// baseVertex1
				);
			scene.addSceneObject(cloakO2);
			
			
		}
		
		/*
		 * adding Tim(s)
		 */
		scene.addSceneObject(new EditableTimHead(
				"Tim's head",	// description
				new Vector3D(0, 2, 10),	// centre, was (0, 0, 13)
				0.2,	// radius
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(0, 1, 0),	// top direction
				new Vector3D(1, 0, 0),	// right direction
				scene,	// parent
				studio
			));
//		scene.addSceneObject(new EditableTimHead(
//				"Tim's head",	// description
//				new Vector3D(0, 2, -10),	// centre, was (0, 0, 13)
//				0.2,	// radius
//				new Vector3D(0, 0, 1),	// front direction
//				new Vector3D(0, 1, 0),	// top direction
//				new Vector3D(-1, 0, 0),	// right direction
//				scene,	// parent
//				studio
//			));
		
	
			
				
				
		
		studio.setScene(scene);	
		studio.setLights(LightSource.getStandardLightsFromBehind());
}
	
    public enum ParametersInitialisationType
    {
            JUST_TIM("No cloaks or lenses"),
            LENSES_AND_TIM("shows 4 lenses and Tim behind them"),
            CLOAK_ALIGNED("Shows the cloaked setup with the inner cloak aligned"),
            CLOAK_ANTI_ALIGNED("Shows the cloaked setup with the inner cloak facing the opposite way as the outer");

           
            private String description;
            private ParametersInitialisationType(String description) {this.description = description;}     
            @Override
            public String toString() {return description;}
    }
	


	private LabelledDoublePanel f1Panel, f2Panel,cameraAnglePanel, lensTransPanel, cameraZoomPanel, baseLensFIPanel, baseLensFOPanel, patternedSphereRadiusPanel,rayAnglePanel,
	cameraUpAnglePanel,cameraDistancePanel;
	private IntPanel numberOfFramesPanel,firstFramePanel,lastFramePanel;
	private JCheckBox  alignedCloaksCheck, frameICheck, frameOCheck, showImageOfInnerCloakCheckBox, showPatternedSphereCheckBox, showImage1OfPatternedSphereCheckBox, showImage2OfPatternedSphereCheckBox,showTrajectoryPanel,
	manualRayStartCheckBox,manualRayDirectionCheckBox, fourLensesCheckBox, antiAlignedCloaksCheckBox,movieCheckBox,showEquivCheckBox,lensCheckBox,cloakCheckBox, outsideLensFramesCheckBox, insideLensFramesCheckBox;	
	private JTextArea rayLastClickTextArea;
	private JButton rayLastClickInfo;
	private JComboBox<LensType> lensTypeIComboBox, lensTypeOComboBox;
	private JComboBox<ParametersInitialisationType> parametersInitialisationComboBox;
	private LabelledVector3DPanel trajectoryDefaultPosPanel, trajectoryDefaultDirectionPanel, rayAimPanel, cameraRotationAxisDirectionPanel;
	
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		
		
		  parametersInitialisationComboBox = new JComboBox<ParametersInitialisationType>(ParametersInitialisationType.values());
          parametersInitialisationComboBox.setSelectedItem(this);
          parametersInitialisationComboBox.addActionListener(this);
          interactiveControlPanel.add(parametersInitialisationComboBox, "span");
          
          lensCheckBox = new JCheckBox("Shows lens set up only");
          lensCheckBox.setSelected(lens);
  	
          cloakCheckBox = new JCheckBox("Shows cloaks set up");
          cloakCheckBox.setSelected(cloak);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");
		
		

		
		fourLensesCheckBox = new JCheckBox("Demonstrates how 4 selected lenses cut out space");
		fourLensesCheckBox.setSelected(fourLenses);
		
		antiAlignedCloaksCheckBox = new JCheckBox("Show the inner cloak anti-aligned with the outer cloak");
		antiAlignedCloaksCheckBox.setSelected(antiAlignedCloaks);
		
		alignedCloaksCheck = new JCheckBox("Show the inner cloak aligned with the outer cloak");
		alignedCloaksCheck.setSelected(alignedCloaks);
		
		JPanel cloaksPanel = new JPanel();
		cloaksPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Cloaks", cloaksPanel);

		
		// inner Abyss cloak
		JPanel innerLensCloakPanel = new JPanel();
		innerLensCloakPanel.setLayout(new MigLayout("insets 0"));
		innerLensCloakPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Inner cloak"));
		cloaksPanel.add(innerLensCloakPanel, "wrap");
		
		
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
		
		showEquivCheckBox = new JCheckBox("Shows the equivalent lens set up");
		showEquivCheckBox.setSelected(showEquiv);
		lensPanel.add(showEquivCheckBox,"span");
		
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
		
		insideLensFramesCheckBox = new JCheckBox("Show the outline of inside lenses");
		insideLensFramesCheckBox.setSelected(insideLensFrames);
		lensPanel.add(insideLensFramesCheckBox, "span");
		
		outsideLensFramesCheckBox = new JCheckBox("Show the outline of projected lenses");
		outsideLensFramesCheckBox.setSelected(outsideLensFrames);
		lensPanel.add(outsideLensFramesCheckBox, "span");
		


		
		
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
		
		cameraZoomPanel = new LabelledDoublePanel("FOV of camera. Default = 80, decrease to 'zoom'");
		cameraZoomPanel.setNumber(cameraFOV);
		panel.add(cameraZoomPanel, "span");
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		panel.add(cameraDistancePanel, "span");
		
		JPanel moviePanel = new JPanel();
		moviePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Movie"));
		moviePanel.setLayout(new MigLayout("insets 0"));
		panel.add(moviePanel, "span");

		movieCheckBox = new JCheckBox("Create movie");
		movieCheckBox.setSelected(movie);
		moviePanel.add(movieCheckBox, "span");
		
		cameraRotationAxisDirectionPanel = new LabelledVector3DPanel("Direction of rotation axis");
		cameraRotationAxisDirectionPanel.setVector3D(cameraRotationAxisDirection);
		moviePanel.add(cameraRotationAxisDirectionPanel, "span");
		
		numberOfFramesPanel = new IntPanel();
		numberOfFramesPanel.setNumber(numberOfFrames);
		
		firstFramePanel = new IntPanel();
		firstFramePanel.setNumber(firstFrame);
		
		lastFramePanel = new IntPanel();
		lastFramePanel.setNumber(lastFrame);

		moviePanel.add(GUIBitsAndBobs.makeRow("Calculate frames", firstFramePanel, "to", lastFramePanel, "out of", numberOfFramesPanel), "wrap");
		
		
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
		
		rayLastClickTextArea = new JTextArea(2, 40);
		JScrollPane scrollPane = new JScrollPane(rayLastClickTextArea); 
		rayLastClickTextArea.setEditable(false);
		rayLastClickTextArea.setText("Click on the aim ray button set the aim of the light ray");
		rayLastClickInfo = new JButton("Aim ray");
		rayLastClickInfo.addActionListener(this);
		rayTracePanel.add(GUIBitsAndBobs.makeRow(scrollPane, rayLastClickInfo), "span");
	
		 
		
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
		lens = lensCheckBox.isSelected();
		cloak = cloakCheckBox.isSelected();
		fourLenses = fourLensesCheckBox.isSelected();
		alignedCloaks = alignedCloaksCheck.isSelected();
		antiAlignedCloaks = antiAlignedCloaksCheckBox.isSelected();
		frameI = frameICheck.isSelected();
		frameO = frameOCheck.isSelected();		
		baseFocalI = baseLensFIPanel.getNumber();
		baseFocalO = baseLensFOPanel.getNumber();
		showImageOfInnerCloak = showImageOfInnerCloakCheckBox.isSelected();
		lensTypeO = (LensType)(lensTypeOComboBox.getSelectedItem());
		lensTypeI = (LensType)(lensTypeIComboBox.getSelectedItem());
		outsideLensFrames = outsideLensFramesCheckBox.isSelected();
		insideLensFrames = insideLensFramesCheckBox.isSelected();

		

		

		
		//camera
		cameraAngle = cameraAnglePanel.getNumber();
		cameraUpAngle = cameraUpAnglePanel.getNumber();
		cameraFOV = cameraZoomPanel.getNumber();
		cameraDistance = cameraDistancePanel.getNumber();
		
		movie = movieCheckBox.isSelected();
		cameraRotationAxisDirection = cameraRotationAxisDirectionPanel.getVector3D();
		numberOfFrames = numberOfFramesPanel.getNumber();
		firstFrame = firstFramePanel.getNumber();
		lastFrame = lastFramePanel.getNumber();
		
		//raytrace
		

		
		showTrajectory = showTrajectoryPanel.isSelected();
		
		rayAngle = rayAnglePanel.getNumber(); //angle

		
		
		manualRayStart = manualRayStartCheckBox.isSelected(); //checkbox to set to manual position
		trajectoryDefaultPos = trajectoryDefaultPosPanel.getVector3D();//manual position of beam

		manualRayDirection = manualRayDirectionCheckBox.isSelected(); //checkbox to set to manual direction
		trajectoryDefaultDirection = trajectoryDefaultDirectionPanel.getVector3D();//manual direction of beam
		
	
		showEquiv = showEquivCheckBox.isSelected();
		patternedSphereRadius = patternedSphereRadiusPanel.getNumber();
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		lensTrans = lensTransPanel.getNumber();
		showPatternedSphere = showPatternedSphereCheckBox.isSelected();
		showImage1OfPatternedSphere = showImage1OfPatternedSphereCheckBox.isSelected();
		showImage2OfPatternedSphere = showImage2OfPatternedSphereCheckBox.isSelected();
	}
	
	
	
	
    void initParameters(ParametersInitialisationType p)
    {
            switch(p)
            {
            case JUST_TIM: 
            		lensCheckBox.setSelected(false);
            		cloakCheckBox.setSelected(false); 
                    break;
            case LENSES_AND_TIM:
            		f1Panel.setNumber(1);
            		f2Panel.setNumber(1);

            		
            		lensCheckBox.setSelected(true);
            		cloakCheckBox.setSelected(false); 
                    break;
            case CLOAK_ALIGNED:

	        		
            		baseLensFOPanel.setNumber(1.17);
            		baseLensFIPanel.setNumber(0.12);
            		f1Panel.setNumber(0.5);
            		f2Panel.setNumber(0.5);
            		hO = 3;//over all height of cloak
            		h1PO = 0.7; // Height to lower inner vertex in physical space
            		h2PO = 0.9; //Height to upper inner vertex in physical space
            		zI = -1.6537095;//-1.527;//base z position
            		hI = 0.4;//0.573;//0.391; //over all height of cloak
            		h1PI = 0.9; // Height to lower inner vertex in physical space
            		h2PI = 0.95;// Height to upper inner vertex in physical space
            		rI = 0.3; //base radius 2+zI;//
            		cameraAnglePanel.setNumber(0.1);
            		baseVertex = new Vector3D(0, 2, zI);
            		topVertex = new Vector3D(0,2,zI-hI);
            		h = Vector3D.sum(Vector3D.scalarTimesVector3D(h1PI,Vector3D.difference(topVertex,baseVertex)),baseVertex);//calc used for lens radius
            		rO = hO/2; //base radius
 
            		lensCheckBox.setSelected(false);
            		cloakCheckBox.setSelected(true);
	        		antiAlignedCloaksCheckBox.setSelected(false);
	        		alignedCloaksCheck.setSelected(true);
	        		
                    break;
            case CLOAK_ANTI_ALIGNED:
	        		baseLensFOPanel.setNumber(1.25);
	        		baseLensFIPanel.setNumber(0.04);
	        		f1Panel.setNumber(0.5);
	        		f2Panel.setNumber(0.5);
	        		hO = 4;//over all height of cloak
	        		h1PO = 0.8; // Height to lower inner vertex in physical space
	        		h2PO = 0.9; //Height to upper inner vertex in physical space
	        		zI = -1.636;//base z position
	        		hI = 0.215; //over all height of cloak
	        		h1PI = 0.4186;// Height to lower inner vertex in physical space
	        		h2PI = 0.6558;// Height to upper inner vertex in physical space
	        		rI = 0.438; //base radius
	        		cameraAnglePanel.setNumber(0.1);
	        		baseVertex = new Vector3D(0, 2, zI);
	        		topVertex = new Vector3D(0,2,zI+hI);
	        		
	        		h = Vector3D.sum(Vector3D.scalarTimesVector3D(h1PI,Vector3D.difference(topVertex,baseVertex)),baseVertex);//calc used for lens radius
	        		 		
	        		
	        		rO = hO/Math.sqrt(2.); //base radius
	
	        		lensCheckBox.setSelected(false);
	        		cloakCheckBox.setSelected(true); 
	        		antiAlignedCloaksCheckBox.setSelected(true);
	        		alignedCloaksCheck.setSelected(false);


            default:
                    // do nothing
            }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
            super.actionPerformed(e);
           
            if(e.getSource().equals(parametersInitialisationComboBox))
            {
                    initParameters((ParametersInitialisationType)(parametersInitialisationComboBox.getSelectedItem()));
                   
                    parametersInitialisationComboBox.setSelectedItem(this);
            }
            if(e.getSource().equals(rayLastClickInfo))
    		{
            	RaySceneObjectIntersection i = raytracingImageCanvas.getLastClickIntersection();
  
            	rayLastClickTextArea.setText("Ray aiming at "+i.p)
    				;

    		}

    }
	
	


	public static void main(final String[] args)
	{
		(new IdealLensWormholeVisualiserFinalInteractive()).run();
	}
}





