package optics.raytrace.GUI.surfaces;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import math.Complex;
import math.Vector2D;
import math.Vector3D;
import optics.DoubleColour;
import optics.raytrace.core.SceneObject;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.core.SurfacePropertyWithControllableShadow;
import optics.raytrace.surfaces.ColourFilter;
import optics.raytrace.surfaces.ConfocalLensletArrays;
import optics.raytrace.surfaces.GalileoTransformInterface;
import optics.raytrace.surfaces.GCLAsWithApertures;
import optics.raytrace.surfaces.GCLAsWithApertures.GCLAsTransmissionCoefficientCalculationMethodType;
import optics.raytrace.surfaces.GlensSurface;
import optics.raytrace.surfaces.IdealThinLensSurface;
import optics.raytrace.surfaces.EatonLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.LorentzTransformInterface;
import optics.raytrace.surfaces.LuneburgLensSurfaceAngleFormulation;
import optics.raytrace.surfaces.MetricInterface;
import optics.raytrace.surfaces.MetricInterface.RefractionType;
import optics.raytrace.surfaces.PhaseConjugating;
import optics.raytrace.surfaces.PhaseHologramOfCylindricalLens;
import optics.raytrace.surfaces.PhaseHologramOfRadialLenticularArray;
import optics.raytrace.surfaces.Pixellation;
import optics.raytrace.surfaces.Point2PointImagingPhaseHologram;
import optics.raytrace.surfaces.Rainbow;
import optics.raytrace.surfaces.RayFlipping;
import optics.raytrace.surfaces.RayRotating;
import optics.raytrace.surfaces.RayRotatingAboutArbitraryAxisDirection;
import optics.raytrace.surfaces.RectangularIdealThinLensletArray;
import optics.raytrace.surfaces.Reflective;
import optics.raytrace.surfaces.Refractive;
import optics.raytrace.surfaces.RefractiveComplex;
import optics.raytrace.surfaces.RotationallySymmetricPhaseHologram;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceColourLightSourceIndependent;
import optics.raytrace.surfaces.SurfaceColourTimeDependent;
import optics.raytrace.surfaces.Teleporting;
import optics.raytrace.surfaces.Teleporting.TeleportationType;
import optics.raytrace.surfaces.Transparent;
import optics.raytrace.utility.Coordinates.CoordinateSystemType;
import optics.raytrace.utility.Coordinates.GlobalOrLocalCoordinateSystemType;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.DoublePanel;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComplexPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoubleColourPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledScientificDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledSymmetricMatrix3DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector2DPanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.GUI.lowLevel.Vector3DPanel;

/**
 * Allows choice of a surface property.
 */
public class SurfacePropertyPanelNew extends JPanel implements ActionListener
{
	
	// the options that come up in the combo box for selecting the class of surface property
	public enum EditableSurfacePropertyType
	{
		COLOURED("Coloured");
//		COLOUR_FILTER("Colour filter"),
//		COLOURED_GLOWING("Coloured (glowing)"),
//		COLOURED_TIME_DEPENDENT("Coloured (time-dependent)"),
//		CONFOCAL_LENSLET_ARRAYS("Confocal lenslet arrays"),
//		CYLINDRICAL_LENS_HOLOGRAM("Cylindrical-lens hologram"),
//		EATON_LENS("Eaton-lens surface"),
//		FLIPPING("Flipping"),
//		GALILEO_TRANSFORM_INTERFACE("Galileo-transform interface"),
//		GENERALISED_CONFOCAL_LENSLET_ARRAYS("Generalised confocal lenslet arrays"),
//		GLENS_HOLOGRAM("Glens hologram"),
//		IDEAL_THIN_LENS_SURFACE("Ideal-thin-lens hologram"),
//		LENSLET_ARRAY("Lenslet-array"),
//		LORENTZ_TRANSFORM_INTERFACE("Lorentz-transform interface"),
//		LUNEBURG_LENS("Luneburg-lens surface"),
//		METRIC_INTERFACE("Metric interface"),
//		PHASE_CONJUGATING("Phase-conjugating"),
//		PICTURE("Picture"),
//		PIXELLATION("Pixellation"),
//		POINT2POINT_IMAGING("Point-to-point imaging hologram"),
//		RADIAL_LENTICULAR_ARRAY("Radial lenticular array"),
//		RAINBOW("Rainbow"),
//		REFLECTIVE("Reflective"),
//		REFRACTIVE("Refractive"),
//		REFRACTIVE_COMPLEX("Refractive (complex)"),
//		ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM("Rotationally symmetric phase hologram"),
//		ROTATING("Rotating"),
//		ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION("Rotating around arbitrary axis direction"),
//		TELEPORTING("Teleporting"),
//		TILED("Tiled"),
//		TRANSPARENT("Transparent"),
//		TWO_SIDED("Two-sided");
		
		private Class<EditableSurfaceProperty> editableSurfacePropertyClass;
		private EditableSurfacePropertyType(Class<EditableSurfaceProperty> editableSurfacePropertyClass) {this.editableSurfacePropertyClass = editableSurfacePropertyClass;}	
		@Override
		public String toString() {return EditableSurfaceProperty.description;}
	}

	//
	// GUI components
	//
	
	private SurfacePropertyComboBox surfacePropertyComboBox;
	
	private JPanel optionalParametersPanel;

