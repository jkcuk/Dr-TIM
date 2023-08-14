package optics.raytrace.research.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableScaledParametrisedDisc;
import optics.raytrace.GUI.sceneObjects.EditableTimHead;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.AdamsIdealLensLookalike;
import optics.raytrace.sceneObjects.ScaledParametrisedCentredParallelogram;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.sceneObjects.TriangulatedSurface;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;


/**
 * Based on NonInteractiveTIM.
 */
public class AdamsIdealLensLookalikeTest extends NonInteractiveTIMEngine
{

	private boolean showIdealLensLookalike;

	private boolean showImageAtIntendedPosition;

	private double refractiveIndexFront, refractiveIndexBack;
	
	private Vector3D pointOnIdealLens;
	
	
	private Vector3D p, q, idealLensNormal;
	
	private double radius;
	
	private double thickness, stepSize;
	
	private int steps;

	/**
	 * Camera position
	 */
	private Vector3D cameraPosition;

	/*
	 * Refractive index
	 */
	private double n;

	public enum Material {
		REFRACTIVE("Refractive"), COLOURED("Coloured"), TINTED("Tinted");

		private String description;
		private Material(String description)
		{
			this.description = description;
		}

		@Override
		public String toString() {return description;}
	};
	public Material material;

	//
	// the rest of the scene
	//

	/**
	 * Determines how to initialise the backdrop
	 */
	private StudioInitialisationType studioInitialisation;




	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public AdamsIdealLensLookalikeTest()
	{
		super();

		showIdealLensLookalike = false;

		n = 1.5;
		refractiveIndexFront = 1/1.5;
		refractiveIndexBack = 1.5;
		
		p = new Vector3D(0,0,-3);
		q = new Vector3D(0,0,3);
		idealLensNormal = new Vector3D(0,0,1);
		radius = 0.5;
		pointOnIdealLens = new Vector3D(0,0,0);
		thickness = 0.3;
		stepSize = 0.01;
		steps = 30;
		
		material =
				// Material.COLOURED;
				Material.REFRACTIVE;

		// other scene objects
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop
		showImageAtIntendedPosition = false;


		renderQuality = RenderQualityEnum.DRAFT;

		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// camera parameters are set in createStudio()
		cameraViewCentre = new Vector3D(0, 0, 0);	// camera is located at (0, 0, 0)
		cameraPosition = new Vector3D(0, 0, -3);
		cameraFocussingDistance = 6;
		cameraHorizontalFOVDeg = 30;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		// ApertureSizeType.SMALL;

		windowTitle = "Dr TIM's ideal-lens-lookalike explorer";
		windowWidth = 1400;
		windowHeight = 650;
	}


	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getClassName()
	{
		return
				"AdamsIdealLensLookalikeExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);
		printStream.println("showIdealLensLookalike = "+showIdealLensLookalike);
		printStream.println("n = "+n);
		printStream.println("material = "+material);
		printStream.println("camera position " + cameraPosition);
		printStream.println("camera FOV "+cameraHorizontalFOVDeg);
		//
		// the rest of the scene
		//

		printStream.println("studioInitialisation = "+studioInitialisation);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
			throws SceneException
	{
		// the studio
		studio = new Studio();

		cameraViewDirection = Vector3D.difference(cameraViewCentre,cameraPosition).getNormalised();
		cameraDistance = (Vector3D.difference(cameraPosition, cameraViewCentre)).getLength();
		studio.setCamera(getStandardCamera());

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);


		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
				);

		if(showImageAtIntendedPosition) {
			scene.addSceneObject(new Sphere(
					"TestSphere",
					q,
					0.01,	// radius
					SurfaceColour.GREEN_MATT,
					scene,
					studio
					));
		}
		
		

		// ... and then adding scene objects to scene

