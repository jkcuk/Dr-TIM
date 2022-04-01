package optics.rayplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import math.MyMath;
import math.Vector2D;

/**
 * References:
 * [1] J. Courtial et al., "Ray-optical transformation optics with ideal thin lenses makes omnidirectional lenses",
 *     Opt. Express 26, 17872-17888 (2018)
 * 
 * @author johannes
 *
 */
class RayPlay2DPanel extends JPanel implements MouseListener, MouseMotionListener
{
	private static final long serialVersionUID = 6137072632788997510L;

	// the parameters -- see [1] for nomenclature
	private double fD;
	private double baseLensRadius;
	private double h1;
	private double h2;
	private double h;
	
	// indices of the points
	private static final int RAY_POINT2 = 0;
	private static final int RAY_START_POINT = 1;
	private static final int V2_POINT = 2;
	private static final int P1_POINT = 3;
	private static final int P2_POINT = 4;
	private static final int P3_POINT = 5;
	private static final int FD_POINT = 6;	// focal point of base lens
	private static final int NO_OF_POINTS = 7;
	
	private InteractivePoint2D point[];
//	private InteractivePoint2D rayStartPoint;
//	private InteractivePoint2D rayDirection;
	private double rayBundleAngle;
	private int rayBundleNoOfRays;
	private boolean rayBundle;
	private boolean forwardTraceOnly;
	
	// the vertices
	private Vector2D vertex[];

	// the lenses
	private Lens2D lens[];
	
	// the rays
	private Ray2D ray[];
	
	// scaled coordinates
	private double xCentre;
	private double yCentre;
	private double xWidth;
	private double yHeight;
	


    // constructor

	RayPlay2DPanel()
	{
		super();

		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(420,420));
		setBackground(new java.awt.Color(255, 255, 255));
		// setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		createVertexArray();
		
		// set the parameters
		fD = .1;
		baseLensRadius = 0.5;
		h1 = 1./3.;
		h2 = 2./3.;
		h = 1;
		point = new InteractivePoint2D[NO_OF_POINTS];
		point[RAY_START_POINT] = new InteractivePoint2D(-0.8, 0.5, 5);
		point[RAY_POINT2] = new InteractivePoint2D(-0.6, 0.5, 3);
		point[V2_POINT] = new InteractivePoint2D(vertex[V2], 3);
		point[P1_POINT] = new InteractivePoint2D(vertex[P1], 3);
		point[P2_POINT] = new InteractivePoint2D(vertex[P2], 3);
		point[P3_POINT] = new InteractivePoint2D(vertex[P3], 3);
		point[FD_POINT] = new InteractivePoint2D(vertex[FD], 3);

		rayBundleAngle = MyMath.deg2rad(30);
		rayBundleNoOfRays = 5;
		rayBundle = true;
		forwardTraceOnly = false;


		init();
		
		xCentre = 0;
		yCentre = 0.5*h;
		yHeight = 2*h;
		// xMin and xMax get set in paintComponent()

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	
	public void writeParameters(PrintStream printStream)
	{
		String className;
		
		try {
			className = Class.forName(Thread.currentThread().getStackTrace()[1].getClassName()).getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			className = "<unknown class name>";
		}

		printStream.println("Created with class "+className+"\n\n");
		
		printStream.println("\n*** Parameters ***\n");

		printStream.println("fD = "+fD);
		printStream.println("baseLensRadius = "+baseLensRadius);
		printStream.println("h1 = "+h1);
		printStream.println("h2 = "+h2);
		printStream.println("h = "+h);
		printStream.println("rayBundleAngle = "+MyMath.rad2deg(rayBundleAngle)+" degrees");
		printStream.println("rayBundleNoOfRays = "+rayBundleNoOfRays);
		printStream.println("rayBundle = "+rayBundle);
		printStream.println("forwardTraceOnly = "+forwardTraceOnly);
		printStream.println("ray (bundle) start point = "+point[RAY_START_POINT].getV());
		Vector2D dC = getCentralRayDirection();
		printStream.println("Angle of (central) ray with horizontal = "+ MyMath.rad2deg(Math.atan2(dC.y, dC.x))+" degrees");
		if(rayBundle)
			printStream.println("Ray bundle angular width = "+MyMath.rad2deg(2*Math.atan(tanRayBundleAngleConstant/dC.getLength()))+" degrees");

		printStream.println("\n*** Lens focal lengths ***\n");
		
		printStream.println("fA = "+lens[A1].getFocalLength());
		printStream.println("fB = "+lens[B1].getFocalLength());
		printStream.println("fC = "+lens[C1].getFocalLength());
		printStream.println("fD = "+lens[D].getFocalLength());
		printStream.println("fE = "+lens[E].getFocalLength());
		printStream.println("fF = "+lens[F].getFocalLength());
	}
	
