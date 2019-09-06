package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.SceneObjectListPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.sceneObjects.transformations.Translation;
import math.*;

/**
 * An array of arbitrary scene objects.
 * @author Johannes Courtial
 */
public class EditableArray extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 8317798023923581191L;

	// parameters
	private SceneObjectContainer arrayUnitCell;
	private double xMin, xMax, dx, yMin, yMax, dy, zMin, zMax, dz;

	// GUI panels
	private LabelledDoublePanel xMinPanel, xMaxPanel, dxPanel, yMinPanel, yMaxPanel, dyPanel, zMinPanel, zMaxPanel, dzPanel;
	private JButton convertButton;

	public EditableArray(
			String description,
			double xMin, double xMax, double dx,
			double yMin, double yMax, double dy,
			double zMin, double zMax, double dz,
			SceneObjectContainer arrayUnitCell,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.xMin = xMin;
		this.xMax = xMax;
		this.dx = dx;
		this.yMin = yMin;
		this.yMax = yMax;
		this.dy = dy;
		this.zMin = zMin;
		this.zMax = zMax;
		this.dz = dz;
		this.arrayUnitCell = arrayUnitCell;

		buildArray();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableArray(EditableArray original)
	{
		this(
				original.getDescription(),
				original.getxMin(), original.getxMax(), original.getDx(),
				original.getyMin(), original.getyMax(), original.getDy(),
				original.getzMin(), original.getzMax(), original.getDz(),
				original.arrayUnitCell,
				original.getParent(),
				original.getStudio()
			);
	}

	@Override
	public EditableArray clone()
	{
		return new EditableArray(this);
	}


	public double getxMin() {
		return xMin;
	}

	public void setxMin(double xMin) {
		this.xMin = xMin;
	}

	public double getxMax() {
		return xMax;
	}

	public void setxMax(double xMax) {
		this.xMax = xMax;
	}

	public double getyMin() {
		return yMin;
	}

	public void setyMin(double yMin) {
		this.yMin = yMin;
	}

	public double getyMax() {
		return yMax;
	}

	public void setyMax(double yMax) {
		this.yMax = yMax;
	}

	public double getzMin() {
		return zMin;
	}

	public void setzMin(double zMin) {
		this.zMin = zMin;
	}

	public double getzMax() {
		return zMax;
	}

	public void setzMax(double zMax) {
		this.zMax = zMax;
	}
	
	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getDz() {
		return dz;
	}

	public void setDz(double dz) {
		this.dz = dz;
	}

	public SceneObjectContainer getArrayUnitCell() {
		return arrayUnitCell;
	}

	public void setArrayUnitCell(SceneObjectContainer arrayUnitCell) {
		this.arrayUnitCell = arrayUnitCell;
	}

	private void buildArray()
	{
		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		if(arrayUnitCell != null)
		// ... and add copies of the unit cell
		for(double x=xMin; x<=xMax; x+=dx)
		{
			for(double y=yMin; y<=yMax; y+=dy)
			{
				for(double z=zMin; z<=zMax; z+=dz)
				{
					addSceneObject(arrayUnitCell.clone().transform(new Translation(new Vector3D(x, y, z))));
				}
			}
		}
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Array"));
		
		editPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		//
		// all x-axis related bits and bobs
		//
		xMinPanel = new LabelledDoublePanel("x_min");
		editPanel.add(xMinPanel, "split 3");
		
		xMaxPanel = new LabelledDoublePanel(", x_max");
		editPanel.add(xMaxPanel);
		
		dxPanel = new LabelledDoublePanel(", dx");
		editPanel.add(dxPanel, "wrap");
		
		//
		// all y-axis related bits and bobs
		//
		yMinPanel = new LabelledDoublePanel("y_min");
		editPanel.add(yMinPanel, "split 3");
		
		yMaxPanel = new LabelledDoublePanel(", y_max");
		editPanel.add(yMaxPanel);
		
		dyPanel = new LabelledDoublePanel(", dy");
		editPanel.add(dyPanel, "wrap");

		//
		// all z-axis related bits and bobs
		//
		zMinPanel = new LabelledDoublePanel("z_min");
		editPanel.add(zMinPanel, "split 3");
		
		zMaxPanel = new LabelledDoublePanel(", z_max");
		editPanel.add(zMaxPanel);
		
		dzPanel = new LabelledDoublePanel(", dz");
		editPanel.add(dzPanel, "wrap");
		
		// the array unit cell

		sceneObjectListPanel = new SceneObjectListPanel("Array unit cell", getArrayUnitCell(), this, iPanel);
		editPanel.add(sceneObjectListPanel, "span");
		
		// the convert button

		// The convert button doesn't quite work, I think because all the cloned and transformed editable scene objects
		// are for some reason no longer editable.  So, for the moment, comment the button out.
		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		// editPanel.add(convertButton);
		
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
		xMinPanel.setNumber(getxMin());
		xMaxPanel.setNumber(getxMax());
		dxPanel.setNumber(getDx());
		yMinPanel.setNumber(getyMin());
		yMaxPanel.setNumber(getyMax());
		dyPanel.setNumber(getDy());
		zMinPanel.setNumber(getzMin());
		zMaxPanel.setNumber(getzMax());
		dzPanel.setNumber(getDz());
		sceneObjectListPanel.setSceneObjectContainer(getArrayUnitCell());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableArray acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		// getSceneObjects().clear();

		setxMin(xMinPanel.getNumber());
		setxMax(xMaxPanel.getNumber());
		setDx(dxPanel.getNumber());
		setyMin(yMinPanel.getNumber());
		setyMax(yMaxPanel.getNumber());
		setDy(dyPanel.getNumber());
		setzMin(zMinPanel.getNumber());
		setzMax(zMaxPanel.getNumber());
		setDz(dzPanel.getNumber());

		// add all the objects
		buildArray();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-array");
		container.setValuesInEditPanel();
	}
	
	public void addSceneObjectToUnitCell(SceneObject sceneObject)
	{
		arrayUnitCell.addSceneObject(sceneObject);
	}
}
