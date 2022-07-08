package optics.rayplay.graphicElements;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveElement2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Circle2D;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;

/**
 * A graphic element representing an infinite line
 * @author johannes
 */
public class LineGE2D extends Line2D implements GraphicElement2D, InteractiveElement2D
{
	protected String name;
	protected Stroke stroke;
	protected Colour colour;
	protected String svgStyle;
	protected int svgLineThickness;
	protected RayPlay2DPanel rayPlay2DPanel;
	
	public LineGE2D(String name, Vector2D a, Vector2D b, Stroke stroke, Colour colour, int svgLineThickness, String svgStyle, RayPlay2DPanel rayPlay2DPanel)
	{
		super(a, b);
		
		this.name = name;
		this.stroke = stroke;
		this.colour = colour;
		this.svgLineThickness = svgLineThickness;
		this.svgStyle = svgStyle;
		this.rayPlay2DPanel = rayPlay2DPanel;
		
		initPopup();
	}

	@Override
	public String getName()
	{
		return name;
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
		Line2D l = new Line2D(
				new Vector2D(p.x2i(a.x), p.y2j(a.y)),
				new Vector2D(p.x2i(b.x), p.y2j(b.y))
				);
		double d = Geometry2D.linePointDistance(l, new Vector2D(i, j));
		return (d < 3);
	}
	
	
	//getters and setters

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
	
	public Vector2D[] getEndPoints()
	{
		Circle2D enclosingCircle = rayPlay2DPanel.getEnclosingCircle();
		return Geometry2D.getLineCircleIntersections(
				a,	// pointOnLine
				Vector2D.difference(b, a),	// lineDirection
				enclosingCircle.getCentre(),	// circleCentre
				enclosingCircle.getRadius()	// circleRadius
			);
	}


	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// by default, just ignore the mouse; override to change this

		g.setStroke(stroke);
		g.setColor(colour.getColor());
		Vector2D[] endPoint = getEndPoints();
		
		if(endPoint != null)
		{
			System.out.println("endPoint[0]="+endPoint[0]+", endPoint[1]="+endPoint[1]);
			p.drawLine(endPoint[0], endPoint[1], g);
		}
		else
		{
			System.out.println("endPoint=null");
		}
	}
	
	@Override
	public void drawInFront(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// draw(p, g, mouseNear, mouseI, mouseJ);
	}
	
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
	
	@Override
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
	
	
	private RayPlay2DPanel panelWithPopup;
	
	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		if(mouseNear && e.isPopupTrigger())
		{
			panelWithPopup = rpp;
			popupMenuX = e.getX();
			popupMenuY = e.getY();
			
			updatePopup();

			popup.show(e.getComponent(), e.getX(), e.getY());
			
			// say that the event has been handled
			return true;
		}
		return false;
	}


	// 
	// popup menu
	// 
	
	private int popupMenuX, popupMenuY;
	final JPopupMenu popup = new JPopupMenu();
	
	private void initPopup()
	{
	    JMenuItem addRaySourceMenuItem = new JMenuItem("Add ray source constrained to this line");
	    addRaySourceMenuItem.setEnabled(true);
	    addRaySourceMenuItem.getAccessibleContext().setAccessibleDescription("Add ray source constrained to this line");
	    addRaySourceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PointRaySource2D ls = new PointRaySource2D(
						"Point ray source constrained to line \""+LineGE2D.this.getName()+"\"",	// name
						Geometry2D.getPointOnLineClosestToPoint(LineGE2D.this, new Vector2D(panelWithPopup.i2x(popupMenuX), panelWithPopup.j2y(popupMenuY))),	// raysStartPoint
						MyMath.deg2rad(0), // centralRayAngle
						false,	// forwardRaysOnly
						true, // rayBundle
						true,	// rayBundleIsotropic
						MyMath.deg2rad(30), // rayBundleAngle
						64,	// rayBundleNoOfRays
						Colour.GREEN,
						255,	// maxTraceLevel
						rayPlay2DPanel
						);
				// set the line constraining the source position
				ls.setLineConstrainingStartPoint(
						LineGE2D.this
					);
				panelWithPopup.iocs.add(ls);
				// panelWithPopup.graphicElements.addAll(ls.getGraphicElements());

				panelWithPopup.repaint();
			}
		});
		popup.add(addRaySourceMenuItem);
}
	
	private void updatePopup()
	{
		// enable/disable + text
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ) {
		// TODO Auto-generated method stub
		
	}	

}
