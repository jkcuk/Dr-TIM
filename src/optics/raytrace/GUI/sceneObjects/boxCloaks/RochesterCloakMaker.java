package optics.raytrace.GUI.sceneObjects.boxCloaks;

import math.NamedVector3D;
import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedConvexPolygon;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;

/**
 * @author johannes
 * Makes a Rochester cloak [1] (four lenses), made omnidirectional (?) through the addition of glenses.
 * 
 * The innerVolumeSizeFactor parameters describe the *transverse* size of the inner volume in EM space and physical space.
 * 
 * [1] J. S. Choi and J. C. Howell, "Paraxial ray optics cloaking", Opt. Express 22, 29465-29478 (2014)
 */
public class RochesterCloakMaker extends CloakMaker
{
	private static final long serialVersionUID = -5686456376246845604L;


	//
	// constructors
	//
	
	/**
	 * @param boxCloak
	 */
	public RochesterCloakMaker(EditableBoxCloak boxCloak)
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
//		System.out.println("RochesterBoxCloakMaker::addSceneObjects: adding scene objects...");
//		System.out.println("RochesterBoxCloakMaker::addSceneObjects: adding scene objects...");
		
		// calculate the focal lengths of the four lenses that make up the Rochester cloak
		calculateF1F2();
		
		// calculate the windows and the frames
		addWindows();
		addFrames();
	}

	
	//
	// the guts
	//
	
	//
	// the focal lengths of the axial lenses
	//

	protected double
		f1,	// focal length of the outer lenses
		f2;	// focal length of the inner lenses

	
	/**
	 * The inner volume appears stretched by a factor M_T in the transverse direction.
	 * Calculate this factor from the sizes of the inner volume in EM space and physical space.
	 * Note that needs to be M_T < -1 so that the lenses are in the right order
	 * @return	M_T
	 */
	private double getInnerTransverseMagnification()
	{
		return boxCloak.getInnerVolumeSizeFactorEM() / boxCloak.getInnerVolumeSizeFactorP();
	}
	
	/**
	 * from the transverse magnification, M_T, and the side length of the outer cube, L, calculate
	 * the focal lengths of the lenses L1 and L2
	 * 
	 * (L is boxCloak.getSideLength(); M is (innerVolumeSizeFactorEM / innerVolumeSizeFactorP) )
	 */
	private void calculateF1F2()
	{
		double MT = getInnerTransverseMagnification();
		
		// I calculated this in a Mathematica notebook
		f1 = boxCloak.getSideLength()*  (MT+1) /(2*   (MT-1));
		f2 = boxCloak.getSideLength()*(-(MT+1))/(2*MT*(MT-1));
		
		// System.out.println("RochesterBoxCloakMaker::calculateF1F2: f1="+f1+", f2="+f2);
	}

	
	//
	// calculate the corner positions
	//
	
	/**
	 * @param longitudinalDirection
	 * @param transverseDirection1
	 * @param transverseDirection2
	 * @return	the position of the box corner in the given directions (e.g. forward, up, right)
	 */
	private Vector3D calculateOuterCornerPosition(Vector3D longitudinalDirection, Vector3D transverseDirection1, Vector3D transverseDirection2)
	{
		return Vector3D.sum(
				boxCloak.getCentre(),
				longitudinalDirection.getWithLength(0.5*boxCloak.getSideLength()),
				transverseDirection1.getWithLength(0.5*boxCloak.getSideLength()),
				transverseDirection2.getWithLength(0.5*boxCloak.getSideLength())
			);
	}

	/**
	 * @param transverseDirection1
	 * @param transverseDirection2
	 * @return	the position of the half-way vertex in the given directions (e.g. up, right)
	 */
	private Vector3D calculateOuterHalfwayVertexPosition(Vector3D transverseDirection1, Vector3D transverseDirection2)
	{
		double centralBellyFactor = 1.5;
		return Vector3D.sum(
				boxCloak.getCentre(),
				transverseDirection1.getWithLength(centralBellyFactor*0.5*boxCloak.getSideLength()),
				transverseDirection2.getWithLength(centralBellyFactor*0.5*boxCloak.getSideLength())
			);
	}

	/**
	 * The focal lengths f1 and f2 must have been set -- @see calculateF1F2()
	 * @param longitudinalDirection
	 * @param transverseDirection1
	 * @param transverseDirection2
	 * @return	the position of the inner-volume corner in the given directions (e.g. forward, up, right)
	 */
	private Vector3D calculateInnerCornerPosition(Vector3D longitudinalDirection, Vector3D transverseDirection1, Vector3D transverseDirection2)
	{
		return Vector3D.sum(
				boxCloak.getCentre(),
				longitudinalDirection.getWithLength(0.5*boxCloak.getSideLength()-f1-f2),
				transverseDirection1.getWithLength(0.5*boxCloak.getSideLength()*boxCloak.getInnerVolumeSizeFactorP()),
				transverseDirection2.getWithLength(0.5*boxCloak.getSideLength()*boxCloak.getInnerVolumeSizeFactorP())
			);
	}


	
	//
	// the windows
	//
	
	private EditableSceneObjectCollection
		rochesterCloakLenses,	// the four lenses that form the "Rochester cloak"
		outerCubeSurfaces,
		innerCuboidSurfaces,
		frontAndBackDiagonalSurfaces,	// the diagonal surfaces intersecting the front and back edges of the outer cube
		centralDiagonalSurfaces,	// the diagonal surfaces intersecting the "central belt" of the outer cube
		sideDiagonalSurfaces;	// the diagonal surfaces intersecting the transverse (i.e. not front and back) edges of the outer cube
		

	/**
	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
	 * The EditableSceneObjectCollection "windows" must exist.
	 */
	private void addWindows()
	{
//		System.out.println("RochesterCloakMaker::addWindows: adding windows...");
//		System.out.println("");
		
		// first create the EditableSceneObjectCollections where the different windows go...
		rochesterCloakLenses = new EditableSceneObjectCollection("Rochester cloak", true, boxCloak.getWindows(), boxCloak.getStudio());
		outerCubeSurfaces = new EditableSceneObjectCollection("Outer cube", true, boxCloak.getWindows(), boxCloak.getStudio());
		innerCuboidSurfaces = new EditableSceneObjectCollection("Inner cuboid", true, boxCloak.getWindows(), boxCloak.getStudio());
		frontAndBackDiagonalSurfaces = new EditableSceneObjectCollection("Front & back diagonals", true, boxCloak.getWindows(), boxCloak.getStudio());
		centralDiagonalSurfaces = new EditableSceneObjectCollection("Central diagonals", true, boxCloak.getWindows(), boxCloak.getStudio());
		sideDiagonalSurfaces = new EditableSceneObjectCollection("Side diagonals", true, boxCloak.getWindows(), boxCloak.getStudio());
		
		// ... and add them to the EditableSceneObjectCollection "Windows"
		boxCloak.getWindows().addSceneObject(rochesterCloakLenses);
		boxCloak.getWindows().addSceneObject(outerCubeSurfaces);
		boxCloak.getWindows().addSceneObject(innerCuboidSurfaces);
		boxCloak.getWindows().addSceneObject(frontAndBackDiagonalSurfaces);
		boxCloak.getWindows().addSceneObject(centralDiagonalSurfaces);
		boxCloak.getWindows().addSceneObject(sideDiagonalSurfaces);
				
		GlensSurface
			frontOuterLensHologram = new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false),
			backOuterLensHologram = new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false),
			frontInnerLensHologram = new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false),
			backInnerLensHologram = new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false);
		
		// add the four Rochester-cloak lenses
		
		addOnAxisLensesForAxialDirection(
				"front",	// description
				boxCloak.getFrontDirection(),	// front,	// direction, 
				boxCloak.getRightDirection(),	// right,	// otherDirection1, 
				boxCloak.getTopDirection(),	// top	// otherDirection2
				frontOuterLensHologram, frontInnerLensHologram
			);

		addOnAxisLensesForAxialDirection(
				"back",	// description
				boxCloak.getBackDirection(),	// back,	// direction, 
				boxCloak.getLeftDirection(),	// left,	// otherDirection1, 
				boxCloak.getBottomDirection(),	// bottom	// otherDirection2
				backOuterLensHologram, backInnerLensHologram
			);
		
		// and add everything that goes around it

		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getFrontDirection(),	// longitudinal direction, 
				boxCloak.getTopDirection(),	// transverse direction, 
				boxCloak.getRightDirection(),	// otherDirection2
				frontOuterLensHologram,
				frontInnerLensHologram,
				true	// addInnerCuboidSurface
			);
		
		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getFrontDirection(),	// longitudinal direction, 
				boxCloak.getRightDirection(),	// transverse direction, 
				boxCloak.getBottomDirection(),	// otherDirection2
				frontOuterLensHologram,
				frontInnerLensHologram,
				true	// addInnerCuboidSurface
			);
		
		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getFrontDirection(),	// longitudinal direction, 
				boxCloak.getBottomDirection(),	// transverse direction, 
				boxCloak.getLeftDirection(),	// otherDirection2
				frontOuterLensHologram,
				frontInnerLensHologram,
				true	// addInnerCuboidSurface
			);
		
		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getFrontDirection(),	// longitudinal direction, 
				boxCloak.getLeftDirection(),	// transverse direction, 
				boxCloak.getTopDirection(),	// otherDirection2
				frontOuterLensHologram,
				frontInnerLensHologram,
				true	// addInnerCuboidSurface
			);

		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getBackDirection(),	// longitudinal direction, 
				boxCloak.getTopDirection(),	// transverse direction, 
				boxCloak.getLeftDirection(),	// otherDirection2
				backOuterLensHologram,
				backInnerLensHologram,
				false	// addInnerCuboidSurface
			);

		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getBackDirection(),	// longitudinal direction, 
				boxCloak.getLeftDirection(),	// transverse direction, 
				boxCloak.getBottomDirection(),	// otherDirection2
				backOuterLensHologram,
				backInnerLensHologram,
				false	// addInnerCuboidSurface
			);

		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getBackDirection(),	// longitudinal direction, 
				boxCloak.getBottomDirection(),	// transverse direction, 
				boxCloak.getRightDirection(),	// otherDirection2
				backOuterLensHologram,
				backInnerLensHologram,
				false	// addInnerCuboidSurface
			);

		addSurfacesForLongitudinalTransverseDirectionCombo(
				boxCloak.getBackDirection(),	// longitudinal direction, 
				boxCloak.getRightDirection(),	// transverse direction, 
				boxCloak.getTopDirection(),	// otherDirection2
				backOuterLensHologram,
				backInnerLensHologram,
				false	// addInnerCuboidSurface
			);

