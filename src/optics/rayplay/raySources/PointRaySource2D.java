package optics.rayplay.raySources;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
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
import optics.rayplay.graphicElements.PointRaySourcePointGE2D;
import optics.rayplay.graphicElements.PointRaySourcePointGE2D.PointRaySource2DPointType;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.core.LightRay2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.geometry2D.Line2D;

/**
 * A source of light ray(s)
 * @author johannes
 */
public class PointRaySource2D implements InteractiveOpticalComponent2D
{
	private String name;
	
	private RayPlay2DPanel rayPlay2DPanel;
	
	// ray / ray bundle
	private Vector2D raysStartPoint;

	/**
	 * ignore if null, otherwise constrain rays start point to the line
	 */
	private Line2D lineConstrainingStartPoint = null;


	/**
	 * angle of ray (or, if rayBundle, the central ray in the bundle) with horizontal
	 */
	private double centralRayAngle;

	/**
	 * if false, any rays also start in the opposite direction
	 */
	private boolean forwardRaysOnly;

	private boolean rayBundle;

	private boolean rayBundleIsotropic;

	/**
	 * angle of cone formed by outermost rays in bundle
	 */
	private double rayBundleAngle;

	/**
	 * number of rays in a ray bundle
	 */
	private int rayBundleNoOfRays;
	
	private Colour colour;
	
	private boolean darkenExhaustedRays;
	
	private int maxTraceLevel;
	
//	private Stroke pointStroke;
//	private Color pointColor;


	// internal variables

	// private PointRaySourcePointGE2D rayBundleStartPoint, raysCharacteristicsPoint;
//	private RayBundleStartPoint2D rayBundleStartPoint;
//	private RaysCharacteristicsPoint2D raysCharacteristicsPoint;
	// private NumberOfRaysPoint2D numberOfRaysPoint;

	private ArrayList<LightRay2D> rays;
	
	private ArrayList<GraphicElement2D> displayGraphicElements = new ArrayList<GraphicElement2D>();
	private ArrayList<GraphicElement2D> controlGraphicElements = new ArrayList<GraphicElement2D>();
	
	private HashMap<PointRaySource2DPointType, PointRaySourcePointGE2D> points;


	// constructor

	public PointRaySource2D(
			String name,
			Vector2D raysStartPoint,
			double centralRayAngle,
			boolean forwardRaysOnly,
			boolean rayBundle,
			boolean rayBundleIsotropic,
			double rayBundleAngle,
			int rayBundleNoOfRays,
			Colour colour,
			int maxTraceLevel,
//			Stroke pointStroke,
//			Color pointColor
			RayPlay2DPanel rayPlay2DPanel
		)
	{
		// super(name);

		this.name = name;
		this.raysStartPoint = raysStartPoint;
		this.centralRayAngle = centralRayAngle;
		this.forwardRaysOnly = forwardRaysOnly;
		this.rayBundle = rayBundle;
		this.rayBundleIsotropic = rayBundleIsotropic;
		this.rayBundleAngle = rayBundleAngle;
		this.rayBundleNoOfRays = rayBundleNoOfRays;
		this.colour = colour;
//		this.pointStroke = pointStroke;
//		this.pointColor = pointColor;
		this.darkenExhaustedRays = true;
		this.maxTraceLevel = maxTraceLevel;
		this.rayPlay2DPanel = rayPlay2DPanel;

		points = new HashMap<PointRaySource2DPointType, PointRaySourcePointGE2D>();

		for(PointRaySource2DPointType pt:PointRaySource2DPointType.values())
		{
			PointRaySourcePointGE2D p = new PointRaySourcePointGE2D(this, pt);
			points.put(pt, p);
			switch(pt)
			{
			case S:
				displayGraphicElements.add(p);
				break;
			default:
				controlGraphicElements.add(p);
			}
		}
//		rayBundleStartPoint = new PointRaySourcePointGE2D(
//				raysStartPoint,	// position -- set value later
//				this,	// rs
//				PointRaySource2DPointType.S
//				);
//				// new RayBundleStartPoint2D(RAYS_START_POINT_NAME, raysStartPoint, 5, pointStroke, pointColor, true, this);
//		raysCharacteristicsPoint = new PointRaySourcePointGE2D(
//				new Vector2D(0, 0),	// position -- set value later
//				this,	// rs
//				PointRaySource2DPointType.C
//				);
//				// new RaysCharacteristicsPoint2D(RAYS_CHARACTERISTICS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
//		// numberOfRaysPoint = new NumberOfRaysPoint2D(NUMBER_OF_RAYS_POINT_NAME, new Vector2D(0, 0), 3, pointStroke, pointColor, true, this);
//		
//		graphicElements.add(rayBundleStartPoint);
//		graphicElements.add(raysCharacteristicsPoint);
//		// graphicElements.add(numberOfRaysPoint);
		
		initPopup();
	}



