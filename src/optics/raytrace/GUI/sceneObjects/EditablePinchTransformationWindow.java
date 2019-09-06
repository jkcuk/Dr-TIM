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
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.RayTraceException;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;
import optics.raytrace.imagingElements.InhomogeneousPlanarImagingSurface;
import optics.raytrace.imagingElements.InhomogeneousPlanarImagingSurface.ParameterCalculationMethod;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;

/**
 * Editable window structure that "funnels" light through an opening that is smaller than the window.
 * This is a transformation-optics device in which physical space is "pinched" with respect to electromagnetic space.
 * The optical elements can be either homogeneous GCLAs or lenses.
 * 
 * @author Johannes
 */
public class EditablePinchTransformationWindow extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 1134387537076888816L;

	// parameters
	private Vector3D
		centre,	// centre of the cuboid in EM space
		x, y,	// unit vectors that span the front surface of the window
		z;	// unit vector perpendicular to the window surfaces
	private double
		width,
		height,
		thickness,
		centralStrainFactor,	// vertical strain in the centre
		elementTransmissionCoefficient,
		frameRadius,
		additionalWallWidth,	// in addition to the width, above
		additionalWallHeight,
		additionalWallThickness;
	private EditablePinchTransformationWindowElementType elementType;
	private boolean // calculateGeometricalTransmissionCoefficient	// now part of elementType
		shadowThrowing, showFrames, showWall, showWallWedge;
	private SurfaceProperty frameSurfaceProperty, wallSurfaceProperty;

	// GUI panels
	private LabelledVector3DPanel centreLine, xLine, yLine;
	private LabelledDoublePanel widthLine, heightLine, thicknessLine, centralStrainFactorLine, gCLAsTransmissionCoefficientLine, frameRadiusLine, additionalWallWidthLine, additionalWallHeightLine, additionalWallThicknessLine;
	private JButton convertButton;
	protected JComboBox<EditablePinchTransformationWindowElementType> elementTypeComboBox;
	private JCheckBox // calculateGeometricalTransmissionCoefficientCheckBox, // now part of elementType
		shadowThrowingCheckBox, showFramesCheckBox, showWallCheckBox, showWallWedgeCheckBox;	//, omitInnermostSurfacesCheckBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel, wallSurfacePropertyPanel;
	
	/**
	 * Minimal constructor, assumes default parameters
	 * @param parent
	 * @param studio
	 */
	public EditablePinchTransformationWindow(SceneObject parent, Studio studio)
	{
		this(
				"Pinch-transformation window",
				new Vector3D(0, 0, 5),	// centre
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// up direction
				1.0,	// width
				1.0,	// height
				0.5,	// thickness
				0.5,	// central strain factor
				EditablePinchTransformationWindowElementType.HOMOGENEOUS,	// elementType
				0.96,	// element transmission coefficient
				// true,	// calculateGeometricalTransmissionCoefficient
				true,	// shadow-throwing
				false,	// show frames
				0.01,	// frame radius
				SurfaceColour.WHITE_SHINY,	// frame surface property
				true,	// show wall
				1,	// additional wall width
				1,	// additional wall height
				0.2,	// additional wall thickness
				true,	// show wall wedge
				SurfaceColour.GREY80_MATT,	// wall surface property
				parent,
				studio
			);
	}

	public EditablePinchTransformationWindow(
			String description,
			Vector3D centre,	// window centre
			Vector3D x,	// unit vector "to the right"
			Vector3D y,	// unit vector "upwards"
			double width,
			double height,
			double thickness,
			double centralStrainFactor,
			EditablePinchTransformationWindowElementType elementType,
			double elementTransmissionCoefficient,
			// boolean calculateGeometricalTransmissionCoefficient,
			boolean shadowThrowing,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,
			boolean showWall,
			double additionalWallWidth,
			double additionalWallHeight,
			double additionalWallThickness,
			boolean showWallWedge,
			SurfaceProperty wallSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setXY(x, y);
		setWidth(width);
		setHeight(height);
		setThickness(thickness);
		setCentralStrainFactor(centralStrainFactor);
		setElementType(elementType);
		setElementTransmissionCoefficient(elementTransmissionCoefficient);
		// setCalculateGeometricalTransmissionCoefficient(calculateGeometricalTransmissionCoefficient);
		setShadowThrowing(shadowThrowing);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);
		setShowWall(showWall);
		setAdditionalWallWidth(additionalWallWidth);
		setAdditionalWallHeight(additionalWallHeight);
		setAdditionalWallThickness(additionalWallThickness);
		setShowWallWedge(showWallWedge);
		setWallSurfaceProperty(wallSurfaceProperty);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePinchTransformationWindow(EditablePinchTransformationWindow original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getX().clone(),
			original.getY().clone(),
			original.getWidth(),
			original.getHeight(),
			original.getThickness(),
			original.getCentralStrainFactor(),
			original.getElementType(),
			original.getElementTransmissionCoefficient(),
			// original.isCalculateGeometricalTransmissionCoefficient(),
			original.isShadowThrowing(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),
			original.isShowWall(),
			original.getAdditionalWallWidth(),
			original.getAdditionalWallHeight(),
			original.getAdditionalWallThickness(),
			original.isShowWallWedge(),
			original.getWallSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePinchTransformationWindow clone()
	{
		return new EditablePinchTransformationWindow(this);
	}

	
	//
	// setters and getters
	//
	

	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getX() {
		return x;
	}

	public void setXY(Vector3D x, Vector3D y)
	{
		this.x = x.getNormalised();
		this.y = y.getNormalised();
		z = Vector3D.crossProduct(this.x, this.y).getNormalised();
	}

	public Vector3D getY() {
		return y;
	}

	public Vector3D getZ() {
		return z;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getThickness() {
		return thickness;
	}

	public void setThickness(double thickness) {
		this.thickness = thickness;
	}

	public double getCentralStrainFactor() {
		return centralStrainFactor;
	}

	public void setCentralStrainFactor(double strain) {
		this.centralStrainFactor = strain;
	}

	public boolean isShadowThrowing() {
		return shadowThrowing;
	}

	public void setShadowThrowing(boolean shadowThrowing) {
		this.shadowThrowing = shadowThrowing;
	}

	public double getElementTransmissionCoefficient() {
		return elementTransmissionCoefficient;
	}

	public void setElementTransmissionCoefficient(double gCLAsTransmissionCoefficient) {
		this.elementTransmissionCoefficient = gCLAsTransmissionCoefficient;
	}

	public EditablePinchTransformationWindowElementType getElementType() {
		return elementType;
	}

	public void setElementType(EditablePinchTransformationWindowElementType elementType) {
		this.elementType = elementType;
	}

	public double getFrameRadius() {
		return frameRadius;
	}

	public void setFrameRadius(double frameRadius) {
		this.frameRadius = frameRadius;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}


	public SurfaceProperty getFrameSurfaceProperty() {
		return frameSurfaceProperty;
	}

	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty) {
		this.frameSurfaceProperty = frameSurfaceProperty;
	}


	public double getAdditionalWallWidth() {
		return additionalWallWidth;
	}

	public void setAdditionalWallWidth(double additionalWallWidth) {
		this.additionalWallWidth = additionalWallWidth;
	}

	public double getAdditionalWallHeight() {
		return additionalWallHeight;
	}

	public void setAdditionalWallHeight(double additionalWallHeight) {
		this.additionalWallHeight = additionalWallHeight;
	}

	public double getAdditionalWallThickness() {
		return additionalWallThickness;
	}

	public void setAdditionalWallThickness(double additionalWallThickness) {
		this.additionalWallThickness = additionalWallThickness;
	}

	public boolean isShowWall() {
		return showWall;
	}

	public void setShowWall(boolean showWall) {
		this.showWall = showWall;
	}

	public boolean isShowWallWedge() {
		return showWallWedge;
	}

	public void setShowWallWedge(boolean showWallWedge) {
		this.showWallWedge = showWallWedge;
	}

	public SurfaceProperty getWallSurfaceProperty() {
		return wallSurfaceProperty;
	}

	public void setWallSurfaceProperty(SurfaceProperty wallSurfaceProperty) {
		this.wallSurfaceProperty = wallSurfaceProperty;
	}


	// the various points
	protected Vector3D
		p1,	// front bottom left corner (physical and EM space)
		p2,	// front top left corner
		p3, // front top right corner
		p4,	// front bottom right corner
		p5,	// back bottom left corner
		p6,	// back top left corner
		p7, // back top right corner
		p8,	// back bottom right corner
		p9EM,	// left central vertex (EM space)
		p9,	// left central vertex (physical space)
		p10EM,	// right central vertex (EM space)
		p10;	// right central vertex (physical space)

	// containers for the windows and frames
	EditableSceneObjectCollection windows, frames, wall;

	// Where is this used?
	@SuppressWarnings("unused")
	private void addWindowsExperimental()
	{
		// the top outer window images the centre of the base, point 10,
		// to the bottom corner of the top triangle in the base plane, point 6
		Vector3D
			u = x,
			v = y;
		InhomogeneousPlanarImagingSurface s1 = new InhomogeneousPlanarImagingSurface(
				p1,	// pointOnPlane
				u, v, Vector3D.crossProduct(u, v),	// n
				p10EM, p10,	// object (EM space) and image (phys. space) positions
				ParameterCalculationMethod.SET_F,
				Double.POSITIVE_INFINITY
			);
		// System.out.println("s1 = " + s1);
		
		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Front window",	 // description 
				p1,	// corner
				x.getWithLength(width),	// spanVector1
				y.getWithLength(height),	// spanVector2
				s1.toGeneralisedLens(elementTransmissionCoefficient, shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surface s1 images p6 to an intermediate position, i
		Vector3D i = s1.getImagePosition(p6);
		
		// surface s2 images this intermediate position back to p6
		v = Vector3D.difference(p2, p9).getNormalised();
		InhomogeneousPlanarImagingSurface s2 = new InhomogeneousPlanarImagingSurface(
				p2,	// pointOnPlane
				u, v, Vector3D.crossProduct(u, v),	// n
				i, p6,	// object and image positions
				ParameterCalculationMethod.SET_F,
				Double.POSITIVE_INFINITY
			);
		// System.out.println("s2 = " + s2);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Inner front diagonal window",	 // description 
				p9,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p2, p9),	// spanVector2
				s2.toGeneralisedLens(elementTransmissionCoefficient, shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surfaces s1 and s2 image p5 to an intermediate position, i
		i = s2.getImagePosition(s1.getImagePosition(p5));
		
		// surface s3 images this intermediate position back to p5
		v = Vector3D.difference(p6, p9).getNormalised();
		InhomogeneousPlanarImagingSurface s3 = new InhomogeneousPlanarImagingSurface(
				p6,	// pointOnPlane
				u, v, Vector3D.crossProduct(u, v),	// n
				i, p5,	// object and image positions
				ParameterCalculationMethod.SET_F,
				Double.POSITIVE_INFINITY
			);
		// System.out.println("s3 = " + s3);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Inner back diagonal window",	 // description 
				p9,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p6, p9),	// spanVector2
				s3.toGeneralisedLens(elementTransmissionCoefficient, shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surfaces s1, s2 and s3 image any point, e.g. p1, to an intermediate position, i
		i = s3.getImagePosition(s2.getImagePosition(s1.getImagePosition(p1)));
		
		// surface s3 images this intermediate position back to p5
		u = x.getProductWith(-1);
		v = y.getReverse();
		InhomogeneousPlanarImagingSurface s4 = new InhomogeneousPlanarImagingSurface(
				p8,	// pointOnPlane
				u, v, Vector3D.crossProduct(u, v),	// n
				i, p1,	// object and image positions
				ParameterCalculationMethod.SET_F,
				Double.POSITIVE_INFINITY
			);
		// System.out.println("s3 = " + s3);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Back window",	 // description 
				p8,	// corner
				x.getWithLength(-width),	// spanVector1
				y.getWithLength(height),	// spanVector2
				s4.toGeneralisedLens(elementTransmissionCoefficient, shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));	
	}

	@SuppressWarnings("unused")
	private void addLenses()
	{
		// add the lenses, numbered as they are being encountered when passing through the window front to cack
		GlensSurface
			lensHologram1 = new GlensSurface(elementTransmissionCoefficient, false),
			lensHologram2 = new GlensSurface(elementTransmissionCoefficient, false),
			lensHologram3 = new GlensSurface(elementTransmissionCoefficient, false),
			lensHologram4 = new GlensSurface(elementTransmissionCoefficient, false);

		// the front outer lens images the left inner vertex from physical to electromagnetic space
		addRectangularLens(
				"Front lens",	// description
				lensHologram1,
				p1,	// vertex 1, front bottom left vertex
				p4,	// vertex 2, front bottom right vertex
				p2,	// vertex 3, front top left vertex
				p2,	// nodalPoint
				z.getReverse(),	// neg2pos; here, positive is frontwards
				p9,	// qNeg, left inner vertex in physical space
				p9EM,	// qPos, left inner vertex in EM space
				windows
			);		

		// the front outer lens images the back top left vertex into an intermediate position
		Vector3D intermediatePosition = lensHologram1.getFiniteImagePosition(p6, ImagingDirection.POS2NEG);
		// the front inner lens images this intermediate position back to the back top left vertex position
		addRectangularLens(
				"Front inner lens",	// description
				lensHologram2,
				p2,	// vertex 1, front top left vertex
				p3,	// vertex 2, front top right vertex
				p1,	// vertex 3, front bottom left vertex
				p2,	// nodal point
				z.getReverse(),	// neg2pos; here, positive is frontwards
				p6,	// qNeg, back top left vertex position
				intermediatePosition,	// qPos, intermediate image position
				windows	// collection
			);

		// the back outer lens images the left inner vertex from physical to electromagnetic space
		addRectangularLens(
				"Back lens",	// description
				lensHologram4,
				p5,	// vertex 1, back bottom left vertex
				p8,	// vertex 2, back bottom right vertex
				p6,	// vertex 3, back top left vertex
				p6,	// nodalPoint
				z,	// neg2pos; here, positive is backwards
				p9,	// qNeg, left inner vertex in physical space
				p9EM,	// qPos, left inner vertex in EM space
				windows
			);		

		// the back outer lens images the front top left vertex into an intermediate position
		intermediatePosition = lensHologram4.getFiniteImagePosition(p2, ImagingDirection.POS2NEG);
		// the back inner lens images this intermediate position back to the front top left vertex position
		addRectangularLens(
				"Back inner lens",	// description
				lensHologram3,
				p6,	// vertex 1, back top left vertex
				p7,	// vertex 2, back top right vertex
				p5,	// vertex 3, back bottom left vertex
				p6,	// nodal point
				z,	// neg2pos; here, positive is frontwards
				p2,	// qNeg, back top left vertex position
				intermediatePosition,	// qPos, intermediate image position
				windows	// collection
			);
	}

	protected void addRectangularLens(
			String description, 
			GlensSurface lensHologram, 
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

		collection.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						description, 
						vertex1,	// corner
						v12,	// vertex1ToVertex2 = spanVector1
						v13,	// vertex1ToVertex3 = spanVector2
						lensHologram,	// surfaceProperty
						collection,	// parent
						getStudio()
					)
			);
	}

	private void addHomogeneousGCLAs()
	{
		// the top outer window images the centre of the base, point 10,
		// to the bottom corner of the top triangle in the base plane, point 6
		Vector3D
			u = x,
			v = y;
		HomogeneousPlanarImagingSurface s1 = new HomogeneousPlanarImagingSurface(
				p1,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v,
				p10EM, p10	// object (EM space) and image (phys. space) positions
			);
		// System.out.println("s1 = " + s1);
		
		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Front window",	 // description 
				p1,	// corner
				x.getWithLength(width),	// spanVector1
				y.getWithLength(height),	// spanVector2
				s1.toGCLAs(elementTransmissionCoefficient,
						// elementType == EditablePinchTransformationWindowElementType.HOMOGENEOUS,	// calculateGeometricalTransmissionCoefficient
						GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
						shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surface s1 images p6 to an intermediate position, i
		Vector3D i = s1.getImagePosition(p6);
		
		// surface s2 images this intermediate position back to p6
		v = Vector3D.difference(p2, p9).getNormalised();
		HomogeneousPlanarImagingSurface s2 = new HomogeneousPlanarImagingSurface(
				p2,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v,
				i, p6	// object and image positions
			);
		// System.out.println("s2 = " + s2);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Inner front diagonal window",	 // description 
				p9,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p2, p9),	// spanVector2
				s2.toGCLAs(elementTransmissionCoefficient,
						// elementType == EditablePinchTransformationWindowElementType.HOMOGENEOUS,	// calculateGeometricalTransmissionCoefficient
						GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
						shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surfaces s1 and s2 image p5 to an intermediate position, i
		i = s2.getImagePosition(s1.getImagePosition(p5));
		
		// surface s3 images this intermediate position back to p5
		v = Vector3D.difference(p6, p9).getNormalised();
		HomogeneousPlanarImagingSurface s3 = new HomogeneousPlanarImagingSurface(
				p6,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v,
				i, p5	// object and image positions
			);
		// System.out.println("s3 = " + s3);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Inner back diagonal window",	 // description 
				p9,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p6, p9),	// spanVector2
				s3.toGCLAs(elementTransmissionCoefficient,
						// elementType == EditablePinchTransformationWindowElementType.HOMOGENEOUS,	// calculateGeometricalTransmissionCoefficient
						GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
						shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));

		// surfaces s1, s2 and s3 image any point, e.g. p1, to an intermediate position, i
		i = s3.getImagePosition(s2.getImagePosition(s1.getImagePosition(p1)));
		
		// surface s3 images this intermediate position back to p5
		u = x.getProductWith(-1);
		v = y.getReverse();
		HomogeneousPlanarImagingSurface s4 = new HomogeneousPlanarImagingSurface(
				p8,	// pointOnPlane
				Vector3D.crossProduct(u, v),	// a
				u, v,
				i, p1	// object and image positions
			);
		// System.out.println("s3 = " + s3);

		// add the corresponding window
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Back window",	 // description 
				p8,	// corner
				x.getWithLength(-width),	// spanVector1
				y.getWithLength(height),	// spanVector2
				s4.toGCLAs(elementTransmissionCoefficient,
						// elementType == EditablePinchTransformationWindowElementType.HOMOGENEOUS,	// calculateGeometricalTransmissionCoefficient
						GCLAsTransmissionCoefficientCalculationMethodType.GEOMETRIC_BEST,
						shadowThrowing),	// surfaceProperty
				windows,	// parent
				getStudio()
			));	
	}
	
	private void addWindows()
	{
		switch(elementType)
		{
//		case LENS:
//			addLenses();
//			break;
		case HOMOGENEOUS:
		case HOMOGENEOUS_NO_GEOMETRIC_LOSS:
		default:
			addHomogeneousGCLAs();
		}
	}

	private void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
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
	
	private void addFrameCylinder(String description, Vector3D startPosition, Vector3D endPosition, EditableSceneObjectCollection collection)
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
	
	private void addFrames()
	{
		// the spheres at the corners
		addFrameSphere("front left bottom corner", p1, frames);
		addFrameSphere("front left top corner", p2, frames);
		addFrameSphere("front right top corner", p3, frames);
		addFrameSphere("front right bottom corner", p4, frames);
		addFrameSphere("back left bottom corner", p5, frames);
		addFrameSphere("back left top corner", p6, frames);
		addFrameSphere("back right top corner", p7, frames);
		addFrameSphere("back right bottom corner", p8, frames);
		addFrameSphere("left inner vertex", p9, frames);
		addFrameSphere("right inner vertex", p10, frames);
		
		// the cylinders between corners
		addFrameCylinder("front left cylinder", p1, p2, frames);
		addFrameCylinder("front top cylinder", p2, p3, frames);
		addFrameCylinder("front right cylinder", p3, p4, frames);
		addFrameCylinder("front bottom cylinder", p4, p1, frames);
		addFrameCylinder("back left cylinder", p5, p6, frames);
		addFrameCylinder("back top cylinder", p6, p7, frames);
		addFrameCylinder("back right cylinder", p7, p8, frames);
		addFrameCylinder("back bottom cylinder", p8, p5, frames);
		addFrameCylinder("left front diagonal cylinder", p2, p9, frames);
		addFrameCylinder("left back diagonal cylinder", p6, p9, frames);
		addFrameCylinder("right front diagonal cylinder", p3, p10, frames);
		addFrameCylinder("right back diagonal cylinder", p7, p10, frames);
		addFrameCylinder("bottom central cylinder", p9, p10, frames);
		// the following are not part of frames around windows
		addFrameCylinder("left top cylinder", p2, p6, frames);
		addFrameCylinder("left bottom front cylinder", p1, p9, frames);
		addFrameCylinder("left bottom back cylinder", p9, p5, frames);
		addFrameCylinder("right top cylinder", p3, p7, frames);
		addFrameCylinder("right bottom front cylinder", p4, p10, frames);
		addFrameCylinder("right bottom back cylinder", p10, p8, frames);
	}
	
	private void addWall()
	{
		// the "rectangular annulus" bit of the wall
		wall.addSceneObject(new EditableRectangularFrame(
				"Wall",	// description
				centre,	// centre
				x,	// unit vector "to the right"
				y,	// unit vector "upwards"
				width,	// openingWidth
				height,	// openingHeight
				additionalWallWidth,	// frameWidth
				additionalWallHeight,	// frameHeight
				thickness+additionalWallThickness,	// frameThickness
				wallSurfaceProperty,	// surface property
				wallSurfaceProperty,	// inside surface property
				wall,	// parent
				getStudio()	// studio
			));

		// the "wedge"
		wall.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Front of wedge",	 // description 
				p1,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p9, p1),	// spanVector2
				wallSurfaceProperty,	// surfaceProperty
				wall,	// parent
				getStudio()
			));
		wall.addSceneObject(new EditableScaledParametrisedParallelogram(
				"Back of wedge",	 // description 
				p5,	// corner
				x.getWithLength(width),	// spanVector1
				Vector3D.difference(p9, p5),	// spanVector2
				wallSurfaceProperty,	// surfaceProperty
				wall,	// parent
				getStudio()
			));
	}

	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
				
		// prepare scene-object collection objects for the windows, ...
		windows = new EditableSceneObjectCollection("Windows", true, this, getStudio());
		
		// ..., the frames, ...
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		// ... and the wall
		wall = new EditableSceneObjectCollection("Wall", true, this, getStudio());
		
		// pre-calculate the vertices
		double
			w2 = 0.5*width,	// half the width
			h2 = 0.5*height,	// half the height
			t2 = 0.5*thickness;	// half the thickness
		Vector3D
			l = x.getWithLength(-w2),	// vector to the left
			r = x.getWithLength(w2),	// vector to the right
			u = y.getWithLength(h2),
			d = y.getWithLength(-h2),
			f = z.getWithLength(-t2),
			b = z.getWithLength(t2);
		p1 = Vector3D.sum(centre, l, d, f);
		p2 = Vector3D.sum(centre, l, u, f);
		p3 = Vector3D.sum(centre, r, u, f);
		p4 = Vector3D.sum(centre, r, d, f);
		p5 = Vector3D.sum(centre, l, d, b);
		p6 = Vector3D.sum(centre, l, u, b);
		p7 = Vector3D.sum(centre, r, u, b);
		p8 = Vector3D.sum(centre, r, d, b);
		p9EM = Vector3D.sum(centre, l, d);
		p9 = Vector3D.sum(centre, l, d, y.getWithLength(height*(1-centralStrainFactor)));
		p10EM = Vector3D.sum(centre, r, d);
		p10 = Vector3D.sum(centre, r, d, y.getWithLength(height*(1-centralStrainFactor)));

		// calculate the windows, ...
		addWindows();
		addSceneObject(windows);

		// ..., the frames, ...
		addFrames();
		addSceneObject(frames, showFrames);
		
		// ... and the wall
		addWall();
		addSceneObject(wall, showWall);
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

		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Funnel window"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		centreLine = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centreLine, "wrap");

		xLine = new LabelledVector3DPanel("Direction to the right");
		basicParametersPanel.add(xLine, "wrap");

		yLine = new LabelledVector3DPanel("Upwards direction");
		basicParametersPanel.add(yLine, "wrap");

		widthLine = new LabelledDoublePanel("Width");
		basicParametersPanel.add(widthLine, "split 3");

		heightLine = new LabelledDoublePanel("Height");
		basicParametersPanel.add(heightLine);

		thicknessLine = new LabelledDoublePanel("Thickness");
		basicParametersPanel.add(thicknessLine, "wrap");

		centralStrainFactorLine = new LabelledDoublePanel("Central strain factor");
		basicParametersPanel.add(centralStrainFactorLine, "wrap");
		
		JPanel windowSurfacesPanel = new JPanel();
		windowSurfacesPanel.setLayout(new MigLayout("insets 0"));
		windowSurfacesPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Window surfaces"));

		elementTypeComboBox = new JComboBox<EditablePinchTransformationWindowElementType>(EditablePinchTransformationWindowElementType.values());
		windowSurfacesPanel.add(GUIBitsAndBobs.makeRow("Cloak type", elementTypeComboBox), "wrap");

		gCLAsTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient");
		windowSurfacesPanel.add(gCLAsTransmissionCoefficientLine, "wrap");
		
		// calculateGeometricalTransmissionCoefficientCheckBox = new JCheckBox("Calculate geometrical transmission coefficient");
		// windowSurfacesPanel.add(calculateGeometricalTransmissionCoefficientCheckBox);

		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
		windowSurfacesPanel.add(shadowThrowingCheckBox);
		
		basicParametersPanel.add(windowSurfacesPanel);
		
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
		
		frameSurfacePropertyPanel = new SurfacePropertyPanel("Frame surface", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		framePanel.add(frameSurfacePropertyPanel);
		frameSurfacePropertyPanel.setIPanel(iPanel);

		// omitInnermostSurfacesCheckBox = new JCheckBox("Simplified cloak (omit innermost interfaces)");
		// editPanel.add(omitInnermostSurfacesCheckBox);
		
		tabbedPane.addTab("Frames", framePanel);

		
		//
		// the wall panel
		// 
		
		JPanel wallPanel = new JPanel();
		wallPanel.setLayout(new MigLayout("insets 0"));

		showWallCheckBox = new JCheckBox("Show wall");
		wallPanel.add(showWallCheckBox, "wrap");
		
		additionalWallWidthLine = new LabelledDoublePanel("additional wall width");
		wallPanel.add(additionalWallWidthLine, "split 3");

		additionalWallHeightLine = new LabelledDoublePanel("height");
		wallPanel.add(additionalWallHeightLine);

		additionalWallThicknessLine = new LabelledDoublePanel("thickness");
		wallPanel.add(additionalWallThicknessLine, "wrap");
		
		showWallWedgeCheckBox = new JCheckBox("Show wedge");
		wallPanel.add(showWallWedgeCheckBox, "wrap");

		wallSurfacePropertyPanel = new SurfacePropertyPanel("Wall surface", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		wallPanel.add(wallSurfacePropertyPanel);
		wallSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Wall", wallPanel);

		
		editPanel.add(tabbedPane, "wrap");

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
		
		centreLine.setVector3D(getCentre());
		xLine.setVector3D(getX());
		yLine.setVector3D(getY());
		widthLine.setNumber(getWidth());
		heightLine.setNumber(getHeight());
		thicknessLine.setNumber(getThickness());
		centralStrainFactorLine.setNumber(getCentralStrainFactor());
		elementTypeComboBox.setSelectedItem(getElementType());
		gCLAsTransmissionCoefficientLine.setNumber(getElementTransmissionCoefficient());
		// calculateGeometricalTransmissionCoefficientCheckBox.setSelected(isCalculateGeometricalTransmissionCoefficient());
		shadowThrowingCheckBox.setSelected(isShadowThrowing());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(getFrameSurfaceProperty());
		showWallCheckBox.setSelected(isShowWall());
		additionalWallWidthLine.setNumber(getAdditionalWallWidth());
		additionalWallHeightLine.setNumber(getAdditionalWallHeight());
		additionalWallThicknessLine.setNumber(getAdditionalWallThickness());
		showWallWedgeCheckBox.setSelected(isShowWallWedge());
		wallSurfacePropertyPanel.setSurfaceProperty(getWallSurfaceProperty());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePinchTransformationWindow acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setXY(xLine.getVector3D(), yLine.getVector3D());
		setWidth(widthLine.getNumber());
		setHeight(heightLine.getNumber());
		setThickness(thicknessLine.getNumber());
		setCentralStrainFactor(centralStrainFactorLine.getNumber());
		setElementType((EditablePinchTransformationWindowElementType)(elementTypeComboBox.getSelectedItem()));
		setElementTransmissionCoefficient(gCLAsTransmissionCoefficientLine.getNumber());
		// setCalculateGeometricalTransmissionCoefficient(calculateGeometricalTransmissionCoefficientCheckBox.isSelected());
		setShadowThrowing(shadowThrowingCheckBox.isSelected());
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());
		setShowWall(showWallCheckBox.isSelected());
		setAdditionalWallWidth(additionalWallWidthLine.getNumber());
		setAdditionalWallHeight(additionalWallHeightLine.getNumber());
		setAdditionalWallThickness(additionalWallThicknessLine.getNumber());
		setShowWallWedge(showWallWedgeCheckBox.isSelected());
		setWallSurfaceProperty(wallSurfacePropertyPanel.getSurfaceProperty());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		acceptValuesInEditPanel();	// accept any changes
		EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
		iPanel.replaceFrontComponent(container, "Edit ex-CLA cloak");
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