	/**
	 * Create a new .txt file and save the parameters into it
	 * @param filename
	 */
	public void saveParameters(String filename)
	{
		System.out.println("Saving parameters as .txt file...");

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(filename+"_parameters.txt");
			PrintStream printStream = new PrintStream(fileOutputStream);
			
			writeParameters(printStream);

			printStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("...done.");
	}

	public void saveSVG(String filename)
	{
		System.out.println("Saving image as SVG...");

		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(filename+".svg");
			PrintStream printStream = new PrintStream(fileOutputStream);

			Dimension size = getSize();
			
			printStream.println(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
					"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
					"<svg width=\""+size.width+"\" height=\""+size.height+"\" viewBox=\"0 0 "+size.width+" "+size.height+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
					// "<rect fill=\"#fff\" stroke=\"#fff\" x=\"0\" y=\"0\" width=\""+size.width+"\" height=\""+size.height+"\"/>\n" +
					"<g opacity=\"1\">"
				);
			
			// draw rays
			for(int r=0; r<ray.length; r++)
			{
				ArrayList<Vector2D> t = ray[r].getTrajectory();
				for(int p=1; p<t.size(); p++)
					writeSVGLine(t.get(p-1), t.get(p), "red", 1, "", printStream);
			}

			// draw lenses
			if(lens != null)
				for(int l=0; l<lens.length; l++)
				{
					if(lens[l] != null) writeSVGLine(lens[l].getA(), lens[l].getB(), "cyan", 3, "opacity=\"0.7\"", printStream);
				}

//					"	<rect x=\"25\" y=\"25\" width=\"200\" height=\"200\" fill=\"lime\" stroke-width=\"4\" stroke=\"pink\" />\n" +
//					"	<circle cx=\"125\" cy=\"125\" r=\"75\" fill=\"orange\" />\n" +
//					"	<polyline points=\"50,150 50,200 200,200 200,100\" stroke=\"red\" stroke-width=\"4\" fill=\"none\" />\n" +
//					"	<line x1=\"50\" y1=\"50\" x2=\"200\" y2=\"200\" stroke=\"blue\" stroke-width=\"4\" />\n" +

			printStream.println(
					"</g>\n" +
					"</svg>"
				);

			printStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println("...done.");
	}
	
	/**
	 * draw a line between points 1 and 2 in the scaled coordinate system
	 * @param point1
	 * @param point2
	 * @param color	e.g. "blue"
	 * @param width
	 * @param style	e.g. "opacity=\"0.8\""
	 * @param p	the PrintStream this gets printed to
	 */
	public void writeSVGLine(Vector2D point1, Vector2D point2, String color, int width, String style, PrintStream p)
	{
		p.println(
				"<line "+
				"x1=\""+x2id(point1.x)+"\" "+
				"y1=\""+y2jd(point1.y)+"\" "+
				"x2=\""+x2id(point2.x)+"\" "+
				"y2=\""+y2jd(point2.y)+"\" "+
				"stroke=\""+color+"\" "+
				"stroke-width=\""+width+"\" "+
				style+
				"/>"
			);
	}


	
	// setters & getters
	
	public double getfD() {
		return fD;
	}

	public void setfD(double fD) {
		this.fD = fD;
	}

	public double getR() {
		return baseLensRadius;
	}

