package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import math.MyMath;
import math.NamedDirection;
import math.NamedVector3D;
import math.Vector3D;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.surfaces.SurfacePropertyPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.exceptions.InconsistencyException;
import optics.raytrace.research.curvedSpaceSimulation.GluingType;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.SurfaceColour;

/**
 * <p>
 * An editable object representing the 3D net of a (4D) hypercube (also known as a Tesseract).
 * </p>
 * @author Johannes
 */
public class EditableNetOfHypercube extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = -8056865339646117851L;

	/**
	 * centre of the central cube
	 */
	protected Vector3D centre;
	
	/**
	 * rightwards vector
	 */
	protected Vector3D rightDirection;
	
	/**
	 * upwards vector
	 */
	protected Vector3D upDirection;
	
	/**
	 * side length of the (hyper)cube
	 */
	protected double sideLength;
	
	/**
	 * show the space-cancelling wedges
	 */
	protected boolean showSpaceCancellingWedges;

	/**
	 * Factor by which the minimum leg length of each space-cancelling wedge is multiplied.
	 */
	protected double spaceCancellingWedgeLegLengthFactor;
	
	/**
	 * Transmission coefficient of each space-cancelling-wedge surface
	 */
	protected double spaceCancellingWedgeSurfaceTransmissionCoefficient;
		
	/**
	 * Space-cancelling-wedge type, which describes the way this space-cancelling wedge is realised
	 */
	protected GluingType gluingType;
	
	/**
	 * Number of negative-space wedges, <i>N</i>, in the space-cancelling wedge
	 */
	protected int numberOfNegativeSpaceWedges;

	/**
	 * show edges and faces of the simplicial complex that forms the net of the 4-simplex
	 */
	protected boolean showNetEdgesAndFaces;
	
	/**
	 * show edges of the space-cancelling wedges, i.e. the vertices and edges of the sheets forming the space-cancelling wedges
	 */
	protected boolean showSpaceCancellingWedgeEdges;

	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty netEdgeSurfaceProperty;
	
	/**
	 * surface property of the faces of the net
	 */
	protected SurfaceProperty netFaceSurfaceProperty;
	
	/**
	 * surface property of the spheres and cylinders representing vertices and edges of the net
	 */
	protected SurfaceProperty spaceCancellingWedgeEdgeSurfaceProperty;

	/**
	 * radius of the spheres and cylinders representing the vertices and edges
	 */
	protected double edgeRadius;
	
	
	

	
	//
	// constructors
	//
	
		
	
	/**
	 * Set the parameters.
	 * If <i>vertices</i> and <i>edges</i> are given, the scene objects representing this net of a 4-simplex are added.
	 * If <i>vertices</i> and <i>edges</i> are not given, <i>vertices</i> and <i>edges</i> must be set later, followed by a call of the
	 * <i>populateSceneObjectCollection()</i> method to add the scene objects.
	 * @see optics.raytrace.GUI.sceneObjects.EditableNetOf4Simplex.populateSceneObjectCollection()
	 * 
	 * @param description
	 * @param centre
	 * @param rightDirection
	 * @param topDirection
	 * @param sideLength
	 * @param edges
	 * @param showSpaceCancellingWedges
	 * @param spaceCancellingWedgeLegLengthFactor
	 * @param spaceCancellingWedgeSurfaceTransmissionCoefficient
	 * @param gluingType
	 * @param numberOfNegativeSpaceWedges
	 * @param showNetEdges
	 * @param showSpaceCancellingWedgeEdges
	 * @param netEdgeSurfaceProperty
	 * @param netFaceSurfaceProperty
	 * @param spaceCancellingWedgeEdgeSurfaceProperty
	 * @param structureTubeRadius
	 * @param parent
	 * @param studio
	 */
	public EditableNetOfHypercube(
			String description,
			Vector3D centre,
			Vector3D rightDirection,
			Vector3D upDirection,
			double sideLength,
			boolean showSpaceCancellingWedges,
			double spaceCancellingWedgeLegLengthFactor,
			double spaceCancellingWedgeSurfaceTransmissionCoefficient,
			GluingType gluingType,
			int numberOfNegativeSpaceWedges,
			boolean showNetEdges,
			boolean showSpaceCancellingWedgeEdges,
			SurfaceProperty netEdgeSurfaceProperty,
			SurfaceProperty netFaceSurfaceProperty,
			SurfaceProperty spaceCancellingWedgeEdgeSurfaceProperty,
			double structureTubeRadius,
			SceneObject parent,
			Studio studio
		)
	{
		super(description, false, parent, studio);
		setCentre(centre);
		setRightDirection(rightDirection);
		setUpDirection(upDirection);
		setSideLength(sideLength);
		this.showSpaceCancellingWedges = showSpaceCancellingWedges;
		this.spaceCancellingWedgeLegLengthFactor = spaceCancellingWedgeLegLengthFactor;
		this.spaceCancellingWedgeSurfaceTransmissionCoefficient = spaceCancellingWedgeSurfaceTransmissionCoefficient;
		this.gluingType = gluingType;
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
		this.showNetEdgesAndFaces = showNetEdges;
		this.showSpaceCancellingWedgeEdges = showSpaceCancellingWedgeEdges;
		this.netEdgeSurfaceProperty = netEdgeSurfaceProperty;
		this.netFaceSurfaceProperty = netFaceSurfaceProperty;
		this.spaceCancellingWedgeEdgeSurfaceProperty = spaceCancellingWedgeEdgeSurfaceProperty;
		this.edgeRadius = structureTubeRadius;
		
		try {
			populateSceneObjectCollection();
		} catch (InconsistencyException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditableNetOfHypercube(EditableNetOfHypercube original)
	{
		this(
			original.getDescription(),
			original.getCentre(),
			original.getRightDirection(),
			original.getUpDirection(),
			original.getSideLength(),
			original.isShowSpaceCancellingWedges(),
			original.getSpaceCancellingWedgeLegLengthFactor(),
			original.getSpaceCancellingWedgeSurfaceTransmissionCoefficient(),
			original.getGluingType(),
			original.getNumberOfNegativeSpaceWedges(),
			original.isShowNetEdges(),
			original.isShowSpaceCancellingWedgeEdges(),
			original.getNetEdgeSurfaceProperty(),
			original.getNetFaceSurfaceProperty(),
			original.getSpaceCancellingWedgeEdgeSurfaceProperty(),
			original.getEdgeRadius(),
			original.getParent(),
			original.getStudio()
		);
	}
	
	public EditableNetOfHypercube(SceneObject parent, Studio studio)
	{
		this(
				"Net of hypercube",
				new Vector3D(0, 0, 10),	// centre
				new Vector3D(1, 0, 0),	// rightDirection
				new Vector3D(0, 1, 0),	// upDirection
				1,	// sideLength,
				true,	// showSpaceCancellingWedges
				1,	// spaceCancellingWedgeLegLengthFactor
				0.96,	// refractingSurfaceTransmissionCoefficient
				GluingType.PERFECT,	// gluingType
				1,	// numberOfNegativeSpaceWedges
				false,	// showNetStructure
				false,	// showSpaceCancellingWedgesStructure
				SurfaceColour.BLUE_SHINY,	// netStructureSurfaceProperty
				ColourFilter.CYAN_GLASS,	// netFaceSurfaceProperty
				SurfaceColour.RED_SHINY,	// spaceCancellingWedgesStructureSurfaceProperty
				0.02,	// structureTubeRadius
				parent,
				studio
			);
	}

	@Override
	public EditableNetOfHypercube clone()
	{
		return new EditableNetOfHypercube(this);
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

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public boolean isShowSpaceCancellingWedges() {
		return showSpaceCancellingWedges;
	}


	public void setShowSpaceCancellingWedges(boolean showSpaceCancellingWedges) {
		this.showSpaceCancellingWedges = showSpaceCancellingWedges;
	}


	public double getSpaceCancellingWedgeLegLengthFactor() {
		return spaceCancellingWedgeLegLengthFactor;
	}


	public void setSpaceCancellingWedgeLegLengthFactor(double spaceCancellingWedgeLegLengthFactor) {
		this.spaceCancellingWedgeLegLengthFactor = spaceCancellingWedgeLegLengthFactor;
	}


	public double getSpaceCancellingWedgeSurfaceTransmissionCoefficient() {
		return spaceCancellingWedgeSurfaceTransmissionCoefficient;
	}


	public void setSpaceCancellingWedgeSurfaceTransmissionCoefficient(double spaceCancellingWedgeSurfaceTransmissionCoefficient) {
		this.spaceCancellingWedgeSurfaceTransmissionCoefficient = spaceCancellingWedgeSurfaceTransmissionCoefficient;
	}


	public GluingType getGluingType() {
		return gluingType;
	}


	public void setGluingType(GluingType gluingType) {
		this.gluingType = gluingType;
	}


	public int getNumberOfNegativeSpaceWedges() {
		return numberOfNegativeSpaceWedges;
	}


	public void setNumberOfNegativeSpaceWedges(int numberOfNegativeSpaceWedges) {
		this.numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedges;
	}


	public boolean isShowNetEdges() {
		return showNetEdgesAndFaces;
	}


	public void setShowNetEdges(boolean showNetEdges) {
		this.showNetEdgesAndFaces = showNetEdges;
	}


	public boolean isShowSpaceCancellingWedgeEdges() {
		return showSpaceCancellingWedgeEdges;
	}


	public void setShowSpaceCancellingWedgeEdges(boolean showSpaceCancellingWedgeEdges) {
		this.showSpaceCancellingWedgeEdges = showSpaceCancellingWedgeEdges;
	}


	public SurfaceProperty getNetEdgeSurfaceProperty() {
		return netEdgeSurfaceProperty;
	}


	public void setNetEdgeSurfaceProperty(SurfaceProperty netEdgeSurfaceProperty) {
		this.netEdgeSurfaceProperty = netEdgeSurfaceProperty;
	}


	public SurfaceProperty getNetFaceSurfaceProperty() {
		return netFaceSurfaceProperty;
	}


	public void setNetFaceSurfaceProperty(SurfaceProperty netFaceSurfaceProperty) {
		this.netFaceSurfaceProperty = netFaceSurfaceProperty;
	}


	public SurfaceProperty getSpaceCancellingWedgeEdgeSurfaceProperty() {
		return spaceCancellingWedgeEdgeSurfaceProperty;
	}


	public void setSpaceCancellingWedgeEdgeSurfaceProperty(SurfaceProperty spaceCancellingWedgeEdgeSurfaceProperty) {
		this.spaceCancellingWedgeEdgeSurfaceProperty = spaceCancellingWedgeEdgeSurfaceProperty;
	}


	public double getEdgeRadius() {
		return edgeRadius;
	}


	public void setEdgeRadius(double edgeRadius) {
		this.edgeRadius = edgeRadius;
	}



	
	
	//
	//
	//

	//
	// add the scene objects that form this scene object
	//
		
	/**
	 * First clears out this EditableSceneObjectCollection, then adds all scene objects that form this scene object
	 * @throws InconsistencyException 
	 */
	public void populateSceneObjectCollection()
	throws InconsistencyException
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// calculate the u, v, w unit vectors, which point in the right, up, and forward direction, respectively
		NamedDirection right = new NamedDirection("Right", "Left", rightDirection.getNormalised());
		NamedDirection up = new NamedDirection("Top", "Bottom", upDirection.getPartPerpendicularTo(right).getNormalised());
		NamedDirection front = new NamedDirection("Front", "Back", Vector3D.crossProduct(up, right));	// note that our coordinate system is left-handed
		
		// create a scene-object collection for the net edges...
		EditableSceneObjectCollection netEdges = new EditableSceneObjectCollection("Net edges", false, this, getStudio());
		addSceneObject(netEdges, showNetEdgesAndFaces);

		// ... and another one for the space-cancelling wedges
		EditableSceneObjectCollection spaceCancellingWedges = new EditableSceneObjectCollection("Space-cancelling wedges", false, this, getStudio());
		addSceneObject(spaceCancellingWedges);
		
		// ... and one for the faces
		EditableSceneObjectCollection faces = new EditableSceneObjectCollection("Faces", false, this, getStudio());
		addSceneObject(faces, showNetEdgesAndFaces);

		// the net structure has a 6-fold symmetry, as the cube at its centre has 6 sides
		addSceneObjectsForSide(right, up, front, netEdges, spaceCancellingWedges, faces);
		addSceneObjectsForSide(right.getReverse(), up, front.getReverse(), netEdges, spaceCancellingWedges, faces);
		addSceneObjectsForSide(up, front, right, netEdges, spaceCancellingWedges, faces);
		addSceneObjectsForSide(up.getReverse(), front, right.getReverse(), netEdges, spaceCancellingWedges, faces);
		addSceneObjectsForSide(front, right, up, netEdges, spaceCancellingWedges, faces);
		addSceneObjectsForSide(front.getReverse(), right, up.getReverse(), netEdges, spaceCancellingWedges, faces);
	}
	
	
	/**
	 * The net structure has a 6-fold symmetry, as the cube at its centre has 6 sides.
	 * This method adds to this SceneObjectCollection the scene objects that correspond to the cube side with outwards normal <i>n</i>.
	 * The other faces of the cube are normal to <i>m</i> and <i>o</i>.
	 * Note that the edges of the inner cube are only added in the direction of <i>m</i>, as otherwise they would be added twice.
	 * @param n	unit normal to the cube sides added here
	 * @param m	unit normal to two other cube sides 
	 * @param o	unit normal to two more cube sides
	 */
	private void addSceneObjectsForSide(
			NamedDirection n,
			NamedDirection m,
			NamedDirection o,
			EditableSceneObjectCollection netEdges,
			EditableSceneObjectCollection spaceCancellingWedges,
			EditableSceneObjectCollection faces
		)
	{
		
		// construct vectors in the n, m, o directions and with length 0.5*<i>sideLength</i>
		Vector3D n2p = n.getWithLength(+0.5*sideLength);
		Vector3D m2p = m.getWithLength(+0.5*sideLength);
		Vector3D m2m = m.getWithLength(-0.5*sideLength);
		Vector3D o2p = o.getWithLength(+0.5*sideLength);
		Vector3D o2m = o.getWithLength(-0.5*sideLength);
		
		// ... and <i>n</i> with 1.5*<i>sideLength</i>
		Vector3D n15p = n.getWithLength(+1.5*sideLength);
		
		// calculate the corners of the inner cube, ...
		NamedVector3D nomInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getName()+" "+m.getName()+" corner", Vector3D.sum(centre, n2p, o2p, m2p));
		NamedVector3D noMInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n2p, o2p, m2m));
		NamedVector3D nOmInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getBackwardsName()+" "+m.getName()+" corner", Vector3D.sum(centre, n2p, o2m, m2p));
		NamedVector3D nOMInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getBackwardsName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n2p, o2m, m2m));

		// ..., those of the outer cube, ...
		NamedVector3D nomOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getName()+" "+m.getName()+" corner", Vector3D.sum(centre, n15p, o2p, m2p));
		NamedVector3D noMOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n15p, o2p, m2m));
		NamedVector3D nOmOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getBackwardsName()+" "+m.getName()+" corner", Vector3D.sum(centre, n15p, o2m, m2p));
		NamedVector3D nOMOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getBackwardsName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n15p, o2m, m2m));
		
		// ..., and the centre of the cube opposite (in 4-space) the inner cube, one quarter of which is attached to each outer cube
		NamedVector3D nOppositeCubeCentre = new NamedVector3D("Centre of cube opposite (in 4-space) inner cube", Vector3D.sum(centre, n.getWithLength(2*sideLength)));

		// add the edges of the inner cube, but only the ones in the <i>m</i> direction
		addSceneObjectsForInnerEdge(
				"Inner cube's "+n.getName()+", "+o.getName()+" edge",	// name,
				noMInnerCorner,	// startPoint
				nomInnerCorner,	// endPoint,
				netEdges, spaceCancellingWedges	// collections
			);
		addSceneObjectsForInnerEdge(
				"Inner cube's "+n.getName()+", "+o.getBackwardsName()+" edge",	// description
				nOMInnerCorner,	// startPoint
				nOmInnerCorner,	// endPoint,
				netEdges, spaceCancellingWedges	// collections
			);
		
		// add the edges of the outer cube;
		// unlike above, add all edges in the <i>n</i>, <i>m</i>, and <i>o</i> directions, but not those that are shared with the inner cube
		// first add the edges of the outer square, ...
		addSceneObjectsForOuterEdge(
				n.getName()+" "+"outer cube's outer "+o.getName()+" edge",	// name,
				noMOuterCorner,	// startPoint
				nomOuterCorner,	// endPoint,
				n,	// normal direction
				o,	// other direction
				netEdges, spaceCancellingWedges	// collections
			);
		addSceneObjectsForOuterEdge(
				n.getName()+" "+"outer cube's outer "+o.getBackwardsName()+" edge",	// description
				nOMOuterCorner,	// startPoint
				nOmOuterCorner,	// endPoint,
				n,	// normal direction
				o.getReverse(),	// other direction
				netEdges, spaceCancellingWedges	// collections
			);
		addSceneObjectsForOuterEdge(
				n.getName()+" "+"outer cube's outer "+m.getName()+" edge",	// name,
				nomOuterCorner,	// startPoint
				nOmOuterCorner,	// endPoint,
				n,	// normal direction
				m,	// other direction
				netEdges, spaceCancellingWedges	// collections
			);
		addSceneObjectsForOuterEdge(
				n.getName()+" "+"outer cube's outer "+m.getBackwardsName()+" edge",	// description
				noMOuterCorner,	// startPoint
				nOMOuterCorner,	// endPoint,
				n,	// normal direction
				m.getReverse(),	// other direction
				netEdges, spaceCancellingWedges	// collections
			);
		// ..., then those linking them to the corners of the inner square, ...
		addCylinder(
				n.getName()+" "+"outer cube's "+o.getName()+" "+m.getName()+" edge",	// name,
				nomInnerCorner,	// startPoint
				nomOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+o.getName()+" "+m.getBackwardsName()+" edge",	// name,
				noMInnerCorner,	// startPoint
				noMOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+o.getBackwardsName()+" "+m.getName()+" edge",	// name,
				nOmInnerCorner,	// startPoint
				nOmOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+o.getBackwardsName()+" "+m.getBackwardsName()+" edge",	// name,
				nOMInnerCorner,	// startPoint
				nOMOuterCorner,	// endPoint,
				netEdges	// collection
			);
		// ..., and finally the outer pyramids
		addCylinder(
				n.getName()+" "+"outer cube's "+nomOuterCorner.getName()+" to opposite cube's centre",	// name,
				nOppositeCubeCentre,	// startPoint
				nomOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+noMOuterCorner.getName()+" to opposite cube's centre",	// name,
				nOppositeCubeCentre,	// startPoint
				noMOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+nOmOuterCorner.getName()+" to opposite cube's centre",	// name,
				nOppositeCubeCentre,	// startPoint
				nOmOuterCorner,	// endPoint,
				netEdges	// collection
			);
		addCylinder(
				n.getName()+" "+"outer cube's "+nOMOuterCorner.getName()+" to opposite cube's centre",	// name,
				nOppositeCubeCentre,	// startPoint
				nOMOuterCorner,	// endPoint,
				netEdges	// collection
			);
		
		// TODO add faces
