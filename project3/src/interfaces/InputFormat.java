package interfaces;

import java.util.HashSet;
import java.util.Set;

public abstract class InputFormat<K,V> {
	
	private static Set<String> validFormats = new HashSet<String>();
	
	public static Set<String> validInputFormats() {
		validFormats.add("TEXTINPUTFORMAT");
		validFormats.add("KEYVALUEINPUTFORMAT");
		return validFormats;
	}
	
	public abstract void parse(String str);
	
	public abstract K getKey();
	
	public abstract V getValue();
}
