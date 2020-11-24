package optics.raytrace.sceneObjects;

import java.io.Serializable;


import math.Vector3D;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.PhaseHologramOfSparseRectangularLensletArray;
import optics.raytrace.surfaces.SurfacePropertyLayerStack;

/**
 * A rectangle whose surface area is a phase hologram of a rectangular lenslet array
 * @author johannes
 */
public class SparseRectangularLensletArray 
extends SceneObjectWithHoles
implements Serializable
{
	private static final long serialVersionUID = 1939712790553898120L;

	/**
	 * the parallelogram (normally a rectangle) that is then "wrapped" by the "SceneObjectWithHoles"
	 */
	private ScaledParametrisedParallelogram parallelogram;

	private PhaseHologramOfSparseRectangularLensletArray sparseLensArray;

	public SparseRectangularLensletArray(
			String description,
			Vector3D corner, 
			Vector3D spanVector1,
			Vector3D spanVector2, 
			double focalLength,
			double xPeriod,
			double yPeriod,
			double xOffset,
			double yOffset,
			int nx,
			int ny,
			boolean simulateDiffractiveBlur,
			double lambda,
			boolean colouredLenses,
			double throughputCoefficient,
			boolean reflective,
			boolean shadowThrowing,
			SceneObject parent,
			Studio studio
		)
	{
		super(description);
		
		// create the wrapped SceneObject
		
		parallelogram = new ScaledParametrisedParallelogram(
				description,
				corner, 
				spanVector1,
				spanVector2, 
				0, spanVector1.getLength(),	// suMin, suMax
				0, spanVector2.getLength(),	// svMin, svMax
				null,	// surfaceProperty, null for the moment
				parent,
				studio
			);

		sparseLensArray = new PhaseHologramOfSparseRectangularLensletArray(
					focalLength,
					xPeriod,
					yPeriod,
					xOffset,
					yOffset,
					nx,
					ny,
					simulateDiffractiveBlur,
					lambda,
					parallelogram,	// sceneObject
					throughputCoefficient,
					reflective,
					shadowThrowing
				);
		
		if(colouredLenses)
			parallelogram.setSurfaceProperty(
					new SurfacePropertyLayerStack(sparseLensArray, ColourFilter.LIGHT_CYAN_GLASS)
				);
		else
			parallelogram.setSurfaceProperty(sparseLensArray);

		try {
			setWrappedSceneObject(parallelogram);
		} catch (SceneException e) {
			// this shouldn't happen
			e.printStackTrace();
		}
		setHoleySurface(sparseLensArray);

	}
	
	public SparseRectangularLensletArray(
			SceneObject parent,
			Studio studio
		)
	{
		this(
				"Sparse rectangular lenslet-array",	// description
				new Vector3D(-0.5, -0.5, 10),	// corner
				new Vector3D(1, 0, 0),	// spanVector1
				new Vector3D(0, 1, 0),	// spanVector2
				1,	// focalLength
				0.1,	// xPeriod
				0.1,	// yPeriod
				0,	// xOffset
				0,	// yOffset
				1,	// nx
				1,	// ny
				true,	// simulateDiffractiveBlur
				632.8e-9,	// lambda
				true,	// colouredLenses
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
				false,	// reflective
				true,	// shadowThrowing
				parent,
				studio
			);
	}

	public SparseRectangularLensletArray(SparseRectangularLensletArray original)
	throws SceneException
	{
		super(original);
		sparseLensArray = (PhaseHologramOfSparseRectangularLensletArray)original.getHoleySurface();
		parallelogram = (ScaledParametrisedParallelogram)original.getWrappedSceneObject();
		
		sparseLensArray.setSceneObject(parallelogram);
		parallelogram.setSurfaceProperty(sparseLensArray);
	}
	
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedPlane#clone()
	 */
	@Override
	public SparseRectangularLensletArray clone()
	{
		try {
			return new SparseRectangularLensletArray(this);
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}

	
	
	//
	// setters & getters
	//
	
	public void setCorner(Vector3D corner)
	{
		parallelogram.setCorner(corner);
	}
	
	public Vector3D getCorner()
	{
		return parallelogram.getCorner();
	}

	public void setSpanVectors(Vector3D spanVector1, Vector3D spanVector2)
	{
		parallelogram.setSpanVectors(spanVector1, spanVector2);
	}

	public Vector3D getSpanVector1()
	{
		return parallelogram.getSpanVector1();
	}

	public Vector3D getSpanVector2()
	{
		return parallelogram.getSpanVector2();
	}


	
//	/**
//	 * 
//	 * Editable-interface stuff
//	 * 
//	 */
//	
//	/**
//	 * variables
//	 */
//	private JPanel editPanel;
//	private LabelledStringPanel descriptionPanel;
//	private LabelledVector3DPanel centrePanel, widthVectorPanel, heightVectorPanel;
//	private LabelledDoublePanel lensletArrayFocalLengthPanel, lensletArrayTransmissionCoefficientPanel;
//	private LabelledVector2DPanel lensletArrayPeriodPanel, lensletArrayOffsetPanel;
//
//
//	/**
//	 * initialise the edit panel
//	 */
//	@Override
//	public void createEditPanel(IPanel iPanel)
//	{	
//		editPanel = new JPanel();
//		editPanel.setLayout(new MigLayout("insets 0"));
//		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lenslet array"));
//		
//		//
//		// the basic-parameters panel
//		//
//		
//		// a text field containing the description
//		descriptionPanel = new LabelledStringPanel("Description");
//		editPanel.add(descriptionPanel, "wrap");
//				
//		centrePanel = new LabelledVector3DPanel("Centre");
//		editPanel.add(centrePanel, "wrap");
//
//		widthVectorPanel = new LabelledVector3DPanel("Vector along width");
//		editPanel.add(widthVectorPanel, "wrap");
//
//		heightVectorPanel = new LabelledVector3DPanel("Vector along height");
//		editPanel.add(heightVectorPanel, "wrap");
//
//		//
//		// the lenslet-array parameters
//		//
//		
//		lensletArrayFocalLengthPanel = new LabelledDoublePanel("Focal length");
//		lensletArrayFocalLengthPanel.setNumber(1);
//		editPanel.add(lensletArrayFocalLengthPanel, "span");
//		
//		lensletArrayPeriodPanel = new LabelledVector2DPanel("Period in (x,y)");
//		lensletArrayPeriodPanel.setVector2D(0.1, 0.1);
//		editPanel.add(lensletArrayPeriodPanel, "span");
//		
//		lensletArrayOffsetPanel = new LabelledVector2DPanel("Offset in (x,y)");
//		lensletArrayOffsetPanel.setVector2D(0, 0);
//		editPanel.add(lensletArrayOffsetPanel, "span");
//		
//		lensletArrayTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient");
//		lensletArrayTransmissionCoefficientPanel.setNumber(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT);
//		editPanel.add(lensletArrayTransmissionCoefficientPanel, "span");;
//
//		editPanel.validate();
//	}
//
//	@Override
//	public void discardEditPanel()
//	{
//		editPanel = null;
//	}
//
//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.Editable#getEditPanel()
//	 */
//	@Override
//	public JPanel getEditPanel()
//	{
//		return editPanel;
//	}
//
//	
//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
//	 */
//	@Override
//	public void setValuesInEditPanel()
//	{
//		descriptionPanel.setString(getDescription());
//		centrePanel.setVector3D(getCentre());
//		widthVectorPanel.setVector3D(getSpanVector1());
//		heightVectorPanel.setVector3D(getSpanVector2());
//		
//		// initialize any fields
//		lensletArrayFocalLengthPanel.setNumber(sparseLensArray.getFocalLength());
//		lensletArrayTransmissionCoefficientPanel.setNumber(sparseLensArray.getTransmissionCoefficient());
//		lensletArrayPeriodPanel.setVector2D(sparseLensArray.getxPeriod(), sparseLensArray.getyPeriod());
//		lensletArrayOffsetPanel.setVector2D(sparseLensArray.getxOffset(), sparseLensArray.getyOffset());
//	}
//
//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
//	 */
//	@Override
//	public SparseRectangularLensletArray acceptValuesInEditPanel()
//	{
//		// don't use super.acceptValuesInEditPanel(); as this sets the surface property and the scaling;
//		// instead, copy the relevant code from it
//		setDescription(descriptionPanel.getString());
//		
//		setCentreAndSpanVectors(
//				centrePanel.getVector3D(),
//				widthVectorPanel.getVector3D(),
//				heightVectorPanel.getVector3D()
//			);
//		
//		sparseLensArray.setFocalLength(lensletArrayFocalLengthPanel.getNumber());
//		sparseLensArray.setxPeriod(lensletArrayPeriodPanel.getVector2D().x);
//		sparseLensArray.setyPeriod(lensletArrayPeriodPanel.getVector2D().y);
//		sparseLensArray.setxOffset(lensletArrayOffsetPanel.getVector2D().x);
//		sparseLensArray.setyOffset(lensletArrayOffsetPanel.getVector2D().y);
//		sparseLensArray.setTransmissionCoefficient(lensletArrayTransmissionCoefficientPanel.getNumber());
//		
//		return this;
//	}
//
//	@Override
//	public void backToFront(IPanelComponent edited) {}
}
