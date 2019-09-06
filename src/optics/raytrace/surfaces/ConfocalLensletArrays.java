package optics.raytrace.surfaces;

import math.*;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;


/**
 * A surface property representing a confocal lenslet array [1].
 * 
 * [1] J. Courtial, "Ray-optical refraction with confocal lenslet arrays", New J. Phys. <b>10</b>,�083033�(2008)
 * 
 * @author Johannes Courtial
 */
public class ConfocalLensletArrays extends GCLAsWithApertures
{	
	private static final long serialVersionUID = 3590603432372739712L;

	/**
	 * Creates an instance of the surface property representing confocal lenslet arrays.
	 * @param eta
	 * @param transmissionCoefficient
	 */
	public ConfocalLensletArrays(
			double eta,
			double transmissionCoefficient,
			boolean shadowThrowing
		)
	{
		super(
				new Vector3D(0, 0, 1),	// a
				new Vector3D(1, 0, 0), 	// u
				new Vector3D(0, 1, 0),	// v
				eta,	// etaU
				eta,	// etaV
				0,	// deltaU
				0,	// deltaV
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				transmissionCoefficient,
				shadowThrowing
			);
	}
	
	public double getEta() {
		return getEtaU();
	}

	public void setEta(double eta) {
		setEtaU(eta);
	}
}