	private JCheckBox shadowThrowingCheckBox;
	private IPanel iPanel;
	
	//
	// other internal variables
	//
	
	private SceneObject scene;
	

	/**
	 * @param sceneObject	the scene object the surface property is associated with
	 * @param scene	the scene object(s) to which Teleporting can teleport
	 */
	public SurfacePropertyPanelNew(String description, boolean showFrame, SceneObject scene)
	{
		super();
		setScene(scene);
		
		// this.iPanel = iPanel;
		
		setLayout(new MigLayout("insets 0"));
		if(showFrame) setBorder(GUIBitsAndBobs.getTitledBorder(description));

		surfacePropertyComboBox = new SurfacePropertyComboBox();
		add(GUIBitsAndBobs.makeRow("Surface type", surfacePropertyComboBox), "wrap");

		shadowThrowingCheckBox = new JCheckBox("Shadow-throwing");
		shadowThrowingCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(shadowThrowingCheckBox, "wrap");

		optionalParametersPanel = new JPanel();
		add(optionalParametersPanel);
		
		// setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
		
		// setPreferredSize(new Dimension(400, 140));
		
		validate();
	}

	public SurfacePropertyPanelNew(String description, SceneObject scene)
	{
		this(description, true, scene);
	}

	public SurfacePropertyPanelNew(SceneObject scene)
	{
		this("Surface", scene);
	}
	
	
	//
	// getters & setters
	//
	
	public void setIPanel(IPanel iPanel)
	{
		this.iPanel = iPanel;
	}

	private void setOptionalParameterPanelComponent(Component newComponent)
	{
		// remove any component currently in the optional-parameter panel
		while(optionalParametersPanel.getComponentCount() > 0) optionalParametersPanel.remove(0);

		// now add the new component
		if(newComponent != null)
		{
			optionalParametersPanel.add(newComponent);
			optionalParametersPanel.revalidate();
		}

		if(iPanel != null)
		{
			// validate the enclosing panel
			iPanel.mainPanelChanged();
		}
	}
	
