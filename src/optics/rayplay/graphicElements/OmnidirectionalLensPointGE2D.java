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

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OmnidirectionalLensLensType;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point in an omnidirectional lens.
 * @author johannes
 */
public class OmnidirectionalLensPointGE2D extends PointGE2D
{
	protected OmnidirectionalLens2D ol;
	
	/**
	 * The point/vertex names.
	 * For nomenclature see Fig. 4 in [1]
	 * @author johannes
	 */
	public enum OmnidirectionalLensPointType
	{
		P0("P0"),
		P1("P1"),
		P2("P2"),
		P3("P3"),
		V1("V1"),
		V2("V2"),
		FD("Focal point of lens D");

		public final String name;

		OmnidirectionalLensPointType(String name) {this.name = name;}
	}

	protected OmnidirectionalLensPointType pt;
	
	public OmnidirectionalLensPointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, OmnidirectionalLens2D ol, OmnidirectionalLensPointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ol = ol;
		this.pt = pt;
		
		initPopup();
	}

	public OmnidirectionalLensPointGE2D(String name, Vector2D position, OmnidirectionalLens2D ol, OmnidirectionalLensPointType pt)
	{
		this(name, position, 3, new BasicStroke(1), Color.gray, true, ol, pt);
		
		switch(pt)
		{
		case P0:
			setRadius(5);
			break;
		case FD:
			setRadius(4);
			break;
		default:
			setRadius(3);
		}
	}

	public OmnidirectionalLensPointGE2D(String name, OmnidirectionalLens2D ol, OmnidirectionalLensPointType pt)
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

	public OmnidirectionalLensPointType getPt() {
		return pt;
	}

	public void setPt(OmnidirectionalLensPointType pt) {
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
					getName() + " (h =" + DoubleFormatter.format(ol.getH()) + ", " +
					"(x,y)=("+DoubleFormatter.format(position.x)+", "+DoubleFormatter.format(position.y)+"))", 
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
	public void mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		// super.mouseDragged(cc, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			// the mouse position
			Vector2D p = new Vector2D(rpp.i2x(mouseI), rpp.j2y(mouseJ));

			switch(pt)
			{
			case P0:
				ol.setpD(p);
				break;
			case P1:
				// dragging P1, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ol.getCentralSymmetryAxis(), p));
				
				// ... changes h1
				ol.setH1(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										ol.getcHat()
										)
								));
				break;
			case P2:
				// dragging P2, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ol.getCentralSymmetryAxis(), p));
				
				// ... changes h2
				ol.setH2(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										ol.getcHat()
										)
								));
				break;
			case P3:
				// dragging P3 rotates and scales the entire omnidirectional lens
				position.setCoordinatesToThoseOf(p);
				
				// the new direction of the central symmetry axis
				Vector2D c = Vector2D.difference(position, ol.getpD());
				
				// set the directions in the omnidirectional lens accordingly
				ol.setcHat(c);
				ol.setdHat(c.getPerpendicularVector());
				
				// do the scaling
				double scaleFactor = c.getLength()/ol.getH();
				ol.setH(scaleFactor*ol.getH());
				ol.setH1(scaleFactor*ol.getH1());
				ol.setH2(scaleFactor*ol.getH2());
				ol.setfD(scaleFactor*ol.getfD());
				ol.setrD(scaleFactor*ol.getrD());
				break;
			case V1:
			case V2:
				// dragging V1 or V2, which are confined to the line of lens D...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ol.getLineOfLensD(), p));
				
				// ... changes rD
				ol.setrD(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ol.getpD()),
										ol.getdHat()
										)
								));
				break;
			case FD:
				// dragging FD, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ol.getCentralSymmetryAxis(), p));
				
				// ... changes fD
				ol.setfD(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ol.getpD()),
								ol.getcHat()
								)
						);
			}
			
			ol.calculatePointParameters();
			ol.calculateLensParameters();
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
//	JMenuItem
//		deleteOLMenuItem,
//		turnIntoIndividualLensesMenuItem;

	private void initPopup()
	{
		JMenuItem deleteOLMenuItem = new JMenuItem("Delete omnidirectional lens");
		deleteOLMenuItem.setEnabled(true);
		deleteOLMenuItem.getAccessibleContext().setAccessibleDescription("Delete omnidirectional lens");
		deleteOLMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ol);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteOLMenuItem);
		
		// Separator
	    popup.addSeparator();

	    JMenuItem turnIntoIndividualLensesMenuItem = new JMenuItem("Turn omnidirectional lens into individual lenses");
		turnIntoIndividualLensesMenuItem.setEnabled(true);
		turnIntoIndividualLensesMenuItem.getAccessibleContext().setAccessibleDescription("Turn omnidirectional lens into individual lenses");
		turnIntoIndividualLensesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the omnidirectional lens
				panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ol);
				
				// and add all the lenses
				for(OmnidirectionalLensLensType olLensType:OmnidirectionalLensLensType.values())
				{
					Lens2DIOC lens = new Lens2DIOC(ol.getLens(olLensType));
					panelWithPopup.iocs.add(lens);
					panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
				}
				
				panelWithPopup.repaint();
			}
		});
		popup.add(turnIntoIndividualLensesMenuItem);

		// Separator
	    popup.addSeparator();

	    JMenuItem addRaySourceMenuItem = new JMenuItem("Add ray source constrained to C line");
	    addRaySourceMenuItem.setEnabled(true);
	    addRaySourceMenuItem.getAccessibleContext().setAccessibleDescription("Add ray source constrained to the omnidirectional lens's C line");
	    addRaySourceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// find intersection of focal plane of lens C1 in cell 2a with the horizontal line through P1
				// works only if f of lens C1 is > 0
				Vector2D focalPointC1 = ol.getLens(OmnidirectionalLensLensType.C1).getFocalPoint();
				Vector2D directionC1 = ol.getLens(OmnidirectionalLensLensType.C1).getDirection();
				double alpha1 = Geometry2D.getAlpha1ForLineLineIntersection2D(
						focalPointC1,	// a1, i.e. point on line 1
						directionC1,	// d1, i.e. direction of line 1
						ol.getLens(OmnidirectionalLensLensType.C1).getPrincipalPoint(),	// a2, i.e. point on line 2
						ol.getLens(OmnidirectionalLensLensType.D).getDirection()	// d2, i.e. direction of line 2
					);
				Vector2D cPoint = Vector2D.sum(focalPointC1, directionC1.getProductWith(alpha1));
				PointRaySource2D ls = new PointRaySource2D(
						"Point ray source constrained to C line",	// name
						cPoint,	// raysStartPoint
						MyMath.deg2rad(0), // centralRayAngle
						false,	// forwardRaysOnly
						true, // rayBundle
						true,	// rayBundleIsotropic
						MyMath.deg2rad(30), // rayBundleAngle
						64,	// rayBundleNoOfRays
						Colour.GREEN,
						255	// maxTraceLevel
						);
				// set the line constraining the source position
				ls.setLineConstrainingStartPoint(new Line2D(cPoint, ol.getPoint(OmnidirectionalLensPointType.V1).getPosition()));
				panelWithPopup.lss.add(ls);
				panelWithPopup.graphicElements.addAll(ls.getGraphicElements());

				panelWithPopup.repaint();
			}
		});
		popup.add(addRaySourceMenuItem);
}
	
	private void updatePopup()
	{
		// enable/disable + text
	}	

}
