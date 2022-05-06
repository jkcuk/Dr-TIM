package optics.rayplay.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import math.MyMath;
import math.Vector2D;
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

	//	private static final String RAYS_START_POINT_NAME = "Start point of ray(s)";
	//	private static final String RAYS_CHARACTERISTICS_POINT_NAME = "Point controlling ray/ray bundle characteristics";
	// private static final String NUMBER_OF_RAYS_POINT_NAME = "Point controlling number of rays";


	// the interactive optical components
	public ArrayList<InteractiveOpticalComponent2D> iocs = new ArrayList<InteractiveOpticalComponent2D>();

	// the light sources
	public ArrayList<PointRaySource2D> lss = new ArrayList<PointRaySource2D>();

	// the optical components
	// public OpticalComponentCollection2D opticalComponents = new OpticalComponentCollection2D("Components");

	// the graphic elements;
	// note that these get drawn in the order in which they appear in the ArrayList
	public ArrayList<GraphicElement2D> graphicElements = new ArrayList<GraphicElement2D>();

	// the rays
	// private ArrayList<Ray2D> rays = new ArrayList<Ray2D>();

	// scaled coordinates
	private double xCentre;
	private double yCentre;
	private double xWidth;
	private double yHeight;



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
				new Vector2D(0, 1)	// cHat
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
				255	// maxTraceLevel
				);
		lss.add(ls1);
		
		// add to the list of graphic elements the graphic elements associated with the interactive optical components...
		for(InteractiveOpticalComponent2D ioc:iocs)
			graphicElements.addAll(ioc.getGraphicElements());

		// ... and with the light sources
		for(PointRaySource2D ls:lss)
			graphicElements.addAll(ls.getGraphicElements());

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

		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('+'), "addRay");
		getActionMap().put("addRay", new AddRayAction());

		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('-'), "subtractRay");
		getActionMap().put("subtractRay", new SubtractRayAction());
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

		for(PointRaySource2D ls:lss)
			ls.writeParameters(printStream);
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

			// draw rays
			for(PointRaySource2D ls:lss)
				ls.writeSVGCode(this);

			// draw the interactive optical components
			for(InteractiveOpticalComponent2D ioc:iocs)
				for(GraphicElement2D g:ioc.getGraphicElements())
					g.writeSVGCode(this);
