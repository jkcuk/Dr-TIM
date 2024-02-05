package optics.raytrace.research.refractiveTaylorSeries;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import math.Geometry;
import math.Vector3D;
import optics.raytrace.core.Ray;
import optics.raytrace.exceptions.RayTraceException;

public class SurfaceParametersOptimisation {

	int noOfIterations;
	RayPairsParameters rayPairsParameters;
	public enum AlgorithmType {
		RANDOM("Random"),
		SIMULATED_ANNEALING("Simulated annealing");
		
		private String description;
		AlgorithmType(String description) {this.description = description;}
		@Override
		public String toString() {return description;}
	}
	AlgorithmType algorithmType;
	double saInitialTemperature;	// for simulated annealing
	
	
	public SurfaceParametersOptimisation(
			int noOfIterations, 
			RayPairsParameters rayPairsParameters,
			AlgorithmType algorithmType, 
			double saInitialTemperature) {
		super();
		this.noOfIterations = noOfIterations;
		this.rayPairsParameters = rayPairsParameters;
		this.algorithmType = algorithmType;
		this.saInitialTemperature = saInitialTemperature;
	}
	
	public void writeParameters(PrintStream printStream)
	{
		// write any parameters not defined in NonInteractiveTIMEngine, each parameter is saved like this:
		// printStream.println("parameterName = "+parameterName);

		printStream.println("noOfIterations="+noOfIterations);
//		printStream.println("noOfDirectionPairs="+rayPairsParameters.noOfDirectionPairs);
//		printStream.println("Ray-direction pairs initialisation:");
//		printStream.println("  directionsInConeAngleDeg="+MyMath.rad2deg(rayPairsParameters.directionsInConeAngleRad));
//		printStream.println("  lawOfRefraction="+rayPairsParameters.lawOfRefraction.toString());
//		printStream.println("  noOfRaysPerBundle="+rayPairsParameters.noOfRaysPerBundle);
//		printStream.println("  rayStartPointsDiscRadius="+rayPairsParameters.rayStartPointsDiscRadius);
//
//		printStream.print("directionIn={");
//		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++) {
//			printStream.print(rayPairsParameters.directionsIn[i]);
//			if(i<rayPairsParameters.noOfDirectionPairs-1) printStream.print(", ");
//		}
//		printStream.println("}");
//
//		printStream.print("directionOut={");
//		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++) {
//			printStream.print(rayPairsParameters.directionsOut[i]);
//			if(i<rayPairsParameters.noOfDirectionPairs-1) printStream.print(", ");
//		}
//		printStream.println("}");
		printStream.println("algorithmType="+algorithmType);
		printStream.println("saInitialTemperature="+saInitialTemperature);
		printStream.println("noOfIterations="+noOfIterations);
	}
	
	
	//
	// optimisation
	//
	
	SimulatedAnnealingWorker simulatedAnnealingWorker;
	
	public void optimise(PolynomialPhaseHologramExplorer pphe)
	{
		pphe.setStatus("Optimising...");

		// create a suitable set of starting points
		// v2Ds = Geometry.getRandomPointsInUnitDisk(noOfPoints);
		
		// check if the rayStartPoints have  been initialised
		if(rayPairsParameters.rayStartPoints == null) rayPairsParameters.randomiseRayStartPoints();
				
		simulatedAnnealingWorker = new SimulatedAnnealingWorker(
				new SurfaceParametersAndMeanAligment(pphe.surfaceParameters, calculateMeanAlignment(pphe.surfaceParameters)), 
				pphe
			);
		simulatedAnnealingWorker.execute();
	}
	
	
	class SurfaceParametersAndMeanAligment
	{
		public SurfaceParameters surfaceParameters;
		public double meanAlignment;
		
		public SurfaceParametersAndMeanAligment(SurfaceParameters surfaceParameters, double meanAlignment) {
			super();
			this.surfaceParameters = surfaceParameters;
			this.meanAlignment = meanAlignment;
		}
	}
	
	class SimulatedAnnealingState
	{
		public SurfaceParametersAndMeanAligment s;
		public int iteration;
		public int maxIterations;
		public double T;
		
		public SimulatedAnnealingState(SurfaceParametersAndMeanAligment s, int iteration, int maxIterations, double T, long millis) {
			super();
			this.s = s;
			this.iteration = iteration;
			this.maxIterations = maxIterations;
			this.T = T;
		}
	}

