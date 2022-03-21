package math;

 /**
  * 
  * @author Johannes
  * 
  * A straight line in 3D, given by the equation <pointOnLine> + a*<directionOfLine>
  */

public class Line3D
{	
	/**
	 * point on line
	 */
	private Vector3D pointOnLine;
	
	/**
	 * direction of line, not necessarily normalised
	 */
	private Vector3D directionOfLine;
	
	public Line3D(Vector3D pointOnLine, Vector3D directionOfLine)
	{	
		this.pointOnLine = pointOnLine;
		this.directionOfLine = directionOfLine;
	}

	public Line3D(Line3D original)
	{
		this(original.getPointOnLine(), original.getDirectionOfLine());
	}
	
	@Override
	public Line3D clone()
	{
		return new Line3D(this);
	}
	

	// getters & setters
	
	public Vector3D getPointOnLine() {
		return pointOnLine;
	}

	public void setPointOnLine(Vector3D pointOnLine) {
		this.pointOnLine = pointOnLine;
	}

	public Vector3D getDirectionOfLine() {
		return directionOfLine;
	}

	public void setDirectionOfLine(Vector3D directionOfLine) {
		this.directionOfLine = directionOfLine;
	}

	@Override
	public String toString() {
		return "<Line3D " + pointOnLine + "+a*" + directionOfLine + ">";
	}	
}
