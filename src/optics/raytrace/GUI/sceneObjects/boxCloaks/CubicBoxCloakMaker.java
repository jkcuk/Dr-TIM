package optics.raytrace.GUI.sceneObjects.boxCloaks;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;

public class CubicBoxCloakMaker extends CloakMaker
{
	private static final long serialVersionUID = 6244203733963960388L;

	//
	// constructors
	//
	
	/**
	 * @param boxCloak
	 */
	public CubicBoxCloakMaker(EditableBoxCloak boxCloak)
	{
		super(boxCloak);
	}
	
	
	//
	// BoxCloakMaker method
	//
	
	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.sceneObjects.boxCloaks.BoxCloakMaker#addSceneObjects(optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection, optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection, optics.raytrace.GUI.sceneObjects.EditableBoxCloak)
	 */
	@Override
	public void addSceneObjects()
	{
		calculateVertices();		
		
		// calculate the windows and the frames
		addWindows();
		addFrames();
	}

	
	//
	// the guts
	//
	
	//
	// the directions and vertices
	//

	// the vertices of the outer cube
	protected Vector3D trfoVertex, trboVertex, tlfoVertex, tlboVertex, brfoVertex, brboVertex, blfoVertex, blboVertex;
	
	// the vertices of the inner cube, physical space
	protected Vector3D trfiPVertex, trbiPVertex, tlfiPVertex, tlbiPVertex, brfiPVertex, brbiPVertex, blfiPVertex, blbiPVertex;


	//
	// the vertices
	//
	
	protected void calculateVertices()
	{
		// calculate the direction vectors with half sidelength length
		Vector3D front, back, right, left, top, bottom;
		front = boxCloak.getFrontDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of front face
		back = boxCloak.getFrontDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of back face
		right = boxCloak.getRightDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of right face
		left = boxCloak.getRightDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of left face
		top = boxCloak.getTopDirection().getWithLength(0.5*boxCloak.getSideLength());	// centre to centre of top face
		bottom = boxCloak.getTopDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of bottom face		
		
		// calculate the direction vectors with half sidelength length * a (suitable for inner cube in EM space)
//		Vector3D frontA, backA, rightA, leftA, topA, bottomA;
//		frontA = front.getProductWith(a);
//		backA = back.getProductWith(a);
//		rightA = right.getProductWith(a);
//		leftA = left.getProductWith(a);
//		topA = top.getProductWith(a);
//		bottomA = bottom.getProductWith(a);

		// calculate the direction vectors with half sidelength length * aPrime (suitable for inner cube in physical space)
		Vector3D frontAP, backAP, rightAP, leftAP, topAP, bottomAP;
		frontAP = front.getProductWith(boxCloak.getInnerVolumeSizeFactorP());
		backAP = back.getProductWith(boxCloak.getInnerVolumeSizeFactorP());
		rightAP = right.getProductWith(boxCloak.getInnerVolumeSizeFactorP());
		leftAP = left.getProductWith(boxCloak.getInnerVolumeSizeFactorP());
		topAP = top.getProductWith(boxCloak.getInnerVolumeSizeFactorP());
		bottomAP = bottom.getProductWith(boxCloak.getInnerVolumeSizeFactorP());

		// pre-calculate the corners of the outer cube
		trfoVertex = Vector3D.sum(boxCloak.getCentre(), top, right, front); // top right front
		trboVertex = Vector3D.sum(boxCloak.getCentre(), top, right, back);	// top right back
		tlfoVertex = Vector3D.sum(boxCloak.getCentre(), top, left, front); // top left front
		tlboVertex = Vector3D.sum(boxCloak.getCentre(), top, left, back);	// top left back
		brfoVertex = Vector3D.sum(boxCloak.getCentre(), bottom, right, front); // bottom right front
		brboVertex = Vector3D.sum(boxCloak.getCentre(), bottom, right, back);	// bottom right back
		blfoVertex = Vector3D.sum(boxCloak.getCentre(), bottom, left, front); // bottom left front
		blboVertex = Vector3D.sum(boxCloak.getCentre(), bottom, left, back);	// bottom left back

		// pre-calculate the corners of the inner cube in physical space
		trfiPVertex = Vector3D.sum(boxCloak.getCentre(), topAP, rightAP, frontAP); // top right front
		trbiPVertex = Vector3D.sum(boxCloak.getCentre(), topAP, rightAP, backAP);	// top right back
		tlfiPVertex = Vector3D.sum(boxCloak.getCentre(), topAP, leftAP, frontAP); // top left front
		tlbiPVertex = Vector3D.sum(boxCloak.getCentre(), topAP, leftAP, backAP);	// top left back
		brfiPVertex = Vector3D.sum(boxCloak.getCentre(), bottomAP, rightAP, frontAP); // bottom right front
		brbiPVertex = Vector3D.sum(boxCloak.getCentre(), bottomAP, rightAP, backAP);	// bottom right back
		blfiPVertex = Vector3D.sum(boxCloak.getCentre(), bottomAP, leftAP, frontAP); // bottom left front
		blbiPVertex = Vector3D.sum(boxCloak.getCentre(), bottomAP, leftAP, backAP);	// bottom left back

//		// pre-calculate the corners of the inner cube in EM space
//		trfiEMVertex = Vector3D.sum(boxCloak.getCentre(), topA, rightA, frontA); // top right front
//		trbiEMVertex = Vector3D.sum(boxCloak.getCentre(), topA, rightA, backA);	// top right back
//		tlfiEMVertex = Vector3D.sum(boxCloak.getCentre(), topA, leftA, frontA); // top left front
//		tlbiEMVertex = Vector3D.sum(boxCloak.getCentre(), topA, leftA, backA);	// top left back
//		brfiEMVertex = Vector3D.sum(boxCloak.getCentre(), bottomA, rightA, frontA); // bottom right front
//		brbiEMVertex = Vector3D.sum(boxCloak.getCentre(), bottomA, rightA, backA);	// bottom right back
//		blfiEMVertex = Vector3D.sum(boxCloak.getCentre(), bottomA, leftA, frontA); // bottom left front
//		blbiEMVertex = Vector3D.sum(boxCloak.getCentre(), bottomA, leftA, backA);	// bottom left back
	}

	
	private EditableSceneObjectCollection
		outerCubeWindows,
		innerCubeWindows,
		diagonalWindows;
	
	
	//
	// the windows
	//
	
