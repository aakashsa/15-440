package lib;

import interfaces.InputFormat;

public class TextInputFormat extends InputFormat<NullWritable, TextWritable> {
	
	private TextWritable value;
	private NullWritable key = new NullWritable();
	
	@Override
	public void parse(String str) {
		value = new TextWritable(str);
	}
	
	public NullWritable getKey() {
		return key;
	}
	
	public TextWritable getValue() {
		return value;
	}

	@Override
	public String getKeyType() {
		return NullWritable.class.getName();
	}

	@Override
	public String getValueType() {
		return TextWritable.class.getName();
	}
}
