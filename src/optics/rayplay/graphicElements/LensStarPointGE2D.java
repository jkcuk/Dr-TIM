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
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;
import optics.rayplay.interactiveOpticalComponents.LensStar2D;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point in an omnidirectional lens.
 * @author johannes
 */
public class LensStarPointGE2D extends PointGE2D
{
	protected LensStar2D ls;
	
	/**
	 * The point/vertex names
	 * @author johannes
	 */
	public enum LensStarPointType
	{
		P0("Principal point of lens 0", 3),
		F0("Focal point of lens 0", 3),
		L0("End point of lens 0", 3),
		L1("End point of lens 1", 2),
		C("Centre", 5);

		public final String name;
		public final int radius;

		LensStarPointType(String name, int radius)
		{
			this.name = name;
			this.radius = radius;
		}
	}

	protected LensStarPointType pt;
	
	public LensStarPointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, LensStar2D ls, LensStarPointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ls = ls;
		this.pt = pt;
		
		initPopup();
	}

	public LensStarPointGE2D(String name, Vector2D position, LensStar2D ls, LensStarPointType pt)
	{
		this(name, position, pt.radius, new BasicStroke(1), Color.gray, true, ls, pt);
	}


	
	// getters & setters
	

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case C:
			super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
			break;
		case P0:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (r_P =" + DoubleFormatter.format(ls.getrP()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case F0:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			p.drawLine(ls.getC(), position, g);

			g.drawString(
					getName() + " (f =" + DoubleFormatter.format(ls.getF()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case L0:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (phi0 =" + DoubleFormatter.format(ls.getPhi0()) + ", r = " + DoubleFormatter.format(ls.getR()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case L1:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			
			// p.drawLine(ls.getC(), position, g);
			p.drawCircle(
					ls.getC(),	// centre
					ls.getR(),	// radius
					g
				);

			g.drawString(
					getName() + " (n =" + ls.getN() + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		}
	}

	
	@Override
	public void mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		// super.mouseDragged(cc, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			// the mouse position
			Vector2D p = new Vector2D(rpp.i2x(mouseI), rpp.j2y(mouseJ));

			switch(pt)
			{
			case C:
				ls.setC(p);
				break;
			case P0:
				// dragging P0, which is confined to the line of lens 0...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ls.getLineOfLens0(), p));
				
				// ... changes rP
				ls.setrP(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ls.getC()),
								ls.getL0()
								)
						);
				break;
			case L0:
				// dragging L0 rotates the entire lens star lens
				position.setCoordinatesToThoseOf(p);
				
				// the new direction of lens 0
				Vector2D l0 = Vector2D.difference(position, ls.getC());
				
				// set the directions in the lens star accordingly
				ls.setPhi0(Math.atan2(l0.y, l0.x));
				
				// set the radius of the lens star accordingly
				ls.setR(l0.getLength());
				break;
			case L1:
				// dragging L1 changes the number of lenses
				position.setCoordinatesToThoseOf(p);
				
				// the new direction of lens 0
				Vector2D l1 = Vector2D.difference(position, ls.getC());
				
				// calculate the corresponding azimuthal direction
				double phi1 = Math.atan2(l1.y, l1.x);
				
				rpp.graphicElements.removeAll(ls.getLenses());
				ls.getOpticalComponents().removeAll(ls.getLenses());
				ls.getGraphicElements().removeAll(ls.getLenses());

				// set the number of lenses accordingly
				ls.setN((int)(2.*Math.PI / (phi1 - ls.getPhi0()) + 0.5));
				ls.createLenses();
				
				rpp.graphicElements.addAll(ls.getLenses());
				
				// set the radius of the lens star accordingly
				// ls.setR(l1.getLength());
				break;
			case F0:
				// dragging F0, which is confined to the optical axis of lens 0...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ls.getOpticalAxisOfLens0(), p));
				
				// ... changes f
				ls.setF(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ls.getP0()),
								ls.getL0().getPerpendicularVector()
								)
						);
			}
			
			ls.calculatePointParameters();
			ls.calculateLensParameters();
		}
	}

	private RayPlay2DPanel panelWithPopup;
	
	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		if(mouseNear && e.isPopupTrigger())
		{
			panelWithPopup = rpp;
			
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
		deleteLSMenuItem,
		turnIntoIndividualLensesMenuItem;

	private void initPopup()
	{
		deleteLSMenuItem = new JMenuItem("Delete lens star");
		deleteLSMenuItem.setEnabled(true);
		deleteLSMenuItem.getAccessibleContext().setAccessibleDescription("Delete lens star");
		deleteLSMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelWithPopup.graphicElements.removeAll(ls.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ls);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteLSMenuItem);
		
		// Separator
	    popup.addSeparator();

	    JMenuItem setRP0MenuItem = new JMenuItem("Set rP (distance of principal points from centre) to 0");
		setRP0MenuItem.setEnabled(true);
		setRP0MenuItem.getAccessibleContext().setAccessibleDescription("Set rP (distance of principal points from centre) to 0");
		setRP0MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ls.setrP(0);
				ls.calculatePointParameters();
				ls.calculateLensParameters();
				panelWithPopup.repaint();
			}
		});
		popup.add(setRP0MenuItem);
		
		// Separator
	    popup.addSeparator();

		turnIntoIndividualLensesMenuItem = new JMenuItem("Turn lens star into individual lenses");
		turnIntoIndividualLensesMenuItem.setEnabled(true);
		turnIntoIndividualLensesMenuItem.getAccessibleContext().setAccessibleDescription("Turn lens star into individual lenses");
		turnIntoIndividualLensesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the lens star
				panelWithPopup.graphicElements.removeAll(ls.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ls);
				
				// and add all the lenses
				for(int i=0; i<ls.getN(); i++)
				{
					Lens2DIOC lens = new Lens2DIOC(ls.getLens(i));
					panelWithPopup.iocs.add(lens);
					panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
				}
				
				panelWithPopup.repaint();
			}
		});
		popup.add(turnIntoIndividualLensesMenuItem);
	}
	
	private void updatePopup()
	{
		// enable/disable + text
	}	

}