	public void setR(double baseLensRadius) {
		this.baseLensRadius = baseLensRadius;
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

	public Vector2D getRayStartPoint() {
		return point[RAY_START_POINT].getV();
	}

	public void setRayStartPoint(Vector2D rayStartPoint) {
		this.point[RAY_START_POINT].setVComponents(rayStartPoint);
	}

	public double getRayBundleAngle() {
		return rayBundleAngle;
	}

	public void setRayBundleAngle(double rayBundleAngle) {
		this.rayBundleAngle = rayBundleAngle;
	}

	public int getRayBundleNoOfRays() {
		return rayBundleNoOfRays;
	}

	public void setRayBundleNoOfRays(int rayBundleNoOfRays) {
		this.rayBundleNoOfRays = rayBundleNoOfRays;
	}
	
	public boolean isRayBundle() {
		return rayBundle;
	}

	public void setRayBundle(boolean rayBundle) {
		this.rayBundle = rayBundle;
	}

	public boolean isForwardTraceOnly() {
		return forwardTraceOnly;
	}

	public void setForwardTraceOnly(boolean forwardTraceOnly) {
		this.forwardTraceOnly = forwardTraceOnly;
	}



	//
	// scaled coordinates; the simulation uses (double x, double y); the panel uses (int i, int j)
	//

	public double x2id(double x)
	{
		return getSize().width*((x-(xCentre-0.5*xWidth))/xWidth);
	}

	public double y2jd(double y)
	{
		return getSize().height*((-y+(yCentre+0.5*yHeight))/yHeight);
	}

	public int x2i(double x)
	{
		return (int)(x2id(x) + 0.5);
	}

	public int y2j(double y)
	{
		return (int)(y2jd(y) + 0.5);
	}
	
	public double i2x(double i)
	{
		return i/getSize().width*xWidth + (xCentre-0.5*xWidth);
	}

	public double j2y(double j)
	{
		return -(j/getSize().height*yHeight - (yCentre+0.5*yHeight));
	}

	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// make sure the aspect ratio is correct
		Dimension size = getSize();
		double aspectRatio = ((double)size.width)/((double)size.height);
		xWidth = yHeight*aspectRatio;

		// draw into a Graphics2D -- much easier
		Graphics2D g2 = (Graphics2D)g;

		traceRays();
		
		// draw lenses
		drawLenses(g2);

		// draw rays
		drawRays(g2);
		
		drawPoints(g2);
	}
	
	/**
	 * draw a line between points 1 and 2 in the scaled coordinate system
	 * @param point1
	 * @param point2
	 * @param g2
	 */
	public void drawLine(Vector2D point1, Vector2D point2, Graphics2D g2)
	{
		g2.drawLine(
				x2i(point1.x), y2j(point1.y),
				x2i(point2.x), y2j(point2.y)
			);
	}
	
	
	// coordinate system for OL
	private Vector2D right = new Vector2D(1, 0);
	private Vector2D up = new Vector2D(0, 1);
	
	//
	// the vertices and principal points
	//
	
	public void init()
	{
		calculateVertices();
		initLenses();
	}
	
	// for nomenclature see Fig. 4 in [1]
	private static final int P0 = 0;
	private static final int P1 = 1;
	private static final int P2 = 2;
	private static final int P3 = 3;
	private static final int V1 = 4;
	private static final int V2 = 5;
	private static final int FD = 6;
	private static final int NO_OF_VERTICES = 7;
	
	private void createVertexArray()
	{
		vertex = new Vector2D[NO_OF_VERTICES];
		for(int v=0; v<vertex.length; v++)
			vertex[v] = new Vector2D(0, 0);
		
		calculateVertices();
	}
	
	private void calculateVertices()
	{
		// the bottom vertices
		vertex[V1].setCoordinatesToThoseOf(right.getProductWith(-baseLensRadius));
		vertex[V2].setCoordinatesToThoseOf(right.getProductWith( baseLensRadius));
		
		// the lower inner vertex
		vertex[P1].setCoordinatesToThoseOf(up.getProductWith(h1));
		
		// the upper inner vertex
		vertex[P2].setCoordinatesToThoseOf(up.getProductWith(h2));

		// the top vertex
		vertex[P3].setCoordinatesToThoseOf(up.getProductWith(h));
		
		// the principal point of lens D
		vertex[P0].setCoordinatesTo(0, 0);	
		
		// the (inside) focal point of lens D
		vertex[FD].setCoordinatesTo(0, fD);
	}
	

	//
	// the lenses
	//

	// for nomenclature see Fig. 7 in [1]
	private static final int A1 = 0;
	private static final int A2 = 1;
	private static final int B1 = 2;
	private static final int B2 = 3;
	private static final int C1 = 4;
	private static final int C2 = 5;
	private static final int D = 6;
	private static final int E = 7;
	private static final int F = 8;

