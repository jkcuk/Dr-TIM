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

import math.MyMath;
import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.Ray2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.graphicElements.LineSegmentGE2D;
import optics.rayplay.graphicElements.OmnidirectionalLensLineGE2D;
import optics.rayplay.graphicElements.OmnidirectionalLensLineGE2D.OmnidirectionalLensLineType;
import optics.rayplay.graphicElements.OmnidirectionalLensPointGE2D;
import optics.rayplay.graphicElements.OmnidirectionalLensPointGE2D.OmnidirectionalLensPointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;

/**
 * An ideal-lens cloak / omnidirectional lens.
 * 
 * References:
 * [1] J. Courtial et al., "Ray-optical transformation optics with ideal thin lenses makes omnidirectional lenses",
 *     Opt. Express 26, 17872-17888 (2018)
 * 
 * @author johannes
 */
public class OmnidirectionalLens2D implements InteractiveOpticalComponent2D
{
	private String name;

	// the parameters -- see [1] for nomenclature

	/**
	 * the focal length of lens D (the base lens)
	 */
	private double fD;

	/**
	 * the radius of lens D (the base lens)
	 */
	private double rD;

	/**
	 * height of lower inner vertex above lens D
	 */
	private double h1;

	/**
	 * height of upper inner vertex above lens D
	 */
	private double h2;

	/**
	 * height of top vertex above lens D
	 */
	private double h;

	/**
	 * the principal point of lens D (the base lens), P_D
	 */
	private Vector2D pD;

	/**
	 * unit vector in the plane of lens D, pointing in the direction from P_D to V_1
	 */
	private Vector2D dHat;

	/**
	 * unit vector in the direction of the central axis, pointing from P_D to the other principal points
	 */
	private Vector2D cHat;
	
	private boolean showC1PointAndLine;



	// internal variables
	
	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();

	/**
	 * list of graphic elements
	 */
	private ArrayList<GraphicElement2D> displayGraphicElements = new ArrayList<GraphicElement2D>();
	private ArrayList<GraphicElement2D> controlGraphicElements = new ArrayList<GraphicElement2D>();

	/**
	 * The lens types.
	 * For nomenclature see Fig. 7 in [1]
	 * @author johannes
	 */
	public enum OmnidirectionalLensLensType
	{
		A1("A1/03a"),
		A2("A2/03b"),
		B1("B1/2a3a"),
		B2("B2/2b3b"),
		C1("C1/12a"),
		C2("C2/12b"),
		D("D/01"),
		E("E/2a2b"),
		F("F/3a3b");

		public final String name;

		OmnidirectionalLensLensType(String name) {this.name = name;}
	}

	/**
	 * the lenses
	 */
	// private Lens2D lens[];
	private HashMap<OmnidirectionalLensLensType, Lens2D> lenses;

	public Lens2D getLens(OmnidirectionalLensLensType type)
	{
		return lenses.get(type);
	}

	private HashMap<OmnidirectionalLensPointType, PointGE2D> points;

	/**
	 * the lenses
	 */
	// private Lens2D lens[];
	private HashMap<OmnidirectionalLensLineType, LineSegmentGE2D> lines;



	// constructor

	public OmnidirectionalLens2D(
			String name,
			double fD,
			double rD,
			double h1,
			double h2,
			double h,
			Vector2D pD,
			Vector2D dHat,
			Vector2D cHat,
			boolean showC1PointAndLine
			)
	{
		this.name = name;
		this.fD = fD;
		this.rD = rD;
		this.h1 = h1;
		this.h2 = h2;
		this.h = h;
		this.pD = pD;
		this.dHat = dHat;
		this.cHat = cHat;
		this.showC1PointAndLine = showC1PointAndLine;

		// create the points, lines and the lenses, ...
		createInternalArrays();

		// ... and set their parameters
		calculateInternalParameters();
		
		initPopup();
	}


	// setters & getters

	public double getfD() {
		return fD;
	}

	public void setfD(double fD) {
		this.fD = fD;
	}

	public double getrD() {
		return rD;
	}

	public void setrD(double rD) {
		this.rD = rD;
	}

	public double getH1() {
		return h1;
	}

	public void setH1(double h1) {
		this.h1 = h1;
	}

	public double getH2() {
		return h2;
	}

