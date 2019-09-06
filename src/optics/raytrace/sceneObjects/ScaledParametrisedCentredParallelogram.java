package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import optics.raytrace.core.*;

/**
 * @author Johannes Courtial
 *
 */
public class ScaledParametrisedCentredParallelogram extends ParametrisedCentredParallelogram
{
	private static final long serialVersionUID = 8239287294279911922L;

	/*
	 * scaling is such that
	 * 		su = su0 + u*dsudu
	 * and
	 * 		sv = sv0 + v*dsvdv
	 */
	private double
		suMin, suMax,	// minimum and maximum value of scaled u coordinate
		svMin, svMax,	// minimum and maximum value of scaled v coordinate
		su0, dsudu,	// variables describing the scaling of the u coordinate; calculated in setUScaling
		sv0, dsvdv;	// variables describing the scaling of the v coordinate; calculated in setVScaling

	/**
	 * @param description
	 * @param centre	centre of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param suMin	lower end of range of scaled u
	 * @param suMax	upper end of range of scaled u
	 * @param svMin	lower end of range of scaled v
	 * @param svMax	upper end of range of scaled v
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ScaledParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2, 
			double suMin, double suMax,
			double svMin, double svMax,
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		super(description, centre, spanVector1, spanVector2, surfaceProperty, parent, studio);
		
		setUScaling(suMin, suMax);
		setVScaling(svMin, svMax);
	}
	
	/**
	 * @param description
	 * @param centre	centre of the parallelogram
	 * @param spanVector1	vector along one side of the parallelogram
	 * @param spanVector2	vector along the other side of the parallelogram
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 */
	public ScaledParametrisedCentredParallelogram(
			String description, 
			Vector3D centre, 
			Vector3D spanVector1, Vector3D spanVector2, 
			SurfaceProperty surfaceProperty, SceneObject parent, Studio studio)
	{
		this(description, centre, spanVector1, spanVector2, 0, 1, 0, 1, surfaceProperty, parent, studio);
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ScaledParametrisedCentredParallelogram(ScaledParametrisedCentredParallelogram original)
	{
		super(original);
		
		setUScaling(original.getSUMin(), original.getSVMax());
		setVScaling(original.getSUMin(), original.getSVMax());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Disc#clone()
	 */
	@Override
	public ScaledParametrisedCentredParallelogram clone()
	{
		return new ScaledParametrisedCentredParallelogram(this);
	}


	/**
	 * Returns the scaled parameter u and the scaled parameter v that describe the point p on the disc
	 * 
	 * @param p	the point on the disc
	 * @return	(scaled u, scaled v)
	 * @see ParametrisedObject#getSurfaceCoordinates(Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		Vector2D uv = p.getDifferenceWith(getCorner()).calculateDecomposition(getSpanVector1(), getSpanVector2());

		return(new Vector2D(
				u2su(uv.x),	// scaled u
				v2sv(uv.y)	// scaled v
		));
	}

	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("u (scaled " + MyMath.doubleToString(suMin) + " to " + MyMath.doubleToString(suMax) + ")");
		parameterNames.add("v (scaled " + MyMath.doubleToString(svMin) + " to " + MyMath.doubleToString(svMax) + ")");
		
		return parameterNames;
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		double
			u1 = su2u(u),
			v1 = sv2v(v);
		
		return Vector3D.sum(
				getCorner(),
				getSpanVector1().getProductWith(u1),
				getSpanVector2().getProductWith(v1)
			);
	}

	/**
	 * Returns the directions dP/dsu and dP/dsv on the surface of the disc
	 * 
	 * @see AnisotropicSurface#getVectorsForSurfacePoint(Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
	{
		// the array holding the directions, initially unscaled
		ArrayList<Vector3D> ds = super.getSurfaceCoordinateAxes(p);
		
		// dP/dsr = dP/dR * dR/dsr
		ds.set(0, ds.get(0).getProductWith(1./dsudu));

		// dP/dsPhi = dP/dPhi * dPhi/dsPhi
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
	 * set su0 and dsudu such that su (scaled u) runs from uMin to uMax
	 * @param suMin
	 * @param suMax
	 */
	public void setUScaling(double suMin, double suMax)
	{
		// first take a note of suMin and suMax
		this.suMin = suMin;
		this.suMax = suMax;
		
		// v runs from 0 to 1
		su0 = suMin;
		dsudu = suMax - suMin;
	}

	public double getSUMin() {
		return suMin;
	}

	public double getSUMax() {
		return suMax;
	}

	/**
	 * set sv0 and dsvdv such that sv (scaled v) runs from vMin to vMax
	 * @param svMin
	 * @param svMax
	 */
	public void setVScaling(double svMin, double svMax)
	{
		// first take a note of svMin and svMax
		this.svMin = svMin;
		this.svMax = svMax;
		
		// v runs from 0 to 1
		sv0 = svMin;
		dsvdv = svMax - svMin;
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
		return "Parallelogram";
	}
}
