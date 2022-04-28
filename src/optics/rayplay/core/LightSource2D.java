package optics.rayplay.core;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.PrintStream;
import java.util.ArrayList;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.graphicElements.RaysCharacteristicsPoint2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;
import optics.rayplay.graphicElements.RayBundleStartPoint2D;

/**
 * A source of light ray(s)
 * @author johannes
 */
public class LightSource2D extends GraphicElementCollection2D
{
	private static final String RAYS_START_POINT_NAME = "Start point of ray(s)";
	private static final String RAYS_CHARACTERISTICS_POINT_NAME = "Point controlling ray/ray bundle characteristics";
	// private static final String NUMBER_OF_RAYS_POINT_NAME = "Point controlling number of rays";

	// ray / ray bundle
	private Vector2D raysStartPoint;

	/**
	 * angle of ray (or, if rayBundle, the central ray in the bundle) with horizontal
	 */
	private double centralRayAngle;

	/**
	 * if false, any rays also start in the opposite direction
	 */
	private boolean forwardRaysOnly;

	private boolean rayBundle;

	private boolean rayBundleIsotropic;

	/**
	 * angle of cone formed by outermost rays in bundle
	 */
	private double rayBundleAngle;

	/**
	 * number of rays in a ray bundle
	 */
	private int rayBundleNoOfRays;
	
	private Colour colour;
	
	private boolean darkenExhaustedRays;

//	private Stroke pointStroke;
//	private Color pointColor;


	// internal variables

	private RayBundleStartPoint2D rayBundleStartPoint;
	private RaysCharacteristicsPoint2D raysCharacteristicsPoint;
	// private NumberOfRaysPoint2D numberOfRaysPoint;

	private ArrayList<Ray2D> rays;

	// constructor

