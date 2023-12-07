package optics.raytrace.research.refractiveTaylorSeries;

import java.awt.event.ActionEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.IntPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.Vector3DPanel;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableRuler;
import optics.raytrace.core.Studio;
import optics.raytrace.core.StudioInitialisationType;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectIntersection;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColourPositionDependent;
import optics.raytrace.surfaces.Transparent;


/**
 * Based on NonInteractiveTIM.
 */
public class WedgeExplorer extends NonInteractiveTIMEngine
{
	private Vector3D cameraCentre;

	private int numberOfSurfaces;

	//the surface position, normal and refractive index ratio (n_inside/n_outside).
	private Vector3D[] surfacePosition;
	private Vector3D[] surfaceNormal;
	private double[] n;

	private double transmissionCoef;

	private boolean restrictSize;

	private boolean makeSidesTransparent;
	
	//Backdrop
	private StudioInitialisationType studioInitialisation;
	//rainbow plane
	private boolean isIndependentDirection, addRainbowPlane;
	private Vector3D independentDirection;
	private double planeDistance, stripePeriod;
	//lattice
	private boolean addLattice;
	private Vector3D latticeCentre;
	private double latticeLength, latticeSize, cylinderRadius;
	private int latticePeriod;
	//ruler
	private boolean addRuler, onlyPos;
	private Vector3D rulerPosDirection, rulerDivisionUpSpanVector, rulerCentre;
	private double rulerLength, lineThickness;
	private double devisionNumber;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public WedgeExplorer()
	{
		super();

		// camera shift
		cameraCentre = new Vector3D (0,0,-10);

		//wedge parameters
		numberOfSurfaces = 3;


		restrictSize = true;
		makeSidesTransparent = false;
		transmissionCoef = 0.98;

		surfacePosition = new Vector3D[numberOfSurfaces];
		surfaceNormal = new Vector3D[numberOfSurfaces];
		n = new double[numberOfSurfaces];

		for (int i = 0; i<numberOfSurfaces;i++) {
			surfacePosition[i] = new Vector3D(0,0,0.5*i);
			surfaceNormal[i] = new Vector3D(0,0,-1);
			n[i] = 1.1;
		}

		//Backdrop
		studioInitialisation = StudioInitialisationType.HEAVEN;
		//rainbow plane
		addRainbowPlane = true;
		isIndependentDirection = true;
		independentDirection = new Vector3D(1,1,0);
		planeDistance = 30;
		stripePeriod = 2;
		//lattice
		addLattice = false;
		latticeCentre = new Vector3D(0,0,32.5);
		latticeLength = 5;
		latticeSize = 2;
		cylinderRadius=0.05;
		latticePeriod = 4;
		//ruler
		addRuler = false;
		onlyPos = false;
		rulerPosDirection = Vector3D.X;
		rulerDivisionUpSpanVector = new Vector3D(0,0.6,0);
		rulerCentre = new Vector3D(0,0,29.99);
		rulerLength = 4;
		lineThickness = 0.2;
		devisionNumber = 1;

		//camera params
		cameraHorizontalFOVDeg = 20;

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
				"WedgeExplorer"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		printStream.println(" ----Wedge----");
		printStream.println(" numberOfSurfaces="+numberOfSurfaces);
		printStream.println(" restrictSize="+restrictSize);
		printStream.println(" makeSidesTransparent="+makeSidesTransparent);
		printStream.println(" transmissionCoef="+transmissionCoef);
		
		for(int i =0;i<numberOfSurfaces;i++) {
			printStream.println(" surfacePosition of wedge"+(i+1)+" ="+surfacePosition[i]);
			printStream.println(" surfaceNormal of wedge"+(i+1)+" ="+surfaceNormal[i]);
			printStream.println(" n of wedge"+(i+1)+" ="+n[i]);
		}

		
		printStream.println(" ----Backdrop----");
		printStream.println(" studioInitialisation="+studioInitialisation);
		
		printStream.println(" Rainbow plane"+ addRainbowPlane);
		printStream.println(" isIndependentDirection="+isIndependentDirection);
		printStream.println(" independentDirection="+independentDirection);
		printStream.println(" planeDistance="+planeDistance);
		printStream.println(" stripePeriod="+stripePeriod);

		printStream.println(" Lattice"+ addLattice);
		printStream.println(" latticeCentre"+ latticeCentre);
		printStream.println(" latticeLength"+ latticeLength);
		printStream.println(" latticeSize"+ latticeSize);
		printStream.println(" cylinderRadius"+ cylinderRadius);
		printStream.println(" latticePeriod"+ latticePeriod);
		
		printStream.println(" Ruler"+ addRuler);
		printStream.println(" onlyPositiveAxis"+ onlyPos);
		printStream.println(" rulerPosDirection"+ rulerPosDirection);
		printStream.println(" rulerDivisionUpSpanVector"+ rulerDivisionUpSpanVector);
		printStream.println(" rulerCentre"+ rulerCentre);
		printStream.println(" rulerLength"+ rulerLength);
		printStream.println(" lineThickness"+ lineThickness);
		printStream.println(" devisionNumber"+ devisionNumber);
		
		
		printStream.println(" ----Camera----");
		printStream.println(" cD="+cameraDistance
				+ " cVD="+cameraViewDirection
				+ " cPos="+cameraCentre
				+ " cVC="+cameraViewCentre);
		printStream.println("camera FOV "+cameraHorizontalFOVDeg);

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




		// ... and then adding the refractive surfaces to the scene
		for (int i=0; i<numberOfSurfaces; i++ ) {
			SceneObjectIntersection wedge = new SceneObjectIntersection("surface "+(i+1), null, null);

			Plane wedgePlane = new Plane(
					"surface "+(i+1),
					surfacePosition[i],
					surfaceNormal[i].getNormalised(),
					//new Transparent(),
					new RefractiveSimple(n[i], transmissionCoef, false),
					scene,
					studio
					);
			wedge.addPositiveSceneObject(wedgePlane);


			if(restrictSize) {

				double indexRatio = 1; 
				for(int j=0; j<=i;j++) {
					indexRatio = indexRatio*n[i];
				}
				//This should give the index ratio of n_i/n_air which is what we want

				SurfaceProperty sideSurfaceProperty;
				if(makeSidesTransparent) {
					sideSurfaceProperty = new Transparent();
				}else {
					sideSurfaceProperty = new RefractiveSimple(indexRatio, transmissionCoef, false);
				}

				Plane cuttingPlane1 = new Plane(
						"side 1 of surface "+(i+1),
						Vector3D.Y,
						Vector3D.Y,
						sideSurfaceProperty,
						null,
						null
						);

				Plane cuttingPlane2 = new Plane(
						"side 2 of surface "+(i+1),
						Vector3D.X.getProductWith(-1),
						Vector3D.X.getProductWith(-1),
						sideSurfaceProperty,
						null,
						null
						);

				Plane cuttingPlane3 = new Plane(
						"side 3 of surface "+(i+1),
						Vector3D.X,
						Vector3D.X,
						sideSurfaceProperty,
						null,
						null
						);

				Plane cuttingPlane4 = new Plane(
						"side 4 of surface "+(i+1),
						Vector3D.Y.getProductWith(-1),
						Vector3D.Y.getProductWith(-1),
						sideSurfaceProperty,
						null,
						null
						);

				//System.out.println(indexRatio);
				wedge.addPositiveSceneObject(cuttingPlane1);
				wedge.addPositiveSceneObject(cuttingPlane2);
				wedge.addPositiveSceneObject(cuttingPlane3);
				wedge.addPositiveSceneObject(cuttingPlane4);


				//restrict the length if it is not the last wedge to make sure sides do not interfere. 
				if(i+1<numberOfSurfaces) {
					Plane wedgePlaneEnd = new Plane(
							"surface end"+(i+2),
							surfacePosition[i+1],
							surfaceNormal[i+1].getNormalised(),
							//new Transparent(),
							new RefractiveSimple(n[i+1], transmissionCoef, false),
							null,
							null
							);
					wedge.addInvisibleNegativeSceneObject(wedgePlaneEnd);
				}
				//					else {
				//						Plane negativeWedgePlane = new Plane(
				//								"back end",
				//								new Vector3D(0,0,20),
				//								Vector3D.Z,
				//								new RefractiveSimple(1/indexRatio, transmissionCoef, false),
				//								null,
				//								null
				//								);
				//						wedge.addPositiveSceneObject(negativeWedgePlane);
				//					}

			}

			scene.addSceneObject(wedge);

		}

		//Add the rainbow background
		scene.addSceneObject(new Plane(						
				"rainbow backdrop ",
				new Vector3D(0,0,planeDistance),
				Vector3D.Z,
				//					new Rainbow(
				//							1,	// saturation
				//							.25,	// lightness
				//							new Vector3D(100,300,-500)
				//							),
				//					new Transparent(),
				//					new SurfaceColourPositionDependent(2, new Vector3D(0,0,30), new Vector3D(1,0,0), false, false, false),
				new SurfaceColourPositionDependent(stripePeriod, new Vector3D(0,0,planeDistance), independentDirection, isIndependentDirection, false, false),
				scene,
				studio
				), addRainbowPlane);
		
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-0.5*latticeSize+latticeCentre.x, 0.5*latticeSize+latticeCentre.x, latticePeriod, Vector3D.X,
				-0.5*latticeSize+latticeCentre.y, 0.5*latticeSize+latticeCentre.y, latticePeriod, Vector3D.Y,
				-0.5*latticeLength+latticeCentre.z, 0.5*latticeLength+latticeCentre.z, latticePeriod, Vector3D.Z, // this puts the "transverse" cylinders into the planes z=10, 20, 30, 40
				cylinderRadius,
				scene,
				studio
				), addLattice);
		
		scene.addSceneObject(new EditableRuler(
				"Ruler",// description,
				rulerCentre,// centre,
				onlyPos,// onlyPositive,
				rulerPosDirection,// positiveDirection,
				rulerDivisionUpSpanVector,// rulerDivisionUpSpanVector,
				rulerLength,// maxLength, 
				lineThickness/2,// cylinderRadius,
				devisionNumber,// nDivisions,
				false,// shadowThrowing,
				scene,// parent, 
				studio// studio
				), addRuler);

		cameraDistance = Vector3D.getDistance(cameraCentre, cameraViewCentre);
		cameraViewDirection = Vector3D.difference(cameraViewCentre, cameraCentre).getNormalised();
		studio.setCamera(getStandardCamera());


	}


	//
	// for the interactive version
	//

	//camera stuff
	private LabelledVector3DPanel cameraCentrePanel, cameraViewCentrePanel;
	private DoublePanel cameraHorizontalFOVDegPanel;
	//wedge stuff
	private LabelledVector3DPanel[] surfacePositionPanel, surfaceNormalPanel;
	private DoublePanel[] nPanel;
	private IntPanel numberOfSurfacesPanel;
	private JCheckBox restrictSizeCheckBox, makeSidesTransparentCheckBox;
	private JPanel wedgePanels[];
	private JPanel wedgePanel, surfaceParamPanel;
	private JButton updateIButton;

	//backdrop stuff
	private JComboBox<StudioInitialisationType> studioInitialisationComboBox; 
	private DoublePanel	planeDistancePanel, stripePeriodPanel, latticeLengthPanel, latticeSizePanel, cylinderRadiusPanel, rulerLengthPanl, lineThicknessPanel, devisionNumberPanel;
	private JCheckBox isIndependentDirectionCheckBox, addRainbowPlaneCheckBox, addLatticeCheckBox, addRulerCheckBox, onlyPosCheckBox;
	private LabelledVector3DPanel independentDirectionPanel;
	private Vector3DPanel latticeCentrePanel, rulerPosDirectionPanel, rulerDivisionUpSpanVectorPanel, rulerCentrePanel;
	private IntPanel latticePeriodPanel;
	



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

		// the refractive surfaces panel

		wedgePanel = new JPanel();
		wedgePanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("wedges", wedgePanel);

		numberOfSurfacesPanel = new IntPanel();
		numberOfSurfacesPanel.setNumber(numberOfSurfaces);


		updateIButton = new JButton("Update");
		updateIButton.setToolTipText("Update this before rendering!");
		updateIButton.addActionListener(this);

		wedgePanel.add(GUIBitsAndBobs.makeRow("number of surfaces", numberOfSurfacesPanel, " ", updateIButton), "span");


		restrictSizeCheckBox = new JCheckBox();
		restrictSizeCheckBox.setSelected(restrictSize);

		makeSidesTransparentCheckBox = new JCheckBox();
		makeSidesTransparentCheckBox.setSelected(makeSidesTransparent);

		wedgePanel.add(GUIBitsAndBobs.makeRow(" restrict size", restrictSizeCheckBox, " Make sides transparent", makeSidesTransparentCheckBox), "span");


		surfaceParamPanel = new JPanel();
		surfaceParamPanel.setLayout(new MigLayout("insets 0"));
		surfaceParamPanel.setBorder(GUIBitsAndBobs.getTitledBorder("surface parameters"));

		wedgePanels = new JPanel[numberOfSurfaces];
		surfacePositionPanel = new LabelledVector3DPanel[numberOfSurfaces];
		surfaceNormalPanel = new LabelledVector3DPanel[numberOfSurfaces];
		nPanel = new DoublePanel[numberOfSurfaces];
		for(int i=0; i<numberOfSurfaces; i++) {

			wedgePanels[i] = new JPanel();
			wedgePanels[i].setLayout(new MigLayout("insets 0"));
			wedgePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("surface #"+(i+1)));

			nPanel[i] = new DoublePanel();
			nPanel[i].setNumber(n[i]);
			wedgePanels[i].add(GUIBitsAndBobs.makeRow("index ratio", nPanel[i]), "span");


			surfacePositionPanel[i] = new LabelledVector3DPanel("surface centre");
			surfacePositionPanel[i].setVector3D(surfacePosition[i]);
			wedgePanels[i].add(surfacePositionPanel[i], "span");

			surfaceNormalPanel[i] = new LabelledVector3DPanel("surface normal");
			surfaceNormalPanel[i].setVector3D(surfaceNormal[i]);
			wedgePanels[i].add(surfaceNormalPanel[i], "span");

			surfaceParamPanel.add(wedgePanels[i], "wrap");

			wedgePanel.add(surfaceParamPanel,"wrap");
		}

		//
		//Backdrop panel
		//
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setLayout(new MigLayout("insets 0"));
		tabbedPane.addTab("Background", backgroundPanel);

		studioInitialisationComboBox = new JComboBox<StudioInitialisationType>(StudioInitialisationType.limitedValuesForBackgrounds);
		studioInitialisationComboBox.setSelectedItem(studioInitialisation);
		backgroundPanel.add(GUIBitsAndBobs.makeRow("Initialise backdrop to", studioInitialisationComboBox), "span");

		JPanel rainbowPanel = new JPanel();
		rainbowPanel.setLayout(new MigLayout("insets 0"));
		rainbowPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Rainbow plane"));

		addRainbowPlaneCheckBox = new JCheckBox();
		addRainbowPlaneCheckBox.setSelected(addRainbowPlane);
		
		planeDistancePanel = new DoublePanel();
		planeDistancePanel.setNumber(planeDistance);		
		
		rainbowPanel.add(GUIBitsAndBobs.makeRow(" add a rainbow backdrop", addRainbowPlaneCheckBox, " at distance", planeDistancePanel), "span");

		stripePeriodPanel = new DoublePanel();
		stripePeriodPanel.setNumber(stripePeriod);		
		rainbowPanel.add(GUIBitsAndBobs.makeRow(" rainbow period", stripePeriodPanel), "span");

		isIndependentDirectionCheckBox = new JCheckBox();
		isIndependentDirectionCheckBox.setSelected(isIndependentDirection);
		rainbowPanel.add(GUIBitsAndBobs.makeRow(" add directional dependancy", isIndependentDirectionCheckBox), "span");
		
		independentDirectionPanel = new LabelledVector3DPanel("direction dependance");
		independentDirectionPanel.setVector3D(independentDirection);
		rainbowPanel.add(independentDirectionPanel, "span");
		
		backgroundPanel.add(rainbowPanel, "wrap");

		JPanel latticePanel = new JPanel();
		latticePanel.setLayout(new MigLayout("insets 0"));
		latticePanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lattice"));

		addLatticeCheckBox = new JCheckBox();
		addLatticeCheckBox.setSelected(addLattice); 	
		
		latticeCentrePanel = new Vector3DPanel();
		latticeCentrePanel.setVector3D(latticeCentre);
		
		latticePanel.add(GUIBitsAndBobs.makeRow(" add lattice", addLatticeCheckBox, " at ", latticeCentrePanel), "span");

		latticeLengthPanel = new DoublePanel();
		latticeLengthPanel.setNumber(latticeLength);
		
		latticeSizePanel= new DoublePanel();
		latticeSizePanel.setNumber(latticeSize);
		
		latticePanel.add(GUIBitsAndBobs.makeRow(" length", latticeLengthPanel, " and size", latticeSizePanel), "span");
		
		cylinderRadiusPanel = new DoublePanel();
		cylinderRadiusPanel.setNumber(cylinderRadius);
		
		latticePeriodPanel = new IntPanel();
		latticePeriodPanel.setNumber(latticePeriod);
		
		latticePanel.add(GUIBitsAndBobs.makeRow(" Cylinder radius", cylinderRadiusPanel, "with period", latticePeriodPanel), "span");
		
		backgroundPanel.add(latticePanel, "wrap");
		
		JPanel rulerPanel = new JPanel();
		rulerPanel.setLayout(new MigLayout("insets 0"));
		rulerPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ruler"));
		
		addRulerCheckBox = new JCheckBox();
		addRulerCheckBox.setSelected(addRuler); 	
		
		rulerCentrePanel = new Vector3DPanel();
		rulerCentrePanel.setVector3D(rulerCentre);
		
		rulerPanel.add(GUIBitsAndBobs.makeRow(" add ruler", addRulerCheckBox, " at ", rulerCentrePanel), "span");
		
		onlyPosCheckBox = new JCheckBox();
		onlyPosCheckBox.setSelected(onlyPos); 
		
		rulerPosDirectionPanel = new Vector3DPanel();
		rulerPosDirectionPanel.setVector3D(rulerPosDirection);	
		
		
		rulerPanel.add(GUIBitsAndBobs.makeRow("only positive direction",onlyPosCheckBox," along", rulerPosDirectionPanel), "span");
		
		rulerDivisionUpSpanVectorPanel = new Vector3DPanel();
		rulerDivisionUpSpanVectorPanel.setVector3D(rulerDivisionUpSpanVector);
		
		rulerPanel.add(GUIBitsAndBobs.makeRow("Division up span vector", rulerDivisionUpSpanVectorPanel), "span");
		
		rulerLengthPanl = new DoublePanel();
		rulerLengthPanl.setNumber(rulerLength);
		
		lineThicknessPanel = new DoublePanel();
		lineThicknessPanel.setNumber(lineThickness);
		

		rulerPanel.add(GUIBitsAndBobs.makeRow("ruler line thickness", lineThicknessPanel, "and length", rulerLengthPanl), "span");
		
		devisionNumberPanel = new DoublePanel();
		devisionNumberPanel.setNumber(devisionNumber);
		
		rulerPanel.add(GUIBitsAndBobs.makeRow("accuracy (an int number of divisions)", devisionNumberPanel), "span");
		
		backgroundPanel.add(rulerPanel, "wrap");

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

		cameraCentrePanel = new LabelledVector3DPanel("aperture Centre");
		cameraCentrePanel.setVector3D(cameraCentre);
		cameraPanel.add(cameraCentrePanel, "span");

		cameraHorizontalFOVDegPanel = new DoublePanel();
		cameraHorizontalFOVDegPanel.setNumber(cameraHorizontalFOVDeg);
		cameraPanel.add(GUIBitsAndBobs.makeRow("Horizontal FOV", cameraHorizontalFOVDegPanel, "°"), "span");



	}

	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		if(numberOfSurfacesPanel.getNumber() != surfaceParamPanel.getComponentCount()) {
			numberOfSurfacesPanel.setNumber(numberOfSurfaces);
			System.err.println("Please press Upade before you render");
		}else {
			numberOfSurfaces = numberOfSurfacesPanel.getNumber();
		}

		restrictSize = restrictSizeCheckBox.isSelected();
		makeSidesTransparent = makeSidesTransparentCheckBox.isSelected();


		for(int i =0; i<numberOfSurfaces; i++) {
			surfaceNormal[i] = surfaceNormalPanel[i].getVector3D();
			surfacePosition[i] = surfacePositionPanel[i].getVector3D();
			n[i] = nPanel[i].getNumber();
		}


		//backdrop
		studioInitialisation = (StudioInitialisationType)(studioInitialisationComboBox.getSelectedItem());
		planeDistance = planeDistancePanel.getNumber();
		stripePeriod = stripePeriodPanel.getNumber();
		isIndependentDirection = isIndependentDirectionCheckBox.isSelected();
		addRainbowPlane = addRainbowPlaneCheckBox.isSelected();
		independentDirection = independentDirectionPanel.getVector3D();
		latticeLength = latticeLengthPanel.getNumber();
		latticeSize = latticeSizePanel.getNumber();
		cylinderRadius = cylinderRadiusPanel.getNumber();
		addLattice = addLatticeCheckBox.isSelected();
		latticeCentre = latticeCentrePanel.getVector3D();
		latticePeriod = latticePeriodPanel.getNumber();
		rulerLength = rulerLengthPanl.getNumber();
		lineThickness = lineThicknessPanel.getNumber();
		addRuler = addRulerCheckBox.isSelected();
		onlyPos = onlyPosCheckBox.isSelected();
		rulerPosDirection = rulerPosDirectionPanel.getVector3D();
		rulerDivisionUpSpanVector = rulerDivisionUpSpanVectorPanel.getVector3D();
		rulerCentre = rulerCentrePanel.getVector3D();
		devisionNumber = devisionNumberPanel.getNumber();
		
		
		//camera
		cameraHorizontalFOVDeg = cameraHorizontalFOVDegPanel.getNumber();
		cameraViewCentre = cameraViewCentrePanel.getVector3D();
		cameraCentre = cameraCentrePanel.getVector3D();


	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(updateIButton))
		{
			surfaceParamPanel.removeAll();			

			surfacePosition = new Vector3D[numberOfSurfacesPanel.getNumber()];
			surfaceNormal = new Vector3D[numberOfSurfacesPanel.getNumber()];
			n = new double[numberOfSurfacesPanel.getNumber()];

			for (int i = 0; i<numberOfSurfacesPanel.getNumber();i++) {
				surfacePosition[i] = new Vector3D(0,0,0.5*i);
				surfaceNormal[i] = new Vector3D(0,0,-1);
				n[i] = 1.1;
			}

			wedgePanels = new JPanel[numberOfSurfacesPanel.getNumber()];
			surfacePositionPanel = new LabelledVector3DPanel[numberOfSurfacesPanel.getNumber()];
			surfaceNormalPanel = new LabelledVector3DPanel[numberOfSurfacesPanel.getNumber()];
			nPanel = new DoublePanel[numberOfSurfacesPanel.getNumber()];

			for(int i =0; i<numberOfSurfacesPanel.getNumber(); i++) {

				wedgePanels[i] = new JPanel();
				wedgePanels[i].setLayout(new MigLayout("insets 0"));
				wedgePanels[i].setBorder(GUIBitsAndBobs.getTitledBorder("surface #"+(i+1)));

				nPanel[i] = new DoublePanel();
				nPanel[i].setNumber(n[i]);
				wedgePanels[i].add(GUIBitsAndBobs.makeRow("index ratio", nPanel[i]), "span");


				surfacePositionPanel[i] = new LabelledVector3DPanel("surface centre");
				surfacePositionPanel[i].setVector3D(surfacePosition[i]);
				wedgePanels[i].add(surfacePositionPanel[i], "span");

				surfaceNormalPanel[i] = new LabelledVector3DPanel("surface normal");
				surfaceNormalPanel[i].setVector3D(surfaceNormal[i]);
				wedgePanels[i].add(surfaceNormalPanel[i], "span");

				surfaceParamPanel.add(wedgePanels[i], "wrap");

			}
			surfaceParamPanel.revalidate();
		}
		super.actionPerformed(e);
	}

	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		(new WedgeExplorer()).run();
	}
}
