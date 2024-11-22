package optics.rayplay.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import math.MyMath;
import math.Vector2D;
import optics.rayplay.geometry2D.Circle2D;
import optics.rayplay.interactiveOpticalComponents.IdeaLensWormhole2D;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;
import optics.rayplay.interactiveOpticalComponents.LensStar2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.raySources.PointRaySource2D;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;

/**
 * @author johannes
 *
 */
public class RayPlay2DPanel extends JPanel implements CoordinateConverterXY2IJ, MouseListener, MouseMotionListener //, KeyListener
{
	private static final long serialVersionUID = 6137072632788997510L;

	/**
	 * a good distance, not too small, not too big, to be easily visible
	 */
	public static final int GOOD_DISTANCE = 70;
	
	//	private static final String RAYS_START_POINT_NAME = "Start point of ray(s)";
	//	private static final String RAYS_CHARACTERISTICS_POINT_NAME = "Point controlling ray/ray bundle characteristics";
	// private static final String NUMBER_OF_RAYS_POINT_NAME = "Point controlling number of rays";


	// the interactive optical components
	public ArrayList<InteractiveOpticalComponent2D> iocs = new ArrayList<InteractiveOpticalComponent2D>();

//	// the light sources
//	public ArrayList<PointRaySource2D> lss = new ArrayList<PointRaySource2D>();

	// the optical components
	// public OpticalComponentCollection2D opticalComponents = new OpticalComponentCollection2D("Components");

	// the graphic elements;
	// note that these get drawn in the order in which they appear in the ArrayList
	// public ArrayList<GraphicElement2D> graphicElements = new ArrayList<GraphicElement2D>();

	// the rays
	// private ArrayList<Ray2D> rays = new ArrayList<Ray2D>();

	// scaled coordinates
	private double xCentre;
	private double yCentre;
	private double xWidth;
	private double yHeight;


	// internal variables
	
	private transient int mouseI, mouseJ, mousePressedI, mousePressedJ;
	private transient double mouseX, mouseY, mousePressedX, mousePressedY, mouseDraggedToX, mouseDraggedToY;
	private transient double mousePressedXCentre, mousePressedYCentre;

	/**
	 * the GraphicElement2D that's currently close to the mouse
	 */
	private transient GraphicElement2D graphicElementNearMouse;
	private transient InteractiveOpticalComponent2D iocNearMouse;
	private transient HashSet<InteractiveOpticalComponent2D> selectedIOCs = new HashSet<InteractiveOpticalComponent2D>();

	
	
	// constructor

	public RayPlay2DPanel()
	{
		super();

		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(420,420));
		setBackground(new java.awt.Color(255, 255, 255));
		// setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));


		// create the 


		// set the parameters
		double h = 1;

		OmnidirectionalLens2D ol = new OmnidirectionalLens2D(
				"Omnidirectional lens 1",	// name
				0.1*h,	// fD
				0.5*h,	// rD
				h/3.,	// h1
				2.*h/3.,	// h2
				h,	// h
				new Vector2D(0, -0.5*h),	// pD
				new Vector2D(1, 0),	// dHat
				new Vector2D(0, 1),	// cHat
				false,
				this
				);
		iocs.add(ol);

		// ray / ray-bundle parameters
		PointRaySource2D ls1 = new PointRaySource2D(
				"Point ray source 1",	// name
				new Vector2D(-0.5*h, 0),	// raysStartPoint
				MyMath.deg2rad(0), // centralRayAngle
				false,	// forwardRaysOnly
				true, // rayBundle
				false,	// rayBundleIsotropic
				MyMath.deg2rad(30), // rayBundleAngle
				4,	// rayBundleNoOfRays
				Colour.RED,
				255,	// maxTraceLevel
				this
				);
		iocs.add(ls1);
		
//		// add to the list of graphic elements the graphic elements associated with the interactive optical components...
//		for(InteractiveOpticalComponent2D ioc:iocs)
//			graphicElements.addAll(ioc.getGraphicElements());
//
//		// ... and with the light sources
//		for(PointRaySource2D ls:lss)
//			graphicElements.addAll(ls.getGraphicElements());

