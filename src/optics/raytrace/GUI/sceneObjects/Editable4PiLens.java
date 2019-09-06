package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.One2OneParametrisedObject;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.LensHologram;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable 4Pi lens which completely surrounds "inside space", which is the tetrahedral cell
 * immediately next to the principal-plane lens.
 * 
 * @author Johannes
 */
public class Editable4PiLens extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 8315511425597793508L;

	// parameters
	
	/**
	 * the position of the principal point
	 */
	private Vector3D principalPoint;
	/**
	 * the normalised direction of the optical axis
	 */
	private Vector3D opticalAxisDirection;
	/**
	 * the focal length (inside space to outside space)
	 */
	private double focalLengthOutwards;
	/**
	 * the first of the transverse directions, normalised
	 */
	private Vector3D transverseDirection1;
	/**
	 * the second of the transverse directions, normalised
	 */
	private Vector3D transverseDirection2;
	/**
	 * radius of 4 Pi lens;
	 * this is the radius of the triangular principal-plane lens, i.e. the
	 * distance from the principal point to the vertices of the lens in the principal plane
	 */
	private double radius;
	/**
	 * axial length of the lens
	 */
	private double length;
	/**
	 * should the lens L1 be shown?
	 */
	private boolean showL1;
	/**
	 * should the remaining lenses, R1, be shown?
	 */
	private boolean showR1;
	/**
	 * the distance from the principal point to the closer inner vertex
	 */
	private double closerInnerVertexDistance;
	/**
	 * the distance from the principal point to the farther inner vertex
	 */
	private double fartherInnerVertexDistance;
	
	public enum LensType {
		IDEAL_THIN_LENS("Ideal thin lens")
		, LENS_HOLOGRAM("Phase hologram of lens")
		// , FRESNEL_LENS("Fresnel lens (eye-position-image optimised)")
		// , LENS_HOLOGRAM_EYE("Phase hologram of lens (eye-position-image optimised)")
		;

		private String description;
		private LensType(String description)
		{
			this.description = description;
		}
		@Override
		public String toString() {return description;}
	}
	
	/**
	 * either IDEAL_THIN_LENS or LENS_HOLOGRAM
	 */
	private LensType lensType;
	
	/**
	 * transmission coefficient of each lens
	 */
	private double individualLensTransmissionCoefficient;	// transmission coefficient of each constituent lens
	/**
	 * radius of the cylinders that form the frame
	 */
	private double frameRadius;
	private boolean showFrames, showPlaceholderSurfaces;
	private SurfaceProperty frameSurfaceProperty;
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection lenses, frames;
	
	// GUI panels
	private LabelledVector3DPanel principalPointPanel, transverseDirection1Panel, transverseDirection2Panel, opticalAxisDirectionPanel;
	private LabelledDoublePanel focalLengthOutwardsPanel, lengthPanel, radiusPanel,
		closerInnerVertexDistancePanel, fartherInnerVertexDistancePanel,
		individualLensTransmissionCoefficientPanel, frameRadiusPanel;
	private JButton convertButton;
	private JCheckBox showL1CheckBox, showR1CheckBox, showFramesCheckBox, showPlaceholderSurfacesCheckBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	private JComboBox<LensType> lensTypeComboBox;
	
	
	//
	// constructors
	//
	
	public Editable4PiLens(
			String description,
			Vector3D principalPoint,
			Vector3D opticalAxisDirection,
			double focalLengthOutwards,
			Vector3D transverseDirection1,
			Vector3D transverseDirection2,
			double radius,
			double length,
			boolean showL1,
			boolean showR1,
			double closerInnerVertexDistance,
			double fartherInnerVertexDistance,
			LensType lensType,
			double individualLensTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			boolean showPlaceholderSurfaces,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setPrincipalPoint(principalPoint);
		setDirections(
				opticalAxisDirection,
				transverseDirection1,
				transverseDirection2
			);
		setFocalLengthOutwards(focalLengthOutwards);
		setRadius(radius);
		setLength(length);
		setShowL1(showL1);
		setShowR1(showR1);
		setCloserInnerVertexDistance(closerInnerVertexDistance);
		setFartherInnerVertexDistance(fartherInnerVertexDistance);
		setLensType(lensType);
		setIndividualLensTransmissionCoefficient(individualLensTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);
		setShowPlaceholderSurfaces(showPlaceholderSurfaces);

		populateSceneObjectCollection();
	}

	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public Editable4PiLens(SceneObject parent, Studio studio)
	{
		this(
				"4Pi lens",	// description
				new Vector3D(0, 0, 5),	// principalPoint
				new Vector3D(0, 0, 1),	// opticalAxisDirection
				1,	// focalLengthOutwards
				new Vector3D(1, 0, 0),	// transverseDirection1
				new Vector3D(0, 1, 0),	// transverseDirection2
				1,	// radius
				1,	// length
				false,	// showL1
				true,	// showR1
				1./3.,	// closerInnerVertexDistance
				2./3.,	// fartherInnerVertexDistance
				LensType.IDEAL_THIN_LENS,	// lensType
				SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// individualLensTransmissionCoefficient
				false,	// showFrames
				0.01,	// frameRadius
				SurfaceColour.GREY50_SHINY,	// frameSurfaceProperty
				false,	// showPlaceholderSurfaces
				parent, 
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public Editable4PiLens(Editable4PiLens original)
	{
		this(
			original.getDescription(),
			original.getPrincipalPoint(),
			original.getOpticalAxisDirection(),
			original.getFocalLengthOutwards(),
			original.getTransverseDirection1(),
			original.getTransverseDirection2(),
			original.getRadius(),
			original.getLength(),
			original.isShowL1(),
			original.isShowR1(),
			original.getCloserInnerVertexDistance(),
			original.getFartherInnerVertexDistance(),
			original.getLensType(),
			original.getIndividualLensTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),
			original.isShowPlaceholderSurfaces(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public Editable4PiLens clone()
	{
		return new Editable4PiLens(this);
	}

	
	
	//
	// setters and getters
	//
	
	public Vector3D getPrincipalPoint() {
		return principalPoint;
	}

	public void setPrincipalPoint(Vector3D principalPoint) {
		this.principalPoint = principalPoint;
	}

	public double getFocalLengthOutwards() {
		return focalLengthOutwards;
	}

	public void setFocalLengthOutwards(double focalLengthOutwards) {
		this.focalLengthOutwards = focalLengthOutwards;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public boolean isShowL1() {
		return showL1;
	}

	public void setShowL1(boolean showL1) {
		this.showL1 = showL1;
	}

	public boolean isShowR1() {
		return showR1;
	}

	public void setShowR1(boolean showR1) {
		this.showR1 = showR1;
	}

	public NamedVector3D getOpticalAxisDirection() {
		return new NamedVector3D("optical-axis direction", opticalAxisDirection);
	}
	
	public NamedVector3D getTransverseDirection1() {
		return new NamedVector3D("transverse direction 1", transverseDirection1);
	}
	
	public NamedVector3D getTransverseDirection2() {
		return new NamedVector3D("transverse direction 2", transverseDirection2);
	}

	/**
	 * First set this.opticalAxisDirection to the normalised opticalAxisDirection;
	 * then set this.frontDirection to the normalised part of frontDirection that is perpendicular to topDirection;
	 * then set this.rightDirection to the normalised part of rightDirection that is perpendicular to this.topDirection and this.frontDirection.
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 */
	public void setDirections(
			Vector3D opticalAxisDirection,
			Vector3D transverseDirection1,
			Vector3D transverseDirection2
		)
	{
		this.opticalAxisDirection = opticalAxisDirection.getNormalised();
		this.transverseDirection1 = transverseDirection1.getPartPerpendicularTo(opticalAxisDirection).getNormalised();
		this.transverseDirection2 = transverseDirection2.getPartParallelTo(
				Vector3D.crossProduct(this.opticalAxisDirection, this.transverseDirection1)
			).getNormalised();
	}

	public double getCloserInnerVertexDistance() {
		return closerInnerVertexDistance;
	}

	public void setCloserInnerVertexDistance(double closerInnerVertexDistance) {
		this.closerInnerVertexDistance = closerInnerVertexDistance;
	}

	public double getFartherInnerVertexDistance() {
		return fartherInnerVertexDistance;
	}

	public void setFartherInnerVertexDistance(double fartherInnerVertexDistance) {
		this.fartherInnerVertexDistance = fartherInnerVertexDistance;
	}

	public LensType getLensType() {
		return lensType;
	}

	public void setLensType(LensType lensType) {
		this.lensType = lensType;
	}

	public double getIndividualLensTransmissionCoefficient() {
		return individualLensTransmissionCoefficient;
	}

	public void setIndividualLensTransmissionCoefficient(double individualLensTransmissionCoefficient) {
		this.individualLensTransmissionCoefficient = individualLensTransmissionCoefficient;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
		
		setSceneObjectVisible(frames, showFrames);
	}
	
	public boolean isShowPlaceholderSurfaces() {
		return showPlaceholderSurfaces;
	}

	public void setShowPlaceholderSurfaces(boolean showPlaceholderSurfaces) {
		this.showPlaceholderSurfaces = showPlaceholderSurfaces;
	}

	public double getFrameRadius()
	{
		return frameRadius;
	}
	
	public void setFrameRadius(double frameRadius)
	{
		this.frameRadius = frameRadius;
	}
	
	public SurfaceProperty getFrameSurfaceProperty()
	{
		return frameSurfaceProperty;
	}
	
	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty)
	{
		this.frameSurfaceProperty = frameSurfaceProperty;
	}
		
	public EditableSceneObjectCollection getLenses() {
		return lenses;
	}

	public EditableSceneObjectCollection getFrames() {
		return frames;
	}

	
	
	//
	// the important bit:  add the scene objects that form this box cloak
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this box cloak
	 */
	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// calculate the windows and the frames
		calculateVertices();
		addLenses();
		addFrames();
	}
	
	
	
	// the vertex positions
	protected Vector3D tipVertex;
	protected Vector3D principalPlaneVertex1;
	protected Vector3D principalPlaneVertex2;
	protected Vector3D principalPlaneVertex3;
	protected Vector3D closerInnerVertex;
	protected Vector3D fartherInnerVertex;
	
	
	protected void calculateVertices()
	{
		// the top vertex is a distance height above the baseCentre
		tipVertex = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(length));
		
		// a distance baseRadius from the principal point
		principalPlaneVertex1 = Vector3D.sum(principalPoint, transverseDirection1.getProductWith(radius));

		double
			angle = MyMath.deg2rad(120.),	// 120 degrees, in radians
			cos = Math.cos(angle),	// cos(120 degrees)
			sin = Math.sin(angle);	// sin(120 degrees)
		
		// a distance baseRadius from the principal point, at is an angle 120 degrees w.r.t. transverseDirection1
		principalPlaneVertex2 = Vector3D.sum(
				principalPoint,
				transverseDirection1.getProductWith(cos*radius),
				transverseDirection2.getProductWith(sin*radius)
			);

		// a distance baseRadius from the principal point, at is an angle -120 degrees w.r.t. transverseDirection1
		principalPlaneVertex3 = Vector3D.sum(
				principalPoint,
				transverseDirection1.getProductWith(cos*radius),
				transverseDirection2.getProductWith(-sin*radius)
			);
		
		// the inner vertices
				
		closerInnerVertex = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(closerInnerVertexDistance));
		fartherInnerVertex = Vector3D.sum(principalPoint, opticalAxisDirection.getProductWith(fartherInnerVertexDistance));
	}
	
	
	//
	// the lenses
	//

	// collections for the lenses
	protected EditableSceneObjectCollection r1Lenses;
	
	/**
	 * create the EditableSceneObjectCollection lenses, populate it, and add it to this
	 * EditableSceneObjectCollection
	 */
	private void addLenses()
	{
		// create a collection for the windows
		lenses = new EditableSceneObjectCollection("Lenses", false, this, getStudio());
		addSceneObject(lenses);

		// the lens L1
		addLens(
				"L1",	// description
				principalPlaneVertex1,	// vertex 1
				principalPlaneVertex2,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				principalPoint,	// nodalPoint
				-focalLengthOutwards,	// focal length
				lenses,	// collection
				showL1
			);
		
		// create a collection for the lenses forming R1
		r1Lenses = new EditableSceneObjectCollection("R1", false, lenses, getStudio());
		lenses.addSceneObject(r1Lenses, showR1);
		
		// calculate the other focal lengths
		// first introduce the snappier variable names used in the Mathematica file
		double
			h3 = length,
			h2 = fartherInnerVertexDistance,
			h1 = closerInnerVertexDistance,
			R = radius,
			f00 = -focalLengthOutwards;
		
		double focalLengthTipOuter = 
				(
					(-h2+h3)*(f00*(h1-h3) + h1*h3)*R) /
					(h1*h2*Math.sqrt(4*h3*h3 + R*R)
				);
		double focalLengthFartherInner = (f00*(h1 - h2)*(h2 - h3)*R)/(h1*h3*Math.sqrt(4*h2*h2 + R*R));
		double focalLengthCloserInner = -(((h1 - h2)*(f00*(h1 - h3) + h1*h3)*R)/(h2*h3*Math.sqrt(4*h1*h1 + R*R)));		
		double focalLengthFartherInnerMeridional = ((h2 - h3)*(f00*(h1 - h3) + h1*h3)*R)/(2*Math.sqrt(3)*h1*h2*h3);
		double focalLengthCloserInnerMeridional = -(((h1 - h2)*(f00*(h1 - h3) + h1*h3)*R)/(2*Math.sqrt(3)*h1*h2*h3));
		
		addLens(
				"Tip outer lens 1",	// description
				tipVertex,	// vertex 1
				principalPlaneVertex1,	// vertex 2
				principalPlaneVertex2,	// vertex 3
				tipVertex,	// nodalPoint
				focalLengthTipOuter,	// focal length
				r1Lenses
			);		
		addLens(
				"Tip outer lens 2",	// description
				tipVertex,	// vertex 1
				principalPlaneVertex2,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				tipVertex,	// nodalPoint
				focalLengthTipOuter,	// focal length
				r1Lenses
			);		
		// the right outer lens
		addLens(
				"Tip outer lens 3",	// description
				tipVertex,	// vertex 1
				principalPlaneVertex3,	// vertex 2
				principalPlaneVertex1,	// vertex 3
				tipVertex,	// nodalPoint
				focalLengthTipOuter,	// focal length
				r1Lenses
			);		

		addLens(
				"Farther inner lens 1",	// description
				fartherInnerVertex,	// vertex 1
				principalPlaneVertex1,	// vertex 2
				principalPlaneVertex2,	// vertex 3
				fartherInnerVertex,	// nodal point
				focalLengthFartherInner,	// focal length
				r1Lenses	// collection
			);
		
		addLens(
				"Farther inner lens 2",	// description
				fartherInnerVertex,	// vertex 1
				principalPlaneVertex2,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				fartherInnerVertex,	// nodal point
				focalLengthFartherInner,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Farther inner lens 3",	// description
				fartherInnerVertex,	// vertex 1
				principalPlaneVertex3,	// vertex 2
				principalPlaneVertex1,	// vertex 3
				fartherInnerVertex,	// nodal point
				focalLengthFartherInner,	// focal length
				r1Lenses	// collection
			);


		addLens(
				"Closer inner lens 1",	// description
				closerInnerVertex,	// vertex 1
				principalPlaneVertex1,	// vertex 2
				principalPlaneVertex2,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInner,	// focal length
				r1Lenses	// collection
			);
		
		addLens(
				"Closer inner lens 2",	// description
				closerInnerVertex,	// vertex 1
				principalPlaneVertex2,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInner,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Closer inner lens 3",	// description
				closerInnerVertex,	// vertex 1
				principalPlaneVertex3,	// vertex 2
				principalPlaneVertex1,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInner,	// focal length
				r1Lenses	// collection
			);

		
		// the farther meridional lenses

		addLens(
				"Farther meridional lens 1",	// description
				tipVertex,	// vertex 1
				fartherInnerVertex,	// vertex 2
				principalPlaneVertex1,	// vertex 3
				tipVertex,	// nodal point
				focalLengthFartherInnerMeridional,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Farther meridional lens 2",	// description
				tipVertex,	// vertex 1
				fartherInnerVertex,	// vertex 2
				principalPlaneVertex2,	// vertex 3
				tipVertex,	// nodal point
				focalLengthFartherInnerMeridional,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Farther meridional lens 3",	// description
				tipVertex,	// vertex 1
				fartherInnerVertex,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				tipVertex,	// nodal point
				focalLengthFartherInnerMeridional,	// focal length
				r1Lenses	// collection
			);

		
		// the closer meridional lenses

		addLens(
				"Closer meridional lens 1",	// description
				fartherInnerVertex,	// vertex 1
				closerInnerVertex,	// vertex 2
				principalPlaneVertex1,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInnerMeridional,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Closer meridional lens 2",	// description
				fartherInnerVertex,	// vertex 1
				closerInnerVertex,	// vertex 2
				principalPlaneVertex2,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInnerMeridional,	// focal length
				r1Lenses	// collection
			);

		addLens(
				"Closer meridional lens 3",	// description
				fartherInnerVertex,	// vertex 1
				closerInnerVertex,	// vertex 2
				principalPlaneVertex3,	// vertex 3
				closerInnerVertex,	// nodal point
				focalLengthCloserInnerMeridional,	// focal length
				r1Lenses	// collection
			);
	}

	/**
	 * Add a lens that's visible
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param nodalPoint
	 * @param focalLength
	 * @param collection
	 */
	protected void addLens(
			String description, 
			Vector3D vertex1, Vector3D vertex2, Vector3D vertex3,
			Vector3D nodalPoint,
			double focalLength,	// focal length
			EditableSceneObjectCollection collection
		)
	{
		addLens(
				description, 
				vertex1, vertex2, vertex3,
				nodalPoint,
				focalLength,	// focal length
				collection,
				true	// isVisible
			);
	}

	protected void addLens(
			String description, 
			Vector3D vertex1, Vector3D vertex2, Vector3D vertex3,
			Vector3D principalPoint,
			double focalLength,	// focal length
			EditableSceneObjectCollection collection,
			boolean isVisible
		)
	{
		// the "span" vectors
		Vector3D
			v12 = Vector3D.difference(vertex2, vertex1),	// vector from vertex 1 to vertex 2
			v13 = Vector3D.difference(vertex3, vertex1);	// vector from vertex 1 to vertex 3
		
		// calculate the optical-axis direction, which is a normalised a vector that is perpendicular to both span vectors
		Vector3D
			opticalAxisDirection = Vector3D.crossProduct(v12, v13).getNormalised();

		// set the lens hologram parameters
		SurfaceProperty lensHologram;
		switch(lensType)
		{
		case LENS_HOLOGRAM:
			lensHologram = new LensHologram(
					opticalAxisDirection,
					principalPoint,
					focalLength,
					individualLensTransmissionCoefficient,	// transmissionCoefficient
					false	// shadowThrowing
				);
			break;
		case IDEAL_THIN_LENS:
		default:
			lensHologram = new IdealThinLensSurface(
				opticalAxisDirection,
				principalPoint,
				focalLength,
				individualLensTransmissionCoefficient,	// transmissionCoefficient
				false	// shadowThrowing
			);
//		GlensSurface lensHologram = new GlensSurface(individualLensTransmissionCoefficient, false);
//		lensHologram.setParametersUsingNodalPoint(opticalAxisDirection, principalPoint, -focalLength, focalLength);
		}

		collection.addSceneObject(
				new EditableTriangle(
						description,
						vertex1,
						v12,	// vertex1ToVertex2
						v13,	// vertex1ToVertex3
						false,	// semiInfinite
						v12,	// uUnitVector
						v13,	// vUnitVector
						(isShowPlaceholderSurfaces())
							// ?SurfaceColour.RED_SHINY
							// ?(new ColourFilter(new DoubleColour(0.9, 0, 0), false))
							?(ColourFilter.CYAN_GLASS)
							// ?(new GlensHologram(individualLensTransmissionCoefficient, false))
							:lensHologram,	// surfaceProperty,
						collection,	// parent,
						getStudio()	// studio
					),
				isVisible
			);
	}
	
	
	//
	// the frames
	//

	
	// collections for the edges and the vertices
	protected EditableSceneObjectCollection edges, vertices;

	protected void addFrames()
	{
		// create a collection for the frames
		frames = new EditableSceneObjectCollection("Frames", false, this, getStudio());
		addSceneObject(frames, showFrames);

		// create a collection for the vertices
		vertices = new EditableSceneObjectCollection("Vertices", false, frames, getStudio());
		frames.addSceneObject(vertices);
		
		// add the vertices
		addFrameSphere("Tip vertex", tipVertex, vertices);
		addFrameSphere("Principal-plane vertex 1", principalPlaneVertex1, vertices);
		addFrameSphere("Principal-plane vertex 2", principalPlaneVertex2, vertices);
		addFrameSphere("Principal-plane vertex 3", principalPlaneVertex3, vertices);
		addFrameSphere("Closer inner vertex", closerInnerVertex, vertices);
		addFrameSphere("Farther inner vertex", fartherInnerVertex, vertices);

		// create a collection for the edges
		edges = new EditableSceneObjectCollection("Edges", false, frames, getStudio());
		frames.addSceneObject(edges);
		
		// add the edges
		
		// the outer edges
		addFrameCylinder("Tip to principal-plane vertex 1", tipVertex, principalPlaneVertex1, edges);
		addFrameCylinder("Tip to principal-plane vertex 2", tipVertex, principalPlaneVertex2, edges);
		addFrameCylinder("Tip to principal-plane vertex 3", tipVertex, principalPlaneVertex3, edges);
		addFrameCylinder("Principal-plane edge 1", principalPlaneVertex1, principalPlaneVertex2, edges);
		addFrameCylinder("Principal-plane edge 2", principalPlaneVertex2, principalPlaneVertex3, edges);
		addFrameCylinder("Principal-plane edge 3", principalPlaneVertex3, principalPlaneVertex1, edges);
		
		// the edges to the upper inner vertex
		addFrameCylinder("Tip to farther inner vertex", tipVertex, fartherInnerVertex, edges);
		addFrameCylinder("Farther inner vertex to principal-plane vertex 1", principalPlaneVertex1, fartherInnerVertex, edges);
		addFrameCylinder("Farther inner vertex to principal-plane vertex 2", principalPlaneVertex2, fartherInnerVertex, edges);
		addFrameCylinder("Farther inner vertex to principal-plane vertex 3", principalPlaneVertex3, fartherInnerVertex, edges);

		// the edges to the lower inner vertex
		addFrameCylinder("Closer inner vertex to farther inner vertex", fartherInnerVertex, closerInnerVertex, edges);
		addFrameCylinder("Closer inner vertex to principal-plane vertex 1", principalPlaneVertex1, closerInnerVertex, edges);
		addFrameCylinder("Closer inner vertex to principal-plane vertex 2", principalPlaneVertex2, closerInnerVertex, edges);
		addFrameCylinder("Closer inner vertex to principal-plane vertex 3", principalPlaneVertex3, closerInnerVertex, edges);
	}
	
	protected void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableScaledParametrisedSphere(
				description,
				centrePosition,	// centre
				frameRadius,	// radius
				frameSurfaceProperty,
				collection,
				getStudio()
		));
	}

	protected void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableParametrisedCylinder(
				description,
				startPosition,	// start point
				endPosition,	// end point
				frameRadius,	// radius
				frameSurfaceProperty,
				collection,
				getStudio()
		));
	}
	

	
	
	
	
	//
	// GUI stuff
	//
	
	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("4Pi lens"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		principalPointPanel = new LabelledVector3DPanel("Principal point");
		basicParametersPanel.add(principalPointPanel, "wrap");

		opticalAxisDirectionPanel = new LabelledVector3DPanel("Optical-axis direction");
		basicParametersPanel.add(opticalAxisDirectionPanel, "wrap");
		
		focalLengthOutwardsPanel = new LabelledDoublePanel("Focal length (outwards)");
		basicParametersPanel.add(focalLengthOutwardsPanel, "wrap");

		transverseDirection1Panel = new LabelledVector3DPanel("Transverse direction 1");
		basicParametersPanel.add(transverseDirection1Panel, "wrap");

		transverseDirection2Panel = new LabelledVector3DPanel("Transverse direction 2");
		basicParametersPanel.add(transverseDirection2Panel, "wrap");

		// basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		radiusPanel = new LabelledDoublePanel("Radius (distance centre to vertex of triangular lens in principal plane)");
		basicParametersPanel.add(radiusPanel, "wrap");

		lengthPanel = new LabelledDoublePanel("Length");
		basicParametersPanel.add(lengthPanel, "wrap");

		closerInnerVertexDistancePanel = new LabelledDoublePanel("Distance of closer inner vertex from principal point");
		basicParametersPanel.add(closerInnerVertexDistancePanel, "wrap");

		fartherInnerVertexDistancePanel = new LabelledDoublePanel("Distance of farther inner vertex from principal point");
		basicParametersPanel.add(fartherInnerVertexDistancePanel, "wrap");
		
		showL1CheckBox = new JCheckBox("Show lens L1");
		basicParametersPanel.add(showL1CheckBox, "wrap");

		showR1CheckBox = new JCheckBox("Show lens structure R1");
		basicParametersPanel.add(showR1CheckBox, "wrap");
		
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		basicParametersPanel.add(new LabelledComponent("Lens type", lensTypeComboBox), "wrap");

		individualLensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each individual lens");
		basicParametersPanel.add(individualLensTransmissionCoefficientPanel, "wrap");

		showPlaceholderSurfacesCheckBox = new JCheckBox("Show placeholder surfaces");
		basicParametersPanel.add(showPlaceholderSurfacesCheckBox, "wrap");

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));

		showFramesCheckBox = new JCheckBox("Show frames");
		framePanel.add(showFramesCheckBox, "wrap");
		
		frameRadiusPanel = new LabelledDoublePanel("frame cylinder radius");
		framePanel.add(frameRadiusPanel, "wrap");
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel(getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel, "wrap");
		frameSurfacePropertyPanel.setIPanel(iPanel);

		// omitInnermostSurfacesCheckBox = new JCheckBox("Simplified cloak (omit innermost interfaces)");
		// editPanel.add(omitInnermostSurfacesCheckBox);
		
		tabbedPane.addTab("Frames", framePanel);
		
		editPanel.add(tabbedPane, "wrap");

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
		
		principalPointPanel.setVector3D(getPrincipalPoint());
		opticalAxisDirectionPanel.setVector3D(getOpticalAxisDirection());
		focalLengthOutwardsPanel.setNumber(getFocalLengthOutwards());
		transverseDirection1Panel.setVector3D(getTransverseDirection1());
		transverseDirection2Panel.setVector3D(getTransverseDirection2());
		radiusPanel.setNumber(getRadius());
		lengthPanel.setNumber(getLength());
		closerInnerVertexDistancePanel.setNumber(getCloserInnerVertexDistance());
		fartherInnerVertexDistancePanel.setNumber(getFartherInnerVertexDistance());
		showL1CheckBox.setSelected(isShowL1());
		showR1CheckBox.setSelected(isShowR1());
		lensTypeComboBox.setSelectedItem(lensType);
		individualLensTransmissionCoefficientPanel.setNumber(getIndividualLensTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		showPlaceholderSurfacesCheckBox.setSelected(isShowPlaceholderSurfaces());
		frameRadiusPanel.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public Editable4PiLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setPrincipalPoint(principalPointPanel.getVector3D());
		setDirections(
				opticalAxisDirectionPanel.getVector3D(),
				transverseDirection1Panel.getVector3D(),
				transverseDirection2Panel.getVector3D()
			);
		setFocalLengthOutwards(focalLengthOutwardsPanel.getNumber());
		setLength(lengthPanel.getNumber());
		setRadius(radiusPanel.getNumber());
		setCloserInnerVertexDistance(closerInnerVertexDistancePanel.getNumber());
		setFartherInnerVertexDistance(fartherInnerVertexDistancePanel.getNumber());
		setShowL1(showL1CheckBox.isSelected());
		setShowR1(showR1CheckBox.isSelected());
		setLensType(((LensType)lensTypeComboBox.getSelectedItem()));
		setIndividualLensTransmissionCoefficient(individualLensTransmissionCoefficientPanel.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setShowPlaceholderSurfaces(showPlaceholderSurfacesCheckBox.isSelected());
		setFrameRadius(frameRadiusPanel.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());
		// setOmitInnermostSurfaces(omitInnermostSurfacesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-4Pi-lens");
		container.setValuesInEditPanel();
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof SurfaceProperty)
			{
				// frame surface property has been edited
				setFrameSurfaceProperty((SurfaceProperty)edited);
				frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
			}
	}
}