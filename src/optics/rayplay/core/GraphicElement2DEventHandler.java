package optics.rayplay.core;

import java.awt.event.MouseEvent;

public interface GraphicElement2DEventHandler {

	public void mouseDragged(GraphicElement2D g, boolean mouseNear, int mouseI, int mouseJ);
	
	public void mouseClicked(GraphicElement2D g, boolean mouseNear, MouseEvent e);

	public boolean mousePressed(CoordinateConverterXY2IJ c, boolean mouseNear, MouseEvent e);
}
