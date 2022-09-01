package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.interactiveOpticalComponents.IdeaLensWormhole2D;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point in an optical wormhole.
 * @author maik
 */
public class IdealLensWormholePointGE2D extends PointGE2D
{
	protected IdeaLensWormhole2D ilw;
	
	/**
	 * The point/vertex names.
	 * For nomenclature see Fig. 4 in [1]
	 * @author johannes
	 */
	public enum IdealLensWormholePointType
	{
		PO0("PO0", 5),
		PO1("PO1", 3),
		PO2("PO2", 3),
		PO3("PO3", 3),
		VO1("VO1", 3),
		VO2("VO2", 3),
		FOD("Focal point of lens DO", 4),
		PI0("PI0", 5),
		PI1("PI1", 3),
		PI2("PI2", 3),
		PI3("PI3", 3),
		VI1("VI1", 3),
		VI2("VI2", 3),
		FID("Focal point of lens DI", 4),
		IMGP("position of the first(closest) image lens",3),
		IMGF1("focal length of first image lens",4),
		IMGF2("focal length of second image lens",4);

		public final String name;
		public final int radius;

		IdealLensWormholePointType(String name, int radius)
		{
			this.name = name;
			this.radius = radius;
		}
	}

	protected IdealLensWormholePointType pt;
	
	public IdealLensWormholePointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, IdeaLensWormhole2D ilw, IdealLensWormholePointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.ilw = ilw;
		this.pt = pt;
		
