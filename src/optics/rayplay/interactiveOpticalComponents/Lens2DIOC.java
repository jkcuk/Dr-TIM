package optics.rayplay.interactiveOpticalComponents;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import math.Vector2D;
import optics.rayplay.core.GraphicElement2D;
import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.OpticalComponent2D;
import optics.rayplay.graphicElements.LensPointGE2D;
import optics.rayplay.graphicElements.LensPointGE2D.LensPointType;
import optics.rayplay.graphicElements.PointGE2D;
import optics.rayplay.opticalComponents.Lens2D;

public class Lens2DIOC extends Lens2D
implements InteractiveOpticalComponent2D
{
	// internal variables
	
	private ArrayList<OpticalComponent2D> opticalComponents = new ArrayList<OpticalComponent2D>();
	private ArrayList<GraphicElement2D> graphicElements = new ArrayList<GraphicElement2D>();
	
	private HashMap<LensPointType, PointGE2D> points;

	
	// constructors
	
	public Lens2DIOC(		
			String name,
			Vector2D principalPoint,
			double focalLength,
			Vector2D endPoint1,
			Vector2D endPoint2
		)
	{
		super(name, principalPoint, focalLength, endPoint1, endPoint2);
		
		// create the points, ...
		createPoints();

		// ... and set their parameters
		calculatePointParameters();

		opticalComponents.add(this);
		graphicElements.add(this);
	}
	
	public Lens2DIOC(Lens2D lens)
	{
		this(lens.getName(), lens.getPrincipalPoint(), lens.getFocalLength(), lens.getA(), lens.getB());
	}
	
	
	// the meat

	private void createPoints()
	{
		// initialise the array of points
		points = new HashMap<LensPointType, PointGE2D>();

		// and add them all to the list graphicElements
		for(LensPointType lensPointType : LensPointType.values())
		{
			LensPointGE2D p = new LensPointGE2D(
						lensPointType.name,	// name
						this,	// lens
						lensPointType
					);
//			switch(lensPointType)
//			{
//			case P:
//				p.setPosition(principalPoint);
//				break;
//			case E1:
//				p.setPosition(a);
//				break;
//			case E2:
//				p.setPosition(b);
//				break;
//			default:
//			}
			
			points.put(lensPointType, p);
			graphicElements.add(p);
		}
	}

	public void calculatePointParameters()
	{		
		// the bottom vertices
		points.get(LensPointType.F).setCoordinatesToThoseOf(getFocalPoint());
		points.get(LensPointType.P).setCoordinatesToThoseOf(getPrincipalPoint());
		points.get(LensPointType.E1).setCoordinatesToThoseOf(a);
		points.get(LensPointType.E2).setCoordinatesToThoseOf(b);
		points.get(LensPointType.L).setCoordinatesToThoseOf(Vector2D.sum(getPrincipalPoint(), getDirection().getWithLength(0.1)));
	}

	
	//
	// InteractiveOpticalComponent2D methods
	//

	@Override
	public ArrayList<OpticalComponent2D> getOpticalComponents()
	{
		return opticalComponents;
	}

	@Override
	public ArrayList<GraphicElement2D> getGraphicElements()
	{
		return graphicElements;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		printStream.println("\nLens \""+ name +"\"\n");

		printStream.println("  principalPoint = "+principalPoint);
		printStream.println("  focalLength = "+focalLength);
		printStream.println("  a (end point 1) = "+a);
		printStream.println("  b (end point 2) = "+b);
	}
}
