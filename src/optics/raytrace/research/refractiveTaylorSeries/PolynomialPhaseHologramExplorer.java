package optics.raytrace.research.refractiveTaylorSeries;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.Ray;
import optics.raytrace.core.SceneObjectPrimitive;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.Checked;
import optics.raytrace.surfaces.PhaseHologramWithPolynomialPhase;


/**
 * Polynomial-phase hologram explorer.
 * 
 * Author: Johannes
 */
public class PolynomialPhaseHologramExplorer extends NonInteractiveTIMEngine
{
	// DirectionChangingSurfaceSequence dcss;
	int polynomialOrder;
	int noOfSurfaces;
	double z[];
	double a[][][];
	double transmissionCoefficient;
	
	//  background
	private StudioInitialisationType studioInitialisation;
	private boolean addZPlane;
	private double zPlaneZ;
	private double zPlaneCheckerboardPeriod;


	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public PolynomialPhaseHologramExplorer()
	{
		super();
		
		// dcss = new DirectionChangingSurfaceSequence();
		
		polynomialOrder = 3;
		noOfSurfaces = 1;
		initialiseCoefficientArrays();
		transmissionCoefficient = 0.96;
		addSurfaces();
		
		studioInitialisation = StudioInitialisationType.TIM_HEAD;
		addZPlane = true;
		zPlaneZ = 1000;
		zPlaneCheckerboardPeriod = 100;

		// camera
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;
		cameraViewDirection = new Vector3D(0, 0, 1);
		cameraHorizontalFOVDeg = 40;
		cameraApertureSize = ApertureSizeType.PINHOLE;
		cameraFocussingDistance = 20;

		windowTitle = "Dr TIM's polynomial-phase hologram explorer";
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
				"PolynomialPhaseHologramExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		
		printStream.println("studioInitialisation="+studioInitialisation);
		printStream.println("addZPlane="+addZPlane);
		printStream.println("zPlaneZ="+zPlaneZ);
		printStream.println("zPlaneCheckerboardPeriod="+zPlaneCheckerboardPeriod);

		printStream.println("numberOfSurfaces="+noOfSurfaces);
		printStream.println("polynomialOrder="+polynomialOrder);
		printStream.println("transmissionCoefficient="+transmissionCoefficient);

		for(int i =0;i<noOfSurfaces;i++) {
			printStream.println("Surface #"+i);
			printStream.println("  z["+i+"]="+z[i]);
			for(int n=0; n<=polynomialOrder; n++)
				for(int m=0; m<=n; m++)
					printStream.println("  a["+i+"]["+n+"]["+m+"]="+a[i][n][m]);
		}
		
		printStream.println("cameraViewCentre="+cameraViewCentre);
		printStream.println("cameraDistance="+cameraDistance);
		printStream.println("cameraViewDirection="+cameraViewDirection);
		printStream.println("cameraHorizontalFOVDeg="+cameraHorizontalFOVDeg);
		printStream.println("cameraApertureSize="+cameraApertureSize);
		printStream.println("cameraFocussingDistance="+cameraFocussingDistance);

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
		studio.setCamera(getStandardCamera());

		if(addZPlane)
			scene.addSceneObject(Plane.zPlane(
					"z plane",	// description, 
					zPlaneZ,	// z0, 
					new Checked(
							DoubleColour.DARK_GREEN,	// colour1, 
							DoubleColour.LIGHT_GREEN,	// colour2,
							zPlaneCheckerboardPeriod,	// checkerWidth,
							true	// shadowThrowing
						),	// surfaceProperty, 
					scene,	// parent, 
					studio
				));
		
		addSurfaces();

		// add the surfaces from the DirectionChangingSurfaceSequence dcss to  the scene
		for(SceneObjectPrimitive s:dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces())
		{
			scene.addSceneObject(s);
		}
	}

	
	/**
	 * @param i
	 * @param n
	 * @param m
	 * @return	a[i][n][m] if it exists, otherwise 0
	 */
	public double getAOr0(int i, int n, int m)
	{
		if(i >= a.length) return 0;
		
		if(n >= a[i].length) return 0;
		
		if(m >= a[i][n].length) return 0;
		
		return a[i][n][m];
	}
	
