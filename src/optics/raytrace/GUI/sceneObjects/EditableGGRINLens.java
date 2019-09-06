package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.Transformation;
import optics.raytrace.sceneObjects.GGRINLens;


/**
 * A GGRIN lens [1].
 * [1] M. Sarbort and T. Tyc, "Spherical media and geodesic lenses in geometrical optics", Journal of Optics�14, 075705�(2012)
 * http://stacks.iop.org/2040-8986/14/i=7/a=075705
 * 
 * @author Johannes
 *
 */
public class EditableGGRINLens extends GGRINLens implements IPanelComponent
{
	private static final long serialVersionUID = 7038224325020390802L;

	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel centrePanel;
	private LabelledDoublePanel radiusPanel;
	private LabelledDoublePanel rPanel;
	private LabelledDoublePanel rPrimePanel;
	private LabelledDoublePanel phiPanel;
	private LabelledDoublePanel ratioNSurfaceNSurroundingPanel;
	private LabelledDoublePanel transparentTunnelRadiusPanel;
	private LabelledDoublePanel transmissionCoefficientPanel;


	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param r
	 * @param rPrime
	 * @param phi
	 * @param ratioNSurfaceNSurrounding
	 * @param transmissionCoefficient
	 * @param parent
	 * @param studio
	 */
	public EditableGGRINLens(
			String description,
			Vector3D centre,
			double radius,
			double r,
			double rPrime,
			double phi,
			double ratioNSurfaceNSurrounding,
			double transparentTunnelRadius,
			double transmissionCoefficient,
			boolean shadowThrowing,
			SceneObject parent, 
			Studio studio
		)
	{
		super(description, centre, radius, r, rPrime, phi, ratioNSurfaceNSurrounding, transparentTunnelRadius, transmissionCoefficient, shadowThrowing, parent, studio);
	}
	
	/**
	 * @param description
	 * @param centre
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableGGRINLens(
			String description,
			Vector3D centre,
			double radius,
			SceneObject parent, 
			Studio studio
		)
	{
		this(	description,
				centre,
				radius,
				1,	// r
				1,	// rPrime
				0,	// phi
				1,	// refractive-index ratio
				0,	// transparent-tunnel radius
				1,	// transmission coefficient
				true,	// shadow-throwing
				parent, studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableGGRINLens(EditableGGRINLens original)
	{
		super(original);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public EditableGGRINLens clone()
	{
		return new EditableGGRINLens(this);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		// editPanel.setLayout(new BorderLayout());
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("GGRIN lens"));
		
		editPanel.setLayout(new MigLayout("insets 0"));
			
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));
		
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		radiusPanel = new LabelledDoublePanel("Radius");
		editPanel.add(radiusPanel, "split 3");
		
		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		rPanel = new LabelledDoublePanel("r");
		editPanel.add(rPanel);

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		rPrimePanel = new LabelledDoublePanel("r'");
		editPanel.add(rPrimePanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		phiPanel = new LabelledDoublePanel("phi (degree)");
		editPanel.add(phiPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		ratioNSurfaceNSurroundingPanel = new LabelledDoublePanel("Ratio n_surface / n_surrounding");
		editPanel.add(ratioNSurfaceNSurroundingPanel, "wrap");

		transparentTunnelRadiusPanel = new LabelledDoublePanel("Radius of central transparent tunnel");
		editPanel.add(transparentTunnelRadiusPanel, "wrap");

		// add a bit of (non-stretchable) space
		// editPanel.add(Box.createRigidArea(new Dimension(10,5)));

		transmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		editPanel.add(transmissionCoefficientPanel);

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
		rPanel.setNumber(getR());
		rPrimePanel.setNumber(getRPrime());
		phiPanel.setNumber(getPhi()*180/Math.PI);
		ratioNSurfaceNSurroundingPanel.setNumber(getRatioNSurfaceNSurrounding());
		transparentTunnelRadiusPanel.setNumber(getTransparentTunnelRadius());
		transmissionCoefficientPanel.setNumber(getTransmissionCoefficient());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableGGRINLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setCentre(centrePanel.getVector3D());
		setRadius(radiusPanel.getNumber());
		setR(rPanel.getNumber());
		setRPrime(rPrimePanel.getNumber());
		setPhi(phiPanel.getNumber()*Math.PI/180);
		setRatioNSurfaceNSurrounding(ratioNSurfaceNSurroundingPanel.getNumber());
		setTransparentTunnelRadius(transparentTunnelRadiusPanel.getNumber());
		setTransmissionCoefficient(transmissionCoefficientPanel.getNumber());

		return this;
	}
	
	@Override
	public EditableGGRINLens transform(Transformation t)
	{
		return new EditableGGRINLens(
				getDescription(),
				t.transformPosition(getCentre()),
				getRadius(),
				getR(),
				getRPrime(),
				getPhi(),
				getRatioNSurfaceNSurrounding(),
				getTransparentTunnelRadius(),
				getTransmissionCoefficient(),
				isShadowThrowing(),
				getParent(), 
				getStudio()
			);
	}

	@Override
	public void backToFront(IPanelComponent edited) {
	}

}
