package lib;

import interfaces.Writable;

public class NullWritable extends Writable<String> {

	private static final long serialVersionUID = 1L;

	public NullWritable() {
		
	}
	
	@Override
	public String parse() {
		return null;
	}
	
	public String getValue() {
		return null;
	}

	@Override
	public String toString() {
		return "NULL";
	}
}
