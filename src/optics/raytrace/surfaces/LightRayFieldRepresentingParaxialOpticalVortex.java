package optics.raytrace.surfaces;

import math.MyMath;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.RaySceneObjectIntersection;
import optics.raytrace.core.SurfaceProperty;

/**
 * A surface property that represents a (paraxial) optical vortex of charge m.
 * 
 * In cylindrical coordinates,  the phase of such a vortex is given  by
 *   
 *   phase(r, phi, z) = m phi + k z.
 *   
 * In Cartesian coordinates, this is
 *   
 *   phase(x, y, z) = m arctan(x,y) + k z.
 * 
 * The phase gradient is
 * 
 *   grad phase(x, y, z) = (-m/r^2 y, m/r^2 x, k)
 *   
 * (as d arctan(x,y) / dx = -  y/(x^2 + y^2) etc.)
 * 
 * Dividing by k gives
 * 
 *   grad phase(x, y, z) \propto (-m/k y/r^2, m/k x/r^2, 1)
 *   
 * So the  direction of  the light-rays (or rather phase-front normals) is determined by m/k
 * 
 * @author Johannes Courtial
 */
public class LightRayFieldRepresentingParaxialOpticalVortex extends LightRayField
{	
	private static final long serialVersionUID = -4691567369762438262L;

	/**
	 * the colour of the light-ray field
	 */
	private DoubleColour colour;
	
	/**
	 * point on vortex line
	 */
	private Vector3D pointOnVortexLine;
	
	/**
	 * vortex-line direction
	 */
	private Vector3D normalisedVortexLineDirection;
	
	/**
	 * the ratio of the topological charge,  m, and the wavenumber, k
	 */
	private double mOverK;
	
	//  constructors etc.
	
	public LightRayFieldRepresentingParaxialOpticalVortex(
			DoubleColour colour, 
			Vector3D pointOnVortexLine, 
			Vector3D normalisedVortexLineDirection,
			double mOverK, 
			double angularFuzzinessRad,
			boolean bidirectional
		)
	{
		super(angularFuzzinessRad, bidirectional);
		this.colour = colour;
		this.pointOnVortexLine = pointOnVortexLine;
		setNormalisedVortexLineDirection(normalisedVortexLineDirection);
		this.mOverK = mOverK;
	}
	
	public LightRayFieldRepresentingParaxialOpticalVortex(DoubleColour colour)
	{
		this(colour, Vector3D.O, Vector3D.Z, 0, MyMath.deg2rad(1), false);
	}
	
	
	@Override
	public SurfaceProperty clone() {
		return new LightRayFieldRepresentingParaxialOpticalVortex(
				getColour(),
				getPointOnVortexLine(),
				getNormalisedVortexLineDirection(),
				getmOverK(),
				getAngularFuzzinessRad(),
				isBidirectional()
			);
	}

	
	
	
	// setters & getters

	/**
	 * @return the colour
	 */
	public DoubleColour getColour() {
		return colour;
	}

	/**
	 * @param colour the colour to set
	 */
	public void setColour(DoubleColour colour) {
		this.colour = colour;
	}

	/**
	 * @return the position
	 */
	public Vector3D getPointOnVortexLine() {
		return pointOnVortexLine;
	}

	/**
	 * @param position the position to set
	 */
	public void setPointOnVortexLine(Vector3D pointOnVortexLine) {
		this.pointOnVortexLine = pointOnVortexLine;
	}

	/**
	 * @return the normalisedVortexLineDirection
	 */
	public Vector3D getNormalisedVortexLineDirection() {
		return normalisedVortexLineDirection;
	}

	/**
	 * @param normalisedVortexLineDirection the normalisedVortexLineDirection to set
	 */
	public void setNormalisedVortexLineDirection(Vector3D normalisedVortexLineDirection) {
		this.normalisedVortexLineDirection = normalisedVortexLineDirection.getNormalised();
		xHat = Vector3D.getANormal(this.normalisedVortexLineDirection);
		yHat = Vector3D.crossProduct(this.normalisedVortexLineDirection, xHat);
	}

	/**
	 * @return the mOverK
	 */
	public double getmOverK() {
		return mOverK;
	}

	/**
	 * @param mOverK the mOverK to set
	 */
	public void setmOverK(double mOverK) {
		this.mOverK = mOverK;
	}

	
	// LightRayField methods
	
	// unit vectors that, together with the normalisedVortexLineDirection, form a right-handed Cartesian coordinate system
	private Vector3D xHat, yHat;

	/**
	 * @param i
	 * @return	the normalised light-ray direction
	 */
	@Override
	public Vector3D getNormalisedLightRayDirection(RaySceneObjectIntersection i)
	{
		// xHat and yHat must be initialised!
		
		// get the x and y coordinates
		Vector3D line2p = Vector3D.difference(i.p, pointOnVortexLine);
		double x = Vector3D.scalarProduct(line2p, xHat);
		double y = Vector3D.scalarProduct(line2p, yHat);
		double r2 = x*x + y*y; //  r^2
		
		// grad phase(x, y, z) \propto (-m/k y/r^2, m/k x/r^2, 1)
		return Vector3D.sum(
				xHat.getProductWith(-mOverK*y/r2),
				yHat.getProductWith( mOverK*x/r2),
				normalisedVortexLineDirection
			).getNormalised();
	}
	
	/**
	 * the colour of of the ray in the field at i.p
	 */
	@Override
	public DoubleColour getRayColour(RaySceneObjectIntersection i)
	{
		return colour;
	}
}

