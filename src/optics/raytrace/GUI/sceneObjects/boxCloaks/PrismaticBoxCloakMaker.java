package optics.raytrace.GUI.sceneObjects.boxCloaks;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

public class PrismaticBoxCloakMaker extends CloakMaker
{
	private static final long serialVersionUID = -6144482557897166516L;

	//
	// constructors
	//
	
	public PrismaticBoxCloakMaker(EditableBoxCloak boxCloak)
	{
		super(boxCloak);
	}

	// the direction vectors, i.e. the vectors from the centre of the cube to the centre of the different faces
	protected Vector3D front, back, right, left, top, bottom;
	
	// the vertices of the cube
	protected Vector3D trfCubeVertex, trbCubeVertex, tlfCubeVertex, tlbCubeVertex, brfCubeVertex, brbCubeVertex, blfCubeVertex, blbCubeVertex;
	
	// the vertices of the central cuboid (for the prismatic cloak)
	protected Vector3D bfCuboidVertex, brCuboidVertex, bbCuboidVertex, blCuboidVertex, tfCuboidVertex, trCuboidVertex, tbCuboidVertex, tlCuboidVertex;

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

		// pre-calculate the vertices of the central cuboid
		bfCuboidVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, front2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		bbCuboidVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, back2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		brCuboidVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, right2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		blCuboidVertex = Vector3D.sum(boxCloak.getCentre(), bottom2, left2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		tfCuboidVertex = Vector3D.sum(boxCloak.getCentre(), top2, front2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		tbCuboidVertex = Vector3D.sum(boxCloak.getCentre(), top2, back2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		trCuboidVertex = Vector3D.sum(boxCloak.getCentre(), top2, right2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
		tlCuboidVertex = Vector3D.sum(boxCloak.getCentre(), top2, left2.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));

		// calculate the boxCloak.getWindows() and the boxCloak.getFrames()
		addWindowsForPrismaticCloak();
		addFramesForPrismaticCloak();
	}
	
	private void addFramesForPrismaticCloak()
	{
		// spheres at the vertices
	
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, boxCloak.getFrames(), boxCloak.getStudio());

		// add the vertices of the cube...
		addCubeVertices(vertices);
		
		// ... and the vertices of the cuboid
		EditableSceneObjectCollection cuboidVertices = new EditableSceneObjectCollection("cuboid vertices", true, vertices, boxCloak.getStudio());

		addFrameSphere("sphere in bottom front cuboid vertex", bfCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in bottom back cuboid vertex", bbCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in bottom right cuboid vertex", brCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in bottom left cuboid vertex", blCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in top front cuboid vertex", tfCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in top back cuboid vertex", tbCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in top right cuboid vertex", trCuboidVertex, cuboidVertices);
		addFrameSphere("sphere in top left cuboid vertex", tlCuboidVertex, cuboidVertices);
		
		vertices.addSceneObject(cuboidVertices);
		
		boxCloak.getFrames().addSceneObject(vertices);

		// cylinders connecting neighbouring vertices

		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, boxCloak.getFrames(), boxCloak.getStudio());

		// add the edges of the cube
		addCubeEdges(edges);
		
		// the edges of the cuboid
		EditableSceneObjectCollection cuboidEdges = new EditableSceneObjectCollection("cuboid edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left front cuboid edge", tlCuboidVertex, tfCuboidVertex, cuboidEdges);
		addFrameCylinder("top right front cuboid edge", trCuboidVertex, tfCuboidVertex, cuboidEdges);
		addFrameCylinder("top left back cuboid edge", tlCuboidVertex, tbCuboidVertex, cuboidEdges);
		addFrameCylinder("top right back cuboid edge", trCuboidVertex, tbCuboidVertex, cuboidEdges);
		addFrameCylinder("bottom left front cuboid edge", blCuboidVertex, bfCuboidVertex, cuboidEdges);
		addFrameCylinder("bottom right front cuboid edge", brCuboidVertex, bfCuboidVertex, cuboidEdges);
		addFrameCylinder("bottom left back cuboid edge", blCuboidVertex, bbCuboidVertex, cuboidEdges);
		addFrameCylinder("bottom right back cuboid edge", brCuboidVertex, bbCuboidVertex, cuboidEdges);
		addFrameCylinder("front cuboid edge", bfCuboidVertex, tfCuboidVertex, cuboidEdges);
		addFrameCylinder("back cuboid edge", bbCuboidVertex, tbCuboidVertex, cuboidEdges);
		addFrameCylinder("right cuboid edge", brCuboidVertex, trCuboidVertex, cuboidEdges);
		addFrameCylinder("left cuboid edge", blCuboidVertex, tlCuboidVertex, cuboidEdges);
		
		edges.addSceneObject(cuboidEdges);
		
		// the edges connecting the cube vertices to the cuboid vertices ("star" edges)
		EditableSceneObjectCollection starEdges = new EditableSceneObjectCollection("star edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left front cube vertex to top left cuboid vertex edge", tlfCubeVertex, tlCuboidVertex, starEdges);
		addFrameCylinder("top left front cube vertex to top front cuboid vertex edge", tlfCubeVertex, tfCuboidVertex, starEdges);
		addFrameCylinder("top right front cube vertex to top right cuboid vertex edge", trfCubeVertex, trCuboidVertex, starEdges);
		addFrameCylinder("top right front cube vertex to top front cuboid vertex edge", trfCubeVertex, tfCuboidVertex, starEdges);
		addFrameCylinder("top left back cube vertex to top left cuboid vertex edge", tlbCubeVertex, tlCuboidVertex, starEdges);
		addFrameCylinder("top left back cube vertex to top back cuboid vertex edge", tlbCubeVertex, tbCuboidVertex, starEdges);
		addFrameCylinder("top right back cube vertex to top right cuboid vertex edge", trbCubeVertex, trCuboidVertex, starEdges);
		addFrameCylinder("top right back cube vertex to top back cuboid vertex edge", trbCubeVertex, tbCuboidVertex, starEdges);
		addFrameCylinder("bottom left front cube vertex to bottom left cuboid vertex edge", blfCubeVertex, blCuboidVertex, starEdges);
		addFrameCylinder("bottom left front cube vertex to bottom front cuboid vertex edge", blfCubeVertex, bfCuboidVertex, starEdges);
		addFrameCylinder("bottom right front cube vertex to bottom right cuboid vertex edge", brfCubeVertex, brCuboidVertex, starEdges);
		addFrameCylinder("bottom right front cube vertex to bottom front cuboid vertex edge", brfCubeVertex, bfCuboidVertex, starEdges);
		addFrameCylinder("bottom left back cube vertex to bottom left cuboid vertex edge", blbCubeVertex, blCuboidVertex, starEdges);
		addFrameCylinder("bottom left back cube vertex to bottom back cuboid vertex edge", blbCubeVertex, bbCuboidVertex, starEdges);
		addFrameCylinder("bottom right back cube vertex to bottom right cuboid vertex edge", brbCubeVertex, brCuboidVertex, starEdges);
		addFrameCylinder("bottom right back cube vertex to bottom back cuboid vertex edge", brbCubeVertex, bbCuboidVertex, starEdges);
		
		edges.addSceneObject(starEdges);
		
		boxCloak.getFrames().addSceneObject(edges);
	}


