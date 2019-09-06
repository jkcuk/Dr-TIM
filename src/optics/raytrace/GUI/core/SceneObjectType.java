package optics.raytrace.GUI.core;

import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.Editable4PiLens;
import optics.raytrace.GUI.sceneObjects.EditableArray;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableBoxCloak;
import optics.raytrace.GUI.sceneObjects.EditableCLAs;
import optics.raytrace.GUI.sceneObjects.EditableCameraShape;
import optics.raytrace.GUI.sceneObjects.EditableChenBelinBlackHole;
import optics.raytrace.GUI.sceneObjects.EditableChristmasTree;
import optics.raytrace.GUI.sceneObjects.EditableComplexThinLens;
import optics.raytrace.GUI.sceneObjects.EditableCuboid;
import optics.raytrace.GUI.sceneObjects.EditableCylinderFrame;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableEatonLens;
import optics.raytrace.GUI.sceneObjects.EditableFramedGCLAMappingGoggles;
import optics.raytrace.GUI.sceneObjects.EditableFramedUSAFTestChart;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableFresnelLens;
import optics.raytrace.GUI.sceneObjects.EditableGCLAsTardisWindow;
import optics.raytrace.GUI.sceneObjects.EditableGGRINLens;
import optics.raytrace.GUI.sceneObjects.EditableGlens;
import optics.raytrace.GUI.sceneObjects.EditableHomogeneousPlanarImagingSurfaceSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableLensStar;
import optics.raytrace.GUI.sceneObjects.EditableLuneburgLens;
import optics.raytrace.GUI.sceneObjects.EditableLuneburgLensMetric;
import optics.raytrace.GUI.sceneObjects.EditableMaxwellFisheyeLens;
import optics.raytrace.GUI.sceneObjects.EditableMirroredSpaceCancellingWedge;
import optics.raytrace.GUI.sceneObjects.EditableNegativeSpaceWedgeStar;
import optics.raytrace.GUI.sceneObjects.EditableNetOfHypercube;
import optics.raytrace.GUI.sceneObjects.EditableNetOfSymmetric4Simplex;
import optics.raytrace.GUI.sceneObjects.EditableNinkyNonkSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableSpaceCancellingWedge;
import optics.raytrace.GUI.sceneObjects.EditableObjectCoordinateSystem;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCone;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedConvexPolygon;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedParaboloid;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedPlane;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditablePinchTransformationWindow;
import optics.raytrace.GUI.sceneObjects.EditablePlatonicLens;
import optics.raytrace.GUI.sceneObjects.EditablePlatonicSolid;
import optics.raytrace.GUI.sceneObjects.EditablePolarToCartesianConverter;
import optics.raytrace.GUI.sceneObjects.EditableRayRotationSheetStar;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryCone;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectoryHyperboloid;
import optics.raytrace.GUI.sceneObjects.EditableRectangularLensletArray;
import optics.raytrace.GUI.sceneObjects.EditableRochesterCloak;
import optics.raytrace.GUI.sceneObjects.EditableSantaSilhouette;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedCentredParallelogram;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.GUI.sceneObjects.EditableSpaceShiftingPlane;
import optics.raytrace.GUI.sceneObjects.EditableSphericalCap;
import optics.raytrace.GUI.sceneObjects.EditableTelescope;
import optics.raytrace.GUI.sceneObjects.EditableText;
import optics.raytrace.GUI.sceneObjects.EditableThinLens;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.GUI.sceneObjects.EditableTriangularPrism;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Rainbow;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.Transparent;

/**
 * An enum of scene-object types available in TIM's interactive version.
 * To add a new scene-object type:
 * 1) Add the type, with its description, to the enum values.
 * 2) Extend the getNewSceneObject method so that it can instantiate a scene object of the new type, with default parameters.
 * 
 * @author johannes
 */
