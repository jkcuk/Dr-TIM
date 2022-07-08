package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.geometry2D.Geometry2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OmnidirectionalLens2DCellType;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D.OmnidirectionalLensLensType;
import optics.rayplay.util.Colour;
import optics.rayplay.util.SVGWriter;

/**
 * The image of a light ray in one cell of an omnidirectional lens.
 * @author johannes
 */
public class ImageOfRayInOmnidirectionalLensCellGE2D extends LineGE2D
{
	public final static Stroke IORIOLC_STROKE = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);	// dashed
			// new BasicStroke(1);
	public final static Colour IORIOLC_COLOUR = Colour.GRAY;
	public final static int IORIOLC_SVG_LINE_THICKNESS = 1;
	public final static String IORIOLC_SVG_STYLE = "stroke-dasharray=\"1,1\" opacity=\"0.7\"";
	
	protected OmnidirectionalLens2D ol;
	protected OmnidirectionalLens2DCellType cellType;
	protected boolean interactive;
	
	public ImageOfRayInOmnidirectionalLensCellGE2D(
			String name,
			Vector2D a,
			Vector2D b,
			OmnidirectionalLens2D ol,
			OmnidirectionalLens2DCellType cellType,
			Stroke stroke,
			Colour colour,
			int svgLineThickness,
			String svgStyle
		)
	{
		super(
				name,
				a,	// start point
				b,	// 2nd point on ray
				stroke,
				colour,
				svgLineThickness,
				svgStyle,
				ol.getRayPlay2DPanel()	// rayPlay2DPanel
			);

		this.interactive = true;
		this.ol = ol;
		this.cellType = cellType;
		
		// initPopup();
	}

	public ImageOfRayInOmnidirectionalLensCellGE2D(
			String name,
			Vector2D a,
			Vector2D b,
			OmnidirectionalLens2D ol,
			OmnidirectionalLens2DCellType cellType,
			Colour colour
		)
	{
		this(
				name,
				a,	// start point
				b,	// 2nd point on ray
				ol,
				cellType,
				IORIOLC_STROKE,	// stroke
				colour,	// colour
				IORIOLC_SVG_LINE_THICKNESS,	// svgLineThickness
				IORIOLC_SVG_STYLE	// svgStyle
			);
	}

	public ImageOfRayInOmnidirectionalLensCellGE2D(
			String name,
			Vector2D a,
			Vector2D b,
			OmnidirectionalLens2D ol,
			OmnidirectionalLens2DCellType cellType
		)
	{
		this(
				name,
				a,	// start point
				b,	// 2nd point on ray
				ol,
				cellType,
				IORIOLC_STROKE,	// stroke
				IORIOLC_COLOUR,	// colour
				IORIOLC_SVG_LINE_THICKNESS,	// svgLineThickness
				IORIOLC_SVG_STYLE	// svgStyle
			);
	}

	
	
	// getters & setters
	
	public OmnidirectionalLens2D getOl() {
		return ol;
	}

	public void setOl(OmnidirectionalLens2D ol) {
		this.ol = ol;
	}

	public OmnidirectionalLens2DCellType getCellType() {
		return cellType;
	}

	public void setCellType(OmnidirectionalLens2DCellType cellType) {
		this.cellType = cellType;
	}

	@Override
	public boolean isInteractive() {
		return interactive;
	}

	public void setInteractive(boolean interactive) {
		this.interactive = interactive;
	}


	
	
	// GraphicElement2D methods
	
	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{
		g.setStroke(stroke);
		g.setColor(colour.getColor());
		Vector2D[] endPoint = getEndPoints();
		switch(cellType)
		{
		case C0:
			// the shape of this cell is the inverse of a triangle
			if(endPoint != null)
			{
				// the ray intersects the triangle; don't draw the bit that intersects
				// go through the end points
				for(int i=0; i<=1; i++)
				{
					// ... and draw a ray (as in semi-infinite line) that starts at the end point, going outwards
					(new RayGE2D(
						endPoint[i],	// start point
						Vector2D.difference(endPoint[i], endPoint[(i+1)%2]),	// direction
						"",	// name
						stroke,
						colour, 
						svgLineThickness, 
						svgStyle, 
						rayPlay2DPanel
					)).draw(p, g, mouseNear, mouseI, mouseJ);;
				}
			}
			else
			{
				// the ray doesn't intersect the triangle; draw all of it
				super.draw(p,  g, mouseNear, mouseI, mouseJ);
			}
			break;
		default:
			// all other cells are triangles
			if(endPoint != null)
			{
				// the ray intersects the triangle; draw the bit that intersects
				p.drawLine(endPoint[0], endPoint[1], g);
			}
		}
	}

	@Override
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		Vector2D[] endPoint = getEndPoints();
		switch(cellType)
		{
		case C0:
			// the shape of this cell is the inverse of a triangle
			if(endPoint != null)
			{
				// the ray intersects the triangle; don't draw the bit that intersects
				// go through the end points
				for(int i=0; i<=1; i++)
				{
					// ... and draw a ray (as in semi-infinite line) that starts at the end point, going outwards
					(new RayGE2D(
						endPoint[i],	// start point
						Vector2D.difference(endPoint[i], endPoint[(i+1)%2]),	// direction
						"",	// name
						stroke,
						colour, 
						svgLineThickness, 
						svgStyle, 
						rayPlay2DPanel
					)).writeSVGCode(rpp);
				}
			}
			else
			{
				// the ray doesn't intersect the triangle; draw all of it
				super.writeSVGCode(rpp);
			}
			break;
		default:
			// all other cells are triangles
			if(endPoint != null)
			{
				// the ray intersects the triangle; draw the bit that intersects
				SVGWriter.writeSVGLine(endPoint[0], endPoint[1], rpp, colour.getSVGName(), svgLineThickness, svgStyle);
			}
		}
	}
	

//	@Override
//	public boolean isMouseNear(RayPlay2DPanel p, int i, int j)
//	{
//		return false;
//	}
	
//	@Override
//	public void drawInFront(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
//	{
//		super.drawInFront(p, g, mouseNear, mouseI, mouseJ);
//	}
//
//	@Override
//	public void drawBehind(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
//	{
//		super.drawBehind(p, g, mouseNear, mouseI, mouseJ);
//	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		// super.drawAdditionalInfoWhenMouseNear(p, g, mouseI, mouseJ);
		g.setColor(Color.GRAY);
		g.drawString(
				getName(), 
				mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
			);
	}
	
	public Vector2D[] getEndPoints()
	{
		ArrayList<Vector2D> endPoints = new ArrayList<Vector2D>();
		
		for(OmnidirectionalLensLensType lt:cellType.lensTypes)
		{
			Vector2D e = Geometry2D.lineSegmentLineIntersection2D(ol.getLens(lt), this);
			if(e != null) endPoints.add(e);
		}
		
		// there are exactly two intersections, so the ray passes through the cell;
		// return the "real" part of the ray, i.e. the part that passes through the cell
		if(endPoints.size() == 2)
		{
			Vector2D endPointsArray[] = new Vector2D[2];
			endPointsArray[0] = endPoints.get(0);
			endPointsArray[1] = endPoints.get(1);
			return endPointsArray;
		}
		
		// there aren't two intersections
		return null;
	}

}
