package optics.raytrace.research.refractiveTaylorSeries;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Random;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.surfaces.PhaseHologramWithPolynomialPhase;

/**
 * Parameters describing a set of planar phase holograms, located at {0, dz[1], dz[1]+dz[2], ...}.
 * Each phase hologram has a polynomial phase distribution; the phase distribution of the ith hologram is
 * phase_i(x, y) = a[i][0][0] 
 *               + a[i][1][0] x   + a[i][1][1] y 
 *               + a[i][2][0] x^2 + a[i][2][1] x y + a[i][2][2] y^2
 *               + ...
 */
public class SurfaceParameters implements Serializable
{
	
	private static final long serialVersionUID = 4091100358326999728L;

	/**
	 * number of surfaces
	 */
	protected int noOfSurfaces;
	
	/**
	 * polynomial order of the phase distribution of all surfaces
	 */
	protected int polynomialOrder;

	/**
	 * {dz[0], dz[1], dz[2], ...} where dz[i] is the z separation between hologram  #i-1 and hologram #i
	 */
	protected double dz[];
	
	/**
	 * Each phase hologram has a polynomial phase distribution; the phase distribution of the ith hologram is
	 * phase_i(x, y) = a[i][0][0] 
	 *               + a[i][1][0] x   + a[i][1][1] y 
	 *               + a[i][2][0] x^2 + a[i][2][1] x y + a[i][2][2] y^2
	 *               + ...
	 */
	protected double a[][][];
	
	/**
	 * if dzOptimizable[i] is true, dz[i] is optimizable by the algorithm
	 */
	protected boolean dzOptimizable[];
	
	/**
	 * if aOptimizable[i][j][k] is true, a[i][j][k] is optimizable by the algorithm
	 */
	protected boolean aOptimizable[][][];
	
	/**
	 *  each phase hologram will be in the plane spanned by xHat, yHat. Subsequent holograms will be spaced along zhat by dz, starting at the oVector. 
	 */
	
	protected Vector3D xHat, yHat, zHat, oVector; 

	// constructors
	
	public SurfaceParameters(int noOfSurfaces, int polynomialOrder, double[] dz, double[][][] a, 
			Vector3D xHat, Vector3D yHat,Vector3D zHat, Vector3D oVector,
			boolean[] dzEditable, boolean[][][] aEditable) {
		super();
		this.noOfSurfaces = noOfSurfaces;
		this.polynomialOrder = polynomialOrder;
		this.xHat = xHat;
		this.yHat = yHat;
		this.zHat = zHat;
		this.oVector = oVector;
		this.dz = dz;
		this.a = a;
		this.dzOptimizable = dzEditable;
		this.aOptimizable = aEditable;
	}
	
	public SurfaceParameters(int noOfSurfaces, int polynomialOrder, double[] dz, double[][][] a, boolean[] dzEditable,
			boolean[][][] aEditable) {
		super();
		this.noOfSurfaces = noOfSurfaces;
		this.polynomialOrder = polynomialOrder;
		this.xHat = Vector3D.X;
		this.yHat = Vector3D.Y;
		this.zHat = Vector3D.Z;
		this.oVector = Vector3D.O;
		this.dz = dz;
		this.a = a;
		this.dzOptimizable = dzEditable;
		this.aOptimizable = aEditable;
	}

	/**
	 * Create the arrays etc., set the values to standard values
	 * @param polynomialOrder
	 * @param noOfSurfaces
	 */
	public SurfaceParameters(int noOfSurfaces, int polynomialOrder) {
		super();
		this.noOfSurfaces = noOfSurfaces;
		this.polynomialOrder = polynomialOrder;
		this.xHat = Vector3D.X;
		this.yHat = Vector3D.Y;
		this.zHat = Vector3D.Z;
		this.oVector = Vector3D.O;
		
		initialiseSurfaceParametersArrays();
	}
	