public enum SceneObjectType
{
	FOUR_PI_LENS("4Pi lens"),
	ARRAY("Array of scene objects"),
	ARROW("Arrow"),
	BOX_CLOAK("Box cloak"),	// commented out temporarily
	CAMERA("Camera"),
	CHEN_BELIN_BLACK_HOLE("Chen-Belin black hole"),
	CHRISTMAS_TREE("Christmas tree"),
	CLAS("Confocal lenslet arrays"),
	CLOAK("Cloak"),
	COLLECTION("Collection/union/intersection of scene objects"),
	COMPLEX_LENS("Complex lens"),
	CONE("Cone"),
	CONVEX_POLYGON("Convex polygon"),
	CUBOID("Cuboid"),
	CYLINDER("Cylinder"),
	DISC("Disc"),
	EATON_LENS("Eaton lens"),
	EXTENDED_ROCHESTER_CLOAK("Extended Rochester cloak"),	// commented out temporarily
	FRAME("Frame"),
	FRESNEL_LENS("Fresnel lens"),
	GCLAS_TO_SIMPLICIAL_COMPLEX("GCLAs TO simplicial complex"),
	GLENS("Glens"),	// commented out temporarily
	// GLENS_CLOAK("Glens cloak"),	// the glens cloak is now part of the box cloak
	GGRIN_LENS("GGRIN lens"),
	IDEAL_LENS_CLOAK("Ideal-lens cloak"),
	LATTICE("Lattice"),
	// LENS("Lens"),
	LENS_STAR("Lens star"),
	LENS_TO_SIMPLICIAL_COMPLEX("Lens TO simplicial complex"),
	LENSLET_ARRAY_HOLOGRAM("Lenslet-array hologram"),
	LUNEBURG_LENS("Luneburg lens"),
	LUNEBURG_LENS_METRIC("Luneburg lens (metric)"),
	MAXWELL_FISHEYE_LENS("Maxwell fisheye lens"),
	NEGATIVE_SPACE_WEDGE_STAR("Negative-space-wedge star"),
	NET_OF_SYMMETRIC_4_SIMPLEX("Net of symmetric 4-simplex"),
	NET_OF_HYPERCUBE("Net of hypercube"),
	NINKY_NONK_SILHOUETTE("Ninky-Nonk silhouette"),
	NULL_SPACE_WEDGE("Null-space wedge"),
	COORDINATES("Object's local coordinate system"),
	PARABOLOID("Paraboloid"),
	PINCH_TRANSFORMATION_WINDOW("Pinch-transformation window"),
	PLANE("Plane"),
	PLATONIC_LENS("Platonic lens"),
	PLATONIC_SOLID("Platonic solid"),
	POLAR_TO_CARTESIAN_CONVERTER("Polar-to-Cartesian converter"),
	RAINBOW_PLANE("Rainbow plane"),
	RAY_ROTATION_SHEET_STAR("Ray-rotation-sheet star"),
	RAY_TRAJECTORY("Ray trajectory"),
	// RAY_TRAJECTORY_THROUGH_COMPLEX_POSITION("Ray trajectory through complex position"),
	RAY_TRAJECTORY_CONE("Ray-trajectory cone"),
	RAY_TRAJECTORY_HYPERBOLOID("Ray-trajectory hyperboloid"),
	RECTANGLE("Rectangle"),
	SANTA_SILHOUETTE("Santa silhouette"),
	SPACE_CANCELLING_WEDGE("Space-cancelling wedge"),
	SPACE_SHIFTING_PLANE("Space-shifting plane"),
	SPHERE("Sphere"),
	SPHERICAL_CAP("Spherical cap"),
	TARDIS_WINDOW("Tardis window"),
	TELESCOPE("Telescope"),
	TEXT("Text"),
	THIN_LENS("Thin lens"),
	TIM_HEAD("Tim's head"),
	TRIANGLE("Triangle"),
	TRIANGULAR_PRISM("Triangular prism"),
	USAF_TEST_CHART("USAF test chart"),
	VIEW_MAPPING_GOGGLES("View-mapping goggles"),
	WINDOW("Window");
	
