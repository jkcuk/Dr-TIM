package optics.raytrace.sceneObjects;


import math.Vector3D;


import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.surfaces.RefractiveSimple;

/**
 * A prism to be used in jakub's cloak. This is symmetric about the y axis. i.e it creates a ring of a prism.
 * @author Maik based on Jakub's design
 */
public class JakubsPrism extends SceneObjectPrimitiveIntersection
{
	private static final long serialVersionUID = -7254719194789423594L;

	/**
	 * The centre
	 */
	private Vector3D centre;

	/**
	 * The symmetry axis, can be thought of as the "up" direction for jakubs cloak.
	 */
	private Vector3D symmetryAxis;

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
	 * d, the diameter of the prism ring
	 */
	private double d;

	/**
	 * True if alpha and beta should be inverted.
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
 * 
 * @param description
 * @param centre
 * @param symmetryAxis
 * @param w
 * @param h
 * @param n
 * @param d
 * @param invertPrism
 * @param surfaceTransmissionCoefficient
 * @param shadowThrowing
 * @param parent
 * @param studio
 */
	public JakubsPrism(
			String description,
			Vector3D centre,
			Vector3D symmetryAxis,
			double w,
			double h,
			double n,
			double d,
			boolean invertPrism,
			double surfaceTransmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio)
	{
		super(description, parent, studio);
		this.centre = centre;
		this.symmetryAxis = symmetryAxis.getNormalised();
		this.w = w;
		this.h = h;
		this.n = n;
		this.d = d;
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
				original.getSymmetryAxis(),
				original.getW(),
				original.getH(),
				original.getN(),
				original.getD(),
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

	public Vector3D getSymmetryAxis() {
		return symmetryAxis;
	}

	/**
	 * set optical-axis direction, ensuring it is normalised
	 * @param opticalAxisDirection
	 */
	public void setSymmetryAxis(Vector3D opticalAxisDirection) {
		this.symmetryAxis = opticalAxisDirection.getNormalised();
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

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
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

		double alpha, beta;
		Vector3D axis;

		//set the type of prism to be created.
		if(invertPrism) {
			axis = symmetryAxis.getProductWith(-1);
			beta = Math.asin(n* Math.sin(w/2));
			alpha = w - beta;
		}else {
			axis = symmetryAxis;
			alpha = Math.asin(n* Math.sin(w/2));
			beta = w - alpha;
		}

		Vector3D positiveApex= Vector3D.sum(centre, axis.getProductWith(0.5*h+0.5*d*Math.tan(Math.PI/2 - alpha)));
		Vector3D negativeApex= Vector3D.sum(centre, axis.getProductWith(0.5*h-0.5*d*Math.tan(Math.PI/2 - beta))); //TODO this is wrongggg
		double heightPositive = 0.5*d*Math.tan(Math.PI/2 - alpha)+h;
		double heightNegative = 0.5*d*Math.tan(Math.PI/2 - beta)+0.5*d*Math.tan(Math.PI/2 - alpha);

		ConeTop positiveCone = new ConeTop(
				"positive top",	// description
				positiveApex,	// Apex	
				axis.getProductWith(-1),	// Axis
				alpha, // theta
				heightPositive,
				surfaceN,
				//SurfaceColour.YELLOW_SHINY,
				this,
				getStudio()
				);

		// surface 2
		ConeTop negativeCone = new ConeTop(
				"Negative bottom",	// description
				negativeApex,	// Apex	
				axis.getProductWith(1),	// Axis
				beta, // theta
				heightNegative,
				surface1OverN,
				//SurfaceColour.GREEN_SHINY,
				this,
				getStudio()
				);
		System.out.println(negativeApex+", height "+heightNegative);

		//bottom plane to close off the prism

		Plane positivePlane = new Plane(
				"prism bottom",
				Vector3D.sum(centre, axis.getProductWith(-0.5*h)), //point on plane
				axis.getProductWith(-1), // plane normal
				surfaceN,
				this,
				getStudio()
				);

		addPositiveSceneObjectPrimitive(positiveCone);
		addNegativeSceneObjectPrimitive(negativeCone);
		addPositiveSceneObjectPrimitive(positivePlane);
	}


	@Override
	public String getType()
	{
		return "Prismatic Cloak";
	}
}
