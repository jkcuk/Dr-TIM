package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import optics.raytrace.core.Orientation;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.imagingElements.HomogeneousPlanarImagingSurface;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;

/**
 * An editable shifty cloak, realised using homogeneous GCLAs (i.e. homogeneous glenses ).
 * 
 * The outside of the cloak is cubic; the cube axes are u, v, w.
 * 
 * Inside the outside cube, there is an inner cube which is, in physical space, centred in the outer cube.
 * In virtual space, the inner cube -- and anything placed inside it -- appears shifted.
 * 
 * @author Johannes
 */
public class EditableCubicShiftyCloak extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 2347562642193958376L;

	// parameters

	/**
	 * the centre of the (cubic) cloak
	 */
	private Vector3D centre;
	
	/**
	 * vector in the u direction, the direction of the first of the axes of the cloak
	 */
	private Vector3D uDirection;

	/**
	 * vector in the v direction, the direction of the second of the axes of the cloak
	 */
	private Vector3D vDirection;

	/**
	 * side length of the (cubic) outside
	 */
	private double sideLengthOutside;
	
	/**
	 * side length of the cubic void on the inside (which appears shifted by delta)
	 */
	private double sideLengthInside;
	
	/**
	 * the vector describing the apparent shift of the inner cube when seen from the outside
	 */
	private Vector3D delta;
	
	private double
		interfaceTransmissionCoefficient,
		frameRadius;
	private boolean showInterfaces, showFrames;
	private SurfaceProperty frameSurfaceProperty;
	
	
	// constructors
	
	public EditableCubicShiftyCloak(
			String description,
			Vector3D centre,
			Vector3D uDirection,
			Vector3D vDirection,
			double sideLengthOutside,
			double sideLengthInside,
			Vector3D delta,
			boolean showInterfaces,
			double interfaceTransmissionCoefficient,
			boolean showFrames,
			double frameRadius,
			SurfaceProperty frameSurfaceProperty,			
			SceneObject parent, 
			Studio studio
	)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		// copy the parameters into this instance's variables
		setCentre(centre);
		setDirections(uDirection, vDirection);
		setSideLengthOutside(sideLengthOutside);
		setSideLengthInside(sideLengthInside);
		setDelta(delta);
		setShowInterfaces(showInterfaces);
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficient);
		setShowFrames(showFrames);
		setFrameRadius(frameRadius);
		setFrameSurfaceProperty(frameSurfaceProperty);

		populateSceneObjectCollection();
	}


	public EditableCubicShiftyCloak(SceneObject parent, Studio studio)
	{
		this(
				"Glens shifty cloak",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// uDirection
				new Vector3D(0, 1, 0),	// vDirection
				1,	// sideLengthOutside
				0.5,	// sideLengthInside
				new Vector3D(1, 0, 0),	// delta
				true,	// showInterfaces
				0.96,	// interfaceTransmissionCoefficient
				true,	// showFrames
				0.01,	// frameRadius
				SurfaceColour.GREY50_SHINY,	// frameSurfaceProperty
				parent,
				studio
			);
	}
	
	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableCubicShiftyCloak(EditableCubicShiftyCloak original)
	{
		this(
			original.getDescription(),
			original.getCentre().clone(),
			original.getuDirection().clone(),
			original.getvDirection().clone(),
			original.getSideLengthOutside(),
			original.getSideLengthInside(),
			original.getDelta().clone(),
			original.isShowInterfaces(),
			original.getInterfaceTransmissionCoefficient(),
			original.isShowFrames(),
			original.getFrameRadius(),
			original.getFrameSurfaceProperty(),			
			original.getParent(),
			original.getStudio()
		);
	}
	

	@Override
	public EditableCubicShiftyCloak clone()
	{
		return new EditableCubicShiftyCloak(this);
	}

	
	
	
	// setters and getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}

	public Vector3D getuDirection() {
		return uDirection;
	}

	public Vector3D getvDirection() {
		return vDirection;
	}

	/**
	 * Set the orientation of the cube by giving two face normals.
	 * Note that uDirection is set first, and this.vDirection is set to the part of vDirection that is perpendicular to uDirection
	 * @param uDirection
	 * @param vDirection
	 */
	public void setDirections(Vector3D uDirection, Vector3D vDirection) {
		this.uDirection = uDirection;
		this.vDirection = vDirection.getPartPerpendicularTo(uDirection);
	}

	public double getSideLengthOutside() {
		return sideLengthOutside;
	}

	public void setSideLengthOutside(double sideLengthOutside) {
		this.sideLengthOutside = sideLengthOutside;
	}

	public double getSideLengthInside() {
		return sideLengthInside;
	}

	public void setSideLengthInside(double sideLengthInside) {
		this.sideLengthInside = sideLengthInside;
	}

	public Vector3D getDelta() {
		return delta;
	}

	public void setDelta(Vector3D delta) {
		this.delta = delta;
	}

	public boolean isShowInterfaces() {
		return showInterfaces;
	}

	public void setShowInterfaces(boolean showInterfaces) {
		this.showInterfaces = showInterfaces;
	}

	public double getInterfaceTransmissionCoefficient() {
		return interfaceTransmissionCoefficient;
	}

	public void setInterfaceTransmissionCoefficient(double interfaceTransmissionCoefficient) {
		this.interfaceTransmissionCoefficient = interfaceTransmissionCoefficient;
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





	
	// internal variables
	
	// unit vectors in the u, v and w directions
	protected Vector3D uHat, vHat, wHat;
	
	// the vertices of the cube forming the outside; U=+u direction, u=-u direction
	protected Vector3D vertexUVW, vertexUVw, vertexUvW, vertexUvw, vertexuVW, vertexuVw, vertexuvW, vertexuvw;

	// the vertices of the inner cube, physical space
	protected Vector3D vertexUVWI, vertexUVwI, vertexUvWI, vertexUvwI, vertexuVWI, vertexuVwI, vertexuvWI, vertexuvwI;
	
	// containers for the interfaces and frames
	EditableSceneObjectCollection interfaces, frames;
	
	
	
	protected void calculateUVWHats()
	{
		uHat = uDirection.getNormalised();
		vHat = vDirection.getPartPerpendicularTo(uHat).getNormalised();
		wHat = Vector3D.crossProduct(uHat, vHat);
	}
	
	
	protected void calculateVertices()
	{
		// vectors from the centre to the outer faces in the different directions
		Vector3D c2U = uHat.getProductWith(+0.5*sideLengthOutside);
		Vector3D c2u = uHat.getProductWith(-0.5*sideLengthOutside);
		Vector3D c2V = vHat.getProductWith(+0.5*sideLengthOutside);
		Vector3D c2v = vHat.getProductWith(-0.5*sideLengthOutside);
		Vector3D c2W = wHat.getProductWith(+0.5*sideLengthOutside);
		Vector3D c2w = wHat.getProductWith(-0.5*sideLengthOutside);
		
		// pre-calculate the corners of the outer cube
		vertexUVW = Vector3D.sum(getCentre(), c2U, c2V, c2W);
		vertexUVw = Vector3D.sum(getCentre(), c2U, c2V, c2w);
		vertexUvW = Vector3D.sum(getCentre(), c2U, c2v, c2W);
		vertexUvw = Vector3D.sum(getCentre(), c2U, c2v, c2w);
		vertexuVW = Vector3D.sum(getCentre(), c2u, c2V, c2W);
		vertexuVw = Vector3D.sum(getCentre(), c2u, c2V, c2w);
		vertexuvW = Vector3D.sum(getCentre(), c2u, c2v, c2W);
		vertexuvw = Vector3D.sum(getCentre(), c2u, c2v, c2w);

		// vectors from the centre to the inner faces in the different directions
		Vector3D c2UI = uHat.getProductWith(+0.5*sideLengthInside);
		Vector3D c2uI = uHat.getProductWith(-0.5*sideLengthInside);
		Vector3D c2VI = vHat.getProductWith(+0.5*sideLengthInside);
		Vector3D c2vI = vHat.getProductWith(-0.5*sideLengthInside);
		Vector3D c2WI = wHat.getProductWith(+0.5*sideLengthInside);
		Vector3D c2wI = wHat.getProductWith(-0.5*sideLengthInside);
		
		// pre-calculate the corners of the inner cube
		vertexUVWI = Vector3D.sum(getCentre(), c2UI, c2VI, c2WI);
		vertexUVwI = Vector3D.sum(getCentre(), c2UI, c2VI, c2wI);
		vertexUvWI = Vector3D.sum(getCentre(), c2UI, c2vI, c2WI);
		vertexUvwI = Vector3D.sum(getCentre(), c2UI, c2vI, c2wI);
		vertexuVWI = Vector3D.sum(getCentre(), c2uI, c2VI, c2WI);
		vertexuVwI = Vector3D.sum(getCentre(), c2uI, c2VI, c2wI);
		vertexuvWI = Vector3D.sum(getCentre(), c2uI, c2vI, c2WI);
		vertexuvwI = Vector3D.sum(getCentre(), c2uI, c2vI, c2wI);
	}
	
	
	/**
	 * @param description
	 * @param vertex1
	 * @param vertex2
	 * @param vertex3
	 * @param vertex4
	 * @param outwardsDirection
	 * @param surfaceProperty
	 * @param parent
	 * @param studio
	 * @return	a trapezium-shaped window with the given vertices and surface property
	 */
	private EditableParametrisedConvexPolygon getTrapeziumWindow(
			String description,
			Vector3D vertex1, Vector3D vertex2, Vector3D vertex3, Vector3D vertex4,
			Vector3D outwardsDirection,
			SurfaceProperty surfaceProperty,
			SceneObject parent, Studio studio
		)
	{
		Vector3D vertices[] = new Vector3D[4];
		vertices[0] = vertex1;
		vertices[1] = vertex2;
		vertices[2] = vertex3;
		vertices[3] = vertex4;
		
		// (c2-c1) x (c2-c3) should be normal to the polygon plane
		Vector3D normal = Vector3D.crossProduct(
				Vector3D.difference(vertex2, vertex1),
				Vector3D.difference(vertex2, vertex3)
			);
		
		// make sure the normal points outwards, i.e.
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
						// SurfaceColour.getRandom(),
						surfaceProperty,
						parent, studio
				);
	}
	

	/**
	 * Add all the interfaces (without frames --- those get added separately)
	 * All directions and vertices have to be pre-calculated, and the EditableSceneObjectCollection "gCLAs" must exist.
	 */
	private void addInterfaces()
	{
		// the faces of the outer cube
		
		HomogeneousPlanarImagingSurface hU = new HomogeneousPlanarImagingSurface(
				vertexUvw,	// pointOnPlane
				uHat,	// a
				vertexUvwI,	// insideSpacePosition
				Vector3D.sum(vertexUvwI, delta)	// outsideSpacePosition
			);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in +u direction",	// description
						vertexUvw,	// vertex1
						vertexUvW,	// vertex2
						vertexUVW,	// vertex3
						vertexUVw,	// vertex4
						uHat,	// outwardsDirection,
						hU.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		HomogeneousPlanarImagingSurface hu = new HomogeneousPlanarImagingSurface(
				vertexuvw,	// pointOnPlane
				uHat.getReverse(),	// a
				vertexuvwI,	// insideSpacePosition
				Vector3D.sum(vertexuvwI, delta)	// outsideSpacePosition
			);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in -u direction",	// description
						vertexuvw,	// vertex1
						vertexuvW,	// vertex2
						vertexuVW,	// vertex3
						vertexuVw,	// vertex4
						uHat.getReverse(),	// outwardsDirection,
						hu.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
						)
				);
		
		HomogeneousPlanarImagingSurface hV = new HomogeneousPlanarImagingSurface(
				vertexuVw,	// pointOnPlane
				vHat,	// a
				vertexuVwI,	// insideSpacePosition
				Vector3D.sum(vertexuVwI, delta)	// outsideSpacePosition
			);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in +v direction",	// description
						vertexuVw,	// vertex1
						vertexuVW,	// vertex2
						vertexUVW,	// vertex3
						vertexUVw,	// vertex4
						vHat,	// outwardsDirection,
						hV.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
						)
				);

		HomogeneousPlanarImagingSurface hv = new HomogeneousPlanarImagingSurface(
				vertexuvw,	// pointOnPlane
				vHat.getReverse(),	// a
				vertexuvwI,	// insideSpacePosition
				Vector3D.sum(vertexuvwI, delta)	// outsideSpacePosition
			);


		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in -v direction",	// description
						vertexuvw,	// vertex1
						vertexuvW,	// vertex2
						vertexUvW,	// vertex3
						vertexUvw,	// vertex4
						vHat.getReverse(),	// outwardsDirection,
						hv.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
						)
				);

		HomogeneousPlanarImagingSurface hW = new HomogeneousPlanarImagingSurface(
				vertexuvW,	// pointOnPlane
				wHat,	// a
				vertexuvWI,	// insideSpacePosition
				Vector3D.sum(vertexuvWI, delta)	// outsideSpacePosition
			);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in +w direction",	// description
						vertexuvW,	// vertex1
						vertexuVW,	// vertex2
						vertexUVW,	// vertex3
						vertexUvW,	// vertex4
						wHat,	// outwardsDirection,
						hW.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
						)
				);

		HomogeneousPlanarImagingSurface hw = new HomogeneousPlanarImagingSurface(
				vertexuvw,	// pointOnPlane
				wHat.getReverse(),	// a
				vertexuvwI,	// insideSpacePosition
				Vector3D.sum(vertexuvwI, delta)	// outsideSpacePosition
			);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Outer face in -w direction",	// description
						vertexuvw,	// vertex1
						vertexuVw,	// vertex2
						vertexUVw,	// vertex3
						vertexUvw,	// vertex4
						wHat.getReverse(),	// outwardsDirection,
						hw.toGCLAs(
								interfaceTransmissionCoefficient,	// transmissionCoefficient
								GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
								false	// shadowThrowing
							),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
						)
				);
		
		// the faces of the inner cube
		
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in +u direction",	// description
						vertexUvwI,	// vertex1
						vertexUvWI,	// vertex2
						vertexUVWI,	// vertex3
						vertexUVwI,	// vertex4
						uHat,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexUvwI,	// pointOnPlane
								uHat,	// a
								vertexuvwI,	// insideSpacePosition
								hU.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in -u direction",	// description
						vertexuvwI,	// vertex1
						vertexuvWI,	// vertex2
						vertexuVWI,	// vertex3
						vertexuVwI,	// vertex4
						uHat.getReverse(),	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvwI,	// pointOnPlane
								uHat.getReverse(),	// a
								vertexUvwI,	// insideSpacePosition
								hu.getImagePosition(
										Vector3D.sum(vertexUvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in +v direction",	// description
						vertexuVwI,	// vertex1
						vertexuVWI,	// vertex2
						vertexUVWI,	// vertex3
						vertexUVwI,	// vertex4
						vHat,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuVwI,	// pointOnPlane
								vHat,	// a
								vertexuvwI,	// insideSpacePosition
								hV.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in -v direction",	// description
						vertexuvwI,	// vertex1
						vertexuvWI,	// vertex2
						vertexUvWI,	// vertex3
						vertexUvwI,	// vertex4
						vHat.getReverse(),	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvwI,	// pointOnPlane
								vHat.getReverse(),	// a
								vertexuVwI,	// insideSpacePosition
								hv.getImagePosition(
										Vector3D.sum(vertexuVwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in +w direction",	// description
						vertexuvWI,	// vertex1
						vertexuVWI,	// vertex2
						vertexUVWI,	// vertex3
						vertexUvWI,	// vertex4
						wHat,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvWI,	// pointOnPlane
								wHat,	// a
								vertexuvwI,	// insideSpacePosition
								hW.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Inner face in -w direction",	// description
						vertexuvwI,	// vertex1
						vertexuVwI,	// vertex2
						vertexUVwI,	// vertex3
						vertexUvwI,	// vertex4
						wHat.getReverse(),	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvwI,	// pointOnPlane
								wHat.getReverse(),	// a
								vertexuvWI,	// insideSpacePosition
								hw.getImagePosition(
										Vector3D.sum(vertexuvWI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		
		// add "diagonal" faces, each connecting one edge of the inner cube with a cvorresponding edge of the outer cube
		
		Vector3D outwardsNormal;
		
		// diagonals parallel to the u direction
		
		outwardsNormal = Vector3D.difference(vHat, wHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +w,+v direction",	// description
						vertexuVWI,	// vertex1
						vertexUVWI,	// vertex2
						vertexUVW,	// vertex3
						vertexuVW,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuVW,	// pointOnPlane
								outwardsNormal,	// a
								vertexuvWI,	// insideSpacePosition
								hV.getImagePosition(
										Vector3D.sum(vertexuvWI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(wHat, vHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +w,-v direction",	// description
						vertexuvWI,	// vertex1
						vertexUvWI,	// vertex2
						vertexUvW,	// vertex3
						vertexuvW,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvW,	// pointOnPlane
								outwardsNormal,	// a
								vertexuvwI,	// insideSpacePosition
								hW.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.difference(wHat, vHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -w,-v direction",	// description
						vertexuvwI,	// vertex1
						vertexUvwI,	// vertex2
						vertexUvw,	// vertex3
						vertexuvw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuVwI,	// insideSpacePosition
								hv.getImagePosition(
										Vector3D.sum(vertexuVwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(wHat, vHat).getReverse();
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -w,+v direction",	// description
						vertexuVwI,	// vertex1
						vertexUVwI,	// vertex2
						vertexUVw,	// vertex3
						vertexuVw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuVw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuVWI,	// insideSpacePosition
								hw.getImagePosition(
										Vector3D.sum(vertexuVWI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		// diagonals parallel to the v direction
		
		outwardsNormal = Vector3D.difference(wHat, uHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +u,+w direction",	// description
						vertexUvWI,	// vertex1
						vertexUVWI,	// vertex2
						vertexUVW,	// vertex3
						vertexUvW,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexUvW,	// pointOnPlane
								outwardsNormal,	// a
								vertexUvwI,	// insideSpacePosition
								hW.getImagePosition(
										Vector3D.sum(vertexUvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(uHat, wHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +u,-w direction",	// description
						vertexUvwI,	// vertex1
						vertexUVwI,	// vertex2
						vertexUVw,	// vertex3
						vertexUvw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexUvw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuvwI,	// insideSpacePosition
								hU.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.difference(uHat, wHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -u,-w direction",	// description
						vertexuvwI,	// vertex1
						vertexuVwI,	// vertex2
						vertexuVw,	// vertex3
						vertexuvw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuvWI,	// insideSpacePosition
								hw.getImagePosition(
										Vector3D.sum(vertexuvWI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(uHat, wHat).getReverse();
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -u,+w direction",	// description
						vertexuvWI,	// vertex1
						vertexuVWI,	// vertex2
						vertexuVW,	// vertex3
						vertexuvW,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvW,	// pointOnPlane
								outwardsNormal,	// a
								vertexUvWI,	// insideSpacePosition
								hu.getImagePosition(
										Vector3D.sum(vertexUvWI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		// diagonals parallel to the w direction
		
		outwardsNormal = Vector3D.difference(vHat, uHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +u,+v direction",	// description
						vertexUVwI,	// vertex1
						vertexUVWI,	// vertex2
						vertexUVW,	// vertex3
						vertexUVw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexUVw,	// pointOnPlane
								outwardsNormal,	// a
								vertexUvwI,	// insideSpacePosition
								hV.getImagePosition(
										Vector3D.sum(vertexUvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(uHat, vHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in +u,-v direction",	// description
						vertexUvwI,	// vertex1
						vertexUvWI,	// vertex2
						vertexUvW,	// vertex3
						vertexUvw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexUvw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuvwI,	// insideSpacePosition
								hU.getImagePosition(
										Vector3D.sum(vertexuvwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.difference(uHat, vHat);
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -u,-v direction",	// description
						vertexuvwI,	// vertex1
						vertexuvWI,	// vertex2
						vertexuvW,	// vertex3
						vertexuvw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuvw,	// pointOnPlane
								outwardsNormal,	// a
								vertexuVwI,	// insideSpacePosition
								hv.getImagePosition(
										Vector3D.sum(vertexuVwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		outwardsNormal = Vector3D.sum(uHat, vHat).getReverse();
		interfaces.addSceneObject(
				getTrapeziumWindow(
						"Diagonal face in -u,+v direction",	// description
						vertexuVwI,	// vertex1
						vertexuVWI,	// vertex2
						vertexuVW,	// vertex3
						vertexuVw,	// vertex4
						outwardsNormal,	// outwardsDirection,
						new HomogeneousPlanarImagingSurface(
								vertexuVw,	// pointOnPlane
								outwardsNormal,	// a
								vertexUVwI,	// insideSpacePosition
								hu.getImagePosition(
										Vector3D.sum(vertexUVwI, delta),	// objectPosition
										Orientation.INWARDS
									) // outsideSpacePosition
							).toGCLAs(
									interfaceTransmissionCoefficient,	// transmissionCoefficient
									GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT,	// transmissionCoefficientMethod
									false	// shadowThrowing
								),	// surfaceProperty
						interfaces,	// parent
						getStudio()	// studio
					)
				);

		// TODO
	}
	
	
	// frames
	
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
	
	/**
	 * add spheres at the vertices of the outside cube
	 */
	private void addVertices()
	{
		EditableSceneObjectCollection vertices = new EditableSceneObjectCollection("Vertices", true, frames, getStudio());

		// the vertices of the outer cube
		addFrameSphere("sphere at +u, +v, +w vertex of cube forming outside", vertexUVW, vertices);
		addFrameSphere("sphere at +u, +v, -w vertex of cube forming outside", vertexUVw, vertices);
		addFrameSphere("sphere at +u, -v, +w vertex of cube forming outside", vertexUvW, vertices);
		addFrameSphere("sphere at +u, -v, -w vertex of cube forming outside", vertexUvw, vertices);
		addFrameSphere("sphere at -u, +v, +w vertex of cube forming outside", vertexuVW, vertices);
		addFrameSphere("sphere at -u, +v, -w vertex of cube forming outside", vertexuVw, vertices);
		addFrameSphere("sphere at -u, -v, +w vertex of cube forming outside", vertexuvW, vertices);
		addFrameSphere("sphere at -u, -v, -w vertex of cube forming outside", vertexuvw, vertices);

		// the vertices of the inner cube
		addFrameSphere("sphere at +u, +v, +w vertex of inner cube", vertexUVWI, vertices);
		addFrameSphere("sphere at +u, +v, -w vertex of inner cube", vertexUVwI, vertices);
		addFrameSphere("sphere at +u, -v, +w vertex of inner cube", vertexUvWI, vertices);
		addFrameSphere("sphere at +u, -v, -w vertex of inner cube", vertexUvwI, vertices);
		addFrameSphere("sphere at -u, +v, +w vertex of inner cube", vertexuVWI, vertices);
		addFrameSphere("sphere at -u, +v, -w vertex of inner cube", vertexuVwI, vertices);
		addFrameSphere("sphere at -u, -v, +w vertex of inner cube", vertexuvWI, vertices);
		addFrameSphere("sphere at -u, -v, -w vertex of inner cube", vertexuvwI, vertices);
		
		frames.addSceneObject(vertices);
	}
	
	private void addEdges()
	{
		EditableSceneObjectCollection edges = new EditableSceneObjectCollection("Edges", true, frames, getStudio());

		// the edges of the outer cube, ...
		addFrameCylinder("cylinder at +u, +v edge of cube forming outside", vertexUVw, vertexUVW, edges);
		addFrameCylinder("cylinder at +u, -v edge of cube forming outside", vertexUvw, vertexUvW, edges);
		addFrameCylinder("cylinder at -u, +v edge of cube forming outside", vertexuVw, vertexuVW, edges);
		addFrameCylinder("cylinder at -u, -v edge of cube forming outside", vertexuvw, vertexuvW, edges);

		addFrameCylinder("cylinder at +u, +w edge of cube forming outside", vertexUvW, vertexUVW, edges);
		addFrameCylinder("cylinder at +u, -w edge of cube forming outside", vertexUvw, vertexUVw, edges);
		addFrameCylinder("cylinder at -u, +w edge of cube forming outside", vertexuvW, vertexuVW, edges);
		addFrameCylinder("cylinder at -u, -w edge of cube forming outside", vertexuvw, vertexuVw, edges);
		
		addFrameCylinder("cylinder at +v, +w edge of cube forming outside", vertexuVW, vertexUVW, edges);
		addFrameCylinder("cylinder at +v, -w edge of cube forming outside", vertexuVw, vertexUVw, edges);
		addFrameCylinder("cylinder at -v, +w edge of cube forming outside", vertexuvW, vertexUvW, edges);
		addFrameCylinder("cylinder at -v, -w edge of cube forming outside", vertexuvw, vertexUvw, edges);

		// ... the edges of the inner cube, ...
		addFrameCylinder("cylinder at +u, +v edge of inner cube", vertexUVwI, vertexUVWI, edges);
		addFrameCylinder("cylinder at +u, -v edge of inner cube", vertexUvwI, vertexUvWI, edges);
		addFrameCylinder("cylinder at -u, +v edge of inner cube", vertexuVwI, vertexuVWI, edges);
		addFrameCylinder("cylinder at -u, -v edge of inner cube", vertexuvwI, vertexuvWI, edges);

		addFrameCylinder("cylinder at +u, +w edge of inner cube", vertexUvWI, vertexUVWI, edges);
		addFrameCylinder("cylinder at +u, -w edge of inner cube", vertexUvwI, vertexUVwI, edges);
		addFrameCylinder("cylinder at -u, +w edge of inner cube", vertexuvWI, vertexuVWI, edges);
		addFrameCylinder("cylinder at -u, -w edge of inner cube", vertexuvwI, vertexuVwI, edges);
		
		addFrameCylinder("cylinder at +v, +w edge of inner cube", vertexuVWI, vertexUVWI, edges);
		addFrameCylinder("cylinder at +v, -w edge of inner cube", vertexuVwI, vertexUVwI, edges);
		addFrameCylinder("cylinder at -v, +w edge of inner cube", vertexuvWI, vertexUvWI, edges);
		addFrameCylinder("cylinder at -v, -w edge of inner cube", vertexuvwI, vertexUvwI, edges);

		// ... and the diagonal edges
		addFrameCylinder("cylinder at diagonal edge in +u, +v, +w direction", vertexUVWI, vertexUVW, edges);
		addFrameCylinder("cylinder at diagonal edge in +u, +v, -w direction", vertexUVwI, vertexUVw, edges);
		addFrameCylinder("cylinder at diagonal edge in +u, -v, +w direction", vertexUvWI, vertexUvW, edges);
		addFrameCylinder("cylinder at diagonal edge in +u, -v, -w direction", vertexUvwI, vertexUvw, edges);
		addFrameCylinder("cylinder at diagonal edge in -u, +v, +w direction", vertexuVWI, vertexuVW, edges);
		addFrameCylinder("cylinder at diagonal edge in -u, +v, -w direction", vertexuVwI, vertexuVw, edges);
		addFrameCylinder("cylinder at diagonal edge in -u, -v, +w direction", vertexuvWI, vertexuvW, edges);
		addFrameCylinder("cylinder at diagonal edge in -u, -v, -w direction", vertexuvwI, vertexuvw, edges);
			
		frames.addSceneObject(edges);
	}
	
	private void addFrames()
	{
		// spheres at the vertices
	
		// add the vertices of the cube...
		addVertices();

		// add the edges of the cube
		addEdges();
	}

	private void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();

		// first calculate the unit vectors in the u, v and w directions and the vertices
		calculateUVWHats();
		calculateVertices();		

		// prepare scene-object collection objects for the interfaces...
		interfaces = new EditableSceneObjectCollection("Interfaces", true, this, getStudio());
		
		// ... and the frames
		frames = new EditableSceneObjectCollection("Frames", true, this, getStudio());
		
		// now fill them
		addInterfaces();
		addFrames();
		
		// add the windows and the frames to this collection
		addSceneObject(interfaces, showInterfaces);
		addSceneObject(frames, showFrames);
	}

	
	
	
	
	// GUI stuff
	
	
	
	// GUI panels
	private LabelledVector3DPanel centreLine, plusUDirectionLine, plusVDirectionLine, plusWDirectionLine, deltaLine;
	private LabelledDoublePanel sideLengthOutsideLine, sideLengthInsideLine, interfaceTransmissionCoefficientLine, frameRadiusLine;
	private JButton convertButton;
	private JCheckBox showInterfacesCheckBox, showFramesCheckBox;
	private SurfacePropertyPanel frameSurfacePropertyPanel;
	

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
		// the shifty cloak panel
		//
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");

		centreLine = new LabelledVector3DPanel("Centre");
		basicParametersPanel.add(centreLine, "wrap");

		plusUDirectionLine = new LabelledVector3DPanel("Direction to front");
		basicParametersPanel.add(plusUDirectionLine, "wrap");

		plusVDirectionLine = new LabelledVector3DPanel("Direction to right");
		basicParametersPanel.add(plusVDirectionLine, "wrap");

		plusWDirectionLine = new LabelledVector3DPanel("Direction to top");
		basicParametersPanel.add(plusWDirectionLine, "wrap");

		basicParametersPanel.add(new JLabel("(The front, right and top vectors have to form a right-handed coordinate system.)"), "wrap");

		sideLengthOutsideLine = new LabelledDoublePanel("Side length");
		basicParametersPanel.add(sideLengthOutsideLine, "wrap");

		sideLengthInsideLine = new LabelledDoublePanel("Side length of inner cube");
		basicParametersPanel.add(sideLengthInsideLine, "wrap");

		deltaLine = new LabelledVector3DPanel("Apparent shift of inner cube in virtual space");
		basicParametersPanel.add(deltaLine, "wrap");

		showInterfacesCheckBox = new JCheckBox("Show interfaces");
		basicParametersPanel.add(showInterfacesCheckBox, "wrap");

		interfaceTransmissionCoefficientLine = new LabelledDoublePanel("Transmission coefficient of each surface");
		basicParametersPanel.add(interfaceTransmissionCoefficientLine, "wrap");

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
		
		centreLine.setVector3D(getCentre());
		plusUDirectionLine.setVector3D(getuDirection());
		plusVDirectionLine.setVector3D(getvDirection());
		sideLengthOutsideLine.setNumber(getSideLengthOutside());
		sideLengthInsideLine.setNumber(getSideLengthInside());
		deltaLine.setVector3D(delta);
		showInterfacesCheckBox.setSelected(isShowInterfaces());
		interfaceTransmissionCoefficientLine.setNumber(getInterfaceTransmissionCoefficient());
		showFramesCheckBox.setSelected(isShowFrames());
		frameRadiusLine.setNumber(getFrameRadius());
		frameSurfacePropertyPanel.setSurfaceProperty(frameSurfaceProperty);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableCubicShiftyCloak acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());

		setCentre(centreLine.getVector3D());
		setDirections(plusUDirectionLine.getVector3D(), plusVDirectionLine.getVector3D());
		setSideLengthOutside(sideLengthOutsideLine.getNumber());
		setSideLengthInside(sideLengthInsideLine.getNumber());
		setDelta(deltaLine.getVector3D());
		setShowInterfaces(showInterfacesCheckBox.isSelected());
		setInterfaceTransmissionCoefficient(interfaceTransmissionCoefficientLine.getNumber());
		setShowFrames(showFramesCheckBox.isSelected());
		setFrameRadius(frameRadiusLine.getNumber());
		setFrameSurfaceProperty(frameSurfacePropertyPanel.getSurfaceProperty());

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