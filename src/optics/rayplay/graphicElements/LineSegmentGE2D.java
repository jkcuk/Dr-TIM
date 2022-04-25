package optics.rayplay.graphicElements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.LineSegment2D;

public abstract class LineSegmentGE2D extends LineSegment2D implements GraphicElement2D
{
	private Stroke stroke;
	private Color color;
	
	public LineSegmentGE2D(Vector2D a, Vector2D b, Stroke stroke, Color color)
	{
		super(a, b);
		
		this.stroke = stroke;
		this.color = color;
	}

	@Override
	public abstract String getName();

	@Override
	public abstract boolean isInteractive();

	@Override
	public boolean isMouseNear(CoordinateConverterXY2IJ cc, int i, int j)
	{
		// define the line segment in (i,j) coordinates
		LineSegment2D s = new LineSegment2D(
				new Vector2D(cc.x2i(a.x), cc.y2j(a.y)),
				new Vector2D(cc.x2i(b.x), cc.y2j(b.y))
				);

		return (Geometry2D.lineSementPointDistance(s, new Vector2D(i, j)) < 3);
	}

	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// by default, just ignore the mouse; override to change this

		g.setStroke(stroke);
		g.setColor(color);
		p.drawLine(a, b, g);
	}
	
	@Override
	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{}
	
	@Override
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ)
	{
		// ignore
	}
	
	@Override
	public void mouseClicked(CoordinateConverterXY2IJ c, boolean mouseNear, MouseEvent e)
	{	
	}
}