	private void initLenses()
	{
		calculateVertices();
		
		lens = new Lens2D[9];
		
		// calculate the focal lengths from fD and r, h1, h2, h -- see AllLoopTheorems 2D.nb
		double r2 = baseLensRadius*baseLensRadius;
		double fA = ((-fD*h + (fD + h)*h1)*(h - h2)*baseLensRadius)/(h1*h2*Math.sqrt(h*h + r2));
		double fB = (fD*(h - h2)*(-h1 + h2)*baseLensRadius)/(h*h1*Math.sqrt(h2*h2 + r2));
		double fC = ((fD*h - (fD + h)*h1)*(h1 - h2)*baseLensRadius)/(h*h2*Math.sqrt(h1*h1 + r2));
		double fE = ((fD*h - (fD + h)*h1)*(h1 - h2)*baseLensRadius)/(2*h*h1*h2);
		double fF = ((fD*h - (fD + h)*h1)*(h  - h2)*baseLensRadius)/(2*h*h1*h2);
		
		// lenses A
		lens[A1] = new Lens2D(
				"A1",	// name
				vertex[P3],	// principal point
				fA,	// focal length
				vertex[V1], vertex[P3]	// the end points
			);

		lens[A2] = new Lens2D(
				"A2",	// name
				vertex[P3],	// principal point
				fA,	// focal length
				vertex[V2], vertex[P3]	// the end points
			);
		
		// lenses B
		lens[B1] = new Lens2D(
				"B1",	// name
				vertex[P2],	// principal point
				fB,	// focal length
				vertex[V1], vertex[P2]	// the end points
			);

		lens[B2] = new Lens2D(
				"B2",	// name
				vertex[P2],	// principal point
				fB,	// focal length
				vertex[V2], vertex[P2]	// the end points
			);

		// lenses C
		lens[C1] = new Lens2D(
				"C1",	// name
				vertex[P1],	// principal point
				fC,	// focal length
				vertex[V1], vertex[P1]	// the end points
			);

		lens[C2] = new Lens2D(
				"C2",	// name
				vertex[P1],	// principal point
				fC,	// focal length
				vertex[V2], vertex[P1]	// the end points
			);
		

		// the "base lens", lens D
		lens[D] = new Lens2D(
				"D (base lens)",	// name
				vertex[P0],	// principal point
				fD,	// focal length
				vertex[V1], vertex[V2]	// the end points
			);
		
		
		// lens E
		lens[E] = new Lens2D(
			"E",	// name
			vertex[P1],
			fE,
			vertex[P1], vertex[P2]	// the end points
		);

		// lens F
		lens[F] = new Lens2D(
			"F",	// name
			vertex[P3],
			fF,
			vertex[P2], vertex[P3]	// the end points
		);
	}
	
	private void traceRay(Ray2D ray)
	{
		int lastIntersectionLens = -1;
		
		// trace it through the lenses
		for(int tl=255; tl>0; tl--)
		{
			Vector2D closestIntersection = null;
			double closestIntersectionDistance = Double.POSITIVE_INFINITY;
			int closestIntersectionLens = -1;
			for(int l=0;
					l<lens.length;
					l++)
			{
				// is the intersection with the lens that had previously been intersected?
				if(l != lastIntersectionLens)
				{
					// the intersection is with a different lens this time
					Vector2D i = lens[l].calculateIntersection(ray, true);
					if(i != null)
					{
						// there is an intersection; is it closer than the current closest intersection?

						double d = Vector2D.distance(ray.getStartingPoint(), i);
						if(d < closestIntersectionDistance)
						{
							// the intersection is closer than the current closest intersection
							closestIntersection = i;
							closestIntersectionDistance = d;
							closestIntersectionLens = l;
						}
					}
				}
			}
			if(closestIntersection == null)
			{
				// there is no intersection

				ray.advance(10);
				return;
			}

			// there is an intersection
			lastIntersectionLens = closestIntersectionLens;
			lens[closestIntersectionLens].passThroughComponent(ray, closestIntersection);
		}
	}
	
	
	public Vector2D getCentralRayDirection() {
		return Vector2D.difference(point[RAY_POINT2].getV(), point[RAY_START_POINT].getV());
	}
	
	private double tanRayBundleAngleConstant = 0.25;
	
