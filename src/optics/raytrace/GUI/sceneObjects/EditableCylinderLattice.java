package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.SurfaceColour;
import math.*;

/**
 * A lattice of cylinders.
 * @author Johannes Courtial
 */
public class EditableCylinderLattice extends EditableSceneObjectCollection implements ActionListener
{	
	private static final long serialVersionUID = -22753857270547329L;

	// parameters
	private double xMin, xMax, yMin, yMax, zMin, zMax, radius;
	private int nX, nY, nZ;	// number of cylinders in x, y, and z direction
	private Vector3D xVector, yVector, zVector, centre;  //////

	// GUI panels
//	private JPanel editPanel;
//	private StringLine descriptionPanel;
	private LabelledDoublePanel xMinPanel, xMaxPanel, yMinPanel, yMaxPanel, zMinPanel, zMaxPanel, radiusPanel;
	private LabelledIntPanel nXPanel, nYPanel, nZPanel;
	private LabelledVector3DPanel xVectorPanel, yVectorPanel, zVectorPanel, centrePanel; //////
	private JButton convertButton;

	
	/**
	 * standard constructor. The lattice will be constructed in the x, y and z directions
	 * @param description
	 * @param xMin
	 * @param xMax
	 * @param nX
	 * @param yMin
	 * @param yMax
	 * @param nY
	 * @param zMin
	 * @param zMax
	 * @param nZ
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableCylinderLattice(
			String description,
			double xMin, double xMax, int nX,
			double yMin, double yMax, int nY,
			double zMin, double zMax, int nZ,
			double radius,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.xMin = xMin;
		this.xMax = xMax;
		this.nX = nX;
		this.yMin = yMin;
		this.yMax = yMax;
		this.nY = nY;
		this.zMin = zMin;
		this.zMax = zMax;
		this.nZ = nZ;
		this.radius = radius;
		
		this.centre = new Vector3D(0,0,0);
		this.xVector = Vector3D.X;
		this.yVector = Vector3D.Y;
		this.zVector = Vector3D.Z;

		addCylinders();
	}
	
	/**
	 * Centre of lattice specifiable. The lattice will be constructed in the x, y and z directions
	 * @param description
	 * @param xMin
	 * @param xMax
	 * @param nX
	 * @param yMin
	 * @param yMax
	 * @param nY
	 * @param zMin
	 * @param zMax
	 * @param nZ
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableCylinderLattice(
			String description,
			double xMin, double xMax, int nX,
			double yMin, double yMax, int nY,
			double zMin, double zMax, int nZ,
			double radius,
			Vector3D centre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.xMin = xMin;
		this.xMax = xMax;
		this.nX = nX;
		this.yMin = yMin;
		this.yMax = yMax;
		this.nY = nY;
		this.zMin = zMin;
		this.zMax = zMax;
		this.nZ = nZ;
		this.radius = radius;
		
		this.centre = centre;
		this.xVector = Vector3D.X;
		this.yVector = Vector3D.Y;
		this.zVector = Vector3D.Z;

		addCylinders();
	}
	
	/**
	 * this constructor allows one to choose the three axis along which the lattice is constructed with (0,0,0) as its centre
	 * @param description
	 * @param xMin
	 * @param xMax
	 * @param nX
	 * @param xVector
	 * @param yMin
	 * @param yMax
	 * @param nY
	 * @param yVector
	 * @param zMin
	 * @param zMax
	 * @param nZ
	 * @param zVector
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableCylinderLattice(
			String description,
			double xMin, double xMax, int nX, Vector3D xVector,
			double yMin, double yMax, int nY, Vector3D yVector,
			double zMin, double zMax, int nZ, Vector3D zVector,
			double radius,	
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.xMin = xMin;
		this.xMax = xMax;
		this.nX = nX;
		this.yMin = yMin;
		this.yMax = yMax;
		this.nY = nY;
		this.zMin = zMin;
		this.zMax = zMax;
		this.nZ = nZ;
		this.radius = radius;

		this.centre = new Vector3D(0,0,0);
		this.xVector = xVector.getNormalised();
		this.yVector = yVector.getNormalised();
		this.zVector = zVector.getNormalised();
		
		addCylinders();
	}
	
	/**
	 * this constructor allows one to choose the three axis along which the lattice is constructed and where its centred
	 * @param description
	 * @param xMin
	 * @param xMax
	 * @param nX
	 * @param xVector
	 * @param yMin
	 * @param yMax
	 * @param nY
	 * @param yVector
	 * @param zMin
	 * @param zMax
	 * @param nZ
	 * @param zVector
	 * @param radius
	 * @param parent
	 * @param studio
	 */
	public EditableCylinderLattice(
			String description,
			double xMin, double xMax, int nX, Vector3D xVector,
			double yMin, double yMax, int nY, Vector3D yVector,
			double zMin, double zMax, int nZ, Vector3D zVector,
			double radius,
			Vector3D centre,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		this.xMin = xMin;
		this.xMax = xMax;
		this.nX = nX;
		this.yMin = yMin;
		this.yMax = yMax;
		this.nY = nY;
		this.zMin = zMin;
		this.zMax = zMax;
		this.nZ = nZ;
		this.radius = radius;

		this.centre = centre;
		this.xVector = xVector.getNormalised();
		this.yVector = yVector.getNormalised();
		this.zVector = zVector.getNormalised();
		
		addCylinders();
	}
	

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCylinderLattice(EditableCylinderLattice original)
	{
		super(original);
		
		// copy the original's parameters
		this.xMin = original.xMin;
		this.xMax = original.xMax;
		this.nX = original.nX;
		this.yMin = original.yMin;
		this.yMax = original.yMax;
		this.nY = original.nY;
		this.zMin = original.zMin;
		this.zMax = original.zMax;
		this.nZ = original.nZ;
		this.radius = original.radius;
		
		this.centre = original.centre;
		this.xVector = original.xVector;
		this.yVector = original.yVector;
		this.zVector = original.zVector;
	}

