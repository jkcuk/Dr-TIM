package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import math.*;
import optics.DoubleColour;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedTriangle;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A simple cat head.
 * The head radius is 1.
 */
public class CatHead extends SceneObjectContainer implements Serializable
{	
	private static final long serialVersionUID = -1972118406111096523L;
	
	
	private Vector3D centre;
	private Vector3D frontDirection;
	private Vector3D topDirection;
	private double radius;
	private DoubleColour headColour, noseColour, rightEyeColour, leftEyeColour, whiskerColour, innerEarColour;

	public EditableScaledParametrisedSphere head;
	public EditableSceneObjectCollection nose;
	public EditableSceneObjectCollection leftEye, rightEye;
 	public EditableSceneObjectCollection leftEar, rightEar;
 	public EditableSceneObjectCollection whiskers;

	public CatHead(
			String description, 
			Vector3D centre, 
			double radius, 
			Vector3D frontDirection, 
			Vector3D topDirection, 
			DoubleColour headColour,
			DoubleColour noseColour,
			DoubleColour innerEarColour,
			DoubleColour rightEyeColour,		
			DoubleColour leftEyeColour,
			DoubleColour whiskerColour,
			SceneObject parent, 
			Studio studio)
	{
		super(description, parent, studio);
		
		setCentre(centre);
		setRadius(radius);
		setFrontDirection(frontDirection);
		setTopDirection(topDirection);
		setHeadColour(headColour);
		setNoseColour(noseColour);
		setInnerEarColour(innerEarColour);
		setRightEyeColour(rightEyeColour);
		setLeftEyeColour(leftEyeColour);
		setWhiskerColour(whiskerColour);

		// add the scene objects
		addElements();
	}

