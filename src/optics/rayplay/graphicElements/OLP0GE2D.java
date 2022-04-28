package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OLLensType;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OLPointType;

public class OLP0GE2D extends OLPointGE2D {

	public OLP0GE2D(String name, Vector2D position, int radius, Stroke stroke, Color color, boolean interactive,
			OmnidirectionalLens2D ol, OLPointType pt)
	{
		super(name, position, radius, stroke, color, interactive, ol, pt);
		
		initPopup();
	}

	public OLP0GE2D(String name, Vector2D position, OmnidirectionalLens2D ol, OLPointType pt)
	{
		this(name, position, 5, new BasicStroke(1), Color.gray, true, ol, pt);
	}

	public OLP0GE2D(String name, OmnidirectionalLens2D ol, OLPointType pt)
	{
		this(name, new Vector2D(0, 0), 5, new BasicStroke(1), Color.gray, true, ol, pt);
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
		deleteOLMenuItem,
		turnIntoIndividualLensesMenuItem;

	private void initPopup()
	{
		deleteOLMenuItem = new JMenuItem("Delete omnidirectional lens");
		deleteOLMenuItem.setEnabled(true);
		deleteOLMenuItem.getAccessibleContext().setAccessibleDescription("Delete omnidirectional lens");
		deleteOLMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ol);
				panelWithPopup.repaint();
			}
		});
		popup.add(deleteOLMenuItem);
		
		// Separator
	    popup.addSeparator();

		turnIntoIndividualLensesMenuItem = new JMenuItem("Turn omnidirectional lens into individual lenses");
		turnIntoIndividualLensesMenuItem.setEnabled(true);
		turnIntoIndividualLensesMenuItem.getAccessibleContext().setAccessibleDescription("Turn omnidirectional lens into individual lenses");
		turnIntoIndividualLensesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// remove the omnidirectional lens
				panelWithPopup.graphicElements.removeAll(ol.getGraphicElements());
				// panelWithPopup.opticalComponents.removeAll(ol.getOpticalComponents());
				panelWithPopup.iocs.remove(ol);
				
				// and add all the lenses
				for(OLLensType olLensType:OLLensType.values())
				{
					Lens2DIOC lens = new Lens2DIOC(ol.getLens(olLensType));
					panelWithPopup.iocs.add(lens);
					panelWithPopup.graphicElements.addAll(lens.getGraphicElements());
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