	/**
	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
	 * The EditableSceneObjectCollection "windows" must exist.
	 */
	private void addWindows()
	{
		// first create the EditableSceneObjectCollections where the different windows go...
		outerCubeWindows = new EditableSceneObjectCollection("Outer cube", true, boxCloak.getWindows(), boxCloak.getStudio());
		innerCubeWindows = new EditableSceneObjectCollection("Inner cube", true, boxCloak.getWindows(), boxCloak.getStudio());
		diagonalWindows = new EditableSceneObjectCollection("Diagonals", true, boxCloak.getWindows(), boxCloak.getStudio());
		
		// ... and add them to the EditableSceneObjectCollection "Windows"
		boxCloak.getWindows().addSceneObject(outerCubeWindows);
		boxCloak.getWindows().addSceneObject(innerCubeWindows);
		boxCloak.getWindows().addSceneObject(diagonalWindows);
				
		// calculate the direction vectors with half sidelength length
		Vector3D
			front = boxCloak.getFrontDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of front face
			back = boxCloak.getFrontDirection().getWithLength(-0.5*boxCloak.getSideLength()),	// centre to centre of back face
			right = boxCloak.getRightDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of right face
			left = boxCloak.getRightDirection().getWithLength(-0.5*boxCloak.getSideLength()),	// centre to centre of left face
			top = boxCloak.getTopDirection().getWithLength(0.5*boxCloak.getSideLength()),	// centre to centre of top face
			bottom = boxCloak.getTopDirection().getWithLength(-0.5*boxCloak.getSideLength());	// centre to centre of bottom face		

		addCubeFacesForDirection(
				"front",	// description
				front,	// direction, 
				right,	// otherDirection1, 
				top	// otherDirection2
			);

		addCubeFacesForDirection(
				"back",	// description
				back,	// direction, 
				left,	// otherDirection1, 
				bottom	// otherDirection2
			);

		addCubeFacesForDirection(
				"top",	// description
				top,	// direction, 
				front,	// otherDirection1, 
				right	// otherDirection2
			);

		addCubeFacesForDirection(
				"bottom",	// description
				bottom,	// direction, 
				back,	// otherDirection1, 
				left	// otherDirection2
			);

		addCubeFacesForDirection(
				"right",	// description
				right,	// direction, 
				top,	// otherDirection1, 
				front	// otherDirection2
			);

		addCubeFacesForDirection(
				"left",	// description
				left,	// direction, 
				bottom,	// otherDirection1, 
				back	// otherDirection2
			);
	}
	
	
	/**
	 * For a given <direction> (i.e. a vector pointing to the front, back, top, bottom, left, or right),
	 * add the relevant glenses.  There are two that are perpendicular to <direction>, and two diagonal ones.
	 * I have divided up the diagonal windows, and defined the other directions, such that adding the cube
	 * faces for all 6 directions adds all 12 diagonal glenses.
	 * @param description
	 * @param direction
	 * @param otherDirection1
	 * @param otherDirection2
	 */
	private void addCubeFacesForDirection(
			String description,
			Vector3D direction, Vector3D otherDirection1, Vector3D otherDirection2
		)
	{
		// System.out.println("EdibleGlensCloak::addCubeFacesForDirection: direction = "+direction+" ("+description+")");
		
		// Calculate one corner of the outer cube in the direction.
		// For example, is <direction> is in the forward direction, then this is one of the front corners.
		// Also calculate two corners of the inner cube in the direction, in physical (P) and EM (E) space.
		// For example, if <direction> is the forward direction, then these are two of the front corners.
		Vector3D
			direction2Corner1 = Vector3D.sum(direction, otherDirection1, otherDirection2),
			direction2Corner2 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2),
			outsideWindowCorner = Vector3D.sum(boxCloak.getCentre(), direction2Corner1),
			insideWindowCorner1P = Vector3D.sum(boxCloak.getCentre(), direction2Corner1.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			insideWindowCorner1E = Vector3D.sum(boxCloak.getCentre(), direction2Corner1.getProductWith(boxCloak.getInnerVolumeSizeFactorEM())),
			insideWindowCorner2P = Vector3D.sum(boxCloak.getCentre(), direction2Corner2.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			insideWindowCorner2E = Vector3D.sum(boxCloak.getCentre(), direction2Corner2.getProductWith(boxCloak.getInnerVolumeSizeFactorEM()));
		
		// outside window
		GlensSurface outsideGlens = new GlensSurface(
				outsideWindowCorner,	// pointOnGlens,
				direction,	// front	// opticalAxisDirectionPos; "outside" is therefore in positive space
				insideWindowCorner1P, insideWindowCorner1E,	// QNeg, QPos,
				insideWindowCorner2P, insideWindowCorner2E,	// RNeg, RPos,
				boxCloak.getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
				false	// shadowThrowing
				);
		outerCubeWindows.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						"outside "+description,	// description
						outsideWindowCorner,	// corner, 
						otherDirection1.getProductWith(-2),	// spanVector1,
						otherDirection2.getProductWith(-2),	// spanVector2, 
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						outsideGlens,	// surfaceProperty,
						outerCubeWindows,	// parent,
						boxCloak.getStudio()	// studio
						)
				);

