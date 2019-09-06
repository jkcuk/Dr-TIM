package optics.raytrace.surfaces;

import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.LightSource;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Ray;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.RaytraceExceptionHandler;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.RayTraceException;

/**
 * A rectangular array of ideal thin lenslets of focal length f.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes
 */
public class RectangularIdealThinLensletArray extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = 589224742127344329L;

	/**
	 * the cylindrical lens's focal length
	 */
	private double focalLength;
	
	/**
	 * period in x direction
	 */
	private double xPeriod;

	/**
	 * period in y direction
	 */
	private double yPeriod;

	/**
	 * offset in x direction
	 */
	private double xOffset;

	/**
	 * offset in y direction
	 */
	private double yOffset;

	//
	// constructors etc.
	//

	/**
	 * @param focalLength
	 * @param xPeriod
	 * @param yPeriod
	 * @param xOffset
	 * @param yOffset
	 * @param sceneObject
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 */
	public RectangularIdealThinLensletArray(
			double focalLength,
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);

		setFocalLength(focalLength);
		setxPeriod(xPeriod);
		setyPeriod(yPeriod);
		setxOffset(xOffset);
		setyOffset(yOffset);
	}

	public RectangularIdealThinLensletArray(RectangularIdealThinLensletArray original)
	{
		this(
				original.getFocalLength(),
				original.getxPeriod(),
				original.getyPeriod(),
				original.getxOffset(),
				original.getyOffset(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public RectangularIdealThinLensletArray clone()
	{
		return new RectangularIdealThinLensletArray(this);
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


	public double getxPeriod() {
		return xPeriod;
	}

	public void setxPeriod(double xPeriod) {
		this.xPeriod = xPeriod;
	}

	public double getyPeriod() {
		return yPeriod;
	}

	public void setyPeriod(double yPeriod) {
		this.yPeriod = yPeriod;
	}

	public double getxOffset() {
		return xOffset;
	}

	public void setxOffset(double xOffset) {
		this.xOffset = xOffset;
	}

	public double getyOffset() {
		return yOffset;
	}

	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
	}
	
	/**
	 * @param u	coordinate of a point on the lenslet array
	 * @param uPeriod	period of the array, i.e. distance between the principal points of neighbouring lenslets
	 * @param uOffset	coordinate of the principal point of the 0th lenslet
	 * @return	the coordinate of the closest principal point
	 */
	private double findLensletCentreCoordinate(double u, double uPeriod, double uOffset)
	{
		return uOffset + uPeriod*Math.floor((u-uOffset)/uPeriod+0.5);
	}
	
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i, SceneObject scene, LightSource l, int traceLevel,
			RaytraceExceptionHandler raytraceExceptionHandler) throws RayTraceException
	{
		if (traceLevel <= 0) return DoubleColour.BLACK;
		
		One2OneParametrisedObject sceneObject = (One2OneParametrisedObject)i.o;
		
		// calculate the x and y coordinates of the position; for this to work, the scene object must be sensibly parametrised
		Vector2D xy = sceneObject.getSurfaceCoordinates(i.p);
		double x = xy.x;
		double y = xy.y;

		// calculate direction of deflected ray;
		// see thinLensAlgebra.pdf
		
		// first, find the coordinates of the closest principal point, which we assume is the principal point of the lenslet being intersected
		double xCentre = findLensletCentreCoordinate(x, xPeriod, xOffset);
		double yCentre = findLensletCentreCoordinate(y, yPeriod, yOffset);
		Vector3D lensCentre = sceneObject.getPointForSurfaceCoordinates(xCentre, yCentre);
		
		// scalar product of ray direction and normalised vector in direction of optical axis is what we call dz in thinLensAlgebra.pdf;
		// need absolute value of this in case the normalised surface normal points "the other way"
		double dz = Math.abs(ray.getD().getScalarProductWith(sceneObject.getNormalisedOutwardsSurfaceNormal(lensCentre)));
		
		// now calculate the point Q in the image-sided focal plane through which
		// the ray has to pass
		Vector3D Q = Vector3D.sum(
				lensCentre,	// point where optical axis intersects surface
				ray.getD().getProductWith(getFocalLength()/dz)	// d*f/dz
			);

		// calculate normalised new light-ray direction
		Vector3D newRayDirection = Vector3D.difference(Q, i.p).getNormalised().getProductWith(Math.signum(getFocalLength()));
		
		// launch a new ray from here
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(
			getTransmissionCoefficient()
			// * Math.abs(newRayDirection.getScalarProductWith(n))/dz // cos(angle of new ray with normal) / cos(angle of old ray with normal)
			//
			// not sure the intensity scales --- see http://www.astronomy.net/articles/29/
			// Also, one of the article's reviewers wrote this:
			// This is also related to the brightening in Fig. 7. In fact, I think that such a brightening should not occur.
			// It is known that brightness of an object does not change if the object is observed by some non-absorbing optical
			// instrument. For example, a sun reflected in a curved metallic surface is equally bright as if it is viewed directly.
			// I expect the same for teleported image. Maybe if the effect of the additional factor in eq. (5) is taken into
			// account together with the other method of calculation of the ray direction, no brightening will occur.
		);
	}

}
