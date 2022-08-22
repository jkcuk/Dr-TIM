package optics.raytrace.GUI.sceneObjects;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.SceneObjectWithHoles;
import optics.raytrace.surfaces.EitherOrSurface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.ImageUtil;

/**
 * A scene object that renders a given text string.
 * @author Johannes Courtial
 */
public class EditableText extends SceneObjectWithHoles implements IPanelComponent
{
	private static final long serialVersionUID = -2577321201923912599L;

	/**
	 * the string that gets displayed
	 */
	private String text;
	private Vector3D bottomLeftCorner, rightDirection, upDirection;
	/**
	 * the font size determines how pixellated the rendered text appears -- the larger the font size, the less pixellated
	 */
	private int fontSize;
	private String fontFamily;
	private double height;
	private SurfaceProperty textSurfaceProperty;

	// GUI panels
	private JPanel editPanel;
	private LabelledStringPanel descriptionPanel;
	private JTextField textField;
	private LabelledVector3DPanel bottomLeftCornerPanel, rightDirectionPanel, upDirectionPanel;
	private LabelledIntPanel fontSizePanel;
	private JComboBox<String> fontFamilyComboBox;
	private LabelledDoublePanel heightPanel;
	private SurfacePropertyPanel textSurfacePropertyPanel;
	
	
	
	/**
	 * Default constructor
	 * 
	 * @param description
	 * @param text
	 * @param bottomLeftCorner
	 * @param rightDirection
	 * @param upDirection
	 * @param fontSize
	 * @param height
	 * @param textSurfaceProperty
	 * @param parent
	 * @param studio
	 */
	public EditableText(
			String description,
			String text,
			Vector3D bottomLeftCorner,
			Vector3D rightDirection,
			Vector3D upDirection,
			int fontSize,
			String fontFamily,
			double height,
			SurfaceProperty textSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description);
		setParent(parent);
		setStudio(studio);
		
		setText(text);
		setBottomLeftCorner(bottomLeftCorner);
		setRightDirection(rightDirection);
		setUpDirection(upDirection);
		setFontSize(fontSize);
		setFontFamily(fontFamily);
		setHeight(height);
		setTextSurfaceProperty(textSurfaceProperty);
		
