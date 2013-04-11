package lib;

import interfaces.InputFormat;

/**
 * An input format. This type assumes that key and value are both TextWritable objects.
 * To parse a string into this type of format, the line should be split by a tab
 * character.
 *
 */
public class KeyValueInputFormat extends InputFormat<TextWritable, TextWritable> {

	private TextWritable value;
	private TextWritable key;

	/**
	 * Parse a string into a key value input type. Split by tab character
	 * @param str String to parse
	 */
	@Override
	public void parse(String str) {
		String[] contents = str.split("\\t");
		if (!(contents.length == 2)) {
			throw new RuntimeException("Couldn't parse key and value from input '" + str + "'");
		}
		value = new TextWritable(contents[1]);
		key = new TextWritable(contents[0]);
	}

	/**
	 * Get key
	 * @return Key
	 */
	@Override
	public TextWritable getKey() {
		return key;
	}

	/**
	 * Get value
	 * @return Value
	 */
	@Override
	public TextWritable getValue() {
		return value;
	}

	/**
	 * Get key type
	 * @return String name of key type
	 */
	@Override
	public String getKeyType() {
		return TextWritable.class.getName();
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
