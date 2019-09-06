package optics.raytrace.GUI.lowLevel;


import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import optics.raytrace.GUI.cameras.QualityType;


/**
 * Allows choice of blur parameters.
 */
public class BlurPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private LabelledApertureSizeComboBox apertureSizePanel;
	private LabelledQualityComboBox blurQualityPanel;

	public BlurPanel()
	{
		super();
		setLayout(new MigLayout("insets 0"));
		// setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		// setBorder(GUIBitsAndBobs.getTitledBorder("Blur"));

		apertureSizePanel = new LabelledApertureSizeComboBox("Aperture size");
		// add a listener here as the blur quality only matters if the aperture size is not "Pinhole"
		// CORRECTION: this is no longer true, as the blur quality also matters when simulating artefacts that lead to
		// blurring, such as pixellation effects of GCLAs
		// apertureSizePanel.addActionListener(new ApertureSizeListener());
		add(apertureSizePanel);
		
		blurQualityPanel = new LabelledQualityComboBox("Blur quality");
		add(blurQualityPanel);

		validate();
		// repaint();
		
		// setPreferredSize(new Dimension(400, 140));
	}

	public void setBlur(ApertureSizeType apertureSize, QualityType blurQuality)
	{
		apertureSizePanel.setApertureSize(apertureSize);
		blurQualityPanel.setQuality(blurQuality);

		// the blur quality only matters if the aperture size is not "Pinhole"
		// CORRECTION: this is no longer true, as the blur quality also matters when simulating artefacts that lead to
		// blurring, such as pixellation effects of GCLAs
		// blurQualityPanel.setEnabled(!apertureSize.equals(ApertureSizeType.PINHOLE));
	}

	public ApertureSizeType getApertureSize()
	{
		return apertureSizePanel.getApertureSize();
	}
	
	public QualityType getBlurQuality()
	{
		return blurQualityPanel.getQuality();
	}
	
//	/**
//	 * A little inner class
//	 */
//	class ApertureSizeListener implements ActionListener
//	{
//		public void actionPerformed(ActionEvent e)
//		{
//			ApertureSizeType apertureSize = apertureSizePanel.getApertureSize();
//			
//			// the blur quality only matters if the aperture size is not "Pinhole"
//			blurQualityPanel.setEnabled(!apertureSize.equals(ApertureSizeType.PINHOLE));
//		}
//	}
}