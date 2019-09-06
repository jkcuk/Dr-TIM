package optics.raytrace.panorama.panorama3DViewer;

import javax.swing.JComboBox;

public class Panorama3DGeometryRenderingTypeComboBox extends JComboBox<Panorama3DGeometryRenderingType>
{
	private static final long serialVersionUID = 764833570860199066L;

	public Panorama3DGeometryRenderingTypeComboBox()
	{
		super(Panorama3DGeometryRenderingType.values());
	}

	public void setPanoramaGeometryType(Panorama3DGeometryRenderingType panoramaGeometryType)
	{
		setSelectedItem(panoramaGeometryType);
	}

	public Panorama3DGeometryRenderingType getPanoramaGeometryType()
	{
		return (Panorama3DGeometryRenderingType)getSelectedItem();
	}
}
