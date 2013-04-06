package lib;

import interfaces.Writable;

public class DoubleWritable extends Writable<Double> {

	private static final long serialVersionUID = 1L;
	private double i;
	
	public DoubleWritable() {
	}
	
	public DoubleWritable(double i) {
		this.i = i;
	}
	
	@Override
	public DoubleWritable parseFromString(String s) {
		try {
			this.i = Double.parseDouble(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public double getValue() {
		return i;
	}
	
	public void setValue(double i) {
		this.i = i;
	}

	@Override
	public String toString() {
		return ((Double) i).toString();
	}
}
