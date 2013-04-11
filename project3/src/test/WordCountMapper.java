package test;

import interfaces.Mapper;

import lib.Context;
import lib.IntWritable;
import lib.NullWritable;
import lib.TextWritable;

/**
 * Word count mapper
 */
public class WordCountMapper implements
		Mapper<NullWritable, TextWritable, TextWritable, IntWritable> {

	/**
	 * Word count map function
	 */
	@Override
	public void map(NullWritable key, TextWritable value, Context<TextWritable, IntWritable> context) {

		String v = value.getValue();
		String[] contents = v.split("\\s");
		
		for(String s : contents) {
			if (s.length() > 0)
				context.write(new TextWritable(s), new IntWritable(1));
		}
	}

	/**
	 * Word count init function. Does nothing as we don't need any initializing
	 */
	@Override
	public void init() {
	}
}