	public void setH2(double h2) {
		this.h2 = h2;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public Vector2D getpD() {
		return pD;
	}

	public void setpD(Vector2D pD) {
		this.pD = pD;
	}

	public Vector2D getdHat() {
		return dHat;
	}

	public void setdHat(Vector2D d) {
		this.dHat = d.getNormalised();
	}

	public Vector2D getcHat() {
		return cHat;
	}

	public void setcHat(Vector2D c) {
		this.cHat = c.getNormalised();
	}

	public boolean isShowC1PointAndLine() {
		return showC1PointAndLine;
	}

	public void setShowC1PointAndLine(boolean showC1PointAndLine) {
		this.showC1PointAndLine = showC1PointAndLine;
	}


	
	
	public PointGE2D getPoint(OmnidirectionalLensPointType type)
	{
		return points.get(type);
	}

	public Line2D getC1Line() {
		return new Line2D(
				getPoint(OmnidirectionalLensPointType.C1).getPosition(),
				getPoint(OmnidirectionalLensPointType.V1).getPosition()
			);
	}





	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void move(Vector2D delta)
	{
		setpD(
				Vector2D.sum(getpD(), delta)
			);
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
	public void writeParameters(PrintStream printStream)
	{
		printStream.println("\nOmnidirectional lens \""+ name +"\"\n");

		printStream.println("  fD = "+fD);
		printStream.println("  baseLensRadius = "+rD);
		printStream.println("  h1 = "+h1);
		printStream.println("  h2 = "+h2);
		printStream.println("  h = "+h);
		printStream.println("  pD = "+pD);
		printStream.println("  dHat = "+dHat);
		printStream.println("  cHat = "+cHat);
		printStream.println("  showC1PointAndLine = "+showC1PointAndLine);
		
		printStream.println("  Lens focal lengths:");
		printStream.println("  	 fA = "+getLens(OmnidirectionalLensLensType.A1).getFocalLength());
		printStream.println("    fB = "+getLens(OmnidirectionalLensLensType.B1).getFocalLength());
		printStream.println("    fC = "+getLens(OmnidirectionalLensLensType.C1).getFocalLength());
		printStream.println("    fD = "+getLens(OmnidirectionalLensLensType.D).getFocalLength());
		printStream.println("    fE = "+getLens(OmnidirectionalLensLensType.E).getFocalLength());
		printStream.println("    fF = "+getLens(OmnidirectionalLensLensType.F).getFocalLength());

		printStream.println("  Other information:");
		printStream.println("  	 c1Point = "+getPoint(OmnidirectionalLensPointType.C1).getPosition());

	}
	

	// the meat
	
	public Line2D getCentralSymmetryAxis()
	{
		return new Line2D(pD, Vector2D.sum(pD, cHat));
	}
	
	public Line2D getLineOfLensD()
	{
		return new Line2D(pD, Vector2D.sum(pD, dHat));
	}

	private void createInternalArrays()
	{
		// initialise the array of points
		points = new HashMap<OmnidirectionalLensPointType, PointGE2D>();

		// and add them all to the list graphicElements
		for(OmnidirectionalLensPointType olPointType : OmnidirectionalLensPointType.values())
		{
			OmnidirectionalLensPointGE2D point = new OmnidirectionalLensPointGE2D(olPointType.name, this, olPointType);
			points.put(olPointType, point);
			switch(olPointType)
			{
			case C1:
				displayGraphicElements.add(point);
				break;
			default:
				controlGraphicElements.add(point);
			}
		}

		// the lines
		lines = new HashMap<OmnidirectionalLensLineType, LineSegmentGE2D>();

		// and add them all to the list graphicElements
		for(OmnidirectionalLensLineType olLineType : OmnidirectionalLensLineType.values())
		{
			OmnidirectionalLensLineGE2D line = new OmnidirectionalLensLineGE2D(olLineType.name, this, olLineType);
			lines.put(olLineType, line);
			displayGraphicElements.add(line);
		}

		
		// initialise the array of lenses
		// lens = new Lens2D[OLLensType.values().length];
		lenses = new HashMap<OmnidirectionalLensLensType, Lens2D>();

		for(OmnidirectionalLensLensType olLensType : OmnidirectionalLensLensType.values())
		{
			// create the lens, ...
			Lens2D l = new Lens2D("Lens \""+olLensType.toString()+"\" in \""+name+"\"");

			// ... add it to the array of lenses, ...
			lenses.put(olLensType, l);

			// ... and add it to the list of optical components and the list of graphic elements
			opticalComponents.add(l);
			displayGraphicElements.add(l);
		}
	}

	public void calculateInternalParameters()
	{
		// the bottom vertices
		points.get(OmnidirectionalLensPointType.V1).setCoordinatesToThoseOf(Vector2D.sum(pD, dHat.getProductWith(-rD)));
		points.get(OmnidirectionalLensPointType.V2).setCoordinatesToThoseOf(Vector2D.sum(pD, dHat.getProductWith( rD)));

		// the lower inner vertex
		points.get(OmnidirectionalLensPointType.P1).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(h1)));

		// the upper inner vertex
		points.get(OmnidirectionalLensPointType.P2).setCoordinatesToThoseOf(
				// new Vector2D(0.2, 0.4)
				Vector2D.sum(pD, cHat.getProductWith(h2))
				);

		// the top vertex
		points.get(OmnidirectionalLensPointType.P3).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(h)));

		// the principal point of lens D
		points.get(OmnidirectionalLensPointType.P0).setCoordinatesToThoseOf(pD);

		// the (inside) focal point of lens D
		points.get(OmnidirectionalLensPointType.FD).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(fD)));

		
		// calculate the focal lengths from fD and r, h1, h2, h -- see AllLoopTheorems 2D.nb
		double r2 = rD*rD;
		double fA = ((-fD*h + (fD + h)*h1)*(h - h2)*rD)/(h1*h2*Math.sqrt(h*h + r2));
		double fB = (fD*(h - h2)*(-h1 + h2)*rD)/(h*h1*Math.sqrt(h2*h2 + r2));
		double fC = ((fD*h - (fD + h)*h1)*(h1 - h2)*rD)/(h*h2*Math.sqrt(h1*h1 + r2));
		double fE = ((fD*h - (fD + h)*h1)*(h1 - h2)*rD)/(2*h*h1*h2);
		double fF = ((fD*h - (fD + h)*h1)*(h  - h2)*rD)/(2*h*h1*h2);

		// lenses A
		lenses.get(OmnidirectionalLensLensType.A1).setEndPoints(points.get(OmnidirectionalLensPointType.V1).getPosition(), points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.A1).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.A1).setFocalLength(fA);

		lenses.get(OmnidirectionalLensLensType.A2).setEndPoints(points.get(OmnidirectionalLensPointType.V2).getPosition(), points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.A2).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.A2).setFocalLength(fA);

		// lenses B
		lenses.get(OmnidirectionalLensLensType.B1).setEndPoints(points.get(OmnidirectionalLensPointType.V1).getPosition(), points.get(OmnidirectionalLensPointType.P2).getPosition());
		lenses.get(OmnidirectionalLensLensType.B1).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P2).getPosition());
		lenses.get(OmnidirectionalLensLensType.B1).setFocalLength(fB);

		lenses.get(OmnidirectionalLensLensType.B2).setEndPoints(points.get(OmnidirectionalLensPointType.V2).getPosition(), points.get(OmnidirectionalLensPointType.P2).getPosition());
		lenses.get(OmnidirectionalLensLensType.B2).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P2).getPosition());
		lenses.get(OmnidirectionalLensLensType.B2).setFocalLength(fB);

		// lenses C
		lenses.get(OmnidirectionalLensLensType.C1).setEndPoints(points.get(OmnidirectionalLensPointType.V1).getPosition(), points.get(OmnidirectionalLensPointType.P1).getPosition());
		lenses.get(OmnidirectionalLensLensType.C1).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P1).getPosition());
		lenses.get(OmnidirectionalLensLensType.C1).setFocalLength(fC);

		lenses.get(OmnidirectionalLensLensType.C2).setEndPoints(points.get(OmnidirectionalLensPointType.V2).getPosition(), points.get(OmnidirectionalLensPointType.P1).getPosition());
		lenses.get(OmnidirectionalLensLensType.C2).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P1).getPosition());
		lenses.get(OmnidirectionalLensLensType.C2).setFocalLength(fC);

		// the "base lens", lens D
		lenses.get(OmnidirectionalLensLensType.D).setEndPoints(points.get(OmnidirectionalLensPointType.V1).getPosition(), points.get(OmnidirectionalLensPointType.V2).getPosition());
		lenses.get(OmnidirectionalLensLensType.D).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P0).getPosition());
		lenses.get(OmnidirectionalLensLensType.D).setFocalLength(fD);

		// lens E
		lenses.get(OmnidirectionalLensLensType.E).setEndPoints(points.get(OmnidirectionalLensPointType.P1).getPosition(), points.get(OmnidirectionalLensPointType.P2).getPosition());
		lenses.get(OmnidirectionalLensLensType.E).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P1).getPosition());
		lenses.get(OmnidirectionalLensLensType.E).setFocalLength(fE);

		// lens F
		lenses.get(OmnidirectionalLensLensType.F).setEndPoints(points.get(OmnidirectionalLensPointType.P2).getPosition(), points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.F).setPrincipalPoint(points.get(OmnidirectionalLensPointType.P3).getPosition());
		lenses.get(OmnidirectionalLensLensType.F).setFocalLength(fF);

		
		// calculate the C1 point
		Vector2D focalPointC1 = getLens(OmnidirectionalLensLensType.C1).getFocalPoint();
		Vector2D directionC1 = getLens(OmnidirectionalLensLensType.C1).getDirection();
		double alpha1 = Geometry2D.getAlpha1ForLineLineIntersection2D(
				focalPointC1,	// a1, i.e. point on line 1
				directionC1,	// d1, i.e. direction of line 1
				getLens(OmnidirectionalLensLensType.C1).getPrincipalPoint(),	// a2, i.e. point on line 2
				getLens(OmnidirectionalLensLensType.D).getDirection()	// d2, i.e. direction of line 2
			);
		points.get(OmnidirectionalLensPointType.C1).setCoordinatesToThoseOf(
				Vector2D.sum(focalPointC1, directionC1.getProductWith(alpha1))
			);
		
		
		// calculate the C1 line
		Line2D c1 = getC1Line();
		alpha1 = Geometry2D.getAlpha1ForLineLineIntersection2D(
				c1.getA(), c1.getA2B(),	// the (unrestricted) c1 line
				lenses.get(OmnidirectionalLensLensType.C2).getA(), lenses.get(OmnidirectionalLensLensType.C2).getA2B()
			);
		lines.get(OmnidirectionalLensLineType.C1).setA(points.get(OmnidirectionalLensPointType.V1).getPosition());
		lines.get(OmnidirectionalLensLineType.C1).setB(Vector2D.sum(c1.getA(), c1.getA2B().getProductWith(alpha1)));
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
	public ArrayList<Ray2D> getRays() {
		return Ray2D.NO_RAYS;
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
				// panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(OmnidirectionalLens2D.this);
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
				// panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(OmnidirectionalLens2D.this);
				
				// and add all the lenses
				for(OmnidirectionalLensLensType olLensType:OmnidirectionalLensLensType.values())
				{
					Lens2DIOC lens = new Lens2DIOC(getLens(olLensType));
					panelWithPopup.iocs.add(lens);
					// panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
				}
				
				panelWithPopup.repaint();
			}
		});
		popup.add(turnIntoIndividualLensesMenuItem);

		// Separator
	    popup.addSeparator();

	    JMenuItem showC1ItemsMenuItem = new JMenuItem("Show C_2a point and line");
	    showC1ItemsMenuItem.setEnabled(true);
	    showC1ItemsMenuItem.getAccessibleContext().setAccessibleDescription("Show C_2a point and line");
	    showC1ItemsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setShowC1PointAndLine(!isShowC1PointAndLine());

				panelWithPopup.repaint();
			}
		});
		popup.add(showC1ItemsMenuItem);
		
	    JMenuItem addRaySourceMenuItem = new JMenuItem("Add ray source at C_2a point and constrained to C_2a line");
	    addRaySourceMenuItem.setEnabled(true);
	    addRaySourceMenuItem.getAccessibleContext().setAccessibleDescription("Add ray source at C1 point and constrained to the omnidirectional lens's C_2a line");
	    addRaySourceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				// find intersection of focal plane of lens C1 in cell 2a with the horizontal line through P1
