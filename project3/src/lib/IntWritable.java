package lib;

import interfaces.Writable;

/**
 * A writable type that represents an integer
 *
 */
public class IntWritable extends Writable<Integer> {

	private static final long serialVersionUID = 1L;
	private int i;
	
	/**
	 * Empty constructor
	 */
	public IntWritable() {
	}
	
	/**
	 * Constructor that sets an integer value
	 * @param i Value to set
	 */
	public IntWritable(int i) {
		this.i = i;
	}
		
	/**
	 * Getter for value of writable
	 * @return Value
	 */
	@Override
	public Integer getValue() {
		return i;
	}
	
	/**
	 * To string function
	 * @return String representation of this writable
	 */
	@Override
	public String toString() {
		return ((Integer) i).toString();
	}

	/**
	 * A function that parses an integer writable from string
	 * @return Writable parsed from given string
	 */
	@Override
	public IntWritable parseFromString(String s) {
		try {
			this.i = Integer.parseInt(s);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return this;
	}
	/**
	 * Compare the value of this object to the value of callee
	 * @param arg0 Argument to be compared with
	 */
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("Can't compare null object");
		if (arg0 instanceof Integer){
			if (i < (Integer)arg0) return -1;
			if (i > (Integer)arg0) return 1;
			return 0;
		}
		else 
			throw new IllegalArgumentException("Expected: Double; Actual: " + arg0.getClass().getName());
	}

}
