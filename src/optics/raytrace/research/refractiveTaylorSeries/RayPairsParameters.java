package optics.raytrace.research.refractiveTaylorSeries;

import java.io.PrintStream;

import math.Geometry;
import math.MyMath;
import math.Vector2D;
import math.Vector3D;

/**
 * Parameters describing a set of corresponding light rays
 */
public class RayPairsParameters {

	public int noOfDirectionPairs;
	public double directionsInConeAngleRad;
	public LawOfRefraction1Parameter lawOfRefraction;

	public Vector3D[] directionsIn;
	public Vector3D[] directionsOut;
	
	public int noOfRaysPerBundle;
	public double rayStartPointsDiscRadius;
	public Vector3D[] rayStartPoints;

	// constructors
	
	public RayPairsParameters(
			int noOfDirectionPairs,
			double directionsInConeAngleRad,
			LawOfRefraction1Parameter lawOfRefraction,
			Vector3D[] directionsIn, 
			Vector3D[] directionsOut,
			int noOfRaysPerBundle,
			double rayStartPointsDiscRadius,
			Vector3D[] rayStartPoints
		) {
		super();
		this.noOfDirectionPairs = noOfDirectionPairs;
		this.directionsInConeAngleRad = directionsInConeAngleRad;
		this.lawOfRefraction = lawOfRefraction;
		this.directionsIn = directionsIn;
		this.directionsOut = directionsOut;
		this.noOfRaysPerBundle = noOfRaysPerBundle;
		this.rayStartPointsDiscRadius = rayStartPointsDiscRadius;
		this.rayStartPoints = rayStartPoints;
	}

	/**
	 * Constructor that sets all the parameters and then creates randomised ray-direction pairs and ray start points accordingly
	 * @param noOfDirectionPairs
	 * @param directionsInConeAngleRad
	 * @param lawOfRefraction
	 * @param noOfRaysPerBundle
	 * @param rayStartPointsDiscRadius
	 */
	public RayPairsParameters(
			int noOfDirectionPairs,
			double directionsInConeAngleRad,
			LawOfRefraction1Parameter lawOfRefraction,
			int noOfRaysPerBundle,
			double rayStartPointsDiscRadius
		) {
		super();
		this.noOfDirectionPairs = noOfDirectionPairs;
		this.directionsInConeAngleRad = directionsInConeAngleRad;
		this.lawOfRefraction = lawOfRefraction;
		this.noOfRaysPerBundle = noOfRaysPerBundle;
		this.rayStartPointsDiscRadius = rayStartPointsDiscRadius;
		
		initialiseRayPairsParametersArrays();
	}

	/**
	 * Create the arrays etc., set the values to standard values
	 * @param noOfDirectionPairs
	 */
	public RayPairsParameters(int noOfDirectionPairs, int noOfRaysPerBundle) {
		super();
		this.noOfDirectionPairs = noOfDirectionPairs;
		this.directionsInConeAngleRad = MyMath.deg2rad(10);
		this.lawOfRefraction = LawOfRefraction1Parameter.TRANSPARENT;
		this.noOfRaysPerBundle = noOfRaysPerBundle;
		this.rayStartPointsDiscRadius = 1;
		
		initialiseRayPairsParametersArrays();
	}



	// setters & getters

	/**
	 * @return the noOfDirectionPairs
	 */
	public int getNoOfDirectionPairs() {
		return noOfDirectionPairs;
	}

	/**
	 * @param noOfDirectionPairs the noOfDirectionPairs to set
	 */
	public void setNoOfDirectionPairs(int noOfDirectionPairs) {
		this.noOfDirectionPairs = noOfDirectionPairs;
	}

	/**
	 * @return the directionsInConeAngleRad
	 */
	public double getDirectionsInConeAngleRad() {
		return directionsInConeAngleRad;
	}

	/**
	 * @param directionsInConeAngleRad the directionsInConeAngleRad to set
	 */
	public void setDirectionsInConeAngleRad(double directionsInConeAngleRad) {
		this.directionsInConeAngleRad = directionsInConeAngleRad;
	}

	/**
	 * @return the lawOfRefraction
	 */
	public LawOfRefraction1Parameter getLawOfRefraction() {
		return lawOfRefraction;
	}

	/**
	 * @param lawOfRefraction the lawOfRefraction to set
	 */
	public void setLawOfRefraction(LawOfRefraction1Parameter lawOfRefraction) {
		this.lawOfRefraction = lawOfRefraction;
	}

