package mapper;

import interfaces.Mapper;

import java.util.StringTokenizer;

import nodework.Context;

public class NaiveMapperStringString implements
		Mapper<String, String, String, Integer> {
	public void map(String key, String value) {
		 System.out.println("Key = " + key + " Value = " + value);
//	
		// System.out.println("Startign to do String ");
//		String word;
//
//		// TODO Auto-generated method stub
//		String line = value.toString();
//		StringTokenizer tokenizer = new StringTokenizer(line);
//		while (tokenizer.hasMoreTokens()) {
//			word = tokenizer.nextToken();
//			 System.out.println("Key = " + word + " Value = " + 1);
//		}
	}

	@Override
	public Context map(String key, String value, Context context) {
		 System.out.println("Key = " + key + " Value = " + value);

		// TODO Auto-generated method stub
		return null;
	}
}
