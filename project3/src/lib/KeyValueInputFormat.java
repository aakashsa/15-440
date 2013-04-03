package lib;

import interfaces.InputFormat;

public class KeyValueInputFormat extends InputFormat<TextWritable, TextWritable> {

	private TextWritable value;
	private TextWritable key;

	@Override
	public void parse(String str) {
		String[] contents = str.split("\\t");
		value = new TextWritable(contents[1]);
		key = new TextWritable(contents[0]);
	}

	@Override
	public TextWritable getKey() {
		return key;
	}

	@Override
	public TextWritable getValue() {
		return value;
	}

	@Override
	public String getKeyType() {
		return TextWritable.class.getName();
	}

	@Override
	public String getValueType() {
		return TextWritable.class.getName();
	}

}
