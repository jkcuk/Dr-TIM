package optics.raytrace.sceneObjects;

import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.GlensSurface;

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
public class Glens extends Disc
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -245687624L;

	/**
	 * Creates an instance of a circular glens.
	 * All parameters are explicitly given exactly as they are being stored.
	 * 
	 * @param description
	 * @param opticalAxisDirectionPos
	 * @param principalPoint
	 * @param meanF
	 * @param nodalPointF
	 * @param focalLengthNegF
	 * @param focalLengthPosF
	 * @param apertureRadius
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public Glens(
			String description,
			Vector3D opticalAxisDirectionPos,
			Vector3D principalPoint,	// not necessarily the principal point!
			double meanF,
			Vector3D nodalPointF,
			double focalLengthNegF,
			double focalLengthPosF,
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
				new GlensSurface(
					opticalAxisDirectionPos,
					principalPoint,	// point on glens
					meanF,
					nodalPointF,
					// GlensHologram.calculateNodalPoint(principalPoint, opticalAxisDirectionPos, focalLengthNeg, focalLengthPos),	// nodal point,
					focalLengthNegF,
					focalLengthPosF,
					transmissionCoefficient,
					shadowThrowing
				),
				parent,
				studio
			);
	}
	
	/**
	 * Creates an instance of a circular glens.
	 * This constructor is suitable for a glens with finite focal lengths
	 * 
	 * @param description
	 * @param principalPoint	the cardinal point P (the principal point) where the optical axis intersects the plane of the glens
	 * @param opticalAxisDirectionPos	unit vector that points along the optical axis, in the +ve direction
	 * @param apertureRadius	aperture radius
	 * @param focalLengthNeg	focal length in -ve space
	 * @param focalLengthPos	focal length in +ve space
	 * @param transmissionCoefficient
	 * @param shadowThrowing
	 * @param parent
	 * @param studio
	 */
	public Glens(
			String description,
			Vector3D principalPoint,
			Vector3D opticalAxisDirectionPos,
			double apertureRadius,
			double focalLengthNeg,
			double focalLengthPos,
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
				new GlensSurface(
					opticalAxisDirectionPos,
					principalPoint,	// point on glens
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
	public Glens(Glens original)
	{
		this(
				original.getDescription(),
				original.getOpticalAxisDirectionPos(),
				original.getPrincipalPoint(),
				original.getMeanF(),
				original.getNodalPointF(),
				original.getFocalLengthNegF(),
				original.getFocalLengthPosF(),
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
	public Glens clone()
	{
		return new Glens(this);
	}

	/**
	 * The parameters can only be set together, as the GlensHologram parameters depend on a combination of these parameters
	 * @param principalPoint
	 * @param opticalAxisDirectionPos
	 * @param focalLengthNeg
	 * @param focalLengthPos
	 */
	public void setParametersForInhomogeneousGlens(Vector3D opticalAxisDirectionPos, Vector3D principalPoint, double focalLengthNeg, double focalLengthPos)
	{
		super.setCentre(principalPoint);
		super.setNormal(opticalAxisDirectionPos);
		((GlensSurface)getSurfaceProperty()).setParametersUsingPrincipalPoint(
			opticalAxisDirectionPos,
			principalPoint,
			// GlensHologram.calculateNodalPoint(principalPoint, opticalAxisDirectionPos, focalLengthNeg, focalLengthPos),	// nodal point
			focalLengthNeg, focalLengthPos
		);
	}
	
	/**
	 * Set the parameters in a way suitable for a homogeneous glens.
	 * 
	 * @param opticalAxisDirectionPos
	 * @param apertureCentre
	 * @param nodalDirection
	 * @param fNegOverFPos
	 */
	public void setParametersForHomogeneousGlens(Vector3D opticalAxisDirectionPos, Vector3D apertureCentre, Vector3D nodalDirection, double fNegOverFPos)
	{
		System.out.println("Glens::setParametersForHomogeneousGlens: nodalDirection="+nodalDirection+", fNegOverFPos="+fNegOverFPos);
		
		super.setCentre(apertureCentre);
		super.setNormal(opticalAxisDirectionPos);

		((GlensSurface)getSurfaceProperty()).setParametersForHomogeneousGlens(
				opticalAxisDirectionPos,
				apertureCentre,
				nodalDirection,
				fNegOverFPos
			);
	}
	
	public double getMeanF()
	{
		return ((GlensSurface)getSurfaceProperty()).getG();
	}
	
	public Vector3D getPrincipalPoint()
	{
		return super.getCentre();
	}

	public Vector3D getNodalPointF()
	{
		return ((GlensSurface)getSurfaceProperty()).getNodalPointG();
	}
	
	public Vector3D getOpticalAxisDirectionPos()
	{
		return ((GlensSurface)getSurfaceProperty()).getOpticalAxisDirectionPos();
	}

	public double getFocalLengthNegF() {
		return ((GlensSurface)getSurfaceProperty()).getFocalLengthNegG();
	}

	public double getFocalLengthPosF() {
		return ((GlensSurface)getSurfaceProperty()).getFocalLengthPosG();
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
		return ((GlensSurface)getSurfaceProperty()).getTransmissionCoefficient();
	}

	public void setTransmissionCoefficient(double transmissionCoefficient) {
		((GlensSurface)getSurfaceProperty()).setTransmissionCoefficient(transmissionCoefficient);
	}
	
	@Override
	public String getType()
	{
		return "Glens";
	}
}
