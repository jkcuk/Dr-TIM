package optics.rayplay.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import optics.rayplay.core.InteractiveOpticalComponent2D;
import optics.rayplay.core.RaySource2D;
import optics.rayplay.interactiveOpticalComponents.Lens2DIOC;

public class CSVUtilities {
	
	public enum Component2DType
	{
		LENS("Lens"),
		LENS_STAR("Lens star"),
		OMNIDIRECTIONAL_LENS("Omnidirectional lens"),
		RAY_SOURCE("Ray source");
		
		public final String name;
		
		Component2DType(String name) {this.name = name;}
	}
	
	/**
	 * @param name
	 * @return	the Component2DType with the given name, or null if there is none
	 */
	public static Component2DType getComponentType(String name)
	{
		for(Component2DType ct:Component2DType.values())
		{
			if(ct.name.toLowerCase().equals(name.toLowerCase())) return ct;
		}
		
		// no Component2DType with the given name was found
		return null;
	}


	public static String toString(String[] string)
	{
		if(string == null) return "";
		if(string.length < 1) return "";
		
		String s = "\""+string[0]+"\"";
		for(int i=1; i<string.length; i++)
		{
			s = s + ", \""+string[i]+"\"";
		}
		
		return s;
	}
	
	/**
	 * Read a CSV file
	 * @param filename
	 * @return	the contents of the CSV file
	 * @throws IOException 
	 */
	public static ArrayList<String[]> readCSV(String filename) throws IOException
	{
		// see https://stackoverflow.com/questions/40413530/csv-to-table-in-java

		FileInputStream fstream = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		ArrayList<String[]> rows = new ArrayList<String[]>();
		String strLine;
		while ((strLine = br.readLine()) != null) {
			String[] dataLine = strLine.split(",");
			for(int s=0; s<dataLine.length; s++) dataLine[s] = dataLine[s].trim();// get rid of spaces before or after
			rows.add(dataLine);
		}

		fstream.close();

		return rows;
	}


	/**
	 * Read the file containing the information about the Interactive Optical Components, and add any components
	 * identified to the ArrayList iocs or lss, as appropriate.
	 * Each line contains information about one component, in comma-separated format of the form
	 *   <component type>, <component name>, <parameter 1>=<parameter 1 value>, ...
	 * @param filename
	 * @param iocs
	 * @throws Exception 
	 */
	public static void readComponentsFromCSV(String filename, ArrayList<InteractiveOpticalComponent2D> iocs, ArrayList<RaySource2D> rss) throws Exception
	{
		ArrayList<String[]> lines = readCSV(filename);

		// the first line should be the header
		String[] headerFormat = {"<Component type>", "<Component name>", "<Name of parameter 1>=<Value of parameter 1>", "<Name of parameter 2>=<Value of parameter 2>", "..."};
		String[] header = lines.get(0);
		for(int i=0; i<headerFormat.length; i++)
			if(!header[i].toLowerCase().equals(headerFormat[i].toLowerCase()))
			{
				throw new IOException(
						"Values in first line, "+
								toString(header)+", do not match the header format, "+
								toString(headerFormat)+"."
						);
			}

		// the header looks okay -- read the IOCs
		for(int l=1; l<lines.size(); l++)
		{
			String[] field = lines.get(l);
			
			switch(getComponentType(field[0]))
			{
			case LENS:
				Lens2DIOC lens = new Lens2DIOC(field);
				iocs.add(lens);
				break;
			case LENS_STAR:
				// TODO LensStar2D ls = new LensStar2D(field);
				// TODO iocs.add(ls);
				break;
			case OMNIDIRECTIONAL_LENS:
				// TODO OmnidirectionalLens2D ol = new OmnidirectionalLens2D(field);
				// TODO iocs.add(ol);
				break;
			case RAY_SOURCE:
				// TODO RaySource2D rs = new RaySource2D(field);
				// TODO rss.add(rs);
			}
		}
	}
	
	public void writeToCSV(String filename, ArrayList<InteractiveOpticalComponent2D> iocs, ArrayList<RaySource2D> rss) throws IOException
	{
		PrintWriter writer = new PrintWriter(filename);

		writer.write("<Component type>, <Component name>, <Name of parameter 1>=<Value of parameter 1>, <Name of parameter 2>=<Value of parameter 2>, ...\n");

		for(InteractiveOpticalComponent2D ioc:iocs)
		{
			ioc.writeToCSV(writer);
		}
		
		for(RaySource2D rs:rss)
		{
			rs.writeToCSV(writer);
		}
		
		writer.close();
	}

}
