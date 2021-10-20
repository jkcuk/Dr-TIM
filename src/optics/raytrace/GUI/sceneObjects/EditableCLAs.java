package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import math.*;
import net.miginfocom.swing.MigLayout;
import optics.DoubleColour;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.surfaces.IdealThinLensSurfaceSimple;
import optics.raytrace.surfaces.PhaseHologramOfLens;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;
import optics.raytrace.surfaces.RectangularIdealThinLensletArray;
import optics.raytrace.surfaces.SemiTransparent;
import optics.raytrace.surfaces.SurfaceOfVolumeWithColouredVoxelBoundaries;
import optics.raytrace.voxellations.SetOfEquidistantParallelPlanes;

/**
 * CLAs that can be configured in a number of ways
 * 
 * @author Johannes
 */
public class EditableCLAs extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -881739071694388536L;

	// parameters

	/**
	 * centre of the common focal plane
	 */
	private Vector3D centreOfCommonFocalPlane;
	
	/**
	 * vector spanning the width of the CLAs, and determining the first direction of periodicity of the array
	 */
	private Vector3D spanVectorU;
	
	/**
	 * vector spanning the height of the CLAs, and determining the second direction of periodicity of the array
	 */
	private Vector3D spanVectorV;
	
	/**
	 * normalised outwards-facing normal to lenslet array 1
	 */
	private Vector3D outwardsNormalToLA1;
	
	/**
	 * focal length of lenslet array 1
	 */
	private double f1;

	/**
	 * focal length of lenslet array 2
	 */
	private double f2;
	
	/**
	 * periodicity of the array in the u direction
	 */
	private double periodU;
	
	/**
	 * periodicity of the array in the v direction
	 */
	private double periodV;
	
	/**
	 * type of lenses that make up the array
	 */
	private ThinLensType lensletArrayType;
	
	/**
	 * if true, show baffles
	 */
	private boolean showBaffles;

	/**
	 * if true, show channeling lens just in front of LA1
	 */
	private boolean showChannelingLens1;

	/**
	 * if true, show channeling lens just in front of LA2
	 */
	private boolean showChannelingLens2;

	/**
	 * focal length of channeling lens 1
	 */
	private double channelingLens1F;

	/**
	 * focal length of channeling lens 2
	 */
	private double channelingLens2F;

	/**
	 * type of channeling lenses
	 */
	private ThinLensType channelingLensesType;
	
	/**
	 * if true, add a random angle that represents diffractive blur to the direction of the outgoing light ray
	 */
	private boolean simulateDiffractiveBlur;

	/**
	 * wavelength of light;
	 * used to calculate approximate magnitude of diffractive blur
	 */
	private double lambda;	// wavelength of light, for diffraction purposes


	
	// constructors etc.
	
	/**
	 * @param description
	 * @param centreOfCommonFocalPlane
	 * @param spanVectorU
	 * @param spanVectorV
	 * @param outwardsNormalToLA1
	 * @param f1
	 * @param f2
	 * @param periodU
	 * @param periodV
	 * @param lensletArrayType
	 * @param showBaffles
	 * @param showChannelingLens1
	 * @param showChannelingLens2
	 * @param channelingLens1F
	 * @param channelingLens2F
	 * @param channelingLensType
	 * @param simulateDiffractiveBlur
	 * @param lambda
	 * @param parent
	 * @param studio
	 */
	public EditableCLAs(
			String description,
			Vector3D centreOfCommonFocalPlane,
			Vector3D spanVectorU,
			Vector3D spanVectorV,
			Vector3D outwardsNormalToLA1,
			double f1,
			double f2,
			double periodU,
			double periodV,
			ThinLensType lensletArrayType,
			boolean showBaffles,
			boolean showChannelingLens1,
			boolean showChannelingLens2,
			double channelingLens1F,
			double channelingLens2F,
			ThinLensType channelingLensType,
			boolean simulateDiffractiveBlur,
			double lambda,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setCentreOfCommonFocalPlane(centreOfCommonFocalPlane);
		setSpanVectorU(spanVectorU);
		setSpanVectorV(spanVectorV);
		setOutwardsNormalToLA1(outwardsNormalToLA1);
		setF1(f1);
		setF2(f2);
		setPeriodU(periodU);
		setPeriodV(periodV);
		setLensletArrayType(lensletArrayType);
		setShowBaffles(showBaffles);
		setShowChannelingLens1(showChannelingLens1);
		setShowChannelingLens2(showChannelingLens2);
		setChannelingLens1F(channelingLens1F);
		setChannelingLens2F(channelingLens2F);
		setChannelingLensType(channelingLensType);
		setSimulateDiffractiveBlur(simulateDiffractiveBlur);
		setLambda(lambda);

		populateSceneObjectCollection();
	}
	
	/**
	 * @param description
	 * @param parent
	 * @param studio
	 */
	public EditableCLAs(
			String description,
			SceneObject parent, 
			Studio studio
	)
	{
		this(
				description,
				new Vector3D(0, 0, 5),	// centreOfCommonFocalPlane
				new Vector3D(1, 0, 0),	// spanVectorU
				new Vector3D(0, 1, 0),	// spanVectorV
				new Vector3D(0, 0, 1),	// outwardsNormalToLA1
				0.1,	// f1
				0.1,	// f2,
				0.1,	// periodU
				0.1,	// periodV
				ThinLensType.IDEAL_THIN_LENS,	// lensletArrayType
				true,	// showBaffles
				false, 	// showChannelingLens1
				false,	// showChannelingLens2
				1,	// channelingLens1F
				1,	// channelingLens2F
				ThinLensType.IDEAL_THIN_LENS,	// channelingLensType
				false,	// simulateDiffractiveBlur
				633e-9,	// lambda
				parent,
				studio
			);
	}


	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCLAs(EditableCLAs original)
	{
		this(
				original.getDescription(),
				original.getCentreOfCommonFocalPlane().clone(),
				original.getSpanVectorU().clone(),
				original.getSpanVectorV().clone(),
				original.getOutwardsNormalToLA1(),
				original.getF1(),
				original.getF2(),
				original.getPeriodU(),
				original.getPeriodV(),
				original.getLensletArrayType(),
				original.isShowBaffles(),
				original.isShowChannelingLens1(),
				original.isShowChannelingLens2(),
				original.getChannelingLens1F(),
				original.getChannelingLens2F(),
				original.getChannelingLensType(),
				original.isSimulateDiffractiveBlur(),
				original.getLambda(),
				original.getParent(), 
				original.getStudio()
			);
	}

	@Override
	public EditableCLAs clone()
	{
		return new EditableCLAs(this);
	}

	

	// setters & getters
	
	public Vector3D getCentreOfCommonFocalPlane() {
		return centreOfCommonFocalPlane;
	}

	public void setCentreOfCommonFocalPlane(Vector3D centreOfCommonFocalPlane) {
		this.centreOfCommonFocalPlane = centreOfCommonFocalPlane;
	}

	public Vector3D getSpanVectorU() {
		return spanVectorU;
	}

	public void setSpanVectorU(Vector3D spanVectorU) {
		this.spanVectorU = spanVectorU;
	}

	public Vector3D getSpanVectorV() {
		return spanVectorV;
	}

	public void setSpanVectorV(Vector3D spanVectorV) {
		this.spanVectorV = spanVectorV;
	}

	public Vector3D getOutwardsNormalToLA1() {
		return outwardsNormalToLA1;
	}

	public void setOutwardsNormalToLA1(Vector3D outwardsNormalToLA1) {
		this.outwardsNormalToLA1 = outwardsNormalToLA1;
	}

	public double getF1() {
		return f1;
	}

	public void setF1(double f1) {
		this.f1 = f1;
	}

	public double getF2() {
		return f2;
	}

	public void setF2(double f2) {
		this.f2 = f2;
	}

	public double getPeriodU() {
		return periodU;
	}

	public void setPeriodU(double periodU) {
		this.periodU = periodU;
	}

	public double getPeriodV() {
		return periodV;
	}

	public void setPeriodV(double periodV) {
		this.periodV = periodV;
	}

	public ThinLensType getLensletArrayType() {
		return lensletArrayType;
	}

	public void setLensletArrayType(ThinLensType lensletArrayType) {
		this.lensletArrayType = lensletArrayType;
	}

	public boolean isShowBaffles() {
		return showBaffles;
	}

	public void setShowBaffles(boolean showBaffles) {
		this.showBaffles = showBaffles;
	}
	
	public boolean isShowChannelingLens1() {
		return showChannelingLens1;
	}

	public void setShowChannelingLens1(boolean showChannelingLens1) {
		this.showChannelingLens1 = showChannelingLens1;
	}

	public boolean isShowChannelingLens2() {
		return showChannelingLens2;
	}

	public void setShowChannelingLens2(boolean showChannelingLens2) {
		this.showChannelingLens2 = showChannelingLens2;
	}

	public double getChannelingLens1F() {
		return channelingLens1F;
	}

	public void setChannelingLens1F(double channelingLens1F) {
		this.channelingLens1F = channelingLens1F;
	}

	public double getChannelingLens2F() {
		return channelingLens2F;
	}

	public void setChannelingLens2F(double channelingLens2F) {
		this.channelingLens2F = channelingLens2F;
	}

	public ThinLensType getChannelingLensType() {
		return channelingLensesType;
	}

	public void setChannelingLensType(ThinLensType channelingLensType) {
		this.channelingLensesType = channelingLensType;
	}

	public boolean isSimulateDiffractiveBlur() {
		return simulateDiffractiveBlur;
	}

	public void setSimulateDiffractiveBlur(boolean simulateDiffractiveBlur) {
		this.simulateDiffractiveBlur = simulateDiffractiveBlur;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}



	
	// the bit with some substance

	private void populateSceneObjectCollection()
	{
		// lenslet arrays
		
		Vector3D normalisedOutwardsNormalToLA1 = getOutwardsNormalToLA1().getNormalised();
		Vector3D la1Centre = Vector3D.sum(centreOfCommonFocalPlane, normalisedOutwardsNormalToLA1.getProductWith(f1));
		Vector3D la2Centre = Vector3D.sum(centreOfCommonFocalPlane, normalisedOutwardsNormalToLA1.getProductWith(-f2));
		Vector3D uVector = spanVectorU.getPartPerpendicularTo(outwardsNormalToLA1);
		Vector3D vVector = spanVectorV.getPartPerpendicularTo(outwardsNormalToLA1);
		
		SurfaceProperty surfacePropertyRepresentingLA1, surfacePropertyRepresentingLA2;
		switch(lensletArrayType)
		{
		case LENS_HOLOGRAM:
			surfacePropertyRepresentingLA1 = new PhaseHologramOfRectangularLensletArray(
					la1Centre,	// centre
					uVector.getNormalised(),	// v1
					vVector.getNormalised(),	// v2
					f1,	// focalLength
					periodU,	// uPeriod
					periodV,	// vPeriod
					0,	// uOffset
					0,	// vOffset
					simulateDiffractiveBlur,
					lambda,
					1,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
					);
			surfacePropertyRepresentingLA2 = new PhaseHologramOfRectangularLensletArray(
					la2Centre,	// centre
					uVector.getNormalised(),	// v1
					vVector.getNormalised(),	// v2
					f2,	// focalLength
					periodU,	// uPeriod
					periodV,	// vPeriod
					0,	// uOffset
					0,	// vOffset
					simulateDiffractiveBlur,
					lambda,
					1,	// throughputCoefficient
					false,	// reflective
					false	// shadowThrowing
					);
			break;
		case SEMITRANSPARENT_PLANE:
			surfacePropertyRepresentingLA1 = SemiTransparent.BLUE_SHINY_SEMITRANSPARENT;
			surfacePropertyRepresentingLA2 = SemiTransparent.BLUE_SHINY_SEMITRANSPARENT;
			break;
		case IDEAL_THIN_LENS:
		default:
			surfacePropertyRepresentingLA1 = new RectangularIdealThinLensletArray(
					f1,	// focalLength
					periodU,	// xPeriod
					periodV,	// yPeriod
					0,	// xOffset
					0,	// yOffset
					simulateDiffractiveBlur,
					lambda,
					1,	// transmissionCoefficient
					false	// shadowThrowing
					);
			surfacePropertyRepresentingLA2 = new RectangularIdealThinLensletArray(
					f2,	// focalLength
					periodU,	// xPeriod
					periodV,	// yPeriod
					0,	// xOffset
					0,	// yOffset
					simulateDiffractiveBlur,
					lambda,
					1,	// transmissionCoefficient
					false	// shadowThrowing
					);
		}

		addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Lenslet array 1",	// description
				la1Centre,	// pointOnPlane
				uVector,	// v1
				vVector,	// v2
				-0.5*uVector.getLength(),
				+0.5*uVector.getLength(),
				-0.5*vVector.getLength(),
				+0.5*vVector.getLength(),
				surfacePropertyRepresentingLA1,	// surface property
				this,
				getStudio()
				));

		addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Lenslet array 2",	// description
				la2Centre,	// pointOnPlane
				uVector,	// v1
				vVector,	// v2
				-0.5*uVector.getLength(),
				+0.5*uVector.getLength(),
				-0.5*vVector.getLength(),
				+0.5*vVector.getLength(),
				surfacePropertyRepresentingLA2,	// surface property
				this,
				getStudio()
				));

		// baffles
		
		// first, construct the baffles, which can then be added later (if required)
		// SceneObjectContainer baffles = new SceneObjectContainer("Baffles", this, getStudio());
		EditableCuboid baffles = new EditableCuboid(
				"Baffles",	// description
				Vector3D.sum(centreOfCommonFocalPlane, normalisedOutwardsNormalToLA1.getProductWith(0.5*(f1-f2))),	// centre
				normalisedOutwardsNormalToLA1.getProductWith(0.5*(f1+f2)-MyMath.TINY),	// centre2centreOfFace1
				uVector.getProductWith(0.5),	// centre2centreOfFace2
				vVector.getProductWith(0.5),	// centre2centreOfFace3
				null,	// surfaceProperty -- will be set to SurfaceOfVolumeWithColouredVoxelBoundaries later
				this,	// parent
				getStudio()
			);

		// the set of planes that define the voxels
		Vector3D pointOnPlanes = Vector3D.sum(
				centreOfCommonFocalPlane,
				uVector.getWithLength(0.5*periodU),
				vVector.getWithLength(0.5*periodV)
			);
		SetOfEquidistantParallelPlanes[] planeSets = new SetOfEquidistantParallelPlanes[2];
		planeSets[0] = new SetOfEquidistantParallelPlanes(
				pointOnPlanes,	// point on 0th plane
				uVector.getNormalised(),	// normal to surfaces
				periodU	// separation between neighbouring planes
			);
		planeSets[1] = new SetOfEquidistantParallelPlanes(
				pointOnPlanes,	// point on 0th plane
				vVector.getNormalised(),	// normal to surfaces
				periodV	// separation between neighbouring planes
			);
		
		SurfaceOfVolumeWithColouredVoxelBoundaries surfaceOfBaffledVolume = new SurfaceOfVolumeWithColouredVoxelBoundaries(
				planeSets,	// the sets of parallel planes defining the voxels
				baffles,	// (SceneObject) new Refractive(0,0), the object
				DoubleColour.BLACK,
				700,	// maxSteps
				1,	// transmission coefficient
				false	// shadow-throwing
			);
		
		baffles.setSurfaceProperty(surfaceOfBaffledVolume);
		baffles.addSceneObjects();

		addSceneObject(baffles, showBaffles);
		
		
		// channeling lenses
		
		SurfaceProperty surfacePropertyRepresentingChannelingLens1, surfacePropertyRepresentingChannelingLens2;
		// principal points of the channeling lenses
		Vector3D channelingLens1P = Vector3D.sum(centreOfCommonFocalPlane, normalisedOutwardsNormalToLA1.getProductWith(f1+MyMath.TINY));
		Vector3D channelingLens2P = Vector3D.sum(centreOfCommonFocalPlane, normalisedOutwardsNormalToLA1.getProductWith(-f2-MyMath.TINY));
		switch(channelingLensesType)
		{
		case LENS_HOLOGRAM:
			surfacePropertyRepresentingChannelingLens1 = new PhaseHologramOfLens(
					channelingLens1F,	// focalLength
					channelingLens1P,	// principalPoint
					0.96,	// throughputCoefficient
					false,	// reflective
					false	//shadowThrowing
				);
			surfacePropertyRepresentingChannelingLens2 = new PhaseHologramOfLens(
					channelingLens2F,	// focalLength
					channelingLens2P,	// principalPoint
					0.96,	// throughputCoefficient
					false,	// reflective
					false	//shadowThrowing
				);
			break;
		case SEMITRANSPARENT_PLANE:
			surfacePropertyRepresentingChannelingLens1 = SemiTransparent.BLUE_SHINY_SEMITRANSPARENT;
			surfacePropertyRepresentingChannelingLens2 = SemiTransparent.BLUE_SHINY_SEMITRANSPARENT;
			break;
		case IDEAL_THIN_LENS:
		default:
			surfacePropertyRepresentingChannelingLens1 = new IdealThinLensSurfaceSimple(
					channelingLens1P,	// lensCentre
					normalisedOutwardsNormalToLA1,	// opticalAxisDirection
					channelingLens1F,	// focalLength
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
			surfacePropertyRepresentingChannelingLens2 = new IdealThinLensSurfaceSimple(
					channelingLens2P,	// lensCentre
					normalisedOutwardsNormalToLA1.getReverse(),	// opticalAxisDirection
					channelingLens2F,	// focalLength
					0.96,	// transmissionCoefficient
					false	// shadowThrowing
				);
		}
		
		addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Channeling lens 1",	// description
				channelingLens1P,	// pointOnPlane
				uVector,	// v1
				vVector,	// v2
				-0.5*uVector.getLength(),
				+0.5*uVector.getLength(),
				-0.5*vVector.getLength(),
				+0.5*vVector.getLength(),
				surfacePropertyRepresentingChannelingLens1,	// surface property
				this,
				getStudio()
				),
				showChannelingLens1
			);

		addSceneObject(new EditableScaledParametrisedCentredParallelogram(
				"Channeling lens 2",	// description
				channelingLens2P,	// pointOnPlane
				uVector,	// v1
				vVector,	// v2
				-0.5*uVector.getLength(),
				+0.5*uVector.getLength(),
				-0.5*vVector.getLength(),
				+0.5*vVector.getLength(),
				surfacePropertyRepresentingChannelingLens2,	// surface property
				this,
				getStudio()
				),
				showChannelingLens2
			);
	}




	// GUI panels
		
	private LabelledVector3DPanel centreOfCommonFocalPlanePanel, spanVectorUPanel, spanVectorVPanel, outwardsNormalToLA1Panel;

	// lenslet arrays
	private LabelledDoublePanel f1Panel, f2Panel, periodUPanel, periodVPanel;
	private JComboBox<ThinLensType> lensletArrayTypeComboBox;
	
	// baffles
	private JCheckBox showBafflesCheckBox;
	
	// channeling lenses
	private JCheckBox showChannelingLens1CheckBox, showChannelingLens2CheckBox;
	private LabelledDoublePanel channelingLens1FPanel, channelingLens2FPanel;
	private JComboBox<ThinLensType> channelingLensesTypeComboBox;

	private JButton convertButton;


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("CLA cloak"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		centreOfCommonFocalPlanePanel = new LabelledVector3DPanel("Centre of common focal plane");
		editPanel.add(centreOfCommonFocalPlanePanel, "wrap");
		
		outwardsNormalToLA1Panel = new LabelledVector3DPanel("Outwards normal to lenslet array 1");
		editPanel.add(outwardsNormalToLA1Panel, "wrap");

		spanVectorUPanel = new LabelledVector3DPanel("Span vector in the u direction");
		editPanel.add(spanVectorUPanel, "wrap");

		spanVectorVPanel = new LabelledVector3DPanel("Span vector in the v direction");
		editPanel.add(spanVectorVPanel, "wrap");

		// lenslet arrays
		
		JPanel lensletArraysPanel = new JPanel();
		lensletArraysPanel.setLayout(new MigLayout("insets 0"));
		lensletArraysPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet arrays"));
		editPanel.add(lensletArraysPanel, "wrap");

		f1Panel = new LabelledDoublePanel("Focal length of lenslet array 1");
		lensletArraysPanel.add(f1Panel, "wrap");

		f2Panel = new LabelledDoublePanel("Focal length of lenslet array 2");
		lensletArraysPanel.add(f2Panel, "wrap");

		periodUPanel = new LabelledDoublePanel("Period in u direction");
		lensletArraysPanel.add(periodUPanel, "wrap");

		periodVPanel = new LabelledDoublePanel("Period in v direction");
		lensletArraysPanel.add(periodVPanel, "wrap");

		lensletArrayTypeComboBox = new JComboBox<ThinLensType>(ThinLensType.values());
		lensletArraysPanel.add(lensletArrayTypeComboBox, "wrap");

		
		// baffles
		
		showBafflesCheckBox = new JCheckBox("Show baffles");
		editPanel.add(showBafflesCheckBox, "wrap");
		
		
		// the channeling lenses
		
		JPanel channelingLensesPanel = new JPanel();
		channelingLensesPanel.setLayout(new MigLayout("insets 0"));
		channelingLensesPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Channeling lenses"));
		editPanel.add(channelingLensesPanel, "wrap");

		showChannelingLens1CheckBox = new JCheckBox("Show channeling lens in front of lenslet array 1");
		channelingLensesPanel.add(showChannelingLens1CheckBox, "wrap");

		showChannelingLens2CheckBox = new JCheckBox("Show channeling lens in front of lenslet array 2");
		channelingLensesPanel.add(showChannelingLens2CheckBox, "wrap");

		channelingLens1FPanel = new LabelledDoublePanel("Focal length of channeling lens 1");
		channelingLensesPanel.add(channelingLens1FPanel, "wrap");

		channelingLens2FPanel = new LabelledDoublePanel("Focal length of channeling lens 2");
		channelingLensesPanel.add(channelingLens2FPanel, "wrap");

		channelingLensesTypeComboBox = new JComboBox<ThinLensType>(ThinLensType.values());
		channelingLensesPanel.add(channelingLensesTypeComboBox, "wrap");
		

		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton);

		// validate the entire edit panel
		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		// initialize any fields
		descriptionPanel.setString(getDescription());
		
		centreOfCommonFocalPlanePanel.setVector3D(centreOfCommonFocalPlane);
		spanVectorUPanel.setVector3D(spanVectorU);
		spanVectorVPanel.setVector3D(spanVectorV);
		outwardsNormalToLA1Panel.setVector3D(outwardsNormalToLA1);
		
		// lenslet arrays
		f1Panel.setNumber(f1);
		f2Panel.setNumber(f2);
		periodUPanel.setNumber(periodU);
		periodVPanel.setNumber(periodV);
		lensletArrayTypeComboBox.setSelectedItem(lensletArrayType);
		
		// baffles
		showBafflesCheckBox.setSelected(showBaffles);
		
		// channeling lenses
		showChannelingLens1CheckBox.setSelected(showChannelingLens1);
		showChannelingLens2CheckBox.setSelected(showChannelingLens2);
		channelingLens1FPanel.setNumber(channelingLens1F);
		channelingLens2FPanel.setNumber(channelingLens2F);
		channelingLensesTypeComboBox.setSelectedItem(channelingLensesType);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCLAs acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		centreOfCommonFocalPlane = centreOfCommonFocalPlanePanel.getVector3D();
		spanVectorU = spanVectorUPanel.getVector3D();
		spanVectorV = spanVectorVPanel.getVector3D();
		outwardsNormalToLA1 = outwardsNormalToLA1Panel.getVector3D();
		
		// lenslet arrays
		f1 = f1Panel.getNumber();
		f2 = f2Panel.getNumber();
		periodU = periodUPanel.getNumber();
		periodV = periodVPanel.getNumber();
		lensletArrayType = (ThinLensType)(lensletArrayTypeComboBox.getSelectedItem());
		
		// baffles
		showBaffles = showBafflesCheckBox.isSelected();
		
		// channeling lenses
		showChannelingLens1 = showChannelingLens1CheckBox.isSelected();
		showChannelingLens2 = showChannelingLens2CheckBox.isSelected();
		channelingLens1F = channelingLens1FPanel.getNumber();
		channelingLens2F = channelingLens2FPanel.getNumber();
		channelingLensesType = (ThinLensType)(channelingLensesTypeComboBox.getSelectedItem());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit CLAs");
		container.setValuesInEditPanel();
	}
}