package optics.raytrace.sceneObjects;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedSphere;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.Refractive;

/**
 * @author johannes
 * A lens with a triangular aperture and spherical surfaces, both with the same radius of curvature.
 */
public class TriangularThickLens extends SceneObjectIntersectionSimple
{
	private static final long serialVersionUID = -4838063942026633127L;

	private double focalLength;
	private Vector3D principalPoint;	// assumed to lie in the plane spanned by the three corners
	private Vector3D corner1;
	private Vector3D corner2;
	private Vector3D corner3;
	private double refractiveIndex;
	private double thicknessAtThinnestPoint;
	
	/**
	 * @param description
	 * @param focalLength
	 * @param principalPoint
	 * @param corner1
	 * @param corner2
	 * @param corner3
	 * @param refractiveIndex
	 * @param thicknessAtThinnestPoint
	 * @param parent
	 * @param studio
	 * @throws SceneException	if the lens surface's radius of curvature is less than the aperture radius
	 */
	public TriangularThickLens(
			String description,
			double focalLength, Vector3D principalPoint, Vector3D corner1, Vector3D corner2, Vector3D corner3,
			double refractiveIndex,
			double thicknessAtThinnestPoint,
			SceneObject parent, 
			Studio studio
		)
	throws SceneException
	{
		// constructor of superclass
		super(description, parent, studio);

		this.focalLength = focalLength;
		this.principalPoint = principalPoint;
		this.corner1 = corner1;
		this.corner2 = corner2;
		this.corner3 = corner3;
		this.refractiveIndex = refractiveIndex;
		this.thicknessAtThinnestPoint = thicknessAtThinnestPoint;
		
		populateSceneObjectCollection();
	}
	
	/**
	 * @param original
	 * @throws SceneException	if the lens surface's radius of curvature is less than the aperture radius, but this should never happen as the original lens came into existence somehow
	 */
	public TriangularThickLens(TriangularThickLens original) throws SceneException
	{
		this(
				original.getDescription(),
				original.getFocalLength(),
				original.getPrincipalPoint(),
				original.getCorner1().clone(),
				original.getCorner2().clone(),
				original.getCorner3().clone(),
				original.getRefractiveIndex(),
				original.getThicknessAtThinnestPoint(),
				original.getParent(),
				original.getStudio()
			);
	}
	
	@Override
	public TriangularThickLens clone()
	{
		TriangularThickLens clonedLens;
		try {
			clonedLens = new TriangularThickLens(this);
		} catch (SceneException e) {
			e.printStackTrace();
			clonedLens = null;
		}
		return clonedLens;
	}


	//
	// setters & getters
	//

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}



	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}



	public Vector3D getCorner1() {
		return corner1;
	}

	public void setCorner1(Vector3D corner1) {
		this.corner1 = corner1;
	}



	public Vector3D getCorner2() {
		return corner2;
	}

	public void setCorner2(Vector3D corner2) {
		this.corner2 = corner2;
	}



	public Vector3D getCorner3() {
		return corner3;
	}

	public void setCorner3(Vector3D corner3) {
		this.corner3 = corner3;
	}

	
	public double getRefractiveIndex() {
		return refractiveIndex;
	}

	public void setRefractiveIndex(double refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}


	public double getThicknessAtThinnestPoint() {
		return thicknessAtThinnestPoint;
	}

	public void setThicknessAtThinnestPoint(double thicknessAtThinnestPoint) {
		this.thicknessAtThinnestPoint = thicknessAtThinnestPoint;
	}




	//
	// the important bit:  add the scene objects that form this box cloak
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 * @throws SceneException 
	 */
	public void populateSceneObjectCollection()
	throws SceneException
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// the surface
		SurfaceProperty surface = 
			// SurfaceColour.RED_SHINY;
			// new ColourFilter(DoubleColour.CYAN, false);
			// new RefractiveSimple(refractiveIndex, 1.0, true);
			new Refractive(refractiveIndex, 1.0, true);
			// new RefractiveComplex(new Complex(refractiveIndex), 1.0, false);