	/**
	 * @return the directionsIn
	 */
	public Vector3D[] getDirectionsIn() {
		return directionsIn;
	}

	/**
	 * @param directionsIn the directionsIn to set
	 */
	public void setDirectionsIn(Vector3D[] directionsIn) {
		this.directionsIn = directionsIn;
	}

	/**
	 * @return the directionsOut
	 */
	public Vector3D[] getDirectionsOut() {
		return directionsOut;
	}

	/**
	 * @param directionsOut the directionsOut to set
	 */
	public void setDirectionsOut(Vector3D[] directionsOut) {
		this.directionsOut = directionsOut;
	}

	/**
	 * @return the noOfRaysPerBundle
	 */
	public int getNoOfRaysPerBundle() {
		return noOfRaysPerBundle;
	}

	/**
	 * @param noOfRaysPerBundle the noOfRaysPerBundle to set
	 */
	public void setNoOfRaysPerBundle(int noOfRaysPerBundle) {
		this.noOfRaysPerBundle = noOfRaysPerBundle;
	}

	/**
	 * @return the rayStartPointsDiscRadius
	 */
	public double getRayStartPointsDiscRadius() {
		return rayStartPointsDiscRadius;
	}

	/**
	 * @param rayStartPointsDiscRadius the rayStartPointsDiscRadius to set
	 */
	public void setRayStartPointsDiscRadius(double rayStartPointsDiscRadius) {
		this.rayStartPointsDiscRadius = rayStartPointsDiscRadius;
	}

	/**
	 * @return the rayStartPoints
	 */
	public Vector3D[] getRayStartPoints() {
		return rayStartPoints;
	}

	/**
	 * @param rayStartPoints the rayStartPoints to set
	 */
	public void setRayStartPoints(Vector3D[] rayStartPoints) {
		this.rayStartPoints = rayStartPoints;
	}
	

	public void writeParameters(PrintStream printStream)
	{
		// printStream.println("parameterName = "+parameterName);
		printStream.println("noOfDirectionPairs="+noOfDirectionPairs);
		printStream.println("directionsInConeAngleRad="+directionsInConeAngleRad);
		printStream.println("lawOfRefraction="+lawOfRefraction);

		for(int i =0;i<noOfDirectionPairs;i++) 
		{
			printStream.println("  directionsIn ["+i+"]="+directionsIn [i]);
			printStream.println("  directionsOut["+i+"]="+directionsOut[i]);
		}
		
		printStream.println("noOfRaysPerBundle="+noOfRaysPerBundle);
		printStream.println("rayStartPointsDiscRadius="+rayStartPointsDiscRadius);
		
		for(int  i=0; i<noOfRaysPerBundle; i++)
		{
			printStream.println("  rayStartPoints["+i+"]="+rayStartPoints[i]);
		}
	}


	// useful
	
	/**
	 * randomise the arrays according to the parameters
	 */
	public void initialiseRayPairsParametersArrays()
	{
		directionsIn = new Vector3D[noOfDirectionPairs];
		randomiseDirectionsIn();
		
		directionsOut = new Vector3D[noOfDirectionPairs];
		calculateDirectionsOut();
		
		rayStartPoints = new Vector3D[noOfRaysPerBundle];
		randomiseRayStartPoints();
	}	

//	/**
//	 * run this if the number of surfaces or the polynomial order might have changed
//	 */
//	public void reshapeSurfaceParametersArrays()
//	{
//		Vector3D[] newDirectionsIn = new Vector3D[noOfDirectionPairs];
//		Vector3D[] newDirectionsOut = new Vector3D[noOfDirectionPairs];
//		Vector3D[] newRayStartPoints = new Vector3D[noOfRaysPerBundle];
//		
//		for(int i=0; i<noOfDirectionPairs; i++)
//		{
//			newDirectionsIn[i] = getDirectionInOrZ(i);
//			newDirectionsOut[i] = getDirectionOutOrZ(i);
//		}
//
//		for(int i=0; i<noOfRaysPerBundle; i++)
//		{
//			newRayStartPoints[i] = getRayStartPointOr00(i);
//		}
//		
//		directionsIn  = newDirectionsIn;
//		directionsOut = newDirectionsOut;
//		rayStartPoints = newRayStartPoints;
//	}
	