	public void setSurfaceProperty(EditableSurfaceProperty surfaceProperty)
	{
		for(EditableSurfacePropertyType editableSurfacePropertyType : EditableSurfacePropertyType.values())
		{
			
		}
		surfaceTiling = new EditableSurfaceTiling(SurfaceColour.GREY50_SHINY, SurfaceColour.WHITE_SHINY, 1, 1, getScene());
		teleportingParametersLine.refreshSceneObjectPrimitivesList();
		pictureSurface = new EditablePictureSurfaceDiffuse((File)null, false, 0, 1, 0, 1);
		twoSidedSurface = new EditableTwoSidedSurface(SurfaceColour.BLACK_MATT, SurfaceColour.WHITE_MATT, getScene());
		
		if(surfaceProperty instanceof SurfacePropertyWithControllableShadow)
		{
			shadowThrowingCheckBox.setVisible(true);
			shadowThrowingCheckBox.setSelected(((SurfacePropertyWithControllableShadow)surfaceProperty).isShadowThrowing());
		}
		else
		{
			shadowThrowingCheckBox.setVisible(false);
		}

		if(surfaceProperty instanceof SurfaceColour)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED);
			colourPanel.setDoubleColour(((SurfaceColour)surfaceProperty).getDiffuseColour());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof ColourFilter)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOUR_FILTER);
			colourPanel.setDoubleColour(((ColourFilter)surfaceProperty).getRgbTransmissionCoefficients());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof SurfaceColourLightSourceIndependent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED_GLOWING);
			colourPanel.setDoubleColour(((SurfaceColourLightSourceIndependent)surfaceProperty).getColour());
			setOptionalParameterPanelComponent(colourPanel);
		}
		else if(surfaceProperty instanceof SurfaceColourTimeDependent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.COLOURED_TIME_DEPENDENT);
			surfaceColourTimeDependentPanel.setNumber(((SurfaceColourTimeDependent)surfaceProperty).getPeriod());
			setOptionalParameterPanelComponent(surfaceColourTimeDependentPanel);
		}
		else if(surfaceProperty instanceof ConfocalLensletArrays)
		{
			ConfocalLensletArrays c = (ConfocalLensletArrays)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CONFOCAL_LENSLET_ARRAYS);
			CLAsEtaPanel.setNumber(c.getEta());
			CLAsTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(CLAsPanel);
		}
		else if(surfaceProperty instanceof EatonLensSurfaceAngleFormulation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.EATON_LENS);
			// criticalAngleOfIncidencePanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getCriticalAngle()*180/Math.PI);
			transmissionCoefficientPanel.setNumber(((EatonLensSurfaceAngleFormulation)surfaceProperty).getTransmissionCoefficient());
			// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfCylindricalLens)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.CYLINDRICAL_LENS_HOLOGRAM);
			pointOnAxisVector3DPanel.setVector3D(((PhaseHologramOfCylindricalLens)surfaceProperty).getPrincipalPoint());
			phaseGradientDirectionVector3DPanel.setVector3D(((PhaseHologramOfCylindricalLens)surfaceProperty).getPhaseGradientDirection());
			cylindricalLensHologramFocalLengthPanel.setNumber(((PhaseHologramOfCylindricalLens)surfaceProperty).getFocalLength());
			cylindricalLensHologramTransmissionCoefficientPanel.setNumber(((PhaseHologramOfCylindricalLens)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(cylindricalLensHologramPanel);
		}
		else if(surfaceProperty instanceof RayFlipping)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.FLIPPING);
			flipAxisAnglePanel.setNumber(((RayFlipping)surfaceProperty).getFlipAxisAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(flipAxisAnglePanel);
		}
		else if(surfaceProperty instanceof GalileoTransformInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GALILEO_TRANSFORM_INTERFACE);
			betaGalileoVector3DPanel.setVector3D(((GalileoTransformInterface)surfaceProperty).getBeta());
			betaGalileoBasisComboBox.setSelectedItem(((GalileoTransformInterface)surfaceProperty).getBasis());
			transmissionCoefficientGalileoPanel.setNumber(((GalileoTransformInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(galileoTransformInterfacePanel);
		}
		else if(surfaceProperty instanceof GCLAsWithApertures)
		{
			GCLAsWithApertures c = (GCLAsWithApertures)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GENERALISED_CONFOCAL_LENSLET_ARRAYS);
			aVector3DPanel.setVector3D(c.getAHat());
			uVector3DPanel.setVector3D(c.getUHat());
			vVector3DPanel.setVector3D(c.getVHat());
			etaPanel.setVector2D(c.getEtaU(), c.getEtaV());
//			etaUPanel.setNumber(c.getEtaU());
//			etaVPanel.setNumber(c.getEtaV());
			deltaPanel.setVector2D(c.getDeltaU(), c.getDeltaV());
//			deltaUPanel.setNumber(c.getDeltaU());
//			deltaVPanel.setNumber(c.getDeltaV());
			gCLAsBasisComboBox.setSelectedItem(c.getBasis());
			gCLAsConstantTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			gCLAsTransmissionCoefficientMethodComboBox.setSelectedItem(c.getTransmissionCoefficientMethod());
			// gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.setSelected(c.isCalculateGeometricalTransmissionCoefficient());
			gCLAsPixelSideLengthPanel.setNumber(c.getPixelSideLength());
			gCLAsLambdaPanel.setNumber(c.getLambda());
			gCLAsSimulateDiffractiveBlurCheckBox.setSelected(c.isSimulateDiffractiveBlur());
			gCLAsSimulateRayOffsetCheckBox.setSelected(c.isSimulateRayOffset());;

			setOptionalParameterPanelComponent(gCLAsPanel);
		}
		else if(surfaceProperty instanceof GlensSurface)
		{
			GlensSurface c = (GlensSurface)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.GLENS_HOLOGRAM);
			nodalPointFVector3DPanel.setVector3D(c.getNodalPointG());
			pointOnGlensVector3DPanel.setVector3D(c.getPointOnGlens());
			opticalAxisPosVector3DPanel.setVector3D(c.getOpticalAxisDirectionPos());
			meanFPanel.setNumber(c.getG());
			focalLengthNegFPanel.setNumber(c.getFocalLengthNegG());
			focalLengthPosFPanel.setNumber(c.getFocalLengthPosG());
			glensTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(glensHologramPanel);
		}
		else if(surfaceProperty instanceof IdealThinLensSurface)
		{
			IdealThinLensSurface c = (IdealThinLensSurface)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.IDEAL_THIN_LENS_SURFACE);
			opticalAxisVector3DPanel.setVector3D(c.getOpticalAxisDirectionPos());
			principalPointVector3DPanel.setVector3D(c.getPrincipalPoint());
			focalLengthPanel.setNumber(c.getFocalLength());
			lensTransmissionCoefficientPanel.setNumber(c.getTransmissionCoefficient());
			setOptionalParameterPanelComponent(lensHologramPanel);
		}
		else if(surfaceProperty instanceof RectangularIdealThinLensletArray)
		{
			RectangularIdealThinLensletArray la = (RectangularIdealThinLensletArray)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LENSLET_ARRAY);
			lensletArrayFocalLengthPanel.setNumber(la.getFocalLength());
			lensletArrayTransmissionCoefficientPanel.setNumber(la.getTransmissionCoefficient());
			lensletArrayPeriodPanel.setVector2D(la.getxPeriod(), la.getyPeriod());
			lensletArrayOffsetPanel.setVector2D(la.getxOffset(), la.getyOffset());
			setOptionalParameterPanelComponent(lensletArrayPanel);
		}
		else if(surfaceProperty instanceof LorentzTransformInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LORENTZ_TRANSFORM_INTERFACE);
			betaVector3DPanel.setVector3D(((LorentzTransformInterface)surfaceProperty).getBeta());
			betaBasisComboBox.setSelectedItem(((LorentzTransformInterface)surfaceProperty).getBasis());
			transmissionCoefficientPanel.setNumber(((LorentzTransformInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(lorentzTransformInterfacePanel);
		}
		else if(surfaceProperty instanceof LuneburgLensSurfaceAngleFormulation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.LUNEBURG_LENS);
			// criticalAngleOfIncidencePanel.setNumber(((LuneburgLensSurfaceAngleFormulation)surfaceProperty).getCriticalAngle()*180/Math.PI);
			transmissionCoefficientPanel.setNumber(((LuneburgLensSurfaceAngleFormulation)surfaceProperty).getTransmissionCoefficient());
			// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
		}
		else if(surfaceProperty instanceof MetricInterface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.METRIC_INTERFACE);
			insideMetricTensorPanel.setMatrix3D(((MetricInterface)surfaceProperty).getMetricTensorInside());
			outsideMetricTensorPanel.setMatrix3D(((MetricInterface)surfaceProperty).getMetricTensorOutside());
			metricTensorsBasisComboBox.setSelectedItem(((MetricInterface)surfaceProperty).getBasis());
			refractionTypeComboBox.setSelectedItem(((MetricInterface)surfaceProperty).getRefractionType());
			allowImaginaryOpticalPathLengthsCheckBox.setSelected(((MetricInterface)surfaceProperty).isAllowImaginaryOpticalPathLengths());
			transmissionCoefficientPanel.setNumber(((MetricInterface)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(metricInterfacePanel);
		}
		else if(surfaceProperty instanceof PhaseConjugating)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PHASE_CONJUGATING);
			boolean isReflective = (((PhaseConjugating)surfaceProperty).getReflectiveOrTransmissive() == SurfaceProperty.ReflectiveOrTransmissive.REFLECTIVE);
			isPhaseConjugatingSurfaceReflectiveCheckBox.setSelected(isReflective);
			phaseConjugatingSurfaceTransmissionCoefficientPanel.setNumber(((PhaseConjugating)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(phaseConjugatingPanel);
		}
		else if(surfaceProperty instanceof PhaseHologramOfRadialLenticularArray)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.RADIAL_LENTICULAR_ARRAY);
			rlapCentrePanel.setVector3D(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getCentre());
			rlapFPanel.setNumber(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getF());
			rlapNPanel.setNumber(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getN());
			rlapD0Panel.setVector3D(((PhaseHologramOfRadialLenticularArray)surfaceProperty).getD0());
			setOptionalParameterPanelComponent(phaseHologramOfRadialLenticularArrayPanel);
		}
		else if(surfaceProperty instanceof Point2PointImagingPhaseHologram)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.POINT2POINT_IMAGING);
			point2pointImagingPoint1Panel.setVector3D(((Point2PointImagingPhaseHologram)surfaceProperty).getInsideSpacePoint());
			point2pointImagingPoint2Panel.setVector3D(((Point2PointImagingPhaseHologram)surfaceProperty).getOutsideSpacePoint());
			boolean isReflective = ((Point2PointImagingPhaseHologram)surfaceProperty).isReflective();
			point2pointImagingIsReflectiveCheckBox.setSelected(isReflective);
			updatePoint2pointImagingPanel(isReflective);
			setOptionalParameterPanelComponent(point2pointImagingPanel);
		}
		else if(surfaceProperty instanceof EditablePictureSurfaceDiffuse)
		{
			pictureSurface = (EditablePictureSurfaceDiffuse)surfaceProperty;
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PICTURE);
			pictureCorner.setVector2D(new Vector2D(pictureSurface.getxMin(), pictureSurface.getyMin()));
			pictureSize.setVector2D(new Vector2D(pictureSurface.getxMax() - pictureSurface.getxMin(), pictureSurface.getyMax() - pictureSurface.getyMin()));
			pictureFile = pictureSurface.getPictureFile();
			pictureFileChanged = false;
			pictureFileNameField.setText(pictureSurface.getFilename());
			pictureSurfaceTiledCheckbox.setSelected(pictureSurface.isTiled());
			setOptionalParameterPanelComponent(picturePanel);
		}
		else if(surfaceProperty instanceof Pixellation)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.PIXELLATION);
			Pixellation pixellation = (Pixellation)surfaceProperty;
			pixellationPixelSideLengthPanel.setNumber(pixellation.getPixelSideLength());
			pixellationLambdaPanel.setNumber(pixellation.getLambda());
			pixellationSimulateDiffractiveBlurCheckBox.setSelected(pixellation.isSimulateDiffractiveBlur());
			pixellationSimulateRayOffsetCheckBox.setSelected(pixellation.isSimulateRayOffset());
			setOptionalParameterPanelComponent(pixellationPanel);
		}
		else if(surfaceProperty instanceof Rainbow)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.RAINBOW);
			Rainbow rainbow = (Rainbow)surfaceProperty;
			rainbowSaturationPanel.setNumber(rainbow.getSaturation());
			rainbowLightnessPanel.setNumber(rainbow.getLightness());
			rainbowLightSourcePositionPanel.setVector3D(rainbow.getLightSourcePosition());
			setOptionalParameterPanelComponent(rainbowPanel);
		}
		else if(surfaceProperty instanceof Reflective)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFLECTIVE);
			setOptionalParameterPanelComponent(disabledParametersPanel);
		}
		else if(surfaceProperty instanceof Refractive)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFRACTIVE);
			refractiveIndexRatioPanel.setNumber(((Refractive)surfaceProperty).getInsideOutsideRefractiveIndexRatio());
			setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
		}
		else if(surfaceProperty instanceof RefractiveComplex)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.REFRACTIVE_COMPLEX);
			complexRefractiveIndexRatioPanel.setNumber(((RefractiveComplex)surfaceProperty).getInsideOutsideRefractiveIndexRatio());
			setOptionalParameterPanelComponent(complexRefractiveIndexRatioPanel);
		}
		else if(surfaceProperty instanceof RayRotating)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATING);
			rayRotationAnglePanel.setNumber(((RayRotating)surfaceProperty).getRotationAngle()*180/Math.PI);
			setOptionalParameterPanelComponent(rayRotationAnglePanel);
		}
		else if(surfaceProperty instanceof RayRotatingAboutArbitraryAxisDirection)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION);
			rayRotationAngleArbitraryAxisDirectionPanel.setNumber(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getOutwardsRotationAngle()*180/Math.PI);
			rayRotationAxisPanel.setVector3D(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getRotationAxisUnitVector());
			rayRotationAxisDirectionBasisComboBox.setSelectedItem(((RayRotatingAboutArbitraryAxisDirection)surfaceProperty).getBasis());
			setOptionalParameterPanelComponent(rayRotationAroundArbitraryAxisDirectionPanel);			
		}
		else if(surfaceProperty instanceof RotationallySymmetricPhaseHologram)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM);
			rsphCentrePanel.setVector3D(((RotationallySymmetricPhaseHologram)surfaceProperty).getCentre());
			rsphBPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getB());
			rsphCPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getC());
			rsphGPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getG());
			rsphHPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getH());
			rsphSPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getS());
			rsphTPanel.setNumber(((RotationallySymmetricPhaseHologram)surfaceProperty).getT());
			setOptionalParameterPanelComponent(rotationallySymmetricPhaseHologramPanel);
		}
		else if(surfaceProperty instanceof Teleporting)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TELEPORTING);
			teleportingParametersLine.setDestinationObject(((Teleporting)surfaceProperty).getDestinationObject());
			teleportationTypeComboBox.setSelectedItem(((Teleporting)surfaceProperty).getTeleportationType());
			setOptionalParameterPanelComponent(teleportingParametersPanel);
		}
		else if(surfaceProperty instanceof EditableSurfaceTiling)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TILED);
			surfaceTiling = (EditableSurfaceTiling)surfaceProperty;
