package optics.raytrace.GUI.sceneObjects;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import math.ODE.IntegrationType;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.surfaces.SurfaceOfLuneburgLensMetricSpace;


/**
 * A refractive-index distribution of a Luneburg lens.
 * 
 * @author Johannes
 */
public class EditableLuneburgLensMetric extends Sphere implements IPanelComponent
{
	private static final long serialVersionUID = 5900018980364840701L;


	// parameters

	/**
	 * The following variables are stored in the superclass:
	 * * the centre of the Luneburg lens, which is also the centre of the simulation sphere that contains the refractive-index distribution;
	 * * the radius of the Luneburg lens, which is also that of the simulation sphere
	 */

//
//	/**
//	 * determines if the refractive-index distribution is simulated as a smooth distribution or as spherical shells with constant refractive index
//	 */
//	private SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType;
	
	/**
	 * if the refractive-index distribution is simulated as a smooth distribution, determines which algorithm is used for numerical integration
	 */
	private IntegrationType integrationType;
	
	/**
	 * max. number of simulation steps; this is relevant for both simulation types
	 */
	private int simulationMaxSteps;

	/**
	 * if the refractive-index distribution is simulated as a smooth distribution, this parameter determines the standard step size taken
	 * (the trajectory is parametrised in terms of a parameter tau, which is not time!)
	 */
	private double deltaTau;
	
	/**
	 * if the refractive-index distribution is simulated as a smooth distribution, this parameter determines the maximum spatial step size
	 */
	private double deltaXMax;
	
//	/**
//	 * if the refractive-index distribution is simulated as spherical shells with constant refractive index, this parameter determines the
//	 * number of these shells
//	 */
//	private int numberOfSphericalLayers;
	
	/**
	 * transmission coefficient of the simulation sphere
	 */
	private double surfaceTransmissionCoefficient;
	
	/**
	 * simulation sphere throws a shadow if true; note that this shadow is not simulated properly
	 */
	// private boolean simulationSphereShadowThrowing;
	

	// local variables
		
	public EditableLuneburgLensMetric(
			String description,
			Vector3D centre,
			double simulationSphereRadius,
//			SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType,
			IntegrationType integrationType,
			int simulationMaxSteps,
			double deltaTau,
			double deltaXMax,
//			int numberOfSphericalLayers,
			double surfaceTransmissionCoefficient,
			// boolean simulationSphereShadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				description,	// description
				centre,	// centre
				simulationSphereRadius,	// radius
				null,	// placeholder --- replace in a minute
				parent, 
				studio
		);
		
//		this.simulationType = simulationType;
		this.integrationType = integrationType;
		this.simulationMaxSteps = simulationMaxSteps;
		this.deltaTau = deltaTau;
		this.deltaXMax = deltaXMax;
//		this.numberOfSphericalLayers = numberOfSphericalLayers;
		this.surfaceTransmissionCoefficient = surfaceTransmissionCoefficient;
 