	/**
	 * run this if the number of direction pairs might have changed
	 */
	public void reshapeDirectionPairsArrays()
	{
		Vector3D newDirectionIn[] = new Vector3D[noOfDirectionPairs];
		Vector3D newDirectionOut[] = new Vector3D[noOfDirectionPairs];
		
		for(int i=0; i<noOfDirectionPairs; i++)
		{
			newDirectionIn[i] = getDirectionInOrZ(i);
			newDirectionOut[i] = getDirectionOutOrZ(i);
		}

		directionsIn = newDirectionIn;
		directionsOut = newDirectionOut;
	}


	
	/**
	 * initialise the ray start points to be uniformly distributed on a disc of radius rayStartPointsDiscRadius
	 * in the plane z=-MyMath.TINY (i.e. immediately in front of the first hologram), centred on
	 * the point (0, 0, -MyMath.TINY)
	 */
	public void randomiseRayStartPoints()
	{
		for(int i=0; i<noOfRaysPerBundle; i++)
		{
			Vector2D v2 = Geometry.getRandomPointOnUnitDisk();

			rayStartPoints[i] = new Vector3D(
					rayStartPointsDiscRadius*v2.x,
					rayStartPointsDiscRadius*v2.y,
					-MyMath.TINY	// in a plane immediately in front of the first hologram
					);

			System.out.println("RayPairsParameters::randomiseRayStartPoints: rayStartPoints["+i+"]="+rayStartPoints[i]);
		}
	}
	
	/**
	 * run this if the number of direction pairs might have changed
	 */
	public void randomiseDirectionPairs()
	{
		randomiseDirectionsIn();
		randomiseDirectionsOut();
	}


	
	/**
	 * Create a random unit vector whose direction within a cone of cone angle directionsInConeAngleRad, centred on the z direction.
	 * The polar and azimuthal angles are uniformly  distributed.
	 * @return	a random unit vector whose direction within a cone of cone angle directionsInConeAngleRad, centred on the z direction
	 */
	public Vector3D getRandomDirectionAroundZ()
	{
		double theta = directionsInConeAngleRad*Math.random();	// random polar angle
		double phi = 2.*Math.PI*Math.random();// random azimuthal angle

		double s = Math.sin(theta);		
		return new Vector3D(
				s*Math.cos(phi),
				s*Math.sin(phi),
				Math.cos(theta)
			);
	}
	
	public void randomiseDirectionsIn()
	{
		directionsIn = new Vector3D[noOfDirectionPairs];

		for(int i=0; i<noOfDirectionPairs; i++)
		{
			directionsIn[i] = getRandomDirectionAroundZ();
		}
	}
	
	public void randomiseDirectionsOut()
	{
		directionsOut = new Vector3D[noOfDirectionPairs];

		for(int i=0; i<noOfDirectionPairs; i++)
		{
			directionsOut[i] = getRandomDirectionAroundZ();
		}
	}
	
	public Vector3D calculateDirectionOut(Vector3D directionIn)
	{
		return lawOfRefraction.refract(directionIn);
	}
	
	public void calculateDirectionsOut()
	{
		directionsOut = new Vector3D[noOfDirectionPairs];

		for(int i=0; i<noOfDirectionPairs; i++)
		{
			directionsOut[i] = calculateDirectionOut(directionsIn[i]);
		}

	}

//	public void randomiseSurfaceParameters()
//	{
//		z = new double[noOfSurfaces];
//		a = new double[noOfSurfaces][][];
//		
//		double currentZ = 0;
//		for(int i=0; i<noOfSurfaces; i++)
//		{
//			z[i] = currentZ;
//			currentZ += Math.random();
//			
//			a[i] = new double[polynomialOrder+1][];
//			for(int n=0; n<=polynomialOrder; n++)
//			{
//				a[i][n] = new double[n+1];
//				for(int m=0; m<=n; m++) 
//					a[i][n][m] = .2*(Math.random()-0.5);
//			}
//		}
//	}

	
	/**
	 * @param i
	 * @return	directionIn[i] if it exists, otherwise (0, 0, 1)
	 */
	public Vector3D getDirectionInOrZ(int i)
	{
		if(i >= directionsIn.length) return new Vector3D(0, 0, 1);
		
		return directionsIn[i];
	}
	
	/**
	 * @param i
	 * @return	directionOut[i] if it exists, otherwise (0, 0, 1)
	 */
	public Vector3D getDirectionOutOrZ(int i)
	{
		if(i >= directionsOut.length) return new Vector3D(0, 0, 1);
		
		return directionsOut[i];
	}
	
