package lib;

import interfaces.Writable;

public class TextWritable extends Writable<String> {

	private static final long serialVersionUID = 1L;
	private String s;
	
	public TextWritable(String s) {
		this.s = s;
	}
	
	@Override
	public String parse() {
		return null;
	}
	
	public String getValue() {
		return s;
	}
	
	public void setValue(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}
	
}