		SurfaceProperty surfaceProperty1, surfaceProperty2, surfacePropertySides;//, surfacePropertySides;
		switch(material)
		{
		case REFRACTIVE:
			surfaceProperty1 = new RefractiveSimple(
					refractiveIndexFront,	// insideOutsideRefractiveIndexRatio
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
					);
			surfaceProperty2 = new RefractiveSimple(
					refractiveIndexBack,	// insideOutsideRefractiveIndexRatio
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
					);
			surfacePropertySides = SurfaceColour.GREEN_MATT;
			break;
		case TINTED:
			surfaceProperty1 = surfaceProperty2 = new SurfaceOfTintedSolid(
					0,	// redAbsorptionCoefficient
					1.,	// greenAbsorptionCoefficient
					1.,	// blueAbsorptionCoefficient
					true	// shadowThrowing
					);
			surfacePropertySides = SurfaceColour.GREEN_MATT;
			break;
		case COLOURED:
		default:
			surfaceProperty1 = SurfaceColour.CYAN_SHINY;
			surfaceProperty2 = SurfaceColour.GREEN_SHINY;
			surfacePropertySides = SurfaceColour.GREEN_MATT;
			//surfacePropertySides = SurfaceColour.YELLOW_SHINY;
		}
		
		System.out.println(idealLensNormal);
		AdamsIdealLensLookalike lookalikeLens = new AdamsIdealLensLookalike(
				"Ideal lens lookalike in java",// description,
				p,// p,
				q,// q,
				thickness,// dp,
				thickness,// dq,
				pointOnIdealLens,// pI,
				idealLensNormal,// idealLensNormal,
				2*radius,// height,
				2*radius,// width,
				steps,// iSteps,
				steps,// jSteps,
				stepSize,//stepSize
				n,// n,
				surfaceProperty1,// surfaceProperty1,
				surfaceProperty1,// surfaceProperty2,
				null,// surfacePropertySides,
				scene,	// parent
				studio
				);

//		//a small test script 
//		Vector3D testXYZ = new Vector3D (3,0,0);
//		//see how it is in UVW space, and add a number
//		Vector3D testUVW = testXYZ.toBasis(lookalikeLens.getB1(), lookalikeLens.getB2() ,lookalikeLens.getB3());
//		// add and return to XYZ space and print.
//		System.out.println(testUVW.getSumWith(Vector3D.X));
//		System.out.println(testUVW.getSumWith(Vector3D.X).fromBasis(lookalikeLens.getB1(), lookalikeLens.getB2() ,lookalikeLens.getB3()));

