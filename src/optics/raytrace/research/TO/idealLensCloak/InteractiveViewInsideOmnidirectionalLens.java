package optics.raytrace.research.TO.idealLensCloak;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;

import math.*;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.core.*;
import optics.raytrace.exceptions.SceneException;
import optics.DoubleColour;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeComboBox;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.LabelledComponent;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LensElementType;
import optics.raytrace.GUI.sceneObjects.EditableArrow;
import optics.raytrace.GUI.sceneObjects.EditableCylinderLattice;
import optics.raytrace.GUI.sceneObjects.EditableIdealLensCloak;


/**
 * Simulation of a combination of three skew lenses.
 * One of the aims is to use this to understand how the view out of the ideal-lens cloak / 4 pi lens can rotate the view!?
 * 
 * @author Johannes Courtial
 */
public class InteractiveViewInsideOmnidirectionalLens extends NonInteractiveTIMEngine
{
	// additional parameters
	
	protected double hCOverF;	// H_C_OVER_F = 0.1;
	protected double hCOverH1;	// H_C_OVER_H_1 = 0.9;
	protected boolean showEdges;	// SHOW_EDGES = true;
	protected double xOffset;	// X_OFFSET = 0.0;
	protected double yOffset;	// Y_OFFSET = -0.05;
	protected boolean showCloak;
	protected boolean lookAtCloak;	// LOOK_AT_CLOAK = false;
	protected double cameraViewDirectionPhi, cameraViewDirectionTheta;
		
	/**
	 * Allows selection of the simulated lens type
	 */
	// protected LensType lensType;
	
	
	// internal variables
	

	// GUI panels
	
	private LabelledDoublePanel hCOverFPanel, hCOverH1Panel, xOffsetPanel, yOffsetPanel, cameraFocussingDistancePanel, cameraHorizontalFOVPanel, cameraViewDirectionPhiPanel, cameraViewDirectionThetaPanel;
	private JCheckBox showEdgesCheckBox, showCloakCheckBox, lookAtCloakCheckBox;
	private ApertureSizeComboBox cameraApertureSizeComboBox;
	// private JComboBox<LensType> lensTypeComboBox;

	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public InteractiveViewInsideOmnidirectionalLens()
	{
		// set all standard parameters, including those for the camera
		super();
		
		// set all parameters
		hCOverF = 0.1;	// H_C_OVER_F = 0.1;
		hCOverH1 = 0.9;	// H_C_OVER_H_1 = 0.9;
		cameraHorizontalFOVDeg = 90;	// HORIZONTAL_ANGLE_OF_VIEW = 90;
		cameraViewDirectionPhi = 90;
		cameraViewDirectionTheta = 0;
		showEdges = true;	// SHOW_EDGES = true;
		xOffset = 0.0;	// X_OFFSET = 0.0;
		yOffset = -0.05;	// Y_OFFSET = -0.05;
		lookAtCloak = false;	// LOOK_AT_CLOAK = false;
		showCloak = true;
		// lensType = LensType.IDEAL_THIN_LENS;
		
		renderQuality = RenderQualityEnum.DRAFT;

		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;
		cameraFocussingDistance = 15;
		cameraApertureSize = ApertureSizeType.PINHOLE;
	}
	
	private void calculateCameraViewDirectionFromDirectionAngle()
	{
		double
			theta = MyMath.deg2rad(cameraViewDirectionTheta),
			phi = MyMath.deg2rad(cameraViewDirectionPhi),
			sinTheta = Math.sin(theta),
			cosTheta = Math.cos(theta),
			sinPhi = Math.sin(phi),
			cosPhi = Math.cos(phi);
		cameraViewDirection = new Vector3D(cosTheta*sinPhi, sinTheta, cosTheta*cosPhi);
	}

	@Override
	public String getClassName()
	{
		return "InteractiveViewInsideOmnidirectionalLens "	// the name
				+ " hCOverF "+hCOverF+"°"
				+ " hCOverH1 "+hCOverH1+"°"
				+ " cameraHorizontalFOVDeg "+cameraHorizontalFOVDeg+"°"
				+ " cameraViewDirectionPhi "+cameraViewDirectionPhi
				+ " cameraViewDirectiontaThe "+cameraViewDirectionTheta
				+ " xOffset "+xOffset
				+ " yOffset "+yOffset
				// + " lens type "+lensType
				+ (showEdges?" (edges shown)":"")
				+ ((cameraApertureSize != ApertureSizeType.PINHOLE)?" focussing distance "+cameraFocussingDistance:"")
				+ " aperture size "+cameraApertureSize
				;
	}
		
