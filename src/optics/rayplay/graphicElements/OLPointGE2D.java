package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.opticalComponents.OmnidirectionalLens2D;
import optics.rayplay.opticalComponents.OmnidirectionalLens2D.OLPointType;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point in an omnidirectional lens that is moveable freely.
 * @author johannes
 */
public class OLPointGE2D extends PointGE2D
{
	protected OmnidirectionalLens2D ol;
	
	protected OLPointType pt;
	
	public OLPointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ol = ol;
		this.pt = pt;
	}

	public OLPointGE2D(String name, Vector2D position, OmnidirectionalLens2D ol, OLPointType pt)
	{
		this(name, position, 3, new BasicStroke(1), Color.gray, true, ol, pt);
	}

	public OLPointGE2D(String name, OmnidirectionalLens2D ol, OLPointType pt)
	{
		this(name, new Vector2D(0, 0), 3, new BasicStroke(1), Color.gray, true, ol, pt);
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
			super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
			break;
		case P3:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h =" + DoubleFormatter.format(ol.getH()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		default:
			// this shouldn't happen
		}
	}

	
	@Override
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ)
	{
		super.mouseDragged(c, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			if(pt == OLPointType.P0)
			{
				// ol.setpD(position);
			}
			else if(pt == OLPointType.P3)
			{
				Vector2D d = Vector2D.difference(position, ol.getpD());
				ol.setcHat(d);
				ol.setdHat(d.getPerpendicularVector());
				
				double scaleFactor = d.getLength()/ol.getH();
				ol.setH(scaleFactor*ol.getH());
				ol.setH1(scaleFactor*ol.getH1());
				ol.setH2(scaleFactor*ol.getH2());
				ol.setfD(scaleFactor*ol.getfD());
				ol.setrD(scaleFactor*ol.getrD());
			}
			
			ol.calculatePointParameters();
			ol.calculateLensParameters();
		}
	}

}