		xCentre = 0;
		yCentre = 0;
		yHeight = 2*h;
		// xMin and xMax get set in paintComponent()

		initPopups();

		addMouseListener(this);
		addMouseMotionListener(this);

		//		setFocusable(true);
		//		requestFocus();
		//		addKeyListener(this);

//		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('+'), "addRay");
//		getActionMap().put("addRay", new AddRayAction());
//
//		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('-'), "subtractRay");
//		getActionMap().put("subtractRay", new SubtractRayAction());
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

		printStream.println("\n*** Parameters ***");

		for(InteractiveOpticalComponent2D ioc:iocs)
			ioc.writeParameters(printStream);

//		for(PointRaySource2D ls:lss)
//			ls.writeParameters(printStream);
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

	/**
	 * Save image as SVG called <filename>.svg
	 * @param filename
	 */
	public void saveSVG(String filename)
	{
		System.out.println("Saving image as SVG...");

		try {
			SVGWriter.startSVGFile(filename, getSize());

//			// draw rays
//			for(PointRaySource2D ls:lss)
//				ls.writeSVGCode(this);

			// draw the interactive optical components
			for(InteractiveOpticalComponent2D ioc:iocs)
				ioc.writeSVGCode(this);
//				for(GraphicElement2D g:ioc.getGraphicElements())
//					g.writeSVGCode(this);
//			{
//				for(OLLensType type:OLLensType.values())
//					if(o.getLens(type) != null) SVGWriter.writeSVGLine(o.getLens(type).getA(), o.getLens(type).getB(), this, "cyan", 3, "opacity=\"0.7\"");
//			}

			SVGWriter.endSVGFile();

			System.out.println("...done");

		} catch (Exception e) {
			System.err.println("...actually not: error");
			System.err.flush();
			e.printStackTrace();
		}
	}


	// setters & getters

	//
	// CoordinateConverterXY2IJ methods
	// scaled coordinates; the simulation uses (double x, double y); the panel uses (int i, int j)
	//

	@Override
	public double x2id(double x)
	{
		return getSize().width*((x-(xCentre-0.5*xWidth))/xWidth);
	}

	@Override
	public double y2jd(double y)
	{
		return getSize().height*((-y+(yCentre+0.5*yHeight))/yHeight);
	}

	@Override
	public int x2i(double x)
	{
		return (int)(x2id(x) + 0.5);
	}

	@Override
	public int y2j(double y)
	{
		return (int)(y2jd(y) + 0.5);
	}

	@Override
	public double i2x(double i)
	{
		return i/getSize().width*xWidth + (xCentre-0.5*xWidth);
	}

	@Override
	public double j2y(double j)
	{
		return -(j/getSize().height*yHeight - (yCentre+0.5*yHeight));
	}

	/**
	 * Adjust xWidth, yHeight, xCentre and yCentre such that the panel is zoomed by a factor f relative to position (i,j)
	 * @param i
	 * @param j
	 * @param f
	 */
	public void zoom(int i, int j, double f)
	{
		// basically solve
		// x(xCentreOld, xWidthOld , i) == x(xCentreNew, f*xWidthOld , i) for xCentreNew and
		// y(yCentreOld, yHeightOld, j) == y(yCentreNew, f*yHeightOld, j) for yCentreNew
		xCentre = xCentre - (f-1)*(2*popupMenuI-getSize().width)*xWidth/(2*getSize().width);
		yCentre = yCentre + (f-1)*(2*popupMenuJ-getSize().height)*yHeight/(2*getSize().height);
		xWidth *= f;
		yHeight *= f;
	}
	
