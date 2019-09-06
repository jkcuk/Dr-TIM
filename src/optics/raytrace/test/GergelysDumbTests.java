package optics.raytrace.test;

import java.util.ArrayList;
import java.util.Arrays;

import math.Vector3D;
import math.simplicialComplex.Face;
import math.simplicialComplex.SimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableLensSimplicialComplex;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.Orientation;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SceneObjectClass;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.ImagingDirection;

/**
 * For testing of individual bits
 */
public class GergelysDumbTests
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

//!!!!!MY MESS STARTS HERE
/*		
		Vector3D insideSpacePosition = new Vector3D((int)(Math.random()*20-9),(int)(Math.random()*20-9),(int)(Math.random()*20-9));
		Vector3D outsideSpacePosition = insideSpacePosition.getProductWith(3.56); //new Vector3D((int)(Math.random()*20-9),(int)(Math.random()*20-9),(int)(Math.random()*20-9));
		Vector3D pointOnPlane = new Vector3D(0,0,0);
		Vector3D a = new Vector3D(0, Math.sqrt(2), 2*Math.sqrt(2));
//		Vector3D a = new Vector3D(0, Math.random()-0.5, 1);
//		Vector3D u = Vector3D.X;
//		Vector3D v = Vector3D.Y;
//		HomogeneousPlanarImagingSurface h = new HomogeneousPlanarImagingSurface(
//				 pointOnPlane,
//				 a,
//				 u,
//				 v,
//				 insideSpacePosition,
//				 outsideSpacePosition
//			);
		IdealThinLensSurface h = null;
		try {
			h = new IdealThinLensSurface(
					 pointOnPlane,
					 a,
					 insideSpacePosition,
					 outsideSpacePosition,
					 1,	// transmission coefficient
					 false	// shadow-throwing
				);
		} catch (InconsistencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("outside-space position: "+outsideSpacePosition+", inside-space position: "+insideSpacePosition);
		System.out.println("inwards imaging test: image of inside-space position (should be inside-space position): "+h.getImagePosition(outsideSpacePosition, Orientation.INWARDS));
		System.out.println("outwards imaging test: image of outside-space position (should be outside-space position): "+h.getImagePosition(insideSpacePosition, Orientation.OUTWARDS));
//		System.out.println("outsideSpacePosition is off by "+(Vector3D.difference(outsideSpacePosition, h.getImagePosition(outsideSpacePosition, Orientation.INWARDS))));
//		System.out.println("insidespacePosition is off by "+(Vector3D.difference(insideSpacePosition, h.getImagePosition(insideSpacePosition, Orientation.OUTWARDS))));
		System.out.println("Lens "+h.toString()); 

//-----------------------------------------		
		double focLength = 2;
		IdealThinLensSurface iTLS = new IdealThinLensSurface(
				new Vector3D(0,0,1), // opticalAxisDirectionPos
				new Vector3D(0,0,0), // principalPoint,
				focLength, //focalLength,
				1,//double transmissionCoefficient,
				false);//boolean shadowThrowing

		for(double objPos = 1.5; objPos > -1.6; objPos = objPos - 0.2) {
			Vector3D objectPosition = new Vector3D(0,0,objPos);
//			System.out.println("object at "+objPos+", manually calculated image at "+(1/(1/focLength - 1/objPos))); 
//			System.out.println("getImagePosition images object to "+iTLS.getImagePosition(objectPosition, Orientation.INWARDS));
			System.out.println("object at "+objPos+", manually calculated image at "+(1/(1/focLength + 1/objPos))); 
			System.out.println("getImagePosition images object to "+iTLS.getImagePosition(objectPosition, Orientation.OUTWARDS));
		}
		//-----------------------------------------		

		
		int counter1 = 0;
		int counter2 = 0;
		Vector3D principalPoint = new Vector3D(0,0,0);
//		Vector3D randomObject = new Vector3D(0,0,-1);
//		double focLen = 2;
//		IdealThinLensSurface iTLS = new IdealThinLensSurface(
//				new Vector3D(0,0,1),
//				principalPoint,
//				focLen,
//				0.96,
//				false
//				);
		int counter = 0;
		for(counter=0; counter <=1; counter++) {
			System.out.println(counter+"\t\t"+Math.pow(-1, counter));
		}
		System.out.println("Object coord.\t\tFocalLength\tOrientation\tImage coord.\t\tu-(Object dot a)\t\tv = Image dot a");
		for(counter1=0; counter1 <= 1; counter1++) {
			Vector3D randomObject = new Vector3D(0,0,Math.pow(-1, counter1)*3);
			for(counter2 = 0; counter2 <= 1; counter2++) {
				double focLen = Math.pow(-1, counter2)*2;
				IdealThinLensSurface iTLS = new IdealThinLensSurface(
						new Vector3D(0,0,1),
						principalPoint,
						focLen,
						0.96,
						false
						);
				System.out.println(randomObject.getScalarProductWith(Vector3D.Z)+"\t\t\t"+iTLS.getFocalLength()+"\t\t"+"OUT"+"\t\t"+(double)(Math.round(100*iTLS.getImagePosition(randomObject, Orientation.OUTWARDS).getScalarProductWith(Vector3D.Z)))/100+"\t\t\t"+(-1)*Vector3D.scalarProduct(randomObject, iTLS.getOpticalAxisDirectionPos())+"\t\t\t"+(double)(Math.round(100*Vector3D.scalarProduct(iTLS.getImagePosition(randomObject, Orientation.OUTWARDS), iTLS.getOpticalAxisDirectionPos())))/100);			
				System.out.println(randomObject.getScalarProductWith(Vector3D.Z)+"\t\t\t"+iTLS.getFocalLength()+"\t\t"+"IN"+"\t\t"+(double)(Math.round(100*iTLS.getImagePosition(randomObject, Orientation.INWARDS).getScalarProductWith(Vector3D.Z)))/100+"\t\t\t"+-Vector3D.scalarProduct(randomObject, iTLS.getOpticalAxisDirectionPos())+"\t\t\t"+(double)(Math.round(100*Vector3D.scalarProduct(iTLS.getImagePosition(randomObject, Orientation.INWARDS), iTLS.getOpticalAxisDirectionPos())))/100);
			}
		}

		System.out.println("PrincipalPoint\t\tOpticaAxisDirectionPos\t\tFocalLength\t\tFocalPointPos\t\tFocalPointNeg");
		System.out.println(iTLS.getPrincipalPoint()+"\t\t"+iTLS.getOpticalAxisDirectionPos()+"\t\t\t"+iTLS.getFocalLength()+"\t\t"+iTLS.getFocalPointPos()+"\t\t"+iTLS.getFocalPointNeg());
		System.out.println("OUTWARDS:\tRandomObject\t\tImageOfRandomObject\tImageOfImageOfRandomObject");
		System.out.println("\t\t"+randomObject+"\t\t"+iTLS.getImagePosition(randomObject, Orientation.OUTWARDS)+"\t\t"+iTLS.getImagePosition(iTLS.getImagePosition(randomObject, Orientation.OUTWARDS), Orientation.INWARDS));
		System.out.println("INWARDS:\t\tRandomObject\t\tImageOfRandomObject\tImageOfImageOfRandomObject");
		System.out.println("\t\t"+randomObject+"\t\t"+iTLS.getImagePosition(randomObject, Orientation.INWARDS)+"\t\t"+iTLS.getImagePosition(iTLS.getImagePosition(randomObject, Orientation.INWARDS), Orientation.OUTWARDS));				
*/
		//
		Studio studio = new Studio();
		EditableSceneObjectCollection scene = new EditableSceneObjectCollection(
				"test scene",	// description,
				false,	// combinationModeEditable,
				null,	// parent,
				studio);
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		
		EditableLensSimplicialComplex thinLensSimplex = new EditableLensSimplicialComplex(
				"ideal thin lens simplicial complex",// description,
				scene,// parent,
				studio// studio
			);
		ArrayList<Face> faces = thinLensSimplex.getLensSimplicialComplex().getFaces();
		System.out.println(faces.size());
		Face face5 = faces.get(5);
		System.out.println(face5);
		int [] vertexIndicesOfFace5 = face5.getVertexIndices();
//		for(int index : vertexIndicesOfFace5) {
//			System.out.println(index+"\t"+vertexIndicesOfFace5[index]);
//		}
		int index =0;
		while(index <= vertexIndicesOfFace5.length-1) {
			System.out.println(index+"\t"+vertexIndicesOfFace5[index]+"\t"+vertexIndicesOfFace5.length);
			System.out.println(face5.getVertex(index));
			index += 1;
		}
		try {
			System.out.println(face5.calculateOutwardsNormal());
		} catch (InconsistencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		int counter = 0;
//		for(counter=0; counter <=2; counter++) {
//			int integer+counter = 
//		}
//		}
		
	}
}
