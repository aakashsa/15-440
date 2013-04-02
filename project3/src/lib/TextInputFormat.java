package lib;

import interfaces.InputFormat;

public class TextInputFormat extends InputFormat<String, String> {
	
	private String value;
	
	@Override
	public void parse(String str) {
		value = str;
	}
	
	public String getKey() {
		return null;
	}
	
	public String getValue() {
		return value;
	}
}
