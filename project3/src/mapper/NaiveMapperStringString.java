package mapper;

import interfaces.Mapper;

import java.util.StringTokenizer;

public class NaiveMapperStringString implements
		Mapper<String, String, String, Integer> {
	public void map(String key, String value) {
		// System.out.println("Startign to do String ");
		String word;

		// TODO Auto-generated method stub
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word = tokenizer.nextToken();
			// System.out.println("Key = " + word + " Value = " + 1);
		}
	}
}
