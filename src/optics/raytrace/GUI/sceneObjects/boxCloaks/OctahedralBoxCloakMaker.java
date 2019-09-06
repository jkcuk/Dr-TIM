package optics.raytrace.GUI.sceneObjects.boxCloaks;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableTriangle;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

/**
 * @author johannes
 * 
 * A BoxCloakMaker for the octahedral cloak, i.e. our first affine cloak
 */
public class OctahedralBoxCloakMaker extends CloakMaker
{
	private static final long serialVersionUID = -7558230672180409470L;

	// the direction vectors, i.e. the vectors from the centre of the cube to the centre of the different faces
	protected Vector3D front, back, right, left, top, bottom;
	
	// the vertices of the cube
	protected Vector3D trfCubeVertex, trbCubeVertex, tlfCubeVertex, tlbCubeVertex, brfCubeVertex, brbCubeVertex, blfCubeVertex, blbCubeVertex;
	
	// the vertices of the central octahedron (for the cubic cloak)
	protected Vector3D frontOctahedronVertex, backOctahedronVertex, rightOctahedronVertex, leftOctahedronVertex, topOctahedronVertex, bottomOctahedronVertex;
	
	
	//
	// constructors
	//
	
	public OctahedralBoxCloakMaker(EditableBoxCloak boxCloak)
	{
		super(boxCloak);
	}
	
	
	
	@Override
	public void addSceneObjects()
	{
		// calculate the direction vectors with full sidelength length
		front = boxCloak.getFrontDirection().getWithLength(boxCloak.getSideLength());	// centre to centre of front face
		back = boxCloak.getFrontDirection().getWithLength(-boxCloak.getSideLength());	// centre to centre of back face
		right = boxCloak.getRightDirection().getWithLength(boxCloak.getSideLength());	// centre to centre of right face
		left = boxCloak.getRightDirection().getWithLength(-boxCloak.getSideLength());	// centre to centre of left face
		top = boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength());	// centre to centre of top face
		bottom = boxCloak.getTopDirection().getWithLength(-boxCloak.getSideLength());	// centre to centre of bottom face		
		
		// calculate the direction vectors with half sidelength length
		Vector3D front2, back2, right2, left2, top2, bottom2;
		front2 = boxCloak.getFrontDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of front face
		back2 = boxCloak.getFrontDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of back face
		right2 = boxCloak.getRightDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of right face
		left2 = boxCloak.getRightDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of left face
		top2 = boxCloak.getTopDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of top face
		bottom2 = boxCloak.getTopDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of bottom face
				
