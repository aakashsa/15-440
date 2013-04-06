package interfaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An interface that represents all writable types supported by the map-reduce facility
 *
 * @param <T> Type to support
 */
public abstract class Writable<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A function to parse the writable type from string
	 * @param s - string to parse
	 * @return Writable type object
	 */
	public abstract Writable<T> parseFromString(String s);

	private static Set<String> validFormats = new HashSet<String>();

	/**
	 * A function to get all the writable types supported by the facility
	 * @return set of supported formats
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
}