	/**
	 * We have divided up all the surfaces that make up the prismatic cloak (apart from the central square prism)
	 * into those behind the different exterior faces.
	 * This method adds the surfaces behind one particular exterior face.
	 * @param faceName
	 * @param cloakCentre2FaceCentre
	 * @param right
	 * @param up
	 */
	private void addInterfacesForPrismaticCloakFace(String faceName, Vector3D cloakCentre2FaceCentre, Vector3D right, Vector3D up)
	{
		// parameters for outside CLAs
		double eta1 = (boxCloak.getInnerVolumeSizeFactorP()-1)/(boxCloak.getInnerVolumeSizeFactorEM()-1);
		
		// parameters for inside gCLAs (deltaY2 = 0)
		double eta2 = (boxCloak.getInnerVolumeSizeFactorP()*(boxCloak.getInnerVolumeSizeFactorEM()-1)*(boxCloak.getInnerVolumeSizeFactorP()-2))/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-1)*(boxCloak.getInnerVolumeSizeFactorEM()-2));
			// (boxCloak.getA()*(4*boxCloak.getAPrime()*boxCloak.getAPrime()-1))/(boxCloak.getAPrime()*(4*boxCloak.getA()*boxCloak.getA()-1));
		double deltax2 = (boxCloak.getInnerVolumeSizeFactorEM()-boxCloak.getInnerVolumeSizeFactorP())*(boxCloak.getInnerVolumeSizeFactorP()-boxCloak.getInnerVolumeSizeFactorEM())/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-1)*(boxCloak.getInnerVolumeSizeFactorEM()-2));
			// (2*(boxCloak.getA()*boxCloak.getA()-2*boxCloak.getA()*boxCloak.getAPrime()+boxCloak.getAPrime()*boxCloak.getAPrime()))/(boxCloak.getAPrime()*(4*boxCloak.getA()*boxCloak.getA()-1));
		
		Vector3D
			faceCentre = Vector3D.sum(boxCloak.getCentre(), cloakCentre2FaceCentre),	// the centre of the exterior face
			left = right.getReverse(),
			down = up.getReverse();
				
		// the corners
		Vector3D
			faceBottomLeftCorner = Vector3D.sum(faceCentre, left, down),
			faceBottomRightCorner = Vector3D.sum(faceCentre, right, down),
			faceTopLeftCorner = Vector3D.sum(faceCentre, left, up),
			// faceTopRightCorner = Vector3D.sum(faceCentre, right, up),	// not actually needed
			prismTopCorner = Vector3D.sum(boxCloak.getCentre(), cloakCentre2FaceCentre.getProductWith(boxCloak.getInnerVolumeSizeFactorP()), up),
			prismBottomCorner = Vector3D.sum(boxCloak.getCentre(), cloakCentre2FaceCentre.getProductWith(boxCloak.getInnerVolumeSizeFactorP()), down);
				
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
				faceBottomLeftCorner,	// corner
				right.getProductWith(2),	// width vector
				up.getProductWith(2),	// height vector
				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
				window1,
				boxCloak,
				boxCloak.getStudio()
		));

		// interior gCLAs
		GCLAsWithApertures window2 = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, eta2, eta2, deltax2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, boxCloak.getInterfaceTransmissionCoefficient(), false);
		
		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"interior left interface behind "+faceName+" exterior face",
				prismTopCorner, //corner
				Vector3D.difference(faceTopLeftCorner, prismTopCorner), // width vector
				down.getProductWith(2),	// height vector
				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
				window2,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"interior right interface behind "+faceName+" exterior face",
				prismBottomCorner, //corner
				Vector3D.difference(faceBottomRightCorner, prismBottomCorner), // width vector
				up.getProductWith(2),	// height vector
				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
				window2,
				boxCloak,
				boxCloak.getStudio()
		));
	}

	/**
	 * Add all the boxCloak.getWindows() (without frames --- those get added separately) for a prismatic cloak.
	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "boxCloak.getWindows()" must exist.
	 */
	private void addWindowsForPrismaticCloak()
	{		
		// prismatic, i.e. "cylindrical", cloak
		addInterfacesForPrismaticCloakFace(
				"Front",	// faceName,
				boxCloak.getFrontDirection().getWithLength(boxCloak.getSideLength()/2),	// cloakCentre2FaceCentre,
				boxCloak.getRightDirection().getWithLength(boxCloak.getSideLength()/2),	// right,
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()/2)	// up
		);

		addInterfacesForPrismaticCloakFace(
				"Back",	// faceName,
				boxCloak.getFrontDirection().getWithLength(-boxCloak.getSideLength()/2),	// cloakCentre2FaceCentre,
				boxCloak.getRightDirection().getWithLength(-boxCloak.getSideLength()/2),	// right,
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()/2)	// up
		);

		addInterfacesForPrismaticCloakFace(
				"Right",	// faceName,
				boxCloak.getRightDirection().getWithLength(boxCloak.getSideLength()/2),	// cloakCentre2FaceCentre,
				boxCloak.getFrontDirection().getWithLength(-boxCloak.getSideLength()/2),	// right,
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()/2)	// up
		);

		addInterfacesForPrismaticCloakFace(
				"Left",	// faceName,
				boxCloak.getRightDirection().getWithLength(-boxCloak.getSideLength()/2),	// cloakCentre2FaceCentre,
				boxCloak.getFrontDirection().getWithLength(boxCloak.getSideLength()/2),	// right,
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()/2)	// up
		);

		// add the central square prism

		// first, establish the material properties
		double eta3 = ((boxCloak.getInnerVolumeSizeFactorEM()-2)*boxCloak.getInnerVolumeSizeFactorP())/(boxCloak.getInnerVolumeSizeFactorEM()*(boxCloak.getInnerVolumeSizeFactorP()-2));
		// ((1 + 2*boxCloak.getA())*(-1 + 2*boxCloak.getAPrime()))/((-1 + 2*boxCloak.getA())*(1 + 2*boxCloak.getAPrime()));
		GCLAsWithApertures sheet3 = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, eta3, eta3, 0, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, boxCloak.getInterfaceTransmissionCoefficient(), false);

		// pre-calculate the bottom corners
		Vector3D
		bottomFrontCorner = Vector3D.sum(boxCloak.getCentre(), boxCloak.getFrontDirection().getWithLength(boxCloak.getInnerVolumeSizeFactorP()*boxCloak.getSideLength()/2), boxCloak.getTopDirection().getWithLength(-boxCloak.getSideLength()/2)),
		bottomRightCorner = Vector3D.sum(boxCloak.getCentre(), boxCloak.getRightDirection().getWithLength(boxCloak.getInnerVolumeSizeFactorP()*boxCloak.getSideLength()/2), boxCloak.getTopDirection().getWithLength(-boxCloak.getSideLength()/2)),
		bottomBackCorner = Vector3D.sum(boxCloak.getCentre(), boxCloak.getFrontDirection().getWithLength(-boxCloak.getInnerVolumeSizeFactorP()*boxCloak.getSideLength()/2), boxCloak.getTopDirection().getWithLength(-boxCloak.getSideLength()/2)),
		bottomLeftCorner = Vector3D.sum(boxCloak.getCentre(), boxCloak.getRightDirection().getWithLength(-boxCloak.getInnerVolumeSizeFactorP()*boxCloak.getSideLength()/2), boxCloak.getTopDirection().getWithLength(-boxCloak.getSideLength()/2));

		// now add the interfaces
		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"innermost front left interface",
				bottomLeftCorner, //corner
				Vector3D.difference(bottomFrontCorner, bottomLeftCorner), // width vector
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()),	// height vector 
				sheet3,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"innermost front right interface",
				bottomFrontCorner, //corner
				Vector3D.difference(bottomRightCorner, bottomFrontCorner), // width vector
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()),	// height vector 
				sheet3,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"innermost back right interface",
				bottomRightCorner, //corner
				Vector3D.difference(bottomBackCorner, bottomRightCorner), // width vector
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()),	// height vector 
				sheet3,
				boxCloak,
				boxCloak.getStudio()
		));

		boxCloak.getWindows().addSceneObject(new EditableScaledParametrisedParallelogram(
				"innermost back left interface",
				bottomBackCorner, //corner
				Vector3D.difference(bottomLeftCorner, bottomBackCorner), // width vector
				boxCloak.getTopDirection().getWithLength(boxCloak.getSideLength()),	// height vector 
				sheet3,
				boxCloak,
				boxCloak.getStudio()
		));
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
}