	/**
	 * @return	a circle, in (x,y) coordinates, that completely encloses this RayPlay2D panel
	 */
	public Circle2D getEnclosingCircle()
	{
		return new Circle2D(
				new Vector2D(xCentre, yCentre),	// centre
				0.5*Math.sqrt(xWidth*xWidth + yHeight*yHeight)	// as, by Pythagoras, (width/2)^2 + (height/2)^2 = (min. radius)^2
			);
	}

	public double getGoodDistanceXY()
	{
		return xWidth*GOOD_DISTANCE/getSize().width;
	}

	private ArrayList<OpticalComponent2D> collectOpticalComponents()
	{
		ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
		
		for(InteractiveOpticalComponent2D ioc:iocs)
			opticalComponents.addAll(ioc.getOpticalComponents());
		
		return opticalComponents;
	}
	
	public ArrayList<PointRaySource2D> collectPointRaySources()
	{
		ArrayList<PointRaySource2D> pointRaySources = new ArrayList<PointRaySource2D>();

		for(InteractiveOpticalComponent2D ioc:iocs)
			if(ioc instanceof PointRaySource2D) pointRaySources.add((PointRaySource2D)ioc);
		
		return pointRaySources;
	}

//	private ArrayList<GraphicElement2D> collectGraphicElements1()
//	{
//		ArrayList<GraphicElement2D> graphicElements = new ArrayList<GraphicElement2D>();
//		
//		for(InteractiveOpticalComponent2D ioc:iocs)
//			graphicElements.addAll(ioc.getGraphicElements());
//		
////		for(PointRaySource2D ls:lss)
////			graphicElements.addAll(ls.getGraphicElements());
//		
//		return graphicElements;
//	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// make sure the aspect ratio is correct
		Dimension size = getSize();
		double aspectRatio = ((double)size.width)/((double)size.height);
		xWidth = yHeight*aspectRatio;

		// draw into a Graphics2D (much easier than Graphics)
		Graphics2D g2 = (Graphics2D)g;

		// draw all graphic elements
		// go through the GraphicElements2D in reverse order so that the one that's drawn last (i.e. on top) is checked first
		// ArrayList<GraphicElement2D> graphicElementsR1 = collectGraphicElements();
		// Collections.reverse(graphicElementsR);
		
		// initialise all light rays
		for(InteractiveOpticalComponent2D ioc:iocs)
		{
			ioc.initialiseRays();
		}

		// draw graphic elements
		for(InteractiveOpticalComponent2D ioc:iocs)
			ioc.drawGraphicElementsBehind(this, g2, selectedIOCs.contains(ioc), iocNearMouse == ioc, graphicElementNearMouse, mouseI, mouseJ);

		for(InteractiveOpticalComponent2D ioc:iocs)
			ioc.drawGraphicElements(this, g2, selectedIOCs.contains(ioc), iocNearMouse == ioc, graphicElementNearMouse, mouseI, mouseJ);

//		// initialise all light sources, ...
//		for(InteractiveOpticalComponent2D ioc:iocs)
//		{
//			ioc.initialiseRays();
//		}
		
		// trace the rays from all light sources through the optical components
		for(InteractiveOpticalComponent2D ioc:iocs)
			for(LightRay2D ray:ioc.getRays())
				ray.traceThrough(collectOpticalComponents());
		
		// draw rays
		for(InteractiveOpticalComponent2D ioc:iocs)
			ioc.drawRays(this, g2, graphicElementNearMouse, mouseI, mouseJ);

		// and draw all the GraphicElements
		// go through the GraphicElements2D in reverse order so that the one that's drawn last (i.e. on top) is checked first
		for(InteractiveOpticalComponent2D ioc:iocs)
			ioc.drawGraphicElementsInFront(this, g2, selectedIOCs.contains(ioc), iocNearMouse == ioc, graphicElementNearMouse, mouseI, mouseJ);