	private void traceRays()
	{
		Vector2D dC = getCentralRayDirection();
		
		rayBundleAngle = 2*Math.atan(tanRayBundleAngleConstant/dC.getLength());	// 180*dC.getLength();
		if(rayBundle)
		{
			ray = new Ray2D[(forwardTraceOnly?rayBundleNoOfRays:2*rayBundleNoOfRays)];
			
			double alphaC = Math.atan2(dC.y, dC.x);
			double alpha0 = alphaC - 0.5*rayBundleAngle;

			for(int r=0; r<rayBundleNoOfRays; r++)
			{
				// calculate the ray direction
				double alpha = alpha0 + r*rayBundleAngle/(rayBundleNoOfRays-1);
				Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

				// initialise the forward ray
				ray[r] = new Ray2D(point[RAY_START_POINT].getV(), d);
				traceRay(ray[r]);

				if(!forwardTraceOnly)
				{
					// ... and the backwards ray
					ray[r+rayBundleNoOfRays] = new Ray2D(point[RAY_START_POINT].getV(), d.getProductWith(-1));
					traceRay(ray[r+rayBundleNoOfRays]);
				}
			}
		}
		else
		{
			ray = new Ray2D[(forwardTraceOnly?1:2)];

			// initialise the forward ray
			ray[0] = new Ray2D(point[RAY_START_POINT].getV(), dC);
			traceRay(ray[0]);
	
			if(!forwardTraceOnly)
			{
				// ... and the backwards ray
				ray[1] = new Ray2D(point[RAY_START_POINT].getV(), dC.getProductWith(-1));
				traceRay(ray[1]);
			}
		}
	}
	
	private void drawLenses(Graphics2D g2)
	{
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.CYAN);

		if(lens != null)
		for(int l=0;
				l<lens.length;
				l++)
		{
			if(lens[l] != null) lens[l].draw(this, g2);
		}
	}
	
	
	private void drawRays(Graphics2D g2)
	{
		// draw a ray
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.RED);

		// drawLine(rayStartPoint, Vector2D.sum(rayStartPoint, rayDirection), g2);
		// g2.drawRect(200, 200, 200, 200);
		// g2.fillOval(x2i(point[RAY_START_POINT].x)-5, y2j(point[RAY_START_POINT].y)-5, 11, 11);
		// g2.drawOval(currentX-2, currentY-2, 5, 5);

		for(int r=0; r<ray.length; r++)
		{
			ArrayList<Vector2D> t = ray[r].getTrajectory();
			for(int p=1; p<t.size(); p++)
				drawLine(t.get(p-1), t.get(p), g2);
		}
		
