package optics.raytrace.panorama.panorama3DViewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import math.MyMath;
import math.Vector3D;
import optics.raytrace.GUI.nonInteractive.PhotoCanvas;
import optics.raytrace.cameras.SurroundAnaglyphCamera;
import optics.raytrace.cameras.SurroundAnaglyphCamera.OutputType;
import optics.raytrace.core.Studio;
import optics.raytrace.panorama.Panorama3D;
import optics.raytrace.panorama.Screen;
import optics.raytrace.utility.MyImageIO;


/**
 * Prompts the user to select two BMP files, interprets one as a 4π surround panoramic view from the
 * left eye, the other from the right eye, and combines them into a 4π surround anaglyph.
 *
 * For some reason, on my computer this runs as an applet but not as a Java Application!?
 * 
 * @author Johannes Courtial
 */
public class SurroundAnaglyphViewerRunnable
implements Runnable, ChangeListener, ActionListener
{
	protected Panorama3D panorama3D;

	// GUI components
	protected Container container;
	private CyclicSpinnerNumberModel phiSpinnerModel;
	private SpinnerNumberModel thetaSpinnerModel, distanceSpinnerModel, sizeSpinnerModel;
	// private transient JSpinner phiSpinner, thetaSpinner, distanceSpinner, sizeSpinner;	// the number part
	private Panorama3DGeometryRenderingTypeComboBox rendererGeometryComboBox;
	private Panorama3DGeometryViewingTypeComboBox viewerGeometryComboBox;
	private PanoramaResolutionComboBox panoramaResolutionComboBox;
	private SurroundAnaglyphViewerSceneTypeComboBox surroundAnaglyphViewerSceneTypeComboBox;
	private JButton renderButton, save3DPanoramaAsTopBottomImageButton, saveAnaglyphAsBMPButton;
	protected PhotoCanvas photoCanvas, surroundAnaglyphCanvas;	


	public SurroundAnaglyphViewerRunnable(Container container)
	{
		super();
		this.container = container;
	}
	
	private Studio createStudio()
	{
		Studio studio;
		
		switch(surroundAnaglyphViewerSceneTypeComboBox.getSurroundAnaglyphViewerSceneType())
		{
		case INSIDE_SPHERE_R1:
			studio = StandardStudios.getInsideSphereStudio(1);
			break;
		case INSIDE_SPHERE_R2:
			studio = StandardStudios.getInsideSphereStudio(2);
			break;
		case INSIDE_SPHERE_R4:
			studio = StandardStudios.getInsideSphereStudio(4);
			break;
		case INSIDE_SPHERE_R8:
			studio = StandardStudios.getInsideSphereStudio(8);
			break;
		case INSIDE_SPHERE_R16:
			studio = StandardStudios.getInsideSphereStudio(16);
			break;
		case INSIDE_SPHERE_R32:
			studio = StandardStudios.getInsideSphereStudio(32);
			break;
		case INSIDE_SPHERE_R64:
			studio = StandardStudios.getInsideSphereStudio(64);
			break;
		case STANDARD_SCENE_ZF1:
			studio = StandardStudios.getStandardSceneStudio(1);
			break;
		case STANDARD_SCENE_ZF2:
			studio = StandardStudios.getStandardSceneStudio(2);
			break;
		case STANDARD_SCENE_ZF4:
			studio = StandardStudios.getStandardSceneStudio(4);
			break;
		case STANDARD_SCENE_ZF8:
			studio = StandardStudios.getStandardSceneStudio(8);
			break;
		case STANDARD_SCENE_ZF16:
			studio = StandardStudios.getStandardSceneStudio(16);
			break;
		case STANDARD_SCENE_ZF32:
			studio = StandardStudios.getStandardSceneStudio(32);
			break;
		case STANDARD_SCENE_ZF64:
		default:
			studio = StandardStudios.getStandardSceneStudio(64);
			break;
		}
		
		// (re)define the camera

		// Note that the view direction and basis Vector3Ds of the detector are chosen such that
		// the x, y, z axes form a LEFT-handed coordinate system.
		// The reason is that, in the photo, the positive x direction is then to the right,
		// the positive y direction is upwards, and the camera looks in the positive z direction.
		
		SurroundAnaglyphCamera camera = new SurroundAnaglyphCamera(
				"Surround anaglyph camera",	// description,
				rendererGeometryComboBox.getPanoramaGeometryType().toPanorama3DGeometry(),	// panorama3DGeometry,
				OutputType.ANAGLYPH_REDBLUE,	// outputType,
				panoramaResolutionComboBox.getResolution().getHPixels(),
				panoramaResolutionComboBox.getResolution().getVPixels(),
				100	// maxTraceLevel
			);

		studio.setCamera(camera);
		
		return studio;
	}
	
	private void calculatePanorama3D()
	{
		// set the panorama3D geometry
		panorama3D.setPanorama3DGeometry(rendererGeometryComboBox.getPanoramaGeometryType().toPanorama3DGeometry());
		
		// create the studio
		Studio studio = createStudio();
		
		// extract the camera
		SurroundAnaglyphCamera camera = (SurroundAnaglyphCamera)studio.getCamera();
		
		// calculate and set the left panoramic image
		System.out.println("Calculating left-eye panorama...");
		camera.setOutputType(OutputType.LEFT_EYE);
		panorama3D.getLeftPanorama().setImage(studio.takePhoto());

		// panel.add(new PhotoCanvas(p.getLeftPanorama().getImage()));

		// calculate and set the right panoramic image
		System.out.println("Calculating right-eye panorama...");
		camera.setOutputType(OutputType.RIGHT_EYE);
		panorama3D.getRightPanorama().setImage(studio.takePhoto());
	}
	

	@Override
	public void run()
	{
		panorama3D = new Panorama3D();

		createGUI();		
	}
	
	private JSpinner getCompactJSpinner(SpinnerModel spinnerModel)
	{
        JSpinner s = new JSpinner(spinnerModel);
		((JSpinner.DefaultEditor)s.getEditor()).getTextField().setColumns(3);
		s.setMaximumSize(s.getPreferredSize());
        // s.setSize(s.getPreferredSize());
		return s;
	}
	
	private void updatePanorama()
	{
		BufferedImage surroundAnaglyph = panorama3D.createSurroundAnaglyph(2*360, 2*180);
		// BufferedImage surroundAnaglyph = panorama3D.getTopBottomImage();
		surroundAnaglyphCanvas.setImage(surroundAnaglyph);
	}
	
	public void createGUI()
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		
		//
		// the Panoramas panel
		//
		
		JPanel panoramasPanel = new JPanel();
		panoramasPanel.setLayout(new BorderLayout());

		JPanel panoramasImagePanel = new JPanel();
		panoramasImagePanel.setLayout(new BorderLayout());
		
		surroundAnaglyphCanvas = new PhotoCanvas(new BufferedImage(2*360, 2*180, BufferedImage.TYPE_INT_RGB));
		panoramasImagePanel.add(surroundAnaglyphCanvas, BorderLayout.CENTER);
		
		save3DPanoramaAsTopBottomImageButton = new JButton("Save 3D panorama as top-bottom image...");
		save3DPanoramaAsTopBottomImageButton.addActionListener(this);
		panoramasImagePanel.add(save3DPanoramaAsTopBottomImageButton, BorderLayout.SOUTH);

		JPanel panoramasControlsPanel = new JPanel();
		
		rendererGeometryComboBox = new Panorama3DGeometryRenderingTypeComboBox();
		panoramasControlsPanel.add(rendererGeometryComboBox);
		panoramaResolutionComboBox = new PanoramaResolutionComboBox();
		panoramasControlsPanel.add(panoramaResolutionComboBox);
		surroundAnaglyphViewerSceneTypeComboBox = new SurroundAnaglyphViewerSceneTypeComboBox();
		panoramasControlsPanel.add(surroundAnaglyphViewerSceneTypeComboBox);
		renderButton = new JButton("Render");
		renderButton.addActionListener(this);
		panoramasControlsPanel.add(renderButton);
		
		panoramasPanel.add(new JScrollPane(panoramasImagePanel), BorderLayout.CENTER);
		panoramasPanel.add(panoramasControlsPanel, BorderLayout.SOUTH);
		
		tabbedPane.addTab("3D Panorama", panoramasPanel);

		//
		// the Anaglyph panel
		//

		JPanel anaglyphPanel = new JPanel();
		anaglyphPanel.setLayout(new BorderLayout());

		// BufferedImage planarAnaglyph = panorama3D.createPlanarAnaglyph(screen);
		// photoCanvas = new PhotoCanvas(planarAnaglyph);
		photoCanvas = new PhotoCanvas(new BufferedImage(640, 400, BufferedImage.TYPE_INT_RGB));
		anaglyphPanel.add(new JScrollPane(photoCanvas), BorderLayout.CENTER);


		// the controls panel
		JPanel anaglyphControlsPanel = new JPanel();

        anaglyphControlsPanel.add(new JLabel("phi="));
        phiSpinnerModel = new CyclicSpinnerNumberModel(0., 0., 360., 5.);
        JSpinner phiSpinner = getCompactJSpinner(phiSpinnerModel);
		phiSpinner.setValue(0);
		anaglyphControlsPanel.add(phiSpinner);
        anaglyphControlsPanel.add(new JLabel("°, "));

        anaglyphControlsPanel.add(new JLabel("theta="));
        thetaSpinnerModel = new SpinnerNumberModel(0., 0., 180, 5.);
        JSpinner thetaSpinner = getCompactJSpinner(thetaSpinnerModel);
		thetaSpinner.setValue(90);
		anaglyphControlsPanel.add(thetaSpinner);
        anaglyphControlsPanel.add(new JLabel("°, "));

        anaglyphControlsPanel.add(new JLabel("distance="));
        distanceSpinnerModel = new SpinnerNumberModel(0, 0, 9999., 0.5);
        JSpinner distanceSpinner = getCompactJSpinner(distanceSpinnerModel);
		distanceSpinner.setValue(5);
		anaglyphControlsPanel.add(distanceSpinner);
        anaglyphControlsPanel.add(new JLabel(", "));

        anaglyphControlsPanel.add(new JLabel("screen size="));
        sizeSpinnerModel = new SpinnerNumberModel(0, 0, 9999., 0.5);
		JSpinner sizeSpinner = getCompactJSpinner(sizeSpinnerModel);
		sizeSpinner.setValue(5);
		anaglyphControlsPanel.add(sizeSpinner);
		
		viewerGeometryComboBox = new Panorama3DGeometryViewingTypeComboBox();
		anaglyphControlsPanel.add(viewerGeometryComboBox);
		
		saveAnaglyphAsBMPButton = new JButton("Save...");
		saveAnaglyphAsBMPButton.addActionListener(this);
		anaglyphControlsPanel.add(saveAnaglyphAsBMPButton);

		anaglyphPanel.add(anaglyphControlsPanel, BorderLayout.SOUTH);


		phiSpinner.addChangeListener(this);
		thetaSpinner.addChangeListener(this);
		distanceSpinner.addChangeListener(this);
		sizeSpinner.addChangeListener(this);
		viewerGeometryComboBox.addActionListener(this);
		
		anaglyphPanel.validate();

		// update();
		
		tabbedPane.addTab("Anaglyph", anaglyphPanel);
		
		//
		// add all of this to the container
		//
		
		container.add(tabbedPane);
	}
	
	public BufferedImage getPlanarAnaglyph()
	{
		// read phi and theta
		double
			d = distanceSpinnerModel.getNumber().doubleValue(),
			width = sizeSpinnerModel.getNumber().doubleValue(),
			// L = ((Double)sizeSpinner.getValue()).doubleValue(),
			// d = 2,	// distance of screen from camera
			// L = 5,
			// phi = MyMath.deg2rad(phiPanel.getNumber()),	// phi, in rad
			phi = MyMath.deg2rad(phiSpinnerModel.getNumber().doubleValue()),	// phi, in rad
			theta = MyMath.deg2rad(thetaSpinnerModel.getNumber().doubleValue()),	// theta, in rad
			sinTheta = Math.sin(theta);
		
		// this points in the direction of the screen normal
		Vector3D n = new Vector3D(sinTheta*Math.cos(phi), Math.cos(theta), sinTheta*Math.sin(phi));
		Vector3D up = new Vector3D(0, 1, 0).getPartPerpendicularTo(n).getNormalised();
		
		Screen s = new Screen(640, 400);
		s.orientScreen(n.getProductWith(d), n, up, width, width*s.getPixelsV()/s.getPixelsH());
		
		panorama3D.setPanorama3DGeometry(((Panorama3DGeometryViewingType)viewerGeometryComboBox.getSelectedItem()).toPanorama3DGeometry());
		return panorama3D.createPlanarAnaglyph(s);
	}
	
	public void updateAnaglyph()
	{
		photoCanvas.setImage(getPlanarAnaglyph());
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		updateAnaglyph();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == viewerGeometryComboBox)
		{
			updateAnaglyph();
		}
		else if(e.getSource() == renderButton)
		{
			// calculate the panorama3D accordingly
			calculatePanorama3D();

			updatePanorama();
			updateAnaglyph();
		}
		else if(e.getSource() == save3DPanoramaAsTopBottomImageButton)
		{
			MyImageIO.selectDestinationAndSaveImage("Save top-bottom image (.BMP)", panorama3D.getTopBottomImage());
		}
		else if(e.getSource() == saveAnaglyphAsBMPButton)
		{
			MyImageIO.selectDestinationAndSaveImage("Save anaglyph(.BMP)", getPlanarAnaglyph());
		}
	}
}
