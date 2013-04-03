package mapper;

import interfaces.Mapper;

import lib.IntWritable;
import lib.NullWritable;
import lib.TextWritable;

import nodework.Context;

public class NaiveMapperStringString implements
		Mapper<NullWritable, TextWritable, TextWritable, IntWritable> {

	@Override
	public Context<TextWritable, IntWritable> map(NullWritable key, TextWritable value, Context<TextWritable, IntWritable> context) {
		return null;
	}
}
