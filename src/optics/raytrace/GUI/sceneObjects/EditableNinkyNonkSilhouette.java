package optics.raytrace.GUI.sceneObjects;

import math.Vector3D;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;

/**
 * The silhouette of the Ninky Nonk.
 * @author Johannes Courtial
 */
public class EditableNinkyNonkSilhouette extends EditableSilhouette implements IPanelComponent
{
	private static final long serialVersionUID = 1222395209293439474L;

	private static final String CLASS_DESCRIPTION = "Ninky Nonk Silhouette";
	
	/**
	 * a description of this scene-object class, which comes up in the edit panel
	 */
	@Override
	protected String getSceneObjectClassDescription()
	{
		return CLASS_DESCRIPTION;
	}
	
	/**
	 * the name of the image that will define the silhouette
	 */
	@Override
	protected String getImageFilename()
	{
		return "NinkyNonkSilhouette.png";
	}
	
	/**
	 * the aspect ratio of the image image
	 */
	@Override
	protected double getAspectRatio()
	{
		return 1027./300.;
	}

	
	//
	// constructors
	//
	
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
	public EditableNinkyNonkSilhouette(
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
		super(description, bottomLeftCorner, rightDirection, upDirection, width, parent, studio);
	}
	
	
	public EditableNinkyNonkSilhouette(SceneObject parent, Studio studio)
	{
		this(
				CLASS_DESCRIPTION,	// description
				new Vector3D(50, 0, 1000),	// centre
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
	public EditableNinkyNonkSilhouette(EditableNinkyNonkSilhouette original)
	throws SceneException
	{
		super(original);

		setEitherOrSurface();
	}

	@Override
	public EditableNinkyNonkSilhouette clone()
	{
		try {
			return new EditableNinkyNonkSilhouette(this);
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}


//	private static final long serialVersionUID = 2488684757263368024L;
//	
//	private Vector3D rightDirection, upDirection;
//	private double width;
//
//	// GUI panels
//	private JPanel editPanel;
//	private LabelledStringPanel descriptionPanel;
//	private LabelledVector3DPanel bottomLeftCornerPanel, rightDirectionPanel, upDirectionPanel;
//	private LabelledDoublePanel widthPanel;
//	
//	/**
//	 * Default constructor
//	 * 
//	 * @param description
//	 * @param bottomLeftCorner
//	 * @param rightDirection
//	 * @param upDirection
//	 * @param width
//	 * @param parent
//	 * @param studio
//	 */
//	public EditableNinkyNonkSilhouette(
//			String description,
//			Vector3D bottomLeftCorner,
//			Vector3D rightDirection,
//			Vector3D upDirection,
//			double width,
//			SceneObject parent, 
//			Studio studio
//	)
//	{
//		// constructor of superclass
//		super(
//				description,
//				bottomLeftCorner,
//				rightDirection.getWithLength(width),
//				upDirection.getWithLength(width*300./1027.),	// aspect ratio is 1027/300
//				0, 1, 1, 0,
//				null,	// SurfaceProperty
//				parent, 
//				studio
//			);
//		
//		setGeometry(bottomLeftCorner, rightDirection, upDirection, width);
//		
//		setNinkyNonkSurface();
//	}
//	
//	
//	public EditableNinkyNonkSilhouette(SceneObject parent, Studio studio)
//	{
//		this(
//				"Ninky Nonk Silhouette",	// description
//				new Vector3D(100, 0, 1000),	// centre
//				new Vector3D(1, 0, 0),	// rightDirection
//				new Vector3D(0, 1, 0),	// upDirection
//				100,	// width
//				parent, studio
//			);
//	}
//
//
//	/**
//	 * Create a clone of original
//	 * @param original
//	 */
//	public EditableNinkyNonkSilhouette(EditableNinkyNonkSilhouette original)
//	{
//		super(original);
//
//		setNinkyNonkSurface();
//	}
//
//	public EditableNinkyNonkSilhouette clone()
//	{
//		return new EditableNinkyNonkSilhouette(this);
//	}
//	
//	public void setGeometry(
//			Vector3D corner,
//			Vector3D rightDirection,
//			Vector3D upDirection,
//			double width
//		)
//	{
//		this.rightDirection = rightDirection;
//		this.upDirection = upDirection;
//		this.width = width;
//
//		setCorner(corner);
//		setSpanVectors(
//				rightDirection.getWithLength(width),	// span vector 1
//				upDirection.getWithLength(width*300./1027.)	// span vector 2; aspect ratio is 1027/300
//			);
//	}
//	
//	private void setNinkyNonkSurface()
//	{
//		BufferedImage ninkyNonk;
//		
//		java.net.URL imgURL = this.getClass().getResource("NinkyNonk.png");
//		
//		if(imgURL != null)
//		{
//			try
//			{
//				ninkyNonk = ImageIO.read(imgURL);
//			} catch (IOException e)
//			{
//				System.err.println("Couldn't load file NinkyNonk.png");
//				ninkyNonk = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
//				e.printStackTrace();
//			}
//		}
//		else
//		{
//			System.err.println("Couldn't find file NinkyNonk.png");
//			ninkyNonk = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
//		}
//
//		setSurfaceProperty(
//				new EitherOrSurface(
//						ninkyNonk,	// picture, 
//						0,	// xMin
//						1,	// xMax
//						0,	// yMin
//						1,	// yMax
//						SurfaceColour.BLACK_MATT,	// surfaceProperty0
//						Transparent.PERFECT	// surfaceProperty1
//						)
//				);
//	}
//	
//
//
//	/**
//	 * initialise the edit panel
//	 */
//	@Override
//	public void createEditPanel(IPanel iPanel)
//	{	
//		editPanel = new JPanel();
//		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ninky Nonk Silhouette"));
//        editPanel.setLayout(new MigLayout("insets 0"));
//        
//        // c.fill = GridBagConstraints.BOTH;
//		
//		// a text field containing the description
//		descriptionPanel = new LabelledStringPanel("Description");
//		editPanel.add(descriptionPanel, "wrap");
//
//		// the centre
//		
//		bottomLeftCornerPanel = new LabelledVector3DPanel("Bottom left corner position");
//		editPanel.add(bottomLeftCornerPanel, "wrap");
//		
//		// span vectors
//		
//		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
//		editPanel.add(rightDirectionPanel, "wrap");
//
//		upDirectionPanel = new LabelledVector3DPanel("Up direction");
//		editPanel.add(upDirectionPanel, "wrap");
//		
//		widthPanel = new LabelledDoublePanel("Width");
//		editPanel.add(widthPanel);
//		
//		editPanel.validate();
//	}
//	
//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.Editable#setValuesInEditPanel()
//	 */
//	@Override
//	public void setValuesInEditPanel()
//	{
//		// initialize any fields
//		descriptionPanel.setString(getDescription());
//		bottomLeftCornerPanel.setVector3D(getCorner());
//		rightDirectionPanel.setVector3D(rightDirection);
//		upDirectionPanel.setVector3D(upDirection);
//		widthPanel.setNumber(width);
//	}
//
//	/* (non-Javadoc)
//	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
//	 */
//	@Override
//	public EditableNinkyNonkSilhouette acceptValuesInEditPanel()
//	{
//		setDescription(descriptionPanel.getString());
//
//		setGeometry(
//				bottomLeftCornerPanel.getVector3D(),	// centre
//				rightDirectionPanel.getVector3D(),	// spanVector1
//				upDirectionPanel.getVector3D(),	// spanVector2
//				widthPanel.getNumber()
//			);
//		
//		return this;
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

}
