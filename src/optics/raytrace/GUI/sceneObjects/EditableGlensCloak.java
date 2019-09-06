package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.ImagingDirection;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * No longer used.
 * 
 * The functionality has been migrated to EditableBoxCloak and CubicBoxCloakMaker..
 * 
 * An editable glens cloak, with the windows optionally framed.
 * The parameters a and a' (aPrime) are the corner-to-corner diameters of the central cube in electromagnetic space
 * and in physical space, respectively.
 * 
 * @author Johannes
 */
public class EditableGlensCloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -2475489487170583598L;

	public enum GlensCloakType
	{
		CUBIC("Cubic");
		
		private String description;
		private GlensCloakType(String description)
		{
			this.description = description;
		}
		@Override
		public String toString() {return description;}
	}

	// parameters
	private Vector3D
		centre,
		frontDirection,	// direction from centre to front face; length irrelevant (size of cloak given by sideLength)
		rightDirection,	// direction from centre to right face; length irrelevant (size of cloak given by sideLength)
		topDirection;	// direction from centre to top face; length irrelevant (size of cloak given by sideLength)
	private double
		sideLength,
		a,	// corner-to-corner diameter of the central octahedron in electromagnetic space
		aPrime,	// corner-to-corner diameter of the central octahedron in physical space
		glensTransmissionCoefficient,
		frameRadiusOverSideLength;
	private boolean showFrames;	// , omitInnermostSurfaces;
	private SurfaceProperty frameSurfaceProperty;
	private GlensCloakType cloakType;
	
	// containers for the windows and frames
	EditableSceneObjectCollection windows, frames;
	

	// GUI panels
	private LabelledVector3DPanel centreLine, frontDirectionLine, rightDirectionLine, topDirectionLine;
	private LabelledDoublePanel sideLengthLine, aLine, aPrimeLine, glensTransmissionCoefficientLine, frameRadiusOverSideLengthLine;
	private JButton convertButton;
	private JCheckBox showFramesCheckBox;	//, omitInnermostSurfacesCheckBox;
	private JComboBox<GlensCloakType> cloakTypeComboBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	
	public EditableGlensCloak(SceneObject parent, Studio studio)
	{
		this(
				"Glens cloak",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				2.0,	// side length
				0.5,	// a
				0.75,	// aPrime
				0.96,	// gCLA transmission coefficient
				GlensCloakType.CUBIC,	// cloak type
				true,	// show frames
				0.01,	// frame radius / side length
				SurfaceColour.GREY50_SHINY,	// frame surface property
				parent,
				studio
			);
	}

	public EditableGlensCloak(
			String description,
			Vector3D centre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double sideLength,
			double a,
			double aPrime,
			double glensTransmissionCoefficient,
			GlensCloakType cloakType,
			// boolean omitInnermostSurfaces,
			boolean showFrames,
			double frameRadiusOverSideLength,
			SurfaceProperty frameSurfaceProperty,
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setDirections(
				frontDirection,
				rightDirection,
				topDirection
			);
		setSideLength(sideLength);
		setA(a);
		setAPrime(aPrime);
		setglensTransmissionCoefficient(glensTransmissionCoefficient);
		setCloakType(cloakType);
		// setOmitInnermostSurfaces(omitInnermostSurfaces);
		setShowFrames(showFrames);
		setFrameRadiusOverSideLength(frameRadiusOverSideLength);
		setFrameSurfaceProperty(frameSurfaceProperty);

		populateSceneObjectCollection();
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableGlensCloak(EditableGlensCloak original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getFrontDirection().clone(),
			original.getRightDirection().clone(),
			original.getTopDirection().clone(),
			original.getSideLength(),
			original.getA(),
			original.getAPrime(),
			original.getglensTransmissionCoefficient(),
			original.getCloakType(),
			// original.isOmitInnermostSurfaces(),
			original.isShowFrames(),
			original.getFrameRadiusOverSideLength(),
			original.getFrameSurfaceProperty(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditableGlensCloak clone()
	{
		return new EditableGlensCloak(this);
	}

	
	// setters and getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public Vector3D getFrontDirection() {
		return frontDirection;
	}

	public Vector3D getRightDirection() {
		return rightDirection;
	}

	public Vector3D getTopDirection() {
		return topDirection;
	}

	/**
	 * Set the direction to the front face to centreToCentreOfFrontFace,
	 * then set the direction to the right face to the part of centreToCentreOfRightFace that is perpendicular to the direction to the front face,
	 * then set the direction to the top face to the part of centreToCentreOfTopFace that is perpendicular to both the direction to the front face and to the right face.
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
		this.frontDirection = frontDirection.getNormalised();
		this.rightDirection = rightDirection.getPartPerpendicularTo(frontDirection).getNormalised();
		this.topDirection = topDirection.getPartParallelTo(
				Vector3D.crossProduct(this.frontDirection, this.rightDirection)
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
	
	public double getglensTransmissionCoefficient() {
		return glensTransmissionCoefficient;
	}

	public void setglensTransmissionCoefficient(double glensTransmissionCoefficient) {
		this.glensTransmissionCoefficient = glensTransmissionCoefficient;
	}

	public boolean isShowFrames() {
		return showFrames;
	}

	public void setShowFrames(boolean showFrames) {
		this.showFrames = showFrames;
	}
	
	public double getFrameRadiusOverSideLength()
	{
		return frameRadiusOverSideLength;
	}
	
	public void setFrameRadiusOverSideLength(double frameRadiusOverSideLength)
	{
		this.frameRadiusOverSideLength = frameRadiusOverSideLength;
	}
	
	public SurfaceProperty getFrameSurfaceProperty()
	{
		return frameSurfaceProperty;
	}
	
	public void setFrameSurfaceProperty(SurfaceProperty frameSurfaceProperty)
	{
		this.frameSurfaceProperty = frameSurfaceProperty;
	}
	
	public GlensCloakType getCloakType() {
		return cloakType;
	}

	public void setCloakType(GlensCloakType cloakType) {
		this.cloakType = cloakType;
	}

	
	//
	// the directions and vertices
	//

	// the direction vectors, i.e. the vectors from the centre of the cube to the centre of the different faces
	// protected Vector3D front, back, right, left, top, bottom;
	
	// the vertices of the outer cube
	protected Vector3D trfoVertex, trboVertex, tlfoVertex, tlboVertex, brfoVertex, brboVertex, blfoVertex, blboVertex;
	
	// the vertices of the inner cube, physical space
	protected Vector3D trfiPVertex, trbiPVertex, tlfiPVertex, tlbiPVertex, brfiPVertex, brbiPVertex, blfiPVertex, blbiPVertex;

	// the vertices of the inner cube, EM space
	// protected Vector3D trfiEMVertex, trbiEMVertex, tlfiEMVertex, tlbiEMVertex, brfiEMVertex, brbiEMVertex, blfiEMVertex, blbiEMVertex;
	
	
	
	protected void calculateVertices()
	{
		// calculate the direction vectors with half sidelength length
		Vector3D front, back, right, left, top, bottom;
		front = frontDirection.getWithLength(0.5*sideLength);	// centre to centre of front face
		back = frontDirection.getWithLength(-0.5*sideLength);	// centre to centre of back face
		right = rightDirection.getWithLength(0.5*sideLength);	// centre to centre of right face
		left = rightDirection.getWithLength(-0.5*sideLength);	// centre to centre of left face
		top = topDirection.getWithLength(0.5*sideLength);	// centre to centre of top face
		bottom = topDirection.getWithLength(-0.5*sideLength);	// centre to centre of bottom face		
		
		// calculate the direction vectors with half sidelength length * a (suitable for inner cube in EM space)
//		Vector3D frontA, backA, rightA, leftA, topA, bottomA;
//		frontA = front.getProductWith(a);
//		backA = back.getProductWith(a);
//		rightA = right.getProductWith(a);
//		leftA = left.getProductWith(a);
//		topA = top.getProductWith(a);
//		bottomA = bottom.getProductWith(a);

		// calculate the direction vectors with half sidelength length * aPrime (suitable for inner cube in physical space)
		Vector3D frontAP, backAP, rightAP, leftAP, topAP, bottomAP;
		frontAP = front.getProductWith(aPrime);
		backAP = back.getProductWith(aPrime);
		rightAP = right.getProductWith(aPrime);
		leftAP = left.getProductWith(aPrime);
		topAP = top.getProductWith(aPrime);
		bottomAP = bottom.getProductWith(aPrime);

		// pre-calculate the corners of the outer cube
		trfoVertex = Vector3D.sum(getCentre(), top, right, front); // top right front
		trboVertex = Vector3D.sum(getCentre(), top, right, back);	// top right back
		tlfoVertex = Vector3D.sum(getCentre(), top, left, front); // top left front
		tlboVertex = Vector3D.sum(getCentre(), top, left, back);	// top left back
		brfoVertex = Vector3D.sum(getCentre(), bottom, right, front); // bottom right front
		brboVertex = Vector3D.sum(getCentre(), bottom, right, back);	// bottom right back
		blfoVertex = Vector3D.sum(getCentre(), bottom, left, front); // bottom left front
		blboVertex = Vector3D.sum(getCentre(), bottom, left, back);	// bottom left back

		// pre-calculate the corners of the inner cube in physical space
		trfiPVertex = Vector3D.sum(getCentre(), topAP, rightAP, frontAP); // top right front
		trbiPVertex = Vector3D.sum(getCentre(), topAP, rightAP, backAP);	// top right back
		tlfiPVertex = Vector3D.sum(getCentre(), topAP, leftAP, frontAP); // top left front
		tlbiPVertex = Vector3D.sum(getCentre(), topAP, leftAP, backAP);	// top left back
		brfiPVertex = Vector3D.sum(getCentre(), bottomAP, rightAP, frontAP); // bottom right front
		brbiPVertex = Vector3D.sum(getCentre(), bottomAP, rightAP, backAP);	// bottom right back
		blfiPVertex = Vector3D.sum(getCentre(), bottomAP, leftAP, frontAP); // bottom left front
		blbiPVertex = Vector3D.sum(getCentre(), bottomAP, leftAP, backAP);	// bottom left back

//		// pre-calculate the corners of the inner cube in EM space
//		trfiEMVertex = Vector3D.sum(getCentre(), topA, rightA, frontA); // top right front
//		trbiEMVertex = Vector3D.sum(getCentre(), topA, rightA, backA);	// top right back
//		tlfiEMVertex = Vector3D.sum(getCentre(), topA, leftA, frontA); // top left front
//		tlbiEMVertex = Vector3D.sum(getCentre(), topA, leftA, backA);	// top left back
//		brfiEMVertex = Vector3D.sum(getCentre(), bottomA, rightA, frontA); // bottom right front
//		brbiEMVertex = Vector3D.sum(getCentre(), bottomA, rightA, backA);	// bottom right back
//		blfiEMVertex = Vector3D.sum(getCentre(), bottomA, leftA, frontA); // bottom left front
//		blbiEMVertex = Vector3D.sum(getCentre(), bottomA, leftA, backA);	// bottom left back
	}
	
//	private Vector3D getFiniteImagePosition(Vector3D objectPosition, GlensHologram glens, GlensImagingDirection direction)
//	{
//		Vector3D
//			imagePosition = glens.getImagePosition(objectPosition, direction);
//		
//		// check if any of the coordinates are infinite
//		for(int i=0; imagePosition.isComponentInf() && i<100; i++)
//		{
//			// the image is at infinity; shift the object position by a tiny amount in a random direction
//			imagePosition = glens.getImagePosition(
//					Vector3D.sum(objectPosition, Vector3D.getRandomVector3D(-MyMath.TINY, MyMath.TINY)),
//					direction
//				);
//		}
//
//		return imagePosition;
//	}
	
	private EditableParametrisedConvexPolygon getDiagonalWindow(
			String description,
			Vector3D c1, Vector3D c2, Vector3D c3, Vector3D c4,
			Vector3D outwardsDirection,
			Vector3D QNeg, Vector3D QPos, Vector3D RNeg, Vector3D RPos,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[4];
		vertices[0] = c1;
		vertices[1] = c2;
		vertices[2] = c3;
		vertices[3] = c4;
		
		// (c2-c1) x (c2-c3) should be normal to the polygon plane
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(c2, c1),
				Vector3D.difference(c2, c3)
			);
		
		// not sure how important this is, but make the normal point outwards, i.e.
		// reverse the normal if normal.outwardsDirection < 0
		if(Vector3D.scalarProduct(normal, outwardsDirection) < 0)
		{
			// the normal points inwards; reverse it
			normal = normal.getReverse();
		}
		
		return new EditableParametrisedConvexPolygon(
				description,	// description,
				normal,	// normalToPlane,
				vertices,	// vertices[],
				new GlensSurface(
						c1,	// pointOnGlens,
						normal,	// opticalAxisDirectionPos; "outside" is therefore in positive space
						QNeg, QPos,	// QNeg, QPos,
						RNeg, RPos,	// RNeg, RPos,
						glensTransmissionCoefficient,	// transmissionCoefficient,
						false	// shadowThrowing
					),	// surfaceProperty,
				parent, studio
			);
	}

	private void addCubeFacesForDirection(
			String description,
			Vector3D direction, Vector3D otherDirection1, Vector3D otherDirection2
		)
	{
		// System.out.println("EdibleGlensCloak::addCubeFacesForDirection: direction = "+direction+" ("+description+")");
		
		// Calculate one corner of the outer cube in the direction.
		// For example, is <direction> is in the forward direction, then this is one of the front corners.
		// Also calculate two corners of the inner cube in the direction, in physical (P) and EM (E) space.
		// For example, if <direction> is the forward direction, then these are two of the front corners.
		Vector3D
			direction2Corner1 = Vector3D.sum(direction, otherDirection1, otherDirection2),
			direction2Corner2 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2),
			outsideWindowCorner = Vector3D.sum(getCentre(), direction2Corner1),
			insideWindowCorner1P = Vector3D.sum(getCentre(), direction2Corner1.getProductWith(aPrime)),
			insideWindowCorner1E = Vector3D.sum(getCentre(), direction2Corner1.getProductWith(a)),
			insideWindowCorner2P = Vector3D.sum(getCentre(), direction2Corner2.getProductWith(aPrime)),
			insideWindowCorner2E = Vector3D.sum(getCentre(), direction2Corner2.getProductWith(a));
		
		// outside window
		GlensSurface outsideGlens = new GlensSurface(
				outsideWindowCorner,	// pointOnGlens,
				direction,	// front	// opticalAxisDirectionPos; "outside" is therefore in positive space
				insideWindowCorner1P, insideWindowCorner1E,	// QNeg, QPos,
				insideWindowCorner2P, insideWindowCorner2E,	// RNeg, RPos,
				glensTransmissionCoefficient,	// transmissionCoefficient,
				false	// shadowThrowing
				);
		windows.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						"outside "+description,	// description
						outsideWindowCorner,	// corner, 
						otherDirection1.getProductWith(-2),	// spanVector1,
						otherDirection2.getProductWith(-2),	// spanVector2, 
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						outsideGlens,	// surfaceProperty,
						windows,	// parent,
						getStudio()	// studio
						)
				);

		// Calculate two of the corners of the inner cube in the "other" direction, in physical (P) and EM (E) space.
		// For example, if <direction> is the forward direction, then these are two of the back corners.
		Vector3D
			insideCubeCorner3P = Vector3D.sum(getCentre(), direction2Corner1.getProductWith(-aPrime)),
			insideCubeCorner3E = Vector3D.sum(getCentre(), direction2Corner1.getProductWith(-a)),
			insideCubeCorner4P = Vector3D.sum(getCentre(), direction2Corner2.getProductWith(-aPrime)),
			insideCubeCorner4E = Vector3D.sum(getCentre(), direction2Corner2.getProductWith(-a));

		// inside window
		// Corners 3 and 4 of the inside cube in physical space, when seen through the outside and inside windows,
		// have to appear to be at the corresponding positions in EM space.
		// We do this in reverse: first we image corner 3 in EM space through the outside glens, then we ask the
		// inside glens to image this intermediate image to the position of corner 3 in physical space.
		// (Same with corner 4, of course.)
		Vector3D
			corner3IntermediateImage = outsideGlens.getFiniteImagePosition(insideCubeCorner3E, ImagingDirection.POS2NEG),
			corner4IntermediateImage = outsideGlens.getFiniteImagePosition(insideCubeCorner4E, ImagingDirection.POS2NEG);
		windows.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						"inside "+description,	// description
						insideWindowCorner1P,	// corner, 
						otherDirection1.getProductWith(-2*aPrime),	// spanVector1,
						otherDirection2.getProductWith(-2*aPrime),	// spanVector2,
						0, 1,	// suMin, suMax,
						0, 1,	// svMin, svMax,
						new GlensSurface(
								insideWindowCorner1P,	// pointOnGlens,
								direction,	// opticalAxisDirectionPos; "outside" is therefore in positive space
								insideCubeCorner3P, corner3IntermediateImage,	// QNeg, QPos,
								insideCubeCorner4P, corner4IntermediateImage,	// RNeg, RPos,
								glensTransmissionCoefficient,	// transmissionCoefficient,
								false	// shadowThrowing
								),	// surfaceProperty,
						windows,	// parent,
						getStudio()	// studio
						)
				);
		
		// c1 to c4: corners of the outside cube on "this" side (i.e. the side given by direction)
		// ci1 to ci4: corners of the inside cube on "this" side
		Vector3D
			direction2c1 = Vector3D.sum(direction, otherDirection1, otherDirection2),
			direction2c2 = Vector3D.sum(direction, otherDirection1, otherDirection2.getReverse()),
			direction2c3 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2.getReverse()),
			direction2c4 = Vector3D.sum(direction, otherDirection1.getReverse(), otherDirection2),
			c1 = Vector3D.sum(getCentre(), direction2c1),
			c2 = Vector3D.sum(getCentre(), direction2c2),
			c3 = Vector3D.sum(getCentre(), direction2c3),
			c4 = Vector3D.sum(getCentre(), direction2c4),
			ci1 = Vector3D.sum(getCentre(), direction2c1.getProductWith(aPrime)),
			ci2 = Vector3D.sum(getCentre(), direction2c2.getProductWith(aPrime)),
			ci3 = Vector3D.sum(getCentre(), direction2c3.getProductWith(aPrime)),
			ci4 = Vector3D.sum(getCentre(), direction2c4.getProductWith(aPrime));
			
		// corners on the other side, co1 to co4
		Vector3D
			direction2co1 = Vector3D.sum(direction.getReverse(), otherDirection1, otherDirection2),
			direction2co2 = Vector3D.sum(direction.getReverse(), otherDirection1, otherDirection2.getReverse()),
			direction2co3 = Vector3D.sum(direction.getReverse(), otherDirection1.getReverse(), otherDirection2),
			direction2co4 = Vector3D.sum(direction.getReverse(), otherDirection1.getReverse(), otherDirection2.getReverse()),
			co1 = Vector3D.sum(getCentre(), direction2co1),
			co2 = Vector3D.sum(getCentre(), direction2co2),
			co3 = Vector3D.sum(getCentre(), direction2co3),
			co4 = Vector3D.sum(getCentre(), direction2co4);
				
		// Corners co1 and co2 of the outside cube, when seen through the outside and diagonal windows,
		// have to appear to be at the corresponding positions in EM space, i.e. co1 and co2 again.
		// We do this in reverse: first we image co1 through the outside glens, then we ask the
		// diagonal glens to image this intermediate image to co1 again.
		// (Same with corner co2, of course.)
		Vector3D
			co1IntermediateImage = outsideGlens.getFiniteImagePosition(co1, ImagingDirection.POS2NEG),
			co2IntermediateImage = outsideGlens.getFiniteImagePosition(co2, ImagingDirection.POS2NEG);
		windows.addSceneObject(
				getDiagonalWindow(
						"diagonal 1 "+description,	// description,
						c1, ci1, ci2, c2,	// corners
						direction,	// outwardsDirection,
						co1, co1IntermediateImage,	// QNeg, QPos, 
						co2, co2IntermediateImage,	// RNeg, RPos,
						windows,	// parent,
						getStudio()	// studio
					)
			);
		Vector3D
			co3IntermediateImage = outsideGlens.getFiniteImagePosition(co3, ImagingDirection.POS2NEG),
			co4IntermediateImage = outsideGlens.getFiniteImagePosition(co4, ImagingDirection.POS2NEG);
		windows.addSceneObject(
				getDiagonalWindow(
						"diagonal 2 "+description,	// description,
						c3, ci3, ci4, c4,	// corners
						direction,	// outwardsDirection,
						co3, co3IntermediateImage,	// QNeg, QPos, 
						co4, co4IntermediateImage,	// RNeg, RPos,
						windows,	// parent,
						getStudio()	// studio
					)
			);
			

	}
	
	/**
	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "windows" must exist.
	 */
	private void addWindowsForCubicCloak()
	{
		// calculate the direction vectors with half sidelength length
		Vector3D
			front = frontDirection.getWithLength(0.5*sideLength),	// centre to centre of front face
			back = frontDirection.getWithLength(-0.5*sideLength),	// centre to centre of back face
			right = rightDirection.getWithLength(0.5*sideLength),	// centre to centre of right face
			left = rightDirection.getWithLength(-0.5*sideLength),	// centre to centre of left face
			top = topDirection.getWithLength(0.5*sideLength),	// centre to centre of top face
			bottom = topDirection.getWithLength(-0.5*sideLength);	// centre to centre of bottom face		

		addCubeFacesForDirection(
				"front",	// description
				front,	// direction, 
				right,	// otherDirection1, 
				top	// otherDirection2
			);

		addCubeFacesForDirection(
				"back",	// description
				back,	// direction, 
				left,	// otherDirection1, 
				bottom	// otherDirection2
			);

		addCubeFacesForDirection(
				"top",	// description
				top,	// direction, 
				front,	// otherDirection1, 
				right	// otherDirection2
			);

		addCubeFacesForDirection(
				"bottom",	// description
				bottom,	// direction, 
				back,	// otherDirection1, 
				left	// otherDirection2
			);

		addCubeFacesForDirection(
				"right",	// description
				right,	// direction, 
				top,	// otherDirection1, 
				front	// otherDirection2
			);

		addCubeFacesForDirection(
				"left",	// description
				left,	// direction, 
				bottom,	// otherDirection1, 
				back	// otherDirection2
			);
	}
	
	private void addFrameSphere(String description, Vector3D centrePosition, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(new EditableScaledParametrisedSphere(
				description,
				centrePosition,	// centre
				frameRadiusOverSideLength * sideLength,	// radius
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
				frameRadiusOverSideLength * sideLength,	// radius
				frameSurfaceProperty,
				collection,
				getStudio()
		));
	}
	
	/**
	 * add spheres at the vertices of the outside cube
	 * @param vertices
	 */
	private void addVertices(EditableSceneObjectCollection frames)
	{
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, frames, getStudio());

		// the corners of the outer cube...
		EditableSceneObjectCollection collection = new EditableSceneObjectCollection("outer cube vertices", true, vertices, getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfoVertex, collection);
		addFrameSphere("sphere in top right back cube vertex", trboVertex, collection);
		addFrameSphere("sphere in top left front cube vertex", tlfoVertex, collection);
		addFrameSphere("sphere in top left back cube vertex", tlboVertex, collection);
		addFrameSphere("sphere in bottom right front cube vertex", brfoVertex, collection);
		addFrameSphere("sphere in bottom right back cube vertex", brboVertex, collection);
		addFrameSphere("sphere in bottom left front cube vertex", blfoVertex, collection);
		addFrameSphere("sphere in bottom left back cube vertex", blboVertex, collection);

		vertices.addSceneObject(collection);

		// ... and the corners of the inner cube...
		collection = new EditableSceneObjectCollection("inner cube vertices", true, vertices, getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfiPVertex, collection);
		addFrameSphere("sphere in top right back cube vertex", trbiPVertex, collection);
		addFrameSphere("sphere in top left front cube vertex", tlfiPVertex, collection);
		addFrameSphere("sphere in top left back cube vertex", tlbiPVertex, collection);
		addFrameSphere("sphere in bottom right front cube vertex", brfiPVertex, collection);
		addFrameSphere("sphere in bottom right back cube vertex", brbiPVertex, collection);
		addFrameSphere("sphere in bottom left front cube vertex", blfiPVertex, collection);
		addFrameSphere("sphere in bottom left back cube vertex", blbiPVertex, collection);

		vertices.addSceneObject(collection);
		
		frames.addSceneObject(vertices);
}
	
	private void addEdges(EditableSceneObjectCollection frames)
	{
		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, frames, getStudio());

		// the edges of the outer cube...
		EditableSceneObjectCollection collection = new EditableSceneObjectCollection("outer cube edges", true, edges, getStudio());

		addFrameCylinder("top left cube edge", tlfoVertex, tlboVertex, collection);
		addFrameCylinder("top right cube edge", trfoVertex, trboVertex, collection);
		addFrameCylinder("top front cube edge", tlfoVertex, trfoVertex, collection);
		addFrameCylinder("top back cube edge", tlboVertex, trboVertex, collection);
		addFrameCylinder("bottom left cube edge", blfoVertex, blboVertex, collection);
		addFrameCylinder("bottom right cube edge", brfoVertex, brboVertex, collection);
		addFrameCylinder("bottom front cube edge", blfoVertex, brfoVertex, collection);
		addFrameCylinder("bottom back cube edge", blboVertex, brboVertex, collection);
		addFrameCylinder("front left cube edge", tlfoVertex, blfoVertex, collection);
		addFrameCylinder("front right cube edge", trfoVertex, brfoVertex, collection);
		addFrameCylinder("back left cube edge", tlboVertex, blboVertex, collection);
		addFrameCylinder("back right cube edge", trboVertex, brboVertex, collection);
		
		edges.addSceneObject(collection);

		// ... and the edges of the inner cube...
		collection = new EditableSceneObjectCollection("inner cube edges", true, edges, getStudio());

		addFrameCylinder("top left cube edge", tlfiPVertex, tlbiPVertex, collection);
		addFrameCylinder("top right cube edge", trfiPVertex, trbiPVertex, collection);
		addFrameCylinder("top front cube edge", tlfiPVertex, trfiPVertex, collection);
		addFrameCylinder("top back cube edge", tlbiPVertex, trbiPVertex, collection);
		addFrameCylinder("bottom left cube edge", blfiPVertex, blbiPVertex, collection);
		addFrameCylinder("bottom right cube edge", brfiPVertex, brbiPVertex, collection);
		addFrameCylinder("bottom front cube edge", blfiPVertex, brfiPVertex, collection);
		addFrameCylinder("bottom back cube edge", blbiPVertex, brbiPVertex, collection);
		addFrameCylinder("front left cube edge", tlfiPVertex, blfiPVertex, collection);
		addFrameCylinder("front right cube edge", trfiPVertex, brfiPVertex, collection);
		addFrameCylinder("back left cube edge", tlbiPVertex, blbiPVertex, collection);
		addFrameCylinder("back right cube edge", trbiPVertex, brbiPVertex, collection);
		
		edges.addSceneObject(collection);

		// ... and the diagonal edges
		collection = new EditableSceneObjectCollection("diagonal edges", true, edges, getStudio());

		addFrameCylinder("top left front diagonal edge", tlfoVertex, tlfiPVertex, collection);
		addFrameCylinder("top left back diagonal edge", tlboVertex, tlbiPVertex, collection);
		addFrameCylinder("top right front diagonal edge", trfoVertex, trfiPVertex, collection);
		addFrameCylinder("top right back diagonal edge", trboVertex, trbiPVertex, collection);
		addFrameCylinder("bottom left front diagonal edge", blfoVertex, blfiPVertex, collection);
		addFrameCylinder("bottom left back diagonal edge", blboVertex, blbiPVertex, collection);
		addFrameCylinder("bottom right front diagonal edge", brfoVertex, brfiPVertex, collection);
		addFrameCylinder("bottom right back diagonal edge", brboVertex, brbiPVertex, collection);
		
		edges.addSceneObject(collection);
			
		frames.addSceneObject(edges);

	}
	
	private void addFramesForCubicCloak()
	{
		// spheres at the vertices
	
		// add the vertices of the cube...
		addVertices(frames);

		// cylinders connecting neighbouring vertices

		// add the edges of the cube
		addEdges(frames);
	}

	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the windows...
		windows = new EditableSceneObjectCollection("Windows", true, this, getStudio());
		
		// ... and the frames
		calculateVertices();		
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		switch(cloakType)
		{
		case CUBIC:
			// calculate the windows and the frames
			addWindowsForCubicCloak();
			if(showFrames) addFramesForCubicCloak();
			break;
		}
		
		// add the windows and the frames to this collection
		addSceneObject(windows);
		addSceneObject(frames);
	}

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Cubic glens cloak"));
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

		cloakTypeComboBox = new JComboBox<GlensCloakType>(GlensCloakType.values());
		basicParametersPanel.add(GUIBitsAndBobs.makeRow("Cloak type", cloakTypeComboBox), "wrap");
		
		centreLine = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centreLine, "wrap");

		frontDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(frontDirectionLine, "wrap");

		rightDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(rightDirectionLine, "wrap");

		topDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(topDirectionLine, "wrap");

		basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		sideLengthLine = new LabelledDoublePanel("Side length");
		basicParametersPanel.add(sideLengthLine, "split 3");

		aLine = new LabelledDoublePanel("a");
		basicParametersPanel.add(aLine);

		aPrimeLine = new LabelledDoublePanel("aPrime");
		basicParametersPanel.add(aPrimeLine, "wrap");

		glensTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each surface");
		basicParametersPanel.add(glensTransmissionCoefficientLine);

		tabbedPane.addTab("Basic parameters", basicParametersPanel);

		//
		// the frame panel
		// 
		
		JPanel framePanel = new JPanel();
		framePanel.setLayout(new MigLayout("insets 0"));

		showFramesCheckBox = new JCheckBox("Show frames");
		framePanel.add(showFramesCheckBox, "wrap");
		
		frameRadiusOverSideLengthLine = new LabelledDoublePanel("frame cylinder radius / side length");
		framePanel.add(frameRadiusOverSideLengthLine, "wrap");
		
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
		
		centreLine.setVector3D(getCentre());
		frontDirectionLine.setVector3D(getFrontDirection());
		rightDirectionLine.setVector3D(getRightDirection());
		topDirectionLine.setVector3D(getTopDirection());
		sideLengthLine.setNumber(getSideLength());
		aLine.setNumber(getA());
		aPrimeLine.setNumber(getAPrime());
		glensTransmissionCoefficientLine.setNumber(getglensTransmissionCoefficient());
		cloakTypeComboBox.setSelectedItem(getCloakType());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusOverSideLengthLine.setNumber(getFrameRadiusOverSideLength());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableGlensCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setDirections(
				frontDirectionLine.getVector3D(),
				rightDirectionLine.getVector3D(),
				topDirectionLine.getVector3D()
			);
		setSideLength(sideLengthLine.getNumber());
		setA(aLine.getNumber());
		setAPrime(aPrimeLine.getNumber());
		setglensTransmissionCoefficient(glensTransmissionCoefficientLine.getNumber());
		setCloakType((GlensCloakType)(cloakTypeComboBox.getSelectedItem()));
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadiusOverSideLength(frameRadiusOverSideLengthLine.getNumber());
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
		iPanel.replaceFrontComponent(container, "Edit ex-glens cloak");
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