		// pre-calculate the corners of the cube
		trfCubeVertex = Vector3D.sum(boxCloak.getCentre(), top2, right2, front2); // top right front
		trbCubeVertex = Vector3D.sum(boxCloak.getCentre(), top2, right2, back2);	// top right back
		tlfCubeVertex = Vector3D.sum(boxCloak.getCentre(), top2, left2, front2); // top left front
		tlbCubeVertex = Vector3D.sum(boxCloak.getCentre(), top2, left2, back2);	// top left back
		brfCubeVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, right2, front2); // bottom right front
		brbCubeVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, right2, back2);	// bottom right back
		blfCubeVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, left2, front2); // bottom left front
		blbCubeVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, left2, back2);	// bottom left back

		// pre-calculate the vertices of the central octahedron
		frontOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), front2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		backOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), back2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		rightOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), right2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		leftOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), left2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		topOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), top2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		bottomOctahedronVertex = Vector3D.sum(boxCloak.getCentre(), bottom2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));

		// calculate the windows and the frames
		addWindowsForOctahedralCloak();
		addFramesForCubicCloak();
	}

	
	
	/**
	 * We have divided up all the surfaces that make up the cubic cloak 
	 * (apart from the surfaces forming the "star" into the corners and the central
	 * octahedron) into those behind the different exterior faces.
	 * This method adds the surfaces behind one particular exterior face.
	 * @param faceName
	 * @param cloakCentre2FaceCentre
	 * @param right
	 * @param up
	 */
	private void addInterfacesForCubicCloakFace(String faceName, Vector3D cloakCentre2FaceCentre, Vector3D right, Vector3D up)
	{
		// parameters for outside CLAs
		double eta1 = (boxCloak.getInnerVolumeSizeFactorP()-1)/(boxCloak.getInnerVolumeSizeFactorEM()-1);	// boxCloak.getAPrime()/a;
		
		// parameters for the "roof" of the outside pyramids (deltax2 = 0)
		double eta2 = (boxCloak.getInnerVolumeSizeFactorP()*(boxCloak.getInnerVolumeSizeFactorEM()-1)*(boxCloak.getInnerVolumeSizeFactorP()-2))/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-1)*(boxCloak.getInnerVolumeSizeFactorEM()-2)); // (a*(4*boxCloak.getAPrime()*boxCloak.getAPrime()-1))/(boxCloak.getAPrime()*(4*a*a-1));
		double deltay2 = ((boxCloak.getInnerVolumeSizeFactorEM()-boxCloak.getInnerVolumeSizeFactorP())*(boxCloak.getInnerVolumeSizeFactorP()-boxCloak.getInnerVolumeSizeFactorEM()))/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-1)*(boxCloak.getInnerVolumeSizeFactorEM()-2));	 // (2*(a*a-2*a*boxCloak.getAPrime()+boxCloak.getAPrime()*boxCloak.getAPrime()))/(boxCloak.getAPrime()*(4*a*a-1));
		
		// the centre of the exterior face
		Vector3D faceCentre = Vector3D.sum(boxCloak.getCentre(), cloakCentre2FaceCentre);
		
		// the tip of the pyramid
		Vector3D pyramidTip = Vector3D.sum(boxCloak.getCentre(), cloakCentre2FaceCentre.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		
		// the corners of the exterior face
		Vector3D
			bottomLeftCorner = Vector3D.sum(faceCentre, right.getProductWith(-1), up.getProductWith(-1)),
			bottomRightCorner = Vector3D.sum(faceCentre, right, up.getProductWith(-1)),
			topLeftCorner = Vector3D.sum(faceCentre, right.getProductWith(-1), up),
			topRightCorner = Vector3D.sum(faceCentre, right, up);
						
		// the exterior CLAs
		GCLAsWithApertures window1 = new GCLAsWithApertures(
				Vector3D.Z,	// aHat
				Vector3D.X,	// uHat
				Vector3D.Y,	// vHat
				eta1, eta1, 0, 0,
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				boxCloak.getInterfaceTransmissionCoefficient(),
				false
			);
		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				faceName+" exterior face",
				Vector3D.sum(faceCentre, right.getProductWith(-1), up.getProductWith(-1)),	// corner
				right.getProductWith(2),	// width vector
				up.getProductWith(2),	// height vector
				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
				window1,
				boxCloak,
				boxCloak.getStudio()
		));

		// interior gCLAs
		GCLAsWithApertures window2 = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, eta2, eta2, deltay2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, boxCloak.getInterfaceTransmissionCoefficient(), false);
		
		Vector3D
			tipToBottomLeftCorner = Vector3D.difference(bottomLeftCorner, pyramidTip),
			tipToBottomRightCorner = Vector3D.difference(bottomRightCorner, pyramidTip),
			tipToTopLeftCorner = Vector3D.difference(topLeftCorner, pyramidTip),
			tipToTopRightCorner = Vector3D.difference(topRightCorner, pyramidTip);
		
		Vector3D
			leftTriangleNormal = Vector3D.crossProduct(tipToBottomLeftCorner, tipToTopLeftCorner),
			rightTriangleNormal = Vector3D.crossProduct(tipToTopRightCorner, tipToBottomRightCorner),
			topTriangleNormal = Vector3D.crossProduct(tipToTopLeftCorner, tipToTopRightCorner),
			bottomTriangleNormal = Vector3D.crossProduct(tipToBottomRightCorner, tipToBottomLeftCorner);
		
		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				"interior left interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToTopLeftCorner,	// corner 1 to corner 3
				tipToBottomLeftCorner,	// corner 1 to corner 2
				false,	// not semi-infinite
				right.getPartPerpendicularTo(leftTriangleNormal).getNormalised().getReverse(),	// unit vector in u direction
				up.getPartPerpendicularTo(leftTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				"interior right interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToBottomRightCorner,	// corner 1 to corner 2
				tipToTopRightCorner,	// corner 1 to corner 3
				false,	// not semi-infinite
				right.getPartPerpendicularTo(rightTriangleNormal).getNormalised(),	// unit vector in u direction
				up.getPartPerpendicularTo(rightTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				"interior top interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToTopRightCorner,	// corner 1 to corner 3
				tipToTopLeftCorner,	// corner 1 to corner 2
				false,	// not semi-infinite
				up.getPartPerpendicularTo(topTriangleNormal).getNormalised(),	// unit vector in u direction
				right.getPartPerpendicularTo(topTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				"interior bottom interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToBottomLeftCorner,	// corner 1 to corner 2
				tipToBottomRightCorner,	// corner 1 to corner 3
				false,	// not semi-infinite
				up.getReverse().getPartPerpendicularTo(bottomTriangleNormal).getNormalised(),	// unit vector in u direction
				right.getPartPerpendicularTo(bottomTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				boxCloak,
				boxCloak.getStudio()
		));
	}

	private void addCentralOctahedronFace(String name, Vector3D corner1, Vector3D corner2, Vector3D corner3, GCLAsWithApertures surfaceProperty)
	{
		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				name,
				corner1,	// corner 1
				Vector3D.difference(corner2, corner1),	// corner 1 to corner 2
				Vector3D.difference(corner3, corner1),	// corner 1 to corner 3
				false,	// not semi-infinite
				Vector3D.difference(corner3, corner2).getNormalised(),	// unit vector in u direction
				Vector3D.difference(corner1, Vector3D.mean(corner2, corner3)).getNormalised(),	// unit vector in v direction
				surfaceProperty,
				boxCloak,
				boxCloak.getStudio()
		));
	}
	
	private void addStarPolygon(String name, Vector3D outerVertex, Vector3D innerVertex1, Vector3D innerVertex2, GCLAsWithApertures surfaceProperty)
	{
		boxCloak.getWindows().addSceneObject(new EditableTriangle(
				name,
				outerVertex,	// corner 1
				Vector3D.difference(innerVertex1, outerVertex),	// corner 1 to corner 3
				Vector3D.difference(innerVertex2, outerVertex),	// corner 1 to corner 2
				false,	// not semi-infinite
				Vector3D.difference(innerVertex1, innerVertex2).getNormalised(),	// unit vector in u direction
				Vector3D.difference(outerVertex, Vector3D.mean(innerVertex1, innerVertex2)).getNormalised(),	// unit vector in v direction
				surfaceProperty,
				boxCloak,
				boxCloak.getStudio()
		));
	}
		

	/**
	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "windows" must exist.
	 */
	private void addWindowsForOctahedralCloak()
	{
		Vector3D
			front = boxCloak.getFrontDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of front face
			back = boxCloak.getFrontDirection().getWithLength(-0.5*boxCloak.getSideLength()),	// centre to centre of back face
			right = boxCloak.getRightDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of right face
			left = boxCloak.getRightDirection().getWithLength(-0.5*boxCloak.getSideLength()),	// centre to centre of left face
			top = boxCloak.getTopDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of top face
			bottom = boxCloak.getTopDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of bottom face

			addInterfacesForCubicCloakFace(
					"Front",	// faceName,
					front,	// cloakCentre2FaceCentre,
					right,	// right,
					top	// up
			);

			addInterfacesForCubicCloakFace(
					"Back",	// faceName,
					back,	// cloakCentre2FaceCentre,
					left,
					top
			);

			addInterfacesForCubicCloakFace(
					"Right",	// faceName,
					right,	// cloakCentre2FaceCentre,
					back,
					top
			);

			addInterfacesForCubicCloakFace(
					"Left",	// faceName,
					left,	// cloakCentre2FaceCentre,
					front,
					top
			);

			addInterfacesForCubicCloakFace(
					"Top",	// faceName,
					top,	// cloakCentre2FaceCentre,
					front,
					right
			);

			addInterfacesForCubicCloakFace(
					"Bottom",	// faceName,
					bottom,	// cloakCentre2FaceCentre,
					back,
					right
			);
			
				// pre-calculate the corners of the central octahedron...
				Vector3D
					frontCorner = Vector3D.sum(boxCloak.getCentre(), front.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
					backCorner = Vector3D.sum(boxCloak.getCentre(), back.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
					rightCorner = Vector3D.sum(boxCloak.getCentre(), right.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
					leftCorner = Vector3D.sum(boxCloak.getCentre(), left.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
					topCorner = Vector3D.sum(boxCloak.getCentre(), top.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
					bottomCorner = Vector3D.sum(boxCloak.getCentre(), bottom.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
								
				// ... and pre-calculate the corners of the cube
				Vector3D
					trfCorner = Vector3D.sum(boxCloak.getCentre(), top, right, front), // top right front
					trbCorner = Vector3D.sum(boxCloak.getCentre(), top, right, back),	// top right back
					tlfCorner = Vector3D.sum(boxCloak.getCentre(), top, left, front), // top left front
					tlbCorner = Vector3D.sum(boxCloak.getCentre(), top, left, back),	// top left back
					brfCorner = Vector3D.sum(boxCloak.getCentre(), bottom, right, front), // bottom right front
					brbCorner = Vector3D.sum(boxCloak.getCentre(), bottom, right, back),	// bottom right back
					blfCorner = Vector3D.sum(boxCloak.getCentre(), bottom, left, front), // bottom left front
					blbCorner = Vector3D.sum(boxCloak.getCentre(), bottom, left, back);	// bottom left back
				
				// add the 8-pointed "star" whose inner vertices coincide with the 6 vertices of the octahedron
				// and whose outer vertices are the 8 corners of the cube

				// first, establish the material properties
				// from Mathematica notebook, where these are the parameters of sheet 3
				double etaStar = (boxCloak.getInnerVolumeSizeFactorP()*(boxCloak.getInnerVolumeSizeFactorEM()-2)*(boxCloak.getInnerVolumeSizeFactorP()-3))/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-2)*(boxCloak.getInnerVolumeSizeFactorEM()-3));
				double deltaxStar = 0;
				double deltayStar = (Math.sqrt(2)*(boxCloak.getInnerVolumeSizeFactorEM()-boxCloak.getInnerVolumeSizeFactorP())*(boxCloak.getInnerVolumeSizeFactorP()-boxCloak.getInnerVolumeSizeFactorEM())*boxCloak.getInnerVolumeSizeFactorP())/(boxCloak.getInnerVolumeSizeFactorEM()*boxCloak.getInnerVolumeSizeFactorP()*(boxCloak.getInnerVolumeSizeFactorP()-2)*(boxCloak.getInnerVolumeSizeFactorEM()-3));
				GCLAsWithApertures starSheet = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, etaStar, etaStar, deltaxStar, deltayStar, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, boxCloak.getInterfaceTransmissionCoefficient(), false);

				// the polygons to the top right front corner
				addStarPolygon("star prong to top right front corner, triangle 1", trfCorner, rightCorner, topCorner, starSheet);
				addStarPolygon("star prong to top right front corner, triangle 2", trfCorner, frontCorner, rightCorner, starSheet);
				addStarPolygon("star prong to top right front corner, triangle 3", trfCorner, topCorner, frontCorner, starSheet);
				// the polygons to the top right back corner
				addStarPolygon("star prong to top right back corner, triangle 1", trbCorner, topCorner, rightCorner, starSheet);
				addStarPolygon("star prong to top right back corner, triangle 2", trbCorner, rightCorner, backCorner, starSheet);
				addStarPolygon("star prong to top right back corner, triangle 3", trbCorner, backCorner, topCorner, starSheet);
				// the polygons to the top left front corner
				addStarPolygon("star prong to top left front corner, triangle 1", tlfCorner, topCorner, leftCorner, starSheet);
				addStarPolygon("star prong to top left front corner, triangle 2", tlfCorner, leftCorner, frontCorner, starSheet);
				addStarPolygon("star prong to top left front corner, triangle 3", tlfCorner, frontCorner, topCorner, starSheet);
				// the polygons to the top left back corner
				addStarPolygon("star prong to top left back corner, triangle 1", tlbCorner, leftCorner, topCorner, starSheet);
				addStarPolygon("star prong to top left back corner, triangle 2", tlbCorner, backCorner, leftCorner, starSheet);
				addStarPolygon("star prong to top left back corner, triangle 3", tlbCorner, topCorner, backCorner, starSheet);
				// the polygons to the bottom right front corner
				addStarPolygon("star prong to bottom right front corner, triangle 1", brfCorner, bottomCorner, rightCorner, starSheet);
				addStarPolygon("star prong to bottom right front corner, triangle 2", brfCorner, rightCorner, frontCorner, starSheet);
				addStarPolygon("star prong to bottom right front corner, triangle 3", brfCorner, frontCorner, bottomCorner, starSheet);
				// the polygons to the bottom right back corner
				addStarPolygon("star prong to bottom right back corner, triangle 1", brbCorner, rightCorner, bottomCorner, starSheet);
				addStarPolygon("star prong to bottom right back corner, triangle 2", brbCorner, backCorner, rightCorner, starSheet);
				addStarPolygon("star prong to bottom right back corner, triangle 3", brbCorner, bottomCorner, backCorner, starSheet);
				// the polygons to the bottom left front corner
				addStarPolygon("star prong to bottom left front corner, triangle 1", blfCorner, leftCorner, bottomCorner, starSheet);
				addStarPolygon("star prong to bottom left front corner, triangle 2", blfCorner, frontCorner, leftCorner, starSheet);
				addStarPolygon("star prong to bottom left front corner, triangle 3", blfCorner, bottomCorner, frontCorner, starSheet);
				// the polygons to the bottom left back corner
				addStarPolygon("star prong to bottom left back corner, triangle 1", blbCorner, bottomCorner, leftCorner, starSheet);
				addStarPolygon("star prong to bottom left back corner, triangle 2", blbCorner, leftCorner, backCorner, starSheet);
				addStarPolygon("star prong to bottom left back corner, triangle 3", blbCorner, backCorner, bottomCorner, starSheet);

				// add the central octahedron
				
				// first, establish the material properties
				double etaOctahedron = (boxCloak.getInnerVolumeSizeFactorP()*(boxCloak.getInnerVolumeSizeFactorEM()-3))/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-3));
				GCLAsWithApertures octahedronFace = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, etaOctahedron, etaOctahedron, 0, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, boxCloak.getInterfaceTransmissionCoefficient(), false);
				
				// now add the interfaces
				addCentralOctahedronFace("innermost top front left triangle", topCorner, leftCorner, frontCorner, octahedronFace);
				addCentralOctahedronFace("innermost top front right triangle", topCorner, frontCorner, rightCorner, octahedronFace);
				addCentralOctahedronFace("innermost top back right triangle", topCorner, rightCorner, backCorner, octahedronFace);
				addCentralOctahedronFace("innermost top back left triangle", topCorner, backCorner, leftCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom front left triangle", bottomCorner, frontCorner, leftCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom back left triangle", bottomCorner, leftCorner, backCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom back right triangle", bottomCorner, backCorner, rightCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom front left triangle", bottomCorner, rightCorner, frontCorner, octahedronFace);
	}
	
	/**
	 * add spheres at the vertices of the outside cube
	 * @param vertices
	 */
	private void addCubeVertices(EditableSceneObjectCollection vertices)
	{
		// the corners of the cube...
		EditableSceneObjectCollection cubeVertices = new EditableSceneObjectCollection("cube vertices", true, vertices, boxCloak.getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfCubeVertex, cubeVertices);
		addFrameSphere("sphere in top right back cube vertex", trbCubeVertex, cubeVertices);
		addFrameSphere("sphere in top left front cube vertex", tlfCubeVertex, cubeVertices);
		addFrameSphere("sphere in top left back cube vertex", tlbCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom right front cube vertex", brfCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom right back cube vertex", brbCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom left front cube vertex", blfCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom left back cube vertex", blbCubeVertex, cubeVertices);
		
		vertices.addSceneObject(cubeVertices);
	}
	
	private void addCubeEdges(EditableSceneObjectCollection edges)
	{
		// the edges of the cube
		EditableSceneObjectCollection cubeEdges = new EditableSceneObjectCollection("cube edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left cube edge", tlfCubeVertex, tlbCubeVertex, cubeEdges);
		addFrameCylinder("top right cube edge", trfCubeVertex, trbCubeVertex, cubeEdges);
		addFrameCylinder("top front cube edge", tlfCubeVertex, trfCubeVertex, cubeEdges);
		addFrameCylinder("top back cube edge", tlbCubeVertex, trbCubeVertex, cubeEdges);
		addFrameCylinder("bottom left cube edge", blfCubeVertex, blbCubeVertex, cubeEdges);
		addFrameCylinder("bottom right cube edge", brfCubeVertex, brbCubeVertex, cubeEdges);
		addFrameCylinder("bottom front cube edge", blfCubeVertex, brfCubeVertex, cubeEdges);
		addFrameCylinder("bottom back cube edge", blbCubeVertex, brbCubeVertex, cubeEdges);
		addFrameCylinder("front left cube edge", tlfCubeVertex, blfCubeVertex, cubeEdges);
		addFrameCylinder("front right cube edge", trfCubeVertex, brfCubeVertex, cubeEdges);
		addFrameCylinder("back left cube edge", tlbCubeVertex, blbCubeVertex, cubeEdges);
		addFrameCylinder("back right cube edge", trbCubeVertex, brbCubeVertex, cubeEdges);
		
		edges.addSceneObject(cubeEdges);
	}
	
	private void addFramesForCubicCloak()
	{
		// spheres at the vertices
	
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, boxCloak.getFrames(), boxCloak.getStudio());

		// add the vertices of the cube...
		addCubeVertices(vertices);
		
		// ... and the vertices of the octahedron
		EditableSceneObjectCollection octahedronVertices = new EditableSceneObjectCollection("octahedron vertices", true, vertices, boxCloak.getStudio());

		addFrameSphere("sphere in front octahedron vertex", frontOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in back octahedron vertex", backOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in right octahedron vertex", rightOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in left octahedron vertex", leftOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in top octahedron vertex", topOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in bottom octahedron vertex", bottomOctahedronVertex, octahedronVertices);
		
		vertices.addSceneObject(octahedronVertices);
		
		boxCloak.getFrames().addSceneObject(vertices);

		// cylinders connecting neighbouring vertices

		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, boxCloak.getFrames(), boxCloak.getStudio());

		// add the edges of the cube
		addCubeEdges(edges);
		
		// the edges of the octahedron
		EditableSceneObjectCollection octahedronEdges = new EditableSceneObjectCollection("octahedron edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left octahedron edge", leftOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("top right octahedron edge", rightOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("top front octahedron edge", frontOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("top back octahedron edge", backOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("left front octahedron edge", leftOctahedronVertex, frontOctahedronVertex, octahedronEdges);
		addFrameCylinder("right front octahedron edge", rightOctahedronVertex, frontOctahedronVertex, octahedronEdges);
		addFrameCylinder("left back octahedron edge", leftOctahedronVertex, backOctahedronVertex, octahedronEdges);
		addFrameCylinder("right back octahedron edge", rightOctahedronVertex, backOctahedronVertex, octahedronEdges);		
		addFrameCylinder("bottom left octahedron edge", leftOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		addFrameCylinder("bottom right octahedron edge", rightOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		addFrameCylinder("bottom front octahedron edge", frontOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		addFrameCylinder("bottom back octahedron edge", backOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		
		edges.addSceneObject(octahedronEdges);
		
		// the edges connecting the cube edges to the octahedron vertices ("star" edges)
		EditableSceneObjectCollection starEdges = new EditableSceneObjectCollection("star edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left front cube corner to left octahedron vertex edge", tlfCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to left octahedron vertex edge", blfCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("top left back cube corner to left octahedron vertex edge", tlbCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("bottom left back cube corner to left octahedron vertex edge", blbCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to right octahedron vertex edge", trfCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to right octahedron vertex edge", brfCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("top right back cube corner to right octahedron vertex edge", trbCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("bottom right back cube corner to right octahedron vertex edge", brbCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("top left front cube corner to top octahedron vertex edge", tlfCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to top octahedron vertex edge", trfCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("top right back cube corner to top octahedron vertex edge", trbCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("top left back cube corner to top octahedron vertex edge", tlbCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to bottom octahedron vertex edge", blfCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to bottom octahedron vertex edge", brfCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("bottom right back cube corner to bottom octahedron vertex edge", brbCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("bottom left back cube corner to bottom octahedron vertex edge", blbCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("top left front cube corner to front octahedron vertex edge", tlfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to front octahedron vertex edge", trfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to front octahedron vertex edge", brfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to front octahedron vertex edge", blfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("top left back cube corner to back octahedron vertex edge", tlbCubeVertex, backOctahedronVertex, starEdges);
		addFrameCylinder("top right back cube corner to back octahedron vertex edge", trbCubeVertex, backOctahedronVertex, starEdges);
		addFrameCylinder("bottom right back cube corner to back octahedron vertex edge", brbCubeVertex, backOctahedronVertex, starEdges);
		addFrameCylinder("bottom left back cube corner to back octahedron vertex edge", blbCubeVertex, backOctahedronVertex, starEdges);
		
		edges.addSceneObject(starEdges);
		
		boxCloak.getFrames().addSceneObject(edges);
	}
	
}