	/**
	 * @param i
	 * @return	z[i] if it exists, otherwise 0
	 */
	public double getZOr0(int i)
	{
		if(i >= z.length) return 0;
		
		return z[i];
	}
	
	public void initialiseCoefficientArrays()
	{
		z = new double[noOfSurfaces];
		a = new double[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			z[i] = i;
			
			a[i] = new double[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				a[i][n] = new double[n+1];
				for(int m=0; m<=n; m++) 
					a[i][n][m] = 0;
			}
		}
	}	

	/**
	 * run this if the number of surfaces or the polynomial order might have changed
	 */
	public void updateCoefficientArrays()
	{
		double[] newZ = new double[noOfSurfaces];
		double[][][] newA = new double[noOfSurfaces][][];
		
		for(int i=0; i<noOfSurfaces; i++)
		{
			newZ[i] = getZOr0(i);
			
			newA[i] = new double[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				newA[i][n] = new double[n+1];
				for(int m=0; m<=n; m++) 
					newA[i][n][m] = getAOr0(i, n, m);
			}
		}

		z = newZ;
		a = newA;
	}
	
	public void randomiseCoefficientArrays()
	{
		z = new double[noOfSurfaces];
		a = new double[noOfSurfaces][][];
		
		double currentZ = 0;
		for(int i=0; i<noOfSurfaces; i++)
		{
			z[i] = currentZ;
			currentZ += Math.random();
			
			a[i] = new double[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				a[i][n] = new double[n+1];
				for(int m=0; m<=n; m++) 
					a[i][n][m] = .2*(Math.random()-0.5);
			}
		}
	}
	
	// internal variables
	private DirectionChangingSurfaceSequence dcss;
	
	public void addSurfaces()
	{
		dcss = new DirectionChangingSurfaceSequence();

		// remove any old surfaces
		// dcss.getSceneObjectPrimitivesWithDirectionChangingSurfaces().clear();
		
		// add a few surfaces
		for(int i=0; i<noOfSurfaces; i++)
		{
			Vector3D origin  = new Vector3D(0, 0, z[i]);
						
			PhaseHologramWithPolynomialPhase ppp = new PhaseHologramWithPolynomialPhase(
					a[i],	// a,
					origin,
					Vector3D.X,	// xHat,
					Vector3D.Y,	// yHat,
					transmissionCoefficient,	// throughputCoefficient,
					false,	// reflective,
					false	// shadowThrowing
				);
			Plane p = new Plane(
					"Plane #"+i,	// description,
					origin,	// pointOnPlane,
					new Vector3D(0, 0, 1),	// normal, 
					ppp,	// surfaceProperty,
					null,	// parent,
					null	// studio
				);

			dcss.addSceneObjectPrimitiveWithDirectionChangingSurface(p);
		}

	}


	//
	// for the interactive version
	//

	
	private JButton optimizeButton, updateSurfacesButton;
	private IntPanel polynomialOrderPanel, noOfSurfacesPanel;
	private LabelledDoublePanel zPanel[], transmissionCoefficientPanel;
	private DoublePanel aPanel[][][];
	private JTabbedPane surfacesTabbedPane;
	private JPanel surfacesPanel;
	
	// background
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox; 
	private JCheckBox addZPlaneCheckBox;
	private LabelledDoublePanel zPlaneZPanel;
	private LabelledDoublePanel zPlaneCheckerboardPeriodPanel;


	// camera stuff
	private LabelledDoublePanel cameraDistancePanel;
	private LabelledVector3DPanel cameraViewDirectionPanel, cameraViewCentrePanel;
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

		// the surfaces panel
		
		surfacesPanel = new JPanel();
		surfacesPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Surfaces", surfacesPanel);

		noOfSurfacesPanel = new IntPanel();
		noOfSurfacesPanel.setNumber(noOfSurfaces);
		
		polynomialOrderPanel = new IntPanel();
		polynomialOrderPanel.setNumber(polynomialOrder);
		
