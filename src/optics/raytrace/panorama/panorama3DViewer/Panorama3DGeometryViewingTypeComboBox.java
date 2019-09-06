package optics.raytrace.panorama.panorama3DViewer;

import javax.swing.JComboBox;

public class Panorama3DGeometryViewingTypeComboBox extends JComboBox<Panorama3DGeometryViewingType>
{
	private static final long serialVersionUID = 1670802801565022060L;

	public Panorama3DGeometryViewingTypeComboBox()
	{
		super(Panorama3DGeometryViewingType.values());
	}

	public void setPanoramaGeometryType(Panorama3DGeometryViewingType panoramaGeometryType)
	{
		setSelectedItem(panoramaGeometryType);
	}

	public Panorama3DGeometryViewingType getPanoramaGeometryType()
	{
		return (Panorama3DGeometryViewingType)getSelectedItem();
	}
}
