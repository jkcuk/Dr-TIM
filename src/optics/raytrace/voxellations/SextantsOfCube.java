package optics.raytrace.voxellations;

import optics.raytrace.GUI.sceneObjects.EditableSceneObjectCollection;
import optics.raytrace.core.SceneObject;
import optics.raytrace.sceneObjects.ParametrisedTriangle;
import math.Vector3D;

/**
 * @author johannes, ejovboke
 * 
 * The sextants of a cube whose faces are perpendicular to the x, y and z directions.
 * We define a sextant of a cube as the pyramid whose base is one of the sides of the cube and whose apex is the cube centre.
 * As a cube has six sides, there are 6 such sextants.
 * Alternatively, sextant <i>i</i> of a cube can be defined as all the points in the cube that are closest to side <i>i</i> of the cube.
 * The sextants themselves are the "voxels".
 * 
 * Defined by the centre of the cube.
 */
public class SextantsOfCube extends Voxellation
{
	private static final long serialVersionUID = -8604404873924850677L;

	public enum AxisType
	{
		X("x"),
		Y("y"),
		Z("z");
		
		private String description;
		private AxisType(String description) {this.description = description;}	
		@Override
		public String toString() {return description;}
	}

	/**
	 * @author johannes
	 * signals that something is either positive or negative
	 */
	public enum SignType
	{
		POSITIVE("+", +1),
		NEGATIVE("-", -1);
		
		private String description;
		private int signum;	// +1 or -1
		private SignType(String description, int signum)
		{
			this.description = description;
			this.signum = signum;
		}	
		@Override
		public String toString() {return description;}
		public int getSignum() {return signum;}
	}
	
	/**
	 * @param number
	 * @return	POSITIVE is number >= 0, otherwise NEGATIVE
	 */
	public SignType getSign(double number)
	{
		return (number<0)?SignType.NEGATIVE:SignType.POSITIVE;
	}

	/**
	 * @author johannes
	 * a type describing the sextants of the cube
	 */
	public enum SextantType
	{
		XPOSITIVE("x+", AxisType.X, SignType.POSITIVE, 0),
		XNEGATIVE("x-", AxisType.X, SignType.NEGATIVE, 1),
		YPOSITIVE("y+", AxisType.Y, SignType.POSITIVE, 2),
		YNEGATIVE("y-", AxisType.Y, SignType.NEGATIVE, 3),
		ZPOSITIVE("z+", AxisType.Z, SignType.POSITIVE, 4),
		ZNEGATIVE("z-", AxisType.Z, SignType.NEGATIVE, 5);
		
		private String description;
		private AxisType axis;
		private SignType sign;
		private int index;	// when used as a "voxellation", each sextant acts as a voxel and has to have an index
		private SextantType(String description, AxisType axis, SignType sign, int index)
		{
			this.description = description;
			this.axis = axis;
			this.sign = sign;
			this.index = index;
		}	
		@Override
		public String toString() {return description;}
		public String getDescription() {return description;}
		public AxisType getAxis() {return axis;}
		public SignType getSign() {return sign;}
		public int getIndex() {return index;}
	}
	
	/**
	 * @param index
	 * @return	the corresponding sextant
	 */
	public static SextantType getSextant(int index)
	throws IndexOutOfBoundsException
	{
		if(index == SextantType.XPOSITIVE.getIndex()) return SextantType.XPOSITIVE;
		if(index == SextantType.XNEGATIVE.getIndex()) return SextantType.XNEGATIVE;
		if(index == SextantType.YPOSITIVE.getIndex()) return SextantType.YPOSITIVE;
		if(index == SextantType.YNEGATIVE.getIndex()) return SextantType.YNEGATIVE;
		if(index == SextantType.ZPOSITIVE.getIndex()) return SextantType.ZPOSITIVE;
		if(index == SextantType.ZNEGATIVE.getIndex()) return SextantType.ZNEGATIVE;
		
		// index out of bounds
		throw new IndexOutOfBoundsException("Sextant index ("+index+") out of bounds.");
	}

	/**
	 * Centre
	 */
	protected Vector3D centre;
		
	// constructor
	
	/**
	 * @param centre
	 */
	public SextantsOfCube(Vector3D centre)
	{
		super();
		
		setCentre(centre);
	}
	
	// the methods that make this class useful
	
	/**
	 * @param axis
	 * @param sign
	 * @return	the sextant corresponsing to the axis and sign
	 */
	public static SextantType getSextant(AxisType axis, SignType sign)
	{
		// the axis is either x, ...
		if(axis == AxisType.X)
		{
			return (sign==SignType.POSITIVE)?SextantType.XPOSITIVE:SextantType.XNEGATIVE;
		}
		
		// ... or y, ...
		if(axis == AxisType.Y)
		{
			return (sign==SignType.POSITIVE)?SextantType.YPOSITIVE:SextantType.YNEGATIVE;
		}
		
		// ... or otherwise z
		return (sign==SignType.POSITIVE)?SextantType.ZPOSITIVE:SextantType.ZNEGATIVE;
	}
	
