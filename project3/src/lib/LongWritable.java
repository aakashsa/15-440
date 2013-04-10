package lib;

import interfaces.Writable;

/**
 * A writable type that represents a long number
 *
 */
public class LongWritable extends Writable<Long> {

	private static final long serialVersionUID = 1L;
	private long i;
	
	/**
	 * Empty constructor
	 */	
	public LongWritable() {
	}
	
	/**
	 * Constructor that sets a long value
	 * @param i
	 */
	public LongWritable(long i) {
		this.i = i;
	}
	
	/**
	 * A function that parses a long writable from string
	 */
	@Override
	public LongWritable parseFromString(String s) {
		try {
			this.i = Long.parseLong(s); 
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
	public Long getValue() {
		return i;
	}
	
	/**
	 * Setter for value of writable
	 * @param i - value to set
	 */
	public void setValue(long i) {
		this.i = i;
	}

	/**
	 * To string function
	 */
	@Override
	public String toString() {
		return ((Long) i).toString();
	}

	/**
	 * Compare the value of this object to the value of callee
	 * @Object arg0 - argument to be compared with
	 */
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == null)
			throw new IllegalArgumentException("Can't compare null object");
		if (arg0 instanceof Long){
			if (i < (Long)arg0) return -1;
			if (i > (Long)arg0) return 1;
			return 0;
		}
		else 
			throw new IllegalArgumentException("Expected: Double; Actual: " + arg0.getClass().getName());
	}	
}
