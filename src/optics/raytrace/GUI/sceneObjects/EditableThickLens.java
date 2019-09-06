package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Complex;
import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.ParametrisedInvertedSphere;
import optics.raytrace.sceneObjects.ParametrisedPlane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectPrimitiveIntersection;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.RefractiveComplex;
import optics.raytrace.utility.CopyModeType;

/**
 * A lens made from some transparent material with a given refractive index and spherical surfaces.
 * @author Johannes Courtial
 */
public class EditableThickLens extends SceneObjectPrimitiveIntersection implements IPanelComponent, ActionListener
{

	//
	// parameters
	//
	
	/**
	 * radius of curvature of the front spherical surface
	 */
	private double radiusOfCurvatureFront;
	
	/**
	 * radius of curvature of the back spherical surface
	 */
	private double radiusOfCurvatureBack;
	
	/**
	 * radius of the circular aperture
	 */
	private double apertureRadius;
	
	/**
	 * thickness of the lens at its thinnest point
	 */
	private double thicknessAtThinnestPoint;
	
	/**
	 * position of the lens centre, half-way between the centres of the two spherical surfaces
	 */
	private Vector3D centre;
	
	/**
	 * normalised direction to the front, along the optical axis
	 */
	private Vector3D unitVectorToFront;
	
	/**
	 * surface property of the front surface
	 */
	private SurfaceProperty surfacePropertyFront;
	
	/**
	 * surface property of the back surface
	 */
	private SurfaceProperty surfacePropertyBack;
	

	//
	// internal variables
	//
	
	/**
	 * the sphere of which the front surface is a part
	 */
	private EditableScaledParametrisedSphere sphereFront;
	
	/**
	 * the sphere of which the back surface is a part
	 */
	private EditableScaledParametrisedSphere sphereBack;
	
	/**
	 * the cylinder which adds constructively to the lens
	 */
	private EditableParametrisedCylinder cylinder;

	
	//
	// GUI panels
	//
	
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledDoublePanel apertureRadiusPanel, radiusOfCurvatureFrontPanel, radiusOfCurvatureBackPanel;
	private LabelledVector3DPanel centrePanel, unitVectorToFrontPanel;
	private SurfacePropertyPanel surfacePropertyFrontPanel, surfacePropertyBackPanel, beingEdited;
	private JButton convertButton;
	
	private IPanel iPanel;

	
	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param apertureRadius
	 * @param radiusOfCurvatureFront
	 * @param radiusOfCurvatureBack
	 * @param centre
	 * @param directionToFront
	 * @param surfacePropertyFront
	 * @param surfacePropertyBack
	 * @param parent
	 * @param studio
	 */
	public EditableThickLens(
			String description,
			double apertureRadius,
			double radiusOfCurvatureFront,
			double radiusOfCurvatureBack,
			Vector3D centre,
			Vector3D vectorToFront,
			SurfaceProperty surfacePropertyFront,
			SurfaceProperty surfacePropertyBack,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		setApertureRadius(apertureRadius);
		setRadiusOfCurvatureFront(radiusOfCurvatureFront);
		setRadiusOfCurvatureBack(radiusOfCurvatureBack);
		setCentre(centre);
		setUnitVectorToFront(vectorToFront);
		setSurfacePropertyFront(surfacePropertyFront);
		setSurfacePropertyBack(surfacePropertyBack);

		addElements();
	}

	/**
	 * Constructor that uses (real) focal length and (real) refractive index as parameters
	 * 
	 * @param description
	 * @param apertureRadius
	 * @param focalLength
	 * @param refractiveIndex
	 * @param centre
	 * @param directionToFront
	 * @param parent
	 * @param studio
	 */
	public EditableThickLens(
			String description,
			double apertureRadius,
			double focalLength,
			double refractiveIndex,
			Vector3D centre,
			Vector3D directionToFront,
			double transmissionCoefficient,
			SceneObject parent, 
			Studio studio
	)
	{
		this(
				description,
				apertureRadius,
				2*focalLength*(refractiveIndex - 1),	// radiusOfCurvatureFront
				2*focalLength*(refractiveIndex - 1),	// radiusOfCurvatureBack
				centre,
				directionToFront,
				new Refractive(refractiveIndex, transmissionCoefficient, true),	// surfacePropertyFront
				new Refractive(refractiveIndex, transmissionCoefficient, true),	// surfacePropertyBack
				parent, 
				studio
		);
//
//		// constructor of superclass
//		super(description, parent, studio);
//		
//		// copy the parameters into this instance's variables
//		this.apertureRadius = apertureRadius;
//		this.radiusOfCurvatureFront = 2*focalLength*(refractiveIndex - 1);
//		this.radiusOfCurvatureBack = 2*focalLength*(refractiveIndex - 1);
//		this.centre = centre;
//		this.directionToFront = directionToFront;
//		SurfaceProperty surface = new Refractive(refractiveIndex, 1, true);
//		this.surfacePropertyFront = surface;
//		this.surfacePropertyBack = surface;
//
//		addElements();
	}

