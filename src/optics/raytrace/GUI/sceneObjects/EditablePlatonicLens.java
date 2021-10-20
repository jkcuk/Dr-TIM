package optics.raytrace.GUI.sceneObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import math.*;
import optics.raytrace.GUI.core.IPanel;
import optics.raytrace.GUI.core.IPanelComponent;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledStringPanel;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.Studio;
import optics.raytrace.surfaces.GlensSurface;

/**
 * A 3D generalisation of the "star of lenses".
 * 
 * @author Johannes
 */
public class EditablePlatonicLens extends EditableSceneObjectCollection implements ActionListener
{
	private static final long serialVersionUID = 1819715062857821657L;

	//
	// parameters
	//
	
	/**
	 * The Platonic solid on which this lens is based.
	 */
	private EditablePlatonicSolid platonicSolid;
	
	/**
	 * Focal length of each of the lenses.
	 */
	private double focalLength;
		
	/**
	 * Transmission coefficient of each lens.
	 */
	private double lensTransmissionCoefficient;
	
	/**
	 * If true, lenses throw shadows
	 */
	private boolean lensShadowThrowing;

	/**
	 * Show the Platonic solid?
	 * To show the lens edges, this has to be true, the Platonic solid's edges must be shown, and <showInnerEdges>
	 * must be true.
	 */
	private boolean showPlatonicSolid;
	
//	/**
//	 * Show pyramidal caps that (hopefully) complete Platonic lenses into TO devices
//	 */
//	private boolean showPyramidalCaps;
	
	/**
	 * Show the lens edges inside the Platonic solid and, if present, of the pyramidal caps on the outside faces
	 */
	private boolean showLensEdges;
	

	//
	// internal variables
	//
	
	// containers for the lenses and frames
	private EditableSceneObjectCollection lenses;	//, frames;
	

	/**
	 * @param description
	 * @param platonicSolid
	 * @param focalLength
	 * @param lensTransmissionCoefficient
	 * @param lensShadowThrowing
	 * @param showPlatonicSolid
	 * @param showLensEdges
	 * @param parent
	 * @param studio
	 */
	public EditablePlatonicLens(
			String description,
			EditablePlatonicSolid platonicSolid,
			double focalLength,
			double lensTransmissionCoefficient,
			boolean lensShadowThrowing,
			boolean showPlatonicSolid,
			// boolean showPyramidalCaps,
			boolean showLensEdges,
			SceneObject parent, 
			Studio studio
			)
	{
		// constructor of superclass
		super(description, true, parent, studio);
		
		setPlatonicSolid(platonicSolid);
		setFocalLength(focalLength);
		setLensTransmissionCoefficient(lensTransmissionCoefficient);
		setLensShadowThrowing(lensShadowThrowing);
		setShowPlatonicSolid(showPlatonicSolid);
		// setShowPyramidalCaps(showPyramidalCaps);
		setShowLensEdges(showLensEdges);

		populateSceneObjectCollection();
	}
	
	public EditablePlatonicLens(SceneObject parent, Studio studio)
	{
		this(
				"Platonic lens",	// description
				new EditablePlatonicSolid(parent, studio),	// platonicSolid
				1.0,	// focalLength
				0.96,	// lensTransmissionCoefficient
				false,	// lensShadowThrowing
				true,	// showPlatonicSolid
				// false,	// showPyramidalCaps
				true,	// showInnerEdges
				parent,
				studio
			);
	}

	/**
	 * Create a clone of original
	 * @param original
	 */
	public EditablePlatonicLens(EditablePlatonicLens original)
	{
		this(
			original.getDescription(),
			original.getPlatonicSolid().clone(),
			original.getFocalLength(),
			original.getLensTransmissionCoefficient(),
			original.isLensShadowThrowing(),
			original.isShowPlatonicSolid(),
			// original.isShowPyramidalCaps(),
			original.isShowLensEdges(),
			original.getParent(),
			original.getStudio()
		);
	}

	@Override
	public EditablePlatonicLens clone()
	{
		return new EditablePlatonicLens(this);
	}

	
	//
	// setters and getters
	//
	
	public EditablePlatonicSolid getPlatonicSolid() {
		return platonicSolid;
	}

	public void setPlatonicSolid(EditablePlatonicSolid platonicSolid) {
		this.platonicSolid = platonicSolid;
	}

