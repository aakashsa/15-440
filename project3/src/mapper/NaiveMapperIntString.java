package mapper;

import java.util.*;

import lib.IntWritable;
import lib.TextWritable;

import nodework.Context;

import interfaces.Mapper;

public class NaiveMapperIntString implements
		Mapper<IntWritable, TextWritable, IntWritable, TextWritable> {

	@Override
	public Context<IntWritable, TextWritable> map(IntWritable key, TextWritable value, Context<IntWritable, TextWritable> context) {
		String word;

		String line = value.getValue();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			System.out.println("Key = " + word + " Value = " + 1);
			context.write(key, value);
		}
		return context;
	}

}
