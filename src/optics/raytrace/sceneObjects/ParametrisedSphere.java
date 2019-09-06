package optics.raytrace.sceneObjects;

import java.io.*;

import math.*;
import optics.raytrace.core.*;

import java.util.ArrayList;

public class ParametrisedSphere extends Sphere
implements One2OneParametrisedObject, Serializable	// , AnisotropicSurface
{
	private static final long serialVersionUID = -386967545725969035L;

	protected Vector3D
		phi0Direction,	// normalised Vector3D to equator/0�-meridian intersection
		phi90Direction;	// normalised centre-to-pole Vector3D

	protected Vector3D pole;
	
	/**
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param pole	direction from the centre to the north pole
	 * @param phi0Direction	direction from the centre to the intersection between zero-degree meridian and equator
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedSphere(String description,
			Vector3D c,
			double r,
			Vector3D pole,
			Vector3D phi0Direction,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, c, r, surfaceProperty, parent, studio);
		setDirections(pole, phi0Direction);
	}

	/**
	 * Constructor that uses standard directions.
	 * 
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedSphere(
			String description,
			Vector3D c,
			double r,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		this(description, c, r, new Vector3D(0, 0, 1), new Vector3D(1, 0, 0), surfaceProperty, parent, studio);
	}
	
	/**
	 * Create a clone of the original
	 * @param original
	 */
	public ParametrisedSphere(ParametrisedSphere original)
	{
		super(original);
		phi0Direction = original.getPhi0Direction().clone();
		phi90Direction = original.getPhi90Direction().clone();
		pole = original.getPole().clone();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Sphere#clone()
	 */
	@Override
	public ParametrisedSphere clone()
	{
		return new ParametrisedSphere(this);
	}
	
	/**
	 * @param p
	 * @return	(theta, phi), i.e. the spherical coordinates of the point
	 */
	public Vector2D getThetaPhiForSurfacePoint(Vector3D p)
	{
		Vector3D v = p.getDifferenceWith(getCentre()).getNormalised();	// normalised Vector3D from the centre of the sphere to p

		return(new Vector2D(
				Math.acos(v.getScalarProductWith(pole)),	// theta
				Math.atan2(v.getScalarProductWith(phi90Direction), v.getScalarProductWith(phi0Direction))	// phi
		));
	}

	/**
	 * Returns the polar angle theta and the azimuthal angle phi that describe the point p on the sphere
	 * 
	 * @param p	the point on the sphere
	 * @return	(theta, phi)
	 * @see ParametrisedObject#getParametersForSurfacePoint
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		return getThetaPhiForSurfacePoint(p);
	}
	
	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("theta");
		parameterNames.add("phi");
		
		return parameterNames;
	}
	
	/**
	 * @param p	(theta, phi)
	 * @return	point on the surface of the sphere with spherical coordinates theta and phi
	 */
	public Vector3D getSurfacePointForThetaPhi(double u, double v)
	{
		double r = getRadius()*Math.sin(u);	// R*sin(theta)
		
		return Vector3D.sum(
				getCentre(),
				pole.getProductWith(getRadius()*Math.cos(u)),	// R*cos(theta)
				phi0Direction.getProductWith(r*Math.cos(v)),	// zeroDeg*r*cos(phi)
				phi90Direction.getProductWith(r*Math.sin(v))	// ninetyDeg*r*sin(phi)
			);
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		return getSurfacePointForThetaPhi(u, v);
	}

	/**
	 * Returns the directions dP/dTheta and dP/dPhi on the surface of the sphere
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		Vector2D thetaPhi = getSurfaceCoordinates(p);
		double
		theta = thetaPhi.x,
		phi = thetaPhi.y,
		sinTheta = Math.sin(theta),
		cosTheta = Math.cos(theta),
		sinPhi = Math.sin(phi),
		cosPhi = Math.cos(phi);

		ArrayList<Vector3D> ds = new ArrayList<Vector3D>(2);

		/* In Cartesian coordinates with basis Vector3Ds zeroDeg, ninetyDeg and pole, a point on the surface
		 * with parameters (theta, phi) is described by the Vector3D
		 * 		P = (r*sin(theta)*cos(phi), r*sin(theta)*sin(phi), r*cos(theta)).
		 */

		/* The first direction is given by the Vector3D
		 * 		dP/dTheta = (r*cos(theta)*cos(phi), r*cos(theta)*sin(phi), -r*sin(theta))
		 */
		ds.add(0, phi0Direction.getProductWith(getRadius()*cosTheta*cosPhi).getSumWith(
				phi90Direction.getProductWith(getRadius()*cosTheta*sinPhi)).getSumWith(pole.getProductWith(-getRadius()*sinTheta)));

		/* The second direction is given by the Vector3D
		 * 		dP/dPhi = (-r*sin(theta)*sin(phi), r*sin(theta)*cos(phi), 0)
		 */
		ds.add(1, phi0Direction.getProductWith(-getRadius()*sinTheta*sinPhi).getSumWith(phi90Direction.getProductWith(getRadius()*sinTheta*cosPhi)));

		return ds;
	}

	// getters and setters
	
	public Vector3D getPhi0Direction() {
		return phi0Direction;
	}

	public Vector3D getPhi90Direction() {
		return phi90Direction;
	}

	public Vector3D getPole() {
		return pole;
	}

	/**
	 * Set the directions from the centre to the pole, to the intersection between equator and
	 * 0 degree meridian, and to the intersection between equator and 90 degree meridian.
	 * Note that the direction to the pole direction is set first;
	 * then the 0-degree direction is set, projecting it (into the plane perpendicular to the
	 * pole direction) such that it is perpendicular to the pole direction;
	 * and finally the 90-degree direction is set, projecting it (onto the line perpendicular
	 * to both the pole direction and the 0-degree direction) such that it is perpendicular
	 * to both the pole direction and the 0-degree direction.
	 * All directions are normalised.
	 * @param pole
	 * @param zeroDeg
	 * @param ninetyDeg
	 */
	public void setDirections(Vector3D pole, Vector3D phi0Direction, Vector3D phi90Direction)
	{
		// first set the (normalised) pole direction, ...
		this.pole = pole.getNormalised();
		
		// ... then set the zeroDeg direction, ensuring is perpendicular to the
		// pole direction (and normalised), ...
		this.phi0Direction = phi0Direction.getPartPerpendicularTo(this.pole).getNormalised();
		
		// ... and finally set the ninetyDeg direction, ensuring it is perpendicular
		// both the pole and zeroDeg direction
		this.phi90Direction = phi90Direction.getProjectionOnto(Vector3D.crossProduct(this.pole, this.phi0Direction)).getNormalised();
	}

	/**
	 * Set the directions from the centre to the pole, to the intersection between equator and
	 * 0 degree meridian, and to the intersection between equator and 90 degree meridian.
	 * Only the direction to the pole and the 0-degree direction are passed as arguments.
	 * Note that the direction to the pole direction is set first;
	 * then the 0-degree direction is set, projecting it (into the plane perpendicular to the
	 * pole direction) such that it is perpendicular to the pole direction;
	 * and finally the 90-degree direction is set as the cross product of the pole direction
	 * and the 0-degree direction.
	 * All directions are normalised.
	 * @param pole
	 * @param zeroDeg
	 */
	public void setDirections(Vector3D pole, Vector3D phi0Direction)
	{
		// first set the (normalised) pole direction, ...
		this.pole = pole.getNormalised();
		
		// ... then set the zeroDeg direction, ensuring is perpendicular to the
		// pole direction (and normalised), ...
		this.phi0Direction = phi0Direction.getPartPerpendicularTo(this.pole).getNormalised();
		
		// ... and finally set the ninetyDeg direction, ensuring it is perpendicular
		// both the pole and zeroDeg direction
		this.phi90Direction = Vector3D.crossProduct(this.pole, this.phi0Direction).getNormalised();
	}

	/**
	 * Set the directions from the centre to the pole, to the intersection between equator and
	 * 0 degree meridian, and to the intersection between equator and 90 degree meridian.
	 * Only the direction to the pole is passed as argument.
	 * Note that the direction to the pole direction is set first;
	 * then the 0-degree direction is set as a direction perpendicular to the pole direction;
	 * and finally the 90-degree direction is set as the cross product of the pole direction
	 * and the 0-degree direction.
	 * All directions are normalised.
	 * @param pole
	 */
	public void setDirections(Vector3D pole)
	{
		// first set the (normalised) pole direction, ...
		this.pole = pole.getNormalised();
		
		// ... then set the zeroDeg direction, ensuring is perpendicular to the
		// pole direction (and normalised), ...
		this.phi0Direction = Vector3D.getANormal(this.pole).getNormalised();
		
		// ... and finally set the ninetyDeg direction, ensuring it is perpendicular
		// both the pole and zeroDeg direction
		this.phi90Direction = Vector3D.crossProduct(this.pole, this.phi0Direction).getNormalised();
	}
	
	@Override
	public ParametrisedSphere transform(Transformation t)
	{
		return new ParametrisedSphere(
				description,
				t.transformPosition(getCentre()),
				getRadius(),
				pole,
				phi0Direction,
				getSurfaceProperty(),
				getParent(),
				getStudio()
		);
	}

	@Override
	public String toString() {
		return "<ParametrisedSphere>\n" + 
		"\tcentre = " + getCentre() + "\n" + 
		"\tradius = " + getRadius() + "\n" + 
		"<ParametrisedSphere>";
	}
	
	@Override
	public String getType()
	{
		return "Sphere";
	}
}