	public double getFocalLength() {
		return focalLength;
	}

	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}
	
	public double getLensTransmissionCoefficient() {
		return lensTransmissionCoefficient;
	}

	public void setLensTransmissionCoefficient(double lensTransmissionCoefficient) {
		this.lensTransmissionCoefficient = lensTransmissionCoefficient;
	}

	public boolean isLensShadowThrowing() {
		return lensShadowThrowing;
	}

	public void setLensShadowThrowing(boolean lensShadowThrowing) {
		this.lensShadowThrowing = lensShadowThrowing;
	}

	public boolean isShowPlatonicSolid() {
		return showPlatonicSolid;
	}

	public void setShowPlatonicSolid(boolean showPlatonicSolid) {
		this.showPlatonicSolid = showPlatonicSolid;
	}
	
//	public boolean isShowPyramidalCaps() {
//		return showPyramidalCaps;
//	}
//
//	public void setShowPyramidalCaps(boolean showPyramidalCaps) {
//		this.showPyramidalCaps = showPyramidalCaps;
//	}

	public boolean isShowLensEdges() {
		return showLensEdges;
	}

	public void setShowLensEdges(boolean showLensEdges) {
		this.showLensEdges = showLensEdges;
	}

	
	/**
	 * add the lenses that for the "Platonic lens"
	 */
	private void populateLenses()
	{
		// we need one lens per edge of the Platonic solid;
		// the vertices of the (triangular) lens are the vertices of the edge and the centre of the Platonic solid
		for(int i=0; i<platonicSolid.getNumberOfEdges(); i++)
		{
			// collect the vertices of the triangle that is the aperture of the lens
			Vector3D[] triangleVertices = new Vector3D[3];
			triangleVertices[0] = platonicSolid.getCentre();
			triangleVertices[1] = platonicSolid.getVertex(platonicSolid.getEdge(i)[0]);
			triangleVertices[2] = platonicSolid.getVertex(platonicSolid.getEdge(i)[1]);
			
			Vector3D normal = Vector3D.crossProduct(
					Vector3D.difference(triangleVertices[1], triangleVertices[0]),
					Vector3D.difference(triangleVertices[2], triangleVertices[0])
				);
			
			lenses.addSceneObject(
					new EditableParametrisedConvexPolygon(
							"Lens "+(i+1),	// description,
					normal,	// normalToPlane,
					triangleVertices,	// vertices[],
					new GlensSurface(
							normal,	// opticalAxisDirectionPos
							platonicSolid.getCentre(),	// principalPoint
							-focalLength,	// focalLengthNeg
							focalLength,	// focalLengthPos
							lensTransmissionCoefficient,	// transmissionCoefficient
							lensShadowThrowing	// shadowThrowing
							),	// surfaceProperty
					lenses,	// parent
					getStudio()
				));
		}
	}
	
	private EditableSceneObjectCollection innerEdges;

	private void populateInnerEdges()
	{
		for(int i=0; i<platonicSolid.getNumberOfVertices(); i++)
		{
			innerEdges.addSceneObject(new EditableParametrisedCylinder(
				"Edge from centre to vertex #"+(i+1),
				platonicSolid.getCentre(),	// start point
				platonicSolid.getVertex(i),	// end point
				platonicSolid.getEdgeRadius(),	// radius
				platonicSolid.getEdgeSurfaceProperty(),
				innerEdges,
				getStudio()
			));
		}
	}
	
	/**
	 * add the edges and lenses that form little pyramids on each of the faces of the Platonic solid
	 */
	
	// the edges of the lenses in those little pyramids
	private EditableSceneObjectCollection outerEdges;
	
