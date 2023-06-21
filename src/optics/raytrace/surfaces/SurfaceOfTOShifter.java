package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.ParametrisedInvertedSphere;
import optics.raytrace.sceneObjects.ParametrisedSphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;

/**
 * A spherical shell comprising a transformation-optics medium.
 * When seen from the outside, the inner sphere and anything within it appears shifted.
 * If an object inside the inner sphere appears shifted outside of the device, the device acts like an abyss cloak.
 * 
 * Calculations in J's lab book dated 21/8/19 (and later dates)
 * and in Mathematica notebook TOAbyssCloakMapping.nb
 * 
 * @author johannes
 */
public class SurfaceOfTOShifter extends SurfaceOfMetricSpace
{
	private static final long serialVersionUID = -2259016305852426223L;

	/**
	 * position of the abyss cloak's centre
	 */
	private Vector3D centre;
	
	/**
	 * the (outer) radius of the spherical transformation-optics medium
	 */
	private double radius;
	
	/**
	 * the inner radius, the space within which appears shifted when seen from the outside
	 */
	private double innerRadius;
	
	/**
	 * apparent shift of the inner sphere
	 */
	private Vector3D delta;
	
	public enum ShiftFunctionType
	{
		LINEAR("Linear shift function"),
		QUADRATIC("Quadratic shift function");

		private String description;

		private ShiftFunctionType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	private ShiftFunctionType shiftFunctionType;

		
	
	// constructors etc.

