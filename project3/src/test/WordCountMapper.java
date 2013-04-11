package test;

import interfaces.Mapper;

import lib.Context;
import lib.IntWritable;
import lib.NullWritable;
import lib.TextWritable;


public class WordCountMapper implements
		Mapper<NullWritable, TextWritable, TextWritable, IntWritable> {

	@Override
	public void map(NullWritable key, TextWritable value, Context<TextWritable, IntWritable> context) {

		String v = value.getValue();
		String[] contents = v.split("\\s");
		
		for(String s : contents) {
			if (s.length() > 0)
				context.write(new TextWritable(s), new IntWritable(1));
		}
	}

	@Override
	public void init() {
	}
}