	private String description;
	private SceneObjectType(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
	
	/**
	 * @param sceneObjectType
	 * @param parent
	 * @param studio
	 * @return	a scene object of the given type, with default parameters
	 */
	public static SceneObject getDefaultSceneObject(SceneObjectType sceneObjectType, SceneObject parent, Studio studio)
	{
		switch(sceneObjectType)
		{
		case ARRAY:
			EditableArray newArray = new EditableArray(
					"array", 
					-1, 1, 1,	// x_min, x_max, dx
					-0.5, 1.5, 1,	// y_min, y_max, dy
					-1, 1, 1,	// z_min, z_max, dz
					new SceneObjectContainer("in array", parent, studio),
					parent,	// parent
					studio	// studio
				);
			newArray.addSceneObjectToUnitCell(
					new EditableScaledParametrisedSphere(
							"sphere -- replace with objects you want to form unit cell",
							new Vector3D(0, 0, 10),	// centre
							0.5,	// radius
							new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
							parent,
							studio
					)
				);
			return(newArray);
		case ARROW:
			return(new EditableArrow(
					"Arrow",
					new Vector3D(0,0,10),	// start point
					new Vector3D(1,0,10),	// end point
					0.05,	// shaft radius
					0.2,	// tip length
					MyMath.deg2rad(30),	// tip angle
					new SurfaceColour(DoubleColour.WHITE, DoubleColour.WHITE, true),
					parent,
					studio
			));
		case BOX_CLOAK:
			return(new EditableBoxCloak(
					parent,	// parent, 
					studio	// studio
				));
		case CAMERA:
			return(
					new EditableCameraShape(
							parent,	// parent
							studio
						)
					);
		case CHEN_BELIN_BLACK_HOLE:
			return(new EditableChenBelinBlackHole(parent, studio));
//		case CLOAK:
//			return(new EditableGCLAsCloak(
//					parent,	// parent, 
//					studio	// studio
//				));
		case CHRISTMAS_TREE:
			return(new EditableChristmasTree(
					parent,
					studio
				));
		case CLAS:
			return new EditableCLAs(
					CLAS.toString(),	// description
					parent, 
					studio
					);
		case COLLECTION:
			EditableSceneObjectCollection newCollection = new EditableSceneObjectCollection("collection", true, parent, studio);
			newCollection.addSceneObject(
					new EditableTimHead(
							"Tim's head -- replace with the objects you want in this collection",
							new Vector3D(0, 0, 10),	// centre
							1,	// radius
							new Vector3D(0, 0, -1),	// front direction
							new Vector3D(0, 1, 0),	// top direction
							new Vector3D(1, 0, 0),	// right direction
							newCollection,
							studio
					)
				);
			return(newCollection);
		case COMPLEX_LENS:
			return(new EditableComplexThinLens(
					parent,
					studio
				));
		case CONE:
			return(new EditableParametrisedCone(
					"cone",
					new Vector3D(0, 0, 10),	// apex
					new Vector3D(0, 0, 1),	// axis
					false,	// open
					MyMath.deg2rad(45),	// cone angle
					1,	// height
					SurfaceColour.BLUE_SHINY,
					parent,
					studio
			));
		case CONVEX_POLYGON:
			return(new EditableParametrisedConvexPolygon(parent, studio));
		case COORDINATES:
			return(new EditableObjectCoordinateSystem(
					"Object's local coordinate system",	// description
					(One2OneParametrisedObject)null,	// object
					new Vector2D(0, 0),	// coordinates
					0.025,	// shaftRadius
					MyMath.deg2rad(30),	// tipAngle
					0.1,	// tipLength
					new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),	//  surfacePropertyU
					new SurfaceColour(DoubleColour.GREEN, DoubleColour.WHITE, true),	//  surfacePropertyV
					new SurfaceColour(DoubleColour.BLUE, DoubleColour.WHITE, true),	//  surfacePropertyN
					parent,	// parent 
					studio
				));
		case CUBOID:
			return new EditableCuboid(parent, studio);
		case CYLINDER:
			return(new EditableParametrisedCylinder(
					"cylinder",
					new Vector3D(-1, 0, 10),	// start point
					new Vector3D(1, 0, 10),	// end point
					1,	// radius
					SurfaceColour.BLUE_SHINY,
					parent,
					studio
			));
		case DISC:
			return(new EditableScaledParametrisedDisc(
					"disc",
					new Vector3D(0, 0, 10),	// centre
					new Vector3D(0, 0, 1),	// normal to "outside"
					1,	// radius
					SurfaceColour.RED_SHINY,
					parent,
					studio
			));
		case EATON_LENS:
			return(new EditableEatonLens(
					"Eaton lens",	// description
					new Vector3D(0, 0, 10),	// centre
					1,	// radius
					1,	// refractive-index ratio
					0,	// radius of transparant tunnel
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmission coefficient
					true, 	// shadow-throwing
					parent,	// parent 
					studio
				));
		case EXTENDED_ROCHESTER_CLOAK:
			return(new EditableRochesterCloak(parent, studio));
		case FOUR_PI_LENS:
			return(new Editable4PiLens(parent, studio));
		case FRAME:
			return(new EditableCylinderFrame(
					"cylinder frame",
					new Vector3D(-1.5, -1, 10),
					new Vector3D(3, 0, 0),
					new Vector3D(0, 2, 0),
					0.025,
					SurfaceColour.GREY50_SHINY,
					parent,
					studio
			));
		case FRESNEL_LENS:
			return(new EditableFresnelLens(parent, studio));
		case GCLAS_TO_SIMPLICIAL_COMPLEX:
			return(new EditableHomogeneousPlanarImagingSurfaceSimplicialComplex(
					"GCLAs TO simplicial complex",	// description
					parent,	// parent
					studio
				));
		case GLENS:
			// return(new EditableSimpleGlens(parent, studio);
			return(new EditableGlens(parent, studio));
//		case GLENS_CLOAK:
//			return(new EditableGlensCloak(parent, studio));
		case GGRIN_LENS:
			return(new EditableGGRINLens(
					"GGRIN lens",	// description
					new Vector3D(0, 0, 10),	// centre
					1,	// radius
					1,	// r1
					1,	// r2
					0,	// alpha
					1,	// refractive-index ratio
					0,	// transparent-tunnel radius
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmission coefficient
					true, 	// shadow-throwing
					parent,	// parent 
					studio
				));
		case IDEAL_LENS_CLOAK:
			return(new EditableIdealLensCloak(parent, studio));
		case LATTICE:
			return(new EditableCylinderLattice(
					"cylinder lattice",
					-1, 1, 4,
					-1, 1, 4,
					10, 25, 4,
					0.02,
					parent,
					studio
			));
//		case LENS:
//			return(new EditableThickLens(
//					"lens",
//					1,	// aperture radius
//					2,	// radius of curvature front
//					2,	// radius of curvature back
//					new Vector3D(0, 0, 10),	// centre
//					new Vector3D(0, 0, -1),	// direction to front
//					new Refractive(1.5, 1, true),	// front surface property
//					new Refractive(1.5, 1, true),	// back surface property
//					parent,
//					studio
//			));
		case LENS_STAR:
			return(new EditableLensStar(parent, studio));
		case LENS_TO_SIMPLICIAL_COMPLEX:
			return(new EditableLensSimplicialComplex(
					"Lens TO simplicial complex",	// description
					parent,	// parent
					studio
				));
		case LENSLET_ARRAY_HOLOGRAM:
			return new EditableRectangularLensletArray(parent, studio);
		case LUNEBURG_LENS:
			return(new EditableLuneburgLens(
					"Luneburg lens",	// description
					new Vector3D(0, 0, 10),	// centre
					1,	// radius
					1,	// refractive-index ratio
					0,	// radius of transparent tunnel
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmission coefficient
					true,	// shadow-throwing
					parent,	// parent 
					studio
				));
		case LUNEBURG_LENS_METRIC:
			return(new EditableLuneburgLensMetric(parent, studio));
		case MAXWELL_FISHEYE_LENS:
			return(new EditableMaxwellFisheyeLens(
					"Maxwell fisheye lens",	// description
					new Vector3D(0, 0, 10),	// centre
					1,	// radius
					1,	// refractive-index ratio
					0,	// radius of transparent tunnel
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmission coefficient
					true,	// shadow-throwing
					parent,	// parent 
					studio
				));
		case NEGATIVE_SPACE_WEDGE_STAR:
			return new EditableNegativeSpaceWedgeStar(parent, studio);
		case NET_OF_SYMMETRIC_4_SIMPLEX:
			// return new EditableNetOfRegular4Simplex(parent, studio);
			return new EditableNetOfSymmetric4Simplex(parent, studio);
		case NET_OF_HYPERCUBE:
			return new EditableNetOfHypercube(parent, studio);
		case NINKY_NONK_SILHOUETTE:
			return(new EditableNinkyNonkSilhouette(parent, studio));
		case NULL_SPACE_WEDGE:
			return new EditableSpaceCancellingWedge(parent, studio);
		case PARABOLOID:
			return(new EditableParametrisedParaboloid("Paraboloid", parent, studio));
		case PINCH_TRANSFORMATION_WINDOW:
			return(new EditablePinchTransformationWindow(parent, studio));
		case PLANE:
			return(new EditableParametrisedPlane(
					"plane",
					new Vector3D(0, 0, 10),	// point on plane
					new Vector3D(0, 0, 1),	// normal
					SurfaceColour.GREEN_SHINY,
					parent,
					studio
			));
		case PLATONIC_LENS:
			return(new EditablePlatonicLens(parent, studio));
		case PLATONIC_SOLID:
			return(new EditablePlatonicSolid(parent, studio));
		case POLAR_TO_CARTESIAN_CONVERTER:
			return(new EditablePolarToCartesianConverter(
					"Polar-to-Cartesian converter",	// description
					new Vector3D(0, 0, 10),	// centre
					new Vector3D(0, 0, -1),	// polarFaceNormal
					new Vector3D(1, 0, 0),	// phi0Direction
					new Vector3D(1, 0, 0),	// xDirection,
					2,	// sideLength
					parent,	// parent 
					studio
				));
		case RAINBOW_PLANE:
			return(new EditableParametrisedPlane(
					"Rainbow plane",
					new Vector3D(0, 0, 100),	// point on plane
					new Vector3D(0, 0, 1),	// normal
					new Rainbow(
							1,	// saturation
							.25,	// lightness
							new Vector3D(100,300,-500)	// lightSourcePosition
						),
					parent,
					studio
			));
		case RAY_ROTATION_SHEET_STAR:
			return(new EditableRayRotationSheetStar(parent, studio));
		case RAY_TRAJECTORY:
			return(new EditableRayTrajectory(
					"ray trajectory",
					new Vector3D(-1,0,10),	// start point
					0,	// start time
					new Vector3D(0,0,1),	// initial ray direction
					0.02,
					// make the colour ultra-bright red
					new SurfaceColour(new DoubleColour(2, 0, 0), new DoubleColour(2, 2, 2), true),
					100,
					false,	// reportToConsole
					parent,
					studio
			));
//		case RAY_TRAJECTORY_THROUGH_COMPLEX_POSITION:
//			return(new EditableRayTrajectory(
//					"ray trajectory through complex position",
//					new Vector3D(-1,0,10),	// start point
//					0,	// start time
//					new Vector3D(0,0,10),	// real part of complex position
//					new Vector3D(0, 0, 0),	// imaginary part of complex position
//					0.02,
//					new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),
//					100,
//					parent,
//					studio
//			));
		case RAY_TRAJECTORY_CONE:
			return(new EditableRayTrajectoryCone(
					"ray-trajectory cone",
					new Vector3D(0,0,8),	// start point
					0,	// start time
					new Vector3D(0,0,1),	// direction of cone axis
					MyMath.deg2rad(20),	// cone angle
					10,	// number of rays
					0.02,	// ray radius
					new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),
					100,	// max. trace level
					parent,
					studio
			));
		case RAY_TRAJECTORY_HYPERBOLOID:
			return(new EditableRayTrajectoryHyperboloid(
					"ray-trajectory hyperboloid",
					new Vector3D(0,0,8),	// start point
					0,	// start time
					new Vector3D(0,0,1),	// direction of cone axis
					MyMath.deg2rad(20),	// cone angle
					1,	// waist radius
					10,	// number of rays
					0.02,	// ray radius
					new SurfaceColour(DoubleColour.RED, DoubleColour.WHITE, true),
					100,	// max. trace level
					parent,
					studio
			));
		case RECTANGLE:
			return(new EditableScaledParametrisedCentredParallelogram(
					"rectangle",
					new Vector3D(0, 0, 10),	// centre
					new Vector3D(3, 0, 0),	// width vector
					new Vector3D(0, 2, 0),	// height vector
					new RayRotating(90*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
					parent,
					studio
			));
		case SANTA_SILHOUETTE:
			return(new EditableSantaSilhouette(parent, studio));
		case SPACE_CANCELLING_WEDGE:
			return new EditableMirroredSpaceCancellingWedge(parent, studio);
		case SPACE_SHIFTING_PLANE:
			return(new EditableSpaceShiftingPlane(parent, studio));
		case SPHERE:
			return(new EditableScaledParametrisedSphere(
					"sphere",
					new Vector3D(0, 0, 10),	// centre
					1,	// radius
					SurfaceColour.WHITE_SHINY,
					parent,
					studio
			));
		case SPHERICAL_CAP:
			return(new EditableSphericalCap(
					"spherical cap",
					new Vector3D(0, 0, 10),	// cap centre
					new Vector3D(0, 0, 15),	// sphere centre
					1,	// aperture radius
					false, // closed?
					SurfaceColour.WHITE_SHINY,
					parent,
					studio
			));
		case TARDIS_WINDOW:
			return(new EditableGCLAsTardisWindow(
					parent,	// parent 
					studio
				));
		case TELESCOPE:
			return(new EditableTelescope(
					"Telescope",	// description
					new Vector3D(0, 0, 10),	// centre
					new Vector3D(0, 0, -1),	// ocularNormal
					1,	// magnification
					1,	// radius of aperture
					parent,	// parent 
					studio
				));
		case TEXT:
			return(new EditableText(parent, studio));
		case THIN_LENS:
			return(new EditableThinLens(
					"thin lens",
					new Vector3D(0, 0, 10),	// centre
					new Vector3D(0, 0, -1),	// direction of optical axis
					1,	// radius
					1,	// focal length,
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmission coefficient
					true,	// shadow-throwing
					parent,
					studio
			));
		case TIM_HEAD:
			return(new EditableTimHead(
					parent,
					studio
			));
		case TRIANGLE:
			return(new EditableParametrisedTriangle(
					"Triangle",
					new Vector3D(0, 1, 10),	// corner1
					new Vector3D(1, -2, 0),	// corner 1 to corner 2
					new Vector3D(-1, -2, 0),	// corner 1 to corner 3
					false,	// semi-infinite
					new Vector3D(1, 0, 0),	// u unit vector
					new Vector3D(0, 1, 0),	// v unit vector
					SurfaceColour.DARK_RED_SHINY,	// surface property
					parent,	// parent
					studio	// studio
			));
		case TRIANGULAR_PRISM:
			return(new EditableTriangularPrism(parent, studio));
		case USAF_TEST_CHART:
			return(new EditableFramedUSAFTestChart(
				"USAF test chart",	// description
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 1, 0),	// up
				new Vector3D(0, 0, -1),	// front
				1,	// width
				SurfaceColour.RED_SHINY,	// frameSurfaceProperty
				false,	// showFrame
				parent, 
				studio
			));
		case VIEW_MAPPING_GOGGLES:
			return(new EditableFramedGCLAMappingGoggles(parent, studio));
		case WINDOW:
		default:
			return(new EditableFramedRectangle(
					"framed window",
					new Vector3D(-.5, -.5, 5),
					new Vector3D(1, 0, 0),
					new Vector3D(0, 1, 0),
					0.025,	// frame radius
					new Transparent(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, true),
					SurfaceColour.GREY50_SHINY,
					true,	// show frame
					parent,
					studio
			));
		}

	}
}