//	// the lenses
//	private EditableSceneObjectCollection outerLenses;
//
//	private void populateOuterLensesAndEdges()
//	{
//		// PlatonicSolidType ps = platonicSolid.getPlatonicSolid();
//		double inRadius = platonicSolid.getInradius();	// inradius of the Platonic solid
//		double h = 2*inRadius;
//		double z1 = -inRadius;
//		double z2 = h*0.25*1./3.;	// h1
//		double z3 = h*2./3.;	// h2
//		double z4 = h;	// h
//		
//
//		// calculate the focal lengths of the lenses;
//		// for identification of the different lens types, see Fig. 7 in 
//		// "Ray-optical transformation optics with ideal thin lenses makes omnidirectional lenses"
//		// J. Courtial and T. Tyc and J. Belin and S. Oxburgh and G. Ferenczi and E. N. Cowie and C. D. White
//		// Opt. Express  26  17872-17888  (2018);
//		// see PlatonicCloakParameters.nb for Jakub's calculation
//
//		double R = platonicSolid.getFaceCircumradius();	// the circumradius of each of the faces of the Platonic solid
//		int M = platonicSolid.getPlatonicSolid().getNumberOfVerticesPerFace();
//		double x0 = R*Math.cos(Math.PI/M);
//		
//		// System.out.println("EditablePlatonicLens::populateOuterLensesAndEdges: z1="+z1+", z2="+z2+", z3="+z3+", z4="+z4+", R="+R+", M="+M+", x0="+x0);
//		
//		double s1 = Math.sqrt(x0*x0 + z1*z1);
//		double s2 = Math.sqrt(x0*x0 + z2*z2);
//		double s3 = Math.sqrt(x0*x0 + z3*z3);
//		double s4 = Math.sqrt(x0*x0 + z4*z4);
//		
//		double fD = 2.*focalLength;		
//		double fC = fD*s1*(z4 - z2)*(z3 - z2)/(s2*(z4 - z1)*(z1 - z3)) - x0*(z2 - z3)*(z2 - z1)/(s2*(z3 - z1));
//		double fB = -fD*s1*(z4 - z3)*(z3 - z2)/(s3*(z4 - z1)*(z1 - z2));
//		double fA = fD*s1*(z4 - z3)*(z4 - z2)/(s4*(z3 - z1)*(z1 - z2)) + x0*(z4 - z3)*(z4 - z1)/(s4*(z3 - z1));
//		double fF = -fA/(2.*(Math.tan(Math.PI/M)*z4)/Math.sqrt(R*R + MyMath.square(z4/Math.cos(Math.PI/M))));
//		double fE = fF*(z2 - z3)/(z4 - z3);
//		
//		// System.out.println("EditablePlatonicLens::populateOuterLensesAndEdges: fA="+fA+", fB="+fB+", fC="+fC+", fD="+fD+", fE="+fE+", fF="+fF);
//		
//		// we need to construct one pyramid on each face, so go through all the faces...
//		for(int i=0; i<platonicSolid.getNumberOfFaces(); i++)
//		{
//			// first create the vertices
//			Vector3D[] faceVertices = platonicSolid.getVerticesForFace(i);// the vertices of the face
//			Vector3D faceCentre = new Vector3D(0, 0, 0);
//			for(int j=0; j<faceVertices.length; j++) faceCentre = Vector3D.sum(faceCentre, faceVertices[j]);
//			faceCentre = faceCentre.getProductWith(1./faceVertices.length);
//			Vector3D faceNormal = platonicSolid.getOutwardFaceNormal(i);
//			
//			// see Fig. 4 in Ray-optical transformation optics with ideal thin lenses makes omnidirectional lenses
//			// J. Courtial and T. Tyc and J. Belin and S. Oxburgh and G. Ferenczi and E. N. Cowie and C. D. White
//			// Opt. Express  26  17872-17888  (2018)
//			Vector3D p1 = Vector3D.sum(faceCentre, faceNormal.getProductWith(z2)); // lower inner vertex, v4
//			Vector3D p2 = Vector3D.sum(faceCentre, faceNormal.getProductWith(z3));	// upper inner vertex, v5
//			Vector3D p3 = Vector3D.sum(faceCentre, faceNormal.getProductWith(z4));	// top vertex, v6
//			
//			outerEdges.addSceneObject(new EditableScaledParametrisedSphere(
//					"Face #"+i+"'s pyramid's lower inner vertex",
//					p1,	// centre
//					platonicSolid.getVertexRadius(),	// radius
//					platonicSolid.getVertexSurfaceProperty(),
//					outerEdges,
//					getStudio()
//				));
//
//			outerEdges.addSceneObject(new EditableScaledParametrisedSphere(
//					"Face #"+i+"'s pyramid's upper inner vertex",
//					p2,	// centre
//					platonicSolid.getVertexRadius(),	// radius
//					platonicSolid.getVertexSurfaceProperty(),
//					outerEdges,
//					getStudio()
//				));
//
//			outerEdges.addSceneObject(new EditableScaledParametrisedSphere(
//					"Face #"+i+"'s pyramid's top vertex",
//					p3,	// centre
//					platonicSolid.getVertexRadius(),	// radius
//					platonicSolid.getVertexSurfaceProperty(),
//					outerEdges,
//					getStudio()
//				));
//			
//			outerEdges.addSceneObject(new EditableParametrisedCylinder(
//					"Face #"+i+"'s pyramid's lower inner vertex to upper inner vertex",
//					p1,	// start point
//					p2,	// end point
//					platonicSolid.getEdgeRadius(),	// radius
//					platonicSolid.getEdgeSurfaceProperty(),
//					outerEdges,
//					getStudio()
//					));
//
//			outerEdges.addSceneObject(new EditableParametrisedCylinder(
//					"Face #"+i+"'s pyramid's upper inner vertex to top vertex",
//					p2,	// start point
//					p3,	// end point
//					platonicSolid.getEdgeRadius(),	// radius
//					platonicSolid.getEdgeSurfaceProperty(),
//					outerEdges,
//					getStudio()
//					));
//
//			for(int j=0; j<faceVertices.length; j++)
//			{
//				outerEdges.addSceneObject(new EditableParametrisedCylinder(
//						"Edge from face #"+i+"'s vertex #"+j+" to pyramid's lower inner vertex",
//						faceVertices[j],	// start point
//						p1,	// end point
//						platonicSolid.getEdgeRadius(),	// radius
//						platonicSolid.getEdgeSurfaceProperty(),
//						outerEdges,
//						getStudio()
//						));
//				
//				outerEdges.addSceneObject(new EditableParametrisedCylinder(
//						"Edge from face #"+i+"'s vertex #"+j+" to pyramid's upper inner vertex",
//						faceVertices[j],	// start point
//						p2,	// end point
//						platonicSolid.getEdgeRadius(),	// radius
//						platonicSolid.getEdgeSurfaceProperty(),
//						outerEdges,
//						getStudio()
//						));
//				
//				outerEdges.addSceneObject(new EditableParametrisedCylinder(
//						"Edge from face #"+i+"'s vertex #"+j+" to pyramid's top vertex",
//						faceVertices[j],	// start point
//						p3,	// end point
//						platonicSolid.getEdgeRadius(),	// radius
//						platonicSolid.getEdgeSurfaceProperty(),
//						outerEdges,
//						getStudio()
//						));
//			}
//
//			//
//			// lenses
//			//
//			
//			Vector3D[] triangleVertices;	// will describe the triangular aperture of the lens
//
//			// one lens of each type per face vertex
//			for(int j=0; j<faceVertices.length; j++)
//			{
//				// lens type E
//				triangleVertices = new Vector3D[3];
//				triangleVertices[0] = faceVertices[j];
//				triangleVertices[1] = p2;	// upper inner vertex
//				triangleVertices[2] = p1;	// lower inner vertex
//				
//				Vector3D normal = Vector3D.crossProduct(
//						Vector3D.difference(triangleVertices[1], triangleVertices[0]),
//						Vector3D.difference(triangleVertices[2], triangleVertices[0])
//					);
//				
//				outerLenses.addSceneObject(
//						new EditableParametrisedConvexPolygon(
//								"Face #"+i+", Lens of type E #"+j,	// description,
//						normal,	// normalToPlane,
//						triangleVertices,	// vertices[],
//						new GlensSurface(
//								normal,	// opticalAxisDirectionPos
//								p1,	// principalPoint
//								-fE,	// focalLengthNeg
//								fE,	// focalLengthPos
//								lensTransmissionCoefficient,	// transmissionCoefficient
//								true	// shadowThrowing
//								),	// surfaceProperty
//						outerLenses,	// parent
//						getStudio()
//					));
//
//				// lens type F
//				triangleVertices = new Vector3D[3];
//				triangleVertices[0] = faceVertices[j];
//				triangleVertices[1] = p3;	// top vertex
//				triangleVertices[2] = p2;	// upper inner vertex
//				
//				// normal is the same as for type E
////				normal = Vector3D.crossProduct(
////						Vector3D.difference(triangleVertices[1], triangleVertices[0]),
////						Vector3D.difference(triangleVertices[2], triangleVertices[0])
////					);
//
//				outerLenses.addSceneObject(
//						new EditableParametrisedConvexPolygon(
//								"Face #"+i+", Lens of type F #"+j,	// description,
//						normal,	// normalToPlane,
//						triangleVertices,	// vertices[],
//						new GlensSurface(
//								normal,	// opticalAxisDirectionPos
//								p3,	// principalPoint
//								-fF,	// focalLengthNeg
//								fF,	// focalLengthPos
//								lensTransmissionCoefficient,	// transmissionCoefficient
//								true	// shadowThrowing
//								),	// surfaceProperty
//						outerLenses,	// parent
//						getStudio()
//					));
//				
//				// lens type A
//				triangleVertices = new Vector3D[3];
//				triangleVertices[0] = faceVertices[j];
//				triangleVertices[1] = faceVertices[(j+1) % faceVertices.length];
//				triangleVertices[2] = p3;	// top vertex
//
//				normal = Vector3D.crossProduct(
//						Vector3D.difference(triangleVertices[1], triangleVertices[0]),
//						Vector3D.difference(triangleVertices[2], triangleVertices[0])
//					);
//
//				outerLenses.addSceneObject(
//						new EditableParametrisedConvexPolygon(
//								"Face #"+i+", Lens of type A #"+j,	// description,
//						normal,	// normalToPlane,
//						triangleVertices,	// vertices[],
//						new GlensSurface(
//								normal,	// opticalAxisDirectionPos
//								p3,	// principalPoint
//								-fA,	// focalLengthNeg
//								fA,	// focalLengthPos
//								lensTransmissionCoefficient,	// transmissionCoefficient
//								true	// shadowThrowing
//								),	// surfaceProperty
//						outerLenses,	// parent
//						getStudio()
//					));
//				
//				// lens type B
//				triangleVertices = new Vector3D[3];
//				triangleVertices[0] = faceVertices[j];
//				triangleVertices[1] = faceVertices[(j+1) % faceVertices.length];
//				triangleVertices[2] = p2;	// upper inner vertex
//
//				normal = Vector3D.crossProduct(
//						Vector3D.difference(triangleVertices[1], triangleVertices[0]),
//						Vector3D.difference(triangleVertices[2], triangleVertices[0])
//					);
//
//				outerLenses.addSceneObject(
//						new EditableParametrisedConvexPolygon(
//								"Face #"+i+", Lens of type B #"+j,	// description,
//						normal,	// normalToPlane,
//						triangleVertices,	// vertices[],
//						new GlensSurface(
//								normal,	// opticalAxisDirectionPos
//								p2,	// principalPoint
//								-fB,	// focalLengthNeg
//								fB,	// focalLengthPos
//								lensTransmissionCoefficient,	// transmissionCoefficient
//								true	// shadowThrowing
//								),	// surfaceProperty
//						outerLenses,	// parent
//						getStudio()
//					));
//				
//				// lens type C
//				triangleVertices = new Vector3D[3];
//				triangleVertices[0] = faceVertices[j];
//				triangleVertices[1] = faceVertices[(j+1) % faceVertices.length];
//				triangleVertices[2] = p1;	// lower inner vertex
//
//				normal = Vector3D.crossProduct(
//						Vector3D.difference(triangleVertices[1], triangleVertices[0]),
//						Vector3D.difference(triangleVertices[2], triangleVertices[0])
//					);
//
//				outerLenses.addSceneObject(
//						new EditableParametrisedConvexPolygon(
//								"Face #"+i+", Lens of type C #"+j,	// description,
//						normal,	// normalToPlane,
//						triangleVertices,	// vertices[],
//						new GlensSurface(
//								normal,	// opticalAxisDirectionPos
//								p1,	// principalPoint
//								-fC,	// focalLengthNeg
//								fC,	// focalLengthPos
//								lensTransmissionCoefficient,	// transmissionCoefficient
//								true	// shadowThrowing
//								),	// surfaceProperty
//						outerLenses,	// parent
//						getStudio()
//					));				
//			}			
//		}
//	}

	

	public void populateSceneObjectCollection()
	{
		// clear out anything that's already in here before adding the objects (again)
		clear();
		
		// prepare scene-object collection objects for the lenses and inner edges...
		lenses = new EditableSceneObjectCollection("Lenses", true, this, getStudio());
		innerEdges = new EditableSceneObjectCollection("Inner edges", true, this, getStudio());
		outerEdges = new EditableSceneObjectCollection("Outer edges", true, this, getStudio());
//		outerLenses = new EditableSceneObjectCollection("Outer pyramid", true, this, getStudio());
		
		// populate these collections
		populateLenses();
		populateInnerEdges();
		// populateOuterLensesAndEdges();
		
		// add the windows and the frames to this collection
		addSceneObject(lenses);
		addSceneObject(innerEdges, showLensEdges);
		addSceneObject(outerEdges, showLensEdges);	// showPyramidalCaps && showLensEdges);
		// addSceneObject(outerLenses, showPyramidalCaps);
		
		addSceneObject(platonicSolid, showPlatonicSolid);
	}
	
	
	
	// GUI panels
	private LabelledDoublePanel focalLengthPanel, lensTransmissionCoefficientPanel;
	private JButton convertButton, editPlatonicSolidButton;
	private JCheckBox 
		showPlatonicSolidCheckBox, 
		// showPyramidalCapsCheckBox, 
		lensShadowThrowingCheckBox,
		showLensEdgesCheckBox;

	


	/**
	 * initialise the edit panel
	 */
	@Override
	public void createEditPanel(IPanel iPanel)
	{	
		this.iPanel = iPanel;
		
		editPanel = new JPanel();
		editPanel.setBorder(GUIBitsAndBobs.getTitledBorder("Star of lenses"));
		editPanel.setLayout(new MigLayout("insets 0"));
		

		//
		// the basic-parameters panel
		// 
		
		JPanel basicParametersPanel = new JPanel();
		basicParametersPanel.setLayout(new MigLayout("insets 0"));

		// a text field containing the description
		descriptionPanel = new LabelledStringPanel("Description");
		basicParametersPanel.add(descriptionPanel, "wrap");
		
		editPlatonicSolidButton = new JButton("Edit Platonic solid");
		editPlatonicSolidButton.addActionListener(this);
		basicParametersPanel.add(editPlatonicSolidButton, "wrap");

		showPlatonicSolidCheckBox = new JCheckBox("Show Platonic solid");
		basicParametersPanel.add(showPlatonicSolidCheckBox, "wrap");
		
//		showPyramidalCapsCheckBox = new JCheckBox("Show pyramidal TO caps on faces");
//		basicParametersPanel.add(showPyramidalCapsCheckBox, "wrap");

		showLensEdgesCheckBox = new JCheckBox("Show inner edges (from vertices to centre)");
		basicParametersPanel.add(showLensEdgesCheckBox, "wrap");

		focalLengthPanel = new LabelledDoublePanel("Focal length of each lens");
		basicParametersPanel.add(focalLengthPanel, "wrap");

		lensTransmissionCoefficientPanel = new LabelledDoublePanel("Transmission coefficient of each lens");
		basicParametersPanel.add(lensTransmissionCoefficientPanel, "wrap");

		lensShadowThrowingCheckBox = new JCheckBox("Show lens shadows");
		basicParametersPanel.add(lensShadowThrowingCheckBox, "wrap");
		
		editPanel.add(basicParametersPanel, "wrap");


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
		focalLengthPanel.setNumber(focalLength);
		lensTransmissionCoefficientPanel.setNumber(lensTransmissionCoefficient);
		lensShadowThrowingCheckBox.setSelected(lensShadowThrowing);
		showPlatonicSolidCheckBox.setSelected(showPlatonicSolid);
		// showPyramidalCapsCheckBox.setSelected(showPyramidalCaps);
		showLensEdgesCheckBox.setSelected(showLensEdges);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.GUI.Editable#acceptValuesInEditPanel()
	 */
	@Override
	public EditablePlatonicLens acceptValuesInEditPanel()
	{
		setDescription(descriptionPanel.getString());
		setFocalLength(focalLengthPanel.getNumber());
		setLensTransmissionCoefficient(lensTransmissionCoefficientPanel.getNumber());
		setLensShadowThrowing(lensShadowThrowingCheckBox.isSelected());
		setShowPlatonicSolid(showPlatonicSolidCheckBox.isSelected());
		// setShowPyramidalCaps(showPyramidalCapsCheckBox.isSelected());
		setShowLensEdges(showLensEdgesCheckBox.isSelected());

		// add the objects
		populateSceneObjectCollection();
		
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(convertButton))
		{
			acceptValuesInEditPanel();	// accept any changes
			EditableSceneObjectCollection container = new EditableSceneObjectCollection(this);
			iPanel.replaceFrontComponent(container, "Edit ex-Platonic-lens");
			container.setValuesInEditPanel();
		}
		else if(e.getSource().equals(editPlatonicSolidButton))
		{
			iPanel.addFrontComponent(platonicSolid, "Platonic solid");
			platonicSolid.setValuesInEditPanel();
		}
	}

	@Override
	public void backToFront(IPanelComponent edited)
	{
			if(edited instanceof EditablePlatonicSolid)
			{
				// frame surface property has been edited
				setPlatonicSolid((EditablePlatonicSolid)edited);
			}
	}
}