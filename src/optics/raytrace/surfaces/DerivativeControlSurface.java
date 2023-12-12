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
 * A surface that changes one particular pair of light-ray directions, Di0 on the inside and Do0 on the outside, into other.
 * If the incident light-ray direction is not either Di0 (if the light ray is incident from  the inside) or Do0 (if incident from the outside),
 * a Jacobian matrix is used to calculate the corresponding outgoing light-ray direction.
 * 
 * The directions Di0 and Do0, as well as the Jacobian, can vary from point to point.
 * 
 * The details are as follows:
 * (1) The (3D) incident light-ray direction is normalised and projected into the tangent plane at the ray-scene object intersection point.
 * (2) If the ray is incident from the scene object's inside, the (2D) projected light-ray direction is called d_i0, otherwise d_o0.
 * (3) The relationship between the inside light-ray direction and outside direction is given by the equation
 *       d_o = d_o0 + JacobianIn2Out (d_i  - d_i0)
 *     i.e. (d_o -  d_o0) = JacobianIn2Out (d_i - d_i0).
 *     This equation is used to calculate the outgoing light-ray direction.
 *     d_i0 and d_o0 are the (2D) projections into the tangent plane at the ray-scene object intersection point of two (3D) light-ray directions.
 * (4) The (2D) outgoing light-ray direction is converted into a 3D light-ray direction.
 * 
 * @author Ewan, Maik, Johannes
 *
 */
public class DerivativeControlSurface extends SurfacePropertyPrimitive
{
	private static final long serialVersionUID = -8805761209985615963L;	
	
	/**
	 * the parametrised SceneObject this surface property is associated with
	 */
	protected One2OneParametrisedObject parametrisedObject;
	
	// TODO make use of this normalisation type!
	//  for the normalisation
	public enum RayDirectionNormalisationType
	{
		LENGTH1("Length = 1"),
		PERPENDICULAR_COMPONENT_1("Perpendicular component = 1");
		
		private final String description;
		public String toString() {return description;}
		RayDirectionNormalisationType(String description) {this.description = description;}
	}
	
