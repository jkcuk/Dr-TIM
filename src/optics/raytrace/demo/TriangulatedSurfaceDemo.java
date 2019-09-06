package optics.raytrace.demo;

import java.io.PrintStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import math.*;
import optics.raytrace.NonInteractiveTIMActionEnum;
import optics.raytrace.NonInteractiveTIMEngine;
import optics.raytrace.GUI.cameras.RenderQualityEnum;
import optics.raytrace.GUI.lowLevel.ApertureSizeType;
import optics.raytrace.GUI.lowLevel.GUIBitsAndBobs;
import optics.raytrace.GUI.lowLevel.LabelledIntPanel;
import optics.raytrace.core.SurfaceProperty;
import optics.raytrace.core.SurfacePropertyPrimitive;
import optics.raytrace.exceptions.SceneException;
import optics.raytrace.sceneObjects.TriangulatedSurface;
import optics.raytrace.sceneObjects.solidGeometry.SceneObjectContainer;
import optics.raytrace.surfaces.RefractiveSimple;
import optics.raytrace.surfaces.SurfaceColour;
import optics.raytrace.surfaces.SurfaceOfTintedSolid;


/**
 * Based on NonInteractiveTIM.
 * 
 * @author Johannes Courtial
 */
public class TriangulatedSurfaceDemo extends NonInteractiveTIMEngine
{
	private boolean showVertices, showEdges, showSurface;
	private int iMax, jMax;
	
	public enum Shape {
		WAVE("Wave"), SPHERE("Sphere");
		