		// finally, draw additional information for the element the mouse is close to (if it is close to one)
		if(graphicElementNearMouse != null)
			graphicElementNearMouse.drawAdditionalInfoWhenMouseNear(this, g2, mouseI, mouseJ);
		
//		if(iocNearMouse != null)
//			iocNearMouse.drawAdditionalInfoWhenMouseNear(this, g2, mouseI, mouseJ);
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

	public void drawTriangle(Vector2D point1, Vector2D point2, Vector2D point3, boolean fill, Graphics2D g2)
	{
		int[] xPoints = {x2i(point1.x), x2i(point2.x), x2i(point3.x)};
		int[] yPoints = {y2j(point1.y), y2j(point2.y), y2j(point3.y)};

		if(fill)
			g2.fillPolygon(xPoints, yPoints, 3);
		else
			g2.drawPolygon(xPoints, yPoints, 3);
	}
	
	public void drawCircle(Vector2D centre, double radius, Graphics2D g2)
	{
		int radiusIJ = (int)(getSize().width*radius/xWidth + 0.5);
		g2.drawOval(x2i(centre.x)-radiusIJ, y2j(centre.y)-radiusIJ, 2*radiusIJ, 2*radiusIJ);	
	}

	public void drawSector(Vector2D centre, double radius, double phi0, double deltaPhi, Graphics2D g2)
	{
		int radiusIJ = (int)(getSize().width*radius/xWidth + 0.5);
		g2.fillArc(x2i(centre.x)-radiusIJ, y2j(centre.y)-radiusIJ, 2*radiusIJ, 2*radiusIJ, (int)(MyMath.rad2deg(phi0)+0.5), (int)(MyMath.rad2deg(deltaPhi)+0.5));	
	}


	// MouseListener methods

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(!e.isPopupTrigger())
		{
			if(graphicElementNearMouse != null)
			{
				// some element is near the mouse; ask it to handle the click
				if(!graphicElementNearMouse.mouseClicked(this, true, e))
				{
					// the selected element didn't handle the event
					// add graphicElementNearMouse to the selected graphic elements
					if(e.isShiftDown())
					{
						// shift-click -- add iocNearMouse to the list of selected IOCs
						selectedIOCs.add(iocNearMouse);
					}
					else
					{
						// no modifiers -- straight click;
						// make the IOC near the mouse the only selected IOC
						selectedIOCs.clear();
						selectedIOCs.add(iocNearMouse);
					}
				}
			}
			else
			{
				// click in the middle of nowhere
				selectedIOCs.clear();
			}
			
//			System.out.println("Selected optical components:");
//			for(InteractiveOpticalComponent2D ioc:selectedIOCs)
//			{
//				System.out.println("  "+ioc.getName());
//			}

			repaint();
		}		
	}
	

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedI = e.getX();
		mousePressedJ = e.getY();
		mousePressedX = i2x(mousePressedI);
		mousePressedY = j2y(mousePressedJ);
		mouseDraggedToX = mousePressedX;
		mouseDraggedToY = mousePressedY;
		mousePressedXCentre = xCentre;
		mousePressedYCentre = yCentre;
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
//		boolean eventHandled = false;

		// run each GraphicElement2D's mousePressed method...
