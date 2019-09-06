package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.SimpleGlensHologram;

/**
 * A generalised lens, with two different focal lengths.
 * A glens has a +ve and a -ve side;
 * +ve space refers to light rays travelling on the +ve side, -ve space to those on the -ve side.
 * The direction of the optical axis is given by
 * 	aPos: a unit vector in the direction of the optical axis, pointing in the direction of +ve space
 * The cardinal points are as follows:
 * 	principalPoint: the principal point, P, where the optical axis intersects the plane of the glens
 * 	nodalPoint: the nodal point, N (any light rays incident on N pass through undeviated; in a lens, N=P)
 * 	focalPointPos: the focal point in +ve space, F+
 * 	focalPointNeg: the focal point in -ve space, F-
 * 
 * @author johannes
 */
public class SimpleGlens extends Disc
{
	private static final long serialVersionUID = -7274656711011361698L;

	/**
	 * Creates an instance of a circular glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param description
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param nodalPoint
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 * @param apertureRadius
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public SimpleGlens(
			String description,
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,	// not necessarily the principal point!
			Vector3D nodalPoint,
			double focalLengthNeg,
			double focalLengthPos,
			double apertureRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		) 
	{
		super(
				description,
				principalPoint,
				opticalAxisDirectionPos,
				apertureRadius,
				new SimpleGlensHologram(
					opticalAxisDirectionPos,
					nodalPoint,
					// GlensHologram.calculateNodalPoint(principalPoint, opticalAxisDirectionPos, focalLengthNeg, focalLengthPos),	// nodal point,
					focalLengthNeg,
					focalLengthPos,
					transmissionCoefficient,
					shadowThrowing
				),
				parent,
				studio
			);
	}
	
	
	/**
	 * Constructor that creates a clone of the original glens
	 * @param original
	 */
	public SimpleGlens(SimpleGlens original)
	{
		this(
				original.getDescription(),
				original.getOpticalAxisDirectionPos(),
				original.getPrincipalPoint(),
				original.getNodalPoint(),
				original.getFocalLengthNeg(),
				original.getFocalLengthPos(),
				original.getApertureRadius(),
				original.getTransmissionCoefficient(),
				original.isShadowThrowing(),
				original.getParent(),
				original.getStudio()
		);
		
//		super(original);
//		setFocalLength(original.getFocalLength());
//		setTransmissionCoefficient(original.getTransmissionCoefficient());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.Plane#clone()
	 */
	@Override
	public SimpleGlens clone()
	{
		return new SimpleGlens(this);
	}

	/**
	 * The parameters can only be set together, as the GlensHologram parameters depend on a combination of these parameters
	 * @param principalPoint
	 * @param opticalAxisDirectionPos
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParameters(Vector3D opticalAxisDirectionPos, Vector3D principalPoint, double focalLengthNeg, double focalLengthPos)
	{
		super.setCentre(principalPoint);
		super.setNormal(opticalAxisDirectionPos);
		((SimpleGlensHologram)getSurfaceProperty()).setParametersUsingPrincipalPoint(
			opticalAxisDirectionPos,
			principalPoint,
			// GlensHologram.calculateNodalPoint(principalPoint, opticalAxisDirectionPos, focalLengthNeg, focalLengthPos),	// nodal point
			focalLengthNeg, focalLengthPos
		);
	}
	
	public Vector3D getPrincipalPoint()
	{
		return super.getCentre();
	}

	public Vector3D getNodalPoint()
	{
		return ((SimpleGlensHologram)getSurfaceProperty()).getNodalPoint();
	}
	
	public Vector3D getOpticalAxisDirectionPos()
	{
		return ((SimpleGlensHologram)getSurfaceProperty()).getOpticalAxisDirectionPos();
	}

	public double getFocalLengthNeg() {
		return ((SimpleGlensHologram)getSurfaceProperty()).getFocalLengthNeg();
	}

	public double getFocalLengthPos() {
		return ((SimpleGlensHologram)getSurfaceProperty()).getFocalLengthPos();
	}
	
	/**
	 * Call the underlying disc's "radius" here, more sensibly, aperture radius
	 * @return
	 */
	public double getApertureRadius() {
		return super.getRadius();
	}
	
	/**
	 * Call the underlying disc's "radius" here, more sensibly, aperture radius
	 * @param apertureRadius
	 */
	public void setApertureRadius(double apertureRadius) {
		super.setRadius(apertureRadius);
	}

	public double getTransmissionCoefficient() {
		return ((SimpleGlensHologram)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		((SimpleGlensHologram)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	@Override
	public String getType()
	{
		return "Glens";
	}
}
