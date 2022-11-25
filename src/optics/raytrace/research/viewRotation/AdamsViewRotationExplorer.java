package optics.raytrace.research.viewRotation;

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
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.TriangulatedSurface;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;


/**
 * Based on NonInteractiveTIM.
 */
public class AdamsViewRotationExplorer extends NonInteractiveTIMEngine
{
	// units
	public static double NM = 1e-9;
	public static double UM = 1e-6;
	public static double MM = 1e-3;
	public static double CM = 1e-2;
	public static double M = 1;

	/**
	 * Number of individual "wings". These will be, angularly, evenly spaced. 
	 */
	
	private double numberOfWings;
	
	/**
	 * Camera position
	 */
	private Vector3D cameraPosition;

	public enum Material {
		MIRROR("Mirror"), COLOURED("Coloured"), TINTED("Tinted");

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
	public AdamsViewRotationExplorer()
	{
		super();

		material =
				// Material.COLOURED;
				Material.MIRROR;

		numberOfWings = 4;
		
		// other scene objects
		studioInitialisation = StudioInitialisationType.TIM_HEAD;	// the backdrop


		renderQuality = RenderQualityEnum.DRAFT;

		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// camera parameters are set in createStudio()
		cameraViewCentre = new Vector3D(0, 0, 0);	// camera is located at (0, 0, 0)
		cameraPosition = new Vector3D(0, 0, -5);
		cameraFocussingDistance = 6;
		cameraHorizontalFOVDeg = 30;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;

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

		printStream.println("material = "+material);
		printStream.println(" ----Camera----");
		printStream.println(" cD="+cameraDistance
				+ " cVD="+cameraViewDirection
				+ " cPos="+cameraPosition
				+ " cFOV="+cameraHorizontalFOVDeg
				+ " cAS="+cameraApertureSize
				+ " cFD="+cameraFocussingDistance);
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

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);


		// initialise the scene and lights
		StudioInitialisationType.initialiseSceneAndLights(
				studioInitialisation,
				scene,
				studio
				);

		// ... and then adding scene objects to scene

		SurfaceProperty surfaceProperty1;//, surfacePropertySides;
		switch(material)
		{
		case MIRROR:
			surfaceProperty1 = Reflective.PERFECT_MIRROR;

			break;
		case TINTED:
			surfaceProperty1 = new SurfaceOfTintedSolid(
					0,	// redAbsorptionCoefficient
					1.,	// greenAbsorptionCoefficient
					1.,	// blueAbsorptionCoefficient
					true	// shadowThrowing
					);
			break;
		case COLOURED:
		default:
			surfaceProperty1 = SurfaceColour.getRandom();
			//surfacePropertySides = SurfaceColour.YELLOW_SHINY;
		}



		//now the same for the back surface
		Vector3D[][] pointsOnMirrorSurface = null;
		//all the files should have the same dimension... else something will break.
		try {
			File xCoords = new File( "src/optics/raytrace/research/viewRotation/X.txt" );
			File yCoords = new File( "src/optics/raytrace/research/viewRotation/Y.txt" );
			File zCoords = new File( "src/optics/raytrace/research/viewRotation/Z.txt" );
			
			BufferedReader brX = new BufferedReader( new FileReader( xCoords ) );
			BufferedReader brY = new BufferedReader( new FileReader( yCoords ) );
			BufferedReader brZ = new BufferedReader( new FileReader( zCoords ) );


			
			//loops through all i
			int i = 0;
			String lineX = brX.readLine();
			String lineY = brY.readLine();
			String lineZ = brZ.readLine();
			            if(lineCounter(xCoords) != lineCounter(yCoords) || 
			            		lineCounter(yCoords) != lineCounter(zCoords) || lineCounter(xCoords) != lineCounter(zCoords)) {
			            	System.err.println("columns are not of the same length: x= "+lineCounter(xCoords)+", y= "+lineCounter(yCoords)+", z= "+lineCounter(zCoords));
			            }
			//get the front point cloud. for now import it from a text file.
			pointsOnMirrorSurface = new Vector3D[lineCounter(xCoords)][lineX.split("	").length];
			while (lineX != null && lineY != null && lineZ != null) {

				//loop over all j now
				String[] x = lineX.split("	");
				String[] y = lineY.split("	");
				String[] z = lineZ.split("	");

				// System.out.println("i= "+i+", lineUy "+lineY);
				if(x.length != y.length || y.length != z.length || x.length != z.length) {
					System.err.println("rows are not of the same length: x= "+x.length+", y= "+y.length+", z= "+z.length);
					break;
				}else {
					//System.out.println(x.length);
					for(int j = 0; j < (x.length); j++) {	
						pointsOnMirrorSurface[i][j] = new Vector3D(Double.parseDouble(x[j]), Double.parseDouble(y[j]), Double.parseDouble(z[j]));   

					}
				}

				lineX = brX.readLine();
				lineY = brY.readLine();
				lineZ = brZ.readLine();    
				i = i+1;
			}
			brX.close();
			brY.close();
			brZ.close();
		} catch ( IOException io ) {
			System.err.println("something does not work");
			// e.printStackTrace();
		}

