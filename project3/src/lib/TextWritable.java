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

	@Override
	public int compareTo(Writable<String> o) {
		return s.compareTo(o.getValue());
	}
}
