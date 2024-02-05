package optics.raytrace.core;

import java.awt.image.*;

import optics.DoubleColour;
import optics.raytrace.GUI.core.*;
import optics.raytrace.exceptions.EvanescentException;
import optics.raytrace.exceptions.RayTraceException;
import math.Vector3D;

/**
 * Represents a camera.
 * This consists of a detector array, and a correspondence between detector-array pixels and
 * light rays, provided by the "getRayForPixel" method
 * (in a real camera, this correspondence is provided by the lens).
 * 
 * @author Johannes Courtial, Richard Bowman
 */
public abstract class CameraClass implements Camera
{
	private static final long serialVersionUID = 3246999291427852372L;

	protected String description;
	protected int maxTraceLevel;
	protected CentredCCD ccd;
	protected RaytraceExceptionHandler raytraceExceptionHandler;
	          
	/**
	 * Create a new camera
	 * 
	 * @param description
	 * @param detectorCentre	centre of the detector array
	 * @param horizontalSpanVector3D	Vector3D running along the width of the detector array
	 * @param verticalSpanVector3D	Vector3D running along the height of the detector array
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 * @param maxTraceLevel	iteration depth
	 * @param raytraceExceptionHandler
	 */
	public CameraClass(
		String description,
		Vector3D detectorCentre,
		Vector3D horizontalSpanVector3D,
		Vector3D verticalSpanVector3D,
		int detectorPixelsHorizontal,
		int detectorPixelsVertical,
		int maxTraceLevel,
		RaytraceExceptionHandler raytraceExceptionHandler
	)
	{
		ccd = new CentredCCD(
			detectorCentre,
			horizontalSpanVector3D, verticalSpanVector3D,
			detectorPixelsHorizontal, detectorPixelsVertical
		);
		this.maxTraceLevel = maxTraceLevel;
		this.description = description;
		this.raytraceExceptionHandler = raytraceExceptionHandler;
	}
	
	/**
	 * Create a new camera with a standard RaytraceExceptionHandler
	 * 
	 * @param description
	 * @param detectorCentre	centre of the detector array
	 * @param horizontalSpanVector3D	Vector3D running along the width of the detector array
	 * @param verticalSpanVector3D	Vector3D running along the height of the detector array
	 * @param detectorPixelsHorizontal	number of detector pixels in the horizontal direction
	 * @param detectorPixelsVertical	number of detector pixels in the vertical direction
	 * @param maxTraceLevel	iteration depth
	 */
	public CameraClass(
		String description,
		Vector3D detectorCentre,
		Vector3D horizontalSpanVector3D,
		Vector3D verticalSpanVector3D,
		int detectorPixelsHorizontal,
		int detectorPixelsVertical,
		int maxTraceLevel
	)
	{
		this(
				description,
				detectorCentre,
				horizontalSpanVector3D,
				verticalSpanVector3D,
				detectorPixelsHorizontal,
				detectorPixelsVertical,
				maxTraceLevel,
				new DefaultRaytraceExceptionHandler()
			);
	}
	
	/**
	 * default constructor;
	 * everything needs to be initialised later
	 */
//	public Camera()
//	{}

