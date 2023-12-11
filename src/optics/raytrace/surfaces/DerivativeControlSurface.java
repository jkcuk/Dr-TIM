package optics.raytrace.surfaces;

import java.util.ArrayList;

import Jama.Matrix;
import math.*;
import optics.DoubleColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.sceneObjects.ParametrisedPlane;


/**
 * 
 * A surface that changes one particular incident light-ray direction to a corresponding outgoing light-ray direction,  and if the incident light-ray direction
 * is  not that  particular one then it  uses the Jacobian to  calculate the  corresponding  outgoing  light-ray direction.
 * 
 * The relationship between the  inside light-ray direction  and outside direction is given  by the equation
 *     d_i0  + (d_i - d_i0) =  d_o0  + (d_o - d_o0) = d_o0 + JacobianIn2Out (d_i  - d_i0)
 * i.e. (d_o -  d_o0) = JacobianIn2Out (d_i - d_i0).
 * 
 * Those directions and the  Jacobian vary from point to point.
 * 
 * @author Ewan, Maik, Johannes
 *
 */
public class DerivativeControlSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -8805761209985615963L;

//	/**
//	 * the u direction
//	 */
//	private Vector3D uHat;
//	
//	/**
//	 * the v direction
//	 */
//	private Vector3D vHat;
//	
//	/**
//	 * outwards-facing  normal (normalised)
//	 */
//	private Vector3D nHat;
	
	
	/**
	 * the parametrised SceneObject this surface property is associated with
	 */
	private ParametrisedObject parametrisedObject;
	
	// TODO allow for a different normalisation (n component = 1)
	
	
	
	public DerivativeControlSurface(ParametrisedObject parametrisedObject, double transmissionCoefficient, boolean shadowThrowing)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		setParametrisedObject(parametrisedObject);
	}
	
	/**
	 * Default
	 */
	public DerivativeControlSurface()
	{
		this(
				new ParametrisedPlane(
						"Plane", // description
						new Vector3D(0, 0, 10),	// pointOnPlane
						new Vector3D(0, 0, 1),	// normal
						new Vector3D(1, 0, 0),	// v1
						new Vector3D(0, 1, 0),	// v2
						SurfaceColour.BLUE_SHINY,	// sp
						null,	// parent
						null	// studio
					),
				DEFAULT_TRANSMISSION_COEFFICIENT,
				false
			);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DerivativeControlSurface clone()
	{
		return new DerivativeControlSurface(
				getParametrisedObject(),
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
	}


	// setters & getters
	
	public ParametrisedObject getParametrisedObject() {
		return parametrisedObject;
	}

	public void setParametrisedObject(ParametrisedObject parametrisedObject) {
		this.parametrisedObject = parametrisedObject;
	}


	
	// methods that define the surface
	
	/**
	 * Override to customise
	 * @param pointOnSurface
	 * @return
	 */
	/**
	 * @param pointOnSurface
	 * @return
	 */
	public Vector3D getDi0Outwards(Vector3D pointOnSurface)
	{
		return parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface);
	}
	
	/**
	 * Override to customise
	 * @param pointOnSurface
	 * @return
	 */
	public Vector3D getDo0Outwards(Vector3D pointOnSurface)
	{
		return parametrisedObject.getNormalisedOutwardsSurfaceNormal(pointOnSurface);
	}
	
	/**
	 * Override to customise
	 * @param pointOnSurface
	 * @return the (2x2) Jacobian matrix d (d_o - d_o0) / d (d_i  - d_i0) for the given pointOnSurface
	 */
	public Matrix getJacobianOutwards(Vector3D pointOnSurface)
	{
//		double theta = MyMath.deg2rad(45);
//		double[][] components = {
//				{Math.cos(theta), Math.sin(theta)},	//  {0, 1},
//				{-Math.sin(theta), Math.cos(theta)}	// {-1, 0}
//			};
		double[][] components = {
				{1, 0},
				{0, 1}
			};
		return new Matrix(components);
	}
	
	
	
	// useful  methods
	
	public Vector3D getDi0(Vector3D pointOnSurface, boolean inwards)
	{
		Vector3D di0 = getDi0Outwards(pointOnSurface);

		if(!inwards) return di0;
		
		return di0.getProductWith(-1);
	}

	public Vector3D getDo0(Vector3D pointOnSurface, boolean inwards)
	{
		return null;
	}

	public Matrix getJacobian(Vector3D pointOnSurface, boolean inwards)
	{
		// calculate outwards Jacobian
		Matrix j = getJacobianOutwards(pointOnSurface);
		
		if(!inwards) return j;
		
		return j.inverse();
	}
	
	public ArrayList<Vector3D> getNormalisedSurfaceCoordinateAxes(Vector3D pointOnSurface)
	{
		ArrayList<Vector3D> axes  =  parametrisedObject.getSurfaceCoordinateAxes(pointOnSurface);

		// normalise all  the vectors
		for(int  i=0;i<2; i++)
			axes.set(i, axes.get(i).getNormalised());
		
		return axes;
	}
	
	
	/**
	 * @param v3D
	 * @return	the u and v components of the given Vector3D, in the form of a Jama vector
	 */
	public Matrix vector3D2JamaVector2D(Vector3D v3D, Vector3D u, Vector3D v)
	{
		Vector3D v3Dn = v3D.getNormalised();
//		return new Vector2D(
//				Vector3D.scalarProduct(vn, uHat),
//				Vector3D.scalarProduct(vn, vHat)
//			);
		// ArrayList<Vector3D> axes  =  parametrisedObject.getSurfaceCoordinateAxes(pointOnSurface);
		double[][] components = {
				{Vector3D.scalarProduct(v3Dn, u)},
				{Vector3D.scalarProduct(v3Dn, v)}
			};
		return new Matrix(components);
	}

	/**
	 * @param v2D
	 * @param inwards
	 * @return
	 * @throws EvanescentException
	 */
	public Vector3D jamaVector2D2Vector3D(Matrix a, boolean inwards, Vector3D u, Vector3D v, Vector3D n)
	throws EvanescentException
	{
		// extract the u and v components of the vector
		double au = a.get(0, 0);
		double av = a.get(1, 0);

		// the square of the normal component
		double an2 = 1.-au*au-av*av;
		// double vn2 = 1.-v.x*v.x-v.y*v.y;
		
		// check for evanescence
		if(an2 < 0)
		{
			// evanescent!
			throw new EvanescentException("Outgoing light-ray direction evanescent...");
		}
		
		// calculate the normal component
		double an = (inwards?-1:1)*Math.sqrt(an2);
		
		// return the 3D vector
		// ArrayList<Vector3D> axes = parametrisedObject.getSurfaceCoordinateAxes(pointOnSurface);
		return Vector3D.sum(
				u.getProductWith(au),
				v.getProductWith(av),
				n.getProductWith(an)
			);
	}
	
	
	
	//  SurfacePropertyPrimitive class methods
	
	@Override
	public DoubleColour getColour(Ray ray, RaySceneObjectIntersection i,
			SceneObject scene, LightSource l, int traceLevel, RaytraceExceptionHandler raytraceExceptionHandler)
	throws RayTraceException
	{
		if(traceLevel <= 0) return DoubleColour.BLACK;
		
		// get the basis vectors
		ArrayList<Vector3D> normalisedAxes = getNormalisedSurfaceCoordinateAxes(i.p);
		Vector3D u = normalisedAxes.get(0);
		Vector3D v = normalisedAxes.get(1);
		Vector3D n = parametrisedObject.getNormalisedOutwardsSurfaceNormal(i.p);
		
		// is the ray approaching inwards or outwards?
		boolean inwards = Vector3D.scalarProduct(ray.getD(), n) < 0;
		
		// calculate the 2D vector that corresponds to the incident light-ray direction
		Matrix v2 = vector3D2JamaVector2D(ray.getD(), u, v).minus(vector3D2JamaVector2D(inwards?getDo0(i.p, inwards):getDi0(i.p, inwards), u, v));
		
		//  get the Jacobian for the point  on the surface
		Matrix j = getJacobian(i.p, inwards);
		
		// calculate the 2D vector that corresponds to the outgoing light-ray direction...
		Matrix vPrime2 = j.times(v2).plus(vector3D2JamaVector2D(inwards?getDi0(i.p, inwards):getDo0(i.p, inwards), u, v));
		
		// ... and  turn that into  a 3D vector
		Vector3D newRayDirection = jamaVector2D2Vector3D(vPrime2, inwards, u, v, n);

		
		// launch a new ray from here
		
		return scene.getColourAvoidingOrigin(
			ray.getBranchRay(i.p, newRayDirection, i.t, ray.isReportToConsole()),
			i.o,
			l,
			scene,
			traceLevel-1,
			raytraceExceptionHandler
		).multiply(getTransmissionCoefficient());
	}
	
}
