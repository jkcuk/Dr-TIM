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
 * When seen from the outside, the inner sphere and anything within it appears shifted in the x direction.
 * If an object inside the inner sphere appears shifted outside of the device, the device acts like an abyss cloak.
 * 
 * Calculations in J's lab book dated 21/8/19 (and later dates)
 * and in Mathematica notebook TOAbyssCloakMapping.nb
 * 
 * @author johannes
 */
public class SurfaceOfTOXShifter extends SurfaceOfMetricSpace
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
	 * for the moment, the apparent shift of the space inside the spherical shell is by deltaX in the x direction
	 * TODO extend to arbitrary shifts
	 */
	private double deltaX;
		
	
	// constructors etc.

	/**
	 * Create a surface property that, when applied to a surface comprising concentric inner and outer spheres,
	 * makes the space between the spheres (a spherical shell) behave like a TO device that translates the space
	 * inside the inner sphere when seen from the outside
	 * 
	 * Once created, the surface must be set
	 */
	public SurfaceOfTOXShifter(
			Vector3D centre,
			double radius,
			double innerRadius,
			double deltaX,
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
		setDeltaX(deltaX);
	}
	
	/**
	 * Clone the original transformation-optics-element surface
	 * 
	 * After cloning the surface, assign it to a suitable SceneObject (and set that SceneObject as the "surface" of the new SurfaceOfTOAbyssCloak)
	 * 
	 * @param original
	 */
	public SurfaceOfTOXShifter(SurfaceOfTOXShifter original)
	{
		this(
				original.getCentre(),
				original.getRadius(),
				original.getInnerRadius(),
				original.getDeltaX(),
				original.getDeltaTau(),
				original.getDeltaXMax(),
				original.getMaxSteps(),
				original.getIntegrationType(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing()
			);
	}


	@Override
	public SurfaceOfTOXShifter clone()
	{
		return new SurfaceOfTOXShifter(this);
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

	public double getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(double deltaX) {
		this.deltaX = deltaX;
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
		double[][] vals =
				{
						{
							((1 + (6*deltaX*(r - radius)*(r - innerRadius)*x.x)/(r*Math.pow(radius - innerRadius,3)))*(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.y,2) + 
							         36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2)))/Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2),
							(-6*deltaX*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3)),
							(-6*deltaX*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3))
//							(r*Math.pow(radius - innerRadius,3))/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x),
//							(-6*deltaX*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x),
//							(-6*deltaX*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)
						},
						{
							(-6*deltaX*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3)),
							1 + (6*deltaX*(r - radius)*(r - innerRadius)*x.x)/(r*Math.pow(radius - innerRadius,3)),
							0
//							(-6*deltaX*(r - radius)*(r - innerRadius)*x.y)/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x),
//							((r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*(1 + (36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.y,2))/Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)))/(r*Math.pow(radius - innerRadius,3)),
//					     	(36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*x.y*x.z)/(r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x))
						},
						{
							(-6*deltaX*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3)),
							0,
							1 + (6*deltaX*(r - radius)*(r - innerRadius)*x.x)/(r*Math.pow(radius - innerRadius,3))
//							(-6*deltaX*(r - radius)*(r - innerRadius)*x.z)/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x),
//							(36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*x.y*x.z)/(r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
//							((r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*(1 + (36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2))/Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)))/(r*Math.pow(radius - innerRadius,3))
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
				(2*(-6*deltaX*k.y*(r - radius)*(r - innerRadius)*x.y + (k.x*Math.pow(r,2)*Math.pow(radius - innerRadius,6) - 6*deltaX*k.z*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*x.z + 
				           36*Math.pow(deltaX,2)*k.x*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*(Math.pow(x.y,2) + Math.pow(x.z,2)))/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)))/(r*Math.pow(radius - innerRadius,3)),
				   (2*(k.y*r*Math.pow(radius - innerRadius,3) + 6*deltaX*k.y*(r - radius)*(r - innerRadius)*x.x - 6*deltaX*k.x*(r - radius)*(r - innerRadius)*x.y))/(r*Math.pow(radius - innerRadius,3)),
				   2*(k.z + (6*deltaX*(r - radius)*(r - innerRadius)*(k.z*x.x - k.x*x.z))/(r*Math.pow(radius - innerRadius,3)))
