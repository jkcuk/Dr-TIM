package optics.rayplay.interactiveOpticalComponents;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.Ray2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.graphicElements.LensPointGE2D;
import optics.rayplay.graphicElements.LensPointGE2D.LensPointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;

public class Lens2DIOC extends Lens2D
implements InteractiveOpticalComponent2D
{
	// internal variables
	
	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
	private ArrayList<GraphicElement2D> displayGraphicElements = new ArrayList<GraphicElement2D>();
	private ArrayList<GraphicElement2D> controlGraphicElements = new ArrayList<GraphicElement2D>();
	
	private HashMap<LensPointType, PointGE2D> points;

	
	// constructors
	
	public Lens2DIOC(		
			String name,
			Vector2D principalPoint,
			double focalLength,
			Vector2D endPoint1,
			Vector2D endPoint2
		)
	{
		super(name, principalPoint, focalLength, endPoint1, endPoint2);
		
		// create the points, ...
		createPoints();

		// ... and set their parameters
		calculatePointParameters();

		opticalComponents.add(this);
		displayGraphicElements.add(this);
		
		initPopup();
	}
	
	public Lens2DIOC(Lens2D lens)
	{
		this(lens.getName(), lens.getPrincipalPoint(), lens.getFocalLength(), lens.getA(), lens.getB());
	}
	
	/**
	 * @param field	{Lens, <name>, <name of parameter 1>=<value of parameter 1>, ...}
	 */
	public Lens2DIOC(String[] field)
	{
		super(field[1]);
	}
	
	
	// the meat

	private void createPoints()
	{
		// initialise the array of points
		points = new HashMap<LensPointType, PointGE2D>();

		// and add them all to the list graphicElements
		for(LensPointType lensPointType : LensPointType.values())
		{
			LensPointGE2D p = new LensPointGE2D(
						lensPointType.name,	// name
						this,	// lens
						lensPointType
					);
//			switch(lensPointType)
//			{
//			case P:
//				p.setPosition(principalPoint);
//				break;
//			case E1:
//				p.setPosition(a);
//				break;
//			case E2:
//				p.setPosition(b);
//				break;
//			default:
//			}
			
			points.put(lensPointType, p);
			switch(lensPointType)
			{
			case P:
				displayGraphicElements.add(p);
				break;
			default:				
				controlGraphicElements.add(p);
			}
		}
	}

	public void calculatePointParameters()
	{		
		// the bottom vertices
		points.get(LensPointType.F).setCoordinatesToThoseOf(getFocalPoint());
		points.get(LensPointType.P).setCoordinatesToThoseOf(getPrincipalPoint());
		points.get(LensPointType.E1).setCoordinatesToThoseOf(a);
		points.get(LensPointType.E2).setCoordinatesToThoseOf(b);
		
		// calculate the position of type L at the time of drawing
		// points.get(LensPointType.L).setCoordinatesToThoseOf(Vector2D.sum(getPrincipalPoint(), getDirection().getWithLength(bla)));
	}

	
	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public void move(Vector2D delta)
	{
		setEndPoints(
				Vector2D.sum(getA(), delta),
				Vector2D.sum(getB(), delta)
			);
		
		// finally, set the principal point to its new position
		setPrincipalPoint(
				Vector2D.sum(getPrincipalPoint(), delta)
			);
		
		calculatePointParameters();
	}
	
	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents()
	{
		return opticalComponents;
	}

	@Override
	public ArrayList<GraphicElement2D> getGraphicElements(boolean isSelected, boolean isMouseNear)
	{
		if(!isSelected)
			return displayGraphicElements;
		else
		{
			ArrayList<GraphicElement2D> ges = new ArrayList<GraphicElement2D>();
			ges.addAll(controlGraphicElements);
			ges.addAll(displayGraphicElements);
			return ges;
		}
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		printStream.println("\nLens \""+ name +"\"\n");

		printStream.println("  principalPoint = "+principalPoint);
		printStream.println("  focalLength = "+focalLength);
		printStream.println("  a (end point 1) = "+a);
		printStream.println("  b (end point 2) = "+b);
	}
	
	@Override
	public void drawGraphicElements(RayPlay2DPanel rpp, Graphics2D g2, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:displayGraphicElements)
			ge.draw(rpp, g2, isSelected || isMouseNear, mouseI, mouseJ);

		if(isSelected)
		for(GraphicElement2D ge:controlGraphicElements)
			ge.draw(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
	}

	@Override
	public void drawGraphicElementsInFront(RayPlay2DPanel rpp, Graphics2D g2, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:displayGraphicElements)
			ge.drawInFront(rpp, g2, isSelected || isMouseNear, mouseI, mouseJ);
		
		if(isSelected)
		for(GraphicElement2D ge:controlGraphicElements)
			ge.drawInFront(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
	}

	@Override
	public void drawGraphicElementsBehind(RayPlay2DPanel rpp, Graphics2D g2, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:displayGraphicElements)
			ge.drawBehind(rpp, g2, isSelected || isMouseNear, mouseI, mouseJ);
		
		if(isSelected)
		for(GraphicElement2D ge:controlGraphicElements)
			ge.drawBehind(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
	}

//	@Override
//	public void drawGraphicElementsBehind(RayPlay2DPanel rpp, Graphics2D g2, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
//	{
//		if(isSelected || isMouseNear)
//			for(GraphicElement2D ge:displayGraphicElements)
//				ge.drawOnTop(rpp, g2, true, mouseI, mouseJ);
//		
//		if(isSelected)
//		for(GraphicElement2D ge:controlGraphicElements)
//			ge.drawOnTop(rpp, g2, ge == graphicElementNearMouse, mouseI, mouseJ);
//	}

	@Override
	public InteractiveOpticalComponent2D readFromCSV(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeToCSV(PrintWriter writer) {
		// TODO Auto-generated method stub
		
	}
	
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		for(GraphicElement2D g:getGraphicElements(false, false))
			g.writeSVGCode(rpp);
	}
	
	@Override
	public void initialiseRays()
	{}
	
	@Override
	public ArrayList<Ray2D> getRays() {
		return Ray2D.NO_RAYS;
	}

	@Override
	public void drawRays(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI,
			int mouseJ)
	{}
	
	
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
		deleteLensMenuItem;

	private void initPopup()
	{
		deleteLensMenuItem = new JMenuItem("Delete lens");
		deleteLensMenuItem.setEnabled(true);
		deleteLensMenuItem.getAccessibleContext().setAccessibleDescription("Delete lens");
		deleteLensMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// panelWithPopup.graphicElements.removeAll(lens.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(Lens2DIOC.this);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteLensMenuItem);
	}
	
	private void updatePopup()
	{
		// enable/disable + text
	}	

}
