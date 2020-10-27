package optics.raytrace.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import optics.raytrace.GUI.core.RaytraceWorker;


/**
 * Everything that is needed to calculate a photo: a scene, the lights, and a camera.
 *  
 * @author Johannes Courtial
 */
public class Studio implements Serializable
{
	private static final long serialVersionUID = -5261679135578662113L;
	
	public static final Studio NULL_STUDIO = new Studio();
	
	protected SceneObject scene;
	protected LightSource lights;
	protected CameraClass camera;

	/**
	 * Create a new Studio object
	 * 
	 * @param scene	the scene to render
	 * @param lights	the lights illuminating the scene
	 * @param camera	the camera
	 */
	public Studio(SceneObject scene, LightSource lights, CameraClass camera)
	{
		super();
		this.scene = scene;
		this.lights = lights;
		this.camera = camera;
	}
	
	/**
	 * Create a new, empty, Studio object with default raytrace-exception handling.
	 * Use the setScene, setLights, and setCamera methods to populate it.
	 * 
	 * @see optics.raytrace.core.Studio#setScene(optics.raytrace.core.SceneObject)
	 * @see optics.raytrace.core.Studio#setLights(optics.raytrace.core.LightSource)
	 * @see optics.raytrace.core.Studio#setCamera(optics.raytrace.core.CameraClass)
	 */
	public Studio()
	{
		super();
	}
	
	/**
	 * This method iterates through every pixel in the scene, calls calculateColour 
	 * and stores each value in a buffered image which is then returned.
	 * 
	 * While this is the obvious place to render stuff, a slightly 
	 * modified version of the code is in RenderWorker for UI related
	 * purposes.   For all other cases just use this method.
	 * @return An image of the scene.
	 */
	public BufferedImage takePhoto()
	{
		// camera.allocatePhotoMemory();
		return camera.takePhoto(scene, lights);
	}

	public BufferedImage takePhoto(RaytraceWorker raytraceWorker)
	{
		// camera.allocatePhotoMemory();
		// System.out.println("camera = "+camera);
		return camera.takePhoto(scene, lights, raytraceWorker);
	}
	
	/**
	 * trace the display rays
	 */
	public void traceRaysWithTrajectory()
	{
		RayWithTrajectory.traceRaysWithTrajectory(getScene());
	}


	/**
	 * Save a photo that was previously taken (and which is now
	 * the detector image) in a given format.  
	 * Possible formats include all those mentioned in javax.imageio.ImageIO.write,
	 * plus CSV (comma-separated).
	 * @param filename The name of the file that the image is saved as.
	 * @param format The format of the image.
	 */
	public void savePhoto(String filename, String format) {
		camera.savePhoto(filename, format);
	}

	
	// setters and getters

	public SceneObject getScene() {
		return scene;
	}

	public void setScene(SceneObject scene) {
		this.scene = scene;
	}

    public LightSource getLights() {
		return lights;
	}

	public void setLights(LightSource lights) {
		this.lights = lights;
	}

	public CameraClass getCamera() {
		return camera;
	}

	public void setCamera(CameraClass camera) {
		this.camera = camera;
	}
	
	/**
	 * get the (previously calculated) photo as a BufferedImage
	 * 
	 * @return the image
	 */
	public BufferedImage getPhoto()
	{
		return camera.getPhoto();
	}
	
	// loading and saving

	/**
	 * Load a studio file that has been saved.
	 *
	 * @param input A *.world file to load containing lights, camera and objects.
	 * @return If all goes well, the loaded photo-description file.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private static Studio load(FileInputStream input) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(input);
		Studio s = (Studio)in.readObject();
		in.close();
		
		return s;
	}

	/**
	 * Load a studio file that has been saved.
	 *
	 * @param filename The name of a *.world file to load containing lights, camera and objects.
	 * @return If all goes well, the loaded studio, otherwise null.
	 */
	public static Studio load(String filename) {
		Studio s = Studio.NULL_STUDIO;
		try {
			s = load(new FileInputStream(filename));
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return s;
	}


	/**
	 * Load a studio file that has been saved.
	 *
	 * @param file A *.world file to load containing lights, camera and objects.
	 * @return If all goes well, the loaded studio, otherwise null.
	 */
	public static Studio load(File file) {
		Studio s = Studio.NULL_STUDIO;
		try {
			s = load(new FileInputStream(file));
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return s;
	}


	/**
	 * Save a file containing lights and scene as well as the camera
	 * @param file The file in which to save the data
	 */
	public void save(File file) {
		try {
			FileOutputStream fo = new FileOutputStream(file);
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(this);
			so.flush();
			so.close();
		} catch (Exception e) {
			System.err.println("Studio::save: Error while saving file `"+file.getName()+"'.");
			e.printStackTrace();
			// System.exit(1);
		}
	}

	/**
	 * Save a file containing lights and scene as well as the camera
	 * @param filename The file in which to save the data
	 */
	public void save(String filename) {
		try {
			FileOutputStream fo = new FileOutputStream(filename);
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(this);
			so.flush();
			so.close();
		} catch (Exception e) {
			System.err.println("Studio::save: Error while saving file `"+filename+"'.");
			e.printStackTrace();
			// System.exit(1);
		}
	}
}