	/**
	 * Constructor that uses complex focal length and mod(refractive index) as parameters
	 *
	 * @param description
	 * @param apertureRadius
	 * @param focalLength
	 * @param modN
	 * @param centre
	 * @param directionToFront
	 * @param parent
	 * @param studio
	 */
	public EditableThickLens(
			String description,
			double apertureRadius,
			Complex focalLength,
			double modN,	// modulus
			Vector3D centre,
			Vector3D directionToFront,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, parent, studio);
		
		// copy the parameters into this instance's variables
		this.apertureRadius = apertureRadius;
		double cosPhi = Math.cos(-focalLength.getArg());
		double modNMinus1 = -cosPhi + Math.sqrt(cosPhi*cosPhi-1+modN*modN);
		Complex refractiveIndex = Complex.sum(
				Complex.fromPolar(modNMinus1, -focalLength.getArg()),
				new Complex(1,0)
			);
		this.radiusOfCurvatureFront = 2*focalLength.getMod()*modNMinus1;
		this.radiusOfCurvatureBack = 2*focalLength.getMod()*modNMinus1;
		System.out.println("lens \""+description+"\": n="+refractiveIndex+", R="+radiusOfCurvatureFront);
		this.centre = centre;
		this.unitVectorToFront = directionToFront;
		SurfaceProperty surface = new RefractiveComplex(refractiveIndex, 1, true);
		this.surfacePropertyFront = surface;
		this.surfacePropertyBack = surface;

		addElements();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableThickLens(EditableThickLens original)
	{
		super(original);
		
		// copy the original's parameters
		this.apertureRadius = original.getApertureRadius();
		this.radiusOfCurvatureFront = original.getRadiusOfCurvatureFront();
		this.radiusOfCurvatureBack = original.getRadiusOfCurvatureBack();
		this.centre = original.getCentre().clone();
		this.unitVectorToFront = original.getUnitVectorToFront().clone();
		this.surfacePropertyFront = original.getSurfacePropertyFront();
		this.surfacePropertyBack = original.getSurfacePropertyBack();
		
		addElements();
	}

	public EditableThickLens clone()
	{
		return new EditableThickLens(this);
	}
	
	public double getApertureRadius() {
		return apertureRadius;
	}

	public void setApertureRadius(double apertureRadius) {
		this.apertureRadius = apertureRadius;
	}

	public double getRadiusOfCurvatureFront() {
		return radiusOfCurvatureFront;
	}

	public void setRadiusOfCurvatureFront(double radiusOfCurvatureFront) {
		this.radiusOfCurvatureFront = radiusOfCurvatureFront;
	}

	public double getRadiusOfCurvatureBack() {
		return radiusOfCurvatureBack;
	}

	public void setRadiusOfCurvatureBack(double radiusOfCurvatureBack) {
		this.radiusOfCurvatureBack = radiusOfCurvatureBack;
	}

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getUnitVectorToFront() {
		return unitVectorToFront;
	}

	public void setUnitVectorToFront(Vector3D vectorToFront) {
		this.unitVectorToFront = vectorToFront.getNormalised();
	}

	public SurfaceProperty getSurfacePropertyFront()
	{
		return surfacePropertyFront;
	}
	
	public void setSurfacePropertyFront(SurfaceProperty surfacePropertyFront)
	{
		this.surfacePropertyFront = surfacePropertyFront;
		sphereFront.setSurfaceProperty(surfacePropertyFront);
	}

	public SurfaceProperty getSurfacePropertyBack()
	{
		return surfacePropertyBack;
	}
	