	class SimulatedAnnealingWorker extends SwingWorker<SurfaceParametersAndMeanAligment, SimulatedAnnealingState> {
		SurfaceParametersAndMeanAligment s;
		PolynomialPhaseHologramExplorer pphe;
				
		public SimulatedAnnealingWorker(
				SurfaceParametersAndMeanAligment s, 
				PolynomialPhaseHologramExplorer pphe
				)
		{
			super();

			this.s = s;
			this.pphe = pphe;
		}

		@Override
		public SurfaceParametersAndMeanAligment doInBackground() {
			return simulatedAnnealing();
		}

		private long startTimeMillis;
		private long lastPublicationTime = -1;

		@Override
		protected void done() {
			try {
				SurfaceParametersAndMeanAligment s = get();
				pphe.meanAlignmentPanel.setNumber(s.meanAlignment);
				s.surfaceParameters.repopulateSurfaceParametersTabbedPane(pphe.surfaceParametersTabbedPane);
				int seconds = (int)Math.floor(1e-3*(System.currentTimeMillis() - startTimeMillis));
				int HH = seconds / 3600;
				int MM = (seconds % 3600) / 60;
				int SS = seconds % 60;
				double fraction = 1e-3*(System.currentTimeMillis() - startTimeMillis) - seconds;
				pphe.setStatus("Done.  Optimisation took "+
						((HH > 0)?String.format("%02dh ", HH):"") +
						((MM > 0)?String.format("%02dm ", MM):"") +
						String.format("%.2fs", SS + fraction)
						// 1e-3*(System.currentTimeMillis() - startTimeMillis)+"s."
				);
			}
			catch (Exception e) {
			}
			pphe.optimizeButton.setText(pphe.OPTIMIZE_BUTTON_OPTIMIZE);
		}
		
		private void myPublish(SimulatedAnnealingState sas, long time)
		{
			lastPublicationTime = time;
			publish(sas);
		}
		
		@Override
		protected void process(List<SimulatedAnnealingState> sass) {
			try {
				SimulatedAnnealingState sas = sass.get(sass.size() - 1);
				pphe.meanAlignmentPanel.setNumber(sas.s.meanAlignment);
				sas.s.surfaceParameters.repopulateSurfaceParametersTabbedPane(pphe.surfaceParametersTabbedPane);
				
				if(simulatedAnnealingWorker.isCancelled()) pphe.setStatus("Optimisation cancelled");
				else
					pphe.setStatus(
							"Optimising ("+
									String.format("%.0f", Math.floor(100*(double)(sas.iteration)/(double)sas.maxIterations))+
									"%, iteration "+sas.iteration+" out of "+sas.maxIterations+", T="+
									String.format("%.2e", sas.T)+
									")"
							);
			} catch (Exception ignore) {
			}
		}


		public SurfaceParametersAndMeanAligment simulatedAnnealing()
		{
			double T;
			startTimeMillis = System.currentTimeMillis();

			for (int i=0; (i<noOfIterations) && (!isCancelled()); i++) 
			{
				// create a new, modified, set of surface parameters...
				SurfaceParameters surfaceParameters2 = s.surfaceParameters.getNeighbouringSurfaceParameters();

				// ... and calculate its mean alignment
				double meanAlignment2 = calculateMeanAlignment(surfaceParameters2);

				// the "temperature" during this iteration (starts at 1, goes to zero)
				T = saInitialTemperature*Math.pow((1.-(((double)(i+1))/((double)noOfIterations))), 4);

				long time = System.currentTimeMillis();

				// if the neighbouring parameter set has a higher alignment, make that the new parameter set
				if(meanAlignment2 > s.meanAlignment)
				{
					// make the change
					// System.out.println("iteration "+i+", T="+T+", improvement: "+s.meanAlignment+" -> "+meanAlignment2);
					s.surfaceParameters = surfaceParameters2;
					s.meanAlignment = meanAlignment2;
					myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
				}
				else
				{
					// meanAlignment2 <= meanAlignment; make the change sometimes
					double deltaA = meanAlignment2 - s.meanAlignment;	// < 0
					// if(T*(1.-deltaH/minHappiness) + Math.random() > 1)
					// if(T*(1.-deltaA/s.meanAlignment) + Math.random() > 1)
					if(Math.random() < T*(1+deltaA/s.meanAlignment))
					{
						// make the change
						s.surfaceParameters = surfaceParameters2;
						s.meanAlignment = meanAlignment2;
						// System.out.println("Making things worse!");
						myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
					}
					else
					{
						if(time - lastPublicationTime > 100) 
							myPublish(new SimulatedAnnealingState(s, i, noOfIterations, T, time - startTimeMillis), time);
					}
				}
			}

			return s;
		}
	}
	
