package test;

import java.util.Iterator;

import nodework.Context;
import lib.IntWritable;
import lib.TextWritable;
import interfaces.Reducer;

public class WordCountReducer implements
		Reducer<TextWritable, IntWritable, TextWritable, IntWritable> {

	@Override
	public void reduce(TextWritable key, Iterator<IntWritable> values,
			Context<TextWritable, IntWritable> context) {
		int sum = 0;
		while (values.hasNext()) {
			sum += values.next().getValue();
		}
		//System.out.println("Key = " + key.getValue() + " Value = " + sum);
		context.write(key, new IntWritable(sum));
	}
}
