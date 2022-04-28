package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OLPointType;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point in an omnidirectional lens that is moveable on a line.
 * @author johannes
 */
public class OLPointMoveableOnLineGE2D extends PointMoveableOnLineGE2D
{
	private OmnidirectionalLens2D ol;
	
	private OLPointType pt;
	
	public OLPointMoveableOnLineGE2D(String name, Vector2D position, Line2D line, int radius, Stroke stroke, Color color, boolean interactive, OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, position, line, radius, stroke, color, interactive);
		
		this.ol = ol;
		this.pt = pt;
	}

	public OLPointMoveableOnLineGE2D(String name, OmnidirectionalLens2D ol, OLPointType pt)
	{
		this(name, new Vector2D(0, 0), null, 3, new BasicStroke(1), Color.gray, true, ol, pt);
	}

	
	// getters & setters
	
	public OmnidirectionalLens2D getOl() {
		return ol;
	}

	public void setOl(OmnidirectionalLens2D ol) {
		this.ol = ol;
	}

	public OLPointType getPt() {
		return pt;
	}

	public void setPt(OLPointType pt) {
		this.pt = pt;
	}

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case P0:
			break;
		case P1:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h1 =" + DoubleFormatter.format(ol.getH1()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case P2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h2 =" + DoubleFormatter.format(ol.getH2()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case P3:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h =" + DoubleFormatter.format(ol.getH()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case V1:
		case V2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (rD =" + DoubleFormatter.format(ol.getrD()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case FD:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (f_D =" + DoubleFormatter.format(ol.getfD()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);	
		}
	}

	
	@Override
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(pt == OLPointType.P0)
		{
			// don't do anything
			return;
		}

		super.mouseDragged(c, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			switch(pt)
			{
			case P0:
				// this should never be called
				break;
			case P1:
				ol.setH1(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										line.getDirection()
										)
								));
				ol.calculatePointParameters();
				break;
			case P2:
				ol.setH2(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										line.getDirection()
										)
								));
				ol.calculatePointParameters();
				break;
			case P3:
				ol.setH(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										line.getDirection()
										)
								));
				ol.calculatePointParameters();
				break;
			case V1:
			case V2:
				ol.setrD(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										line.getDirection()
										)
								));
				ol.calculatePointParameters();
				break;
			case FD:
				ol.setfD(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ol.getpD()),
								line.getDirection()
								)
						);
				break;
			}
			
			ol.calculateLensParameters();
		}
	}

}