		updateSurfacesButton = new JButton("Update fields");
		updateSurfacesButton.setToolTipText("Update fields for parameters describing surfaces");
		updateSurfacesButton.addActionListener(this);

		// GUIBitsAndBobs.makeRow(noOfSurfacesPanel, changeSurfacesButton)
		surfacesPanel.add(
				GUIBitsAndBobs.makeRow("", noOfSurfacesPanel, "planar polynomial-phase holograms of order", polynomialOrderPanel, "", updateSurfacesButton),
				"span");
		
		surfacesTabbedPane = new JTabbedPane();
		surfacesPanel.add(surfacesTabbedPane, "span");
		
		updateSurfacesTabbedPane();
		
		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		transmissionCoefficientPanel.setNumber(transmissionCoefficient);
		surfacesPanel.add(transmissionCoefficientPanel, "span");

		// the background panel
		
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Background", backgroundPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backgroundPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");
		
		addZPlaneCheckBox = new JCheckBox("Add z plane,");
		addZPlaneCheckBox.setSelected(addZPlane);
		
		zPlaneZPanel = new LabelledDoublePanel("z = ");
		zPlaneZPanel.setNumber(zPlaneZ);
		
		zPlaneCheckerboardPeriodPanel = new LabelledDoublePanel(", checkerboard period");
		zPlaneCheckerboardPeriodPanel.setNumber(zPlaneCheckerboardPeriod);

		backgroundPanel.add(GUIBitsAndBobs.makeRow(addZPlaneCheckBox, zPlaneZPanel, zPlaneCheckerboardPeriodPanel), "span");
		
