package lib;

import interfaces.Writable;

public class IntWritable extends Writable<Integer> {

	private static final long serialVersionUID = 1L;
	private int i;
	
	public IntWritable(int i) {
		this.i = i;

	}
	
	@Override
	public Integer parse() {
		return null;
	}
	
	public int getValue() {
		return i;
	}
	
	public void setValue(int i) {
		this.i = i;
	}

	@Override
	public String toString() {
		return ((Integer) i).toString();
	}

}