	/**
	 * @param position
	 * @return	the corresponding sextant
	 */
	public SextantType getSextant(Vector3D position)
	{
		Vector3D v = Vector3D.difference(position, centre);
		
		double
			vx2 = v.x*v.x,
			vy2 = v.y*v.y,
			vz2 = v.z*v.z;
		
		if((vx2 > vy2) && (vx2 > vz2))
		{
			// the x component is greatest
			if(v.x > 0) return SextantType.XPOSITIVE;
			else return SextantType.XNEGATIVE;
		}

		if((vy2 > vx2) && (vy2 > vz2))
		{
			// the y component is greatest
			if(v.y > 0) return SextantType.YPOSITIVE;
			else return SextantType.YNEGATIVE;
		}

		// the z component is greatest
		if(v.z > 0) return SextantType.ZPOSITIVE;
		else return SextantType.ZNEGATIVE;
	}

	/**
	 * The voxel with index <i>i</i>
	 * @param position
	 * @return	voxel index
	 */
	@Override
	public int getVoxelIndex(Vector3D position)
	{
		return getSextant(position).getIndex();
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
		
		Vector3D
			centreToSide,
			sideSpanVector1,
			sideSpanVector2;
		
		if(index == SextantType.XPOSITIVE.getIndex())
		{
			centreToSide = Vector3D.X;
			sideSpanVector1 = Vector3D.Y;
			sideSpanVector2 = Vector3D.Z;
		}
		else if(index == SextantType.XNEGATIVE.getIndex())
		{
			centreToSide = Vector3D.X.getReverse();
			sideSpanVector1 = Vector3D.Y;
			sideSpanVector2 = Vector3D.Z;
		}
		else if(index == SextantType.YPOSITIVE.getIndex())
		{
			centreToSide = Vector3D.Y;
			sideSpanVector1 = Vector3D.X;
			sideSpanVector2 = Vector3D.Z;
		}
		else if(index == SextantType.YNEGATIVE.getIndex())
		{
			centreToSide = Vector3D.Y.getReverse();
			sideSpanVector1 = Vector3D.X;
			sideSpanVector2 = Vector3D.Z;
		}
		else if(index == SextantType.ZPOSITIVE.getIndex())
		{
			centreToSide = Vector3D.Z;
			sideSpanVector1 = Vector3D.X;
			sideSpanVector2 = Vector3D.Y;
		}
		else if(index == SextantType.ZNEGATIVE.getIndex())
		{
			centreToSide = Vector3D.Z.getReverse();
			sideSpanVector1 = Vector3D.X;
			sideSpanVector2 = Vector3D.Y;
		}
		else
		{
			// index out of bounds
			throw new IndexOutOfBoundsException("Sextant index ("+index+") out of bounds.");
		}

		// add the relevant triangles
		
		surface.addSceneObject(new ParametrisedTriangle(
				"side triangle 1",
				centre,	// corner1
				Vector3D.sum(centreToSide, sideSpanVector1, sideSpanVector2),	// side span vector 1
				Vector3D.sum(centreToSide, sideSpanVector1, sideSpanVector2.getReverse()),	// side span vector 2
				true,	// semi-infinite
				null,	// surface property
				null,	// parent
				null	// studio
			));

		surface.addSceneObject(new ParametrisedTriangle(
				"side triangle 2",
				centre,	// corner1
				Vector3D.sum(centreToSide, sideSpanVector1, sideSpanVector2.getReverse()),	// side span vector 1
				Vector3D.sum(centreToSide, sideSpanVector1.getReverse(), sideSpanVector2.getReverse()),	// side span vector 2
				true,	// semi-infinite
				null,	// surface property
				null,	// parent
				null	// studio
			));

		surface.addSceneObject(new ParametrisedTriangle(
				"side triangle 3",
				centre,	// corner1
				Vector3D.sum(centreToSide, sideSpanVector1.getReverse(), sideSpanVector2.getReverse()),	// side span vector 1
				Vector3D.sum(centreToSide, sideSpanVector1.getReverse(), sideSpanVector2),	// side span vector 2
				true,	// semi-infinite
				null,	// surface property
				null,	// parent
				null	// studio
			));

		surface.addSceneObject(new ParametrisedTriangle(
				"side triangle 4",
				centre,	// corner1
				Vector3D.sum(centreToSide, sideSpanVector1.getReverse(), sideSpanVector2),	// side span vector 1
				Vector3D.sum(centreToSide, sideSpanVector1, sideSpanVector2),	// side span vector 2
				true,	// semi-infinite
				null,	// surface property
				null,	// parent
				null	// studio
			));

		return surface;
	}
	


	// setters & getters
	
	public Vector3D getCentre() {
		return centre;
	}

	public void setCentre(Vector3D centre) {
		this.centre = centre;
	}
}