	public CatHead(String description, Vector3D centre, SceneObject parent, Studio studio)
	{
		this(
				description,
				centre, 
				1,	// radius
				new Vector3D(0, 0, -1),	// frontDirection
				new Vector3D(0, 1, 0), 	// topDirection,
				DoubleColour.BLACK,// headColour,
				DoubleColour.SKIN,// noseColour,
				DoubleColour.SKIN,// innerEarColour
				new DoubleColour("green-Cyan", 0.32, 1.12, 1.04),// rightEyeColour,		
				new DoubleColour("green-Cyan", 0.32, 1.12, 1.04),// leftEyeColour,
				DoubleColour.WHITE,// whiskerColour,
				parent,
				studio
			);
	}

	
	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public CatHead(CatHead original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getRadius(),
				original.getFrontDirection(),
				original.getTopDirection(),
				original.getHeadColour(),
				original.getNoseColour(),
				original.getInnerEarColour(),
				original.getRightEyeColour(),
				original.getLeftEyeColour(),
				original.getWhiskerColour(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public CatHead clone()
	{
		return new CatHead(this);
	}
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector3D getFrontDirection() {
		return frontDirection;
	}

	public void setFrontDirection(Vector3D frontDirection) {
		this.frontDirection = frontDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	public void setTopDirection(Vector3D topDirection) {
		this.topDirection = topDirection;
	}
	
	public DoubleColour getHeadColour() {
		return headColour;
	}

	public void setHeadColour(DoubleColour headColour) {
		this.headColour = headColour;
	}

	public DoubleColour getNoseColour() {
		return noseColour;
	}

	public void setNoseColour(DoubleColour noseColour) {
		this.noseColour = noseColour;
	}
	
	public DoubleColour getInnerEarColour() {
		return innerEarColour;
	}

	public void setInnerEarColour(DoubleColour innerEarColour) {
		this.innerEarColour = innerEarColour;
	}

	public DoubleColour getRightEyeColour() {
		return rightEyeColour;
	}

	public void setRightEyeColour(DoubleColour rightEyeColour) {
		this.rightEyeColour = rightEyeColour;
	}

	public DoubleColour getLeftEyeColour() {
		return leftEyeColour;
	}

	public void setLeftEyeColour(DoubleColour leftEyeColour) {
		this.leftEyeColour = leftEyeColour;
	}

	public DoubleColour getWhiskerColour() {
		return whiskerColour;
	}

	public void setWhiskerColour(DoubleColour whiskerColour) {
		this.whiskerColour = whiskerColour;
	}

	public EditableScaledParametrisedSphere getHead() {
		return head;
	}

	public void setHead(EditableScaledParametrisedSphere head) {
		// remove the old head
		removeSceneObject(this.head);
		
		// ... and add the new one
		this.head = head;
		addSceneObject(head);
	}

	public EditableSceneObjectCollection getNose() {
		return nose;
	}

	public void setNose(EditableSceneObjectCollection nose) {
		// remove the old nose
		removeSceneObject(this.nose);
		
		// ... and add the new one
		this.nose = nose;
		addSceneObject(nose);
	}
 	
	public EditableSceneObjectCollection getLeftEye() {
		return leftEye;
	}

	public void setLeftEye(EditableSceneObjectCollection leftEye) {
		// remove the old left eye
		removeSceneObject(this.leftEye);
		
		// ... and add the new one
		this.leftEye = leftEye;
		addSceneObject(leftEye);
	}

	public EditableSceneObjectCollection getRightEye() {
		return rightEye;
	}

	public void setRightEye(EditableSceneObjectCollection rightEye) {
		// remove the old right eye
		removeSceneObject(this.rightEye);
		
		// ... and add the new one
		this.rightEye = rightEye;
		addSceneObject(rightEye);
	}

	public EditableSceneObjectCollection getLeftEar() {
		return leftEar;
	}

	public void setLeftEar(EditableSceneObjectCollection leftEar) {
		// remove the old cap
		removeSceneObject(this.leftEar);
		
		// ... and add the new one
		this.leftEar = leftEar;
		addSceneObject(leftEar);
	}
	
	public EditableSceneObjectCollection getRightEar() {
		return rightEar;
	}

	public void setRightEar(EditableSceneObjectCollection rightEar) {
		// remove the old cap
		removeSceneObject(this.rightEar);
		
		// ... and add the new one
		this.rightEar = rightEar;
		addSceneObject(rightEar);
	}

	public EditableSceneObjectCollection getWhiskers() {
		return whiskers;
	}

	public void setWhiskers(EditableSceneObjectCollection whiskers) {
		removeSceneObject(this.whiskers);
		
		this.whiskers = whiskers;
		addSceneObject(whiskers);
	}

	public void addElements()
	{
		clear();
		
		// first create a coordinate system from the given directions
		ArrayList<Vector3D> basis = new ArrayList<Vector3D>(3);
		basis.add(Vector3D.crossProduct(topDirection, frontDirection.getPartPerpendicularTo(topDirection).getWithLength(-1)).getNormalised());
		basis.add(topDirection.getNormalised());
		basis.add(frontDirection.getPartPerpendicularTo(topDirection).getWithLength(-1));
		
		
		//
		//Adding the head
		//
		
		head = new EditableScaledParametrisedSphere(
				"head", centre, radius, 
				new SurfaceColour("headColour", headColour, DoubleColour.BLACK, true),
				this, getStudio());
		
		//
		//Adding the eyes
		//
		//first define a slightly larger head sphere for both the iris and the pupil these will need to be separate
		Sphere leftIrisSphere = new Sphere(
				"neagtive iris sphere",// description, 
				centre,// centre, 
				radius+MyMath.TINY,// radius,
				new SurfaceColour("leftEyeColour", leftEyeColour, DoubleColour.WHITE, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		
		Sphere rightIrisSphere = new Sphere(
				"neagtive iris sphere",// description, 
				centre,// centre, 
				radius+MyMath.TINY,// radius,
				new SurfaceColour("rightEyeColour", rightEyeColour, DoubleColour.WHITE, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		
		Sphere pupilSphere = new Sphere(
				"neagtive pupil sphere",// description, 
				centre,// centre, 
				radius+2*MyMath.TINY,// radius,
				SurfaceColour.BLACK_SHINY,// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		
		
		
		SceneObjectPrimitiveIntersection leftEyePupil = new SceneObjectPrimitiveIntersection("Left eye pupil", this ,getStudio());
		SceneObjectPrimitiveIntersection leftEyeIris = new SceneObjectPrimitiveIntersection("Left eye iris", this ,getStudio());
		//First get the centre of the eye...
		Vector3D leftEyeCentre = Vector3D.sum(centre, Vector3D.sum(Vector3D.X.getProductWith(1.15).fromBasis(basis),Vector3D.Y.getProductWith(0.7).fromBasis(basis),Vector3D.Z.getProductWith(-2.2).fromBasis(basis)).getWithLength(radius));
		//now set up an axis along which the iris oval will lie...
		Vector3D leftEyeTop = Vector3D.sum(centre, Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(1*radius));
		Vector3D leftEyeZAxis = Vector3D.difference(leftEyeCentre, centre).getNormalised();
		Vector3D leftEyeXAxis = Vector3D.difference(leftEyeTop, leftEyeCentre).getNormalised();
		Vector3D leftEyeYAxis = Vector3D.crossProduct(leftEyeXAxis, leftEyeZAxis);
		
		
		
		Ellipsoid leftEyeIrisPos=	new Ellipsoid(
				"Left iris",// description,
				leftEyeCentre,// centre,
				leftEyeXAxis.getProductWith(radius*0.35),// a,
				leftEyeZAxis.getProductWith(radius*0.5),// b,
				leftEyeYAxis.getProductWith(radius*0.25),// c,
				new SurfaceColour("leftEyeColour", leftEyeColour, DoubleColour.WHITE, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		leftEyeIris.addPositiveSceneObjectPrimitive(leftEyeIrisPos);
		leftEyeIris.addPositiveSceneObjectPrimitive(leftIrisSphere);
		
		Ellipsoid leftEyePupilPos = new Ellipsoid(			
				"Left pupil",// description,
				leftEyeCentre,// centre,
				Vector3D.Y.getProductWith(radius*0.23),// a,
				Vector3D.Z.getProductWith(-1).getProductWith(radius*0.2),// b,
				//leftEyeZAxis.getProductWith(radius*0.1),// b,
				leftEyeYAxis.getProductWith(radius*0.2),// c,
				//Vector3D.Y.getProductWith(radius*0.27),// a,
				SurfaceColour.BLACK_SHINY,// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		
		leftEyePupil.addPositiveSceneObjectPrimitive(leftEyePupilPos);
		leftEyePupil.addPositiveSceneObjectPrimitive(pupilSphere);
		
		
		leftEye = new EditableSceneObjectCollection(new SceneObjectContainer("Left eye", this, getStudio()), true);
		leftEye.addSceneObject(leftEyePupil);
		leftEye.addSceneObject(leftEyeIris);

		
		SceneObjectPrimitiveIntersection rightEyePupil = new SceneObjectPrimitiveIntersection("Right eye pupil", this ,getStudio());
		SceneObjectPrimitiveIntersection rightEyeIris = new SceneObjectPrimitiveIntersection("Right eye iris", this ,getStudio());
		//First get the centre of the eye...
		Vector3D rightEyeCentre = Vector3D.sum(centre, Vector3D.sum(Vector3D.X.getProductWith(-1.15).fromBasis(basis),Vector3D.Y.getProductWith(0.7).fromBasis(basis),Vector3D.Z.getProductWith(-2.2).fromBasis(basis)).getWithLength(radius));
		//now set up an axis along which the iris oval will lie...
		Vector3D rightEyeTop = Vector3D.sum(centre, Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(1*radius));
		Vector3D rightEyeZAxis = Vector3D.difference(rightEyeCentre, centre).getNormalised();
		Vector3D rightEyeXAxis = Vector3D.difference(rightEyeTop, rightEyeCentre).getNormalised();
		Vector3D rightEyeYAxis = Vector3D.crossProduct(rightEyeXAxis, rightEyeZAxis);
		
		
		
		Ellipsoid rightEyeIrisPos=	new Ellipsoid(
				"right iris",// description,
				rightEyeCentre,// centre,
				rightEyeXAxis.getProductWith(radius*0.35),// a,
				rightEyeZAxis.getProductWith(radius*0.5),// b,
				rightEyeYAxis.getProductWith(radius*0.25),// c,
				new SurfaceColour("rightEyeColour", rightEyeColour, DoubleColour.WHITE, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		rightEyeIris.addPositiveSceneObjectPrimitive(rightEyeIrisPos);
		rightEyeIris.addPositiveSceneObjectPrimitive(rightIrisSphere);
		
		Ellipsoid rightEyePupilPos = new Ellipsoid(			
				"right pupil",// description,
				rightEyeCentre,// centre,
				Vector3D.Y.getProductWith(radius*0.23),// a,
				Vector3D.Z.getProductWith(-1).getProductWith(radius*0.2),// b,
				//leftEyeZAxis.getProductWith(radius*0.1),// b,
				rightEyeYAxis.getProductWith(radius*0.2),// c,
				//Vector3D.Y.getProductWith(radius*0.27),// a,
				SurfaceColour.BLACK_SHINY,// surfaceProperty,
				this,// parent,
				getStudio()// studio
			);
		
		rightEyePupil.addPositiveSceneObjectPrimitive(rightEyePupilPos);
		rightEyePupil.addPositiveSceneObjectPrimitive(pupilSphere);
		
		
		rightEye = new EditableSceneObjectCollection(new SceneObjectContainer("right eye", this, getStudio()), true);
		rightEye.addSceneObject(rightEyePupil);
		rightEye.addSceneObject(rightEyeIris);
		
		//
		//Adding the nose
		//
		
		Vector3D noseVertex1, noseVertex2, noseVertex3, noseVertex4;
		noseVertex1 = Vector3D.sum(centre,Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(radius/9.)); //Inside, slightly below the centre of the cat.
		noseVertex2 = Vector3D.sum(Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(radius/6.) ,Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius+MyMath.TINY),noseVertex1); //front top left vertex
		noseVertex3 = Vector3D.sum(Vector3D.X.fromBasis(basis).getWithLength(radius/6.) ,Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius+MyMath.TINY),noseVertex1); //front top right vertex
		noseVertex4 = Vector3D.sum(Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(radius*(0.2887)) ,Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius+MyMath.TINY),noseVertex1); //front bottom vertex 
		
		nose = new EditableSceneObjectCollection(new SceneObjectContainer("nose", this, getStudio()), true);
		nose.addSceneObject( EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"nose part1",
				noseVertex1,// vertex1,
				noseVertex2,// vertex2,
				noseVertex3,// vertex3,
				new SurfaceColour("noseColour", noseColour, DoubleColour.WHITE, true),
				this,
				getStudio()
				)
			);
		nose.addSceneObject( EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"nose part1",
				noseVertex1,// vertex1,
				noseVertex3,// vertex2,
				noseVertex4,// vertex3,
				new SurfaceColour("noseColour", noseColour, DoubleColour.WHITE, true),
				this,
				getStudio()
				)
			);
		nose.addSceneObject( EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"nose part1",
				noseVertex1,// vertex1,
				noseVertex2,// vertex2,
				noseVertex4,// vertex3,
				new SurfaceColour("noseColour", noseColour, DoubleColour.WHITE, true),
				this,
				getStudio()
				)
			);
		nose.addSceneObject( EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"nose part1",
				noseVertex2,// vertex1,
				noseVertex3,// vertex2,
				noseVertex4,// vertex3,
				new SurfaceColour("noseColour", noseColour, DoubleColour.WHITE, true),
				this,
				getStudio()
				)
			);
		
		
		//
		//Adding the whiskers
		//
		
		whiskers = new EditableSceneObjectCollection(new SceneObjectContainer("Whiskers", this, getStudio()), true);
		EditableParametrisedCylinder leftTopWhisker = new EditableParametrisedCylinder(
				"top left whisker",// description,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1) ,//startPoint,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1, Vector3D.sum(Vector3D.X.fromBasis(basis).getWithLength(1.25*radius), Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(0.2*radius))),// endPoint,
				radius*0.01,// radius,
				new SurfaceColour("whisker colour", whiskerColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,
				getStudio()
		);
		
		EditableParametrisedCylinder leftBottomWhisker = new EditableParametrisedCylinder(
				"Bottom left whisker",// description,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1) ,//startPoint,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1, Vector3D.sum(Vector3D.X.fromBasis(basis).getWithLength(1.15*radius), Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(0.4*radius))),// endPoint,
				radius*0.01,// radius,
				new SurfaceColour("whisker colour", whiskerColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,
				getStudio()
		);
		
		EditableParametrisedCylinder rightTopWhisker = new EditableParametrisedCylinder(
				"top right whisker",// description,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1) ,//startPoint,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1, Vector3D.sum(Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(1.25*radius), Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(0.2*radius))),// endPoint,
				radius*0.01,// radius,
				new SurfaceColour("whisker colour", whiskerColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,
				getStudio()
		);
		
		EditableParametrisedCylinder rightBottomWhisker = new EditableParametrisedCylinder(
				"Bottom right whisker",// description,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1) ,//startPoint,
				Vector3D.sum(Vector3D.Z.getProductWith(-1).fromBasis(basis).getWithLength(radius*0.9),noseVertex1, Vector3D.sum(Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(1.15*radius), Vector3D.Y.getProductWith(-1).fromBasis(basis).getWithLength(0.4*radius))),// endPoint,
				radius*0.01,// radius,
				new SurfaceColour("whisker colour", whiskerColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,
				getStudio()
		);
		
		whiskers.addSceneObject(leftTopWhisker);
		whiskers.addSceneObject(leftBottomWhisker);
		whiskers.addSceneObject(rightTopWhisker);
		whiskers.addSceneObject(rightBottomWhisker);
		
		
		
		//
		// Adding the ears
		//
		
		//ears cutting plane
		Plane cuttingPlane = new Plane(
				"ear cutting plane",
				Vector3D.sum(centre, Vector3D.Z.fromBasis(basis).getWithLength(MyMath.TINY)),//Point on plane
				Vector3D.Z.getProductWith(-1).fromBasis(basis),//Normal to plane 
				new SurfaceColour("headColour", headColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
				);
		leftEar = new EditableSceneObjectCollection(new SceneObjectContainer("Left ear", this, getStudio()), true);
		SceneObjectPrimitiveIntersection leftEarShape = new SceneObjectPrimitiveIntersection("leftEarShape", this, getStudio());
		//create the left ear cone and split it using a plane. 
		Vector3D leftEarTip = Vector3D.sum(centre, Vector3D.X.getProductWith(1).fromBasis(basis).getWithLength(radius), Vector3D.Y.fromBasis(basis).getWithLength(radius*1.4));// apex,
		ConeTop leftEarOuline = new ConeTop(
				"outter part of the ear",// description,
				leftEarTip,// apex,
				Vector3D.difference(centre, leftEarTip).getNormalised(),// axis,
				MyMath.deg2rad(25),// theta,
				1.5*radius,// height, 
				new SurfaceColour("headColour", headColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
				);
		
		leftEarShape.addPositiveSceneObjectPrimitive(leftEarOuline);
		leftEarShape.addPositiveSceneObjectPrimitive(cuttingPlane);
		//now add the inner ear part and done.
		leftEar.addSceneObject(leftEarShape);
		leftEar.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"left inner ear",
				Vector3D.sum(centre, Vector3D.X.getProductWith(1).fromBasis(basis).getWithLength(radius*11/15.), Vector3D.Y.fromBasis(basis).getWithLength(radius*77/75.)),// vertex1,
				Vector3D.sum(centre, Vector3D.X.getProductWith(1).fromBasis(basis).getWithLength(0.5*radius)),// vertex2,
				Vector3D.sum(centre, Vector3D.Y.fromBasis(basis).getWithLength(0.6*radius)),// vertex3,
				new SurfaceColour("earColour", innerEarColour, DoubleColour.BLACK, true),// surfaceProperty,
				//SurfaceColour.SKIN_MATT, 
				this,
				getStudio()
				)
			);
		
		rightEar = new EditableSceneObjectCollection(new SceneObjectContainer("Right ear", this, getStudio()), true);
		SceneObjectPrimitiveIntersection rightEarShape = new SceneObjectPrimitiveIntersection("rigthEarShape", this, getStudio());
		//create the right ear cone and split it using a plane. 
		Vector3D rightEarTip = Vector3D.sum(centre, Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(radius), Vector3D.Y.fromBasis(basis).getWithLength(radius*1.4));// apex,
		ConeTop rightEarOuline = new ConeTop(
				"outter part of the ear",// description,
				rightEarTip,// apex,
				Vector3D.difference(centre, rightEarTip).getNormalised(),// axis,
				MyMath.deg2rad(25),// theta,
				1.5*radius,// height
				new SurfaceColour("headColour", headColour, DoubleColour.BLACK, true),// surfaceProperty,
				this,// parent,
				getStudio()// studio
				);
		rightEarShape.addPositiveSceneObjectPrimitive(rightEarOuline);
		rightEarShape.addPositiveSceneObjectPrimitive(cuttingPlane);
		//now add the inner ear part and done.
		rightEar.addSceneObject(rightEarShape);
		rightEar.addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
				"Right inner ear",
				Vector3D.sum(centre, Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(radius*11/15.), Vector3D.Y.fromBasis(basis).getWithLength(radius*77/75.)),// vertex1,
				Vector3D.sum(centre, Vector3D.X.getProductWith(-1).fromBasis(basis).getWithLength(0.5*radius)),// vertex2,
				Vector3D.sum(centre, Vector3D.Y.fromBasis(basis).getWithLength(0.6*radius)),// vertex3,
				new SurfaceColour("earColour", innerEarColour, DoubleColour.BLACK, true),// surfaceProperty,
				//SurfaceColour.SKIN_MATT,
				this,
				getStudio()
				)
			);
		
		
		//
		//Adding all the components together to hopefully make a cat.
		//
		addSceneObject(head);
		addSceneObject(leftEye);
		addSceneObject(rightEye);
		addSceneObject(nose);
		addSceneObject(whiskers);
		addSceneObject(leftEar);
		addSceneObject(rightEar);
	}

	@Override
	public CatHead transform(Transformation t)
	{
		CatHead h = new CatHead(
				getDescription(),
				t.transformPosition(getCentre()),
				getParent(),
				getStudio()
			);
		
		// get rid of all the scene objects in h
		h.clear();
		
		h.setHead(getHead().transform(t));
		h.setNose(getNose().transform(t));
		h.setLeftEye(getLeftEye().transform(t));
		h.setRightEye(getRightEye().transform(t));
	 	h.setWhiskers(getWhiskers().transform(t));
		h.setLeftEar(getLeftEar().transform(t));
		h.setRightEar(getRightEar().transform(t));
		return h;
	}
	
	@Override
	public String getType()
	{
		return "CatHead";
	}
}