	@Override
	public void populateStudio()
	throws SceneException
	{
		super.populateSimpleStudio();

		studio = new Studio();

		// the scene
		SceneObjectContainer scene = new SceneObjectContainer("the scene", null, studio);
		studio.setScene(scene);

		// from FourPiLensViewFromPointWithinMovieMaker.java
		// the standard scene objects
		scene.addSceneObject(SceneObjectClass.getSkySphere(scene, studio));
		scene.addSceneObject(SceneObjectClass.getChequerboardFloor(-2, scene, studio));
				
		// add any other scene objects
		
		// the cylinder lattice from TIMInteractiveBits's populateSceneRelativisticEdition method
		double cylinderRadius = 0.02;

		// a cylinder lattice...
		scene.addSceneObject(new EditableCylinderLattice(
				"cylinder lattice",
				-2.5, 2.5, 6,	// x_min, x_max, no of cylinders => cylinders at x=+-0.5, +-1.5, +-2.5
				-1.5, 1.5, 4,	// y_min, y_max, no of cylinders => cylinders at y=-1.5, -0.5, +0.5, +1.5
				-2.5, 2.5, 6, // z_min, z_max, no of cylinders => cylinders at z=+-0.5, +-1.5, +-2.5
				cylinderRadius,
				scene,
				studio
				));		

		// The parameter we actually want to give is f and hC/f, the height of the camera above the base
		// *in units of the focal length of the base lens*.
		// The height of the camera has to be less than the height of the lower inner vertex
		// (in physical space), as we want the camera to be in the bottom tetrahedron.
		// The relationship between h1, h1E and f is given by the lens equation:
		// 	1/h1 - 1/h1E = 1/f.
		// Let's put the camera very close to the top of the lower inner tetrahedron:
		// 	hC = (hC/h1) h1,
		// where hC/h1 = 0.99 or something.
		// Then
		// 	(hC/h1)/hC - 1/h1E = 1/f,
		// so
		// 	1/h1E = (hC/h1)/hC - 1/f = ((hC/h1) f - hC) / (f hC) = ((hC/h1) f/hC - 1) / f,
		// and therefore
		// 	h1E = f / [(hC/h1) / (hC/f) - 1].
		// Alternatively, we can calculate
		//  1/h1E = ((hC/h1) f - hC) / (f hC) = ((hC/h1) - hC/f) / hC,
		// and so
		//  h1E = hC / ((hC/h1) - hC/f).
		
		double
			h = 0.5,	// height of the lens cloak
			h1 = h*1./3.,	// height of the lower inner vertex above the base
			h2 = h*2./3.,	// height of the upper inner vertex above the base
			hC = hCOverH1*h1,	// height of the camera above the base
			h1E = hC / (hCOverH1 - hCOverF);	// height of the lower inner vertex above the base, in EM space
		
		System.out.println("h1E="+h1E);
		
		// the ideal-lens cloak
		if(showCloak)
		{
			EditableIdealLensCloak editableIdealLensCloak = 
					new EditableIdealLensCloak(
							"Ideal-lens cloak",
							new Vector3D(xOffset, yOffset, -hC),	// base centre
							new Vector3D(0, 1, 0),	// front direction
							new Vector3D(1, 0, 0),	// right direction
							new Vector3D(0, 0, 1),	// top direction
							h*2./3.,	// base radius; if this is 2/3 of height then tetrahedron is regular
							h,	// height
							h1,	// heightLowerInnerVertexP
							h2,	// heightUpperInnerVertexP
							h1E,	// heightLowerInnerVertexE
							0.96,	// interface transmission coefficient
							showEdges,	// show frames
							0.01*h,	// frame radius
							SurfaceColour.WHITE_SHINY,	// frame surface property
							// LOOK_AT_CLOAK,	// show placeholder surfaces
							(lookAtCloak?LensElementType.GLASS_PANE:LensElementType.IDEAL_THIN_LENS),
							scene,
							studio
							);
			scene.addSceneObject(editableIdealLensCloak);
		}
		
		studio.setLights(LightSource.getStandardLightsFromBehind());
		

		if(lookAtCloak)
		{
			// add a little sphere at the position where the camera will be otherwise located
			scene.addSceneObject(
					new EditableArrow(
							"Arrow pointing at camera position",	// description
							new Vector3D(0, h, 0),	// startPoint
							new Vector3D(0, 0, 0),	// endPoint
							0.02*h,	// shaftRadius
							0.1*h,	// tipLength
							MyMath.deg2rad(30),	// tip angle
							new SurfaceColour(DoubleColour.YELLOW_SUPERBRIGHT, DoubleColour.WHITE, true),	// surfaceProperty
							scene, 
							studio
							)
					//						new EditableScaledParametrisedSphere(
					//								"Sphere centred at camera position",	// description
					//								new Vector3D(0, 0, 0),	// centre
					//								0.05*h,	// radius
					//								SurfaceColour.WHITER_SHINY,	// surfaceProperty
					//								scene, 
					//								studio
					//							)
					);

			calculateCameraViewDirectionFromDirectionAngle();
			cameraViewCentre = new Vector3D(0, 0, 0);
			// cameraViewDirection = new Vector3D(-.5, -.06, -.04);
			cameraDistance = 5; // (new Vector3D(5, .6, .4)).getLength();
		}
		else
		{
			calculateCameraViewDirectionFromDirectionAngle();
			cameraDistance = 10;
			cameraViewCentre = Vector3D.sum(
					new Vector3D(0, 0, 0),	// aperture centre
					cameraViewDirection.getWithLength(cameraDistance)
				);
		}
		studio.setCamera(getStandardCamera());
	}

	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		hCOverFPanel = new LabelledDoublePanel("h_C/f");
		hCOverFPanel.setNumber(hCOverF);
		interactiveControlPanel.add(hCOverFPanel, "span");

