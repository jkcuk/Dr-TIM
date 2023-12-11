package objectOrientationTutorial;

public class Car {
	private int numberOfDoors;

	//  constructor
	
	public Car(int numberOfDoors) {
		this.numberOfDoors =  numberOfDoors;
	}
	
	public Car() {
		this(5);
		// this.numberOfDoors = 5;
	}

	// getters & setters
	
	public int getNumberOfDoors() {
		return numberOfDoors;
	}

	public void setNumberOfDoors(int numberOfDoors) {
		this.numberOfDoors = numberOfDoors;
	}
	
	
	//  other methods
	
	public int getNumberOfWheels()
	{
		return 4;
	}

}