	/**
	 * Create a deep copy of the original
	 * @param original
	 */
	public SurfaceParameters(SurfaceParameters original)
	{
		this(original.getNoOfSurfaces(), original.getPolynomialOrder());
		
		dz = new double[noOfSurfaces];
		a = new double[noOfSurfaces][][];
		dzOptimizable = new boolean[noOfSurfaces];
		aOptimizable = new boolean[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			dz[i] = original.getDz()[i];
			dzOptimizable[i] = original.getDzEditable()[i];
			
			a[i] = new double[polynomialOrder+1][];
			aOptimizable[i] = new boolean[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				a[i][n] = new double[n+1];
				aOptimizable[i][n] = new boolean[n+1];
				for(int m=0; m<=n; m++) 
				{
					a[i][n][m] = original.getA()[i][n][m];
					aOptimizable[i][n][m] = original.getaEditable()[i][n][m];
				}
			}
		}
	}




	// setters & getters

	/**
	 * @return the noOfSurfaces
	 */
	public int getNoOfSurfaces() {
		return noOfSurfaces;
	}

	/**
	 * @param noOfSurfaces the noOfSurfaces to set
	 */
	public void setNoOfSurfaces(int noOfSurfaces) {
		this.noOfSurfaces = noOfSurfaces;
	}

	/**
	 * @return the polynomialOrder
	 */
	public int getPolynomialOrder() {
		return polynomialOrder;
	}

	/**
	 * @param polynomialOrder the polynomialOrder to set
	 */
	public void setPolynomialOrder(int polynomialOrder) {
		this.polynomialOrder = polynomialOrder;
	}

	/**
	 * @return the dz
	 */
	public double[] getDz() {
		return dz;
	}

	/**
	 * @param dz the dz to set
	 */
	public void setDz(double[] dz) {
		this.dz = dz;
	}

	/**
	 * @return the a
	 */
	public double[][][] getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(double[][][] a) {
		this.a = a;
	}
	
	/**
	 * @return the dzEditable
	 */
	public boolean[] getDzEditable() {
		return dzOptimizable;
	}

	/**
	 * @param dzEditable the dzEditable to set
	 */
	public void setDzEditable(boolean[] dzEditable) {
		this.dzOptimizable = dzEditable;
	}

	/**
	 * @return the aEditable
	 */
	public boolean[][][] getaEditable() {
		return aOptimizable;
	}

	/**
	 * @param aEditable the aEditable to set
	 */
	public void setaEditable(boolean[][][] aEditable) {
		this.aOptimizable = aEditable;
	}
	

	public void writeParameters(PrintStream printStream)
	{
		// printStream.println("parameterName = "+parameterName);
		
		printStream.println("numberOfSurfaces="+noOfSurfaces);
		printStream.println("polynomialOrder="+polynomialOrder);
		printStream.println("xHat="+xHat);
		printStream.println("yHat="+yHat);
		printStream.println("zHat="+zHat);
		printStream.println("oVector="+oVector);

		double z = 0;
		for(int i=0; i<noOfSurfaces; i++) {
			if(i == 0) z = 0;
			else z += dz[i];
			printStream.println("Surface #"+i);
			printStream.println("  dz["+i+"]="+dz[i]+" (z="+z);
			printStream.println("  dzEditable["+i+"]="+dzOptimizable[i]);
			for(int n=0; n<=polynomialOrder; n++)
				for(int m=0; m<=n; m++)
				{
					printStream.println("  a["+i+"]["+n+"]["+m+"]="+a[i][n][m]);
					printStream.println("  aEditable["+i+"]["+n+"]["+m+"]="+aOptimizable[i][n][m]);
				}
		}
	}


	// useful
	
	public void initialiseSurfaceParametersArrays()
	{
		dz = new double[noOfSurfaces];
		a = new double[noOfSurfaces][][];
		dzOptimizable = new boolean[noOfSurfaces];
		aOptimizable = new boolean[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			dz[i] = (i==0)?0:1;
			dzOptimizable[i] = (i != 0);
			
			a[i] = new double[polynomialOrder+1][];
			aOptimizable[i] = new boolean[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				a[i][n] = new double[n+1];
				aOptimizable[i][n] = new boolean[n+1];
				for(int m=0; m<=n; m++)
				{
					a[i][n][m] = 0;
					aOptimizable[i][n][m] = (n != 0);
				}
			}
		}
	}	