//			new Refractive(
//				refractiveIndex,	// insideOutsideRefractiveIndexRatio
//				1,	// transmissionCoefficient
//				true	// shadowThrowing
//			);
		
		// first calculate the radius of the aperture, i.e. the distance from the principal point to the farthest corner
		double apertureRadius = 
				Math.max(
						Math.max(
								Vector3D.getDistance(principalPoint, corner1),
								Vector3D.getDistance(principalPoint, corner2)
							),
						Vector3D.getDistance(principalPoint, corner3)
					);
		System.out.println("TriangularThickLens::populateSceneObjectCollection: apertureRadius="+apertureRadius);
		
		// lens maker's equation (https://en.wikipedia.org/wiki/Lens_(optics)#Lensmaker.27s_equation)
		// with d = 0 (i.e. the separation between the two surfaces is zero) and two surfaces of radius of curvature
		// R and -R:
		// 	1/f = (n-1) 2/R
		// solve for radius of curvature:
		// 	R = 2 f (n-1)
		// TODO improve this, to take into account the finite thickness of the lens
		double radiusOfCurvature = 2*focalLength*(refractiveIndex-1);
		System.out.println("TriangularThickLens::populateSceneObjectCollection: radiusOfCurvature="+radiusOfCurvature);

		// the radius of curvature *must* be greater than the aperture radius for this to work
		if(Math.abs(radiusOfCurvature) < apertureRadius)
		{
			// the radius of curvature is *less* than the aperture radius!
			throw new SceneException("Radius of curvature of lens surfaces is less than aperture radius");
		}
		
		// add the lens
		if(radiusOfCurvature > 0)
		{
			// Start with a lens with positive focal length.
			// For a lens that consists of two spherical surfaces, both with radius of curvature <radiusOfCurvature>,
			// to have thickness 0 at a distance <apertureRadius> from the principal point, the spherical surfaces
			// have to be centred a distance +/- z from the principal point such that
			// 	radiusOfCurvature^2 = z^2 + apertureRadius^2,
			// and so
			// 	z = sqrt(radiusOfCurvature^2 - apertureRadius^2).
			double z = Math.sqrt(MyMath.square(radiusOfCurvature) - MyMath.square(apertureRadius)) - 0.5*thicknessAtThinnestPoint;
			Vector3D opticalAxisDirection = getNormalisedOpticalAxisDirection();
			Vector3D sphere1Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith( z));
			Vector3D sphere2Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-z));

			// add the two spherical surfaces
			addSceneObject(
					new EditableScaledParametrisedSphere(
					// new ParametrisedSphere(
							"Spherical surface 1",
							sphere1Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new EditableScaledParametrisedSphere(
					// new ParametrisedSphere(
							"Spherical surface 2",
							sphere2Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			System.out.println("TriangularThickLens::populateSceneObjectCollection: 2 spheres added at "
					+sphere1Centre+" and "+sphere2Centre);

		}
		else
		{
			// Now the case of a lens with a negative radius of curvature.
			// The centres of the spherical surfaces are now simply at +/-|radiusOfCurvature| + 0.5*thicknessAtThinnestPoint.
			double z = Math.abs(radiusOfCurvature) + 0.5*thicknessAtThinnestPoint;
			Vector3D opticalAxisDirection = getNormalisedOpticalAxisDirection();
			Vector3D sphere1Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith( z));
			Vector3D sphere2Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-z));

			// add the two spherical surfaces
			addSceneObject(
					new ParametrisedInvertedSphere(
							"Spherical surface 1",
							sphere1Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new ParametrisedInvertedSphere(
							"Spherical surface 2",
							sphere2Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new ParametrisedPlane(
							"Plane selecting the appropriate part of surface 1",
							sphere1Centre,	// pointOnPlane
							opticalAxisDirection,	// normal 
							surface,	// surfaceProperty
							this,	// parent
							getStudio()	// studio
						)
				);
			addSceneObject(
					new ParametrisedPlane(
							"Plane selecting the appropriate part of surface 2",
							sphere2Centre,	// pointOnPlane
							opticalAxisDirection.getReverse(),	// normal 
							surface,	// surfaceProperty
							this,	// parent
							getStudio()	// studio
						)
				);
		}
		
		// add planes that form a triangular prism such that the lens lies inside it
		addSceneObject(
				getPlane(
						"Plane through corners 1 and 2",	// description
						corner1,	// point1OnPlane
						corner2,
						surface
						)
			);
		addSceneObject(
				getPlane(
						"Plane through corners 2 and 3",	// description
						corner2,	// point1OnPlane
						corner3,
						surface
						)
			);
		addSceneObject(
				getPlane(
						"Plane through corners 3 and 1",	// description
						corner3,	// point1OnPlane
						corner1,
						surface
						)
			);
		
		// System.out.println("TriangularThickLens::populateSceneObjectCollection: done");
	}
	
	private Plane getPlane(String description, Vector3D point1OnPlane, Vector3D point2OnPlane, SurfaceProperty surface)
	{
		// the vector (p2-p1) x aHat is perpendicular to the desired plane
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(point2OnPlane, point1OnPlane),
				getNormalisedOpticalAxisDirection()
			).getNormalised();
		// calculate the scalar product of the normal and (p1 - midpoint);
		// this is >0 if the normal points outwards, <0 if it points inwards
		double nDotD = Vector3D.scalarProduct(normal, Vector3D.difference(point1OnPlane, getTriangleMidpoint()));
		return new ParametrisedPlane(
				description,
				point1OnPlane,	// pointOnPlane
				normal.getProductWith(Math.signum(nDotD)),	// normal 
				surface,	// surfaceProperty
				this,	// parent
				getStudio()	// studio
			);
	}
	
	public Vector3D getNormalisedOpticalAxisDirection()
	{
		// the optical axis is perpendicular to the vectors (corner2-corner1) and (corner3-corner1);
		// calculate their cross product and normalise it
		return Vector3D.crossProduct(
				Vector3D.difference(corner2, corner1),
				Vector3D.difference(corner3, corner1)
			).getNormalised();
	}
	
	public Vector3D getTriangleMidpoint()
	{
		return Vector3D.sum(corner1, corner2, corner3).getProductWith(1./3.);
	}
	
	@Override
	public String getType()
	{
		return "Triangular thick lens";
	}
}