		hCOverH1Panel = new LabelledDoublePanel("h_C/h_1");
		hCOverH1Panel.setNumber(hCOverH1);
		interactiveControlPanel.add(hCOverH1Panel, "span");

		xOffsetPanel = new LabelledDoublePanel("x offset");
		xOffsetPanel.setNumber(xOffset);
		interactiveControlPanel.add(xOffsetPanel, "span");

		yOffsetPanel = new LabelledDoublePanel("y offset");
		yOffsetPanel.setNumber(yOffset);
		interactiveControlPanel.add(yOffsetPanel, "span");

		cameraViewDirectionPhiPanel = new LabelledDoublePanel("view-direction phi (degree)");
		cameraViewDirectionPhiPanel.setNumber(cameraViewDirectionPhi);
		interactiveControlPanel.add(cameraViewDirectionPhiPanel, "span");
		
		cameraViewDirectionThetaPanel = new LabelledDoublePanel("view-direction theta (degree)");
		cameraViewDirectionThetaPanel.setNumber(cameraViewDirectionTheta);
		interactiveControlPanel.add(cameraViewDirectionThetaPanel, "span");
		
		cameraHorizontalFOVPanel = new LabelledDoublePanel("horizontal FOV (degree)");
		cameraHorizontalFOVPanel.setNumber(cameraHorizontalFOVDeg);
		interactiveControlPanel.add(cameraHorizontalFOVPanel, "span");
		
		cameraFocussingDistancePanel = new LabelledDoublePanel("focussing distance");
		cameraFocussingDistancePanel.setNumber(cameraFocussingDistance);
		interactiveControlPanel.add(cameraFocussingDistancePanel, "span");
		
		cameraApertureSizeComboBox = new ApertureSizeComboBox();
		cameraApertureSizeComboBox.setApertureSize(cameraApertureSize);
		interactiveControlPanel.add(new LabelledComponent("Camera aperture", cameraApertureSizeComboBox), "span");
				
//		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
//		lensTypeComboBox.setSelectedItem(lensType);
//		interactiveControlPanel.add(new LabelledComponent("Lens type", lensTypeComboBox), "span");

		showEdgesCheckBox = new JCheckBox("Show cloak edges");
		showEdgesCheckBox.setSelected(showEdges);
		interactiveControlPanel.add(showEdgesCheckBox, "span");

		showCloakCheckBox = new JCheckBox("Show cloak");
		showCloakCheckBox.setSelected(showCloak);
		interactiveControlPanel.add(showCloakCheckBox, "span");

		lookAtCloakCheckBox = new JCheckBox("look at cloak from outside");
		lookAtCloakCheckBox.setSelected(lookAtCloak);
		interactiveControlPanel.add(lookAtCloakCheckBox, "span");
	}
	
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		hCOverF = hCOverFPanel.getNumber();
		hCOverH1 = hCOverH1Panel.getNumber();
		xOffset = xOffsetPanel.getNumber();
		yOffset = yOffsetPanel.getNumber();
		cameraViewDirectionPhi = cameraViewDirectionPhiPanel.getNumber();
		cameraViewDirectionTheta = cameraViewDirectionThetaPanel.getNumber();
		cameraHorizontalFOVDeg = cameraHorizontalFOVPanel.getNumber();
		cameraFocussingDistance = cameraFocussingDistancePanel.getNumber();
		cameraApertureSize = cameraApertureSizeComboBox.getApertureSize();
		// lensType = (LensType)(lensTypeComboBox.getSelectedItem());
		showCloak = showCloakCheckBox.isSelected();
		showEdges = showEdgesCheckBox.isSelected();
		lookAtCloak = lookAtCloakCheckBox.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
				
//		if(e.getSource().equals(testCheckBox))
//		{
//			setTest(testCheckBox.isSelected());
//			// render();
//		}
	}


	public static void main(final String[] args)
	{
		(new InteractiveViewInsideOmnidirectionalLens()).run();
	}
}
