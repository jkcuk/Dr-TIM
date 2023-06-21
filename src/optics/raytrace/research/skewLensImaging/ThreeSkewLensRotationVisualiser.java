package optics.raytrace.research.skewLensImaging;

import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.sceneObjects.AdamsIdealLensLookalike;
import optics.raytrace.sceneObjects.CylinderMantle;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectDifference;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersectionSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.LensHologram;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.research.skewLensImaging.TwoLensCombo.OpticalAxisSense;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.EditableOrthographicCameraTop;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeComboBox;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableFramedRectangle;
import optics.raytrace.GUI.sceneObjects.EditableFresnelLens;
import optics.raytrace.GUI.sceneObjects.EditableParametrisedCylinder;
import optics.raytrace.GUI.sceneObjects.EditableRayTrajectory;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;


/**
 * Simulation of a combination of three skew lenses with telescopic properties.
 * 
 * @author Gergely, Jakub
 */
public class ThreeSkewLensRotationVisualiser extends NonInteractiveTIMEngine
{
	// user-controlled parameters
	
	public enum SimulationType
	{
		NO_LENSES("Without lenses, camera in standard position"),
		LENSES("With lenses, camera in standard position"),
		LENSES_CAMERA_SHIFTED("With lenses, camera view direction rotated by 2 degrees"),
		LENSES_CAMERA_Z("With lenses, camera looking in z direction, lattice aligned is opt. axis of lens 3"),
		LENSES_CAMERA_AND_LATTICE_A3("With lenses, camera and lattice aligned with opt. axis of lens 3"),
		LENSES_CAMERA_AND_LATTICE_Z("With lenses, camera looking in z direction, lattice aligned with z axis"),
		CAMERA_ROTATION("Without lenses, camera position and direction rotated");

		private String description;

		private SimulationType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * show lenses, or don't show lenses but rotate camera so that lattice is rotated in the same way
	 */
	protected SimulationType simulationType;

	
	/**
	 * angle (in degrees) between the optical axis and the normal to the 1st lens
	 */
	protected double alpha1;

	/**
	 * angle (in degrees) between the optical axis and the normal to the 1st lens
	 */
	protected double alpha2;
	
	/**
	 * distance between principal points of first two lenses
	 */
	protected double distance;


	/**
	 * focal length of first lens
	 */
	// protected double f1;
	
	/**
	 * rotation angle, in degrees
	 */
	protected double rotationAngle;
	
	/**
	 * if true, shift the 3rd lens in its principal plane so that the combination becomes a Lorentz transformer (rotation + shear)
	 */
	protected boolean lorentzTransformer;
	
	/**
	 * focal length of 2nd lens
	 */
	// protected double f2;
	
//	/**
//	 * magnification of the telescope formed by the combination of lenses 1 and 2 and lens 3
//	 */
//	protected double telescopeMagnification;

	/**
	 * If true, shows lens 3, otherwise doesn't
	 */
	protected boolean showLens3;
	
	/**
	 * if true, lenses are circular, otherwise rectangular
	 */
	protected boolean circularLenses;

	public enum ViewType
	{
		STANDARD("Standard view"),
		TOP("Top view");

		private String description;

		private ViewType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * standard view or top view
	 */
	protected ViewType viewType;
	
	/**
	 * If true, show a number of light-ray trajectories
	 */
	protected boolean showTrajectories;
	
	/**
	 * If true, show the eye and a number of light-ray trajectories hitting the centre of the eye
	 */
	protected boolean showEyeAndEyeTrajectories;
	
	/**
	 * Allows selection of the simulated lens type
	 */
	protected LensType lensType;
	
	// lattice
	
	protected double xMin, xMax, yMin, yMax, zMin, zMax;
	protected int nX, nY,  nZ;

	protected double zLength, zCentre;

	protected Vector3D latticeCentre;
	
	protected boolean rotateLattice;


	/**
	 * Constructor.
	 * Sets all user-controlled parameters to half-sensible initial values.
	 */
	public ThreeSkewLensRotationVisualiser()
	{
		// set all standard parameters, including those for the camera
		super();
		
		// set all parameters
		simulationType = SimulationType.LENSES;
		alpha1 = 5;	// -1.*MyMath.rad2deg(Math.atan2(2, 0.75)); //20;	// degrees
		alpha2 = -5;	// -1.*MyMath.rad2deg(Math.atan2(1.2, 0.75)); //-20.;	// degrees
		distance = 1; //separation of the first two lenses
		rotationAngle = -30;
		lorentzTransformer = false;
// 		f1 = 0.2;	// -0.2808987532707136;//0.2; //focal length of first lens
		// f2 = 0.2;	// 0.33919932160203536;//0.2; //focal length of second lens
//		telescopeMagnification = 1.;
		
		showLens3 = true; //this is vestigial, the quickest way of modifying ThreeSkewLensTelescope was to leave showLens3 variables in and just have them = true always
		viewType = ViewType.STANDARD;
		showTrajectories = true;
		showEyeAndEyeTrajectories = false;
		lensType = LensType.IDEAL_THIN_LENS;
		circularLenses = true;
		
		xMin = -4;
		xMax = 1;
		nX = 10;
		yMin = -1;
		yMax = 4;
		nY = 10;
		zMin = 5;
		zMax = 10;
		nZ = 10;
		latticeCentre = new Vector3D(0,0,0);
		rotateLattice = false;
		
		renderQuality = RenderQualityEnum.DRAFT;
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraFocussingDistance = 13.5;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraHorizontalFOVDeg = 20;
		
		zLength= 8;
		zCentre = distance;
		
		if(nonInteractiveTIMAction == NonInteractiveTIMActionEnum.INTERACTIVE)
		{
			windowTitle = "Dr TIM's 3-skew-lens-rotation visualiser";
			windowWidth = 1400;
			windowHeight = 650;
		}
	}

	@Override
	public String getClassName()
	{
		return "ThreeSkewLensRotationVisualiser"	// the name
				;
	}
	
	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine
		printStream.println("Scene initialisation");
		printStream.println();
		
		printStream.println("simulationType = "+simulationType);
		printStream.println("alpha1 = "+alpha1);
		printStream.println("alpha2 = "+alpha2);
		printStream.println("distance = "+distance);
		printStream.println("rotationAngle = "+rotationAngle);
		printStream.println("lorentzTransformer = "+lorentzTransformer);
		printStream.println("lensType = "+lensType);
		printStream.println("showTrajectories = "+showTrajectories);
		printStream.println("showEyeAndEyeTrajectories = "+showEyeAndEyeTrajectories);
		printStream.println("showLens3 = "+showLens3);
		printStream.println("circularLenses = "+circularLenses);
		