		if(!showIdealLensLookalike){

			
			ScaledParametrisedCentredParallelogram il;						
				//Vector3D pp = Geometry.linePlaneIntersection(p, Vector3D.difference(p,q).getNormalised(), pointOnIdealLens, idealLensNormal);	// centre
						//System.out.println(pp);
				Vector3D v1 = Vector3D.getANormal(idealLensNormal);	
				il = new ScaledParametrisedCentredParallelogram(
						"Ideal lens",	// description
						pointOnIdealLens,
						v1.getWithLength(2*radius),
						Vector3D.crossProduct(v1, idealLensNormal).getWithLength(2*radius),
//						new Vector3D(2*radius, 0, 0),	// spanVector1
//						new Vector3D(0, 2*radius, 0),	// spanVector2
						// size
						new IdealThinLensSurfaceSimple(
								pointOnIdealLens,
								idealLensNormal,	// opticalAxisDirection
								1./(-1./(-Vector3D.getDistance(pointOnIdealLens, p.getPartParallelTo(idealLensNormal))) + 1./(Vector3D.getDistance(pointOnIdealLens, q.getPartParallelTo(idealLensNormal)))),	// focalLength; 1/f = -1/o + 1/i
								SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
								true	// shadowThrowing
						),	// surfaceProperty
						scene,	// parent
						studio
				);
				scene.addSceneObject(il);
			
			
		}else { scene.addSceneObject(lookalikeLens);

//			Vector3D[][] pointsOnBackSurface = null;
//			//all the files should have the same dimension... else something will break.
//			try {
//				//open the files as a file...
//				File xFile = new File( "src/optics/raytrace/research/idealLensLookalike/Vx.txt" );
//				File yFile = new File( "src/optics/raytrace/research/idealLensLookalike/Vy.txt" );
//				File zFile = new File( "src/optics/raytrace/research/idealLensLookalike/Vz.txt" );
//				
//				//... and open as a buffered read.
//				BufferedReader brVx = new BufferedReader( new FileReader( xFile ) );
//				BufferedReader brVy = new BufferedReader( new FileReader( yFile ) );
//				BufferedReader brVz = new BufferedReader( new FileReader( zFile ) );
//
//				//loops through all i
//				int i = 0;
//				String lineVx = brVx.readLine();
//				String lineVz = brVz.readLine();
//				String lineVy = brVy.readLine();
//	            if(lineCounter(xFile) != lineCounter(yFile) || 
//	            		lineCounter(yFile) != lineCounter(zFile) || lineCounter(xFile) != lineCounter(zFile)) {
//	            	System.err.println("columns are not of the same length: x= "+lineCounter(xFile)+", y= "+lineCounter(yFile)+", z= "+lineCounter(zFile));
//	            }
//				//get the front point cloud. for now import it from a text file.
//				pointsOnBackSurface = new Vector3D[lineCounter(xFile)][lineVx.split("	").length];
//				while (lineVx != null && lineVz != null && lineVy != null) {
//
//					//loop over all j now
//					String[] x = lineVx.split("	");
//					String[] y = lineVy.split("	");
//					String[] z = lineVz.split("	");
//
//					//  System.out.println("i= "+i+", lineUy "+lineUy);
//					if(x.length != y.length || y.length != z.length || x.length != z.length) {
//						System.err.println("rows are not of the same length: x= "+x.length+", y= "+y.length+", z= "+z.length);
//						break;
//					}else {
//						for(int j = 0; j < (x.length); j++) {	
//							pointsOnBackSurface[i][j] = new Vector3D(Double.parseDouble(x[j]), Double.parseDouble(y[j]), Double.parseDouble(z[j]));   
//
//						}
//					}
//
//					lineVx = brVx.readLine();
//					lineVz = brVz.readLine();
//					lineVy = brVy.readLine(); 
//					i = i+1;
//				}
//				brVx.close();
//				brVy.close();
//				brVz.close();
//			} catch ( IOException io ) {
//				System.err.println("something does not work");
//				// e.printStackTrace();
//			}
//
//			//		System.out.println(pointsOnFrontSurface[0][0].getClass()+", "+pointsOnFrontSurface[1][0]+", "+ pointsOnFrontSurface[0][1]);
//			TriangulatedSurface backSurface = new TriangulatedSurface(
//					"Back surface",	// description
//					pointsOnBackSurface,
//					false, 
//					surfaceProperty2,
//					scene,	// parent
//					studio
//					);
//
//
//			//now the same for the back surface
//			Vector3D[][] pointsOnFrontSurface = null;
//			//all the files should have the same dimension... else something will break.
//			try {
//				//open the files as a file...
//				File xFile = new File( "src/optics/raytrace/research/idealLensLookalike/Ux.txt" );
//				File yFile = new File( "src/optics/raytrace/research/idealLensLookalike/Uy.txt" );
//				File zFile = new File( "src/optics/raytrace/research/idealLensLookalike/Uz.txt" );
//				
//				//...and buffer red them all
//				BufferedReader brUx = new BufferedReader( new FileReader( xFile ) );
//				BufferedReader brUy = new BufferedReader( new FileReader( yFile ) );
//				BufferedReader brUz = new BufferedReader( new FileReader( zFile ) );
//
//				//loops through all i
//				int i = 0;
//				String lineUx = brUx.readLine();
//				String lineUz = brUz.readLine();
//				String lineUy = brUy.readLine();
//	            if(lineCounter(xFile) != lineCounter(yFile) || 
//	            		lineCounter(yFile) != lineCounter(zFile) || lineCounter(xFile) != lineCounter(zFile)) {
//	            	System.err.println("columns are not of the same length: x= "+lineCounter(xFile)+", y= "+lineCounter(yFile)+", z= "+lineCounter(zFile));
//	            }
//				//get the front point cloud. for now import it from a text file.
//				pointsOnFrontSurface = new Vector3D[lineCounter(xFile)][lineUx.split("	").length];
//				while (lineUx != null && lineUz != null && lineUy != null) {
//
//					//loop over all j now
//					String[] x = lineUx.split("	");
//					String[] y = lineUy.split("	");
//					String[] z = lineUz.split("	");
//
//					//  System.out.println("i= "+i+", lineUy "+lineUy);
//					if(x.length != y.length || y.length != z.length || x.length != z.length) {
//						System.err.println("rows are not of the same length: x= "+x.length+", y= "+y.length+", z= "+z.length);
//						break;
//					}else {
//						for(int j = 0; j < (x.length); j++) {	
//							pointsOnFrontSurface[i][j] = new Vector3D(Double.parseDouble(x[j]), Double.parseDouble(y[j]), Double.parseDouble(z[j]));   
//
//						}
//					}
//
//					lineUx = brUx.readLine();
//					lineUz = brUz.readLine();
//					lineUy = brUy.readLine();    
//					i = i+1;
//				}
//				brUx.close();
//				brUy.close();
//				brUz.close();
//			} catch ( IOException io ) {
//				System.err.println("something does not work");
//				// e.printStackTrace();
//			}
//
//			//System.out.println(pointsOnBackSurface[18][0]);
//
//			System.out.println(pointsOnFrontSurface[2][2]);
//			TriangulatedSurface frontSurface = new TriangulatedSurface(
//					"Front surface",	// description
//					pointsOnFrontSurface,
//					false, 
//					surfaceProperty1,
//					scene,	// parent
//					studio
//					);
//
//			//		scene.addSceneObject(backSurface.getArrayOfCylindersAtEdges(0.001, SurfaceColour.YELLOW_SHINY, scene, studio));
//			//		scene.addSceneObject(frontSurface.getArrayOfCylindersAtEdges(0.001, SurfaceColour.BLACK_SHINY, scene, studio));
//
//			scene.addSceneObject(frontSurface);
//			scene.addSceneObject(backSurface);

		}


	}

	private double getFocalLength(Vector3D p, Vector3D q, Vector3D n, Vector3D principalPoint) {
		
		double f = 1/( ( 1/(Vector3D.difference(p, principalPoint).getScalarProductWith(n.getProductWith(-1))))+( ( 1/(Vector3D.difference(q, principalPoint).getScalarProductWith(n))) )) ;
		System.out.println(f);
		return f;
	}
	
	private int lineCounter(File file) throws IOException {
		BufferedReader lineCounter = new BufferedReader(new FileReader(file));
		int lines = 0;
		while (lineCounter.readLine() != null) lines++;
		lineCounter.close();
		return lines;
	}


	//
	// for interactive version
	//

	// ideal-lens-lookalike panel
	private JCheckBox showIdealLensLookalikeCheckbox, showImageAtIntendedPositionCheckBox;
	private JComboBox<Material> materialComboBox;
	private LabelledIntPanel stepsPanel;
	private LabelledDoublePanel nPanel, refractiveIndexFrontPanel, refractiveIndexBackPanel, 
	radiusPanel, thicknessPanel, stepSizePanel;
	private LabelledVector3DPanel pointOnIdealLensPanel, idealLensNormalPanel, pPanel, qPanel;
	
	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;

	// camera
	private LabelledVector3DPanel cameraPositionPanel;
	private LabelledVector3DPanel cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private LabelledDoublePanel cameraFocussingDistancePanel;


	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();

		JTabbedPane tabbedPane = new JTabbedPane();
		interactiveControlPanel.add(tabbedPane, "span");

		// ideal-lens-lookalike panel

		JPanel idealLensLookalikePanel = new JPanel();
		idealLensLookalikePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Ideal-lens lookalike", idealLensLookalikePanel);

		showIdealLensLookalikeCheckbox = new JCheckBox("Show ideal-lens lookalike");
		showIdealLensLookalikeCheckbox.setSelected(showIdealLensLookalike);
		idealLensLookalikePanel.add(showIdealLensLookalikeCheckbox, "span");

		materialComboBox = new JComboBox<Material>(Material.values());
		materialComboBox.setSelectedItem(material);
		idealLensLookalikePanel.add(GUIBitsAndBobs.makeRow("Material", materialComboBox), "span");

		nPanel = new LabelledDoublePanel("Refractive index");
		nPanel.setNumber(n);
		idealLensLookalikePanel.add(nPanel, "span");

		refractiveIndexFrontPanel = new LabelledDoublePanel("Refractive index front");
		refractiveIndexFrontPanel.setNumber(refractiveIndexFront);
		idealLensLookalikePanel.add(refractiveIndexFrontPanel, "span");

		refractiveIndexBackPanel = new LabelledDoublePanel("Refractive index back");
		refractiveIndexBackPanel.setNumber(refractiveIndexBack);
		idealLensLookalikePanel.add(refractiveIndexBackPanel, "span");
		
		
		radiusPanel = new LabelledDoublePanel("radius");
		radiusPanel.setNumber(radius);
		idealLensLookalikePanel.add(radiusPanel, "span");
		
		thicknessPanel = new LabelledDoublePanel("thickness");
		thicknessPanel.setNumber(thickness);
		idealLensLookalikePanel.add(thicknessPanel, "span");
		
		stepSizePanel = new LabelledDoublePanel("stepSize");
		stepSizePanel.setNumber(stepSize);
		idealLensLookalikePanel.add(stepSizePanel, "span");
		
		pointOnIdealLensPanel = new LabelledVector3DPanel("pointOnIdealLens");
		pointOnIdealLensPanel.setVector3D(pointOnIdealLens);
		idealLensLookalikePanel.add(pointOnIdealLensPanel, "span");
		
		idealLensNormalPanel = new LabelledVector3DPanel("idealLensNormal");
		idealLensNormalPanel.setVector3D(idealLensNormal);
		idealLensLookalikePanel.add(idealLensNormalPanel, "span");
		
		pPanel = new LabelledVector3DPanel("p");
		pPanel.setVector3D(p);
		idealLensLookalikePanel.add(pPanel, "span");
		
		qPanel = new LabelledVector3DPanel("q");
		qPanel.setVector3D(q);
		idealLensLookalikePanel.add(qPanel, "span");
		
		stepsPanel = new LabelledIntPanel("steps");
		stepsPanel.setNumber(steps);
		idealLensLookalikePanel.add(stepsPanel, "span");



		//
		// Other scene-objects panel
		//

		JPanel otherObjectsPanel = new JPanel();
		otherObjectsPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera & Scene", otherObjectsPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		showImageAtIntendedPositionCheckBox = new JCheckBox("Show scene at intended position");
		showImageAtIntendedPositionCheckBox.setSelected(showImageAtIntendedPosition);
		idealLensLookalikePanel.add(showImageAtIntendedPositionCheckBox, "span");

		cameraViewCentrePanel = new LabelledVector3DPanel("Centre of view");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		otherObjectsPanel.add(cameraViewCentrePanel, "span");

		cameraPositionPanel = new LabelledVector3DPanel("Position");
		cameraPositionPanel.setVector3D(cameraPosition);
		otherObjectsPanel.add(cameraPositionPanel, "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");

		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		otherObjectsPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		

		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		otherObjectsPanel.add(cameraFocussingDistancePanel, "span");

	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		radius = radiusPanel.getNumber();
		thickness = thicknessPanel.getNumber();
		stepSize = stepSizePanel.getNumber();
		pointOnIdealLens = pointOnIdealLensPanel.getVector3D();
		idealLensNormal = idealLensNormalPanel.getVector3D();
		p = pPanel.getVector3D();
		q = qPanel.getVector3D();
		steps = stepsPanel.getNumber();
		// showVertices = showVerticesCheckbox.isSelected();
		showIdealLensLookalike = showIdealLensLookalikeCheckbox.isSelected();
		material = (Material)(materialComboBox.getSelectedItem());
		n = nPanel.getNumber();
		refractiveIndexBack = refractiveIndexBackPanel.getNumber();
		refractiveIndexFront = refractiveIndexFrontPanel.getNumber();

		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		showImageAtIntendedPosition = showImageAtIntendedPositionCheckBox.isSelected();

		// cameras
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraPosition = cameraPositionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
	}

	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		//        Runnable r = new NonInteractiveTIM();
		//
		//        EventQueue.invokeLater(r);
		(new AdamsIdealLensLookalikeTest()).run();
	}
}
