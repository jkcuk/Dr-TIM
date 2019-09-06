package math.ODE;

public enum IntegrationType
{
	EULER("Euler integration"),
	RK4("Runge-Kutta (RK4) integration");

	private String description;

	private IntegrationType(String description) {this.description = description;}	
	@Override
	public String toString() {return description;}
}
