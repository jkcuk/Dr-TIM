package optics.raytrace.GUI.sceneObjects;

import javax.swing.JPanel;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.PhaseHologramOfRectangularLensletArray;

/**
 * A rectangle whose surface area is a phase hologram of a rectangular lenslet array
 * @author johannes
 */
public class EditableRectangularLensletArray 
extends EditableScaledParametrisedCentredParallelogram
{
	private static final long serialVersionUID = -8167701964397597829L;


	public EditableRectangularLensletArray(
			String description,
			Vector3D centre, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double focalLength,
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(
				description,
				centre, 
				spanVector1,
				spanVector2, 
				-spanVector1.getLength()/2, spanVector1.getLength()/2,	// suMin, suMax
				-spanVector2.getLength()/2, spanVector2.getLength()/2,	// svMin, svMax
				null,	// surfaceProperty, null for the moment
				parent,
				studio
			);
		
		setSurfaceProperty(new PhaseHologramOfRectangularLensletArray(
				focalLength,
				xPeriod,
				yPeriod,
				xOffset,
				yOffset,
				this,	// sceneObject
				throughputCoefficient,
				reflective,
				shadowThrowing
			)
		);
	}
	
	public EditableRectangularLensletArray(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Lenslet-array hologram",	// description
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// spanVector1
				new Vector3D(0, 1, 0),	// spanVector2
				1,	// focalLength
				0.1,	// xPeriod
				0.1,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				false,	// reflective
				true,	// shadowThrowing
				parent,
				studio
			);
	}

	public EditableRectangularLensletArray(EditableRectangularLensletArray original) {
		super(original);
		((PhaseHologramOfRectangularLensletArray)getSurfaceProperty()).setSceneObject(this);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public EditableRectangularLensletArray clone()
	{
		return new EditableRectangularLensletArray(this);
	}


	
	/**
	 * 
	 * Editable-interface stuff
	 * 
	 */
	
	/**
	 * variables
	 */
	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel;
	private LabelledVector2DPanel lensletArrayPeriodPanel, lensletArrayOffsetPanel;

	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array"));
		
		//
		// the basic-parameters panel
		//
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");
				
		centrePanel = new LabelledVector3DPanel("Centre");
		editPanel.add(centrePanel, "wrap");

		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
		editPanel.add(widthVectorPanel, "wrap");

		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
		editPanel.add(heightVectorPanel, "wrap");

		//
		// the lenslet-array parameters
		//
		
		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
		lensletArrayFocalLengthPanel.setNumber(1);
		editPanel.add(lensletArrayFocalLengthPanel, "span");
		
		lensletArrayPeriodPanel = new LabelledVector2DPanel("Period in (x,y)");
		lensletArrayPeriodPanel.setVector2D(0.1, 0.1);
		editPanel.add(lensletArrayPeriodPanel, "span");
		
		lensletArrayOffsetPanel = new LabelledVector2DPanel("Offset in (x,y)");
		lensletArrayOffsetPanel.setVector2D(0, 0);
		editPanel.add(lensletArrayOffsetPanel, "span");
		
		lensletArrayTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
		lensletArrayTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
		editPanel.add(lensletArrayTransmissionCoefficientPanel, "span");;

		editPanel.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
	 */
	@Override
	public void setValuesInEditPanel()
	{
		descriptionPanel.setString(getDescription());
		centrePanel.setVector3D(getCentre());
		widthVectorPanel.setVector3D(getSpanVector1());
		heightVectorPanel.setVector3D(getSpanVector2());
		
		// initialize any fields
		PhaseHologramOfRectangularLensletArray la = (PhaseHologramOfRectangularLensletArray)getSurfaceProperty();
		lensletArrayFocalLengthPanel.setNumber(la.getFocalLength());
		lensletArrayTransmissionCoefficientPanel.setNumber(la.getTransmissionCoefficient());
		lensletArrayPeriodPanel.setVector2D(la.getxPeriod(), la.getyPeriod());
		lensletArrayOffsetPanel.setVector2D(la.getxOffset(), la.getyOffset());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableScaledParametrisedCentredParallelogram acceptValuesInEditPanel()
	{
		// don't use super.acceptValuesInEditPanel(); as this sets the surface property and the scaling;
		// instead, copy the relevant code from it
		setDescription(descriptionPanel.getString());
		
		setCentreAndSpanVectors(
				centrePanel.getVector3D(),
				widthVectorPanel.getVector3D(),
				heightVectorPanel.getVector3D()
			);
		
		PhaseHologramOfRectangularLensletArray la = (PhaseHologramOfRectangularLensletArray)getSurfaceProperty();

		la.setFocalLength(lensletArrayFocalLengthPanel.getNumber());
		la.setxPeriod(lensletArrayPeriodPanel.getVector2D().x);
		la.setyPeriod(lensletArrayPeriodPanel.getVector2D().y);
		la.setxOffset(lensletArrayOffsetPanel.getVector2D().x);
		la.setyOffset(lensletArrayOffsetPanel.getVector2D().y);
		la.setTransmissionCoefficient(lensletArrayTransmissionCoefficientPanel.getNumber());
		
		return this;
	}
}
