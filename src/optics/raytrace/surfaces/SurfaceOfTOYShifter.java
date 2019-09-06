package optics.raytrace.surfaces;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.ParametrisedInvertedSphere;
import optics.raytrace.sceneObjects.ParametrisedSphere;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;

/**
 * A spherical shell comprising a transformation-optics medium.
 * When seen from the outside, the inner sphere and anything within it appears shifted in the y direction.
 * If an object inside the inner sphere appears shifted outside of the device, the device acts like an abyss cloak.
 * 
 * Calculations in J's lab book dated 21/8/19 (and later dates)
 * and in Mathematica notebook TOAbyssCloakMapping.nb
 * 
 * @see optics.raytrace.surfaces.SurfaceOfTOXShifter
 * @author johannes
 */
public class SurfaceOfTOYShifter extends SurfaceOfMetricSpace
{
	private static final long serialVersionUID = -7156975876307752534L;

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
	 * for the moment, the apparent shift of the space inside the spherical shell is by deltaY in the y direction
	 * TODO extend to arbitrary shifts
	 */
	private double deltaY;
		
	
	// constructors etc.

	/**
	 * Create a surface property that, when applied to a surface comprising concentric inner and outer spheres,
	 * makes the space between the spheres (a spherical shell) behave like a TO device that translates the space
	 * inside the inner sphere when seen from the outside
	 * 
	 * Once created, the surface must be set
	 */
	public SurfaceOfTOYShifter(
			Vector3D centre,
			double radius,
			double innerRadius,
			double deltaY,
			double deltaTau,
			double deltaYMax,
			int maxSteps,
			IntegrationType integrationType,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(
				null,	// surface -- set later
				deltaTau,
				deltaYMax,
				maxSteps,
				integrationType,
				transmissionCoefficient,
				shadowThrowing
			);
		
		setCentre(centre);
		setRadius(radius);
		setInnerRadius(innerRadius);
		setDeltaY(deltaY);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * 
	 * After cloning the surface, assign it to a suitable SceneObject (and set that SceneObject as the "surface" of the new SurfaceOfTOAbyssCloak)
	 * 
	 * @param original
	 */
	public SurfaceOfTOYShifter(SurfaceOfTOYShifter original)
	{
		this(
				original.getCentre(),
				original.getRadius(),
				original.getInnerRadius(),
				original.getDeltaY(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}


	@Override
	public SurfaceOfTOYShifter clone()
	{
		return new SurfaceOfTOYShifter(this);
	}
	
	
	
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
	}

	public double getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(double innerRadius) {
		this.innerRadius = innerRadius;
	}

	public double getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(double deltaY) {
		this.deltaY = deltaY;
	}
	
	
	
	/**
	 * Create a SceneObject that represents the spherical shell described by centre, (outer) radius and inner radius
	 * @param description
	 * @param parent
	 * @param studio
	 * @return
	 */
	public SceneObjectIntersection addSphericalShellSceneObjectAsSurface(
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
		
		SceneObjectIntersection s = new SceneObjectIntersection(
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
		double[][] vals =
				{
						{
							1 + (6*deltaY*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3)),
							(-6*deltaY*(r - radius)*(r - innerRadius)*x.x)/(r*Math.pow(radius - innerRadius,3)),
							0
						},
						{
							(-6*deltaY*(r - radius)*(r - innerRadius)*x.x)/(r*Math.pow(radius - innerRadius,3)),
							((1 + (6*deltaY*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3)))*
							(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.x,2) + 36*Math.pow(deltaY,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2)))/
							Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y,2),
							(-6*deltaY*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3))
						},
						{
							0,
							(-6*deltaY*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3)),
							1 + (6*deltaY*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3))
						}
				};
		
		return new Matrix(vals);
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
		return new Vector3D(
				(2*(k.x*r*Math.pow(radius - innerRadius,3) - 6*deltaY*k.y*(r - radius)*(r - innerRadius)*x.x + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y))/(r*Math.pow(radius - innerRadius,3)),
				   (2*(k.y*Math.pow(r,2)*Math.pow(radius - innerRadius,6) - 6*deltaY*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*(k.x*x.x + k.z*x.z) + 
				        36*Math.pow(deltaY,2)*k.y*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))/(r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)),
				   (2*k.z*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) - 12*deltaY*k.y*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3))
				);
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
		return new Vector3D(
				// dkx / dtau
				(x.x*(12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				        6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				        12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				        Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				           72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				           Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				              6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)) + 
				   (((Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaY*(r - radius)*x.x*x.y)/r + (6*deltaY*(r - innerRadius)*x.x*x.y)/r)*
				      (12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				        6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				        12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				        Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				           72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				           Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				              6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				    (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y,2)) - 
				   (12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + (12*deltaY*k.x*k.y*(r - radius)*Math.pow(x.x,2)*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				      (12*deltaY*k.x*k.y*(r - innerRadius)*Math.pow(x.x,2)*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				      (6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*x.x*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				      (6*deltaY*(-1 + Math.pow(k.z,2))*(r - innerRadius)*x.x*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				      12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-((Math.pow(radius - innerRadius,3)*x.x)/r) - (6*deltaY*(r - radius)*x.x*x.y)/r - (6*deltaY*(r - innerRadius)*x.x*x.y)/r) + 
				      6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*((Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaY*(r - radius)*x.x*x.y)/r + (6*deltaY*(r - innerRadius)*x.x*x.y)/r) + 
				      2*(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y)*((k.x*Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaY*k.x*(r - radius)*x.x*x.y)/r + (6*deltaY*k.x*(r - innerRadius)*x.x*x.y)/r) - 
				      (12*deltaY*k.y*k.z*(r - radius)*x.x*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r - (12*deltaY*k.y*k.z*(r - innerRadius)*x.x*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r - 
				      12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*((Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaY*(r - radius)*x.x*x.y)/r + (6*deltaY*(r - innerRadius)*x.x*x.y)/r)*x.z + 
				      Math.pow(k.y,2)*(72*Math.pow(deltaY,2)*Math.pow(r,4)*x.x + 72*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*x.x - 144*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*x.x - 144*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*x.x + 
				         Math.pow(r,2)*(72*Math.pow(deltaY,2)*Math.pow(radius,2)*x.x + 288*Math.pow(deltaY,2)*radius*innerRadius*x.x + 72*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*x.x) + 144*Math.pow(deltaY,2)*Math.pow(r,2)*x.x*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				         216*Math.pow(deltaY,2)*r*(radius + innerRadius)*x.x*(Math.pow(x.x,2) + Math.pow(x.z,2)) - (72*Math.pow(deltaY,2)*radius*innerRadius*(radius + innerRadius)*x.x*(Math.pow(x.x,2) + Math.pow(x.z,2)))/r + 
				         2*x.x*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				            6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))))/
				    (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)),
				// dky / dtau
				    (x.y*(12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				            6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				            12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				            Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				               72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				               Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                  6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				        (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)) + 
				       ((6*deltaY*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaY*(r - radius)*Math.pow(x.y,2))/r + (6*deltaY*(r - innerRadius)*Math.pow(x.y,2))/r)*
				          (12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				            6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				            12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				            Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				               72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				               Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                  6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				        (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y,2)) - 
				       ((12*deltaY*k.x*k.y*(r - radius)*x.x*x.y*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + (12*deltaY*k.x*k.y*(r - innerRadius)*x.x*x.y*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				          6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				          (6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*Math.pow(x.y,2)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				          (6*deltaY*(-1 + Math.pow(k.z,2))*(r - innerRadius)*Math.pow(x.y,2)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))/r + 
				          12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-6*deltaY*(r - radius)*(r - innerRadius) - (Math.pow(radius - innerRadius,3)*x.y)/r - (6*deltaY*(r - radius)*Math.pow(x.y,2))/r - (6*deltaY*(r - innerRadius)*Math.pow(x.y,2))/r) + 
				          6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(6*deltaY*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaY*(r - radius)*Math.pow(x.y,2))/r + (6*deltaY*(r - innerRadius)*Math.pow(x.y,2))/r) + 
				          2*(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y)*(6*deltaY*k.x*(r - radius)*(r - innerRadius) + (k.x*Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaY*k.x*(r - radius)*Math.pow(x.y,2))/r + 
				             (6*deltaY*k.x*(r - innerRadius)*Math.pow(x.y,2))/r) - (12*deltaY*k.y*k.z*(r - radius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r - 
				          (12*deltaY*k.y*k.z*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r - 
				          12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(6*deltaY*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaY*(r - radius)*Math.pow(x.y,2))/r + (6*deltaY*(r - innerRadius)*Math.pow(x.y,2))/r)*x.z + 
				          Math.pow(k.y,2)*(144*Math.pow(deltaY,2)*Math.pow(r,2)*x.y*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 216*Math.pow(deltaY,2)*r*(radius + innerRadius)*x.y*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				             (72*Math.pow(deltaY,2)*radius*innerRadius*(radius + innerRadius)*x.y*(Math.pow(x.x,2) + Math.pow(x.z,2)))/r + 
				             2*x.y*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))))/
				        (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)),
				// dkz / dtau
				        (x.z*(12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				                6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				                12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				                Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                   72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				                   Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                      6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				            (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)) + 
				           (((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaY*(r - radius)*x.y*x.z)/r + (6*deltaY*(r - innerRadius)*x.y*x.z)/r)*
				              (12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + 
				                6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + Math.pow(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y,2) - 
				                12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				                Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                   72*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 72*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
				                   Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                      6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/
				            (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y,2)) - 
				           (-12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y) + (12*deltaY*k.x*k.y*(r - radius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r + 
				              (12*deltaY*k.x*k.y*(r - innerRadius)*x.x*(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r + 
				              (6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r + 
				              (6*deltaY*(-1 + Math.pow(k.z,2))*(r - innerRadius)*x.y*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z)/r - 
				              (12*deltaY*k.y*k.z*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*Math.pow(x.z,2))/r - 
				              (12*deltaY*k.y*k.z*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*Math.pow(x.z,2))/r + 
				              12*deltaY*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*(-((Math.pow(radius - innerRadius,3)*x.z)/r) - (6*deltaY*(r - radius)*x.y*x.z)/r - (6*deltaY*(r - innerRadius)*x.y*x.z)/r) + 
				              6*deltaY*(-1 + Math.pow(k.z,2))*(r - radius)*(r - innerRadius)*x.y*((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaY*(r - radius)*x.y*x.z)/r + (6*deltaY*(r - innerRadius)*x.y*x.z)/r) - 
				              12*deltaY*k.y*k.z*(r - radius)*(r - innerRadius)*x.z*((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaY*(r - radius)*x.y*x.z)/r + (6*deltaY*(r - innerRadius)*x.y*x.z)/r) + 
				              2*(k.x*r*Math.pow(radius - innerRadius,3) + 6*deltaY*k.x*(r - radius)*(r - innerRadius)*x.y)*((k.x*Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaY*k.x*(r - radius)*x.y*x.z)/r + (6*deltaY*k.x*(r - innerRadius)*x.y*x.z)/r) + 
				              Math.pow(k.y,2)*(72*Math.pow(deltaY,2)*Math.pow(r,4)*x.z + 72*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*x.z - 144*Math.pow(deltaY,2)*Math.pow(r,3)*(radius + innerRadius)*x.z - 144*Math.pow(deltaY,2)*r*radius*innerRadius*(radius + innerRadius)*x.z + 
				                 Math.pow(r,2)*(72*Math.pow(deltaY,2)*Math.pow(radius,2)*x.z + 288*Math.pow(deltaY,2)*radius*innerRadius*x.z + 72*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*x.z) + 144*Math.pow(deltaY,2)*Math.pow(r,2)*x.z*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                 216*Math.pow(deltaY,2)*r*(radius + innerRadius)*x.z*(Math.pow(x.x,2) + Math.pow(x.z,2)) - (72*Math.pow(deltaY,2)*radius*innerRadius*(radius + innerRadius)*x.z*(Math.pow(x.x,2) + Math.pow(x.z,2)))/r + 
				                 2*x.z*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 15*Math.pow(radius,4)*Math.pow(innerRadius,2) - 20*Math.pow(radius,3)*Math.pow(innerRadius,3) + Math.pow(innerRadius,6) + 36*Math.pow(deltaY,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) - 
				                    6*radius*innerRadius*(Math.pow(innerRadius,4) - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))))/
				            (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))
		  );
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
//					(-2*r*Math.pow(r0r1,3) - 12*deltaY*(rr0)*(rr1)*x.x)/
//					Math.sqrt(r*(36*Math.pow(deltaY,2)*Math.pow(d.x,2)*r*Math.pow(rr0,2)*Math.pow(rr1,2) + d2*r*Math.pow(r0r1,6) + 12*deltaY*d.x*(rr0)*(rr1)*Math.pow(r0r1,3)*(d.x*x.x + d.y*x.y + d.z*x.z)))
//				   );
		double alpha = Math.sqrt(
				(4*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y,2))/
				   (Math.pow(d.x,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 
				        36*Math.pow(deltaY,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.x,2)) + 
				     Math.pow(d.y*r*Math.pow(radius - innerRadius,3) + 6*deltaY*d.y*(r - radius)*(r - innerRadius)*x.y,2) + 
				     12*deltaY*d.y*d.z*(r - radius)*(r - innerRadius)*
				      (r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				     12*deltaY*d.x*(r - radius)*(r - innerRadius)*x.x*
				      (d.y*r*Math.pow(radius - innerRadius,3) + 6*deltaY*d.y*(r - radius)*(r - innerRadius)*x.y + 
				        6*deltaY*d.z*(r - radius)*(r - innerRadius)*x.z) + 
				     Math.pow(d.z,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 
				        36*Math.pow(deltaY,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2)))
				   );

		return new Vector3D(
				(alpha*(d.x*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 
				        36*Math.pow(deltaY,2)*d.x*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.x,2) + 
				        6*deltaY*(r - radius)*(r - innerRadius)*x.x*
				         (d.y*r*Math.pow(radius - innerRadius,3) + 6*deltaY*d.y*(r - radius)*(r - innerRadius)*x.y + 
				           6*deltaY*d.z*(r - radius)*(r - innerRadius)*x.z)))/
				    (2.*r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y)),
				   (alpha*(d.y*r*Math.pow(radius - innerRadius,3) + 6*deltaY*d.y*(r - radius)*(r - innerRadius)*x.y + 
				        6*deltaY*(r - radius)*(r - innerRadius)*(d.x*x.x + d.z*x.z)))/(2.*r*Math.pow(radius - innerRadius,3)),
				   (alpha*(d.z*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 
				        6*deltaY*(r - radius)*(r - innerRadius)*
				         (d.y*r*Math.pow(radius - innerRadius,3) + 6*deltaY*d.x*(r - radius)*(r - innerRadius)*x.x + 
				           6*deltaY*d.y*(r - radius)*(r - innerRadius)*x.y)*x.z + 
				        36*Math.pow(deltaY,2)*d.z*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2)))/
				    (2.*r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaY*(r - radius)*(r - innerRadius)*x.y))
			);
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
//				(-(r*Math.pow(radius - innerRadius,3)) - 6*deltaY*(r - radius)*(r - innerRadius)*x.x)/
//				   Math.sqrt(Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + Math.pow(k.y,2)*(36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 
//				        12*deltaY*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaY*(radius + innerRadius)*x.x) - 6*deltaY*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				        12*deltaY*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaY*(radius + innerRadius)*x.x) - 6*deltaY*radius*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				        Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaY*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaY*x.x) + 
//				           Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaY*x.x,2) + 36*Math.pow(deltaY,2)*Math.pow(x.y,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaY*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))) + 
//				           3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))))) + 72*Math.pow(deltaY,2)*k.y*k.z*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*x.y*x.z - 
//				     12*deltaY*k.x*r*(r - radius)*(r - innerRadius)*Math.pow(radius - innerRadius,3)*(k.y*x.y + k.z*x.z) + Math.pow(k.z,2)*
//				      (36*Math.pow(deltaY,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaY,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
//				        12*deltaY*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaY*(radius + innerRadius)*x.x) - 6*deltaY*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				        12*deltaY*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaY*(radius + innerRadius)*x.x) - 6*deltaY*radius*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				        Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaY*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaY*x.x) + 
//				           Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaY*x.x,2) + 36*Math.pow(deltaY,2)*Math.pow(x.z,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaY*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaY,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))))));
//		
//		// return the scaled k vector
//		return k.getProductWith(beta);
//	}
}
