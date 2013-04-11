package interfaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An interface that represents all writable types supported by the map-reduce facility
 *
 * @param <T> Type to support as writable
 */
public abstract class Writable<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A function to parse the writable type from string
	 * @param s String to parse
	 * @return Writable type object
	 */
	public abstract Writable<T> parseFromString(String s);

	private static Set<String> validFormats = new HashSet<String>();

	/**
	 * A function to get all the writable types supported by the facility
	 * @return Set of supported formats
	 */
	public static Set<String> validInputFormats() {
		validFormats.add("IntWritable");
		validFormats.add("TextWritable");
		validFormats.add("LongWritable");
		validFormats.add("DoubleWritable");
		validFormats.add("NullWritable");
		return validFormats;
	}
	
	/**
	 * To string function for a particular writable
	 */
	@Override
	public abstract String toString();
	
	/**
	 * A function that gets the value in this writable
	 * @return Value in this writable
	 */
	public abstract T getValue();
	
	/**
	 * Compare the value of this object to the value of callee
	 * @param arg0 Argument to be compared with
	 */
	public abstract int compareTo(Object arg0);

}