	// setters & getters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector2D getRayStartPoint() {
		return raysStartPoint;
	}

	public void setRayStartPointCoordinatesToThoseOf(Vector2D rayStartPoint) {
		this.raysStartPoint.setCoordinatesToThoseOf(rayStartPoint);
	}

	public Line2D getLineConstrainingStartPoint() {
		return lineConstrainingStartPoint;
	}

	public void setLineConstrainingStartPoint(Line2D lineConstrainingStartPoint) {
		this.lineConstrainingStartPoint = lineConstrainingStartPoint;
	}

//	public Vector2D getRaysCharacteristicsPoint1() {
//		return raysCharacteristicsPoint.getPosition();
//	}
//
//	public void setRaysCharacteristicsPointCoordinatesToThoseOf1(Vector2D rayPoint2) {
//		raysCharacteristicsPoint.setCoordinatesToThoseOf(rayPoint2);
//	}

	public double getRayAngle() {
		return centralRayAngle;
	}

	public void setRayAngle(double rayAngle) {
		this.centralRayAngle = rayAngle;
	}

	public boolean isForwardRaysOnly() {
		return forwardRaysOnly;
	}

	public void setForwardRaysOnly(boolean forwardRaysOnly) {
		this.forwardRaysOnly = forwardRaysOnly;
	}

	public boolean isRayBundle() {
		return rayBundle;
	}

	public void setRayBundle(boolean rayBundle) {
		this.rayBundle = rayBundle;
	}

	public boolean isRayBundleIsotropic() {
		return rayBundleIsotropic;
	}

	public void setRayBundleIsotropic(boolean rayBundleIsotropic) {
		this.rayBundleIsotropic = rayBundleIsotropic;
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
		if((rayBundleNoOfRays > 1) && (rayBundleNoOfRays <= 1000))
			this.rayBundleNoOfRays = rayBundleNoOfRays;
	}

