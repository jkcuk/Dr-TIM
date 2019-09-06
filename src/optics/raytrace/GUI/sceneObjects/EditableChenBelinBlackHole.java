package optics.raytrace.GUI.sceneObjects;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
import optics.raytrace.research.TO.ChenBelinBlackHole.SpaceAroundBlackHoleSphericalShells;
import optics.raytrace.sceneObjects.Sphere;
import optics.raytrace.surfaces.SurfaceOfChenBelinMetricSpace;


/**
 * A refractive-index distribution that simulates the space around a black hole.
 * This was proposed by Huanyang Chen (Kenyon) and modified by Jakub Belin.
 * The refractive-index distribution can be simulated either as a smooth refractive-index distribution,
 * or as a number of spherical shells, each with constant refractive index.
 * In each case, the refractive-index distribution is contained inside a sphere that is centred on the black hole, the "simulation sphere".
 * 
 * @author Johannes
 */
public class EditableChenBelinBlackHole extends Sphere implements IPanelComponent
{
	private static final long serialVersionUID = 2685337211408089067L;

	// parameters

	/**
	 * The following variables are stored in the superclass:
	 * * the centre of the black hole, which is also the centre of the simulation sphere that contains the Chen-Belin refractive-index distribution;
	 * * the radius of the simulation sphere, which is centred on the black hole and which contains the Chen-Belin refractive-index distribution
	 */


	/**
	 * the radius of the black hole's event horizon
	 */
	private double horizonRadius;
	
	/**
	 * the "j" parameter of the refractive-index distribution;
	 * j=1 gives Kenyon's original refractive-index distribution, which corresponds to a photon-sphere radius of horizonRadius*(2+sqrt(3));
	 * j=0.6 gives Jakub's modified refractive-index distribution, which corresponds to a photon-sphere radius of 3*horizonRadius (like a Schwarzschild black hole)
	 */
	private double jParameter;

	/**
	 * determines if the refractive-index distribution is simulated as a smooth distribution or as spherical shells with constant refractive index
	 */
	private SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType;
	
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
	
	/**
	 * if the refractive-index distribution is simulated as spherical shells with constant refractive index, this parameter determines the
	 * number of these shells
	 */
	private int numberOfSphericalLayers;
	
	/**
	 * transmission coefficient of the simulation sphere
	 */
	private double simulationSphereSurfaceTransmissionCoefficient;
	
	/**
	 * simulation sphere throws a shadow if true; note that this shadow is not simulated properly
	 */
	// private boolean simulationSphereShadowThrowing;
	

	// local variables
		
	public EditableChenBelinBlackHole(
			String description,
			Vector3D centre,
			double horizonRadius,
			double jParameter,
			double simulationSphereRadius,
			SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType,
			IntegrationType integrationType,
			int simulationMaxSteps,
			double deltaTau,
			double deltaXMax,
			int numberOfSphericalLayers,
			double simulationSphereSurfaceTransmissionCoefficient,
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
		
		this.horizonRadius = horizonRadius;
		this.jParameter = jParameter;
		this.simulationType = simulationType;
		this.integrationType = integrationType;
		this.simulationMaxSteps = simulationMaxSteps;
		this.deltaTau = deltaTau;
		this.deltaXMax = deltaXMax;
		this.numberOfSphericalLayers = numberOfSphericalLayers;
		this.simulationSphereSurfaceTransmissionCoefficient = simulationSphereSurfaceTransmissionCoefficient;
 
		// now initialise the surface of this sphere so that it becomes a Chen-Belin black hole
		initialiseSurface();
	}

