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
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.utility.CoordinateSystems.GlobalOrLocalCoordinateSystemType;

/**
 * An editable gCLA tardis window, with the windows optionally framed.
 * The window can be either 1/6 of a cloak or the minimum subset of windows (namely 4) that allows the same effect, but with a limited FOV.
 * The parameters a and a' (aPrime) are the corner-to-corner diameters of the central octahedron in electromagnetic space
 * and in physical space, respectively.
 * 
 * @author Johannes
 */
public class EditableGCLAsTardisWindow extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 6515335416487231705L;

	public enum GCLATardisWindowType
	{
		CLOAK_SIDE("1/6 of square prismatic cloak"),
		CLOAK_SIDE_MINIMUM("4 windows out of 1/6 of square prismatic cloak (reduced FOV)");
//		PRISMATIC("Square prismatic"),
//		OCTAHEDRAL("Octahedral");
		
		private String description;
		private GCLATardisWindowType(String description)
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
		gCLAsTransmissionCoefficient,
		frameRadiusOverSideLength;
	private boolean showFrames;	// , omitInnermostSurfaces;
	private SurfaceProperty frameSurfaceProperty;
	private GCLATardisWindowType tardisWindowType;

	// GUI panels
	private LabelledVector3DPanel centreLine, frontDirectionLine, rightDirectionLine, topDirectionLine;
	private LabelledDoublePanel sideLengthLine, aLine, aPrimeLine, gCLAsTransmissionCoefficientLine, frameRadiusOverSideLengthLine;
	private JButton convertButton;
	private JCheckBox showFramesCheckBox;	//, omitInnermostSurfacesCheckBox;
	private JComboBox<GCLATardisWindowType> tardisWindowTypeComboBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	
	public EditableGCLAsTardisWindow(SceneObject parent, Studio studio)
	{
		this(
				"Tardis window",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(0, 0, -1),	// front direction
				new Vector3D(1, 0, 0),	// right direction
				new Vector3D(0, 1, 0),	// top direction
				1.0,	// side length
				0.07,	// a
				0.7,	// aPrime
				0.96,	// gCLA transmission coefficient
				GCLATardisWindowType.CLOAK_SIDE,	// window type
				true,	// show frames
				0.01,	// frame radius / side length
				SurfaceColour.GREY50_SHINY,	// frame surface property
				parent,
				studio
			);
	}

	public EditableGCLAsTardisWindow(
			String description,
			Vector3D centre,
			Vector3D frontDirection,
			Vector3D rightDirection,
			Vector3D topDirection,
			double sideLength,
			double a,
			double aPrime,
			double gCLAsTransmissionCoefficient,
			GCLATardisWindowType cloakType,
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
		setgCLAsTransmissionCoefficient(gCLAsTransmissionCoefficient);
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
	public EditableGCLAsTardisWindow(EditableGCLAsTardisWindow original)
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
			original.getgCLAsTransmissionCoefficient(),
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
	public EditableGCLAsTardisWindow clone()
	{
		return new EditableGCLAsTardisWindow(this);
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
		this.rightDirection = rightDirection.getPartPerpendicularTo(this.frontDirection).getNormalised();
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
	
	public double getgCLAsTransmissionCoefficient() {
		return gCLAsTransmissionCoefficient;
	}

	public void setgCLAsTransmissionCoefficient(double gCLAsTransmissionCoefficient) {
		this.gCLAsTransmissionCoefficient = gCLAsTransmissionCoefficient;
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
	
//	/**
//	 * @return	false if the central octahedron is present, i.e. this is the full cloak;
//	 * true if the central octahedron is not present, i.e. the cloak works approximately
//	 */
//	public boolean isOmitInnermostSurfaces()
//	{
//		return omitInnermostSurfaces;
//	}
//	
//	public void setOmitInnermostSurfaces(boolean omitInnermostSurfaces)
//	{
//		this.omitInnermostSurfaces = omitInnermostSurfaces;
//	}

	public GCLATardisWindowType getCloakType() {
		return tardisWindowType;
	}

	public void setCloakType(GCLATardisWindowType cloakType) {
		this.tardisWindowType = cloakType;
	}
	

	// the direction vectors, i.e. the vectors from the centre of the cube to the centre of the different faces
	protected Vector3D front, back, right, left, top, bottom;
	
	// the vertices of the cube
	protected Vector3D trfCubeVertex, trbCubeVertex, tlfCubeVertex, tlbCubeVertex, brfCubeVertex, brbCubeVertex, blfCubeVertex, blbCubeVertex;
	
	// the vertices of the central octahedron (for the cubic cloak)
	protected Vector3D frontOctahedronVertex, backOctahedronVertex, rightOctahedronVertex, leftOctahedronVertex, topOctahedronVertex, bottomOctahedronVertex;
	
	// the vertices of the central cuboid (for the prismatic cloak)
	protected Vector3D bfCuboidVertex, brCuboidVertex, bbCuboidVertex, blCuboidVertex, tfCuboidVertex, trCuboidVertex, tbCuboidVertex, tlCuboidVertex;
	
	// containers for the windows and frames
	EditableSceneObjectCollection windows, frames;
	
//	private void addPyramidRoof(String description, Vector3D pyramidTip, Vector3D bottomLeftVertex, Vector3D bottomRightVertex, SurfaceProperty surfaceProperty, SceneObject parent)
//	{
//		Vector3D
//			base = Vector3D.difference(bottomLeftVertex, bottomRightVertex).getNormalised(),
//			up = Vector3D.difference(pyramidTip, Vector3D.mean(bottomLeftVertex, bottomRightVertex)).getNormalised();
//			// normal = Vector3D.crossProduct(base, up).getNormalised();
//		
//		addSceneObject(EditableParametrisedTriangle.makeEditableParametrisedTriangleFromVertices(
//			description,
//			pyramidTip,	// corner 1
//			bottomLeftVertex,
//			bottomRightVertex,
//			base,	// unit vector in u direction
//			up,	// unit vector in v direction
//			surfaceProperty,
//			parent,
//			getStudio()
//		));
//	}

//	/**
//	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
//	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "windows" must exist.
//	 */
//	private void addWindowsForCubicCloak()
//	{
//		// the facets of the cube
//		// first calculate the parameters the CLAs
//		double eta1 = (aPrime-1)/(a-1);
//		
//		// then define the surface
//		GeneralisedConfocalLensletArrays window1 = new GeneralisedConfocalLensletArrays(
//				Vector3D.Z,	// aHat
//				Vector3D.X,	//
//				eta1, eta1, 0, 0,
//				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
//				gCLAsTransmissionCoefficient,
//				false	// shadow-throwing
//			);
//		
//		// then add the windows
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, top",	// description, 
//				tlbCubeVertex,	// corner, 
//				front,	// spanVector1,
//				right,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				getParent(), getStudio()
//			));
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, bottom",	// description, 
//				brfCubeVertex,	// corner, 
//				left,	// spanVector1,
//				back,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				windows, getStudio()
//			));
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, front",	// description, 
//				blfCubeVertex,	// corner, 
//				right,	// spanVector1,
//				top,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				windows, getStudio()
//			));
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, right",	// description, 
//				brfCubeVertex,	// corner, 
//				back,	// spanVector1,
//				top,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				windows,	// parent
//				getStudio()
//			));
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, back",	// description, 
//				brbCubeVertex,	// corner, 
//				left,	// spanVector1,
//				top,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				windows, getStudio()
//			));
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"surface 1, left",	// description, 
//				blbCubeVertex,	// corner, 
//				front,	// spanVector1,
//				top,	// spanVector2, 
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax,
//				window1,	// surfaceProperty,
//				windows,	// parent
//				getStudio()
//			));
//		
//		// parameters for the "roof" of the outside pyramids (deltax2 = 0)
//		double eta2 = (aPrime*(a-1)*(aPrime-2))/(a*(aPrime-1)*(a-2)); // (a*(4*aPrime*aPrime-1))/(aPrime*(4*a*a-1));
//		double deltay2 = ((a-aPrime)*(aPrime-a))/(a*(aPrime-1)*(a-2));	 // (2*(a*a-2*a*aPrime+aPrime*aPrime))/(aPrime*(4*a*a-1));
//
//		GeneralisedConfocalLensletArrays window2 = new GeneralisedConfocalLensletArrays(Vector3D.Z, Vector3D.X, eta2, eta2, deltay2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);
//
//		addPyramidRoof(
//				"surface 2, ",	// description,
//				topOctahedronVertex,	// pyramidTip,
//				Vector3D bottomLeftVertex, Vector3D bottomRightVertex, SurfaceProperty surfaceProperty, SceneObject parent)
//	}
	
	
//	/**
//	 * We have divided up all the surfaces that make up the prismatic cloak (apart from the central square prism)
//	 * into those behind the different exterior faces.
//	 * This method adds the surfaces behind one particular exterior face.
//	 * @param faceName
//	 * @param cloakCentre2FaceCentre
//	 * @param right
//	 * @param up
//	 */
//	private void addInterfacesForPrismaticCloakFace(String faceName, Vector3D cloakCentre2FaceCentre, Vector3D right, Vector3D up)
//	{
//		// parameters for outside CLAs
//		double eta1 = (aPrime-1)/(a-1);
//		
//		// parameters for inside gCLAs (deltaY2 = 0)
//		double eta2 = (aPrime*(a-1)*(aPrime-2))/(a*(aPrime-1)*(a-2));
//			// (a*(4*aPrime*aPrime-1))/(aPrime*(4*a*a-1));
//		double deltax2 = (a-aPrime)*(aPrime-a)/(a*(aPrime-1)*(a-2));
//			// (2*(a*a-2*a*aPrime+aPrime*aPrime))/(aPrime*(4*a*a-1));
//		
//		Vector3D
//			faceCentre = Vector3D.sum(getCentre(), cloakCentre2FaceCentre),	// the centre of the exterior face
//			left = right.getReverse(),
//			down = up.getReverse();
//				
//		// the corners
//		Vector3D
//			faceBottomLeftCorner = Vector3D.sum(faceCentre, left, down),
//			faceBottomRightCorner = Vector3D.sum(faceCentre, right, down),
//			faceTopLeftCorner = Vector3D.sum(faceCentre, left, up),
//			// faceTopRightCorner = Vector3D.sum(faceCentre, right, up),	// not actually needed
//			prismTopCorner = Vector3D.sum(centre, cloakCentre2FaceCentre.getProductWith(aPrime), up),
//			prismBottomCorner = Vector3D.sum(centre, cloakCentre2FaceCentre.getProductWith(aPrime), down);
//				
//		// the exterior CLAs
//		GeneralisedConfocalLensletArrays window1 = new GeneralisedConfocalLensletArrays(
//				Vector3D.Z,	// aHat
//				Vector3D.X,	// uHat
//				Vector3D.Y,	// vHat
//				eta1, eta1, 0, 0,
//				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
//				gCLAsTransmissionCoefficient,
//				false
//			);
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				faceName+" exterior face",
//				faceBottomLeftCorner,	// corner
//				right.getProductWith(2),	// width vector
//				up.getProductWith(2),	// height vector
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
//				window1,
//				this,
//				getStudio()
//		));
//
//		// interior gCLAs
//		GeneralisedConfocalLensletArrays window2 = new GeneralisedConfocalLensletArrays(Vector3D.Z, Vector3D.X, Vector3D.Y, eta2, eta2, deltax2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);
//		
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"interior left interface behind "+faceName+" exterior face",
//				prismTopCorner, //corner
//				Vector3D.difference(faceTopLeftCorner, prismTopCorner), // width vector
//				down.getProductWith(2),	// height vector
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
//				window2,
//				this,
//				getStudio()
//		));
//
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"interior right interface behind "+faceName+" exterior face",
//				prismBottomCorner, //corner
//				Vector3D.difference(faceBottomRightCorner, prismBottomCorner), // width vector
//				up.getProductWith(2),	// height vector
//				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
//				window2,
//				this,
//				getStudio()
//		));
//	}
	
	/**
	 * We have divided up all the surfaces that make up the cubic cloak 
	 * (apart from the surfaces forming the "star" into the corners and the central
	 * octahedron) into those behind the different exterior faces.
	 * This method adds the surfaces behind one particular exterior face.
	 * @param faceName
	 * @param cloakCentre2FaceCentre
	 * @param right
	 * @param up
	 */
	private void addInterfacesForCubicCloakFace(String faceName, Vector3D cloakCentre2FaceCentre, Vector3D right, Vector3D up, boolean makeAllWindowsVisible)
	{
		// parameters for outside CLAs
		double eta1 = (aPrime-1)/(a-1);	// aPrime/a;
		
		// parameters for the "roof" of the outside pyramids (deltax2 = 0)
		double eta2 = (aPrime*(a-1)*(aPrime-2))/(a*(aPrime-1)*(a-2)); // (a*(4*aPrime*aPrime-1))/(aPrime*(4*a*a-1));
		double deltay2 = ((a-aPrime)*(aPrime-a))/(a*(aPrime-1)*(a-2));	 // (2*(a*a-2*a*aPrime+aPrime*aPrime))/(aPrime*(4*a*a-1));
		
		// the centre of the exterior face
		Vector3D faceCentre = Vector3D.sum(getCentre(), cloakCentre2FaceCentre);
		
		// the tip of the pyramid
		Vector3D pyramidTip = Vector3D.sum(centre, cloakCentre2FaceCentre.getProductWith(aPrime));
		
		// the corners of the exterior face
		Vector3D
			bottomLeftCorner = Vector3D.sum(faceCentre, right.getProductWith(-1), up.getProductWith(-1)),
			bottomRightCorner = Vector3D.sum(faceCentre, right, up.getProductWith(-1)),
			topLeftCorner = Vector3D.sum(faceCentre, right.getProductWith(-1), up),
			topRightCorner = Vector3D.sum(faceCentre, right, up);
						
		// the exterior CLAs
		GCLAsWithApertures window1 = new GCLAsWithApertures(
				Vector3D.Z,	// aHat
				Vector3D.X,	// uHat
				Vector3D.Y,	// vHat
				eta1, eta1, 0, 0,
				GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS,
				gCLAsTransmissionCoefficient,
				false
			);
		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
				faceName+" exterior face",
				Vector3D.sum(faceCentre, right.getProductWith(-1), up.getProductWith(-1)),	// corner
				right.getProductWith(2),	// width vector
				up.getProductWith(2),	// height vector
				0, 1, 0, 1,	// suMin, suMax, svMin, svMax
				window1,
				this,
				getStudio()
		));

		// interior gCLAs
		GCLAsWithApertures window2 = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, eta2, eta2, deltay2, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);
		
		Vector3D
			tipToBottomLeftCorner = Vector3D.difference(bottomLeftCorner, pyramidTip),
			tipToBottomRightCorner = Vector3D.difference(bottomRightCorner, pyramidTip),
			tipToTopLeftCorner = Vector3D.difference(topLeftCorner, pyramidTip),
			tipToTopRightCorner = Vector3D.difference(topRightCorner, pyramidTip);
		
		Vector3D
			leftTriangleNormal = Vector3D.crossProduct(tipToBottomLeftCorner, tipToTopLeftCorner),
			rightTriangleNormal = Vector3D.crossProduct(tipToTopRightCorner, tipToBottomRightCorner),
			topTriangleNormal = Vector3D.crossProduct(tipToTopLeftCorner, tipToTopRightCorner),
			bottomTriangleNormal = Vector3D.crossProduct(tipToBottomRightCorner, tipToBottomLeftCorner);
		
		windows.addSceneObject(new EditableTriangle(
				"interior left interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToTopLeftCorner,	// corner 1 to corner 3
				tipToBottomLeftCorner,	// corner 1 to corner 2
				false,	// not semi-infinite
				right.getPartPerpendicularTo(leftTriangleNormal).getNormalised().getReverse(),	// unit vector in u direction
				up.getPartPerpendicularTo(leftTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				this,
				getStudio()
				),
				makeAllWindowsVisible
			);

		windows.addSceneObject(new EditableTriangle(
				"interior right interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToBottomRightCorner,	// corner 1 to corner 2
				tipToTopRightCorner,	// corner 1 to corner 3
				false,	// not semi-infinite
				right.getPartPerpendicularTo(rightTriangleNormal).getNormalised(),	// unit vector in u direction
				up.getPartPerpendicularTo(rightTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				this,
				getStudio()
				),
				makeAllWindowsVisible
			);

		windows.addSceneObject(new EditableTriangle(
				"interior top interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToTopRightCorner,	// corner 1 to corner 3
				tipToTopLeftCorner,	// corner 1 to corner 2
				false,	// not semi-infinite
				up.getPartPerpendicularTo(topTriangleNormal).getNormalised(),	// unit vector in u direction
				right.getPartPerpendicularTo(topTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				this,
				getStudio()
		));

		windows.addSceneObject(new EditableTriangle(
				"interior bottom interface behind "+faceName+" exterior face",
				pyramidTip,	// corner 1
				tipToBottomLeftCorner,	// corner 1 to corner 2
				tipToBottomRightCorner,	// corner 1 to corner 3
				false,	// not semi-infinite
				up.getReverse().getPartPerpendicularTo(bottomTriangleNormal).getNormalised(),	// unit vector in u direction
				right.getPartPerpendicularTo(bottomTriangleNormal).getNormalised(),	// unit vector in v direction
				window2,
				this,
				getStudio()
				),
				makeAllWindowsVisible
			);
	}

	private void addCentralOctahedronFace(String name, Vector3D corner1, Vector3D corner2, Vector3D corner3, GCLAsWithApertures surfaceProperty, boolean isVisible)
	{
		windows.addSceneObject(new EditableTriangle(
				name,
				corner1,	// corner 1
				Vector3D.difference(corner2, corner1),	// corner 1 to corner 2
				Vector3D.difference(corner3, corner1),	// corner 1 to corner 3
				false,	// not semi-infinite
				Vector3D.difference(corner3, corner2).getNormalised(),	// unit vector in u direction
				Vector3D.difference(corner1, Vector3D.mean(corner2, corner3)).getNormalised(),	// unit vector in v direction
				surfaceProperty,
				this,
				getStudio()
				),
				isVisible
			);
	}
	
	private void addStarPolygon(String name, Vector3D outerVertex, Vector3D innerVertex1, Vector3D innerVertex2, GCLAsWithApertures surfaceProperty, boolean isVisible)
	{
		windows.addSceneObject(new EditableTriangle(
					name,
					outerVertex,	// corner 1
					Vector3D.difference(innerVertex1, outerVertex),	// corner 1 to corner 3
					Vector3D.difference(innerVertex2, outerVertex),	// corner 1 to corner 2
					false,	// not semi-infinite
					Vector3D.difference(innerVertex1, innerVertex2).getNormalised(),	// unit vector in u direction
					Vector3D.difference(outerVertex, Vector3D.mean(innerVertex1, innerVertex2)).getNormalised(),	// unit vector in v direction
					surfaceProperty,
					this,
					getStudio()
				),
				isVisible
			);
	}
		

	/**
	 * Add all the windows (without frames --- those get added separately) for a cubic cloak.
	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "windows" must exist.
	 */
	private void addWindowsForOctahedralCloak(boolean makeAllWindowsVisible)
	{
		Vector3D
			front = frontDirection.getWithLength(0.5*sideLength),	// centre to centre of front face
//			back = frontDirection.getWithLength(-0.5*sideLength),	// centre to centre of back face
			right = rightDirection.getWithLength(0.5*sideLength),	// centre to centre of right face
			left = rightDirection.getWithLength(-0.5*sideLength),	// centre to centre of left face
			top = topDirection.getWithLength(0.5*sideLength),	// centre to centre of top face
			bottom = topDirection.getWithLength(-0.5*sideLength);	// centre to centre of bottom face

			addInterfacesForCubicCloakFace(
					"Front",	// faceName,
					front,	// cloakCentre2FaceCentre,
					right,	// right,
					top,	// up
					makeAllWindowsVisible
			);

//			addInterfacesForCubicCloakFace(
//					"Back",	// faceName,
//					back,	// cloakCentre2FaceCentre,
//					left,
//					top
//			);
//
//			addInterfacesForCubicCloakFace(
//					"Right",	// faceName,
//					right,	// cloakCentre2FaceCentre,
//					back,
//					top
//			);
//
//			addInterfacesForCubicCloakFace(
//					"Left",	// faceName,
//					left,	// cloakCentre2FaceCentre,
//					front,
//					top
//			);
//
//			addInterfacesForCubicCloakFace(
//					"Top",	// faceName,
//					top,	// cloakCentre2FaceCentre,
//					front,
//					right
//			);
//
//			addInterfacesForCubicCloakFace(
//					"Bottom",	// faceName,
//					bottom,	// cloakCentre2FaceCentre,
//					back,
//					right
//			);
			
				// pre-calculate the corners of the central octahedron...
				Vector3D
					frontCorner = Vector3D.sum(getCentre(), front.getProductWith(aPrime)),
//					backCorner = Vector3D.sum(getCentre(), back.getProductWith(aPrime)),
					rightCorner = Vector3D.sum(getCentre(), right.getProductWith(aPrime)),
					leftCorner = Vector3D.sum(getCentre(), left.getProductWith(aPrime)),
					topCorner = Vector3D.sum(getCentre(), top.getProductWith(aPrime)),
					bottomCorner = Vector3D.sum(getCentre(), bottom.getProductWith(aPrime));
								
				// ... and pre-calculate the corners of the cube
				Vector3D
					trfCorner = Vector3D.sum(getCentre(), top, right, front), // top right front
//					trbCorner = Vector3D.sum(getCentre(), top, right, back),	// top right back
					tlfCorner = Vector3D.sum(getCentre(), top, left, front), // top left front
//					tlbCorner = Vector3D.sum(getCentre(), top, left, back),	// top left back
					brfCorner = Vector3D.sum(getCentre(), bottom, right, front), // bottom right front
//					brbCorner = Vector3D.sum(getCentre(), bottom, right, back),	// bottom right back
					blfCorner = Vector3D.sum(getCentre(), bottom, left, front); // bottom left front
//					blbCorner = Vector3D.sum(getCentre(), bottom, left, back);	// bottom left back
				
				// add the 8-pointed "star" whose inner vertices coincide with the 6 vertices of the octahedron
				// and whose outer vertices are the 8 corners of the cube

				// first, establish the material properties
				// from Mathematica notebook, where these are the parameters of sheet 3
				double etaStar = (aPrime*(a-2)*(aPrime-3))/(a*(aPrime-2)*(a-3));
				double deltaxStar = 0;
				double deltayStar = (Math.sqrt(2)*(a-aPrime)*(aPrime-a)*aPrime)/(a*aPrime*(aPrime-2)*(a-3));
				GCLAsWithApertures starSheet = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, etaStar, etaStar, deltaxStar, deltayStar, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);

				// the polygons to the top right front corner
//				addStarPolygon("star prong to top right front corner, triangle 1", trfCorner, rightCorner, topCorner, starSheet);
				addStarPolygon("star prong to top right front corner, triangle 2", trfCorner, frontCorner, rightCorner, starSheet, makeAllWindowsVisible);
				addStarPolygon("star prong to top right front corner, triangle 3", trfCorner, topCorner, frontCorner, starSheet, true);
//				// the polygons to the top right back corner
//				addStarPolygon("star prong to top right back corner, triangle 1", trbCorner, topCorner, rightCorner, starSheet);
//				addStarPolygon("star prong to top right back corner, triangle 2", trbCorner, rightCorner, backCorner, starSheet);
//				addStarPolygon("star prong to top right back corner, triangle 3", trbCorner, backCorner, topCorner, starSheet);
				// the polygons to the top left front corner
//				addStarPolygon("star prong to top left front corner, triangle 1", tlfCorner, topCorner, leftCorner, starSheet);
				addStarPolygon("star prong to top left front corner, triangle 2", tlfCorner, leftCorner, frontCorner, starSheet, makeAllWindowsVisible);
				addStarPolygon("star prong to top left front corner, triangle 3", tlfCorner, frontCorner, topCorner, starSheet, makeAllWindowsVisible);
//				// the polygons to the top left back corner
//				addStarPolygon("star prong to top left back corner, triangle 1", tlbCorner, leftCorner, topCorner, starSheet);
//				addStarPolygon("star prong to top left back corner, triangle 2", tlbCorner, backCorner, leftCorner, starSheet);
//				addStarPolygon("star prong to top left back corner, triangle 3", tlbCorner, topCorner, backCorner, starSheet);
				// the polygons to the bottom right front corner
//				addStarPolygon("star prong to bottom right front corner, triangle 1", brfCorner, bottomCorner, rightCorner, starSheet);
				addStarPolygon("star prong to bottom right front corner, triangle 2", brfCorner, rightCorner, frontCorner, starSheet, makeAllWindowsVisible);
				addStarPolygon("star prong to bottom right front corner, triangle 3", brfCorner, frontCorner, bottomCorner, starSheet, makeAllWindowsVisible);
//				// the polygons to the bottom right back corner
//				addStarPolygon("star prong to bottom right back corner, triangle 1", brbCorner, rightCorner, bottomCorner, starSheet);
//				addStarPolygon("star prong to bottom right back corner, triangle 2", brbCorner, backCorner, rightCorner, starSheet);
//				addStarPolygon("star prong to bottom right back corner, triangle 3", brbCorner, bottomCorner, backCorner, starSheet);
				// the polygons to the bottom left front corner
//				addStarPolygon("star prong to bottom left front corner, triangle 1", blfCorner, leftCorner, bottomCorner, starSheet);
				addStarPolygon("star prong to bottom left front corner, triangle 2", blfCorner, frontCorner, leftCorner, starSheet, makeAllWindowsVisible);
				addStarPolygon("star prong to bottom left front corner, triangle 3", blfCorner, bottomCorner, frontCorner, starSheet, makeAllWindowsVisible);
//				// the polygons to the bottom left back corner
//				addStarPolygon("star prong to bottom left back corner, triangle 1", blbCorner, bottomCorner, leftCorner, starSheet);
//				addStarPolygon("star prong to bottom left back corner, triangle 2", blbCorner, leftCorner, backCorner, starSheet);
//				addStarPolygon("star prong to bottom left back corner, triangle 3", blbCorner, backCorner, bottomCorner, starSheet);

				// add the central octahedron
				
				// first, establish the material properties
				double etaOctahedron = (aPrime*(a-3))/(a*(aPrime-3));
				GCLAsWithApertures octahedronFace = new GCLAsWithApertures(Vector3D.Z, Vector3D.X, Vector3D.Y, etaOctahedron, etaOctahedron, 0, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);
				
				// now add the interfaces
				addCentralOctahedronFace("innermost top front left triangle", topCorner, leftCorner, frontCorner, octahedronFace, makeAllWindowsVisible);
				addCentralOctahedronFace("innermost top front right triangle", topCorner, frontCorner, rightCorner, octahedronFace, true);
//				addCentralOctahedronFace("innermost top back right triangle", topCorner, rightCorner, backCorner, octahedronFace);
//				addCentralOctahedronFace("innermost top back left triangle", topCorner, backCorner, leftCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom front left triangle", bottomCorner, frontCorner, leftCorner, octahedronFace, makeAllWindowsVisible);
//				addCentralOctahedronFace("innermost bottom back left triangle", bottomCorner, leftCorner, backCorner, octahedronFace);
//				addCentralOctahedronFace("innermost bottom back right triangle", bottomCorner, backCorner, rightCorner, octahedronFace);
				addCentralOctahedronFace("innermost bottom front left triangle", bottomCorner, rightCorner, frontCorner, octahedronFace, makeAllWindowsVisible);
	}

//	/**
//	 * Add all the windows (without frames --- those get added separately) for a prismatic cloak.
//	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "windows" must exist.
//	 */
//	private void addWindowsForPrismaticCloak()
//	{		
//		// prismatic, i.e. "cylindrical", cloak
//		addInterfacesForPrismaticCloakFace(
//				"Front",	// faceName,
//				frontDirection.getWithLength(sideLength/2),	// cloakCentre2FaceCentre,
//				rightDirection.getWithLength(sideLength/2),	// right,
//				topDirection.getWithLength(sideLength/2)	// up
//		);
//
//		addInterfacesForPrismaticCloakFace(
//				"Back",	// faceName,
//				frontDirection.getWithLength(-sideLength/2),	// cloakCentre2FaceCentre,
//				rightDirection.getWithLength(-sideLength/2),	// right,
//				topDirection.getWithLength(sideLength/2)	// up
//		);
//
//		addInterfacesForPrismaticCloakFace(
//				"Right",	// faceName,
//				rightDirection.getWithLength(sideLength/2),	// cloakCentre2FaceCentre,
//				frontDirection.getWithLength(-sideLength/2),	// right,
//				topDirection.getWithLength(sideLength/2)	// up
//		);
//
//		addInterfacesForPrismaticCloakFace(
//				"Left",	// faceName,
//				rightDirection.getWithLength(-sideLength/2),	// cloakCentre2FaceCentre,
//				frontDirection.getWithLength(sideLength/2),	// right,
//				topDirection.getWithLength(sideLength/2)	// up
//		);
//
//		// add the central square prism
//
//		// first, establish the material properties
//		double eta3 = ((a-2)*aPrime)/(a*(aPrime-2));
//		// ((1 + 2*a)*(-1 + 2*aPrime))/((-1 + 2*a)*(1 + 2*aPrime));
//		GeneralisedConfocalLensletArrays sheet3 = new GeneralisedConfocalLensletArrays(Vector3D.Z, Vector3D.X, Vector3D.Y, eta3, eta3, 0, 0, GlobalOrLocalCoordinateSystemType.LOCAL_OBJECT_BASIS, gCLAsTransmissionCoefficient, false);
//
//		// pre-calculate the bottom corners
//		Vector3D
//		bottomFrontCorner = Vector3D.sum(centre, frontDirection.getWithLength(aPrime*sideLength/2), topDirection.getWithLength(-sideLength/2)),
//		bottomRightCorner = Vector3D.sum(centre, rightDirection.getWithLength(aPrime*sideLength/2), topDirection.getWithLength(-sideLength/2)),
//		bottomBackCorner = Vector3D.sum(centre, frontDirection.getWithLength(-aPrime*sideLength/2), topDirection.getWithLength(-sideLength/2)),
//		bottomLeftCorner = Vector3D.sum(centre, rightDirection.getWithLength(-aPrime*sideLength/2), topDirection.getWithLength(-sideLength/2));
//
//		// now add the interfaces
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"innermost front left interface",
//				bottomLeftCorner, //corner
//				Vector3D.difference(bottomFrontCorner, bottomLeftCorner), // width vector
//				topDirection.getWithLength(sideLength),	// height vector 
//				sheet3,
//				this,
//				getStudio()
//		));
//
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"innermost front right interface",
//				bottomFrontCorner, //corner
//				Vector3D.difference(bottomRightCorner, bottomFrontCorner), // width vector
//				topDirection.getWithLength(sideLength),	// height vector 
//				sheet3,
//				this,
//				getStudio()
//		));
//
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"innermost back right interface",
//				bottomRightCorner, //corner
//				Vector3D.difference(bottomBackCorner, bottomRightCorner), // width vector
//				topDirection.getWithLength(sideLength),	// height vector 
//				sheet3,
//				this,
//				getStudio()
//		));
//
//		windows.addSceneObject(new EditableScaledParametrisedParallelogram(
//				"innermost back left interface",
//				bottomBackCorner, //corner
//				Vector3D.difference(bottomLeftCorner, bottomBackCorner), // width vector
//				topDirection.getWithLength(sideLength),	// height vector 
//				sheet3,
//				this,
//				getStudio()
//		));
//	}
	
	
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
	private void addCubeVertices(EditableSceneObjectCollection vertices)
	{
		// the corners of the cube...
		EditableSceneObjectCollection cubeVertices = new EditableSceneObjectCollection("cube vertices", true, vertices, getStudio());

		addFrameSphere("sphere in top right front cube vertex", trfCubeVertex, cubeVertices);
//		addFrameSphere("sphere in top right back cube vertex", trbCubeVertex, cubeVertices);
		addFrameSphere("sphere in top left front cube vertex", tlfCubeVertex, cubeVertices);
//		addFrameSphere("sphere in top left back cube vertex", tlbCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom right front cube vertex", brfCubeVertex, cubeVertices);
//		addFrameSphere("sphere in bottom right back cube vertex", brbCubeVertex, cubeVertices);
		addFrameSphere("sphere in bottom left front cube vertex", blfCubeVertex, cubeVertices);
//		addFrameSphere("sphere in bottom left back cube vertex", blbCubeVertex, cubeVertices);
		
		vertices.addSceneObject(cubeVertices);
	}
	
	private void addCubeEdges(EditableSceneObjectCollection edges)
	{
		// the edges of the cube
		EditableSceneObjectCollection cubeEdges = new EditableSceneObjectCollection("cube edges", true, edges, getStudio());

//		addFrameCylinder("top left cube edge", tlfCubeVertex, tlbCubeVertex, cubeEdges);
//		addFrameCylinder("top right cube edge", trfCubeVertex, trbCubeVertex, cubeEdges);
		addFrameCylinder("top front cube edge", tlfCubeVertex, trfCubeVertex, cubeEdges);
//		addFrameCylinder("top back cube edge", tlbCubeVertex, trbCubeVertex, cubeEdges);
//		addFrameCylinder("bottom left cube edge", blfCubeVertex, blbCubeVertex, cubeEdges);
//		addFrameCylinder("bottom right cube edge", brfCubeVertex, brbCubeVertex, cubeEdges);
		addFrameCylinder("bottom front cube edge", blfCubeVertex, brfCubeVertex, cubeEdges);
//		addFrameCylinder("bottom back cube edge", blbCubeVertex, brbCubeVertex, cubeEdges);
		addFrameCylinder("front left cube edge", tlfCubeVertex, blfCubeVertex, cubeEdges);
		addFrameCylinder("front right cube edge", trfCubeVertex, brfCubeVertex, cubeEdges);
//		addFrameCylinder("back left cube edge", tlbCubeVertex, blbCubeVertex, cubeEdges);
//		addFrameCylinder("back right cube edge", trbCubeVertex, brbCubeVertex, cubeEdges);
		
		edges.addSceneObject(cubeEdges);
	}
	
	private void addFramesForCubicCloak(boolean only4WindowsVisible)
	{
		// spheres at the vertices
	
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, frames, getStudio());

		// add the vertices of the cube...
		addCubeVertices(vertices);
		
		// ... and the vertices of the octahedron
		EditableSceneObjectCollection octahedronVertices = new EditableSceneObjectCollection("octahedron vertices", true, vertices, getStudio());

		addFrameSphere("sphere in front octahedron vertex", frontOctahedronVertex, octahedronVertices);
//		addFrameSphere("sphere in back octahedron vertex", backOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in right octahedron vertex", rightOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in left octahedron vertex", leftOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in top octahedron vertex", topOctahedronVertex, octahedronVertices);
		addFrameSphere("sphere in bottom octahedron vertex", bottomOctahedronVertex, octahedronVertices);
		
		vertices.addSceneObject(octahedronVertices);
		
		frames.addSceneObject(vertices);

		// cylinders connecting neighbouring vertices

		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, frames, getStudio());

		// add the edges of the cube
		addCubeEdges(edges);
		
		// the edges of the octahedron
		EditableSceneObjectCollection octahedronEdges = new EditableSceneObjectCollection("octahedron edges", true, edges, getStudio());

		addFrameCylinder("top left octahedron edge", leftOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("top right octahedron edge", rightOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("top front octahedron edge", frontOctahedronVertex, topOctahedronVertex, octahedronEdges);
//		addFrameCylinder("top back octahedron edge", backOctahedronVertex, topOctahedronVertex, octahedronEdges);
		addFrameCylinder("left front octahedron edge", leftOctahedronVertex, frontOctahedronVertex, octahedronEdges);
		addFrameCylinder("right front octahedron edge", rightOctahedronVertex, frontOctahedronVertex, octahedronEdges);
//		addFrameCylinder("left back octahedron edge", leftOctahedronVertex, backOctahedronVertex, octahedronEdges);
//		addFrameCylinder("right back octahedron edge", rightOctahedronVertex, backOctahedronVertex, octahedronEdges);		
		addFrameCylinder("bottom left octahedron edge", leftOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		addFrameCylinder("bottom right octahedron edge", rightOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		addFrameCylinder("bottom front octahedron edge", frontOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
//		addFrameCylinder("bottom back octahedron edge", backOctahedronVertex, bottomOctahedronVertex, octahedronEdges);
		
		edges.addSceneObject(octahedronEdges);
		
		// the edges connecting the cube edges to the octahedron vertices ("star" edges)
		EditableSceneObjectCollection starEdges = new EditableSceneObjectCollection("star edges", true, edges, getStudio());

		addFrameCylinder("top left front cube corner to left octahedron vertex edge", tlfCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to left octahedron vertex edge", blfCubeVertex, leftOctahedronVertex, starEdges);
//		addFrameCylinder("top left back cube corner to left octahedron vertex edge", tlbCubeVertex, leftOctahedronVertex, starEdges);
//		addFrameCylinder("bottom left back cube corner to left octahedron vertex edge", blbCubeVertex, leftOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to right octahedron vertex edge", trfCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to right octahedron vertex edge", brfCubeVertex, rightOctahedronVertex, starEdges);
//		addFrameCylinder("top right back cube corner to right octahedron vertex edge", trbCubeVertex, rightOctahedronVertex, starEdges);
//		addFrameCylinder("bottom right back cube corner to right octahedron vertex edge", brbCubeVertex, rightOctahedronVertex, starEdges);
		addFrameCylinder("top left front cube corner to top octahedron vertex edge", tlfCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to top octahedron vertex edge", trfCubeVertex, topOctahedronVertex, starEdges);
//		addFrameCylinder("top right back cube corner to top octahedron vertex edge", trbCubeVertex, topOctahedronVertex, starEdges);
//		addFrameCylinder("top left back cube corner to top octahedron vertex edge", tlbCubeVertex, topOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to bottom octahedron vertex edge", blfCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to bottom octahedron vertex edge", brfCubeVertex, bottomOctahedronVertex, starEdges);
//		addFrameCylinder("bottom right back cube corner to bottom octahedron vertex edge", brbCubeVertex, bottomOctahedronVertex, starEdges);
//		addFrameCylinder("bottom left back cube corner to bottom octahedron vertex edge", blbCubeVertex, bottomOctahedronVertex, starEdges);
		addFrameCylinder("top left front cube corner to front octahedron vertex edge", tlfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("top right front cube corner to front octahedron vertex edge", trfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("bottom right front cube corner to front octahedron vertex edge", brfCubeVertex, frontOctahedronVertex, starEdges);
		addFrameCylinder("bottom left front cube corner to front octahedron vertex edge", blfCubeVertex, frontOctahedronVertex, starEdges);
//		addFrameCylinder("top left back cube corner to back octahedron vertex edge", tlbCubeVertex, backOctahedronVertex, starEdges);
//		addFrameCylinder("top right back cube corner to back octahedron vertex edge", trbCubeVertex, backOctahedronVertex, starEdges);
//		addFrameCylinder("bottom right back cube corner to back octahedron vertex edge", brbCubeVertex, backOctahedronVertex, starEdges);
//		addFrameCylinder("bottom left back cube corner to back octahedron vertex edge", blbCubeVertex, backOctahedronVertex, starEdges);
		
		edges.addSceneObject(starEdges);
		
		frames.addSceneObject(edges);
	}

//	private void addFramesForPrismaticCloak()
//	{
//		// spheres at the vertices
//	
//		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, frames, getStudio());
//
//		// add the vertices of the cube...
//		addCubeVertices(vertices);
//		
//		// ... and the vertices of the cuboid
//		EditableSceneObjectCollection cuboidVertices = new EditableSceneObjectCollection("cuboid vertices", true, vertices, getStudio());
//
//		addFrameSphere("sphere in bottom front cuboid vertex", bfCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in bottom back cuboid vertex", bbCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in bottom right cuboid vertex", brCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in bottom left cuboid vertex", blCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in top front cuboid vertex", tfCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in top back cuboid vertex", tbCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in top right cuboid vertex", trCuboidVertex, cuboidVertices);
//		addFrameSphere("sphere in top left cuboid vertex", tlCuboidVertex, cuboidVertices);
//		
//		vertices.addSceneObject(cuboidVertices);
//		
//		frames.addSceneObject(vertices);
//
//		// cylinders connecting neighbouring vertices
//
//		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, frames, getStudio());
//
//		// add the edges of the cube
//		addCubeEdges(edges);
//		
//		// the edges of the cuboid
//		EditableSceneObjectCollection cuboidEdges = new EditableSceneObjectCollection("cuboid edges", true, edges, getStudio());
//
//		addFrameCylinder("top left front cuboid edge", tlCuboidVertex, tfCuboidVertex, cuboidEdges);
//		addFrameCylinder("top right front cuboid edge", trCuboidVertex, tfCuboidVertex, cuboidEdges);
//		addFrameCylinder("top left back cuboid edge", tlCuboidVertex, tbCuboidVertex, cuboidEdges);
//		addFrameCylinder("top right back cuboid edge", trCuboidVertex, tbCuboidVertex, cuboidEdges);
//		addFrameCylinder("bottom left front cuboid edge", blCuboidVertex, bfCuboidVertex, cuboidEdges);
//		addFrameCylinder("bottom right front cuboid edge", brCuboidVertex, bfCuboidVertex, cuboidEdges);
//		addFrameCylinder("bottom left back cuboid edge", blCuboidVertex, bbCuboidVertex, cuboidEdges);
//		addFrameCylinder("bottom right back cuboid edge", brCuboidVertex, bbCuboidVertex, cuboidEdges);
//		addFrameCylinder("front cuboid edge", bfCuboidVertex, tfCuboidVertex, cuboidEdges);
//		addFrameCylinder("back cuboid edge", bbCuboidVertex, tbCuboidVertex, cuboidEdges);
//		addFrameCylinder("right cuboid edge", brCuboidVertex, trCuboidVertex, cuboidEdges);
//		addFrameCylinder("left cuboid edge", blCuboidVertex, tlCuboidVertex, cuboidEdges);
//		
//		edges.addSceneObject(cuboidEdges);
//		
//		// the edges connecting the cube vertices to the cuboid vertices ("star" edges)
//		EditableSceneObjectCollection starEdges = new EditableSceneObjectCollection("star edges", true, edges, getStudio());
//
//		addFrameCylinder("top left front cube vertex to top left cuboid vertex edge", tlfCubeVertex, tlCuboidVertex, starEdges);
//		addFrameCylinder("top left front cube vertex to top front cuboid vertex edge", tlfCubeVertex, tfCuboidVertex, starEdges);
//		addFrameCylinder("top right front cube vertex to top right cuboid vertex edge", trfCubeVertex, trCuboidVertex, starEdges);
//		addFrameCylinder("top right front cube vertex to top front cuboid vertex edge", trfCubeVertex, tfCuboidVertex, starEdges);
//		addFrameCylinder("top left back cube vertex to top left cuboid vertex edge", tlbCubeVertex, tlCuboidVertex, starEdges);
//		addFrameCylinder("top left back cube vertex to top back cuboid vertex edge", tlbCubeVertex, tbCuboidVertex, starEdges);
//		addFrameCylinder("top right back cube vertex to top right cuboid vertex edge", trbCubeVertex, trCuboidVertex, starEdges);
//		addFrameCylinder("top right back cube vertex to top back cuboid vertex edge", trbCubeVertex, tbCuboidVertex, starEdges);
//		addFrameCylinder("bottom left front cube vertex to bottom left cuboid vertex edge", blfCubeVertex, blCuboidVertex, starEdges);
//		addFrameCylinder("bottom left front cube vertex to bottom front cuboid vertex edge", blfCubeVertex, bfCuboidVertex, starEdges);
//		addFrameCylinder("bottom right front cube vertex to bottom right cuboid vertex edge", brfCubeVertex, brCuboidVertex, starEdges);
//		addFrameCylinder("bottom right front cube vertex to bottom front cuboid vertex edge", brfCubeVertex, bfCuboidVertex, starEdges);
//		addFrameCylinder("bottom left back cube vertex to bottom left cuboid vertex edge", blbCubeVertex, blCuboidVertex, starEdges);
//		addFrameCylinder("bottom left back cube vertex to bottom back cuboid vertex edge", blbCubeVertex, bbCuboidVertex, starEdges);
//		addFrameCylinder("bottom right back cube vertex to bottom right cuboid vertex edge", brbCubeVertex, brCuboidVertex, starEdges);
//		addFrameCylinder("bottom right back cube vertex to bottom back cuboid vertex edge", brbCubeVertex, bbCuboidVertex, starEdges);
//		
//		edges.addSceneObject(starEdges);
//		
//		frames.addSceneObject(edges);
//	}

	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// calculate the direction vectors with full sidelength length
		front = frontDirection.getWithLength(sideLength);	// centre to centre of front face
		back = frontDirection.getWithLength(-sideLength);	// centre to centre of back face
		right = rightDirection.getWithLength(sideLength);	// centre to centre of right face
		left = rightDirection.getWithLength(-sideLength);	// centre to centre of left face
		top = topDirection.getWithLength(sideLength);	// centre to centre of top face
		bottom = topDirection.getWithLength(-sideLength);	// centre to centre of bottom face		
		
		// calculate the direction vectors with half sidelength length
		Vector3D front2, back2, right2, left2, top2, bottom2;
		front2 = frontDirection.getWithLength(0.5*sideLength);	// centre to centre of front face
		back2 = frontDirection.getWithLength(-0.5*sideLength);	// centre to centre of back face
		right2 = rightDirection.getWithLength(0.5*sideLength);	// centre to centre of right face
		left2 = rightDirection.getWithLength(-0.5*sideLength);	// centre to centre of left face
		top2 = topDirection.getWithLength(0.5*sideLength);	// centre to centre of top face
		bottom2 = topDirection.getWithLength(-0.5*sideLength);	// centre to centre of bottom face
		
		// prepare scene-object collection objects for the windows...
		windows = new EditableSceneObjectCollection("Windows", true, this, getStudio());
		
		// ... and the frames
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		// pre-calculate the corners of the cube
		trfCubeVertex = Vector3D.sum(getCentre(), top2, right2, front2); // top right front
		trbCubeVertex = Vector3D.sum(getCentre(), top2, right2, back2);	// top right back
		tlfCubeVertex = Vector3D.sum(getCentre(), top2, left2, front2); // top left front
		tlbCubeVertex = Vector3D.sum(getCentre(), top2, left2, back2);	// top left back
		brfCubeVertex = Vector3D.sum(getCentre(), bottom2, right2, front2); // bottom right front
		brbCubeVertex = Vector3D.sum(getCentre(), bottom2, right2, back2);	// bottom right back
		blfCubeVertex = Vector3D.sum(getCentre(), bottom2, left2, front2); // bottom left front
		blbCubeVertex = Vector3D.sum(getCentre(), bottom2, left2, back2);	// bottom left back

		// pre-calculate the vertices of the central octahedron
		frontOctahedronVertex = Vector3D.sum(getCentre(), front2.getProductWith(aPrime));
		backOctahedronVertex = Vector3D.sum(getCentre(), back2.getProductWith(aPrime));
		rightOctahedronVertex = Vector3D.sum(getCentre(), right2.getProductWith(aPrime));
		leftOctahedronVertex = Vector3D.sum(getCentre(), left2.getProductWith(aPrime));
		topOctahedronVertex = Vector3D.sum(getCentre(), top2.getProductWith(aPrime));
		bottomOctahedronVertex = Vector3D.sum(getCentre(), bottom2.getProductWith(aPrime));

		// calculate the windows and the frames
		switch(tardisWindowType)
		{
		case CLOAK_SIDE_MINIMUM:
			addWindowsForOctahedralCloak(false);
			if(showFrames) addFramesForCubicCloak(false);
			break;

		case CLOAK_SIDE:
		default:
			addWindowsForOctahedralCloak(true);
			if(showFrames) addFramesForCubicCloak(true);

//		case PRISMATIC:
//			// pre-calculate the vertices of the central cuboid
//			bfCuboidVertex = Vector3D.sum(getCentre(), bottom2, front2.getProductWith(aPrime));
//			bbCuboidVertex = Vector3D.sum(getCentre(), bottom2, back2.getProductWith(aPrime));
//			brCuboidVertex = Vector3D.sum(getCentre(), bottom2, right2.getProductWith(aPrime));
//			blCuboidVertex = Vector3D.sum(getCentre(), bottom2, left2.getProductWith(aPrime));
//			tfCuboidVertex = Vector3D.sum(getCentre(), top2, front2.getProductWith(aPrime));
//			tbCuboidVertex = Vector3D.sum(getCentre(), top2, back2.getProductWith(aPrime));
//			trCuboidVertex = Vector3D.sum(getCentre(), top2, right2.getProductWith(aPrime));
//			tlCuboidVertex = Vector3D.sum(getCentre(), top2, left2.getProductWith(aPrime));
//
//			// calculate the windows and the frames
//			addWindowsForPrismaticCloak();
//			if(showFrames) addFramesForPrismaticCloak();
//			break;
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
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("gCLA Tardis window"));
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

		tardisWindowTypeComboBox = new JComboBox<GCLATardisWindowType>(GCLATardisWindowType.values());
		basicParametersPanel.add(GUIBitsAndBobs.makeRow("Tardis-window type", tardisWindowTypeComboBox), "wrap");
		
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

		gCLAsTransmissionCoefficientLine = new LabelledDoublePanel("transmission coefficient of each surface");
		basicParametersPanel.add(gCLAsTransmissionCoefficientLine);

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
		gCLAsTransmissionCoefficientLine.setNumber(getgCLAsTransmissionCoefficient());
		tardisWindowTypeComboBox.setSelectedItem(getCloakType());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusOverSideLengthLine.setNumber(getFrameRadiusOverSideLength());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);		
		// omitInnermostSurfacesCheckBox.setSelected(isOmitInnermostSurfaces());
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableGCLAsTardisWindow acceptValuesInEditPanel()
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
		setgCLAsTransmissionCoefficient(gCLAsTransmissionCoefficientLine.getNumber());
		setCloakType((GCLATardisWindowType)(tardisWindowTypeComboBox.getSelectedItem()));
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