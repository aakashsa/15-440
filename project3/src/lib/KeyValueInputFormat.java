package lib;

import interfaces.InputFormat;

public class KeyValueInputFormat extends InputFormat<String, String> {

	private String value;
	private String key;

	@Override
	public void parse(String str) {
		String[] contents = str.split("\\t");
		value = contents[1];
		key = contents[0];
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

}
