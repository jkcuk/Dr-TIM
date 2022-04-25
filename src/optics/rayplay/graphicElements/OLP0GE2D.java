package optics.rayplay.graphicElements;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.opticalComponents.OmnidirectionalLens2D;
import optics.rayplay.opticalComponents.OmnidirectionalLens2D.OLPointType;

public class OLP0GE2D extends OLPointGE2D {

	public OLP0GE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive,
			OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, position, radius, stroke, color, interactive, ol, pt);
		
		initPopup();
	}

	public OLP0GE2D(String name, Vector2D position, OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, position, ol, pt);
		
		initPopup();
	}

	public OLP0GE2D(String name, OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, ol, pt);
		
		initPopup();
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
		deleteOLMenuItem;

	private void initPopup()
	{
		deleteOLMenuItem = new JMenuItem("Delete omnidirectional lens");
		deleteOLMenuItem.setEnabled(true);
		deleteOLMenuItem.getAccessibleContext().setAccessibleDescription("Delete omnidirectional lens");
		deleteOLMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.ols.remove(ol);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteOLMenuItem);
	}
	
	private void updatePopup()
	{
		// enable/disable + text
	}	

}
