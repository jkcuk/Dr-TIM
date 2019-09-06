package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import optics.raytrace.core.*;

/**
 * This can be parametrised either in Cartesian (<i>x</i>,<i>y</i>) or polar (<i>r</i>, <i>phi</i>) coordinates.
 * We call these here <i>u</i> and <i>v</i>, where <i>u</i> is either <i>x</i> or <i>r</i> and <i>v</i> is either <i>y</i> or <i>phi</i>.
 * @author Johannes Courtial
 *
 */
public class ScaledParametrisedDisc extends ParametrisedDisc
{
	private static final long serialVersionUID = 8223934884283800202L;

	/*
	 * scaling is such that
	 * 		su = su0 + u*dsudu
	 * and
	 * 		sv = sv0 + v*dsvdv
	 */
	private double
		suMin, suMax,	// minimum and maximum value of scaled u (x or r) coordinate
		svMin, svMax;	// minimum and maximum value of scaled v (y or phi) coordinate
	
	// internal variables
	private double
		su0, dsudu,	// variables describing the scaling of the u coordinate; calculated in setUScaling
		sv0, dsvdv;	// variables describing the scaling of the v coordinate; calculated in setVScaling

	/**
	 * Constructor that defines the directions of the normal and the zero-degree direction.
	 * The ninety-degree direction
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param xDirection	vector in x / phi=0 degree direction
	 * @param yDirection
	 * @param discParametrisationType,
	 * @param suMin	lower end of range of scaled <i>u</i>
	 * @param suMax	upper end of range of scaled <i>u</i>
	 * @param svMin	lower end of range of scaled <i>v</i>
	 * @param svMax	upper end of range of scaled <i>v</i>
	 * @param sp	surface properties
	 * @param parent	parent object
	 * @param studio	the studio
	 */
	public ScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			Vector3D yDirection,
			DiscParametrisationType discParametrisationType,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, xDirection, yDirection, discParametrisationType, sp, parent, studio);
		
		setUScaling(suMin, suMax);
		setVScaling(svMin, svMax);
	}


	/**
	 * Constructor that defines the directions of the normal and the zero-degree direction.
	 * The ninety-degree direction
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param xDirection	vector in x / phi=0 degree direction
	 * @param discParametrisationType,
	 * @param suMin	lower end of range of scaled <i>u</i>
	 * @param suMax	upper end of range of scaled <i>u</i>
	 * @param svMin	lower end of range of scaled <i>v</i>
	 * @param svMax	upper end of range of scaled <i>v</i>
	 * @param sp	surface properties
	 * @param parent	parent object
	 * @param studio	the studio
	 */
	public ScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D xDirection,
			DiscParametrisationType discParametrisationType,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, xDirection, discParametrisationType, sp, parent, studio);
		
		setUScaling(suMin, suMax);
		setVScaling(svMin, svMax);
	}

	/**
	 * Constructor that defines the directions of the normal and the zero-degree direction.
	 * The ninety-degree direction
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param xDirection	vector in x / phi=0 degree direction
	 * @param suMin	lower end of range of scaled <i>u</i>
	 * @param suMax	upper end of range of scaled <i>u</i>
	 * @param svMin	lower end of range of scaled <i>v</i>
	 * @param svMax	upper end of range of scaled <i>v</i>
	 * @param sp	surface properties
	 * @param parent	parent object
	 * @param studio	the studio
	 */
	public ScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			Vector3D phi0Direction,
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, normal, radius, phi0Direction, sp, parent, studio);
		
		setUScaling(suMin, suMax);
		setVScaling(svMin, svMax);
	}

	/**
	 * Constructor that picks random x/phi=0 degrees and y/phi=90 degrees directions (both perpendicular to normal)
	 * 
	 * @param description
	 * @param centre	centre of the disc
	 * @param normal	surface normal
	 * @param radius	radius of the disc
	 * @param sp	surface properties
	 */
	public ScaledParametrisedDisc(
			String description,
			Vector3D centre,
			Vector3D normal,
			double radius,
			SurfaceProperty sp,
			SceneObject parent, 
			Studio studio
	)
	{
		super(	description,
				centre,
				normal,
				radius,
				sp,
				parent, studio
			);
		
		switch(discParametrisationType)
		{
		case POLAR:
			setUScaling(0, 1);
			setVScaling(0, 2*Math.PI);
			break;
		case CARTESIAN:
		default:
			setUScaling(-1, 1);
			setVScaling(-1, 1);
		}
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ScaledParametrisedDisc(ScaledParametrisedDisc original)
	{
		super(original);
		
		setUScaling(original.getSUMin(), original.getSUMax());
		setVScaling(original.getSVMin(), original.getSVMax());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Disc#clone()
	 */
	@Override
	public ScaledParametrisedDisc clone()
	{
		return new ScaledParametrisedDisc(this);
	}


	/**
	 * Returns the scaled distance r from the centre and the scaled azimuthal angle phi that describe the point p on the disc
	 * 
	 * @param p	the point on the disc
	 * @return	(scaled r, scaled phi)
	 * @see ParametrisedObject#getSurfaceCoordinates(Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		// get unscaled coordinates...
		Vector2D uv = super.getSurfaceCoordinates(p);
		
		// ... and scale and return them
		return new Vector2D(u2su(uv.x), v2sv(uv.y));
	}

	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> unscaledSurfaceCoordinateNames = super.getSurfaceCoordinateNames();
		
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add(unscaledSurfaceCoordinateNames.get(0)+" (scaled " + MyMath.doubleToString(suMin) + " to " + MyMath.doubleToString(suMax) + ")");
		parameterNames.add(unscaledSurfaceCoordinateNames.get(1)+" (scaled " + MyMath.doubleToString(svMin) + " to " + MyMath.doubleToString(svMax) + ")");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double su, double sv)
	{
		return super.getPointForSurfaceCoordinates(su2u(su), sv2v(sv));
	}

	/**
	 * Returns the directions dP/dsu and dP/dsv on the disc
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
	{
		// the array holding the directions, initially unscaled
		ArrayList<Vector3D> ds = super.getSurfaceCoordinateAxes(p);
		
		// dP/dsu = dP/du * du/dsu
		ds.set(0, ds.get(0).getProductWith(1./dsudu));

		// dP/dsv = dP/dv * dv/dsv
		ds.set(1, ds.get(1).getProductWith(1./dsvdv));

		return ds;
	}
	
	// methods that convert between scaled and unscaled coordinates
	
	public double u2su(double u)
	{
		return su0 + u*dsudu;
	}
	
	public double su2u(double su)
	{
		return (su - su0) / dsudu;
	}
	
	public double v2sv(double v)
	{
		return sv0 + v*dsvdv;
	}
	
	public double sv2v(double sv)
	{
		return (sv - sv0) / dsvdv; 
	}
	
	// setters and getters

	/**
	 * set su0 and dsudu such that su (scaled u) runs from uMin to uMax.
	 * 
	 * @param suMin
	 * @param suMax
	 */
	public void setUScaling(double suMin, double suMax)
	{
		// first take a note of srMin and srMax
		this.suMin = suMin;
		this.suMax = suMax;
		
		switch(discParametrisationType)
		{
		case POLAR:
			// r runs from 0 to getRadius()
			su0 = suMin;
			dsudu = (suMax - suMin) / getRadius();
			break;
		case CARTESIAN:
		default:
			// x runs from sMin = -getRadius() to sMax = +getRadius(), so dsudu = (suMax - suMin) / (uMax - uMin)
			dsudu = (suMax - suMin) / (2*getRadius());
			// suMin = su0 + uMin dsudu, so su0 = suMin - uMin dsudu = (uMax suMin - uMin suMax) / (uMax - uMin)
			su0 = (suMin + suMax) / 2.;
		}
	}

	public double getSUMin() {
		return suMin;
	}

	public double getSUMax() {
		return suMax;
	}
	
	/**
	 * set sv0 and dsvdv such that sv (scaled v) runs from svMin to svMax
	 * @param svMin
	 * @param svMax
	 */
	public void setVScaling(double svMin, double svMax)
	{
		// first take a note of svMin and svMax
		this.svMin = svMin;
		this.svMax = svMax;
		
		switch(discParametrisationType)
		{
		case POLAR:
			// phi runs from -Pi to Pi
			sv0 = (svMax + svMin)/2.0;
			dsvdv = (svMax - svMin) / (2.0*Math.PI);
			break;
		case CARTESIAN:
		default:
			// y runs from vMin = -getRadius() to vMax = +getRadius(), so dsvdv = (svMax - svMin) / (vMax - vMin)
			dsvdv = (svMax - svMin) / (2*getRadius());
			// svMin = sv0 + vMin dsvdv, so sv0 = svMin - vMin dsvdv = (vMax svMin - vMin svMax) / (vMax - vMin)
			sv0 = (svMin + svMax) / 2.;
		}

	}
	
	public double getSVMin() {
		return svMin;
	}

	public double getSVMax() {
		return svMax;
	}
	
	@Override
	public String getType()
	{
		return "Disc";
	}
}
