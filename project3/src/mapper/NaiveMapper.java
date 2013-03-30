package mapper;

import java.util.*;

import interfaces.Mapper;

public class NaiveMapper implements Mapper<Integer, String, String, Integer> {

	public void map(Integer key, String value) {
		System.out.println("Startign to do String ");
		String word;

		// TODO Auto-generated method stub
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			System.out.println("Key = " + word + " Value = " + 1);
		}
	}

}
