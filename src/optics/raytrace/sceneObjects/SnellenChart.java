package optics.raytrace.sceneObjects;

import java.io.*;
import java.util.StringJoiner;

import math.*;
import optics.raytrace.GUI.sceneObjects.EditableText;
import optics.raytrace.core.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A Snellen chart that scales the side reading (vision readings) according to the camera object distance.
 * This means the object can be any size and distance and the readings on the side should remain true in meaning
 * Alternatively, if the constructor is called where no camera position is given there will be no side readings i.e just an image of some letters.  
 * 
 */
public class SnellenChart
extends SceneObjectContainer
implements Serializable
{


	private static final long serialVersionUID = -7575909351821557981L;

	private Vector3D centre;
	private Vector3D upDirection;
	private Vector3D rightDirection;
	private Vector3D cameraPosition; //Any way to simply pull this and instead have a switch?

	/**
	 * Different types of charts that can be used.
	 */
	public enum ChartType
	{
		RANDOM("Random Letters"),
		SET("Set Letters"),
		E("All E");

		private String description;
		private ChartType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	private ChartType chartType;


	/**
	 * height of the chart. The relation ship to the chart width is given by height = sqrt(2)*width
	 */
	double height;


	/**
	 * Snellen chart with side scale to read out vision results for given camera object distance and height. 
	 * @param description
	 * @param centre
	 * @param upDirection
	 * @param rightDirection
	 * @param height
	 * @param cameraPosition
	 * @param parent
	 * @param studio
	 */
	public SnellenChart(
			String description,
			Vector3D centre,
			Vector3D upDirection,
			Vector3D rightDirection,
			double height,
			Vector3D cameraPosition,
			ChartType chartType,
			SceneObject parent,
			Studio studio
			)
	{
		super(description, parent, studio);

		setCentre(centre);
		setUpDirection(upDirection);
		setRightDirection(rightDirection);
		setHeight(height);
		setCameraPosition(cameraPosition);
		setChartType(chartType);

		addSnellenChart();
		addSnellenChartReadings();
	}


	/**
	 * Snellen chart without any readings, essentially just some random letters if this is ever needed.
	 * @param description
	 * @param centre
	 * @param upDirection
	 * @param rightDirection
	 * @param height
	 * @param parent
	 * @param studio
	 */
	public SnellenChart(
			String description,
			Vector3D centre,
			Vector3D upDirection,
			Vector3D rightDirection,
			double height,
			ChartType chartType,
			SceneObject parent,
			Studio studio
			)
	{
		super(description, parent, studio);

		setCentre(centre);
		setUpDirection(upDirection);
		setRightDirection(rightDirection);
		setHeight(height);
		setChartType(chartType);

		addSnellenChart();
	}

	/**
	 * Create a clone of the original.
	 * @param original
	 */
	public SnellenChart(SnellenChart original)
	{
		this(
				original.getDescription(),
				original.getCentre(),
				original.getUpDirection(),
				original.getRightDirection(),
				original.getHeight(),
				original.getCameraPosition(),
				original.getChartType(),
				original.getParent(),
				original.getStudio()
				);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection#clone()
	 */
	@Override
	public SnellenChart clone()
	{
		return new SnellenChart(this);
	}


	//
	// setters & getters
	//

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getUpDirection() {
		return upDirection;
	}

	public void setUpDirection(Vector3D upDirection) {
		this.upDirection = upDirection;
	}

	public Vector3D getRightDirection() {
		return rightDirection;
	}

	public void setRightDirection(Vector3D rightDirection) {
		this.rightDirection = rightDirection;
	}

	public Vector3D getCameraPosition() {
		return cameraPosition;
	}

	public void setCameraPosition(Vector3D cameraPosition) {
		this.cameraPosition = cameraPosition;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public ChartType getChartType() {
		return chartType;
	}


	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}


	//calculates the text height for a given row, as this is an optical chart the width and height of the text should be equal
	public double calculateTextHeight(double height, int row) {
		//this is such that at 6m, on an A3 paper, the top letter represents 6/60 vision and is 87.3 mm tall
		double topLetterHeight = height / (420/87.3);

		//now each row will scale the size down by a scale factor, given by:
		//from top row downwards: 10,6,4,2,1.5,1,5/6,2/3,0.5,1/3,1/6 note not all these need to be used.
		double scaleFactor=0;
		if(row>= (int)12 || row <=0) {
			System.err.println("the row: "+row+" is not within the allowed row range");
		}
		if(row == (int)1) scaleFactor = 10;
		if(row == (int)2) scaleFactor = 6;
		if(row == (int)3) scaleFactor = 4;
		if(row == (int)4) scaleFactor = 2;
		if(row == (int)5) scaleFactor = 1.5;
		if(5 <row && row < 12) {
			scaleFactor = (12-(double)row)/6;
		}		
		double rowLetterSize = (scaleFactor/10)*topLetterHeight;
		//System.out.println("rowLetterSize"+row+" = "+rowLetterSize+"sf"+scaleFactor);

		return rowLetterSize;
	}

	//returns the chart readings given the text height and distance between the camera and centre
	public String chartReadings( Vector3D centre, double textHeight, Vector3D cameraPosition) {
		//as the text moves further away, the difficulty to read it changes. Hence, the snellen reading must also change.
		//The front number on the snellen chart may remain the same, in metric usages it will be 6.
		//The back number however, changes according to the angular size of the letters given by the closest integer
		double distance = Vector3D.getDistance(cameraPosition, centre);
		int backNumber = (int) Math.round( 72*2*MyMath.rad2deg(Math.tanh(textHeight/(2*distance))));

		return "6 / "+backNumber;
	}

	//generate the random letters... 
	public String getRandomLetter(){
		int r = (int) (Math.random()*12);
		String randomLetter = new String [] {"C", "D", "E", "F", "H", "K", "N", "P", "R", "U", "V", "Z"}[r];
		return randomLetter;
	}

	//finding the text positions
	public Vector3D textCentre(Vector3D centre, double height, int row, Vector3D upDirection) {
		//get the centre/corner? or the text position...
		Vector3D chartTop = Vector3D.sum(centre, upDirection.getWithLength(height/2));

		//see how much the text needs to shift up or down by: 
		double textSpace = 0;
		double lineSpacing = height/30;
		for (int k = 1; k<=row; k++) {
			double halfLetterSize = 0;
			double lastHalfLetterSize = 0;

			halfLetterSize = 0.5 * calculateTextHeight(height, k);

			if(k>(int)1) lastHalfLetterSize = 0.5 * calculateTextHeight(height, k-1);

			textSpace = textSpace + lineSpacing + lastHalfLetterSize + halfLetterSize;
		}

		//System.out.println("row:"+row+" textspace="+j);
		//System.out.println("row:"+row+" textspace="+textSpace);
		Vector3D textCentre = Vector3D.sum(chartTop, upDirection.getWithLength(-textSpace));
		//System.out.println("chart top= "+chartTop+"textCentre= "+textCentre);
		return textCentre;
	}


	//creating the chart readings on the side of the actual chart.
	public void addSnellenChartReadings()
	{
		//find the position of these:


		for (int i = 1; i<10; i++) {

			EditableText snellenChartRow = EditableText.getCentredEditableText(
					"Chart reading in row="+i,// description
					//the actual text trial and error...
					chartReadings(centre, calculateTextHeight(height, i), cameraPosition),	// text
					Vector3D.sum(textCentre(centre, height, i, upDirection), rightDirection.getWithLength((height/2.8)-(height/15))),	// centre 
					rightDirection,	// rightDirection
					upDirection,	// upDirection
					1024,	// fontSize
					"Times",	// fontFamily
					height/25,	// textHeight
					SurfaceColour.BLACK_MATT,	// textSurfaceProperty
					getParent(),
					getStudio()
					);
			addSceneObject(snellenChartRow);

			System.out.println("Wisiual acuity of row: "+i+" is:   "+chartReadings(centre, calculateTextHeight(height, i), cameraPosition));
		}
	}

	//add the actual chart with the given scaling..
	public void addSnellenChart()
	{
		clear();


		for (int i = 1; i<10; i++) {


			//create a random string of letters from getRandomLetter()
			int j = i;
			if(j >= (int)8)j = 8;
			StringJoiner s = new StringJoiner("  ");

			switch(chartType) {
			case RANDOM:
				//produces a new randomised chart each time
				for (int k = 1; k<=j; k++) {
					s.add(getRandomLetter());
				}
				break;

			case SET:
				//produces a set chart according to the conditions below... TODO surely there is a better way to do this? 
				if(i==1)s.add("Z");
				if(i==2)s.add("L  P");
				if(i==3)s.add("E  F  L");
				if(i==4)s.add("V  R  E  H");
				if(i==5)s.add("H  L  D  C  K");
				if(i==6)s.add("L  P  K  F  P  C");
				if(i==7)s.add("E  P  Z  N  H  D  P");
				if(i==8)s.add("Z  C  P  F  H  R  V  R");
				if(i==9)s.add("K  P  V  N  H  F  E  Z");
				break;

			case E:
				//produces a chart full of E
				for (int k = 1; k<=j; k++) {
					s.add("E");
				}
				break;
			}

			addSceneObject(EditableText.getCentredEditableText(
					"Letters in row="+i,	// description
					s.toString(),	// text
					textCentre(centre, height, i, upDirection),	// centre
					// new Vector3D(-2*CM, (-0.16*zInCM+0.6)*CM, zInCM*CM-zShift),	// bottomLeftCorner
					rightDirection,	// rightDirection
					upDirection,	// upDirection
					1024,	// fontSize
					"Optician Sans",	// fontFamily
					calculateTextHeight(height, i),	// textHeight
					SurfaceColour.BLACK_MATT,	// textSurfaceProperty
					getParent(),
					getStudio()
					));
			System.out.println("Correct letter sequence for row: "+i+" is:   "+s.toString()+"  ...");
		}

		//Adding a white background
		addSceneObject(new CentredParallelogram(
				"White background",// description,
				Vector3D.sum(centre, Vector3D.crossProduct(rightDirection, upDirection).getWithLength(MyMath.TINY)), //centre
				upDirection.getProductWith(height),// spanVector1, 
				rightDirection.getProductWith((height/1.4)),// spanVector2,
				SurfaceColour.WHITE_SHINY,// surfaceProperty,
				getParent(),
				getStudio()
				));

	}


	@Override
	public String getType()
	{
		return "Snellen chart";
	}


}
