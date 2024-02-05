package optics.raytrace.surfaces;

import javax.swing.JComponent;
import javax.swing.JPanel;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.Editable;
import optics.raytrace.core.Orientation;

/**
 * A direct implementation of a phase hologram of a polynomial phase.
 * The phase profile is of the form
 *     Phi(r) = (pi/lambda) Sum_n=0^p Sum_m=0^n a_nm x^m y^(n-m),
 * where 
 *     x = (r - origin).xHat
 *     y = (r - origin).yHat
 * and r is a position on the hologram surface.
 * 
 * @author johannes
 *
 */
public class PhaseHologramWithPolynomialPhase extends PhaseHologram
implements Editable
{
	private static final long serialVersionUID = -3513408628417889642L;

	/**
	 * the coefficients {{a_00},{a_10, a_11}, ..., {a_p0, a_p1, a_p2, a_p3, ...}
	 */
	private double a[][];
	
	/**
	 * the origin, i.e. the point x=y=0
	 */
	private Vector3D origin;
	
	/**
	 * unit vector in the x direction
	 */
	private Vector3D xHat;

	/**
	 * unit vector in the y direction
	 */
	private Vector3D yHat;
	

	//
	// constructors etc.
	//

	public PhaseHologramWithPolynomialPhase(
			double a[][],
			Vector3D origin,
			Vector3D xHat,
			Vector3D yHat,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		super(throughputCoefficient, reflective, shadowThrowing);
		
		this.a = a;
		this.origin = origin;
		this.xHat = xHat;
		this.yHat = yHat;
	}

	/**
	 * A phase hologram with a constant phase, i.e. which doesn't do anything
	 * @param throughputCoefficient
	 * @param reflective
	 * @param shadowThrowing
	 */
	public PhaseHologramWithPolynomialPhase(
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing
		)
	{
		this(
				new double[0][0],	// a
				new Vector3D(0, 0, 0),	// origin
				new Vector3D(1, 0, 0),	// xHat
				new Vector3D(0, 1, 0),	// yHat,
				throughputCoefficient,
				reflective,
				shadowThrowing
			);
	}

	public PhaseHologramWithPolynomialPhase(PhaseHologramWithPolynomialPhase original) {
		this(
				original.getA(),	// a
				original.getOrigin(),	// origin
				original.getxHat(),	// xHat
				original.getyHat(),	// yHat,
				original.getTransmissionCoefficient(),
				original.isReflective(),
				original.isShadowThrowing()
			);
	}
	
	@Override
	public PhaseHologramWithPolynomialPhase clone()
	{
		return new PhaseHologramWithPolynomialPhase(this);
	}


	//
	// setters & getters
	//
	


	/**
	 * @return the a
	 */
	public double[][] getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(double[][] a) {
		this.a = a;
	}

	/**
	 * @return the origin
	 */
	public Vector3D getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Vector3D origin) {
		this.origin = origin;
	}

	/**
	 * @return the xHat
	 */
	public Vector3D getxHat() {
		return xHat;
	}

	/**
	 * @param xHat the xHat to set
	 */
	public void setxHat(Vector3D xHat) {
		this.xHat = xHat.getNormalised();
	}

	/**
	 * @return the yHat
	 */
	public Vector3D getyHat() {
		return yHat;
	}

	/**
	 * @param yHat the yHat to set
	 */
	public void setyHat(Vector3D yHat) {
		this.yHat = yHat.getNormalised();
	}
	
	//
	// phaseHologram methods
	//
	
	private double polypow(double a, int e)
	{
		return (e==0)?1:Math.pow(a, e);
	}
	
	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{
		// The phase profile is of the form
		//     Phi(r) = (2 pi/lambda) Sum_m=0^n a_m x^m y^(n-m),
		// so the (tangential) phase gradient is
		//     {d Phi/d x, d Phi/d y} = (2 pi/lambda) {Sum_m=0^n m a_m x^(m-1) y^(n-m), Sum_m=0^n (n-m) a_m x^m y^(n-m-1)}
		// This method needs to return the phase gradient divided by 2 pi/lambda, i.e. 
		//     {Sum_m=0^n m a_m x^(m-1) y^(n-m), Sum_m=0^n (n-m) a_m x^m y^(n-m-1)}.
		
		Vector3D r = Vector3D.difference(surfacePosition, origin);
		double x = Vector3D.scalarProduct(r, xHat);
		double y = Vector3D.scalarProduct(r, yHat);
		
		// calculate the x and y components of the tangential direction change, tx and ty
		
		// initialise tx and ty to 0, ...
		double tx = 0;
		double ty = 0;
		
		// ... add all the relevant terms, ...
		int p = a.length-1;	// max power
		for(int n=0; n<=p; n++)
			for(int m=0; m <= n; m++)
			{
				if(m > 0)	tx = tx +    m *a[n][m]*polypow(x, m-1)*polypow(y, n-m);
				if(n-m > 0)	ty = ty + (n-m)*a[n][m]*polypow(x, m)  *polypow(y, n-m-1);
				if(Double.isNaN(tx+ty))
					System.out.println("tx="+tx+", ty="+ty);
			}
		
		// ...  and return them, assembled into the tangential-direction-change vector
		return new Vector3D(tx, ty, 0);
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

	
	//
	// Editable methods
	//

	@Override
	public String getDescription() {
		return "Phase hologram with polynomial phase";
	}
	
	private JPanel editPanel;

	@Override
	public JComponent getEditPanel() {
		if(editPanel == null) initialiseEditPanel();
		
		return editPanel;
	}

	@Override
	public void setValuesInEditPanel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acceptValuesInEditPanel() {
		// TODO Auto-generated method stub
		
	}
	
	
	
//	private ArrayList<DoublePanel> aPanel;
//	private LabelledVector3DPanel originPanel;
//	private LabelledVector3DPanel xHatPanel;
//	private LabelledVector3DPanel yHatPanel;

	
	/**
	 * initialise the edit panel
	 */
	private void initialiseEditPanel() {
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		// TODO
	}

}
