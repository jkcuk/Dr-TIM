package optics.rayplay.interactiveOpticalComponents;

import java.awt.BasicStroke;
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

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.LightRay2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.graphicElements.LensTelescopePointGE2D;
import optics.rayplay.graphicElements.LensTelescopePointGE2D.LensTelescopePointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;
import optics.rayplay.util.Colour;

public class LensTelescope2D implements InteractiveOpticalComponent2D
{
	
	private String name;
	private Vector2D principalPointL1;
	private double focalLengthL1;
	private Vector2D endPoint1L1;
	private Vector2D endPoint2L1;
	private Vector2D principalPointL2;
	private double focalLengthL2;
	private Vector2D endPoint1L2;
	private Vector2D endPoint2L2;
	private RayPlay2DPanel rayPlay2DPanel;

	// internal variables
	
	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
	private ArrayList<GraphicElement2D> displayGraphicElements = new ArrayList<GraphicElement2D>();
	private ArrayList<GraphicElement2D> controlGraphicElements = new ArrayList<GraphicElement2D>();

	private Lens2D lens1;
	private Lens2D lens2;
	private Lens2D fplane;
//	public enum LensTelescopeTypes
//	{
//		L1("lens 2"),
//		L2("lens 1");
//
//		public final String name;
//
//		LensTelescopeTypes(String name) {this.name = name;}
//	}
//	
//	private HashMap<LensTelescopeTypes, Lens2D> lenses;
//
//	public Lens2D getLens(LensTelescopeTypes type)
//	{
//		return lenses.get(type);
//	}

	private HashMap<LensTelescopePointType, PointGE2D> points;
	
	// constructors TODO maybe there is a nicer way since at the moment we can set it up non telescopically.... 
	//This should work fine if chosen correctly for ray play at the moment.
	
	public LensTelescope2D(		
			String name,
			Vector2D principalPointL1,
			double focalLengthL1,
			Vector2D endPoint1L1,
			Vector2D endPoint2L1,
			Vector2D principalPointL2,
			double focalLengthL2,
			Vector2D endPoint1L2,
			Vector2D endPoint2L2,
			RayPlay2DPanel rayPlay2DPanel
		)
	{

		this.name = name;
		this.principalPointL1 = principalPointL1;
		this.focalLengthL1 = focalLengthL1;
		this.endPoint1L1 = endPoint1L1;
		this.endPoint2L1 = endPoint2L1;
		this.principalPointL2 = principalPointL2;
		this.focalLengthL2 = focalLengthL2;
		this.endPoint1L2 = endPoint1L2;
		this.endPoint2L2 = endPoint2L2;
		this.rayPlay2DPanel = rayPlay2DPanel;

		// create the points, lines and the lenses, ...
		createInternalArrays();

		// ... and set their parameters
		calculateInternalParameters();
		
		initPopup();
	}


	// setters & getters
	
	public Vector2D getPrincipalPointL1() {
		return principalPointL1;
	}


	public void setPrincipalPointL1(Vector2D principalPointL1) {
		this.principalPointL1 = principalPointL1;
	}


	public double getFocalLengthL1() {
		return focalLengthL1;
	}


	public void setFocalLengthL1(double focalLengthL1) {
		this.focalLengthL1 = focalLengthL1;
	}


	public Vector2D getEndPoint1L1() {
		return endPoint1L1;
	}


	public void setEndPoint1L1(Vector2D endPoint1L1) {
		this.endPoint1L1 = endPoint1L1;
	}


	public Vector2D getEndPoint2L1() {
		return endPoint2L1;
	}


	public void setEndPoint2L1(Vector2D endPoint2L1) {
		this.endPoint2L1 = endPoint2L1;
	}


	public Vector2D getPrincipalPointL2() {
		return principalPointL2;
	}


	public void setPrincipalPointL2(Vector2D principalPointL2) {
		this.principalPointL2 = principalPointL2;
	}


	public double getFocalLengthL2() {
		return focalLengthL2;
	}


	public void setFocalLengthL2(double focalLengthL2) {
		this.focalLengthL2 = focalLengthL2;
	}


	public Vector2D getEndPoint1L2() {
		return endPoint1L2;
	}


	public void setEndPoint1L2(Vector2D endPoint1L2) {
		this.endPoint1L2 = endPoint1L2;
	}


	public Vector2D getEndPoint2L2() {
		return endPoint2L2;
	}


