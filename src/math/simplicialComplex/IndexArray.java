package math.simplicialComplex;

import java.util.HashSet;
import java.util.Set;

import optics.raytrace.exceptions.InconsistencyException;

/**
 * @author johannes
 * A few utility functions related to int[]s.
 * These are intended for use with the SimplicialComplex class,
 * which stores lots of arrays of indices of vertices, edges, and faces.
 */
public class IndexArray
{
	/**
	 * a number that allows the outside of the simplicial complex to be indexed like a simplex
	 */
	public static final int OUTSIDE = -10;
	
	/**
	 * normal indices have values 0, 1, 2, ...; a value NONE indicates that an index hasn't been set
	 */
	public static final int NONE = -1;
	
	/**
	 * normal indices have values 0, 1, 2, ...; a value NOT_FOUND indicates that an index that was expected to be present was not found
	 */
	public static final int NOT_FOUND = -2;
	


	/**
	 * Check that the array has the intended length.
	 * If it hasn't, print out a stack trace.
	 * @param array
	 * @param intendedLength
	 * @throws InconsistencyException 
	 */
	public static void checkArrayLength(int[] array, int intendedLength)
	throws InconsistencyException
	{
		// check if there are exactly <intendedLength> numbers in the array
		if(array.length != intendedLength)
			throw new InconsistencyException("The array should contain "+intendedLength+" elements, but instead has " + array.length + ".");		
	}
	
	/**
	 * Check that all the elements in the array are different.
	 * If they aren't, print a stack trace.
	 * @param array
	 * @throws InconsistencyException 
	 */
	public static void checkElementsAreDifferent(int[] array)
	throws InconsistencyException
	{
		// check if the elements of the array are different
		
		// one after the other, compare element #i...
		for(int i=0; i<array.length; i++)
			// ... with all elements #j (j > i)...
			for(int j=i+1; j<array.length; j++)
				// ... and if they are the same...
				if(array[i] == array[j])
					throw new InconsistencyException(
						"The elements of the array should all be different, but elements #"
						+i+" ("+ array[i] +") and #"
						+j+" ("+ array[j] +") aren't.");
	}
	
	public static int[] set2Array(Set<Integer> set)
	{
		// create an array of ints with the elements of this set
		int[] array = new int[set.size()];
		int i = 0;
		for (Integer integer : set) array[i++] = integer;
		
		return array;
	}
	
	/**
	 * @param array
	 * @return	an array that contains each element in <array>, but only once
	 */
	public static int[] set(int[] array)
	{
		Set<Integer> set = new HashSet<Integer>();

		// add all elements from the array to <i>set</i>; by virtue of this being a HashSet, there will be no duplicates
		for(int i=0; i<array.length; i++) set.add(array[i]);
		
		// return <i>set</i>, converted into an array of ints
		return set2Array(set);
	}

	/**
	 * @param array1
	 * @param array2
	 * @return	an array that contains each element in <array1> and <array2>, but only once
	 */
	public static int[] unionSet(int[] array1, int[] array2)
	{
		Set<Integer> set = new HashSet<Integer>();

		// add all elements from both arrays
		for(int i=0; i<array1.length; i++) set.add(array1[i]);
		for(int i=0; i<array2.length; i++) set.add(array2[i]);
		
		// return <i>set</i>, converted into an array of ints
		return set2Array(set);
	}

	/**
	 * @param array1
	 * @param array2
	 * @param array3
	 * @return	an array that contains each element in <i>array1</i>, <i>array2</i>, and <i>array3</i>, but only once
	 */
	public static int[] unionSet(int[] array1, int[] array2, int[] array3)
	{
		Set<Integer> set = new HashSet<Integer>();

		// add all elements from all three arrays
		for(int i=0; i<array1.length; i++) set.add(array1[i]);
		for(int i=0; i<array2.length; i++) set.add(array2[i]);
		for(int i=0; i<array3.length; i++) set.add(array3[i]);
		
		// return <i>set</i>, converted into an array of ints
		return set2Array(set);
	}
	
	/**
	 * @param i
	 * @param array
	 * @return	true if <i> is in <array>, false otherwise
	 */
	public static boolean isInArray(int i, int[] array)
	{
		// go through all the numbers in <array>...
		for(int j=0; j<array.length; j++)
		{
			// ... and check if the <j>th number equals i, ...
			if(array[j] == i)
				// ... in which case <i> is indeed in <array>
				return true;
		}
		
		// <i> is not equal to any element in <array>
		return false;
	}

	/**
	 * @param testIndices
	 * @param indices
	 * @return	true if all indices in <i>testIndices</i> are also in <i>indices</i>, false otherwise
	 */
	public static boolean areInArray(int[] testIndices, int[] indices)
	{
		// go through all the numbers in <i>testIndices</i>...
		for(int j=0; j<testIndices.length; j++)
		{
			// ... and check if the <i>j</i>th number in <i>testIndices</i> is in <i>indices</i>, ...
			if(!isInArray(testIndices[j], indices))
				// ... and if it isn't, return false
				return false;
		}
		
		// all the numbers in <i>testIndices</i> are in <i>indices</i>
		return true;
	}

	/**
	 * @param i
	 * @param indices
	 * @return	the first element of <i>indices</i> that is not <i>i</i>; NONE if none can be found
	 */
	public static int getFirstOtherIndex(int i, int[] indices)
	{
		for(int j=0; j<indices.length; j++)
			if(indices[j] != i) return indices[j];
		
		// there is no element in array other than <i>i</i>
		return NONE;
	}
	
	/**
	 * @param testIndices
	 * @param indices
	 * @return	the first element of <i>indices</i> that is not in <i>notTheseIndices</i>
	 */
	public static int getFirstOtherIndex(int[] testIndices, int[] indices)
	{
		// go through all the elements of <i>array</i>...
		for(int j=0; j<indices.length; j++)
			// ... and check every one if it is in <i>testIndices</i>, ...
			if(!isInArray(indices[j], testIndices))
				// ...and if it isn't, return it
				return indices[j];
		
		// if no index in <i>indices</i> is not in <i>testIndices</i>, return NONE
		return NONE;
	}
}
