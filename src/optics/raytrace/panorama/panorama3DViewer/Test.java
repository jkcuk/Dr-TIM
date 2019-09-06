package optics.raytrace.panorama.panorama3DViewer;

import java.awt.image.BufferedImage;

import optics.raytrace.core.Ray;
import optics.raytrace.panorama.Panorama2D;
import optics.raytrace.panorama.PhiTheta;
import optics.raytrace.panorama.Side;
import optics.raytrace.panorama.panorama3DGeometry.PositionInvertingGeneralisedStandardPanorama3DGeometry;
import optics.raytrace.panorama.panorama3DGeometry.PositionInvertingStandardPanorama3DGeometry;
import math.MyMath;
import math.Vector3D;


public class Test
{
	public static void testPanorama2D()
	{
		System.out.println("*** Testing Panorama2D ***");
		
		Panorama2D p = new Panorama2D(new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB));
		
		// test conversion between angles and indices
		System.out.println("should be 100: " + p.phi2i(p.i2phi(100)));
		System.out.println("p.j2theta(11)="+MyMath.rad2deg(p.j2theta(11)));
		System.out.println("should be 11: " + p.theta2j(p.j2theta(11)));
	}
	
	public static void testPositionInvertingStandardPanorama3DGeometry()
	{
		System.out.println("*** Testing PositionInvertingStandardPanorama3DGeometry ***");
		
		PositionInvertingStandardPanorama3DGeometry g = new PositionInvertingStandardPanorama3DGeometry(
				new Vector3D(0, 0, 0),	// M,
				new Vector3D(0, 1, 0),	// theta0,
				new Vector3D(0, 0, -1),	// phi0,
				0.7	// i
			);

		// pick some random angles and a side
		double phi = -20;	// in °
		double theta = 70;	// in °
		Side side = Side.RIGHT;
		
		// calculate a (backwards) ray that corresponds to the given angles, starting at the camera position and travelling
		// in the given direction
		Ray r = g.getRayForAngles(MyMath.deg2rad(phi), MyMath.deg2rad(theta), side);
		
		System.out.println("light ray: "+r);
		
		// calculate a random position on this ray
		double f = 2;
		Vector3D P = Vector3D.sum(r.getP(), r.getD().getProductWith(f));	// P + f*d
		
		System.out.println("point on ray: "+P);

		// calculate the camera position from which this point is seen
		Vector3D C = g.calculateCameraPosition(P, side, null);
		
		System.out.println("Camera position: " + C);

		// calculate the angles that correspond to this point
		PhiTheta phiTheta = g.getAnglesForPosition(P, side, null);
		
		// print these angles out; hopefully they are the same as the ones that went in!?
		System.out.println(
				"phi="+MyMath.rad2deg(phiTheta.getPhi())+
				", theta="+MyMath.rad2deg(phiTheta.getTheta())+
				" (should be "+phi+
				" and "+theta+")"
			);
	}

	public static void testGeneralisedStandardPanorama3DGeometry()
	{
		System.out.println("*** Testing GeneralisedStandardPanorama3DGeometry ***");

		PositionInvertingGeneralisedStandardPanorama3DGeometry g = new PositionInvertingGeneralisedStandardPanorama3DGeometry(
				new Vector3D(2, 1, 3),	// M,
				new Vector3D(0, 1, 0),	// theta0,
				new Vector3D(0, 0, -1),	// phi0,
				0.7,	// i
				10	// R
			);

		// pick some random angles and a side
		double phi = -20;	// in °
		double theta = 70;	// in °
		Side side = Side.RIGHT;
		
		// calculate a (backwards) ray that corresponds to the given angles, starting at the camera position and travelling
		// in the given direction
		Ray r = g.getRayForAngles(MyMath.deg2rad(phi), MyMath.deg2rad(theta), side);
		
		System.out.println("light ray: "+r);
		
		// calculate a random position on this ray
		double f = 2;
		Vector3D P = Vector3D.sum(r.getP(), r.getD().getProductWith(f));	// P + f*d
		
		System.out.println("point on ray: "+P);

		// calculate the camera position from which this point is seen
		Vector3D C = g.calculateCameraPosition(P, side, P);
		
		System.out.println("Camera position: " + C);

		// calculate the angles that correspond to this point
		PhiTheta phiTheta = g.getAnglesForPosition(P, side, P);
		
		// print these angles out; hopefully they are the same as the ones that went in!?
		System.out.println(
				"phi="+MyMath.rad2deg(phiTheta.getPhi())+
				", theta="+MyMath.rad2deg(phiTheta.getTheta())
			);
	}

	public static void main(String[] args)
	{
//		Screen s = new Screen(
//				new Vector(0, 0, 0),	// betweenTheEyes
//				new Vector(0, 1, 0),	// up
//				new Vector(0, 0, 1),	// d
//				2,	// w
//				1,	// h
//				360,	// M
//				180	// N
//			);
//		
//		System.out.println(""+s);
//		System.out.println("(0,0) = " + s.getPixelPosition(0, 0));
//		System.out.println("(180, 90) = " + s.getPixelPosition(180, 90));
//		System.out.println("(360, 180) = " + s.getPixelPosition(360, 180));

//		Projection p = new Projection(
//				new Vector(0, 0, 0),	// betweenTheEyes
//				new Vector(0, 1, 0),	// up
//				new Vector(1, 0, 0),	// phi0
//				1	// interpupillary distance
//			);
//		
//		Vector P = new Vector(0, 0, 0.501);
//		System.out.println("The left camera looking at "+P+" is positioned at "+p.calculateEyePosition(P, -1));
//		System.out.println("The right camera looking at "+P+" is positioned at "+p.calculateEyePosition(P, 1));
//
//		Vector d = p.calculateDirectionFromEye(P, -1);
//		System.out.println("The left camera sees "+P+" in the direction "+d);
//		System.out.println("This direction corresponds to angles (theta, phi) = (" + 
//					MyMath.rad2deg(p.d2Theta(d)) + ", " +
//					MyMath.rad2deg(p.d2Phi(d)) + ")"
//				);

//		for(int i=-20; i<=20; i++) System.out.println(i+" mod 10 = "+MyMath.mod(i, 10));
		
		testPositionInvertingStandardPanorama3DGeometry();
		// System.out.println("");

		// testGeneralisedStandardPanorama3DGeometry();
		// System.out.println("");

		// testPanorama2D();
		
		// for(int i=1; i<=10000; i++) System.out.print(i);
	}

}
