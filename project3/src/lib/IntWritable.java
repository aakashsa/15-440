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
	 * @param i
	 */
	public IntWritable(int i) {
		this.i = i;
	}
		
	/**
	 * Getter for value of writable
	 * @return value
	 */
	public int getValue() {
		return i;
	}
	
	/**
	 * Setter for value of writable
	 * @param i - value to set
	 */
	public void setValue(int i) {
		this.i = i;
	}

	/**
	 * To string function
	 */
	@Override
	public String toString() {
		return ((Integer) i).toString();
	}

	/**
	 * A function that parses an integer writable from string
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

}
