package optics.rayplay.graphicElements;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.DoubleFormatter;

/**
 * The start point of a ray bundle.
 * @author johannes
 */
/**
 * @author johannes
 *
 */
public class PointRaySourcePointGE2D extends PointGE2D
{
	private PointRaySource2D rs;
	
	/**
	 * The point names
	 * @author johannes
	 */
	public enum PointRaySource2DPointType
	{
		S("Ray start point", 5),
		N("Ray number control point", 2),
		D("Direction control point", 3),
		A("Cone angle control point", 3);

		public final String name;
		public final int radius;

		PointRaySource2DPointType(String name, int radius)
		{
			this.name = name;
			this.radius = radius;
		}
	}

	protected PointRaySource2DPointType pt;
	
	
	public PointRaySourcePointGE2D(Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, PointRaySource2D rs, PointRaySource2DPointType pt)
	{
		super(pt.name, position, radius, stroke, color, interactive);
		
		this.rs = rs;
		this.pt = pt;
		
		if(pt == PointRaySource2DPointType.S)
		{
			setPosition(rs.getRayStartPoint());
		}
		// for the other point types, the position gets set at the time of drawing
				
		initPopup();
	}

	public PointRaySourcePointGE2D(PointRaySource2D rs, PointRaySource2DPointType pt)
	{
		this(new Vector2D(0, 0), pt.radius, new BasicStroke(1), Color.gray, true, rs, pt);
		// the actual position gets calculated at the time of drawing
	}
	
	
	// getters & setters
	
	public PointRaySource2D getRs() {
		return rs;
	}

	public void setRs(PointRaySource2D rs) {
		this.rs = rs;
	}

	public PointRaySource2DPointType getPt() {
		return pt;
	}

	public void setPt(PointRaySource2DPointType pt) {
		this.pt = pt;
	}

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		// draw the point only if it is needed
		if(
				(pt == PointRaySource2DPointType.S) ||
				(pt == PointRaySource2DPointType.D) ||
				(pt == PointRaySource2DPointType.A && rs.isRayBundle() && !rs.isRayBundleIsotropic()) ||
				(pt == PointRaySource2DPointType.N && rs.isRayBundle())
				)
		{
			// calculate the position
			if(pt != PointRaySource2DPointType.S)
			{
				double phi = 0;
				if(pt == PointRaySource2DPointType.D) phi = rs.getRayAngle();
				else if(pt == PointRaySource2DPointType.A) phi = rs.getRayAngle()+0.5*rs.getRayBundleAngle();
				else if(pt == PointRaySource2DPointType.N)
				{
					if(rs.isRayBundleIsotropic()) phi = rs.getRayAngle() + 2.*Math.PI/rs.getRayBundleNoOfRays();
					else phi = rs.getRayAngle()+(0.5 - 1./(rs.getRayBundleNoOfRays()-1))*rs.getRayBundleAngle();
				}
				position.setCoordinatesToThoseOf(Vector2D.sum(
						rs.getRayStartPoint(),
						new Vector2D(p.getGoodDistanceXY()*Math.cos(phi), p.getGoodDistanceXY()*Math.sin(phi))
						));
			}

			super.drawOnTop(p,  g,  mouseNear, mouseI, mouseJ);
		}
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel rpp, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case S:
			super.drawAdditionalInfoWhenMouseNear(rpp, g, mouseI, mouseJ);
//			g.setColor(Color.GRAY);
//			g.drawString(
//					getName(), 
//					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
//					);	
			break;
		case D:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			
			rpp.drawLine(rs.getRayStartPoint(), position, g);
			rpp.drawCircle(
					rs.getRayStartPoint(),	// centre
					rpp.getGoodDistanceXY(),	// radius
					g
				);
			
			// give some info
			g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(rs.getRayAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+5);
			break;
		case A:
			g.setPaint(Color.red);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
			rpp.drawSector(
					rs.getRayStartPoint(),	// centre
					rpp.getGoodDistanceXY(),	// radius
					rs.getRayAngle()-0.5*rs.getRayBundleAngle(),	// phi0
					rs.getRayBundleAngle(),
					g
				);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			
			rpp.drawLine(rs.getRayStartPoint(), position, g);
			rpp.drawCircle(
					rs.getRayStartPoint(),	// centre
					rpp.getGoodDistanceXY(),	// radius
					g
				);

			// give some info
			g.drawString("Width of ray bundle = "+DoubleFormatter.format(MyMath.rad2deg(rs.getRayBundleAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+5);
			break;
		case N:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed

			rpp.drawLine(rs.getRayStartPoint(), position, g);

			// give some info
			g.drawString("Number of rays = "+rs.getRayBundleNoOfRays(), rpp.x2i(position.x)+10, rpp.y2j(position.y)+5);
		}
	}
		
