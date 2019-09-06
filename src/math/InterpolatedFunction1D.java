package math;

import optics.raytrace.exceptions.InconsistencyException;

/**
 * A function y(x), interpolated from <i>xyTable</i> of the form {{x_0, y(x_0)}, {x_1, y(x_1)}, ..., {x_N, y(x_N)}}, and defined in the range <i>x_0</i> to <i>x_N</i>.
 * @author johannes
 */
public class InterpolatedFunction1D
{
	/**
	 * A list of data points of the function y(x), in the form ((x_0, y_0), (x_1, y_1), ...), and ordered such that x_(i+1) > x_i for all i.
	 */
	private double[][] xyTable;

	/**
	 * Constructor; setXYTable must be called later!
	 */
	public InterpolatedFunction1D()
	{
		super();
	}

	/**
	 * Constructor that sets the xyTable
	 * @param xyTable
	 * @throws InconsistencyException
	 */
	public InterpolatedFunction1D(double[][] xyTable) throws InconsistencyException
	{
		super();
		
		setXYTable(xyTable);
	}
	
	
	//
	// setters & getters
	//
	
	public double[][] getXYTable() {
		return xyTable;
	}

	/**
	 * Check that the table of xy data is consistent, and set it
	 * @param xyTable	(x,y) data in the form {{x_0,y_0}, {x_1,y_1}, ...}
	 * @throws InconsistencyException
	 */
	public void setXYTable(double[][] xyTable) throws InconsistencyException
	{
		// check that the xyTable is in a sensible order, i.e. that the x_i values are monotonically rising
		for(int i=0; i<xyTable.length-1; i++)
		{
			if(xyTable[i][0] >= xyTable[i+1][0])
				throw new InconsistencyException("Data are in wrong order, specifically x["+i+"]="+xyTable[i][0]+" >= "+xyTable[i+1][0]+"=x["+i+1+"]");
		}
		
		// if this point is reached, the table passed the above consistency test(s) 
		this.xyTable = xyTable;
	}
	
	public double getX(int i)
	{
		return xyTable[i][0];
	}
	
	public double getY(int i)
	{
		return xyTable[i][1];
	}
	
	
	//
	// the useful stuff
	//
	
	public double calculateY(double x) throws InconsistencyException
	{
		// check that x is in range
		if((x < getX(0)) || (x > getX(xyTable.length-1)))
		{
			throw new InconsistencyException(x + " is not in the range ["+getX(0)+","+getX(xyTable.length-1)+"]");
		}
		
		// find the index i with the property that x[i] <= x < x[i+1]
		int i;
		for(i=0; (i<xyTable.length-2) && (getX(i+1) < x); i++);
		
		// System.out.println("InterpolatedFunction1D::calculateY("+x+"): i="+i+", x[i]="+getX(i)+", x[i+1]="+getX(i+1));
		
		// calculate the corresponding y value
		// use linear interpolation -- see https://en.wikipedia.org/wiki/Linear_interpolation
		return getY(i) + (x-getX(i))*(getY(i+1)-getY(i))/(getX(i+1)-getX(i));
	}

	public static double calculateY(double x, double[][] xyTable) throws InconsistencyException
	{
		// check that x is in range
		if((x < xyTable[0][0]) || (x > xyTable[xyTable.length-1][0]))
		{
			throw new InconsistencyException(x + " is not in the range ["+xyTable[0][0]+","+xyTable[xyTable.length-1][0]+"]");
		}
		
		// find the index i with the property that x[i] <= x < x[i+1]
		int i;
		for(i=0; (i<xyTable.length-2) && (xyTable[i+1][0] < x); i++);
		
		// System.out.println("InterpolatedFunction1D::calculateY("+x+"): i="+i+", x[i]="+xyTable[i][0]+", x[i+1]="+xyTable[i+1][0]);
		
		// calculate the corresponding y value
		// use linear interpolation -- see https://en.wikipedia.org/wiki/Linear_interpolation
		return xyTable[i][1] + (x-xyTable[i][0])*(xyTable[i+1][1]-xyTable[i][1])/(xyTable[i+1][0]-xyTable[i][0]);
	}

}
