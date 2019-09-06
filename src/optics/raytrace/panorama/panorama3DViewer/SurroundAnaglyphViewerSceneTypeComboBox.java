package optics.raytrace.panorama.panorama3DViewer;

import javax.swing.JComboBox;

public class SurroundAnaglyphViewerSceneTypeComboBox extends JComboBox<SurroundAnaglyphViewerSceneType>
{
	private static final long serialVersionUID = -3776476611729545080L;

	public SurroundAnaglyphViewerSceneTypeComboBox()
	{
		super(SurroundAnaglyphViewerSceneType.values());
	}

	public void setSurroundAnaglyphViewerSceneType(SurroundAnaglyphViewerSceneType surroundAnaglyphViewerSceneType)
	{
		setSelectedItem(surroundAnaglyphViewerSceneType);
	}

	public SurroundAnaglyphViewerSceneType getSurroundAnaglyphViewerSceneType()
	{
		return (SurroundAnaglyphViewerSceneType)getSelectedItem();
	}
}
