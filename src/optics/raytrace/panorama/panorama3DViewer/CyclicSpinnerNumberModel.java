package optics.raytrace.panorama.panorama3DViewer;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class CyclicSpinnerNumberModel extends SpinnerNumberModel
{
	private static final long serialVersionUID = 3678209539935954780L;

	 
    public CyclicSpinnerNumberModel(double value, double minimum, double maximum, double stepSize)
    {
    	super(value, minimum, maximum, stepSize);
    }



	// see https://docs.oracle.com/javase/tutorial/uiswing/components/spinner.html
    private double incrValue(int dir)
    {
    	double value = ((Double)getValue()).doubleValue();
        double newValue = value + (getStepSize().doubleValue() * dir);

        double
        	minimum = ((Double)getMinimum()).doubleValue(),
        	maximum = ((Double)getMaximum()).doubleValue();
        
        if (maximum < newValue) {
            return minimum + (getStepSize().doubleValue() * dir);
        }
        if (minimum > newValue) {
            return maximum + (getStepSize().doubleValue() * dir);
        }
        else {
            return newValue;
        }
    }


    /**
     * Returns the next number in the sequence.
     *
     * @return <code>value + stepSize</code> or <code>null</code> if the sum
     *     exceeds <code>maximum</code>.
     *
     * @see SpinnerModel#getNextValue
     * @see #getPreviousValue
     * @see #setStepSize
     */
    @Override
    public Object getNextValue() {
        return incrValue(+1);
    }


    /**
     * Returns the previous number in the sequence.
     *
     * @return <code>value - stepSize</code>, or
     *     <code>null</code> if the sum is less
     *     than <code>minimum</code>.
     *
     * @see SpinnerModel#getPreviousValue
     * @see #getNextValue
     * @see #setStepSize
     */
    @Override
    public Object getPreviousValue() {
        return incrValue(-1);
    }
}
