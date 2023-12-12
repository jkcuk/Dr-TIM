package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Geometry;
import math.MathException;
import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.ParametrisedObject;

/**
 * A derivative control surface that represents a  refractive/holographic rotator.
 * 
 * The eye position  is considered to be on the  inside of the derivative-control  surface
 */
public class DerivativeControlSurfaceRotatingPhaseHologramApproximation extends DerivativeControlSurface {

	private static final long serialVersionUID = -4547196223761878021L;
	
	private Vector3D eyePosition;
	private Vector3D pointOnPlane;
	
	/**
	 * plane normal, which is also the rotation axis
	 */
	private Vector3D normalisedPlaneNormal;
	
	/**
	 * rotation angle, in radians
	 */
	private double rotationAngleRad;
	
	
	//  constructors
	
	public DerivativeControlSurfaceRotatingPhaseHologramApproximation(ParametrisedObject parametrisedObject,
			double transmissionCoefficient, boolean shadowThrowing, Vector3D eyePosition, Vector3D pointOnPlane,
			Vector3D normalisedPlaneNormal, double rotationAngleRad) 
	{
		super(parametrisedObject, transmissionCoefficient, shadowThrowing);
		this.eyePosition = eyePosition;
		this.pointOnPlane = pointOnPlane;
		this.normalisedPlaneNormal = normalisedPlaneNormal;
		this.rotationAngleRad = rotationAngleRad;
	}

	
	
	//  getters & setters
	
	/**
	 * @return the eyePosition
	 */
	public Vector3D getEyePosition() {
		return eyePosition;
	}

	/**
	 * @param eyePosition the eyePosition to set
	 */
	public void setEyePosition(Vector3D eyePosition) {
		this.eyePosition = eyePosition;
	}

	/**
	 * @return the pointOnPlane
	 */
	public Vector3D getPointOnPlane() {
		return pointOnPlane;
	}

	/**
	 * @param pointOnPlane the pointOnPlane to set
	 */
	public void setPointOnPlane(Vector3D pointOnPlane) {
		this.pointOnPlane = pointOnPlane;
	}

	/**
	 * @return the normalisedPlaneNormal
	 */
	public Vector3D getNormalisedPlaneNormal() {
		return normalisedPlaneNormal;
	}

	/**
	 * @param normalisedPlaneNormal the normalisedPlaneNormal to set
	 */
	public void setNormalisedPlaneNormal(Vector3D normalisedPlaneNormal) {
		this.normalisedPlaneNormal = normalisedPlaneNormal.getNormalised();
	}

	/**
	 * @return the rotationAngleRad
	 */
	public double getRotationAngleRad() {
		return rotationAngleRad;
	}

	/**
	 * @param rotationAngleRad the rotationAngleRad to set
	 */
	public void setRotationAngleRad(double rotationAngleRad) {
		this.rotationAngleRad = rotationAngleRad;
	}


	
	// override  the relevant methods
	

	@Override
	public Vector3D getDi0Outwards(Vector3D pointOnSurface) 
	{
		// di0,  but not  necessarily outwards
		Vector3D di0 =  Vector3D.difference(pointOnSurface, eyePosition);
		
		//  just in  case the new direction is inwards, make sure it's actually outwards
		return  di0.getProductWith(Orientation.getOrientation(di0, parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface)).getScalarProductSign());

	}

	@Override
	public Vector3D getDo0Outwards(Vector3D pointOnSurface)
	{
		try {
			Vector3D apparentPosition = Geometry.linePlaneIntersection(
					pointOnSurface,	//  point  on  line
					Vector3D.difference(pointOnSurface, eyePosition),	//  direction of line
					pointOnPlane,
					normalisedPlaneNormal
				);
			Vector3D rotatedPosition  = Geometry.rotatePositionVector(
					apparentPosition,	//  position
					eyePosition,	// point on  rotation axis
					normalisedPlaneNormal,	//  normalised  rotation -axis direction
					rotationAngleRad
				);
			// do0,  but not  necessarily outwards
			Vector3D do0 =  Vector3D.difference(rotatedPosition, pointOnSurface);
			
			//  just in  case the new direction is inwards, make sure it's actually outwards
			return  do0.getProductWith(Orientation.getOrientation(do0, parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface)).getScalarProductSign());
		} catch (MathException e) {
			e.printStackTrace();
		}
		//  hopefully this will never  happen
		return super.getDo0Outwards(pointOnSurface);
	}

	@Override
	public Matrix getJacobianOutwards(Vector3D pointOnSurface) {
		double[][] components = {
				{Math.cos(-rotationAngleRad), Math.sin(-rotationAngleRad)},	//  {0, 1},
				{-Math.sin(-rotationAngleRad), Math.cos(-rotationAngleRad)}	// {-1, 0}
			};
		return new Matrix(components);

//		return super.getJacobianOutwards(pointOnSurface);
	}

}
