package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Geometry;
import math.MathException;
import math.Vector3D;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.Orientation;

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
	 * overall rotation angle, in radians
	 */
	private double rotationAngleRad;
	
	private double magnificationFactor;
	
	/**
	 * angle by which each pixel rotates transmitted light rays, in radians
	 */
	private double pixelRotationAngleRad;
	
	
	//  constructors
	
	public DerivativeControlSurfaceRotatingPhaseHologramApproximation(One2OneParametrisedObject parametrisedObject, Vector3D eyePosition, Vector3D pointOnPlane,
			Vector3D normalisedPlaneNormal, double rotationAngleRad, double magnificationFactor, double pixelRotationAngleRad, boolean pixellated, double pixelPeriodU, double pixelPeriodV,
			double transmissionCoefficient, boolean shadowThrowing) 
	{
		super(parametrisedObject, pixellated, pixelPeriodU, pixelPeriodV, transmissionCoefficient, shadowThrowing);
		this.eyePosition = eyePosition;
		this.pointOnPlane = pointOnPlane;
		this.normalisedPlaneNormal = normalisedPlaneNormal;
		this.rotationAngleRad = rotationAngleRad;
		this.magnificationFactor = magnificationFactor;
		this.pixelRotationAngleRad = pixelRotationAngleRad;
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

	/**
	 * @return the magnificationFactor
	 */
	public double getMagnificationFactor() {
		return magnificationFactor;
	}

	/**
	 * @param magnificationFactor the magnificationFactor to set
	 */
	public void setMagnificationFactor(double magnificationFactor) {
		this.magnificationFactor = magnificationFactor;
	}

	/**
	 * @return the pixelRotationAngleRad
	 */
	public double getPixelRotationAngleRad() {
		return pixelRotationAngleRad;
	}

	/**
	 * @param pixelRotationAngleRad the pixelRotationAngleRad to set
	 */
	public void setPixelRotationAngleRad(double pixelRotationAngleRad) {
		this.pixelRotationAngleRad = pixelRotationAngleRad;
	}



	// override  the relevant methods



	@Override
	public Vector3D getDi0Outwards(Vector3D pointOnSurface) 
	{
		//  pointOnSurface = new Vector3D(0, 0,  10);

		// di0,  but not  necessarily outwards
		Vector3D di0 =  Vector3D.difference(pointOnSurface, eyePosition);
		
		//  just in  case the new direction is inwards, make sure it's actually outwards
		return  di0.getProductWith(Orientation.getOrientation(di0, parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface)).getScalarProductSign());
	}

	@Override
	public Vector3D getDo0Outwards(Vector3D pointOnSurface)
	{
		// pointOnSurface = new Vector3D(0, 0,  10);
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
			Vector3D rotationCentre = Geometry.linePlaneIntersection(
					eyePosition,	// pointOnLine, 
					normalisedPlaneNormal,	// directionOfLine, rotation axis
					pointOnPlane, 
					normalisedPlaneNormal	// normalToPlane
				);
			Vector3D magnifiedRotatedPosition = Vector3D.sum(
					rotationCentre, 
					Vector3D.difference(rotatedPosition, rotationCentre).getProductWith(1/magnificationFactor)
				);
			// do0,  but not  necessarily outwards
			Vector3D do0 =  Vector3D.difference(magnifiedRotatedPosition, pointOnSurface);
			
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
				{Math.cos(-pixelRotationAngleRad), Math.sin(-pixelRotationAngleRad)},	//  {0, 1},
				{-Math.sin(-pixelRotationAngleRad), Math.cos(-pixelRotationAngleRad)}	// {-1, 0}
			};
		return new Matrix(components);

//		return super.getJacobianOutwards(pointOnSurface);
	}

}
