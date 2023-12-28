
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
/**
 * Defining the method and its variables by name only i.e. like def(Function[x,y,z]:)
 * 
 * Then construct the restrictions/confines of the method i.e. assigning the variables and setting controls for the OOP such as 
 * shadow-throwing and transmission through the interface.
 */
public class EwanDerivativeControlSurfaceExpansion extends DerivativeControlSurface {

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
	
	/**
	 * derotation angle, in radians
	 */
	private double jacAngleRad;
	
	
	
	//  constructors
	
	public EwanDerivativeControlSurfaceExpansion(ParametrisedObject parametrisedObject,
			double transmissionCoefficient, boolean shadowThrowing, Vector3D eyePosition, Vector3D pointOnPlane,
			Vector3D normalisedPlaneNormal, double rotationAngleRad, double jacAngleRad) 
	{
		super(parametrisedObject, transmissionCoefficient, shadowThrowing);
		this.eyePosition = eyePosition;
		this.pointOnPlane = pointOnPlane;
		this.normalisedPlaneNormal = normalisedPlaneNormal;
		this.rotationAngleRad = rotationAngleRad;
		this.jacAngleRad = jacAngleRad;
	}

	/**
	 * Now, we are getting and setting, meaning we tell it that to get eye position we ask for 
	 *'eyeposition', and to set the 'eyeposition' we aks for 'eyeposition'. 
	 * For example
	 *public static void main(String[] args) {
  Vehicle v1 = new Vehicle();
  v1.setColor("Red");
  System.out.println(v1.getColor());
     * There, the output will be RED i.e. define a new object with the properties of Vehicle, set its colour to 
     *red, then use 'get' to get the new colour within the instance of this method. Solid. 
     * Therefore, if we want to use this method in another notebook, we invoke this method, define a new instance of it,
     *and then use the newly stored data to TIMage (data which we can call and check any time).

	 */
	
	
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
	 * @return the rotationAngleRad
	 */
	public double getJacAngleRad() {
		return jacAngleRad;
	}
	
	/**
	 * @param derotationAngleRad the derotationAngleRad to set
	 */
	public void setJacAngleRad(double jacAngleRad) {
		this.jacAngleRad = jacAngleRad;
	}

    /**
     * Now we override the previously defined variables from the first method called 'Derivative_Control_Surface'. Namely, we redefine 
     *some vectors such that they are now more general and that the outgoing Jacobian ensures that all deviations from the central value 
     *behave as for the correct rotation. 
     */

	
	// override  the relevant methods
	
	/**
	 * ensuring that the eye-to surface vector has the freedom of direction, but that the new direction is always outward
	 */
	
	@Override
	public Vector3D getDi0Outwards(Vector3D pointOnSurface) 
	{
		// di0,  but not  necessarily outwards
		Vector3D di0 =  Vector3D.difference(pointOnSurface, eyePosition);
		
		//  just in  case the new direction is inwards, make sure it's actually outwards
		return  di0.getProductWith(Orientation.getOrientation(di0, parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface)).getScalarProductSign());

	}
    /**
     * Now setting the outward direction to be the vector connecting the rotated position to the intersection of Di0- Do0 with the surface.
     * Again, apply the same precaution in case the new direction is inwards. 
     */
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
/**
 * Now, setting the Jacobian to be an anti-rotation matrix, such that the small deviations from the normal-calibrated rotation
 *are dealt with i.e. while the central value for any ingoing direction is rotated, the linear shifts in the x and y positions are not.
 *NOTE: try to derive this rexpression in Wolfram and see what you come away with.  
 *
 */
	@Override
	public Matrix getJacobianOutwards(Vector3D pointOnSurface) {
		double[][] components = {
				{Math.cos(jacAngleRad), Math.sin(jacAngleRad)},	//  {0, 1},
				{-Math.sin(jacAngleRad), Math.cos(jacAngleRad)}	// {-1, 0}
			};
		return new Matrix(components);

//		return super.getJacobianOutwards(pointOnSurface);
	}

}
