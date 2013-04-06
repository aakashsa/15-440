package lib;

import interfaces.Writable;

public class IntWritable extends Writable<Integer> {

	private static final long serialVersionUID = 1L;
	private int i;
	
	public IntWritable() {
	}
	
	public IntWritable(int i) {
		this.i = i;
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

	@Override
	public IntWritable parseFromString(String s) {
		try {
			this.i = Integer.parseInt(s);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return this;
	}

}