//		System.out.println("");
//		System.out.println("RochesterCloakMaker::addWindows: Done!");
	}
	
	
	/**
	 * For a given <direction> along the optical axis (i.e. a vector pointing to the front or back),
	 * add the relevant lenses.
	 * There are the outer and inner lens perpendicular to <direction>.
	 * @param description
	 * @param axialDirection
	 * @param transverseDirection1
	 * @param transverseDirection2
	 * @param outerLens	this GlensHologram will be modified to represent the outer lens
	 * @param innerLens	this GlensHologram will be modified to represent the inner lens
	 */
	private void addOnAxisLensesForAxialDirection(
			String description,
			Vector3D axialDirection, Vector3D transverseDirection1, Vector3D transverseDirection2,
			GlensSurface outerLens,
			GlensSurface innerLens
		)
	{
		// outer lens, which has focal length f1
		outerLens.setParametersUsingPrincipalPoint(
				axialDirection,	// front	// opticalAxisDirectionPos; "outside" is therefore in positive space
				Vector3D.sum(boxCloak.getCentre(), axialDirection),	// nodal point,
				-f1, f1	// f-, f+, i.e. focal lengths in negative and positive space
				);
		rochesterCloakLenses.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						description+" outer lens",	// description
						calculateOuterCornerPosition(axialDirection, transverseDirection1, transverseDirection2),	// corner
						transverseDirection1.getProductWith(-2),	// spanVector1,
						transverseDirection2.getProductWith(-2),	// spanVector2, 
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						(boxCloak.isShowPlaceholderSurfaces())
								?(new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false))
								:outerLens,	// surfaceProperty,
						rochesterCloakLenses,	// parent,
						boxCloak.getStudio()	// studio
						)
				);

		// inner lens, which has focal length f2
		Vector3D
			innerLensN = Vector3D.sum(
					boxCloak.getCentre(),
					axialDirection.getWithLength(0.5*boxCloak.getSideLength()-f1-f2)
				);
		innerLens.setParametersUsingPrincipalPoint(
				axialDirection,	// opticalAxisDirectionPos; "outside" is therefore in positive space
				innerLensN,	// nodal point,
				-f2, f2	// focal lengths in negative and positive space
				);
		rochesterCloakLenses.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						description+" inner lens",	// description
						calculateInnerCornerPosition(axialDirection, transverseDirection1, transverseDirection2),	// corner
						transverseDirection1.getProductWith(-2*boxCloak.getInnerVolumeSizeFactorP()),	// spanVector1,
						transverseDirection2.getProductWith(-2*boxCloak.getInnerVolumeSizeFactorP()),	// spanVector2,
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						(boxCloak.isShowPlaceholderSurfaces())
							?(new GlensSurface(boxCloak.getInterfaceTransmissionCoefficient(), false))
							:innerLens,
						rochesterCloakLenses,	// parent,
						boxCloak.getStudio()	// studio
						)
				);
	}
	
	private void addSurfacesForLongitudinalTransverseDirectionCombo(
			NamedVector3D longitudinalDirection, NamedVector3D transverseDirection, NamedVector3D otherTransverseDirection,
			GlensSurface outsideLens,
			GlensSurface insideLens,
			boolean addInnerCuboidSurfaceAndInnerSideSurface
		)
	{		
		// Consider the case of the transverseDirection = top and longitudinalDirection = front.
		
		// The top front glens has to image the inner top corners from EM space to physical space.
		// Call these corners inner corners 1 and 2.
		// The positions of these corners in physical space are as follows:
		Vector3D
			vertex1P = calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection),
			vertex2P = calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse());
		// The EM space positions can be calculated as the images of these positions due to the corresponding outer lens.
		Vector3D
			vertex1E = outsideLens.getFiniteImagePosition(vertex1P, ImagingDirection.NEG2POS),
			vertex2E = outsideLens.getFiniteImagePosition(vertex2P, ImagingDirection.NEG2POS);
		
		// First extract the GlensHologram from the topFrontGlens
		GlensSurface topFrontGlens = new GlensSurface(
				boxCloak.getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
				false
			);
		
		// Calculate the top front glens
		EditableParametrisedConvexPolygon topFrontSurface = getConvexPolygonalGlens(
				longitudinalDirection.getName() + " " + transverseDirection.getName() + " glens",	// description,
				calculateOuterCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
				calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection), 
				calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection.getReverse()), 
				calculateOuterCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse()), // corners
				transverseDirection,	// outwardsDirection,
				vertex1P, vertex1E,	// QNeg, QPos, 
				vertex2P, vertex2E,	// RNeg, RPos,
				topFrontGlens,	// will be set to have the required glens properties
				false,	// diagnosticInfo
				outerCubeSurfaces,	// parent,
				boxCloak.getStudio()	// studio
			);

		// Add the top front outer surface
		outerCubeSurfaces.addSceneObject(topFrontSurface);
		
		// The combination of the top front glens (calculated above) and top front diagonal glens has to image the inner
		// bottom corners from EM space to physical space.
		// Call these corners inner corners 3 and 4.
		// The positions of these corners in physical space are as follows:
		Vector3D
			vertex3P = calculateInnerCornerPosition(longitudinalDirection, transverseDirection.getReverse(), otherTransverseDirection),
			vertex4P = calculateInnerCornerPosition(longitudinalDirection, transverseDirection.getReverse(), otherTransverseDirection.getReverse());
		// The EM space positions can be calculated as the images of these positions due to the corresponding outer lens.
		Vector3D
			vertex3E = outsideLens.getFiniteImagePosition(vertex3P, ImagingDirection.NEG2POS),
			vertex4E = outsideLens.getFiniteImagePosition(vertex4P, ImagingDirection.NEG2POS);
		// The position of vertex 8, which is the same in EM space and in physical space
		Vector3D
			vertex8 = calculateOuterCornerPosition(longitudinalDirection, transverseDirection.getReverse(), otherTransverseDirection);

		// This has to image the EM-space positions to intermediate positions, ...
		Vector3D
			vertex3EI = topFrontGlens.getFiniteImagePosition(vertex3E, ImagingDirection.POS2NEG),
			vertex4EI = topFrontGlens.getFiniteImagePosition(vertex4E, ImagingDirection.POS2NEG),
			vertex8EI = topFrontGlens.getFiniteImagePosition(vertex8,  ImagingDirection.POS2NEG);
		
		// ... which the top-front diagonal surface has to image to their physical-space positions, ...
		frontAndBackDiagonalSurfaces.addSceneObject(
				getConvexPolygonalGlens(
						longitudinalDirection.getName() + " " + transverseDirection.getName() + " diagonal glens",	// description,
						calculateOuterCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
						calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
						calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse()), 
						calculateOuterCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse()), // corners
						transverseDirection,	// neg2posVector,
						vertex3P, vertex3EI,	// QNeg, QPos, 
						vertex4P, vertex4EI,	// RNeg, RPos,
						null,	// don't need to have the GlensHologram returned
						false,	// diagnosticInfo
						frontAndBackDiagonalSurfaces,	// parent,
						boxCloak.getStudio()	// studio
						)
				);

		// the top-front-right diagonal surface also images the intermediate position of corner 3 to its physical-space position;
		// it also images the similarly created intermediate image of corner 8 to itself
		sideDiagonalSurfaces.addSceneObject(
				getConvexPolygonalGlens(
						longitudinalDirection.getName() + " " + transverseDirection.getName() + " " +
						otherTransverseDirection.getName() + " diagonal glens",	// description,
						calculateOuterCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
						calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
						calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection), // corners
						transverseDirection,	// neg2posVector,
						vertex3P, vertex3EI,	// QNeg, QPos, 
						vertex8,  vertex8EI,	// RNeg, RPos,
						null,	// don't need to have the GlensHologram returned
						false,	// diagnosticInfo
						sideDiagonalSurfaces,	// parent,
						boxCloak.getStudio()	// studio
						)
				);

		// my lovely daughter typed the following line
		// hello my name is bea
		
		// the top centre diagonal surface
		
		// The combination of the top front glens (calculated above) and top centre diagonal glens has to image the
		// "other" inner top corners from EM space to physical space.
		// Call these corners inner corners 5 and 6.
		// The positions of these corners in physical space are as follows:
		Vector3D
			vertex5P = calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection, otherTransverseDirection),
			vertex6P = calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection, otherTransverseDirection.getReverse());
		
