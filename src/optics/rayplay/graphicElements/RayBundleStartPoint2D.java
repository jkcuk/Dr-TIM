package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.Vector2D;
import optics.rayplay.core.RaySource2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.util.Colour;

/**
 * The start point of a ray bundle.
 * @author johannes
 */
public class RayBundleStartPoint2D extends PointGE2D
{
	private RaySource2D ls;
	
	public RayBundleStartPoint2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, RaySource2D ls)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ls = ls;
		
		initPopup();
	}

	public RayBundleStartPoint2D(String name, RaySource2D ls)
	{
		this(name, null, 3, new BasicStroke(1), Color.gray, true, ls);
	}

	
	// getters & setters
	
	public RaySource2D getLs() {
		return ls;
	}

	public void setLs(RaySource2D ls) {
		this.ls = ls;
	}

	
	
	// GraphicElement2D methods
	
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
			// the vector from the ray start point to ray point 2
			Vector2D d = Vector2D.difference(ls.getRaysCharacteristicsPoint(), ls.getRayStartPoint());
			
			// update this point's position, which is also the rpp's ray start point
			super.mouseDragged(p, mouseNear, mouseI, mouseJ);
			// rpp.setRayStartPointCoordinatesToThoseOf(position);
			
			// construct ray point 2 as the new ray start point + d
			ls.setRaysCharacteristicsPointCoordinatesToThoseOf(Vector2D.sum(position, d));
		}
		else
			super.mouseDragged(p, mouseNear, mouseI, mouseJ);
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
//				rpp.setForwardRaysOnly(!rpp.isForwardRaysOnly());
//			}
//		}
//	}
	
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
		darkenExhaustedRaysMenuItem;
	
	private void initPopup()
	{
		forwardRaysOnlyMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		forwardRaysOnlyMenuItem.getAccessibleContext().setAccessibleDescription("Toggle forward rays only");
		forwardRaysOnlyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ls.setForwardRaysOnly(!ls.isForwardRaysOnly());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(forwardRaysOnlyMenuItem);

//		rayBundleMenuItem = new JMenuItem("-");
//		// menuItem.setMnemonic(KeyEvent.VK_P);
//		rayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Toggle ray bundle");
//		rayBundleMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				ls.setRayBundle(!ls.isRayBundle());
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
				ls.setRayBundleIsotropic(!ls.isRayBundleIsotropic());
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
//				ls.setRayBundleNoOfRays(2*ls.getRayBundleNoOfRays());
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
//				if(ls.getRayBundleNoOfRays() > 2)
//				{
//					ls.setRayBundleNoOfRays(ls.getRayBundleNoOfRays()/2);
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
					ls.setRayBundleNoOfRays(rayNumber);
					ls.setRayBundle(rayNumber > 1);
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
					ls.setColour(c);
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
					ls.setMaxTraceLevel(maxTraceLevel);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(maxTraceLevelMenuItem);
		}

		// Separator
	    popup.addSeparator();

	    darkenExhaustedRaysMenuItem = new JMenuItem("-");	// "Switch darkening of exhausted rays "+(ls.isDarkenExhaustedRays()?"off":"on"));
	    darkenExhaustedRaysMenuItem.setEnabled(true);
	    darkenExhaustedRaysMenuItem.getAccessibleContext().setAccessibleDescription("Toggle darken exhausted rays");
	    darkenExhaustedRaysMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ls.setDarkenExhaustedRays(!ls.isDarkenExhaustedRays());
				// darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(ls.isDarkenExhaustedRays()?"off":"on"));
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
				panelAssociatedWithPopup.graphicElements.removeAll(ls.getGraphicElements());
				panelAssociatedWithPopup.lss.remove(ls);
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(deleteRayBundleMenuItem);
	}
	
	private void updatePopup()
	{
		// enable/disable + text
		
		forwardRaysOnlyMenuItem.setText(ls.isForwardRaysOnly()?"Make ray(s) bidirectional":"Make ray(s) unidirectional");
		forwardRaysOnlyMenuItem.setEnabled(!ls.isRayBundleIsotropic());
		
//		rayBundleMenuItem.setText(ls.isRayBundle()?"Change to single ray":"Change to ray bundle");
//		rayBundleMenuItem.setEnabled(!ls.isRayBundleIsotropic());
		
		rayBundleIsotropicMenuItem.setText(ls.isRayBundleIsotropic()?"Make ray bundle directional":"Make ray bundle isotropic");
		rayBundleIsotropicMenuItem.setEnabled(ls.isRayBundle());
		
		darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(ls.isDarkenExhaustedRays()?"off":"on"));
	}	
}
