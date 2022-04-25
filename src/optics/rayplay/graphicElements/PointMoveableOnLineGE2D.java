package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;

public class PointMoveableOnLineGE2D extends PointGE2D
{
	/**
	 * The line on which the point can move.
	 * The line can also be a LineSegment2D, which restricts the point to the line segment.
	 */
	protected Line2D line;
	

	public PointMoveableOnLineGE2D(String name, Vector2D position, Line2D line, int radius, Stroke stroke, Color color, boolean interactive)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.line = line;
	}

	public PointMoveableOnLineGE2D(String name, double x, double y, Line2D line, int radius, Stroke stroke, Color color, boolean interactive)
	{
		this(name, new Vector2D(x, y), line, radius, stroke, color, interactive);
	}
	
	public PointMoveableOnLineGE2D(String name)
	{
		this(name, null, null, 3, new BasicStroke(1), Color.gray, true);
	}

	
	// getters & setters
	
	public Line2D getLine() {
		return line;
	}

	public void setLine(Line2D line) {
		this.line = line;
	}

	
	
	// GraphicElement2D methods
	
	@Override
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(mouseNear)
		{
			Vector2D v = new Vector2D(c.i2x(mouseI), c.j2y(mouseJ));
			
			position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(line, v));
		}
	}
}