//			{
//				for(OLLensType type:OLLensType.values())
//					if(o.getLens(type) != null) SVGWriter.writeSVGLine(o.getLens(type).getA(), o.getLens(type).getB(), this, "cyan", 3, "opacity=\"0.7\"");
//			}

			SVGWriter.endSVGFile();

			System.out.println("...done");

		} catch (FileNotFoundException e) {
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
		for(int i=graphicElements.size()-1; i>=0; i--)
			// for(GraphicElement2D g:graphicElements)
		{
			GraphicElement2D ge = graphicElements.get(i);
			ge.draw(this, g2, graphicElementNearMouse == ge, mouseI, mouseJ);
		}

		// initialise all light sources, ...
		for(PointRaySource2D ls:lss)
		{
			ls.initialiseRays();
		}
		
		// ... gather together all the optical components, ...
		ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
		for(InteractiveOpticalComponent2D ioc:iocs)
			opticalComponents.addAll(ioc.getOpticalComponents());

		// ... and trace the rays from all light sources through the optical components
		for(PointRaySource2D ls:lss)
			for(Ray2D ray:ls.getRays())
				ray.traceThrough(opticalComponents);


		// draw rays
		for(PointRaySource2D ls:lss)
			ls.drawRays(this, g2);

		// and draw all the GraphicElements
		// go through the GraphicElements2D in reverse order so that the one that's drawn last (i.e. on top) is checked first
		for(int i=graphicElements.size()-1; i>=0; i--)
			// for(GraphicElement2D g:graphicElements)
		{
			GraphicElement2D ge = graphicElements.get(i);
			ge.drawOnTop(this, g2, graphicElementNearMouse == ge, mouseI, mouseJ);
		}

		// finally, draw additional information for the element the mouse is close to (if it is close to one)
		if(graphicElementNearMouse != null)
			graphicElementNearMouse.drawAdditionalInfoWhenMouseNear(this, g2, mouseI, mouseJ);
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



	// MouseListener methods

	private int mouseI, mouseJ;

	/**
	 * the GraphicElement2D that's currently close to the mouse
	 */
	private GraphicElement2D graphicElementNearMouse;

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// run each GraphicElement2D's mouseClicked method
		for(GraphicElement2D g:graphicElements)
			g.mouseClicked(this, graphicElementNearMouse == g, e);

		repaint();
	}
	
	int mousePressedI, mousePressedJ;
	double mousePressedXCentre, mousePressedYCentre;

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedI = e.getX();
		mousePressedJ = e.getY();
		mousePressedXCentre = xCentre;
		mousePressedYCentre = yCentre;
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		boolean eventHandled = false;

		// run each GraphicElement2D's mousePressed method...
		for(GraphicElement2D g:graphicElements)
		{
			eventHandled = g.mousePressed(this, graphicElementNearMouse == g, e);

			if(eventHandled)
			{
				// ... until one has handled the event
				break;
			}
		}

		if(!eventHandled)
		{
			// do anything that needs to happen here and that isn't done by the graphic elements
			if (e.isPopupTrigger())
			{
				if(graphicElementNearMouse == null)
				{
					// the mouse isn't near any graphicElement -- show the (empty)spacePopup
					popupMenuX = e.getX();
					popupMenuY = e.getY();
					spacePopup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

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

//		for(GraphicElement2D g:graphicElements)
//		{
//			g.mouseDragged(this, g==graphicElementNearMouse, e.getX(), e.getY());
//		}
		
		if(graphicElementNearMouse != null)
		{
			graphicElementNearMouse.mouseDragged(this, true, e.getX(), e.getY());
		}
		else
		{
			xCentre = mousePressedXCentre - i2x(mouseI) + i2x(mousePressedI);
			yCentre = mousePressedYCentre - j2y(mouseJ) + j2y(mousePressedJ);
		}
		repaint();
	}

	public GraphicElement2D topGraphicElementNearMouse()
	{
		for(GraphicElement2D g:graphicElements)
		{
			if(g.isInteractive())
			{
				if(g.isMouseNear(this, mouseI, mouseJ))
				{
					return g;
				}
			}
		}

		// the mouse isn't close to any GraphicElement
		return null;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseI = e.getX();
		mouseJ = e.getY();

		// is the mouse near any of the GraphicElement2Ds?
		GraphicElement2D g = topGraphicElementNearMouse();
		if(g != graphicElementNearMouse)
		{
			// the GraphicElement2D near the mouse has changed
			graphicElementNearMouse = g;
			repaint();
		}
	}

	public class AddRayAction extends AbstractAction
	{
		private static final long serialVersionUID = -3396160829458102617L;

		public void actionPerformed(ActionEvent e)
		{
			// increase the number of rays in all light sources
			for(PointRaySource2D ls:lss)
			{
				ls.setRayBundleNoOfRays(ls.getRayBundleNoOfRays()+1);
			}
			repaint();
		}
	}

	public class SubtractRayAction extends AbstractAction
	{
		private static final long serialVersionUID = 3631627329424475924L;

		public void actionPerformed(ActionEvent e)
		{
			// decrease the number of rays in all light sources
			for(PointRaySource2D ls:lss)
			{
				if(ls.getRayBundleNoOfRays() > 2) ls.setRayBundleNoOfRays(ls.getRayBundleNoOfRays()-1);
				else ls.setRayBundleNoOfRays(2);
			}
			repaint();
		}
	}


	// popup menus

	final JPopupMenu spacePopup = new JPopupMenu();
	private int popupMenuX, popupMenuY;
	private int
		lensNo = 1,
		lensStarNo = 1,
		omnidirectionalLensNo = 1,
		raySourceNo = 1;

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
				Vector2D p = new Vector2D(i2x(popupMenuX), j2y(popupMenuY));
				Lens2DIOC lens = new Lens2DIOC(
						"Lens "+lensNo,	// name
						p,	// principalPoint
						1,	// focalLength
						Vector2D.sum(p, new Vector2D(0, +0.5)),	// endPoint1
						Vector2D.sum(p, new Vector2D(0, -0.5))	// endPoint2
						);
				iocs.add(lens);
				graphicElements.addAll(lens.getGraphicElements());

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
						new Vector2D(i2x(popupMenuX), j2y(popupMenuY)),	// c,
						0.5,	// r,
						0	// phi0
						);
				iocs.add(ls);
				// opticalComponents.addAll(ol.getOpticalComponents());
				graphicElements.addAll(ls.getGraphicElements());

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
						new Vector2D(i2x(popupMenuX), j2y(popupMenuY)),	// pD
						new Vector2D(1, 0),	// dHat
						new Vector2D(0, 1)	// cHat
						);
				iocs.add(ol);
				// opticalComponents.addAll(ol.getOpticalComponents());
				graphicElements.addAll(ol.getGraphicElements());

				repaint();
			}
		});
		spacePopup.add(createOmnidirectionalLensMenuItem);

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
						new Vector2D(i2x(popupMenuX), j2y(popupMenuY)),	// raysStartPoint
						MyMath.deg2rad(0), // centralRayAngle
						false,	// forwardRaysOnly
						true, // rayBundle
						false,	// rayBundleIsotropic
						MyMath.deg2rad(30), // rayBundleAngle
						4,	// rayBundleNoOfRays
						Colour.GREEN,
						255	// maxTraceLevel
						);
				lss.add(ls);
				graphicElements.addAll(ls.getGraphicElements());

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
				double f = 0.5;	// magnification factor
				// xCentre += (f-1)*xWidth *(getSize().width -2*popupMenuX)/(2*getSize().width);// ((1-f)*popupMenuX*xWidth + xCentre*getSize().width - 0.5*(xWidth-f)*getSize().width)/getSize().width;
				// yCentre += (f-1)*yHeight*(getSize().height-2*popupMenuY)/(2*getSize().height);// ((1-f)*popupMenuY*yHeight + yCentre*getSize().height - 0.5*(yHeight-f)*getSize().height)/getSize().height;
				xWidth *= f;
				yHeight *= f;
				repaint();
			}
		});
		spacePopup.add(zoomInMenuItem);

		JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		zoomOutMenuItem.getAccessibleContext().setAccessibleDescription("Zoom out");
		zoomOutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double f = 2;	// magnification factor
				// xCentre += (f-1)*xWidth *(getSize().width -2*popupMenuX)/(2*getSize().width);// ((1-f)*popupMenuX*xWidth + xCentre*getSize().width - 0.5*(xWidth-f)*getSize().width)/getSize().width;
				// yCentre += (f-1)*yHeight*(getSize().height-2*popupMenuY)/(2*getSize().height);// ((1-f)*popupMenuY*yHeight + yCentre*getSize().height - 0.5*(yHeight-f)*getSize().height)/getSize().height;
				xWidth *= f;
				yHeight *= f;
				repaint();
			}
		});
		spacePopup.add(zoomOutMenuItem);
	}
}