package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.sceneObjects.boxCloaks.NamedVector3D;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.GlensHologram;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable TO device made from lenses, with the lenses optionally framed.
 * The outside of the device is tetrahedral; inside, there are two vertices.
 * The top of the tetrahedron is a distance h above the centre of the base;
 * the first inner vertex is distances a' h and a h (0<a,a'<1) above the base in EM space and physical space, respectively;
 * the second inner vertex is distances b' h and b h (0<b,b'<1) above the base in EM space and physical space, respectively.
 * 
 * @author Johannes
 */
public class EditableLensTOTetrahedron extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -5136284717328079330L;

	// parameters
	
	/**
	 * the position of the centre of the base of the tetrahedron
	 */
	protected Vector3D baseCentre;
	/**
	 * the normalised direction to the front of the tetrahedron
	 */
	protected Vector3D frontDirection;	// direction from centre to front face; length irrelevant (size of cloak given by sideLength)
	/**
	 * the normalised direction to the right of the tetrahedron
	 */
	protected Vector3D rightDirection;	// direction from centre to right face; length irrelevant (size of cloak given by sideLength)
	/**
	 * the normalised direction to the top of the tetrahedron; this direction is perpendicular to the base
	 */
	protected Vector3D topDirection;	// direction from centre to top face; length irrelevant (size of cloak given by sideLength)
	/**
	 * radius of the tetrahedron's base, i.e. distance of the base vertices from the base centre
	 */
	protected double baseRadius;
	/**
	 * height, h, of the tetrahedron
	 */
	protected double height;
	/**
	 * the first inner vertex is a distance a h (0<a<1) above the base in EM space space
	 */
	protected double a;
	/**
	 * the first inner vertex is a distance a' h (0<a'<1) above the base in physical space space
	 */
	protected double aPrime;
	/**
	 * the second inner vertex is a distance b h (0<b<1) above the base in EM space space
	 * -- NO LONGER TRUE: THE EM-SPACE POSITION OF THE SECOND INNER VORTEX IS CALCULATED AUTOMATICALLY --
	 */
	// protected double b;
	/**
	 * the second inner vertex is a distance b' h (0<b'<1) above the base in physical space space
	 */
	protected double bPrime;
	/**
	 * transmission coefficient of each interface
	 */
	protected double interfaceTransmissionCoefficient;	// transmission coefficient of each interface
	/**
	 * radius of the cylinders that form the frame
	 */
	protected double frameRadius;
	protected boolean showFrames, showPlaceholderSurfaces;	// , omitInnermostSurfaces;
	protected SurfaceProperty frameSurfaceProperty;
	
	// containers for the windows and frames
	protected EditableSceneObjectCollection windows, frames;
	
	// GUI panels
	protected LabelledVector3DPanel baseCentreLine, frontDirectionLine, rightDirectionLine, topDirectionLine;
	protected LabelledDoublePanel
		baseRadiusLine, heightLine, aLine, aPrimeLine,
		// bLine,	// no longer required
		bPrimeLine, interfaceTransmissionCoefficientLine, frameRadiusLine;
	protected JButton convertButton;
	protected JCheckBox showFramesCheckBox, showPlaceholderSurfacesCheckBox;	//, omitInnermostSurfacesCheckBox;
	protected SurfacePropertyPanel frameSurfacePropertyPanel;
	
	
	//
	// constructors
	//
	
	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditableLensTOTetrahedron(SceneObject parent, Studio studio)
	{
		this(
				"Lens TO tetrahedron",
				new Vector3D(0, -1, 10),	// base centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.0*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
				2.0,	// height
				0.5,	// a
				0.75,	// a'
				// 0.2,	// b -- no longer required
				0.42855,	// b'
				0.96,	// interface transmission coefficient
				false,	// show frames
				0.02,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				false,	// show placeholder surfaces
				parent,
				studio
			);
	}

	/**
	 * Standard constructor
	 * @param description
	 * @param baseCentre
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 * @param baseRadius
	 * @param height
	 * @param a
	 * @param aPrime
	 * ( b no longer required)
	 * @param bPrime
	 * @param gCLAsTransmissionCoefficient
	 * @param showFrames
	 * @param frameRadius
	 * @param frameSurfaceProperty
	 * @param showPlaceholderSurfaces
	 * @param parent
	 * @param studio
	 */
	public EditableLensTOTetrahedron(
			String description,
			Vector3D baseCentre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double baseRadius,
			double height,
			double a,
			double aPrime,
			// double b,	// no longer required
			double bPrime,
			double gCLAsTransmissionCoefficient,
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
		setBaseCentre(baseCentre);
		setDirections(
				frontDirection,
				rightDirection,
				topDirection
			);
		setBaseRadius(baseRadius);
		setHeight(height);
		setA(a);
		setAPrime(aPrime);
		// setB(b);	// no longer required
		setBPrime(bPrime);
		setInterfaceTransmissionCoefficient(gCLAsTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);
		setShowPlaceholderSurfaces(showPlaceholderSurfaces);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableLensTOTetrahedron(EditableLensTOTetrahedron original)
	{
		this(
			original.getDescription(),
			original.getBaseCentre().clone(),
			original.getFrontDirection().clone(),
			original.getRightDirection().clone(),
			original.getTopDirection().clone(),
			original.getBaseRadius(),
			original.getHeight(),
			original.getA(),
			original.getAPrime(),
			// original.getB(),
			original.getBPrime(),
			original.getInterfaceTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),
			original.isShowPlaceholderSurfaces(),
			original.getParent(),
			original.getStudio()
		);
	}

	public EditableLensTOTetrahedron clone()
	{
		return new EditableLensTOTetrahedron(this);
	}

	
	//
	// setters and getters
	//
	
	public Vector3D getBaseCentre() {
		return baseCentre;
	}

	public void setBaseCentre(Vector3D baseCentre) {
		this.baseCentre = baseCentre;
	}

	public double getBaseRadius() {
		return baseRadius;
	}

	public void setBaseRadius(double baseRadius) {
		this.baseRadius = baseRadius;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public NamedVector3D getFrontDirection() {
		return new NamedVector3D("front", frontDirection);
	}
	
	public NamedVector3D getBackDirection() {
		return new NamedVector3D("back", frontDirection.getReverse());
	}

	public NamedVector3D getRightDirection() {
		return new NamedVector3D("right", rightDirection);
	}
	
	public NamedVector3D getLeftDirection() {
		return new NamedVector3D("left", rightDirection.getReverse());
	}

	public NamedVector3D getTopDirection() {
		return new NamedVector3D("top", topDirection);
	}
	
	public NamedVector3D getBottomDirection() {
		return new NamedVector3D("bottom", topDirection.getReverse());
	}

	/**
	 * First set this.topDirection to the normalised topDirection;
	 * then set this.frontDirection to the normalised part of frontDirection that is perpendicular to topDirection;
	 * then set this.rightDirection to the normalised part of rightDirection that is perpendicular to this.topDirection and this.frontDirection.
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 */
	public void setDirections(
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection
		)
	{
		this.topDirection = topDirection.getNormalised();
		this.frontDirection = frontDirection.getPartPerpendicularTo(topDirection).getNormalised();
		this.rightDirection = rightDirection.getPartParallelTo(
				Vector3D.crossProduct(this.topDirection, this.frontDirection)
			).getNormalised();
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getAPrime() {
		return aPrime;
	}

	public void setAPrime(double aPrime) {
		this.aPrime = aPrime;
	}
	
//	public double getB() {
//		return b;
//	}
//
//	public void setB(double b) {
//		this.b = b;
//	}

	public double getBPrime() {
		return bPrime;
	}

	public void setBPrime(double bPrime) {
		this.bPrime = bPrime;
	}

	public double getInterfaceTransmissionCoefficient() {
		return interfaceTransmissionCoefficient;
	}

	public void setInterfaceTransmissionCoefficient(double interfaceTransmissionCoefficient) {
		this.interfaceTransmissionCoefficient = interfaceTransmissionCoefficient;
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
		
	public EditableSceneObjectCollection getWindows() {
		return windows;
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
	protected void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// calculate the windows and the frames
		calculateVertices();
		addWindows();
		addFrames();
	}
	
	
	
	// the vertex positions
	protected Vector3D topVertex;
	protected Vector3D baseBackVertex;
	protected Vector3D baseLeftVertex;
	protected Vector3D baseRightVertex;
	/**
	 * position of upper inner vertex, in physical space
	 */
	protected Vector3D upperInnerVertexP;
	/**
	 * position of upper inner vertex, in EM space
	 */
	protected Vector3D upperInnerVertexE;
	/**
	 * position of lower inner vertex, in physical space
	 */
	protected Vector3D lowerInnerVertexP;
	/**
	 * position of lower inner vertex, in EM space
	 */
	protected Vector3D lowerInnerVertexE;
	
	
	protected void calculateVertices()
	{
		// the top vertex is a distance height above the baseCentre
		topVertex = Vector3D.sum(baseCentre, topDirection.getProductWith(height));
		
		// the back base vertex is a distance baseRadius behind the baseCentre
		baseBackVertex = Vector3D.sum(baseCentre, frontDirection.getProductWith(-baseRadius));

		double
			angle = MyMath.deg2rad(120),	// 120 degrees, in radians
			cos = Math.cos(angle),	// cos(120 degrees)
			sin = Math.sin(angle);	// sin(120 degrees)
		
		// the base left vertex is an angle 120 degrees from the base back vertex
		baseLeftVertex = Vector3D.sum(
				baseCentre,
				rightDirection.getProductWith(-sin*baseRadius),
				frontDirection.getProductWith(-cos*baseRadius)
			);

		// the base right vertex is an angle -120 degrees from the base back vertex
		baseRightVertex = Vector3D.sum(
				baseCentre,
				rightDirection.getProductWith(sin*baseRadius),
				frontDirection.getProductWith(-cos*baseRadius)
			);
		
		// the inner vertices
		
		boolean bsDescribeLowerVertex = (aPrime > bPrime);
		
		// check which vertex is the upper vertex
		if(bsDescribeLowerVertex)
		{
			// the as describe the upper vertex
			// upper inner vertex is a distance upperHE above the base centre in EM space
			upperInnerVertexE = Vector3D.sum(baseCentre, topDirection.getProductWith(a*height));
			// upper inner vertex is a distance upperHP above the base centre in physical space
			upperInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(aPrime*height));
			// lower inner vertex is a distance lowerHP above the base centre in physical space
			lowerInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(bPrime*height));
			
			// work out the EM-space position of the lower inner vertex by imaging its physical-space condition through
			// the front outer lens and the front upper inner pyramid lens
			
			// first calculate the parameters of these two lenses
			GlensHologram
				frontOuterLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
				frontUpperInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

			// calculate the parameters of the front outer lens
			addLens(
					"front outer lens",	// description
					frontOuterLensHologram,
					topVertex,	// vertex 1
					baseRightVertex,	// vertex 2
					baseLeftVertex,	// vertex 3
					topVertex,	// nodalPoint
					topDirection,	// neg2pos; here, positive is upwards
					upperInnerVertexP,	// qNeg
					upperInnerVertexE,	// qPos
					null	// just calculate the lens parameters -- don't add it to any collection
				);		

			// reserve space for an intermediate position used to calculate imaging properties of the inner lenses
			Vector3D intermediatePosition;
			
			// the front outer lens images the lower inner vertex from EM space into an intermediate position
			intermediatePosition = frontOuterLensHologram.getFiniteImagePosition(lowerInnerVertexE, ImagingDirection.POS2NEG);
			// the front upper-inner-pyramid lens images this intermediate position to the physical-space position of the lower inner vertex
			addLens(
					"front upper inner pyramid lens",	// description
					frontUpperInnerPyramidLensHologram,
					upperInnerVertexP,	// vertex 1
					baseLeftVertex,	// vertex 2
					baseRightVertex,	// vertex 3
					upperInnerVertexP,	// nodal point
					topDirection,	// neg2pos; here, positive is upwards
					lowerInnerVertexP,	// qNeg
					intermediatePosition,	// qPos
					null	// just calculate the lens parameters -- don't add it to any collection
				);

			// now calculate where these two lenses image the lower inner vertex physical-space position
			
			// the front upper inner pyramid lens images the inner vertex physical-space position to an intermediate position
			intermediatePosition = frontUpperInnerPyramidLensHologram.getFiniteImagePosition(objectPosition, direction)
		}
		else
		{
			// the bs describe the upper vertex
			// lower inner vertex is a distance lowerHE above the base centre in EM space
			lowerInnerVertexE = Vector3D.sum(baseCentre, topDirection.getProductWith(a*height));
			// lower inner vertex is a distance lowerHP above the base centre in physical space
			lowerInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(aPrime*height));
			// upper inner vertex is a distance upperHP above the base centre in physical space
			upperInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(bPrime*height));
		}
	}
	
	
	//
	// the windows
	//

	// collections for the lenses
	protected EditableSceneObjectCollection outerLenses, innerLenses;

	private void addWindows()
	{
		// create a collection for the windows
		windows = new EditableSceneObjectCollection("Lenses", false, this, getStudio());
		addSceneObject(windows);

		// create a collection for the outer lenses
		outerLenses = new EditableSceneObjectCollection("Outer lenses", false, windows, getStudio());
		windows.addSceneObject(outerLenses);
		
		// add the outer lenses
		GlensHologram
			frontOuterLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			rightOuterLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			leftOuterLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			baseOuterLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

		// the upper three outer lenses image the upper inner vertex from physical to electromagnetic space
		// the front outer lens
		addLens(
				"front outer lens",	// description
				frontOuterLensHologram,
				topVertex,	// vertex 1
				baseRightVertex,	// vertex 2
				baseLeftVertex,	// vertex 3
				topVertex,	// nodalPoint
				topDirection,	// neg2pos; here, positive is upwards
				upperInnerVertexP,	// qNeg
				upperInnerVertexE,	// qPos
				outerLenses
			);		
		// the left outer lens
		addLens(
				"left outer lens",	// description
				leftOuterLensHologram,
				topVertex,	// vertex 1
				baseLeftVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				topVertex,	// nodalPoint
				topDirection,	// neg2pos; here, positive is upwards
				upperInnerVertexP,	// qNeg
				upperInnerVertexE,	// qPos
				outerLenses
			);		
		// the right outer lens
		addLens(
				"right outer lens",	// description
				rightOuterLensHologram,
				topVertex,	// vertex 1
				baseRightVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				topVertex,	// nodalPoint
				topDirection,	// neg2pos; here, positive is upwards
				upperInnerVertexP,	// qNeg
				upperInnerVertexE,	// qPos
				outerLenses
			);		

		// the base outer lens images the lower inner vertex from physical to electromagnetic space
		addLens(
				"base outer lens",	// description
				baseOuterLensHologram,
				baseLeftVertex,	// vertex 1
				baseRightVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				baseCentre,	// nodalPoint
				topDirection.getReverse(),	// neg2pos; here, positive is downwards
				lowerInnerVertexP,	// qNeg
				lowerInnerVertexE,	// qPos
				outerLenses	// collection
			);
		
		// reserve space for an intermediate position used to calculate imaging properties of the inner lenses
		Vector3D intermediatePosition;
		
		// create a collection for the outer lenses
		innerLenses = new EditableSceneObjectCollection("Inner lenses", false, windows, getStudio());
		windows.addSceneObject(innerLenses);
		
		// next, the lenses of the upper inner pyramid
		GlensHologram
			frontUpperInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			rightUpperInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			leftUpperInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

		// the front outer lens images the lower inner vertex from EM space into an intermediate position
		intermediatePosition = frontOuterLensHologram.getFiniteImagePosition(lowerInnerVertexE, ImagingDirection.POS2NEG);
		// the front upper-inner-pyramid lens images this intermediate position to the physical-space position of the lower inner vertex
		addLens(
				"front upper inner pyramid lens",	// description
				frontUpperInnerPyramidLensHologram,
				upperInnerVertexP,	// vertex 1
				baseLeftVertex,	// vertex 2
				baseRightVertex,	// vertex 3
				upperInnerVertexP,	// nodal point
				topDirection,	// neg2pos; here, positive is upwards
				lowerInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);
		
		// the left outer lens images the lower inner vertex from EM space into an intermediate position
		intermediatePosition = leftOuterLensHologram.getFiniteImagePosition(lowerInnerVertexE, ImagingDirection.POS2NEG);
		// the left upper-inner-pyramid lens images this intermediate position to the physical-space position of the lower inner vertex
		addLens(
				"left upper inner pyramid lens",	// description
				leftUpperInnerPyramidLensHologram,
				upperInnerVertexP,	// vertex 1
				baseLeftVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				upperInnerVertexP,	// nodal point
				topDirection,	// neg2pos; here, positive is upwards
				lowerInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the right outer lens images the lower inner vertex from EM space into an intermediate position
		intermediatePosition = rightOuterLensHologram.getFiniteImagePosition(lowerInnerVertexE, ImagingDirection.POS2NEG);
		// the right upper-inner-pyramid lens images this intermediate position to the physical-space position of the lower inner vertex
		addLens(
				"right upper inner pyramid lens",	// description
				rightUpperInnerPyramidLensHologram,
				upperInnerVertexP,	// vertex 1
				baseRightVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				upperInnerVertexP,	// nodal point
				topDirection,	// neg2pos; here, positive is upwards
				lowerInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);


		// next, the lenses of the lower inner pyramid
		GlensHologram
			frontLowerInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			rightLowerInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			leftLowerInnerPyramidLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

		// the base lens images the upper inner vertex from EM space into an intermediate position
		intermediatePosition = baseOuterLensHologram.getFiniteImagePosition(upperInnerVertexE, ImagingDirection.POS2NEG);
		
		// the front lower-inner-pyramid lens images this intermediate position to the physical-space position of the upper inner vertex
		addLens(
				"front lower inner pyramid lens",	// description
				frontLowerInnerPyramidLensHologram,
				lowerInnerVertexP,	// vertex 1
				baseLeftVertex,	// vertex 2
				baseRightVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				topDirection.getReverse(),	// neg2pos; here, positive is downwards
				upperInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);
		
		// the left lower-inner-pyramid lens images the same intermediate position to the physical-space position of the upper inner vertex
		addLens(
				"left lower inner pyramid lens",	// description
				leftLowerInnerPyramidLensHologram,
				lowerInnerVertexP,	// vertex 1
				baseLeftVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				topDirection.getReverse(),	// neg2pos; here, positive is downwards
				upperInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the right lower-inner-pyramid lens images the same intermediate position to the physical-space position of the upper inner vertex
		addLens(
				"right lower inner pyramid lens",	// description
				rightLowerInnerPyramidLensHologram,
				lowerInnerVertexP,	// vertex 1
				baseRightVertex,	// vertex 2
				baseBackVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				topDirection.getReverse(),	// neg2pos; here, positive is downwards
				upperInnerVertexP,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);
		
		// next, the upper vertical lenses
		GlensHologram
			backUpperVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			leftUpperVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			rightUpperVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

		// the right outer lens images the left base vertex position into an intermediate position
		intermediatePosition = rightOuterLensHologram.getFiniteImagePosition(baseLeftVertex, ImagingDirection.POS2NEG);
		// the back upper vertical lens images this intermediate position back to the left base vertex position
		addLens(
				"back upper vertical lens",	// description
				backUpperVerticalLensHologram,
				topVertex,	// vertex 1
				upperInnerVertexP,	// vertex 2
				baseBackVertex,	// vertex 3
				topVertex,	// nodal point
				rightDirection,	// neg2pos; here, positive is to the front
				baseLeftVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the front outer lens images the back base vertex position into an intermediate position
		intermediatePosition = frontOuterLensHologram.getFiniteImagePosition(baseBackVertex, ImagingDirection.POS2NEG);
		// the left upper vertical lens images this intermediate position back to the back base vertex position
		addLens(
				"left upper vertical lens",	// description
				leftUpperVerticalLensHologram,
				topVertex,	// vertex 1
				upperInnerVertexP,	// vertex 2
				baseLeftVertex,	// vertex 3
				topVertex,	// nodal point
				frontDirection,	// neg2pos; here, positive is to the front
				baseBackVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the right upper vertical lens images the same intermediate position back to the back base vertex position
		addLens(
				"right upper vertical lens",	// description
				rightUpperVerticalLensHologram,
				topVertex,	// vertex 1
				upperInnerVertexP,	// vertex 2
				baseRightVertex,	// vertex 3
				topVertex,	// nodal point
				frontDirection,	// neg2pos; here, positive is to the front
				baseBackVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// next, the lower vertical lenses
		GlensHologram
			backLowerVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			leftLowerVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false),
			rightLowerVerticalLensHologram = new GlensHologram(interfaceTransmissionCoefficient, false);

		// the right outer lens images the left base vertex position into an intermediate position
		intermediatePosition = rightOuterLensHologram.getFiniteImagePosition(baseLeftVertex, ImagingDirection.POS2NEG);
		// the right upper-inner-pyramid lens images this intermediate position into a second intermediate position
		intermediatePosition = rightUpperInnerPyramidLensHologram.getFiniteImagePosition(intermediatePosition, ImagingDirection.POS2NEG);
		// the back lower vertical lens images this intermediate position back to the left base vertex position
		addLens(
				"back lower vertical lens",	// description
				backLowerVerticalLensHologram,
				upperInnerVertexP,	// vertex 1
				lowerInnerVertexP,	// vertex 2
				baseBackVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				rightDirection,	// neg2pos; here, positive is to the front
				baseLeftVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the front outer lens images the back base vertex position into an intermediate position
		intermediatePosition = frontOuterLensHologram.getFiniteImagePosition(baseBackVertex, ImagingDirection.POS2NEG);
		// the front upper-inner-pyramid lens images this intermediate position into a second intermediate position
		intermediatePosition = frontUpperInnerPyramidLensHologram.getFiniteImagePosition(intermediatePosition, ImagingDirection.POS2NEG);
		// the left lower vertical lens images this intermediate position back to the back base vertex position
		addLens(
				"left lower vertical lens",	// description
				leftLowerVerticalLensHologram,
				upperInnerVertexP,	// vertex 1
				lowerInnerVertexP,	// vertex 2
				baseLeftVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				frontDirection,	// neg2pos; here, positive is to the front
				baseBackVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);

		// the right lower vertical lens images the same intermediate position back to the back base vertex position
		addLens(
				"right lower vertical lens",	// description
				rightLowerVerticalLensHologram,
				upperInnerVertexP,	// vertex 1
				lowerInnerVertexP,	// vertex 2
				baseRightVertex,	// vertex 3
				lowerInnerVertexP,	// nodal point
				frontDirection,	// neg2pos; here, positive is to the front
				baseBackVertex,	// qNeg
				intermediatePosition,	// qPos
				innerLenses	// collection
			);
	}

	protected void addLens(
			String description, 
			GlensHologram lensHologram, 
			Vector3D vertex1, Vector3D vertex2, Vector3D vertex3,
			Vector3D nodalPoint,
			Vector3D neg2pos,
			Vector3D qNeg, Vector3D qPos,	// a pair of conjugate positions
			EditableSceneObjectCollection collection
		)
	{
		// the "span" vectors
		Vector3D
			v12 = Vector3D.difference(vertex2, vertex1),	// vector from vertex 1 to vertex 2
			v13 = Vector3D.difference(vertex3, vertex1);	// vector from vertex 1 to vertex 3
		
		// calculate the optical-axis direction
		// start with a vector that is perpendicular to both span vectors...
		Vector3D
			opticalAxisDirection = Vector3D.crossProduct(v12, v13);
		// ...and check whether or not it points in the positive a direction (so that opticalAxisDirection.neg2pos > 0) 
		opticalAxisDirection = opticalAxisDirection.getProductWith(Vector3D.scalarProduct(opticalAxisDirection, neg2pos)).getNormalised();
		
		// first calculate the lensHologram
		try {
			lensHologram.setParametersForInhomogeneousGlensUsingOneConjugatePair(
					nodalPoint,	// pointOnGlens
					opticalAxisDirection,	// opticalAxisDirectionPos
					nodalPoint,	// nodalPoint
					qNeg,	// QNeg
					qPos	// QPos
				);
		} catch (RayTraceException e)
		{
			System.out.println("Element '"+description+"' is not actually a lens. "+e.getMessage());
			// e.printStackTrace();
		}

		if(collection != null)
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
							?(new GlensHologram(interfaceTransmissionCoefficient, false))
							:lensHologram,	// surfaceProperty,
						collection,	// parent,
						getStudio()	// studio
					)
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
		addFrameSphere("top vertex", topVertex, vertices);
		addFrameSphere("base back vertex", baseBackVertex, vertices);
		addFrameSphere("base left vertex", baseLeftVertex, vertices);
		addFrameSphere("base right vertex", baseRightVertex, vertices);
		addFrameSphere("lower inner vertex", lowerInnerVertexP, vertices);
		addFrameSphere("upper inner vertex", upperInnerVertexP, vertices);

		// create a collection for the edges
		edges = new EditableSceneObjectCollection("Edges", false, frames, getStudio());
		frames.addSceneObject(edges);
		
		// add the edges
		
		// the outer edges
		addFrameCylinder("outer back edge", topVertex, baseBackVertex, edges);
		addFrameCylinder("outer left edge", topVertex, baseLeftVertex, edges);
		addFrameCylinder("outer right edge", topVertex, baseRightVertex, edges);
		addFrameCylinder("base front edge", baseLeftVertex, baseRightVertex, edges);
		addFrameCylinder("base right edge", baseRightVertex, baseBackVertex, edges);
		addFrameCylinder("base left edge", baseBackVertex, baseLeftVertex, edges);
		
		// the edges to the upper inner vertex
		addFrameCylinder("top to upper inner vertex edge", topVertex, upperInnerVertexP, edges);
		addFrameCylinder("base back to upper inner vertex edge", baseBackVertex, upperInnerVertexP, edges);
		addFrameCylinder("base left to upper inner vertex edge", baseLeftVertex, upperInnerVertexP, edges);
		addFrameCylinder("base right to upper inner vertex edge", baseRightVertex, upperInnerVertexP, edges);

		// the edges to the lower inner vertex
		addFrameCylinder("upper to lower inner vertex edge", upperInnerVertexP, lowerInnerVertexP, edges);
		addFrameCylinder("base back to lower inner vertex edge", baseBackVertex, lowerInnerVertexP, edges);
		addFrameCylinder("base left to lower inner vertex edge", baseLeftVertex, lowerInnerVertexP, edges);
		addFrameCylinder("base right to lower inner vertex edge", baseRightVertex, lowerInnerVertexP, edges);
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens TO tetrahedron"));
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

		baseCentreLine = new LabelledVector3DPanel("Base centre");
		basicParametersPanel.add(baseCentreLine, "wrap");

		frontDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(frontDirectionLine, "wrap");

		rightDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(rightDirectionLine, "wrap");

		topDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(topDirectionLine, "wrap");

		// basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		baseRadiusLine = new LabelledDoublePanel("Base radius");
		basicParametersPanel.add(baseRadiusLine, "wrap");

		heightLine = new LabelledDoublePanel("Height");
		basicParametersPanel.add(heightLine, "wrap");

		aLine = new LabelledDoublePanel("Height of first inner vertex above base in EM space (as a fraction of height)");
		basicParametersPanel.add(aLine, "wrap");

		aPrimeLine = new LabelledDoublePanel("Height of first inner vertex above base in physical space (as a fraction of height)");
		basicParametersPanel.add(aPrimeLine, "wrap");

//		bLine = new LabelledDoublePanel("Height of second inner vertex above base in EM space (as a fraction of height)");
//		basicParametersPanel.add(bLine, "wrap");

		bPrimeLine = new LabelledDoublePanel("Height of second inner vertex above base in physical space (as a fraction of height)");
		basicParametersPanel.add(bPrimeLine, "wrap");

		interfaceTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each interface");
		basicParametersPanel.add(interfaceTransmissionCoefficientLine, "wrap");

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
		
		frameRadiusLine = new LabelledDoublePanel("frame cylinder radius");
		framePanel.add(frameRadiusLine, "wrap");
		
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
		
		baseCentreLine.setVector3D(getBaseCentre());
		frontDirectionLine.setVector3D(getFrontDirection());
		rightDirectionLine.setVector3D(getRightDirection());
		topDirectionLine.setVector3D(getTopDirection());
		baseRadiusLine.setNumber(getBaseRadius());
		heightLine.setNumber(getHeight());
		aLine.setNumber(getA());
		aPrimeLine.setNumber(getAPrime());
		// bLine.setNumber(getB());
		bPrimeLine.setNumber(getBPrime());
		interfaceTransmissionCoefficientLine.setNumber(getInterfaceTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		showPlaceholderSurfacesCheckBox.setSelected(isShowPlaceholderSurfaces());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableLensTOTetrahedron acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setBaseCentre(baseCentreLine.getVector3D());
		setDirections(
				frontDirectionLine.getVector3D(),
				rightDirectionLine.getVector3D(),
				topDirectionLine.getVector3D()
			);
		setBaseRadius(baseRadiusLine.getNumber());
		setA(aLine.getNumber());
		setAPrime(aPrimeLine.getNumber());
		// setB(bLine.getNumber());
		setBPrime(bPrimeLine.getNumber());
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setShowPlaceholderSurfaces(showPlaceholderSurfacesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());
		// setOmitInnermostSurfaces(omitInnermostSurfacesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-lens-TO-tetrahedron");
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