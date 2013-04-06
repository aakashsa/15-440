package lib;

import interfaces.Writable;

/**
 * A writable type that represents a double number
 *
 */
public class DoubleWritable extends Writable<Double> {

	private static final long serialVersionUID = 1L;
	private double i;
	
	/**
	 * Empty constructor
	 */
	public DoubleWritable() {
	}
	
	/**
	 * Constructor that sets a double value
	 * @param i
	 */
	public DoubleWritable(double i) {
		this.i = i;
	}
	
	/**
	 * A function that parses a double writable from string
	 */
	@Override
	public DoubleWritable parseFromString(String s) {
		try {
			this.i = Double.parseDouble(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Getter for value of writable
	 * @return value
	 */
	public double getValue() {
		return i;
	}
	
	/**
	 * Setter for value of writable
	 * @param i - value to set
	 */
	public void setValue(double i) {
		this.i = i;
	}

	/**
	 * To string function
	 */
	@Override
	public String toString() {
		return ((Double) i).toString();
	}
}