		//  lattice
		printStream.println("xMin = "+xMin);
		printStream.println("xMax = "+xMax);
		printStream.println("nX = "+nX);
		printStream.println("yMin = "+yMin);
		printStream.println("yMax = "+yMax);
		printStream.println("nY = "+nY);
		printStream.println("zMin = "+zMin);
		printStream.println("zMax = "+zMax);
		printStream.println("nZ = "+nZ);
		printStream.println("latticeCentre = "+latticeCentre);
		printStream.println("rotateLattice = "+rotateLattice);
		
		// top view
		printStream.println("viewType = "+viewType);
		printStream.println("zLength = "+zLength);
		printStream.println("zCentre = "+zCentre);
		
		printStream.println("cameraHorizontalFOVDeg = "+cameraHorizontalFOVDeg);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);		
	}

	private void addTrajectory(
			String name,
			Vector3D startPoint,
			double initialAngle,	// in radians
			DoubleColour colour,
			double radius,
			int maxTraceLevel,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		//Vector3D direction = new Vector3D(Math.sin(initialAngle), 0, Math.cos(initialAngle));
		Vector3D direction = Vector3D.sum( Vector3D.crossProduct(Vector3D.Y, cameraViewDirection).getProductWith(Math.sin(initialAngle)), cameraViewDirection.getProductWith(Math.cos(initialAngle))); // 
		
		SurfaceColourLightSourceIndependent c = new SurfaceColourLightSourceIndependent(colour, true);
		
		// a ray trajectory in the positive direction...
		scene.addSceneObject(new EditableRayTrajectory(
				name,
				startPoint,	// start point
				0,	// start time
				direction,	// initial direction
				radius,	// radius
				c,
				maxTraceLevel,	// max trace level
				false,	// reportToConsole
				scene,
				studio
				)
				);
	}
	
	
//	/**
//	 * for compatibility with Mathematica...
//	 * @param x
//	 * @return
//	 */
//	private double csc(double x)
//	{
//		return 1./Math.sin(x);
//	}
//	
//	/**
//	 * for compatibility with Mathematica...
//	 * @param x
//	 * @return
//	 */
//	private double sec(double x)
//	{
//		return 1./Math.cos(x);
//	}
//
//	/**
//	 * for compatibility with Mathematica...
//	 * @param x
//	 * @return
//	 */
//	private double cot(double x)
//	{
//		return 1./Math.tan(x);
//	}

