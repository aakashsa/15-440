package interfaces;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class Writable<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract T parse();

	private static Set<String> validFormats = new HashSet<String>();

	public static Set<String> validInputFormats() {
		validFormats.add("IntWritable");
		validFormats.add("TextWritable");
		validFormats.add("LongWritable");
		validFormats.add("DoubleWritable");
		validFormats.add("NullWritable");
		return validFormats;
	}
}
