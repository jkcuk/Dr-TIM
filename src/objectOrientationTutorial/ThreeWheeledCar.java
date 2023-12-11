package objectOrientationTutorial;

public class ThreeWheeledCar extends Car {

	public ThreeWheeledCar(int numberOfDoors) {
		super(numberOfDoors);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getNumberOfWheels()
	{
		return 3;
	}

}