	public SurfaceParametersAndMeanAligment simulatedAnnealing(SurfaceParametersAndMeanAligment s)
	{
		double T;

		for (int i=0; i<noOfIterations; i++) 
		{
			// create a new, modified, set of surface parameters...
			SurfaceParameters surfaceParameters2 = s.surfaceParameters.getNeighbouringSurfaceParameters();

			// ... and calculate its mean alignment
			double meanAlignment2 = calculateMeanAlignment(surfaceParameters2);

			// the "temperature" during this iteration (starts at 1, goes to zero)
			T = saInitialTemperature*Math.pow((1.-(((double)(i+1))/((double)noOfIterations))), 4);

			// if the neighbouring parameter set has a higher alignment, make that the new parameter set
			if(meanAlignment2 > s.meanAlignment)
			{
				// make the change
				System.out.println("iteration "+i+", T="+T+", improvement: "+s.meanAlignment+" -> "+meanAlignment2);
				s.surfaceParameters = surfaceParameters2;
				s.meanAlignment = meanAlignment2;
			}
			else
			{
				// meanAlignment2 <= meanAlignment; make the change sometimes
				double deltaA = meanAlignment2 - s.meanAlignment;	// < 0
				// if(T*(1.-deltaH/minHappiness) + Math.random() > 1)
				// if(T*(1.-deltaA/s.meanAlignment) + Math.random() > 1)
				if(Math.random() < T*(1+deltaA/s.meanAlignment))
				{
					// make the change
					s.surfaceParameters = surfaceParameters2;
					s.meanAlignment = meanAlignment2;
					System.out.println("Making things worse!");
				}
			}
		}
		
		return s;
	}
	
	class MeanParallelnessCalculationWorker implements Runnable
	{
		public MeanParallelnessCalculationWorker(DirectionChangingSurfaceSequence dcss, double[] parallelnesses, int workerNo)
		{
			this.dcss = dcss;
			this.parallelnesses = parallelnesses;
			this.workerNo = workerNo;
		}
		
		private DirectionChangingSurfaceSequence dcss;
		private double[] parallelnesses;
		private int rayPairNo;
		private int workerNo;
		
		public void setRayPairNo(int rayPairNo) {this.rayPairNo = rayPairNo;}
		@Override
		public void run() {
			// construct a set of rays with the given starting points
			ArrayList<Vector3D> directionsOut = new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				try {
					directionsOut.add(
							dcss.calculateTransmittedRay(
									new Ray(
											rayStartPoint,	// start point
											rayPairsParameters.directionsIn[rayPairNo], 	// direction
											0,	// start time
											false	// reportToConsole
											)	// the incident ray
									).getD()	// the direction of the transmitted ray
							);
				} catch (RayTraceException e) {
					parallelnesses[workerNo] = -1;
					return;
				}

			// return the mean alignment
			parallelnesses[workerNo] = Geometry.calculateParallelness(directionsOut, true);
		}
	}