	/**
	 * run this if the number of surfaces or the polynomial order might have changed
	 */
	public void reshapeSurfaceParametersArrays()
	{
		double[] newDz = new double[noOfSurfaces];
		double[][][] newA = new double[noOfSurfaces][][];
		boolean[] newDzEditable = new boolean[noOfSurfaces];
		boolean[][][] newAEditable = new boolean[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			newDz[i] = getDzOr1(i);
			newDzEditable[i] = getDzEditableOrTrue(i);
			
			newA[i] = new double[polynomialOrder+1][];
			newAEditable[i] = new boolean[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				newA[i][n] = new double[n+1];
				newAEditable[i][n] = new boolean[n+1];
				for(int m=0; m<=n; m++) 
				{
					newA[i][n][m] = getAOr0(i, n, m);
					newAEditable[i][n][m] = getAEditableOrTrue(i, n, m);
				}
			}
		}

		dz = newDz;
		a = newA;
		dzOptimizable = newDzEditable;
		aOptimizable = newAEditable;
	}
	
	public double random()
	{
		// a random number between -1 and 1
		double r = 2*(Math.random() - 0.5);
		
		// a random number that is biased more towards being small
		return r*r*r;
	}
	
	public double getRandomDzValue()
	{
		return 0.1+0.9*Math.random();
	}
	
	public double getRandomAValue()
	{
		return 10*random();
	}

	public void randomiseSurfaceParameters()
	{
		double[] newDz = new double[noOfSurfaces];
		double[][][] newA = new double[noOfSurfaces][][];
		boolean[] newDzEditable = new boolean[noOfSurfaces];
		boolean[][][] newAEditable = new boolean[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			newDzEditable[i] = getDzEditableOrTrue(i);
			newDz[i] = newDzEditable[i]?getRandomDzValue():getDzOr1(i);
			
			newA[i] = new double[polynomialOrder+1][];
			newAEditable[i] = new boolean[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				newA[i][n] = new double[n+1];
				newAEditable[i][n] = new boolean[n+1];
				for(int m=0; m<=n; m++) 
				{
					newAEditable[i][n][m] = getAEditableOrTrue(i, n, m);
					newA[i][n][m] = newAEditable[i][n][m]?getRandomAValue():getAOr0(i, n, m);
				}
			}
		}
		
		dz = newDz;
		a = newA;
		dzOptimizable = newDzEditable;
		aOptimizable = newAEditable;
	}

	/**
	 * Create a randomly and slightly modified deep copy of this
	 * @return
	 */
	public SurfaceParameters getNeighbouringSurfaceParameters()
	{
		// start with a (deep) copy of the current surface parameters
		SurfaceParameters surfaceParameters2 = new SurfaceParameters(this);
		
		// and alter it
		double rand = Math.random();
		if(rand < 0.6)
			// pick one random parameter and change its value
			surfaceParameters2.changeRandomParameter();
		else if(rand < 0.9)
		{
			// pick two random parameters and change their values
			surfaceParameters2.changeRandomParameter();
			surfaceParameters2.changeRandomParameter();	// (there is a chance that this changes the same parameter -- too bad)
		}
		else
		{
			// pick N (N>2) random parameters and change their values
			int N = 3+new Random().nextInt(5);	// no of parameters whose values to change
			for(int n=0; n<N; n++) surfaceParameters2.changeRandomParameter();	// (there is a chance that this changes the same parameter more than once -- too bad)
		}
		
		return surfaceParameters2;
	}
	
	private void changeRandomParameter()
	{
		boolean changeMade = false;
		while(!changeMade)
		{
			if((Math.random() < 0.1) && (noOfSurfaces > 1))	// with 20% probability...
			{
				// ...change a z value, but not that of surface #0, which is always 0
				int i = 1+new Random().nextInt(noOfSurfaces-1);
				if(dzOptimizable[i])
				{
					dz[i] = getRandomDzValue();
					changeMade = true;
				}
			}
			else	// with 90% probability...
			{
				// ... change an a coefficient, but not that corresponding to a constant phase term, which doesn't actually do anything
				// pick a surface
				int i = new Random().nextInt(noOfSurfaces);
				// pick a polynomial order
				int n = 1 + new Random().nextInt(polynomialOrder);	// the +1 avoids the 0th order (i.e. leaves the constant term, which doesn't do anything, alone)
				// pick a term
				int m = new Random().nextInt(n+1);
				
				if(aOptimizable[i][n][m])
				{
					a[i][n][m] = getRandomAValue();
					changeMade = true;
				}
			}
		}
	}
	