	public void setSurfacePropertyBack(SurfaceProperty surfacePropertyBack)
	{
		this.surfacePropertyBack = surfacePropertyBack;
		sphereBack.setSurfaceProperty(surfacePropertyBack);
	}

	private void addElements()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
						
		// the radius of curvature *must* be greater than the aperture radius for this to work
		if(Math.abs(radiusOfCurvatureFront) < apertureRadius)
		{
			// the radius of curvature is *less* than the aperture radius!
			throw new SceneException("Radius of curvature of front lens surface is less than aperture radius");
		}
		// the radius of curvature *must* be greater than the aperture radius for this to work
		if(Math.abs(radiusOfCurvatureBack) < apertureRadius)
		{
			// the radius of curvature is *less* than the aperture radius!
			throw new SceneException("Radius of curvature of back lens surface is less than aperture radius");
		}
		
		Vector3D cylinderAxisFront, cylinderAxisBack;
		
		// add the lens
		if(radiusOfCurvatureFront > 0)
		{
			// Start with a lens with positive focal length.
			// For a lens that consists of two spherical surfaces, both with radius of curvature <radiusOfCurvature>,
			// to have thickness 0 at a distance <apertureRadius> from the principal point, the spherical surfaces
			// have to be centred a distance +/- z from the principal point such that
			// 	radiusOfCurvature^2 = z^2 + apertureRadius^2,
			// and so
			// 	z = sqrt(radiusOfCurvature^2 - apertureRadius^2).
			double z = Math.sqrt(MyMath.square(radiusOfCurvature) - MyMath.square(apertureRadius)) - 0.5*thicknessAtThinnestPoint;
			Vector3D opticalAxisDirection = getNormalisedOpticalAxisDirection();
			Vector3D sphere1Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith( z));
			Vector3D sphere2Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-z));

			// add the two spherical surfaces
			addSceneObject(
					new EditableScaledParametrisedSphere(
					// new ParametrisedSphere(
							"Spherical surface 1",
							sphere1Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new EditableScaledParametrisedSphere(
					// new ParametrisedSphere(
							"Spherical surface 2",
							sphere2Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			System.out.println("TriangularThickLens::populateSceneObjectCollection: 2 spheres added at "
					+sphere1Centre+" and "+sphere2Centre);

		}
		else
		{
			// Now the case of a lens with a negative radius of curvature.
			// The centres of the spherical surfaces are now simply at +/-|radiusOfCurvature| + 0.5*thicknessAtThinnestPoint.
			double z = Math.abs(radiusOfCurvature) + 0.5*thicknessAtThinnestPoint;
			Vector3D opticalAxisDirection = getNormalisedOpticalAxisDirection();
			Vector3D sphere1Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith( z));
			Vector3D sphere2Centre = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(-z));

			// add the two spherical surfaces
			addSceneObject(
					new ParametrisedInvertedSphere(
							"Spherical surface 1",
							sphere1Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new ParametrisedInvertedSphere(
							"Spherical surface 2",
							sphere2Centre,	// centre of sphere
							radiusOfCurvature,
							surface,
							this,
							getStudio()
							)
					);
			addSceneObject(
					new ParametrisedPlane(
							"Plane selecting the appropriate part of surface 1",
							sphere1Centre,	// pointOnPlane
							opticalAxisDirection,	// normal 
							surface,	// surfaceProperty
							this,	// parent
							getStudio()	// studio
						)
				);
			addSceneObject(
					new ParametrisedPlane(
							"Plane selecting the appropriate part of surface 2",
							sphere2Centre,	// pointOnPlane
							opticalAxisDirection.getReverse(),	// normal 
							surface,	// surfaceProperty
							this,	// parent
							getStudio()	// studio
						)
				);
		}
		
		// add planes that form a triangular prism such that the lens lies inside it
		addSceneObject(
				getPlane(
						"Plane through corners 1 and 2",	// description
						corner1,	// point1OnPlane
						corner2,
						surface
						)
			);
		addSceneObject(
				getPlane(
						"Plane through corners 2 and 3",	// description
						corner2,	// point1OnPlane
						corner3,
						surface
						)
			);
		addSceneObject(
				getPlane(
						"Plane through corners 3 and 1",	// description
						corner3,	// point1OnPlane
						corner1,
						surface
						)
			);

//		sphereFront = new EditableScaledParametrisedSphere("front surface",
//			centre.getSumWith(
//				directionToFront.getWithLength(
//					-Math.sqrt(
//						radiusOfCurvatureFront*radiusOfCurvatureFront
//						-apertureRadius * apertureRadius
//					)
//				)
//			),	// centre of sphere
//			radiusOfCurvatureFront,
//			surfacePropertyFront,
//			this,
//			getStudio()
//		);
//		// make the direction to the front the direction to the pole
//		sphereFront.setDirections(directionToFront);
//		addSceneObject(sphereFront);
//		
//		sphereBack = new EditableScaledParametrisedSphere("back surface",
//			centre.getSumWith(
//				directionToFront.getWithLength(
//					Math.sqrt(
//						radiusOfCurvatureBack*radiusOfCurvatureBack
//						-apertureRadius * apertureRadius
//					)
//				)
//			),	// centre of sphere	
//			radiusOfCurvatureBack,
//			surfacePropertyBack,
//			this,
//			getStudio()
//		);
//		sphereBack.setDirections(directionToFront);
//		addSceneObject(sphereBack);
	}
	
	//
	// a few static methods
	//

	public enum RadiusOfCurvatureCalculationType {
		SYMMETRIC_ZERO_SEPARATION,
		SYMMETRIC_SERIES_IN_APERTURE_SIZE_2ND_ORDER
		// TODO add an option for calculating the radius of curvature numerically
	}
	
	public static double calculateFirstRadiusOfCurvature(
			double focalLength,
			double refractiveIndex,
			double apertureSize,
			RadiusOfCurvatureCalculationType radiusOfCurvatureCalculationType
		)
	{
		// The lensmaker's equation is [1]
		//   1/f = (n-1) [1/R1 + 1/R2 + (n-1)/n d/(R1 R2)],
		// where f is the focal length, n is the refractive index, R1 and R2 are the radii of curvature of the
		// two lens surfaces, and d is the separation between the surface centres.
		// References: [1] https://en.wikipedia.org/wiki/Lens_(optics)#Lensmaker.27s_equation
		switch(radiusOfCurvatureCalculationType)
		{
		case SYMMETRIC_SERIES_IN_APERTURE_SIZE_2ND_ORDER:
			// 
			if(focalLength > 0)
			{
				double alpha = apertureSize*apertureSize/(refractiveIndex*(refractiveIndex - 1));
				double p = -2./ alpha;
				double q = -1./(alpha*focalLength);
				double d = Math.pow(q/2.,2) + Math.pow(p/3.,3);
				double r = q/2.;
				double A = Math.sqrt(r*r - d);
				double phi = Math.atan2(Math.sqrt(-d), r);
				double x = Math.pow(A,1./3.) * (Math.sqrt(3)*Math.sin(phi/3.) - Math.cos(phi/3.));
				return (refractiveIndex-1)/x;
			}
			// if f<0, fall through to the zero-separation case below
		case SYMMETRIC_ZERO_SEPARATION:
		default:
			// with R1 = -R2 (symmtric case) and d=0 (zero separation), the lensmaker's equation becomes
			//   1/f = (n-1) 2 / R1,
			// and so
			//   R1 = 2 (n-1) f.
			return 2*(refractiveIndex-1)*focalLength;
		}
	}
	
	/**
	 * @param focalLength	focal length of the lens
	 * @param refractiveIndex	refractive index of lens material
	 * @param radiusOfCurvature1	signed radius of curvature of the first lens surface
	 * @param apertureRadius	radius of the lens's circular aperture
	 * @param thicknessAtThinnestPoint	thickness of the lens at its thinnest point
	 * @return	the (signed) radius of curvature of the second lens surface
	 */
	public static double calculateSecondRadiusOfCurvature(
			 double focalLength,
			 double refractiveIndex,
			 double radiusOfCurvature1,
			 double apertureRadius,
			 double thicknessAtThinnestPoint
		)
	{
		double n1 = refractiveIndex - 1;
		if(focalLength > 0)
		{
			// solution of the lensmaker's equation,
			//   1/f == (-1 + n)*(1/r1 - 1/r2 + (d*(-1 + n))/(n*r1*r2)),
			// and the equation for the separation of the centres of the surfaces of a lens with f>0,
			// radii of curvature r1 and r2, a circular aperture of radius a, and a thickness t at the
			// thinnest part of the lens,
			//   d == (r1 - Sqrt[r1^2 - a^2]) + (-r2 - Sqrt[r2^2 - a^2]) + t
			// This was solved for r2 in Mathematica (eliminating d), copied in C form, and then a few substitutions were being made.
			return 
				(
					-(Math.pow(focalLength,2)*Math.pow(n1,2)*(radiusOfCurvature1 + n1*(Math.sqrt(-Math.pow(apertureRadius,2) + Math.pow(radiusOfCurvature1,2)) - thicknessAtThinnestPoint))) + 
					focalLength*n1*refractiveIndex*radiusOfCurvature1*(
			    		radiusOfCurvature1 + n1*(Math.sqrt(-Math.pow(apertureRadius,2) + Math.pow(radiusOfCurvature1,2)) - thicknessAtThinnestPoint)
					) + 
					refractiveIndex*Math.sqrt(
							-((Math.pow(focalLength,2)*Math.pow(n1,4)*
			            (Math.pow(apertureRadius,2)*Math.pow(focalLength - focalLength*refractiveIndex + refractiveIndex*radiusOfCurvature1,2) - 
			              Math.pow(focalLength,2)*Math.pow(n1,2)*
			               ((2 + (refractiveIndex-2)*refractiveIndex)*Math.pow(radiusOfCurvature1,2) + 
			                 2*n1*radiusOfCurvature1*(Math.sqrt(-Math.pow(apertureRadius,2) + Math.pow(radiusOfCurvature1,2)) - thicknessAtThinnestPoint) - 
			                 Math.pow(n1,2)*(2*Math.sqrt(-Math.pow(apertureRadius,2) + Math.pow(radiusOfCurvature1,2)) - thicknessAtThinnestPoint)*thicknessAtThinnestPoint)))/
			          Math.pow(refractiveIndex,2))
					)
			    ) /
				(
					refractiveIndex*(focalLength*n1 - radiusOfCurvature1)*(focalLength*(refractiveIndex-2)*n1 + refractiveIndex*radiusOfCurvature1)
				);
		}
		else
		{
			// for f<0, this is much simpler, as the equation for the separation of the centres of the lens surfaces
			// becomes simply
			//   d = t,
			// where t is the thickness at the thinnest part of the lens
			return 
				-(
					(focalLength*n1*(refractiveIndex*(radiusOfCurvature1-thicknessAtThinnestPoint) + thicknessAtThinnestPoint)) /
					(refractiveIndex*(focalLength - focalLength*refractiveIndex + radiusOfCurvature1))
				);
		}
	}
	
	/**
	 * @param focalLength
	 * @param refractiveIndex
	 * @param radiusOfCurvature1
	 * @param radiusOfCurvature2
	 * @return	the separation between the centres of the two lens surfaces
	 */
	public static double calculateSurfaceCentreSeparation(
			double focalLength,
			double refractiveIndex,
			double radiusOfCurvature1,
			double radiusOfCurvature2
		)
	{
		// The lensmaker's equation is [1]
		//   1/f = (n-1) [1/R1 + 1/R2 + (n-1)/n d/(R1 R2)],
		// where f is the focal length, n is the refractive index, R1 and R2 are the radii of curvature of the
		// two lens surfaces, and d is the separation between the surface centres.
		// Solving for d (in Mathematica) gives
		//   d = (n (f (n-1) (r1 - r2) + r1 r2))/(f (n-1)^2).
		// References: [1] https://en.wikipedia.org/wiki/Lens_(optics)#Lensmaker.27s_equation
		return (refractiveIndex*(
					(refractiveIndex-1)*(radiusOfCurvature1-radiusOfCurvature2) +
					radiusOfCurvature1*radiusOfCurvature2 / focalLength
				)) /
				((refractiveIndex-1)*(refractiveIndex-1));
	}
	
	//
	// GUI stuff
	//

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens"));
        editPanel.setLayout(new MigLayout("insets 0"));
        
        // c.fill = GridBagConstraints.BOTH;
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		// the aperture radius

		apertureRadiusPanel = new LabelledDoublePanel("Aperture radius");
		editPanel.add(apertureRadiusPanel, "wrap");
		
		// the front radius of curvature

		radiusOfCurvatureFrontPanel = new LabelledDoublePanel("Radius of curvature (front)");
		editPanel.add(radiusOfCurvatureFrontPanel, "wrap");

		// the back radius of curvature

		radiusOfCurvatureBackPanel = new LabelledDoublePanel("Radius of curvature (back)");
		editPanel.add(radiusOfCurvatureBackPanel, "wrap");

		// the centre
		
		centrePanel = new LabelledVector3DPanel("Centre position");
		editPanel.add(centrePanel, "wrap");
		
		// direction to front
		
		directionToFrontPanel = new LabelledVector3DPanel("Direction to front");
		editPanel.add(directionToFrontPanel, "wrap");

		// front surface property
		
		surfacePropertyFrontPanel = new SurfacePropertyPanel("Front surface", getStudio().getScene());
		surfacePropertyFrontPanel.addButtonsActionListener(new SurfacePropertyPanelListener(surfacePropertyFrontPanel));
		// surfacePropertyFrontPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Front surface"));
		editPanel.add(surfacePropertyFrontPanel, "wrap");
		surfacePropertyFrontPanel.setIPanel(iPanel);

		// back surface property
		
		surfacePropertyBackPanel = new SurfacePropertyPanel("Back surface", getStudio().getScene());
		surfacePropertyBackPanel.addButtonsActionListener(new SurfacePropertyPanelListener(surfacePropertyBackPanel));
		// surfacePropertyBackPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Back surface"));
		editPanel.add(surfacePropertyBackPanel, "wrap");
		surfacePropertyBackPanel.setIPanel(iPanel);

		// the convert button
		
		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

		editPanel.validate();
	}
	
	@Override
	public void discardEditPanel()
	{
		editPanel = null;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#getEditPanel()
	 */
	@Override
	public JPanel getEditPanel()
	{
		return editPanel;
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		apertureRadiusPanel.setNumber(getApertureRadius());
		radiusOfCurvatureFrontPanel.setNumber(getRadiusOfCurvatureFront());
		radiusOfCurvatureBackPanel.setNumber(getRadiusOfCurvatureBack());
		centrePanel.setVector3D(getCentre());
		directionToFrontPanel.setVector3D(getDirectionToFront());
		surfacePropertyFrontPanel.setSurfaceProperty(getSurfacePropertyFront());
		surfacePropertyBackPanel.setSurfaceProperty(getSurfacePropertyBack());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableThickLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		sceneObjects.clear();

		setApertureRadius(apertureRadiusPanel.getNumber());
		setRadiusOfCurvatureFront(radiusOfCurvatureFrontPanel.getNumber());
		setRadiusOfCurvatureBack(radiusOfCurvatureBackPanel.getNumber());
		setCentre(centrePanel.getVector3D());
		setDirectionToFront(directionToFrontPanel.getVector3D());
		setSurfacePropertyFront(surfacePropertyFrontPanel.getSurfaceProperty());
		setSurfacePropertyBack(surfacePropertyBackPanel.getSurfaceProperty());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the necessary elements
		addElements();
		
		return this;
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
		if(edited instanceof SurfaceProperty)
		{
			if(beingEdited == surfacePropertyFrontPanel)
			{
				// front surface property has been edited
				setSurfacePropertyFront((SurfaceProperty)edited);
				surfacePropertyFrontPanel.setSurfaceProperty(getSurfacePropertyFront());
			}
			else if(beingEdited == surfacePropertyBackPanel)
			{
				// back surface property has been edited
				setSurfacePropertyBack((SurfaceProperty)edited);
				surfacePropertyBackPanel.setSurfaceProperty(getSurfacePropertyBack());
			}
		}
	}

	class SurfacePropertyPanelListener implements ActionListener
	{
		private SurfacePropertyPanel surfacePropertyPanel;
		
		public SurfacePropertyPanelListener(SurfacePropertyPanel surfacePropertyPanel)
		{
			this.surfacePropertyPanel = surfacePropertyPanel;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals(SurfacePropertyPanel.TILING_PARAMS_BUTTON_TEXT))
			{
				beingEdited = surfacePropertyPanel;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this, true);
		iPanel.replaceFrontComponent(container, "Edit ex-lens");
		container.setValuesInEditPanel();
	}
}
