package optics.raytrace.test;

import Jama.Matrix;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.surfaces.SurfaceOfTOXShifter;

/**
 * For testing of individual bits
 */
public class Test
{
	/**
	 * This method gets called when the Java application starts.
	 * 
	 * @see createStudio
	 * @author	Alasdair Hamilton, Johannes Courtial
	 */
	public static void main(final String[] args)
	{
//		Vector3D
//			QNeg = new Vector3D(1,3,4),
//			QPos = new Vector3D(3,5,7);
		
//		GlensHologram g = new GlensHologram(
//				new Vector3D(0,0,10),	// pointOnGlens,
//				new Vector3D(0,0,1),	// opticalAxisDirectionPos,
//				QNeg,
//				QPos,
//				0.9,	// transmissionCoefficient,
//				true	// shadowThrowing
//			);
		
//		GlensHologram g = new GlensHologram(
//				new Vector3D(0, 0, 1),	// opticalAxisDirectionPos,
//				new Vector3D(0, 0, 10),	// pointOnGlens,
//				new Vector3D(3, 4, 5),	// nodalDirection,
//				-0.5,	// fNegOverFPos,
//				0.9,	// transmissionCoefficient,
//				true	// shadowThrowing
//			);
		
//		System.out.println("GlensHologram: "+g);
//		
//		System.out.println("image is "+g.getImagePosition(QNeg, ImagingDirection.NEG2POS)+" , should be "+QPos);
//		System.out.println("image is "+g.getImagePosition(QPos, ImagingDirection.POS2NEG)+" , should be "+QNeg);
//		
//		Vector3D d = new Vector3D(1, 2, 3);	// incident light-ray direction
//		Vector3D i = new Vector3D(2, 3, 10);	// intersection point
//		
//		Vector3D dP = g.getRefractedLightRayDirection(
//				d,	// incident light-ray direction
//				i	// intersectionPoint
//			);
//		
//		Vector3D dPP = g.getRefractedLightRayDirection(dP.getReverse(), i);
//		
//		System.out.println("d="+d+", d'="+dP+", d''="+dPP);
				
//		Vector3D
//			QNeg = new Vector3D(0.8, 0.8, 10.5),
//			QPos = new Vector3D(0.8, 0.8, 7.5);
//
//		GlensHologram gh = new GlensHologram(
//				new Vector3D(1, 1, 10),	// pointOnGlens,
//				new Vector3D(0, 0.8, -0.32),	// opticalAxisDirectionPos,
//				QNeg,
//				QPos,
//				0.96,	// transmissionCoefficient,
//				true	// shadowThrowing
//			);
//		
//		System.out.println(
//				"The image of Q- is " +
//				gh.getImagePosition(QNeg, ImagingDirection.NEG2POS) +
//				" (should be " + QPos + "). " +
//				"The image of Q+ is " +
//				gh.getImagePosition(QPos, ImagingDirection.POS2NEG) +
//				" (should be " + QNeg + ")."
//			);
//
//		System.out.println("transformed position: "+LorentzTransform.getTransformedPosition(
//				new Vector3D(0.111049, 0., 1.09939),	// x,
//				-1,	// t,
//				new Vector3D(0.1, 0, 0.99)	// beta
//			));
		
//		Studio studio = new Studio();
//		EditableSceneObjectCollection scene = new EditableSceneObjectCollection(
//				"test scene",	// description,
//				false,	// combinationModeEditable,
//				null,	// parent,
//				studio);
//		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
//		scene.addSceneObject(new EditableRochesterCloak(scene, studio));
//		try {
//			System.out.println(
//					"colour: "+
//					scene.getColour(new Ray(new Vector3D(-0.99, 10, 10.99), new Vector3D(0, -1, 0), 0), null, scene, 100, new DefaultRaytraceExceptionHandler())
//				);
//		} catch (RayTraceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println(Geometry.lineLineIntersection(
//				new Vector3D(0,0,0),	// pointOnLine1, 
//				new Vector3D(1,0,1),	// directionOfLine1,
//				new Vector3D(1,0,0),	// pointOnLine2,
//				new Vector3D(-1,0,1),	// directionOfLine2
//				true
//			));
//		
//		try {
//			System.out.println("refracted ray direction = " +
//				MetricInterface.getRefractedLightRayDirectionSurfaceCoordinates(
//					new Vector3D(1,0,1),	// d
//					MetricInterface.getDiagonalMetricTensor(1,1,1),	// g
//					MetricInterface.getDiagonalMetricTensor(1,1,1),	// h
//					RefractionType.POSITIVE_REFRACTION,	// refractionType
//					false	// allowImaginaryOpticalPathLengths
//				)
//			);
//		} catch (RayTraceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println("plane-plane intersection point = "+Geometry.pointOnPlanePlaneIntersection(
//				new Vector3D(10, 20, 0),	// p1
//				new Vector3D(0, 0, 1),	// n1
//				new Vector3D(0, 0, 1),	// p2
//				new Vector3D(0, .1, 1),	// n2
//				new Vector3D(0, 0, 0)	// p0
//			));
		
//		LensSurface_old lensSurface = new LensSurface_old(
//				"Lens surface",	// description
//				new Vector3D(0, 0, 0),	// principal point
//				-1,	// focalDistance
//				1.5,	// refractiveIndex
//				new Vector3D(0, 0, 1),	// directionInside
//				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
//				false,	// shadowThrowing
//				null,	// parent
//				null
//		);
//		System.out.println("lensSurface.calculateW(0,1)="+lensSurface.calculateW(0,1));

//		Vector3D insideSpacePosition = new Vector3D(1,2,6);
//		Vector3D outsideSpacePosition = new Vector3D(0, 0, -1);
//		Vector3D pointOnPlane = new Vector3D(1,2,3);
//		Vector3D a = new Vector3D(3, 2, 0.5);
////		Vector3D u = Vector3D.X;
////		Vector3D v = Vector3D.Y;
////		HomogeneousPlanarImagingSurface h = new HomogeneousPlanarImagingSurface(
////				 pointOnPlane,
////				 a,
////				 u,
////				 v,
////				 insideSpacePosition,
////				 outsideSpacePosition
////			);
//		IdealThinLensSurface h = null;
//		try {
//			h = new IdealThinLensSurface(
//					 pointOnPlane,
//					 a,
//					 insideSpacePosition,
//					 outsideSpacePosition,
//					 1,	// transmission coefficient
//					 false	// shadow-throwing
//				);
//		} catch (InconsistencyException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("outside-space position: "+outsideSpacePosition+", inside-space position: "+insideSpacePosition);
//		System.out.println("outwards imaging test: image of inside-space position (should be outside-space position): "+h.getImagePosition(insideSpacePosition, Orientation.OUTWARDS));
//		System.out.println("inwards imaging test: image of outside-space position (should be inside-space position): "+h.getImagePosition(outsideSpacePosition, Orientation.INWARDS));

//	    String html = "<i>z</i> = 5 10<sup>10</sup>";
//	    int width = 100, height = 100;
//
//	    BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
//	        .getDefaultScreenDevice().getDefaultConfiguration()
//	        .createCompatibleImage(width, height);
//
//	    Graphics graphics = image.createGraphics();
//
//	    JEditorPane jep = new JEditorPane("text/html", html);
//	    jep.setSize(width, height);
//	    jep.print(graphics);
//
//	    try {
//			ImageIO.write(image, "png", new File("Image.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		// test the InterpolatedFunction1D class
//		double[][] xyTable = {{0,0},{0.1,0.2},{0.5,0.21},{1,0.5}};
//		try {
//			InterpolatedFunction1D f = new InterpolatedFunction1D(xyTable);
//			for(int i=0; i<100; i++)
//			{
//				double x = Math.random();
//				System.out.println("f("+x+")="+f.calculateY(x));
//			}
//		} catch (InconsistencyException e) {
//			e.printStackTrace();
//		}
//		
//		for(int i=0; i<100; i++)
//		{
//			double P = Math.random();
//			System.out.println("inverseSingleSlitCumulativeProbabilityFunction("+P+")="+SingleSlitDiffraction.calculateX(P));
//		}
			
//		EditableIdealLensCloak.calculateGeometryAndFDFromFAFB(
//				0.0149,	// fA
//				0.144,	// fB
//				0.5,	// h
//				0.3,	// h2
//				2	// R
//			);
		
//		Vector3D centre = new Vector3D(0, 0, 0);
//		double radius = 1;
//		double innerRadius = 0.5;
//		double deltaX = 1;
//		int maxSteps = 1000;
//		IntegrationType integrationType = IntegrationType.RK4;
//		double deltaTau = 0.001;
//		double deltaXMax = 0.25*innerRadius;
//
//		SurfaceOfTOXShifter s = new SurfaceOfTOXShifter(
//				centre,
//				radius,
//				innerRadius,
//				deltaX,
//				deltaTau,
//				deltaXMax,
//				maxSteps,
//				integrationType,
//				0.96,	// transmissionCoefficient,
//				false	// shadowThrowing
//			);
//
//		Vector3D x = new Vector3D(0.4, 0.5, 0.6);
//		Vector3D d = new Vector3D(1, 2, 3);
//		Vector3D k = s.calculateK(x, d);
//		System.out.println("k = "+k);
//		
//		Matrix n = s.calculateEpsilonMuTensor(x);
//		System.out.println("n = ");
//		for(int r = 0; r < n.getRowDimension(); r++)
//		{
//			for(int c = 0; c < n.getColumnDimension(); c++)
//			{
//				System.out.print("\t "+n.get(r,c));
//			}
//			System.out.println(" ");
//		}
//		
//		System.out.println("H(k,n) = "+s.calculateHamiltonian(k, n));
//
//		System.out.println("dx/dtau = "+s.dXdTau(x, k));
//		System.out.println("dk/dtau = "+s.dKdTau(x, k));
		
		for(int j=0; j<4; j++)
		{
			System.out.println("(j+1) % 4 = " + ((j+1) % 4));
		}
	}
}