//				// works only if f of lens C1 is > 0
//				Vector2D focalPointC1 = ol.getLens(OmnidirectionalLensLensType.C1).getFocalPoint();
//				Vector2D directionC1 = ol.getLens(OmnidirectionalLensLensType.C1).getDirection();
//				double alpha1 = Geometry2D.getAlpha1ForLineLineIntersection2D(
//						focalPointC1,	// a1, i.e. point on line 1
//						directionC1,	// d1, i.e. direction of line 1
//						ol.getLens(OmnidirectionalLensLensType.C1).getPrincipalPoint(),	// a2, i.e. point on line 2
//						ol.getLens(OmnidirectionalLensLensType.D).getDirection()	// d2, i.e. direction of line 2
//					);
//				Vector2D cPoint = Vector2D.sum(focalPointC1, directionC1.getProductWith(alpha1));
				PointRaySource2D ls = new PointRaySource2D(
						"Point ray source constrained to C_2a line",	// name
						new Vector2D(getPoint(OmnidirectionalLensPointType.C1).getPosition()),	// raysStartPoint
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
				ls.setLineConstrainingStartPoint(
						getC1Line()
						// new Line2D(cPoint, ol.getPoint(OmnidirectionalLensPointType.V1).getPosition())
					);
				panelWithPopup.iocs.add(ls);
				// panelWithPopup.graphicElements.addAll(ls.getGraphicElements());

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
