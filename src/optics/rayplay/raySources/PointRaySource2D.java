package optics.rayplay.raySources;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.graphicElements.PointRaySourcePointGE2D;
import optics.rayplay.graphicElements.PointRaySourcePointGE2D.PointRaySource2DPointType;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.Ray2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;

/**
 * A source of light ray(s)
 * @author johannes
 */
public class PointRaySource2D implements InteractiveOpticalComponent2D
{
	private String name;
	
	// ray / ray bundle
	private Vector2D raysStartPoint;

	/**
	 * ignore if null, otherwise constrain rays start point to the line
	 */
	private Line2D lineConstrainingStartPoint = null;


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
	
	private int maxTraceLevel;
	
//	private Stroke pointStroke;
//	private Color pointColor;


	// internal variables

	// private PointRaySourcePointGE2D rayBundleStartPoint, raysCharacteristicsPoint;
//	private RayBundleStartPoint2D rayBundleStartPoint;
//	private RaysCharacteristicsPoint2D raysCharacteristicsPoint;
	// private NumberOfRaysPoint2D numberOfRaysPoint;

	private ArrayList<Ray2D> rays;
	
	private ArrayList<GraphicElement2D> graphicElements;
	
	private HashMap<PointRaySource2DPointType, PointRaySourcePointGE2D> points;


	// constructor

	public PointRaySource2D(
			String name,
			Vector2D raysStartPoint,
			double centralRayAngle,
			boolean forwardRaysOnly,
			boolean rayBundle,
			boolean rayBundleIsotropic,
			double rayBundleAngle,
			int rayBundleNoOfRays,
			Colour colour,
			int maxTraceLevel
//			Stroke pointStroke,
//			Color pointColor
		)
	{
		// super(name);

		this.name = name;
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
		this.maxTraceLevel = maxTraceLevel;

		points = new HashMap<PointRaySource2DPointType, PointRaySourcePointGE2D>();
		graphicElements = new ArrayList<GraphicElement2D>();

		for(PointRaySource2DPointType pt:PointRaySource2DPointType.values())
		{
			PointRaySourcePointGE2D p = new PointRaySourcePointGE2D(this, pt);
			points.put(pt, p);
			graphicElements.add(p);
		}
//		rayBundleStartPoint = new PointRaySourcePointGE2D(
//				raysStartPoint,	// position -- set value later
//				this,	// rs
//				PointRaySource2DPointType.S
//				);
//				// new RayBundleStartPoint2D(RAYS_START_POINT_NAME, raysStartPoint, 5, pointStroke, pointColor, true, this);
//		raysCharacteristicsPoint = new PointRaySourcePointGE2D(
//				new Vector2D(0, 0),	// position -- set value later
//				this,	// rs
//				PointRaySource2DPointType.C
//				);
//				// new RaysCharacteristicsPoint2D(RAYS_CHARACTERISTICS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
//		// numberOfRaysPoint = new NumberOfRaysPoint2D(NUMBER_OF_RAYS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
//		
//		graphicElements.add(rayBundleStartPoint);
//		graphicElements.add(raysCharacteristicsPoint);
//		// graphicElements.add(numberOfRaysPoint);
	}



	// setters & getters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector2D getRayStartPoint() {
		return raysStartPoint;
	}

	public void setRayStartPointCoordinatesToThoseOf(Vector2D rayStartPoint) {
		this.raysStartPoint.setCoordinatesToThoseOf(rayStartPoint);
	}

	public Line2D getLineConstrainingStartPoint() {
		return lineConstrainingStartPoint;
	}

	public void setLineConstrainingStartPoint(Line2D lineConstrainingStartPoint) {
		this.lineConstrainingStartPoint = lineConstrainingStartPoint;
	}