	private boolean pixellated;
	private double pixelPeriodU;
	private double pixelPeriodV;
	
	
	public DerivativeControlSurface(
			One2OneParametrisedObject parametrisedObject, 
			boolean pixellated, 
			double pixelPeriodU, 
			double pixelPeriodV, 
			double transmissionCoefficient, 
			boolean shadowThrowing
		)
	{
		super(transmissionCoefficient, shadowThrowing);
		
		setParametrisedObject(parametrisedObject);
		setPixellated(pixellated);
		setPixelPeriodU(pixelPeriodU);
		setPixelPeriodV(pixelPeriodV);
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
				false,	//  pixellated
				1,	// pixelPeriodU
				1,	// pixelPeriodV
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
				isPixellated(),
				getPixelPeriodU(),
				getPixelPeriodV(),
				getTransmissionCoefficient(),
				isShadowThrowing()
			);
	}


	// setters & getters
	
	public One2OneParametrisedObject getParametrisedObject() {
		return parametrisedObject;
	}

	public void setParametrisedObject(One2OneParametrisedObject parametrisedObject) {
		this.parametrisedObject = parametrisedObject;
	}

	/**
	 * @return the pixellated
	 */
	public boolean isPixellated() {
		return pixellated;
	}

	/**
	 * @param pixellated the pixellated to set
	 */
	public void setPixellated(boolean pixellated) {
		this.pixellated = pixellated;
	}

	/**
	 * @return the pixelPeriodU
	 */
	public double getPixelPeriodU() {
		return pixelPeriodU;
	}

	/**
	 * @param pixelPeriodU the pixelPeriodU to set
	 */
	public void setPixelPeriodU(double pixelPeriodU) {
		this.pixelPeriodU = pixelPeriodU;
	}

	/**
	 * @return the pixelPeriodV
	 */
	public double getPixelPeriodV() {
		return pixelPeriodV;
	}

	/**
	 * @param pixelPeriodV the pixelPeriodV to set
	 */
	public void setPixelPeriodV(double pixelPeriodV) {
		this.pixelPeriodV = pixelPeriodV;
	}

	

	
	// methods that define the surface
	

	/**
	 * Override to customise
	 * @param pointOnSurface
	 * @return	the 3D light-ray direction
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
	
	public RayDirectionNormalisationType rayDirectionNormalisationType(Vector3D pointOnSurface)
	{
		return RayDirectionNormalisationType.LENGTH1;
	}
	
	
	
	// useful  methods
	
	/**
	 * @param pointOnSurface
	 * @param orientation
	 * @return	the "inside" direction Di0 with the given orientation (i.e. inwards or outwards)
	 */
	public Vector3D getDi0(Vector3D pointOnSurface, Orientation orientation)
	{
		Vector3D di0 = getDi0Outwards(pointOnSurface);

		return di0.getProductWith(orientation.getScalarProductSign());
	}

	public Vector3D getDo0(Vector3D pointOnSurface, Orientation orientation)
	{
		Vector3D do0 = getDo0Outwards(pointOnSurface);

		return do0.getProductWith(orientation.getScalarProductSign());
	}

	public Matrix getJacobian(Vector3D pointOnSurface, Orientation orientation)
	{
		// calculate outwards Jacobian
		Matrix j = getJacobianOutwards(pointOnSurface);
		
		switch(orientation)
		{
		case INWARDS:
			return j.inverse();
		case OUTWARDS:
		default:
			return j;
		}
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
	 * @param orientation
	 * @return
	 * @throws EvanescentException
	 */
	public Vector3D jamaVector2D2Vector3D(Matrix a, Orientation orientation, Vector3D u, Vector3D v, Vector3D n)
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
		double an = orientation.getScalarProductSign()*Math.sqrt(an2);
		
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
		
		Vector3D pixelCentre;
		if(pixellated)
		{
			Vector2D uv = parametrisedObject.getSurfaceCoordinates(i.p);
			
			// calculate u and v parameters of pixel centre
			double uC = Math.floor(uv.x/pixelPeriodU + 0.5)*pixelPeriodU;
			double vC = Math.floor(uv.y/pixelPeriodV + 0.5)*pixelPeriodV;
			
			pixelCentre = parametrisedObject.getPointForSurfaceCoordinates(uC, vC);
		}
		else
			pixelCentre = i.p;
		
		// get the basis vectors
		// TODO do we want those at the pixelCentre or at i.p?
		ArrayList<Vector3D> normalisedAxes = getNormalisedSurfaceCoordinateAxes(pixelCentre);
		Vector3D uHat = normalisedAxes.get(0);
		Vector3D vHat = normalisedAxes.get(1);
		Vector3D nHat = parametrisedObject.getNormalisedOutwardsSurfaceNormal(pixelCentre);
		
		// is the ray approaching inwards or outwards?
		Orientation orientation = Orientation.getOrientation(ray, nHat);
		//  boolean inwards = Vector3D.scalarProduct(ray.getD(), n) < 0;
		
		Vector3D d0, dPrime0;
		switch(orientation)
		{
		case INWARDS:
			d0 = getDo0(pixelCentre, orientation);
			dPrime0 = getDi0(pixelCentre, orientation);
			break;
		case OUTWARDS:
		default:
			d0 = getDi0(pixelCentre, orientation);
			dPrime0 = getDo0(pixelCentre, orientation);
		}
		
		// calculate the 2D vector that corresponds to the incident light-ray direction
		Matrix v2 = vector3D2JamaVector2D(ray.getD(), uHat, vHat).minus(vector3D2JamaVector2D(d0, uHat, vHat));
		
		//  get the Jacobian for the point  on the surface
		Matrix j = getJacobian(pixelCentre, orientation);
		
		// calculate the 2D vector that corresponds to the outgoing light-ray direction...
		Matrix vPrime2 = j.times(v2).plus(vector3D2JamaVector2D(dPrime0, uHat, vHat));
		
		// ... and  turn that into  a 3D vector
		Vector3D newRayDirection = jamaVector2D2Vector3D(vPrime2, orientation, uHat, vHat, nHat);

		
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
