package optics.rayplay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
	private double r;
	private double h1;
	private double h2;
	private double h;
	
	// indices of the points
	private static final int RAY_POINT2 = 0;
	private static final int RAY_START_POINT = 1;
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
		
		// set the parameters
		fD = .1;
		r = 0.5;
		h1 = 1./3.;
		h2 = 2./3.;
		h = 1;
		point = new InteractivePoint2D[2];
		point[RAY_START_POINT] = new InteractivePoint2D(-0.8, 0.5, 5);
		point[RAY_POINT2] = new InteractivePoint2D(-0.6, 0.5, 3);
		rayBundleAngle = 30;
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
	
	
	
	// setters & getters
	
	public double getfD() {
		return fD;
	}

	public void setfD(double fD) {
		this.fD = fD;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
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
		return point[RAY_START_POINT];
	}

	public void setRayStartPoint(Vector2D rayStartPoint) {
		this.point[RAY_START_POINT].setPosition(rayStartPoint);
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


	public int x2i(double x)
	{
		int i = (int)(getSize().width*((x-(xCentre-0.5*xWidth))/xWidth) + 0.5);
		// System.out.println("x2i("+x+")="+i);
		return i;
	}

	public int y2j(double y)
	{
		int j = (int)(getSize().height*((-y+(yCentre+0.5*yHeight))/yHeight) + 0.5);
		// System.out.println("y2j("+y+")="+j);
		return j;
	}
	
	public double i2x(int i)
	{
		return ((double)i)/getSize().width*xWidth + (xCentre-0.5*xWidth);
	}

	public double j2y(int j)
	{
		return -(((double)j)/getSize().height*yHeight - (yCentre+0.5*yHeight));
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
		
		drawPoints(g2);	// TODO
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
		initVertices();
		initLenses();
	}
	
	// for nomenclature see Fig. 4 in [1]
	private static final int P0 = 0;
	private static final int P1 = 1;
	private static final int P2 = 2;
	private static final int P3 = 3;
	private static final int V1 = 4;
	private static final int V2 = 5;
	
	private void initVertices()
	{
		vertex = new Vector2D[6];
		
		// the bottom vertices
		vertex[V1] = right.getProductWith(-r);
		vertex[V2] = right.getProductWith( r);
		
		// the lower inner vertex
		vertex[P1] = up.getProductWith(h1);
		
		// the upper inner vertex
		vertex[P2] = up.getProductWith(h2);

		// the top vertex
		vertex[P3] = up.getProductWith(h);
		
		// the principal point of lens D
		vertex[P0] = new Vector2D(0, 0);
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
		lens = new Lens2D[9];
		
		// calculate the focal lengths from fD and r, h1, h2, h -- see AllLoopTheorems 2D.nb
		double fA = ((-fD*h + (fD + h)*h1)*(h - h2)*r)/(h1*h2*Math.sqrt(h*h + r*r));
		double fB = (fD*(h - h2)*(-h1 + h2)*r)/(h*h1*Math.sqrt(h2*h2 + r*r));
		double fC = ((fD*h - (fD + h)*h1)*(h1 - h2)*r)/(h*h2*Math.sqrt(h1*h1 + r*r));
		double fE = ((fD*h - (fD + h)*h1)*(h1 - h2)*r)/(2*h*h1*h2);
		double fF = ((fD*h - (fD + h)*h1)*(h  - h2)*r)/(2*h*h1*h2);
		
		// lenses A
		lens[A1] = new Lens2D(
				vertex[P3],	// principal point
				fA,	// focal length
				vertex[V1], vertex[P3]	// the end points
			);

		lens[A2] = new Lens2D(
				vertex[P3],	// principal point
				fA,	// focal length
				vertex[V2], vertex[P3]	// the end points
			);
		
		// lenses B
		lens[B1] = new Lens2D(
				vertex[P2],	// principal point
				fB,	// focal length
				vertex[V1], vertex[P2]	// the end points
			);

		lens[B2] = new Lens2D(
				vertex[P2],	// principal point
				fB,	// focal length
				vertex[V2], vertex[P2]	// the end points
			);

		// lenses C
		lens[C1] = new Lens2D(
				vertex[P1],	// principal point
				fC,	// focal length
				vertex[V1], vertex[P1]	// the end points
			);

		lens[C2] = new Lens2D(
				vertex[P1],	// principal point
				fC,	// focal length
				vertex[V2], vertex[P1]	// the end points
			);
		

		// the "base lens", lens D
		lens[D] = new Lens2D(
				vertex[P0],	// principal point
				fD,	// focal length
				vertex[V1], vertex[V2]	// the end points
			);
		
		
		// lens E
		lens[E] = new Lens2D(
			vertex[P1],
			fE,
			vertex[P1], vertex[P2]	// the end points
		);

		// lens F
		lens[F] = new Lens2D(
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

				ray.advance(1000);
				return;
			}

			// there is an intersection
			lastIntersectionLens = closestIntersectionLens;
			lens[closestIntersectionLens].passThroughComponent(ray, closestIntersection);
		}
	}
	
	
	public Vector2D getCentralRayDirection() {
		return Vector2D.difference(point[RAY_POINT2], point[RAY_START_POINT]);
	}
	

	private void traceRays()
	{
		Vector2D dC = getCentralRayDirection();
		
		rayBundleAngle = 180*dC.getLength();
		if(rayBundle)
		{
			ray = new Ray2D[(forwardTraceOnly?rayBundleNoOfRays:2*rayBundleNoOfRays)];
			
			double alphaC = Math.atan2(dC.y, dC.x);
			double alpha0 = alphaC - MyMath.deg2rad(0.5*rayBundleAngle);

			for(int r=0; r<rayBundleNoOfRays; r++)
			{
				// calculate the ray direction
				double alpha = alpha0 + r*MyMath.deg2rad(rayBundleAngle)/(rayBundleNoOfRays-1);
				Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

				// initialise the forward ray
				ray[r] = new Ray2D(point[RAY_START_POINT], d);
				traceRay(ray[r]);

				if(!forwardTraceOnly)
				{
					// ... and the backwards ray
					ray[r+rayBundleNoOfRays] = new Ray2D(point[RAY_START_POINT], d.getProductWith(-1));
					traceRay(ray[r+rayBundleNoOfRays]);
				}
			}
		}
		else
		{
			ray = new Ray2D[(forwardTraceOnly?1:2)];

			// initialise the forward ray
			ray[0] = new Ray2D(point[RAY_START_POINT], dC);
			traceRay(ray[0]);
	
			if(!forwardTraceOnly)
			{
				// ... and the backwards ray
				ray[1] = new Ray2D(point[RAY_START_POINT], dC.getProductWith(-1));
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
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.RED);

		for(int p=0; p<point.length; p++)
		{
			int r = point[p].getRadius();
			int pointI = x2i(point[p].x);
			int pointJ = y2j(point[p].y);
			// is the mouse within the radius from the point?
			if(mouseNearPoint == p)
				g2.fillOval(x2i(point[p].x)-r, y2j(point[p].y)-r, 2*r, 2*r);
			else
				g2.drawOval(x2i(point[p].x)-r, y2j(point[p].y)-r, 2*r, 2*r);			
		}
	}

	
	
	// MouseListener methods
	
    private int mouseI, mouseJ;
    private int mouseNearPoint = -1;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)  // double click
		{
			if(mouseNearPoint == RAY_START_POINT)
			{
				// mouse double-clicked near point 2
				rayBundle = !rayBundle;
				repaint();
			}
			else if(mouseNearPoint == RAY_POINT2)
			{
				// mouse double-clicked near point 2
				forwardTraceOnly = !forwardTraceOnly;
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
				
				Vector2D startPoint2Point2 = Vector2D.difference(point[RAY_POINT2], point[RAY_START_POINT]);
				System.out.println("startPoint2Point2="+startPoint2Point2);

				point[RAY_START_POINT].x = i2x(e.getX());
				point[RAY_START_POINT].y = j2y(e.getY());

				// move point[RAY_POINT2] by the same amount
				point[RAY_POINT2].x = point[RAY_START_POINT].x + startPoint2Point2.x;
				point[RAY_POINT2].y = point[RAY_START_POINT].y + startPoint2Point2.y;

				repaint();
			}
			else 
			{
				// the mouse is close to another point
				point[mouseNearPoint].x = i2x(e.getX());
				point[mouseNearPoint].y = j2y(e.getY());
				repaint();
			}
		}

//		if(mouseNearPoint != -1)
//		{
//			point[mouseNearPoint].x = i2x(e.getX());
//			point[mouseNearPoint].y = j2y(e.getY());
//			repaint();
//		}
//		point[RAY_POINT2].x = i2x(e.getX());
//		point[RAY_POINT2].y = j2y(e.getY());
//		currentX = e.getX();
//		currentY = e.getY();
//		oldX = currentX;
//		oldY = currentY;
//		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseI = e.getX();
		mouseJ = e.getY();
		
		for(int p=0; p<point.length; p++)
		{
			int r = point[p].getRadius();
			int pointI = x2i(point[p].x);
			int pointJ = y2j(point[p].y);
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
			mouseNearPoint = -1;
			repaint();
		}
	}
}