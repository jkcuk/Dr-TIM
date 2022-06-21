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

import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.Ray2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.geometry2D.LineSegment2D;
import optics.rayplay.graphicElements.IdealLensWormholePointGE2D;
import optics.rayplay.graphicElements.IdealLensWormholePointGE2D.IdealLensWormholePointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;
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
public class IdeaLensWormhole2D implements InteractiveOpticalComponent2D
{
	private String name;

	// the parameters -- see [1] for nomenclature

	/**
	 * the focal length of lens D (the base lens) outer (O) and inner (I) 
	 */
	private double fDO, fDI;

	/**
	 * the radius of lens D (the base lens) outer (O) and inner (I)
	 */
	private double rDO, rDI;

	/**
	 * height of lower inner vertex above lens D, outer (O) and inner (I)
	 */
	private double h1O, h1I;

	/**
	 * height of upper inner vertex above lens D, outer (O) and inner (I)
	 */
	private double h2O, h2I;

	/**
	 * height of top vertex above lens D, outer (O) and inner (I)
	 */
	private double hO, hI;

	/**
	 * the principal point of lens D (the base lens), P_D, outer (O) and inner (I)
	 */
	private Vector2D pDO, pDI;

	/**
	 * unit vector in the plane of lens D, pointing in the direction from P_D to V_1
	 */
	private Vector2D dHat;

	/**
	 * unit vector in the direction of the central axis, pointing from P_D to the other principal points
	 */
	private Vector2D cHat;
	
	/**
	 * Focal lengths of the four equivalent outside lenses (each pair shares a focal length)
	 */
	private double f1,f2;
	
	/**
	 * the position of the first image of the effective lenses. 
	 */
	private Vector2D pImg;
	
	/**
	 * The toggle for the equivalent lenses
	 */
	private boolean equivalentLenses = false;
	
	/**
	 * Toggle between having the images formed to size and showing the inside cloak image
	 */
	private boolean imageToSizeAndInsideImage = true;



	// internal variables
	
	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
	private ArrayList<OpticalComponent2D> opticalComponentsEquivalentLenses = new ArrayList<OpticalComponent2D>();

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
	public enum WormholeLensTypes
	{
		A1O("A1/03a"),
		A2O("A2/03b"),
		B1O("B1/2a3a"),
		B2O("B2/2b3b"),
		C1O("C1/12a"),
		C2O("C2/12b"),
		DO("D/01"),
		EO("E/2a2b"),
		FO("F/3a3b"),
		A1I("A1/03a"),
		A2I("A2/03b"),
		B1I("B1/2a3a"),
		B2I("B2/2b3b"),
		C1I("C1/12a"),
		C2I("C2/12b"),
		DI("D/01"),
		EI("E/2a2b"),
		FI("F/3a3b"),
		L1("first lens"),
		L2("second lens"),
		L3("third lens"),
		L4("fourth lens"),
		EL1("image of first lens"),
		EL2("image of second lens"),
		EL3("image of third lens"),
		EL4("image of fourth lens"),
		ELI("image of inner cloak base lens"),
		ELI1("image of inner cloak side lens"),
		ELI2("image of inner cloak side lens");

		public final String name;

		WormholeLensTypes(String name) {this.name = name;}
	}

	/**
	 * the lenses
	 */
	// private Lens2D lens[];
	private HashMap<WormholeLensTypes, Lens2D> lenses;

	public Lens2D getLens(WormholeLensTypes type)
	{
		return lenses.get(type);
	}

	private HashMap<IdealLensWormholePointType, PointGE2D> points;



	// constructor

	public IdeaLensWormhole2D(
			String name,
			double fDO,
			double rDO,
			double h1O,
			double h2O,
			double hO,
			Vector2D pDO,
			double fDI,
			double rDI,
			double h1I,
			double h2I,
			double hI,
			Vector2D pDI,
			double f1,
			double f2,
			Vector2D pImg,
			Vector2D dHat,
			Vector2D cHat
			)
	{
		this.name = name;
		this.fDO = fDO;
		this.rDO = rDO;
		this.h1O = h1O;
		this.h2O = h2O;
		this.hO = hO;
		this.pDO = pDO;
		this.fDI = fDI;
		this.rDI = rDI;
		this.h1I = h1I;
		this.h2I = h2I;
		this.hI = hI;
		this.pDI = pDI;
		this.f1 = f1;
		this.f2 = f2;
		this.pImg = pImg;
		this.dHat = dHat;
		this.cHat = cHat;

		// create the points, lines and the lenses, ...
		createInternalArrays();

		// ... and set their parameters
		calculateInternalParameters();
		
		initPopup();
	}


	// setters & getters
	public double getfDO() {
		return fDO;
	}


	public void setfDO(double fDO) {
		this.fDO = fDO;
	}


	public double getfDI() {
		return fDI;
	}


	public void setfDI(double fDI) {
		this.fDI = fDI;
	}


	public double getrDO() {
		return rDO;
	}


	public void setrDO(double rDO) {
		this.rDO = rDO;
	}


	public double getrDI() {
		return rDI;
	}


	public void setrDI(double rDI) {
		this.rDI = rDI;
	}


	public double getH1O() {
		return h1O;
	}


	public void setH1O(double h1o) {
		h1O = h1o;
	}


	public double getH1I() {
		return h1I;
	}


	public void setH1I(double h1i) {
		h1I = h1i;
	}


