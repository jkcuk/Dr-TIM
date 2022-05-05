package optics.rayplay.interactiveOpticalComponents;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.geometry2D.Line2D;
import optics.rayplay.graphicElements.OmnidirectionalLensPointGE2D;
import optics.rayplay.graphicElements.OmnidirectionalLensPointGE2D.OmnidirectionalLensPointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;

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



	// internal variables

	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();

	/**
	 * list of graphic elements
	 */
	private ArrayList<GraphicElement2D> graphicElements = new ArrayList<GraphicElement2D>();

	/**
	 * The lens types.
	 * For nomenclature see Fig. 7 in [1]
	 * @author johannes
	 */
	public enum OmnidirectionalLensLensType
	{
		A1("A1"),
		A2("A2"),
		B1("B1"),
		B2("B2"),
		C1("C1"),
		C2("C2"),
		D("D"),
		E("E"),
		F("F");

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

	// private Vector2D point[];



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
			Vector2D cHat
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

		// create the points, ...
		createPoints();

		// ..., the lenses, ...
		createLenses();

		// ... and set their parameters
		calculatePointParameters();
		calculateLensParameters();
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

	public PointGE2D getPoint(OmnidirectionalLensPointType type)
	{
		return points.get(type);
	}




	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public String getName() {
		return null;
	}

	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents()
	{
		return opticalComponents;
	}

	@Override
	public ArrayList<GraphicElement2D> getGraphicElements()
	{
		return graphicElements;
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
		
		printStream.println("  Lens focal lengths:");
		printStream.println("  	 fA = "+getLens(OmnidirectionalLensLensType.A1).getFocalLength());
		printStream.println("    fB = "+getLens(OmnidirectionalLensLensType.B1).getFocalLength());
		printStream.println("    fC = "+getLens(OmnidirectionalLensLensType.C1).getFocalLength());
		printStream.println("    fD = "+getLens(OmnidirectionalLensLensType.D).getFocalLength());
		printStream.println("    fE = "+getLens(OmnidirectionalLensLensType.E).getFocalLength());
		printStream.println("    fF = "+getLens(OmnidirectionalLensLensType.F).getFocalLength());

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

	private void createPoints()
	{
		// initialise the array of points
		points = new HashMap<OmnidirectionalLensPointType, PointGE2D>();

		// and add them all to the list graphicElements
		for(OmnidirectionalLensPointType olPointType : OmnidirectionalLensPointType.values())
		{
			OmnidirectionalLensPointGE2D point = new OmnidirectionalLensPointGE2D(olPointType.name, this, olPointType);
			points.put(olPointType, point);
			graphicElements.add(point);
			// TODO
			// if(olPointType == OLPointType.P0) setPosition(pD);
//			switch(olPointType)
//			{
//			case P0:
//				// P0 can be moved freely
//				OLP0GE2D p0 = new OLP0GE2D(olPointType.name, pD, this, olPointType);
//				points.put(olPointType, p0);
//				graphicElements.add(p0);
//				break;
//			case P3:
//				// P3 can be moved freely
//				OLPointGE2D p3 = new OLPointGE2D(olPointType.name, this, olPointType);
//				p3.setRadius(4);
//				points.put(olPointType, p3);
//				graphicElements.add(p3);
//				break;
//			default:
//				// all other points are moveable on a line, represented by a OLPointMoveableOnLineGE2D
//				OLPointMoveableOnLineGE2D pml = new OLPointMoveableOnLineGE2D(olPointType.name, this, olPointType);
//				points.put(olPointType, pml);
//				graphicElements.add(pml);
//			}
		}
	}

	private void createLenses()
	{
		// initialise the array of lenses
		// lens = new Lens2D[OLLensType.values().length];
		lenses = new HashMap<OmnidirectionalLensLensType, Lens2D>();

		for(OmnidirectionalLensLensType olLensType : OmnidirectionalLensLensType.values())
		{
			// create the lens, ...
			Lens2D l = new Lens2D(olLensType.toString());

			// ... add it to the array of lenses, ...
			lenses.put(olLensType, l);

			// ... and add it to the list of optical components and the list of graphic elements
			opticalComponents.add(l);
			graphicElements.add(l);
		}
	}

	public void calculatePointParameters()
	{		
		// all the points that can move on a line move on one of two lines
//		Line2D lineThroughPrincipalPoints = new Line2D(pD, Vector2D.sum(pD, cHat));
//		Line2D lineOfLensD = new Line2D(pD, Vector2D.sum(pD, dHat));

		// the bottom vertices
		points.get(OmnidirectionalLensPointType.V1).setCoordinatesToThoseOf(Vector2D.sum(pD, dHat.getProductWith(-rD)));
		points.get(OmnidirectionalLensPointType.V2).setCoordinatesToThoseOf(Vector2D.sum(pD, dHat.getProductWith( rD)));
//		((OLPointMoveableOnLineGE2D)points.get(OLPointType.V1)).setLine(lineOfLensD);
//		((OLPointMoveableOnLineGE2D)points.get(OLPointType.V2)).setLine(lineOfLensD);

		// the lower inner vertex
		points.get(OmnidirectionalLensPointType.P1).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(h1)));
//		((OLPointMoveableOnLineGE2D)points.get(OLPointType.P1)).setLine(lineThroughPrincipalPoints);

		// the upper inner vertex
		// System.out.println("OmnidirectionalLens2D::calculatePointAndLensParameters: h2="+h2);
		points.get(OmnidirectionalLensPointType.P2).setCoordinatesToThoseOf(
				// new Vector2D(0.2, 0.4)
				Vector2D.sum(pD, cHat.getProductWith(h2))
				);
//		((OLPointMoveableOnLineGE2D)points.get(OLPointType.P2)).setLine(lineThroughPrincipalPoints);

		// the top vertex
		points.get(OmnidirectionalLensPointType.P3).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(h)));

		// the principal point of lens D
		points.get(OmnidirectionalLensPointType.P0).setCoordinatesToThoseOf(pD);

		// the (inside) focal point of lens D
		points.get(OmnidirectionalLensPointType.FD).setCoordinatesToThoseOf(Vector2D.sum(pD, cHat.getProductWith(fD)));
//		((OLPointMoveableOnLineGE2D)points.get(OLPointType.FD)).setLine(lineThroughPrincipalPoints);
	}

	public void calculateLensParameters()
	{
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



	//	//
	//	// OpticalComponent2D methods
	//	//
	//
	//	@Override
	//	public RayComponentIntersection2D calculateIntersection(Ray2D r, boolean forwardOnly, OpticalComponent2D lastIntersectionComponent)
	//	{
	//		RayComponentIntersection2D closestIntersection = null;
	//		double closestIntersectionDistance = Double.POSITIVE_INFINITY;
	//
	//		// go through all the optical components that make up this omnidirectional lens
	//		for(OpticalComponent2D o:opticalComponents)
	//		{
	//			// was the last intersection with the current optical component?
	//			if(o != lastIntersectionComponent)
	//			{
	//				// the last intersection was with a different component
	//
	//				// see if there is an intersection with the current optical component
	//				RayComponentIntersection2D i = o.calculateIntersection(r, true, lastIntersectionComponent);
	//				if(i != null)
	//				{
	//					// there is an intersection; is it closer than the current closest intersection?
	//
	//					double d = Vector2D.distance(r.getStartingPoint(), i.p);
	//					if(d < closestIntersectionDistance)
	//					{
	//						// the intersection is closer than the current closest intersection
	//						closestIntersection = i;
	//						closestIntersectionDistance = d;
	//					}
	//				}
	//			}
	//		}
	//		return closestIntersection;
	//	}


}
