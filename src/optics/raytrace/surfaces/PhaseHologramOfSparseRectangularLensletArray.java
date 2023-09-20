package optics.raytrace.surfaces;

import java.util.ArrayList;

import math.Vector2D;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.surfaces.diffraction.SingleSlitDiffraction;

/**
 * A phase hologram of a sparse rectangular array of lenslets of focal length f.
 * The array us "sparse" in the sense that only every nth lens is present; in between, there is nothing.
 * 
 * The associated SceneObject must be a ParametrisedObject as the getSurfaceCoordinates(Vector3D) method is used to calculate the coordinates on the surface.
 * 
 * @author johannes
 */
/**
 * @author johannes
 *
 */
/**
 * @author johannes
 *
 */
public class PhaseHologramOfSparseRectangularLensletArray extends PhaseHologram implements HoleySurface
{
	private static final long serialVersionUID = -1341965636822305236L;

	/**
	 * the lenses' focal length;
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
	 * offset in x direction; one of the lenses is centred at (xOffset, yOffset)
	 */
	private double xOffset;

	/**
	 * offset in y direction; one of the lenses is centred at (xOffset, yOffset)
	 */
	private double yOffset;
	
	/**
	 * every <i>nx</i>th lens in the <i>x</i> direction is physically present (the space in between in a hole)
	 */
	private int nx;
	
	/**
	 * every <i>ny</i>th lens in the <i>y</i> direction is physically present (the space in between in a hole)
	 */
	private int ny;
	
	/**
	 * if true, simulate diffractive light-ray-direction change at aperture of size <xOffset> x <yOffset> for wavelength <lambda>
	 */
	private boolean simulateDiffractiveBlur;
	
	/**
	 * wavelength for which diffractive light-ray-direction change is simulated (if <simulateDiffractiveBlur> = true)
	 */
	private double lambda;

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
	 * @param nx
	 * @param ny
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param sceneObject
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramOfSparseRectangularLensletArray(
			double focalLength,
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			int nx,
			int ny,
			boolean simulateDiffractiveBlur,
			double lambda,
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
		setNx(nx);
		setNy(ny);
		setSimulateDiffractiveBlur(simulateDiffractiveBlur);
		setLambda(lambda);
		setSceneObject(sceneObject);
	}

	public PhaseHologramOfSparseRectangularLensletArray(PhaseHologramOfSparseRectangularLensletArray original) {
		super(original);
		setFocalLength(original.getFocalLength());
		setxPeriod(original.getxPeriod());
		setyPeriod(original.getyPeriod());
		setxOffset(original.getxOffset());
		setyOffset(original.getyOffset());
		setNx(original.getNx());
		setNy(original.getNy());
		setSimulateDiffractiveBlur(original.isSimulateDiffractiveBlur());
		setLambda(original.getLambda());
		setSceneObject(original.getSceneObject());
	}
	
	@Override
	public PhaseHologramOfSparseRectangularLensletArray clone()
	{
		return new PhaseHologramOfSparseRectangularLensletArray(this);
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

	public int getNx() {
		return nx;
	}

	public void setNx(int nx) {
		this.nx = nx;
	}

	public int getNy() {
		return ny;
	}

	public void setNy(int ny) {
		this.ny = ny;
	}

	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public One2OneParametrisedObject getSceneObject() {
		return sceneObject;
	}

	public void setSceneObject(One2OneParametrisedObject sceneObject) {
		this.sceneObject = sceneObject;
	}
	
	private double mod(double a, double b)
	{
		return ((a % b) + b) % b;
	}
		
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// calculate the x and y coordinates of the position; for this to work, the scene object must be sensibly parametrised
		Vector2D xy = sceneObject.getSurfaceCoordinates(surfacePosition);
		double x = xy.x - xOffset;
		double y = xy.y - yOffset;
		
		double xDerivative = mod(x+0.5*nx*xPeriod, nx*xPeriod) - 0.5*nx*xPeriod;
		double yDerivative = mod(y+0.5*ny*yPeriod, ny*yPeriod) - 0.5*ny*yPeriod;
		
		ArrayList<Vector3D> xHatYHat = sceneObject.getSurfaceCoordinateAxes(surfacePosition);
		Vector3D xHat = xHatYHat.get(0);
		Vector3D yHat = xHatYHat.get(1);
		Vector3D directionChange = Vector3D.sum(xHat.getProductWith(-xDerivative/focalLength), yHat.getProductWith(-yDerivative/focalLength));
		if(simulateDiffractiveBlur)
		{
			return Vector3D.sum(directionChange, SingleSlitDiffraction.getTangentialDirectionComponentChange(
					lambda,
					xPeriod,	// pixelSideLengthU
					yPeriod,	// pixelSideLengthV
					xHat,	// uHat
					yHat	// vHat
				));
		}
		else
			return directionChange;
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

	@Override
	public boolean isHole(RaySceneObjectIntersection i)
	{
		Vector2D xy = sceneObject.getSurfaceCoordinates(i.p);
		double x = xy.x - xOffset;
		double y = xy.y - yOffset;
		
		double dx = Math.abs(mod(x+0.5*nx*xPeriod, nx*xPeriod) - 0.5*nx*xPeriod);
		double dy = Math.abs(mod(y+0.5*ny*yPeriod, ny*yPeriod) - 0.5*ny*yPeriod);

		return (dx > 0.5*xPeriod) || (dy > 0.5*yPeriod);
	}

}
