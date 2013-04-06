package lib;

import interfaces.Writable;

public class LongWritable extends Writable<Long> {

	private static final long serialVersionUID = 1L;
	private long i;
	
	public LongWritable() {
	}
	
	public LongWritable(long i) {
		this.i = i;
	}
	
	@Override
	public LongWritable parseFromString(String s) {
		try {
			this.i = Long.parseLong(s); 
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return this;
	}
	
	public long getValue() {
		return i;
	}
	
	public void setValue(long i) {
		this.i = i;
	}

	@Override
	public String toString() {
		return ((Long) i).toString();
	}	
}