	@Override
	public void populateStudio()
	throws SceneException
	{
		// internal variables
		
		double g1;
		double g2;
		double k_o;
		double k_i;
		double twoLensEffectiveF;
		
		Vector3D xVector, yVector, zVector;

		// super.populateSimpleStudio();
		
		// re-calculate values of internal variables
		double beta1 = MyMath.deg2rad(alpha1);	// -1.*Math.atan2(2, 0.75);
		double beta2 = MyMath.deg2rad(alpha2);	// -1.*Math.atan2(1.2, 0.75);
		double rotationAngleRad  = MyMath.deg2rad(rotationAngle);
		
		double f1 = 0.5*distance*(Math.cos(beta1) + Math.sin(beta1)/Math.tan(0.5*rotationAngleRad));

		double f2 = 
//				(Math.cos(beta2)*(3*distance*csc(beta2) - 2*f1*csc(beta2)*sec(beta1) + 
//			       distance*cot(beta1)*sec(beta2) - 2*f1*csc(beta1)*sec(beta2) + // TODO the sign in front of the sqrt might have to change for some parameters
//			       Math.sqrt(-8*csc(beta2)*(distance*distance*cot(beta1) + distance*distance*cot(beta2) - 
//			            3*distance*f1*csc(beta1) - distance*f1*cot(beta2)*sec(beta1) + 
//			            2*f1*f1*csc(beta1)*sec(beta1))*sec(beta2) + 
//			         MyMath.square(-3*distance*csc(beta2) + 2*f1*csc(beta2)*sec(beta1) - 
//			           distance*cot(beta1)*sec(beta2) + 2*f1*csc(beta1)*sec(beta2))))*Math.sin(beta2))/4.;
				0.5*Math.sin(beta2)*(distance/Math.tan(beta1)-2*f1/Math.sin(beta1))+0.5*distance*Math.cos(beta2);
//				1/4*Math.cos(beta2)*(3*distance/Math.sin(beta2) - 2*f1/(Math.sin(beta2)*Math.cos(beta1)) + 
//				   distance/(Math.tan(beta1)*Math.sin(beta2)) - 
//				   2*f1/(Math.sin(beta1)*Math.cos(beta2)) +
//				Math.sqrt(
//						-8/Math.sin(beta2)*(
//								distance*distance/Math.tan(beta1) + distance*distance/Math.tan(beta2) - 
//								3*distance*f1/Math.sin(beta1) - distance*f1/(Math.tan(beta2)*Math.cos(beta1)) + 
//								2*f1*f1/(Math.sin(beta1)*Math.cos(beta1))
//							)/Math.cos(beta2) +
//				         MyMath.square(
//				        		 -3*distance/Math.sin(beta2) + 2*f1/(Math.sin(beta2)*Math.cos(beta1)) - 
//				        		 distance/(Math.tan(beta1)*Math.cos(beta2)) + 
//				        		 2*f1/(Math.sin(beta1)*Math.cos(beta2))
//				        )
//				       )
//						)*Math.sin(beta2);
		
		System.out.println("ThreeSkewLensRotation::populateStudio: f1="+f1);
		System.out.println("ThreeSkewLensRotation::populateStudio: f2="+f2);
		Vector3D p1 = new Vector3D(0, 0, 0); //principal point of first lens
		Vector3D p2 = new Vector3D(0, 0, distance); //principal point of second lens
		Vector3D n1 = new Vector3D(Math.sin(beta1), 0, Math.cos(beta1)); //outwards normal of first lens
		Vector3D n2 = new Vector3D(Math.sin(beta2), 0, Math.cos(beta2)); //outwards normal of second lens

//		xVector = Vector3D.X;
//		yVector = Vector3D.Y;
//		zVector = Vector3D.Z;
//		gfd
		
		g1 = f1/(Math.cos(beta1));
		g2 = f2/(Math.cos(beta2));
		k_o = -1./(Math.tan(beta1))*(distance- g1 - g2)/(distance- g2 - g1*((Math.tan(beta2))/(Math.tan(beta1))));
			// -1./(Math.tan(beta1))*(distance- g1 - g2)/(distance- g1 - g2*((Math.tan(beta1))/(Math.tan(beta2))));
		k_i = -1./(Math.tan(beta2))*(distance- g1 - g2)/(distance- g1 - g2*((Math.tan(beta1))/(Math.tan(beta2))));
		// -1./(Math.tan(beta2))*(distance- g1 - g2)/(distance- g2 - g1*((Math.tan(beta2))/(Math.tan(beta1))));
		twoLensEffectiveF = (g1*g2)/(g1 + g2 - distance);


		studio = new Studio();

		double frameWidth = 0.05;
		
		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// the standard scene objects
		// scene.addSceneObject(SceneObjectClass.getHeavenSphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getLighterChequerboardFloor(-1-frameWidth, scene, studio));
				
		// add any other scene objects
		TwoLensCombo lenses12 = new TwoLensCombo(p1, p2, n1, n2, f1, f2, OpticalAxisSense.P1_TO_P2);

		double lensDiameter = 2.;
		Vector3D lensPlaneIntersectionDirection = lenses12.getDirectionOfLensIntersectionLine();
		Vector3D lensPlaneIntersectionPoint = lenses12.getPointOnPrincipalPlanes(p1);
		
		System.out.println("ThreeSkewLensRotation::populateStudio: intersection point of lens planes: "+lensPlaneIntersectionPoint);
		System.out.println("ThreeSkewLensRotation::populateStudio: intersection direction of lens planes: "+lensPlaneIntersectionDirection);
		
		Vector3D commonCorner = Vector3D.sum(lensPlaneIntersectionPoint, lensPlaneIntersectionDirection.getWithLength(-0.5*lensDiameter));
		
		// calculate the parameters of the third lens
		
		// first calculate the normal to lens 3
		//Vector3D n3 = lenses12.getNormalToTransversePlanes2().getReverse();\Vector3D n3 
		Vector3D n3 =
			new Vector3D( Math.sin(0.5*rotationAngleRad), 0, Math.cos(0.5*rotationAngleRad)).getNormalised();
			// new Vector3D( 1/(Math.sqrt(1 + k_i*k_i)), 0,-1.*k_i/(Math.sqrt(1 + k_i*k_i))).getNormalised();
			// new Vector3D( 1/(Math.sqrt(1 + k_o*k_o)), 0,-1.*k_o/(Math.sqrt(1 + k_o*k_o))).getNormalised();
		//Vector3D n3 = new Vector3D( 1/(Math.sqrt(1 + k_i*k_i)), 0,-1.*k_i/(Math.sqrt(1 + k_i*k_i)));
		// System.out.println("ThreeSkewLensRotation::populateStudio: n3 = "+n3);
		
		yVector = Vector3D.Y;
		switch(simulationType)
		{
		case LENSES_CAMERA_AND_LATTICE_Z:
			zVector = Vector3D.Z; //n3.getReverse();
			break;
		default:
			zVector = n3; //n3.getReverse();
		}
		xVector = Vector3D.crossProduct(yVector, zVector);
				
		// then calculate the remaining parameters, which depends on what exactly we want to do;
		// in all cases, we want the object-sided focal plane of the lens to coincide with the image-sided focal plane of the
		// combination of lenses 1 and 2
		
//		boolean lens3InPrincipalPlane = false;
		double f3 = -1.*twoLensEffectiveF*Math.cos(0.5*rotationAngleRad);
				// -1.*twoLensEffectiveF*k_i/(Math.sqrt(1 + k_i*k_i));
		double px, pz;
		if(lorentzTransformer)
		{
			// rotation + shearing
			px = twoLensEffectiveF*Math.tan(rotationAngleRad);
			pz = -px*Math.tan(0.5*rotationAngleRad) + distance*(1-twoLensEffectiveF/g1);
		}
		else
		{
			// rotation only
			px = -twoLensEffectiveF*(1/(k_o + 1/k_o) - 1/(k_i + 1/k_i)); ///////!!!!!minus
			pz =
				// px/k_o + distance*twoLensEffectiveF/g2;
				px/k_i + distance*(1-twoLensEffectiveF/g1);
		}
		Vector3D p3 = new Vector3D(px, 0,  pz);
		System.out.println("ThreeSkewLensRotation::populateStudio: p1 = "+p1);
		System.out.println("ThreeSkewLensRotation::populateStudio: p2 = "+p2);
		System.out.println("ThreeSkewLensRotation::populateStudio: p3 = "+p3);
		System.out.println("ThreeSkewLensRotation::populateStudio: n3 = "+n3);
		System.out.println("ThreeSkewLensRotation::populateStudio: k_i = "+k_i);
		System.out.println("ThreeSkewLensRotation::populateStudio: k_o = "+k_o);
		
		// double rotationAngle =  2*Math.atan(1/k_o);
		// System.out.println("ThreeSkewLensRotation::populateStudio: rotation angle ="+MyMath.rad2deg(rotationAngle)+"°");
		
		NumberFormat nf = NumberFormat.getInstance();
		infoTextField.setText(
				"f_1 = "+nf.format(f1)
				+ ", f_2 = "+nf.format(f2)
//				+", n_3 = "+n3
//				+", p_3 = "+p3
				+", f_3 = "+nf.format(f3)
			);
		/*
		if(lens3InPrincipalPlane)
		{
			// calculate the focal length, ...
			f3 = Vector3D.scalarProduct(
					Vector3D.difference(lenses12.getPointOnFocalPlane2(), lenses12.getPointOnPrincipalPlanes()),
					n3
					);

			// ... and its principal point
			p3 = Geometry.linePlaneIntersection(
					p1,	// pointOnLine
					lenses12.getOpticalAxisDirection(),	// directionOfLine
					lenses12.getPointOnPrincipalPlanes(),	// pointOnPlane
					n3	// normalToPlane
				);
		}
		else
		{
			f3 = -lenses12.getFocalLength()/telescopeMagnification;
			p3 = Geometry.linePlaneIntersection(
					p1,	// pointOnLine
					lenses12.getOpticalAxisDirection(),	// directionOfLine
					Vector3D.sum(
							lenses12.getPointOnFocalPlane2(),
							n3.getWithLength(f3)
						),	// pointOnPlane
					n3	// normalToPlane
					);
		}*/
		// System.out.println("focal length of 3rd lens: "+f3);

		Vector3D standardCameraViewDirection = new Vector3D(1,0,-k_o);

		switch(simulationType)
		{
		case LENSES:
		case LENSES_CAMERA_SHIFTED:
		case LENSES_CAMERA_AND_LATTICE_Z:
		case LENSES_CAMERA_AND_LATTICE_A3:
		case LENSES_CAMERA_Z:
			// boolean circularLenses = true;
			if(circularLenses)
			{
				// we want the phase hologram of lens 1 to work like an ideal thin lens for rays through the eye position;
				// the eye position should therefore be imaged to <eyePositionImage1>
				cameraViewDirection = standardCameraViewDirection; // to ensure that getStandardCameraPosition() is, in fact, the standard camera position
				Vector3D eyePositionImage1 = IdealThinLensSurface.getImagePosition(
						f1,	// focalLength
						p1,	// principalPoint
						n1,	// opticalAxisDirectionPos
						getStandardCameraPosition(),	// objectPosition
						ImagingDirection.NEG2POS	// direction
						);
				// the first lens
				addFramedCircularLens(
						"lens 1",	// description
						p1,	// principalPoint
						n1,	// normal
						f1,	// focalLength
						getStandardCameraPosition(),	// insideSpacePoint
						eyePositionImage1,	// outsideSpacePoint
						lensType,
						0.5*lensDiameter,	// radius
						frameWidth,
						SurfaceColour.RED_SHINY,	// frameSurfaceProperty
						scene, studio
						);

				// we want the phase hologram of lens 2 to work like an ideal thin lens for rays through the <eyePositionImage1>;
				// the eye position should therefore be imaged to <eyePositionImage2>
				Vector3D eyePositionImage2 = IdealThinLensSurface.getImagePosition(
						f2,	// focalLength
						p2,	// principalPoint
						n2,	// opticalAxisDirectionPos
						eyePositionImage1,	// objectPosition
						ImagingDirection.NEG2POS	// direction
						);
				addFramedCircularLens(
						"lens 2",	// description
						p2,	// principalPoint
						n2,	// normal
						f2,	// focalLength
						eyePositionImage1,	// insideSpacePoint
						eyePositionImage2,	// outsideSpacePoint
						lensType,
						0.5*lensDiameter,	// radius  
						frameWidth,
						SurfaceColour.GREEN_SHINY,	// frameSurfaceProperty
						scene, studio
						);

				if(showLens3)
				{
					// we want the phase hologram of lens 3 to work like an ideal thin lens for rays through the <eyePositionImage2>;
					// the eye position should therefore be imaged to <eyePositionImage3>
					Vector3D eyePositionImage3 = IdealThinLensSurface.getImagePosition(
							f3,	// focalLength
							p3,	// principalPoint
							n3,	// opticalAxisDirectionPos
							eyePositionImage2,	// objectPosition
							ImagingDirection.NEG2POS	// direction
							);
					addFramedCircularLens(
							"lens 3",	// description
							p3,	// principalPoint
							n3,	// normal
							f3,	// focalLength
							eyePositionImage2,	// insideSpacePoint
							eyePositionImage3,	// outsideSpacePoint
							lensType,
							0.5*lensDiameter,	// radius
							frameWidth,
							SurfaceColour.BLUE_SHINY,	// frameSurfaceProperty
							scene, studio
							);
				}
			}
			else
			{
				// rectangular lenses
				System.out.println("Rectangular lenses");
				// the first lens
				EditableFramedRectangle lens1 = new EditableFramedRectangle(
						"lens 1",	// description
						commonCorner,	// corner
						lensPlaneIntersectionDirection.getWithLength(lensDiameter),	// widthVector
						Vector3D.crossProduct(n1, lensPlaneIntersectionDirection).getWithLength(lensDiameter),	// heightVector
						0.5*frameWidth,	// 0.01*lensDiameter,	// frameRadius
						new IdealThinLensSurfaceSimple(
								p1,	// lensCentre
								n1,	// opticalAxisDirection
								f1,	// focalLength
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								true	// shadowThrowing
								),	// windowSurfaceProperty
						SurfaceColour.RED_MATT,	// frameSurfaceProperty
						true,	// showFrames
						scene,	// parent 
						studio
						);
				scene.addSceneObject(lens1);

				// the second lens
				EditableFramedRectangle lens2 = new EditableFramedRectangle(
						"lens 2",	// description
						commonCorner,	// corner
						lensPlaneIntersectionDirection.getWithLength(lensDiameter),	// widthVector
						Vector3D.crossProduct(n2, lensPlaneIntersectionDirection).getWithLength(lensDiameter),	// heightVector
						0.49*frameWidth,	// 0.009*lensDiameter,	// frameRadius
						new IdealThinLensSurfaceSimple(
								p2,	// lensCentre
								n2,	// opticalAxisDirection
								f2,	// focalLength
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								true	// shadowThrowing
								),	// windowSurfaceProperty
						SurfaceColour.YELLOW_MATT,	// frameSurfaceProperty
						true,	// showFrames
						scene,	// parent 
						studio
						);
				scene.addSceneObject(lens2);


				EditableFramedRectangle lens3 = new EditableFramedRectangle(
						"lens 3",	// description
						Geometry.getPointOnPlaneClosestToPoint(
								p3,	// pointOnPlane
								n3,	// planeNormal
								commonCorner	// point
								),	// corner
						lensPlaneIntersectionDirection.getWithLength(lensDiameter),	// widthVector
						Vector3D.crossProduct(n3, lensPlaneIntersectionDirection).getWithLength(-lensDiameter),	// heightVector
						0.48*frameWidth,	// 0.008*lensDiameter,	// frameRadius
						new IdealThinLensSurfaceSimple(
								p3,	// lensCentre
								n3,	// opticalAxisDirection
								f3,	// focalLength
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								true	// shadowThrowing
								),	// windowSurfaceProperty
						SurfaceColour.GREEN_MATT,	// frameSurfaceProperty
						true,	// showFrames
						scene,	// parent 
						studio
						);
				if(showLens3) scene.addSceneObject(lens3);
			}
			break;
		case NO_LENSES:
		case CAMERA_ROTATION:
		default:
			// for  NO_LENSES, simply don't add any lenses
			// for CAMERA_ROTATION, also don't add any lenses and do the camera rotation  later
		}
		
		if((viewType==ViewType.TOP) && showTrajectories)
		{
			for(double x = -0.5; x<0.5; x+=0.125)
			{
			// a few light-ray trajectories
			addTrajectory(
					"trajectory",	// name
					Vector3D.sum(Vector3D.crossProduct(Vector3D.Y, cameraViewDirection).getProductWith(x), cameraViewDirection.getProductWith(-2)),//new Vector3D(x, 0, -2),	// startPoint
					0,	// initialAngle, in radians
					DoubleColour.RED,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);

			addTrajectory(
					"trajectory",	// name
					Vector3D.sum(Vector3D.crossProduct(Vector3D.Y, cameraViewDirection).getProductWith(x), cameraViewDirection.getProductWith(-2)),//new Vector3D(x, 0, -2),	// startPoint
					0.1,	// initialAngle, in radians
					DoubleColour.ORANGE,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);

			addTrajectory(
					"trajectory",	// name
					Vector3D.sum(Vector3D.crossProduct(Vector3D.Y, cameraViewDirection).getProductWith(x), cameraViewDirection.getProductWith(-2)),//new Vector3D(x, 0, -2),	// startPoint
					-0.1,	// initialAngle, in radians
					DoubleColour.VIOLET,	// colour
					0.01,	// radius
					100,	// maxTraceLevel
					scene,
					studio
				);
			}
		}

		if((viewType==ViewType.TOP) && showEyeAndEyeTrajectories)
		{
			// a few light-ray trajectories
			for(double initialAngle = -0.1; initialAngle <= 0.1; initialAngle += 0.0125)
			{
				addTrajectory(
						"trajectory",	// name
						getStandardCameraPosition(),	// startPoint
						initialAngle,	// initialAngle, in radians
						DoubleColour.YELLOW,	// colour
						0.01,	// radius
						100,	// maxTraceLevel
						scene,
						studio
						);
			}
		}

		// something to look at
		
		// the cylinder lattice
		
		if(rotateLattice)
		{
			// rotation axis is given by intersection point of lens planes, lensPlaneIntersectionPoint,
			// and intersection direction of lens planes, lensPlaneIntersectionDirection
			
			// TODO what is the rotation angle?
			
			// TODO rotate xVector, yVector, zVector, latticeCentre; perhaps use LinearTransformation class?
		}
		EditableCylinderLattice cylinderLattice = new EditableCylinderLattice(
				"cylinder lattice",	// description
				xMin, xMax, nX, xVector,	// xMin, xMax, nX, xVector
				yMin, yMax, nY, yVector,	// yMin, yMax, nY, yVector
				zMin, zMax, nZ, zVector,	// zMin, zMax, nZ, zVector
				0.02,	// radius
				latticeCentre, //centre
				scene,	// parent
				studio
		);
		scene.addSceneObject(cylinderLattice);

		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		
		if(viewType==ViewType.TOP)
		{
			studio.setCamera(new EditableOrthographicCameraTop(
					"Ceiling view",
					p2.x,	// xCentre
					(showEyeAndEyeTrajectories?(p3.z + getStandardCameraPosition().z)/2:zCentre),	// zCentre
					(showEyeAndEyeTrajectories?(p3.z - getStandardCameraPosition().z)*1.2:zLength),	// zLength
					cameraPixelsX, cameraPixelsY,	// logical number of pixels
					100,	// maxTraceLevel
					renderQuality.getAntiAliasingQuality()	// anti-aliasing quality
			));
//			camera = new EditableOrthographicCamera(
//					"Camera",	// name
//					new Vector3D(0, -1, 0),	// viewDirection
//					new Vector3D(0, 1000, 0),	// CCDCentre
//					new Vector3D(0, 0, width),	// horizontalSpanVector3D
//					new Vector3D(width*pixelsY/pixelsX, 0, 0),	// verticalSpanVector3D
//					pixelsX,	// imagePixelsHorizontal
//					pixelsY,	// imagePixelsVertical
//					100,	// maxTraceLevel
//					(test?QualityType.NORMAL:QualityType.GOOD)	// anti-aliasing quality
//					);
		}
		else
		{
			// Vector3D standardCameraViewDirection = new Vector3D(1,0,-k_o);

			switch(simulationType)
			{
			case CAMERA_ROTATION:
//				Vector3D lensPlaneIntersectionDirection = lenses12.getDirectionOfLensIntersectionLine();
//				Vector3D lensPlaneIntersectionPoint = lenses12.getPointOnPrincipalPlanes(p1);
//				
//				System.out.println("ThreeSkewLensRotation::populateStudio: intersection point of lens planes: "+lensPlaneIntersectionPoint);
//				System.out.println("ThreeSkewLensRotation::populateStudio: intersection direction of lens planes: "+lensPlaneIntersectionDirection);

				// rotationAngle
				
				cameraViewDirection = standardCameraViewDirection;
				Vector3D standardCameraPosition = getStandardCameraPosition();
				
				// get the rotated view direction...
				cameraViewDirection = Geometry.rotate(standardCameraViewDirection, lensPlaneIntersectionDirection, -rotationAngleRad);
				// ... and position
				Vector3D cameraPosition = Vector3D.sum(
						Geometry.rotate(
								Vector3D.difference(standardCameraPosition, lensPlaneIntersectionPoint),
								lensPlaneIntersectionDirection,
								-rotationAngleRad
							),
						lensPlaneIntersectionPoint
					);
				
				Vector3D cameraViewCentreSave = cameraViewCentre;
				cameraViewCentre = Vector3D.sum(cameraPosition, cameraViewDirection.getWithLength(cameraDistance));
				studio.setCamera(getStandardCamera());
				cameraViewCentre = cameraViewCentreSave;
				break;
			case LENSES_CAMERA_SHIFTED:
				cameraViewDirection = Geometry.rotate(standardCameraViewDirection, lensPlaneIntersectionDirection, MyMath.deg2rad(2));
				studio.setCamera(getStandardCamera());
				break;
			case LENSES_CAMERA_AND_LATTICE_Z:
				cameraViewDirection = Vector3D.Z;
				studio.setCamera(getStandardCamera());
				break;
			case LENSES_CAMERA_AND_LATTICE_A3:
				cameraViewDirection = n3;
				studio.setCamera(getStandardCamera());
				break;
			default:
				cameraViewDirection = standardCameraViewDirection;
				studio.setCamera(getStandardCamera());
			}
		}
	}
	
