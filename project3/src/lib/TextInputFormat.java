package lib;

import interfaces.InputFormat;

public class TextInputFormat extends InputFormat<IntWritable, TextWritable> {
	
	private TextWritable value;
	private IntWritable key = new IntWritable(0);
	
	@Override
	public void parse(String str) {
		value = new TextWritable(str);
	}
	
	public IntWritable getKey() {
		return key;
	}
	
	public TextWritable getValue() {
		return value;
	}

	@Override
	public String getKeyType() {
		return IntWritable.class.getName();
	}

	@Override
	public String getValueType() {
		return TextWritable.class.getName();
	}
}
