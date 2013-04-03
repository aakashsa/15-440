package interfaces;

import java.util.HashSet;
import java.util.Set;

public abstract class InputFormat<K extends Writable<?>, V extends Writable<?>> {
	
	private static Set<String> validFormats = new HashSet<String>();
	
	public static Set<String> validInputFormats() {
		validFormats.add("TextInputFormat");
		validFormats.add("KeyValueInputFormat");
		return validFormats;
	}
	
	public abstract void parse(String str);
	
	public abstract K getKey();
	
	public abstract V getValue();
	
	public abstract String getKeyType();
	
	public abstract String getValueType();
}
