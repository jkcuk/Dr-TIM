package optics.rayplay.graphicElements;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;
import optics.rayplay.core.LightSource2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.util.DoubleFormatter;

/**
 * The second point, after the start point, that defines the main characteristics of a ray bundle.
 * @author johannes
 */
public class RaysCharacteristicsPoint2D extends PointGE2D
{
	private LightSource2D ls;
	
	public RaysCharacteristicsPoint2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, LightSource2D ls)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ls = ls;
		
		// calculate the position
		calculateAndSetPosition();
	}

	public void calculateAndSetPosition()
	{
		// calculate the position
		double l = tanRayBundleAngleConstant / Math.tan(ls.getRayBundleAngle()/2.);
		position.setCoordinatesToThoseOf(Vector2D.sum(
				ls.getRayStartPoint(),
				new Vector2D(l*Math.cos(ls.getRayAngle()), l*Math.sin(ls.getRayAngle()))	// TODO set the distance correctly
				)
				);
	}

	public RaysCharacteristicsPoint2D(String name, LightSource2D ls)
	{
		this(name, null, 3, new BasicStroke(1), Color.gray, true, ls);
	}

	
	// getters & setters
	
	public LightSource2D getLs() {
		return ls;
	}

	public void setLs(LightSource2D ls) {
		this.ls = ls;
	}

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel rpp, Graphics2D g, int mouseI, int mouseJ)
	{
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed

		rpp.drawLine(ls.getRayStartPoint(), position, g);

		Vector2D d = ls.getCentralRayDirection();
		Vector2D n = d.getPerpendicularVector().getNormalised();

		if(ls.isRayBundle() && !ls.isRayBundleIsotropic())
		{
			Vector2D p1 = Vector2D.sum(position, n.getProductWith(-tanRayBundleAngleConstant));
			Vector2D p2 = Vector2D.sum(position, n.getProductWith( tanRayBundleAngleConstant));
			
			g.setPaint(Color.red);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			rpp.drawTriangle(ls.getRayStartPoint(), p1, p2, true, g);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			g.setColor(Color.GRAY);
			rpp.drawLine(p1, p2, g);
		}

		// give some info
		g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(ls.getRayAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+(ls.isRayBundle()?-5:5));
		if(ls.isRayBundle() && !ls.isRayBundleIsotropic())
			g.drawString("Ray bundle angular width = "+DoubleFormatter.format(MyMath.rad2deg(ls.getRayBundleAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+15);

	}
	
	private double tanRayBundleAngleConstant = 0.25;

	@Override
	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ)
	{
		super.mouseDragged(c, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			Vector2D d = Vector2D.difference(position, ls.getRayStartPoint());
			ls.setRayAngle(Math.atan2(d.y, d.x));
			ls.setRayBundleAngle(2*Math.atan(tanRayBundleAngleConstant/d.getLength()));	// 180*dC.getLength();
		}
	}

//	@Override
//	public void mouseClicked(CoordinateConverterXY2IJ c, boolean mouseNear, MouseEvent e)
//	{
//		super.mouseClicked(c,  mouseNear, e);
//		
//		if (e.getClickCount() == 2)  // double click
//		{
//			if(mouseNear)
//			{
//				rpp.setRayBundle(!rpp.isRayBundle());
//			}
//		}
//	}
}