		setup();
	}
	
	
	public EditableText(SceneObject parent, Studio studio)
	{
		this(
				"Text",	// description
				"<i>Hello</i>, <b>world</b>!",	// text
				new Vector3D(-2, 0, 30),	// bottom left corner
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				512,	// fontSize
				"Arial",	// fontFamily
				1,	// height
				SurfaceColour.BLACK_SHINY,	// textSurfaceProperty
				parent, studio
			);
	}


	/**
	 * Create a clone of original
	 * @param original
	 * @throws SceneException 
	 */
	public EditableText(EditableText original)
	{
		this(
				original.getDescription(),
				original.getText(),
				original.getBottomLeftCorner(),
				original.getRightDirection(),
				original.getUpDirection(),
				original.getFontSize(),
				original.getFontFamily(),
				original.getHeight(),
				original.getTextSurfaceProperty(),
				original.getParent(), 
				original.getStudio()
			);
	}

	@Override
	public EditableText clone()
	{
		return new EditableText(this);
	}
	
	
	
	/**
	 * @param description
	 * @param text
	 * @param centre
	 * @param rightDirection
	 * @param upDirection
	 * @param fontSize
	 * @param fontFamily
	 * @param height
	 * @param textSurfaceProperty
	 * @param parent
	 * @param studio
	 * @return	an EditableText scene object, centred on <i>centre</i>
	 */
	public static EditableText getCentredEditableText(
			String description,
			String text,
			Vector3D centre,
			Vector3D rightDirection,
			Vector3D upDirection,
			int fontSize,
			String fontFamily,
			double height,
			SurfaceProperty textSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		double width = calculateWidth(text, fontFamily, fontSize, height);
		
		return new EditableText(
				description,
				text,
				Vector3D.sum(
						centre,
						rightDirection.getWithLength(-width/2),
						upDirection.getPartPerpendicularTo(rightDirection).getWithLength(-height/2)
					),	// bottomLeftCorner
				rightDirection,
				upDirection,
				fontSize,
				fontFamily,
				height,
				textSurfaceProperty,
				parent, 
				studio
			);
	}


	

	//
	// getters & setters
	//
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public int getFontSize() {
		return fontSize;
	}


	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}



	public String getFontFamily() {
		return fontFamily;
	}


	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}


	public double getHeight() {
		return height;
	}


	public void setHeight(double height) {
		this.height = height;
	}


	public SurfaceProperty getTextSurfaceProperty() {
		return textSurfaceProperty;
	}


	public void setTextSurfaceProperty(SurfaceProperty textSurfaceProperty) {
		this.textSurfaceProperty = textSurfaceProperty;
	}


	public Vector3D getBottomLeftCorner() {
		return bottomLeftCorner;
	}


	public void setBottomLeftCorner(Vector3D bottomLeftCorner) {
		this.bottomLeftCorner = bottomLeftCorner;
	}


	public Vector3D getRightDirection() {
		return rightDirection;
	}


	public void setRightDirection(Vector3D rightDirection) {
		this.rightDirection = rightDirection;
	}


	public Vector3D getUpDirection() {
		return upDirection;
	}


	public void setUpDirection(Vector3D upDirection) {
		this.upDirection = upDirection;
	}

	
	public static BufferedImage calculateImage(String text, String fontFamily, int fontSize)
	{
		// ImageUtil.stringToBufferedImage(text, fontSize, Color.white);
		return ImageUtil.htmlStringToBufferedImage(text, fontFamily, fontSize);
	}
	
	public static double calculateWidth(String text, String fontFamily, int fontSize, double height)
	{
		BufferedImage image = calculateImage(text, fontFamily, fontSize);

		return height/image.getHeight()*image.getWidth();
	}

	/**
	 * call this method after all parameters have been set
	 */
	public void setup()
	{
		BufferedImage image = calculateImage(text, fontFamily, fontSize);

		try {
			setWrappedSceneObject(new EditableScaledParametrisedParallelogram(
					description,
					bottomLeftCorner,
					rightDirection.getWithLength(calculateWidth(text, fontFamily, fontSize, height)),
					upDirection.getPartPerpendicularTo(rightDirection).getWithLength(height),
					0, 1, 1, 0,
					textSurfaceProperty,	// SurfaceProperty
					getParent(),
					getStudio()
				));
		} catch (SceneException e) {
			// This should never happen
			e.printStackTrace();
		}
		
		EitherOrSurface eitherOrSurface = new EitherOrSurface(
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
	



	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Text"));
        editPanel.setLayout(new MigLayout("insets 0"));
        
        // c.fill = GridBagConstraints.BOTH;
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		// a text field containing the text (in HTML markup)
		textField = new JTextField();
		textField.setColumns(25);
		// textPanel = new LabelledStringPanel("Text (HTML)");
		editPanel.add(new LabelledComponent("Text (html)", textField), "wrap");

		fontSizePanel = new LabelledIntPanel("Font size");
		editPanel.add(fontSizePanel, "wrap");
		
		String fontNames[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		fontFamilyComboBox = new JComboBox<String>(fontNames);
		editPanel.add(new LabelledComponent("Font family", fontFamilyComboBox), "wrap");
		
		heightPanel = new LabelledDoublePanel("Text height");
		editPanel.add(heightPanel, "wrap");
		
		textSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		editPanel.add(new LabelledComponent("Text surface property", textSurfacePropertyPanel), "wrap");
		textSurfacePropertyPanel.setIPanel(iPanel);

		// the centre
		
		bottomLeftCornerPanel = new LabelledVector3DPanel("Bottom left corner position");
		editPanel.add(bottomLeftCornerPanel, "wrap");
		
		// span vectors
		
		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
		editPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Up direction");
		editPanel.add(upDirectionPanel, "wrap");
		
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
		textField.setText(getText());
		bottomLeftCornerPanel.setVector3D(getBottomLeftCorner());
		rightDirectionPanel.setVector3D(rightDirection);
		upDirectionPanel.setVector3D(upDirection);
		fontSizePanel.setNumber(fontSize);
		fontFamilyComboBox.setSelectedItem(fontFamily);
		heightPanel.setNumber(height);
		textSurfacePropertyPanel.setSurfaceProperty(textSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableText acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setText(textField.getText());
		setFontSize(fontSizePanel.getNumber());
		setFontFamily((String)(fontFamilyComboBox.getSelectedItem()));
		setHeight(heightPanel.getNumber());
		setTextSurfaceProperty(textSurfacePropertyPanel.getSurfaceProperty());
		setBottomLeftCorner(bottomLeftCornerPanel.getVector3D());
		setRightDirection(rightDirectionPanel.getVector3D());
		setUpDirection(upDirectionPanel.getVector3D());
		
		setup();

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
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				setTextSurfaceProperty((SurfaceProperty)edited);
				textSurfacePropertyPanel.setSurfaceProperty(getTextSurfaceProperty());
			}
	}
}
