package optics.raytrace.research.refractiveTaylorSeries;

import math.Geometry;
import math.MyMath;
import math.Vector3D;

/**
 * a simple law of refraction, with 1 paramter
 */
public class LawOfRefraction1Parameter {
	public static final LawOfRefraction1Parameter TRANSPARENT = new LawOfRefraction1Parameter(LawOfRefractionType.TRANSPARENT, 0);
	
	public enum LawOfRefractionType {
		RAY_ROTATION("Ray rotation", "Rotation angle (degrees)"),
		SHIFT_X("Shift in x direction", "Change in x component of normalised ray direction"),
		TELESCOPE("Telescope", "Magnification factor"),
		TRANSPARENT("Transparent", null);
		
		private String description, parameterDescription;
		LawOfRefractionType(String description, String parameterDescription)
		{
			this.description = description;
			this.parameterDescription = parameterDescription;
		}
		public String getParameterDescription()
		{
			return parameterDescription;
		}
		@Override
		public String toString() {return description;}
	}
	public LawOfRefractionType lawOfRefractionType;
	public double parameter;
	
	public LawOfRefraction1Parameter(LawOfRefractionType lawOfRefractionType, double parameter) {
		super();
		this.lawOfRefractionType = lawOfRefractionType;
		this.parameter = parameter;
	}

	public Vector3D refract(Vector3D directionIn)
	{
		double dx, dy, dz2;
		switch(lawOfRefractionType)
		{
		case RAY_ROTATION:
			return Geometry.rotate(directionIn, Vector3D.Z, MyMath.deg2rad(parameter));
		case SHIFT_X:
			dx = directionIn.x + parameter;
			dz2 = 1.-dx*dx-directionIn.y*directionIn.y;
			if(dz2 < 0) return Vector3D.NaV;
			return new Vector3D(dx, directionIn.y, Math.sqrt(dz2));
		case TELESCOPE:
			dx = directionIn.x / parameter;
			dy = directionIn.y / parameter;
			dz2 = 1.-dx*dx-dy*dy;
			if(dz2 < 0) return Vector3D.NaV;
			return new Vector3D(dx, dy, Math.sqrt(dz2));
		case TRANSPARENT:
		default:
			return directionIn;
		}
	}
	
	@Override
	public String toString() 
	{
		return lawOfRefractionType.toString() + ", " + lawOfRefractionType.getParameterDescription() + "=" + parameter;
	}

}
