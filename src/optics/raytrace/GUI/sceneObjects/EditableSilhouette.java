package optics.raytrace.GUI.sceneObjects;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
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
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.SceneObjectWithHoles;
import optics.raytrace.surfaces.EitherOrSurface;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * A silhouette defined by a fixed file.
 * @author Johannes Courtial
 */
public abstract class EditableSilhouette extends SceneObjectWithHoles implements IPanelComponent
{
	private static final long serialVersionUID = 1673771200705216814L;

	/**
	 * a description of this scene-object class, which comes up in the edit panel
	 */
	protected abstract String getSceneObjectClassDescription();
	
	/**
	 * the name of the image that will define the silhouette
	 */
	protected abstract String getImageFilename();
	
	/**
	 * the aspect ratio of the image image
	 */
	protected abstract double getAspectRatio();

	private Vector3D rightDirection, upDirection;
	private double width;
	
	
	private EitherOrSurface eitherOrSurface;	// the holey surface
	

	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param bottomLeftCorner
	 * @param rightDirection
	 * @param upDirection
	 * @param width
	 * @param parent
	 * @param studio
	 * @throws SceneException 
	 */
	public EditableSilhouette(
			String description,
			Vector3D bottomLeftCorner,
			Vector3D rightDirection,
			Vector3D upDirection,
			double width,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description);

		try {
			setWrappedSceneObject(new EditableScaledParametrisedParallelogram(
					description,
					bottomLeftCorner,
					rightDirection.getWithLength(width),
					upDirection.getWithLength(width/getAspectRatio()),
					0, 1, 1, 0,
					SurfaceColour.BLACK_MATT,	// SurfaceProperty
					parent, 
					studio
				));
		} catch (SceneException e) {
			// This should never happen
			e.printStackTrace();
		}
		
		setGeometry(bottomLeftCorner, rightDirection, upDirection, width);
		
		setEitherOrSurface();
	}
	
	
	public EditableSilhouette(SceneObject parent, Studio studio)
	{
		this(
				"Silhouette",	// description
				new Vector3D(100, 0, 1000),	// centre
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				100,	// width
				parent, studio
			);
	}


	/**
	 * Create a clone of original
	 * @param original
	 * @throws SceneException 
	 */
	public EditableSilhouette(EditableSilhouette original) throws SceneException
	{
		super(original);

		setEitherOrSurface();
	}

	@Override
	public abstract EditableSilhouette clone();
//	{
//		try {
//			return new EditableSilhouette(this);
//		} catch (SceneException e) {
//			// this should never happen
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public EditableScaledParametrisedParallelogram getWrappedParallelogram()
	{
		return (EditableScaledParametrisedParallelogram)wrappedSceneObject;
	}
	
	public void setGeometry(
			Vector3D corner,
			Vector3D rightDirection,
			Vector3D upDirection,
			double width
		)
	{
		this.rightDirection = rightDirection;
		this.upDirection = upDirection;
		this.width = width;

		getWrappedParallelogram().setCorner(corner);
		getWrappedParallelogram().setSpanVectors(
				rightDirection.getWithLength(width),	// span vector 1
				upDirection.getWithLength(width/getAspectRatio())	// span vector 2
			);
	}
	
	protected void setEitherOrSurface()
	{
		BufferedImage image;
		
		java.net.URL imgURL = this.getClass().getResource(getImageFilename());
		
		if(imgURL != null)
		{
			try
			{
				image = ImageIO.read(imgURL);
			} catch (IOException e)
			{
				System.err.println("Couldn't load file "+getImageFilename());
				image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
				e.printStackTrace();
			}
		}
		else
		{
			System.err.println("Couldn't find file "+getImageFilename());
			image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
		}

		eitherOrSurface = new EitherOrSurface(
				image,	// picture, 
				0,	// xMin
				1,	// xMax
				0,	// yMin
				1,	// yMax
				null,	// SurfaceColour.BLACK_MATT,	// surfaceProperty0
				null	// Transparent.PERFECT	// surfaceProperty1
				);
		eitherOrSurface.setSurfaceTypeCorrespondingToHole(1);
		
		setHoleySurface(eitherOrSurface);
	}
	
	
	
	// GUI panels
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private LabelledVector3DPanel bottomLeftCornerPanel, rightDirectionPanel, upDirectionPanel;
	private LabelledDoublePanel widthPanel;
	private JCheckBox isInvertedPanel;
	



	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder(getSceneObjectClassDescription()));
        editPanel.setLayout(new MigLayout("insets 0"));
        
        // c.fill = GridBagConstraints.BOTH;
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		// the centre
		
		bottomLeftCornerPanel = new LabelledVector3DPanel("Bottom left corner position");
		editPanel.add(bottomLeftCornerPanel, "wrap");
		
		// span vectors
		
		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
		editPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Up direction");
		editPanel.add(upDirectionPanel, "wrap");
		
		widthPanel = new LabelledDoublePanel("Width");
		editPanel.add(widthPanel);
		
		isInvertedPanel = new JCheckBox("Inverted");
		editPanel.add(isInvertedPanel);
		
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
		bottomLeftCornerPanel.setVector3D(getWrappedParallelogram().getCorner());
		rightDirectionPanel.setVector3D(rightDirection);
		upDirectionPanel.setVector3D(upDirection);
		widthPanel.setNumber(width);
		isInvertedPanel.setSelected(eitherOrSurface.getSurfaceTypeCorrespondingToHole() == 1);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableSilhouette acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setGeometry(
				bottomLeftCornerPanel.getVector3D(),	// centre
				rightDirectionPanel.getVector3D(),	// spanVector1
				upDirectionPanel.getVector3D(),	// spanVector2
				widthPanel.getNumber()
			);
		
		eitherOrSurface.setSurfaceTypeCorrespondingToHole(isInvertedPanel.isSelected()?1:0);
		
		return this;
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
	
	@Override
	public void backToFront(IPanelComponent edited) {}
}