//		// calculate the corners of the inner cube, ...
//		NamedVector3D nomInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getName()+" "+m.getName()+" corner", Vector3D.sum(centre, n2p, o2p, m2p));
//		NamedVector3D noMInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n2p, o2p, m2m));
//		NamedVector3D nOmInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getBackwardsName()+" "+m.getName()+" corner", Vector3D.sum(centre, n2p, o2m, m2p));
//		NamedVector3D nOMInnerCorner = new NamedVector3D("Inner "+n.getName()+" "+o.getBackwardsName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n2p, o2m, m2m));
//
//		// ..., those of the outer cube, ...
//		NamedVector3D nomOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getName()+" "+m.getName()+" corner", Vector3D.sum(centre, n15p, o2p, m2p));
//		NamedVector3D noMOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n15p, o2p, m2m));
//		NamedVector3D nOmOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getBackwardsName()+" "+m.getName()+" corner", Vector3D.sum(centre, n15p, o2m, m2p));
//		NamedVector3D nOMOuterCorner = new NamedVector3D("Outer "+n.getName()+" "+o.getBackwardsName()+" "+m.getBackwardsName()+" corner", Vector3D.sum(centre, n15p, o2m, m2m));
//		
//		// ..., and the centre of the cube opposite (in 4-space) the inner cube, one sixth of which is attached to each outer cube
//		NamedVector3D nOppositeCubeCentre = new NamedVector3D("Centre of cube opposite (in 4-space) inner cube", Vector3D.sum(centre, n.getWithLength(2*sideLength)));
		addSquareFace(n.getName()+" face of inner cube", nomInnerCorner, m.getWithLength(-sideLength), o.getWithLength(-sideLength), faces);
		addSquareFace(m.getName()+" face of "+n.getName()+" outer cube", nomInnerCorner, n.getWithLength(sideLength), o.getWithLength(-sideLength), faces);
		addSquareFace(m.getBackwardsName()+" face of "+n.getName()+" outer cube", noMInnerCorner, n.getWithLength(sideLength), o.getWithLength(-sideLength), faces);
		addSquareFace(o.getName()+" face of "+n.getName()+" outer cube", nomInnerCorner, n.getWithLength(sideLength), m.getWithLength(-sideLength), faces);
		addSquareFace(o.getBackwardsName()+" face of "+n.getName()+" outer cube", nOmInnerCorner, n.getWithLength(sideLength), m.getWithLength(-sideLength), faces);
		addSquareFace(n.getName()+" face of "+n.getName()+" outer cube", nomOuterCorner, m.getWithLength(-sideLength), o.getWithLength(-sideLength), faces);
		addTriangularFace(m.getName()+" face of "+n.getName()+" sixth of 8th cube", nomOuterCorner, o.getWithLength(-sideLength), Vector3D.difference(nOppositeCubeCentre, nomOuterCorner), faces);
		addTriangularFace(m.getBackwardsName()+" face of "+n.getName()+" sixth of 8th cube", noMOuterCorner, o.getWithLength(-sideLength), Vector3D.difference(nOppositeCubeCentre, noMOuterCorner), faces);
		addTriangularFace(o.getName()+" face of "+n.getName()+" sixth of 8th cube", nomOuterCorner, m.getWithLength(-sideLength), Vector3D.difference(nOppositeCubeCentre, nomOuterCorner), faces);
		addTriangularFace(o.getBackwardsName()+" face of "+n.getName()+" sixth of 8th cube", nOmOuterCorner, m.getWithLength(-sideLength), Vector3D.difference(nOppositeCubeCentre, nOmOuterCorner), faces);
	}
	
	/**
	 * Add one of the triangular faces of the cube opposite the inner cube
	 * @param name
	 * @param vertex1
	 * @param vertex1ToVertex2
	 * @param vertex1ToVertex3
	 * @param collection
	 */
	private void addTriangularFace(
			String name,
			Vector3D vertex1,
			Vector3D vertex1ToVertex2,
			Vector3D vertex1ToVertex3,
			EditableSceneObjectCollection collection
		)
	{
		collection.addSceneObject(
		new EditableTriangle(
				name,	// description
				vertex1,
				vertex1ToVertex2,
				vertex1ToVertex3,
				false,	// semiInfinite
				vertex1ToVertex2,	// uUnitVector
				vertex1ToVertex3,	// vUnitVector
				netFaceSurfaceProperty,	// windowSurfaceProperty
				collection,	// parent
				getStudio()
		)
		);
	}
	
	/**
	 * Add one of the cube faces
	 * @param name
	 * @param corner
	 * @param spanVector1
	 * @param spanVector2
	 * @param collection
	 */
	private void addSquareFace(String name, Vector3D corner, Vector3D spanVector1, Vector3D spanVector2, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(
				new EditableScaledParametrisedParallelogram(
						name,	// description
						corner, 
						spanVector1,
						spanVector2, 
						netFaceSurfaceProperty,	// surfaceProperty
						collection,	// parent
						getStudio()
					)
			);
	}
	
	/**
	 * Add a cylinder to represent an edge of the net
	 * @param name
	 * @param startPoint
	 * @param endPoint
	 * @param collection	the scene-object collection the cylinder gets added to
	 */
	private void addCylinder(String name, Vector3D startPoint, Vector3D endPoint, EditableSceneObjectCollection collection)
	{
		collection.addSceneObject(
			new EditableParametrisedCylinder(
					name,	// description
					startPoint,
					endPoint,
					edgeRadius,	// radius
					netEdgeSurfaceProperty,	// surfaceProperty
					collection,	// parent
					getStudio()
				));
	}
	
	/**
	 * For each edge of the inner cube, add a cylinder (for visualising the net structure) and a 90-degree space-cancelling wedge
	 * @param edgeName
	 * @param startPoint
	 * @param endPoint
	 * @param netEdges
	 * @param spaceCancellingWedges
	 */
	private void addSceneObjectsForInnerEdge(String edgeName, Vector3D startPoint, Vector3D endPoint, EditableSceneObjectCollection netEdges, EditableSceneObjectCollection spaceCancellingWedges)
	{
		// add a cylinder that represents the edge in the net, ...
		addCylinder(edgeName, startPoint, endPoint, netEdges);
		
		// ... a 90 degree space-cancelling wedge, ...
		Vector3D apexEdgeCentre = Vector3D.sum(startPoint, endPoint).getProductWith(0.5);
		spaceCancellingWedges.addSceneObject(
				new EditableSpaceCancellingWedge(
						"Space-cancelling wedge at "+edgeName,	// description
						0.5*Math.PI,	// wedgeAngle
						apexEdgeCentre,	// apexEdgeCentre,
						Vector3D.difference(endPoint, startPoint),	// apexEdgeDirection
						Vector3D.difference(apexEdgeCentre, centre),	// bisectorDirection
						/* (1+Math.sqrt(2*0.5*0.5))* */sideLength*spaceCancellingWedgeLegLengthFactor,	// legLength
						sideLength,	// apexEdgeLength
						//  SCWedgeLegFaceShape.RECTANGULAR,
						showSpaceCancellingWedges,	// showSheets
						MyMath.deg2rad(91),	// containmentMirrorsAngleWithSides
						spaceCancellingWedgeSurfaceTransmissionCoefficient,	// sheetTransmissionCoefficient
						showSpaceCancellingWedgeEdges,	// showEdges
						edgeRadius-MyMath.TINY,	// edgeRadius
						spaceCancellingWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty
						gluingType,	// gluingType
						numberOfNegativeSpaceWedges,	// numberOfNegativeSpaceWedges,
						spaceCancellingWedges,	// parent, 
						getStudio()
						)
				);

		// ... and a 180 degree space-cancelling wedge
		spaceCancellingWedges.addSceneObject(
				new EditableSpaceCancellingWedge(
						"Space-cancelling wedge at "+edgeName,	// description
						Math.PI,	// wedgeAngle
						Vector3D.sum(
								centre,
								Vector3D.difference(apexEdgeCentre, centre).getProductWith(2)
							),	// apexEdgeCentre,
						Vector3D.difference(endPoint, startPoint),	// apexEdgeDirection
						Vector3D.difference(apexEdgeCentre, centre),	// bisectorDirection
						2.*Math.sqrt(2*0.5*0.5)*sideLength*spaceCancellingWedgeLegLengthFactor,	// legLength
						sideLength,	// apexEdgeLength
						// SCWedgeLegFaceShape.RECTANGULAR,
						showSpaceCancellingWedges,	// showSheets
						MyMath.deg2rad(91),	// containmentMirrorsAngleWithSides
						spaceCancellingWedgeSurfaceTransmissionCoefficient,	// sheetTransmissionCoefficient
						showSpaceCancellingWedgeEdges,	// showEdges
						edgeRadius-MyMath.TINY,	// edgeRadius
						spaceCancellingWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty
						gluingType,	// gluingType
						numberOfNegativeSpaceWedges,	// numberOfNegativeSpaceWedges,
						spaceCancellingWedges,	// parent, 
						getStudio()
						)
				);
}

	/**
	 * For each outer edge of the outer cubes, add a cylinder (for visualising the net structure) and a 45-degree space-cancelling wedge
	 * @param edgeName
	 * @param startPoint
	 * @param n	normal direction
	 * @param otherDirection	other direction
	 * @param endPoint
	 * @param netEdges
	 * @param spaceCancellingWedges
	 */
	private void addSceneObjectsForOuterEdge(
			String edgeName,
			Vector3D startPoint, Vector3D endPoint,
			Vector3D n,	// normal direction
			Vector3D otherDirection,	// other direction
			EditableSceneObjectCollection netEdges, EditableSceneObjectCollection spaceCancellingWedges
		)
	{
		// add a cylinder that represents the edge in the net...
		addCylinder(edgeName, startPoint, endPoint, netEdges);
		
//		// ... and a 45 degree space-cancelling wedge
//		Vector3D apexEdgeCentre = Vector3D.sum(startPoint, endPoint).getProductWith(0.5);
//		// calculate the bisector direction
//		Vector3D bisectorDirection = Vector3D.sum(
//				n.getWithLength(Math.cos(Math.PI/8.)),
//				otherDirection.getWithLength(-Math.sin(Math.PI/8.))	// "-" because <i>otherDirection</i> is pointing outwards
//			);
//		spaceCancellingWedges.addSceneObject(
//				new EditableSpaceCancellingWedge(
//						"Space-cancelling wedge at "+edgeName,	// description
//						Math.PI/4.,	// wedgeAngle
//						apexEdgeCentre,	// apexEdgeCentre,
//						Vector3D.difference(endPoint, startPoint),	// apexEdgeDirection
//						bisectorDirection,	// bisectorDirection
//						Math.sqrt(2*0.5*0.5)*sideLength*spaceCancellingWedgeLegLengthFactor,	// legLength
//						sideLength,	// apexEdgeLength
//						showSpaceCancellingWedges,	// showSheets
//						spaceCancellingWedgeSurfaceTransmissionCoefficient,	// sheetTransmissionCoefficient
//						showSpaceCancellingWedgeEdges,	// showEdges
//						edgeRadius-MyMath.TINY,	// edgeRadius
//						spaceCancellingWedgeEdgeSurfaceProperty,	// edgeSurfaceProperty
//						gluingType,	// gluingType
//						numberOfNegativeSpaceWedges,	// numberOfNegativeSpaceWedges,
//						spaceCancellingWedges,	// parent, 
//						getStudio()
//						)
//				);
	}

	
	//
	// GUI stuff
	//
	
	// GUI panels
	private LabelledVector3DPanel centrePanel, rightDirectionPanel, upDirectionPanel;
	private LabelledDoublePanel sideLengthPanel, edgeRadiusPanel, spaceCancellingWedgeLegLengthFactorPanel, spaceCancellingWedgeSurfaceTransmissionCoefficientPanel;
	private LabelledIntPanel numberOfNegativeSpaceWedgesPanel;
	private JComboBox<GluingType> gluingTypeComboBox;
	private JCheckBox showSpaceCancellingWedgesCheckBox, showNetEdgesCheckBox, showSpaceCancellingWedgesEdgesCheckBox;
	private SurfacePropertyPanel netStructureSurfacePropertyPanel, netFaceSurfacePropertyPanel, spaceCancellingWedgeEdgesSurfacePropertyPanel;
	private JButton convertButton;

	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		// editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Lens simplicial complex"));
		editPanel.setLayout(new MigLayout("insets 0"));
		
		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		editPanel.add(descriptionPanel, "wrap");

		JTabbedPane tabbedPane = new JTabbedPane();
		editPanel.add(tabbedPane, "wrap");
		
		

		//
		// the main parameters panel
		//
		
		JPanel mainParametersPanel = new JPanel();
		mainParametersPanel.setLayout(new MigLayout("insets 0"));
		
		centrePanel = new LabelledVector3DPanel("Centre");
		mainParametersPanel.add(centrePanel, "wrap");

		rightDirectionPanel = new LabelledVector3DPanel("Right direction");
		mainParametersPanel.add(rightDirectionPanel, "wrap");

		upDirectionPanel = new LabelledVector3DPanel("Up direction");
		mainParametersPanel.add(upDirectionPanel, "wrap");

		sideLengthPanel = new LabelledDoublePanel("Side length");
		mainParametersPanel.add(sideLengthPanel, "wrap");

		showSpaceCancellingWedgesCheckBox = new JCheckBox("Show space-cancelling wedges");
		mainParametersPanel.add(showSpaceCancellingWedgesCheckBox, "wrap");
		
		spaceCancellingWedgeLegLengthFactorPanel = new LabelledDoublePanel("Leg-length factor");
		mainParametersPanel.add(spaceCancellingWedgeLegLengthFactorPanel, "wrap");
		
		gluingTypeComboBox = new JComboBox<GluingType>(GluingType.values());
		gluingTypeComboBox.addActionListener(this);
		mainParametersPanel.add(gluingTypeComboBox, "wrap");
		
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of space-cancelling-wedge surfaces");
		mainParametersPanel.add(spaceCancellingWedgeSurfaceTransmissionCoefficientPanel, "wrap");
		
		numberOfNegativeSpaceWedgesPanel = new LabelledIntPanel("Number of negative-space wedges per space-cancelling wedge");
		mainParametersPanel.add(numberOfNegativeSpaceWedgesPanel, "wrap");
		
		tabbedPane.addTab("Main parameters", mainParametersPanel);

		//
		// the structure-visualisation panel
		//
		
		JPanel structureVisualisationPanel = new JPanel();
		structureVisualisationPanel.setLayout(new MigLayout("insets 0"));
		
		showNetEdgesCheckBox = new JCheckBox("Show structure (edges and faces) of net of 4-simplex");
		structureVisualisationPanel.add(showNetEdgesCheckBox, "wrap");

		showSpaceCancellingWedgesEdgesCheckBox = new JCheckBox("Show edges of space-cancelling wedges");
		structureVisualisationPanel.add(showSpaceCancellingWedgesEdgesCheckBox, "wrap");

		edgeRadiusPanel = new LabelledDoublePanel("Tube radius");
		structureVisualisationPanel.add(edgeRadiusPanel, "wrap");

		netStructureSurfacePropertyPanel = new SurfacePropertyPanel("Surface of net edges", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(netStructureSurfacePropertyPanel, "wrap");
		netStructureSurfacePropertyPanel.setIPanel(iPanel);

		netFaceSurfacePropertyPanel = new SurfacePropertyPanel("Surface of net faces", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(netFaceSurfacePropertyPanel, "wrap");
		netFaceSurfacePropertyPanel.setIPanel(iPanel);
		
		spaceCancellingWedgeEdgesSurfacePropertyPanel = new SurfacePropertyPanel("Surface of space-cancelling-wedge edges", getStudio().getScene());
		// frameSurfacePropertyPanel.addButtonsActionListener(new SurfacePropertyPanelListener(frameSurfacePropertyPanel));
		structureVisualisationPanel.add(spaceCancellingWedgeEdgesSurfacePropertyPanel, "wrap");
		spaceCancellingWedgeEdgesSurfacePropertyPanel.setIPanel(iPanel);

		tabbedPane.addTab("Structure-visualisation details", structureVisualisationPanel);

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
		centrePanel.setVector3D(centre);
		rightDirectionPanel.setVector3D(rightDirection);
		upDirectionPanel.setVector3D(upDirection);
		sideLengthPanel.setNumber(sideLength);
		showSpaceCancellingWedgesCheckBox.setSelected(showSpaceCancellingWedges);
		spaceCancellingWedgeLegLengthFactorPanel.setNumber(spaceCancellingWedgeLegLengthFactor);
		spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.setNumber(spaceCancellingWedgeSurfaceTransmissionCoefficient);
		numberOfNegativeSpaceWedgesPanel.setNumber(numberOfNegativeSpaceWedges);
		gluingTypeComboBox.setSelectedItem(gluingType);
		edgeRadiusPanel.setNumber(edgeRadius);
		showNetEdgesCheckBox.setSelected(showNetEdgesAndFaces);
		showSpaceCancellingWedgesEdgesCheckBox.setSelected(showSpaceCancellingWedgeEdges);
		netStructureSurfacePropertyPanel.setSurfaceProperty(netEdgeSurfaceProperty);
		netFaceSurfacePropertyPanel.setSurfaceProperty(netFaceSurfaceProperty);
		spaceCancellingWedgeEdgesSurfacePropertyPanel.setSurfaceProperty(spaceCancellingWedgeEdgeSurfaceProperty);
		enableOrDisableAdditionalWedgeTypeControlPanels();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditableNetOfHypercube acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		centre = centrePanel.getVector3D();
		rightDirection = rightDirectionPanel.getVector3D();
		upDirection = upDirectionPanel.getVector3D();
		sideLength = sideLengthPanel.getNumber();
		showSpaceCancellingWedges = showSpaceCancellingWedgesCheckBox.isSelected();
		spaceCancellingWedgeLegLengthFactor = spaceCancellingWedgeLegLengthFactorPanel.getNumber();
		spaceCancellingWedgeSurfaceTransmissionCoefficient = spaceCancellingWedgeSurfaceTransmissionCoefficientPanel.getNumber();
		numberOfNegativeSpaceWedges = numberOfNegativeSpaceWedgesPanel.getNumber();
		gluingType = (GluingType)gluingTypeComboBox.getSelectedItem();
		edgeRadius = edgeRadiusPanel.getNumber();
		showNetEdgesAndFaces = showNetEdgesCheckBox.isSelected();
		showSpaceCancellingWedgeEdges = showSpaceCancellingWedgesEdgesCheckBox.isSelected();
		netEdgeSurfaceProperty = netStructureSurfacePropertyPanel.getSurfaceProperty();
		netFaceSurfaceProperty = netFaceSurfacePropertyPanel.getSurfaceProperty();
		spaceCancellingWedgeEdgeSurfaceProperty = spaceCancellingWedgeEdgesSurfacePropertyPanel.getSurfaceProperty();

		// add the objects
		try {
			populateSceneObjectCollection();
		}
		catch (InconsistencyException e)
		{
			e.printStackTrace();
		}
		
		return this;
	}

	/**
	 * depending on the value of <i>gluingType</i>, enable or disable the control panels for additional parameters
	 */
	private void enableOrDisableAdditionalWedgeTypeControlPanels()
	{
		// show or hide additional parameters as appropriate
		switch(gluingType)
		{
		case SPACE_CANCELLING_WEDGES:
			numberOfNegativeSpaceWedgesPanel.setEnabled(true);
			break;
		case PERFECT:
		default:
			numberOfNegativeSpaceWedgesPanel.setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == convertButton)
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-net-of-4-simplex");
			container.setValuesInEditPanel();
		}
		else if(e.getSource() == gluingTypeComboBox)
		{
			setGluingType((GluingType)(gluingTypeComboBox.getSelectedItem()));
			enableOrDisableAdditionalWedgeTypeControlPanels();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited == netStructureSurfacePropertyPanel)
			{
				// net-structure surface property has been edited
				netEdgeSurfaceProperty = (SurfaceProperty)edited;
				netStructureSurfacePropertyPanel.setSurfaceProperty(netEdgeSurfaceProperty);
			}
			else if(edited == netFaceSurfacePropertyPanel)
			{
				// netFaceSurfacePropertyPanel has been edited
				netFaceSurfaceProperty = (SurfaceProperty)edited;
				netFaceSurfacePropertyPanel.setSurfaceProperty(netFaceSurfaceProperty);
			}
			else if(edited == spaceCancellingWedgeEdgesSurfacePropertyPanel)
			{
				// space-cancelling-wedges-structure surface property has been edited
				spaceCancellingWedgeEdgeSurfaceProperty = (SurfaceProperty)edited;
				spaceCancellingWedgeEdgesSurfacePropertyPanel.setSurfaceProperty(spaceCancellingWedgeEdgeSurfaceProperty);
			}
	}
}