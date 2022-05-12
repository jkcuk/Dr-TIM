package optics.rayplay.graphicElements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import math.Vector2D;
import optics.rayplay.core.RayPlay2DPanel;
import optics.rayplay.interactiveOpticalComponents.OmnidirectionalLens2D;
import optics.rayplay.util.Colour;

/**
 * A line in an omnidirectional lens.
 * @author johannes
 */
public class OmnidirectionalLensLineGE2D extends LineSegmentGE2D
{
	protected String name;
	protected OmnidirectionalLens2D ol;
	
	public enum OmnidirectionalLensLineType
	{
		C1("C_2a line");

		public final String name;

		OmnidirectionalLensLineType(String name) {this.name = name;}
	}

	protected OmnidirectionalLensLineType lt;
	
	protected boolean interactive;
	
	
	public OmnidirectionalLensLineGE2D(String name, Vector2D a, Vector2D b, Stroke stroke, Colour colour, int svgLineThickness, String svgStyle, boolean interactive, OmnidirectionalLens2D ol, OmnidirectionalLensLineType lt)
	{
		super(a, b, stroke, colour, svgLineThickness, svgStyle);
		
		this.name = name;
		this.ol = ol;
		this.lt = lt;
		this.interactive = interactive;
	}

	public OmnidirectionalLensLineGE2D(String name, Vector2D a, Vector2D b, OmnidirectionalLens2D ol, OmnidirectionalLensLineType lt)
	{
		this(name, a, b,
				new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0),	// dashed
				Colour.GRAY, 1, "", true, ol, lt
			);
	}

	public OmnidirectionalLensLineGE2D(String name, OmnidirectionalLens2D ol, OmnidirectionalLensLineType lt)
	{
		this(name, new Vector2D(0, 0), new Vector2D(0, 0), ol, lt);
	}

	
	// getters & setters
	
	public OmnidirectionalLens2D getOl() {
		return ol;
	}

	public void setOl(OmnidirectionalLens2D ol) {
		this.ol = ol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OmnidirectionalLensLineType getLt() {
		return lt;
	}

	public void setLt(OmnidirectionalLensLineType lt) {
		this.lt = lt;
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
	public boolean isMouseNear(RayPlay2DPanel p, int i, int j)
	{
		switch(lt)
		{
		case C1:
			if(ol.isShowC1PointAndLine())
				return super.isMouseNear(p, i, j);
			else
				return false;
		default:
			return super.isMouseNear(p, i, j);
		}
	}
	
	@Override
	public void draw(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{	
		switch(lt)
		{
		case C1:
			if(ol.isShowC1PointAndLine())
				super.draw(p, g, mouseNear, mouseI, mouseJ);
			break;
		}
	}

	@Override
	public void drawBehind(RayPlay2DPanel p, Graphics2D g, boolean mouseNear, int mouseI, int mouseJ)
	{	
		switch(lt)
		{
		case C1:
			if(ol.isShowC1PointAndLine())
				super.drawBehind(p, g, mouseNear, mouseI, mouseJ);
			break;
		}
	}

	@Override
	public void drawAdditionalInfoWhenMouseNear(RayPlay2DPanel p, Graphics2D g, int mouseI, int mouseJ)
	{
		switch(lt)
		{
		case C1:
			if(ol.isShowC1PointAndLine())
			{
				g.setColor(Color.GRAY);
				g.drawString(
						getName(),
						mouseI+10, mouseJ+5	// x2i(p.x)+10, y2j(p.y)+5
					);
			}
			break;
		}
	}
	
	@Override
	public void writeSVGCode(RayPlay2DPanel rpp)
	{
		switch(lt)
		{
		case C1:
			if(ol.isShowC1PointAndLine())
				super.writeSVGCode(rpp);
			break;
		}
	}


	
	@Override
	public boolean mouseDragged(RayPlay2DPanel rpp, boolean mouseNear, int mouseI, int mouseJ)
	{
		return false;
	}

}
