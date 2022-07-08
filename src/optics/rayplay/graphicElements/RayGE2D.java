package optics.rayplay.graphicElements;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;


import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Circle2D;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.LineSegment2D;
import optics.rayplay.geometry2D.Ray2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;

/**
 * A ray, i.e. a semi-infinite line
 * @author johannes
 *
 */
public class RayGE2D extends Ray2D implements GraphicElement2D
{
	private String name;
	private Stroke stroke;
	private Colour colour;
	private String svgStyle;
	private int svgLineThickness;
	protected RayPlay2DPanel rayPlay2DPanel;
	
	/**
	 * Create a ray graphics element with start point s and direction d
	 * @param s
	 * @param d
	 * @param name
	 * @param stroke
	 * @param colour
	 * @param svgLineThickness
	 * @param svgStyle
	 * @param rayPlay2DPanel
	 */
	public RayGE2D(Vector2D s, Vector2D d, String name, Stroke stroke, Colour colour, int svgLineThickness, String svgStyle, RayPlay2DPanel rayPlay2DPanel)
	{
		super(s, d);
		
		this.name = name;
		this.stroke = stroke;
		this.colour = colour;
		this.svgLineThickness = svgLineThickness;
		this.svgStyle = svgStyle;
		this.rayPlay2DPanel = rayPlay2DPanel;
	}

	@Override
	public boolean isInteractive()
	{
		return false;
	}

	@Override
	public boolean isMouseNear(RayPlay2DPanel p, int i, int j)
	{
		// define the line segment in (i,j) coordinates
		Vector2D[] endPoint = getEndPoints();

		if(endPoint == null) return false;
		
		LineSegment2D s = new LineSegment2D(
				new Vector2D(p.x2i(endPoint[0].x), p.y2j(endPoint[0].y)),
				new Vector2D(p.x2i(endPoint[1].x), p.y2j(endPoint[1].y))
				);

		return (Geometry2D.lineSementPointDistance(s, new Vector2D(i, j)) < 3);
	}
	
	
	//getters and setters

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}

	public String getSvgStyle() {
		return svgStyle;
	}

	public void setSvgStyle(String svgStyle) {
		this.svgStyle = svgStyle;
	}

	public int getSvgLineThickness() {
		return svgLineThickness;
	}

	public void setSvgLineThickness(int svgLineThickness) {
		this.svgLineThickness = svgLineThickness;
	}
	
	public RayPlay2DPanel getRayPlay2DPanel() {
		return rayPlay2DPanel;
	}

	public void setRayPlay2DPanel(RayPlay2DPanel rayPlay2DPanel) {
		this.rayPlay2DPanel = rayPlay2DPanel;
	}
	
	
	
	//
	// the meat
	//
	
	/**
	 * @return	the end points for drawing purposes; null if nothing is shown
	 */
	public Vector2D[] getEndPoints()
	{
		Circle2D enclosingCircle = rayPlay2DPanel.getEnclosingCircle();
		double alpha[] = Geometry2D.getAlphaForLineCircleIntersections(
				startPoint,	// pointOnLine
				normalisedDirection,	// lineDirection
				enclosingCircle.getCentre(),	// circleCentre
				enclosingCircle.getRadius()	// circleRadius
			);
		if(alpha != null)
		{
			// there are intersections, but are they in the "forward" direction?
			if(Math.max(alpha[0], alpha[1]) >= 0)
			{
				// at least one intersection is in the "forward" direction
				Vector2D intersection[] = new Vector2D[2];

				for(int i=0; i<=1; i++)
				{
					if(alpha[i] < 0) intersection[i] = startPoint;
					else intersection[i] = Vector2D.sum(startPoint, normalisedDirection.getProductWith(alpha[i]));
				}
				
				return intersection;
			}
		}
		
		return null;
	}

	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// by default, just ignore the mouse; override to change this

		g.setStroke(stroke);
		g.setColor(colour.getColor());
		Vector2D[] endPoint = getEndPoints();
		if(endPoint != null) p.drawLine(endPoint[0], endPoint[1], g);
	}
	
	@Override
	public void drawInFront(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{}
	
	@Override
	public void drawBehind(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(mouseNear)
		{
			float lineWidth = ((BasicStroke)stroke).getLineWidth();
			g.setStroke(new BasicStroke(lineWidth+4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(Color.LIGHT_GRAY);
			Vector2D[] endPoint = getEndPoints();
			if(endPoint != null) p.drawLine(endPoint[0], endPoint[1], g);
		}
	}
	
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		Vector2D[] endPoint = getEndPoints();

		if(endPoint != null) SVGWriter.writeSVGLine(endPoint[0], endPoint[1], rpp, colour.getSVGName(), svgLineThickness, svgStyle);
	}
	
	@Override
	public boolean mouseDragged(RayPlay2DPanel p, boolean mouseNear, int mouseI, int mouseJ)
	{
		return false;
	}
	
	@Override
	public boolean mouseClicked(RayPlay2DPanel p, boolean mouseNear, MouseEvent e)
	{
		// don't do anything
		return false;
	}
	
	
	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		return false;
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ) {
		// TODO Auto-generated method stub
		
	}
}
