package mapper;

import java.util.*;

import lib.IntWritable;
import lib.TextWritable;

import nodework.Context;

import interfaces.Mapper;

public class NaiveMapperIntString implements
		Mapper<IntWritable, TextWritable, IntWritable, TextWritable> {

	@Override
	public Context map(IntWritable key, TextWritable value, Context context) {
		// System.out.println("Startign to do String ");
		String word;

		// TODO Auto-generated method stub
		String line = value.getValue();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			System.out.println("Key = " + word + " Value = " + 1);
		}
		return context;
	}

}
