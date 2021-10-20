package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.MyMath;
import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;

/**
 * A phase hologram of a rectangular array of lenslets of focal length f.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes
 */
public class PhaseHologramOfRectangularLensletArrayParametrised extends PhaseHologram
{
	private static final long serialVersionUID = -2021116407743899158L;

	/**
	 * the cylindrical lens's focal length;
	 * the phase cross-section of the lens is Phi(t) = (pi r^2)(lambda f), where r is the distance from the lens centre
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

	/**
	 * the scene object this surface property is associated with
	 */
	private One2OneParametrisedObject sceneObject;
	
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
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfRectangularLensletArrayParametrised(
			double focalLength,
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			One2OneParametrisedObject sceneObject,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		setFocalLength(focalLength);
		setxPeriod(xPeriod);
		setyPeriod(yPeriod);
		setxOffset(xOffset);
		setyOffset(yOffset);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfRectangularLensletArrayParametrised(PhaseHologramOfRectangularLensletArrayParametrised original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setxPeriod(original.getxPeriod());
		setyPeriod(original.getyPeriod());
		setxOffset(original.getxOffset());
		setyOffset(original.getyOffset());
		setSceneObject(original.getSceneObject());
	}
	
	@Override
	public PhaseHologramOfRectangularLensletArrayParametrised clone()
	{
		return new PhaseHologramOfRectangularLensletArrayParametrised(this);
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

	public One2OneParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(One2OneParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}

	
	private double findLensletCentreCoordinate(double u, double uPeriod, double uOffset)
	{
		return uOffset + uPeriod*Math.floor((u-uOffset)/uPeriod+0.5);
	}
	
	public double lensHeight(double x, double y)
	{
		return
				MyMath.square((x-xOffset)-xPeriod*Math.floor((x-xOffset)/xPeriod + 0.5)) + 
				MyMath.square((y-yOffset)-yPeriod*Math.floor((y-yOffset)/yPeriod + 0.5));
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the x and y coordinates of the position; for this to work, the scene object must be sensibly parametrised
		Vector2D xy = sceneObject.getSurfaceCoordinates(surfacePosition);
		double x = xy.x;
		double y = xy.y;
		
		double xDerivative = x-xOffset-findLensletCentreCoordinate(x, xPeriod, xOffset);
		double yDerivative = y-yOffset-findLensletCentreCoordinate(y, yPeriod, yOffset);
		
		ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
		return Vector3D.sum(xHatYHat.get(0).getProductWith(-xDerivative/focalLength), xHatYHat.get(1).getProductWith(-yDerivative/focalLength));
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
