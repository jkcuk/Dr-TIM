package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import optics.raytrace.core.ParametrisedObject;

public class DerivativeControlSurfaceRotating extends DerivativeControlSurface {
	private static final long serialVersionUID = -3598838329885511533L;
	
	/**
	 * rotation angle (in  radians)
	 */
	private double theta;
	
	public DerivativeControlSurfaceRotating(double theta, ParametrisedObject parametrisedObject, double transmissionCoefficient,
			boolean shadowThrowing) {
		super(parametrisedObject, transmissionCoefficient, shadowThrowing);

		setTheta(theta);
	}

	public DerivativeControlSurfaceRotating(double theta) {
		super();
		
		setTheta(theta);
	}

	//  getters & setters
	
	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	
	//  override  the relevant DerivativeControlSurface methods
	
	/**
	 * Override to customise
	 * @param pointOnSurface
	 * @return the (2x2) Jacobian matrix d (d_o - d_o0) / d (d_i  - d_i0) for the given pointOnSurface
	 */
	@Override
	public Matrix getJacobianOutwards(Vector3D pointOnSurface)
	{
		double[][] components = {
				{Math.cos(theta), Math.sin(theta)},	//  {0, 1},
				{-Math.sin(theta), Math.cos(theta)}	// {-1, 0}
			};
		return new Matrix(components);
	}

	
}