	public double getH2O() {
		return h2O;
	}


	public void setH2O(double h2o) {
		h2O = h2o;
	}


	public double getH2I() {
		return h2I;
	}


	public void setH2I(double h2i) {
		h2I = h2i;
	}


	public double gethO() {
		return hO;
	}


	public void sethO(double hO) {
		this.hO = hO;
	}


	public double gethI() {
		return hI;
	}


	public void sethI(double hI) {
		this.hI = hI;
	}


	public Vector2D getpDO() {
		return pDO;
	}


	public void setpDO(Vector2D pDO) {
		this.pDO = pDO;
	}


	public Vector2D getpDI() {
		return pDI;
	}


	public void setpDI(Vector2D pDI) {
		this.pDI = pDI;
	}


	public Vector2D getdHat() {
		return dHat;
	}


	public void setdHat(Vector2D dHat) {
		this.dHat = dHat;
	}


	public Vector2D getcHat() {
		return cHat;
	}


	public void setcHat(Vector2D cHat) {
		this.cHat = cHat;
	}


	public double getF1() {
		return f1;
	}


	public void setF1(double f1) {
		this.f1 = f1;
	}


	public double getF2() {
		return f2;
	}


	public void setF2(double f2) {
		this.f2 = f2;
	}
	
	public Vector2D getPImg() {
		return pImg;
	}
	
	public void setPImg(Vector2D pImg) {
		this.pImg = pImg;
	}

	
	public boolean isEquivalentLenses() {
		return equivalentLenses;
	}


	public void setEquivalentLenses(boolean equivalentLenses) {
		this.equivalentLenses = equivalentLenses;
	}


	public PointGE2D getPoint(IdealLensWormholePointType type)
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

	@Override
	public void move(Vector2D delta)
	{
		setpDO(
				Vector2D.sum(getpDO(), delta)
			);
		
		setpDI(Vector2D.sum(getpDI(),delta)
				);
		
		setPImg(Vector2D.sum(getPImg(),delta)
	);
		
		calculateInternalParameters();
	}

	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents()
	{ if(equivalentLenses) {
		return opticalComponentsEquivalentLenses;
	}
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
		printStream.println("\nWormhole lens \""+ name +"\"\n");

		printStream.println("  fDO = "+fDO);
		printStream.println("  baseLensRadiusO = "+rDO);
		printStream.println("  h1O = "+h1O);
		printStream.println("  h2O = "+h2O);
		printStream.println("  hO = "+hO);
		printStream.println("  pDO = "+pDO);
		printStream.println("  fDI = "+fDI);
		printStream.println("  baseLensRadiusI = "+rDI);
		printStream.println("  h1I = "+h1I);
		printStream.println("  h2I = "+h2I);
		printStream.println("  hI = "+hI);
		printStream.println("  pDI = "+pDI);
		
		
		printStream.println("  dHat = "+dHat);
		printStream.println("  cHat = "+cHat);
		
		printStream.println("  Lens focal lengths Outer:");
		printStream.println("  	 fAO = "+getLens(WormholeLensTypes.A1O).getFocalLength());
		printStream.println("    fBO = "+getLens(WormholeLensTypes.B1O).getFocalLength());
		printStream.println("    fCO = "+getLens(WormholeLensTypes.C1O).getFocalLength());
		printStream.println("    fDO = "+getLens(WormholeLensTypes.DO).getFocalLength());
		printStream.println("    fEO = "+getLens(WormholeLensTypes.EO).getFocalLength());
		printStream.println("    fFO = "+getLens(WormholeLensTypes.FO).getFocalLength());
		printStream.println("  Lens focal lengths Inner:");
		printStream.println("  	 fAI = "+getLens(WormholeLensTypes.A1I).getFocalLength());
		printStream.println("    fBI = "+getLens(WormholeLensTypes.B1I).getFocalLength());
		printStream.println("    fCI = "+getLens(WormholeLensTypes.C1I).getFocalLength());
		printStream.println("    fDI = "+getLens(WormholeLensTypes.DI).getFocalLength());
		printStream.println("    fEI = "+getLens(WormholeLensTypes.EI).getFocalLength());
		printStream.println("    fFI = "+getLens(WormholeLensTypes.FI).getFocalLength());
		printStream.println("  Inside Lens focal lengths:");
		printStream.println("  	 f1 = "+getLens(WormholeLensTypes.L1).getFocalLength());
		printStream.println("    f2 = "+getLens(WormholeLensTypes.L2).getFocalLength());
		printStream.println("    f3 = "+getLens(WormholeLensTypes.L3).getFocalLength());
		printStream.println("    f4 = "+getLens(WormholeLensTypes.L4).getFocalLength());


	}
	

	// the meat
	
	public Line2D getCentralSymmetryAxis()
	{
		return new Line2D(pDO, Vector2D.sum(pDO, cHat));
	}
	
	public Line2D getLineOfLensDO()
	{
		return new Line2D(pDO, Vector2D.sum(pDO, dHat));
	}
	
	public Line2D getLineOfLensDI()
	{
		return new Line2D(pDI, Vector2D.sum(pDI, dHat));
	}

