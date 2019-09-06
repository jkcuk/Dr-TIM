package optics.raytrace.research.idealLensLookalike;

import math.Vector3D;
import optics.raytrace.core.Orientation;
import optics.raytrace.surfaces.PhaseHologram;

/**
 * A pair of planar phase holograms that look, when viewed from two positions, a and b, equivalent to an ideal thin lens.
 * 
 * @author johannes, Stuart
 *
 */
public class PhaseHologramOfIdealLensLookalike extends PhaseHologram
{
	private static final long serialVersionUID = 5894462011623716813L;

	// the points the corresponding ideal thin lens images into each other
	private Vector3D a;	// {ax, ay, az}
	private Vector3D b;	// {bx,by,bz}

	// equivalent ideal thin lens
	private Vector3D idealThinLensNormal;
	
	// phase hologram 1
	private Vector3D phaseHologram1Normal;
	
	// phase hologram 2
	private Vector3D phaseHologram2Normal;
	
	/**
	 * (Common) origin of the coordinate systems that parametrise the surface of the phase holograms
	 */
	private Vector3D o;
	
	/**
	 * Basis vector in u1 direction;
	 * unit vector in the direction of the line along which the holograms intersect
	 */
	private Vector3D u1BasisVector;

	/**
	 * Basis vector in u2 direction, which is in the plane of hologram 1 and perpendicular to u1
	 */
	private Vector3D u2BasisVector;

	/**
	 * Basis vector in w1 direction;
	 * unit vector in the direction of the line along which the holograms intersect (i.e. same as u1)
	 */
	// private Vector3D w1BasisVector;

	/**
	 * Basis vector in w2 direction, which is in the plane of hologram 2 and perpendicular to w1
	 */
	private Vector3D w2BasisVector;

	//
	// constructors etc.
	//

	public PhaseHologramOfIdealLensLookalike(
		Vector3D conjugatePositionInFrontOfThisHologram,
		Vector3D conjugatePositionInFrontOfOtherHologram,
		Vector3D idealThinLensNormal,
		Vector3D thisPhaseHologramNormal,
		Vector3D otherPhaseHologramNormal,
		Vector3D originOfCoordinateSystem,
		Vector3D u1BasisVector,
		Vector3D u2BasisVector,
		// Vector3D w2BasisVector,
		double throughputCoefficient,
		boolean shadowThrowing
	)
	{
		super(throughputCoefficient, false, shadowThrowing);
		
		this.a = conjugatePositionInFrontOfThisHologram;
		this.b = conjugatePositionInFrontOfOtherHologram;
		this.idealThinLensNormal = idealThinLensNormal;
		this.phaseHologram1Normal = thisPhaseHologramNormal;
		this.phaseHologram2Normal = otherPhaseHologramNormal;
		this.o = originOfCoordinateSystem;
		this.u1BasisVector = u1BasisVector;
		// this.w1BasisVector = u1BasisVector;
		this.u2BasisVector = u2BasisVector;
		// this.w2BasisVector = w2BasisVector;
	}

	public PhaseHologramOfIdealLensLookalike(PhaseHologramOfIdealLensLookalike original) {
		super(original);
	}
	
	@Override
	public PhaseHologramOfIdealLensLookalike clone()
	{
		return new PhaseHologramOfIdealLensLookalike(this);
	}


	//
	// setters & getters
	//
	
	public Vector3D getA() {
		return a;
	}

	public void setA(Vector3D a) {
		this.a = a;
	}

	public Vector3D getB() {
		return b;
	}

	public void setB(Vector3D b) {
		this.b = b;
	}

	public Vector3D getIdealThinLensNormal() {
		return idealThinLensNormal;
	}

	public void setIdealThinLensNormal(Vector3D idealThinLensNormal) {
		this.idealThinLensNormal = idealThinLensNormal;
	}

	public Vector3D getPhaseHologram1Normal() {
		return phaseHologram1Normal;
	}

