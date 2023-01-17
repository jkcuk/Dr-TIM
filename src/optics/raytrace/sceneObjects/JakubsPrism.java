package optics.raytrace.sceneObjects;


import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;

/**
 * A prism to be used in jakub's cloak
 * @author Maik based on Jakub's design
 */
public class JakubsPrism extends SceneObjectPrimitiveIntersection
{
	private static final long serialVersionUID = -2686597787702233306L;

	/**
	 * The centre
	 */
	private Vector3D centre;

	/**
	 * Normalised vector in the direction of the optical axis.
	 */
	private Vector3D normalisedOpticalAxisDirection;

	/**
	 * w, the interior angle of the prisms
	 */
	private double w;

	/**
	 * h, the cross sectional height of the prisms
	 */
	private double h;	
	
	/**
	 * the refractive index of the material
	 */
	private double n;
	
	/**
	 * should the inverse be created?
	 */
	
	private boolean invertPrism;
	
	/**
	 * Transmission coefficient of each prism
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * True if the system should  throw shadows
	 */
	private boolean shadowThrowing;


	/**
	 * Create a rotationally symmetric prism
	 * @param description
	 * @param centre
	 * @param normalisedOpticalAxisDirection
	 * @param w interior angle
	 * @param h
	 * @param n the refrative index
	 * @param surfaceTransmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public JakubsPrism(
			String description,
			Vector3D centre,
			Vector3D normalisedOpticalAxisDirection,
			double w,
			double h,
			double n,
			boolean invertPrism,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio)
	{
		super(description, parent, studio);
		this.centre = centre;
		this.normalisedOpticalAxisDirection = normalisedOpticalAxisDirection.getNormalised();
		this.w = w;
		this.h = h;
		this.n = n;
		this.invertPrism = invertPrism;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
		this.shadowThrowing = shadowThrowing;

		addElements();
	}

	public JakubsPrism(JakubsPrism original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getNormalisedOpticalAxisDirection(),
				original.getW(),
				original.getH(),
				original.getN(),
				original.isInvertPrism(),
				original.getSurfaceTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
				);
	}

	@Override
	public JakubsPrism clone()
	{
		return new JakubsPrism(this);
	}




	/**
	 * setters and getters
	 */

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getNormalisedOpticalAxisDirection() {
		return normalisedOpticalAxisDirection;
	}

	/**
	 * set optical-axis direction, ensuring it is normalised
	 * @param opticalAxisDirection
	 */
	public void setNormalisedOpticalAxisDirection(Vector3D opticalAxisDirection) {
		this.normalisedOpticalAxisDirection = opticalAxisDirection.getNormalised();
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public double getN() {
		return n;
	}

	public void setN(double n) {
		this.n = n;
	}

	public boolean isInvertPrism() {
		return invertPrism;
	}

	public void setInvertPrism(boolean invertPrism) {
		this.invertPrism = invertPrism;
	}

	public double getSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}

	public void setSurfaceTransmissionCoefficient(double surfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
	}

	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}


	// interesting method
	
	private void addElements()
	{
		RefractiveSimple surfaceN = new RefractiveSimple(n, surfaceTransmissionCoefficient, shadowThrowing);
		RefractiveSimple surface1OverN = new RefractiveSimple(1./n, surfaceTransmissionCoefficient, shadowThrowing);
		
		double a = Math.asin(n* Math.sin(w/2));
		double b = w - a;

		
		
		if(invertPrism) {
			double frontConeHeight = (2)*h*Math.tan(b);
			double backConeHeight = (2)*h*Math.tan(a);

			
			// prism 1
		 ConeTop frontHalf = new ConeTop(
					"frontHalf",	// description
					centre.getSumWith(normalisedOpticalAxisDirection.getProductWith(-frontConeHeight)),	// Apex	
					normalisedOpticalAxisDirection.getProductWith(1),	// Axis TODO, may need to be inverted
					(Math.PI-2*b)/2, // theta
					frontConeHeight+backConeHeight,
					surfaceN,
					//SurfaceColour.YELLOW_SHINY,
					this,
					getStudio()
				);

			// surface 2
		 ConeTop backHalf = new ConeTop(
					"backHalf",	// description
					centre.getSumWith(normalisedOpticalAxisDirection.getProductWith(backConeHeight)),	// Apex	
					normalisedOpticalAxisDirection.getProductWith(-1),	// Axis TODO, may need to be inverted
					(Math.PI-2*a)/2, // theta
					backConeHeight+frontConeHeight,
					surfaceN,
					//SurfaceColour.GREEN_SHINY,
					this,
					getStudio()
				);
		 
		 CylinderMantle centralHole = new CylinderMantle(
					"Negative interrior",	// description
					Vector3D.sum(normalisedOpticalAxisDirection.getProductWith(frontConeHeight+backConeHeight), centre),	// front
					Vector3D.sum(normalisedOpticalAxisDirection.getProductWith(-frontConeHeight-backConeHeight), centre),	// back
					h,	// radius
					surface1OverN,
					//SurfaceColour.GREEN_SHINY,
					this,
					getStudio()
				);

			

			addPositiveSceneObjectPrimitive(frontHalf);
			addPositiveSceneObjectPrimitive(backHalf);
			addNegativeSceneObjectPrimitive(centralHole);
		}else {
			
			double backConeHeight = h*Math.tan(b);
			double frontConeHeight = h*Math.tan(a);

			
			 ConeTop frontHalf = new ConeTop(
						"Negative frontHalf",	// description
						centre,	// Apex	
						normalisedOpticalAxisDirection.getProductWith(-1),	// Axis TODO, may need to be inverted
						(Math.PI - 2*a)/2, // theta
						frontConeHeight,
						surface1OverN,
						//SurfaceColour.YELLOW_SHINY,
						this,
						getStudio()
					);

				// surface 2
			 ConeTop backHalf = new ConeTop(
						"Negative backHalf",	// description
						centre,	// Apex	
						normalisedOpticalAxisDirection.getProductWith(1),	// Axis TODO, may need to be inverted
						(Math.PI - 2*b)/2, // theta
						backConeHeight,
						surface1OverN,
						//SurfaceColour.GREEN_SHINY,
						this,
						getStudio()
					);
			 
			 CylinderMantle prismCylinder = new CylinderMantle(
						"Positive interrior",	// description
						Vector3D.sum(normalisedOpticalAxisDirection.getProductWith(-frontConeHeight), centre),	// front
						Vector3D.sum(normalisedOpticalAxisDirection.getProductWith(backConeHeight), centre),	// back
						h,	// radius
						surfaceN,
						this,
						getStudio()
					);
	
				addPositiveSceneObjectPrimitive(prismCylinder);
				addNegativeSceneObjectPrimitive(frontHalf);
				addNegativeSceneObjectPrimitive(backHalf);
		}

	}
	

	@Override
	public String getType()
	{
		return "Prismatic Cloak";
	}
}
