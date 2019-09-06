package optics.raytrace.sceneObjects;

import java.util.ArrayList;

import math.*;
import optics.raytrace.core.*;

/**
 * @author Johannes Courtial
 *
 */
public class ScaledParametrisedSphere extends ParametrisedSphere
{
	private static final long serialVersionUID = -1589143036072746994L;

	/*
	 * scaling is such that
	 * 		sTheta = sTheta0 + r*dsThetadTheta
	 * and
	 * 		sPhi = sPhi0 + phi*dsPhidPhi
	 */
	private double
		sThetaMin, sThetaMax,	// minimum and maximum value of scaled theta coordinate
		sPhiMin, sPhiMax,	// minimum and maximum value of scaled phi coordinate
		sTheta0, dsThetadTheta,	// variables describing the scaling of the theta coordinate; calculated in setThetaScaling
		sPhi0, dsPhidPhi;	// variables describing the scaling of the phi coordinate; calculated in setPhiScaling

	/**
	 * @param description
	 * @param c	centre of the sphere
	 * @param r	radius
	 * @param pole	direction from the centre to the north pole
	 * @param phi0Direction	direction from the centre to the intersection between zero-degree meridian and equator
	 * @param sPhiMin	lower end of range of scaled phi
	 * @param sPhiMax	upper end of range of scaled phi
	 * @param surfaceProperty	surface properties
	 * @param parent	parent object
	 * @param studio	the studio
	 */
	public ScaledParametrisedSphere(
			String description,
			Vector3D centre,
			double radius,
			Vector3D pole,
			Vector3D phi0Direction,
			double sThetaMin, double sThetaMax,
			double sPhiMin, double sPhiMax,
			SurfaceProperty surfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		super(description, centre, radius, pole, phi0Direction, surfaceProperty, parent, studio);

		setThetaScaling(sThetaMin, sThetaMax);
		setPhiScaling(sPhiMin, sPhiMax);
	}


	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public ScaledParametrisedSphere(ScaledParametrisedSphere original)
	{
		super(original);
		
		setThetaScaling(original.getSThetaMin(), original.getSThetaMax());
		setPhiScaling(original.getSPhiMin(), original.getSPhiMax());
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Disc#clone()
	 */
	@Override
	public ScaledParametrisedSphere clone()
	{
		return new ScaledParametrisedSphere(this);
	}


	/**
	 * Returns the scaled polar angle theta and the scaled azimuthal angle phi that describe the point p on the sphere
	 * 
	 * @param p	the point on the sphere
	 * @return	(scaled theta, scaled phi)
	 * @see ParametrisedObject#getSurfaceCoordinates(Vector3D)
	 */
	@Override
	public Vector2D getSurfaceCoordinates(Vector3D p)
	{
		Vector2D thetaPhi = getThetaPhiForSurfacePoint(p);
		
		return(new Vector2D(
				theta2sTheta(thetaPhi.x),	// scaled theta
				phi2sPhi(thetaPhi.y)	// scaled phi
		));
	}

	@Override
	public Vector3D getPointForSurfaceCoordinates(double u, double v)
	{
		double
			theta = sTheta2theta(u),
			phi = sPhi2phi(v);
		
		return getSurfacePointForThetaPhi(theta, phi);
	}

	/**
	 * @return the names of the parameters, e.g. ("theta", "phi")
	 */
	@Override
	public ArrayList<String> getSurfaceCoordinateNames()
	{
		ArrayList<String> parameterNames = new ArrayList<String>(2);
		parameterNames.add("theta (scaled " + MyMath.doubleToString(sThetaMin) + " to " + MyMath.doubleToString(sThetaMax) + ")");
		parameterNames.add("phi (scaled " + MyMath.doubleToString(sPhiMin) + " to " + MyMath.doubleToString(sPhiMax) + ")");
		
		return parameterNames;
	}

	/**
	 * Returns the directions dP/dsTheta and dP/dsPhi on the surface of the sphere
	 *
	 * @see optics.raytrace.core.ParametrisedObject#getSurfaceCoordinateAxes(math.Vector3D)
	 */
	@Override
	public ArrayList<Vector3D> getSurfaceCoordinateAxes(Vector3D p)
//	public ArrayList<Vector3D> getVectorsForSurfacePoint(Vector3D p)
	{
		// the array holding the directions, initially unscaled
		ArrayList<Vector3D> ds = super.getSurfaceCoordinateAxes(p);
		
		// dP/dsr = dP/dR * dR/dsr
		ds.set(0, ds.get(0).getProductWith(1./dsThetadTheta));

		// dP/dsPhi = dP/dPhi * dPhi/dsPhi
		ds.set(1, ds.get(1).getProductWith(1./dsPhidPhi));

		return ds;
	}
	
	// methods that convert between scaled and unscaled coordinates
	
	public double theta2sTheta(double theta)
	{
		return sTheta0 + theta*dsThetadTheta;
	}
	
	public double sTheta2theta(double sTheta)
	{
		return (sTheta - sTheta0) / dsThetadTheta;
	}
	
	public double phi2sPhi(double phi)
	{
		return sPhi0 + phi*dsPhidPhi;
	}
	
	public double sPhi2phi(double sPhi)
	{
		return (sPhi - sPhi0) / dsPhidPhi; 
	}
	
	// setters and getters

	/**
	 * set sTheta0 and dsThetadTheta such that sTheta (scaled theta) runs from thetaMin to thetaMax
	 * @param sThetaMin
	 * @param sThetaMax
	 */
	public void setThetaScaling(double sThetaMin, double sThetaMax)
	{
		// first take a note of sThetaMin and sThetaMax
		this.sThetaMin = sThetaMin;
		this.sThetaMax = sThetaMax;
		
		// theta runs from 0 to pi
		sTheta0 = sThetaMin;
		dsThetadTheta = (sThetaMax - sThetaMin) / Math.PI;
	}

	public double getSThetaMin() {
		return sThetaMin;
	}

	public double getSThetaMax() {
		return sThetaMax;
	}
	
	/**
	 * set sPhi0 and dsPhidPhi such that sPhi (scaled phi) runs from sPhiMin to sPhiMax
	 * @param sPhiMin
	 * @param sPhiMax
	 */
	public void setPhiScaling(double sPhiMin, double sPhiMax)
	{
		// first take a note of sPhiMin and sPhiMax
		this.sPhiMin = sPhiMin;
		this.sPhiMax = sPhiMax;
		
		// phi runs from -Pi to Pi
		sPhi0 = (sPhiMax + sPhiMin)/2.0;
		dsPhidPhi = (sPhiMax - sPhiMin) / (2.0*Math.PI);
	}
	
	public double getSPhiMin() {
		return sPhiMin;
	}

	public double getSPhiMax() {
		return sPhiMax;
	}
	
	@Override
	public String getType()
	{
		return "Sphere";
	}
}
