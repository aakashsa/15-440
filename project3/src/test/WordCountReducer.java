package test;

import java.util.Iterator;

import lib.Context;
import lib.IntWritable;
import lib.TextWritable;
import interfaces.Reducer;

/**
 * Word count reducer
 */
public class WordCountReducer implements
		Reducer<TextWritable, IntWritable, TextWritable, IntWritable> {

	/**
	 * Word count reducer function
	 */
	@Override
	public void reduce(TextWritable key, Iterator<IntWritable> values,
			Context<TextWritable, IntWritable> context) {
		int sum = 0;
		while (values.hasNext()) {
			sum += values.next().getValue();
		}
		context.write(key, new IntWritable(sum));
	}
}