//			tilingParametersPanel.setPeriod1(((SurfaceTiling)surfaceProperty).getWidthU());
//			tilingParametersPanel.setPeriod2(((SurfaceTiling)surfaceProperty).getWidthV());
			setOptionalParameterPanelComponent(tilingParametersButton);
		}
		else if(surfaceProperty instanceof Transparent)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TRANSPARENT);
			transmissionCoefficientPanel.setNumber(((Transparent)surfaceProperty).getTransmissionCoefficient());
			setOptionalParameterPanelComponent(transmissionCoefficientPanel);
			// setOptionalParameterPanelComponent(disabledParametersPanel);
		}
		else if(surfaceProperty instanceof EditableTwoSidedSurface)
		{
			surfacePropertyComboBox.setSurfacePropertyType(SurfacePropertyType.TWO_SIDED);
			twoSidedSurface = (EditableTwoSidedSurface)surfaceProperty;
//			tilingParametersPanel.setPeriod1(((SurfaceTiling)surfaceProperty).getWidthU());
//			tilingParametersPanel.setPeriod2(((SurfaceTiling)surfaceProperty).getWidthV());
			setOptionalParameterPanelComponent(twoSidedParametersButton);
		}
	}

	public SurfaceProperty getSurfaceProperty()
	{
		SurfacePropertyType surfacePropertyType = surfacePropertyComboBox.getSurfacePropertyType();

		SurfaceProperty surfaceProperty = null;
		
		switch(surfacePropertyType)
		{
		case COLOURED:
			// return a shiny version of the colour
			surfaceProperty = new SurfaceColour(
					colourPanel.getDoubleColour(),
					DoubleColour.WHITE,	    // specular component; white = shiny
					shadowThrowingCheckBox.isSelected()	// shadow-throwing
				);
			break;
		case COLOUR_FILTER:
			surfaceProperty = new ColourFilter(
					colourPanel.getDoubleColour(),
					shadowThrowingCheckBox.isSelected()	// shadow-throwing
				);
			break;
		case COLOURED_GLOWING:
			surfaceProperty = new SurfaceColourLightSourceIndependent(colourPanel.getDoubleColour(), shadowThrowingCheckBox.isSelected());
			break;
		case COLOURED_TIME_DEPENDENT:
			surfaceProperty = new SurfaceColourTimeDependent(surfaceColourTimeDependentPanel.getNumber(), shadowThrowingCheckBox.isSelected());
			break;
		case CONFOCAL_LENSLET_ARRAYS:
			surfaceProperty = new ConfocalLensletArrays(
					CLAsEtaPanel.getNumber(),
					CLAsTransmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case CYLINDRICAL_LENS_HOLOGRAM:
			surfaceProperty = new PhaseHologramOfCylindricalLens(
					cylindricalLensHologramFocalLengthPanel.getNumber(),	// focal length
					pointOnAxisVector3DPanel.getVector3D(),	// point on central line
					phaseGradientDirectionVector3DPanel.getVector3D(),	// phase-gradient direction
					cylindricalLensHologramTransmissionCoefficientPanel.getNumber(),	// transmission coefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case EATON_LENS:
			surfaceProperty = new EatonLensSurfaceAngleFormulation(
					// criticalAngleOfIncidencePanel.getNumber()*Math.PI/180,
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case FLIPPING:
			surfaceProperty = new RayFlipping(flipAxisAnglePanel.getNumber()*Math.PI/180, SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case GALILEO_TRANSFORM_INTERFACE:
			surfaceProperty = new GalileoTransformInterface(
					betaGalileoVector3DPanel.getVector3D(),
					(CoordinateSystemType)(betaGalileoBasisComboBox.getSelectedItem()),
					transmissionCoefficientGalileoPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case GENERALISED_CONFOCAL_LENSLET_ARRAYS:
			Vector2D
				eta = etaPanel.getVector2D(),
				delta = deltaPanel.getVector2D();
			surfaceProperty = new GCLAsWithApertures(
					aVector3DPanel.getVector3D(),
					uVector3DPanel.getVector3D(),
					vVector3DPanel.getVector3D(),
					eta.x,
					eta.y,
//					etaUPanel.getNumber(),
//					etaVPanel.getNumber(),
					delta.x,
					delta.y,
//					deltaUPanel.getNumber(),
//					deltaVPanel.getNumber(),
					(GlobalOrLocalCoordinateSystemType)(gCLAsBasisComboBox.getSelectedItem()),
					gCLAsConstantTransmissionCoefficientPanel.getNumber(),
					// gCLAsCalculateGeometricalTransmissionCoefficientCheckBox.isSelected(),
					(GCLAsTransmissionCoefficientCalculationMethodType)(gCLAsTransmissionCoefficientMethodComboBox.getSelectedItem()),
					shadowThrowingCheckBox.isSelected(),
					gCLAsPixelSideLengthPanel.getNumber(),	// pixelSideLength
					gCLAsLambdaPanel.getNumber(),	// lambda
					gCLAsSimulateDiffractiveBlurCheckBox.isSelected(),	// simulateDiffractiveBlur
					gCLAsSimulateRayOffsetCheckBox.isSelected()	// simulateRayOffset
				);
			break;
		case GLENS_HOLOGRAM:
			surfaceProperty = new GlensSurface(
					opticalAxisPosVector3DPanel.getVector3D(),	// opticalAxisDirectionPos,
					pointOnGlensVector3DPanel.getVector3D(),
					meanFPanel.getNumber(),
					nodalPointFVector3DPanel.getVector3D(),	// nodalPoint,
					focalLengthNegFPanel.getNumber(),	// focalLengthNeg,
					focalLengthPosFPanel.getNumber(),	// focalLengthPos,
					glensTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient,
					true	// shadowThrowing
				);
			break;
		case IDEAL_THIN_LENS_SURFACE:
			surfaceProperty = new IdealThinLensSurface(
					opticalAxisVector3DPanel.getVector3D(),	// opticalAxisDirection
					principalPointVector3DPanel.getVector3D(),	// principalPoint
					focalLengthPanel.getNumber(),	// focalLength
					lensTransmissionCoefficientPanel.getNumber(),	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case LENSLET_ARRAY:
			surfaceProperty = new RectangularIdealThinLensletArray(
					lensletArrayFocalLengthPanel.getNumber(),	// focalLength
					lensletArrayPeriodPanel.getVector2D().x,	// xPeriod
					lensletArrayPeriodPanel.getVector2D().y,	// yPeriod
					lensletArrayOffsetPanel.getVector2D().x,	// xOffset
					lensletArrayOffsetPanel.getVector2D().y,	// yOffset
					lensletArrayTransmissionCoefficientPanel.getNumber(),	// throughputCoefficient
					true	// shadowThrowing
				);
			break;
		case LORENTZ_TRANSFORM_INTERFACE:
			surfaceProperty = new LorentzTransformInterface(
					betaVector3DPanel.getVector3D(),
					(CoordinateSystemType)(betaBasisComboBox.getSelectedItem()),
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case LUNEBURG_LENS:
			surfaceProperty = new LuneburgLensSurfaceAngleFormulation(
					// criticalAngleOfIncidencePanel.getNumber()*Math.PI/180,
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case METRIC_INTERFACE:
			surfaceProperty = new MetricInterface(
					insideMetricTensorPanel.getMatrix3D(),
					outsideMetricTensorPanel.getMatrix3D(),
					(GlobalOrLocalCoordinateSystemType)(metricTensorsBasisComboBox.getSelectedItem()),
					(RefractionType)(refractionTypeComboBox.getSelectedItem()),
					allowImaginaryOpticalPathLengthsCheckBox.isSelected(),
					transmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case PHASE_CONJUGATING:
			surfaceProperty = new PhaseConjugating(
					isPhaseConjugatingSurfaceReflectiveCheckBox.isSelected()?SurfaceProperty.ReflectiveOrTransmissive.REFLECTIVE:SurfaceProperty.ReflectiveOrTransmissive.TRANSMISSIVE,
					phaseConjugatingSurfaceTransmissionCoefficientPanel.getNumber(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case POINT2POINT_IMAGING:
			surfaceProperty = new Point2PointImagingPhaseHologram(
					point2pointImagingPoint1Panel.getVector3D(),
					point2pointImagingPoint2Panel.getVector3D(),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
					point2pointImagingIsReflectiveCheckBox.isSelected(),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case PICTURE:
			Vector2D
				corner = pictureCorner.getVector2D(),
				size = pictureSize.getVector2D();
			pictureSurface.setxMin(corner.x);
			pictureSurface.setxMax(corner.x + size.x);
			pictureSurface.setyMin(corner.y);
			pictureSurface.setyMax(corner.y + size.y);
			if(pictureFileChanged)
			{
				pictureSurface.setPicture(pictureFile);
				pictureFileChanged = false;
			}
			pictureSurface.setTiled(pictureSurfaceTiledCheckbox.isSelected());
			surfaceProperty = pictureSurface;
			break;
		case PIXELLATION:
			surfaceProperty = new Pixellation(
					pixellationPixelSideLengthPanel.getNumber(),	// pixelSideLength
					pixellationLambdaPanel.getNumber(),	// lambda
					pixellationSimulateDiffractiveBlurCheckBox.isSelected(),	// simulateDiffractiveBlur
					pixellationSimulateRayOffsetCheckBox.isSelected()	// simulateRayOffset
				);
			break;
		case RADIAL_LENTICULAR_ARRAY:
			surfaceProperty = new PhaseHologramOfRadialLenticularArray(
					rlapCentrePanel.getVector3D(),	// centre
					rlapFPanel.getNumber(),	// f
					rlapNPanel.getNumber(),	// n
					rlapD0Panel.getVector3D(),	// d0
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case RAINBOW:
			surfaceProperty = new Rainbow(
					rainbowSaturationPanel.getNumber(),
					rainbowLightnessPanel.getNumber(),
					rainbowLightSourcePositionPanel.getVector3D()
				);
			break;
		case REFLECTIVE:
			surfaceProperty = new Reflective(SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case REFRACTIVE:
			surfaceProperty = new Refractive(refractiveIndexRatioPanel.getNumber(), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case REFRACTIVE_COMPLEX:
			surfaceProperty = new RefractiveComplex(complexRefractiveIndexRatioPanel.getNumber(), SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case ROTATING:
			surfaceProperty = new RayRotating(rayRotationAnglePanel.getNumber()*Math.PI/180., SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected());
			break;
		case ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION:
			surfaceProperty = new RayRotatingAboutArbitraryAxisDirection(
					rayRotationAngleArbitraryAxisDirectionPanel.getNumber()*Math.PI/180,	// rotationa angle
					rayRotationAxisPanel.getVector3D(),	// rotation axis unit vector
					(GlobalOrLocalCoordinateSystemType)(rayRotationAxisDirectionBasisComboBox.getSelectedItem()),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT, shadowThrowingCheckBox.isSelected()
				);
			break;
		case ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM:
			surfaceProperty = new RotationallySymmetricPhaseHologram(
					rsphCentrePanel.getVector3D(),	// centre
					rsphBPanel.getNumber(),	// b
					rsphCPanel.getNumber(),	// c
					rsphGPanel.getNumber(),	// g
					rsphHPanel.getNumber(),	// h
					rsphSPanel.getNumber(),	// s
					rsphTPanel.getNumber(),	// t
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// throughputCoefficient
					false,	// reflective
					shadowThrowingCheckBox.isSelected()	// shadowThrowing
				);
			break;
		case TELEPORTING:
			surfaceProperty = new Teleporting(
					teleportingParametersLine.getDestinationObject(),
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,
					(TeleportationType)(teleportationTypeComboBox.getSelectedItem()),
					shadowThrowingCheckBox.isSelected()
				);
			break;
		case TILED:
			return surfaceTiling;
		case TRANSPARENT:
			surfaceProperty = new Transparent(transmissionCoefficientPanel.getNumber(), shadowThrowingCheckBox.isSelected());
			// return new Transparent(TRANSMISSION_COEFFICIENT);
			break;
		case TWO_SIDED:
			surfaceProperty = twoSidedSurface;
			break;
		}

		if(surfaceProperty != null)
		{
			if(surfaceProperty instanceof SurfacePropertyWithControllableShadow)
			{
				((SurfacePropertyWithControllableShadow)surfaceProperty).setShadowThrowing(shadowThrowingCheckBox.isSelected());
			}
		}

		return surfaceProperty;
	}

	/**
	 * A little inner class describing the combo box for selecting a surface-property class
	 */
	class SurfacePropertyComboBox extends JComboBox<SurfacePropertyType> implements ActionListener
	{
		private static final long serialVersionUID = 7398035768553054607L;

		public SurfacePropertyComboBox()
		{
			super(SurfacePropertyType.values());

			addActionListener(this);
		}

		public void setSurfacePropertyType(SurfacePropertyType surfaceProperty)
		{
			setSelectedItem(surfaceProperty);
		}

		public SurfacePropertyType getSurfacePropertyType()
		{
			return (SurfacePropertyType)getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			SurfacePropertyType surfacePropertyType = getSurfacePropertyType();

			switch(surfacePropertyType)
			{
			case COLOURED:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOUR_FILTER:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOURED_GLOWING:
				setOptionalParameterPanelComponent(colourPanel);
				break;
			case COLOURED_TIME_DEPENDENT:
				setOptionalParameterPanelComponent(surfaceColourTimeDependentPanel);
				break;
			case CONFOCAL_LENSLET_ARRAYS:
				setOptionalParameterPanelComponent(CLAsPanel);
				break;
			case CYLINDRICAL_LENS_HOLOGRAM:
				setOptionalParameterPanelComponent(cylindricalLensHologramPanel);
				break;
			case EATON_LENS:
				// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				break;
			case FLIPPING:
				setOptionalParameterPanelComponent(flipAxisAnglePanel);
				break;
			case GALILEO_TRANSFORM_INTERFACE:
				setOptionalParameterPanelComponent(galileoTransformInterfacePanel);
				break;
			case GENERALISED_CONFOCAL_LENSLET_ARRAYS:
				setOptionalParameterPanelComponent(gCLAsPanel);
				break;
			case GLENS_HOLOGRAM:
				setOptionalParameterPanelComponent(glensHologramPanel);
				break;
			case IDEAL_THIN_LENS_SURFACE:
				setOptionalParameterPanelComponent(lensHologramPanel);
				break;
			case LENSLET_ARRAY:
				setOptionalParameterPanelComponent(lensletArrayPanel);
				break;
			case LORENTZ_TRANSFORM_INTERFACE:
				setOptionalParameterPanelComponent(lorentzTransformInterfacePanel);
				break;
			case LUNEBURG_LENS:
				// setOptionalParameterPanelComponent(EatonLuneburgLensSurfacePanel);
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				break;
			case METRIC_INTERFACE:
				setOptionalParameterPanelComponent(metricInterfacePanel);
				break;
			case PHASE_CONJUGATING:
				setOptionalParameterPanelComponent(phaseConjugatingPanel);
				break;
			case POINT2POINT_IMAGING:
				setOptionalParameterPanelComponent(point2pointImagingPanel);
				break;
			case PICTURE:
				setOptionalParameterPanelComponent(picturePanel);
				break;
			case PIXELLATION:
				setOptionalParameterPanelComponent(pixellationPanel);
				break;
			case RADIAL_LENTICULAR_ARRAY:
				setOptionalParameterPanelComponent(phaseHologramOfRadialLenticularArrayPanel);
				break;
			case RAINBOW:
				setOptionalParameterPanelComponent(rainbowPanel);
				break;
			case REFLECTIVE:
				setOptionalParameterPanelComponent(disabledParametersPanel);
				break;
			case REFRACTIVE:
				setOptionalParameterPanelComponent(refractiveIndexRatioPanel);
				break;
			case REFRACTIVE_COMPLEX:
				setOptionalParameterPanelComponent(complexRefractiveIndexRatioPanel);
				break;
			case ROTATIONALLY_SYMMETRIC_PHASE_HOLOGRAM:
				setOptionalParameterPanelComponent(rotationallySymmetricPhaseHologramPanel);
				break;
			case ROTATING:
				setOptionalParameterPanelComponent(rayRotationAnglePanel);
				break;
			case ROTATING_AROUND_ARBITRARY_AXIS_DIRECTION:
				setOptionalParameterPanelComponent(rayRotationAroundArbitraryAxisDirectionPanel);
				break;
			case TELEPORTING:
				setOptionalParameterPanelComponent(teleportingParametersPanel);
				break;
			case TILED:
				setOptionalParameterPanelComponent(tilingParametersButton);
				break;
			case TRANSPARENT:
				setOptionalParameterPanelComponent(transmissionCoefficientPanel);
				// setOptionalParameterPanelComponent(disabledParametersPanel);
				break;
			case TWO_SIDED:
				setOptionalParameterPanelComponent(twoSidedParametersButton);
				break;
			}
			
			SurfaceProperty surfaceProperty = getSurfaceProperty();
			shadowThrowingCheckBox.setVisible(surfaceProperty instanceof SurfacePropertyWithControllableShadow);
		}
	}
	
	/**
	 * ensures that the panels for point 1 and point 2 in the point-to-point-imaging surface are labelled correctly
	 * @param isReflective
	 */
	private void updatePoint2pointImagingPanel(boolean isReflective)
	{
		if(isReflective)
		{
			point2pointImagingPoint1Label.setText("Object position");
			point2pointImagingPoint2Label.setText("Image position");
		}
		else
		{
			point2pointImagingPoint1Label.setText("Inside-space position");
			point2pointImagingPoint2Label.setText("Outside-space position");
		}

		point2pointImagingPanel.revalidate();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == gCLAsTransmissionCoefficientMethodComboBox)
		{
			gCLAsConstantTransmissionCoefficientPanel.setEnabled(
					(GCLAsTransmissionCoefficientCalculationMethodType)(gCLAsTransmissionCoefficientMethodComboBox.getSelectedItem())
					== GCLAsTransmissionCoefficientCalculationMethodType.CONSTANT
				);
		}
		else if(e.getActionCommand().equals(TILING_PARAMS_BUTTON_TEXT))
		{
			EditableSurfaceTiling edit = surfaceTiling;
			iPanel.addFrontComponent(edit, "Edit tiling");
			edit.setValuesInEditPanel();
		}
		else if(e.getActionCommand().equals(TWO_SIDED_PARAMS_BUTTON_TEXT))
		{
			EditableTwoSidedSurface edit = twoSidedSurface;
			iPanel.addFrontComponent(edit, "Edit two-sided surface");
			edit.setValuesInEditPanel();
		}
		else if(e.getSource() == choosePictureFileButton)
		{
			//Create a file chooser
			JFileChooser fc = new JFileChooser();

			// file chooser info: http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
			int returnVal = fc.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION)
	        {
	        	pictureFile = fc.getSelectedFile();
				pictureFileNameField.setText(pictureFile.getName());
				pictureFileChanged = true;
	        }
		}
		else if(e.getSource() == point2pointImagingIsReflectiveCheckBox)
		{
			updatePoint2pointImagingPanel(point2pointImagingIsReflectiveCheckBox.isSelected());
		}
	}

	public SceneObject getScene()
	{
		return scene;
	}

	public void setScene(SceneObject scene)
	{
		this.scene = scene;
	}
}