	public EditableChenBelinBlackHole(SceneObject parent, Studio studio)
	{
		this(
				"Chen-Belin black hole",	// description
				new Vector3D(0, 0, 10),	// centre
				0.01,	// horizonRadius
				0.6,	// jParameter
				1,	// simulationSphereRadius
				SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N,	// simulationType
				IntegrationType.RK4,	// integrationType
				1000,	// simulationMaxSteps
				0.001,	// deltaTau
				0.005,	// deltaXMax
				100,	// simulationSphereNumberOfSphericalLayers
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
	public EditableChenBelinBlackHole(EditableChenBelinBlackHole original)
	{
		this(
				original.getDescription(),	// description
				original.getCentre().clone(),	// centre
				original.getHorizonRadius(),	// horizonRadius
				original.getjParameter(),	// jParameter
				original.getSimulationSphereRadius(),	// simulationSphereRadius
				original.getSimulationType(),	// simulationType
				original.getIntegrationType(),	// integrationType
				original.getSimulationMaxSteps(),	// simulationMaxSteps
				original.getDeltaTau(),	// deltaTau
				original.getDeltaXMax(),	// deltaXMax
				original.getNumberOfSphericalLayers(),	// numberOfSphericalLayers
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
	public EditableChenBelinBlackHole clone()
	{
		return new EditableChenBelinBlackHole(this);
	}
	
	
	
	//
	// setters & getters
	//
	
	public double getHorizonRadius() {
		return horizonRadius;
	}

	public void setHorizonRadius(double horizonRadius) {
		this.horizonRadius = horizonRadius;
	}

	public double getjParameter() {
		return jParameter;
	}

	public void setjParameter(double jParameter) {
		this.jParameter = jParameter;
	}

	public double getSimulationSphereRadius() {
		return getRadius();
	}

	public void setSimulationSphereRadius(double simulationSphereRadius) {
		setRadius(simulationSphereRadius);
	}

	public SphericallySymmetricRefractiveIndexDistributionSimulationType getSimulationType() {
		return simulationType;
	}

	public void setSimulationType(SphericallySymmetricRefractiveIndexDistributionSimulationType simulationType) {
		this.simulationType = simulationType;
	}

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

	public int getNumberOfSphericalLayers() {
		return numberOfSphericalLayers;
	}

	public void setNumberOfSphericalLayers(int numberOfSphericalLayers) {
		this.numberOfSphericalLayers = numberOfSphericalLayers;
	}

	public double getSimulationSphereSurfaceTransmissionCoefficient() {
		return simulationSphereSurfaceTransmissionCoefficient;
	}

	public void setSimulationSphereSurfaceTransmissionCoefficient(double simulationSphereSurfaceTransmissionCoefficient) {
		this.simulationSphereSurfaceTransmissionCoefficient = simulationSphereSurfaceTransmissionCoefficient;
	}


	private void initialiseSurface()
	{
		switch(simulationType)
		{
		case SPHERICAL_SHELLS_CONSTANT_N:
			SpaceAroundBlackHoleSphericalShells spaceAroundBlackHoleSphericalShells = new SpaceAroundBlackHoleSphericalShells(
					horizonRadius,	// horizon radius
					jParameter,	// jParameter
					this,
					numberOfSphericalLayers,	// number of shells
					simulationMaxSteps,	// maxSimulationSteps
					simulationSphereSurfaceTransmissionCoefficient,	// transmission coefficient
					false	// shadow-throwing
					);
			// now give the sphere that marvellous surface property
			setSurfaceProperty(spaceAroundBlackHoleSphericalShells);
			break;
		case SMOOTH_N:
			SurfaceOfChenBelinMetricSpace spaceAroundBlackHoleMetric = new SurfaceOfChenBelinMetricSpace(
					horizonRadius,	// horizonRadius
					this,	// simulationSphere
					jParameter,	// jParameter
					deltaTau,	// deltaTau
					deltaXMax,
					simulationMaxSteps,	// maxSteps
					integrationType,
					simulationSphereSurfaceTransmissionCoefficient	// transmissionCoefficient
					);
			// now give the sphere that marvellous surface property
			setSurfaceProperty(spaceAroundBlackHoleMetric);
		}
	}
	
	
	//
	// GUI stuff
	//

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private JComboBox<IntegrationType> integrationTypeComboBox;
	private JTabbedPane simulationTypeTabbedPane;
	private LabelledDoublePanel horizonRadiusPanel, jParameterPanel, simulationSphereRadiusPanel, deltaTauPanel, deltaXMaxPanel, simulationSphereSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfSphericalLayersPanel, simulationMaxStepsPanel;


	


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Chen-Belin black hole"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "span");

		horizonRadiusPanel = new LabelledDoublePanel("Horizon radius");
		editPanel.add(horizonRadiusPanel, "wrap");
		
		jParameterPanel = new LabelledDoublePanel("J parameter");
		editPanel.add(jParameterPanel, "wrap");
		
		simulationSphereRadiusPanel = new LabelledDoublePanel("Radius of sphere containing refracive-index distribution");
		editPanel.add(simulationSphereRadiusPanel, "wrap");
		
		// start simulation-type tabbed pane
		
		simulationTypeTabbedPane = new JTabbedPane();
		editPanel.add(simulationTypeTabbedPane, "span");

		// the smooth-refractive-index-distribution panel

		JPanel smoothRefractiveIndexPanel = new JPanel();
		smoothRefractiveIndexPanel.setLayout(new MigLayout("insets 0"));		
		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SMOOTH_N.toString(), smoothRefractiveIndexPanel);

		integrationTypeComboBox = new JComboBox<IntegrationType>(IntegrationType.values());
		smoothRefractiveIndexPanel.add(GUIBitsAndBobs.makeRow("Integration type", integrationTypeComboBox), "span");

		deltaTauPanel = new LabelledDoublePanel("Delta tau");
		smoothRefractiveIndexPanel.add(deltaTauPanel, "wrap");
		
		deltaXMaxPanel = new LabelledDoublePanel("Delta x_max");
		smoothRefractiveIndexPanel.add(deltaXMaxPanel, "wrap");
		
		// the spherical-shells panel
		
		JPanel sphericalShellsPanel = new JPanel();
		sphericalShellsPanel.setLayout(new MigLayout("insets 0"));
		// simulationTypeTabbedPane.addTab("Spherical shells with n=const.", sphericalShellsPanel);
		simulationTypeTabbedPane.addTab(SphericallySymmetricRefractiveIndexDistributionSimulationType.SPHERICAL_SHELLS_CONSTANT_N.toString(), sphericalShellsPanel);

		numberOfSphericalLayersPanel = new LabelledIntPanel("Number of simulated refractive-index layers");
		sphericalShellsPanel.add(numberOfSphericalLayersPanel, "wrap");
		
		// end simulation-type tabbed pane

		
		simulationMaxStepsPanel = new LabelledIntPanel("Max. number of simulation steps");
		editPanel.add(simulationMaxStepsPanel, "wrap");
		

		
		
		simulationSphereSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		editPanel.add(simulationSphereSurfaceTransmissionCoefficientPanel, "wrap");
		
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
		horizonRadiusPanel.setNumber(horizonRadius);
		jParameterPanel.setNumber(jParameter);
		simulationSphereRadiusPanel.setNumber(getRadius());
		integrationTypeComboBox.setSelectedItem(integrationType);
		for(int i=0; i<simulationTypeTabbedPane.getTabCount(); i++)
			if(simulationTypeTabbedPane.getTitleAt(i).equals(simulationType.toString()))
				simulationTypeTabbedPane.setSelectedIndex(i);
		deltaTauPanel.setNumber(deltaTau);
		deltaXMaxPanel.setNumber(deltaXMax);
		numberOfSphericalLayersPanel.setNumber(numberOfSphericalLayers);
		simulationMaxStepsPanel.setNumber(simulationMaxSteps);
		simulationSphereSurfaceTransmissionCoefficientPanel.setNumber(simulationSphereSurfaceTransmissionCoefficient);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableChenBelinBlackHole acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		
		setCentre(centrePanel.getVector3D());
		horizonRadius = horizonRadiusPanel.getNumber();
		jParameter = jParameterPanel.getNumber();
		setRadius(simulationSphereRadiusPanel.getNumber());
		integrationType = (IntegrationType)(integrationTypeComboBox.getSelectedItem());

		String simulationTypeSelectedTitle = simulationTypeTabbedPane.getTitleAt(simulationTypeTabbedPane.getSelectedIndex());
		for(SphericallySymmetricRefractiveIndexDistributionSimulationType s:SphericallySymmetricRefractiveIndexDistributionSimulationType.values())
			if(simulationTypeSelectedTitle.equals(s.toString())) simulationType = s;
		
		deltaTau = deltaTauPanel.getNumber();
		deltaXMax = deltaXMaxPanel.getNumber();
		numberOfSphericalLayers = numberOfSphericalLayersPanel.getNumber();
		simulationMaxSteps = simulationMaxStepsPanel.getNumber();
		simulationSphereSurfaceTransmissionCoefficient = simulationSphereSurfaceTransmissionCoefficientPanel.getNumber();
		
		initialiseSurface();
		
		return this;
	}
	
	@Override
	public EditableChenBelinBlackHole transform(Transformation t)
	{
		return new EditableChenBelinBlackHole(
				getDescription(),
				t.transformPosition(getCentre()),
				horizonRadius,
				jParameter,
				getRadius(),
				simulationType,
				integrationType,
				simulationMaxSteps,
				deltaTau,
				deltaXMax,
				numberOfSphericalLayers,
				simulationSphereSurfaceTransmissionCoefficient,
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
