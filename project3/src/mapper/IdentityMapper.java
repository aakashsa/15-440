package mapper;

import nodework.Context;
import interfaces.Mapper;
import lib.TextWritable;

public class IdentityMapper implements
	Mapper<TextWritable, TextWritable, TextWritable, TextWritable> {

	@Override
	public Context<TextWritable, TextWritable> map(TextWritable key,
			TextWritable value, Context<TextWritable, TextWritable> context) {
		context.write(key, value);
		return context;
	}

	@Override
	public void init() {
	}	
}