	/**
	 * Create a clone of the original
	 * @param original
	 */
	public CameraClass(CameraClass original)
	{
		ccd = original.ccd.clone();
		description = original.getDescription();
		maxTraceLevel = original.getMaxTraceLevel();
		raytraceExceptionHandler = original.getRaytraceExceptionHandler();
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.sceneObjects.ParametrisedSphere#clone()
	 */
	@Override
	public abstract CameraClass clone();

	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#getRayForPixel(int, int)
	 */
	@Override
	public abstract Ray getRayForPixel(double i, double j);

	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#getName()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Set this camera's description
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The max trace level is the number of iterations of propagation
	 * that any light ray may undergo before being ignored as insignificant
	 * or computationally too expensive.
	 */
	public int getMaxTraceLevel()
	{
		return maxTraceLevel;
	}

	/**
	 * The max trace level is the number of iterations of propagation
	 * that any light ray may undergo before being ignored as insignificant
	 * or computationally too expensive.
	 * @param maxTraceLevel The number of iterations a ray may undergo
	 */
	public void setMaxTraceLevel(int maxTraceLevel)
	{
		this.maxTraceLevel = maxTraceLevel;
	}

	public CentredCCD getCCD() {
		return ccd;
	}

	public void setCCD(CentredCCD ccd) {
		this.ccd = ccd;
	}

	public int getDetectorPixelsHorizontal()
	{
		return ccd.getDetectorPixelsHorizontal();
	}

	public void setDetectorPixelsHorizontal(int detectorPixelsHorizontal)
	{
		ccd.setDetectorPixelsHorizontal(detectorPixelsHorizontal);
	}

	public int getDetectorPixelsVertical()
	{
		return ccd.getDetectorPixelsVertical();
	}
	
	public void setDetectorPixelsVertical(int detectorPixelsVertical)
	{
		ccd.setDetectorPixelsVertical(detectorPixelsVertical);
	}

	public void setDetectorCentre(Vector3D detectorCentre)
	{
		ccd.setCentrePosition(detectorCentre);
		validate();
	}
	
	public Vector3D getDetectorCentre()
	{
		return ccd.getCentrePosition();
	}
	
	public void setSpanVectors(Vector3D horizontalSpanVector, Vector3D verticalSpanVector)
	{
		ccd.setSpanVectors(horizontalSpanVector, verticalSpanVector);
		validate();
	}
	
	public Vector3D getHorizontalSpanVector()
	{
		return ccd.getHorizontalSpanVector();
	}

	public Vector3D getVerticalSpanVector()
	{
		return ccd.getVerticalSpanVector();
	}
	
	/**
	 * make sure all the settings are consistent
	 */
	public void validate()
	{
		ccd.validate();
	}

	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#calculatePixelColour(double, double, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource)
	 */
	@Override
	public abstract DoubleColour calculatePixelColour(double i, double j, SceneObject scene, LightSource lights)
	throws RayTraceException;
	/**
	 * This implementation adds the behaviour of a pinhole camera, which traces the ray for pixel (i, j) through the scene.
	 * Override to implement non-standard behaviour.
	 * @see optics.raytrace.core.Camera#calculatePixelColour(int, int, optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource)
	 */
//	{
//		Ray r=getRayForPixel(i,j);
//
//		return scene.getColour(r, lights, scene, getMaxTraceLevel(),
//				getRaytraceExceptionHandler());
//	}


	/**
	 * This method allocates the memory to store the photo and returns the (empty) photo.
	 * @return The empty photo.
	 */
	public BufferedImage allocatePhotoMemory()
	{
		ccd.allocateImageMemory();
		return ccd.getImage();
	}
	
	/* (non-Javadoc)
	 * (multi-threading code by Richard Bowman)
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource, optics.raytrace.GUI.core.RaytraceWorker)
	 */
	@Override
	public BufferedImage takePhoto(
			SceneObject scene,
			LightSource lights,
			RaytraceWorker raytraceWorker
		)
	{
		// check if the memory for the image has been allocated correctly;
		// if not, allocate the memory
		BufferedImage image = ccd.getImage();
		if(image == null)
		{
			// no image memory has been allocated, so do it now
			ccd.allocateImageMemory();
		}
		else if(
			(image.getWidth() != ccd.getDetectorPixelsHorizontal()) ||
			(image.getHeight() != ccd.getDetectorPixelsVertical())
		)
		{
			// the allocated memory has the wrong size or shape; re-allocate the memory
			ccd.allocateImageMemory();
		}
		
		class CameraWorker implements Runnable
		{
			public CameraWorker(CCD ccd, SceneObject scene, LightSource lights)
			{
				this.ccd=ccd;
				this.scene=scene;
				this.lights=lights;
			}
			
			private CCD ccd;
			private SceneObject scene;
			private LightSource lights;
			private int lineToRender;
			
			public void setLineToRender(int j) {lineToRender=j;}
			@Override
			public void run(){
				//render a line
				for (int i=0; i<ccd.getDetectorPixelsHorizontal(); i++) {
					DoubleColour c;
					try {
						c = calculatePixelColour(i, lineToRender, scene, lights);
					} catch (RayTraceException e) {
						if(e instanceof EvanescentException)
							c = DoubleColour.BLACK;
						else
						{
							c = DoubleColour.YELLOW;
							e.printStackTrace();
						}
					}
					catch(Exception e)
					{
						// if there is another exception, catch it here so that the rest of the line is rendered;
						// but note that this should not happen!
						c = DoubleColour.YELLOW;
						e.printStackTrace();
					}

					ccd.setPixelColour(i, lineToRender, c.getRGB());
				}
			}
		}
		
		
		int nthreads=Runtime.getRuntime().availableProcessors();
		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
		
		CameraWorker[] workers= new CameraWorker[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new CameraWorker(ccd,scene,lights); //make an array of worker objects, which tell the threads what to do

		long latestImageUpdateTimeMillis = -1;

		// now calculate the image
		for (int j=0; j<ccd.getDetectorPixelsVertical(); j+=nthreads) {
			Thread[] threads=new Thread[nthreads];
			for(int i=0; i<nthreads; i++) threads[i]=new Thread(workers[i]); //make new threads for the workers
			for(int i=0; i<nthreads && i+j<ccd.getDetectorPixelsVertical(); i++){
				workers[i].setLineToRender(j+i);				//assign one line of the image to each worker object
				threads[i].start();								//and set them going
			}
			try
			{
				for(int i=0; i<nthreads; i++) threads[i].join();	//wait for all the workers to finish
			}
			catch (InterruptedException e)
			{
				// don't do anything, assuming (hoping?) that someone clicked the "Stop" button
				// System.out.println("CameraClass::takePhoto: ");
				// e.printStackTrace();
			}
			
			// is this a RaytraceWorker?
			if(raytraceWorker != null)
			{
				// check whether the calculation has been cancelled
				if(raytraceWorker.isCancelled()) return ccd.getImage();

				//
				// give some feedback on progress
				//
				
				// the simplest way of giving feedback (note that the method being called
				// has to be thread-safe!)
				raytraceWorker.setStatus(description + " rendering... (on "+nthreads+" processors/cores; " + 100*(j+1)/ccd.getDetectorPixelsVertical() + "% completed.)");

				// check how long it has been since the latest image update
				if((System.currentTimeMillis() - latestImageUpdateTimeMillis) > 100)
				{
					// it's been less than 100ms --- update the image again
					raytraceWorker.showIntermediateImage(ccd.getImage());
					latestImageUpdateTimeMillis = System.currentTimeMillis();
				}
				
				// the proper way of giving feedback, which doesn't work though...
				// raytraceWorker.setProgress((int)(100.*j/ccd.getDetectorPixelsVertical()));
				// raytraceWorker.publish();	// doesn't work
			}
			else
			{
				// otherwise print feedback onto the console
				System.out.println("Rendering line " + j + " out of " + ccd.getDetectorPixelsVertical() + " (on "+nthreads+" processors/cores)");
			}
		}
		return ccd.getImage();
	}

//	public BufferedImage takePhoto(
//			SceneObject scene,
//			LightSource lights,
//			RaytraceWorker raytraceWorker
//		)
//	{
//		// check if the memory for the image has been allocated correctly;
//		// if not, allocate the memory
//		BufferedImage image = ccd.getImage();
//		if(image == null)
//		{
//			// no image memory has been allocated, so do it now
//			ccd.allocateImageMemory();
//		}
//		else if(
//			(image.getWidth() != ccd.getDetectorPixelsHorizontal()) ||
//			(image.getHeight() != ccd.getDetectorPixelsVertical())
//		)
//		{
//			// the allocated memory has the wrong size or shape; re-allocate the memory
//			ccd.allocateImageMemory();
//		}
//		
//		// System.out.println("size = "+ccd.getDetectorPixelsHorizontal()+" x "+ccd.getDetectorPixelsVertical());
//		
//		// now calculate the image
//		for (int j=0; j<ccd.getDetectorPixelsVertical(); j++) {
//			for (int i=0; i<ccd.getDetectorPixelsHorizontal(); i++) {
//				DoubleColour c;
//				try {
//					c = calculatePixelColour(i, j, scene, lights);
//				} catch (RayTraceException e) {
//					if(e instanceof EvanescentException)
//						c = DoubleColour.BLACK;
//					else
//					{
//						c = DoubleColour.YELLOW;
//						e.printStackTrace();
//					}
//				}
//				ccd.setPixelColour(i, j, c.getRGB());
//			}
//			
//			// is there is a RaytraceWorker?
//			if(raytraceWorker != null)
//			{
//				// check whether the calculation has been cancelled
//				if(raytraceWorker.isCancelled()) return ccd.getImage();
//
//				//
//				// give some feedback on progress
//				//
//				
//				// the simplest way of giving feedback (note that the method being called
//				// has to be thread-safe!)
//				raytraceWorker.setStatus(description + " rendering... (" + (int)(100*(j+1)/ccd.getDetectorPixelsVertical()) + "% completed.)");
//
//				// the proper way of giving feedback, which doesn't work though...
//				// raytraceWorker.setProgress((int)(100.*j/ccd.getDetectorPixelsVertical()));
//				// raytraceWorker.publish();	// doesn't work
//			}
//			else
//			{
//				// otherwise print feedback onto the console
//				System.out.println("Rendering line " + j + " out of " + ccd.getDetectorPixelsVertical());
//			}
//		}
//		return ccd.getImage();
//	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#takePhoto(optics.raytrace.core.SceneObject, optics.raytrace.core.LightSource)
	 */
	@Override
	public BufferedImage takePhoto(SceneObject scene, LightSource lights)
	{
		return takePhoto(scene, lights, null);
	}
	
	/* (non-Javadoc)
	 * @see optics.raytrace.core.Camera#getPhoto()
	 */
	@Override
	public BufferedImage getPhoto()
	{
		return ccd.getImage();
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
		ccd.saveImage(filename, format);
	}

	@Override
	public RaytraceExceptionHandler getRaytraceExceptionHandler() {
		return raytraceExceptionHandler;
	}

	public void setRaytraceExceptionHandler(
			RaytraceExceptionHandler raytraceExceptionHandler) {
		this.raytraceExceptionHandler = raytraceExceptionHandler;
	}
}