	@Override
	public void mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(mouseNear)
		{
			double dx, dy;
			
			switch(pt)
			{
			case S:
				// the vector from the ray start point to ray point 2
				Vector2D d = Vector2D.difference(rs.getPoint(PointRaySource2DPointType.D).getPosition(), rs.getPoint(PointRaySource2DPointType.S).getPosition());

				// update this point's position, which is also the rpp's ray start point
				// the mouse position
				Vector2D p = new Vector2D(rpp.i2x(mouseI), rpp.j2y(mouseJ));
				// is the source position constrained to a line?
				if(rs.getLineConstrainingStartPoint() != null)
				{
					// yes, it's constrained to a line
					position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(rs.getLineConstrainingStartPoint(), p));
				}
				else
					position.setCoordinatesToThoseOf(p);

				// construct ray point 2 as the new ray start point + d
				rs.getPoint(PointRaySource2DPointType.D).setCoordinatesToThoseOf(Vector2D.sum(position, d));
				break;
			case D:
				dx = rpp.i2x(mouseI) - rs.getRayStartPoint().x;
				dy = rpp.j2y(mouseJ) - rs.getRayStartPoint().y;
				rs.setRayAngle(Math.atan2(dy, dx));
				break;
			case A:
				dx = rpp.i2x(mouseI) - rs.getRayStartPoint().x;
				dy = rpp.j2y(mouseJ) - rs.getRayStartPoint().y;
				rs.setRayBundleAngle(2*(Math.atan2(dy, dx) - rs.getRayAngle()));
				break;
			case N:
				dx = rpp.i2x(mouseI) - rs.getRayStartPoint().x;
				dy = rpp.j2y(mouseJ) - rs.getRayStartPoint().y;
				if(rs.isRayBundleIsotropic())
				{
					rs.setRayBundleNoOfRays((int)(2.*Math.PI/(Math.atan2(dy, dx) - rs.getRayAngle()) + 0.5));
				}
				else
				{
					double phi12 = rs.getRayAngle() + 0.5*rs.getRayBundleAngle() - Math.atan2(dy, dx);	// angle between rays 0 and 1
					rs.setRayBundleNoOfRays((int)(rs.getRayBundleAngle()/phi12+0.5));
				}
			}
		}
		else
			super.mouseDragged(rpp, mouseNear, mouseI, mouseJ);
	}

	private RayPlay2DPanel panelAssociatedWithPopup;
	
	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		if(mouseNear && e.isPopupTrigger())
		{
			panelAssociatedWithPopup = rpp;
			
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
	
	final JPopupMenu popup = new JPopupMenu();
	
	// menu items
	JMenuItem
		forwardRaysOnlyMenuItem,
		rayBundleMenuItem,
		rayBundleIsotropicMenuItem,
		darkenExhaustedRaysMenuItem,
		releaseSourcePositionFromLineMenuItem;
	
	private void initPopup()
	{
		forwardRaysOnlyMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		forwardRaysOnlyMenuItem.getAccessibleContext().setAccessibleDescription("Toggle forward rays only");
		forwardRaysOnlyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rs.setForwardRaysOnly(!rs.isForwardRaysOnly());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(forwardRaysOnlyMenuItem);

		rayBundleMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		rayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Toggle ray bundle");
		rayBundleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rs.setRayBundle(!rs.isRayBundle());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(rayBundleMenuItem);

		rayBundleIsotropicMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		rayBundleIsotropicMenuItem.setEnabled(true);
		rayBundleIsotropicMenuItem.getAccessibleContext().setAccessibleDescription("Toggle isotropic ray bundle");
		rayBundleIsotropicMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rs.setRayBundleIsotropic(!rs.isRayBundleIsotropic());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(rayBundleIsotropicMenuItem);
		
		// Separator
	    popup.addSeparator();
		
//		JMenuItem doubleNoOfRaysMenuItem = new JMenuItem("Double no of rays in bundle");
//		doubleNoOfRaysMenuItem.setEnabled(true);
//		doubleNoOfRaysMenuItem.getAccessibleContext().setAccessibleDescription("Double no of rays in bundle");
//		doubleNoOfRaysMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				rs.setRayBundleNoOfRays(2*rs.getRayBundleNoOfRays());
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(doubleNoOfRaysMenuItem);
//		
//		JMenuItem halfNoOfRaysMenuItem = new JMenuItem("Halve no of rays in bundle");
//		halfNoOfRaysMenuItem.setEnabled(true);
//		halfNoOfRaysMenuItem.getAccessibleContext().setAccessibleDescription("Halve no of rays in bundle");
//		halfNoOfRaysMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(rs.getRayBundleNoOfRays() > 2)
//				{
//					rs.setRayBundleNoOfRays(rs.getRayBundleNoOfRays()/2);
//					panelWithPopup.repaint();
//				}
//			}
//		});
//		popup.add(halfNoOfRaysMenuItem);
		
//		int rayNumbers[] = {1, 2, 4, 8, 16, 32, 64, 128, 1024};
//		for(int rayNumber:rayNumbers)
//		{
//			JMenuItem rayNumberMenuItem = new JMenuItem(rayNumber + ((rayNumber == 1)?" ray":" rays"));
//			rayNumberMenuItem.setEnabled(true);
//			rayNumberMenuItem.getAccessibleContext().setAccessibleDescription("Set number of rays to "+rayNumber);
//			rayNumberMenuItem.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					rs.setRayBundleNoOfRays(rayNumber);
//					rs.setRayBundle(rayNumber > 1);
//					panelAssociatedWithPopup.repaint();
//				}
//			});
//			popup.add(rayNumberMenuItem);
//
//		}
//
//		// Separator
//	    popup.addSeparator();

	    for(Colour c:Colour.RAY_COLOURS)
	    {
	    	JMenuItem colourMenuItem = new JMenuItem(c.getName());
	    	colourMenuItem.setEnabled(true);
	    	colourMenuItem.getAccessibleContext().setAccessibleDescription("Set colour to "+c.getName());
	    	colourMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rs.setColour(c);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(colourMenuItem);

	    }
	    
		// Separator
	    popup.addSeparator();

	    darkenExhaustedRaysMenuItem = new JMenuItem("-");	// "Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
	    darkenExhaustedRaysMenuItem.setEnabled(true);
	    darkenExhaustedRaysMenuItem.getAccessibleContext().setAccessibleDescription("Toggle darken exhausted rays");
	    darkenExhaustedRaysMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rs.setDarkenExhaustedRays(!rs.isDarkenExhaustedRays());
				// darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(darkenExhaustedRaysMenuItem);

		// Separator
	    popup.addSeparator();
	    
		int maxTraceLevels[] = {1, 2, 3, 4, 16, 64, 256, 1024};
		for(int maxTraceLevel:maxTraceLevels)
		{
			JMenuItem maxTraceLevelMenuItem = new JMenuItem("max. trace level = "+maxTraceLevel);
			maxTraceLevelMenuItem.setEnabled(true);
			maxTraceLevelMenuItem.getAccessibleContext().setAccessibleDescription("Set max. trace level to "+maxTraceLevel);
			maxTraceLevelMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rs.setMaxTraceLevel(maxTraceLevel);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(maxTraceLevelMenuItem);
		}
	    
		// Separator
	    popup.addSeparator();
	    
	    JMenuItem deleteRayBundleMenuItem = new JMenuItem("Delete light source");
		deleteRayBundleMenuItem.setEnabled(true);
		deleteRayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Delete light source");
		deleteRayBundleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAssociatedWithPopup.graphicElements.removeAll(rs.getGraphicElements());
				panelAssociatedWithPopup.lss.remove(rs);
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(deleteRayBundleMenuItem);

		// Separator
	    popup.addSeparator();
	    
	    releaseSourcePositionFromLineMenuItem = new JMenuItem("Release source position from line");
	    releaseSourcePositionFromLineMenuItem.setEnabled(rs.getLineConstrainingStartPoint() != null);
	    releaseSourcePositionFromLineMenuItem.getAccessibleContext().setAccessibleDescription("Delete light source");
	    releaseSourcePositionFromLineMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rs.setLineConstrainingStartPoint(null);
			}
		});
		popup.add(releaseSourcePositionFromLineMenuItem);
}
	
	private void updatePopup()
	{
		// enable/disable + text
		
		forwardRaysOnlyMenuItem.setText(rs.isForwardRaysOnly()?"Make ray(s) bidirectional":"Make ray(s) unidirectional");
		forwardRaysOnlyMenuItem.setEnabled(!rs.isRayBundleIsotropic());
		
		rayBundleMenuItem.setText(rs.isRayBundle()?"Change to single ray":"Change to ray bundle");
		rayBundleMenuItem.setEnabled(!rs.isRayBundleIsotropic());
		
		rayBundleIsotropicMenuItem.setText(rs.isRayBundleIsotropic()?"Make ray bundle directional":"Make ray bundle isotropic");
		rayBundleIsotropicMenuItem.setEnabled(rs.isRayBundle());
		
		darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
		
	    releaseSourcePositionFromLineMenuItem.setEnabled(rs.getLineConstrainingStartPoint() != null);
	}	
}