	public void setPhaseHologram1Normal(Vector3D phaseHologram1Normal) {
		this.phaseHologram1Normal = phaseHologram1Normal;
	}

	public Vector3D getPhaseHologram2Normal() {
		return phaseHologram2Normal;
	}

	public void setPhaseHologram2Normal(Vector3D phaseHologram2Normal) {
		this.phaseHologram2Normal = phaseHologram2Normal;
	}

	public Vector3D getO() {
		return o;
	}

	public void setO(Vector3D o) {
		this.o = o;
	}

	public Vector3D getU1BasisVector() {
		return u1BasisVector;
	}

	public void setU1BasisVector(Vector3D u1BasisVector) {
		this.u1BasisVector = u1BasisVector;
	}

	public Vector3D getU2BasisVector() {
		return u2BasisVector;
	}

	public void setU2BasisVector(Vector3D u2BasisVector) {
		this.u2BasisVector = u2BasisVector;
	}

//	public Vector3D getW1BasisVector() {
//		return w1BasisVector;
//	}
//
//	public void setW1BasisVector(Vector3D w1BasisVector) {
//		this.w1BasisVector = w1BasisVector;
//	}

	public Vector3D getW2BasisVector() {
		return w2BasisVector;
	}

	public void setW2BasisVector(Vector3D w2BasisVector) {
		this.w2BasisVector = w2BasisVector;
	}

	
	
	
	//
	// calculation of the phase gradient
	//
	

	@Override
	public Vector3D getTangentialDirectionComponentChangeTransmissive(Vector3D surfacePosition,
			Vector3D surfaceNormal)
	{	
		// translate this into Stuart's parameters
		double ax = a.x - o.x;
		double ay = a.y - o.y;
		double az = a.z - o.z;
		double bx = b.x - o.x;
		double by = b.y - o.y;
		double bz = b.z - o.z;
		// double lx = idealThinLensNormal.x;
		double ly = idealThinLensNormal.y;
		double lz = idealThinLensNormal.z;
		// double h1x = phaseHologram1Normal.x;
		double h1y = phaseHologram1Normal.y;
		double h1z = phaseHologram1Normal.z;
		// double h2x = phaseHologram2Normal.x;
		double h2y = phaseHologram2Normal.y;
		double h2z = phaseHologram2Normal.z;
		
		// find the u and w coordinates of the surface position
		
		Vector3D u1u20 = Vector3D.difference(surfacePosition, o).toBasis(u1BasisVector, u2BasisVector, phaseHologram1Normal);
		double u1 = u1u20.x;
		double u2 = u1u20.y;

		double du1 =((ax - u1)/Math.sqrt(Math.pow(ay*h1y + az*h1z,2)/(Math.pow(h1y,2) + Math.pow(h1z,2)) + Math.pow(ax - u1,2) + Math.pow((az*h1y - ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2,2)) + 
			     (bx - u1 - ((by*h2y + bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
			             (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h1z*ly - h1y*lz)*u2))/
			         (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			           ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			           az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2)))/
			      Math.sqrt((Math.pow(ay*h1y + az*h1z,2)*Math.pow(-(h1z*h2y) + h1y*h2z,2)*Math.pow(by*ly + bz*lz,2)*Math.pow(u2,2))/
			         Math.pow(Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			           ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			           az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2),2) + 
			        Math.pow(u2,2)*Math.pow(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
			            (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			              ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			              az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2)),2) + 
			        Math.pow(bx - u1 + ((-(by*h2y) - bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
			               (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h1z*ly - h1y*lz)*u2))/
			           (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2)),2)));
		
		double du2 = (-(((az*h1y - ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2)/
		        Math.sqrt(Math.pow(ay*h1y + az*h1z,2)/(Math.pow(h1y,2) + Math.pow(h1z,2)) + Math.pow(ax - u1,2) + Math.pow((az*h1y - ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2,2))) + 
			     (u2*(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
			           (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2))))/
			      Math.sqrt((Math.pow(ay*h1y + az*h1z,2)*Math.pow(-(h1z*h2y) + h1y*h2z,2)*Math.pow(by*ly + bz*lz,2)*Math.pow(u2,2))/
			         Math.pow(Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			           ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			           az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2),2) + 
			        Math.pow(u2,2)*Math.pow(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
			            (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			              ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			              az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2)),2) + 
			        Math.pow(bx - u1 + ((-(by*h2y) - bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
			               (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h1z*ly - h1y*lz)*u2))/
			           (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(-(h1z*ly) + h1y*lz)*u2 + 
			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2) + 
			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(h2z*ly - h2y*lz)*u2)),2)));
		