		// now initialise the surface of this sphere so that it becomes a Chen-Belin black hole
		initialiseSurface();
	}

	public EditableLuneburgLensMetric(SceneObject parent, Studio studio)
	{
		this(
				"Luneburg lens",	// description
				new Vector3D(0, 0, 10),	// centre
				1,	// radius
//				SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N,	// simulationType
				IntegrationType.RK4,	// integrationType
				1000,	// simulationMaxSteps
				0.001,	// deltaTau
				0.005,	// deltaXMax
//				100,	// simulationSphereNumberOfSphericalLayers
				0.96,	// random transmission coefficient
				// false,	// shadowThrowing
				parent,
				studio
		);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableLuneburgLensMetric(EditableLuneburgLensMetric original)
	{
		this(
				original.getDescription(),	// description
				original.getCentre().clone(),	// centre
				original.getRadius(),	// simulationSphereRadius
//				original.getSimulationType(),	// simulationType
				original.getIntegrationType(),	// integrationType
				original.getSimulationMaxSteps(),	// simulationMaxSteps
				original.getDeltaTau(),	// deltaTau
				original.getDeltaXMax(),	// deltaXMax
//				original.getNumberOfSphericalLayers(),	// numberOfSphericalLayers
				original.getSimulationSphereSurfaceTransmissionCoefficient(),
				// original.isSimulationSphereShadowThrowing(),
				original.getParent(),	// parent
				original.getStudio()	// studio
			);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableLuneburgLensMetric clone()
	{
		return new EditableLuneburgLensMetric(this);
	}
	
	
	
	//
	// setters & getters
	//
	

//	public SphericallySymmetricRefractiveIndexDistributionSimulationType getSimulationType() {
//		return simulationType;
//	}
//
//	public void setSimulationType(SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType) {
//		this.simulationType = simulationType;
//	}

	public IntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(IntegrationType integrationType) {
		this.integrationType = integrationType;
	}

	public int getSimulationMaxSteps() {
		return simulationMaxSteps;
	}

	public void setSimulationMaxSteps(int simulationMaxSteps) {
		this.simulationMaxSteps = simulationMaxSteps;
	}

	public double getDeltaTau() {
		return deltaTau;
	}

	public void setDeltaTau(double deltaTau) {
		this.deltaTau = deltaTau;
	}

	public double getDeltaXMax() {
		return deltaXMax;
	}

	public void setDeltaXMax(double deltaXMax) {
		this.deltaXMax = deltaXMax;
	}

//	public int getNumberOfSphericalLayers() {
//		return numberOfSphericalLayers;
//	}
//
//	public void setNumberOfSphericalLayers(int numberOfSphericalLayers) {
//		this.numberOfSphericalLayers = numberOfSphericalLayers;
//	}

	public double getSimulationSphereSurfaceTransmissionCoefficient() {
		return surfaceTransmissionCoefficient;
	}

	public void setSimulationSphereSurfaceTransmissionCoefficient(double simulationSphereSurfaceTransmissionCoefficient) {
		this.surfaceTransmissionCoefficient = simulationSphereSurfaceTransmissionCoefficient;
	}


	private void initialiseSurface()
	{
//		switch(simulationType)
//		{
//		case SPHERICAL_SHELLS_CONSTANT_N:
//			SpaceAroundBlackHoleSphericalShells spaceAroundBlackHoleSphericalShells = new SpaceAroundBlackHoleSphericalShells(
//					horizonRadius,	// horizon radius
//					jParameter,	// jParameter
//					this,
//					numberOfSphericalLayers,	// number of shells
//					simulationMaxSteps,	// maxSimulationSteps
//					surfaceTransmissionCoefficient,	// transmission coefficient
//					false	// shadow-throwing
//					);
//			// now give the sphere that marvellous surface property
//			setSurfaceProperty(spaceAroundBlackHoleSphericalShells);
//			break;
//		case SMOOTH_N:
			SurfaceOfLuneburgLensMetricSpace surfaceOfLuneburgLens = new SurfaceOfLuneburgLensMetricSpace(
					this,	// simulationSphere
					deltaTau,	// deltaTau
					deltaXMax,
					simulationMaxSteps,	// maxSteps
					integrationType,
					surfaceTransmissionCoefficient	// transmissionCoefficient
					);
			// now give the sphere that marvellous surface property
			setSurfaceProperty(surfaceOfLuneburgLens);
//		}
	}
	
	
	//
	// GUI stuff
	//

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private JComboBox<IntegrationType> integrationTypeComboBox;
//	private JTabbedPane simulationTypeTabbedPane;
	private LabelledDoublePanel radiusPanel, deltaTauPanel, deltaXMaxPanel, surfaceTransmissionCoefficientPanel;
	private LabelledIntPanel 
//		numberOfSphericalLayersPanel,
		simulationMaxStepsPanel;


	


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Luneburg lens (metric-space simulation)"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "span");
		
		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "wrap");
		
		// start simulation-type tabbed pane
		
//		simulationTypeTabbedPane = new JTabbedPane();
//		editPanel.add(simulationTypeTabbedPane, "span");

		// the smooth-refractive-index-distribution panel

//		JPanel smoothRefractiveIndexPanel = new JPanel();
//		smoothRefractiveIndexPanel.setLayout(new MigLayout("insets 0"));		
//		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N.toString(), smoothRefractiveIndexPanel);

		integrationTypeComboBox = new JComboBox<IntegrationType>(IntegrationType.values());
		editPanel.add(GUIBitsAndBobs.makeRow("Integration type", integrationTypeComboBox), "span");

		deltaTauPanel = new LabelledDoublePanel("Delta tau");
		editPanel.add(deltaTauPanel, "wrap");
		
		deltaXMaxPanel = new LabelledDoublePanel("Delta x_max");
		editPanel.add(deltaXMaxPanel, "wrap");
		
//		// the spherical-shells panel
//		
//		JPanel sphericalShellsPanel = new JPanel();
//		sphericalShellsPanel.setLayout(new MigLayout("insets 0"));
//		// simulationTypeTabbedPane.addTab("Spherical shells with n=const.", sphericalShellsPanel);
//		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SPHERICAL_SHELLS_CONSTANT_N.toString(), sphericalShellsPanel);
//
//		numberOfSphericalLayersPanel = new LabelledIntPanel("Number of simulated refractive-index layers");
//		sphericalShellsPanel.add(numberOfSphericalLayersPanel, "wrap");
//		
//		// end simulation-type tabbed pane

		
		simulationMaxStepsPanel = new LabelledIntPanel("Max. number of simulation steps");
		editPanel.add(simulationMaxStepsPanel, "wrap");
		

		
		
		surfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		editPanel.add(surfaceTransmissionCoefficientPanel, "wrap");
		
//		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
//		editPanel.add(shadowThrowingCheckBox);

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
		centrePanel.setVector3D(getCentre());
		radiusPanel.setNumber(getRadius());
		integrationTypeComboBox.setSelectedItem(integrationType);
//		for(int i=0; i<simulationTypeTabbedPane.getTabCount(); i++)
//			if(simulationTypeTabbedPane.getTitleAt(i).equals(simulationType.toString()))
//				simulationTypeTabbedPane.setSelectedIndex(i);
		deltaTauPanel.setNumber(deltaTau);
		deltaXMaxPanel.setNumber(deltaXMax);
//		numberOfSphericalLayersPanel.setNumber(numberOfSphericalLayers);
		simulationMaxStepsPanel.setNumber(simulationMaxSteps);
		surfaceTransmissionCoefficientPanel.setNumber(surfaceTransmissionCoefficient);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableLuneburgLensMetric acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		integrationType = (IntegrationType)(integrationTypeComboBox.getSelectedItem());

//		String simulationTypeSelectedTitle = simulationTypeTabbedPane.getTitleAt(simulationTypeTabbedPane.getSelectedIndex());
//		for(SphericallySymmetricRefractiveIndexDistributionSimulationType s:SphericallySymmetricRefractiveIndexDistributionSimulationType.values())
//			if(simulationTypeSelectedTitle.equals(s.toString())) simulationType = s;
		
		deltaTau = deltaTauPanel.getNumber();
		deltaXMax = deltaXMaxPanel.getNumber();
//		numberOfSphericalLayers = numberOfSphericalLayersPanel.getNumber();
		simulationMaxSteps = simulationMaxStepsPanel.getNumber();
		surfaceTransmissionCoefficient = surfaceTransmissionCoefficientPanel.getNumber();
		
		initialiseSurface();
		
		return this;
	}
	
	@Override
	public EditableLuneburgLensMetric transform(Transformation t)
	{
		return new EditableLuneburgLensMetric(
				getDescription(),
				t.transformPosition(getCentre()),
				getRadius(),
//				simulationType,
				integrationType,
				simulationMaxSteps,
				deltaTau,
				deltaXMax,
//				numberOfSphericalLayers,
				surfaceTransmissionCoefficient,
				// simulationSphereShadowThrowing,
				getParent(), 
				getStudio()
			);
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}

//	@Override
//	public void actionPerformed(ActionEvent e)
//	{
//		System.out.println("Action!");
//		if(e.getSource().equals(homogeneousCheckBox))
//		{
//			showRelevantPanels();
//		}
//	}

//	@Override
//	public void stateChanged(ChangeEvent e)
//	{
//		System.out.println("Action!");
//		if(e.getSource().equals(homogeneousCheckBox))
//		{
//			showRelevantPanels();
//		}
//	}

}
