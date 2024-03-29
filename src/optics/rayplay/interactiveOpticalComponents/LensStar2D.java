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
import optics.rayplay.core.LightRay2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.graphicElements.LensStarPointGE2D;
import optics.rayplay.graphicElements.LensStarPointGE2D.LensStarPointType;
import optics.rayplay.opticalComponents.Lens2D;

/**
 * An regular star of lenses.
 * 
 * References:
 * [1] J. Belin, J. Courtial, and T. Tyc, "Lens stars and Platonic lenses", Opt. Express  29  42055--42074  (2021)
 * 
 * @author johannes
 */
public class LensStar2D implements InteractiveOpticalComponent2D
{
	private String name;
	
	private RayPlay2DPanel rayPlay2DPanel;

	// the parameters

	/**
	 * the focal length of each of the lenses
	 */
	private double f;

	/**
	 * the number of lenses in the star
	 */
	private int n;

	/**
	 * the radial distance of the principal point of the lenses
	 */
	private double rP;
	
	/**
	 * the centre of the lens star
	 */
	private Vector2D c;

	/**
	 * radius of the lens star
	 */
	private double r;

	/**
	 * azimuthal angle of lens 0
	 */
	private double phi0;



	// internal variables

	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();

	/**
	 * list of graphic elements
	 */
	private ArrayList<GraphicElement2D> displayGraphicElements = new ArrayList<GraphicElement2D>();
	private ArrayList<GraphicElement2D> controlGraphicElements = new ArrayList<GraphicElement2D>();

	
	/**
	 * the lenses
	 */
	private ArrayList<Lens2D> lenses;


	private HashMap<LensStarPointType, LensStarPointGE2D> points;



	// constructor

	public LensStar2D(
			String name,
			double f,
			int n,
			double rP,
			Vector2D c,
			double r,
			double phi0,
			RayPlay2DPanel rayPlay2DPanel
		)
	{
		super();
		this.name = name;
		this.f = f;
		this.n = n;
		this.rP = rP;
		this.c = c;
		this.r = r;
		this.phi0 = phi0;
		this.rayPlay2DPanel = rayPlay2DPanel;

		// create the points and lenses ...
		createPointsAndLenses();

		// ... and set their parameters
		calculatePointParameters();
		calculateLensParameters();
		
		initPopup();
	}



	// setters & getters

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public int getN() {
		return n;
	}

	/**
	 * Set the number of lenses; ignore if n < 2 or n > 1000
	 * @param n
	 */
	public void setN(int n) {
		if((n > 1) && (n <= 1000))
			this.n = n;
	}

	public double getrP() {
		return rP;
	}

	public void setrP(double rP) {
		this.rP = rP;
	}

	public Vector2D getC() {
		return c;
	}

