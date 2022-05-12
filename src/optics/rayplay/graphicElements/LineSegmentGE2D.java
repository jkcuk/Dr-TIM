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
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.LineSegment2D;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;

public abstract class LineSegmentGE2D extends LineSegment2D implements GraphicElement2D
{
	private Stroke stroke;
	private Colour colour;
	private String svgStyle;
	private int svgLineThickness;
	
	public LineSegmentGE2D(Vector2D a, Vector2D b, Stroke stroke, Colour colour, int svgLineThickness, String svgStyle)
	{
		super(a, b);
		
		this.stroke = stroke;
		this.colour = colour;
		this.svgLineThickness = svgLineThickness;
		this.svgStyle = svgStyle;
		
		initPopup();
	}

	@Override
	public abstract String getName();

	@Override
	public abstract boolean isInteractive();

	@Override
	public boolean isMouseNear(RayPlay2DPanel p, int i, int j)
	{
		// define the line segment in (i,j) coordinates
		LineSegment2D s = new LineSegment2D(
				new Vector2D(p.x2i(a.x), p.y2j(a.y)),
				new Vector2D(p.x2i(b.x), p.y2j(b.y))
				);

		return (Geometry2D.lineSementPointDistance(s, new Vector2D(i, j)) < 3);
	}

	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// by default, just ignore the mouse; override to change this

		g.setStroke(stroke);
		g.setColor(colour.getColor());
		p.drawLine(a, b, g);
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
			p.drawLine(a, b, g);
		}
	}
	
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		SVGWriter.writeSVGLine(a, b, rpp, colour.getSVGName(), svgLineThickness, svgStyle);
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
		System.out.println("Hi");
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
						"Point ray source constrained to line \""+LineSegmentGE2D.this.getName()+"\"",	// name
						Geometry2D.getPointOnLineClosestToPoint(LineSegmentGE2D.this, new Vector2D(panelWithPopup.i2x(popupMenuX), panelWithPopup.j2y(popupMenuY))),	// raysStartPoint
						MyMath.deg2rad(0), // centralRayAngle
						false,	// forwardRaysOnly
						true, // rayBundle
						true,	// rayBundleIsotropic
						MyMath.deg2rad(30), // rayBundleAngle
						64,	// rayBundleNoOfRays
						Colour.GREEN,
						255	// maxTraceLevel
						);
				// set the line constraining the source position
				ls.setLineConstrainingStartPoint(
						LineSegmentGE2D.this
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

}
