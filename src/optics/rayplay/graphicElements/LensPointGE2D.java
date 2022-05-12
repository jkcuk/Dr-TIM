package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point graphic element that controls the parameters of an lens interactive component
 * @author johannes
 */
public class LensPointGE2D extends PointGE2D
{
	protected Lens2DIOC lens;
	
	public enum LensPointType
	{
		P("Principal point"),
		F("Focal point"),
		L("Point on lens"),
		E1("Endpoint 1"),
		E2("Endpoint 2");

		public final String name;

		LensPointType(String name) {this.name = name;}
	}

	protected LensPointType pt;
	
	public LensPointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, Lens2DIOC lens, LensPointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.lens = lens;
		this.pt = pt;
		
		// initPopup();
	}

	public LensPointGE2D(String name, Vector2D position, Lens2DIOC lens, LensPointType pt)
	{
		this(name, position, 3, new BasicStroke(1), Color.gray, true, lens, pt);
	}

	public LensPointGE2D(String name, Lens2DIOC lens, LensPointType pt)
	{
		this(name, new Vector2D(0, 0), 3, new BasicStroke(1), Color.gray, true, lens, pt);
		
		switch(pt)
		{
		case P:
			setRadius(5);
			break;
		case F:
			setRadius(4);
			break;
		default:
			setRadius(3);
		}
	}

	
	// getters & setters
	
	public Lens2DIOC getLens() {
		return lens;
	}

	public void setLens(Lens2DIOC lens) {
		this.lens = lens;
	}

	public LensPointType getPt() {
		return pt;
	}

	public void setPt(LensPointType pt) {
		this.pt = pt;
	}	

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawInFront(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
			// calculate the position
			if(pt == LensPointType.L)
			{
				position.setCoordinatesToThoseOf(Vector2D.sum(lens.getPrincipalPoint(), lens.getDirection().getWithLength(p.getGoodDistanceXY())));
			}

			super.drawInFront(p,  g,  mouseNear, mouseI, mouseJ);
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case F:
			g.setColor(Color.GRAY);
			
			// draw part of the optical axis
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			p.drawLine(lens.getPrincipalPoint(), position, g);

			g.drawString(
					getName() + " (f =" + DoubleFormatter.format(lens.getFocalLength()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case L:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			
			p.drawLine(lens.getPrincipalPoint(), position, g);
			p.drawCircle(
					lens.getPrincipalPoint(),	// centre
					p.getGoodDistanceXY(),	// radius
					g
				);
			
			// give some info
			g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(Math.atan2(lens.getDirection().y, lens.getDirection().x)))+" degrees",
					p.x2i(position.x)+10, p.y2j(position.y)+5);
			break;
		default:
			super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
		}
	}


	@Override
	public boolean mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
//		super.mouseDragged(c, mouseNear, mouseI, mouseJ);
		
		if(mouseNear)
		{
			Vector2D p = new Vector2D(rpp.i2x(mouseI), rpp.j2y(mouseJ));

			switch(pt)
			{
			case P:
				// dragging the principal point moves the entire lens
				
				// calculate the shift...
				Vector2D delta = Vector2D.difference(p, lens.getPrincipalPoint());
				
				// ... and add this to the end points
				lens.setEndPoints(
						Vector2D.sum(lens.getA(), delta),
						Vector2D.sum(lens.getB(), delta)
					);
				
				// finally, set the principal point to its new position
				lens.setPrincipalPoint(p);
				break;
			case L:
				// dragging the point on the rotates the lens around the principal point
				
				// create a coordinate system centred on the principal point
								
				// rotation angle
				double dPhi = 
						Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(p, lens.getPrincipalPoint()))	// azimuthal coordinate of new position
						- Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(position, lens.getPrincipalPoint()));	// azimuthal coordinate of old position
				
				// rotate the end points
				lens.setA(Geometry2D.rotateAroundPoint(lens.getA(), dPhi, lens.getPrincipalPoint()));
				lens.setB(Geometry2D.rotateAroundPoint(lens.getB(), dPhi, lens.getPrincipalPoint()));
				break;
			case F:
				// dragging the focal point, which is confined to the optical axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lens.getOpticalAxis(), p));
				
				// ... changes the focal length
				lens.setFocalLength(
						Vector2D.scalarProduct(
								Vector2D.difference(position, lens.getPrincipalPoint()),
								lens.getOpticalAxis().getDirection()
								)
						);
				break;
			case E1:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lens, p));
				lens.setA(position);
				break;
			case E2:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lens, p));
				lens.setB(position);
				break;
			}
			
			lens.calculatePointParameters();
			
			return true;
		}
		return false;
	}

	
//	private RayPlay2DPanel panelWithPopup;
//	
//	@Override
//	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
//	{
//		if(mouseNear && e.isPopupTrigger())
//		{
//			panelWithPopup = rpp;
//			
//			updatePopup();
//
//			popup.show(e.getComponent(), e.getX(), e.getY());
//			
//			// say that the event has been handled
//			return true;
//		}
//		return false;
//	}
//
//
//	// 
//	// popup menu
//	// 
//	
//	final JPopupMenu popup = new JPopupMenu();
//	
//	// menu items
//	JMenuItem
//		deleteLensMenuItem;
//
//	private void initPopup()
//	{
//		deleteLensMenuItem = new JMenuItem("Delete lens");
//		deleteLensMenuItem.setEnabled(true);
//		deleteLensMenuItem.getAccessibleContext().setAccessibleDescription("Delete lens");
//		deleteLensMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// panelWithPopup.graphicElements.removeAll(lens.getGraphicElements());
//				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
//				panelWithPopup.iocs.remove(lens);
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(deleteLensMenuItem);
//	}
//	
//	private void updatePopup()
//	{
//		// enable/disable + text
//	}	
//
}