//		double du1 = (((ax - u1)/Math.sqrt(Math.pow(ay*h1y + az*h1z,2)/(Math.pow(h1y,2) + Math.pow(h1z,2)) + Math.pow(ax - u1,2) + Math.pow((-(az*h1y) + ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2,2)) + 
//			       (bx - u1 - ((by*h2y + bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
//			               (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h1z*ly) + h1y*lz)*u2))/
//			           (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2)))/
//			        Math.sqrt((Math.pow(ay*h1y + az*h1z,2)*Math.pow(-(h1z*h2y) + h1y*h2z,2)*Math.pow(by*ly + bz*lz,2)*Math.pow(u2,2))/
//			           Math.pow(Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2),2) + 
//			          Math.pow(u2,2)*Math.pow(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
//			              (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			                ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			                az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2)),2) + 
//			          Math.pow(bx - u1 + ((-(by*h2y) - bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
//			                 (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h1z*ly) + h1y*lz)*u2))/
//			             (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			               ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			               az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2)),2))));
//		
//		double du2 = (-(((-(az*h1y) + ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2)/
//		          Math.sqrt(Math.pow(ay*h1y + az*h1z,2)/(Math.pow(h1y,2) + Math.pow(h1z,2)) + Math.pow(ax - u1,2) + Math.pow((-(az*h1y) + ay*h1z)/Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2)) + u2,2))) + 
//			       (u2*(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
//			             (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			               ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			               az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2))))/
//			        Math.sqrt((Math.pow(ay*h1y + az*h1z,2)*Math.pow(-(h1z*h2y) + h1y*h2z,2)*Math.pow(by*ly + bz*lz,2)*Math.pow(u2,2))/
//			           Math.pow(Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			             ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			             az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2),2) + 
//			          Math.pow(u2,2)*Math.pow(-1 + ((ay*h1y + az*h1z)*(h1y*h2y + h1z*h2z)*(by*ly + bz*lz))/
//			              (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			                ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			                az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2)),2) + 
//			          Math.pow(bx - u1 + ((-(by*h2y) - bz*h2z)*(ay*(Math.pow(h1y,2) + Math.pow(h1z,2))*ly*(bx - u1) + az*(Math.pow(h1y,2) + Math.pow(h1z,2))*lz*(bx - u1) + 
//			                 (ax - bx)*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h1z*ly) + h1y*lz)*u2))/
//			             (Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(by*h2y + bz*h2z)*(h1z*ly - h1y*lz)*u2 + 
//			               ay*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*ly + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*ly + h1y*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2) + 
//			               az*(by*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2y*lz + bz*(Math.pow(h1y,2) + Math.pow(h1z,2))*h2z*lz + h1z*Math.sqrt(Math.pow(h1y,2) + Math.pow(h1z,2))*(-(h2z*ly) + h2y*lz)*u2)),2)));	// TODO
		
		return Vector3D.sum(u1BasisVector.getProductWith(du1), u2BasisVector.getProductWith(du2));
	}

	@Override
	public Vector3D getTangentialDirectionComponentChangeReflective(Orientation incidentLightRayOrientation,
			Vector3D surfacePosition, Vector3D surfaceNormal)
	{
		// TODO Not sure this makes sense completely...
		return getTangentialDirectionComponentChangeTransmissive(surfacePosition, surfaceNormal);
	}

}
