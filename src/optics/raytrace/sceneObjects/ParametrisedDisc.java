package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.ArrayList;

import math.*;
import math.geometry.ShapeWithRandomPointAndBoundary;
import optics.raytrace.core.*;
import optics.raytrace.surfaces.SurfaceColour;

public class ParametrisedDisc extends Disc
implements One2OneParametrisedObject, Serializable, ShapeWithRandomPointAndBoundary	// , AnisotropicSurface
{
	private static final long serialVersionUID = 6389689478004870837L;

	protected Vector3D
		xDirection,	// normalised Vector3D in x direction, which is also the phi=0 degrees direction
		yDirection;	// normalised Vector3D in y direction, which is perpendicular to x direction; this is also the phi=90 degree direction
	
	public enum DiscParametrisationType {CARTESIAN, POLAR;}
	
	protected DiscParametrisationType discParametrisationType;


	/**
	 * @param description
	 * @param centre
	 * @param normal
	 * @param radius
	 * @param xDirection
	 * @param yDirection
	 * @param discParametrisationType
	 * @param sp
	 * @param parent
	 * @param studio
	 */
	public ParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			Vector3D yDirection,
			DiscParametrisationType discParametrisationType,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, sp, parent, studio);
		
		setDiscParametrisationType(discParametrisationType);
		setDirections(xDirection, yDirection);
	}

	/**
	 * @param description
	 * @param centre
	 * @param normal
	 * @param radius
	 * @param xDirection
	 * @param discParametrisationType
	 * @param sp
	 * @param parent
	 * @param studio
	 */
	public ParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			DiscParametrisationType discParametrisationType,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, sp, parent, studio);
		
		setDiscParametrisationType(discParametrisationType);
		setDirections(xDirection);
	}

	/**
	 * Constructor that defines the directions of the normal and the zero-degree direction.
	 * The ninety-degree direction
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param xDirection	vector in x direction (phi=0 degree direction)
	 * @param surfaceProperty	surface properties
	 */
	public ParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		this(description, centre, normal, radius, xDirection, DiscParametrisationType.POLAR, surfaceProperty, parent, studio);
	}
	
	/**
	 * Constructor that picks random (but perpendicular) x and y directions (both perpendicular to normal)
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param sp	surface properties
	 */
	public ParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		this(	description,
				centre,
				normal,
				radius,
				Vector3D.getANormal(normal),	// x direction
				sp,
				parent, studio
			);
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ParametrisedDisc(ParametrisedDisc original)
	{
		super(original);
		
		xDirection = original.getXDirection().clone();
		yDirection = original.getYDirection().clone();
		discParametrisationType = original.getDiscParametrisationType();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Disc#clone()
	 */
	@Override
	public ParametrisedDisc clone()
	{
		return new ParametrisedDisc(this);
	}


	public DiscParametrisationType getDiscParametrisationType() {
		return discParametrisationType;
	}

	public void setDiscParametrisationType(DiscParametrisationType discParametrisationType) {
		this.discParametrisationType = discParametrisationType;
	}

	/**
	 * @return the names of the parameters, i.e. "x" and "y" if the parametrisation is Cartesian, or "r" and "phi" if it is polar
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		switch(discParametrisationType)
		{
		case POLAR:
			parameterNames.add("r");
			parameterNames.add("phi");
			break;
		case CARTESIAN:
		default:
			parameterNames.add("x");
			parameterNames.add("y");
		}
		return parameterNames;
	}

	/**
	 * Returns the distance r from the centre and the azimuthal angle phi that describe the point p on the disc
	 * 
	 * @param p	the point on the disc
	 * @return	(r, phi)
	 */
	public Vector2D getRAndPhiForSurfacePoint(Vector3D p)
	{
		Vector3D v = p.getDifferenceWith(getCentre());	//  Vector3D from the centre of the disc to p

		return(new Vector2D(
				v.getLength(),	// r
				MyMath.xy2phi(v.calculateDecomposition(xDirection, yDirection))	// phi
//				Math.atan2(v.scalarProduct(ninetyDeg), v.scalarProduct(zeroDeg))	// phi
		));
	}

	/**
	 * Returns the distance r from the centre and the azimuthal angle phi that describe the point p on the disc
	 * 
	 * @param p	the point on the disc
	 * @return	(r, phi)
	 * @see ParametrisedObject#getSurfaceCoordinates(Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		switch(discParametrisationType)
		{
		case POLAR:
			return getRAndPhiForSurfacePoint(p);
		case CARTESIAN:
		default:
			return p.getDifferenceWith(getCentre()).calculateDecomposition(xDirection, yDirection);
		}
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		switch(discParametrisationType)
		{
		case POLAR:
			return Vector3D.sum(
					getCentre(),
					xDirection.getProductWith(u*Math.cos(v)),	// zeroDeg*r*cos(phi)
					yDirection.getProductWith(u*Math.sin(v))	// ninetyDeg*r*sin(phi)
				);
		case CARTESIAN:
		default:
			return Vector3D.sum(getCentre(), xDirection.getProductWith(u), yDirection.getProductWith(v));
		}
	}

	/**
	 * Returns the directions dP/dR and dP/dPhi on the disc
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		ArrayList<Vector3D> ds = new ArrayList<Vector3D>(2);

		switch(discParametrisationType)
		{
		case POLAR:
			Vector2D rPhi = getRAndPhiForSurfacePoint(p);
			double
			r = rPhi.x,
			phi = rPhi.y,
			sinPhi = Math.sin(phi),
			cosPhi = Math.cos(phi);

			/* In Cartesian coordinates with basis Vector3Ds zeroDeg, ninetyDeg and normal, a point on the surface
			 * with parameters (theta, phi) is described by the Vector3D
			 * 		P = (r*cos(phi), r*sin(phi), 0).
			 */

			/* The first direction is given by the Vector3D
			 * 		dP/dR = (cos(phi), sin(phi), 0)
			 */
			ds.add(0, Vector3D.sum(
					xDirection.getProductWith(cosPhi),
					yDirection.getProductWith(sinPhi)
					));

			/* The second direction is given by the Vector3D
			 * 		dP/dPhi = (-r*sin(phi), r*cos(phi), 0)
			 */
			ds.add(1, Vector3D.sum(
					xDirection.getProductWith(-r*sinPhi),
					yDirection.getProductWith(r*cosPhi)
					));
		
			break;
		case CARTESIAN:
		default:
			ds.add(0, xDirection);
			ds.add(1, yDirection);
		}
		
		return ds;
	}

	/**
	 * The normal must have been set before this is called.
	 * @param xDirection
	 * @param yDirection
	 */
	public void setDirections(Vector3D xDirection, Vector3D yDirection)
	{
		this.xDirection = xDirection.getPartPerpendicularTo(getNormal()).getNormalised();
		this.yDirection = yDirection.getPartPerpendicularTo(xDirection, getNormal()).getNormalised();
	}
	
	/**
	 * Assumes that the normal has been set.
	 * @param xDirection
	 */
	public void setDirections(Vector3D xDirection)
	{
		// make sure this.zeroDeg is perpendicular to the normal...
		this.xDirection = xDirection.getPartPerpendicularTo(getNormal()).getNormalised();
		
		// ... and find a vector ninetyDeg that is perpendicular to both normal and zeroDeg
		yDirection = Vector3D.crossProduct(getNormal(), this.xDirection).getNormalised();
	}

	public Vector3D getXDirection()
	{
		return xDirection;
	}

	public Vector3D getYDirection()
	{
		return yDirection;
	}

	@Override
	public Vector3D getRandomPointOnShape() {
		
		double randomRadius = Math.random() + Math.random();
		if(randomRadius > 1) {
			randomRadius = 2 - randomRadius; //this looks arbitrary but it is the limiting case of partitioning the circle up into isosceles triangles and taking the random point inside each of those by the method of getting a random point in the paralellogram obtained by mirroring the triangle about its base and 'folding' back the random point if it is in the outer triangle. @see randomPointOnShape in Parametrisedtriangle.
		}
		double randomPhi = Math.random()*2*Math.PI;
		return Vector3D.sum(getCentre(),getXDirection().getProductWith(getRadius()*randomRadius*Math.cos(randomPhi)),getYDirection().getProductWith(getRadius()*randomRadius*Math.sin(randomPhi)));
	}


	/**
	 * @return a random point on the surface of the disc. This will be used by @see FresnelLensShaped, @see FresnelLens and
	 * @see FresnelLensSurface to sample randomly within the area of its aperture and compute the maximum and minimum focal lengths
	 * required for the lens sections to completely fill the aperture.
	 */
	@Override
	public ParametrisedDisc transform(Transformation t) {
		return new ParametrisedDisc(
					description,
					t.transformPosition(getCentre()),	// centre,
					t.transformDirection(getNormal()),	// normal,
					getRadius(),
					t.transformDirection(xDirection),	// xDirection,
					getSurfaceProperty(),	// sp,
					getParent(), getStudio());
	}
	
	/**
	 * @return a cylinder  perpendicular to the disk that will serve as a boundary and a bounding `box' for a @see FresnelLensShaped
	 */
	@Override
	public SceneObject getBoundary(double boundaryLength)
	{
		return new Cylinder("boundary cylinder of disc",// description,
				Vector3D.sum(getNormal().getProductWith(0.5*boundaryLength), getCentre()),//startPoint
				Vector3D.sum(getNormal().getProductWith(-0.5*boundaryLength), getCentre()), //endPoint
				getRadius(), //radius
				SurfaceColour.BLACK_MATT,
				getParent(),// parent,
				getStudio() //studio
				);
	}

	@Override
	public String getType()
	{
		return "Disc";
	}
}