	/**
	 * Create a surface property that, when applied to a surface comprising concentric inner and outer spheres,
	 * makes the space between the spheres (a spherical shell) behave like a TO device that translates the space
	 * inside the inner sphere when seen from the outside
	 * 
	 * Once created, the surface must be set
	 */
	public SurfaceOfTOShifter(
			Vector3D centre,
			double radius,
			double innerRadius,
			Vector3D delta,
			ShiftFunctionType shiftFunctionType,
			double deltaTau,
			double deltaXMax,
			int maxSteps,
			IntegrationType integrationType,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(
				null,	// surface -- set later
				deltaTau,
				deltaXMax,
				maxSteps,
				integrationType,
				transmissionCoefficient,
				shadowThrowing
			);
		
		setCentre(centre);
		setRadius(radius);
		setInnerRadius(innerRadius);
		setDelta(delta);
		setShiftFunctionType(shiftFunctionType);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * 
	 * After cloning the surface, assign it to a suitable SceneObject (and set that SceneObject as the "surface" of the new SurfaceOfTOAbyssCloak)
	 * 
	 * @param original
	 */
	public SurfaceOfTOShifter(SurfaceOfTOShifter original)
	{
		this(
				original.getCentre(),
				original.getRadius(),
				original.getInnerRadius(),
				original.getDelta(),
				original.getShiftFunctionType(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}


	@Override
	public SurfaceOfTOShifter clone()
	{
		return new SurfaceOfTOShifter(this);
	}
	
	
	
	
	// internal variables
	private double radius2;
	private double innerRadius2;
	
	// setters & getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		radius2 = radius*radius;
	}

	public double getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(double innerRadius) {
		this.innerRadius = innerRadius;
		innerRadius2 = innerRadius*innerRadius;
	}

	public Vector3D getDelta() {
		return delta;
	}

	public void setDelta(Vector3D delta) {
		this.delta = delta;
	}
	
	public ShiftFunctionType getShiftFunctionType() {
		return shiftFunctionType;
	}

	public void setShiftFunctionType(ShiftFunctionType shiftFunctionType) {
		this.shiftFunctionType = shiftFunctionType;
	}

	
	
	/**
	 * Create a SceneObject that represents the spherical shell described by centre, (outer) radius and inner radius
	 * @param description
	 * @param parent
	 * @param studio
	 * @return
	 */
	public SceneObjectIntersectionSimple addSphericalShellSceneObjectAsSurface(
			String description,
			SceneObject parent,
			Studio studio
		)
	{
//		// create array lists for the positive and negative scene-object primitives
//		ArrayList<SceneObjectPrimitive> positiveSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>(1);
//		// ArrayList<SceneObjectPrimitive> negativeSceneObjectPrimitives = new ArrayList<SceneObjectPrimitive>(1);
//		
//		// create the SceneObjectPrimitiveIntersection representing the spherical shell...
//		SceneObjectPrimitiveIntersection s = new SceneObjectPrimitiveIntersection(
//				description,
//				positiveSceneObjectPrimitives,
//				null,	// negativeSceneObjectPrimitives,
//				parent,
//				studio
//			);
//		
//		// ... and add spheres representing the outer and inner surfaces of the spherical shell to the list of positive and negative scene-object primitives, respectively
//		// (so that the spherical shell is the intersection of the inside of the outer sphere and the outside of the inner sphere)
//		positiveSceneObjectPrimitives.add(new ParametrisedSphere(
//				"Outer sphere",	// description
//				centre,
//				radius,
//				this,	// surfaceProperty
//				s,	// parent
//				studio
//		));
//		positiveSceneObjectPrimitives.add(new ParametrisedInvertedSphere(
//				"Inner sphere",	// description
//				centre,
//				innerRadius,
//				this,	// surfaceProperty
//				s,	// parent
//				studio
//		));
		
		SceneObjectIntersectionSimple s = new SceneObjectIntersectionSimple(
				description,
				new ParametrisedSphere(
						"Outer sphere",	// description
						centre,
						radius,
						this,	// surfaceProperty
						null,	// parent
						studio
				),
				new ParametrisedInvertedSphere(
						"Inner sphere",	// description
						centre,
						innerRadius,
						this,	// surfaceProperty
						null,	// parent
						studio
				),
				parent,
				studio
			);
		
		// set the spherical shell to be the surface of this SurfaceProperty
		setSurface(s);
		
		// return the spherical shell
		return s;
	}
	
	
	// SurfaceOfMetricSpace methods

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#getRefractiveIndexTensor(math.Vector3D)
	 */
	public Matrix calculateEpsilonMuTensor(Vector3D x)
	{
		Vector3D rV = Vector3D.difference(x, centre);
		double r = rV.getLength();
		
		// the elements of the n tensor (which equals the epsilon tensor and the mu tensor)
		// calculated in Mathematica notebook TOAbyssCloakMapping, output in CForm, copied, and with the following replacements:
		// Power -> Math.pow
		// r0 -> radius
		// r1 -> innerRadius
		// x -> x.x (Whole word = true)
		// y -> x.y (Whole word = true)
		// z -> x.z (Whole word = true)
		// kx -> kx.x
		// ky -> kx.y
		// kz -> kx.z
		switch(shiftFunctionType)
		{
		case LINEAR:
			double[][] valsLinear =
		{
				{
					-((Math.pow(r,2)*Math.pow(radius - innerRadius,2) - 2*r*(radius - innerRadius)*(delta.y*x.y + delta.z*x.z) + Math.pow(delta.y*x.y + delta.z*x.z,2) + Math.pow(delta.x,2)*(Math.pow(x.y,2) + Math.pow(x.z,2)))/
							(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),-((-(Math.pow(delta.y,2)*x.x*x.y) - delta.x*x.y*(r*(-radius + innerRadius) + delta.x*x.x + delta.z*x.z) + 
									delta.y*(r*(radius - innerRadius)*x.x + x.z*(-(delta.z*x.x) + delta.x*x.z)))/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					-((delta.z*(r*(radius - innerRadius)*x.x + x.y*(-(delta.y*x.x) + delta.x*x.y)) - Math.pow(delta.z,2)*x.x*x.z - delta.x*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y)*x.z)/
							(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				},
				{
					-((-(Math.pow(delta.y,2)*x.x*x.y) - delta.x*x.y*(r*(-radius + innerRadius) + delta.x*x.x + delta.z*x.z) + 
							delta.y*(r*(radius - innerRadius)*x.x + x.z*(-(delta.z*x.x) + delta.x*x.z)))/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					-((Math.pow(r,2)*Math.pow(radius - innerRadius,2) + (Math.pow(delta.x,2) + Math.pow(delta.y,2))*Math.pow(x.x,2) + 2*delta.x*delta.z*x.x*x.z + (Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2) - 
							2*r*(radius - innerRadius)*(delta.x*x.x + delta.z*x.z))/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					-((-(Math.pow(delta.y,2)*x.y*x.z) - delta.z*x.y*(r*(-radius + innerRadius) + delta.x*x.x + delta.z*x.z) + delta.y*(r*(radius - innerRadius)*x.z + x.x*(delta.z*x.x - delta.x*x.z)))/
							(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				},
				{
					-((delta.z*(r*(radius - innerRadius)*x.x + x.y*(-(delta.y*x.x) + delta.x*x.y)) - Math.pow(delta.z,2)*x.x*x.z - 
							delta.x*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y)*x.z)/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					-((-(Math.pow(delta.y,2)*x.y*x.z) - delta.z*x.y*(r*(-radius + innerRadius) + delta.x*x.x + delta.z*x.z) + delta.y*(r*(radius - innerRadius)*x.z + x.x*(delta.z*x.x - delta.x*x.z)))/
							(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))),-((Math.pow(r,2)*Math.pow(radius - innerRadius,2) + (Math.pow(delta.x,2) + Math.pow(delta.z,2))*Math.pow(x.x,2) + 2*delta.x*delta.y*x.x*x.y + 
									(Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2) - 2*r*(radius - innerRadius)*(delta.x*x.x + delta.y*x.y))/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				}
		};

			return new Matrix(valsLinear);
		case QUADRATIC:
		default:
			double[][] valsQuadratic =
		{
				{
					(Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*innerRadius2*(delta.y*x.y + delta.z*x.z) - 2*radius2*(innerRadius2 + 2*delta.y*x.y + 2*delta.z*x.z) + 4*(Math.pow(delta.y*x.y + delta.z*x.z,2) + Math.pow(delta.x,2)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				     ((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),(-2*(2*Math.pow(delta.y,2)*x.x*x.y + delta.x*x.y*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z) + 
				         delta.y*(-(radius2*x.x) + innerRadius2*x.x + 2*delta.z*x.x*x.z - 2*delta.x*Math.pow(x.z,2))))/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
				    (-2*(delta.z*(-(radius2*x.x) + innerRadius2*x.x + 2*delta.y*x.x*x.y - 2*delta.x*Math.pow(x.y,2)) + 2*Math.pow(delta.z,2)*x.x*x.z + delta.x*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y)*x.z))/
				     ((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				},
				{
					(-2*
						       (2*Math.pow(delta.y,2)*x.x*x.y + delta.x*x.y*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z) + delta.y*(-(radius2*x.x) + innerRadius2*x.x + 2*delta.z*x.x*x.z - 2*delta.x*Math.pow(x.z,2))))/
						     ((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),(Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*(Math.pow(delta.x,2) + Math.pow(delta.y,2))*Math.pow(x.x,2) + 8*delta.x*delta.z*x.x*x.z + 
						       4*(Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2) + 4*innerRadius2*(delta.x*x.x + delta.z*x.z) - 2*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z))/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
						    (-2*(-2*delta.y*delta.z*Math.pow(x.x,2) + delta.y*(-radius2 + innerRadius2 + 2*delta.x*x.x)*x.z + 2*Math.pow(delta.y,2)*x.y*x.z + delta.z*x.y*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z)))/
						     ((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				},
				{
					(-2*
						       (delta.z*(-(radius2*x.x) + innerRadius2*x.x + 2*delta.y*x.x*x.y - 2*delta.x*Math.pow(x.y,2)) + 2*Math.pow(delta.z,2)*x.x*x.z + delta.x*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y)*x.z))/
						     ((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),(-2*(-2*delta.y*delta.z*Math.pow(x.x,2) + delta.y*(-radius2 + innerRadius2 + 2*delta.x*x.x)*x.z + 2*Math.pow(delta.y,2)*x.y*x.z + 
						         delta.z*x.y*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z)))/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
						    (Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*(Math.pow(delta.x,2) + Math.pow(delta.z,2))*Math.pow(x.x,2) + 8*delta.x*delta.y*x.x*x.y + 4*(Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2) + 4*innerRadius2*(delta.x*x.x + delta.y*x.y) - 
						       2*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y))/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))
				}
		};

			return new Matrix(valsQuadratic);
		}
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#dXdTau(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D dXdTau(Vector3D x, Vector3D k)
	{
		double r = Vector3D.getDistance(x, centre);

		// calculate from Hamilton's equation d xv / d \tau = \partial H / \partial kv (where xv and kv are vectors);
		// calculated in Mathematica notebook TOAbyssCloakMapping, output in CForm, copied, and with the following replacements:
		// Power -> Math.pow
		// r0 -> radius
		// r1 -> innerRadius
		// x -> x.x (Whole word = true)
		// y -> x.y (Whole word = true)
		// z -> x.z (Whole word = true)
		// kx -> kx.x
		// ky -> kx.y
		// kz -> kx.z
		switch(shiftFunctionType)
		{
		case LINEAR:
			return new Vector3D(
					(-2*(k.x*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*k.y*x.x*x.y - Math.pow(delta.y,2)*k.y*x.x*x.y - delta.y*delta.z*k.z*x.x*x.y + Math.pow(delta.x,2)*k.x*Math.pow(x.y,2) + Math.pow(delta.y,2)*k.x*Math.pow(x.y,2) + 
							delta.x*delta.z*k.z*Math.pow(x.y,2) - ((Math.pow(delta.x,2) + Math.pow(delta.z,2))*k.z*x.x + delta.x*delta.z*k.y*x.y + delta.y*(delta.z*k.y*x.x - 2*delta.z*k.x*x.y + delta.x*k.z*x.y))*x.z + 
							((Math.pow(delta.x,2) + Math.pow(delta.z,2))*k.x + delta.x*delta.y*k.y)*Math.pow(x.z,2) + r*(radius - innerRadius)*(delta.y*k.y*x.x + delta.z*k.z*x.x - 2*delta.y*k.x*x.y + delta.x*k.y*x.y - 2*delta.z*k.x*x.z + delta.x*k.z*x.z)))/
					(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),(-2*(-((delta.x*k.x + delta.z*k.z)*x.y*(r*(-radius + innerRadius) + delta.x*x.x + delta.z*x.z)) - Math.pow(delta.y,2)*x.y*(k.x*x.x + k.z*x.z) + 
							delta.y*(x.x*(k.x*r*(radius - innerRadius) + delta.z*k.z*x.x) - (k.z*r*(-radius + innerRadius) + delta.z*k.x*x.x + delta.x*k.z*x.x)*x.z + delta.x*k.x*Math.pow(x.z,2)) + 
							k.y*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + (Math.pow(delta.x,2) + Math.pow(delta.y,2))*Math.pow(x.x,2) + 2*delta.x*delta.z*x.x*x.z + (Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2) - 
									2*r*(radius - innerRadius)*(delta.x*x.x + delta.z*x.z))))/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),
					(-2*(delta.z*(x.x*(k.x*r*(radius - innerRadius) + delta.y*k.y*x.x) - (k.y*r*(-radius + innerRadius) + delta.y*k.x*x.x + delta.x*k.y*x.x)*x.y + delta.x*k.x*Math.pow(x.y,2)) + 
							k.z*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + (Math.pow(delta.x,2) + Math.pow(delta.z,2))*Math.pow(x.x,2) + 2*delta.x*delta.y*x.x*x.y + (Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2) - 
									2*r*(radius - innerRadius)*(delta.x*x.x + delta.y*x.y)) - (delta.x*k.x + delta.y*k.y)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y)*x.z - Math.pow(delta.z,2)*(k.x*x.x + k.y*x.y)*x.z))/
					(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))
					);
		case QUADRATIC:
		default:
			return new Vector3D(
					(-4*(2*Math.pow(delta.y,2)*k.y*x.x*x.y + 2*Math.pow(delta.z,2)*k.z*x.x*x.z + 2*delta.y*k.z*x.y*(delta.z*x.x + delta.x*x.z) + delta.x*(-radius2 + innerRadius2 + 2*delta.x*x.x)*(k.y*x.y + k.z*x.z) + 
							delta.z*(k.z*(-(radius2*x.x) + innerRadius2*x.x - 2*delta.x*Math.pow(x.y,2)) + 2*delta.x*k.y*x.y*x.z) + delta.y*k.y*(-(radius2*x.x) + innerRadius2*x.x + 2*x.z*(delta.z*x.x - delta.x*x.z))) + 
							2*k.x*(Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*innerRadius2*(delta.y*x.y + delta.z*x.z) - 2*radius2*(innerRadius2 + 2*delta.y*x.y + 2*delta.z*x.z) + 4*(Math.pow(delta.y*x.y + delta.z*x.z,2) + Math.pow(delta.x,2)*(Math.pow(x.y,2) + Math.pow(x.z,2)))))/
					((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),(-4*(delta.x*k.x + delta.z*k.z)*x.y*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z) - 8*Math.pow(delta.y,2)*x.y*(k.x*x.x + k.z*x.z) + 
							2*k.y*(Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*(Math.pow(delta.x,2) + Math.pow(delta.y,2))*Math.pow(x.x,2) + 8*delta.x*delta.z*x.x*x.z + 4*(Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2) + 
									4*innerRadius2*(delta.x*x.x + delta.z*x.z) - 2*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z)) + 
							4*delta.y*(2*delta.z*k.z*Math.pow(x.x,2) + k.z*(radius2 - innerRadius2 - 2*delta.x*x.x)*x.z + k.x*(radius2*x.x - innerRadius2*x.x - 2*delta.z*x.x*x.z + 2*delta.x*Math.pow(x.z,2))))/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					(2*k.z*(Math.pow(radius2,2) + Math.pow(innerRadius2,2) + 4*(Math.pow(delta.x,2) + Math.pow(delta.z,2))*Math.pow(x.x,2) + 8*delta.x*delta.y*x.x*x.y + 4*(Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2) + 4*innerRadius2*(delta.x*x.x + delta.y*x.y) - 
							2*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y)) + 4*delta.z*(2*delta.y*k.y*Math.pow(x.x,2) + k.y*(radius2 - innerRadius2 - 2*delta.x*x.x)*x.y + k.x*(radius2*x.x - innerRadius2*x.x - 2*delta.y*x.x*x.y + 2*delta.x*Math.pow(x.y,2))) - 
							4*(delta.x*k.x + delta.y*k.y)*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y)*x.z - 8*Math.pow(delta.z,2)*(k.x*x.x + k.y*x.y)*x.z)/((radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))
					);
		}
	}

	/* (non-Javadoc)
	 * @see optics.rax.ytrace.surfaces.SurfaceOfMetricSpace#dKdTau(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D dKdTau(Vector3D x, Vector3D k)
	{
		double r = Vector3D.getDistance(x, centre);

		// calculate from Hamilton's equation d kv / d \tau = -\partial H / \partial xv (where xv and kv are vectors);
		// calculated in Mathematica notebook TOAbyssCloakMapping, output in CForm, copied, and with the following replacements:
		// Power -> Math.pow
		// r0 -> radius
		// r1 -> innerRadius
		// x -> x.x (Whole word = true)
		// y -> x.y (Whole word = true)
		// z -> x.z (Whole word = true)
		// kx -> kx.x
		// ky -> kx.y
		// kz -> kx.z
		switch(shiftFunctionType)
		{
		case LINEAR:
		return new Vector3D(
				// dkx / dtau
				-(((delta.x + ((-radius + innerRadius)*x.x)/r)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
				          Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
				          2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
				          Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
				          Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
				             (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
				          (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
				          2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
				             delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(r*(radius - innerRadius)*Math.pow(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z,2))) - 
				   (x.x*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
				        Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
				        2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
				        Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
				        Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
				           (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
				        (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
				        2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
				           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(Math.pow(Math.pow(r,2),1.5)*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)) + 
				   (-2*(-(delta.y*k.x*k.y) - delta.z*k.x*k.z + delta.x*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2)))*r*(radius - innerRadius) - 2*Math.pow(delta.x,2)*x.x + 2*Math.pow(delta.x,2)*Math.pow(k.y,2)*x.x + 2*Math.pow(delta.y,2)*Math.pow(k.y,2)*x.x + 
				      4*delta.y*delta.z*k.y*k.z*x.x + 2*Math.pow(delta.x,2)*Math.pow(k.z,2)*x.x + 2*Math.pow(delta.z,2)*Math.pow(k.z,2)*x.x + 2*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius - innerRadius,2)*x.x - 2*delta.x*delta.y*x.y - 
				      2*Math.pow(delta.x,2)*k.x*k.y*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.y - 2*delta.y*delta.z*k.x*k.z*x.y - 2*delta.x*delta.z*k.y*k.z*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.y - 
				      2*(Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.z - 
				      (2*(radius - innerRadius)*x.x*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
				           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z))))/r)/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),
				// dky / dtau
				   -(((delta.y + ((-radius + innerRadius)*x.y)/r)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
					          Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
					          2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
					          Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
					          Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
					             (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
					          (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
					          2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
					             delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(r*(radius - innerRadius)*Math.pow(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z,2))) - 
					   (x.y*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
					        Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
					        2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
					        Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
					        Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
					           (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
					        (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
					        2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
					           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(Math.pow(Math.pow(r,2),1.5)*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)) + 
					   (-2*(-(delta.x*k.x*k.y) - delta.z*k.y*k.z + delta.y*(-1 + Math.pow(k.x,2) + Math.pow(k.z,2)))*r*(radius - innerRadius) - 2*delta.x*delta.y*x.x - 2*Math.pow(delta.x,2)*k.x*k.y*x.x - 2*Math.pow(delta.y,2)*k.x*k.y*x.x - 
					      2*delta.y*delta.z*k.x*k.z*x.x - 2*delta.x*delta.z*k.y*k.z*x.x + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x - 2*Math.pow(delta.y,2)*x.y + 2*Math.pow(delta.x,2)*Math.pow(k.x,2)*x.y + 2*Math.pow(delta.y,2)*Math.pow(k.x,2)*x.y + 
					      4*delta.x*delta.z*k.x*k.z*x.y + 2*Math.pow(delta.y,2)*Math.pow(k.z,2)*x.y + 2*Math.pow(delta.z,2)*Math.pow(k.z,2)*x.y + 2*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius - innerRadius,2)*x.y - 
					      2*(Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.z - 
					      (2*(radius - innerRadius)*x.y*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
					           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z))))/r)/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),
				// dkz / dtau
					   -((x.z*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
						          Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
						          2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
						          Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
						          Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
						             (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
						          (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
						          2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
						             delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(Math.pow(Math.pow(r,2),1.5)*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))) - 
						   ((delta.z + ((-radius + innerRadius)*x.z)/r)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,2)*Math.pow(radius - innerRadius,2) - Math.pow(delta.x,2)*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 
						        Math.pow(delta.y,2)*Math.pow(k.y,2)*Math.pow(x.x,2) + 2*delta.y*delta.z*k.y*k.z*Math.pow(x.x,2) + Math.pow(delta.x,2)*Math.pow(k.z,2)*Math.pow(x.x,2) + Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.x,2) - 2*delta.x*delta.y*x.x*x.y - 
						        2*Math.pow(delta.x,2)*k.x*k.y*x.x*x.y - 2*Math.pow(delta.y,2)*k.x*k.y*x.x*x.y - 2*delta.y*delta.z*k.x*k.z*x.x*x.y - 2*delta.x*delta.z*k.y*k.z*x.x*x.y + 2*delta.x*delta.y*Math.pow(k.z,2)*x.x*x.y - Math.pow(delta.y,2)*Math.pow(x.y,2) + 
						        Math.pow(delta.x,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.x,2)*Math.pow(x.y,2) + 2*delta.x*delta.z*k.x*k.z*Math.pow(x.y,2) + Math.pow(delta.y,2)*Math.pow(k.z,2)*Math.pow(x.y,2) + 
						        Math.pow(delta.z,2)*Math.pow(k.z,2)*Math.pow(x.y,2) - 2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
						           (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y)*x.z + 
						        (Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*Math.pow(x.z,2) - 
						        2*r*(radius - innerRadius)*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
						           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)))))/(r*(radius - innerRadius)*Math.pow(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z,2)) + 
						   (-2*(delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)) - delta.x*k.x*k.z - delta.y*k.y*k.z)*r*(radius - innerRadius) - 
						      2*((Math.pow(delta.x,2)*k.x*k.z + delta.z*k.x*(delta.y*k.y + delta.z*k.z) + delta.x*(delta.z - delta.z*Math.pow(k.y,2) + delta.y*k.y*k.z))*x.x + 
						         (Math.pow(delta.y,2)*k.y*k.z + delta.z*k.y*(delta.x*k.x + delta.z*k.z) + delta.y*(delta.z - delta.z*Math.pow(k.x,2) + delta.x*k.x*k.z))*x.y) + 
						      2*(Math.pow(delta.x*k.x + delta.y*k.y,2) + Math.pow(delta.z,2)*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2)))*x.z + 2*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius - innerRadius,2)*x.z - 
						      (2*(radius - innerRadius)*x.z*(-(delta.z*k.z*(k.x*x.x + k.y*x.y)) + delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*x.z + delta.y*(-(k.x*k.y*x.x) + Math.pow(k.x,2)*x.y + (-1 + Math.pow(k.z,2))*x.y - k.y*k.z*x.z) + 
						           delta.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z))))/r)/(r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))
		  );
		case QUADRATIC:
		default:
		return new Vector3D(
				(-2*(2*(delta.y*k.y + delta.z*k.z)*(-radius2 + innerRadius2 + 2*delta.y*x.y + 2*delta.z*x.z)*(-2*(delta.y*k.y + delta.z*k.z)*x.x + k.x*(-radius2 + innerRadius2 + 2*delta.y*x.y + 2*delta.z*x.z)) + 
				        Math.pow(delta.x,3)*(-4*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + 4*Math.pow(k.x,2)*(Math.pow(x.y,2) + Math.pow(x.z,2))) + 
				        4*Math.pow(delta.x,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*radius2*x.x - (-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*innerRadius2*x.x - 2*delta.y*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x*x.y - 
				           2*delta.z*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x*x.z + 2*delta.y*k.x*k.y*(Math.pow(x.y,2) + Math.pow(x.z,2)) + 2*delta.z*k.x*k.z*(Math.pow(x.y,2) + Math.pow(x.z,2))) + 
				        delta.x*((1 + Math.pow(k.x,2) - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(radius2,2) + (1 + Math.pow(k.x,2) - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(innerRadius2,2) + 
				           4*(1 + Math.pow(k.x,2) - Math.pow(k.y,2) - Math.pow(k.z,2))*innerRadius2*(delta.y*x.y + delta.z*x.z) - 2*(1 + Math.pow(k.x,2) - Math.pow(k.y,2) - Math.pow(k.z,2))*radius2*(innerRadius2 + 2*delta.y*x.y + 2*delta.z*x.z) + 
				           4*Math.pow(delta.z,2)*(Math.pow(k.z,2)*(-Math.pow(x.x,2) + Math.pow(x.y,2)) + (1 + Math.pow(k.x,2) - Math.pow(k.y,2))*Math.pow(x.z,2)) + 
				           4*Math.pow(delta.y,2)*((1 + Math.pow(k.x,2) - Math.pow(k.z,2))*Math.pow(x.y,2) + Math.pow(k.y,2)*(-Math.pow(x.x,2) + Math.pow(x.z,2))) + 
				           8*delta.y*delta.z*(-(Math.pow(k.y,2)*x.y*x.z) + (1 + Math.pow(k.x,2) - Math.pow(k.z,2))*x.y*x.z + k.y*k.z*(-Math.pow(x.x,2) + Math.pow(x.y,2) + Math.pow(x.z,2))))))/
				    ((radius2 - innerRadius2)*Math.pow(-radius2 + innerRadius2 + 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z),2)),(-2*
				      (-2*(delta.x*k.x + delta.z*k.z)*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z)*(2*(delta.x*k.x + delta.z*k.z)*x.y + k.y*(radius2 - innerRadius2 - 2*delta.x*x.x - 2*delta.z*x.z)) + 
				        4*Math.pow(delta.y,3)*(-((-1 + Math.pow(k.x,2) + Math.pow(k.z,2))*Math.pow(x.y,2)) + Math.pow(k.y,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 
				        4*Math.pow(delta.y,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.z,2))*(radius2 - innerRadius2)*x.y - 2*delta.z*(-1 + Math.pow(k.x,2) + Math.pow(k.z,2))*x.y*x.z + 2*delta.z*k.y*k.z*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				           2*delta.x*(-(Math.pow(k.x,2)*x.x*x.y) - (-1 + Math.pow(k.z,2))*x.x*x.y + k.x*k.y*(Math.pow(x.x,2) + Math.pow(x.z,2)))) + 
				        delta.y*(-((-1 + Math.pow(k.x,2) - Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius2,2)) - (-1 + Math.pow(k.x,2) - Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius2,2) - 
				           4*(-1 + Math.pow(k.x,2) - Math.pow(k.y,2) + Math.pow(k.z,2))*innerRadius2*(delta.x*x.x + delta.z*x.z) + 2*(-1 + Math.pow(k.x,2) - Math.pow(k.y,2) + Math.pow(k.z,2))*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.z*x.z) + 
				           4*Math.pow(delta.z,2)*(Math.pow(k.z,2)*(x.x - x.y)*(x.x + x.y) + (1 - Math.pow(k.x,2) + Math.pow(k.y,2))*Math.pow(x.z,2)) + 
				           4*Math.pow(delta.x,2)*((1 + Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.x,2)*(-Math.pow(x.y,2) + Math.pow(x.z,2))) + 
				           8*delta.x*delta.z*(-(Math.pow(k.x,2)*x.x*x.z) + (1 + Math.pow(k.y,2) - Math.pow(k.z,2))*x.x*x.z + k.x*k.z*(Math.pow(x.x,2) - Math.pow(x.y,2) + Math.pow(x.z,2))))))/
				    ((radius2 - innerRadius2)*Math.pow(-radius2 + innerRadius2 + 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z),2)),(-2*
				      (-2*(delta.x*k.x + delta.y*k.y)*(-radius2 + innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y)*(k.z*(radius2 - innerRadius2 - 2*delta.x*x.x - 2*delta.y*x.y) + 2*(delta.x*k.x + delta.y*k.y)*x.z) + 
				        4*Math.pow(delta.z,2)*(2*(delta.x*k.x + delta.y*k.y)*k.z*(Math.pow(x.x,2) + Math.pow(x.y,2)) + (-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y))*x.z) + 
				        4*Math.pow(delta.z,3)*(Math.pow(k.z,2)*(Math.pow(x.x,2) + Math.pow(x.y,2)) - (-1 + Math.pow(k.x,2) + Math.pow(k.y,2))*Math.pow(x.z,2)) + 
				        delta.z*(-((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(radius2,2)) - (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(innerRadius2,2) - 
				           4*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) - Math.pow(k.z,2))*innerRadius2*(delta.x*x.x + delta.y*x.y) + 2*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) - Math.pow(k.z,2))*radius2*(innerRadius2 + 2*delta.x*x.x + 2*delta.y*x.y) + 
				           4*Math.pow(delta.y,2)*((1 - Math.pow(k.x,2) + Math.pow(k.z,2))*Math.pow(x.y,2) + Math.pow(k.y,2)*(x.x - x.z)*(x.x + x.z)) + 
				           4*Math.pow(delta.x,2)*((1 - Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.x,2)*(x.y - x.z)*(x.y + x.z)) + 
				           8*delta.x*delta.y*(-(Math.pow(k.x,2)*x.x*x.y) + (1 - Math.pow(k.y,2) + Math.pow(k.z,2))*x.x*x.y + k.x*k.y*(Math.pow(x.x,2) + Math.pow(x.y,2) - Math.pow(x.z,2))))))/
				    ((radius2 - innerRadius2)*Math.pow(-radius2 + innerRadius2 + 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z),2))
				);
		}
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#calculateK(math.Vector3D, math.Vector3D)
	 */
	@Override
	public Vector3D calculateK(Vector3D x, Vector3D d)
	{
		// calculate the direction of kv (vector k) from Hamilton's equation d xv / d \tau = \partial H / \partial kv (where xv is vector x)
		// (note that the LHS of this equation is proportional to the light-ray direction, dv; we set it equal to alpha dv)
		// and its length from the requirement that H(kv) = 0;
		
		Vector3D rV = Vector3D.difference(x, centre);
		double r = rV.getLength();
//		double r0r1 = radius - innerRadius;
//		double rr0 = r - radius;
//		double rr1 = r - innerRadius;
//		double d2 = d.getModSquared();

		// calculated in Mathematica notebook TOAbyssCloakMapping, output in CForm, copied, and with the following replacements:
		// Power -> Math.pow
		// r0 - r1 -> r0r1
		// r - r0 -> rr0
		// r - r1 -> rr1
		// x -> x.x (Whole word = true)
		// y -> x.y (Whole word = true)
		// z -> x.z (Whole word = true)
		// kx -> kx.x
		// ky -> kx.y
		// kz -> kx.z
//		double alpha = Math.abs(
//					(-2*r*Math.pow(r0r1,3) - 12*deltaX*(rr0)*(rr1)*x.x)/
//					Math.sqrt(r*(36*Math.pow(deltaX,2)*Math.pow(d.x,2)*r*Math.pow(rr0,2)*Math.pow(rr1,2) + d2*r*Math.pow(r0r1,6) + 12*deltaX*d.x*(rr0)*(rr1)*Math.pow(r0r1,3)*(d.x*x.x + d.y*x.y + d.z*x.z)))
//				   );
		double alpha;
		switch(shiftFunctionType)
		{
		case LINEAR:
			alpha = (2*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))/
			Math.sqrt(Math.pow(d.x,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.x*r*(-radius + innerRadius)*x.x + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.x,2)) + 
					Math.pow(d.y,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.y*r*(-radius + innerRadius)*x.y + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2)) + 
					2*d.y*d.z*(delta.z*r*(-radius + innerRadius)*x.y + Math.pow(delta.z,2)*x.y*x.z + (delta.y*r*(-radius + innerRadius) + Math.pow(delta.x,2)*x.y + Math.pow(delta.y,2)*x.y)*x.z) + 
					Math.pow(d.z,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.z*r*(-radius + innerRadius)*x.z + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2)) + 
					2*d.x*(delta.y*d.y*r*(-radius + innerRadius)*x.x + delta.z*d.z*r*(-radius + innerRadius)*x.x + Math.pow(delta.y,2)*x.x*(d.y*x.y + d.z*x.z) + Math.pow(delta.z,2)*x.x*(d.y*x.y + d.z*x.z) + delta.x*(-(r*radius) + r*innerRadius + delta.x*x.x)*(d.y*x.y + d.z*x.z)));
		case QUADRATIC:
		default:
			alpha = (2*(-radius2 + innerRadius2 + 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))/
			Math.sqrt(Math.pow(d.x,2)*(Math.pow(radius2 - innerRadius2,2) + 4*delta.x*(-radius2 + innerRadius2)*x.x + 
					4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.x,2)) + 
					Math.pow(d.y,2)*(Math.pow(radius2 - innerRadius2,2) + 4*delta.y*(-radius2 + innerRadius2)*x.y + 
							4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2)) + 
					4*d.y*d.z*(delta.z*(-radius2 + innerRadius2)*x.y + 2*Math.pow(delta.x,2)*x.y*x.z + 2*Math.pow(delta.z,2)*x.y*x.z + delta.y*(-radius2 + innerRadius2 + 2*delta.y*x.y)*x.z) + 
					Math.pow(d.z,2)*(Math.pow(radius2 - innerRadius2,2) + 4*delta.z*(-radius2 + innerRadius2)*x.z + 
							4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2)) + 
					4*d.x*(delta.y*d.y*(-radius2 + innerRadius2)*x.x + delta.z*d.z*(-radius2 + innerRadius2)*x.x + 2*Math.pow(delta.y,2)*x.x*(d.y*x.y + d.z*x.z) + 
							2*Math.pow(delta.z,2)*x.x*(d.y*x.y + d.z*x.z) + delta.x*(-radius2 + innerRadius2 + 2*delta.x*x.x)*(d.y*x.y + d.z*x.z)));
		}

		switch(shiftFunctionType)
		{
		case LINEAR:
			return new Vector3D(
					-(alpha*(-(delta.y*d.y*r*radius*x.x) - delta.z*d.z*r*radius*x.x + delta.y*d.y*r*innerRadius*x.x + delta.z*d.z*r*innerRadius*x.x + 
							d.x*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.x*r*(-radius + innerRadius)*x.x + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.x,2)) - delta.x*d.y*r*radius*x.y + delta.x*d.y*r*innerRadius*x.y + 
							Math.pow(delta.x,2)*d.y*x.x*x.y + Math.pow(delta.y,2)*d.y*x.x*x.y + Math.pow(delta.z,2)*d.y*x.x*x.y + d.z*(delta.x*r*(-radius + innerRadius) + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*x.x)*x.z))/
					(2.*r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),
					-(alpha*(d.y*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.y*r*(-radius + innerRadius)*x.y + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2)) - delta.y*r*(radius - innerRadius)*(d.x*x.x + d.z*x.z) + 
							Math.pow(delta.y,2)*x.y*(d.x*x.x + d.z*x.z) + x.y*(delta.x*d.x*r*(-radius + innerRadius) + Math.pow(delta.x,2)*(d.x*x.x + d.z*x.z) + delta.z*(d.z*r*(-radius + innerRadius) + delta.z*d.x*x.x + delta.z*d.z*x.z))))/
					(2.*r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z)),
					-(alpha*(-(delta.z*r*(radius - innerRadius)*(d.x*x.x + d.y*x.y)) + Math.pow(delta.z,2)*(d.x*x.x + d.y*x.y)*x.z + 
							(delta.x*d.x*r*(-radius + innerRadius) + Math.pow(delta.x,2)*(d.x*x.x + d.y*x.y) + delta.y*(d.y*r*(-radius + innerRadius) + delta.y*d.x*x.x + delta.y*d.y*x.y))*x.z + 
							d.z*(Math.pow(r,2)*Math.pow(radius - innerRadius,2) + 2*delta.z*r*(-radius + innerRadius)*x.z + (Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.z,2))))/
					(2.*r*(radius - innerRadius)*(r*(-radius + innerRadius) + delta.x*x.x + delta.y*x.y + delta.z*x.z))
					);
		case QUADRATIC:
		default:
			return new Vector3D(
					(alpha*(d.x*(Math.pow(radius2 - innerRadius2,2) + 4*delta.x*(-radius2 + innerRadius2)*x.x + 
							4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.x,2)) + 
							2*(delta.y*d.y*(-radius2 + innerRadius2)*x.x + delta.z*d.z*(-radius2 + innerRadius2)*x.x + 2*Math.pow(delta.y,2)*x.x*(d.y*x.y + d.z*x.z) + 
									2*Math.pow(delta.z,2)*x.x*(d.y*x.y + d.z*x.z) + delta.x*(-radius2 + innerRadius2 + 2*delta.x*x.x)*(d.y*x.y + d.z*x.z))))/
					(2.*(radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					(alpha*(d.y*(Math.pow(radius2 - innerRadius2,2) + 4*delta.y*(-radius2 + innerRadius2)*x.y + 
							4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*Math.pow(x.y,2)) - 2*delta.y*(radius2 - innerRadius2)*(d.x*x.x + d.z*x.z) + 
							4*Math.pow(delta.y,2)*x.y*(d.x*x.x + d.z*x.z) + 2*x.y*(delta.x*d.x*(-radius2 + innerRadius2) + 2*Math.pow(delta.x,2)*(d.x*x.x + d.z*x.z) + 
									delta.z*(2*delta.z*d.x*x.x + d.z*(-radius2 + innerRadius2 + 2*delta.z*x.z)))))/
					(2.*(radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z))),
					(alpha*((radius2 - innerRadius2)*(d.z*(radius2 - innerRadius2) - 2*delta.z*(d.x*x.x + d.y*x.y)) + 
							2*(delta.x*d.x*(-radius2 + innerRadius2) + delta.y*d.y*(-radius2 + innerRadius2) + 2*Math.pow(delta.x,2)*(d.x*x.x + d.y*x.y) + 
									2*Math.pow(delta.y,2)*(d.x*x.x + d.y*x.y) + 2*delta.z*(-(d.z*radius2) + d.z*innerRadius2 + delta.z*d.x*x.x + delta.z*d.y*x.y))*x.z + 
							4*(Math.pow(delta.x,2) + Math.pow(delta.y,2) + Math.pow(delta.z,2))*d.z*Math.pow(x.z,2)))/
					(2.*(radius2 - innerRadius2)*(radius2 - innerRadius2 - 2*(delta.x*x.x + delta.y*x.y + delta.z*x.z)))
					);
		}
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.surfaces.SurfaceOfMetricSpace#correctLengthOfK(math.Vector3D, math.Vector3D)
	 */
