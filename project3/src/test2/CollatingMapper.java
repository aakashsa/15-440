package test2;

import interfaces.Mapper;

import lib.Context;
import lib.TextWritable;


public class CollatingMapper implements
		Mapper<TextWritable, TextWritable, TextWritable, TextWritable> {

	@Override
	public Context<TextWritable, TextWritable> map(TextWritable key, TextWritable value, Context<TextWritable, TextWritable> context) {
		context.write(new TextWritable(value.getValue().trim()), new TextWritable(key.getValue().trim()));
		return context;
	}

	@Override
	public void init() {
	}
}