//				(2*(k.x*r*Math.pow(radius - innerRadius,3) - 6*deltaX*(r - radius)*(r - innerRadius)*(k.y*x.y + k.z*x.z)))/(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x),
//				(2*(k.y*(36*Math.pow(deltaX,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.y,2)) + 
//				           12*deltaX*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				           12*deltaX*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*radius*(radius + innerRadius)*Math.pow(x.y,2)) + 
//				           Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaX*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaX*x.x) + 
//				              Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaX*x.x,2) + 36*Math.pow(deltaX,2)*Math.pow(x.y,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))) + 
//				              3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.y,2))))) + 6*deltaX*(r - radius)*(r - innerRadius)*x.y*(-(k.x*r*Math.pow(radius - innerRadius,3)) + 6*deltaX*k.z*(r - radius)*(r - innerRadius)*x.z)))/
//				    (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
//				    (2*
//				      (6*deltaX*(r - radius)*(r - innerRadius)*(-(k.x*r*Math.pow(radius - innerRadius,3)) + 6*deltaX*k.y*(r - radius)*(r - innerRadius)*x.y)*x.z + 
//				        k.z*(36*Math.pow(deltaX,2)*Math.pow(r,4)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)) + 
//				           12*deltaX*Math.pow(r,3)*(x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				           12*deltaX*r*innerRadius*(radius*x.x*(Math.pow(radius - innerRadius,3) - 6*deltaX*(radius + innerRadius)*x.x) - 6*deltaX*radius*(radius + innerRadius)*Math.pow(x.z,2)) + 
//				           Math.pow(r,2)*(Math.pow(radius,6) - 6*Math.pow(radius,5)*innerRadius + 3*Math.pow(radius,4)*(5*Math.pow(innerRadius,2) - 4*deltaX*x.x) + 4*Math.pow(radius,3)*innerRadius*(-5*Math.pow(innerRadius,2) + 6*deltaX*x.x) + 
//				              Math.pow(innerRadius,2)*(Math.pow(Math.pow(innerRadius,2) + 6*deltaX*x.x,2) + 36*Math.pow(deltaX,2)*Math.pow(x.z,2)) - 6*radius*innerRadius*(Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*x.x - 24*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.z,2))) + 
//				              3*Math.pow(radius,2)*(5*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*(Math.pow(x.x,2) + Math.pow(x.z,2)))))))/(r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x))
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
				(-6*deltaX*Math.pow(radius,6)*(-r + radius))/Math.pow(radius - innerRadius,9) + (6*deltaX*Math.pow(radius,6)*Math.pow(x.x,2))/(r*Math.pow(radius - innerRadius,9)) - 
				   Math.pow(k.y,2)*((6*deltaX*(r - radius)*(r - innerRadius))/(r*Math.pow(radius - innerRadius,3)) + (6*deltaX*(r - radius)*Math.pow(x.x,2))/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + 
				      (6*deltaX*(r - innerRadius)*Math.pow(x.x,2))/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - (6*deltaX*(r - radius)*(r - innerRadius)*Math.pow(x.x,2))/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3))) + 
				   (12*deltaX*k.x*k.y*(r - radius)*x.x*x.y)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + (12*deltaX*k.x*k.y*(r - innerRadius)*x.x*x.y)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - 
				   (12*deltaX*k.x*k.y*(r - radius)*(r - innerRadius)*x.x*x.y)/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)) + 
				   (x.x*(Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
				   ((6*deltaX*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaX*(r - radius)*Math.pow(x.x,2))/r + (6*deltaX*(r - innerRadius)*Math.pow(x.x,2))/r)*
				      (Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (r*Math.pow(radius - innerRadius,9)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
				   (2*Math.pow(k.x,2)*Math.pow(radius - innerRadius,12)*x.x + (6*deltaX*x.x*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z))/r + 6*deltaX*(r - radius)*(6*deltaX*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaX*(r - radius)*Math.pow(x.x,2))/r + 
				         (6*deltaX*(r - innerRadius)*Math.pow(x.x,2))/r)*(Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + 
				         innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 
				      6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*(Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6) + 
				         innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2))) + (Math.pow(k.z,2)*Math.pow(radius - innerRadius,6)*Math.pow(x.x,2))/r + 
				         ((2*radius - innerRadius)*innerRadius*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2))*Math.pow(x.x,2))/r - (2*k.x*k.z*Math.pow(radius - innerRadius,6)*x.x*x.z)/r) + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*(r - radius)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*x.x*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r)/(r*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
//				(x.x*(36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
//				   ((6*deltaX*(r - radius)*(r - innerRadius) + (Math.pow(radius - innerRadius,3)*x.x)/r + (6*deltaX*(r - radius)*Math.pow(x.x,2))/r + (6*deltaX*(r - innerRadius)*Math.pow(x.x,2))/r)*
//				      (36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
//				   (72*Math.pow(deltaX,2)*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(r,4)*x.x + 72*Math.pow(deltaX,2)*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,2)*Math.pow(innerRadius,2)*x.x - 
//				      12*deltaX*Math.pow(r,3)*((1 - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(radius,3) + 3*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,2)*innerRadius + (-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,3) + 
//				         12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*innerRadius*x.x + radius*(3*(1 - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(innerRadius,2) + 12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x)) - 
//				      12*deltaX*r*radius*innerRadius*((1 - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(radius,3) + 3*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,2)*innerRadius + (-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,3) + 
//				         12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*innerRadius*x.x + radius*(3*(1 - Math.pow(k.y,2) - Math.pow(k.z,2))*Math.pow(innerRadius,2) + 12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x)) + 
//				      Math.pow(r,2)*(-12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,4) + 24*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,3)*innerRadius + 12*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 
//				         72*Math.pow(deltaX,2)*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,2)*x.x + 72*Math.pow(deltaX,2)*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2)*x.x - 
//				         6*radius*innerRadius*(4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 48*Math.pow(deltaX,2)*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x)) + 
//				      144*Math.pow(deltaX,2)*Math.pow(r,2)*x.x*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				      36*deltaX*r*x.x*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				         radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				      (12*deltaX*radius*innerRadius*x.x*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/r + 
//				      2*x.x*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				         (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				         4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				         36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				         6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				            24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				         3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/
//				    (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
				// dky / dtau
				   (12*deltaX*k.x*k.y*(r - radius)*(r - innerRadius))/(r*Math.pow(radius - innerRadius,3)) + (6*deltaX*Math.pow(radius,6)*x.x*x.y)/(r*Math.pow(radius - innerRadius,9)) + (12*deltaX*k.x*k.y*(r - radius)*Math.pow(x.y,2))/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + 
				   (12*deltaX*k.x*k.y*(r - innerRadius)*Math.pow(x.y,2))/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - (12*deltaX*k.x*k.y*(r - radius)*(r - innerRadius)*Math.pow(x.y,2))/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)) - 
				   Math.pow(k.y,2)*((6*deltaX*(r - radius)*x.x*x.y)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + (6*deltaX*(r - innerRadius)*x.x*x.y)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - 
				      (6*deltaX*(r - radius)*(r - innerRadius)*x.x*x.y)/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3))) + 
				   (x.y*(Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
				   (((Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaX*(r - radius)*x.x*x.y)/r + (6*deltaX*(r - innerRadius)*x.x*x.y)/r)*
				      (Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (r*Math.pow(radius - innerRadius,9)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
				   (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*x.y + 2*Math.pow(k.x,2)*Math.pow(radius - innerRadius,12)*x.y + 
				      (6*deltaX*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*x.y*(Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + 
				           innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z))/r + 
				      6*deltaX*(r - radius)*((Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaX*(r - radius)*x.x*x.y)/r + (6*deltaX*(r - innerRadius)*x.x*x.y)/r)*
				       (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				         2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				       ((Math.pow(k.z,2)*Math.pow(radius - innerRadius,6)*x.x*x.y)/r + ((2*radius - innerRadius)*innerRadius*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2))*x.x*x.y)/r - (2*k.x*k.z*Math.pow(radius - innerRadius,6)*x.y*x.z)/r) + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.y*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*(r - radius)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*x.y*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r)/(r*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
//				(x.y*(36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
//				   (((Math.pow(radius - innerRadius,3)*x.y)/r + (6*deltaX*(r - radius)*x.x*x.y)/r + (6*deltaX*(r - innerRadius)*x.x*x.y)/r)*
//				      (36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
//				   (72*Math.pow(deltaX,2)*k.y*Math.pow(r,4)*(k.y*x.y + k.z*x.z) + 72*Math.pow(deltaX,2)*k.y*Math.pow(radius,2)*Math.pow(innerRadius,2)*(k.y*x.y + k.z*x.z) + 
//				      144*Math.pow(deltaX,2)*Math.pow(r,2)*x.y*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				      12*deltaX*Math.pow(r,3)*(k.x*k.y*Math.pow(radius,3) - 3*k.x*k.y*Math.pow(radius,2)*innerRadius - k.x*k.y*Math.pow(innerRadius,3) + 12*deltaX*k.y*innerRadius*(k.y*x.y + k.z*x.z) + radius*(3*k.x*k.y*Math.pow(innerRadius,2) + 12*deltaX*k.y*(k.y*x.y + k.z*x.z))) - 
//				      12*deltaX*r*radius*innerRadius*(k.x*k.y*Math.pow(radius,3) - 3*k.x*k.y*Math.pow(radius,2)*innerRadius - k.x*k.y*Math.pow(innerRadius,3) + 12*deltaX*k.y*innerRadius*(k.y*x.y + k.z*x.z) + radius*(3*k.x*k.y*Math.pow(innerRadius,2) + 12*deltaX*k.y*(k.y*x.y + k.z*x.z))) + 
//				      Math.pow(r,2)*(12*deltaX*k.x*k.y*Math.pow(radius,4) - 24*deltaX*k.x*k.y*Math.pow(radius,3)*innerRadius - 12*deltaX*k.x*k.y*Math.pow(innerRadius,4) + 72*Math.pow(deltaX,2)*k.y*Math.pow(radius,2)*(k.y*x.y + k.z*x.z) + 
//				         72*Math.pow(deltaX,2)*k.y*Math.pow(innerRadius,2)*(k.y*x.y + k.z*x.z) - 6*radius*innerRadius*(-4*deltaX*k.x*k.y*Math.pow(innerRadius,2) - 48*Math.pow(deltaX,2)*k.y*(k.y*x.y + k.z*x.z))) - 
//				      36*deltaX*r*x.y*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				         radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				      (12*deltaX*radius*innerRadius*x.y*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/r + 
//				      2*x.y*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				         (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				         4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				         36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				         6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				            24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				         3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/
//				    (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
				// dkz / dtau
				   (6*deltaX*Math.pow(radius,6)*x.x*x.z)/(r*Math.pow(radius - innerRadius,9)) + (12*deltaX*k.x*k.y*(r - radius)*x.y*x.z)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + (12*deltaX*k.x*k.y*(r - innerRadius)*x.y*x.z)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - 
				   (12*deltaX*k.x*k.y*(r - radius)*(r - innerRadius)*x.y*x.z)/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)) - 
				   Math.pow(k.y,2)*((6*deltaX*(r - radius)*x.x*x.z)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) + (6*deltaX*(r - innerRadius)*x.x*x.z)/(Math.pow(r,2)*Math.pow(radius - innerRadius,3)) - 
				      (6*deltaX*(r - radius)*(r - innerRadius)*x.x*x.z)/(Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3))) + 
				   (x.z*(Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
				   (((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaX*(r - radius)*x.x*x.z)/r + (6*deltaX*(r - innerRadius)*x.x*x.z)/r)*
				      (Math.pow(k.x,2)*Math.pow(r,2)*Math.pow(radius - innerRadius,12) + 6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*
				         (Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				           2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z) + 36*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*(Math.pow(x.y,2) + Math.pow(x.z,2))))/
				    (r*Math.pow(radius - innerRadius,9)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
				   (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*x.z + 2*Math.pow(k.x,2)*Math.pow(radius - innerRadius,12)*x.z + 
				      (6*deltaX*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*x.z*(Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + 
				           innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z))/r + 
				      6*deltaX*(r - radius)*(Math.pow(k.z,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.x + innerRadius*(Math.pow(radius - innerRadius,6) + r*(2*radius - innerRadius)*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2)))*x.x - 
				         2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z)*((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaX*(r - radius)*x.x*x.z)/r + (6*deltaX*(r - innerRadius)*x.x*x.z)/r) + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*Math.pow(r - radius,2)*(r - innerRadius)*Math.pow(radius - innerRadius,6)*x.z*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r + 
				      (72*Math.pow(deltaX,2)*Math.pow(k.x,2)*(r - radius)*Math.pow(r - innerRadius,2)*Math.pow(radius - innerRadius,6)*x.z*(Math.pow(x.y,2) + Math.pow(x.z,2)))/r + 
				      6*deltaX*(r - radius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*(-2*k.x*k.z*(r - innerRadius)*Math.pow(radius - innerRadius,6) + (Math.pow(k.z,2)*Math.pow(radius - innerRadius,6)*x.x*x.z)/r + 
				         ((2*radius - innerRadius)*innerRadius*(3*Math.pow(radius,2) - 3*radius*innerRadius + Math.pow(innerRadius,2))*(Math.pow(radius,2) - radius*innerRadius + Math.pow(innerRadius,2))*x.x*x.z)/r - (2*k.x*k.z*Math.pow(radius - innerRadius,6)*Math.pow(x.z,2))/r))/
				    (r*Math.pow(radius - innerRadius,9)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x))
//				(x.z*(36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (Math.pow(Math.pow(r,2),1.5)*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)) + 
//				   (((Math.pow(radius - innerRadius,3)*x.z)/r + (6*deltaX*(r - radius)*x.x*x.z)/r + (6*deltaX*(r - innerRadius)*x.x*x.z)/r)*
//				      (36*Math.pow(deltaX,2)*Math.pow(r,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				        36*Math.pow(deltaX,2)*Math.pow(radius,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				        12*deltaX*Math.pow(r,3)*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				        12*deltaX*r*radius*innerRadius*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) + 
//				        Math.pow(r,2)*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				           (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				           4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				           36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				           6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				              24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				           3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))))))/
//				    (r*Math.pow(radius - innerRadius,3)*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2)) - 
//				   (72*Math.pow(deltaX,2)*k.z*Math.pow(r,4)*(k.y*x.y + k.z*x.z) + 72*Math.pow(deltaX,2)*k.z*Math.pow(radius,2)*Math.pow(innerRadius,2)*(k.y*x.y + k.z*x.z) + 
//				      144*Math.pow(deltaX,2)*Math.pow(r,2)*x.z*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				      12*deltaX*Math.pow(r,3)*(k.x*k.z*Math.pow(radius,3) - 3*k.x*k.z*Math.pow(radius,2)*innerRadius - k.x*k.z*Math.pow(innerRadius,3) + 12*deltaX*k.z*innerRadius*(k.y*x.y + k.z*x.z) + radius*(3*k.x*k.z*Math.pow(innerRadius,2) + 12*deltaX*k.z*(k.y*x.y + k.z*x.z))) - 
//				      12*deltaX*r*radius*innerRadius*(k.x*k.z*Math.pow(radius,3) - 3*k.x*k.z*Math.pow(radius,2)*innerRadius - k.x*k.z*Math.pow(innerRadius,3) + 12*deltaX*k.z*innerRadius*(k.y*x.y + k.z*x.z) + radius*(3*k.x*k.z*Math.pow(innerRadius,2) + 12*deltaX*k.z*(k.y*x.y + k.z*x.z))) + 
//				      Math.pow(r,2)*(12*deltaX*k.x*k.z*Math.pow(radius,4) - 24*deltaX*k.x*k.z*Math.pow(radius,3)*innerRadius - 12*deltaX*k.x*k.z*Math.pow(innerRadius,4) + 72*Math.pow(deltaX,2)*k.z*Math.pow(radius,2)*(k.y*x.y + k.z*x.z) + 
//				         72*Math.pow(deltaX,2)*k.z*Math.pow(innerRadius,2)*(k.y*x.y + k.z*x.z) - 6*radius*innerRadius*(-4*deltaX*k.x*k.z*Math.pow(innerRadius,2) - 48*Math.pow(deltaX,2)*k.z*(k.y*x.y + k.z*x.z))) - 
//				      36*deltaX*r*x.z*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				         radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))) - 
//				      (12*deltaX*radius*innerRadius*x.z*(3*Math.pow(radius,2)*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + Math.pow(innerRadius,3)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				           Math.pow(radius,3)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*innerRadius*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) + 
//				           radius*(3*Math.pow(innerRadius,2)*(-((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x) + k.x*(k.y*x.y + k.z*x.z)) + 6*deltaX*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/r + 
//				      2*x.z*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,6) - 6*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(radius,5)*innerRadius + 
//				         (-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,6) + 12*deltaX*Math.pow(innerRadius,4)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) + 
//				         3*Math.pow(radius,4)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 4*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 4*deltaX*k.x*(k.y*x.y + k.z*x.z)) - 
//				         4*Math.pow(radius,3)*innerRadius*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,2) - 6*deltaX*(-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x + 6*deltaX*k.x*(k.y*x.y + k.z*x.z)) + 
//				         36*Math.pow(deltaX,2)*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)) - 
//				         6*radius*innerRadius*((-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 4*deltaX*Math.pow(innerRadius,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*x.x - k.x*(k.y*x.y + k.z*x.z)) - 
//				            24*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2))) + 
//				         3*Math.pow(radius,2)*(5*(-1 + Math.pow(k.x,2) + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(innerRadius,4) + 12*Math.pow(deltaX,2)*((-1 + Math.pow(k.y,2) + Math.pow(k.z,2))*Math.pow(x.x,2) + Math.pow(k.y*x.y + k.z*x.z,2)))))/
//				    (r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x))
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
		double alpha = Math.sqrt((4*Math.pow(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x,2))/
				   (Math.pow(d.x*r*Math.pow(radius - innerRadius,3) + 6*deltaX*d.x*(r - radius)*(r - innerRadius)*x.x,2) + Math.pow(d.y,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.y,2)) + 
						     72*Math.pow(deltaX,2)*d.y*d.z*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*x.y*x.z + 12*deltaX*d.x*(r - radius)*(r - innerRadius)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)*(d.y*x.y + d.z*x.z) + 
						     Math.pow(d.z,2)*(Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 36*Math.pow(deltaX,2)*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2))));

		return new Vector3D(
				(alpha*(d.x*r*Math.pow(radius - innerRadius,3) + 6*deltaX*d.x*(r - radius)*(r - innerRadius)*x.x + 6*deltaX*(r - radius)*(r - innerRadius)*(d.y*x.y + d.z*x.z)))/(2.*r*Math.pow(radius - innerRadius,3)),
				(alpha*(d.y*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 36*Math.pow(deltaX,2)*d.y*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.y,2) + 
				          6*deltaX*(r - radius)*(r - innerRadius)*x.y*(d.x*r*Math.pow(radius - innerRadius,3) + 6*deltaX*d.x*(r - radius)*(r - innerRadius)*x.x + 6*deltaX*d.z*(r - radius)*(r - innerRadius)*x.z)))/
				      (2.*r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x)),
						(alpha*(d.z*Math.pow(r,2)*Math.pow(radius - innerRadius,6) + 6*deltaX*(r - radius)*(r - innerRadius)*(d.x*r*Math.pow(radius - innerRadius,3) + 6*deltaX*d.x*(r - radius)*(r - innerRadius)*x.x + 6*deltaX*d.y*(r - radius)*(r - innerRadius)*x.y)*x.z + 
						          36*Math.pow(deltaX,2)*d.z*Math.pow(r - radius,2)*Math.pow(r - innerRadius,2)*Math.pow(x.z,2)))/(2.*r*Math.pow(radius - innerRadius,3)*(r*Math.pow(radius - innerRadius,3) + 6*deltaX*(r - radius)*(r - innerRadius)*x.x))
//				(alpha*(36*Math.pow(deltaX,2)*d.x*r*Math.pow(rr0,2)*Math.pow(rr1,2) + d.x*r*Math.pow(r0r1,6) + 6*deltaX*(rr0)*(rr1)*Math.pow(r0r1,3)*(2*d.x*x.x + d.y*x.y + d.z*x.z)))/
//					      (2.*Math.pow(r0r1,3)*(r*Math.pow(r0r1,3) + 6*deltaX*(rr0)*(rr1)*x.x)),
//				(alpha*d.y*r*Math.pow(r0r1,3) + 6*alpha*deltaX*d.x*(rr0)*(rr1)*x.y)/(2*r*Math.pow(r0r1,3) + 12*deltaX*(rr0)*(rr1)*x.x),
//				(alpha*d.z*r*Math.pow(r0r1,3) + 6*alpha*deltaX*d.x*(rr0)*(rr1)*x.z)/(2*r*Math.pow(r0r1,3) + 12*deltaX*(rr0)*(rr1)*x.x)
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
