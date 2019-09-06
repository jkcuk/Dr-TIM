package math;

public class Vector3DWithBonusVector3D extends Vector3D
{
	private static final long serialVersionUID = -4572046633285140780L;

	private Vector3D bonusVector;

	public Vector3DWithBonusVector3D(double x, double y, double z, Vector3D bonusVector)
	{
		super(x, y, z);
		this.bonusVector = bonusVector;
	}
	
	public Vector3DWithBonusVector3D(Vector3D vector, Vector3D bonusVector)
	{
		super(vector);
		this.bonusVector = bonusVector;
	}

//	public Vector3DWithBonusVector3D(Vector3DWithBonusVector3D original)
//	{
//		this(original, original.getBonusVector());
//	}

	
	// getters & setters
	
	public Vector3D getBonusVector() {
		return bonusVector;
	}

	public void setBonusVector(Vector3D bonusVector) {
		this.bonusVector = bonusVector;
	}	
}
