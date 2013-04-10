package lib;

import interfaces.Writable;


public class TextWritable extends Writable<String> {

	private static final long serialVersionUID = 1L;
	private String s;
	
	/**
	 * Empty constructor
	 */
	public TextWritable() {
	}
	
	/**
	 * Constructor that sets a string
	 * @param i
	 */
	public TextWritable(String s) {
		this.s = s;
	}
	
	/**
	 * A function that parses a string from string
	 */
	@Override
	public TextWritable parseFromString(String s) {
		this.s = s;
		return this;
	}
	
	/**
	 * Getter for value of writable
	 * @return value
	 */
	public String getValue() {
		return s;
	}
	
	/**
	 * Setter for value of writable
	 * @param i - value to set
	 */
	public void setValue(String s) {
		this.s = s;
	}

	/**
	 * To string function
	 */
	@Override
	public String toString() {
		return s;
	}
	
	/**
	 * Compare the value of this object to the value of callee
	 * @Object arg0 - argument to be compared with
	 */
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("Can't compare null object");
		if (arg0 instanceof String)
			return s.compareTo((String)arg0);
		else
			throw new IllegalArgumentException("Expected: Double; Actual: " + arg0.getClass().getName());			
	}
}
