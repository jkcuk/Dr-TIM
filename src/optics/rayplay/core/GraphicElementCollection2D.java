package optics.rayplay.core;

import java.util.ArrayList;

/**
 * A collection of graphic elements.
 * @author johannes
 */
public class GraphicElementCollection2D
{
	protected String name;
	
	protected ArrayList<GraphicElement2D> graphicElements;
	
	public GraphicElementCollection2D(String name)
	{
		super();
		this.name = name;
		graphicElements = new ArrayList<GraphicElement2D>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<GraphicElement2D> getGraphicElements() {
		return graphicElements;
	}

	public void setGraphicElements(ArrayList<GraphicElement2D> graphicElements) {
		this.graphicElements = graphicElements;
	}
	
	

//	/**
//	 * Draw the component.
//	 * It might look different if the mouse is near it.
//	 * By default, just draws, ignoring the mouse.  Override to change this.
//	 * @param p
//	 * @param g
//	 * @param mouseNear
//	 * @param mouseI
//	 * @param mouseJ
//	 */
//	@Override
//	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
//	{
//		for(GraphicElement2D graphicElement:graphicElements)
//		{
//			graphicElement.draw(p, g, mouseNear, mouseI, mouseJ);
//		}
//	}
//
//	@Override
//	public void drawOnTop(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
//	{
//		for(GraphicElement2D graphicElement:graphicElements)
//		{
//			graphicElement.drawOnTop(p, g, mouseNear, mouseI, mouseJ);
//		}
//	}
//
//	/**
//	 * Draw this last, on top
//	 * @param p
//	 * @param g
//	 * @param mouseI
//	 * @param mouseJ
//	 */
//	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
//	{
//		for(GraphicElement2D graphicElement:graphicElements)
//		{
//			graphicElement.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
//		}
//	}
//
//
//	
//	// interactivity
//	
//	public boolean isInteractive();
//	
//	public boolean isMouseNear(CoordinateConverterXY2IJ cc, int i, int j);
//		
//	public void mouseDragged(CoordinateConverterXY2IJ c, boolean mouseNear, int mouseI, int mouseJ);
//	
//	public void mouseClicked(CoordinateConverterXY2IJ c, boolean mouseNear, MouseEvent e);

}
