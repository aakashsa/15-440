package lib;

import interfaces.InputFormat;

/**
 * A text input format. The key is NullWritable and value is TextWritable.
 * An entire string is assigned to the value with no key.
 */
public class TextInputFormat extends InputFormat<NullWritable, TextWritable> {
	
	private TextWritable value;
	private NullWritable key = new NullWritable();
	
	/**
	 * Parse a string into this input format
	 * @param str String to parse
	 */
	@Override
	public void parse(String str) {
		value = new TextWritable(str);
	}
	
	/**
	 * Get key
	 * @return Key
	 */
	public NullWritable getKey() {
		return key;
	}
	
	/**
	 * Get value
	 * @return Value
	 */
	public TextWritable getValue() {
		return value;
	}

	/**
	 * Get key type
	 * @return String name of key type
	 */
	@Override
	public String getKeyType() {
		return NullWritable.class.getName();
	}

	/**
	 * Get value type
	 * @return String name of value type
	 */
	@Override
	public String getValueType() {
		return TextWritable.class.getName();
	}
}