//		System.out.println("RochesterCloakMaker::addSurfacesForLongitudinalTransverseDirectionCombo: "+
//				longitudinalDirection.getName() + " " + transverseDirection.getName() +
//				", vertex5P="+vertex5P+
//				", vertex6P="+vertex6P
//				);

		// The EM space positions can be calculated as the images of these positions due to the corresponding outer lens.
		Vector3D
			vertex5E = outsideLens.getFiniteImagePosition(insideLens.getImagePosition(vertex5P, ImagingDirection.NEG2POS), ImagingDirection.NEG2POS),
			vertex6E = outsideLens.getFiniteImagePosition(insideLens.getImagePosition(vertex6P, ImagingDirection.NEG2POS), ImagingDirection.NEG2POS);

//		System.out.println("RochesterCloakMaker::addSurfacesForLongitudinalTransverseDirectionCombo: "+
//				longitudinalDirection.getName() + " " + transverseDirection.getName() +
//				", vertex5E="+vertex5E+
//				", vertex6E="+vertex6E
//				);

		// The top-front glens has to image the EM-space positions to intermediate positions, ...
		Vector3D
			vertex5EI = topFrontGlens.getFiniteImagePosition(vertex5E, ImagingDirection.POS2NEG),
			vertex6EI = topFrontGlens.getFiniteImagePosition(vertex6E, ImagingDirection.POS2NEG);

