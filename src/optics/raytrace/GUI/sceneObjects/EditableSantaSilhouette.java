package optics.raytrace.GUI.sceneObjects;

import math.Vector3D;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.exceptions.SceneException;

/**
 * The silhouette of Santa.
 * Image from http://www.clipartqueen.com/image-files/silhouette-of-santa-and-reindeer-christmas.png
 * Alternative image from http://gallery.yopriceville.com/Free-Clipart-Pictures/Christmas-PNG/Santa_with_Sleigh_Silhouette_Transparent_PNG_Clip_Art_Image#.WVpx5sZ7Hq4
 * @author Johannes Courtial
 */
public class EditableSantaSilhouette extends EditableSilhouette implements IPanelComponent
{
	private static final long serialVersionUID = 1932741524979154462L;
	
	private static final String CLASS_DESCRIPTION = "Santa Silhouette";
	
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
		return "SantaSilhouette.png";
	}
	
	/**
	 * the aspect ratio of the image image
	 */
	@Override
	protected double getAspectRatio()
	{
		return 1476./296.;	// 6343./2128.; // 2418./645.;
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
	public EditableSantaSilhouette(
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
	
	
	public EditableSantaSilhouette(SceneObject parent, Studio studio)
	{
		this(
				CLASS_DESCRIPTION,	// description
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
	public EditableSantaSilhouette(EditableSantaSilhouette original)
	throws SceneException
	{
		super(original);

		setEitherOrSurface();
	}

	@Override
	public EditableSantaSilhouette clone()
	{
		try {
			return new EditableSantaSilhouette(this);
		} catch (SceneException e) {
			// this should never happen
			e.printStackTrace();
			return null;
		}
	}
}
