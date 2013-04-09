package test;

import interfaces.Mapper;

import lib.IntWritable;
import lib.NullWritable;
import lib.TextWritable;

import nodework.Context;

public class WordCountMapper implements
		Mapper<NullWritable, TextWritable, TextWritable, IntWritable> {

	@Override
	public Context<TextWritable, IntWritable> map(NullWritable key, TextWritable value, Context<TextWritable, IntWritable> context) {

		String v = value.getValue();
		String[] contents = v.split("\\s");
		
		for(String s : contents) {
			context.write(new TextWritable(s), new IntWritable(1));
		}
		try {
			new Thread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return context;
	}

	@Override
	public void init() {
	}
}
