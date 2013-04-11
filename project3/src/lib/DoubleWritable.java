package lib;

import interfaces.Writable;

/**
 * A writable type that represents a double number
 */
public class DoubleWritable extends Writable<Double> {

	private static final long serialVersionUID = 1L;
	/**
	 * Value in this writable
	 */
	private double i;
	
	/**
	 * Empty constructor
	 */
	public DoubleWritable() {
	}
	
	/**
	 * Constructor that sets a double value
	 * @param i Value to set
	 */
	public DoubleWritable(double i) {
		this.i = i;
	}
	
	/**
	 * A function that parses a double writable from string
	 * @return The double writable that is parsed from this string
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
	 * @return Value
	 */
	@Override
	public Double getValue() {
		return i;
	}
	
	/**
	 * To string function
	 * @return String representation of double number
	 */
	@Override
	public String toString() {
		return ((Double) i).toString();
	}
	
	/**
	 * Compare the value of this object to the value of callee
	 * @param arg0 Argument to be compared with
	 */
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("Can't compare null object");
		if (arg0 instanceof Double){
			if (i < (Double) arg0) return -1;
			if (i > (Double) arg0) return 1;
			return 0;
		}
		else {
			throw new IllegalArgumentException("Expected: Double; Actual: " + arg0.getClass().getName());
		}
			
	}
}