	public void setC(Vector2D c) {
		this.c = c;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public double getPhi0() {
		return phi0;
	}

	public void setPhi0(double phi0) {
		this.phi0 = phi0;
	}

	public ArrayList<Lens2D> getLenses() {
		return lenses;
	}

	public void setLenses(ArrayList<Lens2D> lenses) {
		this.lenses = lenses;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public RayPlay2DPanel getRayPlay2DPanel() {
		return rayPlay2DPanel;
	}

	public void setRayPlay2DPanel(RayPlay2DPanel rayPlay2DPanel) {
		this.rayPlay2DPanel = rayPlay2DPanel;
	}

	

	// useful
	
	public Lens2D getLens(int i)
	{
		return lenses.get(i);
	}
	
	public void removeLensesFromOpticalComponentsAndGraphicElements()
	{
		opticalComponents.removeAll(lenses);
		displayGraphicElements.removeAll(lenses);
	}


	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public void move(Vector2D delta)
	{
		setC(
				Vector2D.sum(getC(), delta)
			);
		
		calculatePointParameters();
		calculateLensParameters();
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
		printStream.println("\nLens star \""+ name +"\"\n");

		printStream.println("  f = "+f);
		printStream.println("  n = "+n);
		printStream.println("  rP = "+rP);
		printStream.println("  c = "+c);
		printStream.println("  r = "+r);
		printStream.println("  phi0 = "+phi0);
	}
	

	// the meat
	
	/**
	 * @return	a unit vector in the direction of lens 0
	 */
	public Vector2D getL0()
	{
		return new Vector2D(Math.cos(phi0), Math.sin(phi0));
	}
	
	public Vector2D getL1()
	{
		double phi1 = phi0 + 2.*Math.PI/n;
		return new Vector2D(Math.cos(phi1), Math.sin(phi1));
	}
	
	/**
	 * @return	the principal point of lens 0
	 */
	public Vector2D getP0()
	{
		return Vector2D.sum(c, getL0().getProductWith(rP));
	}
	
	public Vector2D getF0()
	{
		return Vector2D.sum(getP0(), getL0().getPerpendicularVector().getProductWith(f));
	}
	
	public Line2D getLineOfLens0()
	{
		return new Line2D(c, Vector2D.sum(c, getL0()));
	}
	
	public Line2D getOpticalAxisOfLens0()
	{
		Vector2D p0 = getP0();
		return new Line2D(p0, Vector2D.sum(p0, getL0().getPerpendicularVector()));
	}
	
	private void createPointsAndLenses()
	{
		opticalComponents.clear();
		displayGraphicElements.clear();
		controlGraphicElements.clear();
		
		createPoints();
		createLenses();
	}
	

	private void createPoints()
	{
		// initialise the array of points
		points = new HashMap<LensStarPointType, LensStarPointGE2D>();

		// and add them all to the list graphicElements
		for(LensStarPointType lsPointType : LensStarPointType.values())
		{
			LensStarPointGE2D point = new LensStarPointGE2D(lsPointType.name, new Vector2D(0, 0), this, lsPointType);
			points.put(lsPointType, point);
			controlGraphicElements.add(point);
		}
	}

	public void createLenses()
	{
		// initialise the array of lenses
		// lens = new Lens2D[OLLensType.values().length];
		lenses = new ArrayList<Lens2D>(n);

		for(int i=0; i<n; i++)
		{
			// create the lens, ...
			Lens2D l = new Lens2D("Lens "+i+"/"+n+" in \""+name+"\"", rayPlay2DPanel);

			// ... add it to the array of lenses, ...
			lenses.add(l);

			// ... and add it to the list of optical components and the list of graphic elements
			opticalComponents.add(l);
			displayGraphicElements.add(l);
		}
	}

	public void calculatePointParameters()
	{		
		points.get(LensStarPointType.C).setCoordinatesToThoseOf(c);
		points.get(LensStarPointType.L0).setCoordinatesToThoseOf(Vector2D.sum(c, getL0().getProductWith(r)));
		points.get(LensStarPointType.L1).setCoordinatesToThoseOf(Vector2D.sum(c, getL1().getProductWith(r)));
		points.get(LensStarPointType.P0).setCoordinatesToThoseOf(getP0());
		points.get(LensStarPointType.F0).setCoordinatesToThoseOf(getF0());
	}

	public void calculateLensParameters()
	{
		// define a Cartesian coordinate system centred at c and with axes l0 (in the direction of lens 0) and s0 (perpendicular to lens 0)
		Vector2D l0 = getL0();
		Vector2D s0 = l0.getPerpendicularVector();
		
		for(int i=0; i<n; i++)
		{
			double phi = i*2.*Math.PI/n;
			
			// vector in the plane of lens i
			Vector2D li = Vector2D.sum(
					l0.getProductWith(Math.cos(phi)),
					s0.getProductWith(Math.sin(phi))
				);
			
			Lens2D lensi = lenses.get(i);
			lensi.setEndPoints(c, Vector2D.sum(c, li.getProductWith(r)));
			lensi.setPrincipalPoint(Vector2D.sum(c, li.getProductWith(rP)));
			lensi.setFocalLength(f);
		}
	}


	@Override
	public InteractiveOpticalComponent2D readFromCSV(String filename)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeToCSV(PrintWriter writer)
	{
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
				// panelWithPopup.graphicElements.removeAll(ls.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(LensStar2D.this);
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
				setrP(0);
				calculatePointParameters();
				calculateLensParameters();
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
				// panelWithPopup.graphicElements.removeAll(ls.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(LensStar2D.this);
				
				// and add all the lenses
				for(int i=0; i<getN(); i++)
				{
					Lens2DIOC lens = new Lens2DIOC(getLens(i), rayPlay2DPanel);
					panelWithPopup.iocs.add(lens);
					// panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
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
