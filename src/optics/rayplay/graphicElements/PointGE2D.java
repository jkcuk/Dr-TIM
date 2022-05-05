package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.RayPlay2DPanel;

public class PointGE2D implements GraphicElement2D
{
	protected String name;

	protected Vector2D position;
	
	/**
	 * radius, in pixels
	 */
	protected int radius;
	
	protected Stroke stroke;
	
	protected Color color;
	
	protected boolean interactive;
	
	// protected ArrayList<GraphicElement2DEventHandler> graphicElement2DEventHandlers = new ArrayList<GraphicElement2DEventHandler>();
	

	public PointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive)
	{
		this.name = name;
		this.position = position;
		this.radius = radius;
		this.stroke = stroke;
		this.color = color;
		this.interactive = interactive;
	}

	public PointGE2D(String name, double x, double y, int radius, Stroke stroke, Color color, boolean interactive)
	{
		this(name, new Vector2D(x, y), radius, stroke, color, interactive);
	}
	
	public PointGE2D(String name)
	{
		this(name, null, 3, new BasicStroke(1), Color.gray, true);
	}

	
	// getters & setters
	
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	/**
	 * Set the components of this point to those of v
	 * @param v
	 */
	public void setCoordinatesToThoseOf(Vector2D position)
	{
		this.position.x = position.x;
		this.position.y = position.y;
	}
	
	
	// GraphicElement2D methods

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isInteractive() {
		return interactive;
	}

	@Override
	public boolean isMouseNear(RayPlay2DPanel p, int i, int j)
	{
		int iDistance = p.x2i(position.x) - i;
		int jDistance = p.y2j(position.y) - j;

		return iDistance*iDistance + jDistance*jDistance < radius*radius;
	}

	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{}
	
	@Override
	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		g.setStroke(stroke);
		g.setColor(color);

		int pointI = p.x2i(position.x);
		int pointJ = p.y2j(position.y);

		// is the mouse within the radius from the point?
		if(mouseNear)
		{
			g.fillOval(pointI-radius, pointJ-radius, 2*radius, 2*radius);
		}
		else
			g.drawOval(pointI-radius, pointJ-radius, 2*radius, 2*radius);			
	}
	
	
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		g.setColor(Color.GRAY);
		g.drawString(
				getName(), 
				mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
				);	
	}

	
	@Override
	public void mouseDragged(RayPlay2DPanel p, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(mouseNear)
		{
			position.x = p.i2x(mouseI);
			position.y = p.j2y(mouseJ);
		}
//		for(GraphicElement2DEventHandler h:graphicElement2DEventHandlers)
//			h.mouseDragged(this, mouseNear, mouseI, mouseJ);
	}

	@Override
	public void mouseClicked(RayPlay2DPanel p, boolean mouseNear, MouseEvent e)
	{
		// ignore	
	}

	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		// not handled
		return false;
	}

	@Override
	public void writeSVGCode(RayPlay2DPanel rpp) {
	}

//	public void addGraphicElement2DEventHandler(GraphicElement2DEventHandler h)
//	{
//		graphicElement2DEventHandlers.add(h);
//	}
}