		// initPopup();
	}

	public IdealLensWormholePointGE2D(String name, Vector2D position, IdeaLensWormhole2D ol, IdealLensWormholePointType pt)
	{
		this(name, position, pt.radius, new BasicStroke(1), Color.gray, true, ol, pt);
	}

	public IdealLensWormholePointGE2D(String name, IdeaLensWormhole2D ol, IdealLensWormholePointType pt)
	{
		this(name, new Vector2D(0, 0), 3, new BasicStroke(1), Color.gray, true, ol, pt);
	}

	
	// getters & setters
	
	public IdeaLensWormhole2D getILW() {
		return ilw;
	}

	public void setILW(IdeaLensWormhole2D ilw) {
		this.ilw = ilw;
	}

	public IdealLensWormholePointType getPt() {
		return pt;
	}

	public void setPt(IdealLensWormholePointType pt) {
		this.pt = pt;
	}

	
	
	// GraphicElement2D methods
	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		//outer cloak
		case PO0:
			super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
			break;
		case PO1:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h1 outer =" + DoubleFormatter.format(ilw.getH1O()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case PO2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h2 outer =" + DoubleFormatter.format(ilw.getH2O()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case PO3:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h outer =" + DoubleFormatter.format(ilw.gethO()) + ", " +
					"(x,y)=("+DoubleFormatter.format(position.x)+", "+DoubleFormatter.format(position.y)+"))", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case VO1:
		case VO2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (rD outer =" + DoubleFormatter.format(ilw.getrDO()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case FOD:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (f_D outer =" + DoubleFormatter.format(ilw.getfDO()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			
			//inner cloak
			
		case PI0:
			super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
			break;
		case PI1:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h1 inner =" + DoubleFormatter.format(ilw.getH1I()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case PI2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h2 inner =" + DoubleFormatter.format(ilw.getH2I()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case PI3:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (h inner =" + DoubleFormatter.format(ilw.gethI()) + ", " +
					"(x,y)=("+DoubleFormatter.format(position.x)+", "+DoubleFormatter.format(position.y)+"))", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case VI1:
		case VI2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (rD inner =" + DoubleFormatter.format(ilw.getrDI()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case FID:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (f_D inner =" + DoubleFormatter.format(ilw.getfDI()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case IMGP:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (lens image position =[" + DoubleFormatter.format(ilw.getPImg().x) + ","+DoubleFormatter.format(ilw.getPImg().y)+"])", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			//TODO
			break;
		case IMGF1:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (first image lens f =" + DoubleFormatter.format(ilw.getF1()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		case IMGF2:
			g.setColor(Color.GRAY);
			g.drawString(
					getName() + " (second image lens f =" + DoubleFormatter.format(ilw.getF2()) + ")", 
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
		}
		
	}

	
	@Override
	public boolean mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		// super.mouseDragged(cc, mouseNear, mouseI, mouseJ);
		if(mouseNear)
		{
			// the mouse position
			Vector2D p = new Vector2D(rpp.i2x(mouseI), rpp.j2y(mouseJ));
			Vector2D cI, c; //the direction between the base centre and top vertex.
			double scaleFactorI, scaleFactor; //scale factor to be applied to each cloak individually
			double sep; //separation between the base centre of inner and outer cloak

			switch(pt)
			{
			case PO0:
				ilw.setpDO(p);
				break;
			case PO1:
				// dragging P1, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes h1
				ilw.setH1O(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDO()),
										ilw.getcHat()
										)
								));
				break;
			case PO2:
				// dragging P2, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes h2
				ilw.setH2O(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDO()),
										ilw.getcHat()
										)
								));
				break;
			case PO3:
				// dragging P3 rotates both the inner and outer lens and scales the outer omnidirectional lens
				position.setCoordinatesToThoseOf(p);
				
				// the new direction of the central symmetry axis
				c = Vector2D.difference(position, ilw.getpDO());
				
				// set the directions in the omnidirectional lens accordingly
				ilw.setcHat(c.getNormalised());
				ilw.setdHat(c.getPerpendicularVector().getNormalised());
				
				// do the scaling
				scaleFactor = c.getLength()/ilw.gethO();
				scaleFactorI = 1;
				ilw.sethO(scaleFactor*ilw.gethO());
				ilw.setH1O(scaleFactor*ilw.getH1O());
				ilw.setH2O(scaleFactor*ilw.getH2O());
				ilw.setfDO(scaleFactor*ilw.getfDO());
				ilw.setrDO(scaleFactor*ilw.getrDO());
				
				sep = Vector2D.distance(ilw.getpDO(), ilw.getpDI());
				ilw.setpDI( Vector2D.sum(ilw.getpDO(),ilw.getcHat().getWithLength(sep)));	

				break;
			case VO1:
			case VO2:
				// dragging V1 or V2, which are confined to the line of lens D...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getLineOfLensDO(), p));
				
				// ... changes rD
				ilw.setrDO(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDO()),
										ilw.getdHat()
										)
								));
				break;
			case FOD:
				// dragging FD, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes fD
				ilw.setfDO(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ilw.getpDO()),
								ilw.getcHat()
								)
						);
				break;
				
				//inner
			case PI0:
				ilw.setpDI(p);
				//TODO change to only move up down middle of outer cloak maybe...
				break;
			case PI1:
				// dragging P1, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes h1
				ilw.setH1I(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDI()),
										ilw.getcHat()
										)
								));
				break;
			case PI2:
				// dragging P2, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes h2
				ilw.setH2I(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDI()),
										ilw.getcHat()
										)
								));
				break;
			case PI3:
				// dragging P3 rotates both the inner and outer lens and scales the outer omnidirectional lens
				position.setCoordinatesToThoseOf(p);
				
				// the new direction of the central symmetry axis
				cI = Vector2D.difference(p, ilw.getpDI());
				
				// set the directions in the wormhole accordingly
				ilw.setcHat(cI.getNormalised());
				ilw.setdHat(cI.getPerpendicularVector().getNormalised());
				
				// do the scaling
				scaleFactorI = cI.getLength()/ilw.gethI();
				scaleFactor = 1;
				ilw.sethI(scaleFactorI*ilw.gethI());
				ilw.setH1I(scaleFactorI*ilw.getH1I());
				ilw.setH2I(scaleFactorI*ilw.getH2I());
				ilw.setfDI(scaleFactorI*ilw.getfDI());
				ilw.setrDI(scaleFactorI*ilw.getrDI());
				
				sep = Vector2D.distance(ilw.getpDO(), ilw.getpDI());
				ilw.setpDI( Vector2D.sum(ilw.getpDO(),ilw.getcHat().getWithLength(sep)));	
				
				break;
			case VI1:
			case VI2:
				// dragging V1 or V2, which are confined to the line of lens D...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getLineOfLensDI(), p));
				
				// ... changes rD
				ilw.setrDI(
						Math.abs(
								Vector2D.scalarProduct(
										Vector2D.difference(position, ilw.getpDI()),
										ilw.getdHat()
										)
								));
				break;
			case FID:
				// dragging FD, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(ilw.getCentralSymmetryAxis(), p));
				
				// ... changes fD
				ilw.setfDI(
						Vector2D.scalarProduct(
								Vector2D.difference(position, ilw.getpDI()),
								ilw.getcHat()
								)
						);
				break;
			case IMGP:
				//set the position of the first image lens, all subsequent lenses will be aligned the central axis. 
				ilw.setPImg(p);
				break;
			case IMGF1:
				Vector2D pF1 = Vector2D.sum(ilw.getPImg(), ilw.getdHat().getProductWith(-0.1*ilw.getrDI()));
				Line2D lF1 = new Line2D(pF1,Vector2D.sum(pF1,ilw.getcHat()));
				// dragging the focal point, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lF1, p));
				
				// ... changes the image focal length
				ilw.setF1(
						Vector2D.scalarProduct(
								Vector2D.difference(position, pF1),
								ilw.getcHat()
								)
						);
				// and also changes the position of the image "lenses"
				break;
			case IMGF2:
				Vector2D pF2 = Vector2D.sum(ilw.getPImg(), ilw.getdHat().getProductWith(0.1*ilw.getrDI()));
				Line2D lF2 = new Line2D(pF2,Vector2D.sum(pF2,ilw.getcHat()));
				// dragging the focal point, which is confined to the central symmetry axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lF2, p));
				
				// ... changes the image focal length of the second lens
				ilw.setF2(
						Vector2D.scalarProduct(
								Vector2D.difference(position, pF2),
								ilw.getcHat()
								)
						);
				// and also changes the position of the image "lenses"
				
				break;

			}
			
			ilw.calculateInternalParameters();
		}
		
		return mouseNear;
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
////	JMenuItem
////		deleteOLMenuItem,
////		turnIntoIndividualLensesMenuItem;
//
//	private void initPopup()
//	{
//		JMenuItem deleteOLMenuItem = new JMenuItem("Delete omnidirectional lens");
//		deleteOLMenuItem.setEnabled(true);
//		deleteOLMenuItem.getAccessibleContext().setAccessibleDescription("Delete omnidirectional lens");
//		deleteOLMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// panelWithPopup.graphicElements.removeAll(ilw.getGraphicElements());
//				// panelWithPopup.opticalComponents.removeAll(ilw.getOpticalComponents());
//				panelWithPopup.iocs.remove(ol);
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(deleteOLMenuItem);
//		
//		// Separator
//	    popup.addSeparator();
//
//	    JMenuItem turnIntoIndividualLensesMenuItem = new JMenuItem("Turn omnidirectional lens into individual lenses");
//		turnIntoIndividualLensesMenuItem.setEnabled(true);
//		turnIntoIndividualLensesMenuItem.getAccessibleContext().setAccessibleDescription("Turn omnidirectional lens into individual lenses");
//		turnIntoIndividualLensesMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				// remove the omnidirectional lens
//				// panelWithPopup.graphicElements.removeAll(ilw.getGraphicElements());
//				// panelWithPopup.opticalComponents.removeAll(ilw.getOpticalComponents());
//				panelWithPopup.iocs.remove(ol);
//				
//				// and add all the lenses
//				for(OmnidirectionalLensLensType olLensType:OmnidirectionalLensLensType.values())
//				{
//					Lens2DIOC lens = new Lens2DIOC(ilw.getLens(olLensType));
//					panelWithPopup.iocs.add(lens);
//					// panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
//				}
//				
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(turnIntoIndividualLensesMenuItem);
//
//		// Separator
//	    popup.addSeparator();
//
//	    JMenuItem showC1ItemsMenuItem = new JMenuItem("Show C_2a point and line");
//	    showC1ItemsMenuItem.setEnabled(true);
//	    showC1ItemsMenuItem.getAccessibleContext().setAccessibleDescription("Show C_2a point and line");
//	    showC1ItemsMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				ilw.setShowC1PointAndLine(!ilw.isShowC1PointAndLine());
//
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(showC1ItemsMenuItem);
//		
//	    JMenuItem addRaySourceMenuItem = new JMenuItem("Add ray source at C_2a point and constrained to C_2a line");
//	    addRaySourceMenuItem.setEnabled(true);
//	    addRaySourceMenuItem.getAccessibleContext().setAccessibleDescription("Add ray source at C1 point and constrained to the omnidirectional lens's C_2a line");
//	    addRaySourceMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
////				// find intersection of focal plane of lens C1 in cell 2a with the horizontal line through P1
////				// works only if f of lens C1 is > 0
////				Vector2D focalPointC1 = ilw.getLens(OmnidirectionalLensLensType.C1).getFocalPoint();
////				Vector2D directionC1 = ilw.getLens(OmnidirectionalLensLensType.C1).getDirection();
////				double alpha1 = Geometry2D.getAlpha1ForLineLineIntersection2D(
////						focalPointC1,	// a1, i.e. point on line 1
////						directionC1,	// d1, i.e. direction of line 1
////						ilw.getLens(OmnidirectionalLensLensType.C1).getPrincipalPoint(),	// a2, i.e. point on line 2
////						ilw.getLens(OmnidirectionalLensLensType.D).getDirection()	// d2, i.e. direction of line 2
////					);
////				Vector2D cPoint = Vector2D.sum(focalPointC1, directionC1.getProductWith(alpha1));
//				PointRaySource2D ls = new PointRaySource2D(
//						"Point ray source constrained to C_2a line",	// name
//						new Vector2D(ilw.getPoint(OmnidirectionalLensPointType.C1).getPosition()),	// raysStartPoint
//						MyMath.deg2rad(0), // centralRayAngle
//						false,	// forwardRaysOnly
//						true, // rayBundle
//						true,	// rayBundleIsotropic
//						MyMath.deg2rad(30), // rayBundleAngle
//						64,	// rayBundleNoOfRays
//						Colour.GREEN,
//						255	// maxTraceLevel
//						);
//				// set the line constraining the source position
//				ls.setLineConstrainingStartPoint(
//						ilw.getC1Line()
//						// new Line2D(cPoint, ilw.getPoint(OmnidirectionalLensPointType.V1).getPosition())
//					);
//				panelWithPopup.iocs.add(ls);
//				// panelWithPopup.graphicElements.addAll(ls.getGraphicElements());
//
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(addRaySourceMenuItem);
//	}
//	
//	private void updatePopup()
//	{
//		// enable/disable + text
//	}	

}