		//System.out.println(pointsOnBackSurface[18][0]);
		

		for(double k = 0; k < 2*Math.PI; k += 2*Math.PI/numberOfWings) {
			addTriangulatedWing(pointsOnMirrorSurface, surfaceProperty1, k, Vector3D.Z, scene);
		}
		

		cameraViewDirection = Vector3D.difference(cameraViewCentre,cameraPosition).getNormalised();
		cameraDistance = (Vector3D.difference(cameraPosition, cameraViewCentre)).getLength();
		studio.setCamera(getStandardCamera());


	}
	
	private void addTriangulatedWing(Vector3D[][] pointsOnMirrorSurface, SurfaceProperty surfaceProperty1, double rotationAngle, Vector3D rotationAxis, SceneObjectContainer scene) {
		
		//define a new vector field of rotated positions
		Vector3D[][] rotatedSurfacePositions = new Vector3D[pointsOnMirrorSurface.length][pointsOnMirrorSurface[0].length];
		//rotate all vectors to their required given position
		for(int i = 0; i<pointsOnMirrorSurface.length; i++) {
			for(int j = 0; j<pointsOnMirrorSurface[0].length; j++) {
			rotatedSurfacePositions[i][j] = Geometry.rotate(pointsOnMirrorSurface[i][j], rotationAxis.getNormalised(), rotationAngle);
			}
		}
		
		scene.addSceneObject(new TriangulatedSurface(
				"surface at angle "+rotationAngle,	// description
				rotatedSurfacePositions,
				false, 
				surfaceProperty1,
				scene,	// parent
				studio
				));
		
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
	private JComboBox<Material> materialComboBox;
	// other scene objects
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox;
	private DoublePanel numberOfWingsPanel;

	// camera stuff
	// private LabelledVector3DPanel cameraViewDirectionPanel;
	private LabelledVector3DPanel cameraPositionPanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	private JComboBox<ApertureSizeType> cameraApertureSizeComboBox;
	private DoublePanel cameraFocussingDistancePanel;




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

		JPanel viewRotationPanel = new JPanel();
		viewRotationPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("View-rotation", viewRotationPanel);
		
		numberOfWingsPanel = new DoublePanel();
		numberOfWingsPanel.setNumber(numberOfWings);
		viewRotationPanel.add(GUIBitsAndBobs.makeRow("Use ",numberOfWingsPanel," individual wings" ),"span"); 

		materialComboBox = new JComboBox<Material>(Material.values());
		materialComboBox.setSelectedItem(material);
		viewRotationPanel.add(GUIBitsAndBobs.makeRow("Material", materialComboBox), "span");

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		viewRotationPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");


		//
		// the Camera panel
		//

		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);

		// camera stuff

		cameraViewCentrePanel = new LabelledVector3DPanel("Centre of view");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraPositionPanel = new LabelledVector3DPanel("Position");
		cameraPositionPanel.setVector3D(cameraPosition);
		cameraPanel.add(cameraPositionPanel, "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");

		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox));

		cameraFocussingDistancePanel = new DoublePanel();
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance/M);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Focussing distance",cameraFocussingDistancePanel,"m"),"span");


	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		material = (Material)(materialComboBox.getSelectedItem());
		numberOfWings = numberOfWingsPanel.getNumber();


		// other scene objects
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());

		cameraPosition = cameraPositionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber()*M;

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
		(new AdamsViewRotationExplorer()).run();
	}
}