	public LightSource2D(
			String name,
			Vector2D raysStartPoint,
			double centralRayAngle,
			boolean forwardRaysOnly,
			boolean rayBundle,
			boolean rayBundleIsotropic,
			double rayBundleAngle,
			int rayBundleNoOfRays,
			Colour colour
//			Stroke pointStroke,
//			Color pointColor
		)
	{
		super(name);

		this.raysStartPoint = raysStartPoint;
		this.centralRayAngle = centralRayAngle;
		this.forwardRaysOnly = forwardRaysOnly;
		this.rayBundle = rayBundle;
		this.rayBundleIsotropic = rayBundleIsotropic;
		this.rayBundleAngle = rayBundleAngle;
		this.rayBundleNoOfRays = rayBundleNoOfRays;
		this.colour = colour;
//		this.pointStroke = pointStroke;
//		this.pointColor = pointColor;
		this.darkenExhaustedRays = true;

		Stroke pointStroke = new BasicStroke(1);
		Color pointColor = Color.gray;
		rayBundleStartPoint = new RayBundleStartPoint2D(RAYS_START_POINT_NAME, raysStartPoint, 5, pointStroke, pointColor, true, this);
		raysCharacteristicsPoint = new RaysCharacteristicsPoint2D(RAYS_CHARACTERISTICS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
		// numberOfRaysPoint = new NumberOfRaysPoint2D(NUMBER_OF_RAYS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
		
		graphicElements.add(rayBundleStartPoint);
		graphicElements.add(raysCharacteristicsPoint);
		// graphicElements.add(numberOfRaysPoint);
	}



	// setters & getters

	public Vector2D getRayStartPoint() {
		return raysStartPoint;
	}


	public void setRayStartPointCoordinatesToThoseOf(Vector2D rayStartPoint) {
		this.raysStartPoint.setCoordinatesToThoseOf(rayStartPoint);
	}


	public Vector2D getRaysCharacteristicsPoint() {
		return raysCharacteristicsPoint.getPosition();
	}


	public void setRaysCharacteristicsPointCoordinatesToThoseOf(Vector2D rayPoint2) {
		raysCharacteristicsPoint.setCoordinatesToThoseOf(rayPoint2);
	}


	public double getRayAngle() {
		return centralRayAngle;
	}


	public void setRayAngle(double rayAngle) {
		this.centralRayAngle = rayAngle;
	}


	public boolean isForwardRaysOnly() {
		return forwardRaysOnly;
	}


	public void setForwardRaysOnly(boolean forwardRaysOnly) {
		this.forwardRaysOnly = forwardRaysOnly;
	}


	public boolean isRayBundle() {
		return rayBundle;
	}


	public void setRayBundle(boolean rayBundle) {
		this.rayBundle = rayBundle;
	}


	public boolean isRayBundleIsotropic() {
		return rayBundleIsotropic;
	}


	public void setRayBundleIsotropic(boolean rayBundleIsotropic) {
		this.rayBundleIsotropic = rayBundleIsotropic;
	}


	public double getRayBundleAngle() {
		return rayBundleAngle;
	}


	public void setRayBundleAngle(double rayBundleAngle) {
		this.rayBundleAngle = rayBundleAngle;
	}


	public int getRayBundleNoOfRays() {
		return rayBundleNoOfRays;
	}


	public void setRayBundleNoOfRays(int rayBundleNoOfRays) {
		this.rayBundleNoOfRays = rayBundleNoOfRays;
	}

//	public Stroke getPointStroke() {
//		return pointStroke;
//	}
//
//	public void setPointStroke(Stroke pointStroke) {
//		this.pointStroke = pointStroke;
//	}
//
//	public Color getPointColor() {
//		return pointColor;
//	}
//
//	public void setPointColor(Color pointColor) {
//		this.pointColor = pointColor;
//	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}

	public boolean isDarkenExhaustedRays() {
		return darkenExhaustedRays;
	}

	public void setDarkenExhaustedRays(boolean darkenExhaustedRays) {
		this.darkenExhaustedRays = darkenExhaustedRays;
	}

	public ArrayList<Ray2D> getRays() {
		return rays;
	}

	public void setRays(ArrayList<Ray2D> rays) {
		this.rays = rays;
	}


	//

	public Vector2D getCentralRayDirection() {
		return new Vector2D(Math.cos(centralRayAngle), Math.sin(centralRayAngle));
	}

	public void initialiseRays()
	{
		Vector2D dC = getCentralRayDirection();
		rays = new ArrayList<Ray2D>();
		
		if(rayBundle)
		{
			if(rayBundleIsotropic)
			{
				double alpha0 = Math.atan2(dC.y, dC.x);

				for(int r=0; r<rayBundleNoOfRays; r++)
				{
					// calculate the ray direction
					double alpha = alpha0 + r*(2.*Math.PI)/(rayBundleNoOfRays);

					Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

					// initialise the forward ray
					rays.add(new Ray2D(raysStartPoint, d));
				}
			}
			else
			{
				double alphaC = Math.atan2(dC.y, dC.x);
				double alpha0 = alphaC - 0.5*rayBundleAngle;

				for(int r=0; r<rayBundleNoOfRays; r++)
				{
					// calculate the ray direction
					double alpha;
					if(rayBundleNoOfRays == 1) alpha = alphaC;
					else alpha = alpha0 + r*rayBundleAngle/(rayBundleNoOfRays-1);

					Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

					// initialise the forward ray
					rays.add(new Ray2D(raysStartPoint, d));

					if(!forwardRaysOnly)
					{
						// ... and the backwards ray
						rays.add(new Ray2D(raysStartPoint, d.getProductWith(-1)));
					}
				}
			}
		}
		else
		{
			// initialise the forward ray
			rays.add(new Ray2D(raysStartPoint, dC));

			if(!forwardRaysOnly)
			{
				// ... and the backwards ray
				rays.add(new Ray2D(raysStartPoint, dC.getProductWith(-1)));
			}
		}
	}
	
	public void drawRays(RayPlay2DPanel rpp, Graphics2D g2)
	{
		// draw a ray
		g2.setStroke(new BasicStroke(1));

		// make everything that gets drawn transparent
		Composite c = g2.getComposite();
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(alcom);

        for(Ray2D ray:rays)
		{
			ArrayList<Vector2D> t = ray.getTrajectory();
			if(darkenExhaustedRays && (ray.getTraceLevel() == 0))
				g2.setColor(
						colour.getDarkerColor()
						// Color.BLACK
						);
			else g2.setColor(colour.getColor());
			for(int p=1; p<t.size(); p++)
				rpp.drawLine(t.get(p-1), t.get(p), g2);
		}
		
		// set transparency to whatever it was previously
		g2.setComposite(c);
	}
	
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		for(Ray2D ray:rays)
		{
			ArrayList<Vector2D> t = ray.getTrajectory();
			if(darkenExhaustedRays && (ray.getTraceLevel() == 0))
			{
				// assumed to be a closed-loop trajectory
				SVGWriter.writeSVGPolyLine(t, rpp, colour.getSVGNameOfDarkerColour(), 1, "");
			}
			else
			{
				// definitely not a closed-loop trajectory
				SVGWriter.writeSVGPolyLine(t, rpp, colour.getSVGName(), 1, "");
			}
		}

	}

	public void writeParameters(PrintStream printStream)
	{
		printStream.println("\nLight-ray source \""+name+"\"\n");

		printStream.println("  rayStartPoint = "+getRayStartPoint());
		printStream.println("  rayAngle = "+ MyMath.rad2deg(getRayAngle())+" degrees");
		printStream.println("  rayBundle = "+isRayBundle());
		printStream.println("  rayBundleIsotropic = "+isRayBundleIsotropic());
		printStream.println("  rayBundleAngle = "+MyMath.rad2deg(getRayBundleAngle())+" degrees");
		printStream.println("  rayBundleNoOfRays = "+getRayBundleNoOfRays());
		printStream.println("  forwardRaysOnly = "+isForwardRaysOnly());
	}

}