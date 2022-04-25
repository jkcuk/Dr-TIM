package optics.rayplay.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;

import math.Vector2D;
import optics.rayplay.graphicElements.RaysCharacteristicsPoint2D;
import optics.rayplay.graphicElements.RayBundleStartPoint2D;

/**
 * A point source of a single light ray or a ray bundle
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
			int rayBundleNoOfRays
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
//		this.pointStroke = pointStroke;
//		this.pointColor = pointColor;

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


//	public void drawRays(RayPlay2DPanel rpp, Graphics2D g2)
//	{
//		// draw a ray
//		g2.setStroke(new BasicStroke(1));
//
//		// make everything that gets drawn transparent
//		Composite c = g2.getComposite();
//		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
//        g2.setComposite(alcom);
//
//		// drawLine(rayStartPoint, Vector2D.sum(rayStartPoint, rayDirection), g2);
//		// g2.drawRect(200, 200, 200, 200);
//		// g2.fillOval(x2i(point[RAY_START_POINT].x)-5, y2j(point[RAY_START_POINT].y)-5, 11, 11);
//		// g2.drawOval(currentX-2, currentY-2, 5, 5);
//
//		for(Ray2D ray:rays)
//		{
//			ArrayList<Vector2D> t = ray.getTrajectory();
//			if(t.size() > 20) g2.setColor(Color.BLACK);
//			else g2.setColor(Color.RED);
//			for(int p=1; p<t.size(); p++)
//				rpp.drawLine(t.get(p-1), t.get(p), g2);
//		}
//		
//		// set transparency to whatever it was previously
//		g2.setComposite(c);
//	}
	
}