	/**
	 * Adds a framed circular lens to <scene>.
	 * The surface property can be set in different ways, depending on <lensType>.
	 * If <lensType> is IDEAL_THIN_LENS or LENS_HOLOGRAM, it uses <normal>, <principalPoint> and <focalLength> to calculate the surface parameters.
	 * If <lensType> is LENS_HOLOGRAM_EYE, it uses <insideSpacePoint> and <outsideSpacePoint> to optimise the lens hologram (which is chosen
	 * such that it stigmatically images <insideSpacePoint> into <outsideSpacePoint>).
	 * @param description
	 * @param principalPoint
	 * @param normal
	 * @param focalLength
	 * @param insideSpacePoint
	 * @param outsideSpacePoint
	 * @param lensType
	 * @param radius
	 * @param frameWidth
	 * @param frameSurfaceProperty
	 * @param scene
	 * @param studio
	 */
	public static void addFramedCircularLens(
			String description,
			Vector3D principalPoint,
			Vector3D normal,
			double focalLength,
			Vector3D insideSpacePoint,
			Vector3D outsideSpacePoint,
			LensType lensType,
			double radius,
			double frameWidth,
			SurfaceProperty frameSurfaceProperty,
			SceneObjectContainer scene,
			Studio studio
		)
	{
		if(lensType == LensType.FRESNEL_LENS)
		{
			SceneObjectIntersectionSimple lens = new SceneObjectIntersectionSimple(
					description,
					scene,
					studio
				);

			double thickness = 0.05*radius;

			EditableFresnelLens lensNoAperture = new EditableFresnelLens(
					description + "(lens)",
					principalPoint,	// lensCentre
					normal,	// forwardsCentralPlaneNormal
					outsideSpacePoint,	// frontConjugatePoint
					insideSpacePoint,	// backConjugatePoint
					1.5,	// refractiveIndex
					thickness,	// thickness
					0.01*radius,	// minimumSurfaceSeparation,
					60,	// numberOfLenssections
					true,	// makeStepSurfacesBlack
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					lens,	// parent
					studio
				);
			lens.addSceneObject(lensNoAperture);
			EditableParametrisedCylinder cylinderThatDefinesAperture = new EditableParametrisedCylinder(
					description + " (aperture)",	// description
					Vector3D.sum(principalPoint, normal.getWithLength( thickness)),	// startPoint
					Vector3D.sum(principalPoint, normal.getWithLength(-thickness)),	// endPoint
					radius,	// radius
					Transparent.PERFECT,
					lens,
					studio
			);
			lens.addSceneObject(cylinderThatDefinesAperture);
			scene.addSceneObject(lens);
		}
		else
		{
			SurfaceProperty surface;
			switch(lensType)
			{
			case LENS_HOLOGRAM:
				surface = new LensHologram(
						normal,	// opticalAxisDirectionOutwards
						principalPoint,
						focalLength,
						SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
						true	// shadowThrowing
						);
				break;
			case LENS_HOLOGRAM_EYE:
				surface = new Point2PointImagingPhaseHologram(
						insideSpacePoint,
						outsideSpacePoint,
						SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
						false,	// reflective
						true	// shadowThrowing
						);
				break;
			case ITL_LOOKALIKE:
				surface = new Transparent(); //TODO
				//add the negative cylinder to shape the lenses
				ArrayList<SceneObjectPrimitive> positiveSceneObjects = new ArrayList<SceneObjectPrimitive>();
				CylinderMantle apertureShape = new CylinderMantle(
						" shape",	// description
						Vector3D.sum(principalPoint, normal.getWithLength( 1)),	// startPoint
						Vector3D.sum(principalPoint, normal.getWithLength(-1)),	// endPoint
						radius/5,	// radius
						null,
						null, null);
				positiveSceneObjects.add(apertureShape);
				
				//surface property
				double n=15;//refractive index TODO change as needed...
				SurfaceProperty surfaceProperty1, surfaceProperty2;	// , surfacePropertySides;
				surfaceProperty1 = surfaceProperty2 = new RefractiveSimple(
						1/n,	// insideOutsideRefractiveIndexRatio
						SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
						true	// shadowThrowing
					);
				
				scene.addSceneObject(new AdamsIdealLensLookalike(
						"Ideal Lens Lookalike",// description,
						insideSpacePoint,// p,
						outsideSpacePoint,// q,
						0.5,// dp,
						-0.2,// dq,
						principalPoint,// pI,
						normal,// idealLensNormal,
						radius,// height,
						radius,// width,
						20,// iSteps,
						20,// jSteps,
						0.05,// integrationStepSize,
						positiveSceneObjects,//positiveSceneObjects
						n,// n, 
						surfaceProperty1,//SurfaceProperty
						surfaceProperty2, //SurfaceProperty
						null, //SurfaceProperty
						scene,// parent,
						studio// studio
						));
				
				break;
			case IDEAL_THIN_LENS:
			default:
				surface = new IdealThinLensSurfaceSimple(
						principalPoint,	// lensCentre
						normal,	// opticalAxisDirection
						focalLength,
						SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
						true	// shadowThrowing
						);
			}
			EditableScaledParametrisedDisc lens = new EditableScaledParametrisedDisc(
					description,
					principalPoint,	// centre
					normal,	// normal
					radius,	// radius
					surface,	// surfaceProperty
					scene, studio
					);
			scene.addSceneObject(lens);
			System.out.println("insideSpacePoint "+insideSpacePoint+", principalPoint "+principalPoint+", outsideSpacePoint"+outsideSpacePoint);
		}
		
		EditableParametrisedCylinder frameOutside = new EditableParametrisedCylinder(
				description + " (frame outside)",	// description
				Vector3D.sum(principalPoint, normal.getWithLength( 0.5*frameWidth)),	// startPoint
				Vector3D.sum(principalPoint, normal.getWithLength(-0.5*frameWidth)),	// endPoint
				radius + frameWidth,	// radius
				frameSurfaceProperty,
				scene, studio
		);
		EditableParametrisedCylinder frameInside = new EditableParametrisedCylinder(
				description + " (frame inside)",	// description
				Vector3D.sum(principalPoint, normal.getWithLength( frameWidth)),	// startPoint
				Vector3D.sum(principalPoint, normal.getWithLength(-frameWidth)),	// endPoint
				radius,	// radius
				frameSurfaceProperty,
				scene, studio
		);
		SceneObjectDifference frame = new SceneObjectDifference(
				description + " (frame)",	// description
				frameOutside, frameInside,
				scene, studio
			);
		scene.addSceneObject(frame);
		
	}
	
	
	
