package math;

import java.util.ArrayList;
import java.util.Random;

public class Probability {

	public static Random random = new Random();
	
	/**
	 * @param probabilities	list of probabilities; all probabilities must add up to 1!
	 * @return	one index; the likelihood of any particular index being returned is proportional to the probability in the list of probabilities
	 * @throws Exception	shouldn't happen
	 */
	public static int getIndexWithProbability(ArrayList<Double> probabilities) throws Exception
	{
		double rand = random.nextDouble();
		
		// sum up all the probabilities in the list <probabilities>
		double sum = 0;
		for(int i=0; i<probabilities.size(); i++)
		{
			sum += probabilities.get(i);
			if(sum >= rand) return i;
		}
		
		throw new RuntimeException("This shouldn't happen!");
	}

	/**
	 * @param probabilities	list of numbers that are proportional to the probabilities
	 * @return	one index; the likelihood of any particular index being returned is proportional to the probability in the list of probabilities
	 * @throws Exception	shouldn't happen
	 */
	public static int getIndexWithLikelihood(ArrayList<Double> numbersProportionalToProbabilities) throws Exception
	{
		// sum up all the probabilities in the list <numbersProportionalToProbabilities>...
		double sum = 0;
		for(int i=0; i<numbersProportionalToProbabilities.size(); i++)
		{
			sum += numbersProportionalToProbabilities.get(i);
		}
		
		// ... and create a list of probabilies (that add up to 1)
		ArrayList<Double> probabilities = new ArrayList<Double>();
		for(int i=0; i<numbersProportionalToProbabilities.size(); i++)
		{
			probabilities.add(numbersProportionalToProbabilities.get(i)/sum);
		}

		return getIndexWithProbability(probabilities);
	}

}
