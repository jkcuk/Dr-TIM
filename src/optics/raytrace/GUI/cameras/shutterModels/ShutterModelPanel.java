package optics.raytrace.GUI.cameras.shutterModels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import math.Vector3D;
import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.core.*;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledComboBoxPanel;
import optics.raytrace.GUI.lowLevel.LabelledDoublePanel;
import optics.raytrace.GUI.lowLevel.LabelledVector3DPanel;
import optics.raytrace.cameras.shutterModels.ArbitraryPlaneShutterModel;
import optics.raytrace.cameras.shutterModels.DetectorPlaneShutterModel;
import optics.raytrace.cameras.RelativisticAnyFocusSurfaceCamera;
import optics.raytrace.cameras.shutterModels.AperturePlaneShutterModel;
import optics.raytrace.cameras.shutterModels.FixedPointSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.FocusSurfaceShutterModel;
import optics.raytrace.cameras.shutterModels.InstantShutterModel;
import optics.raytrace.cameras.shutterModels.LensType;
import optics.raytrace.cameras.shutterModels.ShutterModel;
import optics.raytrace.cameras.shutterModels.ShutterModelType;

/**
 * Allows editing of the type and parameters of the shutter model
 */
public class ShutterModelPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 5047742925664667502L;

	private JComboBox<ShutterModelType> shutterModelComboBox;
	
	private JPanel optionalParametersPanel;

	// common panels
	private LabelledDoublePanel shutterOpeningTimePanel;
	
	// aperture-plane shutter-model panel
	private JPanel aperturePlaneShutterModelPanel;
	private JTextField aperturePlaneShutterModelInfoTextField;
	private JButton updateAperturePlaneShutterModelInfoButton;

	// arbitrary-plane-shutter-model panel
	private JPanel arbitraryPlaneShutterModelPanel;
	private LabelledVector3DPanel pointInShutterPlanePanel, normalToShutterPlanePanel;
	
	// detector-plane-shutter-model panel
	private JPanel detectorPlaneShutterModelPanel;
	private JComboBox<LensType> lensTypeComboBox;
	private LabelledDoublePanel detectorDistancePanel;
	
	private IPanel iPanel;
	
	// this panel needs to know the camera whose shutter model it is editing
	private RelativisticAnyFocusSurfaceCamera camera;
	
	private IPanelComponent cameraIPanel;

	public ShutterModelPanel(String description, boolean showFrame, RelativisticAnyFocusSurfaceCamera camera)
	{
		super();
		
		setCamera(camera);
		
		// this.iPanel = iPanel;
		
		setLayout(new MigLayout("insets 0"));
		if(showFrame) setBorder(GUIBitsAndBobs.getTitledBorder(description));

		shutterModelComboBox = new JComboBox<ShutterModelType>(ShutterModelType.values());
		shutterModelComboBox.setSelectedItem(ShutterModelType.APERTURE_PLANE_SHUTTER);
		shutterModelComboBox.addActionListener(this);
		add(GUIBitsAndBobs.makeRow("Type", shutterModelComboBox), "wrap");

		shutterOpeningTimePanel = new LabelledDoublePanel("Shutter-opening time (c=1)");
		shutterOpeningTimePanel.getDoublePanel().addActionListener(this);
		add(shutterOpeningTimePanel, "wrap");

		optionalParametersPanel = new JPanel();
		add(optionalParametersPanel);

		//
		// initialise the optional-parameter panels
		//
		
		// aperture-plane shutter-model panel
		aperturePlaneShutterModelPanel = new JPanel();
		aperturePlaneShutterModelPanel.setLayout(new MigLayout("insets 0"));
		aperturePlaneShutterModelInfoTextField = new JTextField(40);
		aperturePlaneShutterModelInfoTextField.setEditable(false);
		updateAperturePlaneShutterModelInfoButton = new JButton("Update");
		updateAperturePlaneShutterModelInfoButton.addActionListener(this);
		aperturePlaneShutterModelPanel.add(GUIBitsAndBobs.makeRow(aperturePlaneShutterModelInfoTextField, updateAperturePlaneShutterModelInfoButton), "wrap");
		aperturePlaneShutterModelPanel.validate();
		
		// arbitrary-plane-shutter-model panel
		arbitraryPlaneShutterModelPanel = new JPanel();
		arbitraryPlaneShutterModelPanel.setLayout(new MigLayout("insets 0"));
		pointInShutterPlanePanel = new LabelledVector3DPanel("Point in shutter plane");
		pointInShutterPlanePanel.setVector3D(new Vector3D(0, 0, 0));
		arbitraryPlaneShutterModelPanel.add(pointInShutterPlanePanel, "wrap");
		normalToShutterPlanePanel = new LabelledVector3DPanel("Normal to shutter plane");
		normalToShutterPlanePanel.setVector3D(new Vector3D(0, 0, 1));
		arbitraryPlaneShutterModelPanel.add(normalToShutterPlanePanel, "wrap");
		arbitraryPlaneShutterModelPanel.validate();
		
		// detector-plane-shutter-model panel
		detectorPlaneShutterModelPanel = new JPanel();
		detectorPlaneShutterModelPanel.setLayout(new MigLayout("insets 0"));
		lensTypeComboBox = new JComboBox<LensType>(LensType.values());
		lensTypeComboBox.setSelectedItem(LensType.IDEAL_LENS);
		detectorPlaneShutterModelPanel.add(new LabelledComboBoxPanel("Camera-lens type", lensTypeComboBox), "wrap");
		detectorDistancePanel = new LabelledDoublePanel("Distance of detector behind entrance pupil");
		detectorDistancePanel.setNumber(1);
		detectorPlaneShutterModelPanel.add(detectorDistancePanel, "wrap");
		detectorPlaneShutterModelPanel.validate();
		
		// setPreferredSize(new Dimension(400, 140));
		
		validate();
	}

	public ShutterModelPanel(RelativisticAnyFocusSurfaceCamera camera)
	{
		this("Shutter model", true, camera);
	}
	
	public RelativisticAnyFocusSurfaceCamera getCamera() {
		return camera;
	}

	public void setCamera(RelativisticAnyFocusSurfaceCamera camera) {
		this.camera = camera;
	}

	public void setIPanel(IPanel iPanel)
	{
		this.iPanel = iPanel;
	}
	

	public IPanelComponent getCameraIPanel() {
		return cameraIPanel;
	}

	public void setCameraIPanel(IPanelComponent cameraIPanel) {
		this.cameraIPanel = cameraIPanel;
	}

	private void setOptionalParameterPanelComponent(Component newComponent)
	{
		// System.out.println("ShutterModelPanel::setOptionalParameterPanelComponent: optionalParametersPanel="+optionalParametersPanel);
		
		// remove any component currently in the optional-parameter panel
		while(optionalParametersPanel.getComponentCount() > 0) optionalParametersPanel.remove(0);

		// now add the new component
		if(newComponent != null)
		{
			optionalParametersPanel.add(newComponent);
		}
		optionalParametersPanel.revalidate();

		if(iPanel != null)
		{
			// validate the enclosing panel
			iPanel.mainPanelChanged();
		}
	}
	
	public void setShutterModel(ShutterModel shutterModel)
	{
		// System.out.println("ShutterModelPanel::setShutterModel: Hi!");
		
		shutterModelComboBox.setSelectedItem(shutterModel.getShutterModelType());
		
		if(shutterModel instanceof InstantShutterModel)
		{
			shutterOpeningTimePanel.setVisible(true);
			shutterOpeningTimePanel.setNumber(((InstantShutterModel)shutterModel).getShutterOpeningTime());
		}
		else
		{
			shutterOpeningTimePanel.setVisible(false);
		}

		switch(shutterModel.getShutterModelType())
		{
		case ARBITRARY_PLANE_SHUTTER:
			pointInShutterPlanePanel.setVector3D(((ArbitraryPlaneShutterModel)shutterModel).getPointInShutterPlane());
			normalToShutterPlanePanel.setVector3D(((ArbitraryPlaneShutterModel)shutterModel).getNormalToShutterPlane());
			setOptionalParameterPanelComponent(arbitraryPlaneShutterModelPanel);
			break;
		case APERTURE_PLANE_SHUTTER:
			setOptionalParameterPanelComponent(aperturePlaneShutterModelPanel);
			updateAperturePlaneShutterModelInfo();
			break;
		case FOCUS_SURFACE_SHUTTER:
			setOptionalParameterPanelComponent(null);
			break;
		case FIXED_POINT_SURFACE_SHUTTER:
			setOptionalParameterPanelComponent(null);
			break;
		case DETECTOR_PLANE_SHUTTER:
		default:
			lensTypeComboBox.setSelectedItem(((DetectorPlaneShutterModel)shutterModel).getLensType());
			detectorDistancePanel.setNumber(((DetectorPlaneShutterModel)shutterModel).getDetectorDistance());
			setOptionalParameterPanelComponent(detectorPlaneShutterModelPanel);
		}
	}

	public ShutterModel getShutterModel()
	{
//		System.out.println("ShutterModelPanel::getShutterModel: "
//				+ (ShutterModelType)(shutterModelComboBox.getSelectedItem())
//				+ ", pointInShuterPlane=" + pointInShutterPlanePanel.getVector3D()
//				+ ", normalToShutterPlane=" + normalToShutterPlanePanel.getVector3D()
//				+ ", lensType=" + (LensType)(lensTypeComboBox.getSelectedItem())
//				+ ", camera=" + getCamera()
//				+ ", shutterOpeningTime=" + shutterOpeningTimePanel.getNumber()
//			);
		
		switch((ShutterModelType)(shutterModelComboBox.getSelectedItem()))
		{
		case ARBITRARY_PLANE_SHUTTER:
			return new ArbitraryPlaneShutterModel(
					pointInShutterPlanePanel.getVector3D(),
					normalToShutterPlanePanel.getVector3D(),
					shutterOpeningTimePanel.getNumber()
				);
		case APERTURE_PLANE_SHUTTER:
			return new AperturePlaneShutterModel(shutterOpeningTimePanel.getNumber());
		case FOCUS_SURFACE_SHUTTER:
			return new FocusSurfaceShutterModel(shutterOpeningTimePanel.getNumber());
		case FIXED_POINT_SURFACE_SHUTTER:
			return new FixedPointSurfaceShutterModel(camera.getBeta());
		case DETECTOR_PLANE_SHUTTER:
		default:
			return new DetectorPlaneShutterModel(
					(LensType)(lensTypeComboBox.getSelectedItem()),
					detectorDistancePanel.getNumber(),
					getCamera(),
					shutterOpeningTimePanel.getNumber()
				);
		}
	}
	
	private void updateAperturePlaneShutterModelInfo()
	{
		if(cameraIPanel != null)
		{
		aperturePlaneShutterModelInfoTextField.setText(
				"Aperture centre (beta = "+camera.getBeta()+", t = "+shutterOpeningTimePanel.getNumber()+
				"): "+
				camera.getSpaceTimeTransformation().getTransformedPosition(
						camera.getApertureCentre(),
						shutterOpeningTimePanel.getNumber()
					)
			);
		}
		else
			aperturePlaneShutterModelInfoTextField.setText("--- unable to calculate required information ---");
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(shutterModelComboBox))
		{
			switch((ShutterModelType)(shutterModelComboBox.getSelectedItem()))
			{
			case APERTURE_PLANE_SHUTTER:
				setOptionalParameterPanelComponent(aperturePlaneShutterModelPanel);
				shutterOpeningTimePanel.setEnabled(true);
				break;
			case ARBITRARY_PLANE_SHUTTER:
				setOptionalParameterPanelComponent(arbitraryPlaneShutterModelPanel);
				shutterOpeningTimePanel.setEnabled(true);
				break;
			case FOCUS_SURFACE_SHUTTER:
				setOptionalParameterPanelComponent(null);
				shutterOpeningTimePanel.setEnabled(true);
				break;
			case FIXED_POINT_SURFACE_SHUTTER:
				setOptionalParameterPanelComponent(null);
				shutterOpeningTimePanel.setEnabled(false);
				break;
			case DETECTOR_PLANE_SHUTTER:
			default:
				setOptionalParameterPanelComponent(detectorPlaneShutterModelPanel);
				shutterOpeningTimePanel.setEnabled(true);
			}
		}
		else if(e.getSource().equals(updateAperturePlaneShutterModelInfoButton))
		{
			if(cameraIPanel != null)
			{
				cameraIPanel.acceptValuesInEditPanel();
				updateAperturePlaneShutterModelInfo();
			}
		}
	}

}