//		System.out.println("RochesterCloakMaker::addSurfacesForLongitudinalTransverseDirectionCombo: Calculating " +
//				longitudinalDirection.getName() + " " + transverseDirection.getName() + " centre diagonal glens...");
		
		// First extract the GlensHologram from the centreDiagonalGlens
		GlensSurface centreDiagonalGlens = new GlensSurface(
				boxCloak.getInterfaceTransmissionCoefficient(),	// transmissionCoefficient,
				false
			);

		// ... which the top-front diagonal surface has to image to their physical-space positions, ...
		EditableParametrisedConvexPolygon centreDiagonalSurface =
				getConvexPolygonalGlens(
						longitudinalDirection.getName() + " " + transverseDirection.getName() + " centre diagonal glens",	// description,
						calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection), 
						calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
						calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse()), 
						calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection.getReverse()), // corners
						transverseDirection,	// neg2posVector,
						vertex5P, vertex5EI,	// QNeg, QPos, 
						vertex6P, vertex6EI,	// RNeg, RPos,
						centreDiagonalGlens,	// the parameters of this GlensHologram will be set to image as required
						true,	// diagnosticInfo
						centralDiagonalSurfaces,	// parent,
						boxCloak.getStudio()	// studio
					);
		centralDiagonalSurfaces.addSceneObject(centreDiagonalSurface);

