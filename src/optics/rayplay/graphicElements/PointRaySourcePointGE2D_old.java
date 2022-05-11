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
public class PointRaySourcePointGE2D_old extends PointGE2D
{
	private PointRaySource2D rs;
	
	/**
	 * The point names
	 * @author johannes
	 */
	public enum PointRaySource2DPointType
	{
		S("Ray start point"),
		C("Ray source control point");

		public final String name;

		PointRaySource2DPointType(String name) {this.name = name;}
	}

	protected PointRaySource2DPointType pt;
	
	
	public PointRaySourcePointGE2D_old(Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, PointRaySource2D rs, PointRaySource2DPointType pt)
	{
		super(pt.name, position, radius, stroke, color, interactive);
		
		this.rs = rs;
		this.pt = pt;
		
		if(pt == PointRaySource2DPointType.C)
		{
			// calculate the position
			double l = tanRayBundleAngleConstant / Math.tan(rs.getRayBundleAngle()/2.);
			position.setCoordinatesToThoseOf(Vector2D.sum(
					rs.getRayStartPoint(),
					new Vector2D(l*Math.cos(rs.getRayAngle()), l*Math.sin(rs.getRayAngle()))	// TODO set the distance correctly
					)
					);
		}
		
		initPopup();
	}

	public PointRaySourcePointGE2D_old(Vector2D position, PointRaySource2D rs, PointRaySource2DPointType pt)
	{
		this(position, 3, new BasicStroke(1), Color.gray, true, rs, pt);
		
		switch(pt)
		{
		case S:
			setRadius(5);
			break;
		case C:
			setRadius(3);
		}
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
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel rpp, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case S:
			g.setColor(Color.GRAY);
			g.drawString(
					getName(), 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);	
			break;
		case C:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed

			rpp.drawLine(rs.getRayStartPoint(), position, g);

			Vector2D d = rs.getCentralRayDirection();
			Vector2D n = d.getPerpendicularVector().getNormalised();

			if(rs.isRayBundle() && !rs.isRayBundleIsotropic())
			{
				Vector2D p1 = Vector2D.sum(position, n.getProductWith(-tanRayBundleAngleConstant));
				Vector2D p2 = Vector2D.sum(position, n.getProductWith( tanRayBundleAngleConstant));
				
				g.setPaint(Color.red);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
				rpp.drawTriangle(rs.getRayStartPoint(), p1, p2, true, g);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

				g.setColor(Color.GRAY);
				rpp.drawLine(p1, p2, g);
			}

			// give some info
			g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(rs.getRayAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+(rs.isRayBundle()?-5:5));
			if(rs.isRayBundle() && !rs.isRayBundleIsotropic())
				g.drawString("Ray bundle angular width = "+DoubleFormatter.format(MyMath.rad2deg(rs.getRayBundleAngle()))+" degrees", rpp.x2i(position.x)+10, rpp.y2j(position.y)+15);
		}
	}
	
	private double tanRayBundleAngleConstant = 0.25;
	
	@Override
	public void mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		if(mouseNear)
		{
			Vector2D d;
			switch(pt)
			{
			case S:
				// the vector from the ray start point to ray point 2
				d = Vector2D.difference(rs.getRaysCharacteristicsPoint(), rs.getRayStartPoint());

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
				rs.setRaysCharacteristicsPointCoordinatesToThoseOf(Vector2D.sum(position, d));
				break;
			case C:
				super.mouseDragged(rpp, mouseNear, mouseI, mouseJ);

				d = Vector2D.difference(position, rs.getRayStartPoint());
				rs.setRayAngle(Math.atan2(d.y, d.x));
				rs.setRayBundleAngle(2*Math.atan(tanRayBundleAngleConstant/d.getLength()));	// 180*dC.getLength();
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
		// rayBundleMenuItem,
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

//		rayBundleMenuItem = new JMenuItem("-");
//		// menuItem.setMnemonic(KeyEvent.VK_P);
//		rayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Toggle ray bundle");
//		rayBundleMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				rs.setRayBundle(!rs.isRayBundle());
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(rayBundleMenuItem);

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
		
		int rayNumbers[] = {1, 2, 4, 8, 16, 32, 64, 128, 1024};
		for(int rayNumber:rayNumbers)
		{
			JMenuItem rayNumberMenuItem = new JMenuItem(rayNumber + ((rayNumber == 1)?" ray":" rays"));
			rayNumberMenuItem.setEnabled(true);
			rayNumberMenuItem.getAccessibleContext().setAccessibleDescription("Set number of rays to "+rayNumber);
			rayNumberMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rs.setRayBundleNoOfRays(rayNumber);
					rs.setRayBundle(rayNumber > 1);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(rayNumberMenuItem);

		}

		// Separator
	    popup.addSeparator();

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
		
//		rayBundleMenuItem.setText(rs.isRayBundle()?"Change to single ray":"Change to ray bundle");
//		rayBundleMenuItem.setEnabled(!rs.isRayBundleIsotropic());
		
		rayBundleIsotropicMenuItem.setText(rs.isRayBundleIsotropic()?"Make ray bundle directional":"Make ray bundle isotropic");
		rayBundleIsotropicMenuItem.setEnabled(rs.isRayBundle());
		
		darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
		
	    releaseSourcePositionFromLineMenuItem.setEnabled(rs.getLineConstrainingStartPoint() != null);
	}	
}