//		// the optimisation panel
//
//		JPanel optimisationPanel = new JPanel();
//		optimisationPanel.setLayout(new MigLayout("insets 0"));
//		tabbedPane.addTab("Optimisation (under construction)", optimisationPanel);
//
//		optimizeButton = new JButton("Optimize");
//		optimizeButton.setToolTipText("Run the optimisation!");
//		optimizeButton.addActionListener(this);
//		optimisationPanel.add(optimizeButton, "span");
//		
//		// the console panel
//
//		tabbedPane.addTab("Console", MessageConsole.createConsole(30, 70));
//		// create a console
//		// interactiveControlPanel.add(MessageConsole.createConsole(30, 70), "span");
		
		// the camera panel
		
		JPanel cameraPanel = new JPanel();
		cameraPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Camera", cameraPanel);

		// camera stuff
		
		cameraDistancePanel = new LabelledDoublePanel("Camera distance");
		cameraDistancePanel.setNumber(cameraDistance);
		cameraPanel.add(cameraDistancePanel, "span");
		
		cameraViewCentrePanel = new LabelledVector3DPanel("View centre");
		cameraViewCentrePanel.setVector3D(cameraViewCentre);
		cameraPanel.add(cameraViewCentrePanel, "span");
		
		cameraViewDirectionPanel = new LabelledVector3DPanel("View direction");
		cameraViewDirectionPanel.setVector3D(cameraViewDirection);
		cameraPanel.add(cameraViewDirectionPanel, "span");
		
		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");
		
		cameraApertureSizeComboBox = new JComboBox<ApertureSizeType>(ApertureSizeType.values());
		cameraApertureSizeComboBox.setSelectedItem(cameraApertureSize);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Camera aperture", cameraApertureSizeComboBox), "span");		
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("Focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		cameraPanel.add(cameraFocussingDistancePanel);
	}

	private void updateSurfacesTabbedPane()
	{
//		if(z == null) z = new double[0];
//		if(zPanel == null) zPanel = new LabelledDoublePanel[0];
		
		zPanel = new LabelledDoublePanel[polynomialOrder + 1];
		aPanel = new DoublePanel[polynomialOrder + 1][][];
		
		// remove any existing tabs
		surfacesTabbedPane.removeAll();
		
		// add new tabs
		for(int i=0; i<noOfSurfaces; i++)
		{
			JPanel surfaceNPanel = new JPanel();
			surfaceNPanel.setLayout(new MigLayout("insets 0"));
			surfacesTabbedPane.addTab("Surface #"+i, new JScrollPane(surfaceNPanel));

			zPanel[i] = new LabelledDoublePanel("z");
//			if(zPanel.length > i) newZPanel[i].setText(zPanel[i].getText());
//			else newZPanel[i].setNumber(z[i]);
			zPanel[i].setNumber(z[i]);
			surfaceNPanel.add(zPanel[i], "wrap");
			
			surfaceNPanel.add(new JLabel("<html>&Phi;(<i>x</i>, <i>y</i>) = </html>"), "wrap");
			
			// add the arrays
			aPanel[i] = new DoublePanel[polynomialOrder+1][];
			for(int n=0; n<=polynomialOrder; n++)
			{
				aPanel[i][n] = new DoublePanel[polynomialOrder+1];
				for(int m=0; m<=n; m++)
				{
					aPanel[i][n][m] = new DoublePanel();
					if(n == 0) 
					{
						aPanel[i][n][m].setBackground(Color.lightGray);
						aPanel[i][n][m].setToolTipText("This coefficient doesn't actually have any effect");
					}
					aPanel[i][n][m].setNumber(a[i][n][m]);
					surfaceNPanel.add(aPanel[i][n][m]);
					surfaceNPanel.add(new JLabel(
							"<html>" +
							((m != 0)?"<i>x</i>"+((m != 1)?"<sup>"+m+"</sup>":""):"") +
							((n-m != 0)?"<i>y</i>"+((n-m != 1)?"<sup>"+(n-m)+"</sup>":""):"") +
							"</html>"
						));
					if(m < n) surfaceNPanel.add(new JLabel("+"));
				}
				surfaceNPanel.add(new JLabel((n < polynomialOrder)?"+":""), "wrap");
			}
		}
		
		surfacesTabbedPane.revalidate();
		// surfacesPanel.revalidate();
	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();
		
		// background
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		addZPlane = addZPlaneCheckBox.isSelected();
		zPlaneZ = zPlaneZPanel.getNumber();
		zPlaneCheckerboardPeriod = zPlaneCheckerboardPeriodPanel.getNumber();

		cameraDistance = cameraDistancePanel.getNumber();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraViewDirection = cameraViewDirectionPanel.getVector3D();
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraApertureSize = (ApertureSizeType)(cameraApertureSizeComboBox.getSelectedItem());
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();

		// read the coefficient values *before* re-shaping the a arrays
		for(int i=0; i<noOfSurfaces; i++)
			for(int n=0; n<=polynomialOrder; n++)
				for(int m=0; m<=n; m++)
					a[i][n][m] = aPanel[i][n][m].getNumber();
		
		//  read the z values *before*  re-shaping the z array
		for(int i=0; i<noOfSurfaces; i++)
			z[i] = zPanel[i].getNumber();
		
		if((noOfSurfaces != noOfSurfacesPanel.getNumber()) || (polynomialOrder != polynomialOrderPanel.getNumber()))
		{
			noOfSurfaces = noOfSurfacesPanel.getNumber();
			polynomialOrder = polynomialOrderPanel.getNumber();

			updateCoefficientArrays();
			updateSurfacesTabbedPane();
		}
		
		transmissionCoefficient = transmissionCoefficientPanel.getNumber();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		
		if(e.getSource().equals(optimizeButton))
		{
			acceptValuesInInteractiveControlPanel();

			// run optimisation
			System.out.println("Starting optimization...");
			
			try {
				Ray r =  dcss.calculateTransmittedRay(new Ray(
						new Vector3D(2*(Math.random()-0.5), 2*(Math.random()-0.5), -1),
						new Vector3D(.2*(Math.random()-0.5), .2*(Math.random()-0.5), 1),
						0,
						false
					));
				System.out.println("r="+r);
			} catch (RayTraceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(e.getSource().equals(updateSurfacesButton))
		{
			acceptValuesInInteractiveControlPanel();
		}
	}

	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new PolynomialPhaseHologramExplorer()).run();
	}
}