//		System.out.println("RochesterCloakMaker::addSurfacesForLongitudinalTransverseDirectionCombo: Done!");

		if(addInnerCuboidSurfaceAndInnerSideSurface)
		{
			// the inner top surface

			// The combination of the outer lens, inner lens, and inner top surface has to image the
			// top outer-halfway vertex positions back to themselves.
			// Call these half-way vertices 1 and 2.
			// The positions of these corners in physical and EM space are as follows:
			Vector3D
			halfWayVertex1 = calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection),
			halfWayVertex2 = calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection.getReverse());

			// The top-front glens has to image the EM-space positions to intermediate positions, ...
			Vector3D
			halfWayVertex1I = outsideLens.getFiniteImagePosition(insideLens.getImagePosition(halfWayVertex1, ImagingDirection.NEG2POS), ImagingDirection.NEG2POS),
			halfWayVertex2I = outsideLens.getFiniteImagePosition(insideLens.getImagePosition(halfWayVertex2, ImagingDirection.NEG2POS), ImagingDirection.NEG2POS);

			// ... which the top inner surface has to image to their physical-space positions, ...
			innerCuboidSurfaces.addSceneObject(
					getConvexPolygonalGlens(
							transverseDirection.getName() + " inner glens",	// description,
							calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
							calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection, otherTransverseDirection), 
							calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection, otherTransverseDirection.getReverse()), 
							calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection.getReverse()), // corners
							transverseDirection,	// neg2posVector,
							halfWayVertex1I, halfWayVertex1,	// QNeg, QPos, 
							halfWayVertex2I, halfWayVertex2,	// RNeg, RPos,
							null,	// don't need to have the GlensHologram returned
							false,	// diagnosticInfo
							innerCuboidSurfaces,	// parent,
							boxCloak.getStudio()	// studio
							)
					);
			
			// The EM space positions can be calculated as the images of these positions due to the corresponding outer lens.
			Vector3D
				vertex7P = calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection.getReverse(), otherTransverseDirection),
				vertex7E = outsideLens.getFiniteImagePosition(insideLens.getImagePosition(vertex7P, ImagingDirection.NEG2POS), ImagingDirection.NEG2POS);

			Vector3D
				vertex3EI2 = centreDiagonalGlens.getImagePosition(topFrontGlens.getImagePosition(vertex3E, ImagingDirection.POS2NEG), ImagingDirection.POS2NEG),
				vertex7EI2 = centreDiagonalGlens.getImagePosition(topFrontGlens.getImagePosition(vertex7E, ImagingDirection.POS2NEG), ImagingDirection.POS2NEG);
			
			// ... and so does the front-side diagonal surface
			sideDiagonalSurfaces.addSceneObject(
					getConvexPolygonalGlens(
							transverseDirection.getName() + " " + otherTransverseDirection.getName() + " centre diagonal glens",	// description,
							calculateInnerCornerPosition(longitudinalDirection, transverseDirection, otherTransverseDirection), 
							calculateInnerCornerPosition(longitudinalDirection.getReverse(), transverseDirection, otherTransverseDirection), 
							calculateOuterHalfwayVertexPosition(transverseDirection, otherTransverseDirection), // corners
							transverseDirection,	// neg2posVector,
							vertex3P, vertex3EI2,	// QNeg, QPos, 
							vertex7P, vertex7EI2,	// RNeg, RPos,
							null,	// don't need to have the GlensHologram returned
							false,	// diagnosticInfo
							sideDiagonalSurfaces,	// parent,
							boxCloak.getStudio()	// studio
							)
					);

		}
	}
	

	private EditableSceneObjectCollection
		edges, vertices;
	
	//
	// the frames
	//

	private void addFrames()
	{
		edges = new EditableSceneObjectCollection("Edges", true, boxCloak.getFrames(), boxCloak.getStudio());
		vertices = new EditableSceneObjectCollection("Vertices", true, boxCloak.getFrames(), boxCloak.getStudio());
		
		addFrameElementsForAxialDirection("front", boxCloak.getFrontDirection(), true);
		addFrameElementsForAxialDirection("back", boxCloak.getBackDirection(), false);
			
		boxCloak.getFrames().addSceneObject(edges);
		boxCloak.getFrames().addSceneObject(vertices);
	}
	
	/**
	 * For a given <direction> along the optical axis (i.e. a vector pointing to the front or back),
	 * add the relevant frame cylinders.
	 * @param description
	 * @param axialDirection
	 * @param transverseDirection1
	 * @param transverseDirection2
	 * @param addSymmetricElements
	 * 
	 */
	private void addFrameElementsForAxialDirection(
			String description,
			Vector3D axialDirection,
			boolean addSymmetricElements
		)
	{
		// corners of outer lens
		Vector3D
			otr = calculateOuterCornerPosition(axialDirection, boxCloak.getTopDirection(), boxCloak.getRightDirection()),	// Vector3D.sum(cO, topRightDirection),
			otl = calculateOuterCornerPosition(axialDirection, boxCloak.getTopDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cO, topLeftDirection),
			obl = calculateOuterCornerPosition(axialDirection, boxCloak.getBottomDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cO, topRightDirection.getReverse()),
			obr = calculateOuterCornerPosition(axialDirection, boxCloak.getBottomDirection(), boxCloak.getRightDirection());	// Vector3D.sum(cO, topLeftDirection.getReverse());

		// add the cylinders
		addFrameCylinder(description+" outer top cylinder", otr, otl, edges);
		addFrameCylinder(description+" outer left cylinder", otl, obl, edges);
		addFrameCylinder(description+" outer bottom cylinder", obl, obr, edges);
		addFrameCylinder(description+" outer right cylinder", obr, otr, edges);
		
		// add the vertices
		addFrameSphere(description + " outer top right sphere", otr, vertices);
		addFrameSphere(description + " outer top left sphere", otl, vertices);
		addFrameSphere(description + " outer bottom left sphere", obl, vertices);
		addFrameSphere(description + " outer bottom right sphere", obr, vertices);


		// corners of inner lens
		Vector3D
			itr = calculateInnerCornerPosition(axialDirection, boxCloak.getTopDirection(), boxCloak.getRightDirection()),	// Vector3D.sum(cI, topRightDirection.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			itl = calculateInnerCornerPosition(axialDirection, boxCloak.getTopDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cI, topLeftDirection.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
			ibl = calculateInnerCornerPosition(axialDirection, boxCloak.getBottomDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cI, topRightDirection.getProductWith(-boxCloak.getInnerVolumeSizeFactorP())),
			ibr = calculateInnerCornerPosition(axialDirection, boxCloak.getBottomDirection(), boxCloak.getRightDirection());	// Vector3D.sum(cI, topLeftDirection.getProductWith(-boxCloak.getInnerVolumeSizeFactorP()));

		// add the cylinders
		addFrameCylinder(description+" inner top cylinder", itr, itl, edges);
		addFrameCylinder(description+" inner left cylinder", itl, ibl, edges);
		addFrameCylinder(description+" inner bottom cylinder", ibl, ibr, edges);
		addFrameCylinder(description+" inner right cylinder", ibr, itr, edges);

		// add the vertices
		addFrameSphere(description + " inner top right sphere", itr, vertices);
		addFrameSphere(description + " inner top left sphere", itl, vertices);
		addFrameSphere(description + " inner bottom left sphere", ibl, vertices);
		addFrameSphere(description + " inner bottom right sphere", ibr, vertices);
		
		// add the diagonal cylinders between the inner and outer corners
		addFrameCylinder(description+" top right diagonal cylinder", otr, itr, edges);
		addFrameCylinder(description+" top left diagonal cylinder", otl, itl, edges);
		addFrameCylinder(description+" bottom left diagonal cylinder", obl, ibl, edges);
		addFrameCylinder(description+" bottom right diagonal cylinder", obr, ibr, edges);
		
		// vertices half-way along outer longitudinal edges
		Vector3D
			htr = calculateOuterHalfwayVertexPosition(boxCloak.getTopDirection(), boxCloak.getRightDirection()),	// Vector3D.sum(boxCloak.getCentre(), topRightDirection),
			htl = calculateOuterHalfwayVertexPosition(boxCloak.getTopDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(boxCloak.getCentre(), topLeftDirection),
			hbl = calculateOuterHalfwayVertexPosition(boxCloak.getBottomDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(boxCloak.getCentre(), topRightDirection.getReverse()),
			hbr = calculateOuterHalfwayVertexPosition(boxCloak.getBottomDirection(), boxCloak.getRightDirection());	// Vector3D.sum(boxCloak.getCentre(), topLeftDirection.getReverse());
		
		// add the diagonal cylinders between the inner corners and the half-way vertices
		addFrameCylinder(description+" top right diagonal middle cylinder", htr, itr, edges);
		addFrameCylinder(description+" top left diagonal middle cylinder", htl, itl, edges);
		addFrameCylinder(description+" bottom left diagonal middle cylinder", hbl, ibl, edges);
		addFrameCylinder(description+" bottom right diagonal middle cylinder", hbr, ibr, edges);
		
		if(addSymmetricElements)
		{
			// add the central vertices
			addFrameSphere("half-way top right sphere", htr, vertices);
			addFrameSphere("half-way top left sphere", htl, vertices);
			addFrameSphere("half-way bottom left sphere", hbl, vertices);
			addFrameSphere("half-way bottom right sphere", hbr, vertices);
			
			// add the cylinders that form a square between the half-way vertices
			addFrameCylinder("half-way top cylinder", htr, htl, edges);
			addFrameCylinder("half-way left cylinder", htl, hbl, edges);
			addFrameCylinder("half-way bottom cylinder", hbl, hbr, edges);
			addFrameCylinder("half-way right cylinder", hbr, htr, edges);

			// corners of other outer lens
			Vector3D
				o2tr = calculateOuterCornerPosition(axialDirection.getReverse(), boxCloak.getTopDirection(), boxCloak.getRightDirection()),	// Vector3D.sum(cO2, topRightDirection),
				o2tl = calculateOuterCornerPosition(axialDirection.getReverse(), boxCloak.getTopDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cO2, topLeftDirection),
				o2bl = calculateOuterCornerPosition(axialDirection.getReverse(), boxCloak.getBottomDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cO2, topRightDirection.getReverse()),
				o2br = calculateOuterCornerPosition(axialDirection.getReverse(), boxCloak.getBottomDirection(), boxCloak.getRightDirection());	// Vector3D.sum(cO2, topLeftDirection.getReverse());

			// the cylinders at the longitudinal edges of the outer volume
			addFrameCylinder("outer top right cylinder", otr, o2tr, edges);
			addFrameCylinder("outer top left cylinder", otl, o2tl, edges);
			addFrameCylinder("outer bottom left cylinder", obl, o2bl, edges);
			addFrameCylinder("outer bottom right cylinder", obr, o2br, edges);

			// corners of other inner lens
			Vector3D
				i2tr = calculateInnerCornerPosition(axialDirection.getReverse(), boxCloak.getTopDirection(), boxCloak.getRightDirection()),	// Vector3D.sum(cI2, topRightDirection.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
				i2tl = calculateInnerCornerPosition(axialDirection.getReverse(), boxCloak.getTopDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cI2, topLeftDirection.getProductWith(boxCloak.getInnerVolumeSizeFactorP())),
				i2bl = calculateInnerCornerPosition(axialDirection.getReverse(), boxCloak.getBottomDirection(), boxCloak.getLeftDirection()),	// Vector3D.sum(cI2, topRightDirection.getProductWith(-boxCloak.getInnerVolumeSizeFactorP())),
				i2br = calculateInnerCornerPosition(axialDirection.getReverse(), boxCloak.getBottomDirection(), boxCloak.getRightDirection());	// Vector3D.sum(cI2, topLeftDirection.getProductWith(-boxCloak.getInnerVolumeSizeFactorP()));

			// the cylinders at the longitudinal edges of the inner volume
			addFrameCylinder("inner top right cylinder", itr, i2tr, edges);
			addFrameCylinder("inner top left cylinder", itl, i2tl, edges);
			addFrameCylinder("inner bottom left cylinder", ibl, i2bl, edges);
			addFrameCylinder("inner bottom right cylinder", ibr, i2br, edges);
		}
	}
}
