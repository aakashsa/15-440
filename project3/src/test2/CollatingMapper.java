package test2;

import interfaces.Mapper;

import lib.Context;
import lib.TextWritable;

/**
 * Collate job mapper
 */
public class CollatingMapper implements
		Mapper<TextWritable, TextWritable, TextWritable, TextWritable> {

	/**
	 * Collate job map function
	 */
	@Override
	public void map(TextWritable key, TextWritable value, Context<TextWritable, TextWritable> context) {
		context.write(new TextWritable(value.getValue().trim()), new TextWritable(key.getValue().trim()));
	}

	/**
	 * Collate job init function. Need no setup before doing map task.
	 */
	@Override
	public void init() {
	}
}
