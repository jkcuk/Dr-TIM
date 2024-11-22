package optics.rayplay.util;

import java.awt.Dimension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import math.Vector2D;
import optics.rayplay.core.CoordinateConverterXY2IJ;

public class SVGWriter
{
	public static PrintStream printStream;

	/**
	 * Create an SVG file and write the header (which specifies the size of the drawing area)
	 * @param filename
	 * @param size
	 * @throws FileNotFoundException 
	 */
	public static void startSVGFile(String filename, Dimension size) throws FileNotFoundException
	{
		FileOutputStream fileOutputStream = new FileOutputStream(filename+".svg");
		printStream = new PrintStream(fileOutputStream);

		// write the header
		printStream.println(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
						"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
						"<svg width=\""+size.width+"\" height=\""+size.height+"\" viewBox=\"0 0 "+size.width+" "+size.height+"\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
						// "<rect fill=\"#fff\" stroke=\"#fff\" x=\"0\" y=\"0\" width=\""+size.width+"\" height=\""+size.height+"\"/>\n" +
						"<g opacity=\"1\">"
				);
		printStream.flush();
	}
	
	public static void endSVGFile()
	{
		// write the footer
		printStream.println(
				"</g>\n" +
				"</svg>"
				);

		printStream.flush();
		printStream.close();
	}
	
	/**
	 * draw a line between points 1 and 2 in the scaled coordinate system
	 * @param point1
	 * @param point2
	 * @param color	e.g. "blue"
	 * @param width
	 * @param style	e.g. "opacity=\"0.8\""
	 * @param p	the PrintStream this gets printed to
	 */
	public static void writeSVGLine(Vector2D point1, Vector2D point2, CoordinateConverterXY2IJ cc, String color, int width, String style)
	{
		String s = "<line "+
				"x1=\""+cc.x2id(point1.x)+"\" "+
				"y1=\""+cc.y2jd(point1.y)+"\" "+
				"x2=\""+cc.x2id(point2.x)+"\" "+
				"y2=\""+cc.y2jd(point2.y)+"\" "+
				"stroke=\""+color+"\" "+
				"stroke-width=\""+width+"\" "+
				style+
				"/>";
		printStream.println(s);
		// printStream.flush();
		// System.out.println(s);
	}

	public static void writeSVGPolyLine(ArrayList<Vector2D> points, CoordinateConverterXY2IJ cc, String color, int width, String style)
	{
		String s = "<polyline points=\"";
		for(int i=0; i<points.size(); i++)
			s += cc.x2id(points.get(i).x)+","+cc.y2jd(points.get(i).y)+" ";
		s += "\" stroke=\""+color+"\" "+
				"stroke-width=\""+width+"\" "+
				"fill=\"none\" "+
				style+
				"/>";
		printStream.println(s);
		// printStream.flush();
		// System.out.println(s);
	}

}