		// Calculate two of the corners of the inner cube in the "other" direction, in physical (P) and EM (E) space.
		// For example, if <direction> is the forward direction, then these are two of the back corners.
		Vector3D
			insideCubeCorner3P = Vector3D.sum(boxCloak.getCentre(), direction2Corner1.getProductWith(-boxCloak.getInnerVolumeSizeFactorP())),
			insideCubeCorner3E = Vector3D.sum(boxCloak.getCentre(), direction2Corner1.getProductWith(-boxCloak.getInnerVolumeSizeFactorEM())),
			insideCubeCorner4P = Vector3D.sum(boxCloak.getCentre(), direction2Corner2.getProductWith(-boxCloak.getInnerVolumeSizeFactorP())),
			insideCubeCorner4E = Vector3D.sum(boxCloak.getCentre(), direction2Corner2.getProductWith(-boxCloak.getInnerVolumeSizeFactorEM()));

		// inside window
		// Corners 3 and 4 of the inside cube in physical space, when seen through the outside and inside windows,
		// have to appear to be at the corresponding positions in EM space.
		// We do this in reverse: first we image corner 3 in EM space through the outside glens, then we ask the
		// inside glens to image this intermediate image to the position of corner 3 in physical space.
		// (Same with corner 4, of course.)
		Vector3D
			corner3IntermediateImage = outsideGlens.getFiniteImagePosition(insideCubeCorner3E, ImagingDirection.POS2NEG),
			corner4IntermediateImage = outsideGlens.getFiniteImagePosition(insideCubeCorner4E, ImagingDirection.POS2NEG);
		innerCubeWindows.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						"inside "+description,	// description
						insideWindowCorner1P,	// corner, 
						otherDirection1.getProductWith(-2*boxCloak.getInnerVolumeSizeFactorP()),	// spanVector1,
						otherDirection2.getProductWith(-2*boxCloak.getInnerVolumeSizeFactorP()),	// spanVector2,
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						new GlensSurface(
								insideWindowCorner1P,	// pointOnGlens,
								direction,	// opticalAxisDirectionPos; "outside" is therefore in positive space
								insideCubeCorner3P, corner3IntermediateImage,	// QNeg, QPos,
								insideCubeCorner4P, corner4IntermediateImage,	// RNeg, RPos,
								boxCloak.getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
								false	// shadowThrowing
								),	// surfaceProperty,
						innerCubeWindows,	// parent,
						boxCloak.getStudio()	// studio
						)
				);
		
		// c1 to c4: corners of the outside cube on "this" side (i.e. the side given by direction)
		// ci1 to ci4: corners of the inside cube on "this" side
		Vector3D
			direction2c1 = Vector3D.sum(direction, otherDirection1, otherDirection2),
			direction2c2 = Vector3D.sum(direction, otherDirection1, otherDirection2.getReverse()),
			direction2c3 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2.getReverse()),
			direction2c4 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2),
			c1 = Vector3D.sum(boxCloak.getCentre(), direction2c1),
			c2 = Vector3D.sum(boxCloak.getCentre(), direction2c2),
			c3 = Vector3D.sum(boxCloak.getCentre(), direction2c3),
			c4 = Vector3D.sum(boxCloak.getCentre(), direction2c4),
			ci1 = Vector3D.sum(boxCloak.getCentre(), direction2c1.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			ci2 = Vector3D.sum(boxCloak.getCentre(), direction2c2.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			ci3 = Vector3D.sum(boxCloak.getCentre(), direction2c3.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			ci4 = Vector3D.sum(boxCloak.getCentre(), direction2c4.getProductWith(boxCloak.getInnerVolumeSizeFactorP()));
			
		// corners on the other side, co1 to co4
		Vector3D
			direction2co1 = Vector3D.sum(direction.getReverse(), otherDirection1, otherDirection2),
			direction2co2 = Vector3D.sum(direction.getReverse(), otherDirection1, otherDirection2.getReverse()),
			direction2co3 = Vector3D.sum(direction.getReverse(), otherDirection1.getReverse(), otherDirection2),
			direction2co4 = Vector3D.sum(direction.getReverse(), otherDirection1.getReverse(), otherDirection2.getReverse()),
			co1 = Vector3D.sum(boxCloak.getCentre(), direction2co1),
			co2 = Vector3D.sum(boxCloak.getCentre(), direction2co2),
			co3 = Vector3D.sum(boxCloak.getCentre(), direction2co3),
			co4 = Vector3D.sum(boxCloak.getCentre(), direction2co4);
				
		// Corners co1 and co2 of the outside cube, when seen through the outside and diagonal windows,
		// have to appear to be at the corresponding positions in EM space, i.e. co1 and co2 again.
		// We do this in reverse: first we image co1 through the outside glens, then we ask the
		// diagonal glens to image this intermediate image to co1 again.
		// (Same with corner co2, of course.)
		Vector3D
			co1IntermediateImage = outsideGlens.getFiniteImagePosition(co1, ImagingDirection.POS2NEG),
			co2IntermediateImage = outsideGlens.getFiniteImagePosition(co2, ImagingDirection.POS2NEG);
		diagonalWindows.addSceneObject(
				getConvexPolygonalGlens(
						"diagonal 1 "+description,	// description,
						c1, ci1, ci2, c2,	// corners
						direction,	// outwardsDirection,
						co1, co1IntermediateImage,	// QNeg, QPos, 
						co2, co2IntermediateImage,	// RNeg, RPos,
						null,	// don't need the GlensHologram returned
						false,	// diagnosticInfo
						diagonalWindows,	// parent,
						boxCloak.getStudio()	// studio
					)
			);
		Vector3D
			co3IntermediateImage = outsideGlens.getFiniteImagePosition(co3, ImagingDirection.POS2NEG),
			co4IntermediateImage = outsideGlens.getFiniteImagePosition(co4, ImagingDirection.POS2NEG);
		diagonalWindows.addSceneObject(
				getConvexPolygonalGlens(
						"diagonal 2 "+description,	// description,
						c3, ci3, ci4, c4,	// corners
						direction,	// outwardsDirection,
						co3, co3IntermediateImage,	// QNeg, QPos, 
						co4, co4IntermediateImage,	// RNeg, RPos,
						null,	// don't need the GlensHologram returned
						false,	// diagnosticInfo
						diagonalWindows,	// parent,
						boxCloak.getStudio()	// studio
					)
			);
			

	}
	

	
	//
	// the frames
	//

	private void addFrames()
	{
		// cylinders connecting neighbouring vertices
		addEdges();

		// spheres at the vertices
		addVertices();
	}

	private void addEdges()
	{
		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, boxCloak.getFrames(), boxCloak.getStudio());

		// the edges of the outer cube...
		EditableSceneObjectCollection collection = new EditableSceneObjectCollection("outer cube edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left cube edge", tlfoVertex, tlboVertex, collection);
		addFrameCylinder("top right cube edge", trfoVertex, trboVertex, collection);
		addFrameCylinder("top front cube edge", tlfoVertex, trfoVertex, collection);
		addFrameCylinder("top back cube edge", tlboVertex, trboVertex, collection);
		addFrameCylinder("bottom left cube edge", blfoVertex, blboVertex, collection);
		addFrameCylinder("bottom right cube edge", brfoVertex, brboVertex, collection);
		addFrameCylinder("bottom front cube edge", blfoVertex, brfoVertex, collection);
		addFrameCylinder("bottom back cube edge", blboVertex, brboVertex, collection);
		addFrameCylinder("front left cube edge", tlfoVertex, blfoVertex, collection);
		addFrameCylinder("front right cube edge", trfoVertex, brfoVertex, collection);
		addFrameCylinder("back left cube edge", tlboVertex, blboVertex, collection);
		addFrameCylinder("back right cube edge", trboVertex, brboVertex, collection);
		
		edges.addSceneObject(collection);

		// ... and the edges of the inner cube...
		collection = new EditableSceneObjectCollection("inner cube edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left cube edge", tlfiPVertex, tlbiPVertex, collection);
		addFrameCylinder("top right cube edge", trfiPVertex, trbiPVertex, collection);
		addFrameCylinder("top front cube edge", tlfiPVertex, trfiPVertex, collection);
		addFrameCylinder("top back cube edge", tlbiPVertex, trbiPVertex, collection);
		addFrameCylinder("bottom left cube edge", blfiPVertex, blbiPVertex, collection);
		addFrameCylinder("bottom right cube edge", brfiPVertex, brbiPVertex, collection);
		addFrameCylinder("bottom front cube edge", blfiPVertex, brfiPVertex, collection);
		addFrameCylinder("bottom back cube edge", blbiPVertex, brbiPVertex, collection);
		addFrameCylinder("front left cube edge", tlfiPVertex, blfiPVertex, collection);
		addFrameCylinder("front right cube edge", trfiPVertex, brfiPVertex, collection);
		addFrameCylinder("back left cube edge", tlbiPVertex, blbiPVertex, collection);
		addFrameCylinder("back right cube edge", trbiPVertex, brbiPVertex, collection);
		
		edges.addSceneObject(collection);

		// ... and the diagonal edges
		collection = new EditableSceneObjectCollection("diagonal edges", true, edges, boxCloak.getStudio());

		addFrameCylinder("top left front diagonal edge", tlfoVertex, tlfiPVertex, collection);
		addFrameCylinder("top left back diagonal edge", tlboVertex, tlbiPVertex, collection);
		addFrameCylinder("top right front diagonal edge", trfoVertex, trfiPVertex, collection);
		addFrameCylinder("top right back diagonal edge", trboVertex, trbiPVertex, collection);
		addFrameCylinder("bottom left front diagonal edge", blfoVertex, blfiPVertex, collection);
		addFrameCylinder("bottom left back diagonal edge", blboVertex, blbiPVertex, collection);
		addFrameCylinder("bottom right front diagonal edge", brfoVertex, brfiPVertex, collection);
		addFrameCylinder("bottom right back diagonal edge", brboVertex, brbiPVertex, collection);
		
		edges.addSceneObject(collection);
			
		boxCloak.getFrames().addSceneObject(edges);
	}	
//	
//	private void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
//	{
//		collection.addSceneObject(new EditableParametrisedCylinder(
//				description,
//				startPosition,	// start point
//				endPosition,	// end point
//				boxCloak.getFrameRadiusOverSideLength() * boxCloak.getSideLength(),	// radius
//				boxCloak.getFrameSurfaceProperty(),
//				collection,
//				boxCloak.getStudio()
//		));
//	}
	
	/**
	 * add spheres at the vertices of the outside cube
	 * @param vertices
	 */
	private void addVertices()
	{
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, boxCloak.getFrames(), boxCloak.getStudio());

		// the corners of the outer cube...
		EditableSceneObjectCollection collection = new EditableSceneObjectCollection("outer cube vertices", true, vertices, boxCloak.getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfoVertex, collection);
		addFrameSphere("sphere in top right back cube vertex", trboVertex, collection);
		addFrameSphere("sphere in top left front cube vertex", tlfoVertex, collection);
		addFrameSphere("sphere in top left back cube vertex", tlboVertex, collection);
		addFrameSphere("sphere in bottom right front cube vertex", brfoVertex, collection);
		addFrameSphere("sphere in bottom right back cube vertex", brboVertex, collection);
		addFrameSphere("sphere in bottom left front cube vertex", blfoVertex, collection);
		addFrameSphere("sphere in bottom left back cube vertex", blboVertex, collection);

		vertices.addSceneObject(collection);

		// ... and the corners of the inner cube...
		collection = new EditableSceneObjectCollection("inner cube vertices", true, vertices, boxCloak.getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfiPVertex, collection);
		addFrameSphere("sphere in top right back cube vertex", trbiPVertex, collection);
		addFrameSphere("sphere in top left front cube vertex", tlfiPVertex, collection);
		addFrameSphere("sphere in top left back cube vertex", tlbiPVertex, collection);
		addFrameSphere("sphere in bottom right front cube vertex", brfiPVertex, collection);
		addFrameSphere("sphere in bottom right back cube vertex", brbiPVertex, collection);
		addFrameSphere("sphere in bottom left front cube vertex", blfiPVertex, collection);
		addFrameSphere("sphere in bottom left back cube vertex", blbiPVertex, collection);

		vertices.addSceneObject(collection);
		
		boxCloak.getFrames().addSceneObject(vertices);
	}

//	private void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
//	{
//		collection.addSceneObject(new EditableScaledParametrisedSphere(
//				description,
//				centrePosition,	// centre
//				boxCloak.getFrameRadiusOverSideLength() * boxCloak.getSideLength(),	// radius
//				boxCloak.getFrameSurfaceProperty(),
//				collection,
//				boxCloak.getStudio()
//		));
//	}
}
