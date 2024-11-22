package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.interactiveOpticalComponents.LensTelescope2D;
import optics.rayplay.util.DoubleFormatter;

/**
 * A point graphic element that controls the parameters of a lens telescope interactive component
 * @author maik
 */
public class LensTelescopePointGE2D extends PointGE2D
{
	protected LensTelescope2D lensTelescope;
	
	public enum LensTelescopePointType
	{
		PL1("Principal point lens1",5),
		FL1("Focal point lens 1",3),
//		LL1("Point on lens 1",4), //TODO this does not currently work but we can actually make the lenses do what we want already by normally rotating it and then shifting the principal points.
		E1L1("Endpoint lens 1",3),
		E2L1("Endpoint lens 1",3),
		PL2("Principal point lens 2",5),
		FL2("Focal point lens 2",3),
		E1L2("Endpoint lens 2",3),
		E2L2("Endpoint lens 2",3),
		POT("Point on telescope",4);
		
		public final String name;
		public final int radius;

		LensTelescopePointType(String name, int radius)
		{
			this.name = name;
			this.radius = radius;
		}
	
	}

	protected LensTelescopePointType pt;
	
	public LensTelescopePointGE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive, LensTelescope2D lensTelescope, LensTelescopePointType pt)
	{
		super(name, position, radius, stroke, color, interactive);
		
		this.lensTelescope = lensTelescope;
		this.pt = pt;
		
		// initPopup();
	}

	public LensTelescopePointGE2D(String name, Vector2D position, LensTelescope2D lensTelescope, LensTelescopePointType pt)
	{
		this(name, position, 3, new BasicStroke(1), Color.gray, true, lensTelescope, pt);
	}

	public LensTelescopePointGE2D(String name, LensTelescope2D lensTelescope, LensTelescopePointType pt)
	{
		this(name, new Vector2D(0, 0), 3, new BasicStroke(1), Color.gray, true, lensTelescope, pt);
		
		switch(pt)
		{
		case PL1:
			setRadius(5);
			break;
		case PL2:
			setRadius(5);
			break;
		case FL1:
			setRadius(4);
			break;
		case FL2:
			setRadius(4);
			break;
		default:
			setRadius(3);
		}
	}

	
	// getters & setters
	
	public LensTelescope2D getLensTelescope() {
		return lensTelescope;
	}

	public void setLensTelescope(LensTelescope2D lensTelescope) {
		this.lensTelescope = lensTelescope;
	}

	public LensTelescopePointType getPt() {
		return pt;
	}

	public void setPt(LensTelescopePointType pt) {
		this.pt = pt;
	}

	
	
	// GraphicElement2D methods
	
	@Override
	public void drawInFront(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
			
//			if(pt == LensTelescopePointType.LL1) //TODO this does not currently work but we can actually make the lenses do what we want already by normally rotating it and then shifting the principal points.
//			{
//				position.setCoordinatesToThoseOf(Vector2D.sum(lensTelescope.getPrincipalPointL1(), lensTelescope.getABNormalisedL1().getWithLength(p.getGoodDistanceXY())));
//			}
			if(pt == LensTelescopePointType.POT)
			{
				Vector2D centre = Vector2D.sum(lensTelescope.getPrincipalPointL1(),lensTelescope.getPrincipalPointL2()).getProductWith(0.5);
				position.setCoordinatesToThoseOf(Vector2D.sum(centre, lensTelescope.getABNormalisedL1().getWithLength(p.getGoodDistanceXY())));
			}

			super.drawInFront(p,  g,  mouseNear, mouseI, mouseJ);
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(pt)
		{
		case PL1:
			g.setColor(Color.GRAY);
			
			// draw part of the optical axis
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			p.drawLine(lensTelescope.getPrincipalPointL1(), position, g);

			g.drawString(
					getName() + " (f1 =" + DoubleFormatter.format(lensTelescope.getFocalLengthL1()) + "test)", //TODO remove test
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
			
		case PL2:
			g.setColor(Color.GRAY);
			
			// draw part of the optical axis
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			p.drawLine(lensTelescope.getPrincipalPointL2(), position, g);

			g.drawString(
					getName() + " (f2 =" + DoubleFormatter.format(lensTelescope.getFocalLengthL2()) + "test)", //TODO remove test
					mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			break;
			
//		case LL1: //TODO this does not currently work but we can actually make the lenses do what we want already by normally rotating it and then shifting the principal points.
//			g.setColor(Color.GRAY);
//			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
//			
//			p.drawLine(lensTelescope.getPrincipalPointL1(), position, g);
//			p.drawCircle(
//					lensTelescope.getPrincipalPointL1(),	// centre
//					p.getGoodDistanceXY(),	// radius
//					g
//				);
//			
//			// give some info
//			g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(Math.atan2(lensTelescope.getABNormalisedL1().y, lensTelescope.getABNormalisedL1().x)))+" degrees",
//					p.x2i(position.x)+10, p.y2j(position.y)+5);
//			break;
		
		case POT:
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));	// dashed
			
			Vector2D toCentre = Vector2D.difference(lensTelescope.getPrincipalPointL2(), lensTelescope.getPrincipalPointL1()).getProductWith(0.5); //This is at the half way point now... surely we can make it a bit nicer but I'm not too sure how yet.
			p.drawLine(Vector2D.sum(lensTelescope.getPrincipalPointL1(),toCentre), position, g);
			p.drawCircle(
					Vector2D.sum(lensTelescope.getPrincipalPointL1(),toCentre),	// centre
					p.getGoodDistanceXY(),	// radius
					g
				);
			
			// give some info TODO adjust this if needed to make it a bit more informative 
			
			g.drawString("Angle with horizontal = "+DoubleFormatter.format(MyMath.rad2deg(Math.atan2(lensTelescope.getABNormalisedL1().y, lensTelescope.getABNormalisedL1().x)))+" degrees testt",
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
			case PL1:
				// dragging the first principal point moves the entire lens telescope
				
				// calculate the shift...
				Vector2D delta = Vector2D.difference(p, lensTelescope.getPrincipalPointL1());
				
				// ... and add this to the end points TODO does this getting and setting work?? I hope so...
				lensTelescope.setEndPoint1L1(
						Vector2D.sum(lensTelescope.getEndPoint1L1(), delta)
					);
				
				lensTelescope.setEndPoint2L1(
						Vector2D.sum(lensTelescope.getEndPoint2L1(), delta)
					);
				
				lensTelescope.setEndPoint1L2(
						Vector2D.sum(lensTelescope.getEndPoint1L2(), delta)
					);
				
				lensTelescope.setEndPoint2L2(
						Vector2D.sum(lensTelescope.getEndPoint2L2(), delta)
					);

				
				// finally, set the principal point to its new position
				lensTelescope.setPrincipalPointL1(p);
				lensTelescope.setPrincipalPointL2(Vector2D.sum(lensTelescope.getPrincipalPointL2(),delta));
				break;
//			case LL1: //TODO this does not currently work but we can actually make the lenses do what we want already by normally rotating it and then shifting the principal points.
//				// dragging the point on the rotates the lens around the principal point of lens 1. 
//				//This essentially works as the pitch of the telescope
//				// create a coordinate system centred on the principal point
//				// rotation angle
//				double dPhi = 
//						Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(p, lensTelescope.getPrincipalPointL1()))	// azimuthal coordinate of new position
//						- Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(position, lensTelescope.getPrincipalPointL1()));	// azimuthal coordinate of old position
//				
//				// rotate the end points for both lenses about their respective centre
//				lensTelescope.setEndPoint1L1(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint1L1(), dPhi, lensTelescope.getPrincipalPointL1()));
//				lensTelescope.setEndPoint2L1(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint2L1(), dPhi, lensTelescope.getPrincipalPointL1())); 
//				
//				lensTelescope.setEndPoint1L2(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint1L2(), dPhi, lensTelescope.getPrincipalPointL2()));
//				lensTelescope.setEndPoint2L2(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint2L2(), dPhi, lensTelescope.getPrincipalPointL2())); 
//				
//				break;
			case FL1:
				// dragging the focal point, which is confined to the optical axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getOpticalAxisL1(), p));
				
				double f1Before  = lensTelescope.getFocalLengthL1();
				
				// ... changes the focal length
				lensTelescope.setFocalLengthL1(
						Vector2D.scalarProduct(
								Vector2D.difference(position, lensTelescope.getPrincipalPointL1()),
								lensTelescope.getL1Normal()
								)
						);
				double deltaF1 = f1Before-lensTelescope.getFocalLengthL1(); //TODO check right way around... either here or two lines down.
				Vector2D principalPointShiftL2 = lensTelescope.getL2Normal().getProductWith(deltaF1);
				lensTelescope.setPrincipalPointL2(Vector2D.sum(lensTelescope.getPrincipalPointL2(),principalPointShiftL2));
				
				//and also apply this to the end points of course.
				lensTelescope.setEndPoint1L2(Vector2D.sum(lensTelescope.getEndPoint1L2(),principalPointShiftL2));
				lensTelescope.setEndPoint2L2(Vector2D.sum(lensTelescope.getEndPoint2L2(),principalPointShiftL2));
				break;
			case E1L1:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getABLineL1(),
						p));
				lensTelescope.setEndPoint1L1(position);
				break;
			case E2L1:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getABLineL1(),
						p));
				lensTelescope.setEndPoint2L1(position);
				break;
			case PL2:
				// dragging the second principal point moves it along lens 2 either up or down.
				
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getABLineL2(),
						p));
				
				lensTelescope.setPrincipalPointL2(position);
				break;
			case FL2:
				// dragging the focal point, which is confined to the optical axis...
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getOpticalAxisL2(), p));
				
				
				double f2Before  = lensTelescope.getFocalLengthL2();
				// ... changes the focal length
				lensTelescope.setFocalLengthL2(
						Vector2D.scalarProduct(
								Vector2D.difference(position, lensTelescope.getPrincipalPointL2()),
								lensTelescope.getL2Normal()
								)
						);
				
				//And now if we do this we also move lens 1 to keep it telescopic. 
				double deltaF2 = lensTelescope.getFocalLengthL2()-f2Before; //TODO check right way around... either here or two lines down.
				Vector2D principalPointShiftL1 = lensTelescope.getL1Normal().getProductWith(deltaF2);
				lensTelescope.setPrincipalPointL1(Vector2D.sum(lensTelescope.getPrincipalPointL1(),principalPointShiftL1));
				
				//and also apply this to the end points of course.
				lensTelescope.setEndPoint1L1(Vector2D.sum(lensTelescope.getEndPoint1L1(),principalPointShiftL1));
				lensTelescope.setEndPoint2L1(Vector2D.sum(lensTelescope.getEndPoint2L1(),principalPointShiftL1));
				break;
			case E1L2:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getABLineL2(),
						p));
				lensTelescope.setEndPoint1L2(position);
				break;
			case E2L2:
				// dragging the end points moves the end points on the line through the principal point and the point on the lens
				position.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lensTelescope.getABLineL2(),
						p));
				lensTelescope.setEndPoint2L2(position);
				break;
			case POT:
				// dragging the point rotates the whole telescope around the common centre point, 
				//a point half way down the line connecting the principal points.
								
				Vector2D centre = Vector2D.sum(lensTelescope.getPrincipalPointL2(), lensTelescope.getPrincipalPointL1()).getProductWith(0.5);//This should be equivalent to the centre calculated above otherwise another TODO
				
				// rotation angle
				double dPhiTelescope = 
						Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(p, centre))	// azimuthal coordinate of new position
						- Geometry2D.calculateAzimuthalCoordinate(Vector2D.difference(position, centre));	// azimuthal coordinate of old position
				
				// rotate the whole system about this centre and pray that it all works
				lensTelescope.setPrincipalPointL1(Geometry2D.rotateAroundPoint(lensTelescope.getPrincipalPointL1(), dPhiTelescope, centre));
				lensTelescope.setEndPoint1L1(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint1L1(), dPhiTelescope, centre));
				lensTelescope.setEndPoint2L1(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint2L1(), dPhiTelescope, centre)); 

				//Note we also have to add some distance in accordance to (f1+f2)-
				lensTelescope.setPrincipalPointL2(Geometry2D.rotateAroundPoint(lensTelescope.getPrincipalPointL2(), dPhiTelescope, centre));
				lensTelescope.setEndPoint1L2(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint1L2(), dPhiTelescope, centre));
				lensTelescope.setEndPoint2L2(Geometry2D.rotateAroundPoint(lensTelescope.getEndPoint2L2(), dPhiTelescope, centre)); 
				break;
			}
			
			lensTelescope.calculateInternalParameters();
			
			return true;
		}
		return false;
	}
}
