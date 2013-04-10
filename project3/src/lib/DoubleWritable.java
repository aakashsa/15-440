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
	@Override
	public Double getValue() {
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
	
	/**
	 * Compare the value of this object to the value of callee
	 * @Object arg0 - argument to be compared with
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
