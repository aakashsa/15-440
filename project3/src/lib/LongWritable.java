package lib;

import interfaces.Writable;

public class LongWritable extends Writable<Long> {

	private static final long serialVersionUID = 1L;
	private long i;
	
	public LongWritable(long i) {
		this.i = i;
	}
	
	@Override
	public Long parse() {
		return null;
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