	private void createInternalArrays()
	{
		// initialise the array of points
		points = new HashMap<IdealLensWormholePointType, PointGE2D>();
		
		// and add them all to the list graphicElements
		for(IdealLensWormholePointType iLWPointType : IdealLensWormholePointType.values())
		{
			IdealLensWormholePointGE2D point = new IdealLensWormholePointGE2D(iLWPointType.name, this, iLWPointType);
			points.put(iLWPointType, point);
			controlGraphicElements.add(point);
		}
			


		
		
		// initialise the array of lenses
		// lens = new Lens2D[ilwLensType.values().length];
		lenses = new HashMap<WormholeLensTypes, Lens2D>();
		for(WormholeLensTypes iLWLensType : WormholeLensTypes.values())
		{
			// create the lens, ...
			Lens2D l = new Lens2D("Lens \""+iLWLensType.toString()+"\" in \""+name+"\"");

			// ... add it to the array of lenses, ...
			lenses.put(iLWLensType, l);

			// ... and add it to the relevant list of optical components and the list of graphic elements
			switch(iLWLensType){
			case EL1:
			case EL2:
			case EL3:
			case EL4:
				opticalComponentsEquivalentLenses.add(l);
				break;
			case ELI:
			case ELI1:
			case ELI2:
				break;
			default:
				opticalComponents.add(l);
			}
			displayGraphicElements.add(l);
		}
		
	}

	public void calculateInternalParameters()
	{
		
		
		BasicStroke imageLensStroke, lensStroke, innerLensStroke;
		Colour lensColour, imageColour;
		
		if(equivalentLenses) {
			imageLensStroke = new BasicStroke(3);
			lensStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
			innerLensStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
			lensColour = Colour.ORANGE;
			imageColour = Colour.CYAN;
		}else {
			imageLensStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
			lensStroke = new BasicStroke(3);
			innerLensStroke = new BasicStroke(1);
			lensColour = Colour.CYAN;
			imageColour = Colour.ORANGE;
		}
		
		
		//outer
		// the bottom vertices
		points.get(IdealLensWormholePointType.VO1).setCoordinatesToThoseOf(Vector2D.sum(pDO, dHat.getProductWith(-rDO)));
		points.get(IdealLensWormholePointType.VO2).setCoordinatesToThoseOf(Vector2D.sum(pDO, dHat.getProductWith( rDO)));

		// the lower inner vertex
		points.get(IdealLensWormholePointType.PO1).setCoordinatesToThoseOf(Vector2D.sum(pDO, cHat.getProductWith(h1O)));

		// the upper inner vertex
		points.get(IdealLensWormholePointType.PO2).setCoordinatesToThoseOf(
				// new Vector2D(0.2, 0.4)
				Vector2D.sum(pDO, cHat.getProductWith(h2O))
				);

		// the top vertex
		points.get(IdealLensWormholePointType.PO3).setCoordinatesToThoseOf(Vector2D.sum(pDO, cHat.getProductWith(hO)));

		// the principal point of lens D
		points.get(IdealLensWormholePointType.PO0).setCoordinatesToThoseOf(pDO);

		// the (inside) focal point of lens D
		points.get(IdealLensWormholePointType.FOD).setCoordinatesToThoseOf(Vector2D.sum(pDO, cHat.getProductWith(fDO)));
		
		//inner
		// the bottom vertices
		points.get(IdealLensWormholePointType.VI1).setCoordinatesToThoseOf(Vector2D.sum(pDI, dHat.getProductWith(-rDI)));
		points.get(IdealLensWormholePointType.VI2).setCoordinatesToThoseOf(Vector2D.sum(pDI, dHat.getProductWith( rDI)));

		// the lower inner vertex
		points.get(IdealLensWormholePointType.PI1).setCoordinatesToThoseOf(Vector2D.sum(pDI, cHat.getProductWith(h1I)));

		// the upper inner vertex
		points.get(IdealLensWormholePointType.PI2).setCoordinatesToThoseOf(
				// new Vector2D(0.2, 0.4)
				Vector2D.sum(pDI, cHat.getProductWith(h2I))
				);

		// the top vertex
		points.get(IdealLensWormholePointType.PI3).setCoordinatesToThoseOf(Vector2D.sum(pDI, cHat.getProductWith(hI)));

		// the principal point of lens D
		points.get(IdealLensWormholePointType.PI0).setCoordinatesToThoseOf(pDI);

		// the (inside) focal point of lens D
		points.get(IdealLensWormholePointType.FID).setCoordinatesToThoseOf(Vector2D.sum(pDI, cHat.getProductWith(fDI)));
		
		
		//Outer
		// calculate the focal lengths from fD and r, h1, h2, h -- see AllLoopTheorems 2D.nb
		double r2O = rDO*rDO;
		double fAO = ((-fDO*hO + (fDO + hO)*h1O)*(hO - h2O)*rDO)/(h1O*h2O*Math.sqrt(hO*hO + r2O));
		double fBO = (fDO*(hO - h2O)*(-h1O + h2O)*rDO)/(hO*h1O*Math.sqrt(h2O*h2O + r2O));
		double fCO = ((fDO*hO - (fDO + hO)*h1O)*(h1O - h2O)*rDO)/(hO*h2O*Math.sqrt(h1O*h1O + r2O));
		double fEO = ((fDO*hO - (fDO + hO)*h1O)*(h1O - h2O)*rDO)/(2*hO*h1O*h2O);
		double fFO = ((fDO*hO - (fDO + hO)*h1O)*(hO  - h2O)*rDO)/(2*hO*h1O*h2O);
		
		//inner
		// calculate the focal lengths from fD and r, h1, h2, h -- see AllLoopTheorems 2D.nb
		double r2I = rDI*rDI;
		double fAI = ((-fDI*hI + (fDI + hI)*h1I)*(hI - h2I)*rDI)/(h1I*h2I*Math.sqrt(hI*hI + r2I));
		double fBI = (fDI*(hI - h2I)*(-h1I + h2I)*rDI)/(hI*h1I*Math.sqrt(h2I*h2I + r2I));
		double fCI = ((fDI*hI - (fDI + hI)*h1I)*(h1I - h2I)*rDI)/(hI*h2I*Math.sqrt(h1I*h1I + r2I));
		double fEI = ((fDI*hI - (fDI + hI)*h1I)*(h1I - h2I)*rDI)/(2*hI*h1I*h2I);
		double fFI = ((fDI*hI - (fDI + hI)*h1I)*(hI  - h2I)*rDI)/(2*hI*h1I*h2I);
		
		//inside lenses calculations... These are a bit big and repetitive so I made a method below...
		Vector2D L1Img = pImg;
		Vector2D L2Img = Vector2D.sum(L1Img, cHat.getProductWith(-(f1+f2)));
		Vector2D L3Img = Vector2D.sum(L2Img, cHat.getProductWith(-((f2/f1)*(f1+f2))));
		Vector2D L4Img = Vector2D.sum(L3Img, cHat.getProductWith(-(f1+f2)));
		
		Vector2D L1P= new Vector2D(InsideXcoord(L1Img, f1, cHat),InsideYcoord(L1Img, f1, cHat));
		Vector2D L2P= new Vector2D(InsideXcoord(L2Img, f2, cHat),InsideYcoord(L2Img, f2, cHat));
		Vector2D L3P = new Vector2D(InsideXcoord(L3Img, f2, cHat),InsideYcoord(L3Img, f2, cHat));
		Vector2D L4P = new Vector2D(InsideXcoord(L4Img, f1, cHat), InsideYcoord(L4Img, f1, cHat));
		
		double f1P = InsideFocalLengths(L1Img, f1, cHat);
		double f2P = InsideFocalLengths(L2Img, f2, cHat);
		double f3P = InsideFocalLengths(L3Img, f2, cHat);
		double f4P = InsideFocalLengths(L4Img, f1, cHat);
		
		Vector2D p1ImgCentre = new LineSegment2D(endPointImage(endPoint(L1P,dHat), cHat), endPointImage(endPoint(L1P,dHat.getProductWith(-1)), cHat)).getMidpoint();
		//the position of the first outer image lens
		points.get(IdealLensWormholePointType.IMGP).setCoordinatesToThoseOf(p1ImgCentre);//pImg);
		//the focal point of this first image lens left of lens centre
		Vector2D pF1 = Vector2D.sum(p1ImgCentre, dHat.getProductWith(-0.1*rDI));
		points.get(IdealLensWormholePointType.IMGF1).setCoordinatesToThoseOf(Vector2D.sum(pF1, cHat.getProductWith(f1)));
		
		//the focal point of the second image lens right of lens centre
		Vector2D pF2 = Vector2D.sum(p1ImgCentre, dHat.getProductWith(0.1*rDI));
		points.get(IdealLensWormholePointType.IMGF2).setCoordinatesToThoseOf(Vector2D.sum(pF2, cHat.getProductWith(f2)));
		

		//outer
		// lenses A
		lenses.get(WormholeLensTypes.A1O).setEndPoints(points.get(IdealLensWormholePointType.VO1).getPosition(), points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.A1O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.A1O).setFocalLength(fAO);
		lenses.get(WormholeLensTypes.A1O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.A1O).setColour(lensColour);

		lenses.get(WormholeLensTypes.A2O).setEndPoints(points.get(IdealLensWormholePointType.VO2).getPosition(), points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.A2O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.A2O).setFocalLength(fAO);
		lenses.get(WormholeLensTypes.A2O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.A2O).setColour(lensColour);

		// lenses B
		lenses.get(WormholeLensTypes.B1O).setEndPoints(points.get(IdealLensWormholePointType.VO1).getPosition(), points.get(IdealLensWormholePointType.PO2).getPosition());
		lenses.get(WormholeLensTypes.B1O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO2).getPosition());
		lenses.get(WormholeLensTypes.B1O).setFocalLength(fBO);
		lenses.get(WormholeLensTypes.B1O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.B1O).setColour(lensColour);

