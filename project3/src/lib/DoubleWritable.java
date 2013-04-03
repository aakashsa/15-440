package lib;

import interfaces.Writable;

public class DoubleWritable extends Writable<Double> {

	private static final long serialVersionUID = 1L;
	private double i;
	
	public DoubleWritable(double i) {
		this.i = i;
	}
	
	@Override
	public Double parse() {
		return null;
	}
	
	public double getValue() {
		return i;
	}
	
	public void setValue(double i) {
		this.i = i;
	}
	
}