//	public Stroke getPointStroke() {
//		return pointStroke;
//	}
//
//	public void setPointStroke(Stroke pointStroke) {
//		this.pointStroke = pointStroke;
//	}
//
//	public Color getPointColor() {
//		return pointColor;
//	}
//
//	public void setPointColor(Color pointColor) {
//		this.pointColor = pointColor;
//	}

	public Colour getColour() {
		return colour;
	}

	public void setColour(Colour colour) {
		this.colour = colour;
	}

	public int getMaxTraceLevel() {
		return maxTraceLevel;
	}

	public void setMaxTraceLevel(int maxTraceLevel) {
		this.maxTraceLevel = maxTraceLevel;
	}

	public boolean isDarkenExhaustedRays() {
		return darkenExhaustedRays;
	}

	public void setDarkenExhaustedRays(boolean darkenExhaustedRays) {
		this.darkenExhaustedRays = darkenExhaustedRays;
	}

	public ArrayList<LightRay2D> getRays() {
		return rays;
	}

	public void setRays(ArrayList<LightRay2D> rays) {
		this.rays = rays;
	}
	
	public PointRaySourcePointGE2D getPoint(PointRaySource2DPointType type)
	{
		return points.get(type);
	}
	
	public RayPlay2DPanel getRayPlay2DPanel() {
		return rayPlay2DPanel;
	}

	public void setRayPlay2DPanel(RayPlay2DPanel rayPlay2DPanel) {
		this.rayPlay2DPanel = rayPlay2DPanel;
	}





	//


	public Vector2D getCentralRayDirection() {
		return new Vector2D(Math.cos(centralRayAngle), Math.sin(centralRayAngle));
	}

	public void initialiseRays()
	{
		Vector2D dC = getCentralRayDirection();
		rays = new ArrayList<LightRay2D>();
		
		if(lineConstrainingStartPoint != null)
		{
			// use this opportunity to check if the source position is still on the line
			raysStartPoint.setCoordinatesToThoseOf(
					Geometry2D.getPointOnLineClosestToPoint(lineConstrainingStartPoint, raysStartPoint)
				);
		}
		
		if(rayBundle)
		{
			if(rayBundleIsotropic)
			{
				double alpha0 = Math.atan2(dC.y, dC.x);

				for(int r=0; r<rayBundleNoOfRays; r++)
				{
					// calculate the ray direction
					double alpha = alpha0 + r*(2.*Math.PI)/(rayBundleNoOfRays);

					Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

					// initialise the forward ray
					rays.add(new LightRay2D("Ray #"+r+" from "+name, raysStartPoint, d, false, maxTraceLevel, rayPlay2DPanel));
				}
			}
			else
			{
				double alphaC = Math.atan2(dC.y, dC.x);
				double alpha0 = alphaC - 0.5*rayBundleAngle;

				for(int r=0; r<rayBundleNoOfRays; r++)
				{
					// calculate the ray direction
					double alpha;
					if(rayBundleNoOfRays == 1) alpha = alphaC;
					else alpha = alpha0 + r*rayBundleAngle/(rayBundleNoOfRays-1);

					Vector2D d = new Vector2D(Math.cos(alpha),Math.sin(alpha));

					// initialise the forward ray
					rays.add(new LightRay2D("Ray #"+r+" from "+name, raysStartPoint, d, false, maxTraceLevel, rayPlay2DPanel));

					if(!forwardRaysOnly)
					{
						// ... and the backwards ray
						rays.add(new LightRay2D("Backwards ray #"+r+" from "+name, raysStartPoint, d.getProductWith(-1), true, maxTraceLevel, rayPlay2DPanel));
					}
				}
			}
		}
		else
		{
			// initialise the forward ray
			rays.add(new LightRay2D("Ray from "+name, raysStartPoint, dC, false, maxTraceLevel, rayPlay2DPanel));

			if(!forwardRaysOnly)
			{
				// ... and the backwards ray
				rays.add(new LightRay2D("Backwards ray from "+name, raysStartPoint, dC.getProductWith(-1), true, maxTraceLevel, rayPlay2DPanel));
			}
		}
	}
	
	@Override
	public void move(Vector2D delta)
	{
		raysStartPoint.setCoordinatesToThoseOf(Vector2D.sum(raysStartPoint, delta));
		
		if(lineConstrainingStartPoint != null)
		{
			// yes, it's constrained to a line
			raysStartPoint.setCoordinatesToThoseOf(Geometry2D.getPointOnLineClosestToPoint(lineConstrainingStartPoint, raysStartPoint));
		}

		// construct ray point 2 as the new ray start point + d
		// getPoint(PointRaySource2DPointType.D).setCoordinatesToThoseOf(Vector2D.sum(position, d));
	}
	
	@Override
	public void drawRays(RayPlay2DPanel rpp, Graphics2D g2, GraphicElement2D graphicElementNearMouse, int mouseI, int mouseJ)
	// public void drawRays(RayPlay2DPanel rpp, Graphics2D g2)
	{
		// draw a ray
		g2.setStroke(new BasicStroke(1));

		// make everything that gets drawn transparent
		Composite c = g2.getComposite();
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2.setComposite(alcom);

        for(LightRay2D ray:rays)
		{
			ArrayList<Vector2D> t = ray.getTrajectory();
			if(darkenExhaustedRays && (ray.getTraceLevel() == 0))
				g2.setColor(
						colour.getDarkerColor()
						// Color.BLACK
						);
			else g2.setColor(colour.getColor());
			for(int p=1; p<t.size(); p++)
				rpp.drawLine(t.get(p-1), t.get(p), g2);
		}
		
		// set transparency to whatever it was previously
		g2.setComposite(c);
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

	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		for(LightRay2D ray:rays)
		{
			ArrayList<Vector2D> t = ray.getTrajectory();
			if(darkenExhaustedRays && (ray.getTraceLevel() == 0))
			{
				// assumed to be a closed-loop trajectory
				SVGWriter.writeSVGPolyLine(t, rpp, colour.getSVGNameOfDarkerColour(), 1, "");
			}
			else
			{
				// definitely not a closed-loop trajectory
				SVGWriter.writeSVGPolyLine(t, rpp, colour.getSVGName(), 1, "");
			}
		}

	}

	public void writeParameters(PrintStream printStream)
	{
		printStream.println("\nRay source \""+name+"\"\n");

		printStream.println("  rayStartPoint = "+getRayStartPoint());
		printStream.println("  rayAngle = "+ MyMath.rad2deg(getRayAngle())+" degrees");
		printStream.println("  rayBundle = "+isRayBundle());
		printStream.println("  rayBundleIsotropic = "+isRayBundleIsotropic());
		printStream.println("  rayBundleAngle = "+MyMath.rad2deg(getRayBundleAngle())+" degrees");
		printStream.println("  rayBundleNoOfRays = "+getRayBundleNoOfRays());
		printStream.println("  forwardRaysOnly = "+isForwardRaysOnly());
	}

	public void writeToCSV(PrintWriter writer)
	{
		writer.write("Ray source, ");
		writer.write("Start point="+getRayStartPoint()+",");
		writer.println("Angle="+ getRayAngle());
		writer.println("Bundle="+isRayBundle());
		writer.println("Isotropic="+isRayBundleIsotropic());
		writer.println("Bundle angle="+getRayBundleAngle());
		writer.println("No of rays="+getRayBundleNoOfRays());
		writer.println("Forward rays only="+isForwardRaysOnly());
	}

	
	//
	// InteractiveOpticalComponent2D methods
	//


	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents() {
		return OpticalComponent2D.NO_COMPONENTS;
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
	public InteractiveOpticalComponent2D readFromCSV(String filename) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private RayPlay2DPanel panelAssociatedWithPopup;
	
	@Override
	public boolean mousePressed(RayPlay2DPanel rpp, boolean mouseNear, MouseEvent e)
	{
		if(mouseNear && e.isPopupTrigger())
		{
			panelAssociatedWithPopup = rpp;
			
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
		forwardRaysOnlyMenuItem,
		rayBundleMenuItem,
		rayBundleIsotropicMenuItem,
		darkenExhaustedRaysMenuItem,
		releaseSourcePositionFromLineMenuItem;
	
	private void initPopup()
	{
		forwardRaysOnlyMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		forwardRaysOnlyMenuItem.getAccessibleContext().setAccessibleDescription("Toggle forward rays only");
		forwardRaysOnlyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setForwardRaysOnly(!isForwardRaysOnly());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(forwardRaysOnlyMenuItem);

		rayBundleMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		rayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Toggle ray bundle");
		rayBundleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRayBundle(!isRayBundle());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(rayBundleMenuItem);

		rayBundleIsotropicMenuItem = new JMenuItem("-");
		// menuItem.setMnemonic(KeyEvent.VK_P);
		rayBundleIsotropicMenuItem.setEnabled(true);
		rayBundleIsotropicMenuItem.getAccessibleContext().setAccessibleDescription("Toggle isotropic ray bundle");
		rayBundleIsotropicMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setRayBundleIsotropic(!isRayBundleIsotropic());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(rayBundleIsotropicMenuItem);
		
		// Separator
	    popup.addSeparator();
		
//		JMenuItem doubleNoOfRaysMenuItem = new JMenuItem("Double no of rays in bundle");
//		doubleNoOfRaysMenuItem.setEnabled(true);
//		doubleNoOfRaysMenuItem.getAccessibleContext().setAccessibleDescription("Double no of rays in bundle");
//		doubleNoOfRaysMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				rs.setRayBundleNoOfRays(2*rs.getRayBundleNoOfRays());
//				panelWithPopup.repaint();
//			}
//		});
//		popup.add(doubleNoOfRaysMenuItem);
//		
//		JMenuItem halfNoOfRaysMenuItem = new JMenuItem("Halve no of rays in bundle");
//		halfNoOfRaysMenuItem.setEnabled(true);
//		halfNoOfRaysMenuItem.getAccessibleContext().setAccessibleDescription("Halve no of rays in bundle");
//		halfNoOfRaysMenuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				if(rs.getRayBundleNoOfRays() > 2)
//				{
//					rs.setRayBundleNoOfRays(rs.getRayBundleNoOfRays()/2);
//					panelWithPopup.repaint();
//				}
//			}
//		});
//		popup.add(halfNoOfRaysMenuItem);
		
//		int rayNumbers[] = {1, 2, 4, 8, 16, 32, 64, 128, 1024};
//		for(int rayNumber:rayNumbers)
//		{
//			JMenuItem rayNumberMenuItem = new JMenuItem(rayNumber + ((rayNumber == 1)?" ray":" rays"));
//			rayNumberMenuItem.setEnabled(true);
//			rayNumberMenuItem.getAccessibleContext().setAccessibleDescription("Set number of rays to "+rayNumber);
//			rayNumberMenuItem.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					rs.setRayBundleNoOfRays(rayNumber);
//					rs.setRayBundle(rayNumber > 1);
//					panelAssociatedWithPopup.repaint();
//				}
//			});
//			popup.add(rayNumberMenuItem);
//
//		}
//
//		// Separator
//	    popup.addSeparator();

	    for(Colour c:Colour.RAY_COLOURS)
	    {
	    	JMenuItem colourMenuItem = new JMenuItem(c.getName());
	    	colourMenuItem.setEnabled(true);
	    	colourMenuItem.getAccessibleContext().setAccessibleDescription("Set colour to "+c.getName());
	    	colourMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setColour(c);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(colourMenuItem);

	    }
	    
		// Separator
	    popup.addSeparator();

	    darkenExhaustedRaysMenuItem = new JMenuItem("-");	// "Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
	    darkenExhaustedRaysMenuItem.setEnabled(true);
	    darkenExhaustedRaysMenuItem.getAccessibleContext().setAccessibleDescription("Toggle darken exhausted rays");
	    darkenExhaustedRaysMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDarkenExhaustedRays(!isDarkenExhaustedRays());
				// darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(rs.isDarkenExhaustedRays()?"off":"on"));
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(darkenExhaustedRaysMenuItem);

		// Separator
	    popup.addSeparator();
	    
		int maxTraceLevels[] = {1, 2, 3, 4, 16, 64, 256, 1024};
		for(int maxTraceLevel:maxTraceLevels)
		{
			JMenuItem maxTraceLevelMenuItem = new JMenuItem("max. trace level = "+maxTraceLevel);
			maxTraceLevelMenuItem.setEnabled(true);
			maxTraceLevelMenuItem.getAccessibleContext().setAccessibleDescription("Set max. trace level to "+maxTraceLevel);
			maxTraceLevelMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setMaxTraceLevel(maxTraceLevel);
					panelAssociatedWithPopup.repaint();
				}
			});
			popup.add(maxTraceLevelMenuItem);
		}
	    
		// Separator
	    popup.addSeparator();
	    
	    JMenuItem deleteRayBundleMenuItem = new JMenuItem("Delete light source");
		deleteRayBundleMenuItem.setEnabled(true);
		deleteRayBundleMenuItem.getAccessibleContext().setAccessibleDescription("Delete light source");
		deleteRayBundleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelAssociatedWithPopup.iocs.remove(PointRaySource2D.this);
				// panelAssociatedWithPopup.graphicElements.removeAll(rs.getGraphicElements());
				panelAssociatedWithPopup.repaint();
			}
		});
		popup.add(deleteRayBundleMenuItem);

		// Separator
	    popup.addSeparator();
	    
	    releaseSourcePositionFromLineMenuItem = new JMenuItem("Release source position from line");
	    releaseSourcePositionFromLineMenuItem.setEnabled(getLineConstrainingStartPoint() != null);
	    releaseSourcePositionFromLineMenuItem.getAccessibleContext().setAccessibleDescription("Delete light source");
	    releaseSourcePositionFromLineMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLineConstrainingStartPoint(null);
			}
		});
		popup.add(releaseSourcePositionFromLineMenuItem);
}
	
	private void updatePopup()
	{
		// enable/disable + text
		
		forwardRaysOnlyMenuItem.setText(isForwardRaysOnly()?"Make ray(s) bidirectional":"Make ray(s) unidirectional");
		forwardRaysOnlyMenuItem.setEnabled(!isRayBundleIsotropic());
		
		rayBundleMenuItem.setText(isRayBundle()?"Change to single ray":"Change to ray bundle");
		rayBundleMenuItem.setEnabled(!isRayBundleIsotropic());
		
		rayBundleIsotropicMenuItem.setText(isRayBundleIsotropic()?"Make ray bundle directional":"Make ray bundle isotropic");
		rayBundleIsotropicMenuItem.setEnabled(isRayBundle());
		
		darkenExhaustedRaysMenuItem.setText("Switch darkening of exhausted rays "+(isDarkenExhaustedRays()?"off":"on"));
		
	    releaseSourcePositionFromLineMenuItem.setEnabled(getLineConstrainingStartPoint() != null);
	}		
}