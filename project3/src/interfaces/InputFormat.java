package interfaces;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract class that represents a file input format
 *
 * @param <K> Type of key
 * @param <V> Type of value
 */
public abstract class InputFormat<K extends Writable<?>, V extends Writable<?>> {
	
	private static Set<String> validFormats = new HashSet<String>();
	
	/**
	 * A function that returns a set of all valid input formats
	 * @return set of input formats
	 */
	public static Set<String> validInputFormats() {
		validFormats.add("TextInputFormat");
		validFormats.add("KeyValueInputFormat");
		return validFormats;
	}
	
	/**
	 * An abstract function to parse key and value for a particular input format
	 * @param str String to parse
	 */
	public abstract void parse(String str);
	
	/**
	 * Get key of input format
	 * @return Key
	 */
	public abstract K getKey();
	
	/**
	 * Get value of input format
	 * @return Value
	 */
	public abstract V getValue();
	
	/**
	 * Get type of key
	 * @return Key type
	 */
	public abstract String getKeyType();
	
	/**
	 * Get value type
	 * @return Value type
	 */
	public abstract String getValueType();
}
