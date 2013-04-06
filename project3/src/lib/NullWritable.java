package lib;

import interfaces.Writable;

public class NullWritable extends Writable<String> {

	private static final long serialVersionUID = 1L;

	public NullWritable() {
	}
	
	@Override
	public NullWritable parseFromString(String s) {
		return this;
	}
	
	public String getValue() {
		return null;
	}

	@Override
	public String toString() {
		return "NULL";
	}
}