	// GUI panels
	
	private JComboBox<SimulationType> simulationTypeComboBox;
//	private JComboBox<ViewType> viewTypeComboBox;
	private LabelledDoublePanel alpha1Panel, alpha2Panel, rotationAnglePanel,	// f1Panel, // f2Panel, 
		distancePanel, cameraFocussingDistancePanel, cameraHorizontalFOVDegPanel, xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, zLengthPanel, zCentrePanel;//telescopeMagnificationPanel, 
	private LabelledIntPanel nXPanel, nYPanel, nZPanel;
	// private LabelledVector3DPanel xVectorPanel, yVectorPanel, zVectorPanel;
	private LabelledVector3DPanel latticeCentrePanel, cameraViewCentrePanel;
	private JCheckBox showTrajectoriesCheckBox, showEyeAndEyeTrajectoriesCheckBox, lorentzTransformerCheckBox, circularLensesCheckBox;	//, rotateLatticeCheckBox;//showLens3CheckBox, 
	private ApertureSizeComboBox cameraApertureSizeComboBox;
	private JComboBox<LensType> lensTypeComboBox;
	private JTextField infoTextField;
	private JTabbedPane viewTypePane;

	@Override
	protected void createInteractiveControlPanel()
	{
//		super.createInteractiveControlPanel();

		interactiveControlPanel = new JPanel();
		interactiveControlPanel.setLayout(new MigLayout("insets 0"));
		
		renderQualityComboBox = new JComboBox<RenderQualityEnum>(RenderQualityEnum.values());
		renderQualityComboBox.setSelectedItem(renderQuality);
		renderQualityComboBox.addActionListener(this);
		interactiveControlPanel.add(renderQualityComboBox, "split 3");

//		testCheckBox = new JCheckBox("Test");
//		testCheckBox.setSelected(test);
//		testCheckBox.addActionListener(this);
//		interactiveControlPanel.add(testCheckBox, "split 3");	// , "split 2");		

		renderButton = new JButton("Render");
		renderButton.addActionListener(this);
		interactiveControlPanel.add(renderButton);
		
		saveButton = new JButton("Save...");
		saveButton.addActionListener(this);
		interactiveControlPanel.add(saveButton, "wrap");		
	
		simulationTypeComboBox = new JComboBox<SimulationType>(SimulationType.values());
		simulationTypeComboBox.setSelectedItem(simulationType);
		interactiveControlPanel.add(new LabelledComponent("Simulation type", simulationTypeComboBox), "wrap");


		JPanel cameraPanel = new JPanel();
		cameraPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Camera settings"));
		cameraPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(cameraPanel, "grow, wrap");
		
		JPanel lensPanel = new JPanel();
		lensPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens settings"));
		lensPanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(lensPanel, "wrap");
		
		JPanel latticePanel = new JPanel();
		latticePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lattice settings"));
		latticePanel.setLayout(new MigLayout("insets 0"));
		interactiveControlPanel.add(latticePanel, "grow, wrap");
		
		//lensPanel stuff
		alpha1Panel = new LabelledDoublePanel("alpha_1 (degrees)");
		alpha1Panel.setNumber(alpha1);
		lensPanel.add(alpha1Panel);

		rotationAnglePanel = new LabelledDoublePanel("rotation angle (degrees)");
		rotationAnglePanel.setNumber(rotationAngle);
		lensPanel.add(rotationAnglePanel, "wrap");
		
//		f1Panel = new LabelledDoublePanel("f_1");
//		f1Panel.setNumber(f1);
//		lensPanel.add(f1Panel, "wrap");
		
		alpha2Panel = new LabelledDoublePanel("alpha_2 (degrees)");
		alpha2Panel.setNumber(alpha2);
		lensPanel.add(alpha2Panel);

		distancePanel = new LabelledDoublePanel("separation of lenses 1 & 2");
		distancePanel.setNumber(distance);
		lensPanel.add(distancePanel, "wrap");

//		f2Panel = new LabelledDoublePanel("f_2");
//		f2Panel.setNumber(f2);
//		lensPanel.add(f2Panel, "wrap");
		
//		showLens3CheckBox = new JCheckBox("Show lens 3");
//		showLens3CheckBox.setSelected(showLens3);
//		lensPanel.add(showLens3CheckBox);
		
//		telescopeMagnificationPanel = new LabelledDoublePanel("magnification");
//		telescopeMagnificationPanel.setNumber(telescopeMagnification);
//		lensPanel.add(telescopeMagnificationPanel, "wrap");
		
		infoTextField = new JTextField(45);
		infoTextField.setText("Hi, scientists!");
		infoTextField.setEditable(false);
		lensPanel.add(infoTextField, "span");
		
//		p3TextField = new JTextField(20);
//		p3TextField.setText("Hi, scientists!");
//		p3TextField.setEditable(false);
//		lensPanel.add(p3TextField);

		//  doesn't  work  at the moment -- comment out
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(lensType);
		lensPanel.add(new LabelledComponent("Lens type", lensTypeComboBox), "span");
		
		circularLensesCheckBox = new JCheckBox("Circular lenses");
		circularLensesCheckBox.setSelected(circularLenses);
		circularLensesCheckBox.setEnabled(false);	// unticking this doesn't show any lenses, so disable for the moment
		lensPanel.add(circularLensesCheckBox, "span");
		
		lorentzTransformerCheckBox = new JCheckBox("Lorentz transformer (lens 3 shifted to add shearing)");
		lorentzTransformerCheckBox.setSelected(lorentzTransformer);
		lensPanel.add(lorentzTransformerCheckBox, "span");

		//latticePanel stuff
		
//		rotateLatticeCheckBox = new JCheckBox("Rotate lattice around axis of three-lens rotation");
//		rotateLatticeCheckBox.setSelected(rotateLattice);
//		latticePanel.add(rotateLatticeCheckBox, "span");
		
		xMinPanel  = new LabelledDoublePanel("x min");
		xMinPanel.setNumber(xMin);
		latticePanel.add(xMinPanel);
		
		yMinPanel  = new LabelledDoublePanel("y min");
		yMinPanel.setNumber(yMin);
		latticePanel.add(yMinPanel);
		
		zMinPanel  = new LabelledDoublePanel("z min");
		zMinPanel.setNumber(zMin);
		latticePanel.add(zMinPanel, "wrap");
		
		xMaxPanel  = new LabelledDoublePanel("x max");
		xMaxPanel.setNumber(xMax);
		latticePanel.add(xMaxPanel);
		
		yMaxPanel  = new LabelledDoublePanel("y max");
		yMaxPanel.setNumber(yMax);
		latticePanel.add(yMaxPanel);
		
		zMaxPanel  = new LabelledDoublePanel("z max");
		zMaxPanel.setNumber(zMax);
		latticePanel.add(zMaxPanel, "wrap");
		
		nXPanel = new LabelledIntPanel("x-lattice density");
		nXPanel.setNumber(nX);
		latticePanel.add(nXPanel);
		
		nYPanel = new LabelledIntPanel("y-lattice density");
		nYPanel.setNumber(nY);
		latticePanel.add(nYPanel);
		
		nZPanel = new LabelledIntPanel("z-lattice density");
		nZPanel.setNumber(nZ);
		latticePanel.add(nZPanel, "wrap");
		
//		xVectorPanel = new LabelledVector3DPanel("lattice x direction");
//		xVectorPanel.setVector3D(xVector);
//		latticePanel.add(xVectorPanel, "span"); 
//		
//		yVectorPanel = new LabelledVector3DPanel("lattice y direction");
//		yVectorPanel.setVector3D(yVector);
//		latticePanel.add(yVectorPanel, "span"); 
//
//		zVectorPanel = new LabelledVector3DPanel("lattice z direction");
//		zVectorPanel.setVector3D(zVector);
//		latticePanel.add(zVectorPanel, "span"); 
		
		latticeCentrePanel = new LabelledVector3DPanel("centre of lattice   ");
		latticeCentrePanel.setVector3D(latticeCentre);
		latticePanel.add(latticeCentrePanel, "span"); 
		
		//cameraPanel stuff
		
		viewTypePane = new JTabbedPane();
		
		JPanel standardViewPanel = new JPanel();
		standardViewPanel.setLayout(new MigLayout("insets 0"));

		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		standardViewPanel.add(cameraViewCentrePanel, "span");
		
		cameraApertureSizeComboBox = new ApertureSizeComboBox();
		cameraApertureSizeComboBox.setApertureSize(cameraApertureSize);
		standardViewPanel.add(new LabelledComponent("Camera aperture", cameraApertureSizeComboBox));

		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		standardViewPanel.add(cameraFocussingDistancePanel, "span");
		
		cameraHorizontalFOVDegPanel = new LabelledDoublePanel("Horizontal FOV", "&deg;");
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		standardViewPanel.add(cameraHorizontalFOVDegPanel, "span");
		
		viewTypePane.addTab(ViewType.STANDARD.toString(), standardViewPanel);

		
		JPanel topViewPanel = new JPanel();
		topViewPanel.setLayout(new MigLayout("insets 0"));

		showTrajectoriesCheckBox = new JCheckBox("Show trajectories");
		showTrajectoriesCheckBox.setSelected(showTrajectories);
		topViewPanel.add(showTrajectoriesCheckBox, "span");

		showEyeAndEyeTrajectoriesCheckBox = new JCheckBox("Show eye and eye trajectories");
		showEyeAndEyeTrajectoriesCheckBox.setSelected(showEyeAndEyeTrajectories);
		topViewPanel.add(showEyeAndEyeTrajectoriesCheckBox, "span");
		
		zLengthPanel = new LabelledDoublePanel("z field of view");
		zLengthPanel.setNumber(zLength);
		topViewPanel.add(zLengthPanel);
		
		zCentrePanel = new LabelledDoublePanel("field of view centred on z =");
		zCentrePanel.setNumber(zCentre);
		topViewPanel.add(zCentrePanel, "wrap");

		viewTypePane.addTab(ViewType.TOP.toString(), topViewPanel);

		
		cameraPanel.add(viewTypePane, "wrap");
		
		// set the selected tab to correspond to viewType
		for(int t=0; t<viewTypePane.getTabCount(); t++)
		{
			if(viewTypePane.getTitleAt(t).equals(viewType.toString()))
			{
				viewTypePane.setSelectedIndex(t);
				break;
			}
		}

//		viewTypeComboBox = new JComboBox<ViewType>(ViewType.values());
//		viewTypeComboBox.setSelectedItem(viewTypeComboBox);
//		cameraPanel.add(new LabelledComponent("View", viewTypeComboBox), "wrap");

//		topViewCheckBox = new JCheckBox("Top view");
//		topViewCheckBox.setSelected(topView);
//		cameraPanel.add(topViewCheckBox, "span");

	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		simulationType = (SimulationType)(simulationTypeComboBox.getSelectedItem());
		alpha1 = alpha1Panel.getNumber();

		alpha2 = alpha2Panel.getNumber();

		rotationAngle  = rotationAnglePanel.getNumber();
// 		f1 = f1Panel.getNumber();
//		f2 = f2Panel.getNumber();
		distance = distancePanel.getNumber();//////////////
		xMin = xMinPanel.getNumber();
		xMax = xMaxPanel.getNumber();
		yMin = yMinPanel.getNumber();
		yMax = yMaxPanel.getNumber();
		zMin = zMinPanel.getNumber();
		zMax = zMaxPanel.getNumber();
		nX = nXPanel.getNumber();
		nY = nYPanel.getNumber();
		nZ = nZPanel.getNumber();
//		xVector = xVectorPanel.getVector3D();
//		yVector = yVectorPanel.getVector3D();
//		zVector = zVectorPanel.getVector3D();
		latticeCentre = latticeCentrePanel.getVector3D();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
//		telescopeMagnification = telescopeMagnificationPanel.getNumber();
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraApertureSize = cameraApertureSizeComboBox.getApertureSize();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
//		showLens3 = showLens3CheckBox.isSelected();
		lensType = (LensType)(lensTypeComboBox.getSelectedItem());	// doesn't work  at  the moment -- comment  out
		circularLenses = circularLensesCheckBox.isSelected();
		lorentzTransformer = lorentzTransformerCheckBox.isSelected();
//		viewType = (ViewType)(viewTypeComboBox.getSelectedItem());
		// set the viewType to correspond to the selected tab
		for(ViewType v:ViewType.values())
		{
			if(v.toString().equals(viewTypePane.getTitleAt(viewTypePane.getSelectedIndex())))
			{
				viewType = v;
				break;
			}
		}

// 		topView = topViewCheckBox.isSelected();
		showTrajectories = showTrajectoriesCheckBox.isSelected();
		showEyeAndEyeTrajectories = showEyeAndEyeTrajectoriesCheckBox.isSelected();
		zLength = zLengthPanel.getNumber();
		zCentre = zCentrePanel.getNumber();
		
		// rotateLattice = rotateLatticeCheckBox.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
				
//		if(e.getSource().equals(testCheckBox))
//		{
//			setTest(testCheckBox.isSelected());
//			// render();
//		}
	}


	public static void main(final String[] args)
	{
		(new ThreeSkewLensRotationVisualiser()).run();
	}
}