	/**
	 * Multi-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanParallelness(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(
				1	// transmissionCoefficient -- doesn't matter here
			);

		int nthreads=Runtime.getRuntime().availableProcessors();
		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
		
		MeanParallelnessCalculationWorker[] workers= new MeanParallelnessCalculationWorker[nthreads];
		double[] parallelnesses = new double[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new MeanParallelnessCalculationWorker(dcss, parallelnesses, i); //make an array of worker objects, which tell the threads what to do

		// go through all set of direction pairs
		double parallelnessSum = 0;
		int noOfDirectionsPairsSimulated = 0;
		for(int j=0; j<rayPairsParameters.noOfDirectionPairs; )
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[j] == Vector3D.NaV) 
			{
				j += 1;	// ignore this direction pair
				break;
			}

			Thread[] threads=new Thread[nthreads];
			int i;
			for(i=0; i<nthreads && j<rayPairsParameters.noOfDirectionPairs; i++) {
				threads[i]=new Thread(workers[i]); //make new threads for the workers
				workers[i].setRayPairNo(j);				//assign one ray pair to each worker object
				threads[i].start();						//and set them going
				j += 1;
			}
			
			int threadsRunning = i;
			try
			{
				for(i=0; i<threadsRunning; i++)
				{
					threads[i].join();	//wait for all the workers to finish
					parallelnessSum += parallelnesses[i];
					noOfDirectionsPairsSimulated += 1;
					if(Double.isNaN(parallelnessSum))
						System.out.println("parallelness = "+parallelnesses[i]+", parallelnessSum = "+parallelnessSum+", noOfDirectionsPairsSimulated = "+noOfDirectionsPairsSimulated);
				}
			}
			catch (InterruptedException e) {}
		}

		// calculate the mean alignment
		return parallelnessSum / noOfDirectionsPairsSimulated;
	}

	
	class MeanAlignmentCalculationWorker implements Runnable
	{
		public MeanAlignmentCalculationWorker(DirectionChangingSurfaceSequence dcss, double[] alignments, int workerNo)
		{
			this.dcss = dcss;
			this.alignments = alignments;
			this.workerNo = workerNo;
		}
		
		private DirectionChangingSurfaceSequence dcss;
		private double[] alignments;
		private int rayPairNo;
		private int workerNo;
		
		public void setRayPairNo(int rayPairNo) {this.rayPairNo = rayPairNo;}
		@Override
		public void run() {
			// construct a set of rays with these starting points
			ArrayList<Ray> raysIn = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				raysIn.add(
						new Ray(
								rayStartPoint,	// start point
								rayPairsParameters.directionsIn[rayPairNo], 	// direction
								0,	// start time
								false	// reportToConsole
								)
						);

			double alignmentSum = 0;
			int noOfAlignments = 0;
			for(Ray rayIn:raysIn)
			{
				try {
					// simulate transmission through the surfaces
					Ray rayOut = dcss.calculateTransmittedRay(rayIn);
					alignmentSum += Vector3D.scalarProduct(
							rayPairsParameters.directionsOut[rayPairNo], 
							rayOut.getD()
						);
				} catch (RayTraceException e) {
					// if the transmitted ray is evanescent, it will get reflected, so the alignment becomes negative;
					// assume the worst-case scenario (alignment = -1) -- we really don't want evanescent rays!
					alignmentSum -= 1;
				}
				noOfAlignments += 1;
			}
			
			// return the mean alignment
			alignments[workerNo] = alignmentSum / noOfAlignments;
			
//			System.out.println("alignmentSum = "+alignmentSum+", noOfAlignments = "+noOfAlignments);
			
//			// calculate the rays after transmission through the surfaces
//			ArrayList<Ray> raysOut = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
//			for(Ray ray:raysIn)
//				try {
//					raysOut.add(dcss.calculateTransmittedRay(ray));
//				}
//			catch (RayTraceException x) {
////				evanescentRays += 1;
//				// System.out.println("Evanescent ray!");
//			}
//
//			// we only care about the directions of the outgoing rays, which we collect into an ArrayList...
//			ArrayList<Vector3D> dOut =  new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
//			for(Ray ray:raysOut) dOut.add(ray.getD());
//
//			// ... whose alignment with the intended direction we then check
//			alignments[workerNo] = Geometry.calculateAlignment(rayPairsParameters.directionsOut[rayPairNo], dOut, true);
		}
	}

	/**
	 * Multi-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanAlignment(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(
				1	// transmissionCoefficient -- doesn't matter here
			);

		int nthreads=Runtime.getRuntime().availableProcessors();
		if(nthreads > 1) nthreads = nthreads - 1;	// leave one processor free to do GUI stuff
		
		MeanAlignmentCalculationWorker[] workers= new MeanAlignmentCalculationWorker[nthreads];
		double[] alignments = new double[nthreads];
		for(int i=0; i<nthreads; i++) workers[i]=new MeanAlignmentCalculationWorker(dcss, alignments, i); //make an array of worker objects, which tell the threads what to do

		// go through all set of direction pairs
		double alignmentSum = 0;
		int noOfDirectionsPairsSimulated = 0;
		for(int j=0; j<rayPairsParameters.noOfDirectionPairs; )
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[j] == Vector3D.NaV) 
			{
				j += 1;	// ignore this direction pair
				break;
			}

			Thread[] threads=new Thread[nthreads];
			int i;
			for(i=0; i<nthreads && j<rayPairsParameters.noOfDirectionPairs; i++) {
				threads[i]=new Thread(workers[i]); //make new threads for the workers
				workers[i].setRayPairNo(j);				//assign one ray pair to each worker object
				threads[i].start();						//and set them going
				j += 1;
			}
			
			int threadsRunning = i;
			try
			{
				for(i=0; i<threadsRunning; i++)
				{
					threads[i].join();	//wait for all the workers to finish
					alignmentSum += alignments[i];
					noOfDirectionsPairsSimulated += 1;
					if(Double.isNaN(alignmentSum))
						System.out.println("alignment = "+alignments[i]+", alignmentSum = "+alignmentSum+", noOfDirectionsPairsSimulated = "+noOfDirectionsPairsSimulated);
				}
			}
			catch (InterruptedException e) {}
		}

		// calculate the mean alignment
		return alignmentSum / noOfDirectionsPairsSimulated;
	}

	
	/**
	 * Single-threaded version
	 * @param testSurfaceParameters
	 * @return
	 */
	public double calculateMeanAlignment_parked(SurfaceParameters testSurfaceParameters)
	{
		DirectionChangingSurfaceSequence dcss = testSurfaceParameters.createCorrespondingDirectionChangingSurfaceSequence(
				1	// transmissionCoefficient -- doesn't matter here
			);
//		addSurfaces();
		
		// go through all set of direction pairs
		double alignmentSum = 0;
		int noOfDirectionsPairsSimulated = 0;
//		int evanescentRays = 0;
		for(int i=0; i<rayPairsParameters.noOfDirectionPairs; i++)
		{
			// check if the outgoing light-ray  direction exists
			if(rayPairsParameters.directionsOut[i] == Vector3D.NaV) continue;
			
			// construct a set of rays with these starting points
			ArrayList<Ray> raysIn = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Vector3D rayStartPoint:rayPairsParameters.rayStartPoints)
				raysIn.add(
						new Ray(
								rayStartPoint,	// start point
								rayPairsParameters.directionsIn[i], 	// direction
								0,	// start time
								false	// reportToConsole
								)
						);

			// calculate the rays after transmission through the surfaces
			ArrayList<Ray> raysOut = new ArrayList<Ray>(rayPairsParameters.noOfRaysPerBundle);
			for(Ray ray:raysIn)
				try {
					raysOut.add(dcss.calculateTransmittedRay(ray));
				}
			catch (RayTraceException x) {
//				evanescentRays += 1;
				// System.out.println("Evanescent ray!");
			}

//			ArrayList<Vector3D> dIn =  new ArrayList<Vector3D>(noOfPoints);
//			for(Ray ray:raysIn) dIn.add(ray.getD());

			// we only care about the directions of the outgoing rays, which we collect into an ArrayList...
			ArrayList<Vector3D> dOut =  new ArrayList<Vector3D>(rayPairsParameters.noOfRaysPerBundle);
			for(Ray ray:raysOut) dOut.add(ray.getD());

			//			// System.out.println("r="+r);
			//			System.out.println(
			//					"Input: "+raysIn.size()+" rays, parallelness "+String.format( "%.3f", Geometry.calculateParallelness(dIn, true))+
			//					"; output: "+raysOut.size()+" rays, parallelness "+String.format( "%.3f", Geometry.calculateParallelness(dOut, true))
			//					);

			// ... whose alignment with the intended direction we then check
			double  alignment = Geometry.calculateAlignment(rayPairsParameters.directionsOut[i], dOut, true);
//			System.out.println(
//					"Direction pair "+i+": alignment with "+rayPairsParameters.directionsOut[i]+" "+
//							String.format("%.3f", alignment)
//					);
			alignmentSum += alignment;
			noOfDirectionsPairsSimulated += 1;
		}

		double meanAlignment = alignmentSum / noOfDirectionsPairsSimulated;
//		System.out.println("Mean alignment with outgoing directions "+String.format("%.3f", meanAlignment));
//
//		double evanescentFraction = ((double)evanescentRays) / (rayPairsParameters.noOfDirectionPairs*noOfPoints);
//		System.out.println("Evanescent fraction "+String.format("%.3f", evanescentFraction));
		
		return meanAlignment;
	}
	

}