//		for(GraphicElement2D g:graphicElements)
//		{
//			eventHandled = g.mousePressed(this, graphicElementNearMouse == g, e);
//
//			if(eventHandled)
//			{
//				// ... until one has handled the event
//				break;
//			}
//		}
		if(iocNearMouse != null)
		{
			boolean eventHandled = iocNearMouse.mousePressed(this, true, e);
			if(!eventHandled)
				if(graphicElementNearMouse != null)
				{
					// eventHandled = 
					graphicElementNearMouse.mousePressed(this, true, e);
				}
		}
		else
		{
			// do anything that needs to happen here and that isn't done by the graphic elements
			if (e.isPopupTrigger())
			{
				// the mouse isn't near any graphicElement -- show the (empty)spacePopup
				popupMenuI = e.getX();
				popupMenuJ = e.getY();
				spacePopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

//		if(!eventHandled)
//		{
//			// do anything that needs to happen here and that isn't done by the graphic elements
//			if (e.isPopupTrigger())
//			{
//				if(graphicElementNearMouse == null)
//				{
//					// the mouse isn't near any graphicElement -- show the (empty)spacePopup
//					popupMenuX = e.getX();
//					popupMenuY = e.getY();
//					spacePopup.show(e.getComponent(), e.getX(), e.getY());
//				}
//			}
//		}

		repaint();
		//		showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouseI = e.getX();
		mouseJ = e.getY();
		mouseX = i2x(mouseI);
		mouseY = j2y(mouseJ);
		

//		for(GraphicElement2D g:graphicElements)
//		{
//			g.mouseDragged(this, g==graphicElementNearMouse, e.getX(), e.getY());
//		}
		
		Vector2D delta = new Vector2D(mouseX - mouseDraggedToX, mouseY - mouseDraggedToY);

		if(graphicElementNearMouse != null)
		{
			if(!graphicElementNearMouse.mouseDragged(this, true, e.getX(), e.getY()))
			{
				for(InteractiveOpticalComponent2D ioc:selectedIOCs)
					ioc.move(delta);

				if(iocNearMouse != null)
					if(!selectedIOCs.contains(iocNearMouse))
						iocNearMouse.move(delta);
			}
		}
		else
		{
			// solve x(xCentreOld, mousePressedI) == x(xCentreNew, mouseI) for xCentreNew etc.
			xCentre = mousePressedXCentre - (mouseI - mousePressedI)*xWidth/getSize().width;
			yCentre = mousePressedYCentre + (mouseJ - mousePressedJ)*yHeight/getSize().height;
			// xCentre -= delta.x;
			// yCentre -= delta.y;
		}

		mouseDraggedToX = mouseX;
		mouseDraggedToY = mouseY;

//		if(graphicElementNearMouse != null)
//		{
//			if(!graphicElementNearMouse.mouseDragged(this, true, e.getX(), e.getY()))
//			{
//				Vector2D delta = new Vector2D(mouseX - mouseDraggedToX, mouseY - mouseDraggedToY);
//
//				for(InteractiveOpticalComponent2D ioc:selectedIOCs)
//					ioc.move(delta);
//
//				if(iocNearMouse != null)
//					if(!selectedIOCs.contains(iocNearMouse))
//						iocNearMouse.move(delta);
//
//				mouseDraggedToX = mouseX;
//				mouseDraggedToY = mouseY;
//			}
//		}
//		else
//		{
//			xCentre = mousePressedXCentre - (mouseX - mousePressedX);
//			yCentre = mousePressedYCentre - (mouseY - mousePressedY);
//		}
		
		repaint();
	}

	public void setElementsNearMouse()
	{
		for(InteractiveOpticalComponent2D ioc:iocs)
			for(GraphicElement2D g:ioc.getGraphicElements(selectedIOCs.contains(ioc), false))
		{
			if(g.isInteractive())
			{
				if(g.isMouseNear(this, mouseI, mouseJ))
				{
					graphicElementNearMouse = g;
					iocNearMouse = ioc;
					return;
				}
			}
		}
		
		// none of the graphic elements are near the mouse
		graphicElementNearMouse = null;
		iocNearMouse = null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseI = e.getX();
		mouseJ = e.getY();

		GraphicElement2D oldGraphicElementNearMouse = graphicElementNearMouse;
		// is the mouse near any of the GraphicElement2Ds?
		setElementsNearMouse();
		if(oldGraphicElementNearMouse != graphicElementNearMouse)
		{
			// the GraphicElement2D near the mouse has changed
			repaint();
		}
	}

//	public class AddRayAction extends AbstractAction
//	{
//		private static final long serialVersionUID = -3396160829458102617L;
//
//		public void actionPerformed(ActionEvent e)
//		{
//			// increase the number of rays in all light sources
//			for(PointRaySource2D ls:lss)
//			{
//				ls.setRayBundleNoOfRays(ls.getRayBundleNoOfRays()+1);
//			}
//			repaint();
//		}
//	}
//
//	public class SubtractRayAction extends AbstractAction
//	{
//		private static final long serialVersionUID = 3631627329424475924L;
//
//		public void actionPerformed(ActionEvent e)
//		{
//			// decrease the number of rays in all light sources
//			for(PointRaySource2D ls:lss)
//			{
//				if(ls.getRayBundleNoOfRays() > 2) ls.setRayBundleNoOfRays(ls.getRayBundleNoOfRays()-1);
//				else ls.setRayBundleNoOfRays(2);
//			}
//			repaint();
//		}
//	}


	// popup menus

    private JFileChooser fileChooser;

	final JPopupMenu spacePopup = new JPopupMenu();
	private int popupMenuI, popupMenuJ;
	private int
		lensNo = 0,
		lensStarNo = 0,
		omnidirectionalLensNo = 1,	// 1 because one gets created at the start
		raySourceNo = 1,	// 1 because one gets created at the start
		ideaLensWormholeNo = 0;

	// menu items
	JMenuItem forwardRaysOnlyMenuItem, rayBundleMenuItem, rayBundleIsotropicMenuItem;

	private void initPopups()
	{
		// the (empty)space popup

		JMenuItem createLensMenuItem = new JMenuItem("Create lens");
		createLensMenuItem.getAccessibleContext().setAccessibleDescription("Create lens");
		createLensMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				lensNo++;
				Vector2D p = new Vector2D(i2x(popupMenuI), j2y(popupMenuJ));
				Lens2DIOC lens = new Lens2DIOC(
						"Lens "+lensNo,	// name
						p,	// principalPoint
						1,	// focalLength
						Vector2D.sum(p, new Vector2D(0, +0.5)),	// endPoint1
						Vector2D.sum(p, new Vector2D(0, -0.5)),	// endPoint2
						RayPlay2DPanel.this
						);
				iocs.add(lens);
				// graphicElements.addAll(lens.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createLensMenuItem);

		// Separator
		// spacePopup.addSeparator();

		JMenuItem createLensStarMenuItem = new JMenuItem("Create lens star");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		createLensStarMenuItem.getAccessibleContext().setAccessibleDescription("Create lens star");
		createLensStarMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				lensStarNo++;
				LensStar2D ls = new LensStar2D(
						"Lens star "+lensStarNo,	// name
						0.1,	// f
						10,	// n
						0,	// rP
						new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)),	// c,
						0.5,	// r,
						0,	// phi0
						RayPlay2DPanel.this
					);
				iocs.add(ls);
				// opticalComponents.addAll(ol.getOpticalComponents());
				// graphicElements.addAll(ls.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createLensStarMenuItem);

		// Separator
		// spacePopup.addSeparator();

		JMenuItem createOmnidirectionalLensMenuItem = new JMenuItem("Create omnidirectional lens");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		createOmnidirectionalLensMenuItem.getAccessibleContext().setAccessibleDescription("Create omnidirectional lens");
		createOmnidirectionalLensMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				double h = 1;

				omnidirectionalLensNo++;
				OmnidirectionalLens2D ol = new OmnidirectionalLens2D(
						"Omnidirectional lens "+omnidirectionalLensNo,	// name
						0.1*h,	// fD
						0.5*h,	// rD
						h/3.,	// h1
						2.*h/3.,	// h2
						h,	// h
						new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)),	// pD
						new Vector2D(1, 0),	// dHat
						new Vector2D(0, 1),	// cHat
						false,
						RayPlay2DPanel.this
					);
				iocs.add(ol);
				// opticalComponents.addAll(ol.getOpticalComponents());
				// graphicElements.addAll(ol.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createOmnidirectionalLensMenuItem);
		
		
		JMenuItem createWormholeMenuItem = new JMenuItem("Create ideal lens wormhole");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		createWormholeMenuItem.getAccessibleContext().setAccessibleDescription("Create ideal lens wormhole");
		createWormholeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				double hO = 1;
				double hI = 0.5;

				ideaLensWormholeNo++;
				IdeaLensWormhole2D ILW = new IdeaLensWormhole2D(
						"Ideal lens wormhole "+ideaLensWormholeNo,// name,
						0.1*hO,	// fDO
						0.5*hO,	// rDO
						2.*hO/3.,	// h1O
						3.*hO/4.,// h2O,
						hO,// hO,
						new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)),	//pDO,
						0.1*hI,	// fDI
						0.5*hI,	// rDI
						2.*hI/3.,	// h1I
						3.*hI/4.,// h2I,
						hI,// hI,
						Vector2D.sum(new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)), new Vector2D(0, 0.15*hI)),// pDI,
						0.1,// f1,
						0.1,// f2,
						Vector2D.sum(new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)),new Vector2D(0,-0.1*hI)),
						new Vector2D(1, 0),	// dHat
						new Vector2D(0, 1),	// cHat
						RayPlay2DPanel.this
					);
				iocs.add(ILW);
				// opticalComponents.addAll(ol.getOpticalComponents());
				// graphicElements.addAll(ol.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createWormholeMenuItem);

		// Separator
		// spacePopup.addSeparator();

		JMenuItem createLightSourceMenuItem = new JMenuItem("Create light source");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		createLightSourceMenuItem.getAccessibleContext().setAccessibleDescription("Create light source");
		createLightSourceMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ray / ray-bundle parameters
				raySourceNo++;
				PointRaySource2D ls = new PointRaySource2D(
						"Point ray source "+raySourceNo,	// name
						new Vector2D(i2x(popupMenuI), j2y(popupMenuJ)),	// raysStartPoint
						MyMath.deg2rad(0), // centralRayAngle
						false,	// forwardRaysOnly
						true, // rayBundle
						false,	// rayBundleIsotropic
						MyMath.deg2rad(30), // rayBundleAngle
						4,	// rayBundleNoOfRays
						Colour.GREEN,
						255,	// maxTraceLevel
						RayPlay2DPanel.this
						);
				iocs.add(ls);
				// graphicElements.addAll(ls.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createLightSourceMenuItem);

		// Separator
		spacePopup.addSeparator();

		JMenuItem zoomInMenuItem = new JMenuItem("Zoom in");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		zoomInMenuItem.getAccessibleContext().setAccessibleDescription("Zoom in");
		zoomInMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoom(popupMenuI, popupMenuJ, 0.5);
				repaint();
			}
		});
		spacePopup.add(zoomInMenuItem);

		JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		zoomOutMenuItem.getAccessibleContext().setAccessibleDescription("Zoom out");
		zoomOutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoom(popupMenuI, popupMenuJ, 2);
				repaint();
			}
		});
		spacePopup.add(zoomOutMenuItem);
		
		// Separator
		spacePopup.addSeparator();

		JMenuItem saveMenuItem = new JMenuItem("Save...");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		saveMenuItem.getAccessibleContext().setAccessibleDescription("Save");
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fileChooser == null)
				{
					fileChooser = new JFileChooser();
					fileChooser.setSelectedFile(new File("RayPlay2DSimulation"));
				}
				int returnVal = fileChooser.showSaveDialog(RayPlay2DPanel.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fileChooser.getSelectedFile();
	                
	                saveSVG(file.getAbsolutePath());
	    			saveParameters(file.getAbsolutePath());

	    			System.out.println("SVG image and parameters saved as \""+file.getAbsolutePath()+"\".");
	            }
				else
				{
					System.out.println("Saving cancelled.");
				}
			}
		});
		spacePopup.add(saveMenuItem);
	}
}