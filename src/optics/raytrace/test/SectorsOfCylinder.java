package optics.raytrace.test;

import math.Vector3D;
import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.ParametrisedCylinder;
import optics.raytrace.sceneObjects.ParametrisedCylinderMantle;
import optics.raytrace.sceneObjects.Plane;
import optics.raytrace.voxellations.Voxellation;

/**
	 * @author e orife, Johannes Courtial
	 * 
	 * The sectors of a cylinder.  Basically, these sections are cake slices of an infinite cake.
	 * 
	 * The number of sectors is given by the variable numberOfSectors;
	 * each sector has an angle 2 pi / numberOfSectors.
	 * The line where all the sectors meet is called the axis, and it is defined by a point on the axis
	 * and the axis direction.
	 * There are also two more direction vectors, zeroDegreeDirection and ninetyDegreeDirection, which are
	 * perpendicular to the axis direction.  Voxel #0 starts at zeroDegreeDirection.
	 * The 90-degree direction is calculated as zeroDegreeDirection x axisDirection.
	 */ 
	public class SectorsOfCylinder extends Voxellation
	{	

		private static final long serialVersionUID = 726449539802288547L;
		
		protected int numberOfSectors;
		protected Vector3D
			pointOnAxis,
			normalisedAxisDirection,
			normalised0DegreeDirection,
			normalised90DegreeDirection;
		
		// constructor(s)
		
		/**
		 * @param pointOnAxis
		 * @param axisDirection
		 * @param zeroDegreeDirection
		 * @param numberOfSectors
		 */
		public SectorsOfCylinder(Vector3D pointOnAxis, Vector3D axisDirection, Vector3D zeroDegreeDirection, int numberOfSectors)
		{
			super();
			
			setPointOnAxis(pointOnAxis);
			setDirections(axisDirection, zeroDegreeDirection);
			setNumberOfSectors(numberOfSectors);
		}
		
		/**
		 * @param cylinderMantle
		 * @param numberOfSectors
		 */
		public SectorsOfCylinder(ParametrisedCylinderMantle cylinderMantle, int numberOfSectors)
		{	
			this(
					cylinderMantle.getStartPoint(),	// point on axis
					cylinderMantle.getAxis(),	// axis direction
					cylinderMantle.getZeroDeg(),	// zeroDegreeDirection
					numberOfSectors
			);
		}

		public SectorsOfCylinder(ParametrisedCylinder cylinder, int numberOfSectors)
		{	
			this(
					cylinder.getParametrisedCylinderMantle(),
					numberOfSectors
			);
		}

		// setters & getters
		
		/**
		 * @param axisDirection
		 * @param zeroDegreeDirection
		 */
		public void setDirections(Vector3D axisDirection, Vector3D zeroDegreeDirection)
		{
			// set the axis direction (normalised)
			normalisedAxisDirection = axisDirection.getNormalised();
			
			// the zero-angle direction has to be perpendicular to the axis direction; make sure it is!
			normalised0DegreeDirection = zeroDegreeDirection.getPartPerpendicularTo(normalisedAxisDirection).getNormalised();
			
			// also set the 90-degree direction
			normalised90DegreeDirection = Vector3D.crossProduct(normalised0DegreeDirection, normalisedAxisDirection);
			
//			System.out.println("SectorsOfCylinder::setDirections: normalisedAxisDirection = " + normalisedAxisDirection + ", normalised0DegreeDirection = " + normalised0DegreeDirection + ", normalised90DegreeDirection = " + normalised90DegreeDirection);
//			System.exit(-1);
		}
		
		public Vector3D getPointOnAxis() {
			return pointOnAxis;
		}

		public void setPointOnAxis(Vector3D pointOnAxis) {
			this.pointOnAxis = pointOnAxis;
		}

		/**
		 * @return the no. of sectors
		 */
		public int getNumberOfSectors() {
			return numberOfSectors;
		}

		/**
		 * @param numberOfSectors the no. of sectors to set
		 */
		public void setNumberOfSectors(int numberOfSectors) {
			this.numberOfSectors = numberOfSectors;
		}

		@Override
		public int getVoxelIndex(Vector3D position)
		{
			double angle = getAzimuthalAngle(position);
			// make sure the angle is positive
			if(angle < 0) angle += 2*Math.PI;

			return (int)Math.floor(angle/(2.*Math.PI)*getNumberOfSectors());
		}
		
		public double getAngle(int voxelIndex) {
			
			return (voxelIndex+0.5)*2.*Math.PI/numberOfSectors;
		}

		@Override
		public SceneObject getSurfaceOfVoxel(int index)
		throws IndexOutOfBoundsException
		{
			EditableSceneObjectCollection surface = new EditableSceneObjectCollection(
					"surface of voxel #" + index,
					true,
					null,	// parent
					null	// studio
				);

			// direction of the normal to surface 1, in terms of the angle w.r.t. the 0 degree direction
			double angleOfNormal1 = 2*Math.PI/getNumberOfSectors()*index - Math.PI/2;
			// System.out.println("angle of normal 1 = " + angleOfNormal1);
			surface.addSceneObject(
					new Plane(
							"first side",	// description,
							pointOnAxis,	// pointOnPlane,
							Vector3D.sum(
									normalised0DegreeDirection.getProductWith(Math.cos(angleOfNormal1)),
									normalised90DegreeDirection.getProductWith(Math.sin(angleOfNormal1))
								),	// outward-facing normal, 
							null,	// surfaceProperty,
							null,	// parent,
							null	// studio
				));

			// angle of *outward*-facing normal
			double angleOfNormal2 = 2*Math.PI/getNumberOfSectors()*(index+1) + Math.PI/2;
			// System.out.println("angle of normal 2 = " + angleOfNormal2);
			surface.addSceneObject(
					new Plane(
							"second side",	// description,
							pointOnAxis,	// pointOnPlane,
							Vector3D.sum(
									normalised0DegreeDirection.getProductWith(Math.cos(angleOfNormal2)),
									normalised90DegreeDirection.getProductWith(Math.sin(angleOfNormal2))
								),	// normal, 
							null,	// surfaceProperty,
							null,	// parent,
							null	// studio
				));

			return surface;
		}

		/** 
		 * 
		 * @param positionVector the position vector
		 * 
		 */
		public double getAzimuthalAngle(Vector3D positionVector)
		{
			Vector3D r = Vector3D.difference(positionVector, pointOnAxis);
					
			return Math.atan2(
					Vector3D.scalarProduct(r, normalised0DegreeDirection), 
					Vector3D.scalarProduct(r, normalised90DegreeDirection)
				);
		}
	}