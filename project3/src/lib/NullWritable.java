package lib;

import interfaces.Writable;

/**
 * A writable type that represents a null object.
 * This may be used when there is nothing to write. For
 * example, the input key type for TextInputFormat type
 * is NullWritable
 *
 */
public class NullWritable extends Writable<String> {

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor
	 */
	public NullWritable() {
	}
	
	/**
	 * A function that parses a null object
	 * @return String to parse
	 */
	@Override
	public NullWritable parseFromString(String s) {
		return this;
	}
	
	/**
	 * Get null value
	 * @return null
	 */
	public String getValue() {
		return null;
	}

	/**
	 * @return String representation of null
	 */
	@Override
	public String toString() {
		return "NULL";
	}
	
	/**
	 * Compare the value of this object to the value of callee
	 * @param arg0 Argument to be compared with
	 */
	@Override
	public int compareTo(Object arg0) {
		return 0;
	}
	
}