	public void setEndPoint2L2(Vector2D endPoint2L2) {
		this.endPoint2L2 = endPoint2L2;
	}
	
	public PointGE2D getPoint(LensTelescopePointType type)
	{
		return points.get(type);
	}
	
	
	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public String getName() {
		return name;
	}



	public RayPlay2DPanel getPanelWithPopup() {
		return panelWithPopup;
	}


	public void setPanelWithPopup(RayPlay2DPanel panelWithPopup) {
		this.panelWithPopup = panelWithPopup;
	}


	public void setOpticalComponents(ArrayList<OpticalComponent2D> opticalComponents) {
		this.opticalComponents = opticalComponents;
	}


	public RayPlay2DPanel getRayPlay2DPanel() {
		return rayPlay2DPanel;
	}

	public void setRayPlay2DPanel(RayPlay2DPanel rayPlay2DPanel) {
		this.rayPlay2DPanel = rayPlay2DPanel;
	}

	@Override
	public void move(Vector2D delta) 
	{
//		setPrincipalPointL1(
//				Vector2D.sum(getPrincipalPointL1(), delta)
//			);
//		
//		setPrincipalPointL2(Vector2D.sum(getPrincipalPointL2(),delta)
//				);
//		
//		setPrincipalPointL1(
//				Vector2D.sum(getPrincipalPointL1(), delta)
//			);
//		
//		setEndPoint1L1(
//				Vector2D.sum(getEndPoint1L1(), delta)
//			);
//		setEndPoint2L1(
//				Vector2D.sum(getEndPoint2L1(), delta)
//			);
//		
//		setPrincipalPointL2(Vector2D.sum(getPrincipalPointL2(),delta)
//				);
//		
//		setEndPoint1L2(
//				Vector2D.sum(getEndPoint1L2(), delta)
//			);
//		setEndPoint2L2(
//				Vector2D.sum(getEndPoint2L2(), delta)
//			);
		
		calculateInternalParameters();
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
	public void drawGraphicElements(RayPlay2DPanel rpp, Graphics2D g2, boolean isSelected, boolean isMouseNear, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	{
		for(GraphicElement2D ge:displayGraphicElements)
		try{
			ge.draw(rpp, g2, isSelected || isMouseNear, mouseI, mouseJ);
		}catch(NullPointerException e){	
			System.out.println("Lens name: "+ge.getName());
			e.printStackTrace();
		}

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

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
	}

	@Override
	public void drawRays(RayPlay2DPanel p, Graphics2D g, GraphicElement2D graphicElementNearMouse, int mouseI,
			int mouseJ)
	{}


	@Override
	public void writeParameters(PrintStream printStream)
	{

		printStream.println(" Lens telescope");
		
		printStream.println("  principalPointL1 = "+principalPointL1);
		printStream.println("  focalLengthL1 = "+focalLengthL1);
		printStream.println("  endPoint1L1 = "+endPoint1L1);
		printStream.println("  endPoint2L1 = "+endPoint2L1);
		printStream.println("  principalPointL2 = "+principalPointL2);
		printStream.println("  focalLengthL2 = "+focalLengthL2);
		printStream.println("  endPoint1L2 = "+endPoint1L2);
		printStream.println("  endPoint2L2 = "+endPoint2L2);
	}
	

	// the meat
	
//	public Line2D getLens1Axis()
//	{
//		return new Line2D(endPoint1L1, endPoint2L1);
//	}
//	
//	public Line2D getLens2Axis()
//	{
//		return new Line2D(endPoint1L2, endPoint2L2);
//	}
//	
//	public Line2D getCommonFocalPlane()
//	{
//		//TODO check it creates it the right way around. 
//		Vector2D f1 = new Line2D(endPoint1L1, endPoint2L1).getNormal(true).getProductWith(focalLengthL1);
//		return new Line2D(Vector2D.sum(endPoint1L1, f1), Vector2D.sum(endPoint1L1, f1));
//	}
	

	private void createInternalArrays()
	{
		// initialise the array of points
		points = new HashMap<LensTelescopePointType, PointGE2D>();
		
		// and add them all to the list graphicElements
		for(LensTelescopePointType lensTelescopePointType : LensTelescopePointType.values())
		{
			LensTelescopePointGE2D point = new LensTelescopePointGE2D(lensTelescopePointType.name, this, lensTelescopePointType);
			points.put(lensTelescopePointType, point);
			controlGraphicElements.add(point);
		}
			// create the lens, ...
			lens1 = new Lens2D("Lens 1", rayPlay2DPanel);
			lens2 = new Lens2D("Lens 2", rayPlay2DPanel);
			fplane = new Lens2D("focal plane", rayPlay2DPanel); //TODO it is really a line segment but this is quicker and hopefully works...

			opticalComponents.add(lens1);
			opticalComponents.add(lens2);
			displayGraphicElements.add(lens1);
			displayGraphicElements.add(lens2);
			displayGraphicElements.add(fplane);
		}
		

	public void calculateInternalParameters()
	{
		
		
		BasicStroke lensStroke, focalLineStroke;
		Colour lensColour, focalLineColour;
		
		focalLineStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
		lensStroke = new BasicStroke(3);
		lensColour = Colour.CYAN;
		focalLineColour = Colour.ORANGE;
		
		

		points.get(LensTelescopePointType.PL1).setCoordinatesToThoseOf(principalPointL1);
		points.get(LensTelescopePointType.E1L1).setCoordinatesToThoseOf(endPoint1L1);
		points.get(LensTelescopePointType.E2L1).setCoordinatesToThoseOf(endPoint2L1);
		Vector2D lensNormalL1 = this.getL1Normal();
		points.get(LensTelescopePointType.FL1).setCoordinatesToThoseOf(Vector2D.sum(principalPointL1, lensNormalL1.getProductWith(focalLengthL1)));

		lens1.setEndPoints(points.get(LensTelescopePointType.E1L1).getPosition(), points.get(LensTelescopePointType.E2L1).getPosition());
		lens1.setPrincipalPoint(points.get(LensTelescopePointType.PL1).getPosition());
		lens1.setFocalLength(focalLengthL1);
		lens1.setStroke(lensStroke);
		lens1.setColour(lensColour);
		
		
		points.get(LensTelescopePointType.PL2).setCoordinatesToThoseOf(principalPointL2);
		points.get(LensTelescopePointType.E1L2).setCoordinatesToThoseOf(endPoint1L2);
		points.get(LensTelescopePointType.E2L2).setCoordinatesToThoseOf(endPoint2L2);
		Vector2D lensNormalL2 = this.getL2Normal();
		points.get(LensTelescopePointType.FL2).setCoordinatesToThoseOf(Vector2D.sum(principalPointL2, lensNormalL2.getProductWith(focalLengthL2)));
		
		
		lens2.setEndPoints(points.get(LensTelescopePointType.E1L2).getPosition(), points.get(LensTelescopePointType.E2L2).getPosition());
		lens2.setPrincipalPoint(points.get(LensTelescopePointType.PL2).getPosition());
		lens2.setFocalLength(focalLengthL2);
		lens2.setStroke(lensStroke);
		lens2.setColour(lensColour);

		// and the focal plane //TODO yet another place this could break...
		Vector2D fp1 = Vector2D.sum(points.get(LensTelescopePointType.E1L1).getPosition(), lensNormalL1.getProductWith(-focalLengthL1));
		Vector2D fp2 = Vector2D.sum(points.get(LensTelescopePointType.E2L1).getPosition(), lensNormalL1.getProductWith(-focalLengthL1));
	
		Vector2D fp1Int = Geometry2D.lineLineIntersection2D(points.get(LensTelescopePointType.E1L1).getPosition(),
				Vector2D.difference(points.get(LensTelescopePointType.E1L1).getPosition(), points.get(LensTelescopePointType.E1L2).getPosition()), 
				fp1, 
				Vector2D.difference(fp2, fp1)
				);
		Vector2D fp2Int = Geometry2D.lineLineIntersection2D(points.get(LensTelescopePointType.E2L1).getPosition(), 
				Vector2D.difference(points.get(LensTelescopePointType.E2L1).getPosition(), points.get(LensTelescopePointType.E2L2).getPosition()), 
				fp1, 
				Vector2D.difference(fp1, fp2)
				);
		fplane.setEndPoints(fp2Int,fp1Int);
		fplane.setStroke(focalLineStroke);
		fplane.setColour(focalLineColour);
	}
	//Some useful methods
	public Line2D getOpticalAxisL1()
	{ 
		Vector2D normal = new Line2D(endPoint1L1, endPoint2L1).getNormal(false);
		if(Vector2D.difference(endPoint2L1, endPoint1L1).getLength() <= MyMath.TINY) {
			normal= new Line2D(endPoint2L1, principalPointL1).getNormal(false); 
		}
		
		return new Line2D(
				principalPointL1,
				Vector2D.sum(principalPointL1, normal.getNormalised())
			);
	}
	
	public Line2D getOpticalAxisL2()
	{ 
		Vector2D normal = new Line2D(endPoint2L2, endPoint1L2).getNormal(false);
		if(Vector2D.difference(endPoint2L2, endPoint1L2).getLength() <= MyMath.TINY) {
			normal= new Line2D(endPoint2L2, principalPointL2).getNormal(false);
		}
		return new Line2D(
				principalPointL2,
				Vector2D.sum(principalPointL2, normal.getNormalised())
			);
	}
	
	public Line2D getABLineL1()
	{
		if(Vector2D.difference(endPoint2L1, endPoint1L1).getLength() <= MyMath.TINY) {
			return new Line2D(endPoint2L1,principalPointL1); 
		}
		
		return new Line2D(endPoint2L1,endPoint1L1);
	}
	
	public Line2D getABLineL2()
	{
		if(Vector2D.difference(endPoint2L2, endPoint1L2).getLength() <= MyMath.TINY) {
			return new Line2D(endPoint2L2,principalPointL2); 
		}
		
		return new Line2D(endPoint2L2,endPoint1L2);
	}
	
	public Vector2D getABNormalisedL1()
	{ 
		if(Vector2D.difference(endPoint2L1, endPoint1L1).getLength() <= MyMath.TINY) {
			return Vector2D.difference(endPoint2L1, principalPointL1).getNormalised();
		}
		return Vector2D.difference(endPoint2L1, endPoint1L1).getNormalised();
	}
	
	public Vector2D getABNormalisedL2()
	{
		if(Vector2D.difference(endPoint2L2, endPoint1L2).getLength() <= MyMath.TINY) {
			return Vector2D.difference(endPoint2L2, principalPointL2).getNormalised();
		}
		return Vector2D.difference(endPoint2L2, endPoint1L2).getNormalised();
	}
	
	public Vector2D getL1Normal()
	{ 
		if(Vector2D.difference(endPoint2L1, endPoint1L1).getLength() <= MyMath.TINY) {
			return new Line2D(endPoint2L1, principalPointL1).getNormal(true);
		}
		return new Line2D(endPoint2L1, endPoint1L1).getNormal(true);
	}
	
	public Vector2D getL2Normal()
	{ 
		if(Vector2D.difference(endPoint2L2, endPoint1L2).getLength() <= MyMath.TINY) {
			return new Line2D(endPoint2L2, principalPointL2).getNormal(true);
		}
		return new Line2D(endPoint2L2, endPoint1L2).getNormal(true);
	}
	
	@Override
	public InteractiveOpticalComponent2D readFromCSV(String filename)
	{
		return null;
	}


	@Override
	public void writeToCSV(PrintWriter writer)
	{
		
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
	public ArrayList<LightRay2D> getRays() {
		return LightRay2D.NO_RAYS;
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
		JMenuItem deleteWHMenuItem = new JMenuItem("Delete telescope");
		deleteWHMenuItem.setEnabled(true);
		deleteWHMenuItem.getAccessibleContext().setAccessibleDescription("Delete telescope");
		deleteWHMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(LensTelescope2D.this);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteWHMenuItem);
		
		// Separator
	    popup.addSeparator();

	    JMenuItem turnIntoIndividualLensesMenuItem = new JMenuItem("Turn into individual lenses");
		turnIntoIndividualLensesMenuItem.setEnabled(true);
		turnIntoIndividualLensesMenuItem.getAccessibleContext().setAccessibleDescription("Turn into individual lenses");
		turnIntoIndividualLensesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the wormhole lens
				
				panelWithPopup.iocs.remove(LensTelescope2D.this);
				
				// and add all the lenses
					Lens2DIOC diocLens1 = new Lens2DIOC(lens1, rayPlay2DPanel);
					Lens2DIOC diocLens2 = new Lens2DIOC(lens2, rayPlay2DPanel);
					// panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
					panelWithPopup.iocs.add(diocLens1);
					panelWithPopup.iocs.add(diocLens2);
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