	@Override
	public EditableCylinderLattice clone()
	{
		return new EditableCylinderLattice(this);
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

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public int getnX() {
		return nX;
	}

	public void setnX(int nX) {
		this.nX = nX;
	}

	public int getnY() {
		return nY;
	}

	public void setnY(int nY) {
		this.nY = nY;
	}

	public int getnZ() {
		return nZ;
	}

	public void setnZ(int nZ) {
		this.nZ = nZ;
	}
	
	public Vector3D getXVector() {
		return xVector;
	}
	
	public void setXVector(Vector3D xVector) {
		this.xVector = xVector.getNormalised();
	}
	
	public Vector3D getYVector() {
		return yVector;
	}
	
	public void setYVector(Vector3D yVector) {
		this.yVector = yVector.getNormalised();
	}
	
	public Vector3D getZVector() {
		return zVector;
	}
	
	public void setZVector(Vector3D zVector) {
		this.zVector = zVector.getNormalised();
	}
	
	public Vector3D getCentre() {
		return centre;
	}
	
	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}
	
	private void addCylinders()
	{
		// create all the cylinders
		EditableSceneObjectCollection redCylinders = new EditableSceneObjectCollection("red cylinders", true, this, getStudio());
		for(int i=0; i<nX; i++)
		{
			for(int j=0; j<nY; j++)
			{
				double
					x = (nX == 1)?xMin:(xMin + i*(xMax-xMin)/(nX-1)),
					y = (nY == 1)?yMin:(yMin + j*(yMax-yMin)/(nY-1));
				redCylinders.addSceneObject(new EditableParametrisedCylinder(
						"red cylinder",
						Vector3D.sum(centre, xVector.getProductWith(x), yVector.getProductWith(y), zVector.getProductWith(zMin)),//new Vector3D(x, y, zMin),	// start point
						Vector3D.sum(centre, xVector.getProductWith(x), yVector.getProductWith(y), zVector.getProductWith(zMax)),//new Vector3D(x, y, zMax),	// end point
						radius,	// radius
						SurfaceColour.RED_SHINY,
						redCylinders,
						getStudio()
				));
			}
		}
		addSceneObject(redCylinders);

		EditableSceneObjectCollection blueCylinders = new EditableSceneObjectCollection("blue cylinders", true, this, getStudio());
		for(int k=0; k<nZ; k++)
		{
			for(int j=0; j<nY; j++)
			{
				double
					y = (nY == 1)?yMin:(yMin + j*(yMax-yMin)/(nY-1)),
					z = (nZ == 1)?zMin:(zMin + k*(zMax-zMin)/(nZ-1));
				blueCylinders.addSceneObject(new EditableParametrisedCylinder(
						"blue cylinder",
						Vector3D.sum(centre, xVector.getProductWith(xMin), yVector.getProductWith(y), zVector.getProductWith(z)), //new Vector3D(xMin, y, z),	// start point
						Vector3D.sum(centre, xVector.getProductWith(xMax), yVector.getProductWith(y), zVector.getProductWith(z)),//new Vector3D(xMax, y, z),	// end point
						radius,	// radius
						SurfaceColour.BLUE_SHINY,
						blueCylinders,
						getStudio()
				));
			}
		}
		addSceneObject(blueCylinders);

		EditableSceneObjectCollection greenCylinders = new EditableSceneObjectCollection("green cylinders", true, this, getStudio());
		for(int i=0; i<nX; i++)
		{
			for(int k=0; k<nZ; k++)
			{
				double
					x = (nX == 1)?xMin:(xMin + i*(xMax-xMin)/(nX-1)),
					z = (nZ == 1)?zMin:(zMin + k*(zMax-zMin)/(nZ-1));
				greenCylinders.addSceneObject(new EditableParametrisedCylinder(
						"green cylinder",
						Vector3D.sum(centre, xVector.getProductWith(x), yVector.getProductWith(yMin), zVector.getProductWith(z)),//new Vector3D(x, yMin, z),	// start point
						Vector3D.sum(centre, xVector.getProductWith(x), yVector.getProductWith(yMax), zVector.getProductWith(z)),//new Vector3D(x, yMax, z),	// end point
						radius,	// radius
						SurfaceColour.GREEN_SHINY,
						greenCylinders,
						getStudio()
				));
			}
		}
		addSceneObject(greenCylinders);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setLayout(new MigLayout("insets 0"));

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cylinder lattice"));

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
		
		nXPanel = new LabelledIntPanel(", no of cylinders");
		editPanel.add(nXPanel, "wrap");
		
		xVectorPanel = new LabelledVector3DPanel(", x direction");
		editPanel.add(xVectorPanel, "wrap"); //"wrap"
		
		//
		// all y-axis related bits and bobs
		//
		yMinPanel = new LabelledDoublePanel("y_min");
		editPanel.add(yMinPanel, "split 3");
		
		yMaxPanel = new LabelledDoublePanel(", y_max");
		editPanel.add(yMaxPanel);
		
		nYPanel = new LabelledIntPanel(", no of cylinders");
		editPanel.add(nYPanel, "wrap");
		
		yVectorPanel = new LabelledVector3DPanel(", y direction");
		editPanel.add(yVectorPanel, "wrap"); //"wrap"
		
		//
		// all z-axis related bits and bobs
		//
		zMinPanel = new LabelledDoublePanel("z_min");
		editPanel.add(zMinPanel, "split 3");
		
		zMaxPanel = new LabelledDoublePanel(", z_max");
		editPanel.add(zMaxPanel);
		
		nZPanel = new LabelledIntPanel(", no of cylinders");
		editPanel.add(nZPanel, "wrap");
		
		zVectorPanel = new LabelledVector3DPanel(", z direction");
		editPanel.add(zVectorPanel, "wrap"); //"wrap"
		
		// the radius

		radiusPanel = new LabelledDoublePanel("Cylinder radius");
		editPanel.add(radiusPanel, "wrap");
		
		// the centre
		
		centrePanel = new LabelledVector3DPanel(", the centre of the lattice");
		editPanel.add(centrePanel, "wrap");  //"wrap"???		
		
		// the convert button

		convertButton = new JButton("Convert to collection of scene objects");
		convertButton.addActionListener(this);
		editPanel.add(convertButton, "south");

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
		nXPanel.setNumber(getnX());
		xVectorPanel.setVector3D(getXVector());
		yMinPanel.setNumber(getyMin());
		yMaxPanel.setNumber(getyMax());
		nYPanel.setNumber(getnY());
		yVectorPanel.setVector3D(getYVector());
		zMinPanel.setNumber(getzMin());
		zMaxPanel.setNumber(getzMax());
		nZPanel.setNumber(getnZ());
		zVectorPanel.setVector3D(getZVector());
		radiusPanel.setNumber(getRadius());
		centrePanel.setVector3D(getCentre());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCylinderLattice acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		// start afresh
		getSceneObjects().clear();

		setxMin(xMinPanel.getNumber());
		setxMax(xMaxPanel.getNumber());
		setnX(nXPanel.getNumber());
		setXVector(xVectorPanel.getVector3D().getNormalised());
		setyMin(yMinPanel.getNumber());
		setyMax(yMaxPanel.getNumber());
		setnY(nYPanel.getNumber());
		setYVector(yVectorPanel.getVector3D().getNormalised());
		setzMin(zMinPanel.getNumber());
		setzMax(zMaxPanel.getNumber());
		setnZ(nZPanel.getNumber());
		setZVector(zVectorPanel.getVector3D().getNormalised());
		setRadius(radiusPanel.getNumber());
		setCentre(centrePanel.getVector3D());

		// get rid of anything that's in this SceneObjectContainer at the moment...
		clear();
		
		// ... and add the cylinders
		addCylinders();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-cylinder lattice");
		container.setValuesInEditPanel();
	}
}