		private String description;
		private Shape(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	public Shape shape;

	public enum Material {
		GLASS("Glass"), YELLOW("Yellow"), TINTED("Tinted");
		
		private String description;
		private Material(String description)
		{
			this.description = description;
		}
		
		@Override
		public String toString() {return description;}
	};
	public Material material;

	
	/**
	 * Constructor.
	 * Sets all parameters.
	 */
	public TriangulatedSurfaceDemo()
	{
		super();
		
		showVertices = false;
		showEdges = false;
		showSurface = true;
		iMax = 10;
		jMax = 10;
		shape = Shape.WAVE;
		material = Material.YELLOW;
		
		renderQuality = RenderQualityEnum.DRAFT;
		
		nonInteractiveTIMAction = NonInteractiveTIMActionEnum.INTERACTIVE;

		// camera parameters are set in createStudio()
		cameraViewDirection = new Vector3D(0, -1, 1);
		cameraViewCentre = new Vector3D(0, 0, 0);
		cameraDistance = 10;	// camera is located at (0, 0, 0)
		cameraFocussingDistance = 10;
		cameraHorizontalFOVDeg = 10;
		cameraMaxTraceLevel = 100;
		cameraPixelsX = 640;
		cameraPixelsY = 480;
		cameraApertureSize = ApertureSizeType.PINHOLE;
				// ApertureSizeType.SMALL;
	}

	
	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#getFirstPartOfFilename()
	 */
	@Override
	public String getFirstPartOfFilename()
	{
		return
				"TriangulatedSurfaceDemo"
				;
	}

	@Override
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		// write all parameters defined in NonInteractiveTIMEngine
		super.writeParameters(printStream);
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.NonInteractiveTIMEngine#populateStudio()
	 */
	@Override
	public void populateStudio()
	throws SceneException
	{
		super.populateSimpleStudio();
		
		// add anything to the scene by uncommenting the following line...
		SceneObjectContainer scene = (SceneObjectContainer)studio.getScene();
		
		// ... and then adding scene objects to scene
		
		Vector3D[][] p = new Vector3D[iMax][jMax];
		for(int i=0; i<iMax; i++)
			for(int j=0; j<jMax; j++)
				switch(shape)
				{
				case WAVE:
					double x = -0.5 + ((double)i)/(iMax-1);
					double z = -0.5 + ((double)j)/(jMax-1);
					p[i][j] = new Vector3D(x, 0.1*Math.sin(4.*Math.PI*Math.sqrt(x*x+z*z)), z);
					break;
				case SPHERE:
				default:
					double phi = 2.*Math.PI*((double)i)/(iMax-1);
					double theta = Math.PI*((double)j)/(jMax-1);
					double r = 0.5;
					p[i][j] = new Vector3D(r*Math.sin(theta)*Math.cos(phi), r*Math.sin(theta)*Math.sin(phi), r*Math.cos(theta));
				}
		
		SurfaceProperty surfaceProperty;
		switch(material)
		{
		case GLASS:
			surfaceProperty = new RefractiveSimple(
					1.3,	// insideOutsideRefractiveIndexRatio
					SurfacePropertyPrimitive.DEFAULT_TRANSMISSION_COEFFICIENT,	// transmissionCoefficient
					true	// shadowThrowing
				);
			break;
		case TINTED:
			surfaceProperty = new SurfaceOfTintedSolid(
					0,	// redAbsorptionCoefficient
					1.,	// greenAbsorptionCoefficient
					1.,	// blueAbsorptionCoefficient
					true	// shadowThrowing
				);
			break;
		case YELLOW:
		default:
			surfaceProperty = SurfaceColour.YELLOW_SHINY;
		}
		TriangulatedSurface t = new TriangulatedSurface(
				"Test surface",	// description
				p,
				false, 
				surfaceProperty,
				scene,	// parent
				studio
			);

		scene.addSceneObject(t.getArrayOfSpheresAtVertices(0.15/iMax, scene, studio), showVertices);
		
		scene.addSceneObject(t.getArrayOfArrowsAtEdges(0.075/iMax, scene, studio), showEdges);
		
		scene.addSceneObject(t, showSurface);
	}

	
	
	//
	// for interactive version
	//

	private JCheckBox showVerticesCheckbox;
	private JCheckBox showEdgesCheckbox;
	private JCheckBox showSurfaceCheckbox;
	private LabelledIntPanel iMaxPanel, jMaxPanel;
	private JComboBox<Shape> shapeComboBox;
	private JComboBox<Material> materialComboBox;



	/**
	 * add controls to the interactive control panel;
	 * override to modify
	 */
	@Override
	protected void createInteractiveControlPanel()
	{
		super.createInteractiveControlPanel();
		
		shapeComboBox = new JComboBox<Shape>(Shape.values());
		shapeComboBox.setSelectedItem(shape);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Shape", shapeComboBox), "span");

		materialComboBox = new JComboBox<Material>(Material.values());
		materialComboBox.setSelectedItem(material);
		interactiveControlPanel.add(GUIBitsAndBobs.makeRow("Material", materialComboBox), "span");

		showVerticesCheckbox = new JCheckBox("Show vertices");
		showVerticesCheckbox.setSelected(showVertices);
		interactiveControlPanel.add(showVerticesCheckbox, "span");

		showEdgesCheckbox = new JCheckBox("Show edges");
		showEdgesCheckbox.setSelected(showEdges);
		interactiveControlPanel.add(showEdgesCheckbox, "span");

		showSurfaceCheckbox = new JCheckBox("Show surface");
		showSurfaceCheckbox.setSelected(showSurface);
		interactiveControlPanel.add(showSurfaceCheckbox, "span");

		iMaxPanel = new LabelledIntPanel("# vertices in i direction");
		iMaxPanel.setNumber(iMax);
		interactiveControlPanel.add(iMaxPanel, "span");

		jMaxPanel = new LabelledIntPanel("# vertices in j direction");
		jMaxPanel.setNumber(jMax);
		interactiveControlPanel.add(jMaxPanel, "span");
	}
	
	/**
	 * called before rendering;
	 * override when adding fields
	 */
	@Override
	protected void acceptValuesInInteractiveControlPanel()
	{
		super.acceptValuesInInteractiveControlPanel();

		shape = (Shape)(shapeComboBox.getSelectedItem());
		material = (Material)(materialComboBox.getSelectedItem());

		showVertices = showVerticesCheckbox.isSelected();
		showEdges = showEdgesCheckbox.isSelected();
		showSurface = showSurfaceCheckbox.isSelected();
		iMax = iMaxPanel.getNumber();
		jMax = jMaxPanel.getNumber();
	}
	
		
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
//        Runnable r = new NonInteractiveTIM();
//
//        EventQueue.invokeLater(r);
		(new TriangulatedSurfaceDemo()).run();
	}
}