//	public Vector2D getRaysCharacteristicsPoint1() {
//		return raysCharacteristicsPoint.getPosition();
//	}
//
//	public void setRaysCharacteristicsPointCoordinatesToThoseOf1(Vector2D rayPoint2) {
//		raysCharacteristicsPoint.setCoordinatesToThoseOf(rayPoint2);
//	}

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
		if((rayBundleNoOfRays > 1) && (rayBundleNoOfRays <= 1000))
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

	public int getMaxTraceLevel() {
		return maxTraceLevel;
	}

	public void setMaxTraceLevel(int maxTraceLevel) {
		this.maxTraceLevel = maxTraceLevel;
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
	
	public PointRaySourcePointGE2D getPoint(PointRaySource2DPointType type)
	{
		return points.get(type);
	}



	//

	public Vector2D getCentralRayDirection() {
		return new Vector2D(Math.cos(centralRayAngle), Math.sin(centralRayAngle));
	}

	public void initialiseRays()
	{
		Vector2D dC = getCentralRayDirection();
		rays = new ArrayList<Ray2D>();
		
		if(lineConstrainingStartPoint != null)
		{
			// use this opportunity to check if the source position is still on the line
			raysStartPoint.setCoordinatesToThoseOf(
					Geometry2D.getPointOnLineClosestToPoint(lineConstrainingStartPoint, raysStartPoint)
				);
		}
		
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
					rays.add(new Ray2D(raysStartPoint, d, maxTraceLevel));
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
					rays.add(new Ray2D(raysStartPoint, d, maxTraceLevel));

					if(!forwardRaysOnly)
					{
						// ... and the backwards ray
						rays.add(new Ray2D(raysStartPoint, d.getProductWith(-1), maxTraceLevel));
					}
				}
			}
		}
		else
		{
			// initialise the forward ray
			rays.add(new Ray2D(raysStartPoint, dC, maxTraceLevel));

			if(!forwardRaysOnly)
			{
				// ... and the backwards ray
				rays.add(new Ray2D(raysStartPoint, dC.getProductWith(-1), maxTraceLevel));
			}
		}
	}
	
	@Override
	public void drawRays(RayPlay2DPanel rpp, Graphics2D g2, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	// public void drawRays(RayPlay2DPanel rpp, Graphics2D g2)
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
	
	@Override
	public void drawGraphicElements(RayPlay2DPanel rpp, Graphics2D g2, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:getGraphicElements())
			ge.draw(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
	}

	@Override
	public void drawOnTop(RayPlay2DPanel rpp, Graphics2D g2, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:getGraphicElements())
			ge.drawOnTop(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
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
		printStream.println("\nRay source \""+name+"\"\n");

		printStream.println("  rayStartPoint = "+getRayStartPoint());
		printStream.println("  rayAngle = "+ MyMath.rad2deg(getRayAngle())+" degrees");
		printStream.println("  rayBundle = "+isRayBundle());
		printStream.println("  rayBundleIsotropic = "+isRayBundleIsotropic());
		printStream.println("  rayBundleAngle = "+MyMath.rad2deg(getRayBundleAngle())+" degrees");
		printStream.println("  rayBundleNoOfRays = "+getRayBundleNoOfRays());
		printStream.println("  forwardRaysOnly = "+isForwardRaysOnly());
	}

	public void writeToCSV(PrintWriter writer)
	{
		writer.write("Ray source, ");
		writer.write("Start point="+getRayStartPoint()+",");
		writer.println("Angle="+ getRayAngle());
		writer.println("Bundle="+isRayBundle());
		writer.println("Isotropic="+isRayBundleIsotropic());
		writer.println("Bundle angle="+getRayBundleAngle());
		writer.println("No of rays="+getRayBundleNoOfRays());
		writer.println("Forward rays only="+isForwardRaysOnly());
	}

	
	//
	// InteractiveOpticalComponent2D methods
	//


	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents() {
		return OpticalComponent2D.NO_COMPONENTS;
	}



	@Override
	public ArrayList<GraphicElement2D> getGraphicElements() {
		return graphicElements;
	}



	@Override
	public InteractiveOpticalComponent2D readFromCSV(String filename) {
		// TODO Auto-generated method stub
		return null;
	}
}