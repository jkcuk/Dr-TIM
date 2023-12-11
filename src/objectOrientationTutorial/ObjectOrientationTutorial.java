package objectOrientationTutorial;

/**
 * @author Johannes Courtial
 */
public class ObjectOrientationTutorial
{
	/**
	 * The main method, required so that this class can run as a Java application
	 * @param args
	 */
	public static void main(final String[] args)
	{
		// properties
		Car myCar = new Car();
		
		System.out.println("number of doors = "+myCar.getNumberOfDoors());
		System.out.println("number of wheels = "+myCar.getNumberOfWheels());

		// now one door is broken
		myCar.setNumberOfDoors(4);
		System.out.println("number of doors = "+myCar.getNumberOfDoors());
		
		Car mySecondCar = new Car(3);
		
		System.out.println("number of doors = "+mySecondCar.getNumberOfDoors());
		System.out.println("number of wheels = "+mySecondCar.getNumberOfWheels());

		ThreeWheeledCar  funnyCar = new ThreeWheeledCar(11);
		System.out.println("number of doors = "+funnyCar.getNumberOfDoors());
		System.out.println("number of wheels = "+funnyCar.getNumberOfWheels());

	}
}