//	@Override
//	public Vector3D getKWithCorrectLength(Vector3D x, Vector3D k)
//	{
//		double r = x.getLength();
//		
//		// the factor by which k needs to be scaled
//		// calculated in Mathematica notebook TOAbyssCloakMapping, output in CForm, copied, and with the following replacements:
//		// Power -> Math.pow
//		// r0 -> radius
//		// r1 -> innerRadius
//		// x -> x.x (Whole word = true)
//		// y -> x.y (Whole word = true)
//		// z -> x.z (Whole word = true)
//		// kx -> kx.x
//		// ky -> kx.y
//		// kz -> kx.z
//		double beta = 
//				(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaX*(r - radius)*(r - innerRadius)*x.x)/
//				   Math.sqrt(Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + Math.pow(k.y,2)*(36*Math.pow(deltaX,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 
//				        12*deltaX*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				        12*deltaX*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*radius*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				        Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaX*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaX*x.x) + 
//				           Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaX*x.x,2) + 36*Math.pow(deltaX,2)*Math.pow(x.y,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))) + 
//				           3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))))) + 72*Math.pow(deltaX,2)*k.y*k.z*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*x.y*x.z - 
//				     12*deltaX*k.x*r*(r - radius)*(r - innerRadius)*Math.pow(radius - innerRadius,3)*(k.y*x.y + k.z*x.z) + Math.pow(k.z,2)*
//				      (36*Math.pow(deltaX,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
//				        12*deltaX*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				        12*deltaX*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*radius*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				        Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaX*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaX*x.x) + 
//				           Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaX*x.x,2) + 36*Math.pow(deltaX,2)*Math.pow(x.z,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))));
//		
//		// return the scaled k vector
//		return k.getProductWith(beta);
//	}
}
