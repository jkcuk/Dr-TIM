package optics.raytrace.sceneObjects;

import java.io.*; 

import math.*;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A cuboid.
 * 
 */
public class Cuboid
// extends SceneObjectContainer
extends SceneObjectIntersectionSimple
implements Serializable
{
	private static final long serialVersionUID = -2810197482613591343L;

	private Vector3D centre;
	private Vector3D centre2centreOfFace1;
	private Vector3D centre2centreOfFace2;
	private Vector3D centre2centreOfFace3;
	private SurfaceProperty surfaceProperty;

	/**
	 * Note that this constructor doesn't check that the three directions are perpendicular to each other...
	 * @param description
	 * @param centre
	 * @param centre2centreOfFace1
	 * @param centre2centreOfFace2
	 * @param centre2centreOfFace3
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public Cuboid(
			String description,
			Vector3D centre,
			Vector3D centre2centreOfFace1,
			Vector3D centre2centreOfFace2,
			Vector3D centre2centreOfFace3,
			SurfaceProperty surfaceProperty,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, parent, studio);
		
		setCentre(centre);
		setCentre2centreOfFace1(centre2centreOfFace1);
		setCentre2centreOfFace2(centre2centreOfFace2);
		setCentre2centreOfFace3(centre2centreOfFace3);
		setSurfaceProperty(surfaceProperty);
		
		addSceneObjects();
	}
	
	public Cuboid(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"cuboid",	// description
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// centre to centre of face 1
				new Vector3D(0, 1, 0),	// centre to centre of face 1
				new Vector3D(0, 0, 1),	// centre to centre of face 1
				SurfaceColour.YELLOW_SHINY,	// surface property
				parent,
				studio
			);
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public Cuboid(Cuboid original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getCentre2centreOfFace1(),
				original.getCentre2centreOfFace2(),
				original.getCentre2centreOfFace3(),
				original.getSurfaceProperty(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public Cuboid clone()
	{
		return new Cuboid(this);
	}

	
	//
	// setters & getters
	//
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getCentre2centreOfFace1() {
		return centre2centreOfFace1;
	}

	public void setCentre2centreOfFace1(Vector3D centre2centreOfFace1) {
		this.centre2centreOfFace1 = centre2centreOfFace1;
	}

	public Vector3D getCentre2centreOfFace2() {
		return centre2centreOfFace2;
	}

	public void setCentre2centreOfFace2(Vector3D centre2centreOfFace2) {
		this.centre2centreOfFace2 = centre2centreOfFace2;
	}

	public Vector3D getCentre2centreOfFace3() {
		return centre2centreOfFace3;
	}

	public void setCentre2centreOfFace3(Vector3D centre2centreOfFace3) {
		this.centre2centreOfFace3 = centre2centreOfFace3;
	}

	public SurfaceProperty getSurfaceProperty() {
		return surfaceProperty;
	}

	public void setSurfaceProperty(SurfaceProperty surfaceProperty) {
		this.surfaceProperty = surfaceProperty;
	}
	
	
	//
	
	public void addSceneObjects()
	{
		clear();
		
		addPlane("Side 1", centre2centreOfFace1, centre2centreOfFace2, centre2centreOfFace3);
		addPlane("Side 4", centre2centreOfFace1.getReverse(), centre2centreOfFace2.getReverse(), centre2centreOfFace3.getReverse());

		addPlane("Side 2", centre2centreOfFace2, centre2centreOfFace3, centre2centreOfFace1);
		addPlane("Side 5", centre2centreOfFace2.getReverse(), centre2centreOfFace3.getReverse(), centre2centreOfFace1.getReverse());

		addPlane("Side 3", centre2centreOfFace3, centre2centreOfFace1, centre2centreOfFace2);
		addPlane("Side 6", centre2centreOfFace3.getReverse(), centre2centreOfFace1.getReverse(), centre2centreOfFace2.getReverse());
	}

	private void addPlane(
			String description,
			Vector3D cuboidCentre2faceCentre,
			Vector3D otherVector1,
			Vector3D otherVector2
		)
	{
//		addSceneObject(
//				new EditableScaledParametrisedCentredParallelogram(
//						description, 
//						Vector3D.sum(centre, cuboidCentre2faceCentre),	// centre
//						otherVector1.getProductWith(2),	// spanVector1
//						otherVector2.getProductWith(2),	// spanVector2
//						surfaceProperty,
//						getParent(),
//						getStudio()
//					)
//			);
		
		Vector3D v1 = cuboidCentre2faceCentre;
//		Vector3D v2 = otherVector1.getPartPerpendicularTo(v1);
//		Vector3D v3 = otherVector2.getPartPerpendicularTo(v1);
		
		addSceneObject(
//				new EditableParametrisedPlane(
//						"Side 1",	// description,
//						Vector3D.sum(centre, v1),	// pointOnPlane
//						v2,	// span vector 1
//						v3.getWithLength(Math.signum(Vector3D.scalarProduct(v1, Vector3D.crossProduct(v2, v3)))),	// span vector 2; reverse sign if resulting normal points "inwards"
//						surfaceProperty,
//						getParent(),
//						getStudio()
//					)
				new Plane(
						description,
						Vector3D.sum(centre, v1),	// pointOnPlane
						v1,	// normal
						surfaceProperty,
						getParent(),
						getStudio()
					)
				);
	}
	
	@Override
	public String getType()
	{
		return "Cuboid";
	}


}
