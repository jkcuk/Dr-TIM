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
import optics.raytrace.GUI.lowLevel.LabelledComboBoxPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.lowLevel.LensElementTypeComboBox;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * An editable TO device made from lenses, with the lenses optionally framed.
 * This is a generalisation of the tetrahedral ideal-lens cloak.
 * The top of the cloak is a distance h above the centre of the base;
 * the first inner vertex is distances a' h and a h (0<a,a'<1) above the base in EM space and physical space, respectively;
 * the second inner vertex is distances b' h and b h (0<b,b'<1) above the base in EM space and physical space, respectively.
 * 
 * @author Johannes
 */
public class EditablePolygonalIdealLensCloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -8054210763663770314L;

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
	 * outer radius of the polyhedral base, i.e. distance of the base vertices from the base centre
	 */
	protected double baseRadius;
	/**
	 * no of vertices of the base lens
	 */
	protected int noOfBaseVertices;
	/**
	 * height, h, of the cloak
	 */
	protected double height;
	/**
	 * the lower inner vertex is a distance heightLowerInnerVertexP above the base in physical space space
	 */
	protected double heightLowerInnerVertexP;
	/**
	 * the upper inner vertex is a distance heightUpperInnerVertexP above the base in physical space space
	 */
	protected double heightUpperInnerVertexP;
	/**
	 * the lower inner vertex is a distance heightLowerInnerVertexE above the base in EM space space
	 */
	protected double heightLowerInnerVertexE;
	/**
	 * transmission coefficient of each interface
	 */
	protected double interfaceTransmissionCoefficient;	// transmission coefficient of each interface
	
	/**
	 * type of each of the lenses; default: ideal thin lens
	 */
	protected LensElementType lensElementType = LensElementType.IDEAL_THIN_LENS;
	
	/**
	 * radius of the cylinders that form the frame
	 */
	protected double frameRadius;
	protected boolean showFrames;
	// protected boolean showPlaceholderSurfaces;	// , omitInnermostSurfaces;
	protected SurfaceProperty frameSurfaceProperty;
	
	// containers for the windows and frames
	protected EditableSceneObjectCollection lenses, frames;
	
	// GUI panels
	protected LabelledVector3DPanel baseCentreLine, frontDirectionLine, rightDirectionLine, topDirectionLine;
	protected LabelledDoublePanel baseRadiusLine, heightLine, heightLowerInnerVertexPLine, heightUpperInnerVertexPLine, heightLowerInnerVertexELine, interfaceTransmissionCoefficientLine, frameRadiusLine;
	protected LabelledIntPanel noOfBaseVerticesPanel;
	protected JButton convertButton;
	protected JCheckBox showFramesCheckBox, showPlaceholderSurfacesCheckBox;	//, omitInnermostSurfacesCheckBox;
	protected SurfacePropertyPanel frameSurfacePropertyPanel;
	protected LensElementTypeComboBox lensElementTypeComboBox;
	
	//
	// constructors
	//
	
	/**
	 * Default constructor
	 * @param parent
	 * @param studio
	 */
	public EditablePolygonalIdealLensCloak(SceneObject parent, Studio studio)
	{
		this(
				"Ideal-lens cloak",
				new Vector3D(0, -1, 10),	// base centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.0*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
				3,	// no of base vertices
				2.0,	// height
				2.0*1./3.,	// heightLowerInnerVertexP
				2.0*2./3.,	// heightUpperInnerVertexP
				-1,	// heightLowerInnerVertexE
				0.96,	// interface transmission coefficient
				false,	// show frames
				0.02,	// frame radius
				SurfaceColour.RED_SHINY,	// frame surface property
				// false,	// show placeholder surfaces
				LensElementType.IDEAL_THIN_LENS,	// lens-element type
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
	 * @param noOfBaseVertices
	 * @param height
	 * @param heightLowerInnerVertexP
	 * @param heightUpperInnerVertexP
	 * @param heightLowerInnerVertexE
	 * @param gCLAsTransmissionCoefficient
	 * @param showFrames
	 * @param frameRadius
	 * @param frameSurfaceProperty
	 * @param showPlaceholderSurfaces
	 * @param parent
	 * @param studio
	 */
	public EditablePolygonalIdealLensCloak(
			String description,
			Vector3D baseCentre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double baseRadius,
			int noOfBaseVertices,
			double height,
			double heightLowerInnerVertexP,
			double heightUpperInnerVertexP,
			double heightLowerInnerVertexE,
			double gCLAsTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			// boolean showPlaceholderSurfaces,
			LensElementType lensElementType,
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
		setNoOfBaseVertices(noOfBaseVertices);
		setHeight(height);
		setHeightLowerInnerVertexP(heightLowerInnerVertexP);
		setHeightUpperInnerVertexP(heightUpperInnerVertexP);
		setHeightLowerInnerVertexE(heightLowerInnerVertexE);
		setInterfaceTransmissionCoefficient(gCLAsTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);
		// setShowPlaceholderSurfaces(showPlaceholderSurfaces);
		setLensElementType(lensElementType);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePolygonalIdealLensCloak(EditablePolygonalIdealLensCloak original)
	{
		this(
			original.getDescription(),
			original.getBaseCentre().clone(),
			original.getFrontDirection().clone(),
			original.getRightDirection().clone(),
			original.getTopDirection().clone(),
			original.getBaseRadius(),
			original.getNoOfBaseVertices(),
			original.getHeight(),
			original.getHeightLowerInnerVertexP(),
			original.getHeightUpperInnerVertexP(),
			original.getHeightLowerInnerVertexE(),
			original.getInterfaceTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),
			// original.isShowPlaceholderSurfaces(),
			original.getLensElementType(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePolygonalIdealLensCloak clone()
	{
		return new EditablePolygonalIdealLensCloak(this);
	}

	
	/**
	 * This static method returns an EditableIdealLensCloak in which the base lens has a given focal length.
	 * @param description
	 * @param baseCentre
	 * @param frontDirection
	 * @param rightDirection
	 * @param topDirection
	 * @param baseRadius
	 * @param noOfBaseVertices
	 * @param height
	 * @param heightLowerInnerVertexP
	 * @param heightUpperInnerVertexP
	 * @param baseLensFocalLength
	 * @param gCLAsTransmissionCoefficient
	 * @param showFrames
	 * @param frameRadius
	 * @param frameSurfaceProperty
	 * @param showPlaceholderSurfaces
	 * @param parent
	 * @param studio
	 * @return the EditableIdealLensCloak
	 */
	public static EditablePolygonalIdealLensCloak getEditableIdealLensCloakUsingBaseLensFocalLength(
			String description,
			Vector3D baseCentre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double baseRadius,
			int noOfBaseVertices,
			double height,
			double heightLowerInnerVertexP,
			double heightUpperInnerVertexP,
			double baseLensFocalLength,
			double gCLAsTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			// boolean showPlaceholderSurfaces,
			LensElementType lensElementType,
			SceneObject parent, 
			Studio studio
		)
	{
		// the base lens images the lower inner vertex in physical space to its position in EM space;
		// the height of this EM-space position is what is required in the standard constructor, so
		// calculate it here
		// h1P = height of lower inner vertex above base in physical space,
		// h1E = height of lower inner vertex above base in EM space
		// lens equation for base lens (focal length f):
		//   1/h1P + 1/(-h1E) = 1/f
		// solve for h1E:
		//   1/h1E = 1/h1P - 1/f = (f - h1P) / (f h1P)
		// take the inverse:
		//   h1E = f h1P / (f - h1P)
		return new EditablePolygonalIdealLensCloak(
				description,
				baseCentre,
				frontDirection,
				rightDirection,
				topDirection,
				baseRadius,
				noOfBaseVertices,
				height,
				heightLowerInnerVertexP,
				heightUpperInnerVertexP,
				baseLensFocalLength*heightLowerInnerVertexP / (baseLensFocalLength - heightLowerInnerVertexP),	// heightLowerInnerVertexE
				gCLAsTransmissionCoefficient,
				showFrames,
				frameRadius,
				frameSurfaceProperty,
				// showPlaceholderSurfaces,
				lensElementType,
				parent, 
				studio
			);
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

	public int getNoOfBaseVertices() {
		return noOfBaseVertices;
	}

	public void setNoOfBaseVertices(int noOfBaseVertices) {
		this.noOfBaseVertices = noOfBaseVertices;
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

	public double getHeightLowerInnerVertexP() {
		return heightLowerInnerVertexP;
	}

	public void setHeightLowerInnerVertexP(double heightLowerInnerVertexP) {
		this.heightLowerInnerVertexP = heightLowerInnerVertexP;
	}

	public double getHeightUpperInnerVertexP() {
		return heightUpperInnerVertexP;
	}

	public void setHeightUpperInnerVertexP(double heightUpperInnerVertexP) {
		this.heightUpperInnerVertexP = heightUpperInnerVertexP;
	}

	public double getHeightLowerInnerVertexE() {
		return heightLowerInnerVertexE;
	}

	public void setHeightLowerInnerVertexE(double heightLowerInnerVertexE) {
		this.heightLowerInnerVertexE = heightLowerInnerVertexE;
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
	
//	public boolean isShowPlaceholderSurfaces() {
//		return showPlaceholderSurfaces;
//	}
//
//	public void setShowPlaceholderSurfaces(boolean showPlaceholderSurfaces) {
//		this.showPlaceholderSurfaces = showPlaceholderSurfaces;
//	}

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

	public LensElementType getLensElementType() {
		return lensElementType;
	}

	public void setLensElementType(LensElementType lensElementType) {
		this.lensElementType = lensElementType;
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
	
	
	public static void calculateGeometryAndFDFromFAFB(double fA, double fB, double h, double h2, double R)
	{
		// see alternativeCloakSolution.nb
		double h1 = (-(h*h*h2*R) + h*h2*h2*R + 
			     fA*h2*h2*Math.sqrt(4*h*h + R*R) + 
			     fB*h*h*Math.sqrt(4*h2*h2 + R*R))/
			   (-(h*h*R) + h*h2*R + fA*h2*Math.sqrt(4*h*h + R*R) + 
			     fB*h*Math.sqrt(4*h2*h2 + R*R));
		
		double fD = -((h*h2*h2*R + fA*h2*h2*Math.sqrt(4*h*h + R*R) + 
			       h*h*(-(h2*R) + fB*Math.sqrt(4*h2*h2 + R*R)))/((h - h2)*(h - h2)*R));
		
		double fC = -((fA*fB*Math.sqrt(4*h*h + R*R)*Math.sqrt(4*h2*h2 + R*R)*
			       (h*h2*h2*R + fA*h2*h2*Math.sqrt(4*h*h + R*R) + 
			    	         h*h*(-(h2*R) + fB*Math.sqrt(4*h2*h2 + R*R))))/
			    	     (Math.pow(-(h*h*R) + h*h2*R + fA*h2*Math.sqrt(4*h*h + R*R) + 
			    	         fB*h*Math.sqrt(4*h2*h2 + R*R),2)*
			    	       Math.sqrt(R*R + (4*Math.pow(h*h2*h2*R + 
			    	              fA*h2*h2*Math.sqrt(4*h*h + R*R) + 
			    	              h*h*(-(h2*R) + fB*Math.sqrt(4*h2*h2 + R*R)),2))/
			    	          Math.pow(-(h*h*R) + h*h2*R + fA*h2*Math.sqrt(4*h*h + R*R) + 
			    	            fB*h*Math.sqrt(4*h2*h2 + R*R),2))));
		
		double fE = -(fA*fB*Math.sqrt(4*h*h + R*R)*Math.sqrt(4*h2*h2 + R*R))/
				   (2.*Math.sqrt(3)*(-(h*h*R) + h*h2*R + fA*h2*Math.sqrt(4*h*h + R*R) + 
					       fB*h*Math.sqrt(4*h2*h2 + R*R)));
		
		double fF = -(fA*Math.sqrt(4*h*h + R*R))/(2.*Math.sqrt(3)*h);
		
		System.out.println("h1="+h1+", fD="+fD+", fC="+fC+", fE="+fE+", fF="+fF);
	}
	
	// the vertex positions
	protected Vector3D topVertex;
	protected Vector3D baseVertex[];
	/**
	 * position of upper inner vertex, in physical space
	 */
	protected Vector3D upperInnerVertexP;
	/**
	 * position of upper inner vertex, in EM space
	 */
	// protected Vector3D upperInnerVertexE;
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
		
		// create space for the base vertices
		baseVertex = new Vector3D[noOfBaseVertices];
		
		for(int i=0; i<noOfBaseVertices; i++)
		{
			double angle = i*2.*Math.PI/noOfBaseVertices;
			baseVertex[i] = Vector3D.sum(
					baseCentre,
					rightDirection.getProductWith(Math.sin(angle)*baseRadius),
					frontDirection.getProductWith(-Math.cos(angle)*baseRadius)
				);
		}
				
		// the inner vertices
				
		// upper inner vertex is a distance upperHE above the base centre in EM space
		// upperInnerVertexE = Vector3D.sum(baseCentre, topDirection.getProductWith(???));

		// upper inner vertex is a distance upperHP above the base centre in physical space
		upperInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(heightUpperInnerVertexP));

		// lower inner vertex is a distance lowerHE above the base centre in EM space
		lowerInnerVertexE = Vector3D.sum(baseCentre, topDirection.getProductWith(heightLowerInnerVertexE));

		// lower inner vertex is a distance lowerHP above the base centre in physical space
		lowerInnerVertexP = Vector3D.sum(baseCentre, topDirection.getProductWith(heightLowerInnerVertexP));
	}
	
	
	//
	// the lenses
	//

	// collections for the lenses
	protected EditableSceneObjectCollection outerLenses, innerLenses;

	/**
	 * add the base lens;
	 * this happens in a separate method so that the Editable4PiLens, which is a subclass of the EditableIdealLensCloak,
	 * can override it;
	 * returns the focal length of the base lens
	 */
	protected double addBaseLens()
	{
		// add the outer lenses
		GlensSurface
			baseOuterLensHologram = new GlensSurface(interfaceTransmissionCoefficient, false);

		double focalLengthBaseLens = 1;
		// first calculate the focal length of the base outer lens
		try {
			baseOuterLensHologram.setParametersForInhomogeneousGlensUsingOneConjugatePair(
					baseCentre,	// point on glens
					topDirection.getReverse(),	// opticalAxisDirectionPos
					baseCentre,	// nodalPoint
					lowerInnerVertexP,	// QNeg
					lowerInnerVertexE	// QPos
				);
			focalLengthBaseLens = baseOuterLensHologram.getFocalLengthPosG()*baseOuterLensHologram.getG();
			// System.out.println("EditableLensTO::addWindows: focalLengthBaseLens="+focalLengthBaseLens);
		} catch (RayTraceException e)
		{
			System.out.println("Base outer lens not actually a lens. "+e.getMessage());
			// e.printStackTrace();
		}

		// the base outer lens images the lower inner vertex from physical to electromagnetic space
		addLens(
				"Base outer lens",	// description
				baseVertex,	// vertices
				baseCentre,	// nodalPoint
				focalLengthBaseLens,	// focal length
				outerLenses,	// collection
				true
			);
		
		// TODO make polygonal!
		
		return focalLengthBaseLens;
	}
	
	/**
	 * create the EditableSceneObjectCollection lenses, populate it, and add it to this
	 * EditableSceneObjectCollection
	 */
	private void addLenses()
	{
		// create a collection for the windows
		lenses = new EditableSceneObjectCollection("Lenses", false, this, getStudio());
		addSceneObject(lenses);

		// create a collection for the outer lenses
		outerLenses = new EditableSceneObjectCollection("Outer lenses", false, lenses, getStudio());
		lenses.addSceneObject(outerLenses);
		
		// create a collection for the inner lenses
		innerLenses = new EditableSceneObjectCollection("Inner lenses", false, lenses, getStudio());
		lenses.addSceneObject(innerLenses);
		
		
		double focalLengthBaseLens = addBaseLens();
		
		// calculate the other focal lengths
		// first introduce the snappier variable names used in the Mathematica file
		double
			h3 = height,
			h2 = heightUpperInnerVertexP,
			h1 = heightLowerInnerVertexP,
			R = baseRadius,
			f00 = focalLengthBaseLens;
		
		double x0 = R*Math.cos(Math.PI/noOfBaseVertices);
		
//		System.out.println("EditableLensTO::addWindows: h1="+h1);
//		System.out.println("EditableLensTO::addWindows: h2="+h2);
//		System.out.println("EditableLensTO::addWindows: h3="+h3);
//		System.out.println("EditableLensTO::addWindows: R="+R);
//		System.out.println("EditableLensTO::addWindows: f00="+f00);
		// Lens A
		double focalLengthUpperOuter = 
				f00*x0*(h3 - h2)*(h3 - h1)/(Math.sqrt(x0*x0 + h3*h3)*h2*(- h1)) + 
					x0*(h3 - h2)* h3      /(Math.sqrt(x0*x0 + h3*h3)*h2);
//				(
//					(-h2+h3)*(f00*(h1-h3) + h1*h3)*R) /
//					(h1*h2*Math.sqrt(4*h3*h3 + R*R)
//				);
		// System.out.println("EditableLensTO::addWindows: focalLengthUpperOuter="+focalLengthUpperOuter);
		
		// Lens B
		double focalLengthUpperInner =
				f00*x0*(h3 - h2)*(h2 - h1)/(Math.sqrt(x0*x0 + h2*h2)*h3*h1);

//				(f00*(h1 - h2)*(h2 - h3)*R)/(h1*h3*Math.sqrt(4*h2*h2 + R*R));
		// System.out.println("EditableLensTO::addWindows: focalLengthUpperInner="+focalLengthUpperInner);
		
		// Lens C
		double focalLengthLowerInner =
				f00*x0*(h3 - h1)*(h2 - h1)/((Math.sqrt(x0*x0 + h1*h1))*h3*(0 - h2)) - 
					x0*(h1 - h2)* h1      /((Math.sqrt(x0*x0 + h1*h1))*h2);

//				-(((h1 - h2)*(f00*(h1 - h3) + h1*h3)*R)/(h2*h3*Math.sqrt(4*h1*h1 + R*R)));
		// System.out.println("EditableLensTO::addWindows: focalLengthLowerInner="+focalLengthLowerInner);
		
		// Lens F
		double R1 = h3/Math.cos(Math.PI/noOfBaseVertices);
		double focalLengthUpperInnerVertical = 
				-focalLengthUpperOuter/(2*(Math.tan(Math.PI/noOfBaseVertices)*h3)/Math.sqrt(R*R + R1*R1));
//				((h2 - h3)*(f00*(h1 - h3) + h1*h3)*R)/(2*Math.sqrt(3)*h1*h2*h3);
		// System.out.println("EditableLensTO::addWindows: focalLengthUpperInnerVertical="+focalLengthUpperInnerVertical);
		
		// Lens E
		double focalLengthLowerInnerVertical =
				focalLengthUpperInnerVertical*(h1 - h2)/(h3 - h2);
//				-(((h1 - h2)*(f00*(h1 - h3) + h1*h3)*R)/(2*Math.sqrt(3)*h1*h2*h3));
		// System.out.println("EditableLensTO::addWindows: focalLengthLowerInnerVertical="+focalLengthLowerInnerVertical);
		
		for(int i=0; i<noOfBaseVertices; i++)
		{
			// the upper outer lenses image the upper inner vertex from physical to electromagnetic space
			addLens(
					"Outer lens #"+i,	// description
					topVertex,	// vertex 1
					baseVertex[i],	// vertex 2
					baseVertex[(i+1)%noOfBaseVertices],	// vertex 3
					topVertex,	// nodalPoint
					focalLengthUpperOuter,	// focal length
					outerLenses
					);		

			// upper inner lenses
			addLens(
					"Upper inner pyramid lens #"+i,	// description
					upperInnerVertexP,	// vertex 1
					baseVertex[i],	// vertex 2
					baseVertex[(i+1)%noOfBaseVertices],	// vertex 3
					upperInnerVertexP,	// nodal point
					focalLengthUpperInner,	// focal length
					innerLenses	// collection
					);


			// next, the lenses of the lower inner pyramid

			addLens(
					"Lower inner pyramid lens #"+i,	// description
					lowerInnerVertexP,	// vertex 1
					baseVertex[i],	// vertex 2
					baseVertex[(i+1)%noOfBaseVertices],	// vertex 3
					lowerInnerVertexP,	// nodal point
					focalLengthLowerInner,	// focal length
					innerLenses	// collection
					);

			// next, the upper vertical lenses

			addLens(
					"Upper vertical lens #"+i,	// description
					topVertex,	// vertex 1
					upperInnerVertexP,	// vertex 2
					baseVertex[i],	// vertex 3
					topVertex,	// nodal point
					focalLengthUpperInnerVertical,	// focal length
					innerLenses	// collection
					);


			// next, the lower vertical lenses

			addLens(
					"Lower vertical lens #"+i,	// description
					upperInnerVertexP,	// vertex 1
					lowerInnerVertexP,	// vertex 2
					baseVertex[i],	// vertex 3
					lowerInnerVertexP,	// nodal point
					focalLengthLowerInnerVertical,	// focal length
					innerLenses	// collection
					);
		}
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
			Vector3D nodalPoint,
			double focalLength,	// focal length
			EditableSceneObjectCollection collection,
			boolean isVisible
		)
	{
		Vector3D vertex[] = new Vector3D[3];
		vertex[0] = vertex1;
		vertex[1] = vertex2;
		vertex[2] = vertex3;
		
		addLens(
				description,
				vertex,
				nodalPoint,
				focalLength,
				collection,
				isVisible
			);
	}


	protected void addLens(
			String description, 
			Vector3D vertices[],
			Vector3D nodalPoint,
			double focalLength,	// focal length
			EditableSceneObjectCollection collection,
			boolean isVisible
		)
	{
//		// the "span" vectors
//		Vector3D
//			v12 = Vector3D.difference(vertex2, vertex1),	// vector from vertex 1 to vertex 2
//			v13 = Vector3D.difference(vertex3, vertex1);	// vector from vertex 1 to vertex 3
//		
//		// calculate the optical-axis direction, which is a normalised a vector that is perpendicular to both span vectors
//		Vector3D
//			opticalAxisDirection = Vector3D.crossProduct(v12, v13).getNormalised();

		// calculate the optical-axis direction, which is a normalised a vector that is perpendicular to both span vectors
		Vector3D
			opticalAxisDirection = Vector3D.crossProduct(
					Vector3D.difference(vertices[1], vertices[0]),
					Vector3D.difference(vertices[2], vertices[0])
				).getNormalised();

		SurfaceProperty surface;
		
		switch(lensElementType)
		{
		case GLASS_PANE:
			// a surface that doesn't change light-ray direction
			surface = new GlensSurface(interfaceTransmissionCoefficient, false);
			break;
		case PHASE_HOLOGRAM_OF_LENS:
			// a phase hologram of a lens, realised with a Point2PointImaging surface
			// The Point2PointImaging surface takes as arguments two points, one in inside space, the other in
			// outside space.
			// Calculate the outwards normal, which should be colinear with the optical-axis direction;
			// the lens is the surface of an EditableTriangle SceneObject, and its outwards normal is calculated
			// as follows:
			Vector3D outwardsNormal = opticalAxisDirection;	// TODO right sign?
			// Vector3D.crossProduct(v12, v13).getNormalised();
			surface = new Point2PointImagingPhaseHologram(
					Vector3D.sum(nodalPoint, outwardsNormal.getProductWith(-2*focalLength)),	// inside-space point, which is imaged to the outside-space point
					Vector3D.sum(nodalPoint, outwardsNormal.getProductWith( 2*focalLength)),	// outside-space point
					interfaceTransmissionCoefficient,
					false,	// reflective
					false	// shadowThrowing
				);
			// TODO can this somehow be optimised for specific viewing directions?
			break;
		case IDEAL_THIN_LENS:
		default:
			// basically the surface of an ideal thin lens
			GlensSurface lensHologram = new GlensSurface(interfaceTransmissionCoefficient, false);
			lensHologram.setParametersUsingNodalPoint(opticalAxisDirection, nodalPoint, -focalLength, focalLength);
			surface = lensHologram;
			break;
		}

		collection.addSceneObject(
//				new EditableTriangle(
//						description,
//						vertex1,
//						v12,	// vertex1ToVertex2
//						v13,	// vertex1ToVertex3
//						false,	// semiInfinite
//						v12,	// uUnitVector
//						v13,	// vUnitVector
//						surface,	// surfaceProperty,
//						collection,	// parent,
//						getStudio()	// studio
//					),
				new EditableParametrisedConvexPolygon(
						description,
						opticalAxisDirection,	// normalToPlane
						vertices,
						surface,	// surfaceProperty
						collection,	// parent
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
		addFrameSphere("top vertex", topVertex, vertices);
		for(int i=0; i<noOfBaseVertices; i++)
		{
			addFrameSphere("base vertex #"+i, baseVertex[i], vertices);
		}
		addFrameSphere("lower inner vertex", lowerInnerVertexP, vertices);
		addFrameSphere("upper inner vertex", upperInnerVertexP, vertices);

		// create a collection for the edges
		edges = new EditableSceneObjectCollection("Edges", false, frames, getStudio());
		frames.addSceneObject(edges);
		
		// add the edges
		
		// the outer edges
		for(int i=0; i<noOfBaseVertices; i++)
		{
			addFrameCylinder("outer edge #"+i, topVertex, baseVertex[i], edges);
			addFrameCylinder("base edge #"+i, baseVertex[i], baseVertex[(i+1)%noOfBaseVertices], edges);
		
			addFrameCylinder("base to upper inner vertex edge #"+i, baseVertex[i], upperInnerVertexP, edges);

			addFrameCylinder("base to lower inner vertex edge #"+i, baseVertex[i], lowerInnerVertexP, edges);
		}
		
		addFrameCylinder("top to upper inner vertex edge", topVertex, upperInnerVertexP, edges);
		addFrameCylinder("upper to lower inner vertex edge", upperInnerVertexP, lowerInnerVertexP, edges);
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Ideal-lens cloak"));
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
		
		noOfBaseVerticesPanel = new LabelledIntPanel("No of base vertices");
		basicParametersPanel.add(noOfBaseVerticesPanel, "wrap");

		heightLine = new LabelledDoublePanel("Height");
		basicParametersPanel.add(heightLine, "wrap");

		heightLowerInnerVertexPLine = new LabelledDoublePanel("Height of lower inner vertex above base in physical space");
		basicParametersPanel.add(heightLowerInnerVertexPLine, "wrap");

		heightUpperInnerVertexPLine = new LabelledDoublePanel("Height of upper inner vertex above base in physical space");
		basicParametersPanel.add(heightUpperInnerVertexPLine, "wrap");

		heightLowerInnerVertexELine = new LabelledDoublePanel("Height of lower inner vertex above base in EM space");
		basicParametersPanel.add(heightLowerInnerVertexELine, "wrap");

		interfaceTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each interface");
		basicParametersPanel.add(interfaceTransmissionCoefficientLine, "wrap");
		
		lensElementTypeComboBox = new LensElementTypeComboBox(true);
		basicParametersPanel.add(new LabelledComboBoxPanel("Lens type", lensElementTypeComboBox));

//		showPlaceholderSurfacesCheckBox = new JCheckBox("Show placeholder surfaces");
//		basicParametersPanel.add(showPlaceholderSurfacesCheckBox, "wrap");

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
		noOfBaseVerticesPanel.setNumber(getNoOfBaseVertices());
		heightLine.setNumber(getHeight());
		heightLowerInnerVertexPLine.setNumber(getHeightLowerInnerVertexP());
		heightUpperInnerVertexPLine.setNumber(getHeightUpperInnerVertexP());
		heightLowerInnerVertexELine.setNumber(getHeightLowerInnerVertexE());
		interfaceTransmissionCoefficientLine.setNumber(getInterfaceTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		// showPlaceholderSurfacesCheckBox.setSelected(isShowPlaceholderSurfaces());
		lensElementTypeComboBox.setSelectedItem(lensElementType);
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePolygonalIdealLensCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setBaseCentre(baseCentreLine.getVector3D());
		setDirections(
				frontDirectionLine.getVector3D(),
				rightDirectionLine.getVector3D(),
				topDirectionLine.getVector3D()
			);
		setHeight(heightLine.getNumber());
		setBaseRadius(baseRadiusLine.getNumber());
		setNoOfBaseVertices(noOfBaseVerticesPanel.getNumber());
		setHeightLowerInnerVertexP(heightLowerInnerVertexPLine.getNumber());
		setHeightUpperInnerVertexP(heightUpperInnerVertexPLine.getNumber());
		setHeightLowerInnerVertexE(heightLowerInnerVertexELine.getNumber());
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		// setShowPlaceholderSurfaces(showPlaceholderSurfacesCheckBox.isSelected());
		setLensElementType((LensElementType)(lensElementTypeComboBox.getSelectedItem()));
		setFrameRadius(frameRadiusLine.getNumber());
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