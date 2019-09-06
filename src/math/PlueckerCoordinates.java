package math;

/**
 * Plücker coordinates -- see S. J. Teller, Computing the Antipenumbra of an Area Light Source, Computer Graphics 26, 139-148 (1992)
 * 
 * @author johannes
 */
public class PlueckerCoordinates {
	/**
	 * the Plücker coordinates, p[0] to p[5]
	 */
	private double[] p;

	/**
	 * Create Pluecker coordinates given by p[0] to p[5]
	 * @param p
	 */
	public PlueckerCoordinates(double[] p)
	{
		// reserve space for the Plücker coordinates...
		this.p = new double[6];

		// ... and copy them over from the given ones
		for(int i=0; i<6; i++) this.p[i] = p[i];
	}
	
	public PlueckerCoordinates(double p0, double p1, double p2, double p3, double p4, double p5)
	{
		// reserve space for the Plücker coordinates...
		p = new double[6];

		// ... and copy them over from the given ones
		p[0] = p0;
		p[1] = p1;
		p[2] = p2;
		p[3] = p3;
		p[4] = p4;
		p[5] = p5;
	}
	
	/**
	 * Create Plücker coordinates for the directed line through the points p and q
	 * @param p
	 * @param q
	 */
	public PlueckerCoordinates(Vector3D p, Vector3D q)
	{
		this(
				p.x*q.y-q.x*p.y,	// p0
				p.x*q.z-q.x*p.z,	// p1
				p.x-q.x,	// p2			 
				p.y*q.z-q.y*p.z,	// p3
				p.z-q.z,	// p4
				q.y-p.y	// p5
			 );
	}


	
	// setters & getters

	public double[] getP() {
		return p;
	}

	public void setP(double[] p) {
		this.p = p;
	}

	
	
	// really useful method
	
	/**
	 * calculate a number whose sign indicates on which side two lines with Plücker coordinates a and b pass
	 * @param a
	 * @param b
	 * @return
	 */
	public static double side(PlueckerCoordinates a, PlueckerCoordinates b)
	{
		return a.p[0]*b.p[4] + a.p[1]*b.p[5] + a.p[2]*b.p[3] + a.p[4]*b.p[0] + a.p[5]*b.p[1] + a.p[3]*b.p[2];
	}
	
}