	/**
	 * @param i
	 * @param n
	 * @param m
	 * @return	a[i][n][m] if it exists, otherwise 0
	 */
	public double getAOr0(int i, int n, int m)
	{
		if(i >= a.length) return 0;
		
		if(n >= a[i].length) return 0;
		
		if(m >= a[i][n].length) return 0;
		
		return a[i][n][m];
	}
	
	public boolean getAEditableOrTrue(int i, int n, int m)
	{
		if(i >= aOptimizable.length) return n != 0;
		
		if(n >= aOptimizable[i].length) return n != 0;
		
		if(m >= aOptimizable[i][n].length) return n != 0;
		
		return aOptimizable[i][n][m];
	}

	/**
	 * @param i
	 * @return	dz[i] if it exists, otherwise 1
	 */
	public double getDzOr1(int i)
	{
		if(i >= dz.length) return 1;
		
		return dz[i];
	}
	
	public boolean getDzEditableOrTrue(int i)
	{
		if(i >= dzOptimizable.length) return i != 0;
		
		return dzOptimizable[i];
	}

	
	/**
	 * @param	transmissionCoefficient	the transmission coefficient of each of the surfaces
	 * @return	a DirectionChangingSurfaceSequence that corresponds to the surface parameters
	 */
	public DirectionChangingSurfaceSequence createCorrespondingDirectionChangingSurfaceSequence(double transmissionCoefficient)
	{
		DirectionChangingSurfaceSequence dcss = new DirectionChangingSurfaceSequence();

		// add a few surfaces
		double z = 0;
		for(int i=0; i<noOfSurfaces; i++)
		{
			if(i==0) z=0;
			else z += dz[i];
			Vector3D origin  = Vector3D.sum(zHat.getWithLength(z), oVector);
						
			PhaseHologramWithPolynomialPhase ppp = new PhaseHologramWithPolynomialPhase(
					a[i],	// a,
					origin,
					xHat,	// xHat,
					yHat,	// yHat,
					transmissionCoefficient,	// throughputCoefficient,
					false,	// reflective,
					false	// shadowThrowing
				);
			Plane p = new Plane(
					"Plane #"+i,	// description,
					origin,	// pointOnPlane,
					Vector3D.crossProduct(xHat, yHat).getNormalised(),	// normal, 
					ppp,	// surfaceProperty,
//					SurfaceColour.RED_MATT,
					null,	// parent,
					null	// studio
				);
			
			dcss.addSceneObjectPrimitiveWithDirectionChangingSurface(p);
		}

		return dcss;
	}
	
	/**
	 * Save this object to a .sur file
	 * @param filename
	 * @throws IOException
	 */
	public void save(String filename) throws IOException 
	{
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(this);
		objectOutputStream.flush();
		objectOutputStream.close();
	}
	
	public static SurfaceParameters load(String filename) throws IOException, ClassNotFoundException
	{
		FileInputStream fileInputStream = new FileInputStream(filename);
	    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
	    SurfaceParameters surfaceParameters = (SurfaceParameters) objectInputStream.readObject();
	    objectInputStream.close(); 
	    return surfaceParameters;
	}

	
	//
	//  GUI stuff
	//
	
	// note that these are all static as there is only one set of these;
	// also note that static fields don't get serialised (i.e. they are automatically transient)
	private static LabelledDoublePanel dzPanel[];
	private static DoublePanel aPanel[][][];
	private static JCheckBox dzEditableCheckBox[];
	private static JCheckBox aEditableCheckBox[][][];
	