//		g2.drawLine(oldX, oldY, currentX, currentY);
//		// g2.drawRect(200, 200, 200, 200);
//		g2.fillOval(oldX-5, oldY-5, 11, 11);
//		g2.drawOval(currentX-2, currentY-2, 5, 5);

	}
	
	
	private void drawPoints(Graphics2D g2)
	{
		DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.000" );

		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.GRAY);

		for(int p=0; p<point.length; p++)
		{
			int r = point[p].getRadius();
			int pointI = x2i(point[p].getV().x);
			int pointJ = y2j(point[p].getV().y);
			// is the mouse within the radius from the point?
			if(mouseNearPoint == p)
				g2.fillOval(pointI-r, pointJ-r, 2*r, 2*r);
			else
				g2.drawOval(pointI-r, pointJ-r, 2*r, 2*r);			
		}

		// add a bit of text about the points
		if(mouseNearPoint != -1)
		{
			// the mouse is near a point
			if(mouseNearPoint == RAY_START_POINT)
			{
				Vector2D p = point[RAY_START_POINT].getV();
				g2.drawString("Ray "+(rayBundle?"bundle ":"")+"start position ("+df2.format(p.x)+","+df2.format(p.y)+")", x2i(p.x)+10, y2j(p.y)+5);
			}
			else if(mouseNearPoint == RAY_POINT2)
			{
				Vector2D n = getCentralRayDirection().getPerpendicularVector().getNormalised();

				if(rayBundle)
				{
					g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
							0, new float[]{3}, 0));
					drawLine(
							Vector2D.sum(point[RAY_POINT2].getV(), n.getProductWith(-tanRayBundleAngleConstant)),
							Vector2D.sum(point[RAY_POINT2].getV(), n.getProductWith( tanRayBundleAngleConstant)),
							g2);
				}

				// give some info
				Vector2D p = point[RAY_POINT2].getV();
				Vector2D dC = getCentralRayDirection();
				g2.drawString("Angle with horizontal = "+df2.format(MyMath.rad2deg(Math.atan2(dC.y, dC.x)))+" degrees", x2i(p.x)+10, y2j(p.y)+(rayBundle?-5:5));
				if(rayBundle)
					g2.drawString("Ray bundle angular width = "+df2.format(MyMath.rad2deg(2*Math.atan(tanRayBundleAngleConstant/dC.getLength())))+" degrees", x2i(p.x)+10, y2j(p.y)+15);
			}
			else if(mouseNearPoint == V2_POINT)
			{
				Vector2D p = point[V2_POINT].getV();
				g2.drawString("Base lens radius = "+df2.format(baseLensRadius), x2i(p.x)+10, y2j(p.y)+5);
			}
			else if(mouseNearPoint == P1_POINT)
			{
				Vector2D p = point[P1_POINT].getV();
				g2.drawString("h1 = "+df2.format(h1), x2i(p.x)+10, y2j(p.y)+5);
			}
			else if(mouseNearPoint == P2_POINT)
			{
				Vector2D p = point[P2_POINT].getV();
				g2.drawString("h2 = "+df2.format(h2), x2i(p.x)+10, y2j(p.y)+5);
			}
			else if(mouseNearPoint == P3_POINT)
			{
				Vector2D p = point[P3_POINT].getV();
				g2.drawString("h = "+df2.format(h), x2i(p.x)+10, y2j(p.y)+5);
			}
			else if(mouseNearPoint == FD_POINT)
			{
				Vector2D p = point[FD_POINT].getV();
				g2.drawString("Lens D (base lens) f = "+df2.format(fD), x2i(p.x)+10, y2j(p.y)+5);
			}
		}
		else
		{
			// the mouse is not near a point; is it near a lens?
			if(mouseNearLens != -1)
			{
				// the mouse is close to a lens
				Lens2D l = lens[mouseNearLens];
				// Vector2D p = l.getMidpoint();
				g2.drawString(
						"Lens "+l.getName()+", f = "+df2.format(l.getFocalLength()), 
						mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
						);
				repaint();
			}
		}
	}


	
	// MouseListener methods
	
    private int mouseI, mouseJ;
    private int mouseNearPoint = -1;
    private int mouseNearLens = -1;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)  // double click
		{
			if(mouseNearPoint == RAY_START_POINT)
			{
				// mouse double-clicked near point 2
				forwardTraceOnly = !forwardTraceOnly;
				repaint();
			}
			else if(mouseNearPoint == RAY_POINT2)
			{
				// mouse double-clicked near point 2
				rayBundle = !rayBundle;
				repaint();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
//		if(mouseNearPoint != -1)
//		{
//			// the mouse is close to a point
//			if(mouseNearPoint == RAY_START_POINT)
//			{
//				// the mouse is close to the ray start point
//				
//				Vector2D startPoint2Point2 = Vector2D.difference(point[RAY_POINT2], point[RAY_START_POINT]);
//				System.out.println("startPoint2Point2="+startPoint2Point2);
//
//				point[RAY_START_POINT].x = i2x(e.getX());
//				point[RAY_START_POINT].y = j2y(e.getY());
//
//				// move point[RAY_POINT2] by the same amount
//				point[RAY_POINT2].x = point[RAY_START_POINT].x + startPoint2Point2.x;
//				point[RAY_POINT2].y = point[RAY_START_POINT].y + startPoint2Point2.y;
//
//				repaint();
//			}
//			else 
//			{
//				// the mouse is close to another point
//				point[mouseNearPoint].x = i2x(e.getX());
//				point[mouseNearPoint].y = j2y(e.getY());
//				repaint();
//			}
//		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// currentX = e.getX();
		// currentY = e.getY();
		// repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// System.out.println("Mouse entered");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// System.out.println("Mouse exited");
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if(mouseNearPoint != -1)
		{
			// the mouse is close to a point
			if(mouseNearPoint == RAY_START_POINT)
			{
				// the mouse is close to the ray start point
				
				Vector2D startPoint2Point2 = Vector2D.difference(point[RAY_POINT2].getV(), point[RAY_START_POINT].getV());
				
				point[RAY_START_POINT].getV().x = i2x(e.getX());
				point[RAY_START_POINT].getV().y = j2y(e.getY());

				// move point[RAY_POINT2] by the same amount
				point[RAY_POINT2].getV().x = point[RAY_START_POINT].getV().x + startPoint2Point2.x;
				point[RAY_POINT2].getV().y = point[RAY_START_POINT].getV().y + startPoint2Point2.y;

				repaint();
			}
			else if(mouseNearPoint == V2_POINT)
			{
				double newBaseLensRadius = Vector2D.scalarProduct(
						Vector2D.difference(new Vector2D(i2x(e.getX()), j2y(e.getY())), vertex[P0]),
						right
					);
					
				if(newBaseLensRadius > 0)
				{
					baseLensRadius = newBaseLensRadius;
					initLenses();
					repaint();
				}
			}
			else if(mouseNearPoint == P1_POINT)
			{
				double newH1 = Vector2D.scalarProduct(
						Vector2D.difference(new Vector2D(i2x(e.getX()), j2y(e.getY())), vertex[P0]),
						up
					);
					
				if((newH1 > 0) && (newH1 < h2))
				{
					h1 = newH1;
					initLenses();
					repaint();
				}
			}
			else if(mouseNearPoint == P2_POINT)
			{
				double newH2 = Vector2D.scalarProduct(
						Vector2D.difference(new Vector2D(i2x(e.getX()), j2y(e.getY())), vertex[P0]),
						up
					);
					
				if((newH2 > 0) && (newH2 > h1) && (newH2 < h))
				{
					h2 = newH2;
					initLenses();
					repaint();
				}
			}
			else if(mouseNearPoint == P3_POINT)
			{
				double newH = Vector2D.scalarProduct(
						Vector2D.difference(new Vector2D(i2x(e.getX()), j2y(e.getY())), vertex[P0]),
						up
					);
				
				if((newH > 0) && (newH > h2))
				{
					h = newH;
					initLenses();
					repaint();
				}
			}
			else if(mouseNearPoint == FD_POINT)
			{
				double newFD = Vector2D.scalarProduct(
						Vector2D.difference(new Vector2D(i2x(e.getX()), j2y(e.getY())), vertex[P0]),
						up
					);
				
				if(newFD != 0)
				{
					fD = newFD;
					initLenses();
					repaint();
				}
			}
			else 
			{
				// the mouse is close to another point
				point[mouseNearPoint].getV().x = i2x(e.getX());
				point[mouseNearPoint].getV().y = j2y(e.getY());
				repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseI = e.getX();
		mouseJ = e.getY();
		
		// is the mouse near one of the points?
		for(int p=0; p<point.length; p++)
		{
			int r = point[p].getRadius();
			int pointI = x2i(point[p].getV().x);
			int pointJ = y2j(point[p].getV().y);
			// is the mouse within the radius from the point?
			if(MyMath.square(mouseI - pointI) + MyMath.square(mouseJ - pointJ) <= r*r)
			{
				// the mouse is near point #p
				if(p != mouseNearPoint)
				{
					mouseNearPoint = p;
					repaint();
				}
				return;
			}
		}
		
		// the mouse isn't near any point
		if(mouseNearPoint != -1)
		{
			// previously it was near a point, now it isn't any longer
			mouseNearPoint = -1;
			repaint();
		}
		
		// is the mouse near one of the lenses?
		for(int l=0; l<lens.length; l++)
		{
			// define the line segment in (i,j) coordinates
			LineSegment2D s = new LineSegment2D(
					new Vector2D(x2i(lens[l].getA().x), y2j(lens[l].getA().y)),
					new Vector2D(x2i(lens[l].getB().x), y2j(lens[l].getB().y))
					);
			
			if(Geometry2D.lineSementPointDistance(s, new Vector2D(mouseI, mouseJ)) < 3)
			{
				// the mouse is near lens #l
				if(l != mouseNearLens)
				{
					mouseNearLens = l;
					repaint();
				}
				return;
			}
		}
		
		// the mouse isn't near any lens
		if(mouseNearLens != -1)
		{
			// previously it was near a lens, now it isn't any longer
			mouseNearLens = -1;
			repaint();
		}
	}
}