		lenses.get(WormholeLensTypes.B2O).setEndPoints(points.get(IdealLensWormholePointType.VO2).getPosition(), points.get(IdealLensWormholePointType.PO2).getPosition());
		lenses.get(WormholeLensTypes.B2O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO2).getPosition());
		lenses.get(WormholeLensTypes.B2O).setFocalLength(fBO);
		lenses.get(WormholeLensTypes.B2O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.B2O).setColour(lensColour);

		// lenses C
		lenses.get(WormholeLensTypes.C1O).setEndPoints(points.get(IdealLensWormholePointType.VO1).getPosition(), points.get(IdealLensWormholePointType.PO1).getPosition());
		lenses.get(WormholeLensTypes.C1O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO1).getPosition());
		lenses.get(WormholeLensTypes.C1O).setFocalLength(fCO);
		lenses.get(WormholeLensTypes.C1O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.C1O).setColour(lensColour);

		lenses.get(WormholeLensTypes.C2O).setEndPoints(points.get(IdealLensWormholePointType.VO2).getPosition(), points.get(IdealLensWormholePointType.PO1).getPosition());
		lenses.get(WormholeLensTypes.C2O).setPrincipalPoint(points.get(IdealLensWormholePointType.PO1).getPosition());
		lenses.get(WormholeLensTypes.C2O).setFocalLength(fCO);
		lenses.get(WormholeLensTypes.C2O).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.C2O).setColour(lensColour);

		// the "base lens", lens D
		lenses.get(WormholeLensTypes.DO).setEndPoints(points.get(IdealLensWormholePointType.VO1).getPosition(), points.get(IdealLensWormholePointType.VO2).getPosition());
		lenses.get(WormholeLensTypes.DO).setPrincipalPoint(points.get(IdealLensWormholePointType.PO0).getPosition());
		lenses.get(WormholeLensTypes.DO).setFocalLength(fDO);
		lenses.get(WormholeLensTypes.DO).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.DO).setColour(lensColour);

		// lens E
		lenses.get(WormholeLensTypes.EO).setEndPoints(points.get(IdealLensWormholePointType.PO1).getPosition(), points.get(IdealLensWormholePointType.PO2).getPosition());
		lenses.get(WormholeLensTypes.EO).setPrincipalPoint(points.get(IdealLensWormholePointType.PO1).getPosition());
		lenses.get(WormholeLensTypes.EO).setFocalLength(fEO);
		lenses.get(WormholeLensTypes.EO).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.EO).setColour(lensColour);

		// lens F
		lenses.get(WormholeLensTypes.FO).setEndPoints(points.get(IdealLensWormholePointType.PO2).getPosition(), points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.FO).setPrincipalPoint(points.get(IdealLensWormholePointType.PO3).getPosition());
		lenses.get(WormholeLensTypes.FO).setFocalLength(fFO);
		lenses.get(WormholeLensTypes.FO).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.FO).setColour(lensColour);
		
		
		//inner
		// lenses A
		lenses.get(WormholeLensTypes.A1I).setEndPoints(points.get(IdealLensWormholePointType.VI1).getPosition(), points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.A1I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.A1I).setFocalLength(fAI);
		lenses.get(WormholeLensTypes.A1I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.A1I).setColour(lensColour);

		lenses.get(WormholeLensTypes.A2I).setEndPoints(points.get(IdealLensWormholePointType.VI2).getPosition(), points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.A2I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.A2I).setFocalLength(fAI);
		lenses.get(WormholeLensTypes.A2I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.A2I).setColour(lensColour);

		// lenses B
		lenses.get(WormholeLensTypes.B1I).setEndPoints(points.get(IdealLensWormholePointType.VI1).getPosition(), points.get(IdealLensWormholePointType.PI2).getPosition());
		lenses.get(WormholeLensTypes.B1I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI2).getPosition());
		lenses.get(WormholeLensTypes.B1I).setFocalLength(fBI);
		lenses.get(WormholeLensTypes.B1I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.B1I).setColour(lensColour);

		lenses.get(WormholeLensTypes.B2I).setEndPoints(points.get(IdealLensWormholePointType.VI2).getPosition(), points.get(IdealLensWormholePointType.PI2).getPosition());
		lenses.get(WormholeLensTypes.B2I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI2).getPosition());
		lenses.get(WormholeLensTypes.B2I).setFocalLength(fBI);
		lenses.get(WormholeLensTypes.B2I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.B2I).setColour(lensColour);

		// lenses C
		lenses.get(WormholeLensTypes.C1I).setEndPoints(points.get(IdealLensWormholePointType.VI1).getPosition(), points.get(IdealLensWormholePointType.PI1).getPosition());
		lenses.get(WormholeLensTypes.C1I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI1).getPosition());
		lenses.get(WormholeLensTypes.C1I).setFocalLength(fCI);
		lenses.get(WormholeLensTypes.C1I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.C1I).setColour(lensColour);

		lenses.get(WormholeLensTypes.C2I).setEndPoints(points.get(IdealLensWormholePointType.VI2).getPosition(), points.get(IdealLensWormholePointType.PI1).getPosition());
		lenses.get(WormholeLensTypes.C2I).setPrincipalPoint(points.get(IdealLensWormholePointType.PI1).getPosition());
		lenses.get(WormholeLensTypes.C2I).setFocalLength(fCI);
		lenses.get(WormholeLensTypes.C2I).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.C2I).setColour(lensColour);

		// the "base lens", lens D
		lenses.get(WormholeLensTypes.DI).setEndPoints(points.get(IdealLensWormholePointType.VI1).getPosition(), points.get(IdealLensWormholePointType.VI2).getPosition());
		lenses.get(WormholeLensTypes.DI).setPrincipalPoint(points.get(IdealLensWormholePointType.PI0).getPosition());
		lenses.get(WormholeLensTypes.DI).setFocalLength(fDI);
		lenses.get(WormholeLensTypes.DI).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.DI).setColour(lensColour);

		// lens E
		lenses.get(WormholeLensTypes.EI).setEndPoints(points.get(IdealLensWormholePointType.PI1).getPosition(), points.get(IdealLensWormholePointType.PI2).getPosition());
		lenses.get(WormholeLensTypes.EI).setPrincipalPoint(points.get(IdealLensWormholePointType.PI1).getPosition());
		lenses.get(WormholeLensTypes.EI).setFocalLength(fEI);
		lenses.get(WormholeLensTypes.EI).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.EI).setColour(lensColour);

		// lens F
		lenses.get(WormholeLensTypes.FI).setEndPoints(points.get(IdealLensWormholePointType.PI2).getPosition(), points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.FI).setPrincipalPoint(points.get(IdealLensWormholePointType.PI3).getPosition());
		lenses.get(WormholeLensTypes.FI).setFocalLength(fFI);
		lenses.get(WormholeLensTypes.FI).setStroke(lensStroke);
		lenses.get(WormholeLensTypes.FI).setColour(lensColour);
		
		//Inside lenses
		
		//lens 1
		lenses.get(WormholeLensTypes.L1).setEndPoints(endPoint(L1P,dHat), endPoint(L1P,dHat.getProductWith(-1)));
		lenses.get(WormholeLensTypes.L1).setPrincipalPoint(L1P);
		lenses.get(WormholeLensTypes.L1).setFocalLength(f1P);
		lenses.get(WormholeLensTypes.L1).setStroke(innerLensStroke);
		lenses.get(WormholeLensTypes.L1).setColour(lensColour);
		
		
		//lens 2
		lenses.get(WormholeLensTypes.L2).setEndPoints(endPoint(L2P,dHat), endPoint(L2P,dHat.getProductWith(-1)));
		lenses.get(WormholeLensTypes.L2).setPrincipalPoint(L2P);
		lenses.get(WormholeLensTypes.L2).setFocalLength(f2P);
		lenses.get(WormholeLensTypes.L2).setStroke(innerLensStroke);
		lenses.get(WormholeLensTypes.L2).setColour(lensColour);
		
		//lens 3
		lenses.get(WormholeLensTypes.L3).setEndPoints(endPoint(L3P,dHat), endPoint(L3P,dHat.getProductWith(-1)));
		lenses.get(WormholeLensTypes.L3).setPrincipalPoint(L3P);
		lenses.get(WormholeLensTypes.L3).setFocalLength(f3P);
		lenses.get(WormholeLensTypes.L3).setStroke(innerLensStroke);
		lenses.get(WormholeLensTypes.L3).setColour(lensColour);
		
		//lens 4
		lenses.get(WormholeLensTypes.L4).setEndPoints(endPoint(L4P,dHat), endPoint(L4P,dHat.getProductWith(-1)));
		lenses.get(WormholeLensTypes.L4).setPrincipalPoint(L4P);
		lenses.get(WormholeLensTypes.L4).setFocalLength(f4P);
		lenses.get(WormholeLensTypes.L4).setStroke(innerLensStroke);
		lenses.get(WormholeLensTypes.L4).setColour(lensColour);
		
		
		//Add a switch to toggle the endpoints to either be the image or make them look nice but not representative of the image size.
		if(imageToSizeAndInsideImage) {
			lenses.get(WormholeLensTypes.EL1).setEndPoints(endPointImage(endPoint(L1P,dHat), cHat), endPointImage(endPoint(L1P,dHat.getProductWith(-1)), cHat));  //Vector2D.sum(L1Img, dHat.getProductWith(-rDI)), Vector2D.sum(L1Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL2).setEndPoints(endPointImage(endPoint(L2P,dHat), cHat), endPointImage(endPoint(L2P,dHat.getProductWith(-1)), cHat));//Vector2D.sum(L2Img, dHat.getProductWith(-rDI)), Vector2D.sum(L2Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL3).setEndPoints(endPointImage(endPoint(L3P,dHat), cHat), endPointImage(endPoint(L3P,dHat.getProductWith(-1)), cHat));//Vector2D.sum(L3Img, dHat.getProductWith(-rDI)), Vector2D.sum(L3Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL4).setEndPoints(endPointImage(endPoint(L4P,dHat), cHat), endPointImage(endPoint(L4P,dHat.getProductWith(-1)), cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			
			//create an image of the inner cloak 
			lenses.get(WormholeLensTypes.ELI).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI2).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI).setColour(Colour.RED);
			
			lenses.get(WormholeLensTypes.ELI1).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.PI3).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI1).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI1).setColour(Colour.RED);
			
			lenses.get(WormholeLensTypes.ELI2).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI2).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.PI3).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI2).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI2).setColour(Colour.RED);
		}else {
			lenses.get(WormholeLensTypes.EL1).setEndPoints(Vector2D.sum(L1Img, dHat.getProductWith(-rDI)), Vector2D.sum(L1Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL2).setEndPoints(Vector2D.sum(L2Img, dHat.getProductWith(-rDI)), Vector2D.sum(L2Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL3).setEndPoints(Vector2D.sum(L3Img, dHat.getProductWith(-rDI)), Vector2D.sum(L3Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.EL4).setEndPoints(Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			
			//create an image of the inner cloak 
			lenses.get(WormholeLensTypes.ELI).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI).setStroke(new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI).setColour(Colour.RED);
			
			lenses.get(WormholeLensTypes.ELI1).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI1).setStroke(new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI1).setColour(Colour.RED);
			
			lenses.get(WormholeLensTypes.ELI2).setEndPoints(endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(), cHat), endPointImageInnerCloak(points.get(IdealLensWormholePointType.VI1).getPosition(),cHat));//Vector2D.sum(L4Img, dHat.getProductWith(-rDI)), Vector2D.sum(L4Img, dHat.getProductWith(rDI)));
			lenses.get(WormholeLensTypes.ELI2).setStroke(new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
			lenses.get(WormholeLensTypes.ELI2).setColour(Colour.RED);
		}
		
		//the equivalent lenses
		lenses.get(WormholeLensTypes.EL1).setPrincipalPoint(L1Img);
		lenses.get(WormholeLensTypes.EL1).setFocalLength(f1);
		lenses.get(WormholeLensTypes.EL1).setStroke(imageLensStroke);
		lenses.get(WormholeLensTypes.EL1).setColour(imageColour);
		
		lenses.get(WormholeLensTypes.EL2).setPrincipalPoint(L2Img);
		lenses.get(WormholeLensTypes.EL2).setFocalLength(f2);
		lenses.get(WormholeLensTypes.EL2).setStroke(imageLensStroke);
		lenses.get(WormholeLensTypes.EL2).setColour(imageColour);
		
		lenses.get(WormholeLensTypes.EL3).setPrincipalPoint(L3Img);
		lenses.get(WormholeLensTypes.EL3).setFocalLength(f2);
		lenses.get(WormholeLensTypes.EL3).setStroke(imageLensStroke);
		lenses.get(WormholeLensTypes.EL3).setColour(imageColour);
		
		lenses.get(WormholeLensTypes.EL4).setPrincipalPoint(L4Img);
		lenses.get(WormholeLensTypes.EL4).setFocalLength(f1);
		lenses.get(WormholeLensTypes.EL4).setStroke(imageLensStroke);
		lenses.get(WormholeLensTypes.EL4).setColour(imageColour);
		
	}
	
	
		

		//for the math check the mathematica document... but it was simplified by changing to some vector notation
	public double InsideFocalLengths(Vector2D posLens, double focalLength, Vector2D normal) {
		double fInside;
		//the calculations...
		double P50 = Vector2D.scalarProduct(Vector2D.difference(posLens, pDO), normal);//cHat or dHat
		double sf = P50+fDO;
		
		fInside = focalLength*(Math.pow(fDI, 2))*(Math.pow(fDO, 2))/(
				Math.pow(sf, 2)*
				Math.pow((fDI+ Vector2D.scalarProduct(Vector2D.sum(pDO.getProductWith(P50),posLens.getProductWith(fDO),pDI.getProductWith(-sf)), normal)/sf
						) , 2));
		return fInside;
	}
	
	public double InsideXcoord(Vector2D posLens, double focalLength, Vector2D normal) {
		double xInside;
		double P50 = Vector2D.scalarProduct(Vector2D.difference(posLens, pDO), normal);//cHat or dHat
		double sf = P50+fDO;
		xInside = (pDI.x*Vector2D.scalarProduct(Vector2D.sum(pDO.getProductWith(P50/sf),posLens.getProductWith(fDO/sf),pDI.getProductWith(-1)),normal)+ 
				fDI*(fDO*posLens.x+P50*pDO.x)/sf)/
				(fDI + Vector2D.scalarProduct(Vector2D.sum(pDO.getProductWith(P50/sf),posLens.getProductWith(fDO/sf),pDI.getProductWith(-1)),normal))
				;
		return xInside;
	}
	
	public double InsideYcoord(Vector2D posLens, double focalLength, Vector2D normal) {
		double yInside;
		double P50 = Vector2D.scalarProduct(Vector2D.difference(posLens, pDO), normal);//cHat or dHat
		double sf = P50+fDO;
		yInside = (pDI.y*Vector2D.scalarProduct(Vector2D.sum(pDO.getProductWith(P50/sf),posLens.getProductWith(fDO/sf),pDI.getProductWith(-1)),normal)+ 
				fDI*(fDO*posLens.y+P50*pDO.y)/sf)/
				(fDI + Vector2D.scalarProduct(Vector2D.sum(pDO.getProductWith(P50/sf),posLens.getProductWith(fDO/sf),pDI.getProductWith(-1)),normal))
				;
		return yInside;
	}
	
	public Vector2D endPoint(Vector2D insideLensPos, Vector2D direction) {
		Vector2D endPoint;
		Vector2D pointAlongCentralInnerAxis = Geometry2D.getPointOnLineClosestToPoint(new Line2D(pDI, Vector2D.sum(pDI,cHat)),insideLensPos);
		double rad = (rDI*(h1I-Vector2D.distance(pDI, pointAlongCentralInnerAxis)))/h1I;
		endPoint = Vector2D.sum(pointAlongCentralInnerAxis, direction.getProductWith(rad));
		return endPoint;
	}
	
	public Vector2D endPointImage(Vector2D insideLensPosEndPoint, Vector2D normal) {
		Vector2D endPointImage;
		
		
		double sf = fDI + pDI.x*normal.x - insideLensPosEndPoint.x * normal.x + pDI.y*normal.y - insideLensPosEndPoint.y*normal.y;
		double xfactor = fDI*insideLensPosEndPoint.x+ pDI.x*pDI.x*normal.x - pDI.x*insideLensPosEndPoint.x*normal.x +pDI.x*pDI.y*normal.y-pDI.x*insideLensPosEndPoint.y*normal.y; 
		double yfactor = fDI*insideLensPosEndPoint.y + pDI.x*normal.x*pDI.y - insideLensPosEndPoint.x*normal.x*pDI.y+pDI.y*pDI.y*normal.y-pDI.y*insideLensPosEndPoint.y*normal.y;

		double xPos = ( normal.x*pDO.x*pDO.x + fDO*(xfactor/sf)-normal.x*pDO.x*(xfactor/sf) - normal.y*pDO.x*(yfactor/sf)+pDO.x*normal.y*pDO.y)/
				( fDO+ normal.x*pDO.x - normal.x*(xfactor/sf) - normal.y*(yfactor/sf)+normal.y*pDO.y);
		
		double yPos = (fDO*(yfactor/sf)+ normal.x*pDO.x*pDO.y - normal.x*pDO.y*(xfactor/sf) - normal.y*pDO.y*(yfactor/sf) +normal.y*pDO.y*pDO.y)/
				( fDO+ normal.x*pDO.x - normal.x*(xfactor/sf)- normal.y*(yfactor/sf) + normal.y*pDO.y);
		
//		double sf = fDI + pDI.x*cHat.x - insideLensPosEndPoint.x * cHat.x + pDI.y*cHat.y - insideLensPosEndPoint.y*cHat.y;
//		double xfactor = fDI*insideLensPosEndPoint.x+ pDI.x*pDI.x*cHat.x - pDI.x*insideLensPosEndPoint.x*cHat.x +pDI.x*pDI.y*cHat.y-pDI.x*insideLensPosEndPoint.y*cHat.y; 
//		double yfactor = fDI*insideLensPosEndPoint.y + pDI.x*cHat.x*pDI.y - insideLensPosEndPoint.y*cHat.x*pDI.y+pDI.y*pDI.y*cHat.y-pDI.y*insideLensPosEndPoint.y*cHat.y;
//
//		double xPos = ( cHat.x*pDO.x*pDO.x + fDO*(xfactor/sf)-cHat.x*pDO.x*(xfactor/sf) - cHat.y*pDO.x*(yfactor/sf)+pDO.x*cHat.y*pDO.y)/
//				( fDO+ cHat.x*pDO.x - cHat.x*(xfactor/sf) - cHat.y*(yfactor/sf)+cHat.y*pDO.y);
//		
//		double yPos = (fDO*(yfactor/sf)+ cHat.x*pDO.x*pDO.y - cHat.x*pDO.y*(xfactor/sf) - cHat.y*pDO.y*(yfactor/sf) +cHat.y*pDO.y*pDO.y)/
//				( fDO+ cHat.x*pDO.x - cHat.x*(xfactor/sf)- cHat.y*(yfactor/sf) + cHat.y*pDO.y);
		
		endPointImage = new Vector2D(xPos,yPos);
		return endPointImage;
	}
	
	public Vector2D endPointImageInnerCloak(Vector2D innerPosition, Vector2D normal) {
		Vector2D endPointImageInnerCloak;
		double xPos = ( fDO*innerPosition.x + pDO.x*pDO.x*normal.x - pDO.x*innerPosition.x*normal.x+pDO.x*pDO.y*normal.y - pDO.x*innerPosition.y*normal.y)/
				(fDO + pDO.x*normal.x- innerPosition.x*normal.x+pDO.y*normal.y - innerPosition.y*normal.y);
		double yPos = ( fDO*innerPosition.y + pDO.x*pDO.y*normal.x - pDO.y*innerPosition.x*normal.x+pDO.y*pDO.y*normal.y - pDO.y*innerPosition.y*normal.y)/
				(fDO + pDO.x*normal.x- innerPosition.x*normal.x+pDO.y*normal.y - innerPosition.y*normal.y);;
		
		endPointImageInnerCloak = new Vector2D (xPos, yPos);
		return endPointImageInnerCloak;
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
	public ArrayList<Ray2D> getRays() {
		return Ray2D.NO_RAYS;
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
		JMenuItem deleteWHMenuItem = new JMenuItem("Delete wormhole");
		deleteWHMenuItem.setEnabled(true);
		deleteWHMenuItem.getAccessibleContext().setAccessibleDescription("Delete wormhole");
		deleteWHMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(IdeaLensWormhole2D.this);
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
				
				panelWithPopup.iocs.remove(IdeaLensWormhole2D.this);
				
				// and add all the lenses
				for(WormholeLensTypes ilwLensType:WormholeLensTypes.values())
				{
					Lens2DIOC lens = new Lens2DIOC(getLens(ilwLensType));
					panelWithPopup.iocs.add(lens);
					// panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
				}
				
				panelWithPopup.repaint();
			}
		});
		popup.add(turnIntoIndividualLensesMenuItem);

		// Separator
	    popup.addSeparator();
	    //Equivalent 
	    JMenuItem equivLensesMenuItem = new JMenuItem("Toggle equivalent lenses");
	    equivLensesMenuItem.setEnabled(true);
	    equivLensesMenuItem.getAccessibleContext().setAccessibleDescription("Toggle equivalent lenses");
	    equivLensesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				equivalentLenses = !equivalentLenses;
				calculateInternalParameters();
				panelWithPopup.repaint();
				
			}
		});
		popup.add(equivLensesMenuItem);		
	
	// Separator
    popup.addSeparator();
    //Equivalent 
    JMenuItem imagesMenuItem = new JMenuItem("Toggle image sizes between real and simplified");
    imagesMenuItem.setEnabled(true);
    imagesMenuItem.getAccessibleContext().setAccessibleDescription("Toggle image sizes");
    imagesMenuItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			imageToSizeAndInsideImage = !imageToSizeAndInsideImage;
			calculateInternalParameters();
			panelWithPopup.repaint();
			
		}
	});
	popup.add(imagesMenuItem);		
}
	
	private void updatePopup()
	{
		// enable/disable + text
	}	

}