	/**
	 * (re)populate the surface-parameters tabbed pane
	 */
	public void repopulateSurfaceParametersTabbedPane(JTabbedPane surfaceParametersTabbedPane)
	{
		dzPanel = new LabelledDoublePanel[noOfSurfaces + 1];
		aPanel = new DoublePanel[noOfSurfaces + 1][][];
		dzEditableCheckBox = new JCheckBox[noOfSurfaces + 1];
		aEditableCheckBox = new JCheckBox[noOfSurfaces + 1][][];
		
		int selectedIndex = surfaceParametersTabbedPane.getSelectedIndex();
		if((selectedIndex < 0) || (selectedIndex >= noOfSurfaces)) selectedIndex = 0;
		
		// remove any existing tabs
		surfaceParametersTabbedPane.removeAll();
		
		// add new tabs
		for(int i=0; i<noOfSurfaces; i++)
		{
			JPanel surfaceNPanel = new JPanel();
			surfaceNPanel.setLayout(new MigLayout("insets 0"));
			surfaceParametersTabbedPane.addTab("Surface #"+i, new JScrollPane(surfaceNPanel));

			dzPanel[i] = new LabelledDoublePanel("dz");
			dzPanel[i].setNumber(dz[i]);
			dzEditableCheckBox[i] = new JCheckBox();
			dzEditableCheckBox[i].setSelected(dzOptimizable[i]);
			dzEditableCheckBox[i].setToolTipText("Randomisable?");
			surfaceNPanel.add(GUIBitsAndBobs.makeRow(dzPanel[i], dzEditableCheckBox[i]), "span");
			
			surfaceNPanel.add(new JLabel("<html>&Phi;(<i>x</i>, <i>y</i>) = </html>"), "wrap");
			
			// add the arrays
			aPanel[i] = new DoublePanel[polynomialOrder+1][];
			aEditableCheckBox[i] = new JCheckBox[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				aPanel[i][n] = new DoublePanel[polynomialOrder+1];
				aEditableCheckBox[i][n] = new JCheckBox[polynomialOrder+1];
				for(int m=0; m<=n; m++)
				{
					aPanel[i][n][m] = new DoublePanel();
					aPanel[i][n][m].setNumber(a[i][n][m]);
					aEditableCheckBox[i][n][m] = new JCheckBox();
					aEditableCheckBox[i][n][m].setSelected(aOptimizable[i][n][m]);
					aEditableCheckBox[i][n][m].setToolTipText("Randomisable?");
					surfaceNPanel.add(aPanel[i][n][m]);
					surfaceNPanel.add(aEditableCheckBox[i][n][m]);
					surfaceNPanel.add(new JLabel(
							"<html>" +
							((m != 0)?"<i>x</i>"+((m != 1)?"<sup>"+m+"</sup>":""):"") +
							((n-m != 0)?"<i>y</i>"+((n-m != 1)?"<sup>"+(n-m)+"</sup>":""):"") +
							"</html>"
						));
					if(m < n) surfaceNPanel.add(new JLabel("+"));
				}
				surfaceNPanel.add(new JLabel((n < polynomialOrder)?"+":""), "wrap");
			}
		}
		
		if(selectedIndex < surfaceParametersTabbedPane.getTabCount()) surfaceParametersTabbedPane.setSelectedIndex(selectedIndex);
		
		surfaceParametersTabbedPane.revalidate();
	}
	
	public void acceptGUIEntries(IntPanel noOfSurfacesPanel, IntPanel polynomialOrderPanel, JTabbedPane surfaceParametersTabbedPane)
	{
		// read the coefficient values *before* re-shaping the a arrays
		for(int i=0; i<noOfSurfaces; i++)
			for(int n=0; n<=polynomialOrder; n++)
				for(int m=0; m<=n; m++)
				{
					a[i][n][m] = aPanel[i][n][m].getNumber();
					aOptimizable[i][n][m] = aEditableCheckBox[i][n][m].isSelected();
				}
		
		//  read the z values *before*  re-shaping the z array
		for(int i=0; i<noOfSurfaces; i++)
		{
			dz[i] = dzPanel[i].getNumber();
			dzOptimizable[i] = dzEditableCheckBox[i].isSelected();
		}
		
		if((noOfSurfaces != noOfSurfacesPanel.getNumber()) || (polynomialOrder != polynomialOrderPanel.getNumber()))
		{
			noOfSurfaces = noOfSurfacesPanel.getNumber();
			polynomialOrder = polynomialOrderPanel.getNumber();

			reshapeSurfaceParametersArrays();
			repopulateSurfaceParametersTabbedPane(surfaceParametersTabbedPane);
		}

	}
}