	/**
	 * @param i
	 * @return	rayStartPoint[i] if it exists, otherwise (0, 0)
	 */
	public Vector3D getRayStartPointOr00(int i)
	{
		if(i >= rayStartPoints.length) return new Vector3D(0, 0, 0);
		
		return rayStartPoints[i];
	}
	
	

	
//	//
//	//  GUI stuff
//	//
//	
//	private LabelledVector3DPanel directionsInPanel[];
//	private LabelledVector3DPanel directionsOutPanel[];
//	private LabelledVector2DPanel rayStartPointsPanel[];
//	
//	/**
//	 * (re)populate the surface-parameters tabbed pane
//	 */
//	public void repopulateSurfaceParametersTabbedPane(JTabbedPane surfaceParametersTabbedPane)
//	{
//		zPanel = new LabelledDoublePanel[noOfSurfaces + 1];
//		aPanel = new DoublePanel[noOfSurfaces + 1][][];
//		
//		int selectedIndex = surfaceParametersTabbedPane.getSelectedIndex();
//		if((selectedIndex < 0) || (selectedIndex >= noOfSurfaces)) selectedIndex = 0;
//		
//		// remove any existing tabs
//		surfaceParametersTabbedPane.removeAll();
//		
//		// add new tabs
//		for(int i=0; i<noOfSurfaces; i++)
//		{
//			JPanel surfaceNPanel = new JPanel();
//			surfaceNPanel.setLayout(new MigLayout("insets 0"));
//			surfaceParametersTabbedPane.addTab("Surface #"+i, new JScrollPane(surfaceNPanel));
//
//			zPanel[i] = new LabelledDoublePanel("z");
//			zPanel[i].setNumber(z[i]);
//			surfaceNPanel.add(zPanel[i], "wrap");
//			
//			surfaceNPanel.add(new JLabel("<html>&Phi;(<i>x</i>, <i>y</i>) = </html>"), "wrap");
//			
//			// add the arrays
//			aPanel[i] = new DoublePanel[polynomialOrder+1][];
//			for(int n=0; n<=polynomialOrder; n++)
//			{
//				aPanel[i][n] = new DoublePanel[polynomialOrder+1];
//				for(int m=0; m<=n; m++)
//				{
//					aPanel[i][n][m] = new DoublePanel();
//					if(n == 0) 
//					{
//						aPanel[i][n][m].setBackground(Color.lightGray);
//						aPanel[i][n][m].setToolTipText("This coefficient doesn't actually have any effect");
//					}
//					aPanel[i][n][m].setNumber(a[i][n][m]);
//					surfaceNPanel.add(aPanel[i][n][m]);
//					surfaceNPanel.add(new JLabel(
//							"<html>" +
//							((m != 0)?"<i>x</i>"+((m != 1)?"<sup>"+m+"</sup>":""):"") +
//							((n-m != 0)?"<i>y</i>"+((n-m != 1)?"<sup>"+(n-m)+"</sup>":""):"") +
//							"</html>"
//						));
//					if(m < n) surfaceNPanel.add(new JLabel("+"));
//				}
//				surfaceNPanel.add(new JLabel((n < polynomialOrder)?"+":""), "wrap");
//			}
//		}
//		
//		if(selectedIndex < surfaceParametersTabbedPane.getTabCount()) surfaceParametersTabbedPane.setSelectedIndex(selectedIndex);
//		
//		surfaceParametersTabbedPane.revalidate();
//	}
//	
//	public void acceptGUIEntries(IntPanel noOfSurfacesPanel, IntPanel polynomialOrderPanel, JTabbedPane surfaceParametersTabbedPane)
//	{
//		// read the coefficient values *before* re-shaping the a arrays
//		for(int i=0; i<noOfSurfaces; i++)
//			for(int n=0; n<=polynomialOrder; n++)
//				for(int m=0; m<=n; m++)
//					a[i][n][m] = aPanel[i][n][m].getNumber();
//		
//		//  read the z values *before*  re-shaping the z array
//		for(int i=0; i<noOfSurfaces; i++)
//			z[i] = zPanel[i].getNumber();
//		
//		if((noOfSurfaces != noOfSurfacesPanel.getNumber()) || (polynomialOrder != polynomialOrderPanel.getNumber()))
//		{
//			noOfSurfaces = noOfSurfacesPanel.getNumber();
//			polynomialOrder = polynomialOrderPanel.getNumber();
//
//			reshapeSurfaceParametersArrays();
//			repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
//		